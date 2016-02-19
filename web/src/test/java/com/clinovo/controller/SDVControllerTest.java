package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SDVControllerTest extends BaseControllerTest {

	public static final StudyUserRoleBean USER_ROLE = new StudyUserRoleBean();
	public static final StudyBean CURRENT_STUDY = new StudyBean();

	static {
		CURRENT_STUDY.setId(1);
	}

	@Before
	public void before() throws Exception {
		session.setAttribute(SpringController.USER_BEAN_NAME, new UserAccountDAO(dataSource).findByPK(1));
	}

	@Test
	public void testThatViewSubjectAggregateHandlerReturnsCode200() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/viewSubjectAggregate").param("studyId", "1").session(session)
						.sessionAttr(SpringController.STUDY, CURRENT_STUDY)
						.sessionAttr(SpringController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatViewSubjectAggregateHandlerBlocksNonAdministrativeRoles() throws Exception {
		USER_ROLE.setRole(Role.INVESTIGATOR);
		this.mockMvc.perform(
				MockMvcRequestBuilders.get("/viewSubjectAggregate").param("studyId", "1").session(session)
						.sessionAttr(SpringController.STUDY, CURRENT_STUDY)
						.sessionAttr(SpringController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatViewAllSubjectSDVTmpHandlerReturnsCode200() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/viewAllSubjectSDVtmp").param("studyId", "1").session(session)
						.sessionAttr(SpringController.STUDY, CURRENT_STUDY)
						.sessionAttr(SpringController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatViewAllSubjectSDVTmpHandlerBlocksNonAdministrativeRoles() throws Exception {
		USER_ROLE.setRole(Role.INVESTIGATOR);
		this.mockMvc.perform(
				MockMvcRequestBuilders.get("/viewAllSubjectSDVtmp").param("studyId", "1").session(session)
						.sessionAttr(SpringController.STUDY, CURRENT_STUDY)
						.sessionAttr(SpringController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatSdvAllSubjectsFormHandlerReturnsCode302() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/handleSDVPost").session(session).sessionAttr(SpringController.STUDY, CURRENT_STUDY)
						.sessionAttr(SpringController.USER_ROLE, USER_ROLE)).andExpect(status().isFound());
	}

	@Test
	public void testThatSdvOneCRFFormHandlerReturnsCode302() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/handleSDVGet").param("crfId", "1").session(session)
						.sessionAttr(SpringController.STUDY, CURRENT_STUDY)
						.sessionAttr(SpringController.USER_ROLE, USER_ROLE)).andExpect(status().isFound());
	}

	@Test
	public void testThatChangeSDVHandlerReturnsCode302() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/handleSDVRemove").param("crfId", "1").session(session)
						.sessionAttr(SpringController.STUDY, CURRENT_STUDY)
						.sessionAttr(SpringController.USER_ROLE, USER_ROLE)).andExpect(status().isFound());
	}

	@Test
	public void testThatSdvStudySubjectHandlerReturnsCode302() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/sdvStudySubject").param("theStudySubjectId", "1").session(session)
						.sessionAttr(SpringController.STUDY, CURRENT_STUDY)
						.sessionAttr(SpringController.USER_ROLE, USER_ROLE)).andExpect(status().isFound());
	}

	@Test
	public void testThatUnSdvStudySubjectHandlerReturnsCode302() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/unSdvStudySubject").param("theStudySubjectId", "1").session(session)
						.sessionAttr(SpringController.STUDY, CURRENT_STUDY)
						.sessionAttr(SpringController.USER_ROLE, USER_ROLE)).andExpect(status().isFound());
	}

	@Test
	public void testThatSdvStudySubjectsHandlerReturnsCode302() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				get("/sdvStudySubjects").session(session).sessionAttr(SpringController.STUDY, CURRENT_STUDY)
						.sessionAttr(SpringController.USER_ROLE, USER_ROLE)).andExpect(status().isFound());
	}
}
