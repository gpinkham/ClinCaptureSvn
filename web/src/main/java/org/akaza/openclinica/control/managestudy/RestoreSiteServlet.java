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
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Restores a removed site and all its data, including users. roles, study groups, definitions, events and items
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "serial", "unchecked" })
@Component
public class RestoreSiteServlet extends Controller {
	/**
     *
     */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		checkStudyLocked(Page.SITE_LIST_SERVLET, respage.getString("current_study_locked"), request, response);
		if (getUserAccountBean(request).isSysAdmin()
				|| getCurrentRole(request).getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.SITE_LIST_SERVLET, resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyDAO sdao = getStudyDAO();
		String idString = request.getParameter("id");
		logger.info("site id:" + idString);

		StudyBean currentStudy = getCurrentStudy(request);
		UserAccountBean currentUser = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		int siteId = Integer.valueOf(idString.trim());
		StudyBean study = (StudyBean) sdao.findByPK(siteId);

		// find all user and roles
		UserAccountDAO udao = getUserAccountDAO();
		ArrayList<StudyUserRoleBean> userRoles = (ArrayList<StudyUserRoleBean>) udao.findAllByStudyId(siteId);

		// find all subjects
		StudySubjectDAO ssdao = getStudySubjectDAO();
		ArrayList<StudySubjectBean> subjects = (ArrayList<StudySubjectBean>) ssdao.findAllByStudy(study);

		String action = request.getParameter("action");
		if (StringUtil.isBlank(idString)) {
			addPageMessage(respage.getString("please_choose_a_site_to_restore"), request);
			forwardPage(Page.SITE_LIST_SERVLET, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				// site can be restored when its parent study is not "removed"
				StudyBean parentstudy = (StudyBean) sdao.findByPK(study.getParentStudyId());
				if (!"removed".equals(parentstudy.getStatus().getName())) {
					request.setAttribute("siteToRestore", study);

					request.setAttribute("userRolesToRestore", userRoles);

					request.setAttribute("subjectsToRestore", subjects);

				} else {
					MessageFormat mf = new MessageFormat("");
					mf.applyPattern(respage.getString("choosen_site_cannot_restored"));
					Object[] arguments = { study.getName(), parentstudy.getName() };
					addPageMessage(mf.format(arguments), request);
					forwardPage(Page.STUDY_LIST_SERVLET, request, response);
				}
				forwardPage(Page.RESTORE_SITE, request, response);
			} else {
				logger.info("submit to restore the site");
				// change all statuses to unavailable
				Status newStatus = Status.AVAILABLE;
				if (study.getParentStudyId() > 0) {
					StudyBean parentStudy = (StudyBean) sdao.findByPK(study.getParentStudyId());
					newStatus = parentStudy.getStatus();
				}

				study.setOldStatus(study.getStatus());
				study.setStatus(newStatus);
				study.setUpdater(currentUser);
				study.setUpdatedDate(new Date());
				sdao.update(study);

				// restore all users and roles
				for (StudyUserRoleBean role : userRoles) {
					getUserAccountService().autoRestoreStudyUserRole(role, currentUser);
				}

				// Meanwhile update current active study
				// attribute of session if restored study is current active
				// study
				if (study.getId() == currentStudy.getId()) {
					currentStudy.setStatus(Status.AVAILABLE);

					StudyUserRoleBean r = udao
							.findRoleByUserNameAndStudyId(currentUser.getName(), currentStudy.getId());
					StudyUserRoleBean rInParent = udao.findRoleByUserNameAndStudyId(currentUser.getName(),
							currentStudy.getParentStudyId());
					// according to logic in SecureController.java: inherited
					// role from parent study, pick the higher role
					currentRole.setRole(Role.max(r.getRole(), rInParent.getRole()));
				}

				// restore all study_group
				StudyGroupDAO sgdao = getStudyGroupDAO();
				SubjectGroupMapDAO sgmdao = getSubjectGroupMapDAO();
				ArrayList<StudyGroupBean> groups = (ArrayList<StudyGroupBean>) sgdao.findAllByStudy(study);
				for (StudyGroupBean group : groups) {
					if (group.getStatus().equals(Status.AUTO_DELETED)) {
						group.setStatus(Status.AVAILABLE);
						group.setUpdater(currentUser);
						group.setUpdatedDate(new Date());
						sgdao.update(group);
						// all subject_group_map
						ArrayList<SubjectGroupMapBean> subjectGroupMaps = (ArrayList<SubjectGroupMapBean>) sgmdao
								.findAllByStudyGroupId(group.getId());
						for (SubjectGroupMapBean sgMap : subjectGroupMaps) {
							if (sgMap.getStatus().equals(Status.AUTO_DELETED)) {
								sgMap.setStatus(Status.AVAILABLE);
								sgMap.setUpdater(currentUser);
								sgMap.setUpdatedDate(new Date());
								sgmdao.update(sgMap);
							}
						}
					}
				}

				StudyEventDAO sedao = getStudyEventDAO();
				for (StudySubjectBean subject : subjects) {
					if (subject.getStatus().equals(Status.AUTO_DELETED)) {
						subject.setStatus(Status.AVAILABLE);
						subject.setUpdater(currentUser);
						subject.setUpdatedDate(new Date());
						ssdao.update(subject);

						ArrayList<StudyEventBean> events = (ArrayList<StudyEventBean>) sedao
								.findAllByStudySubject(subject);
						EventCRFDAO ecdao = getEventCRFDAO();

						for (StudyEventBean event : events) {
							if (event.getStatus().equals(Status.AUTO_DELETED)) {
								event.setStatus(Status.AVAILABLE);
								event.setUpdater(currentUser);
								event.setUpdatedDate(new Date());
								sedao.update(event);

								ArrayList<EventCRFBean> eventCRFs = (ArrayList<EventCRFBean>) ecdao
										.findAllByStudyEvent(event);

								ItemDataDAO iddao = getItemDataDAO();
								for (EventCRFBean eventCRF : eventCRFs) {
									// YW << fix broken page for storing site
									// >> YW
									if (eventCRF.getStatus().equals(Status.AUTO_DELETED)) {
										eventCRF.setStatus(eventCRF.getOldStatus());
										eventCRF.setUpdater(currentUser);
										eventCRF.setUpdatedDate(new Date());
										ecdao.update(eventCRF);

										ArrayList<ItemDataBean> itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
										for (ItemDataBean item : itemDatas) {
											if (item.getStatus().equals(Status.AUTO_DELETED)) {
												item.setStatus(item.getOldStatus());
												item.setUpdater(currentUser);
												item.setUpdatedDate(new Date());
												iddao.update(item);
											}
										}
									}
								}
							}
						}
					}
				}// for subjects

				DatasetDAO datadao = getDatasetDAO();
				ArrayList<DatasetBean> dataset = (ArrayList<DatasetBean>) datadao.findAllByStudyId(study.getId());
				for (DatasetBean data : dataset) {
					data.setStatus(Status.AVAILABLE);
					data.setUpdater(currentUser);
					data.setUpdatedDate(new Date());
					datadao.update(data);
				}

				addPageMessage(respage.getString("this_site_has_been_restored_succesfully"), request);
				String fromListSite = (String) request.getSession().getAttribute("fromListSite");
				if (fromListSite != null && fromListSite.equals("yes")) {
					request.getSession().removeAttribute("fromListSite");
					forwardPage(Page.SITE_LIST_SERVLET, request, response);
				} else {
					request.getSession().removeAttribute("fromListSite");
					forwardPage(Page.STUDY_LIST_SERVLET, request, response);
				}

			}

		}
	}

}
