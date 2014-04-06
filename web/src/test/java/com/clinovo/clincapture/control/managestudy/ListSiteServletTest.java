/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package com.clinovo.clincapture.control.managestudy;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.control.managestudy.ListSiteServlet;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.bean.EntityBeanColumn;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.StudyRow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class ListSiteServletTest {

	HttpServletRequest request;

	@Before
	public void setUp() throws Exception {

		request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		ListSiteServlet listSiteServlet = Mockito.mock(ListSiteServlet.class);
		StudyDAO studyDAO = Mockito.mock(StudyDAO.class);

		StudyBean studyFromDb = new StudyBean();
		studyFromDb.setId(1);
		studyFromDb.setStatus(Status.AVAILABLE);

		List<StudyBean> studies = new ArrayList<StudyBean>();
		StudyBean site1 = new StudyBean();
		site1.setId(1);
		site1.setParentStudyId(1);
		site1.setIdentifier("identifier");
		site1.setOid("OID");
		site1.setPrincipalInvestigator("investigator");
		site1.setFacilityName("facility");
		site1.setCreatedDate(new Date());
		site1.setStatus(Status.AVAILABLE);
		studies.add(site1);

		HashMap<Integer, HashMap<String, Integer>> mapContainer = new HashMap<Integer, HashMap<String, Integer>>();
		HashMap<String, Integer> studyMap = new HashMap<String, Integer>();
		studyMap.put("countLockedEvents", 0);
		studyMap.put("countEvents", 0);
		mapContainer.put(1, studyMap);

		Mockito.doReturn(studyDAO).when((BaseController) listSiteServlet).getStudyDAO();
		Mockito.doReturn(studyFromDb).when((BaseController) listSiteServlet).getCurrentStudy(request);
		Mockito.doReturn(studies).when(studyDAO).findAllByParent(studyFromDb.getId());
		Mockito.doReturn(mapContainer).when(studyDAO).analyzeEvents(studies);
		Mockito.doCallRealMethod().when(listSiteServlet).processRequest(request, response);

		ResourceBundleProvider.updateLocale(Locale.getDefault());
		Whitebox.setInternalState(listSiteServlet, "resword", ResourceBundleProvider.getWordsBundle());

		listSiteServlet.processRequest(request, response);
	}

	@Test
	public void testThatListSiteServletContainsExpectedNumberOfRows() throws Exception {

		EntityBeanTable table = (EntityBeanTable) request.getAttribute("table");

		assertEquals(1, table.getRows().size());
	}

	@Test
	public void testThatListSiteServletContainsExpectedNumberOfColumns() throws Exception {

		EntityBeanTable table = (EntityBeanTable) request.getAttribute("table");

		assertEquals(8, table.getColumns().size());
	}

	@Test
	public void testThatListSiteServletContainsExpectedSearchStrings() throws Exception {

		EntityBeanTable table = (EntityBeanTable) request.getAttribute("table");
		StudyRow studyRow = (StudyRow) table.getRows().get(0);

		assertEquals(" identifier investigator facility", studyRow.getSearchString());
	}

	@Test
	public void testListSiteServletTableColumns() throws Exception {

		EntityBeanTable table = (EntityBeanTable) request.getAttribute("table");

		assertEquals("Name", ((EntityBeanColumn) table.getColumns().get(0)).getName());
		assertEquals("Unique Identifier", ((EntityBeanColumn) table.getColumns().get(1)).getName());
		assertEquals("OID", ((EntityBeanColumn) table.getColumns().get(2)).getName());
		assertEquals("Principal Investigator", ((EntityBeanColumn) table.getColumns().get(3)).getName());
		assertEquals("Facility Name", ((EntityBeanColumn) table.getColumns().get(4)).getName());
		assertEquals("Date Created", ((EntityBeanColumn) table.getColumns().get(5)).getName());
		assertEquals("Status", ((EntityBeanColumn) table.getColumns().get(6)).getName());
		assertEquals("Actions", ((EntityBeanColumn) table.getColumns().get(7)).getName());
	}
}
