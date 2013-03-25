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

/**
 * User: Pavel Date: 01.12.12
 */
public class FormPanelInfoTest extends BaseTest {

	/**
	 * ClinCapture #45 Form Panel should show important and mandatory fields
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFormPanelInfo() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		openSubjectMatrixPage();
		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening");
		selenium.click("//table[@id='crfListTable']/tbody/tr[8]/td[6]/a[2]/img");
		Thread.sleep(5000);

		assertTextPresented("Study:");
		assertTextPresented("Subject:");
		assertTextPresented("Event:");
		assertTextPresented("Form:");
		assertTextPresented("More Info");
	}
}
