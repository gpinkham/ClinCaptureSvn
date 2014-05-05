/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2007 Akaza Research
 */

package org.akaza.openclinica.control.managestudy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.akaza.openclinica.bean.admin.AuditBean;
import org.akaza.openclinica.bean.admin.DeletedEventCRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * @author jsampson
 * @author akung
 */

@SuppressWarnings("serial")
@Component
public class ExportExcelStudySubjectAuditLogServlet extends Controller {

	/**
	 * Checks whether the user has the right permission to proceed function
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study") + " "
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS,
				resexception.getString("not_study_director"), "1");

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		StudySubjectDAO subdao = getStudySubjectDAO();
		SubjectDAO sdao = getSubjectDAO();
		AuditDAO adao = getAuditDAO();

		StudyEventDAO sedao = getStudyEventDAO();
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		EventCRFDAO ecdao = getEventCRFDAO();
		StudyDAO studydao = getStudyDAO();
		CRFDAO cdao = getCRFDAO();
		CRFVersionDAO cvdao = getCRFVersionDAO();
		StudySubjectBean studySubject = null;

		ArrayList events = null;
		ArrayList studySubjectAudits = new ArrayList();
		ArrayList eventCRFAudits = new ArrayList();
		ArrayList studyEventAudits = new ArrayList();
		ArrayList allDeletedEventCRFs = new ArrayList();
		FormProcessor fp = new FormProcessor(request);

		int studySubId = fp.getInt("id", true);

		if (studySubId == 0) {
			addPageMessage(respage.getString("please_choose_a_subject_to_view"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS, request, response);
		} else {
			studySubject = (StudySubjectBean) subdao.findByPK(studySubId);
			StudyBean study = (StudyBean) studydao.findByPK(studySubject.getStudyId());
			// Check if this StudySubject would be accessed from the Current Study
			if (studySubject.getStudyId() != currentStudy.getId()) {
				if (currentStudy.getParentStudyId() > 0) {
					addPageMessage(
							respage.getString("no_have_correct_privilege_current_study") + " "
									+ respage.getString("change_active_study_or_contact"), request);
					forwardPage(Page.MENU_SERVLET, request, response);
					return;
				} else {
					// The SubjectStudy is not belong to currentstudy and current study is not a site.
					Collection sites = studydao.findOlnySiteIdsByStudy(currentStudy);
					if (!sites.contains(study.getId())) {
						addPageMessage(
								respage.getString("no_have_correct_privilege_current_study") + " "
										+ respage.getString("change_active_study_or_contact"), request);
						forwardPage(Page.MENU_SERVLET, request, response);
						return;
					}
				}
			}

			SubjectBean subject = (SubjectBean) sdao.findByPK(studySubject.getSubjectId());

			/* Show both study subject and subject audit events together */
			// Study subject value changed
			Collection studySubjectAuditEvents = adao.findStudySubjectAuditEvents(studySubject.getId());
			// Text values will be shown on the page for the corresponding
			// integer values.
			for (Object studySubjectAuditEvent : studySubjectAuditEvents) {
				AuditBean auditBean = (AuditBean) studySubjectAuditEvent;
				if (auditBean.getAuditEventTypeId() == 3) {
					auditBean.setOldValue(Status.get(Integer.parseInt(auditBean.getOldValue())).getName());
					auditBean.setNewValue(Status.get(Integer.parseInt(auditBean.getNewValue())).getName());
				}
			}
			studySubjectAudits.addAll(studySubjectAuditEvents);

			// Global subject value changed
			studySubjectAudits.addAll(adao.findSubjectAuditEvents(subject.getId()));

			studySubjectAudits.addAll(adao.findStudySubjectGroupAssignmentAuditEvents(studySubject.getId()));

			// Get the list of events
			events = sedao.findAllByStudySubject(studySubject);
			for (Object event : events) {
				// Link study event definitions
				StudyEventBean studyEvent = (StudyEventBean) event;
				studyEvent.setStudyEventDefinition((StudyEventDefinitionBean) seddao.findByPK(studyEvent
						.getStudyEventDefinitionId()));

				// Link event CRFs
				studyEvent.setEventCRFs(ecdao.findAllByStudyEvent(studyEvent));

				// Find deleted Event CRFs
				List deletedEventCRFs = adao.findDeletedEventCRFsFromAuditEvent(studyEvent.getId());
				allDeletedEventCRFs.addAll(deletedEventCRFs);
				logger.info("deletedEventCRFs size[" + deletedEventCRFs.size() + "]");
			}

