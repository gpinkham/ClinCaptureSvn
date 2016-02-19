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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Removes study event definition and all its related data.
 */
@Component
public class RemoveEventDefinitionServlet extends SpringServlet {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.LIST_DEFINITION_SERVLET, getResPage().getString("current_study_locked"), request, response);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
				getResException().getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);
		UserAccountBean updater = getUserAccountBean(request);

		String idString = request.getParameter("id");
		int defId = Integer.parseInt(idString.trim());
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
				.findByPK(defId);

		if (currentStudy.getId() != studyEventDefinitionBean.getStudyId()) {
			addPageMessage(getResPage().getString("no_have_correct_privilege_current_study") + " "
					+ getResPage().getString("change_active_study_or_contact"), request);
			forwardPage(Page.MENU_SERVLET, request, response);
			return;
		}

		String action = request.getParameter("action");
		if (StringUtil.isBlank(idString)) {
			addPageMessage(getResPage().getString("please_choose_a_SED_to_remove"), request);
			forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				if (!studyEventDefinitionBean.getStatus().equals(Status.AVAILABLE)) {
					addPageMessage(getResPage().getString("this_SED_is_not_available_for_this_study")
							+ getResPage().getString("please_contact_sysadmin_for_more_information"), request);
					forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
					return;
				}
				request.setAttribute("definitionToRemove", studyEventDefinitionBean);
				request.setAttribute("eventDefinitionCRFs",
						getEventDefinitionService().getAllEventDefinitionCrfs(studyEventDefinitionBean));
				request.setAttribute("events", getEventDefinitionService().getAllStudyEvents(studyEventDefinitionBean));
				forwardPage(Page.REMOVE_DEFINITION, request, response);
			} else {
				logger.info("submit to remove the definition");

				getEventDefinitionService().removeStudyEventDefinition(studyEventDefinitionBean, updater);

				String emailBody = getResPage().getString("the_SED").concat(studyEventDefinitionBean.getName()).concat(" ")
						.concat(getResPage().getString("has_been_removed_from_the_study")).concat(currentStudy.getName())
						.concat(".");

				addPageMessage(emailBody, request);
				forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
			}
		}
	}
}
