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

import org.akaza.openclinica.bean.core.*;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.managestudy.ViewStudySubjectServlet;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.managestudy.*;
import org.akaza.openclinica.util.*;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.service.crfdata.HideCRFManager;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * copy of the EnterDataForStudyEventServlet
 * 
 * @author ssachs
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
@Component
public class CRFListForStudyEventServlet extends Controller {

	public static final String SUBJECT_FLAG_COLOR = "subjectFlagColor";

	public static final String CURRENT_ROLE = "currentRole";

	public static final String STUDY_ID = "studyId";

	public static final String SES_ICON_URL = "sesIconUrl";

	public static final String INPUT_EVENT_ID = "eventId";

	public static final String BEAN_STUDY_EVENT = "studyEvent";

	public static final String BEAN_STUDY_SUBJECT = "studySubject";

	public static final String BEAN_UNCOMPLETED_EVENTDEFINITIONCRFS = "uncompletedEventDefinitionCRFs";

	public static final String FULL_CRF_LIST = "fullCrfList";

	public static final String BEAN_DISPLAY_EVENT_CRFS = "displayEventCRFs";
	// The study event has an existing discrepancy note related to its location
	// property; this
	// value will be saved as a request attribute
	public final static String HAS_LOCATION_NOTE = "hasLocationNote";
	// The study event has an existing discrepancy note related to its start
	// date property; this
	// value will be saved as a request attribute
	public final static String HAS_START_DATE_NOTE = "hasStartDateNote";
	// The study event has an existing discrepancy note related to its end date
	// property; this
	// value will be saved as a request attribute
	public final static String HAS_END_DATE_NOTE = "hasEndDateNote";

	public static final String SHOW_SIGN_BUTTON = "showSignButton";

	public static final String SHOW_SUBJECT_SIGN_BUTTON = "showSubjectSignButton";

	public static final String SHOW_SDV_BUTTON = "showSDVButton";

	public static final String SHOW_SUBJECT_SDV_BUTTON = "showSubjectSDVButton";
	public static final String EVENT_FLAG_COLOR = "eventFlagColor";
	public static final String STUDY_EVENT_NAME = "studyEventName";

	private StudyEventBean getStudyEvent(HttpServletRequest request, int eventId) throws Exception {
        StudyBean currentStudy = getCurrentStudy(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		StudyEventDAO sedao = getStudyEventDAO();

		StudyBean studyWithSED = currentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			studyWithSED = new StudyBean();
			studyWithSED.setId(currentStudy.getParentStudyId());
		}

        request.setAttribute("currentStudy", currentStudy);

		AuditableEntityBean aeb = sedao.findByPKAndStudy(eventId, studyWithSED);

		if (!aeb.isActive()) {
			addPageMessage(respage.getString("study_event_to_enter_data_not_belong_study"), request);
			throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("study_event_not_belong_study"), "1");
		}

