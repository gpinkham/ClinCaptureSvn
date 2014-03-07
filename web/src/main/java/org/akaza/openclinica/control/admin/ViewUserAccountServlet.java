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

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InconsistentStateException;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class ViewUserAccountServlet extends Controller {
	public static final String PATH = "ViewUserAccount";
	public static final String ARG_USER_ID = "userId";

	public static String getLink(int userId) {
		return PATH + '?' + ARG_USER_ID + '=' + userId;
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		if (!ub.isSysAdmin()) {
			addPageMessage(
					respage.getString("no_have_correct_privilege_current_study")
							+ respage.getString("change_study_contact_sysadmin"), request);
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int userId = fp.getInt(ARG_USER_ID, true);
		UserAccountDAO udao = getUserAccountDAO();

		UserAccountBean user = getBean(udao, userId);

		if (user.isActive()) {
			request.setAttribute("user", user);
		} else {
			throw new InconsistentStateException(Page.ADMIN_SYSTEM,
					resexception.getString("the_user_attemping_to_view_not_exists"));
		}
		// To provide the view with the correct date format pattern, locale
		// sensitive
		String pattn;
		pattn = ResourceBundleProvider.getFormatBundle().getString("date_format_string");
		request.setAttribute("dateFormatPattern", pattn);
		forwardPage(Page.VIEW_USER_ACCOUNT, request, response);
	}

	private UserAccountBean getBean(UserAccountDAO udao, int id) {
		UserAccountBean answer = (UserAccountBean) udao.findByPK(id);
		StudyDAO sdao = getStudyDAO();

		ArrayList roles = answer.getRoles();

		for (int i = 0; i < roles.size(); i++) {
			StudyUserRoleBean sur = (StudyUserRoleBean) roles.get(i);
			StudyBean sb = (StudyBean) sdao.findByPK(sur.getStudyId());
			sur.setStudyName(sb.getName());
			roles.set(i, sur);
		}
		answer.setRoles(roles);

		return answer;
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
