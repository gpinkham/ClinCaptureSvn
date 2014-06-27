package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WidgetsLayoutControllerTest extends BaseControllerTest {

	@Test
	public void testThatConfigureHomePageReturnsCode200() throws Exception {

		UserAccountBean ub = new UserAccountBean();
		ub.setId(1);

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(get(CONFIGURE_HOME_PAGE).sessionAttr("userBean", ub).sessionAttr("study", sb)).andExpect(
				status().isOk());
	}

	@Test
	public void testThatSaveHomePageLayoutReturnsCode204() throws Exception {

		this.mockMvc.perform(
				get(SAVE_HOME_PAGE).param("orderInColumn1", "1,2").param("orderInColumn2", "")
						.param("unusedWidgets", "").param("bigWidgets", "").param("userId", "1").param("studyId", "1"))
				.andExpect(status().isNoContent());
	}

	@Test
	public void testThatInitNDSWidgetReturnsCode200() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(get(NDS_ASSIGNED_TO_ME_WIDGET).param("userId", "1").sessionAttr("study", sb)).andExpect(
				status().isOk());
	}

	@Test
	public void testThatInitEventsCompletionReturnsCode200() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				get(EVENTS_COMPLETION_WIDGET).param("action", "init").param("lastElement", "0").param("studyId", "0")
						.sessionAttr("study", sb)).andExpect(status().isOk());
	}

	@Test
	public void testThatInitSubjectStatusCountReturnsCode200() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(get(SUBJECTS_STATUS_COUNT_WIDGET).sessionAttr("study", sb)).andExpect(status().isOk());
	}

	@Test
	public void testThatInitStudyProgressReturnsCode200() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				get(STUDY_PROGRESS_WIDGET).param("action", "init").param("lastElement", "0").param("studyId", "0")
						.sessionAttr("study", sb)).andExpect(status().isOk());
	}

	@Test
	public void testThatInitSDVProgressReturnsCode200() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(get(SDV_PROGRESS_WIDGET).param("sdvProgressYear", "0").sessionAttr("study", sb))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatInitNdsPerCrfReturnsCode200() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				get(NDS_PER_CRF_WIDGET).param("start", "0").param("action", "init").sessionAttr("study", sb))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatConfigureHomePageReturnsModelWithAllAttributes() throws Exception {

		UserAccountBean ub = new UserAccountBean();
		ub.setId(1);

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(CONFIGURE_HOME_PAGE).sessionAttr("userBean", ub).sessionAttr("study", sb))
				.andExpect(MockMvcResultMatchers.model().attributeExists("dispayWidgetsLayout"));
	}

	@Test
	public void testThatConfigureHomePageReturnsCorrectNubmerOfAttributes() throws Exception {

		UserAccountBean ub = new UserAccountBean();
		ub.setId(1);

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(CONFIGURE_HOME_PAGE).sessionAttr("userBean", ub).sessionAttr("study", sb))
				.andExpect(MockMvcResultMatchers.model().size(1));
	}

	@Test
	public void testThatInitEventsCompleationReturnsCorrectNubmerOfAttributes() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(EVENTS_COMPLETION_WIDGET).param("action", "init").param("lastElement", "0")
						.param("studyId", "0").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().size(4));
	}

	@Test
	public void testThatInitEventsCompleationReturnsModelWithAllAttributes() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(EVENTS_COMPLETION_WIDGET).param("action", "init").param("lastElement", "0")
						.param("studyId", "0").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().attributeExists("eventCompletionRows", "eventCompletionHasNext",
						"eventCompletionHasPrevious", "eventCompletionLastElement"));
	}

	@Test
	public void testThatInitEventsCompletionReurnsCorrectUrl() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(EVENTS_COMPLETION_WIDGET).param("action", "init").param("lastElement", "0")
						.param("studyId", "0").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/eventsCompletionChart"));
	}

	@Test
	public void testThatInitSubjectStatusCountReturnsCorrectNubmerOfAttributes() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(MockMvcRequestBuilders.post(SUBJECTS_STATUS_COUNT_WIDGET).sessionAttr("study", sb))
				.andExpect(MockMvcResultMatchers.model().size(4));
	}

	@Test
	public void testThatInitSubjectStatusCountReturnsModelWithAllAttributes() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(MockMvcRequestBuilders.post(SUBJECTS_STATUS_COUNT_WIDGET).sessionAttr("study", sb))
				.andExpect(
						MockMvcResultMatchers.model().attributeExists("countOfAvailableSubjects",
								"countOfRemovedSubjects", "countOfLockedSubjects", "countOfSignedSubjects"));
	}

	@Test
	public void testThatInitSubjectStatusCountReurnsCorrectUrl() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(SUBJECTS_STATUS_COUNT_WIDGET).param("action", "init")
						.param("lastElement", "0").param("studyId", "0").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/subjectStatusCountChart"));
	}

	@Test
	public void testThatInitStudyProgressReturnsCorrectNubmerOfAttributes() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(MockMvcRequestBuilders.post(STUDY_PROGRESS_WIDGET).sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().size(1));
	}

	@Test
	public void testThatInitStudyProgressReturnsModelWithAllAttributes() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(MockMvcRequestBuilders.post(STUDY_PROGRESS_WIDGET).sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().attributeExists("studyProgressMap"));
	}

	@Test
	public void testThatInitStudyProgressReurnsCorrectUrl() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(STUDY_PROGRESS_WIDGET).param("action", "init").param("lastElement", "0")
						.param("studyId", "0").sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/studyProgressChart"));
	}

	@Test
	public void testThatInitSdvProgressWidgetReturnsCorrectNumberOfAttributes() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc
				.perform(
						MockMvcRequestBuilders.post(SDV_PROGRESS_WIDGET).param("sdvProgressYear", "0")
								.sessionAttr("study", sb)).andExpect(MockMvcResultMatchers.model().size(5));
	}

	@Test
	public void testThatInitSdvProgressWidgetReturnsModelWithAllAttributes() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc
				.perform(
						MockMvcRequestBuilders.post(SDV_PROGRESS_WIDGET).param("sdvProgressYear", "0")
								.sessionAttr("study", sb)).andExpect(
						MockMvcResultMatchers.model().attributeExists("sdvAvailableECRFs", "sdvProgressYear",
								"sdvValuesByMonth", "sdvNextYearExists", "sdvPreviousYearExists"));
	}

	@Test
	public void testThatInitSdvProgressWidgetReturnsCorrectUrl() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc
				.perform(
						MockMvcRequestBuilders.post(SDV_PROGRESS_WIDGET).param("sdvProgressYear", "0")
								.sessionAttr("study", sb)).andExpect(
						MockMvcResultMatchers.view().name("widgets/includes/sdvProgressChart"));
	}

	@Test
	public void testThatInitNdsPerCrfWidgetReturnsModelWithAlAttribures() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(NDS_PER_CRF_WIDGET).param("start", "0").param("action", "init")
						.sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().attributeExists("ndsCrfHasPrevious", "ndsCrfHasNext", "ndsCrfStart",
						"ndsCrfDataColumns"));
	}

	@Test
	public void testThatInitNdsPerCrfWidgetReturnsCorrectNumberOfAttributes() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(NDS_PER_CRF_WIDGET).param("start", "0").param("action", "init")
						.sessionAttr("study", sb)).andExpect(MockMvcResultMatchers.model().size(4));
	}

	@Test
	public void testThatInitNdsPerCrfWidgetReturnsCorrectUrl() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(NDS_PER_CRF_WIDGET).param("start", "0").param("action", "init")
						.sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/ndsPerCrfChart"));
	}

	@Test
	public void testThatInitEnrollmentProgressWidgetReturnsCorrectNumberOfAttributes() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(ENROLLMENT_PROGRESS_WIDGET).param("currentYear", "0")
						.sessionAttr("study", sb)).andExpect(MockMvcResultMatchers.model().size(4));
	}

	@Test
	public void testThatInitEnrollmentProgressWidgetReturnsModelWithAllAttributes() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(ENROLLMENT_PROGRESS_WIDGET).param("currentYear", "0")
						.sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.model().attributeExists("epYear", "epDataRows", "epPreviousYearExists",
						"epNextYearExists"));
	}

	@Test
	public void testThatInitEnrollmentProgressWidgetReturnsCorrectUrl() throws Exception {

		StudyBean sb = new StudyBean();
		sb.setId(1);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(ENROLLMENT_PROGRESS_WIDGET).param("currentYear", "0")
						.sessionAttr("study", sb)).andExpect(
				MockMvcResultMatchers.view().name("widgets/includes/enrollmentProgressChart"));
	}
}
