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

import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Pavel Date: 12.10.12
 */
public class StudyParameterValueBeanTest {

	private StudyParameterValueBean paramValue;

	@Before
	public void setUp() throws Exception {
		paramValue = new StudyParameterValueBean();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDefault() {
		assertEquals(0, paramValue.getStudyId());
		assertEquals("", paramValue.getParameter());
		assertEquals("", paramValue.getValue());
	}

	@Test
	public void testEqualsDefault() {
		StudyParameterValueBean defParamValue = new StudyParameterValueBean();
		assertEquals(paramValue, defParamValue);
	}

	@Test
	public void testEqualsStudyId() {
		StudyParameterValueBean customParamValue = new StudyParameterValueBean();
		customParamValue.setStudyId(1);
		assertEquals(paramValue, customParamValue);
	}

	@Test
	public void testEqualsParameter() {
		StudyParameterValueBean customParamValue = new StudyParameterValueBean();
		customParamValue.setParameter("param");
		assertEquals(paramValue, customParamValue);
	}

	@Test
	public void testEqualsValue() {
		StudyParameterValueBean customParamValue = new StudyParameterValueBean();
		customParamValue.setValue("value");
		assertEquals(paramValue, customParamValue);
	}

	@Test
	public void testNotEqualsNull() {
		assertNotSame(paramValue, null);
	}
}
