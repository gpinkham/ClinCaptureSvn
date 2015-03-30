/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package org.akaza.openclinica.control.extract;

import static org.junit.Assert.assertNull;

import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterConfig;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.clinovo.i18n.LocaleResolver;

public class RemoveDatasetServletTest {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private RemoveDatasetServlet servlet;
	private UserAccountBean currentUser;
	private StudyUserRoleBean currentRole;
	private DatasetBean dataset;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		servlet = Mockito.mock(RemoveDatasetServlet.class);
		currentUser = new UserAccountBean();
		currentUser.setId(1);
		currentRole = new StudyUserRoleBean();
		StudyBean currentStudy = new StudyBean();
		StudyBean otherStudy = new StudyBean();
		Mockito.when(servlet.getCurrentRole(request)).thenReturn(currentRole);
		Mockito.when(servlet.getUserAccountBean(request)).thenReturn(currentUser);
		Mockito.when(servlet.getCurrentStudy(request)).thenReturn(currentStudy);
		Mockito.doCallRealMethod().when(servlet).mayProceed(request, response);
		Mockito.doCallRealMethod().when(servlet).processRequest(request, response);

		Locale locale = new Locale("en");
		LocaleResolver.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(locale);
		ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle(locale);
		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle(locale);
		Whitebox.setInternalState(servlet, "respage", respage);
		Whitebox.setInternalState(servlet, "resexception", resexception);

		currentStudy.setId(1);
		currentStudy.setParentStudyId(0);
		currentStudy.setStudyParameterConfig(new StudyParameterConfig());
		currentStudy.getStudyParameterConfig().setCollectDob("3");
		otherStudy.setId(12);
		StudyDAO sdao = Mockito.mock(StudyDAO.class);
		Mockito.when(servlet.getStudyDAO()).thenReturn(sdao);
		Mockito.when(sdao.findByPK(1)).thenReturn(currentStudy);
		Mockito.when(sdao.findByPK(12)).thenReturn(otherStudy);

		dataset = new DatasetBean();
		dataset.setId(1);
		DatasetDAO dsdao = Mockito.mock(DatasetDAO.class);
		Mockito.when(servlet.getDatasetDAO()).thenReturn(dsdao);
		Mockito.when(dsdao.findByPK(0)).thenReturn(dataset);
	}

	@Test
	public void testThatSysAdminCanProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.SYSADMIN);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatAdminUserCanProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.SYSADMIN);
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatStudyAdminUserCanProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.USER);
		currentRole.setRole(Role.STUDY_ADMINISTRATOR);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatStudyMonitorCanProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.SYSADMIN);
		currentRole.setRole(Role.STUDY_MONITOR);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatSiteMonitorCanProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.USER);
		currentRole.setRole(Role.SITE_MONITOR);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatUnAuthorizedNonAdminUserCannotProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.USER);
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		servlet.mayProceed(request, response);
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatNonOwnerUserCannotProceed() throws Exception {
		currentUser.addUserType(UserType.USER);
		currentRole.setRole(Role.STUDY_MONITOR);
		dataset.setOwner(new UserAccountBean());
		dataset.getOwner().setId(10);
		dataset.setStudyId(1);
		servlet.processRequest(request, response);
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatWhenInDiffStudyCannotProceed() throws Exception {
		currentUser.addUserType(UserType.USER);
		currentRole.setRole(Role.STUDY_MONITOR);
		dataset.setOwner(new UserAccountBean());
		dataset.getOwner().setId(10);
		dataset.setStudyId(12);
		servlet.processRequest(request, response);
	}
}
