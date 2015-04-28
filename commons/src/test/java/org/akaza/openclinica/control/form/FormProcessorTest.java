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
package org.akaza.openclinica.control.form;

import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.clinovo.i18n.LocaleResolver;

public class FormProcessorTest {

	private FormProcessor formProcessor;

	@Before
	public void setUp() {
		Locale locale = new Locale("en");

		MockHttpServletRequest request = new MockHttpServletRequest();
		LocaleResolver.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(LocaleResolver.getLocale(request));

		request.setParameter("field1", "1234");
		request.setParameter("field2", "1234.0");
		request.setParameter("field3", "1");

		formProcessor = new FormProcessor(request);
	}

	@Test
	public void testThatGetFloatMethodReturnsCorrectValue() {
		assertTrue(formProcessor.getFloat("field2") == 1234.0);
	}

	@Test
	public void testThatGetBooleanMethodReturnsCorrectValue() {
		assertTrue(formProcessor.getBoolean("field3"));
	}

	@Test
	public void testThatGetIntMethodReturnsCorrectValue() {
		assertTrue(formProcessor.getInt("field1") == 1234);
	}
}
