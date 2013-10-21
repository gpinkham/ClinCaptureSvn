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

import com.clinovo.util.ValidatorHelper;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.RememberLastPage;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.*;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.service.calendar.CalendarLogic;
import org.akaza.openclinica.util.DAOWrapper;
import org.akaza.openclinica.util.SignUtil;
import org.akaza.openclinica.util.SubjectEventStatusUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.quartz.impl.StdScheduler;

/**
 * @author jxu
 * 
 *         Performs updating study event action
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class UpdateStudyEventServlet extends SecureController {

	private static final long serialVersionUID = -6029524999558420563L;

	public static final String EVENT_ID = "event_id";

	public static final String STUDY_SUBJECT_ID = "ss_id";

	public static final String EVENT_BEAN = "studyEvent";

	public static final String EVENT_DEFINITION_BEAN = "eventDefinition";

	public static final String SUBJECT_EVENT_STATUS_ID = "statusId";

	public static final String INPUT_STARTDATE_PREFIX = "start";

	public static final String INPUT_ENDDATE_PREFIX = "end";

	public static final String INPUT_LOCATION = "location";

	public final static String HAS_LOCATION_NOTE = "hasLocationNote";
	public final static String HAS_START_DATE_NOTE = "hasStartDateNote";
	public final static String HAS_END_DATE_NOTE = "hasEndDateNote";

	@Override
	public void mayProceed() throws InsufficientPermissionException {

		if (SubmitDataServlet.maySubmitData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
				+ respage.getString("change_active_study_or_contact"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	private void redirectToStudySubjectView(int studySubjectId) throws Exception {
		Map storedAttributes = new HashMap();
		storedAttributes.put(SecureController.PAGE_MESSAGE, request.getAttribute(SecureController.PAGE_MESSAGE));
		request.getSession().setAttribute(RememberLastPage.STORED_ATTRIBUTES, storedAttributes);
		String viewStudySubjectUrl = (String) request.getSession().getAttribute(
				ViewStudySubjectServlet.SAVED_VIEW_STUDY_SUBJECT_URL);
		if (viewStudySubjectUrl != null && viewStudySubjectUrl.contains("id=" + studySubjectId + "&")) {
			response.sendRedirect(viewStudySubjectUrl);
		} else {
			response.sendRedirect(request.getContextPath() + Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName() + "?id="
					+ studySubjectId);
		}
	}

	@Override
	public void processRequest() throws Exception {
		FormDiscrepancyNotes discNotes = null;
		FormProcessor fp = new FormProcessor(request);
		int studyEventId = fp.getInt(EVENT_ID, true);
		int studySubjectId = fp.getInt(STUDY_SUBJECT_ID, true);

		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);

		String fromResolvingNotes = fp.getString("fromResolvingNotes", true);
		if (StringUtil.isBlank(fromResolvingNotes)) {
			session.removeAttribute(ViewNotesServlet.WIN_LOCATION);
			session.removeAttribute(ViewNotesServlet.NOTES_TABLE);
			checkStudyLocked(Page.MANAGE_STUDY, respage.getString("current_study_locked"));
			checkStudyFrozen(Page.MANAGE_STUDY, respage.getString("current_study_frozen"));
		}

		if (studyEventId == 0 || studySubjectId == 0) {
			addPageMessage(respage.getString("choose_a_study_event_to_edit"));
			request.setAttribute("id", new Integer(studySubjectId).toString());
			redirectToStudySubjectView(studySubjectId);
			return;
		}

		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
		StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
		StudySubjectBean ssub = null;
		if (studySubjectId > 0) {
			ssub = (StudySubjectBean) ssdao.findByPK(studySubjectId);
			request.setAttribute("studySubject", ssub);
			request.setAttribute("id", studySubjectId + "");// for the workflow
			// box, so it can
			// link back to view
			// study subject
		}

		Status s = ssub.getStatus();
		if ("removed".equalsIgnoreCase(s.getName()) || "auto-removed".equalsIgnoreCase(s.getName())) {
			addPageMessage(resword.getString("study_event") + resterm.getString("could_not_be")
					+ resterm.getString("updated") + "." + respage.getString("study_subject_has_been_deleted"));
			request.setAttribute("id", new Integer(studySubjectId).toString());
			redirectToStudySubjectView(studySubjectId);
		}

		request.setAttribute(STUDY_SUBJECT_ID, new Integer(studySubjectId).toString());
		StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
		EventCRFDAO ecrfdao = new EventCRFDAO(sm.getDataSource());

		StudyEventBean studyEvent = (StudyEventBean) sedao.findByPK(studyEventId);

		studyEvent.setEventCRFs(ecrfdao.findAllByStudyEvent(studyEvent));

		ArrayList statuses = SubjectEventStatus.toArrayList();

		SubjectEventStatusUtil.preparePossibleSubjectEventStates(studyEvent.getEventCRFs(), statuses);
		if (studyEvent.getSubjectEventStatus() == SubjectEventStatus.LOCKED) {
			statuses.add(SubjectEventStatus.UNLOCK);
		}
		StudyDAO sdao = new StudyDAO(this.sm.getDataSource());
		StudyBean studyBean = (StudyBean) sdao.findByPK(ssub.getStudyId());
		checkRoleByUserAndStudy(ub, studyBean.getParentStudyId(), studyBean.getId());
		
		
		// To remove signed status from the list
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
		EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
		DiscrepancyNoteDAO discDao = new DiscrepancyNoteDAO(sm.getDataSource());
		DAOWrapper daoWrapper = new DAOWrapper(null, null, null, ecdao, edcdao, null, discDao);
		ssdao = new StudySubjectDAO(sm.getDataSource());
		StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPK(studyEvent.getStudySubjectId());
		StudyBean study = (StudyBean) sdao.findByPK(ssb.getStudyId());
		if (!SignUtil.permitSign(studyEvent, study, daoWrapper) || !currentRole.isInvestigator()
				|| study.getStatus().isPending()) {
			statuses.remove(SubjectEventStatus.SIGNED);
		}

		
		if (!studyEvent.getSubjectEventStatus().equals(SubjectEventStatus.NOT_SCHEDULED)) {
			statuses.remove(SubjectEventStatus.NOT_SCHEDULED);
		}
		if (!studyEvent.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED)) {
			// can't lock a non-completed CRF, but removed above
			statuses.remove(SubjectEventStatus.SCHEDULED);
			// statuses.remove(SubjectEventStatus.SKIPPED);
			// addl rule: skipped should only be present before data starts
			// being entered
		}
		if (studyEvent.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED)) {
			statuses.remove(SubjectEventStatus.SKIPPED);
		}

		ArrayList getECRFs = studyEvent.getEventCRFs();

		EventDefinitionCRFDAO edefcrfdao = new EventDefinitionCRFDAO(sm.getDataSource());
		ArrayList getAllECRFs = (ArrayList) edefcrfdao.findAllByDefinition(studyBean,
				studyEvent.getStudyEventDefinitionId());
		// does the study event have all complete CRFs which are required?
		logger.info("found number of ecrfs: " + getAllECRFs.size());
		// may not be populated, only entered crfs seem to ping the list
		for (int u = 0; u < getAllECRFs.size(); u++) {
			EventDefinitionCRFBean ecrfBean = (EventDefinitionCRFBean) getAllECRFs.get(u);

			//
			logger.info("found number of existing ecrfs: " + getECRFs.size());
			if (getECRFs.size() == 0) {
				statuses.remove(SubjectEventStatus.COMPLETED);
				statuses.remove(SubjectEventStatus.LOCKED);

			}// otherwise...
			for (int uv = 0; uv < getECRFs.size(); uv++) {
				EventCRFBean existingBean = (EventCRFBean) getECRFs.get(uv);
				logger.info("***** found: " + existingBean.getCRFVersionId() + " " + existingBean.getCrf().getId()
						+ " " + existingBean.getCrfVersion().getName() + " " + existingBean.getStatus().getName() + " "
						+ existingBean.getStage().getName());

				logger.info("***** comparing above to ecrfBean.DefaultVersionID: " + ecrfBean.getDefaultVersionId());

				if (!existingBean.getStatus().equals(Status.UNAVAILABLE)
						&& edefcrfdao.isRequiredInDefinition(existingBean.getCRFVersionId(), studyEvent)) {

					logger.info("found that " + existingBean.getCrfVersion().getName() + " is required...");
					// that is, it's not completed but required to complete
					statuses.remove(SubjectEventStatus.COMPLETED);
					statuses.remove(SubjectEventStatus.LOCKED);
				}
			}
		}

		if (!ub.isSysAdmin() && !currentRole.getRole().equals(Role.STUDY_DIRECTOR)
				&& !currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			statuses.remove(SubjectEventStatus.LOCKED);
		}

		// also, if data entry is started, can't move back to scheduled or not
		// scheduled
		if (studyEvent.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED)) {
			statuses.remove(SubjectEventStatus.NOT_SCHEDULED);
			statuses.remove(SubjectEventStatus.SCHEDULED);
		}

		request.setAttribute("statuses", statuses);

		String action = fp.getString("action");
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(studyEvent
				.getStudyEventDefinitionId());
		request.setAttribute(EVENT_DEFINITION_BEAN, sed);
		if (action.equalsIgnoreCase("submit")) {
			discNotes = (FormDiscrepancyNotes) session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
			DiscrepancyValidator v = new DiscrepancyValidator(new ValidatorHelper(request, getConfigurationDao()),
					discNotes);
			SubjectEventStatus ses = SubjectEventStatus.get(fp.getInt(SUBJECT_EVENT_STATUS_ID));

			if (ses == SubjectEventStatus.LOCKED && studyEvent.getSubjectEventStatus() != SubjectEventStatus.LOCKED) {
				studyEvent.setPrevSubjectEventStatus(studyEvent.getSubjectEventStatus());
				studyEvent.setSubjectEventStatus(SubjectEventStatus.LOCKED);
			} else if (ses == SubjectEventStatus.UNLOCK) {
				studyEvent.setSubjectEventStatus(studyEvent.getPrevSubjectEventStatus());
			} else {
				studyEvent.setSubjectEventStatus(ses);
			}

			ArrayList<EventCRFBean> eventCRFs = ecdao.findAllByStudyEvent(studyEvent);
			if (ses.equals(SubjectEventStatus.SKIPPED) || ses.equals(SubjectEventStatus.STOPPED)) {
				studyEvent.setStatus(Status.UNAVAILABLE);
				for (int i = 0; i < eventCRFs.size(); i++) {
					EventCRFBean ecb = (EventCRFBean) eventCRFs.get(i);
					ecb.setOldStatus(ecb.getStatus());
					ecb.setStatus(Status.UNAVAILABLE);
					ecb.setUpdater(ub);
					ecb.setUpdatedDate(new Date());
					ecdao.update(ecb);
				}
			} else {
				for (int i = 0; i < eventCRFs.size(); i++) {
					EventCRFBean ecb = (EventCRFBean) eventCRFs.get(i);
					ecb.setUpdater(ub);
					ecb.setUpdatedDate(new Date());
					ecdao.update(ecb);
				}
			}

			String strEnd = fp.getDateTimeInputString(INPUT_ENDDATE_PREFIX);
			Date start = fp.getDateTime(INPUT_STARTDATE_PREFIX);
			Date end = null;

			String strStart = fp.getDateTimeInputString(INPUT_STARTDATE_PREFIX);
			if (!strStart.equals("")) {
				v.addValidation(INPUT_STARTDATE_PREFIX, Validator.IS_DATE_TIME);
				v.alwaysExecuteLastValidation(INPUT_STARTDATE_PREFIX);
			} else if (currentStudy.getStudyParameterConfig().getStartDateTimeRequired().equals("yes")) {
				v.addValidation(INPUT_STARTDATE_PREFIX, Validator.NO_BLANKS);
			}
			if (!strEnd.equals("")) {
				v.addValidation(INPUT_ENDDATE_PREFIX, Validator.IS_DATE_TIME);
				v.alwaysExecuteLastValidation(INPUT_ENDDATE_PREFIX);
			} else if (currentStudy.getStudyParameterConfig().getEndDateTimeRequired().equals("yes")) {
				v.addValidation(INPUT_ENDDATE_PREFIX, Validator.NO_BLANKS);
			}

			// empty when updating a study event
			HashMap errors = v.validate();
			if (!strEnd.equals("") && !errors.containsKey(INPUT_STARTDATE_PREFIX)
					&& !errors.containsKey(INPUT_ENDDATE_PREFIX)) {
				end = fp.getDateTime(INPUT_ENDDATE_PREFIX);
				if (!fp.getString(INPUT_STARTDATE_PREFIX + "Date").equals(fp.getString(INPUT_ENDDATE_PREFIX + "Date"))) {
					if (end.before(start)) {
						Validator.addError(errors, INPUT_ENDDATE_PREFIX,
								resexception.getString("input_provided_not_occure_after_previous_start_date_time"));
					}
				} else {
					// if in same date, only check when both had time entered
					if (fp.timeEntered(INPUT_STARTDATE_PREFIX) && fp.timeEntered(INPUT_ENDDATE_PREFIX)) {
						if (end.before(start) || end.equals(start)) {
							Validator.addError(errors, INPUT_ENDDATE_PREFIX,
									resexception.getString("input_provided_not_occure_after_previous_start_date_time"));
						}
					}
				}
			}

			if (!errors.isEmpty()) {
				setInputMessages(errors);
				String prefixes[] = { INPUT_STARTDATE_PREFIX, INPUT_ENDDATE_PREFIX };
				fp.setCurrentDateTimeValuesAsPreset(prefixes);
				setPresetValues(fp.getPresetValues());

				studyEvent.setLocation(fp.getString(INPUT_LOCATION));

				request.setAttribute("changeDate", fp.getString("changeDate"));
				request.setAttribute(EVENT_BEAN, studyEvent);
				forwardPage(Page.UPDATE_STUDY_EVENT);

			} else if (studyEvent.getSubjectEventStatus().isSigned()) {

				request.setAttribute(STUDY_SUBJECT_ID, new Integer(studySubjectId).toString());
				if (fp.getString(INPUT_STARTDATE_PREFIX + "Hour").equals("-1")
						&& fp.getString(INPUT_STARTDATE_PREFIX + "Minute").equals("-1")
						&& fp.getString(INPUT_STARTDATE_PREFIX + "Half").equals("")) {
					studyEvent.setStartTimeFlag(false);
				} else {
					studyEvent.setStartTimeFlag(true);
				}
				studyEvent.setDateStarted(start);

				if (!strEnd.equals("")) {
					studyEvent.setDateEnded(end);
					if (fp.getString(INPUT_ENDDATE_PREFIX + "Hour").equals("-1")
							&& fp.getString(INPUT_ENDDATE_PREFIX + "Minute").equals("-1")
							&& fp.getString(INPUT_ENDDATE_PREFIX + "Half").equals("")) {
						studyEvent.setEndTimeFlag(false);
					} else {
						studyEvent.setEndTimeFlag(true);
					}
				}

				studyEvent.setLocation(fp.getString(INPUT_LOCATION));
				studyEvent.setStudyEventDefinition(sed);

				ssdao = new StudySubjectDAO(sm.getDataSource());
				ssb = (StudySubjectBean) ssdao.findByPK(studyEvent.getStudySubjectId());

				ecdao = new EventCRFDAO(sm.getDataSource());
				eventCRFs = ecdao.findAllByStudyEvent(studyEvent);
				SubjectEventStatusUtil.fillDoubleDataOwner(eventCRFs, sm);
				
				study = (StudyBean) sdao.findByPK(ssb.getStudyId());
				ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(study,
						studyEvent.getStudyEventDefinitionId());

				ArrayList uncompletedEventDefinitionCRFs = getUncompletedCRFs(eventDefinitionCRFs, eventCRFs);
				populateUncompletedCRFsWithCRFAndVersions(uncompletedEventDefinitionCRFs);

				ArrayList displayEventCRFs = ViewStudySubjectServlet.getDisplayEventCRFs(sm.getDataSource(), eventCRFs,
						eventDefinitionCRFs, ub, currentRole, studyEvent.getSubjectEventStatus(), study);

				request.setAttribute("studySubject", ssb);
				request.setAttribute("uncompletedEventDefinitionCRFs", uncompletedEventDefinitionCRFs);
				request.setAttribute("displayEventCRFs", displayEventCRFs);

				request.setAttribute(EVENT_BEAN, studyEvent);
				session.setAttribute("eventSigned", studyEvent);

				DiscrepancyNoteUtil discNoteUtil = new DiscrepancyNoteUtil();
				DisplayStudyEventBean displayEvBean = new DisplayStudyEventBean();
				List<DisplayStudyEventBean> displayEvents = new ArrayList<DisplayStudyEventBean>();
				// Set up a Map for the JSP view, mapping the eventCRFId to
				// another Map: the
				// inner Map maps the resolution status name to the number of
				// notes for that
				// eventCRF id, as in New --> 2
				displayEvBean.setStudyEvent(studyEvent);
				displayEvents.add(displayEvBean);
				// Don't filter for res status or disc note type; disc note
				// beans are returned with eventCRFId set
				discNoteUtil.injectParentDiscNotesIntoDisplayStudyEvents(displayEvents, new HashSet(),
						sm.getDataSource(), 0);
				Map discNoteByEventCRFid = discNoteUtil.createDiscNoteMapByEventCRF(displayEvents);
				request.setAttribute("discNoteByEventCRFid", discNoteByEventCRFid);

				forwardPage(Page.UPDATE_STUDY_EVENT_SIGNED);
			} else {
				logger.info("no validation error");
				if (fp.getString(INPUT_STARTDATE_PREFIX + "Hour").equals("-1")
						&& fp.getString(INPUT_STARTDATE_PREFIX + "Minute").equals("-1")
						&& fp.getString(INPUT_STARTDATE_PREFIX + "Half").equals("")) {
					studyEvent.setStartTimeFlag(false);
				} else {
					studyEvent.setStartTimeFlag(true);
				}
				studyEvent.setDateStarted(start);
				if (!strEnd.equals("")) {
					studyEvent.setDateEnded(end);
					if (fp.getString(INPUT_ENDDATE_PREFIX + "Hour").equals("-1")
							&& fp.getString(INPUT_ENDDATE_PREFIX + "Minute").equals("-1")
							&& fp.getString(INPUT_ENDDATE_PREFIX + "Half").equals("")) {
						studyEvent.setEndTimeFlag(false);
					} else {
						studyEvent.setEndTimeFlag(true);
					}
				}

				studyEvent.setLocation(fp.getString(INPUT_LOCATION));

				logger.info("update study event...");
				studyEvent.setUpdater(ub);
				studyEvent.setUpdatedDate(new Date());
				sedao.update(studyEvent);

				if (ses == SubjectEventStatus.UNLOCK) {
					ssub.setStatus(Status.AVAILABLE);
					ssdao.update(ssub);
				} else if (ses == SubjectEventStatus.LOCKED) {
					int count = 0;
					List<StudyEventBean> studyEventList = sedao.findAllByStudySubject(ssub);
					for (StudyEventBean studyEventBean : studyEventList) {
						if (studyEventBean.getId() != studyEvent.getId()
								&& studyEventBean.getSubjectEventStatus() == SubjectEventStatus.LOCKED) {
							count++;
						}
					}
					if (count == studyEventList.size() - 1) {
						ssub.setStatus(Status.LOCKED);
						ssdao.update(ssub);
					}
				}

				if (studyEvent.getSubjectEventStatus().isCompleted()) {
					StdScheduler scheduler = getScheduler(request);
					CalendarLogic calLogic = new CalendarLogic(sm.getDataSource(), scheduler);
					calLogic.ScheduleSubjectEvents(studyEvent);
					String message = calLogic.MaxMinDaysValidator(studyEvent);
					if (!"empty".equalsIgnoreCase(message)) {
						addPageMessage(message);
					}
				}

				// save discrepancy notes into DB
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session
						.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
				DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());

				AddNewSubjectServlet.saveFieldNotes(INPUT_LOCATION, fdn, dndao, studyEvent.getId(), "studyEvent",
						currentStudy);
				AddNewSubjectServlet.saveFieldNotes(INPUT_STARTDATE_PREFIX, fdn, dndao, studyEvent.getId(),
						"studyEvent", currentStudy);
				AddNewSubjectServlet.saveFieldNotes(INPUT_ENDDATE_PREFIX, fdn, dndao, studyEvent.getId(), "studyEvent",
						currentStudy);

				addPageMessage(respage.getString("study_event_updated"));
				request.setAttribute("id", new Integer(studySubjectId).toString());
				session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);

				redirectToStudySubjectView(studySubjectId);
			}
		} else if (action.equalsIgnoreCase("confirm")) {// confirming the signed
			// status
			String username = request.getParameter("j_user");
			String password = request.getParameter("j_pass");
			SecurityManager securityManager = ((SecurityManager) SpringServletAccess.getApplicationContext(context)
					.getBean("securityManager"));
			UserAccountBean ub = (UserAccountBean) session.getAttribute("userBean");
			StudyEventBean seb = (StudyEventBean) session.getAttribute("eventSigned");
			if (securityManager.isPasswordValid(ub.getPasswd(), password, getUserDetails())
					&& ub.getName().equals(username)) {
				seb.setUpdater(ub);
				seb.setUpdatedDate(new Date());
				sedao.update(seb);

				// If all the StudyEvents become signed we will make the
				// StudySubject signed as well
				List studyEvents = sedao.findAllByStudySubject(ssub);
				boolean allSigned = true;
				for (Iterator iterator = studyEvents.iterator(); iterator.hasNext();) {
					StudyEventBean temp = (StudyEventBean) iterator.next();
					if (!temp.getSubjectEventStatus().equals(SubjectEventStatus.SIGNED)) {
						allSigned = false;
						break;
					}
				}
				if (allSigned) {
					logger.info("Signing StudySubject [" + ssub.getSubjectId() + "]");
					ssub.setStatus(Status.SIGNED);
					ssub.setUpdater(ub);
					ssdao.update(ssub);
				}

				// save discrepancy notes into DB
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session
						.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
				DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());

				AddNewSubjectServlet.saveFieldNotes(INPUT_LOCATION, fdn, dndao, studyEvent.getId(), "studyEvent",
						currentStudy);
				AddNewSubjectServlet.saveFieldNotes(INPUT_STARTDATE_PREFIX, fdn, dndao, studyEvent.getId(),
						"studyEvent", currentStudy);
				AddNewSubjectServlet.saveFieldNotes(INPUT_ENDDATE_PREFIX, fdn, dndao, studyEvent.getId(), "studyEvent",
						currentStudy);

				session.removeAttribute("eventSigned");
				request.setAttribute("id", new Integer(studySubjectId).toString());
				addPageMessage(respage.getString("study_event_updated"));
				redirectToStudySubjectView(studySubjectId);
			} else {
				request.setAttribute(STUDY_SUBJECT_ID, new Integer(studySubjectId).toString());
				request.setAttribute("studyEvent", seb);
				// -------------------
				ssdao = new StudySubjectDAO(sm.getDataSource());
				ssb = (StudySubjectBean) ssdao.findByPK(studyEvent.getStudySubjectId());

				// prepare to figure out what the display should look like
				ecdao = new EventCRFDAO(sm.getDataSource());
				ArrayList<EventCRFBean> eventCRFs = ecdao.findAllByStudyEvent(studyEvent);
				study = (StudyBean) sdao.findByPK(ssb.getStudyId());
				ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(study,
						studyEvent.getStudyEventDefinitionId());

				ArrayList uncompletedEventDefinitionCRFs = getUncompletedCRFs(eventDefinitionCRFs, eventCRFs);
				populateUncompletedCRFsWithCRFAndVersions(uncompletedEventDefinitionCRFs);

				ArrayList displayEventCRFs = ViewStudySubjectServlet.getDisplayEventCRFs(sm.getDataSource(), eventCRFs,
						eventDefinitionCRFs, ub, currentRole, studyEvent.getSubjectEventStatus(), study);

				request.setAttribute("studySubject", ssb);
				request.setAttribute("uncompletedEventDefinitionCRFs", uncompletedEventDefinitionCRFs);
				request.setAttribute("displayEventCRFs", displayEventCRFs);

				// ------------------
				request.setAttribute("studyEvent", session.getAttribute("eventSigned"));
				addPageMessage(restext.getString("password_match"));
				forwardPage(Page.UPDATE_STUDY_EVENT_SIGNED);
			}
		} else {
			logger.info("no action, go to update page");

			DiscrepancyNoteDAO discrepancyNoteDAO = new DiscrepancyNoteDAO(sm.getDataSource());
			StudySubjectBean studySubjectBean = (StudySubjectBean) ssdao.findByPK(studyEvent.getStudySubjectId());
			int studyId = studySubjectBean.getStudyId();
			boolean subjectStudyIsCurrentStudy = studyId == currentStudy.getId();
			boolean isParentStudy = studyBean.getParentStudyId() < 1;

			ArrayList<DiscrepancyNoteBean> allNotesforSubjectAndEvent = new ArrayList<DiscrepancyNoteBean>();

			if (subjectStudyIsCurrentStudy && isParentStudy) {
				allNotesforSubjectAndEvent = discrepancyNoteDAO.findAllStudyEventByStudyAndId(currentStudy,
						studySubjectBean.getId());
				if (!isParentStudy) {
					StudyBean stParent = (StudyBean) sdao.findByPK(studyBean.getParentStudyId());
					allNotesforSubjectAndEvent = discrepancyNoteDAO.findAllStudyEventByStudiesAndSubjectId(stParent,
							studyBean, studySubjectBean.getId());
				} else {

					allNotesforSubjectAndEvent = discrepancyNoteDAO.findAllStudyEventByStudiesAndSubjectId(
							currentStudy, studyBean, studySubjectBean.getId());
				}

			}

			if (!allNotesforSubjectAndEvent.isEmpty()) {
				setRequestAttributesForNotes(allNotesforSubjectAndEvent);
			}

			HashMap presetValues = new HashMap();
			if (studyEvent.getStartTimeFlag() == true) {
				Calendar c = new GregorianCalendar();
				c.setTime(studyEvent.getDateStarted());
				presetValues.put(INPUT_STARTDATE_PREFIX + "Hour", new Integer(c.get(Calendar.HOUR)));
				presetValues.put(INPUT_STARTDATE_PREFIX + "Minute", new Integer(c.get(Calendar.MINUTE)));
				// Later it could be put to somewhere as a static method if
				// necessary.
				switch (c.get(Calendar.AM_PM)) {
				case 0:
					presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "am");
					break;
				case 1:
					presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "pm");
					break;
				default:
					presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "");
					break;
				}
			} else {
				presetValues.put(INPUT_STARTDATE_PREFIX + "Hour", new Integer(-1));
				presetValues.put(INPUT_STARTDATE_PREFIX + "Minute", new Integer(-1));
				presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "");
			}

			String dateValue = local_df.format(studyEvent.getDateStarted());
			presetValues.put(INPUT_STARTDATE_PREFIX + "Date", dateValue);

			presetValues.put(INPUT_ENDDATE_PREFIX + "Hour", new Integer(-1));
			presetValues.put(INPUT_ENDDATE_PREFIX + "Minute", new Integer(-1));
			presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "");
			if (studyEvent.getDateEnded() != null) {
				if (studyEvent.getEndTimeFlag() == true) {
					Calendar c = new GregorianCalendar();
					c.setTime(studyEvent.getDateEnded());
					presetValues.put(INPUT_ENDDATE_PREFIX + "Hour", new Integer(c.get(Calendar.HOUR)));
					presetValues.put(INPUT_ENDDATE_PREFIX + "Minute", new Integer(c.get(Calendar.MINUTE)));
					// Later it could be put to somewhere as a static method if
					// necessary.
					switch (c.get(Calendar.AM_PM)) {
					case 0:
						presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "am");
						break;
					case 1:
						presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "pm");
						break;
					default:
						presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "");
						break;
					}
				}
				presetValues.put(INPUT_ENDDATE_PREFIX + "Date", local_df.format(studyEvent.getDateEnded()));
			}

			setPresetValues(presetValues);

			request.setAttribute("studyEvent", studyEvent);
			request.setAttribute("studySubject", studySubjectBean);

			discNotes = new FormDiscrepancyNotes();
			session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

			forwardPage(Page.UPDATE_STUDY_EVENT);
		}

	}

	private ArrayList getUncompletedCRFs(ArrayList eventDefinitionCRFs, ArrayList eventCRFs) {
		int i;
		HashMap completed = new HashMap();
		HashMap startedButIncompleted = new HashMap();
		ArrayList answer = new ArrayList();

		/**
		 * A somewhat non-standard algorithm is used here: let answer = empty; foreach event definition ED, set
		 * isCompleted(ED) = false foreach event crf EC, set isCompleted(EC.getEventDefinition()) = true foreach event
		 * definition ED, if (!isCompleted(ED)) { answer += ED; } return answer; This algorithm is guaranteed to find
		 * all the event definitions for which no event CRF exists.
		 * 
		 * The motivation for using this algorithm is reducing the number of database hits.
		 * 
		 * -jun-we have to add more CRFs here: the event CRF which dones't have item data yet
		 */

		for (i = 0; i < eventDefinitionCRFs.size(); i++) {
			EventDefinitionCRFBean edcrf = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
			completed.put(new Integer(edcrf.getCrfId()), Boolean.FALSE);
			startedButIncompleted.put(new Integer(edcrf.getCrfId()), new EventCRFBean());
		}

		CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
		ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
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

	private void populateUncompletedCRFsWithCRFAndVersions(ArrayList uncompletedEventDefinitionCRFs) {
		CRFDAO cdao = new CRFDAO(sm.getDataSource());
		CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());

		int size = uncompletedEventDefinitionCRFs.size();
		for (int i = 0; i < size; i++) {
			DisplayEventDefinitionCRFBean dedcrf = (DisplayEventDefinitionCRFBean) uncompletedEventDefinitionCRFs
					.get(i);
			CRFBean cb = (CRFBean) cdao.findByPK(dedcrf.getEdc().getCrfId());
			if (cb.getStatus().equals(Status.AVAILABLE)) {
				// the above does not allow us to show the CRF as a thing with
				// status of 'invalid' so we have to
				// go to the JSP for this one, I think
				dedcrf.getEdc().setCrf(cb);

				ArrayList versions = (ArrayList) cvdao.findAllActiveByCRF(dedcrf.getEdc().getCrfId());
				dedcrf.getEdc().setVersions(versions);
				if (versions != null && versions.size() != 0) {
					boolean isLocked = false;
					for (int ii = 0; ii < versions.size(); ii++) {
						CRFVersionBean crfvb = (CRFVersionBean) versions.get(ii);
						logger.info("...checking versions..." + crfvb.getName());
						if (!crfvb.getStatus().equals(Status.AVAILABLE)) {
							logger.info("found a non active crf version");
							isLocked = true;
						}
					}
					logger.info("re-set event def, line 240: " + isLocked);
					if (isLocked) {
						dedcrf.setStatus(Status.LOCKED);
						dedcrf.getEventCRF().setStage(DataEntryStage.LOCKED);
					}
					uncompletedEventDefinitionCRFs.set(i, dedcrf);
				} else {
					dedcrf.setStatus(Status.LOCKED);
					dedcrf.getEventCRF().setStage(DataEntryStage.LOCKED);
					uncompletedEventDefinitionCRFs.set(i, dedcrf);
				}
			} else {
				dedcrf.getEdc().setCrf(cb);
				logger.info("_found a non active crf _");
				dedcrf.setStatus(Status.LOCKED);
				dedcrf.getEventCRF().setStage(DataEntryStage.LOCKED);
				dedcrf.getEdc().getCrf().setStatus(Status.LOCKED);
				uncompletedEventDefinitionCRFs.set(i, dedcrf);
			}
		}
	}

	@Override
	protected String getAdminServlet() {
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	private void setRequestAttributesForNotes(List<DiscrepancyNoteBean> discBeans) {
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

	private StdScheduler getScheduler(HttpServletRequest request) {
		StdScheduler scheduler = (StdScheduler) SpringServletAccess.getApplicationContext(
				request.getSession().getServletContext()).getBean("schedulerFactoryBean");
		return scheduler;
	}
}
