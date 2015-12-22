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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.DatasetRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.springframework.stereotype.Component;

@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@Component
public class RemoveDatasetServlet extends Controller {

	public static String getLink(int dsId) {
		return "RemoveDataset?dsId=" + dsId;
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		FormProcessor fp = new FormProcessor(request);
		int dsId = fp.getInt("dsId");
		DatasetDAO dsDAO = getDatasetDAO();
		DatasetBean dataset = (DatasetBean) dsDAO.findByPK(dsId);

		StudyDAO sdao = getStudyDAO();
		StudyBean study = (StudyBean) sdao.findByPK(dataset.getStudyId());
		checkRoleByUserAndStudy(request, response, ub, study.getParentStudyId(), study.getId());
		if (study.getId() != currentStudy.getId() && study.getParentStudyId() != currentStudy.getId()) {
			addPageMessage(getResPage().getString("no_have_correct_privilege_current_study") + " "
					+ getResPage().getString("change_active_study_or_contact"), request);
			throw new InsufficientPermissionException(Page.MENU,
					getResException().getString("not_allowed_access_extract_data_servlet"), "1");
		}

		if (!ub.isSysAdmin() && (dataset.getOwnerId() != ub.getId())) {
			addPageMessage(getResPage().getString("no_have_correct_privilege_current_study") + " "
					+ getResPage().getString("change_active_study_or_contact"), request);
			throw new InsufficientPermissionException(Page.MENU,
					getResException().getString("not_allowed_access_extract_data_servlet"), "1");
		}

		String action = request.getParameter("action");
		if (getResWord().getString("submit").equalsIgnoreCase(action)) {
			getDatasetService().removeDataset(dataset, ub);
			addPageMessage(getResPage().getString("dataset_removed"), request);// +
			request.setAttribute("table", getDatasetTable(request));
			forwardPage(Page.VIEW_DATASETS_SERVLET, request, response);
		} else if (getResWord().getString("back").equalsIgnoreCase(action)) {

			request.setAttribute("table", getDatasetTable(request));
			forwardPage(Page.VIEW_DATASETS_SERVLET, request, response);
		} else {
			request.setAttribute("dataset", dataset);
			forwardPage(Page.REMOVE_DATASET, request, response);
		}
	}

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| Role.isMonitor(currentRole.getRole())) {
			return;
		}

		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU,
				getResException().getString("not_allowed_access_extract_data_servlet"), "1");

	}

	private EntityBeanTable getDatasetTable(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		EntityBeanTable table = getEntityBeanTable();
		DatasetDAO dsdao = getDatasetDAO();
		ArrayList datasets = dsdao.findAllByStudyId(currentStudy);

		ArrayList datasetRows = DatasetRow.generateRowsFromBeans(datasets);

		String[] columns = { getResWord().getString("dataset_name"), getResWord().getString("description"),
				getResWord().getString("created_by"), getResWord().getString("created_date"), getResWord().getString("status"),
				getResWord().getString("actions")};
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(5);
		table.addLink(getResWord().getString("show_only_my_datasets"), "ViewDatasets?action=owner&ownerId=" + ub.getId());
		table.addLink(getResWord().getString("create_dataset"), "CreateDataset");
		table.setQuery("ViewDatasets", new HashMap());
		table.setRows(datasetRows);
		table.computeDisplay();
		return table;
	}

}
