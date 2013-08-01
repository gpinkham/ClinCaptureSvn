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

package org.akaza.openclinica.dao.discrepancy;

import java.util.*;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.DnDescription;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.exception.OpenClinicaException;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DnDescriptionDao extends AuditableEntityDAO {

	public DnDescriptionDao(DataSource ds) {
		super(ds);
	}

	public DnDescriptionDao(DataSource ds, DAODigester digester) {
		this(ds);
		this.digester = digester;
	}

	public DnDescriptionDao(DataSource ds, DAODigester digester, Locale locale) {
		this(ds, digester);
		this.locale = locale;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_DN_DESCRIPTION;
	}

	@Override
	public void setTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);// primary key
		this.setTypeExpected(2, TypeNames.STRING);// name
		this.setTypeExpected(3, TypeNames.STRING);// description
		this.setTypeExpected(4, TypeNames.INT); // study id
		this.setTypeExpected(5, TypeNames.STRING); // visibility level
	}

	public Object getRFCDescriptionFromHashMap(HashMap hm) {
		DnDescription term = new DnDescription();
		int id = ((Integer) hm.get("dn_rfc_description_id")).intValue();
		String description = (String) hm.get("description");
		String name = (String) hm.get("name");
		String visibilityLevel = (String) hm.get("visibility_level");
		int studyId = ((Integer) hm.get("study_id")).intValue();
		term.setId(id);
		term.setDescription(description);
		term.setName(name);
		term.setStudyId(studyId);
		term.setVisibilityLevel(visibilityLevel);
		return term;
	}
	
	// expand this dao later and route the different descripts to different methods?
	public Object getEntityFromHashMap(HashMap hm) {
		if (hm.containsKey("dn_rfc_description_id")) {
			return getRFCDescriptionFromHashMap(hm);
		} else {
			return new DnDescription();
		}
	}

	public Collection<DnDescription> findAll() {
		this.setTypesExpected();
		ArrayList<DnDescription> alist = this.select(digester.getQuery("findAll"));
		ArrayList<DnDescription> al = new ArrayList<DnDescription>();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			DnDescription term = (DnDescription) this.getEntityFromHashMap((HashMap) it.next());
			al.add(term);
		}
		return al;
	}
	
	public Collection<DnDescription> findAllByStudyId(int studyId) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(studyId));

		String sql = digester.getQuery("findAllByStudyId");

		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();
		ArrayList<DnDescription> al = new ArrayList<DnDescription>();
		while (it.hasNext()) {
			DnDescription term = (DnDescription) this.getEntityFromHashMap((HashMap) it.next());
			al.add(term);
		}
		return al;

	}
	
	public EntityBean findByPK(int id) {
		DnDescription term = new DnDescription();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(id));

		String sql = digester.getQuery("findByPK");

		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			term = (DnDescription) this.getEntityFromHashMap((HashMap) it.next());
		}
		return term;
	}

	public EntityBean create(EntityBean eb) {
		DnDescription teb = (DnDescription) eb;
		HashMap variables = new HashMap();

		variables.put(Integer.valueOf(1), teb.getName());
		variables.put(Integer.valueOf(2), teb.getDescription());
		variables.put(Integer.valueOf(3), Integer.valueOf(teb.getStudyId()));
		variables.put(Integer.valueOf(4), teb.getVisibilityLevel());
		this.execute(digester.getQuery("create"), variables);
		return teb;
	}

	public EntityBean update(EntityBean eb) {
		DnDescription teb = (DnDescription) eb;
		HashMap variables = new HashMap();
		
		variables.put(Integer.valueOf(1), teb.getName());
		variables.put(Integer.valueOf(2), teb.getDescription());
		variables.put(Integer.valueOf(3), Integer.valueOf(teb.getStudyId()));
		variables.put(Integer.valueOf(4), teb.getVisibilityLevel());
		
		variables.put(Integer.valueOf(5), Integer.valueOf(teb.getId()));
		
		this.execute(digester.getQuery("update"), variables);
		return eb;
	}
	
	public void deleteByPK(int key) {
		HashMap variables = new HashMap();
		variables.put(Integer.valueOf(1), Integer.valueOf(key));
		this.execute(digester.getQuery("deleteByPK"), variables);
	}
	
	public Collection<DnDescription> findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase)
			throws OpenClinicaException {
		ArrayList<DnDescription> al = new ArrayList<DnDescription>();
		// TODO
		return al;
	}

	public Collection<DnDescription> findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) throws OpenClinicaException {
		ArrayList<DnDescription> al = new ArrayList<DnDescription>();
		// TODO
		return al;
	}

	public Collection<DnDescription> findAllByPermission(Object objCurrentUser, int intActionType) throws OpenClinicaException {
		ArrayList<DnDescription> al = new ArrayList<DnDescription>();
		// TODO
		return al;
	}
}

