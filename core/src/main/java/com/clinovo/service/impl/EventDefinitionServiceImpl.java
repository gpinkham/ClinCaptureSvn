package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.util.EventDefinitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.model.EDCItemMetadata;
import com.clinovo.service.EventCRFService;
import com.clinovo.service.EventDefinitionCrfService;
import com.clinovo.service.EventDefinitionService;
import com.clinovo.service.StudyEventService;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SignStateRestorer;
import com.clinovo.util.SubjectEventStatusUtil;

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
	private StudyEventService studyEventService;

	@Autowired
	private EventDefinitionCrfService eventDefinitionCrfService;

	private void disableStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater,
			Status status) throws Exception {
		studyEventDefinitionBean.setStatus(status);
		studyEventDefinitionBean.setUpdater(updater);
		studyEventDefinitionBean.setStatus(status);
		studyEventDefinitionBean.setUpdatedDate(new Date());
		getStudyEventDefinitionDAO().update(studyEventDefinitionBean);
		eventDefinitionCrfService.removeEventDefinitionCRFs(studyEventDefinitionBean, updater);
		studyEventService.removeStudyEvents(studyEventDefinitionBean, updater);
	}

	private void enableStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater)
			throws Exception {
		studyEventDefinitionBean.setUpdater(updater);
		studyEventDefinitionBean.setUpdatedDate(new Date());
		studyEventDefinitionBean.setStatus(Status.AVAILABLE);
		getStudyEventDefinitionDAO().update(studyEventDefinitionBean);
		eventDefinitionCrfService.restoreEventDefinitionCRFs(studyEventDefinitionBean, updater);
		studyEventService.restoreStudyEvents(studyEventDefinitionBean, updater);
	}

	private void disableStudyEventDefinitions(StudyBean studyBean, UserAccountBean updater, Status status)
			throws Exception {
		List<StudyEventDefinitionBean> studyEventDefinitionBeanList = getStudyEventDefinitionDAO()
				.findAllByStudy(studyBean);
		for (StudyEventDefinitionBean studyEventDefinitionBean : studyEventDefinitionBeanList) {
			if (studyEventDefinitionBean.getStatus().isAvailable()) {
				disableStudyEventDefinition(studyEventDefinitionBean, updater, status);
			}
		}
	}

	private void enableStudyEventDefinitions(StudyBean studyBean, UserAccountBean updater) throws Exception {
		List<StudyEventDefinitionBean> studyEventDefinitionBeanList = getStudyEventDefinitionDAO()
				.findAllByStudy(studyBean);
		for (StudyEventDefinitionBean studyEventDefinitionBean : studyEventDefinitionBeanList) {
			if (studyEventDefinitionBean.getStatus().isAutoDeleted()) {
				enableStudyEventDefinition(studyEventDefinitionBean, updater);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void createStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean owner,
			StudyBean studyBean) {
		StudyEventDefinitionDAO studyEventDefinitionDao = getStudyEventDefinitionDAO();
		ArrayList defs = studyEventDefinitionDao.findAllByStudy(studyBean);
		studyEventDefinitionBean.setOrdinal(defs == null || defs.isEmpty()
				? 1
				: ((StudyEventDefinitionBean) defs.get(defs.size() - 1)).getOrdinal() + 1);
		studyEventDefinitionBean.setOwner(owner);
		studyEventDefinitionBean.setCreatedDate(new Date());
		studyEventDefinitionBean.setStatus(Status.AVAILABLE);
		studyEventDefinitionBean.setStudyId(studyBean.getId());
		studyEventDefinitionDao.create(studyEventDefinitionBean);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateTheWholeStudyEventDefinition(StudyBean studyBean, UserAccountBean updater,
			StudyEventDefinitionBean studyEventDefinitionBean, List<EventDefinitionCRFBean> eventDefinitionCRFsToUpdate,
			List<EventDefinitionCRFBean> childEventDefinitionCRFsToUpdate, List<EventDefinitionCRFBean> oldEDCs,
			Map<Integer, SignStateRestorer> signStateRestorerMap,
			HashMap<Integer, ArrayList<EDCItemMetadata>> edcItemMetadataMap) throws Exception {
		studyEventDefinitionBean.setUpdater(updater);
		studyEventDefinitionBean.setUpdatedDate(new Date());
		studyEventDefinitionBean.setStatus(Status.AVAILABLE);
		getStudyEventDefinitionDAO().update(studyEventDefinitionBean);
		updateAllEventDefinitionCRFs(studyBean, updater, studyEventDefinitionBean, eventDefinitionCRFsToUpdate,
				childEventDefinitionCRFsToUpdate, oldEDCs, signStateRestorerMap, edcItemMetadataMap);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateAllEventDefinitionCRFs(StudyBean studyBean, UserAccountBean updater,
			StudyEventDefinitionBean studyEventDefinitionBean, List<EventDefinitionCRFBean> eventDefinitionCRFsToUpdate,
			List<EventDefinitionCRFBean> childEventDefinitionCRFsToUpdate, List<EventDefinitionCRFBean> oldEDCs,
			Map<Integer, SignStateRestorer> signStateRestorerMap,
			HashMap<Integer, ArrayList<EDCItemMetadata>> edcItemMetadataMap) throws Exception {
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();
		Map<Integer, EventDefinitionCRFBean> parentsMap = new HashMap<Integer, EventDefinitionCRFBean>();
		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCRFsToUpdate) {
			parentsMap.put(eventDefinitionCRFBean.getId(), eventDefinitionCRFBean);
			if (eventDefinitionCRFBean.getId() > 0) {
				eventDefinitionCRFBean.setUpdater(updater);
				eventDefinitionCRFBean.setUpdatedDate(new Date());
				eventDefinitionCrfDao.update(eventDefinitionCRFBean);
				if (eventDefinitionCRFBean.getStatus().isDeleted()) {
					eventCRFService.removeEventCRFs(studyEventDefinitionBean.getOid(),
							eventDefinitionCRFBean.getCrf().getOid(), updater);
				} else
					if (eventDefinitionCRFBean.getOldStatus() != null
							&& eventDefinitionCRFBean.getOldStatus().equals(Status.DELETED)) {
					eventCRFService.restoreEventCRFs(studyEventDefinitionBean.getOid(),
							eventDefinitionCRFBean.getCrf().getOid(), updater);
				}
			} else {
				addEventDefinitionCRF(eventDefinitionCRFBean, studyBean, updater);
			}
		}

		Map<Integer, EventDefinitionCRFBean> oldEDCsMap = new HashMap<Integer, EventDefinitionCRFBean>();
		for (EventDefinitionCRFBean edc : oldEDCs) {
			oldEDCsMap.put(edc.getId(), edc);
		}
		eventDefinitionCrfService.updateChildEventDefinitionCRFs(childEventDefinitionCRFsToUpdate, parentsMap,
				oldEDCsMap, updater);
		// Item Level SDV support
		eventDefinitionCrfService.checkIfEventCRFSDVStatusWasUpdated(parentsMap, oldEDCsMap, edcItemMetadataMap,
				updater);
		SubjectEventStatusUtil.determineSubjectEventStates(studyEventDefinitionBean, updater,
				new DAOWrapper(dataSource), signStateRestorerMap);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRFBean, StudyBean studyBean,
			UserAccountBean owner) {
		eventDefinitionCRFBean.setOwner(owner);
		eventDefinitionCRFBean.setCreatedDate(new Date());
		eventDefinitionCRFBean.setStatus(Status.AVAILABLE);
		getEventDefinitionCRFDAO().create(eventDefinitionCRFBean);
		createChildEventDefinitionCrfs(eventDefinitionCRFBean, studyBean);
	}

	/**
	 * {@inheritDoc}
	 */
	public EventDefinitionCRFBean updateChildEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRFBean,
			UserAccountBean updater) {
		eventDefinitionCRFBean.setUpdater(updater);
		eventDefinitionCRFBean.setUpdatedDate(new Date());
		new EventDefinitionCRFDAO(dataSource).update(eventDefinitionCRFBean);
		return eventDefinitionCRFBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public StudyEventDefinitionBean updateOnlyTheStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean,
			UserAccountBean updater) {
		Map<Integer, SignStateRestorer> signStateRestorerMap = prepareSignStateRestorer(studyEventDefinitionBean);
		studyEventDefinitionBean.setUpdatedDate(new Date());
		studyEventDefinitionBean.setStatus(Status.AVAILABLE);
		studyEventDefinitionBean.setUpdater(updater);
		getStudyEventDefinitionDAO().update(studyEventDefinitionBean);
		SubjectEventStatusUtil.determineSubjectEventStates(studyEventDefinitionBean,
				studyEventDefinitionBean.getUpdater(), new DAOWrapper(dataSource), signStateRestorerMap);
		return studyEventDefinitionBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public StudyEventDefinitionBean fillEventDefinitionCrfs(StudyEventDefinitionBean studyEventDefinitionBean,
			StudyBean currentStudy) {
		List<EventDefinitionCRFBean> eventDefinitionCrfs = (List<EventDefinitionCRFBean>) getEventDefinitionCRFDAO()
				.findAllActiveByEventDefinitionId(studyEventDefinitionBean.getId());
		fillEventDefinitionCrfs(studyEventDefinitionBean, eventDefinitionCrfs);
		studyEventDefinitionBean.setEventDefinitionCrfs(eventDefinitionCrfs);
		return studyEventDefinitionBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<Integer, SignStateRestorer> prepareSignStateRestorer(StudyEventDefinitionBean studyEventDefinitionBean) {
		Map<Integer, SignStateRestorer> signStateRestorerMap = new HashMap<Integer, SignStateRestorer>();
		List<EventDefinitionCRFBean> eventDefinitionCRFBeanList = getEventDefinitionCRFDAO()
				.findAllActiveByEventDefinitionId(studyEventDefinitionBean.getId());
		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCRFBeanList) {
			if (eventDefinitionCRFBean.getStatus() != Status.AVAILABLE || !eventDefinitionCRFBean.isActive()) {
				continue;
			}
			SignStateRestorer signStateRestorer = signStateRestorerMap.get(eventDefinitionCRFBean.getStudyId());
			if (signStateRestorer == null) {
				signStateRestorer = new SignStateRestorer();
				signStateRestorerMap.put(eventDefinitionCRFBean.getStudyId(), signStateRestorer);
			}
			EventDefinitionInfo edi = new EventDefinitionInfo();
			edi.id = eventDefinitionCRFBean.getId();
			edi.required = eventDefinitionCRFBean.isRequiredCRF();
			edi.defaultVersionId = eventDefinitionCRFBean.getDefaultVersionId();
			signStateRestorer.getEventDefinitionInfoMap().put(eventDefinitionCRFBean.getId(), edi);
		}
		return signStateRestorerMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EventDefinitionCRFBean> getAllChildrenEventDefinitionCrfs(
			StudyEventDefinitionBean studyEventDefinitionBean) {
		List<EventDefinitionCRFBean> eventDefinitionCrfs = getEventDefinitionCRFDAO()
				.findAllChildrenByDefinition(studyEventDefinitionBean.getId());
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
	public StudyEventDefinitionBean removeStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean,
			UserAccountBean updater) throws Exception {
		disableStudyEventDefinition(studyEventDefinitionBean, updater, Status.DELETED);
		return studyEventDefinitionBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public StudyEventDefinitionBean restoreStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean,
			UserAccountBean updater) throws Exception {
		enableStudyEventDefinition(studyEventDefinitionBean, updater);
		return studyEventDefinitionBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStudyEventDefinitions(StudyBean studyBean, UserAccountBean updater) throws Exception {
		disableStudyEventDefinitions(studyBean, updater, Status.AUTO_DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudyEventDefinitions(StudyBean studyBean, UserAccountBean updater) throws Exception {
		enableStudyEventDefinitions(studyBean, updater);
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
