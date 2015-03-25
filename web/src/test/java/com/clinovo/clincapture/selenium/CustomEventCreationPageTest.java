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

@SuppressWarnings("deprecation")
public class CustomEventCreationPageTest extends BaseTest {

	private static String START_DATE_TIME_LABEL = "Start Date/Time";
	private static String END_DATE_TIME_LABEL = "End Date/Time";

	public static String POSTFIX = " modified";

	@Test
	public void testLabels() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		openStudySetupPage();

		populateEventFormLabels(POSTFIX);
		setRadiosToYes();

		restoreDefaults();
	}

	@Test
	public void testRequired() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		openStudySetupPage();

		setRadiosToYes();

		submitStudyOptions();

		openScheduleEvenPage();

		assertElementPresented("//select[@name='startHour']");
		assertElementPresented("//select[@name='startMinute']");
		assertElementPresented("//select[@name='endHour']");
		assertElementPresented("//select[@name='endMinute']");

		assertElementPresented("//table/tbody/tr[3]/td[2]/table/tbody/tr/td[normalize-space(text())='(DD-MMM-YYYY HH:MM) *']");
		assertElementPresented("//table/tbody/tr[4]/td[2]/table/tbody/tr/td[normalize-space(text())='(DD-MMM-YYYY HH:MM) *']");

		restoreDefaults();
	}

	@Test
	public void testOptional() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		openStudySetupPage();

		setRadiosToNo();

		submitStudyOptions();

		openScheduleEvenPage();

		assertFalse(selenium.isVisible("//select[@name='startHour']"));
		assertFalse(selenium.isVisible("//select[@name='startMinute']"));
		assertFalse(selenium.isVisible("//select[@name='endHour']"));
		assertFalse(selenium.isVisible("//select[@name='endMinute']"));

		assertElementPresented("//table/tbody/tr[3]/td[2]/table/tbody/tr/td[normalize-space(text())='(dd-MMM-yyyy)']");
		assertElementPresented("//table/tbody/tr[4]/td[2]/table/tbody/tr/td[normalize-space(text())='(dd-MMM-yyyy)']");

		restoreDefaults();
	}

	@Test
	public void testNotUsed() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		openStudySetupPage();

		setRadiosToNotUsed();

		submitStudyOptions();

		openScheduleEvenPage();

		assertFalse(selenium.isVisible("//select[@name='startHour']"));
		assertFalse(selenium.isVisible("//select[@name='startMinute']"));
		assertFalse(selenium.isVisible("//select[@name='endHour']"));
		assertFalse(selenium.isVisible("//select[@name='endMinute']"));
		assertFalse(selenium.isElementPresent("//select[@name='startDate']"));
		assertFalse(selenium.isElementPresent("//select[@name='endDate']"));

		restoreDefaults();
	}

	private void populateEventFormLabels(String addToDefault) {
		selenium.type("//input[@name='startDateTimeLabel']", START_DATE_TIME_LABEL + addToDefault);
		selenium.type("//input[@name='endDateTimeLabel']", END_DATE_TIME_LABEL + addToDefault);
	}

	private void setRadiosToYes() {
		selenium.click("//input[@name='startDateTimeRequired'][1]");
		selenium.click("//input[@name='endDateTimeRequired'][1]");
		selenium.click("//input[@name='useStartTime'][1]");
		selenium.click("//input[@name='useEndTime'][1]");
	}

	private void setRadiosToNo() {
		selenium.click("//input[@name='startDateTimeRequired'][2]");
		selenium.click("//input[@name='endDateTimeRequired'][2]");
		selenium.click("//input[@name='useStartTime'][2]");
		selenium.click("//input[@name='useEndTime'][2]");
	}

	private void setRadiosToNotUsed() {
		selenium.click("//input[@name='startDateTimeRequired'][3]");
		selenium.click("//input[@name='endDateTimeRequired'][3]");
	}

	private void setRadiosToDefault() {
		selenium.click("//input[@name='startDateTimeRequired'][1]");
		selenium.click("//input[@name='endDateTimeRequired'][2]");
		selenium.click("//input[@name='useStartTime'][1]");
		selenium.click("//input[@name='useEndTime'][1]");
	}

	private void restoreDefaults() throws Exception {
		openStudySetupPage();

		populateEventFormLabels("");
		setRadiosToDefault();

		submitStudyOptions();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		try {
			restoreDefaults();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.tearDown();
	}

}
