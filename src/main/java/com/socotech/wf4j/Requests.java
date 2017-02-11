/**
 * Requests.java
 */
package com.socotech.wf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * <p/> Helper methods for requests in the all wf4j applications. </p> <p/> Web applications should use the methods in this object to load the member that is currently logged in, if
 * any. </p>
 */
public class Requests {

    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(HttpServletRequest request, String name) {
        return (T) request.getAttribute(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(HttpServletRequest request, String name, T defaultValue) {
        Object value = getAttribute(request, name);
        return value == null ? defaultValue : (T) value;
    }

    /**
     * Return the "Referer" header in the request or an empty string if present.
     *
     * @param request wf4j request
     * @return referer or empty string
     */
    public static String getRefererHeader(HttpServletRequest request) {
        String header = request.getHeader("Referer");
        return StringUtils.defaultString(header);
    }

    /**
     * Is this an include request?
     *
     * @param request current request
     * @return true, if include request URI attribute is not null
     */
    public static boolean isIncludeRequest(HttpServletRequest request) {
        return request.getAttribute(WebPaths.INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
    }

    /**
     * Return the request URI for the given request, detecting an include request URL if called within a RequestDispatcher include. <p>As the value returned by
     * <code>request.getRequestURI()</code> is <i>not</i> decoded by the servlet container, this method will decode it.
     *
     * @param request current HTTP request
     * @return the request URI
     * @throws UnsupportedEncodingException if URL cannot be decoded
     */
    public static String getRequestUri(HttpServletRequest request) throws UnsupportedEncodingException {
        return getRequestUri(request, WebPaths.INCLUDE_REQUEST_URI_ATTRIBUTE);
    }

    /**
     * Return the request URI for root of the given request. If this is a forwarded request, correctly resolves to the request URI of the original request.
     *
     * @param request current HTTP request
     * @return the request URI
     * @throws UnsupportedEncodingException if URL cannot be decoded
     */
    public static String getOriginatingRequestUri(HttpServletRequest request) throws UnsupportedEncodingException {
        return getRequestUri(request, WebPaths.FORWARD_REQUEST_URI_ATTRIBUTE);
    }

    /**
     * Return the query string for the given request, detecting an include request URL if called within a RequestDispatcher include. <p>As the value returned by
     * <code>request.getRequestURI()</code> is <i>not</i> decoded by the servlet container, this method will decode it.
     *
     * @param request current HTTP request
     * @return the query string
     * @throws UnsupportedEncodingException if URL cannot be decoded
     */
    public static String getQueryString(HttpServletRequest request) throws UnsupportedEncodingException {
        return getQueryString(request, WebPaths.INCLUDE_QUERY_STRING_ATTRIBUTE);
    }

    /**
     * Return the query string for root of the given request. If this is a forwarded request, correctly resolves to the request URI of the original request.
     *
     * @param request current HTTP request
     * @return the query string
     * @throws UnsupportedEncodingException if URL cannot be decoded
     */
    public static String getOriginatingQueryString(HttpServletRequest request) throws UnsupportedEncodingException {
        return getQueryString(request, WebPaths.FORWARD_QUERY_STRING_ATTRIBUTE);
    }

    private static String getRequestUri(HttpServletRequest req, String attr) throws UnsupportedEncodingException {
        String uri = StringUtils.defaultIfEmpty((String) req.getAttribute(attr), req.getRequestURI());
        String encoding = StringUtils.defaultIfEmpty(req.getCharacterEncoding(), "UTF-8");
        return URLDecoder.decode(StringUtils.defaultString(uri), encoding);
    }

    private static String getQueryString(HttpServletRequest req, String attr) throws UnsupportedEncodingException {
        String query = StringUtils.defaultIfEmpty((String) req.getAttribute(attr), req.getQueryString());
        String encoding = StringUtils.defaultIfEmpty(req.getCharacterEncoding(), "UTF-8");
        return URLDecoder.decode(StringUtils.defaultString(query), encoding);
    }

    /**
     * Retrieve a String param from the request, but return default value if not found
     *
     * @param request      wf4j request
     * @param name         param name
     * @param defaultValue default value
     * @return value in request or default value provided by caller
     */
    public static String getStringParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    /**
     * Retrieve a String param from the request, but throw exception if not found
     *
     * @param request wf4j request
     * @param name    param name
     * @return value in request or default value provided by caller
     */
    public static String getRequiredStringParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        Validate.notEmpty(name, "Required parameter not found in request: " + name);
        return value;
    }

    /**
     * Retrieve a boolean param from the request, but return default value if not found
     *
     * @param request      req
     * @param name         param name
     * @param defaultValue default value
     * @return value in request or default value provided by caller
     */
    public static boolean getBooleanParameter(HttpServletRequest request, String name, boolean defaultValue) {
        String value = request.getParameter(name);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        } else {
            return Boolean.valueOf(value);
        }
    }

    public static boolean getBooleanAttribute(HttpServletRequest request, String name, boolean defaultValue) {
        Object value = request.getAttribute(name);
        if (value == null) {
            return defaultValue;
        } else {
            return Boolean.class.cast(value);
        }
    }

    /**
     * Retrieve a required boolean param from the request, but throw exception if not found
     *
     * @param request wf4j request
     * @param name    param name
     * @return value in request
     */
    public static boolean getRequiredBooleanParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        Validate.notEmpty("Required parameter not found in request: " + name);
        return Boolean.valueOf(value);
    }

