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

package com.clinovo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.clinovo.enums.discrepancy.DiscrepancyVisibility;
import org.junit.Test;

public class DiscrepancyDescriptionTest {

	@Test
	public void testThatNewDDescriptionHasUnknownType() {
		assertEquals(DiscrepancyDescriptionType.DescriptionType.UNKNOWN.getId(), new DiscrepancyDescription().getTypeId());
	}

	@Test
	public void testThatDDescriptionPersistsAChangedType() {
		DiscrepancyDescription item = new DiscrepancyDescription();
		item.setTypeId(DiscrepancyDescriptionType.DescriptionType.RFC_DESCRIPTION.getId());
		
		assertEquals(DiscrepancyDescriptionType.DescriptionType.RFC_DESCRIPTION.getId(), item.getTypeId());
	}
	
	@Test
	public void testThatForNewDDescriptionVisibilityLevelIsEmpty() {
		assertTrue(new DiscrepancyDescription().getVisibilityLevel().isEmpty());
	}
	
	@Test
	public void testThatSetVisibilityLevelWorksCorrect() {
		DiscrepancyDescription item = new DiscrepancyDescription();
		item.setVisibilityLevel(DiscrepancyVisibility.STUDY.getName());
		
		assertEquals(DiscrepancyVisibility.STUDY.getName(), item.getVisibilityLevel());
	}
}

