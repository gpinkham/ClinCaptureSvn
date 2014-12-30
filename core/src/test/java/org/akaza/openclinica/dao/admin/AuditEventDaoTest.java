package org.akaza.openclinica.dao.admin;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.AuditEventBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class AuditEventDaoTest extends DefaultAppContextTest {

	@Test
	public void testThatCreateWorksFine() throws OpenClinicaException {
		AuditEventBean auditEventBean = new AuditEventBean();
		auditEventBean.setAuditTable("");
		auditEventBean.setUserId(1);
		auditEventBean.setEntityId(1);
		auditEventBean.setReasonForChange("");
		auditEventBean.setActionMessage("");
		auditEventBean = (AuditEventBean) auditEventDAO.create(auditEventBean);
		assertTrue(auditEventBean.getId() > 0);
	}
}
