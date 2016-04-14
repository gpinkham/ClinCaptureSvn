package com.clinovo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyFeatureConfig;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.json.JSONObject;
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
		StudyBean currentStudy = (StudyBean) new StudyDAO(dataSource).findByPK(1);
		currentStudy.setStudyFeatureConfig(new StudyFeatureConfig());
		session.setAttribute(SpringController.USER_BEAN_NAME, new UserAccountDAO(dataSource).findByPK(1));
		session.setAttribute(SpringController.STUDY, currentStudy);
	}

	@Test
	public void testThatGetRequestReturnsJsonObject() throws Exception {
		JSONObject jsonObject = new JSONObject(mockMvc
				.perform(get(SDV_ITEM_CONTROLLER).param(EVENT_DEFINITION_CRF_ID, "1").param(SECTION_ID, "1")
						.param(ITEM_DATA_ID, "1").param(ACTION, "sdv").session(session))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andReturn().getResponse().getContentAsString());
		assertEquals(jsonObject.getString("crf"), "");
		assertEquals(jsonObject.getString("item"), "sdv");
		assertEquals(jsonObject.getString("totalItemsToSDV"), "1");
		assertEquals(jsonObject.getString("totalSectionItemsToSDV"), "1");
		assertEquals(((JSONObject) jsonObject.getJSONArray("itemDataItems").get(0)).getString("itemDataId"), "2");
		assertEquals(((JSONObject) jsonObject.getJSONArray("itemDataItems").get(0)).getString("rowCount"), "0");
		assertEquals(((JSONObject) jsonObject.getJSONArray("itemDataItems").get(0)).getString("itemId"), "2");
	}
}
