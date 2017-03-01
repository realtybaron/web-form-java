package com.socotech.wf4j;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * User: marc Date: Jul 6, 2008 Time: 10:11:40 AM
 */
public class PercentPropertyEditorTest extends TestCase {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testScale() throws Exception {
        PercentPropertyEditor editor = new PercentPropertyEditor(2, false);
        // scale of 2
        editor.setAsText("27.0358426%");
        assertEquals("27.04%", editor.getAsText());
        // scale of 4
        editor = new PercentPropertyEditor(4, false);
        editor.setAsText("27.0358426%");
        assertEquals("27.0358%", editor.getAsText());
    }

    @Test
    public void testEmptyNotAllowed() throws Exception {
        PercentPropertyEditor editor = new PercentPropertyEditor(2, false);
        try {
            editor.setAsText("");
            fail("Editor should not allow empty text");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    // need this for ant junit task
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PercentPropertyEditorTest.class);
    }
}