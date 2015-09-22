package com.clinovo.rest.model;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class RestDataTest {

	@Test
	public void testThatDefaultConstructorSetsCorrectValuesForFields() throws Exception {
		RestData restData = new RestData();
		assertNull(restData.getError());
		assertNull(restData.getUserAccountBean());
		assertNull(restData.getUserDetails());
		assertNull(restData.getStudyEventDefinitionBean());
		assertNull(restData.getEventDefinitionCRFBean());
		assertNull(restData.getCrfVersionBean());
	}
}
