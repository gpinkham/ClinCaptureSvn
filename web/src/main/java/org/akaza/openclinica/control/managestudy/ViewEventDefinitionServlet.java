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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * View the details of a study event definition
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class ViewEventDefinitionServlet extends SecureController {
	/**
	 * Checks whether the user has the correct privilege
	 */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET, resexception.getString("not_director"),
				"1");

	}

	@Override
	public void processRequest() throws Exception {

		StudyEventDefinitionDAO sdao = new StudyEventDefinitionDAO(sm.getDataSource());
		FormProcessor fp = new FormProcessor(request);
		int defId = fp.getInt("id", true);

		if (defId == 0) {
			addPageMessage(respage.getString("please_choose_a_definition_to_view"));
			forwardPage(Page.LIST_DEFINITION_SERVLET);
		} else {
			// definition id
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) sdao.findByPK(defId);

			if (currentStudy.getId() != sed.getStudyId()) {
				addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
						+ respage.getString("change_active_study_or_contact"));
				forwardPage(Page.MENU_SERVLET);
				return;
			}

			checkRoleByUserAndStudy(ub, sed.getStudyId(), 0);

			EventDefinitionCRFDAO edao = new EventDefinitionCRFDAO(sm.getDataSource());
			ArrayList eventDefinitionCRFs = (ArrayList) edao.findAllByDefinition(this.currentStudy, defId);

			CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
			CRFDAO cdao = new CRFDAO(sm.getDataSource());

			for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
				EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
				ArrayList versions = (ArrayList) cvdao.findAllByCRF(edc.getCrfId());
				edc.setVersions(versions);
				CRFBean crf = (CRFBean) cdao.findByPK(edc.getCrfId());
				// edc.setCrfLabel(crf.getLabel());
				edc.setCrfName(crf.getName());
				// to show/hide edit action on jsp page
				if (crf.getStatus().equals(Status.AVAILABLE)) {
					edc.setOwner(crf.getOwner());
				}

				CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edc.getDefaultVersionId());
				edc.setDefaultVersionName(defaultVersion.getName());
			}

			request.setAttribute("definition", sed);
			request.setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
			request.setAttribute("defSize", new Integer(eventDefinitionCRFs.size()));
			// request.setAttribute("eventDefinitionCRFs", new
			// ArrayList(tm.values()));
			forwardPage(Page.VIEW_EVENT_DEFINITION);
		}

	}

}
