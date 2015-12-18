package com.clinovo.rest.model;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResponseTest {

	@Test
	public void testThatDefaultConstructorSetsCorrectValuesForFields() throws Exception {
		Response response = new Response();
		assertNull(response.getStatus());
	}

	@Test
	public void testThatConstructorSetsCorrectValuesForFields() throws Exception {
		String status = "401";
		Response response = new Response(status);
		assertTrue(response.getStatus().equals(status));
	}
}
