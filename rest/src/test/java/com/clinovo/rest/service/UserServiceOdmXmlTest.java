package com.clinovo.rest.service;

import org.junit.After;
import org.junit.Before;
import org.springframework.http.MediaType;

public class UserServiceOdmXmlTest extends UserServiceTest {

	@Before
	public void before() throws Exception {
		mediaType = MediaType.APPLICATION_XML;
		super.before();
	}

	@After
	public void after() {
		super.after();
		if (result != null && mediaType == MediaType.APPLICATION_XML) {
			assertNotNull(restOdmContainer.getRestData().getUserAccountBean());
		}
	}
}
