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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import com.clinovo.i18n.LocaleResolver;

/**
 * DatasetRow.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DatasetRow extends EntityBeanRow {

	public static final int COL_STATUS = 4;
	public static final int COL_DATASETNAME = 0;
	public static final int COL_DATASETDESC = 1;
	public static final int COL_DATASETOWNER = 2;
	public static final int COL_DATASETCREATEDDATE = 3;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			ResourceBundleProvider.getFormatBundle().getString("date_format_string"), LocaleResolver.getLocale());

	@Override
	protected int compareColumn(Object row, int sortingColumn) {
		if (!row.getClass().equals(DatasetRow.class)) {
			return 0;
		}

		DatasetBean thisAccount = (DatasetBean) bean;
		DatasetBean argAccount = (DatasetBean) ((DatasetRow) row).bean;

		int answer;
		switch (sortingColumn) {
			case COL_DATASETNAME :
				answer = thisAccount.getName().toLowerCase().compareTo(argAccount.getName().toLowerCase());
				break;
			case COL_DATASETDESC :
				answer = thisAccount.getDescription().toLowerCase()
						.compareTo(argAccount.getDescription().toLowerCase());
				break;
			case COL_DATASETOWNER :
				answer = thisAccount.getOwner().getName().toLowerCase()
						.compareTo(argAccount.getOwner().getName().toLowerCase());
				break;
			case COL_STATUS :
				answer = thisAccount.getStatus().compareTo(argAccount.getStatus());
				break;
			case COL_DATASETCREATEDDATE :
				answer = thisAccount.getCreatedDate().compareTo(argAccount.getCreatedDate());
				break;
			default :
				answer = 0;
		}

		return answer;
	}

	@Override
	public String getSearchString() {
		DatasetBean thisAccount = (DatasetBean) bean;
		return thisAccount.getName() + " " + thisAccount.getDescription() + " " + thisAccount.getOwner().getName() + " "
				+ simpleDateFormat.format(thisAccount.getCreatedDate());
	}

	/**
	 * Generates rows from beans.
	 * 
	 * @param beans
	 *            ArrayList
	 * @return ArrayList
	 */
	public static ArrayList generateRowsFromBeans(ArrayList beans) {
		ArrayList answer = new ArrayList();
		for (DatasetBean datasetBean : (List<DatasetBean>) beans) {
			try {
				DatasetRow row = new DatasetRow();
				row.setBean(datasetBean);
				answer.add(row);
			} catch (Exception ex) {
				//
			}
		}
		return answer;
	}

	@Override
	public ArrayList generatRowsFromBeans(ArrayList beans) {
		return DatasetRow.generateRowsFromBeans(beans);
	}
}
