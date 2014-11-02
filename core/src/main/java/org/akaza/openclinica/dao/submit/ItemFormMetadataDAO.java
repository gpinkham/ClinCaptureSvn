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

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.core.PreparedStatementFactory;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.domain.crfdata.InstantOnChangePairContainer;
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
public class ItemFormMetadataDAO extends EntityDAO {

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_ITEMFORMMETADATA;
	}

	public ItemFormMetadataDAO(DataSource ds) {
		super(ds);
	}

	public ItemFormMetadataDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
	}

	// This constructor sets up the Locale for JUnit tests; see the locale
	// member variable in EntityDAO, and its initializeI18nStrings() method
	public ItemFormMetadataDAO(DataSource ds, DAODigester digester, Locale locale) {

		this(ds, digester);
		this.locale = locale;
	}

	private int getIntFromRow(HashMap row, String column) {
		Integer i = (Integer) row.get(column);
		return i == null ? 0 : i;
	}

	private boolean getBooleanFromRow(HashMap row, String column) {
		Boolean i = (Boolean) row.get(column);
		return i != null && i;
	}

	private String getStringFromRow(HashMap row, String column) {
		String s = (String) row.get(column);
		return s == null ? "" : s;
	}

	public Object getEntityFromHashMap(HashMap hm) {

		ItemFormMetadataBean answer = new ItemFormMetadataBean();

		answer.setId(getIntFromRow(hm, "item_form_metadata_id"));
		answer.setItemId(getIntFromRow(hm, "item_id"));
		answer.setCrfVersionId(getIntFromRow(hm, "crf_version_id"));
		answer.setHeader(getStringFromRow(hm, "header"));
		answer.setSubHeader(getStringFromRow(hm, "subheader"));
		answer.setParentId(getIntFromRow(hm, "parent_id"));
		answer.setParentLabel(getStringFromRow(hm, "parent_label"));
		answer.setColumnNumber(getIntFromRow(hm, "column_number"));
		answer.setPageNumberLabel(getStringFromRow(hm, "page_number_label"));
		answer.setQuestionNumberLabel(getStringFromRow(hm, "question_number_label"));
		answer.setLeftItemText(getStringFromRow(hm, "left_item_text"));
		answer.setRightItemText(getStringFromRow(hm, "right_item_text"));
		answer.setSectionId(getIntFromRow(hm, "section_id"));
		answer.setDescisionConditionId(getIntFromRow(hm, "decision_condition_id"));
		answer.setResponseSetId(getIntFromRow(hm, "response_set_id"));
		answer.setRegexp(getStringFromRow(hm, "regexp"));
		answer.setRegexpErrorMsg(getStringFromRow(hm, "regexp_error_msg"));
		answer.setOrdinal(getIntFromRow(hm, "ordinal"));
		answer.setRequired(getBooleanFromRow(hm, "required"));
		answer.setDefaultValue(getStringFromRow(hm, "default_value"));
		answer.setResponseLayout(getStringFromRow(hm, "response_layout"));
		answer.setWidthDecimal(getStringFromRow(hm, "width_decimal"));
		answer.setShowItem(getBooleanFromRow(hm, "show_item"));
		answer.setCodeRef(getStringFromRow(hm, "code_ref"));
		ResponseSetBean rsb = new ResponseSetBean();

		rsb.setId(getIntFromRow(hm, "response_set_id"));
		rsb.setLabel(getStringFromRow(hm, "label"));
		rsb.setResponseTypeId(getIntFromRow(hm, "response_type_id"));

		String optionsText = getStringFromRow(hm, "options_text");
		String optionsValues = getStringFromRow(hm, "options_values");
		rsb.setOptions(optionsText, optionsValues);
		answer.setResponseSet(rsb);

		return answer;
	}

	public void setTypesExpected() {
		this.unsetTypeExpected();

		int ind = 1;
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // item form metadata id 2
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // item id 3
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // crf version id 4
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // header 5
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // subheader 6
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // parent id 7
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // parent label 8
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // column number 9
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // page number label 10
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // question number label 11
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // left item text 12
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // right item text 13
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // section id 14
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // decision condition id 15
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // response set id 16
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // regexp 17
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // regexp error msg 18
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // ordinal 19
		this.setTypeExpected(ind, TypeNames.BOOL);
		ind++; // required 20
		this.setTypeExpected(ind, TypeNames.STRING); // default_value
		ind++; // 21
		this.setTypeExpected(ind, TypeNames.STRING); // response_layout 21
		ind++; // 22
		this.setTypeExpected(ind, TypeNames.STRING); // width_decimal 22
		ind++; // 23
		// will need to set the boolean value here, tbh 23
		this.setTypeExpected(ind, TypeNames.BOOL);
		ind++; // show_item 24
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // code_ref 25
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // response_set.response_type_id 26
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // response_set.label 27
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // response_set.options_text 28
		this.setTypeExpected(ind, TypeNames.STRING);
		// response_set.options_values // 29
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase)
			throws OpenClinicaException {
		// Auto-generated method stub
		return null;
	}

	public Collection<ItemFormMetadataBean> findAll() throws OpenClinicaException {
		ArrayList<ItemFormMetadataBean> answer = new ArrayList<ItemFormMetadataBean>();

		this.setTypesExpected();

		String sql = digester.getQuery("findAll");
		ArrayList alist = this.select(sql);

		for (Object anAlist : alist) {
			ItemFormMetadataBean ifmb = (ItemFormMetadataBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(ifmb);
		}

		return answer;
	}

	public int findCountAllHiddenByCRFVersionId(int crfVersionId) {
		int answer = 0;
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, crfVersionId);
		String sql = digester.getQuery("findAllCountHiddenByCRFVersionId");

		ArrayList rows = select(sql, variables);

		if (rows.size() > 0) {
			HashMap row = (HashMap) rows.get(0);
			answer = (Integer) row.get("number");
		}

		int answer2 = 0;

		String sql2 = digester.getQuery("findAllCountHiddenUnderGroupsByCRFVersionId");
		rows = select(sql2, variables);
		if (rows.size() > 0) {
			HashMap row = (HashMap) rows.get(0);
			answer2 = (Integer) row.get("number");
		}
		return answer + answer2;
	}

	public int findCountAllHiddenButShownByEventCRFId(int eventCrfId) {
		int answer = 0;
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, eventCrfId);
		String sql = digester.getQuery("findAllCountHiddenButShownByEventCrfId");

		ArrayList rows = select(sql, variables);

		if (rows.size() > 0) {
			HashMap row = (HashMap) rows.get(0);
			answer = (Integer) row.get("number");
		}

		return answer;
	}

	public ArrayList<ItemFormMetadataBean> findAllByCRFVersionId(int crfVersionId) throws OpenClinicaException {
		ArrayList<ItemFormMetadataBean> answer = new ArrayList<ItemFormMetadataBean>();

		this.setTypesExpected();

		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, crfVersionId);

		String sql = digester.getQuery("findAllByCRFVersionId");
		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			ItemFormMetadataBean ifmb = (ItemFormMetadataBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(ifmb);
		}

		return answer;
	}

	public ItemFormMetadataBean findAllByCRFVersionIdAndItemId(int crfVersionId, int itemId) {
		ItemFormMetadataBean answer = null;

		this.setTypesExpected();

		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, itemId);
		variables.put(2, crfVersionId);

		String sql = digester.getQuery("findAllByCRFVersionIdAndItemId");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			answer = (ItemFormMetadataBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return answer;
	}

	public ArrayList<ItemFormMetadataBean> findAllByCRFIdItemIdAndHasValidations(int crfId, int itemId) {
		ArrayList<ItemFormMetadataBean> answer = new ArrayList<ItemFormMetadataBean>();

		this.setTypesExpected();

		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, crfId);
		variables.put(2, itemId);

		String sql = digester.getQuery("findAllByCRFIdItemIdAndHasValidations");
		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			ItemFormMetadataBean ifmb = (ItemFormMetadataBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(ifmb);
		}

		return answer;
	}

	public ArrayList<ItemFormMetadataBean> findAllByCRFVersionIdAndResponseTypeId(int crfVersionId, int responseTypeId)
			throws OpenClinicaException {
		Object key;

		ArrayList<ItemFormMetadataBean> answer = new ArrayList<ItemFormMetadataBean>();

		this.setTypesExpected();

		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, crfVersionId);
		variables.put(2, responseTypeId);
		ArrayList alist;

		String sql = digester.getQuery("findAllByCRFVersionIdAndResponseTypeId");

		key = (sql + "," + crfVersionId + "," + responseTypeId);

		if ((alist = (ArrayList) cache.get(key)) == null) {
			alist = this.select(sql, variables);
			if (alist != null)
				cache.put(key, alist);
		}

		if (alist != null) {
			for (Object anAlist : alist) {
				ItemFormMetadataBean ifmb = (ItemFormMetadataBean) this.getEntityFromHashMap((HashMap) anAlist);
				answer.add(ifmb);
			}
		}

		return answer;
	}

	public ArrayList<ItemFormMetadataBean> findAllByItemId(int itemId) {

		// TODO place holder for returning here, tbh
		ArrayList<ItemFormMetadataBean> answer = new ArrayList<ItemFormMetadataBean>();

		this.setTypesExpected();
		this.setTypeExpected(29, TypeNames.STRING);// version name
		this.setTypeExpected(30, TypeNames.STRING);// group_label
		this.setTypeExpected(31, TypeNames.INT);// repeat_max
		this.setTypeExpected(32, TypeNames.STRING);// section_name
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, itemId);

		String sql = digester.getQuery("findAllByItemId");
		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			ItemFormMetadataBean ifmb = (ItemFormMetadataBean) this.getEntityFromHashMap(hm);
			String versionName = (String) hm.get("cvname");
			String groupLabel = (String) hm.get("group_label");
			String sectionName = (String) hm.get("section_name");
			int repeatMax = (Integer) hm.get("repeat_max");
			ifmb.setCrfVersionName(versionName);
			ifmb.setGroupLabel(groupLabel);
			ifmb.setSectionName(sectionName);
			ifmb.setRepeatMax(repeatMax);
			answer.add(ifmb);
		}

		return answer;
	}

	public ArrayList<ItemFormMetadataBean> findAllByItemIdAndHasValidations(int itemId) {

		// TODO place holder for returning here, tbh
		ArrayList<ItemFormMetadataBean> answer = new ArrayList<ItemFormMetadataBean>();

		this.setTypesExpected();
		this.setTypeExpected(29, TypeNames.STRING);// version name
		this.setTypeExpected(30, TypeNames.STRING);// group_label
		this.setTypeExpected(31, TypeNames.INT);// repeat_max
		this.setTypeExpected(32, TypeNames.STRING);// section_name
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, itemId);

		String sql = digester.getQuery("findAllByItemIdAndHasValidations");
		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			ItemFormMetadataBean ifmb = (ItemFormMetadataBean) this.getEntityFromHashMap(hm);
			String versionName = (String) hm.get("cvname");
			String groupLabel = (String) hm.get("group_label");
			String sectionName = (String) hm.get("section_name");
			int repeatMax = (Integer) hm.get("repeat_max");
			ifmb.setCrfVersionName(versionName);
			ifmb.setGroupLabel(groupLabel);
			ifmb.setSectionName(sectionName);
			ifmb.setRepeatMax(repeatMax);
			answer.add(ifmb);
		}

		return answer;
	}

	public ArrayList<ItemFormMetadataBean> findAllBySectionId(int sectionId) throws OpenClinicaException {
		ArrayList<ItemFormMetadataBean> answer = new ArrayList<ItemFormMetadataBean>();

		this.setTypesExpected();

		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, sectionId);

		String sql = digester.getQuery("findAllBySectionId");

		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			ItemFormMetadataBean ifmb = (ItemFormMetadataBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(ifmb);
		}

		return answer;
	}

	public ArrayList<ItemFormMetadataBean> findAllByCRFVersionIdAndSectionId(int crfVersionId, int sectionId)
			throws OpenClinicaException {
		ArrayList<ItemFormMetadataBean> answer = new ArrayList<ItemFormMetadataBean>();

		this.setTypesExpected();

		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, crfVersionId);
		variables.put(2, sectionId);

		String sql = digester.getQuery("findAllByCRFVersionIdAndSectionId");
		ArrayList alist = this.select(sql, variables);

		for (Object anAlist : alist) {
			ItemFormMetadataBean ifmb = (ItemFormMetadataBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(ifmb);
		}

		return answer;
	}

	public EntityBean findByPK(int id) throws OpenClinicaException {
		ItemFormMetadataBean ifmb = new ItemFormMetadataBean();
		this.setTypesExpected();

		// TODO place holder to return here, tbh
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			ifmb = (ItemFormMetadataBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return ifmb;
	}

	public EntityBean create(EntityBean eb) throws OpenClinicaException {
		ItemFormMetadataBean ifmb = (ItemFormMetadataBean) eb;
		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();

		int ind = 0;
		int id = getNextPK();
		variables.put(ind, id);
		ind++;
		variables.put(ind, ifmb.getItemId());
		ind++;
		variables.put(ind, ifmb.getCrfVersionId());
		ind++;
		variables.put(ind, ifmb.getHeader());
		ind++;
		variables.put(ind, ifmb.getSubHeader());
		ind++;
		variables.put(ind, ifmb.getParentId());
		ind++;
		variables.put(ind, ifmb.getParentLabel());
		ind++;
		variables.put(ind, ifmb.getColumnNumber());
		ind++;
		variables.put(ind, ifmb.getPageNumberLabel());
		ind++;
		variables.put(ind, ifmb.getQuestionNumberLabel());
		ind++;
		variables.put(ind, ifmb.getLeftItemText());
		ind++;
		variables.put(ind, ifmb.getRightItemText());
		ind++;
		variables.put(ind, ifmb.getSectionId());
		ind++;
		variables.put(ind, ifmb.getDescisionConditionId());
		ind++;
		variables.put(ind, ifmb.getResponseSetId());
		ind++;
		variables.put(ind, ifmb.getRegexp());
		ind++;
		variables.put(ind, ifmb.getRegexpErrorMsg());
		ind++;
		variables.put(ind, ifmb.getOrdinal());
		ind++;
		variables.put(ind, ifmb.isRequired());
		ind++;
		variables.put(ind, ifmb.getDefaultValue());
		ind++;
		variables.put(ind, ifmb.getResponseLayout());
		ind++;
		variables.put(ind, ifmb.getWidthDecimal());
		ind++;
		variables.put(ind, ifmb.isShowItem());
		ind++;
		variables.put(ind, ifmb.getCodeRef());

		execute("create", variables);

		if (isQuerySuccessful()) {
			ifmb.setId(id);
		}

		return ifmb;
	}

	public EntityBean update(EntityBean eb) throws OpenClinicaException {
		ItemFormMetadataBean ifmb = (ItemFormMetadataBean) eb;
		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();

		int ind = 0;

		variables.put(ind, ifmb.getItemId());
		ind++;
		variables.put(ind, ifmb.getCrfVersionId());
		ind++;
		variables.put(ind, ifmb.getHeader());
		ind++;
		variables.put(ind, ifmb.getSubHeader());
		ind++;
		variables.put(ind, ifmb.getParentId());
		ind++;
		variables.put(ind, ifmb.getParentLabel());
		ind++;
		variables.put(ind, ifmb.getColumnNumber());
		ind++;
		variables.put(ind, ifmb.getPageNumberLabel());
		ind++;
		variables.put(ind, ifmb.getQuestionNumberLabel());
		ind++;
		variables.put(ind, ifmb.getLeftItemText());
		ind++;
		variables.put(ind, ifmb.getRightItemText());
		ind++;
		variables.put(ind, ifmb.getSectionId());
		ind++;
		variables.put(ind, ifmb.getDescisionConditionId());
		ind++;
		variables.put(ind, ifmb.getResponseSetId());
		ind++;
		variables.put(ind, ifmb.getRegexp());
		ind++;
		variables.put(ind, ifmb.getRegexpErrorMsg());
		ind++;
		variables.put(ind, ifmb.getOrdinal());
		ind++;
		variables.put(ind, ifmb.isRequired());
		ind++;
		variables.put(ind, ifmb.getId());
		ind++;
		variables.put(ind, ifmb.getDefaultValue());
		ind++;
		variables.put(ind, ifmb.getResponseLayout());
		ind++;
		variables.put(ind, ifmb.getWidthDecimal());
		ind++;
		variables.put(ind, ifmb.isShowItem());
		ind++;
		variables.put(ind, ifmb.getId());
		ind++;
		variables.put(ind, ifmb.getCodeRef());

		execute("update", variables);

		if (!isQuerySuccessful()) {
			ifmb.setId(0);
			ifmb.setActive(false);
		}

		return ifmb;
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) throws OpenClinicaException {
		return null;
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) throws OpenClinicaException {
		// Auto-generated method stub
		return null;
	}

	private void logMe(String message) {
		logger.debug(message);
	}

	public ItemFormMetadataBean findByItemIdAndCRFVersionId(int itemId, int crfVersionId) {
		this.setTypesExpected();
		// TODO note to come back here, tbh
		this.setTypeExpected(29, TypeNames.STRING);// version name
		// add more here for display, tbh 082007
		this.setTypeExpected(30, TypeNames.STRING);// group_label
		this.setTypeExpected(31, TypeNames.INT);// repeat_max
		this.setTypeExpected(32, TypeNames.STRING);// section_name

		logMe("Current Thread:::" + Thread.currentThread() + "types Expected?");
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, itemId);
		variables.put(2, crfVersionId);

		String sql = digester.getQuery("findByItemIdAndCRFVersionId");

		logMe("Thread?" + Thread.currentThread() + "SQL?" + sql + "variables?" + variables);

		ArrayList alist = this.select(sql, variables);

		Iterator it = alist.iterator();

		ItemFormMetadataBean ifmb = new ItemFormMetadataBean();
		HashMap hm = new HashMap();
		if (it.hasNext()) {
			hm = (HashMap) it.next();
			ifmb = (ItemFormMetadataBean) this.getEntityFromHashMap(hm);
		}

		String versionName = (String) hm.get("cvname");
		String groupLabel = (String) hm.get("group_label");
		String sectionName = (String) hm.get("section_name");
		Integer repeatMax = (Integer) hm.get("repeat_max");
		int repeatMaxInt = repeatMax != null ? repeatMax : 0;
		ifmb.setCrfVersionName(versionName);
		ifmb.setGroupLabel(groupLabel);
		ifmb.setSectionName(sectionName);
		ifmb.setRepeatMax(repeatMaxInt);
		return ifmb;
	}

	public ItemFormMetadataBean findByItemIdAndCRFVersionIdNotInIGM(int itemId, int crfVersionId) {
		this.setTypesExpected();

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, itemId);
		variables.put(2, crfVersionId);

		EntityBean eb = this.executeFindByPKQuery("findByItemIdAndCRFVersionIdNotInIGM", variables);

		if (!eb.isActive()) {
			return new ItemFormMetadataBean();
		} else {
			return (ItemFormMetadataBean) eb;
		}
	}

	public ResponseSetBean findResponseSetByPK(int id) {
		this.unsetTypeExpected();
		int ind = 1;
		this.setTypeExpected(ind, TypeNames.INT);// response_set_id
		ind++;
		this.setTypeExpected(ind, TypeNames.INT);// response_type_id
		ind++;
		this.setTypeExpected(ind, TypeNames.STRING);// label
		ind++;
		this.setTypeExpected(ind, TypeNames.STRING);// option_text
		ind++;
		this.setTypeExpected(ind, TypeNames.STRING);// options_values
		ind++;
		this.setTypeExpected(ind, TypeNames.INT);// version_id
		ind++;
		this.setTypeExpected(ind, TypeNames.STRING);// name
		ind++;
		this.setTypeExpected(ind, TypeNames.STRING);// description

		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, id);

		return (ResponseSetBean) this.executeFindByPKQuery("findResponseSetByPK", variables);
	}

	/**
	 * Find all ItemFormMetadataBean which is simple_conditional_display
	 * 
	 * @param sectionId
	 *            Integer
	 * @return ArrayList<ItemFormMetadataBean>
	 */
	public ArrayList<ItemFormMetadataBean> findSCDItemsBySectionId(Integer sectionId) {
		ArrayList<ItemFormMetadataBean> answer = new ArrayList<ItemFormMetadataBean>();
		this.unsetTypeExpected();
		this.setTypesExpected();
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, sectionId);

		String sql = digester.getQuery("findSCDItemsBySectionId");
		ArrayList alist = this.select(sql, variables);
		for (Object anAlist : alist) {
			ItemFormMetadataBean ifmb = (ItemFormMetadataBean) this.getEntityFromHashMap((HashMap) anAlist);
			answer.add(ifmb);
		}
		return answer;
	}

	/**
	 * need to use this method when you want the results to be cached. i.e they do not get updated.
	 */
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

	private void setInstantTypesExpected() {
		this.unsetTypeExpected();
		/*
		 * oifm.section_id as o_sec_id, oit.item_id as o_item_id, oig.oc_oid as o_ig_oid, oig.name as o_ig_name,
		 * oigm.repeating_group as o_repeating, idfm.section_id as d_sec_id, difm.item_id as d_item_id, dig.oc_oid as
		 * d_ig_oid, dig.name as d_ig_name, digm.repeating_group as d_repeating, difm.item_form_metadata_id as d_ifm_id,
		 * ri.option_name
		 */
		int ind = 1; // o_sec_id
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // o_item_id 2
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // o_ig_oid 3
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // o_ig_name 4
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // o_repeating 5
		this.setTypeExpected(ind, TypeNames.BOOL);
		ind++; // d_sec_id 6
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // d_item_id 7
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // d_ig_oid 8
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // d_ig_name 9
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++; // repeating_group 10
		this.setTypeExpected(ind, TypeNames.BOOL);
		ind++; // d_ifm_id 11
		this.setTypeExpected(ind, TypeNames.INT);
		ind++; // option_name 12
		this.setTypeExpected(ind, TypeNames.STRING);
		ind++;
		this.setTypeExpected(ind, TypeNames.STRING);
	}

	public boolean instantTypeExistsInSection(int sectionId) {
		Integer id = null;
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, sectionId);
		ArrayList alist = this.select(digester.getQuery("instantTypeExistsInSection"), variables);
		for (Object anAlist : alist) {
			HashMap row = (HashMap) anAlist;
			id = (Integer) row.get("item_form_metadata_id");
		}
		return id != null && id > 0;
	}

	public Map<Integer, List<InstantOnChangePairContainer>> sectionInstantMapInSameSection(int crfVersionId) {
		Map<Integer, List<InstantOnChangePairContainer>> pairs = new HashMap<Integer, List<InstantOnChangePairContainer>>();
		this.setInstantTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);
		variables.put(2, crfVersionId);
		variables.put(3, crfVersionId);
		variables.put(4, crfVersionId);
		variables.put(5, crfVersionId);
		String sql = digester.getQuery("findInstantItemsByCrfVersionId");
		ArrayList alist = this.select(sql, variables);
		for (Object anAlist : alist) {
			InstantOnChangePairContainer instantItemPair = new InstantOnChangePairContainer();
			HashMap row = (HashMap) anAlist;
			Integer sectionId = (Integer) row.get("o_sec_id");
			instantItemPair.setOriginSectionId(sectionId);
			instantItemPair.setOriginItemId((Integer) row.get("o_item_id"));
			instantItemPair.setOriginItemGroupOid((String) row.get("o_ig_oid"));
			Boolean isUng = Boolean.FALSE;
			if ("Ungrouped".equalsIgnoreCase((String) row.get("o_ig_name"))) {
				isUng = Boolean.TRUE;
			}
			instantItemPair.setOriginUngrouped(isUng);
			Boolean isRep = (Boolean) row.get("o_repeating");
			isRep = isRep == null ? Boolean.FALSE : isRep;
			instantItemPair.setOriginRepeating(isRep);
			instantItemPair.setDestSectionId((Integer) row.get("d_sec_id"));
			instantItemPair.setDestItemId((Integer) row.get("d_item_id"));
			instantItemPair.setDestItemGroupOid((String) row.get("d_ig_oid"));
			isUng = Boolean.FALSE;
			if ("Ungrouped".equalsIgnoreCase((String) row.get("d_ig_name"))) {
				isUng = Boolean.TRUE;
			}
			instantItemPair.setDestUngrouped(isUng);
			isRep = (Boolean) row.get("d_repeating");
			isRep = isRep == null ? Boolean.FALSE : isRep;
			instantItemPair.setDestRepeating(isRep);
			instantItemPair.setDestItemFormMetadataId((Integer) row.get("d_ifm_id"));
			instantItemPair.setOptionValue((String) row.get("option_name"));
			if (pairs.containsKey(sectionId)) {
				pairs.get(sectionId).add(instantItemPair);
			} else {
				List<InstantOnChangePairContainer> ins = new ArrayList<InstantOnChangePairContainer>();
				ins.add(instantItemPair);
				pairs.put(sectionId, ins);
			}
		}
		return pairs;
	}

	public int getCrfSectionsMetric() {
		int crfSections = 0;
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);
		ArrayList rows = select(digester.getQuery("crfSectionsMetric"));
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			crfSections = (Integer) ((HashMap) it.next()).get("count");
		}
		return crfSections;
	}
}
