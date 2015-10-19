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
package org.akaza.openclinica.dao.submit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.odmbeans.SimpleConditionalDisplayBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.core.util.ItemGroupCrvVersionUtil;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.PreparedStatementFactory;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.domain.datamap.ResponseSet;

/**
 * ItemDAO.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemDAO extends AuditableEntityDAO {

	public static final int INT_15 = 15;

	/**
	 * Constructor.
	 * 
	 * @param ds
	 *            DataSource
	 */
	public ItemDAO(DataSource ds) {
		super(ds);
	}

	/**
	 * Constructor.
	 *
	 * @param ds
	 *            DataSource
	 * @param con
	 *            Connection
	 */
	public ItemDAO(DataSource ds, Connection con) {
		super(ds, con);
	}

	/**
	 * Constructor.
	 * 
	 * @param ds
	 *            DataSource
	 * @param digester
	 *            DAODigester
	 */
	public ItemDAO(DataSource ds, DAODigester digester) {
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
	public ItemDAO(DataSource ds, DAODigester digester, Locale locale) {
		this(ds, digester);
		this.locale = locale;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_ITEM;
	}

	@Override
	public void setTypesExpected() {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.BOOL); // phi status
		this.setTypeExpected(index++, TypeNames.INT); // data type id
		this.setTypeExpected(index++, TypeNames.INT); // reference type id
		this.setTypeExpected(index++, TypeNames.INT); // status id
		this.setTypeExpected(index++, TypeNames.INT); // owner id
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // created
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // updated
		this.setTypeExpected(index++, TypeNames.INT); // update id
		this.setTypeExpected(index++, TypeNames.STRING); // oc_oid
		this.setTypeExpected(index, TypeNames.STRING); // sas_name
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityBean update(EntityBean eb) {
		return update(eb, null);
	}

	/**
	 * Updates ItemBean.
	 *
	 * @param eb
	 *            EntityBean
	 * @param con
	 *            Connection
	 * @return EntityBean
	 */
	public EntityBean update(EntityBean eb, Connection con) {
		int index = 1;
		ItemBean ib = (ItemBean) eb;
		HashMap variables = new HashMap();
		variables.put(index++, ib.getName());
		variables.put(index++, ib.getDescription());
		variables.put(index++, ib.getUnits());
		variables.put(index++, ib.isPhiStatus());
		variables.put(index++, ib.getItemDataTypeId());
		variables.put(index++, ib.getItemReferenceTypeId());
		variables.put(index++, ib.getStatus().getId());
		variables.put(index++, ib.getUpdaterId());
		variables.put(index, ib.getId());
		this.execute(digester.getQuery("update"), variables, null, con);
		return eb;
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityBean create(EntityBean eb) {
		return create(eb, null);
	}

	/**
	 * Creates new ItemBean.
	 *
	 * @param eb
	 *            EntityBean
	 * @param con
	 *            Connection
	 * @return EntityBean
	 */
	public EntityBean create(EntityBean eb, Connection con) {
		int index = 1;
		ItemBean ib = (ItemBean) eb;
		// per the create sql statement
		HashMap variables = new HashMap();
		variables.put(index++, ib.getName());
		variables.put(index++, ib.getDescription());
		variables.put(index++, ib.getUnits());
		variables.put(index++, ib.isPhiStatus());
		variables.put(index++, ib.getItemDataTypeId());
		variables.put(index++, ib.getItemReferenceTypeId());
		variables.put(index++, ib.getStatus().getId());
		variables.put(index++, ib.getOwnerId());
		variables.put(index++, ib.getOid());
		variables.put(index, ib.getSasName());
		executeWithPK(digester.getQuery("create"), variables, null, con);
		if (isQuerySuccessful()) {
			eb.setId(getLatestPK());
		}
		return eb;
	}

	/**
	 * Returns true if measurement unit exists.
	 *
	 * @param unit
	 *            String
	 * @param connection
	 *            Connection
	 * @return boolean
	 */
	public boolean doesMeasurementUnitExist(String unit, Connection connection) {
		unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index, unit);
		ArrayList<Map> resultSet = select("select id from measurement_unit where name = ?", variables, connection);
		Iterator it = resultSet.iterator();
		return it.hasNext();
	}

	/**
	 * Creates new measurement unit.
	 *
	 * @param unitOid
	 *            String
	 * @param unit
	 *            String
	 * @param connection
	 *            Connection
	 */
	public void createMeasurementUnit(String unitOid, String unit, Connection connection) {
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, unitOid);
		variables.put(index++, unit);
		variables.put(index++, 0);
		variables.put(index, "");
		executeWithPK("insert into measurement_unit (oc_oid, name, version, description) values (?,?,?,?)", variables,
				null, connection);
	}

	/**
	 * Finds existing response set id.
	 *
	 * @param crfVersionId
	 *            int
	 * @param responseSet
	 *            ResponseSet
	 * @param connection
	 *            Connection
	 * @return Integer
	 */
	public Integer findExistingResponseSetId(int crfVersionId, ResponseSet responseSet, Connection connection) {
		unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, responseSet.getLabel());
		variables.put(index++, responseSet.getOptionsText());
		variables.put(index++, responseSet.getOptionsValues());
		variables.put(index++, responseSet.getResponseType().getResponseTypeId());
		variables.put(index, crfVersionId);
		ArrayList<Map> resultSet = select(
				"select response_set_id from response_set where label = ? and options_text = ? and options_values = ? and response_type_id = ? and version_id = ?",
				variables, connection);
		Iterator it = resultSet.iterator();
		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("response_set_id");
		} else {
			return null;
		}
	}

	/**
	 * Creates new response set.
	 * 
	 * @param crfVersionId
	 *            int
	 * @param responseSet
	 *            ResponseSet
	 * @param connection
	 *            Connection
	 */
	public void createResponseSet(int crfVersionId, ResponseSet responseSet, Connection connection) {
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, responseSet.getLabel());
		variables.put(index++, responseSet.getOptionsText());
		variables.put(index++, responseSet.getOptionsValues());
		variables.put(index++, responseSet.getResponseType().getResponseTypeId());
		variables.put(index, crfVersionId);
		executeWithPK(
				"insert into response_set (label, options_text, options_values, response_type_id, version_id) values (?,?,?,?,?)",
				variables, null, connection);
		if (isQuerySuccessful()) {
			responseSet.setResponseSetId(getLatestPK());
		}
	}

	/**
	 * Creates versioning map.
	 * 
	 * @param itemId
	 *            int
	 * @param crfVersionId
	 *            int
	 * @param connection
	 *            Connection
	 */
	public void createVersioningMap(int itemId, int crfVersionId, Connection connection) {
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, crfVersionId);
		variables.put(index, itemId);
		execute("insert into versioning_map (crf_version_id, item_id) values (?,?)", variables, null, connection);
	}

	/**
	 * Creates scd item meta.
	 *
	 * @param scdItemFormMetadataId
	 *            int
	 * @param controlItemFormMetadataId
	 *            int
	 * @param simpleConditionalDisplay
	 *            SimpleConditionalDisplayBean
	 * @param connection
	 *            Connection
	 */
	public void createSCDItemMeta(int scdItemFormMetadataId, int controlItemFormMetadataId,
			SimpleConditionalDisplayBean simpleConditionalDisplay, Connection connection) {
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, scdItemFormMetadataId);
		variables.put(index++, controlItemFormMetadataId);
		variables.put(index++, simpleConditionalDisplay.getControlItemName());
		variables.put(index++, simpleConditionalDisplay.getOptionValue());
		variables.put(index, simpleConditionalDisplay.getMessage());
		execute("insert into scd_item_metadata (scd_item_form_metadata_id, control_item_form_metadata_id, control_item_name, option_value, message) values (?,?,?,?,?)",
				variables, null, connection);
	}

	private String getOid(ItemBean itemBean, String crfName, String itemLabel) {

		String oid;
		try {
			oid = itemBean.getOid() != null
					? itemBean.getOid()
					: itemBean.getOidGenerator(ds).generateOid(crfName, itemLabel);
			return oid;
		} catch (Exception e) {
			throw new RuntimeException("CANNOT GENERATE OID");
		}
	}

	/**
	 * Returns count of active items.
	 * 
	 * @return Integer
	 */
	public Integer getCountOfActiveItems() {
		setTypesExpected();

		String sql = digester.getQuery("getCountofItems");

		ArrayList rows = this.select(sql);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/**
	 * Returns valid oid.
	 * 
	 * @param itemBean
	 *            ItemBean
	 * @param crfName
	 *            String
	 * @param itemLabel
	 *            String
	 * @param oidList
	 *            ArrayList
	 * @return String
	 */
	public String getValidOid(ItemBean itemBean, String crfName, String itemLabel, ArrayList<String> oidList) {

		String oid = getOid(itemBean, crfName, itemLabel);
		logger.info(oid);
		String oidPreRandomization = oid;
		while (findByOid(oid).size() > 0 || oidList.contains(oid)) {
			oid = itemBean.getOidGenerator(ds).randomizeOid(oidPreRandomization);
		}
		return oid;

	}

	/**
	 * Transforms map to object.
	 * 
	 * @param hm
	 *            HashMap
	 * @return Object
	 */
	@SuppressWarnings("deprecation")
	public Object getEntityFromHashMap(HashMap hm) {
		ItemBean eb = new ItemBean();
		// below inserted to find out a class cast exception, tbh
		Date dateCreated = (Date) hm.get("date_created");
		Date dateUpdated = (Date) hm.get("date_updated");
		Integer statusId = (Integer) hm.get("status_id");
		Integer ownerId = (Integer) hm.get("owner_id");
		Integer updateId = (Integer) hm.get("update_id");

		eb.setCreatedDate(dateCreated);
		eb.setUpdatedDate(dateUpdated);
		eb.setStatus(Status.get(statusId));
		eb.setOwnerId(ownerId);
		eb.setUpdaterId(updateId);
		eb.setName((String) hm.get("name"));
		eb.setId((Integer) hm.get("item_id"));
		eb.setDescription((String) hm.get("description"));
		eb.setUnits((String) hm.get("units"));
		eb.setPhiStatus((Boolean) hm.get("phi_status"));
		eb.setItemDataTypeId((Integer) hm.get("item_data_type_id"));
		eb.setItemReferenceTypeId((Integer) hm.get("item_reference_type_id"));
		eb.setDataType(ItemDataType.get(eb.getItemDataTypeId()));
		eb.setOid((String) hm.get("oc_oid"));
		eb.setSasName((String) hm.get("sas_name"));
		// the rest should be all set
		return eb;
	}

	/**
	 * Find by oid.
	 * 
	 * @param oid
	 *            String
	 * @return List
	 */
	public List<ItemBean> findByOid(String oid) {
		this.setTypesExpected();
		HashMap<Integer, String> variables = new HashMap<Integer, String>();
		variables.put(1, oid);
		List listofMaps = this.select(digester.getQuery("findItemByOid"), variables);

		List<ItemBean> beanList = new ArrayList<ItemBean>();
		ItemBean bean;
		for (Object map : listofMaps) {
			bean = (ItemBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	/**
	 * Find all.
	 * 
	 * @return Collection
	 */
	public Collection findAll() {
		this.setTypesExpected();
		ArrayList alist = this.select(digester.getQuery("findAll"));
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			ItemBean eb = (ItemBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
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
	 * Find all parents by section id.
	 * 
	 * @param sectionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllParentsBySectionId(int sectionId) {
		HashMap variables = new HashMap();
		variables.put(1, sectionId);

		return this.executeFindAllQuery("findAllParentsBySectionId", variables);
	}

	/**
	 * Find all non repeating parents by section id.
	 * 
	 * @param sectionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllNonRepeatingParentsBySectionId(int sectionId) {
		HashMap variables = new HashMap();
		variables.put(1, sectionId);

		return this.executeFindAllQuery("findAllNonRepeatingParentsBySectionId", variables);
	}

	/**
	 * Find all by section id.
	 * 
	 * @param sectionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllBySectionId(int sectionId) {
		HashMap variables = new HashMap();
		variables.put(1, sectionId);

		return this.executeFindAllQuery("findAllBySectionId", variables);
	}

	/**
	 * Find all ungrouped parents by section id.
	 * 
	 * @param sectionId
	 *            int
	 * @param crfVersionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllUngroupedParentsBySectionId(int sectionId, int crfVersionId) {
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, sectionId);
		variables.put(index, crfVersionId);

		return this.executeFindAllQuery("findAllUngroupedParentsBySectionId", variables);
	}

	/**
	 * Find all items by version id.
	 * 
	 * @param versionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllItemsByVersionId(int versionId) {
		HashMap variables = new HashMap();
		variables.put(1, versionId);

		return this.executeFindAllQuery("findAllItemsByVersionId", variables);
	}

	/**
	 * Find all versions by item id.
	 * 
	 * @param itemId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllVersionsByItemId(int itemId) {
		HashMap variables = new HashMap();
		variables.put(1, itemId);
		String sql = digester.getQuery("findAllVersionsByItemId");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			Integer versionId = (Integer) ((HashMap) anAlist).get("crf_version_id");
			al.add(versionId);
		}
		return al;

	}

	/**
	 * Find all items by group id.
	 * 
	 * @param id
	 *            int
	 * @param crfVersionId
	 *            int
	 * @return List
	 */
	public List<ItemBean> findAllItemsByGroupId(int id, int crfVersionId) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, id);
		variables.put(2, crfVersionId);
		String sql = digester.getQuery("findAllItemsByGroupId");
		List listofMaps = this.select(sql, variables);
		List<ItemBean> beanList = new ArrayList<ItemBean>();
		ItemBean bean;
		for (Object map : listofMaps) {
			bean = (ItemBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	/**
	 * Find all items by group id for print.
	 * 
	 * @param id
	 *            int
	 * @param crfVersionId
	 *            int
	 * @param sectionId
	 *            int
	 * @return List
	 */
	public List<ItemBean> findAllItemsByGroupIdForPrint(int id, int crfVersionId, int sectionId) {
		int index = 1;
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(index++, id);
		variables.put(index++, crfVersionId);
		variables.put(index, sectionId);
		String sql = digester.getQuery("findAllItemsByGroupIdForPrint");
		List listofMaps = this.select(sql, variables);
		List<ItemBean> beanList = new ArrayList<ItemBean>();
		ItemBean bean;
		for (Object map : listofMaps) {
			bean = (ItemBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	/**
	 * Find by group id and item oid.
	 * 
	 * @param id
	 *            int
	 * @param itemOid
	 *            String
	 * @return ItemBean
	 */
	public ItemBean findItemByGroupIdAndItemOid(int id, String itemOid) {
		int index = 1;
		ItemBean bean;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, id);
		variables.put(index, itemOid);
		String sql = digester.getQuery("findItemByGroupIdandItemOid");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			bean = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
			return bean;
		} else {
			return null;
		}
	}

	/**
	 * Find all active by crf.
	 * 
	 * @param crf
	 *            CRFBean
	 * @return ArrayList
	 */
	public ArrayList findAllActiveByCRF(CRFBean crf) {
		int index = INT_15;
		HashMap variables = new HashMap();
		this.setTypesExpected();
		this.setTypeExpected(index++, TypeNames.INT); // crf_version_id
		this.setTypeExpected(index, TypeNames.STRING); // version name
		variables.put(1, crf.getId());
		String sql = digester.getQuery("findAllActiveByCRF");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			ItemBean eb = (ItemBean) this.getEntityFromHashMap(hm);
			Integer versionId = (Integer) hm.get("crf_version_id");
			String versionName = (String) hm.get("cvname");
			ItemFormMetadataBean imf = new ItemFormMetadataBean();
			imf.setCrfVersionName(versionName);
			imf.setCrfVersionId(versionId);
			eb.setItemMeta(imf);
			al.add(eb);
		}
		return al;

	}

	/**
	 * Find by pk.
	 *
	 * @param id
	 *            int
	 * @return EntityBean
	 */
	public EntityBean findByPK(int id) {
		ItemBean eb = new ItemBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");

		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * Find by name.
	 * 
	 * @param name
	 *            String
	 * @return EntityBean
	 */
	public EntityBean findByName(String name) {
		ItemBean eb = new ItemBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, name);

		String sql = digester.getQuery("findByName");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * Find by name and crf id.
	 * 
	 * @param name
	 *            String
	 * @param crfId
	 *            int
	 * @return EntityBean
	 */
	public EntityBean findByNameAndCRFId(String name, int crfId) {
		int index = 1;
		ItemBean eb = new ItemBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(index++, name);
		variables.put(index, crfId);

		String sql = digester.getQuery("findByNameAndCRFId");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * Find by name and crf version id.
	 * 
	 * @param itemName
	 *            String
	 * @param crfVersionId
	 *            int
	 * @return EntityBean
	 */
	public EntityBean findByNameAndCRFVersionId(String itemName, int crfVersionId) {

		ItemBean itemBean = new ItemBean();
		this.setTypesExpected();

		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, itemName);
		variables.put(index, crfVersionId);

		String sql = digester.getQuery("findByNameAndCRFVersionId");
		ArrayList items = this.select(sql, variables);

		Iterator it = items.iterator();
		if (it.hasNext()) {
			itemBean = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return itemBean;

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
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
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
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	/**
	 * Finds the children of an item in a given CRF Version, sorted by columnNumber in ascending order.
	 * 
	 * @param parentId
	 *            The id of the children's parent.
	 * @param crfVersionId
	 *            The id of the event CRF in which the children belong to this parent.
	 * @return An array of ItemBeans, where each ItemBean represents a child of the parent and the array is sorted by
	 *         columnNumber in ascending order.
	 */
	public ArrayList findAllByParentIdAndCRFVersionId(int parentId, int crfVersionId) {
		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, parentId);
		variables.put(index, crfVersionId);

		return this.executeFindAllQuery("findAllByParentIdAndCRFVersionId", variables);
	}

	/**
	 * Finds all required by crf version id.
	 * 
	 * @param crfVersionId
	 *            int
	 * @return int
	 */
	public int findAllRequiredByCRFVersionId(int crfVersionId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		int answer = 0;

		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);
		String sql = digester.getQuery("findAllRequiredByCRFVersionId");

		ArrayList rows = select(sql, variables);

		if (rows.size() > 0) {
			HashMap row = (HashMap) rows.get(0);
			answer = (Integer) row.get("number");
		}

		return answer;
	}

	/**
	 * Finds all required by section id.
	 * 
	 * @param sectionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllRequiredBySectionId(int sectionId) {
		HashMap variables = new HashMap();
		variables.put(1, sectionId);

		return this.executeFindAllQuery("findAllRequiredBySectionId", variables);
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
			con = ds.getConnection();
			if (con.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: GenericDAO.select!");
				}
				throw new SQLException();
			}

			ps = con.prepareStatement(query);

			ps = psf.generate(ps); // enter variables here!
			logger.info("query is..." + ps.toString());
			key = ps.toString();
			results = (ArrayList) cache.get(key);
			if (results == null) {
				rs = ps.executeQuery();
				results = this.processResultRows(rs);
				if (results != null) {
					cache.put(key, results);
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

	/**
	 * Finds all with item data by crf version id & event crf id.
	 * 
	 * @param crfVersionId
	 *            int
	 * @param eventCRFId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList<ItemBean> findAllWithItemDataByCRFVersionId(int crfVersionId, int eventCRFId) {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT); // item_id
		this.setTypeExpected(index++, TypeNames.STRING); // item name
		this.setTypeExpected(index++, TypeNames.STRING); // oc_oid
		this.setTypeExpected(index++, TypeNames.INT); // item_data_id
		this.setTypeExpected(index++, TypeNames.STRING); // (item)value
		this.setTypeExpected(index, TypeNames.INT); // ordinal

		ArrayList<ItemBean> answer = new ArrayList<ItemBean>();

		index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, eventCRFId);
		variables.put(index, crfVersionId);

		String sql = digester.getQuery("findAllWithItemDataByCRFVersionId");

		ArrayList rows = super.select(sql, variables);
		Iterator it = rows.iterator();
		int curItemId = 0;
		ItemBean itemBean = null;
		ItemDataBean itemDataBean;
		while (it.hasNext()) {
			HashMap row = (HashMap) it.next();
			Integer id = (Integer) row.get("item_id");
			if (curItemId != id) {
				itemBean = new ItemBean();
				answer.add(itemBean);
				curItemId = id;
				itemBean.setId(curItemId);
				itemBean.setName((String) row.get("name"));
				itemBean.setOid((String) row.get("oc_oid"));

			}
			if (itemBean != null) {
				itemDataBean = new ItemDataBean();
				itemDataBean.setValue((String) row.get("value"));
				itemDataBean.setOrdinal((Integer) row.get("ordinal"));
				itemDataBean.setId((Integer) row.get("item_data_id"));
				itemDataBean.setItemId(curItemId);
				itemBean.addItemDataElement(itemDataBean);
			}

		}

		return answer;
	}

	/**
	 * Finds all with item group, crf version & metadata by crf id.
	 *
	 * @param crfName
	 *            String
	 * @return ArrayList
	 */
	public ArrayList<ItemGroupCrvVersionUtil> findAllWithItemGroupCRFVersionMetadataByCRFId(String crfName) {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.STRING); // (crf)name
		this.setTypeExpected(index++, TypeNames.STRING); // (crf)name
		this.setTypeExpected(index++, TypeNames.STRING); // (crf)name
		this.setTypeExpected(index++, TypeNames.STRING); // (crf)name
		this.setTypeExpected(index, TypeNames.INT); // (crf)name

		HashMap variables = new HashMap();
		variables.put(1, crfName);

		String sql = digester.getQuery("findAllWithItemGroupCRFVersionMetadataByCRFId");
		ArrayList rows = this.select(sql, variables);
		ItemGroupCrvVersionUtil item;
		ArrayList<ItemGroupCrvVersionUtil> itemGroupCrfVersionInfo = new ArrayList<ItemGroupCrvVersionUtil>();
		for (Object row1 : rows) {
			HashMap row = (HashMap) row1;
			item = new ItemGroupCrvVersionUtil();
			item.setItemName((String) row.get("item_name"));
			item.setCrfVersionName((String) row.get("version_name"));
			item.setCrfVersionStatus((Integer) row.get("v_status"));
			item.setGroupName((String) row.get("group_name"));
			item.setGroupOID((String) row.get("group_oid"));
			itemGroupCrfVersionInfo.add(item);

		}
		return itemGroupCrfVersionInfo;
	}

	/**
	 * Finds all with item details group, crf version & metadata by crf id.
	 * 
	 * @param crfName
	 *            String
	 * @return ArrayList
	 */
	public ArrayList<ItemGroupCrvVersionUtil> findAllWithItemDetailsGroupCRFVersionMetadataByCRFId(String crfName) {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.STRING); // (crf)name
		this.setTypeExpected(index++, TypeNames.STRING); // (crf)description
		this.setTypeExpected(index++, TypeNames.INT); // (crf)item_id
		this.setTypeExpected(index++, TypeNames.INT); // (crf)data_type
		this.setTypeExpected(index++, TypeNames.STRING); // (crf)item_oid
		this.setTypeExpected(index++, TypeNames.STRING); // (crf)group_name
		this.setTypeExpected(index++, TypeNames.STRING); // (crf)group_oid
		this.setTypeExpected(index++, TypeNames.STRING); // (crf)version_name
		this.setTypeExpected(index, TypeNames.INT); // (crf)v_status

		HashMap variables = new HashMap();
		variables.put(1, crfName);

		String sql = digester.getQuery("findAllWithItemDetailsGroupCRFVersionMetadataByCRFId");
		ArrayList rows = this.select(sql, variables);
		ItemGroupCrvVersionUtil item;
		ItemDataType itemDT;
		ArrayList<ItemGroupCrvVersionUtil> itemGroupCrfVersionInfo = new ArrayList<ItemGroupCrvVersionUtil>();
		for (Object row1 : rows) {
			int ind = 2;
			HashMap row = (HashMap) row1;
			item = new ItemGroupCrvVersionUtil();
			item.setItemName((String) row.get("item_name"));
			item.setCrfVersionName((String) row.get("version_name"));
			item.setCrfVersionStatus((Integer) row.get("v_status"));
			item.setGroupName((String) row.get("group_name"));
			item.setGroupOID((String) row.get("group_oid"));
			this.setTypeExpected(ind++, TypeNames.STRING); // (crf)
			this.setTypeExpected(ind++, TypeNames.INT); // (crf)item_id
			this.setTypeExpected(ind++, TypeNames.INT); // (crf)data_type
			this.setTypeExpected(ind, TypeNames.STRING); // (crf)item_oid

			item.setItemDescription((String) row.get("description"));
			item.setItemOID((String) row.get("item_oid"));
			itemDT = ItemDataType.get((Integer) row.get("data_type"));

			item.setItemDataType(itemDT.getDescription());
			item.setId((Integer) row.get("item_id"));
			itemGroupCrfVersionInfo.add(item);

		}
		return itemGroupCrfVersionInfo;
	}
}
