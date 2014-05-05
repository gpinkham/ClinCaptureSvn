package com.clinovo.clincapture.dao.managestudy;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

public class ItemDataTest extends DefaultAppContextTest {

	@Test
	public void testThatFindAllDoesNotReturnNull() {

		assertNotNull(itemDataDAO.findAll());
	}

	@Test
	public void testThatFindAllReturnsCorrectNumberOfItemDataItems() {

		assertEquals(63, itemDataDAO.findAll().size());
	}
}
