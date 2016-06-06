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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * StudyEventDefinitionDAO.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class StudyEventDefinitionDAO extends AuditableEntityDAO {

	private void setQueryNames() {
		findAllByStudyName = "findAllByStudy";
		findAllActiveByStudyName = "findAllActiveByStudy";
		findByPKAndStudyName = "findByPKAndStudy";
	}

	/**
	 * StudyEventDefinitionDAO constructor.
	 * 
	 * @param ds
	 *            DataSource
	 */
	public StudyEventDefinitionDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	/**
	 * StudyEventDefinitionDAO constructor.
	 * 
	 * @param ds
	 *            DataSource
	 * @param digester
	 *            DAODigester
	 */
	public StudyEventDefinitionDAO(DataSource ds, DAODigester digester) {
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
	public StudyEventDefinitionDAO(DataSource ds, DAODigester digester, Locale locale) {

		this(ds, digester);
		this.locale = locale;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_STUDYEVENTDEFNITION;
	}

	@Override
	public void setTypesExpected() {
		int ind = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.BOOL);
		this.setTypeExpected(ind++, TypeNames.STRING);
		this.setTypeExpected(ind++, TypeNames.STRING);
		// int int date date int
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP);
		this.setTypeExpected(ind++, TypeNames.TIMESTAMP);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.STRING);
		// Clinovo Ticket #65
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.INT);
		this.setTypeExpected(ind++, TypeNames.BOOL);
		this.setTypeExpected(ind, TypeNames.INT);
	}

	/**
	 * <P>
	 * findNextKey, a method to return a simple int from the database.
	 * 
	 * @return int, which is the next primary key for creating a study event definition.
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

	private String getOid(StudyEventDefinitionBean sedb) {

		String oid;
		try {
			oid = sedb.getOid() != null ? sedb.getOid() : sedb.getOidGenerator(ds).generateOid(sedb.getName());
			return oid;
		} catch (Exception e) {
			throw new RuntimeException("CANNOT GENERATE OID");
		}
	}

	private String getValidOid(StudyEventDefinitionBean sedb) {

		String oid = getOid(sedb);
		logger.info(oid);
		String oidPreRandomization = oid;
		while (findByOid(oid) != null) {
			oid = sedb.getOidGenerator(ds).randomizeOid(oidPreRandomization);
		}
		return oid;

	}

	/**
	 * {@inheritDoc}
	 */
	public EntityBean create(EntityBean eb) {
		// study_event_definition_id ,
		// STUDY_ID, NAME,DESCRIPTION, REPEATING, TYPE, CATEGORY, OWNER_ID,
		// STATUS_ID, DATE_CREATED,ordinal,oid
		int ind = 1;
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) eb;
		sedb.setId(this.findNextKey());
		sedb.setOid(getValidOid(sedb));
		logger.info("***id:" + sedb.getId());
		HashMap variables = new HashMap();
		variables.put(ind++, sedb.getId());
		variables.put(ind++, sedb.getStudyId());
		variables.put(ind++, sedb.getName());
		variables.put(ind++, sedb.getDescription());
		variables.put(ind++, sedb.isRepeating());
		variables.put(ind++, sedb.getType());
		variables.put(ind++, sedb.getCategory());
		variables.put(ind++, sedb.getOwnerId());
		variables.put(ind++, sedb.getStatus().getId());
		variables.put(ind++, sedb.getOrdinal());
		variables.put(ind++, sedb.getOid());
		variables.put(ind++, sedb.getMinDay());
		variables.put(ind++, sedb.getMaxDay());
		variables.put(ind++, sedb.getEmailDay());
		variables.put(ind++, sedb.getScheduleDay());
		variables.put(ind++, sedb.getReferenceVisit());
		variables.put(ind, sedb.getUserEmailId());
		this.execute(digester.getQuery("create"), variables);

		return sedb;
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityBean update(EntityBean eb) {
		int ind = 1;
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) eb;
		HashMap variables = new HashMap();
		variables.put(ind++, sedb.getStudyId());
		variables.put(ind++, sedb.getName());
		variables.put(ind++, sedb.getDescription());
		variables.put(ind++, sedb.isRepeating());
		variables.put(ind++, sedb.getType());
		variables.put(ind++, sedb.getCategory());
		variables.put(ind++, sedb.getStatus().getId());
		variables.put(ind++, sedb.getUpdaterId());
		variables.put(ind++, sedb.getOrdinal());
		variables.put(ind++, sedb.getMinDay());
		variables.put(ind++, sedb.getMaxDay());
		variables.put(ind++, sedb.getEmailDay());
		variables.put(ind++, sedb.getScheduleDay());
		variables.put(ind++, sedb.getReferenceVisit());
		variables.put(ind++, sedb.getUserEmailId());
		variables.put(ind, sedb.getId());
		this.execute(digester.getQuery("update"), variables);
		return eb;
	}

	/**
	 * Updates StudyEventDefinitionBean's status.
	 *
	 * @param studyEventDefinitionBean
	 *            StudyEventDefinitionBean
	 */
	public void updateStatus(StudyEventDefinitionBean studyEventDefinitionBean) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, studyEventDefinitionBean.getStatus().getId());
		variables.put(ind++, studyEventDefinitionBean.getUpdaterId());
		variables.put(ind, studyEventDefinitionBean.getId());
		this.execute(digester.getQuery("updateStatus"), variables);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		StudyEventDefinitionBean eb = new StudyEventDefinitionBean();

		this.setEntityAuditInformation(eb, hm);
		// set dates and ints first, then strings
		// create a sub-function in auditable entity dao that can do this?
		Integer sedId = (Integer) hm.get("study_event_definition_id");
		eb.setId(sedId);

		Integer studyId = (Integer) hm.get("study_id");
		eb.setStudyId(studyId);
		Integer ordinal = (Integer) hm.get("ordinal");
		eb.setOrdinal(ordinal);
		Boolean repeating = (Boolean) hm.get("repeating");
		eb.setRepeating(repeating);

		// below functions changed by get entity audit information functions

		eb.setName((String) hm.get("name"));
		eb.setDescription((String) hm.get("description"));
		eb.setType((String) hm.get("type"));
		eb.setCategory((String) hm.get("category"));
		eb.setOid((String) hm.get("oc_oid"));
		// Clinovo #65 start
		Integer dayMin = (Integer) hm.get("day_min");
		eb.setMinDay((dayMin));
		Integer dayMax = (Integer) hm.get("day_max");
		eb.setMaxDay((dayMax));
		Integer dayEmail = (Integer) hm.get("day_email");
		eb.setEmailDay((dayEmail));
		Integer scheduleDay = (Integer) hm.get("schedule_day");
		eb.setScheduleDay((scheduleDay));
		Boolean referenceVisit = (Boolean) hm.get("reference_visit");
		eb.setReferenceVisit(referenceVisit);
		Integer emailuserId = (Integer) hm.get("email_user_id");
		eb.setUserEmailId(emailuserId);
		// end
		return eb;
	}

	/**
	 * Finds by oid.
	 * 
	 * @param oid
	 *            String
	 * @return StudyEventDefinitionBean
	 */
	public StudyEventDefinitionBean findByOid(String oid) {
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, oid);
		String sql = digester.getQuery("findByOid");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) it.next());
		} else {
			return null;
		}
	}

	/**
	 * Finds by oid and study id - sometimes we have relationships which can't break past the parent study relationship.
	 * This 'covering' allows us to query on both the study and the parent study id. added tbh 10/2008 for 2.5.2.
	 * 
	 * @param oid
	 *            String
	 * @param studyId
	 *            int
	 * @param parentStudyId
	 *            int
	 * @return StudyEventDefinitionBean
	 */
	public StudyEventDefinitionBean findByOidAndStudy(String oid, int studyId, int parentStudyId) {
		StudyEventDefinitionBean studyEventDefinitionBean = this.findByOidAndStudy(oid, studyId);
		if (studyEventDefinitionBean == null) {
			studyEventDefinitionBean = this.findByOidAndStudy(oid, parentStudyId);
		}
		return studyEventDefinitionBean;
	}

	private StudyEventDefinitionBean findByOidAndStudy(String oid, int studyId) {
		setTypesExpected();
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, oid);
		variables.put(ind++, studyId);
		variables.put(ind, studyId);
		String sql = digester.getQuery("findByOidAndStudy");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) it.next());
		} else {
			logger.info("WARNING: cannot find sed bean by oid " + oid + " and study id " + studyId);
			return null;
		}
	}

	/**
	 * @deprecated replaced by {@link #findAllByStudy(int)}
	 */
	@Override
	@Deprecated
	public ArrayList findAllByStudy(StudyBean study) {

		StudyDAO studyDao = new StudyDAO(this.getDs());

		if (study.getParentStudyId() > 0) {
			// If the study has a parent than it is a site, in this case we
			// should get the event definitions of the parent
			StudyBean parentStudy;
			parentStudy = (StudyBean) studyDao.findByPK(study.getParentStudyId());
			return super.findAllByStudy(parentStudy);
		} else {
			return super.findAllByStudy(study);
		}
	}

	/**
	 * Returns all by the study (with no filters on status).
	 *
	 * @param studyId study ID
	 * @return list of event definitions
	 */
	public List<StudyEventDefinitionBean> findAllByStudy(int studyId) {

		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		List<Map<String, Object>> dbRecords = this.select(digester.getQuery("findAllByStudyId"), variables);
		List<StudyEventDefinitionBean> eventDefinitions = new ArrayList<StudyEventDefinitionBean>();
		for (Map<String, Object> record : dbRecords) {
			StudyEventDefinitionBean eb
					= (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap<String, Object>) record);
			eventDefinitions.add(eb);
		}
		return eventDefinitions;
	}

	/**
	 * Finds all available by study.
	 * 
	 * @param study
	 *            StudyBean
	 * @return ArrayList
	 */
	public ArrayList findAllAvailableByStudy(StudyBean study) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();
		HashMap variables = new HashMap();

		if (study.getParentStudyId() > 0) {
			variables.put(1, study.getParentStudyId());
			variables.put(2, study.getParentStudyId());
		} else {
			variables.put(1, study.getId());
			variables.put(2, study.getId());
		}

		ArrayList alist = this.select(digester.getQuery("findAllAvailableByStudy"), variables);

		for (Object anAlist : alist) {
			StudyEventDefinitionBean seb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;
	}

	/**
	 * Gets all available Study Event Definitions that contain CRFs that can be evaluated.
	 * 
	 * @param study
	 *            StudyBean
	 * @return StudyEventDefinitionBean with CRFs which can be evaluated.
	 */
	public List<StudyEventDefinitionBean> findAllAvailableWithEvaluableCRFByStudy(StudyBean study) {
		List<StudyEventDefinitionBean> seds = new ArrayList<StudyEventDefinitionBean>();
		String queryName;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		if (study.isSite()) {
			queryName = "findAllAvailableWithEvaluableCRFBySite";
		} else {
			queryName = "findAllAvailableWithEvaluableCRFByStudy";
		}
		ArrayList alist = this.select(digester.getQuery(queryName), variables);
		for (Object anAlist : alist) {
			StudyEventDefinitionBean seb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			seds.add(seb);
		}
		return seds;
	}

	/**
	 * Returns all in currentStudy with studyEvent.
	 * 
	 * @param currentStudy
	 *            StudyBean
	 * @return ArrayList
	 */
	public ArrayList findAllWithStudyEvent(StudyBean currentStudy) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());

		ArrayList alist = this.select(digester.getQuery("findAllWithStudyEvent"), variables);

		for (Object anAlist : alist) {
			StudyEventDefinitionBean seb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;
	}

	/**
	 * Returns all by crf.
	 * 
	 * @param crf
	 *            CRFBean
	 * @return ArrayList<StudyEventDefinitionBean>
	 */
	public ArrayList<StudyEventDefinitionBean> findAllByCrf(CRFBean crf) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, crf.getId());

		ArrayList alist = this.select(digester.getQuery("findAllByCrf"), variables);

		for (Object anAlist : alist) {
			StudyEventDefinitionBean seb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;
	}

	/**
	 * Returns all by id and ordered by StudyGroupClassId.
	 * 
	 * @param id
	 *            int
	 * @return ArrayList<StudyEventDefinitionBean>
	 */
	public List<StudyEventDefinitionBean> findAllOrderedByStudyGroupClassId(int id) {
		List<StudyEventDefinitionBean> answer = new ArrayList();

		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, id);

		List alist = this.select(digester.getQuery("findAllOrderedByStudyGroupClassId"), variables);

		for (Object anAlist : alist) {
			StudyEventDefinitionBean seb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;
	}

	/**
	 * Returns all available by id and ordered by StudyGroupClassId.
	 * 
	 * @param id
	 *            int
	 * @return ArrayList<StudyEventDefinitionBean>
	 */
	public List<StudyEventDefinitionBean> findAllAvailableAndOrderedByStudyGroupClassId(int id) {
		List<StudyEventDefinitionBean> answer = new ArrayList();

		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, id);

		List alist = this.select(digester.getQuery("findAllAvailableAndOrderedByStudyGroupClassId"), variables);

		for (Object anAlist : alist) {
			StudyEventDefinitionBean seb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}
		return answer;
	}

	/**
	 * Returns all active by StudySubject from active dynamic group and studyId.
	 * 
	 * @param ssb
	 *            StudySubjectBean
	 * @param studyId
	 *            int
	 * @return ArrayList
	 */
	public List findAllActiveBySubjectFromActiveDynGroupAndStudyId(StudySubjectBean ssb, int studyId) {
		List<StudyEventDefinitionBean> defsFromActiveGroup = new ArrayList();
		StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(this.getDs());
		if (ssb.getDynamicGroupClassId() == 0) {
			StudyGroupClassBean sgc = sgcdao.findDefaultByStudyId(studyId);
			if (sgc.getId() > 0) {
				defsFromActiveGroup = findAllActiveOrderedByStudyGroupClassId(sgc.getId());
			}
		} else {
			StudyGroupClassBean sgc = sgcdao.findByPK(ssb.getDynamicGroupClassId());
			if (sgc.getStatus() == Status.AVAILABLE) {
				defsFromActiveGroup = findAllActiveOrderedByStudyGroupClassId(ssb.getDynamicGroupClassId());
			}
		}
		List<StudyEventDefinitionBean> nonGroupDefs = findAllActiveNotClassGroupedAndFromRemovedGroupsByStudyId(studyId);
		defsFromActiveGroup.addAll(nonGroupDefs);

		return defsFromActiveGroup;
	}

	/**
	 * Returns all active by StudySubject and studyId.
	 * 
	 * @param ssb
	 *            StudySubjectBean
	 * @param studyId
	 *            int
	 * @return ArrayList
	 */
	public List findAllActiveBySubjectAndStudyId(StudySubjectBean ssb, int studyId) {
		List<StudyEventDefinitionBean> defsFromGroup = new ArrayList();
		if (ssb.getDynamicGroupClassId() == 0) {
			StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(this.getDs());
			StudyGroupClassBean sgc = sgcdao.findDefaultByStudyId(studyId);
			if (sgc.getId() > 0) {
				defsFromGroup = findAllActiveOrderedByStudyGroupClassId(sgc.getId());
			}
		} else {
			defsFromGroup = findAllActiveOrderedByStudyGroupClassId(ssb.getDynamicGroupClassId());
		}
		List<StudyEventDefinitionBean> nonGroupDefs = findAllActiveNotClassGroupedAndFromRemovedGroupsByStudyId(studyId);
		defsFromGroup.addAll(nonGroupDefs);

		return defsFromGroup;
	}

	/**
	 * Finds all active by id and not class grouped.
	 * 
	 * @param id int
	 * @return ArrayList<StudyEventDefinitionBean>
	 */
	public List<StudyEventDefinitionBean> findAllActiveNotClassGroupedAndFromRemovedGroupsByStudyId(int id) {
		List answer = new ArrayList();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, id);
		List alist = this.select(digester.getQuery("findAllActiveNotClassGroupedAndFromRemovedGroupsByStudyId"), variables);

		for (Object anAlist : alist) {
			StudyEventDefinitionBean seb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}
		return answer;
	}

	/**
	 * Finds all active by id and not class grouped.
	 *
	 * @param id int
	 * @return ArrayList<StudyEventDefinitionBean>
	 */
	public List<StudyEventDefinitionBean> findAllActiveNotClassGroupedByStudyId(int id) {
		List answer = new ArrayList();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, id);
		List alist = this.select(digester.getQuery("findAllActiveNotClassGroupedByStudyId"), variables);

		for (Object anAlist : alist) {
			StudyEventDefinitionBean seb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}
		return answer;
	}

	/**
	 * Finds all active by id and ordered by StudyGroupClassId.
	 * 
	 * @param id
	 *            int
	 * @return ArrayList<StudyEventDefinitionBean>
	 */
	public List<StudyEventDefinitionBean> findAllActiveOrderedByStudyGroupClassId(int id) {
		List<StudyEventDefinitionBean> temp = findAllOrderedByStudyGroupClassId(id);
		List<StudyEventDefinitionBean> answer = new ArrayList();
		for (StudyEventDefinitionBean def : temp) {
			if (def.isActive()) {
				answer.add(def);
			}
		}
		return answer;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection findAll() {
		this.setTypesExpected();
		List alist = this.select(digester.getQuery("findAll"));
		List al = new ArrayList();
		for (Object anAlist : alist) {
			StudyEventDefinitionBean eb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityBean findByPK(int id) {

		StudyEventDefinitionBean eb = new StudyEventDefinitionBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");

		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * Finds by name.
	 * 
	 * @param name
	 *            String
	 * @return EntityBean
	 */
	public EntityBean findByName(String name) {

		StudyEventDefinitionBean eb = new StudyEventDefinitionBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, name);

		String sql = digester.getQuery("findByName");

		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	/**
	 * @param eventDefinitionCRFId
	 *            The id of an event definition crf.
	 * @return the study event definition bean for the specified event definition crf.
	 */
	public StudyEventDefinitionBean findByEventDefinitionCRFId(int eventDefinitionCRFId) {
		StudyEventDefinitionBean answer = new StudyEventDefinitionBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, eventDefinitionCRFId);

		String sql = digester.getQuery("findByEventDefinitionCRFId");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			answer = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return answer;
	}

	/**
	 * returns all active by ParentStudyId.
	 * 
	 * @param parentStudyId
	 *            int
	 * @return ArrayList<StudyEventDefinitionBean>
	 */
	public List<StudyEventDefinitionBean> findAllActiveByParentStudyId(int parentStudyId) {
		return findAllActiveByParentStudyId(parentStudyId, false);
	}

	/**
	 * Returns all active by ParentStudyId and filterOnCalendar.
	 * 
	 * @param parentStudyId
	 *            int
	 * @param filterOnCalendar
	 *            boolean
	 * @return ArrayList<StudyEventDefinitionBean>
	 */
	public List<StudyEventDefinitionBean> findAllActiveByParentStudyId(int parentStudyId, boolean filterOnCalendar) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, parentStudyId);
		List alist = this.select(digester.getQuery("findAllActiveByParentStudyId"), variables);
		List<StudyEventDefinitionBean> al = new ArrayList<StudyEventDefinitionBean>();
		for (Object anAlist : alist) {
			StudyEventDefinitionBean eb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			if (filterOnCalendar && eb.getType().equalsIgnoreCase("calendared_visit")) {
				// al.add(eb);
				logger.trace("found calendared visit");
			} else {
				al.add(eb);
			}
		}
		return al;
	}

	/**
	 * Gets all active study event definitions ordered by dynamic group and study event definition.
	 *
	 * @param parentStudyId
	 *            Study ID
	 * @return List of study even definitions
	 */
	public ArrayList<StudyEventDefinitionBean> findAllActiveByParentStudyIdOrderedByGroupClass(int parentStudyId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, parentStudyId);
		ArrayList alist = this.select(digester.getQuery("findAllActiveByParentStudyIdOrderedByGroupClass"), variables);
		ArrayList<StudyEventDefinitionBean> seds = new ArrayList<StudyEventDefinitionBean>();
		for (Object anAlist : alist) {
			seds.add((StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist));
		}
		return seds;
	}

	/**
	 * Returns all StudyEventDefinitionBeans by parentStudyId and idsToHide.
	 * 
	 * @param parentStudyId
	 *            int
	 * @param idsToHide
	 *            ArrayList<Integer>
	 * @return ArrayList<StudyEventDefinitionBean>
	 */
	public ArrayList<StudyEventDefinitionBean> findAllActiveByParentStudyId(int parentStudyId,
			List<Integer> idsToHide) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, parentStudyId);
		ArrayList alist = this.select(digester.getQuery("findAllActiveByParentStudyId"), variables);
		ArrayList<StudyEventDefinitionBean> al = new ArrayList<StudyEventDefinitionBean>();
		for (Object anAlist : alist) {
			StudyEventDefinitionBean eb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			if (idsToHide.contains(Integer.valueOf(eb.getId()))) {
				// al.add(eb);
				logger.trace("found visit contained in dynamic group, not adding");
			} else {
				al.add(eb);
			}
		}
		return al;
	}

	/**
	 * Returns referenced StudyEventDefinitionBeans.
	 * 
	 * @return ArrayList<StudyEventDefinitionBean>
	 */
	public ArrayList<StudyEventDefinitionBean> findReferenceVisitBeans() {
		this.setTypesExpected();
		ArrayList alist = this.select(digester.getQuery("findReferenceVisitBeans"));
		ArrayList<StudyEventDefinitionBean> al = new ArrayList<StudyEventDefinitionBean>();
		for (Object anAlist : alist) {
			StudyEventDefinitionBean eb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Returns event names from study.
	 * 
	 * @param studyId
	 *            int
	 * @return List<String>
	 */
	public List<String> getEventNamesFromStudy(int studyId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.STRING);
		this.setTypeExpected(2, TypeNames.BOOL);

		HashMap variables = new HashMap();
		variables.put(1, studyId);

		ArrayList alist = this.select(digester.getQuery("getEventNamesFromStudy"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap h = (HashMap) anAlist;
			al.add(h.get("name"));
		}
		return al;
	}

	/**
	 * Returns all active StudyEventDefinitionBeans by StudyId and CRFId.
	 * 
	 * @param crfId
	 *            int
	 * @param studyId
	 *            int
	 * @return List<StudyEventDefinitionBean>
	 */
	public List<StudyEventDefinitionBean> findAllActiveByStudyIdAndCRFId(int crfId, int studyId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, crfId);
		ArrayList alist = this.select(digester.getQuery("findAllActiveByStudyIdAndCRFId"), variables);
		ArrayList<StudyEventDefinitionBean> al = new ArrayList<StudyEventDefinitionBean>();
		for (Object anAlist : alist) {
			StudyEventDefinitionBean eb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}

		return al;
	}

	/**
	 * Check if at least one calendared event is present in the study.
	 * @param parentStudyId int
	 * @return boolean
	 */
	public boolean isAnyCalendaredEventExist(int parentStudyId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, parentStudyId);
		ArrayList alist = this.select(digester.getQuery("findAllActiveCalendaredEventsByStudyId"), variables);
		
		return alist.size() > 0;
	}

	/**
	 * Delete StudyEventDefinition.
	 * @param eventId event definition Id
	 */
	public void deleteEventDefinition(int eventId) {
		HashMap variables = new HashMap();
		variables.put(1, eventId);

		this.execute(digester.getQuery("deleteEventDefinition"), variables);
	}
}
