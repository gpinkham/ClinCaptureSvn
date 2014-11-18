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
 * Created on Jun 9, 2005
 *
 *
 */
package org.akaza.openclinica.control.extract;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.ArchivedDatasetFileRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * purpose of this servlet is to respond with a file listing after we've outlasted the 'please wait' message.
 * 
 * @author thickerson
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class ShowFileServlet extends Controller {

	public static String getLink(int fId, int dId) {
		return "ShowFile?fileId=" + fId + "&datasetId=" + dId;
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int fileId = fp.getInt("fileId");
		int dsId = fp.getInt("datasetId");
		DatasetDAO dsdao = getDatasetDAO();
		DatasetBean db = (DatasetBean) dsdao.findByPK(dsId);

		ArchivedDatasetFileDAO asdfdao = getArchivedDatasetFileDAO();
		ArchivedDatasetFileBean asdfBean = (ArchivedDatasetFileBean) asdfdao.findByPK(fileId);

		ArrayList newFileList = new ArrayList();
		newFileList.add(asdfBean);

		ArrayList filterRows = ArchivedDatasetFileRow.generateRowsFromBeans(newFileList);
		EntityBeanTable table = fp.getEntityBeanTable();
		String[] columns = { resword.getString("file_name"), resword.getString("run_time"),
				resword.getString("file_size"), resword.getString("created_date"), resword.getString("created_by") };

		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(0);
		table.hideColumnLink(1);
		table.hideColumnLink(2);
		table.hideColumnLink(3);
		table.hideColumnLink(4);

		request.setAttribute("dataset", db);
		request.setAttribute("file", asdfBean);
		table.setRows(filterRows);
		table.computeDisplay();

		request.setAttribute("table", table);
		Page finalTarget = Page.EXPORT_DATA_CUSTOM;

		finalTarget.setFileName("/WEB-INF/jsp/extract/generateMetadataFile.jsp");

		forwardPage(finalTarget, request, response);
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
