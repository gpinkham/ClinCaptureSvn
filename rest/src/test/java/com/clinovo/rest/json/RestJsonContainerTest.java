package com.clinovo.rest.json;

import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * RestJsonContainerTest.
 */
public class RestJsonContainerTest {

	@Test
	public void testThatDefaultConstructorSetsCorrectValuesForFields() throws Exception {
		RestJsonContainer restJsonContainer = new RestJsonContainer();
		assertNull(restJsonContainer.getServer());
		assertNull(restJsonContainer.getObject());
	}
}
