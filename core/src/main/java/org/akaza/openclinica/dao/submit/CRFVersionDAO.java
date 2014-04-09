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
 * Copyright 2003-2008 Akaza Research
 *
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 */
package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/**
 * <p>
 * CRFVersionDAO.java, the data access object for versions of instruments in the database. Each of these are related to
 * Sections, a versioning map that links them with Items, and an Event, which then links to a Study.
 * 
 * @author thickerson
 */
@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
public class CRFVersionDAO extends AuditableEntityDAO implements ICRFVersionDAO {

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_CRFVERSION;
	}

	public CRFVersionDAO(DataSource ds) {
		super(ds);
	}

	public CRFVersionDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
	}

	// This constructor sets up the Locale for JUnit tests; see the locale
	// member variable in EntityDAO, and its initializeI18nStrings() method
	public CRFVersionDAO(DataSource ds, DAODigester digester, Locale locale) {

		this(ds, digester);
		this.locale = locale;
	}

	public EntityBean update(EntityBean eb) {
		// UPDATE CRF_VERSION SET CRF_ID=?,STATUS_ID=?,NAME=?,
		// DESCRIPTION=?,DATE_UPDATED=NOW(),UPDATE_ID=?,REVISION_NOTES =? WHERE
		// CRF_VERSION_ID=?
		CRFVersionBean ib = (CRFVersionBean) eb;
		HashMap variables = new HashMap();
		variables.put(1, ib.getCrfId());
		variables.put(2, ib.getStatus().getId());
		variables.put(3, ib.getName());
		variables.put(4, ib.getDescription());
		variables.put(5, ib.getUpdater().getId());
		variables.put(6, ib.getRevisionNotes());
		variables.put(7, ib.getId());
		this.execute(digester.getQuery("update"), variables);
		return eb;
	}

	public EntityBean create(EntityBean eb) {
		return eb;
	}

	@Override
	public void setTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.STRING);

		this.setTypeExpected(5, TypeNames.STRING);
		this.setTypeExpected(6, TypeNames.INT);
		this.setTypeExpected(7, TypeNames.DATE);

		this.setTypeExpected(8, TypeNames.DATE);
		this.setTypeExpected(9, TypeNames.INT);
		this.setTypeExpected(10, TypeNames.INT);
		this.setTypeExpected(11, TypeNames.STRING);

	}

	public Object getEntityFromHashMap(HashMap hm) {
		CRFVersionBean eb = new CRFVersionBean();
		super.setEntityAuditInformation(eb, hm);
		eb.setId((Integer) hm.get("crf_version_id"));

		eb.setName((String) hm.get("name"));
		eb.setDescription((String) hm.get("description"));
		eb.setCrfId((Integer) hm.get("crf_id"));
		eb.setRevisionNotes((String) hm.get("revision_notes"));
		eb.setOid((String) hm.get("oc_oid"));
		return eb;
	}

	public Collection findAll() {
		return new ArrayList();
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public Collection findAllByCRF(int crfId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, crfId);
		String sql = digester.getQuery("findAllByCRF");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			CRFVersionBean eb = (CRFVersionBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	public Collection findAllActiveByCRF(int crfId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, crfId);
		String sql = digester.getQuery("findAllActiveByCRF");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			CRFVersionBean eb = (CRFVersionBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	public Collection findItemFromMap(int versionId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, versionId);
		String sql = digester.getQuery("findItemFromMap");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			ItemBean eb = new ItemBean();
			HashMap hm = (HashMap) anAlist;
			eb.setId((Integer) hm.get("item_id"));
			eb.setName((String) hm.get("name"));
			Integer ownerId = (Integer) hm.get("owner_id");
			eb.setOwnerId(ownerId);

			al.add(eb);
		}
		return al;
	}

	public Collection findItemUsedByOtherVersion(int versionId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, versionId);
		String sql = digester.getQuery("findItemUsedByOtherVersion");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			ItemBean eb = new ItemBean();
			HashMap hm = (HashMap) anAlist;
			eb.setId((Integer) hm.get("item_id"));
			eb.setName((String) hm.get("name"));
			eb.setOwnerId((Integer) hm.get("owner_id"));
			al.add(eb);
		}
		return al;
	}

	public ArrayList findNotSharedItemsByVersion(int versionId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, versionId);
		variables.put(2, versionId);
		String sql = digester.getQuery("findNotSharedItemsByVersion");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			ItemBean eb = new ItemBean();
			HashMap hm = (HashMap) anAlist;
			eb.setId((Integer) hm.get("item_id"));
			eb.setName((String) hm.get("name"));
			eb.setOwnerId((Integer) hm.get("owner_id"));
			al.add(eb);
		}
		return al;
	}

	public boolean isItemUsedByOtherVersion(int versionId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, versionId);
		String sql = digester.getQuery("isItemUsedByOtherVersion");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();
		return it.hasNext();
	}

	public boolean hasItemData(int itemId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, itemId);
		String sql = digester.getQuery("hasItemData");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();
		return it.hasNext();
	}

	public EntityBean findByPK(int id) {
		CRFVersionBean eb = new CRFVersionBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (CRFVersionBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;

	}

	public EntityBean findByFullName(String version, String crfName) {
		CRFVersionBean eb = new CRFVersionBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, version);
		variables.put(2, crfName);

		String sql = digester.getQuery("findByFullName");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (CRFVersionBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;

	}

	/**
	 * Deletes a CRF version
	 */
	@Deprecated
	public void delete(int id) {
		HashMap variables = new HashMap();
		// variables.put(new Integer(1), new Integer(id));

		String sql = digester.getQuery("delete") + id;
		this.execute(sql, variables);
	}

	/**
	 * Generates all the delete queries for deleting a version
	 * 
	 * @param versionId
	 *            int
	 * @param items
	 *            ArrayList
	 */
	public ArrayList generateDeleteQueries(int versionId, ArrayList items) {
		ArrayList sqls = new ArrayList();
		String sql = digester.getQuery("deleteScdItemMetadataByVersion") + versionId + ")";
		sqls.add(sql);
		sql = digester.getQuery("deleteItemMetaDataByVersion") + versionId;
		sqls.add(sql);
		sql = digester.getQuery("deleteSectionsByVersion") + versionId;
		sqls.add(sql);
		sql = digester.getQuery("deleteItemMapByVersion") + versionId;
		sqls.add(sql);

		sql = digester.getQuery("deleteItemGroupMetaByVersion") + versionId;
		sqls.add(sql);

		for (Object item1 : items) {
			ItemBean item = (ItemBean) item1;
			sql = digester.getQuery("deleteItemsByVersion") + item.getId();
			sqls.add(sql);
		}

		sql = digester.getQuery("deleteResponseSetByVersion") + versionId;
		sqls.add(sql);

		sql = digester.getQuery("delete") + versionId;
		sqls.add(sql);
		return sqls;

	}

	private String getOid(CRFVersionBean crfVersion, String crfName, String crfVersionName) {

		String oid;
		try {
			oid = crfVersion.getOid() != null ? crfVersion.getOid() : crfVersion.getOidGenerator(ds).generateOid(
					crfName, crfVersionName);
			return oid;
		} catch (Exception e) {
			throw new RuntimeException("CANNOT GENERATE OID");
		}
	}

	public String getValidOid(CRFVersionBean crfVersion, String crfName, String crfVersionName) {

		String oid = getOid(crfVersion, crfName, crfVersionName);
		logger.info(oid);
		String oidPreRandomization = oid;
		while (findAllByOid(oid).size() > 0) {
			oid = crfVersion.getOidGenerator(ds).randomizeOid(oidPreRandomization);
		}
		return oid;

	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	public ArrayList findAllByOid(String oid) {
		HashMap variables = new HashMap();
		variables.put(1, oid);

		return executeFindAllQuery("findAllByOid", variables);
	}

	public int getCRFIdFromCRFVersionId(int CRFVersionId) {
		int answer = 0;

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, CRFVersionId);

		String sql = digester.getQuery("getCRFIdFromCRFVersionId");
		ArrayList rows = select(sql, variables);

		if (rows.size() > 0) {
			HashMap h = (HashMap) rows.get(0);
			answer = (Integer) h.get("crf_id");
		}
		return answer;
	}

	public ArrayList findAllByCRFId(int CRFId) {
		HashMap variables = new HashMap();
		variables.put(1, CRFId);

		return executeFindAllQuery("findAllByCRFId", variables);
	}

	public Integer findCRFVersionId(int crfId, String versionName) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, crfId);
		variables.put(2, versionName);
		ArrayList result = this.select(digester.getQuery("findCRFVersionId"), variables);
		HashMap map;
		Integer crfVersionId = null;
		if (result.iterator().hasNext()) {
			map = (HashMap) result.iterator().next();
			crfVersionId = (Integer) map.get("crf_version_id");
		}
		return crfVersionId;
	}

	public CRFVersionBean findByOid(String oid) {
		CRFVersionBean crfVersionBean;
		this.unsetTypeExpected();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, oid);
		String sql = digester.getQuery("findByOID");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			crfVersionBean = (CRFVersionBean) this.getEntityFromHashMap((HashMap) it.next());
			return crfVersionBean;
		} else {
			return null;
		}
	}

}
