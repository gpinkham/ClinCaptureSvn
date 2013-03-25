/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.bean.admin;

import static org.junit.Assert.*;

import org.akaza.openclinica.bean.admin.AuditBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AuditBeanTest {

	private AuditBean audit = null;

	@Before
	public void setUp() throws Exception {
		audit = new AuditBean();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAllDefaultValues() {
		assertNull(audit.getAuditDate());
		assertNull(audit.getAuditTable());
		// assert null fails on two lines below - why?
		assertNotNull(audit.getEntityId());
		assertNotNull(audit.getEventCRFId());

		assertNotNull(audit.getId());
		assertNotNull(audit.getName());
		// note to self - probably b/c they are ints.
		assertNotNull(audit.getItemDataTypeId());
		assertNull(audit.getNewValue());
		assertNull(audit.getOldValue());
		assertNotNull(audit.getOrdinal());

		assertNull(audit.getReasonForChange());
		assertNotNull(audit.getStudyEventId());
		assertNotNull(audit.getUserId());
		assertNull(audit.getUserName());

	}

	@Test
	public void testInequalityOfClasses() {
		audit.setName("New Audit Bean");
		audit.setOldValue("old");
		audit.setNewValue("");
		// without this line below, the assertFalse fails
		// there are only a few fields that get checked for equality in AuditBean
		audit.setAuditTable("item_data");

		assertNotSame(audit, new AuditBean());
		assertFalse(audit.equals(new AuditBean()));
		assertFalse(audit.getName().equals(new AuditBean().getName()));
	}

}
