package org.akaza.openclinica.dao.dynamicevent;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.dynamicevent.DynamicEventBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.AbstractContextSentiveTest;

@SuppressWarnings({"rawtypes", "deprecation"})
public class DynamicEventDaoTest extends AbstractContextSentiveTest {

	private DataSource dataSource;
	private DynamicEventDao dynamicEventDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		dataSource = getDataSource();
		dynamicEventDao = new DynamicEventDao(dataSource);
	}

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
		dynamicEventBean.setDefault(true);
		dynamicEventDao.create(dynamicEventBean);
	}
}
