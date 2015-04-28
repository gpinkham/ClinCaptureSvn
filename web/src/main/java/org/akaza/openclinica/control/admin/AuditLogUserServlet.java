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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.AuditEventRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.springframework.stereotype.Component;

@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@Component
public class AuditLogUserServlet extends Controller {

	public static final String ARG_USERID = "userLogId";

	public static String getLink(int userId) {
		return "AuditLogUser?userLogId=" + userId;
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		FormProcessor fp = new FormProcessor(request);
		int userId = fp.getInt(ARG_USERID);
		if (userId == 0) {
			userId = (Integer) request.getSession().getAttribute(ARG_USERID);
		} else {
			request.getSession().setAttribute(ARG_USERID, userId);
		}
		AuditEventDAO aeDAO = getAuditEventDAO();
		ArrayList al = aeDAO.findAllByUserId(userId);

		EntityBeanTable table = getEntityBeanTable();
		ArrayList allRows = AuditEventRow.generateRowsFromBeans(al);

		String[] columns = {
				resword.getString("date_and_time"),
				resword.getString("action_message"),
				resword.getString("entity_operation"),
				resword.getString("study_site"),
				currentStudy == null ? resword.getString("study_subject_ID") : currentStudy.getStudyParameterConfig()
						.getStudySubjectIdLabel(), resword.getString("changes_and_additions"),
				resword.getString("actions")};
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.setAscendingSort(false);
		table.hideColumnLink(1);
		table.hideColumnLink(5);
		table.hideColumnLink(6);
		table.setQuery("AuditLogUser?userLogId=" + userId, new HashMap());
		table.setRows(allRows);

		table.computeDisplay();

		request.setAttribute("table", table);
		UserAccountDAO uadao = getUserAccountDAO();
		UserAccountBean uabean = (UserAccountBean) uadao.findByPK(userId);
		request.setAttribute("auditUserBean", uabean);
		forwardPage(Page.AUDIT_LOG_USER, request, response);
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (!ub.isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU,
					resexception.getString("may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}

}
