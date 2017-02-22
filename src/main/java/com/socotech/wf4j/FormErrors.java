/**
 * FormErrors
 */
package com.socotech.wf4j;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>The FormErrors is a very simple object used to add error messages to wf4j pages.  It's basically just a map of strings to human-readable error descriptions.</p> <p/> <p>Right
 * now these are all English language.</p> <p/> <p>Clients can iterate over the errors, if they're not sure what might have happened; or, if there is a well-known name, they can
 * look for it directly.  For example, the <code>FormErrors</code> for the login page has an error "password" if the password is wrong.</p>
 */
public class FormErrors extends AbstractMap<String, String> {

    /**
     * Push a nested property onto the property stack
     *
     * @param property deeper property
     */
    public void push(String property) {
        this.nesting.push(property);
    }

    /**
     * Pop a nested property off the property stack
     *
     * @return the property which is popped off the stack
     */
    public String pop() {
        return this.nesting.pop();
    }

    /**
     * <p>Sets the human-readable error description for the given code. Neither the code nor the description should be null.</p>
     *
     * @param code        The error code
     * @param description The human-readable description
     */
    @Override
    public String put(String code, String description) {
        Validate.notNull(code, "Can't put an error with a null code");
        Validate.notNull(description, "Can't put an error with a null description");
        if (this.nesting.isEmpty()) {
            this.errors.put(code, description);
        } else {
            List<String> path = new ArrayList<String>(this.nesting);
            path.add(code);
            this.errors.put(StringUtils.join(path.iterator(), '.'), description);
        }
        return description;
    }

    /**
     * <p>Given an error code, returns the description for that code. If there is no such error with that code, then null is returned.</p>
     *
     * @param code The error code
     * @return the description for that error
     */
    @Override
    public String get(Object code) {
        return this.errors.get(code);
    }

    /**
     * <p>Returns true if the specified error code has been set (that is, if calling <code>get</code> on it will return some string.</p>
     *
     * @param code The error code
     * @return true if it's in there, false otherwise
     */
    public boolean isSet(String code) {
        return this.errors.containsKey(code);
    }

    /**
     * Return true if any of the codes are set
     *
     * @param codes one or more codes
     * @return true, if any set
     */
    public boolean isSet(String... codes) {
        for (String code : codes) {
            if (this.isSet(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>Returns true if the specified error code has NOT been set (that is, if calling <code>get</code> on it will return null.</p>
     *
     * @param code The error code
     * @return true if it's absent, false otherwise
     */
    public boolean isNotSet(String code) {
        return !this.isSet(code);
    }

    /**
     * Returns true of none of the codes are set
     *
     * @param codes one or more codes
     * @return true, if none set
     */
    public boolean isNotSet(String... codes) {
        return !this.isSet(codes);
    }

    /**
     * This method brings FormErrors in compliance with the Map interface
     *
     * @return set of map entries
     */
    @Override
    public Set<Entry<String, String>> entrySet() {
        return this.errors.entrySet();
    }

    /**
     * <p>Returns an unmodifiable set of all the codes in this error packet.</p>
     *
     * @return The codes in an unmodifiable set
     */
    public Set<String> getCodes() {
        return Collections.unmodifiableSet(this.errors.keySet());
    }

    /**
     * <p>Returns an unmodifiable set of all the descriptions in this error packet.</p>
     *
     * @return The descriptions in an unmodifiable collection
     */
    @SuppressWarnings("unchecked")
    public Collection getDescriptions() {
        return Collections.unmodifiableCollection(this.errors.values());
    }

    /**
     * <p>Returns the string form of this error packet -- really only useful for logging situations.</p>
     *
     * @return The string form of the error packet
     */
    @Override
    public String toString() {

        // Simple count of the errors
        StringBuilder rv = new StringBuilder();
        rv.append(this.size());
        rv.append(" error");
        if (this.size() != 1) {
            rv.append("s");
        }

        // Build up a list of the errors, separated by semis, and append
        if (this.size() > 0) {
            rv.append(" ");
            rv.append(this.errors.toString());
        }

        return rv.toString();
    }

    /**
     * Returns the JSON form of this error packet -- really only useful for asynchronous actions
     *
     * @return The JSON form of this error packet
     * @throws JSONException if json representation cannot be built
     */
    public JSONArray toJSON() throws JSONException {
        JSONArray errors = new JSONArray();
        for (Entry<String, String> entry : this.errors.entrySet()) {
            JSONObject error = new JSONObject();
            error.put("key", entry.getKey());
            error.put("value", entry.getValue());
            errors.put(error);
        }
        return errors;
    }


    /**
     * <p>Helper function that makes it easy to check to see if a parameter is in a request.  If the parameter's value is set, (that is, if it is not the empty string or null) then
     * this method returns the value and leaves this FormErrors alone.  If the parameter's value is not set, however, then this method returns null and an appropriate error
     * message is added to this packet.</P>
     *
     * @param request   The HTTP request being checked
     * @param parameter The required parameter (like "password" or "screenname")
     * @param message   The message to display if the value isn't there
     * @return The parameter's value or null if it wasn't there
     */
    public String checkRequiredValue(HttpServletRequest request, String parameter, String message) {
        String value = request.getParameter(parameter);
        value = StringUtils.stripToEmpty(value);
        if ((value == null) || (value.length() == 0)) {
            this.put(parameter, message);
            return null;
        }
        return value;
    }


    /**
     * The path to the current property
     */
    private Stack<String> nesting = new Stack<>();
    /**
     * <p>A map of error codes to error descriptions.</p>
     */
    private Map<String, String> errors = new TreeMap<String, String>();
}
