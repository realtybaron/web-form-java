package com.socotech.wf4j;

/**
 *
 */
public class FormValidatorUtil {
    /**
     * Encapsulates the redundancy of invoking a form validator.  Handles nested properties.
     *
     * @param validator a form validator
     * @param o         a form
     * @param errors    validation errors
     * @param field     a simple or nested property
     */
    public static void invokeValidator(FormValidator validator, Object o, FormErrors errors, String field) {
        String[] nested = field.split("\\.");
        try {
            for (String nest : nested) {
                errors.push(nest);
            }
            validator.validate(o, errors);
        } finally {
            for (String s : nested) {
                errors.pop();
            }
        }
    }
}
