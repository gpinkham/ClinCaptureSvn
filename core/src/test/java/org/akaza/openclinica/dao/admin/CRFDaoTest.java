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
		assertEquals(2, crfdao.findAllByStudy(studyId).size());
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
}
