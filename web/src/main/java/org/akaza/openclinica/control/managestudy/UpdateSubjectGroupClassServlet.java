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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class UpdateSubjectGroupClassServlet extends SecureController {
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		checkStudyLocked(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, respage.getString("current_study_locked"));
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
		String action = request.getParameter("action");
		
		if (classId == 0) {
			addPageMessage(respage.getString("please_choose_a_subject_group_class_to_edit"));
			forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);
		} else {
			if (StringUtil.isBlank(action)) {
				if (request.getParameter("name") == null){
					clearSession();
					StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
					StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
			
					StudyGroupClassBean oldGroup = (StudyGroupClassBean) sgcdao.findByPK(classId);
					Map<String, String> fields = new HashMap<String, String>();
				
					fields.put("groupClassName", oldGroup.getName());
					fields.put("groupClassTypeId", String.valueOf(oldGroup.getGroupClassTypeId()));
					fields.put("subjectAssignment", oldGroup.getSubjectAssignment());
					fields.put("isDefault", String.valueOf(oldGroup.isDefault()));
				
					session.setAttribute("fields", fields);
					
					ArrayList defaultStudyGroupClasses = sgcdao.findAllDefault();
					session.setAttribute("defaultGroupAlreadyExists", !defaultStudyGroupClasses.isEmpty());
				
					StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
					EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
					//if you want to see all event definitions from study use next line:
					//ArrayList allDefsFromStudy = seddao.findAllByStudy(currentStudy);
					ArrayList allDefsFromStudy = seddao.findAllActiveOrderedByStudyGroupClassId(classId);
					allDefsFromStudy.addAll(seddao.findAllActiveNotClassGroupedByStudyId(currentStudy.getId()));
					
					HashMap<StudyEventDefinitionBean, Boolean> definitions = new HashMap<StudyEventDefinitionBean, Boolean>();
				
					//create treemap<order,StudyEventDefinitionId> and hashmap<StudyEventDefinitionId,order>
					DynamicEventDao dynevdao = new DynamicEventDao(sm.getDataSource());
					ArrayList dynEvents = (ArrayList)dynevdao.findAllByStudyGroupClassId(oldGroup.getId());
					TreeMap<Integer,Integer> ordinalToStudyEventDefinitionId = new TreeMap<Integer,Integer>();
					HashMap<Integer,Integer> studyEventDefinitionIdToOrdinal = new HashMap<Integer,Integer>();
					HashMap<Integer,Integer> studyEventDefinitionIdToPK = new HashMap<Integer,Integer>();
					for (int i = 0; i < dynEvents.size(); i++) {
						DynamicEventBean dynEventBean = (DynamicEventBean) dynEvents.get(i);
						ordinalToStudyEventDefinitionId.put(dynEventBean.getOrdinal(), dynEventBean.getStudyEventDefinitionId());
						studyEventDefinitionIdToOrdinal.put(dynEventBean.getStudyEventDefinitionId(), dynEventBean.getOrdinal());
						studyEventDefinitionIdToPK.put(dynEventBean.getStudyEventDefinitionId(), dynEventBean.getId());
					}
					//create hashmap<StudyEventDefinitionId,StudyEventDefinition>
					HashMap<Integer, StudyEventDefinitionBean> idToStudyEventDefinition = new HashMap<Integer, StudyEventDefinitionBean>();
					for (int i = 0; i < allDefsFromStudy.size(); i++) {
						StudyEventDefinitionBean def = (StudyEventDefinitionBean) allDefsFromStudy.get(i);
						if (ordinalToStudyEventDefinitionId.values().contains(def.getId())){
							idToStudyEventDefinition.put(def.getId(), def);
						}
					}
					
					for (int i = 0; i < allDefsFromStudy.size(); i++) {
						StudyEventDefinitionBean def = (StudyEventDefinitionBean) allDefsFromStudy.get(i);
						if (def.getStatus().isAvailable()){
							ArrayList crfs = (ArrayList) edcdao.findAllActiveParentsByEventDefinitionId(def.getId());
							def.setCrfNum(crfs.size());
							if (ordinalToStudyEventDefinitionId.containsValue(def.getId())){
								definitions.put(def, true);
							} else {
								definitions.put(def, false);
							}
						}	
					}
					session.setAttribute("definitionsToView", definitions);
					
					session.setAttribute("studyEventDefinitionIdToOrdinal", studyEventDefinitionIdToOrdinal);
					session.setAttribute("ordinalToStudyEventDefinitionId", ordinalToStudyEventDefinitionId);
					session.setAttribute("studyEventDefinitionIdToPK", studyEventDefinitionIdToPK);
					session.setAttribute("idToStudyEventDefinition", idToStudyEventDefinition);
									
					ArrayList studyGroups = sgdao.findAllByGroupClass(oldGroup);
					session.setAttribute("studyGroups", studyGroups);
					
					session.setAttribute("groupTypes", GroupClassType.toArrayList());
					session.setAttribute("oldGroup", oldGroup);
				}
				forwardPage(Page.UPDATE_SUBJECT_GROUP_CLASS);
			} else {
				if ("confirm".equalsIgnoreCase(action)) {
					confirmGroup();
				} else if ("submit".equalsIgnoreCase(action)) {
					submitGroup();
				} else if ("back".equalsIgnoreCase(action)) {
					forwardPage(Page.UPDATE_SUBJECT_GROUP_CLASS);
				}
			}
		}		
	}
	
	/**
	 * Validates the first section of study and save it into study bean
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void confirmGroup() throws Exception {
		boolean atLeastOneEventDefSelected = false;
		boolean isDefault = false;
		StudyGroupClassBean oldGroup = (StudyGroupClassBean) session.getAttribute("oldGroup");
		
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
		ArrayList<StudyGroupClassBean> allStudyGroupClasses = (ArrayList<StudyGroupClassBean>) studyGroupClassDAO.findAll();

		v.addValidation("subjectAssignment", Validator.NO_BLANKS);

		v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);
		v.addValidation("subjectAssignment", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);

		errors = v.validate();
		
		ArrayList studyGroups = new ArrayList();
		if (String.valueOf(GroupClassType.DYNAMIC.getId()).equals(request.getParameter("groupClassTypeId"))){ //dynamic group
			if ("true".equals(request.getParameter("isDefault"))) {
				isDefault = true;
			}
			ArrayList<StudyEventDefinitionBean> sortedListOfDefinitions = new ArrayList<StudyEventDefinitionBean>();
			HashMap<StudyEventDefinitionBean, Boolean> definitions = (HashMap)session.getAttribute("definitionsToView");
			//list of Study Event Defs that aren't contained in DB
			ArrayList<StudyEventDefinitionBean> listOfNewEventDefs = new ArrayList<StudyEventDefinitionBean>();
			TreeMap<Integer,Integer> ordinalToStudyEventDefinitionId = (TreeMap<Integer, Integer>) session.getAttribute("ordinalToStudyEventDefinitionId");
			HashMap<Integer, Integer> studyEventDefinitionIdToOrdinal = new HashMap<Integer,Integer>((HashMap<Integer, Integer>) session.getAttribute("studyEventDefinitionIdToOrdinal"));
			HashMap<Integer, StudyEventDefinitionBean> idToStudyEventDefinition = (HashMap<Integer, StudyEventDefinitionBean>) session.getAttribute("idToStudyEventDefinition");
			
			for (Iterator keyIt = definitions.keySet().iterator(); keyIt.hasNext();) {
				StudyEventDefinitionBean def =(StudyEventDefinitionBean) keyIt.next();
				if ("yes".equals(request.getParameter("selected"+def.getId()))){
					definitions.put(def, true);
					atLeastOneEventDefSelected = true;
					if (!studyEventDefinitionIdToOrdinal.containsKey(def.getId())){
						listOfNewEventDefs.add(def);
					}
				} else {
					definitions.put(def, false);
					if (studyEventDefinitionIdToOrdinal.keySet().contains(def.getId())){
						studyEventDefinitionIdToOrdinal.remove(def.getId());
					}	
				}
			}
			//sort: Events from DB will be first, new marked Events - second
			for (Iterator keyIt = ordinalToStudyEventDefinitionId.keySet().iterator(); keyIt.hasNext();) {
				int index =(Integer) keyIt.next();
				int id = ordinalToStudyEventDefinitionId.get(index);
				if (studyEventDefinitionIdToOrdinal.containsKey(id)){
					sortedListOfDefinitions.add(idToStudyEventDefinition.get(id));
				}
			}
			sortedListOfDefinitions.addAll(listOfNewEventDefs);
			
			//session.setAttribute("studyEventDefinitionIdToOrdinal", studyEventDefinitionIdToOrdinal);
			session.setAttribute("listOfDefinitions", sortedListOfDefinitions);
			session.setAttribute("definitionsToView", definitions);
		} else {  //not dynamic group
			atLeastOneEventDefSelected = true;
			StringBuilder rowsWithDuplicateNames = new StringBuilder("");
			Set<String> setOfNames = new HashSet<String>();
			for (int i = 0; i < 50; i++) {
				String name = fp.getString("studyGroup" + i);
				String description = fp.getString("studyGroupDescription" + i);
				int studyGroupId = fp.getInt("studyGroupId" + i);
				if (!StringUtil.isBlank(name)) {
					StudyGroupBean sGroup = new StudyGroupBean();
					sGroup.setName(name);
					sGroup.setDescription(description);
					sGroup.setId(studyGroupId);
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
			if ((fp.getString("name").trim().equals(thisBean.getName()))&&(!fp.getString("name").trim().equals(oldGroup.getName()))) {
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
			if (!String.valueOf(GroupClassType.DYNAMIC.getId()).equals(request.getParameter("groupClassTypeId"))){ //dynamic group
				group.setSubjectAssignment(fp.getString("subjectAssignment"));
			}
			group.setGroupClassTypeName(GroupClassType.get(group.getGroupClassTypeId()).getName());
			session.setAttribute("group", group);
			
			forwardPage(Page.UPDATE_SUBJECT_GROUP_CLASS_CONFIRM);
		} else {
			logger.info("has validation errors in the first section");
			request.setAttribute("formMessages", errors);

			forwardPage(Page.UPDATE_SUBJECT_GROUP_CLASS);
		}
	}

	private void submitGroup() throws OpenClinicaException {
		StudyGroupClassBean oldGroup = (StudyGroupClassBean) session.getAttribute("oldGroup");
		StudyGroupClassBean group = (StudyGroupClassBean) session.getAttribute("group");
		StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
		group.setStudyId(currentStudy.getId());
		group.setUpdater(ub);
		group.setStatus(Status.AVAILABLE);
		group.setId(oldGroup.getId());
		group.setDynamicOrdinal(oldGroup.getDynamicOrdinal());
		
		group = (StudyGroupClassBean) sgcdao.update(group);
		
		if (!group.isActive()) {
			addPageMessage(respage.getString("the_subject_group_class_no_updated_database"));
		} else {
			if (group.getGroupClassTypeId() == GroupClassType.DYNAMIC.getId()){
				ArrayList<StudyEventDefinitionBean> listOfDefinitions = (ArrayList)session.getAttribute("listOfDefinitions");
				ArrayList<StudyEventDefinitionBean> listOfOrderedDefinitions = new ArrayList(listOfDefinitions);
				HashMap<Integer,Integer> studyEventDefinitionIdToPK = (HashMap)session.getAttribute("studyEventDefinitionIdToPK");
					
				//read order from submit-page and create ordered list
				for ( StudyEventDefinitionBean def: listOfDefinitions) {
					int index = Integer.valueOf(request.getParameter("event" + def.getId()));
					listOfOrderedDefinitions.set(index-1, def);
				}	
				//if it is needed, create DynamicEventBeans and write them to DB
				HashMap<Integer, Integer> studyEventDefinitionIdToOrdinal = (HashMap<Integer, Integer>) session.getAttribute("studyEventDefinitionIdToOrdinal");
				DynamicEventDao dedao = new DynamicEventDao(sm.getDataSource());
				//read DynamicEventBeans by GroupClassId from DB
				for (int i=0; i<listOfOrderedDefinitions.size(); i++) {
					StudyEventDefinitionBean def = listOfOrderedDefinitions.get(i);
						
					DynamicEventBean de = new DynamicEventBean();
					de.setStudyGroupClassId(group.getId());
					de.setStudyEventDefinitionId(def.getId());
					de.setStudyId(currentStudy.getId());
					de.setOrdinal(i);
					//if new then create
					if (!studyEventDefinitionIdToOrdinal.containsKey(def.getId())){
						de.setOwner(ub);
						dedao.create(de);
					} else {
						//exist in DB, but are changed - then update
						de.setId(studyEventDefinitionIdToPK.get(def.getId()));
						de.setUpdater(ub);
						dedao.update(de);
						studyEventDefinitionIdToOrdinal.remove(def.getId());
					}
				}
				//delete DynamicEventBeans that aren't used from DB
				for (Iterator keyIt = studyEventDefinitionIdToOrdinal.keySet().iterator(); keyIt.hasNext();) {
					int id =studyEventDefinitionIdToPK.get(keyIt.next());
					dedao.deleteByPK(id);
				}
			} else {
				ArrayList studyGroups = (ArrayList) session.getAttribute("studyGroups");
				StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
				for (int i = 0; i < studyGroups.size(); i++) {
					StudyGroupBean sg = (StudyGroupBean) studyGroups.get(i);
					sg.setStudyGroupClassId(group.getId());
					sg.setStatus(Status.AVAILABLE);
					if (sg.getId() == 0) {
						sg.setOwner(ub);
						sgdao.create(sg);
					} else {
						sg.setUpdater(ub);
						sgdao.update(sg);
					}
				}
			}	
			addPageMessage(respage.getString("the_subject_group_class_updated_succesfully"));
		}
		ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
		session.setAttribute("pageMessages", pageMessages);
		
		clearSession();
		
		forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);
	}
	
	private void clearSession() {
		session.removeAttribute("listOfDefinitions");
		session.removeAttribute("definitionsToView");
		session.removeAttribute("fields");
		session.removeAttribute("group");
		session.removeAttribute("oldGroup");
		session.removeAttribute("studyGroups");
		session.removeAttribute("groupTypes");
		session.removeAttribute("studyEventDefinitionIdToOrdinal");
		session.removeAttribute("ordinalToStudyEventDefinitionId");
		session.removeAttribute("idToStudyEventDefinition");
		session.removeAttribute("studyEventDefinitionIdToPK");
		session.removeAttribute("defaultGroupAlreadyExists");
	}
}
