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
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.extract.XsltTriggerService;
import org.akaza.openclinica.web.table.scheduledjobs.ScheduledJobTableFactory;
import org.akaza.openclinica.web.table.scheduledjobs.ScheduledJobs;
import org.akaza.openclinica.web.table.sdv.SDVUtil;
import org.apache.commons.dbcp.BasicDataSource;
import org.jmesa.facade.TableFacade;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @author jnyayapathi Controller for listing all the scheduled jobs. Also an interface for canceling the jobs which are
 *         running.
 */
@SuppressWarnings({ "unchecked" })
@Controller("ScheduledJobController")
public class ScheduledJobController {
	public final static String SCHEDULED_TABLE_ATTRIBUTE = "scheduledTableAttribute";
	@Autowired
	@Qualifier("dataSource")
	private BasicDataSource dataSource;
	private String SCHEDULER = "schedulerFactoryBean";

	@Autowired
	@Qualifier("scheduledJobTableFactory")
	private ScheduledJobTableFactory scheduledJobTableFactory;
	public static final String EP_BEAN = "epBean";

	@Autowired
	@Qualifier("sdvUtil")
	private SDVUtil sdvUtil;

	protected final static Logger logger = LoggerFactory
			.getLogger("org.akaza.openclinica.controller.ScheduledJobController");

	public ScheduledJobController() {

	}

	@RequestMapping("/listCurrentScheduledJobs")
	public ModelMap listScheduledJobs(HttpServletRequest request, HttpServletResponse response)
			throws SchedulerException {
		ResourceBundleProvider.updateLocale(request.getLocale());
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
		StdScheduler scheduler = getScheduler(request);
		boolean showMoreLink = false;
		if (request.getParameter("showMoreLink") != null) {
			showMoreLink = Boolean.parseBoolean(request.getParameter("showMoreLink").toString());
		} else {
			showMoreLink = true;
		}
		request.setAttribute("showMoreLink", showMoreLink + "");

		request.setAttribute("imagePathPrefix", "../");

		ArrayList<String> pageMessages = (ArrayList<String>) request.getAttribute("pageMessages");
		if (pageMessages == null) {
			pageMessages = new ArrayList<String>();
		}

		request.setAttribute("pageMessages", pageMessages);

		List<JobExecutionContext> listCurrentJobs = new ArrayList<JobExecutionContext>();
		listCurrentJobs = scheduler.getCurrentlyExecutingJobs();
		Iterator<JobExecutionContext> itCurrentJobs = listCurrentJobs.iterator();
		List<String> currentJobList = new ArrayList<String>();
		while (itCurrentJobs.hasNext()) {
			JobExecutionContext temp = itCurrentJobs.next();
			currentJobList.add(temp.getTrigger().getKey().getName() + temp.getTrigger().getKey().getGroup());
		}

		Iterator<String> it = scheduler.getPausedTriggerGroups().iterator();
		while (it.hasNext()) {
			Set<TriggerKey> legacyTriggers = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(it.next()));
			for (TriggerKey triggerKey : legacyTriggers) {
				Trigger trigger = scheduler.getTrigger(triggerKey);
				logMe("Paused Job Group" + trigger.getJobKey().getGroup());
				logMe("Paused Job Name:" + trigger.getJobKey().getName());
				logMe("Paused Trigger Name:" + trigger.getKey().getName());
				logMe("Paused Trigger Next Fire Time:" + trigger.getNextFireTime());
			}
		}

