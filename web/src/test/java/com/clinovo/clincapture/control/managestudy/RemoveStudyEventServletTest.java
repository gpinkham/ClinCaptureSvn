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

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.control.managestudy.RemoveStudyEventServlet;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.clinovo.service.EventCRFService;

public class RemoveStudyEventServletTest extends DefaultAppContextTest {

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	private UserAccountBean currentUser;

	private StudyUserRoleBean currentRole;

	@Mock
	private RemoveStudyEventServlet removeStudyEventServlet;

	@Mock
	private EventCRFService eventCRFService;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();

		Mockito.doCallRealMethod().when(removeStudyEventServlet).mayProceed(request, response);
		Mockito.doCallRealMethod().when(removeStudyEventServlet).processRequest(request, response);

		Mockito.doReturn(studyEventDao).when(removeStudyEventServlet).getStudyEventDAO();
		Mockito.doReturn(studySubjectDAO).when(removeStudyEventServlet).getStudySubjectDAO();
		Mockito.doReturn(studyEventDefinitionDAO).when(removeStudyEventServlet).getStudyEventDefinitionDAO();
		Mockito.doReturn(studyDAO).when(removeStudyEventServlet).getStudyDAO();
		Mockito.doReturn(eventDefinitionCRFDAO).when(removeStudyEventServlet).getEventDefinitionCRFDAO();
		Mockito.doReturn(eventCRFDAO).when(removeStudyEventServlet).getEventCRFDAO();
		Mockito.doReturn(itemDataDAO).when(removeStudyEventServlet).getItemDataDAO();
		Mockito.doReturn(crfdao).when(removeStudyEventServlet).getCRFDAO();
		Mockito.doReturn(crfVersionDao).when(removeStudyEventServlet).getCRFVersionDAO();
		Mockito.doReturn(codedItemService).when(removeStudyEventServlet).getCodedItemService();
		Mockito.doReturn(eventCRFService).when(removeStudyEventServlet).getEventCRFService();
		Mockito.doReturn(studyEventService).when(removeStudyEventServlet).getStudyEventService();

		Whitebox.setInternalState(removeStudyEventServlet, "logger",
				LoggerFactory.getLogger("RemoveStudyEventServlet"));
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatRemoveStudyEventServletDoesNotGrantAccessToInvestigatorWithTypeUser() throws Exception {

		currentUser = new UserAccountBean();
		currentUser.addUserType(UserType.USER);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.INVESTIGATOR);

		Mockito.doReturn(currentUser).when(removeStudyEventServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(removeStudyEventServlet).getCurrentRole(request);

		removeStudyEventServlet.mayProceed(request, response);
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatRemoveStudyEventServletDoesNotGrantAccessToStudyMonitorWithTypeUser() throws Exception {

		currentUser = new UserAccountBean();
		currentUser.addUserType(UserType.USER);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.STUDY_MONITOR);

		Mockito.doReturn(currentUser).when(removeStudyEventServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(removeStudyEventServlet).getCurrentRole(request);

		removeStudyEventServlet.mayProceed(request, response);
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatRemoveStudyEventServletDoesNotGrantAccessToCRCWithTypeUser() throws Exception {

		currentUser = new UserAccountBean();
		currentUser.addUserType(UserType.USER);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);

		Mockito.doReturn(currentUser).when(removeStudyEventServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(removeStudyEventServlet).getCurrentRole(request);

		removeStudyEventServlet.mayProceed(request, response);
	}

	@Test
	public void testRemoveStudyEventServletProcessingWhenActionProvidedAsConfirm() throws Exception {

		int studyEventId = 1;
		int studySubId = 1;
		String action = "confirm";
		DisplayStudyEventBean displayEventBean;

		currentUser = new UserAccountBean();
		currentUser.setId(1);
		currentUser.setName(UserAccountBean.ROOT);
		currentUser.addUserType(UserType.SYSADMIN);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);

		Mockito.doReturn(currentUser).when(removeStudyEventServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(removeStudyEventServlet).getCurrentRole(request);

		request.setParameter("action", action);
		request.setParameter("id", String.valueOf(studyEventId));
		request.setParameter("studySubId", String.valueOf(studySubId));

		removeStudyEventServlet.processRequest(request, response);

		displayEventBean = (DisplayStudyEventBean) request.getAttribute("displayEvent");

		assertNotNull(displayEventBean);
		assertTrue(displayEventBean.getStudyEvent().getId() == studyEventId);
	}

	@Test
	public void testRemoveStudyEventServletProcessingWhenActionProvidedAsSubmit() throws Exception {

		StudyEventBean event;
		int studyEventId = 1;
		int studySubId = 1;
		String action = "submit";

		currentUser = new UserAccountBean();
		currentUser.setId(1);
		currentUser.setName(UserAccountBean.ROOT);
		currentUser.addUserType(UserType.SYSADMIN);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);

		Mockito.doReturn(currentUser).when(removeStudyEventServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(removeStudyEventServlet).getCurrentRole(request);

		request.setParameter("action", action);
		request.setParameter("id", String.valueOf(studyEventId));
		request.setParameter("studySubId", String.valueOf(studySubId));

		removeStudyEventServlet.processRequest(request, response);

		event = (StudyEventBean) studyEventDao.findByPK(studyEventId);

		assertTrue(event.getStatus().isDeleted());
		assertTrue(response.getStatus() == 302);
		assertTrue(response.getHeader("Location").equals(
				request.getContextPath() + Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName() + "?id=" + studySubId));
	}

}
