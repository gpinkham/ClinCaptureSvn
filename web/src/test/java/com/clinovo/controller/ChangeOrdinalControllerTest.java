package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChangeOrdinalControllerTest extends BaseControllerTest {

	@Test
	public void testThatChangeOrdinalReturnsCode302() throws Exception {

		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.addUserType(UserType.SYSADMIN);

		this.mockMvc.perform(get(CHANGE_ORDINAL_CONTROLLER).param("current", "1").param("previous", "2").sessionAttr("userBean", userAccountBean)).andExpect(status().isFound());
	}

	@Test
	public void testThatChangeOrdinalReturnsCorrectUrl() throws Exception {

		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.addUserType(UserType.SYSADMIN);

		this.mockMvc.perform(get(CHANGE_ORDINAL_CONTROLLER).param("current", "1").param("previous", "2").sessionAttr("userBean", userAccountBean)).andExpect(
				MockMvcResultMatchers.view().name("redirect:/ListEventDefinition"));
	}

	@Test
	public void testThatChangeOrdinalRedirectNonAdminRoles() throws Exception {

		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.addUserType(UserType.USER);

		this.mockMvc.perform(get(CHANGE_ORDINAL_CONTROLLER).param("current", "1").param("previous", "2").sessionAttr("userBean", userAccountBean)).andExpect(
				MockMvcResultMatchers.view().name("redirect:/MainMenu?message=system_no_permission"));
	}
}
