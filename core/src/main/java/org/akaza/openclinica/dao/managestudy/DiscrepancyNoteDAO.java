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

package org.akaza.openclinica.dao.managestudy;

import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteStatisticBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;

import com.clinovo.model.DiscrepancyCorrectionForm;
import com.clinovo.util.DateUtil;

/**
 * <code>DiscrepancyNoteDAO</code> class is a member of DAO layer, extends <code>AuditableEntityDAO</code> class.
 * <p/>
 * This class implements all the required data access logic for bean class <code>DiscrepancyNoteBean</code>.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DiscrepancyNoteDAO extends AuditableEntityDAO {

	public static final String UNION_OP = " UNION ";

	public static final int SQL_QUERY_VARIABLES_COUNT_1 = 4;
	public static final int SQL_QUERY_VARIABLES_COUNT_2 = 10;
	public static final int SQL_QUERY_VARIABLES_COUNT_3 = 20;

	public static final int START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED = 12;

	public static final int DEFAULT_FETCH_UPPER_BOUND = 100;

	// if true, we fetch the mapping along with the bean
	// only applies to functions which return a single bean
	private boolean fetchMapping = false;

	/**
	 * @return Returns the fetchMapping.
	 */
	public boolean isFetchMapping() {
		return fetchMapping;
	}

	/**
	 * @param fetchMapping
	 *            The fetchMapping to set.
	 */
	public void setFetchMapping(boolean fetchMapping) {
		this.fetchMapping = fetchMapping;
	}

	private void setQueryNames() {
		findByPKAndStudyName = "findByPKAndStudy";
		getCurrentPKName = "getCurrentPrimaryKey";
	}

	/**
	 * Constructor with DataSource.
	 * 
	 * @param ds
	 *            DataSource
	 */
	public DiscrepancyNoteDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	/**
	 * Constructor with DAODigester and DataSource.
	 * 
	 * @param ds
	 *            DataSource
	 * @param digester
	 *            DAODigester
	 */
	public DiscrepancyNoteDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
		setQueryNames();
	}

	/**
	 * Constructor with DataSource and Connection.
	 * 
	 * @param ds
	 *            DataSource
	 * @param connection
	 *            Connection
	 */
	public DiscrepancyNoteDAO(DataSource ds, Connection connection) {
		super(ds, connection);
		setQueryNames();
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_DISCREPANCY_NOTE;
	}

	@Override
	public void setTypesExpected() {

		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);

		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.INT);
	}

	private void setMapTypesExpected() {
		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.STRING);
	}

	private void setStatisticTypesExpected() {
		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.INT);
	}

	/**
	 * Sets expected types for DCF query.
	 */
	public void setDcfTypesExpected() {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index, TypeNames.STRING);
	}

	/**
	 * Method that gets the object from the database query.
	 *
	 * @param hm
	 *            HashMap
	 * @return Object
	 */
	@SuppressWarnings("deprecation")
	public DiscrepancyNoteBean getEntityFromHashMap(HashMap hm) {
		DiscrepancyNoteBean eb = new DiscrepancyNoteBean();
		Date dateCreated = (Date) hm.get("date_created");
		Integer ownerId = (Integer) hm.get("owner_id");
		eb.setCreatedDate(dateCreated);
		eb.setOwnerId(ownerId);
		eb.setId(selectInt(hm, "discrepancy_note_id"));
		eb.setDescription((String) hm.get("description"));
		eb.setDiscrepancyNoteTypeId((Integer) hm.get("discrepancy_note_type_id"));
		eb.setResolutionStatusId((Integer) hm.get("resolution_status_id"));
		eb.setParentDnId((Integer) hm.get("parent_dn_id"));
		eb.setDetailedNotes((String) hm.get("detailed_notes"));
		eb.setEntityType((String) hm.get("entity_type"));
		eb.setDisType(DiscrepancyNoteType.get(eb.getDiscrepancyNoteTypeId()));
		eb.setResStatus(ResolutionStatus.get(eb.getResolutionStatusId()));
		eb.setStudyId(selectInt(hm, "study_id"));
		eb.setAssignedUserId(selectInt(hm, "assigned_user_id"));
		if (hm.get("item_data_id") != null) {
			eb.setEntityId((Integer) hm.get("item_data_id"));
		}
		if (hm.get("item_id") != null) {
			eb.setItemId((Integer) hm.get("item_id"));
		}
		if (hm.get("event_crf_id") != null) {
			eb.setEventCRFId((Integer) hm.get("event_crf_id"));
		}
		if (eb.getAssignedUserId() > 0) {
			UserAccountDAO userAccountDAO = new UserAccountDAO(getDataSource());
			UserAccountBean assignedUser = (UserAccountBean) userAccountDAO.findByPK(eb.getAssignedUserId());
			eb.setAssignedUser(assignedUser);
		}
		eb.setAge(selectInt(hm, "age"));
		eb.setDays(selectInt(hm, "days"));
		if (hm.get("item_data_ordinal") != null) {
			eb.setItemDataOrdinal((Integer) hm.get("item_data_ordinal"));
		}
		return eb;
	}

	/**
	 * Method that gets the DNs statistic bean from the database query.
	 *
	 * @param hm
	 *            Map
	 * @return DiscrepancyNoteStatisticBean
	 */
	public DiscrepancyNoteStatisticBean getStatisticEntityFromHashMap(Map hm) {
		DiscrepancyNoteStatisticBean statisticBean = new DiscrepancyNoteStatisticBean();

		statisticBean.setDiscrepancyNotesCount((Integer) hm.get("count"));
		statisticBean.setDiscrepancyNoteTypeId((Integer) hm.get("discrepancy_note_type_id"));
		statisticBean.setResolutionStatusId((Integer) hm.get("resolution_status_id"));

		return statisticBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection findAll() {
		return this.executeFindAllQuery("findAll");
	}

	/**
	 * Returns all the parent DNs from specific study and its sites.
	 *
	 * @param study
	 *            StudyBean
	 * @return ArrayList
	 */
	public ArrayList findAllParentsByStudy(StudyBean study) {
		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		ArrayList notes = executeFindAllQuery("findAllParentsByStudy", variables);
		if (fetchMapping) {
			for (int i = 0; i < notes.size(); i++) {
				DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) notes.get(i);
				dnb = findSingleMapping(dnb);
				notes.set(i, dnb);
			}
		}
		return notes;
	}

	/**
	 * Returns a list of child DNs by parent DN and study/site.
	 *
	 * @param study
	 *            StudyBean
	 * @param parentId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllByStudyAndParent(StudyBean study, int parentId) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, parentId);
		variables.put(index++, study.getId());
		variables.put(index++, study.getId());
		variables.put(index, study.getId());
		return this.executeFindAllQuery("findAllByStudyAndParent", variables);
	}

	/**
	 * Returns all the DNs by event CRF id.
	 *
	 * @param eventCRFId
	 *            int
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findAllItemNotesByEventCRF(int eventCRFId) {
		this.setTypesExpected();
		ArrayList alist;
		HashMap variables = new HashMap();
		variables.put(1, eventCRFId);
		alist = this.select(digester.getQuery("findAllItemNotesByEventCRF"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object hm : alist) {
			al.add(getEntityFromHashMap((HashMap) hm));
		}
		return al;
	}

	/**
	 * Returns all the parent DNs by event CRF id.
	 *
	 * @param eventCRFId
	 *            int
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findAllParentItemNotesByEventCRF(int eventCRFId) {
		this.setTypesExpected();
		ArrayList alist;
		HashMap variables = new HashMap();
		variables.put(1, eventCRFId);
		alist = this.select(digester.getQuery("findAllParentItemNotesByEventCRF"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object hm : alist) {
			al.add(getEntityFromHashMap((HashMap) hm));
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs by study/site with specific filters applied.
	 *
	 * @param filter
	 *            ListNotesFilter
	 * @param currentStudy
	 *            currentStudy
	 * @return Integer
	 */
	public Integer getCountWithFilter(ListNotesFilter filter, StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index, currentStudy.getId());
		String sql = digester.getQuery("getCountWithFilter");
		sql += filter.execute("");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Returns a list of parent DNs by study/site with specific filters and sorting applied.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            ListNotesFilter
	 * @param sort
	 *            ListNotesSort
	 * @param rowStart
	 *            int
	 * @param rowEnd
	 *            int
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> getWithFilterAndSort(StudyBean currentStudy, ListNotesFilter filter,
			ListNotesSort sort, int rowStart, int rowEnd) {
		ArrayList<DiscrepancyNoteBean> discNotes = new ArrayList<DiscrepancyNoteBean>();
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index, currentStudy.getId());
		String sql = digester.getQuery("getWithFilterAndSort");
		sql = sql + filter.execute("");

		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			sql += " AND rownum <= " + rowEnd + " and rownum >" + rowStart;
			sql = sql + sort.execute("");
		} else {
			sql = sql + sort.execute("");
			sql = sql + " LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
		}

		ArrayList rows = select(sql, variables);

		for (Object row : rows) {
			DiscrepancyNoteBean discBean = getEntityFromHashMap((HashMap) row);
			discBean = findSingleMapping(discBean);
			discNotes.add(discBean);
		}
		return discNotes;

	}

	/**
	 * Returns a list of parent DNs by study/site with specific filters applied.
	 *
	 * @param filter
	 *            String
	 * @param currentStudy
	 *            StudyBean
	 * @param userId
	 *            int
	 * @return Integer
	 */
	public Integer getViewNotesCountWithFilter(String filter, StudyBean currentStudy, int userId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		ListNotesFilter filterObj = new ListNotesFilter();

		HashMap variables = new HashMap();
		for (int i = 1; i <= SQL_QUERY_VARIABLES_COUNT_2; i++) {
			variables.put(i, currentStudy.getId());
		}

		StringBuilder sql = new StringBuilder("select count(all_dn.discrepancy_note_id) as COUNT from (");

		sql.append(digester.getQuery("findAllSubjectDNByStudy"));
		sql.append(filter).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudySubjectDNByStudy"));
		sql.append(filter).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudyEventDNByStudy"));
		sql.append(filter).append(UNION_OP);
		sql.append(digester.getQuery("findAllEventCrfDNByStudy"));
		sql.append(filterObj.getFilterForMaskedCRFs(userId));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filter).append(UNION_OP);
		sql.append(digester.getQuery("findAllItemDataDNByStudy"));
		sql.append(filterObj.getFilterForMaskedCRFs(userId));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filter);
		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			sql.append(" ) all_dn");
		} else {
			sql.append(" ) as all_dn");
		}

		ArrayList rows = select(sql.toString(), variables);
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Returns a set of DNs from filtered and sorted list of all the parent DNs by study/site.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            ListNotesFilter
	 * @param sort
	 *            ListNotesSort
	 * @param offset
	 *            int
	 * @param limit
	 *            int
	 * @param userAccount
	 *            UserAccountBean
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> getViewNotesWithFilterAndSortLimits(StudyBean currentStudy,
			ListNotesFilter filter, ListNotesSort sort, int offset, int limit, UserAccountBean userAccount) {
		return getViewNotesWithFilterAndSortLimits(currentStudy, filter, sort, offset, limit, false, userAccount);
	}

	/**
	 * Returns a set of DNs from filtered and sorted list of all the parent DNs by study/site.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            ListNotesFilter
	 * @param sort
	 *            ListNotesSort
	 * @param offset
	 *            int
	 * @param limit
	 *            int
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> getViewNotesWithFilterAndSortLimits(StudyBean currentStudy,
			ListNotesFilter filter, ListNotesSort sort, int offset, int limit) {
		return getViewNotesWithFilterAndSortLimits(currentStudy, filter, sort, offset, limit, null);
	}

	/**
	 * Returns a slq string to retrieve a set of DNs from filtered and sorted list of all the parent DNs by study/site.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            ListNotesFilter
	 * @param sort
	 *            ListNotesSort
	 * @param offset
	 *            int
	 * @param limit
	 *            int
	 * @param eventCrfOnly
	 *            boolean if <code>true</code>, then sql will search for DNs assigned to event CRFs and its data only;
	 *            otherwise - sql will search for all the DNs in a study/site
	 * @param activeUser
	 *            UserAccountBean
	 * @return String
	 */
	public String getSQLViewNotesWithFilterAndSortLimits(StudyBean currentStudy, ListNotesFilter filter,
			ListNotesSort sort, int offset, int limit, boolean eventCrfOnly, UserAccountBean activeUser) {
		StringBuilder sql = new StringBuilder("SELECT dns.* FROM ( ");
		int activeUserId = 0;
		if (activeUser != null) {
			activeUserId = activeUser.getId();
		}
		String filterPart = filter.execute("");
		String sortPart = sort.execute("");

		if (!eventCrfOnly) {
			sql.append(digester.getQuery("findAllSubjectDNByStudy"));
			sql.append(filterPart).append(UNION_OP);
			sql.append(digester.getQuery("findAllStudySubjectDNByStudy"));
			sql.append(filterPart).append(UNION_OP);
			sql.append(digester.getQuery("findAllStudyEventDNByStudy"));
			sql.append(filterPart).append(filter.getAdditionalStudyEventFilter()).append(UNION_OP);
		}
		sql.append(digester.getQuery("findAllEventCrfDNByStudy"));
		sql.append(filter.getFilterForMaskedCRFs(activeUserId));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart).append(filter.getAdditionalStudyEventFilter()).append(UNION_OP);
		sql.append(digester.getQuery("findAllItemDataDNByStudy"));
		sql.append(filter.getFilterForMaskedCRFs(activeUserId));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart).append(filter.getAdditionalStudyEventFilter());
		sql.append(") dns left outer join user_account ua on ua.user_id = dns.assigned_user_id ")
				.append(filter.addUserFilter());
		sql.append(filter.getAdditionalFilter());
		if (!sortPart.isEmpty()) {
			sql.append(sortPart);
		} else {
			sql.append(" order by label asc, age asc ");
		}

		if (offset >= 0 && limit > 0) {
			sql.append(" offset ").append(offset).append(" limit ").append(limit);
		}
		return sql.toString();
	}

	/**
	 * Returns a set of DNs from filtered and sorted list of all the parent DNs by study/site.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            ListNotesFilter
	 * @param sort
	 *            ListNotesSort
	 * @param offset
	 *            int
	 * @param limit
	 *            int
	 * @param eventCrfOnly
	 *            boolean if <code>true</code>, then sql will search for DNs assigned to event CRFs and its data only;
	 *            otherwise - sql will search for all the DNs in a study/site
	 * @param userAccount
	 *            UserAccountBean
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> getViewNotesWithFilterAndSortLimits(StudyBean currentStudy,
			ListNotesFilter filter, ListNotesSort sort, int offset, int limit, boolean eventCrfOnly,
			UserAccountBean userAccount) {
		setTypesExpected();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index, TypeNames.INT);

		Map variables = new HashMap();
		int sqlQueryVariablesCount = eventCrfOnly ? SQL_QUERY_VARIABLES_COUNT_1 : SQL_QUERY_VARIABLES_COUNT_2;
		for (int i = 1; i <= sqlQueryVariablesCount; i++) {
			variables.put(i, currentStudy.getId());
		}

		ArrayList rows = select(getSQLViewNotesWithFilterAndSortLimits(currentStudy, filter, sort, offset, limit,
				eventCrfOnly, userAccount), variables);
		Iterator it = rows.iterator();
		ArrayList<DiscrepancyNoteBean> discNotes = new ArrayList<DiscrepancyNoteBean>();
		while (it.hasNext()) {
			DiscrepancyNoteBean discBean = getEntityFromHashMap((HashMap) it.next());
			discBean = findSingleMapping(discBean);
			discNotes.add(discBean);
		}
		return discNotes;
	}

	/**
	 * Returns a count of parent DNs by study/site with specific filters and sorting applied.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            ListNotesFilter
	 * @return Integer
	 */
	public Integer countViewNotesWithFilter(StudyBean currentStudy, ListNotesFilter filter) {
		return countViewNotesWithFilter(currentStudy, filter, null);
	}

	/**
	 * Returns a count of parent DNs by study/site with specific filters and sorting applied.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            ListNotesFilter
	 * @param ub
	 *            UserAccountBean
	 * @return Integer
	 */
	public Integer countViewNotesWithFilter(StudyBean currentStudy, ListNotesFilter filter, UserAccountBean ub) {
		int activeUserId = ub == null ? 0 : ub.getId();
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);

		Map variables = new HashMap();
		for (int i = 1; i <= SQL_QUERY_VARIABLES_COUNT_2; i++) {
			variables.put(i, currentStudy.getId());
		}

		StringBuilder sql = new StringBuilder("select count(*) count from (");

		String filterPart = filter.execute("");

		sql.append(digester.getQuery("findAllSubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudySubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudyEventDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllEventCrfDNByStudy"));
		sql.append(filter.getFilterForMaskedCRFs(activeUserId));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllItemDataDNByStudy"));
		sql.append(filter.getFilterForMaskedCRFs(activeUserId));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart);
		sql.append(") dns left outer join user_account ua on ua.user_id = dns.assigned_user_id ")
				.append(filter.addUserFilter()).append(filter.getAdditionalFilter());
		ArrayList rows = select(sql.toString(), variables);
		return rows.size() != 0 ? (Integer) ((Map) rows.get(0)).get("count") : Integer.valueOf(0);
	}

	/**
	 * Returns a count of parent DNs by CRFs in study/site.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param ub
	 *            UserAccountBean
	 * @return Map
	 */
	public Map<String, Map<ResolutionStatus, Integer>> countDNsByCRFs(StudyBean currentStudy, UserAccountBean ub) {

		Map<String, Map<ResolutionStatus, Integer>> crfNameToRSToDNCountMap = new HashMap<String, Map<ResolutionStatus, Integer>>();
		int activeUserId = ub == null ? 0 : ub.getId();
		int index = 1;
		unsetTypeExpected();
		setTypeExpected(index++, TypeNames.INT);
		setTypeExpected(index++, TypeNames.INT);
		setTypeExpected(index, TypeNames.STRING);

		Map variables = new HashMap();
		for (int i = 1; i <= SQL_QUERY_VARIABLES_COUNT_1; i++) {
			variables.put(i, currentStudy.getId());
		}

		ListNotesFilter filter = new ListNotesFilter();
		StringBuilder sql = new StringBuilder(
				"select count(discrepancy_note_id), resolution_status_id, crf_name from (");
		sql.append(digester.getQuery("findAllEventCrfDNByStudyForNdsPerCrfWidget"));
		sql.append(filter.getFilterForMaskedCRFs(activeUserId));
		if (currentStudy.isSite()) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(UNION_OP);
		sql.append(digester.getQuery("findAllItemDataDNByStudyForNdsPerCrfWidget"));
		sql.append(filter.getFilterForMaskedCRFs(activeUserId));
		if (currentStudy.isSite()) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(") dns ");
		sql.append(" group by crf_name, resolution_status_id");
		sql.append(" order by crf_name, resolution_status_id");

		List<Map> rows = select(sql.toString(), variables);
		for (Map map : rows) {
			Map<ResolutionStatus, Integer> rsToDNCountMap = crfNameToRSToDNCountMap.get(map.get("crf_name"));
			if (rsToDNCountMap == null) {
				rsToDNCountMap = new HashMap<ResolutionStatus, Integer>();
				crfNameToRSToDNCountMap.put((String) map.get("crf_name"), rsToDNCountMap);
			}
			rsToDNCountMap.put(ResolutionStatus.get((Integer) map.get("resolution_status_id")),
					(Integer) map.get("count"));
		}
		return crfNameToRSToDNCountMap;
	}

	/**
	 * Gets all DNs for view with filter and sort applied.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            ListNotesFilter
	 * @param sort
	 *            ListNotesSort
	 * @param ub
	 *            UserAccountBean
	 * @return List of DiscrepancyNoteBeans
	 */
	public ArrayList<DiscrepancyNoteBean> getViewNotesWithFilterAndSortForPrint(StudyBean currentStudy,
			ListNotesFilter filter, ListNotesSort sort, UserAccountBean ub) {
		ArrayList<DiscrepancyNoteBean> discNotes = new ArrayList<DiscrepancyNoteBean>();
		Map variables = new HashMap();
		for (int i = 1; i <= SQL_QUERY_VARIABLES_COUNT_2; i++) {
			variables.put(i, currentStudy.getId());
		}
		StringBuilder sql = buildViewNotesSQL(currentStudy, filter, sort, ub);
		String sortPart = sort.execute("");
		sql.append(sortPart);
		ArrayList rows = select(sql.toString(), variables);
		for (Object row : rows) {
			DiscrepancyNoteBean discBean = getEntityFromHashMap((HashMap) row);
			discBean = findSingleMapping(discBean);
			discNotes.add(discBean);
		}
		return discNotes;
	}

	/**
	 * Gets DNs owned by or assigned to user for view with filter and sort applied.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param currentUser
	 *            UserAccountBean
	 * @param filter
	 *            ListNotesFilter
	 * @param sort
	 *            ListNotesSort
	 * @param filterMasks
	 *            boolean defines - should DNs for masked CRFs be shown
	 * @return List of DiscrepancyNoteBeans
	 */
	public ArrayList<DiscrepancyNoteBean> getViewNotesWithFilterAndSort(StudyBean currentStudy,
			UserAccountBean currentUser, ListNotesFilter filter, ListNotesSort sort, boolean filterMasks) {
		ArrayList<DiscrepancyNoteBean> discNotes = new ArrayList<DiscrepancyNoteBean>();
		Map variables = new HashMap();
		for (int i = 1; i <= SQL_QUERY_VARIABLES_COUNT_2; i++) {
			variables.put(i, currentStudy.getId());
		}
		StringBuilder sql = filterMasks
				? buildViewNotesSQL(currentStudy, filter, sort, currentUser)
				: buildViewNotesSQL(currentStudy, filter, sort);
		sql.append(filter.getOwnerOrAssignedFilter(currentUser));
		String sortPart = sort.execute("");
		sql.append(sortPart);
		ArrayList rows = select(sql.toString(), variables);
		for (Object row : rows) {
			DiscrepancyNoteBean discBean = getEntityFromHashMap((HashMap) row);
			discBean = findSingleMapping(discBean);
			discNotes.add(discBean);
		}
		return discNotes;
	}

	/**
	 * Gets DNs owned by or assigned to user for view with filter and sort applied.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param currentUser
	 *            UserAccountBean
	 * @param filter
	 *            ListNotesFilter
	 * @param sort
	 *            ListNotesSort
	 * @return List of DiscrepancyNoteBeans
	 */
	public ArrayList<DiscrepancyNoteBean> getViewNotesWithFilterAndSort(StudyBean currentStudy,
			UserAccountBean currentUser, ListNotesFilter filter, ListNotesSort sort) {
		return getViewNotesWithFilterAndSort(currentStudy, currentUser, filter, sort, false);
	}

	private StringBuilder buildViewNotesSQL(StudyBean currentStudy, ListNotesFilter filter, ListNotesSort sort,
			UserAccountBean ub) {
		setTypesExpected();
		int activeUserAccountId = ub == null ? 0 : ub.getId();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index, TypeNames.STRING);
		StringBuilder sql = new StringBuilder("SELECT dns.* FROM ( ");
		String filterPart = filter.execute("");
		sql.append(digester.getQuery("findAllSubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudySubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudyEventDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllEventCrfDNByStudy"));
		sql.append(filter.getFilterForMaskedCRFs(activeUserAccountId));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllItemDataDNByStudy"));
		sql.append(filter.getFilterForMaskedCRFs(activeUserAccountId));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart);
		sql.append(") dns left outer join user_account ua on ua.user_id = dns.assigned_user_id ")
				.append(filter.addUserFilter());
		sql.append(filter.getAdditionalFilter());
		return sql;
	}

	private StringBuilder buildViewNotesSQL(StudyBean currentStudy, ListNotesFilter filter, ListNotesSort sort) {
		return buildViewNotesSQL(currentStudy, filter, sort, null);
	}

	/**
	 * Returns a list of all the parent DNs by study/site.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findAllDiscrepancyNotesDataByStudy(StudyBean currentStudy) {
		ArrayList<DiscrepancyNoteBean> discNotes = new ArrayList<DiscrepancyNoteBean>();
		setTypesExpected();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.INT);

		HashMap variables = new HashMap();
		for (int i = 1; i <= SQL_QUERY_VARIABLES_COUNT_2; i++) {
			variables.put(i, currentStudy.getId());
		}

		String sql = digester.getQuery("findAllSubjectDNByStudy");
		sql += UNION_OP;
		sql += digester.getQuery("findAllStudySubjectDNByStudy");
		sql += UNION_OP;
		sql += digester.getQuery("findAllStudyEventDNByStudy");
		sql += UNION_OP;
		sql += digester.getQuery("findAllEventCrfDNByStudy");
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql += " and ec.event_crf_id not in ( " + this.findSiteHiddenEventCrfIdsString(currentStudy) + " ) ";
		}
		sql += UNION_OP;
		sql += digester.getQuery("findAllItemDataDNByStudy");
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql += " and ec.event_crf_id not in ( " + this.findSiteHiddenEventCrfIdsString(currentStudy) + " ) ";
		}

		ArrayList rows = select(sql, variables);

		for (Object row : rows) {
			DiscrepancyNoteBean discBean = getEntityFromHashMap((HashMap) row);
			discBean = findSingleMapping(discBean);
			discNotes.add(discBean);
		}
		return discNotes;
	}

	/**
	 * Returns DNs statistics by DNs types and resolution statuses from study/site.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param ub
	 *            UserAccountBean
	 * @return List<DiscrepancyNoteStatisticBean>
	 */
	public List<DiscrepancyNoteStatisticBean> countNotesStatisticWithMasks(StudyBean currentStudy, UserAccountBean ub) {
		setStatisticTypesExpected();
		ListNotesFilter filter = new ListNotesFilter();
		int activeUserId = ub == null ? 0 : ub.getId();
		Map variables = new HashMap();
		for (int i = 1; i <= SQL_QUERY_VARIABLES_COUNT_2; i++) {
			variables.put(i, currentStudy.getId());
		}
		StringBuilder sql = new StringBuilder(
				"SELECT count(discrepancy_note_id), discrepancy_note_type_id, resolution_status_id FROM (");
		sql.append(digester.getQuery("countAllSubjectDNByStudyForStat"));
		sql.append(UNION_OP);
		sql.append(digester.getQuery("countAllStudySubjectDNByStudyForStat"));
		sql.append(UNION_OP);
		sql.append(digester.getQuery("countAllStudyEventDNByStudyForStat"));
		sql.append(UNION_OP);
		sql.append(digester.getQuery("countAllEventCrfDNByStudyForStat"));
		sql.append(filter.getFilterForMaskedCRFs(activeUserId));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(UNION_OP);
		sql.append(digester.getQuery("countAllItemDataDNByStudyForStat"));
		sql.append(filter.getFilterForMaskedCRFs(activeUserId));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(") types GROUP BY discrepancy_note_type_id, resolution_status_id");
		ArrayList rows = select(sql.toString(), variables);
		Iterator it = rows.iterator();
		List<DiscrepancyNoteStatisticBean> notesStat = new ArrayList<DiscrepancyNoteStatisticBean>();
		while (it.hasNext()) {
			notesStat.add(getStatisticEntityFromHashMap((Map) it.next()));
		}
		return notesStat;
	}

	/**
	 * Returns DNs statistics by DNs types and resolution statuses from study/site, for DNs assigned to/owned by
	 * specific user account.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param currentUser
	 *            UserAccountBean
	 * @return List<DiscrepancyNoteStatisticBean>
	 */
	public List<DiscrepancyNoteStatisticBean> countUserNotesStatistics(StudyBean currentStudy,
			UserAccountBean currentUser) {
		setStatisticTypesExpected();
		Map variables = new HashMap();
		for (int i = 1; i <= SQL_QUERY_VARIABLES_COUNT_3; i++) {
			variables.put(i++, currentStudy.getId());
			variables.put(i++, currentStudy.getId());
			variables.put(i++, currentUser.getId());
			variables.put(i, currentUser.getId());
		}
		StringBuilder sql = new StringBuilder(
				"SELECT count(discrepancy_note_id), discrepancy_note_type_id, resolution_status_id FROM (");
		sql.append(digester.getQuery("countAllSubjectDNByStudyForStat"));
		sql.append(digester.getQuery("countUsersDNForStatFilter"));
		sql.append(UNION_OP);
		sql.append(digester.getQuery("countAllStudySubjectDNByStudyForStat"));
		sql.append(digester.getQuery("countUsersDNForStatFilter"));
		sql.append(UNION_OP);
		sql.append(digester.getQuery("countAllStudyEventDNByStudyForStat"));
		sql.append(digester.getQuery("countUsersDNForStatFilter"));
		sql.append(UNION_OP);
		sql.append(digester.getQuery("countAllEventCrfDNByStudyForStat"));
		sql.append(digester.getQuery("countUsersDNForStatFilter"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(UNION_OP);
		sql.append(digester.getQuery("countAllItemDataDNByStudyForStat"));
		sql.append(digester.getQuery("countUsersDNForStatFilter"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(") types GROUP BY discrepancy_note_type_id, resolution_status_id");
		ArrayList rows = select(sql.toString(), variables);
		Iterator it = rows.iterator();
		List<DiscrepancyNoteStatisticBean> notesStat = new ArrayList<DiscrepancyNoteStatisticBean>();
		while (it.hasNext()) {
			notesStat.add(getStatisticEntityFromHashMap((Map) it.next()));
		}
		return notesStat;
	}

	/**
	 * Returns a list of all the parent DNs by study/site, with specific filters applied.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            ListNotesFilter
	 * @param ub
	 *            UserAccountBean
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> getNotesWithFilterAndSortForOutput(StudyBean currentStudy,
			ListNotesFilter filter, UserAccountBean ub) {
		setTypesExpected();
		int activeUserAccountId = ub == null ? 0 : ub.getId();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index, TypeNames.STRING);

		Map variables = new HashMap();
		for (int i = 1; i <= SQL_QUERY_VARIABLES_COUNT_2; i++) {
			variables.put(i, currentStudy.getId());
		}

		StringBuilder sql = new StringBuilder("SELECT dns.* FROM ( ");

		String filterPart = filter.execute("");

		sql.append(digester.getQuery("findAllSubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudySubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudyEventDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllEventCrfDNByStudy"));
		sql.append(filter.getFilterForMaskedCRFs(activeUserAccountId));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllItemDataDNByStudy"));
		sql.append(filter.getFilterForMaskedCRFs(activeUserAccountId));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart);
		sql.append(") dns left outer join user_account ua on ua.user_id = dns.assigned_user_id ")
				.append(filter.addUserFilter());
		sql.append(filter.getAdditionalFilter());
		sql.append(" order by dns.label");

		ArrayList rows = select(sql.toString(), variables);
		Iterator it = rows.iterator();
		ArrayList<DiscrepancyNoteBean> discNotes = new ArrayList<DiscrepancyNoteBean>();
		while (it.hasNext()) {
			DiscrepancyNoteBean discBean = getEntityFromHashMap((HashMap) it.next());
			discBean = findSingleMapping(discBean);
			discNotes.add(discBean);
		}
		return discNotes;
	}

	/**
	 * Returns a list of DNs assigned to a specific property of a specific entity (like Date of Birth property of entity
	 * Subject).
	 *
	 * @param study
	 *            StudyBean
	 * @param entityName
	 *            String
	 * @param entityId
	 *            int
	 * @param column
	 *            String
	 * @return Collection
	 */
	public Collection findAllByEntityAndColumnAndStudy(StudyBean study, String entityName, int entityId,
			String column) {
		this.setTypesExpected();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index, TypeNames.STRING); // ss.label
		ArrayList alist = new ArrayList();
		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, entityId);
		variables.put(index++, column);
		if ("subject".equalsIgnoreCase(entityName)) {
			int parentStudyId = study.getParentStudyId() == 0 ? study.getId() : study.getParentStudyId();
			variables.put(index++, parentStudyId);
			variables.put(index++, parentStudyId);
			variables.put(index++, parentStudyId);
			variables.put(index, parentStudyId);
			alist = this.select(digester.getQuery("findAllBySubjectAndColumnAndStudy"), variables);
		} else if ("studySub".equalsIgnoreCase(entityName)) {
			alist = this.select(digester.getQuery("findAllByStudySubjectAndColumn"), variables);
		} else if ("eventCrf".equalsIgnoreCase(entityName)) {
			index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED + 1;
			this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
			this.setTypeExpected(index++, TypeNames.STRING); // sed_name
			this.setTypeExpected(index, TypeNames.STRING); // crf_name
			alist = this.select(digester.getQuery("findAllByEventCRFAndColumn"), variables);
		} else if ("studyEvent".equalsIgnoreCase(entityName)) {
			index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED + 1;
			this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
			this.setTypeExpected(index, TypeNames.STRING); // sed_name
			alist = this.select(digester.getQuery("findAllByStudyEventAndColumn"), variables);
		} else if ("itemData".equalsIgnoreCase(entityName)) {
			index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED + 1;
			this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
			this.setTypeExpected(index++, TypeNames.STRING); // sed_name
			this.setTypeExpected(index++, TypeNames.STRING); // crf_name
			this.setTypeExpected(index, TypeNames.STRING); // item_name
			alist = this.select(digester.getQuery("findAllByItemDataAndColumn"), variables);
		}

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			if ("eventCrf".equalsIgnoreCase(entityName) || "itemData".equalsIgnoreCase(entityName)) {
				eb.setEventName((String) hm.get("sed_name"));
				eb.setEventStart((Date) hm.get("date_start"));
				eb.setCrfName((String) hm.get("crf_name"));
				eb.setEntityName((String) hm.get("item_name"));

			} else if ("studyEvent".equalsIgnoreCase(entityName)) {
				eb.setEventName((String) hm.get("sed_name"));
				eb.setEventStart((Date) hm.get("date_start"));
			}
			if (fetchMapping) {
				eb = findSingleMapping(eb);
			}
			al.add(eb);
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, assigned to Subjects only, from specific study/site.
	 *
	 * @param study
	 *            StudyBean
	 * @return ArrayList
	 */
	public ArrayList findAllSubjectByStudy(StudyBean study) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.STRING); // column_name
		this.setTypeExpected(index, TypeNames.INT); // subject_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, study.getId());
		variables.put(index++, study.getId());
		variables.put(index, study.getId());

		alist = this.select(digester.getQuery("findAllSubjectByStudy"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("subject_id"));
			al.add(eb);
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, assigned to specific Subject in study/site.
	 *
	 * @param study
	 *            StudyBean
	 * @param subjectId
	 *            int
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findAllSubjectByStudyAndId(StudyBean study, int subjectId) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.STRING); // column_name
		this.setTypeExpected(index, TypeNames.INT); // subject_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, study.getId());
		variables.put(index++, study.getId());
		variables.put(index++, study.getId());
		variables.put(index, subjectId);

		alist = this.select(digester.getQuery("findAllSubjectByStudyAndId"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("subject_id"));
			al.add(eb);
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, assigned to Study Subjects only, from specific study/site.
	 *
	 * @param study
	 *            StudyBean
	 * @return ArrayList
	 */
	public ArrayList findAllStudySubjectByStudy(StudyBean study) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.STRING); // column_name
		this.setTypeExpected(index, TypeNames.INT); // study_subject_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, study.getId());
		variables.put(index, study.getId());

		alist = this.select(digester.getQuery("findAllStudySubjectByStudy"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("study_subject_id"));
			al.add(eb);
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, assigned to specific Study Subject in study/site.
	 *
	 * @param study
	 *            StudyBean
	 * @param studySubjectId
	 *            int
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findAllStudySubjectByStudyAndId(StudyBean study, int studySubjectId) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.STRING); // column_name
		this.setTypeExpected(index, TypeNames.INT); // study_subject_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, study.getId());
		variables.put(index++, study.getId());
		variables.put(index, studySubjectId);

		alist = this.select(digester.getQuery("findAllStudySubjectByStudyAndId"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("study_subject_id"));
			al.add(eb);
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, assigned to specific Study Subject in current study and in study, where Study
	 * Subject was enrolled (actually, searches for DNs assigned to Study Subject property Date of Enrollment).
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param subjectStudy
	 *            StudyBean
	 * @param studySubjectId
	 *            int
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findAllStudySubjectByStudiesAndStudySubjectId(StudyBean currentStudy,
			StudyBean subjectStudy, int studySubjectId) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.STRING); // column_name
		this.setTypeExpected(index, TypeNames.INT); // study_subject_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index++, subjectStudy.getId());
		variables.put(index++, subjectStudy.getId());
		variables.put(index, studySubjectId);

		alist = this.select(digester.getQuery("findAllStudySubjectByStudiesAndStudySubjectId"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("study_subject_id"));
			al.add(eb);
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, assigned to specific Study Subject in current study/site and in study/site, where
	 * Study Subject was enrolled (actually, searches for DNs assigned to Subject properties: Date of Birth, Sex, Person
	 * ID).
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param subjectStudy
	 *            StudyBean
	 * @param studySubjectId
	 *            int
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findAllSubjectByStudiesAndSubjectId(StudyBean currentStudy,
			StudyBean subjectStudy, int studySubjectId) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.STRING); // column_name
		this.setTypeExpected(index, TypeNames.INT); // subject_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index++, subjectStudy.getId());
		variables.put(index++, subjectStudy.getId());
		variables.put(index++, currentStudy.getId());
		variables.put(index++, subjectStudy.getId());
		variables.put(index, studySubjectId);

		alist = this.select(digester.getQuery("findAllSubjectByStudiesAndSubjectId"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("subject_id"));
			al.add(eb);
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, assigned to Study Events only, in specific study/site.
	 *
	 * @param study
	 *            StudyBean
	 * @return ArrayList
	 */
	public ArrayList findAllStudyEventByStudy(StudyBean study) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(index++, TypeNames.STRING); // sed_name
		this.setTypeExpected(index++, TypeNames.STRING); // column_name
		this.setTypeExpected(index, TypeNames.INT); // study_event_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, study.getId());
		variables.put(index, study.getId());
		alist = this.select(digester.getQuery("findAllStudyEventByStudy"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("study_event_id"));

			al.add(eb);
		}
		return al;
	}

	/**
	 * Finds all DNs, associated with Study Events of a certain Study Subject, in specific study/site.
	 *
	 * @param study
	 *            A StudyBean, whose id property is checked.
	 * @param studySubjectId
	 *            The id of a Study Subject.
	 * @return An ArrayList of DiscrepancyNoteBeans.
	 */
	public ArrayList findAllStudyEventByStudyAndId(StudyBean study, int studySubjectId) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(index++, TypeNames.STRING); // sed_name
		this.setTypeExpected(index++, TypeNames.STRING); // column_name
		this.setTypeExpected(index, TypeNames.INT); // study_event_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, study.getId());
		variables.put(index++, study.getId());
		variables.put(index, studySubjectId);
		alist = this.select(digester.getQuery("findAllStudyEventByStudyAndId"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("study_event_id"));

			al.add(eb);
		}
		return al;
	}

	/**
	 * Finds all DNs, associated with Study Events of a certain Study Subject, in current study/site and in study/site,
	 * where Study Subject was enrolled.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param subjectStudy
	 *            StudyBean
	 * @param studySubjectId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllStudyEventByStudiesAndSubjectId(StudyBean currentStudy, StudyBean subjectStudy,
			int studySubjectId) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(index++, TypeNames.STRING); // sed_name
		this.setTypeExpected(index++, TypeNames.STRING); // column_name
		this.setTypeExpected(index, TypeNames.INT); // study_event_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index++, subjectStudy.getId());
		variables.put(index++, currentStudy.getId());
		variables.put(index, studySubjectId);
		alist = this.select(digester.getQuery("findAllStudyEventByStudiesAndSubjectId"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("study_event_id"));

			al.add(eb);
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, assigned to Event CRFs only, in specific study/site.
	 *
	 * @param study
	 *            StudyBean
	 * @return ArrayList
	 */
	public ArrayList findAllEventCRFByStudy(StudyBean study) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(index++, TypeNames.STRING); // sed_name
		this.setTypeExpected(index++, TypeNames.STRING); // crf_name
		this.setTypeExpected(index++, TypeNames.STRING); // column_name
		this.setTypeExpected(index, TypeNames.INT); // event_crf_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, study.getId());
		variables.put(index, study.getId());
		alist = this.select(digester.getQuery("findAllEventCRFByStudy"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setCrfName((String) hm.get("crf_name"));
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("event_crf_id"));
			al.add(eb);
		}
		return al;
	}

	/**
	 * Returns a list of child DNs of a certain parent DN, associated with a certain Event CRF, in specific study/site.
	 *
	 * @param study
	 *            StudyBean
	 * @param parent
	 *            DiscrepancyNoteBean
	 * @return ArrayList
	 */
	public ArrayList findAllEventCRFByStudyAndParent(StudyBean study, DiscrepancyNoteBean parent) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(index++, TypeNames.STRING); // sed_name
		this.setTypeExpected(index++, TypeNames.STRING); // crf_name
		this.setTypeExpected(index++, TypeNames.STRING); // column_name
		this.setTypeExpected(index, TypeNames.INT); // event_crf_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, study.getId());
		variables.put(index++, study.getId());
		variables.put(index, parent.getId());

		alist = this.select(digester.getQuery("findAllEventCRFByStudyAndParent"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setCrfName((String) hm.get("crf_name"));
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("event_crf_id"));
			al.add(eb);
		}
		return al;
	}

	/**
	 * Returns a list of DNs, associated with Items of a specific Event CRF.
	 *
	 * @param eventCRFBean
	 *            EventCRFBean
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findItemDataDNotesFromEventCRF(EventCRFBean eventCRFBean) {

		this.setTypesExpected();
		ArrayList dNotelist;

		HashMap variables = new HashMap();
		variables.put(1, eventCRFBean.getId());
		dNotelist = this.select(digester.getQuery("findItemDataDNotesFromEventCRF"), variables);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();
		for (Object hm : dNotelist) {
			DiscrepancyNoteBean eb = getEntityFromHashMap((HashMap) hm);
			eb.setEventCRFId(eventCRFBean.getId());
			returnedNotelist.add(eb);
		}
		return returnedNotelist;
	}

	/**
	 * Returns a list of parent DNs only, associated with Items of a specific Event CRF.
	 *
	 * @param eventCRFBean
	 *            EventCRFBean
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findParentItemDataDNotesFromEventCRF(EventCRFBean eventCRFBean) {

		this.setTypesExpected();
		ArrayList dNotelist;

		HashMap variables = new HashMap();
		variables.put(1, eventCRFBean.getId());
		dNotelist = this.select(digester.getQuery("findParentItemDataDNotesFromEventCRF"), variables);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();
		for (Object hm : dNotelist) {
			DiscrepancyNoteBean eb = getEntityFromHashMap((HashMap) hm);
			eb.setEventCRFId(eventCRFBean.getId());
			returnedNotelist.add(eb);
		}
		return returnedNotelist;
	}

	/**
	 * Returns a list of DNs, associated with Interviewer Name and Interview Date properties of a specific Event CRF.
	 *
	 * @param eventCRFBean
	 *            EventCRFBean
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findEventCRFDNotesFromEventCRF(EventCRFBean eventCRFBean) {

		this.setTypesExpected();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index, TypeNames.STRING);
		ArrayList dNotelist;

		HashMap variables = new HashMap();
		variables.put(1, eventCRFBean.getId());
		dNotelist = this.select(digester.getQuery("findEventCRFDNotesFromEventCRF"), variables);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();
		for (Object aDNotelist : dNotelist) {
			HashMap hm = (HashMap) aDNotelist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setColumn((String) hm.get("column_name"));
			eb.setEventCRFId(eventCRFBean.getId());
			returnedNotelist.add(eb);
		}
		return returnedNotelist;
	}

	/**
	 * findEventCRFDNotesToolTips.
	 *
	 * @param eventCRFBean
	 *            EventCRFBean
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findEventCRFDNotesToolTips(EventCRFBean eventCRFBean) {

		this.setTypesExpected();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index, TypeNames.STRING);
		ArrayList dNotelist;

		HashMap variables = new HashMap();
		for (int i = 1; i <= SQL_QUERY_VARIABLES_COUNT_2; i++) {
			variables.put(i, eventCRFBean.getId());
		}

		dNotelist = this.select(digester.getQuery("findEventCRFDNotesForToolTips"), variables);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();
		for (Object aDNotelist : dNotelist) {
			HashMap hm = (HashMap) aDNotelist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setColumn((String) hm.get("column_name"));
			eb.setEventCRFId(eventCRFBean.getId());
			returnedNotelist.add(eb);
		}
		return returnedNotelist;
	}

	/**
	 * Returns a list of DNs, associated with a specific Item in a specific EventCRF.
	 *
	 * @param eventCRFBean
	 *            eventCRFBean
	 * @param itemName
	 *            String
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findAllDNotesByItemNameAndEventCRF(EventCRFBean eventCRFBean,
			String itemName) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, eventCRFBean.getId());
		variables.put(index, itemName);
		ArrayList dNotelist;

		dNotelist = this.select(digester.getQuery("findAllDNotesByItemNameAndEventCRF"), variables);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();
		for (Object hm : dNotelist) {
			returnedNotelist.add(getEntityFromHashMap((HashMap) hm));
		}
		return returnedNotelist;
	}

	/**
	 * Returns a list of parent DNs, associated with Item Data only, in specific study/site.
	 *
	 * @param study
	 *            StudyBean
	 * @return ArrayList
	 */
	public ArrayList findAllItemDataByStudy(StudyBean study) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(index++, TypeNames.STRING); // sed_name
		this.setTypeExpected(index++, TypeNames.STRING); // crf_name
		this.setTypeExpected(index++, TypeNames.STRING); // item_name
		this.setTypeExpected(index++, TypeNames.STRING); // value
		this.setTypeExpected(index++, TypeNames.INT); // item_data_id
		this.setTypeExpected(index, TypeNames.INT); // item_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, study.getId());
		variables.put(index, study.getId());
		alist = this.select(digester.getQuery("findAllItemDataByStudy"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setCrfName((String) hm.get("crf_name"));
			eb.setSubjectName((String) hm.get("label"));
			eb.setEntityName((String) hm.get("item_name"));
			eb.setEntityValue((String) hm.get("value"));
			eb.setEntityId((Integer) hm.get("item_data_id"));
			eb.setItemId((Integer) hm.get("item_id"));
			al.add(eb);
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, associated with Item Data only, in specific study/site. Excludes DNs, associated
	 * with CRFs from <code>hiddenCrfNames</code> set.
	 *
	 * @param study
	 *            StudyBean
	 * @param hiddenCrfNames
	 *            Set<String>
	 * @return ArrayList
	 */
	public ArrayList findAllItemDataByStudy(StudyBean study, Set<String> hiddenCrfNames) {
		this.setTypesExpected();
		ArrayList al = new ArrayList();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(index++, TypeNames.INT); // sed_id
		this.setTypeExpected(index++, TypeNames.STRING); // sed_name
		this.setTypeExpected(index++, TypeNames.STRING); // crf_name
		this.setTypeExpected(index++, TypeNames.STRING); // item_name
		this.setTypeExpected(index++, TypeNames.STRING); // value
		this.setTypeExpected(index++, TypeNames.INT); // item_data_id
		this.setTypeExpected(index, TypeNames.INT); // item_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, study.getId());
		variables.put(index, study.getId());
		ArrayList alist = this.select(digester.getQuery("findAllItemDataByStudy"), variables);
		Iterator it = alist.iterator();

		if (hiddenCrfNames.size() > 0) {
			while (it.hasNext()) {
				HashMap hm = (HashMap) it.next();
				Integer sedId = (Integer) hm.get("sed_id");
				String crfName = (String) hm.get("crf_name");
				if (!hiddenCrfNames.contains(sedId + "_" + crfName)) {
					DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
					eb.setEventName((String) hm.get("sed_name"));
					eb.setEventStart((Date) hm.get("date_start"));
					eb.setCrfName(crfName);
					eb.setSubjectName((String) hm.get("label"));
					eb.setEntityName((String) hm.get("item_name"));
					eb.setEntityValue((String) hm.get("value"));
					eb.setEntityId((Integer) hm.get("item_data_id"));
					eb.setItemId((Integer) hm.get("item_id"));
					al.add(eb);
				}
			}
		} else {
			while (it.hasNext()) {
				HashMap hm = (HashMap) it.next();
				DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
				eb.setEventName((String) hm.get("sed_name"));
				eb.setEventStart((Date) hm.get("date_start"));
				eb.setCrfName((String) hm.get("crf_name"));
				eb.setSubjectName((String) hm.get("label"));
				eb.setEntityName((String) hm.get("item_name"));
				eb.setEntityValue((String) hm.get("value"));
				eb.setEntityId((Integer) hm.get("item_data_id"));
				eb.setItemId((Integer) hm.get("item_id"));
				al.add(eb);
			}
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, associated with Item Data only, in specific study/site, and assigned to a specific
	 * user account.
	 *
	 * @param study
	 *            StudyBean
	 * @param user
	 *            UserAccountBean
	 * @return Integer
	 */
	public Integer countAllItemDataByStudyAndUser(StudyBean study, UserAccountBean user) {
		this.setTypesExpected();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(index++, TypeNames.STRING); // sed_name
		this.setTypeExpected(index++, TypeNames.STRING); // crf_name
		this.setTypeExpected(index++, TypeNames.STRING); // item_name
		this.setTypeExpected(index++, TypeNames.STRING); // value
		this.setTypeExpected(index++, TypeNames.INT); // item_data_id
		this.setTypeExpected(index, TypeNames.INT); // item_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, study.getId());
		variables.put(index++, study.getId());
		variables.put(index, user.getId());

		ArrayList rows = this.select(digester.getQuery("countAllItemDataByStudyAndUser"), variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Returns a list of child DNs by its Parent DN record and study/site.
	 *
	 * @param study
	 *            StudyBean
	 * @param parent
	 *            DiscrepancyNoteBean
	 * @return ArrayList
	 */
	public ArrayList findAllItemDataByStudyAndParent(StudyBean study, DiscrepancyNoteBean parent) {
		this.setTypesExpected();
		ArrayList alist;
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.STRING); // ss.label
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(index++, TypeNames.STRING); // sed_name
		this.setTypeExpected(index++, TypeNames.STRING); // crf_name
		this.setTypeExpected(index++, TypeNames.STRING); // item_name
		this.setTypeExpected(index++, TypeNames.STRING); // value
		this.setTypeExpected(index++, TypeNames.INT); // item_data_id
		this.setTypeExpected(index, TypeNames.INT); // item_id

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, study.getId());
		variables.put(index++, study.getId());
		variables.put(index, parent.getId());
		alist = this.select(digester.getQuery("findAllItemDataByStudyAndParent"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setCrfName((String) hm.get("crf_name"));
			eb.setSubjectName((String) hm.get("label"));
			eb.setEntityName((String) hm.get("item_name"));
			eb.setEntityValue((String) hm.get("value"));
			// YW << change EntityId from item_id to item_data_id.
			eb.setEntityId((Integer) hm.get("item_data_id"));
			eb.setItemId((Integer) hm.get("item_id"));
			// YW >>
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find all with aditional params. Not Implemented!
	 * 
	 * @param strOrderByColumn
	 *            String
	 * @param blnAscendingSort
	 *            boolean
	 * @param strSearchPhrase
	 *            String
	 * @return new ArrayList.
	 */
	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * Find by id.
	 * 
	 * @param id
	 *            int
	 * @return EntityBean
	 */
	public EntityBean findByPK(int id) {
		DiscrepancyNoteBean eb = new DiscrepancyNoteBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = getEntityFromHashMap((HashMap) it.next());
		}
		if (fetchMapping) {
			eb = findSingleMapping(eb);
		}
		return eb;
	}

	/**
	 * Create Discrepancy Note.
	 * 
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean create(EntityBean eb) {
		return create(eb, null);
	}

	/**
	 * Creates a new DN record.
	 *
	 * @param eb
	 *            EntityBean
	 * @param connection
	 *            Connection
	 * @return EntityBean
	 */
	public EntityBean create(EntityBean eb, Connection connection) {

		DiscrepancyNoteBean sb = (DiscrepancyNoteBean) eb;
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		int index = 1;
		variables.put(index++, sb.getDescription());
		variables.put(index++, sb.getDiscrepancyNoteTypeId());
		variables.put(index++, sb.getResolutionStatusId());
		variables.put(index++, sb.getDetailedNotes());

		variables.put(index++, sb.getOwner().getId());
		if (sb.getParentDnId() == 0) {
			nullVars.put(index, Types.INTEGER);
			variables.put(index++, null);
		} else {
			variables.put(index++, sb.getParentDnId());
		}
		variables.put(index++, sb.getEntityType());
		variables.put(index++, sb.getStudyId());
		if (sb.getAssignedUserId() == 0) {
			nullVars.put(index, Types.INTEGER);
			variables.put(index, null);
		} else {
			variables.put(index, sb.getAssignedUserId());
		}

		this.executeWithPK(digester.getQuery("create"), variables, nullVars, connection);
		if (isQuerySuccessful()) {
			sb.setId(getLatestPK());
		}

		return sb;
	}

	/**
	 * Creates a new discrepancy note map.
	 *
	 * @param eb
	 *            DiscrepancyNoteBean
	 */
	public void createMapping(DiscrepancyNoteBean eb) {
		createMapping(eb, null);
	}

	/**
	 * Creates a new discrepancy note map.
	 *
	 * @param eb
	 *            DiscrepancyNoteBean
	 * @param connection
	 *            Connection
	 */
	public void createMapping(DiscrepancyNoteBean eb, Connection connection) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, eb.getEntityId());
		variables.put(index++, eb.getId());
		variables.put(index, eb.getColumn());
		String entityType = eb.getEntityType();

		if ("subject".equalsIgnoreCase(entityType)) {
			this.execute(digester.getQuery("createSubjectMap"), variables, connection);
		} else if ("studySub".equalsIgnoreCase(entityType)) {
			this.execute(digester.getQuery("createStudySubjectMap"), variables, connection);
		} else if ("eventCrf".equalsIgnoreCase(entityType)) {
			this.execute(digester.getQuery("createEventCRFMap"), variables, connection);
		} else if ("studyEvent".equalsIgnoreCase(entityType)) {
			this.execute(digester.getQuery("createStudyEventMap"), variables, connection);
		} else if ("itemData".equalsIgnoreCase(entityType)) {
			this.execute(digester.getQuery("createItemDataMap"), variables, connection);
		}

	}

	/**
	 * Update Discrepancy Note.
	 * 
	 * @param eb
	 *            EntityBean
	 * @return DiscrepancyNoteBean
	 */
	public EntityBean update(EntityBean eb) {
		DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) eb;
		dnb.setActive(false);

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, dnb.getDescription());
		variables.put(index++, dnb.getDiscrepancyNoteTypeId());
		variables.put(index++, dnb.getResolutionStatusId());
		variables.put(index++, dnb.getDetailedNotes());
		variables.put(index, dnb.getId());
		this.execute(digester.getQuery("update"), variables);

		if (isQuerySuccessful()) {
			dnb.setActive(true);
		}

		return dnb;
	}

	/**
	 * Updates assigned user id of a specific DN record.
	 *
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean updateAssignedUser(EntityBean eb) {
		DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) eb;
		dnb.setActive(false);

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, dnb.getAssignedUserId());
		variables.put(index, dnb.getId());
		this.execute(digester.getQuery("updateAssignedUser"), variables);

		if (isQuerySuccessful()) {
			dnb.setActive(true);
		}

		return dnb;
	}

	/**
	 * Sets assigned user id as <code>null</code> in a specific DN record.
	 *
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean updateAssignedUserToNull(EntityBean eb) {
		DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) eb;
		dnb.setActive(false);

		HashMap variables = new HashMap();
		variables.put(1, dnb.getId());
		this.execute(digester.getQuery("updateAssignedUserToNull"), variables);

		if (isQuerySuccessful()) {
			dnb.setActive(true);
		}

		return dnb;
	}

	/**
	 * Deletes DN record by its id.
	 *
	 * @param id
	 *            int
	 */
	public void deleteNotes(int id) {
		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(1, id);
		this.execute(digester.getQuery("deleteNotes"), variables);
	}

	/**
	 * Find all by permission. Not Implemented!
	 * 
	 * @param objCurrentUser
	 *            Object
	 * @param intActionType
	 *            int
	 * @param strOrderByColumn
	 *            String
	 * @param blnAscendingSort
	 *            boolean
	 * @param strSearchPhrase
	 *            String
	 * @return new ArrayList
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * Find all by permission. Not Implemented!
	 * 
	 * @param objCurrentUser
	 *            Object
	 * @param intActionType
	 *            int
	 * @return new ArrayList
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	@Override
	public int getCurrentPK() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		int pk = 0;
		ArrayList al = select(digester.getQuery("getCurrentPrimaryKey"));
		if (al.size() > 0) {
			HashMap h = (HashMap) al.get(0);
			pk = (Integer) h.get("key");
		}
		return pk;
	}

	/**
	 * Returns a list of child DNs by its Parent DN record.
	 *
	 * @param parent
	 *            DiscrepancyNoteBean
	 * @return ArrayList
	 */
	public ArrayList findAllByParent(DiscrepancyNoteBean parent) {
		HashMap variables = new HashMap();
		variables.put(1, parent.getId());
		return this.executeFindAllQuery("findAllByParent", variables);
	}

	/**
	 * Returns a list of DNs ids, associated with properties of a certain Study Event.
	 *
	 * @param studyEventId
	 *            int
	 * @return List<Integer>
	 */
	public List<Integer> findAllDnIdsByStudyEvent(int studyEventId) {
		List<Integer> result = new ArrayList<Integer>();
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, studyEventId);
		for (Object o : select(digester.getQuery("findAllDnIdsByStudyEvent"), variables)) {
			result.add((Integer) ((HashMap) o).get("discrepancy_note_id"));
		}
		return result;
	}

	private DiscrepancyNoteBean findSingleMapping(DiscrepancyNoteBean note) {
		HashMap variables = new HashMap();
		variables.put(1, note.getId());

		setMapTypesExpected();
		String entityType = note.getEntityType();
		String sql = "";
		if ("subject".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("findSubjectMapByDNId");
		} else if ("studySub".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("findStudySubjectMapByDNId");
		} else if ("eventCrf".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("findEventCRFMapByDNId");
		} else if ("studyEvent".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("findStudyEventMapByDNId");
		} else if ("itemData".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("findItemDataMapByDNId");
			this.unsetTypeExpected();
			int index = 1;
			this.setTypeExpected(index++, TypeNames.INT);
			this.setTypeExpected(index++, TypeNames.INT);
			this.setTypeExpected(index++, TypeNames.STRING);
			this.setTypeExpected(index, TypeNames.INT);
		}

		ArrayList hms = select(sql, variables);
		if (hms.size() > 0) {
			HashMap hm = (HashMap) hms.get(0);
			note = getMappingFromHashMap(hm, note);
		}
		return note;
	}

	private DiscrepancyNoteBean getMappingFromHashMap(HashMap hm, DiscrepancyNoteBean note) {
		String entityType = note.getEntityType();
		String entityIDColumn = getEntityIDColumn(entityType);
		if (!entityIDColumn.equals("")) {
			note.setEntityId(selectInt(hm, entityIDColumn));
		}
		note.setColumn(selectString(hm, "column_name"));
		return note;
	}

	private String getEntityIDColumn(String entityType) {
		String entityIDColumn = "";
		if ("subject".equalsIgnoreCase(entityType)) {
			entityIDColumn = "subject_id";
		} else if ("studySub".equalsIgnoreCase(entityType)) {
			entityIDColumn = "study_subject_id";
		} else if ("eventCrf".equalsIgnoreCase(entityType)) {
			entityIDColumn = "event_crf_id";
		} else if ("studyEvent".equalsIgnoreCase(entityType)) {
			entityIDColumn = "study_event_id";
		} else if ("itemData".equalsIgnoreCase(entityType)) {
			entityIDColumn = "item_data_id";
		}
		return entityIDColumn;
	}

	/**
	 * Returns Entity Bean, DN is associated with.
	 *
	 * @param note
	 *            DiscrepancyNoteBean
	 * @return AuditableEntityBean
	 */
	public AuditableEntityBean findEntity(DiscrepancyNoteBean note) {
		AuditableEntityDAO aedao = getAEDAO(note, getDataSource());
		try {
			if (aedao != null) {
				return (AuditableEntityBean) aedao.findByPK(note.getEntityId());
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return null;
	}

	private AuditableEntityDAO getAEDAO(DiscrepancyNoteBean note, DataSource ds) {
		String entityType = note.getEntityType();
		if ("subject".equalsIgnoreCase(entityType)) {
			return new SubjectDAO(ds);
		} else if ("studySub".equalsIgnoreCase(entityType)) {
			return new StudySubjectDAO(ds);
		} else if ("eventCrf".equalsIgnoreCase(entityType)) {
			return new EventCRFDAO(ds);
		} else if ("studyEvent".equalsIgnoreCase(entityType)) {
			return new StudyEventDAO(ds);
		} else if ("itemData".equalsIgnoreCase(entityType)) {
			return new ItemDataDAO(ds);
		}
		return null;
	}

	/**
	 * Returns count of DNs, associated with specific ItemData bean.
	 *
	 * @param itemDataId
	 *            int
	 * @return int
	 */
	public int findNumExistingNotesForItem(int itemDataId) {
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, itemDataId);
		String sql = digester.getQuery("findNumExistingNotesForItem");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			HashMap hm = (HashMap) it.next();
			try {
				return (Integer) hm.get("num");
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
		return 0;
	}

	/**
	 * Returns a list of DNs, associated with specific ItemData bean.
	 *
	 * @param itemDataId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findExistingNotesForItemData(int itemDataId) {
		this.setTypesExpected();
		ArrayList alist;
		HashMap variables = new HashMap();
		variables.put(1, itemDataId);
		alist = this.select(digester.getQuery("findExistingNotesForItemData"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object hm : alist) {
			al.add(getEntityFromHashMap((HashMap) hm));
		}
		return al;
	}

	/**
	 * Returns a list of DNs, associated with specific ItemData bean, for tooltip.
	 *
	 * @param itemDataId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findExistingNotesForToolTip(int itemDataId) {
		this.setTypesExpected();
		ArrayList alist;
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, itemDataId);
		variables.put(index++, itemDataId);
		variables.put(index++, itemDataId);
		variables.put(index, itemDataId);
		alist = this.select(digester.getQuery("findExistingNotesForToolTip"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object hm : alist) {
			al.add(getEntityFromHashMap((HashMap) hm));
		}
		return al;
	}

	/**
	 * Returns a list of DNs, associated with ItemData beans of a specific Event CRF, for tooltip.
	 *
	 * @param eventCrfId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findExistingNotesForToolTipByEventCrfId(int eventCrfId) {
		this.setTypesExpected();
		this.setTypeExpected(START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED, TypeNames.INT);
		ArrayList alist;
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, eventCrfId);
		variables.put(index, eventCrfId);
		alist = this.select(digester.getQuery("findExistingNotesForToolTipByEventCrfId"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object hm : alist) {
			al.add(getEntityFromHashMap((HashMap) hm));
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, associated with specific ItemData bean, for tooltip.
	 *
	 * @param itemDataId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findParentNotesForToolTip(int itemDataId) {
		this.setTypesExpected();
		ArrayList alist;
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, itemDataId);
		variables.put(index, itemDataId);

		alist = this.select(digester.getQuery("findParentNotesForToolTip"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object hm : alist) {
			al.add(getEntityFromHashMap((HashMap) hm));
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, associated with Item Data beans, in specific Event CRF.
	 *
	 * @param eventCRFId
	 *            int
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findAllTopNotesByEventCRF(int eventCRFId) {
		this.setTypesExpected();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.INT);
		ArrayList alist;
		HashMap variables = new HashMap();
		variables.put(1, eventCRFId);
		alist = this.select(digester.getQuery("findAllTopNotesByEventCRF"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object hm : alist) {
			al.add(getEntityFromHashMap((HashMap) hm));
		}
		return al;
	}

	/**
	 * Returns a list of parent DNs, associated with Interviewer Name and Interview Date properties of a specific Event
	 * CRF.
	 *
	 * @param eventCRFBean
	 *            EventCRFBean
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findOnlyParentEventCRFDNotesFromEventCRF(EventCRFBean eventCRFBean) {
		this.setTypesExpected();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index, TypeNames.STRING);
		ArrayList dNotelist;

		HashMap variables = new HashMap();
		variables.put(1, eventCRFBean.getId());
		dNotelist = this.select(digester.getQuery("findOnlyParentEventCRFDNotesFromEventCRF"), variables);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();
		for (Object aDNotelist : dNotelist) {
			HashMap hm = (HashMap) aDNotelist;
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			eb.setColumn((String) hm.get("column_name"));
			eb.setEventCRFId(eventCRFBean.getId());
			returnedNotelist.add(eb);
		}
		return returnedNotelist;
	}

	private String findSiteHiddenEventCrfIdsString(StudyBean site) {
		String sql;
		String valueOfBooleanTrue = ("oracle".equalsIgnoreCase(CoreResources.getDBType())) ? "1" : "'true'";

		sql = "SELECT DISTINCT ec.event_crf_id "
				+ "FROM (((event_crf ec LEFT JOIN study_event se ON ec.study_event_id = se.study_event_id) "
				+ "LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id) "
				+ "LEFT JOIN study_subject ss ON ec.study_subject_id = ss.study_subject_id) "
				+ "LEFT JOIN (SELECT edc.study_id, edc.study_event_definition_id, edc.crf_id "
				+ "FROM event_definition_crf edc " + "WHERE (edc.study_id = " + site.getId()
				+ " OR edc.study_id = (SELECT s.parent_study_id FROM study s WHERE s.study_id = " + site.getId() + ")) "
				+ "AND edc.status_id = 1 " + "AND edc.hide_crf = " + valueOfBooleanTrue
				+ ") sedc ON cv.crf_id = sedc.crf_id " + "WHERE ec.status_id NOT IN (5,7) "
				+ "AND se.study_event_definition_id = sedc.study_event_definition_id " + "AND (ss.study_id = "
				+ site.getId() + ")";

		return sql;
	}

	/**
	 * Returns the latest child DN of given parent DN record.
	 *
	 * @param parentId
	 *            int
	 * @return EntityBean
	 */
	public EntityBean findLatestChildByParent(int parentId) {
		DiscrepancyNoteBean eb = new DiscrepancyNoteBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, parentId);
		variables.put(index, parentId);

		String sql = digester.getQuery("findLatestChildByParent");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * Returns resolution status id for DN flag, for a specific property of a specific Subject bean.
	 *
	 * @param subjectId
	 *            int
	 * @param column
	 *            String
	 * @return int
	 */
	public int getResolutionStatusIdForSubjectDNFlag(int subjectId, String column) {
		int id = 0;
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, subjectId);
		variables.put(index, column);

		String sql = digester.getQuery("getResolutionStatusIdForSubjectDNFlag");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			HashMap hm = (HashMap) it.next();
			try {
				id = (Integer) hm.get("resolution_status_id");
			} catch (Exception e) {
				id = 0;
			}
		}
		return id;
	}

	/**
	 * Checking if a specific Study Subject has outstanding DNs, associated with Item Data beans within Event CRFs.
	 *
	 * @param ssb
	 *            StudySubjectBean
	 * @return boolean
	 */
	public boolean doesNotHaveOutstandingDNs(StudySubjectBean ssb) {
		Integer count = null;
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, ssb.getId());

		ArrayList rows = select(digester.getQuery("countOfOutstandingDNsForStudySubject"), variables);
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			count = (Integer) ((HashMap) it.next()).get("count");
		}

		return count != null && count == 0;
	}

	/**
	 * Finds out if a specific Study Event has outstanding DNs, associated with Study Event properties or with Item Data
	 * beans within Event CRFs.
	 *
	 * @param seb
	 *            StudyEventBean
	 * @return boolean
	 */
	public boolean doesNotHaveOutstandingDNs(StudyEventBean seb) {
		Integer count = null;
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, seb.getId());

		ArrayList rows = select(digester.getQuery("countOfOutstandingDNsForStudyEvent"), variables);
		rows.addAll(select(digester.getQuery("countOfOutstandingDNsForStudyEventFromStudyEventMap"), variables));
		for (Object row : rows) {
			count = (Integer) ((HashMap) row).get("count");
			if (count != null && count > 0) {
				break;
			}
		}

		return count != null && count == 0;
	}

	/**
	 * Finds out if a specific Event CRF has outstanding DNs, associated with its properties or with Item Data beans
	 * within Event CRF.
	 *
	 * @param ecb
	 *            EventCRFBean
	 * @return boolean
	 */
	public boolean doesNotHaveOutstandingDNs(EventCRFBean ecb) {
		Integer count = null;
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, ecb.getId());
		variables.put(index, ecb.getId());

		ArrayList rows = select(digester.getQuery("countOfOutstandingDNsForEventCrf"), variables);
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			count = (Integer) ((HashMap) it.next()).get("count");
		}
		return count != null && count == 0;
	}

	/**
	 * Finds out if a certain Study Subject has DNs in certain resolution status, associated with Event CRFs properties
	 * or with Item Data beans within Event CRFs.
	 *
	 * @param study
	 *            StudyBean
	 * @param subjectLabel
	 *            String
	 * @param resolutionStatus
	 *            String
	 * @param userAccount
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean doesSubjectHaveDNsInStudy(StudyBean study, String subjectLabel, String resolutionStatus,
			UserAccountBean userAccount) {

		ListNotesFilter listNotesFilter = new ListNotesFilter();
		listNotesFilter.addFilter("studySubject.label", subjectLabel);
		listNotesFilter.addFilter("discrepancyNoteBean.resolutionStatus", resolutionStatus);
		List<DiscrepancyNoteBean> noteBeans = this.getViewNotesWithFilterAndSortLimits(study, listNotesFilter,
				new ListNotesSort(), 0, DEFAULT_FETCH_UPPER_BOUND, true, userAccount);
		return noteBeans.size() > 0;
	}

	/**
	 * Finds out if a certain Study Subject has DNs with resolution status New, associated with Event CRFs properties or
	 * with Item Data beans within Event CRFs.
	 *
	 * @param study
	 *            StudyBean
	 * @param subjectLabel
	 *            String
	 * @param userAccount
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean doesSubjectHaveNewDNsInStudy(StudyBean study, String subjectLabel, UserAccountBean userAccount) {
		return doesSubjectHaveDNsInStudy(study, subjectLabel, "16", userAccount);
	}

	/**
	 * Finds out if a certain Study Subject has DNs with resolution status New/Updated, associated with Event CRFs
	 * properties or with Item Data beans within Event CRFs.
	 *
	 * @param study
	 *            StudyBean
	 * @param subjectLabel
	 *            String
	 * @param userAccount
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean doesSubjectHaveUnclosedDNsInStudy(StudyBean study, String subjectLabel,
			UserAccountBean userAccount) {
		return doesSubjectHaveDNsInStudy(study, subjectLabel, "1236", userAccount);
	}

	/**
	 * Finds out if a certain Study Subject has any DNs in study/site, with certain resolution status.
	 *
	 * @param study
	 *            StudyBean
	 * @param subjectLabel
	 *            String
	 * @param resolutionStatus
	 *            String
	 * @param userAccount
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean doesSubjectHaveAnyDNsInStudy(StudyBean study, String subjectLabel, String resolutionStatus,
			UserAccountBean userAccount) {

		ListNotesFilter listNotesFilter = new ListNotesFilter();
		listNotesFilter.addFilter("studySubject.label", subjectLabel);
		listNotesFilter.addFilter("discrepancyNoteBean.resolutionStatus", resolutionStatus);
		List<DiscrepancyNoteBean> noteBeans = this.getViewNotesWithFilterAndSortLimits(study, listNotesFilter,
				new ListNotesSort(), 0, DEFAULT_FETCH_UPPER_BOUND, userAccount);
		return noteBeans.size() > 0;
	}

	/**
	 * Finds out if a certain Study Subject has any DNs in study/site, with certain resolution status New.
	 *
	 * @param study
	 *            StudyBean
	 * @param subjectLabel
	 *            String
	 * @param userAccount
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean doesSubjectHaveAnyNewDNsInStudy(StudyBean study, String subjectLabel, UserAccountBean userAccount) {
		return doesSubjectHaveAnyDNsInStudy(study, subjectLabel, "16", userAccount);
	}

	/**
	 * Finds out if a certain Study Subject has any DNs in study/site, with certain resolution status New.
	 *
	 * @param study
	 *            StudyBean
	 * @param subjectLabel
	 *            String
	 * @return boolean
	 */
	public boolean doesSubjectHaveAnyNewDNsInStudy(StudyBean study, String subjectLabel) {
		return doesSubjectHaveAnyNewDNsInStudy(study, subjectLabel, null);
	}

	/**
	 * Finds out if a certain Study Subject has any DNs in study/site, with certain resolution status New/Updated.
	 *
	 * @param study
	 *            StudyBean
	 * @param subjectLabel
	 *            String
	 * @param userAccount
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean doesSubjectHaveAnyUnclosedDNsInStudy(StudyBean study, String subjectLabel,
			UserAccountBean userAccount) {
		return doesSubjectHaveAnyDNsInStudy(study, subjectLabel, "1236", userAccount);
	}

	/**
	 * Finds out if a certain Study Subject has any DNs in study/site, with certain resolution status New/Updated.
	 *
	 * @param study
	 *            StudyBean
	 * @param subjectLabel
	 *            String
	 * @return boolean
	 */
	public boolean doesSubjectHaveAnyUnclosedDNsInStudy(StudyBean study, String subjectLabel) {
		return doesSubjectHaveAnyUnclosedDNsInStudy(study, subjectLabel, null);
	}

	/**
	 * Finds out if a certain Study Subject has DNs in certain resolution status, associated with a specific Study Event
	 * bean.
	 *
	 * @param study
	 *            StudyBean
	 * @param eventLabel
	 *            String
	 * @param eventId
	 *            int
	 * @param subjectLabel
	 *            String
	 * @param resolutionStatus
	 *            String
	 * @param userAccount
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean doesEventHaveSomeDNsInStudy(StudyBean study, String eventLabel, int eventId, String subjectLabel,
			String resolutionStatus, UserAccountBean userAccount) {
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		listNotesFilter.addFilter("eventId", eventId);
		listNotesFilter.addFilter("eventName", eventLabel);
		listNotesFilter.addFilter("studySubject.label", subjectLabel);
		listNotesFilter.addFilter("discrepancyNoteBean.resolutionStatus", resolutionStatus);
		List<DiscrepancyNoteBean> noteBeans = this.getViewNotesWithFilterAndSortLimits(study, listNotesFilter,
				new ListNotesSort(), 0, DEFAULT_FETCH_UPPER_BOUND, userAccount);
		return noteBeans.size() > 0;
	}

	/**
	 * Finds out if a certain Study Subject has DNs with resolution status New, associated with a specific Study Event
	 * bean.
	 *
	 * @param study
	 *            StudyBean
	 * @param eventLabel
	 *            String
	 * @param eventId
	 *            int
	 * @param subjectLabel
	 *            String
	 * @param userAccount
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean doesEventHaveNewDNsInStudy(StudyBean study, String eventLabel, int eventId, String subjectLabel,
			UserAccountBean userAccount) {
		return doesEventHaveSomeDNsInStudy(study, eventLabel, eventId, subjectLabel, "16", userAccount);
	}

	/**
	 * Finds out if a certain Study Subject has DNs with resolution status New, associated with a specific Study Event
	 * bean.
	 *
	 * @param study
	 *            StudyBean
	 * @param eventLabel
	 *            String
	 * @param eventId
	 *            int
	 * @param subjectLabel
	 *            String
	 * @return boolean
	 */
	public boolean doesEventHaveNewDNsInStudy(StudyBean study, String eventLabel, int eventId, String subjectLabel) {
		return doesEventHaveNewDNsInStudy(study, eventLabel, eventId, subjectLabel, null);
	}

	/**
	 * Finds out if a certain Study Subject has DNs with resolution status New/Updated, associated with a specific Study
	 * Event bean.
	 *
	 * @param study
	 *            StudyBean
	 * @param eventLabel
	 *            String
	 * @param eventId
	 *            int
	 * @param subjectLabel
	 *            String
	 * @param userAccount
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean doesEventHaveUnclosedDNsInStudy(StudyBean study, String eventLabel, int eventId, String subjectLabel,
			UserAccountBean userAccount) {
		return doesEventHaveSomeDNsInStudy(study, eventLabel, eventId, subjectLabel, "1236", userAccount);
	}

	/**
	 * Finds out if a certain Study Subject has DNs with resolution status New/Updated, associated with a specific Study
	 * Event bean.
	 *
	 * @param study
	 *            StudyBean
	 * @param eventLabel
	 *            String
	 * @param eventId
	 *            int
	 * @param subjectLabel
	 *            String
	 * @return boolean
	 */
	public boolean doesEventHaveUnclosedDNsInStudy(StudyBean study, String eventLabel, int eventId,
			String subjectLabel) {
		return doesEventHaveUnclosedDNsInStudy(study, eventLabel, eventId, subjectLabel, null);
	}

	/**
	 * Finds out if a certain Study Subject has DNs in certain resolution status, associated with a specific Event CRF
	 * inside of a specific Study Event bean.
	 *
	 * @param study
	 *            StudyBean
	 * @param eventLabel
	 *            String
	 * @param eventId
	 *            int
	 * @param subjectLabel
	 *            String
	 * @param crfName
	 *            String
	 * @param resolutionStatus
	 *            String
	 * @param userAccount
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean doesCRFHaveDNsInStudyForSubject(StudyBean study, String eventLabel, int eventId, String subjectLabel,
			String crfName, String resolutionStatus, UserAccountBean userAccount) {
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		listNotesFilter.addFilter("eventId", eventId);
		listNotesFilter.addFilter("crfName", crfName);
		listNotesFilter.addFilter("eventName", eventLabel);
		listNotesFilter.addFilter("studySubject.label", subjectLabel);
		listNotesFilter.addFilter("discrepancyNoteBean.resolutionStatus", resolutionStatus);
		List<DiscrepancyNoteBean> noteBeans = this.getViewNotesWithFilterAndSortLimits(study, listNotesFilter,
				new ListNotesSort(), 0, DEFAULT_FETCH_UPPER_BOUND, true, userAccount);
		return noteBeans.size() > 0;
	}

	/**
	 * Finds out if a certain Study Subject has DNs with resolution status New, associated with a specific Event CRF
	 * inside of a specific Study Event bean.
	 *
	 * @param study
	 *            StudyBean
	 * @param eventLabel
	 *            String
	 * @param eventId
	 *            int
	 * @param subjectLabel
	 *            String
	 * @param crfName
	 *            String
	 * @param userAccount
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean doesCRFHaveNewDNsInStudyForSubject(StudyBean study, String eventLabel, int eventId,
			String subjectLabel, String crfName, UserAccountBean userAccount) {
		return doesCRFHaveDNsInStudyForSubject(study, eventLabel, eventId, subjectLabel, crfName, "16", userAccount);
	}

	/**
	 * Finds out if a certain Study Subject has DNs with resolution status New, associated with a specific Event CRF
	 * inside of a specific Study Event bean.
	 *
	 * @param study
	 *            StudyBean
	 * @param eventLabel
	 *            String
	 * @param eventId
	 *            int
	 * @param subjectLabel
	 *            String
	 * @param crfName
	 *            String
	 * @return boolean
	 */
	public boolean doesCRFHaveNewDNsInStudyForSubject(StudyBean study, String eventLabel, int eventId,
			String subjectLabel, String crfName) {
		return doesCRFHaveNewDNsInStudyForSubject(study, eventLabel, eventId, subjectLabel, crfName, null);
	}

	/**
	 * Finds out if a certain Study Subject has DNs with resolution status New/Updated, associated with a specific Event
	 * CRF inside of a specific Study Event bean.
	 *
	 * @param study
	 *            StudyBean
	 * @param eventLabel
	 *            String
	 * @param eventId
	 *            int
	 * @param subjectLabel
	 *            String
	 * @param crfName
	 *            String
	 * @param userAccount
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean doesCRFHaveUnclosedDNsInStudyForSubject(StudyBean study, String eventLabel, int eventId,
			String subjectLabel, String crfName, UserAccountBean userAccount) {
		return doesCRFHaveDNsInStudyForSubject(study, eventLabel, eventId, subjectLabel, crfName, "1236", userAccount);
	}

	/**
	 * Finds out if a certain Study Subject has DNs with resolution status New/Updated, associated with a specific Event
	 * CRF inside of a specific Study Event bean.
	 *
	 * @param study
	 *            StudyBean
	 * @param eventLabel
	 *            String
	 * @param eventId
	 *            int
	 * @param subjectLabel
	 *            String
	 * @param crfName
	 *            String
	 * @return boolean
	 */
	public boolean doesCRFHaveUnclosedDNsInStudyForSubject(StudyBean study, String eventLabel, int eventId,
			String subjectLabel, String crfName) {
		return doesCRFHaveUnclosedDNsInStudyForSubject(study, eventLabel, eventId, subjectLabel, crfName, null);
	}

	/**
	 * Returns count of DNs, associated with properties of a specific Study Event bean.
	 *
	 * @param studyEvent
	 *            StudyEventBean
	 * @return Integer
	 */
	public Integer countAllByStudyEventTypeAndStudyEvent(StudyEventBean studyEvent) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());

		ArrayList rows = select(digester.getQuery("countAllByStudyEventTypeAndStudyEvent"), variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			Integer count = (Integer) ((HashMap) it.next()).get("count");
			return count == null ? Integer.valueOf(0) : count;
		} else {
			return 0;
		}
	}

	/**
	 * Returns total count of parent DNs with certain resolution status, in specific study/site.
	 *
	 * @param studyId
	 *            int
	 * @param statusId
	 *            int
	 * @return int
	 */
	public int countViewNotesByStatusId(int studyId, int statusId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, statusId);
		variables.put(index++, studyId);
		variables.put(index, studyId);

		ArrayList rows = select(digester.getQuery("countViewNotesByStatusId"), variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			Integer count = (Integer) ((HashMap) it.next()).get("count");
			return count == null ? 0 : count;
		} else {
			return 0;
		}
	}

	/**
	 * Returns a list of Event CRF ids, for a specific Study Subject, which have unclosed DNs, associated with Item Data
	 * beans inside Event CRFs.
	 *
	 * @param studySubjectId
	 *            int
	 * @return List<Integer>
	 */
	public List<Integer> findAllEvCRFIdsWithUnclosedDNsByStSubId(int studySubjectId) {
		List<Integer> result = new ArrayList<Integer>();
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		for (Object o : select(digester.getQuery("findAllEvCRFIdsWithUnclosedDNsByStSubId"), variables)) {
			result.add((Integer) ((HashMap) o).get("event_crf_id"));
		}
		return result;
	}

	/**
	 * Find all by crf id.
	 *
	 * @param crfId
	 *            crf id
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findAllByCRFId(int crfId) {

		this.setTypesExpected();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index, TypeNames.STRING);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();

		HashMap variables = new HashMap();
		variables.put(1, crfId);

		ArrayList<HashMap> rows = select(digester.getQuery("findAllByCRFId"), variables);
		for (HashMap hm : rows) {
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			String studySubLabel = String.valueOf((hm).get("label"));
			eb.getStudySub().setLabel(studySubLabel);
			returnedNotelist.add(eb);
		}

		return returnedNotelist;
	}

	/**
	 * Find all by crf version id.
	 *
	 * @param crfVersionId
	 *            crf version id
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findAllByCrfVersionId(int crfVersionId) {

		this.setTypesExpected();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index, TypeNames.STRING);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();

		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);

		ArrayList<HashMap> rows = select(digester.getQuery("findAllByCrfVersionId"), variables);
		for (HashMap hm : rows) {
			DiscrepancyNoteBean eb = getEntityFromHashMap(hm);
			String studySubLabel = String.valueOf((hm).get("label"));
			eb.getStudySub().setLabel(studySubLabel);
			returnedNotelist.add(eb);
		}
		return returnedNotelist;
	}

	/**
	 * Find all by event crf id.
	 *
	 * @param eventCrfId
	 *            event crf id
	 * @return ArrayList<DiscrepancyNoteBean>
	 */
	public ArrayList<DiscrepancyNoteBean> findAllByEventCrfId(int eventCrfId) {

		this.setTypesExpected();
		int index = START_INDEX_TO_ADD_EXTRA_TYPES_EXPECTED;
		this.setTypeExpected(index, TypeNames.INT);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();

		HashMap variables = new HashMap();
		variables.put(1, eventCrfId);

		ArrayList<HashMap> rows = select(digester.getQuery("findAllByEventCrfId"), variables);
		for (HashMap hm : rows) {
			returnedNotelist.add(getEntityFromHashMap(hm));
		}
		return returnedNotelist;
	}

	/**
	 * Gets list of DiscrepancyCorrectionForms by note Ids.
	 *
	 * @param study
	 *            StudyBean
	 * @param resword
	 *            ResourceBundle
	 * @param noteIds
	 *            Integer...
	 * @return List<DiscrepancyCorrectionForm>
	 */
	public List<DiscrepancyCorrectionForm> getDiscrepancyCorrectionFormsByNoteIds(StudyBean study,
			ResourceBundle resword, Integer... noteIds) {
		this.setDcfTypesExpected();
		List<DiscrepancyCorrectionForm> dcfs = new ArrayList<DiscrepancyCorrectionForm>();
		if (noteIds.length > 0) {
			String query = buildDcfQuery(commaDelimitNoteIds(noteIds));
			List<HashMap> resultRows = select(query, new HashMap());
			for (HashMap resultRow : resultRows) {
				dcfs.add(extractDfcFromResultRow(resultRow, study, resword));
			}
		}
		return dcfs;
	}

	/**
	 * Deletes discrepancy notes by crf id.
	 *
	 * @param crfId
	 *            int
	 */
	public void deleteByCrfId(int crfId) {
		int index = 1;
		String sql = digester.getQuery("deleteByCrfId");
		HashMap variables = new HashMap();
		variables.put(index++, crfId);
		variables.put(index, crfId);
		execute(sql, variables);
	}

	/**
	 * Deletes discrepancy notes by crf version id.
	 *
	 * @param crfVersionId
	 *            int
	 */
	public void deleteByCrfVersionId(int crfVersionId) {
		int index = 1;
		String sql = digester.getQuery("deleteByCrfVersionId");
		HashMap variables = new HashMap();
		variables.put(index++, crfVersionId);
		variables.put(index, crfVersionId);
		execute(sql, variables);
	}

	private String buildDcfQuery(String commaDelimitedNoteIds) {
		final String where = "\nWHERE dn.discrepancy_note_id IN (".concat(commaDelimitedNoteIds).concat(")");
		final String union = "\n\nUNION\n\n";
		return new StringBuilder(digester.getQuery("findDcfsByNoteIdsForItemDataDNs")).append(where).append(union)
				.append(digester.getQuery("findDcfsByNoteIdsForStudyEventDNs")).append(where).append(union)
				.append(digester.getQuery("findDcfsByNoteIdsForEventCrfDNs")).append(where).append(union)
				.append(digester.getQuery("findDcfsByNoteIdsForStudySubjectDNs")).append(where).append(union)
				.append(digester.getQuery("findDcfsByNoteIdsForSubjectDNs")).append(where).toString();
	}

	private DiscrepancyCorrectionForm extractDfcFromResultRow(HashMap resultRow, StudyBean study,
			ResourceBundle resword) {
		int resolutionStatus = (Integer) resultRow.get("resolution_status_id");
		DiscrepancyCorrectionForm dcf = new DiscrepancyCorrectionForm();
		String entityType = resultRow.get("entity_type").toString().trim();
		dcf.setEntityType(entityType);
		setDcfItemNameAndValue(resultRow, dcf, entityType, study, resword);
		dcf.setEntityId((Integer) resultRow.get("entity_id"));
		dcf.setCrfName(resultRow.get("crf_name").toString().trim());
		dcf.setEventName(resultRow.get("event_name").toString().trim());
		dcf.setInvestigatorName(resultRow.get("investigator").toString().trim());
		dcf.setNoteDate((Date) resultRow.get("date_created"));
		dcf.setNoteId((Integer) resultRow.get("discrepancy_note_id"));
		dcf.setNoteType(DiscrepancyNoteType.get((Integer) resultRow.get("discrepancy_note_type_id")).getName().trim());
		dcf.setPage(resultRow.get("page").toString().trim());
		dcf.setQuestionToSite(getQuestionToSite(resultRow));
		if (resolutionStatus < 0) {
			dcf.setResolutionStatus("");
		} else {
			dcf.setResolutionStatus(ResolutionStatus.get(resolutionStatus).getName());
		}
		dcf.setSiteName(resultRow.get("site_name").toString().trim());
		dcf.setSiteOID(resultRow.get("site_oid").toString().trim());
		dcf.setStudyName(resultRow.get("study_name").toString().trim());
		dcf.setStudyProtocolID(resultRow.get("study_protocol").toString().trim());
		dcf.setSubjectId(resultRow.get("subject_id").toString().trim());
		return dcf;
	}

	private void setDcfItemNameAndValue(HashMap resultRow, DiscrepancyCorrectionForm dcf, String entityType,
			StudyBean study, ResourceBundle resword) {
		if (entityType.equals("subject") || entityType.equals("studySub")) {
			formatDcfSubjectItemNameAndValue(dcf, resultRow, study, resword);
		} else if (entityType.equals("studyEvent")) {
			formatDcfStudyEventItemNameAndValue(dcf, resultRow, study);
		} else if (entityType.equals("eventCrf")) {
			formatDcfEventCrfItemNameAndValue(dcf, resultRow, resword);
		} else {
			dcf.setCrfItemName(resultRow.get("item_name").toString().trim());
			dcf.setCrfItemValue(getCrfItemValue(resultRow));
		}
	}

	private void formatDcfSubjectItemNameAndValue(DiscrepancyCorrectionForm dcf, HashMap resultRow, StudyBean study,
			ResourceBundle resword) {
		String itemName = resultRow.get("item_name").toString().trim();
		Object itemValue = resultRow.get(itemName);
		if (itemName.equals("unique_identifier")) {
			dcf.setSubjectItemName(resword.getString("person_ID"));
			dcf.setSubjectItemValue(formatItemValue(itemValue));
		} else if (itemName.equals("date_of_birth")) {
			dcf.setSubjectItemName(resword.getString("date_of_birth"));
			dcf.setSubjectItemValue(formatDateItemValue(itemValue, false));
		} else if (itemName.equals("enrollment_date")) {
			dcf.setSubjectItemName(study.getStudyParameterConfig().getDateOfEnrollmentForStudyLabel());
			dcf.setSubjectItemValue(formatDateItemValue(itemValue, false));
		} else if (itemName.equals("gender")) {
			dcf.setSubjectItemName(study.getStudyParameterConfig().getGenderLabel());
			itemValue = formatItemValue(itemValue);
			if (itemValue.toString().equalsIgnoreCase("m")) {
				itemValue = resword.getString("male");
			} else if (itemValue.toString().equalsIgnoreCase("f")) {
				itemValue = resword.getString("female");
			}
			dcf.setSubjectItemValue(itemValue.toString());
		} else {
			dcf.setSubjectItemName("");
			dcf.setSubjectItemValue("");
		}
	}

	private void formatDcfStudyEventItemNameAndValue(DiscrepancyCorrectionForm dcf, HashMap resultRow,
			StudyBean study) {
		String itemName = resultRow.get("item_name").toString().trim();
		Object itemValue = resultRow.get(itemName);
		if (itemName.equals("date_start")) {
			dcf.setEventItemName(study.getStudyParameterConfig().getStartDateTimeLabel());
			dcf.setEventItemValue(formatDateItemValue(itemValue,
					study.getStudyParameterConfig().getUseStartTime().equalsIgnoreCase("yes")));
		} else if (itemName.equals("date_end")) {
			dcf.setEventItemName(study.getStudyParameterConfig().getEndDateTimeLabel());
			dcf.setEventItemValue(formatDateItemValue(itemValue,
					study.getStudyParameterConfig().getUseEndTime().equalsIgnoreCase("yes")));
		} else {
			dcf.setEventItemName("");
			dcf.setEventItemValue("");
		}
	}

	private void formatDcfEventCrfItemNameAndValue(DiscrepancyCorrectionForm dcf, HashMap resultRow,
			ResourceBundle resword) {
		String itemName = resultRow.get("item_name").toString().trim();
		Object itemValue = resultRow.get(itemName);
		if (itemName.equals("interviewer_name")) {
			dcf.setCrfItemName(resword.getString("interviewer_name"));
			dcf.setCrfItemValue(formatItemValue(itemValue));
		} else if (itemName.equals("date_interviewed")) {
			dcf.setCrfItemName(resword.getString("interview_date"));
			dcf.setCrfItemValue(formatDateItemValue(itemValue, false));
		} else {
			dcf.setCrfItemName("");
			dcf.setCrfItemValue("");
		}
	}

	private String formatItemValue(Object itemValue) {
		if (itemValue == null) {
			return "";
		}
		return itemValue.toString();
	}

	private String formatDateItemValue(Object itemValue, boolean considerTime) {
		if (itemValue == null) {
			return "";
		}
		if (DateUtil.isValidDate(itemValue.toString())) {
			Date date = DateUtil.convertStringToDate(itemValue.toString());
			return considerTime ? DateUtil.convertDateTimeToString(date) : DateUtil.convertDateToString(date);
		}
		return itemValue.toString();
	}

	private String getCrfItemValue(HashMap resultRow) {
		String itemValue = resultRow.get("item_value").toString().trim();
		if (itemValue.length() > 0) {
			String responseOptions = resultRow.get("options_text").toString();
			String responseValues = resultRow.get("options_values").toString();
			ResponseType responseType = ResponseType.get((Integer) resultRow.get("response_type_id"));
			if (responseType.equals(ResponseType.CHECKBOX) || responseType.equals(ResponseType.RADIO)
					|| responseType.equals(ResponseType.SELECT) || responseType.equals(ResponseType.SELECTMULTI)) {
				return extractItemValueText(itemValue, responseOptions, responseValues);
			}
			if (responseType.equals(ResponseType.TEXT) && DateUtil.isValidDate(itemValue)) {
				Date date = DateUtil.convertStringToDate(itemValue);
				return DateUtil.convertDateToString(date);
			}
		}
		return itemValue;
	}

	private String extractItemValueText(String itemValue, String responseOptions, String responseValues) {
		String[] responseOptionsText = responseOptions.split(",");
		String[] responseOptionsValues = responseValues.split(",");
		int itemValueIndex = 0;
		for (int i = 0; i < responseOptionsValues.length; i++) {
			if (responseOptionsValues[i].trim().equals(itemValue.trim())) {
				itemValueIndex = i;
				break;
			}
		}
		if (responseOptionsText.length > itemValueIndex) {
			return responseOptionsText[itemValueIndex];
		}
		return "";
	}

	private String getQuestionToSite(HashMap resultRow) {
		String questionToSite = resultRow.get("description").toString();
		questionToSite = questionToSite.concat("\n").concat(resultRow.get("detailed_notes").toString());
		return questionToSite.trim();
	}

	private String commaDelimitNoteIds(Integer... noteIds) {
		String splitter = "";
		StringBuilder commaDelimitedNoteIds = new StringBuilder("");
		for (Integer noteId : noteIds) {
			commaDelimitedNoteIds.append(splitter).append(noteId);
			splitter = ",";
		}
		return commaDelimitedNoteIds.toString();
	}
}
