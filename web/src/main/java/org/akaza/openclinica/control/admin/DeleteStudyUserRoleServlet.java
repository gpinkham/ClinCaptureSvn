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
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.text.MessageFormat;
import java.util.Stack;

// allows both deletion and remove/restore of a study user role
@SuppressWarnings({ "unchecked", "serial" })
@Component
public class DeleteStudyUserRoleServlet extends Controller {
	public static final String PATH = "DeleteStudyUserRole";
	public static final String ARG_USERID = "userId";
	public static final String ARG_STUDYID = "studyId";
	public static final String ARG_ACTION = "action";

	public static String getLink(int userId, int studyId, EntityAction action) {
		return PATH + "?" + ARG_USERID + "=" + userId + "&" + ARG_STUDYID + "=" + studyId + "&" + ARG_ACTION + "="
				+ action.getId();
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("you_may_not_perform_administrative_functions"), "1");
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		StringBuilder message = new StringBuilder("");
		Page forwardTo;

		Stack<String> visitedURLs = (Stack<String>) request.getSession().getAttribute("visitedURLs");

		if (visitedURLs.pop().startsWith(Page.LIST_USER_ACCOUNTS_SERVLET.getFileName())) {
			forwardTo = Page.LIST_USER_ACCOUNTS_SERVLET;
		} else {
			forwardTo = Page.LIST_USER_IN_STUDY_SERVLET;
		}

		FormProcessor fp = new FormProcessor(request);
		int studyId = fp.getInt(ARG_STUDYID);
		int userId = fp.getInt(ARG_USERID);
		int actionId = fp.getInt(ARG_ACTION);

		UserAccountDAO udao = getUserAccountDAO();
		UserAccountBean user = (UserAccountBean) udao.findByPK(userId);
		StudyUserRoleBean studyUserRole = udao.findRoleByUserNameAndStudyId(user.getName(), studyId);
		UserAccountBean currentUser = getUserAccountBean(request);
		EntityAction actionSpecified = EntityAction.get(actionId);

		boolean operationSucceeded = getUserAccountService().performActionOnStudyUserRole(userId, studyId, actionId,
				currentUser, message, respage);

		if (operationSucceeded) {

			sendEmail(request, user, studyUserRole, actionSpecified);
			if (ub.getId() == user.getId()) {
				request.getSession().setAttribute("reloadUserBean", true);
			}
		}

		addPageMessage(message.toString(), request);
		forwardPage(forwardTo, request, response);
	}

	private void sendEmail(HttpServletRequest request, UserAccountBean user, StudyUserRoleBean studyUserRole,
			EntityAction actionSpecified) throws Exception {

		StudyBean study = (StudyBean) getStudyDAO().findByPK(studyUserRole.getStudyId());
		String subject = "";
		String body;
		MessageFormat msg = new MessageFormat("");

		if (actionSpecified.equals(EntityAction.DELETE)) {
			subject = restext.getString("notification_deleting_role");
			msg.applyPattern(restext.getString("delete_role_email_message_htm"));
		} else if (actionSpecified.equals(EntityAction.REMOVE)) {
			subject = restext.getString("notification_removing_role");
			msg.applyPattern(restext.getString("remove_role_email_message_htm"));
		} else if (actionSpecified.equals(EntityAction.RESTORE)) {
			subject = restext.getString("notification_restoring_role");
			msg.applyPattern(restext.getString("restore_role_email_message_htm"));
		}

		body = msg.format(new Object[] { user.getFirstName() + " " + user.getLastName(),
				CoreResources.getField("sysURL.base"), study.getName(), user.getName(), studyUserRole.getRoleName() });
		sendEmail(user.getEmail().trim(), subject, body, false, request);

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
