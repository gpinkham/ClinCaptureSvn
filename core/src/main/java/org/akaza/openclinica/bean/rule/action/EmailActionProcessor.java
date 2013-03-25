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

package org.akaza.openclinica.bean.rule.action;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.IEmailEngine;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import javax.mail.MessagingException;
import javax.sql.DataSource;

@SuppressWarnings({"unchecked"})
public class EmailActionProcessor implements ActionProcessor {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	DataSource ds;
	IEmailEngine emailEngine;

	public EmailActionProcessor(DataSource ds) {
		this.ds = ds;
	}

	public void execute(RuleActionBean ruleAction, int itemDataBeanId, String itemData, StudyBean currentStudy,
			UserAccountBean ub, Object... arguments) {
		HashMap<String, String> arg0 = (HashMap<String, String>) arguments[0];
		sendEmail(ruleAction, arg0.get("body"), arg0.get("subject"));
	}

	private void sendEmail(RuleActionBean ruleAction, String body, String subject) throws OpenClinicaSystemException {

		logger.info("Sending email...");
		try {
			getEmailEngine().process(((EmailActionBean) ruleAction).getTo().trim(), EmailEngine.getAdminEmail(),
					subject, body);
			logger.info("Sending email done..");
		} catch (MessagingException me) {
			logger.error("Email could not be sent");
			throw new OpenClinicaSystemException(me.getMessage());
		}
	}

	private IEmailEngine getEmailEngine() {
		emailEngine = emailEngine != null ? emailEngine : new EmailEngine(EmailEngine.getSMTPHost(), "5");
		return emailEngine;
	}

	public void setEmailEngine(IEmailEngine emailEngine) {
		this.emailEngine = emailEngine;
	}
}
