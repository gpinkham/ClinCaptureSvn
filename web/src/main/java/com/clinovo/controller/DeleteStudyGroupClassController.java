package com.clinovo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.core.GroupClassType;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.clinovo.util.MayProceedUtil;

/**
 * This controller is triggered by "Delete" icon on "Edit Event Definition" page. And handles all actions that are
 * linked to this page.
 */
@Controller
@RequestMapping("/deleteStudyGroupClass")
public class DeleteStudyGroupClassController extends SpringController {

	@Autowired
	private MessageSource messageSource;
	
	/**
	 * This method is called on "Delete" icon click on Manage All Groups in Study page. 
	 * 
	 * @param request
	 *            HttpServletRequest.   
	 * @param model
	 *            Model.
	 * @param id
	 *            id of the Study Group Class.
	 * @return name of the page.
	 */
	@RequestMapping(method = RequestMethod.GET, params = "confirm")
	public String deleteStudyGroupClass(HttpServletRequest request, Model model, @RequestParam("id") int groupId) throws Exception {
		String page = "managestudy/deleteStudyGroupClass";
		if (!MayProceedUtil.mayProceed(request, Role.SYSTEM_ADMINISTRATOR, Role.STUDY_ADMINISTRATOR)) {
			return "redirect:/MainMenu?message=system_no_permission";
		}			
		if (groupId == 0) {
				page = "redirect:/ListSubjectGroupClass?read=true&message=please_choose_a_subject_group_class_to_remove";
		}
		StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(getDataSource());
		StudyGroupDAO sgdao = new StudyGroupDAO(getDataSource());
		SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(getDataSource());
			
		request.getSession().removeAttribute("group");
		StudyGroupClassBean group = sgcdao.findByPK(groupId);
				
		if (group.getGroupClassTypeId() == GroupClassType.DYNAMIC.getId()) {
			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
			List<StudyEventDefinitionBean> orderedDefinitions = seddao.findAllOrderedByStudyGroupClassId(group.getId());
			EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(getDataSource());
			for (StudyEventDefinitionBean def : orderedDefinitions) {
				def.setCrfNum(edcdao.findAllActiveParentsByEventDefinitionId(def.getId()).size());
			}
			request.setAttribute("orderedDefinitions", orderedDefinitions);
		} else {
			List<StudyGroupBean> studyGroups = sgdao.findAllByGroupClass(group);
			for (StudyGroupBean sg : studyGroups) {
				sg.setSubjectMaps(sgmdao.findAllByStudyGroupClassAndGroup(group.getId(), sg.getId()));
			}
					
			model.addAttribute("studyGroups", studyGroups);
		}
		request.getSession().setAttribute("group", group);
		
		return page;
	}

	@RequestMapping(method = RequestMethod.POST, params = "back")
	public String back(HttpServletRequest request) throws Exception {
		
		return "redirect:system";
	}
	
	/**
	 * Submit deletion and return user to "ListSubjectGroupClassPage".
	 * 
	 * @param request
	 *            HttpServletRequest.
	 * @param id
	 *            id of the Study Group Class.
	 * @return name of the page.
	 */
	@RequestMapping(method = RequestMethod.POST, params = "submit")
	public String submitDeletion(HttpServletRequest request, Model model, @RequestParam("id") int groupId) {
		String page = "redirect:/ListSubjectGroupClass";
		if (!MayProceedUtil.mayProceed(request, Role.SYSTEM_ADMINISTRATOR, Role.STUDY_ADMINISTRATOR)) {
			page = "redirect:/MainMenu?message=system_no_permission";
		} else {			
			if (groupId == 0) {
				page = "redirect:/ListSubjectGroupClass?read=true&message=please_choose_a_subject_group_class_to_remove";
			} else {
				StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(getDataSource());
				
				StudyGroupClassBean group = sgcdao.findByPK(groupId);
				if (group.getGroupClassTypeId() == GroupClassType.DYNAMIC.getId()) {
					DynamicEventDao dedao = new DynamicEventDao(getDataSource());
					StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
					ssdao.updateDynamicGroupClassId(groupId, 0);
					dedao.deleteAllByStudyGroupClassId(groupId);
					sgcdao.deleteByPK(groupId);
				} else {
					SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(getDataSource());
					StudyGroupDAO sgdao = new StudyGroupDAO(getDataSource());
					sgmdao.deleteAllByStudyGroupClassId(groupId);
					sgdao.deleteAllByStudyGroupClassId(groupId);
					sgcdao.deleteByPK(groupId);
				}
			}
		}
		return page;
	}
}
