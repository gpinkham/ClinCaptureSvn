package com.clinovo.jmesa.evaluation;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class CRFEvaluationFilterTest extends DefaultAppContextTest {

	public static final String COMPLETED = "completed";
	public static final String SOURCE_DATA_VERIFIED = "source data verified";

	private CRFEvaluationFilter crfEvaluationFilter;
	private Map<Object, Status> optionsMap = new LinkedHashMap<Object, Status>();

	private EventCRFBean eventCRFBean;
	private StudyEventBean studyEventBean;
	private UserAccountBean userAccountBean;

	@Before
	public void setUp() throws Exception {
		optionsMap.put(COMPLETED, Status.COMPLETED);
		optionsMap.put(SOURCE_DATA_VERIFIED, Status.SOURCE_DATA_VERIFIED);
		crfEvaluationFilter = new CRFEvaluationFilter(optionsMap, null, null);
		eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(1);
		userAccountBean = (UserAccountBean) userAccountDAO.findByPK(1);
		studyEventBean = (StudyEventBean) studyEventDao.findByPK(eventCRFBean.getStudyEventId());
	}

	@Test
	public void testThatCRFEvaluationFilterSkipsCompletedEventCrfsWithLockedStudyEvents() {
		crfEvaluationFilter.addFilter(CRFEvaluationFilter.CRF_STATUS, COMPLETED);
		eventCRFBean.setSdvStatus(false);
		eventCRFBean.setStatus(Status.UNAVAILABLE);
		eventCRFBean.setUpdater(userAccountBean);
		eventCRFDAO.update(eventCRFBean);
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.LOCKED);
		studyEventBean.setUpdater(userAccountBean);
		studyEventDao.update(studyEventBean);
		ArrayList list = eventCRFDAO.select("select ec.* from event_crf ec, study_event se where ec.event_crf_id = "
				.concat(Integer.toString(eventCRFBean.getId())).concat(" and ec.study_event_id = se.study_event_id"));
		assertEquals(1, list.size());
		list = eventCRFDAO.select("select ec.* from event_crf ec, study_event se where ec.event_crf_id = "
				.concat(Integer.toString(eventCRFBean.getId())).concat(" and ec.study_event_id = se.study_event_id ")
				.concat(crfEvaluationFilter.execute("")).concat(")"));
		assertEquals(0, list.size());
	}

	@Test
	public void testThatCRFEvaluationFilterSkipsCompletedEventCrfsWithStoppedStudyEvents() {
		crfEvaluationFilter.addFilter(CRFEvaluationFilter.CRF_STATUS, COMPLETED);
		eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(1);
		eventCRFBean.setSdvStatus(false);
		eventCRFBean.setStatus(Status.UNAVAILABLE);
		eventCRFBean.setUpdater(userAccountBean);
		eventCRFDAO.update(eventCRFBean);
		studyEventBean = (StudyEventBean) studyEventDao.findByPK(eventCRFBean.getStudyEventId());
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.STOPPED);
		studyEventBean.setUpdater(userAccountBean);
		studyEventDao.update(studyEventBean);
		ArrayList list = eventCRFDAO.select("select ec.* from event_crf ec, study_event se where ec.event_crf_id = "
				.concat(Integer.toString(eventCRFBean.getId())).concat(" and ec.study_event_id = se.study_event_id"));
		assertEquals(1, list.size());
		list = eventCRFDAO.select("select ec.* from event_crf ec, study_event se where ec.event_crf_id = "
				.concat(Integer.toString(eventCRFBean.getId())).concat(" and ec.study_event_id = se.study_event_id ")
				.concat(crfEvaluationFilter.execute("")).concat(")"));
		assertEquals(0, list.size());
	}

	@Test
	public void testThatCRFEvaluationFilterSkipsSDVdEventCrfsWithSkippedStudyEvents() {
		crfEvaluationFilter.addFilter(CRFEvaluationFilter.CRF_STATUS, SOURCE_DATA_VERIFIED);
		eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(1);
		eventCRFBean.setSdvStatus(true);
		eventCRFBean.setStatus(Status.UNAVAILABLE);
		eventCRFBean.setUpdater(userAccountBean);
		eventCRFDAO.update(eventCRFBean);
		studyEventBean = (StudyEventBean) studyEventDao.findByPK(eventCRFBean.getStudyEventId());
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.SKIPPED);
		studyEventBean.setUpdater(userAccountBean);
		studyEventDao.update(studyEventBean);
		ArrayList list = eventCRFDAO.select("select ec.* from event_crf ec, study_event se where ec.event_crf_id = "
				.concat(Integer.toString(eventCRFBean.getId())).concat(" and ec.study_event_id = se.study_event_id"));
		assertEquals(1, list.size());
		list = eventCRFDAO.select("select ec.* from event_crf ec, study_event se where ec.event_crf_id = "
				.concat(Integer.toString(eventCRFBean.getId())).concat(" and ec.study_event_id = se.study_event_id ")
				.concat(crfEvaluationFilter.execute("")).concat(")"));
		assertEquals(0, list.size());
	}
}
