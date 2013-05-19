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
import org.jmesa.view.AbstractViewExporter;
import org.jmesa.view.View;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 2.0
 * @author Jeff Johnston
 */
public class XmlViewExporter extends AbstractViewExporter {

	private final HttpServletRequest request;

	public XmlViewExporter(View view, CoreContext coreContext, HttpServletRequest request, HttpServletResponse response) {
		super(view, coreContext, response, null);
		this.request = request;
	}

	public XmlViewExporter(View view, CoreContext coreContext, HttpServletRequest request,
			HttpServletResponse response, String fileName) {
		super(view, coreContext, response, fileName);
		this.request = request;
	}

	public void export() throws Exception {
		// responseHeaders(getResponse());
		// String viewData = (String) getView().render();
		// byte[] contents = (viewData).getBytes();
		// getResponse().getOutputStream().write(contents);
		RequestDispatcher dispatcher = request.getRequestDispatcher("DownloadRuleSetXml?ruleSetRuleIds="
				+ (String) getView().render());
		dispatcher.forward(request, getResponse());
	}

	@Override
	public String getContextType() {
		return "text/plain";
	}

	@Override
	public String getExtensionName() {
		return "txt";
	}
}
