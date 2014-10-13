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

import com.clinovo.model.CodedItem;
import com.clinovo.service.CodedItemService;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
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
import org.akaza.openclinica.control.core.Controller;
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
import org.akaza.openclinica.navigation.HelpNavigationServlet;
import org.akaza.openclinica.util.DAOWrapper;
import org.akaza.openclinica.util.SubjectEventStatusUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * DeleteEventCRFServlet class.
 */
@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class DeleteEventCRFServlet extends Controller {

	public static final String STUDY_SUB_ID = "ssId";
	public static final String EVENT_CRF_ID = "ecId";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (ub.isSysAdmin()) {
			return;
		}
		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS, resexception.getString("not_admin"), "1");

	}

	private void smartForward(Page page, HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.sendRedirect(HelpNavigationServlet.getSavedUrl(request));
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		FormProcessor fp = new FormProcessor(request);

		int studySubId = fp.getInt(STUDY_SUB_ID, true);
		int eventCRFId = fp.getInt(EVENT_CRF_ID);

		String action = request.getParameter("action");

		StudyEventDAO sedao = getStudyEventDAO();
		StudySubjectDAO subdao = getStudySubjectDAO();
		EventCRFDAO ecdao = getEventCRFDAO();
		StudyDAO sdao = getStudyDAO();

		if (eventCRFId == 0) {
			addPageMessage(respage.getString("please_choose_an_event_CRF_to_delete"), request);
			request.setAttribute("id", Integer.toString(studySubId));
			smartForward(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
		} else {
			EventCRFBean eventCRF = (EventCRFBean) ecdao.findByPK(eventCRFId);

			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
			request.setAttribute("studySub", studySub);

			// construct info needed on view event crf page
			CRFDAO cdao = getCRFDAO();
			CRFVersionDAO cvdao = getCRFVersionDAO();

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
			StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(studyEventDefinitionId);
			event.setStudyEventDefinition(sed);
			request.setAttribute("event", event);

			EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();

			StudyBean study = (StudyBean) sdao.findByPK(studySub.getStudyId());
			EventDefinitionCRFBean edc = edcdao.findByStudyEventDefinitionIdAndCRFId(study, studyEventDefinitionId,
					cb.getId());

			DisplayEventCRFBean dec = new DisplayEventCRFBean();
			dec.setEventCRF(eventCRF);
			dec.setFlags(eventCRF, ub, currentRole, edc);

			// find all item data
			ItemDataDAO iddao = getItemDataDAO();
			DiscrepancyNoteDAO dnDao = getDiscrepancyNoteDAO();
			ArrayList itemData = iddao.findAllByEventCRFId(eventCRF.getId());
			request.setAttribute("items", itemData);

			if ("confirm".equalsIgnoreCase(action)) {

				request.setAttribute("displayEventCRF", dec);

				forwardPage(Page.DELETE_EVENT_CRF, request, response);
			} else {
				logger.info("submit to delete the event CRF from event");

				CodedItemService codedItemsService = getCodedItemService();

				for (Object anItemData : itemData) {

					ItemDataBean item = (ItemDataBean) anItemData;
					CodedItem codedItem = codedItemsService.findCodedItem(item.getId());
					ArrayList discrepancyList = dnDao.findExistingNotesForItemData(item.getId());

					iddao.deleteDnMap(item.getId());

					for (Object aDiscrepancyList : discrepancyList) {
						DiscrepancyNoteBean noteBean = (DiscrepancyNoteBean) aDiscrepancyList;
						dnDao.deleteNotes(noteBean.getId());
					}

					item.setUpdater(ub);
					iddao.updateUser(item);
					iddao.delete(item.getId());

					if (codedItem != null) {
						codedItemsService.deleteCodedItem(codedItem);
					}
				}
				ecdao.deleteEventCRFDNMap(eventCRF.getId());
				// update user id before deleting
				eventCRF.setUpdater(ub);
				ecdao.update(eventCRF);
				// delete
				ecdao.delete(eventCRF.getId());

				SubjectEventStatusUtil.determineSubjectEventState(event, new DAOWrapper(sdao, cvdao, sedao, subdao,
						ecdao, edcdao, dnDao));
				event = (StudyEventBean) sedao.update(event);

				String emailBody = respage.getString("the_event_CRF") + cb.getName()
						+ respage.getString("has_been_deleted_from_the_event")
						+ event.getStudyEventDefinition().getName() + ".";

				addPageMessage(emailBody, request);
				// sendEmail(emailBody);
				request.setAttribute("id", Integer.toString(studySubId));
				smartForward(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
			}

		}
	}
}
