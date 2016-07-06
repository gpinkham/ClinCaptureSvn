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

package org.akaza.openclinica.control.managestudy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * 
 * This servlet handles delete study event operations.
 *
 */
@Component
public class DeleteStudyEventServlet extends SpringServlet {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_locked"), request, response);
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_frozen"), request, response);

		if (ub.isSysAdmin() || Role.STUDY_ADMINISTRATOR.equals(currentRole.getRole())) {
			return;
		}

		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_study_director"),
				"1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean();
		FormProcessor fp = new FormProcessor(request);

		int studyEventId = fp.getInt("id"); // studyEventId
		int studySubId = fp.getInt("studySubId"); // studySubjectId

		if (studyEventId == 0) {
			addPageMessage(getResPage().getString("please_choose_a_SE_to_remove"), request);
			request.setAttribute("id", Integer.toString(studySubId));
			forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
		} else {
			StudySubjectBean studySub = getStudySubjectDAO().findByPK(studySubId);
			StudyEventBean event = (StudyEventBean) getStudyEventDAO().findByPK(studyEventId);
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
					.findByPK(event.getStudyEventDefinitionId());

			event.setStudyEventDefinition(sed);
			request.setAttribute("studySub", studySub);
			String action = request.getParameter("action");

			if ("confirm".equalsIgnoreCase(action)) {
				// construct info needed on view study event page
				DisplayStudyEventBean de = new DisplayStudyEventBean();
				de.setStudyEvent(event);
				request.setAttribute("displayEvent", de);
				forwardPage(Page.DELETE_STUDY_EVENT, request, response);
			} else {
				logger.info("submit to delete the event from study");
				// delete event from study
				getStudyEventService().deleteStudyEvent(sed, studySub, event, ub);
				response.sendRedirect(
						request.getContextPath() + Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName() + "?id=" + studySubId);
			}
		}
	}
}
