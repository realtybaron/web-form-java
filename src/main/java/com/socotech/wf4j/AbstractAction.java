/*
 * AbstractAction
 */
package com.socotech.wf4j;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p/> Provides basic services that all actions need, and provides reasonable defaults. </p> <p/> <p/> This class provides storage for the name of the action, and a static logging
 * category for all action objects. WebActions derived from this object do not require authorization by default; if your derived action needs a member to be signed in in order to run
 * it, override <code>mustBeSignedIn</code>. </p>
 */
public abstract class AbstractAction implements WebExecutable {

    /**
     * Invoked as alternative to redirecting user to a sign in page
     *
     * @param request  web request
     * @param response web response
     */
    public void handleUnauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("access-denied.jsp").forward(request, response);
    }

    /**
     * Determine if request is an include or an already committed response.  Otherwise, forward to the page.
     *
     * @param request  web request
     * @param response web response
     * @param page     target page
     * @throws IOException
     * @throws ServletException
     */
    protected void render(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
        RequestDispatcher rd = request.getRequestDispatcher(page);
        if (Requests.isIncludeRequest(request) || response.isCommitted()) {
            log.debug(this + " including " + page);
            rd.include(request, response);
        } else {
            log.debug(this + " forwarding to " + page);
            rd.forward(request, response);
        }
    }

    /**
     * Include page in the response.
     *
     * @param request  web request
     * @param response web response
     * @param page     target page
     * @throws IOException
     * @throws ServletException
     */
    protected void includeIn(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
        request.getRequestDispatcher(page).include(request, response);
    }

    /**
     * <p/> Forwards to the specified page. This does not cause a new HTTP request, and the URL in the user's browser remains the same, but control is passed to the new page. </p>
     * <p/> Note that it is important to forward to JSP pages; WebActions (.act pages) should be redirected. This is to ensure that the sessions are not prematurely closed. </p>
     *
     * @param request  The HTTP request to service from the client
     * @param response The HTTP response for the server to write
     * @param page     The page to forward to
     * @throws IOException
     * @throws ServletException
     */
    protected void forwardTo(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
        log.debug(this + " forwarding to " + page);
        request.getRequestDispatcher(page).forward(request, response);
    }

    /**
     * <p/> Redirects the user's browser to the specified page. This causes a new HTTP request, and the URL in the user's browser is changed. </p>
     *
     * @param response The HTTP response for the server to write
     * @param page     The page to forward to
     * @throws IOException
     * @throws ServletException
     */
    protected boolean redirectTo(HttpServletResponse response, String page) throws IOException, ServletException {
        log.debug(this + " redirecting to " + page);
        response.sendRedirect(page);
        return true;
    }

    /**
     * <p/> Redirects the user's browser to the specified page. This causes a new HTTP request, and the URL in the user's browser is changed. </p>
     *
     * @param response The HTTP response for the server to write
     * @param page     The page to forward to
     * @throws IOException
     * @throws ServletException
     */
    protected boolean redirectTo(HttpServletResponse response, String page, int status) throws IOException, ServletException {
        log.debug(this + " redirecting to " + page);
        response.setStatus(status);
        response.sendRedirect(page);
        return true;
    }

    /**
     * <p/> Utility method to send the user back to a specific page because validation failed. </p> <p/> <p/> Given an FormErrors that's been filled in already, places it in the
     * "error" attribute of the request and redirects the user to the specified page with the same querystring (in other words, it leaves the arguments in the URL the same). </p>
     *
     * @param request  The HTTP request to service from the client
     * @param response The HTTP response for the server to write
     * @param page     The page to send the user back to
     * @param errors   The error packet with codes and descriptions
     * @throws IOException
     * @throws ServletException
     */
    protected void renderWithErrors(HttpServletRequest request, HttpServletResponse response, String page, FormErrors errors) throws IOException, ServletException {
        // If there's no error packet, create a new one
        if (errors == null) {
            errors = new FormErrors();
        }
        // Add the error packet to the request for the signin page to pick up
        request.setAttribute(WF4JScopeVariable.error_list.name(), errors);
        // Build up the page with the same arguments as before
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            page += "?" + queryString;
        }
        log.info(this + " forwarding to " + page + " because " + errors.toString());
        this.render(request, response, page);
    }

    /**
     * <p/> Utility method to send the user back to a specific page because validation failed. </p> <p/> <p/> Given an FormErrors that's been filled in already, places it in the
     * "error" attribute of the request and redirects the user to the specified page with the same querystring (in other words, it leaves the arguments in the URL the same). </p>
     *
     * @param request  The HTTP request to service from the client
     * @param response The HTTP response for the server to write
     * @param page     The page to send the user back to
     * @param errors   The error packet with codes and descriptions
     * @throws IOException
     * @throws ServletException
     */
    protected void forwardToWithErrors(HttpServletRequest request, HttpServletResponse response, String page, FormErrors errors) throws IOException, ServletException {
        // If there's no error packet, create a new one
        if (errors == null) {
            errors = new FormErrors();
        }
        // Add the error packet to the request for the signin page to pick up
        request.setAttribute(WF4JScopeVariable.error_list.name(), errors);
        // Build up the page with the same arguments as before
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            page += "?" + queryString;
        }
        log.info(this + " forwarding to " + page + " because " + errors.toString());
        this.forwardTo(request, response, page);
    }

    /**
     * <p/> Helper function for when an action hits some terrible problem that requires the action to be completely aborted, based on some thrown exception. The user is sent to a
     * generic internal error page, and an error is logged. </p> <p/> <p/> Note that this method simply returns after setting the error code; you should not continue to write data
     * to the response after calling this. Just return as soon as possible. </p>
     *
     * @param request  The request being processed
     * @param response The response being sent to
     * @param why      A friendly string to display to the user
     * @param except   If not null, the exception that caused the problem.
     * @throws IOException
     */
    protected void raiseServerError(HttpServletRequest request, HttpServletResponse response, String why, Exception except) throws IOException {

        // except should never be null, but just to be sure...
        if (except != null) {
            log.error("Server Error Raised: " + why, except);
        } else {
            log.error("Server Error Raised: " + why);
        }
        request.setAttribute("exception", except);
        request.setAttribute("message", why);
        if (response.isCommitted()) {
            log.warn("Attempting to raise server error, but response is already committed");
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * <p/> Helper function for when an action hits some terrible problem that requires the action to be completely aborted. The user is sent to a generic internal error page, and
     * an error is logged. </p> <p/> <p/> Note that this method simply returns after setting the error code; you should not continue to write data to the response after calling
     * this. Just return as soon as possible. </p>
     *
     * @param request  The request being processed
     * @param response The response being sent to
     * @param why      A friendly string to display to the user
     * @throws IOException
     */
    protected void raiseServerError(HttpServletRequest request, HttpServletResponse response, String why) throws IOException {
        String errorMsg = "Server Error Raised: " + why;
        log.error(errorMsg);
        request.setAttribute("message", why);
        if (response.isCommitted()) {
            log.warn("Attempting to raise server error, but response is already committed");
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * <p/> The name of this action. </p>
     */
    protected String name;

    /**
     * <p/> A logging category for each action. </p>
     */
    private static Logger log = LoggerFactory.getLogger(AbstractAction.class);
}
