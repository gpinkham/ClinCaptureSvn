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
package org.akaza.openclinica.control.extract;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.DatasetRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
@Component
public class RestoreDatasetServlet extends Controller {

	public static String getLink(int dsId) {
		return "RestoreDataset?dsId=" + dsId;
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int dsId = fp.getInt("dsId");
		DatasetDAO dsDAO = getDatasetDAO();
		DatasetBean dataset = (DatasetBean) dsDAO.findByPK(dsId);

		String action = request.getParameter("action");
		if (resword.getString("restore_this_dataset").equalsIgnoreCase(action)) {
			dataset.setStatus(Status.AVAILABLE);
			dsDAO.update(dataset);
			addPageMessage(respage.getString("dataset_has_been_succesfully_reinstated"), request);
			request.setAttribute("table", getDatasetTable(request));
			forwardPage(Page.VIEW_DATASETS_SERVLET, request, response);
		} else if (resword.getString("cancel").equalsIgnoreCase(action)) {

			request.setAttribute("table", getDatasetTable(request));
			forwardPage(Page.VIEW_DATASETS_SERVLET, request, response);
		} else {
			request.setAttribute("dataset", dataset);
			forwardPage(Page.RESTORE_DATASET, request, response);
		}
	}

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;// TODO limit to owner only?
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_restore_dataset"), "1");

	}

	private EntityBeanTable getDatasetTable(HttpServletRequest request) {
        UserAccountBean ub = getUserAccountBean(request);
        StudyBean currentStudy = getCurrentStudy(request);

		FormProcessor fp = new FormProcessor(request);

		EntityBeanTable table = fp.getEntityBeanTable();
		DatasetDAO dsdao = getDatasetDAO();
		ArrayList datasets = new ArrayList();
		datasets = dsdao.findAllByStudyId(currentStudy.getId());

		ArrayList datasetRows = DatasetRow.generateRowsFromBeans(datasets);

		String[] columns = { resword.getString("dataset_name"), resword.getString("description"),
				resword.getString("created_by"), resword.getString("created_date"), resword.getString("status"),
				resword.getString("actions") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(5);
		table.addLink(resword.getString("show_only_my_datasets"), "ViewDatasets?action=owner&ownerId=" + ub.getId());
		table.addLink(resword.getString("create_dataset"), "CreateDataset");
		table.setQuery("ViewDatasets", new HashMap());
		table.setRows(datasetRows);
		table.computeDisplay();
		return table;
	}

}
