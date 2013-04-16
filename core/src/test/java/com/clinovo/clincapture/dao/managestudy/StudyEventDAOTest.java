package com.clinovo.clincapture.dao.managestudy;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StudyEventDAOTest extends HibernateOcDbTestCase {

	private DataSource dataSource;
	private StudyEventDAO studyEventDao;

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
		studyEventDao = new StudyEventDAO(dataSource);
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
		path.append("StudyEventDAOTest.xml");
		return new FlatXmlDataSet(HibernateOcDbTestCase.class.getResourceAsStream(path.toString()));
	}

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
		assertTrue(seb.isDynamic());
	}
}
