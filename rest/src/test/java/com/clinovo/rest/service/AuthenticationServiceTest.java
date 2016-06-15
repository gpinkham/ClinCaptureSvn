package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.junit.Test;

public class AuthenticationServiceTest extends BaseServiceTest {

	@Test
	public void testThatRestAPIReturns404IfRequestIsNotMapped() throws Exception {
		mockMvc.perform(post(API_WRONG_MAPPING).param("userName", rootUserName.concat(Long.toString(timestamp)))
				.param("password", rootUserPassword).param("studyName", defaultStudyName))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testThatAuthenticationServiceReturnsUnauthorizedIfUsernameIsWrong() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", rootUserName.concat(Long.toString(timestamp)))
				.param("password", rootUserPassword).param("studyName", defaultStudyName))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfUserNameParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("usErName", rootUserName).param("password", rootUserPassword)
				.param("studyName", defaultStudyName)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfUsernameIsMissing() throws Exception {
		mockMvc.perform(
				post(API_AUTHENTICATION).param("password", rootUserPassword).param("studyName", defaultStudyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfUsernameIsEmpty() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", "").param("password", rootUserPassword)
				.param("studyName", defaultStudyName)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsUnauthorizedIfPasswordIsWrong() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", rootUserName)
				.param("password", rootUserPassword.concat(Long.toString(timestamp)))
				.param("studyName", defaultStudyName)).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfPasswordIsMissing() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", rootUserName).param("studyName", defaultStudyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfPasswordIsEmpty() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", rootUserName).param("password", "")
				.param("studyName", defaultStudyName)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsUnauthorizedIfStudyNameIsWrong() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", rootUserName).param("password", rootUserPassword)
				.param("studyName", defaultStudyName.concat(Long.toString(timestamp))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationMethodReturnsBadRequestIfStudyNameParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", rootUserName).param("password", rootUserPassword)
				.param("stUdyName", defaultStudyName)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAuthenticationMethodReturnsUnauthorizedForUserThatIsNotAssignedToAnyStudy() throws Exception {
		createUserWithoutRole(UserType.SYSADMIN, currentScope.getId());
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", newUser.getName())
				.param("password", newUser.getPasswd()).param("studyName", currentScope.getName()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationOnStudyIsNotPossibleForCRC() throws Exception {
		createNewSite(currentScope.getId());
		createUserWithoutRole(UserType.SYSADMIN, newSite.getId());
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", newUser.getName())
				.param("password", newUser.getPasswd()).param("studyName", currentScope.getName()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationMethodReturnsOkForNewlyCreatedStudyAdministrator() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", newUser.getName())
				.param("password", newUser.getPasswd()).param("studyName", currentScope.getName()))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatStudyAdministratorWithoutAdministrativePrivilegesCannotBeAuthenticated() throws Exception {
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", newUser.getName())
				.param("password", newUser.getPasswd()).param("studyName", currentScope.getName()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationIsNotAllowedForStudyMonitor() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_MONITOR);
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", newUser.getName())
				.param("password", newUser.getPasswd()).param("studyName", currentScope.getName()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationIsNotAllowedForStudyEvaluator() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_EVALUATOR);
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", newUser.getName())
				.param("password", newUser.getPasswd()).param("studyName", currentScope.getName()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationIsNotAllowedForStudyCoder() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_CODER);
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", newUser.getName())
				.param("password", newUser.getPasswd()).param("studyName", currentScope.getName()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationOnSiteIsNotPossible() throws Exception {
		createNewSite(currentScope.getId());
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", rootUserName).param("password", rootUserPassword)
				.param("studyName", newSite.getName())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testThatAuthenticationMethodReturnsErrorIfStudyDoesNotExist() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", rootUserName).param("password", rootUserPassword)
				.param("studyName", "wrong study name!")).andExpect(status().isUnauthorized());
	}
}
