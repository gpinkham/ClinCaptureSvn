package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import com.clinovo.controller.base.BaseController;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;

import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EditUserAccountControllerTest extends BaseControllerTest {

	public static final String EDIT_USER_ACCOUNT_PAGE = "/EditUserAccount";

	private UserAccountBean currentUser = new UserAccountBean();


	@Test
	public void testThatMainGetReturnsCorrectCode() throws Exception {
		currentUser.setId(1);
		currentUser.addUserType(UserType.SYSADMIN);
		this.mockMvc.perform(get(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").session(session)
				.sessionAttr(BaseController.CURRENT_USER, currentUser)).andExpect(status().is(200));
	}

	@Test
	public void testThatUserWillBeRedirectedToCorrectPageIfRoleIsNotSysAdmin() throws Exception {
		currentUser.setId(1);
		currentUser.addUserType(UserType.USER);
		this.mockMvc.perform(get(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").session(session)
				.sessionAttr(BaseController.CURRENT_USER, currentUser)).andExpect(MockMvcResultMatchers.view()
				.name("redirect:/MainMenu?message=system_no_permission"));
	}

	@Test
	public void testThatEditUserAccountContinueReturnsCorrectCode() throws Exception {
		currentUser.setId(1);
		currentUser.addUserType(UserType.SYSADMIN);
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("continue", "true")
				.session(session).sessionAttr(BaseController.CURRENT_USER, currentUser)).andExpect(status().isOk());
	}

	@Test
	public void testThatUserWillBeRedirectedFromEditUserAccountContinueIfRoleIsIncorrect() throws Exception {
		currentUser.setId(1);
		currentUser.addUserType(UserType.USER);
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("continue", "true")
				.session(session).sessionAttr(BaseController.CURRENT_USER, currentUser))
				.andExpect(MockMvcResultMatchers.view().name("redirect:/MainMenu?message=system_no_permission"));
	}

	@Test
	public void testThatEditUserAccountSubmitReturnsCorrectCode() throws Exception {
		currentUser.setId(1);
		currentUser.addUserType(UserType.SYSADMIN);
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("submit", "true")
				.session(session).sessionAttr(BaseController.CURRENT_USER, currentUser)).andExpect(status().is(302))
				.andExpect(MockMvcResultMatchers.view().name("redirect:/ListUserAccounts"));
	}

	@Test
	public void testThatUserWillBeRedirectedFromEditUserAccountSubmitIfRoleIsIncorrect() throws Exception {
		currentUser.setId(1);
		currentUser.addUserType(UserType.USER);
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("submit", "true")
				.session(session).sessionAttr(BaseController.CURRENT_USER, currentUser)).andExpect(MockMvcResultMatchers.view()
				.name("redirect:/MainMenu?message=system_no_permission"));
	}

	@Test
	public void testThatEditUserAccountSubmitAndRestoreReturnsCorrectCode() throws Exception {
		currentUser.setId(1);
		currentUser.addUserType(UserType.SYSADMIN);
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("submit_and_restore", "true")
				.session(session).sessionAttr(BaseController.CURRENT_USER, currentUser)).andExpect(status().is(200));
	}

	@Test
	public void testThatUserWillBeRedirectedFromEditUserAccountSubmitAndRestoreIfRoleIsIncorrect() throws Exception {
		currentUser.setId(1);
		currentUser.addUserType(UserType.USER);
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("submit_and_restore", "true")
				.session(session).sessionAttr(BaseController.CURRENT_USER, currentUser)).andExpect(MockMvcResultMatchers.view()
				.name("redirect:/MainMenu?message=system_no_permission"));
	}
}
