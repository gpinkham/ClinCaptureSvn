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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;

import java.util.List;

@SuppressWarnings("rawtypes")
public class RuleSetServiceTest extends HibernateOcDbTestCase {

	public RuleSetServiceTest() {
		super();
	}

	public void testGetRuleSetsByCrfStudyAndStudyEventDefinition() {
		List<RuleSetBean> ruleSets = getRuleSetsByCrfStudyAndStudyEventDefinition();
		assertEquals("RuleSet size should be 4", 4, ruleSets.size());
		assertNotNull(ruleSets.get(0).getRuleSetRules());
	}

	public void testFilterByStatusEqualsAvailable() {
		List<RuleSetBean> ruleSets = getRuleSetsByCrfStudyAndStudyEventDefinition();
		assertEquals("RuleSet size should be 4", 4, ruleSets.size());

		RuleSetServiceInterface instance = (RuleSetServiceInterface) getContext().getBean("ruleSetService");
		ruleSets = instance.filterByStatusEqualsAvailable(ruleSets);
		assertEquals("RuleSet size should be 3", 3, ruleSets.size());
	}

	private List<RuleSetBean> getRuleSetsByCrfStudyAndStudyEventDefinition() {
		StudyDAO studyDao = new StudyDAO(getDataSource());
		StudyBean study = (StudyBean) studyDao.findByPK(1);
		assertNotNull(study);

		StudyEventDefinitionDAO studyEventDefinitionDao = new StudyEventDefinitionDAO(getDataSource());
		StudyEventDefinitionBean studyEventDefinition = (StudyEventDefinitionBean) studyEventDefinitionDao.findByPK(2);
		assertNotNull(studyEventDefinition);

		CRFVersionDAO crfVersionDao = new CRFVersionDAO(getDataSource());
		CRFVersionBean crfVersion = (CRFVersionBean) crfVersionDao.findByPK(2);
		assertNotNull(crfVersion);

		RuleSetServiceInterface instance = (RuleSetServiceInterface) getContext().getBean("ruleSetService");
		List<RuleSetBean> ruleSets = instance.getRuleSetsByCrfStudyAndStudyEventDefinition(study, studyEventDefinition,
				crfVersion);
		return ruleSets;

	}

}
