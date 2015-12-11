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
package com.clinovo.servlet;

import com.clinovo.context.SubmissionContext;
import com.clinovo.context.impl.JSONSubmissionContext;
import com.clinovo.exception.RandomizationException;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.AuditLogRandomization;
import com.clinovo.model.Randomization;
import com.clinovo.model.RandomizationResult;
import com.clinovo.rule.ext.HttpTransportProtocol;
import com.clinovo.service.AuditLogRandomizationService;
import com.clinovo.util.RandomizationUtil;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * End-point for all randomization calls.
 * 
 */
@SuppressWarnings("serial")
@Component
public class RandomizeServlet extends Controller {

	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	@Autowired
	private AuditLogRandomizationService auditLogRandomizationService;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			return;
		}
		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");

	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("Processing randomize request");

		// Set expected context
		RandomizationUtil.setSessionManager(getSessionManager(request));
		StudySubjectBean subject = RandomizationUtil.getStudySubjectBean(request);

		PrintWriter writer = response.getWriter();
		Exception exception = null;
		RandomizationResult result = null;
		try {
			String eligibility = request.getParameter("eligibility");

			if (isSubjectConfiguredValid(subject, request)) {
				if (!eligibility.equals("null")) {
					// YES
					if ("0".equals(eligibility)) {
						result = randomize(request, writer);
					} else if ("1".equals(eligibility)) {
						throw new RandomizationException(
								resexception.getString("subject_has_not_completed_ie_criteria"));
					}
				} else {
					result = randomize(request, writer);
				}

			} else {
				throw new RandomizationException(resexception.getString("subject_label_and_id_not_equals"));
			}
		} catch (Exception ex) {
			exception = ex;
			log.error("Randomization Error: {0}", ex.getMessage());
			writer.write("Exception: " + ex.getMessage());
			writer.flush();
		} finally {
			saveRandomizationAuditLog(request, result, exception);
		}
	}

	private void saveRandomizationAuditLog(HttpServletRequest request, RandomizationResult result, Exception exception) {
		String eventCrfIdString = request.getParameter("eventCrfId");
		String studySubjectIdString = request.getParameter("subjectId");
		StudyBean studyBean = getCurrentStudy(request);
		UserAccountBean userAccountBean = getUserAccountBean(request);
		int eventCRFId;
		int studySubjectId;

		try {
			eventCRFId = Integer.parseInt(eventCrfIdString);
			studySubjectId = Integer.parseInt(studySubjectIdString);
		} catch (Exception e) {
			logger.error("Unable to write randomization audit log: " + e.getMessage());
			return;
		}
		AuditLogRandomization auditLogRandomization = new AuditLogRandomization();
		auditLogRandomization.setStudyId(studyBean.getId());
		auditLogRandomization.setStudySubjectId(studySubjectId);
		auditLogRandomization.setEventCrfId(eventCRFId);
		auditLogRandomization.setUserId(userAccountBean.getId());
		auditLogRandomization.setSiteName(studyBean.getIdentifier());
		auditLogRandomization.setAuditDate(new Date());
		auditLogRandomization.setUserName(userAccountBean.getName());

		String crfConfiguredTrialId = request.getParameter("trialId");
		String configuredTrialId = studyBean.getStudyParameterConfig().getRandomizationTrialId();
		if (crfConfiguredTrialId != null && !configuredTrialId.equals("")) {
			auditLogRandomization.setTrialId(configuredTrialId);
		} else if (configuredTrialId != null && !configuredTrialId.equals("")) {
			auditLogRandomization.setTrialId(configuredTrialId);
		}

		String randomizationUrl = CoreResources.getField("randomizationUrl");
		String authenticationUrl = CoreResources.getField("randomizationAuthenticationUrl");
		auditLogRandomization.setAuthenticationUrl(authenticationUrl);
		auditLogRandomization.setRandomizationUrl(randomizationUrl);

		String strataItems = request.getParameter("strataLevel");
		if (!strataItems.equals("null")) {
			strataItems = strataItems.replace("[", "").replace("]", "");
			if (strataItems.contains("},")) {
				strataItems = strataItems.replace("},", "<br/>");
			}
			strataItems = strataItems.replace("{", "").replace("}", "");
			auditLogRandomization.setStrataVariables(strataItems);
		}
		if (exception != null) {
			String message = exception.getMessage();
			message = message.replace("javax.xml.ws.WebServiceException:", "");
			if (message.contains("\"Code\":400")) {
				message = message.replace("{\"Code\":400,\"Error\":\"", "").replace("\"}", "");
			}
			auditLogRandomization.setResponse(message);
			auditLogRandomization.setSuccess(0);
		} else if (result != null) {
			auditLogRandomization.setResponse(result.getRandomizationResult());
			auditLogRandomization.setSuccess(1);
		}
		try {
			auditLogRandomizationService.saveOrUpdate(auditLogRandomization);
		} catch (Exception e) {
			logger.error("Unable to write randomization audit log: " + e.getMessage());
		}
	}

	private boolean isSubjectConfiguredValid(StudySubjectBean subject, HttpServletRequest request) {

		boolean result = true;
		StudyBean sb = (StudyBean) request.getSession().getAttribute("study");
		String assignRandomizationResultTo = sb.getStudyParameterConfig().getAssignRandomizationResultTo();
		if (assignRandomizationResultTo.equals("ssid")) {
			String subjectUniqueIdentifier = subject.getUniqueIdentifier();
			String subjectLabel = subject.getLabel();
			result = subjectLabel.equals(subjectUniqueIdentifier);
		}
		return result;
	}

	private RandomizationResult randomize(HttpServletRequest request, PrintWriter writer) throws Exception {

		// Set expected context
		StudyBean currentStudy = getCurrentStudy(request);
		RandomizationUtil.setCurrentStudy(currentStudy);

		RandomizationResult result = initiateRandomizationCall(request);
		result.setStudyId(String.valueOf(getStudyId(currentStudy, getStudyDAO())));
		// Assign subject to group
		String assignRandomizationResultTo = currentStudy.getStudyParameterConfig().getAssignRandomizationResultTo();

		HashMap<String, ItemDataBean> itemsMap = RandomizationUtil.getRandomizationItemData(request);
		// Save randomization result and update all statuses
		RandomizationUtil.saveRandomizationResultToDatabase(result, itemsMap);
		RandomizationUtil.saveStratificationVariablesToDatabase(request);
		RandomizationUtil.saveTrialIDItemToDatabase(request);
		RandomizationUtil.checkAndUpdateEventCRFAndStudyEventStatuses(itemsMap);

		if (assignRandomizationResultTo.equals("dngroup")) {
			RandomizationUtil.assignSubjectToGroup(result);
		} else if (assignRandomizationResultTo.equals("ssid")) {
			RandomizationUtil.addRandomizationResultToSSID(result);
		} else {
			log.info("Subject" + result.getPatientId() + "was randomized successfully");
		}
		JSONObject randomizationResult = new JSONObject();

		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", LocaleResolver.getLocale());
		Date date = new Date();

		randomizationResult.put("date", dateFormat.format(date));
		randomizationResult.put("result", result.getRandomizationResult());
		randomizationResult.put("id", result.getRandomizationID());

		writer.write(randomizationResult.toString());
		writer.flush();
		return result;
	}

	private RandomizationResult initiateRandomizationCall(HttpServletRequest request) throws Exception {

		StudyBean currentStudy = getCurrentStudy(request);
		String trialId;
		// Get Trial Id configured in the CRF
		String crfConfiguredTrialId = request.getParameter("trialId");
		// Get TrialId configured in study parameters
		String configuredTrialId = currentStudy.getStudyParameterConfig().getRandomizationTrialId();
		// Check if the study params trial config is "empty or zero"
		if (RandomizationUtil.isConfiguredTrialIdValid(configuredTrialId)) {
			// Trial Id should be configured in one place
			if (RandomizationUtil.isTrialIdDoubleConfigured(configuredTrialId, crfConfiguredTrialId)) {
				throw new RandomizationException(resexception.getString("trial_id_not_configured_correctly"));
			} else {
				trialId = configuredTrialId;
			}
		} else if (RandomizationUtil.isCRFSpecifiedTrialIdValid(crfConfiguredTrialId)) {
			trialId = crfConfiguredTrialId;
		} else {
			// Valid Trial Id must be specified at least in one place (CRF or Study properties)
			throw new RandomizationException(resexception.getString("specify_valid_trial_id"));
		}
		String strataLevel = request.getParameter("strataLevel").equals("null") ? "" : request
				.getParameter("strataLevel");

		String siteId = getSite(currentStudy).toLowerCase();
		String patientId = request.getParameter("subject");
		String randomizationEnviroment = currentStudy.getStudyParameterConfig().getRandomizationEnviroment();

		Randomization randomization = new Randomization();
		// username and password
		randomization.setUsername(CoreResources.getField("randomizationusername"));
		randomization.setPassword(CoreResources.getField("randomizationpassword"));
		// Rando details
		randomization.setSiteId(siteId);
		randomization.setTrialId(trialId);
		randomization.setPatientId(patientId);
		randomization.setStratificationLevel(strataLevel);
		randomization.setTestOnly(Boolean.toString(randomizationEnviroment.equals("test")));
		// Https details
		randomization.setRandomizationUrl(CoreResources.getField("randomizationUrl"));
		randomization.setAuthenticationUrl(CoreResources.getField("randomizationAuthenticationUrl"));

		SubmissionContext context = new JSONSubmissionContext();
		context.setRandomization(randomization);

		HttpTransportProtocol protocol = new HttpTransportProtocol();
		protocol.setSubmissionContext(context);

		return protocol.call();
	}

	protected String getSite(StudyBean currentStudy) throws RandomizationException {

		if (currentStudy.isSite(currentStudy.getId())) {
			return currentStudy.getIdentifier();
		} else {
			throw new RandomizationException(resexception.getString("randomization_can_be_performer_only_at_site"));
		}
	}

	protected int getStudyId(StudyBean currentStudy, StudyDAO studyDAO) {

		StudyBean study;

		if (currentStudy.getParentStudyId() > 0) {
			study = (StudyBean) studyDAO.findByPK(currentStudy.getParentStudyId());
		} else {
			study = currentStudy;
		}
		return study.getId();
	}
}
