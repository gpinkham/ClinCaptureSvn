package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

public class UserServiceTest extends BaseServiceTest {

	@Test
	public void testThatItIsImpossibleToCreateAStudyUserIfRoleIsSiteRole() throws Exception {
		mockMvc.perform(post(API_USER_CREATE_USER).param("userName", "new_user_".concat(Long.toString(timestamp)))
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("userType", Integer.toString(UserType.USER.getId())).param("allowSoap", "true")
				.param("displayPassword", "true").param("role", Integer.toString(Role.INVESTIGATOR.getId()))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateAStudyUserIfUserTypeParameterHasWrongValue() throws Exception {
		mockMvc.perform(post(API_USER_CREATE_USER).param("userName", "new_user_".concat(Long.toString(timestamp)))
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo").param("userType", "123")
				.param("allowSoap", "true").param("displayPassword", "true").param("role", Integer.toString(2))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateAStudyUserIfRoleParameterHasWrongValue() throws Exception {
		mockMvc.perform(post(API_USER_CREATE_USER).param("userName", "new_user_".concat(Long.toString(timestamp)))
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("userType", Integer.toString(UserType.USER.getId())).param("allowSoap", "true")
				.param("displayPassword", "true").param("role", "123123").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateUserRequestReturnsCode500IfUsernameHasBeenTakenAlready() throws Exception {
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		mockMvc.perform(post(API_USER_CREATE_USER).param("userName", newUser.getName())
				.param("firstName", newUser.getFirstName()).param("lastName", newUser.getLastName())
				.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
				.param("company", newUser.getInstitutionalAffiliation())
				.param("userType", Integer.toString(UserType.SYSADMIN.getId())).param("allowSoap", "true")
				.param("displayPassword", "true").param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId()))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToCreateAStudyUserWithIncorrectEmail() throws Exception {
		mockMvc.perform(post(API_USER_CREATE_USER).param("userName", "new_user_".concat(Long.toString(timestamp)))
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "testgmailcom")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("userType", Integer.toString(UserType.USER.getId())).param("allowSoap", "true")
				.param("displayPassword", "true").param("role", Integer.toString(Role.STUDY_MONITOR.getId()))
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToCreateAStudyUserIfAllParametersAreSpecified() throws Exception {
		String timeZone = "Etc/GMT+11";
		mailSenderHost = mailSender.getHost();
		mailSender.setHost("");
		String additionalUserName = "new_user_".concat(Long.toString(timestamp));
		result = mockMvc.perform(post(API_USER_CREATE_USER).param("userName", additionalUserName)
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("allowSoap", "true").param("displayPassword", "true")
				.param("company", "clinovo").param("userType", Integer.toString(UserType.SYSADMIN.getId()))
				.param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId())).param("timeZone", timeZone)
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getUserTypeCode()
					.equals(UserType.SYSADMIN.getCode()));
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getUserTimeZoneId().equals(timeZone));
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getRunWebservices());
			assertFalse(restOdmContainer.getRestData().getUserAccountBean().getPasswd().isEmpty());
			assertFalse(restOdmContainer.getRestData().getUserAccountBean().getUserTypeCode()
					.equals(UserType.USER.getCode()));
		}
	}

	@Test
	public void testThatItIsPossibleToCreateAStudyUserIfOnlyRequiredParametersAreSpecified() throws Exception {
		String additionalUserName = "new_user_".concat(Long.toString(timestamp));
		mailSenderHost = mailSender.getHost();
		mailSender.setHost("");
		result = mockMvc.perform(post(API_USER_CREATE_USER).param("userName", additionalUserName)
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("userType", Integer.toString(UserType.USER.getId()))
				.param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId())).accept(mediaType).secure(true)
				.session(session)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getUserTypeCode()
					.equals(UserType.USER.getCode()));
			assertFalse(restOdmContainer.getRestData().getUserAccountBean().getUserTypeCode()
					.equals(UserType.SYSADMIN.getCode()));
			assertFalse(restOdmContainer.getRestData().getUserAccountBean().getRunWebservices());
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getPasswd().isEmpty());
		}
	}

	@Test
	public void testThatStudyAdministratorWithAdministrativePrivilegesIsAbleToCallUserAPI() throws Exception {
		ResultMatcher expectStatus = status().isOk();
		mailSenderHost = mailSender.getHost();
		mailSender.setHost("");
		createNewStudy();
		createNewSite(newStudy.getId());
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		String additionalUserName = "new_".concat(newUser.getName());
		mockMvc.perform(post(API_USER_CREATE_USER).param("userName", additionalUserName)
				.param("firstName", newUser.getFirstName()).param("lastName", newUser.getLastName())
				.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
				.param("company", newUser.getInstitutionalAffiliation())
				.param("userType", Integer.toString(UserType.USER.getId())).param("allowSoap", "true")
				.param("displayPassword", "true").param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId()))
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
		mockMvc.perform(post(API_USER_REMOVE).param("userName", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
		mockMvc.perform(post(API_USER_RESTORE).param("userName", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatItIsImpossibleToCreateASiteUserIfRoleIsStudyRole() throws Exception {
		createNewSite(currentScope.getId());
		mockMvc.perform(post(API_USER_CREATE_USER).param("siteName", newSite.getName())
				.param("userName", "new_user_".concat(Long.toString(timestamp)))
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("userType", Integer.toString(UserType.USER.getId())).param("allowSoap", "true")
				.param("displayPassword", "true").param("role", Integer.toString(Role.STUDY_CODER.getId()))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateASiteUserIfUserTypeParameterHasWrongValue() throws Exception {
		createNewSite(currentScope.getId());
		mockMvc.perform(post(API_USER_CREATE_USER).param("siteName", newSite.getName())
				.param("userName", "new_user_".concat(Long.toString(timestamp)))
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo").param("userType", "123")
				.param("allowSoap", "true").param("displayPassword", "true")
				.param("role", Integer.toString(Role.INVESTIGATOR.getId())).accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateASiteUserIfRoleParameterHasWrongValue() throws Exception {
		createNewSite(currentScope.getId());
		mockMvc.perform(post(API_USER_CREATE_USER).param("siteName", newSite.getName())
				.param("userName", "new_user_".concat(Long.toString(timestamp)))
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("userType", Integer.toString(UserType.USER.getId())).param("allowSoap", "true")
				.param("displayPassword", "true").param("role", "123123").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToCreateASiteUserWithIncorrectEmail() throws Exception {
		createNewSite(currentScope.getId());
		mockMvc.perform(post(API_USER_CREATE_USER).param("siteName", newSite.getName())
				.param("userName", "new_user_".concat(Long.toString(timestamp)))
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "testgmailcom")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("userType", Integer.toString(UserType.USER.getId())).param("allowSoap", "true")
				.param("displayPassword", "true").param("role", Integer.toString(Role.INVESTIGATOR.getId()))
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToCreateASiteUserIfAllParametersAreSpecified() throws Exception {
		createNewSite(currentScope.getId());
		String timeZone = "Etc/GMT+11";
		mailSenderHost = mailSender.getHost();
		mailSender.setHost("");
		String additionalUserName = "new_user_".concat(Long.toString(timestamp));
		result = mockMvc.perform(post(API_USER_CREATE_USER).param("siteName", newSite.getName())
				.param("userName", additionalUserName).param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("allowSoap", "true").param("displayPassword", "true")
				.param("company", "clinovo").param("timeZone", timeZone)
				.param("userType", Integer.toString(UserType.SYSADMIN.getId()))
				.param("role", Integer.toString(Role.SITE_MONITOR.getId())).accept(mediaType).secure(true)
				.session(session)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertFalse(restOdmContainer.getRestData().getUserAccountBean().getUserTypeCode()
					.equals(UserType.USER.getCode()));
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getUserTypeCode()
					.equals(UserType.SYSADMIN.getCode()));
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getUserTimeZoneId().equals(timeZone));
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getRunWebservices());
			assertFalse(restOdmContainer.getRestData().getUserAccountBean().getPasswd().isEmpty());
		}
	}

	@Test
	public void testThatItIsPossibleToCreateASiteUserIfOnlyRequiredParametersAreSpecified() throws Exception {
		createNewSite(currentScope.getId());
		String additionalUserName = "new_user_".concat(Long.toString(timestamp));
		mailSenderHost = mailSender.getHost();
		mailSender.setHost("");
		result = mockMvc.perform(post(API_USER_CREATE_USER).param("siteName", newSite.getName())
				.param("userName", additionalUserName).param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("userType", Integer.toString(UserType.USER.getId()))
				.param("role", Integer.toString(Role.CLINICAL_RESEARCH_COORDINATOR.getId())).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertFalse(restOdmContainer.getRestData().getUserAccountBean().getRunWebservices());
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getPasswd().isEmpty());
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getUserTypeCode()
					.equals(UserType.USER.getCode()));
			assertFalse(restOdmContainer.getRestData().getUserAccountBean().getUserTypeCode()
					.equals(UserType.SYSADMIN.getCode()));
		}
	}

	@Test
	public void testThatItIsNotPossibleToCreateASiteUserIfSiteDoesNotBelongToCurrentScope() throws Exception {
		createNewStudy();
		createNewSite(newStudy.getId());
		mockMvc.perform(post(API_USER_CREATE_USER).param("siteName", newSite.getName())
				.param("userName", "new_user_".concat(Long.toString(timestamp)))
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("userType", Integer.toString(UserType.USER.getId())).param("allowSoap", "true")
				.param("displayPassword", "true")
				.param("role", Integer.toString(Role.CLINICAL_RESEARCH_COORDINATOR.getId())).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToCreateASiteUserIfSiteDoesNotExist() throws Exception {
		mockMvc.perform(post(API_USER_CREATE_USER).param("siteName", "adsfasdf")
				.param("userName", "new_user_".concat(Long.toString(timestamp)))
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("userType", Integer.toString(UserType.USER.getId())).param("allowSoap", "true")
				.param("displayPassword", "true")
				.param("role", Integer.toString(Role.CLINICAL_RESEARCH_COORDINATOR.getId())).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToCreateASiteUserIfSiteNameParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_USER_CREATE_USER).param("siteName", "")
				.param("userName", "new_user_".concat(Long.toString(timestamp)))
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("userType", Integer.toString(UserType.USER.getId())).param("allowSoap", "true")
				.param("displayPassword", "true")
				.param("role", Integer.toString(Role.CLINICAL_RESEARCH_COORDINATOR.getId())).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToCreateASiteUserIfSiteNameParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_USER_CREATE_USER).param("userName", "new_user_".concat(Long.toString(timestamp)))
				.param("firstName", "firstname_".concat(Long.toString(timestamp)))
				.param("lastName", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("userType", Integer.toString(UserType.USER.getId())).param("allowSoap", "true")
				.param("displayPassword", "true")
				.param("role", Integer.toString(Role.CLINICAL_RESEARCH_COORDINATOR.getId())).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfUserNameParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_USER_REMOVE).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfUserNameParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_USER_REMOVE).param("userName", "").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(
				post(API_USER_REMOVE).param("userName", "test_user").param("xparam", "1").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfUserNameParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_USER_REMOVE).param("userNAme", "test_user").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfYouAreTryingToRemoveNonExistingUser() throws Exception {
		mockMvc.perform(post(API_USER_REMOVE).param("userName", "misterx").secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfYouAreTryingToRemoveRootUser() throws Exception {
		mockMvc.perform(post(API_USER_REMOVE).param("userName", "root").secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfYouAreTryingToRemoveYourself() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), defaultStudyName);
		mockMvc.perform(post(API_USER_REMOVE).param("userName", newUser.getName()).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfYouAreTryingToRemoveUserThatDoesNotBelongToCurrentScope()
			throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		String additionalUserName = newUser.getName();
		String additionalUserPassword = newUser.getPasswd();
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		timestamp = new Date().getTime() + 1;
		createNewStudyUser(UserType.USER, Role.STUDY_MONITOR);
		login(additionalUserName, UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, additionalUserPassword,
				defaultStudyName);
		mockMvc.perform(post(API_USER_REMOVE).param("userName", newUser.getName()).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveUserMethodChangesUserStatusCorrectly() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		UserAccountBean userAccountBean = (UserAccountBean) userAccountDAO.findByUserName(newUser.getName());
		assertEquals(userAccountBean.getStatus(), Status.AVAILABLE);
		mockMvc.perform(post(API_USER_REMOVE).param("userName", newUser.getName()).secure(true).session(session))
				.andExpect(status().isOk());
		userAccountBean = (UserAccountBean) userAccountDAO.findByUserName(newUser.getName());
		assertEquals(userAccountBean.getStatus(), Status.DELETED);
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfUserNameParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_USER_RESTORE).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfUserNameParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_USER_RESTORE).param("userName", "").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(post(API_USER_RESTORE).param("userName", "test_user").param("xparam", "1").secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfUserNameParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_USER_RESTORE).param("userNAme", "test_user").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfYouAreTryingToRestoreNonExistingUser() throws Exception {
		mockMvc.perform(post(API_USER_RESTORE).param("userName", "misterx").secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfYouAreTryingToRestoreRootUser() throws Exception {
		mockMvc.perform(post(API_USER_RESTORE).param("userName", "root").secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfYouAreTryingToRestoreYourself() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), defaultStudyName);
		mockMvc.perform(post(API_USER_RESTORE).param("userName", newUser.getName()).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfYouAreTryingToRestoreUserThatDoesNotBelongToCurrentScope()
			throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		String additionalUserName = newUser.getName();
		String additionalUserPassword = newUser.getPasswd();
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		timestamp = new Date().getTime() + 1;
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		login(additionalUserName, UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, additionalUserPassword,
				defaultStudyName);
		mockMvc.perform(post(API_USER_RESTORE).param("userName", newUser.getName()).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreUserMethodChangesUserStatusCorrectly() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		UserAccountBean userAccountBean = (UserAccountBean) userAccountDAO.findByUserName(newUser.getName());
		userAccountBean.setStatus(Status.DELETED);
		userAccountBean.setUpdater(userAccountBean);
		userAccountDAO.updateStatus(userAccountBean);
		userAccountBean = (UserAccountBean) userAccountDAO.findByUserName(newUser.getName());
		assertEquals(userAccountBean.getStatus(), Status.DELETED);
		mockMvc.perform(post(API_USER_RESTORE).param("userName", newUser.getName()).secure(true).session(session))
				.andExpect(status().isOk());
		userAccountBean = (UserAccountBean) userAccountDAO.findByUserName(newUser.getName());
		assertEquals(userAccountBean.getStatus(), Status.AVAILABLE);
	}

	@Test
	public void testThatGetUserMethodThrowsExceptionIfYouAreTryingToRestoreUserThatDoesNotBelongToCurrentScope()
			throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		String additionalUserName = newUser.getName();
		String additionalUserPassword = newUser.getPasswd();
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		timestamp = new Date().getTime() + 1;
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		login(additionalUserName, UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, additionalUserPassword,
				defaultStudyName);
		mockMvc.perform(get(API_USER).param("userName", newUser.getName()).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRootUserCanGetInfoAboutAnyUser() throws Exception {
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		timestamp = new Date().getTime() + 1;
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, defaultStudyName);
		mockMvc.perform(get(API_USER).param("userName", newUser.getName()).secure(true).session(session))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatGetUserMethodDoesNotSupportHttpPost() throws Exception {
		mockMvc.perform(post(API_USER).param("userName", rootUserName).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatGetUserMethodThrowsExceptionIfUserNameParameterIsMissing() throws Exception {
		mockMvc.perform(get(API_USER).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatGetUserMethodThrowsExceptionIfUserNameParameterIsEmpty() throws Exception {
		mockMvc.perform(get(API_USER).param("userName", "").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatGetUserMethodThrowsExceptionIfUserNameParameterHasATypo() throws Exception {
		mockMvc.perform(get(API_USER).param("uSerName", "").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatGetUserMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(get(API_USER).param("userName", "test_user").param("xparam", "1").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}
}
