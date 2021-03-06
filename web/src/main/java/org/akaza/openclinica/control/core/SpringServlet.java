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
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.control.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
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
import org.akaza.openclinica.control.managestudy.ListEventsForSubjectsServlet;
import org.akaza.openclinica.control.submit.DataEntryServlet;
import org.akaza.openclinica.control.submit.ListStudySubjectsServlet;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.MapsHolder;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
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
import org.akaza.openclinica.navigation.Navigation;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.service.crfdata.HideCRFManager;
import org.akaza.openclinica.util.CrfComparator;
import org.akaza.openclinica.view.BreadcrumbTrail;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.view.StudyInfoPanelLine;
import org.akaza.openclinica.web.InconsistentStateException;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.clinovo.enums.study.StudyAllocation;
import com.clinovo.enums.study.StudyAssignment;
import com.clinovo.enums.study.StudyConfigurationParameter;
import com.clinovo.enums.study.StudyControl;
import com.clinovo.enums.study.StudyDuration;
import com.clinovo.enums.study.StudyEndPoint;
import com.clinovo.enums.study.StudyFacRecruitStatus;
import com.clinovo.enums.study.StudyFacility;
import com.clinovo.enums.study.StudyFeature;
import com.clinovo.enums.study.StudyMasking;
import com.clinovo.enums.study.StudyPhase;
import com.clinovo.enums.study.StudyPurpose;
import com.clinovo.enums.study.StudySelection;
import com.clinovo.enums.study.StudyTiming;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.RequestUtil;
import com.clinovo.util.SubjectEventStatusUtil;

