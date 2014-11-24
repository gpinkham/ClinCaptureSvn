/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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
package org.akaza.openclinica.dao.service;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameter;
import org.akaza.openclinica.bean.service.StudyParameterConfig;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StudyConfigService class.
 */
@SuppressWarnings({ "rawtypes" })
public class StudyConfigService {

	private DataSource ds;
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	/**
	 * This method is used to initialize Study Config Service.
	 * @param ds The data source to set.
	 */
	public StudyConfigService(DataSource ds) {
		this.ds = ds;
	}

	/**
	 * @return Returns the ds.
	 */
	public DataSource getDs() {
		return ds;
	}

	/**
	 * @param ds The ds to set.
	 */
	public void setDs(DataSource ds) {
		this.ds = ds;
	}

	/**
	 * This method construct an object which has all the study parameter values.
	 *
	 * @param study StudyBean
	 * @return StudyBean
	 */
	public StudyBean setParametersForStudy(StudyBean study) {
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(ds);
		ArrayList parameters = spvdao.findAllParameters();
		StudyParameterConfig spc = new StudyParameterConfig();

		for (Object parameter : parameters) {
			StudyParameter sp = (StudyParameter) parameter;
			String handle = sp.getHandle();
			StudyParameterValueBean spv = spvdao.findByHandleAndStudy(study.getId(), handle);

			setStudyParameterValues(spvdao, spc, handle, spv);
		}
		study.setStudyParameterConfig(spc);
		return study;

	}

	private void setStudyParameterValues(StudyParameterValueDAO spvdao, StudyParameterConfig spc, String handle,
			StudyParameterValueBean spv) {

		// TO DO: will change to use java reflection later
		if (spv.getId() > 0) {
			if (handle.equalsIgnoreCase("collectDob")) {
				spc.setCollectDob(spv.getValue());
			} else if (handle.equalsIgnoreCase("genderRequired")) {
				spc.setGenderRequired(spv.getValue());
			} else if (handle.equalsIgnoreCase("discrepancyManagement")) {
				spc.setDiscrepancyManagement(spv.getValue());
			} else if (handle.equalsIgnoreCase("subjectPersonIdRequired")) {
				spc.setSubjectPersonIdRequired(spv.getValue());
			} else if (handle.equalsIgnoreCase("interviewerNameRequired")) {
				spc.setInterviewerNameRequired(spv.getValue());
			} else if (handle.equalsIgnoreCase("interviewerNameDefault")) {
				spc.setInterviewerNameDefault(spv.getValue());
			} else if (handle.equalsIgnoreCase("interviewerNameEditable")) {
				spc.setInterviewerNameEditable(spv.getValue());
			} else if (handle.equalsIgnoreCase("interviewDateRequired")) {
				spc.setInterviewDateRequired(spv.getValue());
			} else if (handle.equalsIgnoreCase("interviewDateDefault")) {
				spc.setInterviewDateDefault(spv.getValue());
			} else if (handle.equalsIgnoreCase("interviewDateEditable")) {
				spc.setInterviewDateEditable(spv.getValue());
			} else if (handle.equalsIgnoreCase("subjectIdGeneration")) {
				spc.setSubjectIdGeneration(spv.getValue());
				logger.info("subjectIdGeneration" + spc.getSubjectIdGeneration());
			} else if (handle.equalsIgnoreCase("subjectIdPrefixSuffix")) {
				spc.setSubjectIdPrefixSuffix(spv.getValue());
			} else if (handle.equalsIgnoreCase("personIdShownOnCRF")) {
				spc.setPersonIdShownOnCRF(spv.getValue());
			} else if (handle.equalsIgnoreCase("secondaryLabelViewable")) {
				spc.setSecondaryLabelViewable(spv.getValue());
			} else if (handle.equalsIgnoreCase("adminForcedReasonForChange")) {
				spc.setAdminForcedReasonForChange(spv.getValue());
			} else if (handle.equalsIgnoreCase("eventLocationRequired")) {
				spc.setEventLocationRequired(spv.getValue());
			} else if (handle.equalsIgnoreCase("secondaryIdRequired")) {
				spc.setSecondaryIdRequired(spv.getValue());
			} else if (handle.equalsIgnoreCase("dateOfEnrollmentForStudyRequired")) {
				spc.setDateOfEnrollmentForStudyRequired(spv.getValue());
			} else if (handle.equalsIgnoreCase("studySubjectIdLabel")) {
				spc.setStudySubjectIdLabel(spv.getValue());
			} else if (handle.equalsIgnoreCase("secondaryIdLabel")) {
				spc.setSecondaryIdLabel(spv.getValue());
			} else if (handle.equalsIgnoreCase("dateOfEnrollmentForStudyLabel")) {
				spc.setDateOfEnrollmentForStudyLabel(spv.getValue());
			} else if (handle.equalsIgnoreCase("genderLabel")) {
				spc.setGenderLabel(spv.getValue());
			} else if (handle.equalsIgnoreCase("startDateTimeRequired")) {
				spc.setStartDateTimeRequired(spv.getValue());
			} else if (handle.equalsIgnoreCase("useStartTime")) {
				spc.setUseStartTime(spv.getValue());
			} else if (handle.equalsIgnoreCase("endDateTimeRequired")) {
				spc.setEndDateTimeRequired(spv.getValue());
			} else if (handle.equalsIgnoreCase("useEndTime")) {
				spc.setUseEndTime(spv.getValue());
			} else if (handle.equalsIgnoreCase("startDateTimeLabel")) {
				spc.setStartDateTimeLabel(spv.getValue());
			} else if (handle.equalsIgnoreCase("endDateTimeLabel")) {
				spc.setEndDateTimeLabel(spv.getValue());
			} else if (handle.equalsIgnoreCase("markImportedCRFAsCompleted")) {
				spc.setMarkImportedCRFAsCompleted(spv.getValue());
			} else if (handle.equalsIgnoreCase("autoScheduleEventDuringImport")) {
				spc.setAutoScheduleEventDuringImport(spv.getValue());
			} else if (handle.equalsIgnoreCase("autoCreateSubjectDuringImport")) {
				spc.setAutoCreateSubjectDuringImport(spv.getValue());
			} else if (handle.equalsIgnoreCase("allowSdvWithOpenQueries")) {
				spc.setAllowSdvWithOpenQueries(spv.getValue());
			} else if (handle.equalsIgnoreCase("allowDynamicGroupsManagement")) {
				spc.setAllowDynamicGroupsManagement(spv.getValue());
			} else if (handle.equalsIgnoreCase("replaceExisitingDataDuringImport")) {
				spc.setReplaceExisitingDataDuringImport(spv.getValue());
			} else if (handle.equalsIgnoreCase("allowCodingVerification")) {
				spc.setAllowCodingVerification(spv.getValue());
			} else if (handle.equalsIgnoreCase("autoCodeDictionaryName")) {
				spc.setAutoCodeDictionaryName(spv.getValue());
			} else if (handle.equalsIgnoreCase("medicalCodingApprovalNeeded")) {
				spc.setMedicalCodingApprovalNeeded(spv.getValue());
			} else if (handle.equalsIgnoreCase("medicalCodingContextNeeded")) {
				spc.setMedicalCodingContextNeeded(spv.getValue());
			} else if (handle.equalsIgnoreCase("assignRandomizationResultTo")) {
				spc.setAssignRandomizationResultTo(spv.getValue());
			} else if (handle.equalsIgnoreCase("randomizationTrialId")) {
				spc.setRandomizationTrialId(spv.getValue());
			} else if (handle.equalsIgnoreCase("allowCrfEvaluation")) {
				spc.setAllowCrfEvaluation(spv.getValue());
			} else if (handle.equalsIgnoreCase("evaluateWithContext")) {
				spc.setEvaluateWithContext(spv.getValue());
			} else if (handle.equalsIgnoreCase("autoGeneratedPrefix")) {
				spc.setAutoGeneratedPrefix(spv.getValue());
			} else if (handle.equalsIgnoreCase("autoGeneratedSeparator")) {
				spc.setAutoGeneratedSeparator(spv.getValue());
			} else if (handle.equalsIgnoreCase("autoGeneratedSuffix")) {
				spc.setAutoGeneratedSuffix(spv.getValue());
			} else if (handle.equalsIgnoreCase("allowRulesAutoScheduling")) {
				spc.setAllowRulesAutoScheduling(spv.getValue());
			}
		} else if (spv.getId() == 0) {
			setSystemParameterValues(spvdao, spc, handle);
		}

		if (handle.equalsIgnoreCase("defaultBioontologyURL")) {
			setSystemParameterValues(spvdao, spc, handle);
		} else if (handle.equalsIgnoreCase("medicalCodingApiKey")) {
			setSystemParameterValues(spvdao, spc, handle);
		}
	}

