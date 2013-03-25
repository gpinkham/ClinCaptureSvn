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

import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "unchecked"})
public class StudyEventDefinitionRow extends EntityBeanRow {
	// columns:
	// Currently, for URL .../ListEventDefinition, the following collumn
	// match is wrong
	// and not all of them are used.
	// Change has been made and no trouble has been found to couple this this
	// change.
	public static final int COL_ORDINAL = 0;

	public static final int COL_NAME = 1;

	public static final int COL_OID = 2;

	public static final int COL_REPEATING = 3;

	public static final int COL_TYPE = 4;

	public static final int COL_CATEGORY = 5;

	public static final int COL_POPULATED = 6;

	public static final int COL_DATE_CREATED = 7;// 6; -- not been used?

	public static final int COL_OWNER = 7; // -- not been used?

	public static final int COL_DATE_UPDATED = 8;// 8;

	public static final int COL_UPDATER = 9; // -- not been used?

	public static final int COL_STATUS = 10; // -- not been used?

	public static final int COL_DEFAULT_VERSION = 11;

	public static final int COL_ACTIONS = 12;

	@Override
	protected int compareColumn(Object row, int sortingColumn) {
		if (!row.getClass().equals(StudyEventDefinitionRow.class)) {
			return 0;
		}

		StudyEventDefinitionBean thisDefinition = (StudyEventDefinitionBean) bean;
		StudyEventDefinitionBean argDefinition = (StudyEventDefinitionBean) ((StudyEventDefinitionRow) row).bean;

		int answer = 0;
		switch (sortingColumn) {
		case COL_ORDINAL:
			if (thisDefinition.getOrdinal() > argDefinition.getOrdinal()) {
				answer = 1;
			} else if (thisDefinition.getOrdinal() == argDefinition.getOrdinal()) {
				answer = 0;
			} else {
				answer = -1;
			}
			break;
		case COL_NAME:
			answer = thisDefinition.getName().toLowerCase().compareTo(argDefinition.getName().toLowerCase());
			break;
		case COL_REPEATING:
			if (thisDefinition.isRepeating() && !argDefinition.isRepeating()) {
				answer = 1;
			} else if (!thisDefinition.isRepeating() && argDefinition.isRepeating()) {
				answer = -1;
			} else {
				answer = 0;
			}
			break;
		case COL_TYPE:
			answer = thisDefinition.getType().toLowerCase().compareTo(argDefinition.getType().toLowerCase());
			break;
		case COL_CATEGORY:
			String category = "ZZZZZZZZZ";
			if (!"".equals(thisDefinition.getCategory())) {
				category = thisDefinition.getCategory();
			}

			answer = category.toLowerCase().compareTo(argDefinition.getCategory().toLowerCase());
			break;
		case COL_POPULATED:
			if (thisDefinition.isPopulated() && !argDefinition.isPopulated()) {
				answer = 1;
			} else if (!thisDefinition.isPopulated() && argDefinition.isPopulated()) {
				answer = -1;
			} else {
				answer = 0;
			}
			break;
		case COL_DATE_UPDATED:
			answer = compareDate(thisDefinition.getUpdatedDate(), argDefinition.getUpdatedDate());
			break;
		case COL_UPDATER:
			answer = thisDefinition.getUpdater().getName().toLowerCase()
					.compareTo(argDefinition.getUpdater().getName().toLowerCase());
			break;
		case COL_STATUS:
			answer = thisDefinition.getStatus().compareTo(argDefinition.getStatus());
			break;
		}

		return answer;
	}

	@Override
	public String getSearchString() {
		StudyEventDefinitionBean thisDefinition = (StudyEventDefinitionBean) bean;
		return thisDefinition.getName() + " " + thisDefinition.getType() + " " + thisDefinition.getCategory() + " "
				+ thisDefinition.getOwner().getName() + " " + thisDefinition.getUpdater().getName();
	}

	@Override
	public ArrayList generatRowsFromBeans(ArrayList beans) {
		return StudyEventDefinitionRow.generateRowsFromBeans(beans);
	}

	public static ArrayList generateRowsFromBeans(ArrayList beans) {
		ArrayList answer = new ArrayList();

		for (int i = 0; i < beans.size(); i++) {
			try {
				StudyEventDefinitionRow row = new StudyEventDefinitionRow();
				row.setBean((StudyEventDefinitionBean) beans.get(i));
				answer.add(row);
			} catch (Exception e) {
			}
		}

		return answer;
	}

}
