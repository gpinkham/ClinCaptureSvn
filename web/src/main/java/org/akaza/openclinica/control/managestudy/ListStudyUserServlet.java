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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.StudyUserRoleRow;
import org.springframework.stereotype.Component;

/**
 * Lists all the users in a study
 * 
 * @author jxu
 * 
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
@Component
public class ListStudyUserServlet extends Controller {

	/**
     *
     */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
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
				resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		FormProcessor fp = new FormProcessor(request);
		UserAccountDAO udao = new UserAccountDAO(getDataSource());
		ArrayList users = udao.findAllRolesByStudy(currentStudy.getId());
		Map<String, Integer> userRolesAvailableCountMap = new HashMap<String, Integer>();
		List<StudyUserRoleBean> userRoleList;
		String userAccountName;
		int removedRolesCount;

		for (Object userRoleBean : users) {
			StudyUserRoleBean urb = (StudyUserRoleBean) userRoleBean;
			userAccountName = urb.getUserName();
			if (!userRolesAvailableCountMap.containsKey(userAccountName)) {
				removedRolesCount = 0;
				userRoleList = (ArrayList) udao.findAllRolesByUserName(userAccountName);
				for (StudyUserRoleBean roleBean : userRoleList) {
					if (roleBean.getStatus() == Status.DELETED) {
						removedRolesCount += 1;
					}
				}
				userRolesAvailableCountMap.put(userAccountName, userRoleList.size() - removedRolesCount);
			}
		}

		EntityBeanTable table = fp.getEntityBeanTable();
		ArrayList allStudyUserRows = StudyUserRoleRow.generateRowsFromBeans(users);

		String[] columns = { resword.getString("user_name"), resword.getString("first_name"),
				resword.getString("last_name"), resword.getString("role"), resword.getString("study_name"),
				resword.getString("status"), resword.getString("actions") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(6);
		table.setQuery("ListStudyUser", new HashMap());
		table.setRows(allStudyUserRows);
		table.computeDisplay();

		request.setAttribute("table", table);
		request.setAttribute("userRolesAvailableCountMap", userRolesAvailableCountMap);
		request.setAttribute("roleMap", Role.roleMap);
		request.setAttribute("study", currentStudy);
		forwardPage(Page.LIST_USER_IN_STUDY, request, response);

	}
}
