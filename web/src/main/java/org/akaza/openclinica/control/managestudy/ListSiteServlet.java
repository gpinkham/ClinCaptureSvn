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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.StudyRow;
import org.springframework.stereotype.Component;

/**
 * ListSiteServlet class.
 */
@Component
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class ListSiteServlet extends RememberLastPage {

	public static final String SAVED_LIST_SITES_URL = "savedListSitesUrl";

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws InsufficientPermissionException
	 *             the InsufficientPermissionException
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	/**
	 * Finds all the studies, processes the request.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws Exception
	 *             an Exception
	 */
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (shouldRedirect(request, response)) {
			return;
		}

		StudyDAO studyDao = getStudyDAO();
		StudyBean studyBean = getCurrentStudy(request);

		if (studyBean.getParentStudyId() > 0) {
			studyBean = (StudyBean) studyDao.findByPK(studyBean.getParentStudyId());
		}

		ArrayList studies = (ArrayList) studyDao.findAllByParent(studyBean.getId());

		Map<Integer, Map<String, Integer>> infoMap = studyDao.analyzeEvents(studies);
		for (Object studyObj : studies) {
			StudyBean sb = ((StudyBean) studyObj);
			Map<String, Integer> map = infoMap.get(sb.getId());
			int countEvents = map.get("countEvents");
			if (countEvents > 0) {
				int countLockedEvents = map.get("countLockedEvents");
				if (countEvents == countLockedEvents) {
					sb.setShowUnlockEventsButton(true);
				} else {
					sb.setShowLockEventsButton(true);
				}
			}
		}

		EntityBeanTable table = getEntityBeanTable();
		ArrayList allStudyRows = StudyRow.generateRowsFromBeans(studies);

		final int two = 2;
		final int seven = 7;
		String[] columns = {resword.getString("name"), resword.getString("unique_identifier"),
				resword.getString("OID"), resword.getString("principal_investigator"),
				resword.getString("facility_name"), resword.getString("date_created"), resword.getString("status"),
				resword.getString("actions")};
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(two);
		table.hideColumnLink(seven);
		table.setQuery("ListSite", new HashMap());
		table.setRows(allStudyRows);
		table.computeDisplay();

		request.setAttribute("table", table);
		if (request.getParameter("read") != null && request.getParameter("read").equals("true")) {
			request.setAttribute("readOnly", true);
		}
		addNewSiteNotificationMessage(request);
		request.getSession().setAttribute("fromListSite", "yes");
		forwardPage(Page.SITE_LIST, request, response);
	}

	@Override
	protected String getUrlKey(HttpServletRequest request) {
		return SAVED_LIST_SITES_URL;
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

	private void addNewSiteNotificationMessage(HttpServletRequest request) {
		if (request.getSession().getAttribute("new_site_created") != null) {
			request.getSession().removeAttribute("new_site_created");
			addPageMessage(respage.getString("the_new_site_created_succesfully_current"), request);
		}
	}
}
