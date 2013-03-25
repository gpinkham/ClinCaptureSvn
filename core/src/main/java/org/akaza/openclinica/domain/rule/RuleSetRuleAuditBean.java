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
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.domain.rule;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.akaza.openclinica.domain.Status;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "rule_set_rule_audit")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "rule_set_rule_audit_id_seq") })
public class RuleSetRuleAuditBean extends AbstractMutableDomainObject {

	RuleSetRuleBean ruleSetRuleBean;
	Status status;
	UserAccountBean updater;
	Date dateUpdated;

	// TODO: phase out the use of these Once the above beans become Hibernated
	protected Integer updaterId;

	/**
	 * @return the ruleSetBean
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "rule_set_rule_id")
	public RuleSetRuleBean getRuleSetRuleBean() {
		return ruleSetRuleBean;
	}

	/**
	 * @param ruleSetBean
	 *            the ruleSetBean to set
	 */
	public void setRuleSetRuleBean(RuleSetRuleBean ruleSetRuleBean) {
		this.ruleSetRuleBean = ruleSetRuleBean;
	}

	@Type(type = "status")
	@Column(name = "status_id")
	public Status getStatus() {
		if (status != null) {
			return status;
		} else
			return Status.AVAILABLE;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the updater
	 */
	@Transient
	public UserAccountBean getUpdater() {
		return updater;
	}

	/**
	 * @param updater
	 *            the updater to set
	 */
	public void setUpdater(UserAccountBean updater) {
		this.updater = updater;
		if (updater != null) {
			this.updaterId = updater.getId();
		}
	}

	/**
	 * @return the dateUpdated
	 */
	@Column(name = "date_updated")
	public Date getDateUpdated() {
		return new Date();
	}

	@Transient
	public Date getCurrentUpdatedDate() {
		return this.dateUpdated;
	}

	/**
	 * @param dateUpdated
	 *            the dateUpdated to set
	 */
	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	/**
	 * @return the updaterId
	 */
	@Column(name = "updater_id")
	public Integer getUpdaterId() {
		return updaterId;
	}

	/**
	 * @param updaterId
	 *            the updaterId to set
	 */
	public void setUpdaterId(Integer updaterId) {
		this.updaterId = updaterId;
	}

}
