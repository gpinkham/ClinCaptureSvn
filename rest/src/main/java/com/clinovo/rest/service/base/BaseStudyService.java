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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.clinovo.bean.StudyMapsHolder;
import com.clinovo.enums.StudyAllocation;
import com.clinovo.enums.StudyAssignment;
import com.clinovo.enums.StudyControl;
import com.clinovo.enums.StudyDuration;
import com.clinovo.enums.StudyEndPoint;
import com.clinovo.enums.StudyFacility;
import com.clinovo.enums.StudyFeature;
import com.clinovo.enums.StudyMasking;
import com.clinovo.enums.StudyOrigin;
import com.clinovo.enums.StudyParameter;
import com.clinovo.enums.StudyPhase;
import com.clinovo.enums.StudyProtocolType;
import com.clinovo.enums.StudySelection;
import com.clinovo.enums.StudyTiming;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.util.ValidatorUtil;
import com.clinovo.service.StudyService;
import com.clinovo.util.DateUtil;
import com.clinovo.util.RequestUtil;
import com.clinovo.util.StudyUtil;
import com.clinovo.validator.StudyValidator;

/**
 * BaseStudyService.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BaseStudyService extends BaseService {

	@Autowired
	private StudyService studyService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ConfigurationDao configurationDao;

	@Autowired
	private StudyConfigService studyConfigService;

	private void validate(StudyBean studyBean) throws Exception {
		HashMap errors = StudyValidator.validate(getStudyDAO(), configurationDao, studyBean, null,
				DateUtil.DatePattern.ISO_DATE, true);
		ValidatorUtil.checkForErrors(errors);
	}

	protected StudyBean getStudy(int studyId) throws Exception {
		StudyBean studyBean = (StudyBean) getStudyDAO().findByPK(studyId);
		if (!(studyBean.getId() > 0)) {
			throw new RestException(messageSource, "rest.studyservice.studyDoesNotExist", new Object[]{studyId},
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (studyBean.isSite()) {
			throw new RestException(messageSource, "rest.studyservice.studyIsSite", new Object[]{studyId},
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		StudyUserRoleBean studyUserRoleBean = getCurrentUser().getRoleByStudy(studyBean.getId());
		if (!(studyUserRoleBean.getId() > 0
				&& (studyUserRoleBean.isSysAdmin() || studyUserRoleBean.isStudyAdministrator()))) {
			throw new RestException(messageSource, "rest.studyservice.youDoNotHaveRightsToAccessThisStudy");
		}
		studyService.prepareStudyBeanConfiguration(studyBean);
		return studyBean;
	}

	protected StudyBean saveStudyBean(String userName) throws Exception {
		StudyBean studyBean = new StudyBean();
		studyBean.setOrigin(StudyOrigin.STUDIO.getName());
		validate(studyBean);
		int userId = 0;
		if (userName != null) {
			UserAccountBean userAccountBean = (UserAccountBean) getUserAccountDAO().findByUserName(userName);
			if (userAccountBean.getId() == 0) {
				throw new RestException(messageSource, "rest.studyservice.createstudy.userDoesNotExist",
						new Object[]{userName}, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} else {
				if (userAccountBean.hasSiteLevelRoles()) {
					throw new RestException(messageSource, "rest.studyservice.createstudy.userHasSiteLevelRoles",
							new Object[]{userName}, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
				userId = userAccountBean.getId();
			}
		}

		StudyMapsHolder studyMapsHolder = new StudyMapsHolder(StudyUtil.getStudyFeaturesMap(),
				StudyUtil.getStudyParametersMap(), StudyUtil.getStudyFacilitiesMap(),
				StudyUtil.getStudyConfigurationParametersMap());

		studyService.prepareStudyBean(studyBean, getCurrentUser(), studyMapsHolder, DateUtil.DatePattern.ISO_DATE,
				LocaleResolver.getLocale());

		studyService.prepareStudyBeanConfiguration(studyBean, studyMapsHolder.getStudyConfigurationParametersMap());

		studyService.saveStudyBean(userId, studyBean, getCurrentUser(), ResourceBundleProvider.getPageMessagesBundle());

		if (studyBean.getId() == 0) {
			throw new RestException(messageSource, "rest.studyservice.createstudy.operationFailed");
		}

		return studyBean;
	}

	protected StudyBean updateStudyBean(int studyId) throws Exception {
		HttpServletRequest request = RequestUtil.getRequest();
		FormProcessor fp = new FormProcessor(request);

		StudyBean studyBean = getStudy(studyId);

		// start date is required field now but old studies may have null values
		if (studyBean.getDatePlannedStart() == null) {
			studyBean.setDatePlannedStart(studyBean.getCreatedDate() != null ? studyBean.getCreatedDate() : new Date());
		}

		prepareForValidation(StudyParameter.STUDY_NAME.getName(), studyBean.getName());
		prepareForValidation(StudyParameter.BRIEF_TITLE.getName(), studyBean.getBriefTitle());
		prepareForValidation(StudyParameter.PROTOCOL_ID.getName(), studyBean.getIdentifier());
		prepareForValidation(StudyParameter.PROTOCOL_TYPE.getName(),
				StudyProtocolType.get(studyBean.getProtocolTypeKey()).getId());
		prepareForValidation(StudyParameter.SECOND_PRO_ID.getName(), studyBean.getSecondaryIdentifier());
		prepareForValidation(StudyParameter.OFFICIAL_TITLE.getName(), studyBean.getOfficialTitle());
		prepareForValidation(StudyParameter.SUMMARY.getName(), studyBean.getSummary());
		prepareForValidation(StudyParameter.DESCRIPTION.getName(), studyBean.getProtocolDescription());
		prepareForValidation(StudyParameter.PRINCIPAL_INVESTIGATOR.getName(), studyBean.getPrincipalInvestigator());
		prepareForValidation(StudyParameter.SPONSOR.getName(), studyBean.getSponsor());
		prepareForValidation(StudyParameter.COLLABORATORS.getName(), studyBean.getCollaborators());
		prepareForValidation(StudyParameter.TOTAL_ENROLLMENT.getName(), studyBean.getExpectedTotalEnrollment());
		prepareForValidation(StudyParameter.PHASE.getName(), StudyPhase.get(studyBean.getPhaseKey()).getId());
		prepareForValidation(StudyParameter.START_DATE.getName(), studyBean.getDatePlannedStart());
		prepareForValidation(StudyParameter.END_DATE.getName(), studyBean.getDatePlannedEnd());
		prepareForValidation(StudyParameter.APPROVAL_DATE.getName(), studyBean.getProtocolDateVerification());
		prepareForValidation(StudyParameter.PURPOSE.getName(),
				StudyProtocolType.get(studyBean.getProtocolTypeKey()).getId());

		StudyProtocolType protocolType = StudyProtocolType.get(fp.getInt(StudyParameter.PROTOCOL_TYPE.getName()));
		if (protocolType == StudyProtocolType.INTERVENTIONAL) {
			prepareForValidation(StudyParameter.ALLOCATION.getName(),
					StudyAllocation.get(studyBean.getAllocationKey()).getId());
			prepareForValidation(StudyParameter.MASKING.getName(), StudyMasking.get(studyBean.getMaskingKey()).getId());
			prepareForValidation(StudyParameter.CONTROL.getName(), StudyControl.get(studyBean.getControlKey()).getId());
			prepareForValidation(StudyParameter.ASSIGNMENT.getName(),
					StudyAssignment.get(studyBean.getAssignmentKey()).getId());
			prepareForValidation(StudyParameter.END_POINT.getName(),
					StudyEndPoint.get(studyBean.getEndpointKey()).getId());
		} else if (protocolType == StudyProtocolType.OBSERVATIONAL) {
			prepareForValidation(StudyParameter.DURATION.getName(),
					StudyDuration.get(studyBean.getDurationKey()).getId());
			prepareForValidation(StudyParameter.SELECTION.getName(),
					StudySelection.get(studyBean.getSelectionKey()).getId());
			prepareForValidation(StudyParameter.TIMING.getName(), StudyTiming.get(studyBean.getTimingKey()).getId());
		}

		for (StudyFeature studyFeature : StudyFeature.values()) {
			prepareForValidation(studyFeature.getName(),
					studyConfigService.getParameter(studyFeature.getName(), studyBean.getStudyParameterConfig()));
		}

		for (StudyFacility studyFacility : StudyFacility.values()) {
			prepareForValidation(studyFacility.getName(),
					studyConfigService.getParameter(studyFacility.getName(), studyBean));
		}

		validate(studyBean);

		StudyMapsHolder studyMapsHolder = new StudyMapsHolder(StudyUtil.getStudyFeaturesMap(),
				StudyUtil.getStudyParametersMap(), StudyUtil.getStudyFacilitiesMap(),
				StudyUtil.getStudyConfigurationParametersMap());

		studyService.prepareStudyBean(studyBean, getCurrentUser(), studyMapsHolder, DateUtil.DatePattern.ISO_DATE,
				LocaleResolver.getLocale());

		studyService.prepareStudyBeanConfiguration(studyBean, studyMapsHolder.getStudyConfigurationParametersMap());

		studyService.updateStudy(studyBean, null, getCurrentUser());

		return studyBean;
	}

	protected StudyBean removeStudy(int studyId) throws Exception {
		StudyBean studyBean = getStudy(studyId);
		if (studyBean.getStatus().isLocked()) {
			throw new RestException(messageSource, "rest.studyservice.youCannotRemoveLockedStudy");
		} else if (!studyBean.getStatus().isDeleted()) {
			studyService.removeStudy(studyBean, getCurrentUser());
		}
		return studyBean;
	}

	protected StudyBean restoreStudy(int studyId) throws Exception {
		StudyBean studyBean = getStudy(studyId);
		if (!studyBean.getStatus().isDeleted()) {
			throw new RestException(messageSource, "rest.studyservice.studyIsNotInRemovedState");
		} else {
			studyService.restoreStudy(studyBean, getCurrentUser());
		}
		return studyBean;
	}

	protected List<StudyBean> getStudies() throws Exception {
		List<StudyBean> studyBeanList = (List<StudyBean>) getStudyDAO().findAllParents();
		for (StudyBean studyBean : studyBeanList) {
			studyService.prepareStudyBeanConfiguration(studyBean);
		}
		return studyBeanList;
	}
}
