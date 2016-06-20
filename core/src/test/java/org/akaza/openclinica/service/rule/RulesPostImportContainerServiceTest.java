/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
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

package org.akaza.openclinica.service.rule;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.rule.XmlSchemaValidationHelper;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.domain.rule.action.DiscrepancyNoteActionBean;
import org.akaza.openclinica.domain.rule.expression.Context;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.XMLContext;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

public class RulesPostImportContainerServiceTest extends DefaultAppContextTest {

	private RulesPostImportContainer parseRuleFile(String ruleFileName) throws Exception {
		RulesPostImportContainer ruleImport;
		File ruleFile = new DefaultResourceLoader().getResource(ruleFileName).getFile();
		InputStream xsdFile = coreResources.getInputStream("rules.xsd");
		XmlSchemaValidationHelper schemaValidator = new XmlSchemaValidationHelper();
		schemaValidator.validateAgainstSchema(ruleFile, xsdFile);
		XMLContext xmlContext = new XMLContext();
		Mapping mapping = xmlContext.createMapping();
		mapping.loadMapping(coreResources.getURL("mapping.xml"));
		xmlContext.addMapping(mapping);
		org.exolab.castor.xml.Unmarshaller unmarshaller = xmlContext.createUnmarshaller();
		unmarshaller.setWhitespacePreserve(false);
		unmarshaller.setClass(RulesPostImportContainer.class);
		Reader reader = new InputStreamReader(new FileInputStream(ruleFile), "UTF-8");
		ruleImport = (RulesPostImportContainer) unmarshaller.unmarshal(reader);
		ruleImport.initializeRuleDef();
		return ruleImport;
	}

	@Test
	public void testThatItIsImpossibleToUserStaticValuesInValueExpressionTag() throws Exception {
		StudyBean study = (StudyBean) studyDAO.findByPK(1);
		postImportContainerService.setCurrentStudy(study);
		RulesPostImportContainer importedRules = parseRuleFile("rules/InsertRuleWithWrongValue.xml");
		importedRules = postImportContainerService.validateRuleDefs(importedRules);
		importedRules = postImportContainerService.validateRuleSetDefs(importedRules);
		assertEquals(importedRules.getInValidRuleSetDefs().size(), 1);
		org.akaza.openclinica.domain.rule.AuditableBeanWrapper<RuleSetBean> auditableBeanWrapper = importedRules
				.getInValidRuleSetDefs().get(0);
		assertEquals(auditableBeanWrapper.getImportErrors().size(), 1);
		String message = auditableBeanWrapper.getImportErrors().get(0);
		assertEquals(message, "InsertAction is not valid: Value provided for ValueExpression is Invalid");
	}

	@Test
	public void testThatItIsPossibleToUseStaticValuesInTheDestinationPropertyTag() throws Exception {
		StudyBean study = (StudyBean) studyDAO.findByPK(1);
		postImportContainerService.setCurrentStudy(study);
		RulesPostImportContainer importedRules = parseRuleFile("rules/InsertRuleWithCorrectValue.xml");
		importedRules = postImportContainerService.validateRuleDefs(importedRules);
		importedRules = postImportContainerService.validateRuleSetDefs(importedRules);
		assertEquals(importedRules.getInValidRuleSetDefs().size(), 0);
		assertEquals(importedRules.getInValidRuleDefs().size(), 0);
		assertEquals(importedRules.getInValidRules().size(), 0);
		assertEquals(importedRules.getValidRuleSetExpressionValues().size(), 1);
		assertEquals(importedRules.getValidRuleSetDefs().size(), 1);
		assertEquals(importedRules.getValidRuleDefs().size(), 1);
		assertEquals(importedRules.getValidRules().size(), 1);
	}

	@Test
	public void testThatGetDuplicationRuleSetDefsReturnsZeroOnNoDuplicates() {
		StudyBean study = (StudyBean) studyDAO.findByPK(1);
		postImportContainerService.setCurrentStudy(study);

		RulesPostImportContainer container = prepareContainer();

		container = postImportContainerService.validateRuleDefs(container);

		assertEquals(0, container.getDuplicateRuleDefs().size());

	}

	@Test
	public void testThatGetDuplicationRuleSetDefsReturnsZeroOnNoInvalidRuleDefs() {
		StudyBean study = (StudyBean) studyDAO.findByPK(1);
		postImportContainerService.setCurrentStudy(study);

		RulesPostImportContainer container = prepareContainer();

		container = postImportContainerService.validateRuleDefs(container);

		assertEquals(0, container.getInValidRuleDefs().size());

	}

	public void testThatGetDuplicationRuleSetDefsReturnsCorrectDuplicateSize() {
		StudyBean study = (StudyBean) studyDAO.findByPK(1);
		postImportContainerService.setCurrentStudy(study);

		RulesPostImportContainer container = prepareContainer();

		container = postImportContainerService.validateRuleDefs(container);

		assertEquals(1, container.getValidRuleDefs().size());

	}

	private RulesPostImportContainer prepareContainer() {

		RulesPostImportContainer container = new RulesPostImportContainer();
		ArrayList<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
		ArrayList<RuleBean> ruleDefs = new ArrayList<RuleBean>();

		RuleBean rule = createRuleBean();
		RuleSetBean ruleSet = getRuleSet(rule.getOid());
		ruleSets.add(ruleSet);
		ruleDefs.add(rule);
		container.setRuleSets(ruleSets);
		container.setRuleDefs(ruleDefs);
		return container;

	}

	private RuleSetBean getRuleSet(String ruleOid) {

		RuleSetBean ruleSet = new RuleSetBean();
		ruleSet.setTarget(createExpression(Context.OC_RULES_V1,
				"SE_ED2REPEA.F_CONC_V20.IG_CONC_CONCOMITANTMEDICATIONS.I_CONC_CON_MED_NAME"));
		RuleSetRuleBean ruleSetRule = createRuleSetRule(ruleSet, ruleOid);
		ruleSet.addRuleSetRule(ruleSetRule);
		return ruleSet;

	}

	private RuleSetRuleBean createRuleSetRule(RuleSetBean ruleSet, String ruleOid) {

		RuleSetRuleBean ruleSetRule = new RuleSetRuleBean();
		DiscrepancyNoteActionBean ruleAction = new DiscrepancyNoteActionBean();
		ruleAction.setMessage("HELLO WORLD");
		ruleAction.setExpressionEvaluatesTo(true);
		ruleSetRule.addAction(ruleAction);
		ruleSetRule.setRuleSetBean(ruleSet);
		ruleSetRule.setOid(ruleOid);

		return ruleSetRule;
	}

	private RuleBean createRuleBean() {

		RuleBean ruleBean = new RuleBean();
		ruleBean.setName("TEST");
		ruleBean.setOid("BOY");
		ruleBean.setDescription("Yellow");
		ruleBean.setExpression(createExpression(Context.OC_RULES_V1,
				"SE_ED1NONRE.F_AGEN.IG_AGEN_UNGROUPED[1].I_AGEN_PERIODSTART eq \"07/01/2008\" and I_CONC_CON_MED_NAME eq \"Tylenol\""));
		return ruleBean;
	}

	private ExpressionBean createExpression(Context context, String value) {

		ExpressionBean expression = new ExpressionBean();
		expression.setContext(context);
		expression.setValue(value);
		return expression;
	}
}
