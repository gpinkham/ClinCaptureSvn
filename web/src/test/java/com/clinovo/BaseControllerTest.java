package com.clinovo;

import java.util.Locale;

import org.akaza.openclinica.AbstractContextSentiveTest;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.clinovo.i18n.LocaleResolver;

/**
 * BaseControllerTest class.
 */
@Ignore
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:servlet-context.xml")
public class BaseControllerTest extends AbstractContextSentiveTest {

	protected MockMvc mockMvc;

	// Managed controllers
	public static final String TERM_CONTROLLER = "/deleteTerm";
	public static final String CODED_ITEM_CONTROLLER = "/codedItems";
	public static final String SDV_ITEM_CONTROLLER = "/sdvItem";
	public static final String CONFIGURE_HOME_PAGE = "/configureHomePage";
	public static final String INCLUDES_CONTROLLER = "/includes/getPageContent";
	public static final String SAVE_HOME_PAGE = "/saveHomePage";
	public static final String NDS_ASSIGNED_TO_ME_WIDGET = "/initNdsAssignedToMeWidget";
	public static final String EVENTS_COMPLETION_WIDGET = "/initEventsCompletionWidget";
	public static final String SUBJECTS_STATUS_COUNT_WIDGET = "/initSubjectStatusCount";
	public static final String STUDY_PROGRESS_WIDGET = "/initStudyProgress";
	public static final String SDV_PROGRESS_WIDGET = "/initSdvProgressWidget";
	public static final String COMPLETE_CRF_DELETE = "/completeCRFDelete";
	public static final String NDS_PER_CRF_WIDGET = "/initNdsPerCrfWidget";
	public static final String CHANGE_ORDINAL_CONTROLLER = "/changeDefinitionOrdinal";
	public static final String ENROLLMENT_PROGRESS_WIDGET = "/initEnrollmentProgressWidget";
	public static final String CODING_PROGRESS_WIDGET = "/initCodingProgressWidget";
	public static final String ENROLL_STATUS_PER_SITE = "/initEnrollStatusPerSiteWidget";
	public static final String DELETE_CRF_VERSION = "/deleteCRFVersion";
	public static final String CRF_EVALUATION = "/crfEvaluation";
	public static final String ESPS_LEGEND = "/getEnrollStatusPerSiteLegendValues";
	public static final String EVALUATION_PROGRESS_WIDGET = "/initEvaluationProgressWidget";
	public static final String ESPAS_WIDGET = "/initESPASWidget";
	public static final String ESPAS_LIGEND = "/getESPASLegendValues";

	public static final Locale LOCALE = new Locale("en");

	@Autowired
	protected WebApplicationContext wac;

	@Autowired
	protected MessageSource messageSource;

	protected MockHttpSession session = new MockHttpSession();

	@Before
	public void setup() throws Exception {
		super.setUp();
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("BB", "is dreaming about halle berry"));

		LocaleResolver.updateLocale(session, LOCALE);
		ResourceBundleProvider.updateLocale(LOCALE);
	}
}
