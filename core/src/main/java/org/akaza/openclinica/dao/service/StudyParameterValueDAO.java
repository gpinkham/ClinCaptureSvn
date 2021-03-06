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
package org.akaza.openclinica.dao.service;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameter;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.service.StudyParamsConfig;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class StudyParameterValueDAO extends AuditableEntityDAO implements IStudyParameterValueDAO {

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_STUDY_PARAMETER;

	}

	public StudyParameterValueDAO(DataSource ds) {
		super(ds);
	}

	public StudyParameterValueDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
	}

	public Collection findAll() {
		ArrayList al = new ArrayList();

		return al;
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		ArrayList al = new ArrayList();

		return al;
	}

	public EntityBean create(EntityBean eb) {
		StudyParameterValueBean spvb = (StudyParameterValueBean) eb;
		HashMap variables = new HashMap();

		variables.put(new Integer(1), new Integer(spvb.getStudyId()));
		variables.put(new Integer(2), spvb.getValue());
		variables.put(new Integer(3), spvb.getParameter());

		executeWithPK(digester.getQuery("create"), variables);
		if (isQuerySuccessful()) {
			spvb.setId(getLatestPK());
		}
		return spvb;

	}

	public EntityBean update(EntityBean eb) {
		StudyParameterValueBean spvb = (StudyParameterValueBean) eb;
		HashMap variables = new HashMap();

		variables.put(new Integer(1), spvb.getValue());
		variables.put(new Integer(2), new Integer(spvb.getStudyId()));
		variables.put(new Integer(3), spvb.getParameter());

		this.execute(digester.getQuery("update"), variables);
		return spvb;
	}

	public Object getEntityFromHashMap(HashMap hm) {
		// study_id numeric,
		// value varchar(50),
		// study_parameter_id int4,
		StudyParameterValueBean spvb = new StudyParameterValueBean();
		spvb.setValue((String) hm.get("value"));
		spvb.setStudyId(((Integer) hm.get("study_id")).intValue());
		spvb.setId(((Integer) hm.get("study_parameter_value_id")).intValue());
		spvb.setParameter((String) hm.get("parameter"));

		return spvb;
	}

	public com.clinovo.model.System getSystemEntityFromHashMap(HashMap hm) {
		com.clinovo.model.System systemProp = new com.clinovo.model.System();
		systemProp.setValue((String) hm.get("value"));
		systemProp.setId(((Integer) hm.get("id")).intValue());
		systemProp.setName((String) hm.get("name"));

		return systemProp;
	}

	public Object getParameterEntityFromHashMap(HashMap hm) {
		// study_parameter_id serial NOT NULL,
		// handle varchar(50),
		// name varchar(50),
		// description varchar(255),
		// default_value varchar(50),
		// inheritable bool DEFAULT true,
		// overridable bool,
		StudyParameter sp = new StudyParameter();
		sp.setId(((Integer) hm.get("study_parameter_id")).intValue());
		sp.setHandle((String) hm.get("handle"));
		sp.setName((String) hm.get("name"));
		sp.setDescription((String) hm.get("description"));
		sp.setDefaultValue((String) hm.get("default_value"));
		sp.setInheritable(((Boolean) hm.get("inheritable")).booleanValue());
		sp.setOverridable(((Boolean) hm.get("overridable")).booleanValue());
		return sp;
	}

	@Override
	public void setTypesExpected() {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.STRING);
	}

	public void setTypesExpectedForSystemProperty() {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.STRING);
	}

	public void setTypesExpectedForParameter() {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.STRING);
		this.setTypeExpected(5, TypeNames.STRING);
		this.setTypeExpected(6, TypeNames.BOOL);
		this.setTypeExpected(7, TypeNames.BOOL);
	}

	public StudyParameterValueBean findByHandleAndStudy(int studyId, String handle) {
		StudyParameterValueBean spvb = new StudyParameterValueBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(studyId));
		variables.put(new Integer(2), handle);

		String sql = digester.getQuery("findByStudyAndHandle");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			spvb = (StudyParameterValueBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return spvb;
	}

	public StudyParameter findParameterByHandle(String handle) {
		StudyParameter sp = new StudyParameter();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(new Integer(1), handle);

		String sql = digester.getQuery("findParameterByHandle");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			sp = (StudyParameter) this.getEntityFromHashMap((HashMap) it.next());
		}
		return sp;

	}

	public com.clinovo.model.System findSystemPropertyByName(String handle) {
		com.clinovo.model.System systemProperty = new com.clinovo.model.System();
		this.setTypesExpectedForSystemProperty();

		HashMap variables = new HashMap();
		variables.put(new Integer(1), handle);

		String sql = digester.getQuery("findSystemPropertyByName");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			systemProperty = this.getSystemEntityFromHashMap((HashMap) it.next());
		}

		return systemProperty;
	}

	public boolean setParameterValue(int studyId, String parameterHandle, String value) {

		return false;

	}

	public ArrayList findAllParameters() {
		this.setTypesExpectedForParameter();
		ArrayList alist = this.select(digester.getQuery("findAllParameters"));
		ArrayList al = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			StudyParameter eb = (StudyParameter) this.getParameterEntityFromHashMap((HashMap) it.next());
			al.add(eb);
		}
		return al;
	}

	/***
	 * Gets list of study parameters for a particular system setting e.g. medical coding
	 * 
	 * @param systemGroupId
	 * @return
	 */
	public ArrayList<StudyParameter> findParametersBySystemGroup(int systemGroupId) {

		this.setTypesExpectedForParameter();
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(systemGroupId));
		ArrayList alist = this.select(digester.getQuery("findParametersBySystemGroup"), variables);
		ArrayList al = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			StudyParameter eb = (StudyParameter) this.getParameterEntityFromHashMap((HashMap) it.next());
			al.add(eb);
		}
		return al;
	}

	public ArrayList findAllParameterValuesByStudy(StudyBean study) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(study.getId()));

		ArrayList alist = this.select(digester.getQuery("findAllParameterValuesByStudy"), variables);
		ArrayList al = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			StudyParameterValueBean eb = (StudyParameterValueBean) this.getEntityFromHashMap((HashMap) it.next());
			al.add(eb);
		}
		return al;
	}

	public ArrayList findParamConfigByStudy(StudyBean study) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.STRING);
		this.setTypeExpected(5, TypeNames.INT);
		this.setTypeExpected(6, TypeNames.STRING);
		this.setTypeExpected(7, TypeNames.STRING);
		this.setTypeExpected(8, TypeNames.STRING);
		this.setTypeExpected(9, TypeNames.STRING);
		this.setTypeExpected(10, TypeNames.BOOL);
		this.setTypeExpected(11, TypeNames.BOOL);
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(study.getId()));

		ArrayList alist = this.select(digester.getQuery("findParamConfigByStudy"), variables);
		ArrayList al = new ArrayList();
		Iterator it = alist.iterator();
		while (it.hasNext()) {
			HashMap hm = (HashMap) it.next();
			StudyParameterValueBean spvb = new StudyParameterValueBean();
			spvb.setValue((String) hm.get("value"));
			spvb.setStudyId(((Integer) hm.get("study_id")).intValue());
			spvb.setId(((Integer) hm.get("study_parameter_value_id")).intValue());

			StudyParameter sp = new StudyParameter();
			sp.setId(((Integer) hm.get("study_parameter_id")).intValue());
			sp.setHandle((String) hm.get("handle"));
			sp.setName((String) hm.get("name"));
			sp.setDescription((String) hm.get("description"));
			sp.setDefaultValue((String) hm.get("default_value"));
			sp.setInheritable(((Boolean) hm.get("inheritable")).booleanValue());
			sp.setOverridable(((Boolean) hm.get("overridable")).booleanValue());

			StudyParamsConfig config = new StudyParamsConfig();
			config.setParameter(sp);
			config.setValue(spvb);
			al.add(config);
		}
		return al;

	}

	public EntityBean findByPK(int ID) {
		EntityBean eb = new StudyParameterValueBean();
		return eb;

	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		ArrayList al = new ArrayList();

		return al;
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		ArrayList al = new ArrayList();

		return al;
	}

}
