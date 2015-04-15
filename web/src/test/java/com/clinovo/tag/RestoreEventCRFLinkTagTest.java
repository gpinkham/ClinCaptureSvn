package com.clinovo.tag;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.jsp.JspException;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RestoreEventCRFLinkTag.class)
public class RestoreEventCRFLinkTagTest {

	@Mock
	private RestoreEventCRFLinkTag restoreEventCRFLinkTag;

	@Test
	public void testThatDoStartTagDoesNotThrowAnExceptionIfObjectIsNull() throws JspException {
		assertEquals(0, restoreEventCRFLinkTag.doStartTag());
	}
}
