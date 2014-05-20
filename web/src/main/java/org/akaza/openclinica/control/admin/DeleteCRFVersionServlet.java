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
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.admin.NewCRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class DeleteCRFVersionServlet extends Controller {

	private static final String CRF_VERSION_ID_PARAMETER = "verId";

	private static final String ACTION_PARAMETER = "action";

	private static final String CONFIRM_PAGE_PASSED_PARAMETER = "confirmPagePassed";

	private static final String VERSION_TO_DELETE = "version";

	private static final String ACTION_CONFIRM = "confirm";

	private static final String ACTION_SUBMIT = "submit";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (ub.isSysAdmin()) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, "not admin", "1");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int versionId = fp.getInt(CRF_VERSION_ID_PARAMETER, true);
		String action = fp.getString(ACTION_PARAMETER);
		String keyValue = (String) request.getSession().getAttribute("savedListCRFsUrl");

		CRFVersionDAO cvdao = new CRFVersionDAO(getDataSource());
		CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(versionId);
		boolean canDelete;

		if (version.getCrfId() != 0 && !StringUtil.isBlank(action)) {

			EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(getDataSource());
			StudyEventDefinitionDAO sedDao = new StudyEventDefinitionDAO(getDataSource());

			// find definitions using this version
			ArrayList definitions = edcdao.findByDefaultVersion(version.getId());
			for (Object edcBean : definitions) {
				StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) sedDao
						.findByPK(((EventDefinitionCRFBean) edcBean).getStudyEventDefinitionId());
				((EventDefinitionCRFBean) edcBean).setEventName(sedBean.getName());
			}

			// find event crfs using this version
			EventCRFDAO ecdao = new EventCRFDAO(getDataSource());
			ArrayList eventCRFs = ecdao.findAllByCRFVersion(versionId);

			canDelete = true;

			if (!definitions.isEmpty()) {// used in definition
				canDelete = false;
				request.setAttribute("definitions", definitions);
				addPageMessage(
						respage.getString("this_CRF_version") + " " + version.getName() + " "
								+ respage.getString("has_associated_study_events_definitions_cannot_delete"), request);

			} else if (!eventCRFs.isEmpty()) {
				canDelete = false;
				request.setAttribute("eventsForVersion", eventCRFs);
				addPageMessage(
						respage.getString("this_CRF_version") + " " + version.getName() + " "
								+ respage.getString("has_associated_study_events_cannot_delete"), request);
			}

			if (ACTION_CONFIRM.equalsIgnoreCase(action) || (ACTION_SUBMIT.equalsIgnoreCase(action) && !canDelete)) {
				request.setAttribute(VERSION_TO_DELETE, version);
				forwardPage(Page.DELETE_CRF_VERSION, request, response);
				return;
			} else if (ACTION_SUBMIT.equalsIgnoreCase(action)
					&& !fp.getString(CONFIRM_PAGE_PASSED_PARAMETER).equals(FormProcessor.DEFAULT_STRING)) {
				ArrayList items = cvdao.findNotSharedItemsByVersion(versionId);
				NewCRFBean nib = new NewCRFBean(getDataSource(), version.getCrfId());
				nib.setDeleteQueries(cvdao.generateDeleteQueries(versionId, items));
				nib.deleteFromDB();

				// Purge coded items
				getCodedItemService().deleteByCRFVersion(versionId);

				addPageMessage(respage.getString("the_CRF_version_has_been_deleted_succesfully"), request);

			} else {
				addPageMessage(respage.getString("invalid_http_request_parameters"), request);
			}
		} else {
			addPageMessage(respage.getString("invalid_http_request_parameters"), request);
		}

		if (keyValue != null) {
			Map storedAttributes = new HashMap();
			storedAttributes.put(Controller.PAGE_MESSAGE, request.getAttribute(Controller.PAGE_MESSAGE));
			request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
			response.sendRedirect(response.encodeRedirectURL(keyValue));
		} else {
			forwardPage(Page.CRF_LIST_SERVLET, request, response);
		}

	}

}
