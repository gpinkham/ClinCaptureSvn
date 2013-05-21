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

package org.akaza.openclinica.dao.discrepancy;

import java.util.ArrayList;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.bean.core.DnDescription;
import org.junit.Test;

public class DnDescriptionDaoTest extends DefaultAppContextTest {
	
	@Test
	public void updateTest() throws OpenClinicaException {
		String updatedName = "updatedName";
		String updatedDesc = "updatedDesc";
		DnDescription term = (DnDescription) dnDescriptionDao.findByPK(1);
		term.setName(updatedName);
		term.setDescription(updatedDesc);
		assertEquals(1, term.getId());
		dnDescriptionDao.update(term);
		term = (DnDescription) dnDescriptionDao.findByPK(1);
		assertNotNull(term);
		assertEquals(updatedName, term.getName());
		assertEquals(updatedDesc, term.getDescription());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void findAllTest() throws OpenClinicaException {
		ArrayList<DnDescription> terms = (ArrayList<DnDescription>) dnDescriptionDao.findAll();
		assertEquals(2, terms.size());
	}
	
	@Test
	public void findByPkTest() throws OpenClinicaException {
		DnDescription term2 = (DnDescription) dnDescriptionDao.findByPK(2);
		assertNotNull(term2);
		assertEquals(2, term2.getId());
		assertNotNull(term2.getDescription());
		assertNotNull(term2.getName());
	}
	
	@Test
	public void createTest() throws OpenClinicaException {
		DnDescription term3 = new DnDescription();
		term3.setName("threeName");
		term3.setDescription("the third description");
		dnDescriptionDao.create(term3);
		assertNotNull(term3);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void findAllByStudyTest() throws OpenClinicaException {
		ArrayList<DnDescription> descriptions = (ArrayList<DnDescription>) dnDescriptionDao.findAllByStudyId(1);
		assertNotNull(descriptions);
	}

}
