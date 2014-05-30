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
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Removes a crf
 * 
 * @author jxu
 */
@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class RemoveCRFServlet extends Controller {
	/**
     *
     */

	private static final String CRF_ID_PARAMETER = "id";

	private static final String ACTION_PARAMETER = "action";

	private static final String CONFIRM_PAGE_PASSED_PARAMETER = "confirmPagePassed";

	private static final String ACTION_CONFIRM = "confirm";

	private static final String ACTION_SUBMIT = "submit";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, resexception.getString("not_admin"), "1");

	}

	@SuppressWarnings("unchecked")
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		FormProcessor fp = new FormProcessor(request);
		int crfId = fp.getInt(CRF_ID_PARAMETER, true);
		String action = fp.getString(ACTION_PARAMETER);
		String keyValue = (String) request.getSession().getAttribute("savedListCRFsUrl");

		CRFVersionDAO cvdao;
		ArrayList<CRFVersionBean> versions;
		ArrayList edcs;
		ArrayList<EventCRFBean> eventCRFs;
		SectionDAO secdao;
		EventCRFDAO evdao;
		EventDefinitionCRFDAO edcdao;
		StudyEventDAO seDao;
		StudyEventDefinitionDAO sedDao;
		CRFDAO cdao = getCRFDAO();
		CRFBean crf = (CRFBean) cdao.findByPK(crfId);

		if (crf.getId() != 0 && !StringUtil.isBlank(action)) {

			cvdao = getCRFVersionDAO();
			versions = cvdao.findAllByCRFId(crfId);
			crf.setVersions(versions);
			evdao = getEventCRFDAO();
			eventCRFs = evdao.findAllByCRF(crfId);
			seDao = getStudyEventDAO();
			sedDao = getStudyEventDefinitionDAO();
			for (Object ecBean : eventCRFs) {
				StudyEventBean seBean = (StudyEventBean) seDao.findByPK(((EventCRFBean) ecBean).getStudyEventId());
				StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) sedDao.findByPK(seBean
						.getStudyEventDefinitionId());
				((EventCRFBean) ecBean).setEventName(sedBean.getName());
			}

			if (ACTION_CONFIRM.equalsIgnoreCase(action)) {

				if (!ub.isSysAdmin() && (crf.getOwnerId() != ub.getId())) {
					addPageMessage(
							respage.getString("no_have_correct_privilege_current_study") + " "
									+ respage.getString("change_active_study_or_contact"), request);
					forwardPage(Page.MENU_SERVLET, request, response);
					return;
				}

				request.setAttribute("crfToRemove", crf);
				request.setAttribute("eventCRFs", eventCRFs);
				forwardPage(Page.REMOVE_CRF, request, response);
				return;

			} else if (ACTION_SUBMIT.equalsIgnoreCase(action)
					&& !fp.getString(CONFIRM_PAGE_PASSED_PARAMETER).equals(FormProcessor.DEFAULT_STRING)) {

				logger.info("submit to remove the crf");
				crf.setStatus(Status.DELETED);
				crf.setUpdater(ub);
				crf.setUpdatedDate(new Date());
				cdao.update(crf);

				secdao = getSectionDAO();
				for (CRFVersionBean version : versions) {
					if (!version.getStatus().equals(Status.DELETED)) {
						version.setStatus(Status.AUTO_DELETED);
						version.setUpdater(ub);
						version.setUpdatedDate(new Date());
						cvdao.update(version);

						ArrayList<SectionBean> sections = secdao.findAllByCRFVersionId(version.getId());
						for (SectionBean section : sections) {
							if (!section.getStatus().equals(Status.DELETED)) {
								section.setStatus(Status.AUTO_DELETED);
								section.setUpdater(ub);
								section.setUpdatedDate(new Date());
								secdao.update(section);
							}
						}

						// Remove coded items
						getCodedItemService().removeByCRFVersion(version.getId());
					}
				}

				edcdao = new EventDefinitionCRFDAO(getDataSource());
				edcs = (ArrayList) edcdao.findAllByCRF(crfId);
				for (Object edc1 : edcs) {
					EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edc1;
					if (!edc.getStatus().equals(Status.DELETED)) {
						edc.setStatus(Status.AUTO_DELETED);
						edc.setUpdater(ub);
						edc.setUpdatedDate(new Date());
						edcdao.update(edc);
					}
				}

				ItemDataDAO idao = getItemDataDAO();
				CodedItemService codedItemsService = getCodedItemService();

				for (EventCRFBean eventCRF : eventCRFs) {
					if (!eventCRF.getStatus().equals(Status.DELETED)) {
						eventCRF.setStatus(Status.AUTO_DELETED);
						eventCRF.setUpdater(ub);
						eventCRF.setUpdatedDate(new Date());
						evdao.update(eventCRF);

						ArrayList<ItemDataBean> items = idao.findAllByEventCRFId(eventCRF.getId());
						for (ItemDataBean item : items) {
							CodedItem codedItem = codedItemsService.findCodedItem(item.getId());
							if (!item.getStatus().equals(Status.DELETED)) {
								item.setStatus(Status.AUTO_DELETED);
								item.setUpdater(ub);
								item.setUpdatedDate(new Date());
								idao.update(item);
							}
							if (codedItem != null) {
								codedItem.setStatus(com.clinovo.model.Status.CodeStatus.REMOVED.toString());
								codedItemsService.saveCodedItem(codedItem);
							}
						}
					}
				}

				addPageMessage(
						respage.getString("the_CRF") + crf.getName() + " "
								+ respage.getString("has_been_removed_succesfully"), request);

			} else {
				addPageMessage(respage.getString("invalid_http_request_parameters"), request);
			}
		} else {
			addPageMessage(respage.getString("invalid_http_request_parameters"), request);
		}

		if (keyValue != null) {
			Map storedAttributes = new HashMap();
			storedAttributes.put(PAGE_MESSAGE, request.getAttribute(PAGE_MESSAGE));
			request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
			response.sendRedirect(response.encodeRedirectURL(keyValue));
		} else {
			forwardPage(Page.CRF_LIST_SERVLET, request, response);
		}

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}

}
