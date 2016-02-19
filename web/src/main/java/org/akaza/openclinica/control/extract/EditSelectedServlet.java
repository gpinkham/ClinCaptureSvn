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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.i18n.LocaleResolver;

/**
 * Edit selected into dataset items servlet.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class EditSelectedServlet extends SpringServlet {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR) || Role.isMonitor(currentRole.getRole())) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU,
				getResException().getString("not_allowed_access_extract_data_servlet"), "1");

	}

	/**
	 * This function exists in four different places. needs to be added to an additional superclass for Submit Data.
	 *
	 * @param ub UserAccountBean
	 * @param db DatasetBean
	 */
	public void setUpStudyGroups(UserAccountBean ub, DatasetBean db) {
		ArrayList sgclasses = db.getAllSelectedGroups();
		if (sgclasses == null || sgclasses.size() == 0) {
			StudyDAO studydao = getStudyDAO();
			StudyGroupClassDAO sgclassdao = getStudyGroupClassDAO();
			StudyBean theStudy = (StudyBean) studydao.findByPK(ub.getActiveStudyId());
			sgclasses = sgclassdao.findAllActiveByStudy(theStudy);
		}
		db.setAllSelectedGroups(sgclasses);
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		request.setAttribute("subjectAgeAtEvent", currentStudy.getStudyParameterConfig().getCollectDob().equals("3")
				? "0"
				: "1");

		FormProcessor fp = new FormProcessor(request);
		boolean selectAll = fp.getBoolean("all");
		boolean selectAllItemsGroupsAttrs = fp.getBoolean("allAttrsAndItems");
		// Only show a "select all items" like on a side info panel if
		// it is not part of the EditSelected-related JSP>>
		request.setAttribute("EditSelectedSubmitted", true);

		ItemFormMetadataDAO imfdao = getItemFormMetadataDAO();
		DatasetBean db = (DatasetBean) request.getSession().getAttribute("newDataset");

		if (selectAll) {
			db = selectAll(db, null, request);

			MessageFormat msg = new MessageFormat("");
			msg.setLocale(LocaleResolver.getLocale(request));
			msg.applyPattern(getResPage().getString("choose_include_all_items_dataset"));
			Object[] arguments = {db.getItemIds().size()};
			addPageMessage(msg.format(arguments), request);
		}

		if (selectAllItemsGroupsAttrs) {
			logger.info("select everything....");
			db = selectAll(db, imfdao, request);
			db.setShowCRFcompletionDate(true);
			db.setShowCRFinterviewerDate(true);
			db.setShowCRFinterviewerName(true);
			db.setShowCRFstatus(true);
			db.setShowCRFversion(true);

			db.setShowEventEnd(true);
			db.setShowEventEndTime(true);
			db.setShowEventLocation(true);
			db.setShowEventStart(true);
			db.setShowEventStartTime(true);
			db.setShowEventStatus(true);

			db.setShowSubjectAgeAtEvent(true);
			db.setShowSubjectDob(true);
			db.setShowSubjectGender(true);
			db.setShowSubjectGroupInformation(true);
			db.setShowSubjectStatus(true);
			db.setShowSubjectUniqueIdentifier(true);

			ArrayList newsgclasses = new ArrayList();
			StudyDAO studydao = getStudyDAO();
			StudyGroupClassDAO sgclassdao = getStudyGroupClassDAO();
			StudyBean theStudy = (StudyBean) studydao.findByPK(ub.getActiveStudyId());
			ArrayList sgclasses = sgclassdao.findAllActiveByStudy(theStudy);
			for (Object sgclass1 : sgclasses) {
				StudyGroupClassBean sgclass = (StudyGroupClassBean) sgclass1;
				sgclass.setSelected(true);
				newsgclasses.add(sgclass);
			}
			db.setAllSelectedGroups(newsgclasses);
		}

		request.setAttribute("numberOfStudyItems", db.getItemIds().size());
		setUpStudyGroups(ub, db);
		forwardPage(Page.CREATE_DATASET_VIEW_SELECTED, request, response);
	}

	/**
	 * Parent method to select all items into Data set.
	 *
	 * @param db      DatasetBean
	 * @param imfdao  ItemFormMetadataDAO
	 * @param request HttpServletRequest
	 * @return DatasetBean
	 */
	public DatasetBean selectAll(DatasetBean db, ItemFormMetadataDAO imfdao, HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		HashMap events = (HashMap) request.getSession().getAttribute(CreateDatasetServlet.EVENTS_FOR_CREATE_DATASET);
		if (events == null) {
			events = new HashMap();
		}
		request.setAttribute("eventlist", events);

		db.getItemMap().clear();
		db.getItemIds().clear();
		db.getItemDefCrf().clear();

		for (Object o : events.keySet()) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) o;
			if (!db.getEventIds().contains(new Integer(sed.getId()))) {
				db.getEventIds().add(sed.getId());
			}
		}

		ItemDAO idao = getItemDAO();
		CRFDAO crfdao = getCRFDAO();
		db.setItemDefCrf(selectAll(events, crfdao, idao, imfdao, ub));
		for (ItemBean item : (List<ItemBean>) db.getItemDefCrf()) {
			db.getItemIds().add(item.getId());
			db.getItemMap()
					.put(item.getDefId() + "_" + item.getItemMeta().getCrfVersionId() + "_" + item.getId(), item);
		}

		return db;
	}

	@SuppressWarnings("unused")
	private static boolean containsItem(ArrayList allItems, ItemBean item) {
		boolean result = false;
		for (ItemBean itemBean : (List<ItemBean>) allItems) {
			if (itemBean.getId() == item.getId()) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * Finds all the items in a study giving all events in the study.
	 *
	 * @param events HashMap
	 * @param crfdao CRFDAO
	 * @param idao   ItemDAO
	 * @param imfdao ItemFormMetadataDAO
	 * @param ub     UserAccountBean
	 * @return ArrayList
	 */
	public static ArrayList selectAll(HashMap events, CRFDAO crfdao, ItemDAO idao, ItemFormMetadataDAO imfdao, UserAccountBean ub) {

		ArrayList allItems = new ArrayList();
		for (Object o : events.keySet()) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) o;
			ArrayList crfs = (ArrayList) crfdao.findAllActiveUnmaskedByDefinition(sed, ub);
			for (Object crf1 : crfs) {
				CRFBean crf = (CRFBean) crf1;
				ArrayList items = idao.findAllActiveByCRF(crf);
				for (Object item1 : items) {
					ItemBean item = (ItemBean) item1;
					item.setCrfName(crf.getName());
					item.setDefName(sed.getName());
					item.setDefId(sed.getId());
					item.setSelected(true);
					item.setCrfVersion("" + crf.getId());
					if (imfdao != null) {
						item.setItemMetas(imfdao.findAllByItemId(item.getId()));
					}
				}
				allItems.addAll(items);
			}
		}
		Collections.sort(allItems, new ItemBean.ItemBeanComparator());
		return allItems;
	}
}
