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
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Processes request of 'request a user account'
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class RequestAccountServlet extends SecureController {

	@Override
	public void mayProceed() throws InsufficientPermissionException {

	}

	@Override
	public void processRequest() throws Exception {

		String action = request.getParameter("action");

		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		ArrayList studies = (ArrayList) sdao.findAll();
		ArrayList roles = Role.toArrayList();
		roles.remove(Role.SYSTEM_ADMINISTRATOR); // admin is not a user role, only used for
		// tomcat

		request.setAttribute("roles", roles);
		request.setAttribute("studies", studies);

		if (StringUtil.isBlank(action)) {

			session.setAttribute("newUserBean", new UserAccountBean());

			forwardPage(Page.REQUEST_ACCOUNT);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				confirmAccount();

			} else if ("submit".equalsIgnoreCase(action)) {
				submitAccount();
			} else {
				logger.info("here...");
				forwardPage(Page.REQUEST_ACCOUNT);
			}
		}

	}

	/**
	 * 
	 * @param request
	 * @param response
	 */
	private void confirmAccount() throws Exception {
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		v.addValidation("name", Validator.NO_BLANKS);
		v.addValidation("firstName", Validator.NO_BLANKS);
		v.addValidation("lastName", Validator.NO_BLANKS);
		v.addValidation("email", Validator.IS_A_EMAIL);
		v.addValidation("email2", Validator.CHECK_SAME, "email");
		v.addValidation("institutionalAffiliation", Validator.NO_BLANKS);
		v.addValidation("activeStudyId", Validator.IS_AN_INTEGER);
		v.addValidation("activeStudyRole", Validator.IS_VALID_TERM, TermType.ROLE);

		HashMap errors = v.validate();

		FormProcessor fp = new FormProcessor(request);

		UserAccountBean ubForm = getUserBean();
		request.setAttribute("otherStudy", fp.getString("otherStudy"));
		session.setAttribute("newUserBean", ubForm);

		if (!errors.isEmpty()) {
			logger.info("after processing form,error is not empty");
			request.setAttribute("formMessages", errors);
			forwardPage(Page.REQUEST_ACCOUNT);

		} else {
			logger.info("after processing form,no errors");

			sm = new SessionManager(null, ubForm.getName());
			// see whether this user already in the DB
			UserAccountBean ubDB = sm.getUserBean();

			if (StringUtil.isBlank(ubDB.getName())) {
				StudyDAO sdao = new StudyDAO(sm.getDataSource());
				StudyBean study = (StudyBean) sdao.findByPK(ubForm.getActiveStudyId());
				String studyName = study.getName();
				request.setAttribute("studyName", studyName);
				forwardPage(Page.REQUEST_ACCOUNT_CONFIRM);
			} else {

				addPageMessage(respage.getString("your_user_name_used_by_other_try_another"));
				forwardPage(Page.REQUEST_ACCOUNT);
			}

		}

	}

	/**
	 * Gets user basic info and set email to the administrator
	 * 
	 * @param request
	 * @param response
	 */
	private void submitAccount() throws Exception {
		String otherStudy = request.getParameter("otherStudy");
		String studyName = request.getParameter("studyName");
		UserAccountBean ubForm = (UserAccountBean) session.getAttribute("newUserBean");
		logger.info("Sending email...");
		// YW << <<
		StringBuffer email = new StringBuffer("From: " + ubForm.getEmail() + "<br>");
		email.append("Sent: " + new Date() + "<br>");
		email.append("To: " + SQLInitServlet.getField("adminEmail") + "<br>");
		email.append("Subject: Request Account<br><br><br>");
		email.append("Dear Admin, <br><br>");
		email.append(ubForm.getFirstName() + " is requesting an account on the ClinCapture system running at "
				+ SQLInitServlet.getField("sysURL") + ". <br><br>");
		email.append("His/her information is shown as follows: <br><br>");
		email.append(resword.getString("name") + ": " + ubForm.getFirstName() + " " + ubForm.getLastName());
		email.append("<br>" + resword.getString("user_name") + ": " + ubForm.getName());
		email.append("<br>" + resword.getString("email") + ": " + ubForm.getEmail());
		email.append("<br>" + resword.getString("institutional_affiliation") + ": "
				+ ubForm.getInstitutionalAffiliation());
		email.append("<br>" + resword.getString("default_active_study") + ":" + studyName + ", id:"
				+ ubForm.getActiveStudyId());
		email.append("<br>" + resword.getString("other_study") + otherStudy);
		email.append("<br>" + resword.getString("user_role_requested") + ubForm.getActiveStudyRoleName());
		String emailBody = email.toString();
		// YW >>
		logger.info("Sending email...begin" + emailBody);
		sendEmail(EmailEngine.getAdminEmail(), ubForm.getEmail().trim(), "request account", emailBody, false);
		session.removeAttribute("newUserBean");
		forwardPage(Page.LOGIN);
	}

	/**
	 * Constructs userbean from request
	 * 
	 * @param request
	 * @return
	 */
	private UserAccountBean getUserBean() {
		FormProcessor fp = new FormProcessor(request);

		UserAccountBean ubForm = new UserAccountBean();
		ubForm.setName(fp.getString("name"));
		ubForm.setFirstName(fp.getString("firstName"));
		ubForm.setLastName(fp.getString("lastName"));
		ubForm.setEmail(fp.getString("email"));
		ubForm.setInstitutionalAffiliation(fp.getString("institutionalAffiliation"));
		ubForm.setActiveStudyId(fp.getInt("activeStudyId"));
		StudyUserRoleBean uRole = new StudyUserRoleBean();
		uRole.setStudyId(fp.getInt("activeStudyId"));
		uRole.setRole(Role.get(fp.getInt("activeStudyRole")));
		ubForm.addRole(uRole);
		return ubForm;

	}

}
