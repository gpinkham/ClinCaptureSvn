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

import liquibase.spring.SpringLiquibase;
import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.bean.service.PdfProcessingFunction;
import org.akaza.openclinica.bean.service.SasProcessingFunction;
import org.akaza.openclinica.bean.service.SqlProcessingFunction;
import org.akaza.openclinica.core.ExtendedBasicDataSource;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CoreResources implements ResourceLoaderAware {

	protected final static Logger logger = LoggerFactory.getLogger("org.akaza.openclinica.dao.core.CoreResources");

	private ResourceLoader resourceLoader;
	public static String PROPERTIES_DIR;

	private static Properties dataInfo;
	private static Properties extractInfo;

	public static final Integer TAB_ID = 8;
	public static final Integer CDISC_ODM_1_2_ID = 5;
	public static final Integer CDISC_ODM_1_2_EXTENSION_ID = 4;
	public static final Integer CDISC_ODM_1_3_ID = 3;
	public static final Integer CDISC_ODM_1_3_EXTENSION_ID = 2;
	public static final Integer SPSS_ID = 9;

	private static String webapp;
	private static boolean shouldBeRestarted;

	// private MessageSource messageSource;
	private static ArrayList<ExtractPropertyBean> extractProperties;

	public static String ODM_MAPPING_DIR;

	private static boolean loadPropertiesFromDB(Connection connection) throws Exception {
		boolean result = false;
		PreparedStatement ps = connection.prepareStatement("select name, value, type from system");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String propertyName = rs.getString(1);
			String propertyValue = rs.getString(2);
			String propertyType = rs.getString(3);
			if (!propertyType.equalsIgnoreCase("dynamic_input") && !propertyType.equalsIgnoreCase("dynamic_radio")) {
				dataInfo.put(propertyName, propertyValue == null ? "" : propertyValue);
			}
		}
		ps.close();
		rs.close();
		return result;
	}

	private void setDatabaseProperties(String dbType) {
		dataInfo.setProperty("username", dataInfo.getProperty("dbUser"));
		dataInfo.setProperty("password", dataInfo.getProperty("dbPass"));
		String url, driver, hibernateDialect;
		if (dbType.equalsIgnoreCase("postgres")) {
			url = "jdbc:postgresql:" + "//" + dataInfo.getProperty("dbHost") + ":" + dataInfo.getProperty("dbPort")
					+ "/" + dataInfo.getProperty("db");
			driver = "org.postgresql.Driver";
			hibernateDialect = "org.hibernate.dialect.PostgreSQLDialect";
		} else if (dbType.equalsIgnoreCase("oracle")) {
			url = "jdbc:oracle:thin:" + "@" + dataInfo.getProperty("dbHost") + ":" + dataInfo.getProperty("dbPort")
					+ ":" + dataInfo.getProperty("db");
			driver = "oracle.jdbc.driver.OracleDriver";
			hibernateDialect = "org.hibernate.dialect.OracleDialect";

		} else {
			url = dataInfo.getProperty("url");
			driver = dataInfo.getProperty("driver");
			hibernateDialect = dataInfo.getProperty("hibernate.dialect");
		}
		dataInfo.setProperty("url", url.trim());
		dataInfo.setProperty("hibernate.dialect", hibernateDialect);
		dataInfo.setProperty("driver", driver);
	}

	private void loadSystemProperties() throws Exception {
		Connection connection = null;
		ExtendedBasicDataSource dataSource = null;
		try {
			String path = resourceLoader.getResource("/").getURI().getPath();
			webapp = getWebAppName(path);

			String logDir = dataInfo.getProperty("log.dir");
			String filePath = dataInfo.getProperty("filePath");
			String dbType = dataInfo.getProperty("dbType").trim();
			String attachedFileLocation = dataInfo.getProperty("attached_file_location");
			dataInfo.setProperty("log.dir", logDir != null ? logDir.replace("\\", "\\\\") : "");
			dataInfo.setProperty("filePath", filePath != null ? filePath.replace("\\", "\\\\") : "");
			dataInfo.setProperty("attached_file_location",
					attachedFileLocation != null ? attachedFileLocation.replace("\\", "\\\\") : "");

			dataInfo.setProperty("currentWebAppName", webapp);
			dataInfo.setProperty("currentWebAppContext", "/" + webapp);
			dataInfo.setProperty("currentDBName", dataInfo.getProperty("db").trim());

			setDatabaseProperties(dbType);

			// setup dataSource
			dataSource = new ExtendedBasicDataSource();
			dataSource.setUrl(dataInfo.getProperty("url"));
			dataSource.setUsername(dataInfo.getProperty("username"));
			dataSource.setPassword(dataInfo.getProperty("password"));
			dataSource.setDriverClassName(dataInfo.getProperty("driver"));
			dataSource.setMaxActive(50);
			dataSource.setMaxIdle(2);
			dataSource.setMaxWait(180000);
			dataSource.setRemoveAbandoned(true);
			dataSource.setRemoveAbandonedTimeout(300);
			dataSource.setLogAbandoned(true);
			dataSource.setTestWhileIdle(true);
			dataSource.setTestOnReturn(true);
			dataSource.setTimeBetweenEvictionRunsMillis(300000);
			dataSource.setMinEvictableIdleTimeMillis(600000);
			dataSource.setBigStringTryClob("true");
			connection = dataSource.getConnection();

			SpringLiquibase liquibase = new SpringLiquibase();
			liquibase.setDataSource(dataSource);
			liquibase.setChangeLog("classpath:migration/clincaptrue/2014-01-16-TICKET863.xml");
			liquibase.setResourceLoader(resourceLoader);
			liquibase.afterPropertiesSet();

			// load system properties from the database
			loadPropertiesFromDB(connection);

			checkLogo();
			prepareDataInfoProperties();

			SQLFactory.getInstance().run(dbType, resourceLoader);
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
				if (dataSource != null) {
					dataSource.close();
				}
			} catch (Exception ex) {
				logger.error("Error has occurred.", ex);
			}
		}
	}

	private void checkLogo() {
		try {
			new DefaultResourceLoader().getResource(
					new DefaultResourceLoader().getResource(".." + File.separator + ".." + File.separator).getFile()
							.getAbsoluteFile()
							+ CoreResources.getField("logo")).getFile();
		} catch (Exception ex) {
			CoreResources.setField("logo", "/images/Logo.gif");
		}
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
		try {
			setODM_MAPPING_DIR();
			if (dataInfo != null) {
				loadSystemProperties();
				if (extractInfo != null) {
					copyBaseToDest(resourceLoader);
					copyODMMappingXMLtoResources(resourceLoader);
					extractProperties = findExtractProperties();
					// JN: this is in for junits to run without extract props
					copyImportRulesFiles();
				}
			}
		} catch (OpenClinicaSystemException ex) {
			logger.error("Error has occurred.", ex);
		} catch (Exception ex) {
			logger.error("Error has occurred.", ex);
		}
	}

	/**
	 * For changing values which are applicable to all properties, for ex webapp name can be used in any properties
	 */
	private static void prepareDataInfoVals() {

		Enumeration<String> properties = (Enumeration<String>) dataInfo.propertyNames();
		String vals, key;
		while (properties.hasMoreElements()) {
			key = properties.nextElement();
			vals = dataInfo.getProperty(key);
			// replacePaths(vals);
			vals = replaceWebapp(vals);
			vals = replaceCatHome(vals);
			dataInfo.setProperty(key, vals);
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
		String catalina = System.getProperty("CATALINA_HOME");

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

	public static void prepareDataInfoProperties() {
		String filePath = dataInfo.getProperty("filePath");
		if (filePath == null || filePath.isEmpty())
			filePath = "$catalina.home/$WEBAPP.lower.data";
		String dbType = dataInfo.getProperty("dbType");

		prepareDataInfoVals();
		if (dataInfo.getProperty("filePath") == null || dataInfo.getProperty("filePath").length() <= 0)
			dataInfo.setProperty("filePath", filePath);

		dataInfo.setProperty("changeLogFile", "src/main/resources/migration/master.xml");
		// sysURL.base
		String sysURLBase = dataInfo.getProperty("sysURL").replace("/MainMenu", "");
		dataInfo.setProperty("sysURL.base", sysURLBase);

		if (dataInfo.getProperty("org.quartz.jobStore.misfireThreshold") == null)
			dataInfo.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
		dataInfo.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");

		if (dbType.equalsIgnoreCase("oracle")) {
			dataInfo.setProperty("org.quartz.jobStore.driverDelegateClass",
					"org.quartz.impl.jdbcjobstore.oracle.OracleDelegate");
		} else if (dbType.equalsIgnoreCase("postgres")) {
			dataInfo.setProperty("org.quartz.jobStore.driverDelegateClass",
					"org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
		}

		dataInfo.setProperty("org.quartz.jobStore.useProperties", "false");
		dataInfo.setProperty("org.quartz.jobStore.tablePrefix", "oc_qrtz_");
		if (dataInfo.getProperty("org.quartz.threadPool.threadCount") == null)
			dataInfo.setProperty("org.quartz.threadPool.threadCount", "1");
		if (dataInfo.getProperty("org.quartz.threadPool.threadPriority") == null)
			dataInfo.setProperty("org.quartz.threadPool.threadPriority", "5");

		// Clinovo Ticket #188 start
		String themeColor = dataInfo.getProperty("themeColor");
		if (themeColor == null || themeColor.isEmpty()) {
			themeColor = "blue";
			dataInfo.setProperty("themeColor", themeColor);
		}
		// end

		prepareMailProps();
		// setRuleDesignerProps();
		if (dataInfo.getProperty("crfFileExtensions") != null)
			dataInfo.setProperty("crf_file_extensions", dataInfo.getProperty("crfFileExtensions"));
		if (dataInfo.getProperty("crfFileExtensionSettings") != null)
			dataInfo.setProperty("crf_file_extension_settings", dataInfo.getProperty("crfFileExtensionSettings"));

		String dataset_file_delete = dataInfo.getProperty("dataset_file_delete");
		if (dataset_file_delete == null)
			dataInfo.setProperty("dataset_file_delete", "true");
		// TODO:Revisit me!

		if (dataInfo.getProperty("maxInactiveInterval") != null)
			dataInfo.setProperty("max_inactive_interval", dataInfo.getProperty("maxInactiveInterval"));

		dataInfo.setProperty("clinical_research_coordinator", "Clinical_Research_Coordinator");
		dataInfo.setProperty("investigator", "Investigator");
		dataInfo.setProperty("study_director", "Study_Director");

		dataInfo.setProperty("study_administrator", "Study_Administrator");
		dataInfo.setProperty("study_monitor", "Study_Monitor");
		dataInfo.setProperty("ccts.waitBeforeCommit", "6000");

		String rss_url = dataInfo.getProperty("rssUrl");
		if (rss_url == null || rss_url.isEmpty())
			rss_url = "http://clinicalresearch.wordpress.com/feed/";
		dataInfo.setProperty("rss.url", rss_url);
		String rss_more = dataInfo.getProperty("rssMore");
		if (rss_more == null || rss_more.isEmpty())
			rss_more = "http://clinicalresearch.wordpress.com/";
		dataInfo.setProperty("rss.more", rss_more);

		String supportURL = dataInfo.getProperty("supportURL");
		if (supportURL == null || supportURL.isEmpty())
			supportURL = "http://www.openclinica.org/OpenClinica/3.0/support/";
		dataInfo.setProperty("supportURL", supportURL);

		dataInfo.setProperty("show_unique_id", "1");

		dataInfo.setProperty("auth_mode", "password");
		if (dataInfo.getProperty("userAccountNotification") != null)
			dataInfo.setProperty("user_account_notification", dataInfo.getProperty("userAccountNotification"));
	}

	private static void prepareMailProps() {
		dataInfo.setProperty("mail.host", dataInfo.getProperty("mailHost"));
		dataInfo.setProperty("mail.port", dataInfo.getProperty("mailPort"));
		dataInfo.setProperty("mail.protocol", dataInfo.getProperty("mailProtocol"));
		dataInfo.setProperty("mail.username", dataInfo.getProperty("mailUsername"));
		dataInfo.setProperty("mail.password", dataInfo.getProperty("mailPassword"));
		dataInfo.setProperty("mail.smtp.auth", dataInfo.getProperty("mailAuth"));
		dataInfo.setProperty("mail.smtp.starttls.enable", dataInfo.getProperty("mailTls"));
		dataInfo.setProperty("mail.smtps.auth", dataInfo.getProperty("mailAuth"));
		dataInfo.setProperty("mail.smtps.starttls.enable", dataInfo.getProperty("mailTls"));
		dataInfo.setProperty("mail.smtp.connectiontimeout", dataInfo.getProperty("mailSmtpConnectionTimeout"));
		dataInfo.setProperty("mail.errormsg", dataInfo.getProperty("mailErrorMsg"));

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
	 * @deprecated ByteArrayInputStream keeps the whole file in memory needlessly. Use Commons IO's
	 *             {@link IOUtils#copy(java.io.InputStream, java.io.OutputStream)} instead.
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

		File dest;
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
					function.setDatabaseType(getField("dbType"));
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
		String value = extractInfo.getProperty(key);
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

			return new File(getField("filePath") + relDirectory + fileName);

		} catch (IOException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.fillInStackTrace());
		}
	}

	public void setPROPERTIES_DIR() {
		String resource = "classpath:properties/placeholder.properties";
		Resource scr = resourceLoader.getResource(resource);
		String absolutePath;
		try {
			absolutePath = scr.getFile().getAbsolutePath();
			PROPERTIES_DIR = absolutePath.replaceAll("placeholder.properties", "");
		} catch (IOException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.fillInStackTrace());
		}

	}

	/**
	 * pgawade 18-April-2011 - Fix for issue 8394 Method to set the absolute file path value to point to "odm_mapping"
	 * in resources. cd_odm_mapping.xml file used by Castor API during CRF data import will be copied to this location
	 * during application initialization
	 */
	public void setODM_MAPPING_DIR() {
		String resource = "classpath:datainfo.properties";

		Resource scr = resourceLoader.getResource(resource);
		String absolutePath;
		try {

			absolutePath = scr.getFile().getAbsolutePath();
			ODM_MAPPING_DIR = absolutePath.replaceAll("datainfo.properties", "") + "odm_mapping";
		} catch (IOException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.fillInStackTrace());
		}
	}

	public static String getDBType() {
		return dataInfo.getProperty("dbType");
	}

	public static String getField(String key) {
		if (dataInfo == null)
			return "";
		String value = dataInfo.getProperty(key);
		if (value != null) {
			value = value.trim();
		}
		return value == null ? "" : value;
	}

	public static void setField(String key, String value) {
		if (dataInfo != null && value != null) {
			dataInfo.setProperty(key, value.trim());
		}
	}

	/**
	 * gets the actual system URL by doing a DNS name lookup repeated code from web to fix #99 eventually refactor to
	 * work as one, tbh
	 */
	public static String getSystemURL() {
		String url = dataInfo.getProperty("sysURL.base");
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
		String value = extractInfo.getProperty(key);
		if (value != null) {
			value = value.trim();
		}
		value = replacePaths(value);
		return value == null ? "" : value;
	}

	// JN:The following method returns default of true when converting from string
	public static boolean getExtractFieldBoolean(String key) {
		String value = extractInfo.getProperty(key);
		if (value != null) {
			value = value.trim();
		}
		return value == null || !value.equalsIgnoreCase("false");

	}

	public static String[] getExtractFields(String key) {
		String value = extractInfo.getProperty(key);

		if (value != null) {
			value = value.trim();
		}
		return value != null ? value.split(",") : new String[0];
	}

	public ExtractPropertyBean findExtractPropertyBeanById(int id, String datasetId) {
		ArrayList<ExtractPropertyBean> epBeans = findExtractProperties();
		for (ExtractPropertyBean epbean : epBeans) {

			if (epbean.getId() == id) {
				epbean.setDatasetId(datasetId);
				return epbean;
			}

		}
		return null;
	}

	public Properties getDataInfo() {
		return CoreResources.dataInfo;
	}

	public void setDataInfo(Properties dataInfo) {
		CoreResources.dataInfo = dataInfo;
	}

	public Properties getExtractInfo() {
		return CoreResources.extractInfo;
	}

	public void setExtractInfo(Properties extractInfo) {
		CoreResources.extractInfo = extractInfo;
	}

	public String getWebAppName(String servletCtxRealPath) {
		String webAppName = null;
		if (null != servletCtxRealPath) {
			String[] tokens = servletCtxRealPath.split("/");
			webAppName = tokens[(tokens.length - 1)].trim();
		}
		return webAppName;
	}

	public static boolean isShouldBeRestarted() {
		return shouldBeRestarted;
	}

	public static void setShouldBeRestarted(boolean shouldBeRestarted) {
		CoreResources.shouldBeRestarted = shouldBeRestarted;
	}

}
