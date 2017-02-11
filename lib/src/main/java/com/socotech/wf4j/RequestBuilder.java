/**
 * RequestBuilder.java
 */
package com.socotech.wf4j;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p/> A simple class that assists in creating URLs with query strings. This can be created from scratch, or from an existing request. Clients set (or add) parameter values and
 * can afterward generate an URI to be used in Web applications. There are three main ways to view this URL: </p> <ul> <li><code>toString</code> which is human-readable</li>
 * <li><code>toHref</code> which is URL-encoded for wf4j pages</li> <li><code>toAnchorLink</code> for building up an anchor tag</li> </ul> <p/> The map is sorted, so the attributes
 * in the query string come out in alphabetical order. </p>
 */
public class RequestBuilder extends QueryBuilder {

    /**
     * <p/> Creates a new request builder from a path, with no query string. </p>
     *
     * @param path The basic page, like "foo.jsp"
     */
    public RequestBuilder(String path) {
        super();
        Validate.notNull(path);
        Validate.isTrue(path.length() > 0, "Can't build with a zero-length path");
        this.base = path;
    }

    /**
     * <p/> Creates a new request builder with a path and a set of initial parameters. The map of parameters need to be strings to string arrays, as the standard for
     * <code>ServletRequest</code>. </p>
     *
     * @param path The basic page, like "foo.jsp"
     * @param params A set of parameters
     */
    @SuppressWarnings("unchecked")
    public RequestBuilder(String path, Map params) {
        super(params);
        Validate.notNull(path);
        Validate.isTrue(path.length() > 0, "Can't build with a zero-length path");
        this.base = path;
    }

    /**
     * <p/> Creates a new request builder from an existing request. Initializes with the same path and the same set of initial parameters. </p>
     *
     * @param req The request to base the request on
     */
    public RequestBuilder(HttpServletRequest req) {
        this(req.getRequestURI(), req.getParameterMap());
    }

    /**
     * <p/> Given the basic page and the parameters, returns an URL with those as query strings. For example, if the page is "foo.jsp" and the parameters are color=>blue and
     * height=>tall, then the string format is "foo.jsp?color=blue&height=tall". </p>
     *
     * @return An URL with appropriate query strings, useful in internal redirects and so on
     */
    @Override
    public String toString() {
        return this.toStringForm("&");
    }

    /**
     * <p/> When URLs are included in wf4j page HREF anchors, the ampersand used to connect attributes is supposed to be encoded as <code>&amp;</code>. This method does that. </p>
     *
     * @return An URL with appropriate query strings, useful in rendering in wf4j pages
     */
    public String toHref() {
        return this.toStringForm("&amp;");
    }

    /**
     * <p/>
     * Returns an HTML fragment which can be rendered directly in a form for clicking, with the specified text. For example, if you pass in "click here", then the returned string
     * will be <code><a href="LINK">click here</a></code>, where LINK is what is returned by <code>toHref()</code>.
     *
     * @param text The text to use as anchor; cannot be null
     * @return the HTML anchor text
     */
    public String toAnchorLink(String text) {
        return this.toAnchorLink(text, "_self");
    }

    /**
     * <p/>
     * Returns an HTML fragment which can be rendered directly in a form for clicking, with the specified text. For example, if you pass in "click here", then the returned string
     * will be <code><a href="LINK" target="TARGET">click here</a></code>, where LINK is what is returned by <code>toHref()</code>.
     *
     * @param text The text to use as anchor; cannot be null
     * @param target The target to use on the anchor; cannot be null
     * @return the HTML anchor text
     */
    public String toAnchorLink(String text, String target) {
        Validate.isTrue(StringUtils.isNotEmpty(text));
        Validate.isTrue(StringUtils.isNotEmpty(target));
        StringBuilder sb = new StringBuilder("<a href=\"");
        sb.append(this.toHref());
        sb.append("\" target=\"").append(target).append("\">");
        sb.append(text);
        sb.append("</a>");
        return sb.toString();
    }

    /**
     * <p/> Internal helper function for <code>toString</code> and <code>toHref</code>, takes a delimiter and builds the appropriate string form. </p>
     *
     * @param attributeDelimiter A delimiter, usually either ampersand or <code>&amp;</code>.
     * @return an URL that can be used to go to the right place
     */
    @Override
    public String toStringForm(String attributeDelimiter) {
        StringBuilder builder = new StringBuilder();
        builder.append(this.base);
        if (!this.parameters.isEmpty()) {
            builder.append("?").append(super.toStringForm(attributeDelimiter));
        }
        return builder.toString();
    }

    /**
     * <p/> The base page, like "foo.jsp" </p>
     */
    private String base;
}
