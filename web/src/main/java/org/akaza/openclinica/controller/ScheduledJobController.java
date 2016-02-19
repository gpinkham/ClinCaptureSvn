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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clinovo.util.DateUtil;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.extract.XsltTriggerService;
import org.akaza.openclinica.web.table.scheduledjobs.ScheduledJobTableFactory;
import org.akaza.openclinica.web.table.scheduledjobs.ScheduledJobs;
import org.akaza.openclinica.web.table.sdv.SDVUtil;
import org.jmesa.facade.TableFacade;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clinovo.i18n.LocaleResolver;

/**
 * @author jnyayapathi Controller for listing all the scheduled jobs. Also an interface for canceling the jobs which are
 *         running.
 */
@SuppressWarnings({"unchecked"})
@Controller("ScheduledJobController")
public class ScheduledJobController extends SpringController {

	public static final String SCHEDULED_TABLE_ATTRIBUTE = "scheduledTableAttribute";

	private static final String SCHEDULER = "schedulerFactoryBean";

	@Autowired
	private ScheduledJobTableFactory scheduledJobTableFactory;
	public static final String EP_BEAN = "epBean";

	@Autowired
	private SDVUtil sdvUtil;

	protected static final Logger LOGGER = LoggerFactory
			.getLogger("org.akaza.openclinica.controller.ScheduledJobController");

	/**
	 * Default class constructor.
	 */
	public ScheduledJobController() {

	}

	/**
	 *
	 *
	 * @param request
	 *            The incoming request.
	 * @param response
	 *            The response to redirect to.
	 * @return The map with job attributes that will be placed on the UX.
	 * @throws SchedulerException
	 *             for job schedule exceptions.
	 */
	@RequestMapping("/listCurrentScheduledJobs")
	public ModelMap listScheduledJobs(HttpServletRequest request, HttpServletResponse response)
			throws SchedulerException {
		if (!mayProceed(request)) {
			try {
				response.sendRedirect(request.getContextPath() + "/MainMenu?message=authentication_failed");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		ModelMap gridMap = new ModelMap();
		StdScheduler scheduler = getScheduler(request);
		boolean showMoreLink = request.getParameter("showMoreLink") == null
				|| Boolean.parseBoolean(request.getParameter("showMoreLink"));

		request.setAttribute("showMoreLink", showMoreLink + "");

		request.setAttribute("imagePathPrefix", "../");

		ArrayList<String> pageMessages = (ArrayList<String>) request.getAttribute("pageMessages");
		if (pageMessages == null) {
			pageMessages = new ArrayList<String>();
		}

		request.setAttribute("pageMessages", pageMessages);

		List<JobExecutionContext> listCurrentJobs = scheduler.getCurrentlyExecutingJobs();
		Iterator<JobExecutionContext> itCurrentJobs = listCurrentJobs.iterator();
		List<String> currentJobList = new ArrayList<String>();
		while (itCurrentJobs.hasNext()) {
			JobExecutionContext temp = itCurrentJobs.next();
			currentJobList.add(temp.getTrigger().getKey().getName() + temp.getTrigger().getKey().getGroup());
		}

		for (String group : scheduler.getPausedTriggerGroups()) {

			Set<TriggerKey> legacyTriggers = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(group));
			for (TriggerKey triggerKey : legacyTriggers) {
				Trigger trigger = scheduler.getTrigger(triggerKey);
				logMe("Paused Job Group" + trigger.getJobKey().getGroup());
				logMe("Paused Job Name:" + trigger.getJobKey().getName());
				logMe("Paused Trigger Name:" + trigger.getKey().getName());
				logMe("Paused Trigger Next Fire Time:" + trigger.getNextFireTime());
			}
		}

		List<String> triggerGroups = scheduler.getTriggerGroupNames();
		List<SimpleTriggerImpl> simpleTriggers = new ArrayList<SimpleTriggerImpl>();
		for (String triggerGroup : triggerGroups) {
			System.out.println("Group: " + triggerGroup + " contains the following triggers");
			Set<TriggerKey> legacyTriggers = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup));
			for (TriggerKey triggerKey : legacyTriggers) {
				logMe("- " + triggerKey.getName());
				Trigger trigger = scheduler.getTrigger(triggerKey);
				if (trigger instanceof SimpleTriggerImpl) {
					simpleTriggers.add((SimpleTriggerImpl) trigger);
				}
			}
		}

