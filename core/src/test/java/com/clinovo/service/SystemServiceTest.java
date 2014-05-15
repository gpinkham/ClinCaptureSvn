package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Role;
import org.junit.Test;

import com.clinovo.command.SystemCommand;

public class SystemServiceTest extends DefaultAppContextTest {

	@Test(expected = NullPointerException.class)
	public void testThatUpdateSystemPropertiesWorksFine() throws Exception {
		systemService.updateSystemProperties(new SystemCommand());
	}

	@Test
	public void testThatFindAllReturnsCorrectSize() {
		assertTrue(systemService.findAll().size() > 0);
	}

	@Test
	public void testThatGetAllMainGroupsReturnsCorrectSize() {
		assertTrue(systemService.getAllMainGroups().size() > 0);
	}

	@Test
	public void testThatGetAllChildGroupsReturnsCorrectSize() {
		assertTrue(systemService.getAllChildGroups(1).size() > 0);
	}

	@Test
	public void testThatGetAllPropertiesReturnsCorrectSize() {
		assertTrue(systemService.getAllProperties(2, Role.SYSTEM_ADMINISTRATOR).size() > 0);
	}

	@Test
	public void testThatGetSystemPropertyGroupsReturnsCorrectSize() {
		assertTrue(systemService.getSystemPropertyGroups(Role.SYSTEM_ADMINISTRATOR).size() > 0);
	}
}
