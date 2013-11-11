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

package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteStatisticBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.AuditUserLoginDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.ListNotesFilter;
import org.akaza.openclinica.dao.managestudy.ListNotesSort;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.table.filter.CRFFilter;
import org.akaza.openclinica.web.table.filter.StudyEventTableRowFilter;
import org.jmesa.core.filter.DateFilterMatcher;
import org.jmesa.core.filter.FilterMatcher;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.core.filter.StringFilterMatcher;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.RowSelect;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.editor.DroplistFilterEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ListNotesTableFactory extends AbstractTableFactory {

	public static final String DISCREPANCY_NOTE_BEAN_DIS_TYPE = "discrepancyNoteBean.disType";
	public static final String DISCREPANCY_NOTE_BEAN_RESOLUTION_STATUS = "discrepancyNoteBean.resolutionStatus";
	public static final String QUERY_AND_FAILED_VALIDATION_CHECK_KEY = "Query_and_Failed_Validation_Check";
	public static final String NEW_AND_UPDATED_KEY = "New_and_Updated";
	public static final String NOT_CLOSED_KEY = "Not_Closed";
	public static final String QUERY_AND_FAILED_VALIDATION_CHECK_VALUE = "31";
	public static final String NEW_AND_UPDATED_VALUE = "21";
	public static final String NOT_CLOSED_VALUE = "321";
	private AuditUserLoginDao auditUserLoginDao;
	private StudySubjectDAO<?, ?> studySubjectDao;
	private UserAccountDAO userAccountDao;
	private DiscrepancyNoteDAO discrepancyNoteDao;
	private StudyDAO<?, ?> studyDao;
	private SubjectDAO subjectDao;
	private CRFVersionDAO<?, ?> crfVersionDao;
	private CRFDAO<?, ?> crfDao;
	private StudyEventDAO studyEventDao;
	private StudyEventDefinitionDAO<?, ?> studyEventDefinitionDao;
	private EventDefinitionCRFDAO eventDefinitionCRFDao;
	private ItemDataDAO itemDataDao;
	private ItemDAO<?, ?> itemDao;
	private EventCRFDAO<?, ?> eventCRFDao;
	private StudyBean currentStudy;
	private ResourceBundle resword;
	private ResourceBundle resformat;
	private ArrayList<DiscrepancyNoteBean> allNotes = new ArrayList<DiscrepancyNoteBean>();
	private String module;
	private Integer resolutionStatus;
	private Integer discNoteType;
	private Boolean studyHasDiscNotes = new Boolean(false);
	private final boolean showMoreLink;

	private DataSource dataSource;

	public ListNotesTableFactory(boolean showMoreLink) {
		this.showMoreLink = showMoreLink;
	}

	@Override
	protected String getTableName() {
		return "listNotes";
	}

	@Override
	protected void configureColumns(TableFacade tableFacade, Locale locale) {

		final String CRF_NAME = "crfName";
		final String EVENT_NAME = "eventName";

		tableFacade.setColumnProperties("studySubject.label", DISCREPANCY_NOTE_BEAN_DIS_TYPE,
				DISCREPANCY_NOTE_BEAN_RESOLUTION_STATUS, "siteId", "discrepancyNoteBean.createdDate",
				"discrepancyNoteBean.updatedDate", "age", "days", "eventName", "eventStartDate", "crfName",
				"crfStatus", "entityName", "entityValue", "discrepancyNoteBean.entityType",
				"discrepancyNoteBean.description", "discrepancyNoteBean.detailedNotes", "numberOfNotes",
				"discrepancyNoteBean.user", "discrepancyNoteBean.owner", "actions");
		Row row = tableFacade.getTable().getRow();
		StudyBean currentStudy = (StudyBean) tableFacade.getWebContext().getSessionAttribute("study");
		configureColumn(row.getColumn("studySubject.label"), currentStudy != null ? currentStudy
				.getStudyParameterConfig().getStudySubjectIdLabel() : resword.getString("study_subject_ID"), null,
				null, true, true);
		configureColumn(row.getColumn("siteId"), resword.getString("site_id"), null, null, true, false);
		configureColumn(row.getColumn("discrepancyNoteBean.createdDate"), resword.getString("date_created"),
				new DateCellEditor(getDateFormat()), null, false, true);
		configureColumn(row.getColumn("discrepancyNoteBean.updatedDate"), resword.getString("date_updated"),
				new DateCellEditor(getDateFormat()), null, false, false);
		configureColumn(row.getColumn("eventStartDate"), resword.getString("event_date"), new DateCellEditor(
				getDateFormat()), null, false, false);
		configureColumn(row.getColumn(EVENT_NAME), resword.getString("event_name"), null, null, true, false);
		configureColumn(row.getColumn(CRF_NAME), resword.getString("CRF"), null, null, true, false);
		configureColumn(row.getColumn("crfStatus"), resword.getString("CRF_status"), null, null, false, false);
		configureColumn(row.getColumn("entityName"), resword.getString("entity_name"), null, null, true, false);
		configureColumn(row.getColumn("entityValue"), resword.getString("entity_value"), null, null, true, false);
		configureColumn(row.getColumn("discrepancyNoteBean.description"), resword.getString("description"), null, null,
				true, false);
		configureColumn(row.getColumn("discrepancyNoteBean.detailedNotes"), resword.getString("detailed_notes"), null,
				null, false, false);
		configureColumn(row.getColumn("numberOfNotes"), resword.getString("of_notes"), null, null, false, false);
		configureColumn(row.getColumn("discrepancyNoteBean.user"), resword.getString("assigned_user"),
				new AssignedUserCellEditor(), null, true, false);
		configureColumn(row.getColumn(DISCREPANCY_NOTE_BEAN_RESOLUTION_STATUS), resword.getString("resolution_status"),
				new ResolutionStatusCellEditor(), new ResolutionStatusDroplistFilterEditor(), true, false);
		configureColumn(row.getColumn(DISCREPANCY_NOTE_BEAN_DIS_TYPE), resword.getString("type"),
				new DiscrepancyNoteTypeCellEditor(), new TypeDroplistFilterEditor(), true, false);
		configureColumn(row.getColumn("discrepancyNoteBean.entityType"), resword.getString("entity_type"), null, null,
				true, false);
		configureColumn(row.getColumn("discrepancyNoteBean.owner"), resword.getString("owner"), new OwnerCellEditor(),
				null, false, false);
		String actionsHeader = resword.getString("actions")
				+ "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
		configureColumn(row.getColumn("actions"), actionsHeader, new ActionsCellEditor(), new DefaultActionsEditor(
				locale), true, false);
		configureColumn(row.getColumn("age"), resword.getString("days_open"), null, null);
		configureColumn(row.getColumn("days"), resword.getString("days_since_updated"), null, null);

		// Configure the drop-down for the study event control
		CRFFilter crfFileter = new CRFFilter(dataSource, currentStudy);

		HtmlColumn crfNameColumn = ((HtmlRow) row).getColumn(CRF_NAME);
		crfNameColumn.getFilterRenderer().setFilterEditor(crfFileter);

		// Configure the drop-down for the study event control
		StudyEventTableRowFilter studyEventTableRowFilter = new StudyEventTableRowFilter(dataSource, currentStudy);

		HtmlColumn studyEventColumn = ((HtmlRow) row).getColumn(EVENT_NAME);
		studyEventColumn.getFilterRenderer().setFilterEditor(studyEventTableRowFilter);
	}

	@Override
	public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
		super.configureTableFacade(response, tableFacade);
		tableFacade.addFilterMatcher(new MatcherKey(Date.class, "discrepancyNoteBean.createdDate"),
				new DateFilterMatcher(getDateFormat()));
		tableFacade.addFilterMatcher(new MatcherKey(Date.class, "discrepancyNoteBean.updatedDate"),
				new DateFilterMatcher(getDateFormat()));
		tableFacade.addFilterMatcher(new MatcherKey(UserAccountBean.class, "discrepancyNoteBean.user"),
				new GenericFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(UserAccountBean.class, "studySubject.label"),
				new GenericFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "eventName"), new StringFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "crfName"), new StringFilterMatcher());
		// tableFacade.addFilterMatcher(new MatcherKey(String.class, "crfStatus"), new StringFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "entityName"), new StringFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "entityValue"), new StringFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "age"), new AgeDaysFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "days"), new AgeDaysFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, DISCREPANCY_NOTE_BEAN_DIS_TYPE),
				new DNTypeFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, DISCREPANCY_NOTE_BEAN_RESOLUTION_STATUS),
				new DNResolutionStatusFilterMatcher());
	}

	@Override
	public void configureTableFacadePostColumnConfiguration(TableFacade tableFacade) {
		ListNotesTableToolbar toolbar = new ListNotesTableToolbar(showMoreLink);
		tableFacade.setToolbar(toolbar);
		toolbar.setStudyHasDiscNotes(studyHasDiscNotes);
		toolbar.setDiscNoteType(discNoteType);
		toolbar.setResolutionStatus(resolutionStatus);
		toolbar.setModule(module);
		toolbar.setResword(resword);
		/*
		 * ClinCapture #71 set filters to toolbar
		 */
		ListNotesFilter listNotesFilter = getListNoteFilter(tableFacade.getLimit());
		toolbar.setListNotesFilter(listNotesFilter);
		tableFacade.setToolbar(toolbar);
	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {
		// initialize i18n
		resword = ResourceBundleProvider.getWordsBundle(getLocale());
		resformat = ResourceBundleProvider.getFormatBundle(getLocale());

		Limit limit = tableFacade.getLimit();
		ListNotesFilter listNotesFilter = getListNoteFilter(limit);
		ListNotesSort listNotesSort = getListSubjectSort(limit);

		DiscrepancyNoteDAO discrepancyNoteDAO = getDiscrepancyNoteDao();
		Integer dnCount = discrepancyNoteDAO.countViewNotesWithFilter(getCurrentStudy(), listNotesFilter);
		tableFacade.setTotalRows(dnCount == null ? 0 : dnCount);

		RowSelect rowSelect = limit.getRowSelect();
		int offset = rowSelect.getRowStart();
		int count = rowSelect.getRowEnd() - rowSelect.getRowStart();
		List<DiscrepancyNoteBean> customItems = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(
				getCurrentStudy(), listNotesFilter, listNotesSort, offset, count);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		UserAccountBean loggedInUser = (UserAccountBean) userAccountDao.findByUserName(authentication.getName());
		
		if (isCoder(loggedInUser)) {
			
			customItems = filterAccordingToMedicalCodingRole(customItems);
			tableFacade.setTotalRows(customItems.size());
		}
		
		this.setAllNotes(populateRowsWithAttachedData(customItems));

		if (!limit.isComplete()) {
			tableFacade.setTotalRows(allNotes.size());
		}

		Collection<HashMap<Object, Object>> theItems = new ArrayList<HashMap<Object, Object>>();

		for (DiscrepancyNoteBean discrepancyNoteBean : allNotes) {
			
			UserAccountBean owner = (UserAccountBean) getUserAccountDao().findByPK(discrepancyNoteBean.getOwnerId());

			HashMap<Object, Object> h = new HashMap<Object, Object>();

			h.put("studySubject", discrepancyNoteBean.getStudySub());
			h.put("studySubject.label", discrepancyNoteBean.getStudySub().getLabel());
			h.put(DISCREPANCY_NOTE_BEAN_DIS_TYPE, discrepancyNoteBean.getDisType());
			h.put(DISCREPANCY_NOTE_BEAN_RESOLUTION_STATUS, discrepancyNoteBean.getResStatus());
			h.put("age", discrepancyNoteBean.getResolutionStatusId() == 5 ? null : discrepancyNoteBean.getAge());
			h.put("days",
					discrepancyNoteBean.getResolutionStatusId() == 4
							|| discrepancyNoteBean.getResolutionStatusId() == 5 ? null : discrepancyNoteBean.getDays());
			h.put("siteId", discrepancyNoteBean.getStudySub().getStudyName());
			h.put("discrepancyNoteBean", discrepancyNoteBean);
			h.put("discrepancyNoteBean.createdDate", discrepancyNoteBean.getCreatedDate());
			h.put("discrepancyNoteBean.updatedDate", discrepancyNoteBean.getUpdatedDate());
			h.put("eventName", discrepancyNoteBean.getEventName());
			h.put("eventStartDate", discrepancyNoteBean.getEventStart());
			h.put("crfName", discrepancyNoteBean.getCrfName());
			h.put("crfStatus", discrepancyNoteBean.getCrfStatus());
			h.put("entityName", discrepancyNoteBean.getEntityName());
			h.put("entityValue", discrepancyNoteBean.getEntityValue());
			h.put("discrepancyNoteBean", discrepancyNoteBean);
			h.put("discrepancyNoteBean.description", discrepancyNoteBean.getDescription());
			h.put("discrepancyNoteBean.detailedNotes", discrepancyNoteBean.getDetailedNotes());
			h.put("numberOfNotes", discrepancyNoteBean.getNumChildren());
			h.put("discrepancyNoteBean.user", discrepancyNoteBean.getAssignedUser());
			h.put("discrepancyNoteBean.entityType", discrepancyNoteBean.getEntityType());
			h.put("discrepancyNoteBean.owner", owner);

			theItems.add(h);
			setStudyHasDiscNotes(true);
		}
		tableFacade.setItems(theItems);

	}

	private List<DiscrepancyNoteBean> filterAccordingToMedicalCodingRole(List<DiscrepancyNoteBean> customItems) {

		List<DiscrepancyNoteBean> filteredDiscrepancyNotes = new ArrayList<DiscrepancyNoteBean>();

		List<DiscrepancyNoteBean> allDiscrepancyNotes = (List<DiscrepancyNoteBean>) discrepancyNoteDao
				.getViewNotesWithFilterAndSort(getCurrentStudy(), new ListNotesFilter(), new ListNotesSort());

		for (DiscrepancyNoteBean discrepancyNote : allDiscrepancyNotes) {

			UserAccountBean owner = (UserAccountBean) userAccountDao.findByPK(discrepancyNote.getOwnerId());
			UserAccountBean assignedUser = (UserAccountBean) userAccountDao.findByPK(discrepancyNote
					.getAssignedUserId());

			if (isCoder(assignedUser) || isCoder(owner)) {

				filteredDiscrepancyNotes.add(discrepancyNote);
			}
		}

		return filteredDiscrepancyNotes;
	}

	private boolean isCoder(UserAccountBean loggedInUser) {
		return loggedInUser.getRoleByStudy(getCurrentStudy().getId()).getName().equalsIgnoreCase("study coder");
	}

	private ArrayList<DiscrepancyNoteBean> populateRowsWithAttachedData(List<DiscrepancyNoteBean> customItems) {
		
		DiscrepancyNoteDAO dndao = getDiscrepancyNoteDao();
		ArrayList<DiscrepancyNoteBean> allNotes = new ArrayList<DiscrepancyNoteBean>();

		for (int i = 0; i < customItems.size(); i++) {
			DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) customItems.get(i);
			dnb.setAssignedUser((UserAccountBean) getUserAccountDao().findByPK(dnb.getAssignedUserId()));
			if (dnb.getParentDnId() == 0) {
				ArrayList<?> children = dndao.findAllByStudyAndParent(currentStudy, dnb.getId());
				dnb.setNumChildren(children.size());

				for (int j = 0; j < children.size(); j++) {
					DiscrepancyNoteBean child = (DiscrepancyNoteBean) children.get(j);
					/*
					 * The update date is the date created of the latest child note
					 */
					if (dnb.getUpdatedDate() == null || dnb.getUpdatedDate().before(child.getCreatedDate())) {
						dnb.setUpdatedDate(child.getCreatedDate());
					}
				}
			}

			String entityType = dnb.getEntityType();

			if (dnb.getEntityId() > 0 && !entityType.equals("")) {
				AuditableEntityBean aeb = dndao.findEntity(dnb);
				dnb.setEntityName(aeb.getName());
				if (entityType.equalsIgnoreCase("subject")) {
					// allNotes.add(dnb);
					SubjectBean sb = (SubjectBean) aeb;
					StudySubjectBean ssb = studySubjectDao.findBySubjectIdAndStudy(sb.getId(), currentStudy);
					dnb.setStudySub(ssb);
					String column = dnb.getColumn().trim();
					if (!StringUtil.isBlank(column)) {
						if ("gender".equalsIgnoreCase(column)) {
							String genderToDisplay = resword.getString("not_specified");
							if ('m' == sb.getGender()) {
								genderToDisplay = resword.getString("male");
							} else if ('f' == sb.getGender()) {
								genderToDisplay = resword.getString("female");
							} 
							dnb.setEntityValue(genderToDisplay);
							dnb.setEntityName(resword.getString("gender"));
						} else if ("date_of_birth".equals(column)) {
							if (sb.getDateOfBirth() != null) {
								dnb.setEntityValue(sb.getDateOfBirth().toString());
							}
							dnb.setEntityName(resword.getString("date_of_birth"));
						} else if ("year_of_birth".equals(column)) {
							if (sb.getDateOfBirth() != null) {
								GregorianCalendar cal = new GregorianCalendar();
								cal.setTime(sb.getDateOfBirth());
								dnb.setEntityValue(String.valueOf(cal.get(Calendar.YEAR)));
							}
							dnb.setEntityName(resword.getString("year_of_birth"));
						} else if ("unique_identifier".equalsIgnoreCase(column)) {
							dnb.setEntityName(resword.getString("unique_identifier"));
							dnb.setEntityValue(sb.getUniqueIdentifier());
						}
					}
				} else if (entityType.equalsIgnoreCase("studySub")) {
					
					StudySubjectBean ssb = (StudySubjectBean) aeb;
					dnb.setStudySub(ssb);
					String column = dnb.getColumn().trim();
					if (!StringUtil.isBlank(column)) {
						if ("enrollment_date".equals(column)) {
							if (ssb.getEnrollmentDate() != null) {
								dnb.setEntityValue(ssb.getEnrollmentDate().toString());

							}
							dnb.setEntityName(resword.getString("enrollment_date"));

						}
					}
				} else if (entityType.equalsIgnoreCase("eventCRF")) {
					StudyEventDAO sed = getStudyEventDao();
					StudyEventBean se = (StudyEventBean) sed.findByPK(dnb.getEntityId());

					EventCRFBean ecb = (EventCRFBean) aeb;
					CRFVersionDAO<?, ?> cvdao = getCrfVersionDao();
					CRFDAO<?, ?> cdao = getCrfDao();
					CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(ecb.getCRFVersionId());
					CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());

					dnb.setStageId(ecb.getStage().getId());
					dnb.setEntityName(cb.getName() + " (" + cvb.getName() + ")");

					StudySubjectBean ssub = (StudySubjectBean) getStudySubjectDao().findByPK(ecb.getStudySubjectId());
					dnb.setStudySub(ssub);
					if (se != null) {
						dnb.setEventStart(se.getDateStarted());
						dnb.setEventName(se.getName());
					}
					dnb.setCrfName(cb.getName());
					String crfStatus = resword.getString(ecb.getStage().getNameRaw());
					if (crfStatus.equals("Invalid")) {
						crfStatus = "";
					} else if (crfStatus.equals("Data Entry Complete")) {
						crfStatus = "Complete";
					}
					dnb.setCrfStatus(crfStatus);

					String column = dnb.getColumn().trim();
					if (!StringUtil.isBlank(column)) {
						if ("date_interviewed".equals(column)) {
							if (ecb.getDateInterviewed() != null) {
								dnb.setEntityValue(ecb.getDateInterviewed().toString());

							}
							dnb.setEntityName(resword.getString("date_interviewed"));
						} else if ("interviewer_name".equals(column)) {
							dnb.setEntityValue(ecb.getInterviewerName());
							dnb.setEntityName(resword.getString("interviewer_name"));
						}
					}
				} else if (entityType.equalsIgnoreCase("studyEvent")) {
					
					StudyEventDAO sed = getStudyEventDao();
					StudyEventBean se = (StudyEventBean) sed.findByPK(dnb.getEntityId());
					StudyEventDefinitionDAO<?, ?> seddao = getStudyEventDefinitionDao();
					StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(se
							.getStudyEventDefinitionId());
					se.setName(sedb.getName());
					dnb.setEntityName(sedb.getName());
					StudySubjectBean ssub = (StudySubjectBean) getStudySubjectDao().findByPK(se.getStudySubjectId());
					dnb.setStudySub(ssub);
					dnb.setEventStart(se.getDateStarted());
					dnb.setEventName(se.getName());
					String column = dnb.getColumn().trim();
					if (!StringUtil.isBlank(column)) {
						if ("date_start".equals(column)) {
							if (se.getDateStarted() != null) {
								dnb.setEntityValue(se.getDateStarted().toString());
							}
							dnb.setEntityName(resword.getString("start_date"));
						} else if ("date_end".equals(column)) {
							if (se.getDateEnded() != null) {
								dnb.setEntityValue(se.getDateEnded().toString());
							}
							dnb.setEntityName(resword.getString("end_date"));
						} else if ("location".equals(column)) {
							dnb.setEntityValue(se.getLocation());
							dnb.setEntityName(resword.getString("location"));
						}
					}
				} else if (entityType.equalsIgnoreCase("itemData")) {
					ItemDataDAO iddao = getItemDataDao();
					ItemDAO<?, ?> idao = getItemDao();

					ItemDataBean idb = (ItemDataBean) iddao.findByPK(dnb.getEntityId());
					ItemBean ib = (ItemBean) idao.findByPK(idb.getItemId());

					EventCRFDAO<?, ?> ecdao = getEventCRFDao();
					EventCRFBean ec = (EventCRFBean) ecdao.findByPK(idb.getEventCRFId());

					CRFVersionDAO<?, ?> cvdao = getCrfVersionDao();
					CRFDAO<?, ?> cdao = getCrfDao();
					CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(ec.getCRFVersionId());
					CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());

					dnb.setStageId(ec.getStage().getId());
					dnb.setEntityName(ib.getName());
					dnb.setEntityValue(idb.getValue());
					dnb.setItemId(ib.getId());

					StudyEventDAO sed = getStudyEventDao();
					StudyEventBean se = (StudyEventBean) sed.findByPK(ec.getStudyEventId());

					StudyEventDefinitionDAO<?, ?> seddao = getStudyEventDefinitionDao();
					StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(se
							.getStudyEventDefinitionId());

					se.setName(sedb.getName());

					StudySubjectDAO<?, ?> ssdao = getStudySubjectDao();
					StudySubjectBean ssub = (StudySubjectBean) ssdao.findByPK(ec.getStudySubjectId());
					dnb.setStudySub(ssub);
					dnb.setEventStart(se.getDateStarted());
					dnb.setEventName(se.getName());
					dnb.setCrfName(cb.getName());
					String crfStatus = resword.getString(ec.getStage().getNameRaw());
					if (crfStatus.equals("Invalid")) {
						crfStatus = "";
					} else if (crfStatus.equals("Data Entry Complete")) {
						crfStatus = "Complete";
					}
					dnb.setCrfStatus(crfStatus);
					// }
				}
				// Because all places set DiscrepancyNoteBean subjectId as its studySub's Id.
				dnb.setSubjectId(dnb.getStudySub().getId());
			}
			dnb.setSiteId(((StudyBean) getStudyDao().findByPK(dnb.getStudySub().getStudyId())).getIdentifier());
			allNotes.add(dnb);
		}
		return allNotes;
	}

	/**
	 * A very custom way to filter the items. The AuditUserLoginFilter acts as a command for the Hibernate criteria
	 * object. Take the Limit information and filter the rows.
	 * 
	 * @param limit
	 *            The Limit to use.
	 */
	public ListNotesFilter getListNoteFilter(Limit limit) {
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		ResourceBundle reterm = ResourceBundleProvider.getTermsBundle();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			if (DISCREPANCY_NOTE_BEAN_DIS_TYPE.equalsIgnoreCase(property)) {
				if (reterm.getString(QUERY_AND_FAILED_VALIDATION_CHECK_KEY).equals(value)) {
					value = QUERY_AND_FAILED_VALIDATION_CHECK_VALUE;
				} else {
					value = Integer.toString(DiscrepancyNoteType.getByName(value).getId());
				}
			} else if (DISCREPANCY_NOTE_BEAN_RESOLUTION_STATUS.equalsIgnoreCase(property)) {
				if (reterm.getString(NEW_AND_UPDATED_KEY).equalsIgnoreCase(value)) {
					value = NEW_AND_UPDATED_VALUE;
				} else if (reterm.getString(NOT_CLOSED_KEY).equalsIgnoreCase(value)) {
					value = NOT_CLOSED_VALUE;
				} else {
					value = Integer.toString(ResolutionStatus.getByName(value).getId());
				}
			}
			listNotesFilter.addFilter(property, value);
		}

		return listNotesFilter;
	}

	/**
	 * A very custom way to sort the items. The AuditUserLoginSort acts as a command for the Hibernate criteria object.
	 * Take the Limit information and sort the rows.
	 * 
	 * @param limit
	 *            The Limit to use.
	 */
	protected ListNotesSort getListSubjectSort(Limit limit) {
		ListNotesSort listNotesSort = new ListNotesSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			listNotesSort.addSort(property, order);
		}

		return listNotesSort;
	}

	public AuditUserLoginDao getAuditUserLoginDao() {
		return auditUserLoginDao;
	}

	public void setAuditUserLoginDao(AuditUserLoginDao auditUserLoginDao) {
		this.auditUserLoginDao = auditUserLoginDao;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Map<String, String>> getNotesStatistics(List<DiscrepancyNoteStatisticBean> statisticBeans) {

		Map<Integer, String> types = new HashMap<Integer, String>();
		for (DiscrepancyNoteType noteType : ((List<DiscrepancyNoteType>) DiscrepancyNoteType.list)) {
			types.put(noteType.getId(), noteType.getName());
		}

		Map<Integer, String> statuses = new HashMap<Integer, String>();
		for (ResolutionStatus resolutionStatus : ResolutionStatus.getMembers()) {
			statuses.put(resolutionStatus.getId(), resolutionStatus.getName());
		}
		
		Map<String, Map<String, String>> summaryMap = new HashMap<String, Map<String, String>>();
		for (Integer statusKey : statuses.keySet()) {
			Map<String, String> tempMap = new HashMap<String, String>();
			int totalForType = 0;
			for (Integer typeKey : types.keySet()) {
				for (DiscrepancyNoteStatisticBean statisticBean : statisticBeans) {
					if (statisticBean.getResolutionStatusId() == statusKey
							&& statisticBean.getDiscrepancyNoteTypeId() == typeKey) {
						totalForType += statisticBean.getDiscrepancyNotesCount();
						tempMap.put(types.get(typeKey), Integer.toString(statisticBean.getDiscrepancyNotesCount()));
					}
				}
				tempMap.put("Total", Integer.toString(totalForType));
				summaryMap.put(statuses.get(statusKey), tempMap);
			}

		}

		return summaryMap;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getNotesTypesStatistics(List<DiscrepancyNoteStatisticBean> statisticBeans) {

		Map<Integer, Integer> statisticMap = new HashMap<Integer, Integer>();

		Integer total = 0;
		for (DiscrepancyNoteStatisticBean statisticBean : statisticBeans) {
			Integer typeId = statisticBean.getDiscrepancyNoteTypeId();
			total += statisticBean.getDiscrepancyNotesCount();
			if (statisticMap.containsKey(typeId)) {
				Integer currentCount = statisticMap.get(typeId);
				statisticMap.put(typeId, currentCount + statisticBean.getDiscrepancyNotesCount());
			} else {
				statisticMap.put(typeId, statisticBean.getDiscrepancyNotesCount());
			}
		}

		Map<String, String> summaryMap = new HashMap<String, String>();

		Map<Integer, String> types = new HashMap<Integer, String>();
		for (DiscrepancyNoteType noteType : ((List<DiscrepancyNoteType>) DiscrepancyNoteType.list)) {
			types.put(noteType.getId(), noteType.getName());
		}

		for (Integer key : statisticMap.keySet()) {
			summaryMap.put(types.get(key), statisticMap.get(key) == null ? "" : statisticMap.get(key).toString());
		}
		summaryMap.put("Total", total.toString());

		return summaryMap;

	}

	private class ResolutionStatusDroplistFilterEditor extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			ResourceBundle reterm = ResourceBundleProvider.getTermsBundle();
			for (Object status : ResolutionStatus.toArrayList()) {
				options.add(new Option(((ResolutionStatus) status).getName(), ((ResolutionStatus) status).getName()));
			}
			options.add(new Option(reterm.getString("New_and_Updated"), reterm.getString("New_and_Updated")));
			options.add(new Option(reterm.getString(NOT_CLOSED_KEY), reterm.getString(NOT_CLOSED_KEY)));
			return options;
		}
	}

	private class TypeDroplistFilterEditor extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			ResourceBundle reterm = ResourceBundleProvider.getTermsBundle();
			for (Object type : DiscrepancyNoteType.toArrayList()) {
				options.add(new Option(((DiscrepancyNoteType) type).getName(), ((DiscrepancyNoteType) type).getName()));
			}
			options.add(new Option(reterm.getString(QUERY_AND_FAILED_VALIDATION_CHECK_KEY), reterm
					.getString(QUERY_AND_FAILED_VALIDATION_CHECK_KEY)));
			return options;
		}
	}

	private class GenericFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			return true;
		}
	}

	private class DNTypeFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			int itemDNTypeId = ((DiscrepancyNoteType) itemValue).getId();
			ResourceBundle reterm = ResourceBundleProvider.getTermsBundle();
			if (reterm.getString(QUERY_AND_FAILED_VALIDATION_CHECK_KEY).equals(filterValue)) {
				return itemDNTypeId == 1 || itemDNTypeId == 3;
			} else {
				return itemDNTypeId == DiscrepancyNoteType.getByName(filterValue).getId();
			}
		}
	}

	private class DNResolutionStatusFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			int itemDNTypeId = ((ResolutionStatus) itemValue).getId();
			ResourceBundle reterm = ResourceBundleProvider.getTermsBundle();
			if (reterm.getString("New_and_Updated").equals(filterValue)) {
				return itemDNTypeId == 1 || itemDNTypeId == 2;
			} else if (reterm.getString(NOT_CLOSED_KEY).equals(filterValue)) {
				return itemDNTypeId == 1 || itemDNTypeId == 2 || itemDNTypeId == 3;
			} else {
				return itemDNTypeId == ResolutionStatus.getByName(filterValue).getId();
			}
		}
	}

	private class ResolutionStatusCellEditor implements CellEditor {
		@SuppressWarnings("unchecked")
		public Object getValue(Object item, String property, int rowcount) {
			String value = "";
			ResolutionStatus status = (ResolutionStatus) ((HashMap<Object, Object>) item)
					.get(DISCREPANCY_NOTE_BEAN_RESOLUTION_STATUS);

			if (status != null) {
				value = "<img src=\"" + status.getIconFilePath() + "\" border=\"0\" align=\"left\"> &nbsp;&nbsp;"
						+ status.getName();
			}
			return value;
		}
	}

	private class DiscrepancyNoteTypeCellEditor implements CellEditor {
		@SuppressWarnings("unchecked")
		public Object getValue(Object item, String property, int rowcount) {
			String value = "";
			DiscrepancyNoteType type = (DiscrepancyNoteType) ((HashMap<Object, Object>) item)
					.get(DISCREPANCY_NOTE_BEAN_DIS_TYPE);

			if (type != null) {
				value = type.getName();
			}
			return value;
		}
	}

	private class OwnerCellEditor implements CellEditor {
		@SuppressWarnings("unchecked")
		public Object getValue(Object item, String property, int rowcount) {
			String value = "";
			UserAccountBean user = (UserAccountBean) ((HashMap<Object, Object>) item).get("discrepancyNoteBean.owner");

			if (user != null) {
				value = user.getName();
			}
			return value;
		}
	}

	private class AssignedUserCellEditor implements CellEditor {
		@SuppressWarnings("unchecked")
		public Object getValue(Object item, String property, int rowcount) {
			String value = "";
			UserAccountBean user = (UserAccountBean) ((HashMap<Object, Object>) item).get("discrepancyNoteBean.user");

			if (user != null) {
				value = user.getFirstName() + " " + user.getLastName() + " (" + user.getName() + ")";
			}
			return value;
		}
	}

	private class ActionsCellEditor implements CellEditor {
		@SuppressWarnings("unchecked")
		public Object getValue(Object item, String property, int rowcount) {
			DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) ((HashMap<Object, Object>) item).get("discrepancyNoteBean");
			HtmlBuilder builder = new HtmlBuilder();
			// for "view" as action
			// This createNoteURL uses the same method as in ResolveDiscrepancyServlet
			String createNoteURL = CreateDiscrepancyNoteServlet.getAddChildURL(dnb, ResolutionStatus.CLOSED, true);
			builder.a().href("javascript:openDNWindow('" + createNoteURL + "&viewAction=1" + "');");
			builder.close();
			builder.img().name("bt_View1").src("images/bt_View_d.gif").border("0").alt(resword.getString("view"))
					.title(resword.getString("view")).align("left").append("hspace=\"6\"").close();
			builder.aEnd();
			if (!getCurrentStudy().getStatus().isLocked()) {
				if (dnb.getEntityType() != "eventCrf") {
					builder.a().href("ResolveDiscrepancy?noteId=" + dnb.getId());
					builder.close();
					builder.img().name("bt_Reassign1").src("images/bt_Reassign_d.gif").border("0")
							.alt(resword.getString("view_within_record")).title(resword.getString("view_within_record"))
							.align("left").append("hspace=\"6\"").close();
					builder.aEnd();
				} else {
					if (dnb.getStageId() == 5) {
						builder.a().href("ResolveDiscrepancy?noteId=" + dnb.getId());
						builder.close();
						builder.img().name("bt_Reassign1").src("images/bt_Reassign_d.gif").border("0")
								.alt(resword.getString("view_within_record")).title(resword.getString("view_within_record"))
								.align("left").append("hspace=\"6\"").close();
						builder.aEnd();
					}
				}
			}

			StudySubjectBean studySubjectBean = (StudySubjectBean) ((HashMap<Object, Object>) item).get("studySubject");
			Integer studySubjectId = studySubjectBean.getId();
			if (studySubjectId != null) {
				StringBuilder url = new StringBuilder();
				url.append(downloadNotesLinkBuilder(studySubjectBean));
				url.toString();
			}

			return builder.toString();
		}
	}

	private String downloadNotesLinkBuilder(StudySubjectBean studySubject) {
		HtmlBuilder actionLink = new HtmlBuilder();
		if (this.isStudyHasDiscNotes()) {
			if (this.getResolutionStatus() >= 1 && this.getResolutionStatus() <= 5) {
				actionLink.a().href(
						"javascript:openDocWindow('ChooseDownloadFormat?subjectId=" + studySubject.getId()
								+ "&discNoteType=" + discNoteType + "&resolutionStatus=" + resolutionStatus + "')");
				actionLink.img().name("bt_View1").src("images/bt_Download.gif").border("0")
						.alt(resword.getString("download_discrepancy_notes"))
						.title(resword.getString("download_discrepancy_notes"))
						.append("hspace=\"4\" width=\"24 \" height=\"15\"").end().aEnd();
				actionLink.append("&nbsp;&nbsp;&nbsp;");
			} else {
				actionLink.a().href(
						"javascript:openDocWindow('ChooseDownloadFormat?subjectId=" + studySubject.getId()
								+ "&discNoteType=" + discNoteType + "&module=" + module + "')");
				actionLink.img().name("bt_View1").src("images/bt_Download.gif").border("0")
						.alt(resword.getString("download_discrepancy_notes"))
						.title(resword.getString("download_discrepancy_notes"))
						.append("hspace=\"2\" width=\"24 \" height=\"15\"").end().aEnd();
				actionLink.append("&nbsp;&nbsp;&nbsp;");
			}
		}
		return actionLink.toString();
	}

	// Ignore the mathing values with filter
	public class AgeDaysFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			return true;
		}
	}

	private String getDateFormat() {
		return resformat.getString("date_format_string");
	}

	public StudySubjectDAO<?, ?> getStudySubjectDao() {
		return studySubjectDao;
	}

	public void setStudySubjectDao(StudySubjectDAO<?, ?> studySubjectDao) {
		this.studySubjectDao = studySubjectDao;
	}

	public SubjectDAO getSubjectDao() {
		return subjectDao;
	}

	public void setSubjectDao(SubjectDAO subjectDao) {
		this.subjectDao = subjectDao;
	}

	public StudyDAO<?, ?> getStudyDao() {
		return studyDao;
	}

	public void setStudyDao(StudyDAO<?, ?> studyDao) {
		this.studyDao = studyDao;
	}

	public StudyBean getCurrentStudy() {
		return currentStudy;
	}

	public void setCurrentStudy(StudyBean currentStudy) {
		this.currentStudy = currentStudy;
	}

	public UserAccountDAO getUserAccountDao() {
		return userAccountDao;
	}

	public void setUserAccountDao(UserAccountDAO userAccountDao) {
		this.userAccountDao = userAccountDao;
	}

	public DiscrepancyNoteDAO getDiscrepancyNoteDao() {
		return discrepancyNoteDao;
	}

	public void setDiscrepancyNoteDao(DiscrepancyNoteDAO discrepancyNoteDao) {
		this.discrepancyNoteDao = discrepancyNoteDao;
	}

	public CRFVersionDAO<?, ?> getCrfVersionDao() {
		return crfVersionDao;
	}

	public void setCrfVersionDao(CRFVersionDAO<?, ?> crfVersionDao) {
		this.crfVersionDao = crfVersionDao;
	}

	public CRFDAO<?, ?> getCrfDao() {
		return crfDao;
	}

	public void setCrfDao(CRFDAO<?, ?> crfDao) {
		this.crfDao = crfDao;
	}

	public StudyEventDAO getStudyEventDao() {
		return studyEventDao;
	}

	public void setStudyEventDao(StudyEventDAO studyEventDao) {
		this.studyEventDao = studyEventDao;
	}

	public EventDefinitionCRFDAO getEventDefinitionCRFDao() {
		return eventDefinitionCRFDao;
	}

	public void setEventDefinitionCRFDao(EventDefinitionCRFDAO eventDefinitionCRFDao) {
		this.eventDefinitionCRFDao = eventDefinitionCRFDao;
	}

	public ItemDataDAO getItemDataDao() {
		return itemDataDao;
	}

	public void setItemDataDao(ItemDataDAO itemDataDao) {
		this.itemDataDao = itemDataDao;
	}

	public ItemDAO<?, ?> getItemDao() {
		return itemDao;
	}

	public void setItemDao(ItemDAO<?, ?> itemDao) {
		this.itemDao = itemDao;
	}

	public EventCRFDAO<?, ?> getEventCRFDao() {
		return eventCRFDao;
	}

	public void setEventCRFDao(EventCRFDAO<?, ?> eventCRFDao) {
		this.eventCRFDao = eventCRFDao;
	}

	public StudyEventDefinitionDAO<?, ?> getStudyEventDefinitionDao() {
		return studyEventDefinitionDao;
	}

	public void setStudyEventDefinitionDao(StudyEventDefinitionDAO<?, ?> studyEventDefinitionDao) {
		this.studyEventDefinitionDao = studyEventDefinitionDao;
	}

	public ArrayList<DiscrepancyNoteBean> getAllNotes() {
		return allNotes;
	}

	public void setAllNotes(ArrayList<DiscrepancyNoteBean> allNotes) {
		this.allNotes = allNotes;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Integer getDiscNoteType() {
		return discNoteType;
	}

	public void setDiscNoteType(Integer discNoteType) {
		this.discNoteType = discNoteType;
	}

	public Boolean isStudyHasDiscNotes() {
		return studyHasDiscNotes;
	}

	public void setStudyHasDiscNotes(Boolean studyHasDiscNotes) {
		this.studyHasDiscNotes = studyHasDiscNotes;
	}

	public Integer getResolutionStatus() {
		return resolutionStatus;
	}

	public void setResolutionStatus(Integer resolutionStatus) {
		this.resolutionStatus = resolutionStatus;
	}

	public ArrayList<DiscrepancyNoteBean> populateDataInNote(List<DiscrepancyNoteBean> notes) {
		return populateRowsWithAttachedData((ArrayList<DiscrepancyNoteBean>) notes);
	}

	public List<DiscrepancyNoteBean> getNotesForPrintPop(Limit limit) {
		ListNotesFilter listNotesFilter = getListNoteFilter(limit);
		ListNotesSort listNotesSort = getListSubjectSort(limit);
		return getDiscrepancyNoteDao().getViewNotesWithFilterAndSort(getCurrentStudy(), listNotesFilter, listNotesSort);
	}

	public void setDataSource(DataSource dataSource) {

		this.dataSource = dataSource;
	}

	public List<DiscrepancyNoteStatisticBean> getFilteredNotesStatistics() {

		List<DiscrepancyNoteStatisticBean> filteredDiscrepancyNotes = new ArrayList<DiscrepancyNoteStatisticBean>();
		List<DiscrepancyNoteBean> allDiscrepancyNotes = (List<DiscrepancyNoteBean>) discrepancyNoteDao.getViewNotesWithFilterAndSort(getCurrentStudy(), new ListNotesFilter(), new ListNotesSort());

		List<DiscrepancyNoteBean> coderNotes = filterAccordingToMedicalCodingRole(allDiscrepancyNotes);
		for (DiscrepancyNoteBean discrepancyNote : coderNotes) {

			filteredDiscrepancyNotes.add(createDiscrepancyStatistic(discrepancyNote, coderNotes.size()));
		}
		
		return filteredDiscrepancyNotes;
	}

	private DiscrepancyNoteStatisticBean createDiscrepancyStatistic(DiscrepancyNoteBean discrepancyNote, int count) {
		
		DiscrepancyNoteStatisticBean statisticBean = new DiscrepancyNoteStatisticBean();

		statisticBean.setDiscrepancyNotesCount(count);
		statisticBean.setDiscrepancyNoteTypeId(discrepancyNote.getId());
		statisticBean.setResolutionStatusId(discrepancyNote.getResolutionStatusId());

		return statisticBean;
	}
}
