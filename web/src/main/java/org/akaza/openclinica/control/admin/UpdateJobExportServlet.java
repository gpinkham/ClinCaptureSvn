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
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clinovo.util.DateUtil;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.service.extract.ExtractUtils;
import org.akaza.openclinica.service.extract.XsltTriggerService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.job.ExampleSpringJob;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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

@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class UpdateJobExportServlet extends SpringServlet {

	public static final String PERIOD = "periodToRun";
	public static final String FORMAT_ID = "formatId";
	public static final String DATASET_ID = "dsId";
	public static final String DATE_START_JOB = "job";
	public static final String EMAIL = "contactEmail";
	public static final String JOB_NAME = "jobName";
	public static final String JOB_DESC = "jobDesc";

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
				getResException().getString("not_allowed_access_extract_data_servlet"), "1");// TODO
		// above copied from create dataset servlet, needs to be changed to
		// allow only admin-level users

	}

	private void setUpServlet(Trigger trigger, HttpServletRequest request) {
		FormProcessor fp2 = new FormProcessor(request);

		DatasetDAO dsdao = getDatasetDAO();
		Collection dsList = dsdao.findAllOrderByStudyIdAndName();

		request.setAttribute("datasets", dsList);
		request.setAttribute(CreateJobExportServlet.JOB_NAME, trigger.getKey().getName());
		request.setAttribute(CreateJobExportServlet.JOB_DESC, trigger.getDescription());

		JobDataMap dataMap = trigger.getJobDataMap();
		String contactEmail = dataMap.getString(ExampleSpringJob.EMAIL);
		logger.trace("found email: " + contactEmail);
		int dsId = dataMap.getInt(XsltTriggerService.DATASET_ID);
		String period = dataMap.getString(XsltTriggerService.PERIOD);
		int exportFormatId = dataMap.getInt(XsltTriggerService.EXPORT_FORMAT_ID);

		request.setAttribute(FORMAT_ID, exportFormatId);
		request.setAttribute(ExampleSpringJob.EMAIL, contactEmail);
		// how to find out the period of time???
		request.setAttribute(PERIOD, period);
		request.setAttribute(DATASET_ID, dsId);
		request.setAttribute("extractProperties", CoreResources.getExtractProperties());

		DateTime serverJobDate = trigger.getNextFireTime() == null
				? new DateTime(trigger.getStartTime()) : new DateTime(trigger.getNextFireTime());
		DateTimeZone userTimeZone = DateTimeZone.forID(getUserAccountBean().getUserTimeZoneId());
		DateTime localJobDate = serverJobDate.withZone(userTimeZone);
		HashMap presetValues = new HashMap();
		presetValues.put(DATE_START_JOB + "Hour", localJobDate.getHourOfDay());
		presetValues.put(DATE_START_JOB + "Minute", localJobDate.getMinuteOfHour());
		presetValues.put(DATE_START_JOB + "Date", DateUtil.printDate(serverJobDate.toDate(), userTimeZone.getID(),
				DateUtil.DatePattern.DATE, getLocale()));
		fp2.setPresetValues(presetValues);
		setPresetValues(fp2.getPresetValues(), request);

	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		String action = fp.getString("action");
		String triggerName = fp.getString("tname");
		StdScheduler scheduler = getStdScheduler();
		logger.trace("found trigger name " + triggerName);
		ExtractUtils extractUtils = new ExtractUtils();
		Trigger updatingTrigger = scheduler.getTrigger(TriggerKey.triggerKey(triggerName.trim(),
				XsltTriggerService.TRIGGER_GROUP_NAME));
		if (StringUtil.isBlank(action)) {
			setUpServlet(updatingTrigger, request);
			forwardPage(Page.UPDATE_JOB_EXPORT, request, response);
		} else if ("confirmall".equalsIgnoreCase(action)) {
			// change and update trigger here
			// validate first
			// then update or send back
			HashMap errors = validateForm(fp, request,
					scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(XsltTriggerService.TRIGGER_GROUP_NAME)),
					updatingTrigger.getKey().getName());
			if (!errors.isEmpty()) {
				// send back
				request.setAttribute("formMessages", errors);
				logger.info("errors found: " + errors.toString());
				setUpServlet(updatingTrigger, request);
				forwardPage(Page.UPDATE_JOB_EXPORT, request, response);
			} else {
				DatasetDAO datasetDao = getDatasetDAO();
				CoreResources cr = new CoreResources();
				UserAccountBean userBean = (UserAccountBean) request.getSession().getAttribute("userBean");

				int datasetId = fp.getInt(DATASET_ID);
				String period = fp.getString(PERIOD);
				String email = fp.getString(EMAIL);
				String jobName = fp.getString(JOB_NAME);
				String jobDesc = fp.getString(JOB_DESC);
				Date startDateTime = fp.getDateTimeInput(DATE_START_JOB);
				Integer exportFormatId = fp.getInt(FORMAT_ID);

				ExtractPropertyBean epBean = cr.findExtractPropertyBeanById(exportFormatId, "" + datasetId);
				DatasetBean dsBean = (DatasetBean) datasetDao.findByPK(new Integer(datasetId).intValue());
				String[] files = epBean.getFileName();
				String exportFileName;
				int cnt = 0;
				dsBean.setName(dsBean.getName().replaceAll(" ", "_"));
				String[] exportFiles = epBean.getExportFileName();
				String pattern = "yyyy" + File.separator + "MM" + File.separator + "dd" + File.separator + "HHmmssSSS"
						+ File.separator;
				SimpleDateFormat sdfDir = new SimpleDateFormat(pattern);
				int i = 0;
				String[] temp = new String[exportFiles.length];
				String datasetFilePath = SQLInitServlet.getField("filePath") + "datasets";
				while (i < exportFiles.length) {
					temp[i] = extractUtils.resolveVars(exportFiles[i], dsBean, sdfDir, datasetFilePath);
					i++;
				}
				epBean.setDoNotDelFiles(temp);
				epBean.setExportFileName(temp);

				XsltTriggerService xsltService = new XsltTriggerService();
				exportFileName = epBean.getExportFileName()[cnt];

				String xsltPath = SQLInitServlet.getField("filePath") + "xslt" + File.separator + files[cnt];
				String endFilePath = epBean.getFileLocation();
				endFilePath = extractUtils.getEndFilePath(endFilePath, dsBean, sdfDir, datasetFilePath);
				if (epBean.getPostProcExportName() != null) {
					String preProcExportPathName = extractUtils.resolveVars(epBean.getPostProcExportName(), dsBean,
							sdfDir, datasetFilePath);
					epBean.setPostProcExportName(preProcExportPathName);
				}
				if (epBean.getPostProcLocation() != null) {
					String prePocLoc = extractUtils.getEndFilePath(epBean.getPostProcLocation(), dsBean, sdfDir,
							datasetFilePath);
					epBean.setPostProcLocation(prePocLoc);
				}
				extractUtils.setAllProps(epBean, dsBean, sdfDir, datasetFilePath);
				SimpleTriggerImpl newTrigger;

				newTrigger = xsltService.generateXsltTrigger(xsltPath, getParentStudy(), endFilePath + File.separator,
						exportFileName, dsBean.getId(), epBean, userBean, LocaleResolver.getLocale(request).toString(),
						cnt, SQLInitServlet.getField("filePath") + "xslt", XsltTriggerService.TRIGGER_GROUP_NAME);

				newTrigger.setName(jobName);
				newTrigger.setJobName(jobName);
				// Updating the original trigger with user given inputs
				newTrigger.setRepeatCount(64000);
				newTrigger.setRepeatInterval(XsltTriggerService.getIntervalTime(period));
				newTrigger.setDescription(jobDesc);
				// set just the start date
				newTrigger.setStartTime(startDateTime);
				newTrigger
						.setMisfireInstruction(SimpleTriggerImpl.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
				newTrigger.getJobDataMap().put(XsltTriggerService.EMAIL, email);
				newTrigger.getJobDataMap().put(XsltTriggerService.PERIOD, period);
				newTrigger.getJobDataMap().put(XsltTriggerService.EXPORT_FORMAT, epBean.getFiledescription());
				newTrigger.getJobDataMap().put(XsltTriggerService.EXPORT_FORMAT_ID, exportFormatId);
				newTrigger.getJobDataMap().put(XsltTriggerService.JOB_NAME, jobName);
				newTrigger.getJobDataMap().put("job_type", "exportJob");

				JobDetailImpl jobDetailBean = new JobDetailImpl();
				jobDetailBean.setGroup(XsltTriggerService.TRIGGER_GROUP_NAME);
				jobDetailBean.setName(newTrigger.getName());
				jobDetailBean.setJobClass(org.akaza.openclinica.job.XsltStatefulJob.class);
				jobDetailBean.setJobDataMap(newTrigger.getJobDataMap());
				jobDetailBean.setDurability(true); // need durability?
				// jobDetailBean.setVolatility(false);
				try {
					scheduler.deleteJob(updatingTrigger.getJobKey());
					Date dataStart = scheduler.scheduleJob(jobDetailBean, newTrigger);
					logger.info("Job started with a start date of " + dataStart.toString());
					addPageMessage(getResWord().getString("job_modified_successfully"), request);
					forwardPage(Page.VIEW_JOB_SERVLET, request, response);
				} catch (SchedulerException se) {
					se.printStackTrace();
					// set a message here with the exception message
					setUpServlet(newTrigger, request);
					addPageMessage(getResException().getString("unspecified_error_with_job_creation"), request);
					forwardPage(Page.UPDATE_JOB_EXPORT, request, response);
				}
			}
		}
	}

	public HashMap validateForm(FormProcessor fp, HttpServletRequest request, Set<TriggerKey> triggerKeys,
			String properName) {

		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		v.addValidation(DATASET_ID, Validator.NO_BLANKS_SET);
		v.addValidation(JOB_NAME, Validator.NO_BLANKS);
		v.addValidation(JOB_DESC, Validator.NO_BLANKS);
		v.addValidation(EMAIL, Validator.IS_A_EMAIL);
		v.addValidation(PERIOD, Validator.NO_BLANKS);
		v.addValidation(DATE_START_JOB + "Date", Validator.IS_A_DATE);
		HashMap errors = v.validate();

		Matcher matcher = Pattern.compile("[^\\w_\\d ]").matcher(fp.getString(JOB_NAME));
		boolean isContainSpecialSymbol = matcher.find();
		if (isContainSpecialSymbol) {
			Validator.addError(errors, JOB_NAME, getResException().getString("dataset_should_not_contain_any_special"));
		}
		int formatId = fp.getInt(FORMAT_ID);
		if (formatId == 0) {
			Validator.addError(errors, FORMAT_ID, getResPage().getString("please_pick_at_least_one"));
		}
		for (TriggerKey triggerKey : triggerKeys) {
			if (triggerKey.getName().equals(fp.getString(JOB_NAME)) && (!triggerKey.getName().equals(properName))) {
				Validator.addError(errors, JOB_NAME,
						getResException().getString("a_job_with_that_name_already_exist_please_pick"));
			}
		}
		try {
			Date jobDate = fp.getDateTimeInput(DATE_START_JOB);
			if (jobDate.before(new Date())) {
				Validator.addError(errors, DATE_START_JOB + "Date", getResException().getString("this_date_needs_to_be_later"));
			}
		} catch (IllegalArgumentException ex) {
			//
		}
		return errors;
	}
}
