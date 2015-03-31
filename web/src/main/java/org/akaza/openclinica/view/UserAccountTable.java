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
package org.akaza.openclinica.view;

import com.clinovo.controller.EditUserAccountController;
import org.akaza.openclinica.bean.core.EntityAction;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.admin.DeleteStudyUserRoleServlet;
import org.akaza.openclinica.control.admin.DeleteUserServlet;
import org.akaza.openclinica.control.admin.EditStudyUserRoleServlet;
import org.akaza.openclinica.control.admin.ViewUserAccountServlet;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "unchecked"})
public class UserAccountTable extends Table {
	public UserAccountTable() {
		columns.add("Username");
		columns.add("First Name");
		columns.add("Last Name");
		columns.add("Status");
		columns.add("Actions");
	}

	@Override
	public String getEntitiesNamePlural() {
		return "users";
	}

	@Override
	public String showRow(EntityBean e) {
		UserAccountBean u = (UserAccountBean) e;
		Status s = u.getStatus();

		// do the first row, just the "flat" properties
		String row = "<tr>\n";

		// username
		String colorOn = s.equals(Status.AVAILABLE) ? "" : "<font color='gray'>";
		String colorOff = s.equals(Status.AVAILABLE) ? "" : "</font>";
		row += "<td>" + colorOn + u.getName() + colorOff + "</td>\n";

		row += "<td>" + u.getFirstName() + "</td>\n";
		row += "<td>" + u.getLastName() + "</td>\n";

		// status
		row += "<td>" + s.getName() + "</td>\n";

		// actions
		row += "<td>";
		if (!s.equals(Status.DELETED)) {
			String confirmQuestion = "Are you sure you want to delete " + u.getName() + "?";
			String onClick = "onClick=\"return confirm('" + confirmQuestion + "');\"";
			row += "<a href='" + ViewUserAccountServlet.getLink(u.getId()) + "'>view</a>";
			row += " <a href='" + EditUserAccountController.getLink(u.getId()) + "'>edit</a>";
			row += " <a href='" + DeleteUserServlet.getLink(u, EntityAction.DELETE) + "'" + onClick + ">delete</a>";
		} else {
			String confirmQuestion = "Are you sure you want to restore " + u.getName() + "?";
			String onClick = "onClick=\"return confirm('" + confirmQuestion + "');\"";
			row += " <a href='" + DeleteUserServlet.getLink(u, EntityAction.RESTORE) + "'" + onClick + ">restore</a>";
		}
		row += "</td>\n";

		row += "</tr>\n";

		// do the next row, with the user's roles
		row += "<tr>\n";
		row += "<td>&nbsp;</td>\n";

		ArrayList userRoles = u.getRoles();
		row += "<td colspan='3'>"; // study user roles cell

		if (userRoles.size() <= 0) {
			row += "<i>No roles assigned</i>";
		}

		for (int i = 0; i < userRoles.size(); i++) {
			StudyUserRoleBean sur = (StudyUserRoleBean) userRoles.get(i);
			colorOn = sur.getStatus().equals(Status.AVAILABLE) ? "" : "<font color='gray'>";
			colorOff = sur.getStatus().equals(Status.AVAILABLE) ? "" : "</font>";

			String studyName = getStudyName(sur);
			row += studyName + " - " + colorOn + sur.getRole().getDescription() + colorOff + "<br/>\n";
		}

		row += "</td>\n";

		// actions on the study user roles
		row += "<td>";
		for (int i = 0; i < userRoles.size(); i++) {
			StudyUserRoleBean sur = (StudyUserRoleBean) userRoles.get(i);

			if (!sur.getStatus().equals(Status.DELETED)) {
				String studyName = getStudyName(sur);
				String confirmQuestion = "Are you sure you want to delete the " + sur.getRole().getDescription()
						+ " role for " + u.getName() + " in " + studyName + "?";
				row += " <a href='" + EditStudyUserRoleServlet.getLink(sur, u) + "'>edit role</a>";
				row += " <a href='"
						+ DeleteStudyUserRoleServlet.getLink(u.getId(), sur.getStudyId(), EntityAction.DELETE)
						+ "' onClick='return confirm(\"" + confirmQuestion + "\");'>delete role</a>";
			} else {
				String confirmQuestion = "Are you sure you want to restore the " + sur.getRole().getDescription()
						+ " role for " + u.getName() + " in Study " + sur.getStudyId() + "?";
				row += " <a href='"
						+ DeleteStudyUserRoleServlet.getLink(u.getId(), sur.getStudyId(), EntityAction.RESTORE)
						+ "' onClick=\"return confirm('" + confirmQuestion + "');\">restore role</a>";
			}
			row += "<br/>\n";
		}
		row += "</td>\n";

		row += "<tr>\n";
		row += "</tr>\n";

		return row;
	}

	private String getStudyName(StudyUserRoleBean sur) {
		String studyName;
		if (sur.getStudyName().equals("")) {
			studyName = "Study " + sur.getStudyId();
		} else {
			studyName = sur.getStudyName();
		}
		return studyName;
	}

}
