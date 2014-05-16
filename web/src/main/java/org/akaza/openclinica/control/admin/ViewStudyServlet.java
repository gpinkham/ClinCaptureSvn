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

import com.clinovo.model.DiscrepancyDescription;
import com.clinovo.service.DiscrepancyDescriptionService;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Processes the reuqest of 'view study details'
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class ViewStudyServlet extends SecureController {
	/**
	 * Checks whether the user has the correct privilege
	 */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_admin"), "1");

	}

	@Override
	public void processRequest() throws Exception {

		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		FormProcessor fp = new FormProcessor(request);
		int studyId = fp.getInt("id");
		if (studyId == 0) {
			addPageMessage(respage.getString("please_choose_a_study_to_view"));
			forwardPage(Page.STUDY_LIST_SERVLET);
		} else {
			if (currentStudy.getId() != studyId && currentStudy.getParentStudyId() != studyId) {
				checkRoleByUserAndStudy(ub, studyId, 0);
			}

			String viewFullRecords = fp.getString("viewFull");
			StudyBean study = (StudyBean) sdao.findByPK(studyId);

			StudyConfigService scs = new StudyConfigService(sm.getDataSource());
			study = scs.setParametersForStudy(study);

			request.setAttribute("studyToView", study);
			if ("yes".equalsIgnoreCase(viewFullRecords)) {
				UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
				ArrayList sites = new ArrayList();
				ArrayList userRoles;
				if (this.currentStudy.getParentStudyId() > 0 && this.currentRole.getRole().getId() > 3) {
					sites.add(this.currentStudy);
					userRoles = udao.findAllUsersByStudy(currentStudy.getId());
				} else {
					sites = (ArrayList) sdao.findAllByParent(studyId);
					userRoles = udao.findAllUsersByStudy(studyId);
				}

				// find all subjects in the study, include ones in sites
				StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
				EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
				DiscrepancyDescriptionService dDescriptionService = (DiscrepancyDescriptionService) SpringServletAccess
						.getApplicationContext(context).getBean("discrepancyDescriptionService");

				// find all events in the study, include ones in sites
				List<StudyEventDefinitionBean> definitions = seddao.findAllAvailableByStudy(study);
				Map<String, List<DiscrepancyDescription>> dDescriptionsMap = dDescriptionService
						.findAllSortedDescriptionsFromStudy(studyId);

				for (StudyEventDefinitionBean def : definitions) {
					ArrayList crfs = (ArrayList) edcdao.findAllActiveParentsByEventDefinitionId(def.getId());
					def.setCrfNum(crfs.size());

				}

				request.setAttribute("sitesToView", sites);
				request.setAttribute("siteNum", sites.size() + "");
				request.setAttribute("dDescriptionsMap", dDescriptionsMap);

				request.setAttribute("userRolesToView", userRoles);
				request.setAttribute("userNum", userRoles.size() + "");

				request.setAttribute("definitionsToView", definitions);
				request.setAttribute("defNum", definitions.size() + "");
				forwardPage(Page.VIEW_FULL_STUDY);

			} else {
				forwardPage(Page.VIEW_STUDY);
			}
		}
	}

	@Override
	protected String getAdminServlet() {
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

}
