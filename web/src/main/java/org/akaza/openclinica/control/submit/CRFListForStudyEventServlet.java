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
package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.clinovo.service.CRFMaskingService;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SDVUtil;
import com.clinovo.util.SignUtil;
import com.clinovo.util.SubjectEventStatusUtil;

/**
 * copy of the EnterDataForStudyEventServlet.
 * 
 * @author ssachs
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class CRFListForStudyEventServlet extends SpringServlet {

	public static final String SUBJECT_FLAG_COLOR = "subjectFlagColor";
	public static final String STUDY_ID = "studyId";
	public static final String SES_ICON_URL = "sesIconUrl";
	public static final String INPUT_EVENT_ID = "studyEventId";
	// The study event has an existing discrepancy note related to its location
	// property; this
	// value will be saved as a request attribute
	public static final String HAS_LOCATION_NOTE = "hasLocationNote";
	// The study event has an existing discrepancy note related to its start
	// date property; this
	// value will be saved as a request attribute
	public static final String HAS_START_DATE_NOTE = "hasStartDateNote";
	// The study event has an existing discrepancy note related to its end date
	// property; this
	// value will be saved as a request attribute
	public static final String HAS_END_DATE_NOTE = "hasEndDateNote";
	public static final String SHOW_SIGN_BUTTON = "showSignButton";
	public static final String SHOW_SUBJECT_SIGN_BUTTON = "showSubjectSignButton";
	public static final String SHOW_SDV_BUTTON = "showSDVButton";
	public static final String EVENT_FLAG_COLOR = "eventFlagColor";
	public static final String STUDY_EVENT_NAME = "studyEventName";
	public static final String EVENT_CRF_ID_PARAMETER = "eventCRFId";
	public static final String EVENT_DEFINITION_CRF_ID_PARAMETER = "eventDefintionCRFId";
	public static final String PAGE_PARAMETER = "page";
	public static final String PAGE_TO_RENDER = "pageToRender";
	public static final String STUDY_SUBJECT_ID = "studySubjectId";
	public static final String STUDY_EVENT_DEFINITION = "studyEventDefinition";

	@Autowired
	private CRFMaskingService maskingService;

	private StudyEventBean getStudyEvent(HttpServletRequest request, int eventId) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		FormProcessor fp = new FormProcessor(request);

		StudyEventDAO sedao = getStudyEventDAO();

		if (eventId == 0) {
			int studySubjectId = fp.getInt(STUDY_SUBJECT_ID);
			int studyEventDefinitionId = fp.getInt(STUDY_EVENT_DEFINITION);
			List<StudyEventBean> studyEventBeanList = sedao
					.findAllByDefinitionAndSubjectOrderByOrdinal(studyEventDefinitionId, studySubjectId);
			if (studyEventBeanList.size() == 0) {
				StudyEventBean studyEventBean = new StudyEventBean();
				studyEventBean.setOwner(ub);
				studyEventBean.setSampleOrdinal(1);
				studyEventBean.setDateStarted(new Date());
				studyEventBean.setCreatedDate(new Date());
				studyEventBean.setStatus(Status.AVAILABLE);
				studyEventBean.setStudySubjectId(studySubjectId);
				studyEventBean.setStudyEventDefinitionId(studyEventDefinitionId);
				studyEventBean.setSubjectEventStatus(SubjectEventStatus.NOT_SCHEDULED);
				eventId = sedao.create(studyEventBean).getId();
			} else {
				eventId = studyEventBeanList.get(0).getId();
			}
		}

		StudyBean studyWithSED = currentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			studyWithSED = new StudyBean();
			studyWithSED.setId(currentStudy.getParentStudyId());
		}

		request.setAttribute("currentStudy", currentStudy);

		AuditableEntityBean aeb = sedao.findByPKAndStudy(eventId, studyWithSED);

		if (!aeb.isActive()) {
			addPageMessage(getResPage().getString("study_event_to_enter_data_not_belong_study"), request);
			throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					getResException().getString("study_event_not_belong_study"), "1");
		}

		StudyEventBean seb = (StudyEventBean) aeb;

		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(seb.getStudyEventDefinitionId());
		seb.setStudyEventDefinition(sedb);
		if (!(currentRole.isSysAdmin() || currentRole.isStudyAdministrator())
				&& seb.getSubjectEventStatus().isLocked()) {
			seb.setEditable(false);
		}
		return seb;
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		removeLockedCRF(ub.getId());
		FormProcessor fp = new FormProcessor(request);

		StudyEventDAO sedao = getStudyEventDAO();
		StudySubjectDAO ssdao = getStudySubjectDAO();

		int eventId = fp.getInt(INPUT_EVENT_ID, true);
		StudyEventBean seb = getStudyEvent(request, eventId);
		eventId = seb.getId();
		request.setAttribute("eventId", eventId + "");

		StudySubjectBean studySubjectBean = ssdao.findByPK(seb.getStudySubjectId());
		int studyId = studySubjectBean.getStudyId();

		StudyDAO studydao = getStudyDAO();
		StudyBean study = (StudyBean) studydao.findByPK(studyId);
		request.setAttribute("viewModeOnly",
				study.getStatus().isDeleted() || study.getStatus().isLocked()
						|| studySubjectBean.getStatus().isDeleted() || studySubjectBean.getStatus().isLocked()
						|| ub.getRoleByStudy(study.isSite() ? study.getParentStudyId() : studyId).isStudySponsor());

		boolean subjectStudyIsCurrentStudy = studyId == currentStudy.getId();
		boolean isParentStudy = study.getParentStudyId() < 1;

		// Get any disc notes for this study event
		DiscrepancyNoteDAO discrepancyNoteDAO = getDiscrepancyNoteDAO();
		ArrayList<DiscrepancyNoteBean> allNotesforSubjectAndEvent;

		// determines page name, popup content should be customized for
		String pageTitle = fp.getString(PAGE_PARAMETER);

		// if SM is displayed by event CRFs for a particular event definition,
		// we need to know eventCRFId or eventDefintionCRFId (in case if a proper EventCRFBean does not exist in DB)
		// to display the pop-up for a single CRF
		int eventCRFId = 0;
		int eventDefintionCRFId = 0;
		if (pageTitle.equalsIgnoreCase(Page.LIST_EVENTS_FOR_SUBJECTS_SERVLET.getFileName())) {
			eventCRFId = fp.getInt(EVENT_CRF_ID_PARAMETER);
			eventDefintionCRFId = fp.getInt(EVENT_DEFINITION_CRF_ID_PARAMETER);
		}

		// These methods return only parent disc notes
		if (subjectStudyIsCurrentStudy && isParentStudy) {
			allNotesforSubjectAndEvent = discrepancyNoteDAO.findAllStudyEventByStudyAndId(currentStudy,
					studySubjectBean.getId());
		} else { // findAllStudyEventByStudiesAndSubjectId
			if (!isParentStudy) {
				StudyBean stParent = (StudyBean) studydao.findByPK(study.getParentStudyId());
				allNotesforSubjectAndEvent = discrepancyNoteDAO.findAllStudyEventByStudiesAndSubjectId(stParent, study,
						studySubjectBean.getId());
			} else {
				allNotesforSubjectAndEvent = discrepancyNoteDAO.findAllStudyEventByStudiesAndSubjectId(currentStudy,
						study, studySubjectBean.getId());
			}
		}

		if (!allNotesforSubjectAndEvent.isEmpty()) {
			setRequestAttributesForNotes(request, allNotesforSubjectAndEvent);
		}

		SessionManager sm = getSessionManager(request);
		EventCRFDAO ecdao = new EventCRFDAO(getDataSource());
		ArrayList<EventCRFBean> eventCRFs = new ArrayList<EventCRFBean>();
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(getDataSource());
		ArrayList eventDefinitionCRFs = new ArrayList();

		if (pageTitle.equalsIgnoreCase(Page.LIST_EVENTS_FOR_SUBJECTS_SERVLET.getFileName())) {

			// if SM displays subjects list for single selected event definition from study
			if (eventDefintionCRFId > 0) {
				// and user wants to see the pop-up for a specific CRF (using event CRF status icon)
				if (eventCRFId > 0) {
					eventCRFs.add((EventCRFBean) ecdao.findByPK(eventCRFId));
				}
				eventDefinitionCRFs.add(edcdao.findByPK(eventDefintionCRFId));
			} else {
				// and user wants to see the pop-up for whole study event (using study event status icon)
				eventCRFs = ecdao.findAllByStudyEvent(seb);
				eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(study,
						seb.getStudyEventDefinitionId());
			}

		} else {

			// if SM displays subjects list for all of the event definitions in study
			eventCRFs = ecdao.findAllByStudyEvent(seb);
			eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(study,
					seb.getStudyEventDefinitionId());

		}

		if (discrepancyNoteDAO.doesSubjectHaveUnclosedDNsInStudy(currentStudy, studySubjectBean.getLabel(), ub)) {
			String subjectFlagColor = "yellow";
			if (discrepancyNoteDAO.doesSubjectHaveNewDNsInStudy(currentStudy, studySubjectBean.getLabel(), ub)) {
				subjectFlagColor = "red";
			}
			request.setAttribute(SUBJECT_FLAG_COLOR, subjectFlagColor);
		}

		String eventName = seb.getStudyEventDefinition().getName();
		if (discrepancyNoteDAO.doesEventHaveUnclosedDNsInStudy(currentStudy, eventName, eventId,
				studySubjectBean.getLabel(), ub)) {
			String eventFlagColor = "yellow";
			if (discrepancyNoteDAO.doesEventHaveNewDNsInStudy(currentStudy, eventName, eventId,
					studySubjectBean.getLabel(), ub)) {
				eventFlagColor = "red";
			}
			request.setAttribute(EVENT_FLAG_COLOR, eventFlagColor);
		}

		request.setAttribute(STUDY_ID, currentStudy.getId());
		request.setAttribute(STUDY_EVENT_NAME, seb.getStudyEventDefinition().getName());
		request.setAttribute(SES_ICON_URL, SubjectEventStatusUtil.getSESIconUrl(seb.getSubjectEventStatus()));

		List<Object> fullCrfList = prepareFullCrfList(study, studySubjectBean, seb, eventCRFs, eventDefinitionCRFs);

		Map<Integer, String> notedMap = prepareNodeMapForFullCrfList(fullCrfList, studySubjectBean, eventName, eventId);

		request.setAttribute("crfNDsMap", notedMap);

		request.setAttribute("eventName",
				seb.getStudyEventDefinition() != null ? seb.getStudyEventDefinition().getDescription() : "");

		// this is for generating side info panel
		request.setAttribute("beans", new ArrayList());
		EventCRFBean ecb = new EventCRFBean();
		ecb.setStudyEventId(eventId);
		request.setAttribute("eventCRF", ecb);
		// Make available the study
		request.setAttribute("study", currentStudy);

		DAOWrapper daoWrapper = new DAOWrapper(getDataSource());
		request.setAttribute(SHOW_SIGN_BUTTON, SignUtil.permitSign(seb, study, daoWrapper));
		request.setAttribute(SHOW_SUBJECT_SIGN_BUTTON, SignUtil.permitSign(studySubjectBean, daoWrapper));
		request.setAttribute(SHOW_SDV_BUTTON,
				SDVUtil.permitSDV(seb, studySubjectBean.getStudyId(), daoWrapper,
						currentStudy.getStudyParameterConfig().getAllowSdvWithOpenQueries().equals("yes"), notedMap,
						ub.getId(), maskingService));
		request.setAttribute(PAGE_TO_RENDER, Page.CRF_LIST_FOR_STUDY_EVENT);

		boolean allLocked = true;
		List<StudyEventBean> studyEventBeanList = sedao.findAllByStudySubject(studySubjectBean);
		for (StudyEventBean studyEventBean : studyEventBeanList) {
			if (studyEventBean.getSubjectEventStatus() != SubjectEventStatus.LOCKED) {
				allLocked = false;
				break;
			}
		}
		request.setAttribute("allLocked", allLocked);

		forwardPage(Page.CRF_LIST_FOR_STUDY_EVENT, request, response);
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		String exceptionName = getResException().getString("no_permission_to_submit_data");
		String noAccessMessage = getResPage().getString("may_not_enter_data_for_this_study");

		if (mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(noAccessMessage, request);
		throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET, exceptionName, "1");
	}

	private void setRequestAttributesForNotes(HttpServletRequest request, List<DiscrepancyNoteBean> discBeans) {
		for (DiscrepancyNoteBean discrepancyNoteBean : discBeans) {
			if ("location".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
				request.setAttribute(HAS_LOCATION_NOTE, "yes");
			} else if ("date_start".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
				request.setAttribute(HAS_START_DATE_NOTE, "yes");

			} else if ("date_end".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
				request.setAttribute(HAS_END_DATE_NOTE, "yes");
			}
		}
	}
}
