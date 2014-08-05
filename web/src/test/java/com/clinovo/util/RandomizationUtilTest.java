package com.clinovo.util;

import java.sql.SQLException;
import java.util.HashMap;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;

import com.clinovo.exception.RandomizationException;
import com.clinovo.model.RandomizationResult;

public class RandomizationUtilTest extends DefaultAppContextTest {

	private MockHttpServletRequest request;
	private ItemDataDAO mockedItemDataDAO;

	@Before
	public void setUp() {

		StudySubjectDAO studySubjectDAO = createStudySubjectDAOMock();
		SubjectDAO subjectDAO = createSubjectDAOMock();
		StudyGroupClassDAO studyGroupDAO = createStudyGroupDAOMock(createSubjectGroup());
		mockedItemDataDAO = Mockito.mock(ItemDataDAO.class);

		RandomizationUtil.setStudyGroupDAO(studyGroupDAO);
		RandomizationUtil.setStudySubjectDAO(studySubjectDAO);
		RandomizationUtil.setItemDataDAO(itemDataDAO);
		RandomizationUtil.setSubjectDAO(subjectDAO);

		SessionManager manager = Mockito.mock(SessionManager.class);
		Mockito.when(manager.getDataSource()).thenReturn(null);

		RandomizationUtil.setSessionManager(manager);
		RandomizationUtil randomizationUtil = new RandomizationUtil();

		Whitebox.setInternalState(randomizationUtil, "itemDataDAO",
				mockedItemDataDAO);

		request = new MockHttpServletRequest();
		request.getSession().setAttribute("userBean", createUserAccountBean());
		request.setParameter("eventCrfId", "1");
		request.setParameter("dateInputId", "1");
		request.setParameter("resultInputId", "1");
		request.setParameter(
				"strataLevel",
				"[{\"StratificationID\":1,\"Level\":\"2\"},{\"StratificationID\":2,\"Level\":\"1\"}]");
		request.setParameter("strataItemIds", "[1,2]");

		Mockito.when(
				mockedItemDataDAO.findByItemIdAndEventCRFId(Mockito.anyInt(),
						Mockito.anyInt())).thenReturn(new ItemDataBean());

		Mockito.when(mockedItemDataDAO.isQuerySuccessful()).thenReturn(true);
	}

	@Test
	public void testThatIsCRFSpecifiedTrialIdValidReturnsTrueIfCRFIdIsValid() {

		assertTrue(RandomizationUtil.isCRFSpecifiedTrialIdValid("some-id"));
	}

	@Test
	public void testThatIsCRFSpecifiedTrialIdValidReturnsFalseIfCRFIdIsUndefined() {

		assertFalse(RandomizationUtil.isCRFSpecifiedTrialIdValid("undefined"));
	}

	@Test
	public void testThatIsCRFSpecifiedTrialIdValidReturnsFalseForEmptyString() {

		assertFalse(RandomizationUtil.isCRFSpecifiedTrialIdValid(""));
	}

	@Test
	public void testThatIsCRFSpecifiedTrialIdValidReturnsForNullString() {

		assertFalse(RandomizationUtil.isCRFSpecifiedTrialIdValid("null"));
	}

	@Test
	public void testThatIsConfiguredTrialIdValidReturnsTrueIfIdIsSpecified() {

		assertTrue(RandomizationUtil
				.isConfiguredTrialIdValid("some-configured-id"));
	}

	@Test
	public void testThatIsConfiguredTrialIdValidReturnsFalseIfCRFIdIsZero() {

		assertFalse(RandomizationUtil.isConfiguredTrialIdValid("0"));
	}

	@Test
	public void testThatIsConfiguredTrialIdValidReturnsFalseForEmptyString() {

		assertFalse(RandomizationUtil.isConfiguredTrialIdValid(""));
	}

	@Test
	public void testThatIsConfiguredTrialIdValidReturnsForNull() {

		assertFalse(RandomizationUtil.isConfiguredTrialIdValid(null));
	}

	@Test
	public void testThatIsTrialDoubleConfiguredReturnsTrueIfTrialIdIsSpecifiedInBothPlaces() {

		assertTrue(RandomizationUtil.isTrialIdDoubleConfigured(
				"some-configured-trial-id", "some-crf-id"));
	}

