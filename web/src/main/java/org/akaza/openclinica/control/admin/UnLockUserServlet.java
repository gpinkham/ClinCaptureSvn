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
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;

import java.util.Date;
import java.util.Locale;

// allows both deletion and restoration of a study user role
@SuppressWarnings({ "serial" })
public class UnLockUserServlet extends SecureController {

	Locale locale;

	public static final String PATH = "DeleteUser";
	public static final String ARG_USERID = "userId";
	public static final String ARG_ACTION = "action";

	public static String getLink(UserAccountBean u, EntityAction action) {
		return PATH + "?" + ARG_USERID + "=" + u.getId() + "&" + "&" + ARG_ACTION + "=" + action.getId();
	}

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (!ub.isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}

		return;
	}

	@Override
	protected void processRequest() throws Exception {
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());

		FormProcessor fp = new FormProcessor(request);
		int userId = fp.getInt(ARG_USERID);

		UserAccountBean u = (UserAccountBean) udao.findByPK(userId);

		String message;
		if (!u.isActive() || u.getAccountNonLocked()) {
			message = respage.getString("the_specified_user_not_exits");
		} else {
			u.setUpdater(ub);

			SecurityManager sm = ((SecurityManager) SpringServletAccess.getApplicationContext(context).getBean(
					"securityManager"));
			String password = sm.genPassword();
			String passwordHash = sm.encrytPassword(password, getUserDetails());

			u.setPasswd(passwordHash);
			u.setPasswdTimestamp(null);
			u.setAccountNonLocked(Boolean.TRUE);
			u.setStatus(Status.AVAILABLE);
			u.setLockCounter(0);

			Date currDate = new Date();
			u.setLastVisitDate(currDate);

			udao.update(u);

			if (udao.isQuerySuccessful()) {
				message = respage.getString("the_user_has_been_unlocked");

				try {
					sendRestoreEmail(u, password);
				} catch (Exception e) {
					e.printStackTrace();
					message += respage.getString("however_was_error_sending_user_email_regarding");
				}
			} else {
				message = respage.getString("the_user_could_not_be_deleted_due_database_error");
			}
		}

		addPageMessage(message);
		forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
	}

	@SuppressWarnings("rawtypes")
	private void sendRestoreEmail(UserAccountBean u, String password) throws Exception {
		logger.info("Sending restore and password reset notification to " + u.getName());

		String body = resword.getString("dear") + u.getFirstName() + " " + u.getLastName() + ",<br><br>";
		body += restext.getString("your_account_has_been_unlocked_and_password_reset") + ":<br><br>";
		body += resword.getString("user_name")+ ": " + u.getName() + "<br>";
		body += resword.getString("password")+ ": " + password + "<br><br>";
		body += restext.getString("please_test_your_login_information_and_let") + "<br>";
		body += "<A HREF='" + SQLInitServlet.getSystemURL() + "'>";
		body += SQLInitServlet.getField("sysURL") + "</A> <br><br>";
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		StudyBean emailParentStudy = new StudyBean();
		if (currentStudy.getParentStudyId() > 0) {
			emailParentStudy = (StudyBean) sdao.findByPK(currentStudy.getParentStudyId());
		} else {
			emailParentStudy = currentStudy;
		}
		body += respage.getString("best_system_administrator").replace("{0}", emailParentStudy.getName());
		logger.info("Sending email...begin");
		sendEmail(u.getEmail().trim(), restext.getString("your_new_openclinica_account_has_been_restored"), body, false);
		logger.info("Sending email...done");
	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}
}