		// enumerate each job group
		/*
		 * for(String group: scheduler.getJobGroupNames()) { // enumerate each job in group
		 * 
		 * 
		 * for(StdScheduler currentJob:listCurrentJobs) { currentJob.getJobNames(group); }
		 * 
		 * 
		 * for(String jobName:scheduler.getJobNames(group)){
		 * 
		 * System.out.println("Found job identified by: " + jobName);
		 * 
		 * } }
		 */
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
		/*
		 * for(JobExecutionContext jec:listCurrentJobs) { ScheduledJobs jobs = new ScheduledJobs(); StringBuilder
		 * schedulerStatus = new StringBuilder("");
		 * 
		 * schedulerStatus.append( "<center><input style='margin-right: 5px' type='checkbox' "
		 * ).append("class='sdvCheck'") .append(" name='").append("sdvCheck_") .append
		 * ((Integer)jec.getTrigger().getJobDataMap().get("dsId")).append( "' /></center>");
		 * 
		 * jobs.setCheckbox(schedulerStatus.toString()); jobs.setDatasetId((Integer
		 * )jec.getTrigger().getJobDataMap().get("dsId")+""); jobs.setFireTime(jec.getFireTime()+"");
		 * jobs.setScheduledFireTime(jec.getScheduledFireTime()+""); jobsScheduled.add(index, jobs); index++; }
		 */
		for (SimpleTriggerImpl st : simpleTriggers) {

			ScheduledJobs jobs = new ScheduledJobs();

			ExtractPropertyBean epBean = null;
			if (st.getJobDataMap() != null)
				epBean = (ExtractPropertyBean) st.getJobDataMap().get(EP_BEAN);

			if (epBean != null) {
				StringBuilder checkbox = new StringBuilder("");
				checkbox.append("<input style='margin-right: 5px' type='checkbox' ").append("' />");

				StringBuilder actions = new StringBuilder("<table><tr><td>");
				String contextPath = request.getContextPath();
				StringBuilder jsCodeString = new StringBuilder("this.form.method='GET'; this.form.action='")
						.append(contextPath).append("/pages/cancelScheduledJob").append("';")
						.append("this.form.theJobName.value='").append(st.getName()).append("';")
						.append("this.form.theJobGroupName.value='").append(st.getJobGroup()).append("';")
						.append("this.form.theTriggerName.value='").append(st.getName()).append("';")
						.append("this.form.theTriggerGroupName.value='").append(st.getGroup()).append("';")
						.append("this.form.submit();");

				actions.append("<td><input type=\"submit\" class=\"button\" value=\"Cancel Job\" name=\"cancelJob\" ")
						.append("onclick=\"").append(jsCodeString.toString()).append("\" /></td>");
				actions.append("</tr></table>");

				jobs.setCheckbox(checkbox.toString());
				// jobs.setDatasetId((Integer)st.getJobDataMap().get("dsId")+"");
				jobs.setDatasetId(epBean.getDatasetName());
				jobs.setFireTime(st.getStartTime() + "");
				if (st.getNextFireTime() != null)
					jobs.setScheduledFireTime(st.getNextFireTime() + "");
				jobs.setExportFileName(epBean.getExportFileName()[0]);
				jobs.setAction(actions.toString());
				jobs.setJobStatus(currentJobList.contains(st.getJobName() + st.getGroup()) ? "Currently Executing"
						: "Scheduled");

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

	@RequestMapping("/cancelScheduledJob")
	public String cancelScheduledJob(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("theJobName") String theJobName, @RequestParam("theJobGroupName") String theJobGroupName,
			@RequestParam("theTriggerName") String triggerName,
			@RequestParam("theTriggerGroupName") String triggerGroupName,
			@RequestParam("redirection") String redirection, ModelMap model) throws SchedulerException {

		ResourceBundle resPageMessages = ResourceBundleProvider.getPageMessagesBundle(request.getLocale());
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
			// newTrigger.setNextFireTime(nextFireTime );
			newTrigger.setMisfireInstruction(oldTrigger.getMisfireInstruction());
			newTrigger.setJobDataMap(oldTrigger.getJobDataMap());
			// newTrigger.setVolatility(false);
			newTrigger.setRepeatCount(oldTrigger.getRepeatCount());
			newTrigger.setRepeatInterval(oldTrigger.getRepeatInterval());
			newTrigger
					.setMisfireInstruction(SimpleTriggerImpl.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
			newTrigger.setStartTime(startTime);
			newTrigger.setRepeatInterval(oldTrigger.getRepeatInterval());

			scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName));// these are the jobs which
																							// are from
																							// extract data and
																							// are not not required to
																							// be
																							// rescheduled.

			ArrayList<String> pageMessages = new ArrayList<String>();

			if (triggerGroupName.equals(ExtractController.TRIGGER_GROUP_NAME)) {
				scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName), newTrigger);

				messageFormat.applyPattern(resPageMessages.getString("job_has_been_cancelled"));
				Object[] arguments = { theJobName };
				pageMessages.add(messageFormat.format(arguments));
			} else if (triggerGroupName.equals(XsltTriggerService.TRIGGER_GROUP_NAME)) {

				JobDetailImpl jobDetailBean = new JobDetailImpl();
				jobDetailBean.setGroup(XsltTriggerService.TRIGGER_GROUP_NAME);
				jobDetailBean.setName(newTrigger.getName());
				jobDetailBean.setJobClass(org.akaza.openclinica.job.XsltStatefulJob.class);
				jobDetailBean.setJobDataMap(newTrigger.getJobDataMap());
				jobDetailBean.setDurability(true); // need durability?
				// jobDetailBean.setVolatility(false);

				scheduler.deleteJob(jobDetailBean.getKey());
				// scheduler.rescheduleJob(triggerName, triggerGroupName,
				// newTrigger); // These are the jobs which come
				// from export job and need to be rescheduled.
				scheduler.scheduleJob(jobDetailBean, newTrigger);

				messageFormat.applyPattern(resPageMessages.getString("job_has_been_rescheduled"));
				Object[] arguments = { theJobName };
				pageMessages.add(messageFormat.format(arguments));

			}

			request.setAttribute("pageMessages", pageMessages);

			logMe("jobDetails>" + scheduler.getJobDetail(JobKey.jobKey(theJobName, theJobGroupName)));
		}
		sdvUtil.forwardRequestFromController(request, response, "/pages/" + redirection);
		return null;
	}

	private void logMe(String msg) {
		// System.out.println(msg);
		logger.info(msg);
	}

	private StdScheduler getScheduler(HttpServletRequest request) {
		StdScheduler scheduler = (StdScheduler) SpringServletAccess.getApplicationContext(
				request.getSession().getServletContext()).getBean(SCHEDULER);
		return scheduler;
	}

	private boolean mayProceed(HttpServletRequest request) {
		StudyUserRoleBean currentRole = (StudyUserRoleBean) request.getSession().getAttribute("userRole");
		Role r = currentRole.getRole();

		return r != null && !Role.INVALID.equals(r);
	}

	@ExceptionHandler(Exception.class)
	public String handleException(Exception ex) {
		ex.printStackTrace();
		return "redirect:/MainMenu";
	}
}
