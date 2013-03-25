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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.selenium;

import com.clinovo.clincapture.selenium.base.BaseTest;
import org.junit.Test;

public class NewDnCSVOutputTest extends BaseTest {

	@Test
	public void startTest() throws Exception {
		login(ROOT, ROOT_PASSWORD);
		testDnOutput();
	}

	/*
	 * this should look in the DN screen and click on the icon to open the output screen. After that, we should confirm
	 * that the file we get is an ok file, i.e. no oops message.
	 */
	private void testDnOutput() throws Exception {
		selenium.open(DISC_NOTES_URL, PAGE_TIME_OUT);

		wait("selenium.isElementPresent(\"//img[@class='downloadAllDNotes']\")");
		selenium.click("//img[@class='downloadAllDNotes']");

		selenium.selectWindow(waitWindow("selenium_blank", true)); // equal to the openDocWindow javascript call to
																	// 'doc_win' in global_functions?

		// may have to rename in GF? RENAMED but not DEPLOYED
		wait("selenium.isTextPresent(\"downloading discrepancy notes\")");
		assertTrue(selenium.isTextPresent("downloading discrepancy notes"));

		// download some notes in csv format
		wait("selenium.isElementPresent(\"//select[@id='fmt']\")");
		selenium.select("//select[@id='fmt']", "value=csv");

		wait("selenium.isElementPresent(\"//input[@name='submitFormat']\")");
		selenium.click("//input[@name='submitFormat']");

		Thread.sleep(10000);
		// We can not check it with selenium

		wait("selenium.isTextPresent(\"downloading discrepancy notes\")");
		assertTrue(selenium.isTextPresent("downloading discrepancy notes"));
	}

}
