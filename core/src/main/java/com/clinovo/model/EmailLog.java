package com.clinovo.model;

import com.clinovo.bean.EmailDetails;
import com.clinovo.enums.BooleanEnum;
import com.clinovo.enums.EmailAction;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Arrays;
import java.util.Date;

/**
 * AuditLogEmail Bean.
 */
@Entity
@Table(name = "email_log")
@GenericGenerator(name = "id-generator", strategy = "native",
		parameters = {@Parameter(name = "sequence_name", value = "email_log_id_seq")})
public class EmailLog extends AbstractMutableDomainObject {

	private int studyId;
	private int parentId;
	private EmailAction action;
	private String recipient;
	private String cc;
	private String sender;
	private Date dateSent;
	private int sentBy;
	private String subject;
	private String message;
	private BooleanEnum wasSent;
	private BooleanEnum htmlEmail;
	private String fileAttachments;
	private BooleanEnum wasShown;
	private String error;
	private UserAccountBean senderAccount;

	/**
	 * Default constructor.
	 */
	public EmailLog() {
	}

	/**
	 * Create audit entity from email details.
	 *
	 * @param emailDetails EmailDetails bean.
	 */
	public EmailLog(EmailDetails emailDetails) {
		this.studyId = emailDetails.getStudyId();
		this.action = emailDetails.getAction();
		this.recipient = emailDetails.getTo();
		this.cc = emailDetails.getCc();
		this.dateSent = new Date();
		this.sentBy = emailDetails.getSentBy();
		this.subject = emailDetails.getSubject();
		this.message = emailDetails.getMessage();
		this.wasSent = BooleanEnum.TRUE;
		this.htmlEmail = BooleanEnum.getEntity(emailDetails.isHtmlEmail());
		this.wasShown = BooleanEnum.FALSE;
		if (emailDetails.getFileAttachments() != null) {
			this.fileAttachments = StringUtil.join(",", Arrays.asList(emailDetails.getFileAttachments()));
		}
		if (emailDetails.getFrom() == null || emailDetails.getFrom().isEmpty()) {
			this.sender = EmailEngine.getAdminEmail();
		} else {
			this.sender = emailDetails.getFrom();
		}
	}

	@Enumerated(EnumType.STRING)
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

	public Date getDateSent() {
		return dateSent != null ? new Date(dateSent.getTime()) : null;
	}

	/**
	 * Set Date Sent.
	 * @param dateSent Date
	 */
	public void setDateSent(Date dateSent) {
		if (dateSent != null) {
			this.dateSent = new Date(dateSent.getTime());
		} else {
			this.dateSent = null;
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
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

	@Enumerated(EnumType.STRING)
	public BooleanEnum getHtmlEmail() {
		return htmlEmail;
	}

	public void setHtmlEmail(BooleanEnum htmlEmail) {
		this.htmlEmail = htmlEmail;
	}

	public String getFileAttachments() {
		return fileAttachments;
	}

	public void setFileAttachments(String fileAttachments) {
		this.fileAttachments = fileAttachments;
	}

	@Enumerated(EnumType.STRING)
	public BooleanEnum getWasShown() {
		return wasShown;
	}

	public void setWasShown(BooleanEnum wasShown) {
		this.wasShown = wasShown;
	}

	@Enumerated(EnumType.STRING)
	public BooleanEnum getWasSent() {
		return wasSent;
	}

	public void setWasSent(BooleanEnum wasSent) {
		this.wasSent = wasSent;
	}

	/**
	 * Check if it's an html email or plain text.
	 *
	 * @return boolean
	 */
	@Transient
	public boolean htmlEmail() {
		return htmlEmail.equals(BooleanEnum.TRUE);
	}

	/**
	 * Get was sent in boolean.
	 *
	 * @return boolean
	 */
	@Transient
	public boolean wasSent() {
		return BooleanEnum.toBoolean(wasSent);
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	@Transient
	public UserAccountBean getSenderAccount() {
		return senderAccount;
	}

	public void setSenderAccount(UserAccountBean senderAccount) {
		this.senderAccount = senderAccount;
	}
}