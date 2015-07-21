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
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * Remove the reference to a CRF from a study event definition
 * 
 * @author jxu
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class RemoveCRFFromDefinitionServlet extends Controller {

	/**
	 * Checks whether the user has the correct privilege
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_permission_to_update_study_event_definition")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
				resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ArrayList<EventDefinitionCRFBean> edcs = (ArrayList<EventDefinitionCRFBean>) request.getSession().getAttribute(
				"eventDefinitionCRFs");
		ArrayList<EventDefinitionCRFBean> childEdcs = (ArrayList<EventDefinitionCRFBean>) request.getSession()
				.getAttribute("childEventDefCRFs");
		ArrayList updatedEdcs = new ArrayList();
		if (edcs != null && edcs.size() > 1) {
			String idString = request.getParameter("id");
			logger.info("crf id:" + idString);
			if (StringUtil.isBlank(idString)) {
				addPageMessage(respage.getString("please_choose_a_crf_to_remove"), request);
				forwardPage(Page.UPDATE_EVENT_DEFINITION1, request, response);
			} else {
				// crf id
				int id = Integer.valueOf(idString.trim());
				for (EventDefinitionCRFBean edc : edcs) {
					// Set edc status to deleted. Also make sure its child rows are also updated
					if (edc.getCrfId() == id) {
						edc.setStatus(Status.DELETED);
						// Update children if any
						setChildEdcsToRemoved(childEdcs, edc);
					}
					if (edc.getId() > 0 || !edc.getStatus().equals(Status.DELETED)) {
						updatedEdcs.add(edc);
						logger.info("\nversion:" + edc.getDefaultVersionId());
					}
				}
				request.getSession().setAttribute("eventDefinitionCRFs", updatedEdcs);
				request.getSession().setAttribute("childEventDefCRFs", childEdcs);
				addPageMessage(respage.getString("has_been_removed_need_confirmation"), request);
				forwardPage(Page.UPDATE_EVENT_DEFINITION1, request, response);
			}

		} else {
			addPageMessage(respage.getString("an_ED_needs_to_have_least_one_CRF"), request);
			forwardPage(Page.UPDATE_EVENT_DEFINITION1, request, response);
		}
	}

	private void setChildEdcsToRemoved(ArrayList<EventDefinitionCRFBean> childEdcs, EventDefinitionCRFBean parentEdc) {
		for (EventDefinitionCRFBean childEdc : childEdcs) {
			if (childEdc.getParentId() == parentEdc.getId()) {
				childEdc.setStatus(Status.DELETED);
			}
		}
	}
}
