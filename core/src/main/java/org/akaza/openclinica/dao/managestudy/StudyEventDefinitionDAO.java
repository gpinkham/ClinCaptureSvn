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

@SuppressWarnings({ "rawtypes", "unchecked" })
public class StudyEventDefinitionDAO extends AuditableEntityDAO {

	private void setQueryNames() {
		findAllByStudyName = "findAllByStudy";
		findAllActiveByStudyName = "findAllActiveByStudy";
		findByPKAndStudyName = "findByPKAndStudy";
	}

	public StudyEventDefinitionDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	public StudyEventDefinitionDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
		setQueryNames();
	}

	// This constructor sets up the Locale for JUnit tests; see the locale
	// member variable in EntityDAO, and its initializeI18nStrings() method
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
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.STRING);
		this.setTypeExpected(5, TypeNames.BOOL);
		this.setTypeExpected(6, TypeNames.STRING);
		this.setTypeExpected(7, TypeNames.STRING);
		// int int date date int
		this.setTypeExpected(8, TypeNames.INT);
		this.setTypeExpected(9, TypeNames.INT);
		this.setTypeExpected(10, TypeNames.DATE);
		this.setTypeExpected(11, TypeNames.DATE);
		this.setTypeExpected(12, TypeNames.INT);
		this.setTypeExpected(13, TypeNames.INT);
		this.setTypeExpected(14, TypeNames.STRING);
		// Clinovo Ticket #65
		this.setTypeExpected(15, TypeNames.INT);
		this.setTypeExpected(16, TypeNames.INT);
		this.setTypeExpected(17, TypeNames.INT);
		this.setTypeExpected(18, TypeNames.INT);
		this.setTypeExpected(19, TypeNames.BOOL);
		this.setTypeExpected(20, TypeNames.INT);
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

	public EntityBean create(EntityBean eb) {
		// study_event_definition_id ,
		// STUDY_ID, NAME,DESCRIPTION, REPEATING, TYPE, CATEGORY, OWNER_ID,
		// STATUS_ID, DATE_CREATED,ordinal,oid
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) eb;
		sedb.setId(this.findNextKey());
		logger.info("***id:" + sedb.getId());
		HashMap variables = new HashMap();
		variables.put(1, sedb.getId());
		variables.put(2, sedb.getStudyId());
		variables.put(3, sedb.getName());
		variables.put(4, sedb.getDescription());
		variables.put(5, sedb.isRepeating());
		variables.put(6, sedb.getType());
		variables.put(7, sedb.getCategory());
		variables.put(8, sedb.getOwnerId());
		variables.put(9, sedb.getStatus().getId());
		variables.put(10, sedb.getOrdinal());
		variables.put(11, getValidOid(sedb));
		//
		variables.put(12, sedb.getMinDay());
		variables.put(13, sedb.getMaxDay());
		variables.put(14, sedb.getEmailDay());
		variables.put(15, sedb.getScheduleDay());
		variables.put(16, sedb.getReferenceVisit());
		variables.put(17, sedb.getUserEmailId());
		this.execute(digester.getQuery("create"), variables);

		return sedb;
	}

	public EntityBean update(EntityBean eb) {
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) eb;
		HashMap variables = new HashMap();
		variables.put(1, sedb.getStudyId());
		variables.put(2, sedb.getName());
		variables.put(3, sedb.getDescription());
		variables.put(4, sedb.isRepeating());
		variables.put(5, sedb.getType());
		variables.put(6, sedb.getCategory());
		variables.put(7, sedb.getStatus().getId());
		variables.put(8, sedb.getUpdaterId());
		variables.put(9, sedb.getOrdinal());
		variables.put(10, sedb.getMinDay());
		variables.put(11, sedb.getMaxDay());
		variables.put(12, sedb.getEmailDay());
		variables.put(13, sedb.getScheduleDay());
		variables.put(14, sedb.getReferenceVisit());
		variables.put(15, sedb.getUserEmailId());
		variables.put(16, sedb.getId());
		this.execute(digester.getQuery("update"), variables);
		return eb;
	}

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

	/*
	 * find by oid and study id - sometimes we have relationships which can't break past the parent study relationship.
	 * This 'covering' allows us to query on both the study and the parent study id. added tbh 10/2008 for 2.5.2
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

		HashMap variables = new HashMap();
		variables.put(1, oid);
		variables.put(2, studyId);
		variables.put(3, studyId);
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

	@Override
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

	public ArrayList<StudyEventDefinitionBean> findAllOrderedByStudyGroupClassId(int id) {
		ArrayList<StudyEventDefinitionBean> answer = new ArrayList();

		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, id);

		ArrayList alist = this.select(digester.getQuery("findAllOrderedByStudyGroupClassId"), variables);

		for (Object anAlist : alist) {
			StudyEventDefinitionBean seb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;
	}

	public ArrayList<StudyEventDefinitionBean> findAllAvailableAndOrderedByStudyGroupClassId(int id) {
		ArrayList<StudyEventDefinitionBean> answer = new ArrayList();

		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, id);

		ArrayList alist = this.select(digester.getQuery("findAllAvailableAndOrderedByStudyGroupClassId"), variables);

		for (Object anAlist : alist) {
			StudyEventDefinitionBean seb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;
	}

	public ArrayList findAllActiveBySubjectFromActiveDynGroupAndStudyId(StudySubjectBean ssb, int studyId) {
		ArrayList<StudyEventDefinitionBean> defsFromActiveGroup = new ArrayList();
		StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(this.getDs());
		if (ssb.getDynamicGroupClassId() == 0) {
			StudyGroupClassBean sgc = (StudyGroupClassBean) sgcdao.findDefaultByStudyId(studyId);
			if (sgc.getId() > 0) {
				defsFromActiveGroup = findAllActiveOrderedByStudyGroupClassId(sgc.getId());
			}
		} else {
			StudyGroupClassBean sgc = (StudyGroupClassBean) sgcdao.findByPK(ssb.getDynamicGroupClassId());
			if (sgc.getStatus() == Status.AVAILABLE) {
				defsFromActiveGroup = findAllActiveOrderedByStudyGroupClassId(ssb.getDynamicGroupClassId());
			}
		}
		ArrayList<StudyEventDefinitionBean> nonGroupDefs = findAllActiveNotClassGroupedByStudyId(studyId);
		defsFromActiveGroup.addAll(nonGroupDefs);

		return defsFromActiveGroup;
	}

	public ArrayList findAllActiveBySubjectAndStudyId(StudySubjectBean ssb, int studyId) {
		ArrayList<StudyEventDefinitionBean> defsFromGroup = new ArrayList();
		if (ssb.getDynamicGroupClassId() == 0) {
			StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(this.getDs());
			StudyGroupClassBean sgc = (StudyGroupClassBean) sgcdao.findDefaultByStudyId(studyId);
			if (sgc.getId() > 0) {
				defsFromGroup = findAllActiveOrderedByStudyGroupClassId(sgc.getId());
			}
		} else {
			defsFromGroup = findAllActiveOrderedByStudyGroupClassId(ssb.getDynamicGroupClassId());
		}
		ArrayList<StudyEventDefinitionBean> nonGroupDefs = findAllActiveNotClassGroupedByStudyId(studyId);
		defsFromGroup.addAll(nonGroupDefs);

		return defsFromGroup;
	}

	public ArrayList<StudyEventDefinitionBean> findAllActiveNotClassGroupedByStudyId(int id) {
		ArrayList answer = new ArrayList();

		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, id);

		ArrayList alist = this.select(digester.getQuery("findAllActiveNotClassGroupedByStudyId"), variables);

		for (Object anAlist : alist) {
			StudyEventDefinitionBean seb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(seb);
		}

		return answer;
	}

	public ArrayList<StudyEventDefinitionBean> findAllActiveOrderedByStudyGroupClassId(int id) {
		ArrayList<StudyEventDefinitionBean> temp = findAllOrderedByStudyGroupClassId(id);
		ArrayList<StudyEventDefinitionBean> answer = new ArrayList();
		for (StudyEventDefinitionBean def : temp) {
			if (def.isActive()) {
				answer.add(def);
			}
		}
		return answer;
	}

	public Collection findAll() {
		this.setTypesExpected();
		ArrayList alist = this.select(digester.getQuery("findAll"));
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyEventDefinitionBean eb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

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

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

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

	public Collection findAllByStudyAndLimit(int studyId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);
		ArrayList alist = this.select(digester.getQuery("findAllByStudyAndLimit"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			StudyEventDefinitionBean eb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;

	}

	public ArrayList<StudyEventDefinitionBean> findAllActiveByParentStudyId(int parentStudyId) {
		return findAllActiveByParentStudyId(parentStudyId, false);
	}

	public ArrayList<StudyEventDefinitionBean> findAllActiveByParentStudyId(int parentStudyId, boolean filterOnCalendar) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, parentStudyId);
		ArrayList alist = this.select(digester.getQuery("findAllActiveByParentStudyId"), variables);
		ArrayList<StudyEventDefinitionBean> al = new ArrayList<StudyEventDefinitionBean>();
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

	public ArrayList<StudyEventDefinitionBean> findAllActiveByParentStudyId(int parentStudyId,
			ArrayList<Integer> idsToHide) {
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
}
