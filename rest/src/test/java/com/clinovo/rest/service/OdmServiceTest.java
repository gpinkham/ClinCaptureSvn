package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

public class OdmServiceTest extends BaseServiceTest {

	@Test
	public void testThatOdmServiceReturnsOkForPostRequest() throws Exception {
		mockMvc.perform(post(API_ODM).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatOdmServiceReturnsOkForPostRequestIfAcceptIsApplicationJson() throws Exception {
		mockMvc.perform(post(API_ODM).accept(MediaType.APPLICATION_JSON).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatOdmServiceReturnsOkForPostRequestIfAcceptIsApplicationXml() throws Exception {
		mockMvc.perform(post(API_ODM).accept(MediaType.APPLICATION_XML).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatOdmServiceReturnsOkForGetRequestIfAcceptIsNull() throws Exception {
		mockMvc.perform(get(API_ODM).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatOdmServiceReturnsOkForGetRequestIfAcceptIsApplicationJson() throws Exception {
		mockMvc.perform(get(API_ODM).accept(MediaType.APPLICATION_JSON).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatOdmServiceReturnsOkForGetRequestIfAcceptIsApplicationXml() throws Exception {
		mockMvc.perform(get(API_ODM).accept(MediaType.APPLICATION_XML).secure(true)).andExpect(status().isOk());
	}
}
