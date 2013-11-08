package org.akaza.openclinica.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;

public class SubjectEventStatusUtilTest {

	private ResourceBundle resword;

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(new Locale("en"));
		resword = ResourceBundleProvider.getFormatBundle();
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
}
