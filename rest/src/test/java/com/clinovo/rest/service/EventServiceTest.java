package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.junit.Test;
import org.springframework.http.MediaType;

public class EventServiceTest extends BaseServiceTest {

	private String getSymbols(int size) {
		String result = "";
		for (int i = 1; i <= size; i++) {
			result = result.concat("a");
		}
		return result;
	}

	@Test
	public void testThatItIsImpossibleToCreateAStudyEventDefinitionAtSiteLevel() throws Exception {
		createNewStudy();
		createNewSite(newStudy.getId());
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newSite.getName());
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled").accept(mediaType)
						.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToCreateStudyEventDefinitionIfNameHas2000Symbols() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", getSymbols(2000)).param("type", "scheduled").accept(mediaType)
						.secure(true).session(session)).andExpect(status().isOk());
	}

	@Test
	public void testThatItIsPossibleToCreateStudyEventDefinitionIfDescriptionHas2000Symbols() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test name").param("description", getSymbols(2000))
						.param("type", "scheduled").accept(mediaType).secure(true).session(session)).andExpect(
				status().isOk());
	}

	@Test
	public void testThatItIsPossibleToCreateStudyEventDefinitionIfCategoryHas2000Symbols() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test name").param("category", getSymbols(2000))
						.param("type", "scheduled").accept(mediaType).secure(true).session(session)).andExpect(
				status().isOk());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfNameHasMoreThan2000Symbols() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", getSymbols(2001)).param("type", "scheduled").accept(mediaType)
						.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfDescriptionHasMoreThan2000Symbols()
			throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test name").param("description", getSymbols(2001))
						.param("type", "scheduled").accept(mediaType).secure(true).session(session)).andExpect(
				status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfCategoryHasMoreThan2000Symbols() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test name").param("category", getSymbols(2001))
						.param("type", "scheduled").accept(mediaType).secure(true).session(session)).andExpect(
				status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfNameIsEmpty() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "").param("type", "scheduled").accept(mediaType).secure(true)
						.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfTypeIsEmpty() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test name").param("type", "").accept(mediaType).secure(true)
						.session(session)).andExpect(status().isBadRequest());
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
				post(API_EVENT_CREATE).param("type", "scheduled!").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionWithWrongType() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled!").accept(mediaType)
						.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsPossibleToCreateCommonStudyEventDefinitionPassingOnlyNameAndType() throws Exception {
		result = this.mockMvc
				.perform(
						post(API_EVENT_CREATE).param("name", "test_event").param("type", "common").accept(mediaType)
								.secure(true).session(session)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "common");
		}
	}

	@Test
	public void testThatItIsPossibleToCreateUnscheduledStudyEventDefinitionPassingOnlyNameAndType() throws Exception {
		result = this.mockMvc
				.perform(
						post(API_EVENT_CREATE).param("name", "test_event").param("type", "unscheduled")
								.accept(mediaType).secure(true).session(session)).andExpect(status().isOk())
				.andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "unscheduled");
		}
	}

	@Test
	public void testThatItIsPossibleToCreateScheduledStudyEventDefinitionPassingOnlyNameAndType() throws Exception {
		result = this.mockMvc
				.perform(
						post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled").accept(mediaType)
								.secure(true).session(session)).andExpect(status().isOk()).andReturn();
		unmarshalResult();
		if (mediaType == MediaType.APPLICATION_XML) {
			assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getType(), "scheduled");
		}
	}

	@Test
	public void testThatItIsPossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventPassingOnlyNameTypeAndEmailUser()
			throws Exception {
		result = this.mockMvc
				.perform(
						post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
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
				.perform(
						post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
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
				.perform(
						post(API_EVENT_CREATE).param("name", "test_event").param("type", "common")
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
				.perform(
						post(API_EVENT_CREATE).param("name", "test_event").param("type", "unscheduled")
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
				.perform(
						post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
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
				.perform(
						post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
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
				.perform(
						post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
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
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getUserEmailId() == newUser.getId());
			assertTrue(restOdmContainer.getRestData().getStudyEventDefinitionBean().getId() > 0);

		}
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfSchDayHasWrongType() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled").param("schday", "a")
						.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfEmailDayHasWrongType() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled").param("emailday", "a")
						.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfMaxDayHasWrongType() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled").param("maxday", "a")
						.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfMinDayHasWrongType() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled").param("minday", "a")
						.accept(mediaType).secure(true).session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfIsReferenceHasWrongType() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("isreference", "asdfadsf").accept(mediaType).secure(true).session(session)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateStudyEventDefinitionIfRepeatingHasWrongType() throws Exception {
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "scheduled")
						.param("repeating", "asdfadsf").accept(mediaType).secure(true).session(session)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfSchDayMoreThenMaxDay()
			throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("schday", "5").param("maxday", "4").param("minday", "3").param("emailday", "2")
						.param("emailuser", newUser.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void xtestThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfMinDayMoreThenSchDay()
			throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("schday", "1").param("maxday", "4").param("minday", "3").param("emailday", "1")
						.param("emailuser", newUser.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfMinDayMoreThenMaxDay()
			throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("schday", "5").param("maxday", "5").param("minday", "7").param("emailday", "2")
						.param("emailuser", newUser.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCreateCalendaredStudyEventDefinitionThatIsNotReferenceEventIfEmailDayMoreThenSchDay()
			throws Exception {
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
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
		this.mockMvc.perform(
				post(API_EVENT_CREATE).param("name", "test_event").param("type", "calendared_visit")
						.param("description", "test description").param("category", "test category")
						.param("schday", "4").param("maxday", "4").param("minday", "3").param("emailday", "2")
						.param("emailuser", newUser.getName()).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

}
