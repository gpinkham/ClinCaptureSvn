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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class StudySubjectDAO extends AuditableEntityDAO {

	public void setQueryNames() {
		findAllByStudyName = "findAllByStudy";
		findByPKAndStudyName = "findByPKAndStudy";
		getCurrentPKName = "getCurrentPK";

	}

	public StudySubjectDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	public StudySubjectDAO(DataSource ds, Connection connection) {
		super(ds, connection);
		setQueryNames();
	}

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

	public void setDNTypesExpected() {

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
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; //
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; //
		this.setTypeExpected(ind, TypeNames.STRING);
	}

	/**
	 * <p>
	 * getEntityFromHashMap, the method that gets the object from the database query.
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

	public ArrayList getGroupByStudySubject(int studySubjectId, int studyId, int parentStudyId) {

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		variables.put(2, studyId);
		variables.put(3, parentStudyId);

		return executeFindAllQuery("getGroupByStudySubject", variables);
	}

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

	public ArrayList findAllByStudySDV(int studyId, int parentStudyId, StudySubjectSDVFilter filter,
			StudySubjectSDVSort sort, int rowStart, int rowEnd) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		ArrayList<StudySubjectBean> studySubjects = new ArrayList<StudySubjectBean>();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);
		String sql = digester.getQuery("findAllByStudySDV");
		sql = sql + filter.execute("");

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

	public Integer countByLabel(String label) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, label);
		variables.put(2, label);
		variables.put(3, label);

		ArrayList alist = this.select(digester.getQuery("countByLabel"), variables);
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return 0;
		}
	}

	public int countAllByStudySDV(int studyId, int parentStudyId, StudySubjectSDVFilter filter) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);

		String sql = digester.getQuery("countAllByStudySDV");
		sql += filter.execute("");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return 0;
		}
	}

	public boolean allowSDVSubject(int studySubjectId, int studyId, int parentStudyId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.BOOL);

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		variables.put(2, parentStudyId);
		variables.put(3, studyId);

		String sql = digester.getQuery("allowSDVSubject");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Boolean) ((HashMap) it.next()).get("allow");
		} else {
			return false;
		}
	}

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

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public ArrayList findAllByStudyOrderByLabel(StudyBean sb) {
		HashMap variables = new HashMap();
		variables.put(1, sb.getId());
		variables.put(2, sb.getId());

		return executeFindAllQuery("findAllByStudyOrderByLabel", variables);
	}

	public ArrayList findAllActiveByStudyOrderByLabel(StudyBean sb) {
		HashMap variables = new HashMap();
		variables.put(1, sb.getId());
		variables.put(2, sb.getId());

		return executeFindAllQuery("findAllActiveByStudyOrderByLabel", variables);
	}

	public ArrayList findAllWithStudyEvent(StudyBean currentStudy) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());

		String sql = digester.getQuery("findAllWithStudyEvent");
		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			StudySubjectBean ssb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(ssb);
		}

		return answer;
	}

	public ArrayList findAllBySubjectId(int subjectId) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, subjectId);

		String sql = digester.getQuery("findAllBySubjectId");
		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			StudySubjectBean ssb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(ssb);
		}

		return answer;
	}

	public EntityBean findAnotherBySameLabel(String label, int studyId, int studySubjectId) {
		StudySubjectBean eb = new StudySubjectBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, label);
		variables.put(2, studyId);
		variables.put(3, studySubjectId);

		String sql = digester.getQuery("findAnotherBySameLabel");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return eb;
	}

	public EntityBean findAnotherBySameLabelInSites(String label, int studyId, int studySubjectId) {
		StudySubjectBean eb = new StudySubjectBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, label);
		variables.put(2, studyId);
		variables.put(3, studySubjectId);

		String sql = digester.getQuery("findAnotherBySameLabelInSites");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return eb;
	}

	public EntityBean findByPK(int ID) {
		StudySubjectBean eb = new StudySubjectBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, ID);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return eb;
	}

	public StudySubjectBean findByLabelAndStudy(String label, StudyBean study) {
		StudySubjectBean answer = new StudySubjectBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, label);
		variables.put(2, study.getId());
		variables.put(3, study.getId());

		String sql = digester.getQuery("findByLabelAndStudy");

		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			answer = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return answer;
	}

	/**
	 * Finds a study subject which has the same label provided in the same study
	 * 
	 * @param label
	 *            String
	 * @param studyId
	 *            int
	 * @param id
	 *            int
	 * @return StudySubjectBean
	 */
	public StudySubjectBean findSameByLabelAndStudy(String label, int studyId, int id) {
		StudySubjectBean answer = new StudySubjectBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, label);
		variables.put(2, studyId);
		variables.put(3, studyId);
		variables.put(4, id);

		String sql = digester.getQuery("findSameByLabelAndStudy");

		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			answer = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return answer;
	}

	/**
	 * @deprecated Creates a new studysubject
	 */
	@Deprecated
	public EntityBean create(EntityBean eb) {
		StudySubjectBean sb = (StudySubjectBean) eb;
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();

		// INSERT INTO study_subject
		// (LABEL, SUBJECT_ID, STUDY_ID, STATUS_ID,
		// DATE_CREATED, OWNER_ID, ENROLLMENT_DATE,SECONDARY_LABEL, DYNAMIC_GROUP_CLASS_ID)
		// VALUES (?,?,?,?,NOW(),?,?,?,?)

		int ind = 1;
		variables.put(ind, sb.getLabel());
		ind++;
		variables.put(ind, sb.getSubjectId());
		ind++;
		variables.put(ind, sb.getStudyId());
		ind++;
		variables.put(ind, sb.getStatus().getId());
		ind++;
		variables.put(ind, sb.getOwnerId());
		ind++;
		if (sb.getEnrollmentDate() == null) {
			nullVars.put(ind, Types.DATE);
			variables.put(ind, null);
			ind++;
		} else {
			variables.put(ind, sb.getEnrollmentDate());
			ind++;
		}
		variables.put(ind, sb.getSecondaryLabel());
		ind++;
		variables.put(ind, sb.getDynamicGroupClassId());
		this.execute(digester.getQuery("create"), variables, nullVars);

		if (isQuerySuccessful()) {
			sb.setId(getCurrentPK());
		}

		return sb;
	}

	/**
	 * Create a study subject (that is, enroll a subject in a study).
	 * 
	 * @param sb
	 *            The study subject to create.
	 * @param withGroup
	 *            <code>true</code> if the group id has been set (primarily for use with genetic studies);
	 *            <code>false</false> otherwise.
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

		this.executeWithPK(digester.getQuery("create"), variables, nullVars);
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

	public StudySubjectBean createWithGroup(StudySubjectBean sb) {
		return create(sb, true);
	}

	public StudySubjectBean createWithoutGroup(StudySubjectBean sb) {
		return create(sb, false);
	}

	/**
	 * Creates a valid OID for the StudySubject
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

	public StudySubjectBean findByOidAndStudy(String oid, int studyId) {
		StudySubjectBean studySubjectBean;
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, oid);
		variables.put(2, studyId);
		variables.put(3, studyId);
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

	public StudySubjectBean findByOid(String oid) {
		StudySubjectBean studySubjectBean;
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, oid);
		String sql = digester.getQuery("findByOid");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
		} else {
			return null;
		}
	}

	public ArrayList<StudySubjectBean> getWithFilterAndSort(StudyBean currentStudy, FindSubjectsFilter filter,
			FindSubjectsSort sort, int rowStart, int rowEnd) {
		ArrayList<StudySubjectBean> studySubjects = new ArrayList<StudySubjectBean>();
		setTypesExpected();
		String partialSql;
		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
		String sql = digester.getQuery("getWithFilterAndSort");
		sql = sql + filter.execute("");
		// Order by Clause for the defect id 0005480

		partialSql = sort.execute("");
		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			if (partialSql.equals(""))
				sql += " ORDER BY SS.label )x)where r between " + (rowStart + 1) + " and " + rowEnd;
			else
				sql += ")x)where r between " + (rowStart + 1) + " and " + rowEnd;

			sql = sql + partialSql;
		} else {

			sql = sql + partialSql;
			if (partialSql.equals(""))
				sql = sql + "  ORDER BY SS.label LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
			else
				sql = sql + " LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
		}

		ArrayList rows = this.select(sql, variables);

		for (Object row : rows) {
			StudySubjectBean studySubjectBean = (StudySubjectBean) this.getEntityFromHashMap((HashMap) row);
			studySubjects.add(studySubjectBean);
		}
		return studySubjects;
	}

	public Integer getCountofStudySubjectsAtStudyOrSite(StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		String sql = digester.getQuery("getCountofStudySubjectsAtStudyOrSite");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	public Integer getCountofStudySubjectsAtStudy(StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
		String sql = digester.getQuery("getCountofStudySubjectsAtStudy");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	public Integer getCountofStudySubjects(StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
		String sql = digester.getQuery("getCountofStudySubjects");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	public Integer getCountofStudySubjectsBasedOnStatus(StudyBean currentStudy, Status status) {
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
		variables.put(3, status.getId());
		String sql = digester.getQuery("getCountofStudySubjectsBasedOnStatus");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	public Integer getCountWithFilter(ListDiscNotesSubjectFilter filter, StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
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

	public ArrayList<StudySubjectBean> getWithFilterAndSort(StudyBean currentStudy, ListDiscNotesSubjectFilter filter,
			ListDiscNotesSubjectSort sort, int rowStart, int rowEnd) {
		ArrayList<StudySubjectBean> studySubjects = new ArrayList<StudySubjectBean>();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
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

	public Integer getCountWithFilter(FindSubjectsFilter filter, StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
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

	public ArrayList<StudySubjectBean> getWithFilterAndSort(StudyBean currentStudy, StudyAuditLogFilter filter,
			StudyAuditLogSort sort, int rowStart, int rowEnd) {
		ArrayList<StudySubjectBean> studySubjects = new ArrayList<StudySubjectBean>();
		setTypesExpectedFilter();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
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

	public Integer getCountWithFilter(StudyAuditLogFilter filter, StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
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

	public ArrayList<StudySubjectBean> getWithFilterAndSort(StudyBean currentStudy, ListEventsForSubjectFilter filter,
			ListEventsForSubjectSort sort, int rowStart, int rowEnd) {
		ArrayList<StudySubjectBean> studySubjects = new ArrayList<StudySubjectBean>();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
		String sql = digester.getQuery("getWithFilterAndSort");
		sql = sql + filter.execute("");

		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			sql += ")x) where r between " + (rowStart + 1) + " and " + rowEnd + " ";
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

	public Integer getCountWithFilter(ListEventsForSubjectFilter filter, StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
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
	 * Updates a StudySubject updated clinovo #121 - 12/19/2012
	 */
	public EntityBean update(EntityBean eb) {
		Connection con = null;
		return update(eb, null);
	}

	/* this function allows to run transactional updates for an action */
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
			ind++;
		} else {
			variables.put(ind, enrollmentDate);
			ind++;
		}

		variables.put(ind, new java.util.Date());
		ind++;
		variables.put(ind, sb.getUpdater().getId());
		ind++;
		variables.put(ind, sb.getSecondaryLabel());
		ind++;
		variables.put(ind, sb.getDynamicGroupClassId());
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

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	public StudySubjectBean findBySubjectIdAndStudy(int subjectId, StudyBean study) {
		StudySubjectBean answer = new StudySubjectBean();

		this.unsetTypeExpected();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, subjectId);
		variables.put(2, study.getId());
		variables.put(3, study.getId());

		String sql = digester.getQuery("findBySubjectIdAndStudy");

		ArrayList results = select(sql, variables);
		if (results.size() > 0) {
			HashMap row = (HashMap) results.get(0);
			answer = (StudySubjectBean) getEntityFromHashMap(row);
		}

		return answer;
	}

	public ArrayList findAllByStudyId(int studyId) {
		return findAllByStudyIdAndLimit(studyId, false);
	}

	public ArrayList findAllWithAllStatesByStudyId(int studyId) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();
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
		ind++; // secondary_label
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // dynamic_group_class_id
		this.setTypeExpected(ind, TypeNames.STRING);
		// studyName

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);

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

	public ArrayList findAllByStudyIdAndLimit(int studyId, boolean isLimited) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();
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
		ind++; // secondary_label
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // dynamic_group_class_id
		this.setTypeExpected(ind, TypeNames.STRING);
		// studyName

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);

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
		studySubjectIds = studySubjectIds.endsWith(",") ? studySubjectIds.substring(0, studySubjectIds.length() - 1)
				: studySubjectIds;
		return studySubjectIds;
	}

	public String findNextLabel(String SiteId) {
		this.setTypesExpected();
		String sql = digester.getQuery("findAll");
		ArrayList alist = this.select(sql);
		ArrayList answer = new ArrayList();

		for (Object anAlist : alist) {
			StudySubjectBean eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(eb);
		}

		int greatestNum = 0;
		for (Object anAnswer : answer) {
			StudySubjectBean sb = (StudySubjectBean) anAnswer;
			String currentLabel = sb.getLabel();
			int delimiterposition = currentLabel.lastIndexOf('-');
			if (delimiterposition > 0) {
				if (currentLabel.substring(0, delimiterposition).equals(SiteId)) {
					int labelInt;
					try {
						labelInt = Integer.parseInt(currentLabel.substring(currentLabel.lastIndexOf('-') + 1));
					} catch (NumberFormatException ne) {
						labelInt = 0;
					}
					if (labelInt > greatestNum) {
						greatestNum = labelInt;
					}
				}
			}
		}
		greatestNum += 1;

		String numericLabel;
		if (greatestNum < 10) {
			numericLabel = "00" + greatestNum;
		} else if (greatestNum < 100) {
			numericLabel = "0" + greatestNum;
		} else {
			numericLabel = Integer.toString(greatestNum);
		}
		return SiteId + "-" + numericLabel;
	}
}
