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

/**
 * User: Pavel Date: 20.11.12
 */
public class CustomSubjectCreationPageTest extends BaseTest {

	// default values
	public static String STUDY_SUBJECT_ID_LABEL = "Screening ID";
	public static String SECONDARY_ID_LABEL = "Enrollment ID";
	public static String DATE_OF_ENROLLMENT_LABEL = "Date of Screening";
	public static String GENDER_LABEL = "Sex";

	public static String POSTFIX = " modified";

	/**
	 * ClinCapture #11 Customizing the Subject Creation page/panel
	 * 
	 * @throws Exception
	 */

	@Test
	public void testLabels() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		openStudySetupPage();

		populateSubjectFormLabels(POSTFIX);
		setRadiosToYes();

		submitStudyOptions();

		openAddSubjectPage();
		assertLabelsPresented(POSTFIX);

		openSubjectMatrixPage();
		showMore();
		assertTextPresented(SECONDARY_ID_LABEL + POSTFIX);

		restoreDefault();
	}

	@Test
	public void testRequired() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		openStudySetupPage();

		setRadiosToYes();

		submitStudyOptions();

		openAddSubjectPage();
		// Thread.sleep(1000);

		assertElementPresented("//table/tbody/tr[2]/td[2]/table/tbody/tr/td/span[text()='*']");
		assertElementPresented("//table/tbody/tr[3]/td[2]/table/tbody/tr/td/span[text()='*']");
		assertElementPresented("//table/tbody/tr[4]/td[2]/table/tbody/tr/td/span[text()='*']");

		restoreDefault();
	}

	@Test
	public void testOptional() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		openStudySetupPage();

		setRadiosToNo();

		submitStudyOptions();

		openAddSubjectPage();

		assertFalse(selenium.isElementPresent("//table/tbody/tr[2]/td[2]/table/tbody/tr/td/span[text()='*']"));
		assertFalse(selenium.isElementPresent("//table/tbody/tr[3]/td[2]/table/tbody/tr/td/span[text()='*']"));
		assertFalse(selenium.isElementPresent("//table/tbody/tr[4]/td[2]/table/tbody/tr/td/span[text()='*']"));

		restoreDefault();
	}

	@Test
	public void testNotUsed() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		openStudySetupPage();

		setRadiosToNotUsed();

		submitStudyOptions();

		openAddSubjectPage();

		assertFalse(selenium.isVisible("//input[@name='secondaryLabel']"));
		assertFalse(selenium.isVisible("//input[@name='enrollmentDate']"));

		restoreDefault();
	}

	private void showMore() {
		selenium.click("//a[@id='showMore']");
	}

	private void populateSubjectFormLabels(String addToDefault) {
		selenium.type("//input[@name='studySubjectIdLabel']", STUDY_SUBJECT_ID_LABEL + addToDefault);
		selenium.type("//input[@name='secondaryIdLabel']", SECONDARY_ID_LABEL + addToDefault);
		selenium.type("//input[@name='dateOfEnrollmentForStudyLabel']", DATE_OF_ENROLLMENT_LABEL + addToDefault);
		selenium.type("//input[@name='genderLabel']", GENDER_LABEL + addToDefault);
	}

	private void setRadiosToYes() {
		selenium.click("//input[@name='secondaryIdRequired'][1]");
		selenium.click("//input[@name='dateOfEnrollmentForStudyRequired'][1]");
		selenium.click("//input[@name='genderRequired'][1]");
	}

	private void setRadiosToNo() {
		selenium.click("//input[@name='secondaryIdRequired'][2]");
		selenium.click("//input[@name='dateOfEnrollmentForStudyRequired'][2]");
		selenium.click("//input[@name='genderRequired'][2]");
	}

	private void setRadiosToNotUsed() {
		selenium.click("//input[@name='secondaryIdRequired'][3]");
		selenium.click("//input[@name='dateOfEnrollmentForStudyRequired'][3]");
	}

	private void setRadiosToDefault() {
		selenium.click("//input[@name='secondaryIdRequired'][2]");
		selenium.click("//input[@name='dateOfEnrollmentForStudyRequired'][1]");
		selenium.click("//input[@name='genderRequired'][2]");
	}

	private void assertLabelsPresented(String addToDefault) {
		assertTextPresented(STUDY_SUBJECT_ID_LABEL + addToDefault);
		assertTextPresented(SECONDARY_ID_LABEL + addToDefault);
		assertTextPresented(DATE_OF_ENROLLMENT_LABEL + addToDefault);
		assertTextPresented(GENDER_LABEL + addToDefault);
	}

	private void restoreDefault() throws Exception {
		openStudySetupPage();

		populateSubjectFormLabels("");
		setRadiosToDefault();

		submitStudyOptions();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		try {
			restoreDefault();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.tearDown();
	}

}
