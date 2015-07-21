package com.clinovo.service.impl;

import com.clinovo.service.EventCRFService;
import com.clinovo.service.EventDefinitionCrfService;
import com.clinovo.service.EventDefinitionService;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SignStateRestorer;
import com.clinovo.util.SubjectEventStatusUtil;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.util.EventDefinitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventDefinitionServiceImpl.
 */
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class EventDefinitionServiceImpl implements EventDefinitionService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private EventCRFService eventCRFService;

	@Autowired
	private EventDefinitionCrfService eventDefinitionCrfService;

	/**
	 * {@inheritDoc}
	 */
	public void createStudyEventDefinition(StudyBean studyBean, String emailUser,
			StudyEventDefinitionBean studyEventDefinitionBean) {
		StudyEventDefinitionDAO studyEventDefinitionDao = getStudyEventDefinitionDAO();
		ArrayList defs = studyEventDefinitionDao.findAllByStudy(studyBean);
		studyEventDefinitionBean.setOrdinal(defs == null || defs.isEmpty() ? 1 : ((StudyEventDefinitionBean) defs
				.get(defs.size() - 1)).getOrdinal() + 1);
		int userId = getUserAccountDAO().findByUserName(emailUser).getId();
		studyEventDefinitionBean.setUserEmailId(userId != 0 ? userId : 1);
		studyEventDefinitionBean.setCreatedDate(new Date());
		studyEventDefinitionBean.setStatus(Status.AVAILABLE);
		studyEventDefinitionDao.create(studyEventDefinitionBean);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateTheWholeStudyEventDefinition(StudyBean studyBean, UserAccountBean updater,
			StudyEventDefinitionBean studyEventDefinitionBean,
			List<EventDefinitionCRFBean> eventDefinitionCRFsToUpdate,
			List<EventDefinitionCRFBean> childEventDefinitionCRFsToUpdate, SignStateRestorer signStateRestorer)
			throws Exception {
		studyEventDefinitionBean.setUpdater(updater);
		studyEventDefinitionBean.setUpdatedDate(new Date());
		studyEventDefinitionBean.setStatus(Status.AVAILABLE);
		getStudyEventDefinitionDAO().update(studyEventDefinitionBean);
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();
		Map<Integer, EventDefinitionCRFBean> parentsMap = new HashMap<Integer, EventDefinitionCRFBean>();
		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCRFsToUpdate) {
			parentsMap.put(eventDefinitionCRFBean.getId(), eventDefinitionCRFBean);
			if (eventDefinitionCRFBean.getId() > 0) {
				eventDefinitionCRFBean.setUpdater(updater);
				eventDefinitionCRFBean.setUpdatedDate(new Date());
				eventDefinitionCrfDao.update(eventDefinitionCRFBean);
				if (eventDefinitionCRFBean.getStatus().isDeleted()) {
					eventCRFService.removeEventCRFsByEventDefinitionCRF(studyEventDefinitionBean.getOid(),
							eventDefinitionCRFBean.getCrf().getOid(), updater);
				} else if (eventDefinitionCRFBean.getOldStatus() != null
						&& eventDefinitionCRFBean.getOldStatus().equals(Status.DELETED)) {
					eventCRFService.restoreEventCRFsByEventDefinitionCRF(studyEventDefinitionBean.getOid(),
							eventDefinitionCRFBean.getCrf().getOid(), updater);
				}
			} else {
				eventDefinitionCRFBean.setOwner(updater);
				eventDefinitionCRFBean.setCreatedDate(new Date());
				eventDefinitionCRFBean.setStatus(Status.AVAILABLE);
				EventDefinitionCRFBean createdEdc = (EventDefinitionCRFBean) eventDefinitionCrfDao
						.create(eventDefinitionCRFBean);
				createChildEventDefinitionCrfs(createdEdc, studyBean);
			}
		}
		eventDefinitionCrfService.updateChildEventDefinitionCRFs(childEventDefinitionCRFsToUpdate, parentsMap, updater);
		SubjectEventStatusUtil.determineSubjectEventStates(studyEventDefinitionBean, updater,
				new DAOWrapper(dataSource), signStateRestorer);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateOnlyTheStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean) {
		SignStateRestorer signStateRestorer = prepareSignStateRestorer(getAllParentsEventDefinitionCrfs(studyEventDefinitionBean));
		studyEventDefinitionBean.setUpdatedDate(new Date());
		studyEventDefinitionBean.setStatus(Status.AVAILABLE);
		getStudyEventDefinitionDAO().update(studyEventDefinitionBean);
		SubjectEventStatusUtil.determineSubjectEventStates(studyEventDefinitionBean,
				studyEventDefinitionBean.getUpdater(), new DAOWrapper(dataSource), signStateRestorer);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addEventDefinitionCrf(EventDefinitionCRFBean eventDefinitionCrfBean) {
		eventDefinitionCrfBean.setCreatedDate(new Date());
		eventDefinitionCrfBean.setStatus(Status.AVAILABLE);
		getEventDefinitionCRFDAO().create(eventDefinitionCrfBean);
	}

	/**
	 * {@inheritDoc}
	 */
	public void fillEventDefinitionCrfs(StudyBean currentStudy, StudyEventDefinitionBean studyEventDefinitionBean) {
		List<EventDefinitionCRFBean> eventDefinitionCrfs = (List<EventDefinitionCRFBean>) getEventDefinitionCRFDAO()
				.findAllActiveByEventDefinitionId(currentStudy, studyEventDefinitionBean.getId());
		fillEventDefinitionCrfs(studyEventDefinitionBean, eventDefinitionCrfs);
		studyEventDefinitionBean.setEventDefinitionCrfs(eventDefinitionCrfs);
	}

	/**
	 * {@inheritDoc}
	 */
	public SignStateRestorer prepareSignStateRestorer(List<EventDefinitionCRFBean> eventDefinitionCRFBeanList) {
		SignStateRestorer signStateRestorer = new SignStateRestorer();
		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCRFBeanList) {
			if (eventDefinitionCRFBean.getStatus() != Status.AVAILABLE || !eventDefinitionCRFBean.isActive()) {
				continue;
			}
			EventDefinitionInfo edi = new EventDefinitionInfo();
			edi.id = eventDefinitionCRFBean.getId();
			edi.required = eventDefinitionCRFBean.isRequiredCRF();
			edi.defaultVersionId = eventDefinitionCRFBean.getDefaultVersionId();
			signStateRestorer.getEventDefinitionInfoMap().put(eventDefinitionCRFBean.getId(), edi);
		}
		return signStateRestorer;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EventDefinitionCRFBean> getAllChildrenEventDefinitionCrfs(
			StudyEventDefinitionBean studyEventDefinitionBean) {
		List<EventDefinitionCRFBean> eventDefinitionCrfs = getEventDefinitionCRFDAO().findAllChildrenByDefinition(
				studyEventDefinitionBean.getId());
		fillEventDefinitionCrfs(studyEventDefinitionBean, eventDefinitionCrfs);
		return eventDefinitionCrfs;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EventDefinitionCRFBean> getAllParentsEventDefinitionCrfs(
			StudyEventDefinitionBean studyEventDefinitionBean) {
		List<EventDefinitionCRFBean> eventDefinitionCrfs = (List<EventDefinitionCRFBean>) getEventDefinitionCRFDAO()
				.findAllParentsByDefinition(studyEventDefinitionBean.getId());
		fillEventDefinitionCrfs(studyEventDefinitionBean, eventDefinitionCrfs);
		return eventDefinitionCrfs;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EventDefinitionCRFBean> getAllEventDefinitionCrfs(StudyEventDefinitionBean studyEventDefinitionBean) {
		List<EventDefinitionCRFBean> eventDefinitionCrfs = (List<EventDefinitionCRFBean>) getEventDefinitionCRFDAO()
				.findAllByDefinition(studyEventDefinitionBean.getId());
		fillEventDefinitionCrfs(studyEventDefinitionBean, eventDefinitionCrfs);
		return eventDefinitionCrfs;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<StudyEventBean> getAllStudyEvents(StudyEventDefinitionBean studyEventDefinitionBean) {
		return (List<StudyEventBean>) getStudyEventDAO().findAllByDefinition(studyEventDefinitionBean.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater)
			throws Exception {
		StudyEventDAO studyEventDao = getStudyEventDAO();
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();

		studyEventDefinitionBean.setUpdater(updater);
		studyEventDefinitionBean.setStatus(Status.DELETED);
		studyEventDefinitionBean.setUpdatedDate(new Date());
		getStudyEventDefinitionDAO().updateStatus(studyEventDefinitionBean);

		List<EventDefinitionCRFBean> eventDefinitionCRFBeanList = (List<EventDefinitionCRFBean>) eventDefinitionCrfDao
				.findAllByDefinition(studyEventDefinitionBean.getId());
		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCRFBeanList) {
			if (!eventDefinitionCRFBean.getStatus().equals(Status.DELETED)) {
				eventDefinitionCRFBean.setUpdater(updater);
				eventDefinitionCRFBean.setUpdatedDate(new Date());
				eventDefinitionCRFBean.setStatus(Status.AUTO_DELETED);
				eventDefinitionCrfDao.updateStatus(eventDefinitionCRFBean);
			}
		}

		List<StudyEventBean> studyEventBeanList = (List<StudyEventBean>) studyEventDao
				.findAllByDefinition(studyEventDefinitionBean.getId());
		for (StudyEventBean studyEventBean : studyEventBeanList) {
			if (!studyEventBean.getStatus().equals(Status.DELETED)) {
				studyEventBean.setUpdater(updater);
				studyEventBean.setUpdatedDate(new Date());
				studyEventBean.setStatus(Status.AUTO_DELETED);
				studyEventDao.updateStatus(studyEventBean);
				eventCRFService.removeEventCRFsByStudyEvent(studyEventBean, updater);
			}
		}

		SubjectEventStatusUtil.determineSubjectEventStates(studyEventDefinitionBean, updater,
				new DAOWrapper(dataSource), null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater)
			throws Exception {
		StudyEventDAO studyEventDao = getStudyEventDAO();
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();

		studyEventDefinitionBean.setUpdater(updater);
		studyEventDefinitionBean.setUpdatedDate(new Date());
		studyEventDefinitionBean.setStatus(Status.AVAILABLE);
		getStudyEventDefinitionDAO().updateStatus(studyEventDefinitionBean);

		List<EventDefinitionCRFBean> eventDefinitionCRFBeanList = (List<EventDefinitionCRFBean>) eventDefinitionCrfDao
				.findAllByDefinition(studyEventDefinitionBean.getId());
		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCRFBeanList) {
			if (eventDefinitionCRFBean.getStatus().equals(Status.AUTO_DELETED)) {
				eventDefinitionCRFBean.setUpdater(updater);
				eventDefinitionCRFBean.setUpdatedDate(new Date());
				eventDefinitionCRFBean.setStatus(Status.AVAILABLE);
				eventDefinitionCrfDao.updateStatus(eventDefinitionCRFBean);
			}
		}

		List<StudyEventBean> studyEventBeanList = (List<StudyEventBean>) studyEventDao
				.findAllByDefinition(studyEventDefinitionBean.getId());
		for (StudyEventBean studyEventBean : studyEventBeanList) {
			if (studyEventBean.getStatus().equals(Status.AUTO_DELETED)) {
				studyEventBean.setUpdater(updater);
				studyEventBean.setUpdatedDate(new Date());
				studyEventBean.setStatus(Status.AVAILABLE);
				studyEventDao.updateStatus(studyEventBean);
				eventCRFService.restoreEventCRFsByStudyEvent(studyEventBean, updater);
			}
		}

		SubjectEventStatusUtil.determineSubjectEventStates(studyEventDefinitionBean, updater,
				new DAOWrapper(dataSource), null);
	}

	private void fillEventDefinitionCrfs(StudyEventDefinitionBean studyEventDefinitionBean,
			List<EventDefinitionCRFBean> eventDefinitionCrfs) {
		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCrfs) {
			eventDefinitionCrfService.fillEventDefinitionCrf(eventDefinitionCRFBean, studyEventDefinitionBean);
		}
	}

	private void createChildEventDefinitionCrfs(EventDefinitionCRFBean createdEdc, StudyBean studyBean) {
		StudyDAO studyDao = getStudyDAO();
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();
		Collection<Integer> siteIds = studyDao.findAllSiteIdsByStudy(studyBean);
		siteIds.remove(studyBean.getId());
		int parentId = createdEdc.getId();
		for (int siteId : siteIds) {
			createdEdc.setStudyId(siteId);
			createdEdc.setParentId(parentId);
			eventDefinitionCrfDao.create(createdEdc);
		}
	}

	private StudyDAO getStudyDAO() {
		return new StudyDAO(dataSource);
	}

	private UserAccountDAO getUserAccountDAO() {
		return new UserAccountDAO(dataSource);
	}

	private StudyEventDAO getStudyEventDAO() {
		return new StudyEventDAO(dataSource);
	}

	private StudyEventDefinitionDAO getStudyEventDefinitionDAO() {
		return new StudyEventDefinitionDAO(dataSource);
	}

	private EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
		return new EventDefinitionCRFDAO(dataSource);
	}
}
