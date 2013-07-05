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

import java.util.Locale;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * @author thickerson
 * 
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class StudyAuditLogServlet extends SecureController {

	Locale locale;

	public static String getLink(int userId) {
		return "AuditLogStudy";
	}

	/*
	 * TODO: Redo this servlet to run the audits per study subject for the study; need to add a studyId param
	 * and then use the StudySubjectDAO.findAllByStudyOrderByLabel() method to grab a lot of study subject beans and
	 * then return them much like in ViewStudySubjectAuditLogServet.process() currentStudy instead of studyId?
	 */
	@Override
	protected void processRequest() throws Exception {
		StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
		SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
		UserAccountDAO uadao = new UserAccountDAO(sm.getDataSource());

		StudyAuditLogTableFactory factory = new StudyAuditLogTableFactory();
		factory.setSubjectDao(sdao);
		factory.setStudySubjectDao(subdao);
		factory.setUserAccountDao(uadao);
		factory.setCurrentStudy(currentStudy);

		String auditLogsHtml = factory.createTable(request, response).render();
		request.setAttribute("auditLogsHtml", auditLogsHtml);

		forwardPage(Page.AUDIT_LOGS_STUDY);

	}

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		if (ub.isSysAdmin()) {
			return;
		}

		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR) || r.equals(Role.STUDY_MONITOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_director"), "1");
	}

}
