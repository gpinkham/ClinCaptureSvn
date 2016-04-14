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

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
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
import java.util.Locale;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SubjectGroupMapDAO extends AuditableEntityDAO {

	private void setQueryNames() {
		this.getCurrentPKName = "getCurrentPK";
	}

	public SubjectGroupMapDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	public SubjectGroupMapDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
		setQueryNames();
	}

	// This constructor sets up the Locale for JUnit tests; see the locale
	// member variable in EntityDAO, and its initializeI18nStrings() method
	public SubjectGroupMapDAO(DataSource ds, DAODigester digester, Locale locale) {

		this(ds, digester);
		this.locale = locale;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_SUBJECTGROUPMAP;
	}

	@Override
	public void setTypesExpected() {
		// subject_group_map_id serial NOT NULL,
		// study_group_class_id numeric,
		// study_subject_id numeric,
		// study_group_id numeric,

		// status_id numeric,
		// owner_id numeric,
		// date_created date,
		// date_updated date,

		// update_id numeric,
		// notes varchar(255),
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.INT);
		this.setTypeExpected(4, TypeNames.INT);

		this.setTypeExpected(5, TypeNames.INT);
		this.setTypeExpected(6, TypeNames.INT);
		this.setTypeExpected(7, TypeNames.TIMESTAMP);
		this.setTypeExpected(8, TypeNames.TIMESTAMP);

		this.setTypeExpected(9, TypeNames.INT);
		this.setTypeExpected(10, TypeNames.STRING);

	}

	/**
	 * <p>
	 * getEntityFromHashMap, the method that gets the object from the database query.
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		SubjectGroupMapBean eb = new SubjectGroupMapBean();
		super.setEntityAuditInformation(eb, hm);
		// subject_group_map_id serial NOT NULL,
		// study_group_class_id numeric,
		// study_subject_id numeric,
		// study_group_id numeric,
		// status_id numeric,
		// owner_id numeric,
		// date_created date,
		// date_updated date,
		// update_id numeric,
		// notes varchar(255),
		eb.setId(((Integer) hm.get("subject_group_map_id")).intValue());
		eb.setStudyGroupId(((Integer) hm.get("study_group_id")).intValue());
		eb.setStudySubjectId(((Integer) hm.get("study_subject_id")).intValue());
		eb.setStudyGroupClassId(((Integer) hm.get("study_group_class_id")).intValue());
		eb.setNotes((String) hm.get("notes"));

		return eb;
	}

	public Collection findAll() {
		this.setTypesExpected();
		List alist = this.select(digester.getQuery("findAll"));
		List al = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			SubjectGroupMapBean eb = (SubjectGroupMapBean) this.getEntityFromHashMap((HashMap) it.next());
			al.add(eb);
		}
		return al;
	}

	public Collection findAllByStudySubject(int studySubjectId) {
		setTypesExpected();
		this.setTypeExpected(11, TypeNames.STRING);
		this.setTypeExpected(12, TypeNames.STRING);
		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		List alist = this.select(digester.getQuery("findAllByStudySubject"), variables);
		List al = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			HashMap hm = (HashMap) it.next();
			SubjectGroupMapBean eb = (SubjectGroupMapBean) this.getEntityFromHashMap(hm);
			eb.setStudyGroupName(((String) hm.get("group_name")));
			eb.setGroupClassName(((String) hm.get("class_name")));
			al.add(eb);
		}
		return al;
	}

	public SubjectGroupMapBean findAllByStudySubjectAndStudyGroupClass(int studySubjectId, int studyGroupClassId) {
		setTypesExpected();
		this.setTypeExpected(11, TypeNames.STRING);
		this.setTypeExpected(12, TypeNames.STRING);
		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		variables.put(2, studyGroupClassId);
		List alist = this.select(digester.getQuery("findByStudySubjectAndStudyGroupClass"), variables);
		SubjectGroupMapBean eb = null;
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			HashMap hm = (HashMap) it.next();
			eb = (SubjectGroupMapBean) this.getEntityFromHashMap(hm);
			eb.setStudyGroupName(((String) hm.get("group_name")));
			eb.setGroupClassName(((String) hm.get("class_name")));
		}
		return eb;
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		List al = new ArrayList();

		return al;
	}

	public SubjectGroupMapBean findByPK(int ID) {
		SubjectGroupMapBean eb = new SubjectGroupMapBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, ID);

		String sql = digester.getQuery("findByPK");
		List alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (SubjectGroupMapBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return eb;
	}

	/**
	 * Creates a new subject
	 */
	public EntityBean create(EntityBean eb) {
		SubjectGroupMapBean sb = (SubjectGroupMapBean) eb;
		HashMap variables = new HashMap();
		// INSERT INTO SUBJECT_GROUP_MAP (study_group_class_id,
		// study_subject_id, study_group_id,
		// status_id, owner_id,date_created,
		// notes) VALUES (?,?,?,?,?,NOW(),?)
		variables.put(1, sb.getStudyGroupClassId());
		variables.put(2, sb.getStudySubjectId());
		variables.put(3, sb.getStudyGroupId());
		variables.put(4, sb.getStatus().getId());
		variables.put(5, sb.getOwner().getId());
		variables.put(6, sb.getNotes());
		// DATE_CREATED is now()

		executeWithPK(digester.getQuery("create"), variables);
		if (isQuerySuccessful()) {
			eb.setId(getLatestPK());
		}
		return sb;
	}

	/**
	 * <b>update </b>, the method that returns an updated subject bean after it updates the database.
	 * 
	 * @return sb, an updated study bean.
	 */
	public EntityBean update(EntityBean eb) {
		SubjectGroupMapBean sb = (SubjectGroupMapBean) eb;
		HashMap variables = new HashMap();
		// UPDATE SUBJECT_GROUP_MAP SET STUDY_GROUP_CLASS_ID=?,
		// STUDY_SUBJECT_ID=?,STUDY_GROUP_ID=?,
		// STATUS_ID=?,DATE_UPDATED=?, UPDATE_ID=? , notes = ?
		// WHERE SUBJECT_GROUP_MAP_ID=?
		variables.put(1, sb.getStudyGroupClassId());
		variables.put(2, sb.getStudySubjectId());
		variables.put(3, sb.getStudyGroupId());
		variables.put(4, sb.getStatus().getId());

		variables.put(5, new Timestamp(new Date().getTime()));
		variables.put(6, sb.getUpdater().getId());
		variables.put(8, sb.getId());
		variables.put(7, sb.getNotes());

		String sql = digester.getQuery("update");
		this.execute(sql, variables);

		return sb;
	}

	public List<SubjectGroupMapBean> findAllByStudyGroupClassAndGroup(int studyGroupClassId, int studyGroupId) {
		setTypesExpected();
		this.setTypeExpected(11, TypeNames.STRING);
		HashMap variables = new HashMap();
		variables.put(1, studyGroupClassId);
		variables.put(2, studyGroupId);
		List alist = this.select(digester.getQuery("findAllByStudyGroupClassAndGroup"), variables);
		List<SubjectGroupMapBean> al = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			HashMap hm = (HashMap) it.next();
			SubjectGroupMapBean eb = (SubjectGroupMapBean) this.getEntityFromHashMap(hm);
			eb.setSubjectLabel(((String) hm.get("label")));
			al.add(eb);
		}
		return al;
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		List al = new ArrayList();

		return al;
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		ArrayList al = new ArrayList();

		return al;
	}

	public List<SubjectGroupMapBean> findAllByStudyGroupId(int studyGroupId) {
		HashMap variables = new HashMap();
		variables.put(1, studyGroupId);

		return executeFindAllQuery("findAllByStudyGroupId", variables);
	}

	public List<SubjectGroupMapBean> findAllByStudyGroupClassId(int studyGroupClassId) {
		HashMap variables = new HashMap();
		variables.put(1, studyGroupClassId);

		return executeFindAllQuery("findAllByStudyGroupClassId", variables);
	}

	public void deleteTestGroupMap(int id) {
		HashMap variables = new HashMap();
		variables.put(1, id);
		this.execute(digester.getQuery("deleteTestGroupMap"), variables);
	}

	/**
	 * Deletes all SubjectGroupMapBean by StudyGroupClassId id.
	 *
	 * @param groupId the study group class id, to search on.
	 * if no records, matching the SQL query, were found, do nothing.
	 */
	public void deleteAllByStudyGroupClassId(int groupId) {
		HashMap variables = new HashMap();
		variables.put(1, groupId);
		this.execute(digester.getQuery("deleteAllByStudyGroupClassId"), variables);
	}
}
