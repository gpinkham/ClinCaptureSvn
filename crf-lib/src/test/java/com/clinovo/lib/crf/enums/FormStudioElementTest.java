package com.clinovo.lib.crf.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FormStudioElementTest {

	@Test
	public void testThatFindByNameReturnsFalseIfNameIsIncorrect() {
		assertFalse(FormStudioElement.findByName("wrongName"));
	}

	@Test
	public void testThatFindByNameReturnsTrueIfNameIsCorrect() {
		assertTrue(FormStudioElement.findByName("NUMBER"));
	}

	@Test
	public void testThatGetByNameReturnsTextEnumByDefaultIfNameIsIncorrect() {
		assertEquals(FormStudioElement.getByName("wrongName"), FormStudioElement.TEXT);
	}

	@Test
	public void testThatGetByNameReturnsAppropriateEnumIfNameIsCorrect() {
		assertEquals(FormStudioElement.getByName("NUMBER"), FormStudioElement.NUMBER);
	}
}
