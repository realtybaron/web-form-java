/**
 * UserScopeVariable.java
 */

package com.socotech.wf4j;

/**
 * Variables used in the user apps. Use these enumerated types instead of
 * hard-coded strings to avoid typing errors.
 */
public enum UserScopeVariable {
    choice,
    /**
     * The "next" choice on the licensing toolbar
     */
    next,
    /**
     * The "back" choice on the licensing toolbar
     */
    back,
    /**
     * The "jump" choice used to go to a specific page of a wizard page flow
     */
    jump,
    /**
     * The "cancel" option on the licensing toolbar
     */
    cancel,
    /**
     * Depending on context, either the "current" or "target" page of a wizard
     * form submission
     */
    @Deprecated
    page,
    /**
     * The "finish" option on a wizard form
     */
    finish;

    /**
     * Conveniently expose the enum's name as a bean-compliant getter
     *
     * @return enum name
     */
    public String getName() {
        return this.name();
    }
}
