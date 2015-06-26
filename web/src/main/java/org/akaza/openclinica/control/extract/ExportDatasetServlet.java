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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.extract;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clinovo.util.DateUtil;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.extract.CommaReportBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.ExportFormatBean;
import org.akaza.openclinica.bean.extract.ExtractBean;
import org.akaza.openclinica.bean.extract.SPSSReportBean;
import org.akaza.openclinica.bean.extract.TabReportBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.service.extract.GenerateExtractFileService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.bean.ArchivedDatasetFileRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.job.XalanTriggerService;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.stereotype.Component;

import com.clinovo.i18n.LocaleResolver;

/**
 * Take a dataset and show it in different formats,<BR/>
 * Detect whether or not files exist in the system or database,<BR/>
 * Give the user the option of showing a stored dataset, or refresh the current one.
 *
 * retrieved at a later time.
 * 
 * @author thickerson
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class ExportDatasetServlet extends Controller {

	/**
	 * Get link for this servlet.
	 * @param dsId dataset ID.
	 * @return link
	 */
	public static String getLink(int dsId) {
		return "ExportDataset?datasetId=" + dsId;
	}

	private static final String DATASET_DIR = SQLInitServlet.getField("filePath") + "datasets" + File.separator;

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		DatasetDAO dsdao = getDatasetDAO();
		ArchivedDatasetFileDAO asdfdao = getArchivedDatasetFileDAO();
		FormProcessor fp = new FormProcessor(request);

		Locale locale = LocaleResolver.getLocale(request);
		GenerateExtractFileService generateFileService = new GenerateExtractFileService(getDataSource(), locale, ub,
				getCoreResources(), getRuleSetRuleDao());
		String action = fp.getString("action");
		int datasetId = fp.getInt("datasetId");
		int adfId = fp.getInt("adfId");
		if (datasetId == 0) {
			try {
				DatasetBean dsb = (DatasetBean) request.getSession().getAttribute("newDataset");
				datasetId = dsb.getId();
				logger.info("dataset id was zero, trying session: " + datasetId);
			} catch (NullPointerException e) {

				e.printStackTrace();
				logger.info("tripped over null pointer exception");
			}
		}
		DatasetBean db = getDatasetService().initialDatasetData(datasetId, ub);
		StudyDAO sdao = getStudyDAO();
		StudyBean study = (StudyBean) sdao.findByPK(db.getStudyId());
		checkRoleByUserAndStudy(request, response, ub, study.getParentStudyId(), study.getId());

		if (db.isContainsMaskedCRFs()) {
			addPageMessage(resword.getString("this_dataset_contains_items_from_masked_crfs_extract"), request);
			forwardPage(Page.VIEW_DATASETS_SERVLET, request, response);
			return;
		}

		// Checks if the study is current study or child of current study
		if (study.getId() != currentStudy.getId() && study.getParentStudyId() != currentStudy.getId()) {
			addPageMessage(
					respage.getString("no_have_correct_privilege_current_study") + " "
							+ respage.getString("change_active_study_or_contact"), request);
			forwardPage(Page.MENU_SERVLET, request, response);
			return;
		}

		int currentstudyid = currentStudy.getId();

		StudyBean parentStudy = new StudyBean();
		if (currentStudy.getParentStudyId() > 0) {
			parentStudy = (StudyBean) sdao.findByPK(currentStudy.getParentStudyId());
		}

		ExtractBean eb = generateFileService.generateExtractBean(db, currentStudy, parentStudy);

		if (StringUtil.isBlank(action)) {
			loadList(db, asdfdao, datasetId, fp, eb);
			forwardPage(Page.EXPORT_DATASETS, request, response);
		} else if ("delete".equalsIgnoreCase(action) && adfId > 0) {
			boolean success;
			ArchivedDatasetFileBean adfBean = (ArchivedDatasetFileBean) asdfdao.findByPK(adfId);
			if (adfBean.getId() > 0) {
				File file = new File(adfBean.getFileReference());
				if (!file.canWrite()) {
					addPageMessage(respage.getString("write_protected"), request);
				} else {
					success = file.delete();
					if (success) {
						asdfdao.deleteArchiveDataset(adfBean);
						addPageMessage(respage.getString("file_removed"), request);
					} else {
						addPageMessage(respage.getString("error_removing_file"), request);
					}
				}
				loadList(db, asdfdao, datasetId, fp, eb);
			}
			response.sendRedirect(request.getContextPath() + "/ExportDataset?datasetId=" + datasetId);
		} else {
			logger.info("**** found action ****: " + action);
			String generateReport = "";
			String pattern = "yyyy" + File.separator + "MM" + File.separator + "dd" + File.separator + "HHmmssSSS"
					+ File.separator;
			SimpleDateFormat sdfDir = new SimpleDateFormat(pattern);
			String generalFileDir = DATASET_DIR + db.getId() + File.separator + sdfDir.format(new java.util.Date());
			db.setName(db.getName().replaceAll(" ", "_"));
			Page finalTarget;
			finalTarget = Page.EXPORT_DATA_CUSTOM;

			// now display report according to format specified

			// TODO revise final target to set to fileReference????
			long sysTimeBegin = System.currentTimeMillis();
			int fId = 0;
			if ("sas".equalsIgnoreCase(action)) {
				long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
				String sasFileName = db.getName() + "_sas.sas";
				generateFileService.createFile(sasFileName, generalFileDir, generateReport, db, sysTimeEnd,
						ExportFormatBean.TXTFILE, true);
				logger.info("created sas file");
				request.setAttribute("generate", generalFileDir + sasFileName);
				finalTarget.setFileName(generalFileDir + sasFileName);
			} else if ("odm".equalsIgnoreCase(action)) {
				String odmVersion = fp.getString("odmVersion");
				String odmXMLFileName = "";
				HashMap answerMap = generateFileService.createODMFile(odmVersion, sysTimeBegin, generalFileDir, db,
						currentStudy, "", eb, currentStudy.getId(), currentStudy.getParentStudyId(), "99", true, true,
						true, false, null);

				for (Object o : answerMap.entrySet()) {
					Map.Entry entry = (Map.Entry) o;
					Object key = entry.getKey();
					Object value = entry.getValue();
					odmXMLFileName = (String) key;
					fId = (Integer) value;
				}
				request.setAttribute("generate", generalFileDir + odmXMLFileName);
				System.out.println("+++ set the following: " + generalFileDir + odmXMLFileName);
				// Working group
				// put an extra flag here, where we generate the XML, and then
				// find the XSL, run a job and
				// send a link with the SQL file? put the generated SQL file
				// with the dataset?
				if (fp.getString("xalan") != null) {
					XalanTriggerService xts = new XalanTriggerService();

					String propertiesPath = SQLInitServlet.getField("filePath");

					// the trick there, we need to open up the zipped file and
					// get at the XML
					openZipFile(generalFileDir + odmXMLFileName + ".zip");
					// need to find out how to copy this xml file from /bin to
					// the generalFileDir
					SimpleTriggerImpl simpleTrigger = xts.generateXalanTrigger(propertiesPath + File.separator
							+ "ODMReportStylesheet.xsl", odmXMLFileName, generalFileDir + "output.sql", db.getId());
					StdScheduler scheduler = getStdScheduler();

					JobDetailImpl jobDetailBean = new JobDetailImpl();
					jobDetailBean.setGroup(XalanTriggerService.TRIGGER_GROUP_NAME);
					jobDetailBean.setName(simpleTrigger.getName());
					jobDetailBean.setJobClass(org.akaza.openclinica.web.job.XalanStatefulJob.class);
					jobDetailBean.setJobDataMap(simpleTrigger.getJobDataMap());
					jobDetailBean.setDurability(true); // need durability?
					// jobDetailBean.setVolatility(false);

					try {
						Date dateStart = scheduler.scheduleJob(jobDetailBean, simpleTrigger);
						logger.info("== found job date: " + dateStart.toString());
					} catch (SchedulerException se) {
						se.printStackTrace();
					}
				}
			} else if ("txt".equalsIgnoreCase(action)) {
				String txtFileName = "";
				HashMap answerMap = generateFileService.createTabFile(eb, sysTimeBegin, generalFileDir, db,
						currentstudyid, currentstudyid, "");
				// the above gets us the best of both worlds - the file name,
				// together with the file id which we can then
				// push out to the browser. Shame that it is a long hack,
				// though. need to pare it down later, tbh
				// and of course DRY
				for (Object o : answerMap.entrySet()) {
					Map.Entry entry = (Map.Entry) o;
					Object key = entry.getKey();
					Object value = entry.getValue();
					txtFileName = (String) key;
					fId = (Integer) value;
				}
				request.setAttribute("generate", generalFileDir + txtFileName);
				System.out.println("+++ set the following: " + generalFileDir + txtFileName);
			} else if ("html".equalsIgnoreCase(action)) {
				// html based dataset browser
				TabReportBean answer = new TabReportBean();

				eb = dsdao.getDatasetData(eb, currentstudyid, currentstudyid);
				eb.getMetadata();
				eb.computeReport(answer);
				request.setAttribute("dataset", db);
				request.setAttribute("extractBean", eb);
				finalTarget = Page.GENERATE_DATASET_HTML;

			} else if ("spss".equalsIgnoreCase(action)) {
				SPSSReportBean answer = new SPSSReportBean();

				eb = dsdao.getDatasetData(eb, currentstudyid, currentstudyid);

				eb.getMetadata();

				eb.computeReport(answer);

				String ddlFileName = "";
				HashMap answerMap = generateFileService.createSPSSFile(db, eb, currentStudy, parentStudy, sysTimeBegin,
						generalFileDir, answer, "");

				for (Object o : answerMap.entrySet()) {
					Map.Entry entry = (Map.Entry) o;
					Object key = entry.getKey();
					Object value = entry.getValue();
					ddlFileName = (String) key;
					fId = (Integer) value;
				}
				request.setAttribute("generate", generalFileDir + ddlFileName);
				System.out.println("+++ set the following: " + generalFileDir + ddlFileName);
			} else if ("csv".equalsIgnoreCase(action)) {
				CommaReportBean answer = new CommaReportBean();
				eb = dsdao.getDatasetData(eb, currentstudyid, currentstudyid);
				eb.getMetadata();
				eb.computeReport(answer);
				long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
				String csvFileName = db.getName() + "_comma.txt";
				fId = generateFileService.createFile(csvFileName, generalFileDir, answer.toString(), db, sysTimeEnd,
						ExportFormatBean.CSVFILE, true);
				logger.info("just created csv file");
				request.setAttribute("generate", generalFileDir + csvFileName);
			} else if ("excel".equalsIgnoreCase(action)) {
				String excelFileName = db.getName() + "_excel.xls";

				finalTarget = Page.GENERATE_EXCEL_DATASET;

				response.setHeader("Content-Disposition", "attachment; filename=" + db.getName() + "_excel.xls");
				request.setAttribute("generate", generalFileDir + excelFileName);
				logger.info("set 'generate' to :" + generalFileDir + excelFileName);
			}

			if (!finalTarget.equals(Page.GENERATE_EXCEL_DATASET) && !finalTarget.equals(Page.GENERATE_DATASET_HTML)) {

				finalTarget.setFileName("" + "/WEB-INF/jsp/extract/generateMetadataCore.jsp");
				// also set up table here???
				asdfdao = getArchivedDatasetFileDAO();

				ArchivedDatasetFileBean asdfBean = (ArchivedDatasetFileBean) asdfdao.findByPK(fId);
				ArrayList newFileList = new ArrayList();
				newFileList.add(asdfBean);

				ArrayList filterRows = ArchivedDatasetFileRow.generateRowsFromBeans(newFileList);
				EntityBeanTable table = getEntityBeanTable();
				table.setSortingIfNotExplicitlySet(3, false);// sort by date
				String[] columns = {resword.getString("file_name"), resword.getString("run_time"),
						resword.getString("file_size"), resword.getString("created_date"),
						resword.getString("created_by")};

				table.setColumns(new ArrayList(Arrays.asList(columns)));
				table.hideColumnLink(0);
				table.hideColumnLink(1);
				table.hideColumnLink(2);
				table.hideColumnLink(3);
				table.hideColumnLink(4);

				request.setAttribute("dataset", db);
				request.setAttribute("file", asdfBean);
				table.setRows(filterRows);

				table.computeDisplay();

				request.setAttribute("table", table);
			}
			logger.info("set first part of 'generate' to :" + generalFileDir);
			logger.info("found file name: " + finalTarget.getFileName());

			forwardPage(finalTarget, request, response);
		}
	}

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR) || Role.isMonitor(currentRole.getRole())) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");

	}

	private void openZipFile(String fileName) {
		try {
			ZipFile zipFile = new ZipFile(fileName);

			java.util.Enumeration entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();

				if (entry.isDirectory()) {
					// Assume directories are stored parents first then
					// children.
					System.out.println("Extracting directory: " + entry.getName());
					// This is not robust, just for demonstration purposes.
					// noinspection ResultOfMethodCallIgnored
					(new File(entry.getName())).mkdir();
					// no dirs necessary?
					continue;
				}

				System.out.println("Extracting file: " + entry.getName());
				copyInputStream(zipFile.getInputStream(entry), new java.io.BufferedOutputStream(
						new java.io.FileOutputStream(entry.getName())));
			}

			zipFile.close();
		} catch (java.io.IOException ioe) {
			System.err.println("Unhandled exception:");
			ioe.printStackTrace();
		}
	}

	/**
	 * Load list.
	 *
	 * @param db DatasetBean
	 * @param asdfdao ArchivedDatasetFileDAO
	 * @param datasetId int
	 * @param fp FormProcessor
	 * @param eb ExtractBean
	 */
	public void loadList(DatasetBean db, ArchivedDatasetFileDAO asdfdao, int datasetId, FormProcessor fp, ExtractBean eb) {
		logger.info("action is blank");
		fp.getRequest().setAttribute("dataset", db);
		logger.info("just set dataset to request");
		fp.getRequest().setAttribute("extractProperties", CoreResources.getExtractProperties());
		ArrayList fileListRaw;
		fileListRaw = asdfdao.findByDatasetId(datasetId);
		ArrayList fileList = new ArrayList();
		for (Object aFileListRaw : fileListRaw) {

			ArchivedDatasetFileBean asdfBean = (ArchivedDatasetFileBean) aFileListRaw;
			asdfBean.setWebPath(asdfBean.getFileReference());

			if (new File(asdfBean.getFileReference()).isFile()) {
				fileList.add(asdfBean);
			} else {
				logger.warn(asdfBean.getFileReference() + " is NOT a file!");
			}
		}

		logger.warn("");
		logger.warn("file list length: " + fileList.size());
		fp.getRequest().setAttribute("filelist", fileList);

		ArrayList filterRows = ArchivedDatasetFileRow.generateRowsFromBeans(fileList);
		EntityBeanTable table = getEntityBeanTable();
		table.setSortingIfNotExplicitlySet(3, false);// sort by date
		String[] columns = {resword.getString("file_name"), resword.getString("run_time"),
				resword.getString("file_size"), resword.getString("created_date"), resword.getString("created_by"),
				resword.getString("action")};
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		int index = 0;
		table.hideColumnLink(index++);
		table.hideColumnLink(index++);
		table.hideColumnLink(index++);
		table.hideColumnLink(index++);
		table.hideColumnLink(index++);
		table.hideColumnLink(index);

		table.setQuery("ExportDataset?datasetId=" + db.getId(), new HashMap());
		// trying to continue...
		fp.getRequest().getSession().setAttribute("newDataset", db);
		table.setRows(filterRows);
		table.computeDisplay();

		fp.getRequest().setAttribute("table", table);
		StudyInfoPanel panel = getStudyInfoPanel(fp.getRequest());
		panel.reset();
		panel.setStudyInfoShown(false);
		setToPanel(resword.getString("study_name"), eb.getStudy().getName(), fp.getRequest());
		setToPanel(resword.getString("protocol_ID"), eb.getStudy().getIdentifier(), fp.getRequest());
		setToPanel(resword.getString("dataset_name"), db.getName(), fp.getRequest());
		String createdDate = DateUtil.printDate(db.getCreatedDate(), getUserAccountBean().getUserTimeZoneId(),
				DateUtil.DatePattern.DATE, getLocale());
		setToPanel(resword.getString("created_date"), createdDate, fp.getRequest());
		setToPanel(resword.getString("dataset_owner"), db.getOwner().getName(), fp.getRequest());
		try {
			String dateLastRun = DateUtil.printDate(db.getDateLastRun(), getUserAccountBean().getUserTimeZoneId(),
					DateUtil.DatePattern.DATE, getLocale());
			setToPanel(resword.getString("date_last_run"), dateLastRun, fp.getRequest());
		} catch (NullPointerException npe) {
			System.out.println("exception: " + npe.getMessage());
		}

		logger.warn("just set file list to request, sending to page");
	}

	private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) > 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}
}
