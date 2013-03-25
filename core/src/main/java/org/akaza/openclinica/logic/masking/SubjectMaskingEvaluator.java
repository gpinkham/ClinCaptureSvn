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
 * Created on Sep 1, 2005
 *
 *
 */
package org.akaza.openclinica.logic.masking;

import org.akaza.openclinica.bean.masking.MaskingBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.logic.core.BusinessEvaluator;
import org.akaza.openclinica.logic.core.BusinessRule;
import org.akaza.openclinica.logic.masking.rules.MaskSubjectDOBRule;

import java.util.Iterator;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SubjectMaskingEvaluator extends BusinessEvaluator {
	protected MaskingBean mBean;

	public SubjectMaskingEvaluator(SubjectBean sb, MaskingBean mBean) {
		super(sb);
		assertRuleSet();
		this.mBean = mBean;
	}

	@Override
	public void assertRuleSet() {
		if (mBean.getRuleMap().containsKey("org.akaza.openclinica.logic.masking.rule.MaskSubjectDOBRule"))
			ruleSet.add(new MaskSubjectDOBRule());
	}

	@Override
	protected void evaluateRuleSet() {
		// can modify this as necessary? tbh
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
}
