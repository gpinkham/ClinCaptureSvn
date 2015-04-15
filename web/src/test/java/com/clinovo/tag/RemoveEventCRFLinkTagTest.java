package com.clinovo.tag;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.jsp.JspException;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RemoveEventCRFLinkTag.class)
public class RemoveEventCRFLinkTagTest {

	@Mock
	private RemoveEventCRFLinkTag removeEventCRFLinkTag;

	@Test
	public void testThatDoStartTagDoesNotThrowAnExceptionIfObjectIsNull() throws JspException {
		assertEquals(0, removeEventCRFLinkTag.doStartTag());
	}
}
