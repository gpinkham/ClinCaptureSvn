package org.akaza.openclinica.web.table.filter;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.junit.Before;
import org.junit.Test;

public class StudyEventTableRowFilterTest extends DefaultAppContextTest {

	private StudyEventTableRowFilter rowFilter;
	private UserAccountBean user;
	private UserAccountBean evaluator;
	private StudyUserRoleBean evaluatorRole;
	private StudyBean study;

	@Before
	public void setUp() throws Exception {
		user = (UserAccountBean) userAccountDAO.findByPK(1);
		study = (StudyBean) studyDAO.findByPK(1);
		evaluator = new UserAccountBean();
		evaluator.setId(2);
		evaluatorRole = new StudyUserRoleBean();
		evaluatorRole.setRole(Role.STUDY_EVALUATOR);
		evaluatorRole.setStudyId(study.getId());
		evaluator.addRole(evaluatorRole);
	}

	@Test
	public void testThatRootGetsListOfAllAvailableStudyEventDefinitions() {
		rowFilter = new StudyEventTableRowFilter(dataSource, study, user);
		assertEquals(6, rowFilter.getOptions().size());
	}

	@Test
	public void testThatEvaluatorGetsListOfOnlyAvailableStudyEventDefinitionsWithCrfsForEvaluation() {
		rowFilter = new StudyEventTableRowFilter(dataSource, study, evaluator);
		assertEquals(2, rowFilter.getOptions().size());
	}
}

