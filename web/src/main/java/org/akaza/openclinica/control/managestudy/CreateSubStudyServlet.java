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

package org.akaza.openclinica.control.managestudy;

import com.clinovo.util.ValidatorHelper;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.stereotype.Component;

/**
 * @author jxu
 * 
 *         Creates a sub study of user's current active study
 * 
 *         Modified by ywang: [10-10-2007], enable setting overidable study parameters of a sub study.
 */
@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
@Component
public class CreateSubStudyServlet extends Controller {
	public static final String INPUT_VER_DATE = "protocolDateVerification";
	public static final String INPUT_START_DATE = "startDate";
	public static final String INPUT_END_DATE = "endDate";

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.SITE_LIST_SERVLET, respage.getString("current_study_locked"), request, response);
		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}
		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study") + "\n"
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.SITE_LIST_SERVLET, resexception.getString("not_study_director"),
				"1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		HashMap errors = getErrorsHolder(request);

		FormProcessor fp = new FormProcessor(request);
		String action = request.getParameter("action");
		request.getSession().setAttribute("sdvOptions", this.setSDVOptions());

		SimpleDateFormat local_df = getLocalDf(request);

		if (StringUtil.isBlank(action)) {
			if (currentStudy.getParentStudyId() > 0) {
				addPageMessage(respage.getString("you_cannot_create_site_itself_site"), request);

				forwardPage(Page.SITE_LIST_SERVLET, request, response);
			} else {
				StudyBean newStudy = new StudyBean();
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
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("allowSdvWithOpenQueries")) {
								scg.getValue().setValue(fp.getString("allowSdvWithOpenQueries"));
							} else if (scg.getParameter().getHandle()
									.equalsIgnoreCase("replaceExisitingDataDuringImport")) {
								scg.getValue().setValue(fp.getString("replaceExisitingDataDuringImport"));
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("allowCodingVerification")) {
								scg.getValue().setValue(fp.getString("allowCodingVerification"));
							} else if (scg.getParameter().getHandle()
									.equalsIgnoreCase("defaultBioontologyURL")) {
								scg.getValue().setValue(fp.getString("defaultBioontologyURL"));
							} else if (scg.getParameter().getHandle().equalsIgnoreCase("autoCodeDictionaryName")) {
								scg.getValue().setValue(fp.getString("autoCodeDictionaryName"));
							}
							configs.add(scg);
						}
					}
				}
				newStudy.setStudyParameters(configs);

				try {
					local_df.parse(fp.getString(INPUT_START_DATE));
					fp.addPresetValue(INPUT_START_DATE, local_df.format(fp.getDate(INPUT_START_DATE)));
				} catch (ParseException pe) {
					fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
				}
				try {
					local_df.parse(fp.getString(INPUT_END_DATE));
					fp.addPresetValue(INPUT_END_DATE, local_df.format(fp.getDate(INPUT_END_DATE)));
				} catch (ParseException pe) {
					fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
				}
				try {
					local_df.parse(fp.getString(INPUT_VER_DATE));
					fp.addPresetValue(INPUT_VER_DATE, local_df.format(fp.getDate(INPUT_VER_DATE)));
				} catch (ParseException pe) {
					fp.addPresetValue(INPUT_VER_DATE, fp.getString(INPUT_VER_DATE));
				}
				setPresetValues(fp.getPresetValues(), request);

				request.getSession().setAttribute("newStudy", newStudy);
				request.getSession().setAttribute("definitions", this.initDefinitions(newStudy));
				request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
				request.setAttribute("statuses", Status.toActiveArrayList());

				forwardPage(Page.CREATE_SUB_STUDY, request, response);
			}

		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				confirmStudy(request, response, errors);

			} else if ("back".equalsIgnoreCase(action)) {
				StudyBean newStudy = (StudyBean) request.getSession().getAttribute("newStudy");
				try {
					fp.addPresetValue(INPUT_START_DATE, local_df.format(newStudy.getDatePlannedEnd()));
				} catch (Exception pe) {
					fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
				}
				try {
					fp.addPresetValue(INPUT_END_DATE, local_df.format(newStudy.getDatePlannedStart()));
				} catch (Exception pe) {
					fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
				}
				try {
					fp.addPresetValue(INPUT_VER_DATE, local_df.format(newStudy.getProtocolDateVerification()));
				} catch (Exception pe) {
					fp.addPresetValue(INPUT_VER_DATE, fp.getString(INPUT_VER_DATE));
				}
				setPresetValues(fp.getPresetValues(), request);
				request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
				request.setAttribute("statuses", Status.toActiveArrayList());

				forwardPage(Page.CREATE_SUB_STUDY, request, response);
			} else if ("submit".equalsIgnoreCase(action)) {
				submitStudy(request, response);
			}
		}
	}

	/**
	 * Validates the first section of study and save it into study bean
	 * 
	 * @throws Exception
	 */
	private void confirmStudy(HttpServletRequest request, HttpServletResponse response, HashMap errors)
			throws Exception {
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
		if (!StringUtil.isBlank(fp.getString("facConEmail"))) {
			v.addValidation("facConEmail", Validator.IS_A_EMAIL);
		}
		if (!StringUtil.isBlank(fp.getString(INPUT_VER_DATE))) {
			v.addValidation(INPUT_VER_DATE, Validator.IS_A_DATE);
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
		for (StudyBean thisBean : allStudies) {
			if (fp.getString("uniqueProId").trim().equals(thisBean.getIdentifier())) {
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

		StudyBean newSite = this.createStudyBean(request);
		StudyBean parentStudy = (StudyBean) getStudyDAO().findByPK(newSite.getParentStudyId());
		request.getSession().setAttribute("newStudy", newSite);
		request.getSession().setAttribute("definitions", this.createSiteEventDefinitions(request, parentStudy));

		SimpleDateFormat local_df = getLocalDf(request);

		if (errors.isEmpty()) {
			logger.info("no errors");
			forwardPage(Page.CONFIRM_CREATE_SUB_STUDY, request, response);

		} else {
			try {
				local_df.parse(fp.getString(INPUT_START_DATE));
				fp.addPresetValue(INPUT_START_DATE, local_df.format(fp.getDate(INPUT_START_DATE)));
			} catch (ParseException pe) {
				fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
			}
			try {
				local_df.parse(fp.getString(INPUT_END_DATE));
				fp.addPresetValue(INPUT_END_DATE, local_df.format(fp.getDate(INPUT_END_DATE)));
			} catch (ParseException pe) {
				fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
			}
			try {
				local_df.parse(fp.getString(INPUT_VER_DATE));
				fp.addPresetValue(INPUT_VER_DATE, local_df.format(fp.getDate(INPUT_VER_DATE)));
			} catch (ParseException pe) {
				fp.addPresetValue(INPUT_VER_DATE, fp.getString(INPUT_VER_DATE));
			}
			setPresetValues(fp.getPresetValues(), request);
			logger.info("has validation errors");
			request.setAttribute("formMessages", errors);
			request.setAttribute("statuses", Status.toActiveArrayList());
			forwardPage(Page.CREATE_SUB_STUDY, request, response);
		}

	}

	/**
	 * Constructs study bean from request
	 * 
	 * @return HttpServletRequest
	 */
	private StudyBean createStudyBean(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		StudyBean study = (StudyBean) request.getSession().getAttribute("newStudy");
		study.setName(fp.getString("name"));
		study.setIdentifier(fp.getString("uniqueProId"));
		study.setSecondaryIdentifier(fp.getString("secondProId"));
		study.setSummary(fp.getString("description"));
		study.setPrincipalInvestigator(fp.getString("prinInvestigator"));
		study.setExpectedTotalEnrollment(fp.getInt("expectedTotalEnrollment"));
		java.util.Date endDate;
		java.util.Date startDate;
		java.util.Date protocolDate;
		SimpleDateFormat local_df = getLocalDf(request);
		try {
			local_df.setLenient(false);
			startDate = local_df.parse(fp.getString("startDate"));

		} catch (ParseException fe) {
			startDate = study.getDatePlannedStart();
			logger.info(fe.getMessage());
		}
		study.setDatePlannedStart(startDate);

		try {
			local_df.setLenient(false);
			endDate = local_df.parse(fp.getString("endDate"));

		} catch (ParseException fe) {
			endDate = study.getDatePlannedEnd();
		}
		study.setDatePlannedEnd(endDate);

		try {
			local_df.setLenient(false);
			protocolDate = local_df.parse(fp.getString(INPUT_VER_DATE));

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
		study.setFacilityState(fp.getString("facState"));
		study.setFacilityZip(fp.getString("facZip"));
		study.setStatus(Status.get(fp.getInt("statusId")));

		ArrayList parameters = study.getStudyParameters();

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
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewDateRequired")
						&& !fp.getString("interviewDateRequired").isEmpty()) {
					scg.getValue().setValue(fp.getString("interviewDateRequired"));
					study.getStudyParameterConfig().setInterviewDateRequired(fp.getString("interviewDateRequired"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewDateDefault")
						&& !fp.getString("interviewDateDefault").isEmpty()) {
					scg.getValue().setValue(fp.getString("interviewDateDefault"));
					study.getStudyParameterConfig().setInterviewDateDefault(fp.getString("interviewDateDefault"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("markImportedCRFAsCompleted")
						&& !fp.getString("markImportedCRFAsCompleted").isEmpty()) {
					scg.getValue().setValue(fp.getString("markImportedCRFAsCompleted"));
					study.getStudyParameterConfig().setMarkImportedCRFAsCompleted(
							fp.getString("markImportedCRFAsCompleted"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("allowSdvWithOpenQueries")
						&& !fp.getString("allowSdvWithOpenQueries").isEmpty()) {
					scg.getValue().setValue(fp.getString("allowSdvWithOpenQueries"));
					study.getStudyParameterConfig().setAllowSdvWithOpenQueries(fp.getString("allowSdvWithOpenQueries"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("replaceExisitingDataDuringImport")
						&& !fp.getString("replaceExisitingDataDuringImport").isEmpty()) {
					scg.getValue().setValue(fp.getString("replaceExisitingDataDuringImport"));
					study.getStudyParameterConfig().setReplaceExisitingDataDuringImport(
							fp.getString("replaceExisitingDataDuringImport"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("allowCodingVerification")
						&& !fp.getString("allowCodingVerification").isEmpty()) {
					scg.getValue().setValue(fp.getString("allowCodingVerification"));
					study.getStudyParameterConfig().setAllowCodingVerification(fp.getString("allowCodingVerification"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("defaultBioontologyURL")
						&& !fp.getString("defaultBioontologyURL").isEmpty()) {
					scg.getValue().setValue(fp.getString("defaultBioontologyURL"));
					study.getStudyParameterConfig().setDefaultBioontologyURL(
							fp.getString("defaultBioontologyURL"));
				} else if (scg.getParameter().getHandle().equalsIgnoreCase("autoCodeDictionaryName")
						&& !fp.getString("autoCodeDictionaryName").isEmpty()) {
					scg.getValue().setValue(fp.getString("autoCodeDictionaryName"));
					study.getStudyParameterConfig().setAutoCodeDictionaryName(fp.getString("autoCodeDictionaryName"));
				}
			}
		}

		return study;
	}

	/**
	 * Inserts the new study into database
	 * 
	 */
	private void submitStudy(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

		for (int i = 0; i < currentStudy.getStudyParameters().size(); i++) {
			StudyParamsConfig scg = (StudyParamsConfig) currentStudy.getStudyParameters().get(i);
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
		request.getSession().setAttribute("new_site_created", "true");
		forwardPage(Page.SITE_LIST_SERVLET, request, response);
	}

	private ArrayList<StudyEventDefinitionBean> createSiteEventDefinitions(HttpServletRequest request, StudyBean site) {
		FormProcessor fp = new FormProcessor(request);
		ArrayList<StudyEventDefinitionBean> seds;
		StudyBean parentStudy = (StudyBean) getStudyDAO().findByPK(site.getParentStudyId());
		seds = (ArrayList<StudyEventDefinitionBean>) request.getSession().getAttribute("definitions");
		if (seds == null || seds.size() <= 0) {
			StudyEventDefinitionDAO sedDao = getStudyEventDefinitionDAO();
			seds = sedDao.findAllByStudy(parentStudy);
		}
		CRFVersionDAO cvdao = getCRFVersionDAO();
		HashMap<String, Boolean> changes = new HashMap<String, Boolean>();
		for (StudyEventDefinitionBean sed : seds) {
			ArrayList<EventDefinitionCRFBean> edcs = sed.getCrfs();
			int start = 0;
			for (EventDefinitionCRFBean edcBean : edcs) {
				int edcStatusId = edcBean.getStatus().getId();
				if (!(edcStatusId == 5 || edcStatusId == 7)) {
					String order = start + "-" + edcBean.getId();
					int defaultVersionId = fp.getInt("defaultVersionId" + order);
					String requiredCRF = fp.getString("requiredCRF" + order);
					String doubleEntry = fp.getString("doubleEntry" + order);
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
					boolean isRequired = !StringUtil.isBlank(requiredCRF) && "yes".equalsIgnoreCase(requiredCRF.trim());
					boolean isDouble = !StringUtil.isBlank(doubleEntry) && "yes".equalsIgnoreCase(doubleEntry.trim());
					boolean hasPassword = !StringUtil.isBlank(electronicSignature)
							&& "yes".equalsIgnoreCase(electronicSignature.trim());
					boolean isHide = !StringUtil.isBlank(hideCRF) && "yes".equalsIgnoreCase(hideCRF.trim());
					if (edcBean.getParentId() > 0) {
						int dbDefaultVersionId = edcBean.getDefaultVersionId();
						if (defaultVersionId != dbDefaultVersionId) {
							changed = true;
							CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(defaultVersionId);
							edcBean.setDefaultVersionId(defaultVersionId);
							edcBean.setDefaultVersionName(defaultVersion.getName());
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
							String[] ids = selectedVersionIds.split(",");
							ArrayList<Integer> idList = new ArrayList<Integer>();
							for (String id : ids) {
								idList.add(Integer.valueOf(id));
							}
							edcBean.setSelectedVersionIdList(idList);
							edcBean.setSelectedVersionIds(selectedVersionIds);
						}
						if (sdvId > 0 && sdvId != edcBean.getSourceDataVerification().getCode()) {
							changed = true;
							edcBean.setSourceDataVerification(SourceDataVerification.getByCode(sdvId));
						}
					} else {
						// only if definition-crf has been modified, will it be
						// saved for the site
						int defaultId = defaultVersionId > 0 ? defaultVersionId : edcBean.getDefaultVersionId();
						if (defaultId == defaultVersionId) {
							if (isRequired == edcBean.isRequiredCRF()) {
								if (isDouble == edcBean.isDoubleEntry()) {
									if (hasPassword == edcBean.isElectronicSignature()) {
										if (isHide == edcBean.isHideCrf()) {
											if (selectedVersionIdListSize > 0) {
												if (selectedVersionIdListSize == edcBean.getVersions().size()) {
													if (sdvId > 0) {
														if (sdvId != edcBean.getSourceDataVerification().getCode()) {
															changed = true;
															edcBean.setSourceDataVerification(SourceDataVerification
																	.getByCode(sdvId));
														}
													}
												} else {
													changed = true;
													String[] ids = selectedVersionIds.split(",");
													ArrayList<Integer> idList = new ArrayList<Integer>();
													for (String id : ids) {
														idList.add(Integer.valueOf(id));
													}
													edcBean.setSelectedVersionIdList(idList);
													edcBean.setSelectedVersionIds(selectedVersionIds);
												}
											}
										} else {
											changed = true;
											edcBean.setHideCrf(isHide);
										}
									} else {
										changed = true;
										edcBean.setElectronicSignature(hasPassword);
									}
								} else {
									changed = true;
									edcBean.setDoubleEntry(isDouble);
								}
							} else {
								changed = true;
								edcBean.setRequiredCRF(isRequired);
							}
						} else {
							changed = true;
							CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(defaultVersionId);
							edcBean.setDefaultVersionId(defaultVersionId);
							edcBean.setDefaultVersionName(defaultVersion.getName());
						}
					}
					changes.put(sed.getId() + "-" + edcBean.getId(), changed);
					++start;
				}
			}
		}
		request.getSession().setAttribute("changed", changes);
		return seds;
	}

	private void submitSiteEventDefinitions(HttpServletRequest request, StudyBean site) {
		UserAccountBean ub = getUserAccountBean(request);
		ArrayList<StudyEventDefinitionBean> seds;
		seds = (ArrayList<StudyEventDefinitionBean>) request.getSession().getAttribute("definitions");
		HashMap<String, Boolean> changes = (HashMap<String, Boolean>) request.getSession().getAttribute("changed");
		for (StudyEventDefinitionBean sed : seds) {
			EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
			ArrayList<EventDefinitionCRFBean> edcs = sed.getCrfs();
			for (EventDefinitionCRFBean edcBean : edcs) {
				int edcStatusId = edcBean.getStatus().getId();
				if (!(edcStatusId == 5 || edcStatusId == 7)) {
					boolean changed = changes.get(sed.getId() + "-" + edcBean.getId());
					if (changed) {
						edcBean.setParentId(edcBean.getId());
						edcBean.setStudyId(site.getId());
						edcBean.setUpdater(ub);
						edcBean.setUpdatedDate(new Date());
						logger.debug("create for the site");
						edcdao.create(edcBean);
					}
				}
			}
		}
		request.getSession().removeAttribute("definitions");
		request.getSession().removeAttribute("changed");
		request.getSession().removeAttribute("sdvOptions");
	}

	private ArrayList<StudyEventDefinitionBean> initDefinitions(StudyBean site) {
		ArrayList<StudyEventDefinitionBean> seds;
		StudyEventDefinitionDAO sedDao = getStudyEventDefinitionDAO();
		EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
		CRFVersionDAO cvdao = getCRFVersionDAO();
		CRFDAO cdao = getCRFDAO();
		StudyBean parentStudy = (StudyBean) getStudyDAO().findByPK(site.getParentStudyId());
		seds = sedDao.findAllByStudy(parentStudy);
		for (StudyEventDefinitionBean sed : seds) {
			int defId = sed.getId();
			ArrayList<EventDefinitionCRFBean> edcs = (ArrayList<EventDefinitionCRFBean>) edcdao
					.findAllByDefinitionAndSiteIdAndParentStudyId(defId, site.getId(), parentStudy.getId());
			ArrayList<EventDefinitionCRFBean> defCrfs = new ArrayList<EventDefinitionCRFBean>();
			for (EventDefinitionCRFBean edcBean : edcs) {
				int edcStatusId = edcBean.getStatus().getId();
				CRFBean crf = (CRFBean) cdao.findByPK(edcBean.getCrfId());
				int crfStatusId = crf.getStatusId();
				if (!(edcStatusId == 5 || edcStatusId == 7 || crfStatusId == 5 || crfStatusId == 7)) {
					ArrayList<CRFVersionBean> versions = (ArrayList<CRFVersionBean>) cvdao.findAllActiveByCRF(edcBean
							.getCrfId());
					edcBean.setVersions(versions);
					edcBean.setCrfName(crf.getName());
					CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edcBean.getDefaultVersionId());
					edcBean.setDefaultVersionName(defaultVersion.getName());
					String sversionIds = edcBean.getSelectedVersionIds();
					ArrayList<Integer> idList = new ArrayList<Integer>();
					if (sversionIds.length() > 0) {
						String[] ids = sversionIds.split("\\,");
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

	private ArrayList<String> setSDVOptions() {
		ArrayList<String> sdvOptions = new ArrayList<String>();
		sdvOptions.add(SourceDataVerification.AllREQUIRED.toString());
		sdvOptions.add(SourceDataVerification.PARTIALREQUIRED.toString());
		sdvOptions.add(SourceDataVerification.NOTREQUIRED.toString());
		sdvOptions.add(SourceDataVerification.NOTAPPLICABLE.toString());
		return sdvOptions;
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
