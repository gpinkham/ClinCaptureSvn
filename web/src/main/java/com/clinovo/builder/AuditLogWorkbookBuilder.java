package com.clinovo.builder;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.AuditLogRandomization;
import com.clinovo.util.DateUtil;
import com.clinovo.util.RequestUtil;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.akaza.openclinica.bean.admin.AuditBean;
import org.akaza.openclinica.bean.admin.DeletedEventCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class was created in order to move population of AuditLogWorkbook from servlet main body.
 */
@SuppressWarnings("rawtypes")
public class AuditLogWorkbookBuilder {

	private int row;
	private int sheet;
	private String[] currentRow;
	private WritableWorkbook workbook;
	private WritableSheet currentSheet;
	private WritableCellFormat headersFormat;

	public static final int TWO = 2;
	public static final int SIX = 6;
	public static final int EIGHT = 8;
	public static final int AUDIT_EVENT_TYPE_32 = 32;
	public static final int AUDIT_EVENT_TYPE_12 = 12;

	public static final String[] STUDY_SUBJECT_INFO_ROW = new String[]{"study_subject_ID", "secondary_subject_ID",
			"date_of_birth", "person_ID", "created_by", "status"};
	public static final String[] AUDIT_EVENTS_ROW = new String[]{"audit_event", "local_date_time", "user",
			"value_type", "old", "new"};
	public static final String[] STUDY_EVENT_ROW = new String[]{"study_events", "location", "date", "occurrence_number"};
	public static final String[] DELETED_EVENT_CRF_ROW = new String[]{"name", "version", "deleted_by", "delete_date"};
	public static final String[] EVENT_CRF_ROW = new String[]{"name", "version", "date_interviewed",
			"interviewer_name", "owner"};
	public static final String[] RANDOMIZATION_AUDIT_ROW = new String[]{"audit_event", "local_date_time", "user", "event_description"};

	/**
	 * Default constructor.
	 *
	 * @param response HttpServletResponse
	 * @throws java.io.IOException in case of workbook creation failure.
	 */
	public AuditLogWorkbookBuilder(HttpServletResponse response) throws IOException {
		WritableFont font = new WritableFont(WritableFont.ARIAL, EIGHT, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE, Colour.VIOLET2);
		WritableCellFormat cellFormat = new WritableCellFormat();
		cellFormat.setFont(font);
		headersFormat = cellFormat;

		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setLocale(LocaleResolver.getLocale());
		workbook = Workbook.createWorkbook(response.getOutputStream(), wbSettings);
	}

	/**
	 * Populate Study Subject's workbook with data.
	 *
	 * @param studySubject        StudySubjectBean
	 * @param studySubjectAudits  ArrayList
	 * @param events              List<StudyEventBean>
	 * @param allDeletedEventCRFs ArrayList
	 * @param studyEventAudits    ArrayList
	 * @param eventCRFAudits      ArrayList
	 * @param randomizationAudits List<AuditLogRandomization>
	 * @return WritableWorkbook
	 * @throws WriteException in case if there is some issue with data.
	 */
	public WritableWorkbook buildWorkbook(StudySubjectBean studySubject, ArrayList studySubjectAudits,
										  List<StudyEventBean> events, ArrayList allDeletedEventCRFs, ArrayList studyEventAudits,
										  ArrayList eventCRFAudits, List<AuditLogRandomization> randomizationAudits) throws WriteException {
		workbook.createSheet("Subject Information", 0);
		currentSheet = workbook.getSheet(0);

		createSubjectInfoTable(studySubject, studySubjectAudits);
		createRandomizationAuditsTable(randomizationAudits);
		createStudyEventsInfoTable(events);

		for (StudyEventBean event : events) {
			sheet++;
			resetRowNumber();
			workbook.createSheet(getEventSheetName(event), sheet);
			currentSheet = workbook.getSheet(sheet);

			createEventInfoTable(event);
			createDeletedEventCRFsTable(event, allDeletedEventCRFs);
			createStudyEventAuditsTable(event, studyEventAudits);

			for (int j = 0; j < event.getEventCRFs().size(); j++) {
				EventCRFBean eventCrf = (EventCRFBean) event.getEventCRFs().get(j);
				createEventCRFInfoTable(eventCrf);
				createEventCRFAuditsTable(eventCrf, eventCRFAudits);
			}
			autoSizeColumns(currentSheet);
		}
		return workbook;
	}

