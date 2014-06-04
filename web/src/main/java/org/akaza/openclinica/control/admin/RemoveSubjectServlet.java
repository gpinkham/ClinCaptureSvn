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

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.Controller;

import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class RemoveSubjectServlet extends Controller {
	/**
     *
     */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);

		if (ub.isSysAdmin()) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.SUBJECT_LIST_SERVLET, resexception.getString("not_admin"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		SubjectDAO sdao = new SubjectDAO(getDataSource());
		FormProcessor fp = new FormProcessor(request);
		int subjectId = fp.getInt("id");

		String action = fp.getString("action");
		if (subjectId == 0 || StringUtil.isBlank(action)) {
			addPageMessage(respage.getString("please_choose_a_subject_to_remove"), request);
			forwardPage(Page.SUBJECT_LIST_SERVLET, request, response);
		} else {

			SubjectBean subject = (SubjectBean) sdao.findByPK(subjectId);

			// find all study subjects
			StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
			ArrayList studySubs = ssdao.findAllBySubjectId(subjectId);

			// find study events
			StudyEventDAO sedao = new StudyEventDAO(getDataSource());
			ArrayList events = sedao.findAllBySubjectId(subjectId);
			if ("confirm".equalsIgnoreCase(action)) {
				request.setAttribute("subjectToRemove", subject);
				request.setAttribute("studySubs", studySubs);
				request.setAttribute("events", events);
				forwardPage(Page.REMOVE_SUBJECT, request, response);
			} else {
				logger.info("submit to remove the subject");
				// change all statuses to deleted
				subject.setStatus(Status.DELETED);
				subject.setUpdater(ub);
				subject.setUpdatedDate(new Date());
				sdao.update(subject);

				// remove subject references from study
				for (Object studySub1 : studySubs) {
					StudySubjectBean studySub = (StudySubjectBean) studySub1;
					if (!studySub.getStatus().equals(Status.DELETED)) {
						studySub.setStatus(Status.AUTO_DELETED);
						studySub.setUpdater(ub);
						studySub.setUpdatedDate(new Date());
						ssdao.update(studySub);
					}
				}

				EventCRFDAO ecdao = new EventCRFDAO(getDataSource());

				for (Object event1 : events) {
					StudyEventBean event = (StudyEventBean) event1;
					if (!event.getStatus().equals(Status.DELETED)) {
						event.setStatus(Status.AUTO_DELETED);
						event.setSubjectEventStatus(SubjectEventStatus.REMOVED);
						event.setUpdater(ub);
						event.setUpdatedDate(new Date());
						sedao.update(event);

						ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

						ItemDataDAO iddao = new ItemDataDAO(getDataSource());
						for (Object eventCRF1 : eventCRFs) {
							EventCRFBean eventCRF = (EventCRFBean) eventCRF1;
							if (!eventCRF.getStatus().equals(Status.DELETED)) {
								eventCRF.setStatus(Status.AUTO_DELETED);
								eventCRF.setUpdater(ub);
								eventCRF.setUpdatedDate(new Date());
								ecdao.update(eventCRF);
								// remove all the item data
								ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
								for (Object itemData : itemDatas) {
									ItemDataBean item = (ItemDataBean) itemData;
									if (!item.getStatus().equals(Status.DELETED)) {
										item.setStatus(Status.AUTO_DELETED);
										item.setUpdater(ub);
										item.setUpdatedDate(new Date());
										iddao.update(item);
									}
								}
							}
						}
					}
				}

				String emailBody = respage.getString("the_subject") + " " + subject.getUniqueIdentifier() + " "
						+ respage.getString("has_been_removed_succesfully");

				addPageMessage(emailBody, request);
				// sendEmail(emailBody);

				forwardPage(Page.SUBJECT_LIST_SERVLET, request, response);

			}
		}

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}

}
