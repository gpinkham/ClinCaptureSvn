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

import com.clinovo.clincapture.selenium.base.BaseTest;
import org.junit.Test;

public class RandomDnAssignmentTest extends BaseTest {

	@Test
	public void startTest() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		testReasonForChange();
	}

	private void testReasonForChange() throws Exception {
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

		// the test ---->

		administrativeEditing("Monthly Office Visit Follow-up Form"); // administrative Editing of 'Monthly Office Visit
																		// Follow-up Form' CRF

		// set values
		setValue("input354", "11031703-1555-SS");

		// save the CRF
		saveCrf();

		// check alerts on the first TAB
		checkAlertForInput("input354");

		addDirectDn("input354", "DN for input354", DiscrepancyNoteType.REASON_FOR_CHANGE);

		// save the CRF
		saveCrf();

		// wait when the second tab will be opened
		waitForNextTab(2);

		// save the CRF
		saveCrf();

		administrativeEditing("Monthly Office Visit Follow-up Form"); // administrative Editing of 'Monthly Office Visit
																		// Follow-up Form' CRF

		// check DN's on the first TAB
		checkDNForInput("input354");

		checkDn("input354", "DN for input354", "Reason for Change", "Not Applicable", false);
	}
}
