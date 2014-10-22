package com.clinovo.model;

import com.clinovo.jmesa.evaluation.CRFEvaluationFilter;
import com.clinovo.jmesa.evaluation.CRFEvaluationItem;
import com.clinovo.jmesa.evaluation.CRFEvaluationSort;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.web.table.filter.StudyEventTableRowFilter;
import org.jmesa.core.filter.FilterMatcher;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.editor.DroplistFilterEditor;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CRFEvaluationTableFactory class.
 *
 */
@SuppressWarnings({ "rawtypes" })
public class CRFEvaluationTableFactory extends AbstractTableFactory {

	public static final int TEN = 10;
	public static final int NINE = 9;
	public static final int ZERO = 0;
	public static final int EIGHT = 8;

	public static final String YES = "yes";
	public static final String TRUE = "true";
	public static final String PRINT = "print";
	public static final String STUDY = "study";
	public static final String LOCKED = "locked";
	public static final String SIGNED = "signed";
	public static final String OBJECT = "object";
	public static final String REMOVED = "removed";
	public static final String CRF_NAME = "crfName";
	public static final String COMPLETED = "completed";
	public static final String VIEW_DATA = "view_data";
	public static final String EVENT_NAME = "eventName";
	public static final String CRF_STATUS = "crfStatus";
	public static final String EVALUATION = "evaluation";
	public static final String ACTION_COLUMN = "actionColumn";
	public static final String IDE_COMPLETED = "ideCompleted";
	public static final String STUDY_SUBJECT_ID = "studySubjectId";
	public static final String ICON_FORCRFSTATUS_SUFFIX = ".gif'/>";
	public static final String DOUBLE_DATA_ENTRY = "DoubleDataEntry";
	public static final String BT_ENTER_DATA_GIF = "bt_EnterData.gif";
	public static final String INITIAL_DATA_ENTRY = "InitialDataEntry";
	public static final String EVALUATION_STATUS = "evaluation_status";
	public static final String BT_TRANSPARENT_GIF = "bt_Transparent.gif";
	public static final String DATA_ENTRY_STARTED = "data_entry_started";
	public static final String CRF_EVALUATION_TABLE = "crfEvaluationTable";
	public static final String BEGIN_CRF_EVALUATION = "begin_crf_evaluation";
	public static final String SOURCE_DATA_VERIFIED = "source_data_verified";
	public static final String ADMINISTRATIVE_EDITING = "AdministrativeEditing";
	public static final String CONTINUE_ENTERING_DATA = "continue_entering_data";
	public static final String DOUBLE_DATA_ENTRY_STARTED = "double_data_entry_start";
	public static final String CRF_EVALUATION_TABLE_CRF_NAME = "crfEvaluationTable.crfName";
	public static final String CRF_EVALUATION_TABLE_EVENT_NAME = "crfEvaluationTable.eventName";
	public static final String CRF_EVALUATION_TABLE_CRF_STATUS = "crfEvaluationTable.crfStatus";
	public static final String CRF_EVALUATION_TABLE_ACTION_COLUMN = "crfEvaluationTable.actionColumn";
	public static final String CRF_EVALUATION_TABLE_STUDY_SUBJECT_ID = "crfEvaluationTable.studySubjectId";

	public static final Map<Integer, String> CRF_STATUS_ICONS = new HashMap<Integer, String>();

	public static final String ICON_DDE = "DDE";
	public static final String ICON_SIGNED = "Signed";
	public static final String ICON_LOCKED = "Locked";
	public static final String ICON_INVALID = "Invalid";
	public static final String ICON_INITIAL_DE = "InitialDE";
	public static final String ICON_INITIAL_DE1 = "InitialDE";
	public static final String ICON_D_ECOMPLETE = "DEcomplete";
	public static final String ICON_NOT_STARTED = "NotStarted";
	public static final String ICON_DOUBLE_CHECK = "DoubleCheck";
	public static final String ICON_INITIAL_D_ECOMPLETE = "InitialDEcomplete";