		List<ScheduledJobs> jobsScheduled = new ArrayList<ScheduledJobs>();

		for (SimpleTriggerImpl st : simpleTriggers) {

			ScheduledJobs jobs = new ScheduledJobs();

			ExtractPropertyBean epBean = null;
			if (st.getJobDataMap() != null) {
				epBean = (ExtractPropertyBean) st.getJobDataMap().get(EP_BEAN);
			}

			if (epBean != null) {
				StringBuilder checkbox = new StringBuilder("");
				checkbox.append("<input style='margin-right: 5px' type='checkbox' ").append("' />");

				StringBuilder actions = new StringBuilder("<table><tr class='innerTable'><td>");
				String contextPath = request.getContextPath();
				StringBuilder jsCodeString = new StringBuilder("this.form.method='GET'; this.form.action='")
						.append(contextPath).append("/pages/cancelScheduledJob").append("';")
						.append("this.form.theJobName.value='").append(st.getName()).append("';")
						.append("this.form.theJobGroupName.value='").append(st.getJobGroup()).append("';")
						.append("this.form.theTriggerName.value='").append(st.getName()).append("';")
						.append("this.form.theTriggerGroupName.value='").append(st.getGroup()).append("';")
						.append("this.form.submit();").append("setAccessedObjected(this);");

				actions.append("<td><input type=\"button\" class=\"button_medium\" value=\"")
						.append(ResourceBundleProvider.getResWord("skip_next_run")).append("\" name=\"skipNextRun\" ")
						.append("onclick=\"").append(jsCodeString.toString()).append("\" />")
						.append("<a href='#' data-cc-runningJobId='").append(st.getName())
						.append("' style='display: none;'></a>").append("</td></tr></table>");

				jobs.setCheckbox(checkbox.toString());
				jobs.setDatasetId(epBean.getDatasetName());
				UserAccountBean currentUser =
						(UserAccountBean) request.getSession().getAttribute(BaseController.USER_BEAN_NAME);
				jobs.setFireTime(DateUtil.printDate(st.getStartTime(), currentUser.getUserTimeZoneId(),
						DateUtil.DatePattern.TIMESTAMP_WITH_SECONDS, LocaleResolver.getLocale()));
				if (st.getNextFireTime() != null) {
					jobs.setScheduledFireTime(DateUtil.printDate(st.getNextFireTime(), currentUser.getUserTimeZoneId(),
							DateUtil.DatePattern.TIMESTAMP_WITH_SECONDS, LocaleResolver.getLocale()));
				}
				jobs.setExportFileName(epBean.getExportFileName()[0]);
				jobs.setAction(actions.toString());
				jobs.setJobStatus(currentJobList.contains(st.getJobName() + st.getGroup()) ? ResourceBundleProvider
						.getResWord("currently_executing") : ResourceBundleProvider.getResWord("scheduled"));

				jobsScheduled.add(jobs);
			}
		}
		logMe("totalRows" + jobsScheduled.size());

		request.setAttribute("totalJobs", jobsScheduled.size());

		request.setAttribute("jobs", jobsScheduled);

