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
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class ViewCRFVersionPreview extends SecureController {
	/**
	 * Checks whether the user has the right permission to proceed function
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
				+ respage.getString("change_active_study_or_contact"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_director"), "1");

	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int crfId = fp.getInt("crfId");
		// CRFVersionBean
		// SectionBean
		CRFVersionBean version = new CRFVersionBean();
		List sections;
		Map<String, Map> crfMap = (Map) session.getAttribute("preview_crf");
		Map<String, String> crfIdnameInfo = null;
		if (crfMap != null) {
			crfIdnameInfo = crfMap.get("crf_info");
		} else {
			logger.info("The crfMap session attribute has expired or gone out of scope in: "
					+ this.getClass().getName());
		}
		String crfName = "";
		String verNumber = "";
		if (crfIdnameInfo != null) {
			Map.Entry mapEnt = null;
			for (Iterator iter = crfIdnameInfo.entrySet().iterator(); iter.hasNext();) {
				mapEnt = (Map.Entry) iter.next();
				if (((String) mapEnt.getKey()).equalsIgnoreCase("crf_name")) {
					crfName = (String) mapEnt.getValue();
				}
				if (((String) mapEnt.getKey()).equalsIgnoreCase("version")) {
					verNumber = (String) mapEnt.getValue();
				}
			}
		}
		version.setName(verNumber);
		version.setCrfId(crfId);
		// A Map containing the section names as the index
		Map<Integer, Map<String, String>> sectionsMap = null;
		if (crfMap != null)
			sectionsMap = crfMap.get("sections");
		// The itemsMap contains the index of the spreadsheet table items row,
		// followed by a map of the column names/values; it contains values for
		// display
		// such as 'left item text'
		Map<Integer, Map<String, String>> itemsMap = null;
		if (crfMap != null)
			itemsMap = crfMap.get("items");
		// Get groups meta info
		Map<Integer, Map<String, String>> groupsMap = null;
		if (crfMap != null)
			groupsMap = crfMap.get("groups");
		// Set up sections for the preview
		BeanFactory beanFactory = new BeanFactory();
		sections = beanFactory.createSectionBeanList(sectionsMap, itemsMap, crfName, groupsMap);
		request.setAttribute("sections", sections);
		request.setAttribute("version", version);
		forwardPage(Page.VIEW_CRF_VERSION);

	}
}
