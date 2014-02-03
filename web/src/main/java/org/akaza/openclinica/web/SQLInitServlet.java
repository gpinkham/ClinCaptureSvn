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
package org.akaza.openclinica.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.admin.DownloadVersionSpreadSheetServlet;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.domain.technicaladmin.ConfigurationBean;

/**
 * <P>
 * <b>SqlInitServlet.java </b>, servlet designed to run on startup, gathers all the SQL queries and stores them in
 * memory. Runs the static object SqlFactory, which reads the properties file and then processes all the DAO-based XML
 * files.
 * 
 * @author thickerson
 * 
 * 
 */
public class SQLInitServlet extends HttpServlet {

	private static final long serialVersionUID = -7762647189740187841L;

	private ServletContext context;

	private static Properties params = new Properties();
	private static Properties entParams = new Properties();

	@Override
	public void init() throws ServletException {
		context = getServletContext();

		params = (Properties) SpringServletAccess.getApplicationContext(context).getBean("dataInfo");
		entParams = (Properties) SpringServletAccess.getApplicationContext(context).getBean("enterpriseInfo");

		ConfigurationDao configurationDao = SpringServletAccess.getApplicationContext(context).getBean(
				ConfigurationDao.class);

		Role.STUDY_ADMINISTRATOR.setDescription(getField("study_administrator"));
		Role.STUDY_DIRECTOR.setDescription(getField("study_director"));
		Role.INVESTIGATOR.setDescription(getField("investigator"));
		Role.CLINICAL_RESEARCH_COORDINATOR.setDescription(getField("clinical_research_coordinator"));
		Role.STUDY_MONITOR.setDescription(getField("study_monitor"));

		String ruleDirectory = "rules";
		String rootDirectory = getField("filePath");
		String crfDirectory = "crf" + File.separator;
		String crfOriginalDirectory = "original" + File.separator;

		// Creating rules directory if not exist
		if (!(new File(rootDirectory)).isDirectory() || !(new File(ruleDirectory)).isDirectory()) {
			(new File(rootDirectory + ruleDirectory)).mkdirs();
		}

		if (!(new File(rootDirectory)).isDirectory() || !(new File(crfDirectory)).isDirectory()
				|| !(new File(crfOriginalDirectory)).isDirectory()) {

			(new File(rootDirectory + crfDirectory + crfOriginalDirectory)).mkdirs();
			copyTemplate(rootDirectory + crfDirectory + crfOriginalDirectory,
					DownloadVersionSpreadSheetServlet.CRF_VERSION_TEMPLATE);
			copyTemplate(rootDirectory + crfDirectory + crfOriginalDirectory,
					DownloadVersionSpreadSheetServlet.RANDOMIZATION_CRF_TEMPLATE);
		}

		rootDirectory = rootDirectory + crfDirectory + crfOriginalDirectory;

		File crfTemplate = new File(rootDirectory + DownloadVersionSpreadSheetServlet.CRF_VERSION_TEMPLATE);
		File randomizationFormTemplate = new File(rootDirectory
				+ DownloadVersionSpreadSheetServlet.RANDOMIZATION_CRF_TEMPLATE);

		if (!crfTemplate.isFile() || !randomizationFormTemplate.isFile()) {

			copyTemplate(rootDirectory, DownloadVersionSpreadSheetServlet.CRF_VERSION_TEMPLATE);
			copyTemplate(rootDirectory, DownloadVersionSpreadSheetServlet.RANDOMIZATION_CRF_TEMPLATE);
		}

		overridePropertyFromDatabase(configurationDao, "pwd.expiration.days", params);
		overridePropertyFromDatabase(configurationDao, "pwd.change.required", params);
	}

	public static void updateParams(Properties params) {
		SQLInitServlet.params = params;
	}

	/**
	 * Gets a field value from properties by its key name
	 * 
	 * @param key
	 *            String
	 * @return String The value of field
	 */
	public static String getField(String key) {
		String name = params.getProperty(key);
		if (name != null) {
			name = name.trim();
		}
		return name == null ? "" : name;
	}

	public static void setField(String key, String value) {
		if (params != null && value != null) {
			params.setProperty(key, value.trim());
		}
	}

	/**
	 * Gets the supportURL value from properties by its key name
	 * 
	 * @return String The value of supportURL key
	 */
	public static String getSupportURL() {
		String name = params.getProperty("supportURL");
		return name == null ? "" : name.trim();
	}

	/**
	 * gets the actual system URL by doing a DNS name lookup
	 */
	public static String getSystemURL() {
		String url = params.getProperty("sysURL.base");
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
	 * Gets a field value by its key name from the enterprise.properties file
	 * 
	 * @param key
	 *            String
	 * @return String The value of field
	 */
	public static String getEnterpriseField(String key) {
		String name = entParams.getProperty(key);
		return name == null ? "" : name.trim();
	}

	/**
	 * We return empty String if DBName is not found in params. The only reason why this is done this way is for unit
	 * testing to work properly.
	 * 
	 * EntityDAO uses SQLInitServlet.getDBType().equals("oracle") , This works fine in the Servlet environment because
	 * of this class but in a unit test it does not
	 * 
	 * author Krikor Krumlian the return portion
	 * 
	 */
	public static String getDBType() {
		String dbType = params.getProperty("dbType");
		return dbType == null ? "" : dbType;
	}

	public void copyTemplate(String theDir, String fileName) {

		OutputStream out = null;
		InputStream is = null;
		CoreResources cr = (CoreResources) SpringServletAccess.getApplicationContext(context).getBean("coreResources");
		try {
			is = cr.getInputStream(fileName);
			File excelOutFile = new File(theDir + fileName);
			out = new FileOutputStream(excelOutFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				is.close();
				out.close();
			} catch (Exception e) {
				//
			}
		}
	}

	/**
	 * Overrides a configuration in a properties file with a value read from the database.
	 * 
	 * @param configurationDao
	 *            ConfigurationDao
	 * @param propertyNameInDatabase
	 *            String
	 * @param properties
	 *            Properties
	 */
	private void overridePropertyFromDatabase(ConfigurationDao configurationDao, String propertyNameInDatabase,
			Properties properties) {
		ConfigurationBean config = configurationDao.findByKey(propertyNameInDatabase);
		if (config != null) {
			properties.setProperty(propertyNameInDatabase, config.getValue());
			CoreResources.setField(propertyNameInDatabase, config.getValue());
		}
	}
}
