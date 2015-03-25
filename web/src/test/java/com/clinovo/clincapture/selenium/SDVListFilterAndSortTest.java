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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class SDVListFilterAndSortTest extends BaseTest {

	public static final String REQUIRED_PARTIAL_REQUIRED = "100% Required & Partial Required";
	public static final String REQUIRED = "100% Required";
	public static final String PARTIAL_REQUIRED = "Partial Required";
	public static final String NOT_REQUIRED = "Not Required";
	public static final String COMPLETE = "complete";
	public static final String NOT_DONE = "not done";
	public static final String COMPLETED = "Completed";

	/**
	 * ClinCapture #105 SDV by Subject ID: None filter does not work
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTextFilters() throws Exception {

		login(ROOT, ROOT_PASSWORD);

		openSdvPage();
		selenium.click("//a[@id='showMore']/div");

		// Screening ID filter check
		checkSdvListTextFilter("AC-001-TW", 2, 9);
		// Site ID filter check
		checkSdvListTextFilter("11398-1", 3, 15);
		// Event Name filter check
		checkSdvListTextFilter("Adverse Event", 6, 15);
	}

	@Test
	public void testDropdownsFilters() throws Exception {

		login(ROOT, ROOT_PASSWORD);

		openSdvPage();
		selenium.click("//a[@id='showMore']/div");

		// SDV Requirement filter check
		Set<String> expectedValues = new HashSet<String>(Arrays.asList(REQUIRED, PARTIAL_REQUIRED));
		checkDnListDropdownFilterText(REQUIRED_PARTIAL_REQUIRED, 11, expectedValues, 15);

		expectedValues = new HashSet<String>(Arrays.asList(PARTIAL_REQUIRED));
		checkDnListDropdownFilterText(PARTIAL_REQUIRED, 11, expectedValues, 15);

		expectedValues = new HashSet<String>(Arrays.asList(NOT_REQUIRED));
		checkDnListDropdownFilterText(NOT_REQUIRED, 11, expectedValues, 1);

		// SDV Status filter check
		checkDnListDropdownFilterElement(COMPLETE, 1, "//img[@title='SDV Complete']", 15);
		checkDnListDropdownFilterElement(NOT_DONE, 1, "//input[@class='sdvCheck']", 8);

		// CRF Status filter check
		checkDnListDropdownFilterElement(COMPLETED, 12, "//img[@src='../images/icon_DEcomplete.gif']", 15);
	}

	@Test
	public void testTextFiltersBySSID() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		openSdvBySSIDPage();
		selenium.click("//a[@id='showMore']/div");

		// Screening ID filter check
		checkSdvListTextFilterSSID("AC-001-TW", 2, 1);
		// Site ID filter check
		checkSdvListTextFilterSSID("11398-1", 3, 15);
	}

	@Test
	public void testDropdownsFiltersSSID() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		openSdvBySSIDPage();
		selenium.click("//a[@id='showMore']/div");

		// SDV Status filter check
		checkDnListDropdownFilterElementSSID(NOT_DONE, 1, "//input[@class='sdvCheck']", 15);
	}

	private void checkSdvListTextFilter(String filterValue, int filterIndex, int count) throws Exception {

		selenium.click("//table[@id='sdv']//tr[@class='filter']//a[2]");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);

		selenium.click("//table[@id='sdv']/thead/tr[4]/td[" + filterIndex + "]/div");
		Thread.sleep(1000);
		selenium.runScript("jQuery(\"#dynFilterInput\").val(\"" + filterValue + "\")");
		selenium.keyPressNative("10");

		selenium.waitForPageToLoad(PAGE_TIME_OUT);

		for (int i = 1; i <= count; i++) {
			String field = selenium.getText("//table[@id='sdv']/tbody/tr[" + i + "]/td[" + filterIndex + "]");
			assertTrue(field.toLowerCase().contains(filterValue.toLowerCase()));
		}
	}

	private void checkSdvListTextFilterSSID(String filterValue, int filterIndex, int count) throws Exception {

		selenium.click("//table[@id='s_sdv']//tr[@class='filter']//a[2]");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);

		selenium.click("//table[@id='s_sdv']/thead/tr[4]/td[" + filterIndex + "]/div");
		Thread.sleep(1000);
		selenium.runScript("jQuery(\"#dynFilterInput\").val(\"" + filterValue + "\")");
		selenium.keyPressNative("10");

		Thread.sleep(2000);

		for (int i = 1; i <= count; i++) {
			String field = selenium.getText("//table[@id='s_sdv']/tbody/tr[" + i + "]/td[" + filterIndex + "]");
			assertTrue(field.toLowerCase().contains(filterValue.toLowerCase()));
		}
	}

	private void checkDnListDropdownFilterText(String filterValue, int filterIndex, Set<String> expectedValues,
			int count) throws Exception {

		selenium.click("//table[@id='sdv']//tr[@class='filter']//a[2]");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);

		selenium.click("//table[@id='sdv']/thead/tr[4]/td[" + filterIndex + "]/div");
		Thread.sleep(1000);
		selenium.runScript("jQuery(\"#dynFilterDroplist\").val(\"" + filterValue + "\")");
		selenium.runScript("jQuery(\"#dynFilterDroplist\").change()");

		Thread.sleep(2000);

		for (int i = 1; i <= count; i++) {
			String field = selenium.getText("//table[@id='sdv']/tbody/tr[" + i + "]/td[" + filterIndex + "]");
			assertTrue(expectedValues.contains(field));
		}
	}

	private void checkDnListDropdownFilterElement(String filterValue, int filterIndex, String expectedElement, int count)
			throws Exception {

		selenium.click("//table[@id='sdv']//tr[@class='filter']//a[2]");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);

		selenium.click("//table[@id='sdv']/thead/tr[4]/td[" + filterIndex + "]/div");
		Thread.sleep(1000);
		selenium.runScript("jQuery(\"#dynFilterDroplist\").val(\"" + filterValue + "\")");
		selenium.runScript("jQuery(\"#dynFilterDroplist\").change()");

		Thread.sleep(2000);

		for (int i = 1; i <= count; i++) {
			assertElementPresented("//table[@id='sdv']/tbody/tr[" + i + "]/td[" + filterIndex + "]" + expectedElement);
		}
	}

	private void checkDnListDropdownFilterElementSSID(String filterValue, int filterIndex, String expectedElement,
			int count) throws Exception {

		selenium.click("//table[@id='s_sdv']//tr[@class='filter']//a[2]");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);

		selenium.click("//table[@id='s_sdv']/thead/tr[4]/td[" + filterIndex + "]/div");
		Thread.sleep(1000);
		selenium.runScript("jQuery(\"#dynFilterDroplist\").val(\"" + filterValue + "\")");
		selenium.runScript("jQuery(\"#dynFilterDroplist\").change()");

		Thread.sleep(2000);

		for (int i = 1; i <= count; i++) {
			assertElementPresented("//table[@id='s_sdv']/tbody/tr[" + i + "]/td[" + filterIndex + "]" + expectedElement);
		}
	}

	private void openSdvBySSIDPage() {
		selenium.open(CC_CONTEXT + "/pages/viewSubjectAggregate?studyId=9");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
	}

}
