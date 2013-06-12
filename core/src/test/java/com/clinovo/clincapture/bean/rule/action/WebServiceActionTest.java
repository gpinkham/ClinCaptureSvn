package com.clinovo.clincapture.bean.rule.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
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
	public void testThatCreateWebServiceActionDoesNotReturnNull() {

		assertNotNull("Should never return null", wsAction);
	}

	@Test
	public void testThatCreateWebServiceActionReturnsValidActionWithSiteId() {

		assertEquals("Should have valid site id", "clinovotest", wsAction.getSiteId());
	}

	@Test
	public void testThatCreateWebServiceActionReturnsValidActionWithStudyUsername() {

		assertEquals("Should have valid username", "clinovo", wsAction.getUsername());
	}

	@Test
	public void testThatCreateWebServiceActionReturnsValidActionWithPassword() {

		assertEquals("Should have valid password", "clinovo", wsAction.getPassword());
	}

	@Test
	public void testThatCreateWebServiceActionReturnsValidActionWithAuthenticationUrl() {

		assertEquals("Should have valid url", "https://www.randomize.net/api/RandomizeAPIService/Authenticate", wsAction.getAuthenticationUrl());
	}
	
	@Test
	public void testThatWebServiceActionHasRandomizationUrl() {
		
		assertEquals("Should have a randomization url", "https://www.randomize.net/api//RandomizeAPIService/RandomizePatientDelegated", wsAction.getRandomizationUrl());
	}

	@Test
	public void testThatCreateWebServiceActionReturnsValidActionWithPatientId() {

		assertEquals("Should have valid patient id", "SE_TEST_SS_NUM", wsAction.getPatientId());
	}

	@Test
	public void testThatCreateWebServiceActionReturnsValidActionWithTrialId() {

		assertEquals("Should have valid Trial Id", "SE_TEST", wsAction.getTrialId());
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
