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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

/**
 * Edit Dataset Servlet.
 * @author thickerson
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused"})
@Component
public class EditDatasetServlet extends SpringServlet {

	/**
	 * Get link for this servlet.
	 * @param dsId dataset ID.
	 * @return link
	 */
	public static String getLink(int dsId) {
		return "EditDataset?dsId=" + dsId;
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		FormProcessor fp = new FormProcessor(request);

		request.setAttribute("subjectAgeAtEvent",
				currentStudy.getStudyParameterConfig().getCollectDob().equals("3") ? "0" : "1");

		int dsId = fp.getInt("dsId");
		DatasetBean dataset = initializeAttributes(request, dsId);

		StudyDAO sdao = getStudyDAO();
		StudyBean study = (StudyBean) sdao.findByPK(dataset.getStudyId());
		// Checking if user has permission to access the current study/site
		checkRoleByUserAndStudy(request, response, ub, study.getParentStudyId(), study.getId());

		if (dataset.isContainsMaskedCRFs()) {
			addPageMessage(getResWord().getString("this_dataset_contains_items_from_masked_crfs"), request);
		}

		// Checking the dataset belongs to current study or a site of current study
		if (study.getId() != currentStudy.getId() && study.getParentStudyId() != currentStudy.getId()) {
			addPageMessage(
					getResPage().getString("no_have_correct_privilege_current_study") + " "
							+ getResPage().getString("change_active_study_or_contact"), request);
			throw new InsufficientPermissionException(Page.MENU,
					getResException().getString("not_allowed_access_extract_data_servlet"), "1");
		}

		if ((Role.isMonitor(currentRole.getRole()) || currentRole.getRole() == Role.INVESTIGATOR)
				&& (dataset.getOwnerId() != ub.getId())) {
			addPageMessage(
					getResPage().getString("no_have_correct_privilege_current_study") + " "
							+ getResPage().getString("change_active_study_or_contact"), request);
			throw new InsufficientPermissionException(Page.MENU,
					getResException().getString("not_allowed_access_extract_data_servlet"), "1");
		}

		HashMap events = (LinkedHashMap) request.getSession().getAttribute("eventsForCreateDataset");
		CRFDAO crfdao = getCRFDAO();

		if (events == null || events.isEmpty()) {
			events = new LinkedHashMap();
			StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();

			StudyBean studyWithEventDefinitions = currentStudy;
			if (currentStudy.getParentStudyId() > 0) {
				studyWithEventDefinitions = new StudyBean();
				studyWithEventDefinitions.setId(currentStudy.getParentStudyId());

			}
			ArrayList seds = seddao.findAllActiveByStudy(studyWithEventDefinitions);
			for (Object sed1 : seds) {
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) sed1;
				ArrayList crfs = (ArrayList) crfdao.findAllActiveUnmaskedByDefinition(sed, ub);
				if (!crfs.isEmpty()) {
					events.put(sed, crfs);
				}
			}
			if (events.isEmpty()) {
				addPageMessage(getResPage().getString("not_have_study_definitions_assigned"), request);
				forwardPage(Page.VIEW_DATASETS, request, response);
			} else {
				request.setAttribute("eventlist", events);
				request.getSession().setAttribute("eventsForCreateDataset", events);
			}
		}
		request.setAttribute("dataset", dataset);
		request.setAttribute("statuses", getStatuses());
		forwardPage(Page.EDIT_DATASET, request, response);
	}

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

	private ArrayList getStatuses() {
		Status statusesArray[] = { Status.AVAILABLE, Status.PENDING, Status.PRIVATE, Status.UNAVAILABLE };
		List statuses = Arrays.asList(statusesArray);
		return new ArrayList(statuses);
	}

	/**
	 * Initialize data of a DatasetBean and set session attributes for displaying selected data of this DatasetBean
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param datasetId
	 *            int
	 * @return DatasetBean
	 */
	public DatasetBean initializeAttributes(HttpServletRequest request, int datasetId) {
		UserAccountBean ub = getUserAccountBean(request);
		DatasetDAO dsdao = getDatasetDAO();
		DatasetBean db = getDatasetService().initialDatasetData(datasetId, ub);
		DateTimeZone userTimeZone = DateTimeZone.forID(getUserAccountBean().getUserTimeZoneId());
		if (db.getDateStart() != null) {
			DateTime userLocalDateStart = new DateTime(db.getDateStart()).withZone(userTimeZone);
			db.setFirstMonth(userLocalDateStart.getMonthOfYear());
			db.setFirstYear(userLocalDateStart.getYear());
		}
		if (db.getDateEnd() != null) {
			DateTime userLocalDateEnd = new DateTime(db.getDateEnd()).withZone(userTimeZone);
			db.setLastMonth(userLocalDateEnd.getMonthOfYear());
			db.setLastYear(userLocalDateEnd.getYear());
		}
		request.getSession().setAttribute("newDataset", db);
		StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
		StudyDAO studydao = getStudyDAO();
		StudyBean theStudy = (StudyBean) studydao.findByPK(ub.getActiveStudyId());
		ArrayList<StudyGroupClassBean> allSelectedGroups = sgcdao.findAllActiveByStudy(theStudy);
		ArrayList<Integer> selectedSubjectGroupIds = db.getSubjectGroupIds();
		if (selectedSubjectGroupIds != null && allSelectedGroups != null) {
			for (Integer id : selectedSubjectGroupIds) {
				for (StudyGroupClassBean allSelectedGroup : allSelectedGroups) {
					if (allSelectedGroup.getId() == id) {
						allSelectedGroup.setSelected(true);
						break;
					}
				}
			}
		}
		db.setAllSelectedGroups(allSelectedGroups);
		return db;
	}
}
