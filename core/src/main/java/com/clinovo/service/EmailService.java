package com.clinovo.service;

import com.clinovo.bean.EmailDetails;
import com.clinovo.model.EmailLog;

/**
 * Service that will send emails and add an entity to audit log.
 */
public interface EmailService {

	/**
	 * Send email to some user.
	 * @param emailDetails EmailDetails
	 */
	void sendEmail(EmailDetails emailDetails);


	/**
	 * Rend email to some user.
	 * @param emailLog EmailLog
	 * @param sender String
	 * @param sendBy int
	 */
	void resendEmail(EmailLog emailLog, String sender, int sendBy);
}
