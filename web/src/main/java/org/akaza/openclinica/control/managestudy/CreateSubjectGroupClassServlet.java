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

import com.clinovo.util.ValidatorHelper;
import org.akaza.openclinica.bean.core.GroupClassType;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.dynamicevent.DynamicEventBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author jxu, igor
 * 
 *         Servlet to create a new subject group class
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
public class CreateSubjectGroupClassServlet extends SpringServlet {
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, getResPage().getString("current_study_locked"), request,
				response);
		checkStudyFrozen(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, getResPage().getString("current_study_frozen"), request,
				response);
		if (currentStudy.getParentStudyId() > 0) {
			addPageMessage(
					getResPage().getString("subject_group_class_only_added_top_level") + " "
							+ getResPage().getString("please_contact_sysadmin_questions"), request);
			throw new InsufficientPermissionException(Page.SUBJECT_GROUP_CLASS_LIST,
					getResException().getString("not_top_study"), "1");
		}

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
		StudyBean currentStudy = getCurrentStudy(request);

		String action = request.getParameter("action");

		if (StringUtil.isBlank(action)) {
			ArrayList studyGroups = new ArrayList();
			StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
			StudyGroupClassBean defaultStudyGroupClass = sgcdao.findDefaultByStudyId(currentStudy.getId());

			clearSession(request);

			request.getSession().setAttribute("groupTypes", GroupClassType.toArrayList());
			request.getSession().setAttribute("studyGroups", studyGroups);
			request.getSession().setAttribute("defaultGroupAlreadyExists", defaultStudyGroupClass.getId() > 0);

			StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
			EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
			ArrayList allDefsFromStudy = seddao.findAllActiveNotClassGroupedByStudyId(currentStudy.getId());
			HashMap<StudyEventDefinitionBean, Boolean> definitions = new HashMap<StudyEventDefinitionBean, Boolean>();

			for (Object anAllDefsFromStudy : allDefsFromStudy) {
				StudyEventDefinitionBean def = (StudyEventDefinitionBean) anAllDefsFromStudy;
				ArrayList crfs = (ArrayList) edcdao.findAllActiveParentsByEventDefinitionId(def.getId());
				def.setCrfNum(crfs.size());
				if (def.getStatus().isAvailable()) {
					definitions.put(def, false);
				}
			}

			request.getSession().setAttribute("definitionsToView", definitions);
			forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS, request, response);

		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				confirmGroup(request, response);
			} else if ("submit".equalsIgnoreCase(action)) {
				submitGroup(request, response);
			} else if ("back".equalsIgnoreCase(action)) {
				forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS, request, response);
			}
		}
	}

	/**
	 * Validates the first section of study inputs and save it into study bean
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws Exception
	 */
	private void confirmGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean atLeastOneEventDefSelected = false;
		boolean isDefault = false;

		StudyBean currentStudy = getCurrentStudy(request);
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);

		Map<String, String> fields = new HashMap<String, String>();
		fields.put("groupClassName", fp.getString("name").trim());
		fields.put("groupClassTypeId", fp.getString("groupClassTypeId"));
		fields.put("subjectAssignment", fp.getString("subjectAssignment"));
		fields.put("isDefault", fp.getString("isDefault"));

		request.getSession().setAttribute("fields", fields);

		v.addValidation("name", Validator.NO_BLANKS);
		StudyGroupClassDAO studyGroupClassDAO = getStudyGroupClassDAO();
		ArrayList<StudyGroupClassBean> allStudyGroupClasses = studyGroupClassDAO.findAllByStudy(currentStudy);

		v.addValidation("subjectAssignment", Validator.NO_BLANKS);

		v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				30);
		v.addValidation("subjectAssignment", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);

		HashMap errors = v.validate();

		ArrayList studyGroups = new ArrayList();
		if (String.valueOf(GroupClassType.DYNAMIC.getId()).equals(request.getParameter("groupClassTypeId"))) { // dynamic
																												// group
			if ("true".equals(request.getParameter("isDefault"))) {
				isDefault = true;
			}
			ArrayList<StudyEventDefinitionBean> listOfDefinitions = new ArrayList<StudyEventDefinitionBean>();
			HashMap<StudyEventDefinitionBean, Boolean> definitions = (HashMap) request.getSession().getAttribute(
					"definitionsToView");
			for (StudyEventDefinitionBean def : definitions.keySet()) {
				if ("yes".equals(request.getParameter("selected" + def.getId()))) {
					definitions.put(def, true);
					atLeastOneEventDefSelected = true;
					listOfDefinitions.add(def);
				} else {
					definitions.put(def, false);
				}
			}
			request.getSession().setAttribute("listOfDefinitions", listOfDefinitions);
			request.getSession().setAttribute("definitionsToView", definitions);
		} else { // not dynamic group
			atLeastOneEventDefSelected = true;
			StringBuilder rowsWithDuplicateNames = new StringBuilder("");
			Set<String> setOfNames = new HashSet<String>();
			for (int i = 0; i < 50; i++) {
				String name = fp.getString("studyGroup" + i).trim();
				String description = fp.getString("studyGroupDescription" + i);
				if (!StringUtil.isBlank(name)) {
					StudyGroupBean sGroup = new StudyGroupBean();
					sGroup.setName(name);
					sGroup.setDescription(description);
					studyGroups.add(sGroup);
					if (name.length() > 255) {
						Validator.addError(errors, "studyGroupError",
								getResPage().getString("group_name_cannot_be_more_255"));
					}
					if (!setOfNames.add(name)) {
						rowsWithDuplicateNames.append(",").append(studyGroups.size());
						Validator.addError(errors, "studyGroupError",
								getResPage().getString("please_correct_the_duplicate_name_found_in_row") + " "
										+ rowsWithDuplicateNames.substring(1));
					}
					if (description.length() > 1000) {
						Validator.addError(errors, "studyGroupError",
								getResPage().getString("group_description_cannot_be_more_100"));
					}
				}
			}
			request.getSession().setAttribute("studyGroups", studyGroups);
		}

		for (StudyGroupClassBean thisBean : allStudyGroupClasses) {
			if (fp.getString("name").trim().equals(thisBean.getName().trim())) {
				Validator.addError(errors, "name", getResException().getString("group_class_name_used_choose_unique"));
			}
		}
		if (fp.getInt("groupClassTypeId") == 0) {
			Validator.addError(errors, "groupClassTypeId", getResException().getString("group_class_type_is_required"));
		}
		if (!atLeastOneEventDefSelected) {
			Validator.addError(errors, "dynamicEvents",
					getResException().getString("at_least_one_element_should_be_selected"));
		}

		if (errors.isEmpty()) {
			logger.info("no errors in the first section");
			StudyGroupClassBean group = new StudyGroupClassBean();
			group.setName(fp.getString("name").trim());
			group.setGroupClassTypeId(fp.getInt("groupClassTypeId"));
			group.setDefault(isDefault);
			if (!String.valueOf(GroupClassType.DYNAMIC.getId()).equals(request.getParameter("groupClassTypeId"))) { // dynamic
																													// group
				group.setSubjectAssignment(fp.getString("subjectAssignment"));
			}
			group.setGroupClassTypeName(GroupClassType.get(group.getGroupClassTypeId()).getName());
			request.getSession().setAttribute("group", group);

			forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS_CONFIRM, request, response);

		} else {
			logger.info("has validation errors in the first section");
			request.setAttribute("formMessages", errors);

			forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS, request, response);
		}
	}

	/**
	 * Saves study group information into database
	 * 
	 * @throws OpenClinicaException
	 */
	private void submitGroup(HttpServletRequest request, HttpServletResponse response) throws OpenClinicaException,
			IOException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		StudyGroupClassBean group = (StudyGroupClassBean) request.getSession().getAttribute("group");
		StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
		group.setStudyId(currentStudy.getId());
		group.setOwner(ub);
		group.setStatus(Status.AVAILABLE);
		group.setDynamicOrdinal(sgcdao.getMaxDynamicOrdinalByStudyId(currentStudy.getId()) + 1);

		group = sgcdao.create(group);

		if (!group.isActive()) {
			addPageMessage(getResPage().getString("the_subject_group_class_not_created_database"), request);
		} else {
			if (group.getGroupClassTypeId() == GroupClassType.DYNAMIC.getId()) {
				ArrayList<StudyEventDefinitionBean> listOfDefinitions = (ArrayList) request.getSession().getAttribute(
						"listOfDefinitions");
				ArrayList<StudyEventDefinitionBean> listOfOrderedDefinitions = new ArrayList(listOfDefinitions);

				// read order from submit-page and create ordered list
				for (StudyEventDefinitionBean def : listOfDefinitions) {
					int index = Integer.valueOf(request.getParameter("event" + def.getId()));
					listOfOrderedDefinitions.set(index - 1, def);
				}
				// create DynamicEventBeans and write to DB
				DynamicEventDao dedao = getDynamicEventDao();
				for (int i = 0; i < listOfOrderedDefinitions.size(); i++) {
					StudyEventDefinitionBean def = listOfOrderedDefinitions.get(i);
					DynamicEventBean de = new DynamicEventBean();
					de.setStudyGroupClassId(group.getId());
					de.setStudyEventDefinitionId(def.getId());
					de.setStudyId(currentStudy.getId());
					de.setOrdinal(i);
					de.setOwner(ub);
					dedao.create(de);
				}
			} else {
				ArrayList studyGroups = (ArrayList) request.getSession().getAttribute("studyGroups");
				StudyGroupDAO sgdao = getStudyGroupDAO();
				for (Object studyGroup : studyGroups) {
					StudyGroupBean sg = (StudyGroupBean) studyGroup;
					sg.setStudyGroupClassId(group.getId());
					sg.setOwner(ub);
					sg.setStatus(Status.AVAILABLE);
					sgdao.create(sg);
				}
			}
			addPageMessage(getResPage().getString("the_subject_group_class_created_succesfully"), request);
		}
		ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
		request.setAttribute("pageMessages", pageMessages);

		clearSession(request);

		forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, request, response);
	}

	private void clearSession(HttpServletRequest request) {
		request.getSession().removeAttribute("listOfDefinitions");
		request.getSession().removeAttribute("definitionsToView");
		request.getSession().removeAttribute("fields");
		request.getSession().removeAttribute("group");
		request.getSession().removeAttribute("studyGroups");
		request.getSession().removeAttribute("groupTypes");
		request.getSession().removeAttribute("defaultGroupAlreadyExists");
	}
}
