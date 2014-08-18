package org.akaza.openclinica.dao;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

import java.util.List;

public class StudyGroupClassDaoTest extends DefaultAppContextTest {

	@Test
	public void testThatCreateDoesNotReturnNull() throws OpenClinicaException {

		assertNotNull(createStudyGroupClass());
	}

	@Test
	public void testUpdateSetsTheCorrectPropertiesName() throws OpenClinicaException {

		String name = "Study Group Class 4";
		StudyGroupClassBean studyGroupClassBean = studyGroupClassDAO.findByPK(1);

		studyGroupClassBean.setName(name);
		studyGroupClassBean.setUpdater(new UserAccountBean());

		studyGroupClassDAO.update(studyGroupClassBean);

		assertEquals(name, studyGroupClassDAO.findByPK(1).getName());
	}

	@Test
	public void testUpdateSetsTheCorrectNameWhenUpdated() throws OpenClinicaException {

		String name = "Study Group Class 2";
		StudyGroupClassBean studyGroupClassBean = studyGroupClassDAO.findByPK(1);

		studyGroupClassBean.setName(name);
		studyGroupClassBean.setUpdater(new UserAccountBean());

		studyGroupClassDAO.update(studyGroupClassBean);

		assertEquals(name, studyGroupClassDAO.findByPK(1).getName());
	}

	@Test
	public void testUpdateSetsTheCorrectDefaultPropertyWhenUpdated() throws OpenClinicaException {

		StudyGroupClassBean studyGroupClassBean = studyGroupClassDAO.findByPK(1);

		studyGroupClassBean.setDefault(false);
		studyGroupClassBean.setUpdater(new UserAccountBean());

		studyGroupClassDAO.update(studyGroupClassBean);

		assertEquals(false, studyGroupClassDAO.findByPK(1).isDefault());
	}

	@Test
	public void testUpdateSetsTheCorrectStatusWhenUpdated() throws OpenClinicaException {

		StudyGroupClassBean studyGroupClassBean = studyGroupClassDAO.findByPK(1);

		studyGroupClassBean.setStatus(Status.DELETED);
		studyGroupClassBean.setUpdater(new UserAccountBean());

		studyGroupClassDAO.update(studyGroupClassBean);

		assertEquals(Status.DELETED, studyGroupClassDAO.findByPK(1).getStatus());
	}

	@Test
	public void testThatFindByPKDoesNotReturnNull() {

		StudyGroupClassBean studyGroupClassBean = studyGroupClassDAO.findByPK(1);

		assertNotNull(studyGroupClassBean);
	}

	@Test
	public void testThatFindByPKReturnsCorrectStudyGroup() {

		StudyGroupClassBean studyGroupClassBean = studyGroupClassDAO.findByPK(1);

		assertEquals("Should return Study Group with correct name", "study group 1", studyGroupClassBean.getName());
	}

	@Test
	public void testThatFindDefaultByStudyIdReturnsAnEmptyBeanAsThereIsNoDefaultGroupClass() throws OpenClinicaException {

		int studyId = 1;

		assertEquals(0, studyGroupClassDAO.findDefaultByStudyId(studyId).getId());
	}

	@Test
	public void testThatFindByNameAndStudyIdDoesNotReturnNull() {

		assertNotNull(studyGroupClassDAO.findByNameAndStudyId("Study Group Class 3", 2));
	}

	@Test
	public void testThatFindByNameAndStudyIdReturnsCorrectStudyGroupClass() {

		StudyGroupClassBean studyGroup = studyGroupClassDAO.findByNameAndStudyId("study group 1", 1);

		assertEquals("Should return correct study group class with correct name", "study group 1", studyGroup.getName());
	}

	@Test
	public void testGetMaxDynamicOrdinal() {

		assertEquals(3, studyGroupClassDAO.getMaxDynamicOrdinalByStudyId(1));
	}

	@Test
	public void testUpdateDynamicOrdinal() {

		int newDynamicOrdinal = 7;
		int studyId = 1;
		int studyGroupClassId = 1;

		studyGroupClassDAO.updateDynamicOrdinal(newDynamicOrdinal, studyId, studyGroupClassId);
		assertEquals(7, studyGroupClassDAO.findByPK(1).getDynamicOrdinal());
	}

	@Test
	public void testThatFindAvailableDynamicGroupByStudyEventDefinitionIdReturnsInactiveStudyGroupClassBean() {

		int studyEventDefinitionId = 9;
		int inactiveIdCode = 0;
		StudyGroupClassBean studyGroupClassBean;

		studyGroupClassBean = studyGroupClassDAO
				.findAvailableDynamicGroupByStudyEventDefinitionId(studyEventDefinitionId);
		assertEquals(inactiveIdCode, studyGroupClassBean.getId());
	}

	@Test
	public void testThatFindAvailableDynamicGroupByStudyEventDefinitionIdReturnsValidStudyGroupClassBean() {

		int studyEventDefinitionId = 2;
		int expectedStudyGroupClassBeanId = 3;
		StudyGroupClassBean studyGroupClassBean;

		studyGroupClassBean = studyGroupClassDAO
				.findAvailableDynamicGroupByStudyEventDefinitionId(studyEventDefinitionId);
		assertEquals(expectedStudyGroupClassBeanId, studyGroupClassBean.getId());
	}

	private EntityBean createStudyGroupClass() {

		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.setId(1);
		StudyGroupClassBean studyGroupClassBean = new StudyGroupClassBean();
		studyGroupClassBean.setName("Study Group Class 3");
		studyGroupClassBean.setStudyId(1);
		studyGroupClassBean.setOwner(userAccountBean);
		studyGroupClassBean.setGroupClassTypeId(2);
		studyGroupClassBean.setStatus(Status.AVAILABLE);
		studyGroupClassBean.setSubjectAssignment("Arm");
		studyGroupClassBean.setDefault(false);
		studyGroupClassBean.setDynamicOrdinal(3);

		return studyGroupClassDAO.create(studyGroupClassBean);

	}

	@Test
	public void testThatFindAllActiveDynamicGroupClassesByStudyIdReturnsCorrectNumberOfBeans() {

		int studyId = 1;
		int expectedNumberOfStudyGroupClasses = 2;

		List<StudyGroupClassBean> studyGroupClassBeansList =
				studyGroupClassDAO.findAllActiveDynamicGroupClassesByStudyId(studyId);

		assertEquals(expectedNumberOfStudyGroupClasses, studyGroupClassBeansList.size());
	}
}
