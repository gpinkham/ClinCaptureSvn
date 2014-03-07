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
import org.akaza.openclinica.bean.submit.ItemDataBean;
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
import org.akaza.openclinica.dao.submit.ItemDataDAO;
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
@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class RestoreStudyServlet extends Controller {
	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (ub.isSysAdmin()) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.STUDY_LIST_SERVLET, resexception.getString("not_admin"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		StudyDAO sdao = getStudyDAO();
		FormProcessor fp = new FormProcessor(request);
		int studyId = fp.getInt("id");

		StudyBean study = (StudyBean) sdao.findByPK(studyId);
		// find all sites
		ArrayList sites = (ArrayList) sdao.findAllByParent(studyId);

		// find all user and roles in the study, include ones in sites
		UserAccountDAO udao = getUserAccountDAO();
		ArrayList userRoles = udao.findAllByStudyId(studyId);

		// find all subjects in the study, include ones in sites
		StudySubjectDAO ssdao = getStudySubjectDAO();
		ArrayList subjects = ssdao.findAllByStudy(study);

		// find all events in the study, include ones in sites
		StudyEventDefinitionDAO sefdao = getStudyEventDefinitionDAO();
		ArrayList definitions = sefdao.findAllByStudy(study);

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
				StudyDAO studao = getStudyDAO();
				study.setStatus(study.getOldStatus());
				study.setUpdater(ub);
				study.setUpdatedDate(new Date());
				studao.update(study);

				// YW 09-27-2007 << restore auto-removed sites
				for (Object site1 : sites) {
					StudyBean site = (StudyBean) site1;
					if (site.getStatus() == Status.AUTO_DELETED) {
						site.setStatus(site.getOldStatus());
						site.setUpdater(ub);
						site.setUpdatedDate(new Date());
						sdao.update(site);
					}
				}

				// restore all users and roles
				for (Object userRole : userRoles) {
					StudyUserRoleBean role = (StudyUserRoleBean) userRole;
					if (role.getStatus().equals(Status.AUTO_DELETED)) {
						role.setStatus(Status.AVAILABLE);
						role.setUpdater(ub);
						role.setUpdatedDate(new Date());
						udao.updateStudyUserRole(role, role.getUserName());
					}
				}

				// YW << Meanwhile update current active study if restored study
				// is current active study
				if (study.getId() == currentStudy.getId()) {
					currentStudy.setStatus(Status.AVAILABLE);

					StudyUserRoleBean r = (getUserAccountDAO()).findRoleByUserNameAndStudyId(ub.getName(),
							currentStudy.getId());
					currentRole.setRole(r.getRole());
				}
				// when an active site's parent study has been restored, this
				// active site will be restored as well if it was auto-removed
				else if (currentStudy.getParentStudyId() == study.getId()
						&& currentStudy.getStatus() == Status.AUTO_DELETED) {
					currentStudy.setStatus(Status.AVAILABLE);

					StudyUserRoleBean r = (getUserAccountDAO()).findRoleByUserNameAndStudyId(ub.getName(),
							currentStudy.getId());
					StudyUserRoleBean rInParent = (getUserAccountDAO()).findRoleByUserNameAndStudyId(ub.getName(),
							currentStudy.getParentStudyId());
					// according to logic in Controller.java: inherited
					// role from parent study, pick the higher role
					currentRole.setRole(Role.get(Role.max(r.getRole(), rInParent.getRole()).getId()));
				}
				// YW 06-18-2007 >>

				// restore all subjects
				for (Object subject1 : subjects) {
					StudySubjectBean subject = (StudySubjectBean) subject1;
					if (subject.getStatus().equals(Status.AUTO_DELETED)) {
						subject.setStatus(Status.AVAILABLE);
						subject.setUpdater(ub);
						subject.setUpdatedDate(new Date());
						ssdao.update(subject);
					}
				}

				StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
				SubjectGroupMapDAO sgmdao = getSubjectGroupMapDAO();
				ArrayList groups = sgcdao.findAllByStudy(study);
				for (Object group1 : groups) {
					StudyGroupClassBean group = (StudyGroupClassBean) group1;
					if (group.getStatus().equals(Status.AUTO_DELETED)) {
						group.setStatus(Status.AVAILABLE);
						group.setUpdater(ub);
						group.setUpdatedDate(new Date());
						sgcdao.update(group);
						// all subject_group_map
						ArrayList subjectGroupMaps = sgmdao.findAllByStudyGroupClassId(group.getId());
						for (Object subjectGroupMap : subjectGroupMaps) {
							SubjectGroupMapBean sgMap = (SubjectGroupMapBean) subjectGroupMap;
							if (sgMap.getStatus().equals(Status.AUTO_DELETED)) {
								sgMap.setStatus(Status.AVAILABLE);
								sgMap.setUpdater(ub);
								sgMap.setUpdatedDate(new Date());
								sgmdao.update(sgMap);
							}
						}
					}
				}

				// restore all event definitions and event
				EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
				StudyEventDAO sedao = getStudyEventDAO();
				for (Object definition1 : definitions) {
					StudyEventDefinitionBean definition = (StudyEventDefinitionBean) definition1;
					if (definition.getStatus().equals(Status.AUTO_DELETED)) {
						definition.setStatus(Status.AVAILABLE);
						definition.setUpdater(ub);
						definition.setUpdatedDate(new Date());
						sefdao.update(definition);
						ArrayList edcs = (ArrayList) edcdao.findAllByDefinition(definition.getId());
						for (Object edc1 : edcs) {
							EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edc1;
							if (edc.getStatus().equals(Status.AUTO_DELETED)) {
								edc.setStatus(Status.AVAILABLE);
								edc.setUpdater(ub);
								edc.setUpdatedDate(new Date());
								edcdao.update(edc);
							}
						}

						ArrayList events = (ArrayList) sedao.findAllByDefinition(definition.getId());
						EventCRFDAO ecdao = getEventCRFDAO();

						for (Object event1 : events) {
							StudyEventBean event = (StudyEventBean) event1;
							if (event.getStatus().equals(Status.AUTO_DELETED)) {
								event.setStatus(Status.AVAILABLE);
								event.setUpdater(ub);
								event.setUpdatedDate(new Date());
								sedao.update(event);

								ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

								ItemDataDAO iddao = getItemDataDAO();
								for (Object eventCRF1 : eventCRFs) {
									EventCRFBean eventCRF = (EventCRFBean) eventCRF1;
									if (eventCRF.getStatus().equals(Status.AUTO_DELETED)) {
										eventCRF.setStatus(eventCRF.getOldStatus());
										eventCRF.setUpdater(ub);
										eventCRF.setUpdatedDate(new Date());
										ecdao.update(eventCRF);

										ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
										for (Object itemData : itemDatas) {
											ItemDataBean item = (ItemDataBean) itemData;
											if (item.getStatus().equals(Status.AUTO_DELETED)) {
												item.setStatus(item.getOldStatus());
												item.setUpdater(ub);
												item.setUpdatedDate(new Date());
												iddao.update(item);
											}
										}
									}
								}
							}
						}
					}
				}// for definitions

				DatasetDAO datadao = getDatasetDAO();
				ArrayList dataset = datadao.findAllByStudyId(study.getId());
				for (Object aDataset : dataset) {
					DatasetBean data = (DatasetBean) aDataset;
					if (data.getStatus().equals(Status.AUTO_DELETED)) {
						data.setStatus(Status.AVAILABLE);
						data.setUpdater(ub);
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
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return Controller.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

}
