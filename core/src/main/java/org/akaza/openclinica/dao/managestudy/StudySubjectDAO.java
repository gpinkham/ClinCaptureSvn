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

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.dao.StudySubjectSDVFilter;
import org.akaza.openclinica.dao.StudySubjectSDVSort;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.OpenClinicaException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * StudySubjectDAO class. It performs the CRUD operations related to study subject items.
 */
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public class StudySubjectDAO extends AuditableEntityDAO {

	/**
	 * Method that sets the query names.
	 */
	public void setQueryNames() {
		findAllByStudyName = "findAllByStudy";
		findByPKAndStudyName = "findByPKAndStudy";
		getCurrentPKName = "getCurrentPK";

	}

	/**
	 * StudySubjectDAO constructor.
	 *
	 * @param ds
	 *            the jdbc DataSource object
	 */
	public StudySubjectDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	/**
	 * StudySubjectDAO constructor.
	 *
	 * @param ds
	 *            the jdbc DataSource object
	 * @param connection
	 *            the jdbc Connection object
	 */
	public StudySubjectDAO(DataSource ds, Connection connection) {
		super(ds, connection);
		setQueryNames();
	}

	/**
	 * StudySubjectDAO constructor.
	 *
	 * @param ds
	 *            the jdbc DataSource object
	 * @param digester
	 *            the DAODigester object
	 */
	public StudySubjectDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
		setQueryNames();
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_STUDYSUBJECT;
	}

	@Override
	public void setTypesExpected() {
		// study_subject_id | integer | not null default
		// nextval('public.study_subject_study_subject_id_seq'::text)
		// label | character varying(30) |
		// secondary_label | character varying(30) |
		// subject_id | numeric |
		// study_id | numeric |
		// status_id | numeric |
		// enrollment_date | date |
		// date_created | date |
		// date_updated | date |
		// owner_id | numeric |
		// update_id | numeric |
		// dynamic_group_class_id | numeric |

		this.unsetTypeExpected();
		int ind = 1;
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // study_subject_id
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // label
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // secondary_label
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // subject_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // study_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // status_id

		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // enrollment_date
		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // date_created
		this.setTypeExpected(ind, TypeNames.TIMESTAMP);
		ind++; // date_updated
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // owner_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // update_id
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // oc oid
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // dynamic_group_class_id
		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // Randomzation Date
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // Randomization Result
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; //
		this.setTypeExpected(ind, TypeNames.INT);
	}

	/**
	 * Method that sets expected types for some DAO methods.
	 */
	public void setTypesExpectedFilter() {

		this.unsetTypeExpected();
		int ind = 1;
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // study_subject_id
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // label
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // secondary_label
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // subject_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // study_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // status_id

		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // enrollment_date
		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // date_created
		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // date_updated
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // owner_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // update_id
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // oc oid
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // dynamic_group_class_id
		this.setTypeExpected(ind, TypeNames.DATE);
		ind++;
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++;
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++;
		this.setTypeExpected(ind, TypeNames.STRING);
	}

	/**
	 * Method that gets the object from the database query.
	 *
	 * @param hm
	 *            HashMap
	 * @return Object
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		StudySubjectBean eb = new StudySubjectBean();
		super.setEntityAuditInformation(eb, hm);
		// STUDY_SUBJECT_ID, LABEL, SUBJECT_ID, STUDY_ID
		// STATUS_ID, DATE_CREATED, OWNER_ID, STUDY_GROUP_ID
		// DATE_UPDATED, UPDATE_ID, DYNAMIC_GROUP_CLASS_ID
		Integer ssid = (Integer) hm.get("study_subject_id");
		eb.setId(ssid);

		eb.setLabel((String) hm.get("label"));
		eb.setSubjectId((Integer) hm.get("subject_id"));
		eb.setStudyId((Integer) hm.get("study_id"));
		// eb.setStudyGroupId(((Integer) hm.get("study_group_id")).intValue());
		eb.setEnrollmentDate((Date) hm.get("enrollment_date"));
		eb.setSecondaryLabel((String) hm.get("secondary_label"));
		eb.setOid((String) hm.get("oc_oid"));
		eb.setStudyName((String) hm.get("unique_identifier"));
		eb.setDynamicGroupClassId((Integer) hm.get("dynamic_group_class_id"));
		eb.setRandomizationDate((Date) hm.get("randomization_date"));
		eb.setRandomizationResult((String) hm.get("randomization_result"));
		return eb;
	}

	/**
	 * Method that returns List of groups by study subject, study id and parent study id.
	 *
	 * @param studySubjectId
	 *            study subject id
	 * @param studyId
	 *            study id
	 * @param parentStudyId
	 *            parent study id
	 * @return ArrayList
	 */
	public ArrayList getGroupByStudySubject(int studySubjectId, int studyId, int parentStudyId) {

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, studySubjectId);
		variables.put(index++, studyId);
		variables.put(index, parentStudyId);

		return executeFindAllQuery("getGroupByStudySubject", variables);
	}

	/**
	 * Method that returns Collection of all study subjects.
	 *
	 * @return Collection
	 */
	public Collection findAll() {
		this.setTypesExpected();
		String sql = digester.getQuery("findAll");
		ArrayList alist = this.select(sql);
		ArrayList answer = new ArrayList();
		for (Object anAlist : alist) {
			StudySubjectBean eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(eb);
		}
		return answer;
	}

	/**
	 * Method that checks that study subject is ready to be source data verified.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param studySubject
	 *            StudySubjectBean
	 * @return boolean
	 */
	public boolean isStudySubjectReadyToBeSDVed(StudyBean currentStudy, StudySubjectBean studySubject) {
		int result = 0;
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		int studyId = currentStudy.getId();
		int parentStudyId = currentStudy.getParentStudyId() > 0 ? currentStudy.getParentStudyId() : currentStudy
				.getId();
		boolean withoutDns = currentStudy.getStudyParameterConfig().getAllowSdvWithOpenQueries().equalsIgnoreCase("no");

		int index = 1;
		HashMap variables = new HashMap();
		ArrayList<StudySubjectBean> studySubjects = new ArrayList<StudySubjectBean>();
		variables.put(index++, studyId);
		variables.put(index++, studyId);
		variables.put(index, studySubject.getId());

		String sql = "SELECT mss.study_subject_id FROM study_subject mss LEFT JOIN study s on s.study_id = mss.study_id WHERE mss.study_subject_id IN ("
				.concat(digester.getQuery("findAllByStudySDV"));
		if (withoutDns) {
			sql = sql.concat(" ").concat(digester.getQuery("withoutDns"));
		}
		sql = sql.concat(" WHERE (s.study_id = ? or s.parent_study_id = ?) AND mss.study_subject_id IN (")
				.concat(digester.getQuery("readyToBeSdvStudySubjectFilter")).concat(")");
		if (withoutDns) {
			sql = sql.concat(" ").concat(digester.getQuery("withoutDnsTail"));
		}
		sql = sql.concat(")");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			result = (Integer) ((HashMap) it.next()).get("study_subject_id");
		}

		return studySubject.getId() == result;
	}

	/**
	 * Method that checks that study subject is source data verified.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param studySubject
	 *            StudySubjectBean
	 * @return boolean
	 */
	public boolean isStudySubjectSDVed(StudyBean currentStudy, StudySubjectBean studySubject) {
		int result = 0;
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		int studyId = currentStudy.getId();
		int parentStudyId = currentStudy.getParentStudyId() > 0 ? currentStudy.getParentStudyId() : currentStudy
				.getId();
		boolean withoutDns = currentStudy.getStudyParameterConfig().getAllowSdvWithOpenQueries().equalsIgnoreCase("no");

		int index = 1;
		HashMap variables = new HashMap();
		ArrayList<StudySubjectBean> studySubjects = new ArrayList<StudySubjectBean>();
		variables.put(index++, studyId);
		variables.put(index++, studyId);
		variables.put(index, studySubject.getId());

		String sql = "SELECT mss.study_subject_id FROM study_subject mss LEFT JOIN study s on s.study_id = mss.study_id WHERE mss.study_subject_id IN ("
				.concat(digester.getQuery("findAllByStudySDV"));
		if (withoutDns) {
			sql = sql.concat(" ").concat(digester.getQuery("withoutDns"));
		}
		sql = sql.concat(" WHERE (s.study_id = ? or s.parent_study_id = ?) AND mss.study_subject_id IN (")
				.concat(digester.getQuery("sdvCompleteFilterForCertainStudySubject")).concat(")");
		if (withoutDns) {
			sql = sql.concat(" ").concat(digester.getQuery("withoutDnsTail"));
		}
		sql = sql.concat(")");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			result = (Integer) ((HashMap) it.next()).get("study_subject_id");
		}

		return studySubject.getId() == result;
	}

	/**
	 * Method that returns List of study subjects by currentStudy, StudySubjectSDVFilter, StudySubjectSDVSort, rowStart
	 * and rowEnd.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            StudySubjectSDVFilter
	 * @param sort
	 *            StudySubjectSDVSort
	 * @param rowStart
	 *            rowStart
	 * @param rowEnd
	 *            rowEnd
	 * @return boolean
	 */
	public ArrayList findAllByStudySDV(StudyBean currentStudy, StudySubjectSDVFilter filter, StudySubjectSDVSort sort,
			int rowStart, int rowEnd) {
		this.setTypesExpected();

		int studyId = currentStudy.getId();
		int parentStudyId = currentStudy.getParentStudyId() > 0 ? currentStudy.getParentStudyId() : currentStudy
				.getId();
		boolean withoutDns = currentStudy.getStudyParameterConfig().getAllowSdvWithOpenQueries().equalsIgnoreCase("no");

		int index = 1;
		HashMap variables = new HashMap();
		ArrayList<StudySubjectBean> studySubjects = new ArrayList<StudySubjectBean>();
		variables.put(index++, studyId);
		variables.put(index, studyId);

		String sql = "SELECT distinct mss.*, s.unique_identifier FROM study_subject mss"
				+ " INNER JOIN study_event ON study_event.study_subject_id = mss.study_subject_id "
				+ " INNER JOIN event_crf ON event_crf.study_event_id = study_event.study_event_id "
				+ " LEFT JOIN study s on s.study_id = mss.study_id WHERE event_crf.status_id = 2 AND mss.study_subject_id IN ("
		.concat(digester.getQuery("findAllByStudySDV"));
		
		if (withoutDns) {
			sql = sql.concat(" ").concat(digester.getQuery("withoutDns"));
		}
		sql = sql.concat(" where (s.study_id = ? or s.parent_study_id = ?) ");
		sql = sql.concat(filter.execute(""));
		if (withoutDns) {
			sql = sql.concat(" ").concat(digester.getQuery("withoutDnsTail"));
		}
		sql = sql.concat(")");

		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			sql += ")x) where r between " + (rowStart + 1) + " and " + rowEnd;
			sql = sql + sort.execute("");
		} else {
			sql = sql + sort.execute("");
			sql = sql + " LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
		}
		ArrayList rows = this.select(sql, variables);

		for (Object row : rows) {
			StudySubjectBean studySubjectBean = (StudySubjectBean) this.getEntityFromHashMap((HashMap) row);
			studySubjects.add(studySubjectBean);
		}
		return studySubjects;
	}

	/**
	 * Method that returns count of study subjects by label.
	 *
	 * @param label
	 *            study subject label
	 * @return Integer
	 */
	public Integer countByLabel(String label) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, label);
		variables.put(index++, label);
		variables.put(index, label);

		ArrayList alist = this.select(digester.getQuery("countByLabel"), variables);
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return 0;
		}
	}

	/**
	 * Method that returns sql by query name.
	 *
	 * @param queryName
	 *            query name
	 * @return String
	 */
	public String getQuery(String queryName) {
		return digester.getQuery(queryName);
	}

	/**
	 * Method that returns count of study subjects by currentStudy and StudySubjectSDVFilter.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            StudySubjectSDVFilter
	 * @return int
	 */
	public int countAllByStudySDV(StudyBean currentStudy, StudySubjectSDVFilter filter) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		int studyId = currentStudy.getId();
		int parentStudyId = currentStudy.getParentStudyId() > 0 ? currentStudy.getParentStudyId() : currentStudy
				.getId();
		boolean withoutDns = currentStudy.getStudyParameterConfig().getAllowSdvWithOpenQueries().equalsIgnoreCase("no");

		int index = 1;
		HashMap variables = new HashMap();
		ArrayList<StudySubjectBean> studySubjects = new ArrayList<StudySubjectBean>();
		variables.put(index++, studyId);
		variables.put(index, studyId);

		String sql = "SELECT count(distinct mss.study_subject_id) FROM study_subject mss"
				+ " INNER JOIN study_event ON study_event.study_subject_id = mss.study_subject_id "
				+ " INNER JOIN event_crf ON event_crf.study_event_id = study_event.study_event_id "
				+ " LEFT JOIN study s on s.study_id = mss.study_id WHERE event_crf.status_id = 2 AND mss.study_subject_id IN ("
		.concat(digester.getQuery("findAllByStudySDV"));
		if (withoutDns) {
			sql = sql.concat(" ").concat(digester.getQuery("withoutDns"));
		}
		sql = sql.concat(" where (s.study_id = ? or s.parent_study_id = ?)  ");
		sql = sql.concat(filter.execute(""));
		if (withoutDns) {
			sql = sql.concat(" ").concat(digester.getQuery("withoutDnsTail"));
		}
		sql = sql.concat(")");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return 0;
		}
	}

	/**
	 * Method that returns the greatest label.
	 *
	 * @return int
	 */
	public int findTheGreatestLabel() {
		this.setTypesExpected();
		String sql = digester.getQuery("findAll");
		ArrayList alist = this.select(sql);
		ArrayList answer = new ArrayList();
		for (Object anAlist : alist) {
			StudySubjectBean eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(eb);
		}

		int greatestLabel = 0;
		for (Object anAnswer : answer) {
			StudySubjectBean sb = (StudySubjectBean) anAnswer;
			int labelInt;
			try {
				labelInt = Integer.parseInt(sb.getLabel());
			} catch (NumberFormatException ne) {
				labelInt = 0;
			}
			if (labelInt > greatestLabel) {
				greatestLabel = labelInt;
			}
		}
		return greatestLabel;
	}

	/**
	 * Method that returns collection of all study subjects by strOrderByColumn, blnAscendingSort and strSearchPhrase.
	 *
	 * @param strOrderByColumn
	 *            order by column value as string
	 * @param blnAscendingSort
	 *            ascending sort value as string
	 * @param strSearchPhrase
	 *            search phrase
	 * @return Collection collection of study subjects
	 */
	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * Method that returns list of ordered by label study subjects by StudyBean.
	 *
	 * @param sb
	 *            StudyBean
	 * @return ArrayList list of study subjects
	 */
	public ArrayList findAllByStudyOrderByLabel(StudyBean sb) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, sb.getId());
		variables.put(index, sb.getId());

		return executeFindAllQuery("findAllByStudyOrderByLabel", variables);
	}

	/**
	 * Method that returns list of active and ordered by label study subjects by StudyBean.
	 *
	 * @param sb
	 *            StudyBean
	 * @return ArrayList list of study subjects
	 */
	public ArrayList findAllActiveByStudyOrderByLabel(StudyBean sb) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, sb.getId());
		variables.put(index, sb.getId());

		return executeFindAllQuery("findAllActiveByStudyOrderByLabel", variables);
	}

	/**
	 * Method that returns list of study subjects by currentStudy.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @return ArrayList list of study subjects
	 */
	public ArrayList findAllWithStudyEvent(StudyBean currentStudy) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index, currentStudy.getId());

		String sql = digester.getQuery("findAllWithStudyEvent");
		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			StudySubjectBean ssb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(ssb);
		}

		return answer;
	}

	/**
	 * Method that returns list of study subjects by subjectId.
	 *
	 * @param subjectId
	 *            subject id
	 * @return ArrayList list of study subjects
	 */
	public ArrayList findAllBySubjectId(int subjectId) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index, subjectId);

		String sql = digester.getQuery("findAllBySubjectId");
		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			StudySubjectBean ssb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(ssb);
		}

		return answer;
	}

	/**
	 * Method that returns study subject by label, studyId and studySubjectId.
	 *
	 * @param label
	 *            study subject label
	 * @param studyId
	 *            study id
	 * @param studySubjectId
	 *            study subject id
	 * @return EntityBean study subject bean
	 */
	public EntityBean findAnotherBySameLabel(String label, int studyId, int studySubjectId) {
		StudySubjectBean eb = new StudySubjectBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, label);
		variables.put(index++, studyId);
		variables.put(index, studySubjectId);

		String sql = digester.getQuery("findAnotherBySameLabel");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return eb;
	}

	/**
	 * Method that returns study subject in sites by label, studyId and studySubjectId.
	 *
	 * @param label
	 *            study subject label
	 * @param studyId
	 *            study id
	 * @param studySubjectId
	 *            study subject id
	 * @return EntityBean study subject bean
	 */
	public EntityBean findAnotherBySameLabelInSites(String label, int studyId, int studySubjectId) {
		StudySubjectBean eb = new StudySubjectBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, label);
		variables.put(index++, studyId);
		variables.put(index, studySubjectId);

		String sql = digester.getQuery("findAnotherBySameLabelInSites");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return eb;
	}

	/**
	 * Method that returns study subject by id.
	 *
	 * @param id
	 *            study subject id
	 * @return EntityBean study subject bean
	 */
	public EntityBean findByPK(int id) {
		StudySubjectBean eb = new StudySubjectBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index, id);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return eb;
	}

	/**
	 * Method that returns study subject by label and study.
	 *
	 * @param label
	 *            study subject label
	 * @param study
	 *            study bean
	 * @return StudySubjectBean study subject bean
	 */
	public StudySubjectBean findByLabelAndStudy(String label, StudyBean study) {
		StudySubjectBean answer = new StudySubjectBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, label);
		variables.put(index++, study.getId());
		variables.put(index, study.getId());

		String sql = digester.getQuery("findByLabelAndStudy");

		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			answer = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return answer;
	}

	/**
	 * Method that returns study subject by label and studyId.
	 *
	 * @param label
	 *            study subject label
	 * @param studyId
	 *            study id
	 * @param id
	 *            id (it equals to zero. so it's not used)
	 * @return StudySubjectBean study subject bean
	 */
	public StudySubjectBean findSameByLabelAndStudy(String label, int studyId, int id) {
		StudySubjectBean answer = new StudySubjectBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, label);
		variables.put(index++, studyId);
		variables.put(index++, studyId);
		variables.put(index, id);

		String sql = digester.getQuery("findSameByLabelAndStudy");

		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			answer = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return answer;
	}

	/**
	 * Method that creates a new study subject.
	 *
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 * @deprecated Creates a new study subject
	 */
	@Deprecated
	public StudySubjectBean create(EntityBean eb) {
		return create((StudySubjectBean) eb, false);
	}

	/**
	 * Create a study subject (that is, enroll a subject in a study).
	 *
	 * @param sb
	 *            The study subject to create.
	 * @param withGroup
	 *            <code>true</code> if the group id has been set (primarily for use with genetic studies);
	 *            <code>false</code> otherwise.
	 * @return The study subject with id set to the insert id if the operation was successful, or 0 otherwise.
	 */
	public StudySubjectBean create(StudySubjectBean sb, boolean withGroup) {
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();

		int ind = 1;
		variables.put(ind, sb.getLabel());
		ind++;
		variables.put(ind, sb.getSubjectId());
		ind++;
		variables.put(ind, sb.getStudyId());
		ind++;
		variables.put(ind, sb.getStatus().getId());
		ind++;
		variables.put(ind, sb.getOwner().getId());
		ind++;

		Date enrollmentDate = sb.getEnrollmentDate();
		if (enrollmentDate == null) {
			nullVars.put(ind, Types.DATE);
			variables.put(ind, null);
			ind++;
		} else {
			variables.put(ind, enrollmentDate);
			ind++;
		}

		variables.put(ind, sb.getSecondaryLabel());
		ind++;

		variables.put(ind, getValidOid(sb));
		ind++;

		variables.put(ind, sb.getDynamicGroupClassId());

		executeWithPK(digester.getQuery("create"), variables, nullVars);
		if (isQuerySuccessful()) {
			sb.setId(getLatestPK());
		}

		SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(ds);
		ArrayList groupMaps = sb.getStudyGroupMaps();
		for (Object groupMap : groupMaps) {
			SubjectGroupMapBean sgmb = (SubjectGroupMapBean) groupMap;
			sgmb = (SubjectGroupMapBean) sgmdao.create(sgmb);
			if (sgmdao.isQuerySuccessful()) {
				sgmb.setId(sgmdao.getCurrentPK());
			}
		}

		return sb;
	}

	/**
	 * Method that creates a study subject with group.
	 *
	 * @param sb
	 *            StudySubjectBean
	 * @return String study subject oid
	 */
	public StudySubjectBean createWithGroup(StudySubjectBean sb) {
		return create(sb, true);
	}

	/**
	 * Method that creates a study subject without group.
	 *
	 * @param sb
	 *            StudySubjectBean
	 * @return String study subject oid
	 */
	public StudySubjectBean createWithoutGroup(StudySubjectBean sb) {
		return create(sb, false);
	}

	/**
	 * Method that returns study subject oid by StudySubjectBean.
	 *
	 * @param ssb
	 *            StudySubjectBean
	 * @return String study subject oid
	 */
	private String getOid(StudySubjectBean ssb) {

		String oid;
		try {
			oid = ssb.getOid() != null ? ssb.getOid() : ssb.getOidGenerator(ds).generateOid(ssb.getLabel());
			return oid;
		} catch (Exception e) {
			throw new RuntimeException("CANNOT GENERATE OID");
		}
	}

	private String getValidOid(StudySubjectBean ssb) {

		String oid = getOid(ssb);
		logger.info(oid);
		String oidPreRandomization = oid;
		while (findByOid(oid) != null) {
			oid = ssb.getOidGenerator(ds).randomizeOid(oidPreRandomization);
		}
		return oid;

	}

	/**
	 * Method that return study subject by oid and studyId.
	 *
	 * @param oid
	 *            study subject by oid
	 * @param studyId
	 *            study id
	 * @return StudySubjectBean study subject bean
	 */
	public StudySubjectBean findByOidAndStudy(String oid, int studyId) {
		StudySubjectBean studySubjectBean;
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, oid);
		variables.put(index++, studyId);
		variables.put(index, studyId);
		String sql = digester.getQuery("findByOidAndStudy");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			studySubjectBean = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
			return studySubjectBean;
		} else {
			return null;
		}
	}

	/**
	 * Method that return study subject by oid.
	 *
	 * @param oid
	 *            study subject by oid
	 * @return StudySubjectBean study subject bean
	 */
	public StudySubjectBean findByOid(String oid) {
		StudySubjectBean studySubjectBean;
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index, oid);
		String sql = digester.getQuery("findByOid");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
		} else {
			return null;
		}
	}

	/**
	 * Returns a part of the list of the study subjects, that matches specified filters and sorting (if there were
	 * specified some).
	 *
	 * @param currentStudy
	 *            an instance of the <code>StudyBean</code> class, which represents the study/site, to search in.
	 * @param filter
	 *            an instance of the <code>CriteriaCommand</code> interface, which is a specific filter class for
	 *            generating string representation of SQL filters, to be applied to the list of subjects.
	 * @param sort
	 *            an instance of the <code>CriteriaCommand</code> interface, which is a specific sorting class for
	 *            generating string representation of SQL sorting, to be applied to the list of subjects.
	 * @param rowStart
	 *            the start position of the part of the subject's list, to be returned.
	 * @param rowEnd
	 *            the end position of the part of the subject's list, to be returned.
	 * @return the list of instances of <code>StudySubjectBean</code> class, which match the SQL query; if no records,
	 *         matching the SQL query, were found, returns empty list.
	 */
	public ArrayList<StudySubjectBean> getWithFilterAndSort(StudyBean currentStudy, CriteriaCommand filter,
			CriteriaCommand sort, int rowStart, int rowEnd) {

		ArrayList<StudySubjectBean> studySubjects = new ArrayList<StudySubjectBean>();
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index, currentStudy.getId());

		String sql = digester.getQuery("getWithFilterAndSort");
		sql = sql + filter.execute("");

		String partialSql = sort.execute("");
		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {

			if (partialSql.equals("")) {
				sql += " ORDER BY SS.label )x)where r between " + (rowStart + 1) + " and " + rowEnd;
			} else {
				sql += ")x)where r between " + (rowStart + 1) + " and " + rowEnd;
			}

			sql = sql + partialSql;
		} else {

			sql = sql + partialSql;

			if (partialSql.equals("")) {
				sql = sql + "  ORDER BY SS.label LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
			} else {
				sql = sql + " LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
			}
		}

		ArrayList rows = this.select(sql, variables);

		for (Object row : rows) {
			StudySubjectBean studySubjectBean = (StudySubjectBean) this.getEntityFromHashMap((HashMap) row);
			studySubjects.add(studySubjectBean);
		}
		return studySubjects;
	}

	/**
	 * Method that returns count of the study subjects by study / site.
	 *
	 * @param currentStudy
	 *            StudyBean (study or site)
	 * @return Integer count of the study subjects
	 */
	public Integer getCountofStudySubjectsAtStudyOrSite(StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index, currentStudy.getId());
		String sql = digester.getQuery("getCountofStudySubjectsAtStudyOrSite");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Method that returns count of the study subjects by study.
	 *
	 * @param study
	 *            StudyBean
	 * @return Integer count of the study subjects
	 */
	public Integer getCountofStudySubjectsAtStudy(StudyBean study) {
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, study.getId());
		variables.put(index, study.getId());
		String sql = digester.getQuery("getCountofStudySubjectsAtStudy");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Method that returns count of the study subjects by currentStudy.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @return Integer count of the study subjects
	 */
	public Integer getCountofStudySubjects(StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index, currentStudy.getId());
		String sql = digester.getQuery("getCountofStudySubjects");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Method that returns count of the study subjects by currentStudy and status.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param status
	 *            Status
	 * @return Integer count of the study subjects
	 */
	public Integer getCountofStudySubjectsBasedOnStatus(StudyBean currentStudy, Status status) {
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index++, currentStudy.getId());
		variables.put(index, status.getId());
		String sql = digester.getQuery("getCountofStudySubjectsBasedOnStatus");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Method that returns count of the study subjects by ListDiscNotesSubjectFilter and currentStudy.
	 *
	 * @param filter
	 *            ListDiscNotesSubjectFilter
	 * @param currentStudy
	 *            StudyBean
	 * @return Integer count of the study subjects
	 */
	public Integer getCountWithFilter(ListDiscNotesSubjectFilter filter, StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index, currentStudy.getId());
		String sql = digester.getQuery("getCountWithFilterListDiscNotes");
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
	 * Method that returns list of study subjects by currentStudy, ListDiscNotesSubjectFilter, ListDiscNotesSubjectSort,
	 * rowStart and rowEnd.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            ListDiscNotesSubjectFilter
	 * @param sort
	 *            ListDiscNotesSubjectSort
	 * @param rowStart
	 *            rowStart
	 * @param rowEnd
	 *            rowEnd
	 * @return Integer count of the study subjects
	 */
	public ArrayList<StudySubjectBean> getWithFilterAndSort(StudyBean currentStudy, ListDiscNotesSubjectFilter filter,
			ListDiscNotesSubjectSort sort, int rowStart, int rowEnd) {
		ArrayList<StudySubjectBean> studySubjects = new ArrayList<StudySubjectBean>();
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index, currentStudy.getId());
		String sql = digester.getQuery("getWithFilterAndSortListDiscNotes");
		sql = sql + filter.execute("");

		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			sql += " )x)  where r between " + (rowStart + 1) + " and " + rowEnd;
			sql = sql + sort.execute("");
		} else {
			sql = sql + sort.execute("");
			sql = sql + " LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
		}

		ArrayList rows = this.select(sql, variables);

		for (Object row : rows) {
			StudySubjectBean studySubjectBean = (StudySubjectBean) this.getEntityFromHashMap((HashMap) row);
			studySubjects.add(studySubjectBean);
		}
		return studySubjects;
	}

	/**
	 * Method that returns count of the study subjects by FindSubjectsFilter and currentStudy.
	 *
	 * @param filter
	 *            FindSubjectsFilter
	 * @param currentStudy
	 *            StudyBean
	 * @return Integer count of the study subjects
	 */
	public Integer getCountWithFilter(FindSubjectsFilter filter, StudyBean currentStudy) {
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
	 * Method that returns list of study subjects by currentStudy, StudyAuditLogFilter, StudyAuditLogSort, rowStart and
	 * rowEnd.
	 *
	 * @param currentStudy
	 *            StudyBean
	 * @param filter
	 *            StudyAuditLogFilter
	 * @param sort
	 *            StudyAuditLogSort
	 * @param rowStart
	 *            rowStart
	 * @param rowEnd
	 *            rowEnd
	 * @return Integer count of the study subjects
	 */
	public ArrayList<StudySubjectBean> getWithFilterAndSort(StudyBean currentStudy, StudyAuditLogFilter filter,
			StudyAuditLogSort sort, int rowStart, int rowEnd) {
		ArrayList<StudySubjectBean> studySubjects = new ArrayList<StudySubjectBean>();
		setTypesExpectedFilter();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index, currentStudy.getId());
		String sql = digester.getQuery("getWithFilterAndSortAuditLog");
		sql = sql + filter.execute("");

		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			sql += " )x) where r between " + (rowStart + 1) + " and " + rowEnd;
			sql = sql + sort.execute("");
		} else {
			sql = sql + sort.execute("");
			sql = sql + " LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
		}

		// System.out.println("SQL: " + sql);
		ArrayList rows = this.select(sql, variables);

		for (Object row : rows) {
			StudySubjectBean studySubjectBean = (StudySubjectBean) this.getEntityFromHashMap((HashMap) row);
			studySubjects.add(studySubjectBean);
		}
		return studySubjects;
	}

	/**
	 * Method that returns count of the study subjects by StudyAuditLogFilter and currentStudy.
	 *
	 * @param filter
	 *            StudyAuditLogFilter
	 * @param currentStudy
	 *            StudyBean
	 * @return Integer count of the study subjects
	 */
	public Integer getCountWithFilter(StudyAuditLogFilter filter, StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, currentStudy.getId());
		variables.put(index, currentStudy.getId());
		String sql = digester.getQuery("getCountWithFilterAuditLog");
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
	 * Method that returns count of the study subjects by ListEventsForSubjectFilter and currentStudy.
	 *
	 * @param filter
	 *            ListEventsForSubjectFilter
	 * @param currentStudy
	 *            StudyBean
	 * @return Integer count of the study subjects
	 */
	public Integer getCountWithFilter(ListEventsForSubjectFilter filter, StudyBean currentStudy) {
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
	 * Method that updates a study subject item.
	 *
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean update(EntityBean eb) {
		Connection con = null;
		return update(eb, null);
	}

	/**
	 * Method that updates a study subject item. This method allows to run transactional updates for an action.
	 *
	 * @param eb
	 *            EntityBean
	 * @param con
	 *            Connection
	 * @return EntityBean
	 */
	public EntityBean update(EntityBean eb, Connection con) {
		StudySubjectBean sb = (StudySubjectBean) eb;
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();

		// UPDATE study_subject SET LABEL=?, SUBJECT_ID=?, STUDY_ID=?,
		// STATUS_ID=?, ENROLLMENT_DATE=?, DATE_UPDATED=?,
		// UPDATE_ID=?, SECONDARY_LABEL=?, DYNAMIC_GROUP_CLASS_ID=? WHERE STUDY_SUBJECT_ID=?
		int ind = 1;
		variables.put(ind, sb.getLabel());
		ind++;
		variables.put(ind, sb.getSubjectId());
		ind++;
		variables.put(ind, sb.getStudyId());
		ind++;
		variables.put(ind, sb.getStatus().getId());
		ind++;

		Date enrollmentDate = sb.getEnrollmentDate();
		if (enrollmentDate == null) {
			nullVars.put(ind, Types.DATE);
			variables.put(ind, null);
		} else {
			variables.put(ind, enrollmentDate);
		}
		ind++;

		variables.put(ind, new java.util.Date());
		ind++;
		if (sb.getUpdater() == null) {
			nullVars.put(ind, Types.INTEGER);
			variables.put(ind, null);
		} else {
			variables.put(ind, sb.getUpdater().getId());
		}
		ind++;
		variables.put(ind, sb.getSecondaryLabel());
		ind++;
		variables.put(ind, sb.getDynamicGroupClassId());
		ind++;

		Date randomizationDate = sb.getRandomizationDate();
		if (randomizationDate == null) {
			nullVars.put(ind, Types.DATE);
			variables.put(ind, null);
		} else {
			variables.put(ind, randomizationDate);
		}
		ind++;

		String randomizationResult = sb.getRandomizationResult();
		if (randomizationResult == null) {
			nullVars.put(ind, Types.VARCHAR);
			variables.put(ind, null);
		} else {
			variables.put(ind, randomizationResult);
		}
		ind++;

		variables.put(ind, sb.getId());

		String sql = digester.getQuery("update");
		if (con == null) {
			this.execute(sql, variables, nullVars);
		} else {
			this.execute(sql, variables, nullVars, con);
		}
		return sb;
	}

	/**
	 * Method that returns collection of study subjects by currentUser, ationType, strOrderByColumn, blnAscendingSort
	 * and strSearchPhrase.
	 *
	 * @param objCurrentUser
	 *            current user object
	 * @param intActionType
	 *            action type
	 * @param strOrderByColumn
	 *            order by column as string value
	 * @param blnAscendingSort
	 *            ascending sort as string value
	 * @param strSearchPhrase
	 *            string search phrase
	 * @return Collection collection of study subjects
	 * @throws OpenClinicaException
	 *             the custom OpenClinicaException
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) throws OpenClinicaException {
		return new ArrayList();
	}

	/**
	 * Method that returns collection of study subjects by currentUser and ationType.
	 *
	 * @param objCurrentUser
	 *            current user object
	 * @param intActionType
	 *            action type
	 * @return Collection collection of study subjects
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	/**
	 * Method that returns the study subject by subjectId and study.
	 *
	 * @param subjectId
	 *            subject id
	 * @param study
	 *            StudyBean
	 * @return StudySubjectBean study subject bean
	 */
	public StudySubjectBean findBySubjectIdAndStudy(int subjectId, StudyBean study) {
		StudySubjectBean answer = new StudySubjectBean();

		this.unsetTypeExpected();
		setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, subjectId);
		variables.put(index++, study.getId());
		variables.put(index, study.getId());

		String sql = digester.getQuery("findBySubjectIdAndStudy");

		ArrayList results = select(sql, variables);
		if (results.size() > 0) {
			HashMap row = (HashMap) results.get(0);
			answer = (StudySubjectBean) getEntityFromHashMap(row);
		}

		return answer;
	}

	/**
	 * Method that returns list of study subjects by studyId.
	 *
	 * @param studyId
	 *            study id
	 * @return List<StudySubjectBean> list of study subjects
	 */
	public List<StudySubjectBean> findAllByStudyId(int studyId) {
		return findAllByStudyIdAndLimit(studyId, false);
	}

	/**
	 * Method that returns list of study subjects by studyId.
	 *
	 * @param studyId
	 *            study id
	 * @return List<StudySubjectBean> list of study subjects
	 */
	public List<StudySubjectBean> findAllWithAllStatesByStudyId(int studyId) {
		List<StudySubjectBean> answer = new ArrayList();

		this.unsetTypeExpected();
		int ind = 1;
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // unique_identifier
		this.setTypeExpected(ind, TypeNames.CHAR);
		ind++; // gender
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // study_subject_id
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // label
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // secondary_label
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // subject_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // study_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // status_id

		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // enrollment_date
		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // date_created
		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // date_updated
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // owner_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // update_id
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // oc_oid
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // dynamic_group_class_id
		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // randomization_date
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // randomization_result
		this.setTypeExpected(ind, TypeNames.STRING);
		// studyName

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, studyId);
		variables.put(index, studyId);

		String sql = digester.getQuery("findAllWithAllStatesByStudyId");

		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			StudySubjectBean ssb = (StudySubjectBean) this.getEntityFromHashMap(hm);
			ssb.setUniqueIdentifier((String) hm.get("unique_identifier"));
			ssb.setStudyName((String) hm.get("name"));
			try {
				if (hm.get("gender") == null || (hm.get("gender")).equals(" ")) {
					logger.info("here");
					ssb.setGender(' ');

				} else {
					String gender = (String) hm.get("gender");
					char[] genderarr = gender.toCharArray();
					ssb.setGender(genderarr[0]);
				}
			} catch (ClassCastException ce) {
				// object type is Character
				ssb.setGender(' ');
			}

			answer.add(ssb);
		}

		return answer;
	}

	/**
	 * Method that returns list of study subjects by studyId and isLimited.
	 *
	 * @param studyId
	 *            study id
	 * @param isLimited
	 *            parameter that limits the records amount to 5
	 * @return List<StudySubjectBean> list of study subjects
	 */
	public List<StudySubjectBean> findAllByStudyIdAndLimit(int studyId, boolean isLimited) {
		List<StudySubjectBean> answer = new ArrayList();

		this.unsetTypeExpected();
		int ind = 1;
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // unique_identifier
		this.setTypeExpected(ind, TypeNames.CHAR);
		ind++; // gender
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // study_subject_id
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // label
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // secondary_label
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // subject_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // study_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // status_id

		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // enrollment_date
		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // date_created
		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // date_updated
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // owner_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // update_id
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // oc_oid
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // dynamic_group_class_id
		this.setTypeExpected(ind, TypeNames.DATE);
		ind++; // randomization_date
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // randomization_result
		this.setTypeExpected(ind, TypeNames.STRING);
		// studyName

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, studyId);
		variables.put(index, studyId);

		String sql;
		if (isLimited) {
			sql = digester.getQuery("findAllByStudyIdAndLimit");
		} else {
			sql = digester.getQuery("findAllByStudyId");
		}
		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			StudySubjectBean ssb = (StudySubjectBean) this.getEntityFromHashMap(hm);
			ssb.setUniqueIdentifier((String) hm.get("unique_identifier"));
			ssb.setStudyName((String) hm.get("name"));
			try {
				if (hm.get("gender") == null || (hm.get("gender")).equals(" ")) {
					logger.info("here");
					ssb.setGender(' ');

				} else {
					String gender = (String) hm.get("gender");
					char[] genderarr = gender.toCharArray();
					ssb.setGender(genderarr[0]);
				}
			} catch (ClassCastException ce) {
				// object type is Character
				ssb.setGender(' ');
			}

			answer.add(ssb);
		}

		return answer;
	}

	/**
	 * Method that returns the study subject ids as string by study id.
	 *
	 * @param studyIds
	 *            study ids as string
	 * @return String study subject ids
	 */
	public String findStudySubjectIdsByStudyIds(String studyIds) {
		String studySubjectIds = "";
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.STRING);
		ArrayList alist = this
				.select("select study_subject_id from study_subject where study_id in (" + studyIds + ")");
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			studySubjectIds += hm.get("study_subject_id") + ",";
		}
		studySubjectIds = studySubjectIds.endsWith(",")
				? studySubjectIds.substring(0, studySubjectIds.length() - 1)
				: studySubjectIds;
		return studySubjectIds;
	}

	/**
	 * Method that returns the next study subject label.
	 *
	 * @param studyBean
	 *            study from which generation format will be taken.
	 * @return String study subject label
	 */
	public String findNextLabel(StudyBean studyBean) {

		String siteLabel = studyBean.getIdentifier();
		String prefixParam = studyBean.getStudyParameterConfig().getAutoGeneratedPrefix();
		String separatorParam = studyBean.getStudyParameterConfig().getAutoGeneratedSeparator();
		String suffixParam = studyBean.getStudyParameterConfig().getAutoGeneratedSuffix();
		int suffixLength = suffixParam == null || suffixParam.isEmpty() ? 0 : Integer.parseInt(suffixParam);
		String prefix = prefixParam.equals("SiteID") ? siteLabel : prefixParam;
		Integer greatestNum = getCountofStudySubjects(studyBean);

		if (greatestNum == null) {
			greatestNum = 0;
		}
		greatestNum += 1;
		String numericLabel = Integer.toString(greatestNum);
		for (int i = 0; i < suffixLength; i++) {
			if (numericLabel.length() < suffixLength) {
				numericLabel = "0" + numericLabel;
			}
		}
		return prefix + separatorParam + numericLabel;
	}

	/**
	 * Returns a number of study subjects (no matter what status subject has currently), which were enrolled at a
	 * specific study/site and are randomized to a specific dynamic group class.
	 *
	 * @param studyId
	 *            the study/site id, to search on.
	 * @param dynamicGroupClassId
	 *            the dynamic group class id, to search on.
	 * @return a number of study subjects, which match the SQL query.
	 */
	public int getCountOfStudySubjectsByStudyIdAndDynamicGroupClassId(int studyId, int dynamicGroupClassId) {

		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index, TypeNames.INT); // counter

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		index = 1;
		queryParameters.put(index++, studyId);
		queryParameters.put(index++, studyId);
		queryParameters.put(index, dynamicGroupClassId);

		List<HashMap<String, Object>> recordsFromDB = this.select(
				digester.getQuery("getCountOfStudySubjectsByStudyIdAndDynamicGroupClassId"), queryParameters);

		return (Integer) recordsFromDB.get(0).get("count");
	}
}
