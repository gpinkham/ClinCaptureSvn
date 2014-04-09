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
import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class StudyEventDefinitionDAOTest extends DefaultAppContextTest {

	@Test
	public void testFindAllActiveNotClassGroupedByStudyId() throws OpenClinicaException {
		int studyId = 1;
		ArrayList<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllActiveNotClassGroupedByStudyId(studyId);
		assertEquals(3, result.size());
	}

	@Test	
	public void testFindAllActiveBySubjectAndStudyId() throws OpenClinicaException {
		int studyId = 1;
		StudySubjectBean ssb = new StudySubjectBean();
		ssb.setDynamicGroupClassId(1);
		ArrayList<StudyEventDefinitionBean> result = studyEventDefinitionDAO.findAllActiveBySubjectAndStudyId(ssb,
				studyId);
		assertEquals(6, result.size());
	}

	@Test
	public void testFindAllActiveBySubjectFromActiveDynGroupAndStudyId_1() throws OpenClinicaException {
		int studyId = 1;
		StudySubjectBean ssb = new StudySubjectBean();
		ssb.setDynamicGroupClassId(1);
		ArrayList<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllActiveBySubjectFromActiveDynGroupAndStudyId(ssb, studyId);
		assertEquals(6, result.size());
	}

	@Test
	public void testFindAllActiveBySubjectFromActiveDynGroupAndStudyId_2() throws OpenClinicaException {
		int studyId = 1;
		StudySubjectBean ssb = new StudySubjectBean();
		ssb.setDynamicGroupClassId(2);
		ArrayList<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllActiveBySubjectFromActiveDynGroupAndStudyId(ssb, studyId);
		assertEquals(8, result.size());
	}

	@Test
	public void testGetEventNamesFromStudyNotReturnNull() throws OpenClinicaException {
		int studyId = 1;
		List<String> result = studyEventDefinitionDAO.getEventNamesFromStudy(studyId);
		assertNotNull(result);
	}
	
	@Test
	public void testGetEventNamesFromStudyCorrectSize() throws OpenClinicaException {
		int studyId = 1;
		List<String> result = studyEventDefinitionDAO.getEventNamesFromStudy(studyId);
		assertEquals(9, result.size());
	}
	
	@Test
	public void testFindAllAvailableByStudy_excludingEventDefinitionsRemoved() throws OpenClinicaException {
		final int studyId = 1;
		final int expactedSize = 6;
		StudyBean sb = new StudyBean();
		sb.setId(studyId);
		ArrayList<StudyEventDefinitionBean> result = studyEventDefinitionDAO.findAllAvailableByStudy(sb);
		assertEquals(expactedSize, result.size());
	}
		
	@Test
	public void testFindAllAvailableAndOrderedByStudyGroupClassId_excludingEventDefinitionsRemoved_Test1() throws OpenClinicaException {
		final int studyGroupClassId = 1;
		final int expactedSize = 2;
		ArrayList<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllAvailableAndOrderedByStudyGroupClassId(studyGroupClassId);
		assertEquals(expactedSize, result.size());
	}	
		
	@Test
	public void testFindAllAvailableAndOrderedByStudyGroupClassId_excludingEventDefinitionsRemoved_Test2() throws OpenClinicaException {
		final int studyGroupClassId = 2;
		final int expactedSize = 3;
		ArrayList<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllAvailableAndOrderedByStudyGroupClassId(studyGroupClassId);
		assertEquals(expactedSize, result.size());
	}	
}
