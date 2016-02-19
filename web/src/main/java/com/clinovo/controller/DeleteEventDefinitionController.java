/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2015 Clinovo Inc.
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

import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.SpringController;
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

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.EventDefinitionService;
import com.clinovo.util.RequestUtil;

/**
 * Delete event definition controller.
 */
@Controller
@RequestMapping("deleteEventDefinition")
public class DeleteEventDefinitionController extends SpringController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private EventDefinitionService eventDefinitionService;

	/**
	 * Main get method that will open Delete Event Definition page.
	 * 
	 * @param eventId
	 *            int
	 * @param model
	 *            Model
	 * @param request
	 *            HttpServletRequest
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

		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(eventId);
		ArrayList<EventDefinitionCRFBean> eventDefinitionCRFs = eventDefinitionCRFDAO
				.findAllParentsByEventDefinitionId(eventId);
		eventDefinitionCRFs = eventDefinitionCRFs == null
				? new ArrayList<EventDefinitionCRFBean>()
				: eventDefinitionCRFs;
		ArrayList<StudyEventBean> studyEventBeans = studyEventDAO.findAllByStudyAndEventDefinitionId(studyBean,
				eventId);

		for (StudyEventBean studyEvent : studyEventBeans) {
			StudySubjectBean studySubjectBean = (StudySubjectBean) studySubjectDAO
					.findByPK(studyEvent.getStudySubjectId());
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
	 * 
	 * @param eventId
	 *            int
	 * @param request
	 *            HttpServletRequest
	 * @return page name
	 */
	@RequestMapping(method = RequestMethod.POST, params = "confirm")
	public String confirmDeleteEventDefinition(@RequestParam("id") int eventId, HttpServletRequest request) {
		Locale locale = LocaleResolver.getLocale(request);
		StudyBean studyBean = (StudyBean) request.getSession().getAttribute("study");
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) new StudyEventDefinitionDAO(
				dataSource).findByPK(eventId);
		String message = messageSource.getMessage("study_event_definition_was_successfully_deleted",
				new String[]{studyEventDefinitionBean.getName()}, locale);
		try {
			eventDefinitionService.deleteStudyEventDefinition(studyEventDefinitionBean, studyBean, locale);
		} catch (Exception e) {
			message = e.getMessage();
		}
		RequestUtil.storePageMessage(request, message);
		return "redirect:/ListEventDefinition";
	}
}
