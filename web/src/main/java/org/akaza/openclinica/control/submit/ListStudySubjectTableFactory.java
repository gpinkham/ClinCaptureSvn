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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.control.submit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.akaza.openclinica.util.DAOWrapper;
import org.akaza.openclinica.util.SDVUtil;
import org.akaza.openclinica.util.SignUtil;
import org.akaza.openclinica.util.SubjectEventStatusUtil;
import org.akaza.openclinica.util.SubjectLabelNormalizer;
import org.apache.commons.lang.StringUtils;
import org.jmesa.core.filter.FilterMatcher;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.editor.DroplistFilterEditor;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ListStudySubjectTableFactory extends AbstractTableFactory {

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
	private StudyBean studyBean;
	private String[] columnNames = new String[] {};
	private ArrayList<StudyEventDefinitionBean> studyEventDefinitions;
	private ArrayList<StudyGroupClassBean> studyGroupClasses;
	private StudyUserRoleBean currentRole;
	private UserAccountBean currentUser;
	private boolean showMoreLink;
	private ResourceBundle resword;
	private ResourceBundle resformat;
	private ResourceBundle resterms = ResourceBundleProvider.getTermsBundle();

	public static final int WIDTH_600 = 600;
	public static final String WIDTH_600PX = "width: " + WIDTH_600 + "px";

	private Role userRole;

	final HashMap<Integer, String> imageIconPaths = new HashMap<Integer, String>();

	// To avoid showing title in other pages, the request element is used to determine where the request came from.
	@Override
	public TableFacade createTable(HttpServletRequest request, HttpServletResponse response) {
		locale = request.getLocale();
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

	public ListStudySubjectTableFactory(boolean showMoreLink) {
		imageIconPaths.put(1, "images/icon_Scheduled.gif");
		imageIconPaths.put(2, "images/icon_NotStarted.gif");
		imageIconPaths.put(3, "images/icon_InitialDE.gif");
		imageIconPaths.put(4, "images/icon_DEcomplete.gif");
		imageIconPaths.put(5, "images/icon_Stopped.gif");
		imageIconPaths.put(6, "images/icon_Skipped.gif");
		imageIconPaths.put(7, "images/icon_Locked.gif");
		imageIconPaths.put(8, "images/icon_Signed.gif");
		imageIconPaths.put(9, "images/icon_DoubleCheck.gif");
		imageIconPaths.put(10, "images/icon_Invalid.gif");
		this.showMoreLink = showMoreLink;
	}

	@Override
	protected String getTableName() {
		return "findSubjects";
	}

	public void configureTableFacadeCustomView(TableFacade tableFacade, HttpServletRequest request) {
		tableFacade.setView(new ListStudyView(getLocale(), request));
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
		configureColumn(row.getColumn(columnNames[index]), resword.getString("subject_creation_date"), null, null);
		++index;
		configureColumn(row.getColumn(columnNames[index]), resword.getString("subject_status"), new StatusCellEditor(),
				new StatusDroplistFilterEditor());
		++index;
		configureColumn(row.getColumn(columnNames[index]), resword.getString("site_id"), null, null);
		++index;
		configureColumn(row.getColumn(columnNames[index]), resword.getString("rule_oid"), null, null);
		++index;

		if (currentStudy == null || currentStudy.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("true")) {
			configureColumn(row.getColumn(columnNames[index]), currentStudy == null ? resword.getString("gender")
					: currentStudy.getStudyParameterConfig().getGenderLabel(), null, null, true, false);
			++index;
		}
		if (currentStudy == null
				|| !currentStudy.getStudyParameterConfig().getSecondaryIdRequired().equalsIgnoreCase("not_used")) {
			configureColumn(row.getColumn(columnNames[index]), currentStudy == null ? resword.getString("secondary_ID")
					: currentStudy.getStudyParameterConfig().getSecondaryIdLabel(), null, null);
			++index;

		}

		// group class columns
		for (int i = index; i < index + studyGroupClasses.size(); i++) {
			StudyGroupClassBean studyGroupClass = studyGroupClasses.get(i - index);
			configureColumn(row.getColumn(columnNames[i]), studyGroupClass.getName(), new StudyGroupClassCellEditor(
					studyGroupClass), new SubjectGroupClassDroplistFilterEditor(studyGroupClass), true, false);
		}
		// study event definition columns
		for (int i = index + studyGroupClasses.size(); i < columnNames.length - 1; i++) {
			StudyEventDefinitionBean studyEventDefinition = studyEventDefinitions.get(i
					- (index + studyGroupClasses.size()));
			configureColumn(row.getColumn(columnNames[i]), studyEventDefinition.getName(),
					new StudyEventDefinitionMapCellEditor(), new SubjectEventStatusDroplistFilterEditor(), true, false);
		}
		String actionsHeader = resword.getString("rule_actions")
				+ "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
		configureColumn(row.getColumn(columnNames[columnNames.length - 1]), actionsHeader, new ActionsCellEditor(),
				new DefaultActionsEditor(locale), true, false);

	}

	@Override
	public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
		super.configureTableFacade(response, tableFacade);
		int stratFrom = getColumnNamesMap(tableFacade);
		tableFacade.addFilterMatcher(new MatcherKey(Character.class), new CharFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Status.class), new StatusFilterMatcher());

		for (int i = stratFrom; i < stratFrom + studyGroupClasses.size(); i++) {
			tableFacade
					.addFilterMatcher(new MatcherKey(Integer.class, columnNames[i]), new SubjectGroupFilterMatcher());
		}
		for (int i = stratFrom + studyGroupClasses.size(); i < columnNames.length - 1; i++) {
			tableFacade.addFilterMatcher(new MatcherKey(Integer.class, columnNames[i]),
					new SubjectEventStatusFilterMatcher());
		}

	}

	@Override
	public void configureTableFacadePostColumnConfiguration(TableFacade tableFacade) {
		Role r = currentRole.getRole();
		boolean addSubjectLinkShow = studyBean.getStatus().isAvailable() && !r.equals(Role.MONITOR);

		tableFacade.setToolbar(new ListStudySubjectTableToolbar(getStudyEventDefinitions(), getStudyGroupClasses(),
				addSubjectLinkShow, showMoreLink));
	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {
		Limit limit = tableFacade.getLimit();

		userRole = ((StudyUserRoleBean) tableFacade.getWebContext().getSessionAttribute("userRole")).getRole();

		FindSubjectsFilter subjectFilter = getSubjectFilter(limit);

		int totalRows = 0;
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
			theItem.put("studySubject.oid", studySubjectBean.getOid());
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
			theItem.put("isSignable", SignUtil.permitSign(studySubjectBean, new DAOWrapper(getStudyDAO(),
					getStudyEventDAO(), getStudySubjectDAO(), getEventCRFDAO(), getEventDefintionCRFDAO(),
					getDiscrepancyNoteDAO())));

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

			for (StudyEventDefinitionBean studyEventDefinition : getStudyEventDefinitions()) {
				List<StudyEventBean> studyEvents = allStudyEventsForStudySubjectBySedId.get(studyEventDefinition
						.getId());
				SubjectEventStatus subjectEventStatus = null;
				studyEvents = studyEvents == null ? new ArrayList<StudyEventBean>() : studyEvents;
				if (studyEvents.size() < 1) {
					subjectEventStatus = SubjectEventStatus.NOT_SCHEDULED;
				} else {
					for (StudyEventBean studyEventBean : studyEvents) {
						if (studyEventBean.getSampleOrdinal() == 1) {
							subjectEventStatus = studyEventBean.getSubjectEventStatus();
							break;
						}
					}

				}

				theItem.put("sed_" + studyEventDefinition.getId(), subjectEventStatus.getId());
				theItem.put("sed_" + studyEventDefinition.getId() + "_studyEvents", studyEvents);
				theItem.put("sed_" + studyEventDefinition.getId() + "_object", studyEventDefinition);

			}

			theItems.add(theItem);
		}

		// Do not forget to set the items back on the tableFacade.
		tableFacade.setItems(theItems);

	}

	private int getColumnNamesMap(TableFacade tableFacade) {
		int stratFrom = 4;
		StudyBean currentStudy = (StudyBean) tableFacade.getWebContext().getSessionAttribute("study");

		ArrayList<String> columnNamesList = new ArrayList<String>();
		columnNamesList.add("studySubject.label");
		columnNamesList.add("studySubject.createdDate");
		columnNamesList.add("studySubject.status");
		columnNamesList.add("enrolledAt");
		columnNamesList.add("studySubject.oid");
		if (currentStudy == null || currentStudy.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("true")) {
			stratFrom++;
			columnNamesList.add("subject.charGender");
		}
		if (currentStudy == null
				|| !currentStudy.getStudyParameterConfig().getSecondaryIdRequired().equalsIgnoreCase("not_used")) {
			stratFrom++;
			columnNamesList.add("studySubject.secondaryLabel");
		}
		for (StudyGroupClassBean studyGroupClass : getStudyGroupClasses()) {
			columnNamesList.add("sgc_" + studyGroupClass.getId());
		}
		for (StudyEventDefinitionBean studyEventDefinition : getStudyEventDefinitions()) {
			columnNamesList.add("sed_" + studyEventDefinition.getId());
		}
		columnNamesList.add("actions");
		columnNames = columnNamesList.toArray(columnNames);
		return stratFrom;
	}

	protected FindSubjectsFilter getSubjectFilter(Limit limit) {
		FindSubjectsFilter auditUserLoginFilter = new FindSubjectsFilter();
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
			auditUserLoginFilter.addFilter(property, value);
		}

		return auditUserLoginFilter;
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
		FindSubjectsSort auditUserLoginSort = new FindSubjectsSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			auditUserLoginSort.addSort(property, order);
		}

		return auditUserLoginSort;
	}

	private ArrayList<StudyEventDefinitionBean> getStudyEventDefinitions() {
		if (this.studyEventDefinitions == null) {
			if (studyBean.getParentStudyId() > 0) {
				studyEventDefinitions = getStudyEventDefinitionDao().findAllActiveByParentStudyId(
						studyBean.getParentStudyId());
			} else {
				studyEventDefinitions = getStudyEventDefinitionDao().findAllActiveByParentStudyId(studyBean.getId());
			}
		}
		return this.studyEventDefinitions;
	}

	private ArrayList<StudyGroupClassBean> getStudyGroupClasses() {
		if (this.studyGroupClasses == null) {
			if (studyBean.getParentStudyId() > 0) {
				StudyBean parentStudy = (StudyBean) getStudyDAO().findByPK(studyBean.getParentStudyId());
				studyGroupClasses = getStudyGroupClassDAO().findAllActiveByStudy(parentStudy);
			} else {
				studyGroupClasses = getStudyGroupClassDAO().findAllActiveByStudy(studyBean);
			}
		}
		return studyGroupClasses;
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

	private class CharFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			String item = StringUtils.lowerCase(String.valueOf(itemValue));
			String filter = StringUtils.lowerCase(String.valueOf(filterValue));
			if (StringUtils.contains(item, filter)) {
				return true;
			}

			return false;
		}
	}

	public class StatusFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {

			String item = StringUtils.lowerCase(String.valueOf(((Status) itemValue).getName()));
			String filter = StringUtils.lowerCase(String.valueOf(filterValue));

			if (filter.equals(item)) {
				return true;
			}
			return false;
		}
	}

	public class SubjectEventStatusFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			String item = StringUtils.lowerCase(SubjectEventStatus.getSubjectEventStatusName((Integer) itemValue));
			if (filterValue.equals(resterms.getString(item))) {
				return true;
			}
			return false;
		}
	}

	public class SubjectGroupFilterMatcher implements FilterMatcher {

		public boolean evaluate(Object itemValue, String filterValue) {

			String item = StringUtils
					.lowerCase(studyGroupDAO.findByPK(Integer.valueOf(itemValue.toString())).getName());
			String filter = StringUtils.lowerCase(String.valueOf(filterValue.trim()));
			if (filter.equals(item)) {
				return true;
			}
			return false;
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

	private class StudyGroupClassCellEditor implements CellEditor {

		StudyGroupClassBean studyGroupClass;
		String groupName;

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

		StudyEventDefinitionBean studyEventDefinition;
		StudySubjectBean studySubjectBean;
		SubjectEventStatus subjectEventStatus;
		List<StudyEventBean> studyEvents;
		SubjectBean subject;

		private String getCount() {
			return studyEvents.size() < 2 ? "" : "&nbsp;&nbsp;&nbsp;x" + String.valueOf(studyEvents.size() + "");
		}

		public Object getValue(Object item, String property, int rowcount) {

			studyEvents = (List<StudyEventBean>) ((HashMap<Object, Object>) item).get(property + "_studyEvents");
			studyEventDefinition = (StudyEventDefinitionBean) ((HashMap<Object, Object>) item)
					.get(property + "_object");
			subjectEventStatus = SubjectEventStatus.get((Integer) ((HashMap<Object, Object>) item).get(property));
			subject = (SubjectBean) ((HashMap<Object, Object>) item).get("subject");
			studySubjectBean = (StudySubjectBean) ((HashMap<Object, Object>) item).get("studySubject");

			StringBuilder url = new StringBuilder();
			url.append(eventDivBuilder(subject, rowcount, studyEvents, studyEventDefinition, studySubjectBean));

			SubjectEventStatusUtil.determineSubjectEventIconOnTheSubjectMatrix(url, imageIconPaths, studySubjectBean,
					studyEvents, subjectEventStatus, resword);

			url.append(getCount());
			url.append("</a></td></tr></table>");

			return url.toString();
		}

	}

	private class ActionsCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
			String value = "";
			StudySubjectBean studySubjectBean = (StudySubjectBean) ((HashMap<Object, Object>) item).get("studySubject");
			Boolean isSignable = (Boolean) ((HashMap<Object, Object>) item).get("isSignable");
			Integer studySubjectId = studySubjectBean.getId();
			if (studySubjectId != null) {
				StringBuilder url = new StringBuilder();
				url.append("<div style=\"padding-top: 3px;\">");
				url.append(viewStudySubjectLinkBuilder(studySubjectBean));
				if (getCurrentRole().getRole() != Role.MONITOR) {
					if (getStudyBean().getStatus() == Status.AVAILABLE
							&& !(studySubjectBean.getStatus() == Status.DELETED || studySubjectBean.getStatus() == Status.AUTO_DELETED)
							&& getCurrentRole().getRole() != Role.RESEARCHASSISTANT) {
						url.append(removeStudySubjectLinkBuilder(studySubjectBean));
					}
					if (getStudyBean().getStatus() == Status.AVAILABLE
							&& (studySubjectBean.getStatus() == Status.DELETED || studySubjectBean.getStatus() == Status.AUTO_DELETED)) {
						url.append(restoreStudySubjectLinkBuilder(studySubjectBean));
					}
					if (studySubjectBean.getStatus() != Status.DELETED
							&& (currentRole.getRole() == Role.COORDINATOR || currentRole.getRole() == Role.MONITOR)
							&& SDVUtil.permitSDV(studySubjectBean, new DAOWrapper(getStudyDAO(), getStudyEventDAO(),
									getStudySubjectDAO(), getEventCRFDAO(), getEventDefintionCRFDAO(),
									getStudyEventDefinitionDao(), getDiscrepancyNoteDAO()))) {
						url.append(sdvStudySubjectLinkBuilder(studySubjectBean));
					}
					if (getStudyBean().getStatus() == Status.AVAILABLE
							&& getCurrentRole().getRole() != Role.RESEARCHASSISTANT
							&& getCurrentRole().getRole() != Role.INVESTIGATOR
							&& studySubjectBean.getStatus() == Status.AVAILABLE) {
						url.append(reAssignStudySubjectLinkBuilder(studySubjectBean));
					}

					if (getCurrentRole().getRole() == Role.INVESTIGATOR
							&& getStudyBean().getStatus() == Status.AVAILABLE
							&& studySubjectBean.getStatus() != Status.DELETED) {
						url.append(signStudySubjectLinkBuilder(studySubjectBean, isSignable));
					}

					if (getCurrentRole().getRole() == Role.COORDINATOR) {
						url.append(studySubjectLockLinkBuilder(studySubjectBean));
					}
				}
				value = "</div>" + url.toString();
			}

			return value;
		}

	}

	private String viewStudySubjectLinkBuilder(StudySubjectBean studySubject) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href("ViewStudySubject?id=" + studySubject.getId());
		actionLink.append("onMouseDown=\"javascript:setImage('bt_View1','images/bt_View_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_View1','images/bt_View.gif');\"").close();
		actionLink.img().name("bt_View1").src("images/bt_View.gif").border("0").alt(resword.getString("view"))
				.title(resword.getString("view")).append("hspace=\"4\"").end().aEnd();
		
		return actionLink.toString();

	}

	private String removeStudySubjectLinkBuilder(StudySubjectBean studySubject) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href(
				"RemoveStudySubject?action=confirm&id=" + studySubject.getId() + "&subjectId="
						+ studySubject.getSubjectId() + "&studyId=" + studySubject.getStudyId());
		actionLink.append("onMouseDown=\"javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_Remove1','images/bt_Remove.gif');\"").close();
		actionLink.img().name("bt_Remove1").src("images/bt_Remove.gif").border("0").alt(resword.getString("remove"))
				.title(resword.getString("remove")).append("hspace=\"4\"").end().aEnd();
		return actionLink.toString();

	}

	private String signStudySubjectLinkBuilder(StudySubjectBean studySubject, boolean isSignable) {
		String result = "";
		boolean showHidden = !isSignable ? getStudyEventDAO().findAllByStudySubject(studySubject).size() == 0 : false;
		if (isSignable || showHidden) {
			HtmlBuilder actionLink = new HtmlBuilder();
			actionLink.a().id("button_signStudySubject_" + studySubject.getId())
					.href("SignStudySubject?id=" + studySubject.getId()).style(showHidden ? "display: none;" : "");
			actionLink.append("onMouseDown=\"javascript:setImage('icon_signed_blue','images/icon_SignedBlue.gif');\"");
			actionLink.append("onMouseUp=\"javascript:setImage('icon_signed_blue','images/icon_SignedBlue.gif');\"")
					.close();
			actionLink.img().name("bt_Sign1").src("images/icon_SignedBlue.gif").border("0")
					.alt(resword.getString("sign")).title(resword.getString("sign")).append("hspace=\"4\"").end().aEnd();
			result = actionLink.toString();
		}
		return result;
	}

	private String sdvStudySubjectLinkBuilder(StudySubjectBean studySubject) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink
				.a()
				.href("pages/viewSubjectAggregate?sbb=true&studyId="
						+ studyBean.getId()
						+ "&studySubjectId=&theStudySubjectId=0&redirection=viewSubjectAggregate&maxRows=15&showMoreLink=true&s_sdv_tr_=true&s_sdv_p_=1&s_sdv_mr_=15&s_sdv_f_studySubjectId="
						+ studySubject.getLabel()).close();
		actionLink.img().src("images/icon_DoubleCheck_Action.gif").border("0").alt(resword.getString("perform_sdv"))
				.title(resword.getString("perform_sdv")).append("hspace=\"4\"").end().aEnd();
		
		return actionLink.toString();

	}

	private String reAssignStudySubjectLinkBuilder(StudySubjectBean studySubject) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href("ReassignStudySubject?id=" + studySubject.getId());
		actionLink.append("onMouseDown=\"javascript:setImage('bt_Reassign1','images/bt_Reassign_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_Reassign1','images/bt_Reassign.gif');\"").close();
		actionLink.img().name("bt_Reassign1").src("images/bt_Reassign.gif").border("0")
				.alt(resword.getString("reassign")).title(resword.getString("reassign")).append("hspace=\"4\"").end()
				.aEnd();
		return actionLink.toString();
	}

	private String studySubjectLockLinkBuilder(StudySubjectBean studySubject) {
		String link = "";
		List<StudyEventBean> studyEventBeanList = getStudyEventDAO().findAllByStudySubject(studySubject);
		if (studyEventBeanList.size() > 0) {
			boolean allLocked = true;
			boolean hasLockedBy = false;
			for (StudyEventBean studyEventBean : studyEventBeanList) {
				hasLockedBy = !hasLockedBy ? studyEventBean.isWasLockedBy() : hasLockedBy;
				if (studyEventBean.getSubjectEventStatus() != SubjectEventStatus.LOCKED) {
					allLocked = false;
				}
			}

			HtmlBuilder actionLink1 = new HtmlBuilder();
			actionLink1.a().id("button_unlockStudySubject_" + studySubject.getId())
					.href("LockStudySubject?id=" + studySubject.getId() + "&action=unlock")
					.style(allLocked && hasLockedBy ? "" : "display: none;").close();
			actionLink1.img().src("images/bt__Unlock.png").border("0").alt(resword.getString("unlockStudySubject"))
					.title(resword.getString("unlockStudySubject")).append("hspace=\"8\"").end().aEnd();

			HtmlBuilder actionLink2 = new HtmlBuilder();
			actionLink2.a().id("button_lockStudySubject_" + studySubject.getId())
					.href("LockStudySubject?id=" + studySubject.getId() + "&action=lock")
					.style(!allLocked ? "" : "display: none;").close();
			actionLink2.img().src("images/bt__Lock.png").border("0").alt(resword.getString("lockStudySubject"))
					.title(resword.getString("lockStudySubject")).append("hspace=\"4\"").end().aEnd();

			link = actionLink1.toString() + " " + actionLink2.toString();
		}
		return link;
	}

	private String restoreStudySubjectLinkBuilder(StudySubjectBean studySubject) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href(
				"RestoreStudySubject?action=confirm&id=" + studySubject.getId() + "&subjectId="
						+ studySubject.getSubjectId() + "&studyId=" + studySubject.getStudyId());
		actionLink.append("onMouseDown=\"javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');\"").close();
		actionLink.img().name("bt_Restore1").src("images/bt_Restore.gif").border("0").alt(resword.getString("restore"))
				.title(resword.getString("restore")).append("hspace=\"4\"").end().aEnd();
		return actionLink.toString();

	}

	public String eventDivBuilder(SubjectBean subject, Integer rowCount, List<StudyEventBean> studyEvents,
			StudyEventDefinitionBean sed, StudySubjectBean studySubject, Locale locale) {
		this.locale = locale;
		resword = ResourceBundleProvider.getWordsBundle(locale);
		resformat = ResourceBundleProvider.getFormatBundle(locale);

		HtmlBuilder eventDiv = new HtmlBuilder();

		// Event Div
		eventDiv.div().close();

		eventDiv.div().styleClass("box_T").close().div().styleClass("box_L").close().div().styleClass("box_R").close()
				.div().styleClass("box_B").close().div().styleClass("box_TL").close().div().styleClass("box_TR")
				.close().div().styleClass("box_BL").close().div().styleClass("box_BR").close();

		eventDiv.div().styleClass("tablebox_center").close();
		eventDiv.div().styleClass("ViewSubjectsPopup").style("color: rgb(91, 91, 91);").close();

		eventDiv.table(0).border("0").cellpadding("0").cellspacing("0").style("width: 100%;").close();
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

		String divWidth = studyEvents.size() > 1 ? ("" + (WIDTH_600 + 50 + 8)) : ("" + (WIDTH_600 + 8));

		HtmlBuilder eventDiv = new HtmlBuilder();

		eventDiv.table(0).border("0").cellpadding("0").cellspacing("0").close();
		// Lock Div
		eventDiv.div().id("Lock_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount)
				.style("position: absolute; visibility: hidden; z-index: 3; width: 50px; height: 30px; top: 0px;")
				.close();
		if (studyEvents.size() > 1) {
			repeatingLockLinkBuilder(eventDiv, studySubjectLabel, rowCount, studyEvents, sed, "");
		} else {
			lockLinkBuilder(eventDiv, studySubjectLabel, rowCount, studyEvents, sed, "");
		}
		eventDiv.divEnd();

		eventDiv.tr(0).valign("top").close().td(0).close();
		// Event Div
		eventDiv.div()
				.id("Event_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount)
				.style("position: absolute; visibility: hidden; z-index: 3;width:" + divWidth
						+ "px; top: 0px; float: left;").rel("" + studySubject.getId()).close();

		eventDiv.div().styleClass("box_T").close().div().styleClass("box_L").close().div().styleClass("box_R").close()
				.div().styleClass("box_B").close().div().styleClass("box_TL").close().div().styleClass("box_TR")
				.close().div().styleClass("box_BL").close().div().styleClass("box_BR").close();

		eventDiv.div().styleClass("tablebox_center").close();
		eventDiv.div().styleClass("ViewSubjectsPopup").style("color: rgb(91, 91, 91);").close();

		eventDiv.table(0).border("0").cellpadding("0").cellspacing("0").style("width: 100%;").close();
		eventDiv.tr(0).valign("top").close();

		if (studyEvents.size() > 1) {
			repeatingEventDivBuilder(eventDiv, subject, rowCount, studyEvents, sed, studySubject);
		} else {
			singleEventDivBuilder(eventDiv, subject, rowCount, studyEvents, sed, studySubject);
		}

		return eventDiv.toString();
	}

	private void repeatingEventDivBuilder(HtmlBuilder eventDiv, SubjectBean subject, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed, StudySubjectBean studySubject) {

		String tableHeaderRowStyleClass = "table_header_row";
		String tableHeaderRowLeftStyleClass = "table_header_row_left";
		String add_another_occurrence = resword.getString("add_another_occurrence");
		String occurrence_x_of = resword.getString("ocurrence");
		String subjectText = resword.getString("subject");
		String eventText = resword.getString("event");
		String sdvText = resword.getString("sdv");

		studyEvents.get(0);
		String studySubjectLabel = SubjectLabelNormalizer.normalizeSubjectLabel(studySubject.getLabel());
		Status eventSysStatus = studySubject.getStatus();
		Integer studyEventsSize = studyEvents.size();

		eventDiv.td(0).styleClass(tableHeaderRowLeftStyleClass).colspan("2").close();

		eventDiv.div().style("display: none; width: 49%; float: left;").close();
		eventDiv.append(subjectText).append(": ").append(studySubject.getLabel()).br();
		eventDiv.append(
				eventText + ": <a id=\"" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
						+ "\" href=\"#\" style=\"color: #666;\">" + sed.getName() + "</a>").br();
		
		if (userRole == Role.COORDINATOR || userRole == Role.MONITOR) {
			eventDiv.append("<a class=\"sdvLink\" href=\""
					+ "pages/viewAllSubjectSDVtmp?sbb=true&studyId="
					+ studyBean.getId()
					+ "&imagePathPrefix=..%2F&crfId=0&redirection=viewAllSubjectSDVtmp&maxRows=15&showMoreLink=true&sdv_tr_=true&sdv_p_=1&sdv_mr_=15&sdv_f_studySubjectId="
					+ studySubjectLabel + "&sdv_f_eventName=" + sed.getName() + "\" style=\"color: #666;\">" + sdvText
					+ "</a><br class=\"sdvBR\"/>");
		}
		
		eventDiv.divEnd();

		eventDiv.div().style("width: 49%; float: right; text-align: right;").close();
		divCloseRepeatinglinkBuilder(eventDiv, studySubjectLabel, rowCount, studyEvents, sed);
		eventDiv.br();
		if (eventSysStatus != Status.DELETED && eventSysStatus != Status.AUTO_DELETED
				&& studyBean.getStatus() == Status.AVAILABLE) {
			eventDiv.span().styleClass("font-weight: normal;").close();
			eventDiv.ahref("CreateNewStudyEvent?studySubjectId=" + studySubject.getId() + "&studyEventDefinition="
					+ sed.getId(), add_another_occurrence);
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

			eventDiv.td(0).id("Event_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "_" + (i + 1))
					.rel("" + studyEventBean.getId()).valign("top").width("180").rel("" + studySubject.getId());
			if (i + 1 > 1) {
				eventDiv.style("display: none;");
			}
			eventDiv.close();
			eventDiv.table(0).border("0").cellpadding("0").cellspacing("0").width("100%").close();
			eventDiv.tr(0).valign("top").close();
			eventDiv.td(0).styleClass(tableHeaderRowStyleClass).style("border-bottom: none").colspan("2").close();
			eventDiv.bold().append(occurrence_x_of).append("#" + (i + 1) + " of " + studyEventsSize).br();
			eventDiv.append(formatDate(studyEventBean.getDateStarted())).br();

			eventDiv.boldEnd().tdEnd().trEnd(0);
			eventDiv.tr(0).id("Menu_on_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "_" + (i + 1))
					.style("display: all").close();
			eventDiv.td(0).colspan("2").close();
			eventDiv.table(0).border("0").cellpadding("0").cellspacing("0").width("100%").close();

			linksDivBuilder(eventDiv, subject, rowCount, studyEvents, sed, studySubject, studyEventBean);
			eventDiv.tableEnd(0).tdEnd().trEnd(0);
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

		eventDiv.tr(0).id("Menu_off_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount).style("display: none;")
				.close();
		eventDiv.td(0).styleClass("table_cell_left").colspan(String.valueOf(studyEventsSize)).close().append("<i>").append("</i>").tdEnd();
		eventDiv.trEnd(0);

		eventDiv.tableEnd(0);
		eventDiv.divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd();
		if (studyEvents.size() != 0
				|| (studyEvents.size() == 0 && canScheduleStudySubject(studySubject)
						&& currentRole.getRole() != Role.MONITOR && !studyBean.getStatus().isFrozen())) {
			repeatingIconLinkBuilder(eventDiv, studySubjectLabel, rowCount, studyEvents, sed,
					studyEvents.size() > 0 ? ("" + studyEvents.get(0).getId()) : "");
		}
	}

	private void linksDivBuilder(HtmlBuilder eventDiv, SubjectBean subject, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed, StudySubjectBean studySubject,
			StudyEventBean currentEvent) {

		Status eventSysStatus = studySubject.getStatus();
		SubjectEventStatus eventStatus = currentEvent.getSubjectEventStatus();
		String studyEventId = String.valueOf(currentEvent.getId());

		resword.getString("edit");
		String remove = resword.getString("remove");

		if (eventSysStatus.getId() == Status.AVAILABLE.getId() || eventSysStatus == Status.SIGNED
				|| eventSysStatus == Status.LOCKED) {

			if (eventStatus.isCompleted()) {
				eventDiv.tr(0).valign("top").close();
				eventDiv.td(0).styleClass("table_cell").close();
				eventDiv.div().id("crfListWrapper_" + studyEventId).style(WIDTH_600PX).close().divEnd();
				eventDiv.tdEnd().trEnd(0);

			} else if (eventStatus == SubjectEventStatus.LOCKED) {
				if (currentRole.getRole() == Role.STUDYDIRECTOR || currentUser.isSysAdmin()) {
					eventDiv.tr(0).valign("top").close();
					eventDiv.td(0).styleClass("table_cell").close();
					eventDiv.div().id("crfListWrapper_" + studyEventId).style(WIDTH_600PX).close().divEnd();
					eventDiv.tdEnd().trEnd(0);
					if (studyBean.getStatus() == Status.AVAILABLE) {
						eventDiv.tr(0).valign("top").close();
						eventDiv.td(0).styleClass("table_cell").close();
						removeStudyEventLinkBuilder(eventDiv, studySubject.getId(), studyEventId, remove);
						eventDiv.tdEnd().trEnd(0);
					}
				}
			} else {
				eventDiv.tr(0).valign("top").close();
				eventDiv.td(0).styleClass("table_cell_left").close();
				eventDiv.div().id("crfListWrapper_" + studyEventId).style(WIDTH_600PX).close().divEnd();
				eventDiv.tdEnd().trEnd(0);
			}
		}

		if (eventSysStatus == Status.DELETED || eventSysStatus == Status.AUTO_DELETED) {
			eventDiv.tr(0).valign("top").close();
			eventDiv.td(0).styleClass("table_cell").close();
			eventDiv.div().id("crfListWrapper_" + studyEventId).style(WIDTH_600PX).close().divEnd();
			eventDiv.tdEnd().trEnd(0);
		}

	}

	private void singleEventDivBuilder(HtmlBuilder eventDiv, SubjectBean subject, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed, StudySubjectBean studySubject) {

		String tableHeaderRowStyleClass = "table_header_row";
		String tableHeaderRowLeftStyleClass = "table_header_row_left";
		String add_another_occurrence = resword.getString("add_another_occurrence");
		String click_for_more_options = resword.getString("click_for_more_options");
		resword.getString("schedule");
		resword.getString("edit");
		String remove = resword.getString("remove");
		String occurrence_x_of = resword.getString("ocurrence");
		String subjectText = resword.getString("subject");
		String eventText = resword.getString("event");
		String status = resword.getString("status");
		String sdvText = resword.getString("sdv");
		resword.getString("sourceDataVerified");

		SubjectEventStatus eventStatus = studyEvents.size() == 0 ? SubjectEventStatus.NOT_SCHEDULED : studyEvents
				.get(0).getSubjectEventStatus();

		String studyEventId = studyEvents.size() == 0 ? "" : String.valueOf(studyEvents.get(0).getId());
		Status eventSysStatus = studySubject.getStatus();
		String studySubjectLabel = SubjectLabelNormalizer.normalizeSubjectLabel(studySubject.getLabel());

		eventDiv.td(0).styleClass(tableHeaderRowLeftStyleClass).style("display: none").close();
		eventDiv.append(subjectText).append(": ").append(studySubject.getLabel()).br();
		if (studyEventId == null || "".equals(studyEventId)) {
			eventDiv.append(eventText).append(": ").append(sed.getName()).br();
		} else {
			eventDiv.append(
					eventText + ": <a id=\"EventId_" + studyEventId + "\" href=\"" + "UpdateStudyEvent?event_id="
							+ studyEventId + "&ss_id=" + studySubject.getId() + "\" style=\"color: #666;\">"
							+ sed.getName() + "</a>").br();
		}
		if (userRole == Role.COORDINATOR || userRole == Role.MONITOR) {
			eventDiv.append("<a class=\"sdvLink\" href=\""
					+ "pages/viewAllSubjectSDVtmp?sbb=true&studyId="
					+ studyBean.getId()
					+ "&imagePathPrefix=..%2F&crfId=0&redirection=viewAllSubjectSDVtmp&maxRows=15&showMoreLink=true&sdv_tr_=true&sdv_p_=1&sdv_mr_=15&sdv_f_studySubjectId="
					+ studySubjectLabel + "&sdv_f_eventName=" + sed.getName() + "\" style=\"color: #666;\">" + sdvText
					+ "</a><br class=\"sdvBR\"/>");
		}
		eventDiv.divEnd();

		if (!sed.isRepeating()) {
			eventDiv.append(resword.getString("status")).append(": ").append(eventStatus.getName()).br();
			eventDiv.tdEnd();
			eventDiv.td(0).styleClass(tableHeaderRowLeftStyleClass).style("display: none").align("right").close();
			eventDiv.tdEnd();

		} else {
			eventDiv.tdEnd();
			eventDiv.td(0).styleClass(tableHeaderRowLeftStyleClass).style("display: none").align("right").close();
			eventDiv.tdEnd();

			eventDiv.tr(0).valign("top").close();
			eventDiv.td(0).styleClass(tableHeaderRowStyleClass).style("border-bottom: none").colspan("2").close();
			eventDiv.bold().append(occurrence_x_of).append("#1 of 1").br();
			if (studyEvents.size() > 0) {
				eventDiv.append(formatDate(studyEvents.get(0).getDateStarted())).br();
				
			} else {
				eventDiv.append(status + " : " + SubjectEventStatus.NOT_SCHEDULED.getName());
			}
			eventDiv.boldEnd().tdEnd().trEnd(0);
			if (eventStatus != SubjectEventStatus.NOT_SCHEDULED && eventSysStatus != Status.DELETED
					&& eventSysStatus != Status.AUTO_DELETED) {
				eventDiv.tr(0).close().td(0).styleClass("table_cell_left").close();
				eventDiv.ahref("CreateNewStudyEvent?studySubjectId=" + studySubject.getId() + "&studyEventDefinition="
						+ sed.getId(), add_another_occurrence);
				eventDiv.tdEnd().trEnd(0);
			}

		}
		eventDiv.trEnd(0);
		eventDiv.tr(0).id("Menu_off_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount).style("display: none")
				.close();
		eventDiv.td(0).styleClass("table_cell_left").colspan("2").close().append("<i>").append(click_for_more_options)
				.append("</i>").tdEnd();
		eventDiv.trEnd(0);

		eventDiv.tr(0).id("Menu_on_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount).style("display: all")
				.close();
		eventDiv.td(0).colspan("2").close();
		eventDiv.table(0).border("0").cellpadding("0").cellspacing("0").width("100%").close();

		if (eventSysStatus.getId() == Status.AVAILABLE.getId() || eventSysStatus == Status.SIGNED
				|| eventSysStatus == Status.LOCKED) {

			if (eventStatus == SubjectEventStatus.NOT_SCHEDULED && canScheduleStudySubject(studySubject)
					&& currentRole.getRole() != Role.MONITOR && !studyBean.getStatus().isFrozen()) {
				eventDiv.tr(0).valign("top").close();
				eventDiv.td(0).styleClass("table_cell_left").close();
				String href1 = "PageToCreateNewStudyEvent?studySubjectId=" + studySubject.getId()
						+ "&studyEventDefinition=" + sed.getId();
				eventDiv.div().id("eventScheduleWrapper_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount)
						.rel(href1).style(WIDTH_600PX).close().divEnd();
				eventDiv.tdEnd().trEnd(0);
			}

			else if (eventStatus.isCompleted()) {
				eventDiv.tr(0).valign("top").close();
				eventDiv.td(0).styleClass("table_cell_left").close();
				eventDiv.div().id("crfListWrapper_" + studyEventId).style(WIDTH_600PX).close().divEnd();
				eventDiv.tdEnd().trEnd(0);
				
			}

			else if (eventStatus == SubjectEventStatus.LOCKED) {
				eventDiv.tdEnd().trEnd(0);
				if (currentRole.getRole() == Role.STUDYDIRECTOR || currentUser.isSysAdmin()) {
					eventDiv.tr(0).valign("top").close();
					eventDiv.td(0).styleClass("table_cell_left").close();
					eventDiv.div().id("crfListWrapper_" + studyEventId).style(WIDTH_600PX).close().divEnd();
					eventDiv.tdEnd().trEnd(0);
					if (studyBean.getStatus() == Status.AVAILABLE) {
						eventDiv.tr(0).valign("top").close();
						eventDiv.td(0).styleClass("table_cell_left").close();
						removeStudyEventLinkBuilder(eventDiv, studySubject.getId(), studyEventId, remove);
						eventDiv.tdEnd().trEnd(0);
					}
				}
			} else {
				eventDiv.tr(0).valign("top").close();
				eventDiv.td(0).styleClass("table_cell_left").close();
				eventDiv.div().id("crfListWrapper_" + studyEventId).style(WIDTH_600PX).close().divEnd();
				eventDiv.tdEnd().trEnd(0);
			}
		}

		if (eventSysStatus == Status.DELETED || eventSysStatus == Status.AUTO_DELETED) {
			eventDiv.tr(0).valign("top").close();
			eventDiv.td(0).styleClass("table_cell_left").close();
			eventDiv.div().id("crfListWrapper_" + studyEventId).style(WIDTH_600PX).close().divEnd();
			eventDiv.tdEnd().trEnd(0);
		}
		eventDiv.tableEnd(0).tdEnd().trEnd(0);

		eventDiv.tableEnd(0);
		eventDiv.divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd();
		if (eventStatus != SubjectEventStatus.NOT_SCHEDULED
				|| (eventStatus == SubjectEventStatus.NOT_SCHEDULED && canScheduleStudySubject(studySubject)
						&& currentRole.getRole() != Role.MONITOR && !studyBean.getStatus().isFrozen())) {
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

	private void lockLinkBuilder(HtmlBuilder builder, String studySubjectLabel, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed, String studyEventId) {
		String href1 = "javascript:leftnavExpand('Menu_on_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
				+ "'); ";
		String href2 = "javascript:leftnavExpand('Menu_off_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
				+ "'); ";
		String onmouseover = "layersShowOrHide('visible','Event_" + studySubjectLabel + "_" + sed.getId() + "_"
				+ rowCount + "'); ";
		onmouseover += "javascript:setImage('ExpandIcon_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
				+ "','images/icon_collapse.gif');";
		builder.a().href(href1 + href2);
		builder.onmouseover(onmouseover);
		builder.close();
		builder.img().src("images/spacer.gif").border("0").append("height=\"30\"").width("50").close().aEnd();

	}

	private void repeatingLockLinkBuilder(HtmlBuilder builder, String studySubjectLabel, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed, String studyEventId) {
		String href1 = "javascript:ExpandEventOccurrences('" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
				+ "'," + studyEvents.size() + "); ";
		String href2 = "javascript:leftnavExpand('Menu_off_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
				+ "'); ";
		String onmouseover = "layersShowOrHide('visible','Event_" + studySubjectLabel + "_" + sed.getId() + "_"
				+ rowCount + "'); ";
		onmouseover += "javascript:setImage('ExpandIcon_" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
				+ "','images/icon_collapse.gif');";
		builder.a().href(href1 + href2);
		builder.onmouseover(onmouseover);
		builder.close();
		builder.img().src("images/spacer.gif").border("0").append("height=\"30\"").width("50").close().aEnd();

	}

	private void repeatingIconLinkBuilder(HtmlBuilder builder, String studySubjectLabel, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed, String studyEventId) {
		
		String params = "'" + studyEventId + "', '" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount + "',"
				+ studyEvents.size() + ", event";
		
		builder.a().style("cursor: pointer;");// .href(href1 + href2);
		builder.onmouseover("if (canShowPopup('" + studyEventId + "')) { showPopup(" + params + "); } ");
		builder.onmouseout("clearInterval(popupInterval);");
		builder.onclick("justShowPopup(" + params + ");");
		builder.close();

	}

	private void iconLinkBuilder(HtmlBuilder builder, String studySubjectLabel, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed, String studyEventId) {
		
		String params = "'" + studyEventId + "', '" + studySubjectLabel + "_" + sed.getId() + "_" + rowCount
				+ "', undefined, event";
		
		builder.a().style("cursor: pointer;");
		builder.onmouseover("if (canShowPopup('" + studyEventId + "')) { showPopup(" + params + "); } ");
		builder.onmouseout("clearInterval(popupInterval);");
		builder.onclick("justShowPopup(" + params + ");");
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
		String format = resformat.getString("date_format_string");
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

}
