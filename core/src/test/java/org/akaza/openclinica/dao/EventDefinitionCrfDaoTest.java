package org.akaza.openclinica.dao;

import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class EventDefinitionCrfDaoTest extends DefaultAppContextTest {

	@Test
	public void testGetRequiredEventCRFDefIdsThatShouldBeSDVd()
			throws OpenClinicaException {
		StudyBean studyBean = (StudyBean) studyDAO.findByPK(1);
		List<Integer> result = eventDefinitionCRFDAO
				.getRequiredEventCRFDefIdsThatShouldBeSDVd(studyBean);
		assertEquals(result.size(), 0);
	}

	@Test
	public void testThatFindAllReturnsCorrectNumber() {
		List<EventDefinitionCRFBean> eventCRFs = (List<EventDefinitionCRFBean>) eventDefinitionCRFDAO
				.findAll();
		assertEquals(5, eventCRFs.size());
	}

	@Test
	public void testThatFindAllByDefinitionReturnsEdcWithCorrectCrfId() {
		List<EventDefinitionCRFBean> eventCRFs = (List<EventDefinitionCRFBean>) eventDefinitionCRFDAO
				.findAllByDefinition(3);
		assertEquals(2, eventCRFs.get(0).getCrfId());
	}

	@Test
	public void testThatFindAllByDefinitionReturnsEdcWithCorrectEmailStep() {
		List<EventDefinitionCRFBean> eventCRFs = (List<EventDefinitionCRFBean>) eventDefinitionCRFDAO
				.findAllByDefinition(3);
		assertEquals("", eventCRFs.get(0).getEmailStep());
	}

	@Test
	public void restThatFindAllParentsByDefinitionReturnsCorrectNumberOfEdcs() {
		List<EventDefinitionCRFBean> eventCRFs = (List<EventDefinitionCRFBean>) eventDefinitionCRFDAO
				.findAllParentsByDefinition(3);
		assertEquals(1, eventCRFs.size());
	}

	@Test
	public void testThatFindAllParentsByDefinitionReturnsCorrectNumberOfEdcs() {
		List<EventDefinitionCRFBean> eventCRFs = (List<EventDefinitionCRFBean>) eventDefinitionCRFDAO
				.findAllByDefinitionAndSiteIdAndParentStudyId(1, 1, 1);
		assertEquals(0, eventCRFs.size());
	}

	@Test
	public void testThatFindAllByCRFReturnsCorrectNumberOfEdcs() {
		List<EventDefinitionCRFBean> eventCRFs = (List<EventDefinitionCRFBean>) eventDefinitionCRFDAO.findAllByCRF(1);
		assertEquals(2, eventCRFs.size());
	}

	@Test
	public void testThatFindByPkReturnsEdcWithCorrectDefaultVersion(){
		EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(5);
		assertEquals(2, edc.getDefaultVersionId());
	}

	@Test
	public void testThatUpdateSetsTheChangedField() {
		EventDefinitionCRFBean edc;
		int id=5;
		int newDefaultVersion = 1;
		edc = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(id);
		edc.setDefaultVersionId(newDefaultVersion);
		edc.setUpdater(((UserAccountBean) userAccountDAO.findByPK(1)));
		eventDefinitionCRFDAO.update(edc);
		EventDefinitionCRFBean updatedEdc = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(id);
		assertTrue(newDefaultVersion == updatedEdc.getDefaultVersionId());
	}
}
