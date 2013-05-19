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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author jxu
 * 
 * 
 */
@SuppressWarnings({"rawtypes","unchecked", "serial"})
public class SelectItemsServlet extends SecureController {

	Locale locale;

	public static String CURRENT_DEF_ID = "currentDefId";

	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR) || currentRole.getRole().equals(Role.MONITOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");

	}

	public void setUpStudyGroupPage() {
		ArrayList sgclasses = (ArrayList) session.getAttribute("allSelectedGroups");
		if (sgclasses == null || sgclasses.size() == 0) {
			StudyDAO studydao = new StudyDAO(sm.getDataSource());
			StudyGroupClassDAO sgclassdao = new StudyGroupClassDAO(sm.getDataSource());
			StudyBean theStudy = (StudyBean) studydao.findByPK(sm.getUserBean().getActiveStudyId());
			sgclasses = sgclassdao.findAllActiveByStudy(theStudy);

			StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());

			for (int i = 0; i < sgclasses.size(); i++) {
				StudyGroupClassBean sgclass = (StudyGroupClassBean) sgclasses.get(i);
				ArrayList studyGroups = sgdao.findAllByGroupClass(sgclass);
				sgclass.setStudyGroups(studyGroups);
				// hmm, set it back into the array list? tbh
			}
		}
		session.setAttribute("allSelectedGroups", sgclasses);
		request.setAttribute("allSelectedGroups", sgclasses);
	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int crfId = fp.getInt("crfId");
		int defId = fp.getInt("defId");
		int eventAttr = fp.getInt("eventAttr");
		int subAttr = fp.getInt("subAttr");
		int CRFAttr = fp.getInt("CRFAttr");
		int groupAttr = fp.getInt("groupAttr");
		int discAttr = fp.getInt("discAttr");
		CRFDAO crfdao = new CRFDAO(sm.getDataSource());
		ItemDAO idao = new ItemDAO(sm.getDataSource());
		ItemFormMetadataDAO imfdao = new ItemFormMetadataDAO(sm.getDataSource());
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());

		HashMap events = (HashMap) session.getAttribute(CreateDatasetServlet.EVENTS_FOR_CREATE_DATASET);
		if (events == null) {
			events = new HashMap();
		}
		request.setAttribute("eventlist", events);
		logger.info("found dob setting: " + currentStudy.getStudyParameterConfig().getCollectDob());

		if (crfId == 0) {// no crf selected
			if (eventAttr == 0 && subAttr == 0 && CRFAttr == 0 && groupAttr == 0 && discAttr == 0) {

				forwardPage(Page.CREATE_DATASET_2);
			} else if (eventAttr > 0) {
				request.setAttribute("subjectAgeAtEvent", "1");
				if (currentStudy.getStudyParameterConfig().getCollectDob().equals("3")) {
					request.setAttribute("subjectAgeAtEvent", "0");
					logger.info("dob not collected, setting age at event to 0");
				}
				forwardPage(Page.CREATE_DATASET_EVENT_ATTR);
			} else if (subAttr > 0) {
				if (currentStudy.getStudyParameterConfig().getCollectDob().equals("3")) {
					logger.info("dob not collected, setting age at event to 0");
				}
				forwardPage(Page.CREATE_DATASET_SUB_ATTR);
			} else if (CRFAttr > 0) {
				forwardPage(Page.CREATE_DATASET_CRF_ATTR);
			} else if (groupAttr > 0) {
				setUpStudyGroupPage();
				forwardPage(Page.CREATE_DATASET_GROUP_ATTR);
			} 
			else {
				forwardPage(Page.CREATE_DATASET_2);
			}
			return;
		}

		CRFBean crf = (CRFBean) crfdao.findByPK(crfId);
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(defId);

		session.setAttribute("crf", crf);
		session.setAttribute("definition", sed);

		DatasetBean db = (DatasetBean) session.getAttribute("newDataset");
		if (db == null) {
			db = new DatasetBean();
		}

		session.setAttribute("newDataset", db);

		ArrayList items = idao.findAllActiveByCRF(crf);
		for (int i = 0; i < items.size(); i++) {
			ItemBean item = (ItemBean) items.get(i);
			
			ItemFormMetadataBean meta = imfdao.findByItemIdAndCRFVersionId(item.getId(), item.getItemMeta()
					.getCrfVersionId());
			meta.setCrfVersionName(item.getItemMeta().getCrfVersionName());
			item.getItemMetas().add(meta);
		}
		HashMap itemMap = new HashMap();
		for (int i = 0; i < items.size(); i++) {
			ItemBean item = (ItemBean) items.get(i);

			if (!itemMap.containsKey(defId + "_" + item.getId())) {
				if (db.getItemMap().containsKey(defId + "_" + item.getId())) {
					item.setSelected(true);
					item.setDatasetItemMapKey(defId + "_" + item.getId());
				}
				itemMap.put(defId + "_" + item.getId(), item);
			} else {
				// same item,combine the metadata
				ItemBean uniqueItem = (ItemBean) itemMap.get(defId + "_" + item.getId());
				uniqueItem.getItemMetas().add(item.getItemMetas().get(0));
				if (db.getItemMap().containsKey(defId + "_" + uniqueItem.getId())) {
					uniqueItem.setSelected(true);
				}
			}

		}
		ArrayList itemArray = new ArrayList(itemMap.values());
		// now sort them by ordinal/name
		Collections.sort(itemArray);
		session.setAttribute("allItems", itemArray);

		forwardPage(Page.CREATE_DATASET_2);
	}

}
