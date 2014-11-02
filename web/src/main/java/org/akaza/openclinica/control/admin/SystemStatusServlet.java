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

import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

// allows both deletion and restoration of a study user role

@Component
public class SystemStatusServlet extends Controller {

	private static final long serialVersionUID = 1722670001851393612L;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		request.getLocale();
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long databaseChangelLogCount = getDatabaseChangeLogDao().count();
		String applicationStatus = "OK";
		if (request.getSession().getAttribute("ome") != null) {
			applicationStatus = "OutOfMemory.";
		}

		PrintWriter out = response.getWriter();
		out.println(applicationStatus);
		out.println("Users Assigned: " + new UserAccountDAO(getDataSource()).getUsersAssignedMetric());
		out.println("CRF Sections: " + new ItemFormMetadataDAO(getDataSource()).getCrfSectionsMetric());
		out.println(String.valueOf(databaseChangelLogCount));
	}
}
