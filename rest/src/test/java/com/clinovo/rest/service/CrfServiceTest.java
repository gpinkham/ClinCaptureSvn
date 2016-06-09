package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
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
		UserAccountBean user = (UserAccountBean) userAccountDAO.findByPK(1);
		CRFBean crfBean = (CRFBean) crfdao.findByNameAndStudy(CRF_NAME, defaultStudy);
		if (crfBean != null && crfBean.getId() > 0) {
			try {
				deleteCrfService.deleteCrf(crfBean, user, Locale.ENGLISH, false);
			} catch (Exception ex) {
				//
			}
		}
		super.after();
	}

	@Test
	public void testThatImportCrfServiceWorksFine() throws Exception {
		mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsonData", getJsonData("testCrf.json")))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatImportCrfServiceDoesNotAllowToImportSameCrfTwiceInTheSameStudy() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsonData", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true)).andExpect(status().isOk());
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsonData", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfServiceDoesNotAllowToImportSameCrfTwiceInTheSameStudyEvenIfVersionIsDifferent() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsonData", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true)).andExpect(status().isOk());
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("version", "v2.0");
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsonData", jsonObject.toString()).accept(mediaType)
				.secure(true)).andExpect(status().isInternalServerError());
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
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByFullNameAndStudy(newCrfVersion, CRF_NAME,
				defaultStudy);
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

	@Test
	public void testThatCrfsMethodDoesNotSupportPostMethod() throws Exception {
		mockMvc.perform(post(API_CRFS)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCrfsMethodWorksFine() throws Exception {
		mockMvc.perform(get(API_CRFS)).andExpect(status().isOk());
	}

	@Test
	public void testThatCrfVersionsMethodDoesNotSupportPostMethod() throws Exception {
		mockMvc.perform(post(API_CRF_VERSIONS)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCrfVersionsMethodWorksFine() throws Exception {
		mockMvc.perform(get(API_CRF_VERSIONS)).andExpect(status().isOk());
	}

	@Test
	public void testThatCrfMethodDoesNotSupportPostMethod() throws Exception {
		mockMvc.perform(post(API_CRF).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCrfMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(get(API_CRF)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCrfMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(get(API_CRF).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCrfMethodThrowsExceptionIfCrfDoesNotExist() throws Exception {
		mockMvc.perform(get(API_CRF).param("id", "34234234")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCrfMethodWorksFine() throws Exception {
		mockMvc.perform(get(API_CRF).param("id", "1")).andExpect(status().isOk());
	}

	@Test
	public void testThatCrfVersionMethodDoesNotSupportPostMethod() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCrfVersionMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(get(API_CRF_VERSION)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCrfVersionMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(get(API_CRF_VERSION).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCrfVersionMethodThrowsExceptionIfCrfVersionDoesNotExist() throws Exception {
		mockMvc.perform(get(API_CRF_VERSION).param("id", "34234234")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCrfVersionMethodWorksFine() throws Exception {
		mockMvc.perform(get(API_CRF_VERSION).param("id", "1")).andExpect(status().isOk());
	}

	@Test
	public void testThatRemoveCrfMethodDoesNotSupportGetMethod() throws Exception {
		mockMvc.perform(get(API_CRF_REMOVE).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_CRF_REMOVE)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_CRF_REMOVE).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfCrfVersionDoesNotExist() throws Exception {
		mockMvc.perform(post(API_CRF_REMOVE).param("id", "34234234")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveCrfMethodWorksFine() throws Exception {
		mockMvc.perform(post(API_CRF_REMOVE).param("id", "1")).andExpect(status().isOk());
	}

	@Test
	public void testThatRemoveCrfVersionMethodDoesNotSupportGetMethod() throws Exception {
		mockMvc.perform(get(API_CRF_VERSION_REMOVE).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveCrfVersionMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_REMOVE)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfVersionMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_REMOVE).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfVersionMethodThrowsExceptionIfCrfVersionDoesNotExist() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_REMOVE).param("id", "34234234"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveCrfVersionMethodWorksFine() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_REMOVE).param("id", "1")).andExpect(status().isOk());
	}

	@Test
	public void testThatRestoreCrfMethodDoesNotSupportGetMethod() throws Exception {
		mockMvc.perform(get(API_CRF_RESTORE).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_CRF_RESTORE)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_CRF_RESTORE).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfCrfVersionDoesNotExist() throws Exception {
		mockMvc.perform(post(API_CRF_RESTORE).param("id", "34234234")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreCrfMethodWorksFine() throws Exception {
		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.setId(1);
		CRFDAO crfDao = new CRFDAO(dataSource);
		CRFBean crfBean = (CRFBean) crfDao.findByPK(1);
		crfBean.setUpdater(userAccountBean);
		crfBean.setStatus(Status.DELETED);
		crfDao.update(crfBean);
		assertTrue(crfBean.getStatus().isDeleted());
		mockMvc.perform(post(API_CRF_RESTORE).param("id", "1")).andExpect(status().isOk());
		crfBean = (CRFBean) crfDao.findByPK(1);
		assertTrue(crfBean.getStatus().isAvailable());
	}

	@Test
	public void testThatRestoreCrfVersionMethodDoesNotSupportGetMethod() throws Exception {
		mockMvc.perform(get(API_CRF_VERSION_RESTORE).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreCrfVersionMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_RESTORE)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfVersionMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_RESTORE).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfVersionMethodThrowsExceptionIfCrfVersionDoesNotExist() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_RESTORE).param("id", "34234234"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreCrfVersionMethodWorksFine() throws Exception {
		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.setId(1);
		CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(1);
		crfVersionBean.setUpdater(userAccountBean);
		crfVersionBean.setStatus(Status.DELETED);
		crfVersionDao.update(crfVersionBean);
		assertTrue(crfVersionBean.getStatus().isDeleted());
		mockMvc.perform(post(API_CRF_VERSION_RESTORE).param("id", "1")).andExpect(status().isOk());
		crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(1);
		assertTrue(crfVersionBean.getStatus().isAvailable());
	}

	@Test
	public void testThatLockCrfVersionMethodDoesNotSupportGetMethod() throws Exception {
		mockMvc.perform(get(API_CRF_VERSION_LOCK).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatLockCrfVersionMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_LOCK)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatLockCrfVersionMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_LOCK).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatLockCrfVersionMethodThrowsExceptionIfCrfVersionDoesNotExist() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_LOCK).param("id", "34234234")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatLockCrfVersionMethodWorksFine() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_LOCK).param("id", "1")).andExpect(status().isOk());
	}

	@Test
	public void testThatUnLockCrfVersionMethodDoesNotSupportGetMethod() throws Exception {
		mockMvc.perform(get(API_CRF_VERSION_UNLOCK).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatUnLockCrfVersionMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_UNLOCK)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatUnLockCrfVersionMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_UNLOCK).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatUnLockCrfVersionMethodThrowsExceptionIfCrfVersionDoesNotExist() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_UNLOCK).param("id", "34234234"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatUnLockCrfVersionMethodWorksFine() throws Exception {
		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.setId(1);
		CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(1);
		crfVersionBean.setUpdater(userAccountBean);
		crfVersionBean.setStatus(Status.LOCKED);
		crfVersionDao.update(crfVersionBean);
		assertTrue(crfVersionBean.getStatus().isLocked());
		mockMvc.perform(post(API_CRF_VERSION_UNLOCK).param("id", "1")).andExpect(status().isOk());
		crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(1);
		assertTrue(crfVersionBean.getStatus().isAvailable());
	}

	@Test
	public void testThatDeleteCrfMethodDoesNotSupportGetMethod() throws Exception {
		mockMvc.perform(get(API_CRF_DELETE).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatDeleteCrfMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_CRF_DELETE)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatDeleteCrfMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_CRF_DELETE).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatDeleteCrfMethodThrowsExceptionIfIdParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_CRF_DELETE).param("iD", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatDeleteCrfMethodThrowsExceptionIfForceParameterHasWrongValue() throws Exception {
		mockMvc.perform(post(API_CRF_DELETE).param("id", "1").param("force", "123")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatDeleteCrfMethodThrowsExceptionIfForceParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_CRF_DELETE).param("id", "1").param("foRce", "true"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfItHasRelatedData() throws Exception {
		mockMvc.perform(post(API_CRF_DELETE).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatDeleteCrfMethodThrowsExceptionIfCrfDoesNotExist() throws Exception {
		mockMvc.perform(post(API_CRF_DELETE).param("id", "34234234")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatDeleteCrfVersionMethodDoesNotSupportGetMethod() throws Exception {
		mockMvc.perform(get(API_CRF_VERSION_DELETE).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatDeleteCrfVersionMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_DELETE)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatDeleteCrfVersionMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_DELETE).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatDeleteCrfVersionMethodThrowsExceptionIfIdParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_DELETE).param("iD", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatDeleteCrfVersionMethodThrowsExceptionIfForceParameterHasWrongValue() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_DELETE).param("id", "1").param("force", "123"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatDeleteCrfVersionMethodThrowsExceptionIfForceParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_DELETE).param("id", "1").param("foRce", "true"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfVersionIfItHasRelatedData() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_DELETE).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatDeleteCrfVersionMethodThrowsExceptionIfCrfVersionDoesNotExist() throws Exception {
		mockMvc.perform(post(API_CRF_VERSION_DELETE).param("id", "34234234"))
				.andExpect(status().isInternalServerError());
	}
}
