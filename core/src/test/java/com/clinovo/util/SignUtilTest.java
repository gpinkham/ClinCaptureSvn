package com.clinovo.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * SignUtilTest class.
 */
@SuppressWarnings("rawtypes")
public class SignUtilTest {

	private DAOWrapper daoWrapper;
	private StudySubjectBean studySubjectBean;

	private EventDefinitionCRFBean createEventDefinitionCrfBean(int id, int studyEventDefinitionId, int crfId,
			boolean hideCrf, boolean requiredCrf, SourceDataVerification sourceDataVerification, Status status) {
		EventDefinitionCRFBean eventDefinitionCRFBean = new EventDefinitionCRFBean();
		eventDefinitionCRFBean.setId(id);
		eventDefinitionCRFBean.setStudyId(1);
		eventDefinitionCRFBean.setCrfId(crfId);
		eventDefinitionCRFBean.setActive(true);
		eventDefinitionCRFBean.setStatus(status);
		eventDefinitionCRFBean.setHideCrf(hideCrf);
		eventDefinitionCRFBean.setRequiredCRF(requiredCrf);
		eventDefinitionCRFBean.setSourceDataVerification(sourceDataVerification);
		eventDefinitionCRFBean.setStudyEventDefinitionId(studyEventDefinitionId);
		return eventDefinitionCRFBean;
	}

	private EventCRFBean createEventCrfBean(int id, int studySubjectId, int studyEventId, int crfVersionId,
			boolean sdvStatus, boolean notStarted, Status status) {
		EventCRFBean eventCRFBean = new EventCRFBean();
		eventCRFBean.setId(id);
		eventCRFBean.setActive(true);
		eventCRFBean.setStatus(status);
		eventCRFBean.setSdvStatus(sdvStatus);
		eventCRFBean.setNotStarted(notStarted);
		eventCRFBean.setCRFVersionId(crfVersionId);
		eventCRFBean.setStudyEventId(studyEventId);
		eventCRFBean.setStudySubjectId(studySubjectId);
		return eventCRFBean;
	}

	private StudyEventBean createStudyEventBean(int id, int studySubjectId, int studyEventDefinitionId, int ordinal,
			SubjectEventStatus subjectEventStatus, Status status) {
		StudyEventBean studyEventBean = new StudyEventBean();
		studyEventBean.setId(id);
		studyEventBean.setActive(true);
		studyEventBean.setStatus(status);
		studyEventBean.setSampleOrdinal(ordinal);
		studyEventBean.setStudySubjectId(studySubjectId);
		studyEventBean.setSubjectEventStatus(subjectEventStatus);
		studyEventBean.setStudyEventDefinitionId(studyEventDefinitionId);
		return studyEventBean;
	}

