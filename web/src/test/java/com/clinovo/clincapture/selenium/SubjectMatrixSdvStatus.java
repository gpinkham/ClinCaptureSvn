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
import org.junit.Test;

/**
 * User: Pavel Date: 01.12.12
 */
public class SubjectMatrixSdvStatus extends BaseTest {

	public static final String EVENT_NAME = "First Office Visit/Screening";

	@Test
	public void testSdvStatus() throws Exception {

		login(ROOT, ROOT_PASSWORD);

		openSubjectMatrixPage();

		// check event crf SDVed
		assertElementPresented("//tr[@id='findSubjects_row6']/td[14]/table/tbody/tr/td/a/img[@src='images/icon_DoubleCheck.gif']");

		// hover this event crf
		selenium.mouseOver("//tr[@id='findSubjects_row6']/td[14]/table/tbody/tr/td/a");
		Thread.sleep(2000);

		// check crf statuses
		assertTrue(isStatusSdvedOrNotScheduled(1));
	}

	private boolean isStatusSdvedOrNotScheduled(int rowIndex) {
		return selenium.isElementPresent("//table[@id='crfListTable']/tbody/tr[" + (rowIndex + 1)
				+ "]/td[3]/img[@src='images/icon_DoubleCheck.gif']")
				|| selenium.isElementPresent("//table[@id='crfListTable']/tbody/tr[" + (rowIndex + 1)
						+ "]/td[3]/img[@src='images/icon_NotStarted.gif']");
	}

	@Test
	public void checkSdvLink() throws Exception {

		login(ROOT, ROOT_PASSWORD);

		openSubjectMatrixPage();

		// check that link we check is not visible
		assertElementNotVisible("//a[@class='sdvLink']");

		hoverStudySubjectEvent(TEST_SUBJECT_1, EVENT_NAME);

		// check that link we check is visible
		assertElementVisible("//a[@class='sdvLink']");

	}

	/**
	 * ClinCapture #51 link on the Subject Matrix to go directly to the SDV page, filtered by SSID
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkSdvForEventCrf() throws Exception {

		login(ROOT, ROOT_PASSWORD);

		openSubjectMatrixPage();

		hoverStudySubjectEvent(TEST_SUBJECT_1, EVENT_NAME);

		selenium.click("//div[@id='Event_" + TEST_SUBJECT_1 + "_11_1']//a[@class='sdvLink']");
		waitForText("Source Data Verification for ");

		// check subject filter
		assertElementPresented("//table[@id='sdv']/thead/tr[4]/td[2]/div[text()='" + TEST_SUBJECT_1 + "']");
		// check event name filter
		assertElementPresented("//table[@id='sdv']/thead/tr[4]/td[6]/div[text()='" + EVENT_NAME + "']");
	}

}
