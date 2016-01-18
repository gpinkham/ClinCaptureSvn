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

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for view user account page.
 * 
 */

@Component
@SuppressWarnings({ "serial" })
public class ViewStudyUserServlet extends Controller {
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}
		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study") + " "
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_USER_IN_STUDY_SERVLET,
				getResException().getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountDAO udao = new UserAccountDAO(getDataSource());
		String name = request.getParameter("name") == null? "" : request.getParameter("name").trim();
		String studyIdString = request.getParameter("studyId");

		if (StringUtil.isBlank(name) || StringUtil.isBlank(studyIdString)) {
			addPageMessage(getResPage().getString("please_choose_a_user_to_view"), request);
			forwardPage(Page.LIST_USER_IN_STUDY_SERVLET, request, response);
		} else {
			int studyId = Integer.parseInt(studyIdString.trim());
			UserAccountBean user = (UserAccountBean) udao.findByUserName(name);

			request.setAttribute("user", user);

			StudyUserRoleBean uRole = udao.findRoleByUserNameAndStudyId(name, studyId);
			request.setAttribute("uRole", uRole);

			StudyDAO sdao = new StudyDAO(getDataSource());
			StudyBean study = (StudyBean) sdao.findByPK(studyId);
			request.setAttribute("uStudy", study);
			request.setAttribute("roleMap", Role.ROLE_MAP);

			String pattn = ResourceBundleProvider.getFormatBundle().getString("date_format_string");
			request.setAttribute("dateFormatPattern", pattn);
			request.setAttribute("action", "");
			forwardPage(Page.VIEW_USER_IN_STUDY, request, response);
		}
	}
}
