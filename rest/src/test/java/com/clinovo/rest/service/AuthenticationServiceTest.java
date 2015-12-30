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
		this.mockMvc
				.perform(post(API_WRONG_MAPPING).accept(MediaType.APPLICATION_XML).secure(true).session(session)
						.param("userName", rootUserName.concat(Long.toString(timestamp)))
						.param("password", rootUserPassword).param("studyName", defaultStudyName))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfUsernameIsWrong() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("userName", rootUserName.concat(Long.toString(timestamp)))
						.param("password", rootUserPassword).param("studyName", defaultStudyName))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfUserNameParameterHasATypo() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session).param("usErName", rootUserName)
						.param("password", rootUserPassword).param("studyName", defaultStudyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfUsernameIsMissing() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("password", rootUserPassword).param("studyName", defaultStudyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfUsernameIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session).param("userName", "")
						.param("password", rootUserPassword).param("studyName", defaultStudyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsUnauthorizedIfPasswordIsWrong() throws Exception {
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", rootUserName).param("password", rootUserPassword.concat(Long.toString(timestamp)))
				.param("studyName", defaultStudyName)).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfPasswordIsMissing() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("userName", rootUserName).param("studyName", defaultStudyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfPasswordIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("userName", rootUserName).param("password", "").param("studyName", defaultStudyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsUnauthorizedIfStudyNameIsWrong() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("userName", rootUserName).param("password", rootUserPassword)
						.param("studyName", defaultStudyName.concat(Long.toString(timestamp))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfStudyNameParameterHasATypo() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session).param("userName", rootUserName)
						.param("password", rootUserPassword).param("stUdyName", defaultStudyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfStudyNameIsMissing() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("userName", rootUserName).param("password", rootUserPassword))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfStudyNameIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
						.param("userName", rootUserName).param("password", rootUserPassword).param("studyName", ""))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsUnauthorizedForUserThatIsNotAssignedToAnyStudy() throws Exception {
		createUserWithoutRole(UserType.SYSADMIN, currentScope.getId());
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyName", currentScope.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationOnStudyIsNotPossibleForCRC() throws Exception {
		createNewSite(currentScope.getId());
		createUserWithoutRole(UserType.SYSADMIN, newSite.getId());
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyName", currentScope.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationMethodReturnsOkForNewlyCreatedStudyAdministrator() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
								.param("userName", newUser.getName()).param("password", newUser.getPasswd())
								.param("studyName",
										currentScope
												.getName()))
				.andExpect(
						status().isOk())
				.andExpect(content().string(mediaType.equals(MediaType.APPLICATION_JSON)
						? StringContains.containsString("{\"userName\":\"".concat(newUser.getName())
								.concat("\",\"userStatus\":\"").concat(newUser.getStatus().getName())
								.concat("\",\"studyName\":\"").concat(currentScope.getName())
								.concat("\",\"studyStatus\":\"").concat(currentScope.getStatus().getName())
								.concat("\",\"role\":\"").concat(Role.STUDY_ADMINISTRATOR.getCode())
								.concat("\",\"userType\":\"").concat(UserType.SYSADMIN.getCode()).concat("\"}"))
						: StringContains.containsString("<ODM Description=\"REST Data\"")));
	}

	@Test
	public void testThatStudyAdministratorWithoutAdministrativePrivilegesCannotBeAuthenticated() throws Exception {
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyName", currentScope.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationIsNotAllowedForStudyMonitor() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_MONITOR);
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyName", currentScope.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationIsNotAllowedForStudyEvaluator() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_EVALUATOR);
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyName", currentScope.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationIsNotAllowedForStudyCoder() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_CODER);
		this.mockMvc.perform(post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session)
				.param("userName", newUser.getName()).param("password", newUser.getPasswd())
				.param("studyName", currentScope.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationOnSiteIsNotPossible() throws Exception {
		createNewSite(currentScope.getId());
		this.mockMvc.perform(
				post(API_AUTHENTICATION).accept(mediaType).secure(true).session(session).param("userName", rootUserName)
						.param("password", rootUserPassword).param("studyName", newSite.getName()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatChangeScopeMethodReturnsErrorIfStudyNameIsSite() throws Exception {
		createNewSite(currentScope.getId());
		this.mockMvc.perform(post(API_CHANGE_SCOPE).accept(mediaType).secure(true).session(session).param("studyName",
				newSite.getName())).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatChangeScopeMethodReturnsErrorIfUserIsNotAssignedToStudy() throws Exception {
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		this.mockMvc.perform(post(API_CHANGE_SCOPE).accept(mediaType).secure(true).session(session).param("studyName",
				defaultStudyName)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatChangeScopeMethodReturnsErrorIfStudyDoesNotExist() throws Exception {
		this.mockMvc.perform(post(API_CHANGE_SCOPE).accept(mediaType).secure(true).session(session).param("studyName",
				defaultStudyName.concat(Long.toString(timestamp)))).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatChangeScopeMethodReturnsBadRequestIfStudyNameIsMissing() throws Exception {
		this.mockMvc.perform(post(API_CHANGE_SCOPE).accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatChangeScopeMethodReturnsBadRequestIfStudyNameIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_CHANGE_SCOPE).accept(mediaType).secure(true).session(session).param("studyName", ""))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatChangeScopeMethodReturnsBadRequestIfStudyNameParameterHasATypo() throws Exception {
		this.mockMvc.perform(post(API_CHANGE_SCOPE).accept(mediaType).secure(true).session(session).param("stuDyName",
				currentScope.getName())).andExpect(status().isBadRequest());
	}
}
