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
 *
 * Created on Jul 11, 2005
 */
package org.akaza.openclinica.bean.extract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class ReportBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	protected ArrayList metadata; // header block - includes SED CRF codes
	protected ArrayList data;
	protected ArrayList items; // items in the database
	private boolean metadataClosed;
	protected ArrayList currentRow;

	public ReportBean() {
		metadata = new ArrayList();
		data = new ArrayList();
		currentRow = new ArrayList();
		metadataClosed = false;
		items = new ArrayList();
	}

	@Override
	public abstract String toString();

	public void nextCell(String value) {
		currentRow.add(value);
	}

	public void nextRow() {
		logger.info("*** current row count: " + currentRow.size());
		if (!metadataClosed) {
			metadata.add(currentRow);
		} else {
			data.add(currentRow);
		}

		currentRow = new ArrayList();
	}

	public void closeMetadata() {
		if (currentRow.size() > 0) {
			metadata.add(currentRow);
		}
		currentRow = new ArrayList();
		metadataClosed = true;
	}

	protected String getDataColumnEntry(int col, int rowNum) {
		if (data.size() > rowNum) {
			ArrayList row = (ArrayList) data.get(rowNum);

			if (row != null && row.size() > col) {
				String s = (String) row.get(col);

				if (s != null) {
					return s;
				}
			}
		}

		return "";
	}

	/**
	 * @return Returns the items.
	 */
	public ArrayList getItems() {
		return items;
	}

	/**
	 * @param items
	 *            The items to set.
	 */
	public void setItems(ArrayList items) {
		this.items = items;
	}
}
