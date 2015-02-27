/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.extract.ExtractUtils;
import org.akaza.openclinica.service.extract.XsltTriggerService;
import org.akaza.openclinica.web.SQLInitServlet;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.clinovo.util.SessionUtil;

/**
 * ExtractController.
 */
@Controller("extractController")
@RequestMapping("/extract")
public class ExtractController {

	public static final String SCHEDULER = "schedulerFactoryBean";

	@Autowired
	private BasicDataSource dataSource;

	private StdScheduler scheduler;

	public static final String TRIGGER_GROUP_NAME = "XsltTriggers";

	public ExtractController() {

	}

	/**
	 * Process the page from whence you came, i.e. extract a dataset.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param id
	 *            String
	 * @param datasetId
	 *            String
	 * @return ModelMap
	 * @throws IOException
	 *             the IOException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelMap processSubmit(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") String id, @RequestParam("datasetId") String datasetId) throws IOException {
		ModelMap map = new ModelMap();
		ResourceBundleProvider.updateLocale(SessionUtil.getLocale(request));
		// String datasetId = (String)request.getAttribute("datasetId");
		// String id = (String)request.getAttribute("id");
		System.out.println("found both id " + id + " and dataset " + datasetId);
		ExtractUtils extractUtils = new ExtractUtils();
		// get extract id
		// get dataset id
		// if id is a number and dataset id is a number ...
		DatasetDAO datasetDao = new DatasetDAO(dataSource);
		UserAccountBean userBean = (UserAccountBean) request.getSession().getAttribute("userBean");
		CoreResources cr = new CoreResources();

		ExtractPropertyBean epBean = cr.findExtractPropertyBeanById(Integer.parseInt(id), datasetId);

		DatasetBean dsBean = (DatasetBean) datasetDao.findByPK(Integer.parseInt(datasetId));
		// set the job in motion
		String[] files = epBean.getFileName();
		String exportFileName;
		int cnt = 0;
		JobDetailImpl jobDetailBean;
		SimpleTriggerImpl simpleTrigger;

		dsBean.setName(dsBean.getName().replaceAll(" ", "_"));
		String[] exportFiles = epBean.getExportFileName();
		String pattern = "yyyy" + File.separator + "MM" + File.separator + "dd" + File.separator + "HHmmssSSS"
				+ File.separator;
		SimpleDateFormat sdfDir = new SimpleDateFormat(pattern);
		int i = 0;
		String[] temp = new String[exportFiles.length];
		// JN: The following logic is for comma separated variables, to avoid
		// the second file be treated as a old file
		// and deleted.
		while (i < exportFiles.length) {
			temp[i] = resolveVars(exportFiles[i], dsBean, sdfDir, SQLInitServlet.getField("filePath"), extractUtils);
			i++;
		}
		epBean.setDoNotDelFiles(temp);
		epBean.setExportFileName(temp);
		scheduler = getScheduler(request);
		// while(cnt < fileSize)

		XsltTriggerService xsltService = new XsltTriggerService();

		String generalFileDir = SQLInitServlet.getField("filePath");

		generalFileDir = generalFileDir + "datasets" + File.separator + dsBean.getId() + File.separator
				+ sdfDir.format(new java.util.Date());

		exportFileName = epBean.getExportFileName()[cnt];

		// need to set the dataset path here, tbh
		System.out.println("found odm xml file path " + generalFileDir);
		// next, can already run jobs, translations, and then add a message
		// to be notified later
		// JN all the properties need to have the variables...
		String xsltPath = SQLInitServlet.getField("filePath") + "xslt" + File.separator + files[cnt];
		String endFilePath = epBean.getFileLocation();
		endFilePath = getEndFilePath(endFilePath, dsBean, sdfDir, SQLInitServlet.getField("filePath"), extractUtils);
		// exportFileName = resolveVars(exportFileName,dsBean,sdfDir);
		if (epBean.getPostProcExportName() != null) {
			// String preProcExportPathName =
			// getEndFilePath(epBean.getPostProcExportName(),dsBean,sdfDir);
			String preProcExportPathName = resolveVars(epBean.getPostProcExportName(), dsBean, sdfDir,
					SQLInitServlet.getField("filePath"), extractUtils);
			epBean.setPostProcExportName(preProcExportPathName);
		}
		if (epBean.getPostProcLocation() != null) {
			String prePocLoc = getEndFilePath(epBean.getPostProcLocation(), dsBean, sdfDir,
					SQLInitServlet.getField("filePath"), extractUtils);
			epBean.setPostProcLocation(prePocLoc);
		}
		setAllProps(epBean, dsBean, sdfDir, extractUtils);
		// also need to add the status fields discussed w/ cc:
		// result code, user message, optional URL, archive message, log
		// file message
		// asdf table: sort most recent at top
		System.out.println("found xslt file name " + xsltPath);

		StudyBean studyBean = (StudyBean) new StudyDAO(dataSource).findByPK(userBean.getActiveStudyId());
		if (studyBean.getParentStudyId() > 0) {
			studyBean = (StudyBean) new StudyDAO(dataSource).findByPK(studyBean.getParentStudyId());
		}

		// String xmlFilePath = generalFileDir + ODMXMLFileName;
		simpleTrigger = xsltService.generateXsltTrigger(xsltPath, studyBean, endFilePath + File.separator,
				exportFileName, dsBean.getId(), epBean, userBean, SessionUtil.getLocale(request).getLanguage(), cnt,
				SQLInitServlet.getField("filePath") + "xslt", ExtractController.TRIGGER_GROUP_NAME);

		String jobName = simpleTrigger.getName() + System.currentTimeMillis();
		simpleTrigger.setJobName(jobName);

		jobDetailBean = new JobDetailImpl();
		jobDetailBean.setGroup(ExtractController.TRIGGER_GROUP_NAME);
		jobDetailBean.setName(jobName);
		jobDetailBean.setJobClass(org.akaza.openclinica.job.XsltStatefulJob.class);
		jobDetailBean.setJobDataMap(simpleTrigger.getJobDataMap());
		jobDetailBean.setDurability(true); // need durability? YES - we will
											// want to see if it's finished
		// jobDetailBean.setVolatility(false);

		try {
			Date dateStart = scheduler.scheduleJob(jobDetailBean, simpleTrigger);
			System.out.println("== found job date: " + dateStart.toString());

		} catch (SchedulerException se) {
			se.printStackTrace();
		}

		request.setAttribute("datasetId", datasetId);
		// set the job name here in the user's session, so that we can ping the
		// scheduler to pull it out later
		request.getSession().setAttribute("jobName", jobDetailBean.getName());
		request.getSession().setAttribute("groupName", ExtractController.TRIGGER_GROUP_NAME);

		request.getSession().setAttribute("datasetId", dsBean.getId());

		request.getSession().setAttribute("exportStatus", "ongoing");
		response.sendRedirect(request.getContextPath() + "/ViewDatasets");
		return map;
	}

	private ExtractPropertyBean setAllProps(ExtractPropertyBean epBean, DatasetBean dsBean, SimpleDateFormat sdfDir,
			ExtractUtils extractUtils) {

		return extractUtils.setAllProps(epBean, dsBean, sdfDir, SQLInitServlet.getField("filePath"));

	}

	private String getEndFilePath(String endFilePath, DatasetBean dsBean, SimpleDateFormat sdfDir, String filePath,
			ExtractUtils extractUtils) {
		return extractUtils.getEndFilePath(endFilePath, dsBean, sdfDir, filePath);
	}

	private String resolveVars(String endFilePath, DatasetBean dsBean, SimpleDateFormat sdfDir, String filePath,
			ExtractUtils extractUtils) {
		return extractUtils.resolveVars(endFilePath, dsBean, sdfDir, filePath);

	}

	private StdScheduler getScheduler(HttpServletRequest request) {
		scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(
				request.getSession().getServletContext()).getBean(SCHEDULER);
		return scheduler;
	}

	/**
	 * Method handles exception.
	 * 
	 * @param ex
	 *            Exception
	 * @return String
	 */
	@ExceptionHandler(Exception.class)
	public String handleException(Exception ex) {
		ex.printStackTrace();
		return "redirect:/MainMenu";
	}

}
