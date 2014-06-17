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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.controller;

import com.clinovo.util.PageMessagesUtil;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/completeCRFDelete")
public class CompleteCRFDeleteController {

	@Autowired
	private DataSource datasource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	protected RuleSetDao ruleSetDao;

	public static final String ACTION_PAGE = "admin/completeCRFDelete";
	public static final String ERROR_PAGE = "redirect:/MainMenu?message=system_no_permission";
	public static final String CRF_LIST = "redirect:/ListCRF";

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	public String mainGet(HttpServletRequest request, Model model, @RequestParam("crfId") int crfId) throws Exception {

		StudyUserRoleBean userRole = (StudyUserRoleBean) request.getSession().getAttribute(BaseController.USER_ROLE);

		if (userRole.getRole() == Role.SYSTEM_ADMINISTRATOR || (userRole.getRole() == Role.STUDY_ADMINISTRATOR)) {

			CRFDAO crfDao = new CRFDAO(datasource);
			EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(datasource);
			DiscrepancyNoteDAO discrepancyNoteDao = new DiscrepancyNoteDAO(datasource);

			CRFBean crfBean = (CRFBean) crfDao.findByPK(crfId);

			List<RuleSetBean> ruleSetBeanList = ruleSetListFilter(crfId);
			List<EventCRFBean> eventCrfBeanList = getEventCrfBeanList(crfId);
			List<StudyEventDefinitionBean> eventDefinitionListAvailable = studyEventDefinitionListFilter(eventDefinitionCrfDao.findAllByCRF(crfId));
			List<StudyEventDefinitionBean> eventDefinitionListFull = studyEventDefinitionStatusUpdate(crfId);

			List<DiscrepancyNoteBean> crfDiscrepancyNotes = discrepancyNoteDao.findAllByCRFId(crfId);

			model.addAttribute("crfBean", crfBean);
			model.addAttribute("crfDiscrepancyNotes", crfDiscrepancyNotes);
			model.addAttribute("ruleSetBeanList", ruleSetBeanList);
			model.addAttribute("eventDefinitionListAvailable", eventDefinitionListAvailable);
			model.addAttribute("eventDefinitionListFull", eventDefinitionListFull);
			model.addAttribute("eventCRFBeanList", eventCrfBeanList);

			if (eventCrfBeanList.size() > 0 || crfDiscrepancyNotes.size() > 0 || eventDefinitionListAvailable.size() > 0 || ruleSetBeanList.size() > 0) {
				PageMessagesUtil.addPageMessage(request, messageSource.getMessage("this_crf_has_associated_data", null, request.getLocale()));
			} else {
				PageMessagesUtil.addPageMessage(request, messageSource.getMessage("this_crf_has_no_conflict_data", null, request.getLocale()));
			}

		} else {

			return ERROR_PAGE;
		}

		return ACTION_PAGE;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, params = "confirm")
	public String confirm(HttpServletRequest request, @RequestParam("crfId") int crfId) throws Exception {

		CRFDAO crfDao = new CRFDAO(datasource);
		EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(datasource);
		DiscrepancyNoteDAO discrepancyNoteDao = new DiscrepancyNoteDAO(datasource);

		List<RuleSetBean> ruleSetBeanList = ruleSetListFilter(crfId);
		List<EventCRFBean> eventCrfBeanList = getEventCrfBeanList(crfId);
		List<StudyEventDefinitionBean> eventDefinitionListAvailable = studyEventDefinitionListFilter(eventDefinitionCrfDao.findAllByCRF(crfId));
		List<DiscrepancyNoteBean> crfDiscrepancyNotes = discrepancyNoteDao.findAllByCRFId(crfId);

		if (eventCrfBeanList.size() > 0 || crfDiscrepancyNotes.size() > 0 || eventDefinitionListAvailable.size() > 0 || ruleSetBeanList.size() > 0) {
			request.getSession().setAttribute("controllerMessage", messageSource.getMessage("this_crf_has_associated_data", null, request.getLocale()));
		} else {
			crfDao.deleteCrfById(crfId);
			request.getSession().setAttribute("controllerMessage", messageSource.getMessage("the_crf_has_been_removed", null, request.getLocale()));
		}

		return CRF_LIST;
	}

