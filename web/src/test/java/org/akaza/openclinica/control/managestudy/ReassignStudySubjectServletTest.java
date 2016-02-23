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

import java.util.Locale;

import com.clinovo.service.StudyService;
import com.clinovo.service.impl.StudyServiceImpl;
import org.akaza.openclinica.bean.admin.DisplayStudyBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;

import com.clinovo.i18n.LocaleResolver;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ReassignStudySubjectServlet.class, StudySubjectDAO.class, SubjectDAO.class, StudyDAO.class})
public class ReassignStudySubjectServletTest {

	private StudyService studyService;
	private ReassignStudySubjectServlet reassignStudySubjectServlet;
	private MockHttpServletResponse response;
	private MockHttpServletRequest request;
	private StudyBean currentStudy;
	private MockServletContext servletContext;
	private MockRequestDispatcher requestDispatcher;

	@Before
	public void setUp() throws Exception {

		studyService = Mockito.mock(StudyServiceImpl.class);
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		reassignStudySubjectServlet = PowerMockito.spy(new ReassignStudySubjectServlet());
		servletContext = Mockito.mock(MockServletContext.class);
		requestDispatcher = Mockito.mock(MockRequestDispatcher.class);

		Locale locale = new Locale("en");
		LocaleResolver.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(locale);
		Whitebox.setInternalState(reassignStudySubjectServlet, "studyService", studyService);

		currentStudy = new StudyBean();
		currentStudy.setId(1);
		StudyDAO studyDAO = Mockito.mock(StudyDAO.class);
		Mockito.when(studyDAO.findByPK(Mockito.anyInt())).thenReturn(currentStudy);

		StudySubjectBean studySub = new StudySubjectBean();
		StudySubjectDAO studySubjectDAO = Mockito.mock(StudySubjectDAO.class);
		Mockito.when(studySubjectDAO.findByPK(Mockito.anyInt())).thenReturn(studySub);

		SubjectBean subjectBean = new SubjectBean();
		SubjectDAO subjectDAO = Mockito.mock(SubjectDAO.class);
		Mockito.when(subjectDAO.findByPK(Mockito.anyInt())).thenReturn(subjectBean);

		DisplayStudyBean displayStudy = new DisplayStudyBean();
		PowerMockito.doReturn(displayStudy).when(reassignStudySubjectServlet, "getDisplayStudy",
				Mockito.any(StudyDAO.class), Mockito.any(StudySubjectBean.class));

		Mockito.doReturn(studySubjectDAO).when(reassignStudySubjectServlet).getStudySubjectDAO();
		Mockito.doReturn(studyDAO).when(reassignStudySubjectServlet).getStudyDAO();
		Mockito.doReturn(subjectDAO).when(reassignStudySubjectServlet).getSubjectDAO();
		Mockito.doReturn(currentStudy).when(reassignStudySubjectServlet).getCurrentStudy();

		Mockito.when(servletContext.getRequestDispatcher(Mockito.any(String.class))).thenReturn(requestDispatcher);
		Mockito.doReturn(servletContext).when(reassignStudySubjectServlet).getServletContext();
		Mockito.when(studyService.getSubjectStudy(Mockito.any(StudyBean.class), Mockito.any(StudySubjectBean.class)))
				.thenReturn(currentStudy);
	}

	@Test
	public void testThatReassignStudySubjectServletSaveIsDataChangeParameter() throws Exception {
		request.setParameter("id", "1");
		request.setParameter("studyId", "1");
		request.setParameter("action", "back");
		reassignStudySubjectServlet.processRequest(request, response);
		Assert.assertTrue((Boolean) request.getAttribute("isDataChanged"));
	}
}
