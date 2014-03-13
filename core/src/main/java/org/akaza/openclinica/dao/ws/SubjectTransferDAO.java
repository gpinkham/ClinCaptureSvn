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
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.dao.ws;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.SubjectTransferBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.exception.OpenClinicaException;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SubjectTransferDAO extends EntityDAO {

	public SubjectTransferDAO(DataSource ds) {
		super(ds);
		this.getCurrentPKName = "findCurrentPKValue";
	}

	@Override
	public int getCurrentPK() {
		int answer = 0;

		if (getCurrentPKName == null) {
			return answer;
		}

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		ArrayList al = select(digester.getQuery(getCurrentPKName));

		if (al.size() > 0) {
			HashMap h = (HashMap) al.get(0);
			answer = (Integer) h.get("key");
		}

		return answer;
	}

	private UserAccountDAO getUserAccountDao() {
		return new UserAccountDAO(ds);
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_SUBJECTTRANSFER;
	}

	public void setTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.STRING); // personId
		this.setTypeExpected(2, TypeNames.STRING); // studySubject
		this.setTypeExpected(3, TypeNames.DATE); // dateOfBirth
		this.setTypeExpected(4, TypeNames.CHAR); // gender
		this.setTypeExpected(5, TypeNames.STRING); // study_oid
		this.setTypeExpected(6, TypeNames.INT); // owner_id
		this.setTypeExpected(7, TypeNames.DATE); // enrollment_date
		this.setTypeExpected(8, TypeNames.DATE); // date_received

	}

	public Object getEntityFromHashMap(HashMap hm) {
		SubjectTransferBean subjectTransferBean = new SubjectTransferBean();
		subjectTransferBean.setId((Integer) hm.get("subject_transfer_id"));
		subjectTransferBean.setPersonId((String) hm.get("person_id"));
		subjectTransferBean.setStudySubjectId((String) hm.get("study_subject_id"));
		subjectTransferBean.setDateOfBirth((Date) hm.get("date_of_birth"));
		try {
			String gender = (String) hm.get("gender");
			char[] genderarr = gender.toCharArray();
			subjectTransferBean.setGender(genderarr[0]);
		} catch (ClassCastException ce) {
			subjectTransferBean.setGender(' ');
		}
		subjectTransferBean.setEnrollmentDate((Date) hm.get("enrollment_date"));
		subjectTransferBean.setOwner((UserAccountBean) getUserAccountDao().findByPK((Integer) hm.get("owner_id")));

		return subjectTransferBean;
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase)
			throws OpenClinicaException {
		return new ArrayList();
	}

	public Collection findAll() throws OpenClinicaException {
		return new ArrayList();
	}

	public EntityBean findByPK(int id) throws OpenClinicaException {
		SubjectTransferBean transfer = null;

		this.setTypesExpected();
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");
		ArrayList<?> alist = this.select(sql, variables);

		Iterator<?> it = alist.iterator();

		if (it.hasNext()) {
			transfer = (SubjectTransferBean) this.getEntityFromHashMap((HashMap<?, ?>) it.next());
		}
		return transfer;
	}

	public EntityBean create(EntityBean eb) {
		SubjectTransferBean subjectTransferBean = (SubjectTransferBean) eb;
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		HashMap<Integer, Object> nullVars = new HashMap<Integer, Object>();

		if (subjectTransferBean.getDateOfBirth() == null) {
			nullVars.put(1, Types.DATE);
			variables.put(1, null);
		} else {
			variables.put(1, subjectTransferBean.getDateOfBirth());
		}

		if (subjectTransferBean.getGender() != 'm' && subjectTransferBean.getGender() != 'f') {
			nullVars.put(2, Types.CHAR);
			variables.put(2, null);
		} else {
			char[] ch = { subjectTransferBean.getGender() };
			variables.put(2, new String(ch));
		}

		variables.put(3, subjectTransferBean.getPersonId());
		variables.put(4, subjectTransferBean.getStudySubjectId());
		variables.put(5, subjectTransferBean.getStudyOid());
		variables.put(6, subjectTransferBean.getDateReceived());
		variables.put(7, subjectTransferBean.getEnrollmentDate());
		variables.put(8, subjectTransferBean.getOwner().getId());

		executeWithPK(digester.getQuery("create"), variables, nullVars);
		if (isQuerySuccessful()) {
			subjectTransferBean.setId(getLatestPK());
		}
		return subjectTransferBean;
	}

	public EntityBean update(EntityBean eb) throws OpenClinicaException {
		return new ItemGroupMetadataBean(); // To change body of implemented

	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) throws OpenClinicaException {
		return new ArrayList<SubjectTransferBean>();
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) throws OpenClinicaException {
		return new ArrayList<SubjectTransferBean>();
	}

}
