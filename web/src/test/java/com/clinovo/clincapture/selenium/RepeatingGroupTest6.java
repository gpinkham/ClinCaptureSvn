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

public class RepeatingGroupTest6 extends BaseTest {

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

		// add 3 new rows in the end
		addRow();
		addRow();
		addRow();

		// set values
		setInputValue(1, "input981", "z");
		setInputValue(1, "input982", "1");
		setNewInputValue(4, "input981", "x");
		setNewInputValue(4, "input982", "16");

		// remove rows
		removeRow(2);
		removeRow(2);

		// add 4 new rows in the end
		addRow();
		addRow();
		addRow();
		addRow();

		// set values
		setNewInputValue(8, "input981", "x");
		setNewInputValue(8, "input982", "1");

		// remove rows
		removeRow(3);
		removeRow(3);
		removeRow(3);

		// add 2 new rows in the end
		addRow();
		addRow();

		// set values
		setNewInputValue(10, "input981", "z");
		setNewInputValue(10, "input982", "1");

		// remove rows
		removeRow(4);

		// add 2 new rows in the end
		addRow();
		addRow();

		// set values
		setNewInputValue(12, "input981", "z");

		// remove rows
		removeRow(5);

		// add 2 new rows in the end
		addRow();
		addRow();

		// remove rows
		removeRow(6);

		// add 2 new rows in the end
		addRow();
		addRow();

		// remove rows
		removeRow(7);

		// set values
		setNewInputValue(16, "input982", "16");

		// add 2 new rows in the end
		addRow();
		addRow();

		// set values
		setNewInputValue(18, "input981", "x");

		// remove rows
		removeRow(8);

		// save the CRF
		saveCrf();

		// check quantity of alerts
		checkQuantityOfAlertsForRG(7);

		// add DN for first row
		addDn(1, "input981", "DN for -> row 1 input 1");

		// add DN for second row
		addDn(2, "input985", "DN for -> row 2 input 5");

		// add DN for 4th row
		addDn(4, "input981", "DN for -> row 4 input 1");

		// add DN for 5th row
		addDn(5, "input981", "DN for -> row 5 input 1");

		// add DN for 6th row
		addDn(6, "input981", "DN for -> row 6 input 1");

		// add DN for 7th row
		addDn(7, "input981", "DN for -> row 7 input 1");

		// add DN for 7th row
		addDn(7, "input985", "DN for -> row 7 input 5");

		// save the CRF
		saveCrf();

		continueEnteringData("XForm"); // continue entering data for 'XForm' CRF

		// check quantity of DNs
		checkQuantityOfDNsForRG(7);

		// check the added DN's
		checkDn(1, "input981", "DN for -> row 1 input 1");
		checkDn(2, "input985", "DN for -> row 2 input 5");
		checkDn(4, "input981", "DN for -> row 4 input 1");
		checkDn(5, "input981", "DN for -> row 5 input 1");
		checkDn(6, "input981", "DN for -> row 6 input 1");
		checkDn(7, "input981", "DN for -> row 7 input 1");
		checkDn(7, "input985", "DN for -> row 7 input 5");
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
/*
 * z 1 x 16 x 1 z 1 z
 * 
 * 16 x
 */

