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

import com.clinovo.model.CodedItem;
import com.clinovo.service.CodedItemService;
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
import org.akaza.openclinica.control.core.SecureController;
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
 * Processes the request of removing a top level study, all the data assoicated with this study will be removed
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" })
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

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
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
				for (int i = 0; i < sites.size(); i++) {
					StudyBean site = (StudyBean) sites.get(i);
					if (!site.getStatus().equals(Status.DELETED)) {
						site.setOldStatus(site.getStatus());
						site.setStatus(Status.AUTO_DELETED);
						site.setUpdater(currentUser);
						site.setUpdatedDate(new Date());
						sdao.update(site);
					}
				}

				// remove all users and roles
				for (int i = 0; i < userRoles.size(); i++) {
					StudyUserRoleBean role = (StudyUserRoleBean) userRoles.get(i);
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
				for (int i = 0; i < subjects.size(); i++) {
					StudySubjectBean subject = (StudySubjectBean) subjects.get(i);
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

				ArrayList groups = sgcdao.findAllByStudy(study);
				for (int i = 0; i < groups.size(); i++) {
					StudyGroupClassBean group = (StudyGroupClassBean) groups.get(i);
					if (!group.getStatus().equals(Status.DELETED)) {
						group.setStatus(Status.AUTO_DELETED);
						group.setUpdater(currentUser);
						group.setUpdatedDate(new Date());
						sgcdao.update(group);
						// all subject_group_map
						ArrayList subjectGroupMaps = sgmdao.findAllByStudyGroupClassId(group.getId());
						for (int j = 0; j < subjectGroupMaps.size(); j++) {
							SubjectGroupMapBean sgMap = (SubjectGroupMapBean) subjectGroupMaps.get(j);
							if (!sgMap.getStatus().equals(Status.DELETED)) {
								sgMap.setStatus(Status.AUTO_DELETED);
								sgMap.setUpdater(currentUser);
								sgMap.setUpdatedDate(new Date());
								sgmdao.update(sgMap);
							}
						}
					}
				}

				ArrayList groupClasses = sgcdao.findAllActiveByStudy(study);
				for (int i = 0; i < groupClasses.size(); i++) {
					StudyGroupClassBean gc = (StudyGroupClassBean) groupClasses.get(i);
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
				for (int i = 0; i < definitions.size(); i++) {
					StudyEventDefinitionBean definition = (StudyEventDefinitionBean) definitions.get(i);
					if (!definition.getStatus().equals(Status.DELETED)) {
						definition.setStatus(Status.AUTO_DELETED);
						definition.setUpdater(currentUser);
						definition.setUpdatedDate(new Date());
						sefdao.update(definition);
						ArrayList edcs = (ArrayList) edcdao.findAllByDefinition(definition.getId());
						for (int j = 0; j < edcs.size(); j++) {
							EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcs.get(j);
							if (!edc.getStatus().equals(Status.DELETED)) {
								edc.setStatus(Status.AUTO_DELETED);
								edc.setUpdater(currentUser);
								edc.setUpdatedDate(new Date());
								edcdao.update(edc);
							}
						}

						ArrayList events = (ArrayList) sedao.findAllByDefinition(definition.getId());
						EventCRFDAO ecdao = getEventCRFDAO();

						for (int j = 0; j < events.size(); j++) {
							StudyEventBean event = (StudyEventBean) events.get(j);
							if (!event.getStatus().equals(Status.DELETED)) {
								event.setStatus(Status.AUTO_DELETED);
								event.setUpdater(currentUser);
								event.setUpdatedDate(new Date());
								sedao.update(event);

								ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);
								CodedItemService codedItemService = getCodedItemService();
								ItemDataDAO iddao = getItemDataDAO();

								for (int k = 0; k < eventCRFs.size(); k++) {
									EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(k);
									if (!eventCRF.getStatus().equals(Status.DELETED)) {
										eventCRF.setOldStatus(eventCRF.getStatus());
										eventCRF.setStatus(Status.AUTO_DELETED);
										eventCRF.setUpdater(currentUser);
										eventCRF.setUpdatedDate(new Date());
										ecdao.update(eventCRF);

										ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
										for (int a = 0; a < itemDatas.size(); a++) {
											ItemDataBean item = (ItemDataBean) itemDatas.get(a);
											if (!item.getStatus().equals(Status.DELETED)) {
												item.setOldStatus(item.getStatus());
												item.setStatus(Status.AUTO_DELETED);
												item.setUpdater(currentUser);
												item.setUpdatedDate(new Date());
												iddao.update(item);
											}

											CodedItem codedItem = codedItemService.findCodedItem(item.getId());

											if (codedItem != null) {

												codedItem.setStatus("REMOVED");
												codedItemService.saveCodedItem(codedItem);
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
				for (int i = 0; i < dataset.size(); i++) {
					DatasetBean data = (DatasetBean) dataset.get(i);
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
		return SecureController.ADMIN_SERVLET_CODE;
	}
}
