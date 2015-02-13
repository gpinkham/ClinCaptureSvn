/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.util;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Implements rendering of DCF by email.
 * 
 * @author Frank
 * 
 */
public final class DcfEmailer implements DcfRenderType {

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private Locale locale;
	private ResourceBundle respage;
	private ResourceBundle resword;
	private StringBuilder messageBody;
	private boolean multipleDcfs;
	private String dcfName;
	private String dcfFilePath;
	private String recipientEmail;
	private String emailSubject;
	private JavaMailSender mailSender;
	private StudyBean currentStudy;
	private UserAccountBean currentUser;
	private final String emailActionResourceBundleKey = "dcf_emailed";

	private DcfEmailer(DcfEmailerBuilder builder) {
		this.dcfFilePath = builder.dcfFilePath;
		this.dcfName = builder.dcfName;
		this.emailSubject = builder.emailSubject;
		this.mailSender = builder.mailSender;
		this.multipleDcfs = builder.multipleDcfs;
		this.recipientEmail = builder.recipientEmail;
		this.currentStudy = builder.currentStudy;
		this.currentUser = builder.currentUser;
		initResourceBundles();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean render() {
		buildMessageBody();
		return sendEmail();
	}

	private void initResourceBundles() {
		this.locale = new Locale(CoreResources.getSystemLanguage());
		this.respage = ResourceBundleProvider.getPageMessagesBundle(locale);
		this.resword = ResourceBundleProvider.getWordsBundle(locale);
	}

	private void buildMessageBody() {
		String listItemEndTag = "</li>";
		messageBody = new StringBuilder();
		messageBody.append(EmailUtil.getEmailBodyStart());
		messageBody.append(respage.getString("html_email_header_1"));
		messageBody.append(" ").append(this.recipientEmail).append(respage.getString("html_email_header_2"))
				.append("</p>");
		if (multipleDcfs) {
			messageBody.append(respage.getString("dcf_email_message_multiple_DCF"));
		} else {
			messageBody.append(MessageFormat.format(respage.getString("dcf_email_message_single_DCF"), dcfName));
		}
		messageBody.append("<ul>");
		messageBody.append(resword.getString("job_error_mail.firstName"));
		messageBody.append(" ").append(currentUser.getFirstName()).append(listItemEndTag);
		messageBody.append(resword.getString("job_error_mail.lastName"));
		messageBody.append(" ").append(currentUser.getLastName()).append(listItemEndTag);
		messageBody.append(resword.getString("job_error_mail.userName"));
		messageBody.append(" ").append(currentUser.getName()).append(listItemEndTag);
		messageBody.append(resword.getString("job_error_mail.userEmail"));
		messageBody.append(" ").append(currentUser.getEmail()).append(listItemEndTag);
		messageBody.append(resword.getString("job_error_mail.userRole"));
		messageBody.append(" ").append(currentUser.getActiveStudyRoleName()).append(listItemEndTag);
		if (currentStudy.isSite()) {
			messageBody.append(resword.getString("job_error_mail.siteName"));
			messageBody.append(" ").append(currentStudy.getName()).append(listItemEndTag);
			messageBody.append(resword.getString("job_error_mail.studyName"));
			messageBody.append(" ").append(currentStudy.getParentStudyName()).append(listItemEndTag);
		} else {
			messageBody.append(resword.getString("job_error_mail.studyName"));
			messageBody.append(" ").append(currentStudy.getName()).append(listItemEndTag);
		}
		messageBody.append(resword.getString("job_error_mail.serverUrl"));
		messageBody.append(" ").append(CoreResources.getSystemURL()).append(listItemEndTag);
		messageBody.append("</ul>");
		messageBody.append(respage.getString("dcf_email_please_review"));
		messageBody.append(respage.getString("email_body_simple_separator"));
		messageBody.append(respage.getString("email_body_simple_separator"));
		messageBody.append(EmailUtil.getEmailBodyEnd());
		messageBody.append(EmailUtil.getEmailFooter(locale));
		messageBody.toString();
	}

	private boolean sendEmail() {
		boolean messageSent = true;
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setFrom(EmailEngine.getAdminEmail());
			mimeMessageHelper.setTo(recipientEmail);
			mimeMessageHelper.setSubject(emailSubject);
			mimeMessageHelper.setText(messageBody.toString(), true);
			FileSystemResource file = new FileSystemResource(dcfFilePath);
			mimeMessageHelper.addAttachment(file.getFilename(), file);
			mailSender.send(mimeMessage);
			logger.debug("DCF Email sent successfully on {}", new Date());
		} catch (MessagingException e) {
			e.printStackTrace();
			logger.debug("Email could not be sent on {} due to: {}", new Date(), e.toString());
			messageSent = false;
		}
		return messageSent;
	}

