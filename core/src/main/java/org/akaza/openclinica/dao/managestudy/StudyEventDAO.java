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
package org.akaza.openclinica.dao.managestudy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.util.SignedData;

/**
 * StudyEventDAO.
 */
@SuppressWarnings({"rawtypes", "unchecked" })
public class StudyEventDAO extends AuditableEntityDAO {

	private void setQueryNames() {
		findByPKAndStudyName = "findByPKAndStudy";
		getCurrentPKName = "getCurrentPrimaryKey";
	}

	/**
	 * Study event constructor.
	 * 
	 * @param ds
	 *            DataSource
	 */
	public StudyEventDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	/**
	 * Study event constructor.
	 * 
	 * @param ds
	 *            DataSource
	 * @param con
	 *            Connection
	 */
	public StudyEventDAO(DataSource ds, Connection con) {
		super(ds, con);
		setQueryNames();
	}

	/**
	 * Study event constructor.
	 * 
	 * @param ds
	 *            DataSource
	 * @param digester
	 *            DAODigester
	 */
	public StudyEventDAO(DataSource ds, DAODigester digester) {
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
	public StudyEventDAO(DataSource ds, DAODigester digester, Locale locale) {
		this(ds, digester);
		this.locale = locale;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_STUDYEVENT;
	}

	@Override
	public void setTypesExpected() {
		this.unsetTypeExpected();
		int ind = 1;
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.STRING);

		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP); // YW 08-17-2007,
		// date_start
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP); // YW 08-17-2007,
		// date_end
		this.setTypeExpected(ind++, TypeNames.INT);

		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP);
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.BOOL); // start_time_flag
		this.setTypeExpected(ind++, TypeNames.BOOL); // end_time_flag
		this.setTypeExpected(ind++, TypeNames.INT); // prev_status
		this.setTypeExpected(ind++, TypeNames.INT); // reference_visit_id
		this.setTypeExpected(ind, TypeNames.BINARY_STREAM); // signed_data
	}

	/**
	 * Method sets expected types.
	 * 
	 * @param withSubject
	 *            boolean
	 */
	public void setTypesExpected(boolean withSubject) {
		this.unsetTypeExpected();
		int ind = 1;
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.STRING);

		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP); // YW 08-17-2007,
		// date_start
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP); // YW 08-17-2007,
		// date_end
		this.setTypeExpected(ind++, TypeNames.INT);

		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP);
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.BOOL); // start_time_flag
		this.setTypeExpected(ind++, TypeNames.BOOL); // end_time_flag

		this.setTypeExpected(ind++, TypeNames.INT); // prev_status
		this.setTypeExpected(ind++, TypeNames.INT); // reference_visit_id
		if (withSubject) {
			this.setTypeExpected(ind, TypeNames.STRING);
		}
	}

	/**
	 * Method sets expected types.
	 */
	public void setCRFTypesExpected() {
		int ind = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind, TypeNames.STRING);
	}

	/**
	 * <p>
	 * getEntityFromHashMap, the method that gets the object from the database query.
	 * 
	 * @param hm
	 *            HashMap
	 * @return Object
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		StudyEventBean eb = new StudyEventBean();
		super.setEntityAuditInformation(eb, hm);
		// STUDY_EVENT_ID STUDY_EVENT_DEFINITION_ID SUBJECT_ID LOCATION
		// SAMPLE_ORDINAL DATE_START DATE_END OWNER_ID
		// STATUS_ID DATE_CREATED DATE_UPDATED UPDATE_ID
		eb.setId((Integer) hm.get("study_event_id"));
		eb.setStudyEventDefinitionId((Integer) hm.get("study_event_definition_id"));
		eb.setStudySubjectId((Integer) hm.get("study_subject_id"));
		eb.setLocation((String) hm.get("location"));
		eb.setSampleOrdinal((Integer) hm.get("sample_ordinal"));
		eb.setDateStarted((Date) hm.get("date_start"));
		eb.setDateEnded((Date) hm.get("date_end"));
		// eb.setStatus(eb.getStatus());
		int subjectEventStatuId = (Integer) hm.get("subject_event_status_id");
		eb.setSubjectEventStatus(SubjectEventStatus.get(subjectEventStatuId));
		// YW 08-17-2007
		eb.setStartTimeFlag((Boolean) hm.get("start_time_flag"));
		eb.setEndTimeFlag((Boolean) hm.get("end_time_flag"));

		Integer prevSubjectEventStatus = (Integer) hm.get("prev_subject_event_status");
		eb.setPrevSubjectEventStatus(SubjectEventStatus.getByCode(prevSubjectEventStatus));

		Integer referenceVisitId = (Integer) hm.get("reference_visit_id");
		eb.setReferenceVisitId(referenceVisitId);

		try {
			eb.getSignedData().clear();
			ByteArrayInputStream bais = (ByteArrayInputStream) hm.get("signed_data");
			if (bais != null) {
				Map<Integer, SignedData> signedData = (Map<Integer, SignedData>) new ObjectInputStream(bais)
						.readObject();
				if (signedData != null) {
					eb.setSignedData(signedData);
				}
			}
		} catch (Exception e) {
			logger.error("Error has occurred.", e);
		}

		return eb;
	}

	/**
	 * <p>
	 * getEntityFromHashMap, the method that gets the object from the database query.
	 * 
	 * @param hm
	 *            HashMap
	 * @param withSubject
	 *            boolean
	 * @return Object
	 */
	public Object getEntityFromHashMap(HashMap hm, boolean withSubject) {
		StudyEventBean eb = new StudyEventBean();
		super.setEntityAuditInformation(eb, hm);
		// STUDY_EVENT_ID STUDY_EVENT_DEFINITION_ID SUBJECT_ID LOCATION
		// SAMPLE_ORDINAL DATE_START DATE_END OWNER_ID
		// STATUS_ID DATE_CREATED DATE_UPDATED UPDATE_ID
		eb.setId((Integer) hm.get("study_event_id"));
		eb.setStudyEventDefinitionId((Integer) hm.get("study_event_definition_id"));
		eb.setStudySubjectId((Integer) hm.get("study_subject_id"));
		eb.setLocation((String) hm.get("location"));
		eb.setSampleOrdinal((Integer) hm.get("sample_ordinal"));
		eb.setDateStarted((Date) hm.get("date_start"));
		eb.setDateEnded((Date) hm.get("date_end"));
		// eb.setStatus(eb.getStatus());
		int subjectEventStatuId = (Integer) hm.get("subject_event_status_id");
		eb.setSubjectEventStatus(SubjectEventStatus.get(subjectEventStatuId));
		// YW 08-17-2007
		eb.setStartTimeFlag((Boolean) hm.get("start_time_flag"));
		eb.setEndTimeFlag((Boolean) hm.get("end_time_flag"));

		Integer prevSubjectEventStatus = (Integer) hm.get("prev_subject_event_status");
		eb.setPrevSubjectEventStatus(SubjectEventStatus.getByCode(prevSubjectEventStatus));

		eb.setReferenceVisitId((Integer) hm.get("reference_visit_id"));

		if (withSubject) {
			eb.setStudySubjectLabel((String) hm.get("label"));
		}

		return eb;
	}

	/**
	 * Method returns all study events.
	 * 
	 * @return Collection
	 */
	public Collection findAll() {
		this.setTypesExpected();
		ArrayList alist = this.select(digester.getQuery("findAll"));
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyEventBean eb = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method returns all study events by definition id.
	 * 
	 * @param definitionId
	 *            int
	 * @return Collection
	 */
	public Collection findAllByDefinition(int definitionId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, definitionId);

		String sql = digester.getQuery("findAllByDefinition");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyEventBean eb = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method returns all study events by studyEventDefinitionOid & crfOrCrfVersionOid.
	 * 
	 * @param studyEventDefinitionOid
	 *            String
	 * @param crfOrCrfVersionOid
	 *            String
	 * @return ArrayList
	 */
	public ArrayList findAllByStudyEventDefinitionAndCrfOids(String studyEventDefinitionOid, String crfOrCrfVersionOid) {
		this.setTypesExpected(true);
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		int ind = 1;
		variables.put(ind++, studyEventDefinitionOid);
		variables.put(ind++, crfOrCrfVersionOid);
		variables.put(ind, crfOrCrfVersionOid);

		String sql = digester.getQuery("findAllByStudyEventDefinitionAndCrfOids");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyEventBean eb = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist, true);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method returns count of events based on event status.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param subjectEventStatus
	 *            SubjectEventStatus
	 * @return Integer
	 */
	public Integer getCountOfEventsBasedOnEventStatus(StudyBean currentStudy, SubjectEventStatus subjectEventStatus) {
		setTypesExpected();

		HashMap variables = new HashMap();
		int ind = 1;
		variables.put(ind++, currentStudy.getId());
		variables.put(ind++, currentStudy.getId());
		variables.put(ind, subjectEventStatus.getId());
		String sql = digester.getQuery("getCountofEventsBasedOnEventStatus");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Method returns count of no repeats events based on event status.
	 * 
	 * @param currentStudy
	 *            StudyBean
	 * @param subjectEventStatus
	 *            SubjectEventStatus
	 * @return Integer
	 */
	public Integer getCountOfEventsBasedOnEventStatusNoRepeats(StudyBean currentStudy,
			SubjectEventStatus subjectEventStatus) {
		setTypesExpected();
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, currentStudy.getId());
		variables.put(ind++, currentStudy.getId());
		variables.put(ind, subjectEventStatus.getId());
		String sql = digester.getQuery("getCountOfEventsBasedOnEventStatusNoRepeats");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Method returns count of no repeats events based on event status & study event definition id.
	 * 
	 * @param currentStudy
	 *            StudyBean
	 * @param subjectEventStatus
	 *            SubjectEventStatus
	 * @param studyEventDefinitionBean
	 *            StudyEventDefinitionBean
	 * @return Integer
	 */
	public Integer getEventCountFromEventStatusAndStudyEventDefinitionIdNoRepeats(StudyBean currentStudy,
			SubjectEventStatus subjectEventStatus, StudyEventDefinitionBean studyEventDefinitionBean) {
		setTypesExpected();
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, currentStudy.getId());
		variables.put(ind++, currentStudy.getId());
		variables.put(ind++, subjectEventStatus.getId());
		variables.put(ind, studyEventDefinitionBean.getId());

		String sql = digester.getQuery("getEventCountFromEventStatusAndStudyEventDefinitionIdNoRepeats");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Method returns count of events for current study.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @return Integer
	 */
	public Integer getCountOfEvents(StudyBean currentStudy) {
		setTypesExpected();
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, currentStudy.getId());
		variables.put(ind, currentStudy.getId());
		String sql = digester.getQuery("getCountofEvents");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Method returns all study events by passed parameters.
	 * 
	 * @param studyEventDefinitionOid
	 *            String
	 * @param crfOrCrfVersionOid
	 *            String
	 * @param ordinal
	 *            String
	 * @param studySubjectId
	 *            String
	 * @return StudyEventBean
	 */
	public StudyEventBean findAllByStudyEventDefinitionAndCrfOidsAndOrdinal(String studyEventDefinitionOid,
			String crfOrCrfVersionOid, String ordinal, String studySubjectId) {
		this.setTypesExpected(true);
		int ind = 1;
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(ind++, studyEventDefinitionOid);
		variables.put(ind++, Integer.valueOf(studySubjectId));
		variables.put(ind++, Integer.valueOf(ordinal));
		variables.put(ind++, crfOrCrfVersionOid);
		variables.put(ind, crfOrCrfVersionOid);

		String sql = digester.getQuery("findAllByStudyEventDefinitionAndCrfOidsAndOrdinal");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyEventBean eb = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist, true);
			al.add(eb);
		}
		if (al.isEmpty()) {
			return null;
		}
		if (al.size() == 1) {
			return (StudyEventBean) al.get(0);
		} else {
			logger.warn(
					"The query in findAllByStudyEventDefinitionAndCrfOidsAndOrdinal return a list of size {}. Business logic assumes only one",
					al.size());
			return (StudyEventBean) al.get(0);
		}
	}

	/**
	 * Method returns all study events with subject label by definition id.
	 * 
	 * @param definitionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllWithSubjectLabelByDefinition(int definitionId) {
		this.setTypesExpected(true);
		HashMap variables = new HashMap();
		variables.put(1, definitionId);

		String sql = digester.getQuery("findAllWithSubjectLabelByDefinition");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyEventBean eb = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist, true);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method returns all study events with subject label by study subject & definition id.
	 * 
	 * @param studySubject
	 *            StudySubjectBean
	 * @param definitionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllWithSubjectLabelByStudySubjectAndDefinition(StudySubjectBean studySubject, int definitionId) {
		this.setTypesExpected(true);
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studySubject.getId());
		variables.put(ind, definitionId);

		String sql = digester.getQuery("findAllWithSubjectLabelByStudySubjectAndDefinition");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyEventBean eb = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist, true);
			eb.setStudySubject(studySubject);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method returns study event by passed parameters.
	 * 
	 * @param ssbid
	 *            int
	 * @param sedid
	 *            int
	 * @param ord
	 *            int
	 * @return EntityBean
	 */
	public EntityBean findByStudySubjectIdAndDefinitionIdAndOrdinal(int ssbid, int sedid, int ord) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		int ind = 1;
		variables.put(ind++, ssbid);
		variables.put(ind++, sedid);
		variables.put(ind, ord);

		String sql = digester.getQuery("findByStudySubjectIdAndDefinitionIdAndOrdinal");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();
		StudyEventBean eb = new StudyEventBean();
		if (it.hasNext()) {
			eb = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * Method returns all study events.
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
	 * Method returns list of all study events by definition & subject.
	 * 
	 * @param definition
	 *            StudyEventDefinitionBean
	 * @param subject
	 *            StudySubjectBean
	 * @return ArrayList
	 */
	public ArrayList findAllByDefinitionAndSubject(StudyEventDefinitionBean definition, StudySubjectBean subject) {
		ArrayList answer = new ArrayList();

		setTypesExpected();
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, definition.getId());
		variables.put(ind, subject.getId());

		String sql = digester.getQuery("findAllByDefinitionAndSubject");

		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			StudyEventBean studyEvent = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(studyEvent);
		}

		return answer;
	}
	
	/**
	 * method returns list of all study events by definition & subject.
	 * 
	 * @param definition
	 *            StudyEventDefinitionBean
	 * @param subject
	 *            StudySubjectBean
	 * @return ArrayList
	 */
	public ArrayList findAllByDefinitionAndSubjectOrderByOrdinal(StudyEventDefinitionBean definition,
			StudySubjectBean subject) {
		return findAllByDefinitionAndSubjectOrderByOrdinal(definition.getId(), subject.getId());
	}

	/**
	 * method returns list of all study events by definition id & study subject id.
	 * 
	 * @param definitionId
	 *            study event definition id
	 * @param studySubjectId
	 *            stud subject id
	 * @return ArrayList
	 */
	public ArrayList findAllByDefinitionAndSubjectOrderByOrdinal(int definitionId, int studySubjectId) {
		ArrayList answer = new ArrayList();

		setTypesExpected();
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, definitionId);
		variables.put(index, studySubjectId);

		String sql = digester.getQuery("findAllByDefinitionAndSubjectOrderByOrdinal");

		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			StudyEventBean studyEvent = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(studyEvent);
		}

		return answer;
	}

	/**
	 * Method returns study event by id.
	 * 
	 * @param id
	 *            int
	 * @return EntityBean
	 */
	public EntityBean findByPK(int id) {
		StudyEventBean eb = new StudyEventBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return eb;
	}

	/**
	 * Methdo creates a new study event.
	 * 
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean create(EntityBean eb) {
		StudyEventBean sb = (StudyEventBean) eb;
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		int ind = 1;
		variables.put(ind++, sb.getStudyEventDefinitionId());
		variables.put(ind++, sb.getStudySubjectId());
		variables.put(ind++, sb.getLocation());
		variables.put(ind++, sb.getSampleOrdinal());
		if (sb.getDateStarted() == null) {
			nullVars.put(ind, TypeNames.TIMESTAMP);
			variables.put(ind++, null);
		} else {
			variables.put(ind++, new Timestamp(sb.getDateStarted().getTime()));
		}
		if (sb.getDateEnded() == null) {
			nullVars.put(ind, TypeNames.TIMESTAMP);
			variables.put(ind++, null);
		} else {
			variables.put(ind++, new Timestamp(sb.getDateEnded().getTime()));
		}
		variables.put(ind++, sb.getOwner().getId());
		variables.put(ind++, sb.getStatus().getId());
		variables.put(ind++, sb.getSubjectEventStatus().getId());
		variables.put(ind++, sb.getStartTimeFlag());
		variables.put(ind++, sb.getEndTimeFlag());
		variables.put(ind, sb.getReferenceVisitId());

		this.executeWithPK(digester.getQuery("create"), variables, nullVars);
		if (isQuerySuccessful()) {
			sb.setId(getLatestPK());
		}

		return sb;
	}

	/**
	 * Method updates study event.
	 * 
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean update(EntityBean eb) {
		return update(eb, null);
	}

	/**
	 * This function allows to run transactional updates for an action.
	 * 
	 * @param eb
	 *            EntityBean
	 * @param con
	 *            Connection
	 * @return EntityBean
	 */
	public EntityBean update(EntityBean eb, Connection con) {
		StudyEventBean sb = (StudyEventBean) eb;
		HashMap nullVars = new HashMap();
		HashMap variables = new HashMap();

		sb.setActive(false);
		int ind = 1;
		variables.put(ind++, sb.getStudyEventDefinitionId());
		variables.put(ind++, sb.getStudySubjectId());
		variables.put(ind++, sb.getLocation());
		variables.put(ind++, sb.getSampleOrdinal());
		variables.put(ind++, new Timestamp(sb.getDateStarted().getTime()));
		if (sb.getDateEnded() == null) {
			nullVars.put(ind, TypeNames.TIMESTAMP);
			variables.put(ind++, null);
		} else {
			variables.put(ind++, new Timestamp(sb.getDateEnded().getTime()));
		}
		variables.put(ind++, sb.getStatus().getId());
		variables.put(ind++, new Timestamp(new Date().getTime())); // DATE_Updated
		if (sb.getUpdater() == null) {
			nullVars.put(ind, Types.INTEGER);
			variables.put(ind++, null);
		} else {
			variables.put(ind++, sb.getUpdater().getId());
		}
		variables.put(ind++, sb.getSubjectEventStatus().getId());
		variables.put(ind++, sb.getStartTimeFlag()); // YW
		// start_time_flag
		variables.put(ind++, sb.getEndTimeFlag()); // YW
		// end_time_flag
		variables.put(ind++, sb.getPrevSubjectEventStatus().getId());
		variables.put(ind++, sb.getReferenceVisitId());

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(baos);
			oout.writeObject(sb.getSignedData());
			oout.close();
			variables.put(ind++, baos);
		} catch (Exception e) {
			nullVars.put(ind++, null);
		}

		variables.put(ind, sb.getId());

		String sql = digester.getQuery("update");

		this.execute(sql, variables, nullVars, con);

		if (isQuerySuccessful()) {
			sb.setActive(true);
		}

		return sb;
	}

	/**
	 * Method returns collection of all study events by permission.
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
	 * Method returns collection of all study events by permission.
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
	 * Method returns all study events by study & study subject id.
	 * 
	 * @param study
	 *            StudyBean
	 * @param studySubjectId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllByStudyAndStudySubjectId(StudyBean study, int studySubjectId) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, study.getId());
		variables.put(ind++, study.getId());
		variables.put(ind, studySubjectId);

		ArrayList alist = this.select(digester.getQuery("findAllByStudyAndStudySubjectId"), variables);

		for (Object anAlist : alist) {
			StudyEventBean seb = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;
	}

	/**
	 * Method returns all study events by study & event definition id.
	 * 
	 * @param study
	 *            StudyBean
	 * @param eventDefinitionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllByStudyAndEventDefinitionId(StudyBean study, int eventDefinitionId) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, study.getId());
		variables.put(ind++, study.getId());
		variables.put(ind, eventDefinitionId);

		ArrayList alist = this.select(digester.getQuery("findAllByStudyAndEventDefinitionId"), variables);

		for (Object anAlist : alist) {
			StudyEventBean seb = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;
	}

	/**
	 * Method returns all study events except locked, skipped, stopped, removed by study & event definition id.
	 * 
	 * @param study
	 *            StudyBean
	 * @param eventDefinitionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllByStudyAndEventDefinitionIdExceptLockedSkippedStoppedRemoved(StudyBean study,
			int eventDefinitionId) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, study.getId());
		variables.put(ind++, study.getId());
		variables.put(ind, eventDefinitionId);

		ArrayList alist = this.select(
				digester.getQuery("findAllByStudyAndEventDefinitionIdExceptLockedSkippedStoppedRemoved"), variables);

		for (Object anAlist : alist) {
			StudyEventBean seb = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;
	}

	/**
	 * Get the maximum sample ordinal over all study events for the provided StudyEventDefinition / StudySubject
	 * combination. Note that the maximum may be zero but must be non-negative.
	 * 
	 * @param sedb
	 *            The study event definition whose ordinal we're looking for.
	 * @param studySubject
	 *            The study subject whose ordinal we're looking for.
	 * @return The maximum sample ordinal over all study events for the provided combination, or 0 if no such
	 *         combination exists.
	 */
	public int getMaxSampleOrdinal(StudyEventDefinitionBean sedb, StudySubjectBean studySubject) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, sedb.getId());
		variables.put(ind, studySubject.getId());

		ArrayList alist = this.select(digester.getQuery("getMaxSampleOrdinal"), variables);
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			try {
				HashMap hm = (HashMap) it.next();
				return (Integer) hm.get("max_ord");
			} catch (Exception e) {
				logger.error("Error has occurred.", e);
			}
		}

		return 0;
	}

	@Override
	public ArrayList findAllByStudy(StudyBean study) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, study.getId());

		ArrayList alist = this.select(digester.getQuery("findAllByStudy"), variables);

		for (Object anAlist : alist) {
			StudyEventBean seb = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;
	}

	/**
	 * Method returns all study events by subject id & study id.
	 * 
	 * @deprecated
	 * @param subjectId
	 *            int
	 * @param studyId
	 *            int
	 * @return ArrayList
	 */
	@Deprecated
	public ArrayList findAllBySubjectAndStudy(int subjectId, int studyId) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, subjectId);
		variables.put(ind++, studyId);
		variables.put(ind, studyId);

		ArrayList alist = this.select(digester.getQuery("findAllBySubjectAndStudy"), variables);

		for (Object anAlist : alist) {
			StudyEventBean seb = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;

	}

	/**
	 * Method returns all study events by subject id.
	 * 
	 * @param subjectId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllBySubjectId(int subjectId) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, subjectId);

		ArrayList alist = this.select(digester.getQuery("findAllBySubjectId"), variables);

		for (Object anAlist : alist) {
			StudyEventBean seb = (StudyEventBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;

	}

	/**
	 * Method sets expected types.
	 */
	public void setNewCRFTypesExpected() {
		int ind = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind, TypeNames.STRING);

	}

	/**
	 * Using the HashMaps returned from a <code>select</code> call in findCRFsByStudy, prepare a HashMap whose keys are
	 * study event definitions and whose values are ArrayLists of CRF versions included in those definitions.
	 * 
	 * @param rows
	 *            The HashMaps retured by the <code>select</code> call in findCRFsByStudy.
	 * @return a HashMap whose keys are study event definitions and whose values are ArrayLists of CRF versions included
	 *         in those definitions. Both the keys of the HashMap and the elements of the ArrayLists are actually
	 *         EntitBeans.
	 */
	public HashMap getEventsAndMultipleCRFVersionInformation(ArrayList rows) {
		HashMap returnMe = new HashMap();
		Iterator it = rows.iterator();
		EntityBean event;
		EntityBean crf;
		while (it.hasNext()) {
			HashMap answers = (HashMap) it.next();

			// removed setActive since the setId calls automatically result in
			// setActive calls
			event = new EntityBean();
			event.setName((String) answers.get("sed_name"));
			event.setId((Integer) answers.get("study_event_definition_id"));

			crf = new EntityBean();
			crf.setName(answers.get("crf_name") + " " + answers.get("ver_name"));
			crf.setId((Integer) answers.get("crf_version_id"));

			ArrayList crfs;
			if (this.findDouble(returnMe, event)) {

				crfs = this.returnDouble(returnMe, event);
				crfs.add(crf);
				returnMe = this.removeDouble(returnMe, event);
				returnMe.put(event, crfs);
			} else {
				crfs = new ArrayList();
				logger.warn("put a crf into a NEW event: " + crf.getName() + " into " + event.getName());
				crfs.add(crf);
				returnMe.put(event, crfs);
				// maybe combine the two crf +
				// version?
			}
		}
		// end of cycling through answers
		return returnMe;
	}

	/**
	 * Method retuns crfs by sutdy.
	 * 
	 * @param sb
	 *            StudyBean
	 * @return HashMap
	 */
	public HashMap findCRFsByStudy(StudyBean sb) {
		HashMap crfs;
		this.setNewCRFTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, sb.getId());
		ArrayList alist = this.select(digester.getQuery("findCRFsByStudy"), variables);
		crfs = this.getEventsAndMultipleCRFVersionInformation(alist);
		return crfs;
	}

	/**
	 * Method returns all crfs by study event.
	 * 
	 * @param seb
	 *            StudyEventBean
	 * @return HashMap
	 */
	public HashMap findCRFsByStudyEvent(StudyEventBean seb) {
		// Soon-to-be-depreciated, replaced by find crfs by study, tbh 11-26
		// returns a hashmap of crfs + arraylist of crfversions,
		// for creating a checkbox list of crf versions all collected by
		// the study event primary key, tbh
		HashMap crfs = new HashMap();
		this.setCRFTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, seb.getStudyEventDefinitionId());
		ArrayList alist = this.select(digester.getQuery("findCRFsByStudyEvent"), variables);
		Iterator it = alist.iterator();
		CRFDAO cdao = new CRFDAO(this.ds);
		CRFVersionDAO cvdao = new CRFVersionDAO(this.ds);
		while (it.hasNext()) {
			HashMap answers = (HashMap) it.next();
			logger.warn("***First CRF ID: " + answers.get("crf_id"));
			logger.warn("***Next CRFVersion ID: " + answers.get("crf_version_id"));
			// here's the logic:
			// grab a crf,
			// iterate through crfs in hashmap,
			// if one matches, grab it;
			// take a look at its arraylist of versions;
			// if there is no version correlating, add it;
			// else, add the crf with a fresh arraylist + one version.
			// how long could this take to run???
			CRFBean cbean = (CRFBean) cdao.findByPK(((Integer) answers.get("crf_id")).intValue());
			CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(((Integer) answers.get("crf_version_id")).intValue());
			Set se = crfs.entrySet();
			boolean found = false;
			boolean versionFound = false;
			for (Object aSe : se) {
				Map.Entry me = (Map.Entry) aSe;
				CRFBean checkCrf = (CRFBean) me.getKey();
				if (checkCrf.getId() == cbean.getId()) {
					found = true;
					ArrayList oldList = (ArrayList) me.getValue();
					for (Object anOldList : oldList) {
						CRFVersionBean cvbCheck = (CRFVersionBean) anOldList;
						if (cvbCheck.getId() == cvb.getId()) {
							versionFound = true;
						}
					}
					// end of iteration through versions
					if (!versionFound) {
						oldList.add(cvb);
						crfs.put(cbean, oldList);
					}
					// end of adding new version to old crf
				}
				// end of check to see if current crf is in list
			}
			// end of iterating
			if (!found) {
				// add new crf here with version
				// CRFVersionBean cvb = (CRFVersionBean)cvdao.findByPK(
				// ((Integer)answers.get("crf_version_id")).intValue());
				ArrayList newList = new ArrayList();
				newList.add(cvb);
				crfs.put(cbean, newList);
			}
		}
		// end of cycling through answers
		return crfs;
	}

	/**
	 * Method checks for duplication.
	 * 
	 * @param hm
	 *            HashMap
	 * @param event
	 *            EntityBean
	 * @return boolean
	 */
	public boolean findDouble(HashMap hm, EntityBean event) {
		boolean returnMe = false;
		Set s = hm.entrySet();
		for (Object value : s) {
			Map.Entry me = (Map.Entry) value;
			EntityBean eb = (EntityBean) me.getKey();
			if (eb.getId() == event.getId() && eb.getName().equals(event.getName())) {
				logger.warn("found OLD bean, return true");
				returnMe = true;
			}
		}
		return returnMe;
	}

	/**
	 * So as not to get null pointer returns, tbh.
	 * 
	 * @param hm
	 *            HashMap
	 * @param event
	 *            EntityBean
	 * @return ArrayList
	 */
	public ArrayList returnDouble(HashMap hm, EntityBean event) {
		ArrayList al = new ArrayList();
		Set s = hm.entrySet();
		for (Object value : s) {
			Map.Entry me = (Map.Entry) value;
			EntityBean eb = (EntityBean) me.getKey();
			if (eb.getId() == event.getId() && eb.getName().equals(event.getName())) {
				// logger.warn("found OLD bean, return true");
				al = (ArrayList) me.getValue();
			}
		}
		return al;
	}

	/**
	 * So as to remove the object correctly, tbh.
	 * 
	 * @param hm
	 *            HashMap
	 * @param event
	 *            EntityBean
	 * @return HashMap
	 */
	public HashMap removeDouble(HashMap hm, EntityBean event) {
		Set s = hm.entrySet();
		EntityBean removeMe = new EntityBean();
		for (Object value : s) {
			Map.Entry me = (Map.Entry) value;
			EntityBean eb = (EntityBean) me.getKey();
			if (eb.getId() == event.getId() && eb.getName().equals(event.getName())) {
				logger.warn("found OLD bean, remove it");
				removeMe = eb;
			}
		}
		hm.remove(removeMe);
		return hm;
	}

	/**
	 * Method returns definition id by study event id.
	 * 
	 * @param studyEventId
	 *            int
	 * @return int
	 */
	public int getDefinitionIdFromStudyEventId(int studyEventId) {
		int answer = 0;

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyEventId);

		ArrayList rows = select(digester.getQuery("getDefinitionIdFromStudyEventId"), variables);

		if (rows.size() > 0) {
			HashMap row = (HashMap) rows.get(0);
			answer = (Integer) row.get("study_event_definition_id");
		}

		return answer;
	}

	/**
	 * Method returns all study events by study subject.
	 * 
	 * @param ssb
	 *            StudySubjectBean
	 * @return ArrayList
	 */
	public ArrayList findAllByStudySubject(StudySubjectBean ssb) {
		HashMap variables = new HashMap();
		variables.put(1, ssb.getId());

		return executeFindAllQuery("findAllByStudySubject", variables);
	}

	/**
	 *
	 * Method returns all study events by study subject & study event definition.
	 * 
	 * @param ssb
	 *            StudySubjectBean
	 * @param sed
	 *            StudyEventDefinitionBean
	 * @return ArrayList
	 */
	public ArrayList findAllByStudySubjectAndDefinition(StudySubjectBean ssb, StudyEventDefinitionBean sed) {
		HashMap variables = new HashMap();
		int ind = 1;
		variables.put(ind++, ssb.getId());
		variables.put(ind, sed.getId());

		return executeFindAllQuery("findAllByStudySubjectAndDefinition", variables);
	}

	/**
	 * Method returns count of not removed events.
	 * 
	 * @param studyEventDefinitionId
	 *            Integer
	 * @return Integer
	 */
	public Integer countNotRemovedEvents(Integer studyEventDefinitionId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, studyEventDefinitionId);
		String sql = digester.getQuery("countNotRemovedEvents");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return 0;
		}
	}

	/**
	 * Method returns study subject crf data.
	 * 
	 * @param sb
	 *            StudyBean
	 * @param studySubjectId
	 *            int
	 * @param eventDefId
	 *            int
	 * @param crfVersionOID
	 *            String
	 * @param eventOrdinal
	 *            int
	 * @return HashMap
	 */
	public HashMap getStudySubjectCRFData(StudyBean sb, int studySubjectId, int eventDefId, String crfVersionOID,
			int eventOrdinal) {
		HashMap studySubjectCRFDataDetails;
		this.unsetTypeExpected();
		int ind = 1;
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind, TypeNames.INT);

		ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, sb.getParentStudyId() > 0 ? sb.getParentStudyId() : sb.getId());
		variables.put(ind++, eventOrdinal);
		variables.put(ind++, crfVersionOID);
		variables.put(ind++, studySubjectId);
		variables.put(ind, eventDefId);

		ArrayList alist = this.select(digester.getQuery("getStudySubjectCRFDataDetails"), variables);
		studySubjectCRFDataDetails = this.getStudySubjectCRFDataDetails(alist);
		return studySubjectCRFDataDetails;
	}

	private HashMap getStudySubjectCRFDataDetails(ArrayList rows) {
		HashMap returnMe = new HashMap();
		for (Object row : rows) {
			HashMap answers = (HashMap) row;

			returnMe.put("event_crf_id", answers.get("event_crf_id"));
			returnMe.put("event_definition_crf_id", answers.get("event_definition_crf_id"));
			returnMe.put("study_event_id", answers.get("study_event_id"));

		}
		// end of cycling through answers
		return returnMe;
	}

	/**
	 * Method deletes study event dn map by study event id.
	 * 
	 * @param studyEventId
	 *            int
	 */
	public void deleteStudyEventDNMap(int studyEventId) {
		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(1, studyEventId);
		this.execute(digester.getQuery("deleteStudyEventDNMap"), variables);
	}

	/**
	 * Method deletes study event by id.
	 * 
	 * @param id
	 *            int
	 */
	public void deleteByPK(int id) {
		HashMap variables = new HashMap();
		variables.put(1, id);
		this.execute(digester.getQuery("deleteByPK"), variables);
	}

	/**
	 * Method returns all study events by crf version and subject event status.
	 * 
	 * @param crfVersionId
	 *            int
	 * @param subjectEventStatus
	 *            SubjectEventStatus
	 * @return List<StudyEventBean>
	 */
	public List<StudyEventBean> findStudyEventsByCrfVersionAndSubjectEventStatus(int crfVersionId,
			SubjectEventStatus subjectEventStatus) {
		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);
		variables.put(2, subjectEventStatus.getId());
		return executeFindAllQuery("findStudyEventsByCrfVersionAndSubjectEventStatus", variables);
	}
}
