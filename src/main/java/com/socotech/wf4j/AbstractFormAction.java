package com.socotech.wf4j;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractFormAction
 */
public abstract class AbstractFormAction extends AbstractAction {
    /**
     * A uniform method for constructing the name used to persist form in session scope
     *
     * @param form form object
     * @return name
     */
    protected String getSessionAttributeName(Form form) {
        return form.formClass().getName() + "." + form.name().toUpperCase();
    }

    /**
     * Load supporting data into request scope before directing user to form view.  This method must be overriden by subclasses.  By default, this method returns an empty map.
     *
     * @param request HTTP request
     * @param o       form object
     * @return map of data
     * @throws Exception if something bad happens
     */
    protected Map<String, Object> getAuxiliaryData(HttpServletRequest request, Object o) throws Exception {
        return Maps.newHashMap();
    }

    /**
     * Constructs a form object and, if specified, stores it in the session for later use
     *
     * @param req web request
     * @return form object
     * @throws Exception if object cannot be instantiated
     */
    protected Object getFormObject(HttpServletRequest req) throws Exception {
        Form form = this.getClass().getAnnotation(Form.class);
        Object command, newbie = form.formClass().newInstance();
        // as default, assign new instance to command
        command = newbie;
        // try to load an existing object from the session (optional)
        if (form.sessionForm()) {
            String sessionName = this.getSessionAttributeName(form);
            command = WebUtil.getOrCreateSessionAttribute(req, sessionName, newbie);
        }
        // If command & newbie reference the same object, either we're not in session form mode or
        // there was no existing form found in the session.  Either way, form object is a newbie
        // and, if bind on new form is 'true', bind request parameters to new form object now.
        if (command == newbie && this.bindOnNewForm(req)) {
            FormErrors errors = new FormErrors();
            // copy bean properties from request params
            this.bindFormObject(req, command, errors);
            // log binding errors as warnings
            for (String code : errors.getCodes()) {
                log.warn("Error while binding on new form: " + errors.get(code));
            }
            // allow for post-binding of new for
            this.onBindOnNewForm(req, command, errors);
        }
        return command;
    }

    /**
     * If the request method is "POST", a form submission is assumed.  Sub-classes can override this to use request params.
     *
     * @param request HTTP request
     * @param o
     * @return true, if form submission is detected
     */
    protected boolean isFormSubmission(HttpServletRequest request, Object o) {
        return "POST".equalsIgnoreCase(request.getMethod());
    }

