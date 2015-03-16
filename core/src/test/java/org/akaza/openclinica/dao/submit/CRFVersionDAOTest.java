package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

import java.util.ArrayList;

public class CRFVersionDAOTest extends DefaultAppContextTest {

	@Test
	@SuppressWarnings("unchecked")
	public void testThatFindAllByCRFReturnsAllCRFs() throws OpenClinicaException {
		ArrayList<CRFVersionBean> all;
		int cfrId = 1;

		all = (ArrayList<CRFVersionBean>) crfVersionDao.findAllByCRF(cfrId);
		assertEquals(1, all.size());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatFindAllActiveByCRFReturnsAllCRFsAttachedToActiveStudy() throws OpenClinicaException {
		ArrayList<CRFVersionBean> all;
		int cfrId = 2;

		all = (ArrayList<CRFVersionBean>) crfVersionDao.findAllActiveByCRF(cfrId);
		assertEquals(1, all.size());
	}
	
	@Test
	public void testThatFindByPKDoesNotReturnNull() throws OpenClinicaException {
		CRFVersionBean crfVer = null;
		int PK = 1;

		crfVer = (CRFVersionBean) crfVersionDao.findByPK(PK);
		assertNotNull(crfVer);
	}
	
	@Test
	public void testThatFindByFullNameDoesNotReturnNull() throws OpenClinicaException {
		CRFVersionBean crfVer = null;
		String version = "1";
		String cfrName = "Concomitant Medications AG";
		
		crfVer = (CRFVersionBean) crfVersionDao.findByFullName(version, cfrName);
		assertNotNull(crfVer);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testFindAllByOid() throws OpenClinicaException {
		ArrayList<CRFVersionBean> all;
		String oid= "F_CONC_V20";
		
		all = (ArrayList<CRFVersionBean>) crfVersionDao.findAllByOid(oid);
		assertEquals(1, all.size());
	}
	
	@Test
	public void testThatGetCRFIdFromCRFVersionIdReturnsCorrectId() throws OpenClinicaException {
		
		assertEquals(2, crfVersionDao.getCRFIdFromCRFVersionId(2));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testThatFindAllByCRFIdReturnsAllCRFVersionsAttachedToCRF() throws OpenClinicaException {
		ArrayList<CRFVersionBean> all;
		
		all = (ArrayList<CRFVersionBean>) crfVersionDao.findAllByCRFId(2);
		assertEquals(2, all.size());
	}
	
	@Test
	public void testThatFindCRFVersionIdReturnsCorrectCRFVersion() throws OpenClinicaException {
		
		assertEquals(new Integer(1), crfVersionDao.findCRFVersionId(1, "v2.0"));
	}
	
	@Test
	public void testThatFindByOidDoesNotReturnNull() throws OpenClinicaException {
		CRFVersionBean crfVer = null;
		String cfrVerOid = "F_AGEN_V20";
		
		crfVer = (CRFVersionBean) crfVersionDao.findByOid(cfrVerOid);
		assertNotNull(crfVer);
	}
	
	@Test
	public void testThatUpdateSetsTheChangedFields() throws OpenClinicaException {
		CRFVersionBean crfVer = null;
		String cfrVerOid = "F_AGEN_V20";
		String newDescription = "Test update";
		
		crfVer = (CRFVersionBean) crfVersionDao.findByOid(cfrVerOid);
		crfVer.setDescription(newDescription);
		crfVer.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));
		
		crfVersionDao.update(crfVer);
		
		crfVer = (CRFVersionBean) crfVersionDao.findByOid(cfrVerOid);
		assertTrue(newDescription.equals(crfVer.getDescription()));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testThatDeleteRemovesSpecifiedVersionFromDB() throws OpenClinicaException {
		int crfVerPK = 3;
		CRFVersionBean crfVer = null;
		
		crfVersionDao.delete(crfVerPK);
		
		crfVer = (CRFVersionBean) crfVersionDao.findByPK(crfVerPK);
		assertNull(crfVer.getOid());
	}

	@Test
	public void TestThatFind() throws OpenClinicaException {
		int oldVersionId = 5;
		int newVersionId = 6;

		assertEquals(newVersionId, crfVersionDao.findLatestAfterDeleted(oldVersionId).getId());
	}
}
