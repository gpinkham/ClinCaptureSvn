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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.controller.helper.table.SDVToolbarSubject;
import org.akaza.openclinica.controller.helper.table.SubjectAggregateContainer;
import org.akaza.openclinica.dao.StudySubjectSDVFilter;
import org.akaza.openclinica.dao.StudySubjectSDVSort;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Row;
import org.jmesa.view.html.AbstractHtmlView;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.HtmlSnippets;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.component.HtmlTable;
import org.jmesa.web.WebContext;
import org.springframework.stereotype.Component;

/**
 * A Jmesa table that represents study subjects in each row.
 */
@Component
@SuppressWarnings({ "unchecked" })
public class SubjectIdSDVFactory extends AbstractTableFactory {

	private DataSource dataSource;

	private int studyId;
	private boolean showBackButton;
	private String contextPath;
	private ResourceBundle resword;
	private final static String ICON_FORCRFSTATUS_SUFFIX = ".gif'/>";
	public boolean showMoreLink;

	public SubjectIdSDVFactory() {
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	protected String getTableName() {
		// This name creates the underlying id of the HTML table
		return "s_sdv";
	}

	@Override
	public void configureTableFacadeCustomView(TableFacade tableFacade) {
		tableFacade.setView(new SubjectSDVView(getLocale()));
	}

	@Override
	protected void configureColumns(TableFacade tableFacade, Locale locale) {

		tableFacade.setColumnProperties("sdvStatus", "studySubjectId", "siteId", "personId", "studySubjectStatus",
				"group", "numberCRFComplete", "numberOfCRFsSDV", "totalEventCRF", "actions");

		resword = ResourceBundleProvider.getWordsBundle(locale);

		Row row = tableFacade.getTable().getRow();

		StudyBean currentStudy = (StudyBean) tableFacade.getWebContext().getSessionAttribute("study");

		SDVUtil sdvUtil = new SDVUtil();
		String[] allTitles = new String[] {
				resword.getString("SDV_status"),
				currentStudy != null ? currentStudy.getStudyParameterConfig().getStudySubjectIdLabel() : resword
						.getString("study_subject_ID"), resword.getString("site_id"), resword.getString("person_ID"),
				resword.getString("study_subject_status"), resword.getString("group"),
				resword.getString("num_CRFs_completed"), resword.getString("num_CRFs_SDV"),
				resword.getString("total_events_CRF"), resword.getString("actions") };

		sdvUtil.setTitles(allTitles, (HtmlTable) tableFacade.getTable());
		sdvUtil.turnOffFilters(tableFacade, new String[] { "personId", "studySubjectStatus", "group",
				"numberCRFComplete", "numberOfCRFsSDV", "totalEventCRF", "actions" });
		sdvUtil.turnOffSorts(tableFacade, new String[] { "sdvStatus", "personId", "studySubjectStatus", "group",
				"numberCRFComplete", "numberOfCRFsSDV", "totalEventCRF" });

		sdvUtil.setHtmlCellEditors(tableFacade, new String[] { "sdvStatus", "actions" }, false);

		HtmlColumn sdvStatus = ((HtmlRow) row).getColumn("sdvStatus");
		sdvStatus.getFilterRenderer().setFilterEditor(new SdvStatusFilter());

		// siteId-filter
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		List<String> studyIds = eventCRFDAO.getAvailableForSDVSiteNamesByStudyId(studyId);
		Collections.sort(studyIds);

		HtmlColumn studyIdentifier = ((HtmlRow) row).getColumn("siteId");
		studyIdentifier.getFilterRenderer().setFilterEditor(new SDVSimpleListFilter(studyIds));

		String actionsHeader = resword.getString("rule_actions")
				+ "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
		configureColumn(row.getColumn("actions"), actionsHeader, sdvUtil.getCellEditorNoEscapes(),
				new DefaultActionsEditor(locale), true, false);

	}

	@Override
	public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
		super.configureTableFacade(response, tableFacade);

		tableFacade.addFilterMatcher(new MatcherKey(String.class, "sdvStatus"), new SdvStatusMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "siteId"), new SDVSimpleMatcher());

	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {

		Limit limit = tableFacade.getLimit();

		StudySubjectSDVFilter studySubjectSDVFilter = getStudySubjectSDVFilter(limit);
		WebContext context = tableFacade.getWebContext();
		if (context != null) {
			showBackButton = context.getParameter("sbb") != null;
			studyId = Integer.parseInt(context.getParameter("studyId"));
			contextPath = context.getContextPath();
		}

		String restore = (context != null ? context.getRequestAttribute(limit.getId() + "_restore") : null) + "";
		if (!limit.isComplete()) {
			int totalRows = getTotalRowCount(studySubjectSDVFilter);
			tableFacade.setTotalRows(totalRows);
		} else if ("true".equalsIgnoreCase(restore)) {
			int totalRows = getTotalRowCount(studySubjectSDVFilter);
			int pageNum = limit.getRowSelect().getPage();
			int maxRows = limit.getRowSelect().getMaxRows();
			tableFacade.setMaxRows(maxRows);
			tableFacade.setTotalRows(totalRows);
			limit.getRowSelect().setPage(pageNum);
		}

		StudySubjectSDVSort studySubjectSDVSort = getStudySubjectSDVSort(limit);

		int rowStart = limit.getRowSelect().getRowStart();
		int page = limit.getRowSelect().getPage();
		int pageSize = limit.getRowSelect().getMaxRows();
		StudyBean currentStudy = (StudyBean) tableFacade.getWebContext().getSessionAttribute("study");
		Collection<SubjectAggregateContainer> items = getFilteredItems(currentStudy, studySubjectSDVFilter,
				studySubjectSDVSort, rowStart, page * pageSize);
		tableFacade.setItems(items);

	}

