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

package org.akaza.openclinica.web.job;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.sax.SAXSource;

import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.NamespaceFilter;
import org.akaza.openclinica.bean.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SummaryStatsBean;
import org.akaza.openclinica.core.OpenClinicaMailSender;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.logic.rulerunner.ImportDataRuleRunnerContainer;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.akaza.openclinica.util.ImportSummaryInfo;
import org.akaza.openclinica.web.SQLInitServlet;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.clinovo.crfdata.ImportCRFDataService;
import com.clinovo.service.ItemSDVService;
import com.clinovo.service.StudySubjectIdService;
import com.clinovo.util.EmailUtil;
import com.clinovo.util.RuleSetServiceUtil;
import com.clinovo.util.ValidatorHelper;

/**
 * Import Spring Job, a job running asynchronously on the Tomcat server using Spring and Quartz.
 *
 * @author thickerson, 04/2009
 */
public class ImportSpringJob extends QuartzJobBean {

	public static final int INT_52 = 52;
	public static final int INT_1024 = 1024;
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private ResourceBundle respage;
	private ResourceBundle resword;

	private Locale locale;
	public static final String DIRECTORY = "filePathDir";
	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";
	public static final String STUDY_NAME = "study_name";
	public static final String STUDY_OID = "study_oid";
	public static final String DEST_DIR = "Event_CRF_Data";
	public static final String DIR_PATH = "scheduled_data_import";
	public static final String STUDY_ID = "studyId";
	public static final String JOB_NAME = "jobName";
	public static final String JOB_DESC = "jobDesc";
	public static final String HOURS = "hours";
	public static final String MINUTES = "minutes";
	public static final String JOB_MINUTE = "jobMinute";
	public static final String JOB_HOUR = "jobHour";
	public static final String FILE_PATH = "filePath";
	public static final String FILE_PATH_DIR = "filePathDir";
	public static final String STUDIES = "studies";
	public static final String FIRST_FILE_PATH = "firstFilePath";
	public static final String TNAME = "tname";

	private TriggerService triggerService;
	private ImportCRFDataService importCrfDataService;

	private ValidatorHelper validatorHelper;

	private OpenClinicaMailSender mailSender;

	private StudyDAO sdao;
	private AuditEventDAO auditEventDAO;

	private ItemSDVService itemSDVService;
	private RuleSetService ruleSetService;
	private StudySubjectIdService studySubjectIdService;

	private class ProcessDataResult {
		private String msg;
		private String auditMsg;
	}

