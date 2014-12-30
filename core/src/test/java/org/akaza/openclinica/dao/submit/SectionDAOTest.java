package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class SectionDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatCreateWorksFine() throws OpenClinicaException {
		SectionBean sectionBean = new SectionBean();
		sectionBean.setCRFVersionId(1);
		sectionBean.setStatus(Status.AVAILABLE);
		sectionBean.setLabel("");
		sectionBean.setTitle("");
		sectionBean.setInstructions("");
		sectionBean.setSubtitle("");
		sectionBean.setPageNumberLabel("");
		sectionBean.setOrdinal(1);
		sectionBean.setParentId(1);
		sectionBean.setOwner((UserAccountBean) userAccountDAO.findByPK(1));
		sectionBean.setBorders(1);
		sectionBean = (SectionBean) sectionDAO.create(sectionBean);
		assertTrue(sectionBean.getId() > 0);
	}
}