package com.clinovo.clincapture.dao.managestudy;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.AbstractContextSentiveTest;

public class StudyEventDAOTest extends AbstractContextSentiveTest {

	private DataSource dataSource;
	private StudyEventDAO studyEventDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		dataSource = getDataSource();
		studyEventDao = new StudyEventDAO(dataSource);
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
