package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.BaseController;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SDVControllerTest extends BaseControllerTest {

	public static final StudyUserRoleBean USER_ROLE = new StudyUserRoleBean();
	public static final StudyBean CURRENT_STUDY = new StudyBean();

	static {
		CURRENT_STUDY.setId(1);
	}

	@Test
	public void testThatViewSubjectAggregateHandlerReturnsCode200() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/viewSubjectAggregate").param("studyId", "1").sessionAttr(BaseController.STUDY, CURRENT_STUDY)
						.sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatViewSubjectAggregateHandlerBlocksNonAdministrativeRoles() throws Exception {
		USER_ROLE.setRole(Role.INVESTIGATOR);
		this.mockMvc.perform(
				MockMvcRequestBuilders.get("/viewSubjectAggregate").param("studyId", "1")
						.sessionAttr(BaseController.STUDY, CURRENT_STUDY)
						.sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(
				MockMvcResultMatchers.redirectedUrl("/MainMenu?message=authentication_failed"));
	}

	@Test
	public void testThatViewAllSubjectSDVHandlerReturnsCode200() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/viewAllSubjectSDV").param("studyId", "1").sessionAttr(BaseController.STUDY, CURRENT_STUDY)
						.sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatViewAllSubjectSDVTmpHandlerReturnsCode200() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/viewAllSubjectSDVtmp").param("studyId", "1").sessionAttr(BaseController.STUDY, CURRENT_STUDY)
						.sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatViewAllSubjectSDVTmpHandlerBlocksNonAdministrativeRoles() throws Exception {
		USER_ROLE.setRole(Role.INVESTIGATOR);
		this.mockMvc.perform(
				MockMvcRequestBuilders.get("/viewAllSubjectSDVtmp").param("studyId", "1")
						.sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(
				MockMvcResultMatchers.redirectedUrl("/MainMenu?message=authentication_failed"));
	}

	@Test
	public void testThatViewAllSubjectFormHandlerReturnsCode200() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/viewAllSubjectSDVform").param("studyId", "1").sessionAttr(BaseController.STUDY, CURRENT_STUDY)
						.sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatSdvAllSubjectsFormHandlerReturnsCode200() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/handleSDVPost").param("redirection", "viewAllSubjectSDVform")
						.sessionAttr(BaseController.STUDY, CURRENT_STUDY)
						.sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatSdvOneCRFFormHandlerReturnsCode200() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/handleSDVGet").param("crfId", "1").param("redirection", "viewAllSubjectSDVform")
						.sessionAttr(BaseController.STUDY, CURRENT_STUDY)
						.sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatChangeSDVHandlerReturnsCode200() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/handleSDVRemove").param("crfId", "1").param("redirection", "viewAllSubjectSDVform")
						.sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatSdvStudySubjectHandlerReturnsCode200() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/sdvStudySubject").param("theStudySubjectId", "1").param("redirection", "viewAllSubjectSDVform")
						.sessionAttr(BaseController.STUDY, CURRENT_STUDY)
						.sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatUnSdvStudySubjectHandlerReturnsCode200() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/unSdvStudySubject").param("theStudySubjectId", "1").param("redirection", "viewAllSubjectSDVform")
						.sessionAttr(BaseController.STUDY, CURRENT_STUDY)
						.sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatSdvStudySubjectsHandlerReturnsCode200() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/sdvStudySubjects").param("redirection", "viewAllSubjectSDVform")
						.sessionAttr(BaseController.STUDY, CURRENT_STUDY)
						.sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}
}
