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
 * copyright 2003-2009 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Servlet for creating a table.
 * 
 * @author Krikor Krumlian
 */
@Component
@SuppressWarnings("unused")
public class AuditUserActivityServlet extends RememberLastPage {

	private static final long serialVersionUID = 1L;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (!ub.isSysAdmin()) {
			addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
					+ getResPage().getString("change_study_contact_sysadmin"), request);
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					getResException().getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (shouldRedirect(request, response)) {
			return;
		}
		AuditUserLoginTableFactory factory = new AuditUserLoginTableFactory();
		factory.setAuditUserLoginDao(getAuditUserLoginDao());
		factory.setCurrentUser(getUserAccountBean());
		String auditUserLoginHtml = factory.createTable(request, response).render();
		request.setAttribute("auditUserLoginHtml", auditUserLoginHtml);
		forwardPage(Page.AUDIT_USER_ACTIVITY, request, response);
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return SpringServlet.ADMIN_SERVLET_CODE;
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		String module = fp.getString("module") != null ? fp.getString("module") : "admin";
		return "?module=" + module + "&crfId=0&maxRows=15&userLogins_tr_=true&userLogins_p_=1&userLogins_mr_=15";
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null || !request.getQueryString().contains("&userLogins_p_=");
	}
}
