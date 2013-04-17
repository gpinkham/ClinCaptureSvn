package org.akaza.openclinica.dao.dynamicevent;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.dynamicevent.DynamicEventBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({"rawtypes", "deprecation"})
public class DynamicEventDaoTest extends HibernateOcDbTestCase {

	private DataSource dataSource;
	private DynamicEventDao dynamicEventDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		try {
			DatabaseOperation.DELETE_ALL.execute(getConnection(), getMappingDataSet());
			DatabaseOperation.INSERT.execute(getConnection(), getMappingDataSet());
		} catch (Exception e) {
			e.printStackTrace();
		}
		dataSource = getDataSource();
		dynamicEventDao = new DynamicEventDao(dataSource);
	}

	@After
	public void tearDown() {
		try {
			DatabaseOperation.DELETE_ALL.execute(getConnection(), getMappingDataSet());
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.tearDown();
	}

	private IDataSet getMappingDataSet() throws Exception {
		StringBuffer path = new StringBuffer("/");
		path.append(getClass().getPackage().getName().replace(".", "/"));
		path.append("/testdata/");
		path.append("DynamicEventDaoTest.xml");
		return new FlatXmlDataSet(HibernateOcDbTestCase.class.getResourceAsStream(path.toString()));
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
