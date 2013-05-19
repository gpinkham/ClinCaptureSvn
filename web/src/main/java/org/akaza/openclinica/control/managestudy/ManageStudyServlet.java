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

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Generates the index page of manage study module
 * 
 * @author ssachs
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class ManageStudyServlet extends SecureController {

	Locale locale;
	public final List<String> INSTRUCTIONS = new ArrayList<String>();

	@Override
	protected void processRequest() throws Exception {
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
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		ArrayList allSites = (ArrayList) sdao.findAllByParent(currentStudy.getId());
		ArrayList sites = new ArrayList();
		for (int i = 0; i < allSites.size(); i++) {
			sites.add(allSites.get(i));
			if (i == 5) {
				break;
			}
		}
		request.setAttribute("sites", sites);
		request.setAttribute("sitesCount", new Integer(sites.size()));
		request.setAttribute("allSitesCount", new Integer(allSites.size()));
		if (currentStudy != null) {
			request.setAttribute("studyIdentifier", currentStudy.getIdentifier());
		}

		StudyEventDefinitionDAO edao = new StudyEventDefinitionDAO(sm.getDataSource());
		ArrayList seds = (ArrayList) edao.findAllByStudyAndLimit(currentStudy.getId());
		ArrayList allSeds = edao.findAllByStudy(currentStudy);
		request.setAttribute("seds", seds);
		request.setAttribute("sedsCount", new Integer(seds.size()));
		request.setAttribute("allSedsCount", new Integer(allSeds.size()));

		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		ArrayList users = udao.findAllUsersByStudyIdAndLimit(currentStudy.getId(), true);
		ArrayList allUsers = udao.findAllUsersByStudy(currentStudy.getId());
		request.setAttribute("users", users);
		request.setAttribute("usersCount", new Integer(users.size()));
		request.setAttribute("allUsersCount", new Integer(allUsers.size()));

		StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
		ArrayList allSubjects = ssdao.findAllByStudyId(currentStudy.getId());
		ArrayList subjects = new ArrayList();
		for (int i = 0; i < allSubjects.size(); i++) {
			subjects.add(allSubjects.get(i));
			if (i == 5) {
				break;
			}
		}
		request.setAttribute("subs", subjects);
		request.setAttribute("subsCount", new Integer(subjects.size()));
		request.setAttribute("allSubsCount", new Integer(allSubjects.size()));

		resetPanel();

		if (allSubjects.size() > 0) {
			setToPanel("Subjects", new Integer(allSubjects.size()).toString());
		}
		if (allUsers.size() > 0) {
			setToPanel("Users", new Integer(allUsers.size()).toString());
		}
		if (allSites.size() > 0) {
			setToPanel("Sites", new Integer(allSites.size()).toString());
		}
		if (allSeds.size() > 0) {
			setToPanel("Event Definitions", new Integer(allSeds.size()).toString());
		}
		String proto = request.getParameter("proto");
		if (proto == null || "".equalsIgnoreCase(proto)) {
			forwardPage(Page.MANAGE_STUDY);
		} else {
			forwardPage(Page.MANAGE_STUDY_BODY);
		}
	}

	/**
	 * Checks whether the user has the correct privilege
	 */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}

		Role r = currentRole.getRole();
		if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, restext.getString("not_study_director"), "1");// TODO
	}

}
