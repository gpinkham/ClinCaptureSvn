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
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
@SuppressWarnings({ "rawtypes", "serial" })
public class UnlockCRFVersionServlet extends SecureController {
	/**
    *
    */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);

		int crfVersionId = fp.getInt("id");
		String action = fp.getString("action");

		if (crfVersionId == 0) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study"));
			forwardPage(Page.CRF_LIST_SERVLET);
			return;
		}

		CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
		CRFDAO cdao = new CRFDAO(sm.getDataSource());

		CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(crfVersionId);
		CRFBean crf = (CRFBean) cdao.findByPK(version.getCrfId());

		EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
		ArrayList eventCRFs = ecdao.findAllStudySubjectByCRFVersion(crfVersionId);

		if (StringUtil.isBlank(action)) {
			request.setAttribute("crfVersionToUnlock", version);
			request.setAttribute("crf", crf);
			request.setAttribute("eventSubjectsUsingVersion", eventCRFs);
			forwardPage(Page.CONFIRM_UNLOCKING_CRF_VERSION);

		} else if ("confirm".equalsIgnoreCase(action)) {
			version.setStatus(Status.AVAILABLE);
			version.setUpdater(ub);
			cvdao.update(version);
			addPageMessage(respage.getString("crf_version_unarchived_successfully"));
			forwardPage(Page.CRF_LIST_SERVLET);
		}
	}

}
