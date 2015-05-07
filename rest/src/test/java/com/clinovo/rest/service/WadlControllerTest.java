package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

public class WadlControllerTest extends BaseServiceTest {

	@Test
	public void testThatWadlServiceReturnsOkForPostRequest() throws Exception {
		this.mockMvc.perform(post(API_WADL).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatWadlServiceReturnsOkForPostRequestIfAccepIsApplicationJson() throws Exception {
		this.mockMvc.perform(post(API_WADL).accept(MediaType.APPLICATION_JSON).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatWadlServiceReturnsOkForPostRequestIfAccepIsApplicationXml() throws Exception {
		this.mockMvc.perform(post(API_WADL).accept(MediaType.APPLICATION_XML).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatWadlServiceReturnsOkForGetRequest() throws Exception {
		this.mockMvc.perform(get(API_WADL).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatWadlServiceReturnsOkForGetRequestIfAccepIsApplicationJson() throws Exception {
		this.mockMvc.perform(get(API_WADL).accept(MediaType.APPLICATION_JSON).secure(true)).andExpect(status().isOk());
	}

	@Test
	public void testThatWadlServiceReturnsOkForGetRequestIfAccepIsApplicationXml() throws Exception {
		this.mockMvc.perform(get(API_WADL).accept(MediaType.APPLICATION_XML).secure(true)).andExpect(status().isOk());
	}
}
