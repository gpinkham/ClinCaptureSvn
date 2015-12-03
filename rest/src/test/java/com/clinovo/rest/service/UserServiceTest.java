package com.clinovo.rest.service;

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
	public void testThatItIsImpossibleToCreateAUserIfUserTypeParameterHasWrongValue() throws Exception {
		this.mockMvc
				.perform(post(API_USER_CREATE).param("username", "new_user_".concat(Long.toString(timestamp)))
						.param("firstname", "firstname_".concat(Long.toString(timestamp)))
						.param("lastname", "lastname_".concat(Long.toString(timestamp)))
						.param("email", "test@gmail.com").param("phone", "+375232345678").param("company", "clinovo")
						.param("usertype", "123").param("allowsoap", "true").param("displaypassword", "true")
						.param("role", Integer.toString(3456)).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateAUserIfRoleParameterHasWrongValue() throws Exception {
		this.mockMvc.perform(post(API_USER_CREATE).param("username", "new_user_".concat(Long.toString(timestamp)))
				.param("firstname", "firstname_".concat(Long.toString(timestamp)))
				.param("lastname", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
				.param("displaypassword", "true").param("role", "123123").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToCreateAUserWithRoleThatDoesNotExist() throws Exception {
		this.mockMvc.perform(post(API_USER_CREATE).param("username", "new_user_".concat(Long.toString(timestamp)))
				.param("firstname", "firstname_".concat(Long.toString(timestamp)))
				.param("lastname", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
				.param("displaypassword", "true").param("role", Integer.toString(3456)).accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyAdministratorWithoutAdministrativePrivilegesIsNotAbleToCallUserAPI() throws Exception {
		ResultMatcher expectStatus = status().isForbidden();
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.USER, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		String additionalUserName = "new_".concat(newUser.getName());
		this.mockMvc.perform(post(API_USER_CREATE).param("username", additionalUserName)
				.param("firstname", newUser.getFirstName()).param("lastname", newUser.getLastName())
				.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
				.param("company", newUser.getInstitutionalAffiliation())
				.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
				.param("displaypassword", "true").param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId()))
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatStudyAdministratorWithAdministrativePrivilegesIsAbleToCallUserAPI() throws Exception {
		ResultMatcher expectStatus = status().isOk();
		mailSenderHost = mailSender.getHost();
		mailSender.setHost("");
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		String additionalUserName = "new_".concat(newUser.getName());
		this.mockMvc.perform(post(API_USER_CREATE).param("username", additionalUserName)
				.param("firstname", newUser.getFirstName()).param("lastname", newUser.getLastName())
				.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
				.param("company", newUser.getInstitutionalAffiliation())
				.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
				.param("displaypassword", "true").param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId()))
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatCreatedStudyCoderWithoutAdministrativePrivilegesIsNotAbleToCalluserAPI() throws Exception {
		ResultMatcher expectStatus = status().isForbidden();
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		createNewUser(UserType.USER, Role.STUDY_CODER);
		login(newUser.getName(), UserType.USER, Role.STUDY_CODER, newUser.getPasswd(), newStudy.getName());
		String additionalUserName = "new_".concat(newUser.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE).param("username", additionalUserName).param("firstname", newUser.getFirstName())
						.param("lastname", newUser.getLastName()).param("email", newUser.getEmail())
						.param("phone", newUser.getPhone()).param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("role", Integer.toString(Role.STUDY_CODER.getId()))
						.accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatCreatedStudyCoderWithAdministrativePrivilegesIsNotAbleToCalluserAPI() throws Exception {
		ResultMatcher expectStatus = status().isForbidden();
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		createNewUser(UserType.SYSADMIN, Role.STUDY_CODER);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_CODER, newUser.getPasswd(), newStudy.getName());
		String additionalUserName = "new_".concat(newUser.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE).param("username", additionalUserName).param("firstname", newUser.getFirstName())
						.param("lastname", newUser.getLastName()).param("email", newUser.getEmail())
						.param("phone", newUser.getPhone()).param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("role", Integer.toString(Role.STUDY_CODER.getId()))
						.accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatCreatedStudyEvaluatorWithoutAdministrativePrivilegesIsNotAbleToCallUserAPI() throws Exception {
		ResultMatcher expectStatus = status().isForbidden();
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		createNewUser(UserType.USER, Role.STUDY_EVALUATOR);
		login(newUser.getName(), UserType.USER, Role.STUDY_EVALUATOR, newUser.getPasswd(), newStudy.getName());
		String additionalUserName = "new_".concat(newUser.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE).param("username", additionalUserName).param("firstname", newUser.getFirstName())
						.param("lastname", newUser.getLastName()).param("email", newUser.getEmail())
						.param("phone", newUser.getPhone()).param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("role", Integer.toString(Role.STUDY_EVALUATOR.getId()))
						.accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatCreatedStudyEvaluatorWithAdministrativePrivilegesIsNotAbleToCallUserAPI() throws Exception {
		ResultMatcher expectStatus = status().isForbidden();
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		createNewUser(UserType.SYSADMIN, Role.STUDY_EVALUATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_EVALUATOR, newUser.getPasswd(), newStudy.getName());
		String additionalUserName = "new_".concat(newUser.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE).param("username", additionalUserName).param("firstname", newUser.getFirstName())
						.param("lastname", newUser.getLastName()).param("email", newUser.getEmail())
						.param("phone", newUser.getPhone()).param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("role", Integer.toString(Role.STUDY_EVALUATOR.getId()))
						.accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatStudyMonitorWithAdministrativePrivilegesIsAbleToCallUserAPI() throws Exception {
		ResultMatcher expectStatus = status().isOk();
		mailSenderHost = mailSender.getHost();
		mailSender.setHost("");
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		createNewUser(UserType.SYSADMIN, Role.STUDY_MONITOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_MONITOR, newUser.getPasswd(), newStudy.getName());
		String additionalUserName = "new_".concat(newUser.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE).param("username", additionalUserName).param("firstname", newUser.getFirstName())
						.param("lastname", newUser.getLastName()).param("email", newUser.getEmail())
						.param("phone", newUser.getPhone()).param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("role", Integer.toString(Role.STUDY_MONITOR.getId()))
						.accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatStudyMonitorWithoutAdministrativePrivilegesIsNotAbleToCallUserAPI() throws Exception {
		ResultMatcher expectStatus = status().isForbidden();
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		createNewUser(UserType.USER, Role.STUDY_MONITOR);
		login(newUser.getName(), UserType.USER, Role.STUDY_MONITOR, newUser.getPasswd(), newStudy.getName());
		String additionalUserName = "new_".concat(newUser.getName());
		this.mockMvc.perform(
				post(API_USER_CREATE).param("username", additionalUserName).param("firstname", newUser.getFirstName())
						.param("lastname", newUser.getLastName()).param("email", newUser.getEmail())
						.param("phone", newUser.getPhone()).param("company", newUser.getInstitutionalAffiliation())
						.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
						.param("displaypassword", "true").param("role", Integer.toString(Role.STUDY_MONITOR.getId()))
						.accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", additionalUserName).accept(mediaType).secure(true)
				.session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatCreateUserRequestReturnsCode500IfUsernameHasBeenTakenAlready() throws Exception {
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		this.mockMvc.perform(post(API_USER_CREATE).param("username", newUser.getName())
				.param("firstname", newUser.getFirstName()).param("lastname", newUser.getLastName())
				.param("email", newUser.getEmail()).param("phone", newUser.getPhone())
				.param("company", newUser.getInstitutionalAffiliation())
				.param("usertype", Integer.toString(UserType.SYSADMIN.getId())).param("allowsoap", "true")
				.param("displaypassword", "true").param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId()))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsNotPossibleToCreateAUserWithIncorrectEmail() throws Exception {
		this.mockMvc.perform(post(API_USER_CREATE).param("username", "new_user_".concat(Long.toString(timestamp)))
				.param("firstname", "firstname_".concat(Long.toString(timestamp)))
				.param("lastname", "lastname_".concat(Long.toString(timestamp))).param("email", "testgmailcom")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("usertype", Integer.toString(UserType.USER.getId())).param("allowsoap", "true")
				.param("displaypassword", "true").param("role", Integer.toString(Role.STUDY_MONITOR.getId()))
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToCreateUserIfAllParametersAreSpecified() throws Exception {
		String timeZone = "Etc/GMT+11";
		mailSenderHost = mailSender.getHost();
		mailSender.setHost("");
		String additionalUserName = "new_user_".concat(Long.toString(timestamp));
		result = this.mockMvc.perform(post(API_USER_CREATE).param("username", additionalUserName)
				.param("firstname", "firstname_".concat(Long.toString(timestamp)))
				.param("lastname", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("allowsoap", "true").param("displaypassword", "true")
				.param("company", "clinovo").param("timezone", timeZone)
				.param("usertype", Integer.toString(UserType.SYSADMIN.getId()))
				.param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId())).accept(mediaType).secure(true)
				.session(session)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getRunWebservices());
			assertFalse(restOdmContainer.getRestData().getUserAccountBean().getPasswd().isEmpty());
			assertFalse(restOdmContainer.getRestData().getUserAccountBean().getUserTypeCode()
					.equals(UserType.USER.getCode()));
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getUserTypeCode()
					.equals(UserType.SYSADMIN.getCode()));
			assertTrue(restOdmContainer.getRestData().getUserAccountBean().getUserTimeZoneId().equals(timeZone));
		}
	}

	@Test
	public void testThatItIsPossibleToCreateUserIfOnlyRequiredParametersAreSpecified() throws Exception {
		String additionalUserName = "new_user_".concat(Long.toString(timestamp));
		mailSenderHost = mailSender.getHost();
		mailSender.setHost("");
		result = this.mockMvc.perform(post(API_USER_CREATE).param("username", additionalUserName)
				.param("firstname", "firstname_".concat(Long.toString(timestamp)))
				.param("lastname", "lastname_".concat(Long.toString(timestamp))).param("email", "test@gmail.com")
				.param("phone", "+375232345678").param("company", "clinovo")
				.param("usertype", Integer.toString(UserType.USER.getId()))
				.param("role", Integer.toString(Role.STUDY_ADMINISTRATOR.getId())).accept(mediaType).secure(true)
				.session(session)).andExpect(status().isOk()).andReturn();
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
	public void testThatRemoveUserMethodThrowsExceptionIfUserNameParameterIsMissing() throws Exception {
		this.mockMvc.perform(post(API_USER_REMOVE).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfUserNameParameterIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", "").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		this.mockMvc.perform(
				post(API_USER_REMOVE).param("username", "test_user").param("xparam", "1").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfUserNameParameterIsInWrongCase() throws Exception {
		this.mockMvc.perform(post(API_USER_REMOVE).param("userName", "test_user").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfYouAreTryingToRemoveNonExistingUser() throws Exception {
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", "misterx").secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfYouAreTryingToRemoveRootUser() throws Exception {
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", "root").secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfYouAreTryingToRemoveYourself() throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), studyName);
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", newUser.getName()).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveUserMethodThrowsExceptionIfYouAreTryingToRemoveUserThatDoesNotBelongToCurrentScope()
			throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		String additionalUserName = newUser.getName();
		String additionalUserPassword = newUser.getPasswd();
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		timestamp = new Date().getTime() + 1;
		createNewUser(UserType.USER, Role.STUDY_MONITOR);
		login(additionalUserName, UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, additionalUserPassword, studyName);
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", newUser.getName()).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveUserMethodChangesUserStatusCorrectly() throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		UserAccountBean userAccountBean = (UserAccountBean) userAccountDAO.findByUserName(newUser.getName());
		assertEquals(userAccountBean.getStatus(), Status.AVAILABLE);
		this.mockMvc.perform(post(API_USER_REMOVE).param("username", newUser.getName()).secure(true).session(session))
				.andExpect(status().isOk());
		userAccountBean = (UserAccountBean) userAccountDAO.findByUserName(newUser.getName());
		assertEquals(userAccountBean.getStatus(), Status.DELETED);
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfUserNameParameterIsMissing() throws Exception {
		this.mockMvc.perform(post(API_USER_RESTORE).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfUserNameParameterIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", "").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", "test_user").param("xparam", "1").secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfUserNameParameterIsInWrongCase() throws Exception {
		this.mockMvc.perform(post(API_USER_RESTORE).param("userName", "test_user").secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfYouAreTryingToRestoreNonExistingUser() throws Exception {
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", "misterx").secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfYouAreTryingToRestoreRootUser() throws Exception {
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", "root").secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfYouAreTryingToRestoreYourself() throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), studyName);
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", newUser.getName()).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreUserMethodThrowsExceptionIfYouAreTryingToRestoreUserThatDoesNotBelongToCurrentScope()
			throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		String additionalUserName = newUser.getName();
		String additionalUserPassword = newUser.getPasswd();
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		timestamp = new Date().getTime() + 1;
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		login(additionalUserName, UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, additionalUserPassword, studyName);
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", newUser.getName()).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreUserMethodChangesUserStatusCorrectly() throws Exception {
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		UserAccountBean userAccountBean = (UserAccountBean) userAccountDAO.findByUserName(newUser.getName());
		userAccountBean.setStatus(Status.DELETED);
		userAccountBean.setUpdater(userAccountBean);
		userAccountDAO.updateStatus(userAccountBean);
		userAccountBean = (UserAccountBean) userAccountDAO.findByUserName(newUser.getName());
		assertEquals(userAccountBean.getStatus(), Status.DELETED);
		this.mockMvc.perform(post(API_USER_RESTORE).param("username", newUser.getName()).secure(true).session(session))
				.andExpect(status().isOk());
		userAccountBean = (UserAccountBean) userAccountDAO.findByUserName(newUser.getName());
		assertEquals(userAccountBean.getStatus(), Status.AVAILABLE);
	}
}
