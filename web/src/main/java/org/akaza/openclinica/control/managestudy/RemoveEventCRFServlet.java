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
package org.akaza.openclinica.control.managestudy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.stereotype.Component;

import com.clinovo.util.EmailUtil;

/**
 * Removes an Event CRF.
 * 
 * @author jxu
 * 
 */
@Component
public class RemoveEventCRFServlet extends SpringServlet {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_locked"), request, response);
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_frozen"), request, response);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		UserAccountBean currentUser = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		FormProcessor fp = new FormProcessor(request);
		// eventCRFId
		int eventCRFId = fp.getInt("id");
		// studySubjectId
		int studySubId = fp.getInt("studySubId");
		checkStudyLocked("ViewStudySubject?id" + studySubId, getResPage().getString("current_study_locked"), request,
				response);
		StudyEventDAO sedao = getStudyEventDAO();
		StudySubjectDAO subdao = getStudySubjectDAO();
		EventCRFDAO ecdao = getEventCRFDAO();
		StudyDAO sdao = getStudyDAO();

		if (eventCRFId == 0) {
			addPageMessage(getResPage().getString("please_choose_an_event_CRF_to_remove"), request);
			request.setAttribute("id", Integer.toString(studySubId));
			forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
		} else {

			EventCRFBean eventCRF = (EventCRFBean) ecdao.findByPK(eventCRFId);

			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
			request.setAttribute("studySub", studySub);

			// construct info needed on view event crf page
			CRFDAO cdao = getCRFDAO();
			CRFVersionDAO cvdao = getCRFVersionDAO();
			StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();

			int crfVersionId = eventCRF.getCRFVersionId();
			CRFBean cb = cdao.findByVersionId(crfVersionId);
			eventCRF.setCrf(cb);

			CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
			eventCRF.setCrfVersion(cvb);

			// then get the definition so we can call
			// DisplayEventCRFBean.setFlags
			int studyEventId = eventCRF.getStudyEventId();
			StudyEventBean event = (StudyEventBean) sedao.findByPK(studyEventId);
			int studyEventDefinitionId = sedao.getDefinitionIdFromStudyEventId(studyEventId);
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(studyEventDefinitionId);

			event.setStudyEventDefinition(sed);
			request.setAttribute("event", event);

			EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();

			StudyBean study = (StudyBean) sdao.findByPK(studySub.getStudyId());
			EventDefinitionCRFBean edc = edcdao.findByStudyEventDefinitionIdAndCRFId(study, studyEventDefinitionId,
					cb.getId());

			DisplayEventCRFBean dec = new DisplayEventCRFBean();
			dec.setEventCRF(eventCRF);
			dec.setFlags(eventCRF, currentUser, currentRole, edc);

			request.setAttribute("items", getItemDataDAO().findAllByEventCRFId(eventCRF.getId()));

			String action = request.getParameter("action");
			if ("confirm".equalsIgnoreCase(action)) {
				if (eventCRF.getStatus().isDeleted()) {
					addPageMessage(getResPage().getString("this_event_CRF_is_removed_for_this_study") + " "
							+ getResPage().getString("please_contact_sysadmin_for_more_information"), request);
					request.setAttribute("id", Integer.toString(studySubId));
					forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
					return;
				}

				request.setAttribute("displayEventCRF", dec);

				forwardPage(Page.REMOVE_EVENT_CRF, request, response);
			} else {
				logger.info("submit to remove the event CRF from study");

				getEventCRFService().removeEventCRF(eventCRF, currentUser);

				String messageBody = getResPage().getString("the_event_CRF") + " " + cb.getName() + " "
						+ getResPage().getString("has_been_removed_from_the_event")
						+ event.getStudyEventDefinition().getName() + ".";
				addPageMessage(messageBody, request);
				String emailBody = EmailUtil.getEmailBodyStart() + messageBody + "<br/><ul>"
						+ getResWord().getString("job_error_mail.serverUrl") + " " + SQLInitServlet.getSystemURL() + "</li>"
						+ getResWord().getString("job_error_mail.studyName") + " " + study.getName() + "</li>" + "<li><b>"
						+ getResWord().getString("mail.removed_by") + ":</b> " + currentUser.getName() + "</li>" + "<li><b>"
						+ getResWord().getString("subject") + "</b>: " + studySub.getLabel() + "</li></ul>"
						+ EmailUtil.getEmailBodyEnd() + EmailUtil.getEmailFooter(getLocale());
				String emailHeader = getResPage().getString("remove_event_CRF_from_event") + " "
						+ getResWord().getString("subject") + ": " + studySub.getLabel();

				sendEmail(emailHeader, emailBody, request);
				storePageMessages(request);
				response.sendRedirect(request.getContextPath().concat(Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName())
						.concat("?id=").concat(Integer.toString(studySubId)));
			}
		}
	}
	/**
	 * Send email to director and administrator.
	 * 
	 * @param emailBody
	 *            String
	 */
	private void sendEmail(String emailHeader, String emailBody, HttpServletRequest request) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		logger.info("Sending email...");
		// to study director

		sendEmail(ub.getEmail().trim(), emailHeader, emailBody, false, request);
		sendEmail(EmailEngine.getAdminEmail(), emailHeader, emailBody, false, request);
		logger.info("Sending email done..");
	}

}
