package com.clinovo.service;

import com.clinovo.model.CRFMask;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CRFMaskingServiceTest extends DefaultAppContextTest {

	@Test
	public void testThatFindAllReturnsNotNull() {
		assertNotNull(maskingService.findAll());
	}

	@Test
	public void testThatFindAllReturnsAllMasksFromTheDB() {
		assertEquals(2, maskingService.findAll().size());
	}

	@Test
	public void testThatFindByUserIdReturnsNotNull() {
		assertNotNull(maskingService.findByUserId(1));
	}

	@Test
	public void testThatFindByUserIdReturnsMaskWithCorrectStudyID() {
		assertEquals(2, maskingService.findByUserId(1).size());
	}

	@Test
	public void testThatFindByUserIdSiteIdAndCRFIdReturnsNotNull() {
		assertNotNull(maskingService.findByUserIdSiteIdAndCRFId(1, 1, 1));
	}

	@Test
	public void testThatDeleteCompletelyRemovesCRFMask() {
		CRFMask mask = maskingService.findByUserIdSiteIdAndCRFId(1, 1, 1);
		maskingService.delete(mask);
		assertEquals(maskingService.findAll().size(), 1);
	}

	@Test
	public void testThatFindAllActiveReturnsAllActiveMasks() {
		assertNull(maskingService.findActiveByUserIdSiteIdAndCRFId(1, 1, 4));
		assertNotNull(maskingService.findActiveByUserIdSiteIdAndCRFId(1, 1, 1));
	}

	@Test
	public void testThatIsEventDefinitionCRFMaskedReturnsCorrectResult() {
		assertFalse(maskingService.isEventDefinitionCRFMasked(4, 1, 1));
		assertTrue(maskingService.isEventDefinitionCRFMasked(1, 1, 1));
	}

	@Test
		 public void testThatUpdateMasksOnUserRoleUpdateRemovesMask() {
		StudyBean studyBean = new StudyBean();
		studyBean.setParentStudyId(2);
		studyBean.setId(1);
		maskingService.updateMasksOnUserRoleUpdate(Role.CLINICAL_RESEARCH_COORDINATOR, Role.INVESTIGATOR, studyBean, 1);

		assertFalse(maskingService.isEventDefinitionCRFMasked(1, 1, 1));
	}

	@Test
	public void testThatUpdateMasksOnUserRoleUpdateRestoresMask() {
		StudyBean studyBean = new StudyBean();
		studyBean.setParentStudyId(2);
		studyBean.setId(1);
		maskingService.updateMasksOnUserRoleUpdate(Role.INVESTIGATOR, Role.CLINICAL_RESEARCH_COORDINATOR, studyBean, 1);

		assertTrue(maskingService.isEventDefinitionCRFMasked(1, 1, 1));
	}

	@Test
	public void testThatFindAllActiveByUserStudyAndEventDefinitionIdsReturnsAllMasks() {
		assertEquals(1, maskingService.findAllActiveByUserStudyAndEventDefinitionIds(1, 1, 1).size());
	}

	@Test
	 public void testThatRemoveMaskedDisplayEventDefinitionCRFBeansReturnsCorrectResult() {
		List<Object> testList = new ArrayList<Object>();
		EventDefinitionCRFBean edc = new EventDefinitionCRFBean();
		edc.setId(1);
		edc.setStudyId(1);
		DisplayEventDefinitionCRFBean dedc = new DisplayEventDefinitionCRFBean();
		dedc.setEdc(edc);
		testList.add(dedc);
		UserAccountBean user = new UserAccountBean();
		user.setId(1);
		assertEquals(0, maskingService.removeMaskedDisplayEventDefinitionAndEventCRFBeans(testList, user).size());
	}

	@Test
	public void testThatRemoveMaskedDisplayEventCRFBeansReturnsCorrectResult() {
		List<Object> testList = new ArrayList<Object>();
		EventDefinitionCRFBean edc = new EventDefinitionCRFBean();
		edc.setId(1);
		edc.setStudyId(1);
		DisplayEventCRFBean dec = new DisplayEventCRFBean();
		dec.setEventDefinitionCRF(edc);
		testList.add(dec);
		UserAccountBean user = new UserAccountBean();
		user.setId(1);
		assertEquals(0, maskingService.removeMaskedDisplayEventDefinitionAndEventCRFBeans(testList, user).size());
	}

	@Test
	public void testThatReturnFirstNotMaskedEventReturnsCorrectResult() {
		List<StudyEventDefinitionBean> events = new ArrayList<StudyEventDefinitionBean>();
		StudyEventDefinitionBean event = new StudyEventDefinitionBean();
		event.setId(1);
		event.setStudyId(1);
		events.add(event);
		assertEquals(event.getId(), maskingService.returnFirstNotMaskedEvent(events, 1, 1).getId());
	}
}
