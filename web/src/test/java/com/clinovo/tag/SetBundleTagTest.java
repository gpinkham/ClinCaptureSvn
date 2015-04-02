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
import java.util.ResourceBundle;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.clinovo.i18n.LocaleResolver;

/**
 * SetBundleTagTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SetBundleTag.class, LocaleResolver.class, ResourceBundleProvider.class})
public class SetBundleTagTest {

	@Mock
	private SetBundleTag setBundleTag;

	@Mock
	private PageContext pageContext;

	@Mock
	private ResourceBundle resourceBundle;

	@Before
	public void setUp() throws JspException {
		PowerMockito.mockStatic(LocaleResolver.class);
		PowerMockito.when(LocaleResolver.getLocale()).thenReturn(Locale.ENGLISH);
		Whitebox.setInternalState(setBundleTag, "pageContext", pageContext);
		PowerMockito.mockStatic(ResourceBundleProvider.class);
		PowerMockito.when(ResourceBundleProvider.getResBundle("org.akaza.openclinica.i18n.words", Locale.ENGLISH))
				.thenReturn(resourceBundle);
		Whitebox.setInternalState(setBundleTag, "var", "resword");
		Whitebox.setInternalState(setBundleTag, "basename", "org.akaza.openclinica.i18n.words");
		Mockito.when(setBundleTag.doEndTag()).thenCallRealMethod();
	}

	@Test
	public void testThatEndTagWritesLocalizationContextIntoTheVar() throws Exception {
		setBundleTag.doEndTag();
		Mockito.verify(pageContext).setAttribute(Mockito.anyString(), Mockito.anyObject(), Mockito.anyInt());
	}
}
