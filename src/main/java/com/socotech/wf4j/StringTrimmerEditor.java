package com.socotech.wf4j;

import java.beans.PropertyEditorSupport;

/**
 * Trims whitespace from a String
 */
public class StringTrimmerEditor extends PropertyEditorSupport {
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null) {
            setValue(null);
        } else {
            setValue(text.trim());
        }
    }
}
