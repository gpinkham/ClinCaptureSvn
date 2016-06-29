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

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clinovo.bean.EmailDetails;
import com.clinovo.enums.EmailAction;
import com.clinovo.service.EmailService;
import org.akaza.openclinica.bean.login.PwdChallengeQuestion;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.filter.OpenClinicaJdbcService;
import org.springframework.stereotype.Component;

import com.clinovo.util.EmailUtil;
import com.clinovo.util.ValidatorHelper;

/**
 * @author jxu
 * @version CVS: $Id: RequestPasswordServlet.java 9771 2007-08-28 15:26:26Z thickerson $
 * 
 *          Servlet of requesting password
 */
@SuppressWarnings("rawtypes")
@Component
public class RequestPasswordServlet extends SpringServlet {

	public static final String PASS_CHANGE_QUESTION = "passwdChallengeQuestion";
	public static final String PASS_CHANGE_ANSWER = "passwdChallengeAnswer";
	public static final String CHANGE_QUESTIONS = "challengeQuestions";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		logger.info("Sending reset password request.");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String action = request.getParameter("action");
		request.getSession().setAttribute(CHANGE_QUESTIONS, PwdChallengeQuestion.toArrayList());

		request.setAttribute("previouslySelectedPasswdChallengeQuestion",
				request.getParameter(PASS_CHANGE_QUESTION));

		if (StringUtil.isBlank(action) || !"confirm".equalsIgnoreCase(action)) {
			request.setAttribute("userBean1", new UserAccountBean());
			forwardPage(Page.REQUEST_PWD, request, response);
			return;
		}

		confirmPassword(request, response);
	}

	/**
	 * Confirm password.
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws java.lang.Exception in case if error occur while validation.
	 */
	public void confirmPassword(HttpServletRequest request, HttpServletResponse response) throws Exception {

		HashMap errors = validateReuqestAttributes(request);
		UserAccountBean ubForm = getUserAccountFromRequest(request);

		SessionManager sm = getSessionManager(ubForm.getName());
		request.setAttribute(SESSION_MANAGER, sm);

		UserAccountBean ubDB = (UserAccountBean) getUserAccountDAO().findByUserName(ubForm.getName());
		request.setAttribute("userBean1", ubForm);

		if (!errors.isEmpty()) {
			request.setAttribute("formMessages", errors);
			forwardPage(Page.REQUEST_PWD, request, response);
			return;
		}
		if (ubDB.getEmail() == null || !ubDB.getEmail().equalsIgnoreCase(ubForm.getEmail())) {
			addPageMessage(getResPage().getString("your_email_address_not_found_try_again"), request);
			forwardPage(Page.REQUEST_PWD, request, response);
			return;
		}
		if (!ubDB.getPasswdChallengeQuestion().equals(ubForm.getPasswdChallengeQuestion())
				|| !ubDB.getPasswdChallengeAnswer().equalsIgnoreCase(ubForm.getPasswdChallengeAnswer())) {
			addPageMessage(getResPage().getString("your_password_not_verified_try_again"), request);
			forwardPage(Page.REQUEST_PWD, request, response);
			return;
		}
		SecurityManager scm = getSecurityManager();
		String newPass = scm.genPassword();
		OpenClinicaJdbcService ocService = getOpenClinicaJdbcService();
		String newDigestPass = scm.encryptPassword(newPass, ocService.loadUserByUsername(ubForm.getName()));

		ubDB.setPasswd(newDigestPass);
		ubDB.setPasswdTimestamp(null);
		ubDB.setUpdater(ubDB);
		ubDB.setLastVisitDate(new Date());

		getUserAccountDAO().update(ubDB);
		sendPassword(newPass, ubDB, request, response);
	}

	/**
	 * Get Session Manager.
	 * @param userName String
	 * @return Session manager
	 * @throws SQLException in case is error will be thrown while session manager creation.
	 */
	public SessionManager getSessionManager(String userName) throws SQLException {
		return new SessionManager(null, userName,
				SpringServletAccess.getApplicationContext(getServletContext()));
	}

	private HashMap validateReuqestAttributes(HttpServletRequest request) {
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		v.addValidation("name", Validator.NO_BLANKS);
		v.addValidation("email", Validator.IS_A_EMAIL);
		v.addValidation(PASS_CHANGE_QUESTION, Validator.NO_BLANKS);
		v.addValidation(PASS_CHANGE_ANSWER, Validator.NO_BLANKS);
		return v.validate();
	}

	/**
	 * Get UserAccountBean from request.
	 * @param request HttpServletRequest.
	 * @return UserAccountBean.
	 */
	public UserAccountBean getUserAccountFromRequest(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		UserAccountBean user = new UserAccountBean();
		user.setName(fp.getString("name"));
		user.setEmail(fp.getString("email"));
		user.setPasswdChallengeQuestion(fp.getString(PASS_CHANGE_QUESTION));
		user.setPasswdChallengeAnswer(fp.getString(PASS_CHANGE_ANSWER));
		return user;
	}

	/**
	 * Gets user basic info and set email to the administrator.
	 * @param passwd String
	 * @param ubDB UserAccountBean
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws Exception in case if system is unable to send email.
	 */
	public void sendPassword(String passwd, UserAccountBean ubDB, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		StudyBean studyBean = (StudyBean) getStudyDAO().findByPK(ubDB.getActiveStudyId());

		if (studyBean.getParentStudyId() > 0) {
			studyBean = (StudyBean) getStudyDAO().findByPK(studyBean.getParentStudyId());
		}
		String emailBody = EmailUtil.getEmailBodyStart() + "Dear " + ubDB.getFirstName() + " " + ubDB.getLastName()
				+ ", <br><br>" + getResText().getString("this_email_is_from_clincapture_admin") + "<br>"
				+ getResText().getString("your_password_has_been_reset_as") + ": " + passwd + "<br><br>"
				+ getResText().getString("you_will_be_required_to_change") + " "
				+ getResText().getString("time_you_login_to_the_system") + " "
				+ getResText().getString("use_the_following_link_to_log") + ":<br>" + SQLInitServlet.getSystemURL()
				+ "<br><br>" + getResPage().getString("best_system_administrator") + EmailUtil.getEmailBodyEnd()
				+ EmailUtil.getEmailFooter(CoreResources.getSystemLocale());
		emailBody = emailBody.replace("{0}", studyBean.getName());

		EmailDetails emailDetails = new EmailDetails();
		emailDetails.setMessage(emailBody);
		emailDetails.setStudyId(studyBean.getId());
		emailDetails.setSubject(getResText().getString("your_clincapture_password"));
		emailDetails.setTo(ubDB.getEmail().trim());
		emailDetails.setSentBy(ubDB.getId());
		emailDetails.setAction(EmailAction.REQUEST_PASSWORD);
		EmailService emailService = getEmailService();
		try {
			emailService.sendEmail(emailDetails);
		} catch (Exception ex) {
			logger.error("Unable to send request password email: " + ex.getMessage());
		}
		request.getSession().removeAttribute(CHANGE_QUESTIONS);
		forwardPage(Page.LOGIN, request, response);
	}
}
