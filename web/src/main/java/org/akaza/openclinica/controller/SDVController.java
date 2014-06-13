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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.controller.helper.SdvFilterDataBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.table.sdv.SDVUtil;
import org.akaza.openclinica.web.table.sdv.SubjectIdSDVFactory;
import org.jmesa.facade.TableFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Implement the functionality for displaying a table of Event CRFs for Source Data Verification. This is an autowired,
 * multiaction Controller.
 */
@Controller("sdvController")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SDVController {
	
	public final static String SUBJECT_SDV_TABLE_ATTRIBUTE = "sdvTableAttribute";
	@Autowired
	private DataSource dataSource;

	@Autowired
	private SDVUtil sdvUtil;

	@Autowired
	private SubjectIdSDVFactory sdvFactory;

	@Autowired
	private SidebarInit sidebarInit;

	public SDVController() {
	}

	@RequestMapping("/viewSubjectAggregate")
	public ModelMap viewSubjectAggregateHandler(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("studyId") int studyId) {
		if (!mayProceed(request)) {
			try {
				response.sendRedirect(request.getContextPath() + "/MainMenu?message=authentication_failed");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		ResourceBundleProvider.updateLocale(request.getLocale());
		ModelMap gridMap = new ModelMap();
		HttpSession session = request.getSession();
		boolean showMoreLink = false;
		if (session.getAttribute("sSdvRestore") != null && session.getAttribute("sSdvRestore") == "false") {
			session.setAttribute("sSdvRestore", "true");
			showMoreLink = true;
		} else if (request.getParameter("showMoreLink") != null) {
			showMoreLink = Boolean.parseBoolean(request.getParameter("showMoreLink").toString());
		} else if (session.getAttribute("s_sdv_showMoreLink") != null) {
			showMoreLink = Boolean.parseBoolean(session.getAttribute("s_sdv_showMoreLink") + "");
		} else {
			showMoreLink = true;
		}
		request.setAttribute("showMoreLink", showMoreLink + "");
		session.setAttribute("s_sdv_showMoreLink", showMoreLink + "");

		request.setAttribute("studyId", studyId);
		String restore = (String) request.getAttribute("s_sdv_restore");
		restore = restore != null && restore.length() > 0 ? restore : "false";
		request.setAttribute("s_sdv_restore", restore);
		request.setAttribute("imagePathPrefix", "../");

		ArrayList<String> pageMessages = (ArrayList<String>) request.getAttribute("pageMessages");
		if (pageMessages == null) {
			pageMessages = new ArrayList<String>();
		}

		request.setAttribute("showBackButton", request.getParameter("sbb") != null);
		request.setAttribute("pageMessages", pageMessages);
		sdvFactory.showMoreLink = showMoreLink;
		TableFacade facade = sdvFactory.createTable(request, response);
		String sdvMatrix = facade.render();
		gridMap.addAttribute(SUBJECT_SDV_TABLE_ATTRIBUTE, sdvMatrix);
		return gridMap;
	}

	@RequestMapping("/viewAllSubjectSDV")
	public ModelMap viewSubjectHandler(HttpServletRequest request, @RequestParam("studySubjectId") int studySubjectId,
			@RequestParam("studyId") int studyId) {

		ModelMap gridMap = new ModelMap();

		request.setAttribute("studyId", studyId);
		request.setAttribute("studySubjectId", studySubjectId);
		request.setAttribute("imagePathPrefix", "../");

		ArrayList<String> pageMessages = (ArrayList<String>) request.getAttribute("pageMessages");
		if (pageMessages == null) {
			pageMessages = new ArrayList<String>();
		}

		request.setAttribute("pageMessages", pageMessages);

		String sdvMatrix = sdvUtil.renderSubjectsTableWithLimit(request, studyId, studySubjectId);
		gridMap.addAttribute(SUBJECT_SDV_TABLE_ATTRIBUTE, sdvMatrix);
		return gridMap;
	}

	@RequestMapping("/viewAllSubjectSDVtmp")
	public ModelMap viewAllSubjectHandler(HttpServletRequest request, @RequestParam("studyId") int studyId,
			HttpServletResponse response) {

		if (!mayProceed(request)) {
			try {
				response.sendRedirect(request.getContextPath() + "/MainMenu?message=authentication_failed");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		ResourceBundleProvider.updateLocale(request.getLocale());
		// Reseting the side info panel set by SecureControler Mantis Issue: 8680.
		// Todo need something to reset panel from all the Spring Controllers
		StudyInfoPanel panel = new StudyInfoPanel();
		panel.reset();
		HttpSession session = request.getSession();
		request.getSession().setAttribute("panel", panel);

		ModelMap gridMap = new ModelMap();
		// set up request attributes for sidebar
		// Not necessary when using old page design...
		// setUpSidebar(request);
		boolean showMoreLink = false;
		if (session.getAttribute("tableFacadeRestore") != null && session.getAttribute("tableFacadeRestore") == "false") {
			session.setAttribute("tableFacadeRestore", "true");
			session.setAttribute("sSdvRestore", "false");
			showMoreLink = true;
		} else if (request.getParameter("showMoreLink") != null) {
			showMoreLink = Boolean.parseBoolean(request.getParameter("showMoreLink").toString());
		} else if (session.getAttribute("sdv_showMoreLink") != null) {
			showMoreLink = Boolean.parseBoolean(session.getAttribute("sdv_showMoreLink") + "");
		} else {
			showMoreLink = true;
		}
		request.setAttribute("showMoreLink", showMoreLink + "");
		session.setAttribute("sdv_showMoreLink", showMoreLink + "");
		request.setAttribute("studyId", studyId);
		String restore = (String) request.getAttribute("sdv_restore");
		restore = restore != null && restore.length() > 0 ? restore : "false";
		request.setAttribute("sdv_restore", restore);
		// request.setAttribute("imagePathPrefix","../");
		// We need a study subject id for the first tab;
		Integer studySubjectId = (Integer) request.getAttribute("studySubjectId");
		studySubjectId = studySubjectId == null || studySubjectId == 0 ? 0 : studySubjectId;
		request.setAttribute("studySubjectId", studySubjectId);
		request.setAttribute("showBackButton", request.getParameter("sbb") != null);

		// set up the elements for the view's filter box
		// sdvUtil.prepareSDVSelectElements(request,studyBean);

		ArrayList<String> pageMessages = (ArrayList<String>) request.getAttribute("pageMessages");
		if (pageMessages == null) {
			pageMessages = new ArrayList<String>();
		}

		request.setAttribute("pageMessages", pageMessages);

		String sdvMatrix = sdvUtil.renderEventCRFTableWithLimit(request, studyId, "../");

		gridMap.addAttribute(SUBJECT_SDV_TABLE_ATTRIBUTE, sdvMatrix);
		return gridMap;
	}

	@RequestMapping("/viewAllSubjectSDVform")
	public ModelMap viewAllSubjectFormHandler(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("studyId") int studyId) {

		ModelMap gridMap = new ModelMap();
		String pattern = "MM/dd/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		// set up the parameters to take part in filtering
		ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(new SdvFilterDataBean());
		dataBinder
				.setAllowedFields(new String[] { "study_subject_id", "studyEventDefinition", "studyEventStatus",
						"eventCRFStatus", "sdvRequirement", "eventcrfSDVStatus", "startUpdatedDate", "endDate",
						"eventCRFName" });

		dataBinder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(sdf, true));
		dataBinder.bind(request);

		// set up request attributes for sidebar
		// Not necessary when using old page design...
		// setUpSidebar(request);

		request.setAttribute("studyId", studyId);

		ArrayList<String> pageMessages = (ArrayList<String>) request.getAttribute("pageMessages");
		if (pageMessages == null) {
			pageMessages = new ArrayList<String>();
		}

		request.setAttribute("pageMessages", pageMessages);
		String sdvMatrix = sdvUtil.renderEventCRFTableWithLimit(request, studyId, "");
		gridMap.addAttribute(SUBJECT_SDV_TABLE_ATTRIBUTE, sdvMatrix);
		return gridMap;
	}

	// method = RequestMethod.POST
	@RequestMapping("/handleSDVPost")
	public String sdvAllSubjectsFormHandler(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("studyId") int studyId, @RequestParam("redirection") String redirection, ModelMap model) {

		// The application is POSTing parameters with the name "sdvCheck_" plus the
		// Event CRF id, so the parameter is sdvCheck_534.

		ResourceBundle resPageMessages = ResourceBundleProvider.getPageMessagesBundle(request.getLocale());

		Enumeration paramNames = request.getParameterNames();
		Map<String, String> parameterMap = new HashMap<String, String>();
		String tmpName = "";
		for (; paramNames.hasMoreElements();) {
			tmpName = (String) paramNames.nextElement();
			if (tmpName.contains(SDVUtil.CHECKBOX_NAME)) {
				parameterMap.put(tmpName, request.getParameter(tmpName));
			}
		}
		request.setAttribute("sdv_restore", "true");

		// For the messages that appear in the left column of the results page
		ArrayList<String> pageMessages = new ArrayList<String>();

		// In this case, no checked event CRFs were submitted
		if (parameterMap.isEmpty()) {
			pageMessages.add(resPageMessages.getString("none_event_crf_selected_sdv"));
			request.setAttribute("pageMessages", pageMessages);
			sdvUtil.forwardRequestFromController(request, response, "/pages/" + redirection);

		}
		List<Integer> eventCRFIds = sdvUtil.getListOfSdvEventCRFIds(parameterMap.keySet());
		boolean updateCRFs = sdvUtil.setSDVerified(eventCRFIds, getCurrentUser(request).getId(), true);

		if (updateCRFs) {
			pageMessages.add(resPageMessages.getString("event_crf_sdved"));
		} else {
			pageMessages.add(resPageMessages.getString("sdv_database_problem"));
		}
		request.setAttribute("pageMessages", pageMessages);

		sdvUtil.forwardRequestFromController(request, response, "/pages/" + redirection);

		// The name of the view, as in allSdvResult.jsp
		return null;

	}

	@RequestMapping("/handleSDVGet")
	public String sdvOneCRFFormHandler(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("crfId") int crfId, @RequestParam("redirection") String redirection, ModelMap model) {

		ResourceBundle resPageMessages = ResourceBundleProvider.getPageMessagesBundle(request.getLocale());

		if (!mayProceed(request)) {
			try {
				response.sendRedirect(request.getContextPath() + "/MainMenu?message=authentication_failed");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		// For the messages that appear in the left column of the results page
		ArrayList<String> pageMessages = new ArrayList<String>();

		List<Integer> eventCRFIds = new ArrayList<Integer>();
		eventCRFIds.add(crfId);
		boolean updateCRFs = sdvUtil.setSDVerified(eventCRFIds, getCurrentUser(request).getId(), true);

		if (updateCRFs) {
			pageMessages.add(resPageMessages.getString("event_crf_sdved"));
		} else {
			pageMessages.add(resPageMessages.getString("sdv_database_problem"));
		}
		request.setAttribute("pageMessages", pageMessages);

		request.setAttribute("sdv_restore", "true");

		sdvUtil.forwardRequestFromController(request, response, "/pages/" + redirection);

		// The name of the view, as in allSdvResult.jsp
		return null;

	}

	@RequestMapping("/handleSDVRemove")
	public String changeSDVHandler(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("crfId") int crfId, @RequestParam("redirection") String redirection, ModelMap model) {

		ResourceBundle resPageMessages = ResourceBundleProvider.getPageMessagesBundle(request.getLocale());

		// For the messages that appear in the left column of the results page
		ArrayList<String> pageMessages = new ArrayList<String>();

		List<Integer> eventCRFIds = new ArrayList<Integer>();
		eventCRFIds.add(crfId);
		boolean updateCRFs = sdvUtil.setSDVerified(eventCRFIds, getCurrentUser(request).getId(), false);

		if (updateCRFs) {
			pageMessages.add(resPageMessages.getString("unset_event_crf_sdv"));
		} else {
			pageMessages.add(resPageMessages.getString("sdv_database_problem"));
		}
		request.setAttribute("pageMessages", pageMessages);
		request.setAttribute("sdv_restore", "true");

		sdvUtil.forwardRequestFromController(request, response, "/pages/" + redirection);

		// The name of the view, as in allSdvResult.jsp
		return null;

	}

	@RequestMapping("/sdvStudySubject")
	public String sdvStudySubjectHandler(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("theStudySubjectId") int studySubjectId, @RequestParam("redirection") String redirection,
			ModelMap model) {

		ResourceBundle resPageMessages = ResourceBundleProvider.getPageMessagesBundle(request.getLocale());

		// For the messages that appear in the left column of the results page
		ArrayList<String> pageMessages = new ArrayList<String>();

		List<Integer> studySubjectIds = new ArrayList<Integer>();
		studySubjectIds.add(studySubjectId);
		boolean updateCRFs = sdvUtil.setSDVStatusForStudySubjects(studySubjectIds, getCurrentUser(request).getId(),
				isSdvWithOpenQueriesAllowed(request), true);

		if (updateCRFs) {
			pageMessages.add(resPageMessages.getString("subject_sdved"));
		} else {
			pageMessages.add(resPageMessages.getString("sdv_database_problem"));
		}
		request.setAttribute("pageMessages", pageMessages);
		request.setAttribute("s_sdv_restore", "true");
		sdvUtil.forwardRequestFromController(request, response, "/pages/" + redirection);
		return null;
	}

	@RequestMapping("/unSdvStudySubject")
	public String unSdvStudySubjectHandler(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("theStudySubjectId") int studySubjectId, @RequestParam("redirection") String redirection,
			ModelMap model) {

		ResourceBundle resPageMessages = ResourceBundleProvider.getPageMessagesBundle(request.getLocale());

		ArrayList<String> pageMessages = new ArrayList<String>();
		List<Integer> studySubjectIds = new ArrayList<Integer>();

		studySubjectIds.add(studySubjectId);
		boolean updateCRFs = sdvUtil.setSDVStatusForStudySubjects(studySubjectIds, getCurrentUser(request).getId(), true,
				false);

		if (updateCRFs) {
			pageMessages.add(resPageMessages.getString("unset_event_crf_sdv"));
		} else {
			pageMessages.add(resPageMessages.getString("sdv_database_problem"));
		}
		request.setAttribute("pageMessages", pageMessages);
		request.setAttribute("s_sdv_restore", "true");
		sdvUtil.forwardRequestFromController(request, response, "/pages/" + redirection);
		return null;

	}

	@RequestMapping("/sdvStudySubjects")
	public String sdvStudySubjectsHandler(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("studyId") int studyId, @RequestParam("redirection") String redirection, ModelMap model) {

		// The application is POSTing parameters with the name "sdvCheck_" plus the
		// Event CRF id, so the parameter is sdvCheck_534.

		ResourceBundle resPageMessages = ResourceBundleProvider.getPageMessagesBundle(request.getLocale());

		Enumeration paramNames = request.getParameterNames();
		Map<String, String> parameterMap = new HashMap<String, String>();
		String tmpName = "";
		for (; paramNames.hasMoreElements();) {
			tmpName = (String) paramNames.nextElement();
			if (tmpName.contains(SDVUtil.CHECKBOX_NAME)) {
				parameterMap.put(tmpName, request.getParameter(tmpName));
			}
		}
		request.setAttribute("s_sdv_restore", "true");

		// For the messages that appear in the left column of the results page
		ArrayList<String> pageMessages = new ArrayList<String>();

		// In this case, no checked event CRFs were submitted
		if (parameterMap.isEmpty()) {
			pageMessages.add(resPageMessages.getString("none_subjects_selected_sdv"));
			request.setAttribute("pageMessages", pageMessages);
			sdvUtil.forwardRequestFromController(request, response, "/pages/" + redirection);

		}
		
		List<Integer> studySubjectIds = sdvUtil.getListOfStudySubjectIds(parameterMap.keySet());
		boolean updateCRFs = sdvUtil.setSDVStatusForStudySubjects(studySubjectIds, getCurrentUser(request).getId(),
				isSdvWithOpenQueriesAllowed(request), true);

		if (updateCRFs) {
			pageMessages.add(resPageMessages.getString("event_crf_sdved"));
		} else {
			pageMessages.add(resPageMessages.getString("sdv_database_problem"));
		}
		request.setAttribute("pageMessages", pageMessages);

		sdvUtil.forwardRequestFromController(request, response, "/pages/" + redirection);

		// The name of the view, as in allSdvResult.jsp
		return null;

	}

	private boolean isSdvWithOpenQueriesAllowed(HttpServletRequest request) {
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		String allowSdvWithOpenQueries = currentStudy == null? "" : currentStudy.getStudyParameterConfig()
				.getAllowSdvWithOpenQueries();
		return !"no".equals(allowSdvWithOpenQueries);
	}

	private UserAccountBean getCurrentUser(HttpServletRequest request) {
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute("userBean");
		return ub;
	}

	public static void main(String[] args) throws ParseException {

		String pattern = "MM/dd/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		Date date = sdf.parse("01/01/2007");
		System.out.println("date = " + date);

	}

	private boolean mayProceed(HttpServletRequest request) {

		StudyUserRoleBean currentRole = (StudyUserRoleBean) request.getSession().getAttribute("userRole");
		Role r = currentRole.getRole();
		if (Role.SYSTEM_ADMINISTRATOR.equals(r) || Role.STUDY_DIRECTOR.equals(r) || Role.STUDY_ADMINISTRATOR.equals(r)
				|| Role.STUDY_MONITOR.equals(r)) {
			return true;
		}
		return false;
	}

	@ExceptionHandler(Exception.class)
	public String handleException(Exception ex) {
		ex.printStackTrace();
		return "redirect:/MainMenu";
	}
}
