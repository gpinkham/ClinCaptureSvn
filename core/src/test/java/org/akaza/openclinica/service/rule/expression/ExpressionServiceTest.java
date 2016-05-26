/*******************************************************************************
 * Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.service.rule.expression;

import junit.framework.TestCase;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionObjectWrapper;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Contains unit tests for org.akaza.openclinica.service.rule.expression.ExpressionService class.
 * 
 * @author Frank
 * 
 */
public class ExpressionServiceTest extends DefaultAppContextTest {

	private ExpressionService expressionService;
	private RuleSetBean ruleSet;
	private ExpressionService spyExpressionService;
	private ItemGroupDAO mockedItemGroupDAO;

	public static final String TEST_E_OID = "STUDY_EVENT_OID";
	public static final String TEST_F_OID = "FORM_OID";
	public static final String TEST_IG_OID = "ITEM_GROUP_OID";
	public static final String TEST_I_OID = "ITEM_OID_1";
	public static final String TEST_EXPRESSION = TEST_E_OID + "." + TEST_F_OID + "." + TEST_IG_OID  + "." + TEST_I_OID;

	public static final int ID_1 = 1;
	public static final int ID_2 = 2;

	/**
	 * Sets up objects to be used in tests.
	 */
	@Before
	public void setUp() {
		BasicDataSource ds = new BasicDataSource();
		expressionService = new ExpressionService(ds);
		spyExpressionService = Mockito.spy(new ExpressionService(ds));
		ruleSet = new RuleSetBean();

		ExpressionBean ruleTarget = new ExpressionBean();
		ruleTarget.setValue("SE_E2[936].F_CASECOMPLETION.IG_CASEC_UNGROUPED.I_CASEC_RDCSC90DFU");

		ExpressionBean originalTarget = new ExpressionBean();
		originalTarget.setValue("F_CASECOMPLETION.IG_CASEC_UNGROUPED.I_CASEC_RDCSC90DFU");
		originalTarget.setTargetVersionOid("F_CASECOMPLETION_V1");
		originalTarget.setTargetEventOid("SE_E2");

		ruleSet.setOriginalTarget(originalTarget);
		ruleSet.setTarget(ruleTarget);

		StudyBean studyBean = new StudyBean();
		studyBean.setId(ID_1);
		StudyEventDefinitionBean sedBean = new StudyEventDefinitionBean();
		sedBean.setStudyId(ID_2);
		CRFBean crfBean = new CRFBean();
		crfBean.setId(ID_1);
		ItemGroupBean itemGroupBean = new ItemGroupBean();
		itemGroupBean.setCrfId(crfBean.getId());

		ExpressionBean expressionBean = new ExpressionBean();
		ExpressionObjectWrapper wrapper = new ExpressionObjectWrapper(dataSource, studyBean, expressionBean);
		spyExpressionService.setExpressionWrapper(wrapper);

		mockedItemGroupDAO = Mockito.mock(ItemGroupDAO.class);

		Mockito.doReturn(new ItemBean()).when(spyExpressionService).getItemFromExpression(Mockito.anyString());
		Mockito.doReturn(TEST_IG_OID).when(spyExpressionService).getItemGroupOidFromExpression(Mockito.anyString());
		Mockito.doReturn(mockedItemGroupDAO).when(spyExpressionService).getItemGroupDao();
		Mockito.doReturn(itemGroupBean).when(mockedItemGroupDAO).findByOid(Mockito.anyString());
		Mockito.doReturn(crfBean).when(spyExpressionService).getCRFFromExpression(Mockito.anyString());
		Mockito.doReturn(sedBean).when(spyExpressionService).getStudyEventDefinitionFromExpression(Mockito.anyString());
	}

