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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
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
import org.springframework.stereotype.Component;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.ValidatorHelper;

@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@Component
public class UpdateJobImportServlet extends Controller {

	private static final String TRIGGER_IMPORT_GROUP = "importTrigger";

	private static final String IMPORT_DIR = SQLInitServlet.getField(ImportSpringJob.FILE_PATH)
			+ ImportSpringJob.DIR_PATH + File.separator;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (ub.isSysAdmin() || ub.isTechAdmin()) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO

	}

	private void setUpServlet(Trigger trigger, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FormProcessor fp2 = new FormProcessor(request);

		StudyDAO sdao = getStudyDAO();

		request.setAttribute(ImportSpringJob.FIRST_FILE_PATH, IMPORT_DIR);

		if (trigger != null) {
			request.setAttribute(ImportSpringJob.TNAME, trigger.getKey().getName());
			request.setAttribute(ImportSpringJob.JOB_NAME, trigger.getJobKey().getName());
			request.setAttribute(ImportSpringJob.JOB_DESC, trigger.getDescription());

			JobDataMap dataMap = trigger.getJobDataMap();
			String contactEmail = dataMap.getString(ImportSpringJob.EMAIL);
			logger.trace("found email: " + contactEmail);
			int hours = dataMap.getInt(ImportSpringJob.HOURS);
			int minutes = dataMap.getInt(ImportSpringJob.MINUTES);
			String directory = dataMap.getString(ImportSpringJob.DIRECTORY);
			String studyName = dataMap.getString(ImportSpringJob.STUDY_NAME);
			String studyOID = dataMap.getString(ImportSpringJob.STUDY_OID);

			request.setAttribute(ImportSpringJob.FILE_PATH_DIR, dataMap.getString(ImportSpringJob.DIRECTORY));
			request.setAttribute(ImportSpringJob.EMAIL, contactEmail);
			request.setAttribute(ImportSpringJob.STUDY_NAME, studyName);
			request.setAttribute(ImportSpringJob.FILE_PATH, directory);
			request.setAttribute(ImportSpringJob.HOURS, Integer.toString(hours));
			request.setAttribute(ImportSpringJob.MINUTES, Integer.toString(minutes));

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
			request.setAttribute(ImportSpringJob.HOURS, Integer.toString(fp2.getInt(ImportSpringJob.HOURS)));
			request.setAttribute(ImportSpringJob.MINUTES, Integer.toString(fp2.getInt(ImportSpringJob.MINUTES)));
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
				respage.getString("a_user_cannot_be_created_no_study_as_active"), Page.ADMIN_SYSTEM, request, response);

	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		FormProcessor fp = new FormProcessor(request);
		TriggerService triggerService = new TriggerService();
		String action = fp.getString("action");
		String triggerName = fp.getString("tname");
		StdScheduler scheduler = getStdScheduler();
		logger.trace("found trigger name " + triggerName);
		Trigger updatingTrigger = scheduler.getTrigger(TriggerKey.triggerKey(triggerName, TRIGGER_IMPORT_GROUP));
		if (StringUtil.isBlank(action)) {
			setUpServlet(updatingTrigger, request, response);
			forwardPage(Page.UPDATE_JOB_IMPORT, request, response);
		} else if ("confirmall".equalsIgnoreCase(action)) {
			HashMap errors = triggerService.validateImportJobForm(fp, new ValidatorHelper(request,
					getConfigurationDao()), scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("DEFAULT")),
					updatingTrigger.getKey().getName());

			Date startTime = getJobStartTime(errors, fp);

			if (!errors.isEmpty()) {
				// send back
				request.setAttribute("formMessages", errors);
				addPageMessage(resexception.getString("job_modification_error"), request);
				setUpServlet(null, request, response);
				forwardPage(Page.UPDATE_JOB_IMPORT, request, response);
			} else {
				StudyDAO studyDAO = getStudyDAO();
				int studyId = fp.getInt(ImportSpringJob.STUDY_ID);
				StudyBean study = (StudyBean) studyDAO.findByPK(studyId);
				SimpleTriggerImpl newTrigger = triggerService.generateImportTrigger(fp, getUserAccountBean(request),
						study, startTime, LocaleResolver.getLocale(request).toString());
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
					addPageMessage(resword.getString("job_modified_successfully"), request);
					forwardPage(Page.VIEW_IMPORT_JOB_SERVLET, request, response);
				} catch (SchedulerException se) {
					se.printStackTrace();
					// set a message here with the exception message
					setUpServlet(newTrigger, request, response);
					addPageMessage(resexception.getString("unspecified_error_with_job_creation"), request);
					forwardPage(Page.UPDATE_JOB_IMPORT, request, response);
				}
			}
		}
	}
}
