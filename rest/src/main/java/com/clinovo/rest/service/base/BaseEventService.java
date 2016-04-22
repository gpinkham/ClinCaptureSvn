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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.rest.service.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.EDCItemMetadata;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.Response;
import com.clinovo.service.EventDefinitionCrfService;
import com.clinovo.service.EventDefinitionService;
import com.clinovo.util.RuleSetServiceUtil;
import com.clinovo.util.SignStateRestorer;
import com.clinovo.validator.EventDefinitionValidator;

/**
 * BaseEventService.
 */
public abstract class BaseEventService extends BaseService {

	@Autowired
	private EventDefinitionCrfService eventDefinitionCrfService;

	@Autowired
	private EventDefinitionService eventDefinitionService;

	@Autowired
	private MessageSource messageSource;

	private static void validateStudyEventDefinition(MessageSource messageSource, int eventId,
			StudyEventDefinitionBean studyEventDefinitionBean, StudyBean currentStudy, UserAccountDAO userAccountDao,
			boolean editMode) throws RestException {
		Locale locale = LocaleResolver.getLocale();
		if (!(studyEventDefinitionBean.getId() > 0)) {
			throw new RestException(messageSource.getMessage("rest.eventservice.studyEventDefinitionIsNotFound",
					new Object[]{eventId}, locale));
		} else if (studyEventDefinitionBean.getStudyId() != currentStudy.getId()) {
			throw new RestException(
					messageSource.getMessage("rest.eventservice.studyEventDefinitionDoesNotBelongToCurrentScope",
							new Object[]{eventId, currentStudy.getId()}, locale));
		}
		if (editMode) {
			prepareForValidation("name", studyEventDefinitionBean.getName());
			prepareForValidation("description", studyEventDefinitionBean.getDescription());
			prepareForValidation("repeating", studyEventDefinitionBean.isRepeating());
			prepareForValidation("category", studyEventDefinitionBean.getCategory());
			if (prepareForValidation("type", studyEventDefinitionBean.getType()).equalsIgnoreCase("calendared_visit")) {
				boolean isReference = prepareForValidation("isReference", studyEventDefinitionBean.getReferenceVisit())
						.equalsIgnoreCase("true");
				prepareForValidation("schDay", !isReference ? studyEventDefinitionBean.getScheduleDay() : 0);
				prepareForValidation("maxDay", !isReference ? studyEventDefinitionBean.getMaxDay() : 0);
				prepareForValidation("minDay", !isReference ? studyEventDefinitionBean.getMinDay() : 0);
				prepareForValidation("emailDay", !isReference ? studyEventDefinitionBean.getEmailDay() : 0);
				prepareForValidation("emailUser", !isReference
						? userAccountDao.findByPK(studyEventDefinitionBean.getUserEmailId()).getName()
						: "");
			}
		}
	}