	protected StudySubjectSDVFilter getStudySubjectSDVFilter(Limit limit) {
		StudySubjectSDVFilter studySubjectSDVFilter = new StudySubjectSDVFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			studySubjectSDVFilter.addFilter(property, value);
		}

		return studySubjectSDVFilter;
	}

	public StudySubjectSDVFilter createStudySubjectSDVFilter(Limit limit) {
		return getStudySubjectSDVFilter(limit);
	}

	protected StudySubjectSDVSort getStudySubjectSDVSort(Limit limit) {
		StudySubjectSDVSort studySubjectSDVSort = new StudySubjectSDVSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			studySubjectSDVSort.addSort(property, order);
		}

		return studySubjectSDVSort;
	}

	/*
	 * Returns how many subjects exist in the study.
	 */
	public int getTotalRowCount(StudySubjectSDVFilter studySubjectSDVFilter) {

		StudySubjectDAO studySubDAO = new StudySubjectDAO(dataSource);
		return studySubDAO.countAllByStudySDV(studyId, studyId, studySubjectSDVFilter);

	}

	@Override
	public void configureTableFacadePostColumnConfiguration(TableFacade tableFacade) {
		tableFacade.setToolbar(new SDVToolbarSubject(showMoreLink));
	}

	private Collection<SubjectAggregateContainer> getFilteredItems(StudyBean currentStudy,
			StudySubjectSDVFilter filterSet, StudySubjectSDVSort sortSet, int rowStart, int rowEnd) {
		List<SubjectAggregateContainer> rows = new ArrayList<SubjectAggregateContainer>();
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
		if (sortSet.getSorts().size() == 0) {
			sortSet.addSort("studySubject.createdDate", "desc");
		}
		List<StudySubjectBean> studySubjectBeans = studySubjectDAO.findAllByStudySDV(studyId, studyId, filterSet,
				sortSet, rowStart, rowEnd);

		for (StudySubjectBean studSubjBean : studySubjectBeans) {
			rows.add(getRow(studSubjBean, currentStudy));
		}

		return rows;
	}

	String getIconForCrfStatusPrefix() {
		String prefix = "../";
		return "<img hspace='2' border='0'  title='SDV Complete' alt='SDV Status' src='" + prefix + "images/icon_";
	}

	private SubjectAggregateContainer getRow(StudySubjectBean studySubjectBean, StudyBean currentStudy) {
		SubjectAggregateContainer row = new SubjectAggregateContainer();
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		StudyDAO studyDAO = new StudyDAO(dataSource);
		StudyGroupDAO studyGroupDAO = new StudyGroupDAO(dataSource);
		DiscrepancyNoteDAO discrepancyNoteDAO = new DiscrepancyNoteDAO(dataSource);

		row.setStudySubjectId(studySubjectBean.getLabel());
		row.setPersonId(studySubjectBean.getUniqueIdentifier());
		row.setStudySubjectStatus(studySubjectBean.getStatus().getName());

		StudyBean studyBean = (StudyBean) studyDAO.findByPK(studySubjectBean.getStudyId());
		row.setSiteId(studyBean.getIdentifier());

		List<EventCRFBean> eventCRFBeans = eventCRFDAO.getEventCRFsWithNonLockedCRFsByStudySubject(
				studySubjectBean.getId(), studySubjectBean.getStudyId(), studySubjectBean.getStudyId());
		int numberEventCRFs = eventCRFBeans.size();
		row.setTotalEventCRF(numberEventCRFs + "");

		HashMap<String, Integer> stats = getEventCRFStats(eventCRFBeans, studyBean, studySubjectBean);

		int numberOfCompletedEventCRFs = stats.get("numberOfCompletedEventCRFs");
		int numberOfSDVdEventCRFs = stats.get("numberOfSDVdEventCRFs");
		boolean studySubjectSDVd = stats.get("studySubjectSDVd") == 1;
		boolean shouldDisplaySDVButton = stats.get("shouldDisplaySDVButton") == 1;
		String allowSdvWithOpenQueries = currentStudy.getStudyParameterConfig().getAllowSdvWithOpenQueries();
		boolean subjectHasUnclosedNDsInStudy = allowSdvWithOpenQueries.equals("no")
				&& discrepancyNoteDAO.doesSubjectHaveUnclosedNDsInStudy(studyBean, studySubjectBean.getLabel());

		row.setNumberCRFComplete(numberOfCompletedEventCRFs + "");
		row.setNumberOfCRFsSDV(numberOfSDVdEventCRFs + "");

		StringBuilder sdvStatus = new StringBuilder("");
		if (studySubjectSDVd || (numberOfSDVdEventCRFs > 0 && numberOfCompletedEventCRFs == numberOfSDVdEventCRFs)) {
			sdvStatus.append("<center><a href='javascript:void(0)' onclick='prompt(document.sdvForm,");
			sdvStatus.append(studySubjectBean.getId());
			sdvStatus.append(")'>");
			sdvStatus.append(getIconForCrfStatusPrefix()).append("DoubleCheck").append(ICON_FORCRFSTATUS_SUFFIX)
					.append("</a></center>");
		} else {
			if (numberOfCompletedEventCRFs > 0 && !subjectHasUnclosedNDsInStudy) {
				sdvStatus.append("<center><input style='margin-right: 5px' type='checkbox' ")
						.append("class='sdvCheck'").append(" name='").append("sdvCheck_")
						.append(studySubjectBean.getId()).append("' /></center>");
			}

		}
		row.setSdvStatus(sdvStatus.toString());

		List<StudyGroupBean> studyGroupBeans = studyGroupDAO.getGroupByStudySubject(studySubjectBean.getId(),
				studySubjectBean.getStudyId(), studySubjectBean.getStudyId());

		if (studyGroupBeans != null && !studyGroupBeans.isEmpty()) {
			row.setGroup(studyGroupBeans.get(0).getName());
		}
		StringBuilder actions = new StringBuilder("<table><tr><td>");
		StringBuilder urlPrefix = new StringBuilder("<a href=\"");
		StringBuilder path = new StringBuilder(contextPath)
				.append("/pages/viewAllSubjectSDVtmp?" + (showBackButton ? "sbb=true&" : "") + "studyId=")
				.append(studyId).append("&sdv_f_studySubjectId=");
		path.append(studySubjectBean.getLabel());
		urlPrefix.append(path).append("\">");
		actions.append(urlPrefix).append(SDVUtil.VIEW_ICON_HTML).append("</a></td>");

		if (!studySubjectSDVd && shouldDisplaySDVButton && numberOfCompletedEventCRFs > 0
				&& !subjectHasUnclosedNDsInStudy) {
			StringBuilder jsCodeString = new StringBuilder("this.form.method='GET'; this.form.action='")
					.append(contextPath).append("/pages/sdvStudySubject").append("';")
					.append("this.form.theStudySubjectId.value='").append(studySubjectBean.getId()).append("';")
					.append("this.form.submit();");

			actions.append("<td><input type=\"image\" src=\"").append(contextPath)
					.append("/images/icon_DoubleCheck_Action.gif\"").append(" name=\"sdvSubmit\" ")
					.append("onclick=\"").append(jsCodeString.toString()).append("\" /></td>");
		} else if (!studySubjectSDVd) {
			actions.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
		}
		actions.append("</tr></table>");

		row.setActions(actions.toString());

		return row;

	}

	private HashMap<String, Integer> getEventCRFStats(List<EventCRFBean> eventCRFBeans, StudyBean studyBean,
			StudySubjectBean studySubject) {
		StudyEventDAO studyEventDAO = new StudyEventDAO(dataSource);
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		EventDefinitionCRFDAO eventDefinitionCrfDAO = new EventDefinitionCRFDAO(dataSource);
		CRFDAO crfDAO = new CRFDAO(dataSource);
		StudyEventBean studyEventBean;
		Integer numberOfCompletedEventCRFs = 0;
		Integer numberOfSDVdEventCRFs = 0;
		List<Integer> eventCRFDefIds = eventDefinitionCrfDAO.getRequiredEventCRFDefIdsThatShouldBeSDVd(studyBean);
		List<Integer> eventCRFDefIdsCopy = new ArrayList<Integer>(eventCRFDefIds);
		boolean canNotMarkAsSDVd = eventCRFDefIds.size() == 0;
		for (EventCRFBean eventBean : eventCRFBeans) {
			studyEventBean = (StudyEventBean) studyEventDAO.findByPK(eventBean.getStudyEventId());
			StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
					.findByPK(studyEventBean.getStudyEventDefinitionId());
			if (!studyEventDefinitionBean.getStatus().isAvailable()) {
				continue;
			}
			CRFBean crfBean = crfDAO.findByVersionId(eventBean.getCRFVersionId());
			EventDefinitionCRFBean eventDefinitionCrf = eventDefinitionCrfDAO
					.findByStudyEventDefinitionIdAndCRFIdAndStudyId(studyEventBean.getStudyEventDefinitionId(),
							crfBean.getId(), studySubject.getStudyId());
			if (eventDefinitionCrf.getId() == 0) {
				eventDefinitionCrf = eventDefinitionCrfDAO.findForStudyByStudyEventDefinitionIdAndCRFId(
						studyEventBean.getStudyEventDefinitionId(), crfBean.getId());
			}
			// get number of completed event crfs
			if (eventBean.getStage() == DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) {
				numberOfCompletedEventCRFs++;
			}
			// get number of completed event SDVd eventeventDefinitionCrfDAOs
			if (eventBean.isSdvStatus()) {
				numberOfSDVdEventCRFs++;
			}
			if (eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.AllREQUIRED
					|| eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED) {
				if (eventBean.isSdvStatus()) {
					eventCRFDefIds.remove((Integer) eventDefinitionCrf.getId());
					eventCRFDefIdsCopy.remove((Integer) eventDefinitionCrf.getId());
				}
				if (eventBean.getStatus().getId() == Status.UNAVAILABLE.getId() && eventBean.getDateCompleted() != null) {
					eventCRFDefIdsCopy.remove((Integer) eventDefinitionCrf.getId());
				} else {
					canNotMarkAsSDVd = true;
				}
			}
		}
		HashMap<String, Integer> stats = new HashMap<String, Integer>();
		stats.put("numberOfCompletedEventCRFs", numberOfCompletedEventCRFs);
		stats.put("numberOfSDVdEventCRFs", numberOfSDVdEventCRFs);
		stats.put("studySubjectSDVd", !canNotMarkAsSDVd && eventCRFDefIds.size() == 0 ? 1 : 0);
		stats.put("shouldDisplaySDVButton", !canNotMarkAsSDVd && eventCRFDefIdsCopy.size() == 0 ? 1 : 0);
		return stats;
	}

	class SubjectSDVView extends AbstractHtmlView {

		private final ResourceBundle resword;

		public SubjectSDVView(Locale locale) {
			resword = ResourceBundleProvider.getWordsBundle(locale);
		}

		public Object render() {
			HtmlSnippets snippets = getHtmlSnippets();
			HtmlBuilder html = new HtmlBuilder();
			html.append(snippets.themeStart());
			html.append(snippets.tableStart());
			html.append(snippets.theadStart());
			html.append(snippets.toolbar());
			html.append(selectAll());
			html.append(snippets.header());
			html.append(snippets.filter());
			html.append(snippets.theadEnd());
			html.append(snippets.tbodyStart());
			html.append(snippets.body());
			html.append(snippets.tbodyEnd());
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
	}
}
