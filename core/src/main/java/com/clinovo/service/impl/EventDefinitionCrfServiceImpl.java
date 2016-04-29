/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.clinovo.model.EDCItemMetadata;
import com.clinovo.service.EDCItemMetadataService;
import com.clinovo.service.EventCRFService;
import com.clinovo.service.EventDefinitionCrfService;
import com.clinovo.service.ItemSDVService;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SubjectEventStatusUtil;

/**
 * EventDefinitionCrfServiceImpl.
 */
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class EventDefinitionCrfServiceImpl implements EventDefinitionCrfService {

	public static final String ARRAY_TO_STRING_PATTERN = "\\]|\\[| ";

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ItemSDVService itemSDVService;

	@Autowired
	private EventCRFService eventCRFService;

	@Autowired
	private EDCItemMetadataService edcItemMetadataService;

	private CRFDAO getCRFDAO() {
		return new CRFDAO(dataSource);
	}

	public StudyDAO getStudyDAO() {
		return new StudyDAO(dataSource);
	}

	private CRFVersionDAO getCRFVersionDAO() {
		return new CRFVersionDAO(dataSource);
	}

	private EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
		return new EventDefinitionCRFDAO(dataSource);
	}

	private StudyEventDefinitionDAO getStudyEventDefinitionDAO() {
		return new StudyEventDefinitionDAO(dataSource);
	}

	private void disableEventDefinitionCrf(EventDefinitionCRFBean eventDefinitionCRFBean, UserAccountBean updater,
			Status status) throws Exception {
		eventDefinitionCRFBean.setStatus(status);
		eventDefinitionCRFBean.setUpdater(updater);
		eventDefinitionCRFBean.setUpdatedDate(new Date());
		getEventDefinitionCRFDAO().update(eventDefinitionCRFBean);
	}

	private void enableEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRFBean, UserAccountBean updater)
			throws Exception {
		eventDefinitionCRFBean.setUpdater(updater);
		eventDefinitionCRFBean.setUpdatedDate(new Date());
		eventDefinitionCRFBean.setStatus(Status.AVAILABLE);
		getEventDefinitionCRFDAO().update(eventDefinitionCRFBean);
	}

	private void disableParentEventDefinitionCRF(EventDefinitionCRFBean parentEventDefinitionCRFBean,
			UserAccountBean updater, Status status) throws Exception {
		disableEventDefinitionCrf(parentEventDefinitionCRFBean, updater, status);
		disableChildEventDefinitionCRFs(parentEventDefinitionCRFBean, updater, status);
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
				.findByPK(parentEventDefinitionCRFBean.getStudyEventDefinitionId());
		eventCRFService.removeEventCRFs(studyEventDefinitionBean.getOid(),
				parentEventDefinitionCRFBean.getCrf().getOid(), updater);
		SubjectEventStatusUtil.determineSubjectEventStates(studyEventDefinitionBean, updater,
				new DAOWrapper(dataSource), null);
	}

	private void enableParentEventDefinitionCRF(EventDefinitionCRFBean parentEventDefinitionCRFBean,
			UserAccountBean updater) throws Exception {
		enableEventDefinitionCRF(parentEventDefinitionCRFBean, updater);
		enableChildEventDefinitionCRFs(parentEventDefinitionCRFBean, updater);
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
				.findByPK(parentEventDefinitionCRFBean.getStudyEventDefinitionId());
		eventCRFService.restoreEventCRFs(studyEventDefinitionBean.getOid(),
				parentEventDefinitionCRFBean.getCrf().getOid(), updater);
		SubjectEventStatusUtil.determineSubjectEventStates(studyEventDefinitionBean, updater,
				new DAOWrapper(dataSource), null);
	}

	private void disableChildEventDefinitionCRFs(EventDefinitionCRFBean parentEventDefinitionCRFBean,
			UserAccountBean updater, Status status) throws Exception {
		List<EventDefinitionCRFBean> childEventDefinitionCRFBeanList = getEventDefinitionCRFDAO()
				.findAllChildrenByParentId(parentEventDefinitionCRFBean.getId());
		for (EventDefinitionCRFBean childEventDefinitionCRFBean : childEventDefinitionCRFBeanList) {
			if (childEventDefinitionCRFBean.getStatus().isAvailable()) {
				disableEventDefinitionCrf(childEventDefinitionCRFBean, updater, status);
			}
		}
	}

	private void enableChildEventDefinitionCRFs(EventDefinitionCRFBean parentEventDefinitionCRFBean,
			UserAccountBean updater) throws Exception {
		List<EventDefinitionCRFBean> childEventDefinitionCRFBeanList = getEventDefinitionCRFDAO()
				.findAllChildrenByParentId(parentEventDefinitionCRFBean.getId());
		for (EventDefinitionCRFBean childEventDefinitionCRFBean : childEventDefinitionCRFBeanList) {
			StudyBean studyBean = (StudyBean) getStudyDAO().findByPK(childEventDefinitionCRFBean.getStudyId());
			if (studyBean.getStatus().isAvailable() && !childEventDefinitionCRFBean.getStatus().isAvailable()) {
				enableEventDefinitionCRF(childEventDefinitionCRFBean, updater);
			} else if (!studyBean.getStatus().isAvailable()) {
				disableEventDefinitionCrf(childEventDefinitionCRFBean, updater,
						studyBean.getStatus().isDeleted() ? Status.AUTO_DELETED : studyBean.getStatus());
			}
		}
	}

	private void disableParentEventDefinitionCRFs(StudyEventDefinitionBean studyEventDefinitionBean,
			UserAccountBean updater, Status status) throws Exception {
		List<EventDefinitionCRFBean> parentEventDefinitionCRFBeanList = (List<EventDefinitionCRFBean>) getEventDefinitionCRFDAO()
				.findAllParentsByDefinition(studyEventDefinitionBean.getId());
		for (EventDefinitionCRFBean parentEventDefinitionCRFBean : parentEventDefinitionCRFBeanList) {
			if (parentEventDefinitionCRFBean.getStatus().isAvailable()) {
				disableEventDefinitionCrf(parentEventDefinitionCRFBean, updater, status);
				disableChildEventDefinitionCRFs(parentEventDefinitionCRFBean, updater, status);
			}
		}
	}

	private void enableParentEventDefinitionCRFs(StudyEventDefinitionBean studyEventDefinitionBean,
			UserAccountBean updater) throws Exception {
		List<EventDefinitionCRFBean> parentEventDefinitionCRFBeanList = (List<EventDefinitionCRFBean>) getEventDefinitionCRFDAO()
				.findAllParentsByDefinition(studyEventDefinitionBean.getId());
		for (EventDefinitionCRFBean parentEventDefinitionCRFBean : parentEventDefinitionCRFBeanList) {
			CRFBean crfBean = (CRFBean) getCRFDAO().findByPK(parentEventDefinitionCRFBean.getCrfId());
			if (parentEventDefinitionCRFBean.getStatus().isAutoDeleted() && crfBean.getStatus().isAvailable()) {
				enableEventDefinitionCRF(parentEventDefinitionCRFBean, updater);
				enableChildEventDefinitionCRFs(parentEventDefinitionCRFBean, updater);
			}
		}
	}

	private void disableParentEventDefinitionCRFs(CRFBean crfBean, UserAccountBean updater, Status status)
			throws Exception {
		List<EventDefinitionCRFBean> parentEventDefinitionCRFBeanList = (List<EventDefinitionCRFBean>) getEventDefinitionCRFDAO()
				.findAllParentByCRFId(crfBean.getId());
		for (EventDefinitionCRFBean parentEventDefinitionCRFBean : parentEventDefinitionCRFBeanList) {
			StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
					.findByPK(parentEventDefinitionCRFBean.getStudyEventDefinitionId());
			if (parentEventDefinitionCRFBean.getStatus().isAvailable()) {
				disableEventDefinitionCrf(parentEventDefinitionCRFBean, updater, status);
				disableChildEventDefinitionCRFs(parentEventDefinitionCRFBean, updater, status);
			}
			eventCRFService.removeEventCRFs(studyEventDefinitionBean.getOid(), crfBean.getOid(), updater);
		}
	}

	private void enableParentEventDefinitionCRFs(CRFBean crfBean, UserAccountBean updater) throws Exception {
		List<EventDefinitionCRFBean> parentEventDefinitionCRFBeanList = (List<EventDefinitionCRFBean>) getEventDefinitionCRFDAO()
				.findAllParentByCRFId(crfBean.getId());
		for (EventDefinitionCRFBean parentEventDefinitionCRFBean : parentEventDefinitionCRFBeanList) {
			StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
					.findByPK(parentEventDefinitionCRFBean.getStudyEventDefinitionId());
			if (parentEventDefinitionCRFBean.getStatus().isAutoDeleted()
					&& studyEventDefinitionBean.getStatus().isAvailable()) {
				enableEventDefinitionCRF(parentEventDefinitionCRFBean, updater);
				enableChildEventDefinitionCRFs(parentEventDefinitionCRFBean, updater);
			}
			eventCRFService.restoreEventCRFs(studyEventDefinitionBean.getOid(), crfBean.getOid(), updater);
		}
	}

	private void disableChildEventDefinitionCRFs(StudyBean studyBean, UserAccountBean updater, Status status)
			throws Exception {
		List<EventDefinitionCRFBean> childEventDefinitionCRFBeanList = getEventDefinitionCRFDAO()
				.findAllChildrenByStudy(studyBean);
		for (EventDefinitionCRFBean childEventDefinitionCRFBean : childEventDefinitionCRFBeanList) {
			if (childEventDefinitionCRFBean.getStatus().isAvailable()) {
				disableEventDefinitionCrf(childEventDefinitionCRFBean, updater, status);
			}
		}
	}

	private void enableChildEventDefinitionCRFs(StudyBean studyBean, UserAccountBean updater) throws Exception {
		List<EventDefinitionCRFBean> childEventDefinitionCRFBeanList = getEventDefinitionCRFDAO()
				.findAllChildrenByStudy(studyBean);
		for (EventDefinitionCRFBean childEventDefinitionCRFBean : childEventDefinitionCRFBeanList) {
			EventDefinitionCRFBean parentEventDefinitionCRFBean = (EventDefinitionCRFBean) getEventDefinitionCRFDAO()
					.findByPK(childEventDefinitionCRFBean.getParentId());
			if (parentEventDefinitionCRFBean.getStatus().isAvailable()
					&& !childEventDefinitionCRFBean.getStatus().isAvailable()) {
				enableEventDefinitionCRF(childEventDefinitionCRFBean, updater);
			} else if (!parentEventDefinitionCRFBean.getStatus().isAvailable()) {
				disableEventDefinitionCrf(childEventDefinitionCRFBean, updater,
						parentEventDefinitionCRFBean.getStatus());
			}
		}
	}

	private HashMap processNullValues(EventDefinitionCRFBean edc) {
		String s = "";
		HashMap flags = new LinkedHashMap();
		for (int j = 0; j < edc.getNullValuesList().size(); j++) {
			NullValue nv1 = (NullValue) edc.getNullValuesList().get(j);
			s = s + nv1.getName().toUpperCase() + ",";
		}
		for (int i = 1; i <= NullValue.toArrayList().size(); i++) {
			String nv = NullValue.get(i).getName().toUpperCase();
			Pattern p = Pattern.compile(nv + "\\W");
			Matcher m = p.matcher(s);
			if (m.find()) {
				flags.put(nv, "1");
			} else {
				flags.put(nv, "0");
			}
		}
		return flags;
	}

	private void updateDefaultVersionOfEventDefinitionCRF(EventDefinitionCRFBean edcBean,
			List<CRFVersionBean> versionList, UserAccountBean updater) {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		if (!StringUtil.isBlank(edcBean.getSelectedVersionIds())) {
			String sversionIds = edcBean.getSelectedVersionIds();
			String[] ids = sversionIds.split("\\,");
			for (String id : ids) {
				idList.add(Integer.valueOf(id));
			}
		}
		if ((null != versionList) && (versionList.size() > 0)) {
			EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
			for (CRFVersionBean versionBean : versionList) {
				if ((idList.size() == 0 || idList.contains(versionBean.getId())) && versionBean.isAvailable()) {
					edcBean.setUpdater(updater);
					edcBean.setDefaultVersionId(versionBean.getId());
					eventDefinitionCRFDAO.update(edcBean);
					break;
				}
			}
		}
	}

	private boolean shouldEDCBeUpdated(EventDefinitionCRFBean parentEDC, EventDefinitionCRFBean childEDC, int propagateChange) {
		return propagateChange == 1 || (propagateChange == 2 && childEDC.configurationEquals(parentEDC));
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeEventDefinitionCRFs(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater)
			throws Exception {
		disableParentEventDefinitionCRFs(studyEventDefinitionBean, updater, Status.AUTO_DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreEventDefinitionCRFs(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater)
			throws Exception {
		enableParentEventDefinitionCRFs(studyEventDefinitionBean, updater);
	}

	/**
	 * {@inheritDoc}
	 */
	public EventDefinitionCRFBean removeParentEventDefinitionCrf(EventDefinitionCRFBean parentEventDefinitionCRFBean,
			UserAccountBean updater) throws Exception {
		disableParentEventDefinitionCRF(parentEventDefinitionCRFBean, updater, Status.DELETED);
		return parentEventDefinitionCRFBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public EventDefinitionCRFBean restoreParentEventDefinitionCrf(EventDefinitionCRFBean parentEventDefinitionCRFBean,
			UserAccountBean updater) throws Exception {
		enableParentEventDefinitionCRF(parentEventDefinitionCRFBean, updater);
		return parentEventDefinitionCRFBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeParentEventDefinitionCRFs(CRFBean crfBean, UserAccountBean updater) throws Exception {
		disableParentEventDefinitionCRFs(crfBean, updater, Status.AUTO_DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreParentEventDefinitionCRFs(CRFBean crfBean, UserAccountBean updater) throws Exception {
		enableParentEventDefinitionCRFs(crfBean, updater);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeChildEventDefinitionCRFs(StudyBean studyBean, UserAccountBean updater) throws Exception {
		disableChildEventDefinitionCRFs(studyBean, updater, Status.AUTO_DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreChildEventDefinitionCRFs(StudyBean studyBean, UserAccountBean updater) throws Exception {
		enableChildEventDefinitionCRFs(studyBean, updater);
	}

	/**
	 * {@inheritDoc}
	 */
	public void lockChildEventDefinitionCRFs(StudyBean studyBean, UserAccountBean updater) throws Exception {
		disableChildEventDefinitionCRFs(studyBean, updater, Status.LOCKED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void unlockChildEventDefinitionCRFs(StudyBean studyBean, UserAccountBean updater) throws Exception {
		enableChildEventDefinitionCRFs(studyBean, updater);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateChildEventDefinitionCrfsForNewCrfVersion(CRFVersionBean crfVersionBean, UserAccountBean updater) {
		if (crfVersionBean != null && crfVersionBean.getId() > 0) {
			EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();
			for (EventDefinitionCRFBean edcb : (List<EventDefinitionCRFBean>) eventDefinitionCrfDao
					.findAllByCRF(crfVersionBean.getCrfId())) {
				if (edcb.getParentId() > 0 && edcb.isAcceptNewCrfVersions()) {
					String versionIds = edcb.getSelectedVersionIds();
					if (versionIds != null && !versionIds.trim().isEmpty()) {
						List<String> idList = new ArrayList<String>(Arrays.asList(versionIds.trim().split(",")));
						String crfVersionIdStr = Integer.toString(crfVersionBean.getId());
						if (!idList.contains(crfVersionIdStr)) {
							idList.add(crfVersionIdStr);
							edcb.setSelectedVersionIds(idList.toString().replaceAll(ARRAY_TO_STRING_PATTERN, ""));
							edcb.setUpdatedDate(new Date());
							edcb.setUpdater(updater);
							eventDefinitionCrfDao.update(edcb);
						}
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateChildEventDefinitionCRFs(List<EventDefinitionCRFBean> childEventDefinitionCRFsToUpdate,
			Map<Integer, EventDefinitionCRFBean> parentsMap,
			Map<Integer, EventDefinitionCRFBean> parentsBeforeUpdateMap, UserAccountBean updater) {
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();

		for (EventDefinitionCRFBean childEdc : childEventDefinitionCRFsToUpdate) {
			StudyBean siteBean = (StudyBean) getStudyDAO().findByPK(childEdc.getStudyId());
			EventDefinitionCRFBean parentEdc = parentsMap.get(childEdc.getParentId());
			EventDefinitionCRFBean oldParentEdc = parentsBeforeUpdateMap.get(childEdc.getParentId());

			if (parentEdc != null && oldParentEdc != null) {
				int propagateChange = parentEdc.getPropagateChange();


				String versionIds = childEdc.getSelectedVersionIds();
				if (versionIds != null && !versionIds.trim().isEmpty()) {
					List<String> idList = new ArrayList<String>(Arrays.asList(versionIds.trim().split(",")));
					String parentDefaultVersionId = Integer.toString(parentEdc.getDefaultVersionId());
					if (!idList.contains(parentDefaultVersionId)) {
						idList.add(parentDefaultVersionId);
						childEdc.setSelectedVersionIds(idList.toString().replaceAll(ARRAY_TO_STRING_PATTERN, ""));
					}
				}
				childEdc.setUpdater(updater);
				childEdc.setUpdatedDate(new Date());

				if (shouldEDCBeUpdated(childEdc, oldParentEdc, propagateChange)) {
					childEdc.setDefaultVersionId(parentEdc.getDefaultVersionId());
					childEdc.setAcceptNewCrfVersions(parentEdc.isAcceptNewCrfVersions());
					childEdc.setRequiredCRF(parentEdc.isRequiredCRF());
					childEdc.setElectronicSignature(parentEdc.isElectronicSignature());
					childEdc.setHideCrf(parentEdc.isHideCrf());
					childEdc.setDoubleEntry(parentEdc.isDoubleEntry());
					childEdc.setEvaluatedCRF(parentEdc.isEvaluatedCRF());
					childEdc.setEmailStep(parentEdc.getEmailStep());
					childEdc.setEmailTo(parentEdc.getEmailTo());
					childEdc.setTabbingMode(parentEdc.getTabbingMode());
				}

				// Update for all sites, only study level parameter.
				childEdc.setSourceDataVerification(parentEdc.getSourceDataVerification());

				if (childEdc.getStatus().isAvailable()
						&& (siteBean.getStatus().isDeleted() || siteBean.getStatus().isLocked())) {
					childEdc.setStatus(siteBean.getStatus().isDeleted()
							? Status.AUTO_DELETED
							: siteBean.getStatus());
				}
				eventDefinitionCrfDao.update(childEdc);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void fillEventDefinitionCrf(EventDefinitionCRFBean eventDefinitionCRFBean,
			StudyEventDefinitionBean studyEventDefinitionBean) {
		CRFDAO crfDao = getCRFDAO();
		CRFVersionDAO crfVersionDao = getCRFVersionDAO();
		CRFVersionBean crfBeanVersion = (CRFVersionBean) crfVersionDao
				.findByPK(eventDefinitionCRFBean.getDefaultVersionId());
		CRFBean crfBean = (CRFBean) crfDao.findByPK(eventDefinitionCRFBean.getCrfId());
		ArrayList versions = (ArrayList) crfVersionDao.findAllActiveByCRF(eventDefinitionCRFBean.getCrfId());
		eventDefinitionCRFBean.setNullFlags(processNullValues(eventDefinitionCRFBean));
		eventDefinitionCRFBean.setEventName(studyEventDefinitionBean.getName());
		eventDefinitionCRFBean.setDefaultVersionName(crfBeanVersion.getName());
		eventDefinitionCRFBean.setCrfName(crfBean.getName());
		eventDefinitionCRFBean.setVersions(versions);
		eventDefinitionCRFBean.setCrf(crfBean);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateDefaultVersionOfEventDefinitionCRF(CRFVersionBean crfVersionBean, UserAccountBean updater) {
		ArrayList versionList = (ArrayList) getCRFVersionDAO().findAllByCRF(crfVersionBean.getCrfId());
		List<EventDefinitionCRFBean> eventDefinitionCRFBeanList = (ArrayList) getEventDefinitionCRFDAO()
				.findAllByCRF(crfVersionBean.getCrfId());
		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCRFBeanList) {
			if (eventDefinitionCRFBean.getDefaultVersionId() == crfVersionBean.getId()) {
				updateDefaultVersionOfEventDefinitionCRF(eventDefinitionCRFBean, versionList, updater);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteEventDefinitionCRF(RuleSetService ruleSetService,
			StudyEventDefinitionBean studyEventDefinitionBean, EventDefinitionCRFBean eventDefinitionCRFBean,
			Locale locale) throws Exception {
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);

		List<RuleSetRuleBean> ruleSetRuleBeans = ruleSetService.findAllRulesForEventDefinitionCRF(
				studyEventDefinitionBean.getOid(), eventDefinitionCRFBean.getCrfId());

		List<EventCRFBean> eventCRFs = eventCRFDAO.findAllByEventDefinitionCRFId(eventDefinitionCRFBean.getId());
		List<EventCRFBean> startedEventCRFs = eventCRFService.getAllStartedEventCRFsWithStudyAndEventName(eventCRFs);

		if (ruleSetRuleBeans.size() != 0) {
			throw new Exception(messageSource.getMessage("rules_are_present_for_crfs", null, locale));
		} else if (startedEventCRFs.size() != 0) {
			throw new Exception(messageSource.getMessage("data_is_present_for_crfs", null, locale));
		}

		List<EventCRFBean> eventCRFBeans = eventCRFDAO.findAllByEventDefinitionCRFId(eventDefinitionCRFBean.getId());
		for (EventCRFBean eventCRFBean : eventCRFBeans) {
			eventCRFDAO.delete(eventCRFBean.getId());
		}

		List<EventDefinitionCRFBean> childEventDefinitionCRFs = eventDefinitionCRFDAO
				.findAllChildrenByParentId(eventDefinitionCRFBean.getId());
		for (EventDefinitionCRFBean child : childEventDefinitionCRFs) {
			eventDefinitionCRFDAO.delete(child.getId());
		}
		eventDefinitionCRFDAO.delete(eventDefinitionCRFBean.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public void checkIfEventCRFSDVStatusWasUpdated(Map<Integer, EventDefinitionCRFBean> parentsMap,
			Map<Integer, EventDefinitionCRFBean> oldEDCsMap,
			HashMap<Integer, ArrayList<EDCItemMetadata>> edcItemMetadataMap, UserAccountBean updater) {
		for (Map.Entry<Integer, EventDefinitionCRFBean> entry : parentsMap.entrySet()) {
			int key = entry.getKey();
			EventDefinitionCRFBean edcBean = entry.getValue();
			EventDefinitionCRFBean oldEdcBean = oldEDCsMap.get(key);

			if (oldEdcBean == null) {
				continue;
			}
			boolean newItemsAddedToSDV = false;
			if (edcBean.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED
					&& edcItemMetadataMap != null) {
				newItemsAddedToSDV = saveEDCItemMetadataMapToDatabaseAndCheckIfNewItemsWereAdded(edcItemMetadataMap,
						edcBean);
			}

			if ((oldEdcBean.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED
					&& edcBean.getSourceDataVerification() != SourceDataVerification.PARTIALREQUIRED)
					|| (oldEdcBean.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED
							&& newItemsAddedToSDV)) {
				resetSDVForEventCRFs(edcBean, updater, false, !newItemsAddedToSDV);
				if (!newItemsAddedToSDV) {
					edcItemMetadataService.updateSDVRequiredByEventDefinitionCRF(edcBean, false);
				}
			} else
				if (oldEdcBean.getSourceDataVerification() == SourceDataVerification.AllREQUIRED
						&& edcBean.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED) {
				resetSDVForEventCRFs(edcBean, updater, true, true);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean saveEDCItemMetadataMapToDatabaseAndCheckIfNewItemsWereAdded(
			HashMap<Integer, ArrayList<EDCItemMetadata>> edcItemMetadataMap, EventDefinitionCRFBean edcBean) {

		ArrayList<EDCItemMetadata> edcItemMetadataArrayList = edcItemMetadataMap.get(edcBean.getId());
		boolean newSdvItemsWereAdded = false;

		if (edcItemMetadataArrayList != null && edcItemMetadataArrayList.size() > 0) {
			for (EDCItemMetadata edcItemMetadata : edcItemMetadataArrayList) {
				if (edcItemMetadata.getId() != null && edcItemMetadata.getId() != 0) {
					EDCItemMetadata edcItemMetadataFromDB = edcItemMetadataService.findById(edcItemMetadata.getId());
					if (!edcItemMetadataFromDB.sdvRequired() && edcItemMetadata.sdvRequired()) {
						newSdvItemsWereAdded = true;
					}
					edcItemMetadataFromDB.setSdvRequired(edcItemMetadata.isSdvRequired());
					edcItemMetadataService.saveOrUpdate(edcItemMetadataFromDB);
				} else {
					edcItemMetadataService.saveOrUpdate(edcItemMetadata);
				}
			}
		}

		return newSdvItemsWereAdded;
	}

	/**
	 * Change SDVed flag to false for all event CRFs and item data for this EDC.
	 *
	 * @param edcBean
	 *            EventDefinitionCRFBean
	 * @param updater
	 *            UserAccountBean
	 * @param sdvStatusToSet
	 *            boolean
	 * @param updateItemData
	 *            should ItemData be updated
	 */
	private void resetSDVForEventCRFs(EventDefinitionCRFBean edcBean, UserAccountBean updater, boolean sdvStatusToSet,
			boolean updateItemData) {
		int edcId = edcBean.getParentId() != 0 ? edcBean.getParentId() : edcBean.getId();
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		ItemDataDAO itemDataDAO = new ItemDataDAO(dataSource);
		List<EventCRFBean> eventCRFBeans = eventCRFDAO.findAllWithChildByEventDefinitionCRFId(edcId);

		for (EventCRFBean eventCRF : eventCRFBeans) {
			if (!sdvStatusToSet && shouldResetCRFStatus(eventCRF, updateItemData)) {
				if (eventCRF.isSdvStatus()) {
					eventCRF.setSdvStatus(false);
					eventCRF.setSdvUpdateId(updater.getId());
					eventCRFDAO.update(eventCRF);
				}
				if (updateItemData) {
					itemDataDAO.setSDVByEventCRFId(eventCRF.getId(), updater.getId(), false);
				}
			} else {
				if (eventCRF.isSdvStatus() && updateItemData) {
					itemDataDAO.setSDVByEventCRFId(eventCRF.getId(), updater.getId(), true);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDefaultCRFVersionInsteadOfDeleted(int deletedCRFVersionId) {
		EventDefinitionCRFDAO eventCRFDAO = new EventDefinitionCRFDAO(dataSource);
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(dataSource);
		ArrayList<EventDefinitionCRFBean> crfs = eventCRFDAO.findByDefaultVersion(deletedCRFVersionId);

		for (EventDefinitionCRFBean crf : crfs) {
			CRFVersionBean latestVersion = crfVersionDAO.findLatestAfterDeleted(deletedCRFVersionId);
			if (latestVersion != null) {
				crf.setDefaultVersionId(latestVersion.getId());
				crf.setDefaultVersionName(latestVersion.getName());
				eventCRFDAO.update(crf);
			}
		}
	}

	private boolean shouldResetCRFStatus(EventCRFBean eventCRF, boolean updateItemData) {
		return updateItemData || (itemSDVService.getCountOfItemsToSDV(eventCRF.getId()) != 0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void dropItemLevelSDVConfig(EventDefinitionCRFBean edcBean) {

		if (SourceDataVerification.PARTIALREQUIRED.equals(edcBean.getSourceDataVerification())) {

			List<EventDefinitionCRFBean> recordsToUpdate
					= getEventDefinitionCRFDAO().findAllChildrenByParentId(edcBean.getId());
			recordsToUpdate.add(edcBean);
			for (EventDefinitionCRFBean eventDefCRF: recordsToUpdate) {
				eventDefCRF.setSourceDataVerification(SourceDataVerification.AllREQUIRED);
				getEventDefinitionCRFDAO().update(eventDefCRF);
			}
		}
	}
}
