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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.rest.service.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.util.EventDefinitionCRFUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.clinovo.model.EDCItemMetadata;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.validator.EventServiceValidator;
import com.clinovo.service.EventDefinitionCrfService;
import com.clinovo.service.EventDefinitionService;
import com.clinovo.util.SignStateRestorer;
import com.clinovo.validator.EventDefinitionValidator;

/**
 * BaseEventService.
 */
public abstract class BaseEventService extends BaseService {

	@Autowired
	private EventDefinitionService eventDefinitionService;

	@Autowired
	private EventDefinitionCrfService eventDefinitionCrfService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private DataSource dataSource;

	protected StudyEventDefinitionBean getStudyEventDefinition(int id) throws Exception {
		StudyBean currentStudy = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) new StudyEventDefinitionDAO(
				dataSource).findByPK(id);
		EventServiceValidator.validateStudyEventDefinition(messageSource, id, studyEventDefinitionBean, currentStudy);
		return studyEventDefinitionBean;
	}

	protected StudyEventDefinitionBean prepareNewStudyEventDefinition(String name, String type, String description,
			Boolean repeating, String category, Boolean isReference, Integer schDay, Integer dayMax, Integer dayMin,
			Integer emailDay, String emailUser) throws Exception {
		return prepareStudyEventDefinition(new StudyEventDefinitionBean(), name, type, description, repeating, category,
				isReference, schDay, dayMax, dayMin, emailDay, emailUser);
	}

	protected StudyEventDefinitionBean prepareStudyEventDefinition(StudyBean study, int id, String name, String type,
			String description, Boolean repeating, String category, Boolean isReference, Integer schDay, Integer dayMax,
			Integer dayMin, Integer emailDay, String emailUser) throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) new StudyEventDefinitionDAO(
				dataSource).findByPK(id);
		EventServiceValidator.validateStudyEventDefinition(messageSource, id, studyEventDefinitionBean, study,
				new UserAccountDAO(dataSource), true);
		return prepareStudyEventDefinition(studyEventDefinitionBean, name, type, description, repeating, category,
				isReference, schDay, dayMax, dayMin, emailDay, emailUser);
	}

	private StudyEventDefinitionBean prepareStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean,
			String name, String type, String description, Boolean repeating, String category, Boolean isReference,
			Integer schDay, Integer dayMax, Integer dayMin, Integer emailDay, String emailUser) throws Exception {
		studyEventDefinitionBean.setName(name != null ? name : studyEventDefinitionBean.getName());
		studyEventDefinitionBean.setType(type != null ? type : studyEventDefinitionBean.getType());
		studyEventDefinitionBean
				.setDescription(description != null ? description : studyEventDefinitionBean.getDescription());
		studyEventDefinitionBean.setRepeating(repeating != null ? repeating : studyEventDefinitionBean.isRepeating());
		studyEventDefinitionBean.setCategory(category != null ? category : studyEventDefinitionBean.getCategory());
		if (studyEventDefinitionBean.getType().equals(EventDefinitionValidator.CALENDARED_VISIT)) {
			studyEventDefinitionBean.setReferenceVisit(
					isReference != null ? isReference : studyEventDefinitionBean.getReferenceVisit());
			studyEventDefinitionBean.setMaxDay(dayMax != null ? dayMax : studyEventDefinitionBean.getMaxDay());
			studyEventDefinitionBean.setMinDay(dayMin != null ? dayMin : studyEventDefinitionBean.getMinDay());
			studyEventDefinitionBean
					.setScheduleDay(schDay != null ? schDay : studyEventDefinitionBean.getScheduleDay());
			studyEventDefinitionBean.setEmailDay(emailDay != null ? emailDay : studyEventDefinitionBean.getEmailDay());
		}
		int userId = emailUser != null ? new UserAccountDAO(dataSource).findByUserName(emailUser).getId() : 0;
		studyEventDefinitionBean.setUserEmailId(userId != 0 ? userId : 1);
		return studyEventDefinitionBean;
	}

	protected EventDefinitionCRFBean getStudyEventDefinitionCRF(int eventId, String crfName) throws Exception {
		return getEventDefinitionCRF(eventId, crfName, UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource));
	}

	protected EventDefinitionCRFBean getEventDefinitionCRF(int eventId, String crfName, StudyBean studyBean)
			throws Exception {
		CRFBean crfBean = (CRFBean) new CRFDAO(dataSource).findByName(crfName);
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) new StudyEventDefinitionDAO(
				dataSource).findByPK(eventId);

		if (studyEventDefinitionBean.getId() == 0) {
			throw new RestException(messageSource, "rest.event.isNotFound", new Object[]{eventId},
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (!studyEventDefinitionBean.getStatus().equals(Status.AVAILABLE)) {
			throw new RestException(messageSource, "rest.event.cannotPerformOperationOnEDCBecauseTheSEDIsNotAvailable");
		}

		EventDefinitionCRFBean eventDefinitionCRFBean = new EventDefinitionCRFDAO(dataSource)
				.findByStudyEventDefinitionIdAndCRFIdAndStudyId(eventId, crfBean.getId(), studyBean.getId());

		EventServiceValidator.validateStudyEventDefinitionAndEventDefinitionCrf(messageSource, eventId, crfName,
				crfBean, eventDefinitionCRFBean, studyEventDefinitionBean, studyBean);

		eventDefinitionCrfService.fillEventDefinitionCrf(eventDefinitionCRFBean, studyEventDefinitionBean);

		return eventDefinitionCRFBean;
	}

	protected EventDefinitionCRFBean prepareNewEventDefinitionCRF(StudyEventDefinitionBean studyEventDefinitionBean,
			UserAccountBean owner, StudyBean study, String crfName, String defaultVersion, Boolean required,
			Boolean passwordRequired, Boolean hide, Integer sourceDataVerification, String dataEntryQuality,
			String emailWhen, String email, String tabbing, Boolean acceptNewCrfVersions) throws Exception {
		EventDefinitionCRFBean eventDefinitionCRFBean = new EventDefinitionCRFBean();
		eventDefinitionCRFBean.setStudyEventDefinitionId(studyEventDefinitionBean.getId());
		eventDefinitionCRFBean.setEventName(studyEventDefinitionBean.getName());
		eventDefinitionCRFBean.setStudyId(study.getId());
		eventDefinitionCRFBean.setOwner(owner);
		return prepareEventDefinitionCRF(eventDefinitionCRFBean, crfName, defaultVersion, required, passwordRequired,
				hide, sourceDataVerification, dataEntryQuality, emailWhen, email, tabbing, acceptNewCrfVersions, null,
				null);
	}

	protected EventDefinitionCRFBean prepareStudyEventDefinitionCRF(int eventId, String crfName, String defaultVersion,
			Boolean required, Boolean passwordRequired, Boolean hide, Integer sourceDataVerification,
			String dataEntryQuality, String emailWhen, String email, String tabbing, Boolean acceptNewCrfVersions,
			Integer propagateChange) throws Exception {
		EventDefinitionCRFBean eventDefinitionCRFBean = getStudyEventDefinitionCRF(eventId, crfName);
		return prepareEventDefinitionCRF(eventDefinitionCRFBean, crfName, defaultVersion, required, passwordRequired,
				hide, sourceDataVerification, dataEntryQuality, emailWhen, email, tabbing, acceptNewCrfVersions,
				propagateChange, null);
	}

	private EventDefinitionCRFBean prepareEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRFBean,
			String crfName, String defaultVersion, Boolean required, Boolean passwordRequired, Boolean hide,
			Integer sourceDataVerification, String dataEntryQuality, String emailWhen, String email, String tabbing,
			Boolean acceptNewCrfVersions, Integer propagateChange, String availableVersions) throws Exception {
		CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
		StudyBean currentStudy = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);

		eventDefinitionCRFBean.setCrfName(crfName);
		eventDefinitionCRFBean.setDefaultVersionName(
				defaultVersion != null ? defaultVersion : eventDefinitionCRFBean.getDefaultVersionName());
		eventDefinitionCRFBean.setRequiredCRF(required != null ? required : eventDefinitionCRFBean.isRequiredCRF());
		eventDefinitionCRFBean.setElectronicSignature(
				passwordRequired != null ? passwordRequired : eventDefinitionCRFBean.isElectronicSignature());
		eventDefinitionCRFBean.setHideCrf(hide != null ? hide : eventDefinitionCRFBean.isHideCrf());
		eventDefinitionCRFBean.setSourceDataVerification(sourceDataVerification != null
				? SourceDataVerification.getByCode(sourceDataVerification)
				: eventDefinitionCRFBean.getSourceDataVerification());
		eventDefinitionCRFBean.setDoubleEntry(dataEntryQuality != null
				? !dataEntryQuality.equals("none") && dataEntryQuality.equalsIgnoreCase("dde")
				: eventDefinitionCRFBean.isDoubleEntry());
		eventDefinitionCRFBean.setEvaluatedCRF(dataEntryQuality != null
				? !dataEntryQuality.equals("none") && dataEntryQuality.equalsIgnoreCase("evaluation")
				: eventDefinitionCRFBean.isEvaluatedCRF());
		eventDefinitionCRFBean.setEmailStep(emailWhen != null
				? (emailWhen.equals("none") ? "" : emailWhen)
				: eventDefinitionCRFBean.getEmailStep());
		eventDefinitionCRFBean.setEmailTo(email != null
				? email
				: (eventDefinitionCRFBean.getEmailStep().isEmpty() ? "" : eventDefinitionCRFBean.getEmailTo()));
		eventDefinitionCRFBean.setTabbingMode(tabbing != null ? tabbing : eventDefinitionCRFBean.getTabbingMode());
		eventDefinitionCRFBean.setAcceptNewCrfVersions(
				acceptNewCrfVersions != null ? acceptNewCrfVersions : eventDefinitionCRFBean.isAcceptNewCrfVersions());
		eventDefinitionCRFBean.setPropagateChange(propagateChange != null ? propagateChange : PROPAGATE_CHANGE_NO);

		if (currentStudy.isSite()) {
			if (availableVersions == null) {
				String selectedVersionNames = "";
				for (String versionId : eventDefinitionCRFBean.getSelectedVersionIds().split(",")) {
					CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao
							.findByPK(Integer.parseInt(versionId.trim()));
					selectedVersionNames = selectedVersionNames.concat(selectedVersionNames.isEmpty() ? "" : ",")
							.concat(crfVersionBean.getName());
				}
				eventDefinitionCRFBean.setSelectedVersionNames(selectedVersionNames);
			} else {
				eventDefinitionCRFBean.setSelectedVersionNames(availableVersions);
			}
		}

		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao
				.findByFullName(eventDefinitionCRFBean.getDefaultVersionName(), eventDefinitionCRFBean.getCrfName());
		eventDefinitionCRFBean.setDefaultVersionId(crfVersionBean.getId());
		eventDefinitionCRFBean.setCrfId(crfVersionBean.getCrfId());

		return eventDefinitionCRFBean;
	}

	protected void updateEventDefinitionCRFForStudy(StudyEventDefinitionBean studyEventDefinitionBean,
			EventDefinitionCRFBean eventDefinitionCRFBean) throws Exception {
		StudyBean currentStudy = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);
		UserAccountBean updater = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);

		List<EventDefinitionCRFBean> childEventDefinitionCRFs = eventDefinitionService
				.getAllChildrenEventDefinitionCrfs(studyEventDefinitionBean);
		List<EventDefinitionCRFBean> eventDefinitionCRFs = eventDefinitionService
				.getAllParentsEventDefinitionCrfs(studyEventDefinitionBean);
		Map<Integer, SignStateRestorer> signStateRestorerMap = eventDefinitionService
				.prepareSignStateRestorer(studyEventDefinitionBean);
		List<EventDefinitionCRFBean> oldEventDefinitionCRFs = EventDefinitionCRFUtil.cloneList(eventDefinitionCRFs);

		for (EventDefinitionCRFBean edc : eventDefinitionCRFs) {
			if (edc.getId() == eventDefinitionCRFBean.getId()) {
				eventDefinitionCRFs.set(eventDefinitionCRFs.indexOf(edc), eventDefinitionCRFBean);
			}
		}

		HashMap<Integer, ArrayList<EDCItemMetadata>> edcItemMetadataMap = new HashMap<Integer, ArrayList<EDCItemMetadata>>();
		eventDefinitionService.updateAllEventDefinitionCRFs(currentStudy, updater, studyEventDefinitionBean,
				eventDefinitionCRFs, childEventDefinitionCRFs, oldEventDefinitionCRFs, signStateRestorerMap,
				edcItemMetadataMap);
	}

	protected void addEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRFBean, StudyBean studyBean,
			UserAccountBean owner) {
		eventDefinitionService.addEventDefinitionCRF(eventDefinitionCRFBean, studyBean, owner);
		if (eventDefinitionCRFBean.getId() == 0) {
			throw new RestException(messageSource, "rest.addCrf.operationFailed");
		}
	}
}
