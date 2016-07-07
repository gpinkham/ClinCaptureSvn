/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2015 Clinovo Inc.
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
package org.akaza.openclinica.dao.extract;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.DatasetItemStatus;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.ExtractBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.managestudy.StudyDAO;

import com.clinovo.util.OdmExtractUtil;

/**
 * The data access object for datasets; also generates datasets based on their query and criteria set; also generates
 * the extract bean, which holds dataset information.
 * 
 * @author thickerson
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DatasetDAO extends AuditableEntityDAO {

	private static final int QUERY_FETCH_SIZE = 50;

	private static final int DATE_RANGE_LOWER_BOUND_INDEX = 1;

	private static final int DATE_RANGE_UPPER_BOUND_INDEX = 3;

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_DATASET;
	}

	protected void setQueryNames() {
		getCurrentPKName = "getCurrentPK";
	}

	/**
	 * Creates a DatasetDAO object, for use in the application only.
	 * 
	 * @param ds
	 *            DataSource
	 */
	public DatasetDAO(DataSource ds) {
		super(ds);
		this.setQueryNames();
	}

	/**
	 * Creates a DatasetDAO object suitable for testing purposes only.
	 *
	 * @param ds
	 *            DataSource
	 * @param digester
	 *            DAODigester
	 */
	public DatasetDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
		this.setQueryNames();
	}

	@Override
	public void setTypesExpected() {
		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING); // name
		this.setTypeExpected(index++, TypeNames.STRING); // desc
		this.setTypeExpected(index++, TypeNames.STRING); // sql
		this.setTypeExpected(index++, TypeNames.INT); // num runs
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date start. YW,
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date end
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // created
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // updated
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // last run
		this.setTypeExpected(index++, TypeNames.INT); // owner id
		this.setTypeExpected(index++, TypeNames.INT); // approver id
		this.setTypeExpected(index++, TypeNames.INT); // update id
		this.setTypeExpected(index++, TypeNames.BOOL); // show_event_location
		this.setTypeExpected(index++, TypeNames.BOOL); // show_event_start
		this.setTypeExpected(index++, TypeNames.BOOL); // show_event_end
		this.setTypeExpected(index++, TypeNames.BOOL); // show_subject_dob
		this.setTypeExpected(index++, TypeNames.BOOL); // show_subject_gender
		this.setTypeExpected(index++, TypeNames.BOOL); // show_event_status
		this.setTypeExpected(index++, TypeNames.BOOL); // show_subject_status
		this.setTypeExpected(index++, TypeNames.BOOL); // show_subject_unique_id
		this.setTypeExpected(index++, TypeNames.BOOL); // show_subject_age_at_event
		this.setTypeExpected(index++, TypeNames.BOOL); // show_crf_status
		this.setTypeExpected(index++, TypeNames.BOOL); // show_crf_version
		this.setTypeExpected(index++, TypeNames.BOOL); // show_crf_int_name
		this.setTypeExpected(index++, TypeNames.BOOL); // show_crf_int_date
		this.setTypeExpected(index++, TypeNames.BOOL); // show_group_info
		this.setTypeExpected(index++, TypeNames.BOOL); // show_disc_info
		this.setTypeExpected(index++, TypeNames.STRING); // odm_metadataversion_name
		this.setTypeExpected(index++, TypeNames.STRING); // odm_metadataversion_oid
		this.setTypeExpected(index++, TypeNames.STRING); // odm_prior_study_oid
		this.setTypeExpected(index++, TypeNames.STRING); // odm_prior_metadataversion_oid
		this.setTypeExpected(index++, TypeNames.BOOL); // show_secondary_id
		this.setTypeExpected(index++, TypeNames.INT); // dataset_item_status_id
		this.setTypeExpected(index++, TypeNames.STRING); // exclude_items
		this.setTypeExpected(index, TypeNames.STRING); // sed_id_and_crf_id_pairs
	}

	/**
	 * Set Item types expected.
	 */
	public void setItemTypesExpected() {
		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.BOOL); // phi status
		this.setTypeExpected(index++, TypeNames.INT); // data type id
		this.setTypeExpected(index++, TypeNames.INT); // reference type id
		this.setTypeExpected(index++, TypeNames.INT); // status id
		this.setTypeExpected(index++, TypeNames.INT); // owner id
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // created
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // updated
		this.setTypeExpected(index++, TypeNames.INT); // update id
		this.setTypeExpected(index++, TypeNames.STRING); // oc_oid
		this.setTypeExpected(index, TypeNames.STRING); // sas_name
	}

	/**
	 * Set Item types expected.
	 */
	public void setDefinitionCrfItemTypesExpected() {
		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT); // item_id
		this.setTypeExpected(index++, TypeNames.INT); // sed_id
		this.setTypeExpected(index++, TypeNames.STRING); // sed_name
		this.setTypeExpected(index++, TypeNames.INT); // crf_id
		this.setTypeExpected(index++, TypeNames.STRING); // crf_name
		this.setTypeExpected(index++, TypeNames.INT); // cv_version_id
		this.setTypeExpected(index++, TypeNames.STRING); // cv_name
		this.setTypeExpected(index, TypeNames.INT); // crfs_masking id
	}

	/**
	 * Update DatasetBean.
	 * 
	 * @param eb
	 *            EntityBean.
	 * @return EntityBean
	 */
	public EntityBean update(EntityBean eb) {
		DatasetBean db = (DatasetBean) eb;
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		int index = 1;
		variables.put(index++, db.getStudyId());
		variables.put(index++, db.getStatus().getId());
		variables.put(index++, db.getName());
		variables.put(index++, db.getDescription());
		variables.put(index++, db.getSQLStatement());
		variables.put(index++, db.getDateLastRun());
		variables.put(index++, db.getNumRuns());
		variables.put(index++, db.getUpdaterId());
		if (db.getApproverId() <= 0) {
			nullVars.put(index, Types.NUMERIC);
		}
		variables.put(index++, db.getApproverId() <= 0 ? null : db.getApproverId());

		if (db.getDateStart() == null) {
			nullVars.put(index, Types.TIMESTAMP);
			variables.put(index, null);
		} else {
			variables.put(index, new Timestamp(db.getDateStart().getTime()));
		}
		index++;

		if (db.getDateEnd() == null) {
			nullVars.put(index, Types.TIMESTAMP);
			variables.put(index, null);
		} else {
			variables.put(index, new Timestamp(db.getDateEnd().getTime()));
		}
		index++;

		variables.put(index, db.getId());
		this.execute(digester.getQuery("update"), variables, nullVars);
		return eb;
	}

	/**
	 * Create DatasetBean.
	 * 
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean create(EntityBean eb) {
		DatasetBean db = (DatasetBean) eb;
		String excludeItems = "";
		String sedIdAndCrfIdPairs = "";
		for (String key : (Set<String>) db.getItemMap().keySet()) {
			ItemBean ib = (ItemBean) db.getItemMap().get(key);
			if (!ib.isSelected()) {
				excludeItems = excludeItems.concat(excludeItems.isEmpty() ? "" : ",").concat(key);
			}
			sedIdAndCrfIdPairs = addSedIdAndCRFIdIfUnique(sedIdAndCrfIdPairs, key);
		}
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		HashMap nullVars = new HashMap();
		int index = 1;
		variables.put(index++, db.getStudyId());
		variables.put(index++, db.getStatus().getId());
		variables.put(index++, db.getName());
		variables.put(index++, db.getDescription());
		variables.put(index++, db.getSQLStatement());
		variables.put(index++, db.getOwnerId());
		variables.put(index++, db.getNumRuns());

		if (db.getDateStart() == null) {
			nullVars.put(index, Types.TIMESTAMP);
			variables.put(index, null);
		} else {
			variables.put(index, new Timestamp(db.getDateStart().getTime()));
		}
		index++;

		if (db.getDateEnd() == null) {
			nullVars.put(index, Types.TIMESTAMP);
			variables.put(index, null);
		} else {
			variables.put(index, new Timestamp(db.getDateEnd().getTime()));
		}
		index++;

		variables.put(index++, db.isShowEventLocation());
		variables.put(index++, db.isShowEventStart());
		variables.put(index++, db.isShowEventEnd());
		variables.put(index++, db.isShowSubjectDob());
		variables.put(index++, db.isShowSubjectGender());
		variables.put(index++, db.isShowEventStatus());
		variables.put(index++, db.isShowSubjectStatus());
		variables.put(index++, db.isShowSubjectUniqueIdentifier());
		variables.put(index++, db.isShowSubjectAgeAtEvent());
		variables.put(index++, db.isShowCRFstatus());
		variables.put(index++, db.isShowCRFversion());
		variables.put(index++, db.isShowCRFinterviewerName());
		variables.put(index++, db.isShowCRFinterviewerDate());
		variables.put(index++, db.isShowSubjectGroupInformation());
		variables.put(index++, false);
		// currently not changing structure to allow for disc notes to be added
		// in the future
		variables.put(index++, db.getOdmMetaDataVersionName());
		variables.put(index++, db.getOdmMetaDataVersionOid());
		variables.put(index++, db.getOdmPriorStudyOid());
		variables.put(index++, db.getOdmPriorMetaDataVersionOid());
		variables.put(index++, db.isShowSubjectSecondaryId());
		variables.put(index++, db.getDatasetItemStatus().getId());
		variables.put(index++, excludeItems);
		variables.put(index, sedIdAndCrfIdPairs);

		this.executeWithPK(digester.getQuery("create"), variables, nullVars);

		if (isQuerySuccessful()) {
			eb.setId(getLatestPK());
			if (db.isShowSubjectGroupInformation()) {
				// add additional information here
				for (int i = 0; i < db.getSubjectGroupIds().size(); i++) {
					createGroupMap(eb.getId(), (Integer) db.getSubjectGroupIds().get(i), nullVars);
				}
			}
		}
		return eb;
	}

	private String addSedIdAndCRFIdIfUnique(String sedIdAndCrfIdPairs, String key) {
		String [] arguments = key.split("_");
		String sedAndCrfId = arguments[0] + "_" + arguments[1];

		List<String> pairsList = Arrays.asList(sedIdAndCrfIdPairs.split(","));
		if (pairsList.contains(sedAndCrfId)) {
			return sedIdAndCrfIdPairs + (sedIdAndCrfIdPairs.isEmpty() ? "" : ",") + sedAndCrfId;
		} else {
			return sedIdAndCrfIdPairs;
		}
	}

	/**
	 * Get entity from hash map.
	 * 
	 * @param hm
	 *            HashMap
	 * @return DatasetBean
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		DatasetBean eb = new DatasetBean();
		this.setEntityAuditInformation(eb, hm);
		eb.setDescription((String) hm.get("description"));
		eb.setStudyId((Integer) hm.get("study_id"));
		eb.setName((String) hm.get("name"));
		eb.setId((Integer) hm.get("dataset_id"));
		eb.setSQLStatement((String) hm.get("sql_statement"));
		eb.setNumRuns((Integer) hm.get("num_runs"));
		eb.setDateStart((Date) hm.get("date_start"));
		eb.setDateEnd((Date) hm.get("date_end"));
		eb.setApproverId((Integer) hm.get("approver_id"));
		eb.setDateLastRun((Date) hm.get("date_last_run"));
		eb.setShowEventEnd((Boolean) hm.get("show_event_end"));
		eb.setShowEventStart((Boolean) hm.get("show_event_start"));
		eb.setShowEventLocation((Boolean) hm.get("show_event_location"));
		eb.setShowSubjectDob((Boolean) hm.get("show_subject_dob"));
		eb.setShowSubjectGender((Boolean) hm.get("show_subject_gender"));
		eb.setShowEventStatus((Boolean) hm.get("show_event_status"));
		eb.setShowSubjectStatus((Boolean) hm.get("show_subject_status"));
		eb.setShowSubjectUniqueIdentifier((Boolean) hm.get("show_subject_unique_id"));
		eb.setShowSubjectAgeAtEvent((Boolean) hm.get("show_subject_age_at_event"));
		eb.setShowCRFstatus((Boolean) hm.get("show_crf_status"));
		eb.setShowCRFversion((Boolean) hm.get("show_crf_version"));
		eb.setShowCRFinterviewerName((Boolean) hm.get("show_crf_int_name"));
		eb.setShowCRFinterviewerDate((Boolean) hm.get("show_crf_int_date"));
		eb.setShowSubjectGroupInformation((Boolean) hm.get("show_group_info"));

		eb.setSubjectGroupIds(getGroupIds(eb.getId()));

		eb.setOdmMetaDataVersionName((String) hm.get("odm_metadataversion_name"));
		eb.setOdmMetaDataVersionOid((String) hm.get("odm_metadataversion_oid"));
		eb.setOdmPriorStudyOid((String) hm.get("odm_prior_study_oid"));
		eb.setOdmPriorMetaDataVersionOid((String) hm.get("odm_prior_metadataversion_oid"));
		eb.setShowSubjectSecondaryId((Boolean) hm.get("show_secondary_id"));
		int isId = (Integer) hm.get("dataset_item_status_id");
		isId = isId > 0 ? isId : 1;
		DatasetItemStatus dis = DatasetItemStatus.get(isId);
		eb.setDatasetItemStatus(dis);
		eb.setExcludeItems((String) hm.get("exclude_items"));
		eb.setSedIdAndCRFIdPairs((String) hm.get("sed_id_and_crf_id_pairs"));
		return eb;
	}

	/**
	 * Get Ids of the groups.
	 * 
	 * @param datasetId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList getGroupIds(int datasetId) {
		ArrayList<Integer> groupIds = new ArrayList<Integer>();
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT); // dataset id
		this.setTypeExpected(2, TypeNames.INT); // subject group id
		HashMap<Integer, Integer> variablesNew = new HashMap<Integer, Integer>();
		variablesNew.put(1, datasetId);
		ArrayList alist = this.select(digester.getQuery("findAllGroups"), variablesNew);
		// convert them to ids for the array list, tbh
		// the above is an array list of hashmaps, each hash map being a row in
		// the DB
		for (Object anAlist : alist) {
			HashMap row = (HashMap) anAlist;
			Integer id = (Integer) row.get("study_group_class_id");
			groupIds.add(id);
		}
		return groupIds;
	}

	/**
	 * Find all datasets.
	 * 
	 * @return Collection
	 */
	public Collection findAll() {
		this.setTypesExpected();
		ArrayList alist = this.select(digester.getQuery("findAll"));
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			DatasetBean eb = (DatasetBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find all and order by study id and name.
	 * 
	 * @return Collection
	 */
	public Collection findAllOrderByStudyIdAndName() {
		this.setTypesExpected();
		ArrayList alist = this.select(digester.getQuery("findAllOrderByStudyIdAndName"));
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			DatasetBean eb = (DatasetBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find top five.
	 * 
	 * @param studyBean
	 *            StudyBean
	 * @return Collection
	 */
	public Collection findTopFive(StudyBean studyBean) {
		int index = 1;
		setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, studyBean.getId());
		variables.put(index, studyBean.getId());
		List<HashMap> mapList = select(digester.getQuery("findTopFive"), variables);
		ArrayList result = new ArrayList();
		for (HashMap map : mapList) {
			DatasetBean eb = (DatasetBean) getEntityFromHashMap(map);
			eb.setStudyBean(eb.getStudyId() != studyBean.getId()
					? (StudyBean) new StudyDAO(getDataSource()).findByPK(eb.getStudyId())
					: studyBean);
			result.add(eb);
		}
		return result;
	}

	/**
	 * Find by owner id, reports a list of datasets by user account id.
	 *
	 * @param ownerId
	 *            studyId
	 * @param studyBean
	 *            StudyBean
	 * @return Collection
	 */
	public Collection findByOwnerId(int ownerId, StudyBean studyBean) {
		int index = 1;
		setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, studyBean.getId());
		variables.put(index++, studyBean.getId());
		variables.put(index, ownerId);
		ArrayList result = new ArrayList();
		List<HashMap> mapList = select(digester.getQuery("findByOwnerId"), variables);
		for (HashMap map : mapList) {
			DatasetBean eb = (DatasetBean) getEntityFromHashMap(map);
			eb.setStudyBean(eb.getStudyId() != studyBean.getId()
					? (StudyBean) new StudyDAO(getDataSource()).findByPK(eb.getStudyId())
					: studyBean);
			result.add(eb);
		}
		return result;
	}

	/**
	 * Not implemented.
	 * 
	 * @param strOrderByColumn
	 *            String
	 * @param blnAscendingSort
	 *            boolean
	 * @param strSearchPhrase
	 *            String
	 * @return new ArrayList
	 */
	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * Find by ID.
	 * 
	 * @param id
	 *            int
	 * @return EntityBean
	 */
	public EntityBean findByPK(int id) {
		DatasetBean eb = new DatasetBean();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, id);
		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (DatasetBean) this.getEntityFromHashMap((HashMap) it.next());
		} else {
			logger.warn("found no object: " + sql + " " + id);
		}
		return eb;
	}

	/**
	 * Find all by name and study.
	 * 
	 * @param name
	 *            dataset name
	 * @param study
	 *            StudyBean
	 * @return EntityBean
	 */
	public EntityBean findByNameAndStudy(String name, StudyBean study) {
		DatasetBean eb = new DatasetBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, name);
		variables.put(2, study.getId());
		String sql = digester.getQuery("findByNameAndStudy");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (DatasetBean) this.getEntityFromHashMap((HashMap) it.next());
		} else {
			logger.warn("found no object: " + sql + " " + name);
		}
		return eb;
	}

	/**
	 * Implements the Data Algorithm described in Dataset Export Algorithms, stores output in the returned ExtractBean.
	 * 
	 * @param eb
	 *            The ExtractBean containing the dataset and study for which data is being retrieved.
	 * @param currentstudyid
	 *            int
	 * @param parentstudyid
	 *            int
	 * @return An ExtractBean containing structured data stored by subject, study event definition, ordinal, CRF and
	 *         item, as well as the maximum ordinal per study event definition.
	 */
	public ExtractBean getDatasetData(ExtractBean eb, int currentstudyid, int parentstudyid) {
		String sql = eb.getDataset().getSQLStatement();
		String stSedIn = parseSQLDataset(sql, true, true);
		String stItemidIn = parseSQLDataset(sql, false, true);
		// get the study subjects; to each study subject it associates the data from the subjects themselves
		int datasetItemStatusId = eb.getDataset().getDatasetItemStatus().getId();
		String ecStatusConstraint = this.getECStatusConstraint(datasetItemStatusId);
		String itStatusConstraint = this.getItemDataStatusConstraint(datasetItemStatusId);
		Map<Integer, List<OdmExtractUtil.StudySubjectsHolder>> results = selectStudySubjects(
				OdmExtractUtil.pairList(currentstudyid, parentstudyid), stSedIn, stItemidIn,
				this.genDatabaseDateConstraint(eb), ecStatusConstraint, itStatusConstraint, -1);
		// Add it to ths subjects
		eb.addStudySubjectData(OdmExtractUtil.getAllStudySubjects(results));
		// II. Add the study_event records
		HashMap nhInHelpKeys = setHashMapInKeysHelper(currentstudyid, parentstudyid, stSedIn, stItemidIn,
				this.genDatabaseDateConstraint(eb), ecStatusConstraint, itStatusConstraint);
		eb.setHmInKeys(nhInHelpKeys);
		// Get the arrays of ArrayList for SQL BASE There are split in two querries for perfomance
		eb.resetArrayListEntryBASE_ITEMGROUPSIDE();
		loadBaseEventInsideHashMap(currentstudyid, parentstudyid, stSedIn, stItemidIn, eb);
		loadBaseItemGroupSideHashMap(currentstudyid, parentstudyid, stSedIn, stItemidIn, eb);
		// add study_event data
		eb.addStudyEventData();
		// add item_data
		eb.addItemData();
		return eb;
	}

	/**
	 * Not implemented!
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
	 * Not implemented!
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

	/**
	 * Find all by studyBean.
	 * 
	 * @param studyBean
	 *            StudyBean.
	 * @return ArrayList
	 */
	public ArrayList findAllByStudyId(StudyBean studyBean) {
		int index = 1;
		setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, studyBean.getId());
		variables.put(index, studyBean.getId());
		ArrayList result = new ArrayList();
		List<HashMap> mapList = select(digester.getQuery("findAllByStudyId"), variables);
		for (HashMap map : mapList) {
			DatasetBean eb = (DatasetBean) getEntityFromHashMap(map);
			eb.setStudyBean(eb.getStudyId() != studyBean.getId()
					? (StudyBean) new StudyDAO(getDataSource()).findByPK(eb.getStudyId())
					: studyBean);
			result.add(eb);
		}
		return result;
	}

	/**
	 * Find all by studyId admin.
	 * 
	 * @param studyId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllByStudyIdAdmin(int studyId) {
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);

		return executeFindAllQuery("findAllByStudyIdAdmin", variables);
	}

	/**
	 * Select ItemBeans.
	 *
	 * @param itemIds
	 *            String
	 * @return ArrayList
	 */
	public ArrayList selectItemBeans(String itemIds) {
		return select("select * from item where item_id in ".concat(itemIds));
	}

	/**
	 * Select DefinitionCRFItemIds.
	 *
	 * @param userId
	 *            int
	 * @param studyId
	 *            int
	 * @param sedIds
	 *            String
	 * @param itemIds
	 *            String
	 * @return ArrayList
	 */
	public ArrayList selectNotMaskedDefinitionCrfItemIds(int userId, int studyId, String sedIds, String itemIds) {
		return select(
				"select i.item_id as item_id, sed.study_event_definition_id as sed_id, sed.name as sed_name,c.crf_id as crf_id, c.name as crf_name, cv.crf_version_id as cv_version_id, cv.name as cv_name, cm.id as masked "
						+ "from study_event_definition sed " + "join study s on s.study_id = " + studyId
						+ " join event_definition_crf edc on edc.study_event_definition_id = sed.study_event_definition_id and ((s.parent_study_id is null and edc.study_id = s.study_id) or (not(s.parent_study_id is null) and (edc.study_id = s.study_id or edc.study_id = s.parent_study_id) and edc.event_definition_crf_id not in (select parent_id from event_definition_crf edc where edc.study_id = s.study_id))) "
						+ "join crf c on c.crf_id = edc.crf_id " + "join crf_version cv on cv.crf_id = c.crf_id "
						+ "join item_form_metadata ifm on ifm.crf_version_id = cv.crf_version_id "
						+ "join item i on i.item_id = ifm.item_id " + "left join crfs_masking cm on cm.user_id = "
						+ userId
						+ " and cm.study_event_definition_id = sed.study_event_definition_id and cm.event_definition_crf_id = edc.event_definition_crf_id "
						+ "and cm.study_id = s.study_id " + "and cm.status_id != 5 "
						+ "where sed.study_event_definition_id in " + sedIds + " and i.item_id in " + itemIds);
	}

	/**
	 * Update all columns of the dataset table except owner_id.
	 *
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean updateAll(EntityBean eb) {
		eb.setActive(false);
		DatasetBean db = (DatasetBean) eb;
		String excludeItems = "";
		String sedIdAndCrfIdPairs = "";
		for (String key : (Set<String>) db.getItemMap().keySet()) {
			ItemBean ib = (ItemBean) db.getItemMap().get(key);
			if (!ib.isSelected()) {
				excludeItems = excludeItems.concat(excludeItems.isEmpty() ? "" : ",").concat(key);
			}
			sedIdAndCrfIdPairs = addSedIdAndCRFIdIfUnique(sedIdAndCrfIdPairs, key);
		}
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		int index = 1;
		variables.put(index++, db.getStudyId());
		variables.put(index++, db.getStatus().getId());
		variables.put(index++, db.getName());
		variables.put(index++, db.getDescription());
		variables.put(index++, db.getSQLStatement());
		variables.put(index++, excludeItems);
		variables.put(index++, db.getDateLastRun());
		variables.put(index++, db.getNumRuns());
		variables.put(index++, db.getUpdaterId());
		if (db.getApproverId() <= 0) {
			nullVars.put(index, Types.NUMERIC);
		}
		variables.put(index++, db.getApproverId() <= 0 ? null : db.getApproverId());

		if (db.getDateStart() == null) {
			nullVars.put(index, Types.TIMESTAMP);
			variables.put(index, null);
		} else {
			variables.put(index, new Timestamp(db.getDateStart().getTime()));
		}
		index++;

		if (db.getDateEnd() == null) {
			nullVars.put(index, Types.TIMESTAMP);
			variables.put(index, null);
		} else {
			variables.put(index, new Timestamp(db.getDateEnd().getTime()));
		}
		index++;

		variables.put(index++, db.isShowEventLocation());
		variables.put(index++, db.isShowEventStart());
		variables.put(index++, db.isShowEventEnd());
		variables.put(index++, db.isShowSubjectDob());
		variables.put(index++, db.isShowSubjectGender());
		variables.put(index++, db.isShowEventStatus());
		variables.put(index++, db.isShowSubjectStatus());
		variables.put(index++, db.isShowSubjectUniqueIdentifier());
		variables.put(index++, db.isShowSubjectAgeAtEvent());
		variables.put(index++, db.isShowCRFstatus());
		variables.put(index++, db.isShowCRFversion());
		variables.put(index++, db.isShowCRFinterviewerName());
		variables.put(index++, db.isShowCRFinterviewerDate());
		variables.put(index++, db.isShowSubjectGroupInformation());
		variables.put(index++, false);
		variables.put(index++, db.getOdmMetaDataVersionName());
		variables.put(index++, db.getOdmMetaDataVersionOid());
		variables.put(index++, db.getOdmPriorStudyOid());
		variables.put(index++, db.getOdmPriorMetaDataVersionOid());
		variables.put(index++, db.isShowSubjectSecondaryId());
		variables.put(index++, db.getDatasetItemStatus().getId());
		variables.put(index++, sedIdAndCrfIdPairs);
		variables.put(index, db.getId());
		this.execute(digester.getQuery("updateAll"), variables, nullVars);
		if (isQuerySuccessful()) {
			eb.setActive(true);
		}
		return eb;
	}

	/**
	 * Update Group Map.
	 * 
	 * @param db
	 *            DatasetBean
	 * @return EntityBean
	 */
	public EntityBean updateGroupMap(DatasetBean db) {
		HashMap nullVars = new HashMap();
		db.setActive(false);
		boolean success = true;

		ArrayList<Integer> sgcIds = this.getGroupIds(db.getId());
		ArrayList<Integer> dbSgcIds = (ArrayList<Integer>) db.getSubjectGroupIds().clone();
		if (sgcIds.size() > 0) {
			for (Integer id : sgcIds) {
				if (!dbSgcIds.contains(id)) {
					removeGroupMap(db.getId(), id, nullVars);
					if (!isQuerySuccessful()) {
						success = false;
					}
				} else {
					dbSgcIds.remove(id);
				}
			}
		}
		if (success) {
			if (dbSgcIds.size() > 0) {
				for (Integer id : dbSgcIds) {
					createGroupMap(db.getId(), id, nullVars);
					if (!isQuerySuccessful()) {
						success = false;
					}
				}
			}
		}
		if (success) {
			db.setActive(true);
		}
		return db;
	}

	protected void createGroupMap(int datasetId, int studyGroupClassId, HashMap nullVars) {
		HashMap<Integer, Integer> variablesNew = new HashMap<Integer, Integer>();
		variablesNew.put(1, datasetId);
		Integer groupId = studyGroupClassId;
		variablesNew.put(2, groupId);
		this.execute(digester.getQuery("createGroupMap"), variablesNew, nullVars);
	}

	protected void removeGroupMap(int datasetId, int studyGroupClassId, HashMap nullVars) {
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, datasetId);
		Integer groupId = studyGroupClassId;
		variables.put(2, groupId);
		this.execute(digester.getQuery("removeGroupMap"), variables, nullVars);
	}

	/**
	 * Parse Dataset from SQL.
	 * 
	 * @param sql
	 *            String
	 * @param issed
	 *            boolean
	 * @param hasfilterzero
	 *            boolean
	 * @return String
	 */
	public String parseSQLDataset(String sql, boolean issed, boolean hasfilterzero) {
		int sedidOne;
		int sedidTwo;
		int itidOne;
		int itidTwo;
		String sedSt;
		String sedStno;
		String itSt;
		String itStno;
		Vector sedvecTmp = new Vector();
		Vector sedvec = new Vector();
		Vector itvec = new Vector();

		// get the first
		sedidOne = sql.indexOf("(");
		sedidTwo = sql.indexOf(")");
		if (sedidOne != -1 && sedidTwo != -1) {
			// found - get the substring
			sedSt = sql.substring(sedidOne + 1, sedidTwo);
			// parse it for values
			boolean hasmore = true;
			int no;
			do {
				// get to the first comma
				int ic = sedSt.indexOf(",");
				if (ic != -1) {
					// found
					sedStno = sedSt.substring(0, ic);
					// get into int
					try {
						no = Integer.parseInt(sedStno.trim());
						sedvecTmp.add(no);
						// set the new string
						sedSt = sedSt.substring(ic + 1, sedSt.length());
					} catch (NumberFormatException nfe) {
						logger.error(nfe.getLocalizedMessage());
					}
				} else {
					// only one
					try {
						no = Integer.parseInt(sedSt.trim());
						sedvecTmp.add(no);
					} catch (NumberFormatException nfe) {
						logger.error(nfe.getLocalizedMessage());
					}
					hasmore = false;
				}
			} while (hasmore);
		}
		// get the second
		sql = sql.substring(sedidTwo + 1, sql.length());
		itidOne = sql.indexOf("(");
		itidTwo = sql.indexOf(")");
		if (itidOne != -1 && sedidTwo != -1) {
			// found - get the substring
			itSt = sql.substring(itidOne + 1, itidTwo);
			// parse it for values
			boolean hasmore = true;
			int no;
			do {
				// get to the first comma
				int ic = itSt.indexOf(",");
				if (ic != -1) {
					// found
					itStno = itSt.substring(0, ic);
					// get into int
					try {
						no = Integer.parseInt(itStno.trim());
						itvec.add(no);
						// set the new string
						itSt = itSt.substring(ic + 1, itSt.length());
					} catch (NumberFormatException nfe) {
						logger.error(nfe.getLocalizedMessage());
					}
				} else {
					try {
						no = Integer.parseInt(itSt.trim());
						itvec.add(no);
					} catch (NumberFormatException nfe) {
						logger.error(nfe.getLocalizedMessage());
					}

					hasmore = false;
				}

			} while (hasmore);

		}
		// Eliminate 0 from SED but only if
		if (hasfilterzero) {
			for (Object aSedvecTmp : sedvecTmp) {
				Integer itmp = (Integer) aSedvecTmp;
				if (itmp != 0) {
					sedvec.add(itmp);
				}
			}
		}
		String stsedIn = "";
		for (int ij = 0; ij < sedvec.size(); ij++) {
			stsedIn = stsedIn + sedvec.get(ij).toString();
			if (ij != sedvec.size() - 1) {
				stsedIn = stsedIn.concat(",");
			}
		}
		String stitIn = "";
		for (int ij = 0; ij < itvec.size(); ij++) {
			stitIn = stitIn + itvec.get(ij).toString();
			if (ij != itvec.size() - 1) {
				stitIn = stitIn.concat(",");
			}
		}
		stsedIn = "(" + stsedIn + ")";
		stitIn = "(" + stitIn + ")";

		if (issed) {
			return stsedIn;
		} else {
			return stitIn;
		}
	}

	/**
	 * Selects study subjects.
	 *
	 * @param pairList           List
	 * @param sedin              String
	 * @param itIn               String
	 * @param dateConstraint     String
	 * @param ecStatusConstraint String
	 * @param itStatusConstraint String
	 * @param studySubjectNumber int
	 * @return Map
	 */
	public Map<Integer, List<OdmExtractUtil.StudySubjectsHolder>> selectStudySubjects(
			List<Map<Integer, Integer>> pairList, String sedin, String itIn, String dateConstraint,
			String ecStatusConstraint, String itStatusConstraint, int studySubjectNumber) {

		clearSignals();
		String query = getSQLSubjectStudySubjectDataset(pairList, sedin, itIn, dateConstraint, ecStatusConstraint,
				itStatusConstraint);
		logger.error("sqlSubjectStudySubjectDataset=" + query);
		Map<Integer, List<OdmExtractUtil.StudySubjectsHolder>> mapOfStudySubjectsHolderList = new HashMap<Integer, List<OdmExtractUtil.StudySubjectsHolder>>();
		ResultSet rs = null;
		Connection connection = null;
		Statement ps = null;
		try {
			connection = getDataSource().getConnection();
			connection.setAutoCommit(false);
			if (connection.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: DatasetDAO.select!");
				}
				throw new SQLException();
			}
			final int fetchSize = 50;
			ps = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setFetchSize(fetchSize);
			rs = ps.executeQuery(query);
			if (logger.isInfoEnabled()) {
				logger.trace("Executing static query, DatasetDAO.select: " + query);
			}
			signalSuccess();
			mapOfStudySubjectsHolderList = this.processStudySubjects(rs, studySubjectNumber);
		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exeception while executing static query, DatasetDAO.select: " + query + ": "
						+ sqle.getMessage());
				sqle.printStackTrace();
			}
		} finally {
			this.closeIfNecessary(connection, rs, ps);
		}
		return mapOfStudySubjectsHolderList;
	}

	/**
	 * Processes StudySubjects.
	 *
	 * @param rs                 ResultSet
	 * @param studySubjectNumber int
	 * @return ArrayList
	 */
	protected Map<Integer, List<OdmExtractUtil.StudySubjectsHolder>> processStudySubjects(ResultSet rs,
			int studySubjectNumber) {
		Map<Integer, List<OdmExtractUtil.StudySubjectsHolder>> mapOfStudySubjectsHolderList = new HashMap<Integer, List<OdmExtractUtil.StudySubjectsHolder>>();
		try {
			while (rs.next()) {
				int studyId = rs.getInt("study_id");

				List<OdmExtractUtil.StudySubjectsHolder> studySubjectsHolderList = mapOfStudySubjectsHolderList
						.get(studyId);
				if (studySubjectsHolderList == null) {
					studySubjectsHolderList = new ArrayList<OdmExtractUtil.StudySubjectsHolder>();
					mapOfStudySubjectsHolderList.put(studyId, studySubjectsHolderList);
				}
				OdmExtractUtil.StudySubjectsHolder studySubjectsHolder;
				if (studySubjectsHolderList.size() == 0) {
					studySubjectsHolder = new OdmExtractUtil.StudySubjectsHolder();
					studySubjectsHolderList.add(studySubjectsHolder);
				} else {
					studySubjectsHolder = studySubjectsHolderList.get(studySubjectsHolderList.size() - 1);
				}
				if (studySubjectNumber > 0 && studySubjectsHolder.getStudySubjectList().size() == studySubjectNumber) {
					studySubjectsHolder = new OdmExtractUtil.StudySubjectsHolder();
					studySubjectsHolderList.add(studySubjectsHolder);
				}

				StudySubjectBean obj = new StudySubjectBean();

				obj.setId(rs.getInt("study_subject_id"));
				if (rs.wasNull()) {
					obj.setId(0);
				}

				obj.setSubjectId(rs.getInt("subject_id"));
				if (rs.wasNull()) {
					obj.setSubjectId(0);
				}

				// old subject_identifier
				obj.setLabel(rs.getString("label"));
				if (rs.wasNull()) {
					obj.setLabel("");
				}

				obj.setDateOfBirth(rs.getDate("date_of_birth"));

				String gender = rs.getString("gender");
				if (gender != null && gender.length() > 0) {
					obj.setGender(gender.charAt(0));
				} else {
					obj.setGender(' ');
				}

				obj.setUniqueIdentifier(rs.getString("unique_identifier"));
				if (rs.wasNull()) {
					obj.setUniqueIdentifier("");
				}

				if (CoreResources.getDBType().equals("oracle")) {
					obj.setDobCollected(rs.getString("dob_collected").equals("1"));
				} else {
					obj.setDobCollected(rs.getBoolean("dob_collected"));
				}
				if (rs.wasNull()) {
					obj.setDobCollected(false);
				}

				Integer subjectStatusId = rs.getInt("status_id");
				if (rs.wasNull()) {
					subjectStatusId = 0;
				}
				obj.setStatus(Status.get(subjectStatusId));

				obj.setSecondaryLabel(rs.getString("secondary_label"));
				if (rs.wasNull()) {
					obj.setSecondaryLabel("");
				}

				studySubjectsHolder.addStudySubject(obj);
			}
		} catch (SQLException sqle) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while processing result rows, EntityDAO.processStudySubjects: " + ": "
						+ sqle.getMessage() + ": array length: " + mapOfStudySubjectsHolderList.size());
				sqle.printStackTrace();
			}
		}
		return mapOfStudySubjectsHolderList;
	}

	protected String getSQLSubjectStudySubjectDataset(List<Map<Integer, Integer>> pairList, String eventDefIDConstraints,
			String itemIDConstraints, String dateConstraint, String ecStatusConstraint, String itStatusConstraint) {

		return " SELECT   "
				+ " DISTINCT study_subject.study_subject_id, study_subject.study_id, study_subject.label,  study_subject.subject_id, "
				+ "  subject.date_of_birth, subject.gender, subject.unique_identifier, subject.dob_collected,  "
				+ "  subject.status_id, study_subject.secondary_label  "
				+ "  FROM  "
				+ "   study_subject "
				+ "  JOIN subject ON (study_subject.subject_id = subject.subject_id)  "
				+ "  JOIN study_event ON (study_subject.study_subject_id = study_event.study_subject_id) "
				+ "  WHERE  "
				+ "   study_subject.study_subject_id IN  "
				+ "  ( "
				+ "SELECT DISTINCT studysubjectid FROM "
				+ "( "
				+ getSQLDatasetBaseEventSide(pairList, eventDefIDConstraints, itemIDConstraints, dateConstraint,
				ecStatusConstraint, itStatusConstraint) + " ) AS SBQTWO "
				+ "  ) order by study_subject.study_id, study_subject.study_subject_id";
	}

	protected boolean loadBaseItemGroupSideHashMap(int studyID, int parentID, String eventDefIDConstraints,
			String itemIDConstraints, ExtractBean eb) {

		clearSignals();
		int datasetItemStatusId = eb.getDataset().getDatasetItemStatus().getId();
		String ecStatusConstraint = this.getECStatusConstraint(datasetItemStatusId);
		String itStatusConstraint = this.getItemDataStatusConstraint(datasetItemStatusId);
		String query = getSQLDatasetBaseItemGroupSide(studyID, parentID, eventDefIDConstraints, itemIDConstraints,
				genDatabaseDateConstraint(eb), ecStatusConstraint, itStatusConstraint);
		logger.error("sqlDatasetBase_itemGroupside=" + query);
		boolean bret = false;
		ResultSet rs = null;
		Connection connection = null;
		Statement ps = null;
		try {
			connection = getDataSource().getConnection();
			connection.setAutoCommit(false);
			if (connection.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: DatasetDAO.select!");	
				}
				throw new SQLException();
			}
			ps = connection.createStatement();
			ps.setFetchSize(QUERY_FETCH_SIZE);

			rs = ps.executeQuery(query);
			if (logger.isInfoEnabled()) {
				logger.trace("Executing static query, DatasetDAO.select: " + query);
			}
			signalSuccess();
			System.out.println("*** query that runs before we fill the ItemGroupSide: " + query);
			processBaseItemGroupSideRecords(rs, eb);
			bret = true;

		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exeception while executing static query, DatasetDAO.select: " + query + ": "
						+ sqle.getMessage());
				sqle.printStackTrace();
			}
		} finally {
			this.closeIfNecessary(connection, rs, ps);
		}
		return bret;
	}

	protected boolean loadBaseEventInsideHashMap(int studyID, int parentID, String eventDefIDConstraints,
			String itemIDConstraints, ExtractBean eb) {

		clearSignals();
		int datasetItemStatusId = eb.getDataset().getDatasetItemStatus().getId();
		String ecStatusConstraint = this.getECStatusConstraint(datasetItemStatusId);
		String itStatusConstraint = this.getItemDataStatusConstraint(datasetItemStatusId);
		String query = getSQLDatasetBaseEventSide(OdmExtractUtil.pairList(studyID, parentID), eventDefIDConstraints,
				itemIDConstraints, this.genDatabaseDateConstraint(eb),
				ecStatusConstraint, itStatusConstraint);
		logger.error("sqlDatasetBase_eventside=" + query);
		boolean bret = false;
		ResultSet rs = null;
		Connection connection = null;
		Statement ps = null;
		try {
			connection = getDataSource().getConnection();
			connection.setAutoCommit(false);
			if (connection.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: DatasetDAO.select!");	
				}
				throw new SQLException();
			}
			ps = connection.createStatement();
			ps.setFetchSize(QUERY_FETCH_SIZE);

			rs = ps.executeQuery(query);
			if (logger.isInfoEnabled()) {
				logger.trace("Executing static query, DatasetDAO.select: " + query);
			}
			signalSuccess();
			System.out.println("*** query that generates the event side records " + query);
			bret = processBaseEventSideRecords(rs, eb);
			bret = true;

		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while executing static query, DatasetDAO.select: " + query + ": "
						+ sqle.getMessage());
				sqle.printStackTrace();
			}
		} finally {
			this.closeIfNecessary(connection, rs, ps);
		}
		return bret;
	}

	protected boolean processBaseItemGroupSideRecords(ResultSet rs, ExtractBean eb) {

		try {
			while (rs.next()) {

				Integer vitemdataid = rs.getInt("itemdataid");
				Integer vitemdataordinal = rs.getInt("itemdataordinal");
				Integer itemGroupID = rs.getInt("item_group_id");

				String vitemgroupname = rs.getString("name");
				if (rs.wasNull()) {
					vitemgroupname = "";
				}

				if ("ungrouped".equalsIgnoreCase(vitemgroupname) && vitemdataordinal <= 0) {
					vitemdataordinal = 1;
				}

				String vitemdesc = rs.getString("itemdesc");
				if (rs.wasNull()) {
					vitemdesc = "";
				}

				String vitemname = rs.getString("itemname");
				if (rs.wasNull()) {
					vitemname = "";
				}

				String vitemvalue = rs.getString("itemvalue");
				if (rs.wasNull()) {
					vitemvalue = Utils.convertedItemDateValue("", oc_df_string, local_df_string);
				}

				String vitemunits = rs.getString("itemunits");
				if (rs.wasNull()) {
					vitemunits = "";
				}

				String vcrfversioname = rs.getString("crfversioname");
				if (rs.wasNull()) {
					vcrfversioname = "";
				}

				Integer vcrfversionstatusid = rs.getInt("crfversionstatusid");
				java.sql.Date vdateinterviewed = rs.getDate("dateinterviewed");

				String vinterviewername = rs.getString("interviewername");
				if (rs.wasNull()) {
					vinterviewername = "";
				}

				Timestamp veventcrfdatecompleted = rs.getTimestamp("eventcrfdatecompleted");
				Timestamp veventcrfdatevalidatecompleted = rs.getTimestamp("eventcrfdatevalidatecompleted");
				Integer veventcrfcompletionstatusid = rs.getInt("eventcrfcompletionstatusid");
				Integer repeatNumber = rs.getInt("repeat_number");
				Integer vcrfid = rs.getInt("crfid");
				Integer vstudysubjectid = rs.getInt("studysubjectid");
				Integer veventcrfid = rs.getInt("eventcrfid");
				Integer vitemid = rs.getInt("itemid");
				Integer vcrfversionid = rs.getInt("crfversionid");
				Integer eventcrfstatusid = rs.getInt("eventcrfstatusid");
				Integer itemdatatypeid = rs.getInt("itemDataTypeId");

				// add it to the HashMap
				eb.addEntryBASE_ITEMGROUPSIDE(
				/* Integer pitemDataId */vitemdataid,
				/* Integer vitemdataordinal */vitemdataordinal,
				/* Integer pitemGroupId */itemGroupID,
				/* String pitemGroupName */vitemgroupname, itemdatatypeid,
				/* String pitemDescription */vitemdesc,
				/* String pitemName */vitemname,
				/* String pitemValue */vitemvalue,
				/* String pitemUnits */vitemunits,
				/* String pcrfVersionName */vcrfversioname,
				/* Integer pcrfVersionStatusId */vcrfversionstatusid,
				/* Date pdateInterviewed */vdateinterviewed,
				/* String pinterviewerName, */vinterviewername,
				/* Timestamp peventCrfDateCompleted */veventcrfdatecompleted,
				/* Timestamp peventCrfDateValidateCompleted */veventcrfdatevalidatecompleted,
				/* Integer peventCrfCompletionStatusId */veventcrfcompletionstatusid,
				/* Integer repeat_number */repeatNumber,
				/* Integer crfId */vcrfid,
				/* Integer pstudySubjectId */vstudysubjectid,
				/* Integer peventCrfId */veventcrfid,
				/* Integer pitemId */vitemid,
				/* Integer pcrfVersionId */vcrfversionid, eventcrfstatusid);

			}
		} catch (SQLException sqle) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while processing result rows, EntityDAO.addHashMapEntryBASE_ITEMGROUPSIDE: "
						+ ": " + sqle.getMessage() + ": array length: " + eb.getHBASE_ITEMGROUPSIDE().size());
				sqle.printStackTrace();
			}
		}

		if (logger.isInfoEnabled()) {
			logger.trace("Loaded addHashMapEntryBASE_ITEMGROUPSIDE: " + eb.getHBASE_EVENTSIDE().size());
		}
		return true;
	}

	protected boolean processBaseEventSideRecords(ResultSet rs, ExtractBean eb) {

		try {
			while (rs.next()) {

				Integer vitemdataid = rs.getInt("itemdataid");
				Integer vstudysubjectid = rs.getInt("studysubjectid");
				Integer sampleOrdinal = rs.getInt("sample_ordinal");
				Integer studyEventDefID = rs.getInt("study_event_definition_id");

				String vname = rs.getString("name");
				if (rs.wasNull()) {
					vname = "";
				}

				String vlocation = rs.getString("location");
				if (rs.wasNull()) {
					vlocation = "";
				}

				Timestamp dateStart = rs.getTimestamp("date_start");
				Timestamp dateEnd = rs.getTimestamp("date_end");

				Boolean startTimeFlag;
				if (CoreResources.getDBType().equals("oracle")) {
					startTimeFlag = rs.getString("start_time_flag").equals("1");
					if (rs.wasNull()) {
						startTimeFlag = false;
					}
				} else {
					startTimeFlag = rs.getBoolean("start_time_flag");
					if (rs.wasNull()) {
						startTimeFlag = false;
					}
				}

				Boolean endTimeFlag;
				if (CoreResources.getDBType().equals("oracle")) {
					endTimeFlag = rs.getString("end_time_flag").equals("1");
					if (rs.wasNull()) {
						endTimeFlag = false;
					}
				} else {
					endTimeFlag = rs.getBoolean("end_time_flag");
					if (rs.wasNull()) {
						endTimeFlag = false;
					}
				}

				Integer statusID = rs.getInt("status_id");
				Integer subjectEventStatusID = rs.getInt("subject_event_status_id");
				Integer vstudyeventid = rs.getInt("studyeventid");
				Integer veventcrfid = rs.getInt("eventcrfid");
				Integer vitemid = rs.getInt("itemid");
				Integer vcrfversionid = rs.getInt("crfversionid");

				// add it to the HashMap
				eb.addEntryBASE_EVENTSIDE(
				/* Integer pitemDataId */vitemdataid,
				/* Integer pstudySubjectId */vstudysubjectid,
				/* Integer psampleOrdinal */sampleOrdinal,
				/* Integer pstudyEvenetDefinitionId */studyEventDefID,
				/* String pstudyEventDefinitionName */vname,
				/* String pstudyEventLoacation */vlocation,
				/* Timestamp pstudyEventDateStart */dateStart,
				/* Timestamp pstudyEventDateEnd */dateEnd,
				/* Boolean pstudyEventStartTimeFlag */startTimeFlag,
				/* Boolean pstudyEventEndTimeFlag */endTimeFlag,
				/* Integer pstudyEventStatusId */statusID,
				/* Integer pstudyEventSubjectEventStatusId */subjectEventStatusID,
				/* Integer pitemId */vitemid,
				/* Integer pcrfVersionId */vcrfversionid,
				/* Integer peventCrfId */veventcrfid,
				/* Integer pstudyEventId */vstudyeventid);

				eb.addItemDataIdEntry(vitemdataid);
			}
		} catch (SQLException sqle) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while processing result rows, EntityDAO.processBaseEventSideRecords: " + ": "
						+ sqle.getMessage() + ": array length: " + eb.getHBASE_EVENTSIDE().size());
				sqle.printStackTrace();
			}
		}

		if (logger.isInfoEnabled()) {
			logger.trace("Loaded addHashMapEntryBASE_EVENTSIDE: " + eb.getHBASE_EVENTSIDE().size());
		}
		return true;
	}
	
	protected String getSQLDatasetBaseEventSide(List<Map<Integer, Integer>> pairList, String eventDefIDConstraints, 
			String itemIDConstraints, String dateConstraint, String ecStatusConstraint, String itStatusConstraint) {

		return " SELECT  "
				+ " itemdataid,  "
				+ " studysubjectid, study_event.sample_ordinal,  "
				+ " study_event.study_event_definition_id,   "
				+ " study_event_definition.name, study_event.location, study_event.date_start, study_event.date_end, "
				+ " study_event.start_time_flag , study_event.end_time_flag , study_event.status_id, study_event.subject_event_status_id, "
				+ " itemid,  crfversionid,  eventcrfid, studyeventid "
				+ " FROM "
				+ " ( "
				+ "   SELECT item_data.item_data_id AS itemdataid, item_data.item_id AS itemid, item_data.value AS itemvalue, item.name AS itemname, item.description AS itemdesc,  "
				+ "   item.units AS itemunits, event_crf.event_crf_id AS eventcrfid, crf_version.name AS crfversioname, crf_version.crf_version_id AS crfversionid,  "
				+ "   event_crf.study_subject_id as studysubjectid, event_crf.study_event_id AS studyeventid "
				+ "   FROM item_data, item, event_crf "
				+ "   JOIN crf_version  ON event_crf.crf_version_id = crf_version.crf_version_id and (event_crf.status_id "
				+ ecStatusConstraint
				+ ") "
				+ "   WHERE  "
				+ "   item_data.item_id = item.item_id "
				+ "   AND "
				+ "   item_data.event_crf_id = event_crf.event_crf_id "
				+ "   AND "
				+ "   item_data.item_id IN "
				+ itemIDConstraints
				+ "   AND item_data.event_crf_id IN  "
				+ "   ( "
				+ "       SELECT event_crf_id FROM event_crf "
				+ "       WHERE  "
				+ "           event_crf.study_event_id IN  "
				+ "           ( "
				+ "               SELECT study_event_id FROM study_event  "
				+ "               WHERE "
				+ "                   study_event.study_event_definition_id IN "
				+ eventDefIDConstraints
				+ "                  AND  "
				+ "                   (   study_event.sample_ordinal IS NOT NULL AND "
				+ "                       study_event.date_start IS NOT NULL  "
				+ "                   ) "
				+ "                  AND "
				+ "                   study_event.study_subject_id IN "
				+ "                  ( "
				+ "                   SELECT DISTINCT study_subject.study_subject_id "
				+ "                    FROM   study_subject   "
				+ "                    JOIN   study           ON ( "
				+ "                                       study.study_id = study_subject.study_id  "
				+ "                                      AND "
				+ "                                       (study.study_id in "
				+ OdmExtractUtil.keysAsSql(pairList)
				+ "OR study.parent_study_id in "
				+ OdmExtractUtil.valuesAsSql(pairList)
				+ ") "
				+ "                                      ) "
				+ "                    JOIN   subject         ON study_subject.subject_id = subject.subject_id "
				+ "                    JOIN   study_event_definition  ON ( "
				+ "                                       study.study_id = study_event_definition.study_id "
				+ "                                       OR "
				+ "                                       study.parent_study_id = study_event_definition.study_id "
				+ "                                      ) "
				+ "                    JOIN   study_event         ON ( "
				+ "                                       study_subject.study_subject_id = study_event.study_subject_id  "
				+ "                                      AND "
				+ "                                       study_event_definition.study_event_definition_id = study_event.study_event_definition_id  "
				+ "                                      ) "
				+ "                    JOIN   event_crf       ON ( "
				+ "                                       study_event.study_event_id = event_crf.study_event_id  "
				+ "                                      AND  "
				+ "                                       study_event.study_subject_id = event_crf.study_subject_id  "
				+ "                                      AND "
				+ "                                       (event_crf.status_id "
				+ ecStatusConstraint
				+ ") "
				+ "                                      ) "
				+ "                   WHERE "
				+ dateConstraint
				+ "                       AND "
				+ "                       study_event_definition.study_event_definition_id IN "
				+ eventDefIDConstraints
				+ "                  )  "
				+ "           ) "
				+ "           AND study_subject_id IN ( "
				+ "               SELECT DISTINCT study_subject.study_subject_id "
				+ "                FROM   study_subject   "
				+ "                JOIN   study           ON ( "
				+ "                                   study.study_id = study_subject.study_id  "
				+ "                                  AND "
				+ "                                   (study.study_id in "
				+ OdmExtractUtil.keysAsSql(pairList)
				+ " OR study.parent_study_id in "
				+ OdmExtractUtil.valuesAsSql(pairList)
				+ ") "
				+ "                                  ) "
				+ "                JOIN   subject         ON study_subject.subject_id = subject.subject_id "
				+ "                JOIN   study_event_definition  ON ( "
				+ "                                   study.study_id = study_event_definition.study_id  "
				+ "                                   OR  "
				+ "                                   study.parent_study_id = study_event_definition.study_id "
				+ "                                  ) "
				+ "                JOIN   study_event         ON ( "
				+ "                                   study_subject.study_subject_id = study_event.study_subject_id  "
				+ "                                  AND "
				+ "                                   study_event_definition.study_event_definition_id = study_event.study_event_definition_id  "
				+ "                                  ) "
				+ "                JOIN   event_crf       ON ( "
				+ "                                   study_event.study_event_id = event_crf.study_event_id  "
				+ "                                  AND  "
				+ "                                   study_event.study_subject_id = event_crf.study_subject_id  "
				+ "                                  AND "
				+ "                                   (event_crf.status_id "
				+ ecStatusConstraint
				+ ") "
				+ "                                  ) "
				+ "               WHERE "
				+ dateConstraint
				+ "                   AND "
				+ "                   study_event_definition.study_event_definition_id IN "
				+ eventDefIDConstraints
				+ "           ) "
				+ "           AND "
				+ "           (event_crf.status_id "
				+ ecStatusConstraint
				+ ") "
				+ "   )  "
				+ "   AND  "
				+ "   (item_data.status_id "
				+ itStatusConstraint
				+ ")  "
				+ " ) AS SBQONE, study_event, study_event_definition "
				+ " WHERE  "
				+ " (study_event.study_event_id = SBQONE.studyeventid) "
				+ " AND "
				+ " (study_event.study_event_definition_id = study_event_definition.study_event_definition_id) "
				+ " ORDER BY itemdataid asc ";
	}
	
	protected String getSQLDatasetBaseItemGroupSide(int studyID, int studyParentID, String eventDefIDConstraints, 
			String itemIDConstraints, String dateConstraint, String ecStatusConstraint, String itStatusConstraint) {

		return " SELECT  "
				+ " itemdataid,  itemdataordinal,"
				+ " item_group_metadata.item_group_id , item_group.name, itemdatatypeid, itemdesc, itemname, itemvalue, itemunits, "
				+ " crfversioname, crfversionstatusid, crfid, item_group_metadata.repeat_number, "
				+ " dateinterviewed, interviewername, eventcrfdatevalidatecompleted, eventcrfdatecompleted, eventcrfcompletionstatusid, "
				+ " studysubjectid, eventcrfid, itemid, crfversionid, eventcrfstatusid "
				+ " FROM "
				+ " ( "
				+ "   SELECT item_data.item_data_id AS itemdataid, item_data.item_id AS itemid, item_data.value AS itemvalue, item_data.ordinal AS itemdataordinal, item.item_data_type_id AS itemdatatypeid, item.name AS itemname, item.description AS itemdesc,  "
				+ "   item.units AS itemunits, event_crf.event_crf_id AS eventcrfid, crf_version.name AS crfversioname, crf_version.crf_version_id AS crfversionid,  "
				+ "   event_crf.study_subject_id as studysubjectid, crf_version.status_id AS crfversionstatusid, crf_version.crf_id AS crfid, "
				+ "   event_crf.date_interviewed AS dateinterviewed, event_crf.interviewer_name AS interviewername, event_crf.date_completed AS eventcrfdatecompleted, "
				+ "   event_crf.date_validate_completed AS eventcrfdatevalidatecompleted, event_crf.completion_status_id AS eventcrfcompletionstatusid, event_crf.status_id AS eventcrfstatusid "
				+ "   FROM item_data, item, event_crf "
				+ "   join crf_version  ON event_crf.crf_version_id = crf_version.crf_version_id and (event_crf.status_id "
				+ ecStatusConstraint
				+ ") "
				+ "   WHERE  "
				+ "   item_data.item_id = item.item_id "
				+ "   AND "
				+ "   item_data.event_crf_id = event_crf.event_crf_id "
				+ "   AND "
				+ "   item_data.item_id IN "
				+ itemIDConstraints
				+ "   AND item_data.event_crf_id IN "
				+ "   ( "
				+ "       SELECT event_crf_id FROM event_crf "
				+ "       WHERE  "
				+ "           event_crf.study_event_id IN  "
				+ "           ( "
				+ "               SELECT study_event_id FROM study_event  "
				+ "               WHERE "
				+ "                   study_event.study_event_definition_id IN "
				+ eventDefIDConstraints
				+ "                  AND  "
				+ "                   (   study_event.sample_ordinal IS NOT NULL AND "
				+ "                       study_event.location IS NOT NULL AND "
				+ "                       study_event.date_start IS NOT NULL  "
				+ "                   ) "
				+ "                  AND "
				+ "                   study_event.study_subject_id IN "
				+ "                  ( "
				+ "                   SELECT DISTINCT study_subject.study_subject_id "
				+ "                    FROM   study_subject   "
				+ "                    JOIN   study           ON ( "
				+ "                                       study.study_id = study_subject.study_id  "
				+ "                                      AND "
				+ "                                       (study.study_id="
				+ studyID
				+ " OR study.parent_study_id= "
				+ studyParentID
				+ ") "
				+ "                                      ) "
				+ "                    JOIN   subject         ON study_subject.subject_id = subject.subject_id "
				+ "                    JOIN   study_event_definition  ON ( "
				+ "                                       study.study_id = study_event_definition.study_id  "
				+ "                                       OR  "
				+ "                                       study.parent_study_id = study_event_definition.study_id "
				+ "                                      ) "
				+ "                    JOIN   study_event         ON ( "
				+ "                                       study_subject.study_subject_id = study_event.study_subject_id  "
				+ "                                      AND "
				+ "                                       study_event_definition.study_event_definition_id = study_event.study_event_definition_id  "
				+ "                                      ) "
				+ "                    JOIN   event_crf       ON ( "
				+ "                                       study_event.study_event_id = event_crf.study_event_id  "
				+ "                                      AND  "
				+ "                                       study_event.study_subject_id = event_crf.study_subject_id  "
				+ "                                      AND "
				+ "                                       (event_crf.status_id "
				+ ecStatusConstraint
				+ ") "
				+ "                                      ) "
				+ "                   WHERE "
				+ dateConstraint
				+ "                       AND "
				+ "                       study_event_definition.study_event_definition_id IN "
				+ eventDefIDConstraints
				+ "                  )  "
				+ "           ) "
				+ "           AND study_subject_id IN ( "
				+ "               SELECT DISTINCT study_subject.study_subject_id "
				+ "                FROM   study_subject   "
				+ "                JOIN   study           ON ( "
				+ "                                   study.study_id = study_subject.study_id  "
				+ "                                  AND "
				+ "                                   (study.study_id="
				+ studyID
				+ " OR study.parent_study_id= "
				+ studyParentID
				+ " )"
				+ "                                  ) "
				+ "                JOIN   subject         ON study_subject.subject_id = subject.subject_id "
				+ "                JOIN   study_event_definition  ON ( "
				+ "                                   study.study_id = study_event_definition.study_id  "
				+ "                                   OR  "
				+ "                                   study.parent_study_id = study_event_definition.study_id "
				+ "                                  ) "
				+ "                JOIN   study_event         ON ( "
				+ "                                   study_subject.study_subject_id = study_event.study_subject_id  "
				+ "                                  AND "
				+ "                                   study_event_definition.study_event_definition_id = study_event.study_event_definition_id  "
				+ "                                  ) "
				+ "                JOIN   event_crf       ON ( "
				+ "                                   study_event.study_event_id = event_crf.study_event_id  "
				+ "                                  AND  "
				+ "                                   study_event.study_subject_id = event_crf.study_subject_id  "
				+ "                                  AND "
				+ "                                   (event_crf.status_id "
				+ ecStatusConstraint
				+ ") "
				+ "                                  ) "
				+ "               WHERE "
				+ dateConstraint
				+ "                   AND "
				+ "                   study_event_definition.study_event_definition_id IN "
				+ eventDefIDConstraints
				+ "           ) "
				+ "           AND "
				+ "           (event_crf.status_id "
				+ ecStatusConstraint
				+ ") "
				+ "   )  "
				+ "   AND  "
				+ "   (item_data.status_id "
				+ itStatusConstraint
				+ ")  "
				+ " ) AS SBQONE, item_group_metadata, item_group "
				+ " WHERE  "
				+ " (item_group_metadata.item_id = SBQONE.itemid AND item_group_metadata.crf_version_id = SBQONE.crfversionid) "
				+ " AND "
				+ " (item_group.item_group_id = item_group_metadata.item_group_id) "
				+ "  ORDER BY itemdataid asc ";
	}
	
	protected String getSQLInKeyDatasetHelper(int studyID, int studyParentID, String eventDefIDConstraints, 
			String itemIDConstraints, String dateConstraint, String ecStatusConstraint, String itStatusConstraint) {

		return "   SELECT DISTINCT  "
				+ "   study_event.study_event_definition_id,  "
				+ "   study_event.sample_ordinal,  "
				+ "   crfv.crf_id,  "
				+ "   it.item_id,  "
				+ "   ig.name AS item_group_name  "
				+ "    FROM  "
				+ "   event_crf ec  "
				+ " JOIN crf_version crfv ON ec.crf_version_id = crfv.crf_version_id AND (ec.status_id "
				+ ecStatusConstraint
				+ ") "
				+ " JOIN item_form_metadata ifm ON crfv.crf_version_id = ifm.crf_version_id  "
				+ " LEFT JOIN item_group_metadata igm ON ifm.item_id = igm.item_id AND crfv.crf_version_id = igm.crf_version_id  "
				+ " LEFT JOIN item_group ig ON igm.item_group_id = ig.item_group_id  "
				+ " JOIN item it ON ifm.item_id = it.item_id  "
				+ " JOIN study_event ON study_event.study_event_id = ec.study_event_id AND study_event.study_subject_id = ec.study_subject_id   "
				+ " WHERE ec.event_crf_id IN  "
				+ " (  "
				+ "   SELECT DISTINCT eventcrfid FROM  "
				+ "   (     "
				+ getSQLDatasetBaseEventSide(OdmExtractUtil.pairList(studyID, studyParentID), eventDefIDConstraints,
				itemIDConstraints, dateConstraint,
				ecStatusConstraint, itStatusConstraint) + "   ) AS SBQTWO " + " ) ";
	}
	
	protected HashMap setHashMapInKeysHelper(int studyID, int parentID, String eventDefIDConstraints, 
			String itemIDConstraints, String dateConstraint, String ecStatusConstraint, String itStatusConstraint) {

		clearSignals();
		String query = getSQLInKeyDatasetHelper(studyID, parentID, eventDefIDConstraints, itemIDConstraints, 
				dateConstraint, ecStatusConstraint, itStatusConstraint);
		HashMap results = new HashMap();
		ResultSet rs = null;
		Connection connection = null;
		Statement ps = null;
		try {
			connection = getDataSource().getConnection();
			if (connection.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: setHashMapInKeysHelper.select!");
				}
				throw new SQLException();
			}
			ps = connection.createStatement();
			rs = ps.executeQuery(query);
			if (logger.isInfoEnabled()) {
				logger.trace("Executing static query, setHashMapInKeysHelper.select: " + query);
			}
			signalSuccess();
			results = this.processInKeyDataset(rs);

		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while executing static query, DatasetDAO.select: " + query + ": "
						+ sqle.getMessage());
				sqle.printStackTrace();
			}
		} finally {
			this.closeIfNecessary(connection, rs, ps);
		}
		return results;
	}
	
	protected HashMap processInKeyDataset(ResultSet rs) {
		
		HashMap al = new HashMap();
		try {
			while (rs.next()) {
				String stsed;
				stsed = ((Integer) rs.getInt("study_event_definition_id")).toString();
				if (rs.wasNull()) {
					stsed = "";
				}

				String stso;
				stso = ((Integer) rs.getInt("sample_ordinal")).toString();
				if (rs.wasNull()) {
					stso = "";
				}

				String stcrf;
				stcrf = ((Integer) rs.getInt("crf_id")).toString();
				if (rs.wasNull()) {
					stcrf = "";
				}

				String stitem;
				stitem = ((Integer) rs.getInt("item_id")).toString();
				if (rs.wasNull()) {
					stitem = "";
				}

				String stgn;
				stgn = rs.getString("item_group_name");
				if (rs.wasNull()) {
					stgn = "";
				}

				String key = stsed + "_" + stso + "_" + stcrf + "_" + stitem + "_" + stgn;
				al.put(key, true);
			}
		} catch (SQLException sqle) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while processing result rows, EntityDAO.loadExtractStudySubject: " + ": "
						+ sqle.getMessage() + ": array length: " + al.size());
				sqle.printStackTrace();
			}
		}
		return al;
	}

	/**
	 * Constructs data base date constraints.
	 *
	 * @param eb ExtractBean object
	 * @return date constraint string
	 */
	public String genDatabaseDateConstraint(ExtractBean eb) {
		
		String dateConstraint = "";
		String dbName = CoreResources.getDBType();
		String sql = eb.getDataset().getSQLStatement();
		String[] os = sql.split("'");
		if ("postgres".equalsIgnoreCase(dbName)) {
			dateConstraint = " (date(study_subject.enrollment_date) >= date('" + os[DATE_RANGE_LOWER_BOUND_INDEX]
					+ "')) and (date(study_subject.enrollment_date) <= date('" + os[DATE_RANGE_UPPER_BOUND_INDEX] + "'))";
		} else if ("oracle".equalsIgnoreCase(dbName)) {
			dateConstraint = " trunc(study_subject.enrollment_date) >= to_date('" + os[DATE_RANGE_LOWER_BOUND_INDEX]
					+ "') and trunc(study_subject.enrollment_date) <= to_date('" + os[DATE_RANGE_UPPER_BOUND_INDEX] + "')";
		}
		return dateConstraint;
	}
}
