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
public class DNSummaryStatsLinksTest extends BaseTest {

	public static final String STATUS_NEW = "New";
	public static final String DNTYPE_QUERY = "Query";
	public static final String STATUS_UPDATED = "Updated";
	public static final String STATUS_RESOLUTION_PROPOSED = "Resolution Proposed";
	public static final String STATUS_CLOSED = "Closed";
	public static final String DNTYPE_FAILED_VALIDATION_CHECK = "Failed Validation Check";
	public static final String STATUS_NOT_APPLICABLE = "Not Applicable";
	public static final String DNTYPE_REASON_FOR_CHANGE = "Reason for Change";
	public static final String DNTYPE_ANNOTATION = "Annotation";

	/**
	 * ClinCapture #43 Add links to DN Summary Stats
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTableLinksExists() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		openDiscrepancyNotesPage();

		checkLinkExistence(STATUS_NEW, DNTYPE_QUERY);
		checkLinkExistence(STATUS_UPDATED, DNTYPE_QUERY);
		checkLinkExistence(STATUS_RESOLUTION_PROPOSED, DNTYPE_QUERY);
		checkLinkExistence(STATUS_CLOSED, DNTYPE_QUERY);
		checkLinkExistence(null, DNTYPE_QUERY);

		checkLinkExistence(STATUS_NEW, DNTYPE_FAILED_VALIDATION_CHECK);
		checkLinkExistence(STATUS_CLOSED, DNTYPE_FAILED_VALIDATION_CHECK);
		checkLinkExistence(null, DNTYPE_FAILED_VALIDATION_CHECK);

		checkLinkExistence(STATUS_NOT_APPLICABLE, DNTYPE_REASON_FOR_CHANGE);
		checkLinkExistence(null, DNTYPE_REASON_FOR_CHANGE);

		checkLinkExistence(STATUS_NOT_APPLICABLE, DNTYPE_ANNOTATION);
		checkLinkExistence(null, DNTYPE_ANNOTATION);

		checkLinkExistence(STATUS_NEW, null);
		checkLinkExistence(STATUS_UPDATED, null);
		checkLinkExistence(STATUS_RESOLUTION_PROPOSED, null);
		checkLinkExistence(STATUS_CLOSED, null);
		checkLinkExistence(STATUS_NOT_APPLICABLE, null);
	}

	private void checkLinkExistence(String firstFilterValue, String secondFilterValue) {
		if (firstFilterValue != null && secondFilterValue != null) {
			assertElementPresented("//a[@onclick=\"applyStatusAndTypeFilter('" + firstFilterValue + "', '"
					+ secondFilterValue + "')\"]");
		} else if (firstFilterValue == null) {
			assertElementPresented("//a[@onclick=\"applyTypeFilter('" + secondFilterValue + "')\"]");
		} else if (secondFilterValue == null) {
			assertElementPresented("//a[@onclick=\"applyStatusFilter('" + firstFilterValue + "')\"]");
		}
	}

	@Test
	public void testTableLinks() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		openDiscrepancyNotesPage();

		checkLinkFiltering(STATUS_NEW, DNTYPE_QUERY, 15);
		checkLinkFiltering(STATUS_UPDATED, DNTYPE_QUERY, 1);
		checkLinkFiltering(STATUS_RESOLUTION_PROPOSED, DNTYPE_QUERY, 3);
		checkLinkFiltering(STATUS_CLOSED, DNTYPE_QUERY, 15);
		checkLinkFiltering(null, DNTYPE_QUERY, 15);

		checkLinkFiltering(STATUS_NEW, DNTYPE_FAILED_VALIDATION_CHECK, 1);
		checkLinkFiltering(STATUS_CLOSED, DNTYPE_FAILED_VALIDATION_CHECK, 5);
		checkLinkFiltering(null, DNTYPE_FAILED_VALIDATION_CHECK, 6);

		checkLinkFiltering(STATUS_NOT_APPLICABLE, DNTYPE_REASON_FOR_CHANGE, 10);
		checkLinkFiltering(null, DNTYPE_REASON_FOR_CHANGE, 10);

		checkLinkFiltering(STATUS_NOT_APPLICABLE, DNTYPE_ANNOTATION, 15);
		checkLinkFiltering(null, DNTYPE_ANNOTATION, 15);

		checkLinkFiltering(STATUS_NEW, null, 15);
		checkLinkFiltering(STATUS_UPDATED, null, 1);
		checkLinkFiltering(STATUS_RESOLUTION_PROPOSED, null, 3);
		checkLinkFiltering(STATUS_CLOSED, null, 15);
		checkLinkFiltering(STATUS_NOT_APPLICABLE, null, 15);
	}

	private void checkLinkFiltering(String firstFilterValue, String secondFilterValue, int count) throws Exception {
		if (firstFilterValue != null && secondFilterValue != null) {
			assertElementPresented("//a[@onclick=\"applyStatusAndTypeFilter('" + firstFilterValue + "', '"
					+ secondFilterValue + "')\"]");
			selenium.click("//a[@onclick=\"applyStatusAndTypeFilter('" + firstFilterValue + "', '" + secondFilterValue
					+ "')\"]");
		} else if (firstFilterValue == null) {
			assertElementPresented("//a[@onclick=\"applyTypeFilter('" + secondFilterValue + "')\"]");
			selenium.click("//a[@onclick=\"applyTypeFilter('" + secondFilterValue + "')\"]");
		} else if (secondFilterValue == null) {
			assertElementPresented("//a[@onclick=\"applyStatusFilter('" + firstFilterValue + "')\"]");
			selenium.click("//a[@onclick=\"applyStatusFilter('" + firstFilterValue + "')\"]");
		}
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		for (int i = 1; i <= count; i++) {
			if (firstFilterValue != null) {
				String field = selenium.getText("//table[2]/tbody/tr/td[2]/form/div/table/tbody/tr[" + i + "]/td[3]");
				assertTrue(field.toLowerCase().contains(firstFilterValue.toLowerCase()));
			}
			if (secondFilterValue != null) {
				String field = selenium.getText("//table[2]/tbody/tr/td[2]/form/div/table/tbody/tr[" + i + "]/td[2]");
				assertTrue(field.toLowerCase().contains(secondFilterValue.toLowerCase()));
			}
		}
	}
}
