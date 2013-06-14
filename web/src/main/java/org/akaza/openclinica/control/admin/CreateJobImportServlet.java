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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.SimpleTriggerImpl;

/**
 * Create Job Import Servlet, by Tom Hickerson, 2009
 * 
 * @author thickerson Purpose: to create jobs in the 'importTrigger' group, which will be meant to run the
 *         ImportStatefulJob.
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class CreateJobImportServlet extends SecureController {

	private static String SCHEDULER = "schedulerFactoryBean";
	private static String IMPORT_TRIGGER = "importTrigger";

	private StdScheduler scheduler;

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

	/*
	 * Find all the form items and re-populate as necessary
	 */
	private void setUpServlet() throws Exception {
		String directory = SQLInitServlet.getField(ImportSpringJob.FILE_PATH) + ImportSpringJob.DIR_PATH
				+ File.separator;
		// find all the form items and re-populate them if necessary
		FormProcessor fp2 = new FormProcessor(request);

		StudyDAO sdao = new StudyDAO(sm.getDataSource());

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

		request.setAttribute(ImportSpringJob.FILE_PATH, directory);

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

	@Override
	protected void processRequest() throws Exception {
		// TODO multi stage servlet to generate import jobs
		// validate form, create job and return to view jobs servlet
		FormProcessor fp = new FormProcessor(request);
		TriggerService triggerService = new TriggerService();
		scheduler = getScheduler();
		String action = fp.getString("action");
		if (StringUtil.isBlank(action)) {
			// set up list of data sets
			// select by ... active study
			setUpServlet();

			forwardPage(Page.CREATE_JOB_IMPORT);
		} else if ("confirmall".equalsIgnoreCase(action)) {
			// collect form information
			HashMap errors = triggerService.validateImportJobForm(fp, request,
					scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(IMPORT_TRIGGER)));

			Date startTime = getJobStartTime(errors, fp);

			if (!errors.isEmpty()) {
				// set errors to request
				request.setAttribute("formMessages", errors);
				System.out.println("has validation errors in the first section");
				System.out.println("errors found: " + errors.toString());
				setUpServlet();

				forwardPage(Page.CREATE_JOB_IMPORT);
			} else {
				logger.info("found no validation errors, continuing");
				int studyId = fp.getInt(ImportSpringJob.STUDY_ID);
				StudyDAO studyDAO = new StudyDAO(sm.getDataSource());
				StudyBean studyBean = (StudyBean) studyDAO.findByPK(studyId);
				SimpleTriggerImpl trigger = triggerService.generateImportTrigger(fp, sm.getUserBean(), studyBean,
						request.getLocale().getLanguage(), startTime);

				JobDetailImpl jobDetailBean = new JobDetailImpl();
				jobDetailBean.setGroup(IMPORT_TRIGGER);
				jobDetailBean.setName(trigger.getName());
				jobDetailBean.setJobClass(org.akaza.openclinica.web.job.ImportStatefulJob.class);
				jobDetailBean.setJobDataMap(trigger.getJobDataMap());
				jobDetailBean.setDurability(true); // need durability?
				// jobDetailBean.setVolatility(false);

				// set to the scheduler
				try {
					Date dateStart = scheduler.scheduleJob(jobDetailBean, trigger);
					System.out.println("== found job date: " + dateStart.toString());
					// set a success message here
					addPageMessage("You have successfully created a new job: " + trigger.getName()
							+ " which is now set to run at the time you specified.");
					forwardPage(Page.VIEW_IMPORT_JOB_SERVLET);
				} catch (SchedulerException se) {
					se.printStackTrace();
					// set a message here with the exception message
					setUpServlet();
					addPageMessage("There was an unspecified error with your creation, please contact an administrator.");
					forwardPage(Page.CREATE_JOB_IMPORT);
				}
			}
		} else {
			forwardPage(Page.ADMIN_SYSTEM);
		}

	}
}
