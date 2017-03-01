package com.socotech.wf4j;

/**
 * Converts between a currency value's numeric and textual representation
 */
public class SpaceDelimitedStringArrayEditor extends StringArrayEditor {
    public SpaceDelimitedStringArrayEditor() {
        super("\\s+");
    }
}
