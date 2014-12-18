package com.clinovo.rest.service;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserServiceTest.
 */
public class UserServiceTest extends BaseServiceTest {

	@Test
	public void testThatItIsNotPossibleToCreateAUserInStudyThatDoesNotExist() throws Exception {
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_user_".concat(Long.toString(timestamp)))
						.param("firstname", "firstname_".concat(Long.toString(timestamp)))
						.param("lastname", "lastname_".concat(Long.toString(timestamp)))
						.param("email", "test@gmail.com").param("phone", "111111111111").param("company", "clinovo")
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(3456))
						.param("role", Integer.toString(Role.STUDY_MONITOR.getId())).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToCreateAUserWithRoleThatDoesNotExist() throws Exception {
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_user_".concat(Long.toString(timestamp)))
						.param("firstname", "firstname_".concat(Long.toString(timestamp)))
						.param("lastname", "lastname_".concat(Long.toString(timestamp)))
						.param("email", "test@gmail.com").param("phone", "111111111111").param("company", "clinovo")
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(1))
						.param("role", Integer.toString(3456)).secure(true).session(session)).andExpect(
				status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToAssignStudyCoderToSite() throws Exception {
		createNewStudy();
		createNewSite(newStudy.getId());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_user_".concat(Long.toString(timestamp)))
						.param("firstname", "firstname_".concat(Long.toString(timestamp)))
						.param("lastname", "lastname_".concat(Long.toString(timestamp)))
						.param("email", "test@gmail.com").param("phone", "111111111111").param("company", "clinovo")
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(newSite.getId()))
						.param("role", Integer.toString(Role.STUDY_CODER.getId())).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToAssignStudyEvaluatorToSite() throws Exception {
		createNewStudy();
		createNewSite(newStudy.getId());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_user_".concat(Long.toString(timestamp)))
						.param("firstname", "firstname_".concat(Long.toString(timestamp)))
						.param("lastname", "lastname_".concat(Long.toString(timestamp)))
						.param("email", "test@gmail.com").param("phone", "111111111111").param("company", "clinovo")
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(newSite.getId()))
						.param("role", Integer.toString(Role.STUDY_EVALUATOR.getId())).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToAssignStudyMonitorToSite() throws Exception {
		createNewStudy();
		createNewSite(newStudy.getId());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_user_".concat(Long.toString(timestamp)))
						.param("firstname", "firstname_".concat(Long.toString(timestamp)))
						.param("lastname", "lastname_".concat(Long.toString(timestamp)))
						.param("email", "test@gmail.com").param("phone", "111111111111").param("company", "clinovo")
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(newSite.getId()))
						.param("role", Integer.toString(Role.STUDY_MONITOR.getId())).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToAssignStudyDirectorToSite() throws Exception {
		createNewStudy();
		createNewSite(newStudy.getId());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_user_".concat(Long.toString(timestamp)))
						.param("firstname", "firstname_".concat(Long.toString(timestamp)))
						.param("lastname", "lastname_".concat(Long.toString(timestamp)))
						.param("email", "test@gmail.com").param("phone", "111111111111").param("company", "clinovo")
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(newSite.getId()))
						.param("role", Integer.toString(Role.STUDY_DIRECTOR.getId())).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToAssignStudyAdminToSite() throws Exception {
		createNewStudy();
		createNewSite(newStudy.getId());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_user_".concat(Long.toString(timestamp)))
						.param("firstname", "firstname_".concat(Long.toString(timestamp)))
						.param("lastname", "lastname_".concat(Long.toString(timestamp)))
						.param("email", "test@gmail.com").param("phone", "111111111111").param("company", "clinovo")
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(newSite.getId()))
						.param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId())).secure(true)
						.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToAssignSiteMonitorToStudy() throws Exception {
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_user_".concat(Long.toString(timestamp)))
						.param("firstname", "firstname_".concat(Long.toString(timestamp)))
						.param("lastname", "lastname_".concat(Long.toString(timestamp)))
						.param("email", "test@gmail.com").param("phone", "111111111111").param("company", "clinovo")
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(1))
						.param("role", Integer.toString(Role.SITE_MONITOR.getId())).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToAssignCRCToStudy() throws Exception {
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_user_".concat(Long.toString(timestamp)))
						.param("firstname", "firstname_".concat(Long.toString(timestamp)))
						.param("lastname", "lastname_".concat(Long.toString(timestamp)))
						.param("email", "test@gmail.com").param("phone", "111111111111").param("company", "clinovo")
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(1))
						.param("role", Integer.toString(Role.CLINICAL_RESEARCH_COORDINATOR.getId())).secure(true)
						.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToAssignInvestigatorToStudy() throws Exception {
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_user_".concat(Long.toString(timestamp)))
						.param("firstname", "firstname_".concat(Long.toString(timestamp)))
						.param("lastname", "lastname_".concat(Long.toString(timestamp)))
						.param("email", "test@gmail.com").param("phone", "111111111111").param("company", "clinovo")
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(1))
						.param("role", Integer.toString(Role.INVESTIGATOR.getId())).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreatedInvestigatorWithoutAdministrativePrivilegesIsNotAbleToCreateAUser() throws Exception {
		createNewStudy();
		createNewSite(newStudy.getId());
		createNewUser(newSite.getId(), UserType.USER, Role.INVESTIGATOR);
		login(newUser.getName(), UserType.USER, Role.INVESTIGATOR, newUser.getPasswd(), newSite.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_".concat(newUser.getName()))
						.param("firstname", newUser.getFirstName()).param("lastname", newUser.getLastName())
						.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
						.param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(newSite.getId()))
						.param("role", Integer.toString(Role.INVESTIGATOR.getId())).secure(true).session(session))
				.andExpect(status().isForbidden());
	}

	@Test
	public void testThatCreatedInvestigatorWithAdministrativePrivilegesIsAbleToLogin() throws Exception {
		createNewStudy();
		createNewSite(newStudy.getId());
		createNewUser(newSite.getId(), UserType.SYSADMIN, Role.INVESTIGATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.INVESTIGATOR, newUser.getPasswd(), newSite.getName());
	}

	@Test
	public void testThatCreatedCrcWithoutAdministrativePrivilegesIsNotAbleToCreateAUser() throws Exception {
		createNewStudy();
		createNewSite(newStudy.getId());
		createNewUser(newSite.getId(), UserType.USER, Role.CLINICAL_RESEARCH_COORDINATOR);
		login(newUser.getName(), UserType.USER, Role.CLINICAL_RESEARCH_COORDINATOR, newUser.getPasswd(),
				newSite.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_".concat(newUser.getName()))
						.param("firstname", newUser.getFirstName()).param("lastname", newUser.getLastName())
						.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
						.param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(newSite.getId()))
						.param("role", Integer.toString(Role.CLINICAL_RESEARCH_COORDINATOR.getId())).secure(true)
						.session(session)).andExpect(status().isForbidden());
	}

	@Test
	public void testThatCreatedCrcWithAdministrativePrivilegesIsAbleToLogin() throws Exception {
		createNewStudy();
		createNewSite(newStudy.getId());
		createNewUser(newSite.getId(), UserType.SYSADMIN, Role.CLINICAL_RESEARCH_COORDINATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.CLINICAL_RESEARCH_COORDINATOR, newUser.getPasswd(),
				newSite.getName());
	}

	@Test
	public void testThatCreatedStudyEvaluatorWithoutAdministrativePrivilegesIsNotAbleToCreateAUser() throws Exception {
		createNewStudy();
		createNewUser(newStudy.getId(), UserType.USER, Role.STUDY_EVALUATOR);
		login(newUser.getName(), UserType.USER, Role.STUDY_EVALUATOR, newUser.getPasswd(), newStudy.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_".concat(newUser.getName()))
						.param("firstname", newUser.getFirstName()).param("lastname", newUser.getLastName())
						.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
						.param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(studyBean.getId()))
						.param("role", Integer.toString(Role.STUDY_EVALUATOR.getId())).secure(true).session(session))
				.andExpect(status().isForbidden());
	}

	@Test
	public void testThatCreatedStudyEvaluatorWithAdministrativePrivilegesIsAbleToLogin() throws Exception {
		createNewStudy();
		createNewUser(newStudy.getId(), UserType.SYSADMIN, Role.STUDY_EVALUATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_EVALUATOR, newUser.getPasswd(), newStudy.getName());
	}

	@Test
	public void testThatCreatedStudyCoderWithoutAdministrativePrivilegesIsNotAbleToCreateAUser() throws Exception {
		createNewStudy();
		createNewUser(newStudy.getId(), UserType.USER, Role.STUDY_CODER);
		login(newUser.getName(), UserType.USER, Role.STUDY_CODER, newUser.getPasswd(), newStudy.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_".concat(newUser.getName()))
						.param("firstname", newUser.getFirstName()).param("lastname", newUser.getLastName())
						.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
						.param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(studyBean.getId()))
						.param("role", Integer.toString(Role.STUDY_CODER.getId())).secure(true).session(session))
				.andExpect(status().isForbidden());
	}

	@Test
	public void testThatCreatedStudyCoderWithAdministrativePrivilegesIsAbleToLogin() throws Exception {
		createNewStudy();
		createNewUser(newStudy.getId(), UserType.SYSADMIN, Role.STUDY_CODER);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_CODER, newUser.getPasswd(), newStudy.getName());
	}

	@Test
	public void testThatCreatedStudyMonitorWithoutAdministrativePrivilegesIsNotAbleToCreateAUser() throws Exception {
		createNewStudy();
		createNewUser(newStudy.getId(), UserType.USER, Role.STUDY_MONITOR);
		login(newUser.getName(), UserType.USER, Role.STUDY_MONITOR, newUser.getPasswd(), newStudy.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_".concat(newUser.getName()))
						.param("firstname", newUser.getFirstName()).param("lastname", newUser.getLastName())
						.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
						.param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(studyBean.getId()))
						.param("role", Integer.toString(Role.STUDY_MONITOR.getId())).secure(true).session(session))
				.andExpect(status().isForbidden());
	}

	@Test
	public void testThatCreatedStudyMonitorWithAdministrativePrivilegesIsAbleToLogin() throws Exception {
		createNewStudy();
		createNewUser(newStudy.getId(), UserType.SYSADMIN, Role.STUDY_MONITOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_MONITOR, newUser.getPasswd(), newStudy.getName());
	}

	@Test
	public void testThatCreatedStudyAdminWithoutAdministrativePrivilegesIsNotAbleToCreateAUser() throws Exception {
		createNewStudy();
		createNewUser(newStudy.getId(), UserType.USER, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.USER, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_".concat(newUser.getName()))
						.param("firstname", newUser.getFirstName()).param("lastname", newUser.getLastName())
						.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
						.param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(studyBean.getId()))
						.param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId())).secure(true)
						.session(session)).andExpect(status().isForbidden());
	}

	@Test
	public void testThatCreatedStudyAdminWithAdministrativePrivilegesIsAbleToLogin() throws Exception {
		createNewStudy();
		createNewUser(newStudy.getId(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
	}

	@Test
	public void testThatCreateUserRequestReturnsCode500IfUsernameHasBeenTakenAlready() throws Exception {
		createNewStudy();
		createNewUser(newStudy.getId(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", newUser.getName())
						.param("firstname", newUser.getFirstName()).param("lastname", newUser.getLastName())
						.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
						.param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.SYSADMIN.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(newStudy.getId()))
						.param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId())).secure(true)
						.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateUserRequestReturnsCode500IfAuthorisedUserIsNotAssignedToStudyThatWasPassedToCreateUserMethod()
			throws Exception {
		createNewStudy();
		createNewUser(newStudy.getId(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE_USER).param("username", "new_".concat(newUser.getName()))
						.param("firstname", newUser.getFirstName()).param("lastname", newUser.getLastName())
						.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
						.param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.SYSADMIN.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("scope", Integer.toString(studyBean.getId()))
						.param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId())).secure(true)
						.session(session)).andExpect(status().isInternalServerError());
	}
}
