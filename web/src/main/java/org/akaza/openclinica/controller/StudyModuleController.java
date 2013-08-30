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

package org.akaza.openclinica.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.hibernate.StudyModuleStatusDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.domain.managestudy.StudyModuleStatus;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller("studyModuleController")
@RequestMapping("/studymodule")
@SessionAttributes("studyModuleStatus")
@SuppressWarnings({"rawtypes","unchecked"})
public class StudyModuleController {
	@Autowired
	private SidebarInit sidebarInit;

	@Autowired
	private StudyModuleStatusDao studyModuleStatusDao;

	@Autowired
	private RuleSetServiceInterface ruleSetService;

	@Autowired
	private BasicDataSource dataSource;

	private StudyEventDefinitionDAO studyEventDefinitionDao;
	private CRFDAO crfDao;
	private StudyGroupClassDAO studyGroupClassDao;
	private StudyDAO studyDao;
	private UserAccountDAO userDao;
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	@Autowired
	CoreResources coreResources;

	public StudyModuleController() {

	}

	@SuppressWarnings("deprecation")
	@RequestMapping(method = RequestMethod.GET)
	public ModelMap handleMainPage(HttpServletRequest request, HttpServletResponse response) {
		ModelMap map = new ModelMap();
		// Todo need something to reset panel from all the Spring Controllers
		StudyInfoPanel panel = new StudyInfoPanel();
		UserAccountBean userBean = (UserAccountBean) request.getSession().getAttribute("userBean");
		if (!mayProceed(request)) {
			try {
				response.sendRedirect(request.getContextPath() + "/MainMenu?message=authentication_failed");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		panel.reset();
		request.getSession().setAttribute("panel", panel);

		ResourceBundleProvider.updateLocale(request.getLocale());

		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");

		new EventDefinitionCRFDAO(dataSource);
		studyEventDefinitionDao = new StudyEventDefinitionDAO(dataSource);
		crfDao = new CRFDAO(dataSource);
		studyGroupClassDao = new StudyGroupClassDAO(dataSource);
		studyDao = new StudyDAO(dataSource);
		userDao = new UserAccountDAO(dataSource);

		StudyModuleStatus sms = studyModuleStatusDao.findByStudyId(currentStudy.getId());
		if (sms == null) {
			sms = new StudyModuleStatus();
			sms.setStudyId(currentStudy.getId());
		}

		int crfCount = crfDao.findAllByStudy(currentStudy.getId()).size();
		int crfWithEventDefinition = crfDao.findAllActiveByDefinitions(currentStudy.getId()).size();
		int totalCrf = crfCount + crfWithEventDefinition;
		int eventDefinitionCount = studyEventDefinitionDao.findAllActiveByStudy(currentStudy).size();

		int subjectGroupCount = studyGroupClassDao.findAllByStudy(currentStudy).size();

		int ruleCount = ruleSetService.getCountByStudy(currentStudy);

		int siteCount = studyDao.findOlnySiteIdsByStudy(currentStudy).size();
		int userCount = userDao.findAllUsersByStudy(currentStudy.getId()).size();
		Collection childStudies = studyDao.findAllByParent(currentStudy.getId());
		Map childStudyUserCount = new HashMap();
		for (Object sb : childStudies) {
			StudyBean childStudy = (StudyBean) sb;
			childStudyUserCount.put(childStudy.getName(), userDao.findAllUsersByStudy(childStudy.getId()).size());
		}

		if (sms.getCrf() == 0) {
			sms.setCrf(StudyModuleStatus.NOT_STARTED);
		}
		if (sms.getCrf() != 3 && totalCrf > 0) {
			sms.setCrf(StudyModuleStatus.IN_PROGRESS);
		}

		if (sms.getEventDefinition() == 0) {
			sms.setEventDefinition(StudyModuleStatus.NOT_STARTED);
		}
		if (sms.getEventDefinition() != 3 && eventDefinitionCount > 0) {
			sms.setEventDefinition(StudyModuleStatus.IN_PROGRESS);
		}

		if (sms.getSubjectGroup() == 0) {
			sms.setSubjectGroup(StudyModuleStatus.NOT_STARTED);
		}
		if (sms.getSubjectGroup() != 3 && subjectGroupCount > 0) {
			sms.setSubjectGroup(StudyModuleStatus.IN_PROGRESS);
		}

		if (sms.getRule() == 0) {
			sms.setRule(StudyModuleStatus.NOT_STARTED);
		}
		if (sms.getRule() != 3 && ruleCount > 0) {
			sms.setRule(StudyModuleStatus.IN_PROGRESS);
		}

		if (sms.getSite() == 0) {
			sms.setSite(StudyModuleStatus.NOT_STARTED);
		}
		if (sms.getSite() != 3 && siteCount > 0) {
			sms.setSite(StudyModuleStatus.IN_PROGRESS);
		}

		if (sms.getUsers() == 0) {
			sms.setUsers(StudyModuleStatus.NOT_STARTED);
		}
		if (sms.getUsers() != 3 && userCount > 0) {
			sms.setUsers(StudyModuleStatus.IN_PROGRESS);
		}

		map.addObject(sms);
		map.addAttribute("crfCount", totalCrf);
		map.addAttribute("eventDefinitionCount", eventDefinitionCount);
		map.addAttribute("subjectGroupCount", subjectGroupCount);
		map.addAttribute("ruleCount", ruleCount);
		map.addAttribute("siteCount", siteCount);
		map.addAttribute("userCount", userCount);
		map.addAttribute("childStudyUserCount", childStudyUserCount);
		map.addAttribute("studyId", currentStudy.getId());
		map.addAttribute("currentStudy", currentStudy);

		request.setAttribute("userBean", userBean);
		ArrayList statusMap = Status.toStudyUpdateMembersList();
		request.setAttribute("statusMap", statusMap);

		if (currentStudy.getParentStudyId() > 0) {
			StudyBean parentStudy = (StudyBean) studyDao.findByPK(currentStudy.getParentStudyId());
			request.setAttribute("parentStudy", parentStudy);
		}

		ArrayList pageMessages = new ArrayList();
		if (request.getSession().getAttribute("pageMessages") != null) {
			pageMessages.addAll((ArrayList) request.getSession().getAttribute("pageMessages"));
			request.setAttribute("pageMessages", pageMessages);
			request.getSession().removeAttribute("pageMessages");
		}
		return map;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@ModelAttribute("studyModuleStatus") StudyModuleStatus studyModuleStatus,
			BindingResult result, SessionStatus status, HttpServletRequest request) {
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		if (request.getParameter("saveStudyStatus") == null) {
			studyModuleStatusDao.saveOrUpdate(studyModuleStatus);
			status.setComplete();
		} else {
			currentStudy.setOldStatus(currentStudy.getStatus());
			currentStudy.setStatus(Status.get(studyModuleStatus.getStudyStatus()));
			if (currentStudy.getParentStudyId() > 0) {
				studyDao.updateStudyStatus(currentStudy);
			} else {
				studyDao.updateStudyStatus(currentStudy);
			}

			ArrayList siteList = (ArrayList) studyDao.findAllByParent(currentStudy.getId());
			if (siteList.size() > 0) {
				studyDao.updateSitesStatus(currentStudy);
			}
		}
		return "redirect:studymodule";
	}

	@ExceptionHandler(HttpSessionRequiredException.class)
	public String handleSessionRequiredException(HttpSessionRequiredException ex, HttpServletRequest request) {
		return "redirect:/MainMenu";
	}

	@ExceptionHandler(NullPointerException.class)
	public String handleNullPointerException(NullPointerException ex, HttpServletRequest request,
			HttpServletResponse response) {
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		if (currentStudy == null) {
			return "redirect:/MainMenu";
		}
		throw ex;
	}

	public SidebarInit getSidebarInit() {
		return sidebarInit;
	}

	public void setSidebarInit(SidebarInit sidebarInit) {
		this.sidebarInit = sidebarInit;
	}

	public StudyModuleStatusDao getStudyModuleStatusDao() {
		return studyModuleStatusDao;
	}

	public BasicDataSource getDataSource() {
		return dataSource;
	}

	public String getContextPath(HttpServletRequest request) {
		String contextPath = request.getContextPath().replaceAll("/", "");
		return contextPath;
	}

	public String getRequestURLMinusServletPath(HttpServletRequest request) {
		String requestURLMinusServletPath = request.getRequestURL().toString().replaceAll(request.getServletPath(), "");
		logMe("processing.." + requestURLMinusServletPath);
		return requestURLMinusServletPath;
	}

	public String getHostPath(HttpServletRequest request) {
		logMe("into the getHostPath/....URL = " + request.getRequestURL() + "URI=" + request.getRequestURI()
				+ "PROTOCOL=");
		String requestURLMinusServletPath = getRequestURLMinusServletPath(request);
		String hostPath = "";

		if (null != requestURLMinusServletPath) {
			String tmpPath = requestURLMinusServletPath.substring(0, requestURLMinusServletPath.lastIndexOf("/"));
			logMe("processing2..." + tmpPath);
			hostPath = tmpPath.substring(0, tmpPath.lastIndexOf("/"));
			logMe("processing2..." + hostPath);
		}
		logMe("after all the stripping returning" + hostPath);
		return hostPath;
	}

	public String getWebAppName(String servletCtxRealPath) {
		String webAppName = null;
		if (null != servletCtxRealPath) {
			String[] tokens = servletCtxRealPath.split("\\\\");
			webAppName = tokens[(tokens.length - 1)].trim();
		}
		return webAppName;
	}

	private boolean mayProceed(HttpServletRequest request) {
		StudyUserRoleBean currentRole = (StudyUserRoleBean) request.getSession().getAttribute("userRole");
		Role r = currentRole.getRole();

		if (Role.SYSTEM_ADMINISTRATOR.equals(r) || Role.STUDY_DIRECTOR.equals(r) || Role.STUDY_ADMINISTRATOR.equals(r)) {
			return true;
		}
		return false;
	}

	private void logMe(String msg) {
		System.out.println(msg);
		logger.info(msg);
	}

}
