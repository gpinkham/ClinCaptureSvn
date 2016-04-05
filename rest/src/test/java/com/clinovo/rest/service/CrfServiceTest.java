package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultMatcher;

@SuppressWarnings({"unused"})
public class CrfServiceTest extends BaseServiceTest {

	// these fields should be the same as in the data/json/testCrf.json & data/excel/testCrf.xls
	private static final String CRF_VERSION = "v1.0";
	private static final String CRF_NAME = "FS Test CRF";

	@After
	public void after() {
		CRFBean crfBean = (CRFBean) crfdao.findByName(CRF_NAME);
		if (crfBean != null && crfBean.getId() > 0) {
			deleteCrfService.deleteCrf(crfBean.getId());
		}
		super.after();
	}

	@Test
	public void testThatImportCrfServiceWorksFine() throws Exception {
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsonData", getJsonData("testCrf.json")))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatImportCrfVersionServiceWorksFine() throws Exception {
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsonData", getJsonData("testCrf.json")))
				.andExpect(status().isOk());
		String newCrfVersion = "v2.0";
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("version", newCrfVersion);
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsonData", jsonObject.toString()))
				.andExpect(status().isOk());
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByFullName(newCrfVersion, CRF_NAME);
		assertTrue(crfVersionBean.getId() > 0);
	}

	@Test
	public void testThatImportCrfVersionServiceDoesNotAllowToImportSameCrfVersionTwice() throws Exception {
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsonData", getJsonData("testCrf.json")))
				.andExpect(status().isOk());
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsonData", jsonObject.toString()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceDoesNotAllowToImportCrfVersionIfCrfDoesNotExist() throws Exception {
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsonData", getJsonData("testCrf.json")))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceThrowsExceptionIfJsonDataIsEmpty() throws Exception {
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsonData", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatImportCrfVersionServiceThrowsExceptionIfJsonDataIsWrongData() throws Exception {
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsonData", "wrong data"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceThrowsExceptionIfJsonDataIsMissing() throws Exception {
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatImportCrfServiceThrowsExceptionIfJsonDataIsEmpty() throws Exception {
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsonData", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatImportCrfServiceThrowsExceptionIfJsonDataIsWrongData() throws Exception {
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsonData", "wrong data"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfServiceThrowsExceptionIfJsonDataIsMissing() throws Exception {
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatImportCrfVersionServiceThrowsExceptionIfCrfNameIsEmpty() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("name", "");
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsonData", jsonObject.toString()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfServiceDoesNotSupportHttpGet() throws Exception {
		mockMvc.perform(get(API_CRF_JSON_IMPORT_CRF)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceDoesNotSupportHttpGet() throws Exception {
		mockMvc.perform(get(API_CRF_JSON_IMPORT_CRF_VERSION)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyAdministratorWithAdministrativePrivilegesIsAbleToCallCRFAPI() throws Exception {
		ResultMatcher expectStatus = status().isOk();
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(),
				currentScope.getName());
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsonData", getJsonData("testCrf.json")))
				.andExpect(expectStatus);
		String newCrfVersion = "v2.0";
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("version", newCrfVersion);
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsonData", jsonObject.toString()))
				.andExpect(expectStatus);
	}
}
