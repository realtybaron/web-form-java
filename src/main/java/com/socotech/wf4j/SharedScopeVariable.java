package com.socotech.wf4j;

/**
 * SharedScopeVariable.java
 */
public enum SharedScopeVariable {
    /**
     * A generic "name" attribute
     */
    name,
    /**
     * List of user messages
     */
    message_list,
    /**
     * Depending on context, either the "current" or "target" page of a wizard form submission
     */
    page,
    date_end,
    date_start,
    /**
     * The ID of the logged in member
     */
    logged_in_member_id;
}