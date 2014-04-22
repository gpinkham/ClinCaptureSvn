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
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.StudyEventRow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Handles user request of "view study events"
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class ViewStudyEventsServlet extends SecureController {

	Locale locale;

	public static final String INPUT_STARTDATE = "startDate";

	public static final String INPUT_ENDDATE = "endDate";

	public static final String INPUT_DEF_ID = "definitionId";

	public static final String INPUT_STATUS_ID = "statusId";

	public static final String STATUS_MAP = "statuses";

	public static final String DEFINITION_MAP = "definitions";

	public static final String PRINT = "print";

	/**
	 * Checks whether the user has the right permission to proceed function
	 */
	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, restext.getString("not_correct_role"), "1");
	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		// checks which module requests are from
		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);

		int sedId = fp.getInt("sedId");
		int statusId = fp.getInt(INPUT_STATUS_ID);
		int definitionId = fp.getInt(INPUT_DEF_ID);
		Date startDate = fp.getDate(INPUT_STARTDATE);
		Date endDate = fp.getDate(INPUT_ENDDATE);

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		String defaultStartDateString = month + "/01/" + year;
		Date defaultStartDate = new SimpleDateFormat("MM/dd/yy").parse(defaultStartDateString);

		cal.setTime(defaultStartDate);
		cal.add(Calendar.DATE, 30);
		Date defaultEndDate = cal.getTime();

		if (!fp.isSubmitted()) {
			logger.info("not submitted");
			HashMap presetValues = new HashMap();

			presetValues.put(INPUT_STARTDATE, local_df.format(defaultStartDate));
			presetValues.put(INPUT_ENDDATE, local_df.format(defaultEndDate));
			startDate = defaultStartDate;
			endDate = defaultEndDate;
			setPresetValues(presetValues);
		} else {
			Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
			v.addValidation(INPUT_STARTDATE, Validator.IS_A_DATE);
			v.addValidation(INPUT_ENDDATE, Validator.IS_A_DATE);
			errors = v.validate();
			if (!errors.isEmpty()) {
				setInputMessages(errors);
				startDate = defaultStartDate;
				endDate = defaultEndDate;
			}
			fp.addPresetValue(INPUT_STARTDATE, fp.getString(INPUT_STARTDATE));
			fp.addPresetValue(INPUT_ENDDATE, fp.getString(INPUT_ENDDATE));
			fp.addPresetValue(INPUT_DEF_ID, fp.getInt(INPUT_DEF_ID));
			fp.addPresetValue(INPUT_STATUS_ID, fp.getInt(INPUT_STATUS_ID));
			setPresetValues(fp.getPresetValues());
		}

		request.setAttribute(STATUS_MAP, SubjectEventStatus.toArrayList());

		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
		ArrayList<StudyEventDefinitionBean> definitions = seddao.findAllAvailableByStudy(currentStudy);
		request.setAttribute(DEFINITION_MAP, definitions);

		ArrayList allEvents = genTables(fp, definitions, startDate, endDate, sedId, definitionId, statusId);

		request.setAttribute("allEvents", allEvents);

		String queryUrl = INPUT_STARTDATE + "=" + local_df.format(startDate) + "&" + INPUT_ENDDATE + "="
				+ local_df.format(endDate) + "&" + INPUT_DEF_ID + "=" + definitionId + "&" + INPUT_STATUS_ID + "="
				+ statusId + "&" + "sedId=" + sedId + "&submitted=" + fp.getInt("submitted");
		request.setAttribute("queryUrl", queryUrl);
		if ("yes".equalsIgnoreCase(fp.getString(PRINT))) {
			allEvents = genEventsForPrint(definitions, startDate, endDate, definitionId, statusId);
			request.setAttribute("allEvents", allEvents);
			forwardPage(Page.VIEW_STUDY_EVENTS_PRINT);
		} else {
			forwardPage(Page.VIEW_STUDY_EVENTS);
		}

	}

	private ArrayList genTables(FormProcessor fp, ArrayList<StudyEventDefinitionBean> definitions, Date startDate,
			Date endDate, int sedId, int definitionId, int statusId) {
		StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
		EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
		ArrayList allEvents = new ArrayList();
		definitions = findDefinitionById(definitions, definitionId);
		StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
		Map<Integer, ArrayList<StudyEventBean>> studyEventDefinitionEventsMap = new HashMap<Integer, ArrayList<StudyEventBean>>();
		ArrayList studySubjects = ssdao.findAllByStudyId(currentStudy.getId());
		for (Object studySubject : studySubjects) {
			StudySubjectBean ssb = (StudySubjectBean) studySubject;
			ArrayList<StudyEventBean> evts = sedao.findAllByStudySubject(ssb);
			for (StudyEventBean seb : evts) {
				seb.setStudySubjectLabel(ssb.getLabel());
				if (!(currentRole.isStudyDirector() || currentRole.isStudyAdministrator())
						&& seb.getSubjectEventStatus().isLocked()) {
					seb.setEditable(false);
				}
				ArrayList<StudyEventBean> studyEventList = studyEventDefinitionEventsMap.get(seb
						.getStudyEventDefinitionId());
				if (studyEventList == null) {
					studyEventList = new ArrayList<StudyEventBean>();
					studyEventDefinitionEventsMap.put(seb.getStudyEventDefinitionId(), studyEventList);
				}
				studyEventList.add(seb);
			}
		}

		for (Object definition : definitions) {
			ViewEventDefinitionBean ved = new ViewEventDefinitionBean();
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) definition;

			ved.setDefinition(sed);

			ArrayList<StudyEventBean> events = studyEventDefinitionEventsMap.get(sed.getId());
			if (events == null) {
				events = new ArrayList<StudyEventBean>();
			}

			int subjectScheduled = 0;
			int subjectCompleted = 0;
			int subjectDiscontinued = 0;
			events = findEventByStatusAndDate(events, statusId, startDate, endDate);

			Date firstStartDateForScheduled = null;
			Date lastCompletionDate = null;
			// find the first firstStartDateForScheduled
			for (StudyEventBean se : events) {
				if (se.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED)) {
					firstStartDateForScheduled = se.getDateStarted();
					break;
				}

			}
			// find the first lastCompletionDate
			for (StudyEventBean se : events) {
				if (se.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED) && se.getDateEnded() != null) {
					lastCompletionDate = se.getDateEnded();
					break;
				}
			}

			for (StudyEventBean se : events) {
				if (se.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED)) {
					subjectScheduled++;
					if (se.getDateStarted().before(new Date())) {
						ArrayList eventCRFs = ecdao.findAllStartedByStudyEvent(se);
						if (eventCRFs.isEmpty()) {
							se.setScheduledDatePast(true);
						}
					}
					if (firstStartDateForScheduled == null) {
						firstStartDateForScheduled = se.getDateStarted();
					} else if (se.getDateStarted().before(firstStartDateForScheduled)) {
						firstStartDateForScheduled = se.getDateStarted();
					}
				} else if (se.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED)) {
					subjectCompleted++;
					if (lastCompletionDate == null) {
						lastCompletionDate = se.getDateEnded();
					} else if (se.getDateEnded() != null && se.getDateEnded().after(lastCompletionDate)) {
						lastCompletionDate = se.getDateEnded();
					}
				} else if (se.getSubjectEventStatus().getId() > 4) {
					subjectDiscontinued++;
				}
			}

			ved.setSubjectCompleted(subjectCompleted);
			ved.setSubjectScheduled(subjectScheduled);
			ved.setSubjectDiscontinued(subjectDiscontinued);
			ved.setFirstScheduledStartDate(firstStartDateForScheduled);
			ved.setLastCompletionDate(lastCompletionDate);

			EntityBeanTable table;
			if (sedId == sed.getId()) {// apply finding function or ordering
				// function
				// to a specific table
				table = fp.getEntityBeanTable();
			} else {
				table = new EntityBeanTable();
			}
			table.setSortingIfNotExplicitlySet(1, false);// sort by event
			// start date,
			// desc
			ArrayList allEventRows = StudyEventRow.generateRowsFromBeans((ArrayList) events);

			String[] columns = {
					currentStudy == null ? resword.getString("study_subject_ID") : currentStudy
							.getStudyParameterConfig().getStudySubjectIdLabel(),
					resword.getString("event_date_started"), resword.getString("subject_event_status"),
					resword.getString("actions") };
			table.setColumns(new ArrayList(Arrays.asList(columns)));
			table.hideColumnLink(3);
			HashMap args = new HashMap();
			args.put("sedId", Integer.toString(sed.getId()));
			args.put("definitionId", Integer.toString(definitionId));
			args.put("statusId", Integer.toString(statusId));
			args.put("startDate", local_df.format(startDate));
			args.put("endDate", local_df.format(endDate));
			table.setQuery("ViewStudyEvents", args);
			table.setRows(allEventRows);
			table.computeDisplay();

			ved.setStudyEventTable(table);

			if (!events.isEmpty()) {
				allEvents.add(ved);
			}
		}

		return allEvents;
	}

	/**
	 * Generates an arraylist of study events for printing
	 * 
	 * @param definitions
	 *            ArrayList<StudyEventDefinitionBean>
	 * @param startDate
	 *            Date
	 * @param endDate
	 *            Date
	 * @param definitionId
	 *            int
	 * @param statusId
	 *            int
	 * @return ArrayList
	 */
	private ArrayList genEventsForPrint(ArrayList<StudyEventDefinitionBean> definitions, Date startDate, Date endDate,
			int definitionId, int statusId) {
		StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
		EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
		ArrayList allEvents = new ArrayList();
		definitions = findDefinitionById(definitions, definitionId);
		StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
		List<StudySubjectBean> studySubjects = ssdao.findAllByStudyId(currentStudy.getId());
		for (StudyEventDefinitionBean sed : definitions) {
			ViewEventDefinitionBean ved = new ViewEventDefinitionBean();

			ved.setDefinition(sed);

			ArrayList<StudyEventBean> events = new ArrayList<StudyEventBean>();
			for (StudySubjectBean studySubject : studySubjects) {
				List<StudyEventBean> evts = sedao.findAllWithSubjectLabelByStudySubjectAndDefinition(studySubject,
						sed.getId());
				for (StudyEventBean evt : evts) {
					events.add(evt);
				}
			}

			int subjectScheduled = 0;
			int subjectCompleted = 0;
			int subjectDiscontinued = 0;
			events = findEventByStatusAndDate(events, statusId, startDate, endDate);

			Date firstStartDateForScheduled = null;
			Date lastCompletionDate = null;
			// find the first firstStartDateForScheduled
			for (Object event1 : events) {
				StudyEventBean se = (StudyEventBean) event1;
				if (se.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED)) {
					firstStartDateForScheduled = se.getDateStarted();
					break;
				}

			}
			// find the first lastCompletionDate
			for (Object event1 : events) {
				StudyEventBean se = (StudyEventBean) event1;
				if (se.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED)) {
					lastCompletionDate = se.getDateEnded();
					break;
				}
			}

			for (Object event : events) {
				StudyEventBean se = (StudyEventBean) event;
				if (se.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED)) {
					subjectScheduled++;
					if (se.getDateStarted().before(new Date())) {
						ArrayList eventCRFs = ecdao.findAllStartedByStudyEvent(se);
						if (eventCRFs.isEmpty()) {
							se.setScheduledDatePast(true);
						}
					}
					if (firstStartDateForScheduled == null) {
						firstStartDateForScheduled = se.getDateStarted();
					} else if (se.getDateStarted().before(firstStartDateForScheduled)) {
						firstStartDateForScheduled = se.getDateStarted();
					}
				} else if (se.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED)) {
					subjectCompleted++;
					if (lastCompletionDate == null) {
						lastCompletionDate = se.getDateEnded();
					} else if (se.getDateEnded().after(lastCompletionDate)) {
						lastCompletionDate = se.getDateEnded();
					}
				} else if (se.getSubjectEventStatus().getId() > 4) {
					// dropped out/stopped/skipped/relapse
					subjectDiscontinued++;
				}

			}
			ved.setSubjectCompleted(subjectCompleted);
			ved.setSubjectScheduled(subjectScheduled);
			ved.setSubjectDiscontinued(subjectDiscontinued);
			ved.setFirstScheduledStartDate(firstStartDateForScheduled);
			ved.setLastCompletionDate(lastCompletionDate);

			ved.setStudyEvents(events);

			if (!events.isEmpty()) {
				allEvents.add(ved);
			}
		}
		return allEvents;
	}

	private ArrayList<StudyEventDefinitionBean> findDefinitionById(ArrayList<StudyEventDefinitionBean> definitions,
			int definitionId) {
		if (definitionId > 0) {
			for (StudyEventDefinitionBean sed : definitions) {
				if (sed.getId() == definitionId) {
					ArrayList a = new ArrayList();
					a.add(sed);
					return a;
				}
			}
		}
		return definitions;
	}

	private ArrayList<StudyEventBean> findEventByStatusAndDate(ArrayList<StudyEventBean> events, int statusId,
			Date startDate, Date endDate) {
		ArrayList<StudyEventBean> a = new ArrayList<StudyEventBean>();
		for (StudyEventBean se : events) {
			if (!se.getDateStarted().before(startDate) && !se.getDateStarted().after(endDate)) {
				a.add(se);
			}
		}
		ArrayList<StudyEventBean> b = new ArrayList<StudyEventBean>();
		if (statusId > 0) {
			for (StudyEventBean se : a) {
				if (se.getSubjectEventStatus().getId() == statusId) {
					b.add(se);
				}
			}
			return b;
		}
		return a;
	}
}
