package com.socotech.wf4j;

/**
 * Converts between a currency value's numeric and textual representation
 */
public class CommaDelimitedStringArrayEditor extends StringArrayEditor {
    public CommaDelimitedStringArrayEditor() {
        super("\\s*,\\s*");
    }
}
