package com.clinovo.service;

import com.clinovo.model.CRFMask;
import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

public class CRFMaskingServiceTest extends DefaultAppContextTest {

	@Test
	public void testThatFindAllReturnsNotNull() {
		assertNotNull(maskingService.findAll());
	}

	@Test
	public void testThatFindAllReturnsAllWidgetsFromTheDB() {
		assertEquals(maskingService.findAll().size(), 1);
	}

	@Test
	public void testThatFindByUserIdReturnsNotNull() {
		assertNotNull(maskingService.findByUserId(1));
	}

	@Test
	public void testThatFindByUserIdReturnsMaskWithCorrectStudyID() {
		assertEquals(maskingService.findByUserId(1).size(), 1);
	}

	@Test
	public void testThatFindByUserIdSiteIdAndCRFIdReturnsNotNull() {
		assertNotNull(maskingService.findByUserIdSiteIdAndCRFId(1, 1, 1));
	}

	@Test
	public void testThatDeleteCompletelyRemovesCRFMask() {
		CRFMask mask = maskingService.findByUserIdSiteIdAndCRFId(1, 1, 1);
		maskingService.delete(mask);
		assertEquals(maskingService.findAll().size(), 0);
	}
}
