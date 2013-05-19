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
 * Created on Apr 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.akaza.openclinica.web.bean;

import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ArchivedDatasetFileRow extends EntityBeanRow {
	// declare columns first
	public static final int COL_FILENAME = 0;
	public static final int COL_FILERUNTIME = 1;
	public static final int COL_FILESIZE = 2;
	public static final int COL_FILECREATEDDATE = 3;
	public static final int COL_FILEOWNER = 4;

	@Override
	protected int compareColumn(Object row, int sortingColumn) {
		if (!row.getClass().equals(ArchivedDatasetFileRow.class)) {
			return 0;
		}

		ArchivedDatasetFileBean thisAccount = (ArchivedDatasetFileBean) bean;
		ArchivedDatasetFileBean argAccount = (ArchivedDatasetFileBean) ((ArchivedDatasetFileRow) row).bean;

		int answer = 0;
		switch (sortingColumn) {
		case COL_FILENAME:
			answer = thisAccount.getName().toLowerCase().compareTo(argAccount.getName().toLowerCase());
			break;

		case COL_FILECREATEDDATE:
			answer = thisAccount.getDateCreated().compareTo(argAccount.getDateCreated());
			break;
		}

		return answer;
	}

	@Override
	public String getSearchString() {
		ArchivedDatasetFileBean thisAccount = (ArchivedDatasetFileBean) bean;
		return thisAccount.getName();
	}

	public static ArrayList generateRowsFromBeans(ArrayList beans) {
		ArrayList answer = new ArrayList();

		for (int i = 0; i < beans.size(); i++) {
			try {
				ArchivedDatasetFileRow row = new ArchivedDatasetFileRow();
				row.setBean((ArchivedDatasetFileBean) beans.get(i));
				answer.add(row);
			} catch (Exception e) {
			}
		}

		return answer;
	}

	@Override
	public ArrayList generatRowsFromBeans(ArrayList beans) {
		return ArchivedDatasetFileRow.generateRowsFromBeans(beans);
	}
}
