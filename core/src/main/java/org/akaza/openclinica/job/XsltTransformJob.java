/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
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

package org.akaza.openclinica.job;

import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.ExportFormatBean;
import org.akaza.openclinica.bean.extract.ExtractBean;
import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.ProcessingFunction;
import org.akaza.openclinica.bean.service.ProcessingResultType;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.OpenClinicaMailSender;
import org.akaza.openclinica.core.util.XMLFileFilter;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.extract.GenerateExtractFileService;
import org.akaza.openclinica.service.extract.XsltTriggerService;
import org.akaza.openclinica.util.ExcelConverter;
import org.apache.commons.io.FileUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.FileCopyUtils;

import javax.sql.DataSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Xalan Transform Job, an XSLT transform job using the Xalan classes.
 * 
 * @author thickerson
 * 
 */
@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class XsltTransformJob extends QuartzJobBean {

	public static final String DATASET_ID = "dsId";
	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";
	public static final String XSL_FILE_PATH = "xslFilePath";
	public static final String XML_FILE_PATH = "xmlFilePath";
	public static final String POST_FILE_PATH = "postFilePath";
	public static final String POST_FILE_NAME = "postFileName";
	public static final String LOCALE = "locale";
	public static final String STUDY_ID = "studyId";
	public static final String ZIPPED = "zipped";
	public static final String DELETE_OLD = "deleteOld";
	public static final String XSLT_PATH = "XSLT_PATH";
	public static final String SAS_JOB_DIR = "sasJobDir";
	public static final String SAS_EMAIL_BUFFER = "sasEmailBuffer";
	public static final String SAS_ODM_OUTPUT_PATH = "sasOdmOutputPath";
	public static final String SAS_DIR = "sas.dir";
	public static final int SAS_DATASET_JOB_ID = 10;
	public static final int MILLISECONDS_IN_MINUTE = 60000;
	public static final String SAS_DELETE_OLD_OBJECT = "sasDeleteOldObject";
	public static final int MINUTES_IN_HOUR = 60;
	public static final String SAS_TIMER = "sas.timer";
	public static final String SAS_EXTRACT_JOB_NEXT_FIRE_TIME = "sasExtractJobNextFireTime";
	public static final String JOB_TYPE = "job_type";
	public static final String EXPORT_JOB = "exportJob";
	public static final String SAS_EXTRACT_REPEAT_INTERVAL = "sasExtractRepeatInterval";
	public static final String SAS_EXTRACT_REPEAT_COUNT = "sasExtractRepeatCount";
	public static final int ONE_THOUSAND = 1000;
	public static final int FOUR = 4;
	public static final int THREE = 3;
	public static final int TWO = 2;
	public static final int MILLISECONDS_IN_DAY = 86400000;
	private OpenClinicaMailSender mailSender;
	private DataSource dataSource;
	private AuditEventDAO auditEventDAO;
	public static final String EP_BEAN = "epBean";

	// POST PROCESSING VARIABLES
	public static final String POST_PROC_DELETE_OLD = "postProcDeleteOld";
	public static final String POST_PROC_ZIP = "postProcZip";
	public static final String POST_PROC_LOCATION = "postProcLocation";
	public static final String POST_PROC_EXPORT_NAME = "postProcExportName";
	private static final long KILOBYTE = 1024;

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	/**
	 * Class that holds info about files.
	 */

	public static class DeleteOldObject implements Serializable {
		private String endFile;
		private Boolean deleteOld;
		private String[] dontDelFiles = new String[] {};
		private List<File> markForDelete = new LinkedList<File>();
		private List<File> intermediateFiles = new LinkedList<File>();
	}

	private String readFileContent(File file, String separator) throws Exception {
		String content = "";
		String sCurrentLine;
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		while ((sCurrentLine = br.readLine()) != null) {
			content = content.concat(sCurrentLine).concat(separator);
		}
		fr.close();
		br.close();
		return content;
	}

	private boolean reportAboutException(JobExecutionContext context, String exceptionKey) {
		Date exceptionDate = (Date) context.getJobDetail().getJobDataMap().get(exceptionKey);
		boolean reportAboutException = exceptionDate == null
				|| Math.abs(exceptionDate.getTime() - System.currentTimeMillis()) > MILLISECONDS_IN_DAY;
		if (reportAboutException) {
			context.getJobDetail().getJobDataMap().put(exceptionKey, new Date());
		}
		return reportAboutException;
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		boolean reportAboutException = true;
		Locale locale = new Locale("en-US");
		ResourceBundleProvider.updateLocale(locale);
		ResourceBundle pageMessages = ResourceBundleProvider.getPageMessagesBundle();
		List<File> markForDelete = new LinkedList<File>();
		Boolean zipped;
		Boolean deleteOld = true;
		Boolean exceptions = false;
		String sasErrors = "";
		String sasWarnings = "";
		boolean sasExtractJob = false;
		String sasExceptionMessage = "";
		boolean unscheduleSASJob = false;
		boolean sasSideHasErrors = false;
		boolean sasSideHasWarnings = false;
		JobDataMap dataMap = context.getMergedJobDataMap();
		String localeStr = dataMap.getString(LOCALE);
		String[] doNotDeleteUntilExtract = new String[FOUR];
		int cnt = dataMap.getInt("count");
		DatasetBean datasetBean = null;
		if (localeStr != null) {
			locale = new Locale(localeStr);
			ResourceBundleProvider.updateLocale(locale);
			pageMessages = ResourceBundleProvider.getPageMessagesBundle();
		}
		// get the file information from the job
		String alertEmail = dataMap.getString(EMAIL);
		java.io.InputStream in = null;
		FileOutputStream endFileStream = null;
		UserAccountBean userBean = null;
		try {
			ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext()
					.get("applicationContext");
			MessageSource messageSource = (MessageSource) appContext.getBean("messageSource");
			mailSender = (OpenClinicaMailSender) appContext.getBean("openClinicaMailSender");
			dataSource = (DataSource) appContext.getBean("dataSource");
			auditEventDAO = new AuditEventDAO(dataSource);
			DatasetDAO dsdao = new DatasetDAO(dataSource);
			RuleSetRuleDao ruleSetRuleDao = (RuleSetRuleDao) appContext.getBean("ruleSetRuleDao");
			CoreResources coreResources = (CoreResources) appContext.getBean("coreResources");
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ResourceBundleProvider.getFormatBundle()
					.getString("date_time_format_string"));

			// init all fields from the data map
			int userAccountId = dataMap.getInt(USER_ID);
			int studyId = dataMap.getInt(STUDY_ID);
			String outputPath = dataMap.getString(POST_FILE_PATH);
			// get all user info, generate xml
			// logger.trace("found output path: " + outputPath);
			logger.debug("found output path: " + outputPath);
			String generalFileDir = dataMap.getString(XML_FILE_PATH);

			int dsId = dataMap.getInt(DATASET_ID);

			// JN: Change from earlier versions, cannot get static reference as
			// static references don't work. Reason being for example there
			// could be
			// datasetId as a variable which is different for each dataset and
			// that needs to be loaded dynamically
			ExtractPropertyBean epBean = (ExtractPropertyBean) dataMap.get(EP_BEAN);
			boolean sasDatasetJob = epBean.getId() == SAS_DATASET_JOB_ID
					&& context.getTrigger() instanceof SimpleTriggerImpl;

			File doNotDelDir = new File(generalFileDir);
			if (doNotDelDir.isDirectory()) {
				doNotDeleteUntilExtract = doNotDelDir.list();
			}

			zipped = epBean.getZipFormat();

			deleteOld = epBean.getDeleteOld();
			long sysTimeBegin = System.currentTimeMillis();
			UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
			userBean = (UserAccountBean) userAccountDao.findByPK(userAccountId);
			GenerateExtractFileService generateFileService = new GenerateExtractFileService(dataSource, userBean,
					coreResources, ruleSetRuleDao);
			StudyDAO studyDao = new StudyDAO(dataSource);
			StudyBean currentStudy = (StudyBean) studyDao.findByPK(studyId);
			StudyBean parentStudy = (StudyBean) studyDao.findByPK(currentStudy.getParentStudyId());
			String successMsg = epBean.getSuccessMessage();
			String failureMsg = epBean.getFailureMessage();
			final long start = System.currentTimeMillis();

			String subject = "";
			StringBuffer emailBuffer = new StringBuffer("");
			String jobName = dataMap.getString(XsltTriggerService.JOB_NAME);

			datasetBean = (DatasetBean) dsdao.findByPK(dsId);
			ExtractBean eb = generateFileService.generateExtractBean(datasetBean, currentStudy, parentStudy);

			String sasDir = CoreResources.getField(SAS_DIR);
			File sasDirFile = new File(sasDir);
			String sasJobDir = (String) dataMap.get(SAS_JOB_DIR);
			if (sasDatasetJob) {
				String jobType = (String) dataMap.get(JOB_TYPE);
				sasExtractJob = jobType != null && jobType.equals(EXPORT_JOB);
				if (sasDir == null || sasDir.trim().isEmpty()) {
					reportAboutException = reportAboutException(context, "sasDirException");
					throw new Exception(messageSource.getMessage("sasDataset.exception.sasExtractFolder", null, locale));
				} else if (!sasDirFile.exists()) {
					reportAboutException = reportAboutException(context, "latestSasDirException");
					throw new Exception(messageSource.getMessage("sasDataset.exception.sasFolder",
							new Object[] { sasDir }, locale));
				}
			}
			if (sasDatasetJob && sasJobDir != null) {
				int sasTimer = (Integer) dataMap.get(SAS_TIMER);
				File sasJobDirFile = new File(sasJobDir);
				Date nextFireTime = context.getTrigger().getNextFireTime();
				if (((SimpleTriggerImpl) context.getTrigger()).getTimesTriggered() == sasTimer * MINUTES_IN_HOUR + 1
						|| nextFireTime == null) {
					unscheduleSASJob = true;
					throw new Exception(messageSource.getMessage("sasDataset.exception.failed", new Object[] {
							jobName != null ? jobName : datasetBean.getName(), sasTimer, sasTimer > 1 ? "s" : "" },
							locale));
				}
				emailBuffer = (StringBuffer) dataMap.get(SAS_EMAIL_BUFFER);
				String sasOdmOutputPath = (String) dataMap.get(SAS_ODM_OUTPUT_PATH);
				String nextFireTimeStr = simpleDateFormat.format(nextFireTime);
				if (!sasJobDirFile.exists()) {
					reportAboutException = reportAboutException(context, "latestSasJobDirException");
					throw new Exception(messageSource.getMessage("sasDataset.exception.folder", new Object[] {
							sasJobDir, nextFireTimeStr }, locale));
				}

				sasErrors = sasErrors.concat("\n\n").concat(messageSource.getMessage("sas.errors", null, locale))
						.concat("\n\n");
				File errorsFile = new File(sasJobDirFile.getAbsolutePath() + File.separator + "errors.txt");
				if (errorsFile.exists()) {
					sasSideHasErrors = true;
					unscheduleSASJob = true;
					sasErrors = sasErrors.concat(readFileContent(errorsFile, "\n")).concat("\n");
					if (!sasExtractJob) {
						sasExceptionMessage = messageSource.getMessage("sas.failed.checkEmail", null, locale);
					}
					throw new Exception(messageSource.getMessage("sas.failed", null, locale));
				}

				if (!new File(sasJobDirFile.getAbsolutePath() + File.separator + "ready.txt").exists()) {
					logger.info("Job: " + (jobName != null ? jobName : datasetBean.getName())
							+ ". The next fire time is: " + nextFireTimeStr);
					return;
				}

				String[] myFiles = sasJobDirFile.list(new FilenameFilter() {
					public boolean accept(File directory, String fileName) {
						return fileName.toLowerCase().startsWith("sas_");
					}
				});
				if (myFiles.length != 1) {
					unscheduleSASJob = true;
					throw new Exception(messageSource.getMessage("sasDataset.exception.zip",
							new Object[] { nextFireTimeStr }, locale));
				}

				sasWarnings = sasWarnings.concat("<br/>")
						.concat(messageSource.getMessage("sas.warnings", null, locale)).concat("<br/><br/>");
				File warningsFile = new File(sasJobDirFile.getAbsolutePath() + File.separator + "warnings.txt");
				if (warningsFile.exists()) {
					sasSideHasWarnings = true;
					sasWarnings = sasWarnings.concat(readFileContent(warningsFile, "<br/>")).concat("<br/>");
				}

				File sasDirArchivedFile = new File(sasJobDirFile.getAbsolutePath() + File.separator + myFiles[0]);
				File sasOdmDirArchivedFile = new File(sasOdmOutputPath + File.separator + sasDirArchivedFile.getName());
				FileCopyUtils.copy(sasDirArchivedFile, sasOdmDirArchivedFile);

				double done = setFormat(((double) (System.currentTimeMillis() - start)) / ONE_THOUSAND);
				logger.trace("--> job completed in " + done + " ms");

				ArchivedDatasetFileBean fbFinal = generateFileRecord(sasOdmDirArchivedFile.getName(), outputPath,
						datasetBean, done, sasOdmDirArchivedFile.length(), ExportFormatBean.TXTFILE, userAccountId);

				if (jobName != null) {
					subject = "Job Ran: " + jobName;
				} else {
					subject = "Job Ran: " + datasetBean.getName();
				}

				if (sasSideHasWarnings) {
					successMsg = messageSource.getMessage("sas.success.warnings.email", null, locale);
				}

				if (successMsg != null && successMsg.contains("$linkURL")) {
					successMsg = successMsg.replace("$linkURL", "<a href=\"" + CoreResources.getSystemURL()
							+ "AccessFile?fileId=" + fbFinal.getId() + "\">" + CoreResources.getSystemURL()
							+ "AccessFile?fileId=" + fbFinal.getId() + " </a>");
					emailBuffer.append("<p>").append(successMsg).append("</p>");
					if (sasSideHasWarnings) {
						emailBuffer.append(sasWarnings);
					}
				}

				if (sasSideHasWarnings && !sasExtractJob) {
					context.getJobDetail()
							.getJobDataMap()
							.put("SUCCESS_MESSAGE",
									messageSource.getMessage("sas.dataset.success.warnings", null, locale).replaceAll(
											"\\$datasetId", "" + datasetBean.getId()));
				}

				unscheduleSASJob = true;
				context.getJobDetail().getJobDataMap().remove("failMessage");

				XsltTransformJob.DeleteOldObject deleteOldObject = (XsltTransformJob.DeleteOldObject) dataMap
						.get(SAS_DELETE_OLD_OBJECT);

				if (deleteOldObject.deleteOld) {
					deleteIntermFiles(deleteOldObject.intermediateFiles, deleteOldObject.endFile,
							deleteOldObject.dontDelFiles);
					deleteIntermFiles(deleteOldObject.markForDelete, deleteOldObject.endFile,
							deleteOldObject.dontDelFiles);
				}
			} else {

				// generate file directory for file service
				datasetBean.setName(datasetBean.getName().replaceAll(" ", "_"));
				logger.debug("--> job starting: ");

				HashMap answerMap = generateFileService.createODMFile(epBean.getFormat(), sysTimeBegin, generalFileDir,
						datasetBean, currentStudy, "", eb, currentStudy.getId(), currentStudy.getParentStudyId(), "99",
						(Boolean) dataMap.get(ZIPPED), false, (Boolean) dataMap.get(DELETE_OLD), epBean.getOdmType());

				// won't save a record of the XML to db
				// won't be a zipped file, so that we can submit it for
				// transformation
				// this will have to be toggled by the export data format? no, the
				// export file will have to be zipped/not zipped
				String odmXMLFileName = "";
				for (Object o : answerMap.entrySet()) {
					Map.Entry entry = (Map.Entry) o;
					Object key = entry.getKey();
					Object value = entry.getValue();
					odmXMLFileName = (String) key;
					// JN: Since there is a logic to
					// delete all the intermittent
					// files, this file could be a
					// zip
					// file.
					logger.debug("found " + value + " and " + odmXMLFileName);
				}

				// create dirs

				File output = new File(outputPath);
				if (!output.isDirectory()) {
					output.mkdirs();
				}

				TransformerFactory tFactory = TransformerFactory.newInstance();

				// Use the TransformerFactory to instantiate a Transformer that will
				// work with
				// the stylesheet you specify. This method call also processes the
				// stylesheet
				// into a compiled Templates object.

				int numXLS = epBean.getFileName().length;
				int fileCntr = 0;

				String xmlFilePath = generalFileDir + odmXMLFileName;
				String endFile = null;
				File oldFilesPath = new File(generalFileDir);
				while (fileCntr < numXLS) {
					String xsltPath = dataMap.getString(XSLT_PATH) + File.separator + epBean.getFileName()[fileCntr];
					in = new java.io.FileInputStream(xsltPath);

					Transformer transformer = tFactory.newTransformer(new StreamSource(in));

					// String endFile = outputPath + File.separator +
					// dataMap.getString(POST_FILE_NAME);
					endFile = outputPath + File.separator + epBean.getExportFileName()[fileCntr];

					endFileStream = new FileOutputStream(endFile);
					transformer.transform(new StreamSource(xmlFilePath), new StreamResult(endFileStream));

					in.close();
					endFileStream.close();

					// Convert file if Excel 2013
					if (endFile.endsWith("xlsx")) {
						ExcelConverter.convertTabDelimitedToExcel(endFile, endFile, datasetBean.getName());
					}

					fileCntr++;
				}
				if (oldFilesPath.isDirectory()) {

					markForDelete = Arrays.asList(oldFilesPath.listFiles());

				}
				final double done = setFormat(((double) (System.currentTimeMillis() - start)) / ONE_THOUSAND);
				logger.trace("--> job completed in " + done + " ms");
				// run post processing

				ProcessingFunction function = epBean.getPostProcessing();
				emailBuffer.append("<p>").append(pageMessages.getString("email_header_1")).append(" ")
						.append(pageMessages.getString("email_header_2")).append(" Job Execution ")
						.append(pageMessages.getString("email_header_3")).append("</p>");
				emailBuffer.append("<P>Dataset: ").append(datasetBean.getName()).append("</P>");
				emailBuffer.append("<P>Study: ").append(currentStudy.getName()).append("</P>");
				if (function != null
						&& function.getClass().equals(org.akaza.openclinica.bean.service.SqlProcessingFunction.class)) {
					String dbUrl = ((org.akaza.openclinica.bean.service.SqlProcessingFunction) function)
							.getDatabaseUrl();
					int lastIndex = dbUrl.lastIndexOf("/");
					String schemaName = dbUrl.substring(lastIndex);
					int hostIndex = dbUrl.substring(0, lastIndex).indexOf("//");
					String host = dbUrl.substring(hostIndex, lastIndex);
					emailBuffer
							.append("<P>Database: ")
							.append(((org.akaza.openclinica.bean.service.SqlProcessingFunction) function)
									.getDatabaseType()).append("</P>");
					emailBuffer.append("<P>Schema: ").append(schemaName.replace("/", "")).append("</P>");
					emailBuffer.append("<P>Host: ").append(host.replace("//", "")).append("</P>");

				}
				emailBuffer.append("<p>").append(pageMessages.getString("html_email_body_1"))
						.append(datasetBean.getName()).append(pageMessages.getString("html_email_body_2_2"))
						.append("</p>");
				if (function != null) {
					function.setTransformFileName(outputPath + File.separator + dataMap.getString(POST_FILE_NAME));
					function.setODMXMLFileName(endFile);
					function.setXslFileName(dataMap.getString(XSL_FILE_PATH));
					function.setDeleteOld((Boolean) dataMap.get(POST_PROC_DELETE_OLD));
					function.setZip((Boolean) dataMap.get(POST_PROC_ZIP));
					function.setLocation(dataMap.getString(POST_PROC_LOCATION));
					function.setExportFileName(dataMap.getString(POST_PROC_EXPORT_NAME));
					File[] oldFiles = getOldFiles(outputPath, dataMap.getString(POST_PROC_LOCATION));
					function.setOldFiles(oldFiles);
					File[] intermediateFiles = getInterFiles(dataMap.getString(POST_FILE_PATH));
					ProcessingResultType message = function.run();

					// Delete these files only in case when there is no failure
					if (message.getCode() != 2) {
						deleteOldFiles(intermediateFiles);
					}
					final long done2 = System.currentTimeMillis() - start;
					logger.trace("--> postprocessing completed in " + done2 + " ms, found result type "
							+ message.getCode());
					logger.trace("--> postprocessing completed in " + done2 + " ms, found result type "
							+ message.getCode());

					if (!function.getClass().equals(org.akaza.openclinica.bean.service.SqlProcessingFunction.class)) {
						String archivedFile = dataMap.getString(POST_FILE_NAME) + "." + function.getFileType();
						// JN: if the properties is set to zip the output file,
						// download the zip file
						if (function.isZip()) {
							archivedFile = archivedFile + ".zip";
						}
						// JN: The above 2 lines code is useless, it should be
						// removed..added it only for the sake of custom processing
						// but it will produce erroneous results in case of custom
						// post processing as well.
						if (function.getClass().equals(org.akaza.openclinica.bean.service.PdfProcessingFunction.class)) {
							archivedFile = function.getArchivedFileName();
						}

						ArchivedDatasetFileBean fbFinal = generateFileRecord(archivedFile, outputPath, datasetBean,
								done, new File(outputPath + File.separator + archivedFile).length(),
								ExportFormatBean.PDFFILE, userAccountId);

						if (successMsg.contains("$linkURL")) {
							successMsg = successMsg.replace("$linkURL", "<a href=\"" + CoreResources.getSystemURL()
									+ "/AccessFile?fileId=" + fbFinal.getId() + "\">" + CoreResources.getSystemURL()
									+ "/AccessFile?fileId=" + fbFinal.getId() + " </a>");
						}
						emailBuffer.append("<p>").append(successMsg).append("</p>");
						logMe("System time begining.." + sysTimeBegin);
						logMe("System time end.." + System.currentTimeMillis());
						double sysTimeEnd = setFormat((System.currentTimeMillis() - sysTimeBegin) / ONE_THOUSAND);
						logMe("difference" + sysTimeEnd);

						if (fbFinal != null) {
							fbFinal.setFileSize((int) bytesToKilo(new File(archivedFile).length()));
							fbFinal.setRunTime(sysTimeEnd);
						}

					}
					// otherwise don't do it
					if (message.getCode() == 1) {
						if (jobName != null) {
							subject = "Success: " + jobName;
						} else {
							subject = "Success: " + datasetBean.getName();
						}
					} else if (message.getCode() == TWO) {
						if (jobName != null) {
							subject = "Failure: " + jobName;
						} else {
							subject = "Failure: " + datasetBean.getName();
						}
						if (failureMsg != null && !failureMsg.isEmpty()) {
							emailBuffer.append(failureMsg);
						}
						emailBuffer.append("<P>").append(message.getDescription());
						postErrorMessage(message.getDescription(), context);
					} else if (message.getCode() == THREE) {
						if (jobName != null) {
							subject = "Update: " + jobName;
						} else {
							subject = "Update: " + datasetBean.getName();
						}
					}

				} else {
					// extract ran but no post-processing - we send an email with
					// success and url to link to
					// generate archived dataset file bean here, and use the id to
					// build the URL
					String archivedFilename = dataMap.getString(POST_FILE_NAME);
					// JN: if the properties is set to zip the output file, download
					// the zip file
					if (zipped) {
						archivedFilename = dataMap.getString(POST_FILE_NAME) + ".zip";
					}
					// delete old files now
					List<File> intermediateFiles = generateFileService.getOldFiles();
					String[] dontDelFiles = epBean.getDoNotDelFiles();
					// JN: The following is the code for zipping up the files, in
					// case of more than one xsl being provided.
					if (dontDelFiles.length > 1 && zipped) {
						// unzip(dontDelFiles);
						logMe("count =====" + cnt + "dontDelFiles length==---" + dontDelFiles.length);

						logMe("Entering this?" + cnt + "dontDelFiles" + Arrays.toString(dontDelFiles));
						String path = outputPath + File.separator;
						logMe("path = " + path);
						logMe("zipName?? = " + epBean.getZipName());

						String zipName = (epBean.getZipName() == null || epBean.getZipName().isEmpty()) ? endFile
								+ ".zip" : path + epBean.getZipName() + ".zip";

						archivedFilename = new File(zipName).getName();
						zipAll(path, epBean.getDoNotDelFiles(), zipName);
						dontDelFiles = new String[] { archivedFilename };
						endFile = archivedFilename;
						// }

					} else if (zipped) {
						markForDelete = zipxmls(markForDelete, endFile);
						endFile = endFile + ".zip";

						String[] temp = new String[dontDelFiles.length];
						int i = 0;
						while (i < dontDelFiles.length) {
							temp[i] = dontDelFiles[i] + ".zip";
							i++;
						}
						dontDelFiles = temp;

						// Actually deleting all the xml files which are produced
						// since its zipped
						FilenameFilter xmlFilter = new XMLFileFilter();
						File tempFile = new File(generalFileDir);
						deleteOldFiles(tempFile.listFiles(xmlFilter));
					}

					File odmFile = new File(outputPath + File.separator + archivedFilename);

					// odm was generate - starting the SAS transport file job
					if (sasDatasetJob) {
						File sasJobDirFile = new File(sasDirFile.getAbsolutePath() + File.separator
								+ CoreResources.getField("remoteIp") + "_"
								+ CoreResources.getField("currentWebAppName") + "_" + userBean.getId() + "_"
								+ datasetBean.getName() + "_" + System.currentTimeMillis());
						if (!sasJobDirFile.exists()) {
							sasJobDirFile.mkdirs();
						}

						FileCopyUtils.copy(odmFile,
								new File(sasJobDirFile.getAbsolutePath() + File.separator + odmFile.getName()));
						odmFile.delete();
						new File(sasJobDirFile.getAbsolutePath() + File.separator + "uploaded.txt").createNewFile();

						Integer sasTimer = Integer.parseInt(CoreResources.getField(SAS_TIMER));

						XsltTransformJob.DeleteOldObject deleteOldObject = new XsltTransformJob.DeleteOldObject();
						deleteOldObject.endFile = endFile;
						deleteOldObject.deleteOld = deleteOld;
						deleteOldObject.dontDelFiles = dontDelFiles;
						deleteOldObject.markForDelete = markForDelete;
						deleteOldObject.intermediateFiles = intermediateFiles;

						dataMap.put(SAS_TIMER, sasTimer);
						dataMap.put(SAS_EMAIL_BUFFER, emailBuffer);
						dataMap.put(SAS_ODM_OUTPUT_PATH, outputPath);
						dataMap.put(SAS_DELETE_OLD_OBJECT, deleteOldObject);
						dataMap.put(SAS_JOB_DIR, sasJobDirFile.getAbsolutePath());

						if (sasExtractJob) {
							dataMap.put(SAS_EXTRACT_JOB_NEXT_FIRE_TIME, context.getTrigger().getNextFireTime());
							dataMap.put(SAS_EXTRACT_REPEAT_COUNT,
									((SimpleTriggerImpl) context.getTrigger()).getRepeatCount());
							dataMap.put(SAS_EXTRACT_REPEAT_INTERVAL,
									((SimpleTriggerImpl) context.getTrigger()).getRepeatInterval());
						}
						((SimpleTriggerImpl) context.getTrigger())
								.setMisfireInstruction(SimpleTriggerImpl.MISFIRE_INSTRUCTION_FIRE_NOW);
						((SimpleTriggerImpl) context.getTrigger()).setJobDataMap(dataMap);
						((SimpleTriggerImpl) context.getTrigger()).setRepeatInterval(MILLISECONDS_IN_MINUTE);
						((SimpleTriggerImpl) context.getTrigger()).setRepeatCount(sasTimer * MINUTES_IN_HOUR);
						((SimpleTriggerImpl) context.getTrigger()).setStartTime(new Date(System.currentTimeMillis()
								+ MILLISECONDS_IN_MINUTE));
						Date nextFireTime = context.getScheduler().rescheduleJob(context.getTrigger().getKey(),
								context.getTrigger());
						logger.info("Job: " + (jobName != null ? jobName : datasetBean.getName())
								+ ". The next fire time is: " + nextFireTime);
						return;
					}

					ArchivedDatasetFileBean fbFinal = generateFileRecord(archivedFilename, outputPath, datasetBean,
							done, odmFile.length(), ExportFormatBean.TXTFILE, userAccountId);

					if (jobName != null) {
						subject = "Job Ran: " + jobName;
					} else {
						subject = "Job Ran: " + datasetBean.getName();
					}

					if (successMsg == null || successMsg.isEmpty()) {
						logger.trace("email buffer??" + emailBuffer);

					} else {
						if (successMsg.contains("$linkURL")) {
							successMsg = successMsg.replace("$linkURL", "<a href=\"" + CoreResources.getSystemURL()
									+ "AccessFile?fileId=" + fbFinal.getId() + "\">" + CoreResources.getSystemURL()
									+ "AccessFile?fileId=" + fbFinal.getId() + " </a>");
						}
						emailBuffer.append("<p>").append(successMsg).append("</p>");
					}
					if (deleteOld) {
						deleteIntermFiles(intermediateFiles, endFile, dontDelFiles);
						deleteIntermFiles(markForDelete, endFile, dontDelFiles);
					}
				}
			}

			// email the message to the user
			// String email = dataMap.getString(EMAIL);
			StudyBean emailParentStudy;
			if (currentStudy.getParentStudyId() > 0) {
				emailParentStudy = (StudyBean) studyDao.findByPK(currentStudy.getParentStudyId());
			} else {
				emailParentStudy = currentStudy;
			}
			emailBuffer.append("<p>")
					.append(pageMessages.getString("html_email_body_5").replace("{0}", emailParentStudy.getName()))
					.append("</p>");
			try {

				if ((null != dataMap.get("job_type"))
						&& (((String) dataMap.get("job_type")).equalsIgnoreCase("exportJob"))) {
					String extractName = (String) dataMap.get(XsltTriggerService.JOB_NAME);
					TriggerBean triggerBean = new TriggerBean();
					triggerBean.setDataset(datasetBean);
					triggerBean.setUserAccount(userBean);
					triggerBean.setFullName(extractName);
					triggerBean.setFiredDate(context.getFireTime());
					String actionMsg = "You may access the " + dataMap.get(XsltTriggerService.EXPORT_FORMAT)
							+ " file by changing your study/site to " + currentStudy.getName()
							+ " and selecting the Export Data icon for " + datasetBean.getName()
							+ " dataset on the View Datasets page.";
					auditEventDAO
							.createRowForJobExecution(triggerBean,
									sasExtractJob && sasSideHasWarnings ? "__job_fired_success_warings"
											: "__job_fired_success", actionMsg);
				}
				mailSender.sendEmail(alertEmail, EmailEngine.getAdminEmail(), subject, emailBuffer.toString(), true);

			} catch (OpenClinicaSystemException ose) {
				logger.trace("exception sending mail: " + ose.getMessage());
				logger.error("exception sending mail: " + ose.getMessage());
			}

			logger.trace("just sent email to " + alertEmail + ", from " + EmailEngine.getAdminEmail());
			if (successMsg == null) {
				successMsg = " ";
			}

			postSuccessMessage(successMsg, context);
			logMe(emailBuffer.toString());
		} catch (TransformerConfigurationException e) {
			sendErrorEmail(e.getMessage(), context, alertEmail);
			postErrorMessage(e.getMessage(), context);
			e.printStackTrace();
			logger.error("Error has occurred.", e);
			exceptions = true;
		} catch (FileNotFoundException e) {
			sendErrorEmail(e.getMessage(), context, alertEmail);
			postErrorMessage(e.getMessage(), context);
			e.printStackTrace();
			logger.error("Error has occurred.", e);
			exceptions = true;
		} catch (TransformerFactoryConfigurationError e) {
			sendErrorEmail(e.getMessage(), context, alertEmail);
			postErrorMessage(e.getMessage(), context);
			e.printStackTrace();
			logger.error("Error has occurred.", e);
			exceptions = true;
		} catch (TransformerException e) {
			sendErrorEmail(e.getMessage(), context, alertEmail);
			postErrorMessage(e.getMessage(), context);
			e.printStackTrace();
			logger.error("Error has occurred.", e);
			exceptions = true;
		} catch (Exception ee) {
			if (reportAboutException) {
				sendErrorEmail(ee.getMessage(), sasSideHasErrors ? sasErrors.replaceAll("\n", "<br/>") : null, context,
						alertEmail);
				postErrorMessage(!sasExtractJob && sasSideHasErrors ? sasExceptionMessage : ee.getMessage(),
						sasSideHasErrors && sasExtractJob ? sasErrors.replaceAll("\n", "<br/>") : null, context);
			}
			logger.error("Error has occurred.".concat(sasSideHasErrors ? sasErrors : ""), ee);
			exceptions = true;

			if ((null != dataMap.get("job_type")) && (((String) dataMap.get("job_type")).equalsIgnoreCase("exportJob"))) {
				TriggerBean triggerBean = new TriggerBean();
				triggerBean.setUserAccount(userBean);
				triggerBean.setFullName((String) dataMap.get(XsltTriggerService.JOB_NAME));
				triggerBean.setFiredDate(context.getFireTime());
				auditEventDAO.createRowForExtractDataJobFailure(triggerBean);
			}

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Error has occurred.", e);
				}
			}
			if (endFileStream != null) {
				try {
					endFileStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Error has occurred.", e);
				}
			}
			if (exceptions) {
				try {
					logger.debug("EXCEPTIONS... EVEN DELETING OF OLD FILES");
					String generalFileDir = dataMap.getString(XML_FILE_PATH);
					File oldFilesPath = new File(generalFileDir);
					String endFile = dataMap.getString(POST_FILE_PATH) + File.separator
							+ dataMap.getString(POST_FILE_NAME);
					if (oldFilesPath.isDirectory()) {

						markForDelete = Arrays.asList(oldFilesPath.listFiles());

					}
					logger.debug("deleting the old files reference from archive dataset");

					if (deleteOld) {
						deleteIntermFiles(markForDelete, endFile, doNotDeleteUntilExtract);
					}
				} catch (Exception e) {
					logger.error("Error has occurred.", e);
				}
			}
			resetArchiveDataset(datasetBean);
			if (unscheduleSASJob) {
				deleteDirectory(new File((String) dataMap.remove(SAS_JOB_DIR)));
				try {
					if (!sasExtractJob) {
						context.getScheduler().unscheduleJob(context.getTrigger().getKey());
					} else {
						((SimpleTriggerImpl) context.getTrigger())
								.setMisfireInstruction(SimpleTriggerImpl.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
						((SimpleTriggerImpl) context.getTrigger()).setRepeatCount((Integer) dataMap
								.get(SAS_EXTRACT_REPEAT_COUNT));
						((SimpleTriggerImpl) context.getTrigger()).setRepeatInterval((Long) dataMap
								.get(SAS_EXTRACT_REPEAT_INTERVAL));
						Calendar calendar = GregorianCalendar.getInstance();
						calendar.setTime((Date) dataMap.get(SAS_EXTRACT_JOB_NEXT_FIRE_TIME));
						int prevHour = calendar.get(Calendar.HOUR_OF_DAY);
						int prevMinute = calendar.get(Calendar.MINUTE);
						Date currentDate = new Date(System.currentTimeMillis());
						calendar.setTime(currentDate);
						calendar.set(Calendar.HOUR_OF_DAY, prevHour);
						calendar.set(Calendar.MINUTE, prevMinute);
						Date newDate = calendar.getTime();
						if (newDate.compareTo(currentDate) <= 0) {
							String periodToRun = (String) dataMap.get("periodToRun");
							if (periodToRun.equalsIgnoreCase("monthly")) {
								calendar.add(Calendar.MONTH, 1);
							} else if (periodToRun.equalsIgnoreCase("weekly")) {
								calendar.add(Calendar.WEEK_OF_MONTH, 1);
							} else {
								calendar.add(Calendar.DAY_OF_MONTH, 1);
							}
							newDate = calendar.getTime();
						}
						((SimpleTriggerImpl) context.getTrigger()).setStartTime(newDate);
						((SimpleTriggerImpl) context.getTrigger()).setJobDataMap(dataMap);
						context.getScheduler().rescheduleJob(context.getTrigger().getKey(), context.getTrigger());
					}
				} catch (Exception e) {
					logger.error("Error has occurred.", e);
				}
			}
		}
	}

	private void deleteDirectory(File sasJobDirFile) {
		try {
			FileUtils.deleteDirectory(sasJobDirFile);
		} catch (Exception ex) {
			logger.error("Error has occurred.", ex);
		}
	}

	private void logMe(String message) {
		logger.debug(message);
	}

	private void zipAll(String path, String[] files, String zipname) throws IOException {
		final int bufferSize = 2048;
		BufferedInputStream orgin = null;

		FileInputStream fis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(zipname);
			zos = new ZipOutputStream(fos);
			byte[] data = new byte[bufferSize];

			for (String file : files) {
				logMe("Path = " + path + "zipName = " + zipname);
				fis = new FileInputStream(path + file);

				orgin = new BufferedInputStream(fis, bufferSize);
				ZipEntry entry = new ZipEntry(file);
				zos.putNextEntry(entry);
				int cnt;
				while ((cnt = orgin.read(data, 0, bufferSize)) != -1) {
					zos.write(data, 0, cnt);
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (orgin != null) {
				orgin.close();
			}
			if (zos != null) {
				zos.close();
			}
			if (fos != null) {
				fos.close();
			}

		}
	}

	/**
	 * To go through all the existing archived datasets and delete off the records whose file references do not exist
	 * any more.
	 *
	 * @param datasetBean
	 *            DatasetBean
	 */
	private void resetArchiveDataset(DatasetBean datasetBean) {
		try {
			if (datasetBean != null) {
				int datasetId = datasetBean.getId();
				ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(dataSource);
				ArrayList<ArchivedDatasetFileBean> al = asdfDAO.findByDatasetId(datasetId);
				for (ArchivedDatasetFileBean fbExisting : al) {
					logMe("The file to checking?" + fbExisting.getFileReference() + "Does the file exist?"
							+ new File(fbExisting.getFileReference()).exists());
					logMe("check if it still exists in archive set before"
							+ asdfDAO.findByDatasetId(fbExisting.getDatasetId()).size());
					if (!new File(fbExisting.getFileReference()).exists()) {
						logMe(fbExisting.getFileReference() + "Doesnt exist,deleting it from archiveset data");
						asdfDAO.deleteArchiveDataset(fbExisting);

					}
					logMe("check if it still exists in archive set after"
							+ asdfDAO.findByDatasetId(fbExisting.getDatasetId()).size());
				}
			}
		} catch (Exception e) {
			logger.error("Error has occurred.", e);
		}
	}

	// zips up the resultant xml file
	private List<File> zipxmls(List<File> deleteFilesList, String endFileName) throws IOException {
		final int bufferSize = 2048;
		BufferedInputStream orgin = null;
		File endFile = new File(endFileName);
		FileInputStream fis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fis = new FileInputStream(endFile);

			fos = new FileOutputStream(endFileName + ".zip");
			zos = new ZipOutputStream(fos);

			byte[] data = new byte[bufferSize];
			orgin = new BufferedInputStream(fis, bufferSize);
			ZipEntry entry = new ZipEntry(new ZipEntry(endFile.getName()));
			zos.putNextEntry(entry);
			int cnt;
			while ((cnt = orgin.read(data, 0, bufferSize)) != -1) {
				zos.write(data, 0, cnt);
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (orgin != null) {
				orgin.close();
			}
			if (zos != null) {
				zos.close();
			}
			if (fos != null) {
				fos.close();
			}

		}

		// since zip is successful, deleting the endfile.
		logger.trace("About to delete file" + endFile.getName());
		boolean deleted = endFile.delete();
		logger.trace("deleted?" + deleted);
		logger.trace("Does the file exist still?" + endFile.exists());
		return deleteFilesList;

	}

	private void deleteIntermFiles(List<File> intermediateFiles, String dontDeleteFile, String[] dontDelFiles) {

		Iterator<File> fileIt = intermediateFiles.iterator();
		File temp;
		File dontDelFile = new File(dontDeleteFile);
		int i;
		boolean del;
		while (fileIt.hasNext()) {
			temp = fileIt.next();
			if (!temp.getName().equals(dontDelFile.getName())) {
				i = 0;
				del = true;
				logMe("File Name?" + temp.getName());

				while (i < dontDelFiles.length && del) {

					if (temp.getName().equals(dontDelFiles[i])) {
						logMe("file to deleted:" + temp.getName() + "File Not to deleted:" + dontDelFiles[i]);

						del = false;
						// file name contained in doNotDelete list,
						// break;
					}
					i++;
				}
				if (del) {
					temp.delete();
				}
			}
		}
	}

	/**
	 * Utility method, might be useful in the future to convert to kilobytes.
	 *
	 * @param bytes
	 *            long
	 * @return float
	 */
	public float bytesToKilo(long bytes) {
		logger.trace("output bytes?" + bytes + "divided by 1024" + bytes / KILOBYTE);
		logger.trace("output bytes?" + bytes + "divided by 1024" + (float) bytes / KILOBYTE);
		return (float) bytes / KILOBYTE;
	}

	// A stub to delete old files.
	private void deleteOldFiles(File[] oldFiles) {
		// File[] files = complete.listFiles();
		for (File oldFile : oldFiles) {
			if (oldFile.exists()) {
				oldFile.delete();
			}
		}
	}

	/**
	 * Stub to get the list of all old files.
	 *
	 * @param outputPath
	 *            String
	 * @param postProcLoc
	 *            String
	 * @return File[]
	 */
	private File[] getOldFiles(String outputPath, String postProcLoc) {
		File[] exisitingFiles = null;
		File temp;
		if (postProcLoc != null) {
			temp = new File(postProcLoc);
			if (temp.isDirectory()) {
				exisitingFiles = temp.listFiles();
			}
		} else {
			temp = new File(outputPath);
			if (temp.isDirectory()) {
				exisitingFiles = temp.listFiles();
			}
		}

		return exisitingFiles;
	}

	private File[] getInterFiles(String xmlLoc) {
		File[] exisitingFiles = null;
		File temp;
		if (xmlLoc != null) {
			temp = new File(xmlLoc);
			if (temp.isDirectory()) {
				exisitingFiles = temp.listFiles();
			}
		}

		return exisitingFiles;
	}

	private void postSuccessMessage(String message, JobExecutionContext context) {
		try {
			ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext()
					.get("applicationContext");
			StdScheduler scheduler = (StdScheduler) appContext.getBean("schedulerFactoryBean");
			JobDetailImpl jobDetail = (JobDetailImpl) context.getJobDetail();
			JobDataMap dataMap = jobDetail.getJobDataMap();
			dataMap.put("successMsg", message);
			jobDetail.setJobDataMap(dataMap);
			// replace the job with the extra data
			scheduler.addJob(jobDetail, true);

		} catch (Exception e) {
			logger.error("Error has occurred.", e);
		}
	}

	private void postErrorMessage(String message, String additionalMessage, JobExecutionContext context) {
		try {
			ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext()
					.get("applicationContext");
			StdScheduler scheduler = (StdScheduler) appContext.getBean("schedulerFactoryBean");
			JobDetailImpl jobDetail = (JobDetailImpl) context.getJobDetail();
			JobDataMap dataMap = jobDetail.getJobDataMap();
			dataMap.put("failMessage", message.concat(additionalMessage != null ? additionalMessage : ""));
			jobDetail.setJobDataMap(dataMap);
			// replace the job with the extra data
			scheduler.addJob(jobDetail, true);

		} catch (Exception e) {
			logger.error("Error has occurred.", e);
		}
	}

	private void postErrorMessage(String message, JobExecutionContext context) {
		postErrorMessage(message, null, context);
	}

	private ArchivedDatasetFileBean generateFileRecord(String name, String dir, DatasetBean datasetBean, double time,
			long fileLength, ExportFormatBean efb, int userBeanId) {
		ArchivedDatasetFileBean fbInitial = new ArchivedDatasetFileBean();
		// Deleting off the original file archive dataset file.
		ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(dataSource);

		fbInitial.setName(name);
		fbInitial.setFileReference(dir + name);

		// JN: the following is to convert to KB, not possible without changing
		// database schema
		// fbInitial.setFileSize( setFormat(bytesToKilo(fileLength)));

		fbInitial.setFileSize((int) (fileLength));

		fbInitial.setRunTime(time);
		// to convert to seconds
		fbInitial.setDatasetId(datasetBean.getId());
		fbInitial.setExportFormatBean(efb);
		fbInitial.setExportFormatId(efb.getId());
		fbInitial.setOwnerId(userBeanId);
		fbInitial.setDateCreated(new Date(System.currentTimeMillis()));

		return (ArchivedDatasetFileBean) asdfDAO.create(fbInitial);
	}

	private void sendErrorEmail(String message, String additionalBody, JobExecutionContext context, String target) {
		String subject = "Warning: " + message;
		String emailBody = "An exception was thrown while running an extract job on your server, please see the logs for more details."
				.concat(additionalBody != null ? additionalBody : "");
		try {
			ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext()
					.get("applicationContext");
			mailSender = (OpenClinicaMailSender) appContext.getBean("openClinicaMailSender");

			mailSender.sendEmail(target, EmailEngine.getAdminEmail(), subject, emailBody, false);
			logger.trace("sending an email to " + target + " from " + EmailEngine.getAdminEmail());
		} catch (SchedulerException se) {
			se.printStackTrace();
		} catch (OpenClinicaSystemException ose) {
			ose.printStackTrace();
		}
	}

	private void sendErrorEmail(String message, JobExecutionContext context, String target) {
		sendErrorEmail(message, null, context, target);
	}

	// Utility method to format upto 3 decimals.
	private double setFormat(double number) {
		if (number < 1) {
			number = 1.0;
		}
		BigDecimal num = new BigDecimal(number);
		logger.trace("Number is" + num.setScale(THREE, BigDecimal.ROUND_HALF_UP).doubleValue());
		return num.setScale(THREE, BigDecimal.ROUND_HALF_UP).doubleValue();
		/*
		 * DecimalFormat df = new DecimalFormat("#.#"); logger.trace("Number is" +
		 * Double.parseDouble(df.format(number))); logger.trace("Number is" + (float)
		 * Double.parseDouble(df.format(number))); return Double.valueOf(df.format(number));
		 */
	}

}
