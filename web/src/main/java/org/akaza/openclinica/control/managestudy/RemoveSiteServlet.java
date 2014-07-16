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
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
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
 *         Removes a site from a study
 */
@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class RemoveSiteServlet extends Controller {

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
		if (currentStudy.getId() != study.getParentStudyId()) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
							+ respage.getString("change_active_study_or_contact"), request);
			forwardPage(Page.MENU_SERVLET, request, response);
			return;
		}

		// find all user and roles
		UserAccountDAO udao = getUserAccountDAO();
		ArrayList userRoles = udao.findAllByStudyId(siteId);

		// find all subjects
		StudySubjectDAO ssdao = getStudySubjectDAO();
		ArrayList subjects = ssdao.findAllByStudy(study);

		String action = request.getParameter("action");
		if (StringUtil.isBlank(idString)) {
			addPageMessage(respage.getString("please_choose_a_site_to_remove"), request);
			forwardPage(Page.SITE_LIST_SERVLET, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				request.setAttribute("siteToRemove", study);

				request.setAttribute("userRolesToRemove", userRoles);

				request.setAttribute("subjectsToRemove", subjects);

				forwardPage(Page.REMOVE_SITE, request, response);
			} else {
				logger.info("submit to remove the site");
				// change all statuses to unavailable
				StudyDAO studao = getStudyDAO();
				study.setOldStatus(study.getStatus());
				study.setStatus(Status.DELETED);
				study.setUpdater(currentUser);
				study.setUpdatedDate(new Date());
				studao.update(study);

				// remove all users and roles
				for (int i = 0; i < userRoles.size(); i++) {
					StudyUserRoleBean role = (StudyUserRoleBean) userRoles.get(i);
					getUserAccountService().autoRemoveStudyUserRole(role, currentUser);
				}

				if (study.getId() == currentStudy.getId()) {
					currentStudy.setStatus(Status.DELETED);
					currentRole.setStatus(Status.DELETED);
				}

				// remove all study_group
				StudyGroupDAO sgdao = getStudyGroupDAO();
				SubjectGroupMapDAO sgmdao = getSubjectGroupMapDAO();
				ArrayList groups = sgdao.findAllByStudy(study);
				for (int i = 0; i < groups.size(); i++) {
					StudyGroupBean group = (StudyGroupBean) groups.get(i);
					if (!group.getStatus().equals(Status.DELETED)) {
						group.setStatus(Status.AUTO_DELETED);
						group.setUpdater(currentUser);
						group.setUpdatedDate(new Date());
						sgdao.update(group);
						// all subject_group_map
						ArrayList subjectGroupMaps = sgmdao.findAllByStudyGroupId(group.getId());
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

				StudyEventDAO sedao = getStudyEventDAO();
				for (int i = 0; i < subjects.size(); i++) {
					StudySubjectBean subject = (StudySubjectBean) subjects.get(i);

					if (!subject.getStatus().equals(Status.DELETED)) {
						subject.setStatus(Status.AUTO_DELETED);
						subject.setUpdater(currentUser);
						subject.setUpdatedDate(new Date());
						ssdao.update(subject);

						ArrayList events = sedao.findAllByStudySubject(subject);

						for (int j = 0; j < events.size(); j++) {
							StudyEventBean event = (StudyEventBean) events.get(j);
							if (!event.getStatus().equals(Status.DELETED)) {
								event.setStatus(Status.AUTO_DELETED);
								event.setUpdater(currentUser);
								event.setUpdatedDate(new Date());
								sedao.update(event);

								getEventCRFService().removeEventCRFsByStudyEvent(event, currentUser);
							}
						}
					}
				}// for subjects

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

				addPageMessage(respage.getString("this_site_has_been_removed_succesfully"), request);

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
