package com.clinovo.bean;

import com.clinovo.enums.EmailAction;

/**
 * Class that is used to store all email details.
 */
public class EmailDetails {

	private int studyId;
	private EmailAction action;
	private String to;
	private String cc;
	private String from;
	private int sentBy;
	private String subject;
	private String message;
	private boolean htmlEmail;
	private String[] fileAttachments;

	/**
	 * Default constructor.
	 */
	public EmailDetails() {
	}

	public EmailAction getAction() {
		return action;
	}

	public void setAction(EmailAction action) {
		this.action = action;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String[] getFileAttachments() {
		return fileAttachments != null ? fileAttachments.clone() : null;
	}

	/**
	 * Set file attachments.
	 * @param fileAttachments String array.
	 */
	public void setFileAttachments(String[] fileAttachments) {
		if (fileAttachments != null) {
			this.fileAttachments = fileAttachments.clone();
		} else {
			this.fileAttachments = null;
		}
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isHtmlEmail() {
		return htmlEmail;
	}

	public void setHtmlEmail(boolean htmlEmail) {
		this.htmlEmail = htmlEmail;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getSentBy() {
		return sentBy;
	}

	public void setSentBy(int sentBy) {
		this.sentBy = sentBy;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
}
