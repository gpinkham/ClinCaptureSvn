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
				.param("username", userName.concat(Long.toString(timestamp))).param("password", password)
				.param("studyname", studyName)).andExpect(status().isNotFound());
	}
	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfUsernameIsWrong() throws Exception {
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("username", userName.concat(Long.toString(timestamp))).param("password", password)
				.param("studyname", studyName)).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsBadRequestIfUsernameIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("username", "").param("password", password).param("studyname", studyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfPasswordIsWrong() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session).param("username", userName)
						.param("password", password.concat(Long.toString(timestamp))).param("studyname", studyName))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsBadRequestIfPasswordIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("username", userName).param("password", "").param("studyname", studyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfStudyNameIsWrong() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("username", userName).param("password", password)
						.param("studyname", studyName.concat(Long.toString(timestamp))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsBadRequestIfStudyNameIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("username", userName).param("password", password).param("studyname", ""))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedForUserThatIsNotAssignedToAnyStudy() throws Exception {
		createUserWithoutRole(UserType.SYSADMIN, studyBean.getId());
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("username", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyname", studyBean.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationOnStudyIsNotPossibleForCRC() throws Exception {
		createNewSite(studyBean.getId());
		createUserWithoutRole(UserType.SYSADMIN, newSite.getId());
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("username", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyname", studyBean.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationServiceReturnsOkForNewlyCreatedStudyAdministrator() throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
								.param("username", newUser.getName()).param("password", newUser.getPasswd())
								.param("studyname",
										studyBean
												.getName()))
				.andExpect(status().isOk())
				.andExpect(content().string(mediaType.equals(MediaType.APPLICATION_JSON)
						? StringContains.containsString("{\"username\":\"".concat(newUser.getName())
								.concat("\",\"userstatus\":\"").concat(newUser.getStatus().getName())
								.concat("\",\"studyname\":\"").concat(studyBean.getName())
								.concat("\",\"studystatus\":\"").concat(studyBean.getStatus().getName())
								.concat("\",\"role\":\"").concat(Role.STUDY_ADMINISTRATOR.getCode())
								.concat("\",\"usertype\":\"").concat(UserType.SYSADMIN.getCode()).concat("\"}"))
						: StringContains.containsString("<ODM Description=\"REST Data\"")));
	}

	@Test
	public void testThatStudyAdministratorWithoutAdministrativePrivilegesCannotBeAuthenticated() throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("username", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyname", studyBean.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationIsNotAllowedForStudyMonitor() throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_MONITOR);
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("username", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyname", studyBean.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationIsNotAllowedForStudyEvaluator() throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_EVALUATOR);
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("username", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyname", studyBean.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationIsNotAllowedForStudyCoder() throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_CODER);
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("username", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyname", studyBean.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationOnSiteIsNotPossible() throws Exception {
		createNewSite(studyBean.getId());
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("username", userName).param("password", password).param("studyname", newSite.getName()))
				.andExpect(status().isUnauthorized());
	}
}
