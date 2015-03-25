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

@SuppressWarnings("deprecation")
public class SubjectMatrixSortPageSizeTest extends BaseTest {

	// ticket #30
	@Test
	public void testSavedRowsCount() throws Exception {

		int testPageSize = 50;

		login(ROOT, ROOT_PASSWORD);
		openSubjectMatrixPage();

		// set row count
		selenium.runScript("jQuery(\"select[name='maxRows']\").val(\"" + testPageSize + "\")");
		selenium.runScript("jQuery(\"select[name='maxRows']\").change()");
		Thread.sleep(3000);

		// go to other page and back
		openAddSubjectPage();
		openSubjectMatrixPage();

		// check count saved
		assertEquals(testPageSize, selenium.getValue("//select[@name='maxRows']"));
	}

	@Test
	public void testDefaultSort() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		openSubjectMatrixPage();

		// check default sort
		assertElementPresented("//form/div/table/thead/tr[3]/td/div/img[@src='" + CC_CONTEXT
				+ "/images/table/sortAsc.gif']");
	}

	@Test
	public void testSaveSort() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		openSubjectMatrixPage();

		// click on subject id column header
		selenium.click("//form/div/table/thead/tr[3]/td/div");
		Thread.sleep(3000);

		// check sort order changed
		assertElementPresented("//form/div/table/thead/tr[3]/td/div/img[@src='" + CC_CONTEXT
				+ "/images/table/sortDesc.gif']");

		// go to other page and back
		openAddSubjectPage();
		openSubjectMatrixPage();

		// check sort order saved
		assertElementPresented("//form/div/table/thead/tr[3]/td/div/img[@src='" + CC_CONTEXT
				+ "/images/table/sortDesc.gif']");
	}

	// ticket #36
	@Test
	public void testShowMoreSort() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		openSubjectMatrixPage();

		// click on show more link
		selenium.click("//a[@id='showMore']");

		// click on subject id column header
		selenium.click("//form/div/table/thead/tr[3]/td[2]/div");
		Thread.sleep(3000);

		// check element presence
		assertElementVisible("//form/div/table/thead/tr[3]/td[2]/div");
		// check sort
		assertElementPresented("//form/div/table/thead/tr[3]/td[2]/div/img[@src='" + CC_CONTEXT
				+ "/images/table/sortAsc.gif']");
		// check other additional fields
		assertElementVisible("//form/div/table/thead/tr[3]/td[3]/div");
		assertElementVisible("//form/div/table/thead/tr[3]/td[4]/div");
		assertElementVisible("//form/div/table/thead/tr[3]/td[5]/div");
	}

	// ticket #37
	@Test
	public void testSubjectMatrixSubjectIdFilter() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		openSubjectMatrixPage();
		selenium.click("//a[@id='showMore']/div");

		// Subject Id Filter
		checkSubjectMatrixTextFilter("AC-0", 1);

		// Site Id Filter
		checkSubjectMatrixTextFilter("11398-1", 3);

		// Matrix OID Filter
		checkSubjectMatrixTextFilter("SS_AC", 4);
	}

	private void checkSubjectMatrixTextFilter(String filterValue, int filterIndex) throws Exception {
		clearFilter();

		selenium.click("//table[@id='findSubjects']/thead/tr[4]/td[" + filterIndex + "]/div");
		Thread.sleep(2000);
		selenium.runScript("jQuery(\"#dynFilterInput\").val(\"" + filterValue + "\")");
		selenium.keyPressNative("10");

		selenium.waitForPageToLoad(PAGE_TIME_OUT);

		for (int i = 1; i <= 15; i++) {
			String field = selenium.getText("//table[@id='findSubjects']/tbody/tr[" + i + "]/td[" + filterIndex + "]");
			assertTrue(field.toLowerCase().contains(filterValue.toLowerCase()));
		}
	}

	@Test
	public void testSubjectMatrixStatusFilter() throws Exception {
		checkSubjectMatrixStatusFilter("available", 15);
		checkSubjectMatrixStatusFilter("signed", 1);
		checkSubjectMatrixStatusFilter("removed", 1);
	}

	private void checkSubjectMatrixStatusFilter(String filterValue, int resultsCount) throws Exception {
		if (!selenium.isTextPresent("Subject Matrix")) {
			login(ROOT, ROOT_PASSWORD);
		}

		openSubjectMatrixPage();
		selenium.click("//a[@id='showMore']/div");

		selenium.click("//table[@id='findSubjects']/thead/tr[4]/td[2]/div");
		Thread.sleep(1000);
		selenium.runScript("jQuery(\"#dynFilterDroplist\").val(\"" + filterValue + "\")");
		selenium.runScript("jQuery(\"#dynFilterDroplist\").change()");

		Thread.sleep(2000);

		for (int i = 1; i <= resultsCount; i++) {
			String field = selenium.getText("//table[@id='findSubjects']/tbody/tr[" + i + "]/td[2]");
			assertTrue(filterValue.equalsIgnoreCase(field));
		}
	}

	// ticket #38
	@Test
	public void testSubjectMatrixStatusSort() throws Exception {
		checkSubjectMatrixSort(2);
	}

	@Test
	public void testSubjectMatrixOIDSort() throws Exception {
		checkSubjectMatrixSort(3);
	}

	@Test
	public void testSubjectMatrixSecondaryIdSort() throws Exception {
		checkSubjectMatrixSort(4);
	}

	private void checkSubjectMatrixSort(int sortFieldId) throws Exception {
		login(ROOT, ROOT_PASSWORD);

		openSubjectMatrixPage();
		selenium.click("//a[@id='showMore']/div");

		selenium.click("//table[@id='findSubjects']/thead/tr[3]/td[" + sortFieldId + "]/div");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		assertElementPresented("//img[@src='" + CC_CONTEXT + "/images/table/sortAsc.gif']");

		selenium.click("//table[@id='findSubjects']/thead/tr[3]/td[" + sortFieldId + "]/div");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		selenium.click("//a[@id='showMore']/div");
		assertElementPresented("//img[@src='" + CC_CONTEXT + "/images/table/sortDesc.gif']");
	}

	private void clearFilter() throws Exception {
		selenium.click("//a[contains(text(),'Clear Filter')]");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
	}
}
