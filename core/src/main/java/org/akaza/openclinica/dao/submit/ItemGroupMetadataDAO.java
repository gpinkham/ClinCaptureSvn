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
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.dao.core.EntityDAO;
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

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ItemGroupMetadataDAO extends EntityDAO {
	public ItemGroupMetadataDAO(DataSource ds) {
		super(ds);
		this.getNextPKName = "getNextPK";
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_ITEM_GROUP_METADATA;
	}

	public void setTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.STRING);
		this.setTypeExpected(5, TypeNames.STRING);
		this.setTypeExpected(6, TypeNames.INT);
		this.setTypeExpected(7, TypeNames.INT);
		this.setTypeExpected(8, TypeNames.STRING);
		this.setTypeExpected(9, TypeNames.INT);
		this.setTypeExpected(10, TypeNames.INT);
		this.setTypeExpected(11, TypeNames.INT);
		this.setTypeExpected(12, TypeNames.INT);
		this.setTypeExpected(13, TypeNames.INT);
		this.setTypeExpected(14, TypeNames.BOOL);
		this.setTypeExpected(15, TypeNames.BOOL);
	}

	public Object getEntityFromHashMap(HashMap hm) {
		ItemGroupMetadataBean meta = new ItemGroupMetadataBean();
		meta.setId((Integer) hm.get("item_group_metadata_id"));
		meta.setItemGroupId((Integer) hm.get("item_group_id"));
		meta.setHeader((String) hm.get("header"));
		meta.setSubheader((String) hm.get("subheader"));
		meta.setLayout((String) hm.get("layout"));
		meta.setRepeatNum((Integer) hm.get("repeat_number"));
		meta.setRepeatMax((Integer) hm.get("repeat_max"));
		meta.setRepeatArray((String) hm.get("repeat_array"));
		meta.setRowStartNumber((Integer) hm.get("row_start_number"));
		meta.setCrfVersionId((Integer) hm.get("crf_version_id"));
		meta.setItemId((Integer) hm.get("item_id"));
		meta.setOrdinal((Integer) hm.get("ordinal"));
		meta.setBorders((Integer) hm.get("borders"));
		meta.setShowGroup((Boolean) hm.get("show_group"));
		meta.setRepeatingGroup((Boolean) hm.get("repeating_group"));
		return meta;
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase)
			throws OpenClinicaException {
		return new ArrayList();
	}

	public Collection findAll() throws OpenClinicaException {
		return new ArrayList();
	}

	public EntityBean findByPK(int id) throws OpenClinicaException {
		ItemGroupMetadataBean eb = new ItemGroupMetadataBean();
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, id);
		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (ItemGroupMetadataBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
		// return new ItemGroupMetadataBean(); // To change body of implemented
		// methods use File | Settings |
		// File Templates.;
	}

	public EntityBean findByItemAndCrfVersion(Integer itemId, Integer crfVersionId) {
		ItemGroupMetadataBean eb = new ItemGroupMetadataBean();
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, itemId);
		variables.put(2, crfVersionId);
		String sql = digester.getQuery("findByItemIdAndCrfVersionId");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (ItemGroupMetadataBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	public EntityBean create(EntityBean eb) throws OpenClinicaException {
		ItemGroupMetadataBean igMetaBean = (ItemGroupMetadataBean) eb;
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		int id = getNextPK();
		variables.put(1, id);
		variables.put(2, igMetaBean.getItemGroupId());
		variables.put(3, igMetaBean.getHeader());
		variables.put(4, igMetaBean.getSubheader());
		variables.put(5, igMetaBean.getLayout());
		variables.put(6, igMetaBean.getRepeatNum());
		variables.put(7, igMetaBean.getRepeatMax());
		variables.put(8, igMetaBean.getRepeatArray());
		variables.put(9, igMetaBean.getRowStartNumber());
		variables.put(10, igMetaBean.getCrfVersionId());
		variables.put(11, igMetaBean.getItemId());
		variables.put(12, igMetaBean.getOrdinal());
		variables.put(13, igMetaBean.getBorders());
		variables.put(14, igMetaBean.isShowGroup());

		this.execute(digester.getQuery("create"), variables);
		if (isQuerySuccessful()) {
			eb.setId(id);
		}
		return eb;

	}

	public List<ItemGroupMetadataBean> findMetaByGroupAndSection(int itemGroupId, int crfVersionId, int sectionId) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, itemGroupId);
		variables.put(2, crfVersionId);
		variables.put(3, sectionId);
		List listofMaps = this.select(digester.getQuery("findMetaByGroupAndSection"), variables);

		List<ItemGroupMetadataBean> beanList = new ArrayList<ItemGroupMetadataBean>();
		ItemGroupMetadataBean bean;
		for (Object map : listofMaps) {
			bean = (ItemGroupMetadataBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	public List<ItemGroupMetadataBean> findMetaByGroupAndSectionForPrint(int itemGroupId, int crfVersionId,
			int sectionId) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, itemGroupId);
		variables.put(2, crfVersionId);
		variables.put(3, sectionId);
		List listofMaps = this.select(digester.getQuery("findMetaByGroupAndSectionForPrint"), variables);

		List<ItemGroupMetadataBean> beanList = new ArrayList<ItemGroupMetadataBean>();
		ItemGroupMetadataBean bean;
		for (Object map : listofMaps) {
			bean = (ItemGroupMetadataBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	public EntityBean update(EntityBean eb) throws OpenClinicaException {
		return new ItemGroupMetadataBean(); // To change body of implemented
		// methods use File | Settings |
		// File Templates.
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) throws OpenClinicaException {
		return new ArrayList(); // To change body of implemented methods use
		// File | Settings | File Templates.
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) throws OpenClinicaException {
		return new ArrayList(); // To change body of implemented methods use
		// File | Settings | File Templates.
	}

	/**
	 * 
	 * @param crfVersionId
	 *            int
	 * @return boolean
	 */
	public boolean versionIncluded(int crfVersionId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);

		ArrayList al = this.select(digester.getQuery("findThisCrfVersionId"), variables);

		if (al.size() > 0) {
			HashMap h = (HashMap) al.get(0);
			if ((Integer) h.get("crf_version_id") == crfVersionId) {
				return true;
			}
		}

		return false;
	}

	@Override
	public ArrayList select(String query, HashMap variables) {
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

			// if (logger.isInfoEnabled()) {
			logger.debug("Executing dynamic query, EntityDAO.select:query " + query);
			// }
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

	// @Override
	// public ArrayList<V> select(String query, Map variables) {
	// clearSignals();
	//
	// ArrayList results = new ArrayList();
	// V value;
	// K key;
	// ResultSet rs = null;
	// Connection con = null;
	// PreparedStatementFactory psf = new PreparedStatementFactory(variables);
	// PreparedStatement ps = null;
	//
	// try {
	// con = ds.getConnection();
	// if (con.isClosed()) {
	// if (logger.isWarnEnabled())
	// logger.warn("Connection is closed: GenericDAO.select!");
	// throw new SQLException();
	// }
	//
	// ps = con.prepareStatement(query);
	//
	//
	// ps = psf.generate(ps);// enter variables here!
	// key = (K) ps.toString();
	// if((results=(V) cache.get(key))==null)
	// {
	// rs = ps.executeQuery();
	// results = this.processResultRows(rs);
	// if(results!=null){
	// cache.put(key,results);
	// }
	// }
	//
	// if (logger.isInfoEnabled()) {
	// logger.debug("Executing dynamic query, EntityDAO.select:query " + query);
	// }
	// signalSuccess();
	//
	//
	// } catch (SQLException sqle) {
	// signalFailure(sqle);
	// if (logger.isWarnEnabled()) {
	// logger.warn("Exception while executing dynamic query, GenericDAO.select: " + query + ":message: " +
	// sqle.getMessage());
	// sqle.printStackTrace();
	// }
	// } finally {
	// this.closeIfNecessary(con, rs, ps);
	// }
	// return results;
	//
	// }

	/**
	 * findByCrfVersion, added by clinovo for #121, 12/2012
	 * 
	 * @param crfVersionId
	 *            Integer
	 * @return List<ItemGroupMetadataBean>
	 */
	public List<ItemGroupMetadataBean> findByCrfVersion(Integer crfVersionId) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, crfVersionId);
		String sql = digester.getQuery("findByCrfVersionId");
		ArrayList alist = this.select(sql, variables);
		List<ItemGroupMetadataBean> beanList = new ArrayList<ItemGroupMetadataBean>();
		ItemGroupMetadataBean bean;
		for (Object map : alist) {
			bean = (ItemGroupMetadataBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

}
