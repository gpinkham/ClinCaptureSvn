package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class SubjectDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatCreateWorksFine() throws OpenClinicaException {
		SubjectBean subjectBean = new SubjectBean();
		subjectBean.setStatus(Status.AVAILABLE);
		subjectBean.setFatherId(1);
		subjectBean.setMotherId(1);
		subjectBean.setUniqueIdentifier("xxx_test_subject");
		subjectBean.setOwner((UserAccountBean) userAccountDAO.findByPK(1));
		subjectBean = subjectDAO.create(subjectBean);
		assertTrue(subjectBean.getId() > 0);
	}
}
