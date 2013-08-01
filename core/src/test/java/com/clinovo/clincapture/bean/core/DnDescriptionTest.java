/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 * If not, see <http://www.gnu.org/licenses/>.  Updated by Clinovo Inc on 05/19/2013.
 * 
 ******************************************************************************/
package com.clinovo.clincapture.bean.core;

import static org.junit.Assert.*;

import org.akaza.openclinica.bean.core.DnDescription;

import org.junit.Test;

public class DnDescriptionTest {
	
	private DnDescription dnDescription;
	
	@Test
	public void getNameTest() {
		dnDescription = new DnDescription();
		assertEquals("", dnDescription.getName());
	}
	
	@Test 
	public void isSiteVisibleTest() {
		dnDescription = new DnDescription();
		assertEquals("", dnDescription.getVisibilityLevel());
	}
	
	@Test
	public void getDescriptionTest() {
		dnDescription = new DnDescription();
		assertEquals("", dnDescription.getDescription());
	}

}
