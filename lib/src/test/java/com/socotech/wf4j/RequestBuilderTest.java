/**
 * RequestBuilderTest.java
 * (C) Copyright 2005 iCopyright, Inc.  All rights reserved.
 */
package com.socotech.wf4j;

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * Test the request builder object
 */
public class RequestBuilderTest extends TestCase {

    /**
     * Simple constructor test
     */
    public void testPageConstructor() {
        assertEquals(rb.toString(), "foo.jsp");
    }

    /**
     * Construct from an existing HTTP page
     */
    public void testPageAndParamsConstructor() {
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        String[] values = {"blue"};
        map.put("color", values);
        String[] values2 = {"tall"};
        map.put("height", values2);
        rb = new RequestBuilder("foo.jsp", map);
        assertEquals("foo.jsp?color=blue&height=tall", rb.toString());
    }

    /**
     * Make sure the values come out alphabetized
     */
    public void testAlphabetizing() {
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        String[] values = {"blahblah"};
        map.put("ru", values);
        String[] values2 = {"15"};
        map.put("sid", values2);

        rb = new RequestBuilder("foo.jsp", map);
        assertEquals("foo.jsp?ru=blahblah&sid=15", rb.toString());
    }

    /**
     * Test alphabetizing with alternate constructor path
     */
    public void testAlphabetizingOtherConstructor() {
        rb = new RequestBuilder("foo.jsp");
        rb.add("ru", "blahblah");
        rb.add("sid", "15");
        assertEquals("foo.jsp?ru=blahblah&sid=15", rb.toString());
    }

    /**
     * Set a var
     */
    public void testSet() {
        assertEquals(rb.toString(), "foo.jsp");
        rb.set("color", "blue");
        assertEquals(rb.toString(), "foo.jsp?color=blue");
        rb.set("color", "red");
        assertEquals(rb.toString(), "foo.jsp?color=red");
    }

    /**
     * Test basic gets
     */
    public void testSimpleGet() {
        rb = new RequestBuilder("foo.jsp");
        rb.add("ru", "blahblah");
        rb.add("sid", "15");
        assertEquals("foo.jsp?ru=blahblah&sid=15", rb.toString());
        assertEquals("15", rb.get("sid"));
        assertEquals("blahblah", rb.get("ru"));
        assertNull(rb.get("missing"));
    }

    /**
     * If there are multiple values, get returns nothing
     */
    public void testMultipleGet() {
        rb = new RequestBuilder("foo.jsp");
        rb.add("color", "red");
        rb.add("color", "blue");
        assertNull(rb.get("color"));
    }

    /**
     * Add a var
     */
    public void testAdd() {
        assertEquals(rb.toString(), "foo.jsp");
        rb.add("color", "blue");
        assertEquals(rb.toString(), "foo.jsp?color=blue");
        rb.add("color", "red");
        assertEquals(rb.toString(), "foo.jsp?color=blue&color=red");
    }

    /**
     * Add a var that needs encoding
     */
    public void testAddWithEncodingNeeded() {
        assertEquals(rb.toString(), "foo.jsp");
        rb.set("tag", "3.5403?icx_id=4");
        assertEquals(rb.toString(), "foo.jsp?tag=3.5403%3Ficx_id%3D4");
        rb.set("tag", "3.5403?color=blue&height=tall");
        assertEquals(rb.toString(), "foo.jsp?tag=3.5403%3Fcolor%3Dblue%26height%3Dtall");
    }

    /**
     * Make sure the toHrefs come outOK
     */
    public void testToHrefAdd() {
        assertEquals(rb.toHref(), "foo.jsp");
        rb.add("color", "blue");
        assertEquals(rb.toHref(), "foo.jsp?color=blue");
        rb.add("color", "red");
        assertEquals(rb.toHref(), "foo.jsp?color=blue&amp;color=red");
    }

    /**
     * Test the asAnchor methods
     */
    public void testAnchor() {
        assertEquals("<a href=\"foo.jsp\" target=\"_self\">clickme</a>", rb.toAnchorLink("clickme"));
        rb.add("color", "blue");
        assertEquals("<a href=\"foo.jsp?color=blue\" target=\"_self\">clickme</a>", rb.toAnchorLink("clickme"));
        rb.add("color", "red");
        assertEquals("<a href=\"foo.jsp?color=blue&amp;color=red\" target=\"_self\">clickme</a>", rb.toAnchorLink("clickme"));
    }

    /**
     * Test toHref when you need encoding
     */
    public void testToHrefAddWithEncodingNeeded() {
        assertEquals(rb.toHref(), "foo.jsp");
        rb.set("tag", "3.5403?icx_id=4");
        assertEquals(rb.toHref(), "foo.jsp?tag=3.5403%3Ficx_id%3D4");
        rb.set("tag", "3.5403?color=blue&height=tall");
        assertEquals(rb.toHref(), "foo.jsp?tag=3.5403%3Fcolor%3Dblue%26height%3Dtall");
        rb.add("color", "blue");
        assertEquals(rb.toHref(), "foo.jsp?color=blue&amp;tag=3.5403%3Fcolor%3Dblue%26height%3Dtall");
    }

    /**
     * Make sure we can remove an attribute
     */
    public void testRemove() {
        assertEquals(rb.toString(), "foo.jsp");
        rb.add("color", "blue");
        assertEquals(rb.toString(), "foo.jsp?color=blue");
        rb.remove("color");
        assertEquals(rb.toString(), "foo.jsp");
    }

    /**
     * Make sure we can set an attribute wihtout a value
     */
    public void testSetAttributeWithoutValue() {
        assertEquals(rb.toString(), "foo.jsp");
        assertEquals(rb.toHref(), "foo.jsp");
        rb.set("bar");
        assertEquals(rb.toString(), "foo.jsp?bar");
        assertEquals(rb.toHref(), "foo.jsp?bar");
        rb.add("color", "blue");
        assertEquals("foo.jsp?bar&color=blue", rb.toString());
        assertEquals("foo.jsp?bar&amp;color=blue", rb.toHref());
    }

    /**
     * RUN these tests from teh command line
     *
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(RequestBuilderTest.class);
    }

    /**
     * Set up these here tests Ernest
     */
    @Override
    protected void setUp() throws Exception {
        rb = new RequestBuilder("foo.jsp");
		super.setUp();
	}

	private RequestBuilder rb;
}
