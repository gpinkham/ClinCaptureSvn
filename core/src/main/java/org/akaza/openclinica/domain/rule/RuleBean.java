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

/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.domain.rule;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.oid.GenericOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.domain.AbstractAuditableMutableDomainObject;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * <p>
 * Rule, the object that collects rules associated with RuleSets.
 * </p>
 * 
 * @author Krikor Krumlian
 */
@Entity
@Table(name = "rule")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence_name", value = "rule_id_seq") })
public class RuleBean extends AbstractAuditableMutableDomainObject {

	private String oid;
	private String name;
	private String type;
	private String description;
	private boolean enabled;
	private StudyBean study;

	private ExpressionBean expression;
	private List<RuleSetRuleBean> ruleSetRules;
	private OidGenerator oidGenerator;

	// TODO : Pending conversion of the objects below to use Hibernate
	private Integer studyId;

	public RuleBean() {
		this.oidGenerator = new GenericOidGenerator();
	}

	// SETTERS & GETTERS

	@Transient
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "rule_expression_id")
	public ExpressionBean getExpression() {
		return expression;
	}

	public void setExpression(ExpressionBean expression) {
		this.expression = expression;
	}

	@OneToMany(mappedBy = "ruleBean")
	public List<RuleSetRuleBean> getRuleSetRules() {
		return ruleSetRules;
	}

	public void setRuleSetRules(List<RuleSetRuleBean> ruleSetRules) {
		this.ruleSetRules = ruleSetRules;
	}

	@Column(name = "oc_oid")
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the studyId
	 */
	public Integer getStudyId() {
		return studyId;
	}

	/**
	 * @param studyId
	 *            the studyId to set
	 */
	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	@Transient
	public StudyBean getStudy() {
		return study;
	}

	public void setStudy(StudyBean study) {
		if (study.getId() > 0) {
			this.studyId = study.getId();
		}
		this.study = study;
	}

	@Transient
	public OidGenerator getOidGenerator() {
		return oidGenerator;
	}

	public void setOidGenerator(OidGenerator oidGenerator) {
		this.oidGenerator = oidGenerator;
	}
}
