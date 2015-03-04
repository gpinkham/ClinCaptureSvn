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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EscapeTag.class)
public class EscapeTagTest {

	@Mock
	private EscapeTag escapeTag;

	@Mock
	private PageContext pageContext;

	@Mock
	private JspWriter jspWriter;

	@Before
	public void setUp() throws JspException {
		Mockito.when(pageContext.getOut()).thenReturn(jspWriter);
		Whitebox.setInternalState(escapeTag, "pageContext", pageContext);
		Mockito.when(escapeTag.doStartTag()).thenCallRealMethod();
	}

	@Test
	public void testThatDoStartTagWritesCorrectValue() throws Exception {
		Whitebox.setInternalState(escapeTag, "content", "<script>\n var x = 0;\n </script>");
		Whitebox.setInternalState(escapeTag, "tagNames", "script");
		escapeTag.doStartTag();
		Mockito.verify(jspWriter).write("<!-- script -->");
	}
}
