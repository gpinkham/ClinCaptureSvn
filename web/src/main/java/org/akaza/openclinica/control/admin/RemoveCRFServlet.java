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
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.SecureController;
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
public class RemoveCRFServlet extends SecureController {
	/**
     *
     */
	
	private static final String CRF_ID_PARAMETER = "id";
	
	private static final String ACTION_PARAMETER = "action";
	
	private static final String CONFIRM_PAGE_PASSED_PARAMETER = "confirmPagePassed";
	
	private static final String MODULE_ADMIN = "admin";
	
	private static final String MODULE_MANAGE = "manage";
	
	private static final String ACTION_CONFIRM = "confirm";
	
	private static final String ACTION_SUBMIT = "submit";
	
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, resexception.getString("not_admin"), "1");

	}

	@SuppressWarnings("unchecked")
	@Override
	public void processRequest() throws Exception {

		FormProcessor fp = new FormProcessor(request);
		int crfId = fp.getInt(CRF_ID_PARAMETER, true);
		String module = fp.getString(MODULE);
		String action = fp.getString(ACTION_PARAMETER);
		String keyValue = (String) request.getSession().getAttribute("savedListCRFsUrl");

		CRFVersionDAO cvdao;
		ArrayList versions;
		ArrayList edcs;
		ArrayList eventCRFs;
		SectionDAO secdao;
		EventCRFDAO evdao;
		EventDefinitionCRFDAO edcdao;
		StudyEventDAO seDao;
		StudyEventDefinitionDAO sedDao;
		CRFDAO cdao = new CRFDAO(sm.getDataSource());
		CRFBean crf = (CRFBean) cdao.findByPK(crfId);

		request.setAttribute(MODULE, module);
		
		if (crf.getId() != 0 && !StringUtil.isBlank(action) 
				&& (MODULE_ADMIN.equalsIgnoreCase(module) || MODULE_MANAGE.equalsIgnoreCase(module))) {

			cvdao = new CRFVersionDAO(sm.getDataSource());
			versions = cvdao.findAllByCRFId(crfId);
			crf.setVersions(versions);
			evdao = new EventCRFDAO(sm.getDataSource());
			eventCRFs = evdao.findAllByCRF(crfId);
			seDao = new StudyEventDAO(sm.getDataSource());
			sedDao = new StudyEventDefinitionDAO(sm.getDataSource());
			for (Object ecBean : eventCRFs) {
				StudyEventBean seBean 
						= (StudyEventBean) seDao.findByPK(((EventCRFBean) ecBean).getStudyEventId());
				StudyEventDefinitionBean sedBean 
						= (StudyEventDefinitionBean) sedDao.findByPK(seBean.getStudyEventDefinitionId());
				((EventCRFBean) ecBean).setEventName(sedBean.getName());
			}

			if (ACTION_CONFIRM.equalsIgnoreCase(action)) {

				if (!ub.isSysAdmin() && (crf.getOwnerId() != ub.getId())) {
					addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
							+ respage.getString("change_active_study_or_contact"));
					forwardPage(Page.MENU_SERVLET);
				}

				request.setAttribute("crfToRemove", crf);
				request.setAttribute("eventCRFs", eventCRFs);
				forwardPage(Page.REMOVE_CRF);

			} else if (ACTION_SUBMIT.equalsIgnoreCase(action)
					&& !fp.getString(CONFIRM_PAGE_PASSED_PARAMETER).equals(FormProcessor.DEFAULT_STRING)) {
				
				logger.info("submit to remove the crf");
				crf.setStatus(Status.DELETED);
				crf.setUpdater(ub);
				crf.setUpdatedDate(new Date());
				cdao.update(crf);

				secdao = new SectionDAO(sm.getDataSource());
				for (int i = 0; i < versions.size(); i++) {
					CRFVersionBean version = (CRFVersionBean) versions.get(i);
					if (!version.getStatus().equals(Status.DELETED)) {
						version.setStatus(Status.AUTO_DELETED);
						version.setUpdater(ub);
						version.setUpdatedDate(new Date());
						cvdao.update(version);

						ArrayList sections = secdao.findAllByCRFVersionId(version.getId());
						for (int j = 0; j < sections.size(); j++) {
							SectionBean section = (SectionBean) sections.get(j);
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

				edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
				edcs = (ArrayList) edcdao.findAllByCRF(crfId);
				for (int i = 0; i < edcs.size(); i++) {
					EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcs.get(i);
					if (!edc.getStatus().equals(Status.DELETED)) {
						edc.setStatus(Status.AUTO_DELETED);
						edc.setUpdater(ub);
						edc.setUpdatedDate(new Date());
						edcdao.update(edc);
					}
				}

				ItemDataDAO idao = new ItemDataDAO(sm.getDataSource());
				for (int i = 0; i < eventCRFs.size(); i++) {
					EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(i);
					if (!eventCRF.getStatus().equals(Status.DELETED)) {
						eventCRF.setStatus(Status.AUTO_DELETED);
						eventCRF.setUpdater(ub);
						eventCRF.setUpdatedDate(new Date());
						evdao.update(eventCRF);

						ArrayList items = idao.findAllByEventCRFId(eventCRF.getId());
						for (int j = 0; j < items.size(); j++) {
							ItemDataBean item = (ItemDataBean) items.get(j);
							if (!item.getStatus().equals(Status.DELETED)) {
								item.setStatus(Status.AUTO_DELETED);
								item.setUpdater(ub);
								item.setUpdatedDate(new Date());
								idao.update(item);
							}
						}
					}
				}

				addPageMessage(respage.getString("the_CRF") + crf.getName() + " "
						+ respage.getString("has_been_removed_succesfully"));

			} else {
				addPageMessage(respage.getString("invalid_http_request_parameters"));
			}
		} else {
			addPageMessage(respage.getString("invalid_http_request_parameters"));
		}

		if (keyValue != null) {
			Map storedAttributes = new HashMap();
			storedAttributes.put(SecureController.PAGE_MESSAGE, request.getAttribute(SecureController.PAGE_MESSAGE));
			request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
			response.sendRedirect(response.encodeRedirectURL(keyValue));
		} else {
			forwardPage(Page.CRF_LIST_SERVLET);
		}

	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}

}
