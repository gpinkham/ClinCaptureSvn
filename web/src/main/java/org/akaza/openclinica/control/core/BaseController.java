package org.akaza.openclinica.control.core;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.clinovo.lib.crf.factory.CrfBuilderFactory;
import com.clinovo.service.DeleteCrfService;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.hibernate.AuditUserLoginDao;
import org.akaza.openclinica.dao.hibernate.AuthoritiesDao;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.hibernate.DatabaseChangeLogDao;
import org.akaza.openclinica.dao.hibernate.DynamicsItemFormMetadataDao;
import org.akaza.openclinica.dao.hibernate.DynamicsItemGroupMetadataDao;
import org.akaza.openclinica.dao.hibernate.MeasurementUnitDao;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetAuditDao;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleAuditDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.dao.hibernate.SCDItemMetadataDao;
import org.akaza.openclinica.dao.hibernate.UsageStatsServiceDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
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
import org.akaza.openclinica.job.OpenClinicaSchedulerFactoryBean;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.service.crfdata.SimpleConditionalDisplayService;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.filter.OpenClinicaJdbcService;
import org.akaza.openclinica.web.table.sdv.SDVUtil;
import org.quartz.impl.StdScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;

import com.clinovo.dao.SystemDAO;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.CRFMaskingService;
import com.clinovo.service.CodedItemService;
import com.clinovo.service.DatasetService;
import com.clinovo.service.DcfService;
import com.clinovo.service.DictionaryService;
import com.clinovo.service.DiscrepancyDescriptionService;
import com.clinovo.service.EventCRFService;
import com.clinovo.service.EventDefinitionCrfService;
import com.clinovo.service.EventDefinitionService;
import com.clinovo.service.ItemSDVService;
import com.clinovo.service.StudySubjectIdService;
import com.clinovo.service.UserAccountService;
import com.clinovo.service.WidgetService;
import com.clinovo.service.WidgetsLayoutService;
import com.clinovo.util.CrfShortcutsAnalyzer;
import com.clinovo.util.RequestUtil;
import com.clinovo.util.RuleSetServiceUtil;

/**
 * BaseController.
 */
@SuppressWarnings({"rawtypes", "serial"})
public abstract class BaseController extends HttpServlet implements HttpRequestHandler, ServletContextAware {

	public static final String REFERER = "referer";

	public static final String ACTION_START_INITIAL_DATA_ENTRY = "ide_s";
	public static final String ACTION_CONTINUE_INITIAL_DATA_ENTRY = "ide_c";
	public static final String ACTION_START_DOUBLE_DATA_ENTRY = "dde_s";
	public static final String ACTION_CONTINUE_DOUBLE_DATA_ENTRY = "dde_c";
	public static final String ACTION_ADMINISTRATIVE_EDITING = "ae";

	public static final String STUDY = "study";
	public static final String USER_ROLE = "userRole";
	public static final String PARENT_STUDY = "parentStudy";
	public static final String USER_BEAN_NAME = "userBean";
	public static final String ERRORS_HOLDER = "errors_holder";
	public static final String SESSION_MANAGER = "sessionManager";
	public static final String THEME_COLOR = "newThemeColor";

	public static final String BR = "<br/>";
	public static final String STUDY_SHOUD_BE_IN_AVAILABLE_MODE = "studyShoudBeInAvailableMode";

	public static final String STORED_ATTRIBUTES = "RememberLastPage_storedAttributes";
	public static final String JOB_HOUR = "jobHour";
	public static final String JOB_MINUTE = "jobMinute";

	public static final String PAGE_MESSAGE = "pageMessages"; // for showing
	// page
	// wide message

	public static final String INPUT_MESSAGES = "formMessages"; // for showing
	// input-specific
	// messages

	public static final String PRESET_VALUES = "presetValues"; // for setting
	// preset values

	public static final String ADMIN_SERVLET_CODE = "admin";

	public static final String STUDY_INFO_PANEL = "panel"; // for setting the
	// side panel

	public static final String POP_UP_URL = "popUpURL";

	// Use this variable as the key for the support url
	public static final String SUPPORT_URL = "supportURL";

