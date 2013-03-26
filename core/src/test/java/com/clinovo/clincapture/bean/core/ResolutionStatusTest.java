/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.bean.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;

public class ResolutionStatusTest {

	public static final String INVALID_KEY = "invalid";

	public static final String OPEN_KEY = "New";
	public static final String OPEN_IMG_KEY = "images/icon_Note.gif";

	public static final String UPDATED_KEY = "Updated";
	public static final String UPDATED_IMG_KEY = "images/icon_flagYellow.gif";

	public static final String RESOLVED_KEY = "Resolution_Proposed";
	public static final String RESOLVED_IMG_KEY = "images/icon_flagBlack.gif";

	public static final String CLOSED_KEY = "Closed";
	public static final String CLOSED_IMG_KEY = "images/icon_flagGreen.gif";

	public static final String NOT_APPLICABLE_KEY = "Not_Applicable";
	public static final String NOT_APPLICABLE_IMG_KEY = "images/icon_flagWhite.gif";

	private static final String WRONG_KEY = "Monitor";

	ResolutionStatus resolutionStatus;
	ResourceBundle resterm;

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.getDefault());
		resterm = ResourceBundleProvider.getTermsBundle();
	}

	@Test
	public void testInvalid() {
		resolutionStatus = ResolutionStatus.INVALID;
		assertTrue(resolutionStatus.isInvalid());
		assertEquals(resterm.getString(INVALID_KEY), resolutionStatus.getName());
		assertNull(resolutionStatus.getIconFilePath());
	}

	@Test
	public void testOpen() {
		resolutionStatus = ResolutionStatus.get(1);
		assertEquals(ResolutionStatus.OPEN, resolutionStatus);
		assertEquals(resterm.getString(OPEN_KEY), resolutionStatus.getName());
		assertEquals(OPEN_IMG_KEY, resolutionStatus.getIconFilePath());
	}

	@Test
	public void testUpdated() {
		resolutionStatus = ResolutionStatus.get(2);
		assertEquals(ResolutionStatus.UPDATED, resolutionStatus);
		assertEquals(resterm.getString(UPDATED_KEY), resolutionStatus.getName());
		assertEquals(UPDATED_IMG_KEY, resolutionStatus.getIconFilePath());
	}

	@Test
	public void testResolved() {
		resolutionStatus = ResolutionStatus.get(3);
		assertEquals(ResolutionStatus.RESOLVED, resolutionStatus);
		assertEquals(resterm.getString(RESOLVED_KEY), resolutionStatus.getName());
		assertEquals(RESOLVED_IMG_KEY, resolutionStatus.getIconFilePath());
	}

	@Test
	public void testClosed() {
		resolutionStatus = ResolutionStatus.get(4);
		assertEquals(ResolutionStatus.CLOSED, resolutionStatus);
		assertEquals(resterm.getString(CLOSED_KEY), resolutionStatus.getName());
		assertEquals(CLOSED_IMG_KEY, resolutionStatus.getIconFilePath());
	}

	@Test
	public void testNotApplicable() {
		resolutionStatus = ResolutionStatus.get(5);
		assertEquals(ResolutionStatus.NOT_APPLICABLE, resolutionStatus);
		assertEquals(resterm.getString(NOT_APPLICABLE_KEY), resolutionStatus.getName());
		assertEquals(NOT_APPLICABLE_IMG_KEY, resolutionStatus.getIconFilePath());
	}

	@Test
	public void testGet() {
		assertNull(ResolutionStatus.get(-1));
		assertNull(ResolutionStatus.get(0));
		assertNotNull(ResolutionStatus.get(2));
		assertNull(ResolutionStatus.get(7));
	}

	@Test
	public void testContains() {
		assertFalse(ResolutionStatus.contains(-1));
		assertFalse(ResolutionStatus.contains(0));
		assertTrue(ResolutionStatus.contains(2));
		assertFalse(ResolutionStatus.contains(7));
	}

	@Test
	public void testGetByName() {
		assertEquals(ResolutionStatus.INVALID, ResolutionStatus.getByName(resterm.getString(INVALID_KEY)));
		assertEquals(ResolutionStatus.OPEN, ResolutionStatus.getByName(resterm.getString(OPEN_KEY)));
		assertEquals(ResolutionStatus.INVALID, ResolutionStatus.getByName(resterm.getString(WRONG_KEY)));
	}
}
