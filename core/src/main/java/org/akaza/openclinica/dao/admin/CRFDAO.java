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
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

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
	 * Method that sets expected types.
	 */
	@Override
	public void setTypesExpected() {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		// set type for auto_layout property
		if (getDBType().equals("oracle")) {
			this.setTypeExpected(index, TypeNames.INT);
		} else {
			this.setTypeExpected(index, TypeNames.BOOL);
		}
	}

	/**
	 * Method that updates EntityBean in the DB.
	 * 
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean update(EntityBean eb) {
		int index = 1;
		CRFBean cb = (CRFBean) eb;
		HashMap variables = new HashMap();
		variables.put(index++, cb.getStatus().getId());
		variables.put(index++, cb.getName());
		variables.put(index++, cb.getDescription());
		variables.put(index++, cb.getUpdater().getId());
		variables.put(index, cb.getId());
		this.execute(digester.getQuery("update"), variables);
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
			variables.put(index, 1);
		} else {
			variables.put(index, Boolean.TRUE);
		}
		executeWithPK(digester.getQuery("create"), variables, null, con);
		if (isQuerySuccessful()) {
			cb.setId(getLatestPK());
		}
		return cb;
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
		this.setEntityAuditInformation(eb, hm);
		eb.setId((Integer) hm.get("crf_id"));
		eb.setName((String) hm.get("name"));
		eb.setDescription((String) hm.get("description"));
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
	 * Method that returns all crfs.
	 * 
	 * @return Collection
	 */
	public Collection findAll() {
		return findAllByLimit(false);
	}

	/**
	 * Method returns count of active crfs.
	 * 
	 * @return Integer
	 */
	public Integer getCountofActiveCRFs() {
		setTypesExpected();
		String sql = digester.getQuery("getCountofCRFs");
		ArrayList rows = this.select(sql);
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Method that finds crfs by studyId.
	 * 
	 * @param studyId
	 *            int
	 * @return Collection
	 */
	public Collection findAllByStudy(int studyId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		ArrayList alist = this.select(digester.getQuery("findAllByStudy"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			CRFBean eb = (CRFBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that finds crfs using limit flag.
	 * 
	 * @param hasLimit
	 *            boolean
	 * @return Collection
	 */
	public Collection findAllByLimit(boolean hasLimit) {
		this.setTypesExpected();
		ArrayList alist;
		if (hasLimit) {
			alist = this.select(digester.getQuery("findAllByLimit"));
		} else {
			alist = this.select(digester.getQuery("findAll"));
		}
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			CRFBean eb = (CRFBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that finds all active crfs.
	 * 
	 * @return List<CRFBean>
	 */
	public List<CRFBean> findAllActiveCrfs() {
		List result = new ArrayList<CRFBean>();
		this.setTypesExpected();
		ArrayList objList = select(digester.getQuery("findAllActiveCrfs"), new HashMap());
		for (Object object : objList) {
			result.add(getEntityFromHashMap((HashMap) object));
		}
		return result;
	}

	/**
	 * Method that returns crfs by status.
	 * 
	 * @param status
	 *            Status
	 * @return List<CRFBean>
	 */
	public Collection findAllByStatus(Status status) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, status.getId());
		ArrayList alist = this.select(digester.getQuery("findAllByStatus"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			CRFBean eb = (CRFBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that returns all active crfs by StudyEventDefinitionBean.
	 *
	 * @param definition
	 *            StudyEventDefinitionBean
	 * @return Collection
	 */
	public Collection findAllActiveByDefinition(StudyEventDefinitionBean definition) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, definition.getId());
		ArrayList alist = this.select(digester.getQuery("findAllActiveByDefinition"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			CRFBean eb = (CRFBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that returns all active crfs by definitions in the certain studyId.
	 *
	 * @param studyId
	 *            int
	 * @return Collection
	 */
	public Collection findAllActiveByDefinitions(int studyId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);
		ArrayList alist = this.select(digester.getQuery("findAllActiveByDefinitions"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			CRFBean eb = (CRFBean) this.getEntityFromHashMap((HashMap) anAlist);
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
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		ArrayList alist = this.select(digester.getQuery("findAllActiveByDefinitionsForCurrentStudy"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			CRFBean eb = (CRFBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * Method that finds all crfs.
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
	 * Method that finds crf by id.
	 * 
	 * @param id
	 *            int
	 * @return EntityBean
	 */
	public EntityBean findByPK(int id) {
		CRFBean eb = new CRFBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, id);

		ArrayList alist = this.select(digester.getQuery("findByPK"), variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
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
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, itemOid);

		ArrayList alist = this.select(digester.getQuery("findByItemOid"), variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * Method that finds crf by name.
	 *
	 * @param name
	 *            String
	 * @return CRFBean
	 */
	public EntityBean findByName(String name) {
		CRFBean eb = new CRFBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, name);

		String sql = digester.getQuery("findByName");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * Method that finds crf by name that does not have the certain id.
	 *
	 * @param crfId
	 *            int
	 * @param name
	 *            String
	 * @return CRFBean
	 */
	public EntityBean findAnotherByName(String name, int crfId) {
		CRFBean eb = new CRFBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, name);
		variables.put(2, crfId);

		String sql = digester.getQuery("findAnotherByName");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * Method that finds crf by permissions.
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
	 * Method that finds crf by permissions.
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
	 * Method that find crf by crfVersionId.
	 * 
	 * @param crfVersionId
	 *            int
	 * @return CRFBean
	 */
	public CRFBean findByVersionId(int crfVersionId) {
		CRFBean answer = new CRFBean();

		this.unsetTypeExpected();
		this.setTypesExpected();

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
	 * Method returns valid crf oid by crf bean and crfName.
	 * 
	 * @param crfBean
	 *            CRFBean
	 * @param crfName
	 *            String
	 * @return String
	 */
	public String getValidOid(CRFBean crfBean, String crfName) {

		String oid = getOid(crfBean, crfName);
		logger.info(oid);
		String oidPreRandomization = oid;
		while (findAllByOid(oid).size() > 0) {
			oid = crfBean.getOidGenerator(ds).randomizeOid(oidPreRandomization);
		}
		return oid;
	}

	/**
	 * Method that returns list of crfs by oid.
	 * 
	 * @param oid
	 *            String
	 * @return ArrayList<CRFBean>
	 */
	public ArrayList<CRFBean> findAllByOid(String oid) {
		HashMap<Integer, String> variables = new HashMap<Integer, String>();
		variables.put(1, oid);

		return executeFindAllQuery("findByOID", variables);
	}

	/**
	 * Method that returns crf by oid.
	 * 
	 * @param oid
	 *            String
	 * @return CRFBean
	 */
	public CRFBean findByOid(String oid) {
		this.unsetTypeExpected();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, oid);
		String sql = digester.getQuery("findByOID");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
		} else {
			return null;
		}
	}

	/**
	 * Method that returns list of crf names by studyId.
	 * 
	 * @param studyId
	 *            int
	 * @return List<String>
	 */
	public List<String> getAllCRFNamesFromStudy(int studyId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.STRING);
		this.setTypeExpected(2, TypeNames.STRING);
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		ArrayList alist = this.select(digester.getQuery("getAllCRFNamesFromStudy"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap h = (HashMap) anAlist;
			al.add(h.get("name"));
		}
		return al;
	}

	/**
	 * Method that deletes crf by crfId. Note: it will be better to use the DeleteCrfService.deleteCrf(int crfId)
	 * instead of this method, cuz service may have additional logic.
	 * 
	 * @param crfId
	 *            int
	 */
	public void deleteCrfById(int crfId) {
		int index = 1;
		String sql = digester.getQuery("deleteCrfByCrfId");
		HashMap variables = new HashMap();
		variables.put(index++, crfId);
		variables.put(index++, crfId);
		variables.put(index++, crfId);
		variables.put(index++, crfId);
		variables.put(index++, crfId);
		variables.put(index++, crfId);
		variables.put(index++, crfId);
		variables.put(index++, crfId);
		variables.put(index++, crfId);
		variables.put(index++, crfId);
		variables.put(index, crfId);
		this.execute(sql, variables);
	}

	/**
	 * Method that returns crfs that are available for evaluation.
	 * 
	 * @param currentStudyId
	 *            int
	 * @return CRFBean list
	 */
	public List<CRFBean> findAllEvaluableCrfs(int currentStudyId) {
		List result = new ArrayList<CRFBean>();
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, currentStudyId);
		variables.put(2, currentStudyId);
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
		this.setTypesExpected();
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, sed.getId());
		variables.put(index++, ub.getId());
		variables.put(index++, ub.getActiveStudyId());
		variables.put(index, ub.getActiveStudyId());
		ArrayList alist = this.select(digester.getQuery("findAllActiveUnmaskedByDefinition"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			CRFBean eb = (CRFBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}
}
