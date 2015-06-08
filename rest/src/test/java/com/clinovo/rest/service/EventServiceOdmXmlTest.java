package com.clinovo.rest.service;

import org.junit.Before;
import org.springframework.http.MediaType;

public class EventServiceOdmXmlTest extends EventServiceTest {

	@Before
	public void before() throws Exception {
		mediaType = MediaType.APPLICATION_XML;
		super.before();
	}
}
