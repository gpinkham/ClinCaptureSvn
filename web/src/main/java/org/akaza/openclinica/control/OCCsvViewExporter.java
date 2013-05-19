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

import org.jmesa.core.CoreContext;
import org.jmesa.util.ExportUtils;
import org.jmesa.view.AbstractViewExporter;
import org.jmesa.view.View;

import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletResponse;

public class OCCsvViewExporter extends AbstractViewExporter {

	String fileName;

	public OCCsvViewExporter(View view, CoreContext coreContext, HttpServletResponse response) {
		super(view, coreContext, response, null);
		if (fileName == null) {
			fileName = ExportUtils.exportFileName(view, getExtensionName());
		}

	}

	public OCCsvViewExporter(View view, CoreContext coreContext, HttpServletResponse response, String fileName) {
		super(view, coreContext, response, fileName);
		this.fileName = fileName + "." + getExtensionName();
	}

	@SuppressWarnings("resource")
	public void export() throws Exception {
		String viewData = (String) getView().render();
		byte[] contents = (viewData).getBytes();
		File f = new File(fileName);
		FileOutputStream fos = new FileOutputStream(f, true);
		fos.write(contents);
		fos.flush();
	}

	@Override
	public String getContextType() {
		return "text/csv";
	}

	@Override
	public String getExtensionName() {
		return "txt";
	}
}
