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
package org.akaza.openclinica.bean.extract;

import org.akaza.openclinica.bean.core.Term;

@SuppressWarnings("serial")
public class ExportFormatBean extends Term {

	public static final ExportFormatBean TXTFILE = new ExportFormatBean(1, "text/plain");
	public static final ExportFormatBean CSVFILE = new ExportFormatBean(2, "text/plain");
	public static final ExportFormatBean EXCELFILE = new ExportFormatBean(3, "application/vnd.ms-excel");
	// To allow this type, another data type (an addition row) is added
	// as "text/plain" in the table export_format
	public static final ExportFormatBean XMLFILE = new ExportFormatBean(4, "text/plain");
	public static final ExportFormatBean PDFFILE = new ExportFormatBean(5, "application/pdf");
	// may have to add a #6 to export formats, tbh
	private int exportFormatId;
	private String mimeType;

	private ExportFormatBean(int efid, String mime) {
		super(efid, mime);
		this.setMimeType(mime);
	}

	/**
	 * @return Returns the exportFormatId.
	 */
	public int getExportFormatId() {
		return exportFormatId;
	}

	/**
	 * @param exportFormatId
	 *            The exportFormatId to set.
	 */
	public void setExportFormatId(int exportFormatId) {
		this.exportFormatId = exportFormatId;
	}

	/**
	 * @return Returns the mimeType.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @param mimeType
	 *            The mimeType to set.
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public String getName() {
		return name;
	}
}
