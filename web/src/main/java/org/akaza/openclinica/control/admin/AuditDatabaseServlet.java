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
 * copyright 2003-2009 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import static org.jmesa.facade.TableFacadeFactory.createTableFacade;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.domain.technicaladmin.DatabaseChangeLogBean;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.jmesa.facade.TableFacade;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.component.HtmlTable;
import org.springframework.stereotype.Component;

/**
 * Servlet for creating a user account.
 * 
 * @author Krikor Krumlian
 */
@Component
public class AuditDatabaseServlet extends Controller {

	private static final long serialVersionUID = 1L;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (!ub.isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU,
					getResException().getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String auditDatabaseHtml = renderAuditDatabaseTable(getDatabaseChangeLogDao().findAll(), request);
		request.setAttribute("auditDatabaseHtml", auditDatabaseHtml);
		forwardPage(Page.AUDIT_DATABASE, request, response);

	}

	private String renderAuditDatabaseTable(List<DatabaseChangeLogBean> databaseChangeLogs, HttpServletRequest request) {

		TableFacade tableFacade = createTableFacade("databaseChangeLogs", request);
		tableFacade.setColumnProperties("id", "author", "fileName", "dataExecuted", "md5Sum", "description",
				"comments", "tag", "liquibase");

		tableFacade.setItems(databaseChangeLogs);
		// Fix column titles
		HtmlTable table = (HtmlTable) tableFacade.getTable();

		table.setCaption("");
		HtmlRow row = table.getRow();

		HtmlColumn id = row.getColumn("id");
		id.setTitle("Id");

		HtmlColumn author = row.getColumn("author");
		author.setTitle("Author");

		HtmlColumn fileName = row.getColumn("fileName");
		fileName.setTitle("File Name");

		HtmlColumn dataExecuted = row.getColumn("dataExecuted");
		dataExecuted.setTitle("Date Executed");
		dataExecuted.getCellRenderer().setCellEditor(new DateCellEditor("yyyy-MM-dd hh:mm:ss"));

		HtmlColumn md5Sum = row.getColumn("md5Sum");
		md5Sum.setTitle("md5 sum");

		HtmlColumn description = row.getColumn("description");
		description.setTitle("Description");

		HtmlColumn comments = row.getColumn("comments");
		comments.setTitle("Comments");

		HtmlColumn tag = row.getColumn("tag");
		tag.setTitle("Tag");

		HtmlColumn liquibase = row.getColumn("liquibase");
		liquibase.setTitle("Liquibase");

		return tableFacade.render();
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
