package org.akaza.openclinica.dao.submit;

import java.util.ArrayList;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class CRFVersionDAOTest extends DefaultAppContextTest {

	@Test
	@SuppressWarnings("unchecked")
	public void testThatFindAllByCRFReturnsAllCRFs() throws OpenClinicaException {
		int cfrId = 1;
		ArrayList<CRFVersionBean> all = (ArrayList<CRFVersionBean>) crfVersionDao.findAllByCRF(cfrId);
		assertEquals(1, all.size());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatFindAllActiveByCRFReturnsAllCRFsAttachedToActiveStudy() throws OpenClinicaException {
		int cfrId = 2;
		ArrayList<CRFVersionBean> all = (ArrayList<CRFVersionBean>) crfVersionDao.findAllActiveByCRF(cfrId);
		assertEquals(1, all.size());
	}

	@Test
	public void testThatFindByPKDoesNotReturnNull() throws OpenClinicaException {
		int PK = 1;
		CRFVersionBean crfVer = (CRFVersionBean) crfVersionDao.findByPK(PK);
		assertNotNull(crfVer);
	}

	@Test
	public void testThatFindByFullNameDoesNotReturnNull() throws OpenClinicaException {
		String version = "1";
		String cfrName = "Concomitant Medications AG";
		CRFVersionBean crfVer = (CRFVersionBean) crfVersionDao.findByFullName(version, cfrName);
		assertNotNull(crfVer);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindAllByOid() throws OpenClinicaException {
		String oid = "F_CONC_V20";
		ArrayList<CRFVersionBean> all = (ArrayList<CRFVersionBean>) crfVersionDao.findAllByOid(oid);
		assertEquals(1, all.size());
	}

	@Test
	public void testThatGetCRFIdFromCRFVersionIdReturnsCorrectId() throws OpenClinicaException {
		assertEquals(2, crfVersionDao.getCRFIdFromCRFVersionId(2));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatFindAllByCRFIdReturnsAllCRFVersionsAttachedToCRF() throws OpenClinicaException {
		ArrayList<CRFVersionBean> all = (ArrayList<CRFVersionBean>) crfVersionDao.findAllByCRFId(2);
		assertEquals(2, all.size());
	}

	@Test
	public void testThatFindCRFVersionIdReturnsCorrectCRFVersion() throws OpenClinicaException {
		assertEquals(new Integer(1), crfVersionDao.findCRFVersionId(1, "v2.0"));
	}

	@Test
	public void testThatFindByOidDoesNotReturnNull() throws OpenClinicaException {
		String cfrVerOid = "F_AGEN_V20";
		CRFVersionBean crfVer = crfVersionDao.findByOid(cfrVerOid);
		assertNotNull(crfVer);
	}

	@Test
	public void testThatUpdateSetsTheChangedFields() throws OpenClinicaException {
		String cfrVerOid = "F_AGEN_V20";
		String newDescription = "Test update";

		CRFVersionBean crfVer = crfVersionDao.findByOid(cfrVerOid);
		crfVer.setDescription(newDescription);
		crfVer.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));

		crfVersionDao.update(crfVer);

		crfVer = crfVersionDao.findByOid(cfrVerOid);
		assertTrue(newDescription.equals(crfVer.getDescription()));
	}

	@Test
	public void TestThatFind() throws OpenClinicaException {
		int oldVersionId = 5;
		int newVersionId = 6;
		assertEquals(newVersionId, crfVersionDao.findLatestAfterDeleted(oldVersionId).getId());
	}
}
