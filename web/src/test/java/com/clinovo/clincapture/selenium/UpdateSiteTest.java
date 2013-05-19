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
import org.junit.After;
import org.junit.Test;

/**
 * User: Pavel Date: 11.11.12
 */
public class UpdateSiteTest extends BaseTest {

	private static String SITE_NAME = "ApniCure, Inc.";
	private static String FACILITY_NAME = "facility";

	/**
	 * ticket #81 change site name
	 */
	@Test
	public void testChangeSiteFacilityName() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		changeStudyStatus("Design");

		String newFacilityName = getRandomName();
		setSiteFacilityName(newFacilityName);
		checkSiteFacilityName(newFacilityName);

		changeStudyStatus("Available");

		setSiteFacilityName(FACILITY_NAME);
		checkSiteFacilityName(FACILITY_NAME);
	}

	@Test
	public void testChangeSiteName() throws Exception {
		login(ROOT, ROOT_PASSWORD);

		changeStudyStatus("Design");

		String newSiteName = getRandomName();
		setSiteName(newSiteName);
		checkSiteName(newSiteName);

		changeStudyStatus("Available");

		setSiteName(SITE_NAME);
		checkSiteName(SITE_NAME);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		try {
			setSiteName(SITE_NAME);
			setSiteFacilityName(FACILITY_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.tearDown();
	}
}
