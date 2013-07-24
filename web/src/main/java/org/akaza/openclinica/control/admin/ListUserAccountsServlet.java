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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.RememberLastPage;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.util.InactiveAnalyzer;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.UserAccountRow;

@SuppressWarnings({"rawtypes", "unchecked",  "serial"})
public class ListUserAccountsServlet extends RememberLastPage {

	public static final String PATH = "ListUserAccounts";
	public static final String ARG_MESSAGE = "message";
	public static final String SAVED_USER_LIST_URL = "savedUserListUrl";

	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		if (!ub.isSysAdmin()) {
			addPageMessage(respage.getString("you_may_not_perform_administrative_functions"));
			throw new InsufficientPermissionException(Page.ADMIN_SYSTEM_SERVLET,
					respage.getString("you_may_not_perform_administrative_functions"), "1");
		}

		return;
	}

	@Override
	protected void processRequest() throws Exception {
		analyzeUrl();
		FormProcessor fp = new FormProcessor(request);

		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		EntityBeanTable table = fp.getEntityBeanTable();

		ArrayList allUsers = getAllUsers(udao);
		setStudyNamesInStudyUserRoles(allUsers);

		for (int i = 0; i < allUsers.size(); i++) {
			InactiveAnalyzer.analyze((UserAccountBean) allUsers.get(i), udao, restext);
		}

		ArrayList allUserRows = UserAccountRow.generateRowsFromBeans(allUsers);

		String[] columns = { resword.getString("user_name"), resword.getString("first_name"),
				resword.getString("last_name"), resword.getString("status"), resword.getString("actions") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(4);
		table.setQuery("ListUserAccounts", new HashMap());

		table.setRows(allUserRows);
		table.computeDisplay();

		request.setAttribute("table", table);

		String message = fp.getString(ARG_MESSAGE, true);
		request.setAttribute(ARG_MESSAGE, message);
        request.setAttribute("roleMap", Role.roleMap);

		resetPanel();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		if (allUsers.size() > 0) {
			setToPanel(resword.getString("users"), new Integer(allUsers.size()).toString());
		}

		analyzeForward(Page.LIST_USER_ACCOUNTS);
	}

	private ArrayList getAllUsers(UserAccountDAO udao) {
		ArrayList result = (ArrayList) udao.findAll();
		return result;
	}

	/**
	 * For each user, for each study user role, set the study user role's studyName property.
	 * 
	 * @param users
	 *            The users to display in the table of users. Each element is a UserAccountBean.
	 */
	private void setStudyNamesInStudyUserRoles(ArrayList users) {
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		ArrayList allStudies = (ArrayList) sdao.findAll();
		HashMap studiesById = new HashMap();

		int i;
		for (i = 0; i < allStudies.size(); i++) {
			StudyBean sb = (StudyBean) allStudies.get(i);
			studiesById.put(new Integer(sb.getId()), sb);
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

		return;
	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}

	@Override
	protected String getUrlKey() {
		return SAVED_USER_LIST_URL;
	}

	@Override
	protected String getDefaultUrl() {
		FormProcessor fp = new FormProcessor(request);
		String eblFiltered = fp.getString("ebl_filtered");
		String eblFilterKeyword = fp.getString("ebl_filterKeyword");
		String eblSortColumnInd = fp.getString("ebl_sortColumnInd");
		String eblSortAscending = fp.getString("ebl_sortAscending");
		return "?submitted=1&module=" + fp.getString("module") + "&ebl_page=1&ebl_sortColumnInd="
				+ (!eblSortColumnInd.isEmpty() ? eblSortColumnInd : "0") + "&ebl_sortAscending="
				+ (!eblSortAscending.isEmpty() ? eblSortAscending : "1") + "&ebl_filtered="
				+ (!eblFiltered.isEmpty() ? eblFiltered : "0") + "&ebl_filterKeyword="
				+ (!eblFilterKeyword.isEmpty() ? eblFilterKeyword : "") + "&&ebl_paginated=1";
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation() {
		return request.getQueryString() == null || !request.getQueryString().contains("&ebl_page=");
	}
}
