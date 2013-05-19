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

import java.io.PrintWriter;

import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.hibernate.DatabaseChangeLogDao;
import org.akaza.openclinica.web.InsufficientPermissionException;

// allows both deletion and restoration of a study user role

public class SystemStatusServlet extends SecureController {

	private static final long serialVersionUID = 1722670001851393612L;
	private DatabaseChangeLogDao databaseChangeLogDao;

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		request.getLocale();
		return;
	}

	@Override
	protected void processRequest() throws Exception {

		Long databaseChangelLogCount = getDatabaseChangeLogDao().count();
		String applicationStatus = "OK";
		if (session.getAttribute("ome") != null) {
			applicationStatus = "OutOfMemory.";
		}

		PrintWriter out = response.getWriter();
		out.println(applicationStatus);
		out.println(String.valueOf(databaseChangelLogCount));
	}

	public DatabaseChangeLogDao getDatabaseChangeLogDao() {
		databaseChangeLogDao = this.databaseChangeLogDao != null ? databaseChangeLogDao
				: (DatabaseChangeLogDao) SpringServletAccess.getApplicationContext(context).getBean(
						"databaseChangeLogDao");
		return databaseChangeLogDao;
	}

	public void setDatabaseChangeLogDao(DatabaseChangeLogDao databaseChangeLogDao) {
		this.databaseChangeLogDao = databaseChangeLogDao;
	}
}
