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

import java.util.ResourceBundle;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.control.managestudy.RestoreStudyEventServlet;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
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

public class RestoreStudyEventServletTest extends DefaultAppContextTest {

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	private UserAccountBean currentUser;

	private StudyUserRoleBean currentRole;

	@Mock
	private RestoreStudyEventServlet restoreStudyEventServlet;

	@Mock
	private EventCRFService eventCRFService;

	private ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle();

	private ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle();

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();

		Mockito.doCallRealMethod().when(restoreStudyEventServlet).mayProceed(request, response);
		Mockito.doCallRealMethod().when(restoreStudyEventServlet).processRequest(request, response);

		Mockito.doReturn(studyEventDao).when(restoreStudyEventServlet).getStudyEventDAO();
		Mockito.doReturn(studySubjectDAO).when(restoreStudyEventServlet).getStudySubjectDAO();
		Mockito.doReturn(studyEventDefinitionDAO).when(restoreStudyEventServlet).getStudyEventDefinitionDAO();
		Mockito.doReturn(studyDAO).when(restoreStudyEventServlet).getStudyDAO();
		Mockito.doReturn(eventDefinitionCRFDAO).when(restoreStudyEventServlet).getEventDefinitionCRFDAO();
		Mockito.doReturn(eventCRFDAO).when(restoreStudyEventServlet).getEventCRFDAO();
		Mockito.doReturn(itemDataDAO).when(restoreStudyEventServlet).getItemDataDAO();
		Mockito.doReturn(crfdao).when(restoreStudyEventServlet).getCRFDAO();
		Mockito.doReturn(crfVersionDao).when(restoreStudyEventServlet).getCRFVersionDAO();
		Mockito.doReturn(codedItemService).when(restoreStudyEventServlet).getCodedItemService();
		Mockito.doReturn(eventCRFService).when(restoreStudyEventServlet).getEventCRFService();
		Mockito.doReturn(studyEventService).when(restoreStudyEventServlet).getStudyEventService();

		Whitebox.setInternalState(restoreStudyEventServlet, "respage", respage);
		Whitebox.setInternalState(restoreStudyEventServlet, "resexception", resexception);
		Whitebox.setInternalState(restoreStudyEventServlet, "logger",
				LoggerFactory.getLogger("RestoreStudyEventServlet"));
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatRestoreStudyEventServletDoesNotGrantAccessToInvestigatorWithTypeUser() throws Exception {

		currentUser = new UserAccountBean();
		currentUser.addUserType(UserType.USER);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.INVESTIGATOR);

		Mockito.doReturn(currentUser).when(restoreStudyEventServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(restoreStudyEventServlet).getCurrentRole(request);

		restoreStudyEventServlet.mayProceed(request, response);
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatRestoreStudyEventServletDoesNotGrantAccessToStudyMonitorWithTypeUser() throws Exception {

		currentUser = new UserAccountBean();
		currentUser.addUserType(UserType.USER);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.STUDY_MONITOR);

		Mockito.doReturn(currentUser).when(restoreStudyEventServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(restoreStudyEventServlet).getCurrentRole(request);

		restoreStudyEventServlet.mayProceed(request, response);
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatRestoreStudyEventServletDoesNotGrantAccessToCRCWithTypeUser() throws Exception {

		currentUser = new UserAccountBean();
		currentUser.addUserType(UserType.USER);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);

		Mockito.doReturn(currentUser).when(restoreStudyEventServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(restoreStudyEventServlet).getCurrentRole(request);

		restoreStudyEventServlet.mayProceed(request, response);
	}

	@Test
	public void testRestoreStudyEventServletProcessingWhenStudyEventStatusIsAvailable() throws Exception {

		int studyEventId = 1;
		int studySubId = 1;
		String action = "confirm";

		currentUser = new UserAccountBean();
		currentUser.setId(1);
		currentUser.setName(UserAccountBean.ROOT);
		currentUser.addUserType(UserType.SYSADMIN);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);

		Mockito.doReturn(currentUser).when(restoreStudyEventServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(restoreStudyEventServlet).getCurrentRole(request);

		request.setParameter("action", action);
		request.setParameter("id", String.valueOf(studyEventId));
		request.setParameter("studySubId", String.valueOf(studySubId));

		restoreStudyEventServlet.processRequest(request, response);

		assertNull(request.getAttribute("displayEvent"));
		assertTrue(response.getStatus() == 302);
		assertTrue(response.getHeader("Location").equals(
				request.getContextPath() + Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName() + "?id=" + studySubId));
	}

	@Test
	public void testRestoreStudyEventServletProcessingWhenActionProvidedAsConfirm() throws Exception {

		int studyEventId = 12;
		int studySubId = 1;
		String action = "confirm";
		DisplayStudyEventBean displayEventBean;

		currentUser = new UserAccountBean();
		currentUser.setId(1);
		currentUser.setName(UserAccountBean.ROOT);
		currentUser.addUserType(UserType.SYSADMIN);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);

		Mockito.doReturn(currentUser).when(restoreStudyEventServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(restoreStudyEventServlet).getCurrentRole(request);

		request.setParameter("action", action);
		request.setParameter("id", String.valueOf(studyEventId));
		request.setParameter("studySubId", String.valueOf(studySubId));

		restoreStudyEventServlet.processRequest(request, response);

		displayEventBean = (DisplayStudyEventBean) request.getAttribute("displayEvent");

		assertNotNull(displayEventBean);
		assertTrue(displayEventBean.getStudyEvent().getId() == studyEventId);
	}

	@Test
	public void testRestoreStudyEventServletProcessingWhenActionProvidedAsSubmit() throws Exception {

		StudyEventBean event;
		int studyEventId = 12;
		int studySubId = 1;
		String action = "submit";

		currentUser = new UserAccountBean();
		currentUser.setId(1);
		currentUser.setName(UserAccountBean.ROOT);
		currentUser.addUserType(UserType.SYSADMIN);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);

		Mockito.doReturn(currentUser).when(restoreStudyEventServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(restoreStudyEventServlet).getCurrentRole(request);

		request.setParameter("action", action);
		request.setParameter("id", String.valueOf(studyEventId));
		request.setParameter("studySubId", String.valueOf(studySubId));

		restoreStudyEventServlet.processRequest(request, response);

		event = (StudyEventBean) studyEventDao.findByPK(studyEventId);

		assertTrue(event.getStatus().isAvailable());
		assertTrue(response.getStatus() == 302);
		assertTrue(response.getHeader("Location").equals(
				request.getContextPath() + Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName() + "?id=" + studySubId));
	}

}
