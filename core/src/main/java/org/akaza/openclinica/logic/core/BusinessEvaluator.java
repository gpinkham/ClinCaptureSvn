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
 * Created on Sep 1, 2005
 *
 *
 */
package org.akaza.openclinica.logic.core;

import org.akaza.openclinica.bean.core.EntityBean;

import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings({"rawtypes"})
public abstract class BusinessEvaluator implements Runnable {
	protected ArrayList ruleSet;
	protected boolean hasBeenUpdated;
	protected EntityBean businessObject;

	// the 'subject' that shall be affected:
	// could be subject, crf, etc.

	public BusinessEvaluator(EntityBean o) {
		ruleSet = new ArrayList();
		hasBeenUpdated = true;
		businessObject = o;
	}

	public void run() {
		if (hasBeenUpdated) {
			evaluateRuleSet();
		}
	}

	protected void evaluateRuleSet() {
		synchronized (this) {
			for (Iterator it = ruleSet.iterator(); it.hasNext();) {
				BusinessRule bRule = (BusinessRule) it.next();
				if (bRule.isPropertyTrue(bRule.getClass().getName())) {
					bRule.doAction(businessObject);
				}
			}
			hasBeenUpdated = false;
		}
	}

	protected abstract void assertRuleSet();
}
