/******************************************************************************
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

package org.akaza.openclinica.control.submit;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.tag.SDVStudySubjectLinkTag;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.DateUtil;
import com.clinovo.util.SignUtil;
import com.clinovo.util.SubjectEventStatusUtil;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.control.ListStudyView;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.FindSubjectsFilter;
import org.akaza.openclinica.dao.managestudy.FindSubjectsSort;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.util.SubjectLabelNormalizer;
import org.akaza.openclinica.view.Page;
import org.apache.commons.lang.StringUtils;
import org.jmesa.core.filter.FilterMatcher;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Order;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.editor.DroplistFilterEditor;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * ListStudySubjectTableFactory class.
 */
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public class ListStudySubjectTableFactory extends AbstractTableFactory {

	public static final Logger LOGGER = LoggerFactory.getLogger(ListStudySubjectTableFactory.class);

	public static final String WRAPPER = "wrapper";
	public static final int FOUR = 4;
	public static final int EIGHT = 8;
	public static final int FIFTY = 50;
	private StudyEventDefinitionDAO studyEventDefinitionDao;
	private StudySubjectDAO studySubjectDAO;
	private SubjectDAO subjectDAO;
	private StudyEventDAO studyEventDAO;
	private StudyGroupClassDAO studyGroupClassDAO;
	private SubjectGroupMapDAO subjectGroupMapDAO;
	private StudyGroupDAO studyGroupDAO;
	private StudyDAO studyDAO;
	private EventCRFDAO eventCRFDAO;
	private CRFVersionDAO crfVersionDAO;
	private ItemDataDAO itemDataDAO;
	private EventDefinitionCRFDAO eventDefintionCRFDAO;
	private DiscrepancyNoteDAO discrepancyNoteDAO;
	private DynamicEventDao dynamicEventDao;
	private StudyBean studyBean;
	private String[] columnNames = new String[]{};
	private ArrayList<StudyEventDefinitionBean> studyEventDefinitions;
	private ArrayList<StudyEventDefinitionBean> studyEventDefinitionsFullList;
	private List<StudyGroupClassBean> studyGroupClasses;
	private List<StudyGroupClassBean> dynamicGroupClasses;
	private StudyUserRoleBean currentRole;
	private UserAccountBean currentUser;
	private boolean showMoreLink;
	private ResourceBundle resword;
	private ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
	private ResourceBundle resterms = ResourceBundleProvider.getTermsBundle();
	private int hideColumnsNumber;
	private boolean sortForMainMenuServlet;

	public static final int POPUP_BASE_WIDTH = 600;

	// To avoid showing title in other pages, the request element is used to determine where the request came from.
	@Override
	public TableFacade createTable(HttpServletRequest request, HttpServletResponse response) {
		locale = LocaleResolver.getLocale(request);
		setRequest(request);
		TableFacade tableFacade = getTableFacadeImpl(request, response);
		tableFacade.setStateAttr("restore");
		setDataAndLimitVariables(tableFacade);
		configureTableFacade(response, tableFacade);
		if (!tableFacade.getLimit().isExported()) {
			configureColumns(tableFacade, locale);
			tableFacade.setMaxRowsIncrements(getMaxRowIncrements());
			configureTableFacadePostColumnConfiguration(tableFacade);
			configureTableFacadeCustomView(tableFacade, request);
			configureUnexportedTable(tableFacade, locale);
		} else {
			configureExportColumns(tableFacade, locale);
		}
		return tableFacade;
	}

	/**
	 * ListStudySubjectTableFactory constructor.
	 * 
	 * @param showMoreLink
	 *            boolean
	 */
	public ListStudySubjectTableFactory(boolean showMoreLink) {
		this.showMoreLink = showMoreLink;
	}

	@Override
	protected String getTableName() {
		return "findSubjects";
	}

	/**
	 * Method configureTableFacadeCustomView.
	 * 
	 * @param tableFacade
	 *            TableFacade
	 * @param request
	 *            HttpServletRequest
	 */
	public void configureTableFacadeCustomView(TableFacade tableFacade, HttpServletRequest request) {
		tableFacade.setView(new ListStudyView(getLocale(), request, studyGroupClasses, dynamicGroupClasses,
				studyEventDefinitions, hideColumnsNumber));
	}

	@Override
	protected void configureColumns(TableFacade tableFacade, Locale locale) {
		resword = ResourceBundleProvider.getWordsBundle(locale);
		resformat = ResourceBundleProvider.getFormatBundle(locale);
		tableFacade.setColumnProperties(columnNames);
		Row row = tableFacade.getTable().getRow();
		int index = 0;

		StudyBean currentStudy = (StudyBean) tableFacade.getWebContext().getSessionAttribute("study");
		configureColumn(row.getColumn(columnNames[index]), currentStudy != null ? currentStudy
				.getStudyParameterConfig().getStudySubjectIdLabel() : resword.getString("study_subject_ID"), null, null);
		++index;
		configureColumn(row.getColumn(columnNames[index]), resword.getString("subject_creation_date"), new DateEditor(
				getCurrentUser().getUserTimeZoneId()), null);
		++index;
		configureColumn(row.getColumn(columnNames[index]), resword.getString("subject_status"), new StatusCellEditor(),
				new StatusDroplistFilterEditor());
		++index;
		configureColumn(row.getColumn(columnNames[index]), resword.getString("site_id"), null, null);
		++index;

		if (currentStudy == null || currentStudy.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("true")) {
			configureColumn(row.getColumn(columnNames[index]), currentStudy == null
					? resword.getString("gender")
					: currentStudy.getStudyParameterConfig().getGenderLabel(), null, null, true, false);
			++index;
		}
		if (currentStudy == null
				|| !currentStudy.getStudyParameterConfig().getSecondaryIdRequired().equalsIgnoreCase("not_used")) {
			configureColumn(row.getColumn(columnNames[index]), currentStudy == null
					? resword.getString("secondary_ID")
					: currentStudy.getStudyParameterConfig().getSecondaryIdLabel(), null, null);
			++index;
		}
		this.hideColumnsNumber = index - 1;

		// group class columns
		for (int i = index; i < index + studyGroupClasses.size(); i++) {
			StudyGroupClassBean studyGroupClass = studyGroupClasses.get(i - index);
			configureColumn(row.getColumn(columnNames[i]), studyGroupClass.getName(), new StudyGroupClassCellEditor(
					studyGroupClass), new SubjectGroupClassDroplistFilterEditor(studyGroupClass), true, false);
		}
		index = index + studyGroupClasses.size();

		// dynamic event columns, but you need to add one col per event here
		for (StudyGroupClassBean dynamicGroupClass : dynamicGroupClasses) {
			for (int i = index; i < index + dynamicGroupClass.getEventDefinitions().size(); i++) {
				StudyEventDefinitionBean studyEventDefinitionBean = dynamicGroupClass.getEventDefinitions().get(
						i - index);
				configureColumn(row.getColumn(columnNames[i]), studyEventDefinitionBean.getName(),
						new StudyEventDefinitionMapCellEditor(), new SubjectEventStatusDroplistFilterEditor(), true,
						false);
			}
			index = index + dynamicGroupClass.getEventDefinitions().size();
		}

		for (int i = index; i < columnNames.length - 1; i++) {
			StudyEventDefinitionBean studyEventDefinition = studyEventDefinitions.get(i - index);
			configureColumn(row.getColumn(columnNames[i]), studyEventDefinition.getName(),
					new StudyEventDefinitionMapCellEditor(), new SubjectEventStatusDroplistFilterEditor(), true, false);
		}

		configureColumn(row.getColumn(columnNames[columnNames.length - 1]), resword.getString("rule_actions"),
				new ActionsCellEditor(), new ListSubjectsActionsFilterEditor(locale), true, false);

	}

	@Override
	public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
		super.configureTableFacade(response, tableFacade);
		int startFrom = getColumnNamesMap(tableFacade);
		tableFacade.autoFilterAndSort(false);
		tableFacade.addFilterMatcher(new MatcherKey(Character.class), new CharFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Status.class), new StatusFilterMatcher());

		for (int i = startFrom; i < startFrom + studyGroupClasses.size(); i++) {
			tableFacade
					.addFilterMatcher(new MatcherKey(Integer.class, columnNames[i]), new SubjectGroupFilterMatcher());
		}
		for (int i = startFrom + studyGroupClasses.size(); i < columnNames.length - 1; i++) {
			tableFacade.addFilterMatcher(new MatcherKey(Integer.class, columnNames[i]),
					new SubjectEventStatusFilterMatcher());
		}

	}

	@Override
	public void configureTableFacadePostColumnConfiguration(TableFacade tableFacade) {
		tableFacade.setToolbar(new ListStudySubjectTableToolbar(getStudyEventDefinitionsForFilter(),
				getStudyGroupClasses(), tableFacade.getWebContext().getContextPath(), showMoreLink));
	}

	private void prepareLimit(Limit limit) {
		boolean found = false;
		for (Sort sort : limit.getSortSet().getSorts()) {
			if (sort.getProperty().equalsIgnoreCase("studySubject.createdDate")) {
				found = true;
				break;
			}
		}
		if (!found) {
			limit.getSortSet().getSorts().add(new Sort(1, "studySubject.createdDate", Order.DESC));
		}
	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {
		Limit limit = tableFacade.getLimit();
		if (sortForMainMenuServlet) {
			prepareLimit(limit);
		}
		Role userRole = ((StudyUserRoleBean) tableFacade.getWebContext().getSessionAttribute("userRole")).getRole();

		FindSubjectsFilter subjectFilter = getSubjectFilter(limit);

		int totalRows;
		if (!limit.isComplete()) {
			totalRows = getStudySubjectDAO().getCountWithFilter(subjectFilter, getStudyBean());
			tableFacade.setTotalRows(totalRows);
		}

		FindSubjectsSort subjectSort = getSubjectSort(limit);

		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		List<StudySubjectBean> items = getStudySubjectDAO().getWithFilterAndSort(getStudyBean(), subjectFilter,
				subjectSort, rowStart, rowEnd);

		Collection<HashMap<Object, Object>> theItems = new ArrayList<HashMap<Object, Object>>();

		for (StudySubjectBean studySubjectBean : items) {
			HashMap<Object, Object> theItem = new HashMap<Object, Object>();
			theItem.put("studySubject", studySubjectBean);
			theItem.put("studySubject.createdDate", studySubjectBean.getCreatedDate());
			theItem.put("studySubject.label", studySubjectBean.getLabel());
			theItem.put("studySubject.status", studySubjectBean.getStatus());
			theItem.put("enrolledAt",
					((StudyBean) getStudyDAO().findByPK(studySubjectBean.getStudyId())).getIdentifier());
			theItem.put("studySubject.secondaryLabel", studySubjectBean.getSecondaryLabel());

			SubjectBean subjectBean = (SubjectBean) getSubjectDAO().findByPK(studySubjectBean.getSubjectId());
			theItem.put("subject", subjectBean);
			theItem.put("subject.charGender", subjectBean.getGender());

			// Get All study events for this study subject and then put list in
			// HashMap with study event definition id as
			// key and a list of study events as the value.
			List<StudyEventBean> allStudyEventsForStudySubject = getStudyEventDAO().findAllByStudySubject(
					studySubjectBean);
			HashMap<Integer, List<StudyEventBean>> allStudyEventsForStudySubjectBySedId = new HashMap<Integer, List<StudyEventBean>>();
			theItem.put("isSignable", SignUtil.permitSign(studySubjectBean, new DAOWrapper(getStudyDAO().getDs())));

			for (StudyEventBean studyEventBean : allStudyEventsForStudySubject) {
				if (allStudyEventsForStudySubjectBySedId.get(studyEventBean.getStudyEventDefinitionId()) == null) {
					ArrayList<StudyEventBean> a = new ArrayList<StudyEventBean>();
					a.add(studyEventBean);
					allStudyEventsForStudySubjectBySedId.put(studyEventBean.getStudyEventDefinitionId(), a);
				} else {
					allStudyEventsForStudySubjectBySedId.get(studyEventBean.getStudyEventDefinitionId()).add(
							studyEventBean);
				}
			}
			SubjectGroupMapBean subjectGroupMapBean;
			for (StudyGroupClassBean studyGroupClass : getStudyGroupClasses()) {
				subjectGroupMapBean = getSubjectGroupMapDAO().findAllByStudySubjectAndStudyGroupClass(
						studySubjectBean.getId(), studyGroupClass.getId());
				if (null != subjectGroupMapBean) {
					theItem.put("sgc_" + studyGroupClass.getId(), subjectGroupMapBean.getStudyGroupId());
					theItem.put("grpName_sgc_" + studyGroupClass.getId(), subjectGroupMapBean.getStudyGroupName());
				}
			}

			for (int i = 0; i < getDynamicGroupClasses().size(); i++) {
				StudyGroupClassBean dynamicGroupClass = getDynamicGroupClasses().get(i);
				boolean permissionForDynamic = false;
				if ((dynamicGroupClass.isDefault())
						|| (studySubjectBean.getDynamicGroupClassId() == dynamicGroupClass.getId())) {
					permissionForDynamic = true;
				}
				for (StudyEventDefinitionBean studyEventDefinition : dynamicGroupClass.getEventDefinitions()) {
					SubjectEventStatus subjectEventStatus = null;
					List<StudyEventBean> studyEvents = allStudyEventsForStudySubjectBySedId.get(studyEventDefinition
							.getId());
					studyEvents = studyEvents == null ? new ArrayList<StudyEventBean>() : studyEvents;
					if (studyEvents.size() < 1) {
						subjectEventStatus = SubjectEventStatus.NOT_SCHEDULED;
					} else {
						int closestOrdinal = getNextOrdinal(studyEvents);
						for (StudyEventBean studyEventBean : studyEvents) {
							if (studyEventBean.getSampleOrdinal() == closestOrdinal) {
								subjectEventStatus = studyEventBean.getSubjectEventStatus();
								break;
							}
						}
					}
					LOGGER.trace("set study events " + studyEvents.toString());
					theItem.put("sed_" + studyEventDefinition.getId() + "_" + dynamicGroupClass.getId(),
							subjectEventStatus != null ? subjectEventStatus.getId() : 0);
					theItem.put("sed_" + studyEventDefinition.getId() + "_" + dynamicGroupClass.getId()
							+ "_studyEvents", studyEvents);
					theItem.put("sed_" + studyEventDefinition.getId() + "_" + dynamicGroupClass.getId() + "_object",
							studyEventDefinition);
					theItem.put("sed_" + studyEventDefinition.getId() + "_" + dynamicGroupClass.getId()
							+ "_permission_for_dynamic", permissionForDynamic);
					theItem.put("sed_" + studyEventDefinition.getId() + "_" + dynamicGroupClass.getId()
							+ "_number_of_column", i);
				}
			}

			for (StudyEventDefinitionBean studyEventDefinition : getStudyEventDefinitions()) {
				List<StudyEventBean> studyEvents = allStudyEventsForStudySubjectBySedId.get(studyEventDefinition
						.getId());
				SubjectEventStatus subjectEventStatus = null;
				studyEvents = studyEvents == null ? new ArrayList<StudyEventBean>() : studyEvents;
				if (studyEvents.size() < 1) {
					subjectEventStatus = SubjectEventStatus.NOT_SCHEDULED;
				} else {
					for (StudyEventBean studyEventBean : studyEvents) {
						int closestOrdinal = getNextOrdinal(studyEvents);
						if (studyEventBean.getSampleOrdinal() == closestOrdinal) {
							subjectEventStatus = studyEventBean.getSubjectEventStatus();
							break;
						}
					}
				}

				theItem.put("sed_" + studyEventDefinition.getId(),
						subjectEventStatus != null ? subjectEventStatus.getId() : 0);
				theItem.put("sed_" + studyEventDefinition.getId() + "_studyEvents", studyEvents);
				theItem.put("sed_" + studyEventDefinition.getId() + "_object", studyEventDefinition);
				theItem.put("sed_" + studyEventDefinition.getId() + "_permission_for_dynamic", true);
			}

			theItems.add(theItem);
		}

		// Do not forget to set the items back on the tableFacade.
		tableFacade.setItems(theItems);

	}

	private int getNextOrdinal(List<StudyEventBean> studyEvents) {
		int ordinal = 1;
		for (StudyEventBean studyEventBean : studyEvents) {
			if (studyEventBean.getSampleOrdinal() > 1) {
				ordinal = studyEventBean.getSampleOrdinal();
				break;
			}
		}
		return ordinal;
	}

	private int getColumnNamesMap(TableFacade tableFacade) {
		int startFrom = FOUR;
		StudyBean currentStudy = (StudyBean) tableFacade.getWebContext().getSessionAttribute("study");

		ArrayList<String> columnNamesList = new ArrayList<String>();
		columnNamesList.add("studySubject.label");
		columnNamesList.add("studySubject.createdDate");
		columnNamesList.add("studySubject.status");
		columnNamesList.add("enrolledAt");
		if (currentStudy == null || currentStudy.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("true")) {
			startFrom++;
			columnNamesList.add("subject.charGender");
		}
		if (currentStudy == null
				|| !currentStudy.getStudyParameterConfig().getSecondaryIdRequired().equalsIgnoreCase("not_used")) {
			startFrom++;
			columnNamesList.add("studySubject.secondaryLabel");
		}
		for (StudyGroupClassBean studyGroupClass : getStudyGroupClasses()) {
			columnNamesList.add("sgc_" + studyGroupClass.getId());
		}

		for (StudyGroupClassBean dynamicGroupClass : getDynamicGroupClasses()) {
			for (StudyEventDefinitionBean studyEventDefinition : dynamicGroupClass.getEventDefinitions()) {
				columnNamesList.add("sed_" + studyEventDefinition.getId() + "_" + dynamicGroupClass.getId());
			}
		}

		for (StudyEventDefinitionBean studyEventDefinition : getStudyEventDefinitions()) {
			columnNamesList.add("sed_" + studyEventDefinition.getId());
		}
		columnNamesList.add("actions");
		columnNames = columnNamesList.toArray(columnNames);
		return startFrom;
	}

	protected FindSubjectsFilter getSubjectFilter(Limit limit) {
		FindSubjectsFilter findSubjectsFilter = new FindSubjectsFilter(getStudyGroupClassDAO());
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			if ("studySubject.status".equalsIgnoreCase(property)) {
				value = Status.getByName(value).getId() + "";
			} else if (property.startsWith("sgc_")) {
				int studyGroupClassId = property.endsWith("_") ? 0 : Integer.valueOf(property.split("_")[1]);
				value = studyGroupDAO.findByNameAndGroupClassID(value, studyGroupClassId).getId() + "";
			}
			findSubjectsFilter.addFilter(property, value);
		}

		return findSubjectsFilter;
	}

	/**
	 * A very custom way to sort the items. The PresidentSort acts as a command for the Hibernate criteria object. There
	 * are probably many ways to do this, but this is the most flexible way I have found. The point is you need to
	 * somehow take the Limit information and sort the rows.
	 * 
	 * @param limit
	 *            The Limit to use.
	 */
	protected FindSubjectsSort getSubjectSort(Limit limit) {
		FindSubjectsSort findSubjectsSort = new FindSubjectsSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			findSubjectsSort.addSort(property, order);
		}

		return findSubjectsSort;
	}

	private ArrayList<StudyEventDefinitionBean> getStudyEventDefinitionsForFilter() {
		if (studyEventDefinitionsFullList == null) {
			if (studyBean.getParentStudyId() > 0) {
				studyEventDefinitionsFullList = getStudyEventDefinitionDao().findAllAvailableByStudy(
						(StudyBean) getStudyDAO().findByPK(studyBean.getParentStudyId()));
			} else {
				studyEventDefinitionsFullList = getStudyEventDefinitionDao().findAllAvailableByStudy(studyBean);
			}
		}
		return studyEventDefinitionsFullList;
	}

	private ArrayList<StudyEventDefinitionBean> getStudyEventDefinitions() {
		// need to filter by events that are not in dynamic groups #done
		// and yet, at the same time, add them to the beginning of the list
		if (this.studyEventDefinitions == null) {
			if (studyBean.getParentStudyId() > 0) {
				studyEventDefinitions = getStudyEventDefinitionDao().findAllActiveByParentStudyId(
						studyBean.getParentStudyId(),
						getDynamicEventDao().findAllDefIdsInActiveDynGroupsByStudyId(studyBean.getParentStudyId()));
				// filter on calendared events as well?
			} else {
				studyEventDefinitions = getStudyEventDefinitionDao().findAllActiveByParentStudyId(studyBean.getId(),
						getDynamicEventDao().findAllDefIdsInActiveDynGroupsByStudyId(studyBean.getId()));
			}
		}
		return this.studyEventDefinitions;
	}

	private List<StudyGroupClassBean> getStudyGroupClasses() {
		// need to filter here by dyanamic groups #done
		if (this.studyGroupClasses == null) {

			int studyIdToSearchOn = studyBean.isSite() ? studyBean.getParentStudyId() : studyBean.getId();
			studyGroupClasses = getStudyGroupClassDAO().findAllActiveByStudyId(studyIdToSearchOn, true);
		}
		return studyGroupClasses;
	}

	private List<StudyGroupClassBean> getDynamicGroupClasses() {
		ArrayList<StudyEventDefinitionBean> studyEventDefinitionsList;
		StudyGroupClassBean sgcb;
		// need to filter by events that are not in dynamic groups #done
		// and yet, at the same time, add them to the beginning of the list
		if (dynamicGroupClasses == null) {

			int studyIdToSearchOn = studyBean.isSite() ? studyBean.getParentStudyId() : studyBean.getId();
			dynamicGroupClasses = getStudyGroupClassDAO().findAllActiveDynamicGroupsByStudyId(studyIdToSearchOn);

			ListIterator<StudyGroupClassBean> it = dynamicGroupClasses.listIterator();
			while (it.hasNext()) {
				sgcb = it.next();
				studyEventDefinitionsList = studyEventDefinitionDao.findAllAvailableAndOrderedByStudyGroupClassId(sgcb
						.getId());
				if (studyEventDefinitionsList.size() != 0) {
					sgcb.setEventDefinitions(studyEventDefinitionsList);
				} else {
					it.remove();
				}
			}
			Collections.sort(dynamicGroupClasses, StudyGroupClassBean.comparatorForDynGroupClasses);
		}
		return dynamicGroupClasses;
	}

	public StudyEventDefinitionDAO getStudyEventDefinitionDao() {
		return studyEventDefinitionDao;
	}

	public void setStudyEventDefinitionDao(StudyEventDefinitionDAO studyEventDefinitionDao) {
		this.studyEventDefinitionDao = studyEventDefinitionDao;
	}

	public StudyBean getStudyBean() {
		return studyBean;
	}

	public void setStudyBean(StudyBean studyBean) {
		this.studyBean = studyBean;
	}

	public StudySubjectDAO getStudySubjectDAO() {
		return studySubjectDAO;
	}

	public void setStudySubjectDAO(StudySubjectDAO studySubjectDAO) {
		this.studySubjectDAO = studySubjectDAO;
	}

	public DynamicEventDao getDynamicEventDao() {
		return dynamicEventDao;
	}

	public void setDynamicEventDao(DynamicEventDao dynamicEventDao) {
		this.dynamicEventDao = dynamicEventDao;
	}

	public SubjectDAO getSubjectDAO() {
		return subjectDAO;
	}

	public void setSubjectDAO(SubjectDAO subjectDAO) {
		this.subjectDAO = subjectDAO;
	}

	public StudyEventDAO getStudyEventDAO() {
		return studyEventDAO;
	}

	public void setStudyEventDAO(StudyEventDAO studyEventDAO) {
		this.studyEventDAO = studyEventDAO;
	}

	public StudyGroupClassDAO getStudyGroupClassDAO() {
		return studyGroupClassDAO;
	}

	public void setStudyGroupClassDAO(StudyGroupClassDAO studyGroupClassDAO) {
		this.studyGroupClassDAO = studyGroupClassDAO;
	}

	public SubjectGroupMapDAO getSubjectGroupMapDAO() {
		return subjectGroupMapDAO;
	}

	public void setSubjectGroupMapDAO(SubjectGroupMapDAO subjectGroupMapDAO) {
		this.subjectGroupMapDAO = subjectGroupMapDAO;
	}

	public StudyDAO getStudyDAO() {
		return studyDAO;
	}

	public void setStudyDAO(StudyDAO studyDAO) {
		this.studyDAO = studyDAO;
	}

	public StudyUserRoleBean getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(StudyUserRoleBean currentRole) {
		this.currentRole = currentRole;
	}

	public EventCRFDAO getEventCRFDAO() {
		return eventCRFDAO;
	}

	public void setEventCRFDAO(EventCRFDAO eventCRFDAO) {
		this.eventCRFDAO = eventCRFDAO;
	}

	public EventDefinitionCRFDAO getEventDefintionCRFDAO() {
		return eventDefintionCRFDAO;
	}

	public void setEventDefintionCRFDAO(EventDefinitionCRFDAO eventDefintionCRFDAO) {
		this.eventDefintionCRFDAO = eventDefintionCRFDAO;
	}

	public DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
		return discrepancyNoteDAO;
	}

	public void setDiscrepancyNoteDAO(DiscrepancyNoteDAO discrepancyNoteDAO) {
		this.discrepancyNoteDAO = discrepancyNoteDAO;
	}

	public StudyGroupDAO getStudyGroupDAO() {
		return studyGroupDAO;
	}

	public void setStudyGroupDAO(StudyGroupDAO studyGroupDAO) {
		this.studyGroupDAO = studyGroupDAO;
	}

	public UserAccountBean getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserAccountBean currentUser) {
		this.currentUser = currentUser;
	}

	public CRFVersionDAO getCrfVersionDAO() {
		return crfVersionDAO;
	}

	public void setCrfVersionDAO(CRFVersionDAO crfVersionDAO) {
		this.crfVersionDAO = crfVersionDAO;
	}

	public ItemDataDAO getItemDataDAO() {
		return itemDataDAO;
	}

	public void setItemDataDAO(ItemDataDAO itemDataDAO) {
		this.itemDataDAO = itemDataDAO;
	}

	public void setSortForMainMenuServlet(boolean sortForMainMenuServlet) {
		this.sortForMainMenuServlet = sortForMainMenuServlet;
	}

	private class CharFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			String item = StringUtils.lowerCase(String.valueOf(itemValue));
			String filter = StringUtils.lowerCase(String.valueOf(filterValue));

			return StringUtils.contains(item, filter);

		}
	}

	/**
	 * StatusFilterMatcher class.
	 */
	public class StatusFilterMatcher implements FilterMatcher {

		/**
		 * Evaluate method.
		 * 
		 * @param itemValue
		 *            Object
		 * @param filterValue
		 *            String
		 * @return boolean
		 */
		public boolean evaluate(Object itemValue, String filterValue) {

			String item = StringUtils.lowerCase(String.valueOf(((Status) itemValue).getName()));
			String filter = StringUtils.lowerCase(String.valueOf(filterValue));

			return item != null && filter != null && item.contains(filter);
		}
	}

	/**
	 * SubjectEventStatusFilterMatcher class.
	 */
	public class SubjectEventStatusFilterMatcher implements FilterMatcher {
		/**
		 * Evaluate method.
		 * 
		 * @param itemValue
		 *            Object
		 * @param filterValue
		 *            String
		 * @return boolean
		 */
		public boolean evaluate(Object itemValue, String filterValue) {
			// No need to evaluate itemValue and filterValue.
			return true;
		}
	}

	/**
	 * SubjectGroupFilterMatcher class.
	 */
	public class SubjectGroupFilterMatcher implements FilterMatcher {

		/**
		 * Evaluate method.
		 * 
		 * @param itemValue
		 *            Object
		 * @param filterValue
		 *            String
		 * @return boolean
		 */
		public boolean evaluate(Object itemValue, String filterValue) {

			String item = StringUtils
					.lowerCase(studyGroupDAO.findByPK(Integer.valueOf(itemValue.toString())).getName());
			String filter = StringUtils.lowerCase(String.valueOf(filterValue.trim()));
			return filter != null && filter.equals(item);
		}
	}

	private class StatusCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
			StudySubjectBean studySubject = (StudySubjectBean) new BasicCellEditor().getValue(item, "studySubject",
					rowcount);
			return studySubject.getStatus().getName();
		}
	}

	private class StatusDroplistFilterEditor extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			for (Object status : Status.toDropDownArrayList()) {
				((Status) status).getName();
				options.add(new Option(((Status) status).getName(), ((Status) status).getName()));
			}
			return options;
		}
	}

	private class SubjectEventStatusDroplistFilterEditor extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			for (Object subjectEventStatus : SubjectEventStatus.toArrayList()) {
				((SubjectEventStatus) subjectEventStatus).getName();
				options.add(new Option(((SubjectEventStatus) subjectEventStatus).getName(),
						((SubjectEventStatus) subjectEventStatus).getName()));
			}
			return options;
		}
	}

	private class SubjectGroupClassDroplistFilterEditor extends DroplistFilterEditor {
		private StudyGroupClassBean studyGroupClass = new StudyGroupClassBean();

		// constructor
		SubjectGroupClassDroplistFilterEditor(StudyGroupClassBean studyGroupClass) {
			this.studyGroupClass = studyGroupClass;
		}

		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			StudyGroupDAO studyGroupDAO = getStudyGroupDAO();
			for (Object subjectStudyGroup : studyGroupDAO.findAllByGroupClass(this.studyGroupClass)) {
				options.add(new Option(((StudyGroupBean) subjectStudyGroup).getName(),
						((StudyGroupBean) subjectStudyGroup).getName()));
			}
			return options;
		}
	}

	private class ListSubjectsActionsFilterEditor extends DefaultActionsEditor {
		public ListSubjectsActionsFilterEditor(Locale locale) {
			super(locale);
		}

		@Override
		public Object getValue() {
			HtmlBuilder html = new HtmlBuilder();
			String value;

			value = (String) super.getValue();
			html.append(value).div().style("width: 225px;").end().divEnd();
			return html.toString();
		}
	}

	private class StudyGroupClassCellEditor implements CellEditor {

		private StudyGroupClassBean studyGroupClass;
		private String groupName;

		public StudyGroupClassCellEditor(StudyGroupClassBean studyGroupClass) {
			this.studyGroupClass = studyGroupClass;
		}

		private String logic() {
			return groupName != null ? groupName : "";
		}

		public Object getValue(Object item, String property, int rowcount) {
			groupName = (String) ((HashMap<Object, Object>) item).get("grpName_sgc_" + studyGroupClass.getId());
			return logic();
		}
	}

	private class StudyEventDefinitionMapCellEditor implements CellEditor {

		private StudyEventDefinitionBean studyEventDefinition;
		private StudySubjectBean studySubjectBean;
		private SubjectEventStatus subjectEventStatus;
		private List<StudyEventBean> studyEvents;
		private SubjectBean subject;

		private String getCount() {
			return studyEvents.size() < 2 ? "" : "<span class=\"re_indicator\">x"
					+ String.valueOf(studyEvents.size() + "</span>");
		}

		public Object getValue(Object item, String property, int rowcount) {

			StudyBean currentStudy = ListStudySubjectTableFactory.this.getStudyBean();
			studyEvents = (List<StudyEventBean>) ((HashMap<Object, Object>) item).get(property + "_studyEvents");
			studyEventDefinition = (StudyEventDefinitionBean) ((HashMap<Object, Object>) item)
					.get(property + "_object");
			subjectEventStatus = SubjectEventStatus.get((Integer) ((HashMap<Object, Object>) item).get(property));
			subject = (SubjectBean) ((HashMap<Object, Object>) item).get("subject");
			studySubjectBean = (StudySubjectBean) ((HashMap<Object, Object>) item).get("studySubject");
			boolean permissionForDynamic = (Boolean) ((HashMap<Object, Object>) item).get(property
					+ "_permission_for_dynamic");

			StringBuilder url = new StringBuilder();
			url.append(eventDivBuilder(subject, rowcount, studyEvents, studyEventDefinition, studySubjectBean));

			SubjectEventStatusUtil.determineSubjectEventIconOnTheSubjectMatrix(url, currentStudy, studySubjectBean,
					studyEvents, subjectEventStatus, resword, permissionForDynamic);

			url.append(getCount());
			url.append("</a>");

			return url.toString();
		}

	}

	private class ActionsCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
			return getSubjectActionsColumnContent(item, currentUser, getCurrentRole(), getStudyBean(), new DAOWrapper(
					getStudyDAO().getDs()), resword, getRequest());
		}
	}

	private static String viewStudySubjectLinkBuilder(StudySubjectBean studySubject, ResourceBundle resword) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href("ViewStudySubject?id=" + studySubject.getId());
		actionLink.append("onMouseDown=\"javascript:setImage('bt_View1','images/bt_View_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_View1','images/bt_View.gif');\"");
		actionLink.append("onClick=\"setAccessedObjected(this);\"");
		actionLink.append("data-cc-subjectMatrixId=" + studySubject.getId()).close();
		actionLink.img().name("bt_View1").src("images/bt_View.gif").border("0").alt(resword.getString("view"))
				.title(resword.getString("view")).append("hspace=\"4\"").end().aEnd();
		return actionLink.toString();

	}

	private static String removeStudySubjectLinkBuilder(StudySubjectBean studySubject, ResourceBundle resword) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href(
				"RemoveStudySubject?action=confirm&id=" + studySubject.getId() + "&subjectId="
						+ studySubject.getSubjectId() + "&studyId=" + studySubject.getStudyId());
		actionLink.append("onMouseDown=\"javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_Remove1','images/bt_Remove.gif');\"");
		actionLink.append("onClick=\"javascript:setAccessedObjected(this);\"").close();
		actionLink.img().name("bt_Remove1").src("images/bt_Remove.gif").border("0").alt(resword.getString("remove"))
				.title(resword.getString("remove")).append("hspace=\"4\"").end().aEnd();
		return actionLink.toString();

	}

	private static String createNotesAndDiscrepanciesIcon(StudySubjectBean studySubject, String flagColor,
			ResourceBundle resword) {
		String imagePath = "images/icon_flagYellow.gif";
		String status = resword.getString("not_closed").replace(" ", "+");
		if (flagColor.equals("red")) {
			imagePath = "images/icon_Note.gif";
		}
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink
				.a()
				.href("ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_studySubject.label="
						+ studySubject.getLabel() + "&&listNotes_f_discrepancyNoteBean.resolutionStatus=" + status);
		actionLink.append("onMouseDown=\"javascript:setImage('ndIcon','" + imagePath + "');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('ndIcon','" + imagePath + "');\"");
		actionLink.append("onClick=\"javascript:setAccessedObjected(this);\"").close();
		actionLink.img().name("ndIcon").src(imagePath).border("0").alt(resword.getString("view_discrepancy_notes"))
				.title(resword.getString("view_discrepancy_notes")).end().aEnd();

		return actionLink.toString();
	}

	private static String signStudySubjectLinkBuilder(StudySubjectBean studySubject, boolean isSignable,
			ResourceBundle resword, StudyEventDAO studyEventDAO, StudyUserRoleBean currentRole) {
		String result = "";
		HtmlBuilder transparentButton = new HtmlBuilder();
		boolean showHidden = !isSignable && studyEventDAO.findAllByStudySubject(studySubject).size() == 0;
		if (isSignable || showHidden) {
			HtmlBuilder actionLink = new HtmlBuilder();
			actionLink.a().id("button_signStudySubject_" + studySubject.getId())
					.href("SignStudySubject?id=" + studySubject.getId()).style(showHidden ? "display: none;" : "");
			actionLink.append("onMouseDown=\"javascript:setImage('icon_signed_blue','images/icon_SignedBlue.gif');\"");
			actionLink.append("onMouseUp=\"javascript:setImage('icon_signed_blue','images/icon_SignedBlue.gif');\"");
			actionLink.append("onClick=\"javascript:setAccessedObjected(this);\"").close();
			actionLink.img().name("bt_Sign1").src("images/icon_SignedBlue.gif").border("0")
					.alt(resword.getString("sign")).title(resword.getString("sign")).append("hspace=\"4\"").end()
					.aEnd();
			result = actionLink.toString();
		} else if (currentRole.getRole().getId() == Role.INVESTIGATOR.getId()) {
			transparentButton.img().name("bt_Transparent").src("images/bt_Transparent.gif").border("0")
					.append("hspace=\"4\"").end();
		}
		return result + transparentButton.toString();
	}

	private static String sdvStudySubjectLinkBuilder(HttpServletRequest request, StudySubjectBean studySubject,
			String flagColour) {

		SDVStudySubjectLinkTag sdvStudySubjectLinkTag = new SDVStudySubjectLinkTag();
		sdvStudySubjectLinkTag.setRequest(request);
		sdvStudySubjectLinkTag.setStudySubject(studySubject);
		sdvStudySubjectLinkTag.setStudySubjectHasUnclosedDNs(flagColour != null);
		sdvStudySubjectLinkTag.setPage(Page.LIST_STUDY_SUBJECTS);
		return sdvStudySubjectLinkTag.buildLink();
	}

	private static String reAssignStudySubjectLinkBuilder(StudySubjectBean studySubject, ResourceBundle resword) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href("ReassignStudySubject?id=" + studySubject.getId());
		actionLink.append("onMouseDown=\"javascript:setImage('bt_Reassign1','images/bt_Reassign_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_Reassign1','images/bt_Reassign.gif');\"");
		actionLink.append("onClick=\"javascript:setAccessedObjected(this);\"").close();
		actionLink.img().name("bt_Reassign1").src("images/bt_Reassign.gif").border("0")
				.alt(resword.getString("reassign")).title(resword.getString("reassign")).append("hspace=\"4\"").end()
				.aEnd();
		return actionLink.toString();
	}

	private static String studySubjectLockLinkBuilder(StudySubjectBean studySubject, ResourceBundle resword,
			StudyEventDAO studyEventDAO) {
		String link = "";
		List<StudyEventBean> studyEventBeanList = studyEventDAO.findAllByStudySubject(studySubject);
		if (studyEventBeanList.size() > 0) {
			boolean allLocked = true;
			boolean hasLockedBy = false;
			for (StudyEventBean studyEventBean : studyEventBeanList) {
				hasLockedBy = hasLockedBy || studyEventBean.getSubjectEventStatus() == SubjectEventStatus.LOCKED;
				if (studyEventBean.getSubjectEventStatus() != SubjectEventStatus.LOCKED) {
					allLocked = false;
				}
			}

			HtmlBuilder actionLink1 = new HtmlBuilder();
			actionLink1.a().id("button_unlockStudySubject_" + studySubject.getId())
					.href("LockStudySubject?id=" + studySubject.getId() + "&action=unlock")
					.style(allLocked && hasLockedBy ? "" : "display: none;");
			actionLink1.append("onClick=\"javascript:setAccessedObjected(this);\"").close();
			actionLink1.img().src("images/bt__Unlock.png").border("0").alt(resword.getString("unlockStudySubject"))
					.title(resword.getString("unlockStudySubject")).append("hspace=\"4\"").end().aEnd();

			HtmlBuilder actionLink2 = new HtmlBuilder();
			actionLink2.a().id("button_lockStudySubject_" + studySubject.getId())
					.href("LockStudySubject?id=" + studySubject.getId() + "&action=lock")
					.style(!allLocked ? "" : "display: none;");
			actionLink2.append("onClick=\"javascript:setAccessedObjected(this);\"").close();
			actionLink2.img().src("images/bt__Lock.png").border("0").alt(resword.getString("lockStudySubject"))
					.title(resword.getString("lockStudySubject")).append("hspace=\"4\"").end().aEnd();

			link = actionLink1.toString() + actionLink2.toString();
		}
		return link;
	}

	private static String restoreStudySubjectLinkBuilder(StudySubjectBean studySubject, ResourceBundle resword,
			StudyUserRoleBean currentRole) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href(
				"RestoreStudySubject?action=confirm&id=" + studySubject.getId() + "&subjectId="
						+ studySubject.getSubjectId() + "&studyId=" + studySubject.getStudyId());
		actionLink.append("onMouseDown=\"javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');\"");
		actionLink.append("onClick=\"javascript:setAccessedObjected(this);\"").close();
		actionLink.img().name("bt_Restore1").src("images/bt_Restore.gif").border("0").alt(resword.getString("restore"))
				.title(resword.getString("restore")).append("hspace=\"4\"").end().aEnd();
		HtmlBuilder transparentButton = new HtmlBuilder();
		if (currentRole.getRole().getId() != Role.CLINICAL_RESEARCH_COORDINATOR.getId()
				&& currentRole.getRole().getId() != Role.STUDY_ADMINISTRATOR.getId()
				&& currentRole.getRole().getId() != Role.SYSTEM_ADMINISTRATOR.getId()) {
			transparentButton.img().name("bt_Transparent").src("images/bt_Transparent.gif").border("0")
					.append("hspace=\"4\"").end();
		}
		return actionLink.toString() + transparentButton.toString();

	}

	/**
	 * EventDivBuilder method.
	 * 
	 * @param subject
	 *            SubjectBean
	 * @param rowCount
	 *            Integer
	 * @param studyEvents
	 *            List<StudyEventBean>
	 * @param sed
	 *            StudyEventDefinitionBean
	 * @param studySubject
	 *            StudySubjectBean
	 * @param locale
	 *            Locale
	 * @return String
	 */
	public String eventDivBuilder(SubjectBean subject, Integer rowCount, List<StudyEventBean> studyEvents,
			StudyEventDefinitionBean sed, StudySubjectBean studySubject, Locale locale) {
		this.locale = locale;
		resword = ResourceBundleProvider.getWordsBundle(locale);
		resformat = ResourceBundleProvider.getFormatBundle(locale);

		HtmlBuilder eventDiv = new HtmlBuilder();

		eventDiv.table(0).border("0").cellpadding("0").cellspacing("0").close();
		eventDiv.tr(0).valign("top").close();

		if (studyEvents.size() > 1) {
			repeatingEventDivBuilder(eventDiv, subject, rowCount, studyEvents, sed, studySubject);
		} else {
			singleEventDivBuilder(eventDiv, subject, rowCount, studyEvents, sed, studySubject);
		}

		return eventDiv.toString();
	}

	private String eventDivBuilder(SubjectBean subject, Integer rowCount, List<StudyEventBean> studyEvents,
			StudyEventDefinitionBean sed, StudySubjectBean studySubject) {

		String studySubjectLabel = SubjectLabelNormalizer.normalizeSubjectLabel(studySubject.getLabel());

		String divWidth = studyEvents.size() > 1
				? ("" + (POPUP_BASE_WIDTH + FIFTY + EIGHT))
				: ("" + (POPUP_BASE_WIDTH + EIGHT));

		HtmlBuilder eventDiv = new HtmlBuilder();

		// Event Div
		eventDiv.div().id("Event_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount)
				.styleClass("eventDivWrapper ViewSubjectsPopup").style("min-width:" + divWidth + "px;")
				.rel("" + studySubject.getId()).append("event_name='" + sed.getName() + "'").close();

		eventDiv.table(0).border("0").cellpadding("0").cellspacing("0").close();

		if (studyEvents.size() > 1) {
			repeatingEventDivBuilder(eventDiv, subject, rowCount, studyEvents, sed, studySubject);
		} else {
			singleEventDivBuilder(eventDiv, subject, rowCount, studyEvents, sed, studySubject);
		}

		return eventDiv.toString();
	}

	private void repeatingEventDivBuilder(HtmlBuilder eventDiv, SubjectBean subject, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed, StudySubjectBean studySubject) {

		String tableHeaderRowLeftStyleClass = "table_header_row_left";
		String addAnotherOccurrence = resword.getString("add_another_occurrence");
		String occurrenceXOf = resword.getString("ocurrence");

		String idAttribute;
		String studySubjectLabel = SubjectLabelNormalizer.normalizeSubjectLabel(studySubject.getLabel());
		Status eventSysStatus = studySubject.getStatus();
		Integer studyEventsSize = studyEvents.size();

		eventDiv.tr(0).valign("top").close();
		eventDiv.td(0).styleClass(tableHeaderRowLeftStyleClass).colspan("2").close();

		eventDiv.div().styleClass("width49").close();
		divCloseRepeatinglinkBuilder(eventDiv, studySubjectLabel, rowCount, studyEvents, sed);
		eventDiv.br();
		if (eventSysStatus != Status.DELETED && eventSysStatus != Status.AUTO_DELETED
				&& studyBean.getStatus() == Status.AVAILABLE) {
			eventDiv.span().styleClass("wrapper_pl").close();
			eventDiv.ahref("CreateNewStudyEvent?studySubjectId=" + studySubject.getId() + "&studyEventDefinition="
					+ sed.getId(), addAnotherOccurrence);
		}
		eventDiv.nbsp().nbsp().nbsp();
		for (int i = 1; i <= studyEventsSize; i++) {
			eventDiv.ahref("javascript:StatusBoxSkip2('" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
					+ "'," + studyEventsSize + "," + i + ");", String.valueOf(i));
			if (i < studyEventsSize) {
				eventDiv.append("|");
			}
		}
		eventDiv.spanEnd();
		eventDiv.divEnd().tdEnd().append("<td class=\"table_header_row_left\">&nbsp;</td>").trEnd(0);
		eventDiv.tr(0).close();

		eventDiv.td(0).id("Scroll_off_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "_back")
				.styleClass("statusbox_scroll_L_dis").width("20").close();
		eventDiv.img().src("images/arrow_status_back_dis.gif").border("0").close();
		eventDiv.tdEnd();

		eventDiv.td(0).id("Scroll_on_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "_back")
				.styleClass("statusbox_scroll_L").width("20").style("display: none;").close();

		eventDiv.div().id("bt_Scroll_Event_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "_back")
				.style("display: none;").close();
		eventDiv.a()
				.href("javascript:StatusBoxBack2('" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "',"
						+ studyEventsSize + ");").close();
		eventDiv.img().src("images/arrow_status_back.gif").border("0").close();
		eventDiv.aEnd();
		eventDiv.divEnd();

		eventDiv.div().id("bt_Scroll_Event_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "_back_dis")
				.close();
		eventDiv.img().src("images/arrow_status_back_dis.gif").border("0").close();
		eventDiv.divEnd();
		eventDiv.tdEnd();

		for (int i = 0; i < studyEvents.size(); i++) {
			StudyEventBean studyEventBean = studyEvents.get(i);
			idAttribute = studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "_" + (i + 1);

			eventDiv.td(0).id("Event_" + idAttribute).rel("" + studyEventBean.getId()).valign("top")
					.rel("" + studySubject.getId());
			if (i + 1 > 1) {
				eventDiv.style("display: none;");
			}
			eventDiv.close();
			eventDiv.table(0).border("0").cellpadding("0").cellspacing("0").width("100%").close();
			eventDiv.tr(0).valign("top").close();
			eventDiv.td(0).styleClass(tableHeaderRowLeftStyleClass + " thrl_border wrapper_ptl").colspan("2").close();
			eventDiv.bold().append(occurrenceXOf).append("#" + (i + 1) + " of " + studyEventsSize).br();
			eventDiv.append(formatDate(studyEventBean.getDateStarted())).br();

			eventDiv.boldEnd().tdEnd().trEnd(0);
			eventDiv.tr(0).id("Menu_on_" + idAttribute).style("display: all").close();
			eventDiv.td(0).colspan("2").close();

			LinksDivBuilderWrapper linksDivBuilderWrapper = new LinksDivBuilderWrapper();
			linksDivBuilderWrapper.eventDiv = eventDiv;
			linksDivBuilderWrapper.subject = subject;
			linksDivBuilderWrapper.rowCount = rowCount;
			linksDivBuilderWrapper.studyEvents = studyEvents;
			linksDivBuilderWrapper.sed = sed;
			linksDivBuilderWrapper.studySubject = studySubject;
			linksDivBuilderWrapper.currentEvent = studyEventBean;
			linksDivBuilderWrapper.idAttribute = idAttribute;
			linksDivBuilder(linksDivBuilderWrapper);

			eventDiv.tableEnd(0);
			eventDiv.tdEnd();
		}

		eventDiv.td(0).id("Scroll_off_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "_next")
				.styleClass("statusbox_scroll_R_dis").width("20").close();
		eventDiv.img().src("images/arrow_status_next_dis.gif").border("0").close();
		eventDiv.tdEnd();

		eventDiv.td(0).id("Scroll_on_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "_next")
				.styleClass("statusbox_scroll_R").width("20").style("display: none;").close();
		eventDiv.div().id("bt_Scroll_Event_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "_next")
				.close();
		eventDiv.a()
				.href("javascript:StatusBoxNext2('" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "',"
						+ studyEventsSize + ");").close();
		eventDiv.img().src("images/arrow_status_next.gif").border("0").close();
		eventDiv.aEnd();
		eventDiv.divEnd();

		eventDiv.div().id("bt_Scroll_Event_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "_next_dis")
				.style("display: none;").close();
		eventDiv.img().src("images/arrow_status_next_dis.gif").border("0").close();
		eventDiv.divEnd();
		eventDiv.tdEnd().trEnd(0);

		eventDiv.tableEnd(0);
		eventDiv.divEnd().divEnd().divEnd();
		if (studyEvents.size() != 0
				|| (studyEvents.size() == 0 && canScheduleStudySubject(studySubject)
						&& !Role.isMonitor(currentRole.getRole()) && studyBean.getStatus().isAvailable())) {
			repeatingIconLinkBuilder(eventDiv, studySubjectLabel, rowCount, studyEvents, sed, studyEvents.size() > 0
					? ("" + studyEvents.get(0).getId())
					: "");
		}
	}

	/**
	 * LinksDivBuilderWrapper sub class.
	 */
	private class LinksDivBuilderWrapper {
		private Integer rowCount;
		private String idAttribute;
		private SubjectBean subject;
		private HtmlBuilder eventDiv;
		private StudyEventBean currentEvent;
		private StudyEventDefinitionBean sed;
		private StudySubjectBean studySubject;
		private List<StudyEventBean> studyEvents;
	}

	private void linksDivBuilder(LinksDivBuilderWrapper linksDivBuilderWrapper) {

		Status eventSysStatus = linksDivBuilderWrapper.studySubject.getStatus();
		SubjectEventStatus eventStatus = linksDivBuilderWrapper.currentEvent.getSubjectEventStatus();
		String studyEventId = String.valueOf(linksDivBuilderWrapper.currentEvent.getId());

		if (eventSysStatus.getId() == Status.AVAILABLE.getId() || eventSysStatus == Status.SIGNED
				|| eventSysStatus == Status.LOCKED) {

			if (eventStatus.isCompleted() || eventStatus == SubjectEventStatus.LOCKED) {
				linksDivBuilderWrapper.eventDiv.tr(0).valign("top").close();
				linksDivBuilderWrapper.eventDiv.td(0).styleClass("table_cell").close();
				linksDivBuilderWrapper.eventDiv.div().id("crfListWrapper_" + linksDivBuilderWrapper.idAttribute)
						.styleClass(WRAPPER).close().divEnd();
				linksDivBuilderWrapper.eventDiv.tdEnd().trEnd(0);
			} else {
				linksDivBuilderWrapper.eventDiv.tr(0).valign("top").close();
				linksDivBuilderWrapper.eventDiv.td(0).styleClass("table_cell_left").close();
				linksDivBuilderWrapper.eventDiv.div().id("crfListWrapper_" + linksDivBuilderWrapper.idAttribute)
						.styleClass(WRAPPER).close().divEnd();
				linksDivBuilderWrapper.eventDiv.tdEnd().trEnd(0);
			}
		}

		if (eventSysStatus == Status.DELETED || eventSysStatus == Status.AUTO_DELETED) {
			linksDivBuilderWrapper.eventDiv.tr(0).valign("top").close();
			linksDivBuilderWrapper.eventDiv.td(0).styleClass("table_cell").close();
			linksDivBuilderWrapper.eventDiv.div().id("crfListWrapper_" + linksDivBuilderWrapper.idAttribute)
					.styleClass(WRAPPER).close().divEnd();
			linksDivBuilderWrapper.eventDiv.tdEnd().trEnd(0);
		}

	}

	private void singleEventDivBuilder(HtmlBuilder eventDiv, SubjectBean subject, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed, StudySubjectBean studySubject) {

		String tableHeaderRowLeftStyleClass = "table_header_row_left";
		String addAnotherOccurrence = resword.getString("add_another_occurrence");
		String occurrenceXOf = resword.getString("ocurrence");
		String status = resword.getString("status");

		SubjectEventStatus eventStatus = studyEvents.size() == 0 ? SubjectEventStatus.NOT_SCHEDULED : studyEvents
				.get(0).getSubjectEventStatus();

		String studyEventId = studyEvents.size() == 0 ? "" : String.valueOf(studyEvents.get(0).getId());
		Status eventSysStatus = studySubject.getStatus();
		String studySubjectLabel = SubjectLabelNormalizer.normalizeSubjectLabel(studySubject.getLabel());

		if (sed.isRepeating()) {

			eventDiv.tr(0).valign("top").close();
			eventDiv.td(0).styleClass(tableHeaderRowLeftStyleClass + " wrapper_ptl").colspan("2").close();
			eventDiv.bold().append(occurrenceXOf).append("#1 of 1").br();
			if (studyEvents.size() > 0) {
				eventDiv.append(formatDate(studyEvents.get(0).getDateStarted())).br();

			} else {
				eventDiv.append(status + " : " + SubjectEventStatus.NOT_SCHEDULED.getName());
			}
			eventDiv.boldEnd().tdEnd().trEnd(0);
			if (eventStatus != SubjectEventStatus.NOT_SCHEDULED && eventSysStatus != Status.DELETED
					&& eventSysStatus != Status.AUTO_DELETED) {
				eventDiv.tr(0).close().td(0).styleClass("table_cell_left wrapper_pl").close();
				eventDiv.ahref("CreateNewStudyEvent?studySubjectId=" + studySubject.getId() + "&studyEventDefinition="
						+ sed.getId(), addAnotherOccurrence);
				eventDiv.tdEnd().trEnd(0);
			}

		}

		eventDiv.tr(0).id("Menu_on_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount).style("display: all")
				.close();
		eventDiv.td(0).colspan("2").close();

		if (eventSysStatus.getId() == Status.AVAILABLE.getId() || eventSysStatus == Status.SIGNED
				|| eventSysStatus == Status.LOCKED) {

			if (eventStatus == SubjectEventStatus.NOT_SCHEDULED && canScheduleStudySubject(studySubject)
					&& !Role.isMonitor(currentRole.getRole()) && studyBean.getStatus().isAvailable()) {
				eventDiv.tr(0).valign("top").close();
				eventDiv.td(0).styleClass("table_cell_left").close();
				String href1 = "PageToCreateNewStudyEvent?studySubjectId=" + studySubject.getId()
						+ "&studyEventDefinition=" + sed.getId();
				eventDiv.div().id("eventScheduleWrapper_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount)
						.rel(href1).styleClass(WRAPPER).close().divEnd();
				eventDiv.tdEnd().trEnd(0);
			} else {
				eventDiv.tr(0).valign("top").close();
				eventDiv.td(0).styleClass("table_cell_left").close();
				eventDiv.div().id("crfListWrapper_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount)
						.styleClass(WRAPPER).close().divEnd();
				eventDiv.tdEnd().trEnd(0);
			}
		}

		if (eventSysStatus == Status.DELETED || eventSysStatus == Status.AUTO_DELETED) {
			eventDiv.tr(0).valign("top").close();
			eventDiv.td(0).styleClass("table_cell_left").close();
			eventDiv.div().id("crfListWrapper_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount)
					.styleClass(WRAPPER).close().divEnd();
			eventDiv.tdEnd().trEnd(0);
		}

		eventDiv.tableEnd(0);
		eventDiv.divEnd().divEnd().divEnd();
		if (eventStatus != SubjectEventStatus.NOT_SCHEDULED
				|| (eventStatus == SubjectEventStatus.NOT_SCHEDULED && canScheduleStudySubject(studySubject)
						&& !Role.isMonitor(currentRole.getRole()) && studyBean.getStatus().isAvailable())) {
			iconLinkBuilder(eventDiv, studySubjectLabel, rowCount, studyEvents, sed, studyEventId);
		}
	}

	private void removeStudyEventLinkBuilder(HtmlBuilder builder, Integer studySubjectId, String studyEventId,
			String remove) {
		String href1 = "RemoveStudyEvent?action=confirm&id=" + studyEventId + "&studySubId=" + studySubjectId;
		builder.a().href(href1);
		builder.close();
		builder.img().src("images/bt_Remove.gif").border("0").align("left").close().aEnd();
		builder.nbsp().nbsp().a().href(href1);
		builder.close().append(remove).aEnd();

	}

	private boolean canScheduleStudySubject(StudySubjectBean studySubject) {
		return studySubject.getStatus() != Status.INVALID && studySubject.getStatus() != Status.UNAVAILABLE
				&& studySubject.getStatus() != Status.LOCKED && studySubject.getStatus() != Status.DELETED
				&& studySubject.getStatus() != Status.AUTO_DELETED;
	}

	private void repeatingIconLinkBuilder(HtmlBuilder builder, String studySubjectLabel, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed, String studyEventId) {

		JSONObject params = new JSONObject();
		try {
			params.put("page", Page.LIST_STUDY_SUBJECTS_SERVLET.getFileName()); // determines page name, popup content
																				// should be customized for
			params.put("studyEventId", studyEventId);
			params.put("statusBoxId", studySubjectLabel + "_" + sed.getId() + "_" + rowCount);
			params.put("statusBoxNum", studyEvents.size());
		} catch (JSONException e) {
			LOGGER.error("Error has occurred.", e);
		}

		builder.a();
		builder.append(" onmouseover=\"if(canShowPopup())showPopup(eval(" + params.toString().replaceAll("\"", "'")
				+ "),event);\" ");
		builder.onmouseout("clearInterval(popupInterval);");
		builder.append(" onclick=\"justShowPopup(eval(" + params.toString().replaceAll("\"", "'") + "),event);\" ");
		builder.close();

	}

	private void iconLinkBuilder(HtmlBuilder builder, String studySubjectLabel, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed, String studyEventId) {

		JSONObject params = new JSONObject();
		try {
			params.put("page", Page.LIST_STUDY_SUBJECTS_SERVLET.getFileName()); // determines page name, popup content
																				// should be customized for
			params.put("studyEventId", studyEventId);
			params.put("statusBoxId", studySubjectLabel + "_" + sed.getId() + "_" + rowCount);
		} catch (JSONException e) {
			LOGGER.error("Error has occurred.", e);
		}

		builder.a();
		builder.append(" onmouseover=\"if(canShowPopup())showPopup(eval(" + params.toString().replaceAll("\"", "'")
				+ "), event);\" ");
		builder.onmouseout("clearInterval(popupInterval);");
		builder.append(" onclick=\"justShowPopup(eval(" + params.toString().replaceAll("\"", "'") + "),event);\" ");
		builder.close();

	}

	private void divCloseRepeatinglinkBuilder(HtmlBuilder builder, String studySubjectLabel, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed) {
		String href1 = "javascript:ExpandEventOccurrences('" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
				+ "'," + studyEvents.size() + "); ";
		String href2 = "javascript:leftnavExpand('Menu_off_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
				+ "'); ";
		String onClick1 = "layersShowOrHide('hidden','Lock_all'); ";
		String onClick2 = "layersShowOrHide('hidden','Event_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
				+ "'); ";
		String onClick3 = "layersShowOrHide('hidden','Lock_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
				+ "'); ";
		String onClick4 = "javascript:setImage('ExpandIcon_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
				+ "','images/icon_blank.gif'); ";
		builder.a().href(href1 + href2);
		builder.onclick("closePopup(); " + onClick1 + onClick2 + onClick3 + onClick4);
		builder.close().append("X").aEnd();

	}

	private String formatDate(Date date) {
		return DateUtil.printDate(date, getCurrentUser().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale());
	}

	private static String calendaredEventsBuilder(StudyBean studyBean, StudySubjectBean studySubject,
			ResourceBundle resword, StudyUserRoleBean currentRole, DAOWrapper daoWrapper) {
		HtmlBuilder transparentIcon = new HtmlBuilder();
		if (currentRole.getRole().getId() == 2) {
			transparentIcon = new HtmlBuilder();
			List<StudyEventBean> studyEventBeanList = daoWrapper.getSedao().findAllByStudySubject(studySubject);
			if (studyEventBeanList.size() == 0) {
				transparentIcon.img().name("bt_Transparent").src("images/bt_Transparent.gif").border("0")
						.append("hspace=\"4\"").end();
			}
		}

		boolean completedReferenceEvent = false;
		List<StudyEventBean> studyEventBeanList = daoWrapper.getSedao().findAllByStudySubject(studySubject);
		for (StudyEventBean studyEventBean : studyEventBeanList) {
			StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) daoWrapper.getSeddao().findByPK(
					studyEventBean.getStudyEventDefinitionId());
			if (sedBean.getReferenceVisit() && studyEventBean.getSubjectEventStatus().isCompleted()) {
				completedReferenceEvent = true;
				break;
			}
		}
		HtmlBuilder actionLink = new HtmlBuilder();

		if (completedReferenceEvent) {
			String iconColor = "bt_Calendar";
			List<StudyEventBean> studyEventList = daoWrapper.getSedao().findAllByStudySubject(studySubject);
			if (!getCalendarIconColor(studyBean, studyEventList, studySubject, daoWrapper)) {
				iconColor = "bt_Calendar_red";
			}
			actionLink.a().href(
					"javascript:openDocWindow('ViewCalendaredEventsForSubject?id=" + studySubject.getId() + "')");
			actionLink.append("onMouseDown=\"javascript:setImage('bt_Calendar','images/" + iconColor + "_d.gif');\"");
			actionLink.append("onMouseUp=\"javascript:setImage('bt_Calendar','images/" + iconColor + ".gif');\"");
			actionLink.append("onClick=\"javascript:setAccessedObjected(this);\"").close();
			actionLink.img().name("bt_Calendar").src("images/" + iconColor + ".gif").border("0");
			actionLink.alt(resword.getString("view_calendared_parameters"))
					.title(resword.getString("view_calendared_parameters")).append("hspace=\"4\"").end().aEnd();
		} else {
			actionLink.img().name("bt_Transparent").src("images/bt_Transparent.gif").border("0").append("hspace=\"4\"")
					.end();
		}
		return transparentIcon.toString() + actionLink.toString();
	}

	private static boolean getCalendarIconColor(StudyBean studyBean, List<StudyEventBean> studyEventBeanList,
			StudySubjectBean subjectBean, DAOWrapper daoWrapper) {
		boolean defaultColor = true;
		StudyEventBean refEventResult;
		for (StudyEventBean studyEventBean : studyEventBeanList) {
			StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) daoWrapper.getSeddao().findByPK(
					studyEventBean.getStudyEventDefinitionId());
			if (!sedBean.getReferenceVisit() && "calendared_visit".equalsIgnoreCase(sedBean.getType())
					&& studyEventBean.getReferenceVisitId() != 0) {
				refEventResult = (StudyEventBean) daoWrapper.getSedao().findByPK(studyEventBean.getReferenceVisitId());
				LOGGER.info("found for completed event");
				// if null RV for event not found.
				if (refEventResult != null) {

					// Reference event should be completed, signed or SDVed
					if (refEventResult.getSubjectEventStatus().isCompleted()
							|| refEventResult.getSubjectEventStatus().isSigned()
							|| refEventResult.getSubjectEventStatus().isSourceDataVerified()) {
						// Should analyze color if event completed, signed or SDVed (based on dateUpdate)
						if (studyEventBean.getSubjectEventStatus().isCompleted()
								|| studyEventBean.getSubjectEventStatus().isSigned()
								|| studyEventBean.getSubjectEventStatus().isSourceDataVerified()
								&& studyEventBean.getUpdatedDate() != null) {
							Date minDate = new DateTime(refEventResult.getUpdatedDate().getTime()).plusDays(
									sedBean.getMinDay()).toDate();
							Date maxDate = new DateTime(refEventResult.getUpdatedDate().getTime()).plusDays(
									sedBean.getMaxDay()).toDate();
							if ((minDate.after(studyEventBean.getUpdatedDate()))
									&& daoWrapper.getDiscDao().doesEventHaveNewDNsInStudy(studyBean, sedBean.getName(),
											studyEventBean.getId(), subjectBean.getLabel())) {
								defaultColor = false;
								break;
							} else if (maxDate.before(studyEventBean.getUpdatedDate())
									&& daoWrapper.getDiscDao().doesEventHaveNewDNsInStudy(studyBean, sedBean.getName(),
											studyEventBean.getId(), subjectBean.getLabel())) {
								defaultColor = false;
								break;
							}
						}

						// for scheduled events (based on start date)
						if (studyEventBean.getSubjectEventStatus().isScheduled()
								&& studyEventBean.getDateStarted() != null) {
							Date maxDate = new DateTime(refEventResult.getUpdatedDate().getTime()).plusDays(
									sedBean.getMaxDay() + 1).toDate();
							if (maxDate.before(new Date())) {
								defaultColor = false;
								break;
							}
						}
					}
				}
			}

		}
		return defaultColor;
	}

	/**
	 * Method getSubjectActionsColumnContent.
	 *
	 * @param item
	 *            Object item
	 * @param currentUser
	 *            UserAccountBean
	 * @param currentRole
	 *            StudyUserRoleBean
	 * @param studyBean
	 *            StudyBean
	 * @param daoWrapper
	 *            DAOWrapper
	 * @param resword
	 *            ResourceBundle
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public static String getSubjectActionsColumnContent(Object item, UserAccountBean currentUser,
			StudyUserRoleBean currentRole, StudyBean studyBean, DAOWrapper daoWrapper, ResourceBundle resword,
			HttpServletRequest request) {
		String value;
		StudySubjectBean studySubjectBean = (StudySubjectBean) ((HashMap<Object, Object>) item).get("studySubject");
		Boolean isSignable = (Boolean) ((HashMap<Object, Object>) item).get("isSignable");
		Integer studySubjectId = studySubjectBean.getId();
		String flagColour = null;
		if (daoWrapper.getDiscDao().doesSubjectHaveAnyUnclosedDNsInStudy(studyBean, studySubjectBean.getLabel(),
				currentUser)) {
			flagColour = "yellow";
			if (daoWrapper.getDiscDao().doesSubjectHaveAnyNewDNsInStudy(studyBean, studySubjectBean.getLabel(),
					currentUser)) {
				flagColour = "red";
			}
		}

		HtmlBuilder transparentButton = new HtmlBuilder();
		transparentButton = transparentButton.img().name("bt_Transparent").src("images/bt_Transparent.gif").border("0")
				.append("hspace=\"4\"").end();

		StringBuilder url = new StringBuilder();
		url.append("<div style=\"padding-top: 3px; float: left;\">");
		url.append(viewStudySubjectLinkBuilder(studySubjectBean, resword));
		if (studyBean.getStatus() == Status.AVAILABLE
				&& !(studySubjectBean.getStatus() == Status.DELETED || studySubjectBean.getStatus() == Status.AUTO_DELETED)
				&& currentRole.getRole() != Role.CLINICAL_RESEARCH_COORDINATOR
				&& !Role.isMonitor(currentRole.getRole())) {

			if (studySubjectBean.getStatus() != Status.SIGNED) {
				url.append(removeStudySubjectLinkBuilder(studySubjectBean, resword));
			} else {
				url.append(transparentButton);
			}

		}
		if (studyBean.getStatus() == Status.AVAILABLE
				&& !Role.isMonitor(currentRole.getRole())
				&& currentRole.getRole() != Role.CLINICAL_RESEARCH_COORDINATOR
				&& (studySubjectBean.getStatus() == Status.DELETED || studySubjectBean.getStatus() == Status.AUTO_DELETED)) {
			url.append(restoreStudySubjectLinkBuilder(studySubjectBean, resword, currentRole));
		}

		url.append(sdvStudySubjectLinkBuilder(request, studySubjectBean, flagColour));

		if (studyBean.getStatus() == Status.AVAILABLE && currentRole.getRole() != Role.CLINICAL_RESEARCH_COORDINATOR
				&& currentRole.getRole() != Role.INVESTIGATOR && !Role.isMonitor(currentRole.getRole())
				&& studySubjectBean.getStatus() == Status.AVAILABLE) {
			url.append(reAssignStudySubjectLinkBuilder(studySubjectBean, resword));
		} else if (studyBean.getStatus() == Status.AVAILABLE
				&& (studySubjectBean.getStatus() != Status.DELETED || studySubjectBean.getStatus() != Status.AUTO_DELETED)
				&& !Role.isMonitor(currentRole.getRole())
				&& currentRole.getRole().getId() != Role.CLINICAL_RESEARCH_COORDINATOR.getId()
				&& currentRole.getRole().getId() != Role.INVESTIGATOR.getId()) {
			url.append(transparentButton);
		}
		if (currentRole.getRole() == Role.INVESTIGATOR
				&& (studyBean.getStatus() == Status.AVAILABLE || studyBean.getStatus() == Status.FROZEN)
				&& studySubjectBean.getStatus() != Status.DELETED) {
			url.append(signStudySubjectLinkBuilder(studySubjectBean, isSignable, resword, daoWrapper.getSedao(),
					currentRole));
		}

		if (currentRole.getRole() == Role.STUDY_ADMINISTRATOR || currentRole.getRole() == Role.SYSTEM_ADMINISTRATOR) {
			url.append(studySubjectLockLinkBuilder(studySubjectBean, resword, daoWrapper.getSedao()));
		}

		url.append(calendaredEventsBuilder(studyBean, studySubjectBean, resword, currentRole, daoWrapper));

		if (flagColour != null && (studyBean.getStatus() == Status.AVAILABLE || studyBean.getStatus() == Status.FROZEN)) {
			// Make sure this is the last icon
			url.append(createNotesAndDiscrepanciesIcon(studySubjectBean, flagColour, resword));
		}
		value = "</div>" + url.toString();
		return value;
	}
}
