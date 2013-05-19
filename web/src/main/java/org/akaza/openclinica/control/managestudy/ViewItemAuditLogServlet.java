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

package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.dao.admin.AuditDAO;
import java.util.ArrayList;

@SuppressWarnings({ "rawtypes", "serial" })
public class ViewItemAuditLogServlet extends SecureController {

	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
				+ respage.getString("change_active_study_or_contact"));
		throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS,
				resexception.getString("not_study_director"), "1");
	}

	public void processRequest() throws Exception {
		AuditDAO adao = new AuditDAO(sm.getDataSource());
		FormProcessor fp = new FormProcessor(request);
		String auditTable = fp.getString("auditTable");
		if (auditTable.equalsIgnoreCase("studysub")) {
			auditTable = "study_subject";
		} else if (auditTable.equalsIgnoreCase("eventcrf")) {
			auditTable = "event_crf";
		} else if (auditTable.equalsIgnoreCase("studyevent")) {
			auditTable = "study_event";
		} else if (auditTable.equalsIgnoreCase("itemdata")) {
			auditTable = "item_data";
		}
		int entityId = fp.getInt("entityId");
		ArrayList itemAuditEvents = adao.findItemAuditEvents(entityId, auditTable);
		request.setAttribute("itemAudits", itemAuditEvents);
		forwardPage(Page.AUDIT_LOGS_ITEMS);
	}
}
