package com.clinovo.service;

import com.clinovo.bean.EmailDetails;

/**
 * Service that will send emails and add an entity to audit log.
 */
public interface EmailService {

	/**
	 * Send email to some user.
	 * @param emailDetails EmailDetails
	 */
	void sendEmail(EmailDetails emailDetails);
}
