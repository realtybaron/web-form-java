/**
 * WebUtil.java
 */
package com.socotech.wf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.common.base.Strings;
import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * <p/> A set of static methods useful in web applications. </p>
 */
public class WebUtil {

    /**
     * <p/> Private constructor to prevent accidental instantiation. </p>
     */
    private WebUtil() {
        // nop
    }

    /**
     * <p/> Given an HTTP request, returns the full URI that the request was. For example, if the request was a GET for <code>http://www.cnn.com/foo/bar/baz.html?id=4</code>, then
     * this method returns <code>/foo/bar/baz.html?id=4</code>.
     *
     * @param request The HTTP request
     * @return the full URI
     */
    public static String getFullRequestURI(HttpServletRequest request) {
        StringBuilder uri = new StringBuilder(request.getRequestURI());
        String qs = request.getQueryString();
        if (StringUtils.isNotEmpty(qs)) {
            uri.append("?");
            uri.append(qs);
        }
        return uri.toString();
    }

    /**
     * <p/> A helper function for building forms, returning the named parameter from the request parameter, or if not present the attribute, or if not present the default. </p>
     *
     * @param request      the HTTP request, must not be null
     * @param field        The field of the parameter to look at
     * @param defaultValue The value to return if the param isn't in the parameter or attributes list
     * @return the value to use in the form field
     */
    public static String getParam(HttpServletRequest request, String field, String defaultValue) {
        String rv = defaultValue;
        if (request.getParameter(field) != null) {
            rv = request.getParameter(field);
        } else if (request.getAttribute(field) != null) {
            rv = (String) request.getAttribute(field);
        }
        return rv;
    }

    /**
     * Set a session attribute
     *
     * @param request HTTP request
     * @param attr    name
     * @param value   value
     */
    public static void setSessionAttribute(HttpServletRequest request, String attr, Object value) {
        Validate.notNull(request, "Request is null");
        Validate.notEmpty(attr, "Attribute Name is null");
        HttpSession session = request.getSession(true);
        session.setAttribute(attr, value);
    }

    /**
     * Remove a session attribute
     *
     * @param request HTTP request
     * @param attr    name
     */
    public static void removeSessionAttribute(HttpServletRequest request, String attr) {
        Validate.notNull(request, "Request is null");
        Validate.notEmpty(attr, "Attribute Name is null");
        HttpSession session = request.getSession(true);
        session.removeAttribute(attr);
    }

    /**
     * Check the session for an existing attribute with the same name.  If found, return it.  If not, create it then return it.
     *
     * @param request      req
     * @param attr         attribute name
     * @param defaultValue use as default if not found
     * @return value in scope if found, default value if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> T getOrCreateSessionAttribute(HttpServletRequest request, String attr, T defaultValue) {
        HttpSession session = request.getSession(true);
        Object value = session.getAttribute(attr);
        if (value != null) {
            return (T) value;
        } else {
            session.setAttribute(attr, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Check the session for an existing attribute with the same name.  If found, return it.  If not, return null.
     *
     * @param request req
     * @param attr    attribute name
     * @return value in scope if found, default value if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSessionAttribute(HttpServletRequest request, String attr) {
        HttpSession session = request.getSession(true);
        return (T) session.getAttribute(attr);
    }

    /**
     * Check the context for an existing attribute with the same name.  If found, return it.  If not, create it then return it.
     *
     * @param request      req
     * @param attr         attribute name
     * @param defaultValue use as default if not found
     * @return value in scope if found, default value if not found
     */
    public static <T> T getOrCreateContextAttribute(HttpServletRequest request, String attr, T defaultValue) {
        return getOrCreateContextAttribute(request.getSession(true).getServletContext(), attr, defaultValue);
    }

    /**
     * Check the context for an existing attribute with the same name.  If found, return it.  If not, create it then return it.
     *
     * @param session      user session
     * @param attr         attribute name
     * @param defaultValue use as default if not found
     * @return value in scope if found, default value if not found
     */
    public static <T> T getOrCreateContextAttribute(HttpSession session, String attr, T defaultValue) {
        return getOrCreateContextAttribute(session.getServletContext(), attr, defaultValue);
    }

