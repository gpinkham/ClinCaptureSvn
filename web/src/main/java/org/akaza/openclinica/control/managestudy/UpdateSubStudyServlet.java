/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.service.StudyParamsConfig;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.enums.study.StudyOrigin;
import com.clinovo.util.DateUtil;
import com.clinovo.util.ValidatorHelper;
import com.clinovo.validator.StudyValidator;

@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class UpdateSubStudyServlet extends SpringServlet {

	public static final String INPUT_START_DATE = "startDate";
	public static final String INPUT_APPROVAL_DATE = "approvalDate";
	public static final String INPUT_END_DATE = "endDate";
	public static final String YES = "yes";
	public static final String NEW_STUDY = "newStudy";
	public static final String DEFINITIONS = "definitions";
	public static final String PARENT_NAME = "parentName";
	public static final String DDE = "dde";
	public static final String EVALUATION = "evaluation";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);
		checkStudyLocked(Page.SITE_LIST_SERVLET, getResPage().getString("current_study_locked"), request, response);
		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_DIRECTOR)
				|| currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.STUDY_LIST, getResException().getString("not_study_director"),
				"1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyBean study = (StudyBean) request.getSession().getAttribute(NEW_STUDY);
		HashMap errors = getErrorsHolder(request);
		if (study == null) {
			forwardPage(Page.SITE_LIST_SERVLET, request, response);
			return;
		}

		StudyBean parentStudy = (StudyBean) getStudyDAO().findByPK(study.getParentStudyId());
		logger.info("study from session:" + study.getName() + "\n" + study.getCreatedDate() + "\n");
		String action = request.getParameter("action");

		if (StringUtil.isBlank(action)) {
			request.setAttribute("statuses", Status.toStudyUpdateMembersList());
			FormProcessor fp = new FormProcessor(request);
			if (study.getDatePlannedEnd() != null) {
				fp.addPresetValue(INPUT_END_DATE, DateUtil.printDate(study.getDatePlannedEnd(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}
			if (study.getDatePlannedStart() != null) {
				fp.addPresetValue(INPUT_START_DATE, DateUtil.printDate(study.getDatePlannedStart(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}
			if (study.getProtocolDateVerification() != null) {
				fp.addPresetValue(INPUT_APPROVAL_DATE, DateUtil.printDate(study.getProtocolDateVerification(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}
			setPresetValues(fp.getPresetValues(), request);
			forwardPage(Page.UPDATE_SUB_STUDY, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				confirmStudy(request, response, errors, parentStudy);
			}
		}
	}

	/**
	 * Validates the first section of study and save it into study bean
	 * 
	 * @param request
	 *            the incoming request
	 * @param response
	 *            the response to redirect to
	 * @param errors
	 *            the map with previous action errors
	 * @param parentStudy
	 *            the parent study bean for parent study status validation
	 * @throws Exception
	 *             an Exception
	 */
	private void confirmStudy(HttpServletRequest request, HttpServletResponse response, HashMap errors,
			StudyBean parentStudy) throws Exception {
		StudyBean oldStudy = (StudyBean) request.getSession().getAttribute(NEW_STUDY);
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);
		v.addValidation("siteName", Validator.NO_BLANKS);
		v.addValidation("protocolId", Validator.NO_BLANKS);
		v.addValidation("principalInvestigator", Validator.NO_BLANKS);

		if (!StringUtil.isBlank(fp.getString(INPUT_START_DATE))) {
			v.addValidation(INPUT_START_DATE, Validator.IS_A_DATE);
		}
		if (!StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
			v.addValidation(INPUT_END_DATE, Validator.IS_A_DATE);
		}
		if (!StringUtil.isBlank(fp.getString(INPUT_APPROVAL_DATE))) {
			v.addValidation(INPUT_APPROVAL_DATE, Validator.IS_A_DATE);
		}
		if (!StringUtil.isBlank(fp.getString("facilityContactEmail"))) {
			v.addValidation("facilityContactEmail", Validator.IS_A_EMAIL);
		}
		v.addValidation("secondProId", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facilityName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facilityCity", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facilityState", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 20);
		v.addValidation("facilityZip", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 64);
		v.addValidation("facilityCountry", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 64);
		v.addValidation("facilityContactName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facilityContactDegree", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facilityContactPhone", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facilityContactEmail", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("siteName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				oldStudy.getOrigin().equals(StudyOrigin.STUDIO.getName()) ? 20 : 100);

		errors.putAll(v.validate());

		StudyValidator.checkIfStudyFieldsAreUnique(fp, errors, getStudyDAO(), getResPage(), getResException(),
				oldStudy);

		if (fp.getString("protocolId").trim().length() > 30) {
			Validator.addError(errors, "protocolId", getResException().getString("maximum_lenght_unique_protocol_30"));
		}
		if (fp.getString("summary").trim().length() > 2000) {
			Validator.addError(errors, "summary", getResException().getString("maximum_lenght_brief_summary_2000"));
		}
		if (fp.getString("principalInvestigator").trim().length() > 255) {
			Validator.addError(errors, "principalInvestigator",
					getResException().getString("maximum_lenght_principal_investigator_255"));
		}
		if (fp.getInt("totalEnrollment") <= 0) {
			Validator.addError(errors, "totalEnrollment",
					getResPage().getString("expected_total_enrollment_must_be_a_positive_number"));
		}

		if (parentStudy.getStatus().equals(Status.LOCKED)) {
			if (fp.getInt("statusId") != Status.LOCKED.getId()) {
				Validator.addError(errors, "statusId", getResPage().getString("study_locked_site_status_locked"));
			}
		}

		StudyBean study = createStudyBean(request);
		request.getSession().setAttribute(NEW_STUDY, study);

		if (errors.isEmpty()) {
			logger.info("no errors");
			submitStudy(request);
			addPageMessage(getResPage().getString("the_site_has_been_updated_succesfully"), request);
			String fromListSite = (String) request.getSession().getAttribute("fromListSite");
			request.getSession().removeAttribute("fromListSite");
			if (fromListSite != null && fromListSite.equals(YES)) {
				forwardPage(Page.SITE_LIST_SERVLET, request, response);
			} else {
				forwardPage(Page.STUDY_LIST_SERVLET, request, response);
			}
		} else {
			logger.info("has validation errors");
			fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
			fp.addPresetValue(INPUT_APPROVAL_DATE, fp.getString(INPUT_APPROVAL_DATE));
			fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
			setPresetValues(fp.getPresetValues(), request);
			request.setAttribute("formMessages", errors);
			request.setAttribute("statuses", Status.toStudyUpdateMembersList());
			forwardPage(Page.UPDATE_SUB_STUDY, request, response);
		}
	}

	/**
	 * Constructs study bean from request
	 *
	 * @param request
	 *            the incoming request
	 * @return <code>StudyBean</code> bean that will be displayed on UX
	 */
	private StudyBean createStudyBean(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		StudyBean study = (StudyBean) request.getSession().getAttribute(NEW_STUDY);
		study.setName(fp.getString("siteName"));
		study.setIdentifier(fp.getString("protocolId"));
		study.setSecondaryIdentifier(fp.getString("secondProId"));
		study.setSummary(fp.getString("summary"));
		study.setPrincipalInvestigator(fp.getString("principalInvestigator"));
		study.setExpectedTotalEnrollment(fp.getInt("totalEnrollment"));
		try {
			study.setProtocolDateVerification(
					fp.getUpdatedDateProperty(INPUT_APPROVAL_DATE, study.getProtocolDateVerification()));
			study.setDatePlannedStart(fp.getUpdatedDateProperty(INPUT_START_DATE, study.getDatePlannedStart()));
			study.setDatePlannedEnd(fp.getUpdatedDateProperty(INPUT_END_DATE, study.getDatePlannedEnd()));
		} catch (IllegalArgumentException ex) {
			//
		}
		study.setFacilityCity(fp.getString("facilityCity"));
		study.setFacilityName(fp.getString("facilityName"));
		study.setFacilityContactEmail(fp.getString("facilityContactEmail"));
		study.setFacilityContactPhone(fp.getString("facilityContactPhone"));
		study.setFacilityContactName(fp.getString("facilityContactName"));
		study.setFacilityContactDegree(fp.getString("facilityContactDegree"));
		study.setFacilityCountry(fp.getString("facilityCountry"));
		study.setFacilityRecruitmentStatus(fp.getString("facRecStatus"));
		study.setFacilityState(fp.getString("facilityState"));
		study.setFacilityZip(fp.getString("facilityZip"));
		study.setStatus(Status.get(fp.getInt("statusId")));

		study.getStudyParameterConfig().setInterviewerNameRequired(fp.getString("interviewerNameRequired"));
		study.getStudyParameterConfig().setInterviewerNameDefault(fp.getString("interviewerNameDefault"));
		study.getStudyParameterConfig().setInterviewerNameEditable(fp.getString("interviewerNameEditable"));
		study.getStudyParameterConfig().setInterviewDateRequired(fp.getString("interviewDateRequired"));
		study.getStudyParameterConfig().setInterviewDateDefault(fp.getString("interviewDateDefault"));
		study.getStudyParameterConfig().setInterviewDateEditable(fp.getString("interviewDateEditable"));
		study.getStudyParameterConfig().setSubjectPersonIdRequired(fp.getString("subjectPersonIdRequired"));

		study.getStudyParameterConfig().setMarkImportedCRFAsCompleted(fp.getString("markImportedCRFAsCompleted"));
		study.getStudyParameterConfig().setAutoScheduleEventDuringImport(fp.getString("autoScheduleEventDuringImport"));
		study.getStudyParameterConfig().setAutoCreateSubjectDuringImport(fp.getString("autoCreateSubjectDuringImport"));
		study.getStudyParameterConfig().setAllowSdvWithOpenQueries(fp.getString("allowSdvWithOpenQueries"));
		study.getStudyParameterConfig()
				.setReplaceExisitingDataDuringImport(fp.getString("replaceExisitingDataDuringImport"));
		study.getStudyParameterConfig().setAutoCodeDictionaryName(fp.getString("autoCodeDictionaryName"));
		study.getStudyParameterConfig().setAutoTabbing(fp.getString("autoTabbing"));

		ArrayList parameters = study.getStudyParameters();
		HashMap<String, String> paramsMap = new HashMap<String, String>();

		for (Object parameter : parameters) {
			StudyParamsConfig scg = (StudyParamsConfig) parameter;
			String value = fp.getString(scg.getParameter().getHandle());
			logger.info("get value:" + value);
			scg.getValue().setStudyId(study.getId());
			scg.getValue().setParameter(scg.getParameter().getHandle());
			scg.getValue().setValue(value);
			paramsMap.put(scg.getParameter().getHandle(), scg.getValue().getValue());
		}

		request.setAttribute("paramsMap", paramsMap);

		return study;

	}

	private void submitSiteEventDefinitions(HttpServletRequest request, StudyBean site) {
		UserAccountBean ub = getUserAccountBean(request);
		HttpSession httpSession = request.getSession();
		FormProcessor fp = new FormProcessor(request);
		ArrayList<StudyEventDefinitionBean> seds = (ArrayList<StudyEventDefinitionBean>) httpSession
				.getAttribute(DEFINITIONS);
		for (StudyEventDefinitionBean sed : seds) {
			EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
			ArrayList<EventDefinitionCRFBean> edcs = sed.getCrfs();
			int start = 0;
			for (EventDefinitionCRFBean edcBean : edcs) {
				if (!(edcBean.getStatus().isDeleted())) {
					String order = start + "-" + edcBean.getId();
					int defaultVersionId = fp.getInt("defaultVersionId" + order);
					String requiredCRF = fp.getString("requiredCRF" + order);
					String deQuality = fp.getString("deQuality" + order);
					String emailCRFTo = fp.getString("mailTo" + order);
					String tabbingMode = fp.getString("tabbingMode" + order);
					String emailOnStep = fp.getString("emailOnStep" + order);
					String electronicSignature = fp.getString("electronicSignature" + order);
					String hideCRF = fp.getString("hideCRF" + order);
					int sdvId = fp.getInt("sdvOption" + order);
					ArrayList<String> selectedVersionIdList = fp.getStringArray("versionSelection" + order);
					int selectedVersionIdListSize = selectedVersionIdList.size();
					String selectedVersionIds = "";
					if (selectedVersionIdListSize > 0) {
						for (String id : selectedVersionIdList) {
							selectedVersionIds += id + ",";
						}
						selectedVersionIds = selectedVersionIds.substring(0, selectedVersionIds.length() - 1);
					}
					tabbingMode = tabbingMode.equalsIgnoreCase("topToBottom")
							|| tabbingMode.equalsIgnoreCase("leftToRight") ? tabbingMode : "leftToRight";
					boolean isRequired = !StringUtil.isBlank(requiredCRF) && YES.equalsIgnoreCase(requiredCRF.trim());
					boolean isDouble = !StringUtil.isBlank(deQuality) && DDE.equalsIgnoreCase(deQuality.trim());
					boolean hasPassword = !StringUtil.isBlank(electronicSignature)
							&& YES.equalsIgnoreCase(electronicSignature.trim());
					boolean isHide = !StringUtil.isBlank(hideCRF) && YES.equalsIgnoreCase(hideCRF.trim());
					boolean isEvaluatedCRF = !StringUtil.isBlank(deQuality)
							&& EVALUATION.equalsIgnoreCase(deQuality.trim());
					if (edcBean.getParentId() > 0) {
						int dbDefaultVersionId = edcBean.getDefaultVersionId();
						if (defaultVersionId != dbDefaultVersionId) {
							CRFVersionDAO cvdao = getCRFVersionDAO();
							CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(defaultVersionId);
							edcBean.setDefaultVersionId(defaultVersionId);
							edcBean.setDefaultVersionName(defaultVersion.getName());
						}
						if (!tabbingMode.equals(edcBean.getTabbingMode())) {
							edcBean.setTabbingMode(tabbingMode);
						}
						if (isRequired != edcBean.isRequiredCRF()) {
							edcBean.setRequiredCRF(isRequired);
						}
						if (isDouble != edcBean.isDoubleEntry()) {
							edcBean.setDoubleEntry(isDouble);
						}
						if (hasPassword != edcBean.isElectronicSignature()) {
							edcBean.setElectronicSignature(hasPassword);
						}
						if (isHide != edcBean.isHideCrf()) {
							edcBean.setHideCrf(isHide);
						}
						if (!StringUtil.isBlank(selectedVersionIds)
								&& !selectedVersionIds.equals(edcBean.getSelectedVersionIds())) {
							setSelectedVersionList(edcBean, selectedVersionIds);
						}
						if (sdvId > 0 && sdvId != edcBean.getSourceDataVerification().getCode()) {
							edcBean.setSourceDataVerification(SourceDataVerification.getByCode(sdvId));
						}
						if (!emailOnStep.equals(edcBean.getEmailStep())) {
							edcBean.setEmailStep(emailOnStep);
						}
						if (!emailCRFTo.equals(edcBean.getEmailTo())) {

							if (StringUtil.isBlank(emailOnStep)) {
								edcBean.setEmailTo("");
							} else {
								edcBean.setEmailTo(emailCRFTo);
							}
						}
						if (isEvaluatedCRF != edcBean.isEvaluatedCRF()) {
							edcBean.setEvaluatedCRF(isEvaluatedCRF);
						}
						edcBean.setUpdater(ub);
						edcBean.setUpdatedDate(new Date());
						logger.debug("update for site");
						edcdao.update(edcBean);

					} else {
						// if definition-crf not exists, it will be saved for the site
						int defaultId = defaultVersionId > 0 ? defaultVersionId : edcBean.getDefaultVersionId();
						CRFVersionDAO cvdao = getCRFVersionDAO();
						CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(defaultId);
						edcBean.setDefaultVersionId(defaultId);
						edcBean.setDefaultVersionName(defaultVersion.getName());
						edcBean.setRequiredCRF(isRequired);
						edcBean.setDoubleEntry(isDouble);
						edcBean.setElectronicSignature(hasPassword);
						edcBean.setHideCrf(isHide);
						edcBean.setEmailStep(emailOnStep);
						edcBean.setEvaluatedCRF(isEvaluatedCRF);
						edcBean.setTabbingMode(tabbingMode);
						if (StringUtil.isBlank(emailOnStep)) {
							edcBean.setEmailTo("");
						} else {
							edcBean.setEmailTo(emailCRFTo);
						}
						if (selectedVersionIdListSize > 0
								&& selectedVersionIdListSize != edcBean.getVersions().size()) {
							setSelectedVersionList(edcBean, selectedVersionIds);
						}
						if (sdvId > 0 && sdvId != edcBean.getSourceDataVerification().getCode()) {
							edcBean.setSourceDataVerification(SourceDataVerification.getByCode(sdvId));
						}
						edcBean.setParentId(edcBean.getId());
						edcBean.setStudyId(site.getId());
						edcBean.setUpdater(ub);
						edcBean.setUpdatedDate(new Date());
						logger.debug("create for the site");
						edcdao.create(edcBean);
					}
					++start;
				}
			}
		}
	}

	private void setSelectedVersionList(EventDefinitionCRFBean edcBean, String selectedVersionIds) {
		String[] ids = selectedVersionIds.split(",");
		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (String id : ids) {
			idList.add(Integer.valueOf(id));
		}
		edcBean.setSelectedVersionIdList(idList);
		edcBean.setSelectedVersionIds(selectedVersionIds);
	}

	/**
	 * Saves study bean from session
	 *
	 * @param request
	 *            the incoming request
	 */
	public void submitStudy(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		HttpSession httpSession = request.getSession();
		StudyBean study = (StudyBean) httpSession.getAttribute(NEW_STUDY);
		ArrayList parameters = study.getStudyParameters();
		study.setUpdatedDate(new Date());
		study.setUpdater(ub);

		StudyDAO sdao = getStudyDAO();
		StudyParameterValueDAO spvdao = getStudyParameterValueDAO();

		if (study.getStatus() == Status.INVALID) {
			StudyBean studyInDb = (StudyBean) sdao.findByPK(study.getId());
			study.setStatus(studyInDb.getStatus());
		}
		sdao.update(study);

		for (Object parameter : parameters) {
			StudyParamsConfig config = (StudyParamsConfig) parameter;
			StudyParameterValueBean spv = config.getValue();

			StudyParameterValueBean spv1 = spvdao.findByHandleAndStudy(spv.getStudyId(), spv.getParameter());
			if (spv1.getId() > 0) {
				spvdao.update(spv);
			} else {
				spvdao.create(spv);
			}
		}

		submitSiteEventDefinitions(request, study);

		httpSession.removeAttribute(NEW_STUDY);
		httpSession.removeAttribute(PARENT_NAME);
		httpSession.removeAttribute(DEFINITIONS);
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return SpringServlet.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}
}
