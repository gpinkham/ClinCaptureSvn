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

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TechAdminServlet.
 */
@SuppressWarnings("rawtypes")
@Component
public class TechAdminServlet extends SpringServlet {

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyDAO sdao = getStudyDAO();
		ArrayList allStudies = (ArrayList) sdao.findAll();

		UserAccountDAO udao = getUserAccountDAO();
		ArrayList allUsers = (ArrayList) udao.findAll();

		SubjectDAO subdao = getSubjectDAO();
		ArrayList allSubjects = (ArrayList) subdao.findAll();

		CRFDAO cdao = getCRFDAO();
		ArrayList allCrfs = (ArrayList) cdao.findAllCRFs(getCurrentStudy());

        StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		setToPanel(getResWord().getString("in_the_application"), "", request);
		if (allSubjects.size() > 0) {
			setToPanel(getResWord().getString("subjects"), Integer.toString(allSubjects.size()), request);
		}
		if (allUsers.size() > 0) {
			setToPanel(getResWord().getString("users"), Integer.toString(allUsers.size()), request);
		}
		if (allStudies.size() > 0) {
			setToPanel(getResWord().getString("studies"), Integer.toString(allStudies.size()), request);
		}
		if (allCrfs.size() > 0) {
			setToPanel(getResWord().getString("CRFs"), Integer.toString(allCrfs.size()), request);
		}
		forwardPage(Page.TECH_ADMIN_SYSTEM, request, response);
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
		if (!ub.isTechAdmin()) {
			throw new InsufficientPermissionException(Page.MENU,
					getResException().getString("you_may_not_perform_technical_admin_functions"), "1");
		}
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return SpringServlet.ADMIN_SERVLET_CODE;
	}
}
