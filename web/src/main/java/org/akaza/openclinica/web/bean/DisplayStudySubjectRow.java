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

import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.DisplayStudySubjectBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DisplayStudySubjectRow extends EntityBeanRow {

	// The order of columns has been changed to couple with modified view
	public static final int COL_SUBJECT_LABEL = 0;
	public static final int COL_STATUS = 1;
	public static final int COL_OID = 2;

	public static final int COL_GENDER = 3;

	public static final int COL_SECONDARY_LABEL = 4;

	public static final int COL_STUDYGROUP = 5;

	public static final int COL_STUDYEVENT = 6;

	@Override
	protected int compareColumn(Object row, int sortingColumn) {
		if (!row.getClass().equals(DisplayStudySubjectRow.class)) {
			return 0;
		}

		DisplayStudySubjectBean thisStudy = (DisplayStudySubjectBean) bean;
		DisplayStudySubjectBean argStudy = (DisplayStudySubjectBean) ((DisplayStudySubjectRow) row).bean;
		int answer = 0;
		int groupSize = thisStudy.getStudyGroups().size();
		int code;
		if (sortingColumn > 4 + groupSize) {
			if (thisStudy.getSedId() <= 0) {
				code = COL_STUDYEVENT;
			} else {
				code = -1;
			}
		} else if (sortingColumn > 4 && sortingColumn <= 4 + groupSize) {
			code = COL_STUDYGROUP;
		} else {
			code = sortingColumn;
		}
		switch (code) {
		
		case COL_SUBJECT_LABEL:
			answer = thisStudy.getStudySubject().getLabel().toLowerCase()
					.compareTo(argStudy.getStudySubject().getLabel().toLowerCase());
			break;
		case COL_GENDER:
			answer = (thisStudy.getStudySubject().getGender() + "").compareTo(argStudy.getStudySubject().getGender()
					+ "");
			break;
		case COL_OID:
			answer = thisStudy.getStudySubject().getOid().toLowerCase()
					.compareTo(argStudy.getStudySubject().getOid().toLowerCase());
			break;
		case COL_SECONDARY_LABEL:
			answer = thisStudy.getStudySubject().getSecondaryLabel().toLowerCase()
					.compareTo(argStudy.getStudySubject().getSecondaryLabel().toLowerCase());
			break;
		case COL_STATUS:
			answer = thisStudy.getStudySubject().getStatus().compareTo(argStudy.getStudySubject().getStatus());
			break;

		case COL_STUDYGROUP:
			answer = ((SubjectGroupMapBean) thisStudy.getStudyGroups().get(sortingColumn - 5))
					.getStudyGroupName()
					.toLowerCase()
					.compareTo(
							((SubjectGroupMapBean) argStudy.getStudyGroups().get(sortingColumn - 5))
									.getStudyGroupName().toLowerCase());
			break;
		case COL_STUDYEVENT:
			SubjectEventStatus thisSes = ((StudyEventBean) thisStudy.getStudyEvents()
					.get(sortingColumn - 5 - groupSize)).getSubjectEventStatus();
			SubjectEventStatus argSes = ((StudyEventBean) argStudy.getStudyEvents().get(sortingColumn - 5 - groupSize))
					.getSubjectEventStatus();
			/*
			 * Subject event status is ordered in this sequence Not Started=2,Scheduled=1,Data Entry Started=3,
			 * Stopped=5,Skipped=6, Completed=4,Locked=7,Signed=8
			 */
			switch (thisSes.getId()) {
			case 1:
				if (thisSes.getId() == argSes.getId()) {
					answer = 0;
				} else if (argSes.getId() == 2) {
					answer = 1;
				} else {
					answer = -1;
				}
				break;
			case 2:
				if (thisSes.getId() == argSes.getId()) {
					answer = 0;
				} else {
					answer = -1;
				}
				break;
			case 3:
				if (thisSes.getId() == argSes.getId()) {
					answer = 0;
				} else if (argSes.getId() == 1 || argSes.getId() == 2) {
					answer = 1;
				} else {
					answer = -1;
				}
				break;
			case 4:
				if (thisSes.getId() == argSes.getId()) {
					answer = 0;
				} else if (argSes.getId() == 7 || argSes.getId() == 8) {
					answer = -1;
				} else {
					answer = 1;
				}
				break;
			case 5:
				if (thisSes.getId() == argSes.getId()) {
					answer = 0;
				} else if (argSes.getId() == 1 || argSes.getId() == 2 || argSes.getId() == 3) {
					answer = 1;
				} else {
					answer = -1;
				}
				break;
			case 6:
				if (thisSes.getId() == argSes.getId()) {
					answer = 0;
				} else if (argSes.getId() == 4 || argSes.getId() == 7 || argSes.getId() == 8) {
					answer = -1;
				} else {
					answer = 1;
				}
				break;
			case 7:
				if (thisSes.getId() == argSes.getId()) {
					answer = 0;
				} else if (argSes.getId() == 8) {
					answer = -1;
				} else {
					answer = 1;
				}
				break;
			case 8:
				if (thisSes.getId() == argSes.getId()) {
					answer = 0;
				} else {
					answer = 1;
				}
			default:
				answer = 1;
			}
		}
		return answer;
	}

	@Override
	public String getSearchString() {
		DisplayStudySubjectBean thisStudy = (DisplayStudySubjectBean) bean;
		String searchString = thisStudy.getStudySubject().getLabel();
		String secondaryLabel = thisStudy.getStudySubject().getSecondaryLabel();
		if (!"".equalsIgnoreCase(secondaryLabel)) {
			searchString += " ";
			searchString += secondaryLabel;
		}

		return searchString;
	}

	@Override
	public ArrayList generatRowsFromBeans(ArrayList beans) {
		return DisplayStudySubjectRow.generateRowsFromBeans(beans);
	}

	public static ArrayList generateRowsFromBeans(ArrayList beans) {
		ArrayList answer = new ArrayList();

		for (int i = 0; i < beans.size(); i++) {
			try {
				DisplayStudySubjectRow row = new DisplayStudySubjectRow();
				row.setBean((DisplayStudySubjectBean) beans.get(i));
				answer.add(row);
			} catch (Exception e) {
			}
		}

		return answer;
	}

}
