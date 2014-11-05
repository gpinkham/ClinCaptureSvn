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

import org.akaza.openclinica.bean.core.DatasetItemStatus;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: Pavel Date: 13.10.12
 */
public class DatasetItemStatusTest {

	private static final String COMPLETED_KEY = "completed";
	private static final String COMPLETED_DESC_KEY = "completed_items";

	private static final String NONCOMPLETED_DESC_KEY = "non_completed_items";

	private static final String COMPLETED_AND_NONCOMPLETED_DESC_KEY = "completed_and_non_completed_items";

	private DatasetItemStatus datasetItemStatus;
	private ResourceBundle resterm;

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		resterm = ResourceBundleProvider.getTermsBundle();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCompleted() {
		datasetItemStatus = DatasetItemStatus.get(1);
		assertEquals(1, datasetItemStatus.getId());
		assertEquals(DatasetItemStatus.COMPLETED, datasetItemStatus);
		assertEquals(DatasetItemStatus.getCOMPLETED(), datasetItemStatus);
		assertEquals(resterm.getString(COMPLETED_KEY), datasetItemStatus.getName());
		assertEquals(resterm.getString(COMPLETED_DESC_KEY), datasetItemStatus.getDescription());
	}

	@Test
	public void testNonCompleted() {
		datasetItemStatus = DatasetItemStatus.get(2);
		assertEquals(2, datasetItemStatus.getId());
		assertEquals(DatasetItemStatus.NONCOMPLETED, datasetItemStatus);
		assertEquals(DatasetItemStatus.getNONCOMPLETED(), datasetItemStatus);
		assertEquals("", datasetItemStatus.getName());
		assertEquals(resterm.getString(NONCOMPLETED_DESC_KEY), datasetItemStatus.getDescription());
	}

	@Test
	public void testCompletedNonCompleted() {
		datasetItemStatus = DatasetItemStatus.get(3);
		assertEquals(3, datasetItemStatus.getId());
		assertEquals(DatasetItemStatus.COMPLETED_AND_NONCOMPLETED, datasetItemStatus);
		assertEquals(DatasetItemStatus.getCOMPLETEDANDNONCOMPLETED(), datasetItemStatus);
		assertEquals("", datasetItemStatus.getName());
		assertEquals(resterm.getString(COMPLETED_AND_NONCOMPLETED_DESC_KEY), datasetItemStatus.getDescription());
	}

	@Test
	public void testGet() {
		assertNull(DatasetItemStatus.get(0));
		assertNull(DatasetItemStatus.get(-1));
		assertNull(DatasetItemStatus.get(100));
	}
}
