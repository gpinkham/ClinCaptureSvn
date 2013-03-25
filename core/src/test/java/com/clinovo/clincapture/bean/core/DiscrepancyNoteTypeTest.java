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

import org.akaza.openclinica.bean.core.DatasetItemStatus;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: Pavel Date: 13.10.12
 */
public class DiscrepancyNoteTypeTest {

	private DiscrepancyNoteType noteType;
	private ResourceBundle resterm;

	private static final String INVALID_KEY = "Invalid"; // 0

	private static final String FAILEDVAL_KEY = "Failed_Validation_Check";

	private static final String ANNOTATION_KEY = "Annotation";

	private static final String QUERY_KEY = "query";

	private static final String REASON_FOR_CHANGE_KEY = "reason_for_change";

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.getDefault());
		resterm = ResourceBundleProvider.getTermsBundle();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInvalid() {
		noteType = DiscrepancyNoteType.get(0);
		assertNull(noteType);
		noteType = DiscrepancyNoteType.INVALID;
		assertEquals(0, noteType.getId());
		assertEquals(resterm.getString(INVALID_KEY), noteType.getName());
		assertEquals(null, noteType.getDescription());
	}

	@Test
	public void testFailedValidationCheck() {
		noteType = DiscrepancyNoteType.get(1);
		assertEquals(1, noteType.getId());
		assertEquals(DiscrepancyNoteType.FAILEDVAL, noteType);
		assertEquals(resterm.getString(FAILEDVAL_KEY), noteType.getName());
		assertEquals(null, noteType.getDescription());
	}

	@Test
	public void testAnnotation() {
		noteType = DiscrepancyNoteType.get(2);
		assertEquals(2, noteType.getId());
		assertEquals(DiscrepancyNoteType.ANNOTATION, noteType);
		assertEquals(resterm.getString(ANNOTATION_KEY), noteType.getName());
		assertEquals(null, noteType.getDescription());
	}

	@Test
	public void testQuery() {
		noteType = DiscrepancyNoteType.get(3);
		assertEquals(3, noteType.getId());
		assertEquals(DiscrepancyNoteType.QUERY, noteType);
		assertEquals(resterm.getString(QUERY_KEY), noteType.getName());
		assertEquals(null, noteType.getDescription());
	}

	@Test
	public void testReasonForChange() {
		noteType = DiscrepancyNoteType.get(4);
		assertEquals(4, noteType.getId());
		assertEquals(DiscrepancyNoteType.REASON_FOR_CHANGE, noteType);
		assertEquals(resterm.getString(REASON_FOR_CHANGE_KEY), noteType.getName());
		assertEquals(null, noteType.getDescription());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testContent() {
		List<DiscrepancyNoteType> noteTypeList = DiscrepancyNoteType.list;
		assertEquals(4, noteTypeList.size());
		assertTrue(noteTypeList.contains(DiscrepancyNoteType.FAILEDVAL));
		assertTrue(noteTypeList.contains(DiscrepancyNoteType.ANNOTATION));
		assertTrue(noteTypeList.contains(DiscrepancyNoteType.QUERY));
		assertTrue(noteTypeList.contains(DiscrepancyNoteType.REASON_FOR_CHANGE));
	}

	@Test
	public void testGet() {
		assertNull(DatasetItemStatus.get(-1));
		assertNull(DatasetItemStatus.get(100));
	}
}
