package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

public class AuthenticationServiceOdmXmlTest extends BaseServiceTest {

	@Before
	public void setup() throws Exception {
		mediaType = MediaType.APPLICATION_XML;
		super.setup();
	}

	@Test
	public void testThatAuthenticationServiceReturnsNotFoundIfRequestIsNotMapped() throws Exception {
		this.mockMvc.perform(
				post(API_WRONG_MAPPING).accept(mediaType).secure(true)
						.param("username", userName.concat(Long.toString(timestamp))).param("password", password)
						.param("studyname", studyName)).andExpect(status().isNotFound());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfUsernameIsWrong() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).accept(mediaType).secure(true)
						.param("username", userName.concat(Long.toString(timestamp))).param("password", password)
						.param("studyname", studyName)).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsBadRequestIfUsernameIsEmpty() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", "")
						.param("password", password).param("studyname", studyName)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfPasswordIsWrong() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", userName)
						.param("password", password.concat(Long.toString(timestamp))).param("studyname", studyName))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsBadRequestIfPasswordIsEmpty() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", userName)
						.param("password", "").param("studyname", studyName)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfStudynameIsWrong() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", userName)
						.param("password", password).param("studyname", studyName.concat(Long.toString(timestamp))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsBadRequestIfStudyNameIsEmpty() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", userName)
						.param("password", password).param("studyname", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedForUserThatIsNotAssignedToAnyStudy() throws Exception {
		createUserWithoutRole(UserType.SYSADMIN, studyBean.getId());
		this.mockMvc.perform(
				post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", newUser.getName())
						.param("password", newUser.getPasswd()).param("studyname", studyBean.getName())).andExpect(
				status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedStudyAdministrator() throws Exception {
		createNewUser(studyBean.getId(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", newUser.getName())
								.param("password", newUser.getPasswd()).param("studyname", studyBean.getName()))
				.andExpect(status().isOk())
				.andExpect(content().string(StringContains.containsString("<Role>study_administrator</Role>")))
				.andExpect(content().string(StringContains.containsString("<ODM Description=\"REST Data\"")));
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedStudyMonitor() throws Exception {
		createNewUser(studyBean.getId(), UserType.SYSADMIN, Role.STUDY_MONITOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", newUser.getName())
								.param("password", newUser.getPasswd()).param("studyname", studyBean.getName()))
				.andExpect(status().isOk())
				.andExpect(content().string(StringContains.containsString("<Role>study_monitor</Role>")))
				.andExpect(content().string(StringContains.containsString("<ODM Description=\"REST Data\"")));
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedStudyEvaluator() throws Exception {
		createNewUser(studyBean.getId(), UserType.SYSADMIN, Role.STUDY_EVALUATOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", newUser.getName())
								.param("password", newUser.getPasswd()).param("studyname", studyBean.getName()))
				.andExpect(status().isOk())
				.andExpect(content().string(StringContains.containsString("<Role>study_evaluator</Role>")))
				.andExpect(content().string(StringContains.containsString("<ODM Description=\"REST Data\"")));
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedStudyCoder() throws Exception {
		createNewUser(studyBean.getId(), UserType.SYSADMIN, Role.STUDY_CODER);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", newUser.getName())
								.param("password", newUser.getPasswd()).param("studyname", studyBean.getName()))
				.andExpect(status().isOk())
				.andExpect(content().string(StringContains.containsString("<Role>study_coder</Role>")))
				.andExpect(content().string(StringContains.containsString("<ODM Description=\"REST Data\"")));
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedCRC() throws Exception {
		createNewSite(studyBean.getId());
		createNewUser(newSite.getId(), UserType.USER, Role.CLINICAL_RESEARCH_COORDINATOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", newUser.getName())
								.param("password", newUser.getPasswd()).param("studyname", newSite.getName()))
				.andExpect(status().isOk())
				.andExpect(
						content().string(StringContains.containsString("<Role>clinical_research_coordinator</Role>")))
				.andExpect(content().string(StringContains.containsString("<ODM Description=\"REST Data\"")));
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedInvestigator() throws Exception {
		createNewSite(studyBean.getId());
		createNewUser(newSite.getId(), UserType.USER, Role.INVESTIGATOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", newUser.getName())
								.param("password", newUser.getPasswd()).param("studyname", newSite.getName()))
				.andExpect(status().isOk())
				.andExpect(content().string(StringContains.containsString("<Role>investigator</Role>")))
				.andExpect(content().string(StringContains.containsString("<ODM Description=\"REST Data\"")));
	}

}