	public static final String MODULE = "module"; // to determine which module

	public static final String NOT_USED = "not_used";

	public static final String DOMAIN_NAME = "domain_name";

	private static final Map<Integer, Integer> UNAVAILABLE_CRF_LIST = new HashMap<Integer, Integer>();

	private static String restoreSessionFlag;

	private static final HashMap<String, HashMap<String, Object>> STORED_SESSION_ATTRIBUTES = new HashMap<String, HashMap<String, Object>>();

	private static final String[] NAMES_OF_ATTRIBUTES_TO_STORE = {"randomizationEnviroment", "panel", "fdnotes",
			"submittedDNs", "ecb", "dnAdditionalCreatingParameters", "instanceType", "newThemeColor", "visitedURLs"};

	protected static ResourceBundle resadmin, resaudit, resexception, resformat, respage, resterm, restext, resword,
			resworkflow;

	protected static HashMap<String, String> facRecruitStatusMap = new LinkedHashMap<String, String>();

	protected static HashMap<String, String> studyPhaseMap = new LinkedHashMap<String, String>();

	protected static HashMap<String, String> interPurposeMap = new LinkedHashMap<String, String>();

	protected static HashMap<String, String> allocationMap = new LinkedHashMap<String, String>();

	protected static HashMap<String, String> maskingMap = new LinkedHashMap<String, String>();

	protected static HashMap<String, String> controlMap = new LinkedHashMap<String, String>();

	protected static HashMap<String, String> assignmentMap = new LinkedHashMap<String, String>();

	protected static HashMap<String, String> endpointMap = new LinkedHashMap<String, String>();

	protected static HashMap<String, String> interTypeMap = new LinkedHashMap<String, String>();

	protected static HashMap<String, String> obserPurposeMap = new LinkedHashMap<String, String>();

	protected static HashMap<String, String> selectionMap = new LinkedHashMap<String, String>();

	protected static HashMap<String, String> timingMap = new LinkedHashMap<String, String>();

	private ServletContext servletContext;

	@Autowired
	private DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao;
	@Autowired
	private DynamicsItemGroupMetadataDao dynamicsItemGroupMetadataDao;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private AuthoritiesDao authoritiesDao;
	@Autowired
	private UsageStatsServiceDAO usageStatsServiceDAO;
	@Autowired
	private DatabaseChangeLogDao databaseChangeLogDao;
	@Autowired
	private AuditUserLoginDao auditUserLoginDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private MeasurementUnitDao measurementUnitDao;
	@Autowired
	private RuleDao ruleDao;
	@Autowired
	private RuleSetDao ruleSetDao;
	@Autowired
	private RuleSetRuleDao ruleSetRuleDao;
	@Autowired
	private RuleSetAuditDao ruleSetAuditDao;
	@Autowired
	private RuleSetRuleAuditDao ruleSetRuleAuditDao;
	@Autowired
	private StudyConfigService studyConfigService;
	@Autowired
	private OpenClinicaJdbcService openClinicaJdbcService;
	@Autowired
	private OpenClinicaSchedulerFactoryBean scheduler;
	@Autowired
	private SDVUtil sdvUtil;
	@Autowired
	private CoreResources coreResources;
	@Autowired
	private SecurityManager securityManager;
	@Autowired
	private JavaMailSenderImpl mailSender;
	@Autowired
	private CodedItemService codedItemService;
	@Autowired
	private SCDItemMetadataDao scdItemMetadataDao;
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private StudySubjectIdService studySubjectIdService;
	@Autowired
	private WidgetsLayoutService widgetsLayoutService;
	@Autowired
	private WidgetService widgetService;
	@Autowired
	private UserAccountService userAccountService;
	@Autowired
	private SystemDAO systemDAO;
	@Autowired
	private DiscrepancyDescriptionService discrepancyDescriptionService;
	@Autowired
	private EventCRFService eventCRFService;
	@Autowired
	private DcfService dcfService;
	@Autowired
	private ItemSDVService itemSDVService;
	@Autowired
	private CRFMaskingService maskingService;
	@Autowired
	private EventDefinitionCrfService eventDefinitionCrfService;
	@Autowired
	private SimpleConditionalDisplayService simpleConditionalDisplayService;
	@Autowired
	private DatasetService datasetService;
	@Autowired
	private EventDefinitionService eventDefinitionService;
	@Autowired
	private CrfBuilderFactory crfBuilderFactory;
	@Autowired
	private DeleteCrfService deleteCrfService;

