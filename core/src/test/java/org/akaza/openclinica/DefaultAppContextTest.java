package org.akaza.openclinica;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
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
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.EventService;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.akaza.openclinica.service.rule.RulesPostImportContainerService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.junit.Before;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * To avoid the constant loading of beans from the application context, which can take a lot of memory on the test, we
 * load all the required test beans in this class and re use them in the tests.
 */
@SuppressWarnings("rawtypes")
public abstract class DefaultAppContextTest extends AbstractContextSentiveTest {

	private static boolean initialized;

	protected void restoreDb() throws Exception {
		super.setUp();
	}

	@Before
	public void initialization() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		restoreDb();
		if (!initialized) {
			initialized = true;
			// DAO that require data source
			auditDao = new AuditDAO(dataSource);
			subjectDAO = new SubjectDAO(dataSource);
			auditEventDAO = new AuditEventDAO(dataSource);
			subjectGroupMapDAO = new SubjectGroupMapDAO(dataSource);
			studyGroupDAO = new StudyGroupDAO(dataSource);
			studyParameterValueDAO = new StudyParameterValueDAO(dataSource);
			idao = new ItemDAO(dataSource);
			crfdao = new CRFDAO(dataSource);
			eventCRFDAO = new EventCRFDAO(dataSource);
			studyDAO = new StudyDAO(dataSource);
			sectionDAO = new SectionDAO(dataSource);
			datasetDAO = new DatasetDAO(dataSource);
			itemDataDAO = new ItemDataDAO(dataSource);
			imfdao = new ItemFormMetadataDAO(dataSource);
			itgdao = new ItemGroupDAO(dataSource);
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
			ruleSetService = new RuleSetService(dataSource, dynamicsItemFormMetadataDao, dynamicsItemGroupMetadataDao,
					mailSender, ruleDao, ruleSetDao, ruleSetRuleDao, ruleSetAuditDao, ruleActionRunLogDao);
			postImportContainerService = new RulesPostImportContainerService(dataSource, null, null, ruleDao,
					ruleSetDao, ResourceBundleProvider.getPageMessagesBundle(new Locale(locale)));
			postImportContainerService.setRuleDao(ruleDao);
			postImportContainerService.setRuleSetDao(ruleSetDao);

			auditDao.execute("delete from audit_log_event where audit_id > 0", new HashMap());

			Session session = sessionFactory.getCurrentSession();

			resetSequence(session, "id", "event_crf_section");
			resetSequence(session, "id", "term");
			resetSequence(session, "id", "widget");
			resetSequence(session, "crf_id", "crf");
			resetSequence(session, "item_id", "item");
			resetSequence(session, "id", "dictionary");
			resetSequence(session, "id", "coded_item");
			resetSequence(session, "id", "widgets_layout");
			resetSequence(session, "section_id", "section");
			resetSequence(session, "subject_id", "subject");
			resetSequence(session, "id", "edc_item_metadata");
			resetSequence(session, "id", "scd_item_metadata");
			resetSequence(session, "id", "coded_item_element");
			resetSequence(session, "item_data_id", "item_data");
			resetSequence(session, "event_crf_id", "event_crf");
			resetSequence(session, "id", "item_render_metadata");
			resetSequence(session, "item_group_id", "item_group");
			resetSequence(session, "study_group_id", "study_group");
			resetSequence(session, "crf_version_id", "crf_version");
			resetSequence(session, "study_event_id", "study_event");
			resetSequence(session, "response_set_id", "response_set");
			resetSequence(session, "dynamic_event_id", "dynamic_event");
			resetSequence(session, "study_subject_id", "study_subject");
			resetSequence(session, "discrepancy_note_id", "discrepancy_note");
			resetSequence(session, "subject_group_map_id", "subject_group_map");
			resetSequence(session, "study_group_class_id", "study_group_class");
			resetSequence(session, "item_form_metadata_id", "item_form_metadata");
			resetSequence(session, "item_group_metadata_id", "item_group_metadata");
			resetSequence(session, "event_definition_crf_id", "event_definition_crf");
			resetSequence(session, "study_event_definition_id", "study_event_definition");

			UserAccountBean ub = (UserAccountBean) userAccountDAO.findByPK(1);
			ub.setPasswdTimestamp(new Date());
			userAccountDAO.update(ub);
		}
	}

	private void resetSequence(Session session, String idField, String tableName) {
		resetSequence(session, idField, tableName, tableName.concat("_").concat(idField).concat("_seq"));
	}

	private void resetSequence(Session session, String idField, String tableName, String sequenceName) {
		Integer max;
		if (dbDriverClassName.contains(POSTGRESQL)) {
			max = (Integer) session.createSQLQuery("SELECT max(".concat(idField).concat(") from ").concat(tableName))
					.uniqueResult();
			session.createSQLQuery(
					"ALTER SEQUENCE ".concat(sequenceName).concat(" RESTART WITH " + ((max == null ? 0 : max) + 1)))
					.executeUpdate();
		}
		if (dbDriverClassName.contains(ORACLE)) {
			max = (Integer) session.createSQLQuery("SELECT max(".concat(idField).concat(") from ").concat(tableName))
					.uniqueResult();
			session.createSQLQuery("DROP SEQUENCE ".concat(sequenceName)).executeUpdate();
			session.createSQLQuery("CREATE SEQUENCE ".concat(sequenceName).concat(
					" START WITH " + ((max == null ? 0 : max) + 1) + " INCREMENT BY 1 NOMAXVALUE NOCYCLE CACHE 20"))
					.executeUpdate();
		}
	}

	protected Workbook getWorkbook(String fileName) throws Exception {
		InputStream inputStream = null;
		boolean isXlsx = fileName.toLowerCase().endsWith(".xlsx");
		try {
			inputStream = new DefaultResourceLoader().getResource("data/excel/".concat(fileName)).getInputStream();
			return !isXlsx ? new HSSFWorkbook(new POIFSFileSystem(inputStream)) : new XSSFWorkbook(inputStream);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception ex) {
				//
			}
		}
	}

	protected String getJsonData(String fileName) throws Exception {
		InputStream inputStream = null;
		try {
			inputStream = new DefaultResourceLoader().getResource("data/json/".concat(fileName)).getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			return out.toString();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception ex) {
				//
			}
		}
	}
}
