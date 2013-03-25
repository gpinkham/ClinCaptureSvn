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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.extract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * @author thickerson
 * 
 * 
 */
@SuppressWarnings({"rawtypes","unchecked", "serial"})
public class EditDatasetServlet extends SecureController {

	public static String getLink(int dsId) {
		return "EditDataset?dsId=" + dsId;
	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);

		int dsId = fp.getInt("dsId");
		DatasetBean dataset = initializeAttributes(dsId);

		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		StudyBean study = (StudyBean) sdao.findByPK(dataset.getStudyId());
		// Checking if user has permission to access the current study/site
		checkRoleByUserAndStudy(ub, study.getParentStudyId(), study.getId());

		// Checking the dataset belongs to current study or a site of current study
		if (study.getId() != currentStudy.getId() && study.getParentStudyId() != currentStudy.getId()) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
					+ respage.getString("change_active_study_or_contact"));
			forwardPage(Page.MENU_SERVLET);
			return;
		}

		if ((currentRole.isMonitor() || currentRole.isInvestigator()) && (dataset.getOwnerId() != ub.getId())) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
					+ respage.getString("change_active_study_or_contact"));
			forwardPage(Page.MENU_SERVLET);
			return;
		}

		HashMap events = (LinkedHashMap) session.getAttribute("eventsForCreateDataset");
		CRFDAO crfdao = new CRFDAO(sm.getDataSource());

		if (events == null || events.isEmpty()) {
			events = new LinkedHashMap();
			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());

			StudyBean studyWithEventDefinitions = currentStudy;
			if (currentStudy.getParentStudyId() > 0) {
				studyWithEventDefinitions = new StudyBean();
				studyWithEventDefinitions.setId(currentStudy.getParentStudyId());

			}
			ArrayList seds = seddao.findAllActiveByStudy(studyWithEventDefinitions);
			for (int i = 0; i < seds.size(); i++) {
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seds.get(i);
				ArrayList crfs = (ArrayList) crfdao.findAllActiveByDefinition(sed);
				if (!crfs.isEmpty()) {
					events.put(sed, crfs);
				}
			}
			if (events.isEmpty()) {
				addPageMessage(respage.getString("not_have_study_definitions_assigned"));
				forwardPage(Page.VIEW_DATASETS);
			} else {
				request.setAttribute("eventlist", events);
				session.setAttribute("eventsForCreateDataset", events);
			}
		}
		request.setAttribute("dataset", dataset);
		request.setAttribute("statuses", getStatuses());
		forwardPage(Page.EDIT_DATASET);
		// }
	}

	@Override
	public void mayProceed() throws InsufficientPermissionException {

		if (ub.isSysAdmin()) {
			return;
		}
		// TODO add a limit so that the owner can edit, no one else?
		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR) || currentRole.getRole().equals(Role.MONITOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");

	}

	private ArrayList getStatuses() {
		Status statusesArray[] = { Status.AVAILABLE, Status.PENDING, Status.PRIVATE, Status.UNAVAILABLE };
		List statuses = Arrays.asList(statusesArray);
		return new ArrayList(statuses);
	}

	/**
	 * Initialize data of a DatasetBean and set session attributes for displaying selected data of this DatasetBean
	 * 
	 * @param db
	 * @return
	 * 
	 * @author ywang (Feb, 2008)
	 */
	public DatasetBean initializeAttributes(int datasetId) {
		DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
		DatasetBean db = dsdao.initialDatasetData(datasetId);
		session.setAttribute("newDataset", db);
		session.setAttribute("allItems", db.getItemDefCrf().clone());
		session.setAttribute("allSelectedItems", db.getItemDefCrf().clone());
		StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
		StudyDAO studydao = new StudyDAO(sm.getDataSource());
		StudyBean theStudy = (StudyBean) studydao.findByPK(sm.getUserBean().getActiveStudyId());
		ArrayList<StudyGroupClassBean> allSelectedGroups = sgcdao.findAllActiveByStudy(theStudy);
		ArrayList<Integer> selectedSubjectGroupIds = db.getSubjectGroupIds();
		if (selectedSubjectGroupIds != null && allSelectedGroups != null) {
			for (Integer id : selectedSubjectGroupIds) {
				for (int i = 0; i < allSelectedGroups.size(); ++i) {
					if (allSelectedGroups.get(i).getId() == id) {
						allSelectedGroups.get(i).setSelected(true);
						break;
					}
				}
			}
		}
		session.setAttribute("allSelectedGroups", allSelectedGroups);

		return db;
	}
}
