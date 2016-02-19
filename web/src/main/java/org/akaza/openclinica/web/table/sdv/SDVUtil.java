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

package org.akaza.openclinica.web.table.sdv;

import static org.jmesa.facade.TableFacadeFactory.createTableFacade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.controller.helper.table.SDVToolbar;
import org.akaza.openclinica.controller.helper.table.SubjectSDVContainer;
import org.akaza.openclinica.dao.EventCRFSDVFilter;
import org.akaza.openclinica.dao.EventCRFSDVSort;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.util.StudyEventUtil;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.util.ItemUtils;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.AbstractHtmlView;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.HtmlSnippets;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.component.HtmlTable;
import org.jmesa.view.html.editor.HtmlCellEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.CRFMaskingService;
import com.clinovo.service.ItemSDVService;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.DateUtil;
import com.clinovo.util.SubjectEventStatusUtil;

/**
 * A utility class that implements the details of the Source Data Verification (SDV) Jmesa tables.
 */
@Component
@SuppressWarnings({"unchecked", "unused"})
public class SDVUtil {

	public static final String VIEW_ICON_FORSUBJECT_PREFIX = "<a onmouseup=\"javascript:setImage('bt_View1','images/bt_View.gif');\" onmousedown=\"javascript:setImage('bt_View1','images/bt_View_d.gif');\" href=\"ViewStudySubject?id=";
	public static final String VIEW_ICON_HTML = "<img src=\"../images/bt_View.gif\" border=\"0\" />";
	public static final String ICON_FORCRFSTATUS_SUFFIX = ".gif'/>";
	public static final String ICON_FORSVN_SUFFIX = ".gif\" ";
	public static final String CHECKBOX_NAME = "sdvCheck_";
	public static final int FIFTEEN = 15;
	public static final int FIFTY = 50;
	public static final int TWENTY_FIVE = 25;
	public static final int EIGHT = 8;
	public static final int ZERO = 0;

	private ResourceBundle resformat;
	private String pathPrefix;

	@Autowired
	private CRFMaskingService maskingService;

	private class ColumnsInfo {
		private String[] allTitles;
		private String[] allColumns;
		private String[] propertyColumns;
		private String[] turnOffFiltersColumns;
		private String[] turnOffSortsColumns;
		private String[] formatColumns;
	}

	String getIconForSdvStatusPrefix() {
		String prefix = pathPrefix == null ? "../" : pathPrefix;
		return "<img hspace='2' border='0'  title='SDV Complete' alt='SDV Complete' src='" + prefix + "images/icon_";
	}

	String getIconForCrfStatusPrefix() {
		String prefix = pathPrefix == null ? "../" : pathPrefix;
		return "<img hspace='2' border='0'  title='Event CRF Status' alt='Event CRF Status' src='" + prefix
				+ "images/icon_";
	}

	String getIconForSubjectSufix() {
		String prefix = pathPrefix == null ? "../" : pathPrefix;
		return "\"><img hspace=\"6\" border=\"0\" align=\"left\" title=\"View\" alt=\"View\" src=\"" + prefix
				+ "images/bt_View.gif\" name=\"bt_View1\"/></a>";
	}

	public static final Map<Integer, String> CRF_STATUS_ICONS = new HashMap<Integer, String>();

	static {
		int ind = 0;
		CRF_STATUS_ICONS.put(ind++, "Invalid");
		CRF_STATUS_ICONS.put(ind++, "NotStarted");
		CRF_STATUS_ICONS.put(ind++, "InitialDE");
		CRF_STATUS_ICONS.put(ind++, "InitialDEComplete");
		CRF_STATUS_ICONS.put(ind++, "DDE");
		CRF_STATUS_ICONS.put(ind++, "DEcomplete");
		CRF_STATUS_ICONS.put(ind++, "InitialDE");
		CRF_STATUS_ICONS.put(ind, "Locked");
	}

	@Autowired
	private DataSource dataSource;

	public NoEscapeHtmlCellEditor getCellEditorNoEscapes() {
		return new NoEscapeHtmlCellEditor();
	}

	private String getDateFormat() {
		return resformat.getString("date_format_string");
	}

	/**
	 * Method that sets data and limit variables subjects.
	 * 
	 * @param tableFacade
	 *            TableFacade
	 * @param studyId
	 *            int
	 * @param studySubjectId
	 *            int
	 * @param request
	 *            HttpServletRequest
	 * @return int
	 */
	public int setDataAndLimitVariablesSubjects(TableFacade tableFacade, int studyId, int studySubjectId,
			HttpServletRequest request) {
		Limit limit = tableFacade.getLimit();
		FilterSet filterSet = limit.getFilterSet();
		int totalRows = getTotalRowCountSubjects(filterSet, studyId, studySubjectId);

		tableFacade.setTotalRows(totalRows);
		SortSet sortSet = limit.getSortSet();
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();
		Collection<SubjectSDVContainer> items = getFilteredItemsSubject(filterSet, sortSet, rowStart, rowEnd, studyId,
				studySubjectId, request);

		tableFacade.setItems(items);

		return totalRows;

	}

	/**
	 * Method returns total row count.
	 * 
	 * @param filterSet
	 *            FilterSet
	 * @param studyId
	 *            int
	 * @param studySubjectId
	 *            int
	 * @return int
	 */
	public int getTotalRowCountSubjects(FilterSet filterSet, int studyId, int studySubjectId) {

		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);

		if (filterSet.getFilters().size() == 0) {
			return eventCRFDAO.countEventCRFsByStudySubject(studySubjectId, studyId, studyId);
		}

		// Filter for study subject label
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
		StudySubjectBean studySubjectBean;
		studySubjectBean = (StudySubjectBean) studySubjectDAO.findByPK(studySubjectId);
		String label = studySubjectBean.getLabel();
		String eventNameValue = "";

		for (Filter filter : filterSet.getFilters()) {

			if (filter.getProperty().equalsIgnoreCase("eventName")) {
				eventNameValue = filter.getValue();
			}
		}

		if (eventNameValue.length() > 0) {
			return eventCRFDAO.countEventCRFsByEventNameSubjectLabel(eventNameValue, label);
		}

