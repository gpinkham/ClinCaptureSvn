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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.web.bean;

import org.akaza.openclinica.bean.submit.DisplaySubjectBean;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DisplaySubjectRow extends EntityBeanRow {
	// columns:
	public static final int COL_NAME = 0;

	public static final int COL_SUBJECT_IDS = 1;

	public static final int COL_GENDER = 2;

	public static final int COL_DATE_CREATED = 3;

	public static final int COL_OWNER = 4;

	public static final int COL_DATE_UPDATED = 5;

	public static final int COL_UPDATER = 6;

	public static final int COL_STATUS = 7;

	@Override
	protected int compareColumn(Object row, int sortingColumn) {
		if (!row.getClass().equals(DisplaySubjectRow.class)) {
			return 0;
		}

		DisplaySubjectBean thisSubject = (DisplaySubjectBean) bean;
		DisplaySubjectBean argSubject = (DisplaySubjectBean) ((DisplaySubjectRow) row).bean;

		int answer = 0;
		switch (sortingColumn) {
		case COL_NAME:
			answer = thisSubject.getSubject().getName().toLowerCase()
					.compareTo(argSubject.getSubject().getName().toLowerCase());
			break;
		case COL_SUBJECT_IDS:
			answer = thisSubject.getStudySubjectIds().compareTo(argSubject.getStudySubjectIds());
			break;
		case COL_GENDER:
			answer = (thisSubject.getSubject().getGender() + "").compareTo(argSubject.getSubject().getGender() + "");
			break;
		case COL_DATE_CREATED:
			answer = compareDate(thisSubject.getSubject().getCreatedDate(), argSubject.getSubject().getCreatedDate());
			break;
		case COL_OWNER:
			answer = thisSubject.getSubject().getOwner().getName().toLowerCase()
					.compareTo(argSubject.getSubject().getOwner().getName().toLowerCase());
			break;
		case COL_DATE_UPDATED:
			answer = compareDate(thisSubject.getSubject().getUpdatedDate(), argSubject.getSubject().getUpdatedDate());
			break;
		case COL_UPDATER:
			answer = thisSubject.getSubject().getUpdater().getName().toLowerCase()
					.compareTo(argSubject.getSubject().getUpdater().getName().toLowerCase());
			break;
		case COL_STATUS:
			answer = thisSubject.getSubject().getStatus().compareTo(argSubject.getSubject().getStatus());
			break;
		}

		return answer;
	}

	@Override
	public String getSearchString() {
		DisplaySubjectBean thisSubject = (DisplaySubjectBean) bean;
		return thisSubject.getSubject().getName() + " " + thisSubject.getStudySubjectIds();
	}

	@Override
	public ArrayList generatRowsFromBeans(ArrayList beans) {
		return DisplaySubjectRow.generateRowsFromBeans(beans);
	}

	public static ArrayList generateRowsFromBeans(ArrayList beans) {
		ArrayList answer = new ArrayList();

		for (int i = 0; i < beans.size(); i++) {
			try {
				DisplaySubjectRow row = new DisplaySubjectRow();
				row.setBean((DisplaySubjectBean) beans.get(i));
				answer.add(row);
			} catch (Exception e) {
			}
		}

		return answer;
	}

}
