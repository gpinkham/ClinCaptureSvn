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
package org.akaza.openclinica.web.bean;

import org.akaza.openclinica.bean.admin.AuditEventBean;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "unchecked"})
public class AuditEventRow extends EntityBeanRow {
	
	public static final int AUDIT_DATE = 0;
	public static final int AUDIT_ACTION = 1;
	public static final int AUDIT_ENTITY = 2;
	public static final int AUDIT_STUDY_NAME = 3;
	public static final int AUDIT_SUBJECT_NAME = 4;
	public static final int AUDIT_CHANGES = 5;
	public static final int AUDIT_OTHER_INFO = 6;

	@Override
	protected int compareColumn(Object row, int sortingColumn) {
		if (!row.getClass().equals(AuditEventRow.class)) {
			return 0;
		}

		AuditEventBean thisBean = (AuditEventBean) bean;
		AuditEventBean argBean = (AuditEventBean) ((AuditEventRow) row).bean;

		int answer = 0;
		switch (sortingColumn) {
		case AUDIT_DATE:
			answer = compareDate(thisBean.getAuditDate(), argBean.getAuditDate());
			break;
		case AUDIT_ACTION:
			answer = thisBean.getReasonForChange().toLowerCase().compareTo(argBean.getReasonForChange().toLowerCase());
			break;
		case AUDIT_ENTITY:
			answer = thisBean.getAuditTable().toLowerCase().compareTo(argBean.getAuditTable().toLowerCase());
			break;
		case AUDIT_STUDY_NAME:
			answer = new Integer(thisBean.getStudyName()).compareTo(new Integer(argBean.getStudyName()));
			break;
		case AUDIT_SUBJECT_NAME:
			answer = new Integer(thisBean.getSubjectName()).compareTo(new Integer(argBean.getSubjectName()));
			break;
		case AUDIT_CHANGES:
			break;
		case AUDIT_OTHER_INFO:
			break;
		}

		return answer;
	}

	@Override
	public String getSearchString() {
		AuditEventBean thisBean = (AuditEventBean) bean;
		return thisBean.getAuditTable() + " " + thisBean.getEntityId();// ?
	}

	@Override
	public ArrayList generatRowsFromBeans(ArrayList beans) {
		return AuditEventRow.generateRowsFromBeans(beans);
	}

	public static ArrayList generateRowsFromBeans(ArrayList beans) {
		ArrayList answer = new ArrayList();

		for (int i = 0; i < beans.size(); i++) {
			try {
				AuditEventRow row = new AuditEventRow();
				row.setBean((AuditEventBean) beans.get(i));
				answer.add(row);
			} catch (Exception e) {
			}
		}

		return answer;
	}

}
