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
package org.akaza.openclinica.bean.rule.action;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.rule.RuleSetRuleBean;

/**
 * @author Krikor Krumlian
 */
public class RuleActionBean extends AuditableEntityBean {

	private static final long serialVersionUID = 7019049957184162568L;
	private RuleSetRuleBean ruleSetRule;
	private ActionType actionType;
	private Boolean expressionEvaluatesTo;
	private String summary;
	private String curatedMessage;

	public String getCuratedMessage() {
		return curatedMessage;
	}

	public void setCuratedMessage(String curatedMessage) {
		this.curatedMessage = curatedMessage;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public Boolean getExpressionEvaluatesTo() {
		return expressionEvaluatesTo;
	}

	public void setExpressionEvaluatesTo(Boolean ifExpressionEvaluates) {
		this.expressionEvaluatesTo = ifExpressionEvaluates;
	}

	public RuleSetRuleBean getRuleSetRule() {
		return ruleSetRule;
	}

	public void setRuleSetRule(RuleSetRuleBean ruleSetRule) {
		this.ruleSetRule = ruleSetRule;
	}

}