/**
 * Abstract class for creating a spring servlet and extending capabilities of spring controller. However, not using the
 * SingleThreadModel.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class SpringServlet extends SpringController implements HttpRequestHandler, ServletContextAware {

	public final Logger logger = LoggerFactory.getLogger(getClass().getName());

	/**
	 * Process request.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws Exception
	 */
	protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

	/**
	 * Checks whether the user has the right permission to proceed function.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws InsufficientPermissionException
	 *             custom InsufficientPermissionException
	 */
	protected abstract void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException;

	/**
	 * This method is used to handle request and resend it to doGet or doPost methods.
	 *
	 * @param request
	 *            the HttpServletRequest.
	 * @param response
	 *            the HttpServletResponse.
	 * @throws ServletException
	 *             in case if servlet encounters difficulty.
	 * @throws IOException
	 *             will be thrown in case of failed or interrupted I/O operations.
	 */
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
	 *            the HttpServletRequest.
	 * @param response
	 *            the HttpServletResponse.
	 * @throws ServletException
	 *             in case if servlet encounters difficulty.
	 * @throws IOException
	 *             will be thrown in case of failed or interrupted I/O operations.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		try {
			Navigation.addToNavigationStack(request);
			logger.debug("Request");
			process(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			if (request.getAttribute("event") != null && request.getAttribute("event") instanceof EventCRFBean) {
				SpringServlet.justRemoveLockedCRF(((EventCRFBean) request.getAttribute("event")).getId());
			}
		}
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request
	 *            the HttpServletRequest.
	 * @param response
	 *            the HttpServletResponse.
	 * @throws ServletException
	 *             in case if servlet encounters difficulty.
	 * @throws IOException
	 *             will be thrown in case of failed or interrupted I/O operations.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		try {
			Navigation.addToNavigationStack(request);
			logger.debug("Post");
			process(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			if (request.getAttribute("event") != null && request.getAttribute("event") instanceof EventCRFBean) {
				SpringServlet.justRemoveLockedCRF(((EventCRFBean) request.getAttribute("event")).getId());
			}
		}
	}

	/**
	 * Check if user have access for data review.
	 *
	 * @param ub
	 *            the user account bean.
	 * @param currentRole
	 *            the current study user role.
	 * @return true if user have access for data review, false otherwise.
	 */
	public static boolean mayViewData(UserAccountBean ub, StudyUserRoleBean currentRole) {
		if (currentRole != null) {
			Role r = currentRole.getRole();
			if (r != null
					&& (r.equals(Role.SYSTEM_ADMINISTRATOR) || r.equals(Role.STUDY_ADMINISTRATOR)
							|| r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.INVESTIGATOR)
							|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR) || r.equals(Role.STUDY_CODER)
							|| r.equals(Role.STUDY_EVALUATOR) || Role.isMonitor(r))
					|| (r != null && r.equals(Role.STUDY_SPONSOR))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if user have access for data entry.
	 *
	 * @param ub
	 *            the user account bean.
	 * @param currentRole
	 *            the current study user role.
	 * @return true if user have access for data entry, false otherwise.
	 */
	public static boolean maySubmitData(UserAccountBean ub, StudyUserRoleBean currentRole) {
		if (currentRole != null && ub != null) {
			Role r = currentRole.getRole();
			if (r != null && (r.equals(Role.SYSTEM_ADMINISTRATOR) || r.equals(Role.STUDY_ADMINISTRATOR)
					|| r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.INVESTIGATOR)
					|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR) || r.equals(Role.STUDY_EVALUATOR))) {
				return true;
			}
		}

		return false;
	}

	private void pingJobServer(HttpServletRequest request) {
		String jobName = (String) request.getSession().getAttribute("jobName");
		String groupName = (String) request.getSession().getAttribute("groupName");
		Integer datasetId = (Integer) request.getSession().getAttribute("datasetId");
		try {
			if (jobName != null && groupName != null) {
				logger.info("trying to retrieve status on " + jobName + " " + groupName);
				Trigger.TriggerState state = getStdScheduler()
						.getTriggerState(TriggerKey.triggerKey(jobName, groupName));
				logger.info("found state: " + state);
				org.quartz.JobDetail details = getStdScheduler().getJobDetail(JobKey.jobKey(jobName, groupName));
				org.quartz.JobDataMap dataMap = details.getJobDataMap();
				String failMessage = dataMap.getString("failMessage");
				if (state == Trigger.TriggerState.NONE || state == Trigger.TriggerState.COMPLETE) {
					// add the message here that your export is done
					logger.info("adding a message!");
					// TODO make absolute paths in the message, for example a link from /pages/* would break
					if (failMessage != null) {
						// The extract data job failed with the message:
						// ERROR: relation "demographics" already exists
						// More information may be available in the log files.
						addPageMessage(getResPage().getString("the_extract_data_job_failed") + failMessage
								+ getResPage().getString("more_information_may_be_available"), request);
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
								addPageMessage(getResPage().getString("your_extract_is_now_completed")
										+ " <a href='ExportDataset?datasetId=" + datasetId + "'>"
										+ getResWord().getString("here_lower_case") + "</a>.", request);
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

	private void process(HttpServletRequest request, HttpServletResponse response)
			throws OpenClinicaException, UnsupportedEncodingException {
		clearDataEntrySession(request);
		request.setCharacterEncoding("UTF-8");
		request.setAttribute(CURRENT_DATE, new Date());
		HttpSession session = request.getSession();
		reloadUserBean(session, getUserAccountDAO());
		String newThemeColor = CoreResources.getField("themeColor");
		session.setAttribute(SpringController.THEME_COLOR, newThemeColor);
		ApplicationContext applicationContext = SpringServletAccess.getApplicationContext(getServletContext());
		request.setAttribute(STUDY_FEATURES, StudyFeature.values());
		request.setAttribute(STUDY_FACILITIES, StudyFacility.values());
		request.setAttribute(STUDY_CONFIGURATION_PARAMETERS, StudyConfigurationParameter.values());
		try {
			session.setMaxInactiveInterval(Integer.parseInt(SQLInitServlet.getField("max_inactive_interval")));
		} catch (NumberFormatException nfe) {
			// 3600 is the datainfo.properties maxInactiveInterval on
			final int defMaxInactiveInterval = 3600;
			session.setMaxInactiveInterval(defMaxInactiveInterval);
		}

		// If the session already has a value with key SUPPORT_URL don't reset
		if (session.getAttribute(SUPPORT_URL) == null) {
			session.setAttribute(SUPPORT_URL, SQLInitServlet.getSupportURL());
		}

		if (request.getParameter(CW) != null) {
			request.setAttribute(JUST_CLOSE_WINDOW, true);
		}

		restorePageMessages(request);

		StudyBean currentStudy = getUpdatedStudy(request);
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		// Set current language preferences
		LocaleResolver.resolveLocale();

		response.addCookie(
				new Cookie(BOOSTRAP_DATE_FORMAT, getResFormat().getString(BOOTSTRAP_DATAPICKER_DATE_FORMAT)));
		response.addCookie(new Cookie(CC_DATE_FORMAT, getResFormat().getString(DATE_FORMAT_STRING)));

		initMaps();
		getErrorsHolder(request);

		request.setAttribute(FORM_WITH_STATE_FLAG, request.getParameter(FORM_WITH_STATE_FLAG));
		try {
			String userName = request.getRemoteUser();
			SessionManager sm = new SessionManager(ub, userName, applicationContext);
			request.setAttribute(SESSION_MANAGER, sm);

			String restoreSessionFlag = getRestoreSessionFlag();
			if (ub == null && sm.getUserBean() != null && restoreSessionFlag != null
					&& restoreSessionFlag.equals("true")) {
				restoreSavedSessionAttributes(session, userName);
			}

			ub = sm.getUserBean();
			request.getSession().setAttribute(USER_BEAN_NAME, ub);

			String includeReportingVar = "includeReporting";
			request.setAttribute(includeReportingVar, false);

			String popUpUrl = (String) request.getSession().getAttribute(POP_UP_URL);
			if (popUpUrl != null) {
				request.setAttribute(POP_UP_URL, popUpUrl);
				request.getSession().removeAttribute(POP_UP_URL);
			}

			StudyDAO sdao = getStudyDAO();
			if (currentStudy == null || currentStudy.getId() <= 0) {
				StudyBean parentStudy = null;
				if (ub.getId() > 0 && ub.getActiveStudyId() > 0) {
					currentStudy = (StudyBean) sdao.findByPK(ub.getActiveStudyId());
					parentStudy = currentStudy;
					getStudyService().prepareStudyBeanConfiguration(currentStudy);
					// set up the panel here
					StudyInfoPanel panel = getStudyInfoPanel(request);
					panel.reset();
				} else {
					currentStudy = new StudyBean();
				}
				session.setAttribute(STUDY, currentStudy);
				session.setAttribute(PARENT_STUDY, parentStudy);
			}

			updateLastAccessedInstanceType(response, currentStudy);
			String randomizationEnviroment = currentStudy.getStudyParameterConfig().getRandomizationEnviroment();
			session.setAttribute("randomizationEnviroment", randomizationEnviroment);

			int currentStudyId = currentStudy.getParentStudyId() > 0
					? currentStudy.getParentStudyId()
					: currentStudy.getId();
			boolean isEvaluationEnabled = getStudyParameterValueDAO()
					.findByHandleAndStudy(currentStudyId, "studyEvaluator").getValue().equalsIgnoreCase("yes");
			request.getSession().setAttribute(EVALUATION_ENABLED, isEvaluationEnabled);

			if (this instanceof ListStudySubjectsServlet && currentStudy.getStatus() != Status.AVAILABLE) {
				String startWith = BR;
				ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
				if (pageMessages == null) {
					startWith = "";
					pageMessages = new ArrayList();
				}
				String message = getResWord().getString(STUDY_SHOUD_BE_IN_AVAILABLE_MODE) + BR;

				if (!pageMessages.contains(message) && !pageMessages.contains(startWith + message)) {
					pageMessages.add(startWith + message);
				}
				request.setAttribute(PAGE_MESSAGE, pageMessages);
			}

			Role.prepareRoleMapWithDescriptions(getResTerm());

			if (currentRole == null || currentRole.getId() <= 0) {
				// if current study has been "removed", current role will be
				// kept as "invalid"
				if (ub.getId() > 0 && currentStudy.getId() > 0
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
			} else {
				// For the case that current role is not "invalid" but current
				// active study has been removed.
				if (currentRole.getId() > 0 && (currentStudy.getStatus().equals(Status.DELETED)
						|| currentStudy.getStatus().equals(Status.AUTO_DELETED))) {
					currentRole.setRole(Role.INVALID);
					currentRole.setStatus(Status.DELETED);
					session.setAttribute("userRole", currentRole);
				}
			}

			request.setAttribute("isAdminServlet", getAdminServlet(request));

			if (!request.getRequestURI().endsWith("ResetPassword")) {
				passwdTimeOut(request, response);
			}

			String controllerUrl = (String) request.getSession().getAttribute(REDIRECT_BACK_TO_CONTROLLER_AFTER_LOGIN);
			if (controllerUrl != null) {
				request.getSession().removeAttribute(REDIRECT_BACK_TO_CONTROLLER_AFTER_LOGIN);
				response.sendRedirect(controllerUrl);
			} else {
				mayProceed(request, response);
				pingJobServer(request);

				long startTime = System.currentTimeMillis();

				processRequest(request, response);

				final int millisecondsInSec = 1000;
				long endTime = System.currentTimeMillis();
				long reportTime = (endTime - startTime) / millisecondsInSec;
				logger.info("Time taken [" + reportTime + " seconds]");
				// If the time taken is over 5 seconds, write it to the stats table
				final int maxReportTime = 5;
				if (reportTime > maxReportTime) {
					getUsageStatsServiceDAO().savePageLoadTimeToDB(this.getClass().toString(),
							Long.toString(reportTime));
				}
				// Call the usagestats dao here and record a time in the db
			}
		} catch (InconsistentStateException ise) {
			ise.printStackTrace();
			logger.warn("InconsistentStateException: org.akaza.openclinica.control.SpringServlet: " + ise.getMessage());
			if (request.getAttribute("event") != null && request.getAttribute("event") instanceof EventCRFBean) {
				SpringServlet.justRemoveLockedCRF(((EventCRFBean) request.getAttribute("event")).getId());
			}
			addPageMessage(ise.getOpenClinicaMessage(), request);
			forwardPage(ise.getGoTo(), request, response);
		} catch (InsufficientPermissionException ipe) {
			ipe.printStackTrace();
			logger.warn("InsufficientPermissionException: org.akaza.openclinica.control.SpringServlet: "
					+ ipe.getMessage());
			if (request.getAttribute("event") != null && request.getAttribute("event") instanceof EventCRFBean) {
				SpringServlet.justRemoveLockedCRF(((EventCRFBean) request.getAttribute("event")).getId());
			}
			forwardPage(ipe.getGoTo(), request, response);
		} catch (OutOfMemoryError ome) {
			ome.printStackTrace();
			long heapSize = Runtime.getRuntime().totalMemory();
			logger.error("OutOfMemory Exception - " + heapSize);
			if (request.getAttribute("event") != null && request.getAttribute("event") instanceof EventCRFBean) {
				SpringServlet.justRemoveLockedCRF(((EventCRFBean) request.getAttribute("event")).getId());
			}
			session.setAttribute("ome", "yes");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error has occurred.", e);
			if (request.getAttribute("event") != null && request.getAttribute("event") instanceof EventCRFBean) {
				SpringServlet.justRemoveLockedCRF(((EventCRFBean) request.getAttribute("event")).getId());
			}
			forwardPage(Page.ERROR, request, response);
		}
	}

	/**
	 * Method that checks password timeout.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	public void passwdTimeOut(HttpServletRequest request, HttpServletResponse response) {
		UserAccountBean ub = getUserAccountBean(request);
		Date lastChangeDate = ub.getPasswdTimestamp();
		if (lastChangeDate == null) {
			addPageMessage(getResPage().getString("welcome") + " " + ub.getFirstName() + " " + ub.getLastName() + ". "
					+ getResPage().getString("password_set"), request);
			int pwdChangeRequired = Integer.parseInt(SQLInitServlet.getField("pwd.change.required"));
			if (pwdChangeRequired == 1) {
				request.setAttribute("mustChangePass", "yes");
				forwardPage(Page.RESET_PASSWORD, request, response);
			}
		}
	}

	private void reloadUserBean(HttpSession session, UserAccountDAO userAccountDao) {
		if (session.getAttribute("reloadUserBean") != null) {
			UserAccountBean userAccountBean = (UserAccountBean) session.getAttribute(USER_BEAN_NAME);
			session.setAttribute(USER_BEAN_NAME, userAccountDao.findByUserName(userAccountBean.getName()));
			session.removeAttribute("study");
			session.removeAttribute("userRole");
			session.removeAttribute("reloadUserBean");
		}
	}

	protected static void updateLastAccessedInstanceType(HttpServletResponse response, StudyBean currentStudy) {
		if (currentStudy != null) {
			String cookieValue = currentStudy.getStudyParameterConfig().getInstanceType();
			String cookiePath = "/";
			Cookie cookie = new Cookie(COOKIE_NAME, cookieValue);
			cookie.setMaxAge(MONTH_IN_SECONDS);
			cookie.setPath(cookiePath);
			response.addCookie(cookie);
		}
	}

	private void clearDataEntrySession(HttpServletRequest request) {
		if (this instanceof ListStudySubjectsServlet || this instanceof ListEventsForSubjectsServlet) {
			request.getSession().removeAttribute(DataEntryServlet.HAS_DATA_FLAG);
			request.getSession().removeAttribute(DataEntryServlet.GROUP_HAS_DATA);
		}
	}

	private String decodeLINKURL(Integer datasetId) {

		String successMsg = "";
		ArchivedDatasetFileDAO asdfDAO = getArchivedDatasetFileDAO();
		ArrayList<ArchivedDatasetFileBean> fileBeans = asdfDAO.findByDatasetId(datasetId);

		if (fileBeans.size() > 0) {
			successMsg = successMsg.replace("$linkURL",
					"<a href=\"" + SQLInitServlet.getSystemURL() + "AccessFile?fileId=" + fileBeans.get(0).getId()
							+ "\">" + getResWord().getString("here_lower_case") + "</a>");
		}
		return successMsg;
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

	/**
	 * Get SessionManager from request.
	 *
	 * @param request
	 *            HttpServletRequest request
	 * @return SessionManager
	 */
	public SessionManager getSessionManager(HttpServletRequest request) {
		return (SessionManager) request.getAttribute(SESSION_MANAGER);
	}

	/**
	 * Get errors holder.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @return HashMap
	 */
	public HashMap getErrorsHolder(HttpServletRequest request) {
		HashMap errors = (HashMap) request.getAttribute(ERRORS_HOLDER);
		if (errors == null) {
			errors = new HashMap();
			request.setAttribute(ERRORS_HOLDER, errors);
		}
		return errors;
	}

	/**
	 * Get studyInfoPanel.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @return StudyInfoPanel
	 */
	public StudyInfoPanel getStudyInfoPanel(HttpServletRequest request) {
		StudyInfoPanel panel = (StudyInfoPanel) request.getSession().getAttribute(STUDY_INFO_PANEL);
		if (panel == null) {
			panel = new StudyInfoPanel();
			request.getSession().setAttribute(STUDY_INFO_PANEL, panel);
			request.setAttribute(STUDY_INFO_PANEL, panel);
		}
		return panel;
	}

	protected void setPresetValues(HashMap presetValues, HttpServletRequest request) {
		request.setAttribute(PRESET_VALUES, presetValues);
	}

	private StudyBean getUpdatedStudy(HttpServletRequest request) {
		StudyBean currentStudy = getCurrentStudy(request);
		if (currentStudy != null) {
			currentStudy = (StudyBean) getStudyDAO().findByPK(currentStudy.getId());
			StudyBean parentStudy = currentStudy;
			StudyDAO sdao = getStudyDAO();
			StudyParameterValueDAO spvdao = getStudyParameterValueDAO();
			ArrayList studyParameters = spvdao.findParamConfigByStudy(currentStudy);
			currentStudy.setStudyParameters(studyParameters);
			StudyConfigService scs = getStudyConfigService();
			if (currentStudy.getParentStudyId() <= 0) {
				scs.setParametersForStudy(currentStudy);
			} else {
				parentStudy = (StudyBean) sdao.findByPK(currentStudy.getParentStudyId());
				currentStudy.setParentStudyName(parentStudy.getName());
				currentStudy.setParentStudyOid(parentStudy.getOid());
				scs.setParametersForSite(currentStudy);
			}
			request.getSession().setAttribute(STUDY, currentStudy);
			request.getSession().setAttribute(PARENT_STUDY, parentStudy);
		}
		return currentStudy;
	}

	public MapsHolder getMapsHolder() {
		return (MapsHolder) RequestUtil.getRequest().getSession().getAttribute(CURRENT_MAPS_HOLDER);
	}

	private void initMaps() {
		RequestUtil.getRequest().getSession().setAttribute(CURRENT_MAPS_HOLDER, new MapsHolder());

		getMapsHolder().getFacRecruitStatusList().addAll(Arrays.asList(StudyFacRecruitStatus.values()));

		// if StudyProtocolType is INTERVENTIONAL

		getMapsHolder().getInterPurposeList().add(StudyPurpose.EMPTY_VALUE);
		getMapsHolder().getInterPurposeList().add(StudyPurpose.TREATMENT);
		getMapsHolder().getInterPurposeList().add(StudyPurpose.PREVENTION);
		getMapsHolder().getInterPurposeList().add(StudyPurpose.DIAGNOSIS);
		getMapsHolder().getInterPurposeList().add(StudyPurpose.SUPPORTIVE_CARE);
		getMapsHolder().getInterPurposeList().add(StudyPurpose.SCREENING);
		getMapsHolder().getInterPurposeList().add(StudyPurpose.HEALTH_SERVICES_RESEARCH);
		getMapsHolder().getInterPurposeList().add(StudyPurpose.BASIC_SCIENCE);
		getMapsHolder().getInterPurposeList().add(StudyPurpose.OTHER);

		getMapsHolder().getStudyPhaseList().addAll(Arrays.asList(StudyPhase.values()));

		getMapsHolder().getAllocationList().addAll(Arrays.asList(StudyAllocation.values()));
		getMapsHolder().getMaskingList().addAll(Arrays.asList(StudyMasking.values()));
		getMapsHolder().getControlList().addAll(Arrays.asList(StudyControl.values()));
		getMapsHolder().getAssignmentList().addAll(Arrays.asList(StudyAssignment.values()));
		getMapsHolder().getEndPointList().addAll(Arrays.asList(StudyEndPoint.values()));

		// if StudyProtocolType is OBSERVATIONAL

		getMapsHolder().getObserPurposeList().add(StudyPurpose.EMPTY_VALUE);
		getMapsHolder().getObserPurposeList().add(StudyPurpose.NATURAL_HISTORY);
		getMapsHolder().getObserPurposeList().add(StudyPurpose.SCREENING);
		getMapsHolder().getObserPurposeList().add(StudyPurpose.PSYCHOSOCIAL);

		getMapsHolder().getDurationList().addAll(Arrays.asList(StudyDuration.values()));
		getMapsHolder().getSelectionList().addAll(Arrays.asList(StudySelection.values()));
		getMapsHolder().getTimingList().addAll(Arrays.asList(StudyTiming.values()));
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
				if (request.getSession() != null) {
					// added bu jxu, fixed bug for log out
					ArrayList trail = (ArrayList) request.getSession().getAttribute("trail");
					if (trail == null) {
						trail = bt.generateTrail(jspPage, request);
					} else {
						bt.setTrail(trail);
						trail = bt.generateTrail(jspPage, request);
					}
					request.getSession().setAttribute("trail", trail);
					StudyInfoPanel panel = getStudyInfoPanel(request);
					panel.setData(jspPage, request.getSession(), request, getCRFVersionDAO(),
							getEventDefinitionCRFDAO());
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
	 * @return A blank String if this servlet is not an Administer System servlet. SpringServlet.ADMIN_SERVLET_CODE
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

	/**
	 * This method is used to get request URL without name of the servlet in the path.
	 * 
	 * @param request
	 *            the HttpServletRequest from which path will be take.
	 * @return system URL without servlet path.
	 */
	public String getRequestURLMinusServletPath(HttpServletRequest request) {
		return request.getRequestURL().toString().replaceAll(request.getServletPath(), "");
	}

	/**
	 * This method returns host URL.
	 * 
	 * @param request
	 *            the HttpServletRequest from which URL will be take.
	 * @return host URL.
	 */
	public String getHostPath(HttpServletRequest request) {
		String requestURLMinusServletPath = getRequestURLMinusServletPath(request);
		return requestURLMinusServletPath.substring(0, requestURLMinusServletPath.lastIndexOf("/"));
	}

	/**
	 * Checks if the current study is LOCKED.
	 * 
	 * @param page
	 *            the Page object.
	 * @param message
	 *            message that will be shown in the Alerts and Messages panel.
	 * @param request
	 *            the HttpServletRequest.
	 * @param response
	 *            the HttpServletResponse.
	 */
	public void checkStudyLocked(Page page, String message, HttpServletRequest request, HttpServletResponse response) {
		if (getCurrentStudy(request).getStatus().equals(Status.LOCKED)) {
			addPageMessage(message, request);
			forwardPage(page, request, response);
		}
	}

	/**
	 * Checks if study is Locked using URL.
	 * 
	 * @param url
	 *            String.
	 * @param message
	 *            message that will be shown in the Alerts and Messages panel.
	 * @param request
	 *            the HttpServletRequest.
	 * @param response
	 *            the HttpServletResponse.
	 */
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

	/**
	 * Checks if study is Frozen.
	 * 
	 * @param page
	 *            Page object.
	 * @param message
	 *            message that will be shown in the Alerts and Messages panel.
	 * @param request
	 *            the HttpServletRequest.
	 * @param response
	 *            the HttpServletResponse.
	 */
	public void checkStudyFrozen(Page page, String message, HttpServletRequest request, HttpServletResponse response) {
		if (getCurrentStudy(request).getStatus().equals(Status.FROZEN)) {
			addPageMessage(message, request);
			forwardPage(page, request, response);
		}
	}

	/**
	 * Check if study is Frozen using URL.
	 * 
	 * @param url
	 *            String.
	 * @param message
	 *            message that will be shown in the Alerts and Messages panel.
	 * @param request
	 *            the HttpServletRequest.
	 * @param response
	 *            the HttpServletResponse.
	 */
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

	/**
	 * Get list of Events Definitions by current Study.
	 * 
	 * @param request
	 *            the HttpServletRequest.
	 * @return ArrayList.
	 */
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

	/**
	 * Get list of Study Group Classes by Current Study.
	 *
	 * @param request
	 *            the HttpServletRequest.
	 * @return ArrayList.
	 */
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
			List groups = studyGroupDAO.findAllByGroupClass(sgc);
			sgc.setStudyGroups(groups);
		}

		return studyGroupClasses;
	}

	/**
	 * Get list of Dynamic Groups by study ID.
	 * 
	 * @param studyId
	 *            int.
	 * @return List.
	 */
	public List<StudyGroupClassBean> getDynamicGroupClassesByStudyId(int studyId) {

		ListIterator it;
		StudyGroupClassDAO studyGroupClassDAO = getStudyGroupClassDAO();
		StudyEventDefinitionDAO studyEventDefinitionDao = getStudyEventDefinitionDAO();
		List<StudyGroupClassBean> dynamicGroupClasses = studyGroupClassDAO.findAllActiveDynamicGroupsByStudyId(studyId);
		for (StudyGroupClassBean dynGroup : dynamicGroupClasses) {
			dynGroup.setEventDefinitions(
					studyEventDefinitionDao.findAllAvailableAndOrderedByStudyGroupClassId(dynGroup.getId()));
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

	/**
	 * Send an email.
	 * 
	 * @param to
	 *            String.
	 * @param subject
	 *            String.
	 * @param body
	 *            String.
	 * @param htmlEmail
	 *            Boolean.
	 * @param sendMessage
	 *            Boolean.
	 * @param request
	 *            HttpServletRequest.
	 * @return result of the attempt.
	 * @throws Exception
	 *             if there was an error while attempt to send email.
	 */
	public Boolean sendEmail(String to, String subject, String body, Boolean htmlEmail, Boolean sendMessage,
			HttpServletRequest request) throws Exception {
		return sendEmail(to, EmailEngine.getAdminEmail(), subject, body, htmlEmail,
				getResPage().getString("your_message_sent_succesfully"),
				getResPage().getString("mail_cannot_be_sent_to_admin"), sendMessage, request);
	}

	/**
	 * Send an email.
	 * 
	 * @param to
	 *            String.
	 * @param subject
	 *            String.
	 * @param body
	 *            String.
	 * @param htmlEmail
	 *            Boolean.
	 * @param request
	 *            HttpServletRequest.
	 * @return result of the attempt.
	 * @throws Exception
	 *             if there was an error while attempt to send email.
	 */
	public Boolean sendEmail(String to, String subject, String body, Boolean htmlEmail, HttpServletRequest request)
			throws Exception {
		return sendEmail(to, EmailEngine.getAdminEmail(), subject, body, htmlEmail,
				getResPage().getString("your_message_sent_succesfully"),
				getResPage().getString("mail_cannot_be_sent_to_admin"), true, request);
	}

	/**
	 * Send an email.
	 * 
	 * @param to
	 *            String.
	 * @param from
	 *            String.
	 * @param subject
	 *            String.
	 * @param body
	 *            String.
	 * @param htmlEmail
	 *            Boolean.
	 * @param request
	 *            HttpServletRequest.
	 * @return result of the attempt.
	 * @throws Exception
	 *             if there was an error while attempt to send email.
	 */
	public Boolean sendEmail(String to, String from, String subject, String body, Boolean htmlEmail,
			HttpServletRequest request) throws Exception {
		return sendEmail(to, from, subject, body, htmlEmail, getResPage().getString("your_message_sent_succesfully"),
				getResPage().getString("mail_cannot_be_sent_to_admin"), true, request);
	}

	/**
	 * Send an email with success message and fail message.
	 * 
	 * @param to
	 *            String.
	 * @param from
	 *            String.
	 * @param subject
	 *            String.
	 * @param body
	 *            String.
	 * @param htmlEmail
	 *            Boolean.
	 * @param successMessage
	 *            String.
	 * @param failMessage
	 *            String
	 * @param sendMessage
	 *            Boolean.
	 * @param request
	 *            HttpServletRequest.
	 * @return result of the attempt.
	 * @throws Exception
	 *             if there was an error while attempt to send email.
	 */
	public Boolean sendEmail(String to, String from, String subject, String body, Boolean htmlEmail,
			String successMessage, String failMessage, Boolean sendMessage, HttpServletRequest request)
					throws Exception {
		return sendEmailWithAttach(to, from, subject, body, htmlEmail, successMessage, failMessage, sendMessage,
				new String[0], request);
	}

	/**
	 * Send an email with fail message, success message and attached files.
	 * 
	 * @param to
	 *            String.
	 * @param from
	 *            String.
	 * @param subject
	 *            String.
	 * @param body
	 *            String.
	 * @param htmlEmail
	 *            Boolean.
	 * @param successMessage
	 *            String.
	 * @param failMessage
	 *            String.
	 * @param sendMessage
	 *            Boolean.
	 * @param files
	 *            String[].
	 * @param request
	 *            HttpServletRequest.
	 * @return result of the attempt.
	 * @throws Exception
	 *             if there was an error while attempt to send email.
	 */
	public Boolean sendEmailWithAttach(String to, String from, String subject, String body, Boolean htmlEmail,
			String successMessage, String failMessage, Boolean sendMessage, String[] files, HttpServletRequest request)
					throws Exception {
		Boolean messageSent = true;
		if (allRequiredAttributesNotEmpty(to, from)) {
			try {
				JavaMailSenderImpl mailSender = getMailSender();
				MimeMessage mimeMessage = mailSender.createMimeMessage();

				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, htmlEmail);
				helper.setFrom(from);
				helper.setTo(processMultipleImailAddresses(to.trim()));
				helper.setSubject(subject);
				helper.setText(body, true);
				for (String filePath : files) {
					FileSystemResource file = new FileSystemResource(filePath);
					helper.addAttachment(file.getFilename(), file);
				}

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
		} else {
			if (failMessage != null && sendMessage) {
				addPageMessage(failMessage, request);
			}
			logger.info("Email could not be sent, because some required email attributes are empty.");
			messageSent = false;
		}
		return messageSent;
	}

	private boolean allRequiredAttributesNotEmpty(String to, String from) {
		return !to.isEmpty() && !from.isEmpty();
	}

	/**
	 * Get name of the page Servlet.
	 * 
	 * @param request
	 *            HttpServletRequest.
	 * @return String name of the page Servlet.
	 */
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

	/**
	 * Get URL of the current page.
	 * 
	 * @param request
	 *            the HttpServletRequest.
	 * @return page URL.
	 */
	public String getPageURL(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		String query = request.getQueryString();
		if (url.length() > 0 && query != null) {
			url += "?" + query;
		}
		return url;
	}

	/**
	 * Get DiscrepancyNote info.
	 * 
	 * @param request
	 *            the HttpServletRequest.
	 * @param note
	 *            the DiscrepancyNoteBean.
	 * @return DiscrepancyNoteBean with info.
	 */
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
				// default value
				note.setDiscrepancyNoteTypeId(DiscrepancyNoteType.FAILEDVAL.getId());
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
			if (StringUtil.isBlank(note.getSubjectName())) {
				note.setSubjectName(ss.getName());
			}
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
			if (StringUtil.isBlank(note.getSubjectName())) {
				StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(event.getStudySubjectId());
				note.setSubjectName(ss.getName());
			}

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

	/**
	 * Check if user have role in the current study.
	 * 
	 * @param request
	 *            the HttpServletRequest.
	 * @param response
	 *            the HttpServletResponse.
	 * @param ub
	 *            the UserAccountBean.
	 * @param studyId
	 *            int.
	 * @param siteId
	 *            int.
	 */
	public void checkRoleByUserAndStudy(HttpServletRequest request, HttpServletResponse response, UserAccountBean ub,
			int studyId, int siteId) {
		StudyUserRoleBean studyUserRole = ub.getRoleByStudy(studyId);
		StudyUserRoleBean siteUserRole = new StudyUserRoleBean();
		if (siteId != 0) {
			siteUserRole = ub.getRoleByStudy(siteId);
		}
		if (studyUserRole.getRole().equals(Role.INVALID) && siteUserRole.getRole().equals(Role.INVALID)) {
			addPageMessage(getResPage().getString("no_have_correct_privilege_current_study") + " "
					+ getResPage().getString("change_active_study_or_contact"), request);
			forwardPage(Page.MENU_SERVLET, request, response);
		}
	}

	protected Date getJobStartTime(HashMap errors, FormProcessor fp) {

		Date serverStartJobDate = null;
		int h = fp.getInt(JOB_HOUR);
		int m = fp.getInt(JOB_MINUTE);

		if (!(h >= 0)) {
			List<String> messages = new ArrayList<String>();
			messages.add(getResPage().getString("select_the_hour_start"));
			errors.put("jobHour", messages);
		} else if (!(m >= 0)) {
			List<String> messages = new ArrayList<String>();
			messages.add(getResPage().getString("select_the_start_minute"));
			errors.put("jobHour", messages);
		} else {
			DateTimeZone userTimeZone = DateTimeZone.forID(getUserAccountBean().getUserTimeZoneId());
			DateTime userStartJobDate = new DateTime(userTimeZone).withHourOfDay(h).withMinuteOfHour(m);
			serverStartJobDate = userStartJobDate.withZone(DateTimeZone.getDefault()).toDate();
		}
		return serverStartJobDate;
	}

	/**
	 * Populate Custom Elements Config from request.
	 * 
	 * @param request
	 *            the HttpServletRequest.
	 */
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

	/**
	 * Set domain name to the Session and CoreResources.
	 * 
	 * @param request
	 *            the HttpServletRequest.
	 */
	public static void setDomainName(HttpServletRequest request) {
		String domainName = request.getParameter(DOMAIN_NAME);

		if (domainName != null) {
			request.getSession().setAttribute(DOMAIN_NAME, domainName);
		} else {
			request.getSession().setAttribute(DOMAIN_NAME, request.getServerName());
		}
		CoreResources.setDomainName(domainName);
	}

	/**
	 * Get value from hash map.
	 * 
	 * @param h
	 *            the HashMap.
	 * @param key
	 *            Integer key.
	 * @return value.
	 */
	public int getIntById(HashMap h, Integer key) {
		Integer value = (Integer) h.get(key);
		if (value == null) {
			return 0;
		} else {
			return value;
		}
	}

	/**
	 * Get action depending on DataEntryStage.
	 * 
	 * @param stage
	 *            DataEntryStage.
	 * @return String action.
	 */
	public String getActionForStage(DataEntryStage stage) {
		if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY)) {
			return ACTION_CONTINUE_INITIAL_DATA_ENTRY;
		} else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)) {
			return ACTION_START_DOUBLE_DATA_ENTRY;
		} else if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
			return ACTION_CONTINUE_DOUBLE_DATA_ENTRY;
		} else if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)) {
			return ACTION_ADMINISTRATIVE_EDITING;
		}
		return "";
	}

	/**
	 * Get ArrayList of sections.
	 * 
	 * @param ecb
	 *            EventCRFBean.
	 * @param sdao
	 *            SectionDAO.
	 * @param igdao
	 *            ItemGroupDAO.
	 * @return an ArrayList of Sections.
	 */
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

	/**
	 * Get ArrayList of sections by CRF version ID.
	 * 
	 * @param crfVersionId
	 *            int.
	 * @return ArrayList of sections.
	 */
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

	/**
	 * Get DisplayBean by CRF version ID.
	 * 
	 * @param crfVersionId
	 *            int.
	 * @return DisplayTableOfContentsBean.
	 */
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

	/**
	 * Get DisplayBean by EventCRFBean.
	 * 
	 * @param ecb
	 *            EventCRFBean.
	 * @return DisplayTableOfContentsBean.
	 */
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
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
				.findByPK(seb.getStudyEventDefinitionId());
		answer.setStudyEventDefinition(sedb);

		CRFVersionBean cvb = (CRFVersionBean) getCRFVersionDAO().findByPK(ecb.getCRFVersionId());
		answer.setCrfVersion(cvb);

		CRFBean cb = (CRFBean) getCRFDAO().findByPK(cvb.getCrfId());
		answer.setCrf(cb);

		StudyBean studyForStudySubject = getStudyDAO().findByStudySubjectId(ssb.getId());
		EventDefinitionCRFBean edcb = getEventDefinitionCRFDAO()
				.findByStudyEventDefinitionIdAndCRFId(studyForStudySubject, sedb.getId(), cb.getId());
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
					if (dynamicsMetadataService.hasShowingDynGroupInSection(s.getId(), s.getCRFVersionId(),
							ecb.getId())) {
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
	 * @param ub
	 *            UserAccountBean
	 * @param currentRole
	 *            StudyUserRoleBean
	 * @param status
	 *            SubjectEventStatus
	 * @param study
	 *            StudyBean
	 * @return The list of DisplayEventCRFBeans for this study event.
	 */
	protected ArrayList getDisplayEventCRFs(ArrayList eventCRFs, UserAccountBean ub, StudyUserRoleBean currentRole, SubjectEventStatus status, StudyBean study) {
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
				edc.setVersions((ArrayList) cvdao.findAllActiveByCRF(edc.getCrfId()));
				String selectedIds = edc.getSelectedVersionIds();
				if (edc.getParentId() > 0 && selectedIds != null && !selectedIds.trim().equals("")) {
					List<String> idList = Arrays.asList(selectedIds.split(","));
					Iterator<CRFVersionBean> iterator = edc.getVersions().iterator();
					while (iterator.hasNext()) {
						CRFVersionBean crfVersionBean = iterator.next();
						if (!idList.contains(Integer.toString(crfVersionBean.getId()))) {
							iterator.remove();
						}
					}
				}
				dec.setEventDefinitionCRF(edc);
				dec.setFlags(ecb, ub, currentRole, edc);
				dec.getEventCRF()
						.setStudyEventBean((StudyEventBean) getStudyEventDAO().findByPK(ecb.getStudyEventId()));

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
	 * @param status
	 *            SubjectEventStatus
	 * @return The list of event definitions for which no event CRF exists.
	 */
	protected ArrayList getUncompletedCRFs(ArrayList eventDefinitionCRFs, ArrayList eventCRFs, SubjectEventStatus status) {
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
			if (!idata.isEmpty()) {
				// this crf has data already
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
			Boolean b = (Boolean) completed.get(edcrf.getCrfId());
			EventCRFBean ev = (EventCRFBean) startedButIncompleted.get(edcrf.getCrfId());
			if (b == null || !b) {

				dedc.setEventCRF(ev);
				answer.add(dedc);

			}
		}
		return answer;
	}

	/**
	 * Get DisplayStudyEventBean by subject.
	 * 
	 * @param studySub
	 *            StudySubjectBean.
	 * @param ub
	 *            UserAccountBean.
	 * @param currentRole
	 *            StudyUserRoleBean.
	 * @param excludeEventDefinishionsRemoved
	 *            boolean.
	 * @return ArrayList of DisplayStidyEventBean.
	 */
	protected ArrayList<DisplayStudyEventBean> getDisplayStudyEventsForStudySubject(StudySubjectBean studySub,
			UserAccountBean ub, StudyUserRoleBean currentRole, boolean excludeEventDefinishionsRemoved) {
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
			StudySubjectBean studySubject = ssdao.findByPK(event.getStudySubjectId());

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
			de.setDisplayEventCRFs(getDisplayEventCRFs(eventCRFs, ub, currentRole,
					event.getSubjectEventStatus(), study));
			ArrayList al = getUncompletedCRFs(eventDefinitionCRFs, eventCRFs, event.getSubjectEventStatus());
			populateUncompletedCRFsWithCRFAndVersions(al);
			de.setUncompletedCRFs(al);

			de.setMaximumSampleOrdinal(sedao.getMaxSampleOrdinal(sed, studySubject));

			displayEvents.add(de);

		}

		return displayEvents;
	}

	/**
	 * Populates uncompleted crfs with crf and versions.
	 *
	 * @param uncompletedEventDefinitionCRFs
	 *            list of uncompleted CRFs
	 */
	protected void populateUncompletedCRFsWithCRFAndVersions(ArrayList uncompletedEventDefinitionCRFs) {
		CRFDAO cdao = new CRFDAO(getDataSource());
		CRFVersionDAO cvdao = new CRFVersionDAO(getDataSource());
		Comparator<CRFVersionBean> versionComparator = new Comparator<CRFVersionBean>() {
			public int compare(CRFVersionBean v1, CRFVersionBean v2) {
				return v1.getName().compareTo(v2.getName());
			}
		};

		int size = uncompletedEventDefinitionCRFs.size();
		for (int i = 0; i < size; i++) {
			DisplayEventDefinitionCRFBean dedcrf = (DisplayEventDefinitionCRFBean) uncompletedEventDefinitionCRFs
					.get(i);
			CRFBean cb = (CRFBean) cdao.findByPK(dedcrf.getEdc().getCrfId());
			// note that we do not check status in the above query, so let's
			// check it here, tbh 102007
			if (cb.getStatus().equals(Status.AVAILABLE)) {
				// the above does not allow us to show the CRF as a thing with
				// status of 'invalid' so we have to
				// go to the JSP for this one, I think
				dedcrf.getEdc().setCrf(cb);

				ArrayList theVersions = (ArrayList) cvdao.findAllActiveByCRF(dedcrf.getEdc().getCrfId());
				ArrayList versions = new ArrayList();
				HashMap<String, CRFVersionBean> crfVersionIds = new HashMap<String, CRFVersionBean>();

				for (Object theVersion : theVersions) {
					CRFVersionBean crfVersion = (CRFVersionBean) theVersion;
					crfVersionIds.put(String.valueOf(crfVersion.getId()), crfVersion);
				}

				if (!dedcrf.getEdc().getSelectedVersionIds().equals("")) {
					String[] kk = dedcrf.getEdc().getSelectedVersionIds().split(",");
					for (String string : kk) {
						if (crfVersionIds.get(string) != null) {
							versions.add(crfVersionIds.get(string));
						}
					}
				} else {
					versions = theVersions;
				}

				Collections.sort(versions, versionComparator);
				dedcrf.getEdc().setVersions(versions);
				if (versions.size() != 0) {
					boolean isLocked = false;
					for (Object version : versions) {
						CRFVersionBean crfvb = (CRFVersionBean) version;
						logger.info("...checking versions..." + crfvb.getName());
						if (!crfvb.getStatus().equals(Status.AVAILABLE)) {
							logger.info("found a non active crf version");
							isLocked = true;
						}
					}
					logger.info("re-set event def, line 240: " + isLocked);
					if (isLocked) {
						dedcrf.setStatus(Status.LOCKED);
						dedcrf.getEventCRF().setStage(DataEntryStage.LOCKED);
					}
					uncompletedEventDefinitionCRFs.set(i, dedcrf);
				} else {
					dedcrf.setStatus(Status.LOCKED);
					dedcrf.getEventCRF().setStage(DataEntryStage.LOCKED);
					uncompletedEventDefinitionCRFs.set(i, dedcrf);
				}
			} else {
				dedcrf.getEdc().setCrf(cb);
				logger.info("_found a non active crf _");
				dedcrf.setStatus(Status.LOCKED);
				dedcrf.getEventCRF().setStage(DataEntryStage.LOCKED);
				dedcrf.getEdc().getCrf().setStatus(Status.LOCKED);
				uncompletedEventDefinitionCRFs.set(i, dedcrf);
			}
			if (dedcrf.getStatus() == Status.LOCKED && (dedcrf.getEventCRF().getCrfVersion() == null
					|| dedcrf.getEventCRF().getCrfVersion().getId() == 0)) {
				dedcrf.getEventCRF()
						.setCrfVersion((CRFVersionBean) cvdao.findByPK(dedcrf.getEdc().getDefaultVersionId()));
			}
		}
	}

	/**
	 * Prepares CRF Version for locked CRFs.
	 *
	 * @param fullCrfList
	 *            List of Object's
	 */
	private void prepareCRFVersionForLockedCRFs(List<Object> fullCrfList) {
		try {
			CRFVersionDAO crfvdao = getCRFVersionDAO();
			for (Object object : fullCrfList) {
				if (object instanceof DisplayEventDefinitionCRFBean) {
					DisplayEventDefinitionCRFBean dedCrfBean = (DisplayEventDefinitionCRFBean) object;
					if (dedCrfBean.getStatus() == Status.LOCKED && (dedCrfBean.getEventCRF().getCrfVersion() == null
							|| dedCrfBean.getEventCRF().getCrfVersion().getId() == 0)) {
						dedCrfBean.getEventCRF().setCrfVersion(
								(CRFVersionBean) crfvdao.findByPK(dedCrfBean.getEdc().getDefaultVersionId()));
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}

	/**
	 * Populates uncompleted CRFs with an owner.
	 *
	 * @param ds
	 *            DataSource
	 * @param displayEventDefinitionCRFBeans
	 *            List of DisplayEventDefinitionCRFBean's
	 */
	protected void populateUncompletedCRFsWithAnOwner(
			List<DisplayEventDefinitionCRFBean> displayEventDefinitionCRFBeans) {
		if (displayEventDefinitionCRFBeans == null || displayEventDefinitionCRFBeans.isEmpty()) {
			return;
		}
		EventCRFBean eventCRFBean;
		UserAccountBean userAccountBean;
		UserAccountDAO userAccountDAO = getUserAccountDAO();
		for (DisplayEventDefinitionCRFBean dedcBean : displayEventDefinitionCRFBeans) {
			eventCRFBean = dedcBean.getEventCRF();
			if (eventCRFBean != null && eventCRFBean.getOwner() == null && eventCRFBean.getOwnerId() > 0) {
				userAccountBean = (UserAccountBean) userAccountDAO.findByPK(eventCRFBean.getOwnerId());

				eventCRFBean.setOwner(userAccountBean);
			}
			// Failing the above, obtain the owner from the EventDefinitionCRFBean
			if (eventCRFBean != null && eventCRFBean.getOwner() == null) {
				int ownerId = dedcBean.getEdc().getOwnerId();
				if (ownerId > 0) {
					userAccountBean = (UserAccountBean) userAccountDAO.findByPK(ownerId);
					eventCRFBean.setOwner(userAccountBean);
				}
			}
		}
	}

	/**
	 * If DiscrepancyNoteBeans have a certain column value, then set flags that a JSP will check in the request
	 * attribute. This is a convenience method called by the processRequest() method.
	 *
	 * @param discBeans
	 *            List of DiscrepancyNoteBean's
	 * @param seb
	 *            StudyEventBean
	 * @param request
	 *            HttpServletRequest
	 */
	protected void setRequestAttributesForNotes(List<DiscrepancyNoteBean> discBeans, StudyEventBean seb,
			HttpServletRequest request) {
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) seddao.findByPK(seb.getStudyEventDefinitionId());
		List<DiscrepancyNoteBean> locationDNotes = new ArrayList<DiscrepancyNoteBean>();
		List<DiscrepancyNoteBean> dateStartDNotes = new ArrayList<DiscrepancyNoteBean>();
		List<DiscrepancyNoteBean> dateEndDNotes = new ArrayList<DiscrepancyNoteBean>();
		for (DiscrepancyNoteBean discrepancyNoteBean : discBeans) {
			// method discrepancyNoteBean.getEvent.getId() return 0 for all DNs
			if (discrepancyNoteBean.getEventName().equalsIgnoreCase(sedBean.getName())
					&& discrepancyNoteBean.getEntityId() == seb.getId()) {
				if ("location".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
					locationDNotes.add(discrepancyNoteBean);
				} else if ("date_start".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
					dateStartDNotes.add(discrepancyNoteBean);
				} else if ("date_end".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
					dateEndDNotes.add(discrepancyNoteBean);
				}
			}
		}
		request.setAttribute("numberOfLocationDNotes", locationDNotes.size());
		request.setAttribute("numberOfDateStartDNotes", dateStartDNotes.size());
		request.setAttribute("numberOfDateEndDNotes", dateEndDNotes.size());

		request.setAttribute("imageFileNameForLocation",
				DiscrepancyNoteUtil.getImageFileNameForFlagByResolutionStatusId(
						DiscrepancyNoteUtil.getDiscrepancyNoteResolutionStatus(locationDNotes)));
		request.setAttribute("imageFileNameForDateStart",
				DiscrepancyNoteUtil.getImageFileNameForFlagByResolutionStatusId(
						DiscrepancyNoteUtil.getDiscrepancyNoteResolutionStatus(dateStartDNotes)));
		request.setAttribute("imageFileNameForDateEnd", DiscrepancyNoteUtil.getImageFileNameForFlagByResolutionStatusId(
				DiscrepancyNoteUtil.getDiscrepancyNoteResolutionStatus(dateEndDNotes)));
	}

	/**
	 * Prepares full CRF list for data entry pages.
	 * 
	 * @param study
	 *            StudyBean
	 * @param studySubjectBean
	 *            StudySubjectBean
	 * @param studyEventBean
	 *            StudyEventBean
	 * @param eventCRFs
	 *            ArrayList
	 * @param eventDefinitionCRFs
	 *            ArrayList
	 * @return List
	 */
	public List<Object> prepareFullCrfList(StudyBean study, StudySubjectBean studySubjectBean,
			StudyEventBean studyEventBean, ArrayList<EventCRFBean> eventCRFs,
			ArrayList<EventDefinitionCRFBean> eventDefinitionCRFs) {
		StudyBean currentStudy = getCurrentStudy();
		StudyUserRoleBean currentRole = getCurrentRole();
		UserAccountBean userAccountBean = getUserAccountBean();

		HttpServletRequest request = RequestUtil.getRequest();
		SessionManager sm = getSessionManager(request);

		SubjectEventStatusUtil.fillDoubleDataOwner(eventCRFs, sm);

		ArrayList uncompletedEventDefinitionCRFs = getUncompletedCRFs(eventDefinitionCRFs, eventCRFs,
				studyEventBean.getSubjectEventStatus());

		populateUncompletedCRFsWithCRFAndVersions(uncompletedEventDefinitionCRFs);

		populateUncompletedCRFsWithAnOwner(uncompletedEventDefinitionCRFs);

		ArrayList displayEventCRFs = getDisplayEventCRFs(eventCRFs, userAccountBean, currentRole,
				studyEventBean.getSubjectEventStatus(), study);

		if (currentStudy.getParentStudyId() > 0) {
			HideCRFManager hideCRFManager = HideCRFManager.createHideCRFManager();
			uncompletedEventDefinitionCRFs = hideCRFManager
					.removeHiddenEventDefinitionCRFBeans(uncompletedEventDefinitionCRFs);
			displayEventCRFs = hideCRFManager.removeHiddenEventCRFBeans(displayEventCRFs);
		}

		request.setAttribute(BEAN_STUDY_EVENT, studyEventBean);
		request.setAttribute(BEAN_STUDY_SUBJECT, studySubjectBean);
		request.setAttribute(BEAN_DISPLAY_EVENT_CRFS, displayEventCRFs);
		request.setAttribute(BEAN_UNCOMPLETED_EVENTDEFINITIONCRFS, uncompletedEventDefinitionCRFs);

		List<Object> fullCrfList = new ArrayList<Object>();
		fullCrfList.addAll(uncompletedEventDefinitionCRFs);
		fullCrfList.addAll(displayEventCRFs);
		Collections.sort(fullCrfList, new CrfComparator());
		request.setAttribute(FULL_CRF_LIST, fullCrfList);

		prepareCRFVersionForLockedCRFs(fullCrfList);

		return fullCrfList;
	}

	/**
	 * Prepares node map for full CRF list.
	 * 
	 * @param fullCrfList
	 *            List
	 * @param studySubjectBean
	 *            StudySubjectBean
	 * @param eventName
	 *            String
	 * @param eventId
	 *            int
	 * @return Map
	 */
	public Map<Integer, String> prepareNodeMapForFullCrfList(List<Object> fullCrfList,
			StudySubjectBean studySubjectBean, String eventName, int eventId) {
		StudyBean currentStudy = getCurrentStudy();
		UserAccountBean userAccountBean = getUserAccountBean();
		DiscrepancyNoteDAO discrepancyNoteDAO = getDiscrepancyNoteDAO();
		Map<Integer, String> notedMap = new HashMap<Integer, String>();
		for (Object bean : fullCrfList) {
			if (bean instanceof DisplayEventCRFBean) {
				DisplayEventCRFBean displayEventCRFBean = (DisplayEventCRFBean) bean;

				String crfName = displayEventCRFBean.getEventCRF().getCrf().getName();
				Integer crfId = displayEventCRFBean.getEventCRF().getCrf().getId();

				if (!getMaskingService().isEventDefinitionCRFMasked(displayEventCRFBean.getEventDefinitionCRF().getId(),
						userAccountBean.getId(), displayEventCRFBean.getEventDefinitionCRF().getStudyId())) {
					if (discrepancyNoteDAO.doesCRFHaveUnclosedDNsInStudyForSubject(currentStudy, eventName, eventId,
							studySubjectBean.getLabel(), crfName)) {
						String crfFlagColor = "yellow";
						if (discrepancyNoteDAO.doesCRFHaveNewDNsInStudyForSubject(currentStudy, eventName, eventId,
								studySubjectBean.getLabel(), crfName)) {
							crfFlagColor = "red";
						}
						notedMap.put(crfId, crfFlagColor);
					}
				}

			} else if (bean instanceof DisplayEventDefinitionCRFBean) {
				DisplayEventDefinitionCRFBean displayEventDefinitionCRFBean = (DisplayEventDefinitionCRFBean) bean;

				String crfName = displayEventDefinitionCRFBean.getEdc().getCrf().getName();
				Integer crfId = displayEventDefinitionCRFBean.getEdc().getCrf().getId();

				if (!getMaskingService().isEventDefinitionCRFMasked(displayEventDefinitionCRFBean.getEdc().getId(),
						userAccountBean.getId(), displayEventDefinitionCRFBean.getEdc().getStudyId())) {
					if (discrepancyNoteDAO.doesCRFHaveUnclosedDNsInStudyForSubject(currentStudy, eventName, eventId,
							studySubjectBean.getLabel(), crfName)) {
						String crfFlagColor = "yellow";
						if (discrepancyNoteDAO.doesCRFHaveNewDNsInStudyForSubject(currentStudy, eventName, eventId,
								studySubjectBean.getLabel(), crfName)) {
							crfFlagColor = "red";
						}
						notedMap.put(crfId, crfFlagColor);
					}
				}
			}
		}
		return notedMap;
	}

	private List<DiscrepancyNoteBean> extractCoderNotes(List<DiscrepancyNoteBean> notes, HttpServletRequest request) {

		if (isCoder(getUserAccountBean(request), request)) {

			List<DiscrepancyNoteBean> filteredDiscrepancyNotes = new ArrayList<DiscrepancyNoteBean>();

			for (DiscrepancyNoteBean discrepancyNote : notes) {

				UserAccountBean owner = (UserAccountBean) getUserAccountDAO().findByPK(discrepancyNote.getOwnerId());
				UserAccountBean assignedUser = (UserAccountBean) getUserAccountDAO()
						.findByPK(discrepancyNote.getAssignedUserId());

				if (isCoder(assignedUser, request) || isCoder(owner, request)) {

					filteredDiscrepancyNotes.add(discrepancyNote);
				}
			}

			return filteredDiscrepancyNotes;

		} else {
			return notes;
		}
	}

	protected List<DiscrepancyNoteBean> filterNotesByUserRole(List<DiscrepancyNoteBean> notes,
			HttpServletRequest request) {
		StudyUserRoleBean currentRole = getCurrentRole(request);
		if (currentRole.getRole().equals(Role.STUDY_CODER)) {
			notes = extractCoderNotes(notes, request);
		}
		return notes;
	}

	protected boolean isEvaluator(UserAccountBean loggedInUser, HttpServletRequest request) {
		// site
		if (getCurrentStudy(request).isSite(getCurrentStudy(request).getParentStudyId())) {
			return loggedInUser.getRoleByStudy(getCurrentStudy(request).getParentStudyId()).getRoleCode()
					.equalsIgnoreCase("study_evaluator");
		}
		// Otherwise, study
		return loggedInUser.getRoleByStudy(getCurrentStudy(request).getId()).getRoleCode()
				.equalsIgnoreCase("study_evaluator");
	}

	protected boolean isCoder(UserAccountBean loggedInUser, HttpServletRequest request) {
		// site
		if (getCurrentStudy(request).isSite(getCurrentStudy(request).getParentStudyId())) {
			return loggedInUser.getRoleByStudy(getCurrentStudy(request).getParentStudyId()).getRoleCode()
					.equalsIgnoreCase("study_coder");
		}
		// Otherwise, study
		return loggedInUser.getRoleByStudy(getCurrentStudy(request).getId()).getRoleCode()
				.equalsIgnoreCase("study_coder");
	}

	/* Determining the resolution status that will be shown in color flag for an item. */
	protected int getDiscrepancyNoteResolutionStatus(HttpServletRequest request, DiscrepancyNoteDAO dndao,
			int itemDataId, List<DiscrepancyNoteBean> list) {

		int resolutionStatus = ResolutionStatus.INVALID.getId();
		int resolutionStatusDisplayPriority = ResolutionStatus.INVALID.getDisplayPriority();
		boolean hasOtherThread = false;

		List<DiscrepancyNoteBean> existingNotes = dndao.findExistingNotesForItemData(itemDataId);
		existingNotes = filterNotesByUserRole(existingNotes, request);
		for (Object obj : existingNotes) {
			DiscrepancyNoteBean note = (DiscrepancyNoteBean) obj;
			/*
			 * We would only take the resolution status of the parent note of any note thread. If there are more than
			 * one note thread, the thread with the worst resolution status will be taken.
			 */
			if (note.getParentDnId() == 0) {
				if (hasOtherThread) {
					if (resolutionStatusDisplayPriority > note.getResStatus().getDisplayPriority()) {
						resolutionStatus = note.getResolutionStatusId();
						resolutionStatusDisplayPriority = note.getResStatus().getDisplayPriority();
					}
				} else {
					resolutionStatus = note.getResolutionStatusId();
					resolutionStatusDisplayPriority = note.getResStatus().getDisplayPriority();
				}
				hasOtherThread = true;
			}
		}

		if (list == null || list.isEmpty()) {
			return resolutionStatus;
		}

		for (Object obj : filterNotesByUserRole(list, request)) {
			DiscrepancyNoteBean note = (DiscrepancyNoteBean) obj;
			if (note.getParentDnId() == 0) {
				if (hasOtherThread) {
					if (resolutionStatusDisplayPriority > note.getResStatus().getDisplayPriority()) {
						resolutionStatus = note.getResolutionStatusId();
						resolutionStatusDisplayPriority = note.getResStatus().getDisplayPriority();
					}
				} else {
					resolutionStatus = note.getResolutionStatusId();
					resolutionStatusDisplayPriority = note.getResStatus().getDisplayPriority();
				}
				hasOtherThread = true;
			}
		}
		return resolutionStatus;
	}

	/**
	 * SelectNotStartedOrRepeatingSortedEventDefs method.
	 *
	 * @param ssb
	 *            StudySubjectBean
	 * @param parentStudyId
	 *            int
	 * @param seddao
	 *            StudyEventDefinitionDAO
	 * @param sgcdao
	 *            StudyGroupClassDAO
	 * @param sedao
	 *            StudyEventDAO
	 * @return ArrayList<StudyEventDefinitionBean>
	 */
	protected ArrayList<StudyEventDefinitionBean> selectNotStartedOrRepeatingSortedEventDefs(StudySubjectBean ssb,
			int parentStudyId, StudyEventDefinitionDAO seddao, StudyGroupClassDAO sgcdao, StudyEventDAO sedao) {

		ArrayList<StudyEventDefinitionBean> result = new ArrayList<StudyEventDefinitionBean>();
		Map<Integer, StudyEventBean> studyEventDefinitionIdToStudyEvent = new HashMap<Integer, StudyEventBean>();
		ArrayList<StudyEventBean> studyEvents = sedao.findAllByStudySubject(ssb);
		for (StudyEventBean studyEvent : studyEvents) {
			studyEventDefinitionIdToStudyEvent.put(studyEvent.getStudyEventDefinitionId(), studyEvent);
		}

		StudyGroupClassBean defaultStudyGroupClassBean = sgcdao.findDefaultByStudyId(parentStudyId);
		boolean defaultStudyGroupClassBeanExist = !(defaultStudyGroupClassBean == null
				|| defaultStudyGroupClassBean.getId() == 0);

		List<StudyGroupClassBean> allActiveDynGroupClasses = sgcdao.findAllActiveDynamicGroupsByStudyId(parentStudyId);
		Collections.sort(allActiveDynGroupClasses, StudyGroupClassBean.comparatorForDynGroupClasses);

		// ordered eventDefs from dynGroups
		if (defaultStudyGroupClassBeanExist || ssb.getDynamicGroupClassId() != 0) {
			for (StudyGroupClassBean dynGroup : allActiveDynGroupClasses) {
				List<StudyEventDefinitionBean> orderedEventDefinitionsFromDynGroup = seddao
						.findAllAvailableAndOrderedByStudyGroupClassId(dynGroup.getId());
				for (StudyEventDefinitionBean eventDefinition : orderedEventDefinitionsFromDynGroup) {
					if (dynGroup.isDefault() || (ssb.getDynamicGroupClassId() != 0
							&& dynGroup.getId() == ssb.getDynamicGroupClassId())) {
						// eventDefs from defDynGroup and subject's dynGroup
						if (studyEventDefinitionIdToStudyEvent.keySet().contains(eventDefinition.getId())) {
							if (studyEventDefinitionIdToStudyEvent.get(eventDefinition.getId()).getSubjectEventStatus()
									.isNotScheduled() || eventDefinition.isRepeating()) {
								result.add(eventDefinition);
							}
						} else {
							result.add(eventDefinition);
						}
					} else {
						// eventDefs from others dynGroups
						if (eventDefinition.isRepeating()) {
							result.add(eventDefinition);
						}
					}
				}
			}
		}

		List<StudyEventDefinitionBean> eventDefinitionsNotFromDynGroup = seddao.findAllActiveNotClassGroupedAndFromRemovedGroupsByStudyId(parentStudyId);
		// sort by study event definition ordinal
		Collections.sort(eventDefinitionsNotFromDynGroup);
		// filter notStarted and repeating eventDefs
		ArrayList notStartedAndRepeatingEventDefinitions = new ArrayList();
		for (Object anEventDefinitionsNotFromDynGroup : eventDefinitionsNotFromDynGroup) {
			StudyEventDefinitionBean eventDefinition = (StudyEventDefinitionBean) anEventDefinitionsNotFromDynGroup;
			if (studyEventDefinitionIdToStudyEvent.keySet().contains(eventDefinition.getId())) {
				if (eventDefinition.isRepeating()) {
					notStartedAndRepeatingEventDefinitions.add(eventDefinition);
				}
			} else {
				notStartedAndRepeatingEventDefinitions.add(eventDefinition);
			}
		}
		result.addAll(notStartedAndRepeatingEventDefinitions);
		return result;
	}

	/**
	 * Returns EntityBeanTable.
	 * 
	 * @return EntityBeanTable
	 */
	public EntityBeanTable getEntityBeanTable() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		FormProcessor fp = new FormProcessor(request);
		EntityBeanTable answer = new EntityBeanTable();
		answer.setLocale(LocaleResolver.getLocale());
		String sortingColumn = request.getParameter(EBL_SORT_COLUMN);
		if (sortingColumn != null && !"".equals(sortingColumn)) {
			answer.setSortingColumnExplicitlySet(true);
		}

		answer.setCurrPageNumber(fp.getInt(EBL_PAGE));
		answer.setSortingColumnInd(fp.getInt(EBL_SORT_COLUMN));
		answer.setKeywordFilter(fp.getString(EBL_FILTER_KEYWORD));

		// this code says that for each of the boolean properties of the table,
		// if no value was specified on the form or in the GET query, then
		// keep the default value for that bit
		// otherwise, the bits will just be forced to false
		String[] blnFields = {EBL_SORT_ORDER, EBL_FILTERED, EBL_PAGINATED};

		for (int i = 0; i < blnFields.length; i++) {
			String value = fp.getString(blnFields[i]);
			boolean b = fp.getBoolean(blnFields[i]);
			if (!"".equals(value)) {
				if (i == 0) {
					answer.setAscendingSort(b);
				} else if (i == 1) {
					answer.setFiltered(b);
				} else {
					answer.setPaginated(b);
				}
			}
		}

		return answer;
	}

	protected void checkIfStudySponsor(HttpServletRequest request) throws InsufficientPermissionException {
		if (getCurrentRole(request).isStudySponsor()) {
			throw new InsufficientPermissionException(Page.MENU,
					getResException().getString("you_may_not_perform_administrative_functions"), "1");
		}
	}
}
