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
	private Stack<String> visitedURLs;

	@Before
	public void setUp() throws Exception {
		session = new MockHttpSession();
		request = Mockito.mock(MockHttpServletRequest.class);	
		Mockito.when(request.getSession()).thenReturn(session);
	}

	@Test
	public void testThatNavigationUtilSetsRedirectAfterLoginAttributeInTheSession() {
		configureRequest("AccessFile", "fid=123");
		Navigation.addToNavigationStack(request);
		assertTrue(session.getAttribute("redirectAfterLogin") != null);
	}
	
	@Test
	public void testThatURLsFromSpecialURLsAreBiengProcessedCorrectly() {
		configureRequest("EnterDataForStudyEvent");
		Navigation.addToNavigationStack(request);
		assertEquals(1, visitedURLs.size());
		assertTrue(visitedURLs.get(0).equals("/MainMenu"));
	}
	
	@Test
	public void testThatURLsFromexclusionPopUpURLsAreBiengProcessedCorrectly() {
		configureRequest("UploadFile");
		Navigation.addToNavigationStack(request);
		assertEquals(1, visitedURLs.size());
		assertTrue(visitedURLs.get(0).equals("/MainMenu"));
		
	}
	
	private void configureRequest(String url) {
		visitedURLs = new Stack<String>();
		session.setAttribute("visitedURLs", visitedURLs);
		configureRequest(url, "eventId=5&openFirstCrf=true");
	}
	
	private void configureRequest(String url, String query) {
		Mockito.when(request.getQueryString()).thenReturn(query);
		Mockito.when(request.getContextPath()).thenReturn("/clincapture");
		Mockito.when(request.getRequestURI()).thenReturn("/clincapture/" + url);
		Mockito.when(request.getRequestURL())
				.thenReturn(new StringBuffer("http://www.site.com/clincapture/" + url));
	}
}
