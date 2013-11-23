package org.akaza.openclinica.dao.admin;

import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class CRFDaoTest extends DefaultAppContextTest {

	@Test
	public void testThatFindByOidReturnsCRFWithCorrectOID() throws OpenClinicaException {
		CRFBean crf = null;
		String cfrOid = "F_CONC";

		crf = crfdao.findByOid(cfrOid);
		assertEquals(cfrOid, crf.getOid());
	}

	@Test
	public void testThatFindByNameReturnsCRFWithCorrectName() throws OpenClinicaException {
		CRFBean crf = null;
		String cfrName = "Agent Administration";
		
		crf = (CRFBean) crfdao.findByName(cfrName);
		assertEquals(cfrName, crf.getName());
	}

	@Test
	public void testThatUpdateSetsTheChangedField() throws OpenClinicaException {
		CRFBean crf = null;
		String cfrOid = "F_AGEN";
		String newDescription = "Test update";
		
		crf = (CRFBean) crfdao.findByOid(cfrOid);
		crf.setDescription(newDescription);
		crf.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));
		
		crfdao.update(crf);

		crf = crfdao.findByOid(cfrOid);
		assertTrue(newDescription.equals(crf.getDescription()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatFindAllDoesNotReturnAnEmptyList() throws OpenClinicaException {
		ArrayList<CRFBean> all;

		all = (ArrayList<CRFBean>) crfdao.findAll();
		assertFalse(all.isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatFindAllByStudyReturnsAllExistingCRFs() throws OpenClinicaException {
		ArrayList<CRFBean> all;
		int studyId = 1;

		all = (ArrayList<CRFBean>) crfdao.findAllByStudy(studyId);
		assertEquals(2, all.size());
	}

	@Test
	public void testThatGetCountofActiveCRFsReturnsValidSize() throws OpenClinicaException {
		Integer count = null;

		count = crfdao.getCountofActiveCRFs();
		assertTrue(count > 0);
	}
	
	@Test
	public void testThatFindByPKDoesNotReturnNull() throws OpenClinicaException {
		CRFBean crf = null;
		int CrfPK = 2;

		crf = (CRFBean) crfdao.findByPK(CrfPK);
		assertNotNull(crf);
	}
}
