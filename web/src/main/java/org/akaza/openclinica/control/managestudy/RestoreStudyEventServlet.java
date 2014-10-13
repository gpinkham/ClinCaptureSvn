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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
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
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author jxu
 * 
 *         Restores a removed study event and all its data
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
@Component
public class RestoreStudyEventServlet extends Controller {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"), request, response);

		if (getUserAccountBean(request).isSysAdmin() || getCurrentRole(request).isStudyAdministrator()) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		// studyEventId
		int studyEventId = fp.getInt("id");
		// studySubjectId
		int studySubId = fp.getInt("studySubId");
		UserAccountBean currentUser = getUserAccountBean(request);

		StudyEventDAO sedao = getStudyEventDAO();
		StudySubjectDAO subdao = getStudySubjectDAO();

		if (studyEventId == 0) {
			addPageMessage(respage.getString("please_choose_a_SE_to_restore"), request);
			request.setAttribute("id", Integer.toString(studySubId));
			forwardToViewStudySubjectPage(request, response);
		} else {

			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);

			// A study event could not be restored if its study
			// subject has been removed
			Status s = studySub.getStatus();
			if (s.isDeleted()) {
				addPageMessage(
						new StringBuilder("").append(resword.getString("study_event"))
								.append(resterm.getString("could_not_be")).append(resterm.getString("restored"))
								.append(".").append(respage.getString("study_subject_has_been_deleted")).toString(),
						request);
				request.setAttribute("id", Integer.toString(studySubId));
				forwardToViewStudySubjectPage(request, response);
			}
			// YW

			StudyEventBean event = (StudyEventBean) sedao.findByPK(studyEventId);

			request.setAttribute("studySub", studySub);

			StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
					.findByPK(event.getStudyEventDefinitionId());
			event.setStudyEventDefinition(sed);

			StudyDAO studydao = getStudyDAO();
			StudyBean study = (StudyBean) studydao.findByPK(studySub.getStudyId());

			String action = request.getParameter("action");
			if ("confirm".equalsIgnoreCase(action)) {
				if (event.getStatus().equals(Status.AVAILABLE)) {
					addPageMessage(
							new StringBuilder("")
									.append(respage.getString("this_event_is_already_available_for_study")).append(" ")
									.append(respage.getString("please_contact_sysadmin_for_more_information"))
									.toString(), request);
					request.setAttribute("id", Integer.toString(studySubId));
					forwardToViewStudySubjectPage(request, response);
					return;
				}

				EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
				// find all crfs in the definition
				ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllByEventDefinitionId(study, sed.getId());

				EventCRFDAO ecdao = getEventCRFDAO();
				ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

				// construct info needed on view study event page
				DisplayStudyEventBean de = new DisplayStudyEventBean();
				de.setStudyEvent(event);
				de.setDisplayEventCRFs(getDisplayEventCRFs(eventCRFs, eventDefinitionCRFs, request));

				request.setAttribute("displayEvent", de);

				forwardPage(Page.RESTORE_STUDY_EVENT, request, response);
			} else if ("submit".equalsIgnoreCase(action)) {
				logger.info("submit to restore the event to study");

				// restore event to study
				event.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
				event.setStatus(Status.AVAILABLE);
				event.setUpdater(currentUser);
				event.setUpdatedDate(new Date());
				sedao.update(event);

				EventCRFDAO ecdao = getEventCRFDAO();
				List<EventCRFBean> eventCRFs = (ArrayList<EventCRFBean>) ecdao.findAllByStudyEvent(event);
				boolean hasStarted = false;

				getEventCRFService().restoreEventCRFsFromAutoRemovedState(eventCRFs, currentUser);

				for (EventCRFBean eventCRF : eventCRFs) {
					hasStarted = !hasStarted ? !eventCRF.isNotStarted() : hasStarted;
				}

				if (!hasStarted) {
					event.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
					sedao.update(event);
				}

				String emailBody = new StringBuilder("").append(respage.getString("the_event"))
						.append(event.getStudyEventDefinition().getName()).append(" ")
						.append(respage.getString("has_been_restored_to_the_study")).append(" ")
						.append(study.getName()).append(".").toString();

				addPageMessage(emailBody, request);
				request.setAttribute("id", Integer.toString(studySubId));
				forwardToViewStudySubjectPage(request, response);
			}
		}
	}

	private void forwardToViewStudySubjectPage(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		storePageMessages(request);
		String id = (String) request.getAttribute("id");
		String savedUrl = (String) request.getSession().getAttribute(
				ViewStudySubjectServlet.SAVED_VIEW_STUDY_SUBJECT_URL);
		if (savedUrl != null && savedUrl.contains("id=" + id)) {
			response.sendRedirect(savedUrl);
		} else {
			response.sendRedirect(request.getContextPath() + Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName() + "?id="
					+ id);
		}
	}

	/**
	 * Each of the event CRFs with its corresponding CRFBean. Then generates a list of DisplayEventCRFBeans, one for
	 * each event CRF.
	 * 
	 * @param eventCRFs
	 *            The list of event CRFs for this study event.
	 * @param eventDefinitionCRFs
	 *            The list of event definition CRFs for this study event.
	 * @return The list of DisplayEventCRFBeans for this study event.
	 */
	private ArrayList getDisplayEventCRFs(ArrayList eventCRFs, ArrayList eventDefinitionCRFs, HttpServletRequest request) {
		ArrayList answer = new ArrayList();

		HashMap definitionsById = new HashMap();
		int i;
		for (i = 0; i < eventDefinitionCRFs.size(); i++) {
			EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
			definitionsById.put(edc.getStudyEventDefinitionId(), edc);
		}

		StudyEventDAO sedao = getStudyEventDAO();
		CRFDAO cdao = getCRFDAO();
		CRFVersionDAO cvdao = getCRFVersionDAO();

		for (i = 0; i < eventCRFs.size(); i++) {
			EventCRFBean ecb = (EventCRFBean) eventCRFs.get(i);

			// populate the event CRF with its crf bean
			int crfVersionId = ecb.getCRFVersionId();
			CRFBean cb = cdao.findByVersionId(crfVersionId);
			ecb.setCrf(cb);

			CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
			ecb.setCrfVersion(cvb);

			int studyEventId = ecb.getStudyEventId();
			int studyEventDefinitionId = sedao.getDefinitionIdFromStudyEventId(studyEventId);

			EventDefinitionCRFBean edc = (EventDefinitionCRFBean) definitionsById.get(new Integer(
					studyEventDefinitionId));

			DisplayEventCRFBean dec = new DisplayEventCRFBean();
			dec.setFlags(ecb, getUserAccountBean(request), getCurrentRole(request), edc);
			answer.add(dec);
		}

		return answer;
	}

}
