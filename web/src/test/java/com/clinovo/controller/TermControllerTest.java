package com.clinovo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import com.clinovo.BaseControllerTest;

public class TermControllerTest extends BaseControllerTest {

	@Test
	public void testThatGetRequestResolvesWith204Status() throws Exception {
		
		this.mockMvc.perform(get(TERM_CONTROLLER)
				.param("item", "2")
                .param("dictionary", "medDra")
                .param("code", "SOME-CODE-2"))
			.andExpect(status().isNoContent());
	}

	@Test
	public void testThatGetRequestOnWrongUrlRedirectsToErrorPage() throws Exception {
		
		this.mockMvc.perform(get(TERM_CONTROLLER + "/foo"))
			.andExpect(status().isNotFound());
	}

}

