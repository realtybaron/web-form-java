package com.socotech.wf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractWizardFormAction.java
 * <p/>
 * Enables a linear, wizard-like page flow.
 * <p/>
 *
 * @see UserScopeVariable#choice used to capture choice of direction
 * @see UserScopeVariable#back used to move backward in the wizard
 * @see UserScopeVariable#next used to move forward in the wizard
 * @see UserScopeVariable#finish used to complete the wizard
 */
public abstract class AbstractWizardFormAction extends AbstractFormAction {

    /**
     * If form is session scoped, but not found in the user's session, redirect to the beginning of the wizard.
     *
     * @param request  wf4j request
     * @param response wf4j response
     * @return true, if user should be redirected to beginning of wizard
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected boolean redirectRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Form form = this.getClass().getAnnotation(Form.class);
            String sessionName = this.getSessionAttributeName(form);
            if (form.sessionForm() && WebUtil.getSessionAttribute(request, sessionName) == null) {
                // force creation of a new session form object to avoid infinite redirects
                this.getFormObject(request);
                // redirect to first page of wizard
                super.redirectTo(request, response, new RequestBuilder(request).toString());
                // indicate redirect in progress
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
        return false;
    }

    /**
     * Handle a form submission from user.  At this point, the form object has been bound from request parameters and validated.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param o        form object
     * @param errors   error object
     * @throws IOException      i/o exception
     * @throws ServletException servlet problems?
     */
    @Override
    protected final void handleFormSubmission(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws IOException, ServletException {
        try {
            // find current page
            int page = Requests.getIntParameter(request, SharedScopeVariable.page.name(), 0);
            // back, forward, existing, or finishing?
            if (this.isExitSubmission(request)) {
                // exit from wizard
                this.exit(o, request, response);
            } else if (this.isJumpSubmission(request)) {
                // show form of target page
                this.showPage(request, response, o, errors, page);
            } else if (this.isBackSubmission(request)) {
                // show form of previous page
                this.showPage(request, response, o, errors, --page);
            } else if (this.isCancelSubmission(request)) {
                // cancel out of wizard
                this.cancel(o, request, response);
            } else if (this.isNextSubmission(request)) {
                // no errors, process current page
                this.executePage(request, response, o, page);
                // show form of next page
                this.showPage(request, response, o, errors, ++page);
            } else if (this.isFinishSubmission(request)) {
                // fully validate the form one last time
                this.validateFormObject(request, o, errors);
                if (errors.isEmpty()) {
                    // finish up!
                    this.finish(o, request, response);
                    // expel form from session
                    Form form = this.getClass().getAnnotation(Form.class);
                    if (form.sessionForm()) {
                        WebUtil.removeSessionAttribute(request, super.getSessionAttributeName(form));
                    }
                } else {
                    // return to the initial page of wizard and report error
                    this.forwardToWithErrors(request, response, this.getView(request, 0), errors);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    /**
     * If the request method is "POST", a form submission is assumed.  Sub-classes can override this to use request params.
     *
     * @param request HTTP request
     * @return true, if form submission is detected
     */
    @Override
    protected boolean isFormSubmission(HttpServletRequest request) {
        return Requests.getStringParameter(request, UserScopeVariable.choice.name(), null) != null;
    }

    /**
     * Skip binding if moving backwards in the page flow
     *
     * @param request HTTP request
     * @param o       form object
     * @param errors  error packet
     * @return true, if moving backwards
     */
    @Override
    protected boolean suppressBinding(HttpServletRequest request, Object o, FormErrors errors) {
        Form form = this.getClass().getAnnotation(Form.class);
        return form.sessionForm() && this.isBackSubmission(request);
    }

    /**
     * Skip validation if moving backwards in the page flow
     *
     * @param request HTTP request
     * @param o       form object
     * @param errors  error packet
     * @return true, if moving backwards
     */
    @Override
    protected boolean suppressValidation(HttpServletRequest request, Object o, FormErrors errors) {
        return this.isBackSubmission(request);
    }

    /**
     * A call back after request params have been bound to the form object.
     *
     * @param req      HTTP request
     * @param response HTTP response
     * @param o        form object
     * @param errors   error packet
     * @throws Exception bad things
     */
    @Override
    protected final boolean onBind(HttpServletRequest req, HttpServletResponse response, Object o, FormErrors errors) throws Exception {
        int pageNumber = Requests.getIntParameter(req, SharedScopeVariable.page.name(), -1);
        this.onBind(req, o, errors, pageNumber);
        return false;
    }

    /**
     * After binding, validate the form object
     *
     * @param request HTTP request
     * @param command form object
     * @param o       error packet
     * @throws InstantiationException if the validator instance cannot be instantiated
     * @throws IllegalAccessException if something else bad happens
     */
    @Override
    protected final void validateFormObject(HttpServletRequest request, Object command, FormErrors o) throws Exception {
        int pageNumber = Requests.getIntParameter(request, SharedScopeVariable.page.name(), 0);
        this.validatePage(request, command, o, pageNumber);
    }

    /**
     * Return user to the form view.  Uses the page number in the request scope to determine the page to which user is forwarded.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param o        form object
     * @param errors   error packet
     * @throws Exception bad stuff
     */
    @Override
    protected final void showForm(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws Exception {
        int page = Requests.getIntParameter(request, SharedScopeVariable.page.name(), 0);
        this.showPage(request, response, o, errors, page);
    }

    /**
     * Show the specified page if the response has not already be committed.  But first, add the form object to the request scope.  Plus, prepare any reference data for form view.
     * And finally, included any "off page" form values as hidden parameters in the target page.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param o        wf4j form
     * @param errors   error packet
     * @param page     target page
     * @throws Exception bad things, man....bad things
     */
    protected void showPage(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors, int page) throws Exception {
        if (!response.isCommitted()) {
            Form form = this.getClass().getAnnotation(Form.class);
            // add form object to request scope
            request.setAttribute(form.name(), o);
            // prepare reference data for form view
            Map<String, Object> data = this.getAuxiliaryData(request, o, page);
            for (String name : data.keySet()) {
                Object value = data.get(name);
                request.setAttribute(name, value);
            }
            // Figure out where we're supposed to go to next
            String view = this.getView(request, page);
            if (view.startsWith("redirect:")) {
                // save errors in session
                request.getSession(true).setAttribute("errors", errors);
                // build redirect url
                RequestBuilder rb = new RequestBuilder(StringUtils.substringAfter(view, "redirect:"));
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    rb.add(entry.getKey(), entry.getValue().toString());
                }
                // redirect to form view with params
                this.redirectTo(request, response, rb.toString());
            } else {
                // hide form object property values in page
                Set<Pair> formValues = this.getOffPageParameters(request, o, page);
                request.setAttribute(FORM_OBJECT_PARAMS, formValues);
                // forward to form view with or without errors
                if (errors.isEmpty()) {
                    this.forwardTo(request, response, this.getView(request, page));
                } else {
                    this.forwardToWithErrors(request, response, getView(request, page), errors);
                }
            }
        }
    }


    /**
     * Determines if user should be required to sign in before submitting this page's form.
     *
     * @param request HTTP request
     * @param page    current page
     * @return true, if user is required to be signed-in
     */
    protected abstract boolean mustBeSignedIn(HttpServletRequest request, int page);

    /**
     * Load supporting data into request scope before directing user to form view.  This method must be overridden by subclasses.  By default, this method returns an empty map.
     *
     * @param request HTTP request
     * @param o       form object
     * @param page    current page
     * @return map of reference data
     * @throws Exception if something bad happens
     */
    protected Map<String, Object> getAuxiliaryData(HttpServletRequest request, Object o, int page) throws Exception {
        return Collections.emptyMap();
    }

    /**
     * Extract form object properties that are "off page" for use as hidden parameters in the target page. "Off page" means they are visible to the user on either previous or
     * subsequent pages, but not on the target page.
     *
     * @param req  wf4j request
     * @param o    form object
     * @param page current page
     * @return set of request params to be hidden inside page
     * @throws Exception failure of any variety
     */
    protected Set<Pair> getOffPageParameters(HttpServletRequest req, Object o, int page) throws Exception {
        return Collections.emptySet();
    }

    /**
     * A call back after request params have been bound to the form object
     *
     * @param request HTTP request
     * @param o       form object
     * @param errors  error packet
     * @param page    the current page
     * @throws Exception bad things
     */
    protected void onBind(HttpServletRequest request, Object o, FormErrors errors, int page) throws Exception {
        // noop
    }

    /**
     * Validate a form submission from a specific page within the wizard
     *
     * @param request HTTP request
     * @param command form object
     * @param errors  error packet
     * @param page    the current page
     * @throws Exception bad things
     */
    protected abstract void validatePage(HttpServletRequest request, Object command, FormErrors errors, int page) throws Exception;

    /**
     * Handle a form submission from a specific page within the wizard.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param o        form object
     * @param page     the current page
     * @throws IOException      i/o exception
     * @throws ServletException servlet problems?
     */
    protected abstract void executePage(HttpServletRequest request, HttpServletResponse response, Object o, int page) throws IOException, ServletException;

    /**
     * Abort the wizard <i>by backing out</i> of the page flow <i>without</i> completing it.  User is likely to return the page from which the user first entered the wizard.
     *
     * @param o   form object
     * @param req HTTP request
     * @param res HTTP response
     * @throws IOException      i/o exception
     * @throws ServletException servlet problems?
     */
    protected abstract void exit(Object o, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException;

    /**
     * Abort the wizard <i>in the middle</i> of page flow <i>without</i> completing it.
     *
     * @param o   form object
     * @param req HTTP request
     * @param res HTTP response
     * @throws IOException      i/o exception
     * @throws ServletException servlet problems?
     */
    protected abstract void cancel(Object o, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException;

    /**
     * Finish processing wizard.  User should not be able to return to any pages after this method completes.
     *
     * @param o        form object
     * @param request  HTTP request
     * @param response HTTP response
     * @throws IOException      i/o exception
     * @throws ServletException servlet problems?
     */
    protected abstract void finish(Object o, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

    /**
     * Return the document location of the specified page
     *
     * @param request wf4j request
     * @param page    page index
     * @return document location
     */
    protected abstract String getView(HttpServletRequest request, int page);

    /**
     * Determines whether use is moving backward in the wizard
     *
     * @param request incoming HTTP request
     * @return true, if moving backward
     */
    protected boolean isBackSubmission(HttpServletRequest request) {
        String choice = Requests.getStringParameter(request, UserScopeVariable.choice.name(), "");
        return StringUtils.equals(choice, UserScopeVariable.back.name());
    }

    /**
     * Determines whether use is moving backward in the wizard
     *
     * @param request incoming HTTP request
     * @return true, if moving backward
     */
    protected boolean isCancelSubmission(HttpServletRequest request) {
        String choice = Requests.getStringParameter(request, UserScopeVariable.choice.name(), "");
        return StringUtils.equals(choice, UserScopeVariable.cancel.name());
    }

    /**
     * Determines whether use is moving forward in the wizard
     *
     * @param request incoming HTTP request
     * @return true, if moving foward
     */
    protected boolean isNextSubmission(HttpServletRequest request) {
        String choice = Requests.getStringParameter(request, UserScopeVariable.choice.name(), "");
        return StringUtils.equals(choice, UserScopeVariable.next.name());
    }

    /**
     * Determines whether user is jumping to a specific page in the wizard
     *
     * @param request incoming HTTP request
     * @return true, if jumping pages
     */
    protected boolean isJumpSubmission(HttpServletRequest request) {
        String choice = Requests.getStringParameter(request, UserScopeVariable.choice.name(), "");
        return StringUtils.equals(choice, UserScopeVariable.jump.name());
    }

    /**
     * Determines whether user is leaving the wizard without finishing it.
     *
     * @param request incoming HTTP request
     * @return true, if leaving wizard without finishing it
     */
    protected boolean isExitSubmission(HttpServletRequest request) {
        int pageNumber = Requests.getIntParameter(request, SharedScopeVariable.page.name(), 0);
        return pageNumber == 0 && this.isBackSubmission(request);
    }

    /**
     * Determines whether user is moving forward in the wizard
     *
     * @param request incoming HTTP request
     * @return true, if moving foward
     */
    protected boolean isFinishSubmission(HttpServletRequest request) {
        String choice = Requests.getStringParameter(request, UserScopeVariable.choice.name(), "");
        return StringUtils.equals(choice, UserScopeVariable.finish.name());
    }

    public static final String FORM_OBJECT_PARAMS = "_form_object_params";

    private static final Logger log = LoggerFactory.getLogger(AbstractWizardFormAction.class);
}
