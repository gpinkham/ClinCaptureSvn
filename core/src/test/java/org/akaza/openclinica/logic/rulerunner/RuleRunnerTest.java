/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package org.akaza.openclinica.logic.rulerunner;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class RuleRunnerTest extends DefaultAppContextTest {

	private RuleActionContainer ruleActionContainer;
	private List<RuleActionContainer> ruleActionContainerList;
	private RuleRunner ruleRunner;
	private static final int THREE = 3;
	private static final int FIVE = 5;

	@Before
	public void setUp() {
		ruleActionContainerList = new ArrayList<RuleActionContainer>();
		ruleActionContainerList.add(initRuleActionContainer(1));
		ruleActionContainerList.add(initRuleActionContainer(2));
		ruleActionContainerList.add(initRuleActionContainer(THREE));
		ruleRunner = new RuleRunner(dataSource, "", "", new JavaMailSenderImpl());
	}

	private RuleActionContainer initRuleActionContainer(int id) {
		RuleActionBean ruleAction = new RuleActionBean();
		ruleAction.setId(id);
		ExpressionBean expressionBean = new ExpressionBean();
		expressionBean.setId(id);
		ItemDataBean itemDataBean = new ItemDataBean();
		itemDataBean.setId(id);
		RuleSetBean ruleSetBean = new RuleSetBean();
		ruleSetBean.setId(id);
		return new RuleActionContainer(ruleAction, expressionBean, itemDataBean, ruleSetBean);
	}

	@Test
	public void testThatRuleActionContainerAlreadyExistsInListReturnsTrue() {
		ruleActionContainer = initRuleActionContainer(1);
		assertTrue(ruleRunner.ruleActionContainerAlreadyExistsInList(ruleActionContainer, ruleActionContainerList));
	}

	@Test
	public void testThatRuleActionContainerAlreadyExistsInListReturnsFalse() {
		ruleActionContainer = initRuleActionContainer(FIVE);
		assertFalse(ruleRunner.ruleActionContainerAlreadyExistsInList(ruleActionContainer, ruleActionContainerList));
	}
}
