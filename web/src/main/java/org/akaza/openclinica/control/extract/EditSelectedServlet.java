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
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class EditSelectedServlet extends SecureController {

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

	/**
	 * This function exists in four different places... needs to be added to an additional superclass for Submit
	 * Data Control Servlets, tbh July 2007
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
        FormProcessor fp = new FormProcessor(request);
		boolean selectAll = fp.getBoolean("all");
		boolean selectAllItemsGroupsAttrs = fp.getBoolean("allAttrsAndItems");
		// Only show a "select all items" like on a side info panel if
		// it is not part of the EditSelected-related JSP>>
		request.setAttribute("EditSelectedSubmitted", true);

		ItemFormMetadataDAO imfdao = new ItemFormMetadataDAO(sm.getDataSource());
		DatasetBean db = (DatasetBean) session.getAttribute("newDataset");

		if (selectAll) {
			db = selectAll(db, null);

			MessageFormat msg = new MessageFormat("");
			msg.setLocale(locale);
			msg.applyPattern(respage.getString("choose_include_all_items_dataset"));
			Object[] arguments = { db.getItemMap().size() };
			addPageMessage(msg.format(arguments));
		}

		if (selectAllItemsGroupsAttrs) {
			logger.info("select everything....");
			db = selectAll(db, imfdao);
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
			StudyDAO studydao = new StudyDAO(sm.getDataSource());
			StudyGroupClassDAO sgclassdao = new StudyGroupClassDAO(sm.getDataSource());
			StudyBean theStudy = (StudyBean) studydao.findByPK(sm.getUserBean().getActiveStudyId());
            ArrayList sgclasses = sgclassdao.findAllActiveByStudy(theStudy);
			for (int i = 0; i < sgclasses.size(); i++) {
				StudyGroupClassBean sgclass = (StudyGroupClassBean) sgclasses.get(i);
				sgclass.setSelected(true);
				newsgclasses.add(sgclass);
			}
            db.setAllSelectedGroups(newsgclasses);
		}

		session.setAttribute("numberOfStudyItems", db.getItemMap().size());
		setUpStudyGroups(db);
		forwardPage(Page.CREATE_DATASET_VIEW_SELECTED);
	}

	public DatasetBean selectAll(DatasetBean db, ItemFormMetadataDAO imfdao) {
		HashMap events = (HashMap) session.getAttribute(CreateDatasetServlet.EVENTS_FOR_CREATE_DATASET);
		if (events == null) {
			events = new HashMap();
		}
		request.setAttribute("eventlist", events);

		db.getItemMap().clear();
		db.getItemIds().clear();
		db.getItemDefCrf().clear();

		Iterator it = events.keySet().iterator();
		while (it.hasNext()) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) it.next();
			if (!db.getEventIds().contains(new Integer(sed.getId()))) {
				db.getEventIds().add(new Integer(sed.getId()));
			}
		}

        ItemDAO idao = new ItemDAO(sm.getDataSource());
        CRFDAO crfdao = new CRFDAO(sm.getDataSource());
        db.setItemDefCrf(selectAll(events, crfdao, idao, imfdao));
		for (ItemBean item : (List<ItemBean>) db.getItemDefCrf()) {
			db.getItemIds().add(item.getId());
			db.getItemMap().put(item.getDefId() + "_" + item.getId(), item);
		}

		return db;
	}

	/**
	 * Finds all the items in a study giving all events in the study
	 * 
	 * @param events
	 * @return
	 */
	public static ArrayList selectAll(HashMap events, CRFDAO crfdao, ItemDAO idao, ItemFormMetadataDAO imfdao) {
		ArrayList allItems = new ArrayList();
		Iterator it = events.keySet().iterator();
		while (it.hasNext()) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) it.next();
			ArrayList crfs = (ArrayList) crfdao.findAllActiveByDefinition(sed);
			for (int i = 0; i < crfs.size(); i++) {
				CRFBean crf = (CRFBean) crfs.get(i);
				ArrayList items = idao.findAllActiveByCRF(crf);
				for (int j = 0; j < items.size(); j++) {
					ItemBean item = (ItemBean) items.get(j);
					item.setCrfName(crf.getName());
					item.setDefName(sed.getName());
					item.setDefId(sed.getId());
					item.setSelected(true);
                    if (imfdao != null) {
                        item.setItemMetas(imfdao.findAllByItemId(item.getId()));
                    }
				}
				allItems.addAll(items);
			}
		}
		return allItems;
	}
}
