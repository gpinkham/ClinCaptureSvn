package com.clinovo.service.impl;

import com.clinovo.dao.CRFMaskingDAO;
import com.clinovo.model.CRFMask;
import com.clinovo.service.CRFMaskingService;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.domain.datamap.EventCrf;
import org.akaza.openclinica.domain.datamap.StudyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Masking Service implementation.
 */
@Transactional
@Service("crfMaskingService")
@SuppressWarnings("unchecked")
public class CRFMaskingServiceImpl implements CRFMaskingService {

	@Autowired
	private CRFMaskingDAO maskingDAO;

	@Autowired
	private DataSource dataSource;

	/**
	 * {@inheritDoc}
	 */
	public List<CRFMask> findAll() {
		return maskingDAO.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CRFMask> findByUserId(int id) {
		return maskingDAO.findByUserId(id);
	}

	/**
	 * {@inheritDoc}
	 */
	public void saveCRFMask(CRFMask mask) {
		maskingDAO.saveOrUpdate(mask);
	}

	/**
	 * {@inheritDoc}
	 */
	public CRFMask findByUserIdSiteIdAndCRFId(int userId, int siteId, int crfId) {
		return maskingDAO.findByUserIdSiteIdAndCRFId(userId, siteId, crfId);
	}

	/**
	 * {@inheritDoc}
	 */
	public void delete(CRFMask mask) {
		maskingDAO.delete(mask);
	}

	/**
	 * {@inheritDoc}
	 */
	public CRFMask findActiveByUserIdSiteIdAndCRFId(int userId, int siteId, int crfId) {
		return maskingDAO.findActiveByUserIdSiteIdAndCRFId(userId, siteId, crfId);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEventDefinitionCRFMasked(int crfId, int userId, int studyId) {
		return findActiveByUserIdSiteIdAndCRFId(userId, studyId, crfId) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateMasksOnUserRoleUpdate(Role oldRole, Role newRole, StudyBean study, int userId) {
		if (oldRole.getId() != newRole.getId()) {
			if (oldRole == Role.INVESTIGATOR && study.isSite()) {
				maskingDAO.restoreMasksBySiteAndUserIds(userId, study.getId());
				return;
			}
			if (newRole == Role.INVESTIGATOR && study.isSite()) {
				maskingDAO.removeMasksBySiteAndUserIds(userId, study.getId());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ArrayList<Object> removeMaskedDisplayEventDefinitionAndEventCRFBeans(List<Object> beansList, UserAccountBean user) {
		ArrayList<Object> newList = new ArrayList<Object>();
		for (Object object : beansList) {
			if (object instanceof DisplayEventDefinitionCRFBean) {
				DisplayEventDefinitionCRFBean dedc = (DisplayEventDefinitionCRFBean) object;
				int eventCRFId = dedc.getEdc().getId();
				int studyId = dedc.getEdc().getStudyId();
				if (!isEventDefinitionCRFMasked(eventCRFId, user.getId(), studyId)) {
					newList.add(dedc);
				}
			} else if (object instanceof DisplayEventCRFBean) {
				DisplayEventCRFBean dec = (DisplayEventCRFBean) object;
				int eventCRFId = dec.getEventDefinitionCRF().getId();
				int studyId = dec.getEventDefinitionCRF().getStudyId();
				if (!isEventDefinitionCRFMasked(eventCRFId, user.getId(), studyId)) {
					newList.add(dec);
				}
			}
		}
		return newList;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CRFBean> removeCRFIfItsMaskedInAllEvents(List<CRFBean> crfs, int studyId, int userId) {
		EventDefinitionCRFDAO eventCRFDAO = new EventDefinitionCRFDAO(dataSource);
		List<CRFBean> result = new ArrayList<CRFBean>();
		for (CRFBean crf : crfs) {
			boolean shouldInclude = false;
			ArrayList<EventDefinitionCRFBean> eventCRFs = eventCRFDAO.findAllActiveByStudyIdAndCRFId(studyId, crf.getId());
			for (EventDefinitionCRFBean eventCRF : eventCRFs) {
				if (!isEventDefinitionCRFMasked(eventCRF.getId(), userId, studyId)) {
					shouldInclude = true;
				}
			}
			if (shouldInclude) {
				result.add(crf);
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CRFMask> findAllActiveByUserStudyAndEventDefinitionIds(int userId, int eventDefinitionId, int studyId) {
		return maskingDAO.findAllActiveByUserStudyAndEventDefinitionIds(userId, eventDefinitionId, studyId);
	}

	/**
	 * {@inheritDoc}
	 */
	public StudyEventDefinitionBean returnFirstNotMaskedEvent(List<StudyEventDefinitionBean> sedList, int userId, int studyId) {
		EventDefinitionCRFDAO eventCRFDAO = new EventDefinitionCRFDAO(dataSource);
		for (StudyEventDefinitionBean sed : sedList) {
			List<CRFMask> masks = findAllActiveByUserStudyAndEventDefinitionIds(userId, sed.getId(), studyId);			
			List<EventDefinitionCRFBean> eventCRFs = (List<EventDefinitionCRFBean>) eventCRFDAO.findAllActiveByDefinitionAndSiteId(sed.getId(), sed.getStudyId());
			if (masks == null || masks.size() < eventCRFs.size()) {
				return sed;
			}
		}
		return new StudyEventDefinitionBean();
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeMaskedCRFsFromStudyEvents(List<StudyEvent> studyEvents, int userId) {
		UserAccountDAO userAccountDAO = new UserAccountDAO(dataSource);
		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		UserAccountBean user = (UserAccountBean) userAccountDAO.findByPK(userId);
		int studyId = user.getActiveStudyId();

		for (StudyEvent studyEvent : studyEvents) {
			ArrayList<EventCrf> notMaskedList = new ArrayList<EventCrf>();
			for (EventCrf eventCrf : studyEvent.getEventCrfs()) {
				EventDefinitionCRFBean edc = eventDefinitionCRFDAO.findForSiteByEventCrfId(eventCrf.getEventCrfId());
				if (edc != null && !isEventDefinitionCRFMasked(edc.getId(), userId, studyId)) {
					notMaskedList.add(eventCrf);
				}
			}
			studyEvent.setEventCrfs(notMaskedList);
		}
	}
}
