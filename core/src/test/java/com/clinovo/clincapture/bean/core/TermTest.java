/*******************************************************************************
 * Copyright (C) 2009-2014 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.bean.core;

import org.akaza.openclinica.bean.core.Term;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TermTest {

	private Term term;

	public static final String TERM_BUNDLE_KEY = "delete";
	public static final String TERM_BUNDLE_VALUE = "delete";
	public static final String NORMALIZED_TERM_BUNDLE_KEY = "Delete";

	private final static List<Term> termList = Arrays.asList(new Term(1, ""), new Term(2, ""), new Term(3, ""),
			new Term(4, ""), new Term(6, ""), new Term(7, ""));

	@Before
	public void setUp() throws Exception {
		term = new Term();
		ResourceBundleProvider.updateLocale(Locale.getDefault());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDefault() {
		assertEquals(0, term.getId());
		assertEquals("", term.getName());
		assertNull(term.getDescription());
	}

	@Test
	public void testConstructor() {
		Term customTerm = new Term(1000, TERM_BUNDLE_KEY);
		assertEquals(1000, customTerm.getId());
		assertEquals(TERM_BUNDLE_VALUE, customTerm.getName());
		assertNull(customTerm.getDescription());
	}

	@Test
	public void testConstructorDescription() {
		Term customTerm = new Term(1000, TERM_BUNDLE_KEY, TERM_BUNDLE_KEY);
		assertEquals(1000, customTerm.getId());
		assertEquals(TERM_BUNDLE_VALUE, customTerm.getName());
		assertEquals(TERM_BUNDLE_VALUE, customTerm.getDescription());
	}

	@Test
	public void testContains() {
		assertTrue(Term.contains(1, termList));
		assertTrue(Term.contains(7, termList));
		assertFalse(Term.contains(-1, termList));
		assertFalse(Term.contains(5, termList));
	}

	@Test
	public void testGetById() {
		assertEquals(new Term(1, ""), Term.get(1, termList));
		assertEquals(term, Term.get(-1, termList));
	}

	@Test
	public void testEqualsDefault() {
		assertEquals(term, new Term());
	}

	@Test
	public void testEqualsId() {
		assertTrue((new Term(10, TERM_BUNDLE_KEY, TERM_BUNDLE_KEY)).equals(new Term(10, "", "")));
	}

	@Test
	public void testThatGetNormalizedNameReturnsCorrectValue() {
		assertEquals(new Term(10, TERM_BUNDLE_KEY, TERM_BUNDLE_KEY).getNormalizedName(), NORMALIZED_TERM_BUNDLE_KEY);
	}

	@Test
	public void testThatNormalizeStringReturnsCorrectValue() {
		assertEquals(Term.normalizeString(TERM_BUNDLE_KEY), NORMALIZED_TERM_BUNDLE_KEY);
	}
}
