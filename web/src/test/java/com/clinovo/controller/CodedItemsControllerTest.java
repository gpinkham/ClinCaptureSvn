package com.clinovo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import com.clinovo.BaseControllerTest;

public class CodedItemsControllerTest extends BaseControllerTest {

	@Test
	public void testThatGetRequestOnWrongUrlRedirectsToErrorPage() throws Exception {
		
		this.mockMvc.perform(get(CODED_ITEM_CONTROLLER + "/brian"))
			.andExpect(status().isNotFound());
	}
}
