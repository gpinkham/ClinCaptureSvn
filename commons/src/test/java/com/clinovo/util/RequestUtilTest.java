package com.clinovo.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestContextHolder.class)
public class RequestUtilTest {

	@Mock
	private ServletRequestAttributes servletRequestAttributes;

	@Before
	public void setUp() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setQueryString("total=2&action=1&ownerId=");
		request.setContextPath("/clincapture");
		request.setRequestURI("/viewSomething");

		PowerMockito.mockStatic(RequestContextHolder.class);
		Whitebox.setInternalState(servletRequestAttributes, "request", request);
		PowerMockito.when(RequestContextHolder.currentRequestAttributes()).thenReturn(servletRequestAttributes);
	}

	@Test
	public void testThatGetRelativeWithNewParametersMethodWorksFine() {
		assertEquals(RequestUtil.getRelativeWithNewParameters("total=", "ownerId="),
				"/viewSomething?total=&action=1&ownerId=");
		assertEquals(RequestUtil.getRelativeWithNewParameters("action=34", "ownerId=1"),
				"/viewSomething?total=2&action=34&ownerId=1");
		assertEquals(RequestUtil.getRelativeWithNewParameters("action=", "ownerId=1"),
				"/viewSomething?total=2&action=&ownerId=1");
		assertEquals(RequestUtil.getRelativeWithNewParameters("action=", "ownerId="),
				"/viewSomething?total=2&action=&ownerId=");
		assertEquals(RequestUtil.getRelativeWithNewParameters("newParam=12", "ownerId=56"),
				"/viewSomething?total=2&action=1&ownerId=56&newParam=12");
		assertEquals(RequestUtil.getRelativeWithNewParameters("ownerId=77", "newParam=212"),
				"/viewSomething?total=2&action=1&ownerId=77&newParam=212");
		assertEquals(RequestUtil.getRelativeWithNewParameters("total=89", "ownerId=8"),
				"/viewSomething?total=89&action=1&ownerId=8");
		assertEquals(RequestUtil.getRelativeWithNewParameters("total=99", "ownerId=8", "newParam=33"),
				"/viewSomething?total=99&action=1&ownerId=8&newParam=33");
	}
}
