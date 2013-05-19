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

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;

@SuppressWarnings({"serial", "rawtypes"})
public class RestoreCRFFromDefinitionServlet extends SecureController {
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

		addPageMessage(respage.getString("no_have_permission_to_update_study_event_definition")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.STUDY_EVENT_DEFINITION_LIST,
				resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest() throws Exception {
		ArrayList edcs = (ArrayList) session.getAttribute("eventDefinitionCRFs");
		String crfName = "";

		String idString = request.getParameter("id");
		logger.info("crf id:" + idString);
		if (StringUtil.isBlank(idString)) {
			addPageMessage(respage.getString("please_choose_a_CRF_to_restore"));
			forwardPage(Page.UPDATE_EVENT_DEFINITION1);
		} else {
			// event crf definition id
			int id = Integer.valueOf(idString.trim()).intValue();
			for (int i = 0; i < edcs.size(); i++) {
				EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcs.get(i);
				if (edc.getCrfId() == id) {
					edc.setStatus(Status.AVAILABLE);
					edc.setOldStatus(Status.DELETED);
					crfName = edc.getCrfName();
				}

			}
			session.setAttribute("eventDefinitionCRFs", edcs);
			addPageMessage(crfName + " " + respage.getString("has_been_restored"));
			forwardPage(Page.UPDATE_EVENT_DEFINITION1);
		}

	}
}
