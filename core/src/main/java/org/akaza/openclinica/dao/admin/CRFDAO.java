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
package org.akaza.openclinica.dao.admin;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
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

/**
 * the data access object for instruments in the database.
 * 
 * @author thickerson
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CRFDAO extends AuditableEntityDAO {

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_CRF;
	}

	public CRFDAO(DataSource ds) {
		super(ds);
	}

	public CRFDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
	}

	@Override
	public void setTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);
		// this.setTypeExpected(3,TypeNames.STRING);//label
		this.setTypeExpected(3, TypeNames.STRING);// name
		this.setTypeExpected(4, TypeNames.STRING);// description
		this.setTypeExpected(5, TypeNames.INT);// owner id
		this.setTypeExpected(6, TypeNames.DATE);// created
		this.setTypeExpected(7, TypeNames.DATE);// updated
		this.setTypeExpected(8, TypeNames.INT);// update id
		this.setTypeExpected(9, TypeNames.STRING);// oc_oid
		this.setTypeExpected(10, TypeNames.INT);// study_id
	}

	public EntityBean update(EntityBean eb) {
		CRFBean cb = (CRFBean) eb;
		HashMap variables = new HashMap();
		variables.put(1, cb.getStatus().getId());
		variables.put(2, cb.getName());
		variables.put(3, cb.getDescription());
		variables.put(4, cb.getUpdater().getId());
		variables.put(5, cb.getId());
		this.execute(digester.getQuery("update"), variables);
		return eb;
	}

	public EntityBean create(EntityBean eb) {
		CRFBean cb = (CRFBean) eb;
		HashMap variables = new HashMap();
		variables.put(1, cb.getStatus().getId());
		variables.put(2, cb.getName());
		variables.put(3, cb.getDescription());
		variables.put(4, cb.getOwner().getId());
		variables.put(5, getValidOid(cb, cb.getName()));
		this.execute(digester.getQuery("create"), variables);
		if (isQuerySuccessful()) {
			cb.setActive(true);
		}
		return cb;
	}

	public Object getEntityFromHashMap(HashMap hm) {
		CRFBean eb = new CRFBean();
		this.setEntityAuditInformation(eb, hm);
		eb.setId((Integer) hm.get("crf_id"));
		eb.setName((String) hm.get("name"));
		eb.setDescription((String) hm.get("description"));
		eb.setOid((String) hm.get("oc_oid"));
		eb.setStudyId((Integer) hm.get("source_study_id"));
		return eb;
	}

	public Collection findAll() {

		return findAllByLimit(false);
	}

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

	public List<CRFBean> findAllActiveCrfs() {
		List result = new ArrayList<CRFBean>();
		this.setTypesExpected();
		ArrayList objList = select(digester.getQuery("findAllActiveCrfs"), new HashMap());
		for (Object object : objList) {
			result.add(getEntityFromHashMap((HashMap) object));
		}
		return result;
	}

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

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

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

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

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

	private String getOid(CRFBean crfBean, String crfName) {

		String oid;
		try {
			oid = crfBean.getOid() != null ? crfBean.getOid() : crfBean.getOidGenerator(ds).generateOid(crfName);
			return oid;
		} catch (Exception e) {
			throw new RuntimeException("CANNOT GENERATE OID");
		}
	}

	public String getValidOid(CRFBean crfBean, String crfName) {

		String oid = getOid(crfBean, crfName);
		logger.info(oid);
		String oidPreRandomization = oid;
		while (findAllByOid(oid).size() > 0) {
			oid = crfBean.getOidGenerator(ds).randomizeOid(oidPreRandomization);
		}
		return oid;
	}

	public ArrayList<CRFBean> findAllByOid(String oid) {
		HashMap<Integer, String> variables = new HashMap<Integer, String>();
		variables.put(1, oid);

		return executeFindAllQuery("findByOID", variables);
	}

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

	public void deleteCrfById(int crfId) {
		String sql = digester.getQuery("deleteCrfByCrfId");
		HashMap variables = new HashMap();
		variables.put(1, crfId);
		variables.put(2, crfId);
		variables.put(3, crfId);
		variables.put(4, crfId);
		variables.put(5, crfId);
		variables.put(6, crfId);
		variables.put(7, crfId);
		variables.put(8, crfId);
		variables.put(9, crfId);
		this.execute(sql, variables);
	}
}
