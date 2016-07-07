package com.clinovo.dao;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * AuditLogEmailDAOTest.
 */
public class EmailLogDAOTest extends DefaultAppContextTest {

	@Autowired
	private EmailLogDAO emailLogDAO;

	@Test
	public void testThatFindAllByStudyReturnsCorrectResult() {
		assertEquals(2, emailLogDAO.findAllParentsByStudyId(1).size());
	}

	@Test
	public void testThatFindAllByParentIdReturnsCorrectResult() {
		assertEquals(1, emailLogDAO.findAllByParentId(1).size());
	}
}
