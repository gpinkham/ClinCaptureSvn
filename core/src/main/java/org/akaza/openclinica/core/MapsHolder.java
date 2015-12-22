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
package org.akaza.openclinica.core;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * MapsHolder.
 */
public class MapsHolder {

	private HashMap<String, String> facRecruitStatusMap = new LinkedHashMap<String, String>();
	private HashMap<String, String> studyPhaseMap = new LinkedHashMap<String, String>();
	private HashMap<String, String> interPurposeMap = new LinkedHashMap<String, String>();
	private HashMap<String, String> allocationMap = new LinkedHashMap<String, String>();
	private HashMap<String, String> maskingMap = new LinkedHashMap<String, String>();
	private HashMap<String, String> controlMap = new LinkedHashMap<String, String>();
	private HashMap<String, String> assignmentMap = new LinkedHashMap<String, String>();
	private HashMap<String, String> endpointMap = new LinkedHashMap<String, String>();
	private HashMap<String, String> interTypeMap = new LinkedHashMap<String, String>();
	private HashMap<String, String> obserPurposeMap = new LinkedHashMap<String, String>();
	private HashMap<String, String> selectionMap = new LinkedHashMap<String, String>();

	public HashMap<String, String> getTimingMap() {
		return timingMap;
	}

	public HashMap<String, String> getFacRecruitStatusMap() {
		return facRecruitStatusMap;
	}

	public HashMap<String, String> getStudyPhaseMap() {
		return studyPhaseMap;
	}

	public HashMap<String, String> getInterPurposeMap() {
		return interPurposeMap;
	}

	public HashMap<String, String> getAllocationMap() {
		return allocationMap;
	}

	public HashMap<String, String> getMaskingMap() {
		return maskingMap;
	}

	public HashMap<String, String> getControlMap() {
		return controlMap;
	}

	public HashMap<String, String> getAssignmentMap() {
		return assignmentMap;
	}

	public HashMap<String, String> getEndpointMap() {
		return endpointMap;
	}

	public HashMap<String, String> getInterTypeMap() {
		return interTypeMap;
	}

	public HashMap<String, String> getObserPurposeMap() {
		return obserPurposeMap;
	}

	public HashMap<String, String> getSelectionMap() {
		return selectionMap;
	}

	private HashMap<String, String> timingMap = new LinkedHashMap<String, String>();
}
