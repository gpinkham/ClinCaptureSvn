package com.clinovo.service;

import com.clinovo.bean.EmailDetails;
import com.clinovo.model.EmailLog;
import com.clinovo.service.impl.EmailServiceImpl;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Email Service Test.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(EmailServiceImpl.class)
public class EmailServiceTest {

	public static final String EMAIL_EXCEPTION = "Illegal address";
	public static final String EMAIL_ADDRESS = "test@test.com";
	public static final String RESEND_ORIGINAL_SENDER = "test@email.com";
	public static final String RESEND_NEW_SENDER = "test_2@email.com";
	public static final int RESEND_ORIGINAL_ID = 1;
	public static final int RESEND_NEW_ID = 9;

	@Mock
	private EmailServiceImpl mockedEmailService;

	@Mock
	private EmailLogService mockedEmailLogService;

	@Mock
	private JavaMailSenderImpl mockedMailSender;

	@Mock
	private Logger mockedLogger;

	@Before
	public void prepare() throws MessagingException {
		Whitebox.setInternalState(mockedEmailService, "emailLogService", mockedEmailLogService);
		Whitebox.setInternalState(mockedEmailService, "mailSender", mockedMailSender);
		Whitebox.setInternalState(mockedEmailService, "logger", mockedLogger);

		Mockito.doCallRealMethod().when(mockedEmailService).sendEmail(Mockito.any(EmailDetails.class));
		Mockito.doCallRealMethod().when(mockedEmailService).resendEmail(Mockito.any(EmailLog.class),
				Mockito.anyString(), Mockito.anyInt());
	}

	@Test
	public void testThatSaveOrUpdateIsCalledInCaseOfExceptionInMailSender() throws MessagingException {
		Mockito.doCallRealMethod().when(mockedEmailService)
				.createMimeMessage(Mockito.any(EmailLog.class));
		mockedEmailService.sendEmail(new EmailDetails());
		ArgumentCaptor<EmailLog> argument = ArgumentCaptor.forClass(EmailLog.class);
		Mockito.verify(mockedEmailLogService).saveOrUpdate(argument.capture());
		TestCase.assertFalse(argument.getValue().wasSent());
	}

	@Test
	public void testThatWasSentAndErrorMessageAreSetInCaseOfException() throws MessagingException {
		Mockito.doCallRealMethod().when(mockedEmailService)
				.createMimeMessage(Mockito.any(EmailLog.class));
		mockedEmailService.sendEmail(new EmailDetails());
		ArgumentCaptor<EmailLog> argument = ArgumentCaptor.forClass(EmailLog.class);
		Mockito.verify(mockedEmailLogService).saveOrUpdate(argument.capture());

		TestCase.assertEquals(EMAIL_EXCEPTION, argument.getValue().getError());
		TestCase.assertFalse(argument.getValue().wasSent());
	}

	@Test
	public void testThatEmailLogIsWrittenIfNoExceptionIsThrown() {
		EmailDetails emailDetails = new EmailDetails();
		emailDetails.setTo(EMAIL_ADDRESS);
		emailDetails.setFrom(EMAIL_ADDRESS);

		mockedEmailService.sendEmail(emailDetails);
		ArgumentCaptor<EmailLog> argument = ArgumentCaptor.forClass(EmailLog.class);
		Mockito.verify(mockedEmailLogService).saveOrUpdate(argument.capture());

		TestCase.assertTrue(argument.getValue().wasSent());
	}

	@Test
	public void testThatChildEntityIsCreatedToStoreParentErrorAndStatusWhenFirstResendIsCalled() throws Exception {
		EmailLog parentEntry = new EmailLog();
		parentEntry.setSentBy(RESEND_ORIGINAL_ID);
		parentEntry.setId(1);
		parentEntry.setSender(RESEND_ORIGINAL_SENDER);

		EmailLog firstChild = new EmailLog(parentEntry, parentEntry.getSender(), parentEntry.getSentBy());
		firstChild.setMessage("test");
		firstChild.setId(2);

		EmailLog secondChild = new EmailLog();
		secondChild.setId(3);
		secondChild.setMessage("test_2");

		PowerMockito.whenNew(EmailLog.class).withArguments(parentEntry, parentEntry.getSender(),
				parentEntry.getSentBy()).thenReturn(firstChild);
		PowerMockito.whenNew(EmailLog.class).withArguments(parentEntry, RESEND_NEW_SENDER,
				RESEND_NEW_ID).thenReturn(secondChild);

		// Run
		mockedEmailService.resendEmail(parentEntry, RESEND_NEW_SENDER, RESEND_NEW_ID);

		// Verify
		Mockito.verify(mockedEmailLogService).saveOrUpdate(firstChild);
		Mockito.verify(mockedEmailLogService).saveOrUpdate(secondChild);
		Mockito.verify(mockedEmailLogService).saveOrUpdate(parentEntry);
	}

	@Test
	public void testThatChildEntityNotCreatedToStoreParentErrorAndStatusWhenResendIsCalledNotFirstTime() throws Exception {
		EmailLog parentEntry = new EmailLog();
		parentEntry.setSentBy(RESEND_ORIGINAL_ID);
		parentEntry.setId(1);
		parentEntry.setSender(RESEND_ORIGINAL_SENDER);

		EmailLog firstChild = new EmailLog(parentEntry, parentEntry.getSender(), parentEntry.getSentBy());
		firstChild.setMessage("test");
		firstChild.setId(2);

		EmailLog secondChild = new EmailLog();
		secondChild.setId(3);
		secondChild.setMessage("test_2");

		PowerMockito.whenNew(EmailLog.class).withArguments(parentEntry, parentEntry.getSender(),
				parentEntry.getSentBy()).thenReturn(firstChild);
		PowerMockito.whenNew(EmailLog.class).withArguments(parentEntry, RESEND_NEW_SENDER,
				RESEND_NEW_ID).thenReturn(secondChild);

		List<EmailLog> childLogs = new ArrayList<EmailLog>();
		childLogs.add(firstChild);
		Mockito.doReturn(childLogs).when(mockedEmailLogService).findAllByParentId(1);

		// Run
		mockedEmailService.resendEmail(parentEntry, RESEND_NEW_SENDER, RESEND_NEW_ID);

		// Verify
		Mockito.verify(mockedEmailLogService, Mockito.never()).saveOrUpdate(firstChild);
		Mockito.verify(mockedEmailLogService).saveOrUpdate(secondChild);
		Mockito.verify(mockedEmailLogService).saveOrUpdate(parentEntry);
	}
}
