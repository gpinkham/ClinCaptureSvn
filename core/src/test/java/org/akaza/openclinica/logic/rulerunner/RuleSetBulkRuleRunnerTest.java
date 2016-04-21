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
import java.util.HashMap;
import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.domain.rule.RuleSetBasedViewContainer;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class RuleSetBulkRuleRunnerTest extends DefaultAppContextTest {

	private StudyBean currentStudy;
	private CRFBean crf;
	private UserAccountBean currentUser;
	private RuleSetBulkRuleRunner ruleRunner;
	private List<RuleSetBean> ruleSets;
	private HashMap<String, String> variableAndValue;
	private List<RuleSetBasedViewContainer> ruleSetBasedViews;
	private static final int SIX = 6;
	private static final int SEVEN = 7;
	private static final int EIGHT = 8;
	private static final int THIRTY_SIX = 36;

	@Before
	public void setUp() {
		currentStudy = (StudyBean) studyDAO.findByPK(1);
		currentUser = (UserAccountBean) userAccountDAO.findByPK(1);
		crf = (CRFBean) crfdao.findByPK(SIX);
		ruleRunner = new RuleSetBulkRuleRunner(dataSource, "", "", new JavaMailSenderImpl());
		ruleRunner.setRuleActionRunLogDao(ruleActionRunLogDao);
		ruleRunner.setDynamicsMetadataService(ruleSetService.getDynamicsMetadataService());
		variableAndValue = new HashMap<String, String>();
	}

	private void setUpRuleSetObjects(List<RuleSetBean> ruleSets) {
		for (RuleSetBean ruleSet : ruleSets) {
			ruleSet.setStudy(currentStudy);
			ruleSet.setStudyEventDefinition((StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1));
			ruleSet.setCrf(crf);
			ruleSet.setCrfVersion((CRFVersionBean) crfVersionDao.findByPK(EIGHT));
			ruleSet.setItemGroup((ItemGroupBean) itgdao.findByPK(SIX));
			ruleSet.setItem((ItemBean) idao.findByPK(THIRTY_SIX));
		}
	}

	@Test
	public void testThatRunRuleSetBulkRuleReturnsAffectedSubject() {
		ruleSets = new ArrayList<RuleSetBean>();
		ruleSets.add(ruleSetDao.findById(SEVEN));
		setUpRuleSetObjects(ruleSets);
		ruleSets = ruleSetService.filterRuleSetsByStudyEventOrdinal(ruleSets, "8");
		ruleSetBasedViews = ruleRunner.runRulesBulkFromRuleSetScreen(ruleSets, ExecutionMode.DRY_RUN, currentStudy,
				variableAndValue, currentUser);
		assertEquals(1, ruleSetBasedViews.size());
	}

	@Test
	public void testThatRunRuleSetBulkRuleWithRepeatingGroupsReturnsAffectedSubject() {
		ruleSets = new ArrayList<RuleSetBean>();
		ruleSets.add(ruleSetDao.findById(SIX));
		setUpRuleSetObjects(ruleSets);
		ruleSets = ruleSetService.filterRuleSetsByStudyEventOrdinal(ruleSets, "8");
		ruleSetBasedViews = ruleRunner.runRulesBulkFromRuleSetScreen(ruleSets, ExecutionMode.DRY_RUN, currentStudy,
				variableAndValue, currentUser);
		assertEquals(1, ruleSetBasedViews.size());
	}

}
