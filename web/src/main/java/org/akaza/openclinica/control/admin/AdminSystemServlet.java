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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class AdminSystemServlet extends Controller {

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// find last 5 modifed studies
		StudyDAO sdao = getStudyDAO();
		ArrayList studies = (ArrayList) sdao.findAllByLimit(true);
		request.setAttribute("studies", studies);
		ArrayList allStudies = (ArrayList) sdao.findAll();
		request.setAttribute("allStudyNumber", allStudies.size());

		UserAccountDAO udao = getUserAccountDAO();
		ArrayList users = (ArrayList) udao.findAllByLimit(true);
		request.setAttribute("users", users);
		ArrayList allUsers = (ArrayList) udao.findAll();
		request.setAttribute("allUserNumber", allUsers.size());

		SubjectDAO subdao = getSubjectDAO();
		ArrayList subjects = (ArrayList) subdao.findAllByLimit(true);
		request.setAttribute("subjects", subjects);
		ArrayList allSubjects = (ArrayList) subdao.findAll();
		request.setAttribute("allSubjectNumber", allSubjects.size());

		CRFDAO cdao = getCRFDAO();
		ArrayList crfs = (ArrayList) cdao.findAllByLimit(true);
		request.setAttribute("crfs", crfs);
		ArrayList allCrfs = (ArrayList) cdao.findAll();
		request.setAttribute("allCrfNumber", allCrfs.size());

		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
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

		panel.setStudyInfoShown(false);
		forwardPage(Page.ADMIN_SYSTEM, request, response);
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (!ub.isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU, "You may not perform administrative functions", "1");
		}
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
