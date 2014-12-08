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
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica;

import com.clinovo.dao.CodedItemDAO;
import com.clinovo.dao.DictionaryDAO;
import com.clinovo.dao.DiscrepancyDescriptionDAO;
import com.clinovo.dao.StudySubjectIdDAO;
import com.clinovo.dao.SystemDAO;
import com.clinovo.dao.TermDAO;
import com.clinovo.dao.WidgetDAO;
import com.clinovo.dao.WidgetsLayoutDAO;
import com.clinovo.service.CodedItemService;
import com.clinovo.service.DataEntryService;
import com.clinovo.service.DictionaryService;
import com.clinovo.service.DiscrepancyDescriptionService;
import com.clinovo.service.ReportCRFService;
import com.clinovo.service.StudySubjectIdService;
import com.clinovo.service.SystemService;
import com.clinovo.service.TermService;
import com.clinovo.service.WidgetService;
import com.clinovo.service.WidgetsLayoutService;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.extract.OdmExtractDAO;
import org.akaza.openclinica.dao.hibernate.AuditUserLoginDao;
import org.akaza.openclinica.dao.hibernate.AuthoritiesDao;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.hibernate.DatabaseChangeLogDao;
import org.akaza.openclinica.dao.hibernate.PasswordRequirementsDao;
import org.akaza.openclinica.dao.hibernate.RuleActionRunLogDao;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetAuditDao;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleAuditDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.EventServiceInterface;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.service.rule.RulesPostImportContainerService;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.hibernate.SessionFactory;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@ContextConfiguration(locations = { "classpath*:applicationContext-core-spring.xml",
		"classpath*:org/akaza/openclinica/applicationContext-core-db.xml",
		"classpath*:org/akaza/openclinica/applicationContext-core-email.xml",
		"classpath*:org/akaza/openclinica/applicationContext-core-hibernate.xml",
		"classpath*:org/akaza/openclinica/applicationContext-core-scheduler.xml",
		"classpath*:org/akaza/openclinica/applicationContext-core-service.xml",
		"classpath*:org/akaza/openclinica/applicationContext-security.xml" })
public abstract class AbstractContextSentiveTest extends DataSourceBasedDBTestCase {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractContextSentiveTest.class);

	public static final String ORACLE = "oracle";
	public static final String POSTGRESQL = "postgresql";

	protected static Properties properties = new Properties();

	public static String dbName;
	public static String dbUrl;
	public static String dbUserName;
	public static String dbPassword;
	public static String dbDriverClassName;
	public static String locale;

	protected static DataSource dataSource;

	protected static AuditDAO auditDao;
	protected static ItemDAO idao;
	protected static CRFDAO crfdao;
	protected static EventCRFDAO eventCRFDAO;
	protected static StudyDAO studyDAO;
	protected static SectionDAO sectionDAO;
	protected static DatasetDAO datasetDAO;
	protected static ItemDataDAO itemDataDAO;
	protected static ItemFormMetadataDAO imfdao;
	protected static OdmExtractDAO odmExtractDAO;
	protected static CRFVersionDAO crfVersionDao;
	protected static StudyEventDAO studyEventDao;
	protected static UserAccountDAO userAccountDAO;
	protected static StudySubjectDAO studySubjectDAO;
	protected static DynamicEventDao dynamicEventDao;
	protected static StudyGroupClassDAO studyGroupClassDAO;
	protected static DiscrepancyNoteDAO discrepancyNoteDAO;
	protected static ItemGroupMetadataDAO itemGroupMetadataDAO;
	protected static EventDefinitionCRFDAO eventDefinitionCRFDAO;
	protected static StudyEventDefinitionDAO studyEventDefinitionDAO;
	protected static RulesPostImportContainerService postImportContainerService;
	protected static PasswordRequirementsDao requirementsDao;
	protected static EventServiceInterface eventService;
	protected static DiscrepancyNoteService discrepancyNoteService;

	// DAOS
	@Autowired
	protected RuleDao ruleDao;
	@Autowired
	protected RuleSetDao ruleSetDao;
	@Autowired
	protected AuthoritiesDao authoritiesDao;
	@Autowired
	protected RuleSetRuleDao ruleSetRuleDao;
	@Autowired
	protected RuleActionRunLogDao ruleActionRunLogDao;

	@Autowired
	protected RuleSetAuditDao ruleSetAuditDao;
	@Autowired
	protected ConfigurationDao configurationDao;
	@Autowired
	protected AuditUserLoginDao auditUserLoginDao;
	@Autowired
	protected RuleSetRuleAuditDao ruleSetRuleAuditDao;
	@Autowired
	protected DatabaseChangeLogDao databaseChangeLogDao;
	@Autowired
	protected DiscrepancyDescriptionDAO discrepancyDescriptionDAO;

	@Autowired
	protected TermDAO termDAO;
	@Autowired
	protected CodedItemDAO codedItemDAO;
	@Autowired
	protected DictionaryDAO dictionaryDAO;
	@Autowired
	protected StudySubjectIdDAO studySubjectIdDAO;
	@Autowired
	protected SystemDAO systemDAO;
	@Autowired
	protected WidgetDAO widgetDAO;
	@Autowired
	protected WidgetsLayoutDAO widgetsLayoutDAO;

	// Services
	@Autowired
	protected DiscrepancyDescriptionService discrepancyDescriptionService;
	@Autowired
	protected TermService termService;
	@Autowired
	protected CodedItemService codedItemService;
	@Autowired
	protected DictionaryService dictionaryService;
	@Autowired
	protected StudySubjectIdService studySubjectIdService;
	@Autowired
	protected SystemService systemService;
	@Autowired
	protected RuleSetServiceInterface ruleSetService;
	@Autowired
	protected WidgetService widgetService;
	@Autowired
	protected WidgetsLayoutService widgetsLayoutService;
	@Autowired
	protected DataEntryService dataEntryService;
	@Autowired
	protected ReportCRFService reportCRFService;
	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected SessionFactory sessionFactory;

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

		if (dataSource == null) {
			BasicDataSource ds = new BasicDataSource();
			ds.setAccessToUnderlyingConnectionAllowed(true);
			ds.setDriverClassName(dbDriverClassName);
			ds.setUsername(dbUserName);
			ds.setPassword(dbPassword);
			ds.setUrl(dbUrl);
			dataSource = ds;
		}
	}

	@Override
	protected IDataSet getDataSet() throws Exception {

		InputStream resource = AbstractContextSentiveTest.class.getResourceAsStream(getTestDataFilePath());
		FlatXmlDataSet flatXmlDataSet = new FlatXmlDataSet(resource);
		return flatXmlDataSet;
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	public static void loadProperties() {
		try {
			properties.load(AbstractContextSentiveTest.class.getResourceAsStream(getPropertiesFilePath()));
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}

	protected static void initializeLocale() {
		ResourceBundleProvider.updateLocale(new Locale(locale));
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
		return "/com/clinovo/dataset.xml";
	}

	public String getDbName() {
		return dbName;
	}

	@Override
	public void tearDown() {

		try {

			transactionManager.commit(transactionManager.getTransaction(new DefaultTransactionDefinition()));
			super.tearDown();
			if (dataSource != null)
				dataSource.getConnection().close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
