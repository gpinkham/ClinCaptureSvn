package org.akaza.openclinica.navigation;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.Assert.assertEquals;
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
	
	@Test
	public void testThatURLsFromSpecialURLsAreBiengProcessedCorrectly() {
		Mockito.when(request.getQueryString()).thenReturn("eventId=5&openFirstCrf=true");
		Mockito.when(request.getContextPath()).thenReturn("/clincapture");
		Mockito.when(request.getRequestURI()).thenReturn("/clincapture/EnterDataForStudyEvent");
		Mockito.when(request.getRequestURL())
				.thenReturn(new StringBuffer("http://www.site.com/clincapture/EnterDataForStudyEvent"));
		Stack<String> visitedURLs = new Stack<String>();
		session.setAttribute("visitedURLs", visitedURLs);
		Navigation.addToNavigationStack(request);
		assertEquals(1, visitedURLs.size());
		assertTrue(visitedURLs.get(0).equals("/MainMenu"));
	}
}
