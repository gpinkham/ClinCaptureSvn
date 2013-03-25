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

import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * User: Pavel Date: 13.10.12
 */
public class TriggerBeanTest {

	TriggerBean triggerBean;
	private static final String EMPTY = "";

	@Before
	public void setUp() throws Exception {
		triggerBean = new TriggerBean();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDefault() {
		assertNull(triggerBean.getPreviousDate());
		assertNull(triggerBean.getNextDate());
		assertNull(triggerBean.getFullName());
		assertNull(triggerBean.getGroupName());
		assertEquals(EMPTY, triggerBean.getDescription());
		assertEquals(new DatasetBean(), triggerBean.getDataset());
		assertEquals(new UserAccountBean(), triggerBean.getUserAccount());
		assertNull(triggerBean.getTab());
		assertNull(triggerBean.getCdisc());
		assertNull(triggerBean.getSpss());
		assertNull(triggerBean.getExportFormat());
		assertNull(triggerBean.getContactEmail());
		assertNull(triggerBean.getPeriodToRun());
		assertNull(triggerBean.getDatasetName());
		assertNull(triggerBean.getStudyName());
	}

	@Test
	public void testEqualsDefault() {
		assertEquals(new TriggerBean(), triggerBean);
	}

	// TODO is comparison correct?
	@Test
	public void testEqualsCustom() {
		TriggerBean customTrigger = new TriggerBean();
		customTrigger.setPreviousDate(new Date());
		customTrigger.setNextDate(new Date());
		customTrigger.setFullName("full name");
		customTrigger.setGroupName("group name");
		customTrigger.setDescription("description");
		customTrigger.setDataset(null);
		customTrigger.setUserAccount(null);
		customTrigger.setTab("tab");
		customTrigger.setCdisc("cdisc");
		customTrigger.setSpss("spss");
		customTrigger.setExportFormat("format");
		customTrigger.setContactEmail("email");
		customTrigger.setPeriodToRun("period");
		customTrigger.setDatasetName("dataset");
		customTrigger.setStudyName("study");

		assertEquals(customTrigger, triggerBean);
	}

}
