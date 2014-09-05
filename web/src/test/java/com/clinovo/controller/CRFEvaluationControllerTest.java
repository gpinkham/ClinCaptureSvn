package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.BaseController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CRFEvaluationControllerTest extends BaseControllerTest {

	private StudyBean currentStudy;
	private UserAccountBean userBean;
	private StudyUserRoleBean userRole;

	@Before
	public void setUp() {
		currentStudy = new StudyBean();
		currentStudy.setId(1);

		userRole = new StudyUserRoleBean();

		userBean = new UserAccountBean();
		userBean.setId(1);
	}

	@Test
	public void testThatForSystemAdministratorRoleCRFEvaluationControllerReturnsCode200() throws Exception {
		userRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		this.mockMvc.perform(
				get(CRF_EVALUATION).locale(LOCALE).sessionAttr(BaseController.STUDY, currentStudy)
						.sessionAttr(BaseController.USER_BEAN_NAME, userBean)
						.sessionAttr(BaseController.USER_ROLE, userRole)).andExpect(status().isOk());
	}

	@Test
	public void testThatForEvaluatorRoleCRFEvaluationControllerReturnsCode200() throws Exception {
		userRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		this.mockMvc.perform(
				get(CRF_EVALUATION).locale(LOCALE).sessionAttr(BaseController.STUDY, currentStudy)
						.sessionAttr(BaseController.USER_BEAN_NAME, userBean)
						.sessionAttr(BaseController.USER_ROLE, userRole)).andExpect(status().isOk());
	}

	@Test
	public void testThatCRFEvaluationControllerBlocksNonAdministrativeRoles() throws Exception {
		userRole.setRole(Role.INVESTIGATOR);
		this.mockMvc.perform(
				MockMvcRequestBuilders.get(CRF_EVALUATION).locale(LOCALE)
						.sessionAttr(BaseController.STUDY, currentStudy)
						.sessionAttr(BaseController.USER_BEAN_NAME, userBean)
						.sessionAttr(BaseController.USER_ROLE, userRole)).andExpect(
				MockMvcResultMatchers.view().name("redirect:/MainMenu?message=system_no_permission"));
	}
}
