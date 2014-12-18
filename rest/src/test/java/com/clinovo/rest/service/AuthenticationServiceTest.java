package com.clinovo.rest.service;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthenticationServiceTest.
 */
public class AuthenticationServiceTest extends BaseServiceTest {

	@Test
	public void testThatAuthenticationServiceReturnsNotFoundIfRequestIsNotMapped() throws Exception {
		this.mockMvc.perform(
				post(API_WRONG_MAPPING).secure(true).param("username", userName.concat(Long.toString(timestamp)))
						.param("password", password).param("studyname", studyName)).andExpect(status().isNotFound());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfUsernameIsWrong() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).secure(true).param("username", userName.concat(Long.toString(timestamp)))
						.param("password", password).param("studyname", studyName))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsBadRequestIfUsernameIsEmpty() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).secure(true).param("username", "").param("password", password)
						.param("studyname", studyName)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfPasswordIsWrong() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).secure(true).param("username", userName)
						.param("password", password.concat(Long.toString(timestamp))).param("studyname", studyName))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsBadRequestIfPasswordIsEmpty() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).secure(true).param("username", userName).param("password", "")
						.param("studyname", studyName)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfStudynameIsWrong() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).secure(true).param("username", userName).param("password", password)
						.param("studyname", studyName.concat(Long.toString(timestamp)))).andExpect(
				status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsBadRequestIfStudyNameIsEmpty() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).secure(true).param("username", userName).param("password", password)
						.param("studyname", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedForUserThatIsNotAssignedToAnyStudy() throws Exception {
		createUserWithoutRole(UserType.SYSADMIN, studyBean.getId());
		this.mockMvc.perform(
				post(API_AUTHENTICATION).secure(true).param("username", newUser.getName())
						.param("password", newUser.getPasswd()).param("studyname", studyBean.getName())).andExpect(
				status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedStudyAdministrator() throws Exception {
		createNewUser(studyBean.getId(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).secure(true).param("username", newUser.getName())
								.param("password", newUser.getPasswd()).param("studyname", studyBean.getName()))
				.andExpect(status().isOk())
				.andExpect(
						content().string(
								"{\"username\":\"".concat(newUser.getName()).concat("\",\"studyname\":\"")
										.concat(studyBean.getName()).concat("\",\"role\":\"")
										.concat(Role.STUDY_ADMINISTRATOR.getCode()).concat("\",\"usertype\":\"")
										.concat(UserType.SYSADMIN.getCode()).concat("\"}")));
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedStudyMonitor() throws Exception {
		createNewUser(studyBean.getId(), UserType.SYSADMIN, Role.STUDY_MONITOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).secure(true).param("username", newUser.getName())
								.param("password", newUser.getPasswd()).param("studyname", studyBean.getName()))
				.andExpect(status().isOk())
				.andExpect(
						content().string(
								"{\"username\":\"".concat(newUser.getName()).concat("\",\"studyname\":\"")
										.concat(studyBean.getName()).concat("\",\"role\":\"")
										.concat(Role.STUDY_MONITOR.getCode()).concat("\",\"usertype\":\"")
										.concat(UserType.SYSADMIN.getCode()).concat("\"}")));
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedStudyEvaluator() throws Exception {
		createNewUser(studyBean.getId(), UserType.SYSADMIN, Role.STUDY_EVALUATOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).secure(true).param("username", newUser.getName())
								.param("password", newUser.getPasswd()).param("studyname", studyBean.getName()))
				.andExpect(status().isOk())
				.andExpect(
						content().string(
								"{\"username\":\"".concat(newUser.getName()).concat("\",\"studyname\":\"")
										.concat(studyBean.getName()).concat("\",\"role\":\"")
										.concat(Role.STUDY_EVALUATOR.getCode()).concat("\",\"usertype\":\"")
										.concat(UserType.SYSADMIN.getCode()).concat("\"}")));
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedStudyCoder() throws Exception {
		createNewUser(studyBean.getId(), UserType.SYSADMIN, Role.STUDY_CODER);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).secure(true).param("username", newUser.getName())
								.param("password", newUser.getPasswd()).param("studyname", studyBean.getName()))
				.andExpect(status().isOk())
				.andExpect(
						content().string(
								"{\"username\":\"".concat(newUser.getName()).concat("\",\"studyname\":\"")
										.concat(studyBean.getName()).concat("\",\"role\":\"")
										.concat(Role.STUDY_CODER.getCode()).concat("\",\"usertype\":\"")
										.concat(UserType.SYSADMIN.getCode()).concat("\"}")));
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedCRC() throws Exception {
		createNewSite(studyBean.getId());
		createNewUser(newSite.getId(), UserType.USER, Role.CLINICAL_RESEARCH_COORDINATOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).secure(true).param("username", newUser.getName())
								.param("password", newUser.getPasswd()).param("studyname", newSite.getName()))
				.andExpect(status().isOk())
				.andExpect(
						content().string(
								"{\"username\":\"".concat(newUser.getName()).concat("\",\"studyname\":\"")
										.concat(newSite.getName()).concat("\",\"role\":\"")
										.concat(Role.CLINICAL_RESEARCH_COORDINATOR.getCode())
										.concat("\",\"usertype\":\"").concat(UserType.USER.getCode()).concat("\"}")));
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedInvestigator() throws Exception {
		createNewSite(studyBean.getId());
		createNewUser(newSite.getId(), UserType.USER, Role.INVESTIGATOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).secure(true).param("username", newUser.getName())
								.param("password", newUser.getPasswd()).param("studyname", newSite.getName()))
				.andExpect(status().isOk())
				.andExpect(
						content().string(
								"{\"username\":\"".concat(newUser.getName()).concat("\",\"studyname\":\"")
										.concat(newSite.getName()).concat("\",\"role\":\"")
										.concat(Role.INVESTIGATOR.getCode()).concat("\",\"usertype\":\"")
										.concat(UserType.USER.getCode()).concat("\"}")));
	}

}
