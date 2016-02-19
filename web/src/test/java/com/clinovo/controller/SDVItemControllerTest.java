package com.clinovo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import com.clinovo.BaseControllerTest;

public class SDVItemControllerTest extends BaseControllerTest {

	public static final String ACTION = "action";
	public static final String SECTION_ID = "sectionId";
	public static final String ITEM_DATA_ID = "itemDataId";
	public static final String EVENT_DEFINITION_CRF_ID = "eventDefinitionCrfId";

	@Before
	public void before() throws Exception {
		session.setAttribute(SpringController.USER_BEAN_NAME, new UserAccountDAO(dataSource).findByPK(1));
	}

	@Test
	public void testThatGetRequestReturnsJsonObject() throws Exception {
		this.mockMvc
				.perform(
						get(SDV_ITEM_CONTROLLER).param(EVENT_DEFINITION_CRF_ID, "1").param(SECTION_ID, "1")
								.param(ITEM_DATA_ID, "1").param(ACTION, "sdv").session(session))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(
						content()
								.string("{\"crf\":\"\",\"totalItemsToSDV\":\"1\",\"item\":\"sdv\",\"itemDataItems\":[{\"itemDataId\":2,\"rowCount\":0,\"itemId\":2}],\"totalSectionItemsToSDV\":\"1\"}"));
	}
}