			for (Object event : events) {
				StudyEventBean studyEvent = (StudyEventBean) event;
				studyEventAudits.addAll(adao.findStudyEventAuditEvents(studyEvent.getId()));

				ArrayList eventCRFs = studyEvent.getEventCRFs();
				for (Object eventCRF1 : eventCRFs) {
					// Link CRF and CRF Versions
					EventCRFBean eventCRF = (EventCRFBean) eventCRF1;
					eventCRF.setCrfVersion((CRFVersionBean) cvdao.findByPK(eventCRF.getCRFVersionId()));
					eventCRF.setCrf(cdao.findByVersionId(eventCRF.getCRFVersionId()));
					// Get the event crf audits
					eventCRFAudits.addAll(adao.findEventCRFAuditEventsWithItemDataType(eventCRF.getId()));
					logger.info("eventCRFAudits size [" + eventCRFAudits.size() + "] eventCRF id [" + eventCRF.getId()
							+ "]");
				}
			}
			ItemDataDAO itemDataDao = getItemDataDAO();
			for (Object o : eventCRFAudits) {
				AuditBean ab = (AuditBean) o;
				if (ab.getAuditTable().equalsIgnoreCase("item_data")) {
					ItemDataBean idBean = (ItemDataBean) itemDataDao.findByPK(ab.getEntityId());
					ab.setOrdinal(idBean.getOrdinal());
				}
			}

		}

		WritableFont headerFormat = new WritableFont(WritableFont.ARIAL, 8, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE, Colour.VIOLET2);
		WritableCellFormat cellFormat = new WritableCellFormat();
		cellFormat.setFont(headerFormat);
		// WritableFont dataFormat = new WritableFont(WritableFont.ARIAL, 8, WritableFont.NO_BOLD,false,
		// UnderlineStyle.NO_UNDERLINE, Colour.BLACK);

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=export.xls");

		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setLocale(new Locale("en", "EN"));
		WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream(), wbSettings);

		int row = 0;

		// Subject Information
		workbook.createSheet("Subject Information", 0);
		WritableSheet excelSheet = workbook.getSheet(0);
		// Subject Summary
		String[] excelRow = new String[] { "study_subject_ID", "secondary_subject_ID", "date_of_birth", "person_ID",
				"created_by", "status" };
		for (int i = 0; i < excelRow.length; i++) {
			Label label = new Label(i, row, ResourceBundleProvider.getResWord(excelRow[i]), cellFormat);
			excelSheet.addCell(label);
		}
		row++;

		excelRow = new String[] { studySubject != null ? studySubject.getLabel() : null,
				studySubject != null ? studySubject.getSecondaryLabel() : null,
				dateFormat(studySubject != null ? studySubject.getDateOfBirth() : null),
				studySubject != null ? studySubject.getUniqueIdentifier() : null,
				studySubject != null ? studySubject.getOwner().getName() : null,
				studySubject != null ? studySubject.getStatus().getName() : null };
		for (int i = 0; i < excelRow.length; i++) {
			Label label = new Label(i, row, ResourceBundleProvider.getResWord(excelRow[i]));
			excelSheet.addCell(label);
		}
		row++;
		row++;

		// Subject Audit Events
		excelRow = new String[] { "audit_event", "date_time_of_server", "user", "value_type", "old", "new" };
		for (int i = 0; i < excelRow.length; i++) {
			Label label = new Label(i, row, ResourceBundleProvider.getResWord(excelRow[i]), cellFormat);
			excelSheet.addCell(label);
		}
		row++;

		for (Object studySubjectAudit : studySubjectAudits) {
			AuditBean audit = (AuditBean) studySubjectAudit;
			excelRow = new String[] { audit.getAuditEventTypeName(), dateTimeFormat(audit.getAuditDate()),
					audit.getUserName(), audit.getEntityName(), audit.getOldValue(), audit.getNewValue() };
			for (int i = 0; i < excelRow.length; i++) {
				Label label = new Label(i, row, excelRow[i]);
				excelSheet.addCell(label);
			}
			row++;
		}
		row++;

		// Study Events
		excelRow = new String[] { "study_events", "location", "date", "occurrence_number" };
		for (int i = 0; i < excelRow.length; i++) {
			Label label = new Label(i, row, ResourceBundleProvider.getResWord(excelRow[i]), cellFormat);
			excelSheet.addCell(label);
		}
		row++;

		if (events != null) {
			for (Object event1 : events) {
				StudyEventBean event = (StudyEventBean) event1;
				if (event.getStartTimeFlag()) {
					excelRow = new String[] { event.getStudyEventDefinition().getName(), event.getLocation(),
							dateTimeFormat(event.getDateStarted()), Integer.toString(event.getSampleOrdinal()) };
				} else {
					excelRow = new String[] { event.getStudyEventDefinition().getName(), event.getLocation(),
							dateFormat(event.getDateStarted()), Integer.toString(event.getSampleOrdinal()) };
				}
				for (int i = 0; i < excelRow.length; i++) {
					Label label = new Label(i, row, excelRow[i]);
					excelSheet.addCell(label);
				}
				row++;
			}
		}
		autoSizeColumns(excelSheet);

		int sheet = 0;

		// Study Event Summary Looper
		if (events != null) {
			for (Object event1 : events) {
				row = 0;
				sheet++;
				StudyEventBean event = (StudyEventBean) event1;
				workbook.createSheet(
						event.getStudyEventDefinition().getName().replace("/", ".") + "_" + event.getSampleOrdinal(),
						sheet);
				excelSheet = workbook.getSheet(sheet);

				Label label;

				// Header
				label = new Label(0, row, ResourceBundleProvider.getResWord("name"), cellFormat);
				excelSheet.addCell(label);
				label = new Label(1, row, event.getStudyEventDefinition().getName(), cellFormat);
				excelSheet.addCell(label);
				row++;
				label = new Label(0, row, "Location");
				excelSheet.addCell(label);
				label = new Label(1, row, event.getLocation());
				excelSheet.addCell(label);
				row++;
				label = new Label(0, row, "Start Date");
				excelSheet.addCell(label);
				if (event.getStartTimeFlag()) {
					label = new Label(1, row, dateTimeFormat(event.getDateStarted()));
				} else {
					label = new Label(1, row, dateFormat(event.getDateStarted()));
				}
				excelSheet.addCell(label);
				row++;
				label = new Label(0, row, "Status");
				excelSheet.addCell(label);
				label = new Label(1, row, event.getSubjectEventStatus().getName());
				excelSheet.addCell(label);
				row++;
				label = new Label(0, row, ResourceBundleProvider.getResWord("occurrence_number"));
				excelSheet.addCell(label);
				label = new Label(1, row, Integer.toString(event.getSampleOrdinal()));
				excelSheet.addCell(label);
				row++;
				row++;
				// End Header

				// Audit for Deleted Event CRFs
				excelRow = new String[] { "name", "version", "deleted_by", "delete_date" };
				for (int i = 0; i < excelRow.length; i++) {
					label = new Label(i, row, ResourceBundleProvider.getResWord(excelRow[i]), cellFormat);
					excelSheet.addCell(label);
				}
				row++;

				for (Object allDeletedEventCRF : allDeletedEventCRFs) {
					DeletedEventCRFBean deletedEventCRF = (DeletedEventCRFBean) allDeletedEventCRF;
					if (deletedEventCRF.getStudyEventId() == event.getId()) {
						excelRow = new String[] { deletedEventCRF.getCrfName(), deletedEventCRF.getCrfVersion(),
								deletedEventCRF.getDeletedBy(), dateFormat(deletedEventCRF.getDeletedDate()) };
						for (int i = 0; i < excelRow.length; i++) {
							label = new Label(i, row, excelRow[i]);
							excelSheet.addCell(label);
						}
						row++;
					}
				}
				row++;
				row++;

				// Audit Events for Study Event
				excelRow = new String[] { "audit_event", "date_time_of_server", "user", "value_type", "old", "new" };
				for (int i = 0; i < excelRow.length; i++) {
					label = new Label(i, row, ResourceBundleProvider.getResWord(excelRow[i]), cellFormat);
					excelSheet.addCell(label);
				}
				row++;

				for (Object studyEventAudit : studyEventAudits) {
					AuditBean studyEvent = (AuditBean) studyEventAudit;
					if (studyEvent.getEntityId() == event.getId()) {
						String getOld = studyEvent.getOldValue();
						String oldValue;
						if (getOld.equals("0"))
							oldValue = ResourceBundleProvider.getResWord("invalid_");
						else if (getOld.equals("1"))
							oldValue = ResourceBundleProvider.getResWord("scheduled_");
						else if (getOld.equals("2"))
							oldValue = ResourceBundleProvider.getResWord("not_scheduled");
						else if (getOld.equals("3"))
							oldValue = ResourceBundleProvider.getResWord("data_entry_started_");
						else if (getOld.equals("4"))
							oldValue = ResourceBundleProvider.getResWord("completed_");
						else if (getOld.equals("5"))
							oldValue = ResourceBundleProvider.getResWord("stopped_");
						else if (getOld.equals("6"))
							oldValue = ResourceBundleProvider.getResWord("skipped_");
						else if (getOld.equals("7"))
							oldValue = ResourceBundleProvider.getResWord("locked");
						else if (getOld.equals("8"))
							oldValue = ResourceBundleProvider.getResWord("signed");
						else
							oldValue = studyEvent.getOldValue();

						String getNew = studyEvent.getNewValue();
						String newValue;
						if (getNew.equals("0"))
							newValue = ResourceBundleProvider.getResWord("invalid_");
						else if (getNew.equals("1"))
							newValue = ResourceBundleProvider.getResWord("scheduled_");
						else if (getNew.equals("2"))
							newValue = ResourceBundleProvider.getResWord("not_scheduled");
						else if (getNew.equals("3"))
							newValue = ResourceBundleProvider.getResWord("data_entry_started_");
						else if (getNew.equals("4"))
							newValue = ResourceBundleProvider.getResWord("completed_");
						else if (getNew.equals("5"))
							newValue = ResourceBundleProvider.getResWord("removed_");
						else if (getNew.equals("6"))
							newValue = ResourceBundleProvider.getResWord("skipped_");
						else if (getNew.equals("7"))
							newValue = ResourceBundleProvider.getResWord("locked");
						else if (getNew.equals("8"))
							newValue = ResourceBundleProvider.getResWord("signed");
						else if (getNew.equals("9"))
							newValue = ResourceBundleProvider.getResWord("frozen_");
						else
							newValue = studyEvent.getNewValue();

						excelRow = new String[] { studyEvent.getAuditEventTypeName(),
								dateTimeFormat(studyEvent.getAuditDate()), studyEvent.getUserName(),
								studyEvent.getEntityName() + "(" + studyEvent.getOrdinal() + ")", oldValue, newValue };
						for (int i = 0; i < excelRow.length; i++) {
							label = new Label(i, row, excelRow[i]);
							excelSheet.addCell(label);
						}
						row++;
					}
				}
				row++;

				// Event CRFs Audit Events
				for (int j = 0; j < event.getEventCRFs().size(); j++) {
					EventCRFBean eventCrf = (EventCRFBean) event.getEventCRFs().get(j);

					// Audit Events for Study Event
					excelRow = new String[] { "name", "version", "date_interviewed", "interviewer_name", "owner" };
					for (int i = 0; i < excelRow.length; i++) {
						label = new Label(i, row, ResourceBundleProvider.getResWord(excelRow[i]), cellFormat);
						excelSheet.addCell(label);
					}
					row++;

					excelRow = new String[] { eventCrf.getCrf().getName(), eventCrf.getCrfVersion().getName(),
							dateTimeFormat(eventCrf.getDateInterviewed()), eventCrf.getInterviewerName(),
							eventCrf.getOwner().getName() };
					for (int i = 0; i < excelRow.length; i++) {
						label = new Label(i, row, excelRow[i]);
						excelSheet.addCell(label);
					}
					row++;
					row++;

					excelRow = new String[] { "audit_event", "date_time_of_server", "user", "value_type", "old", "new" };
					for (int i = 0; i < excelRow.length; i++) {
						label = new Label(i, row, ResourceBundleProvider.getResWord(excelRow[i]), cellFormat);
						excelSheet.addCell(label);
					}
					row++;

					for (Object eventCRFAudit : eventCRFAudits) {
						AuditBean eventCrfAudit = (AuditBean) eventCRFAudit;
						if (eventCrfAudit.getEventCRFId() == eventCrf.getId()) {
							String oldValue = "";
							String newValue = "";
							if (eventCrfAudit.getAuditEventTypeId() == 12
									|| eventCrfAudit.getEntityName().equals("Status")) {
								String getOld = eventCrfAudit.getOldValue();
								if (getOld.equals("0"))
									oldValue = ResourceBundleProvider.getResWord("invalid_");
								else if (getOld.equals("1"))
									oldValue = ResourceBundleProvider.getResWord("available_");
								else if (getOld.equals("2"))
									oldValue = ResourceBundleProvider.getResWord("completed");
								else if (getOld.equals("3"))
									oldValue = ResourceBundleProvider.getResWord("private");
								else if (getOld.equals("4"))
									oldValue = ResourceBundleProvider.getResWord("pending");
								else if (getOld.equals("5"))
									oldValue = ResourceBundleProvider.getResWord("removed_");
								else if (getOld.equals("6"))
									oldValue = ResourceBundleProvider.getResWord("locked");
								else if (getOld.equals("7"))
									oldValue = ResourceBundleProvider.getResWord("auto_removed");
							} else if (eventCrfAudit.getAuditEventTypeId() == 32) {
								String getOld = eventCrfAudit.getOldValue();
								if (getOld.equals("0"))
									oldValue = ResourceBundleProvider.getResWord("FALSE");
								else if (getOld.equals("1"))
									oldValue = ResourceBundleProvider.getResWord("TRUE");
							} else {
								oldValue = eventCrfAudit.getOldValue();
							}

							if (eventCrfAudit.getAuditEventTypeId() == 12
									|| eventCrfAudit.getEntityName().equals("Status")) {
								String getNew = eventCrfAudit.getNewValue();
								if (getNew.equals("0"))
									newValue = ResourceBundleProvider.getResWord("invalid_");
								else if (getNew.equals("1"))
									newValue = ResourceBundleProvider.getResWord("available_");
								else if (getNew.equals("2"))
									newValue = ResourceBundleProvider.getResWord("completed");
								else if (getNew.equals("3"))
									newValue = ResourceBundleProvider.getResWord("private");
								else if (getNew.equals("4"))
									newValue = ResourceBundleProvider.getResWord("pending");
								else if (getNew.equals("5"))
									newValue = ResourceBundleProvider.getResWord("removed_");
								else if (getNew.equals("6"))
									newValue = ResourceBundleProvider.getResWord("locked");
								else if (getNew.equals("7"))
									newValue = ResourceBundleProvider.getResWord("auto_removed");
							} else if (eventCrfAudit.getAuditEventTypeId() == 32) {
								String getNew = eventCrfAudit.getNewValue();
								if (getNew.equals("0"))
									newValue = ResourceBundleProvider.getResWord("FALSE");
								else if (getNew.equals("1"))
									newValue = ResourceBundleProvider.getResWord("TRUE");
							} else {
								newValue = eventCrfAudit.getNewValue();
							}

							excelRow = new String[] { eventCrfAudit.getAuditEventTypeName(),
									dateTimeFormat(eventCrfAudit.getAuditDate()), eventCrfAudit.getUserName(),
									eventCrfAudit.getEntityName() + "(" + eventCrfAudit.getOrdinal() + ")", oldValue,
									newValue };
							for (int i = 0; i < excelRow.length; i++) {
								label = new Label(i, row, excelRow[i]);
								excelSheet.addCell(label);
							}
							row++;
						}
					}
					row++;
				}

				autoSizeColumns(excelSheet);
			}
		}

		workbook.write();
		workbook.close();
		request.getSession().setAttribute("subject", null);
		request.getSession().setAttribute("study", null);
		request.getSession().setAttribute("studySub", null);
		request.getSession().setAttribute("studyEventAudits", null);
		request.getSession().setAttribute("studySubjectAudits", null);
		request.getSession().setAttribute("events", null);
		request.getSession().setAttribute("eventCRFAudits", null);
		request.getSession().setAttribute("allDeletedEventCRFs", null);
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return Controller.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	private String dateFormat(Date date) {
		if (date == null) {
			return "";
		} else {
			SimpleDateFormat dteFormat = new SimpleDateFormat(ResourceBundleProvider.getFormatBundle().getString(
					"date_format_string"));
			return dteFormat.format(date);
		}
	}

	private String dateTimeFormat(Date date) {
		if (date == null) {
			return "";
		} else {
			SimpleDateFormat dtetmeFormat = new SimpleDateFormat(ResourceBundleProvider.getFormatBundle().getString(
					"date_time_format_string"));
			return dtetmeFormat.format(date);
		}
	}

	private void autoSizeColumns(WritableSheet sheet) {
		for (int x = 0; x < 6; x++) {
			CellView cell = sheet.getColumnView(x);
			cell.setAutosize(true);
			sheet.setColumnView(x, cell);
		}
	}

}
