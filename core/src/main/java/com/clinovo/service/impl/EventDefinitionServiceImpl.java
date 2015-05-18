package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.Date;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.EventDefinitionService;

/**
 * EventDefinitionServiceImpl.
 */
@Service
@SuppressWarnings("rawtypes")
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
		studyEventDefinitionBean.setStudyId(studyBean.getId());
		studyEventDefinitionDao.create(studyEventDefinitionBean);
	}
}
