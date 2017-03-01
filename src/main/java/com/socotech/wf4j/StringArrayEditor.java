package com.socotech.wf4j;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA. User: marc Date: Dec 16, 2010 Time: 2:27:09 PM
 */
public class StringArrayEditor extends PropertyEditorSupport {
    private String splitter;

    public StringArrayEditor(String splitter) {
        this.splitter = splitter;
    }

    public String getAsText() {
        String[] value = (String[]) getValue();
        return StringUtils.join(value, this.splitter);
    }

    public void setAsText(String text) throws IllegalArgumentException {
        setValue(text.trim().split(this.splitter));
    }
}