    /**
     * Retrieve a number param from the request, but return default value if not found
     *
     * @param request      wf4j request
     * @param name         param name
     * @param defaultValue default value
     * @return value in request or default value provided by caller
     */
    public static Number getIdParameter(HttpServletRequest request, String name, Number defaultValue) {
        String value = request.getParameter(name);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * Retrieve a required int attribute from the request, but throw exception if not found
     *
     * @param request wf4j request
     * @param name    param name
     * @return value in request
     */
    public static Number getRequiredIdParameter(HttpServletRequest request, String name) {
        Number value = getIdParameter(request, name, null);
        Validate.notNull(value, "Required parameter not found in request: " + name);
        return value;
    }

    /**
     * Retrieve a int param from the request, but return default value if not found
     *
     * @param request      wf4j request
     * @param name         param name
     * @param defaultValue default value
     * @return value in request or default value provided by caller
     */
    public static Integer getIntParameter(HttpServletRequest request, String name, Integer defaultValue) {
        String value = request.getParameter(name);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * Get int attribute from the request. but return default value if not found.
     *
     * @param request      wf4j request
     * @param name         param name
     * @param defaultValue default value
     * @return value in request or default value provided by caller
     */
    public static Number getIdAttribute(HttpServletRequest request, String name, Number defaultValue) {
        Object value = request.getAttribute(name);
        if (value != null) {
            return Integer.parseInt(value.toString());
        } else if (defaultValue != null) {
            return defaultValue;
        } else {
            return null;
        }
    }

    /**
     * Get int attribute from the request. but return default value if not found.
     *
     * @param request      wf4j request
     * @param name         param name
     * @param defaultValue default value
     * @return value in request or default value provided by caller
     */
    public static Integer getIntAttribute(HttpServletRequest request, String name, Integer defaultValue) {
        Object value = request.getAttribute(name);
        if (value != null) {
            return Integer.parseInt(value.toString());
        } else if (defaultValue != null) {
            return defaultValue;
        } else {
            return null;
        }
    }

    /**
     * Retrieve a required int attribute from the request, but throw exception if not found
     *
     * @param request wf4j request
     * @param name    param name
     * @return value in request
     */
    public static Integer getRequiredIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        Validate.notEmpty(value, "Required parameter not found in request: " + name);
        return Integer.parseInt(value);
    }

    /**
     * Retrieve a long param from the request, but return default value if not found
     *
     * @param request      wf4j request
     * @param name         param name
     * @param defaultValue default value
     * @return value in request or default value provided by caller
     */
    public static Long getLongParameter(HttpServletRequest request, String name, Long defaultValue) {
        String value = request.getParameter(name);
        if (StringUtils.isEmpty(value) || !StringUtils.isNumeric(value)) {
            return defaultValue == null ? null : defaultValue;
        } else {
            return Long.parseLong(value);
        }
    }

    /**
     * Retrieve a long attribute from the request, but return default value if not found
     *
     * @param req          wf4j request
     * @param name         param name
     * @param defaultValue default value
     * @return value in request or default value provided by caller
     */
    public static Long getLongAttribute(HttpServletRequest req, String name, Long defaultValue) {
        Object value = req.getAttribute(name);
        if (value == null) {
            return defaultValue;
        } else {
            return Long.parseLong(value.toString());
        }
    }

    /**
     * Retrieve a required long param from the request, but throw exception if not found
     *
     * @param request wf4j request
     * @param name    param name
     * @return value in request
     */
    public static Long getRequiredLongParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        Validate.notEmpty(value, "Required parameter not found in request: " + name);
        return Long.parseLong(value);
    }

    private static String getLoginToken(HttpServletRequest request) {
        // What's the token we're looking for? Get it from the cookie. No cookie means not signed in.
        Cookie c = WebUtil.getCookie(request, LOGIN_COOKIE);
        if (c == null) {
            return null;
        } else {
            return c.getValue();
        }
    }

