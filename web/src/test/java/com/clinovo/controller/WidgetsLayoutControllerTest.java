package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for WidgetLayoutController.
 **/
public class WidgetsLayoutControllerTest extends BaseControllerTest {

	private static final int EVENT_COMPL_ATTRIBUTES = 4;
	private static final int SUBJECT_STAT_ATTRIBUTES = 4;
	private static final int SDV_PROG_ATTRIBUTES = 5;
	private static final int NDS_PER_CRF_ATTRIBUTES = 4;
	private static final int ENROLL_PROG_ATTRIBUTES = 4;
	private static final int CODING_PROG_ATTRIBUTES = 5;
	private static final int ENROLL_STAT_PER_SITE_ATTRIBUTES = 4;
	private static final int EVALUATION_PROG_ATTRIBUTES = 5;

	private StudyBean sb;
	private UserAccountBean ub;

	/**
	 * Initialize all fields for tests.
	 */
	@Before
	public void prepear() {

		ArrayList<StudyUserRoleBean> roles = new ArrayList<StudyUserRoleBean>();
		StudyUserRoleBean adminRole = new StudyUserRoleBean();
		adminRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		roles.add(adminRole);
		ub = new UserAccountBean();
		ub.setId(1);
		ub.setRoles(roles);
		ub.setActiveStudyId(1);
		ub.setName("root");

		sb = new StudyBean();
		sb.setId(1);
	}

	/**
	 * Tests that "Configure Home Page" page is available.
	 * 
	 * @throws Exception
	 *             if page is not available.
	 **/
	@Test
	public void testThatConfigureHomePageReturnsCode200() throws Exception {

		this.mockMvc.perform(
				get(CONFIGURE_HOME_PAGE).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.sessionAttr("userBean", ub).sessionAttr("study", sb)).andExpect(status().isOk());
	}

	/**
	 * Tests that home page layout can be saved without an errors.
	 * 
	 * @throws Exception
	 *             if layout was not saved.
	 **/
	@Test
	public void testThatSaveHomePageLayoutReturnsCode204() throws Exception {

		this.mockMvc.perform(
				get(SAVE_HOME_PAGE).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("orderInColumn1", "1,2").param("orderInColumn2", "").param("unusedWidgets", "")
						.param("bigWidgets", "").param("userId", "1").param("studyId", "1")).andExpect(
				status().isNoContent());
	}

