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

public class ReasonForChangeTest extends BaseTest {

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
		setValue("input355", "11122101-T014");

		// save the CRF
		saveCrf();

		// check alerts on the first TAB
		checkAlertForInput("input354");
		checkAlertForInput("input355");

		addDirectDn("input354", "DN for input354");
		addDirectDn("input355", "DN for input355");

		// save the CRF
		saveCrf();

		// wait when the second tab will be opened
		waitForNextTab(2);

		// add 2 new rows in the end
		addRow();
		addRow();

		// save the CRF
		saveCrf();

		// we have to wait for something after saving
		wait("selenium.isElementPresent(\"" + groupRowSelector + "[" + (rowsInfo.visibleRows + 1)
				+ "]//span[@class='aka_exclaim_error']\")");
		wait("selenium.isElementPresent(\"" + groupRowSelector + "[" + (rowsInfo.visibleRows + 2)
				+ "]//span[@class='aka_exclaim_error']\")");

		// add some changes
		setInputValue(1, "input367", "08-Feb-2011"); // change 08-Feb-2012 to 08-Feb-2011
		setInputValue(2, "input367", "24-Jan-2011"); // change 24-Jan-2012 to 24-Jan-2011
		setInputValue(rowsInfo.visibleRows + 1, "input367", "15-Aug-2012"); // set value for penultimate row
		setInputValue(rowsInfo.visibleRows + 2, "input367", "16-Aug-2012"); // set value for last row

		// add DN for second row [IG_MONT_DSDL_manual1input367]
		addDn(2, "input367", "DN for -> row 2 input 1");

		// add DN for last row [IG_MONT_DSDL_manual24input367]
		addDn(25, "input367", "DN for -> row last input 1");

		// save the CRF
		saveCrf();

		// we have to wait for something after saving
		wait("selenium.isElementPresent(\"" + groupRowSelector + "[1]//span[@class='aka_exclaim_error']\")");
		wait("selenium.isElementPresent(\"" + groupRowSelector + "[" + (rowsInfo.visibleRows + 1)
				+ "]//span[@class='aka_exclaim_error']\")");

		// check quantity of DNs
		checkQuantityOfDNsForRG(2); // 2 we added

		// check quantity of alerts
		checkQuantityOfAlertsForRG(2);

		// check that previously added DN for second row is saved
		checkDn(2, "input367", "DN for -> row 2 input 1");

		// check that previously added DN for last row is saved
		checkDn(25, "input367", "DN for -> row last input 1");

		// add DN for first row [IG_MONT_DSDL_0input367]
		addDn(1, "input367", "DN for -> row 1 input 1");

		// add DN for penultimate row [IG_MONT_DSDL_manual23input367]
		addDn(24, "input367", "DN for -> row penultimate input 1");

		// save the CRF
		saveCrf();

		administrativeEditing("Monthly Office Visit Follow-up Form"); // administrative Editing of 'Monthly Office Visit
																		// Follow-up Form' CRF

		// check DN's on the first TAB
		checkDNForInput("input354");
		checkDNForInput("input355");

		openTab(2); // open second tab

		// check quantity of DNs
		checkQuantityOfDNsForRG(4); // 4 we added

		// check that previously added DN for first row is saved
		checkDn(1, "input367", "DN for -> row 1 input 1");

		// check that previously added DN for second row is saved
		checkDn(2, "input367", "DN for -> row 2 input 1");

		// check that previously added DN for penultimate row is saved
		checkDn(24, "input367", "DN for -> row penultimate input 1");

		// check that previously added DN for last row is saved
		checkDn(25, "input367", "DN for -> row last input 1");
	}

	protected void fillTab1() throws Exception {
		Thread.sleep(5000);
		enterData("Monthly Office Visit Follow-up Form"); // enter data for 'XForm' CRF
		setValue("input353", "16-Feb-2012");
		setValue("input354", "11031703-1555-SC");
		setValue("input355", "11122101-T013");
		setValue("input356", "10111801-02");
		setInputRadio(1, "input357", "3");
		addDn(1, "input357", "DN 1");
		setValue("input360", "24-Jan-2012");
		addDirectDn("input360", "DN 1");
		setValue("input361", "15-Feb-2012");
		setRadio("input362", "2");
		setRadio("input363", "1");
		setValue("input364", "150");
		setValue("input365", "85");
		setValue("input366", "59");
	}

	protected void fillTab2() throws Exception {
		Thread.sleep(5000);
		String[] arr = new String[] { "08-Feb-2012", "24-Jan-2012", "25-Jan-2012", "26-Jan-2012", "27-Jan-2012",
				"28-Jan-2012", "29-Jan-2012", "30-Jan-2012", "01-Feb-2012", "02-Feb-2012", "03-Feb-2012",
				"04-Feb-2012", "05-Feb-2012", "06-Feb-2012", "07-Feb-2012", "08-Feb-2012", "09-Feb-2012",
				"10-Feb-2012", "11-Feb-2012", "12-Feb-2012", "13-Feb-2012", "14-Feb-2012", "15-Feb-2012" };
		for (int i = 1; i <= 22; i++) {
			addRow();
		}
		setInputValue(1, "input367", arr[0]);
		setInputRadio(1, "input368", "1");
		for (int i = 2; i <= 23; i++) {
			setNewInputValue(i, "input367", arr[i - 1]);
			setNewInputRadio(i, "input368", "1");
		}
	}

	protected void removeCRFData() throws Exception {
		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening"); // view/enter datafor: SSID ->
																				// 'TEST_SUBJECT_1' & event -> 'First
																				// Office Visit/Screening'
		removeCRFData("Monthly Office Visit Follow-up Form"); // remove CRF data
		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening"); // view/enter datafor: SSID ->
																				// 'TEST_SUBJECT_1' & event -> 'First
																				// Office Visit/Screening'
	}
}
