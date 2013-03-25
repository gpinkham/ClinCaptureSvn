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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.ws.validator;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.ws.bean.BaseStudyDefinitionBean;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.sql.DataSource;

public class StudyEventDefinitionRequestValidator implements Validator {

	DataSource dataSource;
	StudyDAO studyDAO;
	StudySubjectDAO studySubjectDAO;
	StudyEventDefinitionDAO studyEventDefinitionDAO;
	UserAccountDAO userAccountDAO;
	BaseVSValidatorImplementation helper;

	public StudyEventDefinitionRequestValidator(DataSource dataSource) {
		this.dataSource = dataSource;
		helper = new BaseVSValidatorImplementation();
	}

	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return BaseStudyDefinitionBean.class.equals(clazz);
	}

	public void validate(Object obj, Errors e) {
		BaseStudyDefinitionBean studyEventDefinitionRequestBean = (BaseStudyDefinitionBean) obj;

		if (studyEventDefinitionRequestBean.getStudyUniqueId() == null
				&& studyEventDefinitionRequestBean.getSiteUniqueId() == null) {
			e.reject("studyEventDefinitionRequestValidator.invalid_study_identifier");
			return;
		}
		StudyBean study;
		int study_id = -1;
		if (studyEventDefinitionRequestBean.getStudyUniqueId() != null) {
			study = helper.verifyStudy(getStudyDAO(), studyEventDefinitionRequestBean.getStudyUniqueId(), null, e);
			if (study == null) {
				return;
			}
			study_id = study.getId();
		}
		StudyBean site;
		int site_id = -1;
		if (studyEventDefinitionRequestBean.getSiteUniqueId() != null) {
			site = helper.verifySite(getStudyDAO(), studyEventDefinitionRequestBean.getStudyUniqueId(),
					studyEventDefinitionRequestBean.getSiteUniqueId(), null, e);
			if (site == null) {
				return;
			}
			site_id = site.getId();
		}
		boolean isRoleVerified = helper.verifyRole(studyEventDefinitionRequestBean.getUser(), study_id, site_id, e);

		// //verify study ID
		// if (studyEventDefinitionRequestBean.getStudyUniqueId() != null ) {
		// StudyBean study = getStudyDAO().findByUniqueIdentifier(studyEventDefinitionRequestBean.getStudyUniqueId());
		// if (study == null) {
		// e.reject("studyEventDefinitionRequestValidator.invalid_study_identifier");
		// return;
		// }
		// studyEventDefinitionRequestBean.setStudy(study);
		// }
		// if ( studyEventDefinitionRequestBean.getSiteUniqueId() != null) {
		// StudyBean site = getStudyDAO().findSiteByUniqueIdentifier(studyEventDefinitionRequestBean.getStudyUniqueId(),
		// studyEventDefinitionRequestBean.getSiteUniqueId());
		//
		// if ( site == null ) {
		// e.reject("studyEventDefinitionRequestValidator.invalid_study_identifier_site_identifier");
		// return;
		// }
		// studyEventDefinitionRequestBean.setStudy(site);
		// }
		// StudyUserRoleBean siteSur =
		// getUserAccountDAO().findRoleByUserNameAndStudyId(studyEventDefinitionRequestBean.getUser().getName(),
		// studyEventDefinitionRequestBean.getStudy().getId());
		// if (siteSur.getStatus() != Status.AVAILABLE) {
		// e.reject("studyEventDefinitionRequestValidator.insufficient_permissions",
		// "You do not have sufficient privileges to proceed with this operation.");
		// return;
		// }

	}

	public StudyDAO getStudyDAO() {
		return this.studyDAO != null ? studyDAO : new StudyDAO(dataSource);
	}

	public StudySubjectDAO getStudySubjectDAO() {
		return this.studySubjectDAO != null ? studySubjectDAO : new StudySubjectDAO(dataSource);
	}

	public StudyEventDefinitionDAO getStudyEventDefinitionDAO() {
		return this.studyEventDefinitionDAO != null ? studyEventDefinitionDAO : new StudyEventDefinitionDAO(dataSource);
	}

	public UserAccountDAO getUserAccountDAO() {
		return this.userAccountDAO != null ? userAccountDAO : new UserAccountDAO(dataSource);
	}

}
