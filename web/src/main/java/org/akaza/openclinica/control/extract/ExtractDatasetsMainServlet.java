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
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.DatasetRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <P>
 * The main page for the extract datasets use case. Show five last datasets and offers links for viewing all and viewing
 * users' datasets, together with a link for extracting datasets.
 * </P>
 * 
 * @author thickerson
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
@Component
public class ExtractDatasetsMainServlet extends Controller {

	public static final String PATH = "ExtractDatasetsMain";
	public static final String ARG_USER_ID = "userId";

	public static String getLink(int userId) {
		return PATH + '?' + ARG_USER_ID + '=' + userId;
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);
        StudyBean currentStudy = getCurrentStudy(request);

		FormProcessor fp = new FormProcessor(request);
		DatasetDAO dsdao = getDatasetDAO();
		EntityBeanTable table = fp.getEntityBeanTable();

		ArrayList datasets = (ArrayList) dsdao.findTopFive(currentStudy);
		ArrayList datasetRows = DatasetRow.generateRowsFromBeans(datasets);

		String[] columns = { resword.getString("dataset_name"), resword.getString("description"),
				resword.getString("created_by"), resword.getString("created_date"), resword.getString("status"),
				resword.getString("actions") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(5);

		table.addLink(resword.getString("view_all"), "ViewDatasets");
		table.addLink(resword.getString("view_my_datasets"), "ViewDatasets?action=owner&ownerId=" + ub.getId());
		table.addLink(resword.getString("create_dataset"), "CreateDataset");
		table.setQuery("ExtractDatasetsMain", new HashMap());
		table.setRows(datasetRows);
		table.computeDisplay();

		request.setAttribute("table", table);

        StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();

		forwardPage(Page.EXTRACT_DATASETS_MAIN, request, response);
	}

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR) || Role.isMonitor(currentRole.getRole())) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");

	}
}
