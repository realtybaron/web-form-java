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
import org.apache.log4j.Logger;

/**
 * <p/> Provides basic services that all actions need, and provides reasonable defaults. </p> <p/> <p/> This class provides storage for the name of the action, and a static logging
 * category for all action objects. Actions derived from this object do not require authorization by default; if your derived action needs a member to be signed in in order to run
 * it, override <code>mustBeSignedIn</code>. </p>
 */
public abstract class AbstractAction {

    /**
     * <p/> Constructs a new un-named action. Sets as its name the simple name of the class without the word "action" -- for example, if the class is <code>FooBarAction</code>,
     * then the name of this action is "fooBar". </p>
     */
    protected AbstractAction() {
        String cname = this.getClass().getSimpleName();
        this.name = StringUtils.uncapitalize(StringUtils.chomp(cname, "Action"));
    }

    /**
     * <p/> Constructs a new named action. </p>
     *
     * @param name The name of the action
     */
    protected AbstractAction(String name) {
        this.name = name;
    }

    /**
     * <p/> Returns this action's short, alphanumeric name. The name is used to run the action in a server; for example, the URL <code>/controller/foobar</code> will run the action
     * named "foobar". </p>
     *
     * @return The name of the action
     */
    public String getName() {
        return name;
    }

    /**
     * <p/> Returns the string form of this action, which is basically just its name. </p>
     *
     * @return The string form of the action
     */
    @Override
    public String toString() {
        return "Action " + name;
    }

    /**
     * <p/> Returns true if this action shouldn't be performed unless there is a signed-in user. The default for actions derived from this class is false -- no one needs to be
     * signed in. </p>
     *
     * @param request HTTP request
     * @return false
     */
    public boolean mustBeSignedIn(HttpServletRequest request) {
        return false;
    }


    /**
     * Should user be redirected to sign in page?
     *
     * @param request wf4j request
     * @return true, if user should provide credentials on a sign in page
     */
    public boolean isSignInPrompt(HttpServletRequest request) {
        return true;
    }

    /**
     * Returns the name of the view to which user is forwarded in order to ca
     *
     * @param request HTTP request
     * @return name of view which prompts user for login/password or account signup (i.e. signin.jsp)
     */
    public String getSignInView(HttpServletRequest request) {
        return "signin.jsp";
    }


