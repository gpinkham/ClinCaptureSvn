/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.control.form.FormProcessor;

import com.clinovo.enums.discrepancy.DiscrepancyCloseDescriptions;
import com.clinovo.enums.discrepancy.DiscrepancyConstants;
import com.clinovo.enums.discrepancy.DiscrepancyUpdateDescriptions;
import com.clinovo.enums.discrepancy.DiscrepancyVisibility;
import com.clinovo.enums.discrepancy.ReasonForChangeDescriptions;
import com.clinovo.enums.study.StudyConfigurationParameter;
import com.clinovo.enums.study.StudyFacility;
import com.clinovo.enums.study.StudyFeature;
import com.clinovo.enums.study.StudyParameter;
import com.clinovo.model.DiscrepancyDescription;
import com.clinovo.model.DiscrepancyDescriptionType;

/**
 * StudyUtil.
 */
public final class StudyUtil {

	public static final String ID = "Id";
	public static final String ERROR = "Error";

	private StudyUtil() {
	}

	/**
	 * Returns study features map.
	 * 
	 * @return Map
	 */
	public static Map<String, String> getStudyFeaturesMap() {
		Map<String, String> map = new HashMap<String, String>();
		FormProcessor fp = new FormProcessor(RequestUtil.getRequest());
		for (StudyFeature studyFeature : StudyFeature.values()) {
			String featureName = studyFeature.getName();
			map.put(featureName, fp.getString(featureName));
		}
		return map;
	}

	/**
	 * Returns study facilities map.
	 *
	 * @return Map
	 */
	public static Map<String, String> getStudyFacilitiesMap() {
		Map<String, String> map = new HashMap<String, String>();
		FormProcessor fp = new FormProcessor(RequestUtil.getRequest());
		for (StudyFacility studyFacility : StudyFacility.values()) {
			String facilityName = studyFacility.getName();
			map.put(facilityName, fp.getString(facilityName));
		}
		return map;
	}

	/**
	 * Returns study parameters map.
	 * 
	 * @return Map
	 */
	public static Map<String, String> getStudyParametersMap() {
		Map<String, String> map = new HashMap<String, String>();
		FormProcessor fp = new FormProcessor(RequestUtil.getRequest());
		for (StudyParameter studyParameter : StudyParameter.values()) {
			String parameterName = studyParameter.getName();
			map.put(parameterName, fp.getString(parameterName));
		}
		return map;
	}

	/**
	 * Returns study configuration parameters map.
	 * 
	 * @return Map
	 */
	public static Map<String, String> getStudyConfigurationParametersMap() {
		HttpServletRequest request = RequestUtil.getRequest();
		FormProcessor fp = new FormProcessor(request);
		Map<String, String> map = new HashMap<String, String>();
		for (StudyConfigurationParameter studyConfigurationParameter : StudyConfigurationParameter.values()) {
			String parameterName = studyConfigurationParameter.getName();
			if (request.getParameter(parameterName) != null) {
				map.put(parameterName, fp.getString(parameterName));
			}
		}
		return map;
	}

	/**
	 * Prepares discrepancy descriptions map.
	 *
	 * @param dnDescriptionsMap
	 *            Map
	 * @param studyId
	 *            int
	 * @param indexableParameters
	 *            boolean
	 */
	public static void prepareDiscrepancyDescriptions(Map<String, List<DiscrepancyDescription>> dnDescriptionsMap,
			int studyId, boolean indexableParameters) {
		dnDescriptionsMap.clear();
		prepareDiscrepancyDescription(dnDescriptionsMap, DiscrepancyConstants.DN_UPDATE_DESCRIPTIONS,
				DiscrepancyUpdateDescriptions.DN_UPDATE_DESCRIPTION.getName(),
				DiscrepancyUpdateDescriptions.DN_UPDATE_VISIBILITY_LEVEL.getName(),
				DiscrepancyDescriptionType.DescriptionType.UPDATE_DESCRIPTION.getId(), studyId, indexableParameters);
		prepareDiscrepancyDescription(dnDescriptionsMap, DiscrepancyConstants.DN_CLOSE_DESCRIPTIONS,
				DiscrepancyCloseDescriptions.DN_CLOSE_DESCRIPTION.getName(),
				DiscrepancyCloseDescriptions.DN_CLOSE_VISIBILITY_LEVEL.getName(),
				DiscrepancyDescriptionType.DescriptionType.CLOSE_DESCRIPTION.getId(), studyId, indexableParameters);
		prepareDiscrepancyDescription(dnDescriptionsMap, DiscrepancyConstants.DN_RFC_DESCRIPTIONS,
				ReasonForChangeDescriptions.DN_RFC_DESCRIPTION.getName(),
				ReasonForChangeDescriptions.DN_RFC_VISIBILITY_LEVEL.getName(),
				DiscrepancyDescriptionType.DescriptionType.RFC_DESCRIPTION.getId(), studyId, indexableParameters);
	}

