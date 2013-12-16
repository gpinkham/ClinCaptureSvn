package com.clinovo.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.clinovo.exception.RandomizationException;
import com.clinovo.model.RandomizationResult;

public class RandomizationUtilTest {
	
	@Before
	public void setUp() {
		
		StudySubjectDAO studySubjectDAO = createStudySubjectDAOMock();
		StudyGroupClassDAO studyGroupDAO = createStudyGroupDAOMock(createSubjectGroup());
		
		RandomizationUtil.setStudyGroupDAO(studyGroupDAO);
		RandomizationUtil.setStudySubjectDAO(studySubjectDAO);
		
		SessionManager manager = Mockito.mock(SessionManager.class);
		Mockito.when(manager.getDataSource()).thenReturn(null);
		RandomizationUtil.setSessionManager(manager);
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

		assertTrue(RandomizationUtil.isConfiguredTrialIdValid("some-configured-id"));
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

		assertTrue(RandomizationUtil.isTrialIdDoubleConfigured("some-configured-trial-id", "some-crf-id"));
	}

	@Test
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInPropertiesFileForUndefined() {

		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured("some-configured-id", "undefined"));
	}

	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInPropertiesFileForNull() {

		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured("some-configured-id", "null"));
	}

	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInPropertiesFileForEmptyString() {

		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured("some-configured-id", ""));
	}

	@Test
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInCRFForUndefined() {

		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured("0", "some-crf-id"));
	}

	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInCRFForNull() {

		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured(null, "some-crf-id"));
	}

	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInCRFForEmptyString() {

		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured("", "some-crf-id"));
	}

	@Test
	public void testThatAssignSubjectToGroupDoesNotReturnNull() throws RandomizationException {
		
		assertNotNull(RandomizationUtil.assignSubjectToGroup(createRandomizationResult()));
	}

	@Test
	public void testThatAssignSubjectToGroupReturnsTheAssignedGroup() throws RandomizationException {
		
		assertEquals("Should return assigned group, having the same name as the rando result", "test-group",
				RandomizationUtil.assignSubjectToGroup(createRandomizationResult()).getName());

	}
	
	@Test(expected=RandomizationException.class)
	public void testThatRandomizationExceptionIsThrownWhenNoMatchingGroupIsFound() throws RandomizationException {
		
		RandomizationUtil.setStudyGroupDAO(createStudyGroupDAOMock(null));
		RandomizationUtil.assignSubjectToGroup(createRandomizationResult());
	}
	
	@Test(expected=RandomizationException.class)
	public void testThatFailedQueryRaisesException() throws RandomizationException {
		
		StudySubjectDAO dao = createStudySubjectDAOMock();
		Mockito.when(dao.isQuerySuccessful()).thenReturn(Boolean.FALSE);
		Mockito.when(dao.getFailureDetails()).thenReturn(new SQLException("some-failure-message-from-the-db"));
		
		RandomizationUtil.setStudySubjectDAO(dao);
		RandomizationUtil.assignSubjectToGroup(createRandomizationResult());
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

		Mockito.when(dao.findByNameAndStudyId(Mockito.anyString(), Mockito.anyInt())).thenReturn((StudyGroupClassBean) result);

		return dao;
	}
	
	private StudySubjectDAO createStudySubjectDAOMock() {

		StudySubjectBean subject = new StudySubjectBean();
		subject.setName("some-subject-label");
		
		StudySubjectDAO dao = Mockito.mock(StudySubjectDAO.class);

		Mockito.when(dao.isQuerySuccessful()).thenReturn(Boolean.TRUE);
		Mockito.when(dao.update(Mockito.any(EntityBean.class))).thenReturn(new StudyGroupClassBean());
		Mockito.when(dao.findByLabelAndStudy(Mockito.anyString(), Mockito.any(StudyBean.class))).thenReturn(subject);

		return dao;
	}

	private StudyGroupClassBean createSubjectGroup() {

		StudyGroupClassBean studyGroupClassBean = new StudyGroupClassBean();
		studyGroupClassBean.setId(1);
		studyGroupClassBean.setName("test-group");

		return studyGroupClassBean;
	}
}
