package com.clinovo.rest.security;

import com.clinovo.rest.service.BaseServiceTest;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PermissionCheckerTest.
 */
public class PermissionCheckerTest extends BaseServiceTest {

	@Test
	public void testThatPermissionCheckerReturnsForbiddenIfProtocolIsNotHttps() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).param("username", userName).param("password", password)
						.param("studyname", studyName)).andExpect(status().isForbidden());
	}

	@Test
	public void testThatPermissionCheckerReturnsBadRequestIfUsernameIsMissing() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).secure(true).param("password", password).param("studyname", studyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatPermissionCheckerReturnsBadRequestIfPasswordIsMissing() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).secure(true).param("username", userName).param("studyname", studyName))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatPermissionCheckerReturnsBadRequestIfStudyNameIsMissing() throws Exception {
		this.mockMvc.perform(
				post(API_AUTHENTICATION).secure(true).param("username", userName).param("password", password))
				.andExpect(status().isBadRequest());
	}

	// @Test
	public void testThatPermissionCheckerReturnsUnauthorizedIfUserIsNotAuthenticated() throws Exception {
		// this test will be implemented when at lest one service except the AuthenticationService will be available
		// -> isUnauthorized
	}
}
