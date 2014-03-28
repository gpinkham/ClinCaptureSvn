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

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jxu
 * 
 *         Modified by ywang, 11-19-2007.
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
@SuppressWarnings({ "serial" })
public class SetUserRoleServlet extends SecureController {

	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.LIST_USER_ACCOUNTS_SERVLET, resexception.getString("not_admin"),
				"1");

	}

	@Override
	public void processRequest() throws Exception {
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		FormProcessor fp = new FormProcessor(request);
		int userId = fp.getInt("userId");
		String pageIsChanged = request.getParameter("pageIsChanged");
		UserAccountBean userLoggedInSystem = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
		List<StudyBean> finalStudyListNotHaveRole;
		List<StudyBean> studyListRoleCanBeAssignedTo;
		List<StudyBean> studyListWithRoleAssigned;
		List<StudyBean> siteListByParent;
		boolean isStudyLevelUser;
		
		if (pageIsChanged != null) {
			request.setAttribute("pageIsChanged", pageIsChanged);
		}
		
		if (userId == 0) {
			addPageMessage(respage.getString("please_choose_a_user_to_set_role_for"));
			forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
		} else {
			String action = request.getParameter("action");
			UserAccountBean user = (UserAccountBean) udao.findByPK(userId);

			Boolean changeRoles = request.getParameter("changeRoles") != null
					&& Boolean.parseBoolean(request.getParameter("changeRoles"));
			int studyId = fp.getInt("studyId");
			request.setAttribute("roles", Role.roleMapWithDescriptions);
			request.setAttribute("studyId", studyId);

			if ("confirm".equalsIgnoreCase(action) || changeRoles) {
				
				studyListRoleCanBeAssignedTo = sdao.findAllActiveStudiesWhereUserHasRole(userLoggedInSystem.getName());
				studyListWithRoleAssigned = sdao.findAllActiveWhereUserHasRole(user.getName());
				isStudyLevelUser = user.getRoles().get(0).isStudyLevelRole();
				
				if (isStudyLevelUser) {
					studyListRoleCanBeAssignedTo.removeAll(studyListWithRoleAssigned);
					finalStudyListNotHaveRole = studyListRoleCanBeAssignedTo;
				} else {
					finalStudyListNotHaveRole = new ArrayList<StudyBean>();
					for (StudyBean sb: studyListRoleCanBeAssignedTo) {
						siteListByParent = sdao.findAllByParentAndActive(sb.getId());
						siteListByParent.removeAll(studyListWithRoleAssigned);
						if(!siteListByParent.isEmpty()) {
							finalStudyListNotHaveRole.add(sb);
							finalStudyListNotHaveRole.addAll(siteListByParent);
						}
					}
				}

				request.setAttribute("user", user);
				request.setAttribute("isStudyLevelUser", isStudyLevelUser);
				request.setAttribute("studies", finalStudyListNotHaveRole);
				StudyUserRoleBean uRole = new StudyUserRoleBean();
				uRole.setFirstName(user.getFirstName());
				uRole.setLastName(user.getLastName());
				uRole.setUserName(user.getName());
				request.setAttribute("uRole", uRole);

				forwardPage(Page.SET_USER_ROLE);
			} else {
				// set role
				String userName = fp.getString("name");
				studyId = fp.getInt("studyId");
				StudyBean userStudy = (StudyBean) sdao.findByPK(studyId);
				int roleId = fp.getInt("roleId");
				// new user role
				StudyUserRoleBean sur = new StudyUserRoleBean();
				sur.setName(userName);
				sur.setRole(Role.get(roleId));
				sur.setStudyId(studyId);
				sur.setStudyName(userStudy.getName());
				sur.setStatus(Status.AVAILABLE);
				sur.setOwner(ub);
				sur.setCreatedDate(new Date());

				if (studyId > 0) {
					udao.createStudyUserRole(user, sur);
					if (ub.getId() == user.getId()) {
						session.setAttribute("reloadUserBean", true);
					}
					addPageMessage(new StringBuilder("").append(user.getFirstName()).append(" ")
							.append(user.getLastName()).append(" (").append(resword.getString("username"))
							.append(": ").append(user.getName()).append(") ")
							.append(respage.getString("has_been_granted_the_role")).append(" \"")
							.append(sur.getRole().getDescription()).append("\" ")
							.append(respage.getString("in_the_study_site")).append(" ")
							.append(userStudy.getName()).append(".").toString());
				}
		
				forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
			}

		}
	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}
}
