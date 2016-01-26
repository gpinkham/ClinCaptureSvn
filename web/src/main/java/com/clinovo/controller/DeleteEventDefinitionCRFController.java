package com.clinovo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.EventCRFService;
import com.clinovo.service.EventDefinitionCrfService;
import com.clinovo.util.RuleSetServiceUtil;

/**
 * This controller is triggered by "Delete" icon on "Edit Event Definition" page. And handles all actions that are
 * linked to this page.
 */
@Controller
@RequestMapping("/deleteEventDefinitionCRF")
public class DeleteEventDefinitionCRFController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private EventCRFService eventCRFService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private EventDefinitionCrfService eventDefinitionCrfService;

	/**
	 * This method is called on "Delete" icon click. Checks if there are some item_data or rules exists for this Event
	 * Definition CRF.
	 * 
	 * @param model
	 *            Model.
	 * @param eventDefinitionCRFId
	 *            id of the Event Definition CRF.
	 * @param eventDefinitionId
	 *            id of the Event Definition
	 * @return name of the page.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String initDeletionPage(Model model, @RequestParam("id") int eventDefinitionCRFId,
			@RequestParam("edId") int eventDefinitionId) {
		String page = "managestudy/deleteEventDefinitionCrf";
		EventDefinitionCRFDAO edcDAO = new EventDefinitionCRFDAO(dataSource);
		EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcDAO.findByPK(eventDefinitionCRFId);
		CRFDAO crfdao = new CRFDAO(dataSource);
		CRFBean crf = (CRFBean) crfdao.findByPK(edc.getCrfId());
		edc.setCrf(crf);
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		List<EventCRFBean> eventCRFs = eventCRFDAO.findAllByEventDefinitionCRFId(eventDefinitionCRFId);
		List<EventCRFBean> startedEventCRFs = eventCRFService.getAllStartedEventCRFsWithStudyAndEventName(eventCRFs);
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		StudyEventDefinitionBean studyEventDefinition = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(eventDefinitionId);
		RuleSetService ruleSetService = RuleSetServiceUtil.getRuleSetService();
		List<RuleSetRuleBean> ruleSetRuleBeans = ruleSetService
				.findAllRulesForEventDefinitionCRF(studyEventDefinition.getOid(), edc.getCrfId());

		boolean canBeDeleted = startedEventCRFs.size() == 0 && ruleSetRuleBeans.size() == 0;
		model.addAttribute("canBeDeleted", canBeDeleted);
		model.addAttribute("eventCRFs", startedEventCRFs);
		model.addAttribute("edc", edc);
		model.addAttribute("edId", eventDefinitionId);
		model.addAttribute("ruleSetRules", ruleSetRuleBeans);
		return page;
	}

	/**
	 * Submit deletion and return user to "UpdateEventDefinitionPage".
	 * 
	 * @param request
	 *            HttpServletRequest.
	 * @param edcId
	 *            id of the Event Definition CRF.
	 * @param eventDefinitionId
	 *            id of the Event Definition.
	 * @return name of the page.
	 */
	@RequestMapping(method = RequestMethod.GET, params = "submit")
	public String submitDeletion(HttpServletRequest request, @RequestParam("id") int edcId, @RequestParam("edId") int eventDefinitionId) {
		String page = "redirect:/InitUpdateEventDefinition?id=" + eventDefinitionId;
		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(edcId);
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(eventDefinitionId);
		CRFDAO crfdao = new CRFDAO(dataSource);
		CRFBean crfBean = (CRFBean) crfdao.findByPK(eventDefinitionCRFBean.getCrfId());
		String message = messageSource.getMessage("crf_was_deleted_from_event_definition",
				new String[]{crfBean.getName(), studyEventDefinitionBean.getName()}, LocaleResolver.getLocale(request));
		try {
			eventDefinitionCrfService.deleteEventDefinitionCRF(RuleSetServiceUtil.getRuleSetService(),
					studyEventDefinitionBean, eventDefinitionCRFBean, LocaleResolver.getLocale());
		} catch (Exception e) {
			message = e.getMessage();
		}
		HashMap<String, Object> storedAttributes = new HashMap<String, Object>();
		ArrayList<String> pageMessages = new ArrayList<String>();
		pageMessages.add(message);
		storedAttributes.put(BaseController.PAGE_MESSAGE, pageMessages);
		request.getSession().setAttribute(BaseController.STORED_ATTRIBUTES, storedAttributes);
		return page;
	}

	/**
	 * Go back to UpdateEventDefinitionPage.
	 * 
	 * @param eventDefinitionId
	 *            id of the Event Definition.
	 * @return name of the page.
	 */
	@RequestMapping(method = RequestMethod.GET, params = "back")
	public String back(@RequestParam("edId") int eventDefinitionId) {
		return "redirect:/InitUpdateEventDefinition?id=" + eventDefinitionId;
	}
}
