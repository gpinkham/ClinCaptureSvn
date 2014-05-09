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

import com.clinovo.util.ValidatorHelper;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.control.managestudy.ListEventsForSubjectTableFactory;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.util.SubjectLabelNormalizer;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
@Component
public class CreateNewStudyEventServlet extends Controller {

	public static final String OPEN_FIRST_CRF = "openFirstCrf";

	public static final String INPUT_PAGE_TO_SHOW_POPUP = "page";

	public static final String INPUT_STUDY_EVENT_DEFINITION = "studyEventDefinition";

	public static final String INPUT_SELECTED_EVENT_DEF_ID = "selectedEventDefId";

	public static final String INPUT_EVENT_CRF_ID = "eventCRFId";

	public static final String INPUT_EVENT_DEFINITION_CRF_ID = "eventDefintionCRFId";

	public static final String INPUT_STUDY_SUBJECT = "studySubject";

	public static final String INPUT_STUDY_SUBJECT_LABEL = "studySubjectLabel";

	public static final String INPUT_STUDY_SUBJECT_ID_FROM_VIEWSUBJECT = "studySubjectId";

	public static final String INPUT_STARTDATE_PREFIX = "start";

	public static final String INPUT_ENDDATE_PREFIX = "end";

	public static final String INPUT_REQUEST_STUDY_SUBJECT = "requestStudySubject";

	public static final String INPUT_LOCATION = "location";

	public final static String[] INPUT_STUDY_EVENT_DEFINITION_SCHEDULED = { "studyEventDefinitionScheduled0",
			"studyEventDefinitionScheduled1", "studyEventDefinitionScheduled2", "studyEventDefinitionScheduled3" };
	public final static String[] INPUT_SCHEDULED_LOCATION = { "locationScheduled0", "locationScheduled1",
			"locationScheduled2", "locationScheduled3" };
	public final static String[] INPUT_STARTDATE_PREFIX_SCHEDULED = { "startScheduled0", "startScheduled1",
			"startScheduled2", "startScheduled3" };
	public final static String[] INPUT_ENDDATE_PREFIX_SCHEDULED = { "endScheduled0", "endScheduled1", "endScheduled2",
			"endScheduled3" };
	public final static String[] DISPLAY_SCHEDULED = { "display0", "display1", "display2", "display3" };
	public final static int ADDITIONAL_SCHEDULED_NUM = 4;

	private void processEvents(JSONArray eventDefs, String eventDivId, int studyEventId,
			StudyEventDefinitionBean definition, StudySubjectBean studySubject, StudyEventDAO sed,
			String pageToShowPopup, int selectedEventDefId) throws Exception {

		String eventCRFDivId;
		JSONObject jsonObject = null;
		JSONArray eventIds = null;
		for (int i = 0; i < eventDefs.length(); i++) {
			if (eventDefs.getJSONObject(i).get("eventDivId").toString().equalsIgnoreCase(eventDivId)) {
				jsonObject = eventDefs.getJSONObject(i);
				eventIds = jsonObject.getJSONArray("eventIds");
				break;
			}
		}

		if (jsonObject == null) {

			if (pageToShowPopup.equalsIgnoreCase(Page.LIST_EVENTS_FOR_SUBJECTS_SERVLET.getFileName())) {

				if (selectedEventDefId == definition.getId()) {

					eventIds = new JSONArray();
					eventIds.put(studyEventId);
					jsonObject = new JSONObject();
					jsonObject.put("eventDivId", eventDivId);
					jsonObject.put("eventIds", eventIds);
					jsonObject.put("totalEvents", sed.findAllByDefinitionAndSubject(definition, studySubject).size());
					jsonObject.put("repeatingEvent", definition.isRepeating());
					jsonObject.put("popupToDisplayEntireEvent", true);
					eventDefs.put(jsonObject);

					for (EventDefinitionCRFBean eventDefBean : (ArrayList<EventDefinitionCRFBean>) getEventDefinitionCRFDAO()
							.findAllActiveByEventDefinitionId(definition.getId())) {

						eventCRFDivId = "Event_"
								+ SubjectLabelNormalizer.normalizeSubjectLabel(studySubject.getLabel()) + "_"
								+ eventDefBean.getId() + "_";
						eventIds = new JSONArray();
						eventIds.put(studyEventId);
						jsonObject = new JSONObject();
						jsonObject.put("eventDivId", eventCRFDivId);
						jsonObject.put("eventIds", eventIds);
						jsonObject.put("totalEvents", sed.findAllByDefinitionAndSubject(definition, studySubject)
								.size());
						jsonObject.put("repeatingEvent", definition.isRepeating());
						jsonObject.put("popupToDisplayEntireEvent", false);
						eventDefs.put(jsonObject);

					}

				}

			} else {
				eventIds = new JSONArray();
				eventIds.put(studyEventId);
				jsonObject = new JSONObject();
				jsonObject.put("eventDivId", eventDivId);
				jsonObject.put("eventIds", eventIds);
				jsonObject.put("totalEvents", sed.findAllByDefinitionAndSubject(definition, studySubject).size());
				jsonObject.put("repeatingEvent", definition.isRepeating());
				jsonObject.put("popupToDisplayEntireEvent", true);
				eventDefs.put(jsonObject);
			}

		} else {
			eventIds.put(studyEventId);
			jsonObject.put("totalEvents", jsonObject.getInt("totalEvents") + 1);
		}
	}

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

		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		String pageToShowPopup = fp.getString(INPUT_PAGE_TO_SHOW_POPUP);
		int selectedEventDefId = fp.getInt(INPUT_SELECTED_EVENT_DEF_ID);
		String eventCRFId = fp.getString(INPUT_EVENT_CRF_ID);
		String eventDefintionCRFId = fp.getString(INPUT_EVENT_DEFINITION_CRF_ID);

