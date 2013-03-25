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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.admin;

import org.akaza.openclinica.bean.admin.AuditEventBean;
import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

@SuppressWarnings({"rawtypes","unchecked"})
public class AuditEventDAO extends AuditableEntityDAO {

	public AuditEventDAO(DataSource ds) {
		super(ds);
	}

	public AuditEventDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_AUDITEVENT;
	}

	@Override
	public void setTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.TIMESTAMP);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.INT);
		this.setTypeExpected(5, TypeNames.INT);
		this.setTypeExpected(6, TypeNames.STRING);
		this.setTypeExpected(7, TypeNames.STRING);

	}

	/**
	 * <p>
	 * getEntityFromHashMap, the method that gets the object from the database query.
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		AuditEventBean eb = new AuditEventBean();
		eb.setId(((Integer) hm.get("audit_id")).intValue());
		eb.setAuditDate((java.util.Date) hm.get("audit_date"));
		eb.setAuditTable((String) hm.get("audit_table"));
		eb.setUserId(((Integer) hm.get("user_id")).intValue());
		eb.setEntityId(((Integer) hm.get("entity_id")).intValue());
		eb.setReasonForChange(((String) hm.get("reason_for_change")).trim());
		eb.setActionMessage(((String) hm.get("action_message")).trim());

		return eb;
	}

	/**
	 * <p>
	 * getEntityFromHashMap, the method that gets the object from the database query.
	 */
	public Object getEntityFromHashMap(HashMap hm, boolean hasValue, boolean hasColumnName, boolean hasContextIds) {
		AuditEventBean eb = new AuditEventBean();
		eb.setId(((Integer) hm.get("audit_id")).intValue());
		eb.setAuditDate((java.util.Date) hm.get("audit_date"));
		eb.setAuditTable((String) hm.get("audit_table"));
		eb.setUserId(((Integer) hm.get("user_id")).intValue());
		eb.setEntityId(((Integer) hm.get("entity_id")).intValue());
		eb.setReasonForChange((String) hm.get("reason_for_change"));
		eb.setActionMessage((String) hm.get("action_message"));
		if (hasValue) {
			eb.setOldValue((String) hm.get("old_value"));
			eb.setNewValue((String) hm.get("new_value"));
		}
		if (hasColumnName) {
			eb.setColumnName((String) hm.get("column_name"));
		}
		if (hasContextIds) {
			eb.setStudyId(((Integer) hm.get("study_id")).intValue());
			eb.setSubjectId(((Integer) hm.get("subject_id")).intValue());
		}

		return eb;
	}

	public AuditEventBean setStudyAndSubjectInfo(AuditEventBean aeb) {
		if (aeb.getStudyId() > 0) {
			StudyDAO sdao = new StudyDAO(this.ds);
			StudyBean sbean = (StudyBean) sdao.findByPK(aeb.getStudyId());
			aeb.setStudyName(sbean.getName());
		}
		if (aeb.getSubjectId() > 0) {
			SubjectBean subbean = new SubjectBean();
			SubjectDAO subdao = new SubjectDAO(this.ds);
			subbean = (SubjectBean) subdao.findByPK(aeb.getSubjectId());
			aeb.setSubjectName(subbean.getName());
		}
		if (aeb.getUserId() > 0) {
			UserAccountBean updater = new UserAccountBean();
			UserAccountDAO uadao = new UserAccountDAO(this.ds);
			updater = (UserAccountBean) uadao.findByPK(aeb.getUserId());
			aeb.setUpdater(updater);
		}
		return aeb;
	}

	/**
	 * <p>
	 * getEntityFromHashMap, the method that gets the object from the database query.
	 */
	public Object getColumnNameFromHashMap(HashMap hm) {
		AuditEventBean eb = new AuditEventBean();

		eb.setUpdateCount(((Integer) hm.get("count")).intValue());
		eb.setColumnName((String) hm.get("column_name"));
		return eb;
	}

	public Collection findAll() {
		this.setTypesExpected();
		ArrayList alist = this.select(digester.getQuery("findAll"));
		ArrayList al = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			AuditEventBean eb = (AuditEventBean) this.getEntityFromHashMap((HashMap) it.next());
			al.add(eb);
		}
		return al;
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		ArrayList al = new ArrayList();

		return al;
	}

	public EntityBean findByPK(int id) {
		AuditEventBean eb = new AuditEventBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(id));

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (AuditEventBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return eb;
	}

	/**
	 * Creates a new row in the audit_log_event table
	 */
	public EntityBean create(EntityBean eb) {
		AuditEventBean sb = (AuditEventBean) eb;
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(new Integer(1), sb.getAuditTable());
		variables.put(new Integer(2), new Integer(sb.getUserId()));
		variables.put(new Integer(3), new Integer(sb.getEntityId()));
		variables.put(new Integer(4), sb.getReasonForChangeKey());
		variables.put(new Integer(5), sb.getActionMessageKey());

		this.execute(digester.getQuery("create"), variables);

		return sb;
	}

	public void createRowForJobConclusion(TriggerBean trigger, int eventTypeId) {
		AuditEventBean auditEvent = new AuditEventBean();
		auditEvent.setUserId(trigger.getUserAccount().getId());
		auditEvent.setEntityId(trigger.getDataset().getId());
		auditEvent.setAuditTable("datasets");
		auditEvent.setName(trigger.getFullName());
		auditEvent.setActionMessage("");
		auditEvent.setOldValue("");
		auditEvent.setNewValue("");
		auditEvent.setReasonForChange("");
	}

	public void createRowForUserAccount(UserAccountBean uab, String reasonForChange, String actionMessage) {
		// creator method for making a row in the audit table
		// for a user account, tbh
		// TODO doesn't work -- set it up by adding rows to context and values
		AuditEventBean aeb = new AuditEventBean();
		aeb.setUserId(uab.getId());
		aeb.setEntityId(uab.getId());
		aeb.setAuditTable("__user_account");
		aeb.setReasonForChange(reasonForChange);
		aeb.setActionMessage(actionMessage);

	}

	public void createRowForFailedLogin(UserAccountBean uab) {
		createRowForUserAccount(uab, "__unsuccessful_login_attempt", "__failed_login");
	}

	public void createRowForLogin(UserAccountBean uab) {
		createRowForUserAccount(uab, "__successful_login", "__logged_in");
	}

	public void createRowForPasswordRequest(UserAccountBean uab) {
		createRowForUserAccount(uab, "__requested_password", "__requested_password");
	}

	public void createRowForJobExecution(TriggerBean triggerBean, String reasonForChange, String actionMessage) {
		AuditEventBean auditEventBean = new AuditEventBean();
		auditEventBean.setUserId(triggerBean.getUserAccount().getId());
		auditEventBean.setEntityId(triggerBean.getDataset().getId());
		auditEventBean.setAuditTable(triggerBean.getFullName());
		//
		auditEventBean.setReasonForChange(reasonForChange);
		auditEventBean.setActionMessage(actionMessage);
	}

	public void createRowForExtractDataJobSuccess(TriggerBean triggerBean) {
		createRowForJobExecution(triggerBean, "__job_fired_success", "__job_fired_success");
	}

	public void createRowForExtractDataJobSuccess(TriggerBean triggerBean, String message) {
		createRowForJobExecution(triggerBean, "__job_fired_success", message);
	}

	public void createRowForExtractDataJobFailure(TriggerBean triggerBean) {
		createRowForJobExecution(triggerBean, "__job_fired_fail", "__job_fired_fail");
	}

	public void createRowForExtractDataJobFailure(TriggerBean triggerBean, String message) {
		createRowForJobExecution(triggerBean, "__job_fired_fail", message);
	}

	public ArrayList findAllByAuditTable(String tableName) {
		ArrayList<AuditEventBean> al = new ArrayList<AuditEventBean>();

		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(new Integer(1), tableName);

		String sql = digester.getQuery("findAllByAuditTable");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		while (it.hasNext()) {
			AuditEventBean eb = new AuditEventBean();
			eb = (AuditEventBean) this.getEntityFromHashMap((HashMap) it.next());
			al.add(eb);
		}

		return al;
	}

	public Collection findAggregatesByTableName(String tableName) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.STRING);
		HashMap variables = new HashMap();
		variables.put(new Integer(1), tableName);

		String sql = digester.getQuery("findAggregatesByTableName");
		logger.info("sql is: " + sql);
		ArrayList alist = this.select(sql, variables);
		logger.info("size is: " + alist.size());

		ArrayList al = new ArrayList();
		Iterator it = alist.iterator();

		while (it.hasNext()) {
			logger.info("has next..");
			AuditEventBean eb = (AuditEventBean) this.getColumnNameFromHashMap((HashMap) it.next());
			logger.info("got bean");
			al.add(eb);
		}

		return al;

	}

	public ArrayList findAllByStudyId(int studyId) {
		/*
		 * select ae. , aev.old_value, aev.new_value, aev.column_name, aec.study_id, aec.subject_id from audit_event ae,
		 * audit_event_values aev, audit_event_context aec where ae.audit_id=aev.audit_id and ae.audit_id = aec.audit_id
		 * and aec.study_id=? order by ae.audit_date
		 */

		ArrayList al = this.findAllByEntityName(studyId, "findAllByStudyId");

		return al;
	}

	public ArrayList findAllByStudyIdAndLimit(int studyId) {
		/*
		 * select ae. , aev.old_value, aev.new_value, aev.column_name, aec.study_id, aec.subject_id from audit_event ae,
		 * audit_event_values aev, audit_event_context aec where ae.audit_id=aev.audit_id and ae.audit_id = aec.audit_id
		 * and aec.study_id=? order by ae.audit_date
		 */

		ArrayList al = this.findAllByEntityName(studyId, "findAllByStudyIdAndLimit");

		return al;
	}

	public ArrayList findAllByEntityName(int entityId, String digesterName) {

		this.unsetTypeExpected();

		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.TIMESTAMP);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.INT);
		this.setTypeExpected(5, TypeNames.INT);
		this.setTypeExpected(6, TypeNames.STRING);
		this.setTypeExpected(7, TypeNames.STRING); // action_message
		this.setTypeExpected(8, TypeNames.STRING); // old_value
		this.setTypeExpected(9, TypeNames.STRING); // new_value
		this.setTypeExpected(10, TypeNames.STRING); // column_name
		this.setTypeExpected(11, TypeNames.INT); // study_id
		this.setTypeExpected(12, TypeNames.INT); // subject_id
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(entityId));

		String sql = digester.getQuery(digesterName);
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		Iterator it = alist.iterator();
		AuditEventBean ebCheck = new AuditEventBean();
		HashMap AuditEventHashMap = new HashMap();
		AuditEventBean eb = new AuditEventBean();
		while (it.hasNext()) {
			HashMap nextEb = (HashMap) it.next();
			eb = (AuditEventBean) this.getEntityFromHashMap(nextEb, true, true, true);

			ebCheck = (AuditEventBean) AuditEventHashMap.get(new Integer(eb.getId()));
			if (ebCheck == null) {
				AuditEventHashMap.put(new Integer(eb.getId()), eb);
				// logger.warn("Put into hashmap: "+eb.getId());
			} else {
				HashMap changes = ebCheck.getChanges();
				changes.put(eb.getColumnName(), eb.getNewValue());
				ebCheck.setChanges(changes);
				AuditEventHashMap.put(new Integer(eb.getId()), ebCheck);
			}

		}// end of first iterator loop
		Set s = AuditEventHashMap.entrySet();
		Iterator sit = s.iterator();
		while (sit.hasNext()) {
			Map.Entry mentry = (Map.Entry) sit.next();
			AuditEventBean newAEBean = (AuditEventBean) mentry.getValue();
			newAEBean = this.setStudyAndSubjectInfo(newAEBean);
			// al.add(mentry.getValue());
			al.add(newAEBean);

		}// end of second iterator loop

		return al;
	}

	public ArrayList findAllByUserId(int userId) {
		/*
		 * select ae. , aev.old_value, aev.new_value, aev.column_name from audit_event ae, audit_event_values aev where
		 * ae.audit_id=aev.audit_id and ae.user_id=? order by ae.audit_date; NEWER QUERY : select ae. , aev.old_value,
		 * aev.new_value, aev.column_name, aec.study_id, aec.subject_id from audit_event ae, audit_event_values aev,
		 * audit_event_context aec where ae.audit_id=aev.audit_id and ae.audit_id = aec.audit_id and ae.user_id=? order
		 * by ae.audit_date
		 */
		this.unsetTypeExpected();

		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.TIMESTAMP);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.INT);
		this.setTypeExpected(5, TypeNames.INT);
		this.setTypeExpected(6, TypeNames.STRING);
		this.setTypeExpected(7, TypeNames.STRING); // action_message
		this.setTypeExpected(8, TypeNames.STRING); // old_value
		this.setTypeExpected(9, TypeNames.STRING); // new_value
		this.setTypeExpected(10, TypeNames.STRING); // column_name
		this.setTypeExpected(11, TypeNames.INT); // study_id
		this.setTypeExpected(12, TypeNames.INT); // subject_id
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(userId));

		String sql = digester.getQuery("findAllByUserId");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		Iterator it = alist.iterator();
		AuditEventBean ebCheck = new AuditEventBean();
		HashMap AuditEventHashMap = new HashMap();
		AuditEventBean eb = new AuditEventBean();
		while (it.hasNext()) {
			HashMap nextEb = (HashMap) it.next();
			eb = (AuditEventBean) this.getEntityFromHashMap(nextEb, true, true, true);
			// currently added here, but is there repeated work?
			// create a method instead to just add the names to the ids
			// found in the context, tbh
			ebCheck = (AuditEventBean) AuditEventHashMap.get(new Integer(eb.getId()));
			if (ebCheck == null) {
				AuditEventHashMap.put(new Integer(eb.getId()), eb);
				logger.warn("Put into hashmap: " + eb.getId());
			} else {
				HashMap changes = ebCheck.getChanges();
				changes.put(eb.getColumnName(), eb.getNewValue());
				ebCheck.setChanges(changes);
				AuditEventHashMap.put(new Integer(eb.getId()), ebCheck);
			}

		}
		Set s = AuditEventHashMap.entrySet();
		Iterator sit = s.iterator();
		while (sit.hasNext()) {
			Map.Entry mentry = (Map.Entry) sit.next();
			AuditEventBean newAEBean = (AuditEventBean) mentry.getValue();
			newAEBean = this.setStudyAndSubjectInfo(newAEBean);
			al.add(newAEBean);

		}

		return al;
	}

	public ArrayList findEventStatusLogByStudySubject(int studySubjectId) {

		/*
		 * select ae. , aev.old_value, aev.new_value from audit_event ae, audit_event_values aev, study_event se where
		 * ae.audit_table='STUDY_EVENT' and ae.audit_id=aev.audit_id and aev.column_name='Subject Event Status ID' and
		 * ae.entity_id=study_event.study_event_id and study_event.study_subject_id=?
		 */
		this.unsetTypeExpected();

		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.TIMESTAMP);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.INT);
		this.setTypeExpected(5, TypeNames.INT);
		this.setTypeExpected(6, TypeNames.STRING);
		this.setTypeExpected(7, TypeNames.STRING); // action_message
		this.setTypeExpected(8, TypeNames.STRING); // old_value
		this.setTypeExpected(9, TypeNames.STRING); // new_value
		this.setTypeExpected(10, TypeNames.STRING); // column name
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(studySubjectId));
		logger.warn("&&& querying study log...");
		String sql = digester.getQuery("findEventStatusLogByStudySubject");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		Iterator it = alist.iterator();
		logger.warn("&&& about to get entities from HM...");
		while (it.hasNext()) {
			AuditEventBean eb = (AuditEventBean) this.getEntityFromHashMap((HashMap) it.next(), true, true, false);
			al.add(eb);
		}
		logger.warn("&&& returning array list...");
		return al;

	}

	/**
	 * Updates a AuditEvent
	 */
	public EntityBean update(EntityBean eb) {
		AuditEventBean sb = (AuditEventBean) eb;
		return sb;
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		ArrayList al = new ArrayList();

		return al;
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		ArrayList al = new ArrayList();

		return al;
	}

}
