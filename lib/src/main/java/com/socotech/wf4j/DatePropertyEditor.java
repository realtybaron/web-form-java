package com.socotech.wf4j;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * DatePropertyEditor.java (C) Copyright 2005 iCopyright, Inc.  All rights reserved.
 */
public class DatePropertyEditor extends PropertyEditorSupport {
    private boolean allowEmpty;

    /**
     * Default constructor
     */
    public DatePropertyEditor() {
        this.allowEmpty = true;
    }

    /**
     * Constructor specifying whether empty is allowed
     *
     * @param allowEmpty if true, empty does not cause exception to be thrown
     */
    public DatePropertyEditor(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    public String getAsText() {
        return DateManager.dateToHumanString((Date) getValue());
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (this.allowEmpty && StringUtils.isEmpty(text)) {
            super.setValue(null);
        } else {
            try {
                super.setValue(DateManager.humanStringToDate(text));
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid format: " + text);
            }
        }
    }
}