	/**
	 * Tests that "My notes and discrepancies" widget is available.
	 * 
	 * @throws Exception
	 *             if widget is not available.
	 **/
	@Test
	public void testThatInitNDSWidgetReturnsCode200() throws Exception {

		this.mockMvc.perform(
				get(NDS_ASSIGNED_TO_ME_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("userId", "1").sessionAttr("study", sb)).andExpect(status().isOk());
	}

	/**
	 * Tests that "Events Completion" widget is available.
	 * 
	 * @throws Exception
	 *             if widget is not available.
	 **/
	@Test
	public void testThatInitEventsCompletionReturnsCode200() throws Exception {

		this.mockMvc.perform(
				get(EVENTS_COMPLETION_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("action", "init").param("lastElement", "0").param("studyId", "0")
						.sessionAttr("study", sb)).andExpect(status().isOk());
	}

	/**
	 * Tests that "Subjects Status" widget is available.
	 * 
	 * @throws Exception
	 *             if widget is not available.
	 **/
	@Test
	public void testThatInitSubjectStatusCountReturnsCode200() throws Exception {

		this.mockMvc.perform(
				get(SUBJECTS_STATUS_COUNT_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).sessionAttr(
						"study", sb)).andExpect(status().isOk());
	}

	/**
	 * Tests that "Study Progress" widget is available.
	 * 
	 * @throws Exception
	 *             if widget is not available.
	 **/
	@Test
	public void testThatInitStudyProgressReturnsCode200() throws Exception {

		this.mockMvc.perform(
				get(STUDY_PROGRESS_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("action", "init").param("lastElement", "0").param("studyId", "0")
						.sessionAttr("study", sb)).andExpect(status().isOk());
	}

	/**
	 * Tests that "SDV Progress" widget is available.
	 * 
	 * @throws Exception
	 *             if widget is not available.
	 **/
	@Test
	public void testThatInitSDVProgressReturnsCode200() throws Exception {

		this.mockMvc.perform(
				get(SDV_PROGRESS_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("sdvProgressYear", "0").sessionAttr("study", sb)).andExpect(status().isOk());
	}

	/**
	 * Tests that "NDs per CRF" widget is available.
	 * 
	 * @throws Exception
	 *             if widget is not available.
	 **/
	@Test
	public void testThatInitNdsPerCrfReturnsCode200() throws Exception {

		this.mockMvc.perform(
				get(NDS_PER_CRF_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("start", "0")
						.param("action", "init").sessionAttr("study", sb)).andExpect(status().isOk());
	}

	@Test
	public void testThatInitEvaluationProgressReturnsCode200() throws Exception {

		this.mockMvc.perform(
				get(EVALUATION_PROGRESS_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("evaluationProgressYear", "0").sessionAttr("userBean", ub).sessionAttr("study", sb))
				.andExpect(status().isOk());
	}

	/**
	 * Tests that "Coding Progress" widget is available.
	 * 
	 * @throws Exception
	 *             if widget is not available.
	 **/
	@Test
	public void testThatInitCodingProgressReturnsCode200() throws Exception {

		this.mockMvc.perform(
				get(CODING_PROGRESS_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("codingProgressYear", "0").sessionAttr("study", sb).sessionAttr("userBean", ub))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatInitEnrollStatusPerSiteReturnsCode200() throws Exception {

		this.mockMvc.perform(
				get(ENROLL_STATUS_PER_SITE).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("epPerSiteDisplay", "0").param("action", "init").sessionAttr("study", sb)
						.sessionAttr("userBean", ub)).andExpect(status().isOk());
	}

	@Test
	public void testThatGetESPSLegendValuesReturnsCode200() throws Exception {

		this.mockMvc.perform(
				get(ESPS_LEGEND).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("userId", "1")
						.sessionAttr("study", sb)).andExpect(status().isOk());
	}

	/**
	 * Tests that <code>configureHomePage</code> method returns correct attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect attributes.
	 **/
	@Test
	public void testThatConfigureHomePageReturnsModelWithAllAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(CONFIGURE_HOME_PAGE)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).sessionAttr("userBean", ub)
						.sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().attributeExists("dispayWidgetsLayout"));
	}

	/**
	 * Tests that <code>configureHomePage</code> method returns correct number of attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect number of attributes.
	 **/
	@Test
	public void testThatConfigureHomePageReturnsCorrectNubmerOfAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(CONFIGURE_HOME_PAGE)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).sessionAttr("userBean", ub)
						.sessionAttr("study", sb)).andExpect(MockMvcResultMatchers.model().size(1));
	}

	/**
	 * Tests that <code>initEventsCompleation</code> method returns correct number of attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect number of attributes.
	 **/
	@Test
	public void testThatInitEventsCompleationReturnsCorrectNubmerOfAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(EVENTS_COMPLETION_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("action", "init")
						.param("lastElement", "0").param("studyId", "0").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().size(EVENT_COMPL_ATTRIBUTES));
	}

	/**
	 * Tests that <code>initEventsCompleation</code> method returns correct attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect attributes.
	 **/
	@Test
	public void testThatInitEventsCompleationReturnsModelWithAllAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(EVENTS_COMPLETION_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("action", "init")
						.param("lastElement", "0").param("studyId", "0").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().attributeExists("eventCompletionRows", "eventCompletionHasNext",
						"eventCompletionHasPrevious", "eventCompletionLastElement"));
	}

	/**
	 * Tests that <code>initEventsCompleation</code> method returns correct URL to jsp.
	 * 
	 * @throws Exception
	 *             if method returns incorrect url.
	 **/
	@Test
	public void testThatInitEventsCompletionReurnsCorrectUrl() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(EVENTS_COMPLETION_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("action", "init")
						.param("lastElement", "0").param("studyId", "0").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/eventsCompletionChart"));
	}

