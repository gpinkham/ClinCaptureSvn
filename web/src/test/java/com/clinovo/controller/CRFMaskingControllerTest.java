package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CRFMaskingControllerTest extends BaseControllerTest {

	private static final String MASKING_PAGE = "/CRFsMasking";

	@Test
	public void testThatOpenCRFsMaskingPageReturnsCode() throws Exception {
		this.mockMvc.perform(post(MASKING_PAGE).param("userId", "1")).andExpect(status().is(200));
	}

	@Test
	public void testThatSubmitCRFsMaskingPageReturnsCode() throws Exception {
		this.mockMvc.perform(post(MASKING_PAGE).param("userId", "1").param("submit_and_restore", "true")).andExpect(status().is(302));
	}
}
