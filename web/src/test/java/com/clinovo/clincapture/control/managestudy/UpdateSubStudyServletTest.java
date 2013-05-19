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
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.managestudy.UpdateSubStudyServlet;
import org.akaza.openclinica.dao.managestudy.IStudyDAO;
import org.akaza.openclinica.dao.service.IStudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.ICRFVersionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Locale;

/**
 * User: Pavel Date: 06.11.12
 */
public class UpdateSubStudyServletTest {

	public static final int ID = 1;
	private Mockery context = new Mockery();

	private IStudyDAO studyDAO;
	private IStudyParameterValueDAO studyParameterValueDAO;
	private ICRFVersionDAO crfVersionDAO;
	private HttpSession session;

	private StudyBean studyFromSession;
	private StudyBean studyFromDb;

	private UpdateSubStudyServlet updateSubStudyServlet;
	private StudyParameterValueBean studyParameterValueBean;
	private UserAccountBean userAccountBean;

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.getDefault());

		studyDAO = context.mock(IStudyDAO.class);
		studyParameterValueDAO = context.mock(IStudyParameterValueDAO.class);
		crfVersionDAO = context.mock(ICRFVersionDAO.class);
		session = context.mock(HttpSession.class);

		studyFromSession = new StudyBean();
		studyFromSession.setId(ID);
		studyFromSession.setStatus(Status.INVALID);

		studyFromDb = new StudyBean();
		studyFromDb.setId(ID);
		studyFromDb.setStatus(Status.AVAILABLE);

		userAccountBean = new UserAccountBean();
		userAccountBean.setId(ID);

		updateSubStudyServlet = new UpdateSubStudyServlet();
		updateSubStudyServlet.setSdao(studyDAO);
		updateSubStudyServlet.setSpvdao(studyParameterValueDAO);
		updateSubStudyServlet.setUserBean(userAccountBean);
		updateSubStudyServlet.setCvdao(crfVersionDAO);
		SecureController.resword = ResourceBundleProvider.getWordsBundle();

		studyParameterValueBean = new StudyParameterValueBean();
		studyParameterValueBean.setId(ID);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testSubmitStudy() {

		context.checking(new Expectations() {
			{
				one(session).getAttribute(UpdateSubStudyServlet.NEW_STUDY);
				will(returnValue(studyFromSession));
				one(studyDAO).findByPK(ID);
				will(returnValue(studyFromDb));
				one(studyDAO).update(studyFromSession);
				one(session).getAttribute(UpdateSubStudyServlet.DEFINITIONS);
				will(returnValue(new ArrayList<StudyEventDefinitionBean>()));
				one(session).removeAttribute(UpdateSubStudyServlet.NEW_STUDY);
				one(session).removeAttribute(UpdateSubStudyServlet.PARENT_NAME);
				one(session).removeAttribute(UpdateSubStudyServlet.DEFINITIONS);
				one(session).removeAttribute(UpdateSubStudyServlet.SDV_OPTIONS);
			}
		});

		updateSubStudyServlet.submitStudy(session);
		context.assertIsSatisfied();
	}
}
