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

/**
 * Created by IntelliJ IDEA.
 * User: bads
 * Date: Mar 24, 2010
 * Time: 8:33:54 PM
 * To change this template use File | Settings | File Templates.
 */
import java.util.Date;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

@SuppressWarnings({ "rawtypes", "serial" })
public class DeleteStudyEventServlet extends SecureController {
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"));
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"));

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
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
			addPageMessage(respage.getString("please_choose_a_SE_to_remove"));
			request.setAttribute("id", new Integer(studySubId).toString());
			forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
		} else {

			StudyEventBean event = (StudyEventBean) sedao.findByPK(studyEventId);

			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
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

				// construct info needed on view study event page
				DisplayStudyEventBean de = new DisplayStudyEventBean();
				de.setStudyEvent(event);

				request.setAttribute("displayEvent", de);

				forwardPage(Page.DELETE_STUDY_EVENT);
			} else {
				logger.info("submit to delete the event from study");
				// delete event from study

				event.setSubjectEventStatus(SubjectEventStatus.NOT_SCHEDULED);
				event.setUpdater(ub);
				event.setUpdatedDate(new Date());
				sedao.update(event);
				String emailBody = respage.getString("the_event") + " " + event.getStudyEventDefinition().getName()
						+ " " + respage.getString("has_been_removed_from_the_subject_record_for") + " "
						+ studySub.getLabel() + " " + respage.getString("in_the_study") + " " + study.getName() + ".";

				addPageMessage(emailBody);
				request.setAttribute("id", new Integer(studySubId).toString());
				forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
			}
		}
	}
}
