/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.bean.core;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings({ "rawtypes" })
public class RoleTest {

	public static final int EIGHT = 8;
	public static final int TEN = 10;
	public static final int THREE = 3;
	public static final int SIX = 6;
	private Map role = null;
	private Map roleWithDescription = null;
	private ResourceBundle resTerm = null;

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		resTerm = ResourceBundleProvider.getTermsBundle();
		role = Role.ROLE_MAP;
		roleWithDescription = Role.ROLE_MAP_WITH_DESCRIPTION;
	}

	@Test
	public void testThatRoleMapContainsValidNumberOfRoles() {
		assertEquals(TEN, role.size());
	}

	@Test
	public void testThatRoleMapWithDescriptionContainsValidNumberOfRoles() {
		assertEquals(TEN, roleWithDescription.size());
	}

	@Test
	public void testThatPrepareRoleMapWithDescriptionContainsValidRoleNames() {
		Role.prepareRoleMapWithDescriptions(resTerm);
		assertEquals("Study Monitor", roleWithDescription.get(Role.STUDY_MONITOR.getId()));
		assertEquals("Study Administrator", roleWithDescription.get(Role.STUDY_ADMINISTRATOR.getId()));
		assertEquals("Study Evaluator", roleWithDescription.get(Role.STUDY_EVALUATOR.getId()));
	}

	@Test
	public void testThatContainsMethodWorksAsExpected() {
		assertEquals(true, Role.contains(EIGHT));
	}

	@Test
	public void testThatGetMethodReturnsCorrectBean() {
		assertEquals(THREE, Role.get(THREE).getId());
		assertEquals(SIX, Role.get(SIX).getId());
	}

	@Test
	public void testThatGetByNameReturnsCorrectRole() {
		assertEquals(Role.STUDY_CODER.getName(), Role.getByName("study_coder").getName());
		assertEquals(Role.INVALID.getName(), Role.getByName("unknown").getName());
		assertEquals(Role.STUDY_SPONSOR.getName(), Role.getByName("study_sponsor").getName());
	}

	@Test
	public void testThatMaxMethodPicksTheHigherRole() {
		Role role = Role.max(Role.STUDY_ADMINISTRATOR, Role.STUDY_EVALUATOR);
		assertEquals(Role.STUDY_ADMINISTRATOR, role);
	}

	@Test
	public void testThatSystemAdminRoleGetsCorrectly() {
		assertEquals(Role.SYSTEM_ADMINISTRATOR, Role.getByName("system administrator"));
	}
	
	@Test
	public void testThatRoleIsMonitorReturnsTrueForStudyMonitor() {
		assertTrue(Role.isMonitor(Role.STUDY_MONITOR));
	}
	
	@Test
	public void testThatRoleIsMonitorReturnsTrueForSiteMonitor() {
		assertTrue(Role.isMonitor(Role.SITE_MONITOR));
	}
	
	@Test
	public void testThatRoleIsMonitorReturnsFalseForNonMonitor() {
		assertFalse(Role.isMonitor(Role.STUDY_CODER));
	}
}
