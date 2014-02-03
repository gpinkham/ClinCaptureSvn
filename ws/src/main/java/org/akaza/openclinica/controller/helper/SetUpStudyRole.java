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

package org.akaza.openclinica.controller.helper;

import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.view.StudyInfoPanel;

/**
 * This class has been created from the existing SecureController to implement the set-up code for the existing
 * view-related JSPs (sidebars, etc.).
 */
@SuppressWarnings({"rawtypes"})
public class SetUpStudyRole {
	private DataSource dataSource;

	public static final String STUDY_INFO_PANEL = "panel";

	public SetUpStudyRole(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setUp(HttpSession httpSession, UserAccountBean userAccountBean) {

		StudyUserRoleBean currentRole = new StudyUserRoleBean();
		StudyBean currentStudy = new StudyBean();
		StudyInfoPanel panel = new StudyInfoPanel();

		StudyDAO sdao = new StudyDAO(dataSource);

		if (userAccountBean.getId() > 0 && userAccountBean.getActiveStudyId() > 0) {
			StudyParameterValueDAO spvdao = new StudyParameterValueDAO(dataSource);
			currentStudy = (StudyBean) sdao.findByPK(userAccountBean.getActiveStudyId());

			ArrayList studyParameters = spvdao.findParamConfigByStudy(currentStudy);

			currentStudy.setStudyParameters(studyParameters);

			StudyConfigService scs = new StudyConfigService(dataSource);
			if (currentStudy.getParentStudyId() <= 0) {// top study
				scs.setParametersForStudy(currentStudy);

			} else {
				currentStudy.setParentStudyName(((StudyBean) sdao.findByPK(currentStudy.getParentStudyId())).getName());
				scs.setParametersForSite(currentStudy);
			}

			panel.reset();
			httpSession.setAttribute(STUDY_INFO_PANEL, panel);
		} else {
			currentStudy = new StudyBean();
		}
		httpSession.setAttribute("study", currentStudy);
		if (currentStudy.getParentStudyId() > 0) {
			currentStudy.setParentStudyName(((StudyBean) sdao.findByPK(currentStudy.getParentStudyId())).getName());
		}

		if (currentRole.getId() <= 0) {
			if (userAccountBean.getId() > 0 && currentStudy.getId() > 0
					&& !currentStudy.getStatus().getName().equals("removed")) {
				currentRole = userAccountBean.getRoleByStudy(currentStudy.getId());
				if (currentStudy.getParentStudyId() > 0) {
					StudyUserRoleBean roleInParent = userAccountBean.getRoleByStudy(currentStudy.getParentStudyId());
					currentRole.setRole(Role.max(currentRole.getRole(), roleInParent.getRole()));
				}
			} else {
				currentRole = new StudyUserRoleBean();
			}
			httpSession.setAttribute("userRole", currentRole);
		}
		else if (currentRole.getId() > 0
				&& (currentStudy.getStatus().equals(Status.DELETED) || currentStudy.getStatus().equals(
						Status.AUTO_DELETED))) {
			currentRole.setRole(Role.INVALID);
			currentRole.setStatus(Status.DELETED);
			httpSession.setAttribute("userRole", currentRole);
		}

	}
}
