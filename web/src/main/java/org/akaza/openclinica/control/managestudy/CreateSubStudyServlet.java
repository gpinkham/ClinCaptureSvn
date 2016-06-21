/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.CRFBean;
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
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.stereotype.Component;

import com.clinovo.enums.study.StudyOrigin;
import com.clinovo.util.DateUtil;
import com.clinovo.util.ValidatorHelper;
import com.clinovo.validator.StudyValidator;

/**
 * The servlet for creating sub study of user's current active study.
 * 
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class CreateSubStudyServlet extends SpringServlet {

	public static final String YES = "yes";
	public static final String DDE = "dde";
	public static final String EVALUATION = "evaluation";
	public static final String INPUT_END_DATE = "endDate";
	public static final String INPUT_START_DATE = "startDate";
	public static final String INPUT_APPROVAL_DATE = "approvalDate";

	public static final int INT_20 = 20;
	public static final int INT_30 = 30;
	public static final int INT_64 = 64;
	public static final int INT_100 = 100;
	public static final int INT_255 = 255;
	public static final int INT_2000 = 2000;

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.SITE_LIST_SERVLET, getResPage().getString("current_study_locked"), request, response);
		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}
		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study") + "\n"
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.SITE_LIST_SERVLET,
				getResException().getString("not_study_director"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyBean currentStudy = getCurrentStudy(request);
		HashMap errors = getErrorsHolder(request);
		FormProcessor fp = new FormProcessor(request);
		String action = request.getParameter("action");

		if (StringUtil.isBlank(action)) {
			if (currentStudy.getParentStudyId() > 0) {
				addPageMessage(getResPage().getString("you_cannot_create_site_itself_site"), request);

				forwardPage(Page.SITE_LIST_SERVLET, request, response);
			} else {
				StudyBean newStudy = new StudyBean();
				newStudy.setOrigin(currentStudy.getOrigin());
				newStudy.setParentStudyId(currentStudy.getId());
				// get default facility info from property file
				newStudy.setFacilityName(SQLInitServlet.getField(CreateStudyServlet.FAC_NAME));
				newStudy.setFacilityCity(SQLInitServlet.getField(CreateStudyServlet.FAC_CITY));
				newStudy.setFacilityState(SQLInitServlet.getField(CreateStudyServlet.FAC_STATE));
				newStudy.setFacilityCountry(SQLInitServlet.getField(CreateStudyServlet.FAC_COUNTRY));
				newStudy.setFacilityContactDegree(SQLInitServlet.getField(CreateStudyServlet.FAC_CONTACT_DEGREE));
				newStudy.setFacilityContactEmail(SQLInitServlet.getField(CreateStudyServlet.FAC_CONTACT_EMAIL));
				newStudy.setFacilityContactName(SQLInitServlet.getField(CreateStudyServlet.FAC_CONTACT_NAME));
				newStudy.setFacilityContactPhone(SQLInitServlet.getField(CreateStudyServlet.FAC_CONTACT_PHONE));
				newStudy.setFacilityZip(SQLInitServlet.getField(CreateStudyServlet.FAC_ZIP));

				ArrayList configs = new ArrayList();
				List<StudyParamsConfig> parentConfigs = currentStudy.getStudyParameters();

				newStudy.setStudyParameterConfig(currentStudy.getStudyParameterConfig());

				HashMap<String, String> paramsMap = new HashMap<String, String>();

				for (StudyParamsConfig scg : parentConfigs) {

					if (scg != null) {
						// find the one that sub study can change
						if (scg.getValue().getId() > 0 && scg.getParameter().isOverridable()) {
							logger.info("parameter:" + scg.getParameter().getHandle());
							logger.info("value:" + scg.getValue().getValue());
							if (scg.getParameter().getHandle().equalsIgnoreCase("interviewerNameRequired")) {
								scg.getValue().setValue(fp.getString("interviewerNameRequired"));
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewerNameDefault")) {
								scg.getValue().setValue(fp.getString("interviewerNameDefault"));
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewDateRequired")) {
								scg.getValue().setValue(fp.getString("interviewDateRequired"));
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewDateDefault")) {
								scg.getValue().setValue(fp.getString("interviewDateDefault"));
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("markImportedCRFAsCompleted")) {
								scg.getValue().setValue(fp.getString("markImportedCRFAsCompleted"));
							} else if (scg.getParameter().getHandle()
									.equalsIgnoreCase("autoScheduleEventDuringImport")) {
								scg.getValue().setValue(fp.getString("autoScheduleEventDuringImport"));
							} else if (scg.getParameter().getHandle()
									.equalsIgnoreCase("autoCreateSubjectDuringImport")) {
								scg.getValue().setValue(fp.getString("autoCreateSubjectDuringImport"));
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("allowSdvWithOpenQueries")) {
								scg.getValue().setValue(fp.getString("allowSdvWithOpenQueries"));
							} else if (scg.getParameter().getHandle()
									.equalsIgnoreCase("replaceExisitingDataDuringImport")) {
								scg.getValue().setValue(fp.getString("replaceExisitingDataDuringImport"));
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("defaultBioontologyURL")) {
								scg.getValue().setValue(fp.getString("defaultBioontologyURL"));
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("autoCodeDictionaryName")) {
								scg.getValue().setValue(fp.getString("autoCodeDictionaryName"));
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("assignRandomizationResultTo")) {
								scg.getValue().setValue(fp.getString("assignRandomizationResultTo"));
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("randomizationTrialId")) {
								scg.getValue().setValue(fp.getString("randomizationTrialId"));
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("autoTabbing")) {
								scg.getValue().setValue(fp.getString("autoTabbing"));
							}
							configs.add(scg);
							paramsMap.put(scg.getParameter().getHandle(), scg.getValue().getValue());
						}
					}
				}
				newStudy.setStudyParameters(configs);
				addPresetValues(fp);
				setPresetValues(fp.getPresetValues(), request);

				request.getSession().setAttribute("paramsMap", paramsMap);
				request.getSession().setAttribute("newStudy", newStudy);
				request.getSession().setAttribute("definitions", this.initDefinitions(newStudy));
				request.setAttribute("statuses", Status.toActiveArrayList());
				forwardPage(Page.CREATE_SUB_STUDY, request, response);
			}

		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				confirmStudy(request, response, errors);
			} else if ("back".equalsIgnoreCase(action)) {

				StudyBean newStudy = (StudyBean) request.getSession().getAttribute("newStudy");
				try {
					fp.addPresetValue(INPUT_START_DATE, DateUtil.printDate(newStudy.getDatePlannedStart(),
							getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
				} catch (Exception pe) {
					fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
				}
				try {
					fp.addPresetValue(INPUT_END_DATE, DateUtil.printDate(newStudy.getDatePlannedEnd(),
							getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
				} catch (Exception pe) {
					fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
				}
				try {
					fp.addPresetValue(INPUT_APPROVAL_DATE, DateUtil.printDate(newStudy.getProtocolDateVerification(),
							getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
				} catch (Exception pe) {
					fp.addPresetValue(INPUT_APPROVAL_DATE, fp.getString(INPUT_APPROVAL_DATE));
				}
				setPresetValues(fp.getPresetValues(), request);
				request.setAttribute("statuses", Status.toActiveArrayList());
				forwardPage(Page.CREATE_SUB_STUDY, request, response);
			} else if ("submit".equalsIgnoreCase(action)) {
				submitStudy(request, response);
			}
		}
	}

	/**
	 * Validates the first section of study and save it into study bean.
	 * 
	 * @param request
	 *            the incoming request
	 * @param response
	 *            the response to redirect to
	 * @param errors
	 *            the map with previous action errors
	 * @throws Exception
	 *             an Exception
	 */
	private void confirmStudy(HttpServletRequest request, HttpServletResponse response, HashMap errors)
			throws Exception {
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		StudyBean newStudy = (StudyBean) request.getSession().getAttribute("newStudy");
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
		if (!StringUtil.isBlank(fp.getString("facilityContactEmail"))) {
			v.addValidation("facilityContactEmail", Validator.IS_A_EMAIL);
		}
		if (!StringUtil.isBlank(fp.getString(INPUT_APPROVAL_DATE))) {
			v.addValidation(INPUT_APPROVAL_DATE, Validator.IS_A_DATE);
		}

		v.addValidation("secondProId", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_255);
		v.addValidation("facilityName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_255);
		v.addValidation("facilityCity", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_255);
		v.addValidation("facilityState", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_20);
		v.addValidation("facilityZip", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_64);
		v.addValidation("facilityCountry", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_64);
		v.addValidation("facilityContactName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_255);
		v.addValidation("facilityContactDegree", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_255);
		v.addValidation("facilityContactPhone", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_255);
		v.addValidation("facilityContactEmail", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_255);
		v.addValidation("siteName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				newStudy.getOrigin().equals(StudyOrigin.STUDIO.getName()) ?  INT_20 : INT_100);

		errors.putAll(v.validate());

		StudyValidator.checkIfStudyFieldsAreUnique(fp, errors, getStudyDAO(), getResPage(), getResException(), newStudy);

		if (fp.getString("protocolId").trim().length() > INT_30) {
			Validator.addError(errors, "protocolId", getResException().getString("maximum_lenght_unique_protocol_30"));
		}
		if (fp.getString("description").trim().length() > INT_2000) {
			Validator.addError(errors, "description", getResException().getString("maximum_lenght_brief_summary_2000"));
		}
		if (fp.getString("principalInvestigator").trim().length() > INT_255) {
			Validator.addError(errors, "principalInvestigator",
					getResException().getString("maximum_lenght_principal_investigator_255"));
		}
		if (fp.getInt("totalEnrollment") <= 0) {
			Validator.addError(errors, "totalEnrollment",
					getResPage().getString("expected_total_enrollment_must_be_a_positive_number"));
		}

		StudyBean newSite = this.createStudyBean(request);
		StudyBean parentStudy = (StudyBean) getStudyDAO().findByPK(newSite.getParentStudyId());
		request.getSession().setAttribute("newStudy", newSite);
		request.getSession().setAttribute("definitions", this.createSiteEventDefinitions(request, parentStudy));

		if (errors.isEmpty()) {
			logger.info("no errors");
			forwardPage(Page.CONFIRM_CREATE_SUB_STUDY, request, response);

		} else {
			addPresetValues(fp);
			setPresetValues(fp.getPresetValues(), request);
			logger.info("has validation errors");

			request.setAttribute("formMessages", errors);
			request.setAttribute("statuses", Status.toActiveArrayList());
			forwardPage(Page.CREATE_SUB_STUDY, request, response);
		}
	}

	/**
	 * Constructs study bean from request.
	 *
	 * @param request
	 *            the incoming request
	 * @return <code>StudyBean</code> bean that will be displayed on UX
	 */
	private StudyBean createStudyBean(HttpServletRequest request) {

		FormProcessor fp = new FormProcessor(request);
		StudyBean study = (StudyBean) request.getSession().getAttribute("newStudy");

		study.setName(fp.getString("siteName"));
		study.setIdentifier(fp.getString("protocolId"));
		study.setSecondaryIdentifier(fp.getString("secondProId"));
		study.setSummary(fp.getString("description"));
		study.setPrincipalInvestigator(fp.getString("principalInvestigator"));
		study.setExpectedTotalEnrollment(fp.getInt("totalEnrollment"));
		try {
			if (!StringUtil.isBlank(fp.getString(INPUT_START_DATE))) {
				study.setDatePlannedStart(fp.getDateInputWithServerTimeOfDay(INPUT_START_DATE));
			}
			if (!StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
				study.setDatePlannedEnd(fp.getDateInputWithServerTimeOfDay(INPUT_END_DATE));
			}
			if (!StringUtil.isBlank(fp.getString(INPUT_APPROVAL_DATE))) {
				study.setProtocolDateVerification(fp.getDateInputWithServerTimeOfDay(INPUT_APPROVAL_DATE));
			}
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
		study.setFacilityState(fp.getString("facilityState"));
		study.setFacilityZip(fp.getString("facilityZip"));
		study.setStatus(Status.get(fp.getInt("statusId")));

		ArrayList parameters = study.getStudyParameters();
		HashMap<String, String> paramsMap = new HashMap<String, String>();

		for (Object parameter : parameters) {
			StudyParamsConfig scg = (StudyParamsConfig) parameter;
			if (scg.getValue().getId() > 0 && scg.getParameter().isOverridable()) {

				logger.info("parameter:" + scg.getParameter().getHandle());
				logger.info("value:" + scg.getValue().getValue());

				if (scg.getParameter().getHandle().equalsIgnoreCase("interviewerNameRequired")
						&& !fp.getString("interviewerNameRequired").isEmpty()) {
					scg.getValue().setValue(fp.getString("interviewerNameRequired"));
					study.getStudyParameterConfig().setInterviewerNameRequired(fp.getString("interviewerNameRequired"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewerNameDefault")
						&& !fp.getString("interviewerNameDefault").isEmpty()) {
					scg.getValue().setValue(fp.getString("interviewerNameDefault"));
					study.getStudyParameterConfig().setInterviewerNameDefault(fp.getString("interviewerNameDefault"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewerNameEditable")
						&& !fp.getString("interviewerNameEditable").isEmpty()) {
					scg.getValue().setValue(fp.getString("interviewerNameEditable"));
					study.getStudyParameterConfig().setInterviewerNameEditable(fp.getString("interviewerNameEditable"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewDateRequired")
						&& !fp.getString("interviewDateRequired").isEmpty()) {
					scg.getValue().setValue(fp.getString("interviewDateRequired"));
					study.getStudyParameterConfig().setInterviewDateRequired(fp.getString("interviewDateRequired"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewDateDefault")
						&& !fp.getString("interviewDateDefault").isEmpty()) {
					scg.getValue().setValue(fp.getString("interviewDateDefault"));
					study.getStudyParameterConfig().setInterviewDateDefault(fp.getString("interviewDateDefault"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewDateEditable")
						&& !fp.getString("interviewDateEditable").isEmpty()) {
					scg.getValue().setValue(fp.getString("interviewDateEditable"));
					study.getStudyParameterConfig().setInterviewDateEditable(fp.getString("interviewDateEditable"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("markImportedCRFAsCompleted")
						&& !fp.getString("markImportedCRFAsCompleted").isEmpty()) {
					scg.getValue().setValue(fp.getString("markImportedCRFAsCompleted"));
					study.getStudyParameterConfig()
							.setMarkImportedCRFAsCompleted(fp.getString("markImportedCRFAsCompleted"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("autoScheduleEventDuringImport")
						&& !fp.getString("autoScheduleEventDuringImport").isEmpty()) {
					scg.getValue().setValue(fp.getString("autoScheduleEventDuringImport"));
					study.getStudyParameterConfig()
							.setAutoScheduleEventDuringImport(fp.getString("autoScheduleEventDuringImport"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("autoCreateSubjectDuringImport")
						&& !fp.getString("autoCreateSubjectDuringImport").isEmpty()) {
					scg.getValue().setValue(fp.getString("autoCreateSubjectDuringImport"));
					study.getStudyParameterConfig()
							.setAutoCreateSubjectDuringImport(fp.getString("autoCreateSubjectDuringImport"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("allowSdvWithOpenQueries")
						&& !fp.getString("allowSdvWithOpenQueries").isEmpty()) {
					scg.getValue().setValue(fp.getString("allowSdvWithOpenQueries"));
					study.getStudyParameterConfig().setAllowSdvWithOpenQueries(fp.getString("allowSdvWithOpenQueries"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("replaceExisitingDataDuringImport")
						&& !fp.getString("replaceExisitingDataDuringImport").isEmpty()) {
					scg.getValue().setValue(fp.getString("replaceExisitingDataDuringImport"));
					study.getStudyParameterConfig()
							.setReplaceExisitingDataDuringImport(fp.getString("replaceExisitingDataDuringImport"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("defaultBioontologyURL")
						&& !fp.getString("defaultBioontologyURL").isEmpty()) {
					scg.getValue().setValue(fp.getString("defaultBioontologyURL"));
					study.getStudyParameterConfig().setDefaultBioontologyURL(fp.getString("defaultBioontologyURL"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("autoCodeDictionaryName")
						&& !fp.getString("autoCodeDictionaryName").isEmpty()) {
					scg.getValue().setValue(fp.getString("autoCodeDictionaryName"));
					study.getStudyParameterConfig().setAutoCodeDictionaryName(fp.getString("autoCodeDictionaryName"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("subjectPersonIdRequired")
						&& !fp.getString("subjectPersonIdRequired").isEmpty()) {
					scg.getValue().setValue(fp.getString("subjectPersonIdRequired"));
					study.getStudyParameterConfig().setSubjectPersonIdRequired(fp.getString("subjectPersonIdRequired"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("assignRandomizationResultTo")
						&& !fp.getString("assignRandomizationResultTo").isEmpty()) {
					scg.getValue().setValue(fp.getString("assignRandomizationResultTo"));
					study.getStudyParameterConfig()
							.setAutoCodeDictionaryName(fp.getString("assignRandomizationResultTo"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("randomizationTrialId")
						&& !fp.getString("randomizationTrialId").isEmpty()) {
					scg.getValue().setValue(fp.getString("randomizationTrialId"));
					study.getStudyParameterConfig().setSubjectPersonIdRequired(fp.getString("randomizationTrialId"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("autoTabbing")
						&& !fp.getString("autoTabbing").isEmpty()) {
					scg.getValue().setValue(fp.getString("autoTabbing"));
					study.getStudyParameterConfig().setAutoTabbing(fp.getString("autoTabbing"));
				}
				paramsMap.put(scg.getParameter().getHandle(), scg.getValue().getValue());
			}
		}

		request.setAttribute("paramsMap", paramsMap);

		return study;
	}

	/**
	 * Saves study bean from session.
	 * 
	 * @param request
	 *            the incoming request
	 * @param response
	 *            the response to redirect to
	 */
	private void submitStudy(HttpServletRequest request, HttpServletResponse response) {

		StudyDAO sdao = getStudyDAO();
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyBean study = (StudyBean) request.getSession().getAttribute("newStudy");

		logger.info("study bean to be created:\n");
		logger.info(study.getName() + "\n" + study.getIdentifier() + "\n" + study.getParentStudyId() + "\n"
				+ study.getSummary() + "\n" + study.getPrincipalInvestigator() + "\n" + study.getDatePlannedStart()
				+ "\n" + study.getDatePlannedEnd() + "\n" + study.getFacilityName() + "\n" + study.getFacilityCity()
				+ "\n" + study.getFacilityState() + "\n" + study.getFacilityZip() + "\n" + study.getFacilityCountry()
				+ "\n" + study.getFacilityRecruitmentStatus() + "\n" + study.getFacilityContactName() + "\n"
				+ study.getFacilityContactEmail() + "\n" + study.getFacilityContactPhone() + "\n"
				+ study.getFacilityContactDegree());

		study.setOwner(ub);
		study.setCreatedDate(new Date());
		StudyBean parent = (StudyBean) sdao.findByPK(study.getParentStudyId());
		study.setType(parent.getType());
		study.setStatus(study.getStatus());

		study.setGenetic(parent.isGenetic());
		study = (StudyBean) sdao.create(study);

		for (Object studyParameter : currentStudy.getStudyParameters()) {
			StudyParamsConfig scg = (StudyParamsConfig) studyParameter;
			if (scg.getValue().getId() > 0 && !scg.getParameter().isOverridable()) {
				study.getStudyParameters().add(scg);
			}
		}

		ArrayList parameters = study.getStudyParameters();

		StudyParameterValueDAO spvdao = getStudyParameterValueDAO();
		for (Object parameter : parameters) {
			StudyParamsConfig config = (StudyParamsConfig) parameter;
			StudyParameterValueBean spv = config.getValue();
			spv.setStudyId(study.getId());
			spv.setValue(config.getValue().getValue());
			spv.setParameter(config.getParameter().getHandle());
			spvdao.create(spv);
		}

		this.submitSiteEventDefinitions(request, study);
		request.getSession().removeAttribute("newStudy");
		request.getSession().removeAttribute("paramsMap");
		request.getSession().setAttribute("new_site_created", "true");
		forwardPage(Page.SITE_LIST_SERVLET, request, response);
	}

	/**
	 * Create site event definitions with unique parameters for current site.
	 * 
	 * @param request
	 *            the incoming request
	 * @param site
	 *            the site for creating specific event definitions
	 * @return the list with study event definitions
	 */
	private ArrayList<StudyEventDefinitionBean> createSiteEventDefinitions(HttpServletRequest request, StudyBean site) {

		FormProcessor fp = new FormProcessor(request);
		StudyBean parentStudy = (StudyBean) getStudyDAO().findByPK(site.getParentStudyId());
		ArrayList<StudyEventDefinitionBean> seds = (ArrayList<StudyEventDefinitionBean>) request.getSession()
				.getAttribute("definitions");

		if (seds == null || seds.size() <= 0) {
			StudyEventDefinitionDAO sedDao = getStudyEventDefinitionDAO();
			seds = sedDao.findAllAvailableByStudy(parentStudy);
		}
		CRFVersionDAO cvdao = getCRFVersionDAO();
		for (StudyEventDefinitionBean sed : seds) {
			ArrayList<EventDefinitionCRFBean> edcs = sed.getCrfs();
			int start = 0;
			for (EventDefinitionCRFBean edcBean : edcs) {
				int edcStatusId = edcBean.getStatus().getId();
				if (!(edcStatusId == Status.DELETED.getId() || edcStatusId == Status.AUTO_DELETED.getId())) {
					String order = start + "-" + edcBean.getId();
					int defaultVersionId = fp.getInt("defaultVersionId" + order);
					String requiredCRF = fp.getString("requiredCRF" + order);
					String deQuality = fp.getString("deQuality" + order);
					String electronicSignature = fp.getString("electronicSignature" + order);
					String hideCRF = fp.getString("hideCRF" + order);
					String emailCRFTo = fp.getString("mailTo" + order);
					String emailOnStep = fp.getString("emailOnStep" + order);
					ArrayList<String> selectedVersionIdList = fp.getStringArray("versionSelection" + order);
					int selectedVersionIdListSize = selectedVersionIdList.size();
					String selectedVersionIds = "";
					if (selectedVersionIdListSize > 0) {
						for (String id : selectedVersionIdList) {
							selectedVersionIds += id + ",";
						}
						selectedVersionIds = selectedVersionIds.substring(0, selectedVersionIds.length() - 1);
					}

					boolean isRequired = !StringUtil.isBlank(requiredCRF) && YES.equalsIgnoreCase(requiredCRF.trim());
					boolean isDouble = !StringUtil.isBlank(deQuality) && DDE.equalsIgnoreCase(deQuality.trim());
					boolean hasPassword = !StringUtil.isBlank(electronicSignature)
							&& YES.equalsIgnoreCase(electronicSignature.trim());
					boolean isHide = !StringUtil.isBlank(hideCRF) && YES.equalsIgnoreCase(hideCRF.trim());
					boolean isEvaluatedCRF = !StringUtil.isBlank(deQuality)
							&& EVALUATION.equalsIgnoreCase(deQuality.trim());

					int dbDefaultVersionId = edcBean.getDefaultVersionId();
					if (defaultVersionId != dbDefaultVersionId) {
						CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(defaultVersionId);
						edcBean.setDefaultVersionId(defaultVersionId);
						edcBean.setDefaultVersionName(defaultVersion.getName());
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
					if ((!StringUtil.isBlank(selectedVersionIds)
							&& !selectedVersionIds.equals(edcBean.getSelectedVersionIds())
							&& (edcBean.getParentId() > 0))
							|| (selectedVersionIdListSize != edcBean.getVersions().size()
									&& !(edcBean.getParentId() > 0))) {
						String[] ids = selectedVersionIds.split(",");
						ArrayList<Integer> idList = new ArrayList<Integer>();
						for (String id : ids) {
							idList.add(Integer.valueOf(id));
						}
						edcBean.setSelectedVersionIdList(idList);
						edcBean.setSelectedVersionIds(selectedVersionIds);
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

					++start;
				}
			}
		}
		return seds;
	}

	/**
	 * Saves created study event definitions from session.
	 * 
	 * @param request
	 *            the incoming request
	 * @param site
	 *            the site for creating specific event definitions
	 */
	private void submitSiteEventDefinitions(HttpServletRequest request, StudyBean site) {
		UserAccountBean ub = getUserAccountBean(request);
		ArrayList<StudyEventDefinitionBean> seds = (ArrayList<StudyEventDefinitionBean>) request.getSession()
				.getAttribute("definitions");
		for (StudyEventDefinitionBean sed : seds) {
			EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
			ArrayList<EventDefinitionCRFBean> edcs = sed.getCrfs();
			for (EventDefinitionCRFBean edcBean : edcs) {
				edcBean.setParentId(edcBean.getId());
				edcBean.setStudyId(site.getId());
				edcBean.setUpdater(ub);
				edcBean.setUpdatedDate(new Date());
				logger.debug("create for the site");
				edcdao.create(edcBean);
			}
		}
		request.getSession().removeAttribute("definitions");
	}

	/**
	 * Prepare eCRF definitions and active eCRFs versions for current study.
	 * 
	 * @param site
	 *            the incoming request
	 * @return the list with <code>StudyEventDefinitionBean</code> that will be placed on UX
	 */
	private ArrayList<StudyEventDefinitionBean> initDefinitions(StudyBean site) {
		StudyEventDefinitionDAO sedDao = getStudyEventDefinitionDAO();
		EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
		CRFVersionDAO cvdao = getCRFVersionDAO();
		CRFDAO cdao = getCRFDAO();

		StudyBean parentStudy = (StudyBean) getStudyDAO().findByPK(site.getParentStudyId());
		ArrayList<StudyEventDefinitionBean> seds = sedDao.findAllAvailableByStudy(parentStudy);

		for (StudyEventDefinitionBean sed : seds) {
			int defId = sed.getId();
			ArrayList<EventDefinitionCRFBean> edcs = (ArrayList<EventDefinitionCRFBean>) edcdao
					.findAllByDefinitionAndSiteIdAndParentStudyId(defId, site.getId(), parentStudy.getId());
			ArrayList<EventDefinitionCRFBean> defCrfs = new ArrayList<EventDefinitionCRFBean>();
			for (EventDefinitionCRFBean edcBean : edcs) {
				CRFBean crf = (CRFBean) cdao.findByPK(edcBean.getCrfId());
				int crfStatusId = crf.getStatusId();
				if (!(crfStatusId == Status.DELETED.getId() || crfStatusId == Status.AUTO_DELETED.getId())) {
					ArrayList<CRFVersionBean> versions = (ArrayList<CRFVersionBean>) cvdao
							.findAllActiveByCRF(edcBean.getCrfId());
					edcBean.setVersions(versions);
					edcBean.setCrfName(crf.getName());
					CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edcBean.getDefaultVersionId());
					edcBean.setDefaultVersionName(defaultVersion.getName());
					String sVersionIds = edcBean.getSelectedVersionIds();
					ArrayList<Integer> idList = new ArrayList<Integer>();
					if (sVersionIds.length() > 0) {
						String[] ids = sVersionIds.split("\\,");
						for (String id : ids) {
							idList.add(Integer.valueOf(id));
						}
					}
					edcBean.setSelectedVersionIdList(idList);
					defCrfs.add(edcBean);
				}
			}
			logger.debug("definitionCrfs size=" + defCrfs.size() + " total size=" + edcs.size());

			sed.setCrfs(defCrfs);
			sed.setCrfNum(defCrfs.size());
		}
		return seds;
	}

	private void addPresetValues(FormProcessor fp) {
		fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
		fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
		fp.addPresetValue(INPUT_APPROVAL_DATE, fp.getString(INPUT_APPROVAL_DATE));
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
