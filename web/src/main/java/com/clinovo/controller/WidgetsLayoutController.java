/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.dao.EventCRFSDVFilter;
import org.akaza.openclinica.dao.EventCRFSDVSort;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.FindSubjectsFilter;
import org.akaza.openclinica.dao.managestudy.FindSubjectsSort;
import org.akaza.openclinica.dao.managestudy.ListEventsForSubjectFilter;
import org.akaza.openclinica.dao.managestudy.ListNotesFilter;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.clinovo.bean.display.DisplayWidgetsLayoutBean;
import com.clinovo.bean.display.WidgetsRowWithExtraField;
import com.clinovo.bean.display.WidgetsRowWithName;
import com.clinovo.dao.CodedItemDAO;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.jmesa.evaluation.CRFEvaluationFilter;
import com.clinovo.jmesa.evaluation.CRFEvaluationItem;
import com.clinovo.jmesa.evaluation.CRFEvaluationSort;
import com.clinovo.model.CodedItem;
import com.clinovo.model.Widget;
import com.clinovo.model.WidgetsLayout;
import com.clinovo.service.CRFMaskingService;
import com.clinovo.service.WidgetService;
import com.clinovo.service.WidgetsLayoutService;

/**
 * This controller was created to gather data from database and send it to widgets.
 */
@Controller
@SuppressWarnings({"unused", "unchecked"})
public class WidgetsLayoutController extends SpringController {

	private static final int FILTER_START = 0;
	private static final int FILTER_END = 99999;
	private static final int NUMBER_OF_MONTHS = 11;
	private static final int PERCENTS = 100;

	private static final int EC_DISPLAY_PER_SCREEN = 5;
	private static final int ND_PER_CRF_DISPLAY_PER_SCREEN = 8;
	private static final int ESPS_DISPLAY_PER_SCREEN = 5;
	private static final int ESPAS_DISPLAY_PER_SCREEN = 5;

