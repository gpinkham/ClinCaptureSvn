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

package com.clinovo.clincapture.saucelabs;

import org.junit.Test;

import com.clinovo.clincapture.saucelabs.base.BaseTest;

public class LinkToSdvFromSubjMatrixTest extends BaseTest {

	@Test
	public void startTest() throws Exception {
		testName = "Link to SDV from Matrix";
		login(ROOT, ROOT_PASSWORD);
		testLinkage();
	}

	private void testLinkage() throws Exception {
		selenium.open(SUBJECT_MATRIX_URL, PAGE_TIME_OUT);
		selenium.click("//a[contains(text(),'Source Data Verification')]");

		wait("selenium.isTextPresent(\"1.0.2\")");
		assertTrue(selenium.isTextPresent("1.0.2"));
	}

}
