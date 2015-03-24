package com.clinovo.dao;

import com.clinovo.model.CRFMask;
import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

public class CRFMaskingDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatFindAllReturnsNotNull() {
		assertNotNull(maskingDAO.findAll());
	}

	@Test
	public void testThatFindAllReturnsAllWidgetsFromTheDB() {
		assertEquals(maskingDAO.findAll().size(), 1);
	}

	@Test
	public void testThatFindByUserIdReturnsNotNull() {
		assertNotNull(maskingDAO.findByUserId(1));
	}

	@Test
	public void testThatFindByUserIdReturnsMaskWithCorrectStudyID() {
		assertEquals(maskingDAO.findByUserId(1).size(), 1);
	}

	@Test
	public void testThatFindByUserIdSiteIdAndCRFIdReturnsNotNull() {
		assertNotNull(maskingDAO.findByUserIdSiteIdAndCRFId(1, 1, 1));
	}

	@Test
	public void testThatDeleteCompletelyRemovesCRFMask() {
		CRFMask mask = maskingDAO.findByUserIdSiteIdAndCRFId(1, 1, 1);
		maskingDAO.delete(mask);
		assertEquals(maskingDAO.findAll().size(), 0);
	}
}