	private void createSubjectInfoTable(StudySubjectBean studySubject, List studySubjectAudits) throws WriteException {
		addRowToSheet(STUDY_SUBJECT_INFO_ROW, headersFormat);
		addRowToSheet(getStudySubjectRow(studySubject), null);
		row++;
		addRowToSheet(AUDIT_EVENTS_ROW, headersFormat);

		for (Object studySubjectAudit : studySubjectAudits) {
			AuditBean audit = (AuditBean) studySubjectAudit;
			currentRow = new String[]{
					ResourceBundleProvider.getAuditEventsBundle().getString(audit.getAuditEventTypeName()),
					dateTimeFormat(audit.getAuditDate()), audit.getUserName(), audit.getEntityName(),
					audit.getOldValue(), audit.getNewValue()};
			addRowToSheet(currentRow, null);
		}
		addBlankRow();
	}

	private void createRandomizationAuditsTable(List<AuditLogRandomization> randomizationAudits) throws WriteException {
		addRowToSheet(RANDOMIZATION_AUDIT_ROW, headersFormat);
		for (AuditLogRandomization audit : randomizationAudits) {
			String randomizationResult = audit.getSuccess() == 1 ? "randomization.call.result.success" : "randomization.call.result.error";
			String eventDescription = ResourceBundleProvider.getResWord("systemProperty.randomizationAuthenticationUrl.label") + ": "
					+ audit.getAuthenticationUrl() + "\n"
					+ ResourceBundleProvider.getResWord("systemProperty.randomizationUrl.label") + ": "
					+ audit.getRandomizationUrl() + "\n"
					+ ResourceBundleProvider.getResWord("systemProperty.randomizationTrialId.label") + ": "
					+ audit.getTrialId() + "\n"
					+ ResourceBundleProvider.getResWord("site_id") + ": "
					+ audit.getSiteName() + "\n"
					+ ResourceBundleProvider.getResWord("stratificationVariables") + ": "
					+ audit.getStrataVariables() + "\n"
					+ ResourceBundleProvider.getResWord("randomization_result") + ": "
					+ audit.getResponse();
			currentRow = new String[]{
					ResourceBundleProvider.getResWord(randomizationResult),
					dateTimeFormat(audit.getAuditDate()), audit.getUserName(),
					eventDescription
			};
			addRowToSheet(currentRow, null);
		}
		addBlankRow();
	}

	private void createStudyEventsInfoTable(List<StudyEventBean> events) throws WriteException {
		addRowToSheet(STUDY_EVENT_ROW, headersFormat);

		for (StudyEventBean event : events) {
			if (event.getStartTimeFlag()) {
				currentRow = new String[]{event.getStudyEventDefinition().getName(), event.getLocation(),
						dateTimeFormat(event.getDateStarted()), Integer.toString(event.getSampleOrdinal())};
			} else {
				currentRow = new String[]{event.getStudyEventDefinition().getName(), event.getLocation(),
						dateFormat(event.getDateStarted()), Integer.toString(event.getSampleOrdinal())};
			}
			addRowToSheet(currentRow, null);
		}
		autoSizeColumns(currentSheet);
	}

	private void createEventInfoTable(StudyEventBean event) throws WriteException {
		currentSheet.addCell(new Label(0, row, ResourceBundleProvider.getResWord("name"), headersFormat));
		currentSheet.addCell(new Label(1, row, event.getStudyEventDefinition().getName(), headersFormat));
		row++;
		currentSheet.addCell(new Label(0, row, "Location"));
		currentSheet.addCell(new Label(1, row, event.getLocation()));
		row++;
		currentSheet.addCell(new Label(0, row, "Start Date"));
		Label label;
		if (event.getStartTimeFlag()) {
			label = new Label(1, row, dateTimeFormat(event.getDateStarted()));
		} else {
			label = new Label(1, row, dateFormat(event.getDateStarted()));
		}
		currentSheet.addCell(label);
		row++;
		currentSheet.addCell(new Label(0, row, "Status"));
		currentSheet.addCell(new Label(1, row, event.getSubjectEventStatus().getName()));
		row++;
		currentSheet.addCell(new Label(0, row, ResourceBundleProvider.getResWord("occurrence_number")));
		currentSheet.addCell(new Label(1, row, Integer.toString(event.getSampleOrdinal())));
		addBlankRow();
	}

