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
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Email Service Test.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailServiceTest {

	public static final String EMAIL_EXCEPTION = "Illegal address";
	public static final String EMAIL_ADDRESS = "test@test.com";

	@Mock
	private EmailServiceImpl mockedEmailService;

	@Mock
	private EmailLogService mockedEmailLogService;

	@Mock
	private JavaMailSenderImpl mockedMailSender;

	@Mock
	private Logger mockedLogger;

	@Mock
	private MimeMessageHelper mockedMimeMessageHelper;

	@Before
	public void prepare() throws MessagingException {
		Whitebox.setInternalState(mockedEmailService, "emailLogService", mockedEmailLogService);
		Whitebox.setInternalState(mockedEmailService, "mailSender", mockedMailSender);
		Whitebox.setInternalState(mockedEmailService, "logger", mockedLogger);

		Mockito.doReturn(mockedMimeMessageHelper).when(mockedEmailService)
				.getMimeMessageHelper(Mockito.any(MimeMessage.class), Mockito.anyBoolean());
		Mockito.doCallRealMethod().when(mockedEmailService).sendEmail(Mockito.any(EmailDetails.class));
	}

	@Test
	public void testThatSaveOrUpdateIsCalledInCaseOfExceptionInMailSender() {
		mockedEmailService.sendEmail(new EmailDetails());
		Mockito.verify(mockedEmailLogService).saveOrUpdate(Mockito.any(EmailLog.class));
	}

	@Test
	public void testThatWasSentAndErrorMessageAreSetInCaseOfException() throws MessagingException {
		Mockito.doThrow(new MessagingException(EMAIL_EXCEPTION)).when(mockedMimeMessageHelper)
				.setFrom(Mockito.anyString());

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
}
