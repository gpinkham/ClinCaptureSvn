
package com.clinovo.clincapture.dao.managestudy;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class StudyEventDAOTest extends DefaultAppContextTest {

	@Test
	public void testUpdate() throws OpenClinicaException {
		int dynamicEventId = 10;
		UserAccountBean updater = new UserAccountBean();
		StudyEventBean seb = (StudyEventBean) studyEventDao.findByPK(3);
		updater.setId(seb.getUpdaterId());
		seb.setUpdater(updater);
		seb.setDynamicEventId(dynamicEventId);
		studyEventDao.update(seb);
		assertEquals(dynamicEventId, seb.getDynamicEventId());
	}
	
	@Test
	public void testThatUpdateSetsCorrectDynamicFlag() {
		
		int dynamicEventId = 10;
		UserAccountBean updater = new UserAccountBean();
		StudyEventBean seb = (StudyEventBean) studyEventDao.findByPK(3);
		updater.setId(seb.getUpdaterId());
		seb.setUpdater(updater);
		seb.setDynamicEventId(dynamicEventId);
		studyEventDao.update(seb);
		
		assertTrue(seb.isDynamic());
	}
}
