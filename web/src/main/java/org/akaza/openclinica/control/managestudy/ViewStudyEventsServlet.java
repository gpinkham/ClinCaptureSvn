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
package org.akaza.openclinica.control.managestudy;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clinovo.util.RequestUtil;
import com.clinovo.util.DateUtil;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.StudyEventRow;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.ValidatorHelper;

/**
 * Handles user request of "view study events".
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class ViewStudyEventsServlet extends RememberLastPage {

	public static final String SAVED_VIEW_STUDY_EVENTS_URL = "savedViewStudyEventsUrl";

	public static final String SED_ID = "sedId";

	public static final String INPUT_STARTDATE = "startDate";

	public static final String INPUT_ENDDATE = "endDate";

	public static final String INPUT_DEF_ID = "definitionId";

	public static final String INPUT_STATUS_ID = "statusId";

	public static final String STATUS_MAP = "statuses";

	public static final String DEFINITION_MAP = "definitions";

	public static final String PRINT = "print";

	public static final int DEFAULT_FILTER_DATE_END_HOUR = 23;

	public static final int DEFAULT_FILTER_DATE_END_MINUTE = 59;

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (!ub.isSysAdmin() && !mayViewData(ub, currentRole)) {
			addPageMessage(
					respage.getString("no_have_correct_privilege_current_study")
							+ respage.getString("change_study_contact_sysadmin"), request);
			throw new InsufficientPermissionException(Page.MENU_SERVLET, restext.getString("not_correct_role"), "1");
		}
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		if (fp.getString(PRINT).isEmpty() && shouldRedirect(request, response)) {
			return;
		}
		StudyBean currentStudy = getCurrentStudy(request);

		// checks which module requests are from
		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);

		int sedId = fp.getInt(SED_ID);
		int statusId = fp.getInt(INPUT_STATUS_ID);
		int definitionId = fp.getInt(INPUT_DEF_ID);
		String startDateString = fp.getString(INPUT_STARTDATE);
		String endDateString = fp.getString(INPUT_ENDDATE);

		request.setAttribute(SED_ID, sedId);
		request.setAttribute(INPUT_STATUS_ID, statusId);
		request.setAttribute(INPUT_DEF_ID, definitionId);
		request.setAttribute(INPUT_STARTDATE, startDateString);
		request.setAttribute(INPUT_ENDDATE, endDateString);

		request.setAttribute(STATUS_MAP, SubjectEventStatus.toArrayList());

		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		ArrayList<StudyEventDefinitionBean> definitions = seddao.findAllAvailableByStudy(currentStudy);
		request.setAttribute(DEFINITION_MAP, definitions);

		Validator v = getValidator(request);
		v.addValidation(INPUT_STARTDATE, Validator.IS_A_DATE);
		v.addValidation(INPUT_ENDDATE, Validator.IS_A_DATE);
		HashMap errors = v.validate();
		if (!errors.isEmpty()) {
			setInputMessages(errors, request);
			request.setAttribute("allEvents", new ArrayList());
			request.setAttribute("queryUrl", "");
			request.setAttribute("enablePrint", "no");
			forwardPage(Page.VIEW_STUDY_EVENTS, request, response);
		} else {
			if ("yes".equalsIgnoreCase(fp.getString(PRINT))) {
				List allEvents = genEventsForPrint(currentStudy, definitions, getStartDateFilterValue(startDateString),
						getEndDateFilterValue(endDateString), definitionId, statusId);
				request.setAttribute("allEvents", allEvents);
				forwardPage(Page.VIEW_STUDY_EVENTS_PRINT, request, response);
			} else {
				List allEvents = genTables(fp, definitions, startDateString, endDateString, sedId, definitionId, statusId);
				request.setAttribute("allEvents", allEvents);
				String queryUrl = INPUT_STARTDATE + "=" + startDateString + "&" + INPUT_ENDDATE + "="
						+ endDateString + "&" + INPUT_DEF_ID + "=" + definitionId + "&" + INPUT_STATUS_ID + "="
						+ statusId + "&" + "sedId=" + sedId + "&submitted=" + fp.getInt("submitted");
				request.setAttribute("queryUrl", queryUrl);
				request.setAttribute("enablePrint", "yes");
				forwardPage(Page.VIEW_STUDY_EVENTS, request, response);
			}
		}
	}

	private ArrayList genTables(FormProcessor fp, ArrayList<StudyEventDefinitionBean> definitions, String startDateString,
			String endDateString, int sedId, int definitionId, int statusId) {

		StudyBean currentStudy = getCurrentStudy(fp.getRequest());
		StudyUserRoleBean currentRole = getCurrentRole(fp.getRequest());
		StudyEventDAO sedao = getStudyEventDAO();
		EventCRFDAO ecdao = getEventCRFDAO();
		ArrayList allEvents = new ArrayList();
		definitions = findDefinitionById(definitions, definitionId);
		StudySubjectDAO ssdao = getStudySubjectDAO();
		Map<Integer, ArrayList<StudyEventBean>> studyEventDefinitionEventsMap = new HashMap<Integer, ArrayList<StudyEventBean>>();
		List<StudySubjectBean> studySubjects = ssdao.findAllByStudyId(currentStudy.getId());
		for (StudySubjectBean ssb : studySubjects) {
			ArrayList<StudyEventBean> evts = sedao.findAllByStudySubject(ssb);
			for (StudyEventBean seb : evts) {
				seb.setStudySubjectLabel(ssb.getLabel());
				if (!(currentRole.isSysAdmin() || currentRole.isStudyAdministrator())
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
			events = findEventByStatusAndDate(events, statusId, getStartDateFilterValue(startDateString),
					getEndDateFilterValue(endDateString));

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
				} else if (se.getSubjectEventStatus().getId() > SubjectEventStatus.COMPLETED.getId()) {
					subjectDiscontinued++;
				}
			}

			ved.setSubjectCompleted(subjectCompleted);
			ved.setSubjectScheduled(subjectScheduled);
			ved.setSubjectDiscontinued(subjectDiscontinued);
			ved.setFirstScheduledStartDate(firstStartDateForScheduled);
			ved.setLastCompletionDate(lastCompletionDate);

			EntityBeanTable table;
			if (sedId == sed.getId()) {
				// apply finding function or ordering
				// function
				// to a specific table
				table = getEntityBeanTable();
			} else {
				table = new EntityBeanTable();
			}
			// sort by event
			// start date,
			// desc
			table.setSortingIfNotExplicitlySet(1, false);
			ArrayList allEventRows = StudyEventRow.generateRowsFromBeans((ArrayList) events);

			final int columnThree = 3;
			String[] columns = {
					currentStudy == null ? resword.getString("study_subject_ID") : currentStudy
							.getStudyParameterConfig().getStudySubjectIdLabel(),
					resword.getString("event_date_started"), resword.getString("subject_event_status"),
					resword.getString("actions")};
			table.setColumns(new ArrayList(Arrays.asList(columns)));
			table.hideColumnLink(columnThree);
			HashMap args = new HashMap();
			args.put("sedId", Integer.toString(sed.getId()));
			args.put("definitionId", Integer.toString(definitionId));
			args.put("statusId", Integer.toString(statusId));
			args.put("startDate", startDateString);
			args.put("endDate", endDateString);
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
	 * Generates an arraylist of study events for printing.
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
	private ArrayList genEventsForPrint(StudyBean currentStudy, ArrayList<StudyEventDefinitionBean> definitions,
			Date startDate, Date endDate, int definitionId, int statusId) {
		StudyEventDAO sedao = getStudyEventDAO();
		EventCRFDAO ecdao = getEventCRFDAO();
		ArrayList allEvents = new ArrayList();
		definitions = findDefinitionById(definitions, definitionId);
		StudySubjectDAO ssdao = getStudySubjectDAO();
		List<StudySubjectBean> studySubjects = ssdao.findAllByStudyId(currentStudy.getId());
		for (StudyEventDefinitionBean sed : definitions) {
			ViewEventDefinitionBean ved = new ViewEventDefinitionBean();

			ved.setDefinition(sed);

			ArrayList<StudyEventBean> events = new ArrayList<StudyEventBean>();
			for (StudySubjectBean studySubject : studySubjects) {
				List<StudyEventBean> evts = sedao.findAllWithSubjectLabelByStudySubjectAndDefinition(studySubject,
						sed.getId());
				for (StudyEventBean evt : evts) {
					evt.setStudySubjectLabel(studySubject.getLabel());
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
				if (se.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED) && se.getDateEnded() != null) {
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
					} else if (se.getDateEnded() != null && se.getDateEnded().after(lastCompletionDate)) {
						lastCompletionDate = se.getDateEnded();
					}
				} else if (se.getSubjectEventStatus().getId() > SubjectEventStatus.COMPLETED.getId()) {
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

	@Override
	protected String getUrlKey(HttpServletRequest request) {
		return SAVED_VIEW_STUDY_EVENTS_URL;
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {

		DateTime userDefaultStartDate = new DateTime().withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0)
				.withZoneRetainFields(DateTimeZone.forID(getUserAccountBean().getUserTimeZoneId()));
		DateTime userDefaultEndDate = userDefaultStartDate.plusMonths(1).minusDays(1)
				.withHourOfDay(DEFAULT_FILTER_DATE_END_HOUR).withMinuteOfHour(DEFAULT_FILTER_DATE_END_MINUTE);
		DateTimeFormatter dateFormatter = DateUtil.getDateTimeFormatter(DateUtil.DatePattern.DATE,
				LocaleResolver.getLocale(request));

		FormProcessor fp = new FormProcessor(request);
		int sedId = fp.getInt(SED_ID);
		int statusId = fp.getInt(INPUT_STATUS_ID);
		int definitionId = fp.getInt(INPUT_DEF_ID);
		String startDate = request.getParameter(INPUT_STARTDATE) == null
				? dateFormatter.print(userDefaultStartDate) : fp.getString(INPUT_STARTDATE);
		String endDate = request.getParameter(INPUT_ENDDATE) == null
				? dateFormatter.print(userDefaultEndDate) : fp.getString(INPUT_ENDDATE);
		try {
			startDate = URLEncoder.encode(startDate, "UTF-8");
			endDate = URLEncoder.encode(endDate, "UTF-8");
		} catch (Exception ex) {
			logger.error("Error has occurred.", ex);
		}
		String eblFiltered = fp.getString("ebl_filtered");
		String eblFilterKeyword = fp.getString("ebl_filterKeyword");
		String eblSortColumnInd = fp.getString("ebl_sortColumnInd");
		String eblSortAscending = fp.getString("ebl_sortAscending");
		StringBuilder sb = new StringBuilder();
		sb.append("").append("?sedId=").append(sedId).append("&statusId=").append(statusId).append("&definitionId=")
				.append(definitionId).append("&startDate=").append(startDate).append("&endDate=").append(endDate)
				.append("&ebl_page=1&ebl_sortColumnInd=")
				.append((!eblSortColumnInd.isEmpty() ? eblSortColumnInd : "0")).append("&ebl_sortAscending=")
				.append((!eblSortAscending.isEmpty() ? eblSortAscending : "1")).append("&ebl_filtered=")
				.append((!eblFiltered.isEmpty() ? eblFiltered : "0")).append("&ebl_filterKeyword=")
				.append((!eblFilterKeyword.isEmpty() ? eblFilterKeyword : "")).append("&&ebl_paginated=1");
		Locale locale = (Locale) request.getSession().getAttribute("viewStudyEventsServletPreviousLocale");
		boolean localeChanged = locale != null && !LocaleResolver.getLocale(request).equals(locale);
		request.getSession().setAttribute("viewStudyEventsServletPreviousLocale", LocaleResolver.getLocale(request));
		if (request.getParameter("refreshPage") != null || localeChanged) {
			saveUrl(getUrlKey(request), request.getRequestURL() + sb.toString(), request);
		}
		return sb.toString();
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null || !request.getQueryString().contains("&ebl_page=");
	}

	private Validator getValidator(HttpServletRequest request) {
		return new Validator(new ValidatorHelper(request, getConfigurationDao()));
	}

	private Date getStartDateFilterValue(String startDateString) {
		return DateUtil.parseDateStringToServerDateTime(startDateString + " 00:00",
				getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.TIMESTAMP,
				LocaleResolver.getLocale(RequestUtil.getRequest()));
	}

	private Date getEndDateFilterValue(String endDateString) {
		return DateUtil.parseDateStringToServerDateTime(endDateString + " 23:59",
				getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.TIMESTAMP,
				LocaleResolver.getLocale(RequestUtil.getRequest()));
	}
}
