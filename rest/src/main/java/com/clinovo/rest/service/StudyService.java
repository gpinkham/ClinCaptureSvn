/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.rest.service;

import java.util.Date;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinovo.rest.annotation.PossibleValues;
import com.clinovo.rest.annotation.PossibleValuesHolder;
import com.clinovo.rest.annotation.ProvideAtLeastOneNotRequired;
import com.clinovo.rest.service.base.BaseStudyService;

/**
 * StudyService.
 */
@RestController("restStudyService")
@SuppressWarnings({"unused"})
public class StudyService extends BaseStudyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StudyService.class);

	@Autowired
	private com.clinovo.service.StudyService studyService;

	/**
	 * Create study.
	 *
	 * @param studyName
	 *            String
	 * @param briefTitle
	 *            String
	 * @param protocolId
	 *            int
	 * @param protocolType
	 *            String
	 * @param secondProId
	 *            String
	 * @param officialTitle
	 *            String
	 * @param summary
	 *            String
	 * @param description
	 *            String
	 * @param principalInvestigator
	 *            String
	 * @param sponsor
	 *            String
	 * @param collaborators
	 *            String
	 * @param phase
	 *            int
	 * @param startDate
	 *            String
	 * @param approvalDate
	 *            String
	 * @param endDate
	 *            String
	 * @param totalEnrollment
	 *            int
	 * @param purpose
	 *            int
	 * @param duration
	 *            Integer
	 * @param selection
	 *            Integer
	 * @param timing
	 *            Integer
	 * @param allocation
	 *            Integer
	 * @param masking
	 *            Integer
	 * @param control
	 *            Integer
	 * @param assignment
	 *            Integer
	 * @param endPoint
	 *            Integer
	 * @param userName
	 *            String
	 * @param crfAnnotation
	 *            String
	 * @param dynamicGroup
	 *            String
	 * @param calendaredVisits
	 *            String
	 * @param interactiveDashboards
	 *            String
	 * @param itemLevelSDV
	 *            String
	 * @param subjectCasebookInPDF
	 *            String
	 * @param crfMasking
	 *            String
	 * @param sasExtracts
	 *            String
	 * @param studyEvaluator
	 *            String
	 * @param randomization
	 *            String
	 * @param medicalCoding
	 *            String
	 * @return StudyBean
	 * @throws Exception
	 *             an Exception
	 */
	@PossibleValuesHolder({
			@PossibleValues(name = "protocolType", values = "0,1", valueDescriptions = "rest.protocolType.valueDescription"),
			@PossibleValues(name = "phase", values = "0,1,2,3,4,5,6,7,8,9,10,11,12,13", valueDescriptions = "rest.phase.valueDescription"),
			@PossibleValues(name = "purpose", values = "rest.purpose.{#}.values", valueDescriptions = "rest.purpose.{#}.valueDescription", dependentOn = "protocolType"),
			@PossibleValues(name = "duration", canBeNotSpecified = true, values = "0,1,2", valueDescriptions = "rest.duration.valueDescription"),
			@PossibleValues(name = "selection", canBeNotSpecified = true, values = "0,1,2,3,4", valueDescriptions = "rest.selection.valueDescription"),
			@PossibleValues(name = "timing", canBeNotSpecified = true, values = "0,1,2", valueDescriptions = "rest.timing.valueDescription"),
			@PossibleValues(name = "allocation", canBeNotSpecified = true, values = "0,1,2,3", valueDescriptions = "rest.allocation.valueDescription"),
			@PossibleValues(name = "masking", canBeNotSpecified = true, values = "0,1,2,3", valueDescriptions = "rest.masking.valueDescription"),
			@PossibleValues(name = "control", canBeNotSpecified = true, values = "0,1,2,3,4,5", valueDescriptions = "rest.control.valueDescription"),
			@PossibleValues(name = "assignment", canBeNotSpecified = true, values = "0,1,2,3,4,5", valueDescriptions = "rest.assignment.valueDescription"),
			@PossibleValues(name = "endPoint", canBeNotSpecified = true, values = "0,1,2,3,4,5,6,7,8", valueDescriptions = "rest.endPoint.valueDescription"),
			@PossibleValues(name = "crfAnnotation", values = "yes,no"),
			@PossibleValues(name = "dynamicGroup", values = "yes,no"),
			@PossibleValues(name = "calendaredVisits", values = "yes,no"),
			@PossibleValues(name = "interactiveDashboards", values = "yes,no"),
			@PossibleValues(name = "itemLevelSDV", values = "yes,no"),
			@PossibleValues(name = "subjectCasebookInPDF", values = "yes,no"),
			@PossibleValues(name = "crfMasking", values = "yes,no"),
			@PossibleValues(name = "sasExtracts", values = "yes,no"),
			@PossibleValues(name = "studyEvaluator", values = "yes,no"),
			@PossibleValues(name = "randomization", values = "yes,no"),
			@PossibleValues(name = "medicalCoding", values = "yes,no")})
	@RequestMapping(value = "/study/create", method = RequestMethod.POST)
	public StudyBean createStudy(@RequestParam(value = "studyName") String studyName,
			@RequestParam(value = "briefTitle", required = false, defaultValue = "") String briefTitle,
			@RequestParam("protocolId") String protocolId, @RequestParam(value = "protocolType") int protocolType,
			@RequestParam(value = "secondProId", required = false, defaultValue = "") String secondProId,
			@RequestParam(value = "officialTitle", required = false, defaultValue = "") String officialTitle,
			@RequestParam("summary") String summary,
			@RequestParam(value = "description", required = false, defaultValue = "") String description,
			@RequestParam("principalInvestigator") String principalInvestigator,
			@RequestParam("sponsor") String sponsor,
			@RequestParam(value = "collaborators", required = false, defaultValue = "") String collaborators,
			@RequestParam(value = "phase", required = false, defaultValue = "0") int phase,
			@RequestParam(value = "startDate") Date startDate,
			@RequestParam(value = "endDate", required = false) Date endDate,
			@RequestParam(value = "approvalDate", required = false) Date approvalDate,
			@RequestParam(value = "totalEnrollment") int totalEnrollment,
			@RequestParam(value = "purpose", required = false, defaultValue = "0") int purpose,
			@RequestParam(value = "duration", required = false) Integer duration,
			@RequestParam(value = "selection", required = false) Integer selection,
			@RequestParam(value = "timing", required = false) Integer timing,
			@RequestParam(value = "allocation", required = false) Integer allocation,
			@RequestParam(value = "masking", required = false) Integer masking,
			@RequestParam(value = "control", required = false) Integer control,
			@RequestParam(value = "assignment", required = false) Integer assignment,
			@RequestParam(value = "endPoint", required = false) Integer endPoint,
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "crfAnnotation", required = false, defaultValue = "yes") String crfAnnotation,
			@RequestParam(value = "dynamicGroup", required = false, defaultValue = "yes") String dynamicGroup,
			@RequestParam(value = "calendaredVisits", required = false, defaultValue = "yes") String calendaredVisits,
			@RequestParam(value = "interactiveDashboards", required = false, defaultValue = "yes") String interactiveDashboards,
			@RequestParam(value = "itemLevelSDV", required = false, defaultValue = "yes") String itemLevelSDV,
			@RequestParam(value = "subjectCasebookInPDF", required = false, defaultValue = "yes") String subjectCasebookInPDF,
			@RequestParam(value = "crfMasking", required = false, defaultValue = "yes") String crfMasking,
			@RequestParam(value = "sasExtracts", required = false, defaultValue = "yes") String sasExtracts,
			@RequestParam(value = "studyEvaluator", required = false, defaultValue = "yes") String studyEvaluator,
			@RequestParam(value = "randomization", required = false, defaultValue = "yes") String randomization,
			@RequestParam(value = "medicalCoding", required = false, defaultValue = "yes") String medicalCoding)
					throws Exception {
		return saveStudyBean(userName);
	}

	/**
	 * Edit study.
	 *
	 * @param studyId
	 *            int
	 * @param studyName
	 *            String
	 * @param briefTitle
	 *            String
	 * @param protocolId
	 *            Integer
	 * @param protocolType
	 *            String
	 * @param secondProId
	 *            String
	 * @param officialTitle
	 *            String
	 * @param summary
	 *            String
	 * @param description
	 *            String
	 * @param principalInvestigator
	 *            String
	 * @param sponsor
	 *            String
	 * @param collaborators
	 *            String
	 * @param phase
	 *            Integer
	 * @param startDate
	 *            String
	 * @param approvalDate
	 *            String
	 * @param endDate
	 *            String
	 * @param totalEnrollment
	 *            Integer
	 * @param purpose
	 *            Integer
	 * @param duration
	 *            Integer
	 * @param selection
	 *            Integer
	 * @param timing
	 *            Integer
	 * @param allocation
	 *            Integer
	 * @param masking
	 *            Integer
	 * @param control
	 *            Integer
	 * @param assignment
	 *            Integer
	 * @param endPoint
	 *            Integer
	 * @param crfAnnotation
	 *            String
	 * @param dynamicGroup
	 *            String
	 * @param calendaredVisits
	 *            String
	 * @param interactiveDashboards
	 *            String
	 * @param itemLevelSDV
	 *            String
	 * @param subjectCasebookInPDF
	 *            String
	 * @param crfMasking
	 *            String
	 * @param sasExtracts
	 *            String
	 * @param studyEvaluator
	 *            String
	 * @param randomization
	 *            String
	 * @param medicalCoding
	 *            String
	 * @return StudyBean
	 * @throws Exception
	 *             an Exception
	 */
	@ProvideAtLeastOneNotRequired
	@PossibleValuesHolder({
			@PossibleValues(name = "protocolType", canBeNotSpecified = true, values = "0,1", valueDescriptions = "rest.protocolType.valueDescription"),
			@PossibleValues(name = "phase", canBeNotSpecified = true, values = "0,1,2,3,4,5,6,7,8,9,10,11,12,13", valueDescriptions = "rest.phase.valueDescription"),
			@PossibleValues(name = "purpose", canBeNotSpecified = true, values = "rest.purpose.{#}.values", valueDescriptions = "rest.purpose.{#}.valueDescription", dependentOn = "protocolType"),
			@PossibleValues(name = "duration", canBeNotSpecified = true, values = "0,1,2", valueDescriptions = "rest.duration.valueDescription"),
			@PossibleValues(name = "selection", canBeNotSpecified = true, values = "0,1,2,3,4", valueDescriptions = "rest.selection.valueDescription"),
			@PossibleValues(name = "timing", canBeNotSpecified = true, values = "0,1,2", valueDescriptions = "rest.timing.valueDescription"),
			@PossibleValues(name = "allocation", canBeNotSpecified = true, values = "0,1,2,3", valueDescriptions = "rest.allocation.valueDescription"),
			@PossibleValues(name = "masking", canBeNotSpecified = true, values = "0,1,2,3", valueDescriptions = "rest.masking.valueDescription"),
			@PossibleValues(name = "control", canBeNotSpecified = true, values = "0,1,2,3,4,5", valueDescriptions = "rest.control.valueDescription"),
			@PossibleValues(name = "assignment", canBeNotSpecified = true, values = "0,1,2,3,4,5", valueDescriptions = "rest.assignment.valueDescription"),
			@PossibleValues(name = "endPoint", canBeNotSpecified = true, values = "0,1,2,3,4,5,6,7,8", valueDescriptions = "rest.endPoint.valueDescription"),
			@PossibleValues(name = "crfAnnotation", canBeNotSpecified = true, values = "yes,no"),
			@PossibleValues(name = "dynamicGroup", canBeNotSpecified = true, values = "yes,no"),
			@PossibleValues(name = "calendaredVisits", canBeNotSpecified = true, values = "yes,no"),
			@PossibleValues(name = "interactiveDashboards", canBeNotSpecified = true, values = "yes,no"),
			@PossibleValues(name = "itemLevelSDV", canBeNotSpecified = true, values = "yes,no"),
			@PossibleValues(name = "subjectCasebookInPDF", canBeNotSpecified = true, values = "yes,no"),
			@PossibleValues(name = "crfMasking", canBeNotSpecified = true, values = "yes,no"),
			@PossibleValues(name = "sasExtracts", canBeNotSpecified = true, values = "yes,no"),
			@PossibleValues(name = "studyEvaluator", canBeNotSpecified = true, values = "yes,no"),
			@PossibleValues(name = "randomization", canBeNotSpecified = true, values = "yes,no"),
			@PossibleValues(name = "medicalCoding", canBeNotSpecified = true, values = "yes,no")})
	@RequestMapping(value = "/study/edit", method = RequestMethod.POST)
	public StudyBean editStudy(@RequestParam("studyId") int studyId,
			@RequestParam(value = "studyName", required = false) String studyName,
			@RequestParam(value = "briefTitle", required = false) String briefTitle,
			@RequestParam(value = "protocolId", required = false) String protocolId,
			@RequestParam(value = "protocolType", required = false) Integer protocolType,
			@RequestParam(value = "secondProId", required = false) String secondProId,
			@RequestParam(value = "officialTitle", required = false) String officialTitle,
			@RequestParam(value = "summary", required = false) String summary,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "principalInvestigator", required = false) String principalInvestigator,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "collaborators", required = false) String collaborators,
			@RequestParam(value = "phase", required = false) Integer phase,
			@RequestParam(value = "startDate", required = false) Date startDate,
			@RequestParam(value = "endDate", required = false) Date endDate,
			@RequestParam(value = "approvalDate", required = false) Date approvalDate,
			@RequestParam(value = "totalEnrollment", required = false) Integer totalEnrollment,
			@RequestParam(value = "purpose", required = false) Integer purpose,
			@RequestParam(value = "duration", required = false) Integer duration,
			@RequestParam(value = "selection", required = false) Integer selection,
			@RequestParam(value = "timing", required = false) Integer timing,
			@RequestParam(value = "allocation", required = false) Integer allocation,
			@RequestParam(value = "masking", required = false) Integer masking,
			@RequestParam(value = "control", required = false) Integer control,
			@RequestParam(value = "assignment", required = false) Integer assignment,
			@RequestParam(value = "endPoint", required = false) Integer endPoint,
			@RequestParam(value = "crfAnnotation", required = false) String crfAnnotation,
			@RequestParam(value = "dynamicGroup", required = false) String dynamicGroup,
			@RequestParam(value = "calendaredVisits", required = false) String calendaredVisits,
			@RequestParam(value = "interactiveDashboards", required = false) String interactiveDashboards,
			@RequestParam(value = "itemLevelSDV", required = false) String itemLevelSDV,
			@RequestParam(value = "subjectCasebookInPDF", required = false) String subjectCasebookInPDF,
			@RequestParam(value = "crfMasking", required = false) String crfMasking,
			@RequestParam(value = "sasExtracts", required = false) String sasExtracts,
			@RequestParam(value = "studyEvaluator", required = false) String studyEvaluator,
			@RequestParam(value = "randomization", required = false) String randomization,
			@RequestParam(value = "medicalCoding", required = false) String medicalCoding) throws Exception {
		return updateStudyBean(studyId);
	}

	/**
	 * Method removes the study.
	 *
	 * @param studyId
	 *            int
	 * @return StudyBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/study/remove", method = RequestMethod.POST)
	public StudyBean remove(@RequestParam(value = "id") int studyId) throws Exception {
		return removeStudy(studyId);
	}

	/**
	 * Method restores the study.
	 *
	 * @param studyId
	 *            int
	 * @return StudyBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/study/restore", method = RequestMethod.POST)
	public StudyBean restore(@RequestParam(value = "id") int studyId) throws Exception {
		return restoreStudy(studyId);
	}
}