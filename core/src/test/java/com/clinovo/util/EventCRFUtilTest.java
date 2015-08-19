/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.util;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventCRFUtilTest {

	@Mock
	private CRFVersionDAO crfVersionDAO;

	@Mock
	private EventDefinitionCRFDAO eventDefinitionCRFDAO;

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_1() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.DELETED);
		StudyEventBean studyEvent  = null;
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = null;
		Mockito.when(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefCRF)).thenReturn(true);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.DELETED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_2() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.LOCKED);
		StudyEventBean studyEvent  = null;
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = null;
		Mockito.when(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefCRF)).thenReturn(true);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.LOCKED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_3() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = null;
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = null;
		Mockito.when(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefCRF)).thenReturn(false);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.LOCKED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_4() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = null;
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = null;
		Mockito.when(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefCRF)).thenReturn(true);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.NOT_STARTED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_5() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.SKIPPED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = null;
		Mockito.when(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefCRF)).thenReturn(true);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.LOCKED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_6() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.STOPPED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = null;
		Mockito.when(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefCRF)).thenReturn(true);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.LOCKED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_7() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.LOCKED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = null;
		Mockito.when(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefCRF)).thenReturn(true);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.LOCKED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_8() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = null;
		Mockito.when(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefCRF)).thenReturn(false);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.LOCKED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_9() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.REMOVED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = null;
		Mockito.when(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefCRF)).thenReturn(true);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.DELETED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_10() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = null;
		Mockito.when(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefCRF)).thenReturn(true);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.NOT_STARTED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_11() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.SKIPPED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.LOCKED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_12() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.STOPPED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.LOCKED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_13() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.LOCKED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.LOCKED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_14() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.REMOVED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.DELETED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_15() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();
		eventCrf.setNotStarted(true);
		Mockito.when(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefCRF)).thenReturn(false);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.LOCKED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_16() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();
		eventCrf.setNotStarted(true);
		Mockito.when(eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefCRF)).thenReturn(true);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.NOT_STARTED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_17() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();
		eventCrf.setCRFVersionId(17);
		CRFVersionBean crfVersion = new CRFVersionBean();
		crfVersion.setId(17);
		crfVersion.setStatus(Status.DELETED);
		Mockito.when(crfVersionDAO.findByPK(eventCrf.getCRFVersionId())).thenReturn(crfVersion);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.LOCKED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_18() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();
		eventCrf.setId(9);
		eventCrf.setStatus(Status.AVAILABLE);
		eventCrf.setCRFVersionId(17);
		eventCrf.setNotStarted(false);
		CRFVersionBean crfVersion = new CRFVersionBean();
		crfVersion.setId(17);
		crfVersion.setStatus(Status.AVAILABLE);
		Mockito.when(crfVersionDAO.findByPK(eventCrf.getCRFVersionId())).thenReturn(crfVersion);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.DATA_ENTRY_STARTED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_19() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();
		eventCrf.setId(9);
		eventCrf.setStatus(Status.PENDING);
		eventCrf.setValidatorId(0);
		eventCrf.setCRFVersionId(17);
		eventCrf.setNotStarted(false);
		CRFVersionBean crfVersion = new CRFVersionBean();
		crfVersion.setId(17);
		crfVersion.setStatus(Status.AVAILABLE);
		Mockito.when(crfVersionDAO.findByPK(eventCrf.getCRFVersionId())).thenReturn(crfVersion);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.INITIAL_DATA_ENTRY_COMPLETED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_20() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();
		eventCrf.setId(9);
		eventCrf.setStatus(Status.PENDING);
		eventCrf.setValidatorId(1);
		eventCrf.setCRFVersionId(17);
		eventCrf.setNotStarted(false);
		CRFVersionBean crfVersion = new CRFVersionBean();
		crfVersion.setId(17);
		crfVersion.setStatus(Status.AVAILABLE);
		Mockito.when(crfVersionDAO.findByPK(eventCrf.getCRFVersionId())).thenReturn(crfVersion);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.DOUBLE_DATA_ENTRY, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_21() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();
		eventCrf.setId(9);
		eventCrf.setStatus(Status.UNAVAILABLE);
		eventCrf.setSdvStatus(false);
		eventCrf.setCRFVersionId(17);
		eventCrf.setNotStarted(false);
		CRFVersionBean crfVersion = new CRFVersionBean();
		crfVersion.setId(17);
		crfVersion.setStatus(Status.AVAILABLE);
		Mockito.when(crfVersionDAO.findByPK(eventCrf.getCRFVersionId())).thenReturn(crfVersion);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.COMPLETED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_22() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();
		eventCrf.setId(9);
		eventCrf.setStatus(Status.UNAVAILABLE);
		eventCrf.setSdvStatus(true);
		eventCrf.setCRFVersionId(17);
		eventCrf.setNotStarted(false);
		CRFVersionBean crfVersion = new CRFVersionBean();
		crfVersion.setId(17);
		crfVersion.setStatus(Status.AVAILABLE);
		Mockito.when(crfVersionDAO.findByPK(eventCrf.getCRFVersionId())).thenReturn(crfVersion);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.SOURCE_DATA_VERIFIED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_23() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.SIGNED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();
		eventCrf.setId(9);
		eventCrf.setStatus(Status.UNAVAILABLE);
		eventCrf.setSdvStatus(true);
		eventCrf.setElectronicSignatureStatus(true);
		eventCrf.setCRFVersionId(17);
		eventCrf.setNotStarted(false);
		CRFVersionBean crfVersion = new CRFVersionBean();
		crfVersion.setId(17);
		crfVersion.setStatus(Status.AVAILABLE);
		Mockito.when(crfVersionDAO.findByPK(eventCrf.getCRFVersionId())).thenReturn(crfVersion);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.SIGNED, crfStatus);
	}

	@Test
	public void testThatGetEventCRFCurrentStatusReturnsCorrectStatus_24() {

		// SETUP
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setStatus(Status.AVAILABLE);
		StudyEventBean studyEvent  = new StudyEventBean();
		studyEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		EventCRFBean eventCrf = new EventCRFBean();
		eventCrf.setId(9);
		eventCrf.setStatus(Status.PARTIAL_DATA_ENTRY);
		eventCrf.setCRFVersionId(17);
		eventCrf.setNotStarted(false);
		CRFVersionBean crfVersion = new CRFVersionBean();
		crfVersion.setId(17);
		crfVersion.setStatus(Status.AVAILABLE);
		Mockito.when(crfVersionDAO.findByPK(eventCrf.getCRFVersionId())).thenReturn(crfVersion);

		// EXECUTE
		Status crfStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent, eventDefCRF, eventCrf,
				crfVersionDAO, eventDefinitionCRFDAO);

		// VERIFY
		Assert.assertEquals(Status.PARTIAL_DATA_ENTRY, crfStatus);
	}
}
