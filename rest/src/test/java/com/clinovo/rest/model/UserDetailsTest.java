package com.clinovo.rest.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.clinovo.rest.security.PermissionChecker;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestContextHolder.class)
public class UserDetailsTest {

	private MockHttpSession session;

	private MockHttpServletRequest request;

	@Mock
	private ServletRequestAttributes servletRequestAttributes;

	@Before
	public void before() {
		session = new MockHttpSession();
		request = new MockHttpServletRequest();
		request.setSession(session);
		session.setAttribute(PermissionChecker.API_AUTHENTICATED_USER_DETAILS, new UserDetails());
		PowerMockito.mockStatic(RequestContextHolder.class);
		Whitebox.setInternalState(servletRequestAttributes, "request", request);
		PowerMockito.when(RequestContextHolder.currentRequestAttributes()).thenReturn(servletRequestAttributes);
	}

	@Test
	public void testThatDefaultConstructorSetsCorrectValuesForFields() throws Exception {
		UserDetails userDetails = new UserDetails();
		assertNull(userDetails.getUserName());
		assertNull(userDetails.getPassword());
		assertNull(userDetails.getStudyName());
		assertNull(userDetails.getRoleCode());
		assertNull(userDetails.getUserTypeCode());
	}

	@Test
	public void testThatGetCurrentUserDetailsReturnsUserDetailsCorrectly() throws Exception {
		assertNotNull(UserDetails.getCurrentUserDetails());
	}
}
