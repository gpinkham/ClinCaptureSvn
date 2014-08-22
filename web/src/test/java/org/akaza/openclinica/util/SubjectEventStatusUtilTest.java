package org.akaza.openclinica.util;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertTrue;

@SuppressWarnings({ "rawtypes" })
public class SubjectEventStatusUtilTest {

	private ResourceBundle resword;

	private DAOWrapper daoWrapper;

	private StudyBean studyBean;

	private StudyEventBean studyEventBean;

	private List<EventCRFBean> eventCrfList;

	private List<EventDefinitionCRFBean> eventDefCrfs;

	private int idCounter;
	
	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(new Locale("en"));
		resword = ResourceBundleProvider.getFormatBundle();
		studyBean = new StudyBean();
		studyEventBean = new StudyEventBean();
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
		daoWrapper = Mockito.mock(DAOWrapper.class);
		EventCRFDAO eventCRFDAO = Mockito.mock(EventCRFDAO.class);
		EventDefinitionCRFDAO eventDefinitionCRFDAO = Mockito.mock(EventDefinitionCRFDAO.class);
		Mockito.when(daoWrapper.getEcdao()).thenReturn(eventCRFDAO);
		Mockito.when(daoWrapper.getEdcdao()).thenReturn(eventDefinitionCRFDAO);
		eventCrfList = new ArrayList<EventCRFBean>();
		eventDefCrfs = new ArrayList<EventDefinitionCRFBean>();
		Mockito.when(daoWrapper.getEcdao().findAllByStudyEvent(studyEventBean)).thenReturn((ArrayList) eventCrfList);
		Mockito.when(daoWrapper.getEdcdao().findAllByDefinition(studyBean, studyEventBean.getStudyEventDefinitionId()))
				.thenReturn((ArrayList) eventDefCrfs);
	}

	private EventDefinitionCRFBean getEventDefinitionCRFBean(Status status, boolean required, SourceDataVerification sdv) {
		EventDefinitionCRFBean edcb = new EventDefinitionCRFBean();
		edcb.setId(idCounter);
		edcb.setStatus(status);
		edcb.setRequiredCRF(required);
		edcb.setSourceDataVerification(sdv);
		edcb.setActive(true);
		return edcb;
	}

	private EventCRFBean getEventCRFBean(Status status, boolean notStarted, boolean sdvStatus) {
		EventCRFBean ecb = new EventCRFBean();
		ecb.setActive(true);
		ecb.setStatus(status);
		ecb.setSdvStatus(sdvStatus);
		ecb.setCRFVersionId(idCounter);
		ecb.setNotStarted(notStarted);
		return ecb;
	}

	private void prepareEventCRFBeanAndEventDefinitionCRFBean(EventCRFBean eventCRFBean,
			EventDefinitionCRFBean eventDefinitionCRFBean) {
		idCounter++;
		eventCrfList.add(eventCRFBean);
		eventDefCrfs.add(eventDefinitionCRFBean);
		Mockito.when(
				daoWrapper.getEdcdao().findByStudyEventIdAndCRFVersionId(studyBean, studyEventBean.getId(),
						eventCRFBean.getCRFVersionId())).thenReturn(eventDefinitionCRFBean);
	}

	private StudyEventBean getStudyEventBean(SubjectEventStatus status) {
		StudyEventBean seb = new StudyEventBean();
		seb.setSubjectEventStatus(status);
		return seb;
	}

	@Test
	public void testThatFinalStateForStudyEventOccurrencesIsScheduled() throws Exception {
		List<StudyEventBean> studyEvents = new ArrayList<StudyEventBean>();
		studyEvents.add(getStudyEventBean(SubjectEventStatus.DATA_ENTRY_STARTED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.COMPLETED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SOURCE_DATA_VERIFIED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SCHEDULED));
		StringBuilder url = new StringBuilder();
		SubjectEventStatusUtil.determineSubjectEventIconOnTheSubjectMatrix(url, new StudyBean(),
				new StudySubjectBean(), studyEvents, SubjectEventStatus.SCHEDULED, resword, true);
		assertTrue(url.toString().contains(
				SubjectEventStatusUtil.imageIconPaths.get(SubjectEventStatus.SCHEDULED.getId())));
	}

	@Test
	public void testThatFinalStateForStudyEventOccurrencesIsDES() throws Exception {
		List<StudyEventBean> studyEvents = new ArrayList<StudyEventBean>();
		studyEvents.add(getStudyEventBean(SubjectEventStatus.DATA_ENTRY_STARTED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.COMPLETED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SOURCE_DATA_VERIFIED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SIGNED));
		StringBuilder url = new StringBuilder();
		SubjectEventStatusUtil.determineSubjectEventIconOnTheSubjectMatrix(url, new StudyBean(),
				new StudySubjectBean(), studyEvents, SubjectEventStatus.SCHEDULED, resword, true);
		assertTrue(url.toString().contains(
				SubjectEventStatusUtil.imageIconPaths.get(SubjectEventStatus.DATA_ENTRY_STARTED.getId())));
	}

	@Test
	public void testThatFinalStateForStudyEventOccurrencesIsCompleted() throws Exception {
		List<StudyEventBean> studyEvents = new ArrayList<StudyEventBean>();
		studyEvents.add(getStudyEventBean(SubjectEventStatus.STOPPED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.COMPLETED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SOURCE_DATA_VERIFIED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SIGNED));
		StringBuilder url = new StringBuilder();
		SubjectEventStatusUtil.determineSubjectEventIconOnTheSubjectMatrix(url, new StudyBean(),
				new StudySubjectBean(), studyEvents, SubjectEventStatus.SCHEDULED, resword, true);
		assertTrue(url.toString().contains(
				SubjectEventStatusUtil.imageIconPaths.get(SubjectEventStatus.COMPLETED.getId())));
	}

	@Test
	public void testThatFinalStateForStudyEventOccurrencesIsSourceDataVerified() throws Exception {
		List<StudyEventBean> studyEvents = new ArrayList<StudyEventBean>();
		studyEvents.add(getStudyEventBean(SubjectEventStatus.STOPPED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.REMOVED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SOURCE_DATA_VERIFIED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SIGNED));
		StringBuilder url = new StringBuilder();
		SubjectEventStatusUtil.determineSubjectEventIconOnTheSubjectMatrix(url, new StudyBean(),
				new StudySubjectBean(), studyEvents, SubjectEventStatus.SCHEDULED, resword, true);
		assertTrue(url.toString().contains(
				SubjectEventStatusUtil.imageIconPaths.get(SubjectEventStatus.SOURCE_DATA_VERIFIED.getId())));
	}

	@Test
	public void testThatFinalStateForStudyEventOccurrencesIsSigned() throws Exception {
		List<StudyEventBean> studyEvents = new ArrayList<StudyEventBean>();
		studyEvents.add(getStudyEventBean(SubjectEventStatus.STOPPED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.REMOVED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SKIPPED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SIGNED));
		StringBuilder url = new StringBuilder();
		SubjectEventStatusUtil.determineSubjectEventIconOnTheSubjectMatrix(url, new StudyBean(),
				new StudySubjectBean(), studyEvents, SubjectEventStatus.SCHEDULED, resword, true);
		assertTrue(url.toString()
				.contains(SubjectEventStatusUtil.imageIconPaths.get(SubjectEventStatus.SIGNED.getId())));
	}

	@Test
	public void testThatFinalStateForStudyEventOccurrencesIsLocked() throws Exception {
		List<StudyEventBean> studyEvents = new ArrayList<StudyEventBean>();
		studyEvents.add(getStudyEventBean(SubjectEventStatus.LOCKED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.LOCKED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.LOCKED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.LOCKED));
		StringBuilder url = new StringBuilder();
		SubjectEventStatusUtil.determineSubjectEventIconOnTheSubjectMatrix(url, new StudyBean(),
				new StudySubjectBean(), studyEvents, SubjectEventStatus.SCHEDULED, resword, true);
		assertTrue(url.toString()
				.contains(SubjectEventStatusUtil.imageIconPaths.get(SubjectEventStatus.LOCKED.getId())));
	}

	@Test
	public void testThatFinalStateForStudyEventOccurrencesIsRemoved() throws Exception {
		List<StudyEventBean> studyEvents = new ArrayList<StudyEventBean>();
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SKIPPED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.REMOVED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.STOPPED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.LOCKED));
		StringBuilder url = new StringBuilder();
		SubjectEventStatusUtil.determineSubjectEventIconOnTheSubjectMatrix(url, new StudyBean(),
				new StudySubjectBean(), studyEvents, SubjectEventStatus.SCHEDULED, resword, true);
		assertTrue(url.toString().contains(
				SubjectEventStatusUtil.imageIconPaths.get(SubjectEventStatus.REMOVED.getId())));
	}

	@Test
	public void testThatFinalStateForStudyEventOccurrencesIsSkipped() throws Exception {
		List<StudyEventBean> studyEvents = new ArrayList<StudyEventBean>();
		studyEvents.add(getStudyEventBean(SubjectEventStatus.LOCKED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.LOCKED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SKIPPED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.LOCKED));
		StringBuilder url = new StringBuilder();
		SubjectEventStatusUtil.determineSubjectEventIconOnTheSubjectMatrix(url, new StudyBean(),
				new StudySubjectBean(), studyEvents, SubjectEventStatus.SCHEDULED, resword, true);
		assertTrue(url.toString().contains(
				SubjectEventStatusUtil.imageIconPaths.get(SubjectEventStatus.SKIPPED.getId())));
	}

	@Test
	public void testThatFinalStateForStudyEventOccurrencesIsStopped() throws Exception {
		List<StudyEventBean> studyEvents = new ArrayList<StudyEventBean>();
		studyEvents.add(getStudyEventBean(SubjectEventStatus.STOPPED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.LOCKED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.SKIPPED));
		studyEvents.add(getStudyEventBean(SubjectEventStatus.LOCKED));
		StringBuilder url = new StringBuilder();
		SubjectEventStatusUtil.determineSubjectEventIconOnTheSubjectMatrix(url, new StudyBean(),
				new StudySubjectBean(), studyEvents, SubjectEventStatus.SCHEDULED, resword, true);
		assertTrue(url.toString().contains(
				SubjectEventStatusUtil.imageIconPaths.get(SubjectEventStatus.STOPPED.getId())));
	}

	@Test
	public void testCase1ThatChecksThatStudyEventBeanStatusIsDES() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, true, SourceDataVerification.AllREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, true, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED));
	}

	@Test
	public void testCase2ThatChecksThatStudyEventBeanStatusIsSDV() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, true, SourceDataVerification.AllREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, true, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SOURCE_DATA_VERIFIED));
	}

	@Test
	public void testCase3ThatChecksThatStudyEventBeanStatusIsSDV() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.AllREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, true, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SOURCE_DATA_VERIFIED));
	}

	@Test
	public void testCase4ThatChecksThatStudyEventBeanStatusIsSDV() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SOURCE_DATA_VERIFIED));
	}

	@Test
	public void testCase5ThatChecksThatStudyEventBeanStatusIsCompleted() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, true, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED));
	}

	@Test
	public void testCase6ThatChecksThatStudyEventBeanStatusIsCompleted() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, true, SourceDataVerification.AllREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED));
	}

	@Test
	public void testCase7ThatChecksThatStudyEventBeanStatusIsCompleted() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, true, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED));
	}

	@Test
	public void testCase8ThatChecksThatStudyEventBeanStatusIsDES() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED));
	}

	@Test
	public void testCase9ThatChecksThatStudyEventBeanStatusIsSDV() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.AllREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.AllREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SOURCE_DATA_VERIFIED));
	}

	@Test
	public void testCase10ThatChecksThatStudyEventBeanStatusIsDES() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.LOCKED, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.AllREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.AllREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED));
	}

	@Test
	public void testCase11ThatChecksThatStudyEventBeanStatusIsDES() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.SIGNED, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.AllREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.AllREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED));
	}

	@Test
	public void testCase12ThatChecksThatStudyEventBeanStatusIsDES() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.SIGNED, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.SIGNED, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED));
	}

	@Test
	public void testCase13ThatChecksThatStudyEventBeanStatusIsSDV() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, true, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SOURCE_DATA_VERIFIED));
	}

	@Test
	public void testCase14ThatChecksThatStudyEventBeanStatusIsSDV() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SOURCE_DATA_VERIFIED));
	}

	@Test
	public void testCase15ThatChecksThatStudyEventBeanStatusIsDES() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, true, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED));
	}

	@Test
	public void testCase16ThatChecksThatStudyEventBeanStatusIsSDV() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, true, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, true, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, true, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SOURCE_DATA_VERIFIED));
	}

	@Test
	public void testCase17ThatChecksThatStudyEventBeanStatusIsSDV() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SOURCE_DATA_VERIFIED));
	}

	@Test
	public void testCase18ThatChecksThatStudyEventBeanStatusIsCompleted() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.PARTIALREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED));
	}

	@Test
	public void testCase19ThatChecksThatStudyEventBeanStatusIsDES() {
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.AVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, true),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.NOTREQUIRED));
		prepareEventCRFBeanAndEventDefinitionCRFBean(getEventCRFBean(Status.UNAVAILABLE, false, false),
				getEventDefinitionCRFBean(Status.AVAILABLE, false, SourceDataVerification.PARTIALREQUIRED));
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED));
	}
}
