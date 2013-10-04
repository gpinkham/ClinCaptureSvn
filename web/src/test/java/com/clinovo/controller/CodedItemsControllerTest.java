package com.clinovo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.clinovo.BaseControllerTest;

public class CodedItemsControllerTest extends BaseControllerTest {

	@Test
	public void testThatGetRequestResolvesWith200Status() throws Exception {
		
		this.mockMvc.perform(get(CODED_ITEM_CONTROLLER).accept(MediaType.ALL))
			.andExpect(status().isOk());
	}

	@Test
	public void testThatGetRequestOnWrongUrlRedirectsToErrorPage() throws Exception {
		
		this.mockMvc.perform(get(CODED_ITEM_CONTROLLER + "brian"))
			.andExpect(status().isNotFound());
	}

	@Test
	public void testThatGetRequestReturnsCorrectViewName() throws Exception {

		this.mockMvc.perform(get(CODED_ITEM_CONTROLLER))
			.andExpect(status().isOk())
			.andExpect(view().name("codedItems"));
	}

	@Test
	public void testThatGetRequestReturnsRequestWithHtmlAttribute() throws Exception {

		this.mockMvc.perform(get(CODED_ITEM_CONTROLLER))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("codedQuestionsHtml"));
	}

	@Test
	public void testThatGetRequestReturnsRequestWithValidHtmlAttribute() throws Exception {

		this.mockMvc.perform(get(CODED_ITEM_CONTROLLER))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("codedQuestionsHtml"));
	}
	
	@Test
	public void testThatGetRequestReturnsRequestWithAllItemsAttribute() throws Exception {

		this.mockMvc.perform(get(CODED_ITEM_CONTROLLER))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("allItems"));
	}

	@Test
	public void testThatGetRequestReturnsRequestWithCodedItemsAttribute() throws Exception {

		this.mockMvc.perform(get(CODED_ITEM_CONTROLLER))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("codedItems"));
	}
	
	@Test
	public void testThatGetRequestReturnsRequestWithUnCodedItemsAttribute() throws Exception {

		this.mockMvc.perform(get(CODED_ITEM_CONTROLLER))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("unCodedItems"));
	}
	
	@Test
	public void testThatGetRequestReturnsRequestWithCodedItemsHtmlAttribute() throws Exception {

		this.mockMvc.perform(get(CODED_ITEM_CONTROLLER))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("codedQuestionsHtml"));
	}
	
	@Test
	public void testThatGetRequestReturnsRequestWithPanelAttribute() throws Exception {

		this.mockMvc.perform(get(CODED_ITEM_CONTROLLER))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("panel"));
	}
}
