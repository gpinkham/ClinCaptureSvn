package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

public class WadlControllerTest extends BaseServiceTest {

	public static final String WADL_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><application xmlns=\"http://wadl.dev.java.net/2009/02\"><doc title=\"ClinCapture REST API Service WADL\"/><resources base=\"http://localhost:80\"><resource path=\"/authentication\"><method id=\"authenticate\" name=\"POST\"><doc title=\"AuthenticationService.authenticate\"/><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"username\" style=\"query\" type=\"xs:string\" required=\"true\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"password\" style=\"query\" type=\"xs:string\" required=\"true\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"studyname\" style=\"query\" type=\"xs:string\" required=\"true\"/></request></method></resource><resource path=\"/user/create\"><method id=\"createUser\" name=\"POST\"><doc title=\"UserService.createUser\"/><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"username\" style=\"query\" type=\"xs:string\" required=\"true\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"firstname\" style=\"query\" type=\"xs:string\" required=\"true\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"lastname\" style=\"query\" type=\"xs:string\" required=\"true\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"email\" style=\"query\" type=\"xs:string\" required=\"true\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"phone\" style=\"query\" type=\"xs:string\" required=\"true\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"company\" style=\"query\" type=\"xs:string\" required=\"true\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"usertype\" style=\"query\" type=\"xs:int\" required=\"true\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"allowsoap\" style=\"query\" type=\"xs:boolean\" required=\"true\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"displaypassword\" style=\"query\" type=\"xs:boolean\" required=\"true\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"scope\" style=\"query\" type=\"xs:int\" required=\"true\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" name=\"role\" style=\"query\" type=\"xs:int\" required=\"true\"/></request></method></resource></resources></application>";

	@Test
	public void testThatWadlServiceReturnsOkForPostRequest() throws Exception {
		this.mockMvc.perform(post(API_WADL).secure(true)).andExpect(status().isOk())
				.andExpect(content().string(WADL_RESPONSE));
	}

	@Test
	public void testThatWadlServiceReturnsOkForPostRequestIfAccepIsApplicationJson() throws Exception {
		this.mockMvc.perform(post(API_WADL).accept(MediaType.APPLICATION_JSON).secure(true)).andExpect(status().isOk())
				.andExpect(content().string(WADL_RESPONSE));
	}

	@Test
	public void testThatWadlServiceReturnsOkForPostRequestIfAccepIsApplicationXml() throws Exception {
		this.mockMvc.perform(post(API_WADL).accept(MediaType.APPLICATION_XML).secure(true)).andExpect(status().isOk())
				.andExpect(content().string(WADL_RESPONSE));
	}

	@Test
	public void testThatWadlServiceReturnsOkForGetRequest() throws Exception {
		this.mockMvc.perform(get(API_WADL).secure(true)).andExpect(status().isOk())
				.andExpect(content().string(WADL_RESPONSE));
	}

	@Test
	public void testThatWadlServiceReturnsOkForGetRequestIfAccepIsApplicationJson() throws Exception {
		this.mockMvc.perform(get(API_WADL).accept(MediaType.APPLICATION_JSON).secure(true)).andExpect(status().isOk())
				.andExpect(content().string(WADL_RESPONSE));
	}

	@Test
	public void testThatWadlServiceReturnsOkForGetRequestIfAccepIsApplicationXml() throws Exception {
		this.mockMvc.perform(get(API_WADL).accept(MediaType.APPLICATION_XML).secure(true)).andExpect(status().isOk())
				.andExpect(content().string(WADL_RESPONSE));
	}
}
