package com.clinovo.rest.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import com.clinovo.rest.service.BaseServiceTest;

/**
 * PermissionCheckerTest.
 */
public class PermissionCheckerTest extends BaseServiceTest {

	@Test
	public void testThatPermissionCheckerReturnsForbiddenIfProtocolIsNotHttps() throws Exception {
		this.mockMvc.perform(post(API_AUTHENTICATION).param("username", userName).param("password", password)
				.param("studyname", studyName)).andExpect(status().isForbidden());
	}

	@Test
	public void testThatPermissionCheckerReturnsBadRequestIfUsernameIsMissing() throws Exception {
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).secure(true).param("password", password).param("studyname", studyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatPermissionCheckerReturnsBadRequestIfPasswordIsMissing() throws Exception {
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).secure(true).param("username", userName).param("studyname", studyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatPermissionCheckerReturnsBadRequestIfStudyNameIsMissing() throws Exception {
		this.mockMvc
				.perform(post(API_AUTHENTICATION).secure(true).param("username", userName).param("password", password))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatPermissionCheckerReturnsUnauthorizedIfUserIsNotAuthenticated() throws Exception {
		session.clearAttributes();
		this.mockMvc
				.perform(post(API_USER_CREATE).param("username", userName).param("firstname", "firstname")
						.param("lastname", "lastname").param("email", "user@gmail.com").param("phone", "+375232345678")
						.param("company", "company").param("usertype", "1").param("allowsoap", "false")
						.param("displaypassword", "true").param("role", "2").secure(true).session(session))
				.andExpect(status().isUnauthorized());
	}
}