	protected StudyEventDefinitionBean getStudyEventDefinition(int eventId, boolean editMode) throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
				.findByPK(eventId);
		validateStudyEventDefinition(messageSource, eventId, studyEventDefinitionBean, getCurrentStudy(),
				getUserAccountDAO(), editMode);
		return studyEventDefinitionBean;
	}

	protected StudyEventDefinitionBean createStudyEventDefinition(String name, String type, String description,
			Boolean repeating, String category, Boolean isReference, Integer schDay, Integer dayMax, Integer dayMin,
			Integer emailDay, String emailUser) throws Exception {
		return prepareStudyEventDefinition(new StudyEventDefinitionBean(), name, type, description, repeating, category,
				isReference, schDay, dayMax, dayMin, emailDay, emailUser);
	}

	protected StudyEventDefinitionBean editStudyEventDefinition(int eventId, String name, String type,
			String description, Boolean repeating, String category, Boolean isReference, Integer schDay, Integer dayMax,
			Integer dayMin, Integer emailDay, String emailUser) throws Exception {
		return prepareStudyEventDefinition(getStudyEventDefinition(eventId, true), name, type, description, repeating,
				category, isReference, schDay, dayMax, dayMin, emailDay, emailUser);
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
		int userId = emailUser != null ? getUserAccountDAO().findByUserName(emailUser).getId() : 0;
		studyEventDefinitionBean.setUserEmailId(userId != 0 ? userId : 1);
		return studyEventDefinitionBean;
	}

	protected Response deleteStudyEventDefinition(int eventId) throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = getStudyEventDefinition(eventId, false);
		if (!studyEventDefinitionBean.getStatus().isAvailable()) {
			throw new RestException(messageSource,
					"rest.eventservice.cannotPerformOperationOnSEDBecauseTheSEDIsNotAvailable");
		}
		eventDefinitionService.deleteStudyEventDefinition(studyEventDefinitionBean, getCurrentStudy(),
				LocaleResolver.getLocale());
		return new Response(String.valueOf(HttpServletResponse.SC_OK));
	}

	protected EventDefinitionCRFBean getEventDefinitionCRF(int eventId, String crfName, StudyBean studyBean,
			boolean checkAvailability) throws Exception {
		CRFBean crfBean = (CRFBean) getCRFDAO().findByName(crfName);
		StudyEventDefinitionBean studyEventDefinitionBean = getStudyEventDefinition(eventId, false);
		if (crfBean.getId() == 0) {
			throw new RestException(messageSource, "rest.eventservice.crfNameIsNotFound", new Object[]{crfName},
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (studyEventDefinitionBean.getId() == 0) {
			throw new RestException(messageSource, "rest.eventservice.studyEventDefinitionIsNotFound",
					new Object[]{eventId}, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (!studyEventDefinitionBean.getStatus().equals(Status.AVAILABLE)) {
			throw new RestException(messageSource,
					"rest.eventservice.cannotPerformOperationOnEDCBecauseTheSEDIsNotAvailable");
		}
		EventDefinitionCRFBean eventDefinitionCRFBean = getEventDefinitionCRFDAO()
				.findByStudyEventDefinitionIdAndCRFIdAndStudyId(eventId, crfBean.getId(), studyBean.getId());
		if (eventDefinitionCRFBean.getId() == 0) {
			throw new RestException(messageSource,
					studyBean.isSite()
							? "rest.eventservice.eventDefinitionCrfIsNotFoundInSite"
							: "rest.eventservice.eventDefinitionCrfIsNotFoundInStudy",
					new Object[]{crfName, studyEventDefinitionBean.getName(), studyBean.getName()},
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (checkAvailability && !eventDefinitionCRFBean.getStatus().isAvailable()) {
			throw new RestException(messageSource, "rest.eventservice.eventDefinitionCrfIsNotAvailable",
					new Object[]{crfName, studyEventDefinitionBean.getName()},
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		eventDefinitionCrfService.fillEventDefinitionCrf(eventDefinitionCRFBean, studyEventDefinitionBean);

		return eventDefinitionCRFBean;
	}

	protected EventDefinitionCRFBean createEventDefinitionCRF(StudyEventDefinitionBean studyEventDefinitionBean,
			UserAccountBean owner, String crfName, String defaultVersion, Boolean required, Boolean passwordRequired,
			Boolean hide, Integer sourceDataVerification, String dataEntryQuality, String emailWhen, String email,
			String tabbing, Boolean acceptNewCrfVersions) throws Exception {
		EventDefinitionCRFBean eventDefinitionCRFBean = new EventDefinitionCRFBean();
		eventDefinitionCRFBean.setStudyEventDefinitionId(studyEventDefinitionBean.getId());
		eventDefinitionCRFBean.setEventName(studyEventDefinitionBean.getName());
		eventDefinitionCRFBean.setStudyId(getCurrentStudy().getId());
		eventDefinitionCRFBean.setOwner(owner);
		return prepareEventDefinitionCRF(getCurrentStudy(), eventDefinitionCRFBean, crfName, defaultVersion, required,
				passwordRequired, hide, sourceDataVerification, dataEntryQuality, emailWhen, email, tabbing,
				acceptNewCrfVersions, null, null);
	}

	protected EventDefinitionCRFBean editParentEventDefinitionCRF(int eventId, String crfName, String defaultVersion,
			Boolean required, Boolean passwordRequired, Boolean hide, Integer sourceDataVerification,
			String dataEntryQuality, String emailWhen, String email, String tabbing, Boolean acceptNewCrfVersions,
			Integer propagateChange) throws Exception {
		EventDefinitionCRFBean eventDefinitionCRFBean = getEventDefinitionCRF(eventId, crfName, getCurrentStudy(),
				true);
		return prepareEventDefinitionCRF(getCurrentStudy(), eventDefinitionCRFBean, crfName, defaultVersion, required,
				passwordRequired, hide, sourceDataVerification, dataEntryQuality, emailWhen, email, tabbing,
				acceptNewCrfVersions, propagateChange, null);
	}

	protected EventDefinitionCRFBean editChildEventDefinitionCRF(int eventId, String crfName, String siteName,
			String defaultVersion, Boolean required, Boolean passwordRequired, Boolean hide,
			Integer sourceDataVerification, String dataEntryQuality, String emailWhen, String email, String tabbing,
			Integer[] availableVersions) throws Exception {
		StudyBean site = getSite(siteName);
		EventDefinitionCRFBean eventDefinitionCRFBean = getEventDefinitionCRF(eventId, crfName, site, true);
		return prepareEventDefinitionCRF(site, eventDefinitionCRFBean, crfName, defaultVersion, required,
				passwordRequired, hide, sourceDataVerification, dataEntryQuality, emailWhen, email, tabbing, null, null,
				availableVersions);
	}

	private EventDefinitionCRFBean prepareEventDefinitionCRF(StudyBean studyBean,
			EventDefinitionCRFBean eventDefinitionCRFBean, String crfName, String defaultVersion, Boolean required,
			Boolean passwordRequired, Boolean hide, Integer sourceDataVerification, String dataEntryQuality,
			String emailWhen, String email, String tabbing, Boolean acceptNewCrfVersions, Integer propagateChange,
			Integer[] availableVersions) throws Exception {
		eventDefinitionCRFBean.setCrfName(crfName);
		eventDefinitionCRFBean.setDefaultVersionName(
				defaultVersion != null ? defaultVersion : eventDefinitionCRFBean.getDefaultVersionName());
		CRFVersionBean crfVersionBean = (CRFVersionBean) getCRFVersionDAO()
				.findByFullName(eventDefinitionCRFBean.getDefaultVersionName(), eventDefinitionCRFBean.getCrfName());
		eventDefinitionCRFBean.setDefaultVersionId(crfVersionBean.getId());
		eventDefinitionCRFBean.setCrfId(crfVersionBean.getCrfId());
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

		if (studyBean.isSite()) {
			List<String> selectedVersionNameList = new ArrayList<String>();
			if (availableVersions == null) {
				prepareSelectedVersionIds(eventDefinitionCRFBean);
				fillSelectedVersionNameList(selectedVersionNameList, eventDefinitionCRFBean.getSelectedVersionIds());
				eventDefinitionCRFBean.setSelectedVersionNames(listAsCommaSeparatedString(selectedVersionNameList));
			} else {
				eventDefinitionCRFBean.setSelectedVersionIds(intArrayAsString(availableVersions));
				fillSelectedVersionNameList(selectedVersionNameList, eventDefinitionCRFBean.getSelectedVersionIds());
				eventDefinitionCRFBean.setSelectedVersionNames(listAsCommaSeparatedString(selectedVersionNameList));
			}
		}

		return eventDefinitionCRFBean;
	}

	protected EventDefinitionCRFBean updateParentEventDefinitionCRF(StudyEventDefinitionBean studyEventDefinitionBean,
			EventDefinitionCRFBean eventDefinitionCRFBean) throws Exception {
		List<EventDefinitionCRFBean> childEventDefinitionCRFs = eventDefinitionService
				.getAllChildrenEventDefinitionCrfs(studyEventDefinitionBean);
		List<EventDefinitionCRFBean> eventDefinitionCRFs = eventDefinitionService
				.getAllParentsEventDefinitionCrfs(studyEventDefinitionBean);
		Map<Integer, SignStateRestorer> signStateRestorerMap = eventDefinitionService
				.prepareSignStateRestorer(studyEventDefinitionBean);
		List<EventDefinitionCRFBean> oldEventDefinitionCRFs = cloneList(eventDefinitionCRFs);

		for (EventDefinitionCRFBean edc : eventDefinitionCRFs) {
			if (edc.getId() == eventDefinitionCRFBean.getId()) {
				eventDefinitionCRFs.set(eventDefinitionCRFs.indexOf(edc), eventDefinitionCRFBean);
			}
		}

		HashMap<Integer, ArrayList<EDCItemMetadata>> edcItemMetadataMap = new HashMap<Integer, ArrayList<EDCItemMetadata>>();
		eventDefinitionService.updateAllEventDefinitionCRFs(getCurrentStudy(), getCurrentUser(),
				studyEventDefinitionBean, eventDefinitionCRFs, childEventDefinitionCRFs, oldEventDefinitionCRFs,
				signStateRestorerMap, edcItemMetadataMap);
		return eventDefinitionCRFBean;
	}

	protected EventDefinitionCRFBean addEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRFBean) {
		eventDefinitionService.addEventDefinitionCRF(eventDefinitionCRFBean, getCurrentStudy(), getCurrentUser());
		if (eventDefinitionCRFBean.getId() == 0) {
			throw new RestException(messageSource, "rest.eventservice.addcrf.operationFailed");
		}
		return eventDefinitionCRFBean;
	}

	protected StudyEventDefinitionBean createStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean) {
		eventDefinitionService.createStudyEventDefinition(studyEventDefinitionBean, getCurrentUser(),
				getCurrentStudy());
		if (studyEventDefinitionBean.getId() == 0) {
			throw new RestException(messageSource, "rest.eventservice.createevent.operationFailed");
		}
		return studyEventDefinitionBean;
	}

	protected Response deleteEventDefinitionCRF(int eventId, String crfName) throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
				.findByPK(eventId);
		EventDefinitionCRFBean eventDefinitionCRFBean = getEventDefinitionCRF(eventId, crfName, getCurrentStudy(),
				true);
		eventDefinitionCrfService.deleteEventDefinitionCRF(RuleSetServiceUtil.getRuleSetService(),
				studyEventDefinitionBean, eventDefinitionCRFBean, LocaleResolver.getLocale());
		return new Response(String.valueOf(HttpServletResponse.SC_OK));
	}

	protected EventDefinitionCRFBean removeEDC(int eventId, String crfName) throws Exception {
		EventDefinitionCRFBean eventDefinitionCRFBean = getEventDefinitionCRF(eventId, crfName, getCurrentStudy(),
				false);
		if (eventDefinitionCRFBean.getStatus().isLocked()) {
			throw new RestException(messageSource, "rest.eventservice.youCannotRemoveLockedEDC");
		} else if (!eventDefinitionCRFBean.getStatus().isDeleted()) {
			eventDefinitionCrfService.removeParentEventDefinitionCrf(eventDefinitionCRFBean, getCurrentUser());
		}
		return eventDefinitionCRFBean;
	}

	protected EventDefinitionCRFBean restoreEDC(int eventId, String crfName) throws Exception {
		EventDefinitionCRFBean eventDefinitionCRFBean = getEventDefinitionCRF(eventId, crfName, getCurrentStudy(),
				false);
		if (!eventDefinitionCRFBean.getStatus().isDeleted()) {
			throw new RestException(messageSource, "rest.eventservice.edcIsNotInRemovedState");
		} else {
			eventDefinitionCrfService.restoreParentEventDefinitionCrf(eventDefinitionCRFBean, getCurrentUser());
		}
		return eventDefinitionCRFBean;
	}

	protected StudyEventDefinitionBean removeSED(int eventId) throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = getStudyEventDefinition(eventId, false);
		if (studyEventDefinitionBean.getStatus().isLocked()) {
			throw new RestException(messageSource, "rest.eventservice.youCannotRemoveLockedSED");
		} else if (!studyEventDefinitionBean.getStatus().isDeleted()) {
			eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, getCurrentUser());
		}
		return studyEventDefinitionBean;
	}

	protected StudyEventDefinitionBean restoreSED(int eventId) throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = getStudyEventDefinition(eventId, false);
		if (!studyEventDefinitionBean.getStatus().isDeleted()) {
			throw new RestException(messageSource, "rest.eventservice.sedIsNotInRemovedState");
		} else {
			eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, getCurrentUser());
		}
		return studyEventDefinitionBean;
	}

	private void fillSelectedVersionNameList(List<String> selectedVersionNameList, String versionIds) {
		CRFVersionDAO crfVersionDao = getCRFVersionDAO();
		try {
			for (String versionId : versionIds.split(",")) {
				CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao
						.findByPK(Integer.parseInt(versionId.trim()));
				if (!selectedVersionNameList.contains(crfVersionBean.getName())) {
					selectedVersionNameList.add(crfVersionBean.getName());
				}
			}
		} catch (Exception ex) {
			throw new RestException(messageSource,
					"eventDefinitionValidator.availableVersionsShouldContainCommaSeparatedCRFVersionIds");
		}
	}

	private void prepareSelectedVersionIds(EventDefinitionCRFBean eventDefinitionCRFBean) {
		String defaultVersion = Integer.toString(eventDefinitionCRFBean.getDefaultVersionId());
		eventDefinitionCRFBean.setSelectedVersionIds(eventDefinitionCRFBean.getSelectedVersionIds() == null
				? ""
				: eventDefinitionCRFBean.getSelectedVersionIds());
		List<String> selectedVersionIds = commaSeparatedStringAsList(eventDefinitionCRFBean.getSelectedVersionIds());
		if (!selectedVersionIds.contains(defaultVersion)) {
			selectedVersionIds.add(defaultVersion);
		}
		eventDefinitionCRFBean.setSelectedVersionIds(listAsCommaSeparatedString(selectedVersionIds));
	}

	private ArrayList<EventDefinitionCRFBean> cloneList(List<EventDefinitionCRFBean> originalList) {
		ArrayList<EventDefinitionCRFBean> clonedList = new ArrayList<EventDefinitionCRFBean>();
		for (EventDefinitionCRFBean edc : originalList) {
			EventDefinitionCRFBean clone = new EventDefinitionCRFBean(edc);
			clonedList.add(clone);
		}
		return clonedList;
	}
}
