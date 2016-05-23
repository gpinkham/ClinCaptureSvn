package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

public class EventServiceTest extends BaseServiceTest {

	@Test
	public void testThatItIsImpossibleToCreateAStudyEventDefinitionIfTypeParameterHasWrongValue() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduledfdfdfdfdf"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled").param("xparamX", "2"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateMethodThrowsExceptionIfDescriptionParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("descRiption", "olololo!")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsPossibleToCreateStudyEventDefinitionIfNameHas2000Symbols() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", getSymbols(2000)).param("type", "scheduled"))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatItIsPossibleToCreateStudyEventDefinitionIfDescriptionHas2000Symbols() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test name").param("description", getSymbols(2000))
				.param("type", "scheduled")).andExpect(status().isOk());
	}

	@Test
	public void testThatItIsPossibleToCreateStudyEventDefinitionIfCategoryHas2000Symbols() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test name").param("category", getSymbols(2000))
				.param("type", "scheduled")).andExpect(status().isOk());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfNameHasMoreThan2000Symbols() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", getSymbols(2001)).param("type", "scheduled"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfDescriptionHasMoreThan2000Symbols()
			throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test name").param("description", getSymbols(2001))
				.param("type", "scheduled")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfCategoryHasMoreThan2000Symbols() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test name").param("category", getSymbols(2001))
				.param("type", "scheduled")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfNameIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "").param("type", "scheduled"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfTypeIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test name").param("type", ""))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfTypeIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfNameIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("type", "scheduled")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionWithWrongType() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled!"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToCreateCommonStudyEventDefinitionPassingOnlyNameAndType() throws Exception {
		result = mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "common"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "common");
		}
	}

	@Test
	public void testThatItIsPossibleToCreateUnscheduledStudyEventDefinitionPassingOnlyNameAndType() throws Exception {
		result = mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "unscheduled"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "unscheduled");
		}
	}

	@Test
	public void testThatHttpGetIsNotSupportedForCreatingAStudyEventDefinition() throws Exception {
		mockMvc.perform(get(API_EVENT_CREATE).param("name", "test_event").param("type", "unscheduled"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToCreateScheduledStudyEventDefinitionPassingOnlyNameAndType() throws Exception {
		result = mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "scheduled");
		}
	}

	@Test
	public void testThatItIsPossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventPassingOnlyNameTypeAndEmailUser()
			throws Exception {
		result = mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("emailUser", "root")).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "calendared_visit");
			assertFalse(restOdmContainer.getRestData().getStudyEventDefinitionBean().getReferenceVisit());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getUserEmailId() == 1);
		}
	}

	@Test
	public void testThatItIsPossibleToCreateCalendaredStudyEventDefinitionThatIsReferenceEventPassingOnlyNameAndType()
			throws Exception {
		result = mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("isReference", "true")).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "calendared_visit");
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getReferenceVisit());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getUserEmailId() == 1);
		}
	}

	@Test
	public void testThatCommonStudyEventDefinitionIsCreatedCorrectly() throws Exception {
		result = mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "common")
				.param("description", "test description").param("category", "test category").param("repeating", "true"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "test_event");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "common");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getDescription(),
					"test description");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getCategory(), "test category");
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().isRepeating());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getId() > 0);
		}
	}

	@Test
	public void testThatUnscheduledStudyEventDefinitionIsCreatedCorrectly() throws Exception {
		result = mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "unscheduled")
				.param("description", "test description").param("category", "test category").param("repeating", "true"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "test_event");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "unscheduled");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getDescription(),
					"test description");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getCategory(), "test category");
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().isRepeating());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getId() > 0);
		}
	}

	@Test
	public void testThatScheduledStudyEventDefinitionIsCreatedCorrectly() throws Exception {
		result = mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("description", "test description").param("category", "test category").param("repeating", "true"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "test_event");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "scheduled");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getDescription(),
					"test description");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getCategory(), "test category");
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().isRepeating());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getId() > 0);
		}
	}

	@Test
	public void testThatCalendaredStudyEventDefinitionThatIsReferenceEventIsCreatedCorrectly() throws Exception {

		result = mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category")
				.param("isReference", "true")).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "test_event");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "calendared_visit");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getDescription(),
					"test description");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getCategory(), "test category");
			assertFalse(restOdmContainer.getRestData().getStudyEventDefinitionBean().isRepeating());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getReferenceVisit());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getMaxDay() == 0);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getMinDay() == 0);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getScheduleDay() == 0);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getEmailDay() == 0);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getUserEmailId() == 1);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getId() > 0);

		}
	}

	@Test
	public void testThatCalendaredStudyEventDefinitionThatIsNotReferenceEventIsCreatedCorrectly() throws Exception {
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		result = mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category").param("schDay", "4")
				.param("maxDay", "4").param("minDay", "3").param("emailDay", "2").param("emailUser", newUser.getName()))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "test_event");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "calendared_visit");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getDescription(),
					"test description");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getCategory(), "test category");
			assertFalse(restOdmContainer.getRestData().getStudyEventDefinitionBean().isRepeating());
			assertFalse(restOdmContainer.getRestData().getStudyEventDefinitionBean().getReferenceVisit());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getMaxDay() == 4);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getMinDay() == 3);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getScheduleDay() == 4);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getEmailDay() == 2);
			assertTrue(
					restOdmContainer.getRestData().getStudyEventDefinitionBean().getUserEmailId() == newUser.getId());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getId() > 0);

		}
	}

	@Test
	public void testThatItIsImpossibleToCreateReferencedCalendaredEventIfMaxDayIsSpecified() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category")
				.param("isReference", "true").param("maxDay", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateReferencedCalendaredEventIfMinDayIsSpecified() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category")
				.param("isReference", "true").param("minDay", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateReferencedCalendaredEventIfSchDayIsSpecified() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category")
				.param("isReference", "true").param("schDay", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateReferencedCalendaredEventIfEmailDayIsSpecified() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category")
				.param("isReference", "true").param("emailDay", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateReferencedCalendaredEventIfUserNameIsSpecified() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category")
				.param("isReference", "true").param("emailUser", "root")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateNonCalendaredEventIfMaxDayIsSpecified() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("description", "test description").param("category", "test category").param("maxDay", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateNonCalendaredEventIfMinDayIsSpecified() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("description", "test description").param("category", "test category").param("minDay", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateNonCalendaredEventIfSchDayIsSpecified() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("description", "test description").param("category", "test category").param("schDay", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateNonCalendaredEventIfEmailDayIsSpecified() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("description", "test description").param("category", "test category").param("emailDay", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateNonCalendaredEventIfUserNameIsSpecified() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("description", "test description").param("category", "test category").param("emailUser", "root"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateReferencedNonCalendaredEvent() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("description", "test description").param("category", "test category")
				.param("isReference", "true")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateTheRepeatingCalendaredStudyEventDefinition() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("repeating", "true").param("description", "test description").param("category", "test category")
				.param("schDay", "4").param("maxDay", "4").param("minDay", "3").param("emailDay", "2")
				.param("emailUser", rootUserName)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfSchDayHasWrongType() throws Exception {
		mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled").param("schDay", "a"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfEmailDayHasWrongType() throws Exception {
		mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled").param("emailDay", "a"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfMaxDayHasWrongType() throws Exception {
		mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled").param("maxDay", "a"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfMinDayHasWrongType() throws Exception {
		mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled").param("minDay", "a"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfIsReferenceHasWrongType() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("isReference", "asdfadsf")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfRepeatingHasWrongType() throws Exception {
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled").param("repeating",
				"asdfadsf")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfSchDayMoreThenMaxDay()
			throws Exception {
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category").param("schDay", "5")
				.param("maxDay", "4").param("minDay", "3").param("emailDay", "2").param("emailUser", newUser.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfMinDayMoreThenSchDay()
			throws Exception {
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category").param("schDay", "1")
				.param("maxDay", "4").param("minDay", "3").param("emailDay", "1").param("emailUser", newUser.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfMinDayMoreThenMaxDay()
			throws Exception {
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category").param("schDay", "5")
				.param("maxDay", "5").param("minDay", "7").param("emailDay", "2").param("emailUser", newUser.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfEmailDayMoreThenSchDay()
			throws Exception {
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category").param("schDay", "5")
				.param("maxDay", "5").param("minDay", "7").param("emailDay", "9").param("emailUser", newUser.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfEmailUserDoesNotHaveScopeRole()
			throws Exception {
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, defaultStudyName);
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category").param("schDay", "4")
				.param("maxDay", "4").param("minDay", "3").param("emailDay", "2").param("emailUser", newUser.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionThatDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1234").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatAddCrfMethodThrowsExceptionIfDefaultVersionParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultvErsion", "v1.0")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAddCrfMethodThrowsExceptionIfCrfNameParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfnAme", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAddCrfMethodThrowsExceptionIfEventIdParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("evEntid", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAddCrfMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("xparamX", "56")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfCrfDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "xxxxxx").param("defaultVersion",
				"v1.0")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfDefaultCrfVersionDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.000000")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfCrfVersionIsLocked() throws Exception {
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(5);
		crfVersionBean.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));
		crfVersionBean.setStatus(Status.LOCKED);
		crfVersionDao.update(crfVersionBean);
		crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(5);
		assertTrue(crfVersionBean.getStatus().equals(Status.LOCKED));
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", crfVersionBean.getName())).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfCrfVersionIsDeleted() throws Exception {
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(5);
		crfVersionBean.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));
		crfVersionBean.setStatus(Status.DELETED);
		crfVersionDao.update(crfVersionBean);
		crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(5);
		assertTrue(crfVersionBean.getStatus().equals(Status.DELETED));
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", crfVersionBean.getName())).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfEventIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("crfName", "Test CRF").param("defaultVersion", "v1.000000"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfEventIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.000000")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfCrfNameParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("defaultVersion", "v1.000000"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfCrfNameParameterIsEmpty() throws Exception {
		mockMvc.perform(
				post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "").param("defaultVersion", "v1.000000"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfDefaultVersionParameterIsMissing()
			throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfDefaultVersionParameterIsEmpty()
			throws Exception {
		mockMvc.perform(
				post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF").param("defaultVersion", ""))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfSourceDataVerificationParameterHasWrongValue()
			throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("sourceDataVerification", "45"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfDataEntryQualityParameterHasWrongValue()
			throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("dataEntryQuality", "x"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfEmailWhenParameterHasWrongValue()
			throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("emailWhen", "z")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfTabbingParameterHasWrongValue() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("tabbing", "ppp")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfEmailParameterHasWrongValue() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("emailWhen", "complete").param("email", "sdfsdfsdf"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItImpossibleToAddCrfToStudyEventDefinitionThatDoesNotBelongToCurrentScope() throws Exception {
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionTwice() throws Exception {
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isOk());
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToAddCrfToStudyEventDefinitionIfOnlyRequiredParametersArePassed() throws Exception {
		result = mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEventName(),
					"ED-1-NonRepeating");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getCrfName(), "Test CRF");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getDefaultVersionName(), "v1.0");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isHideCrf(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isRequiredCRF(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isElectronicSignature(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isAcceptNewCrfVersions(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isEvaluatedCRF(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isDoubleEntry(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getTabbingMode(), "leftToRight");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEmailStep(), "");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEmailTo(), "");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getParentId(), 0);
			assertEquals(
					SourceDataVerification
							.getByDescription(restOdmContainer.getRestData().getEventDefinitionCRFBean().getSdvCode()),
					SourceDataVerification.NOTREQUIRED);
			assertEquals(Status.getByName(restOdmContainer.getRestData().getEventDefinitionCRFBean().getStatusCode()),
					Status.AVAILABLE);
			assertTrue(restOdmContainer.getRestData().getEventDefinitionCRFBean().getId() > 0);
		}
	}

	@Test
	public void testThatItIsPossibleToAddCrfToStudyEventDefinitionIfAllParametersArePassed() throws Exception {
		result = mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("emailWhen", "complete").param("email", "clinovo@gmail.com")
				.param("required", "false").param("passwordRequired", "true").param("hide", "true")
				.param("sourceDataVerification", "1").param("dataEntryQuality", "dde").param("tabbing", "topToBottom")
				.param("acceptNewCrfVersions", "true")).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEventName(),
					"ED-1-NonRepeating");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getCrfName(), "Test CRF");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getDefaultVersionName(), "v1.0");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isHideCrf(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isRequiredCRF(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isElectronicSignature(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isAcceptNewCrfVersions(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isEvaluatedCRF(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isDoubleEntry(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getTabbingMode(), "topToBottom");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEmailStep(), "complete");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEmailTo(), "clinovo@gmail.com");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getParentId(), 0);
			assertEquals(
					SourceDataVerification
							.getByDescription(restOdmContainer.getRestData().getEventDefinitionCRFBean().getSdvCode()),
					SourceDataVerification.AllREQUIRED);
			assertEquals(Status.getByName(restOdmContainer.getRestData().getEventDefinitionCRFBean().getStatusCode()),
					Status.AVAILABLE);
			assertTrue(restOdmContainer.getRestData().getEventDefinitionCRFBean().getId() > 0);
		}
	}

	@Test
	public void testThatHttpGetIsNotSupportedForAddingCrfToStudyEventDefinition() throws Exception {
		mockMvc.perform(get(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatInfoAboutExistingStudyEventDefinitionIsReturnedCorrectly() throws Exception {
		result = mockMvc.perform(get(API_EVENT).param("id", "1")).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "ED-1-NonRepeating");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getEventDefinitionCrfs().size(),
					3);
		}
	}

	@Test
	public void testThatItIsImpossibleToGetInfoAboutNonExistingStudyEventDefinition() throws Exception {
		mockMvc.perform(get(API_EVENT).param("id", "413341")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToGetInfoAboutExistingStudyEventDefinitionThatDoesNotBelongToCurrentScope()
			throws Exception {
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		mockMvc.perform(get(API_EVENT).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatGetInfoThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(get(API_EVENT).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatGetInfoThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(get(API_EVENT)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatGetInfoThrowsExceptionIfIdParameterHasATypo() throws Exception {
		mockMvc.perform(get(API_EVENT).param("Id", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatGetInfoThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(get(API_EVENT).param("id", "1").param("xparamX", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditANonExistingStudyEventDefinition() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1231").param("name", "new name!"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditAStudyEventDefinitionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditAStudyEventDefinitionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAtLeastOneNotRequiredParameterShouldBeSpecifiedDuringEditingAStudyEventDefinition()
			throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatHttpGetMethodIsNotSupportedForEditingOfAStudyEventDefinition() throws Exception {
		mockMvc.perform(get(API_EVENT_EDIT).param("id", "1").param("name", "new name!"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditAStudyEventDefinitionThatDoesNotBelongToCurrentScope() throws Exception {
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", "new name!"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToChangeTheNameInAStudyEventDefinition() throws Exception {
		String newName = "new test name!";
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", newName)).andExpect(status().isOk());
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(1);
		assertEquals(studyEventDefinitionBean.getName(), newName);
	}

	@Test
	public void testThatItIsPossibleToChangeTheDescriptionInAStudyEventDefinition() throws Exception {
		String newDescription = "new test description!!!!";
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("description", newDescription))
				.andExpect(status().isOk());
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(1);
		assertEquals(studyEventDefinitionBean.getDescription(), newDescription);
	}

	@Test
	public void testThatItIsPossibleToChangeTheCategoryInAStudyEventDefinition() throws Exception {
		String newCategory = "new test category!!!!";
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("category", newCategory))
				.andExpect(status().isOk());
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(1);
		assertEquals(studyEventDefinitionBean.getCategory(), newCategory);
	}

	@Test
	public void testThatItIsPossibleToChangeTheRepeatingPropertyInAStudyEventDefinition() throws Exception {
		String repeating = "false";
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("repeating", repeating)).andExpect(status().isOk());
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(1);
		assertFalse(studyEventDefinitionBean.isRepeating());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheMaxDayForANonCalendaredStudyEventDefinition() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("maxDay", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheMinDayForANonCalendaredStudyEventDefinition() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("minDay", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheSchDayForANonCalendaredStudyEventDefinition() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schDay", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheEmailDayForANonCalendaredStudyEventDefinition() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("emailDay", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheEmailUserForANonCalendaredStudyEventDefinition() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("emailUser", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheIsReferencePropertyForANonCalendaredStudyEventDefinition()
			throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("isReference", "true"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheRepeatingPropertyForACalendaredStudyEventDefinition()
			throws Exception {
		mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit").param("emailUser", "root"))
				.andExpect(status().isOk());
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("repeating", "true"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheMaxDayForAReferencedCalendaredStudyEventDefinition() throws Exception {
		mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit").param("isReference", "true"))
				.andExpect(status().isOk());
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("maxDay", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheMinDayForAReferencedCalendaredStudyEventDefinition() throws Exception {
		mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit").param("isReference", "true"))
				.andExpect(status().isOk());
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("minDay", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheSchDayForAReferencedCalendaredStudyEventDefinition() throws Exception {
		mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit").param("isReference", "true"))
				.andExpect(status().isOk());
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schDay", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheEmailDayForAReferencedCalendaredStudyEventDefinition()
			throws Exception {
		mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit").param("isReference", "true"))
				.andExpect(status().isOk());
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("emailDay", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheEmailUserForAReferencedCalendaredStudyEventDefinition()
			throws Exception {
		mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit").param("isReference", "true"))
				.andExpect(status().isOk());
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("emailUser", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeCalendaredStudyEventDefinitionThatIsNotReferenceEventIfSchDayMoreThenMaxDay()
			throws Exception {
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit").param("emailUser", "root"))
				.andExpect(status().isOk());
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schDay", "5").param("maxDay", "4")
				.param("minDay", "3").param("emailDay", "2").param("emailUser", newUser.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeCalendaredStudyEventDefinitionThatIsNotReferenceEventIfMinDayMoreThenSchDay()
			throws Exception {
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit").param("emailUser", "root"))
				.andExpect(status().isOk());
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schDay", "1").param("maxDay", "4")
				.param("minDay", "3").param("emailDay", "1").param("emailUser", newUser.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeCalendaredStudyEventDefinitionThatIsNotReferenceEventIfMinDayMoreThenMaxDay()
			throws Exception {
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit").param("emailUser", "root"))
				.andExpect(status().isOk());
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schDay", "5").param("maxDay", "5")
				.param("minDay", "7").param("emailDay", "2").param("emailUser", newUser.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeCalendaredStudyEventDefinitionThatIsNotReferenceEventIfEmailDayMoreThenSchDay()
			throws Exception {
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit").param("emailUser", "root"))
				.andExpect(status().isOk());
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schDay", "5").param("maxDay", "5")
				.param("minDay", "7").param("emailDay", "9").param("emailUser", newUser.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeCalendaredStudyEventDefinitionThatIsNotReferenceEventIfEmailUserDoesNotHaveScopeRole()
			throws Exception {
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, defaultStudyName);
		mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit").param("emailUser", "root"))
				.andExpect(status().isOk());
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schDay", "4").param("maxDay", "4")
				.param("minDay", "3").param("emailDay", "2").param("emailUser", newUser.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfTypeHasWrongType() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "scheduled!!!!"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfSchDayHasWrongType() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schDay", "a")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfEmailDayHasWrongType() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("emailDay", "a"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfMaxDayHasWrongType() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("maxDay", "a")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfMinDayHasWrongType() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("minDay", "a")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfIsReferenceHasWrongType() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("isReference", "asdfadsf"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfRepeatingHasWrongType() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("repeating", "asdfadsf"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatScheduledStudyEventDefinitionIsChangedCorrectlyToTheUnscheduledStudyEventDefinition()
			throws Exception {
		result = mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", "test_event")
				.param("type", "unscheduled").param("description", "test description")
				.param("category", "test category").param("repeating", "true")).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "test_event");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "unscheduled");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getDescription(),
					"test description");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getCategory(), "test category");
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().isRepeating());
		}
	}

	@Test
	public void testThatScheduledStudyEventDefinitionIsChangedCorrectlyToTheCommonStudyEventDefinition()
			throws Exception {
		result = mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", "test_event")
				.param("type", "common").param("description", "test description").param("category", "test category")
				.param("repeating", "true")).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "test_event");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "common");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getDescription(),
					"test description");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getCategory(), "test category");
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().isRepeating());
		}
	}

	@Test
	public void testThatScheduledStudyEventDefinitionIsChangedCorrectlyToTheCalendaredStudyEventDefinitionThatIsReferenceEvent()
			throws Exception {
		result = mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("name", "test_event")
						.param("type", "calendared_visit").param("description", "test description")
						.param("category", "test category").param("isReference", "true"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "test_event");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "calendared_visit");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getDescription(),
					"test description");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getCategory(), "test category");
			assertFalse(restOdmContainer.getRestData().getStudyEventDefinitionBean().isRepeating());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getReferenceVisit());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getMaxDay() == 0);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getMinDay() == 0);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getScheduleDay() == 0);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getEmailDay() == 0);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getUserEmailId() == 1);
		}
	}

	@Test
	public void testThatScheduledStudyEventDefinitionIsChangedCorrectlyToTheCalendaredStudyEventDefinitionThatIsNotReferenceEvent()
			throws Exception {
		createNewStudyUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		result = mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("name", "test_event")
						.param("type", "calendared_visit").param("description", "test description")
						.param("category", "test category").param("schDay", "4").param("maxDay", "4")
						.param("minDay", "3").param("emailDay", "2").param("emailUser", newUser.getName()))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "test_event");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "calendared_visit");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getDescription(),
					"test description");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getCategory(), "test category");
			assertFalse(restOdmContainer.getRestData().getStudyEventDefinitionBean().isRepeating());
			assertFalse(restOdmContainer.getRestData().getStudyEventDefinitionBean().getReferenceVisit());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getMaxDay() == 4);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getMinDay() == 3);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getScheduleDay() == 4);
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getEmailDay() == 2);
			assertTrue(
					restOdmContainer.getRestData().getStudyEventDefinitionBean().getUserEmailId() == newUser.getId());
		}
	}

	@Test
	public void testThatItIsPossibleToChangeStudyEventDefinitionIfNameHas2000Symbols() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", getSymbols(2000)))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatItIsPossibleToChangeStudyEventDefinitionIfDescriptionHas2000Symbols() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("description", getSymbols(2000)))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatItIsPossibleToChangeStudyEventDefinitionIfCategoryHas2000Symbols() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("category", getSymbols(2000)))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfNameHasMoreThan2000Symbols() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", getSymbols(2001)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfDescriptionHasMoreThan2000Symbols()
			throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("description", getSymbols(2001)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfCategoryHasMoreThan2000Symbols() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("category", getSymbols(2001)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditMethodThrowsExceptionIfIdParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("Id", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditMethodThrowsExceptionIfNameParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("naMe", "ololo"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("xparamX", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToRemoveStudyEventDefinitionThatDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE).param("id", "1345")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE).param("id", "1").param("ololo", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveMethodThrowsExceptionIfIdParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE).param("Id", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveMethodWorksFineForExistingStudyEventDefinition() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE).param("id", "1")).andExpect(status().isOk());
		assertEquals(((StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1)).getStatus(), Status.DELETED);
	}

	@Test
	public void testThatItIsImpossibleToRemoveLockedStudyEventDefinition() throws Exception {
		int studyEventDefinitionId = 1;
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(studyEventDefinitionId);
		studyEventDefinitionBean.setStatus(Status.LOCKED);
		studyEventDefinitionDAO.update(studyEventDefinitionBean);
		studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(studyEventDefinitionId);
		assertTrue(studyEventDefinitionBean.getStatus().isLocked());
		mockMvc.perform(post(API_EVENT_REMOVE).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRestoreStudyEventDefinitionThatDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE).param("id", "1345")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE).param("id", "1").param("ololo", "1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreMethodThrowsExceptionIfIdParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE).param("Id", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreMethodWorksFineForExistingStudyEventDefinition() throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(1);
		studyEventDefinitionBean.setStatus(Status.DELETED);
		studyEventDefinitionBean.setUpdater(rootUser);
		studyEventDefinitionDAO.updateStatus(studyEventDefinitionBean);
		studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1);
		assertEquals(studyEventDefinitionBean.getStatus(), Status.DELETED);
		mockMvc.perform(post(API_EVENT_RESTORE).param("id", "1")).andExpect(status().isOk());
		studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1);
		assertEquals(studyEventDefinitionBean.getStatus(), Status.AVAILABLE);
	}

	@Test
	public void testThatItIsImpossibleToRestoreNotRemovedStudyEventDefinition() throws Exception {
		int studyEventDefinitionId = 1;
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(studyEventDefinitionId);
		studyEventDefinitionBean.setStatus(Status.AVAILABLE);
		studyEventDefinitionDAO.update(studyEventDefinitionBean);
		studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(studyEventDefinitionId);
		assertTrue(studyEventDefinitionBean.getStatus().isAvailable());
		mockMvc.perform(post(API_EVENT_RESTORE).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatSystemAdministratorIsAbleToCallStudyEventAPI() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		ResultMatcher expectStatus = status().isOk();
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("description", "test description").param("category", "test category")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", "new_test_event")).andExpect(expectStatus);
		mockMvc.perform(get(API_EVENT).param("id", "1")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_REMOVE).param("id", "1")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_RESTORE).param("id", "1")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1").param("crfName", "Agent Administration"))
				.andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1").param("crfName", "Agent Administration"))
				.andExpect(expectStatus);
	}

	@Test
	public void testThatStudyAdministratorWithAdministrativePrivilegesIsAbleToCallStudyEventAPI() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		ResultMatcher expectStatus = status().isOk();
		createNewStudyUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), defaultStudyName);
		mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("description", "test description").param("category", "test category")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", "new_test_event")).andExpect(expectStatus);
		mockMvc.perform(get(API_EVENT).param("id", "1")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventId", "1").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_REMOVE).param("id", "1")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_RESTORE).param("id", "1")).andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1").param("crfName", "Agent Administration"))
				.andExpect(expectStatus);
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1").param("crfName", "Agent Administration"))
				.andExpect(expectStatus);
	}

	@Test
	public void testThatItIsImpossibleToRemoveEventDefinitionCrfFromStudyEventDefinitionThatDoesNotExist()
			throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1345").param("crfName", "Agent Administration"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRemoveEventDefinitionCrfThatDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1").param("crfName", "Another CRF"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRemoveEventDefinitionCrfForCrfThatDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1").param("crfName", "XXXXXXXXXX"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfEventIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("crfName", "Agent Administration"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfCrfNameParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfEventIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "").param("crfName", "Agent Administration"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfCrfNameParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1").param("crfName", ""))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1").param("crfName", "Agent Administration")
				.param("ololo", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfEventIdParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("evEntId", "1").param("crfName", "Agent Administration"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfCrfNameParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1").param("crfNaMe", "Agent Administration"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodWorksFineForExistingEventDefinitionCrf() throws Exception {
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1").param("crfName", "Agent Administration"))
				.andExpect(status().isOk());
		assertEquals(((EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1)).getStatus(), Status.DELETED);
	}

	@Test
	public void testThatItIsImpossibleToMakeRemoveOperationOnEventDefinitionCrfIfStudyEventDefinitionIsNotAvailable()
			throws Exception {
		setStatusForSED(1, Status.DELETED);
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1").param("crfName", "Agent Administration"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRemoveLockedEventDefinitionCrf() throws Exception {
		int eventDefinitionCrfId = 1;
		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.setId(1);
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO
				.findByPK(eventDefinitionCrfId);
		eventDefinitionCRFBean.setUpdater(userAccountBean);
		eventDefinitionCRFBean.setStatus(Status.LOCKED);
		eventDefinitionCRFDAO.update(eventDefinitionCRFBean);
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(eventDefinitionCrfId);
		assertTrue(eventDefinitionCRFBean.getStatus().isLocked());
		mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1").param("crfName", "Agent Administration"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRestoreEventDefinitionCrfFromStudyEventDefinitionThatDoesNotExist()
			throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1345").param("crfName", "Agent Administration"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRestoreEventDefinitionCrfThatDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1").param("crfName", "Another CRF"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRestoreEventDefinitionCrfForCrfThatDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1").param("crfName", "XXXXXXXXXX"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfEventIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("crfName", "Agent Administration"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfCrfNameParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfEventIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "").param("crfName", "Agent Administration"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfCrfNameParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1").param("crfName", ""))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1").param("crfName", "Agent Administration")
				.param("ololo", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfEventIdParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("evEntId", "1").param("crfName", "Agent Administration"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfCrfNameParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1").param("crfNamE", "Agent Administration"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToRestoreNotRemoveEventDefinitionCrf() throws Exception {
		int eventDefinitionCrfId = 1;
		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.setId(1);
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO
				.findByPK(eventDefinitionCrfId);
		eventDefinitionCRFBean.setUpdater(userAccountBean);
		eventDefinitionCRFBean.setStatus(Status.AVAILABLE);
		eventDefinitionCRFDAO.update(eventDefinitionCRFBean);
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(eventDefinitionCrfId);
		assertTrue(eventDefinitionCRFBean.getStatus().isAvailable());
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1").param("crfName", "Agent Administration"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreCrfMethodWorksFineForExistingEventDefinitionCrf() throws Exception {
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		eventDefinitionCRFBean.setStatus(Status.DELETED);
		eventDefinitionCRFBean.setUpdater(rootUser);
		eventDefinitionCRFDAO.updateStatus(eventDefinitionCRFBean);
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		assertEquals(eventDefinitionCRFBean.getStatus(), Status.DELETED);
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1").param("crfName", "Agent Administration"))
				.andExpect(status().isOk());
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		assertEquals(eventDefinitionCRFBean.getStatus(), Status.AVAILABLE);
	}

	@Test
	public void testThatItIsImpossibleToMakeRestoreOperationOnEventDefinitionCrfIfStudyEventDefinitionIsNotAvailable()
			throws Exception {
		setStatusForSED(1, Status.DELETED);
		mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1").param("crfName", "Agent Administration"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionThatDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "1234").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyCrfMethodThrowsExceptionIfDefaultVersionParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultvErsion", "v1.0")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyCrfMethodThrowsExceptionIfCrfNameParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfnAme", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyCrfMethodThrowsExceptionIfEventIdParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("evEntid", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyCrfMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("xparamX", "56")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfCrfDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "xxxxxx")
				.param("defaultVersion", "v1.0")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfDefaultCrfVersionDoesNotExist()
			throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.000000")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfCrfVersionIsLocked() throws Exception {
		CRFVersionBean crfVersionBean = setStatusForCrfVersion(5, Status.LOCKED);
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", crfVersionBean.getName())).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfCrfVersionIsDeleted() throws Exception {
		CRFVersionBean crfVersionBean = setStatusForCrfVersion(5, Status.DELETED);
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", crfVersionBean.getName())).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfEDCIsLocked() throws Exception {
		setStatusForEDC(7, Status.LOCKED);
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfEDCIsDeleted() throws Exception {
		setStatusForEDC(7, Status.DELETED);
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfEventIdParameterIsMissing()
			throws Exception {
		mockMvc.perform(
				post(API_EVENT_EDIT_STUDY_CRF).param("crfName", "Test CRF").param("defaultVersion", "v1.000000"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfEventIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.000000")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfCrfNameParameterIsMissing()
			throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("defaultVersion", "v1.000000"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfCrfNameParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "")
				.param("defaultVersion", "v1.000000")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfSourceDataVerificationParameterHasWrongValue()
			throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("sourceDataVerification", "45"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfDataEntryQualityParameterHasWrongValue()
			throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("dataEntryQuality", "x"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfEmailWhenParameterHasWrongValue()
			throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("emailWhen", "z")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfTabbingParameterHasWrongValue()
			throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("tabbing", "ppp")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfEmailParameterHasWrongValue()
			throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("emailWhen", "complete").param("email", "sdfsdfsdf"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItImpossibleToEditStudyCrfOfStudyEventDefinitionThatDoesNotBelongToCurrentScope()
			throws Exception {
		createNewStudy();
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatHttpGetIsNotSupportedForEditingStudyCrfOfStudyEventDefinition() throws Exception {
		mockMvc.perform(get(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToEditStudyCrfOfStudyEventDefinitionIfOnlyRequiredParametersArePassed()
			throws Exception {
		result = mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0")).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEventName(),
					"ED-9-NotRepeating");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getCrfName(), "Test CRF");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getDefaultVersionName(), "v1.0");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isHideCrf(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isRequiredCRF(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isElectronicSignature(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isAcceptNewCrfVersions(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isEvaluatedCRF(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isDoubleEntry(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getTabbingMode(), "leftToRight");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEmailStep(), "");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEmailTo(), "");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getParentId(), 0);
			assertEquals(
					SourceDataVerification
							.getByDescription(restOdmContainer.getRestData().getEventDefinitionCRFBean().getSdvCode()),
					SourceDataVerification.NOTREQUIRED);
			assertEquals(Status.getByName(restOdmContainer.getRestData().getEventDefinitionCRFBean().getStatusCode()),
					Status.AVAILABLE);
			assertTrue(restOdmContainer.getRestData().getEventDefinitionCRFBean().getId() > 0);
		}
	}

	@Test
	public void testThatItIsPossibleToEditStudyCrfOfStudyEventDefinitionIfAllParametersArePassed() throws Exception {
		result = mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
						.param("defaultVersion", "v1.0").param("emailWhen", "complete")
						.param("email", "clinovo@gmail.com, clinovo2@gmail.com").param("required", "false")
						.param("passwordRequired", "true").param("hide", "true").param("sourceDataVerification", "3")
						.param("dataEntryQuality", "dde").param("tabbing", "topToBottom")
						.param("acceptNewCrfVersions", "true").param("propagateChange", "3"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEventName(),
					"ED-9-NotRepeating");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getCrfName(), "Test CRF");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getDefaultVersionName(), "v1.0");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isHideCrf(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isRequiredCRF(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isElectronicSignature(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isAcceptNewCrfVersions(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isEvaluatedCRF(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isDoubleEntry(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getTabbingMode(), "topToBottom");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEmailStep(), "complete");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEmailTo(),
					"clinovo@gmail.com, clinovo2@gmail.com");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getParentId(), 0);
			assertEquals(
					SourceDataVerification
							.getByDescription(restOdmContainer.getRestData().getEventDefinitionCRFBean().getSdvCode()),
					SourceDataVerification.NOTREQUIRED);
			assertEquals(Status.getByName(restOdmContainer.getRestData().getEventDefinitionCRFBean().getStatusCode()),
					Status.AVAILABLE);
			assertTrue(restOdmContainer.getRestData().getEventDefinitionCRFBean().getId() > 0);
		}
	}

	@Test
	public void testThatEditStudyCrfMethodCanUnsetTheEmailWhenParameter() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("emailWhen", "complete").param("email", "clinovo@gmail.com")
				.param("required", "false").param("passwordRequired", "true").param("hide", "true")
				.param("sourceDataVerification", "3").param("dataEntryQuality", "dde").param("tabbing", "topToBottom")
				.param("acceptNewCrfVersions", "true")).andExpect(status().isOk());
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(7);
		assertTrue(eventDefinitionCRFBean.getEmailStep().equals("complete"));
		assertTrue(eventDefinitionCRFBean.getEmailTo().equals("clinovo@gmail.com"));
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("emailWhen", "none").param("required", "false")
				.param("passwordRequired", "true").param("hide", "true").param("sourceDataVerification", "3")
				.param("dataEntryQuality", "dde").param("tabbing", "topToBottom").param("acceptNewCrfVersions", "true"))
				.andExpect(status().isOk());
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(7);
		assertTrue(eventDefinitionCRFBean.getEmailStep().isEmpty());
		assertTrue(eventDefinitionCRFBean.getEmailTo().isEmpty());
	}

	@Test
	public void testThatEditStudyCrfMethodCanUnsetTheDataEntryQualityParameter() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("emailWhen", "complete").param("email", "clinovo@gmail.com")
				.param("required", "false").param("passwordRequired", "true").param("hide", "true")
				.param("sourceDataVerification", "3").param("dataEntryQuality", "dde").param("tabbing", "topToBottom")
				.param("acceptNewCrfVersions", "true")).andExpect(status().isOk());
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(7);
		assertTrue(eventDefinitionCRFBean.isDoubleEntry());
		assertFalse(eventDefinitionCRFBean.isEvaluatedCRF());
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("emailWhen", "none").param("required", "false")
				.param("passwordRequired", "true").param("hide", "true").param("sourceDataVerification", "3")
				.param("dataEntryQuality", "none").param("tabbing", "topToBottom")
				.param("acceptNewCrfVersions", "true")).andExpect(status().isOk());
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(7);
		assertFalse(eventDefinitionCRFBean.isDoubleEntry());
		assertFalse(eventDefinitionCRFBean.isEvaluatedCRF());
	}

	@Test
	public void testThatEditStudyCrfMethodReturnsExceptionIfEmailParameterIsPresentWithoutEmailWhenParameter()
			throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("email", "clinovo@gmail.com").param("required", "false")
				.param("passwordRequired", "true").param("hide", "true").param("sourceDataVerification", "3")
				.param("dataEntryQuality", "dde").param("tabbing", "topToBottom").param("acceptNewCrfVersions", "true"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyCrfMethodReturnsExceptionIfEmailParameterIsNotPresentButEmailWhenParameterIsPresent()
			throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.0").param("emailWhen", "sign").param("required", "false")
				.param("passwordRequired", "true").param("hide", "true").param("sourceDataVerification", "3")
				.param("dataEntryQuality", "dde").param("tabbing", "topToBottom").param("acceptNewCrfVersions", "true"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatHttpGetIsNotSupportedForDeleteCrfMethod() throws Exception {
		mockMvc.perform(get(API_EVENT_DELETE_CRF).param("eventId", "9").param("crfName", "Test CRF"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatDeleteCrfMethodDeletesEventDefinitionCrfSuccessfully() throws Exception {
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(7);
		eventDefinitionCRFBean.setId(0);
		eventDefinitionCRFBean.setCrfId(6);
		eventDefinitionCRFBean.setDefaultVersionId(8);
		eventDefinitionCRFBean.setDefaultVersionName("v1.0");
		eventDefinitionCRFDAO.create(eventDefinitionCRFBean);
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "9").param("crfName", "Case Completion")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isOk());
		assertTrue(eventDefinitionCRFDAO.findByPK(eventDefinitionCRFBean.getId()).getId() == 0);
	}

	@Test
	public void testThatDelteCrfMethodThrowsExceptionIfThereAreCRFsWithRules() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "1").param("crfName", "Agent Administration")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatDeleteCrfMethodThrowsExceptionIfThereAreCRFsWithData() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "1").param("crfName", "Concomitant Medications AG")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfCrfNameParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "9").param("crfName", "")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfCrfNameParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "9").requestAttr("ruleSetService", ruleSetService))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfCrfNameParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "9").param("crfNaME", "Test CRF")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfEventIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "").param("crfName", "Test CRF")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfEventIdParameterIsMissing() throws Exception {
		mockMvc.perform(
				post(API_EVENT_DELETE_CRF).param("crfName", "Test CRF").requestAttr("ruleSetService", ruleSetService))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfEventIdParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eVentId", "9").param("crfName", "Test CRF")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatDeleteCrfMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "9").param("crfName", "Test CRF").param("XP", "bla")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfCrfDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "9").param("crfName", "XXXXCRF")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfEDCDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "9").param("crfName", "Concomitant Medications AG")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfOfStudyEventDefinitionThatDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "11119").param("crfName", "Test CRF")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfOfStudyEventDefinitionIfEDCIsLocked() throws Exception {
		setStatusForEDC(7, Status.LOCKED);
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfOfStudyEventDefinitionIfEDCIsDeleted() throws Exception {
		setStatusForEDC(7, Status.DELETED);
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfOfStudyEventDefinitionThatIsLocked() throws Exception {
		setStatusForSED(9, Status.LOCKED);
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfOfStudyEventDefinitionThatIsDeleted() throws Exception {
		setStatusForSED(9, Status.DELETED);
		mockMvc.perform(post(API_EVENT_DELETE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.requestAttr("ruleSetService", ruleSetService)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatHttpGetIsNotSupportedForDeleteSEDMethod() throws Exception {
		mockMvc.perform(get(API_EVENT_DELETE).param("id", "9")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatDeleteSEDMethodDeletesStudyEventDefinitionCrfSuccessfully() throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(9);
		studyEventDefinitionBean.setId(0);
		studyEventDefinitionDAO.create(studyEventDefinitionBean);
		mockMvc.perform(post(API_EVENT_DELETE).param("id", Integer.toString(studyEventDefinitionBean.getId())))
				.andExpect(status().isOk());
		assertTrue(studyEventDefinitionDAO.findByPK(studyEventDefinitionBean.getId()).getId() == 0);
	}

	@Test
	public void testThatDeleteSEDMethodThrowsExceptionIfThereAreCRFsWithData() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteSEDIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteSEDIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteSEDIfIdParameterHasATypo() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE).param("Id", "9")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatDeleteSEDMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE).param("id", "9").param("XP", "bla")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteSEDThatDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_DELETE).param("id", "11119")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteSEDThatIsLocked() throws Exception {
		setStatusForSED(9, Status.LOCKED);
		mockMvc.perform(post(API_EVENT_DELETE).param("id", "9")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteSEDThatIsDeleted() throws Exception {
		setStatusForSED(9, Status.DELETED);
		mockMvc.perform(post(API_EVENT_DELETE).param("id", "9")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionThatDoesNotExist() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "1234").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditSiteCrfMethodThrowsExceptionIfDefaultVersionParameterIsInWrongCase() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultvErsion", "v1.0"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditSiteCrfMethodThrowsExceptionIfCrfNameParameterIsInWrongCase() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfnAme", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditSiteCrfMethodThrowsExceptionIfSiteNameParameterIsInWrongCase() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("sitename", newSite.getName()).param("defaultVersion", "v1.0"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditSiteCrfMethodThrowsExceptionIfEventIdParameterIsInWrongCase() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("evEntid", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditSiteCrfMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0").param("xparamX", "56"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfCrfDoesNotExist() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "xxxxxx")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfDefaultCrfVersionDoesNotExist()
			throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.000000"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfCrfVersionIsLocked() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		CRFVersionBean crfVersionBean = setStatusForCrfVersion(5, Status.LOCKED);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", crfVersionBean.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfCrfVersionIsDeleted() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		CRFVersionBean crfVersionBean = setStatusForCrfVersion(5, Status.DELETED);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", crfVersionBean.getName()))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfEDCIsLocked() throws Exception {
		createNewSite(currentScope.getId());
		EventDefinitionCRFBean eventDefinitionCRF = createChildEDCForNewSite(7, newSite);
		setStatusForEDC(eventDefinitionCRF.getId(), Status.LOCKED);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfEDCIsDeleted() throws Exception {
		createNewSite(currentScope.getId());
		EventDefinitionCRFBean eventDefinitionCRF = createChildEDCForNewSite(7, newSite);
		setStatusForEDC(eventDefinitionCRF.getId(), Status.DELETED);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfEventIdParameterIsMissing()
			throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("crfName", "Test CRF").param("siteName", newSite.getName())
				.param("defaultVersion", "v1.000000")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfEventIdParameterIsEmpty() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.000000"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfCrfNameParameterIsMissing()
			throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("siteName", newSite.getName())
				.param("defaultVersion", "v1.000000")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfCrfNameParameterIsEmpty() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.000000"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfSiteNameParameterIsMissing()
			throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("defaultVersion", "v1.000000")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfSiteNameParameterIsEmpty() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", "").param("defaultVersion", "v1.000000")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfSourceDataVerificationParameterHasWrongValue()
			throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0")
				.param("sourceDataVerification", "45")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfDataEntryQualityParameterHasWrongValue()
			throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0").param("dataEntryQuality", "x"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfEmailWhenParameterHasWrongValue()
			throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0").param("emailWhen", "z"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfTabbingParameterHasWrongValue()
			throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0").param("tabbing", "ppp"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditSiteCrfOfStudyEventDefinitionIfEmailParameterHasWrongValue()
			throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0").param("emailWhen", "complete")
				.param("email", "sdfsdfsdf")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItImpossibleToEditSiteCrfOfStudyEventDefinitionIfSiteDoesNotExist() throws Exception {
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", "blablabla").param("defaultVersion", "v1.0"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItImpossibleToEditSiteCrfOfStudyEventDefinitionIfSiteDoesNotBelongToCurrentScope()
			throws Exception {
		createNewSite(currentScope.getId());
		String siteName = newSite.getName();
		createChildEDCForNewSite(7, newSite);
		createNewStudy();
		createNewSite(newStudy.getId());
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", siteName).param("defaultVersion", "v1.0"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItImpossibleToEditSiteCrfOfStudyEventDefinitionIfEDCDoesNotExistInSite() throws Exception {
		createNewSite(currentScope.getId());
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItImpossibleToEditSiteCrfOfStudyEventDefinitionThatDoesNotBelongToCurrentScope()
			throws Exception {
		createNewStudy();
		createNewSite(newStudy.getId());
		login(rootUserName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, rootUserPassword, newStudy.getName());
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatHttpGetIsNotSupportedForEditingSiteCrfOfStudyEventDefinition() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(get(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToEditSiteCrfOfStudyEventDefinitionIfOnlyRequiredParametersArePassed()
			throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		result = mockMvc
				.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
						.param("siteName", newSite.getName()).param("defaultVersion", "v1.0"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEventName(),
					"ED-9-NotRepeating");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getCrfName(), "Test CRF");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getDefaultVersionName(), "v1.0");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isHideCrf(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isRequiredCRF(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isElectronicSignature(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isAcceptNewCrfVersions(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isEvaluatedCRF(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isDoubleEntry(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getTabbingMode(), "leftToRight");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEmailStep(), "");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEmailTo(), "");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getParentId(), 7);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getStudyId(), newSite.getId());
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getSelectedVersionIds(), "5");
			assertEquals(
					SourceDataVerification
							.getByDescription(restOdmContainer.getRestData().getEventDefinitionCRFBean().getSdvCode()),
					SourceDataVerification.NOTREQUIRED);
			assertEquals(Status.getByName(restOdmContainer.getRestData().getEventDefinitionCRFBean().getStatusCode()),
					Status.AVAILABLE);
			assertTrue(restOdmContainer.getRestData().getEventDefinitionCRFBean().getId() > 0);
		}
	}

	@Test
	public void testThatItIsPossibleToEditSiteCrfOfStudyEventDefinitionIfAllParametersArePassed() throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		result = mockMvc
				.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
						.param("siteName", newSite.getName()).param("defaultVersion", "v1.0")
						.param("availableVersions", "5").param("emailWhen", "complete")
						.param("email", "clinovo@gmail.com, clinovo2@gmail.com").param("required", "false")
						.param("passwordRequired", "true").param("hide", "true").param("sourceDataVerification", "3")
						.param("dataEntryQuality", "dde").param("tabbing", "topToBottom"))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEventName(),
					"ED-9-NotRepeating");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getCrfName(), "Test CRF");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getDefaultVersionName(), "v1.0");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isHideCrf(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isRequiredCRF(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isElectronicSignature(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isAcceptNewCrfVersions(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isEvaluatedCRF(), false);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().isDoubleEntry(), true);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getTabbingMode(), "topToBottom");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEmailStep(), "complete");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEmailTo(),
					"clinovo@gmail.com, clinovo2@gmail.com");
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getParentId(), 7);
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getStudyId(), newSite.getId());
			assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getSelectedVersionIds(), "5");
			assertEquals(
					SourceDataVerification
							.getByDescription(restOdmContainer.getRestData().getEventDefinitionCRFBean().getSdvCode()),
					SourceDataVerification.NOTREQUIRED);
			assertEquals(Status.getByName(restOdmContainer.getRestData().getEventDefinitionCRFBean().getStatusCode()),
					Status.AVAILABLE);
			assertTrue(restOdmContainer.getRestData().getEventDefinitionCRFBean().getId() > 0);
		}
	}

	@Test
	public void testThatEditSiteCrfMethodCanUnsetTheEmailWhenParameter() throws Exception {
		createNewSite(currentScope.getId());
		EventDefinitionCRFBean eventDefinitionCRFBean = createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0").param("availableVersions", "5")
				.param("emailWhen", "complete").param("email", "clinovo@gmail.com").param("required", "false")
				.param("passwordRequired", "true").param("hide", "true").param("sourceDataVerification", "3")
				.param("dataEntryQuality", "dde").param("tabbing", "topToBottom")).andExpect(status().isOk());
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO
				.findByPK(eventDefinitionCRFBean.getId());
		assertTrue(eventDefinitionCRFBean.getEmailStep().equals("complete"));
		assertTrue(eventDefinitionCRFBean.getEmailTo().equals("clinovo@gmail.com"));
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0").param("emailWhen", "none")
				.param("required", "false").param("passwordRequired", "true").param("hide", "true")
				.param("sourceDataVerification", "3").param("dataEntryQuality", "dde").param("tabbing", "topToBottom"))
				.andExpect(status().isOk());
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO
				.findByPK(eventDefinitionCRFBean.getId());
		assertTrue(eventDefinitionCRFBean.getEmailStep().isEmpty());
		assertTrue(eventDefinitionCRFBean.getEmailTo().isEmpty());
	}

	@Test
	public void testThatEditSiteCrfMethodCanUnsetTheDataEntryQualityParameter() throws Exception {
		createNewSite(currentScope.getId());
		EventDefinitionCRFBean eventDefinitionCRFBean = createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0").param("availableVersions", "5")
				.param("emailWhen", "complete").param("email", "clinovo@gmail.com").param("required", "false")
				.param("passwordRequired", "true").param("hide", "true").param("sourceDataVerification", "3")
				.param("dataEntryQuality", "dde").param("tabbing", "topToBottom")).andExpect(status().isOk());
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO
				.findByPK(eventDefinitionCRFBean.getId());
		assertTrue(eventDefinitionCRFBean.isDoubleEntry());
		assertFalse(eventDefinitionCRFBean.isEvaluatedCRF());
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0").param("emailWhen", "none")
				.param("required", "false").param("passwordRequired", "true").param("hide", "true")
				.param("sourceDataVerification", "3").param("dataEntryQuality", "none").param("tabbing", "topToBottom"))
				.andExpect(status().isOk());
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO
				.findByPK(eventDefinitionCRFBean.getId());
		assertFalse(eventDefinitionCRFBean.isDoubleEntry());
		assertFalse(eventDefinitionCRFBean.isEvaluatedCRF());
	}

	@Test
	public void testThatEditSiteCrfMethodReturnsExceptionIfEmailParameterIsPresentWithoutEmailWhenParameter()
			throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0")
				.param("email", "clinovo@gmail.com").param("required", "false").param("passwordRequired", "true")
				.param("hide", "true").param("sourceDataVerification", "3").param("dataEntryQuality", "dde")
				.param("tabbing", "topToBottom")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditSiteCrfMethodReturnsExceptionIfEmailParameterIsNotPresentButEmailWhenParameterIsPresent()
			throws Exception {
		createNewSite(currentScope.getId());
		createChildEDCForNewSite(7, newSite);
		mockMvc.perform(post(API_EVENT_EDIT_SITE_CRF).param("eventId", "9").param("crfName", "Test CRF")
				.param("siteName", newSite.getName()).param("defaultVersion", "v1.0").param("emailWhen", "sign")
				.param("required", "false").param("passwordRequired", "true").param("hide", "true")
				.param("sourceDataVerification", "3").param("dataEntryQuality", "dde").param("tabbing", "topToBottom"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThaGetEventsMethodReturnsStudyEventDefinitionsCorrectly() throws Exception {
		result = mockMvc.perform(get(API_EVENTS)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionList().size(), 9);
		}
	}

	@Test
	public void testThatGetEventsMethodDoesNotSupportTheHttpPost() throws Exception {
		mockMvc.perform(post(API_EVENTS)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEventsOrderMethodDoesNotSupportTheHttpGet() throws Exception {
		mockMvc.perform(get(API_EVENTS_ORDER).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToOrderEventsIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENTS_ORDER)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToOrderEventsIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENTS_ORDER).param("id", "")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToOrderEventsIfIdParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_EVENTS_ORDER).param("iD", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToOrderEventsIfIdParameterHasWrongData() throws Exception {
		mockMvc.perform(post(API_EVENTS_ORDER).param("id", "asd")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEventsOrderMethodThrowsExceptionIfWrongQuantityOfIdsArePassed() throws Exception {
		mockMvc.perform(post(API_EVENTS_ORDER).param("id", "1")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEventsOrderMethodThrowsExceptionIfWrongIdIsPassed() throws Exception {
		mockMvc.perform(post(API_EVENTS_ORDER).param("id", "1").param("id", "2").param("id", "3").param("id", "4")
				.param("id", "5").param("id", "6").param("id", "7").param("id", "8").param("id", "111167"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEventsOrderMethodWorksFine() throws Exception {
		mockMvc.perform(post(API_EVENTS_ORDER).param("id", "1").param("id", "2").param("id", "3").param("id", "4")
				.param("id", "5").param("id", "6").param("id", "7").param("id", "8").param("id", "9"))
				.andExpect(status().isOk());
		List<StudyEventDefinitionBean> studyEventDefinitionBeanList = eventDefinitionService
				.getAllStudyEventDefinitions(defaultStudy);
		for (StudyEventDefinitionBean studyEventDefinitionBean : studyEventDefinitionBeanList) {
			if (studyEventDefinitionBean.getId() == 1) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 0);
			} else if (studyEventDefinitionBean.getId() == 2) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 1);
			} else if (studyEventDefinitionBean.getId() == 3) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 2);
			} else if (studyEventDefinitionBean.getId() == 4) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 3);
			} else if (studyEventDefinitionBean.getId() == 5) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 4);
			} else if (studyEventDefinitionBean.getId() == 6) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 5);
			} else if (studyEventDefinitionBean.getId() == 7) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 6);
			} else if (studyEventDefinitionBean.getId() == 8) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 7);
			} else if (studyEventDefinitionBean.getId() == 9) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 8);
			}
		}
		mockMvc.perform(post(API_EVENTS_ORDER).param("id", "7").param("id", "2").param("id", "5").param("id", "4")
				.param("id", "3").param("id", "9").param("id", "1").param("id", "8").param("id", "6"))
				.andExpect(status().isOk());
		studyEventDefinitionBeanList = eventDefinitionService.getAllStudyEventDefinitions(defaultStudy);
		for (StudyEventDefinitionBean studyEventDefinitionBean : studyEventDefinitionBeanList) {
			if (studyEventDefinitionBean.getId() == 7) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 0);
			} else if (studyEventDefinitionBean.getId() == 2) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 1);
			} else if (studyEventDefinitionBean.getId() == 5) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 2);
			} else if (studyEventDefinitionBean.getId() == 4) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 3);
			} else if (studyEventDefinitionBean.getId() == 3) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 4);
			} else if (studyEventDefinitionBean.getId() == 9) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 5);
			} else if (studyEventDefinitionBean.getId() == 1) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 6);
			} else if (studyEventDefinitionBean.getId() == 8) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 7);
			} else if (studyEventDefinitionBean.getId() == 6) {
				assertEquals(studyEventDefinitionBean.getOrdinal(), 8);
			}
		}
	}

	@Test
	public void testThatEventCrfsOrderMethodDoesNotSupportTheHttpGet() throws Exception {
		mockMvc.perform(get(API_EVENT_CRFS_ORDER).param("eventId", "1").param("id", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToOrderEventCrfsIfIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_CRFS_ORDER).param("eventId", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToOrderEventCrfsIfIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_CRFS_ORDER).param("eventId", "1").param("id", ""))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToOrderEventCrfsIfIdParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_EVENT_CRFS_ORDER).param("eventId", "1").param("iD", "1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToOrderEventCrfsIfIdParameterHasWrongData() throws Exception {
		mockMvc.perform(post(API_EVENT_CRFS_ORDER).param("eventId", "1").param("id", "asd"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToOrderEventCrfsIfEventIdParameterIsMissing() throws Exception {
		mockMvc.perform(post(API_EVENT_CRFS_ORDER).param("id", "1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToOrderEventCrfsIfEventIdParameterIsEmpty() throws Exception {
		mockMvc.perform(post(API_EVENT_CRFS_ORDER).param("eventId", "").param("id", "1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToOrderEventCrfsIfEventIdParameterHasTypo() throws Exception {
		mockMvc.perform(post(API_EVENT_CRFS_ORDER).param("evEntId", "1").param("id", "1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToOrderEventCrfsIfEventIdParameterHasWrongData() throws Exception {
		mockMvc.perform(post(API_EVENT_CRFS_ORDER).param("eventId", "dsfsdf").param("id", "1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEventCrfsOrderMethodThrowsExceptionIfWrongQuantityOfIdsArePassed() throws Exception {
		mockMvc.perform(post(API_EVENT_CRFS_ORDER).param("eventId", "1").param("id", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEventCrfsOrderMethodThrowsExceptionIfWrongIdIsPassed() throws Exception {
		mockMvc.perform(post(API_EVENT_CRFS_ORDER).param("eventId", "1").param("id", "2").param("id", "111167"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEventCrfsOrderMethodWorksFine() throws Exception {
		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		mockMvc.perform(
				post(API_EVENT_CRFS_ORDER).param("eventId", "1").param("id", "6").param("id", "1").param("id", "2"))
				.andExpect(status().isOk());
		List<EventDefinitionCRFBean> allParentEventDefinitionCRFs = (List<EventDefinitionCRFBean>) eventDefinitionCRFDAO
				.findAllParentsByDefinition(1);
		for (EventDefinitionCRFBean eventDefinitionCRFBean : allParentEventDefinitionCRFs) {
			if (eventDefinitionCRFBean.getCrfId() == 6) {
				assertEquals(eventDefinitionCRFBean.getOrdinal(), 0);
			} else if (eventDefinitionCRFBean.getCrfId() == 1) {
				assertEquals(eventDefinitionCRFBean.getOrdinal(), 1);
			} else if (eventDefinitionCRFBean.getCrfId() == 2) {
				assertEquals(eventDefinitionCRFBean.getOrdinal(), 2);
			}
		}
		mockMvc.perform(
				post(API_EVENT_CRFS_ORDER).param("eventId", "1").param("id", "2").param("id", "6").param("id", "1"))
				.andExpect(status().isOk());
		allParentEventDefinitionCRFs = (List<EventDefinitionCRFBean>) eventDefinitionCRFDAO
				.findAllParentsByDefinition(1);
		for (EventDefinitionCRFBean eventDefinitionCRFBean : allParentEventDefinitionCRFs) {
			if (eventDefinitionCRFBean.getCrfId() == 2) {
				assertEquals(eventDefinitionCRFBean.getOrdinal(), 0);
			} else if (eventDefinitionCRFBean.getCrfId() == 6) {
				assertEquals(eventDefinitionCRFBean.getOrdinal(), 1);
			} else if (eventDefinitionCRFBean.getCrfId() == 1) {
				assertEquals(eventDefinitionCRFBean.getOrdinal(), 2);
			}
		}
	}
}
