package com.clinovo.rest.service;

import org.junit.Before;
import org.springframework.http.MediaType;

public class CrfServiceOdmXmlTest extends CrfServiceTest {

	@Before
	public void before() throws Exception {
		mediaType = MediaType.APPLICATION_XML;
		super.before();
	}
}
