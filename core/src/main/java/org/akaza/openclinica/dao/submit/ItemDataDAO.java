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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

/**
 * ItemDataDAO.java, the equivalent to AnswerDAO in the original code base. Modified by ywang (12-07-2007) to convert
 * date_format string pattern of item value when item data type is date,
 * 
 * @author thickerson
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemDataDAO extends AuditableEntityDAO {

	public static final int INT_13 = 13;

	private void setQueryNames() {
		getCurrentPKName = "getCurrentPK";
		getNextPKName = "getNextPK";
	}

	/**
	 * This constructor sets up the DataSource for DAO.
	 * 
	 * @param ds
	 *            <code>DataSource</code> that will be assigned to DAO.
	 */
	public ItemDataDAO(DataSource ds) {
		super(ds);
		setQueryNames();
		if (this.locale == null) {
			this.locale = ResourceBundleProvider.getLocale(); // locale still might be null.
		}
	}

	/**
	 * This constructor sets up the DataSource and Connection for DAO.
	 * 
	 * @param ds
	 *            <code>DataSource</code> that will be assigned to DAO.
	 * @param con
	 *            <code>Connection</code> that will be assigned to DAO.
	 */
	public ItemDataDAO(DataSource ds, Connection con) {
		super(ds, con);
		setQueryNames();
	}

	/**
	 * This constructor sets up the Locale and DataSource.
	 * 
	 * @param ds
	 *            <code>DataSource</code> that will be assigned to DAO.
	 * @param locale
	 *            <code>Locale</code> that will be assigned to DAO.
	 */
	public ItemDataDAO(DataSource ds, Locale locale) {
		super(ds);
		setQueryNames();
		if (locale != null) {
			this.locale = locale;
		} else {
			this.locale = ResourceBundleProvider.getLocale();
		}
		if (this.locale != null) {
			local_df_string = ResourceBundleProvider.getFormatBundle(this.locale).getString("date_format_string");
		}
	}

	/**
	 * This constructor sets up the DAODigister and DataSource.
	 * 
	 * @param ds
	 *            <code>ItemDataDAO</code> that will be assigned to DAO.
	 * @param digester
	 *            <code>ItemDataDAO</code> that will be assigned to DAO.
	 */
	public ItemDataDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
		setQueryNames();
	}

	/**
	 * This constructor sets up the Locale for JUnit tests. See the locale member variable in EntityDAO, and its
	 * initializeI18nStrings() method.
	 * 
	 * @param ds
	 *            is <code>DataSource</code> that will be assigned to this DAO.
	 * @param digester
	 *            is <code>DAODigister</code> that will be assigned to this DAO.
	 * @param locale
	 *            is <code>Locale</code> that will be set to this DAO.
	 */
	public ItemDataDAO(DataSource ds, DAODigester digester, Locale locale) {

		this(ds, digester);
		this.locale = locale;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_ITEMDATA;
	}

	/**
	 * This method is used to set expected types to compare rows in DB and field in <code>ItemDataBean</code> object.
	 */
	@Override
	public void setTypesExpected() {

		int index = 1;

		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT); // item data id
		this.setTypeExpected(index++, TypeNames.INT); // item id
		this.setTypeExpected(index++, TypeNames.INT); // event crf id
		this.setTypeExpected(index++, TypeNames.INT); // status id
		this.setTypeExpected(index++, TypeNames.STRING); // value
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date created
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // date updated
		this.setTypeExpected(index++, TypeNames.INT); // owner id
		this.setTypeExpected(index++, TypeNames.INT); // update id
		this.setTypeExpected(index++, TypeNames.INT); // ordinal
		this.setTypeExpected(index++, TypeNames.INT); // old status id
		this.setTypeExpected(index, TypeNames.BOOL); // sdv
	}

	/**
	 * This method is used to update itemData in the database.
	 * 
	 * @param eb
	 *            <code>ItemDataBean</code> that will be updated in the database.
	 * @return updated item.
	 */
	public EntityBean update(EntityBean eb) {
		return update(eb, null);
	}

	/**
	 * This method is used to update itemData in the database.
	 * 
	 * @param eb
	 *            <code>ItemDataBean</code> that will be updated in the database.
	 * @param con
	 *            <code>Connection</code> to the database. It can be null.
	 * @return <code>ItemDataBean</code> that was updated.
	 */
	public EntityBean update(EntityBean eb, Connection con) {

		ItemDataBean idb = (ItemDataBean) eb;

		// Convert to oc_date_format_string pattern before
		// inserting into database
		ItemDataType dataType = getDataType(idb.getItemId());
		if (dataType.equals(ItemDataType.DATE)) {
			idb.setValue(Utils.convertedItemDateValue(idb.getValue(), local_df_string, oc_df_string, locale));
		} else if (dataType.equals(ItemDataType.PDATE)) {
			idb.setValue(formatPDate(idb.getValue()));
		}

		idb.setActive(false);
		int index = 1;

		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(index++, idb.getEventCRFId());
		variables.put(index++, idb.getItemId());
		variables.put(index++, idb.getStatus().getId());
		variables.put(index++, idb.getValue());
		variables.put(index++, idb.getUpdaterId());
		variables.put(index++, idb.getOrdinal());
		variables.put(index++, idb.getOldStatus().getId());
		variables.put(index++, idb.isSdv());
		variables.put(index, idb.getId());
		this.execute(digester.getQuery("update"), variables, con);
		if (isQuerySuccessful()) {
			idb.setActive(true);
		}

		return idb;
	}

	/**
	 * This method id used to update only value for <code>ItemDataBean</code>. Date of update and updater will be
	 * written to DB too.
	 * 
	 * @param eb
	 *            <code>EntityBean</code> that will be updated.
	 * @return idb updated <code>ItemDataBean</code>.
	 */
	public EntityBean updateValue(EntityBean eb) {

		ItemDataBean idb = (ItemDataBean) eb;

		// Convert to oc_date_format_string pattern before
		// inserting into database
		ItemDataType dataType = getDataType(idb.getItemId());
		if (dataType.equals(ItemDataType.DATE)) {
			idb.setValue(Utils.convertedItemDateValue(idb.getValue(), local_df_string, oc_df_string, locale));
		} else if (dataType.equals(ItemDataType.PDATE)) {
			idb.setValue(formatPDate(idb.getValue()));
		}

		idb.setActive(false);
		int index = 1;

		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(index++, idb.getStatus().getId());
		variables.put(index++, idb.getValue());
		variables.put(index++, idb.getUpdaterId());
		variables.put(index, idb.getId());
		this.execute(digester.getQuery("updateValue"), variables);

		if (isQuerySuccessful()) {
			idb.setActive(true);
		}

		return idb;
	}

	/**
	 * This method is used to update itemDataBeans with status Removed. Following rows will be updated by query: status,
	 * value, update_id.
	 * 
	 * @param eb
	 *            <code>ItemDataBean</code> that will be updated
	 * @return updated <code>ItemDataBean</code>.
	 */
	public EntityBean updateValueForRemoved(EntityBean eb) {
		ItemDataBean idb = (ItemDataBean) eb;

		// Convert to oc_date_format_string pattern before
		// inserting into database
		ItemDataType dataType = getDataType(idb.getItemId());
		if (dataType.equals(ItemDataType.DATE)) {
			idb.setValue(Utils.convertedItemDateValue(idb.getValue(), local_df_string, oc_df_string, locale));
		} else if (dataType.equals(ItemDataType.PDATE)) {
			idb.setValue(formatPDate(idb.getValue()));
		}

		idb.setActive(false);
		int index = 1;

		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(index++, idb.getStatus().getId());
		variables.put(index++, idb.getValue());
		variables.put(index++, idb.getUpdaterId());
		variables.put(index, idb.getId());
		this.execute(digester.getQuery("updateValueForRemoved"), variables);

		if (isQuerySuccessful()) {
			idb.setActive(true);
		}

		return idb;
	}

	/**
	 * This method will update only item data status.
	 * 
	 * @param eb
	 *            <code>ItemDataBean</code> that will be updated.
	 * @return updated <code>ItemDataBean</code>
	 */
	public EntityBean updateStatus(EntityBean eb) {

		ItemDataBean idb = (ItemDataBean) eb;
		idb.setActive(false);
		int index = 1;

		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(index++, idb.getStatus().getId());
		variables.put(index, idb.getId());
		this.execute(digester.getQuery("updateStatus"), variables);

		if (isQuerySuccessful()) {
			idb.setActive(true);
		}

		return idb;
	}

	/**
	 * This will update item data value, for dates in different formats.
	 * 
	 * @param eb
	 *            <code>ItemDataBeean</code> that will be updated.
	 * @param currentDfString
	 *            - format of date (for example "yyyy-MM-dd").
	 * @return updated <code>ItemDataBean</code>.
	 */
	public EntityBean updateValue(EntityBean eb, String currentDfString) {
		return updateValue(eb, currentDfString, null);
	}

	/**
	 * This will update item data value, for dates in different formats.
	 * 
	 * @param eb
	 *            <code>ItemDataBeean</code> that will be updated.
	 * @param currentDfString
	 *            - format of date (for example "yyyy-MM-dd").
	 * @param con
	 *            <code>Connection</code> that will be set to this DAO.
	 * @return updated <code>ItemDataBean</code>.
	 */
	public EntityBean updateValue(EntityBean eb, String currentDfString, Connection con) {
		ItemDataBean idb = (ItemDataBean) eb;

		// Convert to oc_date_format_string pattern before
		// inserting into database
		idb.setValue(Utils.convertedItemDateValue(idb.getValue(), currentDfString, oc_df_string, locale));

		idb.setActive(false);
		int index = 1;
		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(index++, idb.getStatus().getId());
		variables.put(index++, idb.getValue());
		variables.put(index++, idb.getUpdaterId());
		variables.put(index, idb.getId());
		this.execute(digester.getQuery("updateValue"), variables, con);

		if (isQuerySuccessful()) {
			idb.setActive(true);
		}

		return idb;
	}

	/**
	 * This method will update only updater and update date for item data.
	 * 
	 * @param eb
	 *            - <code>ItemDataBean</code> that will be updated.
	 * @return updated <code>ItemDataBean</code>.
	 */
	public EntityBean updateUser(EntityBean eb) {
		ItemDataBean idb = (ItemDataBean) eb;

		idb.setActive(false);
		int index = 1;

		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(index++, idb.getUpdaterId());
		variables.put(index, idb.getId());
		this.execute(digester.getQuery("updateUser"), variables);

		if (isQuerySuccessful()) {
			idb.setActive(true);
		}

		return idb;
	}

	/**
	 * This method used to create new item data.
	 * 
	 * @param eb
	 *            <code>ItemDataBean</code> that will be written to the DB.
	 * @return <code>ItemDataBean</code> that will be created.
	 */
	public EntityBean create(EntityBean eb) {
		return create(eb, null);
	}

	/**
	 * This method used to create new item data.
	 * 
	 * @param eb
	 *            <code>ItemDataBean</code> that will be written to the DB.
	 * @param con
	 *            <code>Connection</code> connection that will be set for this DAO.
	 * @return <code>ItemDataBean</code> that will be created.
	 */
	public EntityBean create(EntityBean eb, Connection con) {
		ItemDataBean idb = (ItemDataBean) eb;
		// Convert to oc_date_format_string pattern before
		// inserting into database
		ItemDataType dataType = getDataType(idb.getItemId());
		if (dataType.equals(ItemDataType.DATE)) {
			idb.setValue(Utils.convertedItemDateValue(idb.getValue(), local_df_string, oc_df_string, locale));
		} else if (dataType.equals(ItemDataType.PDATE)) {
			idb.setValue(formatPDate(idb.getValue()));
		}

		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		int id = getNextPK();
		int index = 1;
		variables.put(index++, id);
		variables.put(index++, idb.getEventCRFId());
		variables.put(index++, idb.getItemId());
		variables.put(index++, idb.getStatus().getId());
		variables.put(index++, idb.getValue());
		variables.put(index++, idb.getOwnerId());
		variables.put(index++, idb.getOrdinal());
		variables.put(index, idb.getStatus().getId());
		this.execute(digester.getQuery("create"), variables, con);
		if (isQuerySuccessful()) {
			idb.setId(id);
		}

		return idb;
	}

	/**
	 * This method is used to create new item data already with information about date updated and updater.
	 * 
	 * @param eb
	 *            <code>ItemDataBean</code> that will be inserted to the database.
	 * @return inserted <code>ItemDataBean</code>.
	 */
	public EntityBean upsert(EntityBean eb) {
		ItemDataBean idb = (ItemDataBean) eb;
		// Convert to oc_date_format_string pattern before
		// inserting into database
		ItemDataType dataType = getDataType(idb.getItemId());
		if (dataType.equals(ItemDataType.DATE)) {
			idb.setValue(Utils.convertedItemDateValue(idb.getValue(), local_df_string, oc_df_string, locale));
		} else if (dataType.equals(ItemDataType.PDATE)) {
			idb.setValue(formatPDate(idb.getValue()));
		}

		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		int id = getNextPK();
		int index = 1;
		variables.put(index++, id);
		variables.put(index++, idb.getEventCRFId());
		variables.put(index++, idb.getItemId());
		variables.put(index++, idb.getStatus().getId());
		variables.put(index++, idb.getValue());
		variables.put(index++, idb.getOwnerId());
		variables.put(index++, idb.getOrdinal());
		variables.put(index, idb.getUpdaterId());
		this.execute(digester.getQuery("upsert"), variables);

		if (isQuerySuccessful()) {
			idb.setId(id);
		}

		return idb;
	}

	/**
	 * This method is used to check if item data type is a date.
	 * 
	 * @param itemId
	 *            ID of <code>ItemBean</code> that will be checked.
	 * @return Returns the dataType.
	 */
	public ItemDataType getDataType(int itemId) {
		ItemDAO itemDAO = new ItemDAO(this.getDs());
		ItemBean itemBean = (ItemBean) itemDAO.findByPK(itemId);
		return itemBean.getDataType();
	}

	/**
	 * This method is used to check if entered value match partial date pattern and update value according CC date
	 * format.
	 * 
	 * @param pDate
	 *            value that was entered in pDate format.
	 * @return formated date.
	 */
	public String formatPDate(String pDate) {
		String temp = "";
		String yearMonthFormat = StringUtil.parseDateFormat(ResourceBundleProvider.getFormatBundle(locale).getString(
				"date_format_year_month"));
		String yearFormat = StringUtil.parseDateFormat(ResourceBundleProvider.getFormatBundle(locale).getString(
				"date_format_year"));
		String dateFormat = StringUtil.parseDateFormat(ResourceBundleProvider.getFormatBundle(locale).getString(
				"date_format_string"));
		try {
			if (StringUtil.isFormatDate(pDate, "yyyy-MM-dd") || StringUtil.isFormatDate(pDate, "yyyy-MM")
					|| StringUtil.isFormatDate(pDate, "yyyy")) {
				temp = pDate;
			} else if (StringUtil.isFormatDate(pDate, dateFormat, locale)) {
				temp = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat(dateFormat, locale).parse(pDate));
			} else if (StringUtil.isPartialYear(pDate, yearFormat, locale)) {
				temp = pDate;
			} else if (StringUtil.isPartialYearMonth(pDate, yearMonthFormat, locale)) {
				temp = new SimpleDateFormat("yyyy-MM").format(new SimpleDateFormat(yearMonthFormat, locale)
						.parse(pDate));
			}
		} catch (Exception ex) {
			logger.warn("Partial Date Parsing Exception........");
		}
		return temp;
	}

	/**
	 * This method is used to update format of partial date.
	 * 
	 * @param pDate
	 *            value that was entered for item.
	 * @return formated date.
	 */
	public String reFormatPDate(String pDate) {
		String temp = "";
		String yearMonthFormat = StringUtil.parseDateFormat(ResourceBundleProvider.getFormatBundle(locale).getString(
				"date_format_year_month"));
		String yearFormat = StringUtil.parseDateFormat(ResourceBundleProvider.getFormatBundle(locale).getString(
				"date_format_year"));
		String dateFormat = StringUtil.parseDateFormat(ResourceBundleProvider.getFormatBundle(locale).getString(
				"date_format_string"));
		try {
			if (StringUtil.isFormatDate(pDate, dateFormat, locale)
					|| StringUtil.isFormatDate(pDate, yearMonthFormat, locale)
					|| StringUtil.isFormatDate(pDate, yearFormat, locale)) {
				temp = pDate;
			} else if (StringUtil.isFormatDate(pDate, "yyyy-MM-dd")) {
				temp = new SimpleDateFormat(dateFormat, locale).format(new SimpleDateFormat("yyyy-MM-dd").parse(pDate));
			} else if (StringUtil.isPartialYear(pDate, "yyyy")) {
				temp = pDate;
			} else if (StringUtil.isPartialYearMonth(pDate, "yyyy-MM")) {
				temp = new SimpleDateFormat(yearMonthFormat, locale).format(new SimpleDateFormat("yyyy-MM")
						.parse(pDate));
			}
		} catch (Exception ex) {
			logger.warn("Partial Date Parsing Exception........");
		}
		return temp;
	}

	/**
	 * This method is used to get values from HashMap and create <code>ItemDataBean</code>.
	 * 
	 * @param hm
	 *            <code>HashMap</code> with values that will be written to <code>ItemDataBean</code>.
	 * @return created <code>ItemDataBean</code>.
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		ItemDataBean eb = new ItemDataBean();
		this.setEntityAuditInformation(eb, hm);
		eb.setId((Integer) hm.get("item_data_id"));
		eb.setEventCRFId((Integer) hm.get("event_crf_id"));
		eb.setItemId((Integer) hm.get("item_id"));
		eb.setValue((String) hm.get("value"));
		// Since "getEntityFromHashMap" only be used for find
		// right now,
		// convert item date value to local_date_format_string pattern once
		// fetching out from database
		ItemDataType dataType = getDataType(eb.getItemId());
		if (dataType.equals(ItemDataType.DATE)) {
			eb.setValue(Utils.convertedItemDateValue(eb.getValue(), oc_df_string, local_df_string, locale));
		} else if (dataType.equals(ItemDataType.PDATE)) {
			eb.setValue(reFormatPDate(eb.getValue()));
		}
		eb.setStatus(Status.get((Integer) hm.get("status_id")));
		eb.setOrdinal((Integer) hm.get("ordinal"));
		eb.setOldStatus(Status.get(hm.get("old_status_id") == null ? 1 : (Integer) hm.get("old_status_id")));
		Boolean sdv = (Boolean) hm.get("sdv");
		eb.setSdv(sdv != null ? sdv : false);
		return eb;
	}

	/**
	 * This method is used to get all <code>ItemDataBean</code>s by Study ID, Item IDs and Item Group ID.
	 * 
	 * @param studyEventId
	 *            ID of Study Event in which ItemData should be found.
	 * @param itemOid
	 *            ID of Item that should be found.
	 * @param itemGroupOid
	 *            and ID o Item Group in which this item should exist.
	 * @return List of <code>ItemDataBean</code>s.
	 */
	public List<ItemDataBean> findByStudyEventAndOids(Integer studyEventId, String itemOid, String itemGroupOid) {
		setTypesExpected();

		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		int index = 1;
		variables.put(index++, studyEventId);
		variables.put(index++, itemOid);
		variables.put(index++, itemGroupOid);
		variables.put(index++, Status.DELETED.getId());
		variables.put(index, Status.AUTO_DELETED.getId());

		return this.executeFindAllQuery("findByStudyEventAndOIDs", variables);
	}

	/**
	 * This method is used to get all <code>ItemDataBeans</code> from database.
	 * 
	 * @return <code>Collection</code> of all ItemDataBeans.
	 */
	public Collection<ItemDataBean> findAll() {
		setTypesExpected();

		ArrayList alist = this.select(digester.getQuery("findAll"));
		ArrayList<ItemDataBean> al = new ArrayList<ItemDataBean>();
		for (Object anAlist : alist) {
			ItemDataBean eb = (ItemDataBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	/**
	 * This method is used to override appropriate method in AuditableEntityDAO.
	 * 
	 * @param strOrderByColumn
	 *            <code>String</code>.
	 * @param blnAscendingSort
	 *            <code>boolean</code>.
	 * @param strSearchPhrase
	 *            <code>String</code>.
	 * @return new <code>ArrayList</code>
	 */
	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * This method is used to search item data by ID.
	 * 
	 * @param id
	 *            <code>ItemDataBean</code> ID.
	 * @return <code>ItemDataBean</code> that was found.
	 */
	public EntityBean findByPK(int id) {
		ItemDataBean eb = new ItemDataBean();
		this.setTypesExpected();

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (ItemDataBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	/**
	 * This method returns list of <code>ItemDataBean</code>s that were created in the following year.
	 * 
	 * @param itemId
	 *            <code>List</code> of IDs.
	 * @param year
	 *            <code>int</code> number of year in which items were created.
	 * @return <code>List</code> of <code>ItemDataBean</code>s.
	 */
	public ItemDataBean findByPKAndYear(Integer itemId, int year) {

		ItemDataBean ib = new ItemDataBean();
		this.setTypesExpected();

		if (itemId == null) {
			itemId = 0;
		}

		int index = 1;
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();

		variables.put(index++, itemId);
		variables.put(index++, year);
		variables.put(index, year);

		String sql = digester.getQuery("findByPKAndYear");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			ib = (ItemDataBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return ib;
	}

	/**
	 * This method is used to remove item data from database.
	 * 
	 * @param itemDataId
	 *            ID by which item data will be found.
	 */
	public void delete(int itemDataId) {
		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(1, itemDataId);

		this.execute(digester.getQuery("delete"), variables);
	}

	/**
	 * This method is used to delete rows from item_data_map table, using itemDataId.
	 * 
	 * @param itemDataId
	 *            ID that will be used to delete rows from item_data_map table.
	 */
	public void deleteDnMap(int itemDataId) {
		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(1, itemDataId);

		this.execute(digester.getQuery("deleteDn"), variables);
	}

	/**
	 * This method is used to override appropriate method in AuditableEntityDAO.
	 * 
	 * @param objCurrentUser
	 *            <code>Object</code>.
	 * @param intActionType
	 *            <code>int</code>.
	 * @param strOrderByColumn
	 *            <code>String</code>.
	 * @param blnAscendingSort
	 *            <code>boolean</code>.
	 * @param strSearchPhrase
	 *            <code>String</code>.
	 * @return new <code>ArrayList</code>.
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * This method is used to override appropriate method in AuditableEntityDAO.
	 * 
	 * @param objCurrentUser
	 *            <code>Object</code>.
	 * @param intActionType
	 *            <code>int</code>.
	 * @return new <code>ArrayList</code>.
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	/**
	 * This method is used to find all ItemDataBeans by section ID and Event CRF ID.
	 * 
	 * @param sectionId
	 *            ID of section where item should be found.
	 * @param eventCRFId
	 *            ID of event CRF where item should be found.
	 * @return list of <code>ItemDataBean</code>s.
	 */
	public ArrayList<ItemDataBean> findAllBySectionIdAndEventCRFId(int sectionId, int eventCRFId) {
		setTypesExpected();
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		int index = 1;
		variables.put(index++, sectionId);
		variables.put(index, eventCRFId);

		return this.executeFindAllQuery("findAllBySectionIdAndEventCRFId", variables);
	}

	/**
	 * This method is used to find all active ItemDataBeans by section ID and Event CRF ID.
	 * 
	 * @param sectionId
	 *            ID of section where item should be found.
	 * @param eventCRFId
	 *            ID of event CRF where item should be found.
	 * @return ArrayList of <code>ItemDataBean</code>s.
	 */
	public ArrayList<ItemDataBean> findAllActiveBySectionIdAndEventCRFId(int sectionId, int eventCRFId) {
		setTypesExpected();
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		int index = 1;
		variables.put(index++, sectionId);
		variables.put(index, eventCRFId);

		return this.executeFindAllQuery("findAllActiveBySectionIdAndEventCRFId", variables);
	}

	/**
	 * This method is used to search all item data for specific Event CRF.
	 * 
	 * @param eventCRFId
	 *            by which item data will be searched.
	 * @return List of <code>ItemDataBean</code>s.
	 */
	public ArrayList<ItemDataBean> findAllByEventCRFId(int eventCRFId) {
		setTypesExpected();
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, eventCRFId);

		return this.executeFindAllQuery("findAllByEventCRFId", variables);
	}

	/**
	 * This method is used to search all item data for specific Event CRF and Item with status != deleted.
	 * 
	 * @param eventCRFId
	 *            by which item data will be searched.
	 * @param itemId
	 *            by which item data will be searched.
	 * @return List of <code>ItemDataBean</code>s.
	 */
	public ArrayList<ItemDataBean> findAllByEventCRFIdAndItemId(int eventCRFId, int itemId) {
		setTypesExpected();
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		int index = 1;
		variables.put(index++, eventCRFId);
		variables.put(index, itemId);

		return this.executeFindAllQuery("findAllByEventCRFIdAndItemId", variables);
	}

	/**
	 * This method is used to search all item data for specific Event CRF and Item with any status.
	 * 
	 * @param eventCRFId
	 *            by which item data will be searched.
	 * @param itemId
	 *            by which item data will be searched.
	 * @return List of <code>ItemDataBean</code>s.
	 */
	public ArrayList<ItemDataBean> findAllByEventCRFIdAndItemIdNoStatus(int eventCRFId, int itemId) {
		setTypesExpected();
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		int index = 1;
		variables.put(index++, eventCRFId);
		variables.put(index, itemId);

		return this.executeFindAllQuery("findAllByEventCRFIdAndItemIdNoStatus", variables);
	}

	/**
	 * This method is used to search all items with parameter required = true for specific EventCRF for which data was
	 * not entered.
	 * 
	 * @param eventCRFId
	 *            by which item data will be searched.
	 * @param crfVersionId
	 *            by which item data will be searched.
	 * @return List of <code>ItemDataBean</code>s.
	 */
	public ArrayList<ItemDataBean> findAllBlankRequiredByEventCRFId(int eventCRFId, int crfVersionId) {
		setTypesExpected();
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		int index = 1;
		variables.put(index++, eventCRFId);
		variables.put(index, crfVersionId);

		return this.executeFindAllQuery("findAllBlankRequiredByEventCRFId", variables);
	}

	/**
	 * This method is used to update status for all items in the specific EventCRF.
	 * 
	 * @param eventCRF
	 *            <code>EvetnCRFBean</code> id from which will be used for search.
	 * @param s
	 *            <code>Status</code> that will be set to all item data rows.
	 */
	public void updateStatusByEventCRF(EventCRFBean eventCRF, Status s) {
		updateStatusByEventCRF(eventCRF, s, null);
	}

	/**
	 * This method is used to update status for all items in the specific EventCRF.
	 * 
	 * @param eventCRF
	 *            <code>EvetnCRFBean</code> id from which will be used for search.
	 * @param s
	 *            <code>Status</code> that will be set to all item data rows.
	 * @param con
	 *            <code>Connection</code> that will be set to DAO.
	 */
	public void updateStatusByEventCRF(EventCRFBean eventCRF, Status s, Connection con) {
		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		int index = 1;
		variables.put(index++, s.getId());
		variables.put(index, eventCRF.getId());

		String sql = digester.getQuery("updateStatusByEventCRF");

		execute(sql, variables, con);
	}

	/**
	 * This method is used to find all item data for specific item in a specific event.
	 * 
	 * @param itemId
	 *            by which item data will be searched.
	 * @param eventCRFId
	 *            by which item data will be searched.
	 * @return <code>ItemDataBean</code> that was found.
	 */
	public ItemDataBean findByItemIdAndEventCRFId(int itemId, int eventCRFId) {
		setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		int index = 1;
		variables.put(index++, itemId);
		variables.put(index, eventCRFId);

		EntityBean eb = this.executeFindByPKQuery("findByItemIdAndEventCRFId", variables);

		if (!eb.isActive()) {
			return new ItemDataBean();
		} else {
			return (ItemDataBean) eb;
		}
	}

	/**
	 * This method is used to find all item data for specific item in a specific event by ordinal.
	 * 
	 * @param itemId
	 *            by which item data will be searched.
	 * @param eventCRFId
	 *            by which item data will be searched.
	 * @param ordinal
	 *            by which item will be found.
	 * @return <code>ItemDataBean</code> that was found.
	 */
	public ItemDataBean findByItemIdAndEventCRFIdAndOrdinal(int itemId, int eventCRFId, int ordinal) {
		setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		int index = 1;
		variables.put(index++, itemId);
		variables.put(index++, eventCRFId);
		variables.put(index, ordinal);

		EntityBean eb = this.executeFindByPKQuery("findByItemIdAndEventCRFIdAndOrdinal", variables);

		if (!eb.isActive()) {
			return new ItemDataBean();
		} else {
			return (ItemDataBean) eb;
		}
	}

	/**
	 * This function is used to find count of all required items in a specific Event CRFs.
	 * 
	 * @param ecb
	 *            <code>EventCRFBean</code> in which items will be found.
	 * @return list of <code>ItemDataBean</code>s.
	 */
	public int findAllRequiredByEventCRFId(EventCRFBean ecb) {
		setTypesExpected();
		int answer = 0;
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, ecb.getId());
		String sql = digester.getQuery("findAllRequiredByEventCRFId");
		ArrayList rows = this.select(sql, variables);

		if (rows.size() > 0) {
			answer = rows.size();
		}

		return answer;
	}

	/**
	 * Gets the maximum ordinal for item data in a given item group in a given section and event crf.
	 * 
	 * @param ecb
	 *            <code>EventCRFBean</code> for which max ordinal will be found.
	 * @param sb
	 *            <code>SectionBean</code> for which max ordinal will be found.
	 * @param igb
	 *            <code>ItemGroupBean</code> for which max ordinal will be found.
	 * 
	 * @return maximal ordinal for group.
	 */
	public int getMaxOrdinalForGroup(EventCRFBean ecb, SectionBean sb, ItemGroupBean igb) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		int index = 1;
		variables.put(index++, ecb.getId());
		variables.put(index++, sb.getId());
		variables.put(index, igb.getId());

		ArrayList alist = this.select(digester.getQuery("getMaxOrdinalForGroup"), variables);
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			try {
				HashMap hm = (HashMap) it.next();
				return (Integer) hm.get("max_ord");
			} catch (Exception e) {
				logger.warn("An Exception was thrown while performing getMaxOrdinalForGroup........");
			}
		}

		return 0;
	}

	/**
	 * Gets the maximum ordinal for item data in a given item group and event crf.
	 * 
	 * @param itemGroupOid
	 *            OID of item group for which max ordinal will be calculated.
	 * @param eventCrfId
	 *            ID of <code>EventCRFBean</code>.
	 * @return maximum ordinal for item data in a given item group and event crf
	 */
	public int getMaxOrdinalForGroupByGroupOID(String itemGroupOid, int eventCrfId) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.STRING);

		HashMap variables = new HashMap(1);
		variables.put(1, eventCrfId);
		variables.put(2, itemGroupOid);

		ArrayList alist = this.select(digester.getQuery("getMaxOrdinalForGroupByGroupOID"), variables);
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			try {
				HashMap hm = (HashMap) it.next();
				return (Integer) hm.get("max_ord");
			} catch (Exception e) {
				logger.warn("An Exception was thrown while performing getMaxOrdinalForGroupByGroupOID........");
			}
		}

		return 0;
	}

	/**
	 * Gets the maximum ordinal for item data in a given item bean and event crf.
	 * 
	 * @param ib
	 *            OID of item for which max ordinal will be calculated.
	 * @param ec
	 *            <code>EventCRFBean</code>.
	 * @return maximum ordinal for item data in a given item and event crf
	 */
	public int getMaxOrdinalForGroupByItemAndEventCrf(ItemBean ib, EventCRFBean ec) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		int index = 1;
		variables.put(index++, ib.getId());
		variables.put(index, ec.getId());

		ArrayList alist = this.select(digester.getQuery("getMaxOrdinalForGroupByItemAndEventCrf"), variables);
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			try {
				HashMap hm = (HashMap) it.next();
				return (Integer) hm.get("max_ord");
			} catch (Exception e) {
				logger.warn("An Exception was thrown while performing getMaxOrdinalForGroupByItemAndEventCrf........");
			}
		}

		return 0;
	}

	/**
	 * Check if item data exist in the DB. !!!
	 * 
	 * @param itemId
	 *            <code>int</code>.
	 * @param ordinalForRepeatingGroupField
	 *            <code>int</code>.
	 * @param eventCrfId
	 *            <code>int</code>.
	 * @return <code>boolean</code>.
	 */
	public boolean isItemExists(int itemId, int ordinalForRepeatingGroupField, int eventCrfId) {

		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.INT);

		int varInd = 1;
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(varInd++, itemId);
		variables.put(varInd++, ordinalForRepeatingGroupField);
		variables.put(varInd, eventCrfId);

		ArrayList alist = this.select(digester.getQuery("isItemExists"), variables);
		Iterator it = alist.iterator();
		return it.hasNext();

	}

	/**
	 * This function is used get count of items in group.
	 * 
	 * @param itemId
	 *            ID of item in this group.
	 * @param eventcrfId
	 *            <code>int</code> ID of CRF that should contain this group.
	 * @return <code>int</code> size of group.
	 */
	public int getGroupSize(int itemId, int eventcrfId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, itemId);
		variables.put(2, eventcrfId);

		ArrayList alist = this.select(digester.getQuery("getGroupSize"), variables);
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return 0;
		}
	}

	/**
	 * Find item values by OID of item.
	 * 
	 * @param itoid
	 *            - item OID.
	 * @return list of values.
	 */
	public List<String> findValuesByItemOID(String itoid) {
		List<String> vals = new ArrayList<String>();
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.STRING);
		HashMap<Integer, String> variables = new HashMap<Integer, String>();
		variables.put(1, itoid);
		ArrayList alist = this.select(digester.getQuery("findValuesByItemOID"), variables);
		for (Object anAlist : alist) {
			vals.add((String) ((HashMap) anAlist).get("value"));
		}
		return vals;
	}

	/**
	 * Method returns quantity of items that need to be SDV.
	 * 
	 * @param eventCrfId
	 *            int
	 * @return int
	 */
	public int getCountOfItemsToSDV(int eventCrfId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, eventCrfId);

		ArrayList alist = this.select(digester.getQuery("countOfItemsToSDV"), variables);
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return 0;
		}
	}

	/**
	 * Method unsdv fields in the item_data table when crf metadata was changed.
	 *
	 * @param crfVersionId
	 *            int
	 * @return boolean
	 */
	public boolean unsdvItemDataWhenCRFMetadataWasChanged(int crfVersionId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, crfVersionId);

		execute(digester.getQuery("unsdvItemDataWhenCRFMetadataWasChanged"), variables);

		return isQuerySuccessful();
	}

	/**
	 * SDV crf items.
	 *
	 * @param eventCrfId
	 *            int
	 * @param userId
	 *            user id
	 * @param sdv
	 *            boolean
	 * @return boolean
	 */
	public boolean sdvCrfItems(int eventCrfId, int userId, boolean sdv) {
		return sdvCrfItems(eventCrfId, userId, sdv, null);
	}

	/**
	 * SDV crf items.
	 * 
	 * @param eventCrfId
	 *            int
	 * @param userId
	 *            user id
	 * @param sdv
	 *            boolean
	 * @param con
	 *            Connection
	 * @return boolean
	 */
	public boolean sdvCrfItems(int eventCrfId, int userId, boolean sdv, Connection con) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap nullVars = new HashMap();
		HashMap variables = new HashMap();
		int ind = 1;
		variables.put(ind++, sdv);
		variables.put(ind++, userId);
		variables.put(ind, eventCrfId);

		execute(digester.getQuery("sdvCrfItems"), variables, nullVars, con);

		return isQuerySuccessful();
	}

	/**
	 * Returns list of items that are required to be SDV.
	 * 
	 * @param eventCrfId
	 *            int
	 * @return Map
	 */
	public List<DisplayItemBean> getListOfItemsToSDV(int eventCrfId) {
		List<DisplayItemBean> result = new ArrayList<DisplayItemBean>();
		setTypesExpected();
		int ind = INT_13;
		setTypeExpected(ind++, TypeNames.INT); // item_form_metadata_id
		setTypeExpected(ind++, TypeNames.BOOL); // repeating
		setTypeExpected(ind, TypeNames.INT); // section
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, eventCrfId);
		ArrayList rows = select(digester.getQuery("itemsToSDV"), variables);
		for (Object row : rows) {
			DisplayItemBean dib = new DisplayItemBean();

			ItemDataBean itemDataBean = (ItemDataBean) this.getEntityFromHashMap((HashMap) row);
			dib.setData(itemDataBean);

			Integer metadataId = (Integer) ((HashMap) row).get("item_form_metadata_id");
			Integer sectionId = (Integer) ((HashMap) row).get("section");
			ItemFormMetadataBean ifmb = new ItemFormMetadataBean();
			ifmb.setSectionId(sectionId != null ? sectionId : 0);
			ifmb.setId(metadataId != null ? metadataId : 0);
			ifmb.setSdvRequired(true);
			dib.setMetadata(ifmb);

			Boolean repeating = (Boolean) ((HashMap) row).get("repeating");
			ItemGroupMetadataBean igmb = new ItemGroupMetadataBean();
			igmb.setRepeatingGroup(repeating != null && repeating);
			dib.setGroupMetadata(igmb);

			result.add(dib);
		}
		return result;
	}

	/**
	 * Set sdv status for item data id list.
	 * 
	 * @param itemDataIds
	 *            List<Integer>
	 * @param userId
	 *            user id
	 * @param sdv
	 *            boolean
	 * @return boolean
	 */
	public boolean sdvItems(List<Integer> itemDataIds, int userId, boolean sdv) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		int ind = 1;
		variables.put(ind++, sdv);
		variables.put(ind++, userId);
		variables.put(ind, sdv);

		execute(digester.getQuery("sdvItems").concat(" ")
				.concat(itemDataIds.toString().replace("[", "(").replace("]", ")")), variables);

		return isQuerySuccessful();
	}
}
