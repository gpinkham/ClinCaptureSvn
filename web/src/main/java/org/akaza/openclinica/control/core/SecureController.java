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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */

package org.akaza.openclinica.control.core;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.service.StudyParameterConfig;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.ListStudySubjectsServlet;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.hibernate.UsageStatsServiceDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.navigation.Navigation;
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
import org.quartz.impl.StdScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * This class enhances the Controller in several ways.
 * 
 * <ol>
 * <li>The method mayProceed, for which the class is named, is declared abstract and is called before processRequest.
 * This method indicates whether the user may proceed with the action he wishes to perform (as indicated by various
 * attributes or parameters in request or session). Note, howeveer, that the method has a void return, and throws
 * InsufficientPermissionException. The intention is that if the user may not proceed with his desired action, the
 * method should throw an exception. InsufficientPermissionException will accept a Page object which indicates where the
 * user should be redirected in order to be informed that he has insufficient permission, and the process method
 * enforces this redirection by catching an InsufficientPermissionException object.
 * 
 * <li>Four new members, session, request, response, and the UserAccountBean object ub have been declared protected, and
 * are set in the process method. This allows developers to avoid passing these objects between methods, and moreover it
 * accurately encodes the fact that these objects represent the state of the servlet.
 * 
 * <br/>
 * In particular, please note that it is no longer necessary to generate a bean for the session manager, the current
 * user or the current study.
 * 
 * <li>The method processRequest has been declared abstract. This change is unlikely to affect most code, since by
 * custom processRequest is declared in each subclass anyway.
 * 
 * <li>The standard try-catch block within most processRequest methods has been included in the process method, which
 * calls the processRequest method. Therefore, subclasses may throw an Exception in the processRequest method without
 * having to handle it.
 * 
 * <li>The addPageMessage method has been declared to streamline the process of setting page-level messages. The
 * accompanying showPageMessages.jsp file in jsp/include/ automatically displays all of the page messages; the developer
 * need only include this file in the jsp.
 * 
 * <li>The addEntityList method makes it easy to add a Collection of EntityBeans to the request. Note that this method
 * should only be used for Collections from which one EntityBean must be selected by the user. If the Collection is
 * empty, this method will throw an InconsistentStateException, taking the user to an error page and settting a page
 * message indicating that the user may not proceed because no entities are present. Note that the error page and the
 * error message must be specified.
 * </ol>
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public abstract class SecureController extends HttpServlet {

	public static final String BR = "<br/>";
	public static final String STUDY_SHOUD_BE_IN_AVAILABLE_MODE = "studyShoudBeInAvailableMode";

    protected ServletContext context;
	protected SessionManager sm;
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	protected String logDir;
	protected String logLevel;
	protected HttpSession session;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected UserAccountBean ub;
	protected StudyBean currentStudy;
	protected StudyUserRoleBean currentRole;
	protected HashMap errors = new HashMap();

    public static final String STORED_ATTRIBUTES = "RememberLastPage_storedAttributes";
	private static String SCHEDULER = "schedulerFactoryBean";
	UsageStatsServiceDAO usageStatsServiceDAO;
	public static final String JOB_HOUR = "jobHour";
	public static final String JOB_MINUTE = "jobMinute";

	private StdScheduler scheduler;
	/**
	 * local_df is set to the client locale in each request.
	 */
	protected SimpleDateFormat local_df = new SimpleDateFormat("MM/dd/yyyy");
	public static ResourceBundle resadmin, resaudit, resexception, resformat, respage, resterm, restext, resword,
			resworkflow;

	protected StudyInfoPanel panel = new StudyInfoPanel();

	public static final String PAGE_MESSAGE = "pageMessages";// for showing
	// page
	// wide message

	public static final String INPUT_MESSAGES = "formMessages"; // for showing
	// input-specific
	// messages

	public static final String PRESET_VALUES = "presetValues"; // for setting
	// preset values

	public static final String ADMIN_SERVLET_CODE = "admin";

	public static final String BEAN_TABLE = "table";

	public static final String STUDY_INFO_PANEL = "panel"; // for setting the
	// side panel

	public static final String BREADCRUMB_TRAIL = "breadcrumbs";

	public static final String POP_UP_URL = "popUpURL";

	// Use this variable as the key for the support url
	public static final String SUPPORT_URL = "supportURL";

	public static final String MODULE = "module";// to determine which module

	public static final String NOT_USED = "not_used";
	public static final String STUDY = "study";

	protected void addPageMessage(String message) {
		ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);

		if (pageMessages == null) {
			pageMessages = new ArrayList();
		}

		if (!pageMessages.contains(message)) {
            pageMessages.add(message);
        }
		logger.debug(message);
		request.setAttribute(PAGE_MESSAGE, pageMessages);

        Map storedAttributes = new HashMap();
        storedAttributes.put(SecureController.PAGE_MESSAGE, request.getAttribute(SecureController.PAGE_MESSAGE));
        request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
    }

	protected void resetPanel() {
		panel.reset();
	}

	protected void setToPanel(String title, String info) {
		if (panel.isOrderedData()) {
			ArrayList data = panel.getUserOrderedData();
			data.add(new StudyInfoPanelLine(title, info));
			panel.setUserOrderedData(data);
		} else {
			panel.setData(title, info);
		}
		request.setAttribute(STUDY_INFO_PANEL, panel);
	}

	protected void setInputMessages(HashMap messages) {
		request.setAttribute(INPUT_MESSAGES, messages);
	}

	protected void setPresetValues(HashMap presetValues) {
		request.setAttribute(PRESET_VALUES, presetValues);
	}

	protected void setTable(EntityBeanTable table) {
		request.setAttribute(BEAN_TABLE, table);
	}

	@Override
	public void init() throws ServletException {
		context = getServletContext();
	}

	/**
	 * Process request
	 * 
	 * @throws Exception
	 */
	protected abstract void processRequest() throws Exception;

	protected abstract void mayProceed() throws InsufficientPermissionException;

	public static final String USER_BEAN_NAME = "userBean";

	public void passwdTimeOut() {
		Date lastChangeDate = ub.getPasswdTimestamp();
		if (lastChangeDate == null) {
			addPageMessage(respage.getString("welcome") + " " + ub.getFirstName() + " " + ub.getLastName() + ". "
					+ respage.getString("password_set"));
			int pwdChangeRequired = new Integer(SQLInitServlet.getField("change_passwd_required")).intValue();
			if (pwdChangeRequired == 1) {
				request.setAttribute("mustChangePass", "yes");
				forwardPage(Page.RESET_PASSWORD);
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
				Trigger.TriggerState state = getScheduler(request).getTriggerState(
						TriggerKey.triggerKey(jobName, groupName));
				logger.info("found state: " + state);
				org.quartz.JobDetail details = getScheduler(request).getJobDetail(JobKey.jobKey(jobName, groupName));
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
								+ "<br/><br/>More information may be available in the log files.");
						request.getSession().removeAttribute("jobName");
						request.getSession().removeAttribute("groupName");
						request.getSession().removeAttribute("datasetId");
					} else {
						String successMsg = dataMap.getString("SUCCESS_MESSAGE");
						String success = dataMap.getString("successMsg");
						if (success != null) {

							if (successMsg.contains("$linkURL")) {
								successMsg = decodeLINKURL(successMsg, datasetId);
							}

							if (successMsg != null && !successMsg.isEmpty()) {
								addPageMessage(successMsg);
							} else {
								addPageMessage("Your Extract is now completed. Please go to review them at <a href='ExportDataset?datasetId="
										+ datasetId + "'> Here </a>.");
							}
							request.getSession().removeAttribute("jobName");
							request.getSession().removeAttribute("groupName");
							request.getSession().removeAttribute("datasetId");
						}
					}

				} else {

				}
			}
		} catch (SchedulerException se) {
			se.printStackTrace();
		}

	}

	private String decodeLINKURL(String successMsg, Integer datasetId) {

		ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(sm.getDataSource());

		ArrayList<ArchivedDatasetFileBean> fileBeans = asdfDAO.findByDatasetId(datasetId);

		successMsg = successMsg.replace("$linkURL", "<a href=\"" + SQLInitServlet.getSystemURL() + "AccessFile?fileId="
				+ fileBeans.get(0).getId() + "\">here </a>");

		return successMsg;
	}

	private StdScheduler getScheduler(HttpServletRequest request) {
		scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(
				request.getSession().getServletContext()).getBean(SCHEDULER);
		return scheduler;
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
		session = request.getSession();
		reloadUserBean(session, new UserAccountDAO((DataSource) SpringServletAccess.getApplicationContext(context)
				.getBean("dataSource")));
		String newThemeColor = CoreResources.getField("themeColor");
		session.setAttribute("newThemeColor", newThemeColor);
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

        ub = (UserAccountBean) session.getAttribute(USER_BEAN_NAME);
        currentStudy = (StudyBean) session.getAttribute("study");
		currentRole = (StudyUserRoleBean) session.getAttribute("userRole");
        
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

		local_df = new SimpleDateFormat(resformat.getString("date_format_string"), locale);

		String includeReportingVar = "includeReporting";
		if ("true".equals(SQLInitServlet.getField("include.reporting"))) {
			request.setAttribute(includeReportingVar, true);
		} else {
			request.setAttribute(includeReportingVar, false);
		}

		try {
			String userName = request.getRemoteUser();
			sm = new SessionManager(ub, userName, SpringServletAccess.getApplicationContext(context));
			ub = sm.getUserBean();
			session.setAttribute("userBean", ub);

			StudyDAO sdao = new StudyDAO(sm.getDataSource());
			if (currentStudy == null || currentStudy.getId() <= 0) {
				if (ub.getId() > 0 && ub.getActiveStudyId() > 0) {
					StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
					currentStudy = (StudyBean) sdao.findByPK(ub.getActiveStudyId());

					ArrayList studyParameters = spvdao.findParamConfigByStudy(currentStudy);

					currentStudy.setStudyParameters(studyParameters);

					StudyConfigService scs = new StudyConfigService(sm.getDataSource());
					if (currentStudy.getParentStudyId() <= 0) {// top study
						scs.setParametersForStudy(currentStudy);

					} else {
						currentStudy.setParentStudyName(((StudyBean) sdao.findByPK(currentStudy.getParentStudyId()))
								.getName());
						scs.setParametersForSite(currentStudy);
					}

					// set up the panel here
					panel.reset();
					session.setAttribute(STUDY_INFO_PANEL, panel);
				} else {
					currentStudy = new StudyBean();
				}
				session.setAttribute("study", currentStudy);
			} else if (currentStudy.getId() > 0) {
				if (currentStudy.getParentStudyId() > 0) {
					currentStudy.setParentStudyName(((StudyBean) sdao.findByPK(currentStudy.getParentStudyId()))
							.getName());
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
				if (ub.getId() > 0 && currentStudy.getId() > 0 && !currentStudy.getStatus().getName().equals("removed")) {
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
					&& (currentStudy.getStatus().equals(Status.DELETED) || currentStudy.getStatus().equals(
							Status.AUTO_DELETED))) {
				currentRole.setRole(Role.INVALID);
				currentRole.setStatus(Status.DELETED);
				session.setAttribute("userRole", currentRole);
			}

			request.setAttribute("isAdminServlet", getAdminServlet());

			this.request = request;
			this.response = response;

			if (!request.getRequestURI().endsWith("ResetPassword")) {
				passwdTimeOut();
			}
			mayProceed();
			pingJobServer(request);

			long startTime = System.currentTimeMillis();

			processRequest();

			long endTime = System.currentTimeMillis();
			long reportTime = (endTime - startTime) / 1000;
			logger.info("Time taken [" + reportTime + " seconds]");
			// If the time taken is over 5 seconds, write it to the stats table
			if (reportTime > 5) {
				getUsageStatsServiceDAO(context).savePageLoadTimeToDB(this.getClass().toString(),
						new Long(reportTime).toString());
			}
			// Call the usagestats dao here and record a time in the db
		} catch (InconsistentStateException ise) {
			ise.printStackTrace();
			logger.warn("InconsistentStateException: org.akaza.openclinica.control.SecureController: "
					+ ise.getMessage());

			addPageMessage(ise.getOpenClinicaMessage());
			forwardPage(ise.getGoTo());
		} catch (InsufficientPermissionException ipe) {
			ipe.printStackTrace();
			logger.warn("InsufficientPermissionException: org.akaza.openclinica.control.SecureController: "
					+ ipe.getMessage());

			forwardPage(ipe.getGoTo());
		} catch (OutOfMemoryError ome) {
			ome.printStackTrace();
			long heapSize = Runtime.getRuntime().totalMemory();

			logger.error("OutOfMemory Exception - " + heapSize);

			session.setAttribute("ome", "yes");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(SecureController.getStackTrace(e));

			forwardPage(Page.ERROR);
		}
	}

	private UsageStatsServiceDAO getUsageStatsServiceDAO(ServletContext context) {
		usageStatsServiceDAO = this.usageStatsServiceDAO != null ? usageStatsServiceDAO
				: (UsageStatsServiceDAO) SpringServletAccess.getApplicationContext(context).getBean(
						"usageStatsServiceDAO");
		return usageStatsServiceDAO;
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 * @param response
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
	protected void forwardPage(Page jspPage, boolean checkTrail) {
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
				if (session != null) {// added bu jxu, fixed bug for log out
					ArrayList trail = (ArrayList) session.getAttribute("trail");
					if (trail == null) {
						trail = bt.generateTrail(jspPage, request);
					} else {
						bt.setTrail(trail);
						trail = bt.generateTrail(jspPage, request);
					}
					session.setAttribute("trail", trail);
					panel = (StudyInfoPanel) session.getAttribute(STUDY_INFO_PANEL);
					if (panel == null) {
						panel = new StudyInfoPanel();
						panel.setData(jspPage, session, request);
					} else {
						panel.setData(jspPage, session, request);
					}

					session.setAttribute(STUDY_INFO_PANEL, panel);
				}
				// we are also using checkTrail to update the panel, tbh
			}
			context.getRequestDispatcher(jspPage.getFileName()).forward(request, response);
		} catch (Exception se) {
			if ("View Notes".equals(jspPage.getTitle())) {
				String viewNotesURL = jspPage.getFileName();
				if (viewNotesURL != null && viewNotesURL.contains("listNotes_p_=")) {
					String[] ps = viewNotesURL.split("listNotes_p_=");
					String t = ps[1].split("&")[0];
					int p = t.length() > 0 ? Integer.valueOf(t).intValue() : -1;
					if (p > 1) {
						viewNotesURL = viewNotesURL.replace("listNotes_p_=" + p, "listNotes_p_=" + (p - 1));
						forwardPage(Page.setNewPage(viewNotesURL, "View Notes"));
					} else if (p <= 0) {
						forwardPage(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY);
					}
				}
			}
			se.printStackTrace();
		}

	}

	protected void forwardPage(Page jspPage) {
		this.forwardPage(jspPage, true);
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
	protected void addEntityList(String beanName, Collection list, String messageIfEmpty, Page destinationIfEmpty)
			throws InconsistentStateException {
		if (list.isEmpty()) {
			throw new InconsistentStateException(destinationIfEmpty, messageIfEmpty);
		}

		request.setAttribute(beanName, list);
	}

	/**
	 * @return A blank String if this servlet is not an Administer System servlet. SecureController.ADMIN_SERVLET_CODE
	 *         otherwise.
	 */
	protected String getAdminServlet() {
		return "";
	}

	protected void setPopUpURL(String url) {
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
	 * @author ywang 10-18-2007
	 * @param entityId
	 *            int
	 * @param userName
	 *            String
	 * @param adao
	 *            AuditableEntityDAO
	 * @param ds
	 *            javax.sql.DataSource
	 */
	protected boolean entityIncluded(int entityId, String userName, AuditableEntityDAO adao, DataSource ds) {
		StudyDAO sdao = new StudyDAO(ds);
		ArrayList<StudyBean> studies = (ArrayList<StudyBean>) sdao.findAllByUserNotRemoved(userName);
		for (int i = 0; i < studies.size(); ++i) {
			if (adao.findByPKAndStudy(entityId, studies.get(i)).getId() > 0) {
				return true;
			}
			// Here follow the current logic - study subjects at sites level are
			// visible to parent studies.
			if (studies.get(i).getParentStudyId() <= 0) {
				ArrayList<StudyBean> sites = (ArrayList<StudyBean>) sdao.findAllByParent(studies.get(i).getId());
				if (sites.size() > 0) {
					for (int j = 0; j < sites.size(); ++j) {
						if (adao.findByPKAndStudy(entityId, sites.get(j)).getId() > 0) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public String getRequestURLMinusServletPath() {
		String requestURLMinusServletPath = request.getRequestURL().toString().replaceAll(request.getServletPath(), "");
		return requestURLMinusServletPath;
	}

	public String getHostPath() {
		String requestURLMinusServletPath = getRequestURLMinusServletPath();
		return requestURLMinusServletPath.substring(0, requestURLMinusServletPath.lastIndexOf("/"));
	}

	public String getContextPath() {
		String contextPath = request.getContextPath().replaceAll("/", "");
		return contextPath;
	}

	/*
	 * To check if the current study is LOCKED
	 */
	public void checkStudyLocked(Page page, String message) {
		if (currentStudy.getStatus().equals(Status.LOCKED)) {
			addPageMessage(message);
			forwardPage(page);
		}
	}

	public void checkStudyLocked(String url, String message) {
		try {
			if (currentStudy.getStatus().equals(Status.LOCKED)) {
				addPageMessage(message);
				response.sendRedirect(url);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * To check if the current study is FROZEN
	 */

	public void checkStudyFrozen(Page page, String message) {
		if (currentStudy.getStatus().equals(Status.FROZEN)) {
			addPageMessage(message);
			forwardPage(page);
		}
	}

	public void checkStudyFrozen(String url, String message) {
		try {
			if (currentStudy.getStatus().equals(Status.FROZEN)) {
				addPageMessage(message);
				response.sendRedirect(url);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public ArrayList getEventDefinitionsByCurrentStudy() {
		StudyDAO studyDAO = new StudyDAO(sm.getDataSource());
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(sm.getDataSource());
		int parentStudyId = currentStudy.getParentStudyId();
		ArrayList allDefs = new ArrayList();
		if (parentStudyId > 0) {
			StudyBean parentStudy = (StudyBean) studyDAO.findByPK(parentStudyId);
			allDefs = studyEventDefinitionDAO.findAllActiveByStudy(parentStudy);
		} else {
			parentStudyId = currentStudy.getId();
			allDefs = studyEventDefinitionDAO.findAllActiveByStudy(currentStudy);
		}
		return allDefs;
	}

	public ArrayList getStudyGroupClassesByCurrentStudy() {
		StudyDAO studyDAO = new StudyDAO(sm.getDataSource());
		StudyGroupClassDAO studyGroupClassDAO = new StudyGroupClassDAO(sm.getDataSource());
		StudyGroupDAO studyGroupDAO = new StudyGroupDAO(sm.getDataSource());
		int parentStudyId = currentStudy.getParentStudyId();
		ArrayList studyGroupClasses = new ArrayList();
		if (parentStudyId > 0) {
			StudyBean parentStudy = (StudyBean) studyDAO.findByPK(parentStudyId);
			studyGroupClasses = studyGroupClassDAO.findAllActiveByStudy(parentStudy);
		} else {
			parentStudyId = currentStudy.getId();
			studyGroupClasses = studyGroupClassDAO.findAllActiveByStudy(currentStudy);
		}

		for (int i = 0; i < studyGroupClasses.size(); i++) {
			StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClasses.get(i);
			ArrayList groups = studyGroupDAO.findAllByGroupClass(sgc);
			sgc.setStudyGroups(groups);
		}

		return studyGroupClasses;

	}

	public ArrayList<StudyGroupClassBean> getDynamicGroupClassesByStudyId(int studyId) {
		StudyGroupClassDAO studyGroupClassDAO = new StudyGroupClassDAO(sm.getDataSource());
		StudyEventDefinitionDAO studyEventDefinitionDao = new StudyEventDefinitionDAO(sm.getDataSource());
		ArrayList<StudyGroupClassBean> dynamicGroupClasses = studyGroupClassDAO
				.findAllActiveDynamicGroupsByStudyId(studyId);
		for (StudyGroupClassBean dynGroup : dynamicGroupClasses) {
			dynGroup.setEventDefinitions(studyEventDefinitionDao.findAllActiveOrderedByStudyGroupClassId(dynGroup
					.getId()));
		}
		Collections.sort(dynamicGroupClasses, StudyGroupClassBean.comparatorForDynGroupClasses);

		return dynamicGroupClasses;
	}

	public ArrayList<StudyGroupClassBean> getDynamicGroupClassesByCurrentStudy() {
		return getDynamicGroupClassesByStudyId(currentStudy.getId());
	}

	protected UserDetails getUserDetails() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return (UserDetails) principal;
		} else {
			return null;
		}
	}

	public Boolean sendEmail(String to, String subject, String body, Boolean htmlEmail, Boolean sendMessage)
			throws Exception {
		return sendEmail(to, EmailEngine.getAdminEmail(), subject, body, htmlEmail,
				respage.getString("your_message_sent_succesfully"), respage.getString("mail_cannot_be_sent_to_admin"),
				sendMessage);
	}

	public Boolean sendEmail(String to, String subject, String body, Boolean htmlEmail) throws Exception {
		return sendEmail(to, EmailEngine.getAdminEmail(), subject, body, htmlEmail,
				respage.getString("your_message_sent_succesfully"), respage.getString("mail_cannot_be_sent_to_admin"),
				true);
	}

	public Boolean sendEmail(String to, String from, String subject, String body, Boolean htmlEmail) throws Exception {
		return sendEmail(to, from, subject, body, htmlEmail, respage.getString("your_message_sent_succesfully"),
				respage.getString("mail_cannot_be_sent_to_admin"), true);
	}

	public Boolean sendEmail(String to, String from, String subject, String body, Boolean htmlEmail,
			String successMessage, String failMessage, Boolean sendMessage) throws Exception {
		Boolean messageSent = true;
		try {
			JavaMailSenderImpl mailSender = (JavaMailSenderImpl) SpringServletAccess.getApplicationContext(context)
					.getBean("mailSender");
			MimeMessage mimeMessage = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, htmlEmail);
			helper.setFrom(from);
			helper.setTo(processMultipleImailAddresses(to.trim()));
			helper.setSubject(subject);
			helper.setText(body, true);

			mailSender.send(mimeMessage);
			if (successMessage != null && sendMessage) {
				addPageMessage(successMessage);
			}
			logger.debug("Email sent successfully on {}", new Date());
		} catch (MailException me) {
			me.printStackTrace();
			if (failMessage != null && sendMessage) {
				addPageMessage(failMessage);
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
			addressTo[i] = new InternetAddress(recipientsArray.get(i).toString());
		}
		return addressTo;

	}

	public synchronized static void removeLockedCRF(int userId) {
		for (Iterator iter = getUnavailableCRFList().entrySet().iterator(); iter.hasNext();) {
			java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
			int id = (Integer) entry.getValue();
			if (id == userId)
				getUnavailableCRFList().remove(entry.getKey());
		}
	}

	public synchronized void lockThisEventCRF(int ecb, int ub) {
		getUnavailableCRFList().put(ecb, ub);
	}

	public synchronized static HashMap getUnavailableCRFList() {
		return CoreSecureController.getUnavailableCRFList();
	}

	public void dowloadFile(File f, String contentType) throws Exception {

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
			while (in != null && (length = in.read(bbuf)) != -1) {
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

	public String getPageServletFileName() {
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

	public String getPageURL() {
		String url = request.getRequestURL().toString();
		String query = request.getQueryString();
		if (url != null && url.length() > 0 && query != null) {
			url += "?" + query;
		}
		return url;
	}

	public DiscrepancyNoteBean getNoteInfo(DiscrepancyNoteBean note) {
		StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
		if ("itemData".equalsIgnoreCase(note.getEntityType())) {
			int itemDataId = note.getEntityId();
			ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
			ItemDataBean itemData = (ItemDataBean) iddao.findByPK(itemDataId);
			ItemDAO idao = new ItemDAO(sm.getDataSource());
			if (StringUtil.isBlank(note.getEntityName())) {
				ItemBean item = (ItemBean) idao.findByPK(itemData.getItemId());
				note.setEntityName(item.getName());
				request.setAttribute("item", item);
			}
			EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
			StudyEventDAO svdao = new StudyEventDAO(sm.getDataSource());

			EventCRFBean ec = (EventCRFBean) ecdao.findByPK(itemData.getEventCRFId());
			StudyEventBean event = (StudyEventBean) svdao.findByPK(ec.getStudyEventId());

			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
					.findByPK(event.getStudyEventDefinitionId());
			note.setEventName(sed.getName());
			note.setEventStart(event.getDateStarted());

			CRFDAO cdao = new CRFDAO(sm.getDataSource());
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
			EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
			StudyEventDAO svdao = new StudyEventDAO(sm.getDataSource());

			EventCRFBean ec = (EventCRFBean) ecdao.findByPK(eventCRFId);
			StudyEventBean event = (StudyEventBean) svdao.findByPK(ec.getStudyEventId());

			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
					.findByPK(event.getStudyEventDefinitionId());
			note.setEventName(sed.getName());
			note.setEventStart(event.getDateStarted());

			CRFDAO cdao = new CRFDAO(sm.getDataSource());
			CRFBean crf = cdao.findByVersionId(ec.getCRFVersionId());
			note.setCrfName(crf.getName());
			StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(ec.getStudySubjectId());
			note.setSubjectName(ss.getName());
			note.setEventCRFId(ec.getId());

		} else if ("studyEvent".equalsIgnoreCase(note.getEntityType())) {
			int eventId = note.getEntityId();
			StudyEventDAO svdao = new StudyEventDAO(sm.getDataSource());
			StudyEventBean event = (StudyEventBean) svdao.findByPK(eventId);

			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
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
			StudySubjectBean ss = ssdao.findBySubjectIdAndStudy(subjectId, currentStudy);
			note.setSubjectName(ss.getName());
		}

		return note;
	}

	public void checkRoleByUserAndStudy(UserAccountBean ub, int studyId, int siteId) {
		StudyUserRoleBean studyUserRole = ub.getRoleByStudy(studyId);
		StudyUserRoleBean siteUserRole = new StudyUserRoleBean();
		if (siteId != 0) {
			siteUserRole = ub.getRoleByStudy(siteId);
		}
		if (studyUserRole.getRole().equals(Role.INVALID) && siteUserRole.getRole().equals(Role.INVALID)) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
					+ respage.getString("change_active_study_or_contact"));
			forwardPage(Page.MENU_SERVLET);
			return;
		}
	}

	/**
	 * A inner class designed to allow the implementation of a JUnit test case for abstract SecureController. The inner
	 * class allows the test case to call the outer class' private process() method.
	 * 
	 * @author Bruce W. Perry 01/2008
	 * @see org.akaza.openclinica.servlettests.SecureControllerServletTest
	 * @see org.akaza.openclinica.servlettests.SecureControllerWrapper
	 */
	public class SecureControllerTestDelegate {

		public SecureControllerTestDelegate() {
			super();
		}

		public void process(HttpServletRequest request, HttpServletResponse response) throws OpenClinicaException,
				UnsupportedEncodingException {
			SecureController.this.process(request, response);
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
}
