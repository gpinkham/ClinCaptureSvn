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
 * If not, see <http://www.gnu.org/licenses/>.
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

import org.akaza.openclinica.bean.admin.DisplayStudyBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.DisplayStudyRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class ListStudyServlet extends SecureController {

	Locale locale;

	/**
     *
     */
	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin() || ub.isTechAdmin()) {
			return;
		}
		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	/**
	 * Finds all the studies
	 * 
	 */
	@Override
	public void processRequest() throws Exception {

		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		ArrayList studies = (ArrayList) sdao.findAll();
		// find all parent studies
		ArrayList parents = (ArrayList) sdao.findAllParents();
		ArrayList displayStudies = new ArrayList();

		for (int i = 0; i < parents.size(); i++) {
			StudyBean parent = (StudyBean) parents.get(i);
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
		session.setAttribute("fromListSite", "no");

		resetPanel();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		setToPanel(resword.getString("in_the_application"), "");
		if (parents.size() > 0) {
			setToPanel(resword.getString("studies"), new Integer(parents.size()).toString());
		}
		if (studies.size() > 0) {
			setToPanel(resword.getString("sites"), new Integer(studies.size() - parents.size()).toString());
		}
		forwardPage(Page.STUDY_LIST);

	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}

}
