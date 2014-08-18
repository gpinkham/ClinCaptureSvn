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

import java.sql.Types;
import java.util.ArrayList;
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
 * <code>EventDefinitionCRFDAO</code> class is a member of DAO layer, extends <code>AuditableEntityDAO</code> class.
 * <p/>
 * This class implements all the required data access logic for bean class <code>EventDefinitionCRFBean</code>.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EventDefinitionCRFDAO extends AuditableEntityDAO {

	private void setQueryNames() {

		getCurrentPKName = "getCurrentPK";
		getNextPKName = "getNextPK";
		findAllByStudyName = "findAllByStudy";
	}

	/**
	 * <code>EventDefinitionCRFDAO</code> class constructor.
	 *
	 * @param dataSource instance of <code>javax.sql.DataSource</code> class, which represents physical data source.
	 */
	public EventDefinitionCRFDAO(DataSource dataSource) {

		super(dataSource);
		setQueryNames();
	}

	/**
	 * <code>EventDefinitionCRFDAO</code> class constructor.
	 *
	 * @param dataSource instance of <code>javax.sql.DataSource</code> class, which represents physical data source.
	 * @param digester   instance of <code>DAODigester</code> class, which contains all of the named SQL queries,
	 *                   which are specified for DAO class <code>EventDefinitionCRFDAO</code>.
	 */
	public EventDefinitionCRFDAO(DataSource dataSource, DAODigester digester) {

		super(dataSource);
		this.digester = digester;
		setQueryNames();
	}

	@Override
	protected void setDigesterName() {

		digesterName = SQLFactory.getInstance().DAO_EVENTDEFINITIONCRF;
	}

	@Override
	public void setTypesExpected() {

		this.unsetTypeExpected();

		int index = 1;
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
		this.setTypeExpected(index++, TypeNames.DATE);
		this.setTypeExpected(index++, TypeNames.DATE);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.BOOL);

		this.setTypeExpected(index++, TypeNames.BOOL);
		this.setTypeExpected(index++, TypeNames.INT); // source_data_verification_id
		this.setTypeExpected(index++, TypeNames.STRING); // selected_version_ids
		this.setTypeExpected(index++, TypeNames.INT); // parent_id
		this.setTypeExpected(index++, TypeNames.STRING); // email_step
		this.setTypeExpected(index++, TypeNames.STRING); // email_to
		this.setTypeExpected(index, TypeNames.BOOL); //evaluated_crf
	}

	public EventDefinitionCRFBean getEntityFromHashMap(HashMap record) {

		EventDefinitionCRFBean eventDefinitionCRF = new EventDefinitionCRFBean();
		super.setEntityAuditInformation(eventDefinitionCRF, record);

		eventDefinitionCRF.setId((Integer) record.get("event_definition_crf_id"));
		eventDefinitionCRF.setStudyEventDefinitionId((Integer) record.get("study_event_definition_id"));
		eventDefinitionCRF.setStudyId((Integer) record.get("study_id"));
		eventDefinitionCRF.setCrfId((Integer) record.get("crf_id"));
		eventDefinitionCRF.setRequiredCRF((Boolean) record.get("required_crf"));
		eventDefinitionCRF.setDoubleEntry((Boolean) record.get("double_entry"));
		eventDefinitionCRF.setRequireAllTextFilled((Boolean) record.get("require_all_text_filled"));
		eventDefinitionCRF.setDecisionCondition((Boolean) record.get("decision_conditions"));
		eventDefinitionCRF.setNullValues((String) record.get("null_values"));
		eventDefinitionCRF.setDefaultVersionId((Integer) record.get("default_version_id"));
		eventDefinitionCRF.setOrdinal((Integer) record.get("ordinal"));
		eventDefinitionCRF.setElectronicSignature((Boolean) record.get("electronic_signature"));
		String crfName = (String) record.get("crf_name");
		eventDefinitionCRF.setCrfName(crfName != null ? crfName : eventDefinitionCRF.getCrfName());
		eventDefinitionCRF.setHideCrf(((Boolean) record.get("hide_crf")));
		int sdvId = (Integer) record.get("source_data_verification_code");
		eventDefinitionCRF.setSourceDataVerification(sdvId > 0 ? SourceDataVerification.getByCode(sdvId)
				: SourceDataVerification.NOTREQUIRED);
		String selectedVersionIds = (String) record.get("selected_version_ids");
		eventDefinitionCRF.setSelectedVersionIds(selectedVersionIds != null ? selectedVersionIds : "");
		int parentId = (Integer) record.get("parent_id");
		eventDefinitionCRF.setParentId(parentId > 0 ? parentId : 0);
		String emailTo = (String) record.get("email_to");
		eventDefinitionCRF.setEmailTo(emailTo != null ? emailTo : "");
		String emailStep = (String) record.get("email_step");
		eventDefinitionCRF.setEmailStep(emailStep != null ? emailStep : "");
		eventDefinitionCRF.setEvaluatedCRF(((Boolean) record.get("evaluated_crf")));
		return eventDefinitionCRF;
	}

	public List<EventDefinitionCRFBean> findAll() {

		this.setTypesExpected();

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findAll"));

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans;
	}

	/**
	 * Returns the list of all event definition CRF beans, which are bound to the specified study event definition id;
	 * ordered by the <code>ordinal</code> property of <code>EventDefinitionCRFBean</code> class,
	 * in <code>ASC</code> mode.
	 *
	 * @param definitionId study event definition id, to search on.
	 * @return the list of instances of <code>EventDefinitionCRFBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	public List<EventDefinitionCRFBean> findAllByDefinition(int definitionId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		queryParameters.put(1, definitionId);

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findAllByDefinition"),
				queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans;
	}

	/**
	 * Returns the list of event definition CRF beans, which are assigned to a specific study event definition
	 * in a specific study or site.
	 * <p/>
	 * If the <code>study</code> parameter represents a study, then this method returns the list of a study level
	 * event definition CRF beans only (records for sites are excluded), which are assigned to a specific
	 * study event definition.
	 * If the <code>study</code> parameter represents a site, then this method returns the list of event definition
	 * CRF beans, which are assigned to a specific study event definition in that site. That is, if a site has its own
	 * event definition CRF record in the data base for a specific CRF form, assigned to a specific study event
	 * definition, then this event definition CRF record of a site level will be added to the result list instead of
	 * a study level record for the same CRF form; otherwise - to the result list will be added an event definition CRF
	 * record of a study level.
	 * <p/>
	 * The result list is ordered by the <code>ordinal</code> property of the <code>EventDefinitionCRFBean</code> class,
	 * in <code>ASC</code> mode.
	 *
	 * @param study        an instance of the <code>StudyBean</code> class, which represents the study, to search in.
	 * @param definitionId study event definition id, to search on.
	 * @return the list of instances of <code>EventDefinitionCRFBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	public List<EventDefinitionCRFBean> findAllByDefinition(StudyBean study, int definitionId) {

		return study.isSite(study.getParentStudyId()) ? findAllByDefinitionAndSiteIdAndParentStudyId(definitionId,
				study.getId(), study.getParentStudyId()) : findAllParentsByDefinition(definitionId);
	}

	/**
	 * Returns the list of ids of event definition CRF beans with status available, assigned to a specific study or site,
	 * and for which data entry and source data verification are required.
	 *
	 * @param studyBean an instance of the <code>StudyBean</code> class, which represents the study/site, to search in.
	 * @return the list of ids of event definition CRF records, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	public List<Integer> getRequiredEventCRFDefIdsThatShouldBeSDVd(StudyBean studyBean) {

		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);
		setTypeExpected(2, TypeNames.INT);

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index++, studyBean.getId());
		queryParameters.put(index, studyBean.getParentStudyId());

		List<HashMap<String, Object>> recordsFromDB = this
				.select(digester.getQuery("requiredEventCRFDefIdsThatShouldBeSDVd"),
						queryParameters);

		List<Integer> resultIdList = new ArrayList<Integer>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultIdList.add((Integer) record.get("event_definition_crf_id"));
		}

		return resultIdList;
	}

	/**
	 * Returns the list of a study level event definition CRF beans only (records for sites are excluded),
	 * which are assigned to a specific study event definition;
	 * ordered by the <code>ordinal</code> property of the <code>EventDefinitionCRFBean</code> class,
	 * in <code>ASC</code> mode.
	 *
	 * @param definitionId study event definition id, to search on.
	 * @return the list of instances of <code>EventDefinitionCRFBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	public List<EventDefinitionCRFBean> findAllParentsByDefinition(int definitionId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		queryParameters.put(1, definitionId);

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findAllParentsByDefinition"),
				queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans;
	}

	/**
	 * Returns the list of a site level event definition CRF beans only (records for study are excluded),
	 * which are assigned to a specific study event definition;
	 * ordered by the <code>ordinal</code> property of the <code>EventDefinitionCRFBean</code> class,
	 * in <code>ASC</code> mode.
	 *
	 * @param definitionId study event definition id, to search on.
	 * @return the list of instances of <code>EventDefinitionCRFBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	public List<EventDefinitionCRFBean> findAllChildrenByDefinition(int definitionId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		queryParameters.put(1, definitionId);

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findAllChildrenByDefinition"),
				queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans;
	}

	/**
	 * Returns the list of event definition CRF beans, which are assigned to a specific study event definition
	 * for a specific site. That is, if a site has its own event definition CRF record in the data base for
	 * a specific CRF form, assigned to a specific study event definition, then this event definition CRF record
	 * of a site level will be added to the result list instead of a study level record for the same CRF form;
	 * otherwise - to the result list will be added an event definition CRF record of a study level.
	 * <p/>
	 * The result list is ordered by the <code>ordinal</code> property of the <code>EventDefinitionCRFBean</code> class,
	 * in <code>ASC</code> mode.
	 *
	 * @param definitionId  study event definition id, to search on.
	 * @param siteId        site id, to search on.
	 * @param parentStudyId study id, to search on.
	 * @return the list of instances of <code>EventDefinitionCRFBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	public List<EventDefinitionCRFBean> findAllByDefinitionAndSiteIdAndParentStudyId(int definitionId, int siteId,
			int parentStudyId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index++, definitionId);
		queryParameters.put(index++, siteId);
		queryParameters.put(index++, parentStudyId);
		queryParameters.put(index++, definitionId);
		queryParameters.put(index, siteId);

		List<HashMap<String, Object>> recordsFromDB =
				this.select(digester.getQuery("findAllByDefinitionAndSiteIdAndParentStudyId"), queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans;
	}

	/**
	 * Returns the list of event definition CRF beans, which have a specific CRF form assigned.
	 *
	 * @param crfId CRF form id, to search on.
	 * @return the list of instances of <code>EventDefinitionCRFBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	public List<EventDefinitionCRFBean> findAllByCRF(int crfId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		queryParameters.put(1, crfId);

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findByCRFId"), queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans;
	}

	public List<EventDefinitionCRFBean> findAll(String strOrderByColumn, boolean blnAscendingSort,
			String strSearchPhrase) {

		return new ArrayList<EventDefinitionCRFBean>();
	}

	/**
	 * Returns the event definition CRF bean, which matches a specific OID.
	 *
	 * @param oid the OID value, to search on.
	 * @return the instance of the <code>EventDefinitionCRFBean</code> class, which matches given OID;
	 * if such an event definition CRF record was not found, returns <code>null</code>.
	 */
	public EventDefinitionCRFBean findByOid(String oid) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		queryParameters.put(1, oid);

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findByOid"), queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans.isEmpty() ? null : resultSetOfBeans.get(0);
	}

	public EntityBean findByPK(int id) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		queryParameters.put(1, id);

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findByPK"), queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans.isEmpty() ? new EventDefinitionCRFBean() : resultSetOfBeans.get(0);
	}

	@SuppressWarnings("deprecation")
	public EntityBean create(EntityBean entityBean) {

		EventDefinitionCRFBean eventDefCRFBean = (EventDefinitionCRFBean) entityBean;
		HashMap nullVars = new HashMap();
		HashMap queryParameters = new HashMap();
		int id = getNextPK();

		int index = 1;
		queryParameters.put(index++, id);
		queryParameters.put(index++, eventDefCRFBean.getStudyEventDefinitionId());
		queryParameters.put(index++, eventDefCRFBean.getStudyId());
		queryParameters.put(index++, eventDefCRFBean.getCrfId());
		queryParameters.put(index++, eventDefCRFBean.isRequiredCRF());
		queryParameters.put(index++, eventDefCRFBean.isDoubleEntry());
		queryParameters.put(index++, eventDefCRFBean.isRequireAllTextFilled());
		queryParameters.put(index++, eventDefCRFBean.isDecisionCondition());
		queryParameters.put(index++, eventDefCRFBean.getNullValues());
		queryParameters.put(index++, eventDefCRFBean.getDefaultVersionId());
		queryParameters.put(index++, eventDefCRFBean.getStatus().getId());
		queryParameters.put(index++, eventDefCRFBean.getOwnerId());
		queryParameters.put(index++, eventDefCRFBean.getOrdinal());
		queryParameters.put(index++, eventDefCRFBean.isElectronicSignature());
		queryParameters.put(index++, eventDefCRFBean.isHideCrf());
		queryParameters.put(index++, eventDefCRFBean.getSourceDataVerification().getCode());
		queryParameters.put(index++, eventDefCRFBean.getSelectedVersionIds());

		if (eventDefCRFBean.getParentId() == 0) {
			nullVars.put(index, Types.INTEGER);
			queryParameters.put(index, null);
		} else {
			queryParameters.put(index, eventDefCRFBean.getParentId());
		}
		index++;

		queryParameters.put(index++, eventDefCRFBean.getEmailStep());
		queryParameters.put(index++, eventDefCRFBean.getEmailTo());
		queryParameters.put(index, eventDefCRFBean.isEvaluatedCRF());

		this.execute(digester.getQuery("create"), queryParameters, nullVars);

		if (isQuerySuccessful()) {
			eventDefCRFBean.setId(id);
		}

		return eventDefCRFBean;
	}

	@SuppressWarnings("deprecation")
	public EntityBean update(EntityBean entityBean) {

		EventDefinitionCRFBean eventDefCRFBean = (EventDefinitionCRFBean) entityBean;
		HashMap nullVars = new HashMap();
		HashMap queryParameters = new HashMap();

		int index = 1;
		queryParameters.put(index++, eventDefCRFBean.getStudyEventDefinitionId());
		queryParameters.put(index++, eventDefCRFBean.getStudyId());
		queryParameters.put(index++, eventDefCRFBean.getCrfId());
		queryParameters.put(index++, eventDefCRFBean.isRequiredCRF());
		queryParameters.put(index++, eventDefCRFBean.isDoubleEntry());
		queryParameters.put(index++, eventDefCRFBean.isRequireAllTextFilled());
		queryParameters.put(index++, eventDefCRFBean.isDecisionCondition());
		queryParameters.put(index++, eventDefCRFBean.getNullValues());
		queryParameters.put(index++, eventDefCRFBean.getDefaultVersionId());
		queryParameters.put(index++, eventDefCRFBean.getStatus().getId());
		queryParameters.put(index++, new java.util.Date());    // DATE_Updated
		queryParameters.put(index++, eventDefCRFBean.getUpdater().getId());
		queryParameters.put(index++, eventDefCRFBean.getOrdinal());
		queryParameters.put(index++, eventDefCRFBean.isElectronicSignature());
		queryParameters.put(index++, eventDefCRFBean.isHideCrf());
		queryParameters.put(index++, eventDefCRFBean.getSourceDataVerification().getCode());
		queryParameters.put(index++, eventDefCRFBean.getSelectedVersionIds());

		if (eventDefCRFBean.getParentId() == 0) {
			nullVars.put(index, Types.INTEGER);
			queryParameters.put(index, null);
		} else {
			queryParameters.put(index, eventDefCRFBean.getParentId());
		}
		index++;

		queryParameters.put(index++, eventDefCRFBean.getEmailStep());
		queryParameters.put(index++, eventDefCRFBean.getEmailTo());
		queryParameters.put(index++, eventDefCRFBean.isEvaluatedCRF());
		queryParameters.put(index, eventDefCRFBean.getId());

		this.execute(digester.getQuery("update"), queryParameters, nullVars);

		return eventDefCRFBean;
	}

	public List<EventDefinitionCRFBean> findAllByPermission(Object objCurrentUser, int intActionType,
			String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {

		return new ArrayList<EventDefinitionCRFBean>();
	}

	public List<EventDefinitionCRFBean> findAllByPermission(Object objCurrentUser, int intActionType) {

		return new ArrayList<EventDefinitionCRFBean>();
	}

	/**
	 * Returns the list of all of the event definition CRF beans,
	 * which have a specific CRF form version assigned as the default.
	 *
	 * @param versionId CRF form version id, to search on.
	 * @return the list of instances of <code>EventDefinitionCRFBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	public List<EventDefinitionCRFBean> findByDefaultVersion(int versionId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		queryParameters.put(1, versionId);

		List<HashMap<String, Object>> recordsFromDB = this.select(digester.getQuery("findByDefaultVersion"),
				queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans;
	}

	/**
	 * Returns the list of the event definition CRF beans (both of study level and site level),
	 * which are assigned to a specific study event definition and have a specific ordinal number
	 * inside of that definition.
	 *
	 * @param eventDefinitionId the study event definition id, to search on.
	 * @param ordinal           the ordinal number, to search on.
	 * @return the list of instances of <code>EventDefinitionCRFBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	public List<EventDefinitionCRFBean> findAllByEventDefinitionIdAndOrdinal(int eventDefinitionId, int ordinal) {

		HashMap queryParameters = new HashMap();
		int index = 1;
		queryParameters.put(index++, eventDefinitionId);
		queryParameters.put(index, ordinal);

		return (List<EventDefinitionCRFBean>) executeFindAllQuery("findAllByEventDefinitionIdAndOrdinal",
				queryParameters);
	}

	/**
	 * Returns the list of event definition CRF beans with status available only,
	 * which are assigned to a specific study event definition;
	 * ordered by the <code>ordinal</code> property of <code>EventDefinitionCRFBean</code> class,
	 * in <code>ASC</code> mode.
	 *
	 * @param eventDefinitionId the study event definition id, to search on.
	 * @return the list of instances of <code>EventDefinitionCRFBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	public List<EventDefinitionCRFBean> findAllActiveByEventDefinitionId(int eventDefinitionId) {

		HashMap queryParameters = new HashMap();
		queryParameters.put(1, eventDefinitionId);

		return (List<EventDefinitionCRFBean>) executeFindAllQuery("findAllActiveByEventDefinitionId", queryParameters);
	}

	/**
	 * Returns the list of event definition CRF beans with status available only, which are assigned to a specific
	 * study event definition in a specific study or site.
	 * <p/>
	 * If the <code>study</code> parameter represents a study, then this method returns the list of a study level
	 * event definition CRF beans only (records for sites are excluded), which are assigned to a specific
	 * study event definition.
	 * If the <code>study</code> parameter represents a site, then this method returns the list of event definition
	 * CRF beans, which are assigned to a specific study event definition in that site. That is, if a site has its own
	 * event definition CRF record in the data base for a specific CRF form, assigned to a specific study event
	 * definition, then this event definition CRF record of a site level will be added to the result list instead of
	 * a study level record for the same CRF form; otherwise - to the result list will be added an event definition CRF
	 * record of a study level.
	 * <p/>
	 * The result list is ordered by the <code>ordinal</code> property of the <code>EventDefinitionCRFBean</code> class,
	 * in <code>ASC</code> mode.
	 *
	 * @param study             an instance of the <code>StudyBean</code> class, which represents the study, to search in.
	 * @param eventDefinitionId the study event definition id, to search on.
	 * @return the list of instances of <code>EventDefinitionCRFBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	public List<EventDefinitionCRFBean> findAllActiveByEventDefinitionId(StudyBean study, int eventDefinitionId) {

		if (study.isSite(study.getParentStudyId())) {
			return findAllActiveByEventDefinitionIdAndSiteIdAndParentStudyId(eventDefinitionId, study.getId(),
					study.getParentStudyId());
		} else {
			return findAllActiveParentsByEventDefinitionId(eventDefinitionId);
		}
	}

	/**
	 * Returns the list of a study level event definition CRF beans (records for sites are excluded),
	 * with status available only, which are assigned to a specific study event definition;
	 * ordered by the <code>ordinal</code> property of the <code>EventDefinitionCRFBean</code> class,
	 * in <code>ASC</code> mode.
	 *
	 * @param definitionId the study event definition id, to search on.
	 * @return the list of instances of <code>EventDefinitionCRFBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	public List<EventDefinitionCRFBean> findAllActiveParentsByEventDefinitionId(int definitionId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		queryParameters.put(1, definitionId);

		List<HashMap<String, Object>> recordsFromDB =
				this.select(digester.getQuery("findAllActiveParentsByEventDefinitionId"), queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans;
	}

	/**
	 * Returns the list of event definition CRF beans with status available only,
	 * which are assigned to a specific study event definition for a specific site.
	 * That is, if a site has its own event definition CRF record in the data base for
	 * a specific CRF form, assigned to a specific study event definition, then this event definition CRF record
	 * of a site level will be added to the result list instead of a study level record for the same CRF form;
	 * otherwise - to the result list will be added an event definition CRF record of a study level.
	 * <p/>
	 * The result list is ordered by the <code>ordinal</code> property of the <code>EventDefinitionCRFBean</code> class,
	 * in <code>ASC</code> mode.
	 *
	 * @param definitionId  the study event definition id, to search on.
	 * @param siteId        the site id, to search on.
	 * @param parentStudyId the study id, to search on.
	 * @return the list of instances of <code>EventDefinitionCRFBean</code> class, which match the SQL query;
	 * if no records, matching the SQL query, were found, returns empty list.
	 */
	private List<EventDefinitionCRFBean> findAllActiveByEventDefinitionIdAndSiteIdAndParentStudyId(int definitionId,
			int siteId, int parentStudyId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index++, definitionId);
		queryParameters.put(index++, siteId);
		queryParameters.put(index++, parentStudyId);
		queryParameters.put(index++, definitionId);
		queryParameters.put(index, siteId);

		List<HashMap<String, Object>> recordsFromDB =
				this.select(digester.getQuery("findAllActiveByEventDefinitionIdAndSiteIdAndParentStudyId"),
						queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans;
	}

	/**
	 * Discovers if a specific CRF, which is determined by given CRF version id, is marked as data entry required
	 * inside of a specific event definition CRF, which is determined by given study event bean.
	 *
	 * @param crfVersionId the CRF version id, to search on.
	 * @param studyEvent   an instance of the <code>StudyEventBean</code> class, which represents the study event,
	 *                     to search on.
	 * @return <code>true</code> if the discovered CRF is marked as data entry required inside of
	 * a specific event definition CRF, which is discovered by the given study event bean.; <code>false</code> otherwise.
	 */
	public boolean isRequiredInDefinition(int crfVersionId, StudyEventBean studyEvent) {

		StudyBean study = new StudyDAO(this.ds).findByStudySubjectId(studyEvent.getStudySubjectId());
		int studyEventId = studyEvent.getId();

		this.unsetTypeExpected();

		int index = 1;
		this.setTypeExpected(index++, TypeNames.BOOL);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.INT);

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		index = 1;
		queryParameters.put(index++, crfVersionId);
		queryParameters.put(index, studyEventId);

		String sql = digester.getQuery("isRequiredInDefinition");
		List<Map<String, Object>> recordsFromDB = this.select(sql, queryParameters);

		Iterator<Map<String, Object>> iterator = recordsFromDB.iterator();
		Boolean answer;
		Boolean siteR = false;
		Boolean studyR = false;
		Boolean isExisted = false;

		while (iterator.hasNext()) {

			Map<String, Object> record = iterator.next();
			Integer dbStudyId = (Integer) record.get("study_id");
			Integer parentId = (Integer) record.get("parent_id");
			if (dbStudyId == study.getId()) {
				if (parentId != null && parentId > 0) {
					siteR = (Boolean) record.get("required_crf");
					isExisted = true;
				} else {
					studyR = (Boolean) record.get("required_crf");
				}
			} else if (dbStudyId == study.getParentStudyId()) {
				studyR = (Boolean) record.get("required_crf");
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
	 * Searches for the event definition CRF bean by a specific study event and a specific CRF version.
	 * <p/>
	 * If the <code>study</code> parameter represents a study, then this method searches for
	 * the event definition CRF bean of a study level, otherwise - searches for the event definition CRF bean of a site
	 * level.
	 *
	 * @param study        an instance of the <code>StudyBean</code> class, which represents the study, to search in.
	 * @param studyEventId the study event id, to search on.
	 * @param crfVersionId the CRF version id, to search on.
	 * @return the instance of the <code>EventDefinitionCRFBean</code> class, that matches the SQL query;
	 * if such an event definition CRF record was not found, creates and returns an empty
	 * instance of the <code>EventDefinitionCRFBean</code> class.
	 */
	public EventDefinitionCRFBean findByStudyEventIdAndCRFVersionId(StudyBean study, int studyEventId,
			int crfVersionId) {

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
	 * Searches for the event definition CRF bean of a study level (records for sites are excluded),
	 * by a specific study event and a specific CRF version.
	 *
	 * @param studyEventId the study event id, to search on.
	 * @param crfVersionId the CRF version id, to search on.
	 * @return the instance of the <code>EventDefinitionCRFBean</code> class, that matches the SQL query;
	 * if such an event definition CRF record was not found, creates and returns an empty
	 * instance of the <code>EventDefinitionCRFBean</code> class.
	 */
	public EventDefinitionCRFBean findForStudyByStudyEventIdAndCRFVersionId(int studyEventId, int crfVersionId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index++, studyEventId);
		queryParameters.put(index, crfVersionId);

		List<HashMap<String, Object>> recordsFromDB =
				this.select(digester.getQuery("findForStudyByStudyEventIdAndCRFVersionId"), queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans.isEmpty() ? new EventDefinitionCRFBean() : resultSetOfBeans.get(0);
	}

	/**
	 * Searches for the event definition CRF bean of a site level, by a specific study event
	 * and a specific CRF version.
	 *
	 * @param studyEventId  the study event id, to search on.
	 * @param crfVersionId  the CRF version id, to search on.
	 * @param siteId        the site id, to search on.
	 * @param parentStudyId the study id, to search on.
	 * @return the instance of the <code>EventDefinitionCRFBean</code> class, that matches the SQL query;
	 * if such an event definition CRF record was not found, creates and returns an empty
	 * instance of the <code>EventDefinitionCRFBean</code> class.
	 */
	private EventDefinitionCRFBean findByStudyEventIdAndCRFVersionIdAndSiteIdAndParentStudyId(int studyEventId,
			int crfVersionId, int siteId, int parentStudyId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index++, studyEventId);
		queryParameters.put(index++, crfVersionId);
		queryParameters.put(index++, siteId);
		queryParameters.put(index++, parentStudyId);
		queryParameters.put(index, siteId);

		List<HashMap<String, Object>> recordsFromDB =
				this.select(digester.getQuery("findByStudyEventIdAndCRFVersionIdAndSiteIdAndParentStudyId"),
						queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans.isEmpty() ? new EventDefinitionCRFBean() : resultSetOfBeans.get(0);
	}

	/**
	 * Searches for the event definition CRF bean by a specific study event definition and a specific CRF.
	 * <p/>
	 * If the <code>study</code> parameter represents a study, then this method searches for
	 * the event definition CRF bean of a study level, otherwise - searches for the event definition CRF bean of a site
	 * level.
	 *
	 * @param study                  an instance of the <code>StudyBean</code> class, which represents the study,
	 *                               to search in.
	 * @param studyEventDefinitionId the study event definition id, to search on.
	 * @param crfId                  the CRF id, to search on.
	 * @return the instance of the <code>EventDefinitionCRFBean</code> class, that matches the SQL query;
	 * if such an event definition CRF record was not found, creates and returns an empty
	 * instance of the <code>EventDefinitionCRFBean</code> class.
	 */
	public EventDefinitionCRFBean findByStudyEventDefinitionIdAndCRFId(StudyBean study, int studyEventDefinitionId,
			int crfId) {

		return study.isSite(study.getParentStudyId()) ? findByStudyEventDefinitionIdAndCRFIdAndSiteIdAndParentStudyId(
				studyEventDefinitionId, crfId, study.getId(), study.getParentStudyId())
				: findForStudyByStudyEventDefinitionIdAndCRFId(studyEventDefinitionId, crfId);
	}

	/**
	 * Searches for the event definition CRF bean of a study level (records for sites are excluded),
	 * by a specific study event definition and a specific CRF.
	 *
	 * @param studyEventDefinitionId the study event definition id, to search on.
	 * @param crfId                  the CRF id, to search on.
	 * @return the instance of the <code>EventDefinitionCRFBean</code> class, that matches the SQL query;
	 * if such an event definition CRF record was not found, creates and returns an empty
	 * instance of the <code>EventDefinitionCRFBean</code> class.
	 */
	public EventDefinitionCRFBean findForStudyByStudyEventDefinitionIdAndCRFId(int studyEventDefinitionId, int crfId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index++, studyEventDefinitionId);
		queryParameters.put(index, crfId);

		List<HashMap<String, Object>> recordsFromDB =
				this.select(digester.getQuery("findForStudyByStudyEventDefinitionIdAndCRFId"), queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans.isEmpty() ? new EventDefinitionCRFBean() : resultSetOfBeans.get(0);
	}

	/**
	 * Searches for the event definition CRF bean of a site level, by a specific study event definition
	 * and a specific CRF.
	 *
	 * @param studyEventDefId the study event definition id, to search on.
	 * @param crfId           the CRF id, to search on.
	 * @param siteId          the site id, to search on.
	 * @param parentStudyId   the study id, to search on.
	 * @return the instance of the <code>EventDefinitionCRFBean</code> class, that matches the SQL query;
	 * if such an event definition CRF record was not found, creates and returns an empty
	 * instance of the <code>EventDefinitionCRFBean</code> class.
	 */
	private EventDefinitionCRFBean findByStudyEventDefinitionIdAndCRFIdAndSiteIdAndParentStudyId(int studyEventDefId,
			int crfId, int siteId, int parentStudyId) {

		this.setTypesExpected();

		Map<Integer, Object> queryParameters = new HashMap<Integer, Object>();
		int index = 1;
		queryParameters.put(index++, studyEventDefId);
		queryParameters.put(index++, crfId);
		queryParameters.put(index++, siteId);
		queryParameters.put(index++, parentStudyId);
		queryParameters.put(index, siteId);

		List<HashMap<String, Object>> recordsFromDB =
				this.select(digester.getQuery("findByStudyEventDefinitionIdAndCRFIdAndSiteIdAndParentStudyId"),
						queryParameters);

		List<EventDefinitionCRFBean> resultSetOfBeans = new ArrayList<EventDefinitionCRFBean>();

		for (HashMap<String, Object> record : recordsFromDB) {
			resultSetOfBeans.add(this.getEntityFromHashMap(record));
		}

		return resultSetOfBeans.isEmpty() ? new EventDefinitionCRFBean() : resultSetOfBeans.get(0);
	}
}
