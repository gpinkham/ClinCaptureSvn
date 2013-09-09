package org.akaza.openclinica.util;

import static junit.framework.Assert.assertTrue;
import static junitx.framework.Assert.assertEquals;

import java.util.Locale;
import java.util.ResourceBundle;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;

public class ImportSummaryInfoTest {

	private ImportSummaryInfo summary;
	private ResourceBundle reswords;
	private StudyBean studyBean;
    private Locale locale;

	@Before
	public void setUp() throws Exception {
        locale = new Locale("en");

		ResourceBundleProvider.updateLocale(locale);
		reswords = ResourceBundleProvider.getWordsBundle();

		studyBean = new StudyBean();
		studyBean.setId(1);
		studyBean.setName("Default Study");
		studyBean.setOid("S_DEFAULTS1");

		summary = new ImportSummaryInfo();

		summary.processStudySubject(1, false);
		summary.processStudyEvent("1", false);
		summary.processItem("1", false);

		summary.processStudySubject(2, true);
		summary.processStudyEvent("21", false);
		summary.processItem("2", false);

		summary.processStudySubject(2, false);
		summary.processStudyEvent("22", false);
		summary.processItem("3", false);

		summary.processStudySubject(3, false);
		summary.processStudyEvent("31", true);
		summary.processItem("4", true);

		summary.processStudySubject(3, false);
		summary.processStudyEvent("32", false);
		summary.processItem("5", false);

		summary.processStudySubject(4, true);
		summary.processStudyEvent("4", false);
		summary.processItem("6", false);

		summary.processStudySubject(5, false);
		summary.processStudyEvent("5", true);
		summary.processItem("7", false);
	}

	@Test
	public void testTotalStudySubjects() {
		assertEquals(summary.totalStudySubjectIds.size(), 5);
	}

	@Test
	public void testAffectedStudySubjects() {
		assertEquals(summary.affectedStudySubjectIds.size(), 4);
	}

	@Test
	public void testSkippedStudySubjects() {
		assertEquals(summary.skippedStudySubjectIds.size(), 1);
	}

	@Test
	public void testTotalStudyEvents() {
		assertEquals(summary.totalStudyEventIds.size(), 7);
	}

	@Test
	public void testAffectedStudyEvents() {
		assertEquals(summary.affectedStudyEventIds.size(), 5);
	}

	@Test
	public void testSkippedStudyEvents() {
		assertEquals(summary.skippedStudyEventIds.size(), 2);
	}

	@Test
	public void testTotalItems() {
		assertEquals(summary.totalItemIds.size(), 7);
	}

	@Test
	public void testAffectedItems() {
		assertEquals(summary.affectedItemIds.size(), 6);
	}

	@Test
	public void testSkippedItems() {
		assertEquals(summary.skippedItemIds.size(), 1);
	}

	@Test
	public void testSummaryMessage() {
		String msg = summary.prepareSummaryMessage(studyBean, reswords);
		assertTrue(msg.length() > 0);
	}
}
