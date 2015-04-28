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

import java.util.ArrayList;
import java.util.Locale;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.clinovo.i18n.LocaleResolver;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ValidationTest {

	private boolean booleanValue = true;
	private int intValue = 7;
	private float floatValue = 2;
	private String stringValue = "test string";
	private Object objectValue = new Object();
	private ArrayList args = new ArrayList();
	private Validation validation = new Validation(Validator.IS_A_FLOAT);
	
	@Before
	public void setUp() {
		Locale locale = new Locale("en");

		MockHttpServletRequest request = new MockHttpServletRequest();
		LocaleResolver.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(LocaleResolver.getLocale(request));

		args.add(intValue);
		args.add(booleanValue);
		args.add(floatValue);
		args.add(stringValue);
		args.add(objectValue);
	}

	@Test
	public void testThatGetFloatMethodReturnsCorrectFloatValue() {
		validation.addArgument(floatValue);
		assertTrue(validation.getFloat(0) == floatValue);
	}

	@Test
	public void testThatGetBooleanMethodReturnsCorrectBooleanValue() {
		validation.addArgument(booleanValue);
		assertTrue(validation.getBoolean(0) == booleanValue);
	}

	@Test
	public void testThatGetIntMethodReturnsCorrectIntValue() {
		validation.addArgument(intValue);
		assertTrue(validation.getInt(0) == intValue);
	}

	@Test
	public void testThatGetStringMethodReturnsCorrectStringValue() {
		validation.addArgument(stringValue);
		assertTrue(validation.getString(0) == stringValue);
	}

	@Test
	public void testThatGetArgMethodReturnsCorrectObjectValue() {
		validation.addArgument(objectValue);
		assertTrue(validation.getArg(0) == objectValue);
	}

	@Test
	public void testThatGetTypeMethodReturnsTheSameTypeThatWasPassedInConstructor() {
		assertTrue(validation.getType() == Validator.IS_A_FLOAT);
	}

	@Test
	public void testThatSetArgumentsWorksCorrectly() {
		validation.setArguments(args);
		assertTrue(validation.getArguments().size() == 5);
		assertTrue(validation.getInt(0) == intValue);
		assertTrue(validation.getBoolean(1) == booleanValue);
		assertTrue(validation.getFloat(2) == floatValue);
		assertTrue(validation.getString(3).equals(stringValue));
		assertTrue(validation.getArg(4).equals(objectValue));
	}

}