	/**
	 * Test setup method.
	 * 
	 * @throws Exception
	 *             an Exception
	 */
	@Before
	public void setUp() throws Exception {
		studySubjectBean = new StudySubjectBean();
		studySubjectBean.setId(1);
		studySubjectBean.setStudyId(1);
		studySubjectBean.setStatus(Status.AVAILABLE);

		StudyBean study = new StudyBean();
		study.setId(1);

		StudyDAO sdao = Mockito.mock(StudyDAO.class);
		StudyEventDAO sedao = Mockito.mock(StudyEventDAO.class);
		StudySubjectDAO ssdao = Mockito.mock(StudySubjectDAO.class);
		EventCRFDAO ecdao = Mockito.mock(EventCRFDAO.class);
		EventDefinitionCRFDAO edcdao = Mockito.mock(EventDefinitionCRFDAO.class);
		DiscrepancyNoteDAO discDao = Mockito.mock(DiscrepancyNoteDAO.class);
		StudyEventDefinitionDAO seddao = Mockito.mock(StudyEventDefinitionDAO.class);

		daoWrapper = new DAOWrapper(sdao, sedao, ssdao, ecdao, edcdao, seddao, discDao);

		int studySubjectId = 1;

		StudyEventBean studyEventBean = createStudyEventBean(1, studySubjectId, 1, 1,
				SubjectEventStatus.SOURCE_DATA_VERIFIED, Status.AVAILABLE);
		List<StudyEventBean> studyEventBeanList = new ArrayList<StudyEventBean>();
		studyEventBeanList.add(studyEventBean);

		int index = 1;
		List<EventCRFBean> eventCrfBeanList = new ArrayList<EventCRFBean>();
		eventCrfBeanList.add(createEventCrfBean(index++, 1, 1, index++, true, false, Status.UNAVAILABLE));
		eventCrfBeanList.add(createEventCrfBean(index++, 1, 1, index++, true, false, Status.UNAVAILABLE));
		eventCrfBeanList.add(createEventCrfBean(index++, 1, 1, index++, false, false, Status.UNAVAILABLE));
		eventCrfBeanList.add(createEventCrfBean(index, 1, 1, index, false, false, Status.UNAVAILABLE));

		index = 1;
		List<EventDefinitionCRFBean> eventDefinitionCrfBeanList = new ArrayList<EventDefinitionCRFBean>();
		eventDefinitionCrfBeanList.add(createEventDefinitionCrfBean(index++, 1, index++, false, true,
				SourceDataVerification.AllREQUIRED, Status.AVAILABLE));
		eventDefinitionCrfBeanList.add(createEventDefinitionCrfBean(index++, 1, index++, false, true,
				SourceDataVerification.AllREQUIRED, Status.AVAILABLE));
		eventDefinitionCrfBeanList.add(createEventDefinitionCrfBean(index++, 1, index++, true, true,
				SourceDataVerification.AllREQUIRED, Status.AVAILABLE));
		eventDefinitionCrfBeanList.add(createEventDefinitionCrfBean(index, 1, index, true, true,
				SourceDataVerification.AllREQUIRED, Status.AVAILABLE));

		Mockito.when(edcdao.findAllByDefinition(study, studyEventBean.getStudyEventDefinitionId())).thenReturn(
				eventDefinitionCrfBeanList);
		Mockito.when(ecdao.findAllStartedByStudyEvent(studyEventBean)).thenReturn((ArrayList) eventCrfBeanList);
		Mockito.when(sedao.findAllByStudySubject(studySubjectBean)).thenReturn((ArrayList) studyEventBeanList);
		Mockito.when(discDao.doesNotHaveOutstandingDNs(studyEventBean)).thenReturn(true);
		Mockito.when(sdao.findByPK(1)).thenReturn(study);

		index = 0;
		Mockito.when(
				edcdao.findByStudyEventIdAndCRFVersionId(study, studyEventBean.getId(),
						(eventCrfBeanList.get(index)).getCRFVersionId())).thenReturn(
				eventDefinitionCrfBeanList.get(index));
		index++;
		Mockito.when(
				edcdao.findByStudyEventIdAndCRFVersionId(study, studyEventBean.getId(),
						(eventCrfBeanList.get(index)).getCRFVersionId())).thenReturn(
				eventDefinitionCrfBeanList.get(index));
		index++;
		Mockito.when(
				edcdao.findByStudyEventIdAndCRFVersionId(study, studyEventBean.getId(),
						(eventCrfBeanList.get(index)).getCRFVersionId())).thenReturn(
				eventDefinitionCrfBeanList.get(index));
		index++;
		Mockito.when(
				edcdao.findByStudyEventIdAndCRFVersionId(study, studyEventBean.getId(),
						(eventCrfBeanList.get(index)).getCRFVersionId())).thenReturn(
				eventDefinitionCrfBeanList.get(index));
	}

	/**
	 * Method tests that SignUtil.permitSign returns true.
	 */
	@Test
	public void testThatPermitSignReturnsTrue() {
		assertTrue(SignUtil.permitSign(studySubjectBean, daoWrapper));
	}
}
