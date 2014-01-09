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
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Generates the index page of manage study module
 * 
 * @author ssachs
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class ManageStudyServlet extends Controller {

	public final List<String> INSTRUCTIONS = new ArrayList<String>();

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		if (!INSTRUCTIONS.isEmpty()) {
			INSTRUCTIONS.clear();
		}
		INSTRUCTIONS.add(restext.getString("director_coordinator_privileges_manage"));
		INSTRUCTIONS.add(restext.getString("side_tables_shows_last_modified"));
		request.setAttribute("instructions", INSTRUCTIONS);
		// show icon keys on the sidebar, and display the instructions and
		// alert messages fields
		request.setAttribute("showIcons", true);
		request.setAttribute("openIcons", true);
		request.setAttribute("openAlerts", true);
		request.setAttribute("openInstructions", true);

		// find last 5 modifed sites
		StudyDAO sdao = getStudyDAO();
		ArrayList allSites = (ArrayList) sdao.findAllByParent(currentStudy.getId());
		ArrayList sites = new ArrayList();
		for (int i = 0; i < allSites.size(); i++) {
			sites.add(allSites.get(i));
			if (i == 5) {
				break;
			}
		}
		request.setAttribute("sites", sites);
		request.setAttribute("sitesCount", sites.size());
		request.setAttribute("allSitesCount", allSites.size());
		if (currentStudy != null) {
			request.setAttribute("studyIdentifier", currentStudy.getIdentifier());
		}

		StudyEventDefinitionDAO edao = getStudyEventDefinitionDAO();
		ArrayList seds = (ArrayList) edao.findAllByStudyAndLimit(currentStudy != null ? currentStudy.getId() : 0);
		ArrayList allSeds = edao.findAllByStudy(currentStudy);
		request.setAttribute("seds", seds);
		request.setAttribute("sedsCount", seds.size());
		request.setAttribute("allSedsCount", allSeds.size());

		UserAccountDAO udao = getUserAccountDAO();
		ArrayList users = udao.findAllUsersByStudyIdAndLimit(currentStudy != null ? currentStudy.getId() : 0, true);
		ArrayList allUsers = udao.findAllUsersByStudy(currentStudy != null ? currentStudy.getId() : 0);
		request.setAttribute("users", users);
		request.setAttribute("usersCount", users.size());
		request.setAttribute("allUsersCount", allUsers.size());

		StudySubjectDAO ssdao = getStudySubjectDAO();
		ArrayList allSubjects = ssdao.findAllByStudyId(currentStudy != null ? currentStudy.getId() : 0);
		ArrayList subjects = new ArrayList();
		for (int i = 0; i < allSubjects.size(); i++) {
			subjects.add(allSubjects.get(i));
			if (i == 5) {
				break;
			}
		}
		request.setAttribute("subs", subjects);
		request.setAttribute("subsCount", subjects.size());
		request.setAttribute("allSubsCount", allSubjects.size());

		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();

		if (allSubjects.size() > 0) {
			setToPanel("Subjects", Integer.toString(allSubjects.size()), request);
		}
		if (allUsers.size() > 0) {
			setToPanel("Users", Integer.toString(allUsers.size()), request);
		}
		if (allSites.size() > 0) {
			setToPanel("Sites", Integer.toString(allSites.size()), request);
		}
		if (allSeds.size() > 0) {
			setToPanel("Event Definitions", Integer.toString(allSeds.size()), request);
		}
		String proto = request.getParameter("proto");
		if (proto == null || "".equalsIgnoreCase(proto)) {
			forwardPage(Page.MANAGE_STUDY, request, response);
		} else {
			forwardPage(Page.MANAGE_STUDY_BODY, request, response);
		}
	}

	/**
	 * Checks whether the user has the correct privilege
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, restext.getString("not_study_director"), "1");// TODO
	}

}
