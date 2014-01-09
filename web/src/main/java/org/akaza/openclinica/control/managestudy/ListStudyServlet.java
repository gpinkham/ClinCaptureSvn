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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.admin.DisplayStudyBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.DisplayStudyRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class ListStudyServlet extends Controller {
	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (ub.isSysAdmin() || ub.isTechAdmin()) {
			return;
		}
		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	/**
	 * Finds all the studies
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyDAO sdao = getStudyDAO();
		ArrayList studies = (ArrayList) sdao.findAll();
		// find all parent studies
		ArrayList parents = (ArrayList) sdao.findAllParents();
		ArrayList displayStudies = new ArrayList();

		for (Object parent1 : parents) {
			StudyBean parent = (StudyBean) parent1;
			ArrayList children = (ArrayList) sdao.findAllByParent(parent.getId());
			DisplayStudyBean displayStudy = new DisplayStudyBean();
			displayStudy.setParent(parent);
			displayStudy.setChildren(children);
			displayStudies.add(displayStudy);

		}

		FormProcessor fp = new FormProcessor(request);
		EntityBeanTable table = fp.getEntityBeanTable();
		ArrayList allStudyRows = DisplayStudyRow.generateRowsFromBeans(displayStudies);

		String[] columns = { resword.getString("name"), resword.getString("unique_identifier"),
				resword.getString("OID"), resword.getString("principal_investigator"),
				resword.getString("facility_name"), resword.getString("date_created"), resword.getString("status"),
				resword.getString("actions") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(2);
		table.hideColumnLink(6);
		table.setQuery("ListStudy", new HashMap());
		table.setRows(allStudyRows);
		table.computeDisplay();

		request.setAttribute("table", table);
		request.getSession().setAttribute("fromListSite", "no");

		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		setToPanel(resword.getString("in_the_application"), "", request);
		if (parents.size() > 0) {
			setToPanel(resword.getString("studies"), Integer.toString(parents.size()), request);
		}
		if (studies.size() > 0) {
			setToPanel(resword.getString("sites"), Integer.toString(studies.size() - parents.size()), request);
		}
		forwardPage(Page.STUDY_LIST, request, response);

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}

}
