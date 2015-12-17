package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

public class EventServiceTest extends BaseServiceTest {

	private String getSymbols(int size) {
		String result = "";
		for (int i = 1; i <= size; i++) {
			result = result.concat("a");
		}
		return result;
	}

	@Test
	public void testThatItIsImpossibleToCreateAStudyEventDefinitionIfTypeParameterHasWrongValue() throws Exception {
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduledfdfdfdfdf")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatCreateMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("xparamX", "2").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatCreateMethodThrowsExceptionIfDescriptionParameterIsInWrongCase() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("descRiption", "olololo!").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsPossibleToCreateStudyEventDefinitionIfNameHas2000Symbols() throws Exception {
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", getSymbols(2000)).param("type", "scheduled")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
	}

	@Test
	public void testThatItIsPossibleToCreateStudyEventDefinitionIfDescriptionHas2000Symbols() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test name").param("description", getSymbols(2000))
						.param("type", "scheduled").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatItIsPossibleToCreateStudyEventDefinitionIfCategoryHas2000Symbols() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test name").param("category", getSymbols(2000))
						.param("type", "scheduled").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfNameHasMoreThan2000Symbols() throws Exception {
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", getSymbols(2001)).param("type", "scheduled")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfDescriptionHasMoreThan2000Symbols()
			throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test name").param("description", getSymbols(2001))
						.param("type", "scheduled").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfCategoryHasMoreThan2000Symbols() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test name").param("category", getSymbols(2001))
						.param("type", "scheduled").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfNameIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "").param("type", "scheduled").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfTypeIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test name").param("type", "").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfTypeIsMissing() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfNameIsMissing() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("type", "scheduled").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionWithWrongType() throws Exception {
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled!")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToCreateCommonStudyEventDefinitionPassingOnlyNameAndType() throws Exception {
		result = this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "common")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "common");
		}
	}

	@Test
	public void testThatItIsPossibleToCreateUnscheduledStudyEventDefinitionPassingOnlyNameAndType() throws Exception {
		result = this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "unscheduled")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "unscheduled");
		}
	}

	@Test
	public void testThatHttpGetIsNotSupportedForCreatingAStudyEventDefinition() throws Exception {
		this.mockMvc.perform(get(API_EVENT_CREATE).param("name", "test_event").param("type", "unscheduled")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToCreateScheduledStudyEventDefinitionPassingOnlyNameAndType() throws Exception {
		result = this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "scheduled");
		}
	}

	@Test
	public void testThatItIsPossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventPassingOnlyNameTypeAndEmailUser()
			throws Exception {
		result = this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("emailuser", "root").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk()).andReturn();
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
		result = this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("isreference", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "calendared_visit");
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getReferenceVisit());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getUserEmailId() == 1);
		}
	}

	@Test
	public void testThatCommonStudyEventDefinitionIsCreatedCorrectly() throws Exception {
		result = this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "common")
						.param("description", "test description").param("category", "test category")
						.param("repeating", "true").accept(mediaType).secure(true).session(session))
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
		result = this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "unscheduled")
						.param("description", "test description").param("category", "test category")
						.param("repeating", "true").accept(mediaType).secure(true).session(session))
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
		result = this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("description", "test description").param("category", "test category")
						.param("repeating", "true").accept(mediaType).secure(true).session(session))
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

		result = this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("isreference", "true").accept(mediaType).secure(true).session(session))
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
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getId() > 0);

		}
	}

	@Test
	public void testThatCalendaredStudyEventDefinitionThatIsNotReferenceEventIsCreatedCorrectly() throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		result = this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("schday", "4").param("maxday", "4").param("minday", "3").param("emailday", "2")
						.param("emailuser", newUser.getName()).accept(mediaType).secure(true).session(session))
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
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category")
				.param("isreference", "true").param("maxday", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateReferencedCalendaredEventIfMinDayIsSpecified() throws Exception {
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category")
				.param("isreference", "true").param("minday", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateReferencedCalendaredEventIfSchDayIsSpecified() throws Exception {
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category")
				.param("isreference", "true").param("schday", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateReferencedCalendaredEventIfEmailDayIsSpecified() throws Exception {
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category")
				.param("isreference", "true").param("emailday", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateReferencedCalendaredEventIfUserNameIsSpecified() throws Exception {
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("description", "test description").param("category", "test category")
				.param("isreference", "true").param("emailuser", "root").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateNonCalendaredEventIfMaxDayIsSpecified() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("description", "test description").param("category", "test category")
						.param("maxday", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateNonCalendaredEventIfMinDayIsSpecified() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("description", "test description").param("category", "test category")
						.param("minday", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateNonCalendaredEventIfSchDayIsSpecified() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("description", "test description").param("category", "test category")
						.param("schday", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateNonCalendaredEventIfEmailDayIsSpecified() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("description", "test description").param("category", "test category")
						.param("emailday", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateNonCalendaredEventIfUserNameIsSpecified() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("description", "test description").param("category", "test category")
						.param("emailuser", "root").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateReferencedNonCalendaredEvent() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("description", "test description").param("category", "test category")
						.param("isreference", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateTheRepeatingCalendaredStudyEventDefinition() throws Exception {
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
				.param("repeating", "true").param("description", "test description").param("category", "test category")
				.param("schday", "4").param("maxday", "4").param("minday", "3").param("emailday", "2")
				.param("emailuser", userName).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfSchDayHasWrongType() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("schday", "a").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfEmailDayHasWrongType() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("emailday", "a").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfMaxDayHasWrongType() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("maxday", "a").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfMinDayHasWrongType() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("minday", "a").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfIsReferenceHasWrongType() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("isreference", "asdfadsf").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfRepeatingHasWrongType() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("repeating", "asdfadsf").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfSchDayMoreThenMaxDay()
			throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("schday", "5").param("maxday", "4").param("minday", "3").param("emailday", "2")
						.param("emailuser", newUser.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfMinDayMoreThenSchDay()
			throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("schday", "1").param("maxday", "4").param("minday", "3").param("emailday", "1")
						.param("emailuser", newUser.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfMinDayMoreThenMaxDay()
			throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("schday", "5").param("maxday", "5").param("minday", "7").param("emailday", "2")
						.param("emailuser", newUser.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfEmailDayMoreThenSchDay()
			throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("schday", "5").param("maxday", "5").param("minday", "7").param("emailday", "9")
						.param("emailuser", newUser.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfEmailUserDoesNotHaveScopeRole()
			throws Exception {
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, studyName);
		this.mockMvc
				.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("schday", "4").param("maxday", "4").param("minday", "3").param("emailday", "2")
						.param("emailuser", newUser.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionThatDoesNotExist() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1234").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatAddCrfMethodThrowsExceptionIfDefaultVersionParameterIsInWrongCase() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
						.param("defaultvErsion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAddCrfMethodThrowsExceptionIfCrfNameParameterIsInWrongCase() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfnAme", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAddCrfMethodThrowsExceptionIfEventIdParameterIsInWrongCase() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("evEntid", "1").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAddCrfMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		this.mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("xparamX", "56").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfCrfDoesNotExist() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "xxxxxx")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfDefaultCrfVersionDoesNotExist() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
						.param("defaultversion", "v1.000000").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfCrfVersionIsLocked() throws Exception {
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(5);
		crfVersionBean.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));
		crfVersionBean.setStatus(Status.LOCKED);
		crfVersionDao.update(crfVersionBean);
		crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(5);
		assertTrue(crfVersionBean.getStatus().equals(Status.LOCKED));
		this.mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
				.param("defaultversion", crfVersionBean.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfCrfVersionIsDeleted() throws Exception {
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(5);
		crfVersionBean.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));
		crfVersionBean.setStatus(Status.DELETED);
		crfVersionDao.update(crfVersionBean);
		crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(5);
		assertTrue(crfVersionBean.getStatus().equals(Status.DELETED));
		this.mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
				.param("defaultversion", crfVersionBean.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfEventIdParameterIsMissing() throws Exception {
		this.mockMvc.perform(post(API_EVENT_ADD_CRF).param("crfname", "Test CRF").param("defaultversion", "v1.000000")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfEventIdParameterIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "").param("crfname", "Test CRF")
						.param("defaultversion", "v1.000000").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfCrfNameParameterIsMissing() throws Exception {
		this.mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("defaultversion", "v1.000000")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfCrfNameParameterIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "")
						.param("defaultversion", "v1.000000").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfDefaultVersionParameterIsMissing()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfDefaultVersionParameterIsEmpty()
			throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
						.param("defaultversion", "").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfSourceDataVerificationParameterHasWrongValue()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("sourcedataverification", "45").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfDataEntryQualityParameterHasWrongValue()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("dataentryquality", "x").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfEmailWhenParameterHasWrongValue()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("emailwhen", "z").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfTabbingParameterHasWrongValue() throws Exception {
		this.mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("tabbing", "ppp").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionIfEmailParameterHasWrongValue() throws Exception {
		this.mockMvc.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("emailwhen", "complete").param("email", "sdfsdfsdf")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItImpossibleToAddCrfToStudyEventDefinitionThatDoesNotBelongToCurrentScope() throws Exception {
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToAddCrfToStudyEventDefinitionTwice() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToAddCrfToStudyEventDefinitionIfOnlyRequiredParametersArePassed() throws Exception {
		result = this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk()).andReturn();
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
		result = this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").param("emailwhen", "complete")
						.param("email", "clinovo@gmail.com").param("required", "false")
						.param("passwordrequired", "true").param("hide", "true").param("sourcedataverification", "1")
						.param("dataentryquality", "dde").param("tabbing", "topToBottom")
						.param("acceptnewcrfversions", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk()).andReturn();
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
		this.mockMvc
				.perform(get(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatInfoAboutExistingStudyEventDefinitionIsReturnedCorrectly() throws Exception {
		result = this.mockMvc.perform(get(API_EVENT).param("id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "ED-1-NonRepeating");
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getEventDefinitionCrfs().size(),
					3);
		}
	}

	@Test
	public void testThatItIsImpossibleToGetInfoAboutNonExistingStudyEventDefinition() throws Exception {
		this.mockMvc.perform(get(API_EVENT).param("id", "413341").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToGetInfoAboutExistingStudyEventDefinitionThatDoesNotBelongToCurrentScope()
			throws Exception {
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		this.mockMvc.perform(get(API_EVENT).param("id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatGetInfoThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		this.mockMvc.perform(get(API_EVENT).param("id", "").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatGetInfoThrowsExceptionIfIdParameterIsMissing() throws Exception {
		this.mockMvc.perform(get(API_EVENT).accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatGetInfoThrowsExceptionIfIdParameterIsInWrongCase() throws Exception {
		this.mockMvc.perform(get(API_EVENT).param("Id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatGetInfoThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		this.mockMvc.perform(
				get(API_EVENT).param("id", "1").param("xparamX", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditANonExistingStudyEventDefinition() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1231").param("name", "new name!").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditAStudyEventDefinitionIfIdParameterIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditAStudyEventDefinitionIfIdParameterIsMissing() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatAtLeastOneNotRequiredParameterShouldBeSpecifiedDuringEditingAStudyEventDefinition()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatHttpGetMethodIsNotSupportedForEditingOfAStudyEventDefinition() throws Exception {
		this.mockMvc.perform(get(API_EVENT_EDIT).param("id", "1").param("name", "new name!").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditAStudyEventDefinitionThatDoesNotBelongToCurrentScope() throws Exception {
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", "new name!").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToChangeTheNameInAStudyEventDefinition() throws Exception {
		String newName = "new test name!";
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", newName).accept(mediaType).secure(true)
				.session(session)).andExpect(status().isOk());
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(1);
		assertEquals(studyEventDefinitionBean.getName(), newName);
	}

	@Test
	public void testThatItIsPossibleToChangeTheDescriptionInAStudyEventDefinition() throws Exception {
		String newDescription = "new test description!!!!";
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("description", newDescription)
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(1);
		assertEquals(studyEventDefinitionBean.getDescription(), newDescription);
	}

	@Test
	public void testThatItIsPossibleToChangeTheCategoryInAStudyEventDefinition() throws Exception {
		String newCategory = "new test category!!!!";
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("category", newCategory).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isOk());
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(1);
		assertEquals(studyEventDefinitionBean.getCategory(), newCategory);
	}

	@Test
	public void testThatItIsPossibleToChangeTheRepeatingPropertyInAStudyEventDefinition() throws Exception {
		String repeating = "false";
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("repeating", repeating).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isOk());
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(1);
		assertFalse(studyEventDefinitionBean.isRepeating());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheMaxDayForANonCalendaredStudyEventDefinition() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("maxday", "1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheMinDayForANonCalendaredStudyEventDefinition() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("minday", "1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheSchDayForANonCalendaredStudyEventDefinition() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schday", "1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheEmailDayForANonCalendaredStudyEventDefinition() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("emailday", "1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheEmailUserForANonCalendaredStudyEventDefinition() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("emailuser", "1").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheIsReferencePropertyForANonCalendaredStudyEventDefinition()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("isreference", "true").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheRepeatingPropertyForACalendaredStudyEventDefinition()
			throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit")
						.param("emailuser", "root").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("repeating", "true").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheMaxDayForAReferencedCalendaredStudyEventDefinition() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit")
						.param("isreference", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("maxday", "1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheMinDayForAReferencedCalendaredStudyEventDefinition() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit")
						.param("isreference", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("minday", "1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheSchDayForAReferencedCalendaredStudyEventDefinition() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit")
						.param("isreference", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schday", "1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheEmailDayForAReferencedCalendaredStudyEventDefinition()
			throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit")
						.param("isreference", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("emailday", "1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeTheEmailUserForAReferencedCalendaredStudyEventDefinition()
			throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit")
						.param("isreference", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("emailuser", "1").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeCalendaredStudyEventDefinitionThatIsNotReferenceEventIfSchDayMoreThenMaxDay()
			throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit")
						.param("emailuser", "root").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schday", "5").param("maxday", "4")
				.param("minday", "3").param("emailday", "2").param("emailuser", newUser.getName()).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeCalendaredStudyEventDefinitionThatIsNotReferenceEventIfMinDayMoreThenSchDay()
			throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit")
						.param("emailuser", "root").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schday", "1").param("maxday", "4")
				.param("minday", "3").param("emailday", "1").param("emailuser", newUser.getName()).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeCalendaredStudyEventDefinitionThatIsNotReferenceEventIfMinDayMoreThenMaxDay()
			throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit")
						.param("emailuser", "root").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schday", "5").param("maxday", "5")
				.param("minday", "7").param("emailday", "2").param("emailuser", newUser.getName()).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeCalendaredStudyEventDefinitionThatIsNotReferenceEventIfEmailDayMoreThenSchDay()
			throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit")
						.param("emailuser", "root").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schday", "5").param("maxday", "5")
				.param("minday", "7").param("emailday", "9").param("emailuser", newUser.getName()).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeCalendaredStudyEventDefinitionThatIsNotReferenceEventIfEmailUserDoesNotHaveScopeRole()
			throws Exception {
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, studyName);
		this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "calendared_visit")
						.param("emailuser", "root").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schday", "4").param("maxday", "4")
				.param("minday", "3").param("emailday", "2").param("emailuser", newUser.getName()).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfTypeHasWrongType() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("type", "scheduled!!!!").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfSchDayHasWrongType() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("schday", "a").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfEmailDayHasWrongType() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("emailday", "a").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfMaxDayHasWrongType() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("maxday", "a").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfMinDayHasWrongType() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("minday", "a").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfIsReferenceHasWrongType() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("isreference", "asdfadsf").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfRepeatingHasWrongType() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("repeating", "asdfadsf").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatScheduledStudyEventDefinitionIsChangedCorrectlyToTheUnscheduledStudyEventDefinition()
			throws Exception {
		result = this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("name", "test_event").param("type", "unscheduled")
						.param("description", "test description").param("category", "test category")
						.param("repeating", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk()).andReturn();
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
		result = this.mockMvc
				.perform(post(API_EVENT_EDIT).param("id", "1").param("name", "test_event").param("type", "common")
						.param("description", "test description").param("category", "test category")
						.param("repeating", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk()).andReturn();
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
		result = this.mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("isreference", "true").accept(mediaType).secure(true).session(session))
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
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		result = this.mockMvc.perform(
				post(API_EVENT_EDIT).param("id", "1").param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("schday", "4").param("maxday", "4").param("minday", "3").param("emailday", "2")
						.param("emailuser", newUser.getName()).accept(mediaType).secure(true).session(session))
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
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", getSymbols(2000)).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isOk());
	}

	@Test
	public void testThatItIsPossibleToChangeStudyEventDefinitionIfDescriptionHas2000Symbols() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("description", getSymbols(2000))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
	}

	@Test
	public void testThatItIsPossibleToChangeStudyEventDefinitionIfCategoryHas2000Symbols() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("category", getSymbols(2000)).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isOk());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfNameHasMoreThan2000Symbols() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", getSymbols(2001)).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfDescriptionHasMoreThan2000Symbols()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("description", getSymbols(2001))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToChangeStudyEventDefinitionIfCategoryHasMoreThan2000Symbols() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("category", getSymbols(2001)).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToPassASupportedParameterThatIsNotInLowerCase() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("nAme", "bla bla !").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditMethodThrowsExceptionIfIdParameterIsInWrongCase() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("Id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditMethodThrowsExceptionIfNameParameterIsInWrongCase() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("naMe", "ololo").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("xparamX", "1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToRemoveStudyEventDefinitionThatDoesNotExist() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE).param("id", "1345").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE).accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE).param("id", "").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE).param("id", "1").param("ololo", "1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveMethodThrowsExceptionIfIdParameterIsInWrongCase() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE).param("Id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveMethodWorksFineForExistingStudyEventDefinition() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE).param("id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		assertEquals(((StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1)).getStatus(), Status.DELETED);
	}

	@Test
	public void testThatItIsImpossibleToRestoreStudyEventDefinitionThatDoesNotExist() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_RESTORE).param("id", "1345").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreMethodThrowsExceptionIfIdParameterIsMissing() throws Exception {
		this.mockMvc.perform(post(API_EVENT_RESTORE).accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreMethodThrowsExceptionIfIdParameterIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_EVENT_RESTORE).param("id", "").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		this.mockMvc.perform(post(API_EVENT_RESTORE).param("id", "1").param("ololo", "1").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreMethodThrowsExceptionIfIdParameterIsInWrongCase() throws Exception {
		this.mockMvc.perform(post(API_EVENT_RESTORE).param("Id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreMethodWorksFineForExistingStudyEventDefinition() throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(1);
		studyEventDefinitionBean.setStatus(Status.DELETED);
		studyEventDefinitionBean.setUpdater(userBean);
		studyEventDefinitionDAO.updateStatus(studyEventDefinitionBean);
		studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1);
		assertEquals(studyEventDefinitionBean.getStatus(), Status.DELETED);
		this.mockMvc.perform(post(API_EVENT_RESTORE).param("id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1);
		assertEquals(studyEventDefinitionBean.getStatus(), Status.AVAILABLE);
	}

	@Test
	public void testThatSystemAdministratorIsAbleToCallStudyEventAPI() throws Exception {
		createNewSite(studyBean.getId());
		createChildEDCForNewSite(7, newSite);
		ResultMatcher expectStatus = status().isOk();
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("description", "test description").param("category", "test category").accept(mediaType)
				.secure(true).session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", "new_test_event").accept(mediaType)
				.secure(true).session(session)).andExpect(expectStatus);
		this.mockMvc.perform(get(API_EVENT).param("id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc.perform(post(API_EVENT_REMOVE).param("id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc.perform(post(API_EVENT_RESTORE).param("id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventid", "1").param("crfname", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventid", "1").param("crfname", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatStudyAdministratorWithAdministrativePrivilegesIsAbleToCallStudyEventAPI() throws Exception {
		createNewSite(studyBean.getId());
		createChildEDCForNewSite(7, newSite);
		ResultMatcher expectStatus = status().isOk();
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), studyName);
		this.mockMvc.perform(post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
				.param("description", "test description").param("category", "test category").accept(mediaType)
				.secure(true).session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_EVENT_EDIT).param("id", "1").param("name", "new_test_event").accept(mediaType)
				.secure(true).session(session)).andExpect(expectStatus);
		this.mockMvc.perform(get(API_EVENT).param("id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc
				.perform(post(API_EVENT_ADD_CRF).param("eventid", "1").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc.perform(post(API_EVENT_REMOVE).param("id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc.perform(post(API_EVENT_RESTORE).param("id", "1").accept(mediaType).secure(true).session(session))
				.andExpect(expectStatus);
		this.mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventid", "1").param("crfname", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventid", "1").param("crfname", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatItIsImpossibleToRemoveEventDefinitionCrfFromStudyEventDefinitionThatDoesNotExist()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventid", "1345")
				.param("crfname", "Agent Administration").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRemoveEventDefinitionCrfThatDoesNotExist() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventid", "1").param("crfname", "Another CRF")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRemoveEventDefinitionCrfForCrfThatDoesNotExist() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventid", "1").param("crfname", "XXXXXXXXXX")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfEventIdParameterIsMissing() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("crfname", "Agent Administration").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfCrfNameParameterIsMissing() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_REMOVE_CRF).param("eventid", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfEventIdParameterIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventid", "").param("crfname", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfCrfNameParameterIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventid", "1").param("crfname", "").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_REMOVE_CRF).param("eventid", "1").param("crfname", "Agent Administration")
						.param("ololo", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfEventIdParameterIsInWrongCase() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventId", "1").param("crfname", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodThrowsExceptionIfCrfNameParameterIsInWrongCase() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventid", "1").param("crfName", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRemoveCrfMethodWorksFineForExistingEventDefinitionCrf() throws Exception {
		this.mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventid", "1").param("crfname", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		assertEquals(((EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1)).getStatus(), Status.DELETED);
	}

	@Test
	public void testThatItIsImpossibleToMakeRemoveOperationOnEventDefinitionCrfIfStudyEventDefinitionIsNotAvailable()
			throws Exception {
		setStatusForSED(1, Status.DELETED);
		this.mockMvc.perform(post(API_EVENT_REMOVE_CRF).param("eventid", "1").param("crfname", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRestoreEventDefinitionCrfFromStudyEventDefinitionThatDoesNotExist()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventid", "1345")
				.param("crfname", "Agent Administration").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRestoreEventDefinitionCrfThatDoesNotExist() throws Exception {
		this.mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventid", "1").param("crfname", "Another CRF")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToRestoreEventDefinitionCrfForCrfThatDoesNotExist() throws Exception {
		this.mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventid", "1").param("crfname", "XXXXXXXXXX")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfEventIdParameterIsMissing() throws Exception {
		this.mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("crfname", "Agent Administration").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfCrfNameParameterIsMissing() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_RESTORE_CRF).param("eventid", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfEventIdParameterIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventid", "").param("crfname", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfCrfNameParameterIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventid", "1").param("crfname", "").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_RESTORE_CRF).param("eventid", "1").param("crfname", "Agent Administration")
						.param("ololo", "1").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfEventIdParameterIsInWrongCase() throws Exception {
		this.mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventId", "1").param("crfname", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodThrowsExceptionIfCrfNameParameterIsInWrongCase() throws Exception {
		this.mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventid", "1").param("crfName", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatRestoreCrfMethodWorksFineForExistingEventDefinitionCrf() throws Exception {
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		eventDefinitionCRFBean.setStatus(Status.DELETED);
		eventDefinitionCRFBean.setUpdater(userBean);
		eventDefinitionCRFDAO.updateStatus(eventDefinitionCRFBean);
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		assertEquals(eventDefinitionCRFBean.getStatus(), Status.DELETED);
		this.mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventid", "1").param("crfname", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		assertEquals(eventDefinitionCRFBean.getStatus(), Status.AVAILABLE);
	}

	@Test
	public void testThatItIsImpossibleToMakeRestoreOperationOnEventDefinitionCrfIfStudyEventDefinitionIsNotAvailable()
			throws Exception {
		setStatusForSED(1, Status.DELETED);
		this.mockMvc.perform(post(API_EVENT_RESTORE_CRF).param("eventid", "1").param("crfname", "Agent Administration")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionThatDoesNotExist() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "1234").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyCrfMethodThrowsExceptionIfDefaultVersionParameterIsInWrongCase() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultvErsion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyCrfMethodThrowsExceptionIfCrfNameParameterIsInWrongCase() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfnAme", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyCrfMethodThrowsExceptionIfEventIdParameterIsInWrongCase() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("evEntid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatEditStudyCrfMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("xparamX", "56").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfCrfDoesNotExist() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "xxxxxx")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfDefaultCrfVersionDoesNotExist()
			throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.000000").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfCrfVersionIsLocked() throws Exception {
		CRFVersionBean crfVersionBean = setStatusForCrfVersion(5, Status.LOCKED);
		this.mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
				.param("defaultversion", crfVersionBean.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfCrfVersionIsDeleted() throws Exception {
		CRFVersionBean crfVersionBean = setStatusForCrfVersion(5, Status.DELETED);
		this.mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
				.param("defaultversion", crfVersionBean.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfEDCIsLocked() throws Exception {
		setStatusForEDC(7, Status.LOCKED);
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfEDCIsDeleted() throws Exception {
		setStatusForEDC(7, Status.DELETED);
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfEventIdParameterIsMissing()
			throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("crfname", "Test CRF")
						.param("defaultversion", "v1.000000").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfEventIdParameterIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "").param("crfname", "Test CRF")
						.param("defaultversion", "v1.000000").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfCrfNameParameterIsMissing()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("defaultversion", "v1.000000")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfCrfNameParameterIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "")
						.param("defaultversion", "v1.000000").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfSourceDataVerificationParameterHasWrongValue()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("sourcedataverification", "45").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfDataEntryQualityParameterHasWrongValue()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("dataentryquality", "x").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfEmailWhenParameterHasWrongValue()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("emailwhen", "z").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfTabbingParameterHasWrongValue()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("tabbing", "ppp").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToEditStudyCrfOfStudyEventDefinitionIfEmailParameterHasWrongValue()
			throws Exception {
		this.mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("emailwhen", "complete").param("email", "sdfsdfsdf")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItImpossibleToEditStudyCrfOfStudyEventDefinitionThatDoesNotBelongToCurrentScope()
			throws Exception {
		createNewStudy();
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newStudy.getName());
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatHttpGetIsNotSupportedForEditingStudyCrfOfStudyEventDefinition() throws Exception {
		this.mockMvc
				.perform(get(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToEditStudyCrfOfStudyEventDefinitionIfOnlyRequiredParametersArePassed()
			throws Exception {
		result = this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").accept(mediaType).secure(true).session(session))
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
		result = this.mockMvc.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
				.param("defaultversion", "v1.0").param("emailwhen", "complete")
				.param("email", "clinovo@gmail.com, clinovo2@gmail.com").param("required", "false")
				.param("passwordrequired", "true").param("hide", "true").param("sourcedataverification", "3")
				.param("dataentryquality", "dde").param("tabbing", "topToBottom").param("acceptnewcrfversions", "true")
				.param("propagatechange", "3").accept(mediaType).secure(true).session(session))
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
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").param("emailwhen", "complete")
						.param("email", "clinovo@gmail.com").param("required", "false")
						.param("passwordrequired", "true").param("hide", "true").param("sourcedataverification", "3")
						.param("dataentryquality", "dde").param("tabbing", "topToBottom")
						.param("acceptnewcrfversions", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(7);
		assertTrue(eventDefinitionCRFBean.getEmailStep().equals("complete"));
		assertTrue(eventDefinitionCRFBean.getEmailTo().equals("clinovo@gmail.com"));
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").param("emailwhen", "none").param("required", "false")
						.param("passwordrequired", "true").param("hide", "true").param("sourcedataverification", "3")
						.param("dataentryquality", "dde").param("tabbing", "topToBottom")
						.param("acceptnewcrfversions", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(7);
		assertTrue(eventDefinitionCRFBean.getEmailStep().isEmpty());
		assertTrue(eventDefinitionCRFBean.getEmailTo().isEmpty());
	}

	@Test
	public void testThatEditStudyCrfMethodCanUnsetTheDataEntryQualityParameter() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").param("emailwhen", "complete")
						.param("email", "clinovo@gmail.com").param("required", "false")
						.param("passwordrequired", "true").param("hide", "true").param("sourcedataverification", "3")
						.param("dataentryquality", "dde").param("tabbing", "topToBottom")
						.param("acceptnewcrfversions", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(7);
		assertTrue(eventDefinitionCRFBean.isDoubleEntry());
		assertFalse(eventDefinitionCRFBean.isEvaluatedCRF());
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").param("emailwhen", "none").param("required", "false")
						.param("passwordrequired", "true").param("hide", "true").param("sourcedataverification", "3")
						.param("dataentryquality", "none").param("tabbing", "topToBottom")
						.param("acceptnewcrfversions", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isOk());
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(7);
		assertFalse(eventDefinitionCRFBean.isDoubleEntry());
		assertFalse(eventDefinitionCRFBean.isEvaluatedCRF());
	}

	@Test
	public void testThatEditStudyCrfMethodReturnsExceptionIfEmailParameterIsPresentWithoutEmailWhenParameter()
			throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").param("email", "clinovo@gmail.com").param("required", "false")
						.param("passwordrequired", "true").param("hide", "true").param("sourcedataverification", "3")
						.param("dataentryquality", "dde").param("tabbing", "topToBottom")
						.param("acceptnewcrfversions", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatEditStudyCrfMethodReturnsExceptionIfEmailParameterIsNotPresentButEmailWhenParameterIsPresent()
			throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_EDIT_STUDY_CRF).param("eventid", "9").param("crfname", "Test CRF")
						.param("defaultversion", "v1.0").param("emailwhen", "sign").param("required", "false")
						.param("passwordrequired", "true").param("hide", "true").param("sourcedataverification", "3")
						.param("dataentryquality", "dde").param("tabbing", "topToBottom")
						.param("acceptnewcrfversions", "true").accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatHttpGetIsNotSupportedForDeleteCrfMethod() throws Exception {
		this.mockMvc.perform(get(API_EVENT_DELETE_CRF).param("eventid", "9").param("crfname", "Test CRF")
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatDeleteCrfMethodDeletesEventDefinitionCrfSuccessfully() throws Exception {
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(7);
		eventDefinitionCRFBean.setId(0);
		eventDefinitionCRFBean.setCrfId(6);
		eventDefinitionCRFBean.setDefaultVersionId(8);
		eventDefinitionCRFBean.setDefaultVersionName("v1.0");
		eventDefinitionCRFDAO.create(eventDefinitionCRFBean);
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "9").param("crfname", "Case Completion")
						.accept(mediaType).requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isOk());
		assertTrue(eventDefinitionCRFDAO.findByPK(eventDefinitionCRFBean.getId()).getId() == 0);
	}

	@Test
	public void testThatDelteCrfMethodThrowsExceptionIfThereAreCRFsWithRules() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "1").param("crfname", "Agent Administration")
						.accept(mediaType).requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatDeleteCrfMethodThrowsExceptionIfThereAreCRFsWithData() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "1").param("crfname", "Concomitant Medications AG")
						.accept(mediaType).requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfCrfNameParameterIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "9").param("crfname", "").accept(mediaType)
						.requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfCrfNameParameterIsMissing() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "9").accept(mediaType)
						.requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfCrfNameParameterIsInWrongCase() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "9").param("crfName", "Test CRF").accept(mediaType)
						.requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfEventIdParameterIsEmpty() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "").param("crfname", "Test CRF").accept(mediaType)
						.requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfEventIdParameterIsMissing() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("crfname", "Test CRF").accept(mediaType)
						.requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfEventIdParameterIsInWrongCase() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventId", "9").param("crfname", "Test CRF").accept(mediaType)
						.requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatDeleteCrfMethodThrowsExceptionIfWePassParameterThatIsNotSupported() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_DELETE_CRF).param("eventid", "9").param("crfname", "Test CRF").param("XP", "bla")
						.accept(mediaType).requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfCrfDoesNotExist() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "9").param("crfname", "XXXXCRF").accept(mediaType)
						.requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfIfEDCDoesNotExist() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "9").param("crfname", "Concomitant Medications AG")
						.accept(mediaType).requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfOfStudyEventDefinitionThatDoesNotExist() throws Exception {
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "11119").param("crfname", "Test CRF")
						.accept(mediaType).requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfOfStudyEventDefinitionIfEDCIsLocked() throws Exception {
		setStatusForEDC(7, Status.LOCKED);
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "9").param("crfname", "Test CRF").accept(mediaType)
						.requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfOfStudyEventDefinitionIfEDCIsDeleted() throws Exception {
		setStatusForEDC(7, Status.DELETED);
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "9").param("crfname", "Test CRF").accept(mediaType)
						.requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfOfStudyEventDefinitionThatIsLocked() throws Exception {
		setStatusForSED(9, Status.LOCKED);
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "9").param("crfname", "Test CRF").accept(mediaType)
						.requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToDeleteCrfOfStudyEventDefinitionThatIsDeleted() throws Exception {
		setStatusForSED(9, Status.DELETED);
		this.mockMvc
				.perform(post(API_EVENT_DELETE_CRF).param("eventid", "9").param("crfname", "Test CRF").accept(mediaType)
						.requestAttr("ruleSetService", ruleSetService).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}
}
