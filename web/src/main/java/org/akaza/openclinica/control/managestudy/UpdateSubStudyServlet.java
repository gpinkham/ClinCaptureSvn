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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.akaza.openclinica.control.core.Controller;
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

import com.clinovo.util.ValidatorHelper;

@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@Component
public class UpdateSubStudyServlet extends Controller {

	public static final String INPUT_START_DATE = "startDate";
	public static final String INPUT_VER_DATE = "protocolDateVerification";
	public static final String INPUT_END_DATE = "endDate";
	public static final String YES = "yes";
	public static final String NEW_STUDY = "newStudy";
	public static final String DEFINITIONS = "definitions";
	public static final String PARENT_NAME = "parentName";
	public static final String SDV_OPTIONS = "sdvOptions";
	public static final String DDE = "dde";
	public static final String EVALUATION = "evaluation";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);
		checkStudyLocked(Page.SITE_LIST_SERVLET, respage.getString("current_study_locked"), request, response);
		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_DIRECTOR)
				|| currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.STUDY_LIST, resexception.getString("not_study_director"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyDAO sdao = getStudyDAO();
		StudyBean study = (StudyBean) request.getSession().getAttribute(NEW_STUDY);

		HashMap errors = getErrorsHolder(request);

		if (study == null) {
			forwardPage(Page.SITE_LIST_SERVLET, request, response);
			return;
		}

		SimpleDateFormat localDf = getLocalDf(request);
		StudyBean parentStudy = (StudyBean) sdao.findByPK(study.getParentStudyId());

		logger.info("study from session:" + study.getName() + "\n" + study.getCreatedDate() + "\n");
		String action = request.getParameter("action");

		if (StringUtil.isBlank(action)) {
			request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
			request.setAttribute("statuses", Status.toStudyUpdateMembersList());
			FormProcessor fp = new FormProcessor(request);
			logger.info("start date:" + study.getDatePlannedEnd());
			if (study.getDatePlannedEnd() != null) {
				fp.addPresetValue(INPUT_END_DATE, localDf.format(study.getDatePlannedEnd()));
			}
			if (study.getDatePlannedStart() != null) {
				fp.addPresetValue(INPUT_START_DATE, localDf.format(study.getDatePlannedStart()));
			}
			if (study.getProtocolDateVerification() != null) {
				fp.addPresetValue(INPUT_VER_DATE, localDf.format(study.getProtocolDateVerification()));
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

		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);
		v.addValidation("name", Validator.NO_BLANKS);
		v.addValidation("uniqueProId", Validator.NO_BLANKS);
		v.addValidation("prinInvestigator", Validator.NO_BLANKS);

		if (!StringUtil.isBlank(fp.getString(INPUT_START_DATE))) {
			v.addValidation(INPUT_START_DATE, Validator.IS_A_DATE);
		}
		if (!StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
			v.addValidation(INPUT_END_DATE, Validator.IS_A_DATE);
		}
		if (!StringUtil.isBlank(fp.getString(INPUT_VER_DATE))) {
			v.addValidation(INPUT_VER_DATE, Validator.IS_A_DATE);
		}
		if (!StringUtil.isBlank(fp.getString("facConEmail"))) {
			v.addValidation("facConEmail", Validator.IS_A_EMAIL);
		}
		v.addValidation("secondProId", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facCity", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facState", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 20);
		v.addValidation("facZip", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				64);
		v.addValidation("facCountry", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 64);
		v.addValidation("facConName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facConDegree", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facConPhone", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facConEmail", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

		errors.putAll(v.validate());

		StudyDAO studyDAO = getStudyDAO();
		ArrayList<StudyBean> allStudies = (ArrayList<StudyBean>) studyDAO.findAll();
		StudyBean oldStudy = (StudyBean) request.getSession().getAttribute(NEW_STUDY);
		for (StudyBean thisBean : allStudies) {
			if (fp.getString("uniqueProId").trim().equals(thisBean.getIdentifier())
					&& !thisBean.getIdentifier().equals(oldStudy.getIdentifier())) {
				Validator.addError(errors, "uniqueProId", resexception.getString("unique_protocol_id_existed"));
			}
		}

		if (fp.getString("name").trim().length() > 100) {
			Validator.addError(errors, "name", resexception.getString("maximum_lenght_name_100"));
		}
		if (fp.getString("uniqueProId").trim().length() > 30) {
			Validator.addError(errors, "uniqueProId", resexception.getString("maximum_lenght_unique_protocol_30"));
		}
		if (fp.getString("description").trim().length() > 2000) {
			Validator.addError(errors, "description", resexception.getString("maximum_lenght_brief_summary_2000"));
		}
		if (fp.getString("prinInvestigator").trim().length() > 255) {
			Validator.addError(errors, "prinInvestigator",
					resexception.getString("maximum_lenght_principal_investigator_255"));
		}
		if (fp.getInt("expectedTotalEnrollment") <= 0) {
			Validator.addError(errors, "expectedTotalEnrollment",
					respage.getString("expected_total_enrollment_must_be_a_positive_number"));
		}

		if (parentStudy.getStatus().equals(Status.LOCKED)) {
			if (fp.getInt("statusId") != Status.LOCKED.getId()) {
				Validator.addError(errors, "statusId", respage.getString("study_locked_site_status_locked"));
			}
		}

		SimpleDateFormat localDf = getLocalDf(request);
		StudyBean study = createStudyBean(request);
		request.getSession().setAttribute(NEW_STUDY, study);

		if (errors.isEmpty()) {
			logger.info("no errors");
			submitStudy(request);
			addPageMessage(respage.getString("the_site_has_been_updated_succesfully"), request);
			String fromListSite = (String) request.getSession().getAttribute("fromListSite");
			request.getSession().removeAttribute("fromListSite");
			if (fromListSite != null && fromListSite.equals(YES)) {
				forwardPage(Page.SITE_LIST_SERVLET, request, response);
			} else {
				forwardPage(Page.STUDY_LIST_SERVLET, request, response);
			}
		} else {
			logger.info("has validation errors");
			try {
				localDf.parse(fp.getString(INPUT_START_DATE));
				fp.addPresetValue(INPUT_START_DATE, localDf.format(fp.getDate(INPUT_START_DATE)));
			} catch (ParseException pe) {
				fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
			}

			try {
				localDf.parse(fp.getString(INPUT_VER_DATE));
				fp.addPresetValue(INPUT_VER_DATE, localDf.format(fp.getDate(INPUT_VER_DATE)));
			} catch (ParseException pe) {
				fp.addPresetValue(INPUT_VER_DATE, fp.getString(INPUT_VER_DATE));
			}

			try {
				localDf.parse(fp.getString(INPUT_END_DATE));
				fp.addPresetValue(INPUT_END_DATE, localDf.format(fp.getDate(INPUT_END_DATE)));
			} catch (ParseException pe) {
				fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
			}
			setPresetValues(fp.getPresetValues(), request);
			request.setAttribute("formMessages", errors);
			request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
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
		study.setName(fp.getString("name"));
		study.setIdentifier(fp.getString("uniqueProId"));
		study.setSecondaryIdentifier(fp.getString("secondProId"));
		study.setSummary(fp.getString("description"));
		study.setPrincipalInvestigator(fp.getString("prinInvestigator"));
		study.setExpectedTotalEnrollment(fp.getInt("expectedTotalEnrollment"));

		SimpleDateFormat localDf = getLocalDf(request);

		java.util.Date endDate;
		java.util.Date startDate;
		java.util.Date protocolDate;
		try {
			localDf.setLenient(false);
			startDate = localDf.parse(fp.getString("startDate"));

		} catch (ParseException fe) {
			startDate = study.getDatePlannedStart();
			logger.info(fe.getMessage());
		}
		study.setDatePlannedStart(startDate);

		try {
			localDf.setLenient(false);
			endDate = localDf.parse(fp.getString("endDate"));

		} catch (ParseException fe) {
			endDate = study.getDatePlannedEnd();
		}
		study.setDatePlannedEnd(endDate);

		try {
			localDf.setLenient(false);
			protocolDate = localDf.parse(fp.getString(INPUT_VER_DATE));

		} catch (ParseException fe) {
			protocolDate = study.getProtocolDateVerification();
		}
		study.setProtocolDateVerification(protocolDate);
		study.setFacilityCity(fp.getString("facCity"));
		study.setFacilityContactDegree(fp.getString("facConDrgree"));
		study.setFacilityName(fp.getString("facName"));
		study.setFacilityContactEmail(fp.getString("facConEmail"));
		study.setFacilityContactPhone(fp.getString("facConPhone"));
		study.setFacilityContactName(fp.getString("facConName"));
		study.setFacilityContactDegree(fp.getString("facConDegree"));
		study.setFacilityCountry(fp.getString("facCountry"));
		study.setFacilityRecruitmentStatus(fp.getString("facRecStatus"));
		study.setFacilityState(fp.getString("facState"));
		study.setFacilityZip(fp.getString("facZip"));
		study.setStatus(Status.get(fp.getInt("statusId")));

		study.getStudyParameterConfig().setInterviewerNameRequired(fp.getString("interviewerNameRequired"));
		study.getStudyParameterConfig().setInterviewerNameDefault(fp.getString("interviewerNameDefault"));
		study.getStudyParameterConfig().setInterviewDateRequired(fp.getString("interviewDateRequired"));
		study.getStudyParameterConfig().setInterviewDateDefault(fp.getString("interviewDateDefault"));
		study.getStudyParameterConfig().setSubjectPersonIdRequired(fp.getString("subjectPersonIdRequired"));

		study.getStudyParameterConfig().setMarkImportedCRFAsCompleted(fp.getString("markImportedCRFAsCompleted"));
		study.getStudyParameterConfig().setAutoScheduleEventDuringImport(fp.getString("autoScheduleEventDuringImport"));
		study.getStudyParameterConfig().setAutoCreateSubjectDuringImport(fp.getString("autoCreateSubjectDuringImport"));
		study.getStudyParameterConfig().setAllowSdvWithOpenQueries(fp.getString("allowSdvWithOpenQueries"));
		study.getStudyParameterConfig().setReplaceExisitingDataDuringImport(
				fp.getString("replaceExisitingDataDuringImport"));
		study.getStudyParameterConfig().setAllowCodingVerification(fp.getString("allowCodingVerification"));
		study.getStudyParameterConfig().setAutoCodeDictionaryName(fp.getString("autoCodeDictionaryName"));

		ArrayList parameters = study.getStudyParameters();

		for (Object parameter : parameters) {
			StudyParamsConfig scg = (StudyParamsConfig) parameter;
			String value = fp.getString(scg.getParameter().getHandle());
			logger.info("get value:" + value);
			scg.getValue().setStudyId(study.getId());
			scg.getValue().setParameter(scg.getParameter().getHandle());
			scg.getValue().setValue(value);
		}

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
					boolean changed = false;
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
							changed = true;
							CRFVersionDAO cvdao = getCRFVersionDAO();
							CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(defaultVersionId);
							edcBean.setDefaultVersionId(defaultVersionId);
							edcBean.setDefaultVersionName(defaultVersion.getName());
						}
						if (!tabbingMode.equals(edcBean.getTabbingMode())) {
							changed = true;
							edcBean.setTabbingMode(tabbingMode);
						}
						if (isRequired != edcBean.isRequiredCRF()) {
							changed = true;
							edcBean.setRequiredCRF(isRequired);
						}
						if (isDouble != edcBean.isDoubleEntry()) {
							changed = true;
							edcBean.setDoubleEntry(isDouble);
						}
						if (hasPassword != edcBean.isElectronicSignature()) {
							changed = true;
							edcBean.setElectronicSignature(hasPassword);
						}
						if (isHide != edcBean.isHideCrf()) {
							changed = true;
							edcBean.setHideCrf(isHide);
						}
						if (!StringUtil.isBlank(selectedVersionIds)
								&& !selectedVersionIds.equals(edcBean.getSelectedVersionIds())) {
							changed = true;
							setSelectedVersionList(edcBean, selectedVersionIds);
						}
						if (sdvId > 0 && sdvId != edcBean.getSourceDataVerification().getCode()) {
							changed = true;
							edcBean.setSourceDataVerification(SourceDataVerification.getByCode(sdvId));
						}
						if (!emailOnStep.equals(edcBean.getEmailStep())) {
							changed = true;
							edcBean.setEmailStep(emailOnStep);
						}
						if (!emailCRFTo.equals(edcBean.getEmailTo())) {
							changed = true;

							if (StringUtil.isBlank(emailOnStep)) {
								edcBean.setEmailTo("");
							} else {
								edcBean.setEmailTo(emailCRFTo);
							}
						}
						if (isEvaluatedCRF != edcBean.isEvaluatedCRF()) {
							changed = true;
							edcBean.setEvaluatedCRF(isEvaluatedCRF);
						}
						if (changed) {
							edcBean.setUpdater(ub);
							edcBean.setUpdatedDate(new Date());
							logger.debug("update for site");
							edcdao.update(edcBean);
						}
					} else {
						// only if definition-crf has been modified, will it be saved for the site
						int defaultId = defaultVersionId > 0 ? defaultVersionId : edcBean.getDefaultVersionId();
						int dbDefaultVersionId = edcBean.getDefaultVersionId();
						if (defaultId == dbDefaultVersionId && isRequired == edcBean.isRequiredCRF()
								&& isDouble == edcBean.isDoubleEntry() && tabbingMode.equals(edcBean.getTabbingMode())
								&& hasPassword == edcBean.isElectronicSignature() && isHide == edcBean.isHideCrf()) {
							if (selectedVersionIdListSize > 0) {
								if (selectedVersionIdListSize == edcBean.getVersions().size()
										&& emailOnStep.equals(edcBean.getEmailStep())
										&& emailCRFTo.equals(edcBean.getEmailTo())
										&& isEvaluatedCRF == edcBean.isEvaluatedCRF()) {
									if (sdvId > 0) {
										if (sdvId != edcBean.getSourceDataVerification().getCode()) {
											changed = true;
										}
									}
								} else {
									changed = true;
								}
							}
						} else {
							changed = true;
						}
						if (changed) {
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
		httpSession.removeAttribute(SDV_OPTIONS);
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return Controller.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}
}
