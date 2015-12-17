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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({"rawtypes", "serial"})
@Component
public class RemoveSubjectServlet extends Controller {
	/**
	 *
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);

		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);

		if (ub.isSysAdmin() ) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.SUBJECT_LIST_SERVLET, resexception.getString("not_admin"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		UserAccountBean currentUser = getUserAccountBean(request);

		SubjectDAO sdao = new SubjectDAO(getDataSource());
		FormProcessor fp = new FormProcessor(request);
		int subjectId = fp.getInt("id");

		String action = fp.getString("action");
		if (subjectId == 0 || StringUtil.isBlank(action)) {
			addPageMessage(respage.getString("please_choose_a_subject_to_remove"), request);
			forwardPage(Page.SUBJECT_LIST_SERVLET, request, response);
		} else {

			SubjectBean subject = (SubjectBean) sdao.findByPK(subjectId);

			// find all study subjects
			StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
			ArrayList studySubs = ssdao.findAllBySubjectId(subjectId);

			// find study events
			StudyEventDAO sedao = new StudyEventDAO(getDataSource());
			ArrayList events = sedao.findAllBySubjectId(subjectId);
			if ("confirm".equalsIgnoreCase(action)) {
				request.setAttribute("subjectToRemove", subject);
				request.setAttribute("studySubs", studySubs);
				request.setAttribute("events", events);
				forwardPage(Page.REMOVE_SUBJECT, request, response);
			} else {
				logger.info("submit to remove the subject");

				getSubjectService().removeSubject(subject, currentUser);

				String emailBody = new StringBuilder("").append(respage.getString("the_subject")).append(" ")
						.append(subject.getUniqueIdentifier()).append(" ")
						.append(respage.getString("has_been_removed_succesfully")).toString();

				addPageMessage(emailBody, request);

				forwardPage(Page.SUBJECT_LIST_SERVLET, request, response);

			}
		}

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}

}
