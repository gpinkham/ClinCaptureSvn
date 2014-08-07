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
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clinovo.util.StudyParameterPriorityUtil;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.UserAccountRow;
import org.springframework.stereotype.Component;

/**
 * Processes request to assign a user to a study.
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class AssignUserToStudyServlet extends Controller {

	private static final int ROLE_COL = 3;
	private static final int SELECTED_COL = 4;
	private static final int NOTES_COL = 5;

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyBean currentStudy = getCurrentStudy(request);
		String action = request.getParameter("action");
		ArrayList users = findUsers(request);
		String nextListPage = request.getParameter("next_list_page");
		if (StringUtil.isBlank(action) || (nextListPage != null && nextListPage.equalsIgnoreCase("true"))) {
			FormProcessor fp = new FormProcessor(request);
			EntityBeanTable table = fp.getEntityBeanTable();
			ArrayList allRows = UserAccountRow.generateRowsFromBeans(users);

			if (nextListPage == null) {
				request.getSession().removeAttribute("tmpSelectedUsersMap");
			}

			Map tmpSelectedUsersMap = (HashMap) request.getSession().getAttribute("tmpSelectedUsersMap");
			if (tmpSelectedUsersMap == null) {
				tmpSelectedUsersMap = new HashMap();
			}
			if (nextListPage != null && nextListPage.equalsIgnoreCase("true")) {
				for (int i = 0; i < users.size(); i++) {
					int id = fp.getInt("id" + i);
					int roleId = fp.getInt("activeStudyRoleId" + i);
					String checked = fp.getString("selected" + i);
					if (!StringUtil.isBlank(checked) && "yes".equalsIgnoreCase(checked.trim())) {
						tmpSelectedUsersMap.put(id, roleId);
					} else {
						if (tmpSelectedUsersMap.containsKey(id)) {
							tmpSelectedUsersMap.remove(id);
						}
					}
				}
				request.getSession().setAttribute("tmpSelectedUsersMap", tmpSelectedUsersMap);
			}

			String[] columns = { resword.getString("user_name"), resword.getString("first_name"),
					resword.getString("last_name"), resword.getString("role"), resword.getString("selected"),
					resword.getString("notes") };
			table.setColumns(new ArrayList(Arrays.asList(columns)));
			table.hideColumnLink(ROLE_COL);
			table.hideColumnLink(SELECTED_COL);
			table.hideColumnLink(NOTES_COL);
			table.setQuery("AssignUserToStudy", new HashMap());
			table.setRows(allRows);
			table.computeDisplay();

			StudyParameterValueDAO dao = new StudyParameterValueDAO(getDataSource());
			StudyParameterValueBean allowCodingVerification = dao.findByHandleAndStudy(currentStudy.getId(), "allowCodingVerification");

			request.setAttribute("table", table);
			ArrayList roles = Role.toArrayList();
			if (currentStudy.getParentStudyId() > 0) {
				roles.remove(Role.STUDY_ADMINISTRATOR);
				roles.remove(Role.STUDY_MONITOR);
				roles.remove(Role.STUDY_CODER);
			} else {
				if (!allowCodingVerification.getValue().equalsIgnoreCase("yes")) {
					roles.remove(Role.STUDY_CODER);
				}
				roles.remove(Role.INVESTIGATOR);
				roles.remove(Role.CLINICAL_RESEARCH_COORDINATOR);
			}

			int currentStudyId = currentStudy.getParentStudyId() > 0 ? currentStudy.getParentStudyId() : currentStudy.getId();
			boolean isEvaluationEnabled = StudyParameterPriorityUtil.isParameterEnabled("allowCrfEvaluation", currentStudyId, getSystemDAO(), getStudyParameterValueDAO(), getStudyDAO());
			if (!isEvaluationEnabled) {
				Role.ROLE_MAP_WITH_DESCRIPTION.remove(Role.STUDY_EVALUATOR.getId());
				roles.remove(Role.STUDY_EVALUATOR);
			}

			roles.remove(Role.STUDY_DIRECTOR);
			roles.remove(Role.SYSTEM_ADMINISTRATOR);
			request.setAttribute("roles", roles);
			forwardPage(Page.STUDY_USER_LIST, request, response);
		} else {
			if ("submit".equalsIgnoreCase(action)) {
				addUser(request, response, users);
			}
		}
	}

	private void addUser(HttpServletRequest request, HttpServletResponse response, ArrayList users) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		String pageMass = "";
		UserAccountDAO udao = getUserAccountDAO();
		FormProcessor fp = new FormProcessor(request);
		Map tmpSelectedUsersMap = (HashMap) request.getSession().getAttribute("tmpSelectedUsersMap");
		Set addedUsers = new HashSet();
		for (int i = 0; i < users.size(); i++) {
			int id = fp.getInt("id" + i);
			int roleId = fp.getInt("activeStudyRoleId" + i);
			String checked = fp.getString("selected" + i);

			if (!StringUtil.isBlank(checked) && "yes".equalsIgnoreCase(checked.trim())) {
				logger.info("one user selected");
				UserAccountBean user = (UserAccountBean) udao.findByPK(id);
				addedUsers.add(id);

				StudyUserRoleBean sub = new StudyUserRoleBean();
				sub.setRoleName(Role.get(roleId).getName());
				sub.setStudyId(currentStudy.getId());
				sub.setStatus(Status.AVAILABLE);
				sub.setOwner(ub);
				if (udao.findStudyUserRole(user, sub).getName() != null && udao.findStudyUserRole(user, sub).getName().isEmpty()) {
					udao.createStudyUserRole(user, sub);
					getUserAccountService().setActiveStudyId(user, currentStudy.getId());
				} else {
					break;
				}
				logger.info("one user added");
				pageMass = pageMass + sendEmail(user, currentStudy, sub);
			} else {
				if (tmpSelectedUsersMap != null && tmpSelectedUsersMap.containsKey(id)) {
					tmpSelectedUsersMap.remove(id);
				}
			}
		}

		/* Assigning users which might have been selected during list navigation */
		if (tmpSelectedUsersMap != null) {
			for (Object o : tmpSelectedUsersMap.keySet()) {
				int id = (Integer) o;
				int roleId = (Integer) tmpSelectedUsersMap.get(id);
				boolean alreadyAdded = false;
				for (Object addedUser : addedUsers) {
					if (id == (Integer) addedUser) {
						alreadyAdded = true;
					}
				}
				if (!alreadyAdded) {
					UserAccountBean user = (UserAccountBean) udao.findByPK(id);

					StudyUserRoleBean sub = new StudyUserRoleBean();
					sub.setRoleName(Role.get(roleId).getName());
					sub.setStudyId(currentStudy.getId());
					sub.setStatus(Status.AVAILABLE);
					sub.setOwner(ub);
					udao.createStudyUserRole(user, sub);
					getUserAccountService().setActiveStudyId(user, currentStudy.getId());
					logger.info("one user added");
					pageMass = pageMass + sendEmail(user, currentStudy, sub);
				}
			}
		}
		request.getSession().removeAttribute("tmpSelectedUsersMap");

		if ("".equals(pageMass)) {
			addPageMessage(respage.getString("no_new_user_assigned_to_study"), request);
		} else {
			addPageMessage(pageMass, request);
		}

		ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
		request.getSession().setAttribute("pageMessages", pageMessages);
		forwardPage(Page.LIST_USER_IN_STUDY_SERVLET, request, response);

	}

	private ArrayList findUsers(HttpServletRequest request) throws Exception {

		UserAccountBean currentUser = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		UserAccountDAO udao = getUserAccountDAO();
		StudyDAO sdao = getStudyDAO();
		StudyBean site;
		StudyBean study;
		List<UserAccountBean> userAvailable = new ArrayList<UserAccountBean>();
		List<UserAccountBean> userListbyRoles;
		String notes;
		boolean hasRoleInCurrentStudy;
		boolean hasRoleWithStatusRemovedInCurrentStudy;
		List<StudyBean> studyListCurrentUserHasAccessTo = sdao.findAllActiveStudiesWhereUserHasRole(currentUser.getName());
		ListIterator<UserAccountBean> iterateUser;

		if (currentStudy.getParentStudyId() > 0) {
			userListbyRoles = (ArrayList<UserAccountBean>) udao.findAllByRole(
					Role.CLINICAL_RESEARCH_COORDINATOR.getName(), Role.INVESTIGATOR.getName());
		} else {
			userListbyRoles = (ArrayList<UserAccountBean>) udao.findAllByRole(Role.STUDY_ADMINISTRATOR.getName(),
					Role.STUDY_MONITOR.getName());
		}

		iterateUser = userListbyRoles.listIterator();
		while (iterateUser.hasNext()) {
			if (!getUserAccountService().doesUserHaveRoleInStydies(iterateUser.next(),
					studyListCurrentUserHasAccessTo)) {
				iterateUser.remove();
			}
		}

		for (UserAccountBean accountBean : userListbyRoles) {

			if (accountBean.getEnabled() == null || !accountBean.getEnabled()) {
				continue;
			}

			hasRoleInCurrentStudy = false;
			hasRoleWithStatusRemovedInCurrentStudy = false;
			notes = "";
			for (StudyUserRoleBean roleBean : accountBean.getRoles()) {

				if (currentStudy.getId() == roleBean.getStudyId() && roleBean.getStatus().equals(Status.DELETED)) {
					hasRoleWithStatusRemovedInCurrentStudy = true;
					break;
				} else if (currentStudy.getId() == roleBean.getStudyId()) {
					hasRoleInCurrentStudy = true;
				} else if (currentStudy.getParentStudyId() > 0 && !roleBean.getStatus().equals(Status.DELETED)) {
					site = (StudyBean) sdao.findByPK(roleBean.getStudyId());
					study = (StudyBean) sdao.findByPK(site.getParentStudyId());
					notes = new StringBuilder("").append(notes).append(roleBean.getRole().getDescription())
							.append(respage.getString("in_site")).append(": ").append(site.getName()).append(",")
							.append(respage.getString("in_the_study")).append(": ").append(study.getName())
							.append("; ").toString();
				} else if (currentStudy.getParentStudyId() == 0 && !roleBean.getStatus().equals(Status.DELETED)) {
					study = (StudyBean) sdao.findByPK(roleBean.getStudyId());
					notes = new StringBuilder("").append(notes).append(roleBean.getRole().getDescription())
							.append(respage.getString("in_the_study")).append(": ").append(study.getName())
							.append("; ").toString();
				}

			}

			if (!hasRoleWithStatusRemovedInCurrentStudy) {

				accountBean.setNotes(notes);

				if (hasRoleInCurrentStudy) {
					accountBean.setStatus(Status.UNAVAILABLE);
					accountBean.setActiveStudyId(currentStudy.getId());
				} else {
					accountBean.setStatus(Status.AVAILABLE);
				}

				userAvailable.add(accountBean);
			}
		}

		return (ArrayList<UserAccountBean>) userAvailable;
	}

	private String sendEmail(UserAccountBean u, StudyBean currentStudy, StudyUserRoleBean sub) throws Exception {
		logger.info("Sending email...");

		if (currentStudy.getParentStudyId() > 0) {
			return new StringBuilder("").append(u.getFirstName()).append(" ").append(u.getLastName()).append("(")
					.append(resword.getString("username")).append(": ").append(u.getName()).append(") ")
					.append(respage.getString("has_been_assigned_to_the_site")).append(currentStudy.getName())
					.append(" under the Study ").append(currentStudy.getParentStudyName()).append(" ")
					.append(resword.getString("as")).append(" \"").append(sub.getRole().getDescription())
					.append("\". ").toString();
		} else {
			return new StringBuilder("").append(u.getFirstName()).append(" ").append(u.getLastName()).append("(")
					.append(resword.getString("username")).append(": ").append(u.getName()).append(") ")
					.append(respage.getString("has_been_assigned_to_the_study")).append(currentStudy.getName())
					.append(" ").append(resword.getString("as")).append(" \"").append(sub.getRole().getDescription())
					.append("\". ").toString();
		}
	}
}
