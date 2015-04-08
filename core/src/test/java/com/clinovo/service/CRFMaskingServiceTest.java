package com.clinovo.service;

import com.clinovo.model.CRFMask;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.junit.Test;

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
}
