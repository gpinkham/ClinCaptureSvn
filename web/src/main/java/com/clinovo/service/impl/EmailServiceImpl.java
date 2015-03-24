package com.clinovo.service.impl;

import com.clinovo.service.EmailService;
import com.clinovo.util.PageMessagesUtil;
import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.core.EmailEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Email Service implementation.
 */
@Service("emailService")
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSenderImpl mailSender;

	@Autowired
	private MessageSource messageSource;

	private Locale locale;
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public Boolean sendEmail(String to, String subject, String body, Boolean htmlEmail, HttpServletRequest request)
			throws Exception {
		locale = SessionUtil.getLocale(request);
		return sendEmail(to, EmailEngine.getAdminEmail(), subject, body, htmlEmail,
				messageSource.getMessage("your_message_sent_succesfully", null, locale), messageSource.getMessage("mail_cannot_be_sent_to_admin", null, locale),
				true, request);
	}

	public Boolean sendEmail(String to, String subject, String body, Boolean htmlEmail, Boolean sendMessage,
			HttpServletRequest request) throws Exception {
		locale = SessionUtil.getLocale(request);
		return sendEmail(to, EmailEngine.getAdminEmail(), subject, body, htmlEmail,
				messageSource.getMessage("your_message_sent_succesfully", null, locale), messageSource.getMessage(
				"mail_cannot_be_sent_to_admin", null, locale),
				sendMessage, request);
	}

	public Boolean sendEmail(String to, String from, String subject, String body, Boolean htmlEmail,
			String successMessage, String failMessage, Boolean sendMessage, HttpServletRequest request)
			throws Exception {
		return sendEmailWithAttach(to, from, subject, body, htmlEmail, successMessage, failMessage, sendMessage,
				new String[0], request);
	}

	public Boolean sendEmailWithAttach(String to, String from, String subject, String body, Boolean htmlEmail,
			String successMessage, String failMessage, Boolean sendMessage, String[] files, HttpServletRequest request)
			throws Exception {
		Boolean messageSent = true;
		try {
			JavaMailSenderImpl mailSender = getMailSender();
			MimeMessage mimeMessage = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, htmlEmail);
			helper.setFrom(from);
			helper.setTo(processMultipleImailAddresses(to.trim()));
			helper.setSubject(subject);
			helper.setText(body, true);
			for (String filePath : files) {
				FileSystemResource file = new FileSystemResource(filePath);
				helper.addAttachment(file.getFilename(), file);
			}

			mailSender.send(mimeMessage);
			if (successMessage != null && sendMessage) {
				PageMessagesUtil.addPageMessage(request, successMessage);
			}
			logger.debug("Email sent successfully on {}", new Date());
		} catch (MailException me) {
			me.printStackTrace();
			if (failMessage != null && sendMessage) {
				PageMessagesUtil.addPageMessage(request, failMessage);
			}
			logger.debug("Email could not be sent on {} due to: {}", new Date(), me.toString());
			messageSent = false;
		}
		return messageSent;
	}

	/**
	 * Process Multiple Email Addresses.
	 *
	 * @param to list of the recipients.
	 * @return InternetAddress[].
	 * @throws javax.mail.MessagingException if there was an error.
	 */
	private InternetAddress[] processMultipleImailAddresses(String to) throws MessagingException {
		ArrayList<String> recipientsArray = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(to, ",");

		while (st.hasMoreTokens()) {
			recipientsArray.add(st.nextToken());
		}
		int sizeTo = recipientsArray.size();
		InternetAddress[] addressTo = new InternetAddress[sizeTo];

		for (int i = 0; i < sizeTo; i++) {
			addressTo[i] = new InternetAddress(recipientsArray.get(i));
		}
		return addressTo;
	}

	public JavaMailSenderImpl getMailSender() {
		return mailSender;
	}
}



