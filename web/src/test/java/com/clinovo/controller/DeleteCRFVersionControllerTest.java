package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.control.core.BaseController;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteCRFVersionControllerTest extends BaseControllerTest {

	@Test
	public void testThatDeleteCRFVersionReturnsCode200() throws Exception {

		StudyUserRoleBean userRole = new StudyUserRoleBean();
		userRole.setRole(Role.STUDY_ADMINISTRATOR);

		this.mockMvc.perform(
				get(DELETE_CRF_VERSION).param("crfVersionId", "1").sessionAttr(BaseController.USER_ROLE, userRole))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatDeleteCRFVersionReturnsCorrectUrl() throws Exception {

		StudyUserRoleBean userRole = new StudyUserRoleBean();
		userRole.setRole(Role.STUDY_ADMINISTRATOR);

		this.mockMvc.perform(
				MockMvcRequestBuilders.get(DELETE_CRF_VERSION).param("crfVersionId", "1")
						.sessionAttr(BaseController.USER_ROLE, userRole)).andExpect(
				MockMvcResultMatchers.view().name("admin/deleteCRFVersion"));
	}

	@Test
	public void testThatDeleteCRFVersionBlocksNonAdministrativeRoles() throws Exception {

		StudyUserRoleBean userRole = new StudyUserRoleBean();
		userRole.setRole(Role.STUDY_MONITOR);

		this.mockMvc.perform(
				MockMvcRequestBuilders.get(DELETE_CRF_VERSION).param("crfVersionId", "1")
						.sessionAttr(BaseController.USER_ROLE, userRole)).andExpect(
				MockMvcResultMatchers.view().name("redirect:/MainMenu?message=system_no_permission"));
	}

	@Test
	public void testThatDeleteCRFVersionConfirmReturnsCode302() throws Exception {

		this.mockMvc.perform(post(DELETE_CRF_VERSION).param("crfVersionId", "1").param("confirm", "confirm"))
				.andExpect(status().isFound());
	}

	@Test
	public void testThatDeleteCRFVersionConfirmReturnsCorrectUrl() throws Exception {

		this.mockMvc.perform(
				MockMvcRequestBuilders.post(DELETE_CRF_VERSION).param("crfVersionId", "1").param("confirm", "confirm"))
				.andExpect(MockMvcResultMatchers.view().name("redirect:/ListCRF"));
	}
}
