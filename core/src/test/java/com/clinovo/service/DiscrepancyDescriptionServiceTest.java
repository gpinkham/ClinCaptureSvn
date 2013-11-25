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

package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.junit.Test;

import com.clinovo.model.DiscrepancyDescription;

public class DiscrepancyDescriptionServiceTest extends DefaultAppContextTest {

	@Test
	public void testThatFindDDescriptionDoesNotReturnNull() {
		assertNotNull(discrepancyDescriptionService.findAllByStudyIdAndTypeId(1, 1));
	}

	@Test
	public void testThatFindDDescriptionReturnName() {
		assertEquals("test name", discrepancyDescriptionService.findById(2).getName());
	}

	@Test
	public void testThatFindDDescriptionReturnDescription() {
		assertEquals("Standard Protocol Deviation", discrepancyDescriptionService.findById(2).getDescription());
	}

	@Test
	public void testThatFindDDescriptionReturnStudiId() {
		assertEquals(1, discrepancyDescriptionService.findById(2).getStudyId());
	}
	
	@Test
	public void testThatFindDDescriptionReturnVisibilityLevel() {
		assertEquals("Study", discrepancyDescriptionService.findById(2).getVisibilityLevel());
	}
	
	@Test
	public void testThatFindDDescriptionReturnTypeId() {
		assertEquals(2, discrepancyDescriptionService.findById(2).getTypeId());
	}
	
	@Test
	public void testFindAllNotNull() {
		assertNotNull(discrepancyDescriptionService.findAll());
	}
	
	@Test
	public void testFindAll() {
		assertEquals(4, discrepancyDescriptionService.findAll().size());
	}
	
	@Test
	public void testSaveDiscrepancyDescription() {
		DiscrepancyDescription dDescription = discrepancyDescriptionService.findById(2);
		
		assertEquals("test name", discrepancyDescriptionService.saveDiscrepancyDescription(dDescription).getName());
	}
	
	@Test
	public void testFindAllSortedDescriptionsFromStudy() {
		
		assertEquals(3, discrepancyDescriptionService.findAllSortedDescriptionsFromStudy(1).size());
	}
	
	@Test
	public void testFindAllSortedDescriptionsFromStudyMoreDetailed() {
		
		assertEquals(1, discrepancyDescriptionService.findAllSortedDescriptionsFromStudy(1).get("dnUpdateDescriptions").size());
	}
	
	@Test
	public void testGetAssignedToStudySortedDescriptions() {
		StudyBean study = new StudyBean();
		study.setId(1);
		
		assertEquals(3, discrepancyDescriptionService.getAssignedToStudySortedDescriptions(study).size());
	}
	
	@Test
	public void testDeleteDiscrepancyDescriptionRemovesDDescriptionFromDB() {

		discrepancyDescriptionService.deleteDiscrepancyDescription(discrepancyDescriptionService.findById(3));
		assertEquals(3, discrepancyDescriptionService.findAll().size());
	}
}
