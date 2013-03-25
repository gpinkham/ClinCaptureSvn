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

import org.junit.Test;

import com.clinovo.clincapture.selenium.base.BaseTest;

/**
 * User: Pavel Date: 11.11.12
 */
public class AssignToMeDNTest extends BaseTest {

	/**
	 * ticket #86 "Not Closed" DNs only for "Discrepancies assigned to me" link
	 */
	@Test
	public void testAssignToMeDN() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		goToAssignedToMePage();

		checkSelectedDNListStatus("Not Closed");
		checkSelectedDNListUser(ROOT);
	}
}
