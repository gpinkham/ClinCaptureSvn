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
public class CrcLoginTest extends BaseTest {

	/**
	 * ticket #115 Entering invalid user name causes stack dump
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidUserLogin() throws Exception {
		selenium.open(LOGIN_URL, PAGE_TIME_OUT);
		selenium.windowMaximize();
		selenium.windowFocus();

		wait("selenium.isElementPresent(\"//input[@id='username']\")");
		selenium.type("id=username", INVESTIGATOR + CRC);

		wait("selenium.isElementPresent(\"//input[@id='j_password']\")");
		selenium.type("id=j_password", CRC_PASSWORD);

		selenium.click("//input[@name='submit']");
		Thread.sleep(2000);

		assertTextPresented("Your User Name and Password combination could not be found.");
	}
}
