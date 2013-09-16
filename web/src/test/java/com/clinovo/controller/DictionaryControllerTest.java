package com.clinovo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.clinovo.BaseControllerTest;


public class DictionaryControllerTest extends BaseControllerTest {

	@Test
	public void testThatGetRequestResolvesWith200Status() throws Exception {
		this.mockMvc.perform(get("/dictionary").accept(MediaType.ALL)).andExpect(status().isOk());
	}
}
