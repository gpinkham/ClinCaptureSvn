package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.junit.Test;
import org.springframework.http.MediaType;

import com.clinovo.enums.StudyFeature;
import com.clinovo.enums.StudyProtocolType;

public class StudyServiceTest extends BaseServiceTest {

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfWrongPurposeIsUsedForInterventionalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "25").param("startDate", "2016-01-20")
				.param("purpose", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfWrongPurposeIsUsedForObservationalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("protocolId", "X_study_1")
				.param("protocolType", "1").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "25").param("startDate", "2016-01-20")
				.param("purpose", "7").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfDurationIsUsedForInterventionalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "25").param("startDate", "2016-01-20")
				.param("duration", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSelectionIsUsedForInterventionalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "25").param("startDate", "2016-01-20")
				.param("selection", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfTimingIsUsedForInterventionalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "25").param("startDate", "2016-01-20")
				.param("timing", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfAllocationIsUsedForObservationalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("protocolId", "X_study_1")
				.param("protocolType", "1").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "25").param("startDate", "2016-01-20")
				.param("allocation", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfMaskingIsUsedForObservationalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("protocolId", "X_study_1")
				.param("protocolType", "1").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "25").param("startDate", "2016-01-20")
				.param("masking", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfControlIsUsedForObservationalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("protocolId", "X_study_1")
				.param("protocolType", "1").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "25").param("startDate", "2016-01-20")
				.param("control", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfAssignmentIsUsedForObservationalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("protocolId", "X_study_1")
				.param("protocolType", "1").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "25").param("startDate", "2016-01-20")
				.param("assignment", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfEndPointUsedForObservationalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("protocolId", "X_study_1")
				.param("protocolType", "1").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "25").param("startDate", "2016-01-20")
				.param("endPoint", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodWorksFine() throws Exception {
		int newTotalEnrollment = 12;
		String newStudyName = "test_study_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		result = mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName)
				.param("protocolId", newProtocolId).param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", Integer.toString(newTotalEnrollment)).param("startDate", "2016-01-20")
				.param("endDate", "2017-01-20").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyBean().getName(), newStudyName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getIdentifier(), newProtocolId);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getExpectedTotalEnrollment(),
					newTotalEnrollment);
			assertNotNull(restOdmContainer.getRestData().getStudyBean().getDatePlannedStart());
			assertNotNull(restOdmContainer.getRestData().getStudyBean().getDatePlannedEnd());
			for (StudyFeature studyFeature : StudyFeature.values()) {
				assertEquals(studyConfigService.getParameter(studyFeature.getName(),
						restOdmContainer.getRestData().getStudyBean().getStudyParameterConfig()), "yes");
			}
		}
	}

	@Test
	public void testThatCreateStudyMethodSetFeaturesCorrectly() throws Exception {
		createNewStudy();
		int newTotalEnrollment = 12;
		String newStudyName = "test_study_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		result = mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName)
				.param("protocolId", newProtocolId).param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", Integer.toString(newTotalEnrollment)).param("startDate", "2016-01-20")
				.param("approvalDate", "2017-01-20").param("crfAnnotation", "no")
				.param("dynamicGroup", "no").param("calendaredVisits", "no").param("interactiveDashboards", "no")
				.param("itemLevelSDV", "no").param("subjectCasebookInPDF", "no").param("crfMasking", "no")
				.param("sasExtracts", "no").param("studyEvaluator", "no").param("randomization", "no")
				.param("medicalCoding", "no").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyBean().getName(), newStudyName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getIdentifier(), newProtocolId);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getExpectedTotalEnrollment(),
					newTotalEnrollment);
			assertNotNull(restOdmContainer.getRestData().getStudyBean().getDatePlannedStart());
			assertNotNull(restOdmContainer.getRestData().getStudyBean().getProtocolDateVerification());
			for (StudyFeature studyFeature : StudyFeature.values()) {
				assertEquals(studyConfigService.getParameter(studyFeature.getName(),
						restOdmContainer.getRestData().getStudyBean().getStudyParameterConfig()), "no");
			}
		}
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfCrfAnnotationHasWrongDate() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2014-02-20")
				.param("crfAnnotation", "xno").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStartDateIsInWrongFormat() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "20-Jan-2014")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolDateVerificationIsInWrongFormat() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.param("approvalDate", "08-Jan-2017").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfEndDateIsInWrongFormat() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.param("endDate", "10-Jan-2016").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodDoesNotSupportHTTPGetMethod() throws Exception {
		mockMvc.perform(get(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfTotalEnrollmentParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("startDate", "2016-01-20").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfTotalEnrollmentParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfTotalEnrollmentParameterHasWrongData() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "blabla").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfTotalEnrollmentParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("toTalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStartDateParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStartDateParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStartDateParameterHasWrongData() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "blabla")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStartDateParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("stArtDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStudyNameParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("protocolId", "X_study_1").param("protocolType", "0")
				.param("summary", "bla bla").param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", "1").param("startDate", "2016-01-20").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStudyNameParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStudyNameParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("stUdyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolType", "0")
				.param("summary", "bla bla").param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", "1").param("startDate", "2016-01-20").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolIdParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("proTocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolTypeParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("summary", "bla bla").param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", "1").param("startDate", "2016-01-20").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolTypeParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolTypeParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("proTocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolTypeParameterHasWrongValue() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "13").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSummaryParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", "1").param("startDate", "2016-01-20").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSummaryParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSummaryParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("sUmmary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfPrincipalInvestigatorParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("sponsor", "test_study_1")
				.param("totalEnrollment", "1").param("startDate", "2016-01-20").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfPrincipalInvestigatorParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfPrincipalInvestigatorParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("princiPalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSponsorParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("totalEnrollment", "1").param("startDate", "2016-01-20").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSponsorParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "").param("totalEnrollment", "1").param("startDate", "2016-01-20").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSponsorParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("spoNsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfuserNameDoesNotExist() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.param("userName", "wrong_userName_x1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudiesWithTheSameStudyName() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "XX_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudiesWithTheSameProtocolId() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "X_test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithStudyNameThatExceeds100Symbols() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", getSymbols(101)).param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithProtocolIdThatExceeds30Symbols() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", getSymbols(31))
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithCollaboratorsThatExceeds1000Symbols() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("collaborators", getSymbols(1001)).param("totalEnrollment", "1")
				.param("startDate", "2016-01-20").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithDescriptionThatExceeds1000Symbols() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("description", getSymbols(1001)).param("totalEnrollment", "1")
				.param("startDate", "2016-01-20").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfUserNameHasSiteLevelRoles() throws Exception {
		createNewSite(currentScope.getId());
		createNewUser(newSite, UserType.USER, Role.CLINICAL_RESEARCH_COORDINATOR);
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20")
				.param("userName", newUser.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodDoesNotSupportHTTPGetMethod() throws Exception {
		mockMvc.perform(get(API_STUDY_EDIT).param("studyId", "1").param("studyName", "test_study_1").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatAtLeastOneNotRequiredParameterShouldBeSpecifiedForEditStudyMethod() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfTotalEnrollmentParameterIsMissing() throws Exception {
		mockMvc.perform(
				post(API_STUDY_EDIT).param("studyName", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfTotalEnrollmentParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "").param("studyName", "test_study_1").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfTotalEnrollmentParameterHasWrongData() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "blabla").param("studyName", "test_study_1")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfTotalEnrollmentParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("stUdyId", "1").param("studyName", "test_study_1").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfStudyDoesNotExist() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "99991").param("studyName", "test_study_1")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditASiteUsingTheEditStudyMethod() throws Exception {
		createNewSite(currentScope.getId());
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(newSite.getId()))
				.param("studyName", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyAdministratorWithoutRightsCannotEditStudy() throws Exception {
		int studyId = currentScope.getId();
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		createNewUser(newStudy, UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(studyId))
				.param("studyName", "test_study_1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatSystemAdministratorCanEditAnyStudy() throws Exception {
		createNewStudy();
		int newTotalEnrollment = 15;
		String newStudyName = "test_study_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		result = mockMvc.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(newStudy.getId()))
				.param("studyName", newStudyName).param("protocolId", newProtocolId).param("summary", "bla bla")
				.param("principalInvestigator", "test").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("endDate", "2016-04-20").param("crfAnnotation", "yes").param("dynamicGroup", "yes")
				.param("calendaredVisits", "yes").param("interactiveDashboards", "yes").param("itemLevelSDV", "yes")
				.param("subjectCasebookInPDF", "yes").param("crfMasking", "yes").param("sasExtracts", "yes")
				.param("studyEvaluator", "yes").param("randomization", "yes").param("medicalCoding", "yes")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyBean().getName(), newStudyName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getIdentifier(), newProtocolId);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getExpectedTotalEnrollment(),
					newTotalEnrollment);
			assertNotNull(restOdmContainer.getRestData().getStudyBean().getDatePlannedEnd());
			for (StudyFeature studyFeature : StudyFeature.values()) {
				assertEquals(studyConfigService.getParameter(studyFeature.getName(),
						restOdmContainer.getRestData().getStudyBean().getStudyParameterConfig()), "yes");
			}
		}
	}

	@Test
	public void testThatEditStudyMethodSetFeaturesCorrectly() throws Exception {
		createNewStudy();
		int newTotalEnrollment = 12;
		String newStudyName = "test_study_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		result = mockMvc.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(newStudy.getId()))
				.param("studyName", newStudyName).param("protocolId", newProtocolId).param("summary", "bla bla")
				.param("principalInvestigator", "test").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("startDate", "2016-04-20").param("approvalDate", "2017-02-10")
				.param("crfAnnotation", "no").param("dynamicGroup", "no").param("calendaredVisits", "no")
				.param("interactiveDashboards", "no").param("itemLevelSDV", "no").param("subjectCasebookInPDF", "no")
				.param("crfMasking", "no").param("sasExtracts", "no").param("studyEvaluator", "no")
				.param("randomization", "no").param("medicalCoding", "no").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyBean().getName(), newStudyName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getIdentifier(), newProtocolId);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getExpectedTotalEnrollment(),
					newTotalEnrollment);
			assertNotNull(restOdmContainer.getRestData().getStudyBean().getProtocolDateVerification());
			assertNotNull(restOdmContainer.getRestData().getStudyBean().getDatePlannedStart());
			for (StudyFeature studyFeature : StudyFeature.values()) {
				assertEquals(studyConfigService.getParameter(studyFeature.getName(),
						restOdmContainer.getRestData().getStudyBean().getStudyParameterConfig()), "no");
			}
		}
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfStartDateIsInWrongFormat() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("startDate", "01-Jan-2016").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfProtocolDateVerificationIsInWrongFormat() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("approvalDate", "01-Jan-2016")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfEndDateIsInWrongFormat() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("endDate", "01-Jan-2016").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfCrfAnnotationHasWrongDate() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("crfAnnotation", "01-Jan-2016")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfWrongPurposeIsUsedForInterventionalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "0").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("purpose", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfWrongPurposeIsUsedForObservationalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "1").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("purpose", "7").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfDurationIsUsedForInterventionalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "0").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("duration", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfSelectionIsUsedForInterventionalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "0").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("selection", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfTimingIsUsedForInterventionalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "0").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("timing", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfAllocationIsUsedForObservationalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "1").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("allocation", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfMaskingIsUsedForObservationalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "1").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("masking", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfControlIsUsedForObservationalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "1").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("control", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfAssignmentIsUsedForObservationalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "1").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("assignment", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfEndPointUsedForObservationalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "1").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("endPoint", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToEditStudyProtocolType() throws Exception {
		int newTotalEnrollment = 12;
		String newStudyName = "test_study_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName).param("protocolId", newProtocolId)
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("startDate", "2016-01-20").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		StudyBean studyBean = (StudyBean) new StudyDAO(dataSource).findByName(newStudyName);
		assertEquals(studyBean.getProtocolTypeKey(), StudyProtocolType.INTERVENTIONAL.getValue());
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(studyBean.getId()))
				.param("protocolType", "1").accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		studyBean = (StudyBean) new StudyDAO(dataSource).findByName(newStudyName);
		assertEquals(studyBean.getProtocolTypeKey(), StudyProtocolType.OBSERVATIONAL.getValue());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudiesWithIdenticalStudyNames() throws Exception {
		String newStudyName = "test_study_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName).param("protocolId", newProtocolId)
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "12").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("studyName", newStudyName)
				.param("summary", "bla bla").param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", "12").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudiesWithIdenticalProtocolIds() throws Exception {
		String newStudyName = "test_study_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName).param("protocolId", newProtocolId)
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "12").param("startDate", "2016-01-20")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolId", newProtocolId)
				.param("summary", "bla bla").param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", "12").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}
}