	/**
	 * Prepares discrepancy description map.
	 *
	 * @param dnDescriptionsMap
	 *            Map
	 * @param mapKey
	 *            String
	 * @param descriptionParameterName
	 *            String
	 * @param visibilityParameterName
	 *            String
	 * @param typeId
	 *            int
	 * @param studyId
	 *            int
	 * @param indexableParameters
	 *            boolean
	 */
	private static void prepareDiscrepancyDescription(Map<String, List<DiscrepancyDescription>> dnDescriptionsMap,
			String mapKey, String descriptionParameterName, String visibilityParameterName, int typeId, int studyId,
			boolean indexableParameters) {
		HttpServletRequest request = RequestUtil.getRequest();
		FormProcessor fp = new FormProcessor(request);
		List<DiscrepancyDescription> discrepancyDescriptions = new ArrayList<DiscrepancyDescription>();
		if (indexableParameters) {
			dnDescriptionsMap.put(mapKey, discrepancyDescriptions);
			for (int i = DiscrepancyConstants.MIN_DESCRIPTIONS; i < DiscrepancyConstants.MAX_DESCRIPTIONS; i++) {
				String description = fp.getString(descriptionParameterName.concat(Integer.toString(i)));
				int discrepancyDescriptionId = fp
						.getInt(descriptionParameterName.concat(ID).concat(Integer.toString(i)));
				int visibilityLevel = fp.getInt(visibilityParameterName.concat(Integer.toString(i)));
				if (!description.isEmpty()) {
					DiscrepancyDescription discrepancyDescription = new DiscrepancyDescription();
					discrepancyDescription.setTypeId(typeId);
					discrepancyDescription.setParameterName(descriptionParameterName.concat(Integer.toString(i)));
					discrepancyDescription
							.setParameterErrorName(descriptionParameterName.concat(ERROR).concat(Integer.toString(i)));
					discrepancyDescription.setName(description);
					switch (visibilityLevel) {
						case 1 :
							discrepancyDescription.setVisibilityLevel(DiscrepancyVisibility.STUDY.getValue());
							break;
						case 2 :
							discrepancyDescription.setVisibilityLevel(DiscrepancyVisibility.SITE.getValue());
							break;
						default :
							discrepancyDescription.setVisibilityLevel(DiscrepancyVisibility.BOTH.getValue());
					}
					if (discrepancyDescriptionId != 0) {
						discrepancyDescription.setId(discrepancyDescriptionId);
					}
					discrepancyDescription.setStudyId(studyId);
					discrepancyDescriptions.add(discrepancyDescription);
				}
			}
		} else {
			String[] descriptionValues = request.getParameterValues(descriptionParameterName);
			String[] visibilityValues = request.getParameterValues(visibilityParameterName);
			if (descriptionValues != null && visibilityValues != null) {
				dnDescriptionsMap.put(mapKey, discrepancyDescriptions);
				for (int index = 0; index < descriptionValues.length; index++) {
					String description = descriptionValues[index];
					String visibilityLevel = visibilityValues[index];
					if (!description.isEmpty()) {
						DiscrepancyDescription discrepancyDescription = new DiscrepancyDescription();
						discrepancyDescription.setStudyId(studyId);
						discrepancyDescription.setTypeId(typeId);
						discrepancyDescription.setName(description);
						discrepancyDescription.setVisibilityLevel(visibilityLevel);
						discrepancyDescription.setParameterName(descriptionParameterName);
						discrepancyDescription.setParameterErrorName(descriptionParameterName);
						discrepancyDescriptions.add(discrepancyDescription);
					}
				}
			}
		}
	}
}
