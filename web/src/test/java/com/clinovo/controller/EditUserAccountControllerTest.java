package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.BaseController;
import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EditUserAccountControllerTest extends BaseControllerTest {

	public static final String EDIT_USER_ACCOUNT_PAGE = "/EditUserAccount";

	public static final StudyUserRoleBean USER_ROLE = new StudyUserRoleBean();
	public static final StudyBean CURRENT_STUDY = new StudyBean();


	@Test
	public void testThatMainGetReturnsCorrectCode() throws Exception {
		USER_ROLE.setRole(Role.SYSTEM_ADMINISTRATOR);
		this.mockMvc.perform(get(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").session(session).sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().is(200));
	}

	@Test
	public void testThatUserWillBeRedirectedToCorrectPageIfRoleIsNotSysAdmin() throws Exception {
		USER_ROLE.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		this.mockMvc.perform(get(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").session(session).sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(MockMvcResultMatchers.view().name("redirect:/MainMenu?message=system_no_permission"));
	}

	@Test
	public void testThatEditUserAccountContinueReturnsCorrectCode() throws Exception {
		USER_ROLE.setRole(Role.SYSTEM_ADMINISTRATOR);
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("continue", "true").session(session).sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().isOk());
	}

	@Test
	public void testThatUserWillBeRedirectedFromEditUserAccountContinueIfRoleIsIncorrect() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("continue", "true").session(session).sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(MockMvcResultMatchers.view().name("redirect:/MainMenu?message=system_no_permission"));
	}

	@Test
	public void testThatEditUserAccountSubmitReturnsCorrectCode() throws Exception {
		USER_ROLE.setRole(Role.SYSTEM_ADMINISTRATOR);
		UserAccountBean user = new UserAccountBean();
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("submit", "true").session(session).sessionAttr("userBean", user).sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().is(302)).andExpect(MockMvcResultMatchers.view().name("redirect:/ListUserAccounts"));
	}

	@Test
	public void testThatUserWillBeRedirectedFromEditUserAccountSubmitIfRoleIsIncorrect() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("submit", "true").session(session).sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(MockMvcResultMatchers.view().name("redirect:/MainMenu?message=system_no_permission"));
	}

	@Test
	public void testThatEditUserAccountSubmitAndRestoreReturnsCorrectCode() throws Exception {
		USER_ROLE.setRole(Role.SYSTEM_ADMINISTRATOR);
		UserAccountBean user = new UserAccountBean();
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("submit_and_restore", "true").session(session).sessionAttr("userBean", user).sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(status().is(200));
	}

	@Test
	public void testThatUserWillBeRedirectedFromEditUserAccountSubmitAndRestoreIfRoleIsIncorrect() throws Exception {
		USER_ROLE.setRole(Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("submit_and_restore", "true").session(session).sessionAttr(BaseController.USER_ROLE, USER_ROLE)).andExpect(MockMvcResultMatchers.view().name("redirect:/MainMenu?message=system_no_permission"));
	}
}



