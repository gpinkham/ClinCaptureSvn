package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.EventDefinitionService;

/**
 * EventDefinitionServiceImpl.
 */
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class EventDefinitionServiceImpl implements EventDefinitionService {

	@Autowired
	private DataSource dataSource;

	/**
	 * {@inheritDoc}
	 */
	public void createStudyEventDefinition(StudyBean studyBean, String emailUser,
			StudyEventDefinitionBean studyEventDefinitionBean) {
		UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
		StudyEventDefinitionDAO studyEventDefinitionDao = new StudyEventDefinitionDAO(dataSource);
		ArrayList defs = studyEventDefinitionDao.findAllByStudy(studyBean);
		studyEventDefinitionBean.setOrdinal(defs == null || defs.isEmpty() ? 1 : ((StudyEventDefinitionBean) defs
				.get(defs.size() - 1)).getOrdinal() + 1);
		int userId = userAccountDao.findByUserName(emailUser).getId();
		studyEventDefinitionBean.setUserEmailId(userId != 0 ? userId : 1);
		studyEventDefinitionBean.setCreatedDate(new Date());
		studyEventDefinitionBean.setStatus(Status.AVAILABLE);
		studyEventDefinitionDao.create(studyEventDefinitionBean);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addEventDefinitionCrf(EventDefinitionCRFBean eventDefinitionCrfBean) {
		eventDefinitionCrfBean.setCreatedDate(new Date());
		eventDefinitionCrfBean.setStatus(Status.AVAILABLE);
		new EventDefinitionCRFDAO(dataSource).create(eventDefinitionCrfBean);
	}

	/**
	 * {@inheritDoc}
	 */
	public void fillEventDefinitionCrfs(StudyBean currentStudy, StudyEventDefinitionBean studyEventDefinitionBean) {
		if (studyEventDefinitionBean != null) {
			Collection<EventDefinitionCRFBean> eventDefinitionCrfs = new EventDefinitionCRFDAO(dataSource)
					.findAllActiveByEventDefinitionId(currentStudy, studyEventDefinitionBean.getId());
			if (eventDefinitionCrfs != null) {
				CRFDAO crfDao = new CRFDAO(dataSource);
				CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
				for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCrfs) {
					CRFVersionBean crfBeanVersion = (CRFVersionBean) crfVersionDao.findByPK(eventDefinitionCRFBean
							.getDefaultVersionId());
					CRFBean crfBean = (CRFBean) crfDao.findByPK(eventDefinitionCRFBean.getCrfId());
					eventDefinitionCRFBean.setEventName(studyEventDefinitionBean.getName());
					eventDefinitionCRFBean.setDefaultVersionName(crfBeanVersion.getName());
					eventDefinitionCRFBean.setCrfName(crfBean.getName());
					studyEventDefinitionBean.getEventDefinitionCrfs().add(eventDefinitionCRFBean);
				}
			}
		}
	}
}
