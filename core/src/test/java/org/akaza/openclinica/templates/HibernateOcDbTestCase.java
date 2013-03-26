/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.templates;

import java.io.File;
import java.util.Locale;
import java.util.Properties;

import javax.sql.DataSource;

import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public abstract class HibernateOcDbTestCase extends DataSourceBasedDBTestCase {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	protected static ApplicationContext context;

	protected static Properties properties = new Properties();
	public static String dbName;
	public static String dbUrl;
	public static String dbUserName;
	public static String dbPassword;
	public static String dbDriverClassName;
	public static String locale;
	public BasicDataSource ds;

	protected static PlatformTransactionManager transactionManager;
	static {

		loadProperties();
		dbName = properties.getProperty("dbName");
		dbUrl = properties.getProperty("url");
		dbUserName = properties.getProperty("username");
		dbPassword = properties.getProperty("password");
		dbDriverClassName = properties.getProperty("driver");
		locale = properties.getProperty("locale");
		initializeLocale();

		context = new ClassPathXmlApplicationContext(new String[] { "classpath*:applicationContext-core-s*.xml",
				"classpath*:org/akaza/openclinica/applicationContext-core-db.xml",
				"classpath*:org/akaza/openclinica/applicationContext-core-email.xml",
				"classpath*:org/akaza/openclinica/applicationContext-core-hibernate.xml",
				"classpath*:org/akaza/openclinica/applicationContext-core-service.xml",
				" classpath*:org/akaza/openclinica/applicationContext-core-timer.xml",
				"classpath*:org/akaza/openclinica/applicationContext-security.xml" });

		transactionManager = (PlatformTransactionManager) context.getBean("transactionManager");
		transactionManager.getTransaction(new DefaultTransactionDefinition());
		initializeQueriesInXml();
	}

	public HibernateOcDbTestCase() {
	}

	@Override
	protected void setUp() throws Exception {

		super.setUp();

	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(HibernateOcDbTestCase.class.getResourceAsStream(getTestDataFilePath()));
	}

	@Override
	public DataSource getDataSource() {
		ds = new BasicDataSource();
		ds.setAccessToUnderlyingConnectionAllowed(true);
		ds.setDriverClassName(dbDriverClassName);
		ds.setUsername(dbUserName);
		ds.setPassword(dbPassword);
		ds.setUrl(dbUrl);
		return ds;
	}

	public ApplicationContext getContext() {
		return context;
	}

	public static void loadProperties() {
		try {
			properties.load(HibernateOcDbTestCase.class.getResourceAsStream(getPropertiesFilePath()));
		} catch (Exception ioExc) {
			ioExc.printStackTrace();
		}
	}

	protected static void initializeLocale() {
		ResourceBundleProvider.updateLocale(new Locale(locale));
	}

	/**
	 * Instantiates SQLFactory and all the xml files that contain the queries that are used in our dao class
	 */
	protected static void initializeQueriesInXml() {
		String baseDir = System.getProperty("user.dir");
		if (baseDir == null || "".equalsIgnoreCase(baseDir)) {
			throw new IllegalStateException(
					"The system properties basedir were not made available to the application. Therefore we cannot locate the test properties file.");
		}
		SQLFactory.JUNIT_XML_DIR = baseDir + File.separator + "src" + File.separator + "main" + File.separator
				+ "resources" + File.separator + "properties" + File.separator;

		SQLFactory.getInstance().run(dbName, context);
	}

	private static String getPropertiesFilePath() {
		return "/test.properties";
	}

	/**
	 * Gets the path and the name of the xml file holding the data. Example if your Class Name is called
	 * org.akaza.openclinica.service.rule.expression.TestExample.java you need an xml data file in resources folder
	 * under same path + testdata + same Class Name .xml
	 * org/akaza/openclinica/service/rule/expression/testdata/TestExample.xml
	 * 
	 * @return path to data file
	 */
	private String getTestDataFilePath() {
		StringBuffer path = new StringBuffer("/");
		path.append(getClass().getPackage().getName().replace(".", "/"));
		path.append("/testdata/");
		path.append(getClass().getSimpleName() + ".xml");
		return path.toString();
	}

	public String getDbName() {
		return dbName;
	}

	@Override
	public void tearDown() {

		try {

			transactionManager.commit(transactionManager.getTransaction(new DefaultTransactionDefinition()));
			super.tearDown();
			if (ds != null)
				ds.getConnection().close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
