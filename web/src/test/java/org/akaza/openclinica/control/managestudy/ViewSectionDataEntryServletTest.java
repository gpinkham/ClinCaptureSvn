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

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


@RunWith(PowerMockRunner.class)
public class ViewSectionDataEntryServletTest {
	
	@Spy
	private ViewSectionDataEntryServlet viewSectionDataEntryServlet = new ViewSectionDataEntryServlet();
	@Mock
	private MockHttpServletResponse response;
	@Mock
	private UserAccountBean currentUser;
	@Mock
	private StudyUserRoleBean currentRole;
	
	private MockHttpServletRequest request;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();

		currentUser = new UserAccountBean();
		currentUser.addUserType(UserType.USER);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.STUDY_ADMINISTRATOR);

		request.getSession().setAttribute("userRole", currentRole);
		request.getSession().setAttribute(ViewSectionDataEntryServlet.USER_BEAN_NAME, currentUser);
		PowerMockito.doNothing().when(viewSectionDataEntryServlet).mayAccess(request);
	}

	@Test
	public void testThatViewSectionDataEntryServletGrantAccessToStudyAdministrator() throws InsufficientPermissionException {
		viewSectionDataEntryServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatViewSectionDataEntryServletGrantAccessToStudyCoder() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_CODER);
		viewSectionDataEntryServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatViewSectionDataEntryServletGrantAccessToStudyMonitor() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_MONITOR);
		viewSectionDataEntryServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatViewSectionDataEntryServletGrantAccessToCRC() throws InsufficientPermissionException {
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		viewSectionDataEntryServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatViewSectionDataEntryServletGrantAccessToInvestigator() throws InsufficientPermissionException {
		currentRole.setRole(Role.INVESTIGATOR);
		viewSectionDataEntryServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}
}
