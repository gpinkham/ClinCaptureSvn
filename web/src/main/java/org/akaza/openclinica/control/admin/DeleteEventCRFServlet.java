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
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
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
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.util.DAOWrapper;
import org.akaza.openclinica.util.SubjectEventStatusUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;

@SuppressWarnings({ "rawtypes", "serial" })
public class DeleteEventCRFServlet extends SecureController {

	public static final String DELETE_EVENT_CRF_REFERER = "deleteEventCRFReferer";
	public static final String REFERER = "referer";
	public static final String DELETE_EVENT_CRF = "DeleteEventCRF";
	public static final String RESTORE_EVENT_CRF = "RestoreEventCRF";
	public static String STUDY_SUB_ID = "ssId";

	public static String EVENT_CRF_ID = "ecId";

	/**
     * 
     */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}
		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS, resexception.getString("not_admin"), "1");

	}

	private void smartForward(Page page) throws Exception {
		String url = (String) request.getSession().getAttribute(DELETE_EVENT_CRF_REFERER);
		request.getSession().removeAttribute(DELETE_EVENT_CRF_REFERER);
		if (url != null) {
			response.sendRedirect(url);
		} else {
			forwardPage(page);
		}
	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);

		String referer = request.getHeader(REFERER);
		if (referer != null && !referer.contains(DELETE_EVENT_CRF) && !referer.contains(RESTORE_EVENT_CRF)) {
			request.getSession().setAttribute(DELETE_EVENT_CRF_REFERER, request.getHeader(REFERER));
			logger.debug("=== set referer " + referer);
		}

		int studySubId = fp.getInt(STUDY_SUB_ID, true);
		int eventCRFId = fp.getInt(EVENT_CRF_ID);

		String action = request.getParameter("action");

		StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
		StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
		EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
		StudyDAO sdao = new StudyDAO(sm.getDataSource());

		if (eventCRFId == 0) {
			addPageMessage(respage.getString("please_choose_an_event_CRF_to_delete"));
			request.setAttribute("id", new Integer(studySubId).toString());
			smartForward(Page.VIEW_STUDY_SUBJECT_SERVLET);
		} else {
			EventCRFBean eventCRF = (EventCRFBean) ecdao.findByPK(eventCRFId);

			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
			request.setAttribute("studySub", studySub);

			// construct info needed on view event crf page
			CRFDAO cdao = new CRFDAO(sm.getDataSource());
			CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());

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
			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(studyEventDefinitionId);
			event.setStudyEventDefinition(sed);
			request.setAttribute("event", event);

			EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());

			StudyBean study = (StudyBean) sdao.findByPK(studySub.getStudyId());
			EventDefinitionCRFBean edc = edcdao.findByStudyEventDefinitionIdAndCRFId(study, studyEventDefinitionId,
					cb.getId());

			DisplayEventCRFBean dec = new DisplayEventCRFBean();
			dec.setEventCRF(eventCRF);
			dec.setFlags(eventCRF, ub, currentRole, edc.isDoubleEntry());

			// find all item data
			ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
			DiscrepancyNoteDAO dnDao = new DiscrepancyNoteDAO(sm.getDataSource());
			ArrayList itemData = iddao.findAllByEventCRFId(eventCRF.getId());
			request.setAttribute("items", itemData);

			if ("confirm".equalsIgnoreCase(action)) {

				request.setAttribute("displayEventCRF", dec);

				forwardPage(Page.DELETE_EVENT_CRF);
			} else {
				logger.info("submit to delete the event CRF from event");
				// delete all the item data first
				for (int a = 0; a < itemData.size(); a++) {
					ItemDataBean item = (ItemDataBean) itemData.get(a);
					ArrayList discrepancyList = dnDao.findExistingNotesForItemData(item.getId());
					iddao.deleteDnMap(item.getId());
					for (int b = 0; b < discrepancyList.size(); b++) {
						DiscrepancyNoteBean noteBean = (DiscrepancyNoteBean) discrepancyList.get(b);
						dnDao.deleteNotes(noteBean.getId());
					}
					item.setUpdater(ub);
					iddao.updateUser(item);
					iddao.delete(item.getId());
				}
				// delete event crf
				ecdao.delete(eventCRF.getId());

				SubjectEventStatusUtil.determineSubjectEventState(event, study, new DAOWrapper(sdao, sedao, subdao,
						ecdao, edcdao, dnDao));
				event = (StudyEventBean) sedao.update(event);

				String emailBody = respage.getString("the_event_CRF") + cb.getName()
						+ respage.getString("has_been_deleted_from_the_event")
						+ event.getStudyEventDefinition().getName() + ".";

				addPageMessage(emailBody);
				// sendEmail(emailBody);
				request.setAttribute("id", new Integer(studySubId).toString());
				smartForward(Page.VIEW_STUDY_SUBJECT_SERVLET);
			}

		}
	}
}
