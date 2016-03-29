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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.clinovo.util.EventDefinitionCRFUtil;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.CRFRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.springframework.stereotype.Component;

/**
 * Processes request to add new CRFs info study event definition.
 * 
 * @author jxu
 */
@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class AddCRFToDefinitionServlet extends SpringServlet {

	public static final int FIVE = 5;

	/**
	 * Checks whether the user has the correct privilege.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws InsufficientPermissionException
	 *             the InsufficientPermissionException
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_permission_to_update_study_event_definition")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String actionName = request.getParameter("actionName");
		String submit = request.getParameter("Submit");

		CRFDAO cdao = getCRFDAO();
		ArrayList eventDefinitionCRFs = getEventDefinitionCRFsFromRequest(request);
		ArrayList<EventDefinitionCRFBean> crfs = (ArrayList) cdao.findAllActiveCrfs();
		HashMap crfIds = new HashMap();
		for (Object edc1 : eventDefinitionCRFs) {
			EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edc1;
			Integer crfId = edc.getCrfId();
			crfIds.put(crfId, edc);
		}
		for (Object crf1 : crfs) {
			CRFBean crf = (CRFBean) crf1;
			if (crfIds.containsKey(new Integer(crf.getId()))) {
				crf.setSelected(true);
			}
		}
		request.getSession().setAttribute("crfsWithVersion", crfs);
		if (!StringUtil.isBlank(submit)) {
			addCRF(request, response);
		} else {
			if (StringUtil.isBlank(actionName)) {
				request.setAttribute("table", createTable(request, crfs));
				forwardPage(Page.UPDATE_EVENT_DEFINITION2, request, response);
			} else if (actionName.equalsIgnoreCase("next")) {
				confirmDefinition(request, response);
			}
		}
	}

	private ArrayList<EventDefinitionCRFBean> getEventDefinitionCRFsFromRequest(HttpServletRequest request) {
		ArrayList eventCRFs = (ArrayList) request.getSession().getAttribute("eventDefinitionCRFs");
		if (eventCRFs == null) {
			eventCRFs = new ArrayList<EventDefinitionCRFBean>();
		}
		return eventCRFs;
	}

	private void confirmDefinition(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);

		Map tmpCRFIdMap = (HashMap) request.getSession().getAttribute("tmpCRFIdMap");
		if (tmpCRFIdMap == null) {
			tmpCRFIdMap = new HashMap();
		}
		ArrayList crfsWithVersion = (ArrayList) request.getSession().getAttribute("crfsWithVersion");
		for (int i = 0; i < crfsWithVersion.size(); i++) {
			int id = fp.getInt("id" + i);
			String name = fp.getString("name" + i);
			String selected = fp.getString("selected" + i);
			if (!StringUtil.isBlank(selected) && "yes".equalsIgnoreCase(selected.trim())) {
				tmpCRFIdMap.put(id, name);
			} else {
				if (tmpCRFIdMap.containsKey(id)) {
					tmpCRFIdMap.remove(id);
				}
			}
		}
		request.getSession().setAttribute("tmpCRFIdMap", tmpCRFIdMap);

		request.setAttribute("table", createTable(request, crfsWithVersion));
		String queryString = fp.getRequest().getQueryString();
		if (queryString != null) {
			String filterKeyword = fp.getRequest().getParameter("ebl_filterKeyword");
			fp.getRequest()
					.getSession()
					.setAttribute(
							DefineStudyEventServlet.DEFINE_UPDATE_STUDY_EVENT_PAGE_2_URL,
							queryString.concat(filterKeyword != null ? "&ebl_filterKeyword=".concat(filterKeyword) : ""));
		}
		forwardPage(Page.UPDATE_EVENT_DEFINITION2, request, response);
	}

	private void addCRF(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		FormProcessor fp = new FormProcessor(request);
		CRFVersionDAO vdao = getCRFVersionDAO();
		ArrayList crfArray = new ArrayList();
		HttpSession session = request.getSession();
		Map tmpCRFIdMap = (HashMap) session.getAttribute("tmpCRFIdMap");
		if (tmpCRFIdMap == null) {
			tmpCRFIdMap = new HashMap();
		}
		ArrayList crfsWithVersion = (ArrayList) session.getAttribute("crfsWithVersion");
		for (int i = 0; i < crfsWithVersion.size(); i++) {
			int id = fp.getInt("id" + i);
			String name = fp.getString("name" + i);
			String selected = fp.getString("selected" + i);
			if (!StringUtil.isBlank(selected) && "yes".equalsIgnoreCase(selected.trim())) {
				logger.info("one crf selected");
				CRFBean cb = new CRFBean();
				cb.setId(id);
				cb.setName(name);

				// only find active verions
				ArrayList versions = (ArrayList) vdao.findAllActiveByCRF(cb.getId());
				cb.setVersions(versions);

				crfArray.add(cb);
			} else {
				if (tmpCRFIdMap.containsKey(id)) {
					tmpCRFIdMap.remove(id);
				}
			}
		}

		for (Object o : tmpCRFIdMap.keySet()) {
			int id = (Integer) o;
			String name = (String) tmpCRFIdMap.get(id);
			boolean isExists = false;
			for (Object aCrfArray : crfArray) {
				CRFBean cb = (CRFBean) aCrfArray;
				if (id == cb.getId()) {
					isExists = true;
				}
			}
			if (!isExists) {
				CRFBean cb = new CRFBean();
				cb.setId(id);
				cb.setName(name);
				// only find active verions
				ArrayList versions = (ArrayList) vdao.findAllActiveByCRF(cb.getId());
				cb.setVersions(versions);
				crfArray.add(cb);
			}
		}
		session.removeAttribute("tmpCRFIdMap");

		if (crfArray.size() == 0) {
			// no crf seleted
			addPageMessage(getResPage().getString("no_new_CRF_added"), request);
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
			sed.setCrfs(new ArrayList());
			session.setAttribute("definition", sed);
			forwardPage(Page.UPDATE_EVENT_DEFINITION1, request, response);
		} else {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
			ArrayList eventDefinitionCRFs = (ArrayList) session.getAttribute(EventDefinitionCRFUtil.EVENT_DEFINITION_CRFS_LABEL);
			ArrayList<EventDefinitionCRFBean> addedEventDefinitions = EventDefinitionCRFUtil.getAddedEventDefinitionCRFs(session);
			int ordinalForNewCRF = eventDefinitionCRFs.size();
			for (Object aCrfArray : crfArray) {
				CRFBean crf = (CRFBean) aCrfArray;
				EventDefinitionCRFBean edcBean = new EventDefinitionCRFBean();
				edcBean.setCrfId(crf.getId());
				edcBean.setCrfName(crf.getName());
				edcBean.setStudyId(ub.getActiveStudyId());
				edcBean.setStatus(Status.AVAILABLE);
				edcBean.setStudyEventDefinitionId(sed.getId());
				edcBean.setStudyId(ub.getActiveStudyId());
				edcBean.setSourceDataVerification(SourceDataVerification.NOTREQUIRED);
				edcBean.setOrdinal(++ordinalForNewCRF);
				edcBean.setVersions(crf.getVersions());
				SourceDataVerification.fillSDVStatuses(edcBean.getSdvOptions());
				CRFVersionBean defaultVersion1 = (CRFVersionBean) vdao.findByPK(edcBean.getDefaultVersionId());
				edcBean.setDefaultVersionName(defaultVersion1.getName());
				// update lists
				eventDefinitionCRFs.add(edcBean);
				addedEventDefinitions.add(edcBean);
			}
			session.setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
			session.setAttribute("addedEventDefinitionCRFs", addedEventDefinitions);
			addPageMessage(getResPage().getString("has_have_been_added_need_confirmation"), request);
			forwardPage(Page.UPDATE_EVENT_DEFINITION1, request, response);
		}
	}

	private EntityBeanTable createTable(HttpServletRequest request, ArrayList crfs) throws Exception {
		EntityBeanTable table = getEntityBeanTable();
		ArrayList allRows = CRFRow.generateRowsFromBeans(crfs);
		String[] columns = { getResWord().getString("CRF_name"), getResWord().getString("date_created"),
				getResWord().getString("owner"), getResWord().getString("date_updated"), getResWord().getString("last_updated_by"),
				getResWord().getString("selected")};
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(FIVE);
		HashMap args = new HashMap();
		args.put("actionName", "next");
		args.put("formWithStateFlag", request.getParameter("formWithStateFlag"));
		table.setQuery("AddCRFToDefinition", args);
		table.setRows(allRows);
		table.computeDisplay();

		return table;
	}
}
