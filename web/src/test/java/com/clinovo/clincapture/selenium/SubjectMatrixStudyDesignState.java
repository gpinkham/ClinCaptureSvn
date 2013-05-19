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

package com.clinovo.clincapture.selenium;

import com.clinovo.clincapture.selenium.base.BaseTest;
import org.junit.After;
import org.junit.Test;

/**
 * User: Pavel Date: 22.11.12
 */
public class SubjectMatrixStudyDesignState extends BaseTest {

	private static String STUDY_IN_DESIGN_MESSAGE = "The Study's status needs to be 'Available' in order to enter data!";

	// ticket #82
	@Test
	public void testSubjectMatrixInDesignState() throws Exception {

		login(ROOT, ROOT_PASSWORD);

		changeStudyStatus("Design");

		openSubjectMatrixPage();

		assertTextPresented(STUDY_IN_DESIGN_MESSAGE);
		assertElementPresented("//div[text()='First Office Visit/Screening']");
	}

	@Override
	@After
	public void tearDown() throws Exception {
		changeStudyStatus("Available");

		super.tearDown();
	}

}
