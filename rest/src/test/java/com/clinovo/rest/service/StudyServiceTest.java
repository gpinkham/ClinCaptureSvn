package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.junit.Test;
import org.springframework.http.MediaType;

import com.clinovo.enums.study.StudyFeature;
import com.clinovo.enums.study.StudyOrigin;
import com.clinovo.enums.study.StudyProtocolType;

public class StudyServiceTest extends BaseServiceTest {

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfWrongPurposeIsUsedForInterventionalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("briefTitle", "test_study")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "25")
				.param("startDate", "2016-01-20").param("purpose", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfWrongPurposeIsUsedForObservationalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("briefTitle", "test_study")
				.param("protocolId", "X_study_1").param("protocolType", "1").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "25")
				.param("startDate", "2016-01-20").param("purpose", "7")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfDurationIsUsedForInterventionalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("briefTitle", "test_study")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "25")
				.param("startDate", "2016-01-20").param("duration", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSelectionIsUsedForInterventionalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("briefTitle", "test_study")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "25")
				.param("startDate", "2016-01-20").param("selection", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfTimingIsUsedForInterventionalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("briefTitle", "test_study")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "25")
				.param("startDate", "2016-01-20").param("timing", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfAllocationIsUsedForObservationalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("briefTitle", "test_study")
				.param("protocolId", "X_study_1").param("protocolType", "1").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "25")
				.param("startDate", "2016-01-20").param("allocation", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfMaskingIsUsedForObservationalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("briefTitle", "test_study")
				.param("protocolId", "X_study_1").param("protocolType", "1").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "25")
				.param("startDate", "2016-01-20").param("masking", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfControlIsUsedForObservationalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("briefTitle", "test_study")
				.param("protocolId", "X_study_1").param("protocolType", "1").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "25")
				.param("startDate", "2016-01-20").param("control", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfAssignmentIsUsedForObservationalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("briefTitle", "test_study")
				.param("protocolId", "X_study_1").param("protocolType", "1").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "25")
				.param("startDate", "2016-01-20").param("assignment", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfEndPointUsedForObservationalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study").param("briefTitle", "test_study")
				.param("protocolId", "X_study_1").param("protocolType", "1").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "25")
				.param("startDate", "2016-01-20").param("endPoint", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfFacilityNameParameterHasATypo() throws Exception {
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName).param("protocolId", newProtocolId)
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("startDate", "2016-01-20").param("endDate", "2017-01-20").param("fAcilityName", "bla bla"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfFacilityContactEmailParameterHasWrongValue()
			throws Exception {
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName).param("protocolId", newProtocolId)
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("startDate", "2016-01-20").param("endDate", "2017-01-20")
				.param("facilityContactEmail", "blaX!#@")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSubjectPersonIdRequiredHasATypo() throws Exception {
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName).param("protocolId", newProtocolId)
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("startDate", "2016-01-20").param("endDate", "2017-01-20")
				.param("subjectPersONIdRequired", "copyFromSSID")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSubjectPersonIdRequiredHasWrongValue() throws Exception {
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName).param("protocolId", newProtocolId)
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("startDate", "2016-01-20").param("endDate", "2017-01-20")
				.param("subjectPersonIdRequired", "xxx")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodIsAbleToSetSubjectPersonIdRequiredParameter() throws Exception {
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName).param("protocolId", newProtocolId)
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("startDate", "2016-01-20").param("endDate", "2017-01-20")
				.param("subjectPersonIdRequired", "copyFromSSID")).andExpect(status().isOk());
		StudyBean studyBean = (StudyBean) new StudyDAO(dataSource).findByName(newStudyName);
		studyConfigService.setParametersForStudy(studyBean);
		studyBean.getStudyParameterConfig().getSubjectPersonIdRequired().equals("copyFromSSID");
	}

	@Test
	public void testThatCreateStudyMethodWorksFine() throws Exception {
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		result = mockMvc
				.perform(post(API_STUDY_CREATE).param("studyName", newStudyName).param("protocolId", newProtocolId)
						.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
						.param("sponsor", "test_study_1").param("totalEnrollment", Integer.toString(newTotalEnrollment))
						.param("startDate", "2016-01-20").param("endDate", "2017-01-20"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyBean().getName(), newStudyName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getBriefTitle(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getOrigin(), StudyOrigin.STUDIO.getName());
			assertEquals(restOdmContainer.getRestData().getStudyBean().getIdentifier(), newProtocolId);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getExpectedTotalEnrollment(),
					newTotalEnrollment);
			assertNotNull(restOdmContainer.getRestData().getStudyBean().getDatePlannedStart());
			assertNotNull(restOdmContainer.getRestData().getStudyBean().getDatePlannedEnd());
			for (StudyFeature studyFeature : StudyFeature.values()) {
				assertEquals(studyConfigService.getParameter(studyFeature.getName(),
						restOdmContainer.getRestData().getStudyBean().getStudyParameterConfig()), "yes");
			}
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityName(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityCity(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityState(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityZip(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityCountry(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactName(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactDegree(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactPhone(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactEmail(), "");
		}
	}

	@Test
	public void testThatCreateStudyMethodisAbleToSetFacilityParametersCorrectly() throws Exception {
		int newTotalEnrollment = 12;
		String facilityName = "NPMedic";
		String facilityCity = "Austin";
		String facilityState = "TX";
		String facilityZip = "54567";
		String facilityCountry = "USA";
		String facilityContactName = "Dr. Tony Kane";
		String facilityContactDegree = "MD";
		String facilityContactPhone = "(843) 678-2390";
		String facilityContactEmail = "tony.kane@npmedic.com";
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		result = mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName)
				.param("protocolId", newProtocolId).param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", Integer.toString(newTotalEnrollment)).param("startDate", "2016-01-20")
				.param("endDate", "2017-01-20").param("facilityName", facilityName).param("facilityCity", facilityCity)
				.param("facilityState", facilityState).param("facilityZip", facilityZip)
				.param("facilityCountry", facilityCountry).param("facilityContactName", facilityContactName)
				.param("facilityContactDegree", facilityContactDegree)
				.param("facilityContactPhone", facilityContactPhone)
				.param("facilityContactEmail", facilityContactEmail)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityName(), facilityName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityCity(), facilityCity);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityState(), facilityState);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityZip(), facilityZip);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityCountry(), facilityCountry);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactName(), facilityContactName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactDegree(),
					facilityContactDegree);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactPhone(), facilityContactPhone);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactEmail(), facilityContactEmail);
		}
		StudyBean studyBean = (StudyBean) new StudyDAO(dataSource).findByName(newStudyName);
		assertEquals(studyBean.getFacilityName(), facilityName);
		assertEquals(studyBean.getFacilityCity(), facilityCity);
		assertEquals(studyBean.getFacilityState(), facilityState);
		assertEquals(studyBean.getFacilityZip(), facilityZip);
		assertEquals(studyBean.getFacilityCountry(), facilityCountry);
		assertEquals(studyBean.getFacilityContactName(), facilityContactName);
		assertEquals(studyBean.getFacilityContactDegree(), facilityContactDegree);
		assertEquals(studyBean.getFacilityContactPhone(), facilityContactPhone);
		assertEquals(studyBean.getFacilityContactEmail(), facilityContactEmail);
	}

	@Test
	public void testThatCreateStudyMethodSetFeaturesCorrectly() throws Exception {
		createNewStudy();
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		result = mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName)
				.param("briefTitle", newStudyName).param("protocolId", newProtocolId).param("protocolType", "0")
				.param("summary", "bla bla").param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", Integer.toString(newTotalEnrollment)).param("startDate", "2016-01-20")
				.param("approvalDate", "2017-01-20").param("crfAnnotation", "no").param("dynamicGroup", "no")
				.param("calendaredVisits", "no").param("interactiveDashboards", "no").param("itemLevelSDV", "no")
				.param("subjectCasebookInPDF", "no").param("crfMasking", "no").param("sasExtracts", "no")
				.param("studyEvaluator", "no").param("randomization", "no").param("medicalCoding", "no"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyBean().getName(), newStudyName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getBriefTitle(), newStudyName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getOrigin(), StudyOrigin.STUDIO.getName());
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
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2014-02-20").param("crfAnnotation", "xno"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStartDateIsInWrongFormat() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "20-Jan-2014")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolDateVerificationIsInWrongFormat() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20").param("approvalDate", "08-Jan-2017"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfEndDateIsInWrongFormat() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20").param("endDate", "10-Jan-2016")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodDoesNotSupportHTTPGetMethod() throws Exception {
		mockMvc.perform(get(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfTotalEnrollmentParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfTotalEnrollmentParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "")
				.param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfTotalEnrollmentParameterHasWrongData() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", "blabla").param("startDate", "2016-01-20"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfTotalEnrollmentParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("toTalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStartDateParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStartDateParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStartDateParameterHasWrongData() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "blabla")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStartDateParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("stArtDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStudyNameParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("protocolId", "X_study_1").param("protocolType", "0")
				.param("summary", "bla bla").param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", "1").param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStudyNameParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfStudyNameParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("stUdyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfBriefTitleParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("brIefTitle", "xxxxx")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolType", "0")
				.param("summary", "bla bla").param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", "1").param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolIdParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("proTocolId", "X_study_1")
				.param("protocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolTypeParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("summary", "bla bla").param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", "1").param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolTypeParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("protocolType", "").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolTypeParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("protocolId", "X_study_1")
				.param("proTocolType", "0").param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfProtocolTypeParameterHasWrongValue() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "13").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSummaryParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSummaryParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSummaryParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("sUmmary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfPrincipalInvestigatorParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("sponsor", "test_study_1").param("totalEnrollment", "1").param("startDate", "2016-01-20"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfPrincipalInvestigatorParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfPrincipalInvestigatorParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("princiPalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSponsorParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("totalEnrollment", "1").param("startDate", "2016-01-20"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSponsorParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfSponsorParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("spoNsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfuserNameDoesNotExist() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20").param("userName", "wrong_userName_x1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudiesWithTheSameStudyName() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isOk());
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "XX_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudiesWithTheSameProtocolId() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isOk());
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "X_test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithStudyNameThatExceeds20Symbols() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", getSymbols(21)).param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithBriefTitleThatExceeds100Symbols() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", getSymbols(101))
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithProtocolIdThatExceeds30Symbols() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", getSymbols(31)).param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithCollaboratorsThatExceeds1000Symbols() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("collaborators", getSymbols(1001)).param("totalEnrollment", "1")
				.param("startDate", "2016-01-20")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyWithDescriptionThatExceeds1000Symbols() throws Exception {
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("description", getSymbols(1001)).param("totalEnrollment", "1").param("startDate", "2016-01-20"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateStudyMethodThrowsExceptionIfUserNameHasSiteLevelRoles() throws Exception {
		createNewSite(currentScope.getId());
		createNewUser(newSite, UserType.USER, Role.CLINICAL_RESEARCH_COORDINATOR);
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", "test_study_1").param("briefTitle", "test_study_1")
				.param("protocolId", "X_study_1").param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "1")
				.param("startDate", "2016-01-20").param("userName", newUser.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodDoesNotSupportHTTPGetMethod() throws Exception {
		mockMvc.perform(get(API_STUDY_EDIT).param("studyId", "1").param("studyName", "test_study_1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatAtLeastOneNotRequiredParameterShouldBeSpecifiedForEditStudyMethod() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfTotalEnrollmentParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyName", "test_study_1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfTotalEnrollmentParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "").param("studyName", "test_study_1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfTotalEnrollmentParameterHasWrongData() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "blabla").param("studyName", "test_study_1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfTotalEnrollmentParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("stUdyId", "1").param("studyName", "test_study_1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfStudyDoesNotExist() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "99991").param("studyName", "test_study_1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditASiteUsingTheEditStudyMethod() throws Exception {
		createNewSite(currentScope.getId());
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(newSite.getId())).param("studyName",
				"test_study_1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfFacilityNameParameterHasATypo() throws Exception {
		createNewSite(currentScope.getId());
		mockMvc.perform(
				post(API_STUDY_EDIT).param("studyId", String.valueOf(newSite.getId())).param("fAcilityName", "bla bla"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatStudyAdministratorWithoutRightsCannotEditStudy() throws Exception {
		int studyId = currentScope.getId();
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		createNewUser(newStudy, UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		mockMvc.perform(
				post(API_STUDY_EDIT).param("studyId", String.valueOf(studyId)).param("studyName", "test_study_1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatSystemAdministratorCanEditAnyStudy() throws Exception {
		createNewStudy();
		int newTotalEnrollment = 15;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		result = mockMvc
				.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(newStudy.getId()))
						.param("studyName", newStudyName).param("briefTitle", newStudyName)
						.param("protocolId", newProtocolId).param("summary", "bla bla")
						.param("principalInvestigator", "test")
						.param("totalEnrollment", Integer.toString(newTotalEnrollment)).param("endDate", "2016-04-20")
						.param("crfAnnotation", "yes").param("dynamicGroup", "yes").param("calendaredVisits", "yes")
						.param("interactiveDashboards", "yes").param("itemLevelSDV", "yes")
						.param("subjectCasebookInPDF", "yes").param("crfMasking", "yes").param("sasExtracts", "yes")
						.param("studyEvaluator", "yes").param("randomization", "yes").param("medicalCoding", "yes"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyBean().getName(), newStudyName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getBriefTitle(), newStudyName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getOrigin(), StudyOrigin.GUI.getName());
			assertEquals(restOdmContainer.getRestData().getStudyBean().getIdentifier(), newProtocolId);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getExpectedTotalEnrollment(),
					newTotalEnrollment);
			assertNotNull(restOdmContainer.getRestData().getStudyBean().getDatePlannedEnd());
			for (StudyFeature studyFeature : StudyFeature.values()) {
				assertEquals(studyConfigService.getParameter(studyFeature.getName(),
						restOdmContainer.getRestData().getStudyBean().getStudyParameterConfig()), "yes");
			}
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityName(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityCity(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityState(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityZip(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityCountry(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactName(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactDegree(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactPhone(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactEmail(), "");
		}
	}

	@Test
	public void testThatEditStudyMethodisAbleToSetFacilityParametersCorrectly() throws Exception {
		createNewStudy();
		int newTotalEnrollment = 15;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		String facilityName = "NPMedic";
		String facilityCity = "Austin";
		String facilityState = "TX";
		String facilityZip = "54567";
		String facilityCountry = "USA";
		String facilityContactName = "Dr. Tony Kane";
		String facilityContactDegree = "MD";
		String facilityContactPhone = "(843) 678-2390";
		String facilityContactEmail = "tony.kane@npmedic.com";
		result = mockMvc.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(newStudy.getId()))
				.param("studyName", newStudyName).param("briefTitle", newStudyName).param("protocolId", newProtocolId)
				.param("summary", "bla bla").param("principalInvestigator", "test")
				.param("totalEnrollment", Integer.toString(newTotalEnrollment)).param("endDate", "2016-04-20")
				.param("facilityName", facilityName).param("facilityCity", facilityCity)
				.param("facilityState", facilityState).param("facilityZip", facilityZip)
				.param("facilityCountry", facilityCountry).param("facilityContactName", facilityContactName)
				.param("facilityContactDegree", facilityContactDegree)
				.param("facilityContactPhone", facilityContactPhone)
				.param("facilityContactEmail", facilityContactEmail)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityName(), facilityName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityCity(), facilityCity);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityState(), facilityState);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityZip(), facilityZip);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityCountry(), facilityCountry);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactName(), facilityContactName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactDegree(),
					facilityContactDegree);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactPhone(), facilityContactPhone);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactEmail(), facilityContactEmail);
		}
		StudyBean studyBean = (StudyBean) new StudyDAO(dataSource).findByName(newStudyName);
		assertEquals(studyBean.getFacilityName(), facilityName);
		assertEquals(studyBean.getFacilityCity(), facilityCity);
		assertEquals(studyBean.getFacilityState(), facilityState);
		assertEquals(studyBean.getFacilityZip(), facilityZip);
		assertEquals(studyBean.getFacilityCountry(), facilityCountry);
		assertEquals(studyBean.getFacilityContactName(), facilityContactName);
		assertEquals(studyBean.getFacilityContactDegree(), facilityContactDegree);
		assertEquals(studyBean.getFacilityContactPhone(), facilityContactPhone);
		assertEquals(studyBean.getFacilityContactEmail(), facilityContactEmail);
	}

	@Test
	public void testThatEditStudyMethodSetFeaturesCorrectly() throws Exception {
		createNewStudy();
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		result = mockMvc.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(newStudy.getId()))
				.param("studyName", newStudyName).param("protocolId", newProtocolId).param("summary", "bla bla")
				.param("principalInvestigator", "test").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("startDate", "2016-04-20").param("approvalDate", "2017-02-10").param("crfAnnotation", "no")
				.param("dynamicGroup", "no").param("calendaredVisits", "no").param("interactiveDashboards", "no")
				.param("itemLevelSDV", "no").param("subjectCasebookInPDF", "no").param("crfMasking", "no")
				.param("sasExtracts", "no").param("studyEvaluator", "no").param("randomization", "no")
				.param("medicalCoding", "no")).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyBean().getName(), newStudyName);
			assertEquals(restOdmContainer.getRestData().getStudyBean().getBriefTitle(), "");
			assertEquals(restOdmContainer.getRestData().getStudyBean().getOrigin(), StudyOrigin.GUI.getName());
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
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("startDate", "01-Jan-2016"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfProtocolDateVerificationIsInWrongFormat() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("approvalDate", "01-Jan-2016"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfEndDateIsInWrongFormat() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("endDate", "01-Jan-2016"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfCrfAnnotationHasWrongDate() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("crfAnnotation", "01-Jan-2016"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfWrongPurposeIsUsedForInterventionalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "0").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("purpose", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfWrongPurposeIsUsedForObservationalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "1").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("purpose", "7")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfDurationIsUsedForInterventionalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "0").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("duration", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfSelectionIsUsedForInterventionalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "0").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("selection", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfTimingIsUsedForInterventionalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "0").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("timing", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfAllocationIsUsedForObservationalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "1").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("allocation", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfMaskingIsUsedForObservationalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "1").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("masking", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfControlIsUsedForObservationalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "1").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("control", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfAssignmentIsUsedForObservationalProtocolType()
			throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "1").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("assignment", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfEndPointUsedForObservationalProtocolType() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolType", "1").param("summary", "blabla")
				.param("startDate", "2016-04-20").param("principalInvestigator", "test").param("totalEnrollment", "12")
				.param("endPoint", "2")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToEditStudyProtocolType() throws Exception {
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName).param("briefTitle", newStudyName)
				.param("protocolId", newProtocolId).param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", Integer.toString(newTotalEnrollment)).param("startDate", "2016-01-20"))
				.andExpect(status().isOk());
		StudyBean studyBean = (StudyBean) new StudyDAO(dataSource).findByName(newStudyName);
		assertEquals(studyBean.getProtocolTypeKey(), StudyProtocolType.INTERVENTIONAL.getValue());
		mockMvc.perform(
				post(API_STUDY_EDIT).param("studyId", String.valueOf(studyBean.getId())).param("protocolType", "1"))
				.andExpect(status().isOk());
		studyBean = (StudyBean) new StudyDAO(dataSource).findByName(newStudyName);
		assertEquals(studyBean.getProtocolTypeKey(), StudyProtocolType.OBSERVATIONAL.getValue());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudiesWithIdenticalStudyNames() throws Exception {
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName).param("briefTitle", newStudyName)
				.param("protocolId", newProtocolId).param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "12")
				.param("startDate", "2016-01-20")).andExpect(status().isOk());
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("studyName", newStudyName)
				.param("briefTitle", newStudyName).param("summary", "bla bla").param("principalInvestigator", "test")
				.param("sponsor", "test_study_1").param("totalEnrollment", "12"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudiesWithIdenticalProtocolIds() throws Exception {
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_CREATE).param("studyName", newStudyName).param("briefTitle", newStudyName)
				.param("protocolId", newProtocolId).param("protocolType", "0").param("summary", "bla bla")
				.param("principalInvestigator", "test").param("sponsor", "test_study_1").param("totalEnrollment", "12")
				.param("startDate", "2016-01-20")).andExpect(status().isOk());
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", "1").param("protocolId", newProtocolId)
				.param("summary", "bla bla").param("principalInvestigator", "test").param("sponsor", "test_study_1")
				.param("totalEnrollment", "12")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToSetStudySubjectIdLabelParameterUsingStudyEditMethod() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", Integer.toString(defaultStudy.getId()))
				.param("summary", "bla bla").param("totalEnrollment", "111").param("studySubjectIdLabel", "OLOLOX!"))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatStudyEditMethodThrowsExceptionIfAutoGeneratedSeparatorParameterValueIsWrong() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", Integer.toString(defaultStudy.getId()))
				.param("summary", "bla bla").param("totalEnrollment", "111").param("autoGeneratedSeparator", "88"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyEditMethodThrowsExceptionIfAutoGeneratedPrefixParameterValueIsWrong() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", Integer.toString(defaultStudy.getId()))
				.param("summary", "bla bla").param("totalEnrollment", "111").param("autoGeneratedPrefix", "#$"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyEditMethodThrowsExceptionIfStudyParameterValueIsWrong() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", Integer.toString(defaultStudy.getId()))
				.param("medicalCodingContextNeeded", "xxx")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyEditMethodThrowsExceptionIfStudyParameterIsNotSupported() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", Integer.toString(defaultStudy.getId()))
				.param("medicalCodingContextXNeeded", "yes")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatStudyEditMethodThrowsExceptionIfStudyParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", Integer.toString(defaultStudy.getId()))
				.param("medicalCodingCoNtextNeeded", "yes")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatStudyRemoveMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_REMOVE)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatStudyRemoveMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_REMOVE).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatStudyRemoveMethodThrowsExceptionIfStudyDoesNotExist() throws Exception {
		mockMvc.perform(post(API_STUDY_REMOVE).param("id", "11111")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyRemoveMethodThrowsExceptionIfStudyIsSite() throws Exception {
		createNewSite(currentScope.getId());
		mockMvc.perform(post(API_STUDY_REMOVE).param("id", Integer.toString(newSite.getId())))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyRemoveMethodWorksFine() throws Exception {
		StudyDAO studyDao = new StudyDAO(dataSource);
		int studyId = currentScope.getId();
		createNewSite(studyId);

		StudyBean studyBean = (StudyBean) studyDao.findByPK(studyId);
		studyBean.setStatus(Status.AVAILABLE);
		studyDao.update(studyBean);
		studyBean = (StudyBean) studyDao.findByPK(studyId);
		assertTrue(studyBean.getStatus().isAvailable());

		StudyBean siteBean = (StudyBean) studyDao.findByPK(newSite.getId());
		siteBean.setStatus(Status.AVAILABLE);
		studyDao.update(siteBean);
		assertTrue(siteBean.getStatus().isAvailable());

		mockMvc.perform(post(API_STUDY_REMOVE).param("id", Integer.toString(studyId))).andExpect(status().isOk());

		studyBean = (StudyBean) studyDao.findByPK(studyId);
		assertTrue(studyBean.getStatus().isDeleted());

		siteBean = (StudyBean) studyDao.findByPK(newSite.getId());
		assertTrue(siteBean.getStatus().isDeleted());
	}

	@Test
	public void testThatItIsImpossibleToRemoveLockedStudy() throws Exception {
		StudyDAO studyDao = new StudyDAO(dataSource);
		int studyId = currentScope.getId();

		StudyBean studyBean = (StudyBean) studyDao.findByPK(studyId);
		studyBean.setStatus(Status.LOCKED);
		studyDao.update(studyBean);
		studyBean = (StudyBean) studyDao.findByPK(studyId);
		assertTrue(studyBean.getStatus().isLocked());

		mockMvc.perform(post(API_STUDY_REMOVE).param("id", Integer.toString(studyId)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyRemoveMethodThrowsExceptionIfUserDoesNotHaveRightsToAccessStudy() throws Exception {
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		createNewUser(newStudy, UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		mockMvc.perform(post(API_STUDY_REMOVE).param("id", Integer.toString(defaultStudy.getId())))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyRestoreMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_STUDY_RESTORE)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatStudyRestoreMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_STUDY_RESTORE).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatStudyRestoreMethodThrowsExceptionIfStudyDoesNotExist() throws Exception {
		mockMvc.perform(post(API_STUDY_RESTORE).param("id", "11111")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyRestoreMethodThrowsExceptionIfStudyIsSite() throws Exception {
		createNewSite(currentScope.getId());
		mockMvc.perform(post(API_STUDY_RESTORE).param("id", Integer.toString(newSite.getId())))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyRestoreMethodWorksFine() throws Exception {
		StudyDAO studyDao = new StudyDAO(dataSource);
		int studyId = currentScope.getId();
		createNewSite(studyId);

		StudyBean studyBean = (StudyBean) studyDao.findByPK(studyId);
		studyBean.setStatus(Status.DELETED);
		studyDao.update(studyBean);
		studyBean = (StudyBean) studyDao.findByPK(studyId);
		assertTrue(studyBean.getStatus().isDeleted());

		StudyBean siteBean = (StudyBean) studyDao.findByPK(newSite.getId());
		siteBean.setStatus(Status.DELETED);
		studyDao.update(siteBean);
		assertTrue(siteBean.getStatus().isDeleted());

		mockMvc.perform(post(API_STUDY_RESTORE).param("id", Integer.toString(studyId))).andExpect(status().isOk());

		studyBean = (StudyBean) studyDao.findByPK(studyId);
		assertTrue(studyBean.getStatus().isAvailable());

		siteBean = (StudyBean) studyDao.findByPK(newSite.getId());
		assertTrue(siteBean.getStatus().isAvailable());
	}

	@Test
	public void testThatStudyRestoreMethodThrowsExceptionIfUserDoesNotHaveRightsToAccessStudy() throws Exception {
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		createNewUser(newStudy, UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), newStudy.getName());
		mockMvc.perform(post(API_STUDY_RESTORE).param("id", Integer.toString(defaultStudy.getId())))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRestoreNotRemovedStudy() throws Exception {
		StudyDAO studyDao = new StudyDAO(dataSource);
		int studyId = currentScope.getId();

		StudyBean studyBean = (StudyBean) studyDao.findByPK(studyId);
		studyBean.setStatus(Status.AVAILABLE);
		studyDao.update(studyBean);
		studyBean = (StudyBean) studyDao.findByPK(studyId);
		assertTrue(studyBean.getStatus().isAvailable());

		mockMvc.perform(post(API_STUDY_RESTORE).param("id", Integer.toString(studyId)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudiesMethodDoesNotSupportHttpPost() throws Exception {
		mockMvc.perform(post(API_STUDIES)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudiesMethodDoesNotRequireScope() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", rootUserName).param("password", rootUserPassword))
				.andExpect(status().isOk());
		mockMvc.perform(get(API_STUDIES)).andExpect(status().isOk());
	}

	@Test
	public void testThatRemoveStudyMethodRequiresScope() throws Exception {
		mockMvc.perform(post(API_AUTHENTICATION).param("userName", rootUserName).param("password", rootUserPassword))
				.andExpect(status().isOk());
		mockMvc.perform(post(API_STUDY_REMOVE).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfSubjectPersonIdRequiredHasATypo() throws Exception {
		createNewStudy();
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(newStudy.getId()))
				.param("studyName", newStudyName).param("protocolId", newProtocolId).param("summary", "bla bla")
				.param("principalInvestigator", "test").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("startDate", "2016-04-20").param("subjectPersONIdRequired", "copyFromSSID"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfSubjectPersonIdRequiredHasWrongValue() throws Exception {
		createNewStudy();
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(newStudy.getId()))
				.param("studyName", newStudyName).param("protocolId", newProtocolId).param("summary", "bla bla")
				.param("principalInvestigator", "test").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("startDate", "2016-04-20").param("subjectPersonIdRequired", "xxxx"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyMethodIsAbleToSetSubjectPersonIdRequiredParameter() throws Exception {
		createNewStudy();
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(newStudy.getId()))
				.param("studyName", newStudyName).param("protocolId", newProtocolId).param("summary", "bla bla")
				.param("principalInvestigator", "test").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("startDate", "2016-04-20").param("subjectPersonIdRequired", "copyFromSSID"))
				.andExpect(status().isOk());
		StudyBean studyBean = (StudyBean) new StudyDAO(dataSource).findByName(newStudyName);
		studyConfigService.setParametersForStudy(studyBean);
		studyBean.getStudyParameterConfig().getSubjectPersonIdRequired().equals("copyFromSSID");
	}

	@Test
	public void testThatEditStudyMethodThrowsExceptionIfFacilityContactEmailParameterHasWrongValue() throws Exception {
		createNewStudy();
		int newTotalEnrollment = 12;
		String newStudyName = "s_".concat(Long.toString(timestamp));
		String newProtocolId = "X_study_1".concat(Long.toString(timestamp));
		mockMvc.perform(post(API_STUDY_EDIT).param("studyId", String.valueOf(newStudy.getId()))
				.param("studyName", newStudyName).param("protocolId", newProtocolId).param("summary", "bla bla")
				.param("principalInvestigator", "test").param("totalEnrollment", Integer.toString(newTotalEnrollment))
				.param("startDate", "2016-04-20").param("facilityContactEmail", "blaX!#@"))
				.andExpect(status().isInternalServerError());
	}
}
