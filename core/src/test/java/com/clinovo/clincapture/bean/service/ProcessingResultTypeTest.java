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

import org.akaza.openclinica.bean.service.ProcessingResultType;
import org.junit.Test;

/**
 * User: Pavel Date: 12.10.12
 */
public class ProcessingResultTypeTest {

	@Test
	public void testGetByCodeSuccessCode() {
		assertEquals(ProcessingResultType.SUCCESS, ProcessingResultType.getByCode(1));
	}

	@Test
	public void testGetByCodeFailCode() {
		assertEquals(ProcessingResultType.FAIL, ProcessingResultType.getByCode(2));
	}

	@Test
	public void testGetByCodeWarningCode() {
		assertEquals(ProcessingResultType.WARNING, ProcessingResultType.getByCode(3));
	}

	@Test
	public void testGetByCodeFail() {
		assertNotSame(ProcessingResultType.SUCCESS, ProcessingResultType.getByCode(2));
	}

	@Test
	public void testGetByCodeNull() {
		assertNull(ProcessingResultType.getByCode(1000));
	}

	@Test
	public void testGetByCodeSetCode() {
		ProcessingResultType.WARNING.setCode(1001);
		assertNull(ProcessingResultType.getByCode(3));
		assertEquals(ProcessingResultType.WARNING, ProcessingResultType.getByCode(1001));
		ProcessingResultType.WARNING.setCode(3);
	}
}