	/**
	 * Allow access to this for other users.
	 * @param userId int
	 */
	public static synchronized void removeLockedCRF(int userId) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>(UNAVAILABLE_CRF_LIST);
		for (Integer key : map.keySet()) {
			int id = map.get(key);
			if (id == userId) {
				UNAVAILABLE_CRF_LIST.remove(key);
			}
		}
	}

	/**
	 * Prevent the case when other users can open same CRF for data entry.
	 * @param ecb EventCRFBean ID
	 * @param ub UserAccountBean ID
	 */
	public static synchronized void lockThisEventCRF(int ecb, int ub) {
		UNAVAILABLE_CRF_LIST.put(ecb, ub);
	}

	/**
	 * Unlock EventCRF.
	 * @param ecb EventCRFBean ID
	 */
	public static synchronized void justRemoveLockedCRF(int ecb) {
		UNAVAILABLE_CRF_LIST.remove(ecb);
	}

	public static Map getUnavailableCRFList() {
		return UNAVAILABLE_CRF_LIST;
	}

	public static String getRestoreSessionFlag() {
		return restoreSessionFlag;
	}

	public static void setRestoreSessionFlag(String restoreSessionFlag) {
		BaseController.restoreSessionFlag = restoreSessionFlag;
	}

	/**
	 *
	 */
	public static void resetRestoreSessionFlag() {
		restoreSessionFlag = "";
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @return SimpleDateFormat
	 * @deprecated use
	 *             {@link com.clinovo.util.DateUtil#getDateTimeFormatter(com.clinovo.util.DateUtil.DatePattern, java.util.Locale, org.joda.time.DateTimeZone)
	 *             getDateTimeFormatter} instead.
	 */
	@Deprecated
	public SimpleDateFormat getLocalDf(HttpServletRequest request) {
		return new SimpleDateFormat(resformat.getString("date_format_string"), LocaleResolver.getLocale(request));
	}

	/**
	 * Get SessionManager from request.
	 * @param request HttpServletRequest request
	 * @return SessionManager
	 */
	public SessionManager getSessionManager(HttpServletRequest request) {
		return (SessionManager) request.getAttribute(SESSION_MANAGER);
	}

	/**
	 * Get errors holder.
	 * @param request HttpServletRequest
	 * @return HashMap
	 */
	public HashMap getErrorsHolder(HttpServletRequest request) {
		HashMap errors = (HashMap) request.getAttribute(ERRORS_HOLDER);
		if (errors == null) {
			errors = new HashMap();
			request.setAttribute(ERRORS_HOLDER, errors);
		}
		return errors;
	}

	/**
	 * Get studyInfoPanel.
	 * @param request HttpServletRequest
	 * @return StudyInfoPanel
	 */
	public StudyInfoPanel getStudyInfoPanel(HttpServletRequest request) {
		StudyInfoPanel panel = (StudyInfoPanel) request.getSession().getAttribute(STUDY_INFO_PANEL);
		if (panel == null) {
			panel = new StudyInfoPanel();
			request.getSession().setAttribute(STUDY_INFO_PANEL, panel);
			request.setAttribute(STUDY_INFO_PANEL, panel);
		}
		return panel;
	}

	/**
	 * Get current study.
	 * @param request HttpServletRequest
	 * @return StudyBean
	 */
	public StudyBean getCurrentStudy(HttpServletRequest request) {
		return (StudyBean) request.getSession().getAttribute(STUDY);
	}

	/**
	 * Get current study from RequestUtil.
	 * @return StudyBean
	 */
	public StudyBean getCurrentStudy() {
		return getCurrentStudy(RequestUtil.getRequest());
	}

	/**
	 * Get parent study.
	 * @param request HttpServletRequest
	 * @return StudyBean
	 */
	public StudyBean getParentStudy(HttpServletRequest request) {
		return (StudyBean) request.getSession().getAttribute(PARENT_STUDY);
	}

	public StudyBean getParentStudy() {
		return getParentStudy(RequestUtil.getRequest());
	}

	/**
	 * Get UserAccountBean.
	 * @param request HttpServletRequest
	 * @return UserAccountBean
	 */
	public UserAccountBean getUserAccountBean(HttpServletRequest request) {
		return (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
	}

	public UserAccountBean getUserAccountBean() {
		return getUserAccountBean(RequestUtil.getRequest());
	}

	/**
	 * Get current role.
	 * @param request HttpServletRequest
	 * @return StudyUserRoleBean
	 */
	public StudyUserRoleBean getCurrentRole(HttpServletRequest request) {
		return (StudyUserRoleBean) request.getSession().getAttribute(USER_ROLE);
	}

	public StudyUserRoleBean getCurrentRole() {
		return getCurrentRole(RequestUtil.getRequest());
	}

	public DynamicsMetadataService getDynamicsMetadataService() {
		return getRuleSetService().getDynamicsMetadataService();
	}

	public RuleSetService getRuleSetService() {
		return RuleSetServiceUtil.getRuleSetService();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public StdScheduler getStdScheduler() {
		return (StdScheduler) scheduler.getScheduler();
	}

	public StudyConfigService getStudyConfigService() {
		return studyConfigService;
	}

	public SDVUtil getSDVUtil() {
		return sdvUtil;
	}

	public CoreResources getCoreResources() {
		return coreResources;
	}

	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	public JavaMailSenderImpl getMailSender() {
		return mailSender;
	}

	public OpenClinicaJdbcService getOpenClinicaJdbcService() {
		return openClinicaJdbcService;
	}

	public MeasurementUnitDao getMeasurementUnitDao() {
		return measurementUnitDao;
	}

	public RuleSetRuleDao getRuleSetRuleDao() {
		return ruleSetRuleDao;
	}

	public RuleSetRuleAuditDao getRuleSetRuleAuditDao() {
		return ruleSetRuleAuditDao;
	}

	public RuleDao getRuleDao() {
		return ruleDao;
	}

	public RuleSetDao getRuleSetDao() {
		return ruleSetDao;
	}

	public RuleSetAuditDao getRuleSetAuditDao() {
		return ruleSetAuditDao;
	}

	public ItemGroupDAO getItemGroupDAO() {
		return new ItemGroupDAO(getDataSource());
	}

	public AuthoritiesDao getAuthoritiesDao() {
		return authoritiesDao;
	}

	public DatabaseChangeLogDao getDatabaseChangeLogDao() {
		return databaseChangeLogDao;
	}

	public ConfigurationDao getConfigurationDao() {
		return configurationDao;
	}

	public AuditUserLoginDao getAuditUserLoginDao() {
		return auditUserLoginDao;
	}

	public UsageStatsServiceDAO getUsageStatsServiceDAO() {
		return usageStatsServiceDAO;
	}

	public SubjectDAO getSubjectDAO() {
		return new SubjectDAO(getDataSource());
	}

	public StudySubjectDAO getStudySubjectDAO() {
		return new StudySubjectDAO(getDataSource());
	}

	public StudyGroupClassDAO getStudyGroupClassDAO() {
		return new StudyGroupClassDAO(getDataSource());
	}

	public SubjectGroupMapDAO getSubjectGroupMapDAO() {
		return new SubjectGroupMapDAO(getDataSource());
	}

	public StudyEventDAO getStudyEventDAO() {
		return new StudyEventDAO(getDataSource());
	}

	public StudyDAO getStudyDAO() {
		return new StudyDAO(getDataSource());
	}

	public EventCRFDAO getEventCRFDAO() {
		return new EventCRFDAO(getDataSource());
	}

	public EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
		return new EventDefinitionCRFDAO(getDataSource());
	}

	public DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
		return new DiscrepancyNoteDAO(getDataSource());
	}

	public StudyGroupDAO getStudyGroupDAO() {
		return new StudyGroupDAO(getDataSource());
	}

	public DynamicEventDao getDynamicEventDao() {
		return new DynamicEventDao(getDataSource());
	}

	public StudyEventDefinitionDAO getStudyEventDefinitionDAO() {
		return new StudyEventDefinitionDAO(getDataSource());
	}

	public UserAccountDAO getUserAccountDAO() {
		return new UserAccountDAO(getDataSource());
	}

	public StudyParameterValueDAO getStudyParameterValueDAO() {
		return new StudyParameterValueDAO(getDataSource());
	}

	public CRFVersionDAO getCRFVersionDAO() {
		return new CRFVersionDAO(getDataSource());
	}

	public CRFDAO getCRFDAO() {
		return new CRFDAO(getDataSource());
	}

	public ArchivedDatasetFileDAO getArchivedDatasetFileDAO() {
		return new ArchivedDatasetFileDAO(getDataSource());
	}

	public DatasetDAO getDatasetDAO() {
		return new DatasetDAO(getDataSource());
	}

	public ItemDataDAO getItemDataDAO() {
		return new ItemDataDAO(getDataSource());
	}

	public ItemDAO getItemDAO() {
		return new ItemDAO(getDataSource());
	}

	public ItemFormMetadataDAO getItemFormMetadataDAO() {
		return new ItemFormMetadataDAO(getDataSource());
	}

	public AuditDAO getAuditDAO() {
		return new AuditDAO(getDataSource());
	}

	public SectionDAO getSectionDAO() {
		return new SectionDAO(getDataSource());
	}

	public AuditEventDAO getAuditEventDAO() {
		return new AuditEventDAO(getDataSource());
	}

	public ItemGroupMetadataDAO getItemGroupMetadataDAO() {
		return new ItemGroupMetadataDAO(getDataSource());
	}

	public CodedItemService getCodedItemService() {
		return codedItemService;
	}

	public DictionaryService getDictionaryService() {
		return dictionaryService;
	}

	public StudySubjectIdService getStudySubjectIdService() {
		return studySubjectIdService;
	}

	public WidgetsLayoutService getWidgetsLayoutService() {
		return widgetsLayoutService;
	}

	public WidgetService getWidgetService() {
		return widgetService;
	}

	public UserAccountService getUserAccountService() {
		return userAccountService;
	}

	public SystemDAO getSystemDAO() {
		return systemDAO;
	}

	public DiscrepancyDescriptionService getDiscrepancyDescriptionService() {
		return discrepancyDescriptionService;
	}

	public EventCRFService getEventCRFService() {
		return eventCRFService;
	}

	public DcfService getDcfService() {
		return dcfService;
	}

	public ItemSDVService getItemSDVService() {
		return itemSDVService;
	}

	public DynamicsItemGroupMetadataDao getDynamicsItemGroupMetadataDao() {
		return dynamicsItemGroupMetadataDao;
	}

	public DynamicsItemFormMetadataDao getDynamicsItemFormMetadataDao() {
		return dynamicsItemFormMetadataDao;
	}

	public EventDefinitionCrfService getEventDefinitionCrfService() {
		return eventDefinitionCrfService;
	}

	public SimpleConditionalDisplayService getSimpleConditionalDisplayService() {
		return simpleConditionalDisplayService;
	}

	/**
	 * Get CRF shortcuts analyzer.
	 * @param request HttpServletRequest
	 * @param itemSDVService ItemSDVService
	 * @return CrfShortcutsAnalyzer
	 */
	public static CrfShortcutsAnalyzer getCrfShortcutsAnalyzer(HttpServletRequest request, ItemSDVService itemSDVService) {
		return getCrfShortcutsAnalyzer(request, itemSDVService, false);
	}

	/**
	 * Get CRF shortcuts analyzer with recreate flag.
	 * @param request HttpServletRequest
	 * @param itemSDVService ItemSDVService
	 * @param recreate boolean
	 * @return CrfShortcutsAnalyzer
	 */
	public static CrfShortcutsAnalyzer getCrfShortcutsAnalyzer(HttpServletRequest request,
			ItemSDVService itemSDVService, boolean recreate) {
		CrfShortcutsAnalyzer crfShortcutsAnalyzer = (CrfShortcutsAnalyzer) request
				.getAttribute(CrfShortcutsAnalyzer.CRF_SHORTCUTS_ANALYZER);
		if (crfShortcutsAnalyzer == null || recreate) {
			FormProcessor fp = new FormProcessor(request);
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put(CrfShortcutsAnalyzer.EXIT_TO, fp.getString(CrfShortcutsAnalyzer.EXIT_TO, true));
			attributes.put(CrfShortcutsAnalyzer.CW, fp.getRequest().getParameter(CrfShortcutsAnalyzer.CW));
			attributes.put(CrfShortcutsAnalyzer.SECTION_ID, fp.getInt(CrfShortcutsAnalyzer.SECTION_ID, true));
			attributes.put(CrfShortcutsAnalyzer.SECTION, fp.getRequest().getAttribute(CrfShortcutsAnalyzer.SECTION));
			attributes.put(CrfShortcutsAnalyzer.USER_ROLE,
					request.getSession().getAttribute(CrfShortcutsAnalyzer.USER_ROLE));
			attributes.put(CrfShortcutsAnalyzer.SERVLET_PATH, fp.getString(CrfShortcutsAnalyzer.SERVLET_PATH).isEmpty()
					? fp.getRequest().getServletPath()
					: fp.getString(CrfShortcutsAnalyzer.SERVLET_PATH));

			crfShortcutsAnalyzer = new CrfShortcutsAnalyzer(request.getScheme(), request.getMethod(),
					request.getRequestURI(), request.getServletPath(), (String) request.getSession().getAttribute(
							DOMAIN_NAME), attributes, itemSDVService);

			crfShortcutsAnalyzer.getInterviewerDisplayItemBean().setField(CrfShortcutsAnalyzer.INTERVIEWER_NAME);
			crfShortcutsAnalyzer.getInterviewDateDisplayItemBean().setField(CrfShortcutsAnalyzer.DATE_INTERVIEWED);

			request.setAttribute(CrfShortcutsAnalyzer.CRF_SHORTCUTS_ANALYZER, crfShortcutsAnalyzer);
		}
		return crfShortcutsAnalyzer;
	}

	public CRFMaskingService getMaskingService() {
		return maskingService;
	}

	public DatasetService getDatasetService() {
		return datasetService;
	}

	public EventDefinitionService getEventDefinitionService() {
		return eventDefinitionService;
	}

	public static HashMap<String, HashMap<String, Object>> getStoredSessionAttributes() {
		return STORED_SESSION_ATTRIBUTES;
	}

	/**
	 * Set attributes that should be restored for this user after session.destroy.
	 * @param session HttpSession
	 */
	public static void setStoredSessionAttributes(HttpSession session) {
		UserAccountBean ub = (UserAccountBean) session.getAttribute("userBean");
		if (ub != null) {
			HashMap<String, Object> attributesToStore = new HashMap<String, Object>();
			for (String name : NAMES_OF_ATTRIBUTES_TO_STORE) {
				Object value = session.getAttribute(name);
				if (value != null) {
					attributesToStore.put(name, value);
				}
			}
			if (attributesToStore.size() != 0) {
				getStoredSessionAttributes().put(ub.getName(), attributesToStore);
			}
		}
	}

	/**
	 * Restore session attributes for the current user.
	 * @param session HttpSession
	 * @param userName String
	 */
	public static void restoreSavedSessionAttributes(HttpSession session, String userName) {
		HashMap<String, Object> storedAttributes = getStoredSessionAttributes().get(userName);

		if  (storedAttributes == null || storedAttributes.size() == 0) {
			return;
		}
		for (String key : storedAttributes.keySet()) {
			session.setAttribute(key, storedAttributes.get(key));
		}
		getStoredSessionAttributes().remove(userName);
	}

	public CrfBuilderFactory getCrfBuilderFactory() {
		return crfBuilderFactory;
	}

	public DeleteCrfService getDeleteCrfService() {
		return deleteCrfService;
	}
}
