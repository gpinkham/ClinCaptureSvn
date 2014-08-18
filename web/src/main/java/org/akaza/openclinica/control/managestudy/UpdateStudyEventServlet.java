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
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.control.submit.EnterDataForStudyEventServlet;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.service.calendar.CalendarLogic;
import org.akaza.openclinica.util.DAOWrapper;
import org.akaza.openclinica.util.SignUtil;
import org.akaza.openclinica.util.StudyEventDefinitionUtil;
import org.akaza.openclinica.util.SubjectEventStatusUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.quartz.impl.StdScheduler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author jxu
 * 
 *         Performs updating study event action
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class UpdateStudyEventServlet extends Controller {

	private static final long serialVersionUID = -6029524999558420563L;

	public static final String EVENT_ID = "event_id";

	public static final String STUDY_SUBJECT_ID = "ss_id";

	public static final String EVENT_BEAN = "studyEvent";

	public static final String EVENT_DEFINITION_BEAN = "eventDefinition";

	public static final String SUBJECT_EVENT_STATUS_ID = "statusId";

	public static final String INPUT_STARTDATE_PREFIX = "start";

	public static final String INPUT_ENDDATE_PREFIX = "end";

	public static final String INPUT_LOCATION = "location";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (SubmitDataServlet.maySubmitData(ub, currentRole)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study") + " "
						+ respage.getString("change_active_study_or_contact"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	private void redirectToStudySubjectView(HttpServletRequest request, HttpServletResponse response, int studySubjectId)
			throws Exception {
		Map storedAttributes = new HashMap();
		storedAttributes.put(Controller.PAGE_MESSAGE, request.getAttribute(Controller.PAGE_MESSAGE));
		request.getSession().setAttribute(BaseController.STORED_ATTRIBUTES, storedAttributes);
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
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		HttpSession session = request.getSession();
		SessionManager sm = getSessionManager(request);
		FormDiscrepancyNotes discNotes;
		FormProcessor fp = new FormProcessor(request);
		int studyEventId = fp.getInt(EVENT_ID, true);
		int studySubjectId = fp.getInt(STUDY_SUBJECT_ID, true);

		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);

		String fromResolvingNotes = fp.getString("fromResolvingNotes", true);
		if (StringUtil.isBlank(fromResolvingNotes)) {
			session.removeAttribute(ViewNotesServlet.WIN_LOCATION);
			session.removeAttribute(ViewNotesServlet.NOTES_TABLE);
			checkStudyLocked(Page.MENU_SERVLET, respage.getString("current_study_locked"), request, response);
			if (currentRole.getRole() != Role.INVESTIGATOR) {
				checkStudyFrozen(Page.MENU_SERVLET, respage.getString("current_study_frozen"), request, response);
			}
		}

		if (studyEventId == 0 || studySubjectId == 0) {
			addPageMessage(respage.getString("choose_a_study_event_to_edit"), request);
			request.setAttribute("id", Integer.toString(studySubjectId));
			redirectToStudySubjectView(request, response, studySubjectId);
			return;
		}

		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
		StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
		StudySubjectBean ssub = null;
		if (studySubjectId > 0) {
			ssub = (StudySubjectBean) ssdao.findByPK(studySubjectId);
			request.setAttribute("studySubject", ssub);
			request.setAttribute("id", studySubjectId + "");// for the workflow box, so it can link back to view study
															// subject
		}

		Status s = ssub.getStatus();
		if ("removed".equalsIgnoreCase(s.getName()) || "auto-removed".equalsIgnoreCase(s.getName())) {
			addPageMessage(
					resword.getString("study_event") + resterm.getString("could_not_be") + resterm.getString("updated")
							+ "." + respage.getString("study_subject_has_been_deleted"), request);
			request.setAttribute("id", Integer.toString(studySubjectId));
			redirectToStudySubjectView(request, response, studySubjectId);
		}

		request.setAttribute(STUDY_SUBJECT_ID, Integer.toString(studySubjectId));
		StudyEventDAO sedao = getStudyEventDAO();
		EventCRFDAO ecrfdao = getEventCRFDAO();

		StudyEventBean studyEvent = (StudyEventBean) sedao.findByPK(studyEventId);

		studyEvent.setEventCRFs(ecrfdao.findAllByStudyEvent(studyEvent));

		ArrayList statuses = SubjectEventStatus.toArrayList();

		SubjectEventStatusUtil.preparePossibleSubjectEventStates(studyEvent.getEventCRFs(), statuses);
		if (studyEvent.getSubjectEventStatus() == SubjectEventStatus.LOCKED) {
			statuses.add(SubjectEventStatus.UNLOCK);
		}
		StudyDAO sdao = new StudyDAO(getDataSource());
		StudyBean studyBean = (StudyBean) sdao.findByPK(ssub.getStudyId());
		checkRoleByUserAndStudy(request, response, ub, studyBean.getParentStudyId(), studyBean.getId());

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
		if (!studyEvent.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED)) {
			// can't lock a non-completed CRF, but removed above
			statuses.remove(SubjectEventStatus.SCHEDULED);
			statuses.remove(SubjectEventStatus.NOT_SCHEDULED);
			// addl rule: skipped should only be present before data starts being entered
		}
		if (studyEvent.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED)) {
			statuses.remove(SubjectEventStatus.SKIPPED);
		}

		ArrayList getECRFs = studyEvent.getEventCRFs();

		EventDefinitionCRFDAO edefcrfdao = getEventDefinitionCRFDAO();
		ArrayList getAllECRFs = (ArrayList) edefcrfdao.findAllByDefinition(studyBean,
				studyEvent.getStudyEventDefinitionId());
		// does the study event have all complete CRFs which are required?
		logger.info("found number of ecrfs: " + getAllECRFs.size());
		// may not be populated, only entered crfs seem to ping the list
		for (Object getAllECRF : getAllECRFs) {
			EventDefinitionCRFBean ecrfBean = (EventDefinitionCRFBean) getAllECRF;

			//
			logger.info("found number of existing ecrfs: " + getECRFs.size());
			if (getECRFs.size() == 0) {
				statuses.remove(SubjectEventStatus.COMPLETED);
				statuses.remove(SubjectEventStatus.LOCKED);

			}// otherwise...
			for (Object getECRF : getECRFs) {
				EventCRFBean existingBean = (EventCRFBean) getECRF;
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

		// also, if data entry is started, can't move back to scheduled or not scheduled
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
			} else if (ses.equals(SubjectEventStatus.NOT_SCHEDULED)) {
				request.setAttribute("enent_id", studyEventId);
				request.setAttribute("deletedDurringUpdateStudyEvent", "true");
				forwardPage(Page.DELETE_STUDY_EVENT_SERVLET, request, response);
				return;
			} else {
				studyEvent.setSubjectEventStatus(ses);
			}

			ArrayList<EventCRFBean> eventCRFs = ecdao.findAllByStudyEvent(studyEvent);
			if (ses.equals(SubjectEventStatus.SKIPPED) || ses.equals(SubjectEventStatus.STOPPED)) {
				studyEvent.setStatus(Status.UNAVAILABLE);
				for (EventCRFBean ecb : eventCRFs) {
					ecb.setOldStatus(ecb.getStatus());
					ecb.setStatus(Status.UNAVAILABLE);
					ecb.setUpdater(ub);
					ecb.setUpdatedDate(new Date());
					ecdao.update(ecb);
				}
			} else {
				for (EventCRFBean ecb : eventCRFs) {
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
				setInputMessages(errors, request);
				String prefixes[] = { INPUT_STARTDATE_PREFIX, INPUT_ENDDATE_PREFIX };
				fp.setCurrentDateTimeValuesAsPreset(prefixes);
				setPresetValues(fp.getPresetValues(), request);

				studyEvent.setLocation(fp.getString(INPUT_LOCATION));

				request.setAttribute("changeDate", fp.getString("changeDate"));
				request.setAttribute(EVENT_BEAN, studyEvent);
				forwardPage(Page.UPDATE_STUDY_EVENT, request, response);

			} else if (studyEvent.getSubjectEventStatus().isSigned()) {

				request.setAttribute(STUDY_SUBJECT_ID, Integer.toString(studySubjectId));
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

				ssdao = new StudySubjectDAO(getDataSource());
				ssb = (StudySubjectBean) ssdao.findByPK(studyEvent.getStudySubjectId());

				ecdao = new EventCRFDAO(getDataSource());
				eventCRFs = ecdao.findAllByStudyEvent(studyEvent);
				SubjectEventStatusUtil.fillDoubleDataOwner(eventCRFs, sm);

				study = (StudyBean) sdao.findByPK(ssb.getStudyId());
				ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(study,
						studyEvent.getStudyEventDefinitionId());

				ArrayList uncompletedEventDefinitionCRFs = getUncompletedCRFs(eventDefinitionCRFs, eventCRFs);
				populateUncompletedCRFsWithCRFAndVersions(uncompletedEventDefinitionCRFs);

				ArrayList displayEventCRFs = getDisplayEventCRFs(getDataSource(), eventCRFs, ub,
						currentRole, studyEvent.getSubjectEventStatus(), study);

				request.setAttribute("studySubject", ssb);
				request.setAttribute("uncompletedEventDefinitionCRFs", uncompletedEventDefinitionCRFs);
				request.setAttribute("displayEventCRFs", displayEventCRFs);

				request.setAttribute(EVENT_BEAN, studyEvent);
				request.getSession().setAttribute("eventSigned", studyEvent);

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
				discNoteUtil.injectParentDiscNotesIntoDisplayStudyEvents(displayEvents, new HashSet(), getDataSource(),
						0);
				Map discNoteByEventCRFid = discNoteUtil.createDiscNoteMapByEventCRF(displayEvents);
				request.setAttribute("discNoteByEventCRFid", discNoteByEventCRFid);

				forwardPage(Page.UPDATE_STUDY_EVENT_SIGNED, request, response);
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
					List<Integer> studyEventDefinitionIds = StudyEventDefinitionUtil
							.getStudyEventDefinitionIdsForStudySubject(ssub, studyBean, getDynamicEventDao(),
									getStudyGroupClassDAO(), seddao);
					List<StudyEventBean> studyEventList = sedao.findAllByStudySubject(ssub);
					for (StudyEventBean studyEventBean : studyEventList) {
						studyEventDefinitionIds.remove((Integer) studyEventBean.getStudyEventDefinitionId());
						if (studyEventBean.getId() != studyEvent.getId()
								&& studyEventBean.getSubjectEventStatus() == SubjectEventStatus.LOCKED) {
							count++;
						}
					}
					if (count == studyEventList.size() - 1 && studyEventDefinitionIds.size() == 0) {
						ssub.setStatus(Status.LOCKED);
						ssdao.update(ssub);
					}
				}

				if (studyEvent.getSubjectEventStatus().isCompleted()) {
					StdScheduler scheduler = getStdScheduler();
					CalendarLogic calLogic = new CalendarLogic(getDataSource(), scheduler);
					calLogic.ScheduleSubjectEvents(studyEvent);
					String message = calLogic.MaxMinDaysValidator(studyEvent);
					if (!"empty".equalsIgnoreCase(message)) {
						addPageMessage(message, request);
					}
				}

				// save discrepancy notes into DB
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession().getAttribute(
						AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
				DiscrepancyNoteDAO dndao = getDiscrepancyNoteDAO();

				AddNewSubjectServlet.saveFieldNotes(INPUT_LOCATION, fdn, dndao, studyEvent.getId(), "studyEvent",
						currentStudy);
				AddNewSubjectServlet.saveFieldNotes(INPUT_STARTDATE_PREFIX, fdn, dndao, studyEvent.getId(),
						"studyEvent", currentStudy);
				AddNewSubjectServlet.saveFieldNotes(INPUT_ENDDATE_PREFIX, fdn, dndao, studyEvent.getId(), "studyEvent",
						currentStudy);

				addPageMessage(respage.getString("study_event_updated"), request);
				request.setAttribute("id", Integer.toString(studySubjectId));
				request.getSession().removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);

				redirectToStudySubjectView(request, response, studySubjectId);
			}
		} else if (action.equalsIgnoreCase("confirm")) {// confirming the signed
			// status
			String username = request.getParameter("j_user");
			String password = request.getParameter("j_pass");
			SecurityManager securityManager = getSecurityManager();
			StudyEventBean seb = (StudyEventBean) request.getSession().getAttribute("eventSigned");
			if (securityManager.isPasswordValid(ub.getPasswd(), password, getUserDetails())
					&& ub.getName().equals(username)) {
				seb.setUpdater(ub);
				seb.setUpdatedDate(new Date());
				sedao.update(seb);

				// If all the StudyEvents become signed we will make the
				// StudySubject signed as well
				List studyEvents = sedao.findAllByStudySubject(ssub);
				boolean allSigned = true;
				for (Object studyEvent1 : studyEvents) {
					StudyEventBean temp = (StudyEventBean) studyEvent1;
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
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession().getAttribute(
						AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
				DiscrepancyNoteDAO dndao = getDiscrepancyNoteDAO();

				AddNewSubjectServlet.saveFieldNotes(INPUT_LOCATION, fdn, dndao, studyEvent.getId(), "studyEvent",
						currentStudy);
				AddNewSubjectServlet.saveFieldNotes(INPUT_STARTDATE_PREFIX, fdn, dndao, studyEvent.getId(),
						"studyEvent", currentStudy);
				AddNewSubjectServlet.saveFieldNotes(INPUT_ENDDATE_PREFIX, fdn, dndao, studyEvent.getId(), "studyEvent",
						currentStudy);

				session.removeAttribute("eventSigned");
				request.setAttribute("id", Integer.toString(studySubjectId));
				addPageMessage(respage.getString("study_event_updated"), request);
				redirectToStudySubjectView(request, response, studySubjectId);
			} else {
				request.setAttribute(STUDY_SUBJECT_ID, Integer.toString(studySubjectId));
				request.setAttribute("studyEvent", seb);
				// -------------------
				ssdao = new StudySubjectDAO(getDataSource());
				ssb = (StudySubjectBean) ssdao.findByPK(studyEvent.getStudySubjectId());

				// prepare to figure out what the display should look like
				ecdao = new EventCRFDAO(getDataSource());
				ArrayList<EventCRFBean> eventCRFs = ecdao.findAllByStudyEvent(studyEvent);
				study = (StudyBean) sdao.findByPK(ssb.getStudyId());
				ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(study,
						studyEvent.getStudyEventDefinitionId());

				ArrayList uncompletedEventDefinitionCRFs = getUncompletedCRFs(eventDefinitionCRFs, eventCRFs);
				populateUncompletedCRFsWithCRFAndVersions(uncompletedEventDefinitionCRFs);

				ArrayList displayEventCRFs = getDisplayEventCRFs(getDataSource(), eventCRFs, ub,
						currentRole, studyEvent.getSubjectEventStatus(), study);

				request.setAttribute("studySubject", ssb);
				request.setAttribute("uncompletedEventDefinitionCRFs", uncompletedEventDefinitionCRFs);
				request.setAttribute("displayEventCRFs", displayEventCRFs);

				// ------------------
				request.setAttribute("studyEvent", session.getAttribute("eventSigned"));
				addPageMessage(restext.getString("password_match"), request);
				forwardPage(Page.UPDATE_STUDY_EVENT_SIGNED, request, response);
			}
		} else {
			logger.info("no action, go to update page");

			StudySubjectBean studySubjectBean = (StudySubjectBean) ssdao.findByPK(studyEvent.getStudySubjectId());
			List<DiscrepancyNoteBean> allNotesforSubjectAndEvent = DiscrepancyNoteUtil.getAllNotesforSubjectAndEvent(
					studySubjectBean, currentStudy, sm);

			EnterDataForStudyEventServlet.setRequestAttributesForNotes(allNotesforSubjectAndEvent, studyEvent, sm,
					request);

			HashMap presetValues = new HashMap();
			if (studyEvent.getStartTimeFlag()) {
				Calendar c = new GregorianCalendar();
				c.setTime(studyEvent.getDateStarted());
				presetValues.put(INPUT_STARTDATE_PREFIX + "Hour", c.get(Calendar.HOUR));
				presetValues.put(INPUT_STARTDATE_PREFIX + "Minute", c.get(Calendar.MINUTE));
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
				presetValues.put(INPUT_STARTDATE_PREFIX + "Hour", -1);
				presetValues.put(INPUT_STARTDATE_PREFIX + "Minute", -1);
				presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "");
			}

			SimpleDateFormat local_df = getLocalDf(request);
			String dateValue = local_df.format(studyEvent.getDateStarted());
			presetValues.put(INPUT_STARTDATE_PREFIX + "Date", dateValue);

			presetValues.put(INPUT_ENDDATE_PREFIX + "Hour", -1);
			presetValues.put(INPUT_ENDDATE_PREFIX + "Minute", -1);
			presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "");
			if (studyEvent.getDateEnded() != null) {
				if (studyEvent.getEndTimeFlag()) {
					Calendar c = new GregorianCalendar();
					c.setTime(studyEvent.getDateEnded());
					presetValues.put(INPUT_ENDDATE_PREFIX + "Hour", c.get(Calendar.HOUR));
					presetValues.put(INPUT_ENDDATE_PREFIX + "Minute", c.get(Calendar.MINUTE));
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

			setPresetValues(presetValues, request);

			request.setAttribute("studyEvent", studyEvent);
			request.setAttribute("studySubject", studySubjectBean);

			discNotes = new FormDiscrepancyNotes();
			request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

			forwardPage(Page.UPDATE_STUDY_EVENT, request, response);
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
			completed.put(edcrf.getCrfId(), Boolean.FALSE);
			startedButIncompleted.put(edcrf.getCrfId(), new EventCRFBean());
		}

		CRFVersionDAO cvdao = getCRFVersionDAO();
		ItemDataDAO iddao = getItemDataDAO();
		for (i = 0; i < eventCRFs.size(); i++) {
			EventCRFBean ecrf = (EventCRFBean) eventCRFs.get(i);
			int crfId = cvdao.getCRFIdFromCRFVersionId(ecrf.getCRFVersionId());
			ArrayList idata = iddao.findAllByEventCRFId(ecrf.getId());
			if (!idata.isEmpty()) {// this crf has data already
				completed.put(crfId, Boolean.TRUE);
			} else {// event crf got created, but no data entered
				startedButIncompleted.put(crfId, ecrf);
			}
		}

		for (i = 0; i < eventDefinitionCRFs.size(); i++) {
			DisplayEventDefinitionCRFBean dedc = new DisplayEventDefinitionCRFBean();
			EventDefinitionCRFBean edcrf = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
			dedc.setEdc(edcrf);
			Boolean b = (Boolean) completed.get(new Integer(edcrf.getCrfId()));
			EventCRFBean ev = (EventCRFBean) startedButIncompleted.get(new Integer(edcrf.getCrfId()));
			if (b == null || !b) {
				dedc.setEventCRF(ev);
				answer.add(dedc);
			}
		}

		return answer;
	}

	private void populateUncompletedCRFsWithCRFAndVersions(ArrayList uncompletedEventDefinitionCRFs) {
		CRFDAO cdao = getCRFDAO();
		CRFVersionDAO cvdao = getCRFVersionDAO();

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
					for (Object version : versions) {
						CRFVersionBean crfvb = (CRFVersionBean) version;
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
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}
}
