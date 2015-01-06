package org.akaza.openclinica.bean.login;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StudyUserRoleBeanTest {

	private StudyUserRoleBean userRole;
	private UserAccountBean currentUser;
	private StudyUserRoleBean currentRole;
	private StudyBean currentStudy;
	private StudyBean currentSite;

	@Before
	public void prepareForTest() {
		userRole = new StudyUserRoleBean();
		currentUser = new UserAccountBean();
		currentRole = new StudyUserRoleBean();
		currentStudy = new StudyBean();
		currentSite = new StudyBean();
		currentStudy.setId(1);
		currentStudy.setStatus(Status.AVAILABLE);
		currentSite.setId(2);
		currentSite.setParentStudyId(1);
		currentSite.setStatus(Status.AVAILABLE);
		currentRole.setStudyId(1);
	}

	@Test
	public void testThatGetRoleCodeReturnsCorrectCodeForSystemAdministrator() {
		userRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		Assert.assertEquals("system_administrator", userRole.getRoleCode());
	}

	@Test
	public void testThatGetRoleCodeReturnsCorrectCodeForStudyAdministrator() {
		userRole.setRole(Role.STUDY_ADMINISTRATOR);
		Assert.assertEquals("study_administrator", userRole.getRoleCode());
	}

	@Test
	public void testThatGetRoleCodeReturnsCorrectCodeForStudyMonitor() {
		userRole.setRole(Role.STUDY_MONITOR);
		Assert.assertEquals("study_monitor", userRole.getRoleCode());
	}

	@Test
	public void testThatGetRoleCodeReturnsCorrectCodeForSiteMonitor() {
		userRole.setRole(Role.SITE_MONITOR);
		Assert.assertEquals("site_monitor", userRole.getRoleCode());
	}

	@Test
	public void testThatGetRoleCodeReturnsCorrectCodeForStudyCoder() {
		userRole.setRole(Role.STUDY_CODER);
		Assert.assertEquals("study_coder", userRole.getRoleCode());
	}

	@Test
	public void testThatGetRoleCodeReturnsCorrectCodeForStudyEvaluator() {
		userRole.setRole(Role.STUDY_EVALUATOR);
		Assert.assertEquals("study_evaluator", userRole.getRoleCode());
	}

	@Test
	public void testThatGetRoleCodeReturnsCorrectCodeForCRC() {
		userRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		Assert.assertEquals("clinical_research_coordinator", userRole.getRoleCode());
	}

	@Test
	public void testThatGetRoleCodeReturnsCorrectCodeForPI() {
		userRole.setRole(Role.INVESTIGATOR);
		Assert.assertEquals("investigator", userRole.getRoleCode());
	}

	@Test
	public void testThatSystemAdministratorGetsCorrectRoleWhenInStudy() {
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentStudy.getId());
		Assert.assertEquals(Role.SYSTEM_ADMINISTRATOR,
				StudyUserRoleBean.determineRoleInCurrentStudy(currentUser, currentStudy));
	}

	@Test
	public void testThatSystemAdministratorGetsCorrectRoleWhenInSite() {
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		Assert.assertEquals(Role.SYSTEM_ADMINISTRATOR,
				StudyUserRoleBean.determineRoleInCurrentStudy(currentUser, currentSite));
	}
}