		return eventCRFDAO.countEventCRFsByStudySubject(studySubjectId, studyId, studyId);
	}

	/**
	 * Method that sets data and limit variables.
	 * 
	 * @param tableFacade
	 *            TableFacade
	 * @param studyId
	 *            int
	 * @param request
	 *            HttpServletRequest
	 */
	public void setDataAndLimitVariables(TableFacade tableFacade, int studyId, HttpServletRequest request) {

		Limit limit = tableFacade.getLimit();
		int pageNum = 0;
		int maxRows = 0;
		// Store pageNum and maxRows if they already exist in limit
		if (limit.getRowSelect() != null) {
			pageNum = limit.getRowSelect().getPage();
			maxRows = limit.getRowSelect().getMaxRows();
		}
		EventCRFSDVFilter eventCRFSDVFilter = getEventCRFSDVFilter(limit, studyId);
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		UserAccountBean user = getCurrentUser(request);
		boolean allowSdvWithOpenQueries = !"no".equals(currentStudy.getStudyParameterConfig()
				.getAllowSdvWithOpenQueries());
		int totalRows = getTotalRowCount(eventCRFSDVFilter, studyId, allowSdvWithOpenQueries, user.getId());
		// If maxRows was previously set, we need to set it again in the tableFacade before we
		// setTotalRows
		if (maxRows > 0) {
			tableFacade.setMaxRows(maxRows);
		}
		tableFacade.setTotalRows(totalRows);
		// If pageNum was previously set, it might have been lost during the setTotalRows operation
		// so we need to set it back
		if (pageNum > 0) {
			limit.getRowSelect().setPage(pageNum);
		}

		EventCRFSDVSort eventCRFSDVSort = getEventCRFSDVSort(limit);

		int rowStart = limit.getRowSelect().getRowStart();
		// to prevent problem, when url restoring old rowEnd value from the session, set rowEnd to 15
		int rowEnd = limit.getRowSelect().getRowEnd() < FIFTEEN ? FIFTEEN : limit.getRowSelect().getRowEnd();

		Collection<SubjectSDVContainer> items = getFilteredItems(eventCRFSDVFilter, eventCRFSDVSort, rowStart, rowEnd,
				studyId, currentStudy, request);

		tableFacade.setItems(items);
	}

	/**
	 * Method returns total row count.
	 * 
	 * @param eventCRFSDVFilter
	 *            EventCRFSDVFilter
	 * @param studyId
	 *            int
	 * @param allowSdvWithOpenQueries
	 *            boolean
	 * @param userId
	 *            int
	 * @return int
	 */
	public int getTotalRowCount(EventCRFSDVFilter eventCRFSDVFilter, int studyId, boolean allowSdvWithOpenQueries,
			int userId) {

		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		return eventCRFDAO.getCountOfAvailableWithFilter(studyId, eventCRFSDVFilter, allowSdvWithOpenQueries, userId);
	}

	protected EventCRFSDVFilter getEventCRFSDVFilter(Limit limit, Integer studyId) {
		EventCRFSDVFilter eventCRFSDVFilter = new EventCRFSDVFilter(studyId);
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			eventCRFSDVFilter.addFilter(property, value);
		}

		return eventCRFSDVFilter;
	}

	protected EventCRFSDVSort getEventCRFSDVSort(Limit limit) {
		EventCRFSDVSort eventCRFSDVSort = new EventCRFSDVSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			eventCRFSDVSort.addSort(property, order);
		}

		return eventCRFSDVSort;
	}

	private Collection<SubjectSDVContainer> getFilteredItems(EventCRFSDVFilter filterSet, EventCRFSDVSort sortSet,
			int rowStart, int rowEnd, int studyId, StudyBean currentStudy, HttpServletRequest request) {
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		UserAccountBean ub = getCurrentUser(request);
		boolean allowSdvWithOpenQueries = !"no".equals(currentStudy.getStudyParameterConfig()
				.getAllowSdvWithOpenQueries());

		List<EventCRFBean> eventCRFBeans = eventCRFDAO.getAvailableWithFilterAndSort(studyId, filterSet, sortSet,
				allowSdvWithOpenQueries, rowStart, rowEnd, ub.getId());
		return getSubjectRows(eventCRFBeans, request);
	}

	private Collection<SubjectSDVContainer> getFilteredItemsSubject(FilterSet filterSet, SortSet sortSet, int rowStart,
			int rowEnd, int studyId, int studySubjectId, HttpServletRequest request) {

		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		List<EventCRFBean> eventCRFBeans;

		String label;

		if (filterSet.getFilter("studySubjectId") != null) {

			label = filterSet.getFilter("studySubjectId").getValue().trim();
			eventCRFBeans = eventCRFDAO.getEventCRFsByStudySubjectLabelLimit(label, studyId, studyId,
					rowEnd - rowStart, rowStart);

		} else {
			eventCRFBeans = eventCRFDAO.getEventCRFsByStudySubjectLimit(studySubjectId, studyId, studyId, rowEnd
					- rowStart, rowStart);

		}

		return getSubjectRows(eventCRFBeans, request);
	}

	/**
	 * Method that renders EventCrf table with limit.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param studyId
	 *            int
	 * @param pathPrefix
	 *            String
	 * @return String
	 */
	public String renderEventCRFTableWithLimit(HttpServletRequest request, int studyId, String pathPrefix) {
		TableFacade tableFacade = createTableFacade("sdv", request);
		tableFacade.setStateAttr("sdv_restore");

		tableFacade.autoFilterAndSort(false);

		this.pathPrefix = pathPrefix;

		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");

		resformat = ResourceBundleProvider.getFormatBundle(LocaleResolver.getLocale(request));
		ResourceBundle resword = ResourceBundle.getBundle("org.akaza.openclinica.i18n.words",
				LocaleResolver.getLocale(request));

		String[] allTitles = new String[]{
				resword.getString("SDV_status"),
				currentStudy == null ? resword.getString("study_subject_ID") : currentStudy.getStudyParameterConfig()
						.getStudySubjectIdLabel(),
				resword.getString("site_id"),
				resword.getString("person_ID"),
				currentStudy == null ? resword.getString("secondary_ID") : currentStudy.getStudyParameterConfig()
						.getSecondaryIdLabel(),
				resword.getString("event_name"),
				resword.getString("event_date"),
				currentStudy == null ? resword.getString("enrollment_date") : currentStudy.getStudyParameterConfig()
						.getDateOfEnrollmentForStudyLabel(), resword.getString("subject_status"),
				resword.getString("CRF_name") + " / " + resword.getString("version"),
				resword.getString("SDV_requirement"), resword.getString("CRF_status"),
				resword.getString("last_updated_date"), resword.getString("last_updated_by"),
				resword.getString("study_event_status"), resword.getString("actions")};
		String[] allColumns = new String[]{"sdvStatus", "studySubjectId", "studyIdentifier", "personId", "secondaryId",
				"eventName", "eventDate", "enrollmentDate", "studySubjectStatus", "crfNameVersion",
				"sdvRequirementDefinition", "crfStatus", "lastUpdatedDate", "lastUpdatedBy", "studyEventStatus",
				"sdvStatusActions"};
		String[] propertyColumns = new String[]{"sdvStatus", "studySubjectId", "studyIdentifier", "personId",
				"secondaryId", "eventName", "eventDate", "enrollmentDate", "studySubjectStatus", "crfNameVersion",
				"sdvRequirementDefinition", "crfStatus", "lastUpdatedDate", "lastUpdatedBy", "studyEventStatus",
				"sdvStatusActions"};
		String[] formatColumns = new String[]{"eventDate", "enrollmentDate", "lastUpdatedDate"};
		String[] turnOffSortsColumns = new String[]{"sdvStatus", "studyIdentifier", "personId", "secondaryId",
				"eventName", "enrollmentDate", "studySubjectStatus", "crfNameVersion", "sdvRequirementDefinition",
				"crfStatus", "lastUpdatedDate", "lastUpdatedBy", "studyEventStatus", "sdvStatusActions"};
		String[] turnOffFiltersColumns = new String[]{"personId", "secondaryId", "enrollmentDate",
				"studySubjectStatus", "lastUpdatedDate", "lastUpdatedBy", "eventDate", "studyEventStatus"};

		if (currentStudy != null) {
			if (currentStudy.getStudyParameterConfig().getSecondaryIdRequired().equalsIgnoreCase("not_used")
					&& currentStudy.getStudyParameterConfig().getDateOfEnrollmentForStudyRequired()
							.equalsIgnoreCase("not_used")) {
				allColumns = new String[]{"sdvStatus", "studySubjectId", "studyIdentifier", "personId", "eventName",
						"eventDate", "studySubjectStatus", "crfNameVersion", "sdvRequirementDefinition", "crfStatus",
						"lastUpdatedDate", "lastUpdatedBy", "studyEventStatus", "sdvStatusActions"};
				propertyColumns = new String[]{"sdvStatus", "studySubjectId", "studyIdentifier", "personId",
						"eventName", "eventDate", "studySubjectStatus", "crfNameVersion", "sdvRequirementDefinition",
						"crfStatus", "lastUpdatedDate", "lastUpdatedBy", "studyEventStatus", "sdvStatusActions"};
				allTitles = new String[]{resword.getString("SDV_status"),
						currentStudy.getStudyParameterConfig().getStudySubjectIdLabel(), resword.getString("site_id"),
						resword.getString("person_ID"), resword.getString("event_name"),
						resword.getString("event_date"), resword.getString("subject_status"),
						resword.getString("CRF_name") + " / " + resword.getString("version"),
						resword.getString("SDV_requirement"), resword.getString("CRF_status"),
						resword.getString("last_updated_date"), resword.getString("last_updated_by"),
						resword.getString("study_event_status"), resword.getString("actions")};
				turnOffFiltersColumns = new String[]{"personId", "studySubjectStatus", "lastUpdatedDate",
						"lastUpdatedBy", "eventDate", "studyEventStatus"};
				turnOffSortsColumns = new String[]{"sdvStatus", "studyIdentifier", "personId", "eventName",
						"studySubjectStatus", "crfNameVersion", "sdvRequirementDefinition", "crfStatus",
						"lastUpdatedDate", "lastUpdatedBy", "studyEventStatus", "sdvStatusActions"};
				formatColumns = new String[]{"eventDate", "lastUpdatedDate"};
			} else if (currentStudy.getStudyParameterConfig().getSecondaryIdRequired().equalsIgnoreCase("not_used")) {
				allColumns = new String[]{"sdvStatus", "studySubjectId", "studyIdentifier", "personId", "eventName",
						"eventDate", "enrollmentDate", "studySubjectStatus", "crfNameVersion",
						"sdvRequirementDefinition", "crfStatus", "lastUpdatedDate", "lastUpdatedBy",
						"studyEventStatus", "sdvStatusActions"};
				propertyColumns = new String[]{"sdvStatus", "studySubjectId", "studyIdentifier", "personId",
						"eventName", "eventDate", "enrollmentDate", "studySubjectStatus", "crfNameVersion",
						"sdvRequirementDefinition", "crfStatus", "lastUpdatedDate", "lastUpdatedBy",
						"studyEventStatus", "sdvStatusActions"};
				allTitles = new String[]{resword.getString("SDV_status"),
						currentStudy.getStudyParameterConfig().getStudySubjectIdLabel(), resword.getString("site_id"),
						resword.getString("person_ID"), resword.getString("event_name"),
						resword.getString("event_date"),
						currentStudy.getStudyParameterConfig().getDateOfEnrollmentForStudyLabel(),
						resword.getString("subject_status"),
						resword.getString("CRF_name") + " / " + resword.getString("version"),
						resword.getString("SDV_requirement"), resword.getString("CRF_status"),
						resword.getString("last_updated_date"), resword.getString("last_updated_by"),
						resword.getString("study_event_status"), resword.getString("actions")};
				turnOffFiltersColumns = new String[]{"personId", "enrollmentDate", "studySubjectStatus",
						"lastUpdatedDate", "lastUpdatedBy", "eventDate", "studyEventStatus"};
				turnOffSortsColumns = new String[]{"sdvStatus", "studyIdentifier", "personId", "eventName",
						"enrollmentDate", "studySubjectStatus", "crfNameVersion", "sdvRequirementDefinition",
						"crfStatus", "lastUpdatedDate", "lastUpdatedBy", "studyEventStatus", "sdvStatusActions"};
			} else if (currentStudy.getStudyParameterConfig().getDateOfEnrollmentForStudyRequired()
					.equalsIgnoreCase("not_used")) {
				allColumns = new String[]{"sdvStatus", "studySubjectId", "studyIdentifier", "personId", "secondaryId",
						"eventName", "eventDate", "studySubjectStatus", "crfNameVersion", "sdvRequirementDefinition",
						"crfStatus", "lastUpdatedDate", "lastUpdatedBy", "studyEventStatus", "sdvStatusActions"};
				propertyColumns = new String[]{"sdvStatus", "studySubjectId", "studyIdentifier", "personId",
						"secondaryId", "eventName", "eventDate", "studySubjectStatus", "crfNameVersion",
						"sdvRequirementDefinition", "crfStatus", "lastUpdatedDate", "lastUpdatedBy",
						"studyEventStatus", "sdvStatusActions"};
				allTitles = new String[]{resword.getString("SDV_status"),
						currentStudy.getStudyParameterConfig().getStudySubjectIdLabel(), resword.getString("site_id"),
						resword.getString("person_ID"), currentStudy.getStudyParameterConfig().getSecondaryIdLabel(),
						resword.getString("event_name"), resword.getString("event_date"),
						resword.getString("subject_status"),
						resword.getString("CRF_name") + " / " + resword.getString("version"),
						resword.getString("SDV_requirement"), resword.getString("CRF_status"),
						resword.getString("last_updated_date"), resword.getString("last_updated_by"),
						resword.getString("study_event_status"), resword.getString("actions")};
				turnOffFiltersColumns = new String[]{"personId", "secondaryId", "studySubjectStatus",
						"lastUpdatedDate", "lastUpdatedBy", "eventDate", "studyEventStatus"};
				turnOffSortsColumns = new String[]{"sdvStatus", "studyIdentifier", "personId", "secondaryId",
						"eventName", "studySubjectStatus", "crfNameVersion", "sdvRequirementDefinition", "crfStatus",
						"lastUpdatedDate", "lastUpdatedBy", "studyEventStatus", "sdvStatusActions"};
				formatColumns = new String[]{"eventDate", "lastUpdatedDate"};
			}
		}

		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.allTitles = allTitles;
		columnsInfo.allColumns = allColumns;
		columnsInfo.propertyColumns = propertyColumns;
		columnsInfo.turnOffFiltersColumns = turnOffFiltersColumns;
		columnsInfo.turnOffSortsColumns = turnOffSortsColumns;
		columnsInfo.formatColumns = formatColumns;

		return renderEventCRFTableWithLimit(request, tableFacade, studyId, columnsInfo);
	}

	private String renderEventCRFTableWithLimit(HttpServletRequest request, TableFacade tableFacade, int studyId,
			ColumnsInfo columnsInfo) {
		boolean showMoreLink = Boolean.parseBoolean(request.getAttribute("showMoreLink") == null ? "false" : request
				.getAttribute("showMoreLink").toString());

		tableFacade.setColumnProperties(columnsInfo.propertyColumns);

		tableFacade.addFilterMatcher(new MatcherKey(String.class, "studySubjectStatus"), new SubjectStatusMatcher());

		tableFacade.addFilterMatcher(new MatcherKey(String.class, "crfStatus"), new CrfStatusMatcher());

		tableFacade.addFilterMatcher(new MatcherKey(String.class, "sdvStatus"), new SdvStatusMatcher());

		tableFacade.addFilterMatcher(new MatcherKey(String.class, "sdvRequirementDefinition"),
				new SDVRequirementMatcher());

		tableFacade.addFilterMatcher(new MatcherKey(String.class, "studyIdentifier"), new SDVSimpleMatcher());

		tableFacade.addFilterMatcher(new MatcherKey(String.class, "eventName"), new SDVEventNameMatcher());

		tableFacade.addFilterMatcher(new MatcherKey(String.class, "crfNameVersion"), new SDVSimpleMatcher());

		this.setDataAndLimitVariables(tableFacade, studyId, request);

		HtmlRow row = (HtmlRow) tableFacade.getTable().getRow();
		HtmlColumn studySubjectStatus = row.getColumn("studySubjectStatus");
		studySubjectStatus.getFilterRenderer().setFilterEditor(new SubjectStatusFilter());

		HtmlColumn crfStatus = row.getColumn("crfStatus");
		crfStatus.getFilterRenderer().setFilterEditor(new CrfStatusFilter());

		HtmlColumn actions = row.getColumn("sdvStatusActions");
		actions.getFilterRenderer().setFilterEditor(new DefaultActionsEditor(LocaleResolver.getLocale(request)));

		HtmlColumn sdvStatus = row.getColumn("sdvStatus");
		sdvStatus.getFilterRenderer().setFilterEditor(new SdvStatusFilter());

		HtmlColumn sdvRequirementDefinition = row.getColumn("sdvRequirementDefinition");
		sdvRequirementDefinition.getFilterRenderer().setFilterEditor(new SDVRequirementFilter());

		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		StudyDAO sdao = new StudyDAO(dataSource);
		StudyBean study = (StudyBean) sdao.findByPK(studyId);
		int parentStudyId = study.getParentStudyId() > 0 ? study.getParentStudyId() : studyId;

		List<String> studyIds = eventCRFDAO.getAvailableForSDVSiteNamesByStudyId(studyId);
		Collections.sort(studyIds);

		HtmlColumn studyIdentifier = row.getColumn("studyIdentifier");
		studyIdentifier.getFilterRenderer().setFilterEditor(new SDVSimpleListFilter(studyIds));

		List<String> eventNames = sortAsOnSubjectMatrix(eventCRFDAO.getAvailableForSDVEventNamesByStudyId(studyId),
				parentStudyId);

		HtmlColumn eventName = row.getColumn("eventName");
		eventName.getFilterRenderer().setFilterEditor(new SDVSimpleListFilter(eventNames));

		List<String> crfNames = eventCRFDAO.getAvailableForSDVCRFNamesByStudyId(studyId);
		Collections.sort(crfNames);

		HtmlColumn crfNameVersion = row.getColumn("crfNameVersion");
		crfNameVersion.getFilterRenderer().setFilterEditor(new SDVSimpleListFilter(crfNames));

		HtmlColumn actionsColumn = row.getColumn("sdvStatusActions");
		actionsColumn.getHeaderRenderer().setStyle("min-width:140px");

		// fix HTML in columns
		setHtmlCellEditors(tableFacade, columnsInfo.allColumns, true);

		// temporarily disable some of the filters for now
		turnOffFilters(tableFacade, columnsInfo.turnOffFiltersColumns);

		turnOffSorts(tableFacade, columnsInfo.turnOffSortsColumns);

		// Create the custom toolbar
		SDVToolbar sDVToolbar = new SDVToolbar(showMoreLink);

		// if(totalRowCount > 0){
		sDVToolbar.setMaxRowsIncrements(new int[]{FIFTEEN, TWENTY_FIVE, FIFTY});
		tableFacade.setToolbar(sDVToolbar);
		tableFacade.setView(new SDVView(LocaleResolver.getLocale(request), request));

		// Fix column titles
		HtmlTable table = (HtmlTable) tableFacade.getTable();

		setTitles(columnsInfo.allTitles, table);

		// format column dates
		formatColumns(table, columnsInfo.formatColumns, request);

		table.getTableRenderer().setWidth("800");
		return tableFacade.render();
	}

	private List<String> sortAsOnSubjectMatrix(List<String> eventNames, int parentStudyId) {
		ArrayList<String> result = new ArrayList<String>();
		for (StudyEventDefinitionBean sedb : StudyEventUtil.selectStudyEventsSortedLikeOnSubjectMatrix(parentStudyId,
				new StudyEventDefinitionDAO(dataSource), new StudyGroupClassDAO(dataSource))) {
			if (eventNames.contains(sedb.getName())) {
				result.add(sedb.getName());
			}
		}
		return result;
	}

	/**
	 * Method turns off filters.
	 * 
	 * @param tableFacade
	 *            TableFacade
	 * @param colNames
	 *            String[]
	 */
	public void turnOffFilters(TableFacade tableFacade, String[] colNames) {

		HtmlRow row = (HtmlRow) tableFacade.getTable().getRow();
		HtmlColumn col;

		for (String colName : colNames) {
			col = row.getColumn(colName);
			col.setFilterable(false);
		}

	}

	/**
	 * Method turns off sorts.
	 * 
	 * @param tableFacade
	 *            TableFacade
	 * @param colNames
	 *            String[]
	 */
	public void turnOffSorts(TableFacade tableFacade, String[] colNames) {

		HtmlRow row = (HtmlRow) tableFacade.getTable().getRow();
		HtmlColumn col;

		for (String colName : colNames) {
			col = row.getColumn(colName);
			col.setSortable(false);
		}

	}

	/**
	 * Method sets html cell editors.
	 * 
	 * @param tableFacade
	 *            TableFacade
	 * @param columnNames
	 *            String[]
	 * @param preventHtmlEscapes
	 *            boolean
	 */
	public void setHtmlCellEditors(TableFacade tableFacade, String[] columnNames, boolean preventHtmlEscapes) {

		HtmlRow row = ((HtmlTable) tableFacade.getTable()).getRow();
		HtmlColumn column;

		for (String col : columnNames) {

			column = row.getColumn(col);
			column.getCellRenderer().setCellEditor(this.getCellEditorNoEscapes());

		}

	}

	/**
	 * Method formats columns.
	 * 
	 * @param table
	 *            HtmlTable
	 * @param columnNames
	 *            String[]
	 * @param request
	 *            HttpServletRequest
	 */
	public void formatColumns(HtmlTable table, String[] columnNames, HttpServletRequest request) {
		LocaleResolver.resolveLocale();
		ResourceBundle bundle = ResourceBundleProvider.getFormatBundle();
		String format = bundle.getString("date_time_format_string");
		HtmlRow row = table.getRow();
		HtmlColumn column;

		for (String colName : columnNames) {
			column = row.getColumn(colName);
			if (column != null) {
				column.getCellRenderer().setCellEditor(new DateCellEditor(format));
			}
		}

	}

	/**
	 * Generate the rows for the study table. Each row represents an Event CRF.
	 *
	 * @param eventCRFBeans
	 *            List<EventCRFBean>
	 * @param request
	 *            HttpServletRequest
	 * @return Collection<SubjectSDVContainer>
	 */
	public Collection<SubjectSDVContainer> getSubjectRows(List<EventCRFBean> eventCRFBeans, HttpServletRequest request) {

		if (eventCRFBeans == null || eventCRFBeans.isEmpty()) {
			return new ArrayList<SubjectSDVContainer>();
		}

		getEventNamesForEventCRFs(eventCRFBeans);

		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
		SubjectDAO subjectDAO = new SubjectDAO(dataSource);
		StudyDAO studyDAO = new StudyDAO(dataSource);
		StudyEventDAO studyEventDAO = new StudyEventDAO(dataSource);
		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);

		StudySubjectBean studySubjectBean;
		SubjectBean subjectBean;
		StudyEventBean studyEventBean;
		StudyBean studyBean;
		EventDefinitionCRFBean eventDefinitionCRFBean;

		Collection<SubjectSDVContainer> allRows = new ArrayList<SubjectSDVContainer>();
		SubjectSDVContainer tempSDVBean;
		StringBuilder actions;

		for (EventCRFBean crfBean : eventCRFBeans) {
			tempSDVBean = new SubjectSDVContainer();

			studySubjectBean = (StudySubjectBean) studySubjectDAO.findByPK(crfBean.getStudySubjectId());
			studyEventBean = (StudyEventBean) studyEventDAO.findByPK(crfBean.getStudyEventId());
			subjectBean = (SubjectBean) subjectDAO.findByPK(studySubjectBean.getSubjectId());
			// find out the study's identifier
			studyBean = (StudyBean) studyDAO.findByPK(studySubjectBean.getStudyId());
			eventDefinitionCRFBean = eventDefinitionCRFDAO.findByStudyEventIdAndCRFVersionId(studyBean,
					studyEventBean.getId(), crfBean.getCRFVersionId());

			tempSDVBean.setStudyIdentifier(studyBean.getIdentifier());

			SourceDataVerification sourceData = eventDefinitionCRFBean.getSourceDataVerification();
			if (sourceData != null) {
				tempSDVBean.setSdvRequirementDefinition(sourceData.toString());
			} else {
				tempSDVBean.setSdvRequirementDefinition("");
			}

			tempSDVBean.setCrfNameVersion(getCRFName(crfBean.getCRFVersionId()) + "/ "
					+ getCRFVersionName(crfBean.getCRFVersionId()));

			boolean doNotAllowToSDV = studyEventBean.getSubjectEventStatus() == SubjectEventStatus.LOCKED
					|| studyEventBean.getSubjectEventStatus() == SubjectEventStatus.STOPPED
					|| studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SKIPPED;

			if (crfBean.getStatus() != null) {

				Integer status = crfBean.getStage().getId();

				if (doNotAllowToSDV) {
					status = DataEntryStage.LOCKED.getId();
				}

				tempSDVBean.setCrfStatus(getCRFStatusIconPath(status));
			}

			tempSDVBean.setStudyEventStatus(studyEventBean.getStatus().getName());

			if (studySubjectBean.getEnrollmentDate() != null) {
				tempSDVBean.setEnrollmentDate(DateUtil.printDate(studySubjectBean.getEnrollmentDate(),
						getCurrentUser(request).getUserTimeZoneId(), DateUtil.DatePattern.DATE,
						LocaleResolver.getLocale(request)));
			}

			if (crfBean.getCreatedDate() != null) {
				tempSDVBean.setEventDate(DateUtil.printDate(crfBean.getCreatedDate(), getCurrentUser(request)
						.getUserTimeZoneId(), DateUtil.DatePattern.DATE, LocaleResolver.getLocale(request)));
			}
			tempSDVBean.setEventName(crfBean.getEventName());
			// The checkbox is next to the study subject id
			StringBuilder sdvStatus = new StringBuilder("");
			if (crfBean.isSdvStatus()) {
				sdvStatus.append("<center><a href='javascript:void(0)' onclick='prompt(document.sdvForm, ");
				sdvStatus.append(crfBean.getId());
				sdvStatus.append(", this)'>");
				sdvStatus.append(getIconForSdvStatusPrefix()).append("DoubleCheck").append(ICON_FORCRFSTATUS_SUFFIX)
						.append("</a></center>");
			} else if (!doNotAllowToSDV) {
				sdvStatus.append("<center><input style='margin-right: 5px' type='checkbox' ")
						.append("class='sdvCheck'").append(" name='").append(CHECKBOX_NAME).append(crfBean.getId())
						.append("' onclick='setAccessedObjected(this)' /></center>");
			}
			tempSDVBean.setSdvStatus(sdvStatus.toString());
			tempSDVBean.setStudySubjectId(studySubjectBean.getLabel());

			if (subjectBean != null) {
				tempSDVBean.setPersonId(subjectBean.getUniqueIdentifier());
			} else {
				tempSDVBean.setPersonId("");

			}
			tempSDVBean.setSecondaryId(studySubjectBean.getSecondaryLabel());

			String statusName = studySubjectBean.getStatus().getName();
			if (statusName != null) {
				tempSDVBean.setStudySubjectStatus(statusName);
			}

			if (crfBean.getUpdatedDate() != null) {
				tempSDVBean.setLastUpdatedDate(DateUtil.printDate(crfBean.getUpdatedDate(), getCurrentUser(request)
						.getUserTimeZoneId(), DateUtil.DatePattern.DATE, LocaleResolver.getLocale(request)));
			} else {
				tempSDVBean.setLastUpdatedDate("unknown");

			}

			if (crfBean.getUpdater() != null) {

				tempSDVBean.setLastUpdatedBy(crfBean.getUpdater().getFirstName() + " "
						+ crfBean.getUpdater().getLastName());

			}

			actions = new StringBuilder("");
			actions.append(getViewCrfIcon(request, crfBean.getId(), studySubjectBean.getId()));
			if (!crfBean.isSdvStatus() && !doNotAllowToSDV) {
				actions.append("<input type=\"image\" name=\"sdvSubmit\" ").append("src=\"")
						.append((request.getContextPath())).append("/images/icon_DoubleCheck_Action")
						.append(ICON_FORSVN_SUFFIX).append("onclick=\"")
						.append("this.form.method='GET'; this.form.action='").append(request.getContextPath())
						.append("/pages/handleSDVGet").append("';").append("this.form.crfId.value='")
						.append(crfBean.getId()).append("';").append("this.form.submit(); setAccessedObjected(this);")
						.append("\" />");
			}

			tempSDVBean.setSdvStatusActions(actions.toString());
			allRows.add(tempSDVBean);

		}

		return allRows;
	}

	private String getViewCrfIcon(HttpServletRequest request, int eventDefinitionCRFId, int studySubjectId) {
		HtmlBuilder html = new HtmlBuilder();
		html.a()
				.onclick(
						"openDocWindow('" + request.getContextPath()
								+ "/ViewSectionDataEntry?cw=1&eventDefinitionCRFId=&eventCRFId=" + eventDefinitionCRFId
								+ "&tabId=1&studySubjectId=" + studySubjectId + "'); setAccessedObjected(this);")
				.append(" data-cc-sdvCrfId='").append(eventDefinitionCRFId).append("'");
		html.href("#").close().img().append(" hspace=\"4px\" src=\"../images/bt_View.gif\" border=\"0\"").close()
				.aEnd();
		return html.toString();
	}

	private String getCRFStatusIconPath(int statusId) {
		StringBuilder builder = new StringBuilder(getIconForCrfStatusPrefix());
		if (statusId > ZERO && statusId < EIGHT) {
			builder.append(CRF_STATUS_ICONS.get(statusId));
		} else {
			builder.append(CRF_STATUS_ICONS.get(0));
		}
		builder.append(ICON_FORCRFSTATUS_SUFFIX);
		builder.append(" ");
		builder.append("<input type=\"hidden\" statusId=\"").append(statusId).append("\" />");
		return builder.toString();
	}

	/**
	 * Method returns list of EventCrfIds to be SDV.
	 * 
	 * @param paramsContainingIds
	 *            Collection<String>
	 * @return List<Integer>
	 */
	public List<Integer> getListOfSdvEventCRFIds(Collection<String> paramsContainingIds) {

		List<Integer> eventCRFWithSDV = new ArrayList<Integer>();
		if (paramsContainingIds == null || paramsContainingIds.isEmpty()) {
			return eventCRFWithSDV;
		}
		int tmpInt;
		for (String param : paramsContainingIds) {
			tmpInt = stripPrefixFromParam(param);
			if (tmpInt != 0) {
				eventCRFWithSDV.add(tmpInt);
			}
		}

		return eventCRFWithSDV;
	}

	private int stripPrefixFromParam(String param) {
		if (param != null && param.contains(CHECKBOX_NAME)) {
			return Integer.parseInt(param.substring(param.indexOf("_") + 1));
		} else {
			return 0;
		}
	}

	/**
	 * Method returns list of StudySubjectIds.
	 * 
	 * @param paramsContainingIds
	 *            Set<String>
	 * @return List<Integer>
	 */
	public List<Integer> getListOfStudySubjectIds(Set<String> paramsContainingIds) {
		List<Integer> studySubjectIds = new ArrayList<Integer>();
		int tmpInt;

		if (paramsContainingIds == null || paramsContainingIds.isEmpty()) {
			return studySubjectIds;
		}
		for (String param : paramsContainingIds) {
			tmpInt = stripPrefixFromParam(param);
			if (tmpInt != 0) {
				studySubjectIds.add(tmpInt);
			}
		}
		return studySubjectIds;
	}

	/**
	 * Method sets event names for EventCRFs.
	 * 
	 * @param eventCRFBeans
	 *            List<EventCRFBean>
	 */
	public void getEventNamesForEventCRFs(List<EventCRFBean> eventCRFBeans) {
		if (eventCRFBeans == null || eventCRFBeans.isEmpty()) {
			return;
		}

		StudyEventDAO studyEventDAO = new StudyEventDAO(dataSource);
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);

		StudyEventBean studyEventBean;
		StudyEventDefinitionBean studyEventDefBean;
		// Provide a value for the eventName property of the EventCRF
		for (EventCRFBean eventCRFBean : eventCRFBeans) {
			if ("".equalsIgnoreCase(eventCRFBean.getEventName())) {
				studyEventBean = (StudyEventBean) studyEventDAO.findByPK(eventCRFBean.getStudyEventId());
				studyEventDefBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(studyEventBean
						.getStudyEventDefinitionId());
				eventCRFBean.setEventName(studyEventDefBean.getName() + "(" + studyEventBean.getSampleOrdinal() + ")");
			}
		}

	}

	/**
	 * Create the titles for the HTML table's rows.
	 * 
	 * @param allTitles
	 *            String[]
	 * @param table
	 *            HtmlTable
	 */
	public void setTitles(String[] allTitles, HtmlTable table) {
		HtmlRow row = table.getRow();
		HtmlColumn tempColumn;

		for (int i = 0; i < allTitles.length; i++) {
			tempColumn = row.getColumn(i);
			tempColumn.setTitle(allTitles[i]);
		}

	}

	/**
	 * Method returns crf name.
	 * 
	 * @param crfVersionId
	 *            int
	 * @return String
	 */
	public String getCRFName(int crfVersionId) {
		CRFVersionDAO cRFVersionDAO = new CRFVersionDAO(dataSource);
		CRFDAO cRFDAO = new CRFDAO(dataSource);

		CRFVersionBean versionBean = (CRFVersionBean) cRFVersionDAO.findByPK(crfVersionId);
		if (versionBean != null) {
			CRFBean crfBean = (CRFBean) cRFDAO.findByPK(versionBean.getCrfId());
			if (crfBean != null) {
				return crfBean.getName();
			}
		}

		return "";
	}

	/**
	 * Method returns crf version name.
	 * 
	 * @param crfVersionId
	 *            int
	 * @return String
	 */
	public String getCRFVersionName(int crfVersionId) {

		CRFVersionDAO cRFVersionDAO = new CRFVersionDAO(dataSource);
		CRFVersionBean versionBean = (CRFVersionBean) cRFVersionDAO.findByPK(crfVersionId);
		if (versionBean != null) {
			return versionBean.getName();
		}

		return "";

	}

	/**
	 * Method setts SDV status for StudySubjects.
	 * 
	 * @param studySubjectIds
	 *            List<Integer>
	 * @param userAccountBean
	 *            UserAccountBean
	 * @param isSdvWithOpenQueriesAllowed
	 *            boolean
	 * @param setVerification
	 *            boolean
	 * @param itemSDVService
	 *            ItemSDVService
	 * @return boolean
	 */
	public boolean setSDVStatusForStudySubjects(List<Integer> studySubjectIds, UserAccountBean userAccountBean,
			boolean isSdvWithOpenQueriesAllowed, boolean setVerification, ItemSDVService itemSDVService) {

		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
		EventDefinitionCRFDAO eventDefinitionCrfDAO = new EventDefinitionCRFDAO(dataSource);
		StudyEventDAO studyEventDAO = new StudyEventDAO(dataSource);
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
		StudyDAO studyDAO = new StudyDAO(dataSource);
		CRFVersionDAO cvdao = new CRFVersionDAO(dataSource);
		ItemDataDAO itemDataDao = new ItemDataDAO(dataSource);
		DAOWrapper daoWrapper = new DAOWrapper(dataSource);

		if (studySubjectIds == null || studySubjectIds.isEmpty()) {
			return true;
		}

		List<Integer> exceptedEventCrfIdForSubjectList = new ArrayList<Integer>();
		for (Integer studySubjectId : studySubjectIds) {
			if (!isSdvWithOpenQueriesAllowed) {
				exceptedEventCrfIdForSubjectList = dndao.findAllEvCRFIdsWithUnclosedDNsByStSubId(studySubjectId);
			}
			Set<Integer> ignoredStudyEvents = new HashSet<Integer>();
			ArrayList<EventCRFBean> eventCrfs = eventCRFDAO.getEventCRFsByStudySubjectCompleteOrLocked(studySubjectId);
			StudySubjectBean studySubject = (StudySubjectBean) studySubjectDAO.findByPK(studySubjectId);
			List<StudyEventBean> studyEvents = studyEventDAO.findAllByStudySubject(studySubject);
			Iterator<StudyEventBean> iterator = studyEvents.iterator();
			while (iterator.hasNext()) {
				StudyEventBean studyEventBean = iterator.next();
				if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.LOCKED
						|| studyEventBean.getSubjectEventStatus() == SubjectEventStatus.STOPPED
						|| studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SKIPPED) {
					ignoredStudyEvents.add(studyEventBean.getId());
					iterator.remove();
				}
			}
			for (EventCRFBean eventCRFBean : eventCrfs) {
				if (ignoredStudyEvents.contains(eventCRFBean.getStudyEventId())
						|| (!isSdvWithOpenQueriesAllowed && exceptedEventCrfIdForSubjectList.contains(eventCRFBean
								.getId())) || maskingService.isEventCRFMasked(eventCRFBean.getId(), userAccountBean.getId(), userAccountBean.getActiveStudyId())) {
					continue;
				}
				try {
					itemSDVService.sdvCrfItems(eventCRFBean.getId(), userAccountBean.getId(), setVerification);
					eventCRFDAO.setSDVStatus(setVerification, userAccountBean.getId(), eventCRFBean.getId());
				} catch (Exception exc) {
					System.out.println(exc.getMessage());
					return false;
				}

			}
			studySubjectDAO.update(studySubject);

			SubjectEventStatusUtil.determineSubjectEventStates(studyEvents, userAccountBean, daoWrapper, null);
		}

		return true;
	}

	/**
	 * Method make event crfs SDVed.
	 * 
	 * @param eventCRFIds
	 *            List<Integer>
	 * @param userAccountBean
	 *            UserAccountBean
	 * @param setVerification
	 *            boolean
	 * @param itemSDVService
	 *            ItemSDVService
	 * @return boolean
	 */
	public boolean setSDVerified(List<Integer> eventCRFIds, UserAccountBean userAccountBean, boolean setVerification,
			ItemSDVService itemSDVService) {

		// If no event CRFs are offered to SDV, then the transaction has not
		// caused a problem, so return true
		if (eventCRFIds == null || eventCRFIds.isEmpty()) {
			return true;
		}

		ItemDataDAO itemDataDao = new ItemDataDAO(dataSource);
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(dataSource);
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
		StudyEventDAO studyEventDAO = new StudyEventDAO(dataSource);
		StudyDAO studyDAO = new StudyDAO(dataSource);
		CRFVersionDAO cvdao = new CRFVersionDAO(dataSource);
		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		DAOWrapper daoWrapper = new DAOWrapper(dataSource);

		for (Integer eventCrfId : eventCRFIds) {
			try {
				itemSDVService.sdvCrfItems(eventCrfId, userAccountBean.getId(), setVerification);
				eventCRFDAO.setSDVStatus(setVerification, userAccountBean.getId(), eventCrfId);
				EventCRFBean ec = (EventCRFBean) eventCRFDAO.findByPK(eventCrfId);
				StudyEventBean se = (StudyEventBean) studyEventDAO.findByPK(ec.getStudyEventId());
				StudySubjectBean ss = (StudySubjectBean) studySubjectDAO.findByPK(se.getStudySubjectId());
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(se
						.getStudyEventDefinitionId());
				SubjectEventStatusUtil.determineSubjectEventStates(sed, ss, userAccountBean, daoWrapper);
			} catch (Exception exc) {
				System.out.println(exc.getMessage());
				return false;
			}
		}

		return true;
	}

	/**
	 * Methdo forwards request from controller.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param path
	 *            String
	 */
	public void forwardRequestFromController(HttpServletRequest request, HttpServletResponse response, String path) {
		try {
			request.getRequestDispatcher(path).forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Collection<SubjectSDVContainer> getSubjectAggregateRows(List<StudySubjectBean> studySubjectBeans) {

		if (studySubjectBeans == null || studySubjectBeans.isEmpty()) {
			return new ArrayList<SubjectSDVContainer>();
		}
		Collection<SubjectSDVContainer> allRows = new ArrayList<SubjectSDVContainer>();
		SubjectSDVContainer tempSDVBean;

		// The first row is the "select all" checkbox row
		tempSDVBean = new SubjectSDVContainer();
		String firstRowActions = "Select All <input type=checkbox name='checkAll' onclick='selectAllChecks(this.form)'/>";
		tempSDVBean.setSdvStatusActions(firstRowActions);
		allRows.add(tempSDVBean);
		StringBuilder actions;

		for (StudySubjectBean studySubjectBean : studySubjectBeans) {
			tempSDVBean = new SubjectSDVContainer();
			tempSDVBean.setStudySubjectId(studySubjectBean.getId() + "");
			tempSDVBean.setStudySubjectStatus("subject status");
			tempSDVBean.setNumberOfCRFsSDV("0");
			tempSDVBean.setPercentageOfCRFsSDV("0");
			tempSDVBean.setGroup("group");
			actions = new StringBuilder("<input class='sdvCheckbox' type='checkbox' name=");
			actions.append("'sdvCheck").append(studySubjectBean.getId()).append("'/>&nbsp;&nbsp");
			actions.append(VIEW_ICON_FORSUBJECT_PREFIX).append(studySubjectBean.getId())
					.append(getIconForSubjectSufix());
			tempSDVBean.setSdvStatusActions(actions.toString());

			allRows.add(tempSDVBean);

		}

		return allRows;

	}

	private List<EventCRFBean> getAllEventCRFs(List<StudyEventBean> studyEventBeans) {
		List<EventCRFBean> studyEventCRFBeans = new ArrayList<EventCRFBean>();

		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);

		for (StudyEventBean studyEventBean : studyEventBeans) {
			List<EventCRFBean> eventCRFBeans = eventCRFDAO.findAllByStudyEvent(studyEventBean);
			if (eventCRFBeans != null && !eventCRFBeans.isEmpty()) {
				studyEventCRFBeans.addAll(eventCRFBeans);
			}
		}

		return studyEventCRFBeans;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	class SDVView extends AbstractHtmlView {

		private final ResourceBundle resword;
		private boolean showTitle = false;

		public SDVView(Locale locale, HttpServletRequest request) {
			resword = ResourceBundleProvider.getWordsBundle(locale);
			if (request.getRequestURI().contains("MainMenu")) {
				showTitle = true;
			}
		}

		public Object render() {
			HtmlSnippets snippets = getHtmlSnippets();
			HtmlBuilder html = new HtmlBuilder();
			html.append(snippets.themeStart());

			html.append(snippets.tableStart());

			html.append(snippets.theadStart());
			html.append(customHeader());
			html.append(snippets.toolbar());
			html.append(selectAll());

			html.append(snippets.header());
			html.append(snippets.filter());

			html.append(snippets.tbodyStart());

			html.append(snippets.body());

			html.append(snippets.theadEnd());
			html.append(snippets.footer());
			html.append(snippets.statusBar());
			html.append(snippets.tableEnd());
			html.append(snippets.themeEnd());
			html.append(snippets.initJavascriptLimit());
			return html.toString();
		}

		String selectAll() {
			HtmlBuilder html = new HtmlBuilder();
			html.tr(1).styleClass("logic").close().td(1).colspan("100%").style("font-size: 12px;").close();
			html.append("<b>" + resword.getString("table_sdv_select") + "</b>&#160;&#160;");
			html.append("<a name='checkSDVAll' href='javascript:selectAllChecks(document.sdvForm,true)'>"
					+ resword.getString("table_sdv_all"));
			html.append(",</a>");
			html.append("&#160;&#160;&#160;");
			html.append("<a name='checkSDVAll' href='javascript:selectAllChecks(document.sdvForm,false)'>"
					+ resword.getString("table_sdv_none"));
			html.append("</a>");
			html.tdEnd().trEnd(1);
			return html.toString();
		}

		private String customHeader() {
			if (showTitle) {
				HtmlBuilder html = new HtmlBuilder();

				html.tr(0).styleClass("header").width("100%").close();
				html.td(0).colspan("100%")
						.style("border-bottom: 1px solid white;background-color:white;color:black;font-size:12px;")
						.align("left").close().append(resword.getString("source_data_verification")).tdEnd().trEnd(0);

				return html.toString();
			} else {
				return "";
			}
		}
	}

	class NoEscapeHtmlCellEditor extends HtmlCellEditor {
		@Override
		public Object getValue(Object item, String property, int rowCount) {
			return ItemUtils.getItemValue(item, property);
		}
	}

	private UserAccountBean getCurrentUser(HttpServletRequest request) {
		return (UserAccountBean) request.getSession().getAttribute(SpringController.USER_BEAN_NAME);
	}
}
