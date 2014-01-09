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

import java.util.ArrayList;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Locks a study event definition
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class LockEventDefinitionServlet extends Controller {
	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
				resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		String idString = request.getParameter("id");

		int defId = Integer.valueOf(idString.trim());
		StudyEventDefinitionDAO sdao = getStudyEventDefinitionDAO();
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) sdao.findByPK(defId);
		// find all CRFs
		EventDefinitionCRFDAO edao = getEventDefinitionCRFDAO();
		ArrayList eventDefinitionCRFs = (ArrayList) edao.findAllByDefinition(defId);

		CRFVersionDAO cvdao = getCRFVersionDAO();
		CRFDAO cdao = getCRFDAO();
		for (Object eventDefinitionCRF : eventDefinitionCRFs) {
			EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRF;
			ArrayList versions = (ArrayList) cvdao.findAllByCRF(edc.getCrfId());
			edc.setVersions(versions);
			CRFBean crf = (CRFBean) cdao.findByPK(edc.getCrfId());
			edc.setCrfName(crf.getName());
		}

		// finds all events
		StudyEventDAO sedao = getStudyEventDAO();
		ArrayList events = (ArrayList) sedao.findAllByDefinition(sed.getId());

		String action = request.getParameter("action");
		if (StringUtil.isBlank(idString)) {
			addPageMessage(respage.getString("please_choose_a_SED_to_lock"), request);
			forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				if (!sed.getStatus().equals(Status.AVAILABLE)) {
					addPageMessage(
							respage.getString("this_SED_is_not_available_for_this_study")
									+ respage.getString("please_contact_sysadmin_for_more_information"), request);
					forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
					return;
				}

				request.setAttribute("definitionToLock", sed);
				request.setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
				request.setAttribute("events", events);
				forwardPage(Page.LOCK_DEFINITION, request, response);
			} else {
				logger.info("submit to lock the definition");
				// lock definition
				sed.setStatus(Status.LOCKED);
				sed.setUpdater(ub);
				sed.setUpdatedDate(new Date());
				sdao.update(sed);

				// lock all crfs
				for (Object eventDefinitionCRF : eventDefinitionCRFs) {
					EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRF;
					edc.setStatus(Status.LOCKED);
					edc.setUpdater(ub);
					edc.setUpdatedDate(new Date());
					edao.update(edc);
				}
				// lock all events

				EventCRFDAO ecdao = getEventCRFDAO();

				for (Object event1 : events) {
					StudyEventBean event = (StudyEventBean) event1;
					event.setStatus(Status.LOCKED);
					event.setUpdater(ub);
					event.setUpdatedDate(new Date());
					sedao.update(event);

					ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);
					// remove all the item data
					ItemDataDAO iddao = getItemDataDAO();
					for (Object eventCRF1 : eventCRFs) {
						EventCRFBean eventCRF = (EventCRFBean) eventCRF1;
						eventCRF.setStatus(Status.LOCKED);
						eventCRF.setUpdater(ub);
						eventCRF.setUpdatedDate(new Date());
						ecdao.update(eventCRF);

						ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
						for (Object itemData : itemDatas) {
							ItemDataBean item = (ItemDataBean) itemData;
							item.setStatus(Status.LOCKED);
							item.setUpdater(ub);
							item.setUpdatedDate(new Date());
							iddao.update(item);
						}
					}
				}

				String emailBody = respage.getString("the_SED") + sed.getName()
						+ respage.getString("has_been_locked_for_the_study") + currentStudy.getName()
						+ respage.getString("no_new_data_may_entered_for_this_SED");

				addPageMessage(emailBody, request);
				sendEmail(emailBody, request);
				forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
			}

		}

	}

	/**
	 * Send email to director and administrator
	 * 
	 * @param emailBody
	 *            String
	 * @param request
	 *            HttpServletRequest
	 */
	private void sendEmail(String emailBody, HttpServletRequest request) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		logger.info("Sending email...");
		// to study director
		sendEmail(ub.getEmail().trim(), respage.getString("lock_SED"), emailBody, false, request);
		// to admin
		sendEmail(EmailEngine.getAdminEmail(), respage.getString("lock_SED"), emailBody, false, request);
		logger.info("Sending email done..");
	}

}
