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

import java.util.ArrayList;
import java.util.List;

import com.clinovo.enums.study.StudyAllocation;
import com.clinovo.enums.study.StudyAssignment;
import com.clinovo.enums.study.StudyControl;
import com.clinovo.enums.study.StudyDuration;
import com.clinovo.enums.study.StudyEndPoint;
import com.clinovo.enums.study.StudyFacRecruitStatus;
import com.clinovo.enums.study.StudyMasking;
import com.clinovo.enums.study.StudyPhase;
import com.clinovo.enums.study.StudyPurpose;
import com.clinovo.enums.study.StudySelection;
import com.clinovo.enums.study.StudyTiming;

/**
 * MapsHolder.
 */
public class MapsHolder {

	private List<StudyFacRecruitStatus> facRecruitStatusList = new ArrayList<StudyFacRecruitStatus>();
	private List<StudyPhase> studyPhaseList = new ArrayList<StudyPhase>();
	private List<StudyPurpose> interPurposeList = new ArrayList<StudyPurpose>();
	private List<StudyAllocation> allocationList = new ArrayList<StudyAllocation>();
	private List<StudyMasking> maskingList = new ArrayList<StudyMasking>();
	private List<StudyControl> controlList = new ArrayList<StudyControl>();
	private List<StudyAssignment> assignmentList = new ArrayList<StudyAssignment>();
	private List<StudyEndPoint> endPointList = new ArrayList<StudyEndPoint>();
	private List<StudyPurpose> obserPurposeList = new ArrayList<StudyPurpose>();
	private List<StudyDuration> durationList = new ArrayList<StudyDuration>();
	private List<StudySelection> selectionList = new ArrayList<StudySelection>();
	private List<StudyTiming> timingList = new ArrayList<StudyTiming>();

	public List<StudyFacRecruitStatus> getFacRecruitStatusList() {
		return facRecruitStatusList;
	}

	public List<StudyPhase> getStudyPhaseList() {
		return studyPhaseList;
	}

	public List<StudyPurpose> getInterPurposeList() {
		return interPurposeList;
	}

	public List<StudyAllocation> getAllocationList() {
		return allocationList;
	}

	public List<StudyMasking> getMaskingList() {
		return maskingList;
	}

	public List<StudyControl> getControlList() {
		return controlList;
	}

	public List<StudyAssignment> getAssignmentList() {
		return assignmentList;
	}

	public List<StudyEndPoint> getEndPointList() {
		return endPointList;
	}

	public List<StudyPurpose> getObserPurposeList() {
		return obserPurposeList;
	}

	public List<StudySelection> getSelectionList() {
		return selectionList;
	}

	public List<StudyTiming> getTimingList() {
		return timingList;
	}

	public List<StudyDuration> getDurationList() {
		return durationList;
	}
}
