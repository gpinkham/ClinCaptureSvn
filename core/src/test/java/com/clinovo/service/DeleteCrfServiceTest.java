package com.clinovo.service;

import java.util.Locale;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.junit.Before;
import org.junit.Test;

public class DeleteCrfServiceTest extends DefaultAppContextTest {

	private UserAccountBean currentUser;

	@Before
	public void before() throws Exception {
		currentUser = (UserAccountBean) userAccountDAO.findByPK(1);
	}

	@Test
	public void testThatDeleteCrfMethodDeletesCrfFromDataBase() throws Exception {
		CRFBean crfBean = (CRFBean) crfdao.findByPK(4);
		deleteCrfService.deleteCrf(crfBean, currentUser, Locale.ENGLISH, true);
		assertTrue(crfdao.findByPK(crfBean.getId()).getId() == 0);
	}

	@Test
	public void testThatDeleteCrfVersionMethodDeletesCrfVersionFromDataBase() throws Exception {
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(3);
		deleteCrfService.deleteCrfVersion(crfVersionBean, Locale.ENGLISH, true);
		assertTrue(crfVersionDao.findByPK(crfVersionBean.getId()).getId() == 0);
	}
}
