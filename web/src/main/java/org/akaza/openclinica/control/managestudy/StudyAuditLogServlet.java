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
 * Created on Sep 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.akaza.openclinica.control.managestudy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * @author thickerson
 * 
 * 
 */
@SuppressWarnings({"serial"})
@Component
public class StudyAuditLogServlet extends RememberLastPage {

	public static String getLink(int userId) {
		return "AuditLogStudy";
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO: Redo this servlet to run the audits per study subject for the study; need to add a studyId param and
		// then use the StudySubjectDAO.findAllByStudyOrderByLabel() method to grab a lot of study subject beans and
		// then return them much like in ViewStudySubjectAuditLogServet.process() currentStudy instead of studyId?

		if (shouldRedirect(request, response)) {
			return;
		}
		StudyBean currentStudy = getCurrentStudy(request);

		StudySubjectDAO subdao = getStudySubjectDAO();
		SubjectDAO sdao = getSubjectDAO();
		UserAccountDAO uadao = getUserAccountDAO();

		StudyAuditLogTableFactory factory = new StudyAuditLogTableFactory();
		factory.setSubjectDao(sdao);
		factory.setStudySubjectDao(subdao);
		factory.setUserAccountDao(uadao);
		factory.setCurrentStudy(currentStudy);

		String auditLogsHtml = factory.createTable(request, response).render();
		request.setAttribute("auditLogsHtml", auditLogsHtml);

		forwardPage(Page.AUDIT_LOGS_STUDY, request, response);

	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR) || r.equals(Role.STUDY_SPONSOR)
				|| Role.isMonitor(r)) {
			return;
		}

		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_director"), "1");
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		String module = fp.getString("module") != null ? fp.getString("module") : "submit";
		return "?module=" + module + "&maxRows=15&studyAuditLogs_tr_=true&studyAuditLogs_p_=1&studyAuditLogs_mr_=15";
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null || !request.getQueryString().contains("&studyAuditLogs_p_=");
	}

}
