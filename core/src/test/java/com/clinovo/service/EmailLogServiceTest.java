package com.clinovo.service;

import com.clinovo.dao.EmailLogDAO;
import com.clinovo.model.EmailLog;
import com.clinovo.service.impl.EmailLogServiceImpl;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import javax.sql.DataSource;

/**
 * AuditLogEmailServiceTest.
 */
public class EmailLogServiceTest {

	private EmailLogServiceImpl emailLogService;
	private EmailLogDAO emailLogDAO;
	private UserAccountDAO userAccountDAO;

	@Before
	public void prepare() {
		emailLogService = Mockito.spy(EmailLogServiceImpl.class);
		emailLogDAO = Mockito.mock(EmailLogDAO.class);
		userAccountDAO = Mockito.mock(UserAccountDAO.class);
		DataSource dataSource = Mockito.mock(DataSource.class);
		Mockito.doReturn(new EmailLog()).when(emailLogDAO).findById(1);
		Mockito.doReturn(userAccountDAO).when(emailLogService).getUserAccountDao();

		Whitebox.setInternalState(emailLogService, "emailLogDAO", emailLogDAO);
		Whitebox.setInternalState(emailLogService, "dataSource", dataSource);
	}

	@Test
	public void testThatFindAllByStudyCallsCorrectDaoMethod() {
		emailLogService.findAllByStudyId(1);
		Mockito.verify(emailLogDAO).findAllByStudyId(1);
	}

	@Test
	public void testThatFindByIdReturnsEntityWithUserAccountBean() {
		emailLogService.findById(1);
		Mockito.verify(userAccountDAO).findByPK(0);
	}
}
