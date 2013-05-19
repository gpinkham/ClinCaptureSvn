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
 * Copyright 2003-2010 Akaza Research 
 */
package org.akaza.openclinica.logic.rulerunner;

import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;

public class RuleActionContainer implements Comparable<RuleActionBean> {
	RuleActionBean ruleAction;
	ExpressionBean expressionBean;
	ItemDataBean itemDataBean;
	RuleSetBean ruleSetBean;

	public RuleActionContainer(RuleActionBean ruleAction, ExpressionBean expressionBean, ItemDataBean itemDataBean,
			RuleSetBean ruleSetBean) {
		super();
		this.ruleAction = ruleAction;
		this.expressionBean = expressionBean;
		this.itemDataBean = itemDataBean;
		this.ruleSetBean = ruleSetBean;

	}

	public RuleActionBean getRuleAction() {
		return ruleAction;
	}

	public void setRuleAction(RuleActionBean ruleAction) {
		this.ruleAction = ruleAction;
	}

	public ExpressionBean getExpressionBean() {
		return expressionBean;
	}

	public void setExpressionBean(ExpressionBean expressionBean) {
		this.expressionBean = expressionBean;
	}

	public ItemDataBean getItemDataBean() {
		return itemDataBean;
	}

	public void setItemDataBean(ItemDataBean itemDataBean) {
		this.itemDataBean = itemDataBean;
	}

	public RuleSetBean getRuleSetBean() {
		return ruleSetBean;
	}

	public void setRuleSetBean(RuleSetBean ruleSetBean) {
		this.ruleSetBean = ruleSetBean;
	}

	public int compareTo(RuleActionBean o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
