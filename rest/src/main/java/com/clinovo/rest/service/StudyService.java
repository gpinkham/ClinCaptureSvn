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

import java.util.HashMap;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinovo.rest.annotation.RestParameterPossibleValues;
import com.clinovo.rest.annotation.RestParameterPossibleValuesHolder;
import com.clinovo.rest.service.base.BaseStudyService;
import com.clinovo.rest.util.ValidatorUtil;
import com.clinovo.validator.StudyValidator;

/**
 * StudyService.
 */
@RestController("restStudyService")
@SuppressWarnings({"unused", "rawtypes"})
public class StudyService extends BaseStudyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StudyService.class);

	@Autowired
	private ConfigurationDao configurationDao;

	@RestParameterPossibleValuesHolder({
			@RestParameterPossibleValues(name = "protocolType", canBeNotSpecified = true, values = "0,1", valueDescriptions = "rest.protocolType.valueDescription"),
			@RestParameterPossibleValues(name = "crfAnnotation", canBeNotSpecified = true, values = "yes,no", valueDescriptions = "rest.yesNo.valueDescription"),
			@RestParameterPossibleValues(name = "dynamicGroup", canBeNotSpecified = true, values = "yes,no", valueDescriptions = "rest.yesNo.valueDescription"),
			@RestParameterPossibleValues(name = "calendaredVisits", canBeNotSpecified = true, values = "yes,no", valueDescriptions = "rest.yesNo.valueDescription"),
			@RestParameterPossibleValues(name = "interactiveDashboards", canBeNotSpecified = true, values = "yes,no", valueDescriptions = "rest.yesNo.valueDescription"),
			@RestParameterPossibleValues(name = "itemLevelSDV", canBeNotSpecified = true, values = "yes,no", valueDescriptions = "rest.yesNo.valueDescription"),
			@RestParameterPossibleValues(name = "subjectCasebookInPDF", canBeNotSpecified = true, values = "yes,no", valueDescriptions = "rest.yesNo.valueDescription"),
			@RestParameterPossibleValues(name = "crfMasking", canBeNotSpecified = true, values = "yes,no", valueDescriptions = "rest.yesNo.valueDescription"),
			@RestParameterPossibleValues(name = "sasExtracts", canBeNotSpecified = true, values = "yes,no", valueDescriptions = "rest.yesNo.valueDescription"),
			@RestParameterPossibleValues(name = "studyEvaluator", canBeNotSpecified = true, values = "yes,no", valueDescriptions = "rest.yesNo.valueDescription"),
			@RestParameterPossibleValues(name = "randomization", canBeNotSpecified = true, values = "yes,no", valueDescriptions = "rest.yesNo.valueDescription"),
			@RestParameterPossibleValues(name = "medicalCoding", canBeNotSpecified = true, values = "yes,no", valueDescriptions = "rest.yesNo.valueDescription")})
	@RequestMapping(value = "/study/create", method = RequestMethod.POST)
	public StudyBean createStudy(@RequestParam("studyName") String studyName,
			@RequestParam("protocolId") String protocolId,
			@RequestParam(value = "protocolType", defaultValue = "0") int protocolType,
			@RequestParam(value = "secondProId", required = false, defaultValue = "") String secondProId,
			@RequestParam(value = "officialTitle", required = false, defaultValue = "") String officialTitle,
			@RequestParam("summary") String summary,
			@RequestParam(value = "description", required = false, defaultValue = "") String description,
				@RequestParam("principalInvestigator") String principalInvestigator,
			@RequestParam("sponsor") String sponsor,
			@RequestParam(value = "collaborators", required = false, defaultValue = "") String collaborators,
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
		StudyBean studyBean = prepareStudyBean();

		HashMap errors = StudyValidator.validate(getStudyDAO(), configurationDao);

		ValidatorUtil.checkForErrors(errors);

		return saveStudyBean(userName, studyBean);
	}
}