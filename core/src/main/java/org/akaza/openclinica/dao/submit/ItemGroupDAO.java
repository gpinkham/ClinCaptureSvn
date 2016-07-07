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

package org.akaza.openclinica.dao.submit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.PreparedStatementFactory;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.exception.OpenClinicaException;

/**
 * ItemGroupDAO.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemGroupDAO extends AuditableEntityDAO {

	/**
	 * Constructor.
	 * 
	 * @param ds
	 *            DataSource
	 */
	public ItemGroupDAO(DataSource ds) {
		super(ds);
		this.getCurrentPKName = "findCurrentPKValue";
		this.getNextPKName = "getNextPK";
	}

	/**
	 * Constructor.
	 * 
	 * @param ds
	 *            DataSource
	 * @param digester
	 *            DAODigester
	 */
	public ItemGroupDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
	}

	/**
	 * Constructor.
	 * 
	 * @param ds
	 *            DataSource
	 * @param digester
	 *            DAODigester
	 * @param locale
	 *            Locale
	 */
	public ItemGroupDAO(DataSource ds, DAODigester digester, Locale locale) {

		this(ds, digester);
		this.getCurrentPKName = "findCurrentPKValue";
		this.getNextPKName = "getNextPK";
		this.locale = locale;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_ITEM_GROUP;
	}

	@Override
	public void setTypesExpected() {
		int index = 1;
		this.unsetTypeExpected();
		/*
		 * item_group_id serial NOT NULL, name varchar(255), crf_id numeric NOT NULL, status_id numeric, date_created
		 * date, date_updated date, owner_id numeric, update_id numeric,
		 */
		this.setTypeExpected(index++, TypeNames.INT); // item_group_id
		this.setTypeExpected(index++, TypeNames.STRING); // name
		this.setTypeExpected(index++, TypeNames.INT); // crf_id
		this.setTypeExpected(index++, TypeNames.INT); // status_id
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_created
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date_updated
		this.setTypeExpected(index++, TypeNames.INT); // owner_id
		this.setTypeExpected(index++, TypeNames.INT); // update_id
		this.setTypeExpected(index, TypeNames.STRING); // oc_oid

	}

	/**
	 * Update method.
	 * 
	 * @param eb
	 *            EntityBean
	 * @return EntityBean
	 */
	public EntityBean update(EntityBean eb) {
		int index = 1;
		ItemGroupBean formGroupBean = (ItemGroupBean) eb;
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		/*
		 * item_group_id serial NOT NULL, name varchar(255), crf_id numeric NOT NULL, status_id numeric, date_created
		 * date, date_updated date, owner_id numeric, update_id numeric,
		 */
		variables.put(index++, formGroupBean.getName());
		variables.put(index++, formGroupBean.getCrfId());
		variables.put(index++, formGroupBean.getStatus().getId());
		variables.put(index++, formGroupBean.getUpdater().getId());
		variables.put(index, formGroupBean.getId());
		this.execute(digester.getQuery("update"), variables);
		return eb;
	}

	/**
	 * Find all active by crf.
	 *
	 * @param crf
	 *            CRFBean
	 * @return ArrayList
	 */
	public ArrayList findAllActiveByCrf(CRFBean crf) {
		HashMap variables = new HashMap();
		this.setTypesExpected();
		variables.put(1, crf.getId());
		String sql = digester.getQuery("findAllActiveByCrf");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			ItemGroupBean eb = (ItemGroupBean) this.getEntityFromHashMap(hm);
			al.add(eb);
		}
		return al;

	}

	/**
	 * Find all by permission.
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
	 * @throws OpenClinicaException
	 *             the OpenClinicaException
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) throws OpenClinicaException {
		return new ArrayList();
	}

	/**
	 * Find all by permission.
	 * 
	 * @param objCurrentUser
	 *            Object
	 * @param intActionType
	 *            int
	 * @return Collection
	 * @throws OpenClinicaException
	 *             the OpenClinicaException
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType) throws OpenClinicaException {
		return new ArrayList();
	}

	/**
	 * Returns oid.
	 * 
	 * @param itemGroupBean
	 *            ItemGroupBean
	 * @param crfName
	 *            String
	 * @param itemGroupLabel
	 *            String
	 * @return String
	 */
	private String getOid(ItemGroupBean itemGroupBean, String crfName, String itemGroupLabel) {

		String oid;
		try {
			oid = itemGroupBean.getOid() != null
					? itemGroupBean.getOid()
					: itemGroupBean.getOidGenerator(getDataSource()).generateOid(crfName, itemGroupLabel);
			return oid;
		} catch (Exception e) {
			throw new RuntimeException("CANNOT GENERATE OID");
		}
	}

	/**
	 * Returns valid oid.
	 * 
	 * @param itemGroup
	 *            ItemGroupBean
	 * @param crfName
	 *            String
	 * @param itemGroupLabel
	 *            String
	 * @param oidList
	 *            ArrayList
	 * @return String
	 */
	public String getValidOid(ItemGroupBean itemGroup, String crfName, String itemGroupLabel,
			ArrayList<String> oidList) {

		String oid = getOid(itemGroup, crfName, itemGroupLabel);
		logger.info(oid);
		String oidPreRandomization = oid;
		while (findByOid(oid) != null || oidList.contains(oid)) {
			oid = itemGroup.getOidGenerator(getDataSource()).randomizeOid(oidPreRandomization);
		}
		return oid;
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityBean create(EntityBean eb) {
		return create(eb, null);
	}

	/**
	 * Creates new ItemGroupBean.
	 *
	 * @param eb
	 *            EntityBean
	 * @param con
	 *            Connection
	 * @return EntityBean
	 */
	public EntityBean create(EntityBean eb, Connection con) {
		int index = 1;
		ItemGroupBean formGroupBean = (ItemGroupBean) eb;
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(index++, formGroupBean.getName());
		variables.put(index++, formGroupBean.getCrfId());
		variables.put(index++, formGroupBean.getStatus().getId());
		variables.put(index++, formGroupBean.getOwner().getId());
		variables.put(index, formGroupBean.getOid());
		executeWithPK(digester.getQuery("create"), variables, null, con);
		if (isQuerySuccessful()) {
			eb.setId(getLatestPK());
		}
		return eb;
	}

	/**
	 * Find all.
	 * 
	 * @return Collection
	 */
	public Collection findAll() {
		this.setTypesExpected();
		List listofMaps = this.select(digester.getQuery("findAll"));
		List<ItemGroupBean> beanList = new ArrayList<ItemGroupBean>();
		ItemGroupBean bean;
		for (Object map : listofMaps) {
			bean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	/**
	 * Find groups by item id.
	 * 
	 * @param id
	 *            int
	 * @return Collection
	 */
	public Collection findGroupsByItemId(int id) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, id);
		List listofMap = this.select(digester.getQuery("findGroupsByItemID"), variables);

		List<ItemGroupBean> formGroupBs = new ArrayList<ItemGroupBean>();
		for (Object map : listofMap) {
			ItemGroupBean bean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);
			formGroupBs.add(bean);
		}
		return formGroupBs;

	}

	/**
	 * Find by id.
	 * 
	 * @param id
	 *            int
	 * @return EntityBean
	 */
	public EntityBean findByPK(int id) {
		ItemGroupBean formGroupB = new ItemGroupBean();
		this.setTypesExpected();

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");
		ArrayList listofMap = this.select(sql, variables);
		for (Object map : listofMap) {
			formGroupB = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);

		}
		return formGroupB;
	}

	/**
	 * Find by name.
	 * 
	 * @param name
	 *            String
	 * @return EntityBean
	 */
	public EntityBean findByName(String name) {
		ItemGroupBean formGroupBean = new ItemGroupBean();
		this.setTypesExpected();

		HashMap<Integer, String> variables = new HashMap<Integer, String>();
		variables.put(1, name);

		String sql = digester.getQuery("findByName");
		ArrayList listofMap = this.select(sql, variables);
		for (Object map : listofMap) {
			formGroupBean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);

		}
		return formGroupBean;
	}

	/**
	 * Find all by oid.
	 * 
	 * @param oid
	 *            String
	 * @return List
	 */
	public List<ItemGroupBean> findAllByOid(String oid) {

		this.unsetTypeExpected();
		setTypesExpected();

		HashMap<Integer, String> variables = new HashMap<Integer, String>();
		variables.put(1, oid);
		String sql = digester.getQuery("findGroupByOid");

		ArrayList rows = this.select(sql, variables);
		// return rows;
		List<ItemGroupBean> beanList = new ArrayList<ItemGroupBean>();
		ItemGroupBean bean;
		for (Object map : rows) {
			bean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	/**
	 * Find by oid.
	 * 
	 * @param oid
	 *            String
	 * @return ItemGroupBean
	 */
	public ItemGroupBean findByOid(String oid) {
		ItemGroupBean itemGroup;
		this.unsetTypeExpected();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, oid);
		String sql = digester.getQuery("findGroupByOid");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			itemGroup = (ItemGroupBean) this.getEntityFromHashMap((HashMap) it.next());
			return itemGroup;
		} else {
			return null;
		}
	}

	/**
	 * Find by oid and crf.
	 * 
	 * @param oid
	 *            String
	 * @param crfId
	 *            int
	 * @return ItemGroupBean
	 */
	public ItemGroupBean findByOidAndCrf(String oid, int crfId) {
		int index = 1;
		this.unsetTypeExpected();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(index++, oid);
		variables.put(index, crfId);
		String sql = digester.getQuery("findGroupByOidAndCrfId");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (ItemGroupBean) this.getEntityFromHashMap((HashMap) it.next());
		} else {
			return null;
		}
	}

	/**
	 * Find group by crf version id.
	 * 
	 * @param id
	 *            int
	 * @return List
	 */
	public List<ItemGroupBean> findGroupByCrfVersionId(int id) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, id);
		List listofMaps = this.select(digester.getQuery("findGroupByCRFVersionID"), variables);

		List<ItemGroupBean> beanList = new ArrayList<ItemGroupBean>();
		ItemGroupBean bean;
		for (Object map : listofMaps) {
			bean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	/**
	 * Find by item and crf version.
	 * 
	 * @param item
	 *            ItemBean
	 * @param crfVersion
	 *            CRFVersionBean
	 * @return ItemGroupBean
	 */
	public ItemGroupBean findByItemAndCRFVersion(ItemBean item, CRFVersionBean crfVersion) {
		return this.findByItemIdAndCRFVersionId(item.getId(), crfVersion.getId());
	}

	/**
	 * Find by item id and crf version id.
	 * 
	 * @param itemId
	 *            int
	 * @param crfVersionId
	 *            int
	 * @return ItemGroupBean
	 */
	public ItemGroupBean findByItemIdAndCRFVersionId(int itemId, int crfVersionId) {
		int index = 1;
		this.unsetTypeExpected();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(index++, itemId);
		variables.put(index, crfVersionId);
		String sql = digester.getQuery("findByItemAndCRFVersion");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (ItemGroupBean) this.getEntityFromHashMap((HashMap) it.next());
		} else {
			return null;
		}
	}

	/**
	 * Find only groups by crf version id.
	 * 
	 * @param id
	 *            int
	 * @return List
	 */
	public List<ItemGroupBean> findOnlyGroupsByCRFVersionId(int id) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, id);
		List listofMaps = this.select(digester.getQuery("findOnlyGroupsByCRFVersionID"), variables);

		List<ItemGroupBean> beanList = new ArrayList<ItemGroupBean>();
		ItemGroupBean bean;
		for (Object map : listofMaps) {
			bean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	/**
	 * Find group by section id.
	 * 
	 * @param sectionId
	 *            int
	 * @return List
	 */
	public List<ItemGroupBean> findGroupBySectionId(int sectionId) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, sectionId);
		List listofMaps = this.select(digester.getQuery("findGroupBySectionId"), variables);

		List<ItemGroupBean> beanList = new ArrayList<ItemGroupBean>();
		ItemGroupBean bean;
		for (Object map : listofMaps) {
			bean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	/**
	 * Finds legit groups by section id.
	 * 
	 * @param sectionId
	 *            int
	 * @return List
	 */
	public List<ItemGroupBean> findLegitGroupBySectionId(int sectionId) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, sectionId);
		List listofMaps = this.select(digester.getQuery("findLegitGroupBySectionId"), variables);

		List<ItemGroupBean> beanList = new ArrayList<ItemGroupBean>();
		ItemGroupBean bean;
		for (Object map : listofMaps) {
			bean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	/**
	 * Finds legit groups all by section id.
	 * 
	 * @param sectionId
	 *            int
	 * @return List
	 */
	public List<ItemGroupBean> findLegitGroupAllBySectionId(int sectionId) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, sectionId);
		List listofMaps = this.select(digester.getQuery("findLegitGroupAllBySectionId"), variables);

		List<ItemGroupBean> beanList = new ArrayList<ItemGroupBean>();
		ItemGroupBean bean;
		for (Object map : listofMaps) {
			bean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	/**
	 * Transforms map to object.
	 * 
	 * @param hm
	 *            HashMap
	 * @return Object
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		ItemGroupBean formGroupBean = new ItemGroupBean();
		super.setEntityAuditInformation(formGroupBean, hm);
		formGroupBean.setId((Integer) hm.get("item_group_id"));
		formGroupBean.setName((String) hm.get("name"));
		formGroupBean.setCrfId((Integer) hm.get("crf_id"));
		formGroupBean.setOid((String) hm.get("oc_oid"));

		return formGroupBean;
	}

	/**
	 * Find all.
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
	 * Deletes test group.
	 * 
	 * @param name
	 *            String
	 */
	public void deleteTestGroup(String name) {
		HashMap variables = new HashMap();
		variables.put(1, name);
		this.execute(digester.getQuery("deleteTestGroup"), variables);
	}

	/**
	 * Returns true if item group is repeating based on all crf versions.
	 * 
	 * @param groupOid
	 *            String
	 * @return Boolean
	 */
	public Boolean isItemGroupRepeatingBasedOnAllCrfVersions(String groupOid) {
		Boolean result = false;
		setTypesExpected();
		HashMap<Integer, String> variables = new HashMap<Integer, String>();
		variables.put(1, groupOid);

		String sql = digester.getQuery("isItemGroupRepeatingBasedOnAllCrfVersions");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			Integer count = (Integer) ((HashMap) it.next()).get("count");
			result = count > 0;
		}
		return result;
	}

	/**
	 * Returns true if item group is repeating based on crf version.
	 * 
	 * @param groupOid
	 *            String
	 * @param crfVersion
	 *            Integer
	 * @return Boolean
	 */
	public Boolean isItemGroupRepeatingBasedOnCrfVersion(String groupOid, Integer crfVersion) {
		Boolean result = false;
		setTypesExpected();
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, groupOid);
		variables.put(2, crfVersion);

		String sql = digester.getQuery("isItemGroupRepeatingBasedOnCrfVersion");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			Integer count = (Integer) ((HashMap) it.next()).get("count");
			result = count > 0;
		}
		return result;
	}

	/**
	 * Find top one group by section id.
	 * 
	 * @param sectionId
	 *            int
	 * @return ItemGroupBean
	 */
	public ItemGroupBean findTopOneGroupBySectionId(int sectionId) {
		ItemGroupBean formGroupBean = new ItemGroupBean();
		this.setTypesExpected();

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, sectionId);

		String sql = digester.getQuery("findTopOneGroupBySectionId");
		ArrayList listofMap = this.select(sql, variables);
		for (Object map : listofMap) {
			formGroupBean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);

		}
		return formGroupBean;
	}

	@Override
	public ArrayList select(String query, Map variables) {
		clearSignals();

		ArrayList results = new ArrayList();
		Object key;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatementFactory psf = new PreparedStatementFactory(variables);
		PreparedStatement ps = null;

		try {
			con = getDataSource().getConnection();
			if (con.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: GenericDAO.select!");
				}
				throw new SQLException();
			}

			ps = con.prepareStatement(query);

			ps = psf.generate(ps); // enter variables here!
			key = ps.toString();
			results = (ArrayList) getCache().get(key);
			if (results == null) {
				rs = ps.executeQuery();
				results = this.processResultRows(rs);
				if (results != null) {
					getCache().put(key, results);
				}
			}

			if (logger.isInfoEnabled()) {
				logger.info("Executing dynamic query, EntityDAO.select:query " + query);
			}
			signalSuccess();

		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while executing dynamic query, GenericDAO.select: " + query + ":message: "
						+ sqle.getMessage());
				sqle.printStackTrace();
			}
		} finally {
			this.closeIfNecessary(con, rs, ps);
		}
		return results;

	}
}