	private static final String STATUS_NOT_CODED = "items to be coded";
	private static final String STATUS_CODED = "coded items";
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemController.class);

	private static final Status[] SUBJECTS_STATUSES = {Status.LOCKED, Status.DELETED, Status.AUTO_DELETED, Status.SIGNED, Status.AVAILABLE};
	private static final Status[] LEGEND_SUBJECT_STATUSES = {Status.AVAILABLE, Status.DELETED, Status.AUTO_DELETED, Status.LOCKED, Status.SIGNED};

	@Autowired
	private DataSource datasource;

	@Autowired
	private WidgetsLayoutService widgetLayoutService;

	@Autowired
	private WidgetService widgetService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CodedItemDAO codedItemDAO;

	@Autowired
	private CRFMaskingService maskingService;

	/**
	 * This method is used to display the widget on the Home page. It takes the data from the table "widget" and
	 * "widgets_layout" processes it and sends back a list of widgets jsps that should be displayed and their order.
	 * 
	 * @param request
	 *            is used to obtain data about current UserAccount and Study.
	 * @param response
	 *            is used to remove caching.
	 * @return model ModelMap that contains list of jsps and their order.
	 * 
	 * @throws Exception
	 *             if there is incorrect data in the database.
	 */
	@RequestMapping("/configureHomePage")
	public ModelMap configureHomePageHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelMap model = new ModelMap();
		setRequestHeadersAndUpdateLocale(response, request);

		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute("userBean");
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");

		int studyId = sb.getId();
		int userId = ub.getId();

		List<WidgetsLayout> widgetsLayout = widgetLayoutService.findAllByStudyIdAndUserId(studyId, userId);
		List<DisplayWidgetsLayoutBean> dispayWidgetsLayout = new ArrayList<DisplayWidgetsLayoutBean>();

		for (WidgetsLayout currentLayout : widgetsLayout) {

			Widget currentWidget = widgetService.findByChildsId(currentLayout.getId());

			String widgetName = currentWidget.getWidgetName().toLowerCase().replaceAll(" ", "_");

			DisplayWidgetsLayoutBean currentDisplay = new DisplayWidgetsLayoutBean();

			currentDisplay.setWidgetName(widgetName + ".jsp");
			currentDisplay.setOrdinal(currentLayout.getOrdinal());
			currentDisplay.setWidgetId(currentWidget.getId());
			currentDisplay.setTwoColumnWidget(currentWidget.isTwoColumnWidget());

			dispayWidgetsLayout.add(currentDisplay);
		}
		Collections.sort(dispayWidgetsLayout, DisplayWidgetsLayoutBean.comparatorForDisplayWidgetsLayout);
		model.addAttribute("dispayWidgetsLayout", dispayWidgetsLayout);

		return model;
	}

	/**
	 * This method is used to save data after user updates his home page layout.
	 * 
	 * @param request
	 *            is used to gather data about current UserAccount, Study and updates that user has made in his layout.
	 */
	@RequestMapping("/saveHomePage")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	public void saveHomePage(HttpServletRequest request) {

		String orderInColumn1 = request.getParameter("orderInColumn1");
		String orderInColumn2 = request.getParameter("orderInColumn2");
		String unusedWidgets = request.getParameter("unusedWidgets");
		String orderOfBigWidgets = request.getParameter("bigWidgets");
		int userId = Integer.parseInt(request.getParameter("userId"));
		int studyId = Integer.parseInt(request.getParameter("studyId"));

		if (!orderInColumn1.isEmpty()) {
			int ordinalCounter1 = 1;
			List<String> widgetsIdsColumn1 = Arrays.asList(orderInColumn1.split("\\s*,\\s*"));

			for (String widgetIdColumn1 : widgetsIdsColumn1) {
				WidgetsLayout currentWidgetLayout = widgetLayoutService.findByWidgetIdAndStudyIdAndUserId(
						Integer.parseInt(widgetIdColumn1), studyId, userId);
				currentWidgetLayout.setOrdinal(ordinalCounter1);
				widgetLayoutService.saveWidgetLayout(currentWidgetLayout);
				ordinalCounter1 = ordinalCounter1 + 2;
			}
		}

		if (!orderInColumn2.isEmpty()) {
			int ordinalCounter2 = 2;
			List<String> widgetsIdsColumn2 = Arrays.asList(orderInColumn2.split("\\s*,\\s*"));

			for (String widgetIdColumn2 : widgetsIdsColumn2) {

				WidgetsLayout currentWidgetLayout = widgetLayoutService.findByWidgetIdAndStudyIdAndUserId(
						Integer.parseInt(widgetIdColumn2), studyId, userId);
				currentWidgetLayout.setOrdinal(ordinalCounter2);
				widgetLayoutService.saveWidgetLayout(currentWidgetLayout);
				ordinalCounter2 = ordinalCounter2 + 2;
			}
		}

		if (!orderOfBigWidgets.isEmpty()) {
			int ordinalCounter3 = 1;
			List<String> bigWidgetsIds = Arrays.asList(orderOfBigWidgets.split("\\s*,\\s*"));

			for (String bigWidgetId : bigWidgetsIds) {

				WidgetsLayout currentWidgetLayout = widgetLayoutService.findByWidgetIdAndStudyIdAndUserId(
						Integer.parseInt(bigWidgetId), studyId, userId);
				currentWidgetLayout.setOrdinal(ordinalCounter3);
				widgetLayoutService.saveWidgetLayout(currentWidgetLayout);
				ordinalCounter3 = ordinalCounter3 + 1;
			}
		}

		if (!unusedWidgets.isEmpty()) {
			List<String> unusedWidgetsIds = Arrays.asList(unusedWidgets.split("\\s*,\\s*"));

			for (String unusedWidgetsId : unusedWidgetsIds) {
				WidgetsLayout currentWidgetLayout = widgetLayoutService.findByWidgetIdAndStudyIdAndUserId(
						Integer.parseInt(unusedWidgetsId), studyId, userId);
				currentWidgetLayout.setOrdinal(0);
				widgetLayoutService.saveWidgetLayout(currentWidgetLayout);
			}
		}
	}

	/**
	 * This method is used to gather data from Database and send it to widget.
	 * 
	 * @param request
	 *            is used to gather information about current user and study.
	 * 
	 * @param response
	 *            is used to set correct locale and clear cache.
	 * 
	 * @throws IOException
	 *             if data from request is incorrect or database contains corrupted data.
	 */
	@RequestMapping("/initNdsAssignedToMeWidget")
	public void initNdsAssignedToMeWidget(HttpServletRequest request, HttpServletResponse response) throws IOException {

		setRequestHeadersAndUpdateLocale(response, request);

		int currentUser = Integer.parseInt(request.getParameter("userId"));
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		DiscrepancyNoteDAO discrepancyNoteDao = new DiscrepancyNoteDAO(datasource);

		Integer newDns = discrepancyNoteDao.getViewNotesCountWithFilter(" AND dn.assigned_user_id = " + currentUser
				+ " AND dn.resolution_status_id = 1", currentStudy, currentUser);

		if (newDns == null) {
			newDns = 0;
		}
		Integer updatedDns = discrepancyNoteDao.getViewNotesCountWithFilter(" AND dn.assigned_user_id = " + currentUser
				+ " AND dn.resolution_status_id = 2", currentStudy, currentUser);

		if (updatedDns == null) {
			updatedDns = 0;
		}
		Integer resolutionProposedDns = discrepancyNoteDao.getViewNotesCountWithFilter(" AND dn.assigned_user_id = "
				+ currentUser + " AND dn.resolution_status_id = 3", currentStudy, currentUser);

		if (resolutionProposedDns == null) {
			resolutionProposedDns = 0;
		}
		Integer closedDns = discrepancyNoteDao.getViewNotesCountWithFilter(" AND dn.assigned_user_id = " + currentUser
				+ " AND dn.resolution_status_id = 4", currentStudy, currentUser);

		if (closedDns == null) {
			closedDns = 0;
		}
		String result = newDns + "," + updatedDns + "," + resolutionProposedDns + "," + closedDns;
		response.getWriter().println(result);
	}

	/**
	 * This method is used to gather data from Database and send it to widget.
	 * 
	 * @param request
	 *            is used to gather information about current user and study.
	 * @param model
	 *            is used to return gathered from database data.
	 * @param response
	 *            is used to set correct locale and clear cache.
	 * 
	 * @throws IOException
	 *             if data from request is incorrect or database contains corrupted data.
	 * 
	 * @return model - Model with gathered data.
	 */
	@RequestMapping("/initEventsCompletionWidget")
	public String initEventsCompletionWidget(HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {

		setRequestHeadersAndUpdateLocale(response, request);

		String page = "widgets/includes/eventsCompletionChart";
		String action = request.getParameter("action");

		boolean hasPrevious;
		boolean hasNext;
		int displayFrom = Integer.parseInt(request.getParameter("lastElement"));
		int maxDisplayNumber = EC_DISPLAY_PER_SCREEN;
		int studyId = Integer.parseInt(request.getParameter("studyId"));

		if (action.equals("goBack")) {
			displayFrom -= maxDisplayNumber;
		}
		if (action.equals("goForward")) {
			displayFrom += maxDisplayNumber;
		}
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");
		List<StudyEventDefinitionBean> studyEventDefinitions = getListOfEventsDefinitions(sb);
		List<WidgetsRowWithName> eventCompletionRows = getEventCompletionDataRows(displayFrom, maxDisplayNumber, request);

		hasPrevious = displayFrom != 0;
		hasNext = displayFrom + EC_DISPLAY_PER_SCREEN < studyEventDefinitions.size();

		model.addAttribute("eventCompletionRows", eventCompletionRows);
		model.addAttribute("eventCompletionHasNext", hasNext);
		model.addAttribute("eventCompletionHasPrevious", hasPrevious);
		model.addAttribute("eventCompletionLastElement", displayFrom);

		return page;
	}

	private List<WidgetsRowWithName> getEventCompletionDataRows(int displayFrom, int maxDisplayNumber, HttpServletRequest request) {
		List<WidgetsRowWithName> eventCompletionRows = new ArrayList<WidgetsRowWithName>();
		SubjectEventStatus[] subjectEventStatuses = {SubjectEventStatus.SCHEDULED,
				SubjectEventStatus.DATA_ENTRY_STARTED, SubjectEventStatus.SOURCE_DATA_VERIFIED,
				SubjectEventStatus.SIGNED, SubjectEventStatus.COMPLETED, SubjectEventStatus.SKIPPED,
				SubjectEventStatus.STOPPED, SubjectEventStatus.LOCKED, SubjectEventStatus.REMOVED,
				SubjectEventStatus.NOT_SCHEDULED};

		StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(datasource);
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(datasource);
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");
		List<StudyEventDefinitionBean> studyEventDefinitions = getListOfEventsDefinitions(sb);

		for (int i = displayFrom; i < studyEventDefinitions.size() && i < displayFrom + maxDisplayNumber; i++) {
			WidgetsRowWithName currentRow = new WidgetsRowWithName();
			LinkedHashMap<String, Integer> countOfSubjectEventStatuses = new LinkedHashMap<String, Integer>();
			int countOfSubjectsStartedEvent = 0;

			for (SubjectEventStatus subjectEventStatus : subjectEventStatuses) {
				ListEventsForSubjectFilter listEventsForSubjectFilter = new ListEventsForSubjectFilter(
						studyEventDefinitions.get(i).getId(), sgcdao);
				String property = "event.status";
				String value = subjectEventStatus.getId() + "";
				listEventsForSubjectFilter.addFilter(property, value);
				int eventsWithStatusNoRepeats = studySubjectDAO.getCountWithFilter(listEventsForSubjectFilter, sb);
				countOfSubjectEventStatuses.put(subjectEventStatus.getCode().toLowerCase().replaceAll(" ", "_"),
						eventsWithStatusNoRepeats);
				countOfSubjectsStartedEvent += eventsWithStatusNoRepeats;
			}

			currentRow.setId(studyEventDefinitions.get(i).getId());
			currentRow.setRowName(studyEventDefinitions.get(i).getName());
			currentRow.setRowValues(countOfSubjectEventStatuses);
			eventCompletionRows.add(currentRow);
		}
		return eventCompletionRows;
	}

	/**
	 * This method is used to gather data from Database and send it to widget.
	 * 
	 * @param request
	 *            is used to gather information about current user and study.
	 * @param response
	 *            is used to set correct locale and clear cache.
	 * 
	 * @throws IOException
	 *             if data from request is incorrect or database contains corrupted data.
	 */
	@RequestMapping("/getEventsCompletionLegendValues")
	public void getEventsCompletionLegendValues(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		StudyEventDAO studyEventDAO = new StudyEventDAO(datasource);
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");

		SubjectEventStatus[] subjectEventStatuses = {SubjectEventStatus.SCHEDULED,
				SubjectEventStatus.DATA_ENTRY_STARTED, SubjectEventStatus.COMPLETED, SubjectEventStatus.SIGNED,
				SubjectEventStatus.LOCKED, SubjectEventStatus.SKIPPED, SubjectEventStatus.STOPPED,
				SubjectEventStatus.SOURCE_DATA_VERIFIED, SubjectEventStatus.REMOVED, SubjectEventStatus.NOT_SCHEDULED};

		List<WidgetsRowWithName> dataRows = getEventCompletionDataRows(FILTER_START, FILTER_END, request);
		HashMap<SubjectEventStatus, Integer> statusesMap = new HashMap<SubjectEventStatus, Integer>();
		for (WidgetsRowWithName row : dataRows) {
			LinkedHashMap<String, Integer> values = row.getRowValues();
			for (SubjectEventStatus status : subjectEventStatuses) {
				int currentValue = statusesMap.get(status) == null ? 0 : statusesMap.get(status);
				int valueForRow = values.get(status.getCode()) == null ? 0 : values.get(status.getCode());
				statusesMap.put(status, currentValue + valueForRow);
			}
		}
		List<Integer> listOfEventsWithStatuses = new ArrayList<Integer>();

		for (SubjectEventStatus eventStatus : subjectEventStatuses) {
			listOfEventsWithStatuses.add(statusesMap.get(eventStatus));
		}

		response.getWriter().println(listOfEventsWithStatuses);
	}

	/**
	 * This method is used to gather data from Database and send it to widget.
	 * 
	 * @param request
	 *            is used to gather information about current user and study.
	 * @param model
	 *            is used to return gathered from database data.
	 * @param response
	 *            is used to set correct locale and clear cache.
	 * 
	 * @throws IOException
	 *             if data from request is incorrect or database contains corrupted data.
	 * 
	 * @return model Model with gathered data.
	 */
	@RequestMapping("/initSubjectStatusCount")
	public String initSubjectStatusCountWidget(HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {

		setRequestHeadersAndUpdateLocale(response, request);

		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(datasource);
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");

		int availableSubjects = studySubjectDAO.getCountofStudySubjectsBasedOnStatus(sb, Status.AVAILABLE);
		int removedSubjects = studySubjectDAO.getCountofStudySubjectsBasedOnStatus(sb, Status.DELETED);
		int autoRemovedSubjects = studySubjectDAO.getCountofStudySubjectsBasedOnStatus(sb, Status.AUTO_DELETED);
		int lockedSubjects = studySubjectDAO.getCountofStudySubjectsBasedOnStatus(sb, Status.LOCKED);
		int signedSubjects = studySubjectDAO.getCountofStudySubjectsBasedOnStatus(sb, Status.SIGNED);

		model.addAttribute("countOfAvailableSubjects", availableSubjects);
		model.addAttribute("countOfRemovedSubjects", removedSubjects + autoRemovedSubjects);
		model.addAttribute("countOfLockedSubjects", lockedSubjects);
		model.addAttribute("countOfSignedSubjects", signedSubjects);

		return "widgets/includes/subjectStatusCountChart";
	}

	/**
	 * This method is used to gather data from Database and send it to widget.
	 * 
	 * @param request
	 *            is used to gather information about current user and study.
	 * @param model
	 *            is used to return gathered from database data.
	 * @param response
	 *            is used to set correct locale and clear cache.
	 * 
	 * @throws IOException
	 *             if data from request is incorrect or database contains corrupted data.
	 * 
	 * @return model Model with gathered data.
	 */
	@RequestMapping("/initStudyProgress")
	public String initStudyProgressWidget(HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {

		setRequestHeadersAndUpdateLocale(response, request);

		StudyEventDAO studyEventDAO = new StudyEventDAO(datasource);
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");

		SubjectEventStatus[] subjectEventStatuses = {SubjectEventStatus.SCHEDULED,
				SubjectEventStatus.DATA_ENTRY_STARTED, SubjectEventStatus.SOURCE_DATA_VERIFIED,
				SubjectEventStatus.SIGNED, SubjectEventStatus.COMPLETED, SubjectEventStatus.SKIPPED,
				SubjectEventStatus.STOPPED, SubjectEventStatus.LOCKED};

		List<StudyEventDefinitionBean> studyEventDefinitions = getListOfEventsDefinitions(sb);
		int countOfSubject = getCountOfSubjects(sb);
		int countOfStartedEvents = 0;
		int countOfNotStartedEvents;
		LinkedHashMap<String, Integer> mapOfEventsWithStatuses = new LinkedHashMap<String, Integer>();

		for (SubjectEventStatus eventStatus : subjectEventStatuses) {
			int countOfEventsWithStatus = studyEventDAO.getCountOfEventsBasedOnEventStatus(sb, eventStatus);
			int countOfEventsNoRepeats = studyEventDAO.getCountOfEventsBasedOnEventStatusNoRepeats(sb, eventStatus);
			mapOfEventsWithStatuses.put(eventStatus.getCode(), countOfEventsWithStatus);
			countOfStartedEvents += countOfEventsNoRepeats;
		}

		countOfNotStartedEvents = countOfSubject * studyEventDefinitions.size() - countOfStartedEvents;
		mapOfEventsWithStatuses.put(SubjectEventStatus.NOT_SCHEDULED.getCode(), countOfNotStartedEvents);

		model.addAttribute("studyProgressMap", mapOfEventsWithStatuses);

		return "widgets/includes/studyProgressChart";
	}

	/**
	 * This method is used to gather data from Database and send it to widget.
	 * 
	 * @param request
	 *            is used to gather information about current user and study.
	 * @param model
	 *            is used to return gathered from database data.
	 * @param response
	 *            is used to set correct locale and clear cache.
	 * 
	 * @throws IOException
	 *             if data from request is incorrect or database contains corrupted data.
	 * 
	 * @return model Model with gathered data.
	 */
	@RequestMapping("/initSdvProgressWidget")
	public String initSdvProgressWidget(HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {

		setRequestHeadersAndUpdateLocale(response, request);

		int sdvProgressYear = Integer.parseInt(request.getParameter("sdvProgressYear"));
		sdvProgressYear = sdvProgressYear == 0 ? Calendar.getInstance().get(Calendar.YEAR) : sdvProgressYear;
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute("userBean");

		EventCRFDAO eCrfdao = new EventCRFDAO(datasource);
		ArrayList<EventCRFBean> previousYear = (ArrayList<EventCRFBean>) eCrfdao.findSDVedEventCRFsByStudyAndYear(sb,
				sdvProgressYear - 1);
		boolean previousDataExists = previousYear.size() > 0;
		ArrayList<EventCRFBean> nextYear = (ArrayList<EventCRFBean>) eCrfdao.findSDVedEventCRFsByStudyAndYear(sb,
				sdvProgressYear + 1);
		Calendar sdvCal = Calendar.getInstance();
		int currentYear = sdvCal.get(Calendar.YEAR);
		boolean nextDataExists = sdvProgressYear < currentYear || nextYear.size() > 0;
		EventCRFSDVFilter sdvFilterDone = new EventCRFSDVFilter(sb.getId());
		sdvFilterDone.addFilter("sdvStatus", messageSource.getMessage("complete", null, LocaleResolver.getLocale())
				.toLowerCase());
		EventCRFSDVSort sdvSortDone = new EventCRFSDVSort();
		boolean sdvWithOpenQueries = sb.getStudyParameterConfig().getAllowSdvWithOpenQueries().equals("yes");
		ArrayList<EventCRFBean> ecrfs = eCrfdao.getAvailableWithFilterAndSort(sb.getId(), sdvFilterDone, sdvSortDone, sdvWithOpenQueries, 0, FILTER_END, ub.getId());

		List<Integer> countValues = new ArrayList<Integer>(Collections.nCopies(NUMBER_OF_MONTHS + 1, 0));
		int currentMonth = sdvCal.get(Calendar.MONTH);

		for (EventCRFBean ecrf : ecrfs) {
			if (ecrf.getUpdatedDate() != null) {
				sdvCal.setTime(ecrf.getUpdatedDate());
				int ecrfYear = sdvCal.get(Calendar.YEAR);

				if (ecrfYear <= sdvProgressYear) {
					int month = sdvCal.get(Calendar.MONTH);
					int eStartMonth = (ecrfYear == sdvProgressYear) ? month : 0;
					int eEndMonth = (sdvProgressYear != currentYear) ? NUMBER_OF_MONTHS : currentMonth;

					for (int i = eStartMonth; i <= eEndMonth; i++) {
						countValues.set(i, countValues.get(i) + 1);
					}
				}
			} else {
				LOGGER.error("Warning, event CRF with id=" + ecrf.getId() + " was updated, but updated date is NULL.");
			}
		}
		LinkedHashMap<String, Integer> valuesAndSigns = new LinkedHashMap<String, Integer>();
		int counter = 1;

		for (int currentValue : countValues) {
			String currentMonthName = messageSource.getMessage("short.month." + counter, null,
					LocaleResolver.getLocale());
			valuesAndSigns.put(currentMonthName, currentValue);
			counter++;
		}
		EventCRFSDVFilter sdvFilter = new EventCRFSDVFilter(sb.getId());
		sdvFilter.addFilter("sdvStatus", messageSource.getMessage("not_done", null, LocaleResolver.getLocale()));
		EventCRFSDVSort sdvSort = new EventCRFSDVSort();
		ArrayList<EventCRFBean> availableForSDV = eCrfdao.getAvailableWithFilterAndSort(sb.getId(), sdvFilter, sdvSort, sdvWithOpenQueries,
				0, FILTER_END, ub.getId());
		List<Integer> countAvailableCRFs = new ArrayList<Integer>(Collections.nCopies(NUMBER_OF_MONTHS + 1, 0));

		for (EventCRFBean avCRF : availableForSDV) {
			Calendar avCal = Calendar.getInstance();

			if (avCRF.getUpdatedDate() != null) {
				avCal.setTime(avCRF.getUpdatedDate());
				int avYear = avCal.get(Calendar.YEAR);

				if (avYear <= sdvProgressYear) {
					int avMonth = avCal.get(Calendar.MONTH);
					int startMonth = (avYear == sdvProgressYear) ? avMonth : 0;
					int endMonth = (sdvProgressYear != currentYear) ? NUMBER_OF_MONTHS : currentMonth;

					for (int i = startMonth; i <= endMonth; i++) {
						countAvailableCRFs.set(i, countAvailableCRFs.get(i) + 1);
					}
				}
			} else {
				LOGGER.error("Warning, event CRF with id=" + avCRF.getId() + " was updated, but updated date is NULL.");
			}
		}
		
		model.addAttribute("sdvAvailableECRFs", countAvailableCRFs);
		model.addAttribute("sdvProgressYear", sdvProgressYear);
		model.addAttribute("sdvValuesByMonth", valuesAndSigns);
		model.addAttribute("sdvNextYearExists", nextDataExists);
		model.addAttribute("sdvPreviousYearExists", previousDataExists);
		model.addAttribute("sdvProgressActivateLegend", !getCurrentRole(request).isStudySponsor());

		return "widgets/includes/sdvProgressChart";
	}

	/**
	 * This method is used to gather data from Database and send it to widget.
	 * 
	 * @param request
	 *            is used to gather information about current user and study.
	 * @param model
	 *            is used to return gathered from database data.
	 * @param response
	 *            is used to set correct locale and clear cache.
	 * 
	 * @return model Model with gathered data.
	 */
	@RequestMapping("/initNdsPerCrfWidget")
	public String initNdsPerCrfWidget(HttpServletRequest request, HttpServletResponse response, Model model) {

		setRequestHeadersAndUpdateLocale(response, request);
		String page = "widgets/includes/ndsPerCrfChart";
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute("userBean");
		CRFDAO crfdao = new CRFDAO(datasource);
		EventCRFDAO eCrfdao = new EventCRFDAO(datasource);
		DiscrepancyNoteDAO dnDao = new DiscrepancyNoteDAO(datasource);
		List<CRFBean> crfs = (List<CRFBean>) crfdao.findAllActiveByDefinitionsForCurrentStudy(sb.getId());
		crfs = maskingService.removeCRFIfItsMaskedInAllEvents(crfs, sb.getId(), ub.getId());
		List<NDsPerCRFDisplay> displays = new ArrayList<NDsPerCRFDisplay>();
		ResolutionStatus[] statuses = {ResolutionStatus.CLOSED, ResolutionStatus.UPDATED, ResolutionStatus.OPEN,
				ResolutionStatus.NOT_APPLICABLE};
		LinkedHashMap<String, List<Integer>> dataColumns = new LinkedHashMap<String, List<Integer>>();
		LinkedHashMap<String, List<Integer>> crfNameToDataColumns = new LinkedHashMap<String, List<Integer>>();
		Map<String, Map<ResolutionStatus, Integer>> crfNameToRSToDNCountMap = dnDao.countDNsByCRFs(sb,
				new ListNotesFilter(), ub);
		for (CRFBean crf : crfs) {
			int total = 0;
			NDsPerCRFDisplay currentDisplay = new NDsPerCRFDisplay();
			currentDisplay.setCrfName(crf.getName());
			Map<ResolutionStatus, Integer> rsToDNCountMap = crfNameToRSToDNCountMap.get(crf.getName());
			if (rsToDNCountMap == null) {
				rsToDNCountMap = new HashMap<ResolutionStatus, Integer>();
			}
			List<Integer> contNdsWithStatuses = new ArrayList<Integer>();
			for (ResolutionStatus status : statuses) {
				Integer count = rsToDNCountMap.get(status);
				count = count == null ? 0 : count;
				total += count;
				contNdsWithStatuses.add(count);
			}
			crfNameToDataColumns.put(crf.getName(), contNdsWithStatuses);
			currentDisplay.setCountOfNds(total);
			displays.add(currentDisplay);
		}
		Collections.sort(displays, new Comparator<NDsPerCRFDisplay>() {
			public int compare(final NDsPerCRFDisplay display1, final NDsPerCRFDisplay display2) {
				int compareResult;
				if (display1.getCountOfNds() < display2.getCountOfNds()) {
					compareResult = 1;
				} else if (display1.getCountOfNds() > display2.getCountOfNds()) {
					compareResult = -1;
				} else {
					compareResult = display1.getCrfName().compareTo(display2.getCrfName());
				}
				return compareResult;
			}
		});

		int start = Integer.parseInt(request.getParameter("start"));
		int maxDispay = ND_PER_CRF_DISPLAY_PER_SCREEN;
		String action = request.getParameter("action");

		if (action.equals("goForward")) {
			start += maxDispay;
		}
		if (action.equals("goBack")) {
			start -= maxDispay;
		}
		for (int i = start; i < displays.size() && i < start + maxDispay; i++) {
			boolean eCrfExist = true;
			String crfName = displays.get(i).getCrfName();
			dataColumns.put(crfName, crfNameToDataColumns.get(crfName));
		}
		boolean hasPrevious = start != 0;
		boolean hasNext = start + maxDispay < displays.size();
		model.addAttribute("ndsCrfHasPrevious", hasPrevious);
		model.addAttribute("ndsCrfHasNext", hasNext);
		model.addAttribute("ndsCrfStart", start);
		model.addAttribute("ndsCrfDataColumns", dataColumns);
		return page;
	}

	/**
	 * This method is used to gather data from Database and send it to widget.
	 * 
	 * @param request
	 *            is used to gather information about current user and study.
	 * @param model
	 *            is used to return gathered from database data.
	 * @param response
	 *            is used to set correct locale and clear cache.
	 * 
	 * @return model Model with gathered data.
	 */
	@RequestMapping("/initEnrollmentProgressWidget")
	public String initEnrollmentProgressWidget(HttpServletRequest request, HttpServletResponse response, Model model) {

		setRequestHeadersAndUpdateLocale(response, request);

		String page = "widgets/includes/enrollmentProgressChart";
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");
		StudySubjectDAO ssDao = new StudySubjectDAO(datasource);
		StudyGroupClassDAO studyGroupClassDAO = new StudyGroupClassDAO(datasource);

		int displayedYear = Integer.parseInt(request.getParameter("currentYear"));
		displayedYear = displayedYear == 0 ? Calendar.getInstance().get(Calendar.YEAR) : displayedYear;

		FindSubjectsFilter previousYearFilter = new FindSubjectsFilter(studyGroupClassDAO);
		previousYearFilter.addFilter("studySubject.createdYear", displayedYear - 1);
		int previousYearData = ssDao.getCountWithFilter(previousYearFilter, sb);

		boolean previousYearDataExists = previousYearData > 0;

		FindSubjectsFilter nextYearFilter = new FindSubjectsFilter(studyGroupClassDAO);
		nextYearFilter.addFilter("studySubject.createdYear", displayedYear + 1);
		int nextYearData = ssDao.getCountWithFilter(nextYearFilter, sb);

		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH);

		boolean nextYearDataExists = nextYearData > 0 || displayedYear < currentYear;

		FindSubjectsSort findSubjectsSort = new FindSubjectsSort();
		FindSubjectsFilter findSubjectsFilter = new FindSubjectsFilter(studyGroupClassDAO);

		List<StudySubjectBean> listOfSubjects = ssDao.getWithFilterAndSort(sb, findSubjectsFilter, findSubjectsSort,
				FILTER_START, FILTER_END);

		LinkedHashMap<String, LinkedHashMap<Status, Integer>> dataRows = new LinkedHashMap<String, LinkedHashMap<Status, Integer>>();
		ArrayList<String> months = getMonthsList(request);

		for (String month : months) {

			LinkedHashMap<Status, Integer> blankStatuses = new LinkedHashMap<Status, Integer>();
			blankStatuses.put(Status.LOCKED, 0);
			blankStatuses.put(Status.DELETED, 0);
			blankStatuses.put(Status.SIGNED, 0);
			blankStatuses.put(Status.AVAILABLE, 0);

			dataRows.put(month, blankStatuses);
		}

		for (StudySubjectBean subject : listOfSubjects) {

			Calendar subjectCreatedCalendar = Calendar.getInstance();
			subjectCreatedCalendar.setTime(subject.getCreatedDate());

			int subjectCreatedYear = subjectCreatedCalendar.get(Calendar.YEAR);
			int subjectCreatedMonth = subjectCreatedCalendar.get(Calendar.MONTH);
			Date newDate = new Date(0);

			Calendar subjectUpdatedCalendar = Calendar.getInstance();
			if (subject.getUpdatedDate() != null) {
				subjectUpdatedCalendar.setTime(subject.getUpdatedDate());
			} else {
				subject.setUpdatedDate(newDate);
				subjectUpdatedCalendar.setTime(newDate);
			}

			int subjectUpdatedYear = subjectUpdatedCalendar.get(Calendar.YEAR);
			int subjectUpdatedMonth = subjectUpdatedCalendar.get(Calendar.MONTH);
			boolean wasSubjectAvailableAtLeastMonthInDisplayedYear = subjectCreatedYear <= displayedYear
					&& (subject.getStatus() == Status.AVAILABLE || (subjectUpdatedYear >= displayedYear && ((subjectUpdatedYear == subjectCreatedYear && subjectUpdatedMonth != subjectCreatedMonth) || subjectUpdatedYear != subjectCreatedYear)));

			// Add info about time when subjects were available
			if (wasSubjectAvailableAtLeastMonthInDisplayedYear) {

				int cStartMonth = subjectCreatedYear == displayedYear ? subjectCreatedMonth : 0;
				int cEndMonth = displayedYear != currentYear ? NUMBER_OF_MONTHS : currentMonth;

				if (subject.getUpdatedDate() != newDate && subject.getStatus().getId() != Status.AVAILABLE.getId()
						&& subjectUpdatedYear == displayedYear) {

					cEndMonth = subjectUpdatedMonth - 1;
				}

				for (int j = cStartMonth; j <= cEndMonth; j++) {

					String dataRowMonth = months.get(j);
					LinkedHashMap<Status, Integer> dataRowValues = dataRows.get(dataRowMonth);
					int currentValue = dataRowValues.get(Status.AVAILABLE);

					dataRowValues.put(Status.AVAILABLE, currentValue + 1);
					dataRows.put(dataRowMonth, dataRowValues);
				}
			}

			// Add info about subject's latest update
			if (subject.getUpdatedDate() != newDate && subject.getStatus() != Status.AVAILABLE
					&& subjectUpdatedYear <= displayedYear) {

				int uStartMonth = subjectUpdatedYear == displayedYear ? subjectUpdatedMonth : 0;
				int uEndMoth = displayedYear != currentYear ? NUMBER_OF_MONTHS : currentMonth;

				for (int j = uStartMonth; j <= uEndMoth; j++) {

					String dataRowMonth = months.get(j);
					Status updatedStatus = subject.getStatus() == Status.AUTO_DELETED ? Status.DELETED : subject
							.getStatus();

					String updatedDataRowMonth = months.get(j);
					LinkedHashMap<Status, Integer> updatedDataRowValues = dataRows.get(updatedDataRowMonth);

					int uCurrentValue = updatedDataRowValues.get(updatedStatus);

					updatedDataRowValues.put(updatedStatus, uCurrentValue + 1);
					dataRows.put(dataRowMonth, updatedDataRowValues);
				}
			}
		}

		model.addAttribute("epYear", displayedYear);
		model.addAttribute("epDataRows", dataRows);
		model.addAttribute("epPreviousYearExists", previousYearDataExists);
		model.addAttribute("epNextYearExists", nextYearDataExists);

		return page;
	}

	/**
	 * This method is used to gather data from Database and send it to widget.
	 * 
	 * @param request
	 *            is used to gather information about current user and study.
	 * @param model
	 *            is used to return gathered from database data.
	 * @param response
	 *            is used to set correct locale and clear cache.
	 * 
	 * @return model - Model with gathered data.
	 */
	@RequestMapping("/initCodingProgressWidget")
	public String initCodingProgressWidget(HttpServletRequest request, HttpServletResponse response, Model model) {

		setRequestHeadersAndUpdateLocale(response, request);

		String page = "widgets/includes/codingProgressChart";
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");

		ItemDataDAO itemDataDAO = new ItemDataDAO(datasource);

		// Set which year should be displayed.
		int displayedYear = Integer.parseInt(request.getParameter("codingProgressYear"));
		displayedYear = displayedYear == 0 ? Calendar.getInstance().get(Calendar.YEAR) : displayedYear;
		List<CodedItem> codingItems;

		if (sb.isSite(sb.getParentStudyId())) {
			codingItems = codedItemDAO.findByStudyAndSite(sb.getParentStudyId(), sb.getId());
		} else {
			codingItems = codedItemDAO.findByStudy(sb.getId());
		}

		ArrayList<Integer> itemsIds = new ArrayList<Integer>();

		// Get list of IDs of item data for all Coded Items.
		for (CodedItem item : codingItems) {
			if (item.getCodedItemElements().size() > 0) {
				itemsIds.add(item.getCodedItemElements().get(0).getItemDataId());
			}
		}

		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH);
		int previousYear = displayedYear - 1;

		// Check - should "Next" or "Previous" buttons be displayed.
		boolean previousYearDataExist = false;

		for (Integer itemId : itemsIds) {
			ItemDataBean item = itemDataDAO.findByPKAndYear(itemId, previousYear);
			if (item.getItemId() != 0) {
				previousYearDataExist = true;
			}
		}

		boolean nextYearDataExist = displayedYear < currentYear;

		LinkedHashMap<String, LinkedHashMap<String, Integer>> cpDataRows = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
		ArrayList<String> months = getMonthsList(request);

		// Create an empty data rows with months names.
		for (String month : months) {
			LinkedHashMap<String, Integer> blankStatuses = new LinkedHashMap<String, Integer>();
			blankStatuses.put(STATUS_CODED, 0);
			blankStatuses.put(STATUS_NOT_CODED, 0);
			cpDataRows.put(month, blankStatuses);
		}

		// Add values to data rows.
		for (CodedItem item : codingItems) {

			if (item.getCodedItemElements().size() > 0) {

				ItemDataBean codedItemData = (ItemDataBean) itemDataDAO.findByPK(item.getCodedItemElements().get(0)
						.getItemDataId());

				Date createdDate = codedItemData.getCreatedDate();
				Date updatedDate = codedItemData.getUpdatedDate() != null
						? codedItemData.getUpdatedDate()
						: new Date(0);
				Calendar createCalendar = Calendar.getInstance();
				Calendar updateCalendar = Calendar.getInstance();
				createCalendar.setTime(createdDate);
				updateCalendar.setTime(updatedDate);

				int createdYear = createCalendar.get(Calendar.YEAR);
				int createdMonth = createCalendar.get(Calendar.MONTH);
				int updatedYear = updateCalendar.get(Calendar.YEAR);
				int updatedMonth = updateCalendar.get(Calendar.MONTH);
				boolean itemWasNotCoded = (item.getStatus().equals("NOT_CODED")) && createdYear <= displayedYear;

				if (itemWasNotCoded) {

					int startMonth = createdYear == displayedYear ? createdMonth : 0;
					int endMonth = currentYear == displayedYear ? currentMonth : NUMBER_OF_MONTHS;

					for (int i = startMonth; i <= endMonth; i++) {

						cpDataRows = getUpdateValueForMonth(cpDataRows, months.get(i), STATUS_NOT_CODED);
					}
				}

				boolean itemWasCodedInThisYear = (item.getStatus().equals("CODED") && (updatedYear <= displayedYear));

				if (itemWasCodedInThisYear) {

					int startMonth = createdYear == displayedYear ? createdMonth : 0;
					int endMonth = currentYear == displayedYear ? currentMonth : NUMBER_OF_MONTHS;

					boolean itemWasUpdatedAtSameMonthWhenCreated = createdYear == updatedYear
							&& createdMonth == updatedMonth;

					if (itemWasUpdatedAtSameMonthWhenCreated) {

						endMonth--;
					}

					for (int i = startMonth; i < endMonth; i++) {

						cpDataRows = getUpdateValueForMonth(cpDataRows, months.get(i), STATUS_NOT_CODED);
					}
				}

				boolean addItemToCodedBar = (item.getStatus().equals("CODED")) && createdYear <= displayedYear;

				if (addItemToCodedBar) {

					int startMonth = updatedYear == displayedYear ? updatedMonth : 0;
					int endMonth = currentYear == displayedYear ? currentMonth : NUMBER_OF_MONTHS;

					for (int i = startMonth; i <= endMonth; i++) {

						cpDataRows = getUpdateValueForMonth(cpDataRows, months.get(i), STATUS_CODED);
					}
				}
			}
		}

		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute("userBean");
		Role userRole = ub.getActiveStudyRole();
		if (userRole == Role.INVALID && sb.getParentStudyId() > 0) {
			userRole = ub.getRoleByStudy(sb.getParentStudyId()).getRole();
		}
		boolean activateLegend = false;

		if (userRole == Role.SYSTEM_ADMINISTRATOR || userRole == Role.STUDY_ADMINISTRATOR
				|| userRole == Role.STUDY_CODER) {

			activateLegend = true;
		}

		// Set all attributes to model.
		model.addAttribute("cpPreviousYearExists", previousYearDataExist);
		model.addAttribute("cpNextYearExists", nextYearDataExist);
		model.addAttribute("cpDataRows", cpDataRows);
		model.addAttribute("cpYear", displayedYear);
		model.addAttribute("cpActivateLegend", activateLegend);

		return page;
	}

	/**
	 * This method is used to gather data from Database and send it to widget.
	 *
	 * @param request
	 *            is used to gather information about current user and study.
	 * @param model
	 *            is used to return gathered from database data.
	 * @param response
	 *            is used to set correct locale and clear cache.
	 *
	 * @return model - Model with gathered data.
	 */
	@RequestMapping("/initEnrollStatusPerSiteWidget")
	public String initEnrollStatusPerSiteWidget(HttpServletRequest request, HttpServletResponse response, Model model) {

		String page = "widgets/includes/enrollStatusPerSiteChart";
		StudyDAO studyDAO = new StudyDAO(datasource);
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(datasource);
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");
		String action = request.getParameter("action");
		int currentDisplay = Integer.parseInt(request.getParameter("epPerSiteDisplay"));
		if (action.equals("goBack")) {
			currentDisplay -= ESPS_DISPLAY_PER_SCREEN;
		} else if (action.equals("goForward")) {
			currentDisplay += ESPS_DISPLAY_PER_SCREEN;
		}
		ArrayList<Integer> listOfSitesIds = (ArrayList<Integer>) studyDAO.findAllSiteIdsByStudy(sb);
		ArrayList<WidgetsRowWithExtraField> dataRows = new ArrayList<WidgetsRowWithExtraField>();
		listOfSitesIds.remove(listOfSitesIds.indexOf(sb.getId()));
		List<StudyBean> listOfSites = new ArrayList<StudyBean>();

		for (int index : listOfSitesIds) {
			StudyBean currentSite = (StudyBean) studyDAO.findByPK(index);
			listOfSites.add(currentSite);
		}
		listOfSites = sortSitesByPercentOfExpectedEnrollment(listOfSites);
		int listSize = listOfSites.size();

		for (int i = currentDisplay; i < currentDisplay + ESPS_DISPLAY_PER_SCREEN && i < listSize; i++) {
			StudyBean site = listOfSites.get(i);
			WidgetsRowWithExtraField currentRow = new WidgetsRowWithExtraField();
			currentRow.setRowName(site.getName());
			currentRow.setName(site.getIdentifier());
			LinkedHashMap<String, Integer> values = getEnrollmentPerSiteValues(site);
			currentRow.setRowValues(values);
			String percentOfExpectedEnrollment = getPercentOfEnrollmentForSite(site);
			currentRow.setExtraField(percentOfExpectedEnrollment);
			dataRows.add(currentRow);
		}

		boolean nextButtonEnabled = currentDisplay + ESPS_DISPLAY_PER_SCREEN < listSize;
		boolean prevButtonEnabled = currentDisplay > 0;
		model.addAttribute("espsDataRows", dataRows);
		model.addAttribute("espsDisplay", currentDisplay);
		model.addAttribute("espsPreviousPageExists", prevButtonEnabled);
		model.addAttribute("espsNextPageExists", nextButtonEnabled);
		return page;
	}

	/**
	 * This method is used to gather data from Database and send it to widget.
	 * 
	 * @param request
	 *            is used to gather information about current user and study.
	 * @param response
	 *            is used to set correct locale and clear cache.
	 * @throws IOException
	 *             if data from request is incorrect or database contains corrupted data.
	 */
	@RequestMapping("/getEnrollStatusPerSiteLegendValues")
	public void getEnrollStatusPerSiteLegendValues(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		setRequestHeadersAndUpdateLocale(response, request);

		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(datasource);
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");

		int availableSubjects = studySubjectDAO.getCountofStudySubjectsBasedOnStatus(sb, Status.AVAILABLE);
		int removedSubjects = studySubjectDAO.getCountofStudySubjectsBasedOnStatus(sb, Status.DELETED);
		int autoRemovedSubjects = studySubjectDAO.getCountofStudySubjectsBasedOnStatus(sb, Status.AUTO_DELETED);
		int lockedSubjects = studySubjectDAO.getCountofStudySubjectsBasedOnStatus(sb, Status.LOCKED);
		int signedSubjects = studySubjectDAO.getCountofStudySubjectsBasedOnStatus(sb, Status.SIGNED);

		List<Integer> listOfEventsWithStatuses = new ArrayList<Integer>();

		listOfEventsWithStatuses.add(availableSubjects);
		listOfEventsWithStatuses.add(signedSubjects);
		listOfEventsWithStatuses.add(removedSubjects + autoRemovedSubjects);
		listOfEventsWithStatuses.add(lockedSubjects);

		response.getWriter().println(listOfEventsWithStatuses);
	}

	/**
	 * This method is used to gather data from Database and send it to widget.
	 * 
	 * @param request is used to gather information about current user and study.
	 * @param model is used to return gathered from database data.
	 * @param response is used to set correct locale and clear cache.
	 * @return model - Model with gathered data.
	 */
	@RequestMapping("/initEvaluationProgressWidget")
	public String initEvaluationProgressWidget(HttpServletRequest request, HttpServletResponse response, Model model) {

		EventCRFDAO eventCRFDAO = new EventCRFDAO(datasource);
		setRequestHeadersAndUpdateLocale(response, request);
		Locale locale = LocaleResolver.getLocale();
		String page = "widgets/includes/evaluationProgressChart";
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute("userBean");
		int displayedYear = Integer.parseInt(request.getParameter("evaluationProgressYear"));
		displayedYear = displayedYear == 0 ? Calendar.getInstance().get(Calendar.YEAR) : displayedYear;

		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH);
		int previousYear = displayedYear - 1;

		LinkedHashMap<String, LinkedHashMap<String, Integer>> dataRows = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
		ArrayList<String> months = getMonthsList(request);
		// Create an empty data rows with months names.
		for (String month : months) {
			LinkedHashMap<String, Integer> blankStatuses = new LinkedHashMap<String, Integer>();
			blankStatuses.put(messageSource.getMessage("evaluation_completed", null, locale), 0);
			blankStatuses.put(messageSource.getMessage("ready_for_evaluation", null, locale), 0);
			dataRows.put(month, blankStatuses);
		}
		CRFEvaluationFilter filter = new CRFEvaluationFilter(null, messageSource, LocaleResolver.getLocale());
		CRFEvaluationSort sort = new CRFEvaluationSort();
		List<CRFEvaluationItem> evaluationItems = eventCRFDAO.findAllEventCrfsForEvaluation(sb, filter, sort,
				FILTER_START, FILTER_END);

		for (CRFEvaluationItem item : evaluationItems) {
			Calendar availableCalendar = Calendar.getInstance();
			availableCalendar.setTime(item.getDateCompleted());
			int availableYear = availableCalendar.get(Calendar.YEAR);
			int availableMonth = availableCalendar.get(Calendar.MONTH);
			boolean itemWasEvaluated = item.getDateValidateCompleted() != null && availableYear <= displayedYear;
			boolean itemNotEvaluated = item.getDateValidateCompleted() == null && availableYear <= displayedYear;
			// Check if CRF was not evaluated yet.
			if (itemNotEvaluated) {
				int startMonth = availableYear == displayedYear ? availableMonth : 0;
				int endMonth = displayedYear == currentYear ? currentMonth : NUMBER_OF_MONTHS;
				for (int i = startMonth; i <= endMonth; i++) {
					dataRows = getUpdateValueForMonth(dataRows, months.get(i),
							messageSource.getMessage("ready_for_evaluation", null, locale));
				}
			}
			if (itemWasEvaluated) {
				Calendar evaluatedCalendar = Calendar.getInstance();
				evaluatedCalendar.setTime(item.getDateValidateCompleted());
				int evaluatedYear = evaluatedCalendar.get(Calendar.YEAR);
				int evaluatedMonth = evaluatedCalendar.get(Calendar.MONTH);
				boolean wasAvailableMonthOrMore = (evaluatedYear == availableYear && evaluatedMonth > availableMonth)
						|| evaluatedYear > availableYear;
				// Check if CRF was already evaluated, but it was available some time to.
				if (wasAvailableMonthOrMore) {
					int startMonth = availableYear == displayedYear ? availableMonth : 0;
					int endMonth = evaluatedYear == displayedYear ? evaluatedMonth - 1 : NUMBER_OF_MONTHS;
					for (int i = startMonth; i <= endMonth; i++) {
						dataRows = getUpdateValueForMonth(dataRows, months.get(i),
								messageSource.getMessage("ready_for_evaluation", null, locale));
					}
				}
				boolean wasEvaluatedInThisYear = evaluatedYear <= displayedYear;
				// Check if CRF should be displayed in the already evaluated column.
				if (wasEvaluatedInThisYear) {
					int startMonth = displayedYear == evaluatedYear ? evaluatedMonth : 0;
					int endMonth = displayedYear == currentYear ? currentMonth : NUMBER_OF_MONTHS;
					for (int i = startMonth; i <= endMonth; i++) {
						dataRows = getUpdateValueForMonth(dataRows, months.get(i),
								messageSource.getMessage("evaluation_completed", null, locale));
					}
				}
			}
		}
		filter.addFilter("crfCompletedYear", previousYear);
		boolean evalProgNextYearExists = currentYear > displayedYear;
		boolean evalProgPreviousYearExists = eventCRFDAO.countOfAllEventCrfsForEvaluation(filter, sb) != 0;
		boolean evaluationProgressActivateLegend = sb.getStudyParameterConfig().getStudyEvaluator().equals("yes") && !getCurrentRole(request).isStudySponsor();
		model.addAttribute("evalProgNextYearExists", evalProgNextYearExists);
		model.addAttribute("evalProgPreviousYearExists", evalProgPreviousYearExists);
		model.addAttribute("evaluationProgressYear", displayedYear);
		model.addAttribute("evaluationProgressActivateLegend", evaluationProgressActivateLegend);
		model.addAttribute("evaluationProgressDataRows", dataRows);

		return page;
	}

	/**
	 * Enrollment Status per Available Sites Widget.
	 *
	 * @param request is used to gather information about current user and study.
	 * @param model is used to return gathered from database data.
	 * @param response is used to set correct locale and clear cache.
	 *
	 * @return model - Model with gathered data.
	 */
	@RequestMapping("/initESPASWidget")
	public String initESPASWidget(HttpServletRequest request, HttpServletResponse response, Model model) {

		String page = "widgets/includes/enrollmentStatusPerAvailableSitesChart";
		StudyDAO studyDAO = new StudyDAO(datasource);
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");
		UserAccountBean user = (UserAccountBean) request.getSession().getAttribute("userBean");
		String action = request.getParameter("action");
		int firstRowNum = Integer.parseInt(request.getParameter("firstRowNum"));
		if (action.equals("back")) {
			firstRowNum -= ESPAS_DISPLAY_PER_SCREEN;
		} else if (action.equals("forward")) {
			firstRowNum += ESPAS_DISPLAY_PER_SCREEN;
		}
		List<StudyBean> sites = getSitesInCurrentStudyWhereRoleIsMonitor(user, sb);
		ArrayList<WidgetsRowWithExtraField> rows = new ArrayList<WidgetsRowWithExtraField>();
		int sitesNum = sites.size();
		int lastRowNum = firstRowNum + ESPAS_DISPLAY_PER_SCREEN;

		for (int i = firstRowNum; i < lastRowNum && i < sitesNum; i++) {
			StudyBean site = sites.get(i);
			WidgetsRowWithExtraField currentRow = new WidgetsRowWithExtraField();
			currentRow.setRowName(site.getName());
			currentRow.setName(site.getIdentifier());
			currentRow.setRowValues(getEnrollmentPerSiteValues(site));
			currentRow.setExtraField(getPercentOfEnrollmentForSite(site));
			rows.add(currentRow);
		}
		model.addAttribute("rows", rows);
		model.addAttribute("firstRowNum", firstRowNum);
		model.addAttribute("showBack", firstRowNum > 0);
		model.addAttribute("showNext", firstRowNum + ESPS_DISPLAY_PER_SCREEN < sitesNum);
		return page;
	}

	/**
	 * Get legend for Enrollment Status per Available Sites.
	 *
	 * @param request is used to gather information about current user and study.
	 * @param response is used to set correct locale and clear cache.
	 * @throws IOException if data from request is incorrect or database contains corrupted data.
	 */
	@RequestMapping("/getESPASLegendValues")
	public void getESPASLegendValues(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		setRequestHeadersAndUpdateLocale(response, request);
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(datasource);
		UserAccountBean user = (UserAccountBean) request.getSession().getAttribute("userBean");
		StudyBean study = (StudyBean) request.getSession().getAttribute("study");
		List<StudyBean> sites = getSitesInCurrentStudyWhereRoleIsMonitor(user, study);
		LinkedHashMap<String, Integer> statusesMap = new LinkedHashMap<String, Integer>();

		for (Status status : LEGEND_SUBJECT_STATUSES) {
			int subjectsNum = status == Status.AUTO_DELETED ? statusesMap.get(Status.DELETED.getCode()) : 0;
			String key = status == Status.AUTO_DELETED ? Status.DELETED.getCode() : status.getCode();
			for (StudyBean site : sites) {
				subjectsNum += studySubjectDAO.getCountofStudySubjectsBasedOnStatus(site, status);
			}
			statusesMap.put(key, subjectsNum);
		}
		response.getWriter().println(new ArrayList<Integer>(statusesMap.values()));
	}

	private List<StudyBean> getSitesInCurrentStudyWhereRoleIsMonitor(UserAccountBean user, StudyBean currentStudy) {
		StudyDAO studyDAO = new StudyDAO(datasource);
		ArrayList<StudyUserRoleBean> userRoles = user.getRoles();
		List<StudyBean> sites = new ArrayList<StudyBean>();

		for (StudyUserRoleBean userRole : userRoles) {
			if (userRole.getRole() == Role.SITE_MONITOR) {
				int siteId = userRole.getStudyId();
				StudyBean site = (StudyBean) studyDAO.findByPK(siteId);
				if (site.getParentStudyId() == currentStudy.getParentStudyId()) {
					sites.add(site);
				}
			}
		}
		return sites;
	}

	private List<StudyBean> sortSitesByPercentOfExpectedEnrollment(List<StudyBean> listOfSites) {
		Collections.sort(listOfSites, new Comparator<StudyBean>() {
			public int compare(final StudyBean site1, final StudyBean site2) {
				int compareResult;
				StudySubjectDAO studySubjectDAO = new StudySubjectDAO(datasource);
				float site1ExpectedTotalEnrollment = site1.getExpectedTotalEnrollment();
				float site2ExpectedTotalEnrollment = site2.getExpectedTotalEnrollment();
				float subjectOnSite1 = studySubjectDAO.getCountofStudySubjects(site1);
				float subjectOnSite2 = studySubjectDAO.getCountofStudySubjects(site2);
				float site1PercentOfExpected = subjectOnSite1 / site1ExpectedTotalEnrollment * PERCENTS;
				float site2PercentOfExpected = subjectOnSite2 / site2ExpectedTotalEnrollment * PERCENTS;
				if (site1PercentOfExpected < site2PercentOfExpected) {
					compareResult = 1;
				} else if (site1PercentOfExpected > site2PercentOfExpected) {
					compareResult = -1;
				} else {
					compareResult = site1.getName().compareTo(site2.getName());
				}
				return compareResult;
			}
		});
		return listOfSites;
	}

	private LinkedHashMap<String, Integer> getEnrollmentPerSiteValues(StudyBean site) {
		LinkedHashMap<String, Integer> values = new LinkedHashMap<String, Integer>();
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(datasource);
		for (Status status : SUBJECTS_STATUSES) {
			if (status == Status.AUTO_DELETED) {
				int deleted = values.get(Status.DELETED.getCode());
				int autoRemoved = studySubjectDAO.getCountofStudySubjectsBasedOnStatus(site, status);
				values.put(Status.DELETED.getCode(), autoRemoved + deleted);
			} else {
				int countOfSubjectWithStatus = studySubjectDAO.getCountofStudySubjectsBasedOnStatus(site, status);
				values.put(status.getCode(), countOfSubjectWithStatus);
			}
		}
		return values;
	}

	private LinkedHashMap<String, LinkedHashMap<String, Integer>> getUpdateValueForMonth(
			LinkedHashMap<String, LinkedHashMap<String, Integer>> dataRows, String month, String status) {
		LinkedHashMap<String, Integer> updatedValues = dataRows.get(month);
		int currentValue = updatedValues.get(status);
		updatedValues.put(status, ++currentValue);
		dataRows.put(month, updatedValues);
		return dataRows;
	}

	private Integer getCountOfSubjects(StudyBean sb) {

		int countOfSubjects;
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(datasource);
		if (sb.isSite(sb.getParentStudyId())) {
			countOfSubjects = studySubjectDAO.getCountofStudySubjectsAtStudyOrSite(sb);
		} else {
			countOfSubjects = studySubjectDAO.getCountofStudySubjectsAtStudy(sb);
		}
		return countOfSubjects;
	}

	private List<StudyEventDefinitionBean> getListOfEventsDefinitions(StudyBean sb) {

		List<StudyEventDefinitionBean> studyEventDefinitions;
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(datasource);
		if (sb.isSite(sb.getParentStudyId())) {
			studyEventDefinitions = studyEventDefinitionDAO.findAllActiveByParentStudyId(sb.getParentStudyId());
		} else {
			studyEventDefinitions = studyEventDefinitionDAO.findAllActiveByStudyId(sb.getId());
		}
		return studyEventDefinitions;
	}

	private ArrayList<String> getMonthsList(HttpServletRequest request) {

		ArrayList<String> monthsList = new ArrayList<String>();
		for (int i = 1; i <= NUMBER_OF_MONTHS + 1; i++) {
			monthsList.add(messageSource.getMessage("short.month." + i, null, LocaleResolver.getLocale()));
		}
		return monthsList;
	}

	private void setRequestHeadersAndUpdateLocale(HttpServletResponse response, HttpServletRequest request) {

		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", -1);
		response.setHeader("Cache-Control", "no-store");
	}

	private String getPercentOfEnrollmentForSite(StudyBean site) {

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(0);
		float result = 0;
		if (site.isSite()) {
			StudySubjectDAO studySubjectDAO = new StudySubjectDAO(datasource);
			float expectedTotalEnrollment = site.getExpectedTotalEnrollment();
			float subjectOnSite = studySubjectDAO.getCountofStudySubjects(site);
			result = subjectOnSite / expectedTotalEnrollment * PERCENTS;
		}
		return df.format(result);
	}

	/**
	 * This class is designed to store information for Notes and Discrepancies per CRF widget, and display it on the
	 * page.
	 */
	class NDsPerCRFDisplay {

		private String crfName;
		private Integer countOfNds;

		public String getCrfName() {
			return crfName;
		}

		public void setCrfName(String crfName) {
			this.crfName = crfName;
		}

		public Integer getCountOfNds() {
			return countOfNds;
		}

		public void setCountOfNds(Integer countOfNds) {
			this.countOfNds = countOfNds;
		}
	}
}
