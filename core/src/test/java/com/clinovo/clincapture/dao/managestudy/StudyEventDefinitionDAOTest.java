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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.dao.managestudy;

import java.util.ArrayList;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class StudyEventDefinitionDAOTest extends DefaultAppContextTest {

	@Test
	@SuppressWarnings("unchecked")
	public void testFindAllActiveNotClassGroupedByStudyId() throws OpenClinicaException {
		int studyId = 1;
		ArrayList<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllActiveNotClassGroupedByStudyId(studyId);
		assertEquals(3, result.size());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindAllActiveBySubjectAndStudyId() throws OpenClinicaException {
		int studyId = 1;
		StudySubjectBean ssb = new StudySubjectBean();
		ssb.setDynamicGroupClassId(1);
		ArrayList<StudyEventDefinitionBean> result = studyEventDefinitionDAO.findAllActiveBySubjectAndStudyId(ssb,
				studyId);
		assertEquals(3, result.size());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindAllActiveBySubjectFromActiveDynGroupAndStudyId_1() throws OpenClinicaException {
		int studyId = 1;
		StudySubjectBean ssb = new StudySubjectBean();
		ssb.setDynamicGroupClassId(1);
		ArrayList<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllActiveBySubjectFromActiveDynGroupAndStudyId(ssb, studyId);
		assertEquals(3, result.size());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindAllActiveBySubjectFromActiveDynGroupAndStudyId_2() throws OpenClinicaException {
		int studyId = 1;
		StudySubjectBean ssb = new StudySubjectBean();
		ssb.setDynamicGroupClassId(3);
		ArrayList<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllActiveBySubjectFromActiveDynGroupAndStudyId(ssb, studyId);
		assertEquals(3, result.size());
	}

}
