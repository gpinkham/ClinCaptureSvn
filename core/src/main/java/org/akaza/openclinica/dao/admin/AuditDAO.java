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
package org.akaza.openclinica.dao.admin;

import org.akaza.openclinica.bean.admin.AuditBean;
import org.akaza.openclinica.bean.admin.DeletedEventCRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * AuditDAO.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AuditDAO extends EntityDAO {

	public static final int SECOND_INDEX = 14;
	public static final int THIRD_INDEX = 17;
	public static final int ITEM_TYPE_ID = 13;
	public static final int STUDY_SUBJECT_STATUS_CHANGED_ID = 3;
	public static final int SUBJECT_STATUS_CHANGED_ID = 6;
	public static final int ITEM_DATA_STATUS_CHANGED_ID = 12;
	public static final int EVENT_CRF_SDV_ID = 32;
	public static final int STUDY_EVENT_DELETE_ID = 51;

	/**
	 * Constructor with DataSource.
	 * @param ds DataSource
	 */
	public AuditDAO(DataSource ds) {
		super(ds);
	}

	/**
	 * Constructor with DataSource and digister.
	 * @param ds DataSource
	 * @param digester DAODigister.
	 */
	public AuditDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_AUDIT;
	}

	/**
	 * Set expected data types.
	 */
	public void setTypesExpected() {
		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT); // audit_id
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // audit_date
		this.setTypeExpected(index++, TypeNames.STRING); // audit_table
		this.setTypeExpected(index++, TypeNames.INT); // user_id
		this.setTypeExpected(index++, TypeNames.INT); // entity_id
		this.setTypeExpected(index++, TypeNames.STRING); // entity_name
		this.setTypeExpected(index++, TypeNames.STRING); // reason_for_change
		this.setTypeExpected(index++, TypeNames.INT); // audit_event_type_id
		this.setTypeExpected(index++, TypeNames.STRING); // old_value
		this.setTypeExpected(index++, TypeNames.STRING); // new_value
		this.setTypeExpected(index++, TypeNames.INT); // event_crf_id
		this.setTypeExpected(index++, TypeNames.STRING); // USER NAME
		this.setTypeExpected(index, TypeNames.STRING); // AUDIT EVENT NAME
	}

	/**
	 * Set expected type for entity with data type.
	 */
	public void setTypesExpectedWithItemDataType() {
		this.setTypesExpected();
		int index = SECOND_INDEX;
		this.setTypeExpected(index++, TypeNames.INT); // item_data_type_id
		this.setTypeExpected(index++, TypeNames.INT); // item_id
		this.setTypeExpected(index, TypeNames.STRING); // description
	}

	/**
	 * getEntityFromHashMap, the method that gets the object from the database query.
	 * @param hm HashMap.
	 * @return Object.
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		AuditBean eb = new AuditBean();
		eb.setId((Integer) hm.get("audit_id"));
		eb.setAuditDate((java.util.Date) hm.get("audit_date"));
		eb.setAuditTable((String) hm.get("audit_table"));
		eb.setUserId((Integer) hm.get("user_id"));
		eb.setEntityId((Integer) hm.get("entity_id"));
		eb.setEntityName((String) hm.get("entity_name"));
		eb.setReasonForChange((String) hm.get("reason_for_change"));
		if (eb.getAuditTable().equalsIgnoreCase("item_data")) {
			eb.setOldValue(Utils.convertedItemDateValue((String) hm.get("old_value"), oc_df_string, local_df_string));
			eb.setNewValue(Utils.convertedItemDateValue((String) hm.get("new_value"), oc_df_string, local_df_string));
		} else {
			eb.setOldValue((String) hm.get("old_value"));
			eb.setNewValue((String) hm.get("new_value"));
		}
		eb.setEventCRFId((Integer) hm.get("event_crf_id"));
		eb.setAuditEventTypeId((Integer) hm.get("audit_log_event_type_id"));
		eb.setUserName((String) hm.get("user_name"));
		eb.setAuditEventTypeName((String) hm.get("name"));

		return eb;
	}

	/**
	 * {@inheritDoc}
	*/
	public EntityBean findByPK(int id) {
		AuditBean eb = new AuditBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (AuditBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection findAll() {
		this.setTypesExpected();
		ArrayList alist = this.select(digester.getQuery("findAll"));
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find audit log events for a study subject.
	 *
	 * @param studySubjectId  int
	 * @return Collection
	 */
	public Collection findStudySubjectAuditEvents(int studySubjectId) {
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);

		String sql = digester.getQuery("findStudySubjectAuditEvents");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find audit log events type for a subject.
	 *
	 * @param subjectId int
	 * @return Collection.
	 */
	public Collection findSubjectAuditEvents(int subjectId) {
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, subjectId);

		String sql = digester.getQuery("findSubjectAuditEvents");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find audit log events type for an event CRF.
	 *
	 * @param eventCRFId int
	 * @return Collection
	 */
	public Collection findEventCRFAuditEvents(int eventCRFId) {
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, eventCRFId);

		String sql = digester.getQuery("findEventCRFAuditEvents");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find deleted Event CRFs from audit log.
	 *
	 * @param studyEventId int
	 * @return List.
	 */
	public List findDeletedEventCRFsFromAuditEvent(int studyEventId) {
		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT); // study_event_id
		this.setTypeExpected(index++, TypeNames.STRING); // crf name
		this.setTypeExpected(index++, TypeNames.STRING); // crf version
		this.setTypeExpected(index++, TypeNames.STRING); // user name
		this.setTypeExpected(index, TypeNames.DATE); // delete date

		HashMap variables = new HashMap();
		variables.put(1, ITEM_TYPE_ID); // audit_log_event_type_id
		// 13 means deleted
		// items
		variables.put(2, studyEventId);

		String sql = digester.getQuery("findDeletedEventCRFsFromAuditEvent");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		Iterator it = alist.iterator();
		logger.info("alist size [" + alist.size() + "]");
		while (it.hasNext()) {
			DeletedEventCRFBean bean = new DeletedEventCRFBean();
			HashMap map = (HashMap) it.next();
			bean.setStudyEventId(studyEventId);
			bean.setCrfName((String) map.get("crf_name"));
			bean.setCrfVersion((String) map.get("crf_version_name"));
			bean.setDeletedBy((String) map.get("user_name"));
			bean.setDeletedDate((Date) map.get("audit_date"));
			al.add(bean);
		}
		return al;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Not implemented.
	 */
	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Not implemented.
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Not implemented.
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Not implemented.
	 */
	public EntityBean update(EntityBean eb) {
		return eb;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Not implemented.
	 */
	public EntityBean create(EntityBean eb) {
		return eb;
	}

	/**
	 * Find audit group assignment log events for a study subject.
	 *
	 * @param studySubjectId int
	 * @return Collection
	 */
	public Collection findStudySubjectGroupAssignmentAuditEvents(int studySubjectId) {
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);

		String sql = digester.getQuery("findStudySubjectGroupAssignmentAuditEvents");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find audit events for a single Item.
	 *
	 * @param auditTable String
	 * @param entityId int
	 * @return ArrayList
	 */
	public ArrayList findItemAuditEvents(int entityId, String auditTable) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, entityId);
		variables.put(2, auditTable);

		String sql = digester.getQuery("findSingleItemAuditEvents");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) anAlist);
			// 3 6 12 32
			if (eb.getAuditEventTypeId() == STUDY_SUBJECT_STATUS_CHANGED_ID
					|| eb.getAuditEventTypeId() == SUBJECT_STATUS_CHANGED_ID
					|| eb.getAuditEventTypeId() == ITEM_DATA_STATUS_CHANGED_ID
					|| eb.getAuditEventTypeId() == EVENT_CRF_SDV_ID) {
				// If status is pending/unavailable - we should replace it by Initial/Completed
				String oldValue = eb.getOldValue().equals("4")
						? Status.INITIAL.getName() : eb.getOldValue().equals("2")
						? Status.COMPLETED.getName() : Status.get(new Integer(eb.getOldValue())).getName();
				String newValue = eb.getNewValue().equals("4")
						? Status.INITIAL.getName() : eb.getNewValue().equals("2")
						? Status.COMPLETED.getName() : Status.get(new Integer(eb.getNewValue())).getName();
				eb.setOldValue(oldValue);
				eb.setNewValue(newValue);
			}
			al.add(eb);
		}
		return al;
	}

	/**
	 * Check if Item Audit Event Exists.
	 * @param itemId int
	 * @param auditTable String
	 * @param ecbId int
	 * @return ArrayList
	 */
	public ArrayList checkItemAuditEventsExist(int itemId, String auditTable, int ecbId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, itemId);
		variables.put(index++, auditTable);
		variables.put(index, ecbId);

		String sql = digester.getQuery("checkItemAuditEventsExist");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) anAlist);
			if (eb.getAuditEventTypeId() == STUDY_SUBJECT_STATUS_CHANGED_ID
					|| eb.getAuditEventTypeId() == SUBJECT_STATUS_CHANGED_ID
					|| eb.getAuditEventTypeId() == ITEM_DATA_STATUS_CHANGED_ID
					|| eb.getAuditEventTypeId() == EVENT_CRF_SDV_ID) {
				eb.setOldValue(Status.get(new Integer(eb.getOldValue())).getName());
				eb.setNewValue(Status.get(new Integer(eb.getNewValue())).getName());
			}
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find last status.
	 *
	 * @param audiTable String
	 * @param entityId  int
	 * @param newValue  String
	 * @return String
	 */
	public String findLastStatus(String audiTable, int entityId, String newValue) {
		this.setTypesExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.STRING); // crf name
		this.setTypeExpected(index++, TypeNames.INT); // crf name
		this.setTypeExpected(index, TypeNames.STRING); // crf name

		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, audiTable);
		variables.put(index++, entityId);
		variables.put(index, newValue);

		String sql = digester.getQuery("findLastStatus");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (String) ((HashMap) it.next()).get("old_value");
		} else {
			return null;
		}
	}

	/**
	 * Save items.
	 *
	 * @param auditItemList List<Map<String, Object>>
	 */
	public void saveItems(List<Map<String, Object>> auditItemList) {
		for (Map auditItemMap : auditItemList) {
			HashMap nullVars = new HashMap();
			HashMap variables = new HashMap();

			int index = 1;
			variables.put(index++, auditItemMap.get("audit_log_event_type_id"));
			variables.put(index++, auditItemMap.get("user_id"));
			variables.put(index++, auditItemMap.get("audit_table"));
			variables.put(index++, auditItemMap.get("entity_id"));
			String entityName = (String) auditItemMap.get("entity_name");
			if (entityName == null) {
				nullVars.put(index, TypeNames.STRING);
			}
			variables.put(index++, entityName);
			String oldValue = (String) auditItemMap.get("old_value");
			if (oldValue == null) {
				nullVars.put(index, TypeNames.STRING);
			}
			variables.put(index++, oldValue);
			String newValue = (String) auditItemMap.get("new_value");
			if (newValue == null) {
				nullVars.put(index, TypeNames.STRING);
			}
			variables.put(index++, newValue);
			variables.put(index, auditItemMap.get("event_crf_id"));

			String sql = digester.getQuery("insert");
			this.execute(sql, variables, nullVars, con);
		}
	}

	/**
	 * Method that returns the list of the audit events by studyEventId.
	 * 
	 * @param studyEventId
	 *            int
	 * @return List<AuditBean>
	 */
	public Collection findStudyEventAuditEvents(int studyEventId) {
		this.setTypesExpected();

		int index = SECOND_INDEX;
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(index++, TypeNames.STRING); // location
		this.setTypeExpected(index++, TypeNames.INT); // ordinal
		this.setTypeExpected(index++, TypeNames.INT); // event_definition_crf_id
		this.setTypeExpected(index++, TypeNames.INT); // study_event_definition_id
		this.setTypeExpected(index, TypeNames.INT); // study_subject_id

		HashMap variables = new HashMap();
		variables.put(1, studyEventId);

		String sql = digester.getQuery("findStudyEventAuditEvents");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (HashMap anAList : aList) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap(anAList);
			if (eb.getAuditEventTypeId() == STUDY_EVENT_DELETE_ID) {
				eb.setDateStart((Timestamp) anAList.get("date_start"));
				eb.setLocation((String) anAList.get("location"));
				eb.setEventDefinitionCrfId((Integer) anAList.get("event_definition_crf_id"));
				eb.setStudyEventSampleOrdinal((Integer) anAList.get("ordinal"));
				eb.setStudyEventDefinitionId((Integer) anAList.get("study_event_definition_id"));
				eb.setStudySubjectId((Integer) anAList.get("study_subject_id"));
			}
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that finds the deleted study events.
	 * 
	 * @param studySubjectId
	 *            int
	 * @return List<AuditBean>
	 */
	public List<AuditBean> findDeletedStudyEvents(int studySubjectId) {
		this.setTypesExpected();

		int index = SECOND_INDEX;
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(index++, TypeNames.STRING); // location
		this.setTypeExpected(index++, TypeNames.INT); // ordinal
		this.setTypeExpected(index++, TypeNames.INT); // event_definition_crf_id
		this.setTypeExpected(index++, TypeNames.INT); // study_event_definition_id
		this.setTypeExpected(index, TypeNames.INT); // study_subject_id

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);

		String sql = digester.getQuery("findDeletedStudyEvents");
		List<HashMap> aList = this.select(sql, variables);
		List<AuditBean> al = new ArrayList<AuditBean>();
		for (HashMap anAList : aList) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap(anAList);
			eb.setDateStart((Timestamp) anAList.get("date_start"));
			eb.setLocation((String) anAList.get("location"));
			eb.setEventDefinitionCrfId((Integer) anAList.get("event_definition_crf_id"));
			eb.setStudyEventSampleOrdinal((Integer) anAList.get("ordinal"));
			eb.setStudyEventDefinitionId((Integer) anAList.get("study_event_definition_id"));
			eb.setStudySubjectId((Integer) anAList.get("study_subject_id"));
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that finds the deleted event crfs.
	 *
	 * @param studySubjectId int
	 * @param studyEventId   int
	 * @return List<AuditBean>
	 */
	public List<AuditBean> findDeletedEventCrfs(int studySubjectId, int studyEventId) {
		this.setTypesExpected();

		int index = SECOND_INDEX;
		this.setTypeExpected(index++, TypeNames.STRING); // crf name
		this.setTypeExpected(index++, TypeNames.STRING); // crf version
		this.setTypeExpected(index++, TypeNames.DATE); // date_interviewed
		this.setTypeExpected(index++, TypeNames.STRING); // interviewer_name
		this.setTypeExpected(index++, TypeNames.INT); // event_crf_version_id
		this.setTypeExpected(index++, TypeNames.INT); // study_event_id
		this.setTypeExpected(index++, TypeNames.INT); // event_definition_crf_id
		this.setTypeExpected(index++, TypeNames.INT); // study_event_definition_id
		this.setTypeExpected(index, TypeNames.INT); // study_subject_id

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		variables.put(2, studyEventId);

		String sql = digester.getQuery("findDeletedEventCrfs");
		List<HashMap> aList = this.select(sql, variables);
		List<AuditBean> al = new ArrayList<AuditBean>();
		for (HashMap anAList : aList) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap(anAList);
			eb.setCrfName((String) anAList.get("name"));
			eb.setCrfVersion((String) anAList.get("version"));
			eb.setDateInterviewed((Date) anAList.get("date_interviewed"));
			eb.setInterviewerName((String) anAList.get("interviewer_name"));
			eb.setCrfVersionId((Integer) anAList.get("event_crf_version_id"));
			eb.setEventDefinitionCrfId((Integer) anAList.get("event_definition_crf_id"));
			eb.setStudyEventId((Integer) anAList.get("study_event_id"));
			eb.setStudyEventDefinitionId((Integer) anAList.get("study_event_definition_id"));
			eb.setStudySubjectId((Integer) anAList.get("study_subject_id"));
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that finds event CRF audit events with item data type.
	 *
	 * @param eventCRFId   int
	 * @param crfVersionId int
	 * @return List<AuditBean>
	 */
	public List<AuditBean> findEventCRFAuditEventsWithItemDataType(int eventCRFId, int crfVersionId) {
		List<AuditBean> result = new ArrayList();

		this.setTypesExpectedWithItemDataType();
		// item data ordinal
		this.setTypeExpected(THIRD_INDEX, TypeNames.INT);

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, eventCRFId);
		variables.put(index++, crfVersionId);
		variables.put(index, crfVersionId);

		String sql = digester.getQuery("findEventCRFAuditEventsWithItemDataTypeAndItemData");
		List<HashMap> aList = this.select(sql, variables);
		for (HashMap hm : aList) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap(hm);
			eb.setItemDataTypeId((Integer) hm.get("item_data_type_id"));
			eb.setItemId((Integer) hm.get("item_id"));
			eb.setItemDescription((String) hm.get("description"));
			eb.setOrdinal((Integer) hm.get("ordinal"));

			result.add(eb);
		}
		return result;
	}
}
