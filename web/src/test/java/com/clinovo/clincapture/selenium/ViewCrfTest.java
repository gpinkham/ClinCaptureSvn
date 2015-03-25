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

import org.junit.Test;

import com.clinovo.clincapture.selenium.base.BaseTest;

@SuppressWarnings("deprecation")
public class ViewCrfTest extends BaseTest {

	/**
	 * ticket #48 Crash on Viewing CRFs
	 * 
	 * @throws Exception
	 */
	@Test
	public void testViewCrf() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		groupRowSelector = "//table[@class='aka_form_table'][1]/tbody/tr"; // for first we have to specify the group
																			// selector

		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening"); // view/enter data for: SSID ->
																				// 'TEST_SUBJECT_1' & event -> 'First
																				// Office Visit/Screening'

		selenium.click("//table[@id='crfListTable']/tbody/tr[8]/td[6]/a[2]/img");

		Thread.sleep(5000);

		assertTrue(selenium.getText("//h1").contains("Monthly Office Visit Follow-up Form 1.0"));
	}

}
