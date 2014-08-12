package org.akaza.openclinica.navigation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.Assert.assertTrue;

public class NavigationTest {

	private MockHttpSession session;
	private MockHttpServletRequest request;

	@Before
	public void setUp() throws Exception {
		session = new MockHttpSession();
		request = Mockito.mock(MockHttpServletRequest.class);
		Mockito.when(request.getQueryString()).thenReturn("fid=123");
		Mockito.when(request.getContextPath()).thenReturn("/clincapture");
		Mockito.when(request.getRequestURI()).thenReturn("/clincapture/AccessFile");
		Mockito.when(request.getRequestURL())
				.thenReturn(new StringBuffer("http://www.site.com/clincapture/AccessFile"));
		Mockito.when(request.getSession()).thenReturn(session);

	}

	@Test
	public void testThatNavigationUtilSetsRedirectAfterLoginAttributeInTheSession() {
		Navigation.addToNavigationStack(request);
		assertTrue(session.getAttribute("redirectAfterLogin") != null);
	}
}
