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
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.service.calendar.CalendarLogic;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.impl.StdScheduler;
import org.springframework.stereotype.Component;

import com.clinovo.util.DAOWrapper;
import com.clinovo.util.DateUtil;
import com.clinovo.util.SDVUtil;
import com.clinovo.util.SignUtil;
import com.clinovo.util.SubjectEventStatusUtil;
import com.clinovo.util.ValidatorHelper;

/**
 * Performs updating study event action.
 */
@Component
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public class UpdateStudyEventServlet extends SpringServlet {

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

		if (maySubmitData(ub, currentRole)) {
			return;
		}

		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study") + " "
				+ getResPage().getString("change_active_study_or_contact"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_study_director"),
				"1");

	}

	private void redirectToStudySubjectView(HttpServletRequest request, HttpServletResponse response,
			int studySubjectId) throws Exception {
		Map storedAttributes = new HashMap();
		storedAttributes.put(SpringServlet.PAGE_MESSAGE, request.getAttribute(SpringServlet.PAGE_MESSAGE));
		request.getSession().setAttribute(SpringServlet.STORED_ATTRIBUTES, storedAttributes);
		String viewStudySubjectUrl = (String) request.getSession()
				.getAttribute(RememberLastPage.getUrlKey(ViewStudySubjectServlet.class));
		if (viewStudySubjectUrl != null && viewStudySubjectUrl.contains("id=" + studySubjectId + "&")) {
			response.sendRedirect(viewStudySubjectUrl);
		} else {
			response.sendRedirect(
					request.getContextPath() + Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName() + "?id=" + studySubjectId);
		}
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormDiscrepancyNotes discNotes;
		HttpSession session = request.getSession();
		FormProcessor fp = new FormProcessor(request);
		SessionManager sm = getSessionManager(request);
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);
		DiscrepancyNoteService dnService = new DiscrepancyNoteService(getDataSource());

		if (currentStudy.getParentStudyId() > 0) {
			StudyBean parentStudyBean = (StudyBean) getStudyDAO().findByPK(currentStudy.getParentStudyId());
			request.setAttribute("parentStudyOid", parentStudyBean.getOid());
		} else {
			request.setAttribute("parentStudyOid", currentStudy.getOid());
		}

		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);
		String action = fp.getString("action");
		int studyEventId = fp.getInt(EVENT_ID, true);
		int studySubjectId = fp.getInt(STUDY_SUBJECT_ID, true);

		String fromResolvingNotes = fp.getString("fromResolvingNotes", true);
		if (StringUtil.isBlank(fromResolvingNotes)) {
			session.removeAttribute(ViewNotesServlet.WIN_LOCATION);
			session.removeAttribute(ViewNotesServlet.NOTES_TABLE);
			checkStudyLocked(Page.MENU_SERVLET, getResPage().getString("current_study_locked"), request, response);
			if (currentRole.getRole() != Role.INVESTIGATOR) {
				checkStudyFrozen(Page.MENU_SERVLET, getResPage().getString("current_study_frozen"), request, response);
			}
		}

		if (studyEventId == 0 || studySubjectId == 0) {
			addPageMessage(getResPage().getString("choose_a_study_event_to_edit"), request);
			request.setAttribute("id", Integer.toString(studySubjectId));
			redirectToStudySubjectView(request, response, studySubjectId);
			return;
		}

		StudyEventDAO sedao = getStudyEventDAO();
		StudySubjectDAO ssdao = getStudySubjectDAO();
		DAOWrapper daoWrapper = new DAOWrapper(getDataSource());
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();

		StudySubjectBean ssub = ssdao.findByPK(studySubjectId);
		StudyEventBean studyEvent = (StudyEventBean) sedao.findByPK(studyEventId);
		StudyBean studyBean = (StudyBean) getStudyDAO().findByPK(ssub.getStudyId());
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
				.findByPK(studyEvent.getStudyEventDefinitionId());

		Status s = ssub.getStatus();
		if ("removed".equalsIgnoreCase(s.getName()) || "auto-removed".equalsIgnoreCase(s.getName())) {
			addPageMessage(getResWord().getString("study_event") + getResTerm().getString("could_not_be")
					+ getResTerm().getString("updated") + "."
					+ getResPage().getString("study_subject_has_been_deleted"), request);
			request.setAttribute("id", Integer.toString(studySubjectId));
			redirectToStudySubjectView(request, response, studySubjectId);
		}

		request.setAttribute("studySubject", ssub);
		request.setAttribute("enent_id", studyEventId);
		request.setAttribute(EVENT_DEFINITION_BEAN, sed);
		request.setAttribute("id", Integer.toString(studySubjectId));
		request.setAttribute(STUDY_SUBJECT_ID, Integer.toString(studySubjectId));

		checkRoleByUserAndStudy(request, response, ub, studyBean.getParentStudyId(), studyBean.getId());

		ArrayList eventCRFs = getEventCRFDAO().findAllByStudyEvent(studyEvent);
		ArrayList eventDefinitionCRFs = (ArrayList) getEventDefinitionCRFDAO()
				.findAllActiveByEventDefinitionId(studyBean, studyEvent.getStudyEventDefinitionId());

		studyEvent.setEventCRFs(eventCRFs);
		studyEvent.setStudyEventDefinition(sed);

		List<Object> fullCrfList = prepareFullCrfList(studyBean, ssub, studyEvent, eventCRFs, eventDefinitionCRFs);
		Map<Integer, String> notedMap = prepareNodeMapForFullCrfList(fullCrfList, ssub, sed.getName(),
				studyEvent.getId());

		boolean permitSign = SignUtil.permitSign(studyEvent, studyBean, daoWrapper);
		boolean permitSDV = SDVUtil.permitSDV(studyEvent, ssub.getStudyId(), daoWrapper,
				currentStudy.getStudyParameterConfig().getAllowSdvWithOpenQueries().equals("yes"), notedMap, ub.getId(),
				getMaskingService());

		List<SubjectEventStatus> statuses = SubjectEventStatusUtil.getAvailableStatusesForManualTransition(
				studyEvent.getSubjectEventStatus(), currentRole, permitSign, permitSDV);
		request.setAttribute("statuses", statuses);

		if (action.equalsIgnoreCase("submit")) {
			discNotes = (FormDiscrepancyNotes) session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
			DiscrepancyValidator v = new DiscrepancyValidator(new ValidatorHelper(request, getConfigurationDao()),
					discNotes);
			SubjectEventStatus ses = SubjectEventStatus.get(fp.getInt(SUBJECT_EVENT_STATUS_ID));

			if (ses.equals(SubjectEventStatus.NOT_SCHEDULED)) {
				forwardPage(Page.DELETE_STUDY_EVENT_SERVLET, request, response);
				return;
			}

			if (ses.equals(SubjectEventStatus.SKIPPED) || ses.equals(SubjectEventStatus.STOPPED)) {
				studyEvent.setStatus(Status.UNAVAILABLE);
			} else {
				studyEvent.setStatus(Status.AVAILABLE);
			}

			Date start = null;
			Date end = null;
			String strStart = fp.getDateTimeInputString(INPUT_STARTDATE_PREFIX);
			String strEnd = fp.getDateTimeInputString(INPUT_ENDDATE_PREFIX);

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
			HashMap errors = v.validate();

			if (!errors.containsKey(INPUT_STARTDATE_PREFIX)) {
				start = fp.getString(INPUT_STARTDATE_PREFIX + "Date").isEmpty()
						? studyEvent.getDateStarted()
						: fp.getDateTimeInput(INPUT_STARTDATE_PREFIX);
			}

			if (!strEnd.equals("") && !errors.containsKey(INPUT_STARTDATE_PREFIX)
					&& !errors.containsKey(INPUT_ENDDATE_PREFIX)) {
				end = fp.getDateTimeInput(INPUT_ENDDATE_PREFIX);
				if (!fp.getString(INPUT_STARTDATE_PREFIX + "Date")
						.equals(fp.getString(INPUT_ENDDATE_PREFIX + "Date"))) {
					if (end.before(start)) {
						Validator.addError(errors, INPUT_ENDDATE_PREFIX, getResException()
								.getString("input_provided_not_occure_after_previous_start_date_time"));
					}
				} else {
					// if in same date, only check when both had time entered
					if (fp.timeEntered(INPUT_STARTDATE_PREFIX) && fp.timeEntered(INPUT_ENDDATE_PREFIX)) {
						if (end.before(start) || end.equals(start)) {
							Validator.addError(errors, INPUT_ENDDATE_PREFIX, getResException()
									.getString("input_provided_not_occure_after_previous_start_date_time"));
						}
					}
				}
			}

			if (!errors.isEmpty()) {
				StudySubjectBean studySubjectBean = ssdao.findByPK(studyEvent.getStudySubjectId());
				List<DiscrepancyNoteBean> allNotesforSubjectAndEvent = DiscrepancyNoteUtil
						.getAllNotesforSubjectAndEvent(studySubjectBean, currentStudy, sm);
				setRequestAttributesForNotes(allNotesforSubjectAndEvent, studyEvent, request);
				setInputMessages(errors, request);
				String[] prefixes = {INPUT_STARTDATE_PREFIX, INPUT_ENDDATE_PREFIX};
				fp.setCurrentDateTimeValuesAsPreset(prefixes);
				setPresetValues(fp.getPresetValues(), request);

				studyEvent.setLocation(fp.getString(INPUT_LOCATION));

				request.setAttribute("changeDate", fp.getString("changeDate"));
				request.setAttribute(EVENT_BEAN, studyEvent);
				forwardPage(Page.UPDATE_STUDY_EVENT, request, response);

			} else if (ses.isSourceDataVerified()) {
				StringBuilder urlBuilder = new StringBuilder(request.getContextPath());
				urlBuilder.append("/pages/viewAllSubjectSDVtmp?sbb=true&studyId=").append(currentStudy.getId())
						.append("&imagePathPrefix=..%2F&crfId=0&redirection=viewAllSubjectSDVtmp&maxRows=15&showMoreLink=true&sdv_tr_=true&sdv_p_=1&sdv_mr_=15&sdv_f_studySubjectId=")
						.append(ssub.getLabel()).append("&sdv_f_eventName=").append(sed.getName());
				response.sendRedirect(urlBuilder.toString());
			} else if (ses.isSigned()) {
				studyEvent.setSubjectEventStatus(SubjectEventStatus.SIGNED);
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

				logger.info("Update study event");

				if (studyEvent.getSubjectEventStatus().isCompleted()) {
					StdScheduler scheduler = getStdScheduler();
					CalendarLogic calLogic = new CalendarLogic(getDataSource(), scheduler);
					calLogic.scheduleSubjectEvents(studyEvent);
					String message = calLogic.validateCalendaredVisitCompletionDate(studyEvent, new DateTime());
					if (!"empty".equalsIgnoreCase(message)) {
						addPageMessage(message, request);
					}
				}

				// save discrepancy notes into DB
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession()
						.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);

				dnService.saveFieldNotes(INPUT_LOCATION, fdn, studyEvent.getId(), "studyEvent", currentStudy);
				dnService.saveFieldNotes(INPUT_STARTDATE_PREFIX, fdn, studyEvent.getId(), "studyEvent", currentStudy);
				dnService.saveFieldNotes(INPUT_ENDDATE_PREFIX, fdn, studyEvent.getId(), "studyEvent", currentStudy);

				addPageMessage(getResPage().getString("study_event_updated"), request);
				request.setAttribute("id", Integer.toString(studySubjectId));
				request.getSession().removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);

				if (ses == SubjectEventStatus.REMOVED) {
					getStudyEventService().removeStudyEvent(studyEvent, ub);
				} else if (ses == SubjectEventStatus.LOCKED) {
					getStudyEventService().lockStudyEvent(studyEvent, ub);
				} else {
					if (studyEvent.getSubjectEventStatus().isLocked()) {
						getStudyEventService().unlockStudyEvent(studyEvent, ub);
					} else if (studyEvent.getSubjectEventStatus().isRemoved()) {
						getStudyEventService().restoreStudyEvent(studyEvent, ub);
					}
					if (ses != SubjectEventStatus.UNLOCK && ses != SubjectEventStatus.RESTORE) {
						studyEvent.setSubjectEventStatus(ses);
						sedao.update(studyEvent);
					}
				}

				redirectToStudySubjectView(request, response, studySubjectId);
			}
		} else if (action.equalsIgnoreCase("confirm")) {
			// confirming the signed status
			String username = request.getParameter("j_user");
			String password = request.getParameter("j_pass");
			SecurityManager securityManager = getSecurityManager();
			StudyEventBean seb = (StudyEventBean) request.getSession().getAttribute("eventSigned");
			if (securityManager.isPasswordValid(ub.getPasswd(), password, getUserDetails())
					&& ub.getName().equals(username)) {
				seb.setUpdater(ub);
				seb.setUpdatedDate(new Date());
				sedao.update(seb);

				// set Study Subject's status to available
				ssub.setStatus(Status.AVAILABLE);

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
				}
				ssub.setUpdater(ub);
				ssdao.update(ssub);

				// save discrepancy notes into DB
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession()
						.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);

				dnService.saveFieldNotes(INPUT_LOCATION, fdn, studyEvent.getId(), "studyEvent", currentStudy);
				dnService.saveFieldNotes(INPUT_STARTDATE_PREFIX, fdn, studyEvent.getId(), "studyEvent", currentStudy);
				dnService.saveFieldNotes(INPUT_ENDDATE_PREFIX, fdn, studyEvent.getId(), "studyEvent", currentStudy);

				session.removeAttribute("eventSigned");
				request.setAttribute("id", Integer.toString(studySubjectId));
				addPageMessage(getResPage().getString("study_event_updated"), request);
				redirectToStudySubjectView(request, response, studySubjectId);
			} else {
				request.setAttribute("studyEvent", session.getAttribute("eventSigned"));
				addPageMessage(getResText().getString("password_match"), request);
				forwardPage(Page.UPDATE_STUDY_EVENT_SIGNED, request, response);
			}
		} else {
			logger.info("No action, go to update page");
			StudySubjectBean studySubjectBean = ssdao.findByPK(studyEvent.getStudySubjectId());
			List<DiscrepancyNoteBean> allNotesforSubjectAndEvent = DiscrepancyNoteUtil
					.getAllNotesforSubjectAndEvent(studySubjectBean, currentStudy, sm);
			setRequestAttributesForNotes(allNotesforSubjectAndEvent, studyEvent, request);

			HashMap presetValues = new HashMap();
			DateTimeZone userTimeZone = DateTimeZone.forID(getUserAccountBean().getUserTimeZoneId());
			presetValues.put(INPUT_STARTDATE_PREFIX + "Hour", -1);
			presetValues.put(INPUT_STARTDATE_PREFIX + "Minute", -1);
			presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "");
			if (studyEvent.getDateStarted() != null) {
				if (studyEvent.getStartTimeFlag()) {
					Calendar c = new GregorianCalendar();
					c.setTime(studyEvent.getDateStarted());
					DateTime studyEventStartDate = new DateTime(studyEvent.getDateStarted()).withZone(userTimeZone);
					presetValues.put(INPUT_STARTDATE_PREFIX + "Hour", studyEventStartDate.getHourOfDay());
					presetValues.put(INPUT_STARTDATE_PREFIX + "Minute", studyEventStartDate.getMinuteOfHour());
					// Later it could be put to somewhere as a static method if
					// necessary.
					switch (c.get(Calendar.AM_PM)) {
						case 0 :
							presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "am");
							break;
						case 1 :
							presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "pm");
							break;
						default :
							presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "");
							break;
					}
				}
				presetValues.put(INPUT_STARTDATE_PREFIX + "Date", DateUtil.printDate(studyEvent.getDateStarted(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}

			presetValues.put(INPUT_ENDDATE_PREFIX + "Hour", -1);
			presetValues.put(INPUT_ENDDATE_PREFIX + "Minute", -1);
			presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "");
			if (studyEvent.getDateEnded() != null) {
				DateTime studyEventEndDate = new DateTime(studyEvent.getDateEnded()).withZone(userTimeZone);
				if (studyEvent.getEndTimeFlag()) {
					Calendar c = new GregorianCalendar();
					c.setTime(studyEvent.getDateEnded());
					presetValues.put(INPUT_ENDDATE_PREFIX + "Hour", studyEventEndDate.getHourOfDay());
					presetValues.put(INPUT_ENDDATE_PREFIX + "Minute", studyEventEndDate.getMinuteOfHour());
					// Later it could be put to somewhere as a static method if
					// necessary.
					switch (c.get(Calendar.AM_PM)) {
						case 0 :
							presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "am");
							break;
						case 1 :
							presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "pm");
							break;
						default :
							presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "");
							break;
					}
				}
				presetValues.put(INPUT_ENDDATE_PREFIX + "Date", DateUtil.printDate(studyEvent.getDateEnded(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}

			setPresetValues(presetValues, request);

			request.setAttribute("studyEvent", studyEvent);
			request.setAttribute("studySubject", studySubjectBean);

			discNotes = new FormDiscrepancyNotes();
			request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

			forwardPage(Page.UPDATE_STUDY_EVENT, request, response);
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
