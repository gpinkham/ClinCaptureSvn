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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.core;

import org.akaza.openclinica.dao.core.CoreResources;

import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EmailEngine implements IEmailEngine {
	public static String getSMTPHost() {
		return CoreResources.getField(SMTPHostField);
	}

	public static String getAdminEmail() {
		return CoreResources.getField(AdminEmailField);
	}

	public static String SMTPHostField = "smtpHost";
	public static String AdminEmailField = "adminEmail";

	Properties props = new Properties();
	Session s = null;
	MimeMessage message = null;
	MimeMultipart mm = new MimeMultipart();
	MimeBodyPart mbp = new MimeBodyPart();

	/** Creates a new instance of emailEngine */
	public EmailEngine(String smtphost, String connectionTimeout) {
		Integer.parseInt(connectionTimeout);
		props.put("mail.smtp.host", smtphost);
		props.put("mail.smtp.connectiontimeout", connectionTimeout);
		s = Session.getDefaultInstance(props);
		message = new MimeMessage(s);
	}

	public EmailEngine(String smtphost) {
		props.put("mail.smtp.host", smtphost);
		props.put("mail.smtp.connectiontimeout", "90000");
		s = Session.getDefaultInstance(props);
		message = new MimeMessage(s);
	}

	public void process(String to, String from, String subject, String body) throws MessagingException {
		InternetAddress from2 = new InternetAddress(from);

		message.setFrom(from2);
		message.addRecipients(Message.RecipientType.TO, processMultipleImailAddresses(to));
		message.setSubject(subject);
		mbp.setContent(body, "text/plain");
		mm.addBodyPart(mbp);
		message.setContent(mm);
		Transport.send(message);
	}

	public void processHtml(String to, String from, String subject, String body) throws MessagingException {
		InternetAddress from2 = new InternetAddress(from);
		message.setFrom(from2);
		message.addRecipients(Message.RecipientType.TO, processMultipleImailAddresses(to));
		message.setSubject(subject);

		mbp.setContent(body, "text/html");
		mm.addBodyPart(mbp);
		message.setContent(mm);
		Transport.send(message);
	}

	private InternetAddress[] processMultipleImailAddresses(String to) throws MessagingException {
		ArrayList recipientsArray = new ArrayList();
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

}