		String popupSubjectLabel = "";
		JSONArray eventDefs = new JSONArray();
		JSONArray pageMessages = new JSONArray();
		String popupQueryStr = request.getParameter("popupQuery");
		boolean popupQuery = popupQueryStr != null;
		if (popupQuery) {
			studySubjectId = Integer.parseInt(request.getParameter("popupQueryStudySubjectId"));
			StudySubjectBean studySubject = (StudySubjectBean) getStudySubjectDAO().findByPK(studySubjectId);
			popupSubjectLabel = studySubject.getLabel();
		}

		String returnScheduledEvenContent = request.getParameter("returnScheduledEvenContent");
		if (returnScheduledEvenContent != null) {

			String eventDiv = getEventDivForScheduledEvent(request, pageToShowPopup, returnScheduledEvenContent,
					selectedEventDefId, eventCRFId, eventDefintionCRFId);
			response.setContentType("text/html");
			response.getWriter().write(eventDiv);
			getServletContext().getRequestDispatcher("/WEB-INF/jsp/include/changeTheme.jsp").include(request, response);
			return;

		}

		// TODO: make this sensitive to permissions
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

		// TODO: make this sensitive to permissions
		StudyBean studyWithEventDefinitions = currentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			studyWithEventDefinitions = new StudyBean();
			studyWithEventDefinitions.setId(currentStudy.getParentStudyId());
		}

		// find all active definitions with CRFs
		ArrayList eventDefinitions;
		ArrayList eventDefinitionsScheduled;
		if (ssb == null) {
			eventDefinitions = seddao.findAllActiveByStudy(studyWithEventDefinitions);
			Collections.sort(eventDefinitions);
		} else {
			StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
			StudyEventDAO sedao = getStudyEventDAO();
			eventDefinitions = selectNotStartedOrRepeatingSortedEventDefs(ssb, studyWithEventDefinitions.getId(),
					seddao, sgcdao, sedao);
		}
		eventDefinitionsScheduled = eventDefinitions;
		SimpleDateFormat local_df = getLocalDf(request);
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
			String dateValue = local_df.format(new Date(System.currentTimeMillis()));
			presetValues.put(INPUT_STARTDATE_PREFIX + "Date", dateValue);
			for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {
				presetValues.put(INPUT_STARTDATE_PREFIX_SCHEDULED[i] + "Date", dateValue);
				// location
				presetValues.put(INPUT_SCHEDULED_LOCATION[i], currentStudy.getFacilityCity());
				presetValues.put(CreateNewStudyEventServlet.DISPLAY_SCHEDULED[i], "none");
			}
			presetValues.put(INPUT_LOCATION, currentStudy.getFacilityCity());// defualt

			if (ssb != null && ssb.isActive()) {
				presetValues.put(INPUT_STUDY_SUBJECT, ssb);
				String requestStudySubject = (String) request.getAttribute(INPUT_REQUEST_STUDY_SUBJECT);
				if (requestStudySubject != null) {
					presetValues.put(INPUT_REQUEST_STUDY_SUBJECT, requestStudySubject);
					dateValue = local_df.format(new Date());
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

			request.setAttribute("eventDefinitionsScheduled", eventDefinitionsScheduled);
			setInputMessages(new HashMap(), request);
			forwardPage(Page.CREATE_NEW_STUDY_EVENT, request, response);
		} else {

			String dateCheck2 = request.getParameter("startDate");
			String endCheck2 = request.getParameter("endDate");
			logger.info(dateCheck2 + "; " + endCheck2);

			String strEnd = fp.getDateTimeInputString(INPUT_ENDDATE_PREFIX);
			String strEndScheduled[] = new String[ADDITIONAL_SCHEDULED_NUM];
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
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
			if (currentStudy.getStudyParameterConfig().getEventLocationRequired().equalsIgnoreCase("required")) {
				v.addValidation(INPUT_LOCATION, Validator.NO_BLANKS);
			}

			v.alwaysExecuteLastValidation(INPUT_LOCATION);

			boolean hasScheduledEvent = false;
			for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {
				if (!StringUtil.isBlank(fp
						.getString(CreateNewStudyEventServlet.INPUT_STUDY_EVENT_DEFINITION_SCHEDULED[i]))) {

					v.addValidation(CreateNewStudyEventServlet.INPUT_STUDY_EVENT_DEFINITION_SCHEDULED[i],
							Validator.ENTITY_EXISTS_IN_STUDY, seddao, studyWithEventDefinitions);
					if (currentStudy.getStudyParameterConfig().getEventLocationRequired().equalsIgnoreCase("required")) {
						v.addValidation(INPUT_SCHEDULED_LOCATION[i], Validator.NO_BLANKS);
						v.addValidation(INPUT_SCHEDULED_LOCATION[i], Validator.LENGTH_NUMERIC_COMPARISON,
								NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
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
			// what if we are sent here from AddNewSubjectServlet.java??? we need to get that study subject bean
			if (request.getAttribute(INPUT_STUDY_SUBJECT) != null) {
				studySubject = (StudySubjectBean) request.getAttribute(INPUT_STUDY_SUBJECT);
			}
			if (studySubject.getLabel().equals("")) {
				Validator.addError(errors, INPUT_STUDY_SUBJECT,
						respage.getString("must_enter_subject_ID_for_identifying"));
			}

			if (!subjectMayReceiveStudyEvent(getStudyEventDAO(), definition, studySubject)) {
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
						definitionScheduleds.add(sedb);
						scheduledDefinitionIds[i] = pk;
						if (!subjectMayReceiveStudyEvent(getStudyEventDAO(), sedb, studySubject)) {
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
						prevStartPrefix = prevStart == -1 ? INPUT_STARTDATE_PREFIX
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
						endScheduled[i] = fp.getDateTime(INPUT_ENDDATE_PREFIX_SCHEDULED[i]);
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

			if (!errors.isEmpty()) {
				logger.info("we have errors; number of this; " + errors.size());
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
				String prefixes[] = new String[2 + 2 * ADDITIONAL_SCHEDULED_NUM];
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
				request.setAttribute("eventDefinitionsScheduled", eventDefinitionsScheduled);

				if (popupQuery) {
					response.setContentType("application/json");
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("errors", true);
					response.getWriter().write(jsonObject.toString());
				} else {
					forwardPage(Page.CREATE_NEW_STUDY_EVENT, request, response);
				}
			} else {

				StudyEventDAO sed = getStudyEventDAO();
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
				// comment to find bug 1389, tbh
				logger.info("found start date: " + local_df.format(start));
				Date[] startScheduled2 = new Date[ADDITIONAL_SCHEDULED_NUM];
				for (int i = 0; i < startScheduled2.length; ++i) {
					startScheduled2[i] = getInputStartDateScheduled(fp, i);
				}
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
				studyEvent.setSampleOrdinal(sed.getMaxSampleOrdinal(definition, studySubject) + 1);
				studyEvent = (StudyEventBean) sed.create(studyEvent);

				if (!studyEvent.isActive()) {
					throw new OpenClinicaException(restext.getString("event_not_created_in_database"), "2");
				}
				addPageMessage(
						restext.getString("X_event_wiht_definition") + definition.getName()
								+ restext.getString("X_and_subject") + studySubject.getName()
								+ respage.getString("X_was_created_succesfully"), request);

				// save discrepancy notes into DB
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession().getAttribute(
						AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
				DiscrepancyNoteDAO dndao = getDiscrepancyNoteDAO();
				String[] eventFields = { INPUT_LOCATION, INPUT_STARTDATE_PREFIX, INPUT_ENDDATE_PREFIX };
				for (String element : eventFields) {
					AddNewSubjectServlet.saveFieldNotes(element, fdn, dndao, studyEvent.getId(), "studyEvent",
							currentStudy);
				}

				if (popupQuery) {
					String eventDivId = "Event_"
							+ SubjectLabelNormalizer.normalizeSubjectLabel(popupSubjectLabel)
							+ "_"
							+ definition.getId()
							+ (pageToShowPopup.equalsIgnoreCase(Page.LIST_EVENTS_FOR_SUBJECTS_SERVLET.getFileName()) ? "ev"
									: "") + "_";
					processEvents(eventDefs, eventDivId, studyEvent.getId(), definition, studySubject, sed,
							pageToShowPopup, selectedEventDefId);
				}

				if (hasScheduledEvent) {
					for (int i = 0; i < ADDITIONAL_SCHEDULED_NUM; ++i) {

						// should only do the following process if user inputs a
						// scheduled event,
						// which is scheduledDefinitionIds[i] > 0
						if (scheduledDefinitionIds[i] > 0) {
							if (subjectMayReceiveStudyEvent(sed, definitionScheduleds.get(i), studySubject)) {

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
									endScheduled[i] = fp.getDateTime(INPUT_ENDDATE_PREFIX_SCHEDULED[i]);
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
								studyEventScheduled.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);

								studyEventScheduled.setSampleOrdinal(sed.getMaxSampleOrdinal(
										definitionScheduleds.get(i), studySubject) + 1);
								studyEventScheduled = (StudyEventBean) sed.create(studyEventScheduled);
								if (!studyEventScheduled.isActive()) {
									throw new OpenClinicaException(
											restext.getString("scheduled_event_not_created_in_database"), "2");
								}

								AddNewSubjectServlet.saveFieldNotes(INPUT_SCHEDULED_LOCATION[i], fdn, dndao,
										studyEventScheduled.getId(), "studyEvent", currentStudy);
								AddNewSubjectServlet.saveFieldNotes(INPUT_STARTDATE_PREFIX_SCHEDULED[i], fdn, dndao,
										studyEventScheduled.getId(), "studyEvent", currentStudy);
								AddNewSubjectServlet.saveFieldNotes(INPUT_ENDDATE_PREFIX_SCHEDULED[i], fdn, dndao,
										studyEventScheduled.getId(), "studyEvent", currentStudy);

								if (popupQuery) {
									String eventDivId = "Event_"
											+ SubjectLabelNormalizer.normalizeSubjectLabel(popupSubjectLabel)
											+ "_"
											+ scheduledDefinitionIds[i]
											+ (pageToShowPopup.equalsIgnoreCase(Page.LIST_EVENTS_FOR_SUBJECTS_SERVLET
											.getFileName()) ? "ev" : "") + "_";
									processEvents(eventDefs, eventDivId, studyEventScheduled.getId(),
											(StudyEventDefinitionBean) seddao.findByPK(scheduledDefinitionIds[i]),
											studySubject, sed, pageToShowPopup, selectedEventDefId);
								}
							} else {
								String pageMessage = restext.getString("scheduled_event_definition")
										+ definitionScheduleds.get(i).getName() + restext.getString("X_and_subject")
										+ studySubject.getName()
										+ restext.getString("not_created_since_event_not_repeating")
										+ restext.getString("event_type_already_exists");
								addPageMessage(pageMessage, request);
								if (popupQuery) {
									pageMessages.put(pageMessage);
								}
							}
						}
					}

				}

				request.getSession().removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
				request.setAttribute(EnterDataForStudyEventServlet.INPUT_EVENT_ID, String.valueOf(studyEvent.getId()));

				if (popupQuery) {
					response.setContentType("application/json");
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("eventDefs", eventDefs);
					jsonObject.put("pageMessages", pageMessages);
					response.getWriter().write(jsonObject.toString());
				} else if (fp.getString(OPEN_FIRST_CRF).equalsIgnoreCase("true")) {
					response.sendRedirect(request.getContextPath()
							+ Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET.getFileName() + "?eventId=" + studyEvent.getId()
							+ "&openFirstCrf=true");
				} else {
					response.sendRedirect(request.getContextPath()
							+ Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET.getFileName() + "?eventId=" + studyEvent.getId());
				}
			}
		}
	}

	private String getEventDivForScheduledEvent(HttpServletRequest request, String pageToShowPopup,
			String returnScheduledEvenContent, int selectedEventDefId, String eventCRFId, String eventDefintionCRFId) {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		int position = returnScheduledEvenContent.lastIndexOf("_");
		int rowCount = Integer.parseInt(returnScheduledEvenContent.substring(position + 1,
				returnScheduledEvenContent.length()));

		int studySubjectId = Integer.parseInt(request.getParameter("popupQueryStudySubjectId"));
		StudySubjectBean studySubject = (StudySubjectBean) getStudySubjectDAO().findByPK(studySubjectId);
		SubjectBean subject = (SubjectBean) getSubjectDAO().findByPK(studySubject.getSubjectId());

		int studyEventDefinitionId;
		StudyEventDefinitionBean sed;
		List<StudyEventBean> studyEvents;

		if (pageToShowPopup.equalsIgnoreCase(Page.LIST_EVENTS_FOR_SUBJECTS_SERVLET.getFileName())) {

			ListEventsForSubjectTableFactory factory = new ListEventsForSubjectTableFactory(true);
			factory.setStudyBean(currentStudy);
			factory.setCurrentRole(currentRole);
			factory.setCurrentUser(ub);
			factory.setLocale(request.getLocale());

			sed = (StudyEventDefinitionBean) getStudyEventDefinitionDAO().findByPK(selectedEventDefId);
			studyEvents = getStudyEventDAO().findAllByStudySubjectAndDefinition(studySubject, sed);
			Collections.reverse(studyEvents);

			return factory
					.eventDivBuilder(subject, rowCount, studyEvents, studyEvents.size(), sed, studySubject,
							((StringUtil.isBlank(eventCRFId) || eventCRFId.equalsIgnoreCase("undefined")) ? null
									: eventCRFId), ((StringUtil.isBlank(eventDefintionCRFId) || eventDefintionCRFId
									.equalsIgnoreCase("undefined")) ? null : eventDefintionCRFId), true
					);

		} else {

			ListStudySubjectTableFactory factory = new ListStudySubjectTableFactory(true);
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

			studyEventDefinitionId = Integer.parseInt(returnScheduledEvenContent.replace(
					"Event_" + SubjectLabelNormalizer.normalizeSubjectLabel(studySubject.getLabel()) + "_", "")
					.replaceAll("_.*", ""));
			sed = (StudyEventDefinitionBean) getStudyEventDefinitionDAO().findByPK(studyEventDefinitionId);
			studyEvents = getStudyEventDAO().findAllByStudySubjectAndDefinition(studySubject, sed);
			Collections.reverse(studyEvents);

			return factory.eventDivBuilder(subject, rowCount, studyEvents, sed, studySubject, request.getLocale());
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

		if (SubmitDataServlet.maySubmitData(ub, currentRole)) {
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
		return fp.getDateTime(INPUT_STARTDATE_PREFIX);
	}

	private Date getInputStartDateScheduled(FormProcessor fp, int i) {
		return fp.getDateTime(INPUT_STARTDATE_PREFIX_SCHEDULED[i]);
	}

	private Date getInputEndDate(FormProcessor fp) {
		return fp.getDateTime(INPUT_ENDDATE_PREFIX);
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

	public static ArrayList<StudyEventDefinitionBean> selectNotStartedOrRepeatingSortedEventDefs(StudySubjectBean ssb,
			int parentStudyId, StudyEventDefinitionDAO seddao, StudyGroupClassDAO sgcdao, StudyEventDAO sedao) {

		ArrayList<StudyEventDefinitionBean> result = new ArrayList<StudyEventDefinitionBean>();
		Map<Integer, StudyEventBean> StudyEventDefinitionIdToStudyEvent = new HashMap<Integer, StudyEventBean>();
		ArrayList<StudyEventBean> studyEvents = sedao.findAllByStudySubject(ssb);
		for (StudyEventBean studyEvent : studyEvents) {
			StudyEventDefinitionIdToStudyEvent.put(studyEvent.getStudyEventDefinitionId(), studyEvent);
		}

		StudyGroupClassBean defaultStudyGroupClassBean = (StudyGroupClassBean) sgcdao
				.findDefaultByStudyId(parentStudyId);
		boolean defaultStudyGroupClassBeanExist = !(defaultStudyGroupClassBean == null || defaultStudyGroupClassBean
				.getId() == 0);

		ArrayList<StudyGroupClassBean> allActiveDynGroupClasses = sgcdao
				.findAllActiveDynamicGroupsByStudyId(parentStudyId);
		Collections.sort(allActiveDynGroupClasses, StudyGroupClassBean.comparatorForDynGroupClasses);

		// ordered eventDefs from dynGroups
		if (defaultStudyGroupClassBeanExist || ssb.getDynamicGroupClassId() != 0) {
			for (StudyGroupClassBean dynGroup : allActiveDynGroupClasses) {
				ArrayList<StudyEventDefinitionBean> orderedEventDefinitionsFromDynGroup = seddao
						.findAllAvailableAndOrderedByStudyGroupClassId(dynGroup.getId());
				for (StudyEventDefinitionBean eventDefinition : orderedEventDefinitionsFromDynGroup) {
					if ((dynGroup.isDefault() && ssb.getDynamicGroupClassId() == 0)
							|| (ssb.getDynamicGroupClassId() != 0 && dynGroup.getId() == ssb.getDynamicGroupClassId())) {
						// eventDefs from defDynGroup and subject's dynGroup
						if (StudyEventDefinitionIdToStudyEvent.keySet().contains(eventDefinition.getId())) {
							if (StudyEventDefinitionIdToStudyEvent.get(eventDefinition.getId()).getSubjectEventStatus()
									.isNotScheduled()
									|| eventDefinition.isRepeating()) {
								result.add(eventDefinition);
							}
						} else {
							result.add(eventDefinition);
						}
					} else {
						// eventDefs from others dynGroups
						if (eventDefinition.isRepeating()) {
							result.add(eventDefinition);
						}
					}
				}
			}
		}

		ArrayList eventDefinitionsNotFromDynGroup = seddao.findAllActiveNotClassGroupedByStudyId(parentStudyId);
		// sort by study event definition ordinal
		Collections.sort(eventDefinitionsNotFromDynGroup);
		// filter notStarted and repeating eventDefs
		ArrayList notStartedAndRepeatingEventDefinitions = new ArrayList();
		for (Object anEventDefinitionsNotFromDynGroup : eventDefinitionsNotFromDynGroup) {
			StudyEventDefinitionBean eventDefinition = (StudyEventDefinitionBean) anEventDefinitionsNotFromDynGroup;
			if (StudyEventDefinitionIdToStudyEvent.keySet().contains(eventDefinition.getId())) {
				if (StudyEventDefinitionIdToStudyEvent.get(eventDefinition.getId()).getSubjectEventStatus()
						.isNotScheduled()
						|| eventDefinition.isRepeating()) {
					notStartedAndRepeatingEventDefinitions.add(eventDefinition);
				}
			} else {
				notStartedAndRepeatingEventDefinitions.add(eventDefinition);
			}
		}
		result.addAll(notStartedAndRepeatingEventDefinitions);
		return result;
	}
}
