package com.clinovo.clincapture.bean.rule.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.ActionType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.clinovo.BaseTest;
import com.clinovo.rule.WebServiceAction;

// Web service action creation test drive
public class WebServiceActionTest extends BaseTest {

	private WebServiceAction wsAction = null;

	@Before
	public void setUp() {

		RuleSetRuleDao ruleSetRuleDAO = Mockito.mock(RuleSetRuleDao.class);
		
		
		RuleSetRuleBean bean = createRuleSetRuleBean();
		List<RuleSetRuleBean> returnList = new ArrayList<RuleSetRuleBean>();

		returnList.add(bean);
		Mockito.when(ruleSetRuleDAO.findByRuleSetStudyIdAndStatusAvail(Mockito.anyInt())).thenReturn(
				(ArrayList<RuleSetRuleBean>) returnList);
		
		ArrayList<RuleSetRuleBean> ruleSetBeanList = ruleSetRuleDAO.findByRuleSetStudyIdAndStatusAvail(0);
		wsAction = (WebServiceAction) ruleSetBeanList.get(0).getActions().get(0);

	}

	@Test
	public void testThatWebServiceActionHasCorrectType() {

		assertEquals("Should have web service action", "WEB_SERVICE", wsAction.getActionType().toString());
	}

	@Test
	public void testThatWebServiceActionHasCorrectTypeFromActionType() {

		assertEquals("Should have web service action", ActionType.WEB_SERVICE, wsAction.getActionType());
	}

	@Test
	public void testThatCreateWebServiceActionDoesNotReturnNull() {

		assertNotNull("Should never return null", wsAction);
	}

	@Test
	public void testThatCreateWebServiceActionReturnsValidActionWithStudyOID() {

		assertEquals("Should have valid studyOID", "SE_TEST", wsAction.getStudyOID());
	}

	@Test
	public void testThatCreateWebServiceActionReturnsValidActionWithStudyUsername() {

		assertEquals("Should have valid username", "test", wsAction.getUsername());
	}

	@Test
	public void testThatCreateWebServiceActionReturnsValidActionWithPassword() {

		assertEquals("Should have valid password", "test", wsAction.getPassword());
	}

	@Test
	public void testThatCreateWebServiceActionReturnsValidActionWithUrl() {

		assertEquals("Should have valid url", "http://clinovo.com/ws/randomize", wsAction.getUrl());
	}

	@Test
	public void testThatCreateWebServiceActionReturnsValidActionWithStudySubjectOID() {

		assertEquals("Should have valid study subject oid", "SE_TEST_SS_NUM", wsAction.getStudySubjectOID());
	}

	@Test
	public void testThatCreateWebServiceActionReturnsValidActionWithRoleName() {

		assertEquals("Should have valid rolename", "admin", wsAction.getRolename());
	}
	
	@Test
	public void testThatWebServiceActionRunsOnAdministrativeDataEntry() {
		
		assertTrue("This rule should run on Administrative data entry", wsAction.getRuleActionRun().getAdministrativeDataEntry());
	}

	@Test
	public void testThatWebServiceActionRunsOnInitialDataEntry() {
		
		assertTrue("This rule should run on Initial data entry", wsAction.getRuleActionRun().getInitialDataEntry());
	}
	
	@Test
	public void testThatWebServiceActionRunsOnDoubleDataEntry() {
		
		assertTrue("This rule should run on Double data entry", wsAction.getRuleActionRun().getDoubleDataEntry());
	}
	
	@Test
	public void testThatWebServiceActionRunsOnBatchDataEntry() {
		
		assertTrue("This rule should run on Batch data entry", wsAction.getRuleActionRun().getBatch());
	}
	
	@Test
	public void testThatWebServiceActionDoesNotRunOnImportDataEntry() {
		
		assertFalse("This rule should not run on Import data entry", wsAction.getRuleActionRun().getImportDataEntry());
	}
	
	private RuleSetRuleBean createRuleSetRuleBean() {
		
		RuleBean rule = new RuleBean();
		rule.setId(0);
		
		WebServiceAction action = createWebServiceAction();
		
		
		RuleSetRuleBean ruleSetRule = new RuleSetRuleBean();
		ruleSetRule.setRuleBean(rule);
		ruleSetRule.addAction(action);
		
		return ruleSetRule;
	}
}
