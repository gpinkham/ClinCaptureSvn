package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.springframework.http.MediaType;

public class AuthenticationServiceTest extends BaseServiceTest {

	@Test
	public void testThatRestAPIReturns404IfRequestIsNotMapped() throws Exception {
		this.mockMvc.perform(post(API_WRONG_MAPPING).accept(MediaType.APPLICATION_XML).secure(true).session(session)
				.param("userName", userName.concat(Long.toString(timestamp))).param("password", password)
				.param("studyName", studyName)).andExpect(status().isNotFound());
	}
	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfUsernameIsWrong() throws Exception {
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", userName.concat(Long.toString(timestamp))).param("password", password)
				.param("studyName", studyName)).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsBadRequestIfUsernameIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", "").param("password", password).param("studyName", studyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfPasswordIsWrong() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session).param("userName", userName)
						.param("password", password.concat(Long.toString(timestamp))).param("studyName", studyName))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsBadRequestIfPasswordIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("userName", userName).param("password", "").param("studyName", studyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfStudyNameIsWrong() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("userName", userName).param("password", password)
						.param("studyName", studyName.concat(Long.toString(timestamp))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsBadRequestIfStudyNameIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("userName", userName).param("password", password).param("studyName", ""))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedForUserThatIsNotAssignedToAnyStudy() throws Exception {
		createUserWithoutRole(UserType.SYSADMIN, studyBean.getId());
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyName", studyBean.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationOnStudyIsNotPossibleForCRC() throws Exception {
		createNewSite(studyBean.getId());
		createUserWithoutRole(UserType.SYSADMIN, newSite.getId());
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyName", studyBean.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedStudyAdministrator() throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
								.param("userName", newUser.getName()).param("password", newUser.getPasswd())
								.param("studyName",
										studyBean
												.getName()))
				.andExpect(status().isOk())
				.andExpect(content().string(mediaType.equals(MediaType.APPLICATION_JSON)
						? StringContains.containsString("{\"userName\":\"".concat(newUser.getName())
								.concat("\",\"userStatus\":\"").concat(newUser.getStatus().getName())
								.concat("\",\"studyName\":\"").concat(studyBean.getName())
								.concat("\",\"studyStatus\":\"").concat(studyBean.getStatus().getName())
								.concat("\",\"role\":\"").concat(Role.STUDY_ADMINISTRATOR.getCode())
								.concat("\",\"userType\":\"").concat(UserType.SYSADMIN.getCode()).concat("\"}"))
						: StringContains.containsString("<ODM Description=\"REST Data\"")));
	}

	@Test
	public void testThatStudyAdministratorWithoutAdministrativePrivilegesCannotBeAuthenticated() throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyName", studyBean.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationIsNotAllowedForStudyMonitor() throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_MONITOR);
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyName", studyBean.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationIsNotAllowedForStudyEvaluator() throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_EVALUATOR);
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyName", studyBean.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationIsNotAllowedForStudyCoder() throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_CODER);
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyName", studyBean.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationOnSiteIsNotPossible() throws Exception {
		createNewSite(studyBean.getId());
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("userName", userName).param("password", password).param("studyName", newSite.getName()))
				.andExpect(status().isUnauthorized());
	}
}
