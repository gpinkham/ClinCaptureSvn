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
import java.util.*;

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
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.SimpleTriggerImpl;

/**
 * 
 * @author thickerson
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class CreateJobExportServlet extends SecureController {

	public static final String PERIOD = "periodToRun";
	public static final String FORMAT_ID = "formatId";
	public static final String DATASET_ID = "dsId";
	public static final String DATE_START_JOB = "job";
	public static final String EMAIL = "contactEmail";
	public static final String JOB_NAME = "jobName";
	public static final String JOB_DESC = "jobDesc";
	public static final String USER_ID = "user_id";
	public static final String STUDY_NAME = "study_name";

	private static String SCHEDULER = "schedulerFactoryBean";
	// faking out DRY - should we create a super class, Job Servlet, which
	// captures the scheduler?
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
		// above copied from create dataset servlet, needs to be changed to
		// allow only admin-level users

	}

	private StdScheduler getScheduler() {
		scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(
				context).getBean(SCHEDULER);
		return scheduler;
	}

	private void setUpServlet() {

		// TODO find all the form items and re-populate them if necessary
		FormProcessor fp2 = new FormProcessor(request);
		DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
		Collection dsList = dsdao.findAllOrderByStudyIdAndName();
		// TODO will have to dress this up to allow for sites then datasets
		request.setAttribute("datasets", dsList);
		request.setAttribute(JOB_NAME, fp2.getString(JOB_NAME));
		request.setAttribute(JOB_DESC, fp2.getString(JOB_DESC));
		request.setAttribute("extractProperties", CoreResources.getExtractProperties());
		request.setAttribute(EMAIL, fp2.getString(EMAIL));
		request.setAttribute(FORMAT_ID, fp2.getInt(FORMAT_ID));
		request.setAttribute(PERIOD, fp2.getString(PERIOD));
		request.setAttribute(DATASET_ID, fp2.getInt(DATASET_ID));
		Date jobDate = (fp2.getDateTime(DATE_START_JOB));
		HashMap presetValues = new HashMap();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(jobDate);
		presetValues.put(DATE_START_JOB + "Hour", calendar.get(Calendar.HOUR_OF_DAY));
		presetValues.put(DATE_START_JOB + "Minute", calendar.get(Calendar.MINUTE));
		presetValues.put(DATE_START_JOB + "Date", local_df.format(jobDate));
		fp2.setPresetValues(presetValues);
		setPresetValues(fp2.getPresetValues());
		request.setAttribute(DATE_START_JOB, fp2.getDateTime(DATE_START_JOB));
		// EMAIL, TAB, CDISC, SPSS, PERIOD, DATE_START_JOB
		// TODO pick out the datasets and the date
	}

	@Override
	protected void processRequest() throws Exception {
		// TODO multi stage servlet which will create export jobs
		// will accept, create, and return the ViewJob servlet
		FormProcessor fp = new FormProcessor(request);
		scheduler = getScheduler();
		String action = fp.getString("action");
		ExtractUtils extractUtils = new ExtractUtils();
		if (StringUtil.isBlank(action)) {
			// set up list of data sets
			// select by ... active study
			setUpServlet();

			forwardPage(Page.CREATE_JOB_EXPORT);
		} else if ("confirmall".equalsIgnoreCase(action)) {
			// collect form information
			HashMap errors = validateForm(fp, request,
					scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("DEFAULT")), "");

			if (!errors.isEmpty()) {
				// set errors to request
				request.setAttribute("formMessages", errors);
				logger.info("has validation errors in the first section");
				logger.info("errors found: " + errors.toString());
				setUpServlet();

				forwardPage(Page.CREATE_JOB_EXPORT);
			} else {
				logger.info("found no validation errors, continuing");

				DatasetDAO datasetDao = new DatasetDAO(sm.getDataSource());

				UserAccountBean userBean = (UserAccountBean) request.getSession().getAttribute("userBean");
				CoreResources cr = new CoreResources();
				int datasetId = fp.getInt(DATASET_ID);
				String period = fp.getString(PERIOD);
				String email = fp.getString(EMAIL);
				String jobName = fp.getString(JOB_NAME);
				String jobDesc = fp.getString(JOB_DESC);
				Date startDateTime = fp.getDateTime(DATE_START_JOB);

				Integer exportFormatId = fp.getInt(FORMAT_ID);

				ExtractPropertyBean epBean = cr.findExtractPropertyBeanById(exportFormatId, "" + datasetId);
				DatasetBean dsBean = (DatasetBean) datasetDao.findByPK(new Integer(datasetId).intValue());

				// set the job in motion
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
				// JN: The following logic is for comma separated variables, to
				// avoid the second file be treated as a
				// old file and deleted.
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

				// need to set the dataset path here, tbh
				// next, can already run jobs, translations, and then add a
				// message to be notified later
				// JN all the properties need to have the variables...
				String xsltPath = SQLInitServlet.getField("filePath") + "xslt" + File.separator + files[cnt];
				String endFilePath = epBean.getFileLocation();
				endFilePath = extractUtils.getEndFilePath(endFilePath, dsBean, sdfDir, datasetFilePath);
				// exportFileName = resolveVars(exportFileName,dsBean,sdfDir);
				if (epBean.getPostProcExportName() != null) {
					// String preProcExportPathName =
					// getEndFilePath(epBean.getPostProcExportName(),dsBean,sdfDir);
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
				SimpleTriggerImpl trigger = null;

				trigger = xsltService.generateXsltTrigger(xsltPath,
						generalFileDir, // xml_file_path
						endFilePath + File.separator, exportFileName, dsBean.getId(), epBean, userBean, request
								.getLocale().getLanguage(), cnt, SQLInitServlet.getField("filePath") + "xslt",
						XsltTriggerService.TRIGGER_GROUP_NAME);

                trigger.setName(jobName);
                trigger.setJobName(jobName);
				// Updating the original trigger with user given inputs
				trigger.setRepeatCount(64000);
				trigger.setRepeatInterval(XsltTriggerService.getIntervalTime(period));
				trigger.setDescription(jobDesc);
				// set just the start date

				trigger.setStartTime(startDateTime);
				trigger.setMisfireInstruction(SimpleTriggerImpl.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
				trigger.getJobDataMap().put(XsltTriggerService.EMAIL, email);
				trigger.getJobDataMap().put(XsltTriggerService.PERIOD, period);
				trigger.getJobDataMap().put(XsltTriggerService.EXPORT_FORMAT, epBean.getFiledescription());
				trigger.getJobDataMap().put(XsltTriggerService.EXPORT_FORMAT_ID, exportFormatId);
				trigger.getJobDataMap().put(XsltTriggerService.JOB_NAME, jobName);
				trigger.getJobDataMap().put("job_type", "exportJob");

				JobDetailImpl jobDetailBean = new JobDetailImpl();
				jobDetailBean.setGroup(XsltTriggerService.TRIGGER_GROUP_NAME);
				jobDetailBean.setName(trigger.getName());
				jobDetailBean.setJobClass(org.akaza.openclinica.job.XsltStatefulJob.class);
				jobDetailBean.setJobDataMap(trigger.getJobDataMap());
				jobDetailBean.setDurability(true); // need durability?
				// jobDetailBean.setVolatility(false);

				// set to the scheduler
				try {
					Date dateStart = scheduler.scheduleJob(jobDetailBean, trigger);
					logger.info("== found job date: " + dateStart.toString());
					// set a success message here
				} catch (SchedulerException se) {
					se.printStackTrace();
					setUpServlet();
					addPageMessage("Error creating Job.");
					forwardPage(Page.VIEW_JOB_SERVLET);
					return;
				}
				setUpServlet();
				addPageMessage("You have successfully created a new job: " + jobName
						+ " which is now set to run at the time you specified.");
				forwardPage(Page.VIEW_JOB_SERVLET);
			}
		} else {
			forwardPage(Page.ADMIN_SYSTEM);
			// forward to form
			// should we even get to this part?
		}
	}

	public HashMap validateForm(FormProcessor fp, HttpServletRequest request, Set<TriggerKey> triggerKeys,
			String properName) {
		Validator v = new Validator(request);
		v.addValidation(JOB_NAME, Validator.NO_BLANKS);
		// need to be unique too
		v.addValidation(JOB_DESC, Validator.NO_BLANKS);
		v.addValidation(EMAIL, Validator.IS_A_EMAIL);
		v.addValidation(PERIOD, Validator.NO_BLANKS);
		v.addValidation(DATE_START_JOB + "Date", Validator.IS_A_DATE);

		int formatId = fp.getInt(FORMAT_ID);
		Date jobDate = fp.getDateTime(DATE_START_JOB);
		HashMap errors = v.validate();
		if (formatId == 0) {
			// throw an error here, at least one should work
			// errors.put(TAB, "Error Message - Pick one of the below");
			Validator.addError(errors, FORMAT_ID, "Please pick at least one.");
		}
		for (TriggerKey triggerKey : triggerKeys) {
			if (triggerKey.getName().equals(fp.getString(JOB_NAME)) && (!triggerKey.getName().equals(properName))) {
				Validator.addError(errors, JOB_NAME, "A job with that name already exists.  Please pick another name.");
			}
		}
		if (jobDate.before(new Date())) {
			Validator.addError(errors, DATE_START_JOB + "Date", "This date needs to be later than the present time.");
		}
		String jobDesc = fp.getString(JOB_DESC);
		if ((null != jobDesc) && (!jobDesc.equals(""))) {
			if (jobDesc.length() > 250) {
				Validator.addError(errors, JOB_DESC, "A job description cannot be more than 250 characters.");
			}
		}
		return errors;
	}
}
