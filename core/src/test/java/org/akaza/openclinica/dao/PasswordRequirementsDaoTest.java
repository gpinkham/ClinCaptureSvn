/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.dao;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

import java.util.Map;

public class PasswordRequirementsDaoTest extends DefaultAppContextTest {

	public static final int FINAL_COUNT = 4;
	public static final int ELEVEN = 11;
	public static final int THREE_HUNDRED_SIZTY_FIVE = 365;
	public static final int TWELVE = 12;

	@Test
	public void testConfigs() {
		Map<String, Object> configs = requirementsDao.configs();
		assertTrue(configs.containsKey("pwd.chars.min"));
	}

	@Test
	public void testBoolConfigKeys() {
		assertEquals(FINAL_COUNT, requirementsDao.boolConfigKeys().size());

	}

	@Test
	public void testIntConfigKeys() {
		assertEquals(FINAL_COUNT, requirementsDao.intConfigKeys().size());
	}

	@Test
	public void testSetHasLower() {
		requirementsDao.setHasLower(false);
		Map<String, Object> configs = requirementsDao.configs();
		assertFalse((Boolean) configs.get("pwd.chars.case.lower"));
	}

	@Test
	public void testSetHasUpper() {
		requirementsDao.setHasUpper(false);
		Map<String, Object> configs = requirementsDao.configs();
		assertFalse((Boolean) configs.get("pwd.chars.case.upper"));
	}

	@Test
	public void testSetHasDigits() {
		requirementsDao.setHasDigits(false);
		Map<String, Object> configs = requirementsDao.configs();
		assertFalse((Boolean) configs.get("pwd.chars.digits"));
	}

	@Test
	public void testSetHasSpecials() {
		requirementsDao.setHasSpecials(false);
		Map<String, Object> configs = requirementsDao.configs();
		assertFalse((Boolean) configs.get("pwd.chars.specials"));
	}

	@Test
	public void testSetMinLength() {
		requirementsDao.setMinLength(0);
		Map<String, Object> configs = requirementsDao.configs();
		assertEquals(0, ((Integer) configs.get("pwd.chars.min")).intValue());
	}

	@Test
	public void testSetMaxLength() {
		requirementsDao.setMaxLength(ELEVEN);
		Map<String, Object> configs = requirementsDao.configs();
		assertEquals(ELEVEN, ((Integer) configs.get("pwd.chars.max")).intValue());
	}

	@Test
	public void testSetExpirationDays() {
		requirementsDao.setExpirationDays(THREE_HUNDRED_SIZTY_FIVE);
		Map<String, Object> configs = requirementsDao.configs();
		assertEquals(THREE_HUNDRED_SIZTY_FIVE, ((Integer) configs.get("pwd.expiration.days")).intValue());
	}

	@Test
	public void testSetChangeRequired() {
		requirementsDao.setChangeRequired(0);
		Map<String, Object> configs = requirementsDao.configs();
		assertEquals(0, ((Integer) configs.get("pwd.change.required")).intValue());
	}

	@Test
	public void testHasLower() {
		requirementsDao.setHasLower(true);
		assertTrue(requirementsDao.hasLower());
	}

	@Test
	public void testHasUpper() {
		requirementsDao.setHasUpper(true);
		assertTrue(requirementsDao.hasUpper());
	}

	@Test
	public void testHasDigits() {
		requirementsDao.setHasDigits(true);
		assertTrue(requirementsDao.hasDigits());
	}

	@Test
	public void testHasSpecials() {
		requirementsDao.setHasSpecials(true);
		assertTrue(requirementsDao.hasSpecials());
	}

	@Test
	public void testChangeRequired() {
		requirementsDao.setChangeRequired(0);
		assertFalse(requirementsDao.changeRequired());
	}

	@Test
	public void testMinLength() {
		requirementsDao.setMinLength(0);
		assertEquals(0, requirementsDao.minLength());
	}

	@Test
	public void testMaxLength() {
		requirementsDao.setMaxLength(TWELVE);
		assertNotSame(0, requirementsDao.maxLength());
	}

	@Test
	public void testExpirationDays() {
		requirementsDao.setExpirationDays(ELEVEN);
		assertNotSame(THREE_HUNDRED_SIZTY_FIVE, requirementsDao.expirationDays());
	}

}
