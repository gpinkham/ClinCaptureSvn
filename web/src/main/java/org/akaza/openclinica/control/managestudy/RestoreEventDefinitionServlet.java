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

/**
 * Restores a removed study event definition and all its related data
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
@Component
public class RestoreEventDefinitionServlet extends Controller {
	/**
     *
     */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.LIST_DEFINITION_SERVLET, respage.getString("current_study_locked"), request, response);
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
		StudyEventDefinitionDAO sdao = new StudyEventDefinitionDAO(getDataSource());
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) sdao.findByPK(defId);
		// find all CRFs
		EventDefinitionCRFDAO edao = new EventDefinitionCRFDAO(getDataSource());
		ArrayList<EventDefinitionCRFBean> eventDefinitionCRFs = (ArrayList<EventDefinitionCRFBean>) edao
				.findAllByDefinition(defId);

		CRFVersionDAO cvdao = new CRFVersionDAO(getDataSource());
		CRFDAO cdao = new CRFDAO(getDataSource());
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
			addPageMessage(respage.getString("please_choose_a_SED_to_restore"), request);
			forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				if (!sed.getStatus().equals(Status.DELETED)) {
					addPageMessage(
							respage.getString("this_SED_cannot_be_restored") + " "
									+ respage.getString("please_contact_sysadmin_for_more_information"), request);
					forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
					return;
				}

				request.setAttribute("definitionToRestore", sed);
				request.setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
				request.setAttribute("events", events);
				forwardPage(Page.RESTORE_DEFINITION, request, response);
			} else {
				logger.info("submit to restore the definition");
				// restore definition
				sed.setStatus(Status.AVAILABLE);
				sed.setUpdater(ub);
				sed.setUpdatedDate(new Date());
				sdao.update(sed);

				// restore all crfs
				for (EventDefinitionCRFBean edc : eventDefinitionCRFs) {
					if (edc.getStatus().equals(Status.AUTO_DELETED)) {
						edc.setStatus(Status.AVAILABLE);
						edc.setUpdater(ub);
						edc.setUpdatedDate(new Date());
						edao.update(edc);
					}
				}
				// restore all events

				EventCRFDAO ecdao = getEventCRFDAO();
				CodedItemService codedItemService = getCodedItemService();

				for (StudyEventBean event : events) {
					if (event.getStatus().equals(Status.AUTO_DELETED)) {
						event.setStatus(Status.AVAILABLE);
						event.setUpdater(ub);
						event.setUpdatedDate(new Date());
						sedao.update(event);

						ArrayList<EventCRFBean> eventCRFs = ecdao.findAllByStudyEvent(event);
						// remove all the item data
						ItemDataDAO iddao = getItemDataDAO();
						for (EventCRFBean eventCRF : eventCRFs) {
							if (eventCRF.getStatus().equals(Status.AUTO_DELETED)) {
								eventCRF.setStatus(Status.AVAILABLE);
								eventCRF.setUpdater(ub);
								eventCRF.setUpdatedDate(new Date());
								ecdao.update(eventCRF);

								ArrayList<ItemDataBean> itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
								for (ItemDataBean item : itemDatas) {
									if (item.getStatus().equals(Status.AUTO_DELETED)) {
										item.setStatus(Status.AVAILABLE);
										item.setUpdater(ub);
										item.setUpdatedDate(new Date());
										iddao.update(item);
									}

									CodedItem codedItem = codedItemService.findCodedItem(item.getId());
									if (codedItem != null) {
										if (codedItem.getHttpPath() == null || codedItem.getHttpPath().isEmpty()) {
											codedItem.setStatus(com.clinovo.model.Status.CodeStatus.NOT_CODED
													.toString());
										} else {
											codedItem.setStatus(com.clinovo.model.Status.CodeStatus.CODED.toString());
										}

										codedItemService.saveCodedItem(codedItem);
									}
								}
							}
						}
					}
				}
				String emailBody = respage.getString("the_SED") + " " + sed.getName() + "("
						+ respage.getString("and_all_associated_event_data_restored_to_study") + currentStudy.getName()
						+ ".";

				addPageMessage(emailBody, request);

				// sendEmail(emailBody);
				forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
			}

		}

	}

}
