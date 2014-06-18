package com.clinovo.controller;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

@Controller
public class ChangeOrdinalController {

	@Autowired
	private DataSource datasource;

	public static final String USER_BEAN_NAME = "userBean";
	public static final String ERROR_PAGE = "redirect:/MainMenu?message=system_no_permission";
	public static final String EVENT_LIST_PAGE = "redirect:/ListEventDefinition";

	@RequestMapping("/changeDefinitionOrdinal")
	public String changeDefinitionOrdinalHandler(HttpServletRequest request, @RequestParam("current") int current, @RequestParam("previous") int previous) throws Exception {

		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);

		if (ub.isSysAdmin()) {
			increase(current, previous);
		} else {
			return ERROR_PAGE;
		}

		return EVENT_LIST_PAGE;
	}

	private void increase(int idCurrent, int idPrevious) {

		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(datasource);

		if (idCurrent > 0) {

			StudyEventDefinitionBean current = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(idCurrent);
			current.setOrdinal(current.getOrdinal() - 1);

			studyEventDefinitionDAO.update(current);
		}
		if (idPrevious > 0) {

			StudyEventDefinitionBean previous = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(idPrevious);
			previous.setOrdinal(previous.getOrdinal() + 1);

			studyEventDefinitionDAO.update(previous);
		}
	}
}