    /**
     * If the user is logged in, reset the expiration so it doesn't run out
     *
     * @param request  wf4j request
     * @param response wf4j response
     */
    public static void resetAuthTokenTimeout(HttpServletRequest request, HttpServletResponse response) {
        // If the user is logged in, we have to reset the auth token so that it stays
        // around for as long as the underlying session stays around. Otherwise, the
        // auth token may expire too soon, or too late.
        String token = getLoginToken(request);
        if (token != null) {
            HttpSession session = request.getSession();
            int minutesToLive = session.getMaxInactiveInterval() / 60;
            WebUtil.setCookie(request, response, LOGIN_COOKIE, token, minutesToLive);
        }
    }

    /**
     * <p/> Helper function for various report queries: initialize the start and end dates to one month ago and today,
     * respectively. </p>
     *
     * @param request The HTTP request to set up
     */
    public static void initializeDates(HttpServletRequest request) {
        request.setAttribute("end", DateManager.dateToHumanString(DateManager.today()));
        request.setAttribute("start", DateManager.dateToHumanString(DateManager.yestermonth()));
    }

    /**
     * <p/> Validates a "start" and "end" date in a report with two date fields, making sure they're valid dates,
     * they're in the right order, and so on. If there are problems, errors are added to the error packet; if not, then
     * start and end parameters have the parsed dates in them. </p>
     *
     * @param request   The HTTP request
     * @param dateStart The start date, cannot be null
     * @param dateEnd   The end date, cannot be null
     * @param errors    The error packet to update, cannot be null
     */
    public static void validateDates(HttpServletRequest request, Date dateStart, Date dateEnd, ErrorPacket errors) {

        Validate.notNull(dateStart);
        Validate.notNull(dateEnd);
        Validate.notNull(errors);

        // Get the date range from the request
        String paramStartDate = request.getParameter(SharedScopeVariable.date_start.name());
        if (paramStartDate == null) {
            // look for start in session, but fall back on 30 days ago
            Date sessionStartDate = WebUtil.getOrCreateSessionAttribute(request, SharedScopeVariable.date_start.name(), DateManager.rollDate(-30));
            dateStart.setTime(sessionStartDate.getTime());
        } else {
            try {
                // Can we parse the start date?
                dateStart.setTime(DateManager.humanStringToDate(paramStartDate).getTime());
                dateStart = DateManager.setToStartOfDay(dateStart);
                WebUtil.setSessionAttribute(request, SharedScopeVariable.date_start.name(), dateStart);

                // Is the start date "logical"? (See issue #2888)
                if (DateManager.getField(dateStart, Calendar.YEAR) < 2000) {
                    errors.put(SharedScopeVariable.date_start.name(), "Please enter a start date after January 1, 2000.");
                }

            } catch (ParseException pe) {
                errors.put(SharedScopeVariable.date_start.name(), "The start date must be a valid date and in format MM/DD/YYYY.");
            }
        }
        String paramEndDate = request.getParameter(SharedScopeVariable.date_end.name());
        if (paramEndDate == null) {
            // look for end in session, but fall back on today's date
            Date sessionEndDate = WebUtil.getOrCreateSessionAttribute(request, SharedScopeVariable.date_end.name(), DateManager.today());
            dateEnd.setTime(sessionEndDate.getTime());
            dateEnd = DateManager.setToEndOfDay(dateEnd);
        } else {
            try {
                // Can we parse the end date? (And make it go up to the end of
                // that day)
                dateEnd.setTime(DateManager.humanStringToEndDate(paramEndDate).getTime());
                DateManager.setToEndOfDay(dateEnd);
                WebUtil.setSessionAttribute(request, SharedScopeVariable.date_end.name(), dateEnd);

                // Is the end date "logical"? (See issue #2888)
                if (!dateEnd.before(DateManager.tomorrow())) {
                    errors.put(SharedScopeVariable.date_end.name(), "Please enter a end date no later than today.");
                }

            } catch (ParseException pe) {
                errors.put(SharedScopeVariable.date_end.name(), "The end date must be be a valid date and in format MM/DD/YYYY.");
            }
        }

        // Are the dates the right order?
        if (dateStart != null && dateEnd != null && dateStart.after(dateEnd)) {
            errors.put(SharedScopeVariable.date_end.name(), "The start date can't be after the end date.");
        }

        log.debug("Your report's final date range: " + DateManager.dateToString(dateStart) + " to " + DateManager.dateToString(dateEnd));
    }

    /**
     * Name of authentication cookie
     */
    public static final String LOGIN_COOKIE = "AUTHTOKEN";

    /**
     * <p/> A logging category for this class. </p>
     */
    private static Logger log = Logger.getLogger(Requests.class);
}
