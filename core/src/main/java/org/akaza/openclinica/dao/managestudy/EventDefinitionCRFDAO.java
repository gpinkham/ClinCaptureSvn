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

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.domain.SourceDataVerification;

/**
 * EventDefinitionCRFDAO class.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class EventDefinitionCRFDAO extends AuditableEntityDAO {

	private void setQueryNames() {
		getCurrentPKName = "getCurrentPK";
		getNextPKName = "getNextPK";
		findAllByStudyName = "findAllByStudy";
	}

	/**
	 * EventDefinitionCRFDAO constructor.
	 * 
	 * @param ds
	 *            DataSource
	 */
	public EventDefinitionCRFDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	/**
	 * EventDefinitionCRFDAO constructor.
	 * 
	 * @param ds
	 *            DataSource
	 * @param digester
	 *            DAODigester
	 */
	public EventDefinitionCRFDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
		setQueryNames();
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_EVENTDEFINITIONCRF;
	}

	@Override
	public void setTypesExpected() {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.BOOL);

		this.setTypeExpected(index++, TypeNames.BOOL);
		this.setTypeExpected(index++, TypeNames.BOOL);
		this.setTypeExpected(index++, TypeNames.BOOL);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);

		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.BOOL);
		// Issue 3212
		this.setTypeExpected(index++, TypeNames.BOOL);
		this.setTypeExpected(index++, TypeNames.INT); // source_data_verification_id
		this.setTypeExpected(index++, TypeNames.STRING); // selected_version_ids
		this.setTypeExpected(index++, TypeNames.INT); // parent_id
		this.setTypeExpected(index++, TypeNames.STRING); // email_step
		this.setTypeExpected(index++, TypeNames.STRING); // email_to
		this.setTypeExpected(index++, TypeNames.BOOL); // evaluated_crf
		this.setTypeExpected(index++, TypeNames.STRING); // tabbing_mode
		this.setTypeExpected(index, TypeNames.BOOL); // accept_new_crf_versions
	}

	/**
	 * Method that prepare object from HashMap data.
	 * <p>
	 * getEntityFromHashMap, the method that gets the object from the database query.
	 * 
	 * @param hm
	 *            HashMap
	 * @return Object
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		EventDefinitionCRFBean eb = new EventDefinitionCRFBean();
		super.setEntityAuditInformation(eb, hm);
		// EVENT_DEFINITION_CRF_ID STUDY_EVENT_DEFINITION_ID STUDY_ID
		// CRF_ID REQUIRED_CRF DOUBLE_ENTRY REQUIRE_ALL_TEXT_FILLED
		// DECISION_CONDITIONS DEFAULT_VERSION_ID STATUS_ID OWNER_ID
		// DATE_CREATED DATE_UPDATED UPDATE_ID
		eb.setId((Integer) hm.get("event_definition_crf_id"));
		eb.setStudyEventDefinitionId((Integer) hm.get("study_event_definition_id"));
		eb.setStudyId((Integer) hm.get("study_id"));
		eb.setCrfId((Integer) hm.get("crf_id"));
		eb.setRequiredCRF((Boolean) hm.get("required_crf"));
		eb.setDoubleEntry((Boolean) hm.get("double_entry"));
		eb.setRequireAllTextFilled((Boolean) hm.get("require_all_text_filled"));
		eb.setDecisionCondition((Boolean) hm.get("decision_conditions"));
		eb.setNullValues((String) hm.get("null_values"));
		eb.setDefaultVersionId((Integer) hm.get("default_version_id"));
		eb.setOrdinal((Integer) hm.get("ordinal"));
		eb.setElectronicSignature((Boolean) hm.get("electronic_signature"));
		String crfName = (String) hm.get("crf_name");
		eb.setCrfName(crfName != null ? crfName : eb.getCrfName());
		// issue 3212
		eb.setHideCrf(((Boolean) hm.get("hide_crf")));
		int sdvId = (Integer) hm.get("source_data_verification_code");
		final int defSDVId = 3;
		eb.setSourceDataVerification(SourceDataVerification.getByCode(sdvId > 0 ? sdvId : defSDVId));
		String selectedVersionIds = (String) hm.get("selected_version_ids");
		eb.setSelectedVersionIds(selectedVersionIds != null ? selectedVersionIds : "");
		int parentId = (Integer) hm.get("parent_id");
		eb.setParentId(parentId > 0 ? parentId : 0);
		String emailTo = (String) hm.get("email_to");
		eb.setEmailTo(emailTo != null ? emailTo : "");
		String emailStep = (String) hm.get("email_step");
		eb.setEmailStep(emailStep != null ? emailStep : "");
		eb.setEvaluatedCRF(((Boolean) hm.get("evaluated_crf")));
		eb.setTabbingMode(((String) hm.get("tabbing_mode")));
		eb.setAcceptNewCrfVersions(((Boolean) hm.get("accept_new_crf_versions")));
		return eb;
	}

	/**
	 * Mehod that finds all event definition crfs.
	 * 
	 * @return Collection
	 */
	public Collection findAll() {
		this.setTypesExpected();
		List<HashMap> aList = this.select(digester.getQuery("findAll"));
		ArrayList al = new ArrayList();
		for (HashMap anAList : aList) {
			EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Mehod that finds all event definition crfs by definitionId.
	 * 
	 * @param definitionId
	 *            int
	 * @return Collection
	 */
	public Collection findAllByDefinition(int definitionId) {
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index, definitionId);

		String sql = digester.getQuery("findAllByDefinition");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (HashMap anAList : aList) {
			EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find all EventDefinitionCRFBean for the StudyBean.
	 * 
	 * @param study
	 *            StudyBean
	 * @param definitionId
	 *            int
	 * @return Collection
	 */
	public Collection findAllByDefinition(StudyBean study, int definitionId) {
		return study.isSite(study.getParentStudyId()) ? findAllByDefinitionAndSiteIdAndParentStudyId(definitionId,
				study.getId(), study.getParentStudyId()) : findAllParentsByDefinition(definitionId);
	}

	/**
	 * Method returns required EventCRFDefIds that should be SDVed.
	 * 
	 * @param studyBean
	 *            StudyBean
	 * @return List<Integer>
	 */
	public List<Integer> getRequiredEventCRFDefIdsThatShouldBeSDVd(StudyBean studyBean) {
		int index = 1;
		List<Integer> result = new ArrayList<Integer>();
		unsetTypeExpected();
		setTypeExpected(index++, TypeNames.INT);
		setTypeExpected(index, TypeNames.INT);
		index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, studyBean.getId());
		variables.put(index, studyBean.getParentStudyId());
		String sql = digester.getQuery("requiredEventCRFDefIdsThatShouldBeSDVd");
		List<HashMap> rows = this.select(sql, variables);
		for (HashMap row : rows) {
			result.add((Integer) row.get("event_definition_crf_id"));
		}
		return result;
	}

	/**
	 * Find all EventDefinitionCRFBean which have no parent EventDefinitionCRFBean.
	 * 
	 * @param definitionId
	 *            int
	 * @return Collection
	 */
	public Collection findAllParentsByDefinition(int definitionId) {
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index, definitionId);

		String sql = digester.getQuery("findAllParentsByDefinition");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (HashMap anAList : aList) {
			EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find all EventDefinitionCRFBean which have no parent EventDefinitionCRFBean.
	 * 
	 * @param definitionId
	 *            int
	 * @return ArrayList<EventDefinitionCRFBean>
	 */
	public ArrayList<EventDefinitionCRFBean> findAllChildrenByDefinition(int definitionId) {
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index, definitionId);

		String sql = digester.getQuery("findAllChildrenByDefinition");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList<EventDefinitionCRFBean> al = new ArrayList<EventDefinitionCRFBean>();
		for (HashMap anAList : aList) {
			EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Find all EventDefinitionCRFBean for the site.
	 * 
	 * @param definitionId
	 *            int
	 * @param siteId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @return Collection
	 */
	public Collection findAllByDefinitionAndSiteIdAndParentStudyId(int definitionId, int siteId, int parentStudyId) {
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, definitionId);
		variables.put(index++, siteId);
		variables.put(index++, parentStudyId);
		variables.put(index++, definitionId);
		variables.put(index, siteId);

		String sql = digester.getQuery("findAllByDefinitionAndSiteIdAndParentStudyId");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (HashMap anAlist : aList) {
			EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that finds all event definition crfs by crfId.
	 * 
	 * @param crfId
	 *            int
	 * @return Collection
	 */
	public Collection findAllByCRF(int crfId) {
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index, crfId);

		String sql = digester.getQuery("findByCRFId");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (HashMap anAList : aList) {
			EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that finds all event definition crfs by incoming parameters.
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
	 * Method that finds event definition crf by oid.
	 * 
	 * @param oid
	 *            String
	 * @return EventDefinitionCRFBean
	 */
	public EventDefinitionCRFBean findByOid(String oid) {
		int index = 1;
		EventDefinitionCRFBean eb = null;
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(index, oid);

		String sql = digester.getQuery("findByOid");
		List<HashMap> aList = this.select(sql, variables);
		Iterator<HashMap> it = aList.iterator();

		if (it.hasNext()) {
			eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(it.next());
		}
		return eb;
	}

	/**
	 * Method that finds event definition crf by id.
	 * 
	 * @param id
	 *            String
	 * @return EntityBean
	 */
	public EntityBean findByPK(int id) {
		int index = 1;
		EventDefinitionCRFBean eb = new EventDefinitionCRFBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(index, id);

		String sql = digester.getQuery("findByPK");
		List<HashMap> aList = this.select(sql, variables);
		Iterator<HashMap> it = aList.iterator();
		if (it.hasNext()) {
			eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(it.next());
		}

		return eb;
	}

	/**
	 * Creates a new event definition crf.
	 *
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	@SuppressWarnings("deprecation")
	public EntityBean create(EntityBean eb) {
		int index = 1;
		EventDefinitionCRFBean sb = (EventDefinitionCRFBean) eb;
		HashMap nullVars = new HashMap();
		HashMap variables = new HashMap();
		int id = getNextPK();
		// INSERT INTO EVENT_DEFINITION_CRF
		// (EVENT_DEFINITION_CRF_ID,STUDY_EVENT_DEFINITION_ID,STUDY_ID,CRF_ID,
		// REQUIRED_CRF,
		// DOUBLE_ENTRY,REQUIRE_ALL_TEXT_FILLED,DECISION_CONDITIONS,
		// NULL_VALUES,DEFAULT_VERSION_ID,STATUS_ID,OWNER_ID,DATE_CREATED,ordinal,
		// ELECTRONIC_SIGNATURE,HIDE_CRF,SOURCE_DATA_VERIFICATION_ID,
		// SELECTED_VERSION_IDS, PARENT_ID)
		// VALUES (?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,?,?,?,?)
		variables.put(index++, id);
		variables.put(index++, sb.getStudyEventDefinitionId());
		variables.put(index++, sb.getStudyId());
		variables.put(index++, sb.getCrfId());
		variables.put(index++, sb.isRequiredCRF());
		variables.put(index++, sb.isDoubleEntry());
		variables.put(index++, sb.isRequireAllTextFilled());
		variables.put(index++, sb.isDecisionCondition());
		variables.put(index++, sb.getNullValues());
		variables.put(index++, sb.getDefaultVersionId());
		variables.put(index++, sb.getStatus().getId());
		variables.put(index++, sb.getOwnerId());
		variables.put(index++, sb.getOrdinal());
		variables.put(index++, sb.isElectronicSignature());
		variables.put(index++, sb.isHideCrf());
		variables.put(index++, sb.getSourceDataVerification().getCode());
		variables.put(index++, sb.getSelectedVersionIds());
		if (sb.getParentId() == 0) {
			nullVars.put(index, Types.INTEGER);
			variables.put(index++, null);
		} else {
			variables.put(index++, sb.getParentId());
		}
		variables.put(index++, sb.getEmailStep());
		variables.put(index++, sb.getEmailTo());
		variables.put(index++, sb.isEvaluatedCRF());
		variables.put(index++, sb.getTabbingMode());
		variables.put(index, sb.isAcceptNewCrfVersions());
		this.execute(digester.getQuery("create"), variables, nullVars);

		if (isQuerySuccessful()) {
			sb.setId(id);
		}

		return sb;
	}

	/**
	 * Updates a Study event.
	 * 
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	@SuppressWarnings("deprecation")
	public EntityBean update(EntityBean eb) {
		int index = 1;
		EventDefinitionCRFBean sb = (EventDefinitionCRFBean) eb;
		HashMap nullVars = new HashMap();
		HashMap variables = new HashMap();
		// UPDATE EVENT_DEFINITION_CRF SET
		// STUDY_EVENT_DEFINITION_ID=?,STUDY_ID=?,CRF_ID=?, REQUIRED_CRF=?,
		// DOUBLE_ENTRY=?,REQUIRE_ALL_TEXT_FILLED=?,DECISION_CONDITIONS=?,
		// NULL_VALUES=?,DEFAULT_VERSION_ID=?,STATUS_ID=?,DATE_UPDATED=?,UPDATE_ID=?,
		// ordinal=?,ELECTRONIC_SIGNATURE=? HIDE_CRF=?,
		// SOURCE_DATA_VERIFICATION_ID=?, Selected_version_ids=?, parent_id=?
		// WHERE EVENT_DEFINITION_CRF_ID=?
		variables.put(index++, sb.getStudyEventDefinitionId());
		variables.put(index++, sb.getStudyId());
		variables.put(index++, sb.getCrfId());
		variables.put(index++, sb.isRequiredCRF());
		variables.put(index++, sb.isDoubleEntry());
		variables.put(index++, sb.isRequireAllTextFilled());
		variables.put(index++, sb.isDecisionCondition());
		variables.put(index++, sb.getNullValues());
		variables.put(index++, sb.getDefaultVersionId());
		variables.put(index++, sb.getStatus().getId());
		variables.put(index++, new Timestamp(new Date().getTime())); // DATE_Updated
		variables.put(index++, sb.getUpdater().getId());
		variables.put(index++, sb.getOrdinal());
		variables.put(index++, sb.isElectronicSignature());
		variables.put(index++, sb.isHideCrf());
		variables.put(index++, sb.getSourceDataVerification().getCode());
		variables.put(index++, sb.getSelectedVersionIds());
		if (sb.getParentId() == 0) {
			nullVars.put(index, Types.INTEGER);
			variables.put(index++, null);
		} else {
			variables.put(index++, sb.getParentId());
		}
		variables.put(index++, sb.getEmailStep());
		variables.put(index++, sb.getEmailTo());
		variables.put(index++, sb.isEvaluatedCRF());
		variables.put(index++, sb.getTabbingMode());
		variables.put(index++, sb.isAcceptNewCrfVersions());
		variables.put(index, sb.getId());

		String sql = digester.getQuery("update");
		this.execute(sql, variables, nullVars);

		return sb;
	}

	/**
	 * Method that finds all event definition crfs by incoming parameters.
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
	 * Method that finds all event definition crfs by incoming parameters.
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
	 * Method that returns list of the event definition crfs by versionId.
	 *
	 * @param versionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findByDefaultVersion(int versionId) {
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index, versionId);

		String sql = digester.getQuery("findByDefaultVersion");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (HashMap anAList : aList) {
			EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that returns list of the event definition crfs by eventDefinitionId.
	 *
	 * @param eventDefinitionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllByEventDefinitionId(int eventDefinitionId) {
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index, eventDefinitionId);
		return executeFindAllQuery("findAllByEventDefinitionId", variables);
	}

	/**
	 * Method that returns list of the event definition crfs by eventDefinitionId and ordinal.
	 *
	 * @param eventDefinitionId
	 *            int
	 * @param ordinal
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllByEventDefinitionIdAndOrdinal(int eventDefinitionId, int ordinal) {
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, eventDefinitionId);
		variables.put(index, ordinal);
		return executeFindAllQuery("findAllByEventDefinitionIdAndOrdinal", variables);
	}

	/**
	 * Find all EventDefinitionCRFBean for the StudyBean.
	 * 
	 * @param study
	 *            StudyBean
	 * @param eventDefinitionId
	 *            int
	 * @return Collection
	 */
	public Collection findAllByEventDefinitionId(StudyBean study, int eventDefinitionId) {
		return study.isSite(study.getParentStudyId())
				? findAllByEventDefinitionIdAndSiteIdAndParentStudyId(eventDefinitionId, study.getId(),
						study.getParentStudyId())
				: findAllParentsByEventDefinitionId(eventDefinitionId);
	}

	/**
	 * Method that returns collection of the parent's event definition crfs by definitionId.
	 *
	 * @param definitionId
	 *            int
	 * @return ArrayList
	 */
	public Collection findAllParentsByEventDefinitionId(int definitionId) {
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index, definitionId);

		String sql = digester.getQuery("findAllParentsByEventDefinitionId");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (HashMap anAList : aList) {
			EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that returns collection of event definition crfs by definitionId, siteId and parentStudyId.
	 *
	 * @param definitionId
	 *            int
	 * @param siteId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @return ArrayList
	 */
	public Collection findAllByEventDefinitionIdAndSiteIdAndParentStudyId(int definitionId, int siteId,
			int parentStudyId) {
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, definitionId);
		variables.put(index++, siteId);
		variables.put(index++, parentStudyId);
		variables.put(index++, definitionId);
		variables.put(index, siteId);

		String sql = digester.getQuery("findAllByEventDefinitionIdAndSiteIdAndParentStudyId");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (HashMap anAList : aList) {
			EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that returns list of event definition crfs by eventDefinitionId.
	 *
	 * @param eventDefinitionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllActiveByEventDefinitionId(int eventDefinitionId) {
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index, eventDefinitionId);
		return executeFindAllQuery("findAllActiveByEventDefinitionId", variables);
	}

	/**
	 * Method that returns list of event definition crfs by studyId and crfId.
	 *
	 * @param studyId int
	 * @param crfId int
	 * @return ArrayList
	 */
	public ArrayList findAllActiveByStudyIdAndCRFId(int studyId, int crfId) {
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, studyId);
		variables.put(index, crfId);
		return executeFindAllQuery("findAllActiveByStudyIdAndCRFId", variables);
	}

	/**
	 * Find all active EventDefinitionCRFBean for the StudyBean and the study_event_definition_id.
	 * 
	 * @param study
	 *            StudyBean
	 * @param eventDefinitionId
	 *            int
	 * @return Collection
	 */
	public Collection findAllActiveByEventDefinitionId(StudyBean study, int eventDefinitionId) {
		if (study.isSite(study.getParentStudyId())) {
			return findAllActiveByEventDefinitionIdAndSiteIdAndParentStudyId(eventDefinitionId, study.getId(),
					study.getParentStudyId());
		} else {
			return findAllActiveParentsByEventDefinitionId(eventDefinitionId);
		}
	}

	/**
	 * Method that returns collection of active parent's event definition crfs by eventDefinitionId.
	 *
	 * @param definitionId
	 *            int
	 * @return ArrayList
	 */
	public Collection findAllActiveParentsByEventDefinitionId(int definitionId) {
		this.setTypesExpected();
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index, definitionId);

		String sql = digester.getQuery("findAllActiveParentsByEventDefinitionId");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (HashMap anAList : aList) {
			EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that returns collection of active event definition crfs by definitionId, siteId and parentStudyId.
	 *
	 * @param definitionId
	 *            int
	 * @param siteId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @return ArrayList
	 */
	public Collection findAllActiveByEventDefinitionIdAndSiteIdAndParentStudyId(int definitionId, int siteId,
			int parentStudyId) {
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, definitionId);
		variables.put(index++, siteId);
		variables.put(index++, parentStudyId);
		variables.put(index++, definitionId);
		variables.put(index, siteId);

		String sql = digester.getQuery("findAllActiveByEventDefinitionIdAndSiteIdAndParentStudyId");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (HashMap anAList : aList) {
			EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
			al.add(eb);
		}
		return al;
	}

	/**
	 * isRequiredInDefinition, looks at a specific EventCRF and determines if it's required or not.
	 * 
	 * @param crfVersionId
	 *            int
	 * @param studyEvent
	 *            StudyEventBean
	 * @return boolean to tell us if it's required or not.
	 */
	public boolean isRequiredInDefinition(int crfVersionId, StudyEventBean studyEvent) {
		StudyBean study = new StudyDAO(this.ds).findByStudySubjectId(studyEvent.getStudySubjectId());
		int studyEventId = studyEvent.getId();

		/*
		 * select distinct event_definition_crf.study_id, event_definition_crf.required_crf,
		 * event_definition_crf.parent_id from event_definition_crf, event_crf, crf_version, study_event where
		 * crf_version.crf_version_id = 29 and crf_version.crf_version_id = event_crf.crf_version_id and
		 * crf_version.crf_id = event_definition_crf.crf_id and event_definition_crf.study_event_definition_id =
		 * study_event.study_event_definition_id and study_event.study_event_id = 91
		 */

		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.BOOL);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.INT);
		HashMap variables = new HashMap();
		index = 1;
		variables.put(index++, crfVersionId);
		variables.put(index, studyEventId);

		String sql = digester.getQuery("isRequiredInDefinition");
		List<HashMap> aList = this.select(sql, variables);
		Boolean answer;
		Boolean siteR = false;
		Boolean studyR = false;
		Boolean isExisted = false;
		for (HashMap hm : aList) {
			Integer dbStudyId = (Integer) hm.get("study_id");
			Integer parentId = (Integer) hm.get("parent_id");
			if (dbStudyId == study.getId()) {
				if (parentId != null && parentId > 0) {
					siteR = (Boolean) hm.get("required_crf");
					isExisted = true;
				} else {
					studyR = (Boolean) hm.get("required_crf");
				}
			} else if (dbStudyId == study.getParentStudyId()) {
				studyR = (Boolean) hm.get("required_crf");
			}
		}
		if (study.isSite(study.getParentStudyId()) && isExisted) {
			answer = siteR;
		} else {
			answer = studyR;
		}

		logger.info("We are returning " + answer.toString() + " for crfVersionId " + crfVersionId
				+ " and studyEventId " + studyEventId);
		return answer;
	}

	/**
	 * Method returns event definition crf by study, studyEventId and crfVersionId.
	 * 
	 * @param study
	 *            StudyBean
	 * @param studyEventId
	 *            int
	 * @param crfVersionId
	 *            int
	 * @return EventDefinitionCRFBean
	 */
	public EventDefinitionCRFBean findByStudyEventIdAndCRFVersionId(StudyBean study, int studyEventId, int crfVersionId) {
		EventDefinitionCRFBean edc;
		if (study.isSite(study.getParentStudyId())) {
			edc = this.findByStudyEventIdAndCRFVersionIdAndSiteIdAndParentStudyId(studyEventId, crfVersionId,
					study.getId(), study.getParentStudyId());
		} else {
			edc = this.findForStudyByStudyEventIdAndCRFVersionId(studyEventId, crfVersionId);
		}
		return edc;
	}

	/**
	 * Find the EventDefinitionCRFBean of a study. So this EventDefinitionCRFBean has no parent.
	 * 
	 * @param studyEventId
	 *            The requested study event id.
	 * @param crfVersionId
	 *            The requested CRF version id.
	 * @return The event definition crf which defines the study event and crf version.
	 */
	public EventDefinitionCRFBean findForStudyByStudyEventIdAndCRFVersionId(int studyEventId, int crfVersionId) {
		int index = 1;
		final int lastTEIndex = 22;
		EventDefinitionCRFBean answer = new EventDefinitionCRFBean();

		this.setTypesExpected();
		// crfName
		this.setTypeExpected(lastTEIndex, TypeNames.STRING);

		HashMap variables = new HashMap();
		variables.put(index++, studyEventId);
		variables.put(index, crfVersionId);

		String sql = digester.getQuery("findForStudyByStudyEventIdAndCRFVersionId");
		List<HashMap> aList = this.select(sql, variables);
		for (HashMap anAList : aList) {
			answer = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
		}

		return answer;
	}

	/***
	 * Finds the EventDefinitionCRFBean of a site, returns null if site doesn't override Event Definition CRF settings.
	 * 
	 * @param eventCrfId
	 *            EventCrfId
	 * @return EventDefinitionCRFBean
	 */
	public EventDefinitionCRFBean findForSiteByEventCrfId(int eventCrfId) {
		EventDefinitionCRFBean edcb = null;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, eventCrfId);
		String sql = digester.getQuery("findForSiteByEventCrfId");
		List<HashMap> aList = this.select(sql, variables);
		for (HashMap anAList : aList) {
			edcb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
		}
		return edcb;
	}

	/**
	 * Method returns event definition crf by studyEventId, crfVersionId, siteId and parentStudyId.
	 *
	 * @param studyEventId
	 *            int
	 * @param crfVersionId
	 *            int
	 * @param siteId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @return EventDefinitionCRFBean
	 */
	public EventDefinitionCRFBean findByStudyEventIdAndCRFVersionIdAndSiteIdAndParentStudyId(int studyEventId,
			int crfVersionId, int siteId, int parentStudyId) {
		int index = 1;
		final int lastTEIndex = 22;
		EventDefinitionCRFBean answer = new EventDefinitionCRFBean();

		this.setTypesExpected();
		// crfName
		this.setTypeExpected(lastTEIndex, TypeNames.STRING);

		HashMap variables = new HashMap();
		variables.put(index++, studyEventId);
		variables.put(index++, crfVersionId);
		variables.put(index++, siteId);
		variables.put(index++, parentStudyId);
		variables.put(index, siteId);

		String sql = digester.getQuery("findByStudyEventIdAndCRFVersionIdAndSiteIdAndParentStudyId");
		List<HashMap> aList = this.select(sql, variables);
		for (HashMap anAList : aList) {
			answer = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
		}

		return answer;
	}

	/**
	 * @param studyEventDefinitionId
	 *            The study event definition of the desired event definition crf.
	 * @param crfId
	 *            The CRF of the desired event definition crf.
	 * @return EventDefinitionCRFBean The event definition crf for the specified study event definition and CRF.
	 */
	public EventDefinitionCRFBean findByStudyEventDefinitionIdAndCRFId(int studyEventDefinitionId, int crfId) {
		EventDefinitionCRFBean answer = new EventDefinitionCRFBean();
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, studyEventDefinitionId);
		variables.put(index, crfId);

		String sql = digester.getQuery("findByStudyEventDefinitionIdAndCRFId");
		List<HashMap> aList = this.select(sql, variables);
		for (HashMap anAList : aList) {
			answer = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
		}

		return answer;
	}

	/**
	 * Method returns event definition crf by studyEventDefinitionId, crfId and studyId.
	 *
	 * @param studyEventDefinitionId
	 *            int
	 * @param crfId
	 *            int
	 * @param studyId
	 *            int
	 * @return EventDefinitionCRFBean
	 */
	public EventDefinitionCRFBean findByStudyEventDefinitionIdAndCRFIdAndStudyId(int studyEventDefinitionId, int crfId,
			int studyId) {
		int index = 1;
		EventDefinitionCRFBean answer = new EventDefinitionCRFBean();

		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, studyEventDefinitionId);
		variables.put(index++, crfId);
		variables.put(index, studyId);

		String sql = digester.getQuery("findByStudyEventDefinitionIdAndCRFIdAndStudyId");
		List<HashMap> aList = this.select(sql, variables);
		for (HashMap anAList : aList) {
			answer = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
		}

		return answer;
	}

	/**
	 * Find EventDefinitionCRFBean for the StudyBean.
	 * 
	 * @param study
	 *            StudyBean
	 * @param studyEventDefinitionId
	 *            int
	 * @param crfId
	 *            int
	 * @return EventDefinitionCRFBean
	 */
	public EventDefinitionCRFBean findByStudyEventDefinitionIdAndCRFId(StudyBean study, int studyEventDefinitionId,
			int crfId) {
		return study.isSite(study.getParentStudyId())
				? findByStudyEventDefinitionIdAndCRFIdAndSiteIdAndParentStudyId(studyEventDefinitionId, crfId,
						study.getId(), study.getParentStudyId())
				: findForStudyByStudyEventDefinitionIdAndCRFId(studyEventDefinitionId, crfId);
	}

	/**
	 * Find EventDefinitionCRFBean for a study. So this EventDefinitionCRFBean has no parent.
	 * 
	 * @param studyEventDefinitionId
	 *            int
	 * @param crfId
	 *            int
	 * @return EventDefinitionCRFBean
	 */
	public EventDefinitionCRFBean findForStudyByStudyEventDefinitionIdAndCRFId(int studyEventDefinitionId, int crfId) {
		EventDefinitionCRFBean answer = new EventDefinitionCRFBean();
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, studyEventDefinitionId);
		variables.put(index, crfId);

		String sql = digester.getQuery("findForStudyByStudyEventDefinitionIdAndCRFId");
		List<HashMap> aList = this.select(sql, variables);
		for (HashMap anAList : aList) {
			answer = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
		}

		return answer;
	}

	/**
	 * Method returns event definition crf by studyEventDefinitionId, crfId, siteId and parentStudyId.
	 *
	 * @param studyEventDefinitionId
	 *            int
	 * @param crfId
	 *            int
	 * @param siteId
	 *            int
	 * @param parentStudyId
	 *            itn
	 * @return EventDefinitionCRFBean
	 */
	public EventDefinitionCRFBean findByStudyEventDefinitionIdAndCRFIdAndSiteIdAndParentStudyId(
			int studyEventDefinitionId, int crfId, int siteId, int parentStudyId) {
		int index = 1;
		EventDefinitionCRFBean answer = new EventDefinitionCRFBean();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, studyEventDefinitionId);
		variables.put(index++, crfId);
		variables.put(index++, siteId);
		variables.put(index++, parentStudyId);
		variables.put(index, siteId);

		String sql = digester.getQuery("findByStudyEventDefinitionIdAndCRFIdAndSiteIdAndParentStudyId");
		List<HashMap> aList = this.select(sql, variables);
		for (HashMap anAList : aList) {
			answer = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAList);
		}

		return answer;
	}

	/**
	 * Method that finds EventDefinitionCRFBean by eventCrfId and studyId.
	 * 
	 * @param eventCrfId
	 *            int
	 * @param studyId
	 *            int
	 * @return EventDefinitionCRFBean
	 */
	public EventDefinitionCRFBean findByEventCrfIdAndStudyId(int eventCrfId, int studyId) {
		EventDefinitionCRFBean eb = new EventDefinitionCRFBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, eventCrfId);
		variables.put(2, studyId);

		String sql = digester.getQuery("findByEventCrfIdAndStudyId");
		List<HashMap> aList = this.select(sql, variables);
		Iterator<HashMap> it = aList.iterator();

		if (it.hasNext()) {
			eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(it.next());
		}

		return eb;
	}

	/**
	 * Method updates the source data verification code for event definition crfs.
	 *
	 * @param crfVersionId
	 *            int
	 * @param sourceDataVerification
	 *            SourceDataVerification
	 * @return boolean
	 */
	public boolean updateEDCThatHasItemsToSDV(int crfVersionId, SourceDataVerification sourceDataVerification) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		int ind = 1;
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(ind++, sourceDataVerification.getCode());
		variables.put(ind, crfVersionId);

		execute(digester.getQuery("updateEDCThatHasItemsToSDV"), variables);

		return isQuerySuccessful();
	}

	/**
	 * Discovers if a study/site level Event Definition CRF record has at least one available CRF version for data
	 * entry.
	 *
	 * @param eventDefCRFBean
	 *            EventDefinitionCRFBean
	 * @return boolean
	 */
	public boolean doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(EventDefinitionCRFBean eventDefCRFBean) {

		unsetTypeExpected();
		setTypeExpected(1, TypeNames.STRING);

		Map<Integer, Object> variables = new HashMap<Integer, Object>();
		int index = 1;
		variables.put(index++, eventDefCRFBean.getStudyEventDefinitionId());
		variables.put(index++, eventDefCRFBean.getStudyId());
		variables.put(index++, eventDefCRFBean.getCrfId());
		variables.put(index++, eventDefCRFBean.getStudyEventDefinitionId());
		variables.put(index++, eventDefCRFBean.getStudyId());
		variables.put(index++, eventDefCRFBean.getCrfId());
		variables.put(index++, eventDefCRFBean.getCrfId());
		variables.put(index++, eventDefCRFBean.getCrfId());
		variables.put(index++, eventDefCRFBean.getStudyEventDefinitionId());
		variables.put(index++, eventDefCRFBean.getStudyId());
		variables.put(index, eventDefCRFBean.getCrfId());

		String sql = digester.getQuery("doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry");
		List<Map<String, Object>> resultRows = (List<Map<String, Object>>) this.select(sql, variables);
		Iterator<Map<String, Object>> iterator = resultRows.listIterator();
		return iterator.hasNext() && ((String) iterator.next().get("result")).equalsIgnoreCase("Y");
	}

	/**
	 * Find all Event Definition CRFs in the EventDefinition on the Study.
	 * @param definitionId StudyEventDefinition Id
	 * @param siteId StudyBean Id
	 * @return Collection of EventDefinitionCRFBeans
	 */
	public Collection findAllActiveByDefinitionAndSiteId(int definitionId, int siteId) {
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, definitionId);
		variables.put(index, siteId);

		String sql = digester.getQuery("findAllActiveByDefinitionAndSiteId");
		List<HashMap> aList = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (HashMap anAlist : aList) {
			EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap(anAlist);
			al.add(eb);
		}
		return al;
	}
}