    /**
     * Invoked as alternative to redirecting user to a sign in page
     *
     * @param request  wf4j request
     * @param response wf4j response
     */
    public void handleUnauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("access-denied.jsp").forward(request, response);
    }

    /**
     * <p/> Returns true if the logged-in member of the current request meets all of the privilege requirements for this particular page. Should return false for any other reason.
     * By default, we assume that the logged-in user meets all requirements. </p> <p/> This method is called by the Controller, and derived objects which implement it can assume
     * that the member has logged in by the time it's called (in other words, they can safely call <code>getMember()</code> to return the currently logged-in user). </p>
     *
     * @param request The HTTP request to service from the client
     * @return true if the user meets all the privilege requirements
     */
    public boolean meetsPrivilegeRequirements(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    /**
     * Determine if request is an include or an already committed response.  Otherwise, forward to the page.
     *
     * @param request  wf4j request
     * @param response wf4j response
     * @param page     target page
     * @throws IOException
     * @throws ServletException
     */
    protected void render(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {

        // Make sure we're not forwarding to an ACT page
        if (StringUtils.substringBefore(page, "?").endsWith(".act")) {
            throw new ServletException("Attempt to include or forward to " + page + " is illegal because it is an action. Use redirect instead.");
        }

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
     * @param request  wf4j request
     * @param response wf4j response
     * @param page     target page
     * @throws IOException
     * @throws ServletException
     */
    protected void includeIn(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
        // Make sure we're not forwarding to an ACT page
        if (StringUtils.substringBefore(page, "?").endsWith(".act")) {
            throw new ServletException("Attempt to include " + page + " is illegal because it is an action. Use redirect instead.");
        }
        request.getRequestDispatcher(page).include(request, response);
    }

    /**
     * <p/> Forwards to the specified page. This does not cause a new HTTP request, and the URL in the user's browser remains the same, but control is passed to the new page. </p>
     * <p/> Note that it is important to forward to JSP pages; Actions (.act pages) should be redirected. This is to ensure that the sessions are not prematurely closed. </p>
     *
     * @param request  The HTTP request to service from the client
     * @param response The HTTP response for the server to write
     * @param page     The page to forward to
     * @throws IOException
     * @throws ServletException
     */
    protected void forwardTo(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {

        // Make sure we're not forwarding to an ACT page
        int dotpos = StringUtils.lastIndexOf(page, '.');
        if (dotpos >= 0) {
            String extension = StringUtils.substring(page, dotpos + 1);
            if (extension.equals("act")) {
                throw new ServletException("Attempt to forward to " + page + " is illegal because it is an action. Use redirect instead.");
            }
        }

        // Simply forward
        log.debug(this + " forwarding to " + page);
        request.getRequestDispatcher(page).forward(request, response);
    }

    /**
     * <p/> Redirects the user's browser to the specified page. This causes a new HTTP request, and the URL in the user's browser is changed. </p>
     *
     * @param request  The HTTP request to service from the client
     * @param response The HTTP response for the server to write
     * @param page     The page to forward to
     * @throws IOException
     * @throws ServletException
     */
    protected void redirectTo(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
        log.debug(this + " redirecting to " + page);
        response.sendRedirect(page);
    }

    /**
     * <p/> Redirects the user's browser to the specified page. This causes a new HTTP request, and the URL in the user's browser is changed. </p>
     *
     * @param request  The HTTP request to service from the client
     * @param response The HTTP response for the server to write
     * @param page     The page to forward to
     * @throws IOException
     * @throws ServletException
     */
    protected void redirectTo(HttpServletRequest request, HttpServletResponse response, String page, int status) throws IOException, ServletException {
        log.debug(this + " redirecting to " + page);
        response.setStatus(status);
        response.sendRedirect(page);
    }

    /**
     * <p/> Utility method to send the user back to a specific page because validation failed. </p> <p/> <p/> Given an ErrorPacket that's been filled in already, places it in the
     * "error" attribute of the request and redirects the user to the specified page with the same querystring (in other words, it leaves the arguments in the URL the same). </p>
     *
     * @param request  The HTTP request to service from the client
     * @param response The HTTP response for the server to write
     * @param page     The page to send the user back to
     * @param errors   The error packet with codes and descriptions
     * @throws IOException
     * @throws ServletException
     */
    protected void renderWithErrors(HttpServletRequest request, HttpServletResponse response, String page, ErrorPacket errors) throws IOException, ServletException {

        // If there's no error packet, create a new one
        if (errors == null) {
            errors = new ErrorPacket();
        }

        // Add the error packet to the request for the signin page to pick up
        request.setAttribute("errors", errors);

        // Build up the page with the same arguments as before
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            page += "?" + queryString;
        }
        log.info(this + " forwarding to " + page + " because " + errors.toString());
        this.render(request, response, page);
    }

    /**
     * <p/> Utility method to send the user back to a specific page because validation failed. </p> <p/> <p/> Given an ErrorPacket that's been filled in already, places it in the
     * "error" attribute of the request and redirects the user to the specified page with the same querystring (in other words, it leaves the arguments in the URL the same). </p>
     *
     * @param request  The HTTP request to service from the client
     * @param response The HTTP response for the server to write
     * @param page     The page to send the user back to
     * @param errors   The error packet with codes and descriptions
     * @throws IOException
     * @throws ServletException
     */
    protected void forwardToWithErrors(HttpServletRequest request, HttpServletResponse response, String page, ErrorPacket errors) throws IOException, ServletException {

        // If there's no error packet, create a new one
        if (errors == null) {
            errors = new ErrorPacket();
        }

        // Add the error packet to the request for the signin page to pick up
        request.setAttribute("errors", errors);

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
    private static Logger log = Logger.getLogger(AbstractAction.class);
}
