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

package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class DeleteStudyEventServlet extends Controller {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"), request, response);

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		FormProcessor fp = new FormProcessor(request);
		int studyEventId = fp.getInt("id");// studyEventId
		int studySubId = fp.getInt("studySubId");// studySubjectId

		if (request.getAttribute("deletedDurringUpdateStudyEvent")!=null){
			studyEventId = fp.getInt("event_id", true);
			studySubId = fp.getInt("id", true);
		}

		StudyEventDAO sedao = new StudyEventDAO(getDataSource());
		StudySubjectDAO subdao = new StudySubjectDAO(getDataSource());

		if (studyEventId == 0) {
			addPageMessage(respage.getString("please_choose_a_SE_to_remove"), request);
			request.setAttribute("id", Integer.toString(studySubId));
			forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
		} else {
			ItemDataDAO iddao = getItemDataDAO();
			EventCRFDAO ecdao = getEventCRFDAO();
			StudyEventBean event = (StudyEventBean) sedao.findByPK(studyEventId);

			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
			request.setAttribute("studySub", studySub);

			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
					.findByPK(event.getStudyEventDefinitionId());
			event.setStudyEventDefinition(sed);

			String action = request.getParameter("action");
			if ("confirm".equalsIgnoreCase(action)) {

				// construct info needed on view study event page
				DisplayStudyEventBean de = new DisplayStudyEventBean();
				de.setStudyEvent(event);

				request.setAttribute("displayEvent", de);

				forwardPage(Page.DELETE_STUDY_EVENT, request, response);
			} else {
				logger.info("submit to delete the event from study");
				// delete event from study

				DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());

				for (Object eventCRFObject : ecdao.findAllByStudyEvent(event)) {
					EventCRFBean eventCRF = (EventCRFBean) eventCRFObject;
					ArrayList itemData = iddao.findAllByEventCRFId(eventCRF.getId());
					for (Object anItemData : itemData) {
						ItemDataBean item = (ItemDataBean) anItemData;
						ArrayList discrepancyList = dndao.findExistingNotesForItemData(item.getId());
						iddao.deleteDnMap(item.getId());
						for (Object aDiscrepancyList : discrepancyList) {
							DiscrepancyNoteBean noteBean = (DiscrepancyNoteBean) aDiscrepancyList;
							dndao.deleteNotes(noteBean.getId());
						}
						item.setUpdater(ub);
						iddao.updateUser(item);
						iddao.delete(item.getId());
					}
					ecdao.deleteEventCRFDNMap(eventCRF.getId());
					ecdao.delete(eventCRF.getId());
				}

				List<Integer> dnIdList = dndao.findAllDnIdsByStudyEvent(event.getId());
				sedao.deleteStudyEventDNMap(event.getId());
				for (Integer dnId : dnIdList) {
					dndao.deleteNotes(dnId);
				}

				sedao.deleteByPK(event.getId());

				if (request.getAttribute("deletedDurringUpdateStudyEvent")!=null){
					forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
				} else {
					response.sendRedirect(request.getContextPath() + Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName() + "?id="
							+ studySubId);
				}
			}
		}
	}
}
