/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public static final String STARTED_EVENT_CRF_FOUND = "started_event_crf_found";

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ItemSDVService itemSDVService;

	@Autowired
	private EventCRFService eventCRFService;

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
			Map<Integer, EventDefinitionCRFBean> parentsMap, Map<Integer, EventDefinitionCRFBean> parentsBeforeUpdateMap,
			UserAccountBean updater) {
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();
		for (EventDefinitionCRFBean childEdc : childEventDefinitionCRFsToUpdate) {
			EventDefinitionCRFBean parentEdc = parentsMap.get(childEdc.getParentId());
			EventDefinitionCRFBean oldParentEdc = parentsBeforeUpdateMap.get(childEdc.getParentId());

			if (parentEdc != null && oldParentEdc != null) {
				int propagateChange = parentEdc.getPropagateChange();
				String versionIds = childEdc.getSelectedVersionIds();
				childEdc.setDefaultVersionId(parentEdc.getDefaultVersionId());
				childEdc.setAcceptNewCrfVersions(parentEdc.isAcceptNewCrfVersions());
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

				if (shouldParameterBeUpdated(oldParentEdc.isRequiredCRF(), childEdc.isRequiredCRF(), propagateChange)) {
					childEdc.setRequiredCRF(parentEdc.isRequiredCRF());
				}
				if (shouldParameterBeUpdated(oldParentEdc.isElectronicSignature(), childEdc.isElectronicSignature(), propagateChange)) {
					childEdc.setElectronicSignature(parentEdc.isElectronicSignature());
				}
				if (shouldParameterBeUpdated(oldParentEdc.isHideCrf(), childEdc.isHideCrf(), propagateChange)) {
					childEdc.setHideCrf(parentEdc.isHideCrf());
				}
				if (shouldParameterBeUpdated(oldParentEdc.getSourceDataVerification(), childEdc.getSourceDataVerification(), propagateChange)) {
					childEdc.setSourceDataVerification(parentEdc.getSourceDataVerification());
				}
				if (shouldParameterBeUpdated(oldParentEdc.isDoubleEntry(), childEdc.isDoubleEntry(), propagateChange)) {
					childEdc.setDoubleEntry(parentEdc.isDoubleEntry());
				}
				if (shouldParameterBeUpdated(oldParentEdc.isEvaluatedCRF(), childEdc.isEvaluatedCRF(), propagateChange)) {
					childEdc.setEvaluatedCRF(parentEdc.isEvaluatedCRF());
				}
				if (shouldParameterBeUpdated(oldParentEdc.getEmailStep(), childEdc.getEmailStep(), propagateChange)) {
					childEdc.setEmailStep(parentEdc.getEmailStep());
				}
				if (shouldParameterBeUpdated(oldParentEdc.getEmailTo(), childEdc.getEmailTo(), propagateChange)) {
					childEdc.setEmailTo(parentEdc.getEmailTo());
				}
				if (shouldParameterBeUpdated(oldParentEdc.getTabbingMode(), childEdc.getTabbingMode(), propagateChange)) {
					childEdc.setTabbingMode(parentEdc.getTabbingMode());
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
		SourceDataVerification.fillSDVStatuses(eventDefinitionCRFBean.getSdvOptions(),
				itemSDVService.hasItemsToSDV(crfBean.getId()));
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
	public void removeEventDefinitionCrf(EventDefinitionCRFBean parentEventDefinitionCRFBean, UserAccountBean updater)
			throws Exception {
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
				.findByPK(parentEventDefinitionCRFBean.getStudyEventDefinitionId());

		parentEventDefinitionCRFBean.setUpdater(updater);
		parentEventDefinitionCRFBean.setStatus(Status.DELETED);
		eventDefinitionCrfDao.updateStatus(parentEventDefinitionCRFBean);
		eventCRFService.removeEventCRFsByEventDefinitionCRF(studyEventDefinitionBean.getOid(),
				parentEventDefinitionCRFBean.getCrf().getOid(), updater);

		List<EventDefinitionCRFBean> childEventDefinitionCRFBeanList = eventDefinitionCrfDao
				.findAllChildrenByParentId(parentEventDefinitionCRFBean.getId());
		for (EventDefinitionCRFBean childEventDefinitionCRFBean : childEventDefinitionCRFBeanList) {
			childEventDefinitionCRFBean.setUpdater(updater);
			childEventDefinitionCRFBean.setStatus(Status.DELETED);
			eventDefinitionCrfDao.updateStatus(childEventDefinitionCRFBean);
			eventCRFService.removeEventCRFsByEventDefinitionCRF(studyEventDefinitionBean.getOid(),
					parentEventDefinitionCRFBean.getCrf().getOid(), updater);
		}

		SubjectEventStatusUtil.determineSubjectEventStates(studyEventDefinitionBean, updater,
				new DAOWrapper(dataSource), null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreEventDefinitionCrf(EventDefinitionCRFBean parentEventDefinitionCRFBean, UserAccountBean updater)
			throws Exception {
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
				.findByPK(parentEventDefinitionCRFBean.getStudyEventDefinitionId());

		parentEventDefinitionCRFBean.setUpdater(updater);
		parentEventDefinitionCRFBean.setStatus(Status.AVAILABLE);
		eventDefinitionCrfDao.updateStatus(parentEventDefinitionCRFBean);
		eventCRFService.restoreEventCRFsByEventDefinitionCRF(studyEventDefinitionBean.getOid(),
				parentEventDefinitionCRFBean.getCrf().getOid(), updater);

		List<EventDefinitionCRFBean> childEventDefinitionCRFBeanList = eventDefinitionCrfDao
				.findAllChildrenByParentId(parentEventDefinitionCRFBean.getId());
		for (EventDefinitionCRFBean childEventDefinitionCRFBean : childEventDefinitionCRFBeanList) {
			childEventDefinitionCRFBean.setUpdater(updater);
			childEventDefinitionCRFBean.setStatus(Status.AVAILABLE);
			eventDefinitionCrfDao.updateStatus(childEventDefinitionCRFBean);
			eventCRFService.restoreEventCRFsByEventDefinitionCRF(studyEventDefinitionBean.getOid(),
					parentEventDefinitionCRFBean.getCrf().getOid(), updater);
		}

		SubjectEventStatusUtil.determineSubjectEventStates(studyEventDefinitionBean, updater,
				new DAOWrapper(dataSource), null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateDefaultVersionOfEventDefinitionCRF(EventDefinitionCRFBean edcBean,
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
				if ((idList.size() == 0 || idList.contains(versionBean.getId()))
						&& versionBean.isAvailable()) {
					edcBean.setDefaultVersionId(versionBean.getId());
					edcBean.setUpdater(updater);
					eventDefinitionCRFDAO.update(edcBean);
					break;
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteEventDefinitionCrf(EventDefinitionCRFBean eventDefinitionCRFBean) throws Exception {
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		List<EventCRFBean> eventCRFBeans = eventCRFDAO.findAllByEventDefinitionCRFId(eventDefinitionCRFBean.getId());
		for (EventCRFBean eventCRFBean : eventCRFBeans) {
			if (!eventCRFBean.isNotStarted()) {
				throw new Exception(STARTED_EVENT_CRF_FOUND);
			} else {
				eventCRFDAO.delete(eventCRFBean.getId());
			}
		}
		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		List<EventDefinitionCRFBean> childEventDefinitionCRFs = eventDefinitionCRFDAO.findAllChildrenByParentId(eventDefinitionCRFBean.getId());
		for (EventDefinitionCRFBean child : childEventDefinitionCRFs) {
			eventDefinitionCRFDAO.delete(child.getId());
		}
		eventDefinitionCRFDAO.delete(eventDefinitionCRFBean.getId());
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

	private boolean shouldParameterBeUpdated(Object parentValue, Object childValue, int propagateChange) {
		return propagateChange == 1 || (propagateChange == 2 && parentValue.equals(childValue));
	}

	private CRFDAO getCRFDAO() {
		return new CRFDAO(dataSource);
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
}
