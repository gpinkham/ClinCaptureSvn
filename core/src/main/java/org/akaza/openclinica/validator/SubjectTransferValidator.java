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

package org.akaza.openclinica.validator;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.SubjectTransferBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

@SuppressWarnings({"rawtypes"})
public class SubjectTransferValidator implements Validator {

	DataSource dataSource;
	StudyDAO studyDAO;
	StudySubjectDAO studySubjectDAO;
	StudyParameterValueDAO studyParameterValueDAO;

	public SubjectTransferValidator(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public boolean supports(Class clazz) {
		return SubjectTransferBean.class.equals(clazz);
	}

	public void validate(Object obj, Errors e) {
		SubjectTransferBean subjectTransferBean = (SubjectTransferBean) obj;

		StudyBean study = getStudyDAO().findByUniqueIdentifier(subjectTransferBean.getStudyOid());
		if (study == null) {
			e.reject("subjectTransferValidator.study_does_not_exist",
					new Object[] { subjectTransferBean.getStudyOid() }, "Study identifier you specified "
							+ subjectTransferBean.getStudyOid() + " does not correspond to a valid study.");
			return;
		}

		UserAccountBean ua = subjectTransferBean.getOwner();
		StudyUserRoleBean role = ua.getRoleByStudy(study);
		if (role.getId() == 0 || role.getRole().equals(Role.STUDY_MONITOR)) {
			e.reject("subjectTransferValidator.insufficient_permissions",
					"You do not have sufficient privileges to proceed with this operation.");
			return;
		}

		if (subjectTransferBean.getSiteIdentifier() != null) {
			study = getStudyDAO().findSiteByUniqueIdentifier(subjectTransferBean.getStudyOid(),
					subjectTransferBean.getSiteIdentifier());
		}
		subjectTransferBean.setStudy(study);
		if (study == null) {
			e.reject("subjectTransferValidator.site_does_not_exist",
					new Object[] { subjectTransferBean.getSiteIdentifier() },
					"Site identifier you specified does not correspond to a valid site.");
			return;
		}
		int handleStudyId = study.getParentStudyId() > 0 ? study.getParentStudyId() : study.getId();
		StudyParameterValueBean studyParameter = getStudyParameterValueDAO().findByHandleAndStudy(handleStudyId,
				"subjectPersonIdRequired");
		String personId = subjectTransferBean.getPersonId();
		if ("required".equals(studyParameter.getValue()) && (personId == null || personId.length() < 1)) {
			e.reject("subjectTransferValidator.personId_required", new Object[] { study.getName() },
					"personId is required for the study: " + study.getName());
			return;
		}

		if (personId != null && personId.length() > 255) {
			e.reject("subjectTransferValidator.personId_invalid_length", new Object[] { personId }, "personId: "
					+ personId + " cannot be longer than 255 characters.");
			return;
		}

		String idSetting = "";
		StudyParameterValueBean subjectIdGenerationParameter = getStudyParameterValueDAO().findByHandleAndStudy(
				handleStudyId, "subjectIdGeneration");
		idSetting = subjectIdGenerationParameter.getValue();
		if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
			int nextLabel = getStudySubjectDAO().findTheGreatestLabel() + 1;
			subjectTransferBean.setStudySubjectId(new Integer(nextLabel).toString());
		}
		String studySubjectId = subjectTransferBean.getStudySubjectId();
		if (studySubjectId == null || studySubjectId.length() < 1) {
			e.reject("subjectTransferValidator.studySubjectId_required");
			return;
		} else if (studySubjectId.length() > 30) {
			e.reject("subjectTransferValidator.studySubjectId_invalid_length", new Object[] { studySubjectId },
					"studySubjectId: " + studySubjectId + " cannot be longer than 30 characters.");
			return;
		}

		String secondaryId = subjectTransferBean.getSecondaryId();
		if (secondaryId != null && secondaryId.length() > 30) {
			e.reject("subjectTransferValidator.secondaryId_invalid_length", new Object[] { secondaryId },
					"secondaryId: " + secondaryId + " cannot be longer than 30 characters.");
			return;
		}
		String gender = subjectTransferBean.getGender() + "";
		studyParameter = getStudyParameterValueDAO().findByHandleAndStudy(handleStudyId, "genderRequired");
		if ("true".equals(studyParameter.getValue()) && (gender == null || gender.length() < 1)) {
			e.reject("subjectTransferValidator.gender_required", new Object[] { study.getName() },
					"Gender is required for the study: " + study.getName());
			return;
		}

		if (!"m".equals(gender) && !"f".equals(gender)) {
			System.out.println("did not pass gender: " + gender);
			e.reject("subjectTransferValidator.gender_is_m_or_f");
			// e.reject("subjectTransferValidator.gender_required", new Object[] { study.getName() },
			// "Gender is required to be 'm' or 'f'");
			return;
		} else {
			System.out.println("passed gender: " + gender);
		}

		Date dateOfBirth = subjectTransferBean.getDateOfBirth();
		String yearOfBirth = subjectTransferBean.getYearOfBirth();
		studyParameter = getStudyParameterValueDAO().findByHandleAndStudy(handleStudyId, "collectDob");
		if ("1".equals(studyParameter.getValue()) && (dateOfBirth == null)) {
			e.reject("subjectTransferValidator.dateOfBirth_required", new Object[] { study.getName() },
					"Date of birth is required for the study " + study.getName());
			return;
		} else if ("2".equals(studyParameter.getValue()) && (yearOfBirth == null)) {
			e.reject("subjectTransferValidator.yearOfBirth_required", new Object[] { study.getName() },
					"Year of birth is required for the study " + study.getName());
			return;
		} else if ("2".equals(studyParameter.getValue()) && (yearOfBirth != null)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
				subjectTransferBean.setDateOfBirth(sdf.parse(subjectTransferBean.getYearOfBirth()));
			} catch (ParseException xe) {
				e.reject("subjectTransferValidator.yearOfBirth_invalid", new Object[] { yearOfBirth },
						"Year of birth: " + yearOfBirth + " is not valid");
				return;
			}
		}

		Date enrollmentDate = subjectTransferBean.getEnrollmentDate();
		if (enrollmentDate == null) {
			e.reject("subjectTransferValidator.enrollmentDate_required");
			return;
		} else {
			if ((new Date()).compareTo(enrollmentDate) < 0) {
				e.reject("subjectTransferValidator.enrollmentDate_should_be_in_past");
				return;
			}
		}
	}

	public StudyDAO getStudyDAO() {
		return this.studyDAO != null ? studyDAO : new StudyDAO(dataSource);
	}

	public StudySubjectDAO getStudySubjectDAO() {
		return this.studySubjectDAO != null ? studySubjectDAO : new StudySubjectDAO(dataSource);
	}

	public StudyParameterValueDAO getStudyParameterValueDAO() {
		return this.studyParameterValueDAO != null ? studyParameterValueDAO : new StudyParameterValueDAO(dataSource);
	}

}