	static {
		int index = 0;
		CRF_STATUS_ICONS.put(index++, ICON_INVALID);
		CRF_STATUS_ICONS.put(index++, ICON_NOT_STARTED);
		CRF_STATUS_ICONS.put(index++, ICON_INITIAL_DE);
		CRF_STATUS_ICONS.put(index++, ICON_INITIAL_D_ECOMPLETE);
		CRF_STATUS_ICONS.put(index++, ICON_DDE);
		CRF_STATUS_ICONS.put(index++, ICON_D_ECOMPLETE);
		CRF_STATUS_ICONS.put(index++, ICON_INITIAL_DE1);
		CRF_STATUS_ICONS.put(index++, ICON_LOCKED);
		CRF_STATUS_ICONS.put(index++, ICON_DOUBLE_CHECK);
		CRF_STATUS_ICONS.put(index, ICON_SIGNED);
	}

	private DataSource dataSource;

	private StudyBean currentStudy;
	private UserAccountBean userBean;

	private String contextPath;
	private boolean showMoreLink;
	private boolean evaluateWithContext;
	private MessageSource messageSource;

	private Map<Object, Status> optionsMap = new LinkedHashMap<Object, Status>();

	/**
	 * CRFEvaluationTableFactory constructor.
	 * 
	 * @param dataSource
	 *            DataSource
	 * @param messageSource
	 *            MessageSource
	 * @param evaluateWithContext
	 *            StudyParameterValueBean
	 * @param showMoreLink
	 *            String
	 */
	public CRFEvaluationTableFactory(DataSource dataSource, MessageSource messageSource,
			StudyParameterValueBean evaluateWithContext, String showMoreLink) {
		this.dataSource = dataSource;
		this.messageSource = messageSource;
		this.showMoreLink = showMoreLink != null && showMoreLink.equals(TRUE);
		this.evaluateWithContext = evaluateWithContext.getValue().equals(YES);
	}

	@Override
	protected String getTableName() {
		return CRF_EVALUATION_TABLE;
	}

	@Override
	protected void configureColumns(TableFacade tableFacade, Locale locale) {
		contextPath = tableFacade.getWebContext().getContextPath();

		tableFacade.setColumnProperties(CRF_NAME, STUDY_SUBJECT_ID, EVENT_NAME, CRF_STATUS, EVALUATION_STATUS,
				ACTION_COLUMN);

		Row row = tableFacade.getTable().getRow();

		configureColumn(row.getColumn(CRF_NAME), messageSource.getMessage(CRF_EVALUATION_TABLE_CRF_NAME, null, locale),
				null, new EvaluableCRFsFilter(), true, true);
		configureColumn(row.getColumn(STUDY_SUBJECT_ID),
				messageSource.getMessage(CRF_EVALUATION_TABLE_STUDY_SUBJECT_ID, null, locale), null, null, true, true);
		configureColumn(row.getColumn(EVENT_NAME),
				messageSource.getMessage(CRF_EVALUATION_TABLE_EVENT_NAME, null, locale), null,
				new StudyEventTableRowFilter(dataSource, currentStudy), true, false);
		configureColumn(row.getColumn(CRF_STATUS),
				messageSource.getMessage(CRF_EVALUATION_TABLE_CRF_STATUS, null, locale), new CRFStatusCellEditor(),
				new CrfStatusFilter(), true, false);
		configureColumn(row.getColumn(EVALUATION_STATUS), messageSource.getMessage(EVALUATION_STATUS, null, locale),
				new EvaluationStatusCellEditor(), new EvaluationStatusFilter(), true, false);
		configureColumn(row.getColumn(ACTION_COLUMN),
				messageSource.getMessage(CRF_EVALUATION_TABLE_ACTION_COLUMN, null, locale), new ActionsCellEditor(),
				new DefaultActionsEditor(locale), true, false);
	}

