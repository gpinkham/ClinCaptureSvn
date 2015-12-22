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

package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;

import com.clinovo.i18n.LocaleResolver;

@SuppressWarnings({"unchecked"})
@RunWith(PowerMockRunner.class)
public class ChangeDefinitionCRFOrdinalServletTest {

	@Mock
	private EventDefinitionCRFDAO mockedEventDefinitionCRFDAO;

	@Mock
	private MockServletContext mockedServletContext;

	@Mock
	private MockRequestDispatcher mockedRequestDispatcher;

	private ChangeDefinitionCRFOrdinalServlet spiedChangeDefinitionCRFOrdinalServlet = Mockito
			.spy(new ChangeDefinitionCRFOrdinalServlet());

	private MockHttpServletResponse spiedResponse = Mockito.spy(new MockHttpServletResponse());

	private MockHttpServletRequest request;

	private List<EventDefinitionCRFBean> studyLevelEventDefCRFBeanList;

	private ArrayList<EventDefinitionCRFBean> siteLevelEventDefCRFBeanList;

	@Before
	public void beforeTestCaseRun() throws Exception {

		Locale locale = new Locale("en");

		// first obtaining all the required Resource Bundle instances for tests,
		// then stubbing all the static methods of the ResourceBundleProvider class
		ResourceBundleProvider.updateLocale(locale);

		request = new MockHttpServletRequest();
		LocaleResolver.updateLocale(request, locale);

		// setting up Servlet Context mock
		Mockito.when(mockedServletContext.getRequestDispatcher(Mockito.any(String.class)))
				.thenReturn(mockedRequestDispatcher);

		// setting up the test list of EventDefinitionCRFBeans of study level,
		// assigned to the same study event definition
		setupStudyLevelEventDefCRFBeanList();

		// setting up the test list of EventDefinitionCRFBeans of site level,
		// assigned to the same study event definition
		setupSiteLevelEventDefCRFBeanList();

		// current system user
		UserAccountBean currentUser = new UserAccountBean();
		currentUser.setId(1);

		// current study
		StudyBean currentStudy = new StudyBean();
		currentStudy.setId(1);

		// setting up DAO mocks
		Mockito.when(mockedEventDefinitionCRFDAO.findAllByDefinition(Mockito.any(StudyBean.class),
				Mockito.any(Integer.class))).thenReturn(studyLevelEventDefCRFBeanList);
		Mockito.when(mockedEventDefinitionCRFDAO.findAllChildrenByDefinition(Mockito.any(Integer.class)))
				.thenReturn(siteLevelEventDefCRFBeanList);

		// setting up spied ChangeDefinitionCRFOrdinalServlet
		Mockito.doReturn(mockedServletContext).when(spiedChangeDefinitionCRFOrdinalServlet).getServletContext();
		Mockito.doReturn(currentUser).when(spiedChangeDefinitionCRFOrdinalServlet).getUserAccountBean(request);
		Mockito.doReturn(currentStudy).when(spiedChangeDefinitionCRFOrdinalServlet).getCurrentStudy(request);
		Mockito.doReturn(mockedEventDefinitionCRFDAO).when(spiedChangeDefinitionCRFOrdinalServlet)
				.getEventDefinitionCRFDAO();
	}

	private void setupStudyLevelEventDefCRFBeanList() {

		studyLevelEventDefCRFBeanList = new ArrayList<EventDefinitionCRFBean>();
		EventDefinitionCRFBean eventDefCRFBean = new EventDefinitionCRFBean();
		eventDefCRFBean.setId(1);
		eventDefCRFBean.setStudyEventDefinitionId(1);
		eventDefCRFBean.setStudyId(1);
		eventDefCRFBean.setOrdinal(1);
		studyLevelEventDefCRFBeanList.add(eventDefCRFBean);
		eventDefCRFBean = new EventDefinitionCRFBean();
		eventDefCRFBean.setId(2);
		eventDefCRFBean.setStudyEventDefinitionId(1);
		eventDefCRFBean.setStudyId(1);
		eventDefCRFBean.setOrdinal(2);
		studyLevelEventDefCRFBeanList.add(eventDefCRFBean);
		eventDefCRFBean = new EventDefinitionCRFBean();
		eventDefCRFBean.setId(3);
		eventDefCRFBean.setStudyEventDefinitionId(1);
		eventDefCRFBean.setStudyId(1);
		eventDefCRFBean.setOrdinal(3);
		studyLevelEventDefCRFBeanList.add(eventDefCRFBean);
	}

