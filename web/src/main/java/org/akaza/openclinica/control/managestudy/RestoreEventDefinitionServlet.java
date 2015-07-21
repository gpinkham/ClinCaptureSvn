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
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Restores a removed study event definition and all its related data.
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"serial"})
@Component
public class RestoreEventDefinitionServlet extends Controller {
	/**
     *
     */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.LIST_DEFINITION_SERVLET, respage.getString("current_study_locked"), request, response);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
				resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);
		UserAccountBean updater = getUserAccountBean(request);

		String idString = request.getParameter("id");
		int defId = Integer.valueOf(idString.trim());
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
				.findByPK(defId);
		studyEventDefinitionBean.setUpdater(updater);

		String action = request.getParameter("action");
		if (StringUtil.isBlank(idString)) {
			addPageMessage(respage.getString("please_choose_a_SED_to_restore"), request);
			forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				if (!studyEventDefinitionBean.getStatus().equals(Status.DELETED)) {
					addPageMessage(
							respage.getString("this_SED_cannot_be_restored") + " "
									+ respage.getString("please_contact_sysadmin_for_more_information"), request);
					forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
					return;
				}
				request.setAttribute("definitionToRestore", studyEventDefinitionBean);
				request.setAttribute("eventDefinitionCRFs",
						getEventDefinitionService().getAllEventDefinitionCrfs(studyEventDefinitionBean));
				request.setAttribute("events", getEventDefinitionService().getAllStudyEvents(studyEventDefinitionBean));
				forwardPage(Page.RESTORE_DEFINITION, request, response);
			} else {
				logger.info("submit to restore the definition");

				getEventDefinitionService().restoreStudyEventDefinition(studyEventDefinitionBean, updater);

				String emailBody = respage.getString("the_SED").concat(" ").concat(studyEventDefinitionBean.getName())
						.concat("(").concat(respage.getString("and_all_associated_event_data_restored_to_study"))
						.concat(currentStudy.getName()).concat(".");

				addPageMessage(emailBody, request);

				forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
			}

		}

	}

}
