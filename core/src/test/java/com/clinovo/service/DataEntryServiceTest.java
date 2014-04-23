/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.service;

import java.util.ArrayList;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.view.Page;
import org.junit.Test;
import org.mockito.Mockito;

@SuppressWarnings("unchecked")
public class DataEntryServiceTest extends DefaultAppContextTest {
	@Test
	public void testGetAllDisplayBeansReturnsNotNull() throws Exception {
		SectionDAO sdao = new SectionDAO(getDataSource());
		ArrayList<SectionBean> allSectionBeans = sdao.findAllByCRFVersionId(1);
		EventCRFBean ecb = (EventCRFBean) eventCRFDAO.findByPK(1);
		StudyBean study = (StudyBean) studyDAO.findByPK(1);
		assertNotNull(dataEntryService.getAllDisplayBeans(allSectionBeans, ecb, study, Page.INITIAL_DATA_ENTRY_SERVLET));
	}
	
	@Test
	public void testGetAllDisplayBeansReturnsResult() throws Exception {
		ArrayList<SectionBean> allSectionBeans = sectionDAO.findAllByCRFVersionId(1);
		EventCRFBean ecb = (EventCRFBean) eventCRFDAO.findByPK(1);
		StudyBean study = (StudyBean) studyDAO.findByPK(1);
		assertEquals(3, (dataEntryService.getAllDisplayBeans(allSectionBeans, ecb, study, Page.INITIAL_DATA_ENTRY_SERVLET).size()));
	}
	
	@Test
	public void testShouldLoadDBValuesReturnsNotNull() {
		DisplayItemBean dib = new DisplayItemBean();
		assertNotNull(dataEntryService.shouldLoadDBValues(dib, Page.INITIAL_DATA_ENTRY_SERVLET));
	}
	
	@Test
	public void testShouldLoadDBValuesReturnsTrue() {
		DisplayItemBean dib = new DisplayItemBean();
		assertTrue(dataEntryService.shouldLoadDBValues(dib, Page.INITIAL_DATA_ENTRY_SERVLET));
	}
	
	@Test
	public void testGetDisplayBeanReturnsNotNull() throws Exception {
		boolean hasGroup = false;
		boolean includeUngroupedItems = true; 
		boolean isSubmitted = false;
		Page servletPage = Page.INITIAL_DATA_ENTRY_SERVLET;
		StudyBean study = (StudyBean) studyDAO.findByPK(1);
		EventCRFBean ecb = (EventCRFBean) eventCRFDAO.findByPK(1);
		SectionBean sb = new SectionBean(); 
		ArrayList<ItemBean> items = new ArrayList<ItemBean>();
		ItemBean ib = new ItemBean();
		ib.setId(1);
		items.add(ib);
		sb.setItems(items);
		EventDefinitionCRFBean edcb = new EventDefinitionCRFBean();
		int eventDefinitionCRFId = 1;
		SessionManager sm = Mockito.mock(SessionManager.class);
		Mockito.when(sm.getDataSource()).thenReturn(dataSource);
		DisplaySectionBean dsb = dataEntryService.getDisplayBean(hasGroup, includeUngroupedItems, isSubmitted, servletPage,
				study, ecb, sb, edcb, eventDefinitionCRFId, sm);
		assertNotNull(dsb);
	}
}
