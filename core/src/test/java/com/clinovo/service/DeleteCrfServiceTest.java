package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.junit.Test;

public class DeleteCrfServiceTest extends DefaultAppContextTest {

	@Test
	public void testDeleteCrfMethodDeletesCrfFromDataBase() throws Exception {
		CRFBean crfBean = (CRFBean) crfdao.findByPK(4);
		deleteCrfService.deleteCrf(crfBean.getId());
		assertTrue(crfdao.findByPK(crfBean.getId()).getId() == 0);
	}

	@Test
	public void testDeleteCrfVersionMethodDeletesCrfVersionFromDataBase() throws Exception {
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(4);
		deleteCrfService.deleteCrfVersion(crfVersionBean.getId());
		assertTrue(crfVersionDao.findByPK(crfVersionBean.getId()).getId() == 0);
	}
}
