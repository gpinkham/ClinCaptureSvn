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

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: Pavel Date: 24.10.12
 */
public class NumericComparisonOperatorTest {

	public static final String EQUALS_KEY = "equal_to";
	public static final String NOT_EQUALS_KEY = "not_equal_to";
	public static final String LESS_THAN_KEY = "less_than";
	public static final String LESS_THAN_OR_EQUAL_TO_KEY = "less_than_or_equal_to";
	public static final String GREATER_THAN_KEY = "greater_than";
	public static final String GREATER_THAN_OR_EQUAL_TO_KEY = "greater_than_or_equal_to";

	private static final String WRONG_KEY = "Monitor";

	private NumericComparisonOperator comparisonOperator;
	private ResourceBundle resterm;

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.getDefault());
		resterm = ResourceBundleProvider.getTermsBundle();
	}

	@Test
	public void testEquals() {
		comparisonOperator = NumericComparisonOperator.get(1);
		assertEquals(NumericComparisonOperator.EQUALS, comparisonOperator);
		assertEquals(resterm.getString(EQUALS_KEY), comparisonOperator.getName());
		assertEquals(resterm.getString(EQUALS_KEY), comparisonOperator.getDescription());
	}

	@Test
	public void testNotEquals() {
		comparisonOperator = NumericComparisonOperator.get(2);
		assertEquals(NumericComparisonOperator.NOT_EQUALS, comparisonOperator);
		assertEquals(resterm.getString(NOT_EQUALS_KEY), comparisonOperator.getName());
		assertEquals(resterm.getString(NOT_EQUALS_KEY), comparisonOperator.getDescription());
	}

	@Test
	public void testLessThan() {
		comparisonOperator = NumericComparisonOperator.get(3);
		assertEquals(NumericComparisonOperator.LESS_THAN, comparisonOperator);
		assertEquals(resterm.getString(LESS_THAN_KEY), comparisonOperator.getName());
		assertEquals(resterm.getString(LESS_THAN_KEY), comparisonOperator.getDescription());
	}

	@Test
	public void testLessThanEquals() {
		comparisonOperator = NumericComparisonOperator.get(4);
		assertEquals(NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, comparisonOperator);
		assertEquals(resterm.getString(LESS_THAN_OR_EQUAL_TO_KEY), comparisonOperator.getName());
		assertEquals(resterm.getString(LESS_THAN_OR_EQUAL_TO_KEY), comparisonOperator.getDescription());
	}

	@Test
	public void testGreaterThan() {
		comparisonOperator = NumericComparisonOperator.get(5);
		assertEquals(NumericComparisonOperator.GREATER_THAN, comparisonOperator);
		assertEquals(resterm.getString(GREATER_THAN_KEY), comparisonOperator.getName());
		assertEquals(resterm.getString(GREATER_THAN_KEY), comparisonOperator.getDescription());
	}

	@Test
	public void testGreaterThanEquals() {
		comparisonOperator = NumericComparisonOperator.get(6);
		assertEquals(NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO, comparisonOperator);
		assertEquals(resterm.getString(GREATER_THAN_OR_EQUAL_TO_KEY), comparisonOperator.getName());
		assertEquals(resterm.getString(GREATER_THAN_OR_EQUAL_TO_KEY), comparisonOperator.getDescription());
	}

	@Test
	public void testGet() {
		assertNull(NumericComparisonOperator.get(-1));
		assertNull(NumericComparisonOperator.get(0));
		assertNotNull(NumericComparisonOperator.get(2));
		assertNull(NumericComparisonOperator.get(7));
	}

	@Test
	public void testContains() {
		assertFalse(NumericComparisonOperator.contains(-1));
		assertFalse(NumericComparisonOperator.contains(0));
		assertTrue(NumericComparisonOperator.contains(2));
		assertFalse(NumericComparisonOperator.contains(7));
	}
}
