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
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.RememberLastPage;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.ListCRFRow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * Lists all the CRF and their CRF versions
 * 
 * @author jxu
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class ListCRFServlet extends RememberLastPage {

	Locale locale;

	public static final String SAVED_LIST_CRFS_URL = "savedListCRFsUrl";

	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin() || ub.isTechAdmin()) {
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

	/**
	 * Finds all the crfs
	 * 
	 */
	@Override
	public void processRequest() throws Exception {
		analyzeUrl();
		if (currentStudy.getParentStudyId() > 0) {
			addPageMessage(respage.getString("no_crf_available_study_is_a_site"));
			forwardPage(Page.MENU_SERVLET);
			return;
		}

		session.removeAttribute("version");
		FormProcessor fp = new FormProcessor(request);
		// checks which module the requests are from
		String module = fp.getString(MODULE);

		if (module.equalsIgnoreCase("admin") && !(ub.isSysAdmin() || ub.isTechAdmin())) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
					+ respage.getString("change_active_study_or_contact"));
			forwardPage(Page.MENU_SERVLET);
			return;
		}
		request.setAttribute(MODULE, module);

		String dir = SQLInitServlet.getField("filePath") + "crf" + File.separator + "new" + File.separator;// for
		// crf
		// version
		// spreadsheet
		logger.info("found directory: " + dir);

		CRFDAO cdao = new CRFDAO(sm.getDataSource());
		CRFVersionDAO vdao = new CRFVersionDAO(sm.getDataSource());
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

		resetPanel();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		panel.setSubmitDataModule(false);
		panel.setExtractData(false);
		panel.setCreateDataset(false);

		if (crfs.size() > 0) {
			setToPanel("CRFs", new Integer(crfs.size()).toString());
		}

		setToPanel(resword.getString("create_CRF"), respage.getString("br_create_new_CRF_entering"));

		setToPanel(resword.getString("create_CRF_version"), respage.getString("br_create_new_CRF_uploading"));
		setToPanel(resword.getString("revise_CRF_version"), respage.getString("br_if_you_owner_CRF_version"));
		setToPanel(resword.getString("CRF_spreadsheet_template"),
				respage.getString("br_download_blank_CRF_spreadsheet_from"));
		setToPanel(resword.getString("example_CRF_br_spreadsheets"),
				respage.getString("br_download_example_CRF_instructions_from"));
		analyzeForward(Page.CRF_LIST);
	}

	@Override
	protected String getAdminServlet() {
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	@Override
	protected String getUrlKey() {
		return SAVED_LIST_CRFS_URL;
	}

	@Override
	protected String getDefaultUrl() {
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
	protected boolean userDoesNotUseJmesaTableForNavigation() {
		return request.getQueryString() == null || !request.getQueryString().contains("&ebl_page=");
	}
}
