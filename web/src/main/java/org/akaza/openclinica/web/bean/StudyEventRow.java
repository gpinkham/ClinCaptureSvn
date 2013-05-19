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

import org.akaza.openclinica.bean.managestudy.StudyEventBean;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "unchecked"})
public class StudyEventRow extends EntityBeanRow {
	// columns:

	public static final int COL_STUDY_SUBJECT_LABEL = 0;

	public static final int COL_START_DATE = 1;

	public static final int COL_SUBJECT_EVENT_STATUS = 2;

	@Override
	protected int compareColumn(Object row, int sortingColumn) {
		if (!row.getClass().equals(StudyEventRow.class)) {
			return 0;
		}

		StudyEventBean thisEvent = (StudyEventBean) bean;
		StudyEventBean argEvent = (StudyEventBean) ((StudyEventRow) row).bean;

		int answer = 0;
		switch (sortingColumn) {

		case COL_STUDY_SUBJECT_LABEL:
			answer = thisEvent.getStudySubjectLabel().toLowerCase()
					.compareTo(argEvent.getStudySubjectLabel().toLowerCase());
			break;

		case COL_START_DATE:
			answer = compareDate(thisEvent.getDateStarted(), argEvent.getDateStarted());
			break;

		case COL_SUBJECT_EVENT_STATUS:
			answer = thisEvent.getSubjectEventStatus().compareTo(argEvent.getSubjectEventStatus());
			break;
		}

		return answer;
	}

	@Override
	public String getSearchString() {
		StudyEventBean thisEvent = (StudyEventBean) bean;
		return thisEvent.getStudySubjectLabel() + " " + thisEvent.getSubjectEventStatus().getName();
	}

	@Override
	public ArrayList generatRowsFromBeans(ArrayList beans) {
		return StudyEventRow.generateRowsFromBeans(beans);
	}

	public static ArrayList generateRowsFromBeans(ArrayList beans) {
		ArrayList answer = new ArrayList();

		for (int i = 0; i < beans.size(); i++) {
			try {
				StudyEventRow row = new StudyEventRow();
				row.setBean((StudyEventBean) beans.get(i));
				answer.add(row);
			} catch (Exception e) {
			}
		}

		return answer;
	}

}
