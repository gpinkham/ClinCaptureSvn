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

package org.akaza.openclinica.dao.submit;

import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.EventCRFSDVFilter;
import org.akaza.openclinica.dao.EventCRFSDVSort;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

import com.clinovo.jmesa.evaluation.CRFEvaluationFilter;
import com.clinovo.jmesa.evaluation.CRFEvaluationItem;
import com.clinovo.jmesa.evaluation.CRFEvaluationSort;

/**
 * <p/>
 * EventCRFDAO.java, data access object for an instance of an event being filled out on a subject. Was originally
 * individual_instrument table in OpenClinica v.1.
 * 
 * @author thickerson
 *         <p/>
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class EventCRFDAO extends AuditableEntityDAO {

	public static final int INT_25 = 25;

	private void setQueryNames() {
		this.findByPKAndStudyName = "findByPKAndStudy";
		this.getCurrentPKName = "getCurrentPK";
	}

	/**
	 * Event crf constructor.
	 * 
	 * @param ds
	 *            DataSource
	 */
	public EventCRFDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	/**
	 * Event crf constructor.
	 * 
	 * @param ds
	 *            DataSource
	 * @param con
	 *            Connection
	 */
	public EventCRFDAO(DataSource ds, Connection con) {
		super(ds, con);
		setQueryNames();
	}

	/**
	 * Event crf constructor.
	 * 
	 * @param ds
	 *            DataSource
	 * @param digester
	 *            DAODigester
	 */
	public EventCRFDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
		setQueryNames();
	}

	/**
	 * This constructor sets up the Locale for JUnit tests; see the locale member variable in EntityDAO, and its
	 * initializeI18nStrings() method.
	 * 
	 * @param ds
	 *            DataSource
	 * @param digester
	 *            DAODigester
	 * @param locale
	 *            Locale
	 */
	public EventCRFDAO(DataSource ds, DAODigester digester, Locale locale) {

		this(ds, digester);
		this.locale = locale;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_EVENTCRF;
	}

	@Override
	public void setTypesExpected() {
		int ind = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.STRING); // annotations
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP); // completed
		this.setTypeExpected(ind++, TypeNames.INT); // validator id
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP); // date validate
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP); // date val. completed
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.INT); // owner id
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP);
		this.setTypeExpected(ind++, TypeNames.INT); // subject id
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP); // date updated
		this.setTypeExpected(ind++, TypeNames.INT); // updater
		this.setTypeExpected(ind++, TypeNames.BOOL); // electronic_signature_status
		this.setTypeExpected(ind++, TypeNames.BOOL); // sdv_status
		this.setTypeExpected(ind++, TypeNames.INT); // old_status
		this.setTypeExpected(ind++, TypeNames.INT); // sdv_update_id
		this.setTypeExpected(ind, TypeNames.BOOL); // not_started

	}

	/**
	 * Method updates event crf.
	 * 
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean update(EntityBean eb) {
		return update(eb, null);
	}

	/**
	 * Method updates event crf.
	 * 
	 * @param eb
	 *            EntityBean
	 * @param con
	 *            Connection
	 * @return EntityBean
	 */
	public EntityBean update(EntityBean eb, Connection con) {
		EventCRFBean ecb = (EventCRFBean) eb;

		ecb.setActive(false);

		int ind = 1;
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		variables.put(ind++, ecb.getStudyEventId());
		variables.put(ind++, ecb.getCRFVersionId());
		if (ecb.getDateInterviewed() == null) {
			nullVars.put(ind, Types.TIMESTAMP);
			variables.put(ind++, null);
		} else {
			variables.put(ind++, new Timestamp(ecb.getDateInterviewed().getTime()));
		}
		variables.put(ind++, ecb.getInterviewerName());
		variables.put(ind++, ecb.getCompletionStatusId());
		variables.put(ind++, ecb.getStatus().getId());
		variables.put(ind++, ecb.getAnnotations());
		if (ecb.getDateCompleted() == null) {
			nullVars.put(ind, Types.TIMESTAMP);
			variables.put(ind++, null);
		} else {
			variables.put(ind++, new Timestamp(ecb.getDateCompleted().getTime()));
		}

		variables.put(ind++, ecb.getValidatorId());

		if (ecb.getDateValidate() == null) {
			nullVars.put(ind, Types.TIMESTAMP);
			variables.put(ind++, null);
		} else {
			variables.put(ind++, new Timestamp(ecb.getDateValidate().getTime()));
		}

		if (ecb.getDateValidateCompleted() == null) {
			nullVars.put(ind, Types.TIMESTAMP);
			variables.put(ind++, null);
		} else {
			variables.put(ind++, new Timestamp(ecb.getDateValidateCompleted().getTime()));
		}
		variables.put(ind++, ecb.getValidatorAnnotations());
		variables.put(ind++, ecb.getValidateString());
		variables.put(ind++, ecb.getStudySubjectId());
		variables.put(ind++, ecb.getUpdaterId());
		variables.put(ind++, ecb.isElectronicSignatureStatus());

		variables.put(ind++, ecb.isSdvStatus());
		if (ecb.getOldStatus() != null && ecb.getOldStatus().getId() > 0) {
			variables.put(ind++, ecb.getOldStatus().getId());
		} else {
			variables.put(ind++, 0);
		}
		variables.put(ind++, ecb.getSdvUpdateId());
		variables.put(ind++, ecb.isNotStarted());
		variables.put(ind++, ecb.getOwnerId());
		variables.put(ind, ecb.getId());
		this.execute(digester.getQuery("update"), variables, nullVars, con);
		if (isQuerySuccessful()) {
			ecb.setActive(true);
		}

		return ecb;
	}

	/**
	 * Updates EventCRFBean's status.
	 *
	 * @param eventCRFBean
	 *            EventCRFBean
	 */
	public void updateStatus(EventCRFBean eventCRFBean) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, eventCRFBean.getStatus().getId());
		variables.put(ind++, eventCRFBean.getUpdaterId());
		variables.put(ind, eventCRFBean.getId());
		this.execute(digester.getQuery("updateStatus"), variables);
	}

	/**
	 * Method that marks event crf complete.
	 * 
	 * @param ecb
	 *            EventCRFBean
	 * @param ide
	 *            boolean
	 */
	public void markComplete(EventCRFBean ecb, boolean ide) {
		markComplete(ecb, ide, null);
	}

	/**
	 * Method that marks event crf complete.
	 * 
	 * @param ecb
	 *            EventCRFBean
	 * @param ide
	 *            boolean
	 * @param con
	 *            Connection
	 */
	public void markComplete(EventCRFBean ecb, boolean ide, Connection con) {
		HashMap variables = new HashMap();
		variables.put(1, ecb.getId());

		if (ide) {
			execute(digester.getQuery("markCompleteIDE"), variables, con);
		} else {
			execute(digester.getQuery("markCompleteDDE"), variables, con);
		}
	}

	/**
	 * Method creates event crf.
	 * 
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean create(EntityBean eb) {
		EventCRFBean ecb = (EventCRFBean) eb;
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		int ind = 1;
		variables.put(ind++, ecb.getStudyEventId());
		variables.put(ind++, ecb.getCRFVersionId());

		Date interviewed = ecb.getDateInterviewed();
		if (interviewed != null) {
			variables.put(ind++, new Timestamp(ecb.getDateInterviewed().getTime()));
		} else {
			variables.put(ind, null);
			nullVars.put(ind++, Types.TIMESTAMP);
		}
		logger.info("created: ecb.getInterviewerName()" + ecb.getInterviewerName());
		variables.put(ind++, ecb.getInterviewerName());

		variables.put(ind++, ecb.getCompletionStatusId());
		variables.put(ind++, ecb.getStatus().getId());
		variables.put(ind++, ecb.getAnnotations());
		variables.put(ind++, ecb.getOwnerId());
		variables.put(ind++, ecb.getStudySubjectId());
		variables.put(ind++, ecb.getValidateString());
		variables.put(ind++, ecb.getValidatorAnnotations());
		variables.put(ind, ecb.isNotStarted());

		executeWithPK(digester.getQuery("create"), variables, nullVars);
		if (isQuerySuccessful()) {
			ecb.setId(getLatestPK());
		}

		return ecb;
	}

	/**
	 * This method is used to get values from HashMap and create <code>EventCRFBean</code>.
	 * 
	 * @param hm
	 *            HashMap
	 * @return Object
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		EventCRFBean eb = new EventCRFBean();
		this.setEntityAuditInformation(eb, hm);

		eb.setId((Integer) hm.get("event_crf_id"));
		eb.setStudyEventId((Integer) hm.get("study_event_id"));
		eb.setCRFVersionId((Integer) hm.get("crf_version_id"));
		eb.setDateInterviewed((Date) hm.get("date_interviewed"));
		eb.setInterviewerName((String) hm.get("interviewer_name"));
		eb.setCompletionStatusId((Integer) hm.get("completion_status_id"));
		eb.setAnnotations((String) hm.get("annotations"));
		eb.setDateCompleted((Date) hm.get("date_completed"));
		eb.setValidatorId((Integer) hm.get("validator_id"));
		eb.setDateValidate((Date) hm.get("date_validate"));
		eb.setDateValidateCompleted((Date) hm.get("date_validate_completed"));
		eb.setValidatorAnnotations((String) hm.get("validator_annotations"));
		eb.setValidateString((String) hm.get("validate_string"));
		eb.setStudySubjectId((Integer) hm.get("study_subject_id"));
		eb.setSdvStatus((Boolean) hm.get("sdv_status"));
		Integer oldStatusId = (Integer) hm.get("old_status_id");
		eb.setOldStatus(Status.get(oldStatusId));
		eb.setNotStarted((Boolean) hm.get("not_started"));

		return eb;
	}

	/**
	 * Method returns all event crfs.
	 * 
	 * @return Collection
	 */
	public Collection findAll() {
		this.setTypesExpected();
		ArrayList alist = this.select(digester.getQuery("findAll"));
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			EventCRFBean eb = (EventCRFBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method returns all event crfs.
	 * 
	 * @param strOrderByColumn
	 *            String
	 * @param blnAscendingSort
	 *            boolean
	 * @param strSearchPhrase
	 *            String
	 * @return Collection
	 */
	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * Method returns event crf by id.
	 * 
	 * @param id
	 *            int
	 * @return EntityBean
	 */
	public EntityBean findByPK(int id) {
		EventCRFBean eb = new EventCRFBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (EventCRFBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return eb;
	}

	/**
	 * Method returns event crfs by permission.
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
	 * @return Collection
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * Method returns event crfs by permission.
	 * 
	 * @param objCurrentUser
	 *            Object
	 * @param intActionType
	 *            int
	 * @return Collection
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	/**
	 * Method returns all event crfs by study event.
	 * 
	 * @param studyEvent
	 *            StudyEventBean
	 * @return ArrayList
	 */
	public ArrayList findAllByStudyEvent(StudyEventBean studyEvent) {
		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());

		return executeFindAllQuery("findAllByStudyEvent", variables);
	}

	/**
	 * Find all started event crfs by crf id.
	 * 
	 * @param crfId
	 *            crf id
	 * @return List<EventCRFBean>
	 */
	public List<EventCRFBean> findAllStartedByCrf(int crfId) {
		List<EventCRFBean> result = new ArrayList<EventCRFBean>();
		this.setTypesExpected();
		int ind = INT_25;
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind, TypeNames.STRING);

		HashMap variables = new HashMap();
		variables.put(1, crfId);

		ArrayList alist = this.select(digester.getQuery("findAllStartedByCrf"), variables);

		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			EventCRFBean eb = (EventCRFBean) this.getEntityFromHashMap(hm);
			eb.setStudySubjectName((String) hm.get("label"));
			eb.setEventName((String) hm.get("sed_name"));
			result.add(eb);
		}
		return result;
	}

	/**
	 * Find all started event crfs by crf version id.
	 * 
	 * @param crfVersionId
	 *            crf version id
	 * @return List<EventCRFBean>
	 */
	public List<EventCRFBean> findAllStartedByCrfVersion(int crfVersionId) {
		List<EventCRFBean> result = new ArrayList<EventCRFBean>();
		this.setTypesExpected();
		int ind = INT_25;
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind, TypeNames.STRING);

		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);

		ArrayList alist = this.select(digester.getQuery("findAllStartedByCrfVersion"), variables);

		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			EventCRFBean eb = (EventCRFBean) this.getEntityFromHashMap(hm);
			eb.setStudySubjectName((String) hm.get("label"));
			eb.setEventName((String) hm.get("sed_name"));
			result.add(eb);
		}
		return result;
	}

	/**
	 * Method returns all started event crfs by study event.
	 * 
	 * @param studyEvent
	 *            StudyEventBean
	 * @return ArrayList
	 */
	public ArrayList findAllStartedByStudyEvent(StudyEventBean studyEvent) {
		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());

		return executeFindAllQuery("findAllStartedByStudyEvent", variables);
	}

	/**
	 * Method returns all event crfs by study event & status.
	 * 
	 * @param studyEvent
	 *            StudyEventBean
	 * @param status
	 *            Status
	 * @return ArrayList
	 */
	public ArrayList findAllByStudyEventAndStatus(StudyEventBean studyEvent, Status status) {
		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());
		variables.put(2, status.getId());
		return executeFindAllQuery("findAllByStudyEventAndStatus", variables);
	}

	/**
	 * Method returns all event crfs by study subject id.
	 * 
	 * @param studySubjectId
	 *            int
	 * @return ArrayList<EventCRFBean>
	 */
	public ArrayList<EventCRFBean> findAllByStudySubject(int studySubjectId) {
		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);

		return executeFindAllQuery("findAllByStudySubject", variables);
	}

	/**
	 * Method returns all event crfs by study event & crf version or crf OID.
	 * 
	 * @param studyEvent
	 *            StudyEventBean
	 * @param crfVersionOrCrfOID
	 *            String
	 * @return ArrayList
	 */
	public ArrayList findAllByStudyEventAndCrfOrCrfVersionOid(StudyEventBean studyEvent, String crfVersionOrCrfOID) {
		HashMap variables = new HashMap();
		int ind = 1;
		variables.put(ind++, studyEvent.getId());
		variables.put(ind++, crfVersionOrCrfOID);
		variables.put(ind, crfVersionOrCrfOID);

		return executeFindAllQuery("findAllByStudyEventAndCrfOrCrfVersionOid", variables);
	}

	/**
	 * Method returns all event crfs by crf id.
	 * 
	 * @param crfId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllByCRF(int crfId) {
		HashMap variables = new HashMap();
		variables.put(1, crfId);

		return executeFindAllQuery("findAllByCRF", variables);
	}

	/**
	 * Method returns all event crfs by crf version id.
	 * 
	 * @param crfVersionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllByCRFVersion(int crfVersionId) {
		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);

		return executeFindAllQuery("findAllByCRFVersion", variables);
	}

	/**
	 * Method returns all event crfs with study subjects by crf version id.
	 * 
	 * @param crfVersionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllStudySubjectByCRFVersion(int crfVersionId) {
		this.setTypesExpected();
		int ind = INT_25;
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind, TypeNames.STRING);
		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			ind = INT_25;
			this.setTypeExpected(ind++, TypeNames.STRING); // r
			this.setTypeExpected(ind++, TypeNames.STRING); // r
			this.setTypeExpected(ind, TypeNames.STRING); // r
		}
		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);

		ArrayList alist = this.select(digester.getQuery("findAllStudySubjectByCRFVersion"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			EventCRFBean eb = (EventCRFBean) this.getEntityFromHashMap(hm);
			eb.setStudySubjectName((String) hm.get("label"));
			eb.setEventName((String) hm.get("sed_name"));
			eb.setStudyName((String) hm.get("study_name"));
			al.add(eb);
		}
		return al;

	}

	/**
	 * Method returns undeleted event crfs with study subjects by crf version id.
	 * 
	 * @param crfVersionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findUndeletedWithStudySubjectsByCRFVersion(int crfVersionId) {
		this.setTypesExpected();
		int ind = INT_25;
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);

		ArrayList alist = this.select(digester.getQuery("findUndeletedWithStudySubjectsByCRFVersion"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			EventCRFBean eb = (EventCRFBean) this.getEntityFromHashMap(hm);
			eb.setStudySubjectName((String) hm.get("label"));
			eb.setEventName((String) hm.get("sed_name"));
			eb.setStudyName((String) hm.get("study_name"));
			eb.setEventOrdinal((Integer) hm.get("repeat_number"));
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method returns list of event crfs by study event, study subject & crf version.
	 * 
	 * @param studyEvent
	 *            StudyEventBean
	 * @param studySubject
	 *            StudySubjectBean
	 * @param crfVersion
	 *            CRFVersionBean
	 * @return ArrayList
	 */
	public ArrayList findByEventSubjectVersion(StudyEventBean studyEvent, StudySubjectBean studySubject,
			CRFVersionBean crfVersion) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyEvent.getId());
		variables.put(ind++, crfVersion.getId());
		variables.put(ind, studySubject.getId());

		return executeFindAllQuery("findByEventSubjectVersion", variables);
	}

	/**
	 * Finds all active event crfs by study event id and crf id.
	 * 
	 * @param studyEventId
	 *            Study Event ID
	 * @param crfId
	 *            CRF ID
	 * @return List of active EventCRFs filtered by Study Event ID and CRF ID
	 */
	public ArrayList findAllActiveByStudyEventIdAndCrfId(int studyEventId, int crfId) {
		HashMap variables = new HashMap();
		variables.put(1, studyEventId);
		variables.put(2, crfId);

		return executeFindAllQuery("findAllActiveByStudyEventIdAndCrfId", variables);
	}

	/**
	 * Method returns the event crf by study event & crf version.
	 * 
	 * @param studyEvent
	 *            StudyEventBean
	 * @param crfVersion
	 *            CRFVersionBean
	 * @return EventCRFBean
	 */
	public EventCRFBean findByEventCrfVersion(StudyEventBean studyEvent, CRFVersionBean crfVersion) {
		EventCRFBean eventCrfBean = null;
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		int ind = 1;
		variables.put(ind++, studyEvent.getId());
		variables.put(ind, crfVersion.getId());

		ArrayList<EventCRFBean> eventCrfs = executeFindAllQuery("findByEventCrfVersion", variables);
		if (!eventCrfs.isEmpty() && eventCrfs.size() == 1) {
			eventCrfBean = eventCrfs.get(0);
		}
		return eventCrfBean;

	}

	/**
	 * Method deletes event crf by id.
	 * 
	 * @param eventCRFId
	 *            int
	 */
	public void delete(int eventCRFId) {
		HashMap variables = new HashMap();
		variables.put(1, eventCRFId);
		this.execute(digester.getQuery("delete"), variables);
	}

	/**
	 * Method sets sdv status.
	 * 
	 * @param sdvStatus
	 *            boolean
	 * @param userId
	 *            int
	 * @param eventCRFId
	 *            int
	 */
	public void setSDVStatus(boolean sdvStatus, int userId, int eventCRFId) {
		HashMap variables = new HashMap();
		int ind = 1;
		variables.put(ind++, sdvStatus);
		variables.put(ind++, userId);
		variables.put(ind, eventCRFId);

		this.execute(digester.getQuery("setSDVStatus"), variables);
	}

	/**
	 * Method returns collection of SDVed event crfs by study and year.
	 * 
	 * @param study
	 *            StudyBean
	 * @param year
	 *            int
	 * @return Collection
	 */
	public Collection findSDVedEventCRFsByStudyAndYear(StudyBean study, int year) {

		HashMap variables = new HashMap();
		int ind = 1;
		variables.put(ind++, study.getId());
		variables.put(ind++, study.getId());
		variables.put(ind, year);

		return executeFindAllQuery("findSDVedEventCRFsByStudyAndYear", variables);
	}

	/**
	 * Method returns count of event crfs by study & parent study id.
	 * 
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @return Integer
	 */
	public Integer countEventCRFsByStudy(int studyId, int parentStudyId) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyId);
		variables.put(ind, parentStudyId);
		String sql = digester.getQuery("countEventCRFsByStudy");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	/**
	 * Method returns count of event crfs by study identifier.
	 * 
	 * @param identifier
	 *            String
	 * @return Integer
	 */
	public Integer countEventCRFsByStudyIdentifier(String identifier) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, identifier);
		String sql = digester.getQuery("countEventCRFsByStudyIdentifier");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	/**
	 * Method returns count of event crfs.
	 * 
	 * @param studySubjectId
	 *            int
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @return Integer
	 */
	public Integer countEventCRFsByStudySubject(int studySubjectId, int studyId, int parentStudyId) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studySubjectId);
		variables.put(ind++, studyId);
		variables.put(ind, parentStudyId);
		String sql = digester.getQuery("countEventCRFsByStudySubject");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	/**
	 * Method returns count of event crfs.
	 * 
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @param studyIdentifier
	 *            String
	 * @return Integer
	 */
	public Integer countEventCRFsByStudyIdentifier(int studyId, int parentStudyId, String studyIdentifier) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyId);
		variables.put(ind++, parentStudyId);
		variables.put(ind, studyIdentifier);
		String sql = digester.getQuery("countEventCRFsByStudyIdentifier");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	/**
	 * Method returns count of completed or locked event crfs and that are not SDVed.
	 * 
	 * @param studySubjectId
	 *            int
	 * @return Integer
	 */
	public Integer countEventCRFsByByStudySubjectCompleteOrLockedAndNotSDVd(int studySubjectId) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		String sql = digester.getQuery("countEventCRFsByByStudySubjectCompleteOrLockedAndNotSDVd");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	/**
	 * Method returns completed or locked event crfs by study subject id.
	 * 
	 * @param studySubjectId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsByStudySubjectCompleteOrLocked(int studySubjectId) {

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);

		return executeFindAllQuery("getEventCRFsByStudySubjectCompleteOrLocked", variables);
	}

	/**
	 * Method returns event crfs by study subject id.
	 * 
	 * @param studySubjectId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsByStudySubjectExceptInvalid(int studySubjectId) {

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);

		return executeFindAllQuery("getEventCRFsByStudySubjectExceptInvalid", variables);
	}

	/**
	 * Method returns event crfs.
	 * 
	 * @param studySubjectId
	 *            int
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @param limit
	 *            int
	 * @param offset
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsByStudySubjectLimit(int studySubjectId, int studyId, int parentStudyId, int limit,
			int offset) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studySubjectId);
		variables.put(ind++, studyId);
		variables.put(ind++, parentStudyId);
		variables.put(ind++, limit);
		variables.put(ind, offset);

		return executeFindAllQuery("getEventCRFsByStudySubjectLimit", variables);
	}

	/**
	 * Method returns event crfs.
	 * 
	 * @param studySubjectId
	 *            int
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsByStudySubject(int studySubjectId, int studyId, int parentStudyId) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studySubjectId);
		variables.put(ind++, studyId);
		variables.put(ind, parentStudyId);

		return executeFindAllQuery("getEventCRFsByStudySubject", variables);
	}

	/**
	 * Method returns event crfs.
	 * 
	 * @param studySubjectId
	 *            int
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsWithNonLockedCRFsByStudySubject(int studySubjectId, int studyId, int parentStudyId) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studySubjectId);
		variables.put(ind++, studyId);
		variables.put(ind, parentStudyId);

		return executeFindAllQuery("getEventCRFsWithNonLockedCRFsByStudySubject", variables);
	}

	/**
	 * Method returns group.
	 * 
	 * @param studySubjectId
	 *            int
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getGroupByStudySubject(int studySubjectId, int studyId, int parentStudyId) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studySubjectId);
		variables.put(ind++, studyId);
		variables.put(ind, parentStudyId);

		return executeFindAllQuery("getGroupByStudySubject", variables);
	}

	/**
	 * Method returns event crfs.
	 * 
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @param studyIdentifier
	 *            String
	 * @param limit
	 *            int
	 * @param offset
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsByStudyIdentifier(int studyId, int parentStudyId, String studyIdentifier, int limit,
			int offset) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyId);
		variables.put(ind++, parentStudyId);
		variables.put(ind++, studyIdentifier);
		variables.put(ind++, limit);
		variables.put(ind, offset);

		return executeFindAllQuery("getEventCRFsByStudyIdentifier", variables);
	}

	/**
	 * Method returns count of event crfs.
	 * 
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @param filter
	 *            EventCRFSDVFilter
	 * @return Integer
	 */
	public Integer getCountWithFilter(int studyId, int parentStudyId, EventCRFSDVFilter filter) {
		int ind = 1;
		setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(ind++, studyId);
		variables.put(ind, parentStudyId);
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
	 * Method returns count of available event crfs.
	 * 
	 * @param studyId
	 *            int
	 * @param filter
	 *            EventCRFSDVFilter
	 * @param allowSdvWithOpenQueries
	 *            boolean
	 * @param userId
	 *            int
	 * @return Integer
	 */
	public Integer getCountOfAvailableWithFilter(int studyId, EventCRFSDVFilter filter, boolean allowSdvWithOpenQueries,
			int userId) {
		setTypesExpected();
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyId);
		variables.put(ind, studyId);
		String sql = digester.getQuery("getCountOfAvailableWithFilter");
		sql += EventCRFSDVFilter.getMaskedCRFsFilter(userId);
		sql += filter.execute("");
		if (!allowSdvWithOpenQueries) {
			sql = sql + digester.getQuery("notAllowSdvWithOpenQueries");
		}
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Method returns list of event crfs.
	 * 
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @param filter
	 *            EventCRFSDVFilter
	 * @param sort
	 *            EventCRFSDVSort
	 * @param rowStart
	 *            int
	 * @param rowEnd
	 *            int
	 * @return ArrayList<EventCRFBean>
	 */
	public ArrayList<EventCRFBean> getWithFilterAndSort(int studyId, int parentStudyId, EventCRFSDVFilter filter,
			EventCRFSDVSort sort, int rowStart, int rowEnd) {
		ArrayList<EventCRFBean> eventCRFs = new ArrayList<EventCRFBean>();
		setTypesExpected();
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyId);
		variables.put(ind, parentStudyId);
		String sql = digester.getQuery("getWithFilterAndSort");
		sql = sql + filter.execute("");
		sql = sql + " order By  ec.date_created ASC "; // major hack
		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			sql += " )x)where r between " + (rowStart + 1) + " and " + rowEnd;
		} else {
			sql = sql + " LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
		}
		ArrayList rows = this.select(sql, variables);

		for (Object row : rows) {
			EventCRFBean eventCRF = (EventCRFBean) this.getEntityFromHashMap((HashMap) row);
			eventCRFs.add(eventCRF);
		}
		return eventCRFs;
	}

	/**
	 * Method returns list of available event crfs.
	 * 
	 * @param studyId
	 *            int
	 * @param filter
	 *            EventCRFSDVFilter
	 * @param sort
	 *            EventCRFSDVSort
	 * @param allowSdvWithOpenQueries
	 *            boolean
	 * @param rowStart
	 *            int
	 * @param rowEnd
	 *            int
	 * @param userId
	 *            int can be 0.
	 * @return ArrayList<EventCRFBean>
	 */
	public ArrayList<EventCRFBean> getAvailableWithFilterAndSort(int studyId, EventCRFSDVFilter filter,
			EventCRFSDVSort sort, boolean allowSdvWithOpenQueries, int rowStart, int rowEnd, int userId) {
		ArrayList<EventCRFBean> eventCRFs = new ArrayList<EventCRFBean>();
		setTypesExpected();

		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyId);
		variables.put(ind, studyId);
		String sql = digester.getQuery("getAvailableWithFilterAndSort");
		sql += EventCRFSDVFilter.getMaskedCRFsFilter(userId);
		sql += filter.execute("");

		if (!allowSdvWithOpenQueries) {
			sql = sql + digester.getQuery("notAllowSdvWithOpenQueries");
		}

		String sortQuery = sort.execute("");
		if (sortQuery.isEmpty()) {
			sql = sql + " order by ec.study_event_id asc";
		} else {
			sql = sql + sortQuery;
		}

		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			sql += " )x)where r between " + (rowStart + 1) + " and " + rowEnd;
		} else {
			sql = sql + " LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
		}

		ArrayList rows = this.select(sql, variables);

		for (Object row : rows) {
			EventCRFBean eventCRF = (EventCRFBean) this.getEntityFromHashMap((HashMap) row);
			eventCRFs.add(eventCRF);
		}
		return eventCRFs;
	}

	/**
	 * Method returns list of entities that are available for SDV.
	 * 
	 * @param studyId
	 *            int
	 * @param entityProperty
	 *            String
	 * @return List<String>
	 */
	public List<String> getAvailableForSDVEntitiesByStudyId(int studyId, String entityProperty) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.STRING);

		List<String> result = new ArrayList<String>();

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);
		String sql = digester.getQuery("getAvailableForSDVEntitiesByStudyId").replaceFirst("entity", entityProperty);
		ArrayList rows = this.select(sql, variables);

		for (Object row : rows) {
			result.add((String) ((HashMap) row).get("property"));
		}
		return result;
	}

	/**
	 * Method returns list of crf names that are available for SDV.
	 * 
	 * @param studyId
	 *            int
	 * @return List<String>
	 */
	public List<String> getAvailableForSDVCRFNamesByStudyId(int studyId) {
		return getAvailableForSDVEntitiesByStudyId(studyId, "crf.name");
	}

	/**
	 * Method returns list of site names that are available for SDV.
	 * 
	 * @param studyId
	 *            int
	 * @return List<String>
	 */
	public List<String> getAvailableForSDVSiteNamesByStudyId(int studyId) {
		return getAvailableForSDVEntitiesByStudyId(studyId, "s.unique_identifier");
	}

	/**
	 * Method returns list of event names that are available for SDV.
	 * 
	 * @param studyId
	 *            int
	 * @return List<String>
	 */
	public List<String> getAvailableForSDVEventNamesByStudyId(int studyId) {
		return getAvailableForSDVEntitiesByStudyId(studyId, "sed.name");
	}

	/**
	 * Method returns event crfs.
	 * 
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @param limit
	 *            int
	 * @param offset
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsByStudy(int studyId, int parentStudyId, int limit, int offset) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyId);
		variables.put(ind++, parentStudyId);
		variables.put(ind++, limit);
		variables.put(ind, offset);

		return executeFindAllQuery("getEventCRFsByStudy", variables);
	}

	/**
	 * Method returns event crfs.
	 * 
	 * @param label
	 *            String
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @param limit
	 *            int
	 * @param offset
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsByStudySubjectLabelLimit(String label, int studyId, int parentStudyId, int limit,
			int offset) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, '%' + label + '%');
		variables.put(ind++, studyId);
		variables.put(ind++, parentStudyId);
		variables.put(ind++, limit);
		variables.put(ind, offset);

		return executeFindAllQuery("getEventCRFsByStudySubjectLabelLimit", variables);
	}

	/**
	 * Method returns event crfs.
	 * 
	 * @param eventName
	 *            String
	 * @param limit
	 *            int
	 * @param offset
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsByEventNameLimit(String eventName, int limit, int offset) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, eventName);
		variables.put(ind++, limit);
		variables.put(ind, offset);

		return executeFindAllQuery("getEventCRFsByEventNameLimit", variables);
	}

	/**
	 * Method returns event crfs.
	 * 
	 * @param studyId
	 *            int
	 * @param eventDate
	 *            String
	 * @param limit
	 *            int
	 * @param offset
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsByEventDateLimit(int studyId, String eventDate, int limit, int offset) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyId);
		variables.put(ind++, eventDate);
		variables.put(ind++, limit);
		variables.put(ind, offset);

		return executeFindAllQuery("getEventCRFsByEventDateLimit", variables);
	}

	/**
	 * Method returns event crfs by study sdv.
	 * 
	 * @param studyId
	 *            int
	 * @param sdvStatus
	 *            int
	 * @param limit
	 *            int
	 * @param offset
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsByStudySDV(int studyId, boolean sdvStatus, int limit, int offset) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyId);
		variables.put(ind++, sdvStatus);
		variables.put(ind++, limit);
		variables.put(ind, offset);

		return executeFindAllQuery("getEventCRFsByStudySDV", variables);
	}

	/**
	 * Method returns event crfs by crf status.
	 * 
	 * @param studyId
	 *            int
	 * @param subjectEventStatusId
	 *            int
	 * @param limit
	 *            int
	 * @param offset
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsByCRFStatus(int studyId, int subjectEventStatusId, int limit, int offset) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyId);
		variables.put(ind++, subjectEventStatusId);
		variables.put(ind++, limit);
		variables.put(ind, offset);

		return executeFindAllQuery("getEventCRFsByCRFStatus", variables);
	}

	/**
	 * Method returns event crfs by sdv requirement.
	 * 
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @param limit
	 *            int
	 * @param offset
	 *            int
	 * @param sdvCode
	 *            Integer...
	 * @return ArrayList
	 */
	public ArrayList getEventCRFsBySDVRequirement(int studyId, int parentStudyId, int limit, int offset,
			Integer... sdvCode) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyId);
		variables.put(ind, parentStudyId);
		this.setTypesExpected();

		String sql = digester.getQuery("getEventCRFsBySDVRequirement");
		sql += " AND ( ";
		for (int i = 0; i < sdvCode.length; i++) {
			sql += i != 0 ? " OR " : "";
			sql += " source_data_verification_code = " + sdvCode[i];
		}
		sql += " ) ))  limit " + limit + " offset " + offset;

		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();

		for (Object anAlist : alist) {
			EventCRFBean eb = (EventCRFBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method returns count of event crfs.
	 * 
	 * @param label
	 *            String
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @return Integer
	 */
	public Integer countEventCRFsByStudySubjectLabel(String label, int studyId, int parentStudyId) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, label);
		variables.put(ind++, studyId);
		variables.put(ind, parentStudyId);

		String sql = digester.getQuery("countEventCRFsByStudySubjectLabel");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	/**
	 * Method returns count of event crfs by study sdv.
	 * 
	 * @param studyId
	 *            int
	 * @param sdvStatus
	 *            boolean
	 * @return Integer
	 */
	public Integer countEventCRFsByStudySDV(int studyId, boolean sdvStatus) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, sdvStatus);
		String sql = digester.getQuery("countEventCRFsByStudySDV");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	/**
	 * Method returns count of event crfs by crf status.
	 * 
	 * @param studyId
	 *            int
	 * @param statusId
	 *            int
	 * @return Integer
	 */
	public Integer countEventCRFsByCRFStatus(int studyId, int statusId) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, statusId);
		String sql = digester.getQuery("countEventCRFsByCRFStatus");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	/**
	 * Method returns count of event crfs by event name.
	 * 
	 * @param eventName
	 *            String
	 * @return Integer
	 */
	public Integer countEventCRFsByEventName(String eventName) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, eventName);
		String sql = digester.getQuery("countEventCRFsByEventName");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	/**
	 * Method returns count of event crfs by sdv requirement.
	 * 
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @param sdvCode
	 *            Integer...
	 * @return Integer
	 */
	public Integer countEventCRFsBySDVRequirement(int studyId, int parentStudyId, Integer... sdvCode) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);
		String sql = digester.getQuery("countEventCRFsBySDVRequirement");
		sql += " AND ( ";
		for (int i = 0; i < sdvCode.length; i++) {
			sql += i != 0 ? " OR " : "";
			sql += " source_data_verification_code = " + sdvCode[i];
		}
		sql += "))) ";

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	/**
	 * Method returns count of event crfs by event name & subject label.
	 * 
	 * @param eventName
	 *            String
	 * @param subjectLabel
	 *            String
	 * @return Integer
	 */
	public Integer countEventCRFsByEventNameSubjectLabel(String eventName, String subjectLabel) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, eventName);
		variables.put(2, subjectLabel);
		String sql = digester.getQuery("countEventCRFsByEventNameSubjectLabel");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	/**
	 * Method returns count of event crfs by event date.
	 * 
	 * @param studyId
	 *            int
	 * @param eventDate
	 *            String
	 * @return Integer
	 */
	public Integer countEventCRFsByEventDate(int studyId, String eventDate) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, eventDate);
		String sql = digester.getQuery("countEventCRFsByEventDate");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	/**
	 * Method builds event crf list by study event.
	 * 
	 * @param studySubjectId
	 *            Integer
	 * @return Map
	 */
	public Map<Integer, SortedSet<EventCRFBean>> buildEventCrfListByStudyEvent(Integer studySubjectId) {
		this.setTypesExpected(); // <== Must be called first

		Map<Integer, SortedSet<EventCRFBean>> result = new HashMap<Integer, SortedSet<EventCRFBean>>();

		HashMap<Integer, Object> param = new HashMap<Integer, Object>();
		param.put(1, studySubjectId);

		List selectResult = select(digester.getQuery("buildEventCrfListByStudyEvent"), param);

		for (Object aSelectResult : selectResult) {
			EventCRFBean bean = (EventCRFBean) this.getEntityFromHashMap((HashMap) aSelectResult);

			Integer studyEventId = bean.getStudyEventId();
			if (!result.containsKey(studyEventId)) {
				result.put(studyEventId, new TreeSet<EventCRFBean>(new Comparator<EventCRFBean>() {
					public int compare(EventCRFBean o1, EventCRFBean o2) {
						Integer id1 = o1.getId();
						Integer id2 = o2.getId();
						return id1.compareTo(id2);
					}
				}));
			}
			result.get(studyEventId).add(bean);
		}

		return result;
	}

	/**
	 * Method builds non empty event crf ids.
	 * 
	 * @param studySubjectId
	 *            Integer
	 * @return Set<Integer>
	 */
	public Set<Integer> buildNonEmptyEventCrfIds(Integer studySubjectId) {
		Set<Integer> result = new HashSet<Integer>();

		HashMap<Integer, Object> param = new HashMap<Integer, Object>();
		param.put(1, studySubjectId);

		List selectResult = select(digester.getQuery("buildNonEmptyEventCrfIds"), param);

		for (Object aSelectResult : selectResult) {
			HashMap hm = (HashMap) aSelectResult;
			result.add((Integer) hm.get("event_crf_id"));
		}

		return result;
	}

	/**
	 * Method updates crf version id.
	 * 
	 * @param eventCrfId
	 *            int
	 * @param crfVersionId
	 *            int
	 * @param userId
	 *            int
	 */
	public void updateCRFVersionID(int eventCrfId, int crfVersionId, int userId) {
		updateCRFVersionID(eventCrfId, crfVersionId, userId, null);
	}

	/**
	 * Method updates crf version id.
	 * 
	 * @param eventCrfId
	 *            int
	 * @param crfVersionId
	 *            int
	 * @param userId
	 *            int
	 * @param con
	 *            Connection
	 */
	public void updateCRFVersionID(int eventCrfId, int crfVersionId, int userId, Connection con) {
		int ind = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.BOOL);
		this.setTypeExpected(ind, TypeNames.INT);

		ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, crfVersionId);
		variables.put(ind++, userId);
		variables.put(ind++, userId);
		variables.put(ind++, false);
		variables.put(ind, eventCrfId);
		String sql = digester.getQuery("updateCRFVersionID");
		// this is the way to make the change transactional
		if (con == null) {
			this.execute(sql, variables);
		} else {
			this.execute(sql, variables, con);
		}
	}

	/**
	 * Method deletes event crf dn map by event crf id.
	 * 
	 * @param eventCRFId
	 *            int
	 */
	public void deleteEventCRFDNMap(int eventCRFId) {
		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(1, eventCRFId);
		this.execute(digester.getQuery("deleteEventCRFDNMap"), variables);
	}

	/**
	 * Method returns all ids with required SDV codes by study subject id.
	 * 
	 * @param studySujectId
	 *            int
	 * @param userId
	 *            int
	 * @return ArrayList<Integer>
	 */
	public ArrayList<Integer> findAllIdsWithRequiredSDVCodesBySSubjectId(int studySujectId, int userId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studySujectId);
		variables.put(ind++, 1);
		variables.put(ind++, 2);
		variables.put(ind++, 1);
		variables.put(ind, 2);
		String sql = digester.getQuery("findAllIdsWithSDVCodesBySSubjectId");
		sql += EventCRFSDVFilter.getMaskedCRFsFilterWithEDC(userId);
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			al.add(hm.get("event_crf_id"));
		}
		return al;
	}

	/**
	 * Method returns count of all event crfs for evaluation.
	 * 
	 * @param filter
	 *            CRFEvaluationFilter
	 * @param currentStudy
	 *            StudyBean
	 * @return int
	 */
	public int countOfAllEventCrfsForEvaluation(CRFEvaluationFilter filter, StudyBean currentStudy) {
		int index = 1;
		int result = 0;
		this.unsetTypeExpected();
		this.setTypeExpected(index, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(index++, currentStudy.getId());
		variables.put(index, currentStudy.getId());
		String sql = digester.getQuery("countOfAllEventCrfsForEvaluation");
		sql = sql.concat(filter.execute(""));

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			result = (Integer) ((HashMap) it.next()).get("total");
		}

		return result;
	}

	/**
	 * Method returns all event crfs for evaluation.
	 * 
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            CRFEvaluationFilter
	 * @param sort
	 *            CRFEvaluationSort
	 * @param rowStart
	 *            int
	 * @param rowEnd
	 *            int
	 * @return List<CRFEvaluationItem>
	 */
	public List<CRFEvaluationItem> findAllEventCrfsForEvaluation(StudyBean currentStudy, CRFEvaluationFilter filter,
			CRFEvaluationSort sort, int rowStart, int rowEnd) {
		List<CRFEvaluationItem> result = new ArrayList<CRFEvaluationItem>();

		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.BOOL);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index, TypeNames.TIMESTAMP);

		index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, currentStudy.getId());
		variables.put(index, currentStudy.getId());

		String sql = digester.getQuery("findAllEventCrfsForEvaluation");
		sql = sql.concat(filter.execute(""));

		String partialSql = sort.execute("");
		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			if (partialSql.equals("")) {
				sql = sql.concat(" ORDER BY ec.date_created )x)where r between ").concat(Integer.toString(rowStart + 1))
						.concat(" and ").concat(Integer.toString(rowEnd));
			} else {
				sql = sql.concat(")x)where r between ").concat(Integer.toString(rowStart + 1)).concat(" and ")
						.concat(Integer.toString(rowEnd)).concat(" ").concat(partialSql);
			}
		} else {
			sql = sql.concat(partialSql);
			if (partialSql.equals("")) {
				sql = sql.concat(" ORDER BY ec.date_created LIMIT ").concat(Integer.toString(rowEnd - rowStart))
						.concat(" OFFSET ").concat(Integer.toString(rowStart));
			} else {
				sql = sql.concat(" LIMIT ").concat(Integer.toString(rowEnd - rowStart)).concat(" OFFSET ")
						.concat(Integer.toString(rowStart));
			}
		}

		ArrayList alist = this.select(sql, variables);
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			CRFEvaluationItem crfEvaluationItem = new CRFEvaluationItem();
			crfEvaluationItem.setEventCrfId((Integer) hm.get("event_crf_id"));
			crfEvaluationItem.setEventDefinitionCrfId((Integer) hm.get("event_definition_crf_id"));
			crfEvaluationItem.setStudyEventId((Integer) hm.get("study_event_id"));
			crfEvaluationItem.setStudyEventDefinitionId((Integer) hm.get("study_event_definition_id"));
			crfEvaluationItem.setStudySubjectId((Integer) hm.get("study_subject_id"));
			crfEvaluationItem.setStudyId((Integer) hm.get("study_id"));
			crfEvaluationItem.setCrfVersionId((Integer) hm.get("crf_version_id"));
			crfEvaluationItem.setCrfId((Integer) hm.get("crf_id"));
			crfEvaluationItem.setCrfName((String) hm.get("crf_name"));
			crfEvaluationItem.setStudyEventName((String) hm.get("study_event_name"));
			crfEvaluationItem.setStudySubjectLabel((String) hm.get("study_subject_label"));
			crfEvaluationItem.setStatus(Status.get((Integer) hm.get("status_id")));
			crfEvaluationItem
					.setSubjectEventStatus(SubjectEventStatus.get((Integer) hm.get("subject_event_status_id")));
			crfEvaluationItem.setSdv((Boolean) hm.get("sdv_status"));
			crfEvaluationItem.setValidatorId((Integer) hm.get("validator_id"));
			crfEvaluationItem.setOwnerId((Integer) hm.get("owner_id"));
			crfEvaluationItem.setUpdaterId((Integer) hm.get("updater_id"));
			crfEvaluationItem.setDateValidate((Date) hm.get("date_validate"));
			crfEvaluationItem.setDateCompleted((Date) hm.get("date_completed"));
			crfEvaluationItem.setDateValidateCompleted((Date) hm.get("date_validate_completed"));
			crfEvaluationItem.setDateCreated((Date) hm.get("date_created"));
			crfEvaluationItem.setDateUpdated((Date) hm.get("date_updated"));

			result.add(crfEvaluationItem);
		}

		return result;
	}

	/**
	 * Method unsdvs event crfs when crf metadata was changed.
	 *
	 * @param crfVersionId
	 *            crf version id
	 * @param userId
	 *            user id
	 * @return boolean
	 */
	public boolean unsdvEventCRFsWhenCRFMetadataWasChanged(int crfVersionId, int userId) {
		unsetTypeExpected();

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		int ind = 1;
		variables.put(ind++, userId);
		variables.put(ind, crfVersionId);

		execute(digester.getQuery("unsdvEventCRFsWhenCRFMetadataWasChanged"), variables);

		return isQuerySuccessful();
	}

	/**
	 * Method sdvs event crfs when crf metadata was changed and al items are sdv.
	 *
	 * @param crfVersionId
	 *            crf version id
	 * @param userId
	 *            user id
	 * @param ignoreOutstandingQueries
	 *            boolean
	 * @return boolean
	 */
	public boolean sdvEventCRFsWhenCRFMetadataWasChangedAndAllItemsAreSDV(int crfVersionId, int userId,
			boolean ignoreOutstandingQueries) {
		unsetTypeExpected();

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		int ind = 1;
		variables.put(ind++, userId);
		variables.put(ind, crfVersionId);

		execute(digester.getQuery(ignoreOutstandingQueries
				? "sdvEventCRFsWhenCRFMetadataWasChangedAndAllItemsAreSDV"
				: "sdvEventCRFsWithoutOutstandingDNsWhenCRFMetadataWasChangedAndAllItemsAreSDV"), variables);

		return isQuerySuccessful();
	}

	/**
	 * Saves partial section info.
	 * 
	 * @param eventCrfId
	 *            int
	 * @param sectionId
	 *            int
	 * @param connection
	 *            Connection
	 */
	public void savePartialSectionInfo(int eventCrfId, int sectionId, Connection connection) {
		int ind = 1;
		unsetTypeExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(ind++, eventCrfId);
		variables.put(ind, sectionId);
		execute(digester.getQuery("savePartialSectionInfo"), variables, connection, true);
	}
}
