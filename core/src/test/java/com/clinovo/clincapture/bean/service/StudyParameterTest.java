/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.bean.service;

import static org.junit.Assert.*;

import org.akaza.openclinica.bean.service.StudyParameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Pavel Date: 12.10.12
 */
public class StudyParameterTest {

	private StudyParameter param;

	@Before
	public void setUp() throws Exception {
		param = new StudyParameter();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStudyParameterDefaults() {
		assertEquals("", param.getHandle());
		assertEquals("", param.getName());
		assertEquals("", param.getDescription());
		assertEquals("", param.getDefaultValue());
		assertEquals(true, param.isInheritable());
		assertEquals(false, param.isOverridable());
	}

	@Test
	public void testEquals() {
		StudyParameter defParam = new StudyParameter();
		assertEquals(param, defParam);
	}

	@Test
	public void testEqualsDefaultValue() {
		StudyParameter defParam = new StudyParameter();
		defParam.setDefaultValue("defaultValue");
		assertEquals(param, defParam);
	}

	// TODO is comparison correct?
	@Test
	public void testEqualsHandle() {
		StudyParameter defParam = new StudyParameter();
		defParam.setHandle("handle");
		assertEquals(param, defParam);
	}

	// TODO is comparison correct?
	@Test
	public void testEqualsDescription() {
		StudyParameter defParam = new StudyParameter();
		defParam.setDescription("description");
		assertEquals(param, defParam);
	}

	// TODO is comparison correct?
	@Test
	public void testEqualsInheritable() {
		StudyParameter defParam = new StudyParameter();
		defParam.setInheritable(!param.isInheritable());
		assertEquals(param, defParam);
	}

	// TODO is comparison correct?
	@Test
	public void testEqualsOverridable() {
		StudyParameter defParam = new StudyParameter();
		defParam.setOverridable(!param.isOverridable());
		assertEquals(param, defParam);
	}

	@Test
	public void testNotEqualsName() {
		StudyParameter customParam = new StudyParameter();
		customParam.setName("customParam");
		assertNotSame(param, customParam);
	}

	@Test
	public void testNotEqualsActive() {
		StudyParameter customParam = new StudyParameter();
		customParam.setActive(!param.isActive());
		assertNotSame(param, customParam);
	}

	@Test
	public void testNotEqualsId() {
		StudyParameter customParam = new StudyParameter();
		customParam.setId(param.getId() + 1);
		assertNotSame(param, customParam);
	}

	@Test
	public void testNotEqualsNull() {
		assertNotSame(param, null);
	}
}
