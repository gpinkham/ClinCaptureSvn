/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

import org.akaza.openclinica.bean.admin.DisplayStudyBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Assigns a study subject to another study.
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class ReassignStudySubjectServlet extends Controller {
	/**
	 * Permits access to reassigning subject.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws InsufficientPermissionException                  
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_locked"), request, response);
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_frozen"), request, response);
		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_study_director"), "1");

	}

	/**
	 * Process user's request for reassigning subject to another site.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws Exception
	 * 				Exception
	 */
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		String action = request.getParameter("action");
		StudyDAO sdao = getStudyDAO();
		StudySubjectDAO ssdao = getStudySubjectDAO();
		SubjectDAO subdao = getSubjectDAO();
		FormProcessor fp = new FormProcessor(request);

		int studySubId = fp.getInt("id");
		if (studySubId == 0) {
			addPageMessage(getResPage().getString("please_choose_a_subject_to_reassign"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
		} else {
			StudySubjectBean studySub = (StudySubjectBean) ssdao.findByPK(studySubId);
			int subjectId = studySub.getSubjectId();
			request.setAttribute("studySub", studySub);
			SubjectBean subject = (SubjectBean) subdao.findByPK(subjectId);
			request.setAttribute("subject", subject);

			DisplayStudyBean displayStudy = getDisplayStudy(sdao, studySub);
			if (StringUtil.isBlank(action)) {
				request.setAttribute("displayStudy", displayStudy);
				forwardPage(Page.REASSIGN_STUDY_SUBJECT, request, response);
			} else {
				int studyId = fp.getInt("studyId");
				if (studyId == 0) {
					addPageMessage(getResPage().getString("please_choose_a_study_site_to_reassign_the_subject"), request);
					forwardPage(Page.REASSIGN_STUDY_SUBJECT, request, response);
					return;
				}
				StudyBean st = (StudyBean) sdao.findByPK(studyId);

				if ("confirm".equalsIgnoreCase(action)) {
					StudySubjectBean sub1 = (StudySubjectBean) ssdao.findAnotherBySameLabel(studySub.getLabel(),
							studyId, studySub.getId());
					if (sub1.getId() > 0) {
						addPageMessage(getResPage().getString("the_study_subject_ID_used_by_another_in_study_site"), request);
						forwardPage(Page.REASSIGN_STUDY_SUBJECT, request, response);
						return;
					}

					request.setAttribute("newStudy", st);
					forwardPage(Page.REASSIGN_STUDY_SUBJECT_CONFIRM, request, response);
				} else if ("back".equalsIgnoreCase(action)) {
					request.setAttribute("displayStudy", displayStudy);
					if (studySub.getStudyId() != studyId) {
						studySub.setStudyId(studyId);
						request.setAttribute("isDataChanged", true);
					}
					request.setAttribute("studySub", studySub);
					forwardPage(Page.REASSIGN_STUDY_SUBJECT, request, response);
				} else {
					addPageMessage(checkAndUpdateSubject(studyId, studySub, displayStudy, ub, ssdao, st), request);
					forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
				}
			}
		}
	}

	private DisplayStudyBean getDisplayStudy(StudyDAO sdao, StudySubjectBean studySub) {
		ArrayList studies;
		DisplayStudyBean displayStudy = new DisplayStudyBean();
		StudyBean study = (StudyBean) sdao.findByPK(studySub.getStudyId());
		displayStudy.setStatus(study.getStatus());
		if (study.getParentStudyId() > 0) {
			// current in site
			studies = (ArrayList) sdao.findAllByParent(study.getParentStudyId());
			StudyBean parent = (StudyBean) sdao.findByPK(study.getParentStudyId());
			displayStudy.setParent(parent);
			displayStudy.setChildren(studies);
		} else {
			studies = (ArrayList) sdao.findAllByParent(study.getId());
			displayStudy.setParent(study);
			displayStudy.setChildren(studies);
		}
		return displayStudy;
	}

	private String checkAndUpdateSubject(int newStudyId, StudySubjectBean studySub, DisplayStudyBean displayStudy,
			UserAccountBean ub, StudySubjectDAO ssdao, StudyBean st) {
		SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(getDataSource());
		ArrayList<SubjectGroupMapBean> groupMaps = (ArrayList<SubjectGroupMapBean>) sgmdao
				.findAllByStudySubject(studySub.getId());
		MessageFormat mf = new MessageFormat("");
		for (StudyBean study : (ArrayList<StudyBean>) displayStudy.getChildren()) {
			if (study.getId() == newStudyId) {
				logger.info("submit to reassign the subject");
				studySub.setUpdatedDate(new Date());
				studySub.setUpdater(ub);
				studySub.setStudyId(newStudyId);
				ssdao.update(studySub);

				for (SubjectGroupMapBean sgm : groupMaps) {
					sgm.setUpdatedDate(new Date());
					sgm.setUpdater(ub);
					sgm.setStatus(Status.DELETED);
					sgmdao.update(sgm);
				}

				mf.applyPattern(getResPage().getString("subject_reassigned"));
				Object[] arguments = { studySub.getLabel(), st.getName() };

				return mf.format(arguments);
			}
		}
		mf.applyPattern(getResPage().getString("subject_not_reassigned"));
		Object[] arguments = { studySub.getLabel() };

		return mf.format(arguments);
	}
}