	private class EvaluableCRFsFilter extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			for (CRFBean crf : new CRFDAO(dataSource).findAllEvaluableCrfs(currentStudy.getId())) {
				options.add(new Option(crf.getName(), crf.getName()));
			}
			return options;
		}
	}

	private String getIconForCrfStatusPrefix(String title) {
		return "<img hspace='2' border='0'  title='".concat(title).concat("' alt='").concat(title)
				.concat("' src='../images/icon_");
	}

	private String getCRFStatusIconPath(CRFEvaluationItem crfEvaluationItem) {
		DataEntryStage stage = crfEvaluationItem.getStage();

		int statusId = stage.getId();
		String title = statusId > ZERO ? stage.getNormalizedName() : crfEvaluationItem.getStatus().getNormalizedName();

		if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)) {
			title = messageSource.getMessage(IDE_COMPLETED, null, locale);
		}
		if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
			title = messageSource.getMessage(EVALUATION, null, locale);
		}

		if (crfEvaluationItem.getSubjectEventStatus() == SubjectEventStatus.REMOVED) {
			title = SubjectEventStatus.REMOVED.getNormalizedName();
		} else if (crfEvaluationItem.getSubjectEventStatus() == SubjectEventStatus.LOCKED
				|| crfEvaluationItem.getSubjectEventStatus() == SubjectEventStatus.STOPPED
				|| crfEvaluationItem.getSubjectEventStatus() == SubjectEventStatus.SKIPPED) {
			statusId = DataEntryStage.LOCKED.getId();
			title = DataEntryStage.LOCKED.getNormalizedName();
		} else if (crfEvaluationItem.getSubjectEventStatus() == SubjectEventStatus.SIGNED
				&& crfEvaluationItem.getStatus().equals(Status.UNAVAILABLE)) {
			statusId = NINE;
			title = SubjectEventStatus.SIGNED.getNormalizedName();
		} else if (crfEvaluationItem.isSdv() && crfEvaluationItem.getStatus().equals(Status.UNAVAILABLE)) {
			statusId = EIGHT;
			title = SubjectEventStatus.SOURCE_DATA_VERIFIED.getNormalizedName();
		}

		StringBuilder builder = new StringBuilder().append(getIconForCrfStatusPrefix(title));

		if (statusId > ZERO && statusId < TEN) {
			builder.append(CRF_STATUS_ICONS.get(statusId));
		} else {
			builder.append(CRF_STATUS_ICONS.get(ZERO));
		}

		builder.append(ICON_FORCRFSTATUS_SUFFIX).append("<br/><input type=\"hidden\" statusId=\"").append(statusId)
				.append("\" />");
		return builder.toString();
	}

	private class CRFStatusCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowCount) {
			return getCRFStatusIconPath((CRFEvaluationItem) ((HashMap) item).get(OBJECT));
		}
	}

	private class ActionsCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowCount) {
			CRFEvaluationItem crfEvaluationItem = (CRFEvaluationItem) ((HashMap) item).get(OBJECT);

			StringBuilder builder = new StringBuilder();

			String additionalAttr = " data-cc-crfEvaluationId=\"".concat(
					Integer.toString(crfEvaluationItem.getEventCrfId())).concat("\"");

			HtmlBuilder html = new HtmlBuilder()
					.a().href("#")
					.onclick("setAccessedObjected(this); openDocWindow('".concat(contextPath)
									.concat("/ViewSectionDataEntry?eventDefinitionCRFId=")
									.concat(Integer.toString(crfEvaluationItem.getEventDefinitionCrfId()))
									.concat("&eventCRFId=").concat(Integer.toString(crfEvaluationItem.getEventCrfId()))
									.concat("&tabId=1&eventId=")
									.concat(Integer.toString(crfEvaluationItem.getStudyEventId()))
									.concat("&studySubjectId=")
									.concat(Integer.toString(crfEvaluationItem.getStudySubjectId())).concat("&cw=1');"));

			String printMessage = messageSource.getMessage(PRINT, null, locale);
			String viewMessage = messageSource.getMessage(VIEW_DATA, null, locale);
			String enterDataMessage = crfEvaluationItem.getStage().equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) ? DataEntryStage.ADMINISTRATIVE_EDITING
					.getNormalizedName() : (crfEvaluationItem.getStage().equals(
					DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) ? messageSource.getMessage(BEGIN_CRF_EVALUATION, null,
					locale) : messageSource.getMessage(CONTINUE_ENTERING_DATA, null, locale));

			String dataEntryPage = crfEvaluationItem.getStage().equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
					|| crfEvaluationItem.getStage().equals(DataEntryStage.DOUBLE_DATA_ENTRY) ? DOUBLE_DATA_ENTRY
					: (crfEvaluationItem.getStage().equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) ? ADMINISTRATIVE_EDITING
							: INITIAL_DATA_ENTRY);

			String enterDataImg = BT_ENTER_DATA_GIF;

			String aLink = "<a href=\"#\" onclick=\"setAccessedObjected(this); checkCRFLocked('"
					.concat(Integer.toString(crfEvaluationItem.getEventCrfId())).concat("', '../")
					.concat(dataEntryPage).concat("?eventCRFId=")
					.concat(Integer.toString(crfEvaluationItem.getEventCrfId())).concat("&cw=1');\"")
					.concat(additionalAttr).concat(">");

			if (crfEvaluationItem.getSubjectEventStatus().equals(SubjectEventStatus.REMOVED)
					|| crfEvaluationItem.getSubjectEventStatus().equals(SubjectEventStatus.LOCKED)
					|| crfEvaluationItem.getSubjectEventStatus().equals(SubjectEventStatus.STOPPED)
					|| crfEvaluationItem.getSubjectEventStatus().equals(SubjectEventStatus.SKIPPED)
					|| (userBean.getId() == crfEvaluationItem.getOwnerId() && crfEvaluationItem.getStage().equals(
							DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE))) {
				aLink = "";
				enterDataMessage = "";
				enterDataImg = BT_TRANSPARENT_GIF;
			}

			builder.append(aLink).append("<img name=\"bt_EnterData\" src=\"../images/").append(enterDataImg)
					.append("\" border=\"0\" alt=\"").append(enterDataMessage).append("\" title=\"")
					.append(enterDataMessage).append("\" align=\"left\" hspace=\"4\"></a>");
			builder.append(html.toString())
					.append(additionalAttr)
					.append("><img name=\"bt_View\" src=\"../images/bt_View.gif\" border=\"0\" alt=\""
							.concat(viewMessage).concat("\" title=\"").concat(viewMessage)
							.concat("\" align=\"left\" hspace=\"4\"></a>"));
			builder.append("<a href=\"#\" onclick=\"setAccessedObjected(this); openDocWindow('../PrintDataEntry?ecId="
					.concat(Integer.toString(crfEvaluationItem.getEventCrfId())).concat("')\"").concat(additionalAttr)
					.concat("><img name=\"bt_Print\" src=\"../images/bt_Print.gif\" border=\"0\" alt=\"")
					.concat(printMessage).concat("\" title=\"").concat(printMessage)
					.concat("\" align=\"left\" hspace=\"4\"></a>"));

			return builder.toString();
		}
	}

	private class CrfStatusFilter extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> optionList = new ArrayList<Option>();
			for (Object name : optionsMap.keySet()) {
				optionList.add(new Option((String) name, (String) name));
			}
			return optionList;
		}
	}

	@Override
	public void configureTableFacadePostColumnConfiguration(TableFacade tableFacade) {
		tableFacade.setToolbar(new CRFEvaluationTableToolbar(evaluateWithContext, showMoreLink));
	}

	@Override
	public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
		super.configureTableFacade(response, tableFacade);
		tableFacade.addFilterMatcher(new MatcherKey(String.class, CRF_NAME), new DefaultFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, STUDY_SUBJECT_ID), new DefaultFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, EVENT_NAME), new DefaultFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, CRF_STATUS), new DefaultFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, EVALUATION_STATUS), new DefaultFilterMatcher());
	}

	private class DefaultFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			return true;
		}
	}

	private CRFEvaluationFilter getCRFEvaluationFilter(Limit limit) {
		CRFEvaluationFilter crfEvaluationFilter = new CRFEvaluationFilter(optionsMap);
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			crfEvaluationFilter.addFilter(filter.getProperty(), filter.getValue());
		}
		return crfEvaluationFilter;
	}

	private CRFEvaluationSort getCRFEvaluationSort(Limit limit) {
		CRFEvaluationSort crfEvaluationSort = new CRFEvaluationSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			crfEvaluationSort.addSort(sort.getProperty(), sort.getOrder().toParam());
		}
		return crfEvaluationSort;
	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {
		Limit limit = tableFacade.getLimit();
		CRFEvaluationFilter crfEvaluationFilter = getCRFEvaluationFilter(limit);

		optionsMap.put(messageSource.getMessage(IDE_COMPLETED, null, locale), Status.DATA_ENTRY_STARTED);
		optionsMap.put(messageSource.getMessage(DOUBLE_DATA_ENTRY_STARTED, null, locale), Status.DOUBLE_DATA_ENTRY);
		optionsMap.put(messageSource.getMessage(COMPLETED, null, locale), Status.COMPLETED);
		optionsMap.put(org.akaza.openclinica.bean.core.Term.normalizeString(messageSource.getMessage(
				SOURCE_DATA_VERIFIED, null, locale)), Status.SOURCE_DATA_VERIFIED);
		optionsMap.put(
				org.akaza.openclinica.bean.core.Term.normalizeString(messageSource.getMessage(SIGNED, null, locale)),
				Status.SIGNED);
		optionsMap.put(
				org.akaza.openclinica.bean.core.Term.normalizeString(messageSource.getMessage(REMOVED, null, locale)),
				Status.DELETED);
		optionsMap.put(
				org.akaza.openclinica.bean.core.Term.normalizeString(messageSource.getMessage(LOCKED, null, locale)),
				Status.LOCKED);

		currentStudy = (StudyBean) tableFacade.getWebContext().getSessionAttribute(BaseController.STUDY);
		userBean = (UserAccountBean) tableFacade.getWebContext().getSessionAttribute(BaseController.USER_BEAN_NAME);

		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);

		int totalRows = 0;
		if (!limit.isComplete()) {
			totalRows = eventCRFDAO.countOfAllEventCrfsForEvaluation(crfEvaluationFilter, currentStudy);
			tableFacade.setTotalRows(totalRows);
		}

		CRFEvaluationSort crfEvaluationSort = getCRFEvaluationSort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		List<CRFEvaluationItem> crfEvaluationItems = totalRows == 0 ? new ArrayList<CRFEvaluationItem>() : eventCRFDAO
				.findAllEventCrfsForEvaluation(currentStudy, crfEvaluationFilter, crfEvaluationSort, rowStart, rowEnd);
		Collection<HashMap<Object, Object>> crfEvaluationItemsResult = new ArrayList<HashMap<Object, Object>>();

		for (CRFEvaluationItem crfEvaluationItem : crfEvaluationItems) {
			HashMap<Object, Object> h = new HashMap<Object, Object>();
			h.put(OBJECT, crfEvaluationItem);
			h.put(CRF_NAME, crfEvaluationItem.getCrfName());
			h.put(STUDY_SUBJECT_ID, crfEvaluationItem.getStudySubjectLabel());
			h.put(EVENT_NAME, crfEvaluationItem.getStudyEventName());
			h.put(CRF_STATUS, getCRFStatusIconPath(crfEvaluationItem));
			h.put(EVALUATION_STATUS,
					crfEvaluationItem.getDateValidateCompleted() != null ? messageSource.getMessage(
							"evaluation_completed", null, locale) : messageSource.getMessage("ready_for_evaluation",
							null, locale));
			crfEvaluationItemsResult.add(h);
		}

		tableFacade.setItems(crfEvaluationItemsResult);

		HtmlBuilder htmlBuilder = createSummaryTable();
		tableFacade.getWebContext().setRequestAttribute("summaryTable", htmlBuilder.toString());
	}

	private HtmlBuilder createSummaryTable() {
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		int totalRows = eventCRFDAO.countOfAllEventCrfsForEvaluation(new CRFEvaluationFilter(optionsMap), currentStudy);
		List<CRFEvaluationItem> crfEvaluationItems = eventCRFDAO.findAllEventCrfsForEvaluation(currentStudy,
				new CRFEvaluationFilter(optionsMap), new CRFEvaluationSort(), 0, totalRows);
		int evaluationCompleted = 0;

		for (CRFEvaluationItem item : crfEvaluationItems) {
			if (item.getDateValidateCompleted() != null) {
				evaluationCompleted++;
			}
		}
		int evaluationReadyNumber = totalRows - evaluationCompleted;

		HtmlBuilder summaryTable = new HtmlBuilder();
		summaryTable.a().id("sumBoxParent").href("javascript:void(0)").onclick("showSummaryBox('sumBox',document.getElementById('sumBoxParent'),'"
				+ messageSource.getMessage("show_summary_statistics", null, locale) + "','"
				+ messageSource.getMessage("hide_summary_statistics", null, locale) + "')").close()
				.img().name("ExpandGroup1").src("../images/bt_Collapse.gif").border("0").close()
				.append(" ").append(messageSource.getMessage("hide_summary_statistics", null, locale)).aEnd().divEnd().br()
				.div().id("sumBox").style("clear:left;float:left").close()
				.table(1).styleClass("summaryTable").width("600px").cellspacing("0").close()
				.tr(1).close().td(1).close().nbsp().tdEnd()
				.td(2).align("center").close().append(messageSource.getMessage("ready_for_evaluation", null, locale)).tdEnd()
				.td(2).align("center").close().append(messageSource.getMessage("evaluation_completed", null, locale)).tdEnd()
				.td(2).align("center").width("100px").close().append(messageSource.getMessage("total", null, locale)).tdEnd().trEnd(1)
				.tr(2).close().td(1).align("center").close().append(messageSource.getMessage("evaluated_crf", null, locale)).tdEnd()
				.td(2).align("center").close().a().href("javascript:$.jmesa.addFilterToLimit('" + getTableName() + "', '" + EVALUATION_STATUS + "', '"
				+ messageSource.getMessage("ready_for_evaluation", null, locale) + "'); $.jmesa.onInvokeAction('"
				+ getTableName() + "', 'filter');").close().append(evaluationReadyNumber).aEnd().tdEnd()
				.td(1).align("center").close().a().href("javascript:$.jmesa.addFilterToLimit('" + getTableName() + "', '" + EVALUATION_STATUS + "', '"
				+ messageSource.getMessage("evaluation_completed", null, locale) +  "'); $.jmesa.onInvokeAction('"
				+ getTableName() + "', 'filter');").close().append(evaluationCompleted).aEnd().tdEnd()
				.td(1).align("center").close().a().href("javascript:$.jmesa.addFilterToLimit('" + getTableName() + "', '" + EVALUATION_STATUS
				+ "', ''); $.jmesa.onInvokeAction('" + getTableName() + "', 'filter');").close().append(totalRows).aEnd().tdEnd().trEnd(2)
				.tableEnd(1).divEnd();
		return summaryTable;
	}

	private class EvaluationStatusCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowCount) {
			CRFEvaluationItem crfEvaluationItem = (CRFEvaluationItem) ((HashMap) item).get(OBJECT);
			return crfEvaluationItem.getDateValidateCompleted() != null ? messageSource.getMessage(
					"evaluation_completed", null, locale) : messageSource.getMessage("ready_for_evaluation", null,
					locale);
		}
	}

	private class EvaluationStatusFilter extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> optionList = new ArrayList<Option>();
			optionList.add(new Option(messageSource.getMessage("ready_for_evaluation", null, locale), messageSource
					.getMessage("ready_for_evaluation", null, locale)));
			optionList.add(new Option(messageSource.getMessage("evaluation_completed", null, locale), messageSource
					.getMessage("evaluation_completed", null, locale)));
			return optionList;
		}
	}
}
