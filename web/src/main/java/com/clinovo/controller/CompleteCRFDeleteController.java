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
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.util.StudyEventDefinitionUtil;
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
import java.util.List;

/**
 * CompleteCRFDeleteController that handles requests from the delete crf page.
 */
@Controller
@RequestMapping("/completeCRFDelete")
@SuppressWarnings({ "unchecked" })
public class CompleteCRFDeleteController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private RuleSetDao ruleSetDao;

	public static final String ACTION_PAGE = "admin/completeCRFDelete";
	public static final String ERROR_PAGE = "redirect:/MainMenu?message=system_no_permission";
	public static final String CRF_LIST = "redirect:/ListCRF";

	/**
	 * Method that handles requests for delete crf page.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param crfId
	 *            crf id
	 * @param model
	 *            model
	 * @return String
	 * @throws Exception
	 *             an exception
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String mainGet(HttpServletRequest request, Model model, @RequestParam("crfId") int crfId) throws Exception {

		StudyUserRoleBean userRole = (StudyUserRoleBean) request.getSession().getAttribute(BaseController.USER_ROLE);

		if (userRole.getRole() == Role.SYSTEM_ADMINISTRATOR || (userRole.getRole() == Role.STUDY_ADMINISTRATOR)) {

			CRFDAO crfDao = new CRFDAO(dataSource);
			EventCRFDAO eventCrfDAO = new EventCRFDAO(dataSource);
			DiscrepancyNoteDAO discrepancyNoteDao = new DiscrepancyNoteDAO(dataSource);
			EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(dataSource);

			CRFBean crfBean = (CRFBean) crfDao.findByPK(crfId);

			List<RuleSetBean> ruleSetBeanList = ruleSetListFilter(crfId);
			List<EventCRFBean> eventCrfBeanList = eventCrfDAO.findAllStartedByCrf(crfId);
			List<StudyEventDefinitionBean> eventDefinitionListAvailable = StudyEventDefinitionUtil
					.studyEventDefinitionListFilter(dataSource, eventDefinitionCrfDao.findAllByCRF(crfId));
			List<StudyEventDefinitionBean> eventDefinitionListFull = StudyEventDefinitionUtil
					.studyEventDefinitionStatusUpdate(dataSource, crfId);

			List<DiscrepancyNoteBean> crfDiscrepancyNotes = discrepancyNoteDao.findAllByCRFId(crfId);

			model.addAttribute("crfBean", crfBean);
			model.addAttribute("crfDiscrepancyNotes", crfDiscrepancyNotes);
			model.addAttribute("ruleSetBeanList", ruleSetBeanList);
			model.addAttribute("eventDefinitionListAvailable", eventDefinitionListAvailable);
			model.addAttribute("eventDefinitionListFull", eventDefinitionListFull);
			model.addAttribute("eventCRFBeanList", eventCrfBeanList);

			if (eventCrfBeanList.size() > 0 || crfDiscrepancyNotes.size() > 0
					|| eventDefinitionListAvailable.size() > 0 || ruleSetBeanList.size() > 0) {
				PageMessagesUtil.addPageMessage(request,
						messageSource.getMessage("this_crf_has_associated_data", null, request.getLocale()));
			} else {
				PageMessagesUtil.addPageMessage(request,
						messageSource.getMessage("this_crf_has_no_conflict_data", null, request.getLocale()));
			}

		} else {

			return ERROR_PAGE;
		}

		return ACTION_PAGE;
	}

	/**
	 * Method that handles requests from the confirm delete crf page.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param crfId
	 *            crf id
	 * @return String
	 * @throws Exception
	 *             an exception
	 */
	@RequestMapping(method = RequestMethod.POST, params = "confirm")
	public String confirm(HttpServletRequest request, @RequestParam("crfId") int crfId) throws Exception {

		CRFDAO crfDao = new CRFDAO(dataSource);
		EventCRFDAO eventCrfDAO = new EventCRFDAO(dataSource);
		DiscrepancyNoteDAO discrepancyNoteDao = new DiscrepancyNoteDAO(dataSource);
		EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(dataSource);

		List<RuleSetBean> ruleSetBeanList = ruleSetListFilter(crfId);
		List<EventCRFBean> eventCrfBeanList = eventCrfDAO.findAllStartedByCrf(crfId);
		List<StudyEventDefinitionBean> eventDefinitionListAvailable = StudyEventDefinitionUtil
				.studyEventDefinitionListFilter(dataSource, eventDefinitionCrfDao.findAllByCRF(crfId));
		List<DiscrepancyNoteBean> crfDiscrepancyNotes = discrepancyNoteDao.findAllByCRFId(crfId);

		if (eventCrfBeanList.size() > 0 || crfDiscrepancyNotes.size() > 0 || eventDefinitionListAvailable.size() > 0
				|| ruleSetBeanList.size() > 0) {
			request.getSession().setAttribute("controllerMessage",
					messageSource.getMessage("this_crf_has_associated_data", null, request.getLocale()));
		} else {
			crfDao.deleteCrfById(crfId);
			request.getSession().setAttribute("controllerMessage",
					messageSource.getMessage("the_crf_has_been_removed", null, request.getLocale()));
		}

		return CRF_LIST;
	}

	private List<RuleSetBean> ruleSetListFilter(int crfId) {
		CRFDAO crfDao = new CRFDAO(dataSource);

		List<RuleSetBean> ruleSetBeanList = new ArrayList<RuleSetBean>();
		CRFBean crfBean = (CRFBean) crfDao.findByPK(crfId);

		List<RuleSetBean> studyRuleSetBeanList = ruleSetDao.findByCrfIdAndCrfOid(crfBean);
		for (RuleSetBean ruleSetRule : studyRuleSetBeanList) {
			ruleSetBeanList.add(ruleSetRule);
		}

		return ruleSetBeanList;
	}
}
