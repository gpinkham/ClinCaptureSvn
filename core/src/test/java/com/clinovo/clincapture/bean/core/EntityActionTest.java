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

import org.akaza.openclinica.bean.core.EntityAction;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: Pavel Date: 14.10.12
 */
public class EntityActionTest {

	private ResourceBundle resterm;
	private EntityAction entityAction;

	private static final String VIEW_KEY = "view";
	private static final String EDIT_KEY = "edit";
	private static final String DELETE_KEY = "delete";
	private static final String RESTORE_KEY = "restore";
	private static final String DEPLOY_KEY = "deploy";

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.getDefault());
		resterm = ResourceBundleProvider.getTermsBundle();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testView() {
		entityAction = EntityAction.get(1);
		assertEquals(1, entityAction.getId());
		assertEquals(resterm.getString(VIEW_KEY), entityAction.getName());
		assertNull(entityAction.getDescription());
	}

	@Test
	public void testEdit() {
		entityAction = EntityAction.get(2);
		assertEquals(2, entityAction.getId());
		assertEquals(resterm.getString(EDIT_KEY), entityAction.getName());
		assertNull(entityAction.getDescription());
	}

	@Test
	public void testDelete() {
		entityAction = EntityAction.get(3);
		assertEquals(3, entityAction.getId());
		assertEquals(resterm.getString(DELETE_KEY), entityAction.getName());
		assertNull(entityAction.getDescription());
	}

	@Test
	public void testRestore() {
		entityAction = EntityAction.get(4);
		assertEquals(4, entityAction.getId());
		assertEquals(resterm.getString(RESTORE_KEY), entityAction.getName());
		assertNull(entityAction.getDescription());
	}

	@Test
	public void testDeploy() {
		entityAction = EntityAction.get(5);
		assertEquals(5, entityAction.getId());
		assertEquals(resterm.getString(DEPLOY_KEY), entityAction.getName());
		assertNull(entityAction.getDescription());
	}

	@Test
	public void testGet() {
		assertNull(EntityAction.get(6));
		assertNull(EntityAction.get(-1));
		assertNull(EntityAction.get(100));
	}

	@Test
	public void testContains() {
		assertFalse(EntityAction.contains(0));
		assertTrue(EntityAction.contains(1));
		assertTrue(EntityAction.contains(5));
		assertFalse(EntityAction.contains(6));
		assertFalse(EntityAction.contains(100));
	}
}
