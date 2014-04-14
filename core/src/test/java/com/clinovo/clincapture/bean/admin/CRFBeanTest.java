/*******************************************************************************
 * Copyright (C) 2009-2014 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.bean.admin;

import static org.junit.Assert.*;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CRFBeanTest {
	private CRFBean crf = null;

	@Before
	public void setUp() throws Exception {
		crf = new CRFBean();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAllDefaultValues() {
		assertEquals(1, crf.getStatusId());
		assertEquals("", crf.getDescription());
		assertEquals(false, crf.isSelected());

		assertNotNull(crf.getVersions());
		assertNotNull(crf.getOidGenerator(null));

		// testing what is set up in the super class, auditable entity bean
		assertNotNull(crf.getOwnerId());
		assertNotNull(crf.getUpdaterId());
		assertNotNull(crf.getCreatedDate());
		assertNotNull(crf.getUpdatedDate());

		// there are still some values that get set to null by default.
		assertNull(crf.getOid());
		assertNull(crf.getOwner());
		assertNull(crf.getUpdater());

		// testing what is set up in the super class plain entity bean
		assertNotNull(crf.getId());
		assertNotNull(crf.getName());

		// finally testing equality of objects
		assertEquals(crf, new CRFBean());
	}

	@Test
	public void testInequalityOfClasses() {
		crf.setName("New CRF");
		crf.setDescription("New CRF Description");
		crf.setOid("F_0001");

		assertNotSame(crf, new CRFBean());
		assertFalse(crf.equals(new CRFBean()));
		assertFalse(crf.getName().equals(new CRFBean().getName()));
	}
}
