package com.socotech.wf4j;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Converts between a currency value's numeric and textual representation
 */
public class StringListEditor extends PropertyEditorSupport {
    public String getAsText() {
        List value = (List) getValue();
        return StringUtils.join(value.iterator(), ',');
    }

    public void setAsText(String text) throws IllegalArgumentException {
        setValue(Arrays.asList(StringUtils.split(text, ',')));
    }
}