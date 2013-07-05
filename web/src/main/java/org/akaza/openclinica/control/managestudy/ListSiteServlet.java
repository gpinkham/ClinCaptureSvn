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
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.StudyRow;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class ListSiteServlet extends RememberLastPage {

	public static final String SAVED_LIST_SITES_URL = "savedListSitesUrl";
	Locale locale;

	/**
     *
     */
	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET,
				resexception.getString("not_study_director"), "1");

	}

	/**
	 * Finds all the studies, processes the request
	 */
	@Override
	public void processRequest() throws Exception {
		analyzeUrl();
		FormProcessor fp = new FormProcessor(request);
		if (currentStudy.getParentStudyId() > 0) {
			addPageMessage(respage.getString("no_sites_available_study_is_a_site"));
			forwardPage(Page.MENU_SERVLET);
		} else {

			StudyDAO sdao = new StudyDAO(sm.getDataSource());
			ArrayList studies = (ArrayList) sdao.findAllByParent(currentStudy.getId());

			Map<Integer, Map<String, Integer>> infoMap = sdao.analyzeEvents(studies);
			for (Object studyObj : studies) {
				StudyBean sb = ((StudyBean) studyObj);
				Map<String, Integer> map = infoMap.get(sb.getId());
				int countEvents = map.get("countEvents");
				if (countEvents > 0) {
					boolean hasLockedBy = map.get("countLockedByEvents") > 0;
					int countLockedEvents = map.get("countLockedEvents");
					boolean allLocked = countEvents == countLockedEvents;
					if (allLocked && hasLockedBy) {
						sb.setShowUnlockEventsButton(true);
					} else if (!allLocked) {
						sb.setShowLockEventsButton(true);
					}
				}
			}

			EntityBeanTable table = fp.getEntityBeanTable();
			ArrayList allStudyRows = StudyRow.generateRowsFromBeans(studies);

			String[] columns = { resword.getString("name"), resword.getString("unique_identifier"),
					resword.getString("OID"), resword.getString("principal_investigator"),
					resword.getString("facility_name"), resword.getString("date_created"), resword.getString("status"),
					resword.getString("actions") };
			table.setColumns(new ArrayList(Arrays.asList(columns)));
			table.hideColumnLink(2);
			table.hideColumnLink(6);
			table.setQuery("ListSite", new HashMap());
			// if (!currentStudy.getStatus().isLocked()) {
			// table.addLink(resword.getString("create_a_new_site"),
			// "CreateSubStudy");
			// }

			table.setRows(allStudyRows);
			table.computeDisplay();

			request.setAttribute("table", table);
			if (request.getParameter("read") != null && request.getParameter("read").equals("true")) {
				request.setAttribute("readOnly", true);
			}
			session.setAttribute("fromListSite", "yes");
			analyzeForward(Page.SITE_LIST);
		}

	}

	@Override
	protected String getUrlKey() {
		return SAVED_LIST_SITES_URL;
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
