/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.core;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class OpenClinicaMailSender {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	JavaMailSenderImpl mailSender;

	public void sendEmail(String to, String subject, String body, Boolean htmlEmail) throws OpenClinicaSystemException {
		sendEmail(to, EmailEngine.getAdminEmail(), subject, body, htmlEmail);
	}

	public void sendEmail(String to, String from, String subject, String body, Boolean htmlEmail)
			throws OpenClinicaSystemException {
		try {

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, htmlEmail);
			helper.setFrom(from);
			helper.setTo(processMultipleImailAddresses(to.trim()));
			helper.setSubject(subject);
			helper.setText(body, true);

			mailSender.send(mimeMessage);
			logger.debug("Email sent successfully on {}", new Date());
		} catch (MailException me) {
			logger.debug("Email could not be sent on {} due to: {}", new Date(), me.toString());
			throw new OpenClinicaSystemException(me.getMessage());
		} catch (MessagingException e) {
			logger.debug("Email could not be sent on {} due to: {}", new Date(), e.toString());
			throw new OpenClinicaSystemException(e.getMessage());
		}
	}

	private InternetAddress[] processMultipleImailAddresses(String to) throws MessagingException {
		ArrayList<String> recipientsArray = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(to, ",");
		while (st.hasMoreTokens()) {
			recipientsArray.add(st.nextToken());
		}

		int sizeTo = recipientsArray.size();
		InternetAddress[] addressTo = new InternetAddress[sizeTo];
		for (int i = 0; i < sizeTo; i++) {
			addressTo[i] = new InternetAddress(recipientsArray.get(i).toString());
		}
		return addressTo;
	}

	public JavaMailSenderImpl getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

}