	private void createDeletedEventCRFsTable(StudyEventBean event, ArrayList allDeletedEventCRFs) throws WriteException {
		addRowToSheet(DELETED_EVENT_CRF_ROW, headersFormat);

		for (Object allDeletedEventCRF : allDeletedEventCRFs) {
			DeletedEventCRFBean deletedEventCRF = (DeletedEventCRFBean) allDeletedEventCRF;
			if (deletedEventCRF.getStudyEventId() == event.getId()) {
				currentRow = new String[]{deletedEventCRF.getCrfName(), deletedEventCRF.getCrfVersion(),
						deletedEventCRF.getDeletedBy(), dateFormat(deletedEventCRF.getDeletedDate())};
				addRowToSheet(currentRow, null);
			}
		}
		addBlankRow();
	}

	private void createStudyEventAuditsTable(StudyEventBean event, ArrayList studyEventAudits) throws WriteException {
		addRowToSheet(AUDIT_EVENTS_ROW, headersFormat);

		for (Object studyEventAudit : studyEventAudits) {
			AuditBean studyEvent = (AuditBean) studyEventAudit;
			if (studyEvent.getEntityId() == event.getId()) {
				String oldValue = getStudyEventValueByCode(studyEvent.getOldValue());
				String newValue = getStudyEventValueByCode(studyEvent.getNewValue());

				currentRow = new String[]{
						ResourceBundleProvider.getAuditEventsBundle().getString(
								studyEvent.getAuditEventTypeName()),
						dateTimeFormat(studyEvent.getAuditDate()),
						studyEvent.getUserName(),
						studyEvent.getEntityName()
								+ (event.getStudyEventDefinition().isRepeating() ? "("
								+ studyEvent.getOrdinal() + ")" : ""), oldValue, newValue};
				addRowToSheet(currentRow, null);
			}
		}
		addBlankRow();
	}

	private void createEventCRFInfoTable(EventCRFBean eventCrf) throws WriteException {
		addRowToSheet(EVENT_CRF_ROW, headersFormat);
		currentRow = new String[]{eventCrf.getCrf().getName(), eventCrf.getCrfVersion().getName(),
				dateTimeFormat(eventCrf.getDateInterviewed()), eventCrf.getInterviewerName(),
				eventCrf.getOwner().getName()};
		addRowToSheet(currentRow, null);
		addBlankRow();
	}

	private void createEventCRFAuditsTable(EventCRFBean eventCrf, ArrayList eventCRFAudits) throws WriteException {
		addRowToSheet(AUDIT_EVENTS_ROW, headersFormat);

		for (Object eventCRFAudit : eventCRFAudits) {
			AuditBean eventCrfAudit = (AuditBean) eventCRFAudit;
			if (eventCrfAudit.getEventCRFId() == eventCrf.getId()) {
				String oldValue = getEventCRFValueByCode(eventCrfAudit.getOldValue(),
						eventCrfAudit.getAuditEventTypeId(), eventCrfAudit.getEntityName());
				String newValue = getEventCRFValueByCode(eventCrfAudit.getNewValue(),
						eventCrfAudit.getAuditEventTypeId(), eventCrfAudit.getEntityName());
				currentRow = new String[]{
						ResourceBundleProvider.getAuditEventsBundle().getString(
								eventCrfAudit.getAuditEventTypeName()),
						dateTimeFormat(eventCrfAudit.getAuditDate()), eventCrfAudit.getUserName(),
						eventCrfAudit.getEntityName() + "(" + eventCrfAudit.getOrdinal() + ")", oldValue,
						newValue};
				addRowToSheet(currentRow, null);
			}
		}
		row++;
	}

	private void addRowToSheet(String[] excelRow, WritableCellFormat cellFormat) throws WriteException {
		for (int i = 0; i < excelRow.length; i++) {
			if (cellFormat != null) {
				Label label = new Label(i, row, ResourceBundleProvider.getResWord(excelRow[i]), cellFormat);
				currentSheet.addCell(label);
			} else {
				Label label = new Label(i, row, excelRow[i]);
				currentSheet.addCell(label);
			}
		}
		row++;
	}

