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
package org.akaza.openclinica.control.login;

import com.clinovo.util.ValidatorHelper;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings({ "rawtypes", "serial" })
public class RequestStudyServlet extends SecureController {
	@Override
	public void mayProceed() throws InsufficientPermissionException {

	}

	@Override
	public void processRequest() throws Exception {

		String action = request.getParameter("action");
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		ArrayList studies = sdao.findAllByStatus(Status.AVAILABLE);
		ArrayList roles = Role.toArrayList();
		roles.remove(Role.SYSTEM_ADMINISTRATOR); // admin is not a user role, only used for
		// tomcat

		StudyUserRoleBean newRole = new StudyUserRoleBean();
		StudyBean requestedStudy = studies.size() > 0 ? (StudyBean) studies.get(0) : null;
		Integer requestedStudyId = request.getParameter("requestedStudyId") == null
				|| request.getParameter("requestedStudyId").trim().isEmpty() ? null : Integer.parseInt(request
				.getParameter("requestedStudyId"));
		if (requestedStudyId != null) {
			action = null;
			newRole.setStudyId(requestedStudyId);
			requestedStudy = (StudyBean) sdao.findByPK(requestedStudyId);
		}
		request.setAttribute("isThisStudy", requestedStudy != null ? !(requestedStudy.getParentStudyId() > 0) : false);
		request.setAttribute("roles", requestedStudy == null ? new ArrayList() : roles);
		request.setAttribute("studies", studies);

		if (StringUtil.isBlank(action)) {
			request.setAttribute("newRole", newRole);
			forwardPage(Page.REQUEST_STUDY);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				confirm();

			} else if ("submit".equalsIgnoreCase(action)) {
				submit();
			} else {
				logger.info("here...");
				forwardPage(Page.REQUEST_STUDY);
			}
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 */
	private void confirm() throws Exception {
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		v.addValidation("studyId", Validator.IS_AN_INTEGER);
		v.addValidation("studyRoleId", Validator.IS_VALID_TERM, TermType.ROLE);

		HashMap errors = v.validate();
		FormProcessor fp = new FormProcessor(request);
		StudyUserRoleBean newRole = new StudyUserRoleBean();
		if (fp.getInt("studyRoleId") > 0) {
			newRole.setRole(Role.get(fp.getInt("studyRoleId")));
		}
		newRole.setStudyId(fp.getInt("studyId"));
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		StudyBean studyRequested = (StudyBean) sdao.findByPK(newRole.getStudyId());
		newRole.setStudyName(studyRequested.getName());
		session.setAttribute("newRole", newRole);
		if (!errors.isEmpty()) {
			logger.info("after processing form,error is not empty");
			request.setAttribute("formMessages", errors);

			forwardPage(Page.REQUEST_STUDY);

		} else {
			logger.info("after processing form,no errors");

			forwardPage(Page.REQUEST_STUDY_CONFIRM);
		}

	}

	/**
	 * Gets user basic info and set email to the administrator
	 * 
	 * @param request
	 * @param response
	 */
	private void submit() throws Exception {
		StudyUserRoleBean newRole = (StudyUserRoleBean) session.getAttribute("newRole");

		logger.info("Sending email...");
		StringBuffer email = new StringBuffer(restext.getString("dear_openclinica_administrator") + ", <br>");
		email.append(ub.getFirstName() + restext.getString("request_to_acces_the_following_study") + ": <br>");
		email.append(resword.getString("user_full_name") + ": " + ub.getFirstName() + " " + ub.getLastName());
		email.append("<br>" + resword.getString("username2") + ": " + ub.getName());
		email.append("<br>" + resword.getString("email") + ": " + ub.getEmail());
		email.append("<br>" + resword.getString("study_requested") + ":" + newRole.getStudyName() + ", id:"
				+ newRole.getStudyId());
		email.append("<br>" + resword.getString("user_role_requested") + ": " + newRole.getRole().getDescription());
		String emailBody = email.toString();
		logger.info("Sending email...begin" + emailBody);

		sendEmail(EmailEngine.getAdminEmail(), ub.getEmail().trim(), "request study access", emailBody, false);

		session.removeAttribute("newRole");
		forwardPage(Page.MENU);
	}

}
