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
 * copyright �� 2003-2005 Akaza Research
 */

package org.akaza.openclinica.web.bean;

import org.akaza.openclinica.bean.admin.CRFBean;

import java.util.ArrayList;

/**
 * A help class for displaying CRF object in show table class
 * 
 * @author Jun Xu
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CRFRow extends EntityBeanRow {
	// columns:
	public static final int COL_NAME = 0;

	public static final int COL_DATE_CREATED = 1;

	public static final int COL_OWNER = 2;

	public static final int COL_DATE_UPDATED = 3;

	public static final int COL_UPDATER = 4;

	public static final int COL_STATUS = 5;

	@Override
	protected int compareColumn(Object row, int sortingColumn) {
		if (!row.getClass().equals(CRFRow.class)) {
			return 0;
		}

		CRFBean thisCRF = (CRFBean) bean;
		CRFBean argCRF = (CRFBean) ((CRFRow) row).bean;

		int answer = 0;
		switch (sortingColumn) {
		case COL_NAME:
			answer = thisCRF.getName().toLowerCase().compareTo(argCRF.getName().toLowerCase());
			break;
		case COL_DATE_CREATED:
			answer = compareDate(thisCRF.getCreatedDate(), argCRF.getCreatedDate());
			break;
		case COL_OWNER:
			answer = thisCRF.getOwner().getName().toLowerCase().compareTo(argCRF.getOwner().getName().toLowerCase());
			break;
		case COL_DATE_UPDATED:
			answer = compareDate(thisCRF.getUpdatedDate(), argCRF.getUpdatedDate());
			break;
		case COL_UPDATER:
			answer = thisCRF.getUpdater().getName().toLowerCase()
					.compareTo(argCRF.getUpdater().getName().toLowerCase());
			break;
		case COL_STATUS:
			answer = thisCRF.getStatus().compareTo(argCRF.getStatus());
			break;
		}

		return answer;
	}

	@Override
	public String getSearchString() {
		CRFBean thisCRF = (CRFBean) bean;
		return thisCRF.getName();
	}

	@Override
	public ArrayList generatRowsFromBeans(ArrayList beans) {
		return CRFRow.generateRowsFromBeans(beans);
	}

	public static ArrayList generateRowsFromBeans(ArrayList beans) {
		ArrayList answer = new ArrayList();

		for (int i = 0; i < beans.size(); i++) {
			try {
				CRFRow row = new CRFRow();
				row.setBean((CRFBean) beans.get(i));
				answer.add(row);
			} catch (Exception e) {
			}
		}

		return answer;
	}
}
