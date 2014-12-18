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
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.util.InactiveAnalyzer;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.UserAccountRow;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Servlet for user account list table.
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class ListUserAccountsServlet extends RememberLastPage {

	public static final String ARG_MESSAGE = "message";
	public static final String SAVED_USER_LIST_URL = "savedUserListUrl";
	public static final int ACTION_COLUMN_NUM = 5;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		if (!ub.isSysAdmin()) {
			addPageMessage(respage.getString("you_may_not_perform_administrative_functions"), request);
			throw new InsufficientPermissionException(Page.ADMIN_SYSTEM_SERVLET,
					respage.getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (shouldRedirect(request, response)) {
			return;
		}

		FormProcessor fp = new FormProcessor(request);

		UserAccountDAO udao = getUserAccountDAO();
		StudyDAO sdao = getStudyDAO();
		EntityBeanTable table = fp.getEntityBeanTable();
		UserAccountBean currentUser = getUserAccountBean(request);
		List<StudyBean> studyListCurrentUserHasAccessTo = sdao.findAllActiveStudiesWhereUserHasRole(currentUser
				.getName());
		List<UserAccountBean> allUsers = getAllUsers(udao);
		ListIterator<UserAccountBean> iterateUser;

		if (!currentUser.getName().equals(UserAccountBean.ROOT)) {
			iterateUser = allUsers.listIterator();
			while (iterateUser.hasNext()) {
				if (!getUserAccountService().doesUserHaveRoleInStudies(iterateUser.next(),
						studyListCurrentUserHasAccessTo)) {
					iterateUser.remove();
				}
			}
		}

		setStudyNamesInStudyUserRoles(allUsers);

		for (Object allUser : allUsers) {
			InactiveAnalyzer.analyze((UserAccountBean) allUser, udao, restext);
		}

		Map<String, Integer> userRolesRemovedCountMap = new HashMap<String, Integer>();
		int removedRolesCount;

		for (Object userBean : allUsers) {
			UserAccountBean uab = (UserAccountBean) userBean;
			removedRolesCount = 0;
			for (StudyUserRoleBean urb : uab.getRoles()) {
				if (urb.getStatus() == Status.DELETED) {
					removedRolesCount += 1;
				}
			}
			userRolesRemovedCountMap.put(uab.getName(), removedRolesCount);
		}

		StudyBean sb = (StudyBean) sdao.findByPK(((UserAccountBean) (request.getSession().getAttribute("userBean")))
				.getActiveStudyId());

		ArrayList allUserRows = UserAccountRow.generateRowsFromBeans((ArrayList) allUsers);

		String[] columns = { resword.getString("user_name"), resword.getString("user_type"),
				resword.getString("first_name"), resword.getString("last_name"), resword.getString("status"),
				resword.getString("actions") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(ACTION_COLUMN_NUM);
		table.setQuery("ListUserAccounts", new HashMap());

		table.setRows(allUserRows);
		table.computeDisplay();

		request.setAttribute("table", table);

		String message = fp.getString(ARG_MESSAGE, true);
		request.setAttribute(ARG_MESSAGE, message);
		request.setAttribute("roleMap", Role.ROLE_MAP);
		request.setAttribute("userRolesRemovedCountMap", userRolesRemovedCountMap);
		request.setAttribute("studyId", sb.getId());
		request.setAttribute("parentStudyId", sb.getParentStudyId());

		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		if (allUsers.size() > 0) {
			setToPanel(resword.getString("users"), Integer.toString(allUsers.size()), request);
		}

		forwardPage(Page.LIST_USER_ACCOUNTS, request, response);
	}

	private List<UserAccountBean> getAllUsers(UserAccountDAO udao) {
		return (ArrayList<UserAccountBean>) udao.findAll();
	}

	/**
	 * For each user, for each study user role, set the study user role's studyName property.
	 * 
	 * @param users
	 *            The users to display in the table of users. Each element is a UserAccountBean.
	 */
	private void setStudyNamesInStudyUserRoles(List<UserAccountBean> users) {
		StudyDAO sdao = getStudyDAO();
		ArrayList allStudies = (ArrayList) sdao.findAll();
		HashMap studiesById = new HashMap();

		int i;
		for (i = 0; i < allStudies.size(); i++) {
			StudyBean sb = (StudyBean) allStudies.get(i);
			studiesById.put(sb.getId(), sb);
		}

		for (i = 0; i < users.size(); i++) {
			UserAccountBean u = (UserAccountBean) users.get(i);
			ArrayList roles = u.getRoles();

			for (int j = 0; j < roles.size(); j++) {
				StudyUserRoleBean surb = (StudyUserRoleBean) roles.get(j);
				StudyBean sb = (StudyBean) studiesById.get(new Integer(surb.getStudyId()));
				if (sb != null) {
					surb.setStudyName(sb.getName());
					surb.setParentStudyId(sb.getParentStudyId());
				}
				roles.set(j, surb);
			}
			u.setRoles(roles);
			users.set(i, u);
		}
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}

	@Override
	protected String getUrlKey(HttpServletRequest request) {
		return SAVED_USER_LIST_URL;
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		String eblFiltered = fp.getString("ebl_filtered");
		String eblFilterKeyword = fp.getString("ebl_filterKeyword");
		String eblSortColumnInd = fp.getString("ebl_sortColumnInd");
		String eblSortAscending = fp.getString("ebl_sortAscending");
		return new StringBuilder("").append("?submitted=1&module=").append(fp.getString("module"))
				.append("&ebl_page=1&ebl_sortColumnInd=")
				.append((!eblSortColumnInd.isEmpty() ? eblSortColumnInd : "0")).append("&ebl_sortAscending=")
				.append((!eblSortAscending.isEmpty() ? eblSortAscending : "1")).append("&ebl_filtered=")
				.append((!eblFiltered.isEmpty() ? eblFiltered : "0")).append("&ebl_filterKeyword=")
				.append((!eblFilterKeyword.isEmpty() ? eblFilterKeyword : "")).append("&&ebl_paginated=1").toString();
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null || !request.getQueryString().contains("&ebl_page=");
	}

}
