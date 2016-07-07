package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SpringController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * EmailLogController Tests.
 */
public class EmailLogControllerTest extends BaseControllerTest  {

	public static final String MAIN_PAGE_NAME = "admin/emailLog";
	public static final String MAIN_URL = "/EmailLog";
	public static final String DETAILS_PAGE_NAME = "admin/emailLogDetails";
	public static final String DETAILS_URL = "/EmailLogDetails";

	@Before
	public void prepare() {
		StudyBean studyBean = new StudyBean();
		studyBean.setId(1);
		UserAccountBean userAccountBean = new UserAccountBean();
		session.setAttribute(SpringController.STUDY, studyBean);
		session.setAttribute(SpringController.USER_BEAN_NAME, userAccountBean);
	}

	@Test
	public void testThatHttpStatus200IsReturnedByEmailLogPageCall() throws Exception {
		this.mockMvc.perform(get(MAIN_URL).session(session)).andExpect(status().isOk());
	}

	@Test
	public void testThatEmailLogPageCallReturnsModelWithAllAttributes() throws Exception {
		this.mockMvc.perform(get(MAIN_URL).session(session)).andExpect(
				MockMvcResultMatchers.model().attributeExists("dataTable", "logs"));
	}

	@Test
	public void testThatEmailLogPageCallReturnsCorrectUrl() throws Exception {
		this.mockMvc.perform(get(MAIN_URL).session(session)).andExpect(
				MockMvcResultMatchers.view().name(MAIN_PAGE_NAME));
	}

	@Test
	public void testThatHttpStatus200IsReturnedByEmailLogDetailsPageCall() throws Exception {
		this.mockMvc.perform(get(DETAILS_URL).param("id", "1").session(session)).andExpect(status().isOk());
	}

	@Test
	public void testThatEmailLogDetailsPageCallReturnsModelWithAllAttributes() throws Exception {
		this.mockMvc.perform(get(DETAILS_URL).param("id", "1").session(session)).andExpect(
				MockMvcResultMatchers.model().attributeExists("logEntry", "dataTable", "sentFromAdminEmail"));
	}

	@Test
	public void testThatEmailLogDetailsPageCallReturnsCorrectUrl() throws Exception {
		this.mockMvc.perform(get(DETAILS_URL).param("id", "1").session(session)).andExpect(
				MockMvcResultMatchers.view().name(DETAILS_PAGE_NAME));
	}
}
