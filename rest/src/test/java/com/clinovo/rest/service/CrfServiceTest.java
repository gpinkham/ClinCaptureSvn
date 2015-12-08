package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
	}

	@Test
	public void testThatImportCrfServiceDoesNotAllowToImportSameCrfTwice() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfServiceDoesNotAllowToImportSameCrfTwiceEvenIfVersionIsDifferent() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("version", "v2.0");
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", jsonObject.toString()).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceWorksFine() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		String newCrfVersion = "v2.0";
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("version", newCrfVersion);
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", jsonObject.toString())
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByFullName(newCrfVersion, CRF_NAME);
		assertTrue(crfVersionBean.getId() > 0);
	}

	@Test
	public void testThatImportCrfVersionServiceDoesNotAllowToImportSameCrfVersionTwice() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", jsonObject.toString())
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceDoesNotAllowToImportCrfVersionIfCrfDoesNotExist() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceThrowsExceptionIfJsonDataIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", "").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatImportCrfVersionServiceThrowsExceptionIfJsonDataIsWrongData() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", "wrong data").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceThrowsExceptionIfJsonDataIsMissing() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatImportCrfServiceThrowsExceptionIfJsonDataIsEmpty() throws Exception {
		this.mockMvc.perform(
				post(API_CRF_JSON_IMPORT_CRF).param("jsondata", "").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatImportCrfServiceThrowsExceptionIfJsonDataIsWrongData() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", "wrong data").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfServiceThrowsExceptionIfJsonDataIsMissing() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatImportCrfVersionServiceThrowsExceptionIfCrfNameIsEmpty() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("name", "");
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", jsonObject.toString())
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfServiceDoesNotSupportHttpGet() throws Exception {
		this.mockMvc.perform(get(API_CRF_JSON_IMPORT_CRF).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceDoesNotSupportHttpGet() throws Exception {
		this.mockMvc.perform(get(API_CRF_JSON_IMPORT_CRF_VERSION).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyAdministratorWithAdministrativePrivilegesIsAbleToCallCRFAPI() throws Exception {
		ResultMatcher expectStatus = status().isOk();
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), studyBean.getName());
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
		String newCrfVersion = "v2.0";
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("version", newCrfVersion);
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", jsonObject.toString())
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
	}
}
