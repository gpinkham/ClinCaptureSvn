package com.clinovo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.clinovo.BaseControllerTest;


public class DictionaryControllerTest extends BaseControllerTest {

	@Test
	public void testThatGetRequestResolvesWith200Status() throws Exception {
		this.mockMvc.perform(get(DICTIONARY_CONTROLLER).accept(MediaType.ALL)).andExpect(status().isOk());
	}
	
	@Test
	public void testThatGetRequestOnWrongUrlRedirectsToErrorPage() throws Exception {
		
		// xD
		this.mockMvc.perform(get(DICTIONARY_CONTROLLER + "/openclinica"))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void testThatGetRequestReturnsRequestWithPanelAttribute() throws Exception {

		this.mockMvc.perform(get(DICTIONARY_CONTROLLER))
		
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("panel"));
	}
	
	@Test
	public void testThatGetRequestReturnsCorrectViewName() throws Exception {

		this.mockMvc.perform(get(DICTIONARY_CONTROLLER))
			.andExpect(status().isOk())
			.andExpect(view().name("dictionaries"));
	}
	
	@Test
	public void testThatGetRequestReturnsRequestWithHtmlAttribute() throws Exception {

		this.mockMvc.perform(get(DICTIONARY_CONTROLLER))
		
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("dictionaryTable"));
	}
}
