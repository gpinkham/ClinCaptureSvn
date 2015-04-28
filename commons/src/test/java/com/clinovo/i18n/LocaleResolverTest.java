/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
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

/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 */
package com.clinovo.i18n;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.akaza.openclinica.dao.core.CoreResources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreResources.class, RequestContextHolder.class})
@SuppressWarnings("static-access")
public class LocaleResolverTest {

	private MockHttpSession session;

	private MockHttpServletRequest request;

	private LocaleResolver localeResolver;

	@Mock
	private ServletRequestAttributes servletRequestAttributes;

	@Before
	public void setUp() throws Exception {
		localeResolver = new LocaleResolver();
		request = new MockHttpServletRequest();
		session = new MockHttpSession();
		request.setSession(session);

		Locale locale = Locale.ENGLISH;
		PowerMockito.mockStatic(CoreResources.class);
		PowerMockito.when(CoreResources.getSystemLocale()).thenReturn(locale);

		PowerMockito.mockStatic(RequestContextHolder.class);
		Whitebox.setInternalState(servletRequestAttributes, "request", request);
		PowerMockito.when(RequestContextHolder.currentRequestAttributes()).thenReturn(servletRequestAttributes);
	}

	@Test
	public void testThatUpdateLocaleSetsNewLocaleForRequestCorrectly() {
		LocaleResolver.updateLocale(request, Locale.FRENCH);
		assertEquals(LocaleResolver.getLocale(), Locale.FRENCH);
	}

	@Test
	public void testThatUpdateLocaleSetsNewLocaleForSessionCorrectly() {
		LocaleResolver.updateLocale(request.getSession(), Locale.FRENCH);
		assertEquals(LocaleResolver.getLocale(request), Locale.FRENCH);
	}

	@Test
	public void testThatResolveLocaleResolvesLocaleCorrectly() {
		LocaleResolver.resolveLocale();
		assertEquals(LocaleResolver.getLocale(), Locale.ENGLISH);
	}
	
	@Test
	public void testThatResolveLocaleResolvesLocaleForRequestCorrectly() {
		localeResolver.resolveLocale(request);
		assertEquals(localeResolver.getLocale(request), Locale.ENGLISH);
	}

	@Test
	public void testThatResolveRestApiLocaleResolvesLocaleCorrectly() {
		LocaleResolver.resolveRestApiLocale();
		assertEquals(LocaleResolver.getLocale(), Locale.ENGLISH);
	}
}
