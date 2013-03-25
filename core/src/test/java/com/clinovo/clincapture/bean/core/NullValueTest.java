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

import static org.junit.Assert.*;

import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: Pavel Date: 24.10.12
 */
public class NullValueTest {

	public static final String INVALID_KEY = "invalid";
	public static final String INVALID_DESC_KEY = "invalid";

	public static final String NI_KEY = "NI";
	public static final String NI_DESC_KEY = "no_information";

	public static final String NA_KEY = "NA";
	public static final String NA_DESC_KEY = "not_applicable";

	public static final String UNK_KEY = "UNK";
	public static final String UNK_DESC_KEY = "unknown";

	public static final String NASK_KEY = "NASK";
	public static final String NASK_DESC_KEY = "not_asked";

	public static final String ASKU_KEY = "ASKU";
	public static final String ASKU_DESC_KEY = "asked_but_unknown";

	public static final String NAV_KEY = "NAV";
	public static final String NAV_DESC_KEY = "not_available";

	public static final String OTH_KEY = "OTH";
	public static final String OTH_DESC_KEY = "other";

	public static final String PINF_KEY = "PINF";
	public static final String PINF_DESC_KEY = "positive_infinity";

	public static final String NINF_KEY = "NINF";
	public static final String NINF_DESC_KEY = "negative_infinity";

	public static final String MSK_KEY = "MSK";
	public static final String MSK_DESC_KEY = "masked";

	public static final String NP_KEY = "NP";
	public static final String NP_DESC_KEY = "not_present";

	public static final String NPE_KEY = "NPE";
	public static final String NPE_DESC_KEY = "not_performed";

	private static final String WRONG_KEY = "Monitor";

	private NullValue nullValue;
	private ResourceBundle resterm;

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.getDefault());
		resterm = ResourceBundleProvider.getTermsBundle();
	}

	@Test
	public void testInvalid() {
		nullValue = NullValue.get(0);
		assertEquals(NullValue.INVALID, nullValue);
		assertEquals(INVALID_KEY, nullValue.getName());
		assertEquals(resterm.getString(INVALID_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testNoInformation() {
		nullValue = NullValue.get(1);
		assertEquals(NullValue.NI, nullValue);
		assertEquals(NI_KEY, nullValue.getName());
		assertEquals(resterm.getString(NI_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testNotApplicable() {
		nullValue = NullValue.get(2);
		assertEquals(NullValue.NA, nullValue);
		assertEquals(NA_KEY, nullValue.getName());
		assertEquals(resterm.getString(NA_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testUnknown() {
		nullValue = NullValue.get(3);
		assertEquals(NullValue.UNK, nullValue);
		assertEquals(UNK_KEY, nullValue.getName());
		assertEquals(resterm.getString(UNK_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testNotAsked() {
		nullValue = NullValue.get(4);
		assertEquals(NullValue.NASK, nullValue);
		assertEquals(NASK_KEY, nullValue.getName());
		assertEquals(resterm.getString(NASK_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testAskedButUnknown() {
		nullValue = NullValue.get(5);
		assertEquals(NullValue.ASKU, nullValue);
		assertEquals(ASKU_KEY, nullValue.getName());
		assertEquals(resterm.getString(ASKU_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testNotAvailable() {
		nullValue = NullValue.get(6);
		assertEquals(NullValue.NAV, nullValue);
		assertEquals(NAV_KEY, nullValue.getName());
		assertEquals(resterm.getString(NAV_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testOther() {
		nullValue = NullValue.get(7);
		assertEquals(NullValue.OTH, nullValue);
		assertEquals(OTH_KEY, nullValue.getName());
		assertEquals(resterm.getString(OTH_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testPositiveInfinity() {
		nullValue = NullValue.get(8);
		assertEquals(NullValue.PINF, nullValue);
		assertEquals(PINF_KEY, nullValue.getName());
		assertEquals(resterm.getString(PINF_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testNegativeInfinity() {
		nullValue = NullValue.get(9);
		assertEquals(NullValue.NINF, nullValue);
		assertEquals(NINF_KEY, nullValue.getName());
		assertEquals(resterm.getString(NINF_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testMasked() {
		nullValue = NullValue.get(10);
		assertEquals(NullValue.MSK, nullValue);
		assertEquals(MSK_KEY, nullValue.getName());
		assertEquals(resterm.getString(MSK_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testNotPresent() {
		nullValue = NullValue.get(11);
		assertEquals(NullValue.NP, nullValue);
		assertEquals(NP_KEY, nullValue.getName());
		assertEquals(resterm.getString(NP_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testNotPerformed() {
		nullValue = NullValue.get(12);
		assertEquals(NullValue.NPE, nullValue);
		assertEquals(NPE_KEY, nullValue.getName());
		assertEquals(resterm.getString(NPE_DESC_KEY), nullValue.getDescription());
	}

	@Test
	public void testContains() {
		assertFalse(NullValue.contains(-1));
		assertFalse(NullValue.contains(0));
		assertTrue(NullValue.contains(1));
		assertTrue(NullValue.contains(11));
		assertFalse(NullValue.contains(13));
	}

	@Test
	public void testGetByName() {
		assertEquals(NullValue.INVALID, NullValue.getByName(INVALID_KEY));
		assertEquals(NullValue.OTH, NullValue.getByName(OTH_KEY));
		assertEquals(NullValue.INVALID, NullValue.getByName(WRONG_KEY));
	}

	@Test
	public void testGetInactive() {
		nullValue = NullValue.get(1);
		assertEquals(NullValue.NI, nullValue);
		nullValue.setActive(false);
		assertEquals(NullValue.INVALID, NullValue.get(1));
		nullValue.setActive(true);
	}
}
