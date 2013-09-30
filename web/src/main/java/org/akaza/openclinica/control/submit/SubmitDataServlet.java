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
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.Locale;

@SuppressWarnings({ "serial" })
public class SubmitDataServlet extends SecureController {

	Locale locale;

	@Override
	protected void processRequest() throws Exception {
		forwardPage(Page.SUBMIT_DATA);
	}

	public static boolean mayViewData(UserAccountBean ub, StudyUserRoleBean currentRole) {
		if (currentRole != null) {
			Role r = currentRole.getRole();
			if (r != null
					&& (r.equals(Role.SYSTEM_ADMINISTRATOR) || r.equals(Role.STUDY_ADMINISTRATOR)
							|| r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.INVESTIGATOR)
							|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR) || r.equals(Role.STUDY_MONITOR) || r.equals(Role.STUDY_CODER))) {
				return true;
			}
		}

		return false;
	}

	public static boolean maySubmitData(UserAccountBean ub, StudyUserRoleBean currentRole) {
		if (currentRole != null && ub != null) {
			Role r = currentRole.getRole();
			if (r != null
					&& (r.equals(Role.SYSTEM_ADMINISTRATOR) || r.equals(Role.STUDY_ADMINISTRATOR)
							|| r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.INVESTIGATOR) || r
								.equals(Role.CLINICAL_RESEARCH_COORDINATOR))) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		String exceptionName = resexception.getString("no_permission_to_submit_data");
		String noAccessMessage = respage.getString("may_not_enter_data_for_this_study")
				+ respage.getString("change_study_contact_sysadmin");

		if (mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(noAccessMessage);
		throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
	}

}
