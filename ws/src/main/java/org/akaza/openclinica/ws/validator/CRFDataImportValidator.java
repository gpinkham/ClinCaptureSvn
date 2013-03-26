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

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.ws.bean.BaseStudyDefinitionBean;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@SuppressWarnings({"rawtypes"})
public class CRFDataImportValidator implements Validator {

	DataSource dataSource;
	StudyDAO studyDAO;
	UserAccountDAO userAccountDAO;
	BaseVSValidatorImplementation helper;

	public CRFDataImportValidator(DataSource dataSource) {
		this.dataSource = dataSource;
		helper = new BaseVSValidatorImplementation();
	}

	public boolean supports(Class clazz) {
		return BaseStudyDefinitionBean.class.equals(clazz);

	}

	public void validate(Object obj, Errors e) {
		BaseStudyDefinitionBean crfDataImportBean = (BaseStudyDefinitionBean) obj;

		if (crfDataImportBean.getStudyUniqueId() == null) {
			e.reject("studyEventDefinitionRequestValidator.study_does_not_exist");
			return;
		}
		Status[] included_status = new Status[] { Status.AVAILABLE, Status.PENDING };
		StudyBean study = helper.verifyStudyByOID(getStudyDAO(), crfDataImportBean.getStudyUniqueId(), included_status,
				e);
		if (study == null)
			return;
		boolean isRoleVerified = helper.verifyRole(crfDataImportBean.getUser(), study.getId(), -1, Role.MONITOR, e);
		if (!isRoleVerified)
			return;

		crfDataImportBean.setStudy(study);

	}

	public StudyDAO getStudyDAO() {
		return this.studyDAO != null ? studyDAO : new StudyDAO(dataSource);
	}

	public UserAccountDAO getUserAccountDAO() {
		return this.userAccountDAO != null ? userAccountDAO : new UserAccountDAO(dataSource);
	}

}
