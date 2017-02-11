package com.socotech.wf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * Created by IntelliJ IDEA. User: marc Date: Nov 9, 2010 Time: 3:16:46 PM
 */
public class QueryBuilder {
  /**
   * <p/> The parameters managed by this query builder. </p>
   */
  protected Map<String, TreeSet<String>> parameters;

  /**
   * <p/> Creates a new query builder with an empty parameter map. The map of parameters need to be strings to string arrays, as the standard for <code>ServletRequest</code>.
   * </p>
   */
  public QueryBuilder() {
    this.parameters = new TreeMap<String, TreeSet<String>>();
  }

  /**
   * <p/> Creates a new query builder with a set of initial parameters. The map of parameters need to be strings to string arrays, as the standard for
   * <code>ServletRequest</code>. </p>
   *
   * @param params A set of parameters
   */
  public QueryBuilder(Map params) {
    this();
    Validate.notNull(params);
    for (Object o : params.keySet()) {
      String key = (String) o;
      String[] values = (String[]) params.get(o);
      TreeSet<String> s = new TreeSet<String>();
      s.addAll(Arrays.asList(values));
      this.parameters.put(key, s);
    }
  }

  /**
   * <p/> Sets the value of the specified attribute, replacing what's already there if there's one already set. </p>
   *
   * @param attribute The attribute to set
   * @param value     Its new value
   * @return this query builder for chaining
   */
  public QueryBuilder set(String attribute, String value) {
    TreeSet<String> s = new TreeSet<String>();
    s.add(value);
    this.parameters.put(attribute, s);
    return this;
  }

  /**
   * Returns the attribute for the specified parameter, if it is set and has only one value. If the attribute has never been set, or if it has more than one attribute, then null
   * is returned
   *
   * @param attribute the attribute to look for
   * @return the value
   */
  public String get(String attribute) {
    TreeSet<String> values = this.parameters.get(attribute);
    if ((values != null) && (values.size() == 1)) {
      return values.first();
    } else {
      return null;
    }
  }

  /**
   * <p/> Adds the value to the list of values managed by this attribute. If the attribute has already been set or added to, then this new value is appended onto it. If the
   * attribute has never been set, then the attribute is added to the parameter list with the specified value. </p>
   *
   * @param attribute The attribute to add to
   * @param value     The value to add
   */
  public void add(String attribute, String value) {
    TreeSet<String> values = this.parameters.get(attribute);
    if (values == null) {
      values = new TreeSet<String>();
      this.parameters.put(attribute, values);
    }
    values.add(value);
  }

  /**
   * <p/> Adds the value to the list of values managed by this attribute. If the attribute has already been set or added to, then this new value is appended onto it. If the
   * attribute has never been set, then the attribute is added to the parameter list with the specified value. </p>
   *
   * @param attribute The attribute to add to
   * @param value     The value to add
   * @return this
   */
  public QueryBuilder add(String attribute, int value) {
    this.add(attribute, String.valueOf(value));
    return this;
  }

  /**
   * <p/> Adds the value to the list of values managed by this attribute. If the attribute has already been set or added to, then this new value is appended onto it. If the
   * attribute has never been set, then the attribute is added to the parameter list with the specified value. </p>
   *
   * @param attribute The attribute to add to
   * @param value     The value to add
   */
  public void add(String attribute, long value) {
    this.add(attribute, String.valueOf(value));
  }

  /**
   * <p/> Sets the specified attribute to be a valueless attribute: in other words, it gets added to the list of attributes, but when rendered as an URL, it doesn't have the = or
   * the value after it. </p>
   *
   * @param attribute the attribute to set
   */
  public void set(String attribute) {
    this.parameters.put(attribute, null);
  }

  /**
   * Conveniently replaces an existing attribute or, if not already present, adds a new attribute.
   *
   * @param attribute The attribute to replace
   * @param value     The value to add
   */
  public void replace(String attribute, String value) {
    this.remove(attribute);
    this.add(attribute, value);
  }

  /**
   * Conveniently replaces an existing attribute or, if not already present, adds a new attribute.
   *
   * @param attribute The attribute to replace
   * @param value     The value to add
   */
  public void replace(String attribute, int value) {
    this.remove(attribute);
    this.add(attribute, value);
  }

  /**
   * <p/> Removes the specified attribute from the parameters. </p>
   *
   * @param attribute The attribute to remove
   */
  public void remove(String attribute) {
    this.parameters.remove(attribute);
  }

  /**
   * Build the query string of a URL
   *
   * @param attributeDelimiter A delimiter, usually either ampersand or <code>&amp;</code>.
   * @return a query string
   */
  public String toStringForm(String attributeDelimiter) {
    StringBuilder builder = new StringBuilder();
    boolean needAmp = false;
    try {
      for (String k : parameters.keySet()) {
        Set<String> values = parameters.get(k);
        if (values == null) {
          // This is a valueless attribute; simply
          // add the attribute without values
          if (needAmp) {
            builder.append(attributeDelimiter);
          }
          builder.append(k);
          needAmp = true;
        } else {
          // This attribute has at least one value;
          // add each of them to the list
          for (String v : values) {
            if (needAmp) {
              builder.append(attributeDelimiter);
            }
            builder.append(k);
            builder.append("=");
            if (StringUtils.isNotEmpty(v)) {
              builder.append(URLEncoder.encode(v, "UTF-8"));
            }
            needAmp = true;
          }
        }
      }
    } catch (UnsupportedEncodingException uee) {
      // This will never happen
    }
    return builder.toString();
  }
}
