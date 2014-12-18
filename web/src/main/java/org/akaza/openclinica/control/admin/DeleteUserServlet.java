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

// allows both deletion and restoration of a study user role
@SuppressWarnings("serial")
@Component
public class DeleteUserServlet extends Controller {

	public static final String PATH = "DeleteUser";
	public static final String ARG_USERID = "userId";
	public static final String ARG_ACTION = "action";

	public static String getLink(UserAccountBean u, EntityAction action) {
		return PATH + "?" + ARG_USERID + "=" + u.getId() + "&" + "&" + ARG_ACTION + "=" + action.getId();
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (!ub.isSysAdmin()) {
			addPageMessage(
					respage.getString("no_have_correct_privilege_current_study")
							+ respage.getString("change_study_contact_sysadmin"), request);
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		UserAccountDAO udao = getUserAccountDAO();

		FormProcessor fp = new FormProcessor(request);
		int userId = fp.getInt(ARG_USERID);
		int action = fp.getInt(ARG_ACTION);

		UserAccountBean u = (UserAccountBean) udao.findByPK(userId);

		MessageFormat messageFormat = new MessageFormat("");

		String message;
		if (!u.isActive()) {

			messageFormat.applyPattern(respage.getString("the_specified_user_not_exits"));
			message = messageFormat.format(new Object[] { userId });

		} else if (!EntityAction.contains(action)) {
			message = respage.getString("the_specified_action_on_the_user_is_invalid");
		} else if (!EntityAction.get(action).equals(EntityAction.DELETE)
				&& !EntityAction.get(action).equals(EntityAction.RESTORE)) {
			message = respage.getString("the_specified_action_is_not_allowed");
		} else {
			EntityAction desiredAction = EntityAction.get(action);
			u.setUpdater(ub);

			if (desiredAction.equals(EntityAction.DELETE)) {
				udao.delete(u);

				if (udao.isQuerySuccessful()) {
					message = respage.getString("the_user_has_been_removed_successfully");

					try {
						sendRestoreEmail(request, u, null, desiredAction);
					} catch (Exception e) {
						message += respage.getString("however_was_error_sending_user_email_regarding");
					}
				} else {
					message = respage.getString("the_user_could_not_be_deleted_due_database_error");
				}
			} else {
				SecurityManager sm = getSecurityManager();
				String password = sm.genPassword();
				String passwordHash = sm.encryptPassword(password, getUserDetails());

				u.setPasswd(passwordHash);
				u.setPasswdTimestamp(null);

				udao.restore(u);

				if (udao.isQuerySuccessful()) {
					message = respage.getString("the_user_has_been_restored");

					try {
						sendRestoreEmail(request, u, password, desiredAction);
					} catch (Exception e) {
						message += respage.getString("however_was_error_sending_user_email_regarding");
					}
				} else {
					message = respage.getString("the_user_could_not_be_deleted_due_database_error");
				}
			}
		}

		addPageMessage(message, request);
		forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET, request, response);
	}

	private void sendRestoreEmail(HttpServletRequest request, UserAccountBean u, String password,
			EntityAction desiredAction) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);
		String body = "";
		String subject = "";
		StudyDAO sdao = getStudyDAO();
		StudyBean emailParentStudy;
		Object arguments[] = {};
		MessageFormat msg = new MessageFormat("");

		if (currentStudy.getParentStudyId() > 0) {
			emailParentStudy = (StudyBean) sdao.findByPK(currentStudy.getParentStudyId());
		} else {
			emailParentStudy = currentStudy;
		}

		if (desiredAction.equals(EntityAction.DELETE)) {
			logger.info("Sending remove account notification to " + u.getName());
			subject = restext.getString("your_clin_capture_account_has_been_removed");
			msg.applyPattern(restext.getString("your_account_has_been_removed_email_message_html"));
			arguments = new Object[] { u.getFirstName() + " " + u.getLastName(), u.getName(),
					emailParentStudy.getName() };
		} else if (desiredAction.equals(EntityAction.RESTORE)) {
			logger.info("Sending restore and password reset notification to " + u.getName());
			subject = restext.getString("your_new_openclinica_account_has_been_restored");
			msg.applyPattern(restext.getString("your_account_has_been_restored_and_password_reset_email_message_html"));
			arguments = new Object[] { u.getFirstName() + " " + u.getLastName(), u.getName(), password,
					SQLInitServlet.getSystemURL(), emailParentStudy.getName() };
		}

		body = msg.format(arguments);
		logger.info("Sending email...begin");
		sendEmail(u.getEmail().trim(), subject, body, false, request);
		logger.info("Sending email...done");
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