    /**
     * Use request parameters to populate the form object.  Any form binders present in the form configuration are applied.
     * <p/>
     * Enum types are automagically detected and translated from String values to their respective enum type.
     *
     * @param request incoming HTTP request
     * @param o       form object
     * @param errors  error packet
     * @throws Exception if unhandled exceptions occur while binding
     */
    @SuppressWarnings("unchecked")
    protected void bindFormObject(HttpServletRequest request, Object o, FormErrors errors) throws Exception {
        // assemble parameter map
        Map<String, String[]> parameters = Maps.newHashMap();
        if (!ServletFileUpload.isMultipartContent(request)) {
            // process as standard request
            Enumeration names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                String[] values = request.getParameterValues(name);
                parameters.put(name, values);
            }
        } else {
            // process as multi-part request
            ServletFileUpload fu = new ServletFileUpload(this.getFileItemFactory());
            try {
                List<FileItem> fitems = fu.parseRequest(request);
                for (FileItem fitem : fitems) {
                    String name = fitem.getFieldName();
                    if (fitem.isFormField()) {
                        String value = fitem.getString();
                        String[] values = parameters.get(name);
                        if (values == null) {
                            // Not in parameter map yet, so add as new value.
                            parameters.put(name, new String[]{value});
                        } else {
                            // Multiple field values, so add new value to existing array.
                            int length = values.length;
                            String[] newValues = new String[length + 1];
                            System.arraycopy(values, 0, newValues, 0, length);
                            newValues[length] = value;
                            parameters.put(name, newValues);
                        }
                    } else {
                        // bind uploaded files to form object now instead of below
                        try {
                            byte[] bytes = IOUtils.toByteArray(fitem.getInputStream());
                            BeanUtils.setProperty(o, name, bytes);
                        } catch (Exception e) {
                            if (!errors.isSet(name)) {  // don't overwrite existing property error
                                errors.put(name, ExceptionUtils.getRootCauseMessage(e));
                            }
                        }
                    }
                }
            } catch (FileUploadException e) {
                log.warn(e.getMessage(), e);
            }
        }
        // copy param keys to list
        List<String> paramKeys = Lists.newArrayList(parameters.keySet());
        // sort by name so we bind in ancestral order
        Collections.sort(paramKeys);
        // bind simple form fields to form object
        for (String fieldName : paramKeys) {
            String fieldPath = fieldName.replaceAll(Patterns.INDEX_REFERENCE.pattern(), "");
            String[] valueArray = parameters.get(fieldName);
            Field field = Reflect.getDeclaredField(o, fieldName);
            if (field != null) {
                try {
                    Class<?> type = field.getType();
                    // resolve binders based on type and path
                    Set<FormBinder> binders = this.getBinders(fieldPath);
                    // re-package array as collection
                    Set<String> values = Sets.newHashSet(valueArray);
                    // determine if field is an array
                    if (type.isArray()) {
                        // get the underlying class of this array
                        type = type.getComponentType();
                        // determine if field is enum
                        if (type.isEnum()) {
                            // array of enums; try to resolve value if not blank
                            Set<Enum> enums = Sets.newHashSet();
                            for (String value : values) {
                                if (StringUtils.isNotBlank(value)) {
                                    Class<Enum> ce = (Class<Enum>) type;
                                    Enum e = Enum.valueOf(ce, value);
                                    enums.add(e);
                                }
                            }
                            Object[] array = (Object[]) Array.newInstance(type, enums.size());
                            Object object = enums.toArray(array);
                            BeanUtils.setProperty(o, fieldName, object);
                        } else {
                            // array of primitive or object types
                            Set<Object> objects = Sets.newHashSet();
                            for (String value : values) {
                                Object obj = value;
                                // try to match using equality; otherwise, try to match using a regular expression
                                for (FormBinder binder : binders) {
                                    PropertyEditor pe = this.newPropertyEditor(request, binder);
                                    pe.setAsText(value);
                                    obj = pe.getValue();
                                }
                                if (obj.getClass().isArray()) {
                                    // value is delimited array of elements
                                    Object[] array = (Object[]) obj;
                                    objects.addAll(Arrays.asList(array));
                                } else {
                                    // value is a primitive or object
                                    objects.add(obj);
                                }
                            }
                            Object[] array = (Object[]) Array.newInstance(type, objects.size());
                            Object object = objects.toArray(array);
                            BeanUtils.setProperty(o, fieldName, object);
                        }
                    } else {
                        String value = values.iterator().next();
                        if (!binders.isEmpty()) {
                            // use form binders to convert...
                            Object object = value;
                            for (FormBinder binder : binders) {
                                PropertyEditor pe = this.newPropertyEditor(request, binder);
                                pe.setAsText(value);
                                object = pe.getValue();
                            }
                            // set property with no type conversions
                            PropertyUtils.setProperty(o, fieldName, object);
                        } else if (StringUtils.isNotBlank(value)) {
                            if (type.isEnum()) {
                                Class<Enum> e = (Class<Enum>) type;
                                Object object = Enum.valueOf(e, value);
                                // set property with no type conversions
                                PropertyUtils.setProperty(o, fieldName, object);
                            } else {
                                // set property using type conversions if necessary
                                BeanUtils.setProperty(o, fieldName, value);
                            }
                        } else if (StringUtils.isEmpty(value)) {
                            // per #4408, set value to null
                            if (type.isPrimitive()) {
                                // set property using type conversions if necessary
                                BeanUtils.setProperty(o, fieldName, null);
                            } else {
                                // set property with no type conversions
                                PropertyUtils.setProperty(o, fieldName, null);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (e instanceof IllegalArgumentException) {
                        // This is a simple IAE: no point in writing out the stack
                        log.warn(e.getMessage());
                    } else {
                        log.warn(e.getMessage(), e);
                    }
                    if (!errors.isSet(fieldName)) {  // don't overwrite existing property error
                        errors.put(fieldName, e.getMessage());
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Writable property \"" + fieldName + "\" not found on form object");
                }
            }
        }
    }

    /**
     * Sub-classes can override to avoid writing to disk, i.e. Google Appengine
     *
     * @return file item factory
     */
    protected FileItemFactory getFileItemFactory() {
        return new DiskFileItemFactory();
    }

    /**
     * Extract binders for a specific form property
     *
     * @param path path to property
     * @return set of binders
     */
    @SuppressWarnings("unchecked")
    private Set<FormBinder> getBinders(String path) {
        Set<FormBinder> set = Sets.newHashSet();
        FormBinder[] binders = this.getClass().getAnnotation(Form.class).binders();
        for (FormBinder binder : binders) {
            if (binder.property().equals(path) || Pattern.compile(binder.property()).matcher(path).matches()) {
                set.add(binder);
            }
        }
        return set;
    }

    /**
     * Does user meet privilege requirements?
     *
     * @param request  web request
     * @param response web response
     * @param o        web form
     * @return true, if user meets privilege requirements
     */
    public boolean meetsPrivilegeRequirements(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        return true;
    }

    /**
     * Create a new validator. By default, it simply creates a new instance of the class.
     * <p>
     * Sub-classes can override this method to do something fancier, i.e. instantiate via DI/IoC
     *
     * @param request web request
     * @param form    form spec
     * @return new form validator instance
     */
    protected FormValidator newFormValidator(HttpServletRequest request, Form form) throws Exception {
        return (FormValidator) form.validatorClass().newInstance();
    }

    /**
     * Given a form binder, resolve the proper property editor.  Sub-classes can override to add specialized handling, i.e. currency editor.
     *
     * @param request web request
     * @param binder  form binder
     * @return new instance of editor class
     * @throws Exception if editor class cannot be instantiated
     */
    protected PropertyEditor newPropertyEditor(HttpServletRequest request, FormBinder binder) throws Exception {
        return (PropertyEditor) binder.editorClass().newInstance();
    }

    /**
     * Determine whether binding should be skipped.  Default is false.  Sub-classes should override to customize behavior.
     *
     * @param request HTTP request
     * @param o       form object
     * @param errors  error packet
     * @return true, if binding should be skipped
     */

    protected boolean suppressBinding(HttpServletRequest request, Object o, FormErrors errors) {
        return false;
    }

    /**
     * Determine whether validation should be skipped.  Default is false.  Sub-classes should override to customize behavior.
     *
     * @param request HTTP request
     * @param o       form object
     * @param errors  error packet
     * @return true, if validation should be skipped
     */
    protected boolean suppressValidation(HttpServletRequest request, Object o, FormErrors errors) {
        return false;
    }

    /**
     * Determine if we should bind request parameters to a newly instantiated form object
     *
     * @param req web request
     * @return true, if we should bind to new form object
     */
    protected boolean bindOnNewForm(HttpServletRequest req) {
        return false;
    }

    /**
     * Determine if we should handle binding and validation errors instead of returning to form view
     *
     * @param request web request
     * @param o       form object
     * @param errors  error packet
     * @return true, if errors should be handled and avoid returning to form view
     */
    protected boolean handleBindingAndValidationErrors(HttpServletRequest request, Object o, FormErrors errors) {
        return false;
    }

    /**
     * Handle a form submission which produced errors.  The form object is automagically marshaled from either the session or request parameters.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param o        form object
     * @param errors   error packet
     * @throws IOException      i/o exception
     * @throws ServletException servlet problems?
     */
    protected void onBindingAndValidationErrors(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws Exception {
        // noop
    }

    /**
     * After binding, validate the form object
     *
     * @param request HTTP request
     * @param o       form object
     * @param errors  error packet
     * @throws Exception if the form type or validator instance cannot be instantiated
     */
    protected void validateFormObject(HttpServletRequest request, Object o, FormErrors errors) throws Exception {
        Form form = this.getClass().getAnnotation(Form.class);
        if (!form.validatorClass().equals(void.class)) {
            this.newFormValidator(request, form).validate(o, errors);
        }
    }

    /**
     * Handle the default execution method and forward with command object
     *
     * @param req incoming HTTP request
     * @param res incoming HTTP response
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public final void execute(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        if (!this.redirectRequest(req, res)) {
            FormErrors errors = new FormErrors();
            try {
                Form form = this.getClass().getAnnotation(Form.class);
                // get or create a new form
                Object o = this.getFormObject(req);
                // bind parameters to the form (optional)
                if (!this.suppressBinding(req, o, errors)) {
                    // copy bean properties from request params
                    this.bindFormObject(req, o, errors);
                    // Allow actions to do their own work prior to form processing
                    if (this.onBind(req, res, o, errors)) {
                        return;
                    }
                    // Check privileges
                    if (!this.meetsPrivilegeRequirements(req, res, o)) {
                        this.handleUnauthorized(req, res);
                    }
                }
                if (this.isFormSubmission(req, o)) {
                    // validate the form (optional)
                    if (!this.suppressValidation(req, o, errors)) {
                        this.validateFormObject(req, o, errors);
                    }
                    // allow for post-binding and post-validation processing
                    this.onBindAndValidate(req, o, errors);
                    // still no errors?
                    if (errors.isEmpty()) {
                        // no errors, continue to execute
                        this.handleFormSubmission(req, res, o, errors);
                        // clean up after successful form submission
                        if (!form.sessionForm()) {
                            WebUtil.removeSessionAttribute(req, getSessionAttributeName(form));
                        }
                        return; // successful form submission...exit now!
                    } else if (this.handleBindingAndValidationErrors(req, o, errors)) {
                        this.onBindingAndValidationErrors(req, res, o, errors);
                        return; // errors handled...exit now!
                    }
                }
                // either not a form submission or unhandled errors produced from binding and validation.  In any event, show the form.
                this.showForm(req, res, o, errors);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                this.raiseServerError(req, res, e.getMessage());
            }
        }
    }

    /**
     * Called early in the execute process.
     *
     * @param request  web request
     * @param response web response
     * @return true, if you are redirecting the request outside of the process.
     * @throws ServletException
     * @throws IOException
     */
    protected boolean redirectRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return false;
    }

    /**
     * Called only after a new form is instantiated and request parameters are bound to it.
     *
     * @param request web request
     * @param o       form object
     * @param errors  error packet
     * @throws Exception bad stuff
     */
    protected void onBindOnNewForm(HttpServletRequest request, Object o, FormErrors errors) throws Exception {
        // noop
    }

    /**
     * Called after form is bound but before it is processed and displayed. return true if you want to bypass subsequent processing
     *
     * @param request  web request
     * @param response web response
     * @param o        form object
     * @param errors   error packet
     * @return true, if further processing should be halted
     * @throws Exception any show stoppers
     * @see #onBindAndValidate for preferred usage
     */
    protected boolean onBind(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws Exception {
        return false;
    }

    /**
     * Called after form is bound to request parameters and validated.
     *
     * @param request web request
     * @param o       form object
     * @param errors  error packet
     * @throws Exception bad stuff
     */
    protected void onBindAndValidate(HttpServletRequest request, Object o, FormErrors errors) throws Exception {
        // noop
    }

    /**
     * Return user to the form view
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param o        form object
     * @param errors   error packet
     * @throws Exception bad stuff
     */
    protected abstract void showForm(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws Exception;

    /**
     * Handle a form submission from user.  The form object is automagically marshaled from either the session or request parameters.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param o        form object
     * @param errors   error packet
     * @throws IOException                    i/o exception
     * @throws javax.servlet.ServletException servlet problems?
     */
    protected abstract void handleFormSubmission(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws IOException, ServletException;

    /**
     * <p/> A logging category for each action. </p>
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractFormAction.class);
}
