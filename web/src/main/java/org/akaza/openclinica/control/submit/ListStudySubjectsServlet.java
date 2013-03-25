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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2009 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.RememberLastPage;
import org.akaza.openclinica.control.core.CoreSecureController;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.*;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.Locale;
import java.util.Date;
import java.util.HashMap;

/**
 * Servlet for creating a table.
 * 
 * @author Krikor Krumlian
 */
@SuppressWarnings({ "rawtypes" })
public class ListStudySubjectsServlet extends RememberLastPage {

	public static final String SAVED_SUBJECT_MATRIX_URL = "savedSubjectMatrixUrl";
	private static final long serialVersionUID = 1L;
	private StudyEventDefinitionDAO studyEventDefinitionDAO;
	private SubjectDAO subjectDAO;
	private StudySubjectDAO studySubjectDAO;
	private StudyEventDAO studyEventDAO;
	private StudyGroupClassDAO studyGroupClassDAO;
	private SubjectGroupMapDAO subjectGroupMapDAO;
	private StudyDAO studyDAO;
	private EventCRFDAO eventCRFDAO;
	private EventDefinitionCRFDAO eventDefintionCRFDAO;
	private DiscrepancyNoteDAO discrepancyNoteDAO;
	private StudyGroupDAO studyGroupDAO;
	private boolean showMoreLink;
	Locale locale;

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	@Override
	protected void processRequest() throws Exception {
		analyzeUrl();
		CoreSecureController.removeLockedCRF(ub.getId());
		FormProcessor fp = new FormProcessor(request);
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
			String todayFormatted = local_df.format(today);
			if (request.getAttribute(PRESET_VALUES) != null) {
				fp.setPresetValues((HashMap) request.getAttribute(PRESET_VALUES));
			}
			fp.addPresetValue(AddNewSubjectServlet.INPUT_ENROLLMENT_DATE, todayFormatted);
			fp.addPresetValue(AddNewSubjectServlet.INPUT_EVENT_START_DATE, todayFormatted);
			setPresetValues(fp.getPresetValues());
		}

		request.setAttribute("closeInfoShowIcons", true);
		if (fp.getString("navBar").equals("yes")
				&& fp.getString("findSubjects_f_studySubject.label").trim().length() > 0) {
			StudySubjectBean studySubject = getStudySubjectDAO().findByLabelAndStudy(
					fp.getString("findSubjects_f_studySubject.label"), currentStudy);
			if (studySubject.getId() > 0) {
				request.setAttribute("id", new Integer(studySubject.getId()).toString());
				forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
			} else {
				createTable();
			}
		} else {
			createTable();
		}

	}

	private void createTable() throws Exception {

		ListStudySubjectTableFactory factory = new ListStudySubjectTableFactory(showMoreLink);
		factory.setStudyEventDefinitionDao(getStudyEventDefinitionDao());
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
		String findSubjectsHtml = factory.createTable(request, response).render();
		request.setAttribute("findSubjectsHtml", findSubjectsHtml);
		request.setAttribute("allDefsArray", super.getEventDefinitionsByCurrentStudy());
		request.setAttribute("studyGroupClasses", super.getStudyGroupClassesByCurrentStudy());
		FormDiscrepancyNotes discNotes = new FormDiscrepancyNotes();
		session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

		analyzeForward(Page.LIST_STUDY_SUBJECTS);
	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}

	public StudyEventDefinitionDAO getStudyEventDefinitionDao() {
		studyEventDefinitionDAO = studyEventDefinitionDAO == null ? new StudyEventDefinitionDAO(sm.getDataSource())
				: studyEventDefinitionDAO;
		return studyEventDefinitionDAO;
	}

	public SubjectDAO getSubjectDAO() {
		subjectDAO = this.subjectDAO == null ? new SubjectDAO(sm.getDataSource()) : subjectDAO;
		return subjectDAO;
	}

	public StudySubjectDAO getStudySubjectDAO() {
		studySubjectDAO = this.studySubjectDAO == null ? new StudySubjectDAO(sm.getDataSource()) : studySubjectDAO;
		return studySubjectDAO;
	}

	public StudyGroupClassDAO getStudyGroupClassDAO() {
		studyGroupClassDAO = this.studyGroupClassDAO == null ? new StudyGroupClassDAO(sm.getDataSource())
				: studyGroupClassDAO;
		return studyGroupClassDAO;
	}

	public SubjectGroupMapDAO getSubjectGroupMapDAO() {
		subjectGroupMapDAO = this.subjectGroupMapDAO == null ? new SubjectGroupMapDAO(sm.getDataSource())
				: subjectGroupMapDAO;
		return subjectGroupMapDAO;
	}

	public StudyEventDAO getStudyEventDAO() {
		studyEventDAO = this.studyEventDAO == null ? new StudyEventDAO(sm.getDataSource()) : studyEventDAO;
		return studyEventDAO;
	}

	public StudyDAO getStudyDAO() {
		studyDAO = this.studyDAO == null ? new StudyDAO(sm.getDataSource()) : studyDAO;
		return studyDAO;
	}

	public EventCRFDAO getEventCRFDAO() {
		eventCRFDAO = this.eventCRFDAO == null ? new EventCRFDAO(sm.getDataSource()) : eventCRFDAO;
		return eventCRFDAO;
	}

	public EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
		eventDefintionCRFDAO = this.eventDefintionCRFDAO == null ? new EventDefinitionCRFDAO(sm.getDataSource())
				: eventDefintionCRFDAO;
		return eventDefintionCRFDAO;
	}

	public DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
		discrepancyNoteDAO = this.discrepancyNoteDAO == null ? new DiscrepancyNoteDAO(sm.getDataSource())
				: discrepancyNoteDAO;
		return discrepancyNoteDAO;
	}

	public StudyGroupDAO getStudyGroupDAO() {
		studyGroupDAO = this.studyGroupDAO == null ? new StudyGroupDAO(sm.getDataSource()) : studyGroupDAO;
		return studyGroupDAO;
	}

	@Override
	protected String getUrlKey() {
		return SAVED_SUBJECT_MATRIX_URL;
	}

	@Override
	protected String getDefaultUrl() {
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
	protected boolean userDoesNotUseJmesaTableForNavigation() {
		return request.getQueryString() == null || !request.getQueryString().contains("&findSubjects_p_=");
	}
}
