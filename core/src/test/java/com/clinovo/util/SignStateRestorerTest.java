package com.clinovo.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.util.EventCrfInfo;
import org.akaza.openclinica.util.EventDefinitionInfo;
import org.akaza.openclinica.util.SignedData;
import org.junit.Before;
import org.junit.Test;

public class SignStateRestorerTest {

	private StudyEventBean studyEventBean;

	@Before
	public void setUp() throws Exception {
		studyEventBean = new StudyEventBean();

		Map<Integer, SignedData> savedSignedData = new HashMap<Integer, SignedData>();
		addObjects(savedSignedData, 1, true, 1, 1, true, 1);
		addObjects(savedSignedData, 2, true, 1, 2, true, 1);
		addObjects(savedSignedData, 3, true, 1, 3, true, 1);

		studyEventBean.setSignedData(savedSignedData);
	}

	private void addObjects(Map<Integer, SignedData> savedSignedData, int eciId, boolean eciSdv, int eciStatusId,
			int ediId, boolean ediRequired, int ediDefaultVersionId) {
		SignedData sd = new SignedData();
		EventCrfInfo eci = new EventCrfInfo();
		eci.id = eciId;
		eci.sdv = eciSdv;
		eci.statusId = eciStatusId;
		sd.eventCrfInfo = eci;
		EventDefinitionInfo edi = new EventDefinitionInfo();
		edi.id = ediId;
		edi.required = ediRequired;
		edi.defaultVersionId = ediDefaultVersionId;
		sd.eventDefinitionInfo = edi;
		savedSignedData.put(edi.id, sd);
	}

	@Test
	public void testThatStudyEventBeanWillHaveSignedStatus() throws Exception {
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.SOURCE_DATA_VERIFIED);
		studyEventBean.setPrevSubjectEventStatus(SubjectEventStatus.SIGNED);

		Map<Integer, SignedData> currSignedData = new HashMap<Integer, SignedData>();
		addObjects(currSignedData, 1, true, 1, 1, true, 1);
		addObjects(currSignedData, 2, true, 1, 2, true, 1);
		addObjects(currSignedData, 3, true, 1, 3, true, 1);

		SignStateRestorer.restoreSignState(studyEventBean, SubjectEventStatus.DATA_ENTRY_STARTED,
				SubjectEventStatus.SIGNED, studyEventBean.getSignedData(), currSignedData);

		assertEquals(studyEventBean.getSubjectEventStatus(), SubjectEventStatus.SIGNED);
	}

	@Test
	public void testThatStudyEventBeanWillNotHaveSignedStatus() throws Exception {
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.SOURCE_DATA_VERIFIED);
		studyEventBean.setPrevSubjectEventStatus(SubjectEventStatus.SIGNED);

		Map<Integer, SignedData> currSignedData = new HashMap<Integer, SignedData>();
		addObjects(currSignedData, 1, true, 1, 1, true, 1);
		addObjects(currSignedData, 2, true, 1, 2, true, 1);
		addObjects(currSignedData, 3, true, 1, 3, true, 1);
		addObjects(currSignedData, 4, true, 1, 4, true, 1);

		SignStateRestorer.restoreSignState(studyEventBean, SubjectEventStatus.DATA_ENTRY_STARTED,
				SubjectEventStatus.SIGNED, studyEventBean.getSignedData(), currSignedData);

		assertNotEquals(studyEventBean.getSubjectEventStatus(), SubjectEventStatus.SIGNED);
	}

	@Test
	public void testThatStudyEventBeanWillHaveCompletedStatus() throws Exception {
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
		studyEventBean.setPrevSubjectEventStatus(SubjectEventStatus.SIGNED);

		Map<Integer, SignedData> currSignedData = new HashMap<Integer, SignedData>();
		addObjects(currSignedData, 1, false, 1, 1, true, 1);
		addObjects(currSignedData, 2, true, 1, 2, true, 1);
		addObjects(currSignedData, 3, true, 1, 3, true, 1);

		SignStateRestorer.restoreSignState(studyEventBean, SubjectEventStatus.DATA_ENTRY_STARTED,
				SubjectEventStatus.SIGNED, studyEventBean.getSignedData(), currSignedData);

		assertEquals(studyEventBean.getSubjectEventStatus(), SubjectEventStatus.COMPLETED);
	}

	@Test
	public void testThatStudyEventBeanWillHaveSDVStatus() throws Exception {
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.SOURCE_DATA_VERIFIED);
		studyEventBean.setPrevSubjectEventStatus(SubjectEventStatus.SIGNED);

		Map<Integer, SignedData> currSignedData = new HashMap<Integer, SignedData>();
		addObjects(currSignedData, 1, true, 1, 1, true, 1);
		addObjects(currSignedData, 2, true, 1, 2, false, 1);
		addObjects(currSignedData, 3, true, 1, 3, true, 1);

		SignStateRestorer.restoreSignState(studyEventBean, SubjectEventStatus.DATA_ENTRY_STARTED,
				SubjectEventStatus.SIGNED, studyEventBean.getSignedData(), currSignedData);

		assertEquals(studyEventBean.getSubjectEventStatus(), SubjectEventStatus.SOURCE_DATA_VERIFIED);
	}
}