		TableFacade facade = scheduledJobTableFactory.createTable(request, response);
		String sdvMatrix = facade.render();
		gridMap.addAttribute(SCHEDULED_TABLE_ATTRIBUTE, sdvMatrix);
		return gridMap;

	}

	/**
	 * Method cancels job run.
	 *
	 * @param request
	 *            The incoming request.
	 * @param response
	 *            The response to redirect to.
	 * @param theJobName
	 *            The job name.
	 * @param theJobGroupName
	 *            The selected job group name.
	 * @param triggerName
	 *            the job trigger name.
	 * @param triggerGroupName
	 *            the job trigger group name.
	 * @param redirection
	 *            the page for redirection.
	 * @param model
	 *            Map The map with job attributes.
	 * @return The page for redirection.
	 * @throws SchedulerException
	 *             for job schedule exceptions.
	 */
	@RequestMapping("/cancelScheduledJob")
	public String cancelScheduledJob(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("theJobName") String theJobName, @RequestParam("theJobGroupName") String theJobGroupName,
			@RequestParam("theTriggerName") String triggerName,
			@RequestParam("theTriggerGroupName") String triggerGroupName,
			@RequestParam("redirection") String redirection, ModelMap model) throws SchedulerException {

		ResourceBundle resPageMessages = ResourceBundleProvider.getPageMessagesBundle(LocaleResolver.getLocale());
		MessageFormat messageFormat = new MessageFormat("");

		if (!mayProceed(request)) {
			try {
				response.sendRedirect(request.getContextPath() + "/MainMenu?message=authentication_failed");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		StdScheduler scheduler = getScheduler(request);

		Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(theJobName, theJobGroupName));
		SimpleTriggerImpl oldTrigger = trigger instanceof SimpleTriggerImpl ? (SimpleTriggerImpl) trigger : null;
		logMe("About to pause the job-->" + oldTrigger.getJobKey().getName() + "Job Group Name -->"
				+ oldTrigger.getJobKey().getGroup());

		if (oldTrigger != null) {
			Date startTime = new Date(oldTrigger.getStartTime().getTime() + oldTrigger.getRepeatInterval());
			if (triggerGroupName.equals(ExtractController.TRIGGER_GROUP_NAME)) {
				scheduler.interrupt(JobKey.jobKey(theJobName, theJobGroupName));
			}

			scheduler.pauseJob(JobKey.jobKey(theJobName, theJobGroupName));

			SimpleTriggerImpl newTrigger = new SimpleTriggerImpl();
			newTrigger.setName(triggerName);
			newTrigger.setGroup(triggerGroupName);
			newTrigger.setJobName(theJobName);
			newTrigger.setJobGroup(theJobGroupName);
			newTrigger.setMisfireInstruction(oldTrigger.getMisfireInstruction());
			newTrigger.setJobDataMap(oldTrigger.getJobDataMap());
			newTrigger.setRepeatCount(oldTrigger.getRepeatCount());
			newTrigger.setRepeatInterval(oldTrigger.getRepeatInterval());
			newTrigger
					.setMisfireInstruction(SimpleTriggerImpl.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
			newTrigger.setStartTime(startTime);
			newTrigger.setRepeatInterval(oldTrigger.getRepeatInterval());

			scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName));

			ArrayList<String> pageMessages = new ArrayList<String>();

			if (triggerGroupName.equals(ExtractController.TRIGGER_GROUP_NAME)) {
				scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName), newTrigger);

				messageFormat.applyPattern(resPageMessages.getString("job_has_been_cancelled"));
				Object[] arguments = {theJobName};
				pageMessages.add(messageFormat.format(arguments));
			} else if (triggerGroupName.equals(XsltTriggerService.TRIGGER_GROUP_NAME)) {

				JobDetailImpl jobDetailBean = new JobDetailImpl();
				jobDetailBean.setGroup(XsltTriggerService.TRIGGER_GROUP_NAME);
				jobDetailBean.setName(newTrigger.getName());
				jobDetailBean.setJobClass(org.akaza.openclinica.job.XsltStatefulJob.class);
				jobDetailBean.setJobDataMap(newTrigger.getJobDataMap());
				jobDetailBean.setDurability(true);

				scheduler.deleteJob(jobDetailBean.getKey());

				scheduler.scheduleJob(jobDetailBean, newTrigger);

				messageFormat.applyPattern(resPageMessages.getString("job_has_been_rescheduled"));
				Object[] arguments = {theJobName};
				pageMessages.add(messageFormat.format(arguments));

			}

			request.setAttribute("pageMessages", pageMessages);

			logMe("jobDetails>" + scheduler.getJobDetail(JobKey.jobKey(theJobName, theJobGroupName)));
		}
		sdvUtil.forwardRequestFromController(request, response, "/pages/" + redirection);
		return null;
	}

	private void logMe(String msg) {

		LOGGER.info(msg);
	}

	private StdScheduler getScheduler(HttpServletRequest request) {

		return (StdScheduler) SpringServletAccess.getApplicationContext(request.getSession().getServletContext())
				.getBean(SCHEDULER);
	}

	private boolean mayProceed(HttpServletRequest request) {

		StudyUserRoleBean currentRole = (StudyUserRoleBean) request.getSession().getAttribute("userRole");
		Role r = currentRole.getRole();

		return r != null && !Role.INVALID.equals(r);
	}

	/**
	 * Common exception handler.
	 *
	 * @param ex
	 *            an object, which represents the exception was thrown.
	 * @return URL to redirect the user to.
	 */
	@ExceptionHandler(Exception.class)
	public String handleException(Exception ex) {

		ex.printStackTrace();
		return "redirect:/MainMenu";
	}
}
