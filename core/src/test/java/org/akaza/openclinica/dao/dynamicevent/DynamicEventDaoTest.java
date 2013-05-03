package org.akaza.openclinica.dao.dynamicevent;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.dynamicevent.DynamicEventBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

@SuppressWarnings({"deprecation"})
public class DynamicEventDaoTest extends DefaultAppContextTest {

	@Test
	public void testUpdate() throws OpenClinicaException {
		String description = "LALALA!";
		DynamicEventBean dynamicEventBean = (DynamicEventBean) dynamicEventDao.findByPK(1);
		assertNotNull(dynamicEventBean);
		dynamicEventBean.setDescription(description);
		dynamicEventDao.update(dynamicEventBean);
		assertEquals(description, ((DynamicEventBean) dynamicEventDao.findByPK(1)).getDescription());
	} 
	
	@Test
	public void testCreate() throws OpenClinicaException {
		DynamicEventBean dynamicEventBean = new DynamicEventBean();
		dynamicEventBean.setStudyGroupClassId(2);
		dynamicEventBean.setStudyEventDefinitionId(2);
		dynamicEventBean.setStudyId(2);
		dynamicEventBean.setOrdinal(2);
		dynamicEventBean.setOwnerId(2);
		dynamicEventBean.setName("dynamic event 2");
		dynamicEventBean.setDescription("test dynamic event 2");
		dynamicEventDao.create(dynamicEventBean);
	}
	
	@Test
	public void testDeleteByPK() throws OpenClinicaException {
		dynamicEventDao.deleteByPK(1);
		assertNull(((DynamicEventBean)dynamicEventDao.findByPK(1)).getCreatedDate());
	}
	@Test
	public void deleteAllFromStudyGroupClass() throws OpenClinicaException {
		dynamicEventDao.deleteAllFromStudyGroupClass(1);
		assertNull(((DynamicEventBean)dynamicEventDao.findByPK(2)).getCreatedDate());
	}
}
