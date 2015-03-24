package com.clinovo.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Service to send emails from the Controllers.
 */
public interface EmailService {

	/**
	 * Send an email.
	 *
	 * @param to        String.
	 * @param subject   String.
	 * @param body      String.
	 * @param htmlEmail Boolean.
	 * @param request   HttpServletRequest.
	 * @return result of the attempt.
	 * @throws Exception if there was an error while attempt to send email.
	 */
	Boolean sendEmail(String to, String subject, String body, Boolean htmlEmail, HttpServletRequest request)
			throws Exception;

	/**
	 * Send an email.
	 *
	 * @param to          String.
	 * @param subject     String.
	 * @param body        String.
	 * @param htmlEmail   Boolean.
	 * @param sendMessage Boolean.
	 * @param request     HttpServletRequest.
	 * @return result of the attempt.
	 * @throws Exception if there was an error while attempt to send email.
	 */
	Boolean sendEmail(String to, String subject, String body, Boolean htmlEmail, Boolean sendMessage,
			HttpServletRequest request) throws Exception;

	/**
	 * Send an email with success message and fail message.
	 *
	 * @param to             String.
	 * @param from           String.
	 * @param subject        String.
	 * @param body           String.
	 * @param htmlEmail      Boolean.
	 * @param successMessage String.
	 * @param failMessage    String
	 * @param sendMessage    Boolean.
	 * @param request        HttpServletRequest.
	 * @return result of the attempt.
	 * @throws Exception if there was an error while attempt to send email.
	 */
	Boolean sendEmail(String to, String from, String subject, String body, Boolean htmlEmail,
			String successMessage, String failMessage, Boolean sendMessage, HttpServletRequest request)
			throws Exception;

	/**
	 * Send an email with fail message, success message and attached files.
	 *
	 * @param to             String.
	 * @param from           String.
	 * @param subject        String.
	 * @param body           String.
	 * @param htmlEmail      Boolean.
	 * @param successMessage String.
	 * @param failMessage    String.
	 * @param sendMessage    Boolean.
	 * @param files          String[].
	 * @param request        HttpServletRequest.
	 * @return result of the attempt.
	 * @throws Exception if there was an error while attempt to send email.
	 */
	Boolean sendEmailWithAttach(String to, String from, String subject, String body, Boolean htmlEmail,
			String successMessage, String failMessage, Boolean sendMessage, String[] files, HttpServletRequest request)
			throws Exception;
}



