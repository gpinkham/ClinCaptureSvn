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
package org.akaza.openclinica.control.login;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.admin.EventStatusStatisticsTableFactory;
import org.akaza.openclinica.control.admin.SiteStatisticsTableFactory;
import org.akaza.openclinica.control.admin.StudyStatisticsTableFactory;
import org.akaza.openclinica.control.admin.StudySubjectStatusStatisticsTableFactory;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.control.submit.ListStudySubjectTableFactory;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.util.StudyParameterPriorityUtil;
import com.clinovo.util.ValidatorHelper;

/**
 * Processes the request of changing current study.
 *
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class ChangeStudyServlet extends Controller {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		String action = request.getParameter("action");
		UserAccountDAO udao = getUserAccountDAO();
		StudyDAO sdao = getStudyDAO();

		ArrayList<StudyUserRoleBean> studies = udao.findStudyByUser(ub, (ArrayList) sdao.findAllNotRemoved());
		request.setAttribute("roleMap", Role.ROLE_MAP);
		if (request.getAttribute("label") != null) {
			String label = (String) request.getAttribute("label");
			if (label.length() > 0) {
				request.setAttribute("label", label);
			}
		}

		ArrayList validStudies = new ArrayList();
		for (StudyUserRoleBean sr : studies) {
			StudyBean study = (StudyBean) sdao.findByPK(sr.getStudyId());
			if (study != null && study.getStatus().equals(Status.PENDING)) {
				sr.setStatus(study.getStatus());
			}
			validStudies.add(sr);
		}

		if (StringUtil.isBlank(action)) {
			request.setAttribute("studies", validStudies);
			forwardPage(Page.CHANGE_STUDY, request, response);
		} else {
			logger.info("confirm action");
			changeStudy(request, response, studies);
		} 
	}

	private void changeStudy(HttpServletRequest request, HttpServletResponse response, ArrayList<StudyUserRoleBean> studies) throws Exception {
		
		StudyBean currentStudy = getCurrentStudy(request);
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);
		v.addValidation("studyId", Validator.IS_AN_INTEGER);
		
		HashMap errors = v.validate();
		if (!errors.isEmpty()) {
			request.setAttribute("studies", studies);
			forwardPage(Page.CHANGE_STUDY, request, response);
			return;
		}

		int studyId = fp.getInt("studyId");
		logger.info("new study id: " + studyId);
		
		boolean isStudySelected = false;
		for (StudyUserRoleBean studyWithRole : studies) {
			if (studyWithRole.getStudyId() == studyId) {
				if (studyWithRole.getParentStudyId() > 0) {
					StudyDAO studyDAO = getStudyDAO();
					StudyBean parentStudy = (StudyBean) studyDAO.findByPK(studyWithRole.getParentStudyId());
					request.setAttribute("parentStudyName", parentStudy.getName());
				}
				request.setAttribute("studyId", studyId);
				request.getSession().setAttribute("studyWithRole", studyWithRole);
				request.setAttribute("currentStudy", currentStudy);
				isStudySelected = true;
			}
		}
		
		if (!isStudySelected) {
			addPageMessage(restext.getString("no_study_selected"), request);
			forwardPage(Page.CHANGE_STUDY, request, response);
			return;
		}
		
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);
		int prevStudyId = currentStudy.getId();

		ub.updateSysAdminRole(studyId, prevStudyId);

		StudyDAO sdao = getStudyDAO();
		StudyBean current = (StudyBean) sdao.findByPK(studyId);
		StudyBean parent = current;
		StudyParameterValueDAO spvdao = getStudyParameterValueDAO();

		ArrayList studyParameters = spvdao.findParamConfigByStudy(current);
		current.setStudyParameters(studyParameters);
		int parentStudyId = currentStudy.getParentStudyId() > 0 ? currentStudy.getParentStudyId() : currentStudy
				.getId();
		StudyParameterValueBean parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectIdGeneration");
		current.getStudyParameterConfig().setSubjectIdGeneration(parentSPV.getValue());
		String idSetting = current.getStudyParameterConfig().getSubjectIdGeneration();
		if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
			int nextLabel = this.getStudySubjectDAO().findTheGreatestLabel() + 1;
			request.setAttribute("label", Integer.toString(nextLabel));
		}

		StudyConfigService scs = getStudyConfigService();
		if (current.getParentStudyId() <= 0) {
			scs.setParametersForStudy(current);

		} else {
			if (current.getParentStudyId() > 0) {
				parent = (StudyBean) sdao.findByPK(current.getParentStudyId());
				current.setParentStudyName(parent.getName());
				current.setParentStudyOid(parent.getOid());
			}
			scs.setParametersForSite(current);
		}
		if (current.getStatus().equals(Status.DELETED) || current.getStatus().equals(Status.AUTO_DELETED)) {
			request.getSession().removeAttribute("studyWithRole");
			addPageMessage(restext.getString("study_choosed_removed_restore_first"), request);
		} else {
			request.getSession().setAttribute(STUDY, current);
			request.getSession().setAttribute(PARENT_STUDY, parent);
			currentStudy = current;
			UserAccountDAO udao = getUserAccountDAO();
			ub.setActiveStudyId(current.getId());
			ub.setUpdater(ub);
			ub.setUpdatedDate(new java.util.Date());
			udao.update(ub);

			int currentStudyId = currentStudy.getParentStudyId() > 0 ? currentStudy.getParentStudyId() : currentStudy
					.getId();
			boolean isEvaluationEnabled = StudyParameterPriorityUtil.isParameterEnabled(EVALUATION_ENABLED,
					currentStudyId, getSystemDAO(), getStudyParameterValueDAO(), getStudyDAO());
			request.getSession().setAttribute(EVALUATION_ENABLED, isEvaluationEnabled);

			currentRole = (StudyUserRoleBean) request.getSession().getAttribute("studyWithRole");
			if (currentRole == null) {
				response.sendRedirect(request.getContextPath() + "/ChangeStudy");
				return;
			}
			request.getSession().setAttribute("userRole", currentRole);
			request.getSession().removeAttribute("studyWithRole");
			addPageMessage(restext.getString("current_study_changed_succesfully"), request);
		}
		ub.incNumVisitsToMainMenu();
		if (prevStudyId != studyId) {
			request.getSession().removeAttribute("eventsForCreateDataset");
			request.getSession().setAttribute("tableFacadeRestore", "false");
		}
		request.setAttribute("studyJustChanged", "yes");
		Integer assignedDiscrepancies = getDiscrepancyNoteDAO().getViewNotesCountWithFilter(
				" AND dn.assigned_user_id =" + ub.getId()
						+ " AND (dn.resolution_status_id=1 OR dn.resolution_status_id=2 OR dn.resolution_status_id=3)",
				currentStudy, ub.getId());
		request.setAttribute("assignedDiscrepancies", assignedDiscrepancies == null ? 0 : assignedDiscrepancies);

		if (currentRole.isInvestigator() || currentRole.isClinicalResearchCoordinator()) {
			setupListStudySubjectTable(request, response);
		}
		if (currentRole.isMonitor()) {
			setupSubjectSDVTable(request);
		} else if (currentRole.isStudyAdministrator() || currentRole.isStudyDirector()) {
			if (currentStudy.getStatus().isPending()) {
				response.sendRedirect(request.getContextPath() + Page.MANAGE_STUDY_MODULE);
				return;
			}
			setupStudySiteStatisticsTable(request, response);
			setupSubjectEventStatusStatisticsTable(request, response);
			setupStudySubjectStatusStatisticsTable(request, response);
			if (currentStudy.getParentStudyId() == 0) {
				setupStudyStatisticsTable(request, response);
			}
		}
		response.sendRedirect("/" + getContextPath(request) + Page.MENU_SERVLET.getFileName());
	}

	private void setupSubjectSDVTable(HttpServletRequest request) {
		StudyBean currentStudy = getCurrentStudy(request);
		request.setAttribute("studyId", currentStudy.getId());
		String sdvMatrix = getSDVUtil().renderEventCRFTableWithLimit(request, currentStudy.getId(), "");
		request.setAttribute("sdvMatrix", sdvMatrix);
	}

	private void setupStudySubjectStatusStatisticsTable(HttpServletRequest request, HttpServletResponse response) {
		StudyBean currentStudy = getCurrentStudy(request);
		StudySubjectStatusStatisticsTableFactory factory = new StudySubjectStatusStatisticsTableFactory();
		factory.setStudySubjectDao(getStudySubjectDAO());
		factory.setCurrentStudy(currentStudy);
		factory.setStudyDao(getStudyDAO());
		String studySubjectStatusStatistics = factory.createTable(request, response).render();
		request.setAttribute("studySubjectStatusStatistics", studySubjectStatusStatistics);
	}

	private void setupSubjectEventStatusStatisticsTable(HttpServletRequest request, HttpServletResponse response) {
		StudyBean currentStudy = getCurrentStudy(request);
		EventStatusStatisticsTableFactory factory = new EventStatusStatisticsTableFactory();
		factory.setStudySubjectDao(getStudySubjectDAO());
		factory.setCurrentStudy(currentStudy);
		factory.setStudyEventDao(getStudyEventDAO());
		factory.setStudyDao(getStudyDAO());
		String subjectEventStatusStatistics = factory.createTable(request, response).render();
		request.setAttribute("subjectEventStatusStatistics", subjectEventStatusStatistics);
	}

	private void setupStudySiteStatisticsTable(HttpServletRequest request, HttpServletResponse response) {
		StudyBean currentStudy = getCurrentStudy(request);
		SiteStatisticsTableFactory factory = new SiteStatisticsTableFactory();
		factory.setStudySubjectDao(getStudySubjectDAO());
		factory.setCurrentStudy(currentStudy);
		factory.setStudyDao(getStudyDAO());
		String studySiteStatistics = factory.createTable(request, response).render();
		request.setAttribute("studySiteStatistics", studySiteStatistics);
	}

	private void setupStudyStatisticsTable(HttpServletRequest request, HttpServletResponse response) {
		StudyBean currentStudy = getCurrentStudy(request);
		StudyStatisticsTableFactory factory = new StudyStatisticsTableFactory();
		factory.setStudySubjectDao(getStudySubjectDAO());
		factory.setCurrentStudy(currentStudy);
		factory.setStudyDao(getStudyDAO());
		String studyStatistics = factory.createTable(request, response).render();
		request.setAttribute("studyStatistics", studyStatistics);
	}

	private void setupListStudySubjectTable(HttpServletRequest request, HttpServletResponse response) {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);
		ListStudySubjectTableFactory factory = new ListStudySubjectTableFactory(true);
		factory.setStudyEventDefinitionDao(getStudyEventDefinitionDAO());
		factory.setSubjectDAO(getSubjectDAO());
		factory.setStudySubjectDAO(getStudySubjectDAO());
		factory.setStudyEventDAO(getStudyEventDAO());
		factory.setStudyBean(currentStudy);
		factory.setStudyGroupClassDAO(getStudyGroupClassDAO());
		factory.setSubjectGroupMapDAO(getSubjectGroupMapDAO());
		factory.setStudyDAO(getStudyDAO());
		factory.setCrfVersionDAO(getCRFVersionDAO());
		factory.setCurrentRole(currentRole);
		factory.setCurrentUser(ub);
		factory.setEventCRFDAO(getEventCRFDAO());
		factory.setEventDefintionCRFDAO(getEventDefinitionCRFDAO());
		factory.setDiscrepancyNoteDAO(getDiscrepancyNoteDAO());
		factory.setStudyGroupDAO(getStudyGroupDAO());
		factory.setDynamicEventDao(getDynamicEventDao());
		String findSubjectsHtml = factory.createTable(request, response).render();
		request.setAttribute("findSubjectsHtml", findSubjectsHtml);
	}
}
