/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.managestudy.ListEventsForSubjectsServlet;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Servlet for creating subject matrix page.
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class ListStudySubjectsServlet extends RememberLastPage {

	public static final String SUBJECT_MATRIX_PAGE_SIZE = "subjectMatrixPageSize";
	public static final String SAVED_LIST_STUDY_SUBJECTS_URL = "savedListStudySubjectsUrl";
	public static final String CURRENT_SUBJECT_MATRIX_SERVLET = "currentSubjectMatrixServlet";

	private static final long serialVersionUID = 1L;

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole != null
				&& (currentRole.getRole().equals(Role.SYSTEM_ADMINISTRATOR)
						|| currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
						|| currentRole.getRole().equals(Role.STUDY_DIRECTOR)
						|| currentRole.getRole().equals(Role.INVESTIGATOR)
						|| currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR) || Role
							.isMonitor(currentRole.getRole()))) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		if (fp.getString("navBar").equalsIgnoreCase("yes")) {
			StudySubjectBean studySubject = getStudySubjectDAO().findByLabelAndStudy(
					fp.getString("findSubjects_f_studySubject.label"), currentStudy);
			if (studySubject.getId() > 0) {
				Stack<String> visitedURLs = (Stack<String>) request.getSession().getAttribute("visitedURLs");
				visitedURLs.pop();
				response.sendRedirect(request.getContextPath() + Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName() + "?id="
						+ Integer.toString(studySubject.getId()) + "&ref=sm");
				return;
			} else {
				request.getSession().removeAttribute(getUrlKey(request));
			}
		}

		if (shouldRedirect(request, response)) {
			return;
		}

		Controller.removeLockedCRF(ub.getId());

		boolean showMoreLink;
		showMoreLink = fp.getString("showMoreLink").equals("") || Boolean.parseBoolean(fp.getString("showMoreLink"));

		String idSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();
		// set up auto study subject id
		if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {

			String nextLabel = getStudySubjectDAO().findNextLabel(currentStudy);
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
		request.setAttribute("findSubjectsHtml", createTable(request, response, showMoreLink));
		request.setAttribute("allDefsArray", getEventDefinitionsByCurrentStudy(request));
		request.setAttribute("studyGroupClasses", getStudyGroupClassesByCurrentStudy(request));
		request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, new FormDiscrepancyNotes());
		forwardPage(Page.LIST_STUDY_SUBJECTS, request, response);
	}

	/**
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param showMoreLink boolean
	 * @return String
	 * @throws Exception exception
	 */
	public String createTable(HttpServletRequest request, HttpServletResponse response, boolean showMoreLink)
			throws Exception {

		ListStudySubjectTableFactory factory = new ListStudySubjectTableFactory(showMoreLink);
		factory.setStudyEventDefinitionDao(getStudyEventDefinitionDAO());
		factory.setSubjectDAO(getSubjectDAO());
		factory.setStudySubjectDAO(getStudySubjectDAO());
		factory.setStudyEventDAO(getStudyEventDAO());
		factory.setStudyBean(getCurrentStudy(request));
		factory.setStudyGroupClassDAO(getStudyGroupClassDAO());
		factory.setSubjectGroupMapDAO(getSubjectGroupMapDAO());
		factory.setStudyDAO(getStudyDAO());
		factory.setCrfVersionDAO(getCRFVersionDAO());
		factory.setCurrentRole(getCurrentRole(request));
		factory.setCurrentUser(getUserAccountBean(request));
		factory.setEventCRFDAO(getEventCRFDAO());
		factory.setEventDefintionCRFDAO(getEventDefinitionCRFDAO());
		factory.setDiscrepancyNoteDAO(getDiscrepancyNoteDAO());
		factory.setStudyGroupDAO(getStudyGroupDAO());
		factory.setDynamicEventDao(getDynamicEventDao());
		return factory.createTable(request, response).render();
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return ADMIN_SERVLET_CODE;
	}

	@Override
	protected String getUrlKey(HttpServletRequest request) {
		return SAVED_LIST_STUDY_SUBJECTS_URL;
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		boolean showMoreLink = fp.getString("showMoreLink").equals("")
				|| Boolean.parseBoolean(fp.getString("showMoreLink"));
		String pageSize = (String) request.getSession().getAttribute(SUBJECT_MATRIX_PAGE_SIZE);
		pageSize = pageSize == null ? "15" : pageSize;
		return "?module="
				+ fp.getString("module")
				+ "&maxRows="
				+ pageSize
				+ "&showMoreLink="
				+ showMoreLink
				+ "&findSubjects_tr_=true&findSubjects_p_=1&findSubjects_mr_="
				+ pageSize
				+ "&findSubjects_s_0_studySubject.createdDate=desc"
				+ (fp.getString("navBar").equalsIgnoreCase("yes") ? ("&findSubjects_f_studySubject.label=" + fp
						.getString("findSubjects_f_studySubject.label")) : "");
	}

	@Override
	protected void saveUrl(String key, String value, HttpServletRequest request) {
		super.saveUrl(key, value, request);
		FormProcessor fp = new FormProcessor(request);
		String pageSize = fp.getString("findSubjects_mr_");
		request.getSession().setAttribute(ListStudySubjectsServlet.SUBJECT_MATRIX_PAGE_SIZE, pageSize);
		String savedUrl = getSavedUrl(ListEventsForSubjectsServlet.SAVED_LIST_EVENTS_FOR_SUBJECTS_URL, request);
		if (savedUrl != null) {
			super.saveUrl(ListEventsForSubjectsServlet.SAVED_LIST_EVENTS_FOR_SUBJECTS_URL,
					savedUrl.replaceAll("listEventsForSubject_mr_=\\d*", "listEventsForSubject_mr_=".concat(pageSize)),
					request);
		}
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null || !request.getQueryString().contains("&findSubjects_p_=");
	}

	@Override
	protected boolean shouldRedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (request.getQueryString() == null) {
			String currentSubjectMatrixServlet = (String) request.getSession().getAttribute(
					CURRENT_SUBJECT_MATRIX_SERVLET);
			if (currentSubjectMatrixServlet != null && !currentSubjectMatrixServlet.equals(request.getServletPath())) {
				response.sendRedirect(request.getContextPath().concat(currentSubjectMatrixServlet));
				return true;
			}
		}
		request.getSession().setAttribute(CURRENT_SUBJECT_MATRIX_SERVLET, request.getServletPath());
		return super.shouldRedirect(request, response);
	}
}
