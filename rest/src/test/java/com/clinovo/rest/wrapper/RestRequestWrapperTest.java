package com.clinovo.rest.wrapper;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class RestRequestWrapperTest {

	public static final String TEST_PARAM = "testParam";
	public static final String TEST_PATAM_VALUE = "test patam value!!!";

	private MockHttpServletRequest request;

	private RestRequestWrapper restRequestWrapper;

	@Before
	public void before() throws Exception {
		request = new MockHttpServletRequest();
		restRequestWrapper = new RestRequestWrapper(request);
	}

	@Test
	public void testThatAddsParameterWorksFine() throws Exception {
		restRequestWrapper.addParameter(TEST_PARAM, TEST_PATAM_VALUE);
		assertEquals(restRequestWrapper.getParameter(TEST_PARAM), TEST_PATAM_VALUE);
	}
}
