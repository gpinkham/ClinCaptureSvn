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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.hibernate.UsageStatsServiceDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
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
 * Abstract class for creating a controller servlet and extending capabilities of SecureController. However, not using
 * the SingleThreadModel.
 * 
 * @author jnyayapathi
 * 
 */
@SuppressWarnings({ "all" })
public abstract class CoreSecureController extends HttpServlet {
	public static final String STUDY = "study";
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	protected HashMap errors = new HashMap();

    private static String SCHEDULER = "schedulerFactoryBean";

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

	private DataSource dataSource = null;
	private UsageStatsServiceDAO usageStatsServiceDAO;

	protected void addPageMessage(String message, HttpServletRequest request) {
		ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);

		if (pageMessages == null) {
			pageMessages = new ArrayList();
		}

		pageMessages.add(message);
		logger.debug(message);
		request.setAttribute(PAGE_MESSAGE, pageMessages);
	}

    public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			ServletContext context = getServletContext();
			SessionManager sm = new SessionManager(SpringServletAccess.getApplicationContext(context));
			dataSource = sm.getDataSource();
		} catch (Exception ne) {
			ne.printStackTrace();
		}
	}

	protected DataSource getDataSource() {
		return dataSource;
	}

	protected void resetPanel() {
		panel.reset();
	}

	protected void setToPanel(String title, String info, HttpServletRequest request) {
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

	/**
	 * Process request
	 * 
	 * @param request
	 *            TODO
	 * @param response
	 *            TODO
	 * 
	 * @throws Exception
	 */
	protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

	protected abstract void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException;

	public static final String USER_BEAN_NAME = "userBean";

	public void passwdTimeOut(HttpServletRequest request, HttpServletResponse response, UserAccountBean ub) {
		Date lastChangeDate = ub.getPasswdTimestamp();
		if (lastChangeDate == null) {
			addPageMessage(respage.getString("welcome") + " " + ub.getFirstName() + " " + ub.getLastName() + ". "
					+ respage.getString("password_set"), request);
			int pwdChangeRequired = new Integer(SQLInitServlet.getField("change_passwd_required")).intValue();
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
				Trigger.TriggerState state = getScheduler(request).getTriggerState(
						TriggerKey.triggerKey(jobName, groupName));
				logger.info("found state: " + state);
				org.quartz.JobDetail details = getScheduler(request).getJobDetail(JobKey.jobKey(jobName, groupName));
				org.quartz.JobDataMap dataMap = details.getJobDataMap();
				String failMessage = dataMap.getString("failMessage");
				if (state == Trigger.TriggerState.NONE) {
					// add the message here that your export is done
					logger.info("adding a message!");
					// TODO make absolute paths in the message, for example a
					// link from /pages/* would break
					// TODO i18n
					if (failMessage != null) {
						// The extract data job failed with the message:
						// ERROR: relation "demographics" already exists
						// More information may be available in the log files.
						addPageMessage("The extract data job failed with the message: <br/><br/>" + failMessage
								+ "<br/><br/>More information may be available in the log files.", request);
					} else {
						String successMsg = dataMap.getString("SUCCESS_MESSAGE");
						if (successMsg != null) {
							if (successMsg.contains("$linkURL")) {
								successMsg = decodeLINKURL(successMsg, datasetId);
							}

							addPageMessage(
									"Your Extract is now completed. Please go to review them at <a href='ViewDatasets'>View Datasets</a> or <a href='ExportDataset?datasetId="
											+ datasetId + "'>View Specific Dataset</a>." + successMsg, request);
						} else {
							addPageMessage(
									"Your Extract is now completed. Please go to review them at <a href='ViewDatasets'>View Datasets</a> or <a href='ExportDataset?datasetId="
											+ datasetId + "'>View Specific Dataset</a>.", request);
						}
					}
					request.getSession().removeAttribute("jobName");
					request.getSession().removeAttribute("groupName");
					request.getSession().removeAttribute("datasetId");
				} 
			}
		} catch (SchedulerException se) {
			se.printStackTrace();
		}

	}

	private String decodeLINKURL(String successMsg, Integer datasetId) {

		successMsg = "";

		ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(getDataSource());

		ArrayList<ArchivedDatasetFileBean> fileBeans = asdfDAO.findByDatasetId(datasetId);

		if (fileBeans.size() > 0) {
			successMsg = successMsg.replace("$linkURL", "<a href=\"" + SQLInitServlet.getSystemURL()
					+ "AccessFile?fileId=" + fileBeans.get(0).getId() + "\">here</a>");
		}

		return successMsg;
	}

	private StdScheduler getScheduler(HttpServletRequest request) {
		scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(
				request.getSession().getServletContext()).getBean(SCHEDULER);
		return scheduler;
	}

	private void process(HttpServletRequest request, HttpServletResponse response) throws OpenClinicaException,
			UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Encoding", "gzip");
		HttpSession session = request.getSession();
		SecureController.reloadUserBean(session, new UserAccountDAO(dataSource));
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

		UserAccountBean ub = (UserAccountBean) session.getAttribute(USER_BEAN_NAME);
		StudyBean currentStudy = (StudyBean) session.getAttribute(STUDY);        
		StudyUserRoleBean currentRole = (StudyUserRoleBean) session.getAttribute("userRole");

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
			ServletContext context = getServletContext();
			SessionManager sm = new SessionManager(ub, userName, SpringServletAccess.getApplicationContext(context));
			ub = sm.getUserBean();

			request.getSession().setAttribute("sm", sm);
			session.setAttribute("userBean", ub);

			StudyDAO sdao = new StudyDAO(getDataSource());
			if (currentStudy == null || currentStudy.getId() <= 0) {
				if (ub.getId() > 0 && ub.getActiveStudyId() > 0) {
					StudyParameterValueDAO spvdao = new StudyParameterValueDAO(getDataSource());
					currentStudy = (StudyBean) sdao.findByPK(ub.getActiveStudyId());

					ArrayList studyParameters = spvdao.findParamConfigByStudy(currentStudy);

					currentStudy.setStudyParameters(studyParameters);

					StudyConfigService scs = new StudyConfigService(getDataSource());
					if (currentStudy.getParentStudyId() <= 0) {// top study
						scs.setParametersForStudy(currentStudy);

					} else {
						currentStudy.setParentStudyName(((StudyBean) sdao.findByPK(currentStudy.getParentStudyId()))
								.getName());
						scs.setParametersForSite(currentStudy);
					}

					// set up the panel here, tbh
					panel.reset();
					session.setAttribute(STUDY_INFO_PANEL, panel);
				} else {
					currentStudy = new StudyBean();
				}
				session.setAttribute(STUDY, currentStudy);// The above line is
															// moved here since
															// currentstudy's
															// value is
															// set in else block
															// and could change
			} else if (currentStudy.getId() > 0) {
				// Set site's parentstudy name when site is restored
				if (currentStudy.getParentStudyId() > 0) {
					currentStudy.setParentStudyName(((StudyBean) sdao.findByPK(currentStudy.getParentStudyId()))
							.getName());
				}
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
			if (!request.getRequestURI().endsWith("ResetPassword")) {
				passwdTimeOut(request, response, ub);
			}
			mayProceed(request, response);
			pingJobServer(request);
			long startTime = System.currentTimeMillis();

			processRequest(request, response);

			long endTime = System.currentTimeMillis();
			long reportTime = (endTime - startTime) / 1000;
			logger.info("Time taken [" + reportTime + " seconds]");
			if (reportTime > 5) {
				getUsageStatsServiceDAO(context).savePageLoadTimeToDB(this.getClass().toString(),
						new Long(reportTime).toString());
			}
		} catch (InconsistentStateException ise) {
			ise.printStackTrace();
			logger.warn("InconsistentStateException: org.akaza.openclinica.control.CoreSecureController: "
					+ ise.getMessage());
			if (((EventCRFBean) request.getAttribute("event")) != null)
				getUnavailableCRFList().remove(((EventCRFBean) request.getAttribute("event")).getId());
			addPageMessage(ise.getOpenClinicaMessage(), request);
			forwardPage(ise.getGoTo(), request, response);
		} catch (InsufficientPermissionException ipe) {
			ipe.printStackTrace();
			logger.warn("InsufficientPermissionException: org.akaza.openclinica.control.CoreSecureController: "
					+ ipe.getMessage());
			if (((EventCRFBean) request.getAttribute("event")) != null)
				getUnavailableCRFList().remove(((EventCRFBean) request.getAttribute("event")).getId());
			forwardPage(ipe.getGoTo(), request, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(CoreSecureController.getStackTrace(e));
			if (((EventCRFBean) request.getAttribute("event")) != null)
				getUnavailableCRFList().remove(((EventCRFBean) request.getAttribute("event")).getId());
			forwardPage(Page.ERROR, request, response);
		}

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
			if (((EventCRFBean) request.getAttribute("event")) != null)
				getUnavailableCRFList().remove(((EventCRFBean) request.getAttribute("event")).getId());
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
			// UNLOCK EVENTCRF From the request.
			if (((EventCRFBean) request.getAttribute("event")) != null)
				getUnavailableCRFList().remove(((EventCRFBean) request.getAttribute("event")).getId());
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
	 * @param request
	 *            TODO
	 * @param response
	 *            TODO
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
		HttpSession session = request.getSession();

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
			}

			getServletContext().getRequestDispatcher(jspPage.getFileName()).forward(request, response);

		} catch (Exception se) {
			if ("View Notes".equals(jspPage.getTitle())) {
				String viewNotesURL = jspPage.getFileName();
				if (viewNotesURL != null && viewNotesURL.contains("listNotes_p_=")) {
					String[] ps = viewNotesURL.split("listNotes_p_=");
					String t = ps[1].split("&")[0];
					int p = t.length() > 0 ? Integer.valueOf(t).intValue() : -1;
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

	private UsageStatsServiceDAO getUsageStatsServiceDAO(ServletContext context) {
		usageStatsServiceDAO = this.usageStatsServiceDAO != null ? usageStatsServiceDAO
				: (UsageStatsServiceDAO) SpringServletAccess.getApplicationContext(context).getBean(
						"usageStatsServiceDAO");
		return usageStatsServiceDAO;
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
	 * @param request
	 *            TODO
	 * @param response
	 *            TODO
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
	 * @return A blank String if this servlet is not an Administer System servlet.
	 *         CoreSecureController.ADMIN_SERVLET_CODE otherwise.
	 */
	protected String getAdminServlet() {
		return "";
	}

	protected void setPopUpURL(String url, HttpServletRequest request) {
		if (url != null && request != null) {
			request.setAttribute(POP_UP_URL, url);
			logger.info("just set pop up url: " + url);
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

	public String getRequestURLMinusServletPath(HttpServletRequest request) {
		String requestURLMinusServletPath = request.getRequestURL().toString().replaceAll(request.getServletPath(), "");
		return requestURLMinusServletPath;
	}

	public String getHostPath(HttpServletRequest request) {
		String requestURLMinusServletPath = getRequestURLMinusServletPath(request);
		return requestURLMinusServletPath.substring(0, requestURLMinusServletPath.lastIndexOf("/"));
	}

	public String getContextPath(HttpServletRequest request) {
		String contextPath = request.getContextPath().replaceAll("/", "");
		return contextPath;
	}

	/*
	 * To check if the current study is LOCKED
	 */
	public void checkStudyLocked(Page page, String message, HttpServletRequest request, HttpServletResponse response) {
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute(STUDY);
		if (currentStudy.getStatus().equals(Status.LOCKED)) {
			addPageMessage(message, request);
			forwardPage(page, request, response);
		}
	}

	public void checkStudyLocked(String url, String message, HttpServletRequest request, HttpServletResponse response) {

		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute(STUDY);
		try {
			if (currentStudy.getStatus().equals(Status.LOCKED)) {
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
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute(STUDY);
		if (currentStudy.getStatus().equals(Status.FROZEN)) {
			addPageMessage(message, request);
			forwardPage(page, request, response);
		}
	}

	public void checkStudyFrozen(String url, String message, HttpServletRequest request, HttpServletResponse response) {
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute(STUDY);
		try {
			if (currentStudy.getStatus().equals(Status.FROZEN)) {
				addPageMessage(message, request);
				response.sendRedirect(url);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public ArrayList getEventDefinitionsByCurrentStudy(HttpServletRequest request) {
		StudyDAO studyDAO = new StudyDAO(getDataSource());
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(getDataSource());
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute(STUDY);
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

	public ArrayList getStudyGroupClassesByCurrentStudy(HttpServletRequest request) {
		StudyDAO studyDAO = new StudyDAO(getDataSource());
		StudyGroupClassDAO studyGroupClassDAO = new StudyGroupClassDAO(getDataSource());
		StudyGroupDAO studyGroupDAO = new StudyGroupDAO(getDataSource());
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute(STUDY);
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
			JavaMailSenderImpl mailSender = (JavaMailSenderImpl) SpringServletAccess.getApplicationContext(
					getServletContext()).getBean("mailSender");
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
			addressTo[i] = new InternetAddress(recipientsArray.get(i).toString());
		}
		return addressTo;

	}

	// JN:Synchornized in the securecontroller to avoid concurrent modification
	// exception
	// JN: this could still throw concurrentModification, coz of remove TODO:
	// try to do better.
	public static synchronized void removeLockedCRF(int userId) {
		try {
			for (Iterator iter = getUnavailableCRFList().entrySet().iterator(); iter.hasNext();) {
				java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();

				int id = (Integer) entry.getValue();
				if (id == userId) {
					getUnavailableCRFList().remove(entry.getKey());
				}
			}
		} catch (ConcurrentModificationException cme) {
			cme.printStackTrace();// swallowing the exception, not the ideal
									// thing to do but safer as of now.
		}
	}

	public synchronized void lockThisEventCRF(int ecb, int ub) {

		getUnavailableCRFList().put(ecb, ub);

	}

	public synchronized static HashMap getUnavailableCRFList() {
		return Controller.getUnavailableCRFList();
	}

	/**
	 * A inner class designed to allow the implementation of a JUnit test case for abstract CoreSecureController. The
	 * inner class allows the test case to call the outer class' private process() method.
	 * 
	 * @see org.akaza.openclinica.servlettests.SecureControllerServletTest
	 * @see org.akaza.openclinica.servlettests.SecureControllerWrapper
	 */
	public class SecureControllerTestDelegate {

		public SecureControllerTestDelegate() {
			super();
		}

		public void process(HttpServletRequest request, HttpServletResponse response) throws OpenClinicaException,
				UnsupportedEncodingException {
			CoreSecureController.this.process(request, response);
		}
	}
}
