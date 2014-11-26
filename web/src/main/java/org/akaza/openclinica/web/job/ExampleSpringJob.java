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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.ExtractBean;
import org.akaza.openclinica.bean.extract.SPSSReportBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.core.OpenClinicaMailSender;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.extract.GenerateExtractFileService;
import org.akaza.openclinica.web.SQLInitServlet;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

@SuppressWarnings({ "rawtypes" })
public class ExampleSpringJob extends QuartzJobBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	// variables to pull out
	public static final String PERIOD = "periodToRun";
	public static final String TAB = "tab";
	public static final String CDISC = "cdisc";
	public static final String CDISC12 = "cdisc12";
	public static final String CDISC13 = "cdisc13";
	public static final String CDISC13OC = "cdisc13oc";
	public static final String SPSS = "spss";
	public static final String DATASET_ID = "dsId";
	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";
	public static final String STUDY_NAME = "study_name";
	public static final String STUDY_ID = "studyId";
	public static final String LOCALE = "locale";

	private static final String DATASET_DIR = SQLInitServlet.getField("filePath") + "datasets" + File.separator;

	private OpenClinicaMailSender mailSender;
	private DataSource dataSource;
	private GenerateExtractFileService generateFileService;
	private UserAccountBean userBean;
	private CoreResources coreResources;
	private RuleSetRuleDao ruleSetRuleDao;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		// need to generate a Locale so that user beans and other things will
		// generate normally
		Locale locale = new Locale("en-US");
		ResourceBundleProvider.updateLocale(locale);
		ResourceBundle pageMessages = ResourceBundleProvider.getPageMessagesBundle();
		JobDataMap dataMap = context.getMergedJobDataMap();
		SimpleTriggerImpl trigger = (SimpleTriggerImpl) context.getTrigger();
		try {
			ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext()
					.get("applicationContext");
			String studySubjectNumber = CoreResources.getField("extract.number");
			coreResources = (CoreResources) appContext.getBean("coreResources");
			ruleSetRuleDao = (RuleSetRuleDao) appContext.getBean("ruleSetRuleDao");
			dataSource = (DataSource) appContext.getBean("dataSource");
			mailSender = (OpenClinicaMailSender) appContext.getBean("openClinicaMailSender");
			AuditEventDAO auditEventDAO = new AuditEventDAO(dataSource);
			String alertEmail = dataMap.getString(EMAIL);
			String localeStr = dataMap.getString(LOCALE);
			if (localeStr != null) {
				locale = new Locale(localeStr);
				ResourceBundleProvider.updateLocale(locale);
				pageMessages = ResourceBundleProvider.getPageMessagesBundle();

			}
			int dsId = dataMap.getInt(DATASET_ID);
			String tab = dataMap.getString(TAB);
			String cdisc = dataMap.getString(CDISC);
			String cdisc12 = dataMap.getString(CDISC12);
			if (cdisc12 == null) {
				cdisc12 = "0";
			}
			String cdisc13 = dataMap.getString(CDISC13);
			if (cdisc13 == null) {
				cdisc13 = "0";
			}
			String cdisc13oc = dataMap.getString(CDISC13OC);
			if (cdisc13oc == null) {
				cdisc13oc = "0";
			}
			String spss = dataMap.getString(SPSS);
			int userId = dataMap.getInt(USER_ID);
			int studyId = dataMap.getInt(STUDY_ID);

			logger.debug("-- found the job: " + dsId + " dataset id");

			HashMap fileName = new HashMap<String, Integer>();
			if (dsId > 0) {
				// trying to not throw an error if there's no dataset id
				DatasetDAO dsdao = new DatasetDAO(dataSource);
				DatasetBean datasetBean = (DatasetBean) dsdao.findByPK(dsId);
				StudyDAO studyDao = new StudyDAO(dataSource);
				UserAccountDAO userAccountDAO = new UserAccountDAO(dataSource);
				// hmm, three lines in the if block DRY?
				String generalFileDir = "";
				String generalFileDirCopy = "";
				String exportFilePath = SQLInitServlet.getField("exportFilePath");
				String pattern = "yyyy" + File.separator + "MM" + File.separator + "dd" + File.separator + "HHmmssSSS"
						+ File.separator;
				SimpleDateFormat sdfDir = new SimpleDateFormat(pattern);
				generalFileDir = DATASET_DIR + datasetBean.getId() + File.separator
						+ sdfDir.format(new java.util.Date());
				if (!"".equals(exportFilePath)) {
					generalFileDirCopy = SQLInitServlet.getField("filePath") + exportFilePath + File.separator;
				}
				// logger.debug("-- created the following dir: " +
				// generalFileDir);
				long sysTimeBegin = System.currentTimeMillis();
				// set up the user bean here, tbh
				// logger.debug("-- gen tab file 00");

				userBean = (UserAccountBean) userAccountDAO.findByPK(userId);
				// needs to also be captured by the servlet, tbh
				// logger.debug("-- gen tab file 00");
				generateFileService = new GenerateExtractFileService(dataSource, userBean, coreResources,
						ruleSetRuleDao);

				// logger.debug("-- gen tab file 00");

				// tbh #5796 - covers a bug when the user changes studies,
				// 10/2010
				StudyBean activeStudy = (StudyBean) studyDao.findByPK(studyId);
				StudyBean parentStudy = new StudyBean();
				logger.debug("active study: " + studyId + " parent study: " + activeStudy.getParentStudyId());
				if (activeStudy.getParentStudyId() > 0) {
					parentStudy = (StudyBean) studyDao.findByPK(activeStudy.getParentStudyId());
				} else {
					parentStudy = activeStudy;
				}

				logger.debug("-- found extract bean ");

				ExtractBean eb = generateFileService.generateExtractBean(datasetBean, activeStudy, parentStudy);
				StringBuffer message = new StringBuffer();
				StringBuffer auditMessage = new StringBuffer();
				message.append("<p>" + pageMessages.getString("email_header_1") + " "
						+ pageMessages.getString("email_header_2") + " Job Execution "
						+ pageMessages.getString("email_header_3") + "</p>");
				message.append("<P>Dataset: " + datasetBean.getName() + "</P>");
				message.append("<P>Study: " + activeStudy.getName() + "</P>");
				message.append("<p>" + pageMessages.getString("html_email_body_1") + datasetBean.getName()
						+ pageMessages.getString("html_email_body_2") + SQLInitServlet.getSystemURL()
						+ pageMessages.getString("html_email_body_3") + "</p>");
				if ("1".equals(tab)) {

					logger.debug("-- gen tab file 01");
					fileName = generateFileService.createTabFile(eb, sysTimeBegin, generalFileDir, datasetBean,
							activeStudy.getId(), parentStudy.getId(), generalFileDirCopy);
					message.append("<p>" + pageMessages.getString("html_email_body_4") + " " + getFileNameStr(fileName)
							+ pageMessages.getString("html_email_body_4_5") + SQLInitServlet.getSystemURL()
							+ "AccessFile?fileId=" + getFileIdInt(fileName)
							+ pageMessages.getString("html_email_body_3") + "</p>");
					auditMessage.append(pageMessages.getString("you_can_access_tab_delimited") + getFileIdInt(fileName)
							+ pageMessages.getString("access_end"));
				}

				if ("1".equals(cdisc)) {
					String odmVersion = "oc1.2";
					fileName = generateFileService.createODMFile(odmVersion, sysTimeBegin, generalFileDir, datasetBean,
							activeStudy, generalFileDirCopy, eb, activeStudy.getId(), parentStudy.getId(),
							studySubjectNumber, true, true, true, false, null);
					logger.debug("-- gen odm file");
					message.append("<p>" + pageMessages.getString("html_email_body_4") + " " + getFileNameStr(fileName)
							+ pageMessages.getString("html_email_body_4_5") + SQLInitServlet.getSystemURL()
							+ "AccessFile?fileId=" + getFileIdInt(fileName)
							+ pageMessages.getString("html_email_body_3") + "</p>");

					auditMessage.append(pageMessages.getString("you_can_access_odm_12") + getFileIdInt(fileName)
							+ pageMessages.getString("access_end"));
				}

				if ("1".equals(cdisc12)) {
					String odmVersion = "1.2";
					fileName = generateFileService.createODMFile(odmVersion, sysTimeBegin, generalFileDir, datasetBean,
							activeStudy, generalFileDirCopy, eb, activeStudy.getId(), parentStudy.getId(),
							studySubjectNumber, true, true, true, false, null);
					logger.debug("-- gen odm file 1.2 default");
					message.append("<p>" + pageMessages.getString("html_email_body_4") + " " + getFileNameStr(fileName)
							+ pageMessages.getString("html_email_body_4_5") + SQLInitServlet.getSystemURL()
							+ "AccessFile?fileId=" + getFileIdInt(fileName)
							+ pageMessages.getString("html_email_body_3") + "</p>");

					auditMessage.append(pageMessages.getString("you_can_access_odm_12_xml") + getFileIdInt(fileName)
							+ pageMessages.getString("access_end"));
				}

				if ("1".equals(cdisc13)) {
					String odmVersion = "1.3";
					fileName = generateFileService.createODMFile(odmVersion, sysTimeBegin, generalFileDir, datasetBean,
							activeStudy, generalFileDirCopy, eb, activeStudy.getId(), parentStudy.getId(),
							studySubjectNumber, true, true, true, false, null);
					logger.debug("-- gen odm file 1.3");
					message.append("<p>" + pageMessages.getString("html_email_body_4") + " " + getFileNameStr(fileName)
							+ pageMessages.getString("html_email_body_4_5") + SQLInitServlet.getSystemURL()
							+ "AccessFile?fileId=" + getFileIdInt(fileName)
							+ pageMessages.getString("html_email_body_3") + "</p>");

					auditMessage.append(pageMessages.getString("you_can_access_odm_13") + getFileIdInt(fileName)
							+ pageMessages.getString("access_end"));
				}

				if ("1".equals(cdisc13oc)) {
					String odmVersion = "oc1.3";
					fileName = generateFileService.createODMFile(odmVersion, sysTimeBegin, generalFileDir, datasetBean,
							activeStudy, generalFileDirCopy, eb, activeStudy.getId(), parentStudy.getId(),
							studySubjectNumber, true, true, true, false, null);
					logger.debug("-- gen odm file 1.3 oc");
					message.append("<p>" + pageMessages.getString("html_email_body_4") + " " + getFileNameStr(fileName)
							+ pageMessages.getString("html_email_body_4_5") + SQLInitServlet.getSystemURL()
							+ "AccessFile?fileId=" + getFileIdInt(fileName)
							+ pageMessages.getString("html_email_body_3") + "</p>");

					auditMessage.append(pageMessages.getString("you_can_access_odm_13_xml") + getFileIdInt(fileName)
							+ pageMessages.getString("access_end"));
				}
				if ("1".equals(spss)) {
					SPSSReportBean answer = new SPSSReportBean();
					fileName = generateFileService.createSPSSFile(datasetBean, eb, activeStudy, parentStudy,
							sysTimeBegin, generalFileDir, answer, generalFileDirCopy);
					logger.debug("-- gen spss file");
					message.append("<p>" + pageMessages.getString("html_email_body_4") + " " + getFileNameStr(fileName)
							+ pageMessages.getString("html_email_body_4_5") + SQLInitServlet.getSystemURL()
							+ "AccessFile?fileId=" + getFileIdInt(fileName)
							+ pageMessages.getString("html_email_body_3") + "</p>");

					auditMessage.append(pageMessages.getString("you_can_access_spss") + getFileIdInt(fileName)
							+ pageMessages.getString("access_end"));
				}

				// wrap up the message, and send the email
				message.append("<p>" + pageMessages.getString("html_email_body_5") + "</P><P>"
						+ pageMessages.getString("email_footer"));
				try {
					mailSender.sendEmail(alertEmail.trim(),
							pageMessages.getString("job_ran_for") + " " + datasetBean.getName(), message.toString(),
							true);
				} catch (OpenClinicaSystemException ose) {
					// Do Nothing, In the future we might want to have an email
					// status added to system.
				}
				TriggerBean triggerBean = new TriggerBean();
				triggerBean.setDataset(datasetBean);
				triggerBean.setUserAccount(userBean);
				triggerBean.setFullName(trigger.getName());
				triggerBean.setFiredDate(trigger.getStartTime());
				auditEventDAO.createRowForExtractDataJobSuccess(triggerBean, auditMessage.toString());
			} else {
				TriggerBean triggerBean = new TriggerBean();
				triggerBean.setUserAccount(userBean);
				triggerBean.setFullName(trigger.getName());
				triggerBean.setFiredDate(trigger.getStartTime());
				auditEventDAO.createRowForExtractDataJobFailure(triggerBean);
			}

		} catch (Exception e) {
			logger.debug("-- found exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String getFileNameStr(HashMap fileName) {
		String fileNameStr = "";
		for (Iterator it = fileName.entrySet().iterator(); it.hasNext();) {
			java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
			Object key = entry.getKey();
			fileNameStr = (String) key;
		}
		return fileNameStr;
	}

	private int getFileIdInt(HashMap fileName) {
		Integer fileID = new Integer(0);
		for (Iterator it = fileName.entrySet().iterator(); it.hasNext();) {
			java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
			Object value = entry.getValue();
			fileID = (Integer) value;
		}
		return fileID.intValue();
	}
}
