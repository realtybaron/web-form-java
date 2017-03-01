package com.socotech.wf4j;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * User: marc Date: Nov 18, 2008 Time: 8:41:49 PM
 */
public class StringArrayEditorTest extends TestCase {
    private CommaDelimitedStringArrayEditor editor = new CommaDelimitedStringArrayEditor();

    @Test
    public void testWhitespace() throws Exception {
        this.editor.setAsText(" foo, bar, choo");
        String[] array = (String[]) this.editor.getValue();
        Assert.assertEquals(array[0], "foo");
        Assert.assertEquals(array[1], "bar");
        Assert.assertEquals(array[2], "choo");
    }

    // need this for ant junit task

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StringArrayEditorTest.class);
    }
}