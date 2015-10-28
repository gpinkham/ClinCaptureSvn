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
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.DatasetRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.springframework.stereotype.Component;

import com.clinovo.util.RequestUtil;

/**
 * ViewDatasetsServlet.java, the view datasets function accessed from the extract datasets main page.
 * 
 * @author thickerson
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class ViewDatasetsServlet extends RememberLastPage {

	public static final String SAVED_VIEW_DATASETS_URL = "savedViewDatasetsUrl";
	private static final int FIVE = 5;

	/**
	 * Get ViewDatasets link by Dataset ID.
	 * 
	 * @param dsId
	 *            Dataset ID
	 * @return Link
	 */
	public static String getLink(int dsId) {
		return "ViewDatasets?action=details&datasetId=" + dsId;
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String action = request.getParameter("action");
		if (!(action != null && action.equalsIgnoreCase("details")) && shouldRedirect(request, response)) {
			return;
		}
		if ("ongoing".equals(request.getSession().getAttribute("exportStatus"))) {
			addPageMessage(respage.getString("extract_is_running"), request);
			request.getSession().removeAttribute("exportStatus");
		}
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		request.setAttribute("subjectAgeAtEvent", currentStudy.getStudyParameterConfig().getCollectDob().equals("3")
				? "0"
				: "1");
		DatasetDAO dsdao = getDatasetDAO();
		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		request.getSession().removeAttribute("newDataset");
		if (StringUtil.isBlank(action)) {
			StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
			StudyBean studyWithEventDefinitions = currentStudy;
			if (currentStudy.getParentStudyId() > 0) {
				studyWithEventDefinitions = new StudyBean();
				studyWithEventDefinitions.setId(currentStudy.getParentStudyId());

			}
			ArrayList seds = seddao.findAllActiveByStudy(studyWithEventDefinitions);
			CRFDAO crfdao = getCRFDAO();
			HashMap events = new LinkedHashMap();
			for (Object sed1 : seds) {
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) sed1;
				ArrayList crfs = (ArrayList) crfdao.findAllActiveUnmaskedByDefinition(sed, ub);
				if (!crfs.isEmpty()) {
					events.put(sed, crfs);
				}
			}
			request.getSession().setAttribute("eventsForCreateDataset", events);
			EntityBeanTable table = getEntityBeanTable();
			ArrayList datasets;
			datasets = dsdao.findAllByStudyId(currentStudy.getId());
			ArrayList datasetRows = DatasetRow.generateRowsFromBeans(datasets);
			String[] columns = {resword.getString("dataset_name"), resword.getString("description"),
					resword.getString("created_by"), resword.getString("created_date"), resword.getString("status"),
					resword.getString("actions")};
			table.setColumns(new ArrayList(Arrays.asList(columns)));
			table.hideColumnLink(FIVE);
			table.addLink(resword.getString("show_only_my_datasets"), RequestUtil
					.getRelativeWithNewParameters("action=owner", "ownerId=".concat(Integer.toString(ub.getId()))));
			table.setQuery("ViewDatasets", getOwnerQueryArgs("", ""));
			table.setRows(datasetRows);
			table.computeDisplay();
			request.setAttribute("table", table);
			forwardPage(Page.VIEW_DATASETS, request, response);
		} else {
			if ("owner".equalsIgnoreCase(action)) {
				FormProcessor fp = new FormProcessor(request);
				int ownerId = fp.getInt("ownerId");
				EntityBeanTable table = getEntityBeanTable();
				ArrayList datasets = (ArrayList) dsdao.findByOwnerId(ownerId, currentStudy.getId());
				ArrayList datasetRows = DatasetRow.generateRowsFromBeans(datasets);
				String[] columns = {resword.getString("dataset_name"), resword.getString("description"),
						resword.getString("created_by"), resword.getString("created_date"),
						resword.getString("status"), resword.getString("actions")};
				table.setColumns(new ArrayList(Arrays.asList(columns)));
				table.hideColumnLink(FIVE);
				table.addLink(resword.getString("show_all_datasets"),
						RequestUtil.getRelativeWithNewParameters("action=", "ownerId="));
				table.setQuery("ViewDatasets", getOwnerQueryArgs(Integer.toString(ub.getId()), "owner"));
				table.setRows(datasetRows);
				table.computeDisplay();
				request.setAttribute("table", table);
				forwardPage(Page.VIEW_DATASETS, request, response);
			} else if ("details".equalsIgnoreCase(action)) {
				FormProcessor fp = new FormProcessor(request);
				int datasetId = fp.getInt("datasetId");
				DatasetBean db = initializeAttributes(datasetId, request);
				StudyDAO sdao = getStudyDAO();
				StudyBean study = (StudyBean) sdao.findByPK(db.getStudyId());
				if (study.getId() != currentStudy.getId() && study.getParentStudyId() != currentStudy.getId()) {
					addPageMessage(
							respage.getString("no_have_correct_privilege_current_study") + " "
									+ respage.getString("change_active_study_or_contact"), request);
					forwardPage(Page.MENU_SERVLET, request, response);
					return;
				}
				request.setAttribute("dataset", db);
				forwardPage(Page.VIEW_DATASET_DETAILS, request, response);
			}
		}
	}

	private HashMap<String, String> getOwnerQueryArgs(String ownerId, String action) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("ownerId", ownerId);
		args.put("action", action);
		return args;
	}

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (!currentRole.getRole().equals(Role.STUDY_CODER)
				&& !currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");

	}

	/**
	 * Initialize data of a DatasetBean and set session attributes for displaying selected data of this DatasetBean.
	 * 
	 * @param datasetId
	 *            int
	 * @param request
	 *            HttpServletRequest
	 * @return DatasetBean
	 */
	public DatasetBean initializeAttributes(int datasetId, HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		DatasetBean db = getDatasetService().initialDatasetData(datasetId, ub);
		request.getSession().setAttribute("newDataset", db);
		StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
		StudyDAO studydao = getStudyDAO();
		StudyBean theStudy = (StudyBean) studydao.findByPK(ub.getActiveStudyId());
		ArrayList<StudyGroupClassBean> allSelectedGroups = sgcdao.findAllActiveByStudy(theStudy);
		ArrayList<Integer> selectedSubjectGroupIds = db.getSubjectGroupIds();
		if (selectedSubjectGroupIds != null && allSelectedGroups != null) {
			for (Integer id : selectedSubjectGroupIds) {
				for (StudyGroupClassBean allSelectedGroup : allSelectedGroups) {
					if (allSelectedGroup.getId() == id) {
						allSelectedGroup.setSelected(true);
						break;
					}
				}
			}
		}
		db.setAllSelectedGroups(allSelectedGroups);

		return db;
	}

	@Override
	protected String getUrlKey(HttpServletRequest request) {
		return SAVED_VIEW_DATASETS_URL;
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		String action = fp.getString("action");
		String ownerId = fp.getString("ownerId");
		String eblFiltered = fp.getString("ebl_filtered");
		String eblFilterKeyword = fp.getString("ebl_filterKeyword");
		String eblSortColumnInd = fp.getString("ebl_sortColumnInd");
		String eblSortAscending = fp.getString("ebl_sortAscending");
		return "?submitted=1&module=" + fp.getString("module") + "&ebl_page=1&ebl_sortColumnInd="
				+ (!eblSortColumnInd.isEmpty() ? eblSortColumnInd : "0") + "&ebl_sortAscending="
				+ (!eblSortAscending.isEmpty() ? eblSortAscending : "1") + "&ebl_filtered="
				+ (!eblFiltered.isEmpty() ? eblFiltered : "0") + "&ebl_filterKeyword="
				+ (!eblFilterKeyword.isEmpty() ? eblFilterKeyword : "") + "&ebl_paginated=1" + "&action=" + action
				+ "&ownerId=" + ownerId;
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null || !request.getQueryString().contains("&ebl_page=");
	}
}
