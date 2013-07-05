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

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

@SuppressWarnings({ "rawtypes", "serial" })
public class ViewStudyUserServlet extends SecureController {
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
		String name = request.getParameter("name");
		String studyIdString = request.getParameter("studyId");

		{

			if (StringUtil.isBlank(name) || StringUtil.isBlank(studyIdString)) {
				addPageMessage(respage.getString("please_choose_a_user_to_view"));
				forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);
			} else {
				int studyId = Integer.valueOf(studyIdString.trim()).intValue();
				UserAccountBean user = (UserAccountBean) udao.findByUserName(name);

				request.setAttribute("user", user);

				StudyUserRoleBean uRole = udao.findRoleByUserNameAndStudyId(name, studyId);
				request.setAttribute("uRole", uRole);

				StudyDAO sdao = new StudyDAO(sm.getDataSource());
				StudyBean study = (StudyBean) sdao.findByPK(studyId);
				request.setAttribute("uStudy", study);
				request.setAttribute("roleMap", Role.roleMap);
				// To provide the view with the correct date format
				// pattern, locale sensitive
				String pattn = "";
				pattn = ResourceBundleProvider.getFormatBundle().getString("date_format_string");
				request.setAttribute("dateFormatPattern", pattn);
				request.setAttribute("action", "");
				forwardPage(Page.VIEW_USER_IN_STUDY);

			}
		}
	}
}