	private void setupSiteLevelEventDefCRFBeanList() {

		siteLevelEventDefCRFBeanList = new ArrayList<EventDefinitionCRFBean>();
		EventDefinitionCRFBean eventDefCRFBean = new EventDefinitionCRFBean();
		eventDefCRFBean.setId(10);
		eventDefCRFBean.setStudyEventDefinitionId(1);
		eventDefCRFBean.setStudyId(2);
		eventDefCRFBean.setOrdinal(2);
		eventDefCRFBean.setParentId(1);
		siteLevelEventDefCRFBeanList.add(eventDefCRFBean);
		eventDefCRFBean = new EventDefinitionCRFBean();
		eventDefCRFBean.setId(11);
		eventDefCRFBean.setStudyEventDefinitionId(1);
		eventDefCRFBean.setStudyId(2);
		eventDefCRFBean.setOrdinal(2);
		eventDefCRFBean.setParentId(2);
		siteLevelEventDefCRFBeanList.add(eventDefCRFBean);
		eventDefCRFBean = new EventDefinitionCRFBean();
		eventDefCRFBean.setId(12);
		eventDefCRFBean.setStudyEventDefinitionId(1);
		eventDefCRFBean.setStudyId(2);
		eventDefCRFBean.setOrdinal(3);
		eventDefCRFBean.setParentId(3);
		siteLevelEventDefCRFBeanList.add(eventDefCRFBean);
	}

	@Test
	public void testChangeDefinitionCRFOrdinalServletBehaviorWhenRequestParameterEventCRFDefIdIsInvalid()
			throws Exception {

		// 1. SETTING UP TEST

		String action = "moveDown";
		request.setParameter("action", action);

		EventDefinitionCRFBean eventDefCRFBean = new EventDefinitionCRFBean();
		Mockito.when(mockedEventDefinitionCRFDAO.findByPK(Mockito.any(Integer.class))).thenReturn(eventDefCRFBean);

		// 2. TESTING BEHAVIOR

		spiedChangeDefinitionCRFOrdinalServlet.processRequest(request, spiedResponse);

		List<String> pageMessages = (List<String>) request.getAttribute("pageMessages");
		Assert.assertTrue(pageMessages
				.contains(ResourceBundleProvider.getPageMessagesBundle().getString("invalid_http_request_parameters")));

		Mockito.verify(mockedServletContext).getRequestDispatcher(Page.LIST_DEFINITION_SERVLET.getFileName());
		Mockito.verify(mockedRequestDispatcher).forward(request, spiedResponse);
	}

	@Test
	public void testChangeDefinitionCRFOrdinalServletBehaviorWhenRequestParameterActionIsInvalid() throws Exception {

		// 1. SETTING UP TEST

		String action = "someAction";
		request.setParameter("action", action);

		Mockito.when(mockedEventDefinitionCRFDAO.findByPK(Mockito.any(Integer.class)))
				.thenReturn(studyLevelEventDefCRFBeanList.get(1));

		// 2. TESTING BEHAVIOR

		spiedChangeDefinitionCRFOrdinalServlet.processRequest(request, spiedResponse);

		List<String> pageMessages = (List<String>) request.getAttribute("pageMessages");
		Assert.assertTrue(pageMessages
				.contains(ResourceBundleProvider.getPageMessagesBundle().getString("invalid_http_request_parameters")));

		Mockito.verify(mockedServletContext).getRequestDispatcher(Page.LIST_DEFINITION_SERVLET.getFileName());
		Mockito.verify(mockedRequestDispatcher).forward(request, spiedResponse);
	}

