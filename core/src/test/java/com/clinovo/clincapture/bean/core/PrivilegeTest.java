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

import org.akaza.openclinica.bean.core.Privilege;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PrivilegeTest {

	public static final String SYSTEM_ADMINISTRATOR_KEY = "system_administrator";
	public static final String STUDY_DIRECTOR_KEY = "study_director";
	public static final String INVESTIGATOR_KEY = "investigator";
	public static final String CLINICAL_RESEARCH_COORDINATOR_KEY = "clinical_research_coordinator";
	public static final String STUDY_MONITOR_KEY = "study_monitor";
	public static final String SITE_MONITOR_KEY = "site_monitor";
	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	public static final int EIGHT = 8;
	public static final int NINE = 9;

	private Privilege privilege;
	private ResourceBundle resterm;

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		resterm = ResourceBundleProvider.getTermsBundle();
	}

	@Test
	public void testAdmin() {
		privilege = Privilege.get(1);
		assertEquals(Privilege.SYSTEM_ADMINISTRATOR, privilege);
		assertEquals(resterm.getString(SYSTEM_ADMINISTRATOR_KEY), privilege.getName());
		assertNull(privilege.getDescription());
	}

	@Test
	public void testDirector() {
		privilege = Privilege.get(2);
		assertEquals(Privilege.STUDY_DIRECTOR, privilege);
		assertEquals(resterm.getString(STUDY_DIRECTOR_KEY), privilege.getName());
		assertNull(privilege.getDescription());
	}

	@Test
	public void testInvestigator() {
		privilege = Privilege.get(THREE);
		assertEquals(Privilege.INVESTIGATOR, privilege);
		assertEquals(resterm.getString(INVESTIGATOR_KEY), privilege.getName());
		assertNull(privilege.getDescription());
	}

	@Test
	public void testResearchAssistant() {
		privilege = Privilege.get(FOUR);
		assertEquals(Privilege.CLINICAL_RESEARCH_COORDINATOR, privilege);
		assertEquals(resterm.getString(CLINICAL_RESEARCH_COORDINATOR_KEY), privilege.getName());
		assertNull(privilege.getDescription());
	}

	@Test
	public void testMonitor() {
		privilege = Privilege.get(FIVE);
		assertEquals(Privilege.STUDY_MONITOR, privilege);
		assertEquals(resterm.getString(STUDY_MONITOR_KEY), privilege.getName());
		assertNull(privilege.getDescription());
	}

	@Test
	public void testGetSiteMonitorPrivilegeById() {
		privilege = Privilege.get(EIGHT);
		assertEquals(Privilege.SITE_MONITOR, privilege);
	}

	@Test
	public void testGet() {
		assertNull(Privilege.get(-1));
		assertNull(Privilege.get(0));
		assertNotNull(Privilege.get(2));
		assertNull(Privilege.get(NINE));
	}

	@Test
	public void testContains() {
		assertFalse(Privilege.contains(-1));
		assertFalse(Privilege.contains(0));
		assertTrue(Privilege.contains(2));
		assertFalse(Privilege.contains(NINE));
	}
}
