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

import org.akaza.openclinica.bean.core.GroupClassType;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * Removes a subject group class from a study
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
@Component
public class RemoveSubjectGroupClassServlet extends Controller {
	/**
     *
     */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, getResPage().getString("current_study_locked"), request,
				response);
		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET,
				getResException().getString("not_study_director"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		String action = request.getParameter("action");
		FormProcessor fp = new FormProcessor(request);
		int classId = fp.getInt("id");

		if (classId == 0) {
			addPageMessage(getResPage().getString("please_choose_a_subject_group_class_to_remove"), request);
			forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, request, response);
		} else {
			StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(getDataSource());
			StudyGroupDAO sgdao = new StudyGroupDAO(getDataSource());
			StudyDAO studyDao = new StudyDAO(getDataSource());
			SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(getDataSource());

			if (action.equalsIgnoreCase("confirm")) {
				clearSession(request.getSession());

				StudyGroupClassBean group = (StudyGroupClassBean) sgcdao.findByPK(classId);
				StudyBean study = (StudyBean) studyDao.findByPK(group.getStudyId());

				checkRoleByUserAndStudy(request, response, ub, group.getStudyId(), study.getParentStudyId());

				if (group.getStatus().equals(Status.DELETED)) {
					addPageMessage(getResPage().getString("this_subject_group_class_has_been_deleted_already"), request);
					forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, request, response);
					return;
				}

				if (group.getGroupClassTypeId() == GroupClassType.DYNAMIC.getId()) {
					StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
					ArrayList<StudyEventDefinitionBean> orderedDefinitions = (ArrayList<StudyEventDefinitionBean>) seddao
							.findAllOrderedByStudyGroupClassId(group.getId());

					EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(getDataSource());
					for (StudyEventDefinitionBean def : orderedDefinitions) {
						def.setCrfNum(edcdao.findAllActiveParentsByEventDefinitionId(def.getId()).size());
					}
					request.setAttribute("orderedDefinitions", orderedDefinitions);

				} else {
					ArrayList<StudyGroupBean> studyGroups = (ArrayList<StudyGroupBean>) sgdao
							.findAllByGroupClass(group);

					for (StudyGroupBean sg : studyGroups) {
						ArrayList subjectMaps = sgmdao.findAllByStudyGroupClassAndGroup(group.getId(), sg.getId());
						sg.setSubjectMaps(subjectMaps);
					}
					request.setAttribute("studyGroups", studyGroups);
				}
				request.getSession().setAttribute("group", group);

				forwardPage(Page.REMOVE_SUBJECT_GROUP_CLASS, request, response);

			} else if (action.equalsIgnoreCase("submit")) {
				StudyGroupClassBean group = (StudyGroupClassBean) request.getSession().getAttribute("group");
				group.setStatus(Status.DELETED);
				group.setUpdater(ub);
				group.setDefault(false);
				sgcdao.update(group);

				ArrayList<SubjectGroupMapBean> subjectMaps = (ArrayList<SubjectGroupMapBean>) sgmdao
						.findAllByStudyGroupClassId(group.getId());
				for (SubjectGroupMapBean sgmb : subjectMaps) {
					if (!sgmb.getStatus().equals(Status.DELETED)) {
						sgmb.setStatus(Status.AUTO_DELETED);
						sgmb.setUpdater(ub);
						sgmdao.update(sgmb);
					}
				}
				addPageMessage(getResPage().getString("this_subject_group_class_was_removed_succesfully"), request);
				forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, request, response);
			} else {
				addPageMessage(getResPage().getString("no_action_specified"), request);
				forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, request, response);
			}
		}
	}

	private void clearSession(HttpSession session) {
		session.removeAttribute("group");
	}
}
