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
import com.clinovo.service.DictionaryService;
import com.clinovo.service.DiscrepancyDescriptionService;
import com.clinovo.service.StudySubjectIdService;
import com.clinovo.service.SystemService;
import com.clinovo.service.TermService;
import com.clinovo.service.WidgetService;
import com.clinovo.service.WidgetsLayoutService;

import java.util.Date;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.extract.OdmExtractDAO;
import org.akaza.openclinica.dao.hibernate.AuditUserLoginDao;
import org.akaza.openclinica.dao.hibernate.AuthoritiesDao;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.hibernate.DatabaseChangeLogDao;
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
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.service.rule.RulesPostImportContainerService;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * To avoid the constant loading of beans from the application context, which can take a lot of memory on the test, we
 * load all the required test beans in this class and re use them in the tests.
 */
@SuppressWarnings("rawtypes")
public abstract class DefaultAppContextTest extends AbstractContextSentiveTest {

	public static final String POSTGRESQL = "postgresql";
	public static final String ORACLE = "oracle";
	protected DataSource dataSource = getDataSource();

	protected ItemDAO idao;
	protected CRFDAO crfdao;
	protected EventCRFDAO eventCRFDAO;
	protected StudyDAO studyDAO;
	protected DatasetDAO datasetDAO;
	protected ItemDataDAO itemDataDAO;
	protected ItemFormMetadataDAO imfdao;
	protected OdmExtractDAO odmExtractDAO;
	protected CRFVersionDAO crfVersionDao;
	protected StudyEventDAO studyEventDao;
	protected UserAccountDAO userAccountDAO;
	protected StudySubjectDAO studySubjectDAO;
	protected DynamicEventDao dynamicEventDao;
	protected StudyGroupClassDAO studyGroupClassDAO;
	protected DiscrepancyNoteDAO discrepancyNoteDAO;
	protected EventDefinitionCRFDAO eventDefinitionCRFDAO;
	protected StudyEventDefinitionDAO studyEventDefinitionDAO;
	protected RulesPostImportContainerService postImportContainerService;

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
	private SessionFactory sessionFactory;

	@Before
	public void initializeDAOs() throws Exception {

		super.setUp();

		// DAO that require data source
		idao = new ItemDAO(dataSource);
		crfdao = new CRFDAO(dataSource);
		eventCRFDAO = new EventCRFDAO(dataSource);
		studyDAO = new StudyDAO(dataSource);
		datasetDAO = new DatasetDAO(dataSource);
		itemDataDAO = new ItemDataDAO(dataSource);
		imfdao = new ItemFormMetadataDAO(dataSource);
		odmExtractDAO = new OdmExtractDAO(dataSource);
		studyEventDao = new StudyEventDAO(dataSource);
		crfVersionDao = new CRFVersionDAO(dataSource);
		userAccountDAO = new UserAccountDAO(dataSource);
		dynamicEventDao = new DynamicEventDao(dataSource);
		studySubjectDAO = new StudySubjectDAO(dataSource);
		discrepancyNoteDAO = new DiscrepancyNoteDAO(dataSource);
		studyGroupClassDAO = new StudyGroupClassDAO(dataSource);
		eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);

		postImportContainerService = new RulesPostImportContainerService(dataSource);
		postImportContainerService.setRuleDao(ruleDao);
		postImportContainerService.setRuleSetDao(ruleSetDao);

		Session session = sessionFactory.getCurrentSession();
		if (dbDriverClassName.contains(POSTGRESQL)) {
			Integer max = (Integer) session.createSQLQuery("SELECT max(id) from dictionary").uniqueResult();
			session.createSQLQuery("ALTER SEQUENCE dictionary_id_seq RESTART WITH " + (max + 1)).executeUpdate();

			max = (Integer) session.createSQLQuery("SELECT max(id) from term").uniqueResult();
			session.createSQLQuery("ALTER SEQUENCE term_id_seq RESTART WITH " + (max + 1)).executeUpdate();

			max = (Integer) session.createSQLQuery("SELECT max(id) from coded_item").uniqueResult();
			session.createSQLQuery("ALTER SEQUENCE coded_item_id_seq RESTART WITH " + (max + 1)).executeUpdate();

			max = (Integer) session.createSQLQuery("SELECT max(id) from coded_item_element").uniqueResult();
			session.createSQLQuery("ALTER SEQUENCE coded_item_element_id_seq RESTART WITH " + (max + 1))
					.executeUpdate();

			max = (Integer) session.createSQLQuery("SELECT max(id) from widget").uniqueResult();
			session.createSQLQuery("ALTER SEQUENCE widget_id_seq RESTART WITH " + (max + 1)).executeUpdate();

			max = (Integer) session.createSQLQuery("SELECT max(id) from widgets_layout").uniqueResult();
			session.createSQLQuery("ALTER SEQUENCE widgets_layout_id_seq RESTART WITH " + (max + 1)).executeUpdate();
			
		} else if (dbDriverClassName.contains(ORACLE)) {
			Integer max = (Integer) session.createSQLQuery("SELECT max(id) from dictionary").uniqueResult();
			session.createSQLQuery("DROP SEQUENCE dictionary_id_seq").executeUpdate();
			session.createSQLQuery(
					"CREATE SEQUENCE dictionary_id_seq START WITH " + (max + 1)
							+ " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20").executeUpdate();

			max = (Integer) session.createSQLQuery("SELECT max(id) from term").uniqueResult();
			session.createSQLQuery("DROP SEQUENCE term_id_seq").executeUpdate();
			session.createSQLQuery(
					"CREATE SEQUENCE term_id_seq START WITH " + (max + 1)
							+ " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20").executeUpdate();

			max = (Integer) session.createSQLQuery("SELECT max(id) from coded_item").uniqueResult();
			session.createSQLQuery("DROP SEQUENCE coded_item_id_seq").executeUpdate();
			session.createSQLQuery(
					"CREATE SEQUENCE coded_item_id_seq START WITH " + (max + 1)
							+ " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20").executeUpdate();

			max = (Integer) session.createSQLQuery("SELECT max(id) from coded_item_element").uniqueResult();
			session.createSQLQuery("DROP SEQUENCE coded_item_element_id_seq").executeUpdate();
			session.createSQLQuery(
					"CREATE SEQUENCE coded_item_element_id_seq START WITH " + (max + 1)
							+ " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20").executeUpdate();

			max = (Integer) session.createSQLQuery("SELECT max(id) from widgets_layout").uniqueResult();
			session.createSQLQuery("DROP SEQUENCE widgets_layout_id_seq").executeUpdate();
			session.createSQLQuery(
					"CREATE SEQUENCE widgets_layout_id_seq START WITH " + (max + 1)
							+ " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20").executeUpdate();

			max = (Integer) session.createSQLQuery("SELECT max(id) from widget").uniqueResult();
			session.createSQLQuery("DROP SEQUENCE widget_id_seq").executeUpdate();
			session.createSQLQuery(
					"CREATE SEQUENCE widget_id_seq START WITH " + (max + 1)
							+ " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20").executeUpdate();
		}

		UserAccountBean ub = (UserAccountBean) userAccountDAO.findByPK(1);
		ub.setPasswdTimestamp(new Date());
		userAccountDAO.update(ub);
	}
}
