/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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
import org.akaza.openclinica.bean.core.GroupClassType;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>StudyGroupClassDAO</code> class is a member of DAO layer, extends <code>AuditableEntityDAO</code> class.
 * <p/>
 * This class implements all the required data access logic for bean class <code>StudyGroupClassBean</code>.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StudyGroupClassDAO extends AuditableEntityDAO {

	private static final int START_INDEX_FOR_ADDITIONAL_ATTRIBUTES_TO_BE_RETRIEVED_FROM_THE_DB = 13;

	protected void setQueryNames() {

		findAllByStudyName = "findAllByStudy";
		findByPKAndStudyName = "findByPKAndStudy";
		getNextPKName = "getNextPK";
		getCurrentPKName = "getCurrentPrimaryKey";
	}

	/**
	 * <code>StudyGroupClassDAO</code> class constructor.
	 *
	 * @param dataSource an instance of <code>javax.sql.DataSource</code> class, which represents the data source.
	 */
	public StudyGroupClassDAO(DataSource dataSource) {

		super(dataSource);
		setQueryNames();
	}

	/**
	 * <code>StudyGroupClassDAO</code> class constructor.
	 *
	 * @param dataSource an instance of <code>javax.sql.DataSource</code> class, which represents the data source.
	 * @param digester   an instance of <code>DAODigester</code> class, which contains all of the named SQL queries,
	 *                   which are specified for DAO class <code>StudyGroupClassDAO</code>.
	 */
	public StudyGroupClassDAO(DataSource dataSource, DAODigester digester) {

		super(dataSource);
		this.digester = digester;
		setQueryNames();
	}

	@Override
	protected void setDigesterName() {

		digesterName = SQLFactory.getInstance().DAO_STUDYGROUPCLASS;
	}

	@Override
	public void setTypesExpected() {

		this.unsetTypeExpected();

		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT);    	// study_group_class_id int4
		this.setTypeExpected(index++, TypeNames.STRING);    // name varchar(30)
		this.setTypeExpected(index++, TypeNames.INT);    	// study_id numeric
		this.setTypeExpected(index++, TypeNames.INT);    	// owner_id numeric
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);    	// date_created date
		this.setTypeExpected(index++, TypeNames.INT);    	// group_class_type_id numeric
		this.setTypeExpected(index++, TypeNames.INT);    	// status_id numeric
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);    	// date_updated date
		this.setTypeExpected(index++, TypeNames.INT);   	// update_id numeric
		this.setTypeExpected(index++, TypeNames.STRING);	// subject_assignment varchar(30)
		this.setTypeExpected(index++, TypeNames.BOOL);		// is_default boolean
		this.setTypeExpected(index, TypeNames.INT);			// dynamic_ordinal numeric
	}

	public StudyGroupClassBean getEntityFromHashMap(HashMap record) {

		StudyGroupClassBean studyGroupClassBean = new StudyGroupClassBean();
		super.setEntityAuditInformation(studyGroupClassBean, record);

		studyGroupClassBean.setId((Integer) record.get("study_group_class_id"));
		studyGroupClassBean.setName((String) record.get("name"));
		studyGroupClassBean.setStudyId((Integer) record.get("study_id"));
		studyGroupClassBean.setGroupClassTypeId((Integer) record.get("group_class_type_id"));
		String classTypeName = GroupClassType.get((Integer) record.get("group_class_type_id")).getName();
		studyGroupClassBean.setGroupClassTypeName(classTypeName);
		studyGroupClassBean.setSubjectAssignment((String) record.get("subject_assignment"));
		studyGroupClassBean.setDefault((Boolean) record.get("is_default"));
		studyGroupClassBean.setDynamicOrdinal((Integer) record.get("dynamic_ordinal"));

		return studyGroupClassBean;
	}

	public List<StudyGroupClassBean> findAll() {

		this.setTypesExpected();

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findAll"));

		List<StudyGroupClassBean> resultSetOfBeans = new ArrayList<StudyGroupClassBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans;
	}

	@Override
	public ArrayList<StudyGroupClassBean> findAllByStudy(StudyBean study) {

		this.setTypesExpected();

		int index = START_INDEX_FOR_ADDITIONAL_ATTRIBUTES_TO_BE_RETRIEVED_FROM_THE_DB;
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index, TypeNames.STRING);

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		index = 1;
		queryParameters.put(index++, study.getId());
		queryParameters.put(index, study.getId());

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findAllByStudy"), queryParameters);

		ArrayList<StudyGroupClassBean> resultSetOfBeans = new ArrayList<StudyGroupClassBean>();

		for (HashMap<String, Object> record: recordsFromDB) {

			StudyGroupClassBean group = this.getEntityFromHashMap(record);
			group.setStudyName((String) record.get("study_name"));
			group.setGroupClassTypeName((String) record.get("type_name"));
			resultSetOfBeans.add(group);
		}

		return resultSetOfBeans;
	}

	@Override
	public ArrayList<StudyGroupClassBean> findAllActiveByStudy(StudyBean study) {

		return findAllActiveByStudyId(study.getId(), false);
	}

	/**
	 * Returns a list of study group classes with status Available only from a specific study;
	 * the list can contains both dynamic (including the default one) and regular study group classes
	 * or regular study group classes only, based on <code>filterOnDynamic</code> parameter value.
	 * The list is ordered by the group class name in <code>ASC</code> mode.
	 *
	 * @param studyId         the study id, to search on.
	 * @param filterOnDynamic if <code>true</code> - dynamic study group classes will be excluded from search;
	 *                        otherwise dynamic study group classes (including the default one) with status Available
	 *                        will be included to the result list.
	 * @return the list of instances of <code>StudyGroupClassBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns an empty list.
	 */
	public ArrayList<StudyGroupClassBean> findAllActiveByStudyId(int studyId, boolean filterOnDynamic) {

		this.setTypesExpected();

		int index = START_INDEX_FOR_ADDITIONAL_ATTRIBUTES_TO_BE_RETRIEVED_FROM_THE_DB;
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index, TypeNames.STRING);

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		index = 1;
		queryParameters.put(index++, studyId);
		queryParameters.put(index, studyId);

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findAllActiveByStudy"), queryParameters);

		ArrayList<StudyGroupClassBean> resultSetOfBeans = new ArrayList<StudyGroupClassBean>();

		for (HashMap<String, Object> record: recordsFromDB) {

			StudyGroupClassBean group = this.getEntityFromHashMap(record);
			group.setStudyName((String) record.get("study_name"));
			group.setGroupClassTypeName((String) record.get("type_name"));
			group.setSelected(false);

			if (!(filterOnDynamic && group.getGroupClassTypeId() == GroupClassType.DYNAMIC.getId())) {
				resultSetOfBeans.add(group);
			}
		}

		return resultSetOfBeans;
	}

	/**
	 * Returns a list of dynamic study group classes (including the default one) with status Available only
	 * from a specific study.
	 * The list is ordered by the group class name in <code>ASC</code> mode.
	 *
	 * @param studyId the study id, to search on.
	 * @return the list of instances of <code>StudyGroupClassBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns an empty list.
	 */
	public List<StudyGroupClassBean> findAllActiveDynamicGroupsByStudyId(int studyId) {

		List<StudyGroupClassBean> tmpSetOfBeans = this.findAllActiveByStudyId(studyId, false);

		List<StudyGroupClassBean> resultSetOfBeans = new ArrayList<StudyGroupClassBean>();

		for (StudyGroupClassBean group : tmpSetOfBeans) {

			if (group.getGroupClassTypeId() == GroupClassType.DYNAMIC.getId()) {
				resultSetOfBeans.add(group);
			}
		}

		return resultSetOfBeans;
	}

	/**
	 * Searches for a study group class by a specific group class name in a specific study.
	 *
	 * @param studyGroupName the group class name, to search on.
	 * @param studyId        the study id, to search on.
	 * @return the instance of the <code>StudyGroupClassBean</code> class, that matches the SQL query;
	 * if such a study group class record was not found, returns an empty instance
	 * of the <code>StudyGroupClassBean</code> class.
	 */
	public StudyGroupClassBean findByNameAndStudyId(String studyGroupName, int studyId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index++, studyGroupName);
		queryParameters.put(index, studyId);

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findByNameAndStudyId"), queryParameters);

		List<StudyGroupClassBean> resultSetOfBeans = new ArrayList<StudyGroupClassBean>();

		for (HashMap<String, Object> record: recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans.isEmpty() ? new StudyGroupClassBean() : resultSetOfBeans.get(0);
	}

	public List<StudyGroupClassBean> findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {

		return new ArrayList<StudyGroupClassBean>();
	}

	public StudyGroupClassBean findByPK(int id) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index, id);

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findByPK"), queryParameters);

		List<StudyGroupClassBean> resultSetOfBeans = new ArrayList<StudyGroupClassBean>();

		for (HashMap<String, Object> record: recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans.isEmpty() ? new StudyGroupClassBean() : resultSetOfBeans.get(0);
	}

	/**
	 * Searches for the default dynamic study group class in a specific study.
	 *
	 * @param studyId the study id, to search on.
	 * @return the instance of the <code>StudyGroupClassBean</code> class, that matches the SQL query;
	 * if such a study group class record was not found, returns an empty instance
	 * of the <code>StudyGroupClassBean</code> class.
	 */
	public StudyGroupClassBean findDefaultByStudyId(int studyId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index, studyId);

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findDefaultByStudyId"), queryParameters);

		List<StudyGroupClassBean> resultSetOfBeans = new ArrayList<StudyGroupClassBean>();

		for (HashMap<String, Object> record: recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans.isEmpty() ? new StudyGroupClassBean() : resultSetOfBeans.get(0);
	}

	/**
	 * Discovers if the given study event definition is assigned to any of the dynamic study group classes
	 * with status Available, and returns that dynamic study group class bean.
	 *
	 * @param studyEventDefinitionId the study event definition id, to search on.
	 * @return the instance of the <code>StudyGroupClassBean</code> class, that matches the SQL query;
	 * if such a study group class record was not found, returns an empty instance
	 * of the <code>StudyGroupClassBean</code> class.
	 */
	public StudyGroupClassBean findAvailableDynamicGroupByStudyEventDefinitionId(int studyEventDefinitionId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index, studyEventDefinitionId);

		List<HashMap<String, Object>> recordsFromDB =
				this.select(digester.getQuery("findAvailableDynamicGroupByStudyEventDefinitionId"), queryParameters);

		List<StudyGroupClassBean> resultSetOfBeans = new ArrayList<StudyGroupClassBean>();

		for (HashMap<String, Object> record: recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans.isEmpty() ? new StudyGroupClassBean() : resultSetOfBeans.get(0);
	}

	public StudyGroupClassBean create(EntityBean eb) {

		StudyGroupClassBean groupClass = (StudyGroupClassBean) eb;

		int id = getNextPK();

		HashMap<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index++, id);
		queryParameters.put(index++, groupClass.getName());
		queryParameters.put(index++, groupClass.getStudyId());
		queryParameters.put(index++, groupClass.getOwner().getId());
		queryParameters.put(index++, groupClass.getGroupClassTypeId());
		queryParameters.put(index++, groupClass.getStatus().getId());
		queryParameters.put(index++, groupClass.getSubjectAssignment());
		queryParameters.put(index++, groupClass.isDefault());
		queryParameters.put(index, groupClass.getDynamicOrdinal());

		this.execute(digester.getQuery("create"), queryParameters);

		if (isQuerySuccessful()) {
			groupClass.setId(id);
		}

		return groupClass;
	}

	public StudyGroupClassBean update(EntityBean eb) {

		StudyGroupClassBean groupClass = (StudyGroupClassBean) eb;

		HashMap<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index++, groupClass.getName());
		queryParameters.put(index++, groupClass.getStudyId());
		queryParameters.put(index++, groupClass.getGroupClassTypeId());
		queryParameters.put(index++, groupClass.getStatus().getId());
		queryParameters.put(index++, new Timestamp(new Date().getTime()));
		queryParameters.put(index++, groupClass.getUpdater().getId());
		queryParameters.put(index++, groupClass.getSubjectAssignment());
		queryParameters.put(index++, groupClass.isDefault());
		queryParameters.put(index++, groupClass.getDynamicOrdinal());
		queryParameters.put(index, groupClass.getId());

		this.execute(digester.getQuery("update"), queryParameters);

		return groupClass;
	}

	/**
	 * Returns the max ordinal number of the dynamic study group classes in a specific study.
	 *
	 * @param studyId the study id, to search on.
	 * @return the max ordinal number of the dynamic study group classes in a specific study;
	 * returns zero in case if the study has no dynamic study group classes assigned.
	 */
	public int getMaxDynamicOrdinalByStudyId(int studyId) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index, studyId);

		List<HashMap<String, Object>> recordsFromDB =
				this.select(digester.getQuery("getMaxDynamicOrdinalByStudyId"), queryParameters);

		return recordsFromDB.isEmpty() ? 0 : (Integer) recordsFromDB.get(0).get("max_ord");
	}

	/**
	 * Sets a new dynamic ordinal number for an existing dynamic study group class, specified by its id.
	 *
	 * @param newDynamicOrdinal the new dynamic ordinal number, to set.
	 * @param studyId the study id, to search on.
	 * @param studyGroupClassId the study group class id, to search on.
	 */
	public void updateDynamicOrdinal(int newDynamicOrdinal, int studyId, int studyGroupClassId) {

		HashMap<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index++, newDynamicOrdinal);
		queryParameters.put(index++, studyId);
		queryParameters.put(index, studyGroupClassId);

		this.execute(digester.getQuery("updateDynamicOrdinal"), queryParameters);
	}

	public List<StudyGroupClassBean> findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {

		return new ArrayList<StudyGroupClassBean>();
	}

	public List<StudyGroupClassBean> findAllByPermission(Object objCurrentUser, int intActionType) {

		return new ArrayList<StudyGroupClassBean>();
	}

	/**
	 * Returns the list of dynamic study group classes from a specific study, which have status Available only.
	 * Default dynamic group class is excluded.
	 *
	 * @param studyId the study id, to search on.
	 * @return the list of instances of <code>StudyGroupClassBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns an empty list.
	 */
	public List<StudyGroupClassBean> findAllActiveDynamicGroupClassesByStudyId(int studyId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index, studyId);

		List<HashMap<String, Object>> recordsFromDB =
				this.select(digester.getQuery("findAllActiveDynamicGroupClassesByStudyId"), queryParameters);

		List<StudyGroupClassBean> resultSetOfBeans = new ArrayList<StudyGroupClassBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans;
	}
}