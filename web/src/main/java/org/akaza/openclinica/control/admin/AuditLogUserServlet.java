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
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.AuditEventRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class AuditLogUserServlet extends SecureController {

	Locale locale;

	public static final String ARG_USERID = "userLogId";

	public static String getLink(int userId) {
		return "AuditLogUser?userLogId=" + userId;
	}

	@Override
	protected void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int userId = fp.getInt(ARG_USERID);
		if (userId == 0) {
			Integer userIntId = (Integer) session.getAttribute(ARG_USERID);
			userId = userIntId.intValue();
		} else {
			session.setAttribute(ARG_USERID, new Integer(userId));
		}
		AuditEventDAO aeDAO = new AuditEventDAO(sm.getDataSource());
		ArrayList al = aeDAO.findAllByUserId(userId);

		EntityBeanTable table = fp.getEntityBeanTable();
		ArrayList allRows = AuditEventRow.generateRowsFromBeans(al);

		String[] columns = {
				resword.getString("date_and_time"),
				resword.getString("action_message"),
				resword.getString("entity_operation"),
				resword.getString("study_site"),
				currentStudy == null ? resword.getString("study_subject_ID") : currentStudy.getStudyParameterConfig()
						.getStudySubjectIdLabel(), resword.getString("changes_and_additions"),
				resword.getString("actions") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.setAscendingSort(false);
		table.hideColumnLink(1);
		table.hideColumnLink(5);
		table.hideColumnLink(6);
		table.setQuery("AuditLogUser?userLogId=" + userId, new HashMap());
		table.setRows(allRows);

		table.computeDisplay();

		request.setAttribute("table", table);
		UserAccountDAO uadao = new UserAccountDAO(sm.getDataSource());
		UserAccountBean uabean = (UserAccountBean) uadao.findByPK(userId);
		request.setAttribute("auditUserBean", uabean);
		forwardPage(Page.AUDIT_LOG_USER);
	}

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (!ub.isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU,
					resexception.getString("may_not_perform_administrative_functions"), "1");
		}
		return;
	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}

}
