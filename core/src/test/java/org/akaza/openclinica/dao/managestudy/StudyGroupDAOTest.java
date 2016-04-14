package org.akaza.openclinica.dao.managestudy;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class StudyGroupDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatCreateWorksFine() throws OpenClinicaException {
		StudyGroupBean studyGroupBean = new StudyGroupBean();
		studyGroupBean.setDescription("");
		studyGroupBean.setName("");
		studyGroupBean.setStudyGroupClassId(1);
		studyGroupBean = (StudyGroupBean) studyGroupDAO.create(studyGroupBean);
		assertTrue(studyGroupBean.getId() > 0);
	}
	
	@Test
	public void testThatDeleteAllByStudyGroupClassIdDeletesStudyGroupBeans() {
		int groupId = 1;
		int studyGroupClassId = 2;
		String name = "study group 1";
		assertEquals(name, studyGroupDAO.findByPK(groupId).getName());
		studyGroupDAO.deleteAllByStudyGroupClassId(studyGroupClassId);
		assertEquals("", studyGroupDAO.findByPK(groupId).getName());
	}
}