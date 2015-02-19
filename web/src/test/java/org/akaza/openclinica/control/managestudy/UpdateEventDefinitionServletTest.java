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

import com.clinovo.service.impl.EventCRFServiceImpl;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.util.DAOWrapper;
import org.akaza.openclinica.util.SignStateRestorer;
import org.akaza.openclinica.util.SubjectEventStatusUtil;
import org.akaza.openclinica.view.Page;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@SuppressWarnings({ "unchecked" })
@RunWith(PowerMockRunner.class)
@PrepareForTest({SubjectEventStatusUtil.class, ResourceBundleProvider.class})
public class UpdateEventDefinitionServletTest {

	@Spy
	private UpdateEventDefinitionServlet spyUpdateEventDefinitionServlet = new UpdateEventDefinitionServlet();

	@Mock
	private EventCRFServiceImpl mockedEventCRFService;

	@Mock
	private StudyDAO mockedStudyDAO;

	@Mock
	private StudyEventDAO mockedStudyEventDAO;

	@Mock
	private StudyEventDefinitionDAO mockedStudyEventDefinitionDAO;

	@Mock
	private EventDefinitionCRFDAO mockedEventDefinitionCRFDAO;

	@Mock
	private MockServletContext mockedServletContext;

	@Mock
	private MockRequestDispatcher mockedRequestDispatcher;

	private MockHttpServletResponse response = new MockHttpServletResponse();

	private MockHttpServletRequest request;

	private UserAccountBean currentUser;

	private StudyEventDefinitionBean testStudyEventDefinition;

	private ResourceBundle resPage;

	@Before
	public void beforeTestCaseRun() throws Exception {

		// setting up servlet context
		Mockito.when(mockedServletContext.getRequestDispatcher(Mockito.any(String.class))).thenReturn(mockedRequestDispatcher);
		request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		request.setSession(session);

		// current system user
		currentUser = new UserAccountBean();
		currentUser.setId(1);

		// current study
		StudyBean currentStudy = new StudyBean();
		currentStudy.setId(10);

		// test Study Event Definition
		testStudyEventDefinition = new StudyEventDefinitionBean();
		testStudyEventDefinition.setId(77);
		testStudyEventDefinition.setStudyId(currentStudy.getId());
		testStudyEventDefinition.setOid("testStudyEventDefinition77");
		session.setAttribute("definition", testStudyEventDefinition);

		// first obtaining all the required Resource Bundle instances for tests,
		// then stubbing all the static methods of the ResourceBundleProvider class
		Locale locale = new Locale("en");
		ResourceBundleProvider.updateLocale(locale);
		resPage = ResourceBundleProvider.getPageMessagesBundle();
		ResourceBundle workflowBundle = ResourceBundleProvider.getWorkflowBundle(locale);
		ResourceBundle resFormat = ResourceBundleProvider.getFormatBundle();
		PowerMockito.mockStatic(ResourceBundleProvider.class);
		PowerMockito.when(ResourceBundleProvider.getWorkflowBundle(Mockito.any(Locale.class))).thenReturn(workflowBundle);
		PowerMockito.when(ResourceBundleProvider.getFormatBundle(Mockito.any(Locale.class))).thenReturn(resFormat);

		PowerMockito.mockStatic(SubjectEventStatusUtil.class);

		// setting up DAO mocks
		Mockito.when(mockedStudyEventDefinitionDAO.findReferenceVisitBeans())
				.thenReturn(new ArrayList<StudyEventDefinitionBean>());

		// setting up spied UpdateEventDefinitionServlet
		Whitebox.setInternalState(spyUpdateEventDefinitionServlet, "respage", resPage);
		Mockito.doReturn(mockedServletContext).when(spyUpdateEventDefinitionServlet).getServletContext();
		Mockito.doReturn(currentUser).when(spyUpdateEventDefinitionServlet).getUserAccountBean(request);
		Mockito.doReturn(mockedStudyDAO).when(spyUpdateEventDefinitionServlet).getStudyDAO();
		Mockito.doReturn(mockedStudyEventDAO).when(spyUpdateEventDefinitionServlet).getStudyEventDAO();
		Mockito.doReturn(mockedStudyEventDefinitionDAO).when(spyUpdateEventDefinitionServlet).getStudyEventDefinitionDAO();
		Mockito.doReturn(mockedEventDefinitionCRFDAO).when(spyUpdateEventDefinitionServlet).getEventDefinitionCRFDAO();
		Mockito.doReturn(mockedEventCRFService).when(spyUpdateEventDefinitionServlet).getEventCRFService();
	}

