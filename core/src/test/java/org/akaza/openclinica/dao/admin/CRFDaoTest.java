/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.dao.admin;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class CRFDaoTest extends DefaultAppContextTest {

	@Test
	public void testThatFindByOidReturnsCRFWithCorrectOID() throws OpenClinicaException {
		String cfrOid = "F_CONC";
		assertEquals(cfrOid, crfdao.findByOid(cfrOid).getOid());
	}

	@Test
	public void testThatFindByNameReturnsCRFWithCorrectName() throws OpenClinicaException {
		String cfrName = "Agent Administration";
		assertEquals(cfrName, crfdao.findByName(cfrName).getName());
	}

	@Test
	public void testThatUpdateSetsTheChangedField() throws OpenClinicaException {
		CRFBean crf;
		String cfrOid = "F_AGEN";
		String newDescription = "Test update";

		crf = crfdao.findByOid(cfrOid);
		crf.setDescription(newDescription);
		crf.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));

		crfdao.update(crf);

		assertTrue(newDescription.equals(crfdao.findByOid(cfrOid).getDescription()));
	}

	@Test
	public void testThatFindAllDoesNotReturnAnEmptyList() throws OpenClinicaException {
		assertFalse(crfdao.findAll().isEmpty());
	}

	@Test
	public void testThatFindAllByStudyReturnsAllExistingCRFs() throws OpenClinicaException {
		int studyId = 1;
		assertEquals(3, crfdao.findAllByStudy(studyId).size());
	}

	@Test
	public void testThatGetCountofActiveCRFsReturnsValidSize() throws OpenClinicaException {
		assertTrue(crfdao.getCountofActiveCRFs() > 0);
	}

	@Test
	public void testThatFindByPKDoesNotReturnNull() throws OpenClinicaException {
		int CrfPK = 2;
		assertNotNull(crfdao.findByPK(CrfPK));
	}

	@Test
	public void testGetAllCRFNamesFromStudyNotReturnNull() throws OpenClinicaException {
		assertNotNull(crfdao.getAllCRFNamesFromStudy(1));
	}

	@Test
	public void testGetAllCRFNamesFromStudyReturnCorrectSize() throws OpenClinicaException {
		assertEquals(3, crfdao.getAllCRFNamesFromStudy(1).size());
	}

	@Test
	public void testThatFindAllActiveByDefinitionsForCurrentStudyReturnsCorrectSize() throws Exception {
		assertEquals(3, crfdao.findAllActiveByDefinitionsForCurrentStudy(1).size());
	}

	@Test
	public void testThatFindAllActiveCrfsReturnsCorrectSize() throws OpenClinicaException {
		assertEquals(3, crfdao.findAllActiveCrfs().size());
	}

	@Test
	public void testThatRemoveCrfByIdRemovesCrf() throws OpenClinicaException {
		crfdao.deleteCrfById(4);
		CRFBean crf = (CRFBean) crfdao.findByPK(4);

		assertEquals(0, crf.getId());
		assertNull(crf.getOid());
	}
}
