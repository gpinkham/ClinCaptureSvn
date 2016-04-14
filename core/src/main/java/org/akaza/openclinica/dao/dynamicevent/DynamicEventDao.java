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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.dao.dynamicevent;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.dynamicevent.DynamicEventBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.exception.OpenClinicaException;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DynamicEventDao extends AuditableEntityDAO {

	public DynamicEventDao(DataSource ds) {
		super(ds);
	}

	public DynamicEventDao(DataSource ds, DAODigester digester) {
		this(ds);
		this.digester = digester;
	}

	public DynamicEventDao(DataSource ds, DAODigester digester, Locale locale) {
		this(ds, digester);
		this.locale = locale;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_DYNAMIC_EVENT;
	}

	@Override
	public void setTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);// dynamic_event_id
		this.setTypeExpected(2, TypeNames.INT);// study_group_class_id
		this.setTypeExpected(3, TypeNames.INT);// study_event_definition_id
		this.setTypeExpected(4, TypeNames.INT);// study_id
		this.setTypeExpected(5, TypeNames.INT);// ordinal
		this.setTypeExpected(6, TypeNames.INT);// owner id
		this.setTypeExpected(7, TypeNames.INT);// updater id
		this.setTypeExpected(8, TypeNames.TIMESTAMP);// created
		this.setTypeExpected(9, TypeNames.TIMESTAMP);// updated
		this.setTypeExpected(10, TypeNames.STRING);// name
		this.setTypeExpected(11, TypeNames.STRING);// description
	}

	@SuppressWarnings("deprecation")
	public DynamicEventBean getEntityFromHashMap(HashMap hm) {
		DynamicEventBean deb = new DynamicEventBean();
		deb.setId(((Integer) hm.get("dynamic_event_id")).intValue());
		deb.setStudyGroupClassId(((Integer) hm.get("study_group_class_id")).intValue());
		deb.setStudyEventDefinitionId(((Integer) hm.get("study_event_definition_id")).intValue());
		deb.setStudyId(((Integer) hm.get("study_id")).intValue());
		deb.setOrdinal(((Integer) hm.get("ordinal")).intValue());
		deb.setOwnerId((Integer) hm.get("owner_id"));
		deb.setUpdaterId((Integer) hm.get("update_id"));
		deb.setCreatedDate((Date) hm.get("date_created"));
		deb.setUpdatedDate((Date) hm.get("date_updated"));
		deb.setName((String) hm.get("name"));
		deb.setDescription((String) hm.get("description"));
		return deb;
	}

	public Collection findAll() throws OpenClinicaException {
		this.setTypesExpected();
		List alist = this.select(digester.getQuery("findAll"));
		List al = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			DynamicEventBean deb = (DynamicEventBean) this.getEntityFromHashMap((HashMap) it.next());
			al.add(deb);
		}
		return al;
	}

	public DynamicEventBean findByPK(int id) throws OpenClinicaException {
		DynamicEventBean deb = new DynamicEventBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(id));

		String sql = digester.getQuery("findByPK");

		List alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			deb = (DynamicEventBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return deb;
	}

	public EntityBean create(EntityBean eb) throws OpenClinicaException {
		DynamicEventBean deb = (DynamicEventBean) eb;
		HashMap variables = new HashMap();
		variables.put(Integer.valueOf(1), new Integer(deb.getStudyGroupClassId()));
		variables.put(Integer.valueOf(2), new Integer(deb.getStudyEventDefinitionId()));
		variables.put(Integer.valueOf(3), new Integer(deb.getStudyId()));
		variables.put(Integer.valueOf(4), new Integer(deb.getOrdinal()));
		variables.put(Integer.valueOf(5), new Integer(deb.getOwnerId()));
		variables.put(Integer.valueOf(6), deb.getName());
		variables.put(Integer.valueOf(7), deb.getDescription());
		executeWithPK(digester.getQuery("create"), variables);
		if (isQuerySuccessful()) {
			eb.setId(getLatestPK());
		}
		return eb;
	}

	public EntityBean update(EntityBean eb) throws OpenClinicaException {
		DynamicEventBean deb = (DynamicEventBean) eb;
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(deb.getStudyGroupClassId()));
		variables.put(new Integer(2), new Integer(deb.getStudyEventDefinitionId()));
		variables.put(new Integer(3), new Integer(deb.getStudyId()));
		variables.put(new Integer(4), new Integer(deb.getOrdinal()));
		variables.put(new Integer(5), new Integer(deb.getUpdaterId()));
		variables.put(new Integer(6), deb.getName());
		variables.put(new Integer(7), deb.getDescription());
		variables.put(new Integer(8), new Integer(deb.getId()));
		this.execute(digester.getQuery("update"), variables);
		return eb;
	}

	public void deleteByPK(int key) throws OpenClinicaException {
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(key));
		this.execute(digester.getQuery("deleteByPK"), variables);
	}

	public void deleteAllByStudyGroupClassId(int studyGroupClassId) {
		HashMap variables = new HashMap();
		variables.put(1, new Integer(studyGroupClassId));
		this.execute(digester.getQuery("deleteAllByStudyGroupClassId"), variables);
	}

	public Collection findAllByStudyGroupClassId(int studyGroupClassId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, new Integer(studyGroupClassId));
		List alist = this.select(digester.getQuery("findAllByStudyGroupClassId"), variables);
		List al = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			DynamicEventBean deb = (DynamicEventBean) this.getEntityFromHashMap((HashMap) it.next());
			al.add(deb);
		}
		return al;
	}

	/**
	 * 
	 * @return list of all events, used in the subject matrix
	 * @throws OpenClinicaException
	 */

	public List<Integer> findAllDefIdsInActiveDynGroupsByStudyId(int id) {
		List<Integer> idList = new ArrayList<Integer>();
		this.setTypesExpected();
		List alist = this.findAllByGroupClassStatusIdAndStudyId(Status.AVAILABLE.getCode(), id);
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			DynamicEventBean deb = (DynamicEventBean) it.next();
			idList.add(Integer.valueOf(deb.getStudyEventDefinitionId()));
		}

		return idList;
	}

	public List<DynamicEventBean> findAllByGroupClassStatusIdAndStudyId(int statusId, int studyId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(statusId));
		variables.put(new Integer(2), new Integer(studyId));
		List alist = this.select(digester.getQuery("findAllByGroupClassStatusIdAndStudyId"), variables);
		List result = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			DynamicEventBean deb = (DynamicEventBean) this.getEntityFromHashMap((HashMap) it.next());
			result.add(deb);
		}
		return result;
	}

	public List<DynamicEventBean> findAllByStudyId(int studyId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(studyId));
		List alist = this.select(digester.getQuery("findAllByStudyId"), variables);
		List result = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			DynamicEventBean deb = (DynamicEventBean) this.getEntityFromHashMap((HashMap) it.next());
			result.add(deb);
		}
		return result;
	}

	public DynamicEventBean findByStudyEventDefinitionId(int studyEventDefinitionId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(studyEventDefinitionId));
		List alist = this.select(digester.getQuery("findByStudyEventDefinitionId"), variables);
		DynamicEventBean result = null;
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			result = (DynamicEventBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return result;
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase)
			throws OpenClinicaException {
		ArrayList al = new ArrayList();
		// TODO
		return al;
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) throws OpenClinicaException {
		ArrayList al = new ArrayList();
		// TODO
		return al;
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) throws OpenClinicaException {
		ArrayList al = new ArrayList();
		// TODO
		return al;
	}
}
