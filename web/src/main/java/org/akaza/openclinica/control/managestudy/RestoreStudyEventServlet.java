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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * @author jxu
 * 
 *         Restores a removed study event and all its data
 */
@SuppressWarnings({"unchecked", "rawtypes", "serial"})
public class RestoreStudyEventServlet extends SecureController {
	
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"));
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"));

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int studyEventId = fp.getInt("id");// studyEventId
		int studySubId = fp.getInt("studySubId");// studySubjectId

		StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
		StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());

		if (studyEventId == 0) {
			addPageMessage(respage.getString("please_choose_a_SE_to_restore"));
			request.setAttribute("id", new Integer(studySubId).toString());
			forwardToViewStudySubjectPage();
		} else {

			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);

			// A study event could not be restored if its study
			// subject has been removed
			Status s = studySub.getStatus();
			if ("removed".equalsIgnoreCase(s.getName()) || "auto-removed".equalsIgnoreCase(s.getName())) {
				addPageMessage(resword.getString("study_event") + resterm.getString("could_not_be")
						+ resterm.getString("restored") + "." + respage.getString("study_subject_has_been_deleted"));
				request.setAttribute("id", new Integer(studySubId).toString());
				forwardToViewStudySubjectPage();
			}
			// YW

			StudyEventBean event = (StudyEventBean) sedao.findByPK(studyEventId);

			request.setAttribute("studySub", studySub);

			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
					.findByPK(event.getStudyEventDefinitionId());
			event.setStudyEventDefinition(sed);

			StudyDAO studydao = new StudyDAO(sm.getDataSource());
			StudyBean study = (StudyBean) studydao.findByPK(studySub.getStudyId());
			request.setAttribute("study", study);

			String action = request.getParameter("action");
			if ("confirm".equalsIgnoreCase(action)) {
				if (event.getStatus().equals(Status.AVAILABLE)) {
					addPageMessage(respage.getString("this_event_is_already_available_for_study") + " "
							+ respage.getString("please_contact_sysadmin_for_more_information"));
					request.setAttribute("id", new Integer(studySubId).toString());
					forwardToViewStudySubjectPage();
					return;
				}

				EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
				// find all crfs in the definition
				ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllByEventDefinitionId(study, sed.getId());

				EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
				ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

				// construct info needed on view study event page
				DisplayStudyEventBean de = new DisplayStudyEventBean();
				de.setStudyEvent(event);
				de.setDisplayEventCRFs(getDisplayEventCRFs(eventCRFs, eventDefinitionCRFs));

				request.setAttribute("displayEvent", de);

				forwardPage(Page.RESTORE_STUDY_EVENT);
			} else {
				logger.info("submit to restore the event to study");
				// restore event to study
				event.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
				event.setStatus(Status.AVAILABLE);
				event.setUpdater(ub);
				event.setUpdatedDate(new Date());
				sedao.update(event);

				// restore event crfs
				EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

				ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

				boolean hasStarted = false;
				ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
				for (int k = 0; k < eventCRFs.size(); k++) {
					EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(k);
					if (eventCRF.getStatus().equals(Status.AUTO_DELETED)) {
						eventCRF.setStatus(Status.AVAILABLE);
						eventCRF.setUpdater(ub);
						eventCRF.setUpdatedDate(new Date());
						ecdao.update(eventCRF);
						// remove all the item data
						ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
						for (int a = 0; a < itemDatas.size(); a++) {
							ItemDataBean item = (ItemDataBean) itemDatas.get(a);
							if (item.getStatus().equals(Status.AUTO_DELETED)) {
								item.setStatus(Status.AVAILABLE);
								item.setUpdater(ub);
								item.setUpdatedDate(new Date());
								iddao.update(item);
							}
						}
					}
					hasStarted = !hasStarted ? !eventCRF.isNotStarted() : hasStarted;
				}

				if (!hasStarted) {
					event.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
					sedao.update(event);
				}

				String emailBody = respage.getString("the_event") + event.getStudyEventDefinition().getName() + " "
						+ respage.getString("has_been_restored_to_the_study") + " " + study.getName() + ".";

				addPageMessage(emailBody);
				// sendEmail(emailBody);
				request.setAttribute("id", new Integer(studySubId).toString());
				forwardToViewStudySubjectPage();
			}
		}
	}

	private void forwardToViewStudySubjectPage() throws Exception {
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
	private ArrayList getDisplayEventCRFs(ArrayList eventCRFs, ArrayList eventDefinitionCRFs) {
		ArrayList answer = new ArrayList();

		HashMap definitionsById = new HashMap();
		int i;
		for (i = 0; i < eventDefinitionCRFs.size(); i++) {
			EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
			definitionsById.put(new Integer(edc.getStudyEventDefinitionId()), edc);
		}

		StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
		CRFDAO cdao = new CRFDAO(sm.getDataSource());
		CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());

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
			dec.setFlags(ecb, ub, currentRole, edc.isDoubleEntry());
			answer.add(dec);
		}

		return answer;
	}

}
