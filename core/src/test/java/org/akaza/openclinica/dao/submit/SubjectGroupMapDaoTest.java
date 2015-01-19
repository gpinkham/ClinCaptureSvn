package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.After;
import org.junit.Test;

import java.util.HashMap;

@SuppressWarnings("rawtypes")
public class SubjectGroupMapDaoTest extends DefaultAppContextTest {

	private SubjectGroupMapBean subjectGroupMapBean;

	@Test
	public void testThatCreateWorksFine() throws OpenClinicaException {
		StudyGroupBean studyGroupBean = new StudyGroupBean();
		studyGroupBean.setDescription("");
		studyGroupBean.setName("");
		studyGroupBean.setStudyGroupClassId(1);
		studyGroupBean = (StudyGroupBean) studyGroupDAO.create(studyGroupBean);
		subjectGroupMapBean = new SubjectGroupMapBean();
		subjectGroupMapBean.setStudyGroupClassId(4);
		subjectGroupMapBean.setStudySubjectId(1);
		subjectGroupMapBean.setStudyGroupId(studyGroupBean.getId());
		subjectGroupMapBean.setStatus(Status.AVAILABLE);
		subjectGroupMapBean.setOwner((UserAccountBean) userAccountDAO.findByPK(1));
		subjectGroupMapBean.setNotes("");
		subjectGroupMapBean = (SubjectGroupMapBean) subjectGroupMapDAO.create(subjectGroupMapBean);
		assertTrue(subjectGroupMapBean.getId() > 0);
	}
	
	@After
	public void tearDown() {
		if (subjectGroupMapBean != null && subjectGroupMapBean.getId() > 0) {
			subjectGroupMapDAO.execute("delete from subject_group_map where subject_group_map_id = ".concat(Integer
					.toString(subjectGroupMapBean.getId())), new HashMap());
		}
	}
}
