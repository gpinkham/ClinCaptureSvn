package com.clinovo.rest.serializer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.security.PermissionChecker;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestContextHolder.class)
public class JsonSerializerTest {

	public static final String ACCEPT = "Accept";

	private JsonSerializer serializer;

	private MockHttpSession session;

	private MockHttpServletRequest request;

	@Mock
	private ServletRequestAttributes servletRequestAttributes;

	@Before
	public void before() {
		serializer = new JsonSerializer();
		session = new MockHttpSession();
		request = new MockHttpServletRequest();
		request.setSession(session);
		session.setAttribute(PermissionChecker.API_AUTHENTICATED_USER_DETAILS, new UserDetails());
		PowerMockito.mockStatic(RequestContextHolder.class);
		Whitebox.setInternalState(servletRequestAttributes, "request", request);
		PowerMockito.when(RequestContextHolder.currentRequestAttributes()).thenReturn(servletRequestAttributes);
	}

	@Test
	public void testThatCanWriteReturnsTrueIfAcceptIsNull() throws Exception {
		assertTrue(serializer.canWrite(UserDetails.class, MediaType.APPLICATION_JSON));
	}

	@Test
	public void testThatCanWriteReturnsTrueIfAcceptIsJson() throws Exception {
		request.addHeader(ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		assertTrue(serializer.canWrite(UserDetails.class, MediaType.APPLICATION_JSON));
	}

	@Test
	public void testThatCanWriteReturnsFalseIfAcceptIsXml() throws Exception {
		request.addHeader(ACCEPT, MediaType.APPLICATION_XML_VALUE);
		assertFalse(serializer.canWrite(UserDetails.class, MediaType.APPLICATION_JSON));
	}

	@Test
	public void testThatCanWriteReturnsTrueIfAcceptIsNotJsonAndItIsNotXml() throws Exception {
		request.addHeader(ACCEPT, MediaType.APPLICATION_ATOM_XML_VALUE);
		assertTrue(serializer.canWrite(UserDetails.class, MediaType.APPLICATION_JSON));
	}
}
