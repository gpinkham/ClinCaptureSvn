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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.control.form.FormProcessor;

import com.clinovo.enums.study.StudyConfigurationParameter;
import com.clinovo.enums.study.StudyFacility;
import com.clinovo.enums.study.StudyFeature;
import com.clinovo.enums.study.StudyParameter;

/**
 * StudyUtil.
 */
public final class StudyUtil {

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
}
