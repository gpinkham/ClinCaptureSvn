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

package org.akaza.openclinica.service.extract;

import java.math.BigInteger;
import java.util.Date;

import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.quartz.JobDataMap;
import org.quartz.impl.triggers.SimpleTriggerImpl;

public class XsltTriggerService {

	public XsltTriggerService() {
	}

	public static final String DATASET_ID = "dsId";
	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";
	public static final String XSL_FILE_PATH = "xslFilePath";
	public static final String XML_FILE_PATH = "xmlFilePath";
	public static final String POST_FILE_PATH = "postFilePath";
	public static final String POST_FILE_NAME = "postFileName";
	public static final String EXTRACT_PROPERTY = "extractProperty";
	public static final String LOCALE = "locale";
	public static final String STUDY_ID = "studyId";
	public static final String ZIPPED = "zipped";
	public static final String DELETE_OLD = "deleteOld";
	public static final String SUCCESS_MESSAGE = "SUCCESS_MESSAGE";
	public static final String FAILURE_MESSAGE = "FAILURE_MESSAGE";
	public static final String XSLT_PATH = "XSLT_PATH";
	public static final String EP_BEAN = "epBean";
	public static final String TRIGGER_GROUP_NAME = "XsltTriggersExportJobs";
	public static final String PERIOD = "periodToRun";
	public static final String EXPORT_FORMAT = "exportFormat";
	public static final String EXPORT_FORMAT_ID = "exportFormatId";
	public static final String JOB_NAME = "jobName";

	// POST PROCESSING VARIABLES
	public static final String POST_PROC_DELETE_OLD = "postProcDeleteOld";
	public static final String POST_PROC_ZIP = "postProcZip";
	public static final String POST_PROC_LOCATION = "postProcLocation";
	public static final String POST_PROC_EXPORT_NAME = "postProcExportName";
	public static final String COUNT = "count";

	public SimpleTriggerImpl generateXsltTrigger(String xslFile, String xmlFile, String endFilePath, String endFile,
			int datasetId, ExtractPropertyBean epBean, UserAccountBean userAccountBean, String locale, int cnt,
			String xsltPath, String triggerGroupName) {
		Date startDateTime = new Date(System.currentTimeMillis());
		String jobName = datasetId + "_" + epBean.getExportFileName()[0];

		SimpleTriggerImpl trigger = new SimpleTriggerImpl();

		trigger.setStartTime(startDateTime);
		trigger.setName(jobName);// + datasetId);
		trigger.setGroup(triggerGroupName);// + datasetId);
		trigger.setJobName(jobName);
		trigger.setJobGroup(triggerGroupName);
		trigger.setRepeatCount(0);
		trigger.setRepeatInterval(1);
		trigger.setMisfireInstruction(SimpleTriggerImpl.MISFIRE_INSTRUCTION_FIRE_NOW);
		// set job data map
		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put(XSL_FILE_PATH, xslFile);
		jobDataMap.put(XML_FILE_PATH, endFilePath);
		jobDataMap.put(POST_FILE_PATH, endFilePath);
		jobDataMap.put(POST_FILE_NAME, endFile);

		jobDataMap.put(EXTRACT_PROPERTY, epBean.getId());
		jobDataMap.put(USER_ID, userAccountBean.getId());
		jobDataMap.put(STUDY_ID, userAccountBean.getActiveStudyId());
		jobDataMap.put(LOCALE, locale);
		jobDataMap.put(DATASET_ID, datasetId);
		jobDataMap.put(EMAIL, userAccountBean.getEmail());
		jobDataMap.put(ZIPPED, epBean.getZipFormat());
		jobDataMap.put(DELETE_OLD, epBean.getDeleteOld());
		jobDataMap.put(SUCCESS_MESSAGE, epBean.getSuccessMessage());
		jobDataMap.put(FAILURE_MESSAGE, epBean.getFailureMessage());

		jobDataMap.put(POST_PROC_DELETE_OLD, epBean.getPostProcDeleteOld());
		jobDataMap.put(POST_PROC_ZIP, epBean.getPostProcZip());
		jobDataMap.put(POST_PROC_LOCATION, epBean.getPostProcLocation());
		jobDataMap.put(POST_PROC_EXPORT_NAME, epBean.getPostProcExportName());
		jobDataMap.put(COUNT, cnt);
		jobDataMap.put(XSLT_PATH, xsltPath);
		// jobDataMap.put(DIRECTORY, directory);
		// jobDataMap.put(ExampleSpringJob.LOCALE, locale);
		jobDataMap.put(EP_BEAN, epBean);

		trigger.setJobDataMap(jobDataMap);
		// trigger.setVolatility(false);

		return trigger;
	}

	public static long getIntervalTime(String period) {
		BigInteger interval = new BigInteger("0");
		if ("monthly".equalsIgnoreCase(period)) {
			interval = new BigInteger("2419200000"); // how many
			// milliseconds in
			// a month? should
			// be 24192000000
		} else if ("weekly".equalsIgnoreCase(period)) {
			interval = new BigInteger("604800000"); // how many
			// milliseconds in
			// a week? should
			// be 6048000000
		} else { // daily
			interval = new BigInteger("86400000");// how many
			// milliseconds in a
			// day?
		}
		return interval.longValue();
	}
}
