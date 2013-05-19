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

public class NewStudyParameterTest extends BaseTest {

	@Test
	public void startTest() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		testStudyParam();
	}

	/*
	 * test whether or not the param is there, changes it and checks the add study subject page to see if the 'Secondary
	 * ID' field is there
	 * 
	 * link to get to study info: /html/body/table/tbody/tr/td/table/tbody/tr/td/div[2]/div/b/a div id='studyInfo'/b/a
	 */
	private void testStudyParam() throws Exception {
		String inputName = "secondaryIdRequired";
		String value = "not_used";

		selenium.open(CC_CONTEXT, PAGE_TIME_OUT);
		wait("selenium.isElementPresent(\"//a[contains(@href,'ViewStudy?id=')]\")");
		selenium.click("//a[contains(@href,'ViewStudy?id=')]");

		wait("selenium.isTextPresent(\"View Study Details: [SECTION F: Study Parameter Configuration\")");
		assertTrue(selenium.isTextPresent("View Study Details: [SECTION F: Study Parameter Configuration"));

		wait("selenium.isElementPresent(\"//a[contains(@href, 'sectionf')]\")");
		selenium.click("//a[contains(@href, 'sectionf')]");

		wait("selenium.isTextPresent(\"Secondary ID Required?\")");
		assertTrue(selenium.isTextPresent("Secondary ID Required?"));

		selenium.click("//a[@id='nav_Tasks_link']");
		selenium.click("//a[contains(@href, 'studymodule')]");

		wait("selenium.isElementPresent(\"//select[@name='studyStatus']\")");
		selenium.select("//select[@name='studyStatus']", "Design"); // or 4??

		selenium.click("//input[@name='saveStudyStatus']");

		wait("selenium.isElementPresent(\"//a[contains(@href, 'UpdateStudyNew')]\")");
		selenium.click("//a[contains(@href, 'UpdateStudyNew')]");
		// this did not do what we wanted it to do

		// do some stuff - that is, change the parameter

		wait("selenium.isElementPresent(\"//a[contains(@href,'sectionf')]\")");
		selenium.click("//a[contains(@href,'sectionf')]");

		wait("selenium.isElementPresent(\"//input[@name='" + inputName + "' and @value='" + value + "']\")");
		selenium.click("//input[@name='" + inputName + "' and @value='" + value + "']");

		wait("selenium.isElementPresent(\"//input[@type='submit' and @value='Submit']\")");
		selenium.click("//input[@type='submit' and @value='Submit']");

		// at the end, select the available status and re-set the study
		value = "no";

		wait("selenium.isElementPresent(\"//a[contains(@href, 'UpdateStudyNew')]\")");
		selenium.click("//a[contains(@href, 'UpdateStudyNew')]");

		wait("selenium.isElementPresent(\"//a[contains(@href,'sectionf')]\")");
		selenium.click("//a[contains(@href,'sectionf')]");

		wait("selenium.isElementPresent(\"//input[@name='" + inputName + "' and @value='" + value + "']\")");
		selenium.click("//input[@name='" + inputName + "' and @value='" + value + "']");

		wait("selenium.isElementPresent(\"//input[@type='submit' and @value='Submit']\")");
		selenium.click("//input[@type='submit' and @value='Submit']");

		wait("selenium.isElementPresent(\"//a[@id='nav_Tasks_link']\")");

		selenium.click("//a[@id='nav_Tasks_link']");
		selenium.click("//a[contains(@href, 'studymodule')]");

		wait("selenium.isElementPresent(\"//select[@name='studyStatus']\")");
		selenium.select("//select[@name='studyStatus']", "Available"); // or 1??

		selenium.click("//input[@name='saveStudyStatus']");

		wait("selenium.isElementPresent(\"//a[contains(@href, 'updateStudy_Alert')]\")");
	}

}
