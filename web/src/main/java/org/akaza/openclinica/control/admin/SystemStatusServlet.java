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
package org.akaza.openclinica.control.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.bean.SystemStatusBean;
import com.clinovo.util.SystemStatusUtil;

/**
 * SystemStatusServlet allows both deletion and restoration of a study user role.
 */
@Component
@SuppressWarnings("unused")
public class SystemStatusServlet extends Controller {

	private static final long serialVersionUID = 1722670001851393612L;

	public static final String OK = "OK";
	public static final String ID = "id";
	public static final String OME = "ome";
	public static final String FILE_PATH = "filePath";
	public static final String OUT_OF_MEMORY = "OutOfMemory";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		//
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter(ID);
		StringBuilder htmlContent = new StringBuilder();
		SystemStatusBean systemStatusBean = new SystemStatusBean(id, getStudyDAO(), getUserAccountDAO(),
				getItemFormMetadataDAO());
		htmlContent.append("<html><body>");
		htmlContent.append("<script type=\"application/xml\">")
				.append(SystemStatusUtil.getXmlStatisticsForStudy(systemStatusBean)).append("</script>");
		htmlContent.append("<pre>").append(request.getSession().getAttribute(OME) != null ? OUT_OF_MEMORY : OK)
				.append("\n");
		htmlContent.append(String.valueOf(getDatabaseChangeLogDao().count())).append("\n\n");
		htmlContent.append(SystemStatusUtil.getStatisticsForStudy(systemStatusBean)).append("</pre></body></html>");
		response.getWriter().println(htmlContent.toString());
	}

}
