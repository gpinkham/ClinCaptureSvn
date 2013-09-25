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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;

public class ItemDataTypeTest {

	public static final String INVALID_KEY = "invalid";
	public static final String INVALID_DESC_KEY = "Invalid_Type";

	public static final String BL_KEY = "bl";
	public static final String BL_DESC_KEY = "Boolean";

	public static final String BN_KEY = "bln";
	public static final String BN_DESC_KEY = "Boolean_Non_Null";

	public static final String ED_KEY = "ed";
	public static final String ED_DESC_KEY = "encapsulated_data";

	public static final String TEL_KEY = "tel";
	public static final String TEL_DESC_KEY = "URL";

	public static final String ST_KEY = "st";
	public static final String ST_DESC_KEY = "character_string";

	public static final String INTEGER_KEY = "int";
	public static final String INTEGER_DESC_KEY = "integer";

	public static final String REAL_KEY = "real";
	public static final String REAL_DESC_KEY = "floating";

	public static final String SET_KEY = "set";
	public static final String SET_DESC_KEY = "set";

	public static final String DATE_KEY = "date";
	public static final String DATE_DESC_KEY = "date";

	public static final String PDATE_KEY = "pdate";
	public static final String PDATE_DESC_KEY = "partial_date";

	public static final String FILE_KEY = "file";
	public static final String FILE_DESC_KEY = "file";

	private static final String WRONG_KEY = "Monitor";

	private ItemDataType itemDataType;
	private ResourceBundle resterm;

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.getDefault());
		resterm = ResourceBundleProvider.getTermsBundle();
	}

	@Test
	public void testInvalid() {
		assertNull(ItemDataType.get(0));
		itemDataType = ItemDataType.INVALID;
		assertEquals(itemDataType.getId(), 0);
		assertEquals(resterm.getString(INVALID_KEY), itemDataType.getName());
		assertEquals(resterm.getString(INVALID_DESC_KEY), itemDataType.getDescription());
	}

	@Test
	public void testBoolean() {
		itemDataType = ItemDataType.get(1);
		assertEquals(ItemDataType.BL, itemDataType);
		assertEquals(resterm.getString(BL_KEY), itemDataType.getName());
		assertEquals(resterm.getString(BL_DESC_KEY), itemDataType.getDescription());
	}

	@Test
	public void testBooleanNotNull() {
		itemDataType = ItemDataType.get(2);
		assertEquals(ItemDataType.BN, itemDataType);
		assertEquals(resterm.getString(BN_KEY), itemDataType.getName());
		assertEquals(resterm.getString(BN_DESC_KEY), itemDataType.getDescription());
	}

	@Test
	public void testEncapsulatedData() {
		itemDataType = ItemDataType.get(3);
		assertEquals(ItemDataType.ED, itemDataType);
		assertEquals(resterm.getString(ED_KEY), itemDataType.getName());
		assertEquals(resterm.getString(ED_DESC_KEY), itemDataType.getDescription());
	}

	@Test
	public void testURL() {
		itemDataType = ItemDataType.get(4);
		assertEquals(ItemDataType.TEL, itemDataType);
		assertEquals(resterm.getString(TEL_KEY), itemDataType.getName());
		assertEquals(resterm.getString(TEL_DESC_KEY), itemDataType.getDescription());
	}

	@Test
	public void testString() {
		itemDataType = ItemDataType.get(5);
		assertEquals(ItemDataType.ST, itemDataType);
		assertEquals(resterm.getString(ST_KEY), itemDataType.getName());
		assertEquals(resterm.getString(ST_DESC_KEY), itemDataType.getDescription());
	}

	@Test
	public void testInteger() {
		itemDataType = ItemDataType.get(6);
		assertEquals(ItemDataType.INTEGER, itemDataType);
		assertEquals(resterm.getString(INTEGER_KEY), itemDataType.getName());
		assertEquals(resterm.getString(INTEGER_DESC_KEY), itemDataType.getDescription());
	}

	@Test
	public void testReal() {
		itemDataType = ItemDataType.get(7);
		assertEquals(ItemDataType.REAL, itemDataType);
		assertEquals(resterm.getString(REAL_KEY), itemDataType.getName());
		assertEquals(resterm.getString(REAL_DESC_KEY), itemDataType.getDescription());
	}

	@Test
	public void testSet() {
		itemDataType = ItemDataType.get(8);
		assertEquals(ItemDataType.SET, itemDataType);
		assertEquals(resterm.getString(SET_KEY), itemDataType.getName());
		assertEquals(resterm.getString(SET_DESC_KEY), itemDataType.getDescription());
	}

	@Test
	public void testDate() {
		itemDataType = ItemDataType.get(9);
		assertEquals(ItemDataType.DATE, itemDataType);
		assertEquals(resterm.getString(DATE_KEY), itemDataType.getName());
		assertEquals(resterm.getString(DATE_DESC_KEY), itemDataType.getDescription());
	}

	@Test
	public void testPDate() {
		itemDataType = ItemDataType.get(10);
		assertEquals(ItemDataType.PDATE, itemDataType);
		assertEquals(resterm.getString(PDATE_KEY), itemDataType.getName());
		assertEquals(resterm.getString(PDATE_DESC_KEY), itemDataType.getDescription());
	}

	@Test
	public void testFile() {
		itemDataType = ItemDataType.get(11);
		assertEquals(ItemDataType.FILE, itemDataType);
		assertEquals(resterm.getString(FILE_KEY), itemDataType.getName());
		assertEquals(resterm.getString(FILE_DESC_KEY), itemDataType.getDescription());
	}

	@Test
	public void testContains() {
		assertFalse(ItemDataType.contains(-1));
		assertFalse(ItemDataType.contains(0));
		assertTrue(ItemDataType.contains(1));
		assertTrue(ItemDataType.contains(11));
		assertTrue(ItemDataType.contains(12));
	}

	@Test
	public void testGetByName() {
		assertEquals(ItemDataType.INVALID, ItemDataType.getByName(resterm.getString(INVALID_KEY)));
		assertEquals(ItemDataType.DATE, ItemDataType.getByName(resterm.getString(DATE_KEY)));
		assertEquals(ItemDataType.INVALID, ItemDataType.getByName(resterm.getString(WRONG_KEY)));
	}

	@Test
	public void testFindByName() {
		assertFalse(ItemDataType.findByName(resterm.getString(INVALID_KEY)));
		assertTrue(ItemDataType.findByName(resterm.getString(DATE_KEY)));
		assertFalse(ItemDataType.findByName(resterm.getString(WRONG_KEY)));
	}
}
