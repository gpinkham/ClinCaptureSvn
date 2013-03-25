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

import org.akaza.openclinica.bean.core.Privilege;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: Pavel Date: 24.10.12
 */
public class PrivilegeTest {

	public static final String ADMIN_KEY = "admin";
	public static final String STUDYDIRECTOR_KEY = "director";
	public static final String INVESTIGATOR_KEY = "investigator";
	public static final String RESEARCHASSISTANT_KEY = "ra";
	public static final String MONITOR_KEY = "monitor";

	private Privilege privilege;
	private ResourceBundle resterm;

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.getDefault());
		resterm = ResourceBundleProvider.getTermsBundle();
	}

	@Test
	public void testAdmin() {
		privilege = Privilege.get(1);
		assertEquals(Privilege.ADMIN, privilege);
		assertEquals(resterm.getString(ADMIN_KEY), privilege.getName());
		assertNull(privilege.getDescription());
	}

	@Test
	public void testDirector() {
		privilege = Privilege.get(2);
		assertEquals(Privilege.STUDYDIRECTOR, privilege);
		assertEquals(resterm.getString(STUDYDIRECTOR_KEY), privilege.getName());
		assertNull(privilege.getDescription());
	}

	@Test
	public void testInvestigator() {
		privilege = Privilege.get(3);
		assertEquals(Privilege.INVESTIGATOR, privilege);
		assertEquals(resterm.getString(INVESTIGATOR_KEY), privilege.getName());
		assertNull(privilege.getDescription());
	}

	@Test
	public void testResearchAssistant() {
		privilege = Privilege.get(4);
		assertEquals(Privilege.RESEARCHASSISTANT, privilege);
		assertEquals(resterm.getString(RESEARCHASSISTANT_KEY), privilege.getName());
		assertNull(privilege.getDescription());
	}

	@Test
	public void testMonitor() {
		privilege = Privilege.get(5);
		assertEquals(Privilege.MONITOR, privilege);
		assertEquals(resterm.getString(MONITOR_KEY), privilege.getName());
		assertNull(privilege.getDescription());
	}

	@Test
	public void testGet() {
		assertNull(Privilege.get(-1));
		assertNull(Privilege.get(0));
		assertNotNull(Privilege.get(2));
		assertNull(Privilege.get(7));
	}

	@Test
	public void testContains() {
		assertFalse(Privilege.contains(-1));
		assertFalse(Privilege.contains(0));
		assertTrue(Privilege.contains(2));
		assertFalse(Privilege.contains(7));
	}
}
