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

package org.akaza.openclinica.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.jmesa.core.CoreContext;
import org.jmesa.facade.TableFacadeImpl;
import org.jmesa.limit.ExportType;
import org.jmesa.view.View;
import org.jmesa.view.csv.CsvViewExporter;
import org.jmesa.view.jexcel.JExcelViewExporter;

public class OCTableFacadeImpl extends TableFacadeImpl {

	private final HttpServletResponse response;
	private final HttpServletRequest request;
	private final String fileName;

	public OCTableFacadeImpl(String id, HttpServletRequest request, HttpServletResponse response, String fileName) {
		super(id, request);
		this.response = response;
		this.fileName = fileName + System.currentTimeMillis();
		this.request = request;
	}

	@Override
	protected View getExportView(ExportType exportType) {

		if (exportType == ExportType.PDF) {
			return new XmlView(getTable(), getCoreContext());
		} else {
			return super.getExportView(exportType);
		}
	}

	@Override
	protected void renderExport(ExportType exportType, View view) {

		try {
			CoreContext cc = getCoreContext();

			if (exportType == ExportType.CSV) {
				new CsvViewExporter(view, cc, response, fileName + ".txt").export();
			} else if (exportType == ExportType.JEXCEL) {
				new JExcelViewExporter(view, cc, response, fileName + ".xls").export();
			} else if (exportType == ExportType.PDF) {
				new XmlViewExporter(view, cc, request, response).export();
			} else {
				super.renderExport(exportType, view);
			}
		} catch (Exception e) {
			throw new OpenClinicaSystemException(e);
		}
	}
}
