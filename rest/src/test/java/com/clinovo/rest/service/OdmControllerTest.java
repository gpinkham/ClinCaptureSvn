package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.http.MediaType;

public class OdmControllerTest extends BaseServiceTest {

	private String odmResponse;

	@Before
	public void before() throws IOException {
		odmResponse = IOUtils.toString(new FileSystemResourceLoader().getResource(
				"classpath:properties/ClinCapture_Rest_ODM1-3-0.xsd").getInputStream());
	}

	@Test
	public void testThatOdmServiceReturnsOkForPostRequest() throws Exception {
		this.mockMvc.perform(post(API_ODM).secure(true)).andExpect(status().isOk())
				.andExpect(content().string(odmResponse));
	}

	@Test
	public void testThatOdmServiceReturnsOkForPostRequestIfAcceptIsApplicationJson() throws Exception {
		this.mockMvc.perform(post(API_ODM).accept(MediaType.APPLICATION_JSON).secure(true)).andExpect(status().isOk())
				.andExpect(content().string(odmResponse));
	}

	@Test
	public void testThatOdmServiceReturnsOkForPostRequestIfAcceptIsApplicationXml() throws Exception {
		this.mockMvc.perform(post(API_ODM).accept(MediaType.APPLICATION_XML).secure(true)).andExpect(status().isOk())
				.andExpect(content().string(odmResponse));
	}

	@Test
	public void testThatOdmServiceReturnsOkForGetRequestIfAcceptIsNull() throws Exception {
		this.mockMvc.perform(get(API_ODM).secure(true)).andExpect(status().isOk())
				.andExpect(content().string(odmResponse));
	}

	@Test
	public void testThatOdmServiceReturnsOkForGetRequestIfAcceptIsApplicationJson() throws Exception {
		this.mockMvc.perform(get(API_ODM).accept(MediaType.APPLICATION_JSON).secure(true)).andExpect(status().isOk())
				.andExpect(content().string(odmResponse));
	}

	@Test
	public void testThatOdmServiceReturnsOkForGetRequestIfAcceptIsApplicationXml() throws Exception {
		this.mockMvc.perform(get(API_ODM).accept(MediaType.APPLICATION_XML).secure(true)).andExpect(status().isOk())
				.andExpect(content().string(odmResponse));
	}
}
