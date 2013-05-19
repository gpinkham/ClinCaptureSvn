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

import org.akaza.openclinica.domain.rule.action.ActionType;

import java.util.Comparator;
import java.util.HashMap;

public class RuleActionContainerComparator implements Comparator<RuleActionContainer> {

	HashMap<ActionType, String> order = new HashMap<ActionType, String>();

	public RuleActionContainerComparator() {
		order.put(ActionType.EMAIL, "1");
		order.put(ActionType.FILE_DISCREPANCY_NOTE, "2");
		order.put(ActionType.INSERT, "3");
		order.put(ActionType.SHOW, "4");
		order.put(ActionType.HIDE, "5");

	}

	public int compare(RuleActionContainer o1, RuleActionContainer o2) {
		return order.get(o1.getRuleAction().getActionType()).compareTo(order.get(o2.getRuleAction().getActionType()));
	}

}
