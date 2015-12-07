package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import com.clinovo.bean.display.DisplayItemLevelSDVRow;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Item Level SDV Controller Test.
 */
public class ItemLevelSDVControllerTest extends BaseControllerTest {

	public static final String CONFIGURE_ITEM_SDV = "/configureItemLevelSDV";
	public static final String CONFIGURE_ITEM_SDV_PAGE = "managestudy/itemLevelSDV";
	public static final String ITEM_SDV_CRF_VERSION_TABLE = "include/itemLevelSDVCrfVersionTable";

	private String jsonData;

	protected MockHttpServletRequest request = new MockHttpServletRequest();

	@Before
	public void prepare() {
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();
		object.put("inputsData", array);
		jsonData = object.toString();
	}

	@Test
	public void testThatInitConfigurePageReturnsCorrectStatus() throws Exception {
		this.mockMvc.perform(get(CONFIGURE_ITEM_SDV).param("edcId", "1")).andExpect(status().isOk());
	}

	@Test
	public void testThatInitConfigurePageReturnsCorrectPageName() throws Exception {
		this.mockMvc.perform(get(CONFIGURE_ITEM_SDV).param("edcId", "1")).andExpect(MockMvcResultMatchers.view()
				.name(CONFIGURE_ITEM_SDV_PAGE));
	}

	@Test
	public void testThatInitConfigurePageReturnsCorrectNumberOfAttributes() throws Exception {
		this.mockMvc.perform(get(CONFIGURE_ITEM_SDV).param("edcId", "1")).andExpect(MockMvcResultMatchers.model().size(4));
	}

	@Test
	public void testThatInitConfigurePageReturnsCorrectNamesOfAttributes() throws Exception {
		this.mockMvc.perform(get(CONFIGURE_ITEM_SDV).param("edcId", "1"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("studyEventDefinition", "crf", "edcId", "versionsList"));
	}

	@Test
	public void testThatInitConfigurePageReturnsCorrectStudyEventDefinition() throws Exception {
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1);
		MvcResult result = this.mockMvc.perform(get(CONFIGURE_ITEM_SDV).param("edcId", "1")).andReturn();
		StudyEventDefinitionBean studyEventDefinitionBeanFromRequest = (StudyEventDefinitionBean) result
				.getModelAndView().getModel().get("studyEventDefinition");
		assertEquals(studyEventDefinitionBean.getName(), studyEventDefinitionBeanFromRequest.getName());
		assertEquals(studyEventDefinitionBean.getType(), studyEventDefinitionBeanFromRequest.getType());
		assertEquals(studyEventDefinitionBean.getDescription(), studyEventDefinitionBeanFromRequest.getDescription());
	}

	@Test
	public void testThatInitConfigurePageReturnsCorrectCRF() throws Exception {
		CRFDAO crfdao = new CRFDAO(dataSource);
		CRFBean crfBean = (CRFBean) crfdao.findByPK(1);
		MvcResult result = this.mockMvc.perform(get(CONFIGURE_ITEM_SDV).param("edcId", "1")).andReturn();
		CRFBean crfFromRequest = (CRFBean) result.getModelAndView().getModel().get("crf");
		assertEquals(crfBean.getName(), crfFromRequest.getName());
		assertEquals(crfBean.getDescription(), crfFromRequest.getDescription());
		assertTrue(crfFromRequest.getId() == crfBean.getId());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatInitConfigurePageReturnsCorrectCRFVersionsList() throws Exception {
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(dataSource);
		ArrayList<CRFVersionBean> crfVersionBeans = (ArrayList<CRFVersionBean>) crfVersionDAO.findAllByCRF(1);
		MvcResult result = this.mockMvc.perform(get(CONFIGURE_ITEM_SDV).param("edcId", "1")).andReturn();
		ArrayList<CRFVersionBean> crfVersionsFromRequest = (ArrayList<CRFVersionBean>) result.getModelAndView().getModel().get("versionsList");
		assertTrue(crfVersionsFromRequest.size() != 0);
		assertEquals(crfVersionBeans.size(), crfVersionsFromRequest.size());
	}

	@Test
	public void testThatGetPageContentForVersionReturnsCorrectStatus() throws Exception {
		this.mockMvc.perform(post(CONFIGURE_ITEM_SDV).param("edcId", "1").param("crfVersionId", "1"))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatGetPageContentForVersionReturnsCorrectPageName() throws Exception {
		this.mockMvc.perform(post(CONFIGURE_ITEM_SDV).param("edcId", "1").param("crfVersionId", "1"))
				.andExpect(MockMvcResultMatchers.view()
						.name(ITEM_SDV_CRF_VERSION_TABLE));
	}

	@Test
	public void testThatGetPageContentForVersionReturnsCorrectNumberOfAttributes() throws Exception {
		this.mockMvc.perform(post(CONFIGURE_ITEM_SDV).param("edcId", "1").param("crfVersionId", "1"))
				.andExpect(MockMvcResultMatchers.model().size(2));
	}

	@Test
	public void testThatGetPageContentForVersionReturnsCorrectNamesOfAttributes() throws Exception {
		this.mockMvc.perform(post(CONFIGURE_ITEM_SDV).param("edcId", "1").param("crfVersionId", "1"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("sdvMetadataBySection", "crfVersionId"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatGetPageContentForVersionReturnsCorrectSDVMetadataMapSize() throws Exception {
		MvcResult result = this.mockMvc.perform(post(CONFIGURE_ITEM_SDV).param("edcId", "1").param("crfVersionId", "1")).andReturn();
		HashMap<SectionBean, ArrayList<DisplayItemLevelSDVRow>> sdvMetadataBySection =
				(HashMap<SectionBean, ArrayList<DisplayItemLevelSDVRow>>) result
						.getModelAndView().getModel().get("sdvMetadataBySection");
		assertEquals(3, sdvMetadataBySection.size());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatGetPageContentForVersionReturnsCorrectNumberOfSDVRequiredItems() throws Exception {
		MvcResult result = this.mockMvc.perform(post(CONFIGURE_ITEM_SDV).param("edcId", "1").param("crfVersionId", "1")).andReturn();
		HashMap<SectionBean, ArrayList<DisplayItemLevelSDVRow>> sdvMetadataBySection =
				(HashMap<SectionBean, ArrayList<DisplayItemLevelSDVRow>>) result
						.getModelAndView().getModel().get("sdvMetadataBySection");
		int countRequired = 0;
		for (Map.Entry<SectionBean, ArrayList<DisplayItemLevelSDVRow>> entry : sdvMetadataBySection.entrySet()) {
			for (DisplayItemLevelSDVRow row : entry.getValue()) {
				if (row.getEdcItemMetadata().sdvRequired()) {
					countRequired++;
				}
			}
		}
		assertEquals(2, countRequired);
	}

	@Test
	public void testThatSaveConfigurationReturnsCorrectStatus() throws Exception {
		this.mockMvc.perform(post(CONFIGURE_ITEM_SDV).param("edcId", "1").param("versionId", "1").param("submit", "true")
				.param("jsonData", jsonData)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testThatSaveConfigurationReturnsCorrectResponse() throws Exception {
		this.mockMvc.perform(post(CONFIGURE_ITEM_SDV).param("edcId", "1").param("versionId", "1").param("submit", "true")
				.param("jsonData", jsonData)).andExpect(MockMvcResultMatchers.content().string("success"));
	}
}