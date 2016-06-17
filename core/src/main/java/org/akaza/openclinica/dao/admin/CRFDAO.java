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
package org.akaza.openclinica.dao.admin;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

import com.clinovo.enums.study.StudyOrigin;

/**
 * The data access object for instruments in the database.
 * 
 * @author thickerson
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CRFDAO extends AuditableEntityDAO {

	private static String dbType = CoreResources.getDBType();

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_CRF;
	}

	/**
	 * CRFDAO constructor.
	 * 
	 * @param ds
	 *            DataSource
	 */
	public CRFDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	/**
	 * CRFDAO constructor.
	 *
	 * @param ds
	 *            DataSource
	 * @param digester
	 *            DAODigester
	 */
	public CRFDAO(DataSource ds, DAODigester digester) {
		super(ds);
		setQueryNames();
		this.digester = digester;
	}

	private String getDBType() {
		return dbType;
	}

	private void setQueryNames() {
		getCurrentPKName = "getCurrentPK";
		getNextPKName = "getNextPK";
	}

	/**
	 * Method that updates EntityBean in the DB.
	 * 
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean update(EntityBean eb) {
		if (eb instanceof CRFBean) {
			int index = 1;
			CRFBean cb = (CRFBean) eb;
			HashMap variables = new HashMap();
			variables.put(index++, cb.getStatus().getId());
			variables.put(index++, cb.getName());
			variables.put(index++, cb.getDescription());
			variables.put(index++, cb.getUpdater().getId());
			variables.put(index++, cb.getSource());
			variables.put(index, cb.getId());
			execute(digester.getQuery("update"), variables);
		}
		return eb;
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityBean create(EntityBean eb) {
		return create(eb, null);
	}

	/**
	 * Creates new CRFBean.
	 *
	 * @param eb
	 *            EntityBean
	 * @param con
	 *            Connection
	 * @return EntityBean
	 */
	public EntityBean create(EntityBean eb, Connection con) {
		if (eb instanceof CRFBean) {
			int index = 1;
			CRFBean cb = (CRFBean) eb;
			cb.setOid(getValidOid(cb, cb.getName()));
			HashMap variables = new HashMap();
			variables.put(index++, cb.getStatus().getId());
			variables.put(index++, cb.getName());
			variables.put(index++, cb.getDescription());
			variables.put(index++, cb.getOwner().getId());
			variables.put(index++, cb.getOid());
			variables.put(index++, cb.getStudyId());
			// set auto_layout property
			if (getDBType().equals("oracle")) {
				variables.put(index++, 1);
			} else {
				variables.put(index++, Boolean.TRUE);
			}
			variables.put(index, cb.getSource());
			executeWithPK(digester.getQuery("create"), variables, null, con);
			if (isQuerySuccessful()) {
				cb.setId(getLatestPK());
			}
		}
		return eb;
	}

	/**
	 * Method that sets expected types.
	 */
	@Override
	public void setTypesExpected() {
		int index = 1;
		unsetTypeExpected();
		setTypeExpected(index++, TypeNames.INT);
		setTypeExpected(index++, TypeNames.INT);
		setTypeExpected(index++, TypeNames.STRING);
		setTypeExpected(index++, TypeNames.STRING);
		setTypeExpected(index++, TypeNames.INT);
		setTypeExpected(index++, TypeNames.TIMESTAMP);
		setTypeExpected(index++, TypeNames.TIMESTAMP);
		setTypeExpected(index++, TypeNames.INT);
		setTypeExpected(index++, TypeNames.STRING);
		setTypeExpected(index++, TypeNames.INT);
		// set type for auto_layout property
		if (getDBType().equals("oracle")) {
			setTypeExpected(index++, TypeNames.INT);
		} else {
			setTypeExpected(index++, TypeNames.BOOL);
		}
		setTypeExpected(index, TypeNames.STRING);
	}

	/**
	 * Method builds CRFBean from HashMap data.
	 * 
	 * @param hm
	 *            HashMap
	 * @return CRFBean
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		CRFBean eb = new CRFBean();
		setEntityAuditInformation(eb, hm);
		eb.setId((Integer) hm.get("crf_id"));
		eb.setName((String) hm.get("name"));
		eb.setDescription((String) hm.get("description"));
		eb.setSource((String) hm.get("source"));
		eb.setOid((String) hm.get("oc_oid"));
		eb.setStudyId((Integer) hm.get("source_study_id"));
		// get auto_layout property
		if (getDBType().equals("oracle")) {
			eb.setAutoLayout(((Integer) hm.get("auto_layout")) == 1);
		} else {
			eb.setAutoLayout((Boolean) hm.get("auto_layout"));
		}

		return eb;
	}

	/**
	 * Method that finds crf by id.
	 *
	 * @param id
	 *            int
	 * @return EntityBean
	 */
	public EntityBean findByPK(int id) {
		CRFBean eb = new CRFBean();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, id);

		ArrayList alist = select(digester.getQuery("findByPK"), variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (CRFBean) getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * Method that returns crf by oid.
	 *
	 * @param oid
	 *            String
	 * @return CRFBean
	 */
	public CRFBean findByOid(String oid) {
		unsetTypeExpected();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, oid);
		String sql = digester.getQuery("findByOID");

		ArrayList rows = select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (CRFBean) getEntityFromHashMap((HashMap) it.next());
		} else {
			return null;
		}
	}

	/**
	 * Method that finds crf by item oid.
	 *
	 * @param itemOid
	 *            String
	 * @return CRFBean
	 */
	public CRFBean findByItemOid(String itemOid) {
		CRFBean eb = new CRFBean();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, itemOid);

		ArrayList alist = select(digester.getQuery("findByItemOid"), variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (CRFBean) getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * Method that finds crf by name and study.
	 *
	 * @param name
	 *            String
	 * @param studyBean
	 *            StudyBean
	 * @return CRFBean
	 */
	public EntityBean findByNameAndStudy(String name, StudyBean studyBean) {
		String sql;
		int index = 1;
		setTypesExpected();
		CRFBean crfBean = new CRFBean();

		HashMap variables = new HashMap();
		variables.put(index++, name.trim());

		if (studyBean.getOrigin().equalsIgnoreCase(StudyOrigin.STUDIO.getName())) {
			sql = digester.getQuery("findByNameAndStudy");
			variables.put(index, studyBean.getParentStudyId() > 0 ? studyBean.getParentStudyId() : studyBean.getId());
		} else {
			sql = digester.getQuery("findByNameInGUIStudies");
		}

		List<HashMap> mapList = select(sql, variables);
		Iterator<HashMap> mapIterator = mapList.iterator();
		if (mapIterator.hasNext()) {
			crfBean = (CRFBean) getEntityFromHashMap(mapIterator.next());
		}
		return crfBean;
	}

	/**
	 * Method that finds crf by name that does not have the certain id.
	 *
	 * @param crfId
	 *            int
	 * @param name
	 *            String
	 * @param studyBean
	 *            StudyBean
	 * @return CRFBean
	 */
	public EntityBean findAnotherByName(String name, int crfId, StudyBean studyBean) {
		String sql;
		int index = 1;
		setTypesExpected();
		CRFBean crfBean = new CRFBean();
		HashMap variables = new HashMap();

		variables.put(index++, name);
		variables.put(index++, crfId);

		if (studyBean.getOrigin().equalsIgnoreCase(StudyOrigin.STUDIO.getName())) {
			sql = digester.getQuery("findAnotherByNameInStudy");
			variables.put(index, studyBean.getParentStudyId() > 0 ? studyBean.getParentStudyId() : studyBean.getId());
		} else {
			sql = digester.getQuery("findAnotherByNameInGUIStudies");
		}

		List<HashMap> mapList = select(sql, variables);
		Iterator<HashMap> mapIterator = mapList.iterator();
		if (mapIterator.hasNext()) {
			crfBean = (CRFBean) getEntityFromHashMap(mapIterator.next());
		}
		return crfBean;
	}

	/**
	 * Method that find crf by crfVersionId.
	 *
	 * @param crfVersionId
	 *            int
	 * @return CRFBean
	 */
	public CRFBean findByVersionId(int crfVersionId) {
		CRFBean answer = new CRFBean();

		unsetTypeExpected();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);

		String sql = digester.getQuery("findByVersionId");
		ArrayList rows = select(sql, variables);

		if (rows.size() > 0) {
			HashMap row = (HashMap) rows.get(0);
			answer = (CRFBean) getEntityFromHashMap(row);
		}

		return answer;
	}

	/**
	 * Method returns count of active crfs.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @return Integer
	 */
	public Integer getCountOfActiveCRFs(StudyBean studyBean) {
		String sql;
		int index = 1;
		setTypesExpected();
		Integer result = null;
		HashMap variables = new HashMap();
		if (studyBean.getOrigin().equalsIgnoreCase(StudyOrigin.STUDIO.getName())) {
			sql = digester.getQuery("getCountOfActiveCRFsInStudy");
			variables.put(index, studyBean.getParentStudyId() > 0 ? studyBean.getParentStudyId() : studyBean.getId());
		} else {
			sql = digester.getQuery("getCountOfActiveCRFsInGUIStudies");
		}
		List<HashMap> mapList = select(sql, variables);
		Iterator<HashMap> iterator = mapList.iterator();
		if (iterator.hasNext()) {
			result = (Integer) iterator.next().get("count");
		}
		return result;
	}

	/**
	 * Method that finds all CRFs by study.
	 * 
	 * @param studyBean
	 *            StudyBean
	 * @return List<CRFBean>
	 */
	public List<CRFBean> findAllCRFs(StudyBean studyBean) {
		return findAllCRFsByLimit(studyBean, false);
	}

	/**
	 * Method that finds all CRFs by study.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @param hasLimit
	 *            boolean
	 * @return List<CRFBean>
	 */
	public List<CRFBean> findAllCRFsByLimit(StudyBean studyBean, boolean hasLimit) {
		String sql;
		int index = 1;
		setTypesExpected();
		HashMap variables = new HashMap();
		List<CRFBean> result = new ArrayList<CRFBean>();
		if (studyBean.getOrigin().equalsIgnoreCase(StudyOrigin.STUDIO.getName())) {
			sql = digester.getQuery("findAllCRFsInStudy");
			variables.put(index, studyBean.getParentStudyId() > 0 ? studyBean.getParentStudyId() : studyBean.getId());
		} else {
			sql = digester.getQuery("findAllCRFsInGUIStudies");
		}
		List<HashMap> mapList = select(sql.concat(hasLimit ? " limit 5" : ""), variables);
		for (HashMap aMapList : mapList) {
			result.add((CRFBean) getEntityFromHashMap(aMapList));
		}
		return result;
	}

	/**
	 * Method that finds all active crfs.
	 * 
	 * @param studyBean
	 *            StudyBean
	 * @return List<CRFBean>
	 */
	public List<CRFBean> findAllActiveCRFs(StudyBean studyBean) {
		String sql;
		int index = 1;
		setTypesExpected();
		HashMap variables = new HashMap();
		List<CRFBean> result = new ArrayList<CRFBean>();
		if (studyBean.getOrigin().equalsIgnoreCase(StudyOrigin.STUDIO.getName())) {
			sql = digester.getQuery("findAllActiveCRFsInStudy");
			variables.put(index, studyBean.getParentStudyId() > 0 ? studyBean.getParentStudyId() : studyBean.getId());
		} else {
			sql = digester.getQuery("findAllActiveCRFsInGUIStudies");
		}
		List<HashMap> mapList = select(sql, variables);
		for (HashMap aMapList : mapList) {
			result.add((CRFBean) getEntityFromHashMap(aMapList));
		}
		return result;
	}

	/**
	 * Method that returns all active crfs by StudyEventDefinitionBean.
	 *
	 * @param definition
	 *            StudyEventDefinitionBean
	 * @return Collection
	 */
	public Collection findAllActiveByDefinition(StudyEventDefinitionBean definition) {
		setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, definition.getId());
		ArrayList alist = select(digester.getQuery("findAllActiveByDefinition"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			CRFBean eb = (CRFBean) getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that returns all active crfs by definitions in the current study.
	 *
	 * @param studyId
	 *            int
	 * @return Collection
	 */
	public Collection findAllActiveByDefinitionsForCurrentStudy(int studyId) {
		setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		ArrayList alist = select(digester.getQuery("findAllActiveByDefinitionsForCurrentStudy"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			CRFBean eb = (CRFBean) getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that returns crfs that are available for evaluation.
	 *
	 * @param currentStudyId
	 *            int
	 * @return CRFBean list
	 */
	public List<CRFBean> findAllEvaluableCrfs(int currentStudyId) {
		int index = 1;
		List result = new ArrayList<CRFBean>();
		setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, currentStudyId);
		variables.put(index, currentStudyId);
		ArrayList objList = select(digester.getQuery("findAllEvaluableCrfs"), variables);
		for (Object object : objList) {
			result.add(getEntityFromHashMap((HashMap) object));
		}
		return result;
	}

	/**
	 * Method returns all unmasked available CRFs.
	 *
	 * @param sed
	 *            StudyEventDefinitionBean
	 * @param ub
	 *            UserAccountBean
	 * @return List<CRFBean>
	 */
	public List<CRFBean> findAllActiveUnmaskedByDefinition(StudyEventDefinitionBean sed, UserAccountBean ub) {
		setTypesExpected();
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, sed.getId());
		variables.put(index++, ub.getId());
		variables.put(index++, ub.getActiveStudyId());
		variables.put(index, ub.getActiveStudyId());
		ArrayList alist = select(digester.getQuery("findAllActiveUnmaskedByDefinition"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			CRFBean eb = (CRFBean) getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Be careful to use this method because we need to provide different set of CRF Versions depend on origin of
	 * current study.
	 */
	public List<CRFBean> findAll() {
		setTypesExpected();
		List<CRFBean> result = new ArrayList<CRFBean>();
		List<HashMap> mapList = select(digester.getQuery("findAll"));
		for (HashMap aMapList : mapList) {
			result.add((CRFBean) getEntityFromHashMap(aMapList));
		}
		return result;
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
	 * Method that deletes crf by crfId. Note: it will be better to use the DeleteCrfService.deleteCrf(int crfId)
	 * instead of this method, cuz service may have additional logic.
	 *
	 * @param crfId
	 *            int
	 */
	public void deleteCrf(int crfId) {
		final int max = 16;
		String sql = digester.getQuery("deleteCrf");
		HashMap variables = new HashMap();
		for (int i = 1; i <= max; i++) {
			variables.put(i, crfId);
		}
		execute(sql, variables);
	}

	/**
	 * Method returns valid crf oid by crf bean and crfName.
	 *
	 * @param crfBean
	 *            CRFBean
	 * @param crfName
	 *            String
	 * @return String
	 */
	private String getValidOid(CRFBean crfBean, String crfName) {

		String oid = getOid(crfBean, crfName);
		logger.info(oid);
		String oidPreRandomization = oid;
		while (findAllByOid(oid).size() > 0) {
			oid = crfBean.getOidGenerator(ds).randomizeOid(oidPreRandomization);
		}
		return oid;
	}

	/**
	 * Method returns next crf oid by crf bean and crfName.
	 *
	 * @param crfBean
	 *            CRFBean
	 * @param crfName
	 *            String
	 * @return String
	 */
	private String getOid(CRFBean crfBean, String crfName) {

		String oid;
		try {
			oid = crfBean.getOid() != null ? crfBean.getOid() : crfBean.getOidGenerator(ds).generateOid(crfName);
			return oid;
		} catch (Exception e) {
			throw new RuntimeException("CANNOT GENERATE OID");
		}
	}

	/**
	 * Method that returns list of crfs by oid.
	 *
	 * @param oid
	 *            String
	 * @return ArrayList<CRFBean>
	 */
	private ArrayList<CRFBean> findAllByOid(String oid) {
		HashMap<Integer, String> variables = new HashMap<Integer, String>();
		variables.put(1, oid);

		return executeFindAllQuery("findByOID", variables);
	}
}
