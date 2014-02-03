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

package org.akaza.openclinica.control.core;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.service.StudyParameterConfig;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.EnterDataForStudyEventServlet;
import org.akaza.openclinica.control.submit.ListStudySubjectsServlet;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.navigation.Navigation;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.view.BreadcrumbTrail;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.view.StudyInfoPanelLine;
import org.akaza.openclinica.web.InconsistentStateException;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Abstract class for creating a controller servlet and extending capabilities of Controller. However, not using the
 * SingleThreadModel.
 * 
 * @author jnyayapathi
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public abstract class Controller extends BaseController {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	protected void addPageMessage(String message, HttpServletRequest request) {
		ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);

		if (pageMessages == null) {
			pageMessages = new ArrayList();
		}

		if (!pageMessages.contains(message)) {
			pageMessages.add(message);
		}
		logger.debug(message);
		request.setAttribute(PAGE_MESSAGE, pageMessages);
	}

	protected void storePageMessages(HttpServletRequest request) {
		Map storedAttributes = new HashMap();
		storedAttributes.put(PAGE_MESSAGE, request.getAttribute(PAGE_MESSAGE));
		request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
	}

	protected void setToPanel(String title, String info, HttpServletRequest request) {
		StudyInfoPanel panel = getStudyInfoPanel(request);
		if (panel.isOrderedData()) {
			ArrayList data = panel.getUserOrderedData();
			data.add(new StudyInfoPanelLine(title, info));
			panel.setUserOrderedData(data);
		} else {
			panel.setData(title, info);
		}
		request.setAttribute(STUDY_INFO_PANEL, panel);
	}

	protected void setInputMessages(HashMap messages, HttpServletRequest request) {
		request.setAttribute(INPUT_MESSAGES, messages);
	}

	protected void setPresetValues(HashMap presetValues, HttpServletRequest request) {
		request.setAttribute(PRESET_VALUES, presetValues);
	}

	protected void setTable(EntityBeanTable table, HttpServletRequest request) {
		request.setAttribute(BEAN_TABLE, table);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/**
	 * Process request
	 * 
	 * @throws Exception
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

	protected abstract void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException;

	public static final String USER_BEAN_NAME = "userBean";

	public void passwdTimeOut(HttpServletRequest request, HttpServletResponse response) {
		UserAccountBean ub = getUserAccountBean(request);
		Date lastChangeDate = ub.getPasswdTimestamp();
		if (lastChangeDate == null) {
			addPageMessage(respage.getString("welcome") + " " + ub.getFirstName() + " " + ub.getLastName() + ". "
					+ respage.getString("password_set"), request);
			int pwdChangeRequired = Integer.parseInt(SQLInitServlet.getField("pwd.change.required"));
			if (pwdChangeRequired == 1) {
				request.setAttribute("mustChangePass", "yes");
				forwardPage(Page.RESET_PASSWORD, request, response);
			}
		}
	}

	private void pingJobServer(HttpServletRequest request) {
		String jobName = (String) request.getSession().getAttribute("jobName");
		String groupName = (String) request.getSession().getAttribute("groupName");
		Integer datasetId = (Integer) request.getSession().getAttribute("datasetId");
		try {
			if (jobName != null && groupName != null) {
				logger.info("trying to retrieve status on " + jobName + " " + groupName);
				Trigger.TriggerState state = getStdScheduler().getTriggerState(
						TriggerKey.triggerKey(jobName, groupName));
				logger.info("found state: " + state);
				org.quartz.JobDetail details = getStdScheduler().getJobDetail(JobKey.jobKey(jobName, groupName));
				org.quartz.JobDataMap dataMap = details.getJobDataMap();
				String failMessage = dataMap.getString("failMessage");
				if (state == Trigger.TriggerState.NONE || state == Trigger.TriggerState.COMPLETE) {
					// add the message here that your export is done
					logger.info("adding a message!");
					// TODO make absolute paths in the message, for example a link from /pages/* would break
					// TODO i18n
					if (failMessage != null) {
						// The extract data job failed with the message:
						// ERROR: relation "demographics" already exists
						// More information may be available in the log files.
						addPageMessage("The extract data job failed with the message: <br/><br/>" + failMessage
								+ "<br/><br/>More information may be available in the log files.", request);
						request.getSession().removeAttribute("jobName");
						request.getSession().removeAttribute("groupName");
						request.getSession().removeAttribute("datasetId");
					} else {
						String successMsg = dataMap.getString("SUCCESS_MESSAGE");
						String success = dataMap.getString("successMsg");
						if (success != null) {

							if (successMsg.contains("$linkURL")) {
								successMsg = decodeLINKURL(datasetId);
							}

							if (successMsg != null && !successMsg.isEmpty()) {
								addPageMessage(successMsg, request);
							} else {
								addPageMessage(
										"Your Extract is now completed. Please go to review it <a href='ExportDataset?datasetId="
												+ datasetId + "'>here</a>.", request);
							}
							request.getSession().removeAttribute("jobName");
							request.getSession().removeAttribute("groupName");
							request.getSession().removeAttribute("datasetId");
						}
					}
				}
			}
		} catch (SchedulerException se) {
			se.printStackTrace();
		}

	}

	private String decodeLINKURL(Integer datasetId) {

		String successMsg = "";

		ArchivedDatasetFileDAO asdfDAO = getArchivedDatasetFileDAO();

		ArrayList<ArchivedDatasetFileBean> fileBeans = asdfDAO.findByDatasetId(datasetId);

		if (fileBeans.size() > 0) {
			successMsg = successMsg.replace("$linkURL", "<a href=\"" + SQLInitServlet.getSystemURL()
					+ "AccessFile?fileId=" + fileBeans.get(0).getId() + "\">here</a>");
		}

		return successMsg;
	}

	static void reloadUserBean(HttpSession session, UserAccountDAO userAccountDao) {
		if (session.getAttribute("reloadUserBean") != null) {
			UserAccountBean userAccountBean = (UserAccountBean) session.getAttribute(USER_BEAN_NAME);
			session.setAttribute(USER_BEAN_NAME, userAccountDao.findByUserName(userAccountBean.getName()));
			session.removeAttribute("study");
			session.removeAttribute("userRole");
			session.removeAttribute("reloadUserBean");
		}
	}

	private void process(HttpServletRequest request, HttpServletResponse response) throws OpenClinicaException,
			UnsupportedEncodingException {
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		reloadUserBean(session, getUserAccountDAO());
		String newThemeColor = CoreResources.getField("themeColor");
		session.setAttribute("newThemeColor", newThemeColor);
		ApplicationContext applicationContext = SpringServletAccess.getApplicationContext(getServletContext());
		try {
			session.setMaxInactiveInterval(Integer.parseInt(SQLInitServlet.getField("max_inactive_interval")));
		} catch (NumberFormatException nfe) {
			// 3600 is the datainfo.properties maxInactiveInterval on
			session.setMaxInactiveInterval(3600);
		}

		// If the session already has a value with key SUPPORT_URL don't reset
		if (session.getAttribute(SUPPORT_URL) == null) {
			session.setAttribute(SUPPORT_URL, SQLInitServlet.getSupportURL());
		}

		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		// Set current language preferences
		Locale locale = request.getLocale();
		ResourceBundleProvider.updateLocale(locale);
		resadmin = ResourceBundleProvider.getAdminBundle(locale);
		resaudit = ResourceBundleProvider.getAuditEventsBundle(locale);
		resexception = ResourceBundleProvider.getExceptionsBundle(locale);
		resformat = ResourceBundleProvider.getFormatBundle(locale);
		restext = ResourceBundleProvider.getTextsBundle(locale);
		resterm = ResourceBundleProvider.getTermsBundle(locale);
		resword = ResourceBundleProvider.getWordsBundle(locale);
		respage = ResourceBundleProvider.getPageMessagesBundle(locale);
		resworkflow = ResourceBundleProvider.getWorkflowBundle(locale);

		initMaps();
		getErrorsHolder(request);

		try {
			String userName = request.getRemoteUser();
			SessionManager sm = new SessionManager(ub, userName, applicationContext);
			request.setAttribute(SESSION_MANAGER, sm);

			ub = sm.getUserBean();
			request.getSession().setAttribute(USER_BEAN_NAME, ub);

			String includeReportingVar = "includeReporting";
			if (!SQLInitServlet.getField("pentaho.url").trim().equals("")) {
				request.setAttribute(includeReportingVar, true);
			} else {
				request.setAttribute(includeReportingVar, false);
			}

			StudyDAO sdao = getStudyDAO();
			if (currentStudy == null || currentStudy.getId() <= 0) {
				if (ub.getId() > 0 && ub.getActiveStudyId() > 0) {
					StudyParameterValueDAO spvdao = getStudyParameterValueDAO();
					currentStudy = (StudyBean) sdao.findByPK(ub.getActiveStudyId());

					ArrayList studyParameters = spvdao.findParamConfigByStudy(currentStudy);

					currentStudy.setStudyParameters(studyParameters);

					StudyConfigService scs = getStudyConfigService();
					if (currentStudy.getParentStudyId() <= 0) {// top study
						scs.setParametersForStudy(currentStudy);

					} else {
						currentStudy.setParentStudyName((sdao.findByPK(currentStudy.getParentStudyId())).getName());
						scs.setParametersForSite(currentStudy);
					}

					// set up the panel here
					StudyInfoPanel panel = getStudyInfoPanel(request);
					panel.reset();
				} else {
					currentStudy = new StudyBean();
				}
				session.setAttribute("study", currentStudy);
			} else if (currentStudy.getId() > 0) {
				if (currentStudy.getParentStudyId() > 0) {
					currentStudy.setParentStudyName((sdao.findByPK(currentStudy.getParentStudyId())).getName());
				}
			}

			if (this instanceof ListStudySubjectsServlet && currentStudy != null
					&& currentStudy.getStatus() != Status.AVAILABLE) {
				String startWith = BR;
				ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
				if (pageMessages == null) {
					startWith = "";
					pageMessages = new ArrayList();
				}
				pageMessages.add(startWith + resword.getString(STUDY_SHOUD_BE_IN_AVAILABLE_MODE) + BR);
				request.setAttribute(PAGE_MESSAGE, pageMessages);
			}

			Role.prepareRoleMapWithDescriptions(resterm);

			if (currentRole == null || currentRole.getId() <= 0) {
				// if current study has been "removed", current role will be
				// kept as "invalid"
				if (ub.getId() > 0 && currentStudy != null && currentStudy.getId() > 0
						&& !currentStudy.getStatus().getName().equals("removed")) {
					currentRole = ub.getRoleByStudy(currentStudy.getId());
					if (currentStudy.getParentStudyId() > 0) {
						// Checking if currentStudy has been removed or not will
						// ge good enough
						StudyUserRoleBean roleInParent = ub.getRoleByStudy(currentStudy.getParentStudyId());
						// inherited role from parent study, pick the higher
						// role
						currentRole.setRole(Role.max(currentRole.getRole(), roleInParent.getRole()));
					}
				} else {
					currentRole = new StudyUserRoleBean();
				}
				session.setAttribute("userRole", currentRole);
			}
			// For the case that current role is not "invalid" but current
			// active study has been removed.
			else if (currentRole.getId() > 0
					&& currentStudy != null
					&& (currentStudy.getStatus().equals(Status.DELETED) || currentStudy.getStatus().equals(
							Status.AUTO_DELETED))) {
				currentRole.setRole(Role.INVALID);
				currentRole.setStatus(Status.DELETED);
				session.setAttribute("userRole", currentRole);
			}

			request.setAttribute("isAdminServlet", getAdminServlet(request));

			if (!request.getRequestURI().endsWith("ResetPassword")) {
				passwdTimeOut(request, response);
			}
			mayProceed(request, response);
			pingJobServer(request);

			long startTime = System.currentTimeMillis();

			processRequest(request, response);

			long endTime = System.currentTimeMillis();
			long reportTime = (endTime - startTime) / 1000;
			logger.info("Time taken [" + reportTime + " seconds]");
			// If the time taken is over 5 seconds, write it to the stats table
			if (reportTime > 5) {
				getUsageStatsServiceDAO().savePageLoadTimeToDB(this.getClass().toString(), Long.toString(reportTime));
			}
			// Call the usagestats dao here and record a time in the db
		} catch (InconsistentStateException ise) {
			ise.printStackTrace();
			logger.warn("InconsistentStateException: org.akaza.openclinica.control.Controller: " + ise.getMessage());
			if (request.getAttribute("event") != null && request.getAttribute("event") instanceof EventCRFBean)
				Controller.justRemoveLockedCRF(((EventCRFBean) request.getAttribute("event")).getId());
			addPageMessage(ise.getOpenClinicaMessage(), request);
			forwardPage(ise.getGoTo(), request, response);
		} catch (InsufficientPermissionException ipe) {
			ipe.printStackTrace();
			logger.warn("InsufficientPermissionException: org.akaza.openclinica.control.Controller: "
					+ ipe.getMessage());
			if (request.getAttribute("event") != null && request.getAttribute("event") instanceof EventCRFBean)
				Controller.justRemoveLockedCRF(((EventCRFBean) request.getAttribute("event")).getId());
			forwardPage(ipe.getGoTo(), request, response);
		} catch (OutOfMemoryError ome) {
			ome.printStackTrace();
			long heapSize = Runtime.getRuntime().totalMemory();
			logger.error("OutOfMemory Exception - " + heapSize);
			if (request.getAttribute("event") != null && request.getAttribute("event") instanceof EventCRFBean)
				Controller.justRemoveLockedCRF(((EventCRFBean) request.getAttribute("event")).getId());
			session.setAttribute("ome", "yes");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(getStackTrace(e));
			if (request.getAttribute("event") != null && request.getAttribute("event") instanceof EventCRFBean)
				Controller.justRemoveLockedCRF(((EventCRFBean) request.getAttribute("event")).getId());
			forwardPage(Page.ERROR, request, response);
		}
	}

	private void initMaps() {
		facRecruitStatusMap.put("not_yet_recruiting", resadmin.getString("not_yet_recruiting"));
		facRecruitStatusMap.put("recruiting", resadmin.getString("recruiting"));
		facRecruitStatusMap.put("no_longer_recruiting", resadmin.getString("no_longer_recruiting"));
		facRecruitStatusMap.put("completed", resadmin.getString("completed"));
		facRecruitStatusMap.put("suspended", resadmin.getString("suspended"));
		facRecruitStatusMap.put("terminated", resadmin.getString("terminated"));

		studyPhaseMap.put("n_a", resadmin.getString("n_a"));
		studyPhaseMap.put("phaseI", resadmin.getString("phaseI"));
		studyPhaseMap.put("phaseI_II", resadmin.getString("phaseI_II"));
		studyPhaseMap.put("phaseII", resadmin.getString("phaseII"));
		studyPhaseMap.put("phaseII_III", resadmin.getString("phaseII_III"));
		studyPhaseMap.put("phaseIII", resadmin.getString("phaseIII"));
		studyPhaseMap.put("phaseIII_IV", resadmin.getString("phaseIII_IV"));
		studyPhaseMap.put("phaseIV", resadmin.getString("phaseIV"));

		interPurposeMap.put("treatment", resadmin.getString("treatment"));
		interPurposeMap.put("prevention", resadmin.getString("prevention"));
		interPurposeMap.put("diagnosis", resadmin.getString("diagnosis"));
		// interPurposeMap.put("educ_couns_train",
		// resadmin.getString("educ_couns_train"));
		interPurposeMap.put("supportive_care", resadmin.getString("supportive_care"));
		interPurposeMap.put("screening", resadmin.getString("screening"));
		interPurposeMap.put("health_services_research", resadmin.getString("health_services_research"));
		interPurposeMap.put("basic_science", resadmin.getString("basic_science"));
		interPurposeMap.put("other", resadmin.getString("other"));

		allocationMap.put("randomized", resadmin.getString("randomized"));
		allocationMap.put("non_randomized", resadmin.getString("non_randomized"));
		allocationMap.put("n_a", resadmin.getString("n_a"));

		maskingMap.put("open", resadmin.getString("open"));
		maskingMap.put("single_blind", resadmin.getString("single_blind"));
		maskingMap.put("double_blind", resadmin.getString("double_blind"));

		controlMap.put("placebo", resadmin.getString("placebo"));
		controlMap.put("active", resadmin.getString("active"));
		controlMap.put("uncontrolled", resadmin.getString("uncontrolled"));
		controlMap.put("historical", resadmin.getString("historical"));
		controlMap.put("dose_comparison", resadmin.getString("dose_comparison"));

		assignmentMap.put("single_group", resadmin.getString("single_group"));
		assignmentMap.put("parallel", resadmin.getString("parallel"));
		assignmentMap.put("cross_over", resadmin.getString("cross_over"));
		assignmentMap.put("factorial", resadmin.getString("factorial"));
		assignmentMap.put("expanded_access", resadmin.getString("expanded_access"));

		endpointMap.put("safety", resadmin.getString("safety"));
		endpointMap.put("efficacy", resadmin.getString("efficacy"));
		endpointMap.put("safety_efficacy", resadmin.getString("safety_efficacy"));
		endpointMap.put("bio_equivalence", resadmin.getString("bio_equivalence"));
		endpointMap.put("bio_availability", resadmin.getString("bio_availability"));
		endpointMap.put("pharmacokinetics", resadmin.getString("pharmacokinetics"));
		endpointMap.put("pharmacodynamics", resadmin.getString("pharmacodynamics"));
		endpointMap.put("pharmacokinetics_pharmacodynamics", resadmin.getString("pharmacokinetics_pharmacodynamics"));

		interTypeMap.put("drug", resadmin.getString("drug"));
		interTypeMap.put("gene_transfer", resadmin.getString("gene_transfer"));
		interTypeMap.put("vaccine", resadmin.getString("vaccine"));
		interTypeMap.put("behavior", resadmin.getString("behavior"));
		interTypeMap.put("device", resadmin.getString("device"));
		interTypeMap.put("procedure", resadmin.getString("procedure"));
		interTypeMap.put("other", resadmin.getString("other"));

		obserPurposeMap.put("natural_history", resadmin.getString("natural_history"));
		obserPurposeMap.put("screening", resadmin.getString("screening"));
		obserPurposeMap.put("psychosocial", resadmin.getString("psychosocial"));

		selectionMap.put("convenience_sample", resadmin.getString("convenience_sample"));
		selectionMap.put("defined_population", resadmin.getString("defined_population"));
		selectionMap.put("random_sample", resadmin.getString("random_sample"));
		selectionMap.put("case_control", resadmin.getString("case_control"));

		timingMap.put("retrospective", resadmin.getString("retrospective"));
		timingMap.put("prospective", resadmin.getString("prospective"));
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		if (request.getMethod().equalsIgnoreCase("GET")) {
			doGet(request, response);
		} else if (request.getMethod().equalsIgnoreCase("POST")) {
			doPost(request, response);
		}
	}

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws ServletException
	 * @throws java.io.IOException
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			java.io.IOException {
		try {
			Navigation.addToNavigationStack(request);
			logger.debug("Request");
			process(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			if (request.getAttribute("event") != null && request.getAttribute("event") instanceof EventCRFBean)
				Controller.justRemoveLockedCRF(((EventCRFBean) request.getAttribute("event")).getId());
		}
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			java.io.IOException {
		try {
			Navigation.addToNavigationStack(request);
			logger.debug("Post");
			process(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			if (request.getAttribute("event") != null && request.getAttribute("event") instanceof EventCRFBean)
				Controller.justRemoveLockedCRF(((EventCRFBean) request.getAttribute("event")).getId());
		}
	}

	/**
	 * <P>
	 * Forwards to a jsp page. Additions to the forwardPage() method involve checking the session for the bread crumb
	 * trail and setting it, if necessary. Setting it here allows the developer to only have to update the
	 * <code>BreadcrumbTrail</code> class.
	 * 
	 * @param jspPage
	 *            The page to go to.
	 * @param checkTrail
	 *            The command to check for, and set a trail in the session.
	 */
	protected void forwardPage(Page jspPage, boolean checkTrail, HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", -1);
		response.setHeader("Cache-Control", "no-store");

		if (request.getAttribute(POP_UP_URL) == null) {
			request.setAttribute(POP_UP_URL, "");
		}

		try {
			if (checkTrail) {
				BreadcrumbTrail bt = new BreadcrumbTrail();
				if (request.getSession() != null) {// added bu jxu, fixed bug for log out
					ArrayList trail = (ArrayList) request.getSession().getAttribute("trail");
					if (trail == null) {
						trail = bt.generateTrail(jspPage, request);
					} else {
						bt.setTrail(trail);
						trail = bt.generateTrail(jspPage, request);
					}
					request.getSession().setAttribute("trail", trail);
					StudyInfoPanel panel = getStudyInfoPanel(request);
					panel.setData(jspPage, request.getSession(), request);
				}
				// we are also using checkTrail to update the panel, tbh
			}
			getServletContext().getRequestDispatcher(jspPage.getFileName()).forward(request, response);
		} catch (Exception se) {
			if ("View Notes".equals(jspPage.getTitle())) {
				String viewNotesURL = jspPage.getFileName();
				if (viewNotesURL != null && viewNotesURL.contains("listNotes_p_=")) {
					String[] ps = viewNotesURL.split("listNotes_p_=");
					String t = ps[1].split("&")[0];
					int p = t.length() > 0 ? Integer.parseInt(t) : -1;
					if (p > 1) {
						viewNotesURL = viewNotesURL.replace("listNotes_p_=" + p, "listNotes_p_=" + (p - 1));
						forwardPage(Page.setNewPage(viewNotesURL, "View Notes"), request, response);
					} else if (p <= 0) {
						forwardPage(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY, request, response);
					}
				}
			}
			se.printStackTrace();
		}

	}

	protected void forwardPage(Page jspPage, HttpServletRequest request, HttpServletResponse response) {
		this.forwardPage(jspPage, true, request, response);
	}

	/**
	 * This method supports functionality of the type
	 * "if a list of entities is empty, then jump to some page and display an error message." This prevents users from
	 * seeing empty drop-down lists and being given error messages when they can't choose an entity from the drop-down
	 * list. Use, e.g.:
	 * <code>addEntityList("groups", allGroups, "There are no groups to display, so you cannot add a subject to this Study.",
	 * Page.SUBMIT_DATA)</code>
	 * 
	 * @param beanName
	 *            The name of the entity list as it should be stored in the request object.
	 * @param list
	 *            The Collection of entities.
	 * @param messageIfEmpty
	 *            The message to display if the collection is empty.
	 * @param destinationIfEmpty
	 *            The Page to go to if the collection is empty.
	 * @throws InconsistentStateException
	 */
	protected void addEntityList(String beanName, Collection list, String messageIfEmpty, Page destinationIfEmpty,
			HttpServletRequest request, HttpServletResponse response) throws InconsistentStateException {
		if (list.isEmpty()) {
			throw new InconsistentStateException(destinationIfEmpty, messageIfEmpty);
		}

		request.setAttribute(beanName, list);
	}

	/**
	 * @return A blank String if this servlet is not an Administer System servlet. Controller.ADMIN_SERVLET_CODE
	 *         otherwise.
	 * @param request
	 *            HttpServletRequest
	 */
	protected String getAdminServlet(HttpServletRequest request) {
		return "";
	}

	protected void setPopUpURL(HttpServletRequest request, String url) {
		if (url != null && request != null) {
			request.setAttribute(POP_UP_URL, url);
			request.setAttribute("hasPopUp", 1);
			logger.info("just set pop up url: " + url);
		}
	}

	/**
	 * <p>
	 * Check if an entity with passed entity id is included in studies of current user.
	 * </p>
	 * 
	 * <p>
	 * Note: This method called AuditableEntityDAO.findByPKAndStudy which required "The subclass must define
	 * findByPKAndStudyName before calling this method. Otherwise an inactive AuditableEntityBean will be returned."
	 * </p>
	 * 
	 * @param entityId
	 *            int
	 * @param userName
	 *            String
	 * @param adao
	 *            AuditableEntityDAO
	 */
	protected boolean entityIncluded(int entityId, String userName, AuditableEntityDAO adao) {
		StudyDAO sdao = getStudyDAO();
		ArrayList<StudyBean> studies = (ArrayList<StudyBean>) sdao.findAllByUserNotRemoved(userName);
		for (StudyBean study : studies) {
			if (adao.findByPKAndStudy(entityId, study).getId() > 0) {
				return true;
			}
			// Here follow the current logic - study subjects at sites level are
			// visible to parent studies.
			if (study.getParentStudyId() <= 0) {
				ArrayList<StudyBean> sites = (ArrayList<StudyBean>) sdao.findAllByParent(study.getId());
				if (sites.size() > 0) {
					for (StudyBean site : sites) {
						if (adao.findByPKAndStudy(entityId, site).getId() > 0) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public String getRequestURLMinusServletPath(HttpServletRequest request) {
		return request.getRequestURL().toString().replaceAll(request.getServletPath(), "");
	}

	public String getHostPath(HttpServletRequest request) {
		String requestURLMinusServletPath = getRequestURLMinusServletPath(request);
		return requestURLMinusServletPath.substring(0, requestURLMinusServletPath.lastIndexOf("/"));
	}

	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath().replaceAll("/", "");
	}

	/*
	 * To check if the current study is LOCKED
	 */
	public void checkStudyLocked(Page page, String message, HttpServletRequest request, HttpServletResponse response) {
		if (getCurrentStudy(request).getStatus().equals(Status.LOCKED)) {
			addPageMessage(message, request);
			forwardPage(page, request, response);
		}
	}

	public void checkStudyLocked(String url, String message, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (getCurrentStudy(request).getStatus().equals(Status.LOCKED)) {
				addPageMessage(message, request);
				response.sendRedirect(url);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * To check if the current study is FROZEN
	 */

	public void checkStudyFrozen(Page page, String message, HttpServletRequest request, HttpServletResponse response) {
		if (getCurrentStudy(request).getStatus().equals(Status.FROZEN)) {
			addPageMessage(message, request);
			forwardPage(page, request, response);
		}
	}

	public void checkStudyFrozen(String url, String message, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (getCurrentStudy(request).getStatus().equals(Status.FROZEN)) {
				addPageMessage(message, request);
				response.sendRedirect(url);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public ArrayList getEventDefinitionsByCurrentStudy(HttpServletRequest request) {
		StudyBean currentStudy = getCurrentStudy(request);
		StudyDAO studyDAO = getStudyDAO();
		StudyEventDefinitionDAO studyEventDefinitionDAO = getStudyEventDefinitionDAO();
		int parentStudyId = currentStudy.getParentStudyId();
		ArrayList allDefs;
		if (parentStudyId > 0) {
			StudyBean parentStudy = (StudyBean) studyDAO.findByPK(parentStudyId);
			allDefs = studyEventDefinitionDAO.findAllActiveByStudy(parentStudy);
		} else {
			allDefs = studyEventDefinitionDAO.findAllActiveByStudy(currentStudy);
		}
		return allDefs;
	}

	public ArrayList getStudyGroupClassesByCurrentStudy(HttpServletRequest request) {
		getSessionManager(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyDAO studyDAO = getStudyDAO();
		StudyGroupClassDAO studyGroupClassDAO = getStudyGroupClassDAO();
		StudyGroupDAO studyGroupDAO = getStudyGroupDAO();
		int parentStudyId = currentStudy.getParentStudyId();
		ArrayList studyGroupClasses;
		if (parentStudyId > 0) {
			StudyBean parentStudy = (StudyBean) studyDAO.findByPK(parentStudyId);
			studyGroupClasses = studyGroupClassDAO.findAllActiveByStudy(parentStudy);
		} else {
			studyGroupClasses = studyGroupClassDAO.findAllActiveByStudy(currentStudy);
		}

		for (Object studyGroupClass : studyGroupClasses) {
			StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClass;
			ArrayList groups = studyGroupDAO.findAllByGroupClass(sgc);
			sgc.setStudyGroups(groups);
		}

		return studyGroupClasses;

	}

	public ArrayList<StudyGroupClassBean> getDynamicGroupClassesByStudyId(HttpServletRequest request, int studyId) {

		ListIterator it;
		StudyGroupClassDAO studyGroupClassDAO = getStudyGroupClassDAO();
		StudyEventDefinitionDAO studyEventDefinitionDao = getStudyEventDefinitionDAO();
		ArrayList<StudyGroupClassBean> dynamicGroupClasses = studyGroupClassDAO
				.findAllActiveDynamicGroupsByStudyId(studyId);
		for (StudyGroupClassBean dynGroup : dynamicGroupClasses) {
			dynGroup.setEventDefinitions(studyEventDefinitionDao.findAllAvailableAndOrderedByStudyGroupClassId(dynGroup
					.getId()));
		}

		it = dynamicGroupClasses.listIterator();
		while (it.hasNext()) {
			if (((StudyGroupClassBean) it.next()).getEventDefinitions().size() == 0) {
				it.remove();
			}
		}

		Collections.sort(dynamicGroupClasses, StudyGroupClassBean.comparatorForDynGroupClasses);

		return dynamicGroupClasses;
	}

	public ArrayList<StudyGroupClassBean> getDynamicGroupClassesByCurrentStudy(HttpServletRequest request) {
		return getDynamicGroupClassesByStudyId(request, getCurrentStudy(request).getId());
	}

	protected UserDetails getUserDetails() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return (UserDetails) principal;
		} else {
			return null;
		}
	}

	public Boolean sendEmail(String to, String subject, String body, Boolean htmlEmail, Boolean sendMessage,
			HttpServletRequest request) throws Exception {
		return sendEmail(to, EmailEngine.getAdminEmail(), subject, body, htmlEmail,
				respage.getString("your_message_sent_succesfully"), respage.getString("mail_cannot_be_sent_to_admin"),
				sendMessage, request);
	}

	public Boolean sendEmail(String to, String subject, String body, Boolean htmlEmail, HttpServletRequest request)
			throws Exception {
		return sendEmail(to, EmailEngine.getAdminEmail(), subject, body, htmlEmail,
				respage.getString("your_message_sent_succesfully"), respage.getString("mail_cannot_be_sent_to_admin"),
				true, request);
	}

	public Boolean sendEmail(String to, String from, String subject, String body, Boolean htmlEmail,
			HttpServletRequest request) throws Exception {
		return sendEmail(to, from, subject, body, htmlEmail, respage.getString("your_message_sent_succesfully"),
				respage.getString("mail_cannot_be_sent_to_admin"), true, request);
	}

	public Boolean sendEmail(String to, String from, String subject, String body, Boolean htmlEmail,
			String successMessage, String failMessage, Boolean sendMessage, HttpServletRequest request)
			throws Exception {
		Boolean messageSent = true;
		try {
			JavaMailSenderImpl mailSender = getMailSender();
			MimeMessage mimeMessage = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, htmlEmail);
			helper.setFrom(from);
			helper.setTo(processMultipleImailAddresses(to.trim()));
			helper.setSubject(subject);
			helper.setText(body, true);

			mailSender.send(mimeMessage);
			if (successMessage != null && sendMessage) {
				addPageMessage(successMessage, request);
			}
			logger.debug("Email sent successfully on {}", new Date());
		} catch (MailException me) {
			me.printStackTrace();
			if (failMessage != null && sendMessage) {
				addPageMessage(failMessage, request);
			}
			logger.debug("Email could not be sent on {} due to: {}", new Date(), me.toString());
			messageSent = false;
		}
		return messageSent;
	}

	private InternetAddress[] processMultipleImailAddresses(String to) throws MessagingException {
		ArrayList<String> recipientsArray = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(to, ",");
		while (st.hasMoreTokens()) {
			recipientsArray.add(st.nextToken());
		}

		int sizeTo = recipientsArray.size();
		InternetAddress[] addressTo = new InternetAddress[sizeTo];
		for (int i = 0; i < sizeTo; i++) {
			addressTo[i] = new InternetAddress(recipientsArray.get(i));
		}
		return addressTo;

	}

	public void dowloadFile(File f, String contentType, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setHeader("Content-disposition", "attachment; filename=\"" + f.getName() + "\";");
		response.setContentType("text/xml");
		response.setHeader("Pragma", "public");

		ServletOutputStream op = response.getOutputStream();

		DataInputStream in = null;
		try {
			response.setContentType("text/xml");
			response.setHeader("Pragma", "public");
			response.setContentLength((int) f.length());

			byte[] bbuf = new byte[(int) f.length()];
			in = new DataInputStream(new FileInputStream(f));
			int length;
			while ((length = in.read(bbuf)) != -1) {
				op.write(bbuf, 0, length);
			}

			in.close();
			op.flush();
			op.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
			if (op != null) {
				op.close();
			}
		}
	}

	public String getPageServletFileName(HttpServletRequest request) {
		String fileName = request.getServletPath();
		String temp = request.getPathInfo();
		if (temp != null) {
			fileName += temp;
		}
		temp = request.getQueryString();
		if (temp != null && temp.length() > 0) {
			fileName += "?" + temp;
		}
		return fileName;
	}

	public String getPageURL(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		String query = request.getQueryString();
		if (url.length() > 0 && query != null) {
			url += "?" + query;
		}
		return url;
	}

	public DiscrepancyNoteBean getNoteInfo(HttpServletRequest request, DiscrepancyNoteBean note) {

		StudySubjectDAO ssdao = getStudySubjectDAO();
		if ("itemData".equalsIgnoreCase(note.getEntityType())) {
			int itemDataId = note.getEntityId();
			ItemDataDAO iddao = getItemDataDAO();
			ItemDataBean itemData = (ItemDataBean) iddao.findByPK(itemDataId);
			ItemDAO idao = getItemDAO();
			if (StringUtil.isBlank(note.getEntityName())) {
				ItemBean item = (ItemBean) idao.findByPK(itemData.getItemId());
				note.setEntityName(item.getName());
				request.setAttribute("item", item);
			}
			EventCRFDAO ecdao = getEventCRFDAO();
			StudyEventDAO svdao = getStudyEventDAO();

			EventCRFBean ec = (EventCRFBean) ecdao.findByPK(itemData.getEventCRFId());
			StudyEventBean event = (StudyEventBean) svdao.findByPK(ec.getStudyEventId());

			StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
					.findByPK(event.getStudyEventDefinitionId());
			note.setEventName(sed.getName());
			note.setEventStart(event.getDateStarted());

			CRFDAO cdao = getCRFDAO();
			CRFBean crf = cdao.findByVersionId(ec.getCRFVersionId());
			note.setCrfName(crf.getName());
			note.setEventCRFId(ec.getId());

			if (StringUtil.isBlank(note.getSubjectName())) {
				StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(ec.getStudySubjectId());
				note.setSubjectName(ss.getName());
			}

			if (note.getDiscrepancyNoteTypeId() == 0) {
				note.setDiscrepancyNoteTypeId(DiscrepancyNoteType.FAILEDVAL.getId());// default
				// value
			}

		} else if ("eventCrf".equalsIgnoreCase(note.getEntityType())) {
			int eventCRFId = note.getEntityId();
			EventCRFDAO ecdao = getEventCRFDAO();
			StudyEventDAO svdao = getStudyEventDAO();

			EventCRFBean ec = (EventCRFBean) ecdao.findByPK(eventCRFId);
			StudyEventBean event = (StudyEventBean) svdao.findByPK(ec.getStudyEventId());

			StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
					.findByPK(event.getStudyEventDefinitionId());
			note.setEventName(sed.getName());
			note.setEventStart(event.getDateStarted());

			CRFDAO cdao = getCRFDAO();
			CRFBean crf = cdao.findByVersionId(ec.getCRFVersionId());
			note.setCrfName(crf.getName());
			StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(ec.getStudySubjectId());
			note.setSubjectName(ss.getName());
			note.setEventCRFId(ec.getId());

		} else if ("studyEvent".equalsIgnoreCase(note.getEntityType())) {
			int eventId = note.getEntityId();
			StudyEventDAO svdao = getStudyEventDAO();
			StudyEventBean event = (StudyEventBean) svdao.findByPK(eventId);

			StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
					.findByPK(event.getStudyEventDefinitionId());
			note.setEventName(sed.getName());
			note.setEventStart(event.getDateStarted());

			StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(event.getStudySubjectId());
			note.setSubjectName(ss.getName());

		} else if ("studySub".equalsIgnoreCase(note.getEntityType())) {
			int studySubjectId = note.getEntityId();
			StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(studySubjectId);
			note.setSubjectName(ss.getName());

		} else if ("Subject".equalsIgnoreCase(note.getEntityType())) {
			int subjectId = note.getEntityId();
			StudySubjectBean ss = ssdao.findBySubjectIdAndStudy(subjectId, getCurrentStudy(request));
			note.setSubjectName(ss.getName());
		}

		return note;
	}

	public void checkRoleByUserAndStudy(HttpServletRequest request, HttpServletResponse response, UserAccountBean ub,
			int studyId, int siteId) {
		StudyUserRoleBean studyUserRole = ub.getRoleByStudy(studyId);
		StudyUserRoleBean siteUserRole = new StudyUserRoleBean();
		if (siteId != 0) {
			siteUserRole = ub.getRoleByStudy(siteId);
		}
		if (studyUserRole.getRole().equals(Role.INVALID) && siteUserRole.getRole().equals(Role.INVALID)) {
			addPageMessage(
					respage.getString("no_have_correct_privilege_current_study") + " "
							+ respage.getString("change_active_study_or_contact"), request);
			forwardPage(Page.MENU_SERVLET, request, response);
		}
	}

	protected Date getJobStartTime(HashMap errors, FormProcessor fp) {
		Date currDate = new Date();
		Calendar currentDateCalendar = new GregorianCalendar();
		currentDateCalendar.setTime(currDate);

		int h = fp.getInt(JOB_HOUR);
		int m = fp.getInt(JOB_MINUTE);

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(currDate);
		calendar.set(Calendar.HOUR_OF_DAY, h);
		calendar.set(Calendar.MINUTE, m);

		if (!(h >= 0)) {
			List<String> messages = new ArrayList<String>();
			messages.add("Select the start hour, please.");
			errors.put("jobHour", messages);
		} else if (!(m >= 0)) {
			List<String> messages = new ArrayList<String>();
			messages.add("Select the start minute, please.");
			errors.put("jobHour", messages);
		}

		return calendar.getTime();
	}

	public void populateCustomElementsConfig(HttpServletRequest request) {
		StudyBean study = (StudyBean) request.getSession().getAttribute(STUDY);
		if (study != null) {
			StudyParameterConfig config = study.getStudyParameterConfig();

			request.setAttribute("isEndDateUsed", !NOT_USED.equals(config.getEndDateTimeRequired()));
			request.setAttribute("endDateLabel", config.getEndDateTimeLabel());

			request.setAttribute("isStartDateUsed", !NOT_USED.equals(config.getStartDateTimeRequired()));
			request.setAttribute("startDateLabel", config.getStartDateTimeLabel());
		}
	}

	public int getIntById(HashMap h, Integer key) {
		Integer value = (Integer) h.get(key);
		if (value == null) {
			return 0;
		} else {
			return value;
		}
	}

	public String getActionForStage(DataEntryStage stage) {
		if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY)) {
			return ACTION_CONTINUE_INITIAL_DATA_ENTRY;
		}

		else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)) {
			return ACTION_START_DOUBLE_DATA_ENTRY;
		}

		else if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
			return ACTION_CONTINUE_DOUBLE_DATA_ENTRY;
		}

		else if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)) {
			return ACTION_ADMINISTRATIVE_EDITING;
		}

		return "";
	}

	public ArrayList getSections(EventCRFBean ecb, SectionDAO sdao, ItemGroupDAO igdao) {
		HashMap numItemsBySectionId = sdao.getNumItemsBySectionId();
		HashMap numItemsPlusRepeatBySectionId = sdao.getNumItemsPlusRepeatBySectionId(ecb);
		HashMap numItemsCompletedBySectionId = sdao.getNumItemsCompletedBySectionId(ecb);
		HashMap numItemsPendingBySectionId = sdao.getNumItemsPendingBySectionId(ecb);

		ArrayList sections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());

		for (int i = 0; i < sections.size(); i++) {
			SectionBean sb = (SectionBean) sections.get(i);

			int sectionId = sb.getId();
			Integer key = sectionId;
			int numItems = getIntById(numItemsBySectionId, key);
			List<ItemGroupBean> itemGroups = igdao.findLegitGroupBySectionId(sectionId);
			if (!itemGroups.isEmpty()) {
				// this section has repeating rows-jxu
				int numItemsPlusRepeat = getIntById(numItemsPlusRepeatBySectionId, key);
				if (numItemsPlusRepeat > numItems) {
					sb.setNumItems(numItemsPlusRepeat);
				} else {
					sb.setNumItems(numItems);
				}
			} else {
				sb.setNumItems(numItems);
			}

			// According to logic that I searched from code of this package by
			// this time,
			// for double data entry and stage.initial_data_entry,
			// pending should be the status in query.
			int numItemsCompleted = getIntById(numItemsCompletedBySectionId, key);

			sb.setNumItemsCompleted(numItemsCompleted);
			sb.setNumItemsNeedingValidation(getIntById(numItemsPendingBySectionId, key));
			sections.set(i, sb);
		}

		return sections;
	}

	public ArrayList getSectionsByCrfVersionId(int crfVersionId) {
		SectionDAO sdao = getSectionDAO();

		HashMap numItemsBySectionId = sdao.getNumItemsBySectionId();
		ArrayList sections = sdao.findAllByCRFVersionId(crfVersionId);

		for (int i = 0; i < sections.size(); i++) {
			SectionBean sb = (SectionBean) sections.get(i);

			Integer key = sb.getId();
			sb.setNumItems(getIntById(numItemsBySectionId, key));
			sections.set(i, sb);
		}

		return sections;
	}

	public DisplayTableOfContentsBean getDisplayBeanByCrfVersionId(int crfVersionId) {
		DisplayTableOfContentsBean answer = new DisplayTableOfContentsBean();

		ArrayList sections = getSectionsByCrfVersionId(crfVersionId);
		answer.setSections(sections);

		CRFVersionDAO cvdao = getCRFVersionDAO();
		CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
		answer.setCrfVersion(cvb);

		CRFDAO cdao = getCRFDAO();
		CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());
		answer.setCrf(cb);

		answer.setEventCRF(new EventCRFBean());

		answer.setStudyEventDefinition(new StudyEventDefinitionBean());

		return answer;
	}

	public DisplayTableOfContentsBean getDisplayBean(EventCRFBean ecb) {
		DisplayTableOfContentsBean answer = new DisplayTableOfContentsBean();

		answer.setEventCRF(ecb);

		// get data
		StudySubjectBean ssb = (StudySubjectBean) getStudySubjectDAO().findByPK(ecb.getStudySubjectId());
		answer.setStudySubject(ssb);

		StudyEventBean seb = (StudyEventBean) getStudyEventDAO().findByPK(ecb.getStudyEventId());
		answer.setStudyEvent(seb);

		ArrayList sections = getSections(ecb, getSectionDAO(), getItemGroupDAO());
		answer.setSections(sections);

		// get metadata
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) getStudyEventDefinitionDAO().findByPK(
				seb.getStudyEventDefinitionId());
		answer.setStudyEventDefinition(sedb);

		CRFVersionBean cvb = (CRFVersionBean) getCRFVersionDAO().findByPK(ecb.getCRFVersionId());
		answer.setCrfVersion(cvb);

		CRFBean cb = (CRFBean) getCRFDAO().findByPK(cvb.getCrfId());
		answer.setCrf(cb);

		StudyBean studyForStudySubject = getStudyDAO().findByStudySubjectId(ssb.getId());
		EventDefinitionCRFBean edcb = getEventDefinitionCRFDAO().findByStudyEventDefinitionIdAndCRFId(
				studyForStudySubject, sedb.getId(), cb.getId());
		answer.setEventDefinitionCRF(edcb);

		answer.setAction(getActionForStage(ecb.getStage()));

		return answer;
	}

	/**
	 * A section contains all hidden dynamics will be removed from data entry tab and jump box.
	 * 
	 * @param displayTableOfContentsBean
	 *            DisplayTableOfContentsBean
	 * @param dynamicsMetadataService
	 *            DynamicsMetadataService
	 * @return DisplayTableOfContentsBean
	 */
	public DisplayTableOfContentsBean getDisplayBeanWithShownSections(
			DisplayTableOfContentsBean displayTableOfContentsBean, DynamicsMetadataService dynamicsMetadataService) {
		if (displayTableOfContentsBean == null) {
			return null;
		}

		SectionDAO sdao = getSectionDAO();
		ItemGroupDAO igdao = getItemGroupDAO();

		EventCRFBean ecb = displayTableOfContentsBean.getEventCRF();
		ArrayList<SectionBean> sectionBeans = getSections(ecb, sdao, igdao);
		ArrayList<SectionBean> showSections = new ArrayList<SectionBean>();
		if (sectionBeans != null && sectionBeans.size() > 0) {
			for (SectionBean s : sectionBeans) {
				if (sdao.containNormalItem(s.getCRFVersionId(), s.getId())) {
					showSections.add(s);
				} else {
					// for section contains dynamics, does it contain showing item_group/item?
					if (dynamicsMetadataService
							.hasShowingDynGroupInSection(s.getId(), s.getCRFVersionId(), ecb.getId())) {
						showSections.add(s);
					} else {
						if (dynamicsMetadataService.hasShowingDynItemInSection(s.getId(), s.getCRFVersionId(),
								ecb.getId())) {
							showSections.add(s);
						}
					}
				}
			}
			displayTableOfContentsBean.setSections(showSections);
		}
		return displayTableOfContentsBean;
	}

	/**
	 * Each of the event CRFs with its corresponding CRFBean. Then generates a list of DisplayEventCRFBeans, one for
	 * each event CRF.
	 * 
	 * @param eventCRFs
	 *            The list of event CRFs for this study event.
	 * @param eventDefinitionCRFs
	 *            The list of event definition CRFs for this study event.
	 * @return The list of DisplayEventCRFBeans for this study event.
	 */
	public ArrayList getDisplayEventCRFs(DataSource ds, ArrayList eventCRFs, ArrayList eventDefinitionCRFs,
			UserAccountBean ub, StudyUserRoleBean currentRole, SubjectEventStatus status, StudyBean study) {
		ArrayList answer = new ArrayList();

		StudyEventDAO sedao = getStudyEventDAO();
		CRFDAO cdao = getCRFDAO();
		CRFVersionDAO cvdao = getCRFVersionDAO();
		ItemDataDAO iddao = getItemDataDAO();
		EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();

		for (Object eventCRF : eventCRFs) {
			EventCRFBean ecb = (EventCRFBean) eventCRF;

			// populate the event CRF with its crf bean
			int crfVersionId = ecb.getCRFVersionId();
			CRFBean cb = cdao.findByVersionId(crfVersionId);
			ecb.setCrf(cb);

			CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
			ecb.setCrfVersion(cvb);

			// then get the definition so we can call
			// DisplayEventCRFBean.setFlags
			int studyEventId = ecb.getStudyEventId();
			int studyEventDefinitionId = sedao.getDefinitionIdFromStudyEventId(studyEventId);

			EventDefinitionCRFBean edc = edcdao.findByStudyEventDefinitionIdAndCRFId(study, studyEventDefinitionId,
					cb.getId());

			if (status.equals(SubjectEventStatus.LOCKED) || status.equals(SubjectEventStatus.SKIPPED)
					|| status.equals(SubjectEventStatus.STOPPED)) {
				ecb.setStage(DataEntryStage.LOCKED);

				// we need to set a SED-wide flag here, because other edcs
				// in this event can be filled in and change the status, tbh
			} else if (status.equals(SubjectEventStatus.INVALID)) {
				ecb.setStage(DataEntryStage.LOCKED);
			} else if (!cb.getStatus().equals(Status.AVAILABLE)) {
				ecb.setStage(DataEntryStage.LOCKED);
			} else if (!cvb.getStatus().equals(Status.AVAILABLE)) {
				ecb.setStage(DataEntryStage.LOCKED);
			}

			if (edc != null) {
				DisplayEventCRFBean dec = new DisplayEventCRFBean();
				dec.setEventDefinitionCRF(edc);
				dec.setFlags(ecb, ub, currentRole, edc.isDoubleEntry());

				ArrayList idata = iddao.findAllByEventCRFId(ecb.getId());
				if (!idata.isEmpty()) {
					// consider an event crf started only if item data get
					// created
					answer.add(dec);
				}
			}
		}

		return answer;
	}

	/**
	 * Finds all the event definitions for which no event CRF exists - which is the list of event definitions with
	 * uncompleted event CRFs.
	 * 
	 * @param eventDefinitionCRFs
	 *            All of the event definition CRFs for this study event.
	 * @param eventCRFs
	 *            All of the event CRFs for this study event.
	 * @return The list of event definitions for which no event CRF exists.
	 */
	public ArrayList getUncompletedCRFs(ArrayList eventDefinitionCRFs, ArrayList eventCRFs, SubjectEventStatus status) {
		int i;
		HashMap completed = new HashMap();
		HashMap startedButIncompleted = new HashMap();
		ArrayList answer = new ArrayList();

		/**
		 * A somewhat non-standard algorithm is used here: let answer = empty; foreach event definition ED, set
		 * isCompleted(ED) = false foreach event crf EC, set isCompleted(EC.getEventDefinition()) = true foreach event
		 * definition ED, if (!isCompleted(ED)) { answer += ED; } return answer; This algorithm is guaranteed to find
		 * all the event definitions for which no event CRF exists.
		 * 
		 * The motivation for using this algorithm is reducing the number of database hits.
		 * 
		 * -jun-we have to add more CRFs here: the event CRF which dones't have item data yet
		 */

		for (i = 0; i < eventDefinitionCRFs.size(); i++) {
			EventDefinitionCRFBean edcrf = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
			completed.put(edcrf.getCrfId(), Boolean.FALSE);
			startedButIncompleted.put(edcrf.getCrfId(), new EventCRFBean());
		}

		CRFVersionDAO cvdao = getCRFVersionDAO();
		ItemDataDAO iddao = getItemDataDAO();
		for (i = 0; i < eventCRFs.size(); i++) {
			EventCRFBean ecrf = (EventCRFBean) eventCRFs.get(i);
			int crfId = cvdao.getCRFIdFromCRFVersionId(ecrf.getCRFVersionId());
			ArrayList idata = iddao.findAllByEventCRFId(ecrf.getId());
			if (!idata.isEmpty()) {// this crf has data already
				completed.put(crfId, Boolean.TRUE);
			} else {
				startedButIncompleted.put(crfId, ecrf);
			}
		}

		// TODO possible relation to 1689 here, tbh
		for (i = 0; i < eventDefinitionCRFs.size(); i++) {
			DisplayEventDefinitionCRFBean dedc = new DisplayEventDefinitionCRFBean();
			EventDefinitionCRFBean edcrf = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);

			dedc.setEdc(edcrf);
			if (status.equals(SubjectEventStatus.LOCKED)) {
				dedc.setStatus(Status.LOCKED);
			}
			Boolean b = (Boolean) completed.get(new Integer(edcrf.getCrfId()));
			EventCRFBean ev = (EventCRFBean) startedButIncompleted.get(new Integer(edcrf.getCrfId()));
			if (b == null || !b) {

				dedc.setEventCRF(ev);
				answer.add(dedc);

			}
		}
		return answer;
	}

	public ArrayList<DisplayStudyEventBean> getDisplayStudyEventsForStudySubject(StudySubjectBean studySub,
			DataSource ds, UserAccountBean ub, StudyUserRoleBean currentRole, boolean excludeEventDefinishionsRemoved) {
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		StudyEventDAO sedao = getStudyEventDAO();
		EventCRFDAO ecdao = getEventCRFDAO();
		EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
		StudySubjectDAO ssdao = getStudySubjectDAO();
		StudyDAO sdao = getStudyDAO();

		ArrayList events = sedao.findAllByStudySubject(studySub);

		ArrayList displayEvents = new ArrayList();
		for (Object event1 : events) {
			StudyEventBean event = (StudyEventBean) event1;
			StudySubjectBean studySubject = (StudySubjectBean) ssdao.findByPK(event.getStudySubjectId());

			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
					.findByPK(event.getStudyEventDefinitionId());
			event.setStudyEventDefinition(sed);

			if (excludeEventDefinishionsRemoved && sed.getStatus().isDeleted()) {
				continue;
			}

			// find all active crfs in the definition
			StudyBean study = (StudyBean) sdao.findByPK(studySubject.getStudyId());
			ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(study, sed.getId());
			ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

			// construct info needed on view study event page
			DisplayStudyEventBean de = new DisplayStudyEventBean();
			de.setStudyEvent(event);
			de.setDisplayEventCRFs(getDisplayEventCRFs(ds, eventCRFs, eventDefinitionCRFs, ub, currentRole,
					event.getSubjectEventStatus(), study));
			ArrayList al = getUncompletedCRFs(eventDefinitionCRFs, eventCRFs, event.getSubjectEventStatus());
			EnterDataForStudyEventServlet.populateUncompletedCRFsWithCRFAndVersions(ds, logger, al);
			de.setUncompletedCRFs(al);

			de.setMaximumSampleOrdinal(sedao.getMaxSampleOrdinal(sed, studySubject));

			displayEvents.add(de);

		}

		return displayEvents;
	}

	public DisplayStudyEventBean getDisplayStudyEventsForStudySubject(StudyEventBean event, DataSource ds,
			UserAccountBean ub, StudyUserRoleBean currentRole, StudyBean study) {
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
		EventCRFDAO ecdao = new EventCRFDAO(ds);
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(ds);

		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(event.getStudyEventDefinitionId());
		event.setStudyEventDefinition(sed);

		// find all active crfs in the definition
		ArrayList eventDefinitionCRFs = edcdao.findAllActiveByEventDefinitionId(sed.getId());

		ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

		// construct info needed on view study event page
		DisplayStudyEventBean de = new DisplayStudyEventBean();
		de.setStudyEvent(event);
		de.setDisplayEventCRFs(getDisplayEventCRFs(ds, eventCRFs, eventDefinitionCRFs, ub, currentRole,
				event.getSubjectEventStatus(), study));
		ArrayList al = getUncompletedCRFs(eventDefinitionCRFs, eventCRFs, event.getSubjectEventStatus());
		de.setUncompletedCRFs(al);

		return de;
	}

	protected List<DiscrepancyNoteBean> extractCoderNotes(List<DiscrepancyNoteBean> notes, HttpServletRequest request) {

		if (isCoder(getUserAccountBean(request), request)) {

			List<DiscrepancyNoteBean> filteredDiscrepancyNotes = new ArrayList<DiscrepancyNoteBean>();

			for (DiscrepancyNoteBean discrepancyNote : notes) {

				UserAccountBean owner = (UserAccountBean) getUserAccountDAO().findByPK(discrepancyNote.getOwnerId());
				UserAccountBean assignedUser = (UserAccountBean) getUserAccountDAO().findByPK(
						discrepancyNote.getAssignedUserId());

				if (isCoder(assignedUser, request) || isCoder(owner, request)) {

					filteredDiscrepancyNotes.add(discrepancyNote);
				}
			}

			return filteredDiscrepancyNotes;

		} else {
			return notes;
		}
	}

	protected boolean isCoder(UserAccountBean loggedInUser, HttpServletRequest request) {
		return loggedInUser.getRoleByStudy(getCurrentStudy(request).getId()).getName().equalsIgnoreCase("study coder");
	}
}