	/**
	 * Builds instance of DcfEmailer.
	 * 
	 * @author Frank
	 * 
	 */
	public static class DcfEmailerBuilder {
		private boolean multipleDcfs;
		private String dcfName;
		private String dcfFilePath;
		private String recipientEmail;
		private String emailSubject;
		private JavaMailSender mailSender;
		private StudyBean currentStudy;
		private UserAccountBean currentUser;

		/**
		 * Adds DCF Name.
		 * 
		 * @param dcfName
		 *            String
		 * @return DcfEmailerBuilder
		 */
		public DcfEmailerBuilder addDcfName(String dcfName) {
			this.dcfName = dcfName;
			return this;
		}

		/**
		 * Adds DCF File Path.
		 * 
		 * @param dcfFilePath
		 *            String
		 * @return DcfEmailerBuilder
		 */
		public DcfEmailerBuilder addDcfFilePath(String dcfFilePath) {
			this.dcfFilePath = dcfFilePath;
			return this;
		}

		/**
		 * Adds Recipient Email.
		 * 
		 * @param recipientEmail
		 *            String
		 * @return DcfEmailerBuilder
		 */
		public DcfEmailerBuilder addRecipientEmail(String recipientEmail) {
			this.recipientEmail = recipientEmail;
			return this;
		}

		/**
		 * Adds email subject.
		 * 
		 * @param emailSubject
		 *            String
		 * @return DcfEmailerBuilder
		 */
		public DcfEmailerBuilder addEmailSubject(String emailSubject) {
			this.emailSubject = emailSubject;
			return this;
		}

		/**
		 * Adds MailSender.
		 * 
		 * @param mailSender
		 *            JavaMailSender
		 * @return DcfEmailerBuilder
		 */
		public DcfEmailerBuilder addMailSender(JavaMailSender mailSender) {
			this.mailSender = mailSender;
			return this;
		}

		/**
		 * Specifies whether DCFs are mulitple.
		 * 
		 * @param multipleDcfs
		 *            boolean
		 * @return DcfEmailerBuilder
		 */
		public DcfEmailerBuilder setMultipleDcfs(boolean multipleDcfs) {
			this.multipleDcfs = multipleDcfs;
			return this;
		}

		/**
		 * Sets current study.
		 * 
		 * @param study
		 *            StudyBean
		 * @return DcfEmailerBuilder
		 */
		public DcfEmailerBuilder setCurrentStudy(StudyBean study) {
			this.currentStudy = study;
			return this;
		}

		/**
		 * Sets current user.
		 * 
		 * @param user
		 *            UserAccountBean
		 * @return DcfEmailerBuilder
		 */
		public DcfEmailerBuilder setCurrentUser(UserAccountBean user) {
			this.currentUser = user;
			return this;
		}

		/**
		 * Builds DcfEmailer instance.
		 * 
		 * @return DcfEmailer
		 */
		public DcfEmailer build() {
			return new DcfEmailer(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getResourceBundleKeyForAction() {
		return emailActionResourceBundleKey;
	}
}