	@Test
	public void testChangeDefinitionCRFOrdinalServletBehaviorOnActionMoveDown() throws Exception {

		// 1. SETTING UP TEST

		String action = "moveDown";
		request.setParameter("action", action);

		Mockito.when(mockedEventDefinitionCRFDAO.findByPK(Mockito.any(Integer.class)))
				.thenReturn(studyLevelEventDefCRFBeanList.get(1));

		// 2. TESTING BEHAVIOR

		spiedChangeDefinitionCRFOrdinalServlet.processRequest(request, spiedResponse);

		// verifying that target bean and its site level children were updated
		Assert.assertTrue(studyLevelEventDefCRFBeanList.get(1).getOrdinal() == 2);
		Mockito.verify(mockedEventDefinitionCRFDAO).update(studyLevelEventDefCRFBeanList.get(1));
		Assert.assertTrue(siteLevelEventDefCRFBeanList.get(0).getOrdinal() == 1);
		Mockito.verify(mockedEventDefinitionCRFDAO).update(siteLevelEventDefCRFBeanList.get(0));
		Assert.assertTrue(siteLevelEventDefCRFBeanList.get(1).getOrdinal() == 3);
		Mockito.verify(mockedEventDefinitionCRFDAO).update(siteLevelEventDefCRFBeanList.get(1));

		// verifying that proper neighbour bean was updated
		Assert.assertTrue(studyLevelEventDefCRFBeanList.get(2).getOrdinal() == 3);
		Mockito.verify(mockedEventDefinitionCRFDAO).update(studyLevelEventDefCRFBeanList.get(2));

		// checking forward
		Assert.assertTrue(request.getAttribute("id")
				.equals(String.valueOf(studyLevelEventDefCRFBeanList.get(1).getStudyEventDefinitionId())));
		Mockito.verify(mockedServletContext).getRequestDispatcher(Page.VIEW_EVENT_DEFINITION_SERVLET.getFileName());
		Mockito.verify(mockedRequestDispatcher).forward(request, spiedResponse);
	}

	@Test
	public void testChangeDefinitionCRFOrdinalServletBehaviorOnActionMoveUp() throws Exception {

		// 1. SETTING UP TEST

		String action = "moveUp";
		request.setParameter("action", action);

		Mockito.when(mockedEventDefinitionCRFDAO.findByPK(Mockito.any(Integer.class)))
				.thenReturn(studyLevelEventDefCRFBeanList.get(1));

		// 2. TESTING BEHAVIOR

		spiedChangeDefinitionCRFOrdinalServlet.processRequest(request, spiedResponse);

		// verifying that target bean and its site level children were updated
		Assert.assertTrue(studyLevelEventDefCRFBeanList.get(1).getOrdinal() == 2);
		Mockito.verify(mockedEventDefinitionCRFDAO).update(studyLevelEventDefCRFBeanList.get(1));
		Assert.assertTrue(siteLevelEventDefCRFBeanList.get(0).getOrdinal() == 2);
		Mockito.verify(mockedEventDefinitionCRFDAO).update(siteLevelEventDefCRFBeanList.get(0));
		Assert.assertTrue(siteLevelEventDefCRFBeanList.get(1).getOrdinal() == 1);
		Mockito.verify(mockedEventDefinitionCRFDAO).update(siteLevelEventDefCRFBeanList.get(1));

		// verifying that proper neighbour bean was updated
		Assert.assertTrue(studyLevelEventDefCRFBeanList.get(0).getOrdinal() == 1);
		Mockito.verify(mockedEventDefinitionCRFDAO).update(studyLevelEventDefCRFBeanList.get(0));

		// checking forward
		Assert.assertTrue(request.getAttribute("id")
				.equals(String.valueOf(studyLevelEventDefCRFBeanList.get(1).getStudyEventDefinitionId())));
		Mockito.verify(mockedServletContext).getRequestDispatcher(Page.VIEW_EVENT_DEFINITION_SERVLET.getFileName());
		Mockito.verify(mockedRequestDispatcher).forward(request, spiedResponse);
	}
}
