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

import org.akaza.openclinica.bean.core.Role;
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
import org.akaza.openclinica.bean.submit.EventCRFBean;
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
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author jxu
 * 
 *         Processes the request of restoring a top level study, all the data assoicated with this study will be
 *         restored
 */
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
@Component
public class RestoreStudyServlet extends Controller {
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

		StudyBean study = (StudyBean) sdao.findByPK(studyId);
		// find all sites
		ArrayList<StudyBean> sites = (ArrayList) sdao.findAllByParent(studyId);

		// find all user and roles in the study, include ones in sites
		UserAccountDAO udao = getUserAccountDAO();
		ArrayList<StudyUserRoleBean> userRoles = udao.findAllByStudyId(studyId);

		// find all subjects in the study, include ones in sites
		StudySubjectDAO ssdao = getStudySubjectDAO();
		ArrayList<StudySubjectBean> subjects = ssdao.findAllByStudy(study);

		// find all events in the study, include ones in sites
		StudyEventDefinitionDAO sefdao = getStudyEventDefinitionDAO();
		ArrayList<StudyEventDefinitionBean> definitions = sefdao.findAllByStudy(study);

		String action = request.getParameter("action");
		if (studyId == 0) {
			addPageMessage(respage.getString("please_choose_a_study_to_restore"), request);
			forwardPage(Page.STUDY_LIST_SERVLET, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				request.setAttribute("studyToRestore", study);

				request.setAttribute("sitesToRestore", sites);

				request.setAttribute("userRolesToRestore", userRoles);

				request.setAttribute("subjectsToRestore", subjects);

				request.setAttribute("definitionsToRRestore", definitions);
				forwardPage(Page.RESTORE_STUDY, request, response);
			} else {
				logger.info("submit to restore the study");
				// change all statuses to unavailable
				study.setStatus(study.getOldStatus());
				study.setUpdater(currentUser);
				study.setUpdatedDate(new Date());
				sdao.update(study);

				// YW 09-27-2007 << restore auto-removed sites
				for (StudyBean site : sites) {
					if (site.getStatus() == Status.AUTO_DELETED) {
						site.setStatus(site.getOldStatus());
						site.setUpdater(currentUser);
						site.setUpdatedDate(new Date());
						sdao.update(site);
					}
				}

				// restore all users and roles
				for (StudyUserRoleBean role : userRoles) {
					getUserAccountService().autoRestoreStudyUserRole(role, currentUser);
				}

				// YW << Meanwhile update current active study if restored study
				// is current active study
				if (study.getId() == currentStudy.getId()) {
					currentStudy.setStatus(Status.AVAILABLE);

					StudyUserRoleBean r = udao
							.findRoleByUserNameAndStudyId(currentUser.getName(), currentStudy.getId());
					currentRole.setRole(r.getRole());
				}
				// when an active site's parent study has been restored, this
				// active site will be restored as well if it was auto-removed
				else if (currentStudy.getParentStudyId() == study.getId()
						&& currentStudy.getStatus() == Status.AUTO_DELETED) {
					currentStudy.setStatus(Status.AVAILABLE);

					StudyUserRoleBean r = udao
							.findRoleByUserNameAndStudyId(currentUser.getName(), currentStudy.getId());
					StudyUserRoleBean rInParent = udao.findRoleByUserNameAndStudyId(currentUser.getName(),
							currentStudy.getParentStudyId());
					// according to logic in Controller.java: inherited
					// role from parent study, pick the higher role
					currentRole.setRole(Role.get(Role.max(r.getRole(), rInParent.getRole()).getId()));
				}
				// YW 06-18-2007 >>

				// restore all subjects
				for (StudySubjectBean subject : subjects) {
					if (subject.getStatus().equals(Status.AUTO_DELETED)) {
						subject.setStatus(Status.AVAILABLE);
						subject.setUpdater(currentUser);
						subject.setUpdatedDate(new Date());
						ssdao.update(subject);
					}
				}

				StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
				SubjectGroupMapDAO sgmdao = getSubjectGroupMapDAO();
				ArrayList<StudyGroupClassBean> groups = sgcdao.findAllByStudy(study);
				for (StudyGroupClassBean group : groups) {
					if (group.getStatus().equals(Status.AUTO_DELETED)) {
						group.setStatus(Status.AVAILABLE);
						group.setUpdater(currentUser);
						group.setUpdatedDate(new Date());
						sgcdao.update(group);
						// all subject_group_map
						ArrayList subjectGroupMaps = sgmdao.findAllByStudyGroupClassId(group.getId());
						for (Object subjectGroupMap : subjectGroupMaps) {
							SubjectGroupMapBean sgMap = (SubjectGroupMapBean) subjectGroupMap;
							if (sgMap.getStatus().equals(Status.AUTO_DELETED)) {
								sgMap.setStatus(Status.AVAILABLE);
								sgMap.setUpdater(currentUser);
								sgMap.setUpdatedDate(new Date());
								sgmdao.update(sgMap);
							}
						}
					}
				}

				// restore all event definitions and event
				EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
				StudyEventDAO sedao = getStudyEventDAO();
				for (StudyEventDefinitionBean definition : definitions) {
					if (definition.getStatus().equals(Status.AUTO_DELETED)) {
						definition.setStatus(Status.AVAILABLE);
						definition.setUpdater(currentUser);
						definition.setUpdatedDate(new Date());
						sefdao.update(definition);

						ArrayList<EventDefinitionCRFBean> edcs = (ArrayList) edcdao.findAllByDefinition(definition.getId());
						for (EventDefinitionCRFBean edc : edcs) {
							if (edc.getStatus().equals(Status.AUTO_DELETED)) {
								edc.setStatus(Status.AVAILABLE);
								edc.setUpdater(currentUser);
								edc.setUpdatedDate(new Date());
								edcdao.update(edc);
							}
						}

						ArrayList<StudyEventBean> events = (ArrayList) sedao.findAllByDefinition(definition.getId());
						EventCRFDAO ecdao = getEventCRFDAO();

						for (StudyEventBean event : events) {
							if (event.getStatus().equals(Status.AUTO_DELETED)) {
								event.setStatus(Status.AVAILABLE);
								event.setUpdater(currentUser);
								event.setUpdatedDate(new Date());
								sedao.update(event);

								ArrayList<EventCRFBean> eventCRFs = ecdao.findAllByStudyEvent(event);

								getEventCRFService().restoreEventCRFsFromAutoRemovedState(eventCRFs, currentUser);
							}
						}
					}
				}// for definitions

				DatasetDAO datadao = getDatasetDAO();
				ArrayList<DatasetBean> datasets = datadao.findAllByStudyId(study.getId());
				for (DatasetBean data : datasets) {
					if (data.getStatus().equals(Status.AUTO_DELETED)) {
						data.setStatus(Status.AVAILABLE);
						data.setUpdater(currentUser);
						data.setUpdatedDate(new Date());
						datadao.update(data);
					}
				}

				addPageMessage(respage.getString("this_study_has_been_restored_succesfully"), request);
				forwardPage(Page.STUDY_LIST_SERVLET, request, response);

			}
		}

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		if (getUserAccountBean(request).isSysAdmin()) {
			return Controller.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

}
