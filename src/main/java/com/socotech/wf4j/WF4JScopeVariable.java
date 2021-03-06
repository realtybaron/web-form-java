package com.socotech.wf4j;

public enum WF4JScopeVariable {
    /**
     * A generic "name" attribute
     */
    name,
    /**
     * Error list
     */
    error_list,
    /**
     * List of user messages
     */
    message_list,
    /**
     * Depending on context, either the "current" or "target" page of a wizard form submission
     */
    page,
    date_end,
    date_start
}