package com.clinovo.service;

import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;

import java.util.List;

/**
 * Audit Log Service.
 */
public interface AuditLogService {
	/**
	 * Add Deleted Events to the list of StudyEvents
	 * @param studySubjectBean StudySubjectBean
	 * @param studyEvents studyEvents
	 */
	void addDeletedStudyEvents(StudySubjectBean studySubjectBean, List<StudyEventBean> studyEvents);

	/**
	 * Add Deleted EventCRFBeans to the StudyEvent
	 * @param studySubjectBean StudySubjectBean
	 * @param studyEventBean StudyEventBean
	 */
	void addDeletedEventCRFs(StudySubjectBean studySubjectBean, StudyEventBean studyEventBean);
}