	/**
	 * Tests that <code>initSubjectStatusCount</code> method returns correct number of attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect number of attributes.
	 **/
	@Test
	public void testThatInitSubjectStatusCountReturnsCorrectNubmerOfAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(SUBJECTS_STATUS_COUNT_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().size(SUBJECT_STAT_ATTRIBUTES));
	}

	/**
	 * Tests that <code>initSubjectStatusCount</code> method returns correct attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect attributes.
	 **/
	@Test
	public void testThatInitSubjectStatusCountReturnsModelWithAllAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(SUBJECTS_STATUS_COUNT_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().attributeExists("countOfAvailableSubjects", "countOfRemovedSubjects",
						"countOfLockedSubjects", "countOfSignedSubjects"));
	}

	/**
	 * Tests that <code>initSubjectStatusCount</code> method returns correct URL to jsp.
	 * 
	 * @throws Exception
	 *             if method returns incorrect url.
	 **/
	@Test
	public void testThatInitSubjectStatusCountReurnsCorrectUrl() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(SUBJECTS_STATUS_COUNT_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("action", "init")
						.param("lastElement", "0").param("studyId", "0").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/subjectStatusCountChart"));
	}

	/**
	 * Tests that <code>initStudyProgress</code> method returns correct number of attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect number of attributes.
	 **/
	@Test
	public void testThatInitStudyProgressReturnsCorrectNubmerOfAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(STUDY_PROGRESS_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().size(1));
	}

	/**
	 * Tests that <code>initStudyProgress</code> method returns correct attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect attributes.
	 **/
	@Test
	public void testThatInitStudyProgressReturnsModelWithAllAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(STUDY_PROGRESS_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().attributeExists("studyProgressMap"));
	}

	/**
	 * Tests that <code>initStudyProgress</code> method returns correct URL to jsp.
	 * 
	 * @throws Exception
	 *             if method returns incorrect url.
	 **/
	@Test
	public void testThatInitStudyProgressReurnsCorrectUrl() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(STUDY_PROGRESS_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("action", "init")
						.param("lastElement", "0").param("studyId", "0").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/studyProgressChart"));
	}

	/**
	 * Tests that <code>initSdvProgress</code> method returns correct number of attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect number of attributes.
	 **/
	@Test
	public void testThatInitSdvProgressWidgetReturnsCorrectNumberOfAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(SDV_PROGRESS_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("sdvProgressYear", "0")
						.sessionAttr("study", sb)).andExpect(MockMvcResultMatchers.model().size(SDV_PROG_ATTRIBUTES));
	}

	/**
	 * Tests that <code>initSdvProgress</code> method returns correct attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect attributes.
	 **/
	@Test
	public void testThatInitSdvProgressWidgetReturnsModelWithAllAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(SDV_PROGRESS_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("sdvProgressYear", "0")
						.sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().attributeExists("sdvAvailableECRFs", "sdvProgressYear",
						"sdvValuesByMonth", "sdvNextYearExists", "sdvPreviousYearExists"));
	}

	/**
	 * Tests that <code>initSdvProgress</code> method returns correct URL to jsp.
	 * 
	 * @throws Exception
	 *             if method returns incorrect url.
	 **/
	@Test
	public void testThatInitSdvProgressWidgetReturnsCorrectUrl() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(SDV_PROGRESS_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("sdvProgressYear", "0")
						.sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/sdvProgressChart"));
	}

	/**
	 * Tests that <code>initNdsPerCrf</code> method returns correct number of attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect number of attributes.
	 **/
	@Test
	public void testThatInitNdsPerCrfWidgetReturnsModelWithAlAttribures() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(NDS_PER_CRF_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("start", "0").param("action", "init").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().attributeExists("ndsCrfHasPrevious", "ndsCrfHasNext", "ndsCrfStart",
						"ndsCrfDataColumns"));
	}

	/**
	 * Tests that <code>initNdsPerCrf</code> method returns correct attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect attributes.
	 **/
	@Test
	public void testThatInitNdsPerCrfWidgetReturnsCorrectNumberOfAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(NDS_PER_CRF_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("start", "0").param("action", "init").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().size(NDS_PER_CRF_ATTRIBUTES));
	}

	/**
	 * Tests that <code>initNdsPerCrf</code> method returns correct URL to jsp.
	 * 
	 * @throws Exception
	 *             if method returns incorrect url.
	 **/
	@Test
	public void testThatInitNdsPerCrfWidgetReturnsCorrectUrl() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(NDS_PER_CRF_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("start", "0").param("action", "init").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/ndsPerCrfChart"));
	}

	/**
	 * Tests that <code>initEnrollmentProgress</code> method returns correct number of attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect number of attributes.
	 **/
	@Test
	public void testThatInitEnrollmentProgressWidgetReturnsCorrectNumberOfAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(ENROLLMENT_PROGRESS_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("currentYear", "0")
						.sessionAttr("study", sb))
				.andExpect(MockMvcResultMatchers.model().size(ENROLL_PROG_ATTRIBUTES));
	}

	/**
	 * Tests that <code>initEnrollmentProgress</code> method returns correct attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect attributes.
	 **/
	@Test
	public void testThatInitEnrollmentProgressWidgetReturnsModelWithAllAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(ENROLLMENT_PROGRESS_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("currentYear", "0")
						.sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().attributeExists("epYear", "epDataRows", "epPreviousYearExists",
						"epNextYearExists"));
	}

	/**
	 * Tests that <code>initEnrollmentProgress</code> method returns correct URL to jsp.
	 * 
	 * @throws Exception
	 *             if method returns incorrect url.
	 **/
	@Test
	public void testThatInitEnrollmentProgressWidgetReturnsCorrectUrl() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(ENROLLMENT_PROGRESS_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("currentYear", "0")
						.sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/enrollmentProgressChart"));
	}

	/**
	 * Tests that <code>initCodingProgress</code> method returns correct number of attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect number of attributes.
	 **/
	@Test
	public void testThatInitCodingProgressWidgetReturnsCorrectNumberOfAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(CODING_PROGRESS_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("codingProgressYear", "0")
						.sessionAttr("study", sb).sessionAttr("userBean", ub)).andExpect(
				MockMvcResultMatchers.model().size(CODING_PROG_ATTRIBUTES));
	}

	/**
	 * Tests that <code>initCodingProgress</code> method returns correct attributes.
	 * 
	 * @throws Exception
	 *             if method returns incorrect attributes.
	 **/
	@Test
	public void testThatInitCodingProgressWidgetReturnsModelWithAllAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(CODING_PROGRESS_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("codingProgressYear", "0")
						.sessionAttr("study", sb).sessionAttr("userBean", ub)).andExpect(
				MockMvcResultMatchers.model().attributeExists("cpYear", "cpDataRows", "cpPreviousYearExists",
						"cpNextYearExists", "cpActivateLegend"));
	}

	/**
	 * Tests that <code>initCodingProgress</code> method returns correct URL to jsp.
	 * 
	 * @throws Exception
	 *             if method returns incorrect url.
	 **/
	@Test
	public void testThatInitCodingProgressWidgetReturnsCorrectUrl() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(CODING_PROGRESS_WIDGET)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("codingProgressYear", "0")
						.sessionAttr("study", sb).sessionAttr("userBean", ub)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/codingProgressChart"));
	}

	@Test
	public void testThatInitEvaluationProgressReturnsCorrectNumberOfAttributes() throws Exception {

		this.mockMvc.perform(
				get(EVALUATION_PROGRESS_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("evaluationProgressYear", "0").sessionAttr("userBean", ub).sessionAttr("study", sb))
				.andExpect(MockMvcResultMatchers.model().size(EVALUATION_PROG_ATTRIBUTES));
	}

	@Test
	public void testThatInitEvaluationProgressReturnsModelWithAllAttributes() throws Exception {

		this.mockMvc.perform(
				get(EVALUATION_PROGRESS_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("evaluationProgressYear", "0").sessionAttr("userBean", ub).sessionAttr("study", sb))
				.andExpect(
						MockMvcResultMatchers.model().attributeExists("evaluationProgressYear",
								"evaluationProgressDataRows", "evalProgPreviousYearExists", "evalProgNextYearExists",
								"evaluationProgressActivateLegend"));
	}

	@Test
	public void testThatInitEvaluationProgressReturnsCorrectUrl() throws Exception {

		this.mockMvc.perform(
				get(EVALUATION_PROGRESS_WIDGET).sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("evaluationProgressYear", "0").sessionAttr("userBean", ub).sessionAttr("study", sb))
				.andExpect(MockMvcResultMatchers.view().name("widgets/includes/evaluationProgressChart"));
	}

	@Test
	public void testThatInitEnrollStatusPerSiteReturnsCorrectNumberOfAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(ENROLL_STATUS_PER_SITE)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("epPerSiteDisplay", "0")
						.param("action", "init").sessionAttr("study", sb).sessionAttr("userBean", ub)).andExpect(
				MockMvcResultMatchers.model().size(ENROLL_STAT_PER_SITE_ATTRIBUTES));
	}

	@Test
	public void testThatInitEnrollStatusPerSiteReturnsModelWithAllAttributes() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(ENROLL_STATUS_PER_SITE)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("epPerSiteDisplay", "0")
						.param("action", "init").sessionAttr("study", sb).sessionAttr("userBean", ub)).andExpect(
				MockMvcResultMatchers.model().attributeExists("espsDisplay", "espsDataRows", "espsPreviousPageExists",
						"espsNextPageExists"));
	}

	@Test
	public void testThatInitEnrollStatusPerSiteReturnsCorrectUrl() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(ENROLL_STATUS_PER_SITE)
						.sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE).param("epPerSiteDisplay", "0")
						.param("action", "init").sessionAttr("study", sb).sessionAttr("userBean", ub)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/enrollStatusPerSiteChart"));
	}}