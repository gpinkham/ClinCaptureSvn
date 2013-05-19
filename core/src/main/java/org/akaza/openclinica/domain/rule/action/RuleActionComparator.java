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

package org.akaza.openclinica.domain.rule.action;

import java.util.Comparator;
import java.util.HashMap;

public class RuleActionComparator implements Comparator<RuleActionBean> {

	HashMap<ActionType, String> order = new HashMap<ActionType, String>();

	public RuleActionComparator() {

		order.put(ActionType.FILE_DISCREPANCY_NOTE, "1");
		order.put(ActionType.EMAIL, "2");
		order.put(ActionType.SHOW, "3");
		order.put(ActionType.HIDE, "4");
		order.put(ActionType.INSERT, "5");

	}

	public int compare(RuleActionBean o1, RuleActionBean o2) {
		return order.get(o1.getActionType()).compareTo(order.get(o2.getActionType()));
	}

}
