/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.navigation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.Stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HelpNavigationServletTest {

	private MockHttpServletRequest request;
	MockHttpSession session;

	@Before
	public void setUp() throws Exception {
		Stack<String> visitedURLs = new Stack<String>();
		visitedURLs.push("/testURL1?abc=144&cde=53");
		visitedURLs.push("/testURL2?abc=12&cde=13");
		session = new MockHttpSession();
		session.setAttribute("visitedURLs", visitedURLs);
		request = Mockito.mock(MockHttpServletRequest.class);

		Mockito.when(request.getSession()).thenReturn(session);
		Mockito.when(request.getContextPath()).thenReturn("/ClinCapture-SNAPSHOT");
	}

	@Test
	public void testThatGetSavedUrlReturnsNotNull() {
		assertNotNull(HelpNavigationServlet.getSavedUrl(request));
	}

	@Test
	public void testThatGetSavedUrlReturnsCorrectURL() {
		assertEquals(HelpNavigationServlet.getSavedUrl(request), "/ClinCapture-SNAPSHOT/testURL1?abc=144&cde=53");
	}

	@Test
	public void testThatGetSavedUrlReturnsDefaultURLIfStackContainsOneElement() {
		Stack<String> visitedURLs = new Stack<String>();
		visitedURLs.push("/testURL2");
		session.setAttribute("visitedURLs", visitedURLs);
		assertEquals(HelpNavigationServlet.getSavedUrl(request), "/ClinCapture-SNAPSHOT/MainMenu");
	}
}
