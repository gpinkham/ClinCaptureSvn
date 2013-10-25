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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.ListCRFRow;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Lists all the CRF and their CRF versions
 * 
 * @author jxu
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
@Component
public class ListCRFServlet extends RememberLastPage {

	public static final String SAVED_LIST_CRFS_URL = "savedListCRFsUrl";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin() || ub.isTechAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET,
				resexception.getString("not_study_director"), "1");

	}

	/**
	 * Finds all the crfs
	 *
     * @param request
     * @param response
     */
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (shouldRedirect(request, response)) {
			return;
		}

		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		if (currentStudy.getParentStudyId() > 0) {
			addPageMessage(respage.getString("no_crf_available_study_is_a_site"), request);
			forwardPage(Page.MENU_SERVLET, request, response);
			return;
		}

		request.getSession().removeAttribute("version");
		FormProcessor fp = new FormProcessor(request);
		// checks which module the requests are from
		String module = fp.getString(MODULE);

		if (module.equalsIgnoreCase("admin") && !(ub.isSysAdmin() || ub.isTechAdmin())) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
					+ respage.getString("change_active_study_or_contact"), request);
			forwardPage(Page.MENU_SERVLET, request, response);
			return;
		}
		request.setAttribute(MODULE, module);

		String dir = SQLInitServlet.getField("filePath") + "crf" + File.separator + "new" + File.separator;// for
		// crf
		// version
		// spreadsheet
		logger.info("found directory: " + dir);

		CRFDAO cdao = getCRFDAO();
		CRFVersionDAO vdao = getCRFVersionDAO();
		ArrayList crfs = (ArrayList) cdao.findAll();
		for (int i = 0; i < crfs.size(); i++) {
			CRFBean eb = (CRFBean) crfs.get(i);
			logger.info("crf id:" + eb.getId());
			ArrayList versions = (ArrayList) vdao.findAllByCRF(eb.getId());

			// check whether the speadsheet is available on the server
			for (int j = 0; j < versions.size(); j++) {
				CRFVersionBean cv = (CRFVersionBean) versions.get(j);
				File file = new File(dir + eb.getId() + cv.getOid() + ".xls");
				logger.info("looking in " + dir + eb.getId() + cv.getOid() + ".xls");
				if (file.exists()) {
					cv.setDownloadable(true);
				} else {
					File file2 = new File(dir + eb.getId() + cv.getName() + ".xls");
					logger.info("initial failed, looking in " + dir + eb.getId() + cv.getName() + ".xls");
					if (file2.exists()) {
						cv.setDownloadable(true);
					}
				}

			}
			eb.setVersions(versions);

		}

		EntityBeanTable table = fp.getEntityBeanTable();
		ArrayList allRows = ListCRFRow.generateRowsFromBeans(crfs);

		String[] columns = { resword.getString("CRF_name"), resword.getString("date_updated"),
				resword.getString("last_updated_by"), resword.getString("crf_oid"), resword.getString("versions"),
				resword.getString("version_oid"), resword.getString("date_created"), resword.getString("owner"),
				resword.getString("status"), resword.getString("download"), resword.getString("actions") };

		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(3);
		table.hideColumnLink(4); // oid column
		table.hideColumnLink(8);
		table.setQuery("ListCRF", new HashMap());
		table.addLink(resword.getString("blank_CRF_template"), "DownloadVersionSpreadSheet?template=1");
		table.addLink(resword.getString("randomization_crf_template"), "DownloadVersionSpreadSheet?template=2");
		table.setRows(allRows);
		table.computeDisplay();

		request.setAttribute("table", table);

        StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		panel.setSubmitDataModule(false);
		panel.setExtractData(false);
		panel.setCreateDataset(false);

		if (crfs.size() > 0) {
			setToPanel("CRFs", new Integer(crfs.size()).toString(), request);
		}

		setToPanel(resword.getString("create_CRF"), respage.getString("br_create_new_CRF_entering"), request);

		setToPanel(resword.getString("create_CRF_version"), respage.getString("br_create_new_CRF_uploading"), request);
		setToPanel(resword.getString("revise_CRF_version"), respage.getString("br_if_you_owner_CRF_version"), request);
		setToPanel(resword.getString("CRF_spreadsheet_template"),
				respage.getString("br_download_blank_CRF_spreadsheet_from"), request);
		setToPanel(resword.getString("example_CRF_br_spreadsheets"),
				respage.getString("br_download_example_CRF_instructions_from"), request);
		forward(Page.CRF_LIST, request, response);
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
        UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return Controller.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	@Override
	protected String getUrlKey(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		String module = fp.getString("module");
		return module.isEmpty() ? SAVED_LIST_CRFS_URL : (SAVED_LIST_CRFS_URL + "_" + module);
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		String eblFiltered = fp.getString("ebl_filtered");
		String eblFilterKeyword = fp.getString("ebl_filterKeyword");
		String eblSortColumnInd = fp.getString("ebl_sortColumnInd");
		String eblSortAscending = fp.getString("ebl_sortAscending");
		return "?submitted=1&module=" + fp.getString("module") + "&ebl_page=1&ebl_sortColumnInd="
				+ (!eblSortColumnInd.isEmpty() ? eblSortColumnInd : "0") + "&ebl_sortAscending="
				+ (!eblSortAscending.isEmpty() ? eblSortAscending : "1") + "&ebl_filtered="
				+ (!eblFiltered.isEmpty() ? eblFiltered : "0") + "&ebl_filterKeyword="
				+ (!eblFilterKeyword.isEmpty() ? eblFilterKeyword : "") + "&ebl_paginated=1";
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null || !request.getQueryString().contains("&ebl_page=");
	}
}
