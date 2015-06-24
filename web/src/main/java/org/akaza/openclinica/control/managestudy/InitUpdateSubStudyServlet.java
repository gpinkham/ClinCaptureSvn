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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clinovo.util.DateUtil;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.service.StudyParamsConfig;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * InitUpdateSubStudyServlet.
 */
@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@Component
public class InitUpdateSubStudyServlet extends Controller {

	/**
     * 
     */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.SITE_LIST_SERVLET, respage.getString("current_study_locked"), request, response);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		StudyDAO sdao = getStudyDAO();
		String idString = request.getParameter("id");
		logger.info("study id:" + idString);
		if (StringUtil.isBlank(idString)) {
			addPageMessage(respage.getString("please_choose_a_study_to_edit"), request);
			forwardPage(Page.STUDY_LIST_SERVLET, request, response);
		} else {
			int studyId = Integer.valueOf(idString.trim());
			StudyBean study = (StudyBean) sdao.findByPK(studyId);

			checkRoleByUserAndStudy(request, response, ub, study.getParentStudyId(), study.getId());

			String parentStudyName = "";
			StudyBean parent = new StudyBean();
			if (study.getParentStudyId() > 0) {
				parent = (StudyBean) sdao.findByPK(study.getParentStudyId());
				parentStudyName = parent.getName();
				// at this time, this feature is only available for site
				createEventDefinitions(request, parent);
			}

			if (currentStudy.getId() != study.getId()) {
				ArrayList parentConfigs = currentStudy.getStudyParameters();
				ArrayList configs = new ArrayList();
				StudyParameterValueDAO spvdao = getStudyParameterValueDAO();
				for (Object parentConfig : parentConfigs) {
					StudyParamsConfig scg = (StudyParamsConfig) parentConfig;
					if (scg != null) {
						// find the one that sub study can change
						if (scg.getValue().getId() > 0 && scg.getParameter().isOverridable()) {
							StudyParameterValueBean spvb = spvdao.findByHandleAndStudy(study.getId(), scg
									.getParameter().getHandle());
							if (spvb.getId() > 0) {
								// the sub study itself has the parameter
								scg.setValue(spvb);
							}
							configs.add(scg);
						}
					}
				}

				study.setStudyParameters(configs);
			}
			request.setAttribute("parentStudy", parent);
			request.getSession().setAttribute("parentName", parentStudyName);
			request.getSession().setAttribute("newStudy", study);
			request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
			request.setAttribute("statuses", Status.toStudyUpdateMembersList());

			FormProcessor fp = new FormProcessor(request);
			if (study.getDatePlannedEnd() != null) {
				fp.addPresetValue(UpdateSubStudyServlet.INPUT_END_DATE, DateUtil.printDate(study.getDatePlannedEnd(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}
			if (study.getDatePlannedStart() != null) {
				fp.addPresetValue(UpdateSubStudyServlet.INPUT_START_DATE, DateUtil.printDate(study.getDatePlannedStart(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}
			if (study.getProtocolDateVerification() != null) {
				fp.addPresetValue(UpdateSubStudyServlet.INPUT_VER_DATE,
						DateUtil.printDate(study.getProtocolDateVerification(), getUserAccountBean().getUserTimeZoneId(),
								DateUtil.DatePattern.DATE, getLocale()));
			}
			setPresetValues(fp.getPresetValues(), request);
			forwardPage(Page.UPDATE_SUB_STUDY, request, response);
		}

	}

	private void createEventDefinitions(HttpServletRequest request, StudyBean parentStudy) {
		int siteId = Integer.parseInt(request.getParameter("id").trim());
		ArrayList<StudyEventDefinitionBean> seds;
		StudyEventDefinitionDAO sedDao = getStudyEventDefinitionDAO();
		EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
		CRFVersionDAO cvdao = getCRFVersionDAO();
		CRFDAO cdao = getCRFDAO();
		seds = sedDao.findAllAvailableByStudy(parentStudy);
		for (StudyEventDefinitionBean sed : seds) {
			int defId = sed.getId();
			ArrayList<EventDefinitionCRFBean> edcs = (ArrayList<EventDefinitionCRFBean>) edcdao
					.findAllByDefinitionAndSiteIdAndParentStudyId(defId, siteId, parentStudy.getId());
			ArrayList<EventDefinitionCRFBean> defCrfs = new ArrayList<EventDefinitionCRFBean>();
			for (EventDefinitionCRFBean edcBean : edcs) {
				int edcStatusId = edcBean.getStatus().getId();
				CRFBean crf = (CRFBean) cdao.findByPK(edcBean.getCrfId());
				int crfStatusId = crf.getStatusId();
				if (!(edcStatusId == Status.DELETED.getId() || edcStatusId == Status.AUTO_DELETED.getId()
						|| crfStatusId == Status.DELETED.getId() || crfStatusId == Status.AUTO_DELETED.getId())) {
					ArrayList<CRFVersionBean> versions = (ArrayList<CRFVersionBean>) cvdao.findAllActiveByCRF(edcBean
							.getCrfId());
					edcBean.setVersions(versions);
					edcBean.setCrfName(crf.getName());
					CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edcBean.getDefaultVersionId());
					edcBean.setDefaultVersionName(defaultVersion.getName());
					String sversionIds = edcBean.getSelectedVersionIds();
					ArrayList<Integer> idList = new ArrayList<Integer>();
					if (sversionIds.length() > 0) {
						String[] ids = sversionIds.split("\\,");
						for (String id : ids) {
							idList.add(Integer.valueOf(id));
						}
					}
					edcBean.setSelectedVersionIdList(idList);
					SourceDataVerification.fillSDVStatuses(edcBean.getSdvOptions(),
							getItemSDVService().hasItemsToSDV(crf.getId()));
					defCrfs.add(edcBean);
				}
			}
			logger.debug("definitionCrfs size=" + defCrfs.size() + " total size=" + edcs.size());
			sed.setCrfs(defCrfs);
			sed.setCrfNum(defCrfs.size());
		}
		// not sure if request is better, since not sure if there is another
		// process using this.
		request.getSession().setAttribute("definitions", seds);
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return Controller.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

}