	@Override
	protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {
		logger.debug("Starting execute internal");
		ApplicationContext appContext;
		try {
			appContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
			TransactionTemplate transactionTemplate = (TransactionTemplate) appContext
					.getBean("sharedTransactionTemplate");
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					executeInternalInTransaction(context);
				}
			});
		} catch (SchedulerException e) {
			logger.debug("found exception");
			e.printStackTrace();
			throw new JobExecutionException(e);
		}
	}

	protected void executeInternalInTransaction(JobExecutionContext context) {

		JobDataMap dataMap = context.getMergedJobDataMap();
		String localeStr = dataMap.getString(ExampleSpringJob.LOCALE);
		if (localeStr != null) {
			locale = new Locale(localeStr);
		} else {
			locale = new Locale("en");
		}
		ResourceBundleProvider.updateLocale(locale);
		respage = ResourceBundleProvider.getPageMessagesBundle();
		resword = ResourceBundleProvider.getWordsBundle();
		triggerService = new TriggerService();
		SimpleTriggerImpl trigger = (SimpleTriggerImpl) context.getTrigger();
		TriggerBean triggerBean = new TriggerBean();
		triggerBean.setFullName(trigger.getName());
		triggerBean.setFiredDate(trigger.getStartTime());
		String contactEmail = dataMap.getString(EMAIL);
		logger.debug("=== starting to run trigger " + trigger.getName() + " ===");
		StudyBean emailParentStudy = null;
		try {
			ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext()
					.get("applicationContext");
			ruleSetService = RuleSetServiceUtil.createRuleSetService(appContext);
			DataSource dataSource = (DataSource) appContext.getBean("dataSource");
			itemSDVService = (ItemSDVService) appContext.getBean("itemSDVServiceImpl");
			mailSender = (OpenClinicaMailSender) appContext.getBean("openClinicaMailSender");
			ConfigurationDao configurationDao = (ConfigurationDao) appContext.getBean("configurationDao");
			studySubjectIdService = (StudySubjectIdService) appContext.getBean("studySubjectIdServiceImpl");
			importCrfDataService = new ImportCRFDataService(ruleSetService, itemSDVService, studySubjectIdService,
					dataSource, locale);

			sdao = new StudyDAO(dataSource);
			auditEventDAO = new AuditEventDAO(dataSource);
			StudyConfigService scs = new StudyConfigService(dataSource);
			UserAccountDAO userAccountDAO = new UserAccountDAO(dataSource);

			validatorHelper = new ValidatorHelper(configurationDao, locale);

			int userId = dataMap.getInt(USER_ID);
			UserAccountBean ub = (UserAccountBean) userAccountDAO.findByPK(userId);
			triggerBean.setUserAccount(ub);

			String directory = dataMap.getString(DIRECTORY);
			String studyName = dataMap.getString(STUDY_NAME);
			String studyOid = dataMap.getString(STUDY_OID);
			if (localeStr != null) {
				locale = new Locale(localeStr);
				ResourceBundleProvider.updateLocale(locale);
				respage = ResourceBundleProvider.getPageMessagesBundle();
			}
			StudyBean studyBean;
			if (studyOid != null) {
				studyBean = sdao.findByOid(studyOid);
			} else {
				studyBean = (StudyBean) sdao.findByName(studyName);
			}
			studyBean = scs.setParametersForStudy(studyBean);
			if (studyBean.getStatus().isFrozen()) {
				try {
					String message = respage.getString("import_study_frozen");
					auditEventDAO.createRowForExtractDataJobFailure(triggerBean, message);
					if (contactEmail != null && !"".equals(contactEmail)) {
						if (studyBean.getParentStudyId() > 0) {
							emailParentStudy = (StudyBean) sdao.findByPK(studyBean.getParentStudyId());
						} else {
							emailParentStudy = studyBean;
						}
						String body = EmailUtil.getEmailBodyStart() + respage.getString("html_email_header_1") + " "
								+ contactEmail + ",<br>" + respage.getString("your_job_ran_html") + "  "
								+ respage.getString("please_review_the_data_html") + message + "<br/>"
								+ respage.getString("best_system_administrator").replace("{0}",
										emailParentStudy.getName())
								+ EmailUtil.getEmailBodyEnd() + EmailUtil.getEmailFooter(locale);
						mailSender.sendEmail(contactEmail,
								respage.getString("job_ran_for") + " " + triggerBean.getFullName(), body, true);
					}
				} catch (OpenClinicaSystemException ose) {
					logger.error("=== throw an ocse: " + ose.getMessage());
				}
				return;
			}
			StudyBean mainStudyBean = studyBean;
			if (mainStudyBean.getParentStudyId() > 0) {
				mainStudyBean = (StudyBean) sdao.findByPK(mainStudyBean.getParentStudyId());
			}
			File fileDirectory = new File(SQLInitServlet.getField("filePath") + DIR_PATH + File.separator
					+ mainStudyBean.getOid() + File.separator);
			if (!"".equals(directory)) {
				fileDirectory = new File(SQLInitServlet.getField("filePath") + DIR_PATH + File.separator
						+ mainStudyBean.getOid() + File.separator + directory + File.separator);
			}
			if (!fileDirectory.isDirectory()) {
				fileDirectory.mkdirs();
			}
			File destDirectory = new File(SQLInitServlet.getField("filePath") + DEST_DIR + File.separator
					+ mainStudyBean.getOid() + File.separator);
			if (!destDirectory.isDirectory()) {
				destDirectory.mkdirs();
			}
			String[] files = fileDirectory.list();
			logger.debug("found " + files.length + " files under directory " + SQLInitServlet.getField("filePath")
					+ DIR_PATH + File.separator + mainStudyBean.getOid() + File.separator + directory);
			File[] target = new File[files.length];
			File[] destination = new File[files.length];
			for (int i = 0; i < files.length; i++) {
				if (!new File(fileDirectory + File.separator + files[i]).isDirectory()) {
					File f = new File(fileDirectory + File.separator + files[i]);
					if (!f.getName().contains(".xml") && !f.getName().contains(".XML")) {
						logger.debug("does not seem to be an xml file");

						// we need a place holder to avoid 'gaps' in the file
						// list
					} else {
						logger.debug("adding: " + f.getName());
						target[i] = f;
						destination[i] = new File(SQLInitServlet.getField("filePath") + DEST_DIR + File.separator
								+ mainStudyBean.getOid() + File.separator + files[i]);
					}
				}
			}
			if (target.length > 0 && destination.length > 0) {
				cutAndPaste(target, destination);
				destination = removeNullElements(destination);
				// do everything else here with 'destination'
				ProcessDataResult pdResult = processData(destination, respage, resword, ub, studyBean, destDirectory,
						triggerBean);

				auditEventDAO.createRowForExtractDataJobSuccess(triggerBean, pdResult.auditMsg);
				try {
					if (contactEmail != null && !"".equals(contactEmail)) {
						if (studyBean.getParentStudyId() > 0) {
							emailParentStudy = (StudyBean) sdao.findByPK(studyBean.getParentStudyId());
						} else {
							emailParentStudy = studyBean;
						}
						mailSender.sendEmail(contactEmail,
								respage.getString("job_ran_for") + " " + triggerBean.getFullName(),
								generateMsg(pdResult.msg, contactEmail, emailParentStudy.getName()), true);
						logger.debug("email body: " + pdResult.msg);
					}
				} catch (OpenClinicaSystemException e) {
					// Do nothing
					logger.error("=== throw an ocse === " + e.getMessage());
					e.printStackTrace();
				}

			} else {
				logger.debug("no real files found");
				auditEventDAO.createRowForExtractDataJobSuccess(triggerBean, respage.getString("job_ran_but_no_files"));
				// no email here, tbh
			}
		} catch (Exception e) {
			// more detailed reporting here
			logger.error("found a fail exception: " + e.getMessage());
			e.printStackTrace();
			auditEventDAO.createRowForExtractDataJobFailure(triggerBean, e.getMessage());
			try {
				String msg = EmailUtil.getEmailBodyStart() + respage.getString("html_email_header_1") + " "
						+ contactEmail + ",<br>";
				// Add information about error
				msg += respage.getString("your_job_ran_html") + "<br/><ul>" + resword.getString("job_error_mail.error")
						+ e.getMessage() + "</li>";
				// Add information about server where this error was thrown
				msg += resword.getString("job_error_mail.serverUrl") + CoreResources.getDomainName() + "</li></ul>";
				if (emailParentStudy != null) {
					msg += respage.getString("best_system_administrator").replace("{0}", emailParentStudy.getName());
				}
				msg += EmailUtil.getEmailBodyEnd() + EmailUtil.getEmailFooter(CoreResources.getSystemLocale());
				mailSender.sendEmail(contactEmail,
						respage.getString("job_failure_for") + " " + triggerBean.getFullName(), msg, true);
			} catch (OpenClinicaSystemException ose) {
				// Do nothing
				logger.error("=== throw an ocse: " + ose.getMessage());
			}
		}
	}

	private String generateMsg(String msg, String contactEmail, String studyName) {
		return EmailUtil.getEmailBodyStart() + respage.getString("html_email_header_1") + " " + contactEmail + ",<br>"
				+ respage.getString("your_job_ran_success_html") + "  "
				+ respage.getString("please_review_the_data_html") + msg + "<br/>"
				+ respage.getString("best_system_administrator").replace("{0}", studyName) + EmailUtil.getEmailBodyEnd()
				+ EmailUtil.getEmailFooter(CoreResources.getSystemLocale());
	}

	/*
	 * processData, a method which should take in all XML files, check to see if they were imported previously, ? insert
	 * them into the database if not, and return a message which will go to audit and to the end user. Sergey thinks
	 * that we should use here the single transaction logic from the VerifyImportedCRFDataServlet.
	 */
	private ProcessDataResult processData(File[] dest, ResourceBundle respage, ResourceBundle resword,
			UserAccountBean ub, StudyBean studyBean, File destDirectory, TriggerBean triggerBean) throws Exception {

		BufferedWriter out;
		boolean fail = false;
		ODMContainer odmContainer;
		boolean hasSkippedItems = false;
		StringBuilder msg = new StringBuilder();
		StringBuilder auditMsg = new StringBuilder();
		ImportSummaryInfo summary = new ImportSummaryInfo();
		Set<Integer> skippedItemIds = new HashSet<Integer>();
		List<Map<String, Object>> auditItemList = new ArrayList<Map<String, Object>>();

		for (File f : dest) {
			String regex = "\\s+"; // all whitespace, one or more times
			String replacement = "_"; // replace with underscores
			String pattern = "yyyy" + File.separator + "MM" + File.separator + "dd" + File.separator + "HHmmssSSS"
					+ File.separator;
			SimpleDateFormat sdfDir = new SimpleDateFormat(pattern);
			String generalFileDir = sdfDir.format(new java.util.Date());
			File logDestDirectory = new File(destDirectory + File.separator + generalFileDir
					+ f.getName().replaceAll(regex, replacement) + ".log.txt");
			if (!logDestDirectory.isDirectory()) {
				logger.debug("creating new dir: " + logDestDirectory.getAbsolutePath());
				logDestDirectory.mkdirs();
			}
			File newFile = new File(logDestDirectory, "log.txt");
			out = new BufferedWriter(new FileWriter(newFile));

			String firstLine = "<P>" + f.getName() + ": <br/>";
			msg.append(firstLine);
			out.write(firstLine);
			auditMsg.append(firstLine);
			JAXBContext jaxbContext = JAXBContext.newInstance(ODMContainer.class);
			javax.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			XMLReader reader = XMLReaderFactory.createXMLReader();
			NamespaceFilter namespaceFilter = new NamespaceFilter("http://www.cdisc.org/ns/odm/v1.3", true);
			namespaceFilter.setParent(reader);

			InputSource inputSource = new InputSource(new FileInputStream(f));
			SAXSource saxSource = new SAXSource(namespaceFilter, inputSource);
			try {
				odmContainer = (ODMContainer) jaxbUnmarshaller.unmarshal(saxSource);
				logger.debug("Found crf data container for study oid: "
						+ odmContainer.getCrfDataPostImportContainer().getStudyOID());
				logger.debug("found length of subject list: "
						+ odmContainer.getCrfDataPostImportContainer().getSubjectData().size());
			} catch (Exception me1) {

				MessageFormat mf = new MessageFormat("");
				mf.applyPattern(respage.getString("your_xml_is_not_well_formed"));
				Object[] arguments = {me1.getMessage()};
				msg.append(mf.format(arguments)).append("<br/>");
				auditMsg.append(mf.format(arguments)).append("<br/>");
				logger.error("found an error with XML: " + msg.toString());
				continue;
			}
			// next: check, then import
			List<String> errors = importCrfDataService.validateStudyMetadata(odmContainer, studyBean.getId(), ub);
			// this needs to be replaced with the study name from the job, since
			// the user could be in any study ...
			if (errors != null) {
				// add to session
				// forward to another page

				if (errors.size() > 0) {
					out.write("<P>Errors:<br/>");
					for (String error : errors) {
						out.write(error + "<br/>");
					}
					out.write("</P>");
					MessageFormat mf = new MessageFormat("");
					mf.applyPattern(respage.getString("your_xml_in_the_file"));
					Object[] arguments = {f.getName(), errors.size()};
					auditMsg.append(mf.format(arguments)).append("<br/>");
					msg.append(mf.format(arguments)).append("<br/>");
					auditMsg.append(respage.getString("you_can_see_the_log_file")).append(" <a href='")
							.append(SQLInitServlet.getSystemURL()).append("ViewLogMessage?n=").append(generalFileDir)
							.append(f.getName()).append("&tn=").append(triggerBean.getName())
							.append("&gn=1'>here</a>.<br/>");
					msg.append(respage.getString("you_can_see_the_log_file")).append(" <a href='")
							.append(SQLInitServlet.getSystemURL()).append("ViewLogMessage?n=").append(generalFileDir)
							.append(f.getName()).append("&tn=").append(triggerBean.getName())
							.append("&gn=1'>here</a>.<br/>");
					continue;
				} else {
					msg.append(respage.getString("passed_study_check")).append("<br/>");
					msg.append(respage.getString("passed_oid_metadata_check")).append("<br/>");
					auditMsg.append(respage.getString("passed_study_check")).append("<br/>");
					auditMsg.append(respage.getString("passed_oid_metadata_check")).append("<br/>");
				}

			}
			List<EventCRFBean> eventCRFBeans = importCrfDataService.fetchEventCRFBeans(odmContainer, ub);

			ArrayList<Integer> permittedEventCRFIds = new ArrayList<Integer>();
			logger.debug("found a list of eventCRFBeans: " + eventCRFBeans.toString());

			List<DisplayItemBeanWrapper> displayItemBeanWrappers = new ArrayList<DisplayItemBeanWrapper>();
			HashMap<String, String> totalValidationErrors = new HashMap<String, String>();
			HashMap<String, String> hardValidationErrors = new HashMap<String, String>();

			if (!eventCRFBeans.isEmpty()) {
				for (EventCRFBean eventCRFBean : eventCRFBeans) {
					DataEntryStage dataEntryStage = eventCRFBean.getStage();
					Status eventCRFStatus = eventCRFBean.getStatus();

					logger.debug("Event CRF Bean: id " + eventCRFBean.getId() + ", data entry stage "
							+ dataEntryStage.getName() + ", status " + eventCRFStatus.getName());
					if (eventCRFStatus.equals(Status.AVAILABLE)
							|| dataEntryStage.equals(DataEntryStage.INITIAL_DATA_ENTRY)
							|| dataEntryStage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
							|| dataEntryStage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)
							|| dataEntryStage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
						permittedEventCRFIds.add(eventCRFBean.getId());
					} else {
						MessageFormat mf = new MessageFormat("");
						mf.applyPattern(respage.getString("your_listed_crf_in_the_file"));
						Object[] arguments = {f.getName()};
						msg.append(mf.format(arguments)).append("<br/>");
						auditMsg.append(mf.format(arguments)).append("<br/>");
						out.write(mf.format(arguments) + "<br/>");
					}
				}

				if (eventCRFBeans.size() >= permittedEventCRFIds.size()) {
					msg.append(respage.getString("passed_event_crf_status_check")).append("<br/>");
					auditMsg.append(respage.getString("passed_event_crf_status_check")).append("<br/>");
				} else {
					fail = true;
					msg.append(respage.getString("the_event_crf_not_correct_status")).append("<br/>");
					auditMsg.append(respage.getString("the_event_crf_not_correct_status")).append("<br/>");
				}

				try {
					List<DisplayItemBeanWrapper> tempDisplayItemBeanWrappers;
					tempDisplayItemBeanWrappers = importCrfDataService.lookupValidationErrors(validatorHelper,
							odmContainer, ub, totalValidationErrors, hardValidationErrors, permittedEventCRFIds);
					displayItemBeanWrappers.addAll(tempDisplayItemBeanWrappers);
				} catch (NullPointerException npe1) {
					npe1.printStackTrace();
					fail = true;
					logger.debug("threw a NPE after calling lookup validation errors");
					msg.append(respage.getString("an_error_was_thrown_while_validation_errors")).append("<br/>");
					auditMsg.append(respage.getString("an_error_was_thrown_while_validation_errors")).append("<br/>");
					out.write(respage.getString("an_error_was_thrown_while_validation_errors") + "<br/>");
					logger.debug("=== threw the null pointer, import ===");
				} catch (OpenClinicaException oce1) {
					fail = true;
					logger.error("threw an OCE after calling lookup validation errors " + oce1.getOpenClinicaMessage());
					msg.append("<br/>").append(oce1.getOpenClinicaMessage()).append("<br/>");
					auditMsg.append("<br/>").append(oce1.getOpenClinicaMessage()).append("<br/>");
					out.write(oce1.getOpenClinicaMessage() + "<br/>");

				}
			} else {
				msg.append(respage.getString("no_event_crfs_matching_the_xml_metadata")).append("<br/>");
				out.write(respage.getString("no_event_crfs_matching_the_xml_metadata") + "<br/>");
				continue;
			}

			ArrayList<SubjectDataBean> subjectData = odmContainer.getCrfDataPostImportContainer().getSubjectData();

			SummaryStatsBean ssBean = importCrfDataService.generateSummaryStatsBean(odmContainer,
					displayItemBeanWrappers, validatorHelper);
			hasSkippedItems = validatorHelper.getAttribute(ImportCRFDataService.IMPORT_HAS_DATA_TO_SKIP) != null;

			if (!hardValidationErrors.isEmpty()) {
				String messageHardVals = triggerService.generateHardValidationErrorMessage(subjectData,
						hardValidationErrors, false, false, respage, resword, ssBean);
				out.write(messageHardVals);
			} else {
				if (!totalValidationErrors.isEmpty()) {
					String totalValErrors = triggerService.generateHardValidationErrorMessage(subjectData,
							totalValidationErrors, false, false, respage, resword, ssBean);
					out.write(totalValErrors);
				}
				String validMsgs = triggerService.generateValidMessage(subjectData, totalValidationErrors,
						hasSkippedItems, respage, resword, ssBean);
				out.write(validMsgs);
			}

			if (fail) {
				MessageFormat mf = new MessageFormat("");
				mf.applyPattern(respage.getString("problems_encountered_with_file"));
				Object[] arguments = {f.getName(), msg.toString()};
				msg = new StringBuilder();
				msg.append(mf.format(arguments)).append("<br/>");
				continue;
			} else {
				msg.append(respage.getString("passing_crf_edit_checks")).append("<br/>");
				auditMsg.append(respage.getString("passing_crf_edit_checks")).append("<br/>");
				logger.debug("found total validation errors: " + totalValidationErrors.size());
				msg.append(triggerService.generateSummaryStatsMessage(ssBean, respage, resword)).append("<br/>");

				// actually the crf data import is here
				List<ImportDataRuleRunnerContainer> containers = importCrfDataService.importCrfData(studyBean, ub,
						skippedItemIds, auditItemList, displayItemBeanWrappers, summary, odmContainer);

				msg.append(respage.getString("data_has_been_successfully_import")).append("<br/>");
				auditMsg.append(respage.getString("data_has_been_successfully_import")).append("<br/>");

				msg.append(summary.prepareSummaryMessage(resword));
				auditMsg.append(summary.prepareSummaryMessage(resword));

				boolean importHasDataForUnavailableVersions =
						validatorHelper.getAttribute(ImportCRFDataService.IMPORT_HAS_DATA_FOR_UNAVAILABLE_VERSIONS) != null;
				if (hasSkippedItems && importHasDataForUnavailableVersions) {

					String additionalMsg = "<br/><br/>"
							.concat(resword.getString("import_contained_unavailable_crf_versions")).concat("<br/>")
							.concat(resword.getString("unavailable_crf_version_oids")).concat(":");
					for (String crfVersionOID : ssBean.getUnavailableCRFVersionOIDs()) {
						additionalMsg += "<br/>".concat(crfVersionOID);
					}
					additionalMsg = additionalMsg.concat("<br/>");
					auditMsg.append(additionalMsg);
				}

				boolean importHasDataToReplaceExisting =
						validatorHelper.getAttribute(ImportCRFDataService.IMPORT_HAS_DATA_TO_REPLACE_EXISTING) != null;
				if (hasSkippedItems && importHasDataToReplaceExisting) {
					String additionalMsg = "<br/>"
							+ (studyBean.getParentStudyId() > 0
									? respage.getString("site")
									: respage.getString("study"))
							+ " '" + studyBean.getName() + "' " + respage.getString("import_job_msg") + "  <br/>";
					msg.append(additionalMsg);
					auditMsg.append(additionalMsg);
				}

				String linkMessage = respage.getString("you_can_review_the_data") + SQLInitServlet.getSystemURL()
						+ respage.getString("you_can_review_the_data_2") + SQLInitServlet.getSystemURL()
						+ respage.getString("you_can_review_the_data_3") + generalFileDir + f.getName() + "&tn="
						+ triggerBean.getFullName() + "&gn=1" + respage.getString("you_can_review_the_data_4")
						+ "<br/>";
				msg.append(linkMessage);
				auditMsg.append(linkMessage);

				importCrfDataService.saveAuditItems(auditItemList);

				auditMsg.append(importCrfDataService.runRulesAndGenerateMessage(true, skippedItemIds, studyBean, ub,
						containers));
			}
			out.close();
		}

		ProcessDataResult pdResult = new ProcessDataResult();
		pdResult.msg = msg.toString();
		pdResult.auditMsg = auditMsg.toString();
		return pdResult;
	}

	private void cutAndPaste(File[] tar, File[] dest) throws IOException {
		for (int j = 0; j < tar.length; j++) {
			try {
				java.io.InputStream in = new FileInputStream(tar[j]);
				java.io.OutputStream out = new FileOutputStream(dest[j]);

				byte[] buf = new byte[INT_1024];
				int len;

				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				tar[j].delete();
			} catch (NullPointerException npe) {
				// list can be 'gappy' which is why we need to catch this
				logger.error("Found Npe: " + npe.getMessage());
			}
		}
	}

	private File[] removeNullElements(File[] source) {

		ArrayList<File> list = new ArrayList<File>();
		for (File f : source) {
			if (f != null) {
				list.add(f);
			}
		}
		return list.toArray(new File[list.size()]);
	}
}
