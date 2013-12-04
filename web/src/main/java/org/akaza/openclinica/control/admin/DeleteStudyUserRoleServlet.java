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

import java.util.Stack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.core.EntityAction;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.navigation.Navigation;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

// allows both deletion and remove/restore of a study user role
@SuppressWarnings({ "unchecked", "serial" })
@Component
public class DeleteStudyUserRoleServlet extends Controller {
	public static final String PATH = "DeleteStudyUserRole";
	public static final String ARG_USERNAME = "userName";
	public static final String ARG_STUDYID = "studyId";
	public static final String ARG_ACTION = "action";

	public static String getLink(String userName, int studyId, EntityAction action) {
		return PATH + "?" + ARG_USERNAME + "=" + userName + "&" + ARG_STUDYID + "=" + studyId + "&" + ARG_ACTION + "="
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

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("you_may_not_perform_administrative_functions"), "1");
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		String message = "";
		String additionalMessage = "";
		Page forwardTo;
		UserAccountDAO udao = new UserAccountDAO(getDataSource());
		Stack<String> visitedURLs = (Stack<String>) request.getSession().getAttribute("visitedURLs");

		if (visitedURLs.pop().startsWith(Page.LIST_USER_ACCOUNTS_SERVLET.getFileName())) {
			forwardTo = Page.LIST_USER_ACCOUNTS_SERVLET;
		} else {
			forwardTo = Page.LIST_USER_IN_STUDY_SERVLET;
		}

		FormProcessor fp = new FormProcessor(request);
		int studyId = fp.getInt(ARG_STUDYID);
		String uName = fp.getString(ARG_USERNAME);
		UserAccountBean user = (UserAccountBean) udao.findByUserName(uName);
		int action = fp.getInt(ARG_ACTION);

		StudyUserRoleBean s = udao.findRoleByUserNameAndStudyId(uName, studyId);

		if (!s.isActive()) {
			message = respage.getString("the_specified_user_role_not_exits_for_study");
		} else if (!EntityAction.contains(action)) {
			message = respage.getString("the_specified_action_is_invalid");
		} else if (!EntityAction.get(action).equals(EntityAction.DELETE)
				&& !EntityAction.get(action).equals(EntityAction.RESTORE)
				&& !EntityAction.get(action).equals(EntityAction.REMOVE)) {
			message = respage.getString("the_specified_action_is_not_allowed");
		} else if (EntityAction.get(action).equals(EntityAction.RESTORE) && user.getStatus().equals(Status.DELETED)) {
			message = respage.getString("the_role_cannot_be_restored_since_user_deleted");
		} else {
			EntityAction desiredAction = EntityAction.get(action);
			if ((ub.isSysAdmin() || ub.getActiveStudyRole().equals(Role.STUDY_ADMINISTRATOR))
					&& desiredAction.equals(EntityAction.DELETE)) {
				if (s.getUserName().equalsIgnoreCase("root") && s.isSysAdmin()) {
					message = respage.getString("you_cannot_remove_the_sysadmin_role_for_the_root_user");
				} else {
					if (s.isSysAdmin()) {
						message = respage.getString("the_user_role_deleted");
						if ((ub.getId() == user.getId()) && (ub.getActiveStudyId() == s.getStudyId())) {
							forwardTo = Page.MENU_SERVLET;
							Navigation.removeUrl(request, "/ListUserAccounts");
							additionalMessage = respage.getString("you_may_not_perform_administrative_functions");
						}
					} else {
						message = respage.getString("the_study_user_role_deleted");
					}
					udao.deleteUserRole(studyId, s.getRole(), user);
				}
			} else {
				if ((ub.isSysAdmin() || ub.getActiveStudyRole().equals(Role.STUDY_ADMINISTRATOR))
						&& desiredAction.equals(EntityAction.REMOVE)) {
					s.setStatus(Status.DELETED);
					message = respage.getString("the_study_user_role_removed");
					if ((ub.getId() == user.getId()) && (ub.getActiveStudyId() == s.getStudyId())) {
						forwardTo = Page.MENU_SERVLET;
						Navigation.removeUrl(request, "/ListUserAccounts");
						additionalMessage = respage.getString("you_may_not_perform_administrative_functions");
					}
				} else if ((ub.isSysAdmin() || ub.getActiveStudyRole().equals(Role.STUDY_ADMINISTRATOR))
						&& desiredAction.equals(EntityAction.RESTORE)) {
					s.setStatus(Status.AVAILABLE);
					message = respage.getString("the_study_user_role_restored");
				}
				s.setUpdater(ub);
				udao.updateStudyUserRole(s, uName);
			}

			sendEmail(request, user, s, desiredAction);
		}
		addPageMessage(message, request);
		if (!additionalMessage.isEmpty()) {
			addPageMessage(additionalMessage, request);
		}
		if (ub.getId() == user.getId()) {
			request.getSession().setAttribute("reloadUserBean", true);
		}
		forwardPage(forwardTo, request, response);
	}

	private void sendEmail(HttpServletRequest request, UserAccountBean u, StudyUserRoleBean sub, EntityAction action)
			throws Exception {

		StudyDAO sdao = new StudyDAO(getDataSource());
		StudyBean study = (StudyBean) sdao.findByPK(sub.getStudyId());
		String subject = null;
		String body = null;
		logger.info("Sending email...");

		if (action.equals(EntityAction.DELETE)) {
			subject = resword.getString("notification_deleting_role");
			body = resword.getString("delete_role_email_message_htm");
		} else if (action.equals(EntityAction.REMOVE)) {
			subject = resword.getString("notification_removing_role");
			body = resword.getString("remove_role_email_message_htm");
		} else if (action.equals(EntityAction.RESTORE)) {
			subject = resword.getString("notification_restoring_role");
			body = resword.getString("restore_role_email_message_htm");
		}

		body = body != null ? body.replace("{0}", u.getFirstName() + " " + u.getLastName()).replace("{1}", u.getName())
				.replace("{2}", study.getName()).replace("{3}", sub.getRoleName()) : null;
		sendEmail(u.getEmail().trim(), subject, body, false, request);

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
