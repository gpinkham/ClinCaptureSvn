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
package org.akaza.openclinica.dao.service;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameter;
import org.akaza.openclinica.bean.service.StudyParameterConfig;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import javax.sql.DataSource;

@SuppressWarnings({"rawtypes"})
public class StudyConfigService {

	private DataSource ds;
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

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
	 * @param ds
	 *            The ds to set.
	 */
	public void setDs(DataSource ds) {
		this.ds = ds;
	}

	/**
	 * � true if the study has a value defined for this parameter o if studyId is a parent study, then this is true
	 * iff there is a row for this study/parameter pair in the study_parameter_value table o if studyId is a site, then
	 * this is true if: ? * the parameter is inheritable and the study�s parent has a defined parameter value; OR ? *
	 * the parameter is not inheritable and there is a row for this studyId/parameter pair in the study_parameter_value
	 * table
	 * 
	 * @param studyId
	 * @param parameterHandle
	 * @return
	 */
	public String hasDefinedParameterValue(int studyId, String parameterHandle) {
		StudyDAO sdao = new StudyDAO(ds);
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(ds);

		if (studyId <= 0 || StringUtil.isBlank(parameterHandle)) {
			return null;
		}

		StudyParameterValueBean spv = spvdao.findByHandleAndStudy(studyId, parameterHandle);
		StudyParameter sp = spvdao.findParameterByHandle(parameterHandle);
		StudyBean study = (StudyBean) sdao.findByPK(studyId);
		if (spv.getId() > 0) {// there is a row for that study, no matter it
			// is a
			// top study or not
			return spv.getValue();
		}
		int parentId = study.getParentStudyId();
		if (parentId > 0) {
			StudyParameterValueBean spvParent = spvdao.findByHandleAndStudy(parentId, parameterHandle);
			if (spvParent.getId() > 0 && sp.isInheritable()) {
				return spvParent.getValue();
			}

		}
		return null;

	}

