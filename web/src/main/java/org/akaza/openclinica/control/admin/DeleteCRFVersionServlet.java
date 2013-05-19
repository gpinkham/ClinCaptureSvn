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

import java.util.ArrayList;

import org.akaza.openclinica.bean.admin.NewCRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

@SuppressWarnings({ "rawtypes", "serial" })
public class DeleteCRFVersionServlet extends SecureController {
	public static final String VERSION_ID = "verId";

	public static final String VERSION_TO_DELETE = "version";

	/**
     *
     */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, "not admin", "1");
	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int versionId = fp.getInt(VERSION_ID, true);
		String action = request.getParameter("action");
		if (versionId == 0) {
			addPageMessage(respage.getString("please_choose_a_CRF_version_to_delete"));
			forwardPage(Page.CRF_LIST_SERVLET);
		} else {
			CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
			CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(versionId);
			EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
			StudyEventDefinitionDAO sedDao = new StudyEventDefinitionDAO(sm.getDataSource());

			// find definitions using this version
			ArrayList definitions = edcdao.findByDefaultVersion(version.getId());
			for (Object edcBean : definitions) {
				StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) sedDao
						.findByPK(((EventDefinitionCRFBean) edcBean).getStudyEventDefinitionId());
				((EventDefinitionCRFBean) edcBean).setEventName(sedBean.getName());
			}

			// find event crfs using this version
			EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
			ArrayList eventCRFs = ecdao.findAllByCRFVersion(versionId);
			boolean canDelete = true;
			if (!definitions.isEmpty()) {// used in definition
				canDelete = false;
				request.setAttribute("definitions", definitions);
				addPageMessage(respage.getString("this_CRF_version") + version.getName()
						+ respage.getString("has_associated_study_events_definitions_cannot_delete"));

			} else if (!eventCRFs.isEmpty()) {
				canDelete = false;
				request.setAttribute("eventsForVersion", eventCRFs);
				addPageMessage(respage.getString("this_CRF_version") + version.getName()
						+ respage.getString("has_associated_study_events_cannot_delete"));
			}
			if ("confirm".equalsIgnoreCase(action)) {
				request.setAttribute(VERSION_TO_DELETE, version);
				forwardPage(Page.DELETE_CRF_VERSION);
			} else {
				// submit
				if (canDelete) {
					ArrayList items = cvdao.findNotSharedItemsByVersion(versionId);
					NewCRFBean nib = new NewCRFBean(sm.getDataSource(), version.getCrfId());
					nib.setDeleteQueries(cvdao.generateDeleteQueries(versionId, items));
					nib.deleteFromDB();
					addPageMessage(respage.getString("the_CRF_version_has_been_deleted_succesfully"));
				} else {
					addPageMessage(respage.getString("the_CRF_version_cannot_be_deleted"));
				}
				forwardPage(Page.CRF_LIST_SERVLET);
			}

		}

	}

}
