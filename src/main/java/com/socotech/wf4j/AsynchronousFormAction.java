package com.socotech.wf4j;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: marc Date: Jun 6, 2010 Time: 1:16:02 PM
 */
public abstract class AsynchronousFormAction extends AbstractFormAction {

    /**
     * Invoked as alternative to redirecting user to a sign in page
     *
     * @param request  wf4j request
     * @param response wf4j response
     */
    @Override
    public void handleUnauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        try {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            writer.write("You are unauthorized to make that request.");
        } finally {
            writer.flush();
            writer.close();
        }
    }

    /**
     * Returns 'true' because there is no form or success view
     *
     * @param request HTTP request
     * @return true
     */
    @Override
    protected boolean isFormSubmission(HttpServletRequest request) {
        return true;
    }

    /**
     * Does nothing because there is no form view.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param o        form object
     * @param errors   error packet
     * @throws Exception
     */
    @Override
    protected void showForm(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws Exception {
        // noop
    }

    /**
     * Returns 'true' so binding and validation errors can be handled here
     *
     * @param request wf4j request
     * @param o       form object
     * @param errors  error packet
     * @return true
     */
    @Override
    protected boolean handleBindingAndValidationErrors(HttpServletRequest request, Object o, FormErrors errors) {
        return true;
    }

    @Override
    protected void onBindingAndValidationErrors(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws Exception {
        JSONObject json = new JSONObject();
        PrintWriter writer = response.getWriter();
        try {
            json.put("errors", errors.toJSON());
            writer.write(json.toString());
        } catch (JSONException e) {
            log.warn(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            writer.flush();
            writer.close();
        }
    }

    private static Logger log = LoggerFactory.getLogger(AsynchronousFormAction.class);

}
