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
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InconsistentStateException;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class MarkEventCRFCompleteServlet extends Controller {

	public static final String INPUT_EVENT_CRF_ID = "eventCRFId";

	public static final String INPUT_MARK_COMPLETE = "markComplete";

	public static final String VALUE_YES = "Yes";

	public static final String VALUE_NO = "No";

	public static final String BEAN_DISPLAY = "toc";

	private EventCRFBean getEventCRFBean(HttpServletRequest request) {
        FormProcessor fp = new FormProcessor(request);
		int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID);

        EventCRFDAO ecdao = getEventCRFDAO();
		return (EventCRFBean) ecdao.findByPK(eventCRFId);
	}

	private boolean isEachRequiredFieldFillout(EventCRFBean ecb) {
		ItemDataDAO iddao = getItemDataDAO();
		ArrayList dataList = iddao.findAllBlankRequiredByEventCRFId(ecb.getId(), ecb.getCRFVersionId());
		// empty means all required fields got filled out,return true-jxu
		return dataList.isEmpty();
	}

	private EventDefinitionCRFBean getEventDefinitionCRFBean(EventCRFBean ecb) {
        EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
		return edcdao.findForStudyByStudyEventIdAndCRFVersionId(ecb.getStudyEventId(), ecb.getCRFVersionId());
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);
        StudyBean currentStudy = getCurrentStudy(request);

        FormProcessor fp = new FormProcessor(request);

        EventCRFBean ecb = getEventCRFBean(request);
        EventDefinitionCRFBean edcb = getEventDefinitionCRFBean(ecb);
		DataEntryStage stage = ecb.getStage();

		request.setAttribute(TableOfContentsServlet.INPUT_EVENT_CRF_BEAN, ecb);
		Page errorPage = Page.LIST_STUDY_SUBJECTS_SERVLET;

		if (stage.equals(DataEntryStage.UNCOMPLETED) || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)
				|| stage.equals(DataEntryStage.LOCKED)) {
			throw new InconsistentStateException(errorPage, respage.getString("not_mark_CRF_complete1"));
		}

		if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {

			if (!edcb.isDoubleEntry()) {
				throw new InconsistentStateException(errorPage, respage.getString("not_mark_CRF_complete2"));
			}
		}

		if (!isEachRequiredFieldFillout(ecb)) {
			throw new InconsistentStateException(errorPage, respage.getString("not_mark_CRF_complete4"));
		}

		if (ecb.getInterviewerName().trim().equals("")) {
			throw new InconsistentStateException(errorPage, respage.getString("not_mark_CRF_complete5"));
		}

		if (!fp.isSubmitted()) {
			DisplayTableOfContentsBean toc = getDisplayBean(ecb);
			toc = getDisplayBeanWithShownSections(toc, (DynamicsMetadataService) SpringServletAccess
					.getApplicationContext(getServletContext()).getBean("dynamicsMetadataService"));
			request.setAttribute(BEAN_DISPLAY, toc);

            StudyInfoPanel panel = getStudyInfoPanel(request);
            panel.reset();
			panel.setStudyInfoShown(false);
			panel.setOrderedData(true);
			setToPanel(resword.getString("subject"), toc.getStudySubject().getLabel(), request);
			setToPanel(resword.getString("study_event_definition"), toc.getStudyEventDefinition().getName(), request);

			StudyEventBean seb = toc.getStudyEvent();
			setToPanel(resword.getString("location"), seb.getLocation(), request);
			setToPanel(resword.getString("start_date"), seb.getDateStarted().toString(), request);
			setToPanel(resword.getString("end_date"), seb.getDateEnded().toString(), request);

			setToPanel(resword.getString("CRF"), toc.getCrf().getName(), request);
			setToPanel(resword.getString("CRF_version"), toc.getCrfVersion().getName(), request);

			forwardPage(Page.MARK_EVENT_CRF_COMPLETE, request, response);
		} else {
			boolean markComplete = fp.getString(INPUT_MARK_COMPLETE).equals(VALUE_YES);
			if (markComplete) {
				Status newStatus = ecb.getStatus();
				boolean ide = true;
				if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) && edcb.isDoubleEntry()) {
					newStatus = Status.PENDING;
					ecb.setUpdaterId(ub.getId());
					ecb.setUpdatedDate(new Date());
					ecb.setDateCompleted(new Date());
				} else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) && !edcb.isDoubleEntry()) {
					newStatus = Status.UNAVAILABLE;
					ecb.setUpdaterId(ub.getId());
					ecb.setUpdatedDate(new Date());
					ecb.setDateCompleted(new Date());
					ecb.setDateValidateCompleted(new Date());
				} else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
						|| stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
					newStatus = Status.UNAVAILABLE;
					ecb.setDateValidateCompleted(new Date());
					ide = false;
				}
                EventCRFDAO ecdao = getEventCRFDAO();
				ecb.setStatus(newStatus);
				ecb = (EventCRFBean) ecdao.update(ecb);
				ecdao.markComplete(ecb, ide);

				ItemDataDAO iddao = getItemDataDAO();
				iddao.updateStatusByEventCRF(ecb, newStatus);

				// change status for event
				StudyEventDAO sedao = getStudyEventDAO();
				StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
				seb.setUpdatedDate(new Date());
				seb.setUpdater(ub);

				EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
				ArrayList allCRFs = ecdao.findAllByStudyEvent(seb);
				ArrayList allEDCs = edcdao.findAllActiveByEventDefinitionId(seb.getStudyEventDefinitionId());
				boolean eventCompleted = true;
				for (int i = 0; i < allCRFs.size(); i++) {
					EventCRFBean ec = (EventCRFBean) allCRFs.get(i);
					if (!ec.getStatus().equals(Status.UNAVAILABLE)) {
						eventCompleted = false;
						break;
					}
				}
				if (eventCompleted && allCRFs.size() >= allEDCs.size()) {
					seb.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
				}

				seb = (StudyEventBean) sedao.update(seb);

				addPageMessage(respage.getString("event_CRF_marked_complete"), request);
				request.setAttribute(EnterDataForStudyEventServlet.INPUT_EVENT_ID,
						String.valueOf(ecb.getStudyEventId()));
				forwardPage(Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET, request, response);
			} else {
				request.setAttribute(DataEntryServlet.INPUT_IGNORE_PARAMETERS, Boolean.TRUE);
				addPageMessage(respage.getString("event_CRF_not_marked_complete"), request);
				forwardPage(errorPage, request, response);
			}
		}
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

        FormProcessor fp = new FormProcessor(request);

		if (currentRole.equals(Role.SYSTEM_ADMINISTRATOR) || currentRole.equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.equals(Role.STUDY_DIRECTOR)) {
			return;
		}

        EventCRFBean ecb = getEventCRFBean(request);

		Role r = currentRole.getRole();
		if (ecb.getStage().equals(DataEntryStage.INITIAL_DATA_ENTRY)) {
			if (ecb.getOwnerId() != ub.getId() && !r.equals(Role.STUDY_ADMINISTRATOR) && !r.equals(Role.STUDY_DIRECTOR)) {
				request.setAttribute(TableOfContentsServlet.INPUT_EVENT_CRF_BEAN, ecb);
				addPageMessage(respage.getString("not_mark_CRF_complete6"), request);
				throw new InsufficientPermissionException(Page.TABLE_OF_CONTENTS_SERVLET,
						resexception.getString("not_study_owner"), "1");
			}
		} else if (ecb.getStage().equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
			if (ecb.getValidatorId() != ub.getId() && !r.equals(Role.STUDY_ADMINISTRATOR) && !r.equals(Role.STUDY_DIRECTOR)) {
				request.setAttribute(TableOfContentsServlet.INPUT_EVENT_CRF_BEAN, ecb);
				addPageMessage(respage.getString("not_mark_CRF_complete7"), request);
				throw new InsufficientPermissionException(Page.TABLE_OF_CONTENTS_SERVLET,
						resexception.getString("not_study_owner"), "1");
			}
		}

		return;
	}
}
