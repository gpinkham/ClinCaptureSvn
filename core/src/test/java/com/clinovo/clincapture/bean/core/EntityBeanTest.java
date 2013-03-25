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

import org.akaza.openclinica.bean.core.EntityBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Pavel Date: 12.10.12
 */

public class EntityBeanTest {

	private EntityBean entity;

	@Before
	public void setUp() throws Exception {
		entity = new EntityBean();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDefault() {
		assertEquals(0, entity.getId());
		assertEquals("", entity.getName());
		assertEquals(false, entity.isActive());
	}

	@Test
	public void testActivatedBySettingId() {
		EntityBean customEntity = new EntityBean();
		assertEquals(false, customEntity.isActive());
		customEntity.setId(1);
		assertEquals(true, customEntity.isActive());
	}

	@Test
	public void testNotActivatedBySettingId() {
		EntityBean customEntity = new EntityBean();
		assertEquals(false, customEntity.isActive());
		customEntity.setId(-1);
		assertEquals(false, customEntity.isActive());
	}

	@Test
	public void testEqual() {
		EntityBean defEntity = new EntityBean();
		assertEquals(defEntity, entity);
	}

	@Test
	public void testEqualNameIsNull() {
		EntityBean defEntity = new EntityBean();
		defEntity.setName(null);
		String entityName = entity.getName();
		entity.setName(null);
		assertEquals(defEntity, entity);
		entity.setName(entityName);
	}

	@Test
	public void testNotEqualId() {
		EntityBean customEntity = new EntityBean();
		customEntity.setId(1);
		assertNotSame(customEntity, entity);
	}

	@Test
	public void testNotEqualName() {
		EntityBean customEntity = new EntityBean();
		customEntity.setName("name");
		assertNotSame(customEntity, entity);
	}

	@Test
	public void testNotEqualActive() {
		EntityBean customEntity = new EntityBean();
		customEntity.setActive(!entity.isActive());
		assertNotSame(customEntity, entity);
	}

	@Test
	public void testNotEqualWrongClass() {
		assertNotSame(entity, "");
	}

	@Test
	public void testNotEqualNull() {
		assertNotSame(entity, null);
	}

}