	@Test
	public void testRemoveEventDefinitionCRFFromStudyEventDefinitionWorkFlow() throws Exception {

		// SETTING UP TEST

		String action = "submit";
		request.setParameter("action", action);

		CRFBean testCRF = new CRFBean();
		testCRF.setId(15);
		testCRF.setOid("testCRF15");

		EventDefinitionCRFBean parentEventDefinitionCRF = new EventDefinitionCRFBean();
		parentEventDefinitionCRF.setId(34);
		parentEventDefinitionCRF.setStatus(Status.DELETED);
		parentEventDefinitionCRF.setOldStatus(Status.AVAILABLE);
		parentEventDefinitionCRF.setCrf(testCRF);
		List<EventDefinitionCRFBean> eventDefinitionCRFsToUpdate = new ArrayList<EventDefinitionCRFBean>();
		eventDefinitionCRFsToUpdate.add(parentEventDefinitionCRF);
		request.getSession().setAttribute("eventDefinitionCRFs", eventDefinitionCRFsToUpdate);

		EventDefinitionCRFBean childEventDefinitionCRF = new EventDefinitionCRFBean();
		childEventDefinitionCRF.setId(90);
		childEventDefinitionCRF.setParentId(parentEventDefinitionCRF.getId());
		List<EventDefinitionCRFBean> childEventDefinitionCRFsToUpdate = new ArrayList<EventDefinitionCRFBean>();
		childEventDefinitionCRFsToUpdate.add(childEventDefinitionCRF);
		request.getSession().setAttribute("childEventDefCRFs", childEventDefinitionCRFsToUpdate);

		// EXECUTE

		spyUpdateEventDefinitionServlet.processRequest(request, response);

		// VERIFY BEHAVIOR

		Mockito.verify(mockedEventDefinitionCRFDAO).update(parentEventDefinitionCRF);
		Mockito.verify(mockedEventCRFService).removeEventCRFsByEventDefinitionCRF(testStudyEventDefinition.getOid(),
				testCRF.getOid(), currentUser);
		Mockito.verify(mockedEventCRFService, Mockito.never()).restoreEventCRFsByEventDefinitionCRF(testStudyEventDefinition.getOid(),
				testCRF.getOid(), currentUser);
		Mockito.verify(mockedEventDefinitionCRFDAO).update(childEventDefinitionCRF);

		PowerMockito.verifyStatic();
		SubjectEventStatusUtil.determineSubjectEventStates(Mockito.any(ArrayList.class), Mockito.any(DAOWrapper.class),
				Mockito.any(SignStateRestorer.class));

		List<String> pageMessages = (List<String>) request.getAttribute("pageMessages");
		Assert.assertTrue(pageMessages.contains(resPage.getString("the_ED_has_been_updated_succesfully")));

		Mockito.verify(mockedServletContext).getRequestDispatcher(Page.LIST_DEFINITION_SERVLET.getFileName());
		Mockito.verify(mockedRequestDispatcher).forward(request, response);
	}

	@Test
	public void testRestoreEventDefinitionCRFFromStudyEventDefinitionWorkFlow() throws Exception {

		// SETTING UP TEST

		String action = "submit";
		request.setParameter("action", action);

		CRFBean testCRF = new CRFBean();
		testCRF.setId(15);
		testCRF.setOid("testCRF15");

		EventDefinitionCRFBean parentEventDefinitionCRF = new EventDefinitionCRFBean();
		parentEventDefinitionCRF.setId(34);
		parentEventDefinitionCRF.setStatus(Status.AVAILABLE);
		parentEventDefinitionCRF.setOldStatus(Status.DELETED);
		parentEventDefinitionCRF.setCrf(testCRF);
		List<EventDefinitionCRFBean> eventDefinitionCRFsToUpdate = new ArrayList<EventDefinitionCRFBean>();
		eventDefinitionCRFsToUpdate.add(parentEventDefinitionCRF);
		request.getSession().setAttribute("eventDefinitionCRFs", eventDefinitionCRFsToUpdate);

		EventDefinitionCRFBean childEventDefinitionCRF = new EventDefinitionCRFBean();
		childEventDefinitionCRF.setId(90);
		childEventDefinitionCRF.setParentId(parentEventDefinitionCRF.getId());
		List<EventDefinitionCRFBean> childEventDefinitionCRFsToUpdate = new ArrayList<EventDefinitionCRFBean>();
		childEventDefinitionCRFsToUpdate.add(childEventDefinitionCRF);
		request.getSession().setAttribute("childEventDefCRFs", childEventDefinitionCRFsToUpdate);

		// EXECUTE

		spyUpdateEventDefinitionServlet.processRequest(request, response);

		// VERIFY BEHAVIOR

		Mockito.verify(mockedEventDefinitionCRFDAO).update(parentEventDefinitionCRF);
		Mockito.verify(mockedEventCRFService).restoreEventCRFsByEventDefinitionCRF(testStudyEventDefinition.getOid(),
				testCRF.getOid(), currentUser);
		Mockito.verify(mockedEventCRFService, Mockito.never()).removeEventCRFsByEventDefinitionCRF(testStudyEventDefinition.getOid(),
				testCRF.getOid(), currentUser);
		Mockito.verify(mockedEventDefinitionCRFDAO).update(childEventDefinitionCRF);

		PowerMockito.verifyStatic();
		SubjectEventStatusUtil.determineSubjectEventStates(Mockito.any(ArrayList.class), Mockito.any(DAOWrapper.class),
				Mockito.any(SignStateRestorer.class));

		List<String> pageMessages = (List<String>) request.getAttribute("pageMessages");
		Assert.assertTrue(pageMessages.contains(resPage.getString("the_ED_has_been_updated_succesfully")));

		Mockito.verify(mockedServletContext).getRequestDispatcher(Page.LIST_DEFINITION_SERVLET.getFileName());
		Mockito.verify(mockedRequestDispatcher).forward(request, response);
	}
}
