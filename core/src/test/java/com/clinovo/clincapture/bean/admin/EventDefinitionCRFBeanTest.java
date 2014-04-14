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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EventDefinitionCRFBeanTest {
	private EventDefinitionCRFBean edc = null;

	@Before
	public void setUp() throws Exception {
		edc = new EventDefinitionCRFBean();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAllDefaultValues() {
		assertEquals(0, edc.getStudyEventDefinitionId());
		assertEquals(false, edc.isHideCrf());
		assertEquals(false, edc.isHidden());
		assertEquals(0, edc.getStudyId());
		assertEquals(0, edc.getCrfId());
		assertEquals(true, edc.isRequiredCRF());
		assertEquals(false, edc.isDoubleEntry());
		assertEquals(false, edc.isElectronicSignature());
		assertEquals(false, edc.isRequireAllTextFilled());
		assertEquals(true, edc.isDecisionCondition());
		assertEquals("", edc.getSelectedVersionIds());
		assertEquals("", edc.getEmailStep());
		assertEquals("", edc.getEmailTo());

		assertNotNull(edc.getOwnerId());
		assertNotNull(edc.getUpdaterId());
		assertNotNull(edc.getCreatedDate());
		assertNotNull(edc.getUpdatedDate());

		assertNotNull(edc.getId());
		assertNotNull(edc.getName());

		assertNull(edc.getEventName());
		assertNull(edc.getSourceDataVerification());

		assertEquals(edc, new EventDefinitionCRFBean());
	}

	@Test
	public void testInequalityOfClasses() {
		edc.setCrfId(1);
		edc.setDefaultVersionId(1);
	
		assertNotSame(edc, new EventDefinitionCRFBean());
		assertFalse(edc.equals(new EventDefinitionCRFBean()));
		assertFalse(edc.getDefaultVersionId() == new EventDefinitionCRFBean().getDefaultVersionId());
	}
}
