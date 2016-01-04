package com.clinovo.controller;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.RequestUtil;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
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
 * Delete event definition controller.
 */
@Controller
@RequestMapping("deleteEventDefinition")
public class DeleteEventDefinitionController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Main get method that will open Delete Event Definition page.
	 * @param eventId int
	 * @param model Model
	 * @param request HttpServletRequest
	 * @return String page name
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String mainGet(@RequestParam("id") int eventId, Model model, HttpServletRequest request) {
		String pageName = "managestudy/deleteEventDefinition";
		StudyBean studyBean = (StudyBean) request.getSession().getAttribute("study");

		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		StudyEventDAO studyEventDAO = new StudyEventDAO(dataSource);
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
		CRFDAO crfdao = new CRFDAO(dataSource);

		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(eventId);
		ArrayList<EventDefinitionCRFBean> eventDefinitionCRFs = eventDefinitionCRFDAO.findAllParentsByEventDefinitionId(eventId);
		eventDefinitionCRFs = eventDefinitionCRFs == null ? new ArrayList<EventDefinitionCRFBean>() : eventDefinitionCRFs;
		ArrayList<StudyEventBean> studyEventBeans = studyEventDAO.findAllByStudyAndEventDefinitionId(studyBean, eventId);

		for (StudyEventBean studyEvent : studyEventBeans) {
			StudySubjectBean studySubjectBean = (StudySubjectBean) studySubjectDAO.findByPK(studyEvent.getStudySubjectId());
			studyEvent.setStudySubject(studySubjectBean);
		}

		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCRFs) {
			CRFBean crfBean = (CRFBean) crfdao.findByPK(eventDefinitionCRFBean.getCrfId());
			eventDefinitionCRFBean.setCrf(crfBean);
		}

		model.addAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
		model.addAttribute("studyEventBeans", studyEventBeans);
		model.addAttribute("eventId", eventId);
		model.addAttribute("event", studyEventDefinitionBean);

		return pageName;
	}

	/**
	 * Delete event definition.
	 * @param eventId int
	 * @param request HttpServletRequest
	 * @return page name
	 */
	@RequestMapping(method = RequestMethod.POST, params = "confirm")
	public String confirmDeleteEventDefinition(@RequestParam("id") int eventId, HttpServletRequest request)  {
		String page = "redirect:/ListEventDefinition";
		StudyBean studyBean = (StudyBean) request.getSession().getAttribute("study");
		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		List<EventDefinitionCRFBean> eventDefinitionCRFs = eventDefinitionCRFDAO.findAllByEventDefinitionId(eventId);

		if (eventDefinitionCRFs != null && eventDefinitionCRFs.size() != 0) {
			String message = messageSource.getMessage("you_are_trying_to_delete_event_with_crfs", null, LocaleResolver.getLocale(request));
			RequestUtil.storePageMessage(request, message);
			return page;
		}

		StudyEventDAO studyEventDAO = new StudyEventDAO(dataSource);
		ArrayList<StudyEventBean> studyEventBeans = studyEventDAO.findAllByStudyAndEventDefinitionId(studyBean, eventId);

		if (studyEventBeans != null && studyEventBeans.size() != 0) {
			String message = messageSource.getMessage("you_are_trying_to_delete_event_definition_but_study_events_are_present",
					null, LocaleResolver.getLocale(request));
			RequestUtil.storePageMessage(request, message);
			return page;
		}

		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(eventId);
		studyEventDefinitionDAO.deleteEventDefinition(eventId);
		String successMessage = messageSource.getMessage("study_event_definition_was_successfully_deleted", new String[]{studyEventDefinitionBean.getName()}, LocaleResolver.getLocale(request));
		RequestUtil.storePageMessage(request, successMessage);

		return page;
	}
}
