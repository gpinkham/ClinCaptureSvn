package com.clinovo.rest.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import com.clinovo.rest.service.BaseServiceTest;

/**
 * PermissionCheckerTest.
 */
public class PermissionCheckerTest extends BaseServiceTest {

	@Test
	public void testThatPermissionCheckerReturnsBadRequestIfUsernameIsMissing() throws Exception {
		mockMvc.perform(
				post(API_AUTHENTICATION).param("password", rootUserPassword).param("studyname", defaultStudyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatPermissionCheckerReturnsBadRequestIfPasswordIsMissing() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("username", rootUserName).param("studyname", defaultStudyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatPermissionCheckerReturnsBadRequestIfStudyNameIsMissing() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("username", rootUserName).param("password", rootUserPassword))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatPermissionCheckerReturnsUnauthorizedIfUserIsNotAuthenticated() throws Exception {
		session.clearAttributes();
		mockMvc.perform(post(API_USER_CREATE_USER).param("userName", rootUserName).param("firstName", "firstname")
				.param("lastName", "lastname").param("email", "user@gmail.com").param("phone", "+375232345678")
				.param("company", "company").param("userType", "1").param("allowSoap", "false")
				.param("displayPassword", "true").param("role", "2")).andExpect(status().isUnauthorized());
	}
}
