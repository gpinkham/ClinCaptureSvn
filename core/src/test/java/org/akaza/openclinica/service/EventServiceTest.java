package org.akaza.openclinica.service;

import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.junit.Before;
import org.junit.Test;

public class EventServiceTest extends DefaultAppContextTest {

	private StudyEventDefinitionBean studyEventDefinition;
	private StudySubjectBean studySubject;
	private List<StudyEventBean> studyEvents;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		studyEventDefinition = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(2);
		studySubject = (StudySubjectBean) studySubjectDAO.findByPK(1);
		studyEvents = (List<StudyEventBean>) studyEventDao.findAllByDefinitionAndSubjectOrderByOrdinal(
				studyEventDefinition, studySubject);
		for (StudyEventBean event : studyEvents) {
			if (event.getId() == 2) {
				event.setSampleOrdinal(2);
			}
			if (event.getId() == 3) {
				event.setSampleOrdinal(4);
			}
			if (event.getId() == 4) {
				event.setSampleOrdinal(6);
			}
		}
	}

	@Test
	public void testThatRegenerateStudyEventOrdinalsPutsThemInAscendingOrder() {
		eventService.regenerateStudyEventOrdinals(studyEvents);
		int ordinal = 1;
		boolean ordered = true;
		for (StudyEventBean event : studyEvents) {
			if (ordered) {
				ordered = event.getSampleOrdinal() == ordinal;
			}
			ordinal++;
		}
		assertTrue(ordered);
	}
}
