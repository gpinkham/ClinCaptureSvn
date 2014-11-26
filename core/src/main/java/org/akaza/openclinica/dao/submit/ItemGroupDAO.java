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

import javax.sql.DataSource;
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

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ItemGroupDAO extends AuditableEntityDAO {

	public ItemGroupDAO(DataSource ds) {
		super(ds);
		this.getCurrentPKName = "findCurrentPKValue";
		this.getNextPKName = "getNextPK";
	}

	public ItemGroupDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
	}

	// This constructor sets up the Locale for JUnit tests; see the locale
	// member variable in EntityDAO, and its initializeI18nStrings() method
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
		this.unsetTypeExpected();
		/*
		 * item_group_id serial NOT NULL, name varchar(255), crf_id numeric NOT NULL, status_id numeric, date_created
		 * date, date_updated date, owner_id numeric, update_id numeric,
		 */
		this.setTypeExpected(1, TypeNames.INT); // item_group_id
		this.setTypeExpected(2, TypeNames.STRING); // name
		this.setTypeExpected(3, TypeNames.INT);// crf_id
		this.setTypeExpected(4, TypeNames.INT); // status_id
		this.setTypeExpected(5, TypeNames.DATE); // date_created
		this.setTypeExpected(6, TypeNames.DATE); // date_updated
		this.setTypeExpected(7, TypeNames.INT); // owner_id
		this.setTypeExpected(8, TypeNames.INT); // update_id
		this.setTypeExpected(9, TypeNames.STRING); // oc_oid

	}

	public EntityBean update(EntityBean eb) {
		ItemGroupBean formGroupBean = (ItemGroupBean) eb;
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		/*
		 * item_group_id serial NOT NULL, name varchar(255), crf_id numeric NOT NULL, status_id numeric, date_created
		 * date, date_updated date, owner_id numeric, update_id numeric,
		 */
		variables.put(1, formGroupBean.getName());
		variables.put(2, formGroupBean.getCrfId());
		variables.put(3, formGroupBean.getStatus().getId());
		variables.put(4, formGroupBean.getUpdater().getId());
		variables.put(5, formGroupBean.getId());
		this.execute(digester.getQuery("update"), variables);
		return eb;
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) throws OpenClinicaException {
		return new ArrayList();
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) throws OpenClinicaException {
		return new ArrayList();
	}

	private String getOid(ItemGroupBean itemGroupBean, String crfName, String itemGroupLabel) {

		String oid;
		try {
			oid = itemGroupBean.getOid() != null ? itemGroupBean.getOid() : itemGroupBean.getOidGenerator(ds)
					.generateOid(crfName, itemGroupLabel);
			return oid;
		} catch (Exception e) {
			throw new RuntimeException("CANNOT GENERATE OID");
		}
	}

	public String getValidOid(ItemGroupBean itemGroup, String crfName, String itemGroupLabel, ArrayList<String> oidList) {

		String oid = getOid(itemGroup, crfName, itemGroupLabel);
		logger.info(oid);
		String oidPreRandomization = oid;
		while (findByOid(oid) != null || oidList.contains(oid)) {
			oid = itemGroup.getOidGenerator(ds).randomizeOid(oidPreRandomization);
		}
		return oid;
	}

	/*
	 * name varchar(255), crf_id numeric NOT NULL, status_id numeric, date_created date, date_updated date, owner_id
	 * numeric, update_id numeric,
	 */
	public EntityBean create(EntityBean eb) {
		ItemGroupBean formGroupBean = (ItemGroupBean) eb;
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		int id = getNextPK();
		variables.put(1, id);
		variables.put(2, formGroupBean.getName());
		variables.put(3, formGroupBean.getCrfId());
		variables.put(4, formGroupBean.getStatus().getId());
		variables.put(5, formGroupBean.getOwner().getId());

		this.execute(digester.getQuery("create"), variables);
		if (isQuerySuccessful()) {
			eb.setId(id);
			eb.setActive(true);
		}
		return eb;
	}

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

	public Collection findGroupsByItemID(int ID) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, ID);
		List listofMap = this.select(digester.getQuery("findGroupsByItemID"), variables);

		List<ItemGroupBean> formGroupBs = new ArrayList<ItemGroupBean>();
		for (Object map : listofMap) {
			ItemGroupBean bean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);
			formGroupBs.add(bean);
		}
		return formGroupBs;

	}

	public EntityBean findByPK(int ID) {
		ItemGroupBean formGroupB = new ItemGroupBean();
		this.setTypesExpected();

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, ID);

		String sql = digester.getQuery("findByPK");
		ArrayList listofMap = this.select(sql, variables);
		for (Object map : listofMap) {
			formGroupB = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);

		}
		return formGroupB;
	}

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

	public ItemGroupBean findByOidAndCrf(String oid, int crfId) {
		this.unsetTypeExpected();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, oid);
		variables.put(2, crfId);
		String sql = digester.getQuery("findGroupByOidAndCrfId");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (ItemGroupBean) this.getEntityFromHashMap((HashMap) it.next());
		} else {
			return null;
		}
	}

	public List<ItemGroupBean> findGroupByCRFVersionID(int Id) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, Id);
		List listofMaps = this.select(digester.getQuery("findGroupByCRFVersionID"), variables);

		List<ItemGroupBean> beanList = new ArrayList<ItemGroupBean>();
		ItemGroupBean bean;
		for (Object map : listofMaps) {
			bean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	public ItemGroupBean findByItemAndCRFVersion(ItemBean item, CRFVersionBean crfVersion) {
		return this.findByItemIdAndCRFVersionId(item.getId(), crfVersion.getId());
	}
	
	public ItemGroupBean findByItemIdAndCRFVersionId(int itemId, int crfVersionId) {
		this.unsetTypeExpected();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, itemId);
		variables.put(2, crfVersionId);
		String sql = digester.getQuery("findByItemAndCRFVersion");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (ItemGroupBean) this.getEntityFromHashMap((HashMap) it.next());
		} else {
			return null;
		}
	}

	public List<ItemGroupBean> findOnlyGroupsByCRFVersionID(int Id) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, Id);
		List listofMaps = this.select(digester.getQuery("findOnlyGroupsByCRFVersionID"), variables);

		List<ItemGroupBean> beanList = new ArrayList<ItemGroupBean>();
		ItemGroupBean bean;
		for (Object map : listofMaps) {
			bean = (ItemGroupBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

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

	public Object getEntityFromHashMap(HashMap hm) {
		ItemGroupBean formGroupBean = new ItemGroupBean();
		super.setEntityAuditInformation(formGroupBean, hm);
		formGroupBean.setId((Integer) hm.get("item_group_id"));
		formGroupBean.setName((String) hm.get("name"));
		formGroupBean.setCrfId((Integer) hm.get("crf_id"));
		formGroupBean.setOid((String) hm.get("oc_oid"));

		return formGroupBean;
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public void deleteTestGroup(String name) {
		HashMap variables = new HashMap();
		variables.put(1, name);
		this.execute(digester.getQuery("deleteTestGroup"), variables);
	}

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
			con = ds.getConnection();
			if (con.isClosed()) {
				if (logger.isWarnEnabled())
					logger.warn("Connection is closed: GenericDAO.select!");
				throw new SQLException();
			}

			ps = con.prepareStatement(query);

			ps = psf.generate(ps);// enter variables here!
			key = ps.toString();
			if ((results = (ArrayList) cache.get(key)) == null) {
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
}
