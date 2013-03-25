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

public class RepeatingGroupTest3 extends BaseTest {

	@Test
	public void startTest() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		testRepeatingGroup();
	}

	private void testRepeatingGroup() throws Exception {
		groupRowSelector = "//table[@class='aka_form_table'][1]/tbody/tr"; // for first we have to specify the group
																			// selector

		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening"); // view/enter datafor: SSID ->
																				// 'TEST_SUBJECT_1' & event -> 'First
																				// Office Visit/Screening'

		// remove CRF data if it exists
		if (haveToRemoveCRFData("XForm")) {
			removeCRFData();
		}

		enterData("XForm"); // enter data for 'XForm' CRF

		// add 2 new rows in the end
		addRow();
		addRow();

		// set values
		setNewInputValue(2, "input981", "x");

		// save the CRF
		saveCrf();

		// check quantity of alerts
		checkQuantityOfAlertsForRG(2);

		// save the CRF
		saveCrf();

		continueEnteringData("XForm"); // continue entering data for 'XForm' CRF

		// check quantity of DNs
		checkQuantityOfDNsForRG(2);
	}

	protected void removeCRFData() throws Exception {
		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening"); // view/enter datafor: SSID ->
																				// 'TEST_SUBJECT_1' & event -> 'First
																				// Office Visit/Screening'
		removeCRFData("XForm"); // remove CRF data
		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening"); // view/enter datafor: SSID ->
																				// 'TEST_SUBJECT_1' & event -> 'First
																				// Office Visit/Screening'
	}

}