	private String[] getStudySubjectRow(StudySubjectBean studySubject) {
		return new String[]{studySubject.getLabel(),
				studySubject.getSecondaryLabel(),
				studySubject.getDateOfBirth() != null
						? DateUtil.printDate(studySubject.getDateOfBirth(), DateUtil.DatePattern.DATE, LocaleResolver.getLocale()) : null,
				studySubject.getUniqueIdentifier(),
				studySubject.getOwner().getName(),
				studySubject.getStatus().getName()};
	}

	private String dateFormat(Date date) {
		if (date == null) {
			return "";
		} else {
			return DateUtil.printDate(date, RequestUtil.getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE,
					LocaleResolver.getLocale());
		}
	}

	private String dateTimeFormat(Date date) {
		if (date == null) {
			return "";
		} else {
			return DateUtil.printDate(date, RequestUtil.getUserAccountBean().getUserTimeZoneId(),
					DateUtil.DatePattern.TIMESTAMP_WITH_SECONDS, LocaleResolver.getLocale());
		}
	}

	private void autoSizeColumns(WritableSheet sheet) {
		for (int x = 0; x < SIX; x++) {
			CellView cell = sheet.getColumnView(x);
			cell.setAutosize(true);
			sheet.setColumnView(x, cell);
		}
	}

	private String getStudyEventValueByCode(String code) {
		if (code.equals("0")) {
			return ResourceBundleProvider.getResTerm("invalid");
		} else if (code.equals("1")) {
			return ResourceBundleProvider.getResTerm("scheduled");
		} else if (code.equals("2")) {
			return ResourceBundleProvider.getResTerm("not_scheduled");
		} else if (code.equals("3")) {
			return ResourceBundleProvider.getResTerm("data_entry_started");
		} else if (code.equals("4")) {
			return ResourceBundleProvider.getResTerm("completed");
		} else if (code.equals("5")) {
			return ResourceBundleProvider.getResTerm("removed");
		} else if (code.equals("6")) {
			return ResourceBundleProvider.getResTerm("skipped");
		} else if (code.equals("7")) {
			return ResourceBundleProvider.getResTerm("locked");
		} else if (code.equals("8")) {
			return ResourceBundleProvider.getResTerm("signed");
		} else if (code.equals("9")) {
			return ResourceBundleProvider.getResTerm("source_data_verified");
		} else if (code.equals("10")) {
			return ResourceBundleProvider.getResTerm("deleted");
		} else {
			return code;
		}
	}

	private String getEventCRFValueByCode(String code, int auditType, String entityName) {
		if (auditType == AUDIT_EVENT_TYPE_12
				|| entityName.equals("Status")) {
			return ResourceBundleProvider.getResWord(getEventCRFStatusKeyByCode(code));
		} else if (auditType == AUDIT_EVENT_TYPE_32) {
			return ResourceBundleProvider.getResWord(getBooleanValueByCode(code));
		} else {
			return code;
		}
	}

	private String getEventCRFStatusKeyByCode(String statusId) {
		String result = "";
		if (statusId.equals("0")) {
			result = "invalid_";
		} else if (statusId.equals("1")) {
			result = "available_";
		} else if (statusId.equals("2")) {
			result = "completed";
		} else if (statusId.equals("3")) {
			result = "private";
		} else if (statusId.equals("4")) {
			result = "pending";
		} else if (statusId.equals("5")) {
			result = "removed_";
		} else if (statusId.equals("6")) {
			result = "locked";
		} else if (statusId.equals("7")) {
			result = "auto_removed";
		}
		return result;
	}

	private String getBooleanValueByCode(String code) {
		String result = "";
		if (code.equals("0")) {
			result = "FALSE";
		} else if (code.equals("1")) {
			result = "TRUE";
		}
		return result;
	}

	private void resetRowNumber() {
		row = 0;
	}

	private void addBlankRow() {
		row += TWO;
	}

	private String getEventSheetName(StudyEventBean eventBean) {
		return eventBean.getStudyEventDefinition().getName().replace("/", ".") + "_" + eventBean.getSampleOrdinal();
	}
}