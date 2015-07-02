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
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 */
package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.util.DateUtil;
import com.clinovo.util.ValidatorHelper;

/**
 * PageToCreateNewStudyEventServlet.
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class PageToCreateNewStudyEventServlet extends Controller {

	public static final String INPUT_PAGE_TO_SHOW_POPUP = "page";

	public static final String INPUT_STUDY_EVENT_DEFINITION = "studyEventDefinition";

	public static final String INPUT_STUDY_SUBJECT = "studySubject";

	public static final String INPUT_STUDY_SUBJECT_LABEL = "studySubjectLabel";

	public static final String INPUT_STUDY_SUBJECT_ID_FROM_VIEWSUBJECT = "studySubjectId";

	public static final String INPUT_STARTDATE_PREFIX = "start";

	public static final String INPUT_ENDDATE_PREFIX = "end";

	public static final String INPUT_REQUEST_STUDY_SUBJECT = "requestStudySubject";

	public static final String INPUT_LOCATION = "location";

	public static final String[] INPUT_STUDY_EVENT_DEFINITION_SCHEDULED = {"studyEventDefinitionScheduled0",
			"studyEventDefinitionScheduled1", "studyEventDefinitionScheduled2", "studyEventDefinitionScheduled3"};
	public static final String[] INPUT_SCHEDULED_LOCATION = {"locationScheduled0", "locationScheduled1",
			"locationScheduled2", "locationScheduled3"};
	public static final String[] INPUT_STARTDATE_PREFIX_SCHEDULED = {"startScheduled0", "startScheduled1",
			"startScheduled2", "startScheduled3"};
	public static final String[] INPUT_ENDDATE_PREFIX_SCHEDULED = {"endScheduled0", "endScheduled1", "endScheduled2",
			"endScheduled3"};
	public static final String[] DISPLAY_SCHEDULED = {"display0", "display1", "display2", "display3"};
	public static final int ADDITIONAL_SCHEDULED_NUM = 4;

	public static final int INT_2000 = 2000;

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);
		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.setStudyInfoShown(false);
		FormProcessor fp = new FormProcessor(request);
		FormDiscrepancyNotes discNotes;
		int studySubjectId = fp.getInt(INPUT_STUDY_SUBJECT_ID_FROM_VIEWSUBJECT);
		// input from manage subject matrix, user has specified definition id
		int studyEventDefinitionId = fp.getInt(INPUT_STUDY_EVENT_DEFINITION);

		String pageToShowPopup = fp.getString(INPUT_PAGE_TO_SHOW_POPUP);
		request.setAttribute(INPUT_PAGE_TO_SHOW_POPUP, pageToShowPopup);

		// make this sensitive to permissions
		StudySubjectDAO sdao = getStudySubjectDAO();
		StudySubjectBean ssb;
		if (studySubjectId <= 0) {
			ssb = (StudySubjectBean) request.getAttribute(INPUT_STUDY_SUBJECT);
		} else {
			ssb = (StudySubjectBean) sdao.findByPK(studySubjectId);
			Status s = ssb.getStatus();
			if ("removed".equalsIgnoreCase(s.getName()) || "auto-removed".equalsIgnoreCase(s.getName())) {
				addPageMessage(
						resword.getString("study_event") + resterm.getString("could_not_be")
								+ resterm.getString("added") + "."
								+ respage.getString("study_subject_has_been_deleted"), request);
				request.setAttribute("id", Integer.toString(studySubjectId));
				forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
			}
			request.setAttribute(INPUT_REQUEST_STUDY_SUBJECT, "no");
		}

		// make this sensitive to permissions
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());

		StudyBean studyWithEventDefinitions = currentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			studyWithEventDefinitions = new StudyBean();
			studyWithEventDefinitions.setId(currentStudy.getParentStudyId());
		}

		StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(getDataSource());
		StudyEventDAO sedao = new StudyEventDAO(getDataSource());
		ArrayList eventDefinitions = CreateNewStudyEventServlet.selectNotStartedOrRepeatingSortedEventDefs(ssb,
				studyWithEventDefinitions.getId(), seddao, sgcdao, sedao);

		if (!fp.isSubmitted()) {

			HashMap presetValues = new HashMap();

			presetValues.put(INPUT_STARTDATE_PREFIX + "Hour", -1);
			presetValues.put(INPUT_STARTDATE_PREFIX + "Minute", -1);
			presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "");
			presetValues.put(INPUT_ENDDATE_PREFIX + "Hour", -1);
			presetValues.put(INPUT_ENDDATE_PREFIX + "Minute", -1);
			presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "");
			for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {
				presetValues.put(INPUT_STARTDATE_PREFIX_SCHEDULED[i] + "Hour", -1);
				presetValues.put(INPUT_STARTDATE_PREFIX_SCHEDULED[i] + "Minute", -1);
				presetValues.put(INPUT_STARTDATE_PREFIX_SCHEDULED[i] + "Half", "");
				presetValues.put(INPUT_ENDDATE_PREFIX_SCHEDULED[i] + "Hour", -1);
				presetValues.put(INPUT_ENDDATE_PREFIX_SCHEDULED[i] + "Minute", -1);
				presetValues.put(INPUT_ENDDATE_PREFIX_SCHEDULED[i] + "Half", "");
			}

			// SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			// example of taking the above line and transferring to i18n on the
			// below line, tbh
			String dateValue = DateUtil.printDate(new Date(), getUserAccountBean().getUserTimeZoneId(),
					DateUtil.DatePattern.DATE, getLocale());
			presetValues.put(INPUT_STARTDATE_PREFIX + "Date", dateValue);
			for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {
				presetValues.put(INPUT_STARTDATE_PREFIX_SCHEDULED[i] + "Date", dateValue);
				// location
				presetValues.put(INPUT_SCHEDULED_LOCATION[i], currentStudy.getFacilityCity());
				presetValues.put(PageToCreateNewStudyEventServlet.DISPLAY_SCHEDULED[i], "none");
			}
			presetValues.put(INPUT_LOCATION, currentStudy.getFacilityCity()); // defualt

			if (ssb != null && ssb.isActive()) {
				presetValues.put(INPUT_STUDY_SUBJECT, ssb);

				String requestStudySubject = (String) request.getAttribute(INPUT_REQUEST_STUDY_SUBJECT);
				if (requestStudySubject != null) {
					presetValues.put(INPUT_REQUEST_STUDY_SUBJECT, requestStudySubject);

					dateValue = DateUtil.printDate(new Date(), getUserAccountBean().getUserTimeZoneId(),
							DateUtil.DatePattern.DATE, getLocale());
					presetValues.put(INPUT_STARTDATE_PREFIX + "Date", dateValue);
				}
			}

			if (studyEventDefinitionId > 0) {
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(studyEventDefinitionId);
				presetValues.put(INPUT_STUDY_EVENT_DEFINITION, sed);
			}

			logger.trace("set preset values: " + presetValues.toString());
			logger.info("found def.w.CRF list, size " + eventDefinitions.size());
			setPresetValues(presetValues, request);

			setupBeans(request, response, eventDefinitions);

			discNotes = new FormDiscrepancyNotes();
			request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

			request.setAttribute("eventDefinitionsScheduled", eventDefinitions);
			setInputMessages(new HashMap(), request);

			forwardPage(Page.PAGE_TO_CREATE_NEW_STUDY_EVENT, request, response);
		} else {

			String dateCheck2 = request.getParameter("startDate");
			String endCheck2 = request.getParameter("endDate");
			logger.info(dateCheck2 + "; " + endCheck2);

			String strEnd = fp.getDateTimeInputString(INPUT_ENDDATE_PREFIX);
			String[] strEndScheduled = new String[ADDITIONAL_SCHEDULED_NUM];
			for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {
				strEndScheduled[i] = fp.getDateTimeInputString(INPUT_ENDDATE_PREFIX_SCHEDULED[i]);
			}
			Date start = getInputStartDate(fp);
			Date end = null;
			Date[] startScheduled = new Date[ADDITIONAL_SCHEDULED_NUM];
			for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {
				startScheduled[i] = getInputStartDateScheduled(fp, i);
			}
			Date[] endScheduled = new Date[ADDITIONAL_SCHEDULED_NUM];

			discNotes = (FormDiscrepancyNotes) request.getSession().getAttribute(
					AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
			if (discNotes == null) {
				discNotes = new FormDiscrepancyNotes();
				request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);
			}
			DiscrepancyValidator v = new DiscrepancyValidator(new ValidatorHelper(request, getConfigurationDao()),
					discNotes);

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

			v.addValidation(INPUT_STUDY_EVENT_DEFINITION, Validator.ENTITY_EXISTS_IN_STUDY, seddao,
					studyWithEventDefinitions);
			v.addValidation(INPUT_STUDY_SUBJECT_LABEL, Validator.NO_BLANKS);
			v.addValidation(INPUT_LOCATION, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_2000);
			if (currentStudy.getStudyParameterConfig().getEventLocationRequired().equalsIgnoreCase("required")) {
				v.addValidation(INPUT_LOCATION, Validator.NO_BLANKS);
			}

			v.alwaysExecuteLastValidation(INPUT_LOCATION);

			boolean hasScheduledEvent = false;
			for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {
				if (!StringUtil.isBlank(fp
						.getString(PageToCreateNewStudyEventServlet.INPUT_STUDY_EVENT_DEFINITION_SCHEDULED[i]))) {
					v.addValidation(PageToCreateNewStudyEventServlet.INPUT_STUDY_EVENT_DEFINITION_SCHEDULED[i],
							Validator.ENTITY_EXISTS_IN_STUDY, seddao, studyWithEventDefinitions);
					if (currentStudy.getStudyParameterConfig().getEventLocationRequired().equalsIgnoreCase("required")) {
						v.addValidation(INPUT_SCHEDULED_LOCATION[i], Validator.NO_BLANKS);
						v.addValidation(INPUT_SCHEDULED_LOCATION[i], Validator.LENGTH_NUMERIC_COMPARISON,
								NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_2000);
						v.alwaysExecuteLastValidation(INPUT_SCHEDULED_LOCATION[i]);
					}
					v.addValidation(INPUT_STARTDATE_PREFIX_SCHEDULED[i], Validator.IS_DATE_TIME);
					v.alwaysExecuteLastValidation(INPUT_STARTDATE_PREFIX_SCHEDULED[i]);
					if (!strEndScheduled[i].equals("")) {
						v.addValidation(INPUT_ENDDATE_PREFIX_SCHEDULED[i], Validator.IS_DATE_TIME);
						v.alwaysExecuteLastValidation(INPUT_ENDDATE_PREFIX_SCHEDULED[i]);
					}
					hasScheduledEvent = true;
					fp.addPresetValue(DISPLAY_SCHEDULED[i], "all");
				} else {
					fp.addPresetValue(DISPLAY_SCHEDULED[i], "none");
				}
			}

			HashMap errors = v.validate();
			String location = resword.getString("location");
			// don't allow user to use the default value 'Location' since
			// location
			// is a required field
			if (!StringUtil.isBlank(fp.getString(INPUT_LOCATION))
					&& fp.getString(INPUT_LOCATION).equalsIgnoreCase(location)) {
				Validator.addError(errors, INPUT_LOCATION, restext.getString("not_a_valid_location"));
			}

			StudyEventDefinitionBean definition = (StudyEventDefinitionBean) seddao.findByPK(fp
					.getInt(INPUT_STUDY_EVENT_DEFINITION));

			StudySubjectBean studySubject = sdao.findByLabelAndStudy(fp.getString(INPUT_STUDY_SUBJECT_LABEL),
					currentStudy);
			if (request.getAttribute(INPUT_STUDY_SUBJECT) != null) {
				studySubject = (StudySubjectBean) request.getAttribute(INPUT_STUDY_SUBJECT);
			}
			if (studySubject.getLabel().equals("")) {
				// add an error here, tbh
				System.out.println("tripped the error here 20091109");
				Validator.addError(errors, INPUT_STUDY_SUBJECT,
						respage.getString("must_enter_subject_ID_for_identifying"));
			}

			if (!subjectMayReceiveStudyEvent(sedao, definition, studySubject)) {
				Validator.addError(errors, INPUT_STUDY_EVENT_DEFINITION,
						restext.getString("not_added_since_event_not_repeating"));
			}

			ArrayList<StudyEventDefinitionBean> definitionScheduleds = new ArrayList<StudyEventDefinitionBean>();
			int[] scheduledDefinitionIds = new int[ADDITIONAL_SCHEDULED_NUM];
			if (hasScheduledEvent) {
				for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {
					int pk = fp.getInt(INPUT_STUDY_EVENT_DEFINITION_SCHEDULED[i]);
					if (pk > 0) {
						StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(pk);
						System.out.println("scheduled def:" + pk + " " + INPUT_STUDY_EVENT_DEFINITION_SCHEDULED[i]
								+ " " + sedb.getName());
						definitionScheduleds.add(sedb);
						scheduledDefinitionIds[i] = pk;
						if (!subjectMayReceiveStudyEvent(sedao, sedb, studySubject)) {
							Validator.addError(errors, INPUT_STUDY_EVENT_DEFINITION_SCHEDULED[i],
									restext.getString("not_added_since_event_not_repeating"));
						}
					} else {
						definitionScheduleds.add(new StudyEventDefinitionBean());
					}
				}
			}

			if (!"".equals(strEnd) && !errors.containsKey(INPUT_STARTDATE_PREFIX)
					&& !errors.containsKey(INPUT_ENDDATE_PREFIX)) {
				end = getInputEndDate(fp);
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

			String prevStartPrefix;
			HashMap<Integer, Integer> scheduledSeds = new HashMap<Integer, Integer>();
			scheduledSeds.put(studyEventDefinitionId, -1);
			for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {
				if (scheduledDefinitionIds[i] > 0 && !errors.containsKey(INPUT_STARTDATE_PREFIX_SCHEDULED[i])
						&& !errors.containsKey(INPUT_ENDDATE_PREFIX_SCHEDULED[i])) {
					if (scheduledSeds.containsKey(scheduledDefinitionIds[i])) {
						int prevStart = scheduledSeds.get(scheduledDefinitionIds[i]);
						prevStartPrefix = prevStart == -1
								? INPUT_STARTDATE_PREFIX
								: INPUT_STARTDATE_PREFIX_SCHEDULED[prevStart];
						Date prevStartDate = prevStart == -1 ? this.getInputStartDate(fp) : this
								.getInputStartDateScheduled(fp,
										Integer.parseInt(prevStartPrefix.charAt(prevStartPrefix.length() - 1) + ""));
						if (fp.getString(INPUT_STARTDATE_PREFIX_SCHEDULED[i] + "Date").equals(
								fp.getString(prevStartPrefix + "Date"))) {
							// if in same day, only check when both have time
							// inputs.
							boolean schStartTime = fp.timeEntered(INPUT_STARTDATE_PREFIX_SCHEDULED[i]);
							boolean startTime = fp.timeEntered(prevStartPrefix);
							if (schStartTime && startTime) {
								if (startScheduled[i].before(prevStartDate)) {
									Validator.addError(errors, INPUT_STARTDATE_PREFIX_SCHEDULED[i], resexception
											.getString("input_provided_not_occure_after_previous_start_date_time"));
								}
							}
						} else {
							if (startScheduled[i].before(prevStartDate)) {
								Validator.addError(errors, INPUT_STARTDATE_PREFIX_SCHEDULED[i], resexception
										.getString("input_provided_not_occure_after_previous_start_date_time"));
							}
						}
					}
					scheduledSeds.put(scheduledDefinitionIds[i], i);
					if (!strEndScheduled[i].equals("")) {
						endScheduled[i] = fp.getDateTimeInput(INPUT_ENDDATE_PREFIX_SCHEDULED[i]);
						String prevEndPrefix = i > 0 ? INPUT_ENDDATE_PREFIX_SCHEDULED[i - 1] : INPUT_ENDDATE_PREFIX;
						if (!fp.getString(INPUT_STARTDATE_PREFIX_SCHEDULED[i] + "Date").equals(
								fp.getString(prevEndPrefix + "Date"))) {
							if (endScheduled[i].before(startScheduled[i])) {
								Validator.addError(errors, INPUT_ENDDATE_PREFIX_SCHEDULED[i], resexception
										.getString("input_provided_not_occure_after_previous_start_date_time"));
							}
						} else {
							// if in same date, only check when both had time
							// entered
							if (fp.timeEntered(INPUT_STARTDATE_PREFIX_SCHEDULED[i])
									&& fp.timeEntered(INPUT_ENDDATE_PREFIX_SCHEDULED[i])) {
								if (endScheduled[i].before(startScheduled[i])
										|| endScheduled[i].equals(startScheduled[i])) {
									Validator.addError(errors, INPUT_ENDDATE_PREFIX_SCHEDULED[i], resexception
											.getString("input_provided_not_occure_after_previous_start_date_time"));
								}
							}
						}
					}
				}
			}
			// YW >>
			System.out.println("we have errors; number of this; " + errors.size());
			if (!errors.isEmpty()) {
				logger.info("we have errors; number of this; " + errors.size());
				System.out.println("found request study subject: " + fp.getString(INPUT_REQUEST_STUDY_SUBJECT));
				addPageMessage(respage.getString("errors_in_submission_see_below"), request);
				setInputMessages(errors, request);

				fp.addPresetValue(INPUT_STUDY_EVENT_DEFINITION, definition);
				fp.addPresetValue(INPUT_STUDY_SUBJECT, studySubject);
				fp.addPresetValue(INPUT_STUDY_SUBJECT_LABEL, fp.getString(INPUT_STUDY_SUBJECT_LABEL));
				fp.addPresetValue(INPUT_REQUEST_STUDY_SUBJECT, fp.getString(INPUT_REQUEST_STUDY_SUBJECT));
				fp.addPresetValue(INPUT_LOCATION, fp.getString(INPUT_LOCATION));

				for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {
					fp.addPresetValue(INPUT_SCHEDULED_LOCATION[i], fp.getString(INPUT_SCHEDULED_LOCATION[i]));
				}
				String[] prefixes = new String[2 + 2 * ADDITIONAL_SCHEDULED_NUM];
				prefixes[0] = INPUT_STARTDATE_PREFIX;
				prefixes[1] = INPUT_ENDDATE_PREFIX;
				int b = ADDITIONAL_SCHEDULED_NUM + 2;
				System.arraycopy(INPUT_STARTDATE_PREFIX_SCHEDULED, 0, prefixes, 2, b - 2);
				System.arraycopy(INPUT_ENDDATE_PREFIX_SCHEDULED, 0, prefixes, b, ADDITIONAL_SCHEDULED_NUM + b - b);
				fp.setCurrentDateTimeValuesAsPreset(prefixes);

				if (hasScheduledEvent) {
					for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {
						fp.addPresetValue(INPUT_STUDY_EVENT_DEFINITION_SCHEDULED[i], definitionScheduleds.get(i));
					}
				}

				setPresetValues(fp.getPresetValues(), request);
				setupBeans(request, response, eventDefinitions);
				request.setAttribute("eventDefinitionsScheduled", eventDefinitions);
				forwardPage(Page.PAGE_TO_CREATE_NEW_STUDY_EVENT, request, response);
			} else {

				StudyEventBean studyEvent = new StudyEventBean();
				studyEvent.setStudyEventDefinitionId(definition.getId());
				studyEvent.setStudySubjectId(studySubject.getId());

				if ("-1".equals(getInputStartHour(fp)) && "-1".equals(getInputStartMinute(fp))
						&& "".equals(getInputStartHalf(fp))) {
					studyEvent.setStartTimeFlag(false);
				} else {
					studyEvent.setStartTimeFlag(true);
				}
				studyEvent.setDateStarted(start);
				if (!"".equals(strEnd)) {
					if ("-1".equals(getInputEndHour(fp)) && "-1".equals(getInputEndMinute(fp))
							&& "".equals(getInputEndHalf(fp))) {
						studyEvent.setEndTimeFlag(false);
					} else {
						studyEvent.setEndTimeFlag(true);
					}
					studyEvent.setDateEnded(end);
				}
				studyEvent.setOwner(ub);
				studyEvent.setStatus(Status.AVAILABLE);
				studyEvent.setLocation(fp.getString(INPUT_LOCATION));
				studyEvent.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);

				studyEvent.setSampleOrdinal(sedao.getMaxSampleOrdinal(definition, studySubject) + 1);

				studyEvent = (StudyEventBean) sedao.create(studyEvent);

				if (!studyEvent.isActive()) {
					throw new OpenClinicaException(restext.getString("event_not_created_in_database"), "2");
				}
				addPageMessage(
						restext.getString("X_event_wiht_definition") + definition.getName()
								+ restext.getString("X_and_subject") + studySubject.getName()
								+ respage.getString("X_was_created_succesfully"), request);

				// save discrepancy notes into DB
				DiscrepancyNoteService dnService = new DiscrepancyNoteService(getDataSource());
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession().getAttribute(
						AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);

				String[] eventFields = {INPUT_LOCATION, INPUT_STARTDATE_PREFIX, INPUT_ENDDATE_PREFIX};
				for (String element : eventFields) {
					dnService.saveFieldNotes(element, fdn, studyEvent.getId(), "studyEvent", currentStudy);
				}
				if (hasScheduledEvent) {
					for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {

						// should only do the following process if user inputs a
						// scheduled event,
						// which is scheduledDefinitionIds[i] > 0
						if (scheduledDefinitionIds[i] > 0) {
							if (subjectMayReceiveStudyEvent(sedao, definitionScheduleds.get(i), studySubject)) {

								StudyEventBean studyEventScheduled = new StudyEventBean();
								studyEventScheduled.setStudyEventDefinitionId(scheduledDefinitionIds[i]);
								studyEventScheduled.setStudySubjectId(studySubject.getId());

								if ("-1".equals(fp.getString(INPUT_STARTDATE_PREFIX_SCHEDULED[i] + "Hour"))
										&& "-1".equals(fp.getString(INPUT_STARTDATE_PREFIX_SCHEDULED[i] + "Minute"))
										&& "".equals(fp.getString(INPUT_STARTDATE_PREFIX_SCHEDULED[i] + "Half"))) {
									studyEventScheduled.setStartTimeFlag(false);
								} else {
									studyEventScheduled.setStartTimeFlag(true);
								}

								studyEventScheduled.setDateStarted(startScheduled[i]);
								if (!"".equals(strEndScheduled[i])) {
									endScheduled[i] = fp.getDateTimeInput(INPUT_ENDDATE_PREFIX_SCHEDULED[i]);
									if ("-1".equals(fp.getString(INPUT_ENDDATE_PREFIX_SCHEDULED[i] + "Hour"))
											&& "-1".equals(fp.getString(INPUT_ENDDATE_PREFIX_SCHEDULED[i] + "Minute"))
											&& "".equals(fp.getString(INPUT_ENDDATE_PREFIX_SCHEDULED[i] + "Half"))) {
										studyEventScheduled.setEndTimeFlag(false);
									} else {
										studyEventScheduled.setEndTimeFlag(true);
									}
								}
								studyEventScheduled.setDateEnded(endScheduled[i]);
								studyEventScheduled.setOwner(ub);
								studyEventScheduled.setStatus(Status.AVAILABLE);
								studyEventScheduled.setLocation(fp.getString(INPUT_SCHEDULED_LOCATION[i]));
								studyEvent.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);

								studyEventScheduled.setSampleOrdinal(sedao.getMaxSampleOrdinal(
										definitionScheduleds.get(i), studySubject) + 1);
								studyEventScheduled = (StudyEventBean) sedao.create(studyEventScheduled);
								if (!studyEventScheduled.isActive()) {
									throw new OpenClinicaException(
											restext.getString("scheduled_event_not_created_in_database"), "2");
								}

								dnService.saveFieldNotes(INPUT_SCHEDULED_LOCATION[i], fdn, studyEventScheduled.getId(),
										"studyEvent", currentStudy);
								dnService.saveFieldNotes(INPUT_STARTDATE_PREFIX_SCHEDULED[i], fdn,
										studyEventScheduled.getId(), "studyEvent", currentStudy);
								dnService.saveFieldNotes(INPUT_ENDDATE_PREFIX_SCHEDULED[i], fdn,
										studyEventScheduled.getId(), "studyEvent", currentStudy);
							} else {
								addPageMessage(
										restext.getString("scheduled_event_definition")
												+ definitionScheduleds.get(i).getName()
												+ restext.getString("X_and_subject") + studySubject.getName()
												+ restext.getString("not_created_since_event_not_repeating")
												+ restext.getString("event_type_already_exists"), request);
							}
						}
					}

				} // if

				request.getSession().removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
				request.setAttribute(EnterDataForStudyEventServlet.INPUT_EVENT_ID, String.valueOf(studyEvent.getId()));
				response.encodeRedirectURL("EnterDataForStudyEvent?eventId=" + studyEvent.getId());
				forwardPage(Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET, request, response);
			}
		}
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		String exceptionName = resexception.getString("no_permission_to_add_new_study_event");
		String noAccessMessage = respage.getString("not_create_new_event") + " "
				+ respage.getString("change_study_contact_sysadmin");

		if (maySubmitData(ub, currentRole)) {
			return;
		}

		addPageMessage(noAccessMessage, request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, exceptionName, "1");
	}

	/**
	 * Determines whether a subject may receive an additional study event. This is true if:
	 * <ul>
	 * <li>The study event definition is repeating; or
	 * <li>The subject does not yet have a study event for the given study event definition
	 * </ul>
	 * 
	 * @param sedao
	 *            StudyEventDAO
	 * @param studyEventDefinition
	 *            The definition of the study event which is to be added for the subject.
	 * @param studySubject
	 *            The subject for which the study event is to be added.
	 * @return <code>true</code> if the subject may receive an additional study event, <code>false</code> otherwise.
	 */
	public static boolean subjectMayReceiveStudyEvent(StudyEventDAO sedao,
			StudyEventDefinitionBean studyEventDefinition, StudySubjectBean studySubject) {

		if (studyEventDefinition.isRepeating()) {
			return true;
		}

		ArrayList allEvents = sedao.findAllByDefinitionAndSubject(studyEventDefinition, studySubject);

		return allEvents.size() <= 0;

	}

	private void setupBeans(HttpServletRequest request, HttpServletResponse response, ArrayList eventDefinitions)
			throws Exception {
		addEntityList("eventDefinitions", eventDefinitions,
				restext.getString("cannot_create_event_because_no_event_definitions"),
				Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);

	}

	private Date getInputStartDate(FormProcessor fp) {
		return fp.getDateTimeInput(INPUT_STARTDATE_PREFIX);
	}

	private Date getInputStartDateScheduled(FormProcessor fp, int i)  {
		return fp.getDateTimeInput(INPUT_STARTDATE_PREFIX_SCHEDULED[i]);
	}

	private Date getInputEndDate(FormProcessor fp) {
		return fp.getDateTimeInput(INPUT_ENDDATE_PREFIX);
	}

	private String getInputStartHour(FormProcessor fp) {
		return fp.getString(INPUT_STARTDATE_PREFIX + "Hour");
	}

	private String getInputStartMinute(FormProcessor fp) {
		return fp.getString(INPUT_STARTDATE_PREFIX + "Minute");
	}

	private String getInputStartHalf(FormProcessor fp) {
		return fp.getString(INPUT_STARTDATE_PREFIX + "Half");
	}

	private String getInputEndHour(FormProcessor fp) {
		return fp.getString(INPUT_ENDDATE_PREFIX + "Hour");
	}

	private String getInputEndMinute(FormProcessor fp) {
		return fp.getString(INPUT_ENDDATE_PREFIX + "Minute");
	}

	private String getInputEndHalf(FormProcessor fp) {
		return fp.getString(INPUT_ENDDATE_PREFIX + "Half");
	}
}
