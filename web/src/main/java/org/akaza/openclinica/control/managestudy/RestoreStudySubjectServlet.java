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
package org.akaza.openclinica.control.managestudy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Restores a removed subject to a study.
 * 
 * @author jxu
 * 
 */
@Component
public class RestoreStudySubjectServlet extends SpringServlet {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);
		checkStudyLocked(Page.LIST_STUDY_SUBJECTS_SERVLET, getResPage().getString("current_study_locked"), request,
				response);
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS_SERVLET, getResPage().getString("current_study_frozen"), request,
				response);
		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)) {
			return;
		}

		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
				getResException().getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		UserAccountBean currentUser = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		String studySubIdString = request.getParameter("id");
		String subIdString = request.getParameter("subjectId");
		String studyIdString = request.getParameter("studyId");

		SubjectDAO sdao = getSubjectDAO();
		StudySubjectDAO subdao = getStudySubjectDAO();

		if (StringUtil.isBlank(studySubIdString) || StringUtil.isBlank(subIdString)
				|| StringUtil.isBlank(studyIdString)) {
			addPageMessage(getResPage().getString("please_choose_study_subject_to_restore"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
		} else {
			int studySubId = Integer.parseInt(studySubIdString.trim());
			int subjectId = Integer.parseInt(subIdString.trim());

			SubjectBean subject = (SubjectBean) sdao.findByPK(subjectId);

			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);

			StudyDAO studydao = getStudyDAO();
			StudyBean subjectStudy = (StudyBean) studydao.findByPK(studySub.getStudyId());
			getStudyConfigService().setParametersForStudy(subjectStudy);

			String action = request.getParameter("action");
			if ("confirm".equalsIgnoreCase(action)) {
				if (studySub.getStatus().equals(Status.AVAILABLE)) {
					addPageMessage(getResPage().getString("this_subject_is_already_available_for_study") + " "
							+ getResPage().getString("please_contact_sysadmin_for_more_information"), request);
					forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
					return;
				}

				request.setAttribute("subject", subject);
				request.setAttribute("subjectStudy", subjectStudy);
				request.setAttribute("studySub", studySub);
				request.setAttribute("events", getDisplayStudyEventsForStudySubject(studySub, currentUser, currentRole, false));

				forwardPage(Page.RESTORE_STUDY_SUBJECT, request, response);
			} else {
				logger.info("submit to restore the subject from study");

				getStudySubjectService().restoreStudySubject(studySub, currentUser);

				String emailBody = new StringBuilder("").append(getResPage().getString("the_subject")).append(" ")
						.append(studySub.getLabel()).append(" ")
						.append((subjectStudy.isSite()
								? getResPage().getString("has_been_restored_to_the_site")
								: getResPage().getString("has_been_restored_to_the_study")))
						.append(" ").append(subjectStudy.getName()).append(".").toString();

				addPageMessage(emailBody, request);

				forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
			}
		}
	}
}
