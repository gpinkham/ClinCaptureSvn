package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * AuditLogRandomizationService Test.
 */
public class AuditLogRandomizationServiceTest extends DefaultAppContextTest {

	@Autowired
	private AuditLogRandomizationService auditLogRandomizationService;

	@Test
	public void testThatFindByStudySubjectIdReturnsCorrectResult() {
		assertEquals(2, auditLogRandomizationService.findAllByStudySubjectId(1).size());
	}
}
