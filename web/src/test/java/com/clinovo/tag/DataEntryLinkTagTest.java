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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.jsp.JspException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DataEntryLinkTag.class)
public class DataEntryLinkTagTest {

	@Mock
	private DataEntryLinkTag dataEntryLinkTag;

	@Before
	public void setUp() throws JspException {
		Mockito.when(dataEntryLinkTag.doStartTag()).thenCallRealMethod();
	}

	@Test
	public void testThatDoStartTagDoesNotThrowAnExceptionIfObjectIsNull() throws JspException {
		assertEquals(0, dataEntryLinkTag.doStartTag());
	}
}
