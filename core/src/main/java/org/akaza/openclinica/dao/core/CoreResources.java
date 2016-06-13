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

package org.akaza.openclinica.dao.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import liquibase.integration.spring.SpringLiquibase;

import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.bean.service.PdfProcessingFunction;
import org.akaza.openclinica.bean.service.SasProcessingFunction;
import org.akaza.openclinica.bean.service.SqlProcessingFunction;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

/**
 * Core resources.
 */
@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class CoreResources implements ResourceLoaderAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoreResources.class);

	public static final Set<String> CALENDAR_LOCALES = new HashSet<String>();

	public static final String UTF_8 = "utf-8";

	private static boolean copyODM;

	private ResourceLoader resourceLoader;

	private static Properties dataInfo;
	private static Properties extractInfo;

	public static final Integer TAB_ID = 8;
	public static final Integer CDISC_ODM_1_2_ID = 5;
	public static final Integer CDISC_ODM_1_2_EXTENSION_ID = 4;
	public static final Integer CDISC_ODM_1_3_ID = 3;
	public static final Integer CDISC_ODM_1_3_EXTENSION_ID = 2;
	public static final Integer SPSS_ID = 9;

	public static final String PROP_CLIENT_LOGO = "logo";

	public static final String CLIENT_DEFAULT_LOGO_PATH = "/images/logo_client_default_200x61.png";
	public static final String SYSTEM_LOGO_PATH = "/images/logo_system_200x61.png";
	public static final String SYSTEM_FAVICON_PATH = "/images/favicon.png";

	private static ArrayList<ExtractPropertyBean> extractProperties;
	private static String domainName;

	private static void loadPropertiesFromDB(Connection connection) throws Exception {
		boolean filePathIsEmptyInDb = false;
		PreparedStatement ps = connection.prepareStatement("select name, value, type from system");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String propertyName = rs.getString(1);
			String propertyValue = rs.getString(2);
			String propertyType = rs.getString(3);
			propertyValue = (propertyValue == null ? "" : propertyValue).trim();
			if (propertyName.equals("filePath")) {
				filePathIsEmptyInDb = propertyValue.isEmpty();
				if (!filePathIsEmptyInDb) {
					dataInfo.put(
							propertyName,
							propertyValue.endsWith(File.separator) ? propertyValue : propertyValue
									.concat(File.separator));
				}
			} else if (!propertyType.equalsIgnoreCase("dynamic_input")
					&& !propertyType.equalsIgnoreCase("dynamic_radio")) {
				dataInfo.put(propertyName, propertyValue);
			}
		}
		ps.close();
		rs.close();
		if (filePathIsEmptyInDb) {
			ps = connection.prepareStatement("update system set value = ? where name = 'filePath'");
			ps.setString(1, (String) dataInfo.get("filePath"));
			ps.executeUpdate();
		}
		File filePath = new File((String) dataInfo.get("filePath"));
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
	}

	private void setDatabaseProperties(String dbType) {
		LOGGER.info("Initializing web application properties");
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
		BasicDataSource dataSource = null;
		try {
			String path = resourceLoader.getResource("/").exists()
					? resourceLoader.getResource("/").getURI().getPath()
					: "";
			String webapp = getWebAppName(path);
			String logDir = System.getProperty("log.dir") == null ? "" : System.getProperty("log.dir");
			String dbType = dataInfo.getProperty("dbType").trim();
			String attachedFileLocation = dataInfo.getProperty("attached_file_location");
			String catalinaHome = System.getProperty("catalina.home") == null ? new File(URLDecoder.decode(getClass()
					.getClassLoader().getResource(".").getPath(), UTF_8)).getPath() : System
					.getProperty("catalina.home");
			StringBuilder filePath = new StringBuilder(catalinaHome);
			filePath.append(File.separator);
			filePath.append(webapp);
			filePath.append("-data");
			filePath.append(File.separator);
			// Set properties
			dataInfo.setProperty("log.dir", logDir);
			dataInfo.setProperty("currentWebAppName", webapp);
			dataInfo.setProperty("filePath", filePath.toString());
			dataInfo.setProperty("currentWebAppContext", "/" + webapp);
			dataInfo.setProperty("currentDBName", dataInfo.getProperty("db").trim());
			dataInfo.setProperty("attached_file_location",
					attachedFileLocation != null ? attachedFileLocation.replace("\\", "\\\\") : "");

			setDatabaseProperties(dbType);

			// setup dataSource
			dataSource = new BasicDataSource();
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
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
				if (dataSource != null) {
					dataSource.close();
				}
			} catch (Exception ex) {
				LOGGER.error("Error has occurred.", ex);
			}
		}
	}

	private void fillCalendarLangs() {
		try {
			Resource resource = new DefaultResourceLoader().getResource("..".concat(File.separator).concat("..")
					.concat(File.separator).concat("includes").concat(File.separator).concat("calendar")
					.concat(File.separator).concat("locales"));
			if (resource.exists()) {
				for (String fileName : resource.getFile().list()) {
					CALENDAR_LOCALES.add(fileName.replaceAll(".*-", "").replaceAll("\\..*", ""));
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
	}

	private void checkLogo() {
		try {
			File logoFile = new File(new DefaultResourceLoader()
					.getResource(".." + File.separator + ".." + File.separator).getFile().getAbsoluteFile()
					+ CoreResources.getField(PROP_CLIENT_LOGO));
			if (!logoFile.exists()) {
				FileCopyUtils.copy(new File(new File(CoreResources.getField("filePath")).getAbsolutePath()
						+ File.separator + "uploads" + File.separator + logoFile.getName()), logoFile);
			}
		} catch (Exception ex) {
			CoreResources.setField(PROP_CLIENT_LOGO, CLIENT_DEFAULT_LOGO_PATH);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
		try {
			if (dataInfo != null) {
				fillCalendarLangs();
				loadSystemProperties();
				if (extractInfo != null) {
					copyBaseToDest(resourceLoader);
					if (copyODM) {
						copyODMMappingXMLtoResources(resourceLoader);
					}
					extractProperties = findExtractProperties();
					// JN: this is in for junits to run without extract props
					copyImportRulesFiles();
				}
			}
		} catch (OpenClinicaSystemException ex) {
			ex.printStackTrace();
			LOGGER.error("Error has occurred.", ex);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.error("Error has occurred.", ex);
		}
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

	private static void replaceWebappIfItsPresent() {
		String sysURL = dataInfo.getProperty("sysURL");
		String currentWebAppName = dataInfo.getProperty("currentWebAppName");
		if (sysURL.contains("${WEBAPP}")) {
			dataInfo.setProperty("sysURL", sysURL.replace("${WEBAPP}", currentWebAppName));
		} else if (sysURL.contains("${webapp}")) {
			dataInfo.setProperty("sysURL", sysURL.replace("${webapp}", currentWebAppName));
		}
	}

	/**
	 * Prepares dataInfo properties.
	 */
	public static void prepareDataInfoProperties() {
		String dbType = dataInfo.getProperty("dbType");

		dataInfo.setProperty("changeLogFile", "src/main/resources/migration/master.xml");
		// sysURL.base
		replaceWebappIfItsPresent();
		String sysURLBase = dataInfo.getProperty("sysURL").replace("/MainMenu", "");
		dataInfo.setProperty("sysURL.base", sysURLBase);

		if (dataInfo.getProperty("org.quartz.jobStore.misfireThreshold") == null) {
			dataInfo.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
		}
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
		if (dataInfo.getProperty("org.quartz.threadPool.threadCount") == null) {
			dataInfo.setProperty("org.quartz.threadPool.threadCount", "1");
		}
		if (dataInfo.getProperty("org.quartz.threadPool.threadPriority") == null) {
			dataInfo.setProperty("org.quartz.threadPool.threadPriority", "5");
		}

		// Clinovo Ticket #188 start
		String themeColor = dataInfo.getProperty("themeColor");
		if (themeColor == null || themeColor.isEmpty()) {
			themeColor = "blue";
			dataInfo.setProperty("themeColor", themeColor);
		}
		// end

		String assignRandomizationResultTo = dataInfo.getProperty("assignRandomizationResultTo");
		if (assignRandomizationResultTo == null || assignRandomizationResultTo.isEmpty()) {
			assignRandomizationResultTo = "dnfroup";
			dataInfo.setProperty("assignRandomizationResultTo", assignRandomizationResultTo);
		}

		prepareMailProps();
		// setRuleDesignerProps();
		if (dataInfo.getProperty("crfFileExtensions") != null) {
			dataInfo.setProperty("crf_file_extensions", dataInfo.getProperty("crfFileExtensions"));
		}
		if (dataInfo.getProperty("crfFileExtensionSettings") != null) {
			dataInfo.setProperty("crf_file_extension_settings", dataInfo.getProperty("crfFileExtensionSettings"));
		}

		String datasetFileDelete = dataInfo.getProperty("dataset_file_delete");
		if (datasetFileDelete == null) {
			dataInfo.setProperty("dataset_file_delete", "true");
		}

		if (dataInfo.getProperty("maxInactiveInterval") != null) {
			dataInfo.setProperty("max_inactive_interval", dataInfo.getProperty("maxInactiveInterval"));
		}

		dataInfo.setProperty("clinical_research_coordinator", "Clinical_Research_Coordinator");
		dataInfo.setProperty("investigator", "Investigator");
		dataInfo.setProperty("study_director", "Study_Director");

		dataInfo.setProperty("study_administrator", "Study_Administrator");
		dataInfo.setProperty("study_monitor", "Study_Monitor");
		dataInfo.setProperty("ccts.waitBeforeCommit", "6000");

		String rssUrl = dataInfo.getProperty("rssUrl");
		if (rssUrl == null || rssUrl.isEmpty()) {
			rssUrl = "http://clinicalresearch.wordpress.com/feed/";
		}
		dataInfo.setProperty("rss.url", rssUrl);
		String rssMore = dataInfo.getProperty("rssMore");
		if (rssMore == null || rssMore.isEmpty()) {
			rssMore = "http://clinicalresearch.wordpress.com/";
		}
		dataInfo.setProperty("rss.more", rssMore);

		String supportURL = dataInfo.getProperty("supportURL");
		if (supportURL == null || supportURL.isEmpty()) {
			supportURL = "http://www.openclinica.org/OpenClinica/3.0/support/";
		}
		dataInfo.setProperty("supportURL", supportURL);

		dataInfo.setProperty("show_unique_id", "1");

		dataInfo.setProperty("auth_mode", "password");
		if (dataInfo.getProperty("userAccountNotification") != null) {
			dataInfo.setProperty("user_account_notification", dataInfo.getProperty("userAccountNotification"));
		}
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
			LOGGER.debug(ioe.getMessage(), ioe);
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
				LOGGER.debug(ioe.getMessage(), ioe);
				throw new OpenClinicaSystemException("Unable to copy file: " + r.getFilename() + " to "
						+ f.getAbsolutePath(), ioe);

			}
		}
	}

	private void copyImportRulesFiles() throws IOException {
		final int size = 3;
		InputStream[] listSrcFiles = new InputStream[size];
		String[] fileNames = {"rules.xsd", "rules_template.xml", "rules_template_with_notes.xml"};
		listSrcFiles[0] = resourceLoader.getResource("classpath:properties" + File.separator + fileNames[0])
				.getInputStream();
		listSrcFiles[1] = resourceLoader.getResource("classpath:properties" + File.separator + fileNames[1])
				.getInputStream();
		listSrcFiles[2] = resourceLoader.getResource("classpath:properties" + File.separator + fileNames[2])
				.getInputStream();
		File dest = new File(getField("filePath") + "rules");
		if (!dest.exists()) {
			if (!dest.mkdirs()) {
				throw new OpenClinicaSystemException("Copying files, Could not create direcotry: "
						+ dest.getAbsolutePath() + ".");
			}
		}
		for (int i = 0; i < listSrcFiles.length; i++) {
			if (listSrcFiles[i] != null) {
				FileCopyUtils.copy(listSrcFiles[i],
						new FileOutputStream(dest.getPath().concat(File.separator).concat(fileNames[i])));
			}
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
		LOGGER.debug("fis?" + fis);
		try {
			fos = new FileOutputStream(dest);
			while ((bytesRead = fis.read(buffer)) >= 0) {
				fos.write(buffer, 0, bytesRead);
			}
		} catch (IOException ioe) {
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
					LOGGER.debug(ioe.getMessage());
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
					LOGGER.debug(ioe.getMessage());
					throw oe;

				}
			}
		}
	}

	/**
	 * Copy core\resources\properties\cd_odm_mapping.xml to web application resources outside the core jar file.
	 * Reason - During CRF data import, Castor API is not able to load this mapping xml file from core jar file.
	 *
	 * @param resourceLoader ResourceLoader
	 */
	private void copyODMMappingXMLtoResources(ResourceLoader resourceLoader) {

		ByteArrayInputStream[] listSrcFiles = new ByteArrayInputStream[10];
		String[] fileNames = {"cd_odm_mapping.xml"};
		try {
			listSrcFiles[0] = new ByteArrayInputStream(resourceLoader
					.getResource("classpath:properties" + File.separator + "cd_odm_mapping.xml").getURL().getFile()
					.getBytes());

		} catch (IOException ioe) {
			OpenClinicaSystemException oe = new OpenClinicaSystemException("Unable to read source files");
			oe.initCause(ioe);
			oe.setStackTrace(ioe.getStackTrace());
			LOGGER.error(ioe.getMessage());
			throw oe;
		}

		File dest;
		try {
			File placeholderFile = new File(resourceLoader
					.getResource("classpath:org/akaza/openclinica/applicationContext-web-beans.xml").getURL().getFile());

			String placeholderFilePath = URLDecoder.decode(placeholderFile.getPath(), UTF_8);

			String tmp2 = placeholderFilePath.substring(0, placeholderFilePath.indexOf("WEB-INF") - 1);
			String tmp3 = tmp2 + File.separator + "WEB-INF" + File.separator + "classes";
			dest = new File(tmp3 + File.separator + "odm_mapping");

		} catch (IOException ioe) {
			OpenClinicaSystemException oe = new OpenClinicaSystemException("Unable to get web app base path");
			oe.initCause(ioe);
			oe.setStackTrace(ioe.getStackTrace());
			LOGGER.error(ioe.getMessage());
			throw oe;
		}

		if (!dest.exists()) {
			if (!dest.mkdirs()) {
				LOGGER.error("Copying files, Could not create directory: " + dest.getAbsolutePath() + ".");
				throw new OpenClinicaSystemException("Copying files, Could not create directory: "
						+ dest.getAbsolutePath() + ".");
			}
		}

		for (int i = 0; i < fileNames.length; i++) {
			File dest1 = new File(dest, fileNames[i]);
			// File src1 = listSrcFiles[i];
			// switch with IOUtils.copy?
			if (listSrcFiles[i] != null) {
				copyFiles(listSrcFiles[i], dest1);
			}
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
			epbean.setOrder(Integer.parseInt(getExtractField("extract." + i + ".order")));
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
			epbean.setSkipBlanks(getExtractFieldBoolean("extract." + i + ".skipBlanks"));
			epbean.setSuccessMessage(getExtractField("extract." + i + ".success"));
			epbean.setFailureMessage(getExtractField("extract." + i + ".failure"));
			epbean.setZipName(getExtractField("extract." + i + ".zipName"));
			if (epbean.getFileName().length != epbean.getExportFileName().length) {
				throw new OpenClinicaSystemException(
						"The comma seperated values of file names and export file names should correspond 1 on 1 for the property number"
								+ i);
			}

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
				} else {
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
		Collections.sort(ret);
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
			if (!f.exists()) {
				throw new OpenClinicaSystemException("FileNotFound -- Please make sure" + extractFields[i] + "exists");
			}

			i++;

		}

	}

	/**
	 * Returns input stream instance, bind to specified file.
	 *
	 * @param fileName file name
	 * @return InputStream
	 * @throws IOException exception
	 */
	public InputStream getInputStream(String fileName) throws IOException {
		return resourceLoader.getResource("classpath:properties/" + fileName).getInputStream();
	}

	/**
	 * Returns URL of specified file.
	 *
	 * @param fileName file name
	 * @return URL
	 * @throws IOException exception
	 */
	public URL getURL(String fileName) throws IOException {
		return resourceLoader.getResource("classpath:properties/" + fileName).getURL();
	}

	/**
	 * Get file.
	 *
	 * @param fileName file name
	 * @return File
	 * @throws IOException exception
	 * @deprecated Use {@link #getFile(String, String)} instead
	 */
	public File getFile(String fileName) throws IOException {
		return getFile(fileName, "filePath");
	}

	/**
	 * Get file.
	 *
	 * @param fileName     file name
	 * @param relDirectory directory
	 * @return File
	 */
	public File getFile(String fileName, String relDirectory) {
		try {

			getInputStream(fileName);

			return new File(getField("filePath") + relDirectory + fileName);

		} catch (IOException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.fillInStackTrace());
		}
	}

	public static String getDBType() {
		return dataInfo.getProperty("dbType");
	}

	private static String getSystemLanguage() {
		String language = CoreResources.getField("system.language");
		if (language == null || language.trim().isEmpty()) {
			language = "en";
			CoreResources.setField("system.language", language);
		}
		return language;
	}

	public static Locale getSystemLocale() {
		return LocaleUtils.toLocale(getSystemLanguage());
	}

	/**
	 * Get field.
	 *
	 * @param key property key.
	 * @return value of property
	 */
	public static String getField(String key) {
		if (dataInfo == null) {
			return "";
		}
		String value = dataInfo.getProperty(key);
		if (value != null) {
			value = value.trim();
		}
		return value == null ? "" : value;
	}

	/**
	 * Set field.
	 *
	 * @param key   property key
	 * @param value new value
	 */
	public static void setField(String key, String value) {
		if (dataInfo != null && value != null) {
			dataInfo.setProperty(key, value.trim());
		}
	}

	/**
	 * Gets the actual system URL by doing a DNS name lookup repeated code from web to fix #99.
	 *
	 * @return String
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

	/**
	 * Get extract field.
	 *
	 * @param key property key
	 * @return String
	 */
	public static String getExtractField(String key) {
		String value = extractInfo.getProperty(key);
		if (value != null) {
			value = value.trim();
		}
		value = replacePaths(value);
		return value == null ? "" : value;
	}

	/**
	 * Get extract field of type Boolean.
	 *
	 * @param key property key
	 * @return boolean
	 */
	public static boolean getExtractFieldBoolean(String key) {
		String value = extractInfo.getProperty(key);
		if (value != null) {
			value = value.trim();
		}
		return value == null || !value.equalsIgnoreCase("false");

	}

	/**
	 * Get extract field as string array. Comma is used as the delimiter.
	 *
	 * @param key property key
	 * @return string array
	 */
	public static String[] getExtractFields(String key) {
		String value = extractInfo.getProperty(key);

		if (value != null) {
			value = value.trim();
		}
		return value != null ? value.split(",") : new String[0];
	}

	/**
	 * Find extract property by ID.
	 *
	 * @param id        property ID
	 * @param datasetId dataset ID
	 * @return ExtractPropertyBean
	 */
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

	/**
	 * Get webapp name.
	 *
	 * @param servletCtxRealPath servlet context real path
	 * @return String
	 */
	public String getWebAppName(String servletCtxRealPath) {
		String webAppName = "";
		if (null != servletCtxRealPath) {
			String[] tokens = servletCtxRealPath.split("/");
			if (tokens.length > 0) {
				webAppName = tokens[(tokens.length - 1)].trim();
			}
		}
		return webAppName;
	}

	public static void setDomainName(String domain) {
		domainName = domain;
	}

	public static String getDomainName() {
		return domainName;
	}

	public static void setCopyODM(boolean copyODM) {
		CoreResources.copyODM = copyODM;
	}
}
