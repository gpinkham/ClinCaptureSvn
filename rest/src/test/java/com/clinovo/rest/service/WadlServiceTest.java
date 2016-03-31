package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

public class WadlServiceTest extends BaseServiceTest {

	@Test
	public void testThatWadlServiceReturnsOkForPostRequest() throws Exception {
		mockMvc.perform(post(API_WADL).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatWadlServiceReturnsOkForPostRequestIfAccepIsApplicationJson() throws Exception {
		mockMvc.perform(post(API_WADL).accept(MediaType.APPLICATION_JSON).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatWadlServiceReturnsOkForPostRequestIfAccepIsApplicationXml() throws Exception {
		mockMvc.perform(post(API_WADL).accept(MediaType.APPLICATION_XML).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatWadlServiceReturnsOkForGetRequest() throws Exception {
		mockMvc.perform(get(API_WADL).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatWadlServiceReturnsOkForGetRequestIfAccepIsApplicationJson() throws Exception {
		mockMvc.perform(get(API_WADL).accept(MediaType.APPLICATION_JSON).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatWadlServiceReturnsOkForGetRequestIfAccepIsApplicationXml() throws Exception {
		mockMvc.perform(get(API_WADL).accept(MediaType.APPLICATION_XML).secure(true)).andExpect(status().isOk());
	}
}
