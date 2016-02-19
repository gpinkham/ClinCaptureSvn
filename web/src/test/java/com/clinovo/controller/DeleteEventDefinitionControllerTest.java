package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import com.clinovo.util.RequestUtil;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * DeleteEventDefinition SpringServlet Tests.
 */
@SuppressWarnings("unchecked")
public class DeleteEventDefinitionControllerTest extends BaseControllerTest {

	public static final String DELETE_EVENT_DEFINITION_CONTROLLER =  "/deleteEventDefinition";

	@Before
	public void prepare() {
		StudyBean study = new StudyBean();
		study.setId(1);
		session.setAttribute("study", study);
	}

	@Test
	public void testThatMainGetReturnsCorrectCode() throws Exception {
		this.mockMvc.perform(get(DELETE_EVENT_DEFINITION_CONTROLLER).param("id", "1").session(session)).andExpect(status().is(200));
	}

	@Test
	public void testThatMainGetReturnsCorrectNumberOfAttributes() throws Exception {
		this.mockMvc.perform(get(DELETE_EVENT_DEFINITION_CONTROLLER).param("id", "1").session(session)).andExpect(model().size(4));
	}

	@Test
	public void testThatMainGetReturnsCorrectAttributes() throws Exception {
		this.mockMvc.perform(get(DELETE_EVENT_DEFINITION_CONTROLLER).param("id", "1").session(session))
				.andExpect(model().attributeExists("eventDefinitionCRFs", "studyEventBeans", "eventId", "event"));
	}

	@Test
	public void testThatMainGetReturnsCorrectViewName() throws Exception {
		this.mockMvc.perform(get(DELETE_EVENT_DEFINITION_CONTROLLER).param("id", "1").session(session))
				.andExpect(view().name("managestudy/deleteEventDefinition"));
	}

	@Test
	public void testThatConfirmDeleteEventDefinitionReturnsCorrectCode() throws Exception {
		this.mockMvc.perform(post(DELETE_EVENT_DEFINITION_CONTROLLER).param("id", "1").param("confirm", "true").session(session)).andExpect(status().is(302));
	}

	@Test
	public void testThatConfirmDeleteEventDefinitionReturnsCorrectNumberOfAttributes() throws Exception {
		this.mockMvc.perform(post(DELETE_EVENT_DEFINITION_CONTROLLER).param("id", "1").param("confirm", "true").session(session)).andExpect(model().size(0));
	}

	@Test
	public void testThatConfirmDeleteEventDefinitionReturnsCorrectMessageIfStudyEventsArePresent() throws Exception {
		this.mockMvc.perform(post(DELETE_EVENT_DEFINITION_CONTROLLER).param("id", "1").param("confirm", "true").session(session));
		HashMap<String, Object> storedAttributes = (HashMap<String, Object>) session.getAttribute(RequestUtil.STORED_ATTRIBUTES);
		ArrayList<String> messages = (ArrayList<String>) storedAttributes.get(RequestUtil.PAGE_MESSAGE);
		String message = messages.get(0);
		assertTrue(message.contains("cannot"));
	}

	@Test
	public void testThatConfirmDeleteEventDefinitionReturnsCorrectMessageIfEventCRFsArePresent() throws Exception {
		this.mockMvc.perform(post(DELETE_EVENT_DEFINITION_CONTROLLER).param("id", "9").param("confirm", "true").session(session));
		HashMap<String, Object> storedAttributes = (HashMap<String, Object>) session.getAttribute(RequestUtil.STORED_ATTRIBUTES);
		ArrayList<String> messages = (ArrayList<String>) storedAttributes.get(RequestUtil.PAGE_MESSAGE);
		String message = messages.get(0);
		assertTrue(message.contains("cannot"));
	}

	@Test
	public void testThatConfirmDeleteEventDefinitionReturnsCorrectMessageIfEventCRFsAndDefinitionsAreNotPresent() throws Exception {
		this.mockMvc.perform(post(DELETE_EVENT_DEFINITION_CONTROLLER).param("id", "4").param("confirm", "true").session(session));
		HashMap<String, Object> storedAttributes = (HashMap<String, Object>) session.getAttribute(RequestUtil.STORED_ATTRIBUTES);
		ArrayList<String> messages = (ArrayList<String>) storedAttributes.get(RequestUtil.PAGE_MESSAGE);
		String message = messages.get(0);
		assertTrue(message.contains("successfully"));
	}

	@Test
	public void testThatConfirmDeleteEventDefinitionReturnsCorrectViewName() throws Exception {
		this.mockMvc.perform(post(DELETE_EVENT_DEFINITION_CONTROLLER).param("id", "1").param("confirm", "true").session(session))
				.andExpect(view().name("redirect:/ListEventDefinition"));
	}
}
