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
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
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

@SuppressWarnings({ "serial", "rawtypes" })
public class RestoreCRFServlet extends SecureController {
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
		// checks which module the requests are from
		String module = fp.getString(MODULE);
		int crfId = fp.getInt(CRF_ID_PARAMETER, true);
		String action = fp.getString(ACTION_PARAMETER);
		String keyValue = (String) request.getSession().getAttribute("savedListCRFsUrl");

		CRFDAO cdao = new CRFDAO(sm.getDataSource());
		CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
		CRFBean crf = (CRFBean) cdao.findByPK(crfId);
		ArrayList versions;
		ArrayList edcs;
		ArrayList eventCRFs;
		SectionDAO secdao;
		EventDefinitionCRFDAO edcdao;
		EventCRFDAO evdao;

		request.setAttribute(MODULE, module);
		
		if (crf.getId() != 0 && !StringUtil.isBlank(action)
				&& (MODULE_ADMIN.equalsIgnoreCase(module) || MODULE_MANAGE.equalsIgnoreCase(module))) {

			versions = cvdao.findAllByCRFId(crfId);
			crf.setVersions(versions);
			evdao = new EventCRFDAO(sm.getDataSource());
			eventCRFs = evdao.findAllByCRF(crfId);

			if (ACTION_CONFIRM.equalsIgnoreCase(action)) {

				if (!ub.isSysAdmin() && (crf.getOwnerId() != ub.getId())) {
					addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
							+ respage.getString("change_active_study_or_contact"));
					forwardPage(Page.MENU_SERVLET);
				}

				request.setAttribute("crfToRestore", crf);
				request.setAttribute("eventCRFs", eventCRFs);
				forwardPage(Page.RESTORE_CRF);
			} else if (ACTION_SUBMIT.equalsIgnoreCase(action)
					&& !fp.getString(CONFIRM_PAGE_PASSED_PARAMETER).equals(FormProcessor.DEFAULT_STRING)) {

				logger.info("submit to restore the crf");
				crf.setStatus(Status.AVAILABLE);
				crf.setUpdater(ub);
				crf.setUpdatedDate(new Date());
				cdao.update(crf);

				secdao = new SectionDAO(sm.getDataSource());
				for (int i = 0; i < versions.size(); i++) {
					CRFVersionBean version = (CRFVersionBean) versions.get(i);
					if (version.getStatus().equals(Status.AUTO_DELETED)) {
						version.setStatus(Status.AVAILABLE);
						version.setUpdater(ub);
						version.setUpdatedDate(new Date());
						cvdao.update(version);

						ArrayList sections = secdao.findAllByCRFVersionId(version.getId());
						for (int j = 0; j < sections.size(); j++) {
							SectionBean section = (SectionBean) sections.get(j);
							if (section.getStatus().equals(Status.AUTO_DELETED)) {
								section.setStatus(Status.AVAILABLE);
								section.setUpdater(ub);
								section.setUpdatedDate(new Date());
								secdao.update(section);
							}
						}
						
						// Restore coded items
						getCodedItemService().restoreByCRFVersion(version.getId());
					}
				}

				edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
				edcs = (ArrayList) edcdao.findAllByCRF(crfId);
				for (int i = 0; i < edcs.size(); i++) {
					EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcs.get(i);
					if (edc.getStatus().equals(Status.AUTO_DELETED)) {
						edc.setStatus(Status.AVAILABLE);
						edc.setUpdater(ub);
						edc.setUpdatedDate(new Date());
						edcdao.update(edc);
					}
				}

				ItemDataDAO idao = new ItemDataDAO(sm.getDataSource());
				for (int i = 0; i < eventCRFs.size(); i++) {
					EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(i);
					if (eventCRF.getStatus().equals(Status.AUTO_DELETED)) {
						eventCRF.setStatus(Status.AVAILABLE);
						eventCRF.setUpdater(ub);
						eventCRF.setUpdatedDate(new Date());
						evdao.update(eventCRF);

						ArrayList items = idao.findAllByEventCRFId(eventCRF.getId());
						for (int j = 0; j < items.size(); j++) {
							ItemDataBean item = (ItemDataBean) items.get(j);
							if (item.getStatus().equals(Status.AUTO_DELETED)) {
								item.setStatus(Status.AVAILABLE);
								item.setUpdater(ub);
								item.setUpdatedDate(new Date());
								idao.update(item);
							}
						}
					}
				}

				addPageMessage(respage.getString("the_CRF") + crf.getName() + " "
						+ respage.getString("has_been_restored_succesfully"));
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
