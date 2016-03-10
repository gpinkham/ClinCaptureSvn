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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyType;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

/**
 * StudyDAO.java, the data access object that users will access the database for study objects.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class StudyDAO extends AuditableEntityDAO implements IStudyDAO {

	public StudyDAO(DataSource ds) {
		super(ds);
	}

	public StudyDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
	}

	public StudyDAO(DataSource ds, DAODigester digester, Locale locale) {

		this(ds, digester);
		this.locale = locale;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_STUDY;
	}

	@Override
	public void setTypesExpected() {

		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT); // sid
		this.setTypeExpected(index++, TypeNames.INT); // parent id
		this.setTypeExpected(index++, TypeNames.STRING); // ident
		this.setTypeExpected(index++, TypeNames.STRING); // second ident
		this.setTypeExpected(index++, TypeNames.STRING); // name
		this.setTypeExpected(index++, TypeNames.STRING); // summary
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // start
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // end
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // create
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // update
		this.setTypeExpected(index++, TypeNames.INT); // owner
		this.setTypeExpected(index++, TypeNames.INT); // updater
		this.setTypeExpected(index++, TypeNames.INT); // type id
		this.setTypeExpected(index++, TypeNames.INT); // status
		this.setTypeExpected(index++, TypeNames.STRING); // pi
		this.setTypeExpected(index++, TypeNames.STRING); // fname
		this.setTypeExpected(index++, TypeNames.STRING); // fcity
		this.setTypeExpected(index++, TypeNames.STRING); // fstate
		this.setTypeExpected(index++, TypeNames.STRING); // fzip
		this.setTypeExpected(index++, TypeNames.STRING); // country
		this.setTypeExpected(index++, TypeNames.STRING); // frs
		this.setTypeExpected(index++, TypeNames.STRING); // fcn
		this.setTypeExpected(index++, TypeNames.STRING); // fcdegree
		this.setTypeExpected(index++, TypeNames.STRING); // fcphone
		this.setTypeExpected(index++, TypeNames.STRING); // fcemail
		this.setTypeExpected(index++, TypeNames.STRING); // prottype
		this.setTypeExpected(index++, TypeNames.STRING); // protdesc
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // pdateverif
		this.setTypeExpected(index++, TypeNames.STRING); // phase
		this.setTypeExpected(index++, TypeNames.INT); // expectotenroll
		this.setTypeExpected(index++, TypeNames.STRING); // sponsor
		this.setTypeExpected(index++, TypeNames.STRING); // collab
		this.setTypeExpected(index++, TypeNames.STRING); // medline
		this.setTypeExpected(index++, TypeNames.STRING); // url
		this.setTypeExpected(index++, TypeNames.STRING); // url-desc
		this.setTypeExpected(index++, TypeNames.STRING); // conds
		this.setTypeExpected(index++, TypeNames.STRING); // keyw
		this.setTypeExpected(index++, TypeNames.STRING); // eligible
		this.setTypeExpected(index++, TypeNames.STRING); // gender, no char avail.
		this.setTypeExpected(index++, TypeNames.STRING); // agemax
		this.setTypeExpected(index++, TypeNames.STRING); // agemin
		this.setTypeExpected(index++, TypeNames.BOOL); // healthy volunteer
		this.setTypeExpected(index++, TypeNames.STRING); // purpose
		this.setTypeExpected(index++, TypeNames.STRING); // allocation
		this.setTypeExpected(index++, TypeNames.STRING); // masking
		this.setTypeExpected(index++, TypeNames.STRING); // control
		this.setTypeExpected(index++, TypeNames.STRING); // assignment
		this.setTypeExpected(index++, TypeNames.STRING); // endpoint
		this.setTypeExpected(index++, TypeNames.STRING); // interv
		this.setTypeExpected(index++, TypeNames.STRING); // duration
		this.setTypeExpected(index++, TypeNames.STRING); // selection
		this.setTypeExpected(index++, TypeNames.STRING); // timing
		this.setTypeExpected(index++, TypeNames.STRING); // official_title
		this.setTypeExpected(index++, TypeNames.BOOL); // results_reference
		this.setTypeExpected(index++, TypeNames.STRING); // oc oid
		this.setTypeExpected(index, TypeNames.INT);
	}

	/**
	 * <b>update </b>, the method that returns an updated study bean after it updates the database. Note that we can use
	 * the three stages from our creation use case.
	 *
	 * @param eb
	 *            EntityBean
	 * @return sb an updated study bean.
	 */
	public EntityBean update(EntityBean eb) {
		StudyBean sb = (StudyBean) eb;
		sb = this.updateStepOne(sb);
		sb = this.createStepTwo(sb);
		sb = this.createStepThree(sb);
		sb = this.createStepFour(sb);
		return sb;
	}

	/**
	 * updateStepOne, the update method for the database. This method takes the place of createStepOne, since it runs an
	 * update and assumes you already have a primary key in the study bean object.
	 * 
	 * @param sb
	 *            the study bean which will be updated.
	 * @return sb the study bean after it is updated with this phase.
	 */
	public StudyBean updateStepOne(StudyBean sb) {
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();

		int index = 1;
		if (sb.getParentStudyId() == 0) {
			nullVars.put(index, Types.INTEGER);
			variables.put(index, null);
		} else {
			variables.put(index, sb.getParentStudyId());
		}
		index++;

		variables.put(index++, sb.getName());
		variables.put(index++, sb.getOfficialTitle());
		variables.put(index++, sb.getIdentifier());
		variables.put(index++, sb.getSecondaryIdentifier());
		variables.put(index++, sb.getSummary());
		variables.put(index++, sb.getPrincipalInvestigator());

		if (sb.getDatePlannedStart() == null) {
			nullVars.put(index, Types.TIMESTAMP);
			variables.put(index, null);
		} else {
			variables.put(index, new Timestamp(sb.getDatePlannedStart().getTime()));
		}
		index++;

		if (sb.getDatePlannedEnd() == null) {
			nullVars.put(index, Types.TIMESTAMP);
			variables.put(index, null);
		} else {
			variables.put(index, new Timestamp(sb.getDatePlannedEnd().getTime()));
		}
		index++;

		variables.put(index++, sb.getFacilityName());
		variables.put(index++, sb.getFacilityCity());
		variables.put(index++, sb.getFacilityState());
		variables.put(index++, sb.getFacilityZip());
		variables.put(index++, sb.getFacilityCountry());
		variables.put(index++, sb.getFacilityRecruitmentStatus());
		variables.put(index++, sb.getFacilityContactName());
		variables.put(index++, sb.getFacilityContactDegree());
		variables.put(index++, sb.getFacilityContactPhone());
		variables.put(index++, sb.getFacilityContactEmail());
		variables.put(index++, sb.getStatus().getId()); // status
		variables.put(index++, sb.getUpdaterId()); // owner
		variables.put(index++, sb.getOldStatus().getId()); // study id
		variables.put(index, sb.getId()); // study id
		this.execute(digester.getQuery("updateStepOne"), variables, nullVars);
		return sb;
	}

	/**
	 * <b>create </b>, the method that creates a study in the database.
	 * <P>
	 * note: create is split up into four custom functions, per the use case; we are creating the standard create
	 * function here which calls all four functions at once, but the seperate functions may be required in the control
	 * servlets.
	 *
	 * @param eb
	 *            EntityBean
	 * @return eb the created entity bean.
	 */
	public EntityBean create(EntityBean eb) {
		StudyBean sb = (StudyBean) eb;
		sb = this.createStepOne(sb);
		// in the above step, we will have created a primary key,
		// and in the next steps, we update the study bean
		// in phases
		sb = this.createStepTwo(sb);
		sb = this.createStepThree(sb);
		sb = this.createStepFour(sb);
		return sb;
	}

	/**
	 * findNextKey, a method to return a simple int from the database.
	 * 
	 * @return int, which is the next primary key for creating a study.
	 */
	public int findNextKey() {
		this.unsetTypeExpected();
		Integer keyInt = 0;
		this.setTypeExpected(1, TypeNames.INT);
		ArrayList alist = this.select(digester.getQuery("findNextKey"));
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			HashMap key = (HashMap) it.next();
			keyInt = (Integer) key.get("key");
		}
		return keyInt;
	}

	/**
	 * createStepOne, per the 'Detailed use case for administer system document v1.0rc1' document. We insert the study
	 * in this method, and then update the same study in the next three steps.
	 * <P>
	 * The next three steps, by the way, can then be used to update studies as well.
	 * 
	 * @param sb
	 *            Study bean about to be created.
	 * @return same study bean with a primary key in the ID field.
	 */
	public StudyBean createStepOne(StudyBean sb) {
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		sb.setId(this.findNextKey());
		int index = 1;
		variables.put(index++, sb.getId());

		if (sb.getParentStudyId() == 0) {
			nullVars.put(index, Types.INTEGER);
			variables.put(index, null);
		} else {
			variables.put(index, sb.getParentStudyId());
		}
		index++;

		variables.put(index++, sb.getName());
		variables.put(index++, sb.getOfficialTitle());
		variables.put(index++, sb.getIdentifier());
		variables.put(index++, sb.getSecondaryIdentifier());
		variables.put(index++, sb.getSummary());
		variables.put(index++, sb.getPrincipalInvestigator());

		if (sb.getDatePlannedStart() == null) {
			nullVars.put(index, Types.TIMESTAMP);
			variables.put(index, null);
		} else {
			variables.put(index, new Timestamp(sb.getDatePlannedStart().getTime()));
		}
		index++;

		if (sb.getDatePlannedEnd() == null) {
			nullVars.put(index, Types.TIMESTAMP);
			variables.put(index, null);
		} else {
			variables.put(index, new Timestamp(sb.getDatePlannedEnd().getTime()));
		}
		index++;

		variables.put(index++, sb.getFacilityName());
		variables.put(index++, sb.getFacilityCity());
		variables.put(index++, sb.getFacilityState());
		variables.put(index++, sb.getFacilityZip());
		variables.put(index++, sb.getFacilityCountry());
		variables.put(index++, sb.getFacilityRecruitmentStatusKey());
		variables.put(index++, sb.getFacilityContactName());
		variables.put(index++, sb.getFacilityContactDegree());
		variables.put(index++, sb.getFacilityContactPhone());
		variables.put(index++, sb.getFacilityContactEmail());
		variables.put(index++, sb.getStatus().getId());
		variables.put(index++, sb.getOwnerId());
		variables.put(index, getValidOid(sb));
		// replace this with the owner id
		this.execute(digester.getQuery("createStepOne"), variables, nullVars);
		return sb;
	}

	// we are generating and creating the valid oid at step one, tbh
	private String getOid(StudyBean sb) {

		String oid;
		try {
			oid = sb.getOid() != null ? sb.getOid() : sb.getOidGenerator(ds).generateOid(sb.getIdentifier());
			return oid;
		} catch (Exception e) {
			throw new RuntimeException("CANNOT GENERATE OID");
		}
	}

	private String getValidOid(StudyBean sb) {

		String oid = getOid(sb);
		logger.info("*** " + oid);
		String oidPreRandomization = oid;
		while (findByOid(oid) != null) {
			oid = sb.getOidGenerator(ds).randomizeOid(oidPreRandomization);
		}
		logger.info("returning the following oid: " + oid);
		return oid;

	}

	/**
	 *
	 * @param oid
	 *            String
	 * @return StudyBean
	 */
	public StudyBean findByOid(String oid) {
		StudyBean sb;
		this.unsetTypeExpected();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, oid);
		ArrayList alist = this.select(digester.getQuery("findByOid"), variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			sb = this.getEntityFromHashMap((HashMap) it.next());
			return sb;
		} else {
			logger.info("returning null from find by oid...");
			return null;
		}

	}

	/**
	 *
	 * @param oid
	 *            String
	 * @return StudyBean
	 */
	public StudyBean findByUniqueIdentifier(String oid) {
		StudyBean sb;
		this.unsetTypeExpected();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, oid);
		ArrayList alist = this.select(digester.getQuery("findByUniqueIdentifier"), variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			sb = this.getEntityFromHashMap((HashMap) it.next());
			return sb;
		} else {
			logger.info("returning null from find by Unique Identifier...");
			return null;
		}
	}

	/**
	 * @param parentUniqueIdentifier
	 *            String
	 * @param siteUniqueIdentifier
	 *            String
	 * @return StudyBean
	 */
	public StudyBean findSiteByUniqueIdentifier(String parentUniqueIdentifier, String siteUniqueIdentifier) {
		StudyBean sb;
		this.unsetTypeExpected();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, parentUniqueIdentifier);
		variables.put(2, siteUniqueIdentifier);
		ArrayList alist = this.select(digester.getQuery("findSiteByUniqueIdentifier"), variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			sb = this.getEntityFromHashMap((HashMap) it.next());
			return sb;
		} else {
			logger.info("returning null from find by Unique Identifier...");
			return null;
		}
	}

	/**
	 *
	 * @param sb
	 *            StudyBean
	 * @return StudyBean
	 */
	public StudyBean createStepTwo(StudyBean sb) {

		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		int index = 1;
		variables.put(index++, sb.getType().getId());
		variables.put(index++, sb.getProtocolTypeKey());
		variables.put(index++, sb.getProtocolDescription());

		if (sb.getProtocolDateVerification() == null) {
			nullVars.put(index, Types.TIMESTAMP);
			variables.put(index, null);
		} else {
			variables.put(index, new Timestamp(sb.getProtocolDateVerification().getTime()));
		}
		index++;

		variables.put(index++, sb.getPhaseKey());
		variables.put(index++, sb.getExpectedTotalEnrollment());
		variables.put(index++, sb.getSponsor());
		variables.put(index++, sb.getCollaborators());
		variables.put(index++, sb.getMedlineIdentifier());
		variables.put(index++, sb.isResultsReference());
		variables.put(index++, sb.getUrl());
		variables.put(index++, sb.getUrlDescription());
		variables.put(index++, sb.getConditions());
		variables.put(index++, sb.getKeywords());
		variables.put(index++, sb.getEligibility());
		variables.put(index++, sb.getGenderKey());
		variables.put(index++, sb.getAgeMax());
		variables.put(index++, sb.getAgeMin());
		variables.put(index++, sb.getHealthyVolunteerAccepted());
		variables.put(index, sb.getId());
		this.execute(digester.getQuery("createStepTwo"), variables, nullVars);
		return sb;
	}

	/**
	 *
	 * @param sb
	 *            StudyBean
	 * @return StudyBean
	 */
	public StudyBean createStepThree(StudyBean sb) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, sb.getPurposeKey());
		variables.put(index++, sb.getAllocationKey());
		variables.put(index++, sb.getMaskingKey());
		variables.put(index++, sb.getControlKey());
		variables.put(index++, sb.getAssignmentKey());
		variables.put(index++, sb.getEndpointKey());
		variables.put(index++, sb.getInterventionsKey());
		variables.put(index, sb.getId());
		this.execute(digester.getQuery("createStepThree"), variables);
		return sb;
	}

	/**
	 *
	 * @param sb
	 *            StudyBean
	 * @return StudyBean
	 */
	public StudyBean createStepFour(StudyBean sb) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, sb.getDurationKey());
		variables.put(index++, sb.getSelectionKey());
		variables.put(index++, sb.getTimingKey());
		variables.put(index, sb.getId());
		this.execute(digester.getQuery("createStepFour"), variables);
		return sb;
	}

	/**
	 * getEntityFromHashMap, the method that gets the object from the database query.
	 *
	 * @param hm
	 *            HashMap
	 * @return Object
	 */
	@SuppressWarnings("deprecation")
	public StudyBean getEntityFromHashMap(HashMap hm) {
		StudyBean eb = new StudyBean();

		// first set all the strings
		eb.setIdentifier((String) hm.get("unique_identifier"));
		eb.setName((String) hm.get("name"));
		eb.setSummary((String) hm.get("summary"));
		eb.setSecondaryIdentifier((String) hm.get("secondary_identifier"));
		eb.setPrincipalInvestigator((String) hm.get("principal_investigator"));
		eb.setFacilityName((String) hm.get("facility_name"));
		eb.setFacilityCity((String) hm.get("facility_city"));
		eb.setFacilityState((String) hm.get("facility_state"));
		eb.setFacilityZip((String) hm.get("facility_zip"));
		eb.setFacilityCountry((String) hm.get("facility_country"));
		eb.setFacilityRecruitmentStatus((String) hm.get("facility_recruitment_status"));
		eb.setFacilityContactName((String) hm.get("facility_contact_name"));
		eb.setFacilityContactDegree((String) hm.get("facility_contact_degree"));
		eb.setFacilityContactPhone((String) hm.get("facility_contact_phone"));
		eb.setFacilityContactEmail((String) hm.get("facility_contact_email"));
		eb.setProtocolType((String) hm.get("protocol_type"));
		eb.setProtocolDescription((String) hm.get("protocol_description"));
		eb.setPhase((String) hm.get("phase"));
		eb.setSponsor((String) hm.get("sponsor"));
		eb.setCollaborators((String) hm.get("collaborators"));
		eb.setMedlineIdentifier((String) hm.get("medline_identifier"));
		eb.setUrl((String) hm.get("url"));
		eb.setUrlDescription((String) hm.get("url_description"));
		eb.setConditions((String) hm.get("conditions"));
		eb.setKeywords((String) hm.get("keywords"));
		eb.setEligibility((String) hm.get("eligibility"));
		String gender = (String) hm.get("gender");
		eb.setGender(gender);
		eb.setAgeMax((String) hm.get("age_max"));
		eb.setAgeMin((String) hm.get("age_min"));
		eb.setPurpose((String) hm.get("purpose"));
		eb.setAllocation((String) hm.get("allocation"));
		eb.setMasking((String) hm.get("masking"));
		eb.setControl((String) hm.get("control"));
		eb.setAssignment((String) hm.get("assignment"));
		eb.setEndpoint((String) hm.get("endpoint"));
		eb.setInterventions((String) hm.get("interventions"));
		eb.setDuration((String) hm.get("duration"));
		eb.setSelection((String) hm.get("selection"));
		eb.setTiming((String) hm.get("timing"));
		eb.setOfficialTitle((String) hm.get("official_title"));

		eb.setHealthyVolunteerAccepted((Boolean) hm.get("healthy_volunteer_accepted"));
		eb.setResultsReference((Boolean) hm.get("results_reference"));

		Integer studyId = (Integer) hm.get("study_id");
		eb.setId(studyId);
		Integer parentStudyId = (Integer) hm.get("parent_study_id");
		if (parentStudyId == null) {
			eb.setParentStudyId(0);
		} else {
			eb.setParentStudyId(parentStudyId);
		}
		Integer ownerId = (Integer) hm.get("owner_id");
		eb.setOwnerId(ownerId);
		Integer updateId = (Integer) hm.get("update_id");
		eb.setUpdaterId(updateId);
		Integer typeId = (Integer) hm.get("type_id");
		eb.setType(StudyType.get(typeId));
		Integer statusId = (Integer) hm.get("status_id");
		eb.setStatus(Status.get(statusId));
		Integer expecTotalEnrollment = (Integer) hm.get("expected_total_enrollment");
		eb.setExpectedTotalEnrollment(expecTotalEnrollment);
		Date dateCreated = (Date) hm.get("date_created");
		Date dateUpdated = (Date) hm.get("date_updated");
		Date datePlannedStart = (Date) hm.get("date_planned_start");
		Date datePlannedEnd = (Date) hm.get("date_planned_end");
		Date dateProtocolVerification = (Date) hm.get("protocol_date_verification");

		eb.setCreatedDate(dateCreated);
		eb.setUpdatedDate(dateUpdated);
		eb.setDatePlannedStart(datePlannedStart);
		eb.setDatePlannedEnd(datePlannedEnd);
		eb.setProtocolDateVerification(dateProtocolVerification); // added by
		eb.setStatus(Status.get(statusId));
		eb.setOid((String) hm.get("oc_oid"));
		Integer oldStatusId = (Integer) hm.get("old_status_id");
		eb.setOldStatus(Status.get(oldStatusId));
		return eb;
	}

	/**
	 *
	 * @param username
	 *            String
	 * @return Collection
	 */
	public Collection findAllByUser(String username) {
		this.unsetTypeExpected();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, username);
		ArrayList alist = this.select(digester.getQuery("findAllByUser"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 *
	 * @param crfId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList<Integer> getStudyIdsByCRF(int crfId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, crfId);
		ArrayList alist = this.select(digester.getQuery("getStudyIdsByCRF"), variables);
		ArrayList<Integer> al = new ArrayList<Integer>();
		for (Object anAlist : alist) {
			HashMap h = (HashMap) anAlist;
			al.add((Integer) h.get("study_id"));
		}
		return al;
	}

	/**
	 *
	 * @param username
	 *            String
	 * @return Collection
	 */
	public Collection findAllByUserNotRemoved(String username) {
		this.unsetTypeExpected();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, username);
		ArrayList alist = this.select(digester.getQuery("findAllByUserNotRemoved"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 *
	 * @param username
	 *            String
	 * @return List
	 */
	public List<StudyBean> findAllActiveStudiesWhereUserHasRole(String username) {
		this.unsetTypeExpected();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, username);
		variables.put(2, username);
		List<HashMap> alist = this.select(digester.getQuery("findAllActiveStudiesWhereUserHasRole"), variables);
		List<StudyBean> al = new ArrayList<StudyBean>();
		for (HashMap anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap(anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 *
	 * @param username
	 *            String
	 * @return List
	 */
	public List<StudyBean> findAllActiveWhereUserHasRole(String username) {
		this.unsetTypeExpected();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, username);
		List<HashMap> alist = this.select(digester.getQuery("findAllActiveWhereUserHasRole"), variables);
		List<StudyBean> al = new ArrayList<StudyBean>();
		for (HashMap anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap(anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 *
	 * @param username
	 *            String
	 * @return List
	 */
	public List<StudyBean> findAllActiveWhereUserHasActiveRole(String username) {
		this.unsetTypeExpected();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, username);
		List<HashMap> alist = this.select(digester.getQuery("findAllActiveWhereUserHasActiveRole"), variables);
		List<StudyBean> al = new ArrayList<StudyBean>();
		for (HashMap anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap(anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 *
	 * @param status
	 *            String
	 * @return ArrayList
	 */
	public ArrayList findAllByStatus(Status status) {
		this.unsetTypeExpected();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, status.getId());
		String sql = digester.getQuery("findAllByStatus");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	public Collection findAll() {
		return findAllByLimit(false);
	}

	/**
	 *
	 * @param isLimited
	 *            boolean
	 * @return Collection
	 */
	public Collection findAllByLimit(boolean isLimited) {
		this.setTypesExpected();
		String sql;
		if (isLimited) {
			if (CoreResources.getDBType().equals("oracle")) {
				sql = digester.getQuery("findAll") + " where ROWNUM <=5";
			} else {
				sql = digester.getQuery("findAll") + " limit 5";
			}
		} else {
			sql = digester.getQuery("findAll");
		}
		ArrayList alist = this.select(sql);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 *
	 * @return Collection
	 */
	public Collection findAllNotRemoved() {
		this.setTypesExpected();
		String sql = digester.getQuery("findAllNotRemoved");
		ArrayList alist = this.select(sql);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 *
	 * @return Collection
	 */
	public Collection findAllParents() {
		this.setTypesExpected();

		String sql = digester.getQuery("findAllParents");
		ArrayList alist = this.select(sql);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 *
	 * @param parentStudyId
	 *            int
	 * @return Collection
	 */
	public Collection findAllByParent(int parentStudyId) {
		return findAllByParentAndLimit(parentStudyId, false);
	}

	/**
	 *
	 * @param parentStudyId
	 *            int
	 * @param isLimited
	 *            boolean
	 * @return Collection
	 */
	public Collection findAllByParentAndLimit(int parentStudyId, boolean isLimited) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, parentStudyId);
		ArrayList alist;
		if (isLimited) {
			alist = this.select(digester.getQuery("findAllByParentLimit5"), variables);
		} else {
			alist = this.select(digester.getQuery("findAllByParent"), variables);
		}
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 *
	 * @param parentStudyId
	 *            int
	 * @return List
	 */
	public List<StudyBean> findAllByParentAndActive(int parentStudyId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, parentStudyId);
		List<HashMap> alist = this.select(digester.getQuery("findAllByParentAndActive"), variables);
		List<StudyBean> al = new ArrayList<StudyBean>();
		for (HashMap anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap(anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 *
	 * @param studyId
	 *            int
	 * @return Collection
	 */
	public Collection findAll(int studyId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);
		ArrayList alist;
		alist = this.select(digester.getQuery("findAllByStudyId"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public EntityBean findByPK(int id) {
		StudyBean eb = new StudyBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 *
	 * @param name
	 *            String
	 * @return EntityBean
	 */
	public EntityBean findByName(String name) {
		StudyBean eb = new StudyBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, name);

		String sql = digester.getQuery("findByName");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * Find Study by name.
	 * 
	 * @param name
	 *            String
	 * @return EntityBean
	 */
	public EntityBean findStudyByName(String name) {
		StudyBean eb = new StudyBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, name);

		String sql = digester.getQuery("findStudyByName");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * deleteTestOnly, used only to clean up after unit testing.
	 *
	 * @param name
	 *            String
	 */
	public void deleteTestOnly(String name) {
		HashMap variables = new HashMap();
		variables.put(1, name);
		this.execute(digester.getQuery("deleteTestOnly"), variables);
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	/**
	 * Only for use by getChildrenByParentIds.
	 * 
	 * @param answer
	 *            HashMap
	 * @param parentId
	 *            int
	 * @param child
	 *            StudyBean
	 * @return HashMap
	 */
	private HashMap addChildToParent(HashMap answer, int parentId, StudyBean child) {
		Integer key = parentId;
		ArrayList children = (ArrayList) answer.get(key);
		if (children == null) {
			children = new ArrayList();
		}
		children.add(child);
		answer.put(key, children);
		return answer;
	}

	/**
	 * @param allStudies
	 *            The result of findAll().
	 * @return A HashMap where the keys are Integers whose intValue are studyIds and the values are ArrayLists; each
	 *         element of the ArrayList is a StudyBean representing a child of the study whose id is the key
	 *         <p/>
	 *         e.g., if A has children B and C, then this will return a HashMap h where h.get(A.getId()) returns an
	 *         ArrayList whose elements are B and C
	 */
	public HashMap getChildrenByParentIds(ArrayList allStudies) {
		HashMap answer = new HashMap();

		if (allStudies == null) {
			return answer;
		}
		for (Object allStudy : allStudies) {
			StudyBean study = (StudyBean) allStudy;
			int parentStudyId = study.getParentStudyId();
			if (parentStudyId > 0) {
				answer = addChildToParent(answer, parentStudyId, study);
			}
		}
		return answer;
	}

	/**
	 *
	 * @param study
	 *            StudyBean
	 * @return Collection
	 */
	public Collection<Integer> findAllSiteIdsByStudy(StudyBean study) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT); // sid
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		ArrayList alist = this.select(digester.getQuery("findAllSiteIdsByStudy"), variables);
		ArrayList<Integer> al = new ArrayList<Integer>();
		for (Object anAlist : alist) {
			HashMap h = (HashMap) anAlist;
			al.add((Integer) h.get("study_id"));
		}
		return al;
	}

	/**
	 *
	 * @param study
	 *            StudyBean
	 * @return Collection
	 */
	public Collection<Integer> findOlnySiteIdsByStudy(StudyBean study) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT); // sid
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, study.getId());
		ArrayList alist = this.select(digester.getQuery("findOlnySiteIdsByStudy"), variables);
		ArrayList<Integer> al = new ArrayList<Integer>();
		for (Object anAlist : alist) {
			HashMap h = (HashMap) anAlist;
			al.add((Integer) h.get("study_id"));
		}
		return al;
	}

	/**
	 *
	 * @param sb
	 *            StudyBean
	 * @return StudyBean
	 */
	public StudyBean updateSitesStatus(StudyBean sb) {
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		int index = 1;
		variables.put(index++, sb.getStatus().getId());
		variables.put(index++, sb.getOldStatus().getId());
		variables.put(index, sb.getId());
		this.execute(digester.getQuery("updateSitesStatus"), variables, nullVars);
		return sb;
	}

	/**
	 *
	 * @param studyId
	 *            int
	 * @return int
	 */
	public int countLockedEvents(int studyId) {
		int result = 0;

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		String sql = digester.getQuery("countLockedEvents");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			result = (Integer) ((HashMap) it.next()).get("count");
		}
		return result;
	}

	/**
	 *
	 * @param studyId
	 *            int
	 * @return int
	 */
	public int countEvents(int studyId) {
		int result = 0;

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		String sql = digester.getQuery("countEvents");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			result = (Integer) ((HashMap) it.next()).get("count");
		}
		return result;
	}

	/**
	 *
	 * @param studies
	 *            List
	 * @return Map
	 */
	public Map<Integer, Map<String, Integer>> analyzeEvents(List<StudyBean> studies) {
		Map<Integer, Map<String, Integer>> infoMap = new HashMap<Integer, Map<String, Integer>>();

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		String sql = digester.getQuery("analyzeEvents");

		String tmp = "";
		for (StudyBean sb : studies) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("countEvents", 0);
			map.put("countLockedEvents", 0);
			infoMap.put(sb.getId(), map);
			tmp += (tmp.isEmpty() ? "" : ", ") + sb.getId();
		}

		if (!tmp.isEmpty()) {
			ResultSet rs = null;
			Connection con = null;
			PreparedStatement ps = null;
			sql = sql.replace("?", tmp);
			try {
				con = ds.getConnection();
				if (con.isClosed()) {
					if (logger.isWarnEnabled()) {
						logger.warn("Connection is closed: StudyDAO.analyzeEvents!");
					}
					throw new SQLException();
				}

				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					Map<String, Integer> map = infoMap.get(rs.getInt("studyId"));
					map.put("countEvents", rs.getInt("countEvents"));
					map.put("countLockedEvents", rs.getInt("countLockedEvents"));
				}
				if (logger.isInfoEnabled()) {
					logger.info("Executing dynamic query, StudyDAO.analyzeEvents:query " + sql);
				}
				signalSuccess();
			} catch (SQLException sqle) {
				signalFailure(sqle);
				if (logger.isWarnEnabled()) {
					logger.warn("Exception while executing dynamic query, StudyDAO.analyzeEvents: " + sql + ":message: "
							+ sqle.getMessage());
					sqle.printStackTrace();
				}
			} finally {
				this.closeIfNecessary(con, rs, ps);
			}
		}

		return infoMap;
	}

	/**
	 *
	 * @param sb
	 *            StudyBean
	 * @return StudyBean
	 */
	public StudyBean updateStudyStatus(StudyBean sb) {
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		int index = 1;
		variables.put(index++, sb.getStatus().getId());
		variables.put(index++, sb.getOldStatus().getId());
		variables.put(index, sb.getId());
		this.execute(digester.getQuery("updateStudyStatus"), variables, nullVars);
		return sb;
	}

	/**
	 *
	 * @param studySubjectId
	 *            int
	 * @return StudyBean
	 */
	public StudyBean findByStudySubjectId(int studySubjectId) {
		StudyBean sb = new StudyBean();
		HashMap variables = new HashMap();
		this.setTypesExpected();
		variables.put(1, studySubjectId);
		ArrayList alist = this.select(digester.getQuery("findByStudySubjectId"), variables);
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			sb = this.getEntityFromHashMap((HashMap) it.next());
		}
		return sb;
	}

	/**
	 *
	 * @param parentStudyId
	 *            int
	 * @return Collection
	 */
	public Collection findAllByParentStudyIdOrderedByIdAsc(int parentStudyId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, parentStudyId);
		variables.put(2, parentStudyId);
		ArrayList alist = this.select(digester.getQuery("findAllByParentStudyIdOrderedByIdAsc"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	public ArrayList<StudyBean> findAllActiveWhereCRFIsUsed(int crfId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, crfId);
		ArrayList alist = this.select(digester.getQuery("findAllActiveWhereCRFIsUsed"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyBean eb = this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}
}
