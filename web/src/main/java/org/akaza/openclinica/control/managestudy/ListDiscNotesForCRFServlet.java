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

/**
 *
 */

import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.ListDiscNotesForCRFTableFactory;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class ListDiscNotesForCRFServlet extends Controller {

	public static final String DISCREPANCY_NOTE_TYPE = "discrepancyNoteType";
	public static final String RESOLUTION_STATUS = "resolutionStatus";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR) || r.equals(Role.INVESTIGATOR)
				|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR) || r.equals(Role.STUDY_MONITOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		FormProcessor fp = new FormProcessor(request);
		// Determine whether to limit the displayed DN's to a certain DN type
		int resolutionStatus;
		try {
			resolutionStatus = Integer.parseInt(request.getParameter("resolutionStatus"));
		} catch (NumberFormatException nfe) {
			// Show all DN's
			resolutionStatus = -1;
		}

		// Determine whether we already have a collection of resolutionStatus
		// Ids, and if not
		// create a new attribute. If there is no resolution status, then the
		// Set object should be cleared,
		// because we do not have to save a set of filter IDs.
		boolean hasAResolutionStatus = resolutionStatus >= 1 && resolutionStatus <= 5;
		Set<Integer> resolutionStatusIds = (HashSet) request.getSession().getAttribute(RESOLUTION_STATUS);
		// remove the session if there is no resolution status
		if (!hasAResolutionStatus && resolutionStatusIds != null) {
			request.getSession().removeAttribute(RESOLUTION_STATUS);
			resolutionStatusIds = null;
		}
		if (hasAResolutionStatus) {
			if (resolutionStatusIds == null) {
				resolutionStatusIds = new HashSet<Integer>();
			}
			resolutionStatusIds.add(resolutionStatus);
			request.getSession().setAttribute(RESOLUTION_STATUS, resolutionStatusIds);
		}
		int discNoteType;
		try {
			discNoteType = Integer.parseInt(request.getParameter("type"));
		} catch (NumberFormatException nfe) {
			// Show all DN's
			discNoteType = -1;
		}
		request.setAttribute(DISCREPANCY_NOTE_TYPE, discNoteType);

		// checks which module the requests are from
		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);

		int definitionId = fp.getInt("defId");
		if (definitionId <= 0) {
			addPageMessage(respage.getString("please_choose_an_ED_ta_to_vies_details"), request);
			forwardPage(Page.LIST_DNOTES_FOR_CRF, request, response);
			return;
		}

		request.setAttribute("eventDefinitionId", definitionId);

		ListDiscNotesForCRFTableFactory factory = new ListDiscNotesForCRFTableFactory();
		factory.setStudyEventDefinitionDao(getStudyEventDefinitionDAO());
		factory.setSubjectDAO(getSubjectDAO());
		factory.setStudySubjectDAO(getStudySubjectDAO());
		factory.setStudyEventDAO(getStudyEventDAO());
		factory.setStudyBean(currentStudy);
		factory.setStudyGroupClassDAO(getStudyGroupClassDAO());
		factory.setSubjectGroupMapDAO(getSubjectGroupMapDAO());
		factory.setStudyDAO(getStudyDAO());
		factory.setStudyGroupDAO(getStudyGroupDAO());
		factory.setCurrentRole(currentRole);
		factory.setCurrentUser(ub);
		factory.setEventCRFDAO(getEventCRFDAO());
		factory.setEventDefintionCRFDAO(getEventDefinitionCRFDAO());
		factory.setCrfDAO(getCRFDAO());
		factory.setDiscrepancyNoteDAO(getDiscrepancyNoteDAO());
		factory.setDiscNoteType(discNoteType);
		factory.setModule(module);
		factory.setResolutionStatus(resolutionStatus);
		factory.setResolutionStatusIds(resolutionStatusIds);
		factory.setSelectedStudyEventDefinition((StudyEventDefinitionBean) getStudyEventDefinitionDAO().findByPK(
				definitionId));
		String listDiscNotesForCRFHtml = factory.createTable(request, response).render();
		request.setAttribute("listDiscNotesForCRFHtml", listDiscNotesForCRFHtml);
		request.setAttribute("defId", definitionId);

		forwardPage(Page.LIST_DNOTES_FOR_CRF, request, response);
	}
}
