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

/**
 * User: Pavel Date: 10.11.12
 */
public class DefaultDnAssignmentTest extends BaseTest {

	/**
	 * ticket #1 assign DN with types Query and Failed Validation Check to eventCrf updater/owner by default
	 */
	@Test
	public void testCheckDefaultAssignment() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		groupRowSelector = "//table[@class='aka_form_table'][1]/tbody/tr"; // for first we have to specify the group
																			// selector

		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening"); // view/enter data for: SSID ->
																				// 'TEST_SUBJECT_1' & event -> 'First
																				// Office Visit/Screening'

		// remove CRF data if it exists
		if (haveToRemoveCRFData("Monthly Office Visit Follow-up Form")) {
			removeCRFData();
		}

		// fill first tab
		fillTab1();

		// save the CRF
		saveCrf();

		waitForNextTab(2);

		// fill second tab
		fillTab2();

		// mark CRF Complete
		markCRFComplete();

		// save the CRF
		saveCrf();

		// TODO should be removed after the fix of ticket #117
		Thread.sleep(2000);
		if (selenium.isElementPresent("//input[@id='seh']")) {
			exitCrf();
		}

		administrativeEditing("Monthly Office Visit Follow-up Form"); // administrative Editing of 'Monthly Office Visit
																		// Follow-up Form' CRF

		addDirectDn("input353", "DN for input353", DiscrepancyNoteType.QUERY);
		checkDn("input353", "DN for input353", "Query", "New", true);

		addDirectDn("input354", "DN for input354", DiscrepancyNoteType.FAILED_VALIDATION_CHECK);
		checkDn("input354", "DN for input354", "Failed Validation Check", "New", ROOT);
	}
}
