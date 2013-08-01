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
package org.akaza.openclinica.control.extract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * Views selected items for creating dataset, aslo allow user to de-select or select all items in a study
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"rawtypes", "serial"})
public class ViewSelectedServlet extends SecureController {

	Locale locale;

	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR) || currentRole.getRole().equals(Role.STUDY_MONITOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");

	}

	/*
	 * setup study groups, tbh, added july 2007 FIXME in general a repeated set of code -- need to create a superclass
	 * which will contain this class, tbh
	 */
	public void setUpStudyGroups(DatasetBean db) {
		ArrayList sgclasses = db.getAllSelectedGroups();
		if (sgclasses == null || sgclasses.size() == 0) {
			StudyDAO studydao = new StudyDAO(sm.getDataSource());
			StudyGroupClassDAO sgclassdao = new StudyGroupClassDAO(sm.getDataSource());
			StudyBean theStudy = (StudyBean) studydao.findByPK(sm.getUserBean().getActiveStudyId());
			sgclasses = sgclassdao.findAllActiveByStudy(theStudy);
		}
        db.setAllSelectedGroups(sgclasses);
	}

	@Override
	public void processRequest() throws Exception {
		HashMap events = (HashMap) session.getAttribute(CreateDatasetServlet.EVENTS_FOR_CREATE_DATASET);
		if (events == null) {
			events = new HashMap();
		}
		request.setAttribute("eventlist", events);

        DatasetBean db = (DatasetBean) session.getAttribute("newDataset");

		request.setAttribute("numberOfStudyItems", new Integer(db.getItemIds().size()).toString());
		request.setAttribute("subjectAgeAtEvent",
				currentStudy.getStudyParameterConfig().getCollectDob().equals("3") ? "0" : "1");
        
		FormProcessor fp = new FormProcessor(request);
		String status = fp.getString("status");
		if (!StringUtil.isBlank(status) && "html".equalsIgnoreCase(status)) {
			forwardPage(Page.CREATE_DATASET_VIEW_SELECTED_HTML);
		} else {
			setUpStudyGroups(db);
			forwardPage(Page.CREATE_DATASET_VIEW_SELECTED);
		}

	}
}
