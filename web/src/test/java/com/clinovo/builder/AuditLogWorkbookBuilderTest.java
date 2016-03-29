package com.clinovo.builder;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.AuditLogRandomization;
import com.clinovo.util.RequestUtil;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.akaza.openclinica.bean.admin.AuditBean;
import org.akaza.openclinica.bean.admin.DeletedEventCRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Tests for AuditLogWorkbookBuilder.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LocaleResolver.class, RequestUtil.class})
@SuppressWarnings("deprecation")
public class AuditLogWorkbookBuilderTest {

	private AuditLogWorkbookBuilder builder;
	private StudySubjectBean studySubject;
	private ArrayList<Object> studySubjectAudits;
	private List<StudyEventBean> events;
	private ArrayList<Object> allDeletedEventCRFs;
	private ArrayList<Object> studyEventAudits;
	private ArrayList<Object> eventCRFAudits;
	private List<AuditLogRandomization> randomizationAudits;

	@Before
	public void prepareForTests() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		StudyBean study = new StudyBean();
		Locale locale = Locale.ENGLISH;
		ResourceBundleProvider.updateLocale(locale);
		PowerMockito.mockStatic(LocaleResolver.class);
		PowerMockito.when(LocaleResolver.getLocale()).thenReturn(locale);
		UserAccountBean user = new UserAccountBean();
		user.setName("test");
		PowerMockito.mockStatic(RequestUtil.class);
		PowerMockito.when(RequestUtil.getUserAccountBean()).thenReturn(user);
		builder = new AuditLogWorkbookBuilder(response, study);
		studySubject = new StudySubjectBean();
		studySubject.setOwner(user);
		studySubject.setStatus(Status.AVAILABLE);
		studySubjectAudits = new ArrayList<Object>();
		events = new ArrayList<StudyEventBean>();
		allDeletedEventCRFs = new ArrayList<Object>();
		studyEventAudits = new ArrayList<Object>();
		eventCRFAudits = new ArrayList<Object>();
		randomizationAudits = new ArrayList<AuditLogRandomization>();
	}

	@Test
	public void testThatBuilderIsCreatedSuccessfully() {
		assertEquals(true, builder != null);
	}

	@Test
	public void testThatWorkbookCanBeCreatedForSubjectWithoutCompletedEventCRFs() {
		boolean exceptionThrown = false;
		try {
			buildWorkbook();
		} catch (WriteException ex) {
			exceptionThrown = true;
		}
		assertFalse(exceptionThrown);
	}

	@Test
	public void testThatSubjectAuditWasAddedWithoutErrors() throws WriteException {
		WritableWorkbook workbookWithoutAudit = buildWorkbook();
		WritableSheet sheetWithoutAudit = workbookWithoutAudit.getSheet(getSubjectInfoSheetName());
		addSubjectAudit();
		WritableWorkbook workbookWithAudit = buildWorkbook();
		WritableSheet sheetWithAudit = workbookWithAudit.getSheet(getSubjectInfoSheetName());
		assertTrue(sheetWithoutAudit.getRows() < sheetWithAudit.getRows());
		resetSubjectAudit();
	}

	@Test
	public void testThatSubjectInfoSheetIsPresentInWorkbook() throws WriteException {
		WritableWorkbook workbook = buildWorkbook();
		WritableSheet sheet = workbook.getSheet(getSubjectInfoSheetName());
		assertNotNull(sheet);
	}

	@Test
	public void testThatSheetWillBeGeneratedForEachEvent() throws WriteException {
		addTestEvent();
		WritableWorkbook workbook = buildWorkbook();
		WritableSheet sheet = workbook.getSheet(AuditLogWorkbookBuilder.getEventSheetName(events.get(0)));
		assertNotNull(sheet);
		resetEvents();
	}

	@Test
	public void testThatRandomizationAuditLogIsWrittenToWorkbookWithoutErrors() throws WriteException {
		WritableWorkbook workbookWithoutRandomization = buildWorkbook();
		WritableSheet subjectInfoWithoutRandomization = workbookWithoutRandomization.getSheet(getSubjectInfoSheetName());
		AuditLogRandomization logRandomization = new AuditLogRandomization();
		logRandomization.setSuccess(1);
		logRandomization.setUserName("Test");
		randomizationAudits.add(logRandomization);
		WritableWorkbook workbookWithRandomization = buildWorkbook();
		WritableSheet subjectInfoWithRandomization = workbookWithRandomization.getSheet(getSubjectInfoSheetName());
		assertTrue(subjectInfoWithoutRandomization.getRows() < subjectInfoWithRandomization.getRows());
		resetRandomizationAudits();
	}

	@Test
	public void testThatEventInfoTableWasAddedWithoutErrors() throws WriteException {
		WritableWorkbook workbookWithoutEvents = buildWorkbook();
		WritableSheet sheetWithoutEvents = workbookWithoutEvents.getSheet(getSubjectInfoSheetName());
		addTestEvent();
		WritableWorkbook workbookWithEvents = buildWorkbook();
		WritableSheet sheetWithEvents = workbookWithEvents.getSheet(getSubjectInfoSheetName());
		assertTrue(sheetWithoutEvents.getRows() < sheetWithEvents.getRows());
		resetEvents();
	}

	@Test
	public void testThatDeletedEventCRFsInfoTableWasAddedWithoutErrors() throws WriteException {
		addTestEvent();
		WritableWorkbook workbookWithoutDeletedCRFs = buildWorkbook();
		WritableSheet sheetWithoutDeletedCRF = workbookWithoutDeletedCRFs.getSheet(getSubjectInfoSheetName());
		addDeletedEventCRF();
		WritableWorkbook workbookWithDeletedEventCRF = buildWorkbook();
		WritableSheet sheetWithDeletedEventCRF = workbookWithDeletedEventCRF.getSheet(getSubjectInfoSheetName());
		assertTrue(sheetWithoutDeletedCRF.getRows() < sheetWithDeletedEventCRF.getRows());
		resetDeletedEventCRFs();
		resetEvents();
	}

	@Test
	public void testThatEventsAuditTableWasAddedWithoutErrors() throws WriteException {
		addTestEvent();
		WritableWorkbook workbookWithoutEventsAudit = buildWorkbook();
		WritableSheet sheetWithoutEventsAudit = workbookWithoutEventsAudit.getSheet(getSubjectInfoSheetName());
		addStudyEventAudit();
		WritableWorkbook workbookWithEventsAudit = buildWorkbook();
		WritableSheet sheetWithEventsAudit = workbookWithEventsAudit.getSheet(getSubjectInfoSheetName());
		assertTrue(sheetWithoutEventsAudit.getRows() < sheetWithEventsAudit.getRows());
		resetStudyEventsAudit();
		resetEvents();
	}

	@Test
	public void testThatEventCRFInfoTableWasAddedWithoutErrors() throws WriteException {
		addTestEvent();
		WritableWorkbook workbookWithoutCRFs = buildWorkbook();
		WritableSheet sheetWithoutCRFs = workbookWithoutCRFs.getSheet(getSubjectInfoSheetName());
		addTestCRFIntoEvent();
		WritableWorkbook workbookWithCRFs = buildWorkbook();
		WritableSheet sheetWithCRFs = workbookWithCRFs.getSheet(getSubjectInfoSheetName());
		assertTrue(sheetWithoutCRFs.getRows() < sheetWithCRFs.getRows());
		resetEvents();
	}

	@Test
	public void testThatEventCRFAuditsTableWasAddedWithoutErrors() throws WriteException {
		addTestEvent();
		addTestCRFIntoEvent();
		WritableWorkbook workbookWithoutCRFsAudit = buildWorkbook();
		WritableSheet sheetWithoutCRFsAudit = workbookWithoutCRFsAudit.getSheet(getSubjectInfoSheetName());
		addTestCRFsAudit();
		WritableWorkbook workbookWithCRFsAudit = buildWorkbook();
		WritableSheet sheetWithCRFsAudit = workbookWithCRFsAudit.getSheet(getSubjectInfoSheetName());
		assertTrue(sheetWithoutCRFsAudit.getRows() < sheetWithCRFsAudit.getRows());
		resetEvents();
		resetTestCRFsAudit();
	}

	private void addSubjectAudit() {
		AuditBean subjectAudit = new AuditBean();
		subjectAudit.setAuditEventTypeName("test");
		subjectAudit.setAuditDate(new Date());
		studySubjectAudits.add(subjectAudit);
	}

	private void resetSubjectAudit() {
		studyEventAudits = new ArrayList<Object>();
	}

	private void addTestCRFsAudit() {
		AuditBean eventCRFAudit = new AuditBean();
		eventCRFAudit.setEventCRFId(1);
		eventCRFAudit.setAuditDate(new Date());
		eventCRFAudit.setAuditEventTypeId(12);
		eventCRFAudit.setOldValue("0");
		eventCRFAudit.setNewValue("1");
		eventCRFAudit.setAuditEventTypeName("test");
		eventCRFAudits.add(eventCRFAudit);
	}

	private void resetTestCRFsAudit() {
		eventCRFAudits = new ArrayList<Object>();
	}

	private void addTestCRFIntoEvent() {
		StudyEventBean event = events.get(0);
		UserAccountBean owner = new UserAccountBean();
		owner.setName("test");
		EventCRFBean eventCRF = new EventCRFBean();
		eventCRF.setDateInterviewed(new Date());
		eventCRF.setOwner(owner);
		eventCRF.setId(1);
		ArrayList<Object> eventCRFs = new ArrayList<Object>();
		eventCRFs.add(eventCRF);
		event.setEventCRFs(eventCRFs);
	}

	private void resetStudyEventsAudit() {
		studyEventAudits = new ArrayList<Object>();
	}

	private void addStudyEventAudit() {
		AuditBean studyEventAudit = new AuditBean();
		studyEventAudits.add(studyEventAudit);
	}

	private WritableWorkbook buildWorkbook() throws WriteException {
		return builder.buildWorkbook(studySubject, studySubjectAudits, events, allDeletedEventCRFs, studyEventAudits, eventCRFAudits, randomizationAudits);
	}

	private void addTestEvent() {
		StudyEventBean event = new StudyEventBean();
		StudyEventDefinitionBean studyEventDefinitionBean = new StudyEventDefinitionBean();
		studyEventDefinitionBean.setName("Test");
		event.setStudyEventDefinition(studyEventDefinitionBean);
		event.setId(1);
		events.add(event);
	}

	private void addDeletedEventCRF() {
		DeletedEventCRFBean crf = new DeletedEventCRFBean();
		crf.setStudyEventId(1);
		crf.setDeletedDate(new Date());
		allDeletedEventCRFs.add(crf);
	}

	private void resetDeletedEventCRFs() {
		allDeletedEventCRFs = new ArrayList<Object>();
	}

	private void resetEvents() {
		events = new ArrayList<StudyEventBean>();
	}

	private void resetRandomizationAudits() {
		randomizationAudits = new ArrayList<AuditLogRandomization>();
	}

	private String getSubjectInfoSheetName() {
		return AuditLogWorkbookBuilder.SUBJECT_INFO_SHEET;
	}
}
