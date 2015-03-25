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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.clinovo.clincapture.selenium.base.BaseTest;

@SuppressWarnings("deprecation")
public class DNListFilterAndSortTest extends BaseTest {

	public static final String STATUS_NEW = "New";
	public static final String STATUS_CLOSED = "Closed";
	public static final String STATUS_NOT_APPLICABLE = "Not Applicable";
	public static final String STATUS_UPDATED = "Updated";
	public static final String RESOLUTION_PROPOSED = "Resolution Proposed";

	/**
	 * tickets #87, #88 Sorting NDs by Date Created does not work
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubjectIDSort() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		selenium.open(CC_CONTEXT + "/ViewNotes?module=submit");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		selenium.click("//a[@id='showMore']/div");

		// sort by Subject ID
		checkDNListSort("//table[@id='listNotes']/thead/tr[2]/td[1]/div");

		// sort by date created
		checkDNListSort("//table[@id='listNotes']/thead/tr[2]/td[5]/div");

		// sort by days
		checkDNListSort("//table[@id='listNotes']/thead/tr[2]/td[7]/div");

		// sort by days since update
		checkDNListSort("//table[@id='listNotes']/thead/tr[2]/td[8]/div");
	}

	private void checkDNListSort(String sortFieldLocator) throws Exception {
		selenium.click(sortFieldLocator);
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		selenium.click("//a[@id='showMore']/div");
		assertTrue(selenium.isElementPresent("//img[@src='" + CC_CONTEXT + "/images/table/sortAsc.gif']"));

		selenium.click(sortFieldLocator);
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		selenium.click("//a[@id='showMore']/div");
		assertTrue(selenium.isElementPresent("//img[@src='" + CC_CONTEXT + "/images/table/sortDesc.gif']"));

		selenium.click(sortFieldLocator);
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
	}

	@Test
	public void testDnListTextFilter() throws Exception {

		login(ROOT, ROOT_PASSWORD);

		selenium.open(CC_CONTEXT + "/ViewNotes?module=submit");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		selenium.click("//a[@id='showMore']/div");

		// "Subject ID" filter
		checkDnListTextFilter("018", 1);

		// "Site ID" filter
		checkDnListTextFilter("11398-1", 4);

		// "Days Open" filter
		checkDnListTextFilter("1", 7);

		// "Days Since Updated" filter
		checkDnListTextFilter("", 8);

		// "Event Name" filter
		checkDnListTextFilter("Visit ", 9);

		// "CRF" filter
		checkDnListTextFilter("Medication ", 11);

		// "Entity Name" filter
		checkDnListTextFilter("MED001", 13);

		// "Entity Value" filter
		checkDnListTextFilter("1", 14);

		// "Entity Type" filter
		checkDnListTextFilter("itemData", 15);

		// "Description" filter
		checkDnListTextFilter("field", 16);

		// "Assigned User" filter
		checkDnListTextFilter("apni_cra1", 19);
	}

	private void checkDnListTextFilter(String filterValue, int filterIndex) throws Exception {

		clearFilter();

		selenium.click("//table[@id='listNotes']/thead/tr[3]/td[" + filterIndex + "]/div");
		Thread.sleep(1000);
		selenium.runScript("jQuery(\"#dynFilterInput\").val(\"" + filterValue + "\")");
		selenium.keyPressNative("10");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);

		for (int i = 1; i <= 15; i++) {
			String field = selenium.getText("//table[2]/tbody/tr/td[2]/form/div/table/tbody/tr[" + i + "]/td["
					+ filterIndex + "]");
			assertTrue(field.toLowerCase().contains(filterValue.toLowerCase()));
		}
	}

	// "Type" filter
	@Test
	public void testTypeFilter() throws Exception {
		Set<String> expectedTypes = new HashSet<String>(Arrays.asList("Query"));
		checkDnListDropdownFilter("Query", 2, expectedTypes);

		expectedTypes = new HashSet<String>(Arrays.asList("Query", "Failed Validation Check"));
		checkDnListDropdownFilter("Query and Failed Validation Check", 2, expectedTypes);
	}

	// "Resolution status" filter
	@Test
	public void testStatusFilter() throws Exception {
		Set<String> expectedStatuses = new HashSet<String>(Arrays.asList(STATUS_NEW));
		checkDnListDropdownFilter(STATUS_NEW, 3, expectedStatuses);

		expectedStatuses = new HashSet<String>(Arrays.asList(STATUS_CLOSED));
		checkDnListDropdownFilter(STATUS_CLOSED, 3, expectedStatuses);

		expectedStatuses = new HashSet<String>(Arrays.asList(STATUS_NOT_APPLICABLE));
		checkDnListDropdownFilter(STATUS_NOT_APPLICABLE, 3, expectedStatuses);

		expectedStatuses = new HashSet<String>(Arrays.asList(STATUS_NEW, STATUS_UPDATED));
		checkDnListDropdownFilter("New and Updated", 3, expectedStatuses);

		expectedStatuses = new HashSet<String>(Arrays.asList(STATUS_NEW, STATUS_UPDATED, RESOLUTION_PROPOSED));
		checkDnListDropdownFilter("Not Closed", 3, expectedStatuses);
	}

	private void checkDnListDropdownFilter(String filterValue, int filterIndex, Set<String> expectedValues)
			throws Exception {
		if (selenium.isTextPresent("Notes and Discrepancies")) {
			clearFilter();
		} else {
			login(ROOT, ROOT_PASSWORD);
		}

		selenium.open(CC_CONTEXT + "/ViewNotes?module=submit");
		waitForText("Notes and Discrepancies");
		selenium.click("//a[@id='showMore']/div");

		selenium.click("//table[@id='listNotes']/thead/tr[3]/td[" + filterIndex + "]/div");
		Thread.sleep(1000);
		selenium.runScript("jQuery(\"#dynFilterDroplist\").val(\"" + filterValue + "\")");
		selenium.runScript("jQuery(\"#dynFilterDroplist\").change()");

		Thread.sleep(2000);

		for (int i = 1; i <= 15; i++) {
			String field = selenium.getText("//table[2]/tbody/tr/td[2]/form/div/table/tbody/tr[" + i + "]/td["
					+ filterIndex + "]");
			assertTrue(expectedValues.contains(field));
		}
	}

	private void clearFilter() throws Exception {
		selenium.click("//a[contains(text(),'Clear Filter')]");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
	}

	/**
	 * ClinCapture #64 DNs display performance
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDNListPerformance() throws Exception {

		login(ROOT, ROOT_PASSWORD);

		long beforeLoad = System.currentTimeMillis();
		selenium.open(CC_CONTEXT + "/ViewNotes?module=submit");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		long afterLoad = System.currentTimeMillis();

		assertTrue((afterLoad - beforeLoad) <= 5000);

		beforeLoad = System.currentTimeMillis();
		selenium.click("//table[@id='listNotes']//img[@alt='Next']");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		afterLoad = System.currentTimeMillis();

		assertTrue((afterLoad - beforeLoad) <= 5000);
	}

}
