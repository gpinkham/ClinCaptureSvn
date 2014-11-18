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
package org.akaza.openclinica.control.managestudy;

import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.submit.ListStudySubjectsServlet;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.FindSubjectsFilter;
import org.akaza.openclinica.dao.managestudy.FindSubjectsSort;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ListStudySubjectsServletTest {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private ListStudySubjectsServlet listStudySubjectsServlet;
	private UserAccountBean currentUser;
	private StudyUserRoleBean currentRole;
	private MockServletContext servletContext;
	private MockRequestDispatcher requestDispatcher;
	private StudyBean currentStudy;
	private StudySubjectDAO studySubjectDAO;

	@Before
	public void setUp() throws Exception {

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		servletContext = Mockito.mock(MockServletContext.class);
		requestDispatcher = Mockito.mock(MockRequestDispatcher.class);
		studySubjectDAO = Mockito.mock(StudySubjectDAO.class);
		listStudySubjectsServlet = PowerMockito.spy(new ListStudySubjectsServlet());
		currentUser = new UserAccountBean();
		currentUser.addUserType(UserType.USER);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.STUDY_ADMINISTRATOR);

		StudyDAO studyDAO = Mockito.mock(StudyDAO.class);
		SubjectDAO subjectDAO = Mockito.mock(SubjectDAO.class);
		StudyEventDAO studyEventDAO = Mockito.mock(StudyEventDAO.class);
		DiscrepancyNoteDAO discrepancyNoteDAO = Mockito.mock(DiscrepancyNoteDAO.class);
		StudyGroupClassDAO studyGroupClassDAO = Mockito.mock(StudyGroupClassDAO.class);
		StudyEventDefinitionDAO studyEventDefinitionDAO = Mockito.mock(StudyEventDefinitionDAO.class);
		DynamicEventDao dynamicEventDao = Mockito.mock(DynamicEventDao.class);

		Locale locale = new Locale("en");
		SessionUtil.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(locale);
		ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle(locale);
		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle(locale);
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle(locale);
		Whitebox.setInternalState(listStudySubjectsServlet, "respage", respage);
		Whitebox.setInternalState(listStudySubjectsServlet, "resexception", resexception);
		Whitebox.setInternalState(listStudySubjectsServlet, "resformat", resformat);

		currentStudy = new StudyBean();
		currentStudy.setId(1);
		currentStudy.setIdentifier("ident");
		currentStudy.setStatus(Status.AVAILABLE);

		List<StudySubjectBean> studySubjectBeanList = new ArrayList<StudySubjectBean>();
		StudySubjectBean studySubject = buildStudySubject();
		SubjectBean subjectBean = new SubjectBean();
		subjectBean.setGender("male".charAt(0));
		studySubjectBeanList.add(studySubject);

		List<StudyEventBean> studyEventBeanList = new ArrayList<StudyEventBean>();
		StudyEventBean studyEvent = new StudyEventBean();
		studyEvent.setStudyEventDefinitionId(1);
		studyEvent.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
		studyEventBeanList.add(studyEvent);

		List<StudyEventDefinitionBean> studyEventDefinitionBeanList = new ArrayList<StudyEventDefinitionBean>();
		StudyEventDefinitionBean studyEventDefinitionBean = new StudyEventDefinitionBean();
		studyEventDefinitionBean.setName("Event 1");
		studyEventDefinitionBean.setReferenceVisit(true);
		studyEventDefinitionBean.setId(1);

		request.getSession().setAttribute("study", currentStudy);
		request.getSession().setAttribute("userRole", currentRole);

		Mockito.when(
				studySubjectDAO.getWithFilterAndSort(Mockito.any(StudyBean.class),
						Mockito.any(FindSubjectsFilter.class), Mockito.any(FindSubjectsSort.class), Mockito.anyInt(),
						Mockito.anyInt())).thenReturn((ArrayList) studySubjectBeanList);
		Mockito.when(
				studySubjectDAO.getCountWithFilter(Mockito.any(FindSubjectsFilter.class), Mockito.any(StudyBean.class)))
				.thenReturn(15);
		Mockito.when(studySubjectDAO.findNextLabel(Mockito.any(StudyBean.class))).thenReturn("study_parameter");

		Mockito.when(studyDAO.findByPK(Mockito.anyInt())).thenReturn(currentStudy);
		Mockito.when(subjectDAO.findByPK(Mockito.anyInt())).thenReturn(subjectBean);
		Mockito.when(studyEventDAO.findAllByStudySubject(Mockito.any(StudySubjectBean.class))).thenReturn(
				(ArrayList) studyEventBeanList);
		Mockito.when(studyEventDAO.findAllByStudySubject(Mockito.any(StudySubjectBean.class))).thenReturn(
				(ArrayList) studyEventBeanList);
		Mockito.when(
				studyEventDefinitionDAO.findAllActiveByParentStudyId(Mockito.anyInt(), Mockito.any(ArrayList.class)))
				.thenReturn((ArrayList) studyEventDefinitionBeanList);
		Mockito.when(studyEventDefinitionDAO.findByPK(Mockito.anyInt())).thenReturn(studyEventDefinitionBean);
		Mockito.when(dynamicEventDao.findAllDefIdsInActiveDynGroupsByStudyId(Mockito.anyInt())).thenReturn(
				new ArrayList<StudyEventDefinitionBean>());
		Mockito.when(servletContext.getRequestDispatcher(Mockito.any(String.class))).thenReturn(requestDispatcher);

		Mockito.doReturn(servletContext).when(listStudySubjectsServlet).getServletContext();
		Mockito.doReturn(dynamicEventDao).when(listStudySubjectsServlet).getDynamicEventDao();
		Mockito.doReturn(studyEventDefinitionDAO).when(listStudySubjectsServlet).getStudyEventDefinitionDAO();
		Mockito.doReturn(studyGroupClassDAO).when(listStudySubjectsServlet).getStudyGroupClassDAO();
		Mockito.doReturn(discrepancyNoteDAO).when(listStudySubjectsServlet).getDiscrepancyNoteDAO();
		Mockito.doReturn(subjectDAO).when(listStudySubjectsServlet).getSubjectDAO();
		Mockito.doReturn(studySubjectDAO).when(listStudySubjectsServlet).getStudySubjectDAO();
		Mockito.doReturn(studyDAO).when(listStudySubjectsServlet).getStudyDAO();
		Mockito.doReturn(studyEventDAO).when(listStudySubjectsServlet).getStudyEventDAO();
		Mockito.doReturn(currentUser).when(listStudySubjectsServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(listStudySubjectsServlet).getCurrentRole(request);
	}

	private StudySubjectBean buildStudySubject() {
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setId(1);
		studySubject.setCreatedDate(new Date());
		studySubject.setLabel("subject-label");
		studySubject.setStatus(Status.AVAILABLE);
		studySubject.setSecondaryLabel("label");
		StudyBean studyBean = new StudyBean();
		studyBean.setIdentifier("ident");
		studyBean.setGender("male");

		return studySubject;
	}

	@Test
	public void testThatListStudySubjectServletGrantAccessToStudyAdministrator() throws InsufficientPermissionException {
		listStudySubjectsServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatListStudySubjectServletDoesNotGrantAccessToStudyCoder() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_CODER);
		listStudySubjectsServlet.mayProceed(request, response);
	}

	@Test
	public void testThatListStudySubjectServletGrantAccessToStudyMonitor() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_MONITOR);
		listStudySubjectsServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}
	
	@Test
	public void testThatListStudySubjectServletGrantsAccessToSiteMonitor() throws InsufficientPermissionException {
		currentRole.setRole(Role.SITE_MONITOR);
		listStudySubjectsServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatListStudySubjectServletGrantAccessToCRC() throws InsufficientPermissionException {
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		listStudySubjectsServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatListStudySubjectServletGrantAccessToInvestigator() throws InsufficientPermissionException {
		currentRole.setRole(Role.INVESTIGATOR);
		listStudySubjectsServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatListStudySubjectServletReturnsCorrectPageUrl() throws Exception {
		listStudySubjectsServlet.processRequest(request, response);
		Mockito.verify(servletContext).getRequestDispatcher(Page.LIST_STUDY_SUBJECTS.getFileName());
		Mockito.verify(requestDispatcher).forward(request, response);
	}

	@Test
	public void testThatListStudySubjectServletReturnsNotNullResponseObjects() throws Exception {
		listStudySubjectsServlet.processRequest(request, response);
		Assert.assertNotNull(request.getAttribute("findSubjectsHtml"));
		Assert.assertNotNull(request.getAttribute("allDefsArray"));
		Assert.assertNotNull(request.getAttribute("studyGroupClasses"));
	}

	@Test
	public void testThatListStudySubjectServletReturnsSubjectMatrixTableWithCalendaredEventsIcon() throws Exception {
		listStudySubjectsServlet.processRequest(request, response);
		String htmlCode = (String) request.getAttribute("findSubjectsHtml");
		Assert.assertEquals(true, htmlCode.contains("bt_Calendar"));
	}

	@Test
	public void testThatListStudySubjectServletReturnsSubjectMatrixTableWithSdvIcon() throws Exception {
		Mockito.when(studySubjectDAO.allowSDVSubject(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(
				true);
		listStudySubjectsServlet.processRequest(request, response);
		String htmlCode = (String) request.getAttribute("findSubjectsHtml");
		Assert.assertEquals(true, htmlCode.contains("icon_DoubleCheck_Action"));	}
}