	@Test
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInPropertiesFileForUndefined() {

		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured(
				"some-configured-id", "undefined"));
	}

	@Test
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInPropertiesFileForNull() {

		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured(
				"some-configured-id", "null"));
	}

	@Test
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInPropertiesFileForEmptyString() {

		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured(
				"some-configured-id", ""));
	}

	@Test
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInCRFForUndefined() {

		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured("0",
				"some-crf-id"));
	}

	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInCRFForNull() {

		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured(null,
				"some-crf-id"));
	}

	@Test
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInCRFForEmptyString() {

		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured("",
				"some-crf-id"));
	}

	@Test
	public void testThatAssignSubjectToGroupDoesNotReturnNull()
			throws RandomizationException {

		assertNotNull(RandomizationUtil
				.assignSubjectToGroup(createRandomizationResult()));
	}

	@Test
	public void testThatAssignSubjectToGroupReturnsTheAssignedGroup()
			throws RandomizationException {

		assertEquals(
				"Should return assigned group, having the same name as the rando result",
				"test-group",
				RandomizationUtil.assignSubjectToGroup(
						createRandomizationResult()).getName());

	}

	@Test(expected = RandomizationException.class)
	public void testThatRandomizationExceptionIsThrownWhenNoMatchingGroupIsFound()
			throws RandomizationException {

		RandomizationUtil.setStudyGroupDAO(createStudyGroupDAOMock(null));
		RandomizationUtil.assignSubjectToGroup(createRandomizationResult());
	}

	@Test(expected = RandomizationException.class)
	public void testThatFailedQueryRaisesException()
			throws RandomizationException {

		StudySubjectDAO dao = createStudySubjectDAOMock();
		Mockito.when(dao.isQuerySuccessful()).thenReturn(Boolean.FALSE);
		Mockito.when(dao.getFailureDetails()).thenReturn(
				new SQLException("some-failure-message-from-the-db"));

		RandomizationUtil.setStudySubjectDAO(dao);
		RandomizationUtil.assignSubjectToGroup(createRandomizationResult());
	}

	@Test
	public void testThatGetRandomizationItemDataReturnsValidObjects() {

		HashMap<String, ItemDataBean> listOfItems = RandomizationUtil
				.getRandomizationItemData(request);
		assertEquals(2, listOfItems.size());
	}

	@Test
	public void testThatGetRandomizationItemDataReturnsValidSetOfItems() {

		HashMap<String, ItemDataBean> listOfItems = RandomizationUtil
				.getRandomizationItemData(request);

		assertNotNull(listOfItems.get("resultItem"));
		assertNotNull(listOfItems.get("dateItem"));
	}

	@Test
	public void testThatSaveStratificationVariablesToDatabaseRunsCorrectly()
			throws JSONException, RandomizationException {

		RandomizationUtil.saveStratificationVariablesToDatabase(request);
		Mockito.verify(mockedItemDataDAO).update(
				Mockito.any(ItemDataBean.class));
	}

	private RandomizationResult createRandomizationResult() {

		RandomizationResult result = new RandomizationResult();

		result.setStudyId("0");
		result.setPatientId("some-subject-oid");
		result.setRandomizationResult("test-group");

		return result;
	}

	private StudyGroupClassDAO createStudyGroupDAOMock(Object result) {

		StudyGroupClassDAO dao = Mockito.mock(StudyGroupClassDAO.class);

		Mockito.when(
				dao.findByNameAndStudyId(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn((StudyGroupClassBean) result);

		return dao;
	}

	private StudySubjectDAO createStudySubjectDAOMock() {

		StudySubjectBean subject = new StudySubjectBean();
		subject.setName("some-subject-label");

		StudySubjectDAO dao = Mockito.mock(StudySubjectDAO.class);

		Mockito.when(dao.isQuerySuccessful()).thenReturn(Boolean.TRUE);
		Mockito.when(dao.update(Mockito.any(EntityBean.class))).thenReturn(
				new StudyGroupClassBean());
		Mockito.when(
				dao.findByLabelAndStudy(Mockito.anyString(),
						Mockito.any(StudyBean.class))).thenReturn(subject);

		return dao;
	}

	private SubjectDAO createSubjectDAOMock() {

		SubjectBean subject = new SubjectBean();
		subject.setUniqueIdentifier("some-subject-uid");

		SubjectDAO dao = Mockito.mock(SubjectDAO.class);

		Mockito.when(dao.isQuerySuccessful()).thenReturn(Boolean.TRUE);
		Mockito.when(dao.update(Mockito.any(EntityBean.class))).thenReturn(
				new StudyGroupClassBean());
		Mockito.when(
				dao.findByUniqueIdentifierAndStudy(Mockito.anyString(),
						Mockito.anyInt())).thenReturn(subject);

		return dao;
	}

	private StudyGroupClassBean createSubjectGroup() {

		StudyGroupClassBean studyGroupClassBean = new StudyGroupClassBean();
		studyGroupClassBean.setId(1);
		studyGroupClassBean.setName("test-group");

		return studyGroupClassBean;
	}

	private UserAccountBean createUserAccountBean() {

		UserAccountBean ub = new UserAccountBean();

		return ub;
	}
}