    /**
     * Check the context for an existing attribute with the same name.  If found, return it.  If not, create it then return it.
     *
     * @param context      servlet context
     * @param attr         attribute name
     * @param defaultValue use as default if not found
     * @return value in scope if found, default value if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> T getOrCreateContextAttribute(ServletContext context, String attr, T defaultValue) {
        Object value = context.getAttribute(attr);
        if (value != null) {
            return (T) value;
        } else {
            context.setAttribute(attr, defaultValue);
            return defaultValue;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getContextAttribute(HttpServletRequest request, String attr) {
        return (T) getContextAttribute(request.getSession(true).getServletContext(), attr);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getContextAttribute(HttpSession session, String attr) {
        return (T) getContextAttribute(session.getServletContext(), attr);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getContextAttribute(ServletContext context, String attr) {
        return context != null ? (T) context.getAttribute(attr) : null;
    }

    /**
     * Retreive an attribute from the session and remove it to prevent future use
     *
     * @param request http request
     * @param attr    attribute name
     * @return attribute value
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAndRemoveSessionAttribute(HttpServletRequest request, String attr) {
        HttpSession session = request.getSession(true);
        Object value = session.getAttribute(attr);
        if (value != null) {
            session.removeAttribute(attr);
        }
        return (T) value;
    }

    /**
     * Retreive an attribute from the context and remove it to prevent future use
     *
     * @param context http request
     * @param attr    attribute name
     * @return attribute value
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAndRemoveContextAttribute(ServletContext context, String attr) {
        Object value = context.getAttribute(attr);
        if (value != null) {
            context.removeAttribute(attr);
        }
        return (T) value;
    }

    /**
     * Retreive a required attribute from the session and remove it to prevent future use
     *
     * @param req  http request
     * @param attr attribute name
     * @return attribute value
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAndRemoveRequiredSessionAttribute(HttpServletRequest req, String attr) {
        Object value = getAndRemoveSessionAttribute(req, attr);
        if (value == null) {
            throw new IllegalStateException("Unable to find required attribute in session scope: " + attr);
        }
        return (T) value;
    }

    /**
     * Find required attribute by name in session scope
     *
     * @param request req
     * @param attr    attribute name
     * @return attribute value if found, throw unchecked exception if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> T getRequiredSessionAttribute(HttpServletRequest request, String attr) {
        Object value = getOrCreateSessionAttribute(request, attr, null);
        Validate.notNull(value, "Unable to find required attribute in session scope: " + attr);
        return (T) value;
    }

    /**
     * Scan both scopes---request and session--to find required attribute by name
     *
     * @param request req
     * @param attr    attribute name
     * @return attribute value if found, throw unchecked exception if not found
     */
    public static Object findRequiredAttribute(HttpServletRequest request, String attr) {
        Object value = request.getAttribute(attr);
        if (value == null) {
            value = request.getSession(true).getAttribute(attr);
        }
        Validate.notNull(value, "Unable to find required attribute in scope: " + attr);
        return value;
    }

    /**
     * Adds the string to the set of messages to display
     *
     * @param req     form request
     * @param message message to be added
     */
    @SuppressWarnings("unchecked")
    public static void addMessage(HttpServletRequest req, String message) {
        Set<String> messages = WebUtil.getOrCreateSessionAttribute(req, WF4JScopeVariable.message_list.name(), new ListOrderedSet());
        messages.add(message);
    }

    /**
     * messages in the session? move from session to request scope.
     *
     * @param req form request
     */
    public static void dumpErrorsToRequest(HttpServletRequest req) {
        String attrName = "errors";
        FormErrors errors = WebUtil.getAndRemoveSessionAttribute(req, attrName);
        if (errors != null && !errors.isEmpty()) {
            req.setAttribute(attrName, errors);
        }
    }

    /**
     * messages in the session? move from session to request scope.
     *
     * @param req form request
     */
    public static void dumpMessagesToRequest(HttpServletRequest req) {
        String attrName = WF4JScopeVariable.message_list.name();
        Set<String> set = WebUtil.getAndRemoveSessionAttribute(req, attrName);
        if (set != null && !set.isEmpty()) {
            req.setAttribute(attrName, set);
        }
    }

    /**
     * Helper function to take HTML in a string and simply dump it to the response, as the entire output of the page
     *
     * @param response the response object
     * @param html     the HTML to send
     * @throws IOException i/o exception
     */
    public static void dumpHtmlToResponse(HttpServletResponse response, String html) throws IOException {
        if (!Strings.isNullOrEmpty(html)) {
            response.setContentType("text/html; charset=utf-8");
            response.setContentLength(html.length());
            OutputStream o = response.getOutputStream();
            byte[] bytes = html.getBytes();
            o.write(bytes);
            o.flush();
            o.close();
        }
    }


    /**
     * Given the name of a cookie, return its value from the request. If there is no such cookie return NULL.
     *
     * @param request an HTTP request
     * @param cookie  the name of the cookie you are looking for
     * @return the cookie
     */
    public static Cookie getCookie(HttpServletRequest request, String cookie) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (StringUtils.equals(c.getName(), cookie)) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * Set cookie
     *
     * @param response An HTTP response
     * @param host     the host
     * @param cookie   the cookie name
     * @param value    the value you want it to have
     * @param age      the maximum length of time for the cookie, in seconds
     */
    public static void setCookie(HttpServletResponse response, String host, String cookie, String value, int age) {
        Cookie c = new Cookie(cookie, value);
        c.setPath("/");
        c.setMaxAge(age);
        if (!host.equals("localhost")) {
            c.setDomain(host);
        }
        response.addCookie(c);
    }

    /**
     * Given an HTTP response, a cookie name, and an age, sets the cookie to the specified value and tells it to last for
     *
     * @param request  an HTTP servlet request
     * @param response An HTTP response
     * @param cookie   the cookie name
     * @param value    the value you want it to have
     * @param age      the maximum length of time for the cookie, in seconds
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookie, String value, int age) {
        Cookie c = new Cookie(cookie, value);
        c.setPath("/");
        c.setMaxAge(age);
        String host = StringUtils.substringBeforeLast(request.getHeader("host"), ":");
        if (!host.equals("localhost")) {
            c.setDomain(host);
        }
        response.addCookie(c);
    }

    /**
     * Given an HTTP request and response, and the name of a cookie, removes that cookie.
     *
     * @param request  form request
     * @param response form response
     * @param cookie   name of cookie to be removed
     */
    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookie) {
        setCookie(request, response, cookie, "", 0);
    }
}