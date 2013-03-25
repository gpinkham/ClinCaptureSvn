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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2009 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.Locale;

import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.hibernate.AuditUserLoginDao;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * Servlet for creating a table.
 * 
 * @author Krikor Krumlian
 */
public class AuditUserActivityServlet extends SecureController {

	private static final long serialVersionUID = 1L;
	private AuditUserLoginDao auditUserLoginDao;
	Locale locale;

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (!ub.isSysAdmin()) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study")
					+ respage.getString("change_study_contact_sysadmin"));
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}

		return;
	}

	@Override
	protected void processRequest() throws Exception {
		AuditUserLoginTableFactory factory = new AuditUserLoginTableFactory();
		factory.setAuditUserLoginDao(getAuditUserLoginDao());
		String auditUserLoginHtml = factory.createTable(request, response).render();
		request.setAttribute("auditUserLoginHtml", auditUserLoginHtml);
		forwardPage(Page.AUDIT_USER_ACTIVITY);

	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}

	public AuditUserLoginDao getAuditUserLoginDao() {
		auditUserLoginDao = this.auditUserLoginDao != null ? auditUserLoginDao
				: (AuditUserLoginDao) SpringServletAccess.getApplicationContext(context).getBean("auditUserLoginDao");
		return auditUserLoginDao;
	}
}
