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

import java.util.ArrayList;

import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DisplayStudyEventRow extends EntityBeanRow {

	public static final int COL_EVENT = 0;

	public static final int COL_START_DATE = 1;

	public static final int COL_LOCATION = 3;

	public static final int COL_SUBJECT_EVENT_STATUS = 4;

	public static final int COL_STATUS = 5;

	private int compareTo(DisplayStudyEventBean thisEvent, DisplayStudyEventBean argEvent) {
		Integer ordinal = 0;
		try {
			ordinal = ((Integer) thisEvent.getStudyEvent().getStudyEventDefinition().getOrdinal()).compareTo(argEvent
					.getStudyEvent().getStudyEventDefinition().getOrdinal());
		} catch (Exception ex) {
			//
		}
		return ordinal;
	}

	@Override
	protected int compareColumn(Object row, int sortingColumn) {
		if (!row.getClass().equals(DisplayStudyEventRow.class)) {
			return 0;
		}

		DisplayStudyEventBean thisEvent = (DisplayStudyEventBean) bean;
		DisplayStudyEventBean argEvent = (DisplayStudyEventBean) ((DisplayStudyEventRow) row).bean;

		int answer = 0;
		switch (sortingColumn) {
		case COL_EVENT:
			answer = compareTo(thisEvent, argEvent);
			break;
		case COL_START_DATE:
			answer = compareDate(thisEvent.getStudyEvent().getDateStarted(), argEvent.getStudyEvent().getDateStarted());
			break;
		case COL_LOCATION:
			answer = thisEvent.getStudyEvent().getLocation().toLowerCase()
					.compareTo(argEvent.getStudyEvent().getLocation().toLowerCase());
			break;
		case COL_SUBJECT_EVENT_STATUS:
			answer = thisEvent.getStudyEvent().getSubjectEventStatus()
					.compareTo(argEvent.getStudyEvent().getSubjectEventStatus());
			break;
		case COL_STATUS:
			answer = thisEvent.getStudyEvent().getStatus().compareTo(argEvent.getStudyEvent().getStatus());
			break;
		}

		return answer;
	}

	@Override
	public String getSearchString() {
		DisplayStudyEventBean thisEvent = (DisplayStudyEventBean) bean;
		return thisEvent.getStudyEvent().getStudyEventDefinition().getName() + " "
				+ thisEvent.getStudyEvent().getLocation();
	}

	@Override
	public ArrayList generatRowsFromBeans(ArrayList beans) {
		return DisplayStudyEventRow.generateRowsFromBeans(beans);
	}

	public static ArrayList generateRowsFromBeans(ArrayList beans) {
		ArrayList answer = new ArrayList();

		for (int i = 0; i < beans.size(); i++) {
			try {
				DisplayStudyEventRow row = new DisplayStudyEventRow();
				DisplayStudyEventBean dseBean = (DisplayStudyEventBean) beans.get(i);
				row.setBean(dseBean);
				answer.add(row);
			} catch (Exception e) {
			}
		}

		return answer;
	}
}
