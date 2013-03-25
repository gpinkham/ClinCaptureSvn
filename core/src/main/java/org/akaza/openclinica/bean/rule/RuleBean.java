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
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.rule;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.oid.GenericOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.bean.rule.expression.ExpressionBean;

import java.util.List;

/**
 * <p>
 * Rule, the object that collects rules associated with RuleSets.
 * </p>
 * If the sourceExpression evaluates to true then the targetExpression should evaluate to true too ; if target does not
 * evaluate to true actions will be executed.
 * 
 * @author Krikor Krumlian
 */
public class RuleBean extends AuditableEntityBean {

	private String oid;
	private String type;
	private String description;
	private boolean enabled;

	private ExpressionBean expression;
	private List<RuleSetRuleBean> ruleSetRules;
	private OidGenerator oidGenerator;

	public RuleBean() {
		this.oidGenerator = new GenericOidGenerator();
	}

	// SETTERS & GETTERS

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

	public ExpressionBean getExpression() {
		return expression;
	}

	public void setExpression(ExpressionBean expression) {
		this.expression = expression;
	}

	public List<RuleSetRuleBean> getRuleSetRules() {
		return ruleSetRules;
	}

	public void setRuleSetRules(List<RuleSetRuleBean> ruleSetRules) {
		this.ruleSetRules = ruleSetRules;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public OidGenerator getOidGenerator() {
		return oidGenerator;
	}

	public void setOidGenerator(OidGenerator oidGenerator) {
		this.oidGenerator = oidGenerator;
	}
}
