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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * Removes a crf version
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class RemoveCRFVersionServlet extends SecureController {
	
	private static final String CRF_VERSION_ID_PARAMETER = "id";

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
		int versionId = fp.getInt(CRF_VERSION_ID_PARAMETER, true);
		String module = fp.getString(MODULE);
		String action = fp.getString(ACTION_PARAMETER);
		String keyValue = (String) request.getSession().getAttribute("savedListCRFsUrl");

		CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
		CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(versionId);
		ArrayList eventCRFs;
		EventCRFDAO evdao;
		SectionDAO secdao;

		request.setAttribute(MODULE, module);
		
		if (version.getId() != 0 && !StringUtil.isBlank(action)
				&& (MODULE_ADMIN.equalsIgnoreCase(module) || MODULE_MANAGE.equalsIgnoreCase(module))) {

			evdao = new EventCRFDAO(sm.getDataSource());
			// find all event crfs by version id
			eventCRFs = evdao.findUndeletedWithStudySubjectsByCRFVersion(versionId);

			if (ACTION_CONFIRM.equalsIgnoreCase(action)) {
				if (!ub.isSysAdmin() && (version.getOwnerId() != ub.getId())) {
					addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
							+ respage.getString("change_active_study_or_contact"));
					forwardPage(Page.MENU_SERVLET);
				}

				request.setAttribute("versionToRemove", version);
				request.setAttribute("eventCRFs", eventCRFs);
				forwardPage(Page.REMOVE_CRF_VERSION);
			} else if (ACTION_SUBMIT.equalsIgnoreCase(action)
					&& !fp.getString(CONFIRM_PAGE_PASSED_PARAMETER).equals(FormProcessor.DEFAULT_STRING)) {
				
				logger.info("submit to remove the crf version");
				// version
				version.setStatus(Status.DELETED);
				version.setUpdater(ub);
				version.setUpdatedDate(new Date());
				cvdao.update(version);
				// added below tbh 092007, seems that we don't remove the event
				// crfs in the second pass
				for (int ii = 0; ii < eventCRFs.size(); ii++) {
					EventCRFBean ecbean = (EventCRFBean) eventCRFs.get(ii);
					ecbean.setStatus(Status.AUTO_DELETED);
					ecbean.setUpdater(ub);
					ecbean.setUpdatedDate(new Date());
					evdao.update(ecbean);
				}
				// added above tbh 092007, to fix task
				// all sections
				secdao = new SectionDAO(sm.getDataSource());
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

				// all item data related to event crfs
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

				ArrayList versionList = (ArrayList) cvdao.findAllByCRF(version.getCrfId());
				if (versionList.size() > 0) {
					EventDefinitionCRFDAO edCRFDao = new EventDefinitionCRFDAO(sm.getDataSource());
					ArrayList edcList = (ArrayList) edCRFDao.findAllByCRF(version.getCrfId());
					for (int i = 0; i < edcList.size(); i++) {
						EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean) edcList.get(i);
						updateEventDef(edcBean, edCRFDao, versionList);
					}
				}

				// Remove coded items
				getCodedItemService().removeByCRFVersion(versionId);
				
				addPageMessage(respage.getString("the_CRF") + version.getName() + " "
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
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	public static void updateEventDef(EventDefinitionCRFBean edcBean, EventDefinitionCRFDAO edcDao,
			ArrayList versionList) {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		CRFVersionBean temp = (CRFVersionBean) versionList.get(0);
		if (StringUtil.isBlank(edcBean.getSelectedVersionIds())) {
			edcBean.setDefaultVersionId(temp.getId());
			edcDao.update(edcBean);
		} else {
			String sversionIds = edcBean.getSelectedVersionIds();
			String[] ids = sversionIds.split("\\,");
			for (String id : ids) {
				idList.add(Integer.valueOf(id));
			}
			for (int i = 0; i < versionList.size(); i++) {
				CRFVersionBean versionBean = (CRFVersionBean) versionList.get(i);
				if (idList.contains(versionBean.getId())) {
					edcBean.setDefaultVersionId(versionBean.getId());
					edcDao.update(edcBean);
					break;
				}
			}
		}
	}

	// @pgawade 18-May-2011 #5414 - Ovrloaded the method updateEventDef for an
	// additional parameter of crf version being locked.
	// These are changes for setting the correct default crf version Id to event
	// when existing default version is locked
	public static void updateEventDef(EventDefinitionCRFBean edcBean, EventDefinitionCRFDAO edcDao,
			ArrayList versionList, int crfVIdToLock) {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		CRFVersionBean temp = null;
		if ((null != versionList) && (versionList.size() > 0)) {
			temp = (CRFVersionBean) versionList.get(0);
		}
		// Check the first version in list if it is getting locked
		// here. If not, make that as default version. Otherwise get the next
		// element in list and make that as the default version.

		if (StringUtil.isBlank(edcBean.getSelectedVersionIds())) {
			if ((null != temp) && (temp.getId() == crfVIdToLock) && (null != versionList) && (versionList.size() > 1)) {
				CRFVersionBean temp2 = (CRFVersionBean) versionList.get(1);
				edcBean.setDefaultVersionId(temp2.getId());
			} else {
				edcBean.setDefaultVersionId(temp.getId());
			}
			edcDao.update(edcBean);
		} else {
			String sversionIds = edcBean.getSelectedVersionIds();
			String[] ids = sversionIds.split("\\,");
			for (String id : ids) {
				idList.add(Integer.valueOf(id));
			}
			for (int i = 0; i < versionList.size(); i++) {
				CRFVersionBean versionBean = (CRFVersionBean) versionList.get(i);
				if (idList.contains(versionBean.getId())) {
					edcBean.setDefaultVersionId(versionBean.getId());
					edcDao.update(edcBean);
					break;
				}
			}
		}
	}
}
