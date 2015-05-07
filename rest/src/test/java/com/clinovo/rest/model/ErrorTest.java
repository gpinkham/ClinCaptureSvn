package com.clinovo.rest.model;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ErrorTest {

	@Test
	public void testThatDefaultConstructorSetsCorrectValuesForFields() throws Exception {
		Error error = new Error();
		assertTrue(error.getCode() == 0);
		assertNull(error.getMessage());
	}

	@Test
	public void testThatConstructorSetsCorrectValuesForFields() throws Exception {
		int code = 401;
		String message = "No study found for specified study name";
		Error error = new Error(new Exception(message), code);
		assertTrue(error.getCode() == code);
		assertTrue(error.getMessage().equals(message));
	}
}
