/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.bean.core;

import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResponseTypeTest {

	public static final String INVALID_KEY = "invalid";
	public static final String TEXT_KEY = "text";
	public static final String TEXTAREA_KEY = "textarea";
	public static final String CHECKBOX_KEY = "checkbox";
	public static final String FILE_KEY = "file";
	public static final String RADIO_KEY = "radio";
	public static final String SELECT_KEY = "single-select";
	public static final String SELECTMULTI_KEY = "multi-select";
	public static final String CALCULATION_KEY = "calculation";
	public static final String GROUP_CALCULATION_KEY = "group-calculation";

	private static final String WRONG_KEY = "Monitor";

	ResponseType responseType;
	ResourceBundle resterm;

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		resterm = ResourceBundleProvider.getTermsBundle();
	}

	@Test
	public void testInvalid() {
		responseType = ResponseType.INVALID;
		assertEquals(0, responseType.getId());
		assertEquals(resterm.getString(INVALID_KEY), responseType.getName());
	}

	@Test
	public void testText() {
		responseType = ResponseType.get(1);
		assertEquals(ResponseType.TEXT, responseType);
		assertEquals(resterm.getString(TEXT_KEY), responseType.getName());
		assertEquals(TEXT_KEY, responseType.getCode());
	}

	@Test
	public void testTextarea() {
		responseType = ResponseType.get(2);
		assertEquals(ResponseType.TEXTAREA, responseType);
		assertEquals(resterm.getString(TEXTAREA_KEY), responseType.getName());
		assertEquals(TEXTAREA_KEY, responseType.getCode());
	}

	@Test
	public void testCheckbox() {
		responseType = ResponseType.get(3);
		assertEquals(ResponseType.CHECKBOX, responseType);
		assertEquals(resterm.getString(CHECKBOX_KEY), responseType.getName());
		assertEquals(CHECKBOX_KEY, responseType.getCode());
	}

	@Test
	public void testFile() {
		responseType = ResponseType.get(4);
		assertEquals(ResponseType.FILE, responseType);
		assertEquals(resterm.getString(FILE_KEY), responseType.getName());
		assertEquals(FILE_KEY, responseType.getCode());
	}

	@Test
	public void testRadio() {
		responseType = ResponseType.get(5);
		assertEquals(ResponseType.RADIO, responseType);
		assertEquals(resterm.getString(RADIO_KEY), responseType.getName());
		assertEquals(RADIO_KEY, responseType.getCode());
	}

	@Test
	public void testSelect() {
		responseType = ResponseType.get(6);
		assertEquals(ResponseType.SELECT, responseType);
		assertEquals(SELECT_KEY, responseType.getCode());
	}

	@Test
	public void testMultiSelect() {
		responseType = ResponseType.get(7);
		assertEquals(ResponseType.SELECTMULTI, responseType);
		assertEquals(resterm.getString(SELECTMULTI_KEY), responseType.getName());
		assertEquals(SELECTMULTI_KEY, responseType.getCode());
	}

	@Test
	public void testCalculation() {
		responseType = ResponseType.get(8);
		assertEquals(ResponseType.CALCULATION, responseType);
		assertEquals(CALCULATION_KEY, responseType.getCode());
	}

	@Test
	public void testGroupCalculation() {
		responseType = ResponseType.get(9);
		assertEquals(ResponseType.GROUP_CALCULATION, responseType);
		assertEquals(GROUP_CALCULATION_KEY, responseType.getCode());
	}

	@Test
	public void testGet() {
		assertEquals(ResponseType.TEXT, ResponseType.get(-1));
		assertEquals(ResponseType.TEXT, ResponseType.get(0));
		// updating thanks to changes in instant-calculations
		assertEquals(ResponseType.TEXT, ResponseType.get(16));
	}

	@Test
	public void testContains() {
		assertFalse(ResponseType.contains(-1));
		assertFalse(ResponseType.contains(0));
		assertTrue(ResponseType.contains(2));
		// updating thanks to changes in instant-calculations
		assertFalse(ResponseType.contains(16));
	}

	@Test
	public void testGetByName() {
		assertEquals(ResponseType.INVALID, ResponseType.getByName(resterm.getString(INVALID_KEY)));
		assertEquals(ResponseType.SELECT, ResponseType.getByName(SELECT_KEY));
		assertEquals(ResponseType.INVALID, ResponseType.getByName(resterm.getString(WRONG_KEY)));
	}
}
