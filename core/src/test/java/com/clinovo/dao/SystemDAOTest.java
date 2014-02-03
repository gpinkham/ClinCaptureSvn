package com.clinovo.dao;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Role;
import org.junit.Test;

public class SystemDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatFindByNameReturnsValue() {
		assertNotNull(systemDAO.findByName("sysURL"));
	}

	@Test
	public void testThatFindAllReturnsCorrectSize() {
		assertTrue(systemDAO.findAll().size() > 0);
	}

	@Test
	public void testThatGetAllMainGroupsReturnsCorrectSize() {
		assertTrue(systemDAO.getAllMainGroups().size() > 0);
	}

	@Test
	public void testThatGetAllChildGroupsReturnsCorrectSize() {
		assertTrue(systemDAO.getAllChildGroups(1).size() > 0);
	}

	@Test
	public void testThatGetAllPropertiesReturnsCorrectSize() {
		assertTrue(systemDAO.getAllProperties(2, Role.SYSTEM_ADMINISTRATOR).size() > 0);
	}
}
