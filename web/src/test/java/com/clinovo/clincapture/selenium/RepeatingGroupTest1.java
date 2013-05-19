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

public class RepeatingGroupTest1 extends BaseTest {

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
		if (haveToRemoveCRFData("Protocol Deviation Form")) {
			removeCRFData();
		}

		enterData("Protocol Deviation Form"); // enter data for 'Protocol Deviation Form' CRF

		// set values
		setSelectValue(1, "input399", "label=Test/s not performed");
		setSelectValue(1, "input407", "label=Yes");

		// add 3 new rows in the end
		addRow();
		addRow();
		addRow();

		// set value
		setNewInputValue(4, "input398", "06-Aug-2012");

		// remove rows
		removeRow(2);
		removeRow(2);

		// add 4 new rows in the end
		addRow();
		addRow();
		addRow();
		addRow();

		// set values
		setNewInputValue(8, "input398", "07-Aug-2012");
		setNewSelectValue(8, "input399", "label=Subject enrolled but Exclusion Criteria was met");

		// remove rows
		removeRow(3);
		removeRow(3);
		removeRow(3);

		// add 2 new rows in the end
		addRow();
		addRow();

		// set value
		setNewSelectValue(10, "input399", "label=Subject enrolled but Exclusion Criteria was met");

		// remove row
		removeRow(4);

		// add 2 new rows in the end
		addRow();
		addRow();

		// set value
		setNewSelectValue(12, "input399", "label=Subject enrolled but Exclusion Criteria was met");

		// remove row 9 and leave only the row 10
		removeRow(5);

		// add 2 new rows in the end
		addRow();
		addRow();

		// set values
		setNewInputValue(14, "input398", "10-Aug-2012");

		// remove row
		removeRow(6);

		// add 2 new rows in the end
		addRow();
		addRow();

		// set value
		setNewSelectValue(16, "input407", "label=Yes");

		// remove row
		removeRow(7);

		// add 2 new rows in the end
		addRow();
		addRow();

		// set values
		setNewSelectValue(18, "input399", "label=Study visit out of time window");

		// remove row
		removeRow(8);

		// save the CRF
		saveCrf();

		// check quantity of alerts
		checkQuantityOfAlertsForRG(14);

		// add DN for first row
		addDn(1, "input398", "DN for -> row first input 1");

		// add DN for 6th row
		addDn(6, "input399", "DN for -> row 6 input 2");

		// add DN for last row
		addDn(8, "input398", "DN for -> row last input 1");

		// save the CRF
		saveCrf();

		// check quantity of alerts
		checkQuantityOfAlertsForRG(11);

		// add DN for 4th row
		addDn(4, "input398", "DN for -> row 4 input 1");

		// add DN for 5th row
		addDn(5, "input398", "DN for -> row 5 input 1");

		// add DN for 7th row -> input 1
		addDn(7, "input398", "DN for -> row 7 input 1");

		// add DN for 7th row -> input 2
		addDn(7, "input399", "DN for -> row 7 input 2");

		// add DN for 2th row -> input 2
		addDn(2, "input399", "DN for -> row 2 input 2");

		// add DN for 2th row -> last input
		addDn(2, "input407", "DN for -> row 2 last input");

		// add DN for 6th row -> last input
		addDn(6, "input407", "DN for -> row 6 last input");

		// add DN for 8th row -> last input
		addDn(8, "input407", "DN for -> row 8 last input");

		// set value == No for rows: 3,4,5
		setSelectValue(3, "input407", "label=No");
		setSelectValue(4, "input407", "label=No");
		setSelectValue(5, "input407", "label=No");

		// save the CRF
		saveCrf();

		continueEnteringData("Protocol Deviation Form"); // continue entering data for 'Protocol Deviation Form' CRF

		// check quantity of DNs
		checkQuantityOfDNsForRG(11);

		// check the added DN's
		// 1st column
		checkDn(1, "input398", "DN for -> row first input 1");

		checkDn(4, "input398", "DN for -> row 4 input 1");

		checkDn(5, "input398", "DN for -> row 5 input 1");

		checkDn(7, "input398", "DN for -> row 7 input 1");

		checkDn(8, "input398", "DN for -> row last input 1");

		// 2nd column
		checkDn(2, "input399", "DN for -> row 2 input 2");

		checkDn(6, "input399", "DN for -> row 6 input 2");

		checkDn(7, "input399", "DN for -> row 7 input 2");

		// last column
		checkDn(2, "input407", "DN for -> row 2 last input");

		checkDn(6, "input407", "DN for -> row 6 last input");

		checkDn(8, "input407", "DN for -> row 8 last input");
	}

	protected void removeCRFData() throws Exception {
		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening"); // view/enter datafor: SSID ->
																				// 'TEST_SUBJECT_1' & event -> 'First
																				// Office Visit/Screening'
		removeCRFData("Protocol Deviation"); // remove CRF data
		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening"); // view/enter datafor: SSID ->
																				// 'TEST_SUBJECT_1' & event -> 'First
																				// Office Visit/Screening'
	}
}
/*
 * 1 yes x x 1 1 1 x yes 1
 */
