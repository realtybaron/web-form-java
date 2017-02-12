package com.socotech.wf4j;

import java.util.regex.Pattern;

class Patterns {
    /**
     * <p/> Do not instantiate Pattern objects. </p>
     */
    private Patterns() {
        // noop
    }

    /**
     * A pattern for identifying the index of an array element
     */
    static final Pattern INDEX_REFERENCE = Pattern.compile("\\[\\d+\\]");
}