		StudyEventBean seb = (StudyEventBean) aeb;

		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(seb.getStudyEventDefinitionId());
		seb.setStudyEventDefinition(sedb);
		if (!(currentRole.isStudyDirector() || currentRole.isStudyAdministrator()) && seb.getSubjectEventStatus().isLocked()) {
			seb.setEditable(false);
		}
		return seb;
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);
        StudyBean currentStudy = getCurrentStudy(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		removeLockedCRF(ub.getId());
		FormProcessor fp = new FormProcessor(request);

		int eventId = fp.getInt(INPUT_EVENT_ID, true);
		request.setAttribute("eventId", eventId + "");

		// so we can display the event for which we're entering data
		StudyEventBean seb = getStudyEvent(request, eventId);

		StudyDAO sdao = getStudyDAO();
		StudyEventDAO sedao = getStudyEventDAO();
		DiscrepancyNoteDAO discDao = getDiscrepancyNoteDAO();
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();

		// so we can display the subject's label
		StudySubjectDAO ssdao = getStudySubjectDAO();
		StudySubjectBean studySubjectBean = (StudySubjectBean) ssdao.findByPK(seb.getStudySubjectId());
		int studyId = studySubjectBean.getStudyId();

		StudyDAO studydao = getStudyDAO();
		StudyBean study = (StudyBean) studydao.findByPK(studyId);

		boolean subjectStudyIsCurrentStudy = studyId == currentStudy.getId();
		boolean isParentStudy = study.getParentStudyId() < 1;

		// Get any disc notes for this study event
		DiscrepancyNoteDAO discrepancyNoteDAO = getDiscrepancyNoteDAO();
		ArrayList<DiscrepancyNoteBean> allNotesforSubjectAndEvent = new ArrayList<DiscrepancyNoteBean>();

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
		ArrayList<EventCRFBean> eventCRFs = ecdao.findAllByStudyEvent(seb);
		SubjectEventStatusUtil.fillDoubleDataOwner(eventCRFs, sm);
		
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(getDataSource());
		ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(study,
				seb.getStudyEventDefinitionId());

		ArrayList uncompletedEventDefinitionCRFs = getUncompletedCRFs(eventDefinitionCRFs, eventCRFs);
		EnterDataForStudyEventServlet.populateUncompletedCRFsWithCRFAndVersions(sm.getDataSource(), logger,
				uncompletedEventDefinitionCRFs);

		EnterDataForStudyEventServlet.populateUncompletedCRFsWithAnOwner(sm.getDataSource(),
				uncompletedEventDefinitionCRFs);

		ArrayList displayEventCRFs = ViewStudySubjectServlet.getDisplayEventCRFs(getDataSource(), eventCRFs,
				eventDefinitionCRFs, ub, currentRole, seb.getSubjectEventStatus(), study);

		if (currentStudy.getParentStudyId() > 0) {
			HideCRFManager hideCRFManager = HideCRFManager.createHideCRFManager();

			uncompletedEventDefinitionCRFs = hideCRFManager
					.removeHiddenEventDefinitionCRFBeans(uncompletedEventDefinitionCRFs);

			displayEventCRFs = hideCRFManager.removeHiddenEventCRFBeans(displayEventCRFs);
		}
		
		request.setAttribute(STUDY_ID, currentStudy.getId());
		request.setAttribute(BEAN_STUDY_EVENT, seb);
		request.setAttribute(STUDY_EVENT_NAME, seb.getStudyEventDefinition().getName());
		request.setAttribute(BEAN_STUDY_SUBJECT, studySubjectBean);
		request.setAttribute(BEAN_UNCOMPLETED_EVENTDEFINITIONCRFS, uncompletedEventDefinitionCRFs);
		request.setAttribute(BEAN_DISPLAY_EVENT_CRFS, displayEventCRFs);
		request.setAttribute(SES_ICON_URL, SubjectEventStatusUtil.getSESIconUrl(seb.getSubjectEventStatus()));
		request.setAttribute(CURRENT_ROLE, currentRole);
		if (discrepancyNoteDAO.doesSubjectHasUnclosedNDsInStudy(currentStudy, studySubjectBean.getLabel())){
			String subjectFlagColor = "yellow";
			if (discrepancyNoteDAO.doesSubjectHasNewNDsInStudy(currentStudy, studySubjectBean.getLabel())){
				subjectFlagColor = "red";
			}
			request.setAttribute(SUBJECT_FLAG_COLOR, subjectFlagColor);
		}
		
		String eventName = seb.getStudyEventDefinition().getName();
		if (discrepancyNoteDAO.doesEventHasUnclosedNDsInStudy(currentStudy, eventName, eventId, studySubjectBean.getLabel())){
			String eventFlagColor = "yellow";
			if (discrepancyNoteDAO.doesEventHasNewNDsInStudy(currentStudy, eventName, eventId, studySubjectBean.getLabel())){
				eventFlagColor = "red";
			}
			request.setAttribute(EVENT_FLAG_COLOR, eventFlagColor);
		}

		List<Object> fullCrfList = new ArrayList<Object>();
		fullCrfList.addAll(uncompletedEventDefinitionCRFs);
		fullCrfList.addAll(displayEventCRFs);
		Collections.sort(fullCrfList, new CrfComparator());
		request.setAttribute(FULL_CRF_LIST, fullCrfList);

		Map<Integer, String> notedMap = new HashMap<Integer, String>();

		for (Object bean : fullCrfList) {
			if (bean instanceof DisplayEventCRFBean) {
				DisplayEventCRFBean displayEventCRFBean = (DisplayEventCRFBean) bean;

				String crfName = displayEventCRFBean.getEventCRF().getCrf().getName();
				Integer crfId = displayEventCRFBean.getEventCRF().getCrf().getId();

				if (discrepancyNoteDAO.doesCRFHasUnclosedNDsInStudyForSubject(currentStudy, eventName, eventId,
						studySubjectBean.getLabel(), crfName)) {
					String crfFlagColor = "yellow";
					if (discrepancyNoteDAO.doesCRFHasNewNDsInStudyForSubject(currentStudy, eventName, eventId,
							studySubjectBean.getLabel(), crfName)) {
						crfFlagColor = "red";
					}
					notedMap.put(crfId, crfFlagColor);
				}

			} else if (bean instanceof DisplayEventDefinitionCRFBean) {
				DisplayEventDefinitionCRFBean displayEventDefinitionCRFBean = (DisplayEventDefinitionCRFBean) bean;

				String crfName = displayEventDefinitionCRFBean.getEdc().getCrf().getName();
				Integer crfId = displayEventDefinitionCRFBean.getEdc().getCrf().getId();

				if (discrepancyNoteDAO.doesCRFHasUnclosedNDsInStudyForSubject(currentStudy, eventName, eventId,
						studySubjectBean.getLabel(), crfName)) {
					String crfFlagColor = "yellow";
					if (discrepancyNoteDAO.doesCRFHasNewNDsInStudyForSubject(currentStudy, eventName, eventId,
							studySubjectBean.getLabel(), crfName)) {
						crfFlagColor = "red";
					}
					notedMap.put(crfId, crfFlagColor);
				}
			}
		}

		request.setAttribute("crfNDsMap", notedMap);

		request.setAttribute("eventName", seb != null && seb.getStudyEventDefinition() != null ? seb
				.getStudyEventDefinition().getDescription() : "");

		// this is for generating side info panel
		request.setAttribute("beans", new ArrayList());
		EventCRFBean ecb = new EventCRFBean();
		ecb.setStudyEventId(eventId);
		request.setAttribute("eventCRF", ecb);
		// Make available the study
		request.setAttribute("study", currentStudy);

		DAOWrapper daoWrapper = new DAOWrapper(sdao, sedao, ssdao, ecdao, edcdao, seddao, discDao);
		request.setAttribute(SHOW_SIGN_BUTTON, SignUtil.permitSign(seb, study, daoWrapper));
		request.setAttribute(SHOW_SUBJECT_SIGN_BUTTON, SignUtil.permitSign(studySubjectBean, daoWrapper));
		request.setAttribute(SHOW_SDV_BUTTON, SDVUtil.permitSDV(seb, studySubjectBean.getStudyId(), daoWrapper));
		request.setAttribute(SHOW_SUBJECT_SDV_BUTTON, SDVUtil.permitSDV(studySubjectBean, daoWrapper));

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
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		String exceptionName = resexception.getString("no_permission_to_submit_data");
		String noAccessMessage = respage.getString("may_not_enter_data_for_this_study");

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(noAccessMessage, request);
		throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET, exceptionName, "1");
	}

	private ArrayList getUncompletedCRFs(ArrayList eventDefinitionCRFs, ArrayList eventCRFs) {
		int i;
		HashMap completed = new HashMap();
		HashMap startedButIncompleted = new HashMap();
		ArrayList answer = new ArrayList();

		for (i = 0; i < eventDefinitionCRFs.size(); i++) {
			EventDefinitionCRFBean edcrf = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
			completed.put(new Integer(edcrf.getCrfId()), Boolean.FALSE);
			startedButIncompleted.put(new Integer(edcrf.getCrfId()), new EventCRFBean());
		}

		CRFVersionDAO cvdao = getCRFVersionDAO();
		ItemDataDAO iddao = getItemDataDAO();
		for (i = 0; i < eventCRFs.size(); i++) {
			EventCRFBean ecrf = (EventCRFBean) eventCRFs.get(i);
			int crfId = cvdao.getCRFIdFromCRFVersionId(ecrf.getCRFVersionId());
			ArrayList idata = iddao.findAllByEventCRFId(ecrf.getId());
			if (!idata.isEmpty()) {// this crf has data already
				completed.put(new Integer(crfId), Boolean.TRUE);
			} else {// event crf got created, but no data entered
				startedButIncompleted.put(new Integer(crfId), ecrf);
			}
		}

		for (i = 0; i < eventDefinitionCRFs.size(); i++) {
			DisplayEventDefinitionCRFBean dedc = new DisplayEventDefinitionCRFBean();
			EventDefinitionCRFBean edcrf = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
			dedc.setEdc(edcrf);
			Boolean b = (Boolean) completed.get(new Integer(edcrf.getCrfId()));
			EventCRFBean ev = (EventCRFBean) startedButIncompleted.get(new Integer(edcrf.getCrfId()));
			if (b == null || !b.booleanValue()) {
				dedc.setEventCRF(ev);
				answer.add(dedc);
			}
		}

		return answer;
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
