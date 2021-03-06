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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.hibernate.PasswordRequirementsDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.ValidatorHelper;
import com.clinovo.validator.PasswordValidator;

/**
 * Reset expired password
 * 
 * @author ywang
 */
@Component
@SuppressWarnings("unused")
public class ResetPasswordServlet extends SpringServlet {

	private static final long serialVersionUID = -5259201015824317949L;

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		//
	}

	/**
	 * Tasks include:
	 * <ol>
	 * <li>Validation:
	 * <ol>
	 * <li>1. old password match database record
	 * <li>2. new password is follows requirements
	 * <li>4. two times entered passwords are same
	 * <li>5. all required fields are filled
	 * </ol>
	 * <li>Update ub - UserAccountBean - in session and database
	 * </ol>
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		logger.info("Change expired password");

		UserAccountDAO udao = new UserAccountDAO(getDataSource());
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		HashMap errors = new HashMap();
		FormProcessor fp = new FormProcessor(request);
		String mustChangePwd = request.getParameter("mustChangePwd");
		String newPwd = fp.getString("passwd").trim();
		String passwdChallengeQ = fp.getString("passwdChallengeQ");
		String passwdChallengeA = fp.getString("passwdChallengeA");

		if ("yes".equalsIgnoreCase(mustChangePwd)) {
			addPageMessage(getResPage().getString("your_password_has_expired_must_change"), request);
		} else {
			addPageMessage(getResPage().getString("password_expired") + " "
					+ getResPage().getString("if_you_do_not_want_change_leave_blank"), request);
		}
		request.setAttribute("mustChangePass", mustChangePwd);

		String oldPwd = fp.getString("oldPasswd").trim();
		UserAccountBean ubForm = new UserAccountBean(); // user bean from web
		// form
		ubForm.setPasswd(oldPwd);
		ubForm.setPasswdChallengeQuestion(passwdChallengeQ);
		ubForm.setPasswdChallengeAnswer(passwdChallengeA);
		request.setAttribute("userBean1", ubForm);

		SecurityManager sm = getSecurityManager();
		if (!sm.isPasswordValid(ub.getPasswd(), oldPwd, getUserDetails())) {
			Validator.addError(errors, "oldPasswd", getResException().getString("wrong_old_password"));
			request.setAttribute("formMessages", errors);

			forwardPage(Page.RESET_PASSWORD, request, response);
		} else {
			if (mustChangePwd.equalsIgnoreCase("yes")) {
				v.addValidation("passwd", Validator.NO_BLANKS);
				v.addValidation("passwd1", Validator.NO_BLANKS);
				v.addValidation("passwdChallengeQ", Validator.NO_BLANKS);
				v.addValidation("passwdChallengeA", Validator.NO_BLANKS);
				v.addValidation("passwd", Validator.CHECK_DIFFERENT, "oldPasswd");
			}

			String newDigestPass = sm.encryptPassword(newPwd, getUserDetails());

			List<String> pwdErrors = new ArrayList<String>();

			if (!StringUtils.isEmpty(newPwd)) {
				v.addValidation("passwd", Validator.IS_A_PASSWORD);
				v.addValidation("passwd1", Validator.CHECK_SAME, "passwd");

				ConfigurationDao configurationDao = getConfigurationDao();

				PasswordRequirementsDao passwordRequirementsDao = new PasswordRequirementsDao(configurationDao);

				Locale locale = LocaleResolver.getLocale(request);
				ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle(locale);

				pwdErrors = PasswordValidator.validatePassword(passwordRequirementsDao, udao, ub.getId(), newPwd,
						newDigestPass, resexception);

			}
			errors = v.validate();
			for (String err : pwdErrors) {
				Validator.addError(errors, "passwd", err);
			}

			if (!errors.isEmpty()) {
				logger.info("ResetPassword page has validation errors");
				request.setAttribute("formMessages", errors);
				forwardPage(Page.RESET_PASSWORD, request, response);
			} else {
				logger.info("ResetPassword page has no errors");

				if (!StringUtils.isBlank(newPwd)) {
					ub.setPasswd(newDigestPass);
					ub.setPasswdTimestamp(new Date());
				} else if ("no".equalsIgnoreCase(mustChangePwd)) {
					ub.setPasswdTimestamp(new Date());
				}
				ub.setOwner(ub);
				ub.setUpdater(ub);// when update ub, updator id is required
				ub.setPasswdChallengeQuestion(passwdChallengeQ);
				ub.setPasswdChallengeAnswer(passwdChallengeA);
				udao.update(ub);

				ArrayList<String> pageMessages = new ArrayList<String>();
				request.setAttribute(PAGE_MESSAGE, pageMessages);
				addPageMessage(getResPage().getString("your_expired_password_reset_successfully"), request);
				ub.incNumVisitsToMainMenu();
				forwardPage(Page.MENU_SERVLET, request, response);
			}
		}

	}

}
