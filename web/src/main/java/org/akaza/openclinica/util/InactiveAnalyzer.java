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

package org.akaza.openclinica.util;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.web.SQLInitServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.clinovo.util.EmailUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.mail.internet.MimeMessage;

//Added by Clinovo
public class InactiveAnalyzer {

	protected static final Logger logger = LoggerFactory.getLogger(InactiveAnalyzer.class.getName());

	private static final String INACTIVE_ACCOUNT = "INACTIVE_ACCOUNT";

	public static void analyze(UserAccountBean userAccountBean, UserAccountDAO userAccountDao, MessageSource messageSource, JavaMailSenderImpl mailSender) {
		// Clinovo - tbh - protect your inputs!
		String strMaxInactiveAccount = SQLInitServlet.getField("max_inactive_account") != "" ? SQLInitServlet
				.getField("max_inactive_account") : "90";
		int maxInactiveAccount = Integer.parseInt(strMaxInactiveAccount);
		if (maxInactiveAccount != 0) {
			maxInactiveAccount = strMaxInactiveAccount != "" ? maxInactiveAccount * (-1) : -30; // default is 30 days
																								// ago
			Calendar calendar;
			Date cutoffDate;
			Date currDate = new Date();
			calendar = Calendar.getInstance();
			calendar.setTime(currDate);
			calendar.add(Calendar.DATE, maxInactiveAccount); // default cut-off date is 30 days ago
			cutoffDate = calendar.getTime();
			if (userAccountBean != null) { // if account exists
				if (!userAccountBean.getName().equals("root")) { // if not root account
					if (userAccountBean.getLastVisitDate() != null) { // not a newly created user
						if (userAccountBean.getLastVisitDate().before(cutoffDate)) {
							userAccountDao.lockUser(userAccountBean.getId()); // lock
							updateBean(userAccountBean);
							sendAccountLockEmail(userAccountBean, messageSource, INACTIVE_ACCOUNT, maxInactiveAccount, mailSender);
						}
					} else { // new user
						if (userAccountBean.getCreatedDate().before(cutoffDate)) { // account was created before cut-off
																					// date
							userAccountDao.lockUser(userAccountBean.getId()); // lock
							updateBean(userAccountBean);
							sendAccountLockEmail(userAccountBean, messageSource, INACTIVE_ACCOUNT, maxInactiveAccount, mailSender);
						}
					}
				}
			}
		}
	}

	private static void updateBean(UserAccountBean uab) {
		uab.setAccountNonLocked(false);
		uab.setStatus(Status.LOCKED);
	}

	private static void sendAccountLockEmail(UserAccountBean userAccountBean, MessageSource messageSource, 
			String reason, int numdays, JavaMailSenderImpl mailSender) {
		try {
			Locale locale = CoreResources.getSystemLocale();
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
			helper.setFrom(EmailEngine.getAdminEmail());
			helper.setTo(userAccountBean.getEmail());
			String subject = messageSource.getMessage("openclinica_account", null, locale) + " '" + userAccountBean.getName() + "' " +
					messageSource.getMessage("has_been_locked", null, locale) + " " + messageSource.getMessage("for_security_reasons", null, locale);
			helper.setSubject(subject);
			String text = "";
			if (reason.equals(INACTIVE_ACCOUNT)) {
				text = messageSource.getMessage("when_a_user_tried_to_login_with_this_account", null, locale) +
				" " + messageSource.getMessage("after_a_period_of_inactivity_of_more_than", null, locale) + " "+(-1) * numdays + 
				" " + messageSource.getMessage("days_", null, locale) + ".";
			} else {
				text = messageSource.getMessage("due_to_excessive_failed_login_attempts", null, locale) + ".";
			}
			helper.setText(
					EmailUtil.getEmailBodyStart().concat(messageSource.getMessage("dear_openclinica_administrator", null, locale))
							.concat(",<br><br>")
							.concat(messageSource.getMessage("please_be_informed_that", null, locale) + " " + subject + ". ")
							.concat(messageSource.getMessage("this_action_was_triggered", null, locale)).concat(" ")
							.concat(text).concat("<br><br><br>").concat(CoreResources.getSystemURL()).concat("<br><br> ")
							.concat(messageSource.getMessage("best_system_admin", null, locale).replace("{0}",""))
							.concat(EmailUtil.getEmailBodyEnd()).concat(EmailUtil.getEmailFooter(CoreResources.getSystemLocale()))
							, true);
			mailSender.send(mimeMessage);
		} catch (Exception ex) {
			logger.error("Error has occurred.", ex);
		}
	}
}
