package com.clinovo.service.impl;

import com.clinovo.service.AuditLogService;
import org.akaza.openclinica.bean.admin.AuditBean;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Audit Log Service Implementation.
 */
@Transactional
@Service("auditLogService")
public class AuditLogServiceImpl implements AuditLogService {

	@Autowired
	private DataSource dataSource;

	/**
	 * {@inheritDoc}
	 */
	public void addDeletedStudyEvents(StudySubjectBean studySubjectBean, List<StudyEventBean> studyEvents) {
		AuditDAO auditDao = new AuditDAO(dataSource);
		UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
		StudyEventDefinitionDAO studyEventDefinitionDao = new StudyEventDefinitionDAO(dataSource);

		for (AuditBean auditBean : auditDao.findDeletedStudyEvents(studySubjectBean.getId())) {
			StudyEventBean studyEventBean = new StudyEventBean();
			studyEventBean.setId(auditBean.getEntityId());
			studyEventBean.setDateStarted(auditBean.getDateStart());
			studyEventBean.setLocation(auditBean.getLocation());
			studyEventBean.setStudySubjectId(auditBean.getStudySubjectId());
			studyEventBean.setStudyEventDefinitionId(auditBean.getStudyEventDefinitionId());
			studyEventBean.setSampleOrdinal(auditBean.getStudyEventSampleOrdinal());
			studyEventBean.setStatus(Status.get(Integer.parseInt(auditBean.getNewValue())));
			studyEventBean.setOwner((UserAccountBean) userAccountDao.findByPK(auditBean.getUserId()));
			studyEventBean.setUpdater(studyEventBean.getOwner());
			studyEvents.add(studyEventBean);
		}
		for (StudyEventBean studyEventBean : studyEvents) {
			StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDao
					.findByPK(studyEventBean.getStudyEventDefinitionId());
			studyEventBean.setStudyEventDefinitionOrdinal(studyEventDefinitionBean.getOrdinal());
			studyEventBean.setStudyEventDefinition(studyEventDefinitionBean);
		}
		Collections.sort(studyEvents, new Comparator<StudyEventBean>() {
			public int compare(StudyEventBean se1, StudyEventBean se2) {
				int result = new Integer(se1.getStudyEventDefinitionOrdinal())
						.compareTo(se2.getStudyEventDefinitionOrdinal());
				if (result == 0) {
					result = new Integer(se1.getSampleOrdinal()).compareTo(se2.getSampleOrdinal());
					if (result == 0) {
						result = new Integer(se1.getId()).compareTo(se2.getId());
					}
				}
				return result;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void addDeletedEventCRFs(StudySubjectBean studySubjectBean, StudyEventBean studyEventBean) {
		AuditDAO auditDao = new AuditDAO(dataSource);
		UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
		List<AuditBean> auditsList = auditDao.findDeletedEventCrfs(studySubjectBean.getId(), studyEventBean.getId());

		for (AuditBean auditBean : auditsList) {
			CRFBean crfBean = new CRFBean();
			crfBean.setName(auditBean.getCrfName());
			CRFVersionBean crfVersionBean = new CRFVersionBean();
			crfVersionBean.setName(auditBean.getCrfVersion());
			EventCRFBean eventCRFBean = new EventCRFBean();
			eventCRFBean.setCrfVersion(crfVersionBean);
			eventCRFBean.setCrf(crfBean);
			eventCRFBean.setId(auditBean.getEntityId());
			eventCRFBean.setInterviewerName(auditBean.getInterviewerName());
			eventCRFBean.setDateInterviewed(auditBean.getDateInterviewed());
			eventCRFBean.setStudySubjectId(auditBean.getStudySubjectId());
			eventCRFBean.setCRFVersionId(auditBean.getCrfVersionId());
			eventCRFBean.setStudyEventId(auditBean.getStudyEventId());
			eventCRFBean.setStatus(Status.get(Integer.parseInt(auditBean.getNewValue())));
			eventCRFBean.setOwner((UserAccountBean) userAccountDao.findByPK(auditBean.getUserId()));
			eventCRFBean.setUpdater(eventCRFBean.getOwner());
			studyEventBean.getEventCRFs().add(eventCRFBean);
		}
		Collections.sort(studyEventBean.getEventCRFs(), new Comparator<EventCRFBean>() {
			public int compare(EventCRFBean ec1, EventCRFBean ec2) {
				return new Integer(ec1.getId()).compareTo(ec2.getId());
			}
		});
	}
}
