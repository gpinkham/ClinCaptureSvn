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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.Date;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * Removes a study user role
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class RemoveStudyUserRoleServlet extends SecureController {
	/**
	 * Checks whether the user has the right permission to proceed function
	 */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.LIST_USER_IN_STUDY_SERVLET,
				resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest() throws Exception {

		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		String name = request.getParameter("name");
		String studyIdString = request.getParameter("studyId");
		if (StringUtil.isBlank(name) || StringUtil.isBlank(studyIdString)) {
			addPageMessage(respage.getString("please_choose_a_user_to_remove_his_role"));
			forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);
		} else {
			String action = request.getParameter("action");
			UserAccountBean user = (UserAccountBean) udao.findByUserName(name);
			if ("confirm".equalsIgnoreCase(action)) {
				int studyId = Integer.valueOf(studyIdString.trim()).intValue();

				request.setAttribute("user", user);

				StudyUserRoleBean uRole = udao.findRoleByUserNameAndStudyId(name, studyId);
				request.setAttribute("uRole", uRole);

				StudyDAO sdao = new StudyDAO(sm.getDataSource());
				StudyBean study = (StudyBean) sdao.findByPK(studyId);
				request.setAttribute("uStudy", study);
				forwardPage(Page.REMOVE_USER_ROLE_IN_STUDY);
			} else {
				// remove role
				FormProcessor fp = new FormProcessor(request);
				String userName = fp.getString("name");
				int studyId = fp.getInt("studyId");
				int roleId = fp.getInt("roleId");
				StudyUserRoleBean sur = new StudyUserRoleBean();
				sur.setName(userName);
				sur.setRole(Role.get(roleId));
				sur.setStudyId(studyId);
				sur.setStatus(Status.DELETED);
				sur.setUpdater(ub);
				sur.setUpdatedDate(new Date());
				udao.updateStudyUserRole(sur, userName);

				addPageMessage(sendEmail(user, sur));

				forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);

			}

		}
	}

	/**
	 * Send email to the user, director and administrator
	 * 
	 * @param request
	 * @param response
	 */
	private String sendEmail(UserAccountBean u, StudyUserRoleBean sub) throws Exception {

		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		StudyBean study = (StudyBean) sdao.findByPK(sub.getStudyId());
		logger.info("Sending email...");
		String body = u.getFirstName() + " " + u.getLastName() + "(" + resword.getString("username") + ": "
				+ u.getName() + ") " + respage.getString("has_been_removed_from_the_study_site") + study.getName()
				+ " " + respage.getString("with_role") + " " + sub.getRoleName() + ". "
				+ respage.getString("the_user_will_no_longer_access_to_the_study");
		// Mantis Issue: 5768. Email Notification Removed
		// sendEmail(u.getEmail().trim(), respage.getString("remove_user_role"), body, false);
		// sendEmail(ub.getEmail().trim(), respage.getString("remove_user_role"), body, false);
		// sendEmail(EmailEngine.getAdminEmail(), respage.getString("remove_user_role"), body, false);

		return body;

	}

}