	@SuppressWarnings("unchecked")
	private List<EventCRFBean> getEventCrfBeanList(int crfId) {

		EventCRFDAO eventCrfDao = new EventCRFDAO(datasource);
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(datasource);

		List<CRFVersionBean> crfVersionBeanList = crfVersionDAO.findAllByCRFId(crfId);
		List<EventCRFBean> eventCRFBeanList = new ArrayList<EventCRFBean>();

		for (CRFVersionBean crfVersionBean : crfVersionBeanList) {
			List<EventCRFBean> eventCRFBeanWithSubjectList = eventCrfDao.findAllStudySubjectByCRFVersion(crfVersionBean.getId());

			for (EventCRFBean eventCRFBean : eventCRFBeanWithSubjectList) {
				if (!eventCRFBean.isNotStarted()) {
					eventCRFBeanList.add(eventCRFBean);
				}
			}
		}

		return eventCRFBeanList;
	}

	@SuppressWarnings("unchecked")
	private List<RuleSetBean> ruleSetListFilter(int crfId) {

		StudyDAO studyDao = new StudyDAO(datasource);
		CRFDAO crfDao = new CRFDAO(datasource);

		List<RuleSetBean> ruleSetBeanList = new ArrayList<RuleSetBean>();
		List<StudyBean> studyList = (List<StudyBean>) studyDao.findAll();
		CRFBean crfBean = (CRFBean) crfDao.findByPK(crfId);

		for (StudyBean study : studyList) {
			List<RuleSetBean> studyRuleSetBeanList = ruleSetDao.findByCrf(crfBean, study);
			for (RuleSetBean ruleSetRule : studyRuleSetBeanList) {
				ruleSetBeanList.add(ruleSetRule);
			}
		}

		return ruleSetBeanList;
	}


	private List<StudyEventDefinitionBean> studyEventDefinitionListFilter(Collection<EventDefinitionCRFBean> eventDefinitionCrfList) {

		List<StudyEventDefinitionBean> studyEventDefinitionListFiltered = new ArrayList<StudyEventDefinitionBean>();
		StudyEventDefinitionDAO studyEventDefinitionDao = new StudyEventDefinitionDAO(datasource);
		UserAccountDAO userAccountDao = new UserAccountDAO(datasource);

		for (EventDefinitionCRFBean eventDefCrfBean : eventDefinitionCrfList) {

			if (!eventDefCrfBean.getStatus().isDeleted()) {
				StudyEventDefinitionBean studyEventDefinition = (StudyEventDefinitionBean) studyEventDefinitionDao.findByPK(eventDefCrfBean.getStudyEventDefinitionId());
				UserAccountBean userAccountBean = (UserAccountBean) userAccountDao.findByPK(studyEventDefinition.getOwnerId());
				studyEventDefinition.setOwner(userAccountBean);

				studyEventDefinitionListFiltered.add(studyEventDefinition);
			}
		}

		return studyEventDefinitionListFiltered;
	}

	@SuppressWarnings("unchecked")
	private List<StudyEventDefinitionBean> studyEventDefinitionStatusUpdate(int crfId) {

		EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(datasource);
		StudyEventDefinitionDAO studyEventDefinitionDao = new StudyEventDefinitionDAO(datasource);

		List<StudyEventDefinitionBean> studyEventDefinitionUpdated = new ArrayList<StudyEventDefinitionBean>();
		List<EventDefinitionCRFBean> eventDefinitionCrfList = (List<EventDefinitionCRFBean>) eventDefinitionCrfDao.findAllByCRF(crfId);

		for (EventDefinitionCRFBean eventDefinitionCrfBean : eventDefinitionCrfList) {
			StudyEventDefinitionBean studyEventDefinition = (StudyEventDefinitionBean) studyEventDefinitionDao.findByPK(eventDefinitionCrfBean.getStudyEventDefinitionId());
			studyEventDefinition.setStatus(eventDefinitionCrfBean.getStatus());
			studyEventDefinitionUpdated.add(studyEventDefinition);
		}

		return studyEventDefinitionUpdated;
	}
}
