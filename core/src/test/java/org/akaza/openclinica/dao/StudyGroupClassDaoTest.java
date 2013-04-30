package org.akaza.openclinica.dao;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

@SuppressWarnings({"deprecation"})

public class StudyGroupClassDaoTest extends DefaultAppContextTest {

	@Test
	public void testUpdate() throws OpenClinicaException {
		boolean aDefault = false;
		String name = "Study Group Class 2";
		StudyGroupClassBean studyGroupClassBean = (StudyGroupClassBean) studyGroupClassDAO.findByPK(1);
		assertNotNull(studyGroupClassBean);
		studyGroupClassBean.setName(name);
		studyGroupClassBean.setDefault(aDefault);
		studyGroupClassBean.setStatus(Status.DELETED);
		studyGroupClassBean.setUpdater(new UserAccountBean());
		studyGroupClassDAO.update(studyGroupClassBean);
		assertEquals(name, ((StudyGroupClassBean) studyGroupClassDAO.findByPK(1)).getName());
		assertEquals(aDefault, ((StudyGroupClassBean) studyGroupClassDAO.findByPK(1)).isDefault());
		assertEquals(Status.DELETED, ((StudyGroupClassBean) studyGroupClassDAO.findByPK(1)).getStatus());
	}
	
	@Test
	public void testCreate() throws OpenClinicaException {
		StudyGroupClassBean studyGroupClassBean = new StudyGroupClassBean();
		studyGroupClassBean.setName("Study Group Class 3");
		studyGroupClassBean.setStudyId(2);
		studyGroupClassBean.setOwner(new UserAccountBean());
		studyGroupClassBean.setGroupClassTypeId(2);
		studyGroupClassBean.setStatus(Status.AVAILABLE);
		studyGroupClassBean.setSubjectAssignment("Arm");
		studyGroupClassBean.setDefault(false);
		studyGroupClassDAO.create(studyGroupClassBean);
	}
}


