package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SDVItemControllerTest extends BaseControllerTest {

	public static final String ACTION = "action";
	public static final String ITEM_DATA_ID = "itemDataId";

	@Before
	public void before() throws Exception {
		session.setAttribute(BaseController.USER_BEAN_NAME, new UserAccountDAO(dataSource).findByPK(1));
	}

	@Test
	public void testThatGetRequestReturnsJsonObject() throws Exception {
		this.mockMvc.perform(get(SDV_ITEM_CONTROLLER).param(ITEM_DATA_ID, "1").param(ACTION, "sdv").session(session))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string("{\"crf\":\"sdv\",\"item\":\"sdv\",\"itemsToSDV\":\"0\"}"));
	}
}
