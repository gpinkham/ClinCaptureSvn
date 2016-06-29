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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.service.crfdata.HideCRFManager;
import org.akaza.openclinica.util.CrfComparator;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.clinovo.service.CRFMaskingService;
import com.clinovo.util.SubjectEventStatusUtil;

/**
 * @author ssachs
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class EnterDataForStudyEventServlet extends SpringServlet {

	public static final String TRUE = "true";
	public static final String INPUT_EVENT_ID = "eventId";
	public static final String FULL_CRF_LIST = "fullCrfList";
	public static final String OPEN_FIRST_CRF = "openFirstCrf";
	public static final String BEAN_STUDY_EVENT = "studyEvent";
	public static final String BEAN_STUDY_SUBJECT = "studySubject";
	public static final String BEAN_DISPLAY_EVENT_CRFS = "displayEventCRFs";
	public static final String HIDE_SCHEDULE_EVENT_BUTTON = "hideScheduleEventButton";
	public static final String BEAN_UNCOMPLETED_EVENTDEFINITIONCRFS = "uncompletedEventDefinitionCRFs";

	private StudyEventBean getStudyEvent(HttpServletRequest request, int eventId) throws Exception {

		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		StudyEventDAO sedao = getStudyEventDAO();

		StudyBean studyWithSED = currentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			studyWithSED = new StudyBean();
			studyWithSED.setId(currentStudy.getParentStudyId());
		}

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
		StudyUserRoleBean currentRole = getCurrentRole(request);
		CRFMaskingService maskingService = getMaskingService();

		// ClinCapture custom attributes
		populateCustomElementsConfig(request);

		request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, new FormDiscrepancyNotes());

		removeLockedCRF(ub.getId());
		FormProcessor fp = new FormProcessor(request);

		int eventId = fp.getInt(INPUT_EVENT_ID, true);
		request.setAttribute("eventId", eventId + "");

		// so we can display the event for which we're entering data
		StudyEventBean seb = getStudyEvent(request, eventId);

		// so we can display the subject's label
		StudyDAO studydao = new StudyDAO(getDataSource());
		CRFVersionDAO crfvdao = new CRFVersionDAO(getDataSource());
		StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
		StudySubjectBean studySubjectBean = (StudySubjectBean) ssdao.findByPK(seb.getStudySubjectId());
		StudyBean study = (StudyBean) studydao.findByPK(studySubjectBean.getStudyId());

		ArrayList eventDefinitions = selectNotStartedOrRepeatingSortedEventDefs(studySubjectBean,
				study.getParentStudyId() > 0 ? study.getParentStudyId() : study.getId(), getStudyEventDefinitionDAO(),
				getStudyGroupClassDAO(), getStudyEventDAO());
		request.setAttribute(HIDE_SCHEDULE_EVENT_BUTTON, eventDefinitions.size() == 0);

		SessionManager sm = getSessionManager(request);
		List<DiscrepancyNoteBean> allNotesforSubjectAndEvent = DiscrepancyNoteUtil
				.getAllNotesforSubjectAndEvent(studySubjectBean, currentStudy, sm);
		setRequestAttributesForNotes(allNotesforSubjectAndEvent, seb, sm, request);

		// prepare to figure out what the display should look like
		EventCRFDAO ecdao = new EventCRFDAO(getDataSource());
		ArrayList<EventCRFBean> eventCRFs = ecdao.findAllByStudyEvent(seb);
		SubjectEventStatusUtil.fillDoubleDataOwner(eventCRFs, sm);

		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(getDataSource());
		ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(study,
				seb.getStudyEventDefinitionId());

		// get the event definition CRFs for which no event CRF exists
		// the event definition CRFs must be populated with versions so we can
		// let the user choose which version he will enter data for
		// However, this method seems to be returning DisplayEventDefinitionCRFs
		// that contain valid eventCRFs??
		ArrayList uncompletedEventDefinitionCRFs = getUncompletedCRFs(eventDefinitionCRFs, eventCRFs, seb.getSubjectEventStatus());
		populateUncompletedCRFsWithCRFAndVersions(uncompletedEventDefinitionCRFs);

		// Attempt to provide the DisplayEventDefinitionCRF with a
		// valid owner
		// only if its container eventCRf has a valid id
		populateUncompletedCRFsWithAnOwner(getDataSource(), uncompletedEventDefinitionCRFs);

		// for the event definition CRFs for which event CRFs exist, get
		// DisplayEventCRFBeans, which the JSP will use to determine what
		// the user will see for each event CRF

		// removing the below row in exchange for the ViewStudySubjectServlet
		// version, for two
		// reasons:
		// 1. concentrate all business logic in one place
		// 2. VSSS seems to handle the javascript creation correctly
		// ArrayList displayEventCRFs = getDisplayEventCRFs(eventCRFs,
		// eventDefinitionCRFs, seb.getSubjectEventStatus());

		ArrayList displayEventCRFs = getDisplayEventCRFs(eventCRFs, ub, currentRole, seb.getSubjectEventStatus(),
				study);

		if (currentStudy.getParentStudyId() > 0) {
			HideCRFManager hideCRFManager = HideCRFManager.createHideCRFManager();
			uncompletedEventDefinitionCRFs = hideCRFManager
					.removeHiddenEventDefinitionCRFBeans(uncompletedEventDefinitionCRFs);
			displayEventCRFs = hideCRFManager.removeHiddenEventCRFBeans(displayEventCRFs);
		}

		// Remove all masked CRFs from the list

		request.setAttribute(BEAN_STUDY_EVENT, seb);
		request.setAttribute(BEAN_STUDY_SUBJECT, studySubjectBean);
		request.setAttribute(BEAN_UNCOMPLETED_EVENTDEFINITIONCRFS, uncompletedEventDefinitionCRFs);
		request.setAttribute(BEAN_DISPLAY_EVENT_CRFS, displayEventCRFs);

		List<Object> fullCrfList = new ArrayList<Object>();
		fullCrfList.addAll(uncompletedEventDefinitionCRFs);
		fullCrfList.addAll(displayEventCRFs);
		Collections.sort(fullCrfList, new CrfComparator());
		request.setAttribute(FULL_CRF_LIST, fullCrfList);

		prepareCRFVersionForLockedCRFs(fullCrfList, crfvdao, logger);

		// this is for generating side info panel
		ArrayList beans = getDisplayStudyEventsForStudySubject(studySubjectBean, ub, currentRole, false);

		request.setAttribute("beans", beans);
		EventCRFBean ecb = new EventCRFBean();
		ecb.setStudyEventId(eventId);
		request.setAttribute("eventCRF", ecb);
		// Make available the study
		request.setAttribute("study", currentStudy);
		if (currentStudy.getParentStudyId() > 0) {
			StudyBean parentStudyBean = (StudyBean) studydao.findByPK(currentStudy.getParentStudyId());
			request.setAttribute("parentStudyOid", parentStudyBean.getOid());
		} else {
			request.setAttribute("parentStudyOid", currentStudy.getOid());
		}

		if (fp.getString(OPEN_FIRST_CRF).equalsIgnoreCase(TRUE)) {
			try {
				// Remove all masked CRFs from the list
				List<Object> fullListWithoutMasked = maskingService
						.removeMaskedDisplayEventDefinitionAndEventCRFBeans(fullCrfList, ub);
				if (fullListWithoutMasked.size() == 0) {
					addPageMessage(getResWord().getString("no_crf_available"), request);
					forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
					return;
				}
				DisplayEventDefinitionCRFBean dedcb = (DisplayEventDefinitionCRFBean) fullListWithoutMasked.get(0);
				CRFVersionBean defaultCRFVerBean = new CRFVersionBean();
				for (int i = 0; i < dedcb.getEdc().getVersions().size(); i++) {
					defaultCRFVerBean = (CRFVersionBean) dedcb.getEdc().getVersions().get(i);
					if (defaultCRFVerBean.getId() == dedcb.getEdc().getDefaultVersionId()) {
						break;
					}
				}

				response.sendRedirect(request.getContextPath() + Page.INITIAL_DATA_ENTRY_SERVLET.getFileName()
						+ "?studyEventId=" + ecb.getStudyEventId() + "&eventCRFId=0&subjectId="
						+ studySubjectBean.getSubjectId() + "&eventDefinitionCRFId=" + dedcb.getEdc().getId()
						+ "&crfVersionId=" + defaultCRFVerBean.getId() + "&action=ide_s&exitTo="
						+ URLEncoder.encode(Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET.getFileName().replace("/", "")
								+ "?eventId=" + ecb.getStudyEventId(), "UTF-8"));
				return;
			} catch (Exception e) {
				logger.error("An error has occured during processing the IDE for first crf in the study event.", e);
			}
		}

		forwardPage(Page.ENTER_DATA_FOR_STUDY_EVENT, request, response);
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

	/**
	 * populateUncompletedCRFsWithAnOwner.
	 *
	 * @param ds
	 *            DataSource
	 * @param displayEventDefinitionCRFBeans
	 *            List<DisplayEventDefinitionCRFBean>
	 */
	public static void populateUncompletedCRFsWithAnOwner(DataSource ds,
			List<DisplayEventDefinitionCRFBean> displayEventDefinitionCRFBeans) {
		if (displayEventDefinitionCRFBeans == null || displayEventDefinitionCRFBeans.isEmpty()) {
			return;
		}
		UserAccountDAO userAccountDAO = new UserAccountDAO(ds);
		UserAccountBean userAccountBean;
		EventCRFBean eventCRFBean;
		for (DisplayEventDefinitionCRFBean dedcBean : displayEventDefinitionCRFBeans) {

			eventCRFBean = dedcBean.getEventCRF();
			if (eventCRFBean != null && eventCRFBean.getOwner() == null && eventCRFBean.getOwnerId() > 0) {
				userAccountBean = (UserAccountBean) userAccountDAO.findByPK(eventCRFBean.getOwnerId());

				eventCRFBean.setOwner(userAccountBean);
			}

			// Failing the above, obtain the owner from the
			// EventDefinitionCRFBean
			if (eventCRFBean != null && eventCRFBean.getOwner() == null) {
				int ownerId = dedcBean.getEdc().getOwnerId();
				if (ownerId > 0) {
					userAccountBean = (UserAccountBean) userAccountDAO.findByPK(ownerId);

					eventCRFBean.setOwner(userAccountBean);
				}
			}
		}
	}

	/**
	 * If DiscrepancyNoteBeans have a certain column value, then set flags that a JSP will check in the request
	 * attribute. This is a convenience method called by the processRequest() method.
	 *
	 * @param discBeans
	 *            List<DiscrepancyNoteBean>
	 * @param seb
	 *            StudyEventBean
	 * @param sm
	 *            SessionManager
	 * @param request
	 *            HttpServletRequest
	 */
	public static void setRequestAttributesForNotes(List<DiscrepancyNoteBean> discBeans, StudyEventBean seb,
			SessionManager sm, HttpServletRequest request) {
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
		StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) seddao.findByPK(seb.getStudyEventDefinitionId());
		List<DiscrepancyNoteBean> locationDNotes = new ArrayList<DiscrepancyNoteBean>();
		List<DiscrepancyNoteBean> dateStartDNotes = new ArrayList<DiscrepancyNoteBean>();
		List<DiscrepancyNoteBean> dateEndDNotes = new ArrayList<DiscrepancyNoteBean>();
		for (DiscrepancyNoteBean discrepancyNoteBean : discBeans) {
			// method discrepancyNoteBean.getEvent.getId() return 0 for all DNs
			if (discrepancyNoteBean.getEventName().equalsIgnoreCase(sedBean.getName())
					&& discrepancyNoteBean.getEntityId() == seb.getId()) {
				if ("location".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
					locationDNotes.add(discrepancyNoteBean);
				} else if ("date_start".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
					dateStartDNotes.add(discrepancyNoteBean);
				} else if ("date_end".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
					dateEndDNotes.add(discrepancyNoteBean);
				}
			}
		}
		request.setAttribute("numberOfLocationDNotes", locationDNotes.size());
		request.setAttribute("numberOfDateStartDNotes", dateStartDNotes.size());
		request.setAttribute("numberOfDateEndDNotes", dateEndDNotes.size());

		request.setAttribute("imageFileNameForLocation",
				DiscrepancyNoteUtil.getImageFileNameForFlagByResolutionStatusId(
						DiscrepancyNoteUtil.getDiscrepancyNoteResolutionStatus(locationDNotes)));
		request.setAttribute("imageFileNameForDateStart",
				DiscrepancyNoteUtil.getImageFileNameForFlagByResolutionStatusId(
						DiscrepancyNoteUtil.getDiscrepancyNoteResolutionStatus(dateStartDNotes)));
		request.setAttribute("imageFileNameForDateEnd", DiscrepancyNoteUtil.getImageFileNameForFlagByResolutionStatusId(
				DiscrepancyNoteUtil.getDiscrepancyNoteResolutionStatus(dateEndDNotes)));
	}
}
