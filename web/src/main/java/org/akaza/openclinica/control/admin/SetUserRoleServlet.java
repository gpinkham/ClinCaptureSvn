/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Set user role servlet for adding roles for user account.
 * 
 */
@SuppressWarnings({ "serial", "unchecked" })
@Component
public class SetUserRoleServlet extends Controller {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		if (getUserAccountBean(request).isSysAdmin()) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_USER_ACCOUNTS_SERVLET, resexception.getString("not_admin"),
				"1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountDAO udao = getUserAccountDAO();
		StudyDAO sdao = getStudyDAO();
		FormProcessor fp = new FormProcessor(request);
		int userId = fp.getInt("userId");
		String pageIsChanged = request.getParameter("pageIsChanged");
		UserAccountBean currentUser = getUserAccountBean(request);
		List<StudyBean> finalStudyListNotHaveRole;
		List<StudyBean> studyListRoleCanBeAssignedTo;
		List<StudyBean> studyListWithRoleAssigned;
		List<StudyBean> siteListByParent;
		boolean isStudyLevelUser;
		boolean withoutRoles = false;
		StudyBean selectedStudy;

		if (pageIsChanged != null) {
			request.setAttribute("pageIsChanged", pageIsChanged);
		}

		if (userId == 0) {
			addPageMessage(respage.getString("please_choose_a_user_to_set_role_for"), request);
			forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET, request, response);
		} else {
			String action = request.getParameter("action");
			UserAccountBean user = (UserAccountBean) udao.findByPK(userId);

			Boolean changeRoles = request.getParameter("changeRoles") != null
					&& Boolean.parseBoolean(request.getParameter("changeRoles"));
			int studyId = fp.getInt("studyId");
			request.setAttribute("studyId", studyId);
			Map<Object, Object> roleMap = new LinkedHashMap<Object, Object>(Role.ROLE_MAP_WITH_DESCRIPTION);
			request.setAttribute("roles", roleMap);

			if ("confirm".equalsIgnoreCase(action) || changeRoles) {

				studyListRoleCanBeAssignedTo = sdao.findAllActiveStudiesWhereUserHasRole(currentUser.getName());
				studyListWithRoleAssigned = sdao.findAllActiveWhereUserHasRole(user.getName());

				if (user.getRoles().size() > 0) {
					isStudyLevelUser = user.getRoles().get(0).isStudyLevelRole();
					if (isStudyLevelUser) {
						studyListRoleCanBeAssignedTo.removeAll(studyListWithRoleAssigned);
						finalStudyListNotHaveRole = studyListRoleCanBeAssignedTo;
					} else {
						finalStudyListNotHaveRole = new ArrayList<StudyBean>();
						for (StudyBean sb : studyListRoleCanBeAssignedTo) {
							siteListByParent = sdao.findAllByParentAndActive(sb.getId());
							siteListByParent.removeAll(studyListWithRoleAssigned);
							if (!siteListByParent.isEmpty()) {
								finalStudyListNotHaveRole.add(sb);
								finalStudyListNotHaveRole.addAll(siteListByParent);
							}
						}
					}
				} else {
					withoutRoles = true;
					selectedStudy = (StudyBean) sdao.findByPK(studyId);
					request.setAttribute("selectedStudy", selectedStudy == null || selectedStudy.getId() == 0 ? null
							: selectedStudy);
					isStudyLevelUser = !(selectedStudy != null && selectedStudy.getParentStudyId() > 0);
					finalStudyListNotHaveRole = new ArrayList<StudyBean>();
					for (StudyBean sb : studyListRoleCanBeAssignedTo) {
						siteListByParent = sdao.findAllByParentAndActive(sb.getId());
						if (!siteListByParent.isEmpty()) {
							finalStudyListNotHaveRole.add(sb);
							finalStudyListNotHaveRole.addAll(siteListByParent);
						}
					}
				}

				StudyBean selectedStudyBean = new StudyBean();
				if (studyId > 0) {
					selectedStudyBean = (StudyBean) sdao.findByPK(studyId);
				} else {
					if (finalStudyListNotHaveRole.size() > 0) {
						selectedStudyBean = (StudyBean) sdao.findByPK(finalStudyListNotHaveRole.get(0).getId());
					}
				}
				int currentStudyId = selectedStudyBean.getParentStudyId() > 0 ? selectedStudyBean.getParentStudyId()
						: selectedStudyBean.getId();
				boolean isEvaluationEnabled = getStudyParameterValueDAO().findByHandleAndStudy(currentStudyId, "studyEvaluator")
						.getValue().equalsIgnoreCase("yes");
				if (!isEvaluationEnabled) {
					roleMap.remove(Role.STUDY_EVALUATOR.getId());
					request.setAttribute("roles", roleMap);
				} else {
					request.setAttribute("roles", roleMap);
				}
				request.setAttribute("selectedStudy", selectedStudyBean);

				request.setAttribute("user", user);
				request.setAttribute("withoutRoles", withoutRoles);
				request.setAttribute("isStudyLevelUser", isStudyLevelUser);
				request.setAttribute("studies", finalStudyListNotHaveRole);
				StudyUserRoleBean uRole = new StudyUserRoleBean();
				uRole.setFirstName(user.getFirstName());
				uRole.setLastName(user.getLastName());
				uRole.setUserName(user.getName());
				request.setAttribute("uRole", uRole);

				forwardPage(Page.SET_USER_ROLE, request, response);
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
				sur.setOwner(currentUser);
				sur.setCreatedDate(new Date());
				StringBuilder sb = new StringBuilder("");
				if (studyId > 0) {
					udao.createStudyUserRole(user, sur);

					if (currentUser.getId() != user.getId()) {
						getUserAccountService().setActiveStudyId(user, studyId);
					}

					if (currentUser.getId() == user.getId()) {
						request.getSession().setAttribute("reloadUserBean", true);
					}
					addPageMessage(
							sb.append(user.getFirstName()).append(" ").append(user.getLastName()).append(" (")
									.append(resword.getString("username")).append(": ").append(user.getName())
									.append(") ").append(respage.getString("has_been_granted_the_role")).append(" \"")
									.append(sur.getRole().getDescription()).append("\" ")
									.append(respage.getString("in_the_study_site")).append(" ")
									.append(userStudy.getName()).append(".").toString(), request);
				}

				forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET, request, response);
			}

		}
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
