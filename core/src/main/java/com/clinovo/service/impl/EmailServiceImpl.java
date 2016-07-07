package com.clinovo.service.impl;

import com.clinovo.bean.EmailDetails;
import com.clinovo.enums.BooleanEnum;
import com.clinovo.model.EmailLog;
import com.clinovo.service.EmailLogService;
import com.clinovo.service.EmailService;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Email Service Implementation.
 */
@Service("emailService")
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSenderImpl mailSender;

	@Autowired
	private EmailLogService emailLogService;

	@Autowired
	private DataSource dataSource;

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	/**
	 * {@inheritDoc}
	 */
	@Async
	public void sendEmail(EmailDetails emailDetails) {
		EmailLog emailLog = new EmailLog(emailDetails);

		try {
			MimeMessage mimeMessage = createMimeMessage(emailLog);
			mailSender.send(mimeMessage);
			logger.debug("Email sent successfully on {}", new Date());
		} catch (Exception me) {
			logger.debug("Email could not be sent on {} due to: {}", new Date(), me.toString());
			emailLog.setWasSent(BooleanEnum.FALSE);
			emailLog.setError(me.getMessage());
		}
		emailLogService.saveOrUpdate(emailLog);
	}

	/**
	 * {@inheritDoc}
	 */
	@Async
	public void resendEmail(EmailLog emailLog, String sender, int sendBy) {
		List<EmailLog> childEntries = emailLogService.findAllByParentId(emailLog.getId());
		// Create clone of the parent entity to store it's error and state.
		if (childEntries == null || childEntries.size() == 0) {
			EmailLog parentClone = new EmailLog(emailLog, emailLog.getSender(), emailLog.getSentBy());
			parentClone.setWasShown(BooleanEnum.TRUE);
			emailLogService.saveOrUpdate(parentClone);
		}
		EmailLog childEntry = new EmailLog(emailLog, sender, sendBy);
		childEntry.setDateSent(new Date());
		emailLog.setSentBy(sendBy);
		emailLog.setDateSent(new Date());
		emailLog.setSender(sender);
		try {
			MimeMessage mimeMessage = createMimeMessage(childEntry);
			mailSender.send(mimeMessage);
			logger.debug("Email sent successfully on {}", new Date());

			childEntry.setWasSent(BooleanEnum.TRUE);
			childEntry.setError("");
			emailLog.setWasSent(BooleanEnum.TRUE);
			emailLog.setError("");
		} catch (Exception ex) {
			childEntry.setWasSent(BooleanEnum.FALSE);
			childEntry.setError(ex.getMessage());
			emailLog.setWasSent(BooleanEnum.FALSE);
			emailLog.setError(ex.getMessage());
		}
		emailLogService.saveOrUpdate(emailLog);
		emailLogService.saveOrUpdate(childEntry);
	}

	/**
	 * Create MimeMessageHelper.
	 * @param emailLog EmailLog
	 * @throws MessagingException in case if error was thrown while creation of helper.
	 * @return MimeMessage
	 */
	public MimeMessage createMimeMessage(EmailLog emailLog) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, emailLog.htmlEmail());
		helper.setFrom(emailLog.getSender());
		helper.setTo(processMultipleEmailAddresses(emailLog.getRecipient().trim()));
		helper.setSubject(emailLog.getSubject());
		helper.setText(emailLog.getMessage(), true);

		if (emailLog.getFileAttachments() != null) {
			String[] attachments = emailLog.getFileAttachments().split(",");
			for (String filePath : attachments) {
				FileSystemResource file = new FileSystemResource(filePath);
				helper.addAttachment(file.getFilename(), file);
			}
		}
		return mimeMessage;
	}

	/**
	 * Process Multiple Email Addresses.
	 * @param to list of the recipients.
	 * @return InternetAddress[].
	 * @throws javax.mail.MessagingException if there was an error.
	 */
	private InternetAddress[] processMultipleEmailAddresses(String to) throws MessagingException {
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

	/**
	 * Get UserAccountDAO.
	 * @return UserAccountDAO.
	 */
	public UserAccountDAO getUserAccountDAO() {
		return new UserAccountDAO(dataSource);
	}
}
