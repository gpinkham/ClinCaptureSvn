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
 * copyright 2003-2009 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for creating a table.
 * 
 * @author Krikor Krumlian
 */
@SuppressWarnings({ "rawtypes" })
@Component
public class ListStudySubjectsServlet extends RememberLastPage {

	public static final String SAVED_SUBJECT_MATRIX_URL = "savedSubjectMatrixUrl";
	private static final long serialVersionUID = 1L;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
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
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (shouldRedirect(request, response)) {
			return;
		}

		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		Controller.removeLockedCRF(ub.getId());
		FormProcessor fp = new FormProcessor(request);

		boolean showMoreLink;
		if (fp.getString("showMoreLink").equals("")) {
			showMoreLink = true;
		} else {
			showMoreLink = Boolean.parseBoolean(fp.getString("showMoreLink"));
		}

		String idSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();
		// set up auto study subject id
		if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {

			String nextLabel = getStudySubjectDAO().findNextLabel(currentStudy.getIdentifier());
			request.setAttribute("label", nextLabel);
			fp.addPresetValue("label", nextLabel);

		}

		if (fp.getRequest().getParameter("subjectOverlay") == null) {
			Date today = new Date(System.currentTimeMillis());
			String todayFormatted = getLocalDf(request).format(today);
			if (request.getAttribute(PRESET_VALUES) != null) {
				fp.setPresetValues((HashMap) request.getAttribute(PRESET_VALUES));
			}
			fp.addPresetValue(AddNewSubjectServlet.INPUT_ENROLLMENT_DATE, todayFormatted);
			fp.addPresetValue(AddNewSubjectServlet.INPUT_EVENT_START_DATE, todayFormatted);
			setPresetValues(fp.getPresetValues(), request);
		}

		request.setAttribute("closeInfoShowIcons", true);
		if (fp.getString("navBar").equals("yes")
				&& fp.getString("findSubjects_f_studySubject.label").trim().length() > 0) {
			StudySubjectBean studySubject = getStudySubjectDAO().findByLabelAndStudy(
					fp.getString("findSubjects_f_studySubject.label"), currentStudy);
			if (studySubject.getId() > 0) {
				request.setAttribute("id", new Integer(studySubject.getId()).toString());
				forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
			} else {
				createTable(request, response, showMoreLink);
			}
		} else {
			createTable(request, response, showMoreLink);
		}

	}

	private void createTable(HttpServletRequest request, HttpServletResponse response, boolean showMoreLink)
			throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		ListStudySubjectTableFactory factory = new ListStudySubjectTableFactory(showMoreLink);
		factory.setStudyEventDefinitionDao(getStudyEventDefinitionDAO());
		factory.setSubjectDAO(getSubjectDAO());
		factory.setStudySubjectDAO(getStudySubjectDAO());
		factory.setStudyEventDAO(getStudyEventDAO());
		factory.setStudyBean(currentStudy);
		factory.setStudyGroupClassDAO(getStudyGroupClassDAO());
		factory.setSubjectGroupMapDAO(getSubjectGroupMapDAO());
		factory.setStudyDAO(getStudyDAO());
		factory.setCurrentRole(currentRole);
		factory.setCurrentUser(ub);
		factory.setEventCRFDAO(getEventCRFDAO());
		factory.setEventDefintionCRFDAO(getEventDefinitionCRFDAO());
		factory.setDiscrepancyNoteDAO(getDiscrepancyNoteDAO());
		factory.setStudyGroupDAO(getStudyGroupDAO());
		factory.setDynamicEventDao(getDynamicEventDao());
		String findSubjectsHtml = factory.createTable(request, response).render();
		request.setAttribute("findSubjectsHtml", findSubjectsHtml);
		request.setAttribute("allDefsArray", getEventDefinitionsByCurrentStudy(request));
		request.setAttribute("studyGroupClasses", getStudyGroupClassesByCurrentStudy(request));

		FormDiscrepancyNotes discNotes = new FormDiscrepancyNotes();
		request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

		forward(Page.LIST_STUDY_SUBJECTS, request, response);
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return ADMIN_SERVLET_CODE;
	}

	@Override
	protected String getUrlKey() {
		return SAVED_SUBJECT_MATRIX_URL;
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {
		boolean showMoreLink;
		FormProcessor fp = new FormProcessor(request);
		if (fp.getString("showMoreLink").equals("")) {
			showMoreLink = true;
		} else {
			showMoreLink = Boolean.parseBoolean(fp.getString("showMoreLink"));
		}
		String savedUrl = (String) request.getSession().getAttribute(SAVED_SUBJECT_MATRIX_URL);
		savedUrl = savedUrl != null ? savedUrl.replaceAll(".*" + request.getContextPath() + "/ListStudySubjects", "")
				: savedUrl;
		return request.getMethod().equalsIgnoreCase("POST") && savedUrl != null ? savedUrl
				: "?module="
						+ fp.getString("module")
						+ "&maxRows=15&showMoreLink="
						+ showMoreLink
						+ "&findSubjects_tr_=true&findSubjects_p_=1&findSubjects_mr_=15&findSubjects_s_0_studySubject.createdDate=desc";
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null || !request.getQueryString().contains("&findSubjects_p_=");
	}

}
