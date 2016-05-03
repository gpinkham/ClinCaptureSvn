package com.clinovo.rest.model;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ErrorTest {

	@Test
	public void testThatDefaultConstructorSetsCorrectValuesForFields() throws Exception {
		Error error = new Error();
		assertNull(error.getStatus());
		assertNull(error.getMessage());
	}

	@Test
	public void testThatConstructorSetsCorrectValuesForFields() throws Exception {
		String status = "401";
		String message = "No study found for specified study name";
		Error error = new Error(new Exception(message), status, "");
		assertTrue(error.getStatus().equals(status));
		assertTrue(error.getMessage().equals(message));
	}
}
