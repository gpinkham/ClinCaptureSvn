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
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
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
import java.util.ArrayList;

/**
 * Views details of a Subject Group Class
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class ViewSubjectGroupClassServlet extends Controller {
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}
		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study") + "\n"
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET,
				getResException().getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		FormProcessor fp = new FormProcessor(request);
		int classId = fp.getInt("id");

		if (classId == 0) {

			addPageMessage(getResPage().getString("please_choose_a_subject_group_class_to_view"), request);
			forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, request, response);
		} else {
			StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(getDataSource());
			StudyGroupDAO sgdao = new StudyGroupDAO(getDataSource());
			StudyDAO studyDao = new StudyDAO(getDataSource());

			StudyGroupClassBean group = (StudyGroupClassBean) sgcdao.findByPK(classId);
			StudyBean study = (StudyBean) studyDao.findByPK(group.getStudyId());

			checkRoleByUserAndStudy(request, response, ub, group.getStudyId(), study.getParentStudyId());

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
				SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(getDataSource());
				ArrayList<StudyGroupBean> studyGroups = (ArrayList<StudyGroupBean>) sgdao.findAllByGroupClass(group);

				for (StudyGroupBean sg : studyGroups) {
					ArrayList subjectMaps = sgmdao.findAllByStudyGroupClassAndGroup(group.getId(), sg.getId());
					sg.setSubjectMaps(subjectMaps);
				}

				request.setAttribute("studyGroups", studyGroups);
			}
			request.setAttribute("group", group);
			forwardPage(Page.VIEW_SUBJECT_GROUP_CLASS, request, response);
		}
	}
}
