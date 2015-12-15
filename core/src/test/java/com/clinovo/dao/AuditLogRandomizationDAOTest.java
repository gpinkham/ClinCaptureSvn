package com.clinovo.dao;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * AuditLogRandomizationDAO test.
 */
public class AuditLogRandomizationDAOTest extends DefaultAppContextTest {

	@Autowired
	private AuditLogRandomizationDAO auditLogRandomizationDAO;

	@Test
	public void testThatFindByStudySubjectIdFindsAllEntities() {
		assertEquals(auditLogRandomizationDAO.findAllByStudySubjectId(1).size(), 2);
	}
}
