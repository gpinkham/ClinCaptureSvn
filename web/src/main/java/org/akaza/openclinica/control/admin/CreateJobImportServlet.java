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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SpringServlet;
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
import org.springframework.stereotype.Component;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.ValidatorHelper;

/**
 * Create Job Import Servlet, by Tom Hickerson, 2009.
 * 
 * @author thickerson Purpose: to create jobs in the 'importTrigger' group, which will be meant to run the
 *         ImportStatefulJob.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class CreateJobImportServlet extends SpringServlet {

	private static final String IMPORT_TRIGGER = "importTrigger";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (ub.isSysAdmin() || ub.isTechAdmin()) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				getResException().getString("not_allowed_access_extract_data_servlet"), "1");

	}

	/*
	 * Find all the form items and re-populate as necessary
	 */
	private void setUpServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String directory = SQLInitServlet.getField(ImportSpringJob.FILE_PATH) + ImportSpringJob.DIR_PATH
				+ File.separator;
		// find all the form items and re-populate them if necessary
		FormProcessor fp2 = new FormProcessor(request);

		StudyDAO sdao = getStudyDAO();

		ArrayList<StudyBean> all = (ArrayList<StudyBean>) sdao.findAll();
		ArrayList<StudyBean> finalList = new ArrayList<StudyBean>();
		for (StudyBean sb : all) {
			if (!(sb.getParentStudyId() > 0)) {
				finalList.add(sb);
				finalList.addAll(sdao.findAllByParent(sb.getId()));
			}
		}
		addEntityList(ImportSpringJob.STUDIES, finalList,
				getResPage().getString("a_user_cannot_be_created_no_study_as_active"), Page.ADMIN_SYSTEM, request, response);

		request.setAttribute(ImportSpringJob.FILE_PATH, directory);

		request.setAttribute(ImportSpringJob.STUDY_ID, fp2.getInt(ImportSpringJob.STUDY_ID));
		request.setAttribute(ImportSpringJob.JOB_NAME, fp2.getString(ImportSpringJob.JOB_NAME));
		request.setAttribute(ImportSpringJob.JOB_DESC, fp2.getString(ImportSpringJob.JOB_DESC));
		request.setAttribute(ImportSpringJob.JOB_HOUR, fp2.getString(ImportSpringJob.JOB_HOUR));
		request.setAttribute(ImportSpringJob.JOB_MINUTE, fp2.getString(ImportSpringJob.JOB_MINUTE));
		request.setAttribute(ImportSpringJob.HOURS, Integer.toString(fp2.getInt(ImportSpringJob.HOURS)));
		request.setAttribute(ImportSpringJob.MINUTES, Integer.toString(fp2.getInt(ImportSpringJob.MINUTES)));
		request.setAttribute(ImportSpringJob.EMAIL, fp2.getString(ImportSpringJob.EMAIL));
		request.setAttribute(ImportSpringJob.FILE_PATH_DIR, fp2.getString(ImportSpringJob.FILE_PATH_DIR));
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		// TODO multi stage servlet to generate import jobs
		// validate form, create job and return to view jobs servlet
		FormProcessor fp = new FormProcessor(request);
		TriggerService triggerService = new TriggerService();
		StdScheduler scheduler = getStdScheduler();
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		String action = fp.getString("action");
		if (StringUtil.isBlank(action)) {
			// set up list of data sets
			// select by ... active study
			setUpServlet(request, response);

			forwardPage(Page.CREATE_JOB_IMPORT, request, response);
		} else if ("confirmall".equalsIgnoreCase(action)) {
			// collect form information
			HashMap errors = triggerService.validateImportJobForm(fp, new ValidatorHelper(request,
					getConfigurationDao()), scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(IMPORT_TRIGGER)));

			Date startTime = getJobStartTime(errors, fp);

			if (!errors.isEmpty()) {
				// set errors to request
				request.setAttribute("formMessages", errors);
				System.out.println("has validation errors in the first section");
				System.out.println("errors found: " + errors.toString());
				setUpServlet(request, response);

				forwardPage(Page.CREATE_JOB_IMPORT, request, response);
			} else {
				logger.info("found no validation errors, continuing");
				int studyId = fp.getInt(ImportSpringJob.STUDY_ID);
				StudyDAO studyDAO = getStudyDAO();
				StudyBean studyBean = (StudyBean) studyDAO.findByPK(studyId);
				SimpleTriggerImpl trigger = triggerService.generateImportTrigger(fp, ub, studyBean, LocaleResolver
						.getLocale(request).toString(), startTime);

				JobDetailImpl jobDetailBean = new JobDetailImpl();
				jobDetailBean.setGroup(IMPORT_TRIGGER);
				jobDetailBean.setName(trigger.getName());
				jobDetailBean.setJobClass(org.akaza.openclinica.web.job.ImportStatefulJob.class);
				jobDetailBean.setJobDataMap(trigger.getJobDataMap());
				jobDetailBean.setDurability(true); // need durability?
				// jobDetailBean.setVolatility(false);

				// set to the scheduler
				try {
					Date dateStart = getStdScheduler().scheduleJob(jobDetailBean, trigger);
					System.out.println("== found job date: " + dateStart.toString());
					// set a success message here
					addPageMessage(
							getResPage().getString("you_have_successfully_created_a_new_job") + " " + trigger.getName()
									+ " " + getResPage().getString("which_is_now_set_to_run"), request);

					if ((currentStudy.isSite() && studyBean.getId() != currentStudy.getId())
							|| (studyBean.isSite() && !currentStudy.isSite() && studyBean.getParentStudyId() != currentStudy.getId())
							|| (!studyBean.isSite() && !currentStudy.isSite() && currentStudy.getId() != studyBean.getId())) {
						addPageMessage(getResWord().getString("job_view_on_another_study").replace("{0}", studyBean.getName()), request);
					}
					forwardPage(Page.VIEW_IMPORT_JOB_SERVLET, request, response);
				} catch (SchedulerException se) {
					se.printStackTrace();
					// set a message here with the exception message
					setUpServlet(request, response);
					addPageMessage(getResException().getString("there_was_an_unspecified_error"), request);
					forwardPage(Page.CREATE_JOB_IMPORT, request, response);
				}
			}
		} else {
			forwardPage(Page.ADMIN_SYSTEM, request, response);
		}

	}
}