	private void setSystemParameterValues(StudyParameterValueDAO spvdao, StudyParameterConfig spc, String handle) {
		
		com.clinovo.model.System systemProp = spvdao.findSystemPropertyByName(handle);
		String value;
		if (systemProp != null) {
			value = systemProp.getValue();
			if (handle.equalsIgnoreCase("markImportedCRFAsCompleted")) {
				spc.setMarkImportedCRFAsCompleted(value);
			} else if (handle.equalsIgnoreCase("autoScheduleEventDuringImport")) {
				spc.setAutoScheduleEventDuringImport(value);
		    } else if (handle.equalsIgnoreCase("autoCreateSubjectDuringImport")) {
		        spc.setAutoCreateSubjectDuringImport(value);
		    } else if (handle.equalsIgnoreCase("replaceExisitingDataDuringImport")) {
		        spc.setReplaceExisitingDataDuringImport(value);
			} else if (handle.equalsIgnoreCase("defaultBioontologyURL")) {
				spc.setDefaultBioontologyURL(value);
			} else if (handle.equalsIgnoreCase("autoCodeDictionaryName")) {
				spc.setAutoCodeDictionaryName(value);
			} else if (handle.equalsIgnoreCase("medicalCodingApiKey")) {
		        spc.setMedicalCodingApiKey(value);
		    } else if (handle.equalsIgnoreCase("allowCrfEvaluation")) {
				spc.setAllowCrfEvaluation(value);
			} else if (handle.equalsIgnoreCase("evaluateWithContext")) {
				spc.setEvaluateWithContext(value);
			}
		}
	}

	/**
	 * This method is used to set all study parameters to site.
	 *
	 * @param site The <code>StudyBean</code> for which all site parameters should be set.
	 * @return The <code>StudyBean</code> for which all site parameters had been set
	 */
	public StudyBean setParametersForSite(StudyBean site) {

		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(ds);
		ArrayList parameters = spvdao.findAllParameters();
		StudyParameterConfig spc = new StudyParameterConfig();

		for (Object parameter : parameters) {
			StudyParameter sp = (StudyParameter) parameter;
			String handle = sp.getHandle();
			StudyParameterValueBean spv = spvdao.findByHandleAndStudy(site.getId(), handle);

			setStudyParameterValues(spvdao, spc, handle, spv);
		}
		site.setStudyParameterConfig(spc);
		return site;
	}
}
