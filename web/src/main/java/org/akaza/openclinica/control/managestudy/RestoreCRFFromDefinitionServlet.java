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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "serial", "unchecked" })
@Component
public class RestoreCRFFromDefinitionServlet extends Controller {
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
		throw new InsufficientPermissionException(Page.STUDY_EVENT_DEFINITION_LIST,
				resexception.getString("not_study_director"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<EventDefinitionCRFBean> edcs = (List<EventDefinitionCRFBean>) request.getSession().getAttribute(
				"eventDefinitionCRFs");
		List<EventDefinitionCRFBean> childEdcs = (List<EventDefinitionCRFBean>) request.getSession().getAttribute(
				"childEventDefCRFs");
 
		String crfName;
		String pageMessage = respage.getString("please_choose_a_CRF_to_restore");

		String idString = request.getParameter("id");
		logger.info("crf id:" + idString);
		if (StringUtil.isBlank(idString)) {
			addPageMessage(pageMessage, request);
			forwardPage(Page.UPDATE_EVENT_DEFINITION1, request, response);
		} else {
			// event crf definition id
			int id = Integer.valueOf(idString.trim());
			CRFDAO crfdao = getCRFDAO();
			CRFVersionDAO cvdao = getCRFVersionDAO();
			for (EventDefinitionCRFBean edc : edcs) {
				if (edc.getCrfId() == id) {
					CRFBean crf = (CRFBean) crfdao.findByPK(edc.getCrfId());
					CRFVersionBean defaultCrfVersion = (CRFVersionBean) cvdao.findByPK(edc.getDefaultVersionId());
					if (crf.getStatus().getId() != Status.AVAILABLE.getId()) {
						pageMessage = respage.getString("restore_event_crf_failed_crf_is_not_available");
					} else if (edc.getVersions().size() == 0
							|| defaultCrfVersion.getStatusId() != Status.AVAILABLE.getId()) {
						pageMessage = respage.getString("restore_event_crf_failed_crf_version_is_not_available");
					} else {
						edc.setStatus(Status.AVAILABLE);
						edc.setOldStatus(Status.DELETED);
						//Restore children if any
						restoreRemovedChildEdcs(childEdcs, edc, cvdao);
						crfName = edc.getCrfName();
						pageMessage = crfName + " " + respage.getString("has_been_restored");
					}
				}
				if (edc.getParentId() == id) {
					edc.setStatus(Status.AVAILABLE);
					edc.setOldStatus(Status.DELETED);
				}
			}
			
			request.getSession().setAttribute("eventDefinitionCRFs", edcs);
			request.getSession().setAttribute("childEventDefCRFs", childEdcs);
			addPageMessage(pageMessage, request);
			forwardPage(Page.UPDATE_EVENT_DEFINITION1, request, response);
		}
	}
	
	/**
	 * Restores child edcs that were auto-removed during parent removal
	 * @param childEdcs
	 * @param parentEdc
	 * @param updatedEdcs
	 * @param cvdao
	 */
	private void restoreRemovedChildEdcs (List<EventDefinitionCRFBean> childEdcs, EventDefinitionCRFBean parentEdc, CRFVersionDAO cvdao) {
		for (EventDefinitionCRFBean childEdc : childEdcs) {
			if (childEdc.getParentId() == parentEdc.getId()) {
				//Get default version of child edc
				CRFVersionBean defaultCrfVersion = (CRFVersionBean) cvdao.findByPK(childEdc.getDefaultVersionId());
				//Restore childEdc if its default crf version is available
				if (defaultCrfVersion.getStatusId() == Status.AVAILABLE.getId()){
					childEdc.setStatus(Status.AVAILABLE);
					childEdc.setOldStatus(Status.AUTO_DELETED);
				}
			}
		}
	} 
}
