package org.akaza.openclinica.service.calendar;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Locale;

public class CalendarLogicTest {

	@Mock
	CalendarLogic calendarLogic;

	@Mock
	StudyEventDefinitionDAO studyEventDefDAO;

	@Mock
	StudyEventDAO studyEventDAO;

	@Mock
	StudySubjectDAO studySubjectDAO;

	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(this);

		Mockito.when(calendarLogic.getStudyEventDefDAO()).thenReturn(studyEventDefDAO);
		Mockito.when(calendarLogic.getStudyEventDAO()).thenReturn(studyEventDAO);
		Mockito.when(calendarLogic.getStudySubjectDAO()).thenReturn(studySubjectDAO);

		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		Whitebox.setInternalState(calendarLogic, "resexception", ResourceBundleProvider.getExceptionsBundle());
		Whitebox.setInternalState(calendarLogic, "resword", ResourceBundleProvider.getWordsBundle());
	}

	@Test
	public void testThatValidateCalendaredVisitCompletionDateWontCreateAnyDNsForReferenceVisit() {

		// SETUP
		StudyEventBean testVisit = new StudyEventBean();
		testVisit.setStudyEventDefinitionId(11);

		StudyEventDefinitionBean testVisitDefBean = new StudyEventDefinitionBean();
		testVisitDefBean.setId(11);
		testVisitDefBean.setReferenceVisit(true);

		DateTime completionDate = new DateTime(2014, 6, 11, 15, 0, DateTimeZone.UTC);

		Mockito.when(studyEventDefDAO.findByPK(11)).thenReturn(testVisitDefBean);
		Mockito.when(calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate)).thenCallRealMethod();

		// TEST
		String errorMsg = calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate);

		// VERIFY
		Assert.assertEquals("empty", errorMsg);
		Mockito.verify(calendarLogic, Mockito.never()).createDiscrepancyNote(Mockito.anyBoolean(),
				Mockito.any(StudySubjectBean.class), Mockito.any(StudyEventDefinitionBean.class),
				Mockito.any(StudyEventBean.class), Mockito.anyInt());
	}

	@Test
	public void testThatValidateCalendaredVisitCompletionDateWontCreateAnyDNsForScheduledVisit() {

		// SETUP
		StudyEventBean testVisit = new StudyEventBean();
		testVisit.setStudyEventDefinitionId(11);

		StudyEventDefinitionBean testVisitDefBean = new StudyEventDefinitionBean();
		testVisitDefBean.setId(11);
		testVisitDefBean.setReferenceVisit(false);
		testVisitDefBean.setType("scheduled");

		DateTime completionDate = new DateTime(2014, 6, 11, 15, 0, DateTimeZone.UTC);

		Mockito.when(studyEventDefDAO.findByPK(11)).thenReturn(testVisitDefBean);
		Mockito.when(calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate)).thenCallRealMethod();

		// TEST
		String errorMsg = calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate);

		// VERIFY
		Assert.assertEquals("empty", errorMsg);
		Mockito.verify(calendarLogic, Mockito.never()).createDiscrepancyNote(Mockito.anyBoolean(),
				Mockito.any(StudySubjectBean.class), Mockito.any(StudyEventDefinitionBean.class),
				Mockito.any(StudyEventBean.class), Mockito.anyInt());
	}

	@Test
	public void testThatValidateCalendaredVisitCompletionDateWontCreateAnyDNsIfCalendaredVisitIsNotYetCompleted() {

		// SETUP
		StudyEventBean testVisit = new StudyEventBean();
		testVisit.setStudyEventDefinitionId(11);
		testVisit.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		testVisit.setStatus(Status.DATA_ENTRY_STARTED);

		StudyEventDefinitionBean testVisitDefBean = new StudyEventDefinitionBean();
		testVisitDefBean.setId(11);
		testVisitDefBean.setReferenceVisit(false);
		testVisitDefBean.setType("calendared_visit");

		DateTime completionDate = new DateTime(2014, 6, 11, 15, 0, DateTimeZone.UTC);

		Mockito.when(studyEventDefDAO.findByPK(11)).thenReturn(testVisitDefBean);
		Mockito.when(calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate)).thenCallRealMethod();

		// TEST
		String errorMsg = calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate);

		// VERIFY
		Assert.assertEquals("empty", errorMsg);
		Mockito.verify(calendarLogic, Mockito.never()).createDiscrepancyNote(Mockito.anyBoolean(),
				Mockito.any(StudySubjectBean.class), Mockito.any(StudyEventDefinitionBean.class),
				Mockito.any(StudyEventBean.class), Mockito.anyInt());
	}

	@Test
	public void testThatValidateCalendaredVisitCompletionDateWontCreateAnyDNsForCompletedCalendaredVisitIfReferenceVisitWasNotCompleted() {

		// SETUP
		StudyEventBean testVisit = new StudyEventBean();
		testVisit.setStudyEventDefinitionId(11);
		testVisit.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
		testVisit.setReferenceVisitId(0);

		StudyEventDefinitionBean testVisitDefBean = new StudyEventDefinitionBean();
		testVisitDefBean.setId(11);
		testVisitDefBean.setReferenceVisit(false);
		testVisitDefBean.setType("calendared_visit");

		DateTime completionDate = new DateTime(2014, 6, 11, 15, 0, DateTimeZone.UTC);

		Mockito.when(studyEventDefDAO.findByPK(11)).thenReturn(testVisitDefBean);
		Mockito.when(studyEventDAO.findByPK(0)).thenReturn(new StudyEventBean());
		Mockito.when(calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate)).thenCallRealMethod();

		// TEST
		String errorMsg = calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate);

		// VERIFY
		Assert.assertEquals("empty", errorMsg);
		Mockito.verify(calendarLogic, Mockito.never()).createDiscrepancyNote(Mockito.anyBoolean(),
				Mockito.any(StudySubjectBean.class), Mockito.any(StudyEventDefinitionBean.class),
				Mockito.any(StudyEventBean.class), Mockito.anyInt());
	}

	@Test
	public void testThatValidateCalendaredVisitCompletionDateWillCreateDNIfCalendaredVisitWasCompletedEarlierThenExpected()
			throws Exception {

		// SETUP
		StudyEventBean testVisit = new StudyEventBean();
		testVisit.setStudyEventDefinitionId(11);
		testVisit.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
		testVisit.setReferenceVisitId(55);
		testVisit.setStudySubjectId(190);

		StudyEventDefinitionBean testVisitDefBean = new StudyEventDefinitionBean();
		testVisitDefBean.setId(11);
		testVisitDefBean.setReferenceVisit(false);
		testVisitDefBean.setType("calendared_visit");
		testVisitDefBean.setScheduleDay(5);
		testVisitDefBean.setMinDay(4);
		testVisitDefBean.setMaxDay(6);

		StudyEventBean referenceVisit = new StudyEventBean();
		referenceVisit.setId(55);
		referenceVisit.setDateEnded(new DateTime(2014, 6, 11, 15, 0, DateTimeZone.UTC).toDate());

		StudySubjectBean studySubjectBean = new StudySubjectBean();
		studySubjectBean.setId(190);

		DiscrepancyNoteBean parentDN = new DiscrepancyNoteBean();
		parentDN.setId(347);

		DateTime completionDate = new DateTime(2014, 6, 14, 17, 35, DateTimeZone.UTC);

		Mockito.when(studyEventDefDAO.findByPK(11)).thenReturn(testVisitDefBean);
		Mockito.when(studyEventDAO.findByPK(55)).thenReturn(referenceVisit);
		Mockito.when(studySubjectDAO.findByPK(190)).thenReturn(studySubjectBean);
		Mockito.when(calendarLogic.createDiscrepancyNote(true, studySubjectBean, testVisitDefBean, testVisit, null))
				.thenReturn(parentDN);
		Mockito.when(calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate)).thenCallRealMethod();

		// TEST
		String errorMsg = calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate);

		// VERIFY
		Assert.assertEquals("Event was completed earlier than expected.", errorMsg);
		InOrder inOrder  = Mockito.inOrder(calendarLogic);
		inOrder.verify(calendarLogic, Mockito.times(1)).createDiscrepancyNote(true, studySubjectBean, testVisitDefBean,
				testVisit, null);
		inOrder.verify(calendarLogic, Mockito.times(1)).createDiscrepancyNote(true, studySubjectBean, testVisitDefBean,
				testVisit, 347);
	}

	@Test
	public void testThatValidateCalendaredVisitCompletionDateWillCreateDNIfCalendaredVisitWasCompletedLaterThenExpected()
			throws Exception {

		// SETUP
		StudyEventBean testVisit = new StudyEventBean();
		testVisit.setStudyEventDefinitionId(11);
		testVisit.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
		testVisit.setReferenceVisitId(55);
		testVisit.setStudySubjectId(190);

		StudyEventDefinitionBean testVisitDefBean = new StudyEventDefinitionBean();
		testVisitDefBean.setId(11);
		testVisitDefBean.setReferenceVisit(false);
		testVisitDefBean.setType("calendared_visit");
		testVisitDefBean.setScheduleDay(5);
		testVisitDefBean.setMinDay(4);
		testVisitDefBean.setMaxDay(6);

		StudyEventBean referenceVisit = new StudyEventBean();
		referenceVisit.setId(55);
		referenceVisit.setDateEnded(new DateTime(2014, 6, 11, 15, 0, DateTimeZone.UTC).toDate());

		StudySubjectBean studySubjectBean = new StudySubjectBean();
		studySubjectBean.setId(190);

		DiscrepancyNoteBean parentDN = new DiscrepancyNoteBean();
		parentDN.setId(347);

		DateTime completionDate = new DateTime(2014, 6, 18, 0, 1, DateTimeZone.UTC);

		Mockito.when(studyEventDefDAO.findByPK(11)).thenReturn(testVisitDefBean);
		Mockito.when(studyEventDAO.findByPK(55)).thenReturn(referenceVisit);
		Mockito.when(studySubjectDAO.findByPK(190)).thenReturn(studySubjectBean);
		Mockito.when(calendarLogic.createDiscrepancyNote(false, studySubjectBean, testVisitDefBean, testVisit, null))
				.thenReturn(parentDN);
		Mockito.when(calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate)).thenCallRealMethod();

		// TEST
		String errorMsg = calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate);

		// VERIFY
		Assert.assertEquals("Event was completed later than expected.", errorMsg);
		InOrder inOrder  = Mockito.inOrder(calendarLogic);
		inOrder.verify(calendarLogic, Mockito.times(1)).createDiscrepancyNote(false, studySubjectBean, testVisitDefBean,
				testVisit, null);
		inOrder.verify(calendarLogic, Mockito.times(1)).createDiscrepancyNote(false, studySubjectBean, testVisitDefBean,
				testVisit, 347);
	}

	@Test
	public void testThatValidateCalendaredVisitCompletionDateWontCreateDNIfCalendaredVisitWasCompletedInTime()
			throws Exception {

		// SETUP
		StudyEventBean testVisit = new StudyEventBean();
		testVisit.setStudyEventDefinitionId(11);
		testVisit.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
		testVisit.setReferenceVisitId(55);
		testVisit.setStudySubjectId(190);

		StudyEventDefinitionBean testVisitDefBean = new StudyEventDefinitionBean();
		testVisitDefBean.setId(11);
		testVisitDefBean.setReferenceVisit(false);
		testVisitDefBean.setType("calendared_visit");
		testVisitDefBean.setScheduleDay(5);
		testVisitDefBean.setMinDay(4);
		testVisitDefBean.setMaxDay(6);

		StudyEventBean referenceVisit = new StudyEventBean();
		referenceVisit.setId(55);
		referenceVisit.setDateEnded(new DateTime(2014, 6, 11, 15, 0, DateTimeZone.UTC).toDate());

		StudySubjectBean studySubjectBean = new StudySubjectBean();
		studySubjectBean.setId(190);

		DateTime completionDate = new DateTime(2014, 6, 17, 10, 0, DateTimeZone.UTC);

		Mockito.when(studyEventDefDAO.findByPK(11)).thenReturn(testVisitDefBean);
		Mockito.when(studyEventDAO.findByPK(55)).thenReturn(referenceVisit);
		Mockito.when(studySubjectDAO.findByPK(190)).thenReturn(studySubjectBean);
		Mockito.when(calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate)).thenCallRealMethod();

		// TEST
		String errorMsg = calendarLogic.validateCalendaredVisitCompletionDate(testVisit, completionDate);

		// VERIFY
		Assert.assertEquals("empty", errorMsg);
		Mockito.verify(calendarLogic, Mockito.never()).createDiscrepancyNote(Mockito.anyBoolean(),
				Mockito.any(StudySubjectBean.class), Mockito.any(StudyEventDefinitionBean.class),
				Mockito.any(StudyEventBean.class), Mockito.anyInt());
	}
}
