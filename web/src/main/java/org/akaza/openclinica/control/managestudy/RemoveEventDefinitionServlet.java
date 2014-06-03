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

import com.clinovo.model.CodedItem;
import com.clinovo.service.CodedItemService;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.Controller;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
@Component
public class RemoveEventDefinitionServlet extends Controller {
	/**
     *
     */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.LIST_DEFINITION_SERVLET, respage.getString("current_study_locked"), request, response);
		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
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

		int defId = Integer.parseInt(idString.trim());
		StudyEventDefinitionDAO sdao = getStudyEventDefinitionDAO();
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) sdao.findByPK(defId);

		// checkRoleByUserAndStudy(ub.getName(), sed.getStudyId(), 0);
		if (currentStudy.getId() != sed.getStudyId()) {
			addPageMessage(
					respage.getString("no_have_correct_privilege_current_study") + " "
							+ respage.getString("change_active_study_or_contact"), request);
			forwardPage(Page.MENU_SERVLET, request, response);
			return;
		}

		// find all CRFs
		EventDefinitionCRFDAO edao = getEventDefinitionCRFDAO();
		ArrayList<EventDefinitionCRFBean> eventDefinitionCRFs = (ArrayList) edao.findAllByDefinition(defId);

		CRFVersionDAO cvdao = getCRFVersionDAO();
		CRFDAO cdao = getCRFDAO();
		for (EventDefinitionCRFBean edc : eventDefinitionCRFs) {
			ArrayList versions = (ArrayList) cvdao.findAllByCRF(edc.getCrfId());
			edc.setVersions(versions);
			CRFBean crf = (CRFBean) cdao.findByPK(edc.getCrfId());
			edc.setCrfName(crf.getName());
			CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edc.getDefaultVersionId());
			edc.setDefaultVersionName(defaultVersion.getName());
		}

		// finds all events
		StudyEventDAO sedao = new StudyEventDAO(getDataSource());
		ArrayList<StudyEventBean> events = (ArrayList<StudyEventBean>) sedao.findAllByDefinition(sed.getId());

		String action = request.getParameter("action");
		if (StringUtil.isBlank(idString)) {
			addPageMessage(respage.getString("please_choose_a_SED_to_remove"), request);
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

				request.setAttribute("definitionToRemove", sed);
				request.setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
				request.setAttribute("events", events);
				forwardPage(Page.REMOVE_DEFINITION, request, response);
			} else {
				logger.info("submit to remove the definition");
				// remove definition
				sed.setStatus(Status.DELETED);
				sed.setUpdater(ub);
				sed.setUpdatedDate(new Date());
				sdao.update(sed);

				// remove all crfs
				for (EventDefinitionCRFBean edc : eventDefinitionCRFs) {
					if (!edc.getStatus().equals(Status.DELETED)) {
						edc.setStatus(Status.AUTO_DELETED);
						edc.setUpdater(ub);
						edc.setUpdatedDate(new Date());
						edao.update(edc);
					}
				}
				// remove all events

				EventCRFDAO ecdao = getEventCRFDAO();
				CodedItemService codedItemsService = getCodedItemService();

				for (StudyEventBean event : events) {
					if (!event.getStatus().equals(Status.DELETED)) {
						event.setStatus(Status.AUTO_DELETED);
						event.setUpdater(ub);
						event.setUpdatedDate(new Date());
						sedao.update(event);

						ArrayList<EventCRFBean> eventCRFs = ecdao.findAllByStudyEvent(event);
						// remove all the item data
						ItemDataDAO iddao = getItemDataDAO();
						for (EventCRFBean eventCRF : eventCRFs) {
							if (!eventCRF.getStatus().equals(Status.DELETED)) {
								eventCRF.setStatus(Status.AUTO_DELETED);
								eventCRF.setUpdater(ub);
								eventCRF.setUpdatedDate(new Date());
								ecdao.update(eventCRF);

								ArrayList<ItemDataBean> itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
								for (ItemDataBean item : itemDatas) {
									CodedItem codedItem = codedItemsService.findCodedItem(item.getId());
									if (!item.getStatus().equals(Status.DELETED)) {
										item.setStatus(Status.AUTO_DELETED);
										item.setUpdater(ub);
										item.setUpdatedDate(new Date());
										iddao.update(item);

										if (codedItem != null) {
											codedItem.setStatus(com.clinovo.model.Status.CodeStatus.REMOVED.toString());
											codedItemsService.saveCodedItem(codedItem);
										}
									}
								}
							}
						}
					}
				}
				String emailBody = respage.getString("the_SED") + sed.getName() + " "
						+ respage.getString("has_been_removed_from_the_study") + currentStudy.getName() + ".";

				addPageMessage(emailBody, request);
				forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
			}
		}
	}
}
