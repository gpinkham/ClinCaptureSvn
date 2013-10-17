package org.akaza.openclinica;

import javax.sql.DataSource;

import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.discrepancy.DnDescriptionDao;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.extract.DatasetDAO;
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
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.service.rule.RulesPostImportContainerService;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.clinovo.dao.CodedItemDAO;
import com.clinovo.dao.DictionaryDAO;
import com.clinovo.dao.TermDAO;
import com.clinovo.service.CodedItemService;
import com.clinovo.service.DictionaryService;
import com.clinovo.service.TermService;

/**
 * To avoid the constant loading of beans from the application context, which can take a lot of memory on the test, we
 * load all the required test beans in this class and re use them in the tests.
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class DefaultAppContextTest extends AbstractContextSentiveTest {

	protected DataSource dataSource = getDataSource();

    protected ItemDAO idao;
    protected CRFDAO crfdao;
    protected StudyDAO studyDAO;
    protected DatasetDAO datasetDAO;
    protected ItemDataDAO itemDataDAO;
    protected ItemFormMetadataDAO imfdao;
    protected CRFVersionDAO crfVersionDao;
	protected StudyEventDAO studyEventDao;
    protected UserAccountDAO userAccountDAO;
    protected StudySubjectDAO studySubjectDAO;
	protected DynamicEventDao dynamicEventDao;
	protected DnDescriptionDao dnDescriptionDao;
	protected StudyGroupClassDAO studyGroupClassDAO;
    protected DiscrepancyNoteDAO discrepancyNoteDAO;
    protected EventDefinitionCRFDAO eventDefinitionCRFDAO;
	protected StudyEventDefinitionDAO studyEventDefinitionDAO;
	
	// DAOS
	@Autowired protected RuleDao ruleDao;
	@Autowired protected RuleSetDao ruleSetDao;
	@Autowired protected AuthoritiesDao authoritiesDao;
	@Autowired protected RuleSetRuleDao ruleSetRuleDao;
    @Autowired protected RuleActionRunLogDao ruleActionRunLogDao;
	
	@Autowired protected RuleSetAuditDao ruleSetAuditDao;
	@Autowired protected ConfigurationDao configurationDao;
	@Autowired protected AuditUserLoginDao auditUserLoginDao;
	@Autowired protected RuleSetRuleAuditDao ruleSetRuleAuditDao;
	@Autowired protected DatabaseChangeLogDao databaseChangeLogDao;
	
	@Autowired protected TermDAO termDAO;
	@Autowired protected CodedItemDAO codedItemDAO;
	@Autowired protected DictionaryDAO dictionaryDAO;
	
	// Services
	@Autowired protected TermService termService;
	@Autowired protected CodedItemService codedItemService;
	@Autowired protected DictionaryService dictionaryService;
	@Autowired protected RuleSetServiceInterface ruleSetService;
	@Autowired protected RulesPostImportContainerService postImportContainerService;
	

	@Before
	public void initializeDAOs() throws Exception {
		
		super.setUp();
		
		// DAO that require data source
        idao = new ItemDAO(dataSource);
        crfdao = new CRFDAO(dataSource);
        studyDAO = new StudyDAO(dataSource);
        datasetDAO = new DatasetDAO(dataSource);
        itemDataDAO = new ItemDataDAO(dataSource);
        imfdao = new ItemFormMetadataDAO(dataSource);
		studyEventDao = new StudyEventDAO(dataSource);
		crfVersionDao = new CRFVersionDAO(dataSource);
        userAccountDAO = new UserAccountDAO(dataSource);
		dynamicEventDao = new DynamicEventDao(dataSource);
        studySubjectDAO = new StudySubjectDAO(dataSource);
        dnDescriptionDao = new DnDescriptionDao(dataSource);
		discrepancyNoteDAO = new DiscrepancyNoteDAO(dataSource);
		studyGroupClassDAO = new StudyGroupClassDAO(dataSource);
        eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
	}
}
