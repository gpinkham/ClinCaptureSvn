package com.clinovo.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RegexpUtilTest {

	@Test
	public void testThatParseGroupParsesCorrectValue() throws Exception {
		assertTrue(RegexpUtil.parseGroup("CM001_TXT_INT1(#1)", "(\\(#\\d*\\))", 1).equals("(#1)"));
	}

	@Test
	public void testThatParseGroupDoesNotThrowAnException() throws Exception {
		assertTrue(RegexpUtil.parseGroup("CM001_TXT_INT1", "(\\(#\\d*\\))", 1).equals("CM001_TXT_INT1"));
	}
}
