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
package org.akaza.openclinica.web.bean;

import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "unchecked"})
public class StudyGroupClassRow extends EntityBeanRow {
	// columns:
	public static final int COL_NAME = 0;
	public static final int COL_TYPE = 1;
	public static final int COL_SUBJECT_ASSIGNMENT = 2;
	public static final int COL_STATUS = 3;

	@Override
	protected int compareColumn(Object row, int sortingColumn) {
		if (!row.getClass().equals(StudyGroupClassRow.class)) {
			return 0;
		}

		StudyGroupClassBean thisStudy = (StudyGroupClassBean) bean;
		StudyGroupClassBean argStudy = (StudyGroupClassBean) ((StudyGroupClassRow) row).bean;

		int answer = 0;
		switch (sortingColumn) {
		case COL_NAME:
			answer = thisStudy.getName().toLowerCase().compareTo(argStudy.getName().toLowerCase());
			break;
		case COL_TYPE:
			answer = thisStudy.getGroupClassTypeName().toLowerCase()
					.compareTo(argStudy.getGroupClassTypeName().toLowerCase());
			break;
		case COL_SUBJECT_ASSIGNMENT:
			answer = thisStudy.getSubjectAssignment().toLowerCase()
					.compareTo(argStudy.getSubjectAssignment().toLowerCase());
			break;
		case COL_STATUS:
			answer = thisStudy.getStatus().compareTo(argStudy.getStatus());
			break;
		}

		return answer;
	}

	@Override
	public String getSearchString() {
		StudyGroupClassBean thisStudy = (StudyGroupClassBean) bean;
		return thisStudy.getName() + " " + thisStudy.getGroupClassTypeName() + " " + thisStudy.getSubjectAssignment();
	}

	@Override
	public ArrayList generatRowsFromBeans(ArrayList beans) {
		return StudyGroupClassRow.generateRowsFromBeans(beans);
	}

	public static ArrayList generateRowsFromBeans(ArrayList beans) {
		ArrayList answer = new ArrayList();

		for (int i = 0; i < beans.size(); i++) {
			try {
				StudyGroupClassRow row = new StudyGroupClassRow();
				row.setBean((StudyGroupClassBean) beans.get(i));
				answer.add(row);
			} catch (Exception e) {
			}
		}

		return answer;
	}

}
