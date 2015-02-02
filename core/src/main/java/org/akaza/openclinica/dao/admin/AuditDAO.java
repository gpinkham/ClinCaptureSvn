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

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AuditDAO extends EntityDAO {

	public AuditDAO(DataSource ds) {
		super(ds);
	}

	public AuditDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_AUDIT;
	}

	public void setTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT); // audit_id
		this.setTypeExpected(2, TypeNames.TIMESTAMP); // audit_date
		this.setTypeExpected(3, TypeNames.STRING); // audit_table
		this.setTypeExpected(4, TypeNames.INT); // user_id
		this.setTypeExpected(5, TypeNames.INT); // entity_id
		this.setTypeExpected(6, TypeNames.STRING); // entity_name
		this.setTypeExpected(7, TypeNames.STRING); // reason_for_change
		this.setTypeExpected(8, TypeNames.INT); // audit_event_type_id
		this.setTypeExpected(9, TypeNames.STRING); // old_value
		this.setTypeExpected(10, TypeNames.STRING); // new_value
		this.setTypeExpected(11, TypeNames.INT); // event_crf_id
		this.setTypeExpected(12, TypeNames.STRING); // USER NAME
		this.setTypeExpected(13, TypeNames.STRING); // AUDIT EVENT NAME

	}

	public void setTypesExpectedWithItemDataType() {
		this.setTypesExpected();
		this.setTypeExpected(14, TypeNames.INT); // item_data_type_id
		this.setTypeExpected(15, TypeNames.INT); // item_id
		this.setTypeExpected(16, TypeNames.STRING); // description
	}

	/**
	 * <p>
	 * getEntityFromHashMap, the method that gets the object from the database query.
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		AuditBean eb = new AuditBean();
		// AUDIT_ID AUDIT_DATE AUDIT_TABLE USER_ID ENTITY_ID
		// REASON_FOR_CHANGE
		eb.setId((Integer) hm.get("audit_id"));
		eb.setAuditDate((java.util.Date) hm.get("audit_date"));
		eb.setAuditTable((String) hm.get("audit_table"));
		eb.setUserId((Integer) hm.get("user_id"));
		eb.setEntityId((Integer) hm.get("entity_id"));
		eb.setEntityName((String) hm.get("entity_name"));
		eb.setReasonForChange((String) hm.get("reason_for_change"));
		// YW 12-07-2007 <<
		if (eb.getAuditTable().equalsIgnoreCase("item_data")) {
			eb.setOldValue(Utils.convertedItemDateValue((String) hm.get("old_value"), oc_df_string, local_df_string));
			eb.setNewValue(Utils.convertedItemDateValue((String) hm.get("new_value"), oc_df_string, local_df_string));
		} else {
			eb.setOldValue((String) hm.get("old_value"));
			eb.setNewValue((String) hm.get("new_value"));
		}
		// YW >>
		eb.setEventCRFId((Integer) hm.get("event_crf_id"));
		eb.setAuditEventTypeId((Integer) hm.get("audit_log_event_type_id"));
		eb.setUserName((String) hm.get("user_name"));
		eb.setAuditEventTypeName((String) hm.get("name"));

		return eb;
	}

	public Object getEntityFromHashMapWithItemDataTypeAndItemData(HashMap hm) {
		AuditBean eb = (AuditBean) this.getEntityFromHashMap(hm);
		eb.setItemDataTypeId((Integer) hm.get("item_data_type_id"));
		eb.setItemId((Integer) hm.get("item_id"));
		eb.setItemDescription((String) hm.get("description"));
		return eb;
	}

	/**
	 * Find By Primary Key
	 * 
	 * @see org.akaza.openclinica.dao.core.DAOInterface#findByPK(int)
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
	 * Find All Audit Beans
	 * 
	 * @see org.akaza.openclinica.dao.core.DAOInterface#findAll()
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
	 * Find audit log events for a study subject
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
	 * Find audit log events type for a subject
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
	 * Find audit log events type for an event CRF
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
	 * Find deleted Event CRFs from audit log
	 */
	public List findDeletedEventCRFsFromAuditEvent(int studyEventId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT); // study_event_id
		this.setTypeExpected(2, TypeNames.STRING); // crf name
		this.setTypeExpected(3, TypeNames.STRING); // crf version
		this.setTypeExpected(4, TypeNames.STRING); // user name
		this.setTypeExpected(5, TypeNames.DATE); // delete date

		HashMap variables = new HashMap();
		variables.put(1, 13); // audit_log_event_type_id
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

	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	// TODO: This method not fully implemented
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// /
	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	// TODO: This method not fully implemented
	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	// TODO: This method not fully implemented
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// /
	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	// TODO: This method not fully implemented
	// Audit events should not be writable
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// /
	public EntityBean update(EntityBean eb) {
		return eb;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	// TODO: This method not fully implemented
	// Audit events should not be created in code, they are only created by
	// database triggers
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// /
	public EntityBean create(EntityBean eb) {
		return eb;
	}

	/**
	 * Find audit group assignment log events for a study subject
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
	 * Find audit events for a single Item
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
			if (eb.getAuditEventTypeId() == 3 || eb.getAuditEventTypeId() == 6 || eb.getAuditEventTypeId() == 12
					|| eb.getAuditEventTypeId() == 32) {
				// If status is pending/unavailable - we should replace it by Initial/Completed
				String oldValue = eb.getOldValue().equals("4") ?
						Status.INITIAL.getName() :
						eb.getOldValue().equals("2") ?
								Status.COMPLETED.getName() :
								Status.get(new Integer(eb.getOldValue())).getName();
				String newValue = eb.getNewValue().equals("4") ?
						Status.INITIAL.getName() :
						eb.getNewValue().equals("2") ?
								Status.COMPLETED.getName() :
								Status.get(new Integer(eb.getNewValue())).getName();
				eb.setOldValue(oldValue);
				eb.setNewValue(newValue);
			}

			al.add(eb);
		}
		return al;

	}

	public ArrayList checkItemAuditEventsExist(int itemId, String auditTable, int ecbId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, itemId);
		variables.put(2, auditTable);
		variables.put(3, ecbId);

		String sql = digester.getQuery("checkItemAuditEventsExist");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) anAlist);
			if (eb.getAuditEventTypeId() == 3 || eb.getAuditEventTypeId() == 6 || eb.getAuditEventTypeId() == 12
					|| eb.getAuditEventTypeId() == 32) {
				eb.setOldValue(Status.get(new Integer(eb.getOldValue())).getName());
				eb.setNewValue(Status.get(new Integer(eb.getNewValue())).getName());
			}
			al.add(eb);
		}
		return al;
	}

	/**
	 * find last status
	 * 
	 * @param audit_table
	 *            String
	 * @param entity_id
	 *            int
	 * @param new_value
	 *            String
	 * @return String
	 */
	public String findLastStatus(String audit_table, int entity_id, String new_value) {
		this.setTypesExpected();
		this.setTypeExpected(1, TypeNames.STRING); // crf name
		this.setTypeExpected(2, TypeNames.INT); // crf name
		this.setTypeExpected(3, TypeNames.STRING); // crf name

		HashMap variables = new HashMap();
		variables.put(1, audit_table);
		variables.put(2, entity_id);
		variables.put(3, new_value);

		String sql = digester.getQuery("findLastStatus");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (String) ((HashMap) it.next()).get("old_value");
		} else {
			return null;
		}
	}

	public void saveItems(List<Map<String, Object>> auditItemList) {
		for (Map auditItemMap : auditItemList) {
			HashMap nullVars = new HashMap();
			HashMap variables = new HashMap();

			variables.put(1, auditItemMap.get("audit_log_event_type_id"));
			variables.put(2, auditItemMap.get("user_id"));
			variables.put(3, auditItemMap.get("audit_table"));
			variables.put(4, auditItemMap.get("entity_id"));
			String entityName = (String) auditItemMap.get("entity_name");
			variables.put(5, entityName);
			if (entityName == null) {
				nullVars.put(5, TypeNames.STRING);
			}
			String oldValue = (String) auditItemMap.get("old_value");
			variables.put(6, oldValue);
			if (oldValue == null) {
				nullVars.put(6, TypeNames.STRING);
			}
			String newValue = (String) auditItemMap.get("new_value");
			variables.put(7, newValue);
			if (newValue == null) {
				nullVars.put(7, TypeNames.STRING);
			}

			variables.put(8, auditItemMap.get("event_crf_id"));

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

		this.setTypeExpected(14, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(15, TypeNames.STRING); // location
		this.setTypeExpected(16, TypeNames.INT); // ordinal
		this.setTypeExpected(17, TypeNames.INT); // event_definition_crf_id
		this.setTypeExpected(18, TypeNames.INT); // study_event_definition_id
		this.setTypeExpected(19, TypeNames.INT); // study_subject_id

		HashMap variables = new HashMap();
		variables.put(1, studyEventId);

		String sql = digester.getQuery("findStudyEventAuditEvents");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (HashMap anAList : aList) {
			AuditBean eb = (AuditBean) this.getEntityFromHashMap(anAList);
			if (eb.getAuditEventTypeId() == 51) {
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

		this.setTypeExpected(14, TypeNames.TIMESTAMP); // date_start
		this.setTypeExpected(15, TypeNames.STRING); // location
		this.setTypeExpected(16, TypeNames.INT); // ordinal
		this.setTypeExpected(17, TypeNames.INT); // event_definition_crf_id
		this.setTypeExpected(18, TypeNames.INT); // study_event_definition_id
		this.setTypeExpected(19, TypeNames.INT); // study_subject_id

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
	 * @param studySubjectId
	 *            int
	 * @param studyEventId
	 *            int
	 * @return List<AuditBean>
	 */
	public List<AuditBean> findDeletedEventCrfs(int studySubjectId, int studyEventId) {
		this.setTypesExpected();

		this.setTypeExpected(14, TypeNames.STRING); // crf name
		this.setTypeExpected(15, TypeNames.STRING); // crf version
		this.setTypeExpected(16, TypeNames.DATE); // date_interviewed
		this.setTypeExpected(17, TypeNames.STRING); // interviewer_name
		this.setTypeExpected(18, TypeNames.INT); // event_crf_version_id
		this.setTypeExpected(19, TypeNames.INT); // study_event_id
		this.setTypeExpected(20, TypeNames.INT); // event_definition_crf_id
		this.setTypeExpected(21, TypeNames.INT); // study_event_definition_id
		this.setTypeExpected(22, TypeNames.INT); // study_subject_id

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
	 * @param eventCRFId
	 *            int
	 * @return List<AuditBean>
	 */
	public List<AuditBean> findEventCRFAuditEventsWithItemDataType(int eventCRFId) {
		List<AuditBean> result = new ArrayList();

		this.setTypesExpectedWithItemDataType();
		// item data ordinal
		this.setTypeExpected(17, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, eventCRFId);

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
