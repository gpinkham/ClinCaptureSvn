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

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.ImportDataRuleRunnerContainer;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RuleSetServiceTest extends DefaultAppContextTest {

	protected boolean runRulesOptimisation = true;
	protected Set<Integer> skippedItemsIds;
	protected ODMContainer container;
	protected StudyBean studyBean;
	protected InputStream stream;
	protected UserAccountBean ub;

	{
		skippedItemsIds = new HashSet<Integer>();
		skippedItemsIds.add(1);

		studyBean = new StudyBean();
		studyBean.setId(1);
		studyBean.setName("Default Study");
		studyBean.setOid("S_DEFAULTS1");

		ub = new UserAccountBean();
		ub.setId(1);
		ub.setName("root");
	}

	@Before
	public void setUp() throws Exception {
		parseFile("import4.xml");
	}

	private void parseFile(String fileName) throws Exception {
		container = new ODMContainer();
		stream = this.getClass().getClassLoader().getResourceAsStream("com/clinovo/" + fileName);
		JAXBContext jaxbContext = JAXBContext.newInstance(ODMContainer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		if (stream != null) {
			InputSource inputSource = new InputSource(stream);
			SAXSource saxSource = new SAXSource(inputSource);
			try {
				container = (ODMContainer) jaxbUnmarshaller.unmarshal(saxSource);
			} catch (Exception e) {
				fail("Unmarshaller exception: " + e.getMessage());
			}
		} else {
			fail("XML not found!");
		}
	}

	private List<RuleSetBean> getRuleSetsByCrfStudyAndStudyEventDefinition() {

		StudyDAO studyDao = new StudyDAO(getDataSource());
		StudyBean study = (StudyBean) studyDao.findByPK(1);

		StudyEventDefinitionDAO studyEventDefinitionDao = new StudyEventDefinitionDAO(getDataSource());
		StudyEventDefinitionBean studyEventDefinition = (StudyEventDefinitionBean) studyEventDefinitionDao.findByPK(2);

		CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
		CRFVersionBean crfVersion = (CRFVersionBean) crfVersionDao.findByPK(2);

		return ruleSetService.getRuleSetsByCrfStudyAndStudyEventDefinition(study, studyEventDefinition, crfVersion);
	}

	@Test
	public void testGetRuleSetsByCrfStudyAndStudyEventDefinition() {

		List<RuleSetBean> ruleSets = getRuleSetsByCrfStudyAndStudyEventDefinition();
		assertEquals("RuleSet size should be 4", 4, ruleSets.size());
		assertNotNull(ruleSets.get(0).getRuleSetRules());
	}

	@Test
	public void testFilterByStatusEqualsAvailable() {

		List<RuleSetBean> ruleSets = getRuleSetsByCrfStudyAndStudyEventDefinition();
		assertEquals("RuleSet size should be 4", 4, ruleSets.size());

		ruleSets = ruleSetService.filterByStatusEqualsAvailable(ruleSets);
		assertEquals("RuleSet size should be 3", 3, ruleSets.size());
	}

	@Test
	public void testImportRunRules() throws Exception {
		discrepancyNoteDAO.unsetTypeExpected();
		discrepancyNoteDAO.setTypeExpected(1, TypeNames.INT);
		int total = discrepancyNoteDAO.findAll().size();
		List<ImportDataRuleRunnerContainer> containers = new ArrayList<ImportDataRuleRunnerContainer>();
		ArrayList<SubjectDataBean> subjectDataBeans = container.getCrfDataPostImportContainer().getSubjectData();
		if (ruleSetService.getCountByStudy(studyBean) > 0) {
			ImportDataRuleRunnerContainer idrrContainer = new ImportDataRuleRunnerContainer();
			for (SubjectDataBean subjectDataBean : subjectDataBeans) {
				idrrContainer.initRuleSetsAndTargets(dataSource, studyBean, subjectDataBean, ruleSetService);
				if (idrrContainer.getShouldRunRules()) {
					containers.add(idrrContainer);
				}
			}
			if (!containers.isEmpty()) {
				ruleSetService.runRulesInImportData(runRulesOptimisation, getDataSource().getConnection(), containers,
						studyBean, ub, ExecutionMode.DRY_RUN);
			}
		}
		ruleSetService.runRulesInImportData(runRulesOptimisation, getDataSource().getConnection(), containers,
				new HashSet<Integer>(), studyBean, ub, ExecutionMode.SAVE);
		assertTrue(discrepancyNoteDAO.findAll().size() > total);
	}

	@Test
	public void testImportRunRulesWithSkippedItems() throws Exception {
		int total = discrepancyNoteDAO.findAll().size();
		List<ImportDataRuleRunnerContainer> containers = new ArrayList<ImportDataRuleRunnerContainer>();
		ArrayList<SubjectDataBean> subjectDataBeans = container.getCrfDataPostImportContainer().getSubjectData();
		if (ruleSetService.getCountByStudy(studyBean) > 0) {
			ImportDataRuleRunnerContainer idrrContainer = new ImportDataRuleRunnerContainer();
			for (SubjectDataBean subjectDataBean : subjectDataBeans) {
				idrrContainer.initRuleSetsAndTargets(dataSource, studyBean, subjectDataBean, ruleSetService);
				if (idrrContainer.getShouldRunRules()) {
					containers.add(idrrContainer);
				}
			}
			if (!containers.isEmpty()) {
				ruleSetService.runRulesInImportData(runRulesOptimisation, getDataSource().getConnection(), containers,
						studyBean, ub, ExecutionMode.DRY_RUN);
			}
		}
		ruleSetService.runRulesInImportData(runRulesOptimisation, getDataSource().getConnection(), containers,
				skippedItemsIds, studyBean, ub, ExecutionMode.SAVE);
		assertEquals(discrepancyNoteDAO.findAll().size(), total);
	}
}
