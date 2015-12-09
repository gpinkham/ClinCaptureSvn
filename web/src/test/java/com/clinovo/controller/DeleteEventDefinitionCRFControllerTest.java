package com.clinovo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;

import org.akaza.openclinica.control.core.BaseController;
import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.clinovo.BaseControllerTest;
import com.clinovo.i18n.LocaleResolver;

/**
 * DeleteEventDefinitionCRFController test
 */
public class DeleteEventDefinitionCRFControllerTest extends BaseControllerTest {

	@Test
	public void testThatMainGetReturnsCorrectStatus() throws Exception {
		mockMvc.perform(get(DELETE_EVENT_DEFINITION_CRF_CONTROLLER).param("id", "1").param("edId", "1"))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatMainGetReturnsCorrectNumberOfAttributes() throws Exception {
		mockMvc.perform(get(DELETE_EVENT_DEFINITION_CRF_CONTROLLER).param("id", "1").param("edId", "1"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("canBeDeleted", "eventCRFs", "edc", "edId",
						"ruleSetRules"));
	}

	@Test
	public void testThatMainGetReturnsCorrectPageURL() throws Exception {
		mockMvc.perform(get(DELETE_EVENT_DEFINITION_CRF_CONTROLLER).param("id", "1").param("edId", "1"))
				.andExpect(MockMvcResultMatchers.view().name("managestudy/deleteEventDefinitionCrf"));
	}

	@Test
	public void testThatBackReturnsCorrectPageURL() throws Exception {
		mockMvc.perform(get(DELETE_EVENT_DEFINITION_CRF_CONTROLLER).param("edId", "1").param("back", "true"))
				.andExpect(MockMvcResultMatchers.view().name("redirect:/InitUpdateEventDefinition?id=1"));
	}

	@Test
	public void testThatSubmitReturnsCorrectPageURL() throws Exception {
		mockMvc.perform(
				get(DELETE_EVENT_DEFINITION_CRF_CONTROLLER).param("edId", "1").param("submit", "true").param("id", "1"))
				.andExpect(MockMvcResultMatchers.view().name("redirect:/InitUpdateEventDefinition?id=1"));
	}

	@Test
	public void testThatSubmitDoesNotReturnsAnyModelAttributes() throws Exception {
		mockMvc.perform(
				get(DELETE_EVENT_DEFINITION_CRF_CONTROLLER).param("edId", "1").param("submit", "true").param("id", "1"))
				.andExpect(MockMvcResultMatchers.model().size(0));
	}

	@Test
	public void testThatCorrectMessageIsWrittenToSessionIfCRFCannotBeDeleted() throws Exception {
		String expectedMessage = messageSource.getMessage("rules_are_present_for_crfs", null,
				LocaleResolver.getLocale());
		HashMap<String, Object> storedAttributes = new HashMap<String, Object>();
		ArrayList<String> pageMessages = new ArrayList<String>();
		pageMessages.add(expectedMessage);
		storedAttributes.put(BaseController.PAGE_MESSAGE, pageMessages);
		mockMvc.perform(
				get(DELETE_EVENT_DEFINITION_CRF_CONTROLLER).param("edId", "1").param("submit", "true").param("id", "1"))
				.andExpect(MockMvcResultMatchers.request().sessionAttribute(BaseController.STORED_ATTRIBUTES,
						storedAttributes));
	}

	@Test
	public void testThatCorrectMessageIsWrittenToSessionIfCRFCanBeDeleted() throws Exception {
		String expectedMessage = messageSource.getMessage("crf_was_deleted_from_event_definition",
				new String[]{"", "ED-1-NonRepeating"}, LocaleResolver.getLocale());
		HashMap<String, Object> storedAttributes = new HashMap<String, Object>();
		ArrayList<String> pageMessages = new ArrayList<String>();
		pageMessages.add(expectedMessage);
		storedAttributes.put(BaseController.PAGE_MESSAGE, pageMessages);
		mockMvc.perform(get(DELETE_EVENT_DEFINITION_CRF_CONTROLLER).param("edId", "1").param("submit", "true")
				.param("id", "12"))
				.andExpect(MockMvcResultMatchers.request().sessionAttribute(BaseController.STORED_ATTRIBUTES,
						storedAttributes));
	}
}
