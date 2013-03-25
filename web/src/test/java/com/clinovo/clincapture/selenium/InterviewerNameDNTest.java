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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.selenium;

import org.junit.Test;

import com.clinovo.clincapture.selenium.base.BaseTest;

/**
 * User: Pavel Date: 11.11.12
 */
public class InterviewerNameDNTest extends BaseTest {

	/**
	 * ticket #73 generating a DN on Interviewer Name generates an error if viewed again
	 */
	@Test
	public void testInterviewerNameDNView() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		groupRowSelector = "//table[@class='aka_form_table'][1]/tbody/tr"; // for first we have to specify the group
																			// selector

		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening"); // view/enter data for: SSID ->
																				// 'TEST_SUBJECT_1' & event -> 'First
																				// Office Visit/Screening'
		administrativeEditing("Monthly Office Visit Follow-up Form"); // administrative Editing of 'Monthly Office Visit
																		// Follow-up Form' CRF

		selenium.click("//tr[@id='CRF_infobox_closed']/td/div/div[2]/div[2]/a/b");

		if (!selenium.isElementPresent("//img[@id='flag_interviewer' and @src='images/icon_Note.gif']")) {
			addDirectDn("interviewer", "DN for interviewer", DiscrepancyNoteType.QUERY);
		}

		checkDn("interviewer", "DN for interviewer", "Query", "New", false);
	}
}
