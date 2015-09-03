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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

/**
 * SectionDAO.java, the data access object for creation and access to the sections of a CRF. CRFs will have more than
 * one version, which in turn will have one or more sections, which will have one or more items with metadata for
 * presentation.
 * 
 * @author thickerson
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SectionDAO extends AuditableEntityDAO {

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_SECTION;
	}

	/**
	 * Constructor.
	 * 
	 * @param ds
	 *            DataSource
	 */
	public SectionDAO(DataSource ds) {
		super(ds);
	}

	/**
	 * Constructor.
	 * 
	 * @param ds
	 *            DataSource
	 * @param digester
	 *            DAODigester
	 */
	public SectionDAO(DataSource ds, DAODigester digester) {
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
	public SectionDAO(DataSource ds, DAODigester digester, Locale locale) {
		this(ds, digester);
		this.locale = locale;
	}

	@Override
	public void setTypesExpected() {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT); // crf version id
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING); // label
		this.setTypeExpected(index++, TypeNames.STRING); // title
		this.setTypeExpected(index++, TypeNames.STRING); // subtitle
		this.setTypeExpected(index++, TypeNames.STRING); // instructions
		this.setTypeExpected(index++, TypeNames.STRING); // page num label
		this.setTypeExpected(index++, TypeNames.INT); // order by
		this.setTypeExpected(index++, TypeNames.INT); // parent id
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.INT); // owner id
		this.setTypeExpected(index++, TypeNames.INT); // update id
		this.setTypeExpected(index, TypeNames.INT); // borders

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
		SectionBean sb = (SectionBean) eb;
		HashMap variables = new HashMap();
		variables.put(index++, sb.getCRFVersionId());
		variables.put(index++, sb.getStatus().getId());
		variables.put(index++, sb.getLabel());
		variables.put(index++, sb.getTitle());
		variables.put(index++, sb.getInstructions());
		variables.put(index++, sb.getSubtitle());
		variables.put(index++, sb.getPageNumberLabel());
		variables.put(index++, sb.getOrdinal());
		variables.put(index++, sb.getUpdaterId());
		variables.put(index++, sb.getBorders());
		variables.put(index, sb.getId());
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
	 * Creates new SectionBean.
	 *
	 * @param eb
	 *            EntityBean
	 * @param con
	 *            Connection
	 * @return EntityBean
	 */
	public EntityBean create(EntityBean eb, Connection con) {
		int index = 1;
		SectionBean sb = (SectionBean) eb;
		HashMap variables = new HashMap();
		variables.put(index++, sb.getCRFVersionId());
		variables.put(index++, sb.getStatus().getId());
		variables.put(index++, sb.getLabel());
		variables.put(index++, sb.getTitle());
		variables.put(index++, sb.getInstructions());
		variables.put(index++, sb.getSubtitle());
		variables.put(index++, sb.getPageNumberLabel());
		variables.put(index++, sb.getOrdinal());
		variables.put(index++, sb.getParentId());
		variables.put(index++, sb.getOwnerId());
		variables.put(index, sb.getBorders());
		executeWithPK(digester.getQuery("create"), variables, null, con);
		if (isQuerySuccessful()) {
			eb.setId(getLatestPK());
		}
		return eb;
	}

	/**
	 * Transforms map to object.
	 * 
	 * @param hm
	 *            HashMap
	 * @return Object
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		SectionBean eb = new SectionBean();
		this.setEntityAuditInformation(eb, hm);
		eb.setId((Integer) hm.get("section_id"));
		eb.setCRFVersionId((Integer) hm.get("crf_version_id"));
		eb.setLabel((String) hm.get("label"));
		eb.setTitle((String) hm.get("title"));
		eb.setInstructions((String) hm.get("instructions"));
		eb.setSubtitle((String) hm.get("subtitle"));
		eb.setPageNumberLabel((String) hm.get("page_number_label"));
		eb.setOrdinal((Integer) hm.get("ordinal"));
		eb.setParentId((Integer) hm.get("parent_id"));
		eb.setBorders((Integer) hm.get("borders"));
		return eb;
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
			SectionBean eb = (SectionBean) this.getEntityFromHashMap((HashMap) anAlist);
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
	 * Find by version id.
	 * 
	 * @param id
	 *            int
	 * @return Collection
	 */
	public Collection findByVersionId(int id) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, id);

		String sql = digester.getQuery("findByVersionId");
		ArrayList alist = this.selectByCache(sql, variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			SectionBean eb = (SectionBean) this.getEntityFromHashMap((HashMap) anAlist);
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
		SectionBean eb = new SectionBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.selectByCache(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (SectionBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
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
	 * Find all by crf version id.
	 * 
	 * @param crfVersionId
	 *            int
	 * @return ArrayList
	 */
	public ArrayList findAllByCRFVersionId(int crfVersionId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);

		return this.executeFindAllQuery("findAllByCRFVersion", variables);
	}

	/**
	 * Returns num items by section id.from rows.
	 * 
	 * @param rows
	 *            ArrayList
	 * @return HashMap
	 */
	private HashMap getNumItemsBySectionIdFromRows(ArrayList rows) {
		HashMap answer = new HashMap();

		for (Object row : rows) {
			HashMap hm = (HashMap) row;
			Integer sectionIdInt = (Integer) hm.get("section_id");
			Integer numItemsInt = (Integer) hm.get("num_items");

			if (numItemsInt != null && sectionIdInt != null) {
				answer.put(sectionIdInt, numItemsInt);
			}
		}

		return answer;
	}

	/**
	 * Returns num items by section id.
	 * 
	 * @return HashMap
	 */
	public HashMap getNumItemsBySectionId() {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT); // section_id
		this.setTypeExpected(index, TypeNames.INT); // count

		String sql = digester.getQuery("getNumItemsBySectionId");
		ArrayList rows = this.select(sql);
		return getNumItemsBySectionIdFromRows(rows);
	}

	/**
	 * Groups by sectionId and takes section id.
	 * 
	 * @param sb
	 *            SectionBean
	 * @return HashMap
	 */
	public HashMap getNumItemsBySection(SectionBean sb) {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT); // section_id
		this.setTypeExpected(index, TypeNames.INT); // count

		String sql = digester.getQuery("getNumItemsBySection");
		ArrayList rows = this.select(sql);
		return getNumItemsBySectionIdFromRows(rows);

	}

	/**
	 * Returns num items plus repeat by section id.
	 * 
	 * @param ecb
	 *            EventCRFBean
	 * @return HashMap
	 */
	public HashMap getNumItemsPlusRepeatBySectionId(EventCRFBean ecb) {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT); // section_id
		this.setTypeExpected(index, TypeNames.INT); // count

		HashMap variables = new HashMap();
		variables.put(1, ecb.getId());
		String sql = digester.getQuery("getNumItemsPlusRepeatBySectionId");

		ArrayList rows = this.select(sql, variables);
		return getNumItemsBySectionIdFromRows(rows);
	}

	/**
	 * Returns num items completed by section id.
	 * 
	 * @param ecb
	 *            EventCRFBean
	 * @return HashMap
	 */
	public HashMap getNumItemsCompletedBySectionId(EventCRFBean ecb) {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT); // section_id
		this.setTypeExpected(index, TypeNames.INT); // count

		HashMap variables = new HashMap();
		variables.put(1, ecb.getId());
		String sql = digester.getQuery("getNumItemsCompletedBySectionId");

		ArrayList rows = this.select(sql, variables);
		return getNumItemsBySectionIdFromRows(rows);
	}

	/**
	 * Returns num items completed by section.
	 * 
	 * @param ecb
	 *            EventCRFBean
	 * @return HashMap
	 */
	public HashMap getNumItemsCompletedBySection(EventCRFBean ecb) {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT); // section_id
		this.setTypeExpected(index, TypeNames.INT); // count

		HashMap variables = new HashMap();
		variables.put(1, ecb.getId());
		String sql = digester.getQuery("getNumItemsCompletedBySection");

		ArrayList rows = this.select(sql, variables);
		return getNumItemsBySectionIdFromRows(rows);
	}

	/**
	 * Returns num items pending by section id.
	 * 
	 * @param ecb
	 *            EventCRFBean
	 * @return HashMap
	 */
	public HashMap getNumItemsPendingBySectionId(EventCRFBean ecb) {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT); // section_id
		this.setTypeExpected(index, TypeNames.INT); // count

		HashMap variables = new HashMap();
		variables.put(1, ecb.getId());
		String sql = digester.getQuery("getNumItemsPendingBySectionId");

		ArrayList rows = this.select(sql, variables);
		return getNumItemsBySectionIdFromRows(rows);
	}

	/**
	 * Returns num items pending by section.
	 * 
	 * @param ecb
	 *            EventCRFBean
	 * @param sb
	 *            SectionBean
	 * @return HashMap
	 */
	public HashMap getNumItemsPendingBySection(EventCRFBean ecb, SectionBean sb) {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT); // section_id
		this.setTypeExpected(index, TypeNames.INT); // count

		index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, ecb.getId());
		variables.put(index, sb.getId());
		String sql = digester.getQuery("getNumItemsPendingBySection");

		ArrayList rows = this.select(sql, variables);
		return getNumItemsBySectionIdFromRows(rows);
	}

	/**
	 * Returns num items blank by section id.
	 * 
	 * @param ecb
	 *            EventCRFBean
	 * @return HashMap
	 */
	public HashMap getNumItemsBlankBySectionId(EventCRFBean ecb) {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT); // section_id
		this.setTypeExpected(index, TypeNames.INT); // count

		HashMap variables = new HashMap();
		variables.put(1, ecb.getId());
		String sql = digester.getQuery("getNumItemsBlankBySectionId");

		ArrayList rows = this.select(sql, variables);
		return getNumItemsBySectionIdFromRows(rows);
	}

	/**
	 * Returns num items blank by section.
	 * 
	 * @param ecb
	 *            EventCRFBean
	 * @param sb
	 *            SectionBean
	 * @return HashMap
	 */
	public HashMap getNumItemsBlankBySection(EventCRFBean ecb, SectionBean sb) {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(index++, TypeNames.INT); // section_id
		this.setTypeExpected(index, TypeNames.INT); // count

		index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, ecb.getId());
		variables.put(index, sb.getId());
		String sql = digester.getQuery("getNumItemsBlankBySectionId");

		ArrayList rows = this.select(sql, variables);
		return getNumItemsBySectionIdFromRows(rows);
	}

	/**
	 * Find next.
	 * 
	 * @param ecb
	 *            EventCRFBean
	 * @param current
	 *            SectionBean
	 * @return SectionBean
	 */
	public SectionBean findNext(EventCRFBean ecb, SectionBean current) {
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, ecb.getCRFVersionId());
		variables.put(index, current.getOrdinal());

		String sql = digester.getQuery("findNext");
		ArrayList rows = this.select(sql, variables);

		SectionBean answer = new SectionBean();
		if (rows.size() > 0) {
			HashMap row = (HashMap) rows.get(0);
			answer = (SectionBean) getEntityFromHashMap(row);
		}

		return answer;
	}

	/**
	 * Find previous.
	 * 
	 * @param ecb
	 *            EventCRFBean
	 * @param current
	 *            SectionBean
	 * @return SectionBean
	 */
	public SectionBean findPrevious(EventCRFBean ecb, SectionBean current) {
		int index = 1;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(index++, ecb.getCRFVersionId());
		variables.put(index, current.getOrdinal());

		String sql = digester.getQuery("findPrevious");
		ArrayList rows = this.select(sql, variables);
		SectionBean answer = new SectionBean();

		if (rows.size() > 0) {
			HashMap row = (HashMap) rows.get(0);
			answer = (SectionBean) getEntityFromHashMap(row);
		}

		return answer;
	}

	/**
	 * Deletes test section.
	 * 
	 * @param label
	 *            String
	 */
	public void deleteTestSection(String label) {
		HashMap variables = new HashMap();
		variables.put(1, label);
		this.execute(digester.getQuery("deleteTestSection"), variables);
	}

	/**
	 * Returns true if section has scd item.
	 * 
	 * @param sectionId
	 *            Integer
	 * @return boolean
	 */
	public boolean hasSCDItem(Integer sectionId) {
		return countSCDItemBySectionId(sectionId) > 0;
	}

	/**
	 * Counts scd item by section id.
	 * 
	 * @param sectionId
	 *            Integer
	 * @return int
	 */
	public int countSCDItemBySectionId(Integer sectionId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT); // count

		HashMap variables = new HashMap();
		variables.put(1, sectionId);
		ArrayList rows = this.select(digester.getQuery("countSCDItemBySectionId"), variables);
		if (rows.size() > 0) {
			return (Integer) ((HashMap) rows.iterator().next()).get("count");
		} else {
			return 0;
		}
	}

	/**
	 * Returns true if contains normal item.
	 * 
	 * @param crfVersionId
	 *            Integer
	 * @param sectionId
	 *            Integer
	 * @return boolean
	 */
	public boolean containNormalItem(Integer crfVersionId, Integer sectionId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT); // item_id

		int index = 1;
		HashMap variables = new HashMap();
		variables.put(index++, sectionId);
		variables.put(index++, crfVersionId);
		variables.put(index++, crfVersionId);
		variables.put(index++, sectionId);
		variables.put(index, crfVersionId);
		ArrayList rows = this.select(digester.getQuery("containNormalItem"), variables);
		return rows.size() > 0 && (Integer) ((HashMap) rows.iterator().next()).get("item_id") > 0;
	}

	/**
	 * Returns section id for tab id.
	 * 
	 * @param crfVersionId
	 *            int
	 * @param tabId
	 *            int
	 * @return HashMap
	 */
	public HashMap getSectionIdForTabId(int crfVersionId, int tabId) {
		int index = 1;
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT); // section_id

		HashMap variables = new HashMap();
		variables.put(index++, crfVersionId);
		variables.put(index, tabId);

		ArrayList rows = this.select(digester.getQuery("getSectionIdForTabId"), variables);
		return getSectionIdFromRows(rows);
	}

	private HashMap getSectionIdFromRows(ArrayList rows) {
		Iterator it = rows.iterator();
		HashMap hm = new HashMap();
		while (it.hasNext()) {
			hm = (HashMap) it.next();
		}

		return hm;
	}
}
