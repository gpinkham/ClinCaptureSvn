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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import org.akaza.openclinica.bean.core.GroupClassType;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.dynamicevent.DynamicEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.core.SecureController;
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


/**
 * @author jxu, igor
 * 
 *         Servlet to create a new subject group class
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class CreateSubjectGroupClassServlet extends SecureController {
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		checkStudyLocked(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, respage.getString("current_study_locked"));
		checkStudyFrozen(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, respage.getString("current_study_frozen"));
		if (currentStudy.getParentStudyId() > 0) {
			addPageMessage(respage.getString("subject_group_class_only_added_top_level") + " "
					+ respage.getString("please_contact_sysadmin_questions"));
			throw new InsufficientPermissionException(Page.SUBJECT_GROUP_CLASS_LIST,
					resexception.getString("not_top_study"), "1");
		}

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
			return;
		}
		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET,
				resexception.getString("not_study_director"), "1");
	}

	@Override
	public void processRequest() throws Exception {
		String action = request.getParameter("action");

		if (StringUtil.isBlank(action)) {
			ArrayList studyGroups = new ArrayList();
			clearSession();
			
			session.setAttribute("groupTypes", GroupClassType.toArrayList());
			session.setAttribute("studyGroups", studyGroups);
			
			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
			EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
			ArrayList definitionsList = seddao.findAllByStudy(currentStudy);
			HashMap<StudyEventDefinitionBean, Boolean> definitions = new HashMap<StudyEventDefinitionBean, Boolean>();

			for (int i = 0; i < definitionsList.size(); i++) {
				StudyEventDefinitionBean def = (StudyEventDefinitionBean) definitionsList.get(i);
				ArrayList crfs = (ArrayList) edcdao.findAllActiveParentsByEventDefinitionId(def.getId());
				def.setCrfNum(crfs.size());
				definitions.put(def, false);
			}

			session.setAttribute("definitionsToView", definitions);
			session.setAttribute("defNum", definitions.size() + "");
			forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS);

		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				confirmGroup();
			} else if ("submit".equalsIgnoreCase(action)) {
				submitGroup();
			} else if ("back".equalsIgnoreCase(action)) {
				forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS);
			}
		}
	}

	/**
	 * Validates the first section of study inputs and save it into study bean
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void confirmGroup() throws Exception {
		boolean atLeastOneEventDefSelected = false;
		boolean isDefault = false;
		
		Validator v = new Validator(request);
		FormProcessor fp = new FormProcessor(request);
		
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("groupClassName", fp.getString("name"));
		fields.put("groupClassTypeId", fp.getString("groupClassTypeId"));
		fields.put("subjectAssignment", fp.getString("subjectAssignment"));
		fields.put("isDefault", fp.getString("isDefault"));
		
		session.setAttribute("fields", fields);

		v.addValidation("name", Validator.NO_BLANKS);
		StudyGroupClassDAO studyGroupClassDAO = new StudyGroupClassDAO(sm.getDataSource());
		ArrayList<StudyGroupClassBean> allStudyGroupClasses = (ArrayList<StudyGroupClassBean>) studyGroupClassDAO
				.findAll();

		v.addValidation("subjectAssignment", Validator.NO_BLANKS);

		v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				30);
		v.addValidation("subjectAssignment", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);

		errors = v.validate();
		
		ArrayList studyGroups = new ArrayList();
		if ("4".equals(request.getParameter("groupClassTypeId"))){ //dynamic group
			if ("yes".equals(request.getParameter("isDefault"))) {
				isDefault = true;
			}
			ArrayList<StudyEventDefinitionBean> listOfDefenitions = new ArrayList<StudyEventDefinitionBean>();
			HashMap<StudyEventDefinitionBean, Boolean> definitions = (HashMap)session.getAttribute("definitionsToView");
			for (Iterator keyIt = definitions.keySet().iterator(); keyIt.hasNext();) {
				StudyEventDefinitionBean def =(StudyEventDefinitionBean) keyIt.next();
				if ("yes".equals(request.getParameter("selected"+def.getId()))){
					definitions.put(def, true);
					atLeastOneEventDefSelected = true;
					listOfDefenitions.add(def);
				} else {
					definitions.put(def, false);
				}
			}
			session.setAttribute("listOfDefenitions", listOfDefenitions);
			session.setAttribute("definitionsToView", definitions);
		} else {  //not dynamic group
			atLeastOneEventDefSelected = true;
			StringBuilder rowsWithDuplicateNames = new StringBuilder("");
			Set<String> setOfNames = new HashSet<String>();
			for (int i = 0; i < 50; i++) {
				String name = fp.getString("studyGroup" + i);
				String description = fp.getString("studyGroupDescription" + i);
				if (!StringUtil.isBlank(name)) {
					StudyGroupBean sGroup = new StudyGroupBean();
					sGroup.setName(name);
					sGroup.setDescription(description);
					studyGroups.add(sGroup);
					if (name.length() > 255) {
						Validator.addError(errors, "studyGroupError", respage.getString("group_name_cannot_be_more_255"));
					}
					if (!setOfNames.add(name)) {
						rowsWithDuplicateNames.append(","+studyGroups.size());
						Validator.addError(errors, "studyGroupError", respage.getString("please_correct_the_duplicate_name_found_in_row")+" "+rowsWithDuplicateNames.substring(1));
					}
					if (description.length() > 1000) {
						Validator.addError(errors, "studyGroupError", respage.getString("group_description_cannot_be_more_100"));
					}
				}
			}
			session.setAttribute("studyGroups", studyGroups);
		}
		
		for (StudyGroupClassBean thisBean : allStudyGroupClasses) {
			if (fp.getString("name").trim().equals(thisBean.getName())) {
				Validator.addError(errors, "name", resexception.getString("group_class_name_used_choose_unique"));
			}
		}
		if (fp.getInt("groupClassTypeId") == 0) {
			Validator.addError(errors, "groupClassTypeId", resexception.getString("group_class_type_is_required"));
		}
		if (!atLeastOneEventDefSelected) {
			Validator.addError(errors, "dynamicEvents", resexception.getString("at_least_one_element_should_be_selected"));
		}
		
		if (errors.isEmpty()) {
			logger.info("no errors in the first section");
			StudyGroupClassBean group = new StudyGroupClassBean();
			group.setName(fp.getString("name"));
			group.setGroupClassTypeId(fp.getInt("groupClassTypeId"));
			group.setDefault(isDefault);
			if (!"4".equals(request.getParameter("groupClassTypeId"))){ //dynamic group
				group.setSubjectAssignment(fp.getString("subjectAssignment"));
			}
			group.setGroupClassTypeName(GroupClassType.get(group.getGroupClassTypeId()).getName());
			session.setAttribute("group", group);
			
			forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS_CONFIRM);

		} else {
			logger.info("has validation errors in the first section");
			request.setAttribute("formMessages", errors);

			forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS);
		}
	}

	/**
	 * Saves study group information into database
	 * 
	 * @throws OpenClinicaException
	 */
	private void submitGroup() throws OpenClinicaException, IOException {
		StudyGroupClassBean group = (StudyGroupClassBean) session.getAttribute("group");
		StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
		group.setStudyId(currentStudy.getId());
		group.setOwner(ub);
		group.setStatus(Status.AVAILABLE);
		
		group = (StudyGroupClassBean) sgcdao.create(group);
		
		if (!group.isActive()) {
			addPageMessage(respage.getString("the_subject_group_class_not_created_database"));
		} else {
			if (group.getGroupClassTypeId() == 4){
				ArrayList<StudyEventDefinitionBean> listOfDefenitions = (ArrayList)session.getAttribute("listOfDefenitions");
				ArrayList<StudyEventDefinitionBean> listOfOrderedDefenitions = new ArrayList(listOfDefenitions);
				
				//read order from submit-page and create ordered list
				for ( StudyEventDefinitionBean def: listOfDefenitions) {
					int index = Integer.valueOf(request.getParameter("event" + def.getId()));
					listOfOrderedDefenitions.set(index-1, def);
				}	
				//create DynamicEventBeans and write to DB
				DynamicEventDao dedao = new DynamicEventDao(sm.getDataSource());
				for ( int i=0; i<listOfOrderedDefenitions.size(); i++) {
					StudyEventDefinitionBean def = listOfOrderedDefenitions.get(i);
					DynamicEventBean de = new DynamicEventBean();
					de.setStudyGroupClassId(group.getId());
					de.setStudyEventDefinitionId(def.getId());
					de.setStudyId(currentStudy.getId());
					de.setOrdinal(i);
					dedao.create(de);
				}
			} else {
				ArrayList studyGroups = (ArrayList) session.getAttribute("studyGroups");
				StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
				for (int i = 0; i < studyGroups.size(); i++) {
					StudyGroupBean sg = (StudyGroupBean) studyGroups.get(i);
					sg.setStudyGroupClassId(group.getId());
					sg.setOwner(ub);
					sg.setStatus(Status.AVAILABLE);
					sgdao.create(sg);
				}
			}	
			addPageMessage(respage.getString("the_subject_group_class_created_succesfully"));
		}
		ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
		session.setAttribute("pageMessages", pageMessages);
		
		clearSession();
		
		forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);
	}
	
	private void clearSession() {
		session.removeAttribute("listOfDefenitions");
		session.removeAttribute("definitionsToView");
		session.removeAttribute("fields");
		session.removeAttribute("defNum");
		session.removeAttribute("group");
		session.removeAttribute("studyGroups");
		session.removeAttribute("groupTypes");
	}
}
