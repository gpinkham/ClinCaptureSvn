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
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for user role action validation.
 * 
 */
@SuppressWarnings({ "serial" })
@Component
public class SubmitDataServlet extends Controller {

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		forwardPage(Page.SUBMIT_DATA, request, response);
	}

	/**
	 * Check if user have access for data review.
	 * 
	 * @param ub
	 *            the user account bean.
	 * @param currentRole
	 *            the current study user role.
	 * @return true if user have access for data review, false otherwise.
	 */
	public static boolean mayViewData(UserAccountBean ub, StudyUserRoleBean currentRole) {
		if (currentRole != null) {
			Role r = currentRole.getRole();
			if (r != null
					&& (r.equals(Role.SYSTEM_ADMINISTRATOR) || r.equals(Role.STUDY_ADMINISTRATOR)
							|| r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.INVESTIGATOR)
							|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR) || r.equals(Role.STUDY_MONITOR)
							|| r.equals(Role.STUDY_CODER) || r.equals(Role.STUDY_EVALUATOR))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if user have access for data entry.
	 * 
	 * @param ub
	 *            the user account bean.
	 * @param currentRole
	 *            the current study user role.
	 * @return true if user have access for data entry, false otherwise.
	 */
	public static boolean maySubmitData(UserAccountBean ub, StudyUserRoleBean currentRole) {
		if (currentRole != null && ub != null) {
			Role r = currentRole.getRole();
			if (r != null && (r.equals(Role.SYSTEM_ADMINISTRATOR) || r.equals(Role.STUDY_ADMINISTRATOR)
					|| r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.INVESTIGATOR)
					|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR))) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		String exceptionName = resexception.getString("no_permission_to_submit_data");
		String noAccessMessage = respage.getString("may_not_enter_data_for_this_study")
				+ respage.getString("change_study_contact_sysadmin");

		if (mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(noAccessMessage, request);
		throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
	}

}
