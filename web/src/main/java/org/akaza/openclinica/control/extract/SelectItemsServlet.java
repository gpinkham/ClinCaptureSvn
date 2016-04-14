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
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.control.core.SpringServlet;
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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jxu
 * 
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class SelectItemsServlet extends SpringServlet {

	public static String CURRENT_DEF_ID = "currentDefId";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		if (CreateDatasetServlet.haveAccess(getUserAccountBean(request), getCurrentRole(request))){
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU,
				getResException().getString("not_allowed_access_extract_data_servlet"), "1");

	}

	public void setUpStudyGroupPage(UserAccountBean ub, DatasetBean db) {
		ArrayList sgclasses = db.getAllSelectedGroups();
		if (sgclasses == null || sgclasses.size() == 0) {
			StudyDAO studydao = getStudyDAO();
			StudyGroupClassDAO sgclassdao = getStudyGroupClassDAO();
			StudyBean theStudy = (StudyBean) studydao.findByPK(ub.getActiveStudyId());
			sgclasses = sgclassdao.findAllActiveByStudy(theStudy);

			StudyGroupDAO sgdao = getStudyGroupDAO();

			for (int i = 0; i < sgclasses.size(); i++) {
				StudyGroupClassBean sgclass = (StudyGroupClassBean) sgclasses.get(i);
				sgclass.setStudyGroups(sgdao.findAllByGroupClass(sgclass));
				// hmm, set it back into the array list? tbh
			}
		}
		db.setAllSelectedGroups(sgclasses);
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		FormProcessor fp = new FormProcessor(request);
		int crfId = fp.getInt("crfId");
		int defId = fp.getInt("defId");
		int eventAttr = fp.getInt("eventAttr");
		int subAttr = fp.getInt("subAttr");
		int CRFAttr = fp.getInt("CRFAttr");
		int groupAttr = fp.getInt("groupAttr");
		int discAttr = fp.getInt("discAttr");
		CRFDAO crfdao = getCRFDAO();
		ItemDAO idao = getItemDAO();
		ItemFormMetadataDAO imfdao = getItemFormMetadataDAO();
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		DatasetBean dsb = (DatasetBean) request.getSession().getAttribute("newDataset");
		HashMap events = (HashMap) request.getSession().getAttribute(CreateDatasetServlet.EVENTS_FOR_CREATE_DATASET);
		if (events == null) {
			events = new HashMap();
		}
		request.setAttribute("eventlist", events);
		logger.info("found dob setting: " + currentStudy.getStudyParameterConfig().getCollectDob());

		if (crfId == 0) {// no crf selected
			if (eventAttr == 0 && subAttr == 0 && CRFAttr == 0 && groupAttr == 0 && discAttr == 0) {

				forwardPage(Page.CREATE_DATASET_2, request, response);
			} else if (eventAttr > 0) {
				request.setAttribute("subjectAgeAtEvent", "1");
				if (currentStudy.getStudyParameterConfig().getCollectDob().equals("3")) {
					request.setAttribute("subjectAgeAtEvent", "0");
					logger.info("dob not collected, setting age at event to 0");
				}
				forwardPage(Page.CREATE_DATASET_EVENT_ATTR, request, response);
			} else if (subAttr > 0) {
				if (currentStudy.getStudyParameterConfig().getCollectDob().equals("3")) {
					logger.info("dob not collected, setting age at event to 0");
				}
				forwardPage(Page.CREATE_DATASET_SUB_ATTR, request, response);
			} else if (CRFAttr > 0) {
				forwardPage(Page.CREATE_DATASET_CRF_ATTR, request, response);
			} else if (groupAttr > 0) {
				setUpStudyGroupPage(ub, dsb);
				forwardPage(Page.CREATE_DATASET_GROUP_ATTR, request, response);
			} else {
				forwardPage(Page.CREATE_DATASET_2, request, response);
			}
			return;
		}

		CRFBean crf = (CRFBean) crfdao.findByPK(crfId);
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(defId);

		request.getSession().setAttribute("crf", crf);
		request.getSession().setAttribute("definition", sed);

		DatasetBean db = (DatasetBean) request.getSession().getAttribute("newDataset");

		ArrayList items = idao.findAllActiveByCRF(crf);
		for (int i = 0; i < items.size(); i++) {
			ItemBean item = (ItemBean) items.get(i);
			item.setDefId(sed.getId());
			ItemFormMetadataBean meta = imfdao.findByItemIdAndCRFVersionId(item.getId(), item.getItemMeta()
					.getCrfVersionId());
			item.setItemMeta(meta);
		}
		HashMap itemMap = new HashMap();
		for (int i = 0; i < items.size(); i++) {
			ItemBean item = (ItemBean) items.get(i);
			ItemBean selectedItem = (ItemBean) db.getItemMap().get(
					defId + "_" + item.getItemMeta().getCrfVersionId() + "_" + item.getId());
			if (selectedItem != null && selectedItem.isSelected()) {
				item.setSelected(true);
				item.setDatasetItemMapKey(defId + "_" + item.getItemMeta().getCrfVersionId() + "_" + item.getId());
			}
			itemMap.put(defId + "_" + item.getItemMeta().getCrfVersionId() + "_" + item.getId(), item);
		}

		ArrayList allCrfItems = new ArrayList(itemMap.values());
		// now sort them by ordinal/crf version

		Collections.sort(allCrfItems, new ItemBean.ItemBeanComparator());
		request.getSession().setAttribute("allCrfItems", allCrfItems);

		forwardPage(Page.CREATE_DATASET_2, request, response);
	}

}
