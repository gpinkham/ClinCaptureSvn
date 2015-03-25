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
@SuppressWarnings("deprecation")
public class NewDnScreenTest extends BaseTest {

	@Test
	public void startTest() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		testDnCounts();
	}

	/*
	 * this should look in the matrix and compare the # of DNs with the rows in the table location in the matrix:
	 * /html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/div[2]/table/tbody/tr[8]/td[6] table class=summaryTable
	 * location in the table: /html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/form/div/table/tbody[2]/tr/td table
	 * id = 'listNotes' Phrase to match: 'Rows 1 - 6 of 6' we are looking for the last 6.
	 */
	private void testDnCounts() throws Exception {
		selenium.open(DISC_NOTES_URL, PAGE_TIME_OUT);

		wait("selenium.isElementPresent(\"//table[@class='summaryTable']//tr[8]/td[6]\")");
		String dnCount = selenium.getText("//table[@class='summaryTable']//tr[8]/td[6]");

		wait("selenium.isElementPresent(\"//table[@id='listNotes']/tbody[2]/tr/td\")");
		String dnPhrase = selenium.getText("//table[@id='listNotes']/tbody[2]/tr/td");

		System.out.println(dnCount);
		System.out.println(dnPhrase);
		assertTrue(dnPhrase.endsWith(dnCount + "."));

	}

}
