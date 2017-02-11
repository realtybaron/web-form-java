/**
 * AbstractSimpleFormAction.java 
 */

package com.socotech.wf4j;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

public abstract class AbstractSimpleFormAction extends AbstractFormAction {

  /**
   * Return user to the form view. Reference data is added to request scope. Additionally, the form object is added to
   * request scope under the form's name.
   *
   * @param request  HTTP request
   * @param response HTTP response
   * @param o        form object
   * @param errors   error packet
   * @throws Exception bad stuff
   */
  @Override
  protected void showForm(HttpServletRequest request, HttpServletResponse response, Object o, ErrorPacket errors) throws Exception {
    Form form = this.getClass().getAnnotation(Form.class);
    // add form object to request scope
    request.setAttribute(form.name(), o);
    // prepare reference data for form view
    Map<String, Object> data = this.getAuxiliaryData(request, o);
    for (String name : data.keySet()) {
      Object value = data.get(name);
      request.setAttribute(name, value);
    }
    // errors in the session? we must have been redirecting user
    if (errors.isEmpty()) {
      ErrorPacket errors2 = WebUtil.getAndRemoveSessionAttribute(request, "errors");
      if (errors2 != null) {
        errors.putAll(errors2);
      }
    }
    // Tokenize form?
    if (this.isTokenizedForm(request)) {
      this.saveToken(request, o);
    }
    // Figure out where we're supposed to go to next
    String view = this.getFormView(request, o);
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
      // messages in the session? move from session to request scope.
      WebUtil.dumpMessagesToRequest(request);
      // no errors? render view
      if (errors.isEmpty()) {
        this.render(request, response, view);
      } else { // otherwise, render view with errors
        this.renderWithErrors(request, response, view, errors);
      }
    }
  }

  /**
   * Handle a form submission from user. The form object is automagically marshalled from either the session or
   * request parameters. After submission handling, the form object is added to request scope under the form's name.
   *
   * @param request  HTTP request
   * @param response HTTP response
   * @param o        form object
   * @param errors   error object
   */
  @Override
  protected void handleFormSubmission(HttpServletRequest request, HttpServletResponse response, Object o, ErrorPacket errors) throws IOException, ServletException {
    try {
      if (!this.isTokenizedForm(request) || this.isTokenValid(request, o)) {
        // handle valid submission
        this.doSubmit(request, response, o, errors);
        // check for form submission errors
        if (errors.isEmpty()) {
          // add form object to request scope making it available in success view
          Form form = this.getClass().getAnnotation(Form.class);
          request.setAttribute(form.name(), o);
          // Prepare for view
          this.prepareSuccessView(request, o);
          // Figure out where we're supposed to go to next
          String view = this.getSuccessView(request, o);
          // go to the success view
          this.showView(request, response, view);
          // reset token only when we're assured a successful submission
          this.resetToken(request, o);
        } else {
          // return to the form and report error
          this.showForm(request, response, o, errors);
        }
      } else {
        errors.put("error", "A duplicate form submission was detected");
        this.showForm(request, response, o, errors);
      }
    } catch (Exception e) {
      this.raiseServerError(request, response, "Error in abstract simple form submission", e);
    }
  }

  /**
   * Determine if we should guard against duplicate form submission
   *
   * @param request HTTP request
   * @return true, if form will be tokenized
   */
  protected boolean isTokenizedForm(HttpServletRequest request) {
    return false;
  }

  /**
   * Generate a new, random token. Store it in the form and in the user's session
   *
   * @param request HTTP request
   * @param o       form object
   */
  private void saveToken(HttpServletRequest request, Object o) {
    Validate.isTrue(TokenizedForm.class.isAssignableFrom(o.getClass()), "Form is not tokenized");
    TokenizedForm form = TokenizedForm.class.cast(o);
    form.setToken(RandomStringUtils.randomAlphanumeric(8));
    WebUtil.setSessionAttribute(request, this.getTokenName(o), form.getToken());
  }

  /**
   * Compare a token from the form to a token in the user's session
   *
   * @param request HTTP request
   * @param o       form object
   * @return true, if the form token matches the session token
   */
  private boolean isTokenValid(HttpServletRequest request, Object o) {
    Validate.isTrue(TokenizedForm.class.isAssignableFrom(o.getClass()), "Form is not tokenized");
    TokenizedForm form = TokenizedForm.class.cast(o);
    return WebUtil.getOrCreateSessionAttribute(request, this.getTokenName(o), "").equals(form.getToken());
  }

  /**
   * Remove a user's session token.
   *
   * @param request HTTP request
   * @param o       form object
   */
  private void resetToken(HttpServletRequest request, Object o) {
    WebUtil.removeSessionAttribute(request, this.getTokenName(o));
  }

  /**
   * Generate a name for a token
   *
   * @param o form object
   * @return token name
   */
  private String getTokenName(Object o) {
    return o.getClass().getName() + ".TOKEN";
  }

  /**
   * Form has been processed. Now, show the success view for the action.
   *
   * @param req  wf4j request
   * @param res  wf4j response
   * @param view success view
   * @throws IOException      general I/O problems
   * @throws ServletException general HTTP problems
   */
  private void showView(HttpServletRequest req, HttpServletResponse res, String view) throws IOException, ServletException {
    if (view.startsWith("redirect:")) {
      // redirect to action; pull apart any params
      view = StringUtils.substringAfter(view, "redirect:");
      this.redirectTo(req, res, view, req.getMethod().equalsIgnoreCase("post") ? HttpServletResponse.SC_SEE_OTHER : HttpServletResponse.SC_MOVED_TEMPORARILY);
    } else {
      String path = StringUtils.substringBefore(view, "?");
      if (path.endsWith(".jsp")) {
        // move from session to request scope.
        WebUtil.dumpMessagesToRequest(req);
        // render view
        this.render(req, res, view);
      } else if (path.endsWith(".jspf")) {
        // render view
        this.render(req, res, view);
      } else if (path.endsWith(".act")) {
        // redirect to action
        this.redirectTo(req, res, view, req.getMethod().equalsIgnoreCase("post") ? HttpServletResponse.SC_SEE_OTHER : HttpServletResponse.SC_MOVED_TEMPORARILY);
      } else {
        throw new IllegalStateException(this.name + " failed to redirect to view [" + view + "]");
      }
    }
  }

  /**
   * Handle a simple form submission
   *
   * @param request  wf4j request
   * @param response wf4j response
   * @param o        form object
   * @param errors   error packet
   * @throws Exception bad things
   */
  protected abstract void doSubmit(HttpServletRequest request, HttpServletResponse response, Object o, ErrorPacket errors) throws Exception;

  /**
   * Get the URL location of the page on which the form is displayed.
   *
   * @param req HTTP request
   * @param o   form object
   * @return URL location
   */
  protected abstract String getFormView(HttpServletRequest req, Object o);

  /**
   * Get the URL location of the page to which a user is redirected after a successful form submission
   *
   * @param req HTTP request
   * @param o   form object
   * @return URL location
   * @throws Exception if a success view cannot be constructed, i.e. dynamic URL
   */
  protected abstract String getSuccessView(HttpServletRequest req, Object o) throws Exception;

  /**
   * Optionally prepare the success view, typically by adding new parameters to the request
   *
   * @param req the HTTP servlet request
   * @param o   the form object
   */
  protected void prepareSuccessView(HttpServletRequest req, Object o) {
    // This method intentionally left blank
  }

  /**
   * <p/> A logging category for each action. </p>
   */
  private static Logger log = Logger.getLogger(AbstractSimpleFormAction.class);
}