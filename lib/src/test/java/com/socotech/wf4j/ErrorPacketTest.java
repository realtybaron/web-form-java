/**
 * ErrorPacketTest
 * (C) Copyright 2005-2007 iCopyright, Inc.  All rights reserved.
 */
package com.socotech.wf4j;

import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test the error packet
 */
public class ErrorPacketTest extends TestCase {

	/**
	 * Run these tests from the command line
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ErrorPacketTest.class);
	}

	/**
	 * Can we add items to the error packet?
	 */
	public void testPut() {
		ErrorPacket p = new ErrorPacket();
		p.put("object", "value");
		Assert.assertEquals(p.get("object"), "value");
		p.put("object", "new_value");
		Assert.assertEquals(p.get("object"), "new_value");
		try {
			p.put(null, "Hey");
			fail();
		} catch (IllegalArgumentException iae) {
			// pass
		}
		try {
			p.put("code", null);
			fail();
		} catch (IllegalArgumentException iae) {
			// pass
		}
	}

	/**
	 * Can we get the data out?
	 */
	public void testGet() {
		ErrorPacket p = new ErrorPacket();
		assertNull(p.get("code"));
		p.put("code", "value");
		Assert.assertEquals(p.get("code"), "value");
	}

	/**
	 * Test the "isSet" method
	 */
	public void testIsSet() {
		ErrorPacket p = new ErrorPacket();
		assertFalse(p.isSet("code"));
		p.put("code", "value");
		assertTrue(p.isSet("code"));
	}

	/**
	 * Test count
	 */
	public void testSize() {
		ErrorPacket p = new ErrorPacket();
		assertTrue(p.size() == 0);
		p.put("code", "value");
		assertTrue(p.size() == 1);
	}

	/**
	 * Test the get-codes method
	 */
	@SuppressWarnings("unchecked")
	public void testGetCodes() {
		ErrorPacket p = new ErrorPacket();
		p.put("one", "one");
		p.put("two", "two");
		p.put("three", "three");
		Set s = p.getCodes();
		assertTrue(s.size() == 3);
		assertTrue(s.contains("one"));
		assertTrue(s.contains("two"));
		assertTrue(s.contains("three"));
	}

	public void testNestedPath() {
		ErrorPacket p = new ErrorPacket();
		p.push("1");
		p.push("2");
		p.push("3");
		p.put("4", "four");
		Assert.assertEquals(p.get("1.2.3.4"), "four");
		p.pop();
		p.pop();
		p.pop();
		p.put("5", "five");
		Assert.assertEquals(p.get("5"), "five");
	}

	/**
	 * Test string method
	 */
	public void testToString() {
		ErrorPacket p = new ErrorPacket();
		Assert.assertEquals(p.toString(), "0 errors");
		p.put("one", "thing");
		Assert.assertEquals(p.toString(), "1 error {one=thing}");
		p.put("two", "things");
		String s = p.toString();
		assertEquals(s, "2 errors {one=thing, two=things}");
	}


}
