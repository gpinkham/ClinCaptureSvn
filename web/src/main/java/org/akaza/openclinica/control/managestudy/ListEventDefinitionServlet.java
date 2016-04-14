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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clinovo.util.EventDefinitionCRFUtil;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.StudyEventDefinitionRow;
import org.springframework.stereotype.Component;

/**
 * Processes user request to generate study event definition list.
 */
@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
@Component
public class ListEventDefinitionServlet extends SpringServlet {

	/**
	 * Checks whether the user has the correct privilege.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		request.getSession().removeAttribute("tmpCRFIdMap");
		request.getSession().removeAttribute("crfsWithVersion");
		request.getSession().removeAttribute("eventDefinitionCRFs");

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_study_director"), "1");
	}

	/**
	 * Processes the request.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CRFDAO crfDao = getCRFDAO();
		StudyEventDAO sedao = getStudyEventDAO();
		CRFVersionDAO crfVersionDao = getCRFVersionDAO();
		StudyBean currentStudy = getCurrentStudy(request);
		EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
		StudyEventDefinitionDAO edao = getStudyEventDefinitionDAO();
		ArrayList studyEventDefinitions = edao.findAllByStudy(currentStudy);
		EventDefinitionCRFUtil.resetAddedEvents(request.getSession());
		for (Object sed1 : studyEventDefinitions) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) sed1;
			Collection eventDefinitionCRFlist = edcdao.findAllParentsByDefinition(sed.getId());
			Map crfWithDefaultVersion = new LinkedHashMap();
			for (Object anEventDefinitionCRFlist : eventDefinitionCRFlist) {
				// FIXME can this be reduced to a non - N^2 loop?
				EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean) anEventDefinitionCRFlist;
				if (edcBean.getStatus() != Status.AUTO_DELETED && edcBean.getStatus() != Status.DELETED) {
					CRFBean crfBean = (CRFBean) crfDao.findByPK(edcBean.getCrfId());
					CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(edcBean.getDefaultVersionId());
					logger.info("ED[" + sed.getName() + "]crf[" + crfBean.getName() + "]dv[" + crfVersionBean.getName() + "]");
					crfWithDefaultVersion.put(crfBean.getName(), crfVersionBean.getName());
				}
			}
			sed.setCrfsWithDefaultVersion(crfWithDefaultVersion);
			logger.info("CRF size [" + sed.getCrfs().size() + "]");
			if (sed.getUpdater().getId() == 0) {
				sed.setUpdater(sed.getOwner());
				sed.setUpdatedDate(sed.getCreatedDate());
			}
			if (isPopulated(sed, sedao)) {
				sed.setPopulated(true);
			}
		}

		EntityBeanTable table = getEntityBeanTable();
		ArrayList allStudyRows = StudyEventDefinitionRow.generateRowsFromBeans(studyEventDefinitions);

		String[] columns = { getResWord().getString("order"), getResWord().getString("name"), getResWord().getString("OID"),
				getResWord().getString("repeating"), getResWord().getString("type"), getResWord().getString("category"),
				getResWord().getString("populated"), getResWord().getString("date_created"), getResWord().getString("date_updated"),
				getResWord().getString("CRFs"), getResWord().getString("default_version"), getResWord().getString("actions")};
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		int index = 2;
		table.hideColumnLink(index++);
		table.hideColumnLink(index++);
		table.hideColumnLink(index++);
		index++;
		table.hideColumnLink(index++);
		table.hideColumnLink(index++);
		table.hideColumnLink(index++);
		table.hideColumnLink(index++);
		table.hideColumnLink(index++); // crfs, tbh
		table.hideColumnLink(index++);
		table.hideColumnLink(index);
		table.setQuery("ListEventDefinition", new HashMap());

		table.setRows(allStudyRows);

		table.setPaginated(false);
		table.computeDisplay();

		request.setAttribute("isAnyCalendaredEventExist", edao.isAnyCalendaredEventExist(currentStudy.getId()));
		request.setAttribute("table", table);
		request.setAttribute("defSize", studyEventDefinitions.size());

		if (request.getParameter("read") != null && request.getParameter("read").equals("true")) {
			request.setAttribute("readOnly", true);
		}

		forwardPage(Page.STUDY_EVENT_DEFINITION_LIST, request, response);
	}

	/**
	 * Checked whether a definition is available to be locked.
	 * 
	 * @param sed
	 *            StudyEventDefinitionBean
	 * @param sedao
	 *            StudyEventDAO
	 * @return boolean
	 */
	private boolean isPopulated(StudyEventDefinitionBean sed, StudyEventDAO sedao) {
		return sedao.countNotRemovedEvents(sed.getId()) > 0;
	}

}
