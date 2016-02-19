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
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Removes a study subject and all the related data
 * 
 * @author jxu
 * 
 */
@Component
public class RemoveStudySubjectServlet extends Controller {
	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
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

		String studySubIdString = request.getParameter("id"); // studySubjectId
		String subIdString = request.getParameter("subjectId");
		String studyIdString = request.getParameter("studyId");

		SubjectDAO sdao = getSubjectDAO();
		StudySubjectDAO subdao = getStudySubjectDAO();
		StudyParameterValueDAO spvdao = getStudyParameterValueDAO();

		if (StringUtil.isBlank(studySubIdString) || StringUtil.isBlank(subIdString)
				|| StringUtil.isBlank(studyIdString)) {
			addPageMessage(getResPage().getString("please_choose_a_study_subject_to_remove"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
		} else {
			int studyId = Integer.parseInt(studyIdString.trim());
			int studySubId = Integer.parseInt(studySubIdString.trim());
			int subjectId = Integer.parseInt(subIdString.trim());

			SubjectBean subject = (SubjectBean) sdao.findByPK(subjectId);

			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);

			StudyDAO studydao = getStudyDAO();
			StudyBean study = (StudyBean) studydao.findByPK(studyId);
			study.getStudyParameterConfig().setSubjectPersonIdRequired(
					spvdao.findByHandleAndStudy(study.getId(), "subjectPersonIdRequired").getValue());

			checkRoleByUserAndStudy(request, response, currentUser, study.getParentStudyId(), study.getId());

			String action = request.getParameter("action");
			if ("confirm".equalsIgnoreCase(action)) {
				if (!studySub.getStatus().equals(Status.AVAILABLE)) {
					addPageMessage(getResPage().getString("this_subject_is_not_available_for_this_study") + " "
							+ getResPage().getString("please_contact_sysadmin_for_more_information"), request);
					forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
					return;
				}

				request.setAttribute("subject", subject);
				request.setAttribute("subjectStudy", study);
				request.setAttribute("studySub", studySub);
				request.setAttribute("events", getDisplayStudyEventsForStudySubject(studySub, getDataSource(),
						currentUser, currentRole, false));

				forwardPage(Page.REMOVE_STUDY_SUBJECT, request, response);
			} else {
				logger.info("submit to remove the subject from study");

				getStudySubjectService().removeStudySubject(studySub, currentUser);

				String emailBody = new StringBuilder("").append(getResPage().getString("the_subject")).append(" ")
						.append(studySub.getLabel()).append(" ")
						.append((study.isSite(study.getParentStudyId())
								? getResPage().getString("has_been_removed_from_the_site")
								: getResPage().getString("has_been_removed_from_the_study")))
						.append(study.getName()).append(".").toString();

				addPageMessage(emailBody, request);
				forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
			}
		}
	}
}
