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
 *
 */
package org.akaza.openclinica.control.techadmin;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;

@SuppressWarnings({ "rawtypes", "serial" })
public class TechAdminServlet extends SecureController {

	@Override
	protected void processRequest() throws Exception {
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		ArrayList allStudies = (ArrayList) sdao.findAll();

		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		ArrayList allUsers = (ArrayList) udao.findAll();

		SubjectDAO subdao = new SubjectDAO(sm.getDataSource());
		ArrayList allSubjects = (ArrayList) subdao.findAll();

		CRFDAO cdao = new CRFDAO(sm.getDataSource());
		ArrayList allCrfs = (ArrayList) cdao.findAll();

		resetPanel();

		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		setToPanel(resword.getString("in_the_application"), "");
		if (allSubjects.size() > 0) {
			setToPanel(resword.getString("subjects"), new Integer(allSubjects.size()).toString());
		}
		if (allUsers.size() > 0) {
			setToPanel(resword.getString("users"), new Integer(allUsers.size()).toString());
		}
		if (allStudies.size() > 0) {
			setToPanel(resword.getString("studies"), new Integer(allStudies.size()).toString());
		}
		if (allCrfs.size() > 0) {
			setToPanel(resword.getString("CRFs"), new Integer(allCrfs.size()).toString());
		}
		forwardPage(Page.TECH_ADMIN_SYSTEM);
	}

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		if (!ub.isTechAdmin()) {
			throw new InsufficientPermissionException(Page.MENU,
					resexception.getString("you_may_not_perform_technical_admin_functions"), "1");
		}

		return;
	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}
}
