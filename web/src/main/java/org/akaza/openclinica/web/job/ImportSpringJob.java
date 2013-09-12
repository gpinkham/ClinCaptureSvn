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

import com.clinovo.util.ValidatorHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.sax.SAXSource;

import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.rule.XmlSchemaValidationHelper;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SummaryStatsBean;
import org.akaza.openclinica.core.OpenClinicaMailSender;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.ImportDataRuleRunnerContainer;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.util.DAOWrapper;
import org.akaza.openclinica.util.ImportSummaryInfo;
import org.akaza.openclinica.util.SubjectEventStatusUtil;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.crfdata.ImportCRFDataService;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.xml.sax.InputSource;

/**
 * Import Spring Job, a job running asynchronously on the Tomcat server using Spring and Quartz.
 * 
 * @author thickerson, 04/2009
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class ImportSpringJob extends QuartzJobBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	XmlSchemaValidationHelper schemaValidator = new XmlSchemaValidationHelper();

	ResourceBundle respage;
    ResourceBundle resword;

	Locale locale;
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

	public static final String IMPORT_DIR_2 = SQLInitServlet.getField("filePath") + DEST_DIR + File.separator;

	private DataSource dataSource;

    private TriggerService triggerService;
    private ImportCRFDataService dataService;
    private RuleSetServiceInterface ruleSetService;

    private ValidatorHelper validatorHelper;

    private OpenClinicaMailSender mailSender;

    private StudyDAO sdao;
    private EventCRFDAO ecdao;
    private StudyEventDAO sedao;
    private StudySubjectDAO ssdao;
    private ItemDataDAO itemDataDao;
    private DiscrepancyNoteDAO dndao;
	private AuditEventDAO auditEventDAO;
    private EventDefinitionCRFDAO edcdao;
    private ConfigurationDao configurationDao;

	@Override
	protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {
		logger.debug("=== starting execute internal ===");
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
		locale = new Locale("en-US");

		ResourceBundleProvider.updateLocale(locale);
		respage = ResourceBundleProvider.getPageMessagesBundle();
        resword = ResourceBundleProvider.getWordsBundle();
		triggerService = new TriggerService();

		JobDataMap dataMap = context.getMergedJobDataMap();
		SimpleTriggerImpl trigger = (SimpleTriggerImpl) context.getTrigger();
		TriggerBean triggerBean = new TriggerBean();
		triggerBean.setFullName(trigger.getName());
		triggerBean.setFiredDate(trigger.getStartTime());
		String contactEmail = dataMap.getString(EMAIL);
		logger.debug("=== starting to run trigger " + trigger.getName() + " ===");
		try {
			ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext()
					.get("applicationContext");
			dataSource = (DataSource) appContext.getBean("dataSource");
			mailSender = (OpenClinicaMailSender) appContext.getBean("openClinicaMailSender");
			ruleSetService = (RuleSetServiceInterface) appContext.getBean("ruleSetService");
            configurationDao = (ConfigurationDao) appContext.getBean("configurationDao");

			sdao = new StudyDAO(dataSource);
			ecdao = new EventCRFDAO(dataSource);
			sedao = new StudyEventDAO(dataSource);
			ssdao = new StudySubjectDAO(dataSource);
			itemDataDao = new ItemDataDAO(dataSource);
			dndao = new DiscrepancyNoteDAO(dataSource);
			auditEventDAO = new AuditEventDAO(dataSource);
			edcdao = new EventDefinitionCRFDAO(dataSource);
			StudyConfigService scs = new StudyConfigService(dataSource);
			UserAccountDAO userAccountDAO = new UserAccountDAO(dataSource);

            validatorHelper = new ValidatorHelper(configurationDao, locale);

			int userId = dataMap.getInt(USER_ID);
			UserAccountBean ub = (UserAccountBean) userAccountDAO.findByPK(userId);
			triggerBean.setUserAccount(ub);

			String directory = dataMap.getString(DIRECTORY);
			String studyName = dataMap.getString(STUDY_NAME);
			String studyOid = dataMap.getString(STUDY_OID);
			String localeStr = dataMap.getString(ExampleSpringJob.LOCALE);
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
			File fileDirectory = new File(SQLInitServlet.getField("filePath") + DIR_PATH + File.separator);
			if ("".equals(directory)) { // avoid NPEs
			} else {
				fileDirectory = new File(SQLInitServlet.getField("filePath") + DIR_PATH + File.separator + directory
						+ File.separator);
			}
			if (!fileDirectory.isDirectory()) {
				fileDirectory.mkdirs();
			}
			File destDirectory = new File(SQLInitServlet.getField("filePath") + DEST_DIR + File.separator);
			if (!destDirectory.isDirectory()) {
				destDirectory.mkdirs();
			}
			String[] files = fileDirectory.list();
			logger.debug("found " + files.length + " files under directory " + SQLInitServlet.getField("filePath")
					+ DIR_PATH + File.separator + directory);
			File[] target = new File[files.length];
			File[] destination = new File[files.length];
			for (int i = 0; i < files.length; i++) {
				if (!new File(fileDirectory + File.separator + files[i]).isDirectory()) {
					File f = new File(fileDirectory + File.separator + files[i]);
					if (f == null || f.getName() == null) {
						logger.debug("found a null file");
					} else if (f.getName().indexOf(".xml") < 0 && f.getName().indexOf(".XML") < 0) {
						logger.debug("does not seem to be an xml file");

						// we need a place holder to avoid 'gaps' in the file
						// list
					} else {
						logger.debug("adding: " + f.getName());
						target[i] = f;
						destination[i] = new File(SQLInitServlet.getField("filePath") + DEST_DIR + File.separator
								+ files[i]);
					}
				}
			}
			if (target.length > 0 && destination.length > 0) {
				cutAndPaste(target, destination);
				destination = removeNullElements(destination);
				// do everything else here with 'destination'
				ArrayList<String> auditMessages = processData(destination, dataSource, respage, ub, studyBean,
						destDirectory, triggerBean, ruleSetService);

				auditEventDAO.createRowForExtractDataJobSuccess(triggerBean, auditMessages.get(1));
				String skippedItemsSql = auditMessages.get(2);
				if (!skippedItemsSql.isEmpty()) {
					auditEventDAO.execute(skippedItemsSql);
				}
				try {
					if (contactEmail != null && !"".equals(contactEmail)) {
						StudyBean emailParentStudy = new StudyBean();
						if (studyBean.getParentStudyId() > 0) {
							emailParentStudy = (StudyBean) sdao.findByPK(studyBean.getParentStudyId());
						} else {
							emailParentStudy = studyBean;
						}
						mailSender.sendEmail(contactEmail,
								respage.getString("job_ran_for") + " " + triggerBean.getFullName(),
								generateMsg(auditMessages.get(0), contactEmail, emailParentStudy.getName()), true);
						logger.debug("email body: " + auditMessages.get(1));
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
				mailSender.sendEmail(contactEmail,
						respage.getString("job_failure_for") + " " + triggerBean.getFullName(), e.getMessage(), true);
			} catch (OpenClinicaSystemException ose) {
				// Do nothing
				logger.error("=== throw an ocse: " + ose.getMessage());

			}
		}
	}

	private ImportCRFDataService getImportCRFDataService(DataSource dataSource) {
		dataService = this.dataService != null ? dataService : new ImportCRFDataService(dataSource, locale);
		return dataService;
	}

	private String generateMsg(String msg, String contactEmail, String studyName) {
		String returnMe = respage.getString("html_email_header_1") + " " + contactEmail + ",<br>"
				+ respage.getString("your_job_ran_success_html") + "  "
				+ respage.getString("please_review_the_data_html") + msg
				+ "<br/>" + respage.getString("best_system_administrator").replace("{0}", studyName);
		return returnMe;
	}

	/*
	 * processData, a method which should take in all XML files, check to see if they were imported previously, ? insert
	 * them into the database if not, and return a message which will go to audit and to the end user.
	 */
	@SuppressWarnings("deprecation")
	private ArrayList<String> processData(File[] dest, DataSource dataSource, ResourceBundle respage,
			UserAccountBean ub, StudyBean studyBean, File destDirectory, TriggerBean triggerBean,
			RuleSetServiceInterface ruleSetService) throws Exception {
        ImportSummaryInfo summary = new ImportSummaryInfo();
        boolean hasSkippedItems = false;
        boolean runRulesOptimisation = true;
		StringBuffer msg = new StringBuffer();
		StringBuffer auditMsg = new StringBuffer();
        StringBuffer skippedItemsSql = new StringBuffer();
		String propertiesPath = CoreResources.PROPERTIES_DIR;

		File xsdFile2 = new File(propertiesPath + File.separator + "ODM1-2-1.xsd");
		boolean fail = false;
		ODMContainer odmContainer = new ODMContainer();
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
			BufferedWriter out = new BufferedWriter(new FileWriter(newFile));

			// TODO add more info here, like a timestamp

			String firstLine = "<P>" + f.getName() + ": ";
			msg.append(firstLine);
			out.write(firstLine);
			auditMsg.append(firstLine);
            JAXBContext jaxbContext = JAXBContext.newInstance(ODMContainer.class);
            javax.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            //Create SAXSource
            InputSource inputSource = new InputSource(new FileInputStream(f));
            SAXSource saxSource = new SAXSource(inputSource);
			try {
                odmContainer = (ODMContainer) jaxbUnmarshaller.unmarshal(saxSource);
				logger.debug("Found crf data container for study oid: "
						+ odmContainer.getCrfDataPostImportContainer().getStudyOID());
				logger.debug("found length of subject list: "
						+ odmContainer.getCrfDataPostImportContainer().getSubjectData().size());
			} catch (Exception me1) {
				// fail against one, try another
				try {
					schemaValidator.validateAgainstSchema(f, xsdFile2);
					// for backwards compatibility, we also try to validate vs
                    odmContainer = (ODMContainer) jaxbUnmarshaller.unmarshal(saxSource);
				} catch (Exception me2) {
					// not sure if we want to report me2

					MessageFormat mf = new MessageFormat("");
					mf.applyPattern(respage.getString("your_xml_is_not_well_formed"));
					Object[] arguments = { me1.getMessage() };
					msg.append(mf.format(arguments) + "<br/>");
					auditMsg.append(mf.format(arguments) + "<br/>");
					// break here with an exception
					logger.error("found an error with XML: " + msg.toString());
					// throw new Exception(msg.toString());
					// instead of breaking the entire operation, we should
					// continue looping
					continue;
				}
			}
			// next: check, then import
			List<String> errors = getImportCRFDataService(dataSource).validateStudyMetadata(odmContainer,
					studyBean.getId());
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
					Object[] arguments = { f.getName(), errors.size() };
					auditMsg.append(mf.format(arguments) + "<br/>");
					msg.append(mf.format(arguments) + "<br/>");
					auditMsg.append("You can see the log file <a href='" + SQLInitServlet.getSystemURL()
							+ "ViewLogMessage?n=" + generalFileDir + f.getName() + "&tn=" + triggerBean.getName()
							+ "&gn=1'>here</a>.<br/>");
					msg.append("You can see the log file <a href='" + SQLInitServlet.getSystemURL()
							+ "ViewLogMessage?n=" + generalFileDir + f.getName() + "&tn=" + triggerBean.getName()
							+ "&gn=1'>here</a>.<br/>");
					out.close();
					continue;
				} else {
					msg.append(respage.getString("passed_study_check") + "<br/>");
					msg.append(respage.getString("passed_oid_metadata_check") + "<br/>");
					auditMsg.append(respage.getString("passed_study_check") + "<br/>");
					auditMsg.append(respage.getString("passed_oid_metadata_check") + "<br/>");
				}

			}
			// validation errors, the same as in the ImportCRFDataServlet. DRY?
			List<EventCRFBean> eventCRFBeans = getImportCRFDataService(dataSource).fetchEventCRFBeans(odmContainer, ub);

			ArrayList<Integer> permittedEventCRFIds = new ArrayList<Integer>();
			logger.debug("found a list of eventCRFBeans: " + eventCRFBeans.toString());

			List<DisplayItemBeanWrapper> displayItemBeanWrappers = new ArrayList<DisplayItemBeanWrapper>();
			HashMap<String, String> totalValidationErrors = new HashMap<String, String>();
			HashMap<String, String> hardValidationErrors = new HashMap<String, String>();

			// -- does the event already exist? if not, fail
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
						permittedEventCRFIds.add(new Integer(eventCRFBean.getId()));
					} else {
						MessageFormat mf = new MessageFormat("");
						mf.applyPattern(respage.getString("your_listed_crf_in_the_file"));
						Object[] arguments = { f.getName() };
						msg.append(mf.format(arguments) + "<br/>");
						auditMsg.append(mf.format(arguments) + "<br/>");
						out.write(mf.format(arguments) + "<br/>");
						out.close();
						continue;
					}
				}

				if (eventCRFBeans.size() >= permittedEventCRFIds.size()) {
					msg.append(respage.getString("passed_event_crf_status_check") + "<br/>");
					auditMsg.append(respage.getString("passed_event_crf_status_check") + "<br/>");
				} else {
					fail = true;
					msg.append(respage.getString("the_event_crf_not_correct_status") + "<br/>");
					auditMsg.append(respage.getString("the_event_crf_not_correct_status") + "<br/>");
				}

				try {
					List<DisplayItemBeanWrapper> tempDisplayItemBeanWrappers = new ArrayList<DisplayItemBeanWrapper>();
					tempDisplayItemBeanWrappers = getImportCRFDataService(dataSource).lookupValidationErrors(
							validatorHelper, odmContainer, ub, totalValidationErrors, hardValidationErrors,
							permittedEventCRFIds);
					logger.debug("size of total validation errors: " + totalValidationErrors.size());
                    hasSkippedItems = validatorHelper.getAttribute("hasSkippedItems") != null;
					displayItemBeanWrappers.addAll(tempDisplayItemBeanWrappers);
				} catch (NullPointerException npe1) {
					// what if you have 2 event crfs but the third is a fake?
					npe1.printStackTrace();
					fail = true;
					logger.debug("threw a NPE after calling lookup validation errors");
					msg.append(respage.getString("an_error_was_thrown_while_validation_errors") + "<br/>");
					auditMsg.append(respage.getString("an_error_was_thrown_while_validation_errors") + "<br/>");
					out.write(respage.getString("an_error_was_thrown_while_validation_errors") + "<br/>");
					logger.debug("=== threw the null pointer, import ===");
				} catch (OpenClinicaException oce1) {
					fail = true;
					logger.error("threw an OCE after calling lookup validation errors " + oce1.getOpenClinicaMessage());
					msg.append(oce1.getOpenClinicaMessage() + "<br/>");
					out.write(oce1.getOpenClinicaMessage() + "<br/>");

				}
			} else {
				msg.append(respage.getString("no_event_crfs_matching_the_xml_metadata") + "<br/>");
				out.write(respage.getString("no_event_crfs_matching_the_xml_metadata") + "<br/>");
				out.close();
				continue;
			}

			ArrayList<SubjectDataBean> subjectData = odmContainer.getCrfDataPostImportContainer().getSubjectData();

			if (!hardValidationErrors.isEmpty()) {
				String messageHardVals = triggerService.generateHardValidationErrorMessage(subjectData,
						hardValidationErrors, false, false, respage);
				out.write(messageHardVals);
			} else {
				if (!totalValidationErrors.isEmpty()) {
					String totalValErrors = triggerService.generateHardValidationErrorMessage(subjectData,
							totalValidationErrors, false, false, respage);
					out.write(totalValErrors);
					// here we also append data to the file, tbh 06/2010
				}
				String validMsgs = triggerService.generateValidMessage(subjectData, totalValidationErrors,
                        hasSkippedItems, respage);
				out.write(validMsgs);
			}
			out.close();

			if (fail) {
				MessageFormat mf = new MessageFormat("");
				mf.applyPattern(respage.getString("problems_encountered_with_file"));
				Object[] arguments = { f.getName(), msg.toString() };
				msg.append(mf.format(arguments) + "<br/>");
				out.close();
				continue;
			} else {
				msg.append(respage.getString("passing_crf_edit_checks") + "<br/>");
				auditMsg.append(respage.getString("passing_crf_edit_checks") + "<br/>");
				logger.debug("found total validation errors: " + totalValidationErrors.size());
				SummaryStatsBean ssBean = getImportCRFDataService(dataSource).generateSummaryStatsBean(odmContainer,
						displayItemBeanWrappers);
				msg.append(triggerService.generateSummaryStatsMessage(ssBean, respage) + "<br/>");
				// setup ruleSets to run if applicable
				logger.debug("=== about to run rules ===");
				List<ImportDataRuleRunnerContainer> containers = this.ruleRunSetup(runRulesOptimisation,
						dataSource.getConnection(), dataSource, studyBean, ub, ruleSetService, odmContainer);
				Set<Integer> studyEventIds = new HashSet<Integer>();
				Set<Integer> skippedItemIds = new HashSet<Integer>();
				for (DisplayItemBeanWrapper wrapper : displayItemBeanWrappers) {
                    HashMap<Integer, EventCRFBean> idToEventCrfBeans = new HashMap<Integer, EventCRFBean>();
					logger.debug("right before we check to make sure it is savable: " + wrapper.isSavable());
					if (wrapper.isSavable()) {
						logger.debug("wrapper problems found : " + wrapper.getValidationErrors().toString());
						for (DisplayItemBean displayItemBean : wrapper.getDisplayItemBeans()) {
							EventCRFBean eventCrfBean = new EventCRFBean();
							ItemDataBean itemDataBean = new ItemDataBean();
							int eventCrfBeanId = displayItemBean.getData().getEventCRFId();
							if (idToEventCrfBeans.containsKey(eventCrfBeanId)) {
								eventCrfBean = idToEventCrfBeans.get(eventCrfBeanId);
							} else {
								eventCrfBean = (EventCRFBean) ecdao.findByPK(eventCrfBeanId);
								if (!displayItemBean.isSkip()) {
									idToEventCrfBeans.put(eventCrfBeanId, eventCrfBean);
								}
							}
                            logger.debug("found value here: " + displayItemBean.getData().getValue());
                            logger.debug("found status here: " + eventCrfBean.getStatus().getName());
							StudyEventBean studyEventBean = (StudyEventBean) sedao.findByPK(eventCrfBean
									.getStudyEventId());
							itemDataBean = itemDataDao.findByItemIdAndEventCRFIdAndOrdinal(displayItemBean.getItem()
									.getId(), eventCrfBean.getId(), displayItemBean.getData().getOrdinal());
							summary.processStudySubject(eventCrfBean.getStudySubjectId(), displayItemBean.isSkip());
							summary.processStudyEvent(studyEventBean.getId() + "_" + studyEventBean.getRepeatingNum(),
									displayItemBean.isSkip());
							summary.processItem(studyEventBean.getId() + "_" + studyEventBean.getRepeatingNum() + "_"
									+ displayItemBean.getItem().getId() + "_" + displayItemBean.getData().getOrdinal(),
									displayItemBean.isSkip());
                            if (!displayItemBean.isSkip()) {
								if (wrapper.isOverwrite() && itemDataBean.getStatus() != null) {
									logger.debug("just tried to find item data bean on item name "
											+ displayItemBean.getItem().getName());
									itemDataBean.setUpdatedDate(new Date());
									itemDataBean.setUpdater(ub);
									itemDataBean.setValue(displayItemBean.getData().getValue());
									// set status?
									itemDataDao.update(itemDataBean);
									logger.debug("updated: " + itemDataBean.getItemId());
									// need to set pk here in order to create dn
									displayItemBean.getData().setId(itemDataBean.getId());
								} else {
									itemDataBean = (ItemDataBean) itemDataDao.create(displayItemBean.getData());
									logger.debug("created: " + displayItemBean.getData().getItemId()
											+ "event CRF ID = " + eventCrfBean.getId() + "CRF VERSION ID ="
											+ eventCrfBean.getCRFVersionId());
									displayItemBean.getData().setId(itemDataBean.getId());
								}
								ItemDAO idao = new ItemDAO(dataSource);
								ItemBean ibean = (ItemBean) idao.findByPK(displayItemBean.getData().getItemId());
								logger.debug("*** checking for validation errors: " + ibean.getName());
								String itemOid = displayItemBean.getItem().getOid() + "_"
										+ wrapper.getStudyEventRepeatKey() + "_"
										+ displayItemBean.getData().getOrdinal() + "_" + wrapper.getStudySubjectOid();
								if (wrapper.getValidationErrors().containsKey(itemOid)) {
									ArrayList messageList = (ArrayList) wrapper.getValidationErrors().get(itemOid);
									for (int iter = 0; iter < messageList.size(); iter++) {
										String message = (String) messageList.get(iter);
										DiscrepancyNoteBean parentDn = createDiscrepancyNote(ibean, message,
												eventCrfBean, displayItemBean, null, ub, dataSource, studyBean);
										createDiscrepancyNote(ibean, message, eventCrfBean, displayItemBean,
												parentDn.getId(), ub, dataSource, studyBean);
										logger.debug("*** created disc note with message: " + message);
									}
								}
                            } else {
								skippedItemIds.add(displayItemBean.getItem().getId());
                                skippedItemsSql.append("INSERT INTO audit_log_event(audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id) " +
                                        "VALUES (35, now(), " + ub.getId() + ", 'item_data', " + itemDataBean.getId() + ", '" + displayItemBean.getItem().getName() + "', '" + itemDataBean.getValue() + "', '" + displayItemBean.getData().getValue() + "', " + displayItemBean.getData().getEventCRFId() + "); ");
                            }
						}

						for (EventCRFBean eventCrfBean : idToEventCrfBeans.values()) {
							studyEventIds.add(eventCrfBean.getStudyEventId());

							eventCrfBean.setSdvStatus(false);
							eventCrfBean.setNotStarted(false);
							eventCrfBean.setStatus(Status.AVAILABLE);
							if (studyBean.getStudyParameterConfig().getMarkImportedCRFAsCompleted()
									.equalsIgnoreCase("yes")) {
								EventDefinitionCRFBean edcb = edcdao.findByStudyEventIdAndCRFVersionId(studyBean,
										eventCrfBean.getStudyEventId(), eventCrfBean.getCRFVersionId());

								eventCrfBean.setUpdaterId(ub.getId());
								eventCrfBean.setUpdater(ub);
								eventCrfBean.setUpdatedDate(new Date());
								eventCrfBean.setDateCompleted(new Date());
								eventCrfBean.setDateValidateCompleted(new Date());
								eventCrfBean.setStatus(Status.UNAVAILABLE);
								eventCrfBean.setStage(edcb.isDoubleEntry() ? DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE
										: DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
								itemDataDao.updateStatusByEventCRF(eventCrfBean, Status.UNAVAILABLE);
							}

							ecdao.update(eventCrfBean);
						}

						for (int studyEventId : studyEventIds) {
							if (studyEventId > 0) {
								StudyEventBean seb = (StudyEventBean) sedao.findByPK(studyEventId);

								seb.setUpdatedDate(new Date());
								seb.setUpdater(ub);

								sedao.update(seb);
							}
						}                       
					}
				}

				for (int studyEventId : studyEventIds) {
					if (studyEventId > 0) {
						StudyEventBean seb = (StudyEventBean) sedao.findByPK(studyEventId);
						StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPK(seb.getStudySubjectId());
						StudyBean sb = (StudyBean) sdao.findByPK(ssb.getStudyId());

						SubjectEventStatusUtil.determineSubjectEventState(seb, sb, new DAOWrapper(sdao, sedao, ssdao,
								ecdao, edcdao, dndao));
						
						seb.setUpdatedDate(new Date());
						seb.setUpdater(ub);

						sedao.update(seb);
					}
				}

				msg.append(respage.getString("data_has_been_successfully_import") + "<br/>");
				auditMsg.append(respage.getString("data_has_been_successfully_import") + "<br/>");

				msg.append(summary.prepareSummaryMessage(studyBean, resword));
				auditMsg.append(summary.prepareSummaryMessage(studyBean, resword));
                
				if (hasSkippedItems) {
					String additionalMsg = "<br/>"
							+ (studyBean.getParentStudyId() > 0 ? respage.getString("site") : respage
									.getString("study")) + " '" + studyBean.getName() + "' "
							+ respage.getString("import_job_msg") + "  <br/>";
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

				auditMsg.append(this.runRules(runRulesOptimisation, dataSource.getConnection(), skippedItemIds,
						studyBean, ub, containers, ruleSetService, ExecutionMode.SAVE));
			}
		}// end for loop
			// is the writer still not closed? try to close it

		ArrayList<String> retList = new ArrayList<String>();
		retList.add(msg.toString());
		retList.add(auditMsg.toString());
        retList.add(skippedItemsSql.toString());
		return retList;

	}

	public static DiscrepancyNoteBean createDiscrepancyNote(ItemBean itemBean, String message,
			EventCRFBean eventCrfBean, DisplayItemBean displayItemBean, Integer parentId, UserAccountBean uab,
			DataSource ds, StudyBean study) {
		DiscrepancyNoteBean note = new DiscrepancyNoteBean();
		StudySubjectDAO ssdao = new StudySubjectDAO(ds);
		note.setDescription(message);
		note.setDetailedNotes("Failed Validation Check");
		note.setOwner(uab);
		note.setCreatedDate(new Date());
		note.setResolutionStatusId(ResolutionStatus.OPEN.getId());
		note.setDiscrepancyNoteTypeId(DiscrepancyNoteType.FAILEDVAL.getId());
		if (parentId != null) {
			note.setParentDnId(parentId);
		}

		note.setField(itemBean.getName());
		note.setStudyId(study.getId());
		note.setEntityName(itemBean.getName());
		note.setEntityType("ItemData");
		note.setEntityValue(displayItemBean.getData().getValue());

		note.setEventName(eventCrfBean.getName());
		note.setEventStart(eventCrfBean.getCreatedDate());
		note.setCrfName(displayItemBean.getEventDefinitionCRF().getCrfName());

		StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(eventCrfBean.getStudySubjectId());
		note.setSubjectName(ss.getName());

		note.setEntityId(displayItemBean.getData().getId());
		note.setColumn("value");

		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(ds);
		note = (DiscrepancyNoteBean) dndao.create(note);
		dndao.createMapping(note);
		return note;
	}

	private void cutAndPaste(File[] tar, File[] dest) throws IOException {
		for (int j = 0; j < tar.length; j++) {
			try {
				java.io.InputStream in = new FileInputStream(tar[j]);
				java.io.OutputStream out = new FileOutputStream(dest[j]);

				byte[] buf = new byte[1024];
				int len = 0;

				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				tar[j].delete();
			} catch (NullPointerException npe) {
				// list can be 'gappy' which is why we need to catch this
				logger.error("found Npe: " + npe.getMessage());
			}
		}
	}

	@Transactional
	private List<ImportDataRuleRunnerContainer> ruleRunSetup(Boolean runRulesOptimisation, Connection connection,
			DataSource dataSource, StudyBean studyBean, UserAccountBean userBean,
			RuleSetServiceInterface ruleSetService, ODMContainer odmContainer) {
		List<ImportDataRuleRunnerContainer> containers = new ArrayList<ImportDataRuleRunnerContainer>();
		if (odmContainer != null) {
			ArrayList<SubjectDataBean> subjectDataBeans = odmContainer.getCrfDataPostImportContainer().getSubjectData();
			logger.debug("=== found number of rules present: " + ruleSetService.getCountByStudy(studyBean) + " ===");
			if (ruleSetService.getCountByStudy(studyBean) > 0) {
				ImportDataRuleRunnerContainer container;
				for (SubjectDataBean subjectDataBean : subjectDataBeans) {
					container = new ImportDataRuleRunnerContainer();
					container.initRuleSetsAndTargets(dataSource, studyBean, subjectDataBean, ruleSetService);
					logger.debug("=== found container: should run rules? " + container.getShouldRunRules() + " ===");
					if (container.getShouldRunRules()) {
						logger.debug("=== added a container in run rule setup ===");
						containers.add(container);
					}
				}
				if (containers != null && !containers.isEmpty()) {
					logger.debug("=== dry run of rules in data entry ===");
					ruleSetService.runRulesInImportData(runRulesOptimisation, connection, containers, studyBean,
							userBean, ExecutionMode.DRY_RUN);
				}

			}
		}
		return containers;
	}

	@Transactional
	private StringBuffer runRules(Boolean runRulesOptimisation, Connection connection, Set<Integer> skippedItemIds, StudyBean studyBean,
			UserAccountBean userBean, List<ImportDataRuleRunnerContainer> containers,
			RuleSetServiceInterface ruleSetService, ExecutionMode executionMode) {
		StringBuffer messages = new StringBuffer();
		if (containers != null && !containers.isEmpty()) {
			logger.debug("=== real running of rules in import data ===");
			HashMap<String, ArrayList<String>> summary = ruleSetService.runRulesInImportData(runRulesOptimisation,
					connection, containers, skippedItemIds, studyBean, userBean, executionMode);
			messages = extractRuleActionWarnings(summary);
		}
		return messages;
	}

	private StringBuffer extractRuleActionWarnings(HashMap<String, ArrayList<String>> summaryMap) {
		StringBuffer messages = new StringBuffer();
		if (summaryMap != null && !summaryMap.isEmpty()) {
			for (String key : summaryMap.keySet()) {
				messages.append(key);
				messages.append(" : ");
				messages.append(StringUtils.join(summaryMap.get(key), ", "));
			}
		}
		return messages;
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
