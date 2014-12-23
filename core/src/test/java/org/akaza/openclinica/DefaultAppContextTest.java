package org.akaza.openclinica;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.extract.OdmExtractDAO;
import org.akaza.openclinica.dao.hibernate.PasswordRequirementsDao;
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
import org.akaza.openclinica.service.EventService;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.service.rule.RulesPostImportContainerService;
import org.hibernate.classic.Session;
import org.junit.Before;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * To avoid the constant loading of beans from the application context, which can take a lot of memory on the test, we
 * load all the required test beans in this class and re use them in the tests.
 */
@SuppressWarnings("rawtypes")
public abstract class DefaultAppContextTest extends AbstractContextSentiveTest {

	private static boolean initialized;

	@Before
	public void initialization() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		super.setUp();
		if (!initialized) {
			initialized = true;
			// DAO that require data source
			auditDao = new AuditDAO(dataSource);
			idao = new ItemDAO(dataSource);
			crfdao = new CRFDAO(dataSource);
			eventCRFDAO = new EventCRFDAO(dataSource);
			studyDAO = new StudyDAO(dataSource);
			sectionDAO = new SectionDAO(dataSource);
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
			itemGroupMetadataDAO = new ItemGroupMetadataDAO(dataSource);
			eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
			studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
			requirementsDao = new PasswordRequirementsDao(configurationDao);
			discrepancyNoteService = new DiscrepancyNoteService(dataSource);
			eventService = new EventService(dataSource);

			postImportContainerService = new RulesPostImportContainerService(dataSource, null, null, ruleDao,
					ruleSetDao, ResourceBundleProvider.getPageMessagesBundle(new Locale(locale)));
			postImportContainerService.setRuleDao(ruleDao);
			postImportContainerService.setRuleSetDao(ruleSetDao);

			auditDao.execute("delete from audit_log_event where audit_id > 0", new HashMap());

			Session session = sessionFactory.getCurrentSession();
			if (dbDriverClassName.contains(POSTGRESQL)) {
				Integer max = (Integer) session.createSQLQuery("SELECT max(discrepancy_note_id) from discrepancy_note")
						.uniqueResult();
				session.createSQLQuery(
						"ALTER SEQUENCE discrepancy_note_discrepancy_note_id_seq RESTART WITH " + (max + 1))
						.executeUpdate();

				max = (Integer) session.createSQLQuery("SELECT max(dynamic_event_id) from dynamic_event")
						.uniqueResult();
				session.createSQLQuery("ALTER SEQUENCE dynamic_event_dynamic_event_id_seq RESTART WITH " + (max + 1))
						.executeUpdate();

				max = (Integer) session.createSQLQuery("SELECT max(event_crf_id) from event_crf").uniqueResult();
				session.createSQLQuery("ALTER SEQUENCE event_crf_event_crf_id_seq RESTART WITH " + (max + 1))
						.executeUpdate();

				max = (Integer) session.createSQLQuery("SELECT max(study_group_class_id) from study_group_class")
						.uniqueResult();
				session.createSQLQuery(
						"ALTER SEQUENCE study_group_class_study_group_class_id_seq RESTART WITH " + (max + 1))
						.executeUpdate();

				max = (Integer) session.createSQLQuery("SELECT max(id) from dictionary").uniqueResult();
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
				session.createSQLQuery("ALTER SEQUENCE widgets_layout_id_seq RESTART WITH " + (max + 1))
						.executeUpdate();

				max = (Integer) session.createSQLQuery("SELECT max(crf_id) from crf").uniqueResult();
				session.createSQLQuery("ALTER SEQUENCE crf_crf_id_seq RESTART WITH " + (max + 1)).executeUpdate();

				max = (Integer) session.createSQLQuery("SELECT max(item_data_id) from item_data").uniqueResult();
				session.createSQLQuery("ALTER SEQUENCE item_data_item_data_id_seq RESTART WITH " + (max + 1))
						.executeUpdate();

			} else if (dbDriverClassName.contains(ORACLE)) {
				Integer max = (Integer) session.createSQLQuery("SELECT max(discrepancy_note_id) from discrepancy_note")
						.uniqueResult();
				session.createSQLQuery("DROP SEQUENCE discrepancy_note_discrepancy_note_id_seq").executeUpdate();
				session.createSQLQuery(
						"CREATE SEQUENCE discrepancy_note_discrepancy_note_id_seq START WITH " + (max + 1)
								+ " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20").executeUpdate();

				max = (Integer) session.createSQLQuery("SELECT max(dynamic_event_id) from dynamic_event")
						.uniqueResult();
				session.createSQLQuery("DROP SEQUENCE dynamic_event_dynamic_event_id_seq").executeUpdate();
				session.createSQLQuery(
						"CREATE SEQUENCE dynamic_event_dynamic_event_id_seq START WITH " + (max + 1)
								+ " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20").executeUpdate();

				max = (Integer) session.createSQLQuery("SELECT max(event_crf_id) from event_crf").uniqueResult();
				session.createSQLQuery("DROP SEQUENCE event_crf_event_crf_id_seq").executeUpdate();
				session.createSQLQuery(
						"CREATE SEQUENCE event_crf_event_crf_id_seq START WITH " + (max + 1)
								+ " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20").executeUpdate();

				max = (Integer) session.createSQLQuery("SELECT max(study_group_class_id) from study_group_class")
						.uniqueResult();
				session.createSQLQuery("DROP SEQUENCE study_group_class_study_group_class_id_seq").executeUpdate();
				session.createSQLQuery(
						"CREATE SEQUENCE study_group_class_study_group_class_id_seq START WITH " + (max + 1)
								+ " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20").executeUpdate();

				max = (Integer) session.createSQLQuery("SELECT max(id) from dictionary").uniqueResult();
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

				max = (Integer) session.createSQLQuery("SELECT max(crf_id) from crf").uniqueResult();
				session.createSQLQuery("DROP SEQUENCE crf_crf_id_seq").executeUpdate();
				session.createSQLQuery(
						"CREATE SEQUENCE crf_crf_id_seq START WITH " + (max + 1)
								+ " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20").executeUpdate();

				max = (Integer) session.createSQLQuery("SELECT max(item_data_id) from item_data").uniqueResult();
				session.createSQLQuery("DROP SEQUENCE item_data_item_data_id_seq").executeUpdate();
				session.createSQLQuery(
						"CREATE SEQUENCE item_data_item_data_id_seq START WITH " + (max + 1)
								+ " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20").executeUpdate();
			}

			UserAccountBean ub = (UserAccountBean) userAccountDAO.findByPK(1);
			ub.setPasswdTimestamp(new Date());
			userAccountDAO.update(ub);
		}
	}
}
