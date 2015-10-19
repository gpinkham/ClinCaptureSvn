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
package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.logic.rulerunner.ImportDataRuleRunnerContainer;
import org.akaza.openclinica.util.ImportSummaryInfo;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.crfdata.ImportCRFDataService;

/**
 * View the uploaded data and verify what is going to be saved into the system and what is not.
 * 
 * @author Krikor Krumlian
 */
@SuppressWarnings("unchecked")
@Component
public class VerifyImportedCRFDataServlet extends Controller {

	private static final long serialVersionUID = 1L;
	public static final int INT_3600 = 3600;
	public static final int INT_10800 = 10800;
	public static final int INT_52 = 52;

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR) || r.equals(Role.INVESTIGATOR)
				|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"),
				"1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		ImportSummaryInfo summary = new ImportSummaryInfo();
		Set<Integer> skippedItemIds = new HashSet<Integer>();
		List<Map<String, Object>> auditItemList = new ArrayList<Map<String, Object>>();

		FormProcessor fp = new FormProcessor(request);
		String action = request.getParameter("action");

		request.getSession().setMaxInactiveInterval(INT_10800);

		// checks which module the requests are from
		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);

		request.getSession().removeAttribute(STUDY_INFO_PANEL);
		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);

		setToPanel(resword.getString("create_CRF"), respage.getString("br_create_new_CRF_entering"), request);

		setToPanel(resword.getString("create_CRF_version"), respage.getString("br_create_new_CRF_uploading"), request);
		setToPanel(resword.getString("revise_CRF_version"), respage.getString("br_if_you_owner_CRF_version"), request);
		setToPanel(resword.getString("CRF_spreadsheet_template"),
				respage.getString("br_download_blank_CRF_spreadsheet_from"), request);
		setToPanel(resword.getString("example_CRF_br_spreadsheets"),
				respage.getString("br_download_example_CRF_instructions_from"), request);

		if ("confirm".equalsIgnoreCase(action)) {
			List<DisplayItemBeanWrapper> displayItemBeanWrappers = (List<DisplayItemBeanWrapper>) request.getSession()
					.getAttribute("importedData");
			logger.debug("Size of displayItemBeanWrappers : " + displayItemBeanWrappers.size());
			forwardPage(Page.VERIFY_IMPORT_CRF_DATA, request, response);
		}

		if ("save".equalsIgnoreCase(action)) {
			ODMContainer odmContainer = (ODMContainer) request.getSession().getAttribute("odmContainer");
			List<DisplayItemBeanWrapper> displayItemBeanWrappers = (List<DisplayItemBeanWrapper>) request.getSession()
					.getAttribute("importedData");

			// actually the crf data import is here
			List<ImportDataRuleRunnerContainer> containers;
			ImportCRFDataService importCrfDataService = getImportCRFDataService();
			try {
				containers = importCrfDataService.importCrfData(currentStudy, ub, skippedItemIds, auditItemList,
						displayItemBeanWrappers, summary, odmContainer);
			} catch (Exception ex) {
				addPageMessage(resexception.getString("dataImport.failed"), request);
				forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
				return;
			}

			addPageMessage(respage.getString("data_has_been_successfully_import"), request);
			System.out.println("Data is committed");

			addPageMessage(summary.prepareSummaryMessage(currentStudy, resword), request);

			importCrfDataService.saveAuditItems(auditItemList);

			addPageMessage(
					importCrfDataService.runRulesAndGenerateMessage(true, skippedItemIds, currentStudy, ub, containers),
					request);

			request.getSession().setMaxInactiveInterval(INT_3600);
			forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
		}
	}

}
