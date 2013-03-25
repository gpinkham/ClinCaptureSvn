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
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.bean.core.GroupClassType;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * Views details of a Subject Group Class
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class ViewSubjectGroupClassServlet extends SecureController {
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
			return;
		}
		addPageMessage(respage.getString("no_have_correct_privilege_current_study") + "\n"
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET,
				resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int classId = fp.getInt("id");

		if (classId == 0) {

			addPageMessage(respage.getString("please_choose_a_subject_group_class_to_view"));
			forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);
		} else {
			StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
			StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
			SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
			StudyDAO studyDao = new StudyDAO(sm.getDataSource());

			StudyGroupClassBean sgcb = (StudyGroupClassBean) sgcdao.findByPK(classId);
			StudyBean study = (StudyBean) studyDao.findByPK(sgcb.getStudyId());

			checkRoleByUserAndStudy(ub, sgcb.getStudyId(), study.getParentStudyId());

			sgcb.setGroupClassTypeName(GroupClassType.get(sgcb.getGroupClassTypeId()).getName());

			ArrayList groups = sgdao.findAllByGroupClass(sgcb);
			ArrayList studyGroups = new ArrayList();

			for (int i = 0; i < groups.size(); i++) {
				StudyGroupBean sg = (StudyGroupBean) groups.get(i);
				ArrayList subjectMaps = sgmdao.findAllByStudyGroupClassAndGroup(sgcb.getId(), sg.getId());
				sg.setSubjectMaps(subjectMaps);
				studyGroups.add(sg);
			}

			request.setAttribute("group", sgcb);
			request.setAttribute("studyGroups", studyGroups);
			forwardPage(Page.VIEW_SUBJECT_GROUP_CLASS);
		}
	}
}
