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

package org.akaza.openclinica.domain.rule.action;

import com.clinovo.context.SubmissionContext;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.dao.hibernate.RuleActionRunLogDao;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.RuleRunner.RuleRunnerMode;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

@SuppressWarnings("unchecked")
public class EmailActionProcessor implements ActionProcessor {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private Connection connection;
	private DataSource ds;
	private JavaMailSenderImpl mailSender;
	private RuleActionRunLogDao ruleActionRunLogDao;
	private DiscrepancyNoteService discrepancyNoteService;
	private RuleSetRuleBean ruleSetRule;

	public EmailActionProcessor(Connection connection, DataSource ds, JavaMailSenderImpl mailSender,
			RuleActionRunLogDao ruleActionRunLogDao, RuleSetRuleBean ruleSetRule) {
		this.connection = connection;
		this.ds = ds;
		this.mailSender = mailSender;
		this.ruleSetRule = ruleSetRule;
		this.ruleActionRunLogDao = ruleActionRunLogDao;
	}

	public RuleActionBean execute(RuleRunnerMode ruleRunnerMode, ExecutionMode executionMode,
			RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData, StudyBean currentStudy,
			UserAccountBean ub, Object... arguments) {
		switch (executionMode) {
		case DRY_RUN: {
			return ruleAction;
		}

		case SAVE: {
			HashMap<String, String> arg0 = (HashMap<String, String>) arguments[1];
			if (!sendEmail(ruleAction, arg0.get("body"), arg0.get("subject"))) {
				getDiscrepancyNoteService().saveFieldNotes(((EmailActionBean) ruleAction).getExceptionMessage(),
						itemDataBean.getId(), itemData, connection, currentStudy, ub, true);
			}
			RuleActionRunLogBean ruleActionRunLog = new RuleActionRunLogBean(ruleAction.getActionType(), itemDataBean,
					itemDataBean.getValue(), ruleSetRule.getRuleBean().getOid());
			ruleActionRunLogDao.saveOrUpdate(ruleActionRunLog, connection);
			return null;
		}
		default:
			return null;
		}
	}

	private boolean sendEmail(RuleActionBean ruleAction, String body, String subject) {
		boolean sent = true;
		logger.info("Sending email...");
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
			helper.setFrom(EmailEngine.getAdminEmail());
			helper.setTo(processMultipleImailAddresses(((EmailActionBean) ruleAction).getTo().trim()));
			helper.setSubject(subject);
			helper.setText(body);

			mailSender.send(mimeMessage);
			logger.debug("Email sent successfully on {}", new Date());
		} catch (Exception e) {
			sent = false;
			logger.error("Email could not be sent. ", e);
		}
		return sent;
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
			addressTo[i] = new InternetAddress(recipientsArray.get(i));
		}
		return addressTo;

	}

	private DiscrepancyNoteService getDiscrepancyNoteService() {
		discrepancyNoteService = this.discrepancyNoteService != null ? discrepancyNoteService
				: new DiscrepancyNoteService(ds);
		return discrepancyNoteService;
	}

	public Object execute(SubmissionContext context) {
		// Do nothing
		return null;
	}
}
