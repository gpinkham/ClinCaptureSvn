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

import org.akaza.openclinica.bean.core.EntityAction;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.Date;

// allows both deletion and restoration of a study user role
@SuppressWarnings({ "serial" })
@Component
public class UnLockUserServlet extends Controller {

	public static final String PATH = "DeleteUser";
	public static final String ARG_USERID = "userId";
	public static final String ARG_ACTION = "action";

	public static String getLink(UserAccountBean u, EntityAction action) {
		return PATH + "?" + ARG_USERID + "=" + u.getId() + "&" + "&" + ARG_ACTION + "=" + action.getId();
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		if (!getUserAccountBean(request).isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountDAO udao = getUserAccountDAO();

		FormProcessor fp = new FormProcessor(request);
		int userId = fp.getInt(ARG_USERID);

		UserAccountBean user = (UserAccountBean) udao.findByPK(userId);

		MessageFormat messageFormat = new MessageFormat("");
		Object[] argsForMessage = { user.getName() };

		String message;

		if (!user.isActive()) {

			messageFormat.applyPattern(respage.getString("the_specified_user_not_exits"));
			message = messageFormat.format(new Object[] { userId });

		} else if (user.getAccountNonLocked()) {

			messageFormat.applyPattern(respage.getString("the_specified_user_not_locked"));
			message = messageFormat.format(argsForMessage);

		} else if (!getUserAccountService().doesUserHaveAvailableRole(user.getId())) {

			messageFormat.applyPattern(respage.getString("the_user_could_not_be_unlocked_since_no_active_role"));
			message = messageFormat.format(argsForMessage);

		} else {

			user.setUpdater(getUserAccountBean(request));

			SecurityManager sm = getSecurityManager();
			String password = sm.genPassword();
			String passwordHash = sm.encryptPassword(password, getUserDetails());

			user.setPasswd(passwordHash);
			user.setPasswdTimestamp(null);
			user.setAccountNonLocked(Boolean.TRUE);
			user.setStatus(Status.AVAILABLE);
			user.setLockCounter(0);
			user.setEnabled(true);

			Date currDate = new Date();
			user.setLastVisitDate(currDate);

			udao.update(user);

			if (udao.isQuerySuccessful()) {

				messageFormat.applyPattern(respage.getString("the_user_has_been_unlocked"));
				message = messageFormat.format(argsForMessage);

				try {
					sendRestoreEmail(user, password, request);
				} catch (Exception e) {
					e.printStackTrace();
					message += respage.getString("however_was_error_sending_user_email_regarding");
				}

			} else {

				messageFormat.applyPattern(respage.getString("the_user_could_not_be_unlocked_due_database_error"));
				message = messageFormat.format(argsForMessage);

			}
		}

		addPageMessage(message, request);
		forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET, request, response);
	}

	private void sendRestoreEmail(UserAccountBean u, String password, HttpServletRequest request) throws Exception {
		logger.info("Sending restore and password reset notification to " + u.getName());
		StudyBean currentStudy = getCurrentStudy(request);

		String body = resword.getString("dear") + u.getFirstName() + " " + u.getLastName() + ",<br><br>";
		body += restext.getString("your_account_has_been_unlocked_and_password_reset") + ":<br><br>";
		body += resword.getString("user_name") + ": " + u.getName() + "<br>";
		body += resword.getString("password") + ": " + password + "<br><br>";
		body += restext.getString("please_test_your_login_information_and_let") + "<br>";
		body += "<A HREF='" + SQLInitServlet.getSystemURL() + "'>";
		body += SQLInitServlet.getField("sysURL") + "</A> <br><br>";
		StudyDAO sdao = getStudyDAO();
		StudyBean emailParentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			emailParentStudy = (StudyBean) sdao.findByPK(currentStudy.getParentStudyId());
		} else {
			emailParentStudy = currentStudy;
		}
		body += respage.getString("best_system_administrator").replace("{0}", emailParentStudy.getName());
		logger.info("Sending email...begin");
		sendEmail(u.getEmail().trim(), restext.getString("your_new_openclinica_account_has_been_restored"), body,
				false, request);
		logger.info("Sending email...done");
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
