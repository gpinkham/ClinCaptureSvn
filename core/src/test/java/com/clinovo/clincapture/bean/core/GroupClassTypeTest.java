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

import org.akaza.openclinica.bean.core.GroupClassType;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Pavel Date: 14.10.12
 */
public class GroupClassTypeTest {

	private GroupClassType groupClassType;
	private ResourceBundle resterm;

	private static final String INVALID_KEY = "invalid";
	private static final String ARM_KEY = "Arm";
	private static final String FAMILY_KEY = "Family/Pedigree";
	private static final String DEMOGRAPHIC_KEY = "Demographic";
	private static final String DYNAMIC_KEY = "Dynamic_Group";
	private static final String OTHER_KEY = "Other";
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
	public void testInvalid() {
		groupClassType = GroupClassType.INVALID;
		assertEquals(0, groupClassType.getId());
		assertEquals(resterm.getString(INVALID_KEY), groupClassType.getName());
	}

	@Test
	public void testArm() {
		groupClassType = GroupClassType.get(1);
		assertEquals(1, groupClassType.getId());
		assertEquals(groupClassType, GroupClassType.ARM);
		assertEquals(resterm.getString(ARM_KEY), groupClassType.getName());
	}

	@Test
	public void testFamily() {
		groupClassType = GroupClassType.get(2);
		assertEquals(2, groupClassType.getId());
		assertEquals(groupClassType, GroupClassType.FAMILY);
		assertEquals(resterm.getString(FAMILY_KEY), groupClassType.getName());
	}

	@Test
	public void testDemographic() {
		groupClassType = GroupClassType.get(3);
		assertEquals(3, groupClassType.getId());
		assertEquals(groupClassType, GroupClassType.DEMOGRAPHIC);
		assertEquals(resterm.getString(DEMOGRAPHIC_KEY), groupClassType.getName());
	}

	@Test
	public void testDynamic() {
		groupClassType = GroupClassType.get(4);
		assertEquals(4, groupClassType.getId());
		assertEquals(groupClassType, GroupClassType.DYNAMIC);
		assertEquals(resterm.getString(DYNAMIC_KEY), groupClassType.getName());
	}

	@Test
	public void testOther() {
		groupClassType = GroupClassType.get(5);
		assertEquals(5, groupClassType.getId());
		assertEquals(groupClassType, GroupClassType.OTHER);
		assertEquals(OTHER_KEY, groupClassType.getName());
	}

	@Test
	public void testContains() {
		assertFalse(GroupClassType.contains(0));
		assertTrue(GroupClassType.contains(1));
		assertTrue(GroupClassType.contains(4));
		assertFalse(GroupClassType.contains(6));
		assertFalse(GroupClassType.contains(100));
	}

	@Test
	public void testGet() {
		assertEquals(GroupClassType.INVALID, GroupClassType.get(6));
		assertEquals(GroupClassType.INVALID, GroupClassType.get(-1));
		assertEquals(GroupClassType.INVALID, GroupClassType.get(100));
	}

	@Test
	public void testGetInactive() {
		groupClassType = GroupClassType.get(5);
		assertEquals(groupClassType, GroupClassType.OTHER);
		assertTrue(groupClassType.isActive());

		groupClassType.setActive(false);
		assertEquals(GroupClassType.INVALID, GroupClassType.get(5));
		groupClassType.setActive(true);
	}

	@Test
	public void testFindByName() {
		assertFalse(GroupClassType.findByName(resterm.getString(INVALID_KEY)));
		assertTrue(GroupClassType.findByName(resterm.getString(ARM_KEY)));
		assertTrue(GroupClassType.findByName(resterm.getString(FAMILY_KEY)));
		assertTrue(GroupClassType.findByName(resterm.getString(DEMOGRAPHIC_KEY)));
		assertTrue(GroupClassType.findByName(resterm.getString(DYNAMIC_KEY)));
		assertTrue(GroupClassType.findByName(resterm.getString(OTHER_KEY)));
		assertFalse(GroupClassType.findByName(resterm.getString(WRONG_KEY)));
	}

	@Test
	public void testGetByName() {
		assertEquals(GroupClassType.getByName(resterm.getString(INVALID_KEY)), GroupClassType.INVALID);
		assertEquals(GroupClassType.getByName(resterm.getString(ARM_KEY)), GroupClassType.ARM);
		assertEquals(GroupClassType.getByName(resterm.getString(FAMILY_KEY)), GroupClassType.FAMILY);
		assertEquals(GroupClassType.getByName(resterm.getString(DEMOGRAPHIC_KEY)), GroupClassType.DEMOGRAPHIC);
		assertEquals(GroupClassType.getByName(resterm.getString(DYNAMIC_KEY)), GroupClassType.DYNAMIC);
		assertEquals(GroupClassType.getByName(resterm.getString(OTHER_KEY)), GroupClassType.OTHER);
		assertEquals(GroupClassType.getByName(resterm.getString(WRONG_KEY)), GroupClassType.INVALID);
	}

}
