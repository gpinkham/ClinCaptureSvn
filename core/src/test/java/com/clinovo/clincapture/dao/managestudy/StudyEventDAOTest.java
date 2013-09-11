
package com.clinovo.clincapture.dao.managestudy;

import java.util.Date;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class StudyEventDAOTest extends DefaultAppContextTest {

	@Test
	public void testUpdate() throws OpenClinicaException {
        Date date = new Date();
		UserAccountBean updater = new UserAccountBean();
		StudyEventBean seb = (StudyEventBean) studyEventDao.findByPK(3);
		updater.setId(seb.getUpdaterId());
		seb.setUpdater(updater);
		seb.setUpdatedDate(date);
        seb = (StudyEventBean)studyEventDao.update(seb);
		assertEquals(seb.getUpdatedDate(), date);
	}
}
