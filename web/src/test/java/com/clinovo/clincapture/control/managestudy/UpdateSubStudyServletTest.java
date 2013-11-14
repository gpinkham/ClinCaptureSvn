/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
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
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.managestudy.UpdateSubStudyServlet;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.IStudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.ICRFVersionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Locale;

public class UpdateSubStudyServletTest {

	public static final int ID = 1;
	private Mockery context = new Mockery();

	private StudyDAO<?, ?> studyDAO;
	private HttpServletRequest request;
    private HttpSession session;

	private StudyBean studyFromSession;
	private StudyBean studyFromDb;

	private UpdateSubStudyServlet updateSubStudyServlet;
	private StudyParameterValueBean studyParameterValueBean;
	private UserAccountBean userAccountBean;
	
    @Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.getDefault());

        context.mock(DataSource.class);
		context.mock(IStudyParameterValueDAO.class);
		context.mock(ICRFVersionDAO.class);
        request = context.mock(HttpServletRequest.class);
        session = context.mock(HttpSession.class);

		studyFromSession = new StudyBean();
		studyFromSession.setId(ID);
		studyFromSession.setStatus(Status.INVALID);

		studyFromDb = new StudyBean();
		studyFromDb.setId(ID);
		studyFromDb.setStatus(Status.AVAILABLE);

		userAccountBean = new UserAccountBean();
		userAccountBean.setId(ID);

        studyDAO = Mockito.mock(StudyDAO.class);
        Mockito.doReturn(studyFromDb).when(studyDAO).findByPK(ID);
        Mockito.doReturn(studyFromDb).when(studyDAO).update(studyFromSession);

        updateSubStudyServlet = Mockito.mock(UpdateSubStudyServlet.class);
        Mockito.doCallRealMethod().when(updateSubStudyServlet).submitStudy(request);
        Mockito.doReturn(userAccountBean).when(updateSubStudyServlet).getUserAccountBean(request);
        Mockito.doReturn(studyDAO).when(updateSubStudyServlet).getStudyDAO();

        Whitebox.setInternalState(updateSubStudyServlet, "resword", ResourceBundleProvider.getWordsBundle());

		studyParameterValueBean = new StudyParameterValueBean();
		studyParameterValueBean.setId(ID);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testSubmitStudy() {

		context.checking(new Expectations() {
			{
                allowing(request).getSession();
                will(returnValue(session));
                allowing(session).getAttribute(UpdateSubStudyServlet.NEW_STUDY);
				will(returnValue(studyFromSession));
                allowing(session).getAttribute(UpdateSubStudyServlet.DEFINITIONS);
				will(returnValue(new ArrayList<StudyEventDefinitionBean>()));
				one(session).removeAttribute(UpdateSubStudyServlet.NEW_STUDY);
				one(session).removeAttribute(UpdateSubStudyServlet.PARENT_NAME);
				one(session).removeAttribute(UpdateSubStudyServlet.DEFINITIONS);
				one(session).removeAttribute(UpdateSubStudyServlet.SDV_OPTIONS);
			}
		});

		updateSubStudyServlet.submitStudy(request);
		context.assertIsSatisfied();
	}
}
