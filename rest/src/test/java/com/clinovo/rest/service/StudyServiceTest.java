package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

public class StudyServiceTest extends BaseServiceTest {

	@Test
	public void testThatCreateStudyMethodWorksFine() throws Exception {
		result = this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk()).andReturn();
	}

	@Test
	public void testThatCreateStudyMethodDoesNotSupportHTTPGetMethod() throws Exception {
		this.mockMvc
				.perform(get(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStudyNameParameterIsMissing() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("protocolId", "X_study_1").param("protocolType", "0")
						.param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStudyNameParameterIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStudyNameParameterHasTypo() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("stUdyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolIdParameterIsMissing() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolType", "0")
						.param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolIdParameterIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolIdParameterHasTypo() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("proTocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolTypeParameterIsMissing() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolTypeParameterIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolTypeParameterHasTypo() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("proTocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolTypeParameterHasWrongValue() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "13").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSummaryParameterIsMissing() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSummaryParameterIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSummaryParameterHasTypo() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("sUmmary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfPrincipalInvestigatorParameterIsMissing() throws Exception {
		this.mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("sponsor", "test_study_1")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfPrincipalInvestigatorParameterIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfPrincipalInvestigatorParameterHasTypo() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("princiPalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSponsorParameterIsMissing() throws Exception {
		this.mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSponsorParameterIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSponsorParameterHasTypo() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("spoNsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfuserNameDoesNotExist() throws Exception {
		this.mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("userName", "wrong_userName_x1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudiesWithTheSameStudyName() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "XX_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudiesWithTheSameProtocolId() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "X_test_study_1").param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithStudyNameThatExceeds100Symbols() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", getSymbols(101)).param("protocolId", "X_study_1")
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithProtocolIdThatExceeds30Symbols() throws Exception {
		this.mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", getSymbols(31))
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithCollaboratorsThatExceeds1000Symbols() throws Exception {
		this.mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("collaborators", getSymbols(1001)).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithDescriptionThatExceeds1000Symbols() throws Exception {
		this.mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("description", getSymbols(1001)).accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}
}
