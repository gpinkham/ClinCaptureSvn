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
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
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
 * @author jxu
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class SetStudyUserRoleServlet extends SecureController {
	/**
     *
     */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.LIST_USER_IN_STUDY_SERVLET,
				resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest() throws Exception {
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		String name = request.getParameter("name");
		String studyIdString = request.getParameter("studyId");
		if (StringUtil.isBlank(name) || StringUtil.isBlank(studyIdString)) {
			addPageMessage(respage.getString("please_choose_a_user_to_set_role_for"));
			forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);
		} else {
			String action = request.getParameter("action");
			FormProcessor fp = new FormProcessor(request);
			UserAccountBean user = (UserAccountBean) udao.findByUserName(name);
			StudyBean userStudy = (StudyBean) sdao.findByPK(fp.getInt("studyId"));
			if ("confirm".equalsIgnoreCase(action)) {
				int studyId = Integer.valueOf(studyIdString.trim()).intValue();
                StudyBean study = (StudyBean) sdao.findByPK(studyId);
                request.setAttribute("isThisStudy", !(study.getParentStudyId() > 0));

				request.setAttribute("user", user);

				StudyUserRoleBean uRole = udao.findRoleByUserNameAndStudyId(name, studyId);
				uRole.setStudyName(userStudy.getName());
				request.setAttribute("uRole", uRole);

				ArrayList roles = Role.toArrayList();
				roles.remove(Role.SYSTEM_ADMINISTRATOR); // admin is not a user role, only used for tomcat

				StudyBean studyBean = (StudyBean) sdao.findByPK(uRole.getStudyId());

				if (currentStudy.getParentStudyId() > 0) {
					roles.remove(Role.STUDY_ADMINISTRATOR);
					roles.remove(Role.STUDY_DIRECTOR);
				} else if (studyBean.getParentStudyId() > 0) {
					roles.remove(Role.STUDY_ADMINISTRATOR);
					roles.remove(Role.STUDY_DIRECTOR);
					// TODO: redo this fix
					Role r = Role.CLINICAL_RESEARCH_COORDINATOR;
					r.setDescription("Clinical_Research_Coordinator");
					roles.remove(Role.CLINICAL_RESEARCH_COORDINATOR);
					roles.add(r);
					Role ri = Role.INVESTIGATOR;
					ri.setDescription("Investigator");
					roles.remove(Role.INVESTIGATOR);

					roles.add(ri);
				}
				request.setAttribute("roles", roles);

				forwardPage(Page.SET_USER_ROLE_IN_STUDY);
			} else {
				// set role

				String userName = fp.getString("name");
				int studyId = fp.getInt("studyId");
				int roleId = fp.getInt("roleId");
				StudyUserRoleBean sur = new StudyUserRoleBean();
				sur.setName(userName);
				sur.setRole(Role.get(roleId));
				sur.setStudyId(studyId);
				sur.setStudyName(userStudy.getName());
				sur.setStatus(Status.AVAILABLE);
				sur.setUpdater(ub);
				sur.setUpdatedDate(new Date());
				udao.updateStudyUserRole(sur, userName);
                if (ub.getId() == user.getId()) {
                    session.setAttribute("reloadUserBean", true);
                }
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
		String body = u.getFirstName() + " " + u.getLastName() + " (" + resword.getString("username") + ": "
				+ u.getName() + ") " + respage.getString("has_been_granted_the_role") + " "
				+ sub.getRole().getDescription() + " " + respage.getString("in_the_study_site") + " " + study.getName()
				+ ".";

		return body;

	}

}
