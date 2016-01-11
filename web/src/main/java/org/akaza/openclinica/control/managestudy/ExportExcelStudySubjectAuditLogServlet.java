/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2007 Akaza Research
 */

package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clinovo.builder.AuditLogWorkbookBuilder;
import com.clinovo.model.AuditLogRandomization;
import com.clinovo.service.AuditLogRandomizationService;
import com.clinovo.service.AuditLogService;
import jxl.write.WritableWorkbook;

import org.akaza.openclinica.bean.admin.AuditBean;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * ExportExcelStudySubjectAuditLogServlet class.
 */
@SuppressWarnings({"serial", "unchecked", "rawtypes"})
@Component
public class ExportExcelStudySubjectAuditLogServlet extends Controller {

	public static final int AUDIT_EVENT_TYPE_3 = 3; // Subject's status was changed

	/**
	 * Checks whether the user has the right permission to proceed function.
	 *
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws InsufficientPermissionException the InsufficientPermissionException
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study") + " "
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS,
				getResException().getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);
		AuditLogService auditLogService = getAuditLogService(getServletContext());

		StudySubjectDAO subdao = getStudySubjectDAO();
		SubjectDAO sdao = getSubjectDAO();
		AuditDAO adao = getAuditDAO();

		StudyEventDAO sedao = getStudyEventDAO();
		EventCRFDAO ecdao = getEventCRFDAO();
		CRFDAO cdao = getCRFDAO();
		CRFVersionDAO cvdao = getCRFVersionDAO();

		ArrayList studySubjectAudits = new ArrayList();
		ArrayList eventCRFAudits = new ArrayList();
		ArrayList studyEventAudits = new ArrayList();
		ArrayList allDeletedEventCRFs = new ArrayList();
		FormProcessor fp = new FormProcessor(request);

		int studySubId = fp.getInt("id", true);

		if (studySubId == 0) {
			addPageMessage(getResPage().getString("please_choose_a_subject_to_view"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS, request, response);
			return;
		}

		StudySubjectBean studySubject = (StudySubjectBean) subdao.findByPK(studySubId);

		if (subjectCanBeAccessedFromCurrentStudy(studySubject, currentStudy)) {
			addPageMessage(
					getResPage().getString("no_have_correct_privilege_current_study") + " "
					+ getResPage().getString("change_active_study_or_contact"), request);
			forwardPage(Page.MENU_SERVLET, request, response);
			return;
		}

		SubjectBean subject = (SubjectBean) sdao.findByPK(studySubject.getSubjectId());
		studySubject.setDateOfBirth(subject.getDateOfBirth());
		studySubject.setUniqueIdentifier(subject.getUniqueIdentifier());
		/* Show both study subject and subject audit events together */
		studySubjectAudits.addAll(adao.findSubjectAuditEvents(subject.getId()));
		studySubjectAudits.addAll(getStudySubjectAuditEvents(studySubject));
		studySubjectAudits.addAll(adao.findStudySubjectGroupAssignmentAuditEvents(studySubject.getId()));
		// Get the list of events
		List<StudyEventBean> events = sedao.findAllByStudySubject(studySubject);
		auditLogService.addDeletedStudyEvents(studySubject, events);

		for (StudyEventBean studyEvent : events) {
			// Link event CRFs
			studyEvent.setEventCRFs(ecdao.findAllByStudyEvent(studyEvent));
			auditLogService.addDeletedEventCRFs(studySubject, studyEvent);
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
				CRFVersionBean crfVersionBean = (CRFVersionBean) cvdao.findByPK(eventCRF.getCRFVersionId());
				if (crfVersionBean.getId() > 0) {
					eventCRF.setCrfVersion(crfVersionBean);
				}
				CRFBean crfBean = cdao.findByVersionId(eventCRF.getCRFVersionId());
				if (crfBean.getId() > 0) {
					eventCRF.setCrf(crfBean);
				}
				// Get the event crf audits
				eventCRFAudits.addAll(adao.findEventCRFAuditEventsWithItemDataType(eventCRF.getId(), eventCRF.getCRFVersionId()));
				logger.info("eventCRFAudits size [" + eventCRFAudits.size() + "] eventCRF id [" + eventCRF.getId() + "]");
			}
		}
		List<AuditLogRandomization> randomizationAudit = getAuditLogRandomizationService(getServletContext()).findAllByStudySubjectId(studySubId);
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=export.xls");

		AuditLogWorkbookBuilder workbookFactory = new AuditLogWorkbookBuilder(response);
		WritableWorkbook workbook = workbookFactory.buildWorkbook(studySubject, studySubjectAudits, events,
				allDeletedEventCRFs, studyEventAudits, eventCRFAudits, randomizationAudit);
		workbook.write();
		workbook.close();
		clearSession(request);
	}

	private void clearSession(HttpServletRequest request) {
		request.getSession().setAttribute("subject", null);
		request.getSession().setAttribute("study", null);
		request.getSession().setAttribute("studySub", null);
		request.getSession().setAttribute("studyEventAudits", null);
		request.getSession().setAttribute("studySubjectAudits", null);
		request.getSession().setAttribute("events", null);
		request.getSession().setAttribute("eventCRFAudits", null);
		request.getSession().setAttribute("allDeletedEventCRFs", null);
	}

	private Collection getStudySubjectAuditEvents(StudySubjectBean studySubject) {
		Collection studySubjectAuditEvents = getAuditDAO().findStudySubjectAuditEvents(studySubject.getId());
		// Text values will be shown on the page for the corresponding integer values.
		for (Object studySubjectAuditEvent : studySubjectAuditEvents) {
			AuditBean auditBean = (AuditBean) studySubjectAuditEvent;
			if (auditBean.getAuditEventTypeId() == AUDIT_EVENT_TYPE_3) {
				auditBean.setOldValue(Status.get(Integer.parseInt(auditBean.getOldValue())).getName());
				auditBean.setNewValue(Status.get(Integer.parseInt(auditBean.getNewValue())).getName());
			}
		}
		return studySubjectAuditEvents;
	}

	private boolean subjectCanBeAccessedFromCurrentStudy(StudySubjectBean studySubject, StudyBean currentStudy) {
		return studySubject.getStudyId() != currentStudy.getId()
				&& (!currentStudy.isSite() || subjectWasCreatedInDifferentStudy(studySubject, currentStudy));
	}

	private boolean subjectWasCreatedInDifferentStudy(StudySubjectBean studySubject, StudyBean currentStudy) {
		StudyDAO studydao = getStudyDAO();
		StudyBean subjectsStudy = (StudyBean) studydao.findByPK(studySubject.getStudyId());
		Collection sites = studydao.findOlnySiteIdsByStudy(currentStudy);
		return !sites.contains(subjectsStudy.getId());
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

	/**
	 * Get Audit Log Service.
	 * @param context ServletContext
	 * @return AuditLogService
	 */
	public AuditLogService getAuditLogService(ServletContext context) {
		return (AuditLogService) SpringServletAccess.getApplicationContext(context).getBean("auditLogService");
	}

	private AuditLogRandomizationService getAuditLogRandomizationService(ServletContext context) {
		return (AuditLogRandomizationService) SpringServletAccess.getApplicationContext(context).getBean("auditLogRandomizationService");
	}
}