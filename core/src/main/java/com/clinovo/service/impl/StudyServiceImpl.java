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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.enums.StudyConfigurationParameterType;
import com.clinovo.enums.StudyConfigurationParameters;
import com.clinovo.enums.StudyFeatures;
import com.clinovo.enums.StudyParameterNames;
import com.clinovo.enums.StudyProtocolType;
import com.clinovo.exception.CodeException;
import com.clinovo.model.DiscrepancyDescription;
import com.clinovo.model.DiscrepancyDescriptionType;
import com.clinovo.service.DatasetService;
import com.clinovo.service.DictionaryService;
import com.clinovo.service.DiscrepancyDescriptionService;
import com.clinovo.service.EventDefinitionCrfService;
import com.clinovo.service.EventDefinitionService;
import com.clinovo.service.StudyService;
import com.clinovo.service.StudySubjectService;
import com.clinovo.service.UserAccountService;
import com.clinovo.util.ParameterUtil;

/**
 * StudyServiceImpl.
 */
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class StudyServiceImpl implements StudyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StudyServiceImpl.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private DictionaryService dictionaryService;

	@Autowired
	private StudyConfigService studyConfigService;

	@Autowired
	private UserAccountService userAccountService;

	@Autowired
	private StudySubjectService studySubjectService;

	@Autowired
	private EventDefinitionService eventDefinitionService;

	@Autowired
	private EventDefinitionCrfService eventDefinitionCrfService;

	@Autowired
	private DiscrepancyDescriptionService discrepancyDescriptionService;

	private StudyDAO getStudyDAO() {
		return new StudyDAO(dataSource);
	}

	private UserAccountDAO getUserAccountDAO() {
		return new UserAccountDAO(dataSource);
	}

	private void autoRemoveStudyUserRole(StudyBean studyBean, UserAccountBean updater) throws Exception {
		List<StudyUserRoleBean> studyUserRoleBeanList = getUserAccountDAO().findAllByStudyIdOnly(studyBean.getId());
		for (StudyUserRoleBean studyUserRoleBean : studyUserRoleBeanList) {
			userAccountService.autoRemoveStudyUserRole(studyUserRoleBean, updater);
		}
	}

	private void autoRestoreStudyUserRole(StudyBean studyBean, UserAccountBean updater) throws Exception {
		List<StudyUserRoleBean> studyUserRoleBeanList = getUserAccountDAO().findAllByStudyIdOnly(studyBean.getId());
		for (StudyUserRoleBean studyUserRoleBean : studyUserRoleBeanList) {
			userAccountService.autoRestoreStudyUserRole(studyUserRoleBean, updater);
		}
	}

	private void disableStudyObjects(StudyBean studyBean, UserAccountBean updater, Status status) throws Exception {
		if (status.isDeleted()) {
			autoRemoveStudyUserRole(studyBean, updater);
			studySubjectService.removeStudySubjects(studyBean, updater);
			datasetService.removeDatasets(studyBean, updater);
			if (!studyBean.isSite()) {
				eventDefinitionService.removeStudyEventDefinitions(studyBean, updater);
			} else {
				eventDefinitionCrfService.removeChildEventDefinitionCRFs(studyBean, updater);
			}
		} else {
			studySubjectService.lockStudySubjects(studyBean, updater);
			datasetService.lockDatasets(studyBean, updater);
			eventDefinitionCrfService.lockChildEventDefinitionCRFs(studyBean, updater);
		}
	}

	private void enableStudyObjects(StudyBean studyBean, UserAccountBean updater, boolean restore) throws Exception {
		if (restore) {
			autoRestoreStudyUserRole(studyBean, updater);
			studySubjectService.restoreStudySubjects(studyBean, updater);
			datasetService.restoreDatasets(studyBean, updater);
			if (!studyBean.isSite()) {
				eventDefinitionService.restoreStudyEventDefinitions(studyBean, updater);
			} else {
				eventDefinitionCrfService.restoreChildEventDefinitionCRFs(studyBean, updater);
			}
		} else {
			studySubjectService.unlockStudySubjects(studyBean, updater);
			datasetService.unlockDatasets(studyBean, updater);
			eventDefinitionCrfService.unlockChildEventDefinitionCRFs(studyBean, updater);
		}
	}

	private void disableStudy(StudyBean studyBean, UserAccountBean updater, Status status) throws Exception {
		studyBean.setOldStatus(studyBean.getStatus());
		studyBean.setStatus(status);
		studyBean.setUpdater(updater);
		studyBean.setUpdatedDate(new Date());
		getStudyDAO().update(studyBean);
	}

	private void enableStudy(StudyBean studyBean, UserAccountBean updater) throws Exception {
		studyBean.setStatus(studyBean.getOldStatus());
		studyBean.setOldStatus(Status.AVAILABLE);
		studyBean.setUpdater(updater);
		studyBean.setUpdatedDate(new Date());
		getStudyDAO().update(studyBean);
	}

	private void disableStudyAndItsSites(StudyBean studyBean, UserAccountBean updater, Status status) throws Exception {
		disableStudy(studyBean, updater, status);
		disableStudyObjects(studyBean, updater, status);
		List<StudyBean> siteList = (List<StudyBean>) getStudyDAO().findAllByParent(studyBean.getId());
		for (StudyBean site : siteList) {
			disableStudy(site, updater, status);
		}
	}

	private void enableStudyAndItsSites(StudyBean studyBean, UserAccountBean updater, boolean restore)
			throws Exception {
		enableStudy(studyBean, updater);
		List<StudyBean> siteList = (List<StudyBean>) getStudyDAO().findAllByParent(studyBean.getId());
		for (StudyBean site : siteList) {
			enableStudy(site, updater);
		}
		enableStudyObjects(studyBean, updater, restore);
	}

	private void disableSite(StudyBean studyBean, UserAccountBean updater, Status status) throws Exception {
		disableStudy(studyBean, updater, status);
		disableStudyObjects(studyBean, updater, status);
	}

	private void enableSite(StudyBean studyBean, UserAccountBean updater, boolean restore) throws Exception {
		enableStudy(studyBean, updater);
		enableStudyObjects(studyBean, updater, restore);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStudy(StudyBean studyBean, UserAccountBean updater) throws Exception {
		disableStudyAndItsSites(studyBean, updater, Status.DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudy(StudyBean studyBean, UserAccountBean updater) throws Exception {
		enableStudyAndItsSites(studyBean, updater, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSite(StudyBean studyBean, UserAccountBean updater) throws Exception {
		disableSite(studyBean, updater, Status.DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreSite(StudyBean studyBean, UserAccountBean updater) throws Exception {
		enableSite(studyBean, updater, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void lockSite(StudyBean studyBean, UserAccountBean updater) throws Exception {
		disableSite(studyBean, updater, Status.LOCKED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void unlockSite(StudyBean studyBean, UserAccountBean updater) throws Exception {
		enableSite(studyBean, updater, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public StudyBean getSubjectStudy(StudyBean currentStudy, StudySubjectBean studySubject) {

		StudyBean subjectStudy;
		if (currentStudy.getId() == studySubject.getStudyId()) {
			subjectStudy = currentStudy;
		} else {
			subjectStudy = (StudyBean) getStudyDAO().findByPK(studySubject.getStudyId());
			studyConfigService.setParametersForStudy(subjectStudy);
		}
		return subjectStudy;
	}

	/**
	 * {@inheritDoc}
	 */
	public StudyBean saveStudyBean(int userId, StudyBean studyBean, UserAccountBean currentUser,
			ResourceBundle pageMessagesBundle) {
		StudyDAO studyDao = getStudyDAO();
		studyBean.setOwner(currentUser);
		studyBean.setCreatedDate(new Date());
		studyBean.setStatus(Status.PENDING);
		studyDao.create(studyBean);
		createDefaultDiscrepancyDescriptions(studyBean, pageMessagesBundle);
		submitStudyParameters(studyBean);
		createStudyUserRoleForStudy(userId, studyBean, currentUser);
		return studyBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public StudyBean prepareStudyBean(StudyBean studyBean, Map<String, String> parametersMap,
			Map<String, String> featuresMap) {
		studyBean.setName(parametersMap.get(StudyParameterNames.STUDY_NAME.getName()));
		studyBean.setSummary(parametersMap.get(StudyParameterNames.SUMMARY.getName()));
		studyBean.setSponsor(parametersMap.get(StudyParameterNames.SPONSOR.getName()));
		studyBean.setIdentifier(parametersMap.get(StudyParameterNames.PROTOCOL_ID.getName()));
		if (parametersMap.get(StudyParameterNames.PROTOCOL_TYPE.getName()) != null) {
			studyBean.setProtocolType(StudyProtocolType
					.getStudyProtocolType(
							ParameterUtil.getIntValue(parametersMap, StudyParameterNames.PROTOCOL_TYPE.getName(), 0))
					.getName());
		}
		studyBean.setCollaborators(parametersMap.get(StudyParameterNames.COLLABORATORS.getName()));
		studyBean.setProtocolDescription(parametersMap.get(StudyParameterNames.DESCRIPTION.getName()));
		studyBean.setPrincipalInvestigator(parametersMap.get(StudyParameterNames.PRINCIPAL_INVESTIGATOR.getName()));
		studyBean.setOfficialTitle(parametersMap.get(StudyParameterNames.OFFICIAL_TITLE.getName()));
		studyBean.setSecondaryIdentifier(parametersMap.get(StudyParameterNames.SECOND_PRO_ID.getName()));

		// Features
		setFeatures(studyBean, featuresMap);

		return studyBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public StudyBean prepareStudyBeanConfiguration(StudyBean studyBean,
			Map<String, String> configurationParametersMap) {
		setConfigurationParameters(studyBean, configurationParametersMap);
		try {
			// Create custom dictionary
			if (studyBean.getStudyParameterConfig().getAutoCodeDictionaryName() != null
					&& !studyBean.getStudyParameterConfig().getAutoCodeDictionaryName().isEmpty()) {
				dictionaryService.createDictionary(studyBean.getStudyParameterConfig().getAutoCodeDictionaryName(),
						studyBean);
			}
		} catch (CodeException e) {
			LOGGER.info("Custom dictionary with similar name exists");
		}
		return studyBean;
	}

	public StudyBean updateStudy(StudyBean studyBean, Map<String, List<DiscrepancyDescription>> dDescriptionsMap,
			UserAccountBean currentUser) throws CodeException {
		StudyDAO sdao = getStudyDAO();
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(dataSource);

		submitDescriptions(dDescriptionsMap, studyBean.getId());

		studyBean.setUpdatedDate(new Date());
		studyBean.setUpdater(currentUser);
		sdao.update(studyBean);

		ArrayList siteList = (ArrayList) sdao.findAllByParent(studyBean.getId());
		if (siteList.size() > 0) {
			sdao.updateSitesStatus(studyBean);
		}

		StudyParameterValueBean spv = new StudyParameterValueBean();

		spv.setStudyId(studyBean.getId());

		updateStudyParameters(studyBean, spv, spvdao);

		if (currentUser.isSysAdmin()) {
			updateFeatures(studyBean, spv, spvdao);
		}

		try {
			// Create custom dictionary
			if (studyBean.getStudyParameterConfig().getAutoCodeDictionaryName() != null
					&& !studyBean.getStudyParameterConfig().getAutoCodeDictionaryName().isEmpty()) {
				dictionaryService.createDictionary(studyBean.getStudyParameterConfig().getAutoCodeDictionaryName(),
						studyBean);
			}
		} catch (CodeException e) {
			LOGGER.info("Custom dictionary with similar name exists");
		}

		// update manage_pedigrees for all sites
		updateSiteParameters(studyBean, currentUser, sdao, spvdao);

		return studyBean;
	}

	private void updateSiteParameters(StudyBean studyBean, UserAccountBean ub, StudyDAO sdao,
			StudyParameterValueDAO spvdao) {
		ArrayList children = (ArrayList) sdao.findAllByParent(studyBean.getId());
		for (Object aChildren : children) {
			StudyBean child = (StudyBean) aChildren;
			child.setType(studyBean.getType()); // same as parent's type
			child.setUpdatedDate(new Date());
			child.setUpdater(ub);
			sdao.update(child);

			StudyParameterValueBean childspv = new StudyParameterValueBean();
			childspv.setStudyId(child.getId());

			updateStudyParameters(studyBean, childspv, spvdao);

			updateFeatures(studyBean, childspv, spvdao);
		}
	}

	private void submitDescriptions(Map<String, List<DiscrepancyDescription>> dDescriptionsMap, int studyId) {
		submitSpecifiedDescriptions(dDescriptionsMap.get("dnUpdateDescriptions"),
				DiscrepancyDescriptionType.DescriptionType.UPDATE_DESCRIPTION.getId(), studyId);
		submitSpecifiedDescriptions(dDescriptionsMap.get("dnCloseDescriptions"),
				DiscrepancyDescriptionType.DescriptionType.CLOSE_DESCRIPTION.getId(), studyId);
		submitSpecifiedDescriptions(dDescriptionsMap.get("dnRFCDescriptions"),
				DiscrepancyDescriptionType.DescriptionType.RFC_DESCRIPTION.getId(), studyId);
	}

	private void submitSpecifiedDescriptions(List<DiscrepancyDescription> newDescriptions, int typeId, int studyId) {
		// DiscrepancyDescriptions-section start
		Map<Integer, DiscrepancyDescription> idToDnDescriptionMap = new HashMap<Integer, DiscrepancyDescription>();
		for (DiscrepancyDescription dDescription : discrepancyDescriptionService.findAllByStudyIdAndTypeId(studyId,
				typeId)) {
			idToDnDescriptionMap.put(dDescription.getId(), dDescription);
		}
		for (DiscrepancyDescription dDescription : newDescriptions) {
			if (idToDnDescriptionMap.keySet().contains(dDescription.getId())) {
				DiscrepancyDescription dDescriptionOld = idToDnDescriptionMap.get(dDescription.getId());
				if (!dDescription.getName().equals(dDescriptionOld.getName())
						|| !dDescription.getVisibilityLevel().equals(dDescriptionOld.getVisibilityLevel())) {
					// description was changed
					discrepancyDescriptionService.saveDiscrepancyDescription(dDescription);
					idToDnDescriptionMap.remove(dDescription.getId());
				} else {
					// description wasn't changed
					idToDnDescriptionMap.remove(dDescription.getId());
				}
			} else {
				// description is new (id=0)
				discrepancyDescriptionService.saveDiscrepancyDescription(dDescription);
			}
		}
		// delete unneeded descriptions
		for (DiscrepancyDescription dDescriptionForDelete : idToDnDescriptionMap.values()) {
			discrepancyDescriptionService.deleteDiscrepancyDescription(dDescriptionForDelete);
		}
	}

	private void createDefaultDiscrepancyDescriptions(StudyBean studyBean, ResourceBundle pageMessagesBundle) {
		int studyId = studyBean.getId();

		int dnFailedValidationCheckTypeId = 1;
		int dnAnnotationTypeId = 2;
		int dnQueryTypeId = 3;

		// create default update discrepancy descriptions
		discrepancyDescriptionService.saveDiscrepancyDescription(
				new DiscrepancyDescription(pageMessagesBundle.getString("corrected_CRF_data"), "", studyId,
						"Study and Site", dnFailedValidationCheckTypeId));
		discrepancyDescriptionService.saveDiscrepancyDescription(
				new DiscrepancyDescription(pageMessagesBundle.getString("CRF_data_was_correctly_entered"), "", studyId,
						"Study and Site", dnFailedValidationCheckTypeId));
		discrepancyDescriptionService.saveDiscrepancyDescription(
				new DiscrepancyDescription(pageMessagesBundle.getString("need_additional_clarification"), "", studyId,
						"Study and Site", dnFailedValidationCheckTypeId));
		discrepancyDescriptionService.saveDiscrepancyDescription(
				new DiscrepancyDescription(pageMessagesBundle.getString("requested_information_is_provided"), "",
						studyId, "Study and Site", dnFailedValidationCheckTypeId));

		// create default close discrepancy descriptions
		discrepancyDescriptionService.saveDiscrepancyDescription(
				new DiscrepancyDescription(pageMessagesBundle.getString("query_response_monitored"), "", studyId,
						"Study and Site", dnAnnotationTypeId));
		discrepancyDescriptionService.saveDiscrepancyDescription(
				new DiscrepancyDescription(pageMessagesBundle.getString("CRF_data_change_monitored"), "", studyId,
						"Study and Site", dnAnnotationTypeId));
		discrepancyDescriptionService.saveDiscrepancyDescription(
				new DiscrepancyDescription(pageMessagesBundle.getString("calendared_event_monitored"), "", studyId,
						"Study and Site", dnAnnotationTypeId));
		discrepancyDescriptionService.saveDiscrepancyDescription(
				new DiscrepancyDescription(pageMessagesBundle.getString("failed_edit_check_monitored"), "", studyId,
						"Study and Site", dnAnnotationTypeId));

		// create default RFC discrepancy descriptions
		discrepancyDescriptionService.saveDiscrepancyDescription(
				new DiscrepancyDescription(pageMessagesBundle.getString("corrected_CRF_data_entry_error"), "", studyId,
						"Study and Site", dnQueryTypeId));
		discrepancyDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(
				pageMessagesBundle.getString("source_data_was_missing"), "", studyId, "Study and Site", dnQueryTypeId));
		discrepancyDescriptionService.saveDiscrepancyDescription(
				new DiscrepancyDescription(pageMessagesBundle.getString("source_data_was_incorrect"), "", studyId,
						"Study and Site", dnQueryTypeId));
		discrepancyDescriptionService.saveDiscrepancyDescription(
				new DiscrepancyDescription(pageMessagesBundle.getString("information_was_not_available"), "", studyId,
						"Study and Site", dnQueryTypeId));
	}

	private void submitStudyParameters(StudyBean studyBean) {
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(dataSource);

		studyBean.setCreatedDate(new Date());

		StudyParameterValueBean spv = new StudyParameterValueBean();
		spv.setStudyId(studyBean.getId());

		updateStudyParameters(studyBean, spv, spvdao);

		updateFeatures(studyBean, spv, spvdao);
	}

	private void createStudyUserRoleForStudy(int userId, StudyBean study, UserAccountBean currentUser) {
		UserAccountDAO udao = getUserAccountDAO();
		if (userId > 0) {
			UserAccountBean user = (UserAccountBean) udao.findByPK(userId);
			StudyUserRoleBean sub = new StudyUserRoleBean();
			sub.setRole(Role.STUDY_ADMINISTRATOR);
			sub.setStudyId(study.getId());
			sub.setStatus(Status.AVAILABLE);
			sub.setOwner(currentUser);
			udao.createStudyUserRole(user, sub);
			if (!currentUser.isSysAdmin() && currentUser.getId() != userId) {
				sub = new StudyUserRoleBean();
				sub.setRole(Role.STUDY_ADMINISTRATOR);
				sub.setStudyId(study.getId());
				sub.setStatus(Status.AVAILABLE);
				sub.setOwner(currentUser);
				udao.createStudyUserRole(currentUser, sub);
			}
		} else if (!currentUser.isSysAdmin()) {
			StudyUserRoleBean sub = new StudyUserRoleBean();
			sub.setRole(Role.STUDY_ADMINISTRATOR);
			sub.setStudyId(study.getId());
			sub.setStatus(Status.AVAILABLE);
			sub.setOwner(currentUser);
			udao.createStudyUserRole(currentUser, sub);
		}
	}

	private void updateStudyParameters(StudyBean studyBean, StudyParameterValueBean spv,
			StudyParameterValueDAO spvdao) {
		for (StudyConfigurationParameters studyConfigurationParameter : StudyConfigurationParameters.values()) {
			if (studyConfigurationParameter.getType() != StudyConfigurationParameterType.DYNAMIC_LABEL
					&& studyConfigurationParameter.getType() != StudyConfigurationParameterType.GROUP
					&& !studyConfigurationParameter.isSkip()) {
				studyConfigService.updateParameter(studyConfigurationParameter.getName(),
						studyBean.getStudyParameterConfig(), spv, spvdao);
			}
		}
	}

	private void updateFeatures(StudyBean studyBean, StudyParameterValueBean spv, StudyParameterValueDAO spvdao) {
		for (StudyFeatures studyFeature : StudyFeatures.values()) {
			studyConfigService.updateParameter(studyFeature.getName(), studyBean.getStudyParameterConfig(), spv,
					spvdao);
		}
	}

	private void setFeatures(StudyBean studyBean, Map<String, String> featuresMap) {
		for (StudyFeatures studyFeature : StudyFeatures.values()) {
			String parameterName = studyFeature.getName();
			String value = featuresMap.get(parameterName);
			studyConfigService.setParameter(parameterName, value, studyBean.getStudyParameterConfig());
		}
	}

	private void setConfigurationParameters(StudyBean studyBean, Map<String, String> configurationParametersMap) {
		for (StudyConfigurationParameters studyConfigurationParameter : StudyConfigurationParameters.values()) {
			if (studyConfigurationParameter.getType() != StudyConfigurationParameterType.GROUP
					&& studyConfigurationParameter.getType() != StudyConfigurationParameterType.DYNAMIC_LABEL
					&& !studyConfigurationParameter.isSkip()) {
				if (studyConfigurationParameter == StudyConfigurationParameters.DISCREPANCY_MANAGEMENT
						&& studyBean.getStatus().isLocked()) {
					studyBean.getStudyParameterConfig().setDiscrepancyManagement("false");
				} else {
					String parameterName = studyConfigurationParameter.getName();
					String value = configurationParametersMap.get(parameterName);
					studyConfigService.setParameter(parameterName, value, studyBean.getStudyParameterConfig());
				}
			}
		}
	}
}
