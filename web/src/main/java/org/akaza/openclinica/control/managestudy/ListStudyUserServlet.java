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
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.StudyUserRoleRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Lists all the users in a study
 * 
 * @author jxu
 * 
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class ListStudyUserServlet extends SecureController {

	/**
     *
     */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET,
				resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		ArrayList users = udao.findAllUsersByStudy(currentStudy.getId());

		EntityBeanTable table = fp.getEntityBeanTable();
		ArrayList allStudyUserRows = StudyUserRoleRow.generateRowsFromBeans(users);

		String[] columns = { resword.getString("user_name"), resword.getString("first_name"),
				resword.getString("last_name"), resword.getString("role"), resword.getString("study_name"),
				resword.getString("status"), resword.getString("actions") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(6);
		table.setQuery("ListStudyUser", new HashMap());
		table.setRows(allStudyUserRows);
		table.computeDisplay();

		request.setAttribute("table", table);
		request.setAttribute("siteRoleMap", Role.siteRoleMap);
		request.setAttribute("studyRoleMap", Role.studyRoleMap);
		request.setAttribute("study", currentStudy);
		forwardPage(Page.LIST_USER_IN_STUDY);

	}
}
