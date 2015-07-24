package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class UserAccountDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatGetUsersAssignedMetricReturnsCorrectValue() {
		assertEquals(userAccountDAO.getUsersAssignedMetric(1), 0);
	}

	@Test
	public void testThatIsUserPresentInStudyReturnsCorrectValue() {
		assertTrue(userAccountDAO.isUserPresentInStudy("root", 1));
	}

	@Test
	public void testThatUpdateStatusMethodWorksFine() throws OpenClinicaException {
		UserAccountBean userAccountBean = (UserAccountBean) userAccountDAO.findByPK(1);
		userAccountBean.setUpdater(userAccountBean);
		userAccountBean.setStatus(Status.DELETED);
		userAccountDAO.updateStatus(userAccountBean);
		userAccountBean = (UserAccountBean) userAccountDAO.findByPK(1);
		assertEquals(userAccountBean.getStatus(), Status.DELETED);
		userAccountBean.setUpdater(userAccountBean);
		userAccountBean.setStatus(Status.AVAILABLE);
		userAccountDAO.updateStatus(userAccountBean);
		userAccountBean = (UserAccountBean) userAccountDAO.findByPK(1);
		assertEquals(userAccountBean.getStatus(), Status.AVAILABLE);
	}
}
