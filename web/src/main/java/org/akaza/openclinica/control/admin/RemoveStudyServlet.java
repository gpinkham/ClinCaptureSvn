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
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.Controller;

import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Processes the request of removing a top level study, all the data assoicated with this study will be removed
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
@Component
public class RemoveStudyServlet extends Controller {
	/**
     *
     */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		if (getUserAccountBean(request).isSysAdmin()) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.STUDY_LIST_SERVLET, resexception.getString("not_admin"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyDAO sdao = getStudyDAO();
		FormProcessor fp = new FormProcessor(request);
		int studyId = fp.getInt("id");
		StudyBean currentStudy = getCurrentStudy(request);
		UserAccountBean currentUser = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		// it's impossible to remove the current study
		if ((currentStudy.getParentStudyId() > 0 && currentStudy.getParentStudyId() == studyId)
				|| (currentStudy.getId() == studyId)) {
			addPageMessage(resword.getString("you_are_trying_to_remove_the_current_study"), request);
			forwardPage(Page.STUDY_LIST_SERVLET, request, response);
			return;
		}

		StudyBean study = (StudyBean) sdao.findByPK(studyId);
		// find all sites
		List<StudyBean> sites = (List<StudyBean>) sdao.findAllByParent(studyId);

		// find all user and roles in the study, include ones in sites
		UserAccountDAO udao = getUserAccountDAO();
		ArrayList<StudyUserRoleBean> userRoles = udao.findAllByStudyId(studyId);

		// find all subjects in the study, include ones in sites
		StudySubjectDAO ssdao = getStudySubjectDAO();
		List<StudySubjectBean> subjects = ssdao.findAllByStudy(study);

		// find all events in the study, include ones in sites
		StudyEventDefinitionDAO sefdao = getStudyEventDefinitionDAO();
		ArrayList<StudyEventDefinitionBean> definitions = sefdao.findAllByStudy(study);

		String action = request.getParameter("action");
		if (studyId == 0) {
			addPageMessage(respage.getString("please_choose_a_study_to_remove"), request);
			forwardPage(Page.STUDY_LIST_SERVLET, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				request.setAttribute("studyToRemove", study);

				request.setAttribute("sitesToRemove", sites);

				request.setAttribute("userRolesToRemove", userRoles);

				request.setAttribute("subjectsToRemove", subjects);

				request.setAttribute("definitionsToRemove", definitions);
				forwardPage(Page.REMOVE_STUDY, request, response);
			} else {
				logger.info("submit to remove the study");
				// change all statuses to unavailable
				study.setOldStatus(study.getStatus());
				study.setStatus(Status.DELETED);
				study.setUpdater(currentUser);
				study.setUpdatedDate(new Date());
				sdao.update(study);

				// remove all sites
				for (StudyBean site : sites) {
					if (!site.getStatus().equals(Status.DELETED)) {
						site.setOldStatus(site.getStatus());
						site.setStatus(Status.AUTO_DELETED);
						site.setUpdater(currentUser);
						site.setUpdatedDate(new Date());
						sdao.update(site);
					}
				}

				// remove all users and roles
				for (StudyUserRoleBean role : userRoles) {
					getUserAccountService().autoRemoveStudyUserRole(role, currentUser);
				}

				// YW << bug fix for that current active study has been deleted
				if (study.getId() == currentStudy.getId()) {
					currentStudy.setStatus(Status.DELETED);
					currentRole.setStatus(Status.DELETED);
				}
				// if current active study is a site and the deleted study is
				// this active site's parent study,
				// then this active site has to be removed as well
				// (auto-removed)
				else if (currentStudy.getParentStudyId() == study.getId()) {
					currentStudy.setStatus(Status.AUTO_DELETED);
					// we may need handle this later?
					currentRole.setStatus(Status.DELETED);
				}

				// remove all subjects
				for (StudySubjectBean subject : subjects) {
					if (!subject.getStatus().equals(Status.DELETED)) {
						subject.setStatus(Status.AUTO_DELETED);
						subject.setUpdater(currentUser);
						subject.setUpdatedDate(new Date());
						ssdao.update(subject);
					}
				}

				// remove all study_group_class
				// changed by jxu on 08-31-06, to fix the problem of no study_id
				// in study_group table
				StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
				SubjectGroupMapDAO sgmdao = getSubjectGroupMapDAO();

				ArrayList<StudyGroupClassBean> groups = sgcdao.findAllByStudy(study);
				for (StudyGroupClassBean group : groups) {
					if (!group.getStatus().equals(Status.DELETED)) {
						group.setStatus(Status.AUTO_DELETED);
						group.setUpdater(currentUser);
						group.setUpdatedDate(new Date());
						sgcdao.update(group);
						// all subject_group_map
						ArrayList<SubjectGroupMapBean> subjectGroupMaps = sgmdao.findAllByStudyGroupClassId(group
								.getId());
						for (SubjectGroupMapBean sgMap : subjectGroupMaps) {
							if (!sgMap.getStatus().equals(Status.DELETED)) {
								sgMap.setStatus(Status.AUTO_DELETED);
								sgMap.setUpdater(currentUser);
								sgMap.setUpdatedDate(new Date());
								sgmdao.update(sgMap);
							}
						}
					}
				}

				ArrayList<StudyGroupClassBean> groupClasses = (ArrayList<StudyGroupClassBean>) sgcdao
						.findAllActiveByStudy(study);
				for (StudyGroupClassBean gc : groupClasses) {
					if (!gc.getStatus().equals(Status.DELETED)) {
						gc.setStatus(Status.AUTO_DELETED);
						gc.setUpdater(currentUser);
						gc.setUpdatedDate(new Date());
						sgcdao.update(gc);
					}
				}

				// remove all event definitions and event
				EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
				StudyEventDAO sedao = getStudyEventDAO();
				for (StudyEventDefinitionBean definition : definitions) {
					if (!definition.getStatus().equals(Status.DELETED)) {
						definition.setStatus(Status.AUTO_DELETED);
						definition.setUpdater(currentUser);
						definition.setUpdatedDate(new Date());
						sefdao.update(definition);
						ArrayList<EventDefinitionCRFBean> edcs = (ArrayList) edcdao.findAllByDefinition(definition.getId());

						for (EventDefinitionCRFBean edc : edcs) {
							if (!edc.getStatus().equals(Status.DELETED)) {
								edc.setStatus(Status.AUTO_DELETED);
								edc.setUpdater(currentUser);
								edc.setUpdatedDate(new Date());
								edcdao.update(edc);
							}
						}

						ArrayList<StudyEventBean> events = (ArrayList) sedao.findAllByDefinition(definition.getId());

						for (StudyEventBean event : events) {
							if (!event.getStatus().equals(Status.DELETED)) {
								event.setStatus(Status.AUTO_DELETED);
								event.setUpdater(currentUser);
								event.setUpdatedDate(new Date());
								sedao.update(event);

								getEventCRFService().removeEventCRFsByStudyEvent(event, currentUser);
							}
						}
					}
				}// for definitions

				DatasetDAO datadao = getDatasetDAO();
				ArrayList<DatasetBean> datasets = datadao.findAllByStudyId(study.getId());
				for (DatasetBean data : datasets) {
					if (!data.getStatus().equals(Status.DELETED)) {
						data.setStatus(Status.AUTO_DELETED);
						data.setUpdater(currentUser);
						data.setUpdatedDate(new Date());
						datadao.update(data);
					}
				}

				addPageMessage(resexception.getString("this_study_has_been_removed_succesfully"), request);
				forwardPage(Page.STUDY_LIST_SERVLET, request, response);

			}
		}

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
