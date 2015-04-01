package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EditUserAccountControllerTest extends BaseControllerTest {

	public static final String EDIT_USER_ACCOUNT_PAGE = "/EditUserAccount";
	public static final String USER_BEAN = "userBean";

	private UserAccountBean userBean;

	@Before
	public void prepare(){
		userBean = new UserAccountBean();
		userBean.addUserType(UserType.SYSADMIN);
	}

	@Test
	public void testThatMainGetReturnsCorrectCode() throws Exception {
		this.mockMvc.perform(get(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").session(session).sessionAttr(USER_BEAN, userBean)).andExpect(status().is(200));
	}

	@Test
	public void testThatUserWillBeRedirectedToCorrectPageIfRoleIsNotSysAdmin() throws Exception {
		userBean = new UserAccountBean();
		this.mockMvc.perform(get(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").session(session).sessionAttr(USER_BEAN, userBean)).andExpect(MockMvcResultMatchers.view().name("redirect:/MainMenu?message=system_no_permission"));
	}

	@Test
	public void testThatEditUserAccountContinueReturnsCorrectCode() throws Exception {
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("continue", "true").session(session).sessionAttr(USER_BEAN, userBean)).andExpect(status().isOk());
	}

	@Test
	public void testThatUserWillBeRedirectedFromEditUserAccountContinueIfRoleIsIncorrect() throws Exception {
		userBean = new UserAccountBean();
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("continue", "true").session(session).sessionAttr(USER_BEAN, userBean)).andExpect(MockMvcResultMatchers.view().name("redirect:/MainMenu?message=system_no_permission"));
	}

	@Test
	public void testThatEditUserAccountSubmitReturnsCorrectCode() throws Exception {
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("submit", "true").session(session).sessionAttr(USER_BEAN, userBean)).andExpect(status().is(302)).andExpect(MockMvcResultMatchers.view().name("redirect:/ListUserAccounts"));
	}

	@Test
	public void testThatUserWillBeRedirectedFromEditUserAccountSubmitIfRoleIsIncorrect() throws Exception {
		userBean = new UserAccountBean();
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("submit", "true").session(session).sessionAttr(USER_BEAN, userBean)).andExpect(MockMvcResultMatchers.view().name("redirect:/MainMenu?message=system_no_permission"));
	}

	@Test
	public void testThatEditUserAccountSubmitAndRestoreReturnsCorrectCode() throws Exception {
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("submit_and_restore", "true").session(session).sessionAttr(USER_BEAN, userBean)).andExpect(status().is(200));
	}

	@Test
	public void testThatUserWillBeRedirectedFromEditUserAccountSubmitAndRestoreIfRoleIsIncorrect() throws Exception {
		userBean = new UserAccountBean();
		this.mockMvc.perform(post(EDIT_USER_ACCOUNT_PAGE).param("userId", "1").param("submit_and_restore", "true").session(session).sessionAttr(USER_BEAN, userBean)).andExpect(MockMvcResultMatchers.view().name("redirect:/MainMenu?message=system_no_permission"));
	}
}
