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

package org.akaza.openclinica.dao.core;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.bean.service.PdfProcessingFunction;
import org.akaza.openclinica.bean.service.SasProcessingFunction;
import org.akaza.openclinica.bean.service.SqlProcessingFunction;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CoreResources implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;
	public static String PROPERTIES_DIR;
	private static String DB_NAME;
	private static Properties DATAINFO;
	private static Properties EXTRACTINFO;

	private Properties dataInfo;
	private Properties extractInfo;

	public static final Integer PDF_ID = 10;
	public static final Integer TAB_ID = 8;
	public static final Integer CDISC_ODM_1_2_ID = 5;
	public static final Integer CDISC_ODM_1_2_EXTENSION_ID = 4;
	public static final Integer CDISC_ODM_1_3_ID = 3;
	public static final Integer CDISC_ODM_1_3_EXTENSION_ID = 2;
	public static final Integer SPSS_ID = 9;

	private static String webapp;
	protected final static Logger logger = LoggerFactory.getLogger("org.akaza.openclinica.dao.core.CoreResources");
	// private MessageSource messageSource;
	private static ArrayList<ExtractPropertyBean> extractProperties;

	public static String ODM_MAPPING_DIR;

	// TODO:Clean up all system outs
	// default no arg constructor
	public CoreResources() {

	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
		try {
			setODM_MAPPING_DIR();
			webapp = getWebAppName(resourceLoader.getResource("/").getURI().getPath());

			if (dataInfo != null) {
				
				String dbName = dataInfo.getProperty("dbType").trim();

				DATAINFO = dataInfo;
				dataInfo = setDataInfoProperties();// weird, but there are references to dataInfo...MainMenuServlet for
													// instance
				EXTRACTINFO = extractInfo;

				DB_NAME = dbName;
				SQLFactory factory = SQLFactory.getInstance();
				factory.run(dbName, resourceLoader);
				if (extractInfo != null) {
					copyBaseToDest(resourceLoader);
					copyODMMappingXMLtoResources(resourceLoader);
					extractProperties = findExtractProperties();
					// JN: this is in for junits to run without extract props
					copyImportRulesFiles();
				}
			}

		} catch (OpenClinicaSystemException e) {
			logger.debug(e.getMessage());
			logger.debug(e.toString());
			throw new OpenClinicaSystemException(e.getMessage(), e.fillInStackTrace());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * For changing values which are applicable to all properties, for ex webapp name can be used in any properties
	 */
	private void setDataInfoVals() {

		Enumeration<String> properties = (Enumeration<String>) DATAINFO.propertyNames();
		String vals, key;
		while (properties.hasMoreElements()) {
			key = properties.nextElement();
			vals = DATAINFO.getProperty(key);
			// replacePaths(vals);
			vals = replaceWebapp(vals);
			vals = replaceCatHome(vals);
			DATAINFO.setProperty(key, vals);
		}

	}

	private static String replaceWebapp(String value) {

		if (value.contains("${WEBAPP}")) {
			value = value.replace("${WEBAPP}", webapp);
		}

		else if (value.contains("${WEBAPP.lower}")) {
			value = value.replace("${WEBAPP.lower}", webapp.toLowerCase());
		}
		if (value.contains("$WEBAPP.lower")) {
			value = value.replace("$WEBAPP.lower", webapp.toLowerCase());
		} else if (value.contains("$WEBAPP")) {
			value = value.replace("$WEBAPP", webapp);
		}

		return value;
	}

	private static String replaceCatHome(String value) {
		String catalina = null;
		if (catalina == null) {
			catalina = System.getProperty("CATALINA_HOME");
		}

		if (catalina == null) {
			catalina = System.getProperty("catalina.home");
		}

		if (catalina == null) {
			catalina = System.getenv("CATALINA_HOME");
		}

		if (catalina == null) {
			catalina = System.getenv("catalina.home");
		}

		if (value.contains("${catalina.home}") && catalina != null) {
			value = value.replace("${catalina.home}", catalina);
		}

		if (value.contains("$catalina.home") && catalina != null) {
			value = value.replace("$catalina.home", catalina);
		}

		return value;
	}

	private static String replacePaths(String vals) {
		if (vals != null) {
			if (vals.contains("/")) {
				vals = vals.replace("/", File.separator);
			} else if (vals.contains("\\")) {
				vals = vals.replace("\\", File.separator);
			} else if (vals.contains("\\\\")) {
				vals = vals.replace("\\\\", File.separator);
			}
		}
		return vals;
	}

	private Properties setDataInfoProperties() {
		String filePath = DATAINFO.getProperty("filePath");
		if (filePath == null || filePath.isEmpty())
			filePath = "$catalina.home/$WEBAPP.lower.data";
		String database = DATAINFO.getProperty("dbType");

		setDatabaseProperties(database);

		setDataInfoVals();
		if (DATAINFO.getProperty("filePath") == null || DATAINFO.getProperty("filePath").length() <= 0)
			DATAINFO.setProperty("filePath", filePath);

		DATAINFO.setProperty("changeLogFile", "src/main/resources/migration/master.xml");
		// sysURL.base
		String sysURLBase = DATAINFO.getProperty("sysURL").replace("/MainMenu", "");
		DATAINFO.setProperty("sysURL.base", sysURLBase);

		if (DATAINFO.getProperty("org.quartz.jobStore.misfireThreshold") == null)
			DATAINFO.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
		DATAINFO.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");

		if (database.equalsIgnoreCase("oracle")) {
			DATAINFO.setProperty("org.quartz.jobStore.driverDelegateClass",
					"org.quartz.impl.jdbcjobstore.oracle.OracleDelegate");
		} else if (database.equalsIgnoreCase("postgres")) {
			DATAINFO.setProperty("org.quartz.jobStore.driverDelegateClass",
					"org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
		}

		DATAINFO.setProperty("org.quartz.jobStore.useProperties", "false");
		DATAINFO.setProperty("org.quartz.jobStore.tablePrefix", "oc_qrtz_");
		if (DATAINFO.getProperty("org.quartz.threadPool.threadCount") == null)
			DATAINFO.setProperty("org.quartz.threadPool.threadCount", "1");
		if (DATAINFO.getProperty("org.quartz.threadPool.threadPriority") == null)
			DATAINFO.setProperty("org.quartz.threadPool.threadPriority", "5");

		String attached_file_location = DATAINFO.getProperty("attached_file_location");
		if (attached_file_location == null || attached_file_location.isEmpty()) {
			attached_file_location = DATAINFO.getProperty("filePath") + "attached_files" + File.separator;
			DATAINFO.setProperty("attached_file_location", attached_file_location);
		}

		String change_passwd_required = DATAINFO.getProperty("change_passwd_required");
		if (change_passwd_required == null || change_passwd_required.isEmpty()) {
			change_passwd_required = "1";
			DATAINFO.setProperty("change_passwd_required", change_passwd_required);

		}
		// Clinovo Ticket #188 start
		String themeColor = DATAINFO.getProperty("themeColor");
		if (themeColor == null || themeColor.isEmpty()) {
			themeColor = "blue";
			DATAINFO.setProperty("themeColor", themeColor);
		}
		// end

		setMailProps();
		// setRuleDesignerProps();
		if (DATAINFO.getProperty("crfFileExtensions") != null)
			DATAINFO.setProperty("crf_file_extensions", DATAINFO.getProperty("crfFileExtensions"));
		if (DATAINFO.getProperty("crfFileExtensionSettings") != null)
			DATAINFO.setProperty("crf_file_extension_settings", DATAINFO.getProperty("crfFileExtensionSettings"));

		String dataset_file_delete = DATAINFO.getProperty("dataset_file_delete");
		if (dataset_file_delete == null)
			DATAINFO.setProperty("dataset_file_delete", "true");
		;// TODO:Revisit me!
		String password_expiration_time = DATAINFO.getProperty("passwdExpirationTime");
		if (password_expiration_time != null)
			DATAINFO.setProperty("passwd_expiration_time", password_expiration_time);

		if (DATAINFO.getProperty("maxInactiveInterval") != null)
			DATAINFO.setProperty("max_inactive_interval", DATAINFO.getProperty("maxInactiveInterval"));

		DATAINFO.setProperty("clinical_research_coordinator", "Clinical_Research_Coordinator");
		DATAINFO.setProperty("investigator", "Investigator");
		DATAINFO.setProperty("study_director", "Study_Director");

		DATAINFO.setProperty("study_administrator", "Study_Administrator");
		DATAINFO.setProperty("study_monitor", "Study_Monitor");
		DATAINFO.setProperty("ccts.waitBeforeCommit", "6000");

		String rss_url = DATAINFO.getProperty("rssUrl");
		if (rss_url == null || rss_url.isEmpty())
			rss_url = "http://clinicalresearch.wordpress.com/feed/";
		DATAINFO.setProperty("rss.url", rss_url);
		String rss_more = DATAINFO.getProperty("rssMore");
		if (rss_more == null || rss_more.isEmpty())
			rss_more = "http://clinicalresearch.wordpress.com/";
		DATAINFO.setProperty("rss.more", rss_more);

		String supportURL = DATAINFO.getProperty("supportURL");
		if (supportURL == null || supportURL.isEmpty())
			supportURL = "http://www.openclinica.org/OpenClinica/3.0/support/";
		DATAINFO.setProperty("supportURL", supportURL);

		DATAINFO.setProperty("show_unique_id", "1");

		DATAINFO.setProperty("auth_mode", "password");
		if (DATAINFO.getProperty("userAccountNotification") != null)
			DATAINFO.setProperty("user_account_notification", DATAINFO.getProperty("userAccountNotification"));

		return DATAINFO;
	}

	private void setMailProps() {

		DATAINFO.setProperty("mail.host", DATAINFO.getProperty("mailHost"));
		DATAINFO.setProperty("mail.port", DATAINFO.getProperty("mailPort"));
		DATAINFO.setProperty("mail.protocol", DATAINFO.getProperty("mailProtocol"));
		DATAINFO.setProperty("mail.username", DATAINFO.getProperty("mailUsername"));
		DATAINFO.setProperty("mail.password", DATAINFO.getProperty("mailPassword"));
		DATAINFO.setProperty("mail.smtp.auth", DATAINFO.getProperty("mailSmtpAuth"));
		DATAINFO.setProperty("mail.smtp.starttls.enable", DATAINFO.getProperty("mailSmtpStarttls.enable"));
		DATAINFO.setProperty("mail.smtps.auth", DATAINFO.getProperty("mailSmtpsAuth"));
		DATAINFO.setProperty("mail.smtps.starttls.enable", DATAINFO.getProperty("mailSmtpsStarttls.enable"));
		DATAINFO.setProperty("mail.smtp.connectiontimeout", DATAINFO.getProperty("mailSmtpConnectionTimeout"));
		DATAINFO.setProperty("mail.errormsg", DATAINFO.getProperty("mailErrorMsg"));

	}

	private void setDatabaseProperties(String database) {

		DATAINFO.setProperty("username", DATAINFO.getProperty("dbUser"));
		DATAINFO.setProperty("password", DATAINFO.getProperty("dbPass"));
		String url = null, driver = null, hibernateDialect = null;
		if (database.equalsIgnoreCase("postgres")) {
			url = "jdbc:postgresql:" + "//" + DATAINFO.getProperty("dbHost") + ":" + DATAINFO.getProperty("dbPort")
					+ "/" + DATAINFO.getProperty("db");
			driver = "org.postgresql.Driver";
			hibernateDialect = "org.hibernate.dialect.PostgreSQLDialect";
		} else if (database.equalsIgnoreCase("oracle")) {
			url = "jdbc:oracle:thin:" + "@" + DATAINFO.getProperty("dbHost") + ":" + DATAINFO.getProperty("dbPort")
					+ ":" + DATAINFO.getProperty("db");
			driver = "oracle.jdbc.driver.OracleDriver";
			hibernateDialect = "org.hibernate.dialect.OracleDialect";
		}
		DATAINFO.setProperty("dataBase", database);
		DATAINFO.setProperty("url", url.trim());
		DATAINFO.setProperty("hibernate.dialect", hibernateDialect);
		DATAINFO.setProperty("driver", driver);

	}

	private void copyBaseToDest(ResourceLoader resourceLoader) {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
		Resource[] resources;

		try {
			resources = resolver.getResources("classpath*:properties/xslt/*.xsl");
		} catch (IOException ioe) {
			logger.debug(ioe.getMessage(), ioe);
			throw new OpenClinicaSystemException("Unable to read source files", ioe);

		}

		File dest = new File(getField("filePath") + "xslt");
		if (!dest.exists()) {
			if (!dest.mkdirs()) {
				throw new OpenClinicaSystemException("Copying files, Could not create direcotry: "
						+ dest.getAbsolutePath() + ".");
			}
		}

		for (Resource r : resources) {
			File f = new File(dest, r.getFilename());
			try {

				FileOutputStream out = new FileOutputStream(f);
				IOUtils.copy(r.getInputStream(), out);
				out.close();

			} catch (IOException ioe) {
				logger.debug(ioe.getMessage(), ioe);
				throw new OpenClinicaSystemException("Unable to copy file: " + r.getFilename() + " to "
						+ f.getAbsolutePath(), ioe);

			}
		}
	}

	private void copyImportRulesFiles() throws IOException {
		ByteArrayInputStream listSrcFiles[] = new ByteArrayInputStream[3];
		String[] fileNames = { "rules.xsd", "rules_template.xml", "rules_template_with_notes.xml" };
		listSrcFiles[0] = (ByteArrayInputStream) resourceLoader.getResource(
				"classpath:properties" + File.separator + fileNames[0]).getInputStream();
		listSrcFiles[1] = (ByteArrayInputStream) resourceLoader.getResource(
				"classpath:properties" + File.separator + fileNames[1]).getInputStream();
		listSrcFiles[2] = (ByteArrayInputStream) resourceLoader.getResource(
				"classpath:properties" + File.separator + fileNames[2]).getInputStream();
		File dest = new File(getField("filePath") + "rules");
		if (!dest.exists()) {
			if (!dest.mkdirs()) {
				throw new OpenClinicaSystemException("Copying files, Could not create direcotry: "
						+ dest.getAbsolutePath() + ".");
			}
		}
		for (int i = 0; i < listSrcFiles.length; i++) {
			File dest1 = new File(dest, fileNames[i]);
			if (listSrcFiles[i] != null)
				copyFiles(listSrcFiles[i], dest1);
		}

	}

	/**
	 * @deprecated. ByteArrayInputStream keeps the whole file in memory needlessly. Use Commons IO's
	 *              {@link IOUtils#copy(java.io.InputStream, java.io.OutputStream)} instead.
	 */
	@Deprecated
	private void copyFiles(ByteArrayInputStream fis, File dest) {
		FileOutputStream fos = null;
		byte[] buffer = new byte[512]; // Buffer 4K at a time (you can change this).
		int bytesRead;
		logger.debug("fis?" + fis);
		try {
			fos = new FileOutputStream(dest);
			while ((bytesRead = fis.read(buffer)) >= 0) {
				fos.write(buffer, 0, bytesRead);
			}
		} catch (IOException ioe) {// error while copying files
			OpenClinicaSystemException oe = new OpenClinicaSystemException("Unable to copy file: " + fis + "to"
					+ dest.getAbsolutePath() + "." + dest.getAbsolutePath() + ".");
			oe.initCause(ioe);
			oe.setStackTrace(ioe.getStackTrace());
			throw oe;
		} finally { // Ensure that the files are closed (if they were open).
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioe) {
					OpenClinicaSystemException oe = new OpenClinicaSystemException("Unable to copy file: " + fis + "to"
							+ dest.getAbsolutePath() + "." + dest.getAbsolutePath() + ".");
					oe.initCause(ioe);
					oe.setStackTrace(ioe.getStackTrace());
					logger.debug(ioe.getMessage());
					throw oe;

				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
					OpenClinicaSystemException oe = new OpenClinicaSystemException("Unable to copy file: " + fis + "to"
							+ dest.getAbsolutePath() + "." + dest.getAbsolutePath() + ".");
					oe.initCause(ioe);
					oe.setStackTrace(ioe.getStackTrace());
					logger.debug(ioe.getMessage());
					throw oe;

				}
			}
		}
	}

	/**
	 * @pgawade 18-April-2011 - Fix for issue 8394 Copy core\resources\properties\cd_odm_mapping.xml to web application
	 *          resources outside the core jar file Reason - During CRF data import, Castor API is not able to load this
	 *          mapping xml file from core jar file
	 */
	private void copyODMMappingXMLtoResources(ResourceLoader resourceLoader) {

		ByteArrayInputStream listSrcFiles[] = new ByteArrayInputStream[10];
		String[] fileNames = { "cd_odm_mapping.xml" };
		try {
			listSrcFiles[0] = (ByteArrayInputStream) resourceLoader.getResource(
					"classpath:properties" + File.separator + "cd_odm_mapping.xml").getInputStream();

		} catch (IOException ioe) {
			OpenClinicaSystemException oe = new OpenClinicaSystemException("Unable to read source files");
			oe.initCause(ioe);
			oe.setStackTrace(ioe.getStackTrace());
			logger.error(ioe.getMessage());
			throw oe;
		}

		File dest = null;
		try {
			File placeholder_file = new File(resourceLoader
					.getResource("classpath:org/akaza/openclinica/applicationContext-web-beans.xml").getURL().getFile());

			String placeholder_file_path = placeholder_file.getPath();

			String tmp2 = placeholder_file_path.substring(0, placeholder_file_path.indexOf("WEB-INF") - 1);
			String tmp3 = tmp2 + File.separator + "WEB-INF" + File.separator + "classes";
			dest = new File(tmp3 + File.separator + "odm_mapping");

		} catch (IOException ioe) {
			OpenClinicaSystemException oe = new OpenClinicaSystemException("Unable to get web app base path");
			oe.initCause(ioe);
			oe.setStackTrace(ioe.getStackTrace());
			logger.error(ioe.getMessage());
			throw oe;
		}

		if (!dest.exists()) {
			if (!dest.mkdirs()) {
				logger.error("Copying files, Could not create directory: " + dest.getAbsolutePath() + ".");
				throw new OpenClinicaSystemException("Copying files, Could not create directory: "
						+ dest.getAbsolutePath() + ".");
			}
		}

		for (int i = 0; i < fileNames.length; i++) {
			File dest1 = new File(dest, fileNames[i]);
			// File src1 = listSrcFiles[i];
			// switch with IOUtils.copy?
			if (listSrcFiles[i] != null)
				copyFiles(listSrcFiles[i], dest1);
		}

	}

	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public static ArrayList<ExtractPropertyBean> getExtractProperties() {
		return extractProperties;
	}

	public void setExtractProperties(ArrayList extractProperties) {
		CoreResources.extractProperties = extractProperties;
	}

	private ArrayList<ExtractPropertyBean> findExtractProperties() throws OpenClinicaSystemException {
		ArrayList<ExtractPropertyBean> ret = new ArrayList<ExtractPropertyBean>();

		int i = 1;
		while (!getExtractField("extract." + i + ".file").equals("")) {
			ExtractPropertyBean epbean = new ExtractPropertyBean();
			epbean.setId(i);
			// we will implement a find by id function in the front end

			// check to make sure the file exists, if not throw an exception and system will abort to start.
			checkForFile(getExtractFields("extract." + i + ".file"));
			epbean.setFileName(getExtractFields("extract." + i + ".file"));
			// file name of the xslt stylesheet
			epbean.setFiledescription(getExtractField("extract." + i + ".fileDescription"));
			// description of the choice of format
			epbean.setHelpText(getExtractField("extract." + i + ".helpText"));
			// help text, currently in the alt-text of the link
			epbean.setLinkText(getExtractField("extract." + i + ".linkText"));

			epbean.setFileLocation(getExtractField("extract." + i + ".location"));

			epbean.setOdmType(getExtractField("extract." + i + ".odmType"));

			epbean.setFormat("oc1.3");

			// destination file name of the copied files
			epbean.setExportFileName(getExtractFields("extract." + i + ".exportname"));
			String whichFunction = getExtractField("extract." + i + ".post").toLowerCase();
			epbean.setZipFormat(getExtractFieldBoolean("extract." + i + ".zip"));
			epbean.setDeleteOld(getExtractFieldBoolean("extract." + i + ".deleteOld"));
			epbean.setSuccessMessage(getExtractField("extract." + i + ".success"));
			epbean.setFailureMessage(getExtractField("extract." + i + ".failure"));
			epbean.setZipName(getExtractField("extract." + i + ".zipName"));
			if (epbean.getFileName().length != epbean.getExportFileName().length)
				throw new OpenClinicaSystemException(
						"The comma seperated values of file names and export file names should correspond 1 on 1 for the property number"
								+ i);

			if ("sql".equals(whichFunction)) {
				// set the bean within, so that we can access the file locations etc
				SqlProcessingFunction function = new SqlProcessingFunction(epbean);
				String whichSettings = getExtractField("xsl.post." + i + ".sql");
				if (!"".equals(whichSettings)) {
					function.setDatabaseType(getExtractFieldNoRep(whichSettings + ".dataBase").toLowerCase());
					function.setDatabaseUrl(getExtractFieldNoRep(whichSettings + ".url"));
					function.setDatabaseUsername(getExtractFieldNoRep(whichSettings + ".username"));
					function.setDatabasePassword(getExtractFieldNoRep(whichSettings + ".password"));
				} else {
					// set default db settings here
					function.setDatabaseType(getField("dataBase"));
					function.setDatabaseUrl(getField("url"));
					function.setDatabaseUsername(getField("username"));
					function.setDatabasePassword(getField("password"));
				}
				// also pre-set the database connection stuff
				epbean.setPostProcessing(function);
			} else if ("pdf".equals(whichFunction)) {
				// TODO add other functions here
				epbean.setPostProcessing(new PdfProcessingFunction());
			} else if ("sas".equals(whichFunction)) {
				epbean.setPostProcessing(new SasProcessingFunction());
			} else if (!whichFunction.isEmpty()) {
				String postProcessorName = getExtractField(whichFunction + ".postProcessor");
				if (postProcessorName.equals("pdf")) {
					epbean.setPostProcessing(new PdfProcessingFunction());
					epbean.setPostProcDeleteOld(getExtractFieldBoolean(whichFunction + ".deleteOld"));
					epbean.setPostProcZip(getExtractFieldBoolean(whichFunction + ".zip"));
					epbean.setPostProcLocation(getExtractField(whichFunction + ".location"));
					epbean.setPostProcExportName(getExtractField(whichFunction + ".exportname"));
				}
				// since the database is the last option TODO: think about custom post processing options
				else {
					SqlProcessingFunction function = new SqlProcessingFunction(epbean);

					function.setDatabaseType(getExtractFieldNoRep(whichFunction + ".dataBase").toLowerCase());
					function.setDatabaseUrl(getExtractFieldNoRep(whichFunction + ".url"));
					function.setDatabaseUsername(getExtractFieldNoRep(whichFunction + ".username"));
					function.setDatabasePassword(getExtractFieldNoRep(whichFunction + ".password"));
					epbean.setPostProcessing(function);
				}

			} else {
				// add a null here
				epbean.setPostProcessing(null);
			}
			ret.add(epbean);
			i++;
		}

		return ret;
	}

	private String getExtractFieldNoRep(String key) {
		String value = EXTRACTINFO.getProperty(key);
		if (value != null) {
			value = value.trim();
		}

		return value == null ? "" : value;
	}

	private void checkForFile(String[] extractFields) throws OpenClinicaSystemException {

		int cnt = extractFields.length;
		int i = 0;
		// iterate through all comma separated file names
		while (i < cnt) {

			File f = new File(getField("filePath") + "xslt" + File.separator + extractFields[i]);
			if (!f.exists())
				throw new OpenClinicaSystemException("FileNotFound -- Please make sure" + extractFields[i] + "exists");

			i++;

		}

	}

	public InputStream getInputStream(String fileName) throws IOException {
		return resourceLoader.getResource("classpath:properties/" + fileName).getInputStream();
	}

	public URL getURL(String fileName) throws IOException {
		return resourceLoader.getResource("classpath:properties/" + fileName).getURL();
	}

	/**
	 * @throws IOException
	 * @deprecated Use {@link #getFile(String,String)} instead
	 */
	public File getFile(String fileName) throws IOException {
		return getFile(fileName, "filePath");
	}

	public File getFile(String fileName, String relDirectory) {
		try {

			getInputStream(fileName);

			File f = new File(getField("filePath") + relDirectory + fileName);
			return f;

		} catch (IOException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.fillInStackTrace());
		}
	}

	public void setPROPERTIES_DIR() {
		String resource = "classpath:properties/placeholder.properties";
		Resource scr = resourceLoader.getResource(resource);
		String absolutePath = null;
		try {
			absolutePath = scr.getFile().getAbsolutePath();
			PROPERTIES_DIR = absolutePath.replaceAll("placeholder.properties", "");
		} catch (IOException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.fillInStackTrace());
		}

	}

	/**
	 * @pgawade 18-April-2011 - Fix for issue 8394 Method to set the absolute file path value to point to "odm_mapping"
	 *          in resources. cd_odm_mapping.xml file used by Castor API during CRF data import will be copied to this
	 *          location during application initialization
	 */
	public void setODM_MAPPING_DIR() {
		String resource = "classpath:datainfo.properties";

		Resource scr = resourceLoader.getResource(resource);
		String absolutePath = null;
		try {

			absolutePath = scr.getFile().getAbsolutePath();
			ODM_MAPPING_DIR = absolutePath.replaceAll("datainfo.properties", "") + "odm_mapping";
		} catch (IOException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.fillInStackTrace());
		}
	}

	public static String getDBName() {
		return DB_NAME;
	}

	public static String getField(String key) {
		if (DATAINFO == null)
			return "";
		String value = DATAINFO.getProperty(key);
		if (value != null) {
			value = value.trim();
		}
		return value == null ? "" : value;
	}

	/**
	 * gets the actual system URL by doing a DNS name lookup repeated code from web to fix #99 eventually refactor to
	 * work as one, tbh
	 */
	public static String getSystemURL() {
		String url = DATAINFO.getProperty("sysURL.base");
		if (url != null) {
			url = url.trim();
		} else {
			return "";
		}
		// lookup the DNS here and report it
		DNSLookup lookup = new DNSLookup();
		return lookup.getTrueSystemURL(url);
	}

	// TODO internationalize
	public static String getExtractField(String key) {
		String value = EXTRACTINFO.getProperty(key);
		if (value != null) {
			value = value.trim();
		}
		value = replacePaths(value);
		return value == null ? "" : value;
	}

	// JN:The following method returns default of true when converting from string
	public static boolean getExtractFieldBoolean(String key) {
		String value = EXTRACTINFO.getProperty(key);
		if (value != null) {
			value = value.trim();
		}
		if (value == null)
			return true;// Defaulting to true
		if (value.equalsIgnoreCase("false"))
			return false;
		else
			return true;// defaulting to true

	}

	public static String[] getExtractFields(String key) {
		String value = EXTRACTINFO.getProperty(key);

		if (value != null) {
			value = value.trim();
		}
		return value.split(",");
	}

	public ExtractPropertyBean findExtractPropertyBeanById(int id, String datasetId) {
		ArrayList<ExtractPropertyBean> epBeans = findExtractProperties();
		ExtractPropertyBean returnBean = null;
		for (ExtractPropertyBean epbean : epBeans) {

			if (epbean.getId() == id) {
				epbean.setDatasetId(datasetId);
				return epbean;
			}

		}
		return returnBean;
	}

	public Properties getDataInfo() {
		return DATAINFO;
	}

	public void setDataInfo(Properties dataInfo) {
		this.dataInfo = dataInfo;
	}

	public Properties getExtractInfo() {
		return extractInfo;
	}

	public void setExtractInfo(Properties extractInfo) {
		this.extractInfo = extractInfo;
	}

	public String getWebAppName(String servletCtxRealPath) {
		String webAppName = null;
		if (null != servletCtxRealPath) {
			String[] tokens = servletCtxRealPath.split("/");
			webAppName = tokens[(tokens.length - 1)].trim();
		}
		return webAppName;
	}

}
