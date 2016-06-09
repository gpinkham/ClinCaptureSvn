package com.clinovo.service;

import com.clinovo.dao.EmailLogDAO;
import com.clinovo.service.impl.EmailLogServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

/**
 * AuditLogEmailServiceTest.
 */
public class EmailLogServiceTest {

	private EmailLogService emailLogService;
	private EmailLogDAO emailLogDAO;

	@Before
	public void prepare() {
		emailLogService = Mockito.spy(EmailLogServiceImpl.class);
		emailLogDAO = Mockito.mock(EmailLogDAO.class);
		Whitebox.setInternalState(emailLogService, "emailLogDAO", emailLogDAO);
	}

	@Test
	public void testThatFindAllByStudyCallsCorrectDaoMethod() {
		emailLogService.findAllByStudyId(1);
		Mockito.verify(emailLogDAO).findAllByStudyId(1);
	}
}
