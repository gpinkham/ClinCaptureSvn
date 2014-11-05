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

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * User: Pavel Date: 13.10.12
 */
public class DataEntryStageTest {

	private DataEntryStage dataEntryStage;
	private ResourceBundle resterm;

	private static final String INVALID_KEY = "invalid";

	private static final String UNCOMPLETED_KEY = "not_started";

	private static final String INITIAL_DATA_ENTRY_KEY = "initial_data_entry";
	private static final String INITIAL_DATA_ENTRY_DESC_KEY = "data_being_entered";

	private static final String INITIAL_DATA_ENTRY_COMPLETE_KEY = "initial_data_entry_complete";
	private static final String INITIAL_DATA_ENTRY_COMPLETE_DESC_KEY = "initial_data_entry_completed";

	private static final String DOUBLE_DATA_ENTRY_KEY = "double_data_entry";
	private static final String DOUBLE_DATA_ENTRY_DESC_KEY = "being_validated";

	private static final String DOUBLE_DATA_ENTRY_COMPLETE_KEY = "data_entry_complete";
	private static final String DOUBLE_DATA_ENTRY_COMPLETE_DESC_KEY = "validation_completed";

	private static final String ADMINISTRATIVE_EDITING_KEY = "administrative_editing";
	private static final String ADMINISTRATIVE_EDITING_DESC_KEY = "completed";

	private static final String LOCKED_KEY = "locked";

	private static final String WRONG_KEY = "Monitor";

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		resterm = ResourceBundleProvider.getTermsBundle();
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testStageInvalid() {
		dataEntryStage = DataEntryStage.get(0);
		assertNull(dataEntryStage);
		dataEntryStage = DataEntryStage.INVALID;
		assertEquals(0, dataEntryStage.getId());
		assertEquals(DataEntryStage.INVALID, dataEntryStage);
		assertTrue(dataEntryStage.isInvalid());
		assertEquals(resterm.getString(INVALID_KEY), dataEntryStage.getName());
		assertNull(dataEntryStage.getDescription());
		assertEquals(INVALID_KEY, dataEntryStage.getNameRaw());
	}

	@Test
	public void testStageNotStarted() {
		dataEntryStage = DataEntryStage.get(1);
		assertEquals(1, dataEntryStage.getId());
		assertEquals(DataEntryStage.UNCOMPLETED, dataEntryStage);
		assertTrue(dataEntryStage.isUncompleted());
		assertEquals(resterm.getString(UNCOMPLETED_KEY), dataEntryStage.getName());
		assertEquals(resterm.getString(UNCOMPLETED_KEY), dataEntryStage.getDescription());
		assertEquals(UNCOMPLETED_KEY, dataEntryStage.getNameRaw());
	}

	@Test
	public void testStageInitialDataEntry() {
		dataEntryStage = DataEntryStage.get(2);
		assertEquals(2, dataEntryStage.getId());
		assertEquals(DataEntryStage.INITIAL_DATA_ENTRY, dataEntryStage);
		assertTrue(dataEntryStage.isInitialDE());
		assertEquals(resterm.getString(INITIAL_DATA_ENTRY_KEY), dataEntryStage.getName());
		assertEquals(resterm.getString(INITIAL_DATA_ENTRY_DESC_KEY), dataEntryStage.getDescription());
		assertEquals(INITIAL_DATA_ENTRY_KEY, dataEntryStage.getNameRaw());
	}

