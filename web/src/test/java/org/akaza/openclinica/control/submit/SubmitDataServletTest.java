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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2009 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

public class SubmitDataServletTest {

	private UserAccountBean userBean;

	private StudyUserRoleBean userRole;

	@Before
	public void setUp() throws Exception {
		userBean = Mockito.mock(UserAccountBean.class);
		userBean.setId(1);
		userRole = Mockito.mock(StudyUserRoleBean.class);
	}

	@Test
	public void testThatSystemAdministratorMaySubmitData() {
		Mockito.when(userRole.getRole()).thenReturn(Role.SYSTEM_ADMINISTRATOR);
		assertTrue(SubmitDataServlet.maySubmitData(userBean, userRole));
	}

	@Test
	public void testThatStudyAdministratorMaySubmitData() {
		Mockito.when(userRole.getRole()).thenReturn(Role.STUDY_ADMINISTRATOR);
		assertTrue(SubmitDataServlet.maySubmitData(userBean, userRole));
	}

	@Test
	public void testThatStudyDirectorMaySubmitData() {
		Mockito.when(userRole.getRole()).thenReturn(Role.STUDY_DIRECTOR);
		assertTrue(SubmitDataServlet.maySubmitData(userBean, userRole));
	}

	@Test
	public void testThatInvestigatorMaySubmitData() {
		Mockito.when(userRole.getRole()).thenReturn(Role.INVESTIGATOR);
		assertTrue(SubmitDataServlet.maySubmitData(userBean, userRole));
	}

	@Test
	public void testThatClinicalResearchCoordinatorMaySubmitData() {
		Mockito.when(userRole.getRole()).thenReturn(Role.CLINICAL_RESEARCH_COORDINATOR);
		assertTrue(SubmitDataServlet.maySubmitData(userBean, userRole));
	}

	@Test
	public void testThatStudyEvaluatorMaySubmitData() {
		Mockito.when(userRole.getRole()).thenReturn(Role.STUDY_EVALUATOR);
		assertTrue(SubmitDataServlet.maySubmitData(userBean, userRole));
	}
}
