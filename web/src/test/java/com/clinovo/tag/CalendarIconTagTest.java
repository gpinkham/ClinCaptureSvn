package com.clinovo.tag;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.jsp.JspException;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CalendarIconTag.class)
public class CalendarIconTagTest {

	@Mock
	private CalendarIconTag calendarIconTag;

	@Before
	public void setUp() throws JspException {
		Mockito.when(calendarIconTag.doStartTag()).thenCallRealMethod();
	}

	@Test
	public void testThatDoStartTagDoesNotThrowsAnExceptionIfRequestIsEmpty() throws JspException {
		assertEquals(0, calendarIconTag.doStartTag());
	}
}