	@Test
	public void testStageInitialDataEntryComplete() {
		dataEntryStage = DataEntryStage.get(3);
		assertEquals(3, dataEntryStage.getId());
		assertEquals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE, dataEntryStage);
		assertTrue(dataEntryStage.isInitialDE_Complete());
		assertEquals(resterm.getString(INITIAL_DATA_ENTRY_COMPLETE_KEY), dataEntryStage.getName());
		assertEquals(resterm.getString(INITIAL_DATA_ENTRY_COMPLETE_DESC_KEY), dataEntryStage.getDescription());
		assertEquals(INITIAL_DATA_ENTRY_COMPLETE_KEY, dataEntryStage.getNameRaw());
	}

	@Test
	public void testStageDoubleDataEntry() {
		dataEntryStage = DataEntryStage.get(4);
		assertEquals(4, dataEntryStage.getId());
		assertEquals(DataEntryStage.DOUBLE_DATA_ENTRY, dataEntryStage);
		assertTrue(dataEntryStage.isDoubleDE());
		assertEquals(resterm.getString(DOUBLE_DATA_ENTRY_KEY), dataEntryStage.getName());
		assertEquals(resterm.getString(DOUBLE_DATA_ENTRY_DESC_KEY), dataEntryStage.getDescription());
		assertEquals(DOUBLE_DATA_ENTRY_KEY, dataEntryStage.getNameRaw());
	}

	@Test
	public void testStageDoubleDataEntryComplete() {
		dataEntryStage = DataEntryStage.get(5);
		assertEquals(5, dataEntryStage.getId());
		assertEquals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE, dataEntryStage);
		assertTrue(dataEntryStage.isDoubleDE_Complete());
		assertEquals(resterm.getString(DOUBLE_DATA_ENTRY_COMPLETE_KEY), dataEntryStage.getName());
		assertEquals(resterm.getString(DOUBLE_DATA_ENTRY_COMPLETE_DESC_KEY), dataEntryStage.getDescription());
		assertEquals(DOUBLE_DATA_ENTRY_COMPLETE_KEY, dataEntryStage.getNameRaw());
	}

	@Test
	public void testStageAdminEditing() {
		dataEntryStage = DataEntryStage.get(6);
		assertEquals(6, dataEntryStage.getId());
		assertEquals(DataEntryStage.ADMINISTRATIVE_EDITING, dataEntryStage);
		assertTrue(dataEntryStage.isAdmin_Editing());
		assertEquals(resterm.getString(ADMINISTRATIVE_EDITING_KEY), dataEntryStage.getName());
		assertEquals(resterm.getString(ADMINISTRATIVE_EDITING_DESC_KEY), dataEntryStage.getDescription());
		assertEquals(ADMINISTRATIVE_EDITING_KEY, dataEntryStage.getNameRaw());
	}

	@Test
	public void testStageLocked() {
		dataEntryStage = DataEntryStage.get(7);
		assertEquals(7, dataEntryStage.getId());
		assertEquals(DataEntryStage.LOCKED, dataEntryStage);
		assertTrue(dataEntryStage.isLocked());
		assertEquals(resterm.getString(LOCKED_KEY), dataEntryStage.getName());
		assertEquals(resterm.getString(LOCKED_KEY), dataEntryStage.getDescription());
		assertEquals(LOCKED_KEY, dataEntryStage.getNameRaw());
	}

	@Test
	public void testGet() {
		assertNull(DataEntryStage.get(100));
		assertNull(DataEntryStage.get(-1));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetFromList() {
		assertNull(DataEntryStage.get(0, DataEntryStage.list));
		List<DataEntryStage> customStageList = new ArrayList<DataEntryStage>();
		customStageList.addAll(DataEntryStage.list);
		customStageList.add(DataEntryStage.INVALID);
		assertNotNull(DataEntryStage.get(0, customStageList));
	}

	@Test
	public void testGetByName() {
		assertEquals(DataEntryStage.INVALID, DataEntryStage.getByName(resterm.getString(INVALID_KEY)));
		assertEquals(DataEntryStage.INVALID, DataEntryStage.getByName(resterm.getString(WRONG_KEY)));
		assertEquals(DataEntryStage.INITIAL_DATA_ENTRY,
				DataEntryStage.getByName(resterm.getString(INITIAL_DATA_ENTRY_KEY)));
	}

}