	/**
	 * Tests that check syntax works fine.
	 */
	@Test
	public void testThatCheckSyntaxWorksFine() {
		// Syntax
		assertEquals(false,
				expressionService.checkSyntax("StudyEventName[ALL].FormName1.ItemGroupName[ALL].ItemName11."));
		assertEquals(false,
				expressionService.checkSyntax(".StudyEventName[ALL].FormName1.ItemGroupName[ALL].ItemName11."));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL]..ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].ITEM_GROUP_OID[ALL]..ITEM_OID_11"));

		// STUDY_EVENT_DEFINITION_OID
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[123].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[10].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[1].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[10004].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID_12[123].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY12_EVENT_OID_12[123].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID.FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[.FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[Krikor].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[KK].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVE[NT_OID[].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("[].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("[.FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID$[12].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[0].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[123].FORM_OID.ITEM_GROUP_OID[0].ITEM_OID_11"));

		// CRF_OID or CRF_VERSION_OID
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID_.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID_123.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM123_2__OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID[.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID[ALL].ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID[].ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID].ITEM_GROUP_OID[ALL].ITEM_OID_11"));

		// ITEM_GROUP_OID
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[23].ITEM_OID_11"));
		assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[2].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[100].ITEM_OID_11"));
		assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID__[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID_123[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.12_ITEM_GROUP_12_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.12_iTEM_GROUP_12_OID[ALL].ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID].ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALLL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL]$.ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL]_KK.ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[0].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[0].ITEM_OID_11"));

		// ITEM_OID
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OI123D_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITE123M_OID_11"));
		assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11["));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11]"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11[]"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11$"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OId_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM-_OID_11"));

	}

	/**
	 * Tests that prepareRuleExpression method doesn not return null.
	 */
	@Test
	public void testThatPrepareRuleExpressionDoesNotReturnNull() {
		String expression = "F_CASECOMPLETION.IG_CASEC_UNGROUPED.I_CASEC_RDCSC90DFU eq SE_E2.F_CONCOMITANTMEDICATION.IG_CONCO_GROUP.I_CONCO_SE023_TXT_DOSE";
		assertNotNull(expressionService.prepareRuleExpression(expression, ruleSet));
	}

	/**
	 * Tests that prepareRuleExpression method returns a list with at least one element.
	 */
	@Test
	public void testThatPrepareRuleExpressionReturnsAtleastOne() {
		String expression = "F_CASECOMPLETION.IG_CASEC_UNGROUPED.I_CASEC_RDCSC90DFU eq SE_E2.F_CONCOMITANTMEDICATION.IG_CONCO_GROUP.I_CONCO_SE023_TXT_DOSE";
		List<String> expressions = expressionService.prepareRuleExpression(expression, ruleSet);
		boolean atLeastOne = expressions.size() > 0;
		assertTrue(atLeastOne);
	}

	/**
	 * Tests that insertGroupOrdinal method works fine with a grouped item.
	 */
	@Test
	public void testThatInsertGroupOrdinalWorksForGroupedItem() {
		String expression = "SE_E2.F_CONCOMITANTMEDICATION.IG_CONCO_GROUP.I_CONCO_SE023_TXT_DOSE";
		String newExpression = expressionService.insertGroupOrdinal(expression, 2);
		assertEquals("SE_E2.F_CONCOMITANTMEDICATION.IG_CONCO_GROUP[2].I_CONCO_SE023_TXT_DOSE", newExpression);
	}

	/**
	 * Tests that insertGroupOrdinal works fine with an ungrouped item.
	 */
	@Test
	public void testThatInsertGroupOrdinalWorksForUngroupedItem() {
		String expression = "F_CASECOMPLETION.IG_CASEC_UNGROUPED.I_CASEC_RDCSC90DFU";
		String newExpression = expressionService.insertGroupOrdinal(expression, 2);
		assertEquals("F_CASECOMPLETION.IG_CASEC_UNGROUPED.I_CASEC_RDCSC90DFU", newExpression);
	}

	/**
	 * Tests that ifValueIsDate works fine for all locales.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testThatIfValueIsDateMethodWorksFineForAllLocales() throws Exception {
		for (String isoLang : Locale.getISOLanguages()) {
			Locale locale = new Locale(isoLang);
			String systemDate = "2014-10-17";
			ItemBean itemBean = new ItemBean();
			itemBean.setDataType(ItemDataType.DATE);
			ResourceBundleProvider.updateLocale(locale);
			String dateFormatString = ResourceBundleProvider.getFormatBundle().getString("date_format_string");
			String localizedDate = new SimpleDateFormat(dateFormatString, locale).format(new SimpleDateFormat(
					"yyyy-MM-dd").parse(systemDate));
			assertEquals(expressionService.ifValueIsDate(itemBean, localizedDate), systemDate);
		}
	}

	@Test
	public void testThatIsExpressionValidWithOptimiseRuleValidatorThrowsErrorIfItemIsNotFound() {
		Mockito.doReturn(null).when(spyExpressionService).getItemFromExpression(Mockito.anyString());

		String errorCode = "";
		try {
			spyExpressionService.isExpressionValidWithOptimiseRuleValidator(TEST_EXPRESSION, false);
		} catch (OpenClinicaSystemException ex) {
			errorCode = ex.getErrorCode();
		} finally {
			TestCase.assertEquals("OCRERR_0023", errorCode);
		}
	}

	@Test
	public void testThatIsExpressionValidWithOptimiseRuleValidatorThrowsErrorIfItemGroupIsNotFound() {
		Mockito.doReturn(null).when(mockedItemGroupDAO).findByOid(Mockito.anyString());

		String errorCode = "";
		try {
			spyExpressionService.isExpressionValidWithOptimiseRuleValidator(TEST_EXPRESSION, false);
		} catch (OpenClinicaSystemException ex) {
			errorCode = ex.getErrorCode();
		} finally {
			TestCase.assertEquals("OCRERR_0022", errorCode);
		}
	}

	@Test
	public void testThatIsExpressionValidWithOptimiseRuleValidatorThrowsErrorIfCRFIsNotFound() {
		Mockito.doReturn(null).when(spyExpressionService).getCRFFromExpression(Mockito.anyString());

		String errorCode = "";
		try {
			spyExpressionService.isExpressionValidWithOptimiseRuleValidator(TEST_EXPRESSION, false);
		} catch (OpenClinicaSystemException ex) {
			errorCode = ex.getErrorCode();
		} finally {
			TestCase.assertEquals("OCRERR_0033", errorCode);
		}
	}

	@Test
	public void testThatIsExpressionValidWithOptimiseRuleValidatorThrowsErrorIfStudyEventIsNotFound() {
		Mockito.doReturn(null).when(spyExpressionService).getStudyEventDefinitionFromExpression(Mockito.anyString());

		String errorCode = "";
		try {
			spyExpressionService.isExpressionValidWithOptimiseRuleValidator(TEST_EXPRESSION, false);
		} catch (OpenClinicaSystemException ex) {
			errorCode = ex.getErrorCode();
		} finally {
			TestCase.assertEquals("OCRERR_0034", errorCode);
		}
	}

	@Test
	public void testThatIsExpressionValidWithOptimiseRuleValidatorThrowsErrorIfEventIsNotPresentInStudy() {
		String errorCode = "";
		try {
			spyExpressionService.isExpressionValidWithOptimiseRuleValidator(TEST_EXPRESSION, false);
		} catch (OpenClinicaSystemException ex) {
			errorCode = ex.getErrorCode();
		} finally {
			TestCase.assertEquals("CCRERR_0036", errorCode);
		}
	}
}
