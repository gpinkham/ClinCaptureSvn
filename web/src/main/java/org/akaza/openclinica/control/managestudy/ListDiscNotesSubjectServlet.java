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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.submit.ListDiscNotesSubjectTableFactory;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class ListDiscNotesSubjectServlet extends Controller {
	public static final String RESOLUTION_STATUS = "resolutionStatus";
	// Include extra path info on the URL, which generates a file name hint in
	// some
	// browser's "save as..." dialog boxes
	public static final String DISCREPANCY_NOTE_TYPE = "discrepancyNoteType";
	public static final String FILTER_SUMMARY = "filterSummary";

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		String module = request.getParameter("module");
		String moduleStr = "manage";
		if (module != null && module.trim().length() > 0) {
			if ("submit".equals(module)) {
				request.setAttribute("module", "submit");
				moduleStr = "submit";
			} else if ("admin".equals(module)) {
				request.setAttribute("module", "admin");
				moduleStr = "admin";
			} else {
				request.setAttribute("module", "manage");
			}
		}
		// Filter out the entire module parameter to catch injections

		// Close the info side panel and show icons
		request.setAttribute("closeInfoShowIcons", true);
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

		DiscrepancyNoteUtil discNoteUtil = new DiscrepancyNoteUtil();
		// Generate a summary of how we are filtering;
		Map<String, List<String>> filterSummary = discNoteUtil.generateFilterSummary(discNoteType, resolutionStatusIds);

		if (!filterSummary.isEmpty()) {
			request.setAttribute(FILTER_SUMMARY, filterSummary);
		}

		Map stats = discNoteUtil.generateDiscNoteSummaryRefactored(getDataSource(), currentStudy, resolutionStatusIds,
				discNoteType);
		request.setAttribute("summaryMap", stats);
		Set mapKeys = stats.keySet();
		request.setAttribute("mapKeys", mapKeys);

		StudyDAO studyDAO = getStudyDAO();
		StudySubjectDAO sdao = getStudySubjectDAO();
		StudyEventDAO sedao = getStudyEventDAO();
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		SubjectGroupMapDAO sgmdao = getSubjectGroupMapDAO();
		StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
		StudyGroupDAO sgdao = getStudyGroupDAO();
		EventCRFDAO edao = getEventCRFDAO();
		EventDefinitionCRFDAO eddao = getEventDefinitionCRFDAO();
		SubjectDAO subdao = getSubjectDAO();
		DiscrepancyNoteDAO dnDAO = getDiscrepancyNoteDAO();

		ListDiscNotesSubjectTableFactory factory = new ListDiscNotesSubjectTableFactory(
				ResourceBundleProvider.getTermsBundle(request.getLocale()));
		factory.setStudyEventDefinitionDao(seddao);
		factory.setSubjectDAO(subdao);
		factory.setStudySubjectDAO(sdao);
		factory.setStudyEventDAO(sedao);
		factory.setStudyBean(currentStudy);
		factory.setStudyGroupClassDAO(sgcdao);
		factory.setSubjectGroupMapDAO(sgmdao);
		factory.setStudyDAO(studyDAO);
		factory.setCurrentRole(currentRole);
		factory.setCurrentUser(ub);
		factory.setEventCRFDAO(edao);
		factory.setEventDefintionCRFDAO(eddao);
		factory.setStudyGroupDAO(sgdao);
		factory.setDiscrepancyNoteDAO(dnDAO);

		factory.setModule(moduleStr);
		factory.setDiscNoteType(discNoteType);
		factory.setResolutionStatus(resolutionStatus);
		factory.setResolutionStatusIds(resolutionStatusIds);
		factory.setResword(ResourceBundleProvider.getWordsBundle(request.getLocale()));
		String listDiscNotesHtml = factory.createTable(request, response).render();
		request.setAttribute("listDiscNotesHtml", listDiscNotesHtml);

		forwardPage(getJSP(), request, response);
	}

	/**
	 * Checks whether the user has the right permission to proceed function
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

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	protected Page getJSP() {
		return Page.LIST_SUBJECT_DISC_NOTE;
	}

}
