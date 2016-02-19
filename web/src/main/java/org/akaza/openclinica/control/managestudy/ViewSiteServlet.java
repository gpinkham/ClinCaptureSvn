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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * ViewSiteServlet.
 *
 * @author jxu
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class ViewSiteServlet extends Controller {

	/**
	 * Checks whether the user has the correct privilege.
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}
		int siteId = request.getParameter("id") == null ? 0 : Integer.valueOf(request.getParameter("id"));
		if (currentStudy.getId() == siteId) {
			return;
		}
		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study") + " "
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		StudyDAO sdao = new StudyDAO(getDataSource());
		String idString;
		if (request.getAttribute("siteId") == null) {
			idString = request.getParameter("id");
		} else {
			idString = request.getAttribute("siteId").toString();
		}
		logger.info("site id:" + idString);
		if (StringUtil.isBlank(idString)) {
			addPageMessage(getResPage().getString("please_choose_a_site_to_edit"), request);
			forwardPage(Page.SITE_LIST_SERVLET, request, response);
		} else {
			int siteId = Integer.parseInt(idString.trim());
			StudyBean study = (StudyBean) sdao.findByPK(siteId);

			checkRoleByUserAndStudy(request, response, ub, study.getParentStudyId(), study.getId());

			ArrayList configs;
			StudyParameterValueDAO spvdao = new StudyParameterValueDAO(getDataSource());
			configs = spvdao.findParamConfigByStudy(study);
			study.setStudyParameters(configs);

			String parentStudyName = "";
			String parentStudyOid = "";
			if (study.getParentStudyId() > 0) {
				StudyBean parent = (StudyBean) sdao.findByPK(study.getParentStudyId());
				parentStudyName = parent.getName();
				parentStudyOid = parent.getOid();
			}
			request.setAttribute("parentName", parentStudyName);
			request.setAttribute("parentStudyOid", parentStudyOid);
			request.setAttribute("siteToView", study);
			request.setAttribute("idToSort", request.getAttribute("idToSort"));
			request.setAttribute("showCasebookButton", areSubjectsOnSite(study));
			viewSiteEventDefinitions(request, study);

			forwardPage(Page.VIEW_SITE, request, response);
		}
	}

	private boolean areSubjectsOnSite(StudyBean study) {
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(getDataSource());
		int countOfSubjects = studySubjectDAO.getCountofStudySubjects(study);
		return countOfSubjects != 0;
	}

	private void viewSiteEventDefinitions(HttpServletRequest request, StudyBean siteToView) {
		int siteId = siteToView.getId();
		ArrayList<StudyEventDefinitionBean> seds;
		StudyEventDefinitionDAO sedDao = new StudyEventDefinitionDAO(getDataSource());
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(getDataSource());
		CRFVersionDAO cvdao = new CRFVersionDAO(getDataSource());
		CRFDAO cdao = new CRFDAO(getDataSource());
		seds = sedDao.findAllAvailableByStudy(siteToView);
		for (StudyEventDefinitionBean sed : seds) {
			int defId = sed.getId();
			ArrayList<EventDefinitionCRFBean> edcs = (ArrayList<EventDefinitionCRFBean>) edcdao
					.findAllByDefinitionAndSiteIdAndParentStudyId(defId, siteId, siteToView.getParentStudyId());
			ArrayList<EventDefinitionCRFBean> defCrfs = new ArrayList<EventDefinitionCRFBean>();
			for (EventDefinitionCRFBean edcBean : edcs) {
				CRFBean crf = (CRFBean) cdao.findByPK(edcBean.getCrfId());
				ArrayList<CRFVersionBean> versions = (ArrayList<CRFVersionBean>) cvdao.findAllActiveByCRF(edcBean
						.getCrfId());
				edcBean.setVersions(versions);
				edcBean.setCrfName(crf.getName());
				CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edcBean.getDefaultVersionId());
				edcBean.setDefaultVersionName(defaultVersion.getName());
				String sversionIds = edcBean.getSelectedVersionIds();
				ArrayList<Integer> idList = new ArrayList<Integer>();
				String idNames = "";
				if (sversionIds.length() > 0) {
					String[] ids = sversionIds.split("\\,");
					for (String id : ids) {
						idList.add(Integer.valueOf(id));
						for (CRFVersionBean v : versions) {
							if (v.getId() == Integer.valueOf(id)) {
								idNames += v.getName() + ",";
								break;
							}
						}
					}
					if (!idNames.isEmpty()) {
						idNames = idNames.substring(0, idNames.length() - 1);
					}
				}
				edcBean.setSelectedVersionIdList(idList);
				edcBean.setSelectedVersionNames(idNames);
				defCrfs.add(edcBean);
			}
			sed.setCrfs(defCrfs);
			sed.setCrfNum(defCrfs.size());
		}
		request.setAttribute("definitions", seds);
	}

}
