package com.clinovo.rest.model;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ServerTest {

	@Test
	public void testThatDefaultConstructorSetsCorrectValuesForFields() throws Exception {
		Server server = new Server();
		assertNull(server.getVersion());
	}
}
