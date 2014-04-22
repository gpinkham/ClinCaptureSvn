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
import org.akaza.openclinica.bean.login.PwdChallengeQuestion;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.filter.OpenClinicaJdbcService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;

/**
 * @author jxu
 * @version CVS: $Id: RequestPasswordServlet.java 9771 2007-08-28 15:26:26Z thickerson $
 * 
 *          Servlet of requesting password
 */
@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class RequestPasswordServlet extends Controller {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		//
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String action = request.getParameter("action");
		request.getSession().setAttribute("challengeQuestions", PwdChallengeQuestion.toArrayList());

		request.setAttribute("previouslySelectedPasswdChallengeQuestion",
				request.getParameter("passwdChallengeQuestion"));

		if (StringUtil.isBlank(action)) {
			request.setAttribute("userBean1", new UserAccountBean());
			forwardPage(Page.REQUEST_PWD, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				confirmPassword(request, response);

			} else {
				request.setAttribute("userBean1", new UserAccountBean());
				forwardPage(Page.REQUEST_PWD, request, response);
			}
		}

	}

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	private void confirmPassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);
		v.addValidation("name", Validator.NO_BLANKS);
		v.addValidation("email", Validator.IS_A_EMAIL);
		v.addValidation("passwdChallengeQuestion", Validator.NO_BLANKS);
		v.addValidation("passwdChallengeAnswer", Validator.NO_BLANKS);

		HashMap errors = v.validate();

		UserAccountBean ubForm = new UserAccountBean(); // user bean from web
		// form
		ubForm.setName(fp.getString("name"));
		ubForm.setEmail(fp.getString("email"));
		ubForm.setPasswdChallengeQuestion(fp.getString("passwdChallengeQuestion"));
		ubForm.setPasswdChallengeAnswer(fp.getString("passwdChallengeAnswer"));

		SessionManager sm = new SessionManager(null, ubForm.getName(),
				SpringServletAccess.getApplicationContext(getServletContext()));
		request.setAttribute(SESSION_MANAGER, sm);

		UserAccountDAO uDAO = getUserAccountDAO();
		// see whether this user in the DB
		UserAccountBean ubDB = (UserAccountBean) uDAO.findByUserName(ubForm.getName());

		request.setAttribute("userBean1", ubForm);
		if (!errors.isEmpty()) {
			logger.info("after processing form,has errors");
			request.setAttribute("formMessages", errors);
			forwardPage(Page.REQUEST_PWD, request, response);
		} else {
			logger.info("after processing form,no errors");
			// whether this user's email is in the DB
			if (ubDB.getEmail() != null && ubDB.getEmail().equalsIgnoreCase(ubForm.getEmail())) {
				logger.info("ubDB.getPasswdChallengeQuestion()" + ubDB.getPasswdChallengeQuestion());
				logger.info("ubForm.getPasswdChallengeQuestion()" + ubForm.getPasswdChallengeQuestion());
				logger.info("ubDB.getPasswdChallengeAnswer()" + ubDB.getPasswdChallengeAnswer());
				logger.info("ubForm.getPasswdChallengeAnswer()" + ubForm.getPasswdChallengeAnswer());

				// if this user's password challenge can be verified
				if (ubDB.getPasswdChallengeQuestion().equals(ubForm.getPasswdChallengeQuestion())
						&& ubDB.getPasswdChallengeAnswer().equalsIgnoreCase(ubForm.getPasswdChallengeAnswer())) {

					SecurityManager scm = getSecurityManager();
					String newPass = scm.genPassword();
					OpenClinicaJdbcService ocService = getOpenClinicaJdbcService();
					String newDigestPass = scm.encrytPassword(newPass, ocService.loadUserByUsername(ubForm.getName()));
					ubDB.setPasswd(newDigestPass);

					ubDB.setPasswdTimestamp(null);
					ubDB.setUpdater(ubDB);
					ubDB.setLastVisitDate(new Date());

					logger.info("user bean to be updated:" + ubDB.getId() + ubDB.getName() + ubDB.getActiveStudyId());

					uDAO.update(ubDB);
					sendPassword(newPass, ubDB, request, response);
				} else {
					addPageMessage(respage.getString("your_password_not_verified_try_again"), request);
					forwardPage(Page.REQUEST_PWD, request, response);
				}

			} else {
				addPageMessage(respage.getString("your_email_address_not_found_try_again"), request);
				forwardPage(Page.REQUEST_PWD, request, response);
			}

		}

	}

	/**
	 * Gets user basic info and set email to the administrator
	 * 
	 */
	private void sendPassword(String passwd, UserAccountBean ubDB, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		StudyDAO sdao = getStudyDAO();
		StudyBean sBean = (StudyBean) sdao.findByPK(ubDB.getActiveStudyId());
		logger.info("Sending email...");

		StudyBean emailParentStudy;
		if (sBean.getParentStudyId() > 0) {
			emailParentStudy = (StudyBean) sdao.findByPK(sBean.getParentStudyId());
		} else {
			emailParentStudy = sBean;
		}
		String emailBody = "Dear " + ubDB.getFirstName() + " " + ubDB.getLastName() + ", <br><br>"
				+ restext.getString("this_email_is_from_openclinica_admin") + "<br>"
				+ restext.getString("your_password_has_been_reset_as") + ": " + passwd + "<br><br>"
				+ restext.getString("you_will_be_required_to_change") + " "
				+ restext.getString("time_you_login_to_the_system") + " "
				+ restext.getString("use_the_following_link_to_log") + ":<br>" + SQLInitServlet.getSystemURL()
				+ "<br><br>" + respage.getString("best_system_administrator");
		emailBody = emailBody.replace("{0}", emailParentStudy.getName());
		sendEmail(ubDB.getEmail().trim(), EmailEngine.getAdminEmail(), restext.getString("your_openclinica_password"),
				emailBody, true, respage.getString("your_password_reset_new_password_emailed"),
				respage.getString("your_password_not_send_due_mail_server_problem"), true, request);

		request.getSession().removeAttribute("challengeQuestions");
		forwardPage(Page.LOGIN, request, response);

	}
}
