package org.akaza.openclinica.control.extract;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertTrue;

public class AccessFileServletTest {

	public static final String URL = "http://www.site.com/clincapture/AccessFile?fid=123";

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private AccessFileServlet accessFileServlet;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		accessFileServlet = Mockito.mock(AccessFileServlet.class);
		request.getSession().setAttribute("redirectAfterLogin", URL);
		Mockito.doCallRealMethod().when(accessFileServlet).processRequest(request, response);
	}

	@Test
	public void testThatProcessRequestDoesRedirectToMainMenuIfAttributeRedirectAfterLoginIsPresent() throws Exception {
		accessFileServlet.processRequest(request, response);
		assertTrue(response.getHeader("Location").equals("/MainMenu"));
	}
}
