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

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.admin.DownloadVersionSpreadSheetServlet;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.domain.technicaladmin.ConfigurationBean;

import java.util.Properties;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

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
		
		Role.COORDINATOR.setDescription(getField("coordinator"));
		Role.STUDYDIRECTOR.setDescription(getField("director"));
		Role.INVESTIGATOR.setDescription(getField("investigator"));
		Role.RESEARCHASSISTANT.setDescription(getField("ra"));
		Role.MONITOR.setDescription(getField("monitor"));

		// The crf/original/CRF Template will be created if not exist.
		String theDir = getField("filePath");
		String dir1 = "crf" + File.separator;
		String dir2 = "original" + File.separator;
		String dirRules = "rules";

		// Creating rules directory if not exist mantis issue 6584.
		if (!(new File(theDir)).isDirectory() || !(new File(dirRules)).isDirectory()) {
			(new File(theDir + dirRules)).mkdirs();
		}

		if (!(new File(theDir)).isDirectory() || !(new File(dir1)).isDirectory() || !(new File(dir2)).isDirectory()) {
			(new File(theDir + dir1 + dir2)).mkdirs();
			copyTemplate(theDir + dir1 + dir2 + DownloadVersionSpreadSheetServlet.CRF_VERSION_TEMPLATE);
		}
		theDir = theDir + dir1 + dir2;
		File excelFile = new File(theDir + DownloadVersionSpreadSheetServlet.CRF_VERSION_TEMPLATE);
		if (!excelFile.isFile()) {
			copyTemplate(theDir);
		}

		// 'passwd_expiration_time' and 'change_passwd_required' are now defined in the database
		// Here the values in the datainfo.properites file (if any) are overridden.
		overridePropertyFromDatabase(configurationDao, "pwd.expiration.days", params, "passwd_expiration_time");
		overridePropertyFromDatabase(configurationDao, "pwd.change.required", params, "change_passwd_required");
	}

	/**
	 * Gets a field value from properties by its key name
	 * 
	 * @param key
	 * @return String The value of field
	 */
	public static String getField(String key) {
		String name = params.getProperty(key);
		if (name != null) {
			name = name.trim();
		}
		return name == null ? "" : name;
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
	 * @return String The value of field
	 */
	public static String getEnterpriseField(String key) {
		String name = null;
		name = entParams.getProperty(key).trim();
		return name == null ? "" : name;
	}

	/**
	 * We return empty String if DBName is not found in params. The only reason why this is done this way is for unit
	 * testing to work properly.
	 * 
	 * EntityDAO uses SQLInitServlet.getDBName().equals("oracle") , This works fine in the Servlet environment because
	 * of this class but in a unit test it does not
	 * 
	 * @author Krikor Krumlian the return portion
	 * 
	 */
	public static String getDBName() {
		String name = params.getProperty("dataBase");
		return name == null ? "" : name;
	}

	public void copyTemplate(String theDir) {
		OutputStream out = null;
		InputStream is = null;
		CoreResources cr = (CoreResources) SpringServletAccess.getApplicationContext(context).getBean("coreResources");
		try {
			is = cr.getInputStream(DownloadVersionSpreadSheetServlet.CRF_VERSION_TEMPLATE);
			File excelOutFile = new File(theDir);
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
			}
		}
	}

	/**
	 * Overrides a configuration in a properties file with a value read from the database.
	 * 
	 * @param configurationDao
	 * @param propertyNameInDatabase
	 * @param properties
	 * @param propertyNameInProperties
	 */
	private void overridePropertyFromDatabase(ConfigurationDao configurationDao, String propertyNameInDatabase,
			Properties properties, String propertyNameInProperties) {
		ConfigurationBean config = configurationDao.findByKey(propertyNameInDatabase);
		if (config != null) {
			properties.setProperty(propertyNameInProperties, config.getValue());
		}
	}
}
