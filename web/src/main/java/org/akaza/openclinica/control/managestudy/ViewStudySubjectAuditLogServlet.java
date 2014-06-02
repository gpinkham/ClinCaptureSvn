/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2007 Akaza Research
 */

package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.admin.AuditBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.Utils;
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
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class ViewStudySubjectAuditLogServlet extends Controller {

	/**
	 * Checks whether the user has the right permission to proceed function
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

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		StudySubjectDAO subdao = getStudySubjectDAO();
		SubjectDAO sdao = getSubjectDAO();
		AuditDAO adao = getAuditDAO();

		FormProcessor fp = new FormProcessor(request);

		StudyEventDAO sedao = getStudyEventDAO();
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		EventCRFDAO ecdao = getEventCRFDAO();
		StudyDAO studydao = getStudyDAO();
		CRFDAO cdao = getCRFDAO();
		CRFVersionDAO cvdao = getCRFVersionDAO();
		StudyParameterValueDAO spvdao = getStudyParameterValueDAO();

		ArrayList studySubjectAudits = new ArrayList();
		ArrayList eventCRFAudits = new ArrayList();
		ArrayList studyEventAudits = new ArrayList();
		ArrayList allDeletedEventCRFs = new ArrayList();
		String attachedFilePath = Utils.getAttachedFilePath(currentStudy);

		int studySubId = fp.getInt("id", true);// studySubjectId
		request.setAttribute("id", studySubId);

		if (studySubId == 0) {
			addPageMessage(respage.getString("please_choose_a_subject_to_view"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS, request, response);
		} else {
			StudySubjectBean studySubject = (StudySubjectBean) subdao.findByPK(studySubId);
			StudyBean study = (StudyBean) studydao.findByPK(studySubject.getStudyId());

			study.getStudyParameterConfig().setSubjectPersonIdRequired(
					spvdao.findByHandleAndStudy(study.getId(), "subjectPersonIdRequired").getValue());

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

			request.setAttribute("studySub", studySubject);
			SubjectBean subject = (SubjectBean) sdao.findByPK(studySubject.getSubjectId());
			request.setAttribute("subject", subject);

			request.setAttribute("study", study);

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

			// Global subject value changed
			studySubjectAudits.addAll(adao.findSubjectAuditEvents(subject.getId()));
			studySubjectAudits.addAll(studySubjectAuditEvents);
			studySubjectAudits.addAll(adao.findStudySubjectGroupAssignmentAuditEvents(studySubject.getId()));
			request.setAttribute("studySubjectAudits", studySubjectAudits);

			// Get the list of events
			ArrayList events = sedao.findAllByStudySubject(studySubject);
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
			ItemDataDAO itemDataDao = new ItemDataDAO(getDataSource());
			for (Object o : eventCRFAudits) {
				AuditBean ab = (AuditBean) o;
				if (ab.getAuditTable().equalsIgnoreCase("item_data")) {
					ItemDataBean idBean = (ItemDataBean) itemDataDao.findByPK(ab.getEntityId());
					ab.setOrdinal(idBean.getOrdinal());
				}
			}
			request.setAttribute("events", events);
			request.setAttribute("eventCRFAudits", eventCRFAudits);
			request.setAttribute("studyEventAudits", studyEventAudits);
			request.setAttribute("allDeletedEventCRFs", allDeletedEventCRFs);
			request.setAttribute("attachedFilePath", attachedFilePath);

			forwardPage(Page.VIEW_STUDY_SUBJECT_AUDIT, request, response);

		}
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

}
