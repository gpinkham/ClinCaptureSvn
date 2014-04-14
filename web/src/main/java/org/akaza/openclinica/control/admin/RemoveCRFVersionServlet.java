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

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.Controller;
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
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Removes a crf version
 * 
 * @author jxu
 * 
 */
@Component
@SuppressWarnings({ "rawtypes", "serial" })
public class RemoveCRFVersionServlet extends Controller {

	private static final String CRF_VERSION_ID_PARAMETER = "id";

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
		int versionId = fp.getInt(CRF_VERSION_ID_PARAMETER, true);
		String action = fp.getString(ACTION_PARAMETER);
		String keyValue = (String) request.getSession().getAttribute("savedListCRFsUrl");

		CRFVersionDAO cvdao = getCRFVersionDAO();
		CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(versionId);
		List<EventCRFBean> eventCRFs;
		EventCRFDAO evdao;
		SectionDAO secdao;

		if (version.getId() != 0 && !StringUtil.isBlank(action)) {

			evdao = getEventCRFDAO();
			// find all event crfs by version id
			eventCRFs = evdao.findAllByCRFVersion(versionId);

			if (ACTION_CONFIRM.equalsIgnoreCase(action)) {
				if (!ub.isSysAdmin() && (version.getOwnerId() != ub.getId())) {
					addPageMessage(
							respage.getString("no_have_correct_privilege_current_study") + " "
									+ respage.getString("change_active_study_or_contact"), request);
					forwardPage(Page.MENU_SERVLET, request, response);
					return;
				}

				request.setAttribute("versionToRemove", version);
				request.setAttribute("eventCRFs", eventCRFs);
				forwardPage(Page.REMOVE_CRF_VERSION, request, response);
				return;
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
				for (EventCRFBean ecbean : eventCRFs) {
					ecbean.setStatus(Status.AUTO_DELETED);
					ecbean.setUpdater(ub);
					ecbean.setUpdatedDate(new Date());
					evdao.update(ecbean);
				}
				// added above tbh 092007, to fix task
				// all sections
				secdao = getSectionDAO();
				List<SectionBean> sections = secdao.findAllByCRFVersionId(version.getId());
				for (SectionBean section : sections) {
					if (!section.getStatus().equals(Status.DELETED)) {
						section.setStatus(Status.AUTO_DELETED);
						section.setUpdater(ub);
						section.setUpdatedDate(new Date());
						secdao.update(section);
					}
				}

				// all item data related to event crfs
				ItemDataDAO idao = getItemDataDAO();
				for (EventCRFBean eventCRF : eventCRFs) {
					if (!eventCRF.getStatus().equals(Status.DELETED)) {
						eventCRF.setStatus(Status.AUTO_DELETED);
						eventCRF.setUpdater(ub);
						eventCRF.setUpdatedDate(new Date());
						evdao.update(eventCRF);

						List<ItemDataBean> items = idao.findAllByEventCRFId(eventCRF.getId());
						for (ItemDataBean item : items) {
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
					EventDefinitionCRFDAO edCRFDao = getEventDefinitionCRFDAO();
					List<EventDefinitionCRFBean> edcList = (ArrayList) edCRFDao.findAllByCRF(version.getCrfId());
					for (EventDefinitionCRFBean edcBean : edcList) {
						updateEventDef(edcBean, edCRFDao, versionList);
					}
				}

				// Remove coded items
				getCodedItemService().removeByCRFVersion(versionId);

				addPageMessage(
						respage.getString("the_CRF") + version.getName() + " "
								+ respage.getString("has_been_removed_succesfully"), request);

			} else {
				addPageMessage(respage.getString("invalid_http_request_parameters"), request);
			}
		} else {
			addPageMessage(respage.getString("invalid_http_request_parameters"), request);
		}

		if (keyValue != null) {
			Map storedAttributes = new HashMap();
			storedAttributes.put(SecureController.PAGE_MESSAGE, request.getAttribute(SecureController.PAGE_MESSAGE));
			request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
			response.sendRedirect(response.encodeRedirectURL(keyValue));
		} else {
			forwardPage(Page.CRF_LIST_SERVLET, request, response);
		}

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	public static void updateEventDef(EventDefinitionCRFBean edcBean, EventDefinitionCRFDAO edcDao,
			List<CRFVersionBean> versionList) {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		CRFVersionBean temp = versionList.get(0);
		if (StringUtil.isBlank(edcBean.getSelectedVersionIds())) {
			edcBean.setDefaultVersionId(temp.getId());
			edcDao.update(edcBean);
		} else {
			String sversionIds = edcBean.getSelectedVersionIds();
			String[] ids = sversionIds.split("\\,");
			for (String id : ids) {
				idList.add(Integer.valueOf(id));
			}
			for (CRFVersionBean versionBean : versionList) {
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
			List<CRFVersionBean> versionList, int crfVIdToLock) {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		CRFVersionBean temp = null;
		if ((null != versionList) && (versionList.size() > 0)) {
			temp = versionList.get(0);
		}
		// Check the first version in list if it is getting locked
		// here. If not, make that as default version. Otherwise get the next
		// element in list and make that as the default version.

		if (StringUtil.isBlank(edcBean.getSelectedVersionIds())) {
			if ((null != temp) && (temp.getId() == crfVIdToLock) && (versionList.size() > 1)) {
				CRFVersionBean temp2 = versionList.get(1);
				edcBean.setDefaultVersionId(temp2.getId());
			} else {
				if (temp != null) {
					edcBean.setDefaultVersionId(temp.getId());
				}
			}
			edcDao.update(edcBean);
		} else {
			String sversionIds = edcBean.getSelectedVersionIds();
			String[] ids = sversionIds.split("\\,");
			for (String id : ids) {
				idList.add(Integer.valueOf(id));
			}
			if (versionList != null) {
				for (CRFVersionBean versionBean : versionList) {
					if (idList.contains(versionBean.getId())) {
						edcBean.setDefaultVersionId(versionBean.getId());
						edcDao.update(edcBean);
						break;
					}
				}
			}
		}
	}
}
