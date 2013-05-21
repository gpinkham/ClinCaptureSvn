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

import java.util.*;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.dynamicevent.DynamicEventBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.exception.OpenClinicaException;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DynamicEventDao<K, V extends ArrayList> extends AuditableEntityDAO {

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
		this.setTypeExpected(8, TypeNames.DATE);// created
		this.setTypeExpected(9, TypeNames.DATE);// updated
		this.setTypeExpected(10, TypeNames.STRING);// name
		this.setTypeExpected(11, TypeNames.STRING);// description
	}

	@SuppressWarnings("deprecation")
	public Object getEntityFromHashMap(HashMap hm) {
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
		ArrayList alist = this.select(digester.getQuery("findAll"));
		ArrayList al = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			DynamicEventBean deb = (DynamicEventBean) this.getEntityFromHashMap((HashMap) it.next());
			al.add(deb);
		}
		return al;
	}

	public EntityBean findByPK(int id) throws OpenClinicaException {
		DynamicEventBean deb = new DynamicEventBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(id));

		String sql = digester.getQuery("findByPK");

		ArrayList alist = this.select(sql, variables);
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
		this.execute(digester.getQuery("create"), variables);
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

	public void deleteAllFromStudyGroupClass(int studyGroupClassId) throws OpenClinicaException {
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(studyGroupClassId));
		this.execute(digester.getQuery("deleteAllFromStudyGroupClass"), variables);
	}
	
	public Collection findAllByStudyGroupClassId(int studyGroupClassId) throws OpenClinicaException {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(studyGroupClassId)); 
		ArrayList alist = this.select(digester.getQuery("findAllByStudyGroupClassId"), variables);
		ArrayList al = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			DynamicEventBean deb = (DynamicEventBean) this.getEntityFromHashMap((HashMap) it.next());
			al.add(deb);
		}
		return al;
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
