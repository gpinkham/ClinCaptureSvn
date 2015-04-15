/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package com.clinovo.tag;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.akaza.openclinica.dao.core.CoreResources;
import org.apache.commons.lang3.LocaleUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.clinovo.i18n.LocaleResolver;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CalendarTag.class)
public class CalendarTagTest {

	@Mock
	private PageContext pageContext;

	@Mock
	private CalendarTag calendarTag;

	@Mock
	private ServletContext servletContext;

	@Mock
	private MockHttpServletRequest request;

	@Mock
	private JspWriter jspWriter;

	private MockHttpSession session;

	@Before
	public void setUp() throws Exception {
		session = new MockHttpSession();
		CoreResources.CALENDAR_LOCALES.add(Locale.ENGLISH.toString());
		Mockito.when(pageContext.getOut()).thenReturn(jspWriter);
		Mockito.when(pageContext.getRequest()).thenReturn(request);
		Mockito.when(request.getSession()).thenReturn(session);
		Mockito.when(pageContext.getSession()).thenReturn(session);
		LocaleResolver.updateLocale(session, Locale.ENGLISH);
		Mockito.when(pageContext.getServletContext()).thenReturn(servletContext);
		Mockito.when(servletContext.getContextPath()).thenReturn("/clincapture");
		Whitebox.setInternalState(calendarTag, "pageContext", pageContext);
		Mockito.when(calendarTag.doStartTag()).thenCallRealMethod();
		CoreResources.CALENDAR_LOCALES.clear();
	}

	@Test
	public void testThatDoStartTagDoesNotThrowAnExceptionIfSessionIsEmpty() throws Exception {
		calendarTag.doStartTag();
	}

	@Test
	public void testThatForESMXLocaleTheDatepickerESWillBeChosen() throws Exception {
		CoreResources.CALENDAR_LOCALES.add(LocaleUtils.toLocale("es").toString());
		LocaleResolver.updateLocale(session, LocaleUtils.toLocale("es_MX"));
		calendarTag.doStartTag();
		Mockito.verify(jspWriter)
				.write("<script type=\"text/javascript\" src=\"/clincapture/includes/calendar/locales/datepicker-es.js\"></script><script>$.datepicker.regional['es']</script><link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"/clincapture/includes/calendar/css/calendar_blue.css\"/>");
	}

	@Test
	public void testThatForESMXLocaleTheDatepickerESMXWillBeChosen() throws Exception {
		CoreResources.CALENDAR_LOCALES.add(LocaleUtils.toLocale("es").toString());
		CoreResources.CALENDAR_LOCALES.add(LocaleUtils.toLocale("es_MX").toString());
		LocaleResolver.updateLocale(session, LocaleUtils.toLocale("es_MX"));
		calendarTag.doStartTag();
		Mockito.verify(jspWriter)
				.write("<script type=\"text/javascript\" src=\"/clincapture/includes/calendar/locales/datepicker-es_MX.js\"></script><script>$.datepicker.regional['es_MX']</script><link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"/clincapture/includes/calendar/css/calendar_blue.css\"/>");
	}

	@Test
	public void testThatForESLocaleTheDatepickerESWillBeChosen() throws Exception {
		CoreResources.CALENDAR_LOCALES.add(LocaleUtils.toLocale("es").toString());
		CoreResources.CALENDAR_LOCALES.add(LocaleUtils.toLocale("es_MX").toString());
		LocaleResolver.updateLocale(session, LocaleUtils.toLocale("es"));
		calendarTag.doStartTag();
		Mockito.verify(jspWriter)
				.write("<script type=\"text/javascript\" src=\"/clincapture/includes/calendar/locales/datepicker-es.js\"></script><script>$.datepicker.regional['es']</script><link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"/clincapture/includes/calendar/css/calendar_blue.css\"/>");
	}
}
