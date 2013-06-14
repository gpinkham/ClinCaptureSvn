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

package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.job.ImportSpringJob;
import org.akaza.openclinica.web.job.TriggerService;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class UpdateJobImportServlet extends SecureController {

	private static String SCHEDULER = "schedulerFactoryBean";
	private static String TRIGGER_IMPORT_GROUP = "importTrigger";

	private StdScheduler scheduler;
	private JobDataMap dataMap;
	private static final String IMPORT_DIR = SQLInitServlet.getField(ImportSpringJob.FILE_PATH)
			+ ImportSpringJob.DIR_PATH + File.separator;

	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin() || ub.isTechAdmin()) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO

	}

	private StdScheduler getScheduler() {
		scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(
				context).getBean(SCHEDULER);
		return scheduler;
	}

	private void setUpServlet(Trigger trigger) throws Exception {
		FormProcessor fp2 = new FormProcessor(request);

		StudyDAO sdao = new StudyDAO(sm.getDataSource());

		request.setAttribute(ImportSpringJob.FIRST_FILE_PATH, IMPORT_DIR);

		if (trigger != null) {
			request.setAttribute(ImportSpringJob.TNAME, trigger.getKey().getName());
			request.setAttribute(ImportSpringJob.JOB_NAME, trigger.getJobKey().getName());
			request.setAttribute(ImportSpringJob.JOB_DESC, trigger.getDescription());

			dataMap = trigger.getJobDataMap();
			String contactEmail = dataMap.getString(ImportSpringJob.EMAIL);
			System.out.println("found email: " + contactEmail);
			int hours = dataMap.getInt(ImportSpringJob.HOURS);
			int minutes = dataMap.getInt(ImportSpringJob.MINUTES);
			String directory = dataMap.getString(ImportSpringJob.DIRECTORY);
			String studyName = dataMap.getString(ImportSpringJob.STUDY_NAME);
			String studyOID = dataMap.getString(ImportSpringJob.STUDY_OID);

			request.setAttribute(ImportSpringJob.FILE_PATH_DIR, dataMap.getString(ImportSpringJob.DIRECTORY));
			request.setAttribute(ImportSpringJob.EMAIL, contactEmail);
			request.setAttribute(ImportSpringJob.STUDY_NAME, studyName);
			request.setAttribute(ImportSpringJob.FILE_PATH, directory);
			request.setAttribute(ImportSpringJob.HOURS, new Integer(hours).toString());
			request.setAttribute(ImportSpringJob.MINUTES, new Integer(minutes).toString());

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(trigger.getStartTime());
			request.setAttribute(ImportSpringJob.JOB_HOUR, "" + calendar.get(Calendar.HOUR_OF_DAY));
			request.setAttribute(ImportSpringJob.JOB_MINUTE, "" + calendar.get(Calendar.MINUTE));

			StudyBean studyBean;
			if (studyOID != null && !studyOID.trim().isEmpty()) {
				studyBean = sdao.findByOid(studyOID);
			} else {
				studyBean = (StudyBean) sdao.findByName(studyName);
			}
			request.setAttribute(ImportSpringJob.STUDY_ID, studyBean != null ? studyBean.getId() : "");
		} else {
			request.setAttribute(ImportSpringJob.TNAME, fp2.getString(ImportSpringJob.TNAME));
			request.setAttribute(ImportSpringJob.STUDY_ID, fp2.getInt(ImportSpringJob.STUDY_ID));
			request.setAttribute(ImportSpringJob.JOB_NAME, fp2.getString(ImportSpringJob.JOB_NAME));
			request.setAttribute(ImportSpringJob.JOB_DESC, fp2.getString(ImportSpringJob.JOB_DESC));
			request.setAttribute(ImportSpringJob.JOB_HOUR, fp2.getString(ImportSpringJob.JOB_HOUR));
			request.setAttribute(ImportSpringJob.JOB_MINUTE, fp2.getString(ImportSpringJob.JOB_MINUTE));
			request.setAttribute(ImportSpringJob.HOURS, new Integer(fp2.getInt(ImportSpringJob.HOURS)).toString());
			request.setAttribute(ImportSpringJob.MINUTES, new Integer(fp2.getInt(ImportSpringJob.MINUTES)).toString());
			request.setAttribute(ImportSpringJob.EMAIL, fp2.getString(ImportSpringJob.EMAIL));
			request.setAttribute(ImportSpringJob.FILE_PATH_DIR, fp2.getString(ImportSpringJob.FILE_PATH_DIR));
		}

		ArrayList<StudyBean> all = (ArrayList<StudyBean>) sdao.findAll();
		ArrayList<StudyBean> finalList = new ArrayList<StudyBean>();
		for (StudyBean sb : all) {
			if (!(sb.getParentStudyId() > 0)) {
				finalList.add(sb);
				finalList.addAll(sdao.findAllByParent(sb.getId()));
			}
		}
		addEntityList(ImportSpringJob.STUDIES, finalList,
				respage.getString("a_user_cannot_be_created_no_study_as_active"), Page.ADMIN_SYSTEM);

	}

	@Override
	protected void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		TriggerService triggerService = new TriggerService();
		String action = fp.getString("action");
		String triggerName = fp.getString("tname");
		scheduler = getScheduler();
		System.out.println("found trigger name " + triggerName);
		Trigger updatingTrigger = scheduler.getTrigger(TriggerKey.triggerKey(triggerName, TRIGGER_IMPORT_GROUP));
		if (StringUtil.isBlank(action)) {
			setUpServlet(updatingTrigger);
			forwardPage(Page.UPDATE_JOB_IMPORT);
		} else if ("confirmall".equalsIgnoreCase(action)) {
			HashMap errors = triggerService.validateImportJobForm(fp, request,
					scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("DEFAULT")), updatingTrigger.getKey().getName());

			Date startTime = getJobStartTime(errors, fp);

			if (!errors.isEmpty()) {
				// send back
				request.setAttribute("formMessages", errors);
				addPageMessage("Your modifications caused an error, please see the messages for more information.");
				setUpServlet(null);
				forwardPage(Page.UPDATE_JOB_IMPORT);
			} else {
				StudyDAO studyDAO = new StudyDAO(sm.getDataSource());
				int studyId = fp.getInt(ImportSpringJob.STUDY_ID);
				StudyBean study = (StudyBean) studyDAO.findByPK(studyId);
                SimpleTriggerImpl newTrigger = triggerService.generateImportTrigger(fp, sm.getUserBean(), study, startTime, request
						.getLocale().getLanguage());
				JobDetailImpl jobDetailBean = new JobDetailImpl();
				jobDetailBean.setGroup(TRIGGER_IMPORT_GROUP);
				jobDetailBean.setName(newTrigger.getKey().getName());
				jobDetailBean.setJobClass(org.akaza.openclinica.web.job.ImportStatefulJob.class);
				jobDetailBean.setJobDataMap(newTrigger.getJobDataMap());
				jobDetailBean.setDurability(true); // need durability?
				// jobDetailBean.setVolatility(false);

				try {
					scheduler.deleteJob(updatingTrigger.getJobKey());
					Date dateStart = scheduler.scheduleJob(jobDetailBean, newTrigger);
					logger.info("Job scheduled with a start date of " + dateStart.toString());
					addPageMessage("Your job has been successfully modified.");
					forwardPage(Page.VIEW_IMPORT_JOB_SERVLET);
				} catch (SchedulerException se) {
					se.printStackTrace();
					// set a message here with the exception message
					setUpServlet(newTrigger);
					addPageMessage("There was an unspecified error with your creation, please contact an administrator.");
					forwardPage(Page.UPDATE_JOB_IMPORT);
				}
			}
		}

	}
}
