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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.control.RememberLastPage;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.Locale;

/**
 * Processes user request and generate subject list
 * 
 * @author jxu
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class ListSubjectServlet extends RememberLastPage {
	public static final String SAVED_LIST_SUBJECTS_URL = "savedListSubjectsUrl";
	Locale locale;

	/**
     *
     */
	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.ADMIN_SYSTEM_SERVLET, resexception.getString("not_admin"), "1");

	}

	@Override
	public void processRequest() throws Exception {
		analyzeUrl();
		SubjectDAO sdao = new SubjectDAO(sm.getDataSource());

		StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
		StudyDAO studyDao = new StudyDAO(sm.getDataSource());
		UserAccountDAO uadao = new UserAccountDAO(sm.getDataSource());

		ListSubjectTableFactory factory = new ListSubjectTableFactory();
		factory.setSubjectDao(sdao);
		factory.setStudySubjectDao(subdao);
		factory.setUserAccountDao(uadao);
		factory.setStudyDao(studyDao);
		factory.setCurrentStudy(currentStudy);

		String auditLogsHtml = factory.createTable(request, response).render();
		request.setAttribute("listSubjectsHtml", auditLogsHtml);

		analyzeForward(Page.SUBJECT_LIST);
	}

	@Override
	protected String getAdminServlet() {
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	@Override
	protected String getUrlKey() {
		return SAVED_LIST_SUBJECTS_URL;
	}

	@Override
	protected String getDefaultUrl() {
		FormProcessor fp = new FormProcessor(request);
		return "?module=" + fp.getString("module")
				+ "&maxRows=15&listSubjects_tr_=true&listSubjects_p_=1&listSubjects_mr_=15";
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation() {
		return request.getQueryString() == null || !request.getQueryString().contains("&listSubjects_p_=");
	}
}