	/**
	 * This method construct an object which has all the study parameter values
	 * 
	 * @param study
	 * @return
	 */
	public StudyBean setParametersForStudy(StudyBean study) {
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(ds);
		ArrayList parameters = spvdao.findAllParameters();
		StudyParameterConfig spc = new StudyParameterConfig();

		for (int i = 0; i < parameters.size(); i++) {
			StudyParameter sp = (StudyParameter) parameters.get(i);
			String handle = sp.getHandle();
			StudyParameterValueBean spv = spvdao.findByHandleAndStudy(study.getId(), handle);
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
				}
				else if (handle.equalsIgnoreCase("secondaryIdRequired")) {
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
				}
				else if (handle.equalsIgnoreCase("startDateTimeRequired")) {
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
				} else if (handle.equalsIgnoreCase("allowSdvWithOpenQueries")) {
                    spc.setAllowSdvWithOpenQueries(spv.getValue());
                } else if (handle.equalsIgnoreCase("replaceExisitingDataDuringImport")) {
                    spc.setReplaceExisitingDataDuringImport(spv.getValue());
				} else if (handle.equalsIgnoreCase("allowCodingVerification")) {
					spc.setAllowCodingVerification(spv.getValue());
				} else if (handle.equalsIgnoreCase("defaultMedicalCodingDictionary")) {
					spc.setDefaultMedicalCodingDictionary(spv.getValue());
				}
			}
		}
		study.setStudyParameterConfig(spc);
		return study;

	}

	public StudyBean setParameterValuesForStudy(StudyBean study) {
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(ds);
		ArrayList theParameters = spvdao.findParamConfigByStudy(study);
		study.setStudyParameters(theParameters);

		ArrayList parameters = spvdao.findAllParameterValuesByStudy(study);

		for (int i = 0; i < parameters.size(); i++) {
			StudyParameterValueBean spvb = (StudyParameterValueBean) parameters.get(i);
			String parameter = spvb.getParameter();
			if (parameter.equalsIgnoreCase("collectDob")) {
				study.getStudyParameterConfig().setCollectDob(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("genderRequired")) {
				study.getStudyParameterConfig().setGenderRequired(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("subjectPersonIdRequired")) {
				study.getStudyParameterConfig().setSubjectPersonIdRequired(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("discrepancyManagement")) {
				study.getStudyParameterConfig().setDiscrepancyManagement(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("subjectIdGeneration")) {
				study.getStudyParameterConfig().setSubjectIdGeneration(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("subjectIdPrefixSuffix")) {
				study.getStudyParameterConfig().setSubjectIdPrefixSuffix(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("interviewerNameRequired")) {
				study.getStudyParameterConfig().setInterviewerNameRequired(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("interviewerNameDefault")) {
				study.getStudyParameterConfig().setInterviewerNameDefault(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("interviewerNameEditable")) {
				study.getStudyParameterConfig().setInterviewerNameEditable(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("interviewDateRequired")) {
				study.getStudyParameterConfig().setInterviewDateRequired(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("interviewDateDefault")) {
				study.getStudyParameterConfig().setInterviewDateDefault(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("interviewDateEditable")) {
				study.getStudyParameterConfig().setInterviewDateEditable(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("personIdShownOnCRF")) {
				study.getStudyParameterConfig().setPersonIdShownOnCRF(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("adminForcedReasonForChange")) {
				study.getStudyParameterConfig().setAdminForcedReasonForChange(spvb.getValue());
			}

			else if (parameter.equalsIgnoreCase("secondaryIdRequired")) {
				study.getStudyParameterConfig().setSecondaryIdRequired(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("secondaryLabelViewable")) {
				study.getStudyParameterConfig().setSecondaryLabelViewable(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("dateOfEnrollmentForStudyRequired")) {
				study.getStudyParameterConfig().setDateOfEnrollmentForStudyRequired(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("studySubjectIdLabel")) {
				study.getStudyParameterConfig().setStudySubjectIdLabel(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("secondaryIdLabel")) {
				study.getStudyParameterConfig().setSecondaryIdLabel(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("dateOfEnrollmentForStudyLabel")) {
				study.getStudyParameterConfig().setDateOfEnrollmentForStudyLabel(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("genderLabel")) {
				study.getStudyParameterConfig().setGenderLabel(spvb.getValue());
			}
			else if (parameter.equalsIgnoreCase("startDateTimeRequired")) {
				study.getStudyParameterConfig().setStartDateTimeRequired(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("useStartTime")) {
				study.getStudyParameterConfig().setUseStartTime(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("endDateTimeRequired")) {
				study.getStudyParameterConfig().setEndDateTimeRequired(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("useEndTime")) {
				study.getStudyParameterConfig().setUseEndTime(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("startDateTimeLabel")) {
				study.getStudyParameterConfig().setStartDateTimeLabel(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("endDateTimeLabel")) {
				study.getStudyParameterConfig().setEndDateTimeLabel(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("markImportedCRFAsCompleted")) {
				study.getStudyParameterConfig().setMarkImportedCRFAsCompleted(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("allowSdvWithOpenQueries")) {
                study.getStudyParameterConfig().setAllowSdvWithOpenQueries(spvb.getValue());
            } else if (parameter.equalsIgnoreCase("replaceExisitingDataDuringImport")) {
                study.getStudyParameterConfig().setReplaceExisitingDataDuringImport(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("allowCodingVerification")) {
				study.getStudyParameterConfig().setReplaceExisitingDataDuringImport(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("defaultMedicalCodingDictionary")) {
				study.getStudyParameterConfig().setDefaultMedicalCodingDictionary(spvb.getValue());
			}
		}
		return study;

	}

	public StudyBean setParametersForSite(StudyBean site) {
		StudyDAO sdao = new StudyDAO(ds);
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(ds);
		StudyBean parent = (StudyBean) sdao.findByPK(site.getParentStudyId());
		parent = this.setParameterValuesForStudy(parent);
		site.setStudyParameterConfig(parent.getStudyParameterConfig());
		ArrayList siteParameters = spvdao.findAllParameterValuesByStudy(site);

		for (int i = 0; i < siteParameters.size(); i++) {
			StudyParameterValueBean spvb = (StudyParameterValueBean) siteParameters.get(i);
			String parameter = spvb.getParameter();
			if (parameter.equalsIgnoreCase("collectDob")) {
				site.getStudyParameterConfig().setCollectDob(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("genderRequired")) {
				site.getStudyParameterConfig().setGenderRequired(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("subjectPersonIdRequired")) {
				site.getStudyParameterConfig().setSubjectPersonIdRequired(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("discrepancyManagement")) {
				site.getStudyParameterConfig().setDiscrepancyManagement(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("subjectIdGeneration")) {
				site.getStudyParameterConfig().setSubjectIdGeneration(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("subjectIdPrefixSuffix")) {
				site.getStudyParameterConfig().setSubjectIdPrefixSuffix(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("interviewerNameRequired")) {
				site.getStudyParameterConfig().setInterviewerNameRequired(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("interviewerNameDefault")) {
				site.getStudyParameterConfig().setInterviewerNameDefault(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("interviewerNameEditable")) {
				site.getStudyParameterConfig().setInterviewerNameEditable(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("interviewDateRequired")) {
				site.getStudyParameterConfig().setInterviewDateRequired(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("interviewDateDefault")) {
				site.getStudyParameterConfig().setInterviewDateDefault(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("interviewDateEditable")) {
				site.getStudyParameterConfig().setInterviewDateEditable(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("personIdShownOnCRF")) {
				site.getStudyParameterConfig().setPersonIdShownOnCRF(spvb.getValue());

			} else if (parameter.equalsIgnoreCase("adminForcedReasonForChange")) {
				site.getStudyParameterConfig().setAdminForcedReasonForChange(spvb.getValue());
			}

			else if (parameter.equalsIgnoreCase("secondaryIdRequired")) {
				site.getStudyParameterConfig().setSecondaryIdRequired(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("dateOfEnrollmentForStudyRequired")) {
				site.getStudyParameterConfig().setDateOfEnrollmentForStudyRequired(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("studySubjectIdLabel")) {
				site.getStudyParameterConfig().setStudySubjectIdLabel(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("secondaryIdLabel")) {
				site.getStudyParameterConfig().setSecondaryIdLabel(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("dateOfEnrollmentForStudyLabel")) {
				site.getStudyParameterConfig().setDateOfEnrollmentForStudyLabel(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("genderLabel")) {
				site.getStudyParameterConfig().setGenderLabel(spvb.getValue());
			}
			else if (parameter.equalsIgnoreCase("startDateTimeRequired")) {
				site.getStudyParameterConfig().setStartDateTimeRequired(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("useStartTime")) {
				site.getStudyParameterConfig().setUseStartTime(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("endDateTimeRequired")) {
				site.getStudyParameterConfig().setEndDateTimeRequired(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("useEndTime")) {
				site.getStudyParameterConfig().setUseEndTime(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("startDateTimeLabel")) {
				site.getStudyParameterConfig().setStartDateTimeLabel(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("endDateTimeLabel")) {
				site.getStudyParameterConfig().setEndDateTimeLabel(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("markImportedCRFAsCompleted")) {
				site.getStudyParameterConfig().setMarkImportedCRFAsCompleted(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("allowSdvWithOpenQueries")) {
                site.getStudyParameterConfig().setAllowSdvWithOpenQueries(spvb.getValue());
            } else if (parameter.equalsIgnoreCase("replaceExisitingDataDuringImport")) {
                site.getStudyParameterConfig().setReplaceExisitingDataDuringImport(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("allowCodingVerification")) {
				site.getStudyParameterConfig().setReplaceExisitingDataDuringImport(spvb.getValue());
			} else if (parameter.equalsIgnoreCase("defaultMedicalCodingDictionary")) {
				site.getStudyParameterConfig().setDefaultMedicalCodingDictionary(spvb.getValue());
			}
		}
		return site;
	}
}
