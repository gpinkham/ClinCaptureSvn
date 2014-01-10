/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.bean.service;

import static org.junit.Assert.*;

import org.akaza.openclinica.bean.service.StudyParameterConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StudyParameterConfigTest {

	private StudyParameterConfig config;

	@Before
	public void setUp() throws Exception {
		config = new StudyParameterConfig();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAllDefaultValues() {
		assertEquals("1", config.getCollectDob());
		assertEquals("true", config.getDiscrepancyManagement());
		assertEquals("true", config.getGenderRequired());
		assertEquals("required", config.getSubjectPersonIdRequired());
		assertEquals("not_used", config.getInterviewerNameRequired());

		assertEquals("blank", config.getInterviewerNameDefault());
		assertEquals("true", config.getInterviewDateEditable());
		assertEquals("not_used", config.getInterviewDateRequired());
		assertEquals("blank", config.getInterviewDateDefault());
		assertEquals("true", config.getInterviewDateEditable());
		assertEquals("manual", config.getSubjectIdGeneration());
		assertEquals("true", config.getSubjectIdPrefixSuffix());
		assertEquals("false", config.getPersonIdShownOnCRF());
		assertEquals("false", config.getSecondaryLabelViewable());
		assertEquals("true", config.getAdminForcedReasonForChange());
		assertEquals("not_used", config.getEventLocationRequired());
		assertEquals("no", config.getSecondaryIdRequired());
		assertEquals("yes", config.getDateOfEnrollmentForStudyRequired());

		assertEquals("Study Subject ID", config.getStudySubjectIdLabel());
		assertEquals("Secondary ID", config.getSecondaryIdLabel());
		assertEquals("Date of Enrollment for Study", config.getDateOfEnrollmentForStudyLabel());
		assertEquals("Sex", config.getGenderLabel());
		assertEquals("yes", config.getStartDateTimeRequired());
		assertEquals("yes", config.getUseStartTime());
		assertEquals("no", config.getEndDateTimeRequired());
		assertEquals("yes", config.getUseEndTime());

		assertEquals("Start Date/Time", config.getStartDateTimeLabel());
		assertEquals("End Date/Time", config.getEndDateTimeLabel());
		assertEquals("no", config.getMarkImportedCRFAsCompleted());
		assertEquals("no", config.getAutoScheduleEventDuringImport());
        assertEquals("no", config.getAutoCreateSubjectDuringImport());

        assertEquals("no", config.getMedicalCodingApprovalNeeded());
        assertEquals("no", config.getMedicalCodingContextNeeded());
        assertEquals("no", config.getAllowCodingVerification());


	}
}
