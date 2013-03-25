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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.control.admin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
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
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;
import org.springframework.scheduling.quartz.JobDetailBean;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class UpdateJobExportServlet extends SecureController {

	private static String SCHEDULER = "schedulerFactoryBean";

	private StdScheduler scheduler;
	private JobDataMap dataMap;
	public static final String PERIOD = "periodToRun";
	public static final String FORMAT_ID = "formatId";
	public static final String DATASET_ID = "dsId";
	public static final String DATE_START_JOB = "job";
	public static final String EMAIL = "contactEmail";
	public static final String JOB_NAME = "jobName";
	public static final String JOB_DESC = "jobDesc";
	public static final String USER_ID = "user_id";
	public static final String STUDY_NAME = "study_name";
	public static final String TRIGGER_GROUP_JOB = "XsltTriggersExportJobs";

	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin() || ub.isTechAdmin()) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO
		// above copied from create dataset servlet, needs to be changed to
		// allow only admin-level users

	}

	private StdScheduler getScheduler() {
		scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(
				context).getBean(SCHEDULER);
		return scheduler;
	}

	private void setUpServlet(Trigger trigger) {
		FormProcessor fp2 = new FormProcessor(request);

		DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
		Collection dsList = dsdao.findAllOrderByStudyIdAndName();
		// TODO will have to dress this up to allow for sites then datasets
		request.setAttribute("datasets", dsList);
		request.setAttribute(CreateJobExportServlet.JOB_NAME, trigger.getName());
		request.setAttribute(CreateJobExportServlet.JOB_DESC, trigger.getDescription());

		dataMap = trigger.getJobDataMap();
		String contactEmail = dataMap.getString(ExampleSpringJob.EMAIL);
		System.out.println("found email: " + contactEmail);
		int dsId = dataMap.getInt(XsltTriggerService.DATASET_ID);
		String period = dataMap.getString(XsltTriggerService.PERIOD);
		int exportFormatId = dataMap.getInt(XsltTriggerService.EXPORT_FORMAT_ID);

		request.setAttribute(FORMAT_ID, exportFormatId);
		request.setAttribute(ExampleSpringJob.EMAIL, contactEmail);
		// how to find out the period of time???
		request.setAttribute(PERIOD, period);
		request.setAttribute(DATASET_ID, dsId);
		request.setAttribute("extractProperties", CoreResources.getExtractProperties());

		Date jobDate = trigger.getNextFireTime();
		HashMap presetValues = new HashMap();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(jobDate);
		presetValues.put(CreateJobExportServlet.DATE_START_JOB + "Hour", calendar.get(Calendar.HOUR_OF_DAY));
		presetValues.put(CreateJobExportServlet.DATE_START_JOB + "Minute", calendar.get(Calendar.MINUTE));
		presetValues.put(CreateJobExportServlet.DATE_START_JOB + "Date", local_df.format(jobDate));
		fp2.setPresetValues(presetValues);
		setPresetValues(fp2.getPresetValues());
		// TODO pick out the datasets and the date
	}

	@Override
	protected void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		String action = fp.getString("action");
		String triggerName = fp.getString("tname");
		scheduler = getScheduler();
		System.out.println("found trigger name " + triggerName);
		ExtractUtils extractUtils = new ExtractUtils();
		Trigger updatingTrigger = scheduler.getTrigger(triggerName.trim(), XsltTriggerService.TRIGGER_GROUP_NAME);
		if (StringUtil.isBlank(action)) {
			setUpServlet(updatingTrigger);
			forwardPage(Page.UPDATE_JOB_EXPORT);
		} else if ("confirmall".equalsIgnoreCase(action)) {
			// change and update trigger here
			// validate first
			// then update or send back
			HashMap errors = validateForm(fp, request,
					scheduler.getTriggerNames(XsltTriggerService.TRIGGER_GROUP_NAME), updatingTrigger.getName());
			if (!errors.isEmpty()) {
				// send back
				addPageMessage("Your modifications caused an error, please see the messages for more information.");
				setUpServlet(updatingTrigger);
				System.out.println("errors : " + errors.toString());
				forwardPage(Page.UPDATE_JOB_EXPORT);
			} else {
				DatasetDAO datasetDao = new DatasetDAO(sm.getDataSource());
				CoreResources cr = new CoreResources();
				UserAccountBean userBean = (UserAccountBean) request.getSession().getAttribute("userBean");

				int datasetId = fp.getInt(DATASET_ID);
				String period = fp.getString(PERIOD);
				String email = fp.getString(EMAIL);
				String jobName = fp.getString(JOB_NAME);
				String jobDesc = fp.getString(JOB_DESC);
				Date startDateTime = fp.getDateTime(DATE_START_JOB);
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
				String generalFileDir = SQLInitServlet.getField("filePath");
				generalFileDir = generalFileDir + "datasets" + File.separator + dsBean.getId() + File.separator
						+ sdfDir.format(new java.util.Date());
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
				SimpleTrigger trigger = null;

				trigger = xsltService.generateXsltTrigger(xsltPath,
						generalFileDir, // xml_file_path
						endFilePath + File.separator, exportFileName, dsBean.getId(), epBean, userBean, request
								.getLocale().getLanguage(), cnt, SQLInitServlet.getField("filePath") + "xslt",
						TRIGGER_GROUP_JOB);

				// Updating the original trigger with user given inputs
				trigger.setRepeatCount(64000);
				trigger.setRepeatInterval(XsltTriggerService.getIntervalTime(period));
				trigger.setDescription(jobDesc);
				// set just the start date
				trigger.setStartTime(startDateTime);
				trigger.setName(jobName);// + datasetId);
				trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
				trigger.getJobDataMap().put(XsltTriggerService.EMAIL, email);
				trigger.getJobDataMap().put(XsltTriggerService.PERIOD, period);
				trigger.getJobDataMap().put(XsltTriggerService.EXPORT_FORMAT, epBean.getFiledescription());
				trigger.getJobDataMap().put(XsltTriggerService.EXPORT_FORMAT_ID, exportFormatId);
				trigger.getJobDataMap().put(XsltTriggerService.JOB_NAME, jobName);

				JobDetailBean jobDetailBean = new JobDetailBean();
				jobDetailBean.setGroup(XsltTriggerService.TRIGGER_GROUP_NAME);
				jobDetailBean.setName(trigger.getName());
				jobDetailBean.setJobClass(org.akaza.openclinica.job.XsltStatefulJob.class);
				jobDetailBean.setJobDataMap(trigger.getJobDataMap());
				jobDetailBean.setDurability(true); // need durability?
				jobDetailBean.setVolatility(false);
				try {
					scheduler.deleteJob(triggerName, XsltTriggerService.TRIGGER_GROUP_NAME);
					addPageMessage("Your job has been successfully modified.");
					forwardPage(Page.VIEW_JOB_SERVLET);
				} catch (SchedulerException se) {
					se.printStackTrace();
					// set a message here with the exception message
					setUpServlet(trigger);
					addPageMessage("There was an unspecified error with your creation, please contact an administrator.");
					forwardPage(Page.UPDATE_JOB_EXPORT);
				}
			}
		}
	}

	public HashMap validateForm(FormProcessor fp, HttpServletRequest request, String[] triggerNames, String properName) {
		Validator v = new Validator(request);
		v.addValidation(JOB_NAME, Validator.NO_BLANKS);
		// need to be unique too
		v.addValidation(JOB_DESC, Validator.NO_BLANKS);
		v.addValidation(EMAIL, Validator.IS_A_EMAIL);
		v.addValidation(PERIOD, Validator.NO_BLANKS);
		v.addValidation(DATE_START_JOB + "Date", Validator.IS_A_DATE);
		// TODO job names will have to be unique, tbh

		int formatId = fp.getInt(FORMAT_ID);
		Date jobDate = fp.getDateTime(DATE_START_JOB);
		HashMap errors = v.validate();
		if (formatId == 0) {
			Validator.addError(errors, FORMAT_ID, "Please pick at least one.");
		}
		for (String triggerName : triggerNames) {
			if (triggerName.equals(fp.getString(JOB_NAME)) && (!triggerName.equals(properName))) {
				Validator.addError(errors, JOB_NAME, "A job with that name already exists.  Please pick another name.");
			}
		}
		if (jobDate.before(new Date())) {
			Validator.addError(errors, DATE_START_JOB + "Date", "This date needs to be later than the present time.");
		}
		return errors;
	}
}
