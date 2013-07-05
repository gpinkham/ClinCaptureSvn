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
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.DatasetRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class RemoveDatasetServlet extends SecureController {

	Locale locale;

	public static String getLink(int dsId) {
		return "RemoveDataset?dsId=" + dsId;
	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int dsId = fp.getInt("dsId");
		DatasetDAO dsDAO = new DatasetDAO(sm.getDataSource());
		DatasetBean dataset = (DatasetBean) dsDAO.findByPK(dsId);

		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		StudyBean study = (StudyBean) sdao.findByPK(dataset.getStudyId());
		checkRoleByUserAndStudy(ub, study.getParentStudyId(), study.getId());
		if (study.getId() != currentStudy.getId() && study.getParentStudyId() != currentStudy.getId()) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
					+ respage.getString("change_active_study_or_contact"));
			forwardPage(Page.MENU_SERVLET);
			return;
		}

		if (!ub.isSysAdmin() && (dataset.getOwnerId() != ub.getId())) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
					+ respage.getString("change_active_study_or_contact"));
			forwardPage(Page.MENU_SERVLET);
			return;
		}

		String action = request.getParameter("action");
		if (resword.getString("remove_this_dataset").equalsIgnoreCase(action)) {
			dataset.setStatus(Status.DELETED);
			dsDAO.update(dataset);
			addPageMessage(respage.getString("dataset_removed"));// +
			request.setAttribute("table", getDatasetTable());
			forwardPage(Page.VIEW_DATASETS);
		} else if (resword.getString("cancel").equalsIgnoreCase(action)) {

			request.setAttribute("table", getDatasetTable());
			forwardPage(Page.VIEW_DATASETS);
		} else {
			request.setAttribute("dataset", dataset);
			forwardPage(Page.REMOVE_DATASET);
		}
	}

	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;// TODO limit to owner only?
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");

	}

	private EntityBeanTable getDatasetTable() {
		FormProcessor fp = new FormProcessor(request);

		EntityBeanTable table = fp.getEntityBeanTable();
		DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
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
