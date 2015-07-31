/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.dao.managestudy;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class EventDefinitionCRFDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatUpdateEDCThatHasItemsToSDVReturnsCorrectValue() {
		assertTrue(eventDefinitionCRFDAO.updateEDCThatHasItemsToSDV(1, SourceDataVerification.AllREQUIRED));
	}

	@Test
	public void testThatFindForSiteByEventCrfIdReturnsTheCorrectEventDefinitionCRFBean() {
		assertEquals(2, eventDefinitionCRFDAO.findForSiteByEventCrfId(13).getId());
	}

	@Test
	public void testThatDoesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntryReturnsTrue() {
		EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(4);
		assertTrue(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(edc));
	}

	@Test
	public void testThatDoesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntryReturnsFalse() {
		EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(5);
		assertFalse(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(edc));
	}

	@Test
	public void testThatFindAllActiveByDefinitionAndSiteIdReturnsCorrectResult() {
		assertEquals(3, eventDefinitionCRFDAO.findAllActiveByDefinitionAndSiteId(1, 1).size());
	}

	@Test
	public void testThatFindAllActiveByStudyIdAndCRFIdReturnsCorrectResult() {
		assertEquals(2, eventDefinitionCRFDAO.findAllActiveByStudyIdAndCRFId(1, 1).size());
	}

	@Test
	public void testThatUpdateStatusMethodWorksFine() throws OpenClinicaException {
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		assertEquals(eventDefinitionCRFBean.getStatus(), Status.AVAILABLE);
		eventDefinitionCRFBean.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));
		eventDefinitionCRFBean.setStatus(Status.DELETED);
		eventDefinitionCRFDAO.updateStatus(eventDefinitionCRFBean);
		eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		assertEquals(eventDefinitionCRFBean.getStatus(), Status.DELETED);
	}
}