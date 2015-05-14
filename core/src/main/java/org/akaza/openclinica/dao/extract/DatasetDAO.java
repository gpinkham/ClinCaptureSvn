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

import org.akaza.openclinica.bean.core.DatasetItemStatus;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.ExtractBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.submit.ItemDAO;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * The data access object for datasets; also generates datasets based on their query and criteria set; also generates
 * the extract bean, which holds dataset information.
 * 
 * @author thickerson
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DatasetDAO extends AuditableEntityDAO {

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
	 * @param ds DataSource
	 */
	public DatasetDAO(DataSource ds) {
		super(ds);
		this.setQueryNames();
	}

	/**
	 * Creates a DatasetDAO object suitable for testing purposes only.
	 * 
	 * @param ds DataSource
	 * @param digester DAODigester
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
		this.setTypeExpected(index++, TypeNames.DATE); // last run
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
		this.setTypeExpected(index, TypeNames.STRING); //sed_id_and_crf_id_pairs
	}

	/**
	 * Set Extract types expected.
	 */
	public void setExtractTypesExpected() {
		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT); // subj id
		this.setTypeExpected(index++, TypeNames.STRING); // subj identifier
		this.setTypeExpected(index++, TypeNames.INT); // study id
		this.setTypeExpected(index++, TypeNames.STRING); // study ident
		this.setTypeExpected(index++, TypeNames.INT); // event def crf id
		this.setTypeExpected(index++, TypeNames.INT); // crf id
		this.setTypeExpected(index++, TypeNames.STRING); // crf label
		this.setTypeExpected(index++, TypeNames.STRING); // crf name
		this.setTypeExpected(index++, TypeNames.INT); // version id
		this.setTypeExpected(index++, TypeNames.STRING); // version label
		this.setTypeExpected(index++, TypeNames.STRING); // version name
		this.setTypeExpected(index++, TypeNames.INT); // study event id
		this.setTypeExpected(index++, TypeNames.INT); // event crf id
		this.setTypeExpected(index++, TypeNames.INT); // item data id
		this.setTypeExpected(index++, TypeNames.STRING); // value
		this.setTypeExpected(index++, TypeNames.STRING); // sed.name
		this.setTypeExpected(index++, TypeNames.BOOL); // repeating
		this.setTypeExpected(index++, TypeNames.INT); // sample ordinal
		this.setTypeExpected(index++, TypeNames.INT); // item id
		this.setTypeExpected(index++, TypeNames.STRING); // item name
		this.setTypeExpected(index++, TypeNames.STRING); // item desc
		this.setTypeExpected(index++, TypeNames.STRING); // item units
		this.setTypeExpected(index++, TypeNames.DATE); // date created for item
		this.setTypeExpected(index++, TypeNames.INT); // study event definition id
		this.setTypeExpected(index++, TypeNames.STRING); // option stings
		this.setTypeExpected(index++, TypeNames.STRING); // option values
		this.setTypeExpected(index++, TypeNames.INT); // response type id
		this.setTypeExpected(index++, TypeNames.STRING); // gender
		this.setTypeExpected(index++, TypeNames.DATE); // dob
		this.setTypeExpected(index++, TypeNames.INT); // s.status_id AS
		this.setTypeExpected(index++, TypeNames.STRING); // s.unique_identifier,
		this.setTypeExpected(index++, TypeNames.BOOL); // s.dob_collected,
		this.setTypeExpected(index++, TypeNames.INT); // ec.completion_status_id,
		this.setTypeExpected(index++, TypeNames.DATE); // ec.date_created AS
		this.setTypeExpected(index++, TypeNames.INT); // crfv.status_id AS
		this.setTypeExpected(index++, TypeNames.STRING); // ec.interviewer_name,
		this.setTypeExpected(index++, TypeNames.DATE); // ec.date_interviewed,
		this.setTypeExpected(index++, TypeNames.DATE); // ec.date_completed AS
		this.setTypeExpected(index++, TypeNames.DATE); // ec.date_validate_completed
		this.setTypeExpected(index++, TypeNames.INT); // sgmap.study_group_id,
		this.setTypeExpected(index++, TypeNames.INT); // sgmap.study_group_class_id
		this.setTypeExpected(index++, TypeNames.STRING); // location
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date start. YW,
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date end
		this.setTypeExpected(index++, TypeNames.INT); // item data ordinal, added tbh
		this.setTypeExpected(index++, TypeNames.STRING); // item group name, added
		this.setTypeExpected(index++, TypeNames.STRING); // secondary label
		this.setTypeExpected(index++, TypeNames.INT); // item_data_type_id
		this.setTypeExpected(index++, TypeNames.STRING); // study_event_definition_oid
		this.setTypeExpected(index++, TypeNames.STRING); // crf_version_oid
		this.setTypeExpected(index++, TypeNames.STRING); // item_group_oid
		this.setTypeExpected(index++, TypeNames.STRING); // item_oid
		this.setTypeExpected(index++, TypeNames.STRING); // study_subject_oid
		this.setTypeExpected(index++, TypeNames.INT); // sed_order
		this.setTypeExpected(index++, TypeNames.INT); // crf_order
		this.setTypeExpected(index, TypeNames.INT); // item_order
	}

	/**
	 * Set Item types expected.
	 */
	public void setDefinitionCrfItemTypesExpected() {
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
		this.setTypeExpected(index++, TypeNames.DATE); // created
		this.setTypeExpected(index++, TypeNames.DATE); // updated
		this.setTypeExpected(index++, TypeNames.INT); // update id
		this.setTypeExpected(index++, TypeNames.STRING); // oc_oid
		this.setTypeExpected(index++, TypeNames.STRING); // sas_name
		this.setTypeExpected(index++, TypeNames.INT); // sed_id
		this.setTypeExpected(index++, TypeNames.STRING); // sed_name
		this.setTypeExpected(index++, TypeNames.INT); // crf_id
		this.setTypeExpected(index++, TypeNames.STRING); // crf_name
        this.setTypeExpected(index++, TypeNames.INT); // cv_version_id
        this.setTypeExpected(index, TypeNames.STRING); // cv_name
	}

	/**
	 * Update DatasetBean.
	 * @param eb EntityBean.
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
            nullVars.put(index, Types.DATE);
        }
		variables.put(index++, db.getDateStart());
        if (db.getDateEnd() == null) {
            nullVars.put(index, Types.DATE);
        }
		variables.put(index++, db.getDateEnd());
		variables.put(index, db.getId());
		this.execute(digester.getQuery("update"), variables, nullVars);
		return eb;
	}

	/**
	 * Create DatasetBean.
	 * @param eb EntityBean
	 * @return EntityBean
	 */
	public EntityBean create(EntityBean eb) {
		DatasetBean db = (DatasetBean) eb;
        String excludeItems = "";
		String sedIdAndCrfIdPairs = "";
        for (String key : (Set<String>) db.getItemMap().keySet()) {
            ItemBean ib = (ItemBean) db.getItemMap().get(key);
            if (!ib.isSelected()) {
                excludeItems += (excludeItems.isEmpty() ? "" : ",") + key;
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
            nullVars.put(index, Types.DATE);
        }
		variables.put(index++, db.getDateStart());
        if (db.getDateEnd() == null) {
            nullVars.put(index, Types.DATE);
        }
		variables.put(index++, db.getDateEnd());
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
		if (!sedIdAndCrfIdPairs.contains(sedAndCrfId)) {
			sedIdAndCrfIdPairs += (sedIdAndCrfIdPairs.isEmpty() ? "" : ",") + sedAndCrfId;
		}
		return sedIdAndCrfIdPairs;
	}

	/**
	 * Get entity from hash map.
	 * @param hm HashMap
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
	 * @param datasetId int
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
	 * @param currentStudy StudyBean
	 * @return Collection
	 */
	public Collection findTopFive(StudyBean currentStudy) {
		int studyId = currentStudy.getId();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);
		ArrayList alist = this.select(digester.getQuery("findTopFive"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			DatasetBean eb = (DatasetBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find by owner id, reports a list of datasets by user account id.
	 * 
	 * @param ownerId
	 *            studyId
	 * @param studyId int
	 * @return Collection
	 */
	public Collection findByOwnerId(int ownerId, int studyId) {
		this.setTypesExpected();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, studyId);
		variables.put(index++, studyId);
		variables.put(index, ownerId);

		ArrayList alist = this.select(digester.getQuery("findByOwnerId"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			DatasetBean eb = (DatasetBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Not implemented.
	 * @param strOrderByColumn String
	 * @param blnAscendingSort boolean
	 * @param strSearchPhrase String
	 * @return new ArrayList
	 */
	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * Find by ID.
	 * @param id int
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
	 * @param name dataset name
	 * @param study StudyBean
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
	 * @param eb The ExtractBean containing the dataset and study for which data is being retrieved.
	 * @param currentstudyid int
	 * @param parentstudyid int
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
		ArrayList newRows = selectStudySubjects(currentstudyid, parentstudyid, stSedIn, stItemidIn,
				this.genDatabaseDateConstraint(eb), ecStatusConstraint, itStatusConstraint);
		// Add it to ths subjects
		eb.addStudySubjectData(newRows);
		// II. Add the study_event records
		HashMap nhInHelpKeys = setHashMapInKeysHelper(currentstudyid, parentstudyid, stSedIn, stItemidIn,
				this.genDatabaseDateConstraint(eb), ecStatusConstraint, itStatusConstraint);
		eb.setHmInKeys(nhInHelpKeys);
		// Get the arrays of ArrayList for SQL BASE There are split in two querries for perfomance
		eb.resetArrayListEntryBASE_ITEMGROUPSIDE();
		loadBASE_EVENTINSIDEHashMap(currentstudyid, parentstudyid, stSedIn, stItemidIn, eb);
		loadBASE_ITEMGROUPSIDEHashMap(currentstudyid, parentstudyid, stSedIn, stItemidIn, eb);
		// add study_event data
		eb.addStudyEventData();
		// add item_data
		eb.addItemData();
		return eb;
	}

	/**
	 * Not implemented!
	 * @param objCurrentUser Object
	 * @param intActionType int
	 * @param strOrderByColumn String
	 * @param blnAscendingSort boolean
	 * @param strSearchPhrase String
	 * @return new ArrayList
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * Not implemented!
	 * @param objCurrentUser Object
	 * @param intActionType int
	 * @return new ArrayList
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	/**
	 * Find all by studyId.
	 * @param studyId int.
	 * @return ArrayList
	 */
	public ArrayList findAllByStudyId(int studyId) {
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);

		return executeFindAllQuery("findAllByStudyId", variables);
	}

	/**
	 * Find all by studyId admin.
	 * @param studyId int
	 * @return ArrayList
	 */
	public ArrayList findAllByStudyIdAdmin(int studyId) {
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);

		return executeFindAllQuery("findAllByStudyIdAdmin", variables);
	}

	/**
	 * Select DefinitionCRFItems.
	 * @param sedIds String
	 * @param itemIds String
	 * @return ArrayList
	 */
	public ArrayList selectDefinitionCrfItems(String sedIds, String itemIds) {
		return select(getDefinitionCrfItemSql(sedIds, itemIds));
	}

	private String getDefinitionCrfItemSql(String sedIds, String itemIds) {
		return "select item.*, sed.study_event_definition_id as sed_id, sed.name as sed_name, crf.crf_id, crf.name as crf_name, "
				+ " cv.crf_version_id as cv_version_id,  cv.name as cv_name"
				+ " from study_event_definition sed, event_definition_crf edc, crf, crf_version cv,item_form_metadata ifm, item"
				+ " where sed.study_event_definition_id in "
				+ sedIds
				+ " and item.item_id in "
				+ itemIds
				+ " and sed.study_event_definition_id = edc.study_event_definition_id and edc.crf_id = crf.crf_id"
				+ " and crf.crf_id = cv.crf_id and cv.crf_version_id = ifm.crf_version_id and ifm.item_id = item.item_id";
	}

	/**
	 * Update all columns of the dataset table except owner_id.
	 * 
	 * @param eb EntityBean
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
                excludeItems += (excludeItems.isEmpty() ? "" : ",") + key;
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
            nullVars.put(index, Types.DATE);
        }
		variables.put(index++, db.getDateStart());
        if (db.getDateEnd() == null) {
            nullVars.put(index, Types.DATE);
        }
		variables.put(index++, db.getDateEnd());
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
	 * @param db DatasetBean
	 * @return EntityBean
	 */
	public EntityBean updateGroupMap(DatasetBean db) {
		HashMap nullVars = new HashMap();
		db.setActive(false);
		boolean success = true;

		ArrayList<Integer> sgcIds = this.getGroupIds(db.getId());
		if (sgcIds == null) {
			sgcIds = new ArrayList<Integer>();
		}
		ArrayList<Integer> dbSgcIds = (ArrayList<Integer>) db.getSubjectGroupIds().clone();
		if (dbSgcIds == null) {
			dbSgcIds = new ArrayList<Integer>();
		}
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
	 * @param sql String
	 * @param issed boolean
	 * @param hasfilterzero boolean
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
				stsedIn = stsedIn + ",";
			}
		}
		String stitIn = "";
		for (int ij = 0; ij < itvec.size(); ij++) {
			stitIn = stitIn + itvec.get(ij).toString();
			if (ij != itvec.size() - 1) {
				stitIn = stitIn + ",";
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
}